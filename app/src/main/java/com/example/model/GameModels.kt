package com.example.model

enum class Direction {
    LEFT, RIGHT, UP, DOWN
}

enum class ThemeMode {
    DARK, LIGHT, AUTO
}

enum class DigitsMode {
    FA, EN
}

data class Tile(
    val id: Long,
    val r: Int,
    val c: Int,
    val value: Int,
    val merged: Boolean = false,
    val spawned: Boolean = false
)

data class GhostTile(
    val id: Long,
    val fromR: Int,
    val fromC: Int,
    val toR: Int,
    val toC: Int,
    val value: Int
)

data class MoveResult(
    val tiles: List<Tile>,
    val ghosts: List<GhostTile>,
    val moved: Boolean,
    val gained: Int
)

data class GameRecord(
    val score: Int,
    val tile: Int,
    val moves: Int,
    val seconds: Int,
    val dateMillis: Long
)

data class GameStats(
    val games: Int = 0,
    val wins: Int = 0,
    val bestTile: Int = 0,
    val moves: Int = 0,
    val seconds: Int = 0
)

data class GameSettings(
    val theme: ThemeMode = ThemeMode.DARK,
    val sound: Boolean = true,
    val digits: DigitsMode = DigitsMode.FA,
    val anim: Boolean = true
)

data class GameSnapshot(
    val tiles: List<Tile>,
    val score: Int,
    val moves: Int,
    val won: Boolean,
    val keepPlaying: Boolean,
    val over: Boolean,
    val recorded: Boolean,
    val timeBank: Int,
    val runSinceMillis: Long?
)

data class GameSession(
    val tiles: List<Tile>,
    val score: Int,
    val moves: Int,
    val won: Boolean,
    val showWin: Boolean,
    val keepPlaying: Boolean,
    val over: Boolean,
    val recorded: Boolean,
    val timeBank: Int,
    val runSinceMillis: Long?
)

data class PersistedState(
    val best: Int = 0,
    val settings: GameSettings = GameSettings(),
    val stats: GameStats = GameStats(),
    val session: GameSession? = null,
    val history: List<GameSnapshot> = emptyList(),
    val records: List<GameRecord> = emptyList()
)
