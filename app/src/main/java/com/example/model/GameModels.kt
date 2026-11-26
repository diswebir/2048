package com.example.model

import androidx.compose.runtime.Immutable

enum class Direction {
    LEFT, RIGHT, UP, DOWN
}

enum class ThemeMode {
    DARK, LIGHT, AUTO
}

enum class ColorPalette(val storageKey: String) {
    NUDE_MOCHA("nude_mocha"),
    PASTEL_BLUSH("pastel_blush"),
    PASTEL_SAGE("pastel_sage"),
    PASTEL_LAVENDER("pastel_lavender"),
    PASTEL_PEACH("pastel_peach");

    companion object {
        fun fromKey(key: String?): ColorPalette {
            return entries.firstOrNull { it.storageKey == key } ?: NUDE_MOCHA
        }
    }
}

enum class DigitsMode {
    FA, EN
}

@Immutable
data class Tile(
    val id: Long,
    val r: Int,
    val c: Int,
    val value: Int,
    val merged: Boolean = false,
    val spawned: Boolean = false
)

@Immutable
data class GhostTile(
    val id: Long,
    val fromR: Int,
    val fromC: Int,
    val toR: Int,
    val toC: Int,
    val value: Int
)

@Immutable
data class MoveResult(
    val tiles: List<Tile>,
    val ghosts: List<GhostTile>,
    val moved: Boolean,
    val gained: Int
)

@Immutable
data class GameRecord(
    val score: Int,
    val tile: Int,
    val moves: Int,
    val seconds: Int,
    val dateMillis: Long
)

@Immutable
data class GameStats(
    val games: Int = 0,
    val wins: Int = 0,
    val bestTile: Int = 0,
    val moves: Int = 0,
    val seconds: Int = 0
)

@Immutable
data class GameSettings(
    val theme: ThemeMode = ThemeMode.LIGHT,
    val palette: ColorPalette = ColorPalette.NUDE_MOCHA,
    val sound: Boolean = true,
    val digits: DigitsMode = DigitsMode.FA,
    val anim: Boolean = true,
    val fullScreenTouch: Boolean = true
)

@Immutable
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

@Immutable
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

@Immutable
data class PersistedState(
    val best: Int = 0,
    val settings: GameSettings = GameSettings(),
    val stats: GameStats = GameStats(),
    val session: GameSession? = null,
    val history: List<GameSnapshot> = emptyList(),
    val records: List<GameRecord> = emptyList()
)
