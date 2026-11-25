package com.example.game

import com.example.model.Direction
import com.example.model.GameSession
import com.example.model.GameSnapshot
import com.example.model.GhostTile
import com.example.model.MoveResult
import com.example.model.Tile
import kotlin.random.Random

object GameEngine {
    const val SIZE = 4
    const val TARGET = 2048
    const val HISTORY_LIMIT = 30
    const val RECORD_LIMIT = 40

    fun spawnTile(
        tiles: List<Tile>,
        count: Int = 1,
        nextId: () -> Long,
        random: Random = Random.Default
    ): List<Tile> {
        val result = tiles.toMutableList()
        repeat(count) {
            val occupied = BooleanArray(SIZE * SIZE)
            for (t in result) {
                if (t.r in 0 until SIZE && t.c in 0 until SIZE) {
                    occupied[t.r * SIZE + t.c] = true
                }
            }
            val emptyCells = ArrayList<Pair<Int, Int>>(SIZE * SIZE)
            for (r in 0 until SIZE) {
                for (c in 0 until SIZE) {
                    if (!occupied[r * SIZE + c]) {
                        emptyCells.add(r to c)
                    }
                }
            }
            if (emptyCells.isEmpty()) return result
            val (r, c) = emptyCells[random.nextInt(emptyCells.size)]
            val value = if (random.nextFloat() < 0.9f) 2 else 4
            result.add(
                Tile(
                    id = nextId(),
                    r = r,
                    c = c,
                    value = value,
                    merged = false,
                    spawned = true
                )
            )
        }
        return result
    }

    private fun linesFor(dir: Direction): List<List<Pair<Int, Int>>> {
        val out = ArrayList<List<Pair<Int, Int>>>(SIZE)
        when (dir) {
            Direction.LEFT, Direction.RIGHT -> {
                for (r in 0 until SIZE) {
                    val line = (0 until SIZE).map { c -> r to c }
                    out.add(if (dir == Direction.LEFT) line else line.reversed())
                }
            }
            Direction.UP, Direction.DOWN -> {
                for (c in 0 until SIZE) {
                    val line = (0 until SIZE).map { r -> r to c }
                    out.add(if (dir == Direction.UP) line else line.reversed())
                }
            }
        }
        return out
    }

    private data class MergeGroup(
        var value: Int,
        val tiles: MutableList<Tile>,
        var merged: Boolean
    )

    fun simulateMove(
        tiles: List<Tile>,
        dir: Direction,
        nextId: () -> Long
    ): MoveResult {
        val byCell = HashMap<Int, Tile>(tiles.size * 2)
        for (t in tiles) {
            byCell[t.r * SIZE + t.c] = t
        }

        val result = ArrayList<Tile>(tiles.size)
        val ghosts = ArrayList<GhostTile>()
        var moved = false
        var gained = 0

        for (line in linesFor(dir)) {
            val seq = ArrayList<Tile>(SIZE)
            for ((r, c) in line) {
                byCell[r * SIZE + c]?.let { seq.add(it) }
            }

            val groups = ArrayList<MergeGroup>(seq.size)
            for (cur in seq) {
                val last = groups.lastOrNull()
                if (last != null && !last.merged && last.value == cur.value) {
                    last.value *= 2
                    last.tiles.add(cur)
                    last.merged = true
                    gained += last.value
                } else {
                    groups.add(MergeGroup(value = cur.value, tiles = mutableListOf(cur), merged = false))
                }
            }

            for (g in groups.indices) {
                val grp = groups[g]
                val (targetR, targetC) = line[g]
                if (grp.tiles.size == 1) {
                    val single = grp.tiles[0]
                    if (single.r != targetR || single.c != targetC) {
                        moved = true
                    }
                    result.add(
                        single.copy(
                            r = targetR,
                            c = targetC,
                            merged = false,
                            spawned = false
                        )
                    )
                } else {
                    moved = true
                    for (consumed in grp.tiles) {
                        ghosts.add(
                            GhostTile(
                                id = consumed.id,
                                fromR = consumed.r,
                                fromC = consumed.c,
                                toR = targetR,
                                toC = targetC,
                                value = consumed.value
                            )
                        )
                    }
                    result.add(
                        Tile(
                            id = nextId(),
                            r = targetR,
                            c = targetC,
                            value = grp.value,
                            merged = true,
                            spawned = false
                        )
                    )
                }
            }
        }

        return MoveResult(
            tiles = result,
            ghosts = ghosts,
            moved = moved,
            gained = gained
        )
    }

    fun hasMoves(tiles: List<Tile>): Boolean {
        if (tiles.size < SIZE * SIZE) return true
        val grid = Array(SIZE) { IntArray(SIZE) }
        for (t in tiles) {
            if (t.r in 0 until SIZE && t.c in 0 until SIZE) {
                grid[t.r][t.c] = t.value
            }
        }
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) {
                if (c < SIZE - 1 && grid[r][c] == grid[r][c + 1]) return true
                if (r < SIZE - 1 && grid[r][c] == grid[r + 1][c]) return true
            }
        }
        return false
    }

    fun maxTileValue(tiles: List<Tile>): Int {
        var max = 0
        for (t in tiles) {
            if (t.value > max) max = t.value
        }
        return max
    }

    fun freshGame(nextId: () -> Long): GameSession {
        val tiles = spawnTile(emptyList(), count = 2, nextId = nextId)
        return GameSession(
            tiles = tiles,
            score = 0,
            moves = 0,
            won = false,
            showWin = false,
            keepPlaying = false,
            over = false,
            recorded = false,
            timeBank = 0,
            runSinceMillis = null
        )
    }

    fun toSnapshot(session: GameSession): GameSnapshot {
        return GameSnapshot(
            tiles = session.tiles.map { it.copy(merged = false, spawned = false) },
            score = session.score,
            moves = session.moves,
            won = session.won,
            keepPlaying = session.keepPlaying,
            over = session.over,
            recorded = session.recorded,
            timeBank = session.timeBank,
            runSinceMillis = session.runSinceMillis
        )
    }

    fun fromSnapshot(snap: GameSnapshot, nowMillis: Long): GameSession {
        return GameSession(
            tiles = snap.tiles.map { it.copy(merged = false, spawned = false) },
            score = snap.score,
            moves = snap.moves,
            won = snap.won,
            showWin = false,
            keepPlaying = snap.keepPlaying,
            over = snap.over,
            recorded = snap.recorded,
            timeBank = snap.timeBank,
            runSinceMillis = if (snap.over || snap.moves == 0) null else nowMillis
        )
    }
}
