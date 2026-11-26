package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.game.GameEngine
import com.example.model.DigitsMode
import com.example.model.GameRecord
import com.example.model.GameSession
import com.example.model.GameSettings
import com.example.model.GameSnapshot
import com.example.model.GameStats
import com.example.model.PersistedState
import com.example.model.ThemeMode
import com.example.model.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fa2048_store")

class StateRepository(private val context: Context) {

    private val stateKey = stringPreferencesKey("fa2048_state_v1")
    private val backupFile = File(context.filesDir, "fa2048_backup_v1.json")
    private val backupTmpFile = File(context.filesDir, "fa2048_backup_v1.json.tmp")
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val writeMutex = Mutex()

    suspend fun load(nextId: () -> Long): PersistedState = withContext(Dispatchers.IO) {
        try {
            val prefs = context.dataStore.data.first()
            val raw = prefs[stateKey]
            if (!raw.isNullOrBlank()) {
                return@withContext decodeState(raw, nextId)
            }
        } catch (_: Exception) {
            // Fall through to local atomic backup file
        }
        try {
            if (backupFile.exists()) {
                val rawBackup = backupFile.readText(Charsets.UTF_8)
                if (rawBackup.isNotBlank()) {
                    return@withContext decodeState(rawBackup, nextId)
                }
            }
        } catch (_: Exception) {
        }
        PersistedState()
    }

    @Volatile
    private var pendingSave: Pair<PersistedState, Int>? = null

    fun saveAsync(state: PersistedState, elapsedSeconds: Int) {
        pendingSave = state to elapsedSeconds
        ioScope.launch {
            writeMutex.withLock {
                val payload = pendingSave ?: return@withLock
                pendingSave = null
                val json = encodeState(payload.first, payload.second)
                withContext(NonCancellable) {
                    writeInternal(json)
                }
            }
        }
    }

    suspend fun clearAll() = withContext(NonCancellable + Dispatchers.IO) {
        pendingSave = null
        writeMutex.withLock {
            try {
                context.dataStore.edit { prefs ->
                    prefs.remove(stateKey)
                }
            } catch (_: Exception) {
            }
            try {
                if (backupFile.exists()) backupFile.delete()
                if (backupTmpFile.exists()) backupTmpFile.delete()
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun writeInternal(json: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[stateKey] = json
            }
        } catch (_: Exception) {
        }
        try {
            backupTmpFile.writeText(json, Charsets.UTF_8)
            if (!backupTmpFile.renameTo(backupFile)) {
                backupFile.writeText(json, Charsets.UTF_8)
                backupTmpFile.delete()
            }
        } catch (_: Exception) {
        }
    }

    private fun encodeState(state: PersistedState, elapsedSeconds: Int): String {
        val root = JSONObject()
        root.put("v", 1)
        root.put("best", state.best.coerceAtLeast(0))

        val settingsObj = JSONObject().apply {
            put("theme", when (state.settings.theme) {
                ThemeMode.DARK -> "dark"
                ThemeMode.LIGHT -> "light"
                ThemeMode.AUTO -> "auto"
            })
            put("palette", state.settings.palette.storageKey)
            put("sound", state.settings.sound)
            put("digits", if (state.settings.digits == DigitsMode.FA) "fa" else "en")
            put("anim", state.settings.anim)
            put("fullScreenTouch", state.settings.fullScreenTouch)
        }
        root.put("settings", settingsObj)

        val statsObj = JSONObject().apply {
            put("games", state.stats.games)
            put("wins", state.stats.wins)
            put("bestTile", state.stats.bestTile)
            put("moves", state.stats.moves)
            put("seconds", state.stats.seconds)
        }
        root.put("stats", statsObj)

        state.session?.let { s ->
            val tilesArr = JSONArray()
            for (t in s.tiles) {
                tilesArr.put(JSONArray().apply {
                    put(t.value)
                    put(t.r)
                    put(t.c)
                })
            }
            val sessionObj = JSONObject().apply {
                put("tiles", tilesArr)
                put("score", s.score)
                put("moves", s.moves)
                put("won", s.won)
                put("keepPlaying", s.keepPlaying)
                put("over", s.over)
                put("recorded", s.recorded)
                put("seconds", if (s.over) s.timeBank else elapsedSeconds)
            }
            root.put("session", sessionObj)
        }

        val historyArr = JSONArray()
        for (snap in state.history.takeLast(GameEngine.HISTORY_LIMIT)) {
            val tArr = JSONArray()
            for (t in snap.tiles) {
                tArr.put(JSONArray().apply {
                    put(t.value)
                    put(t.r)
                    put(t.c)
                })
            }
            historyArr.put(JSONObject().apply {
                put("t", tArr)
                put("score", snap.score)
                put("moves", snap.moves)
                put("won", snap.won)
                put("keepPlaying", snap.keepPlaying)
                put("over", snap.over)
                put("recorded", snap.recorded)
                put("timeBank", snap.timeBank)
            })
        }
        root.put("history", historyArr)

        val recordsArr = JSONArray()
        for (rec in state.records.take(GameEngine.RECORD_LIMIT)) {
            recordsArr.put(JSONObject().apply {
                put("score", rec.score)
                put("tile", rec.tile)
                put("moves", rec.moves)
                put("seconds", rec.seconds)
                put("date", rec.dateMillis)
            })
        }
        root.put("records", recordsArr)

        return root.toString()
    }

    private fun decodeState(raw: String, nextId: () -> Long): PersistedState {
        val root = JSONObject(raw)
        val best = root.optInt("best", 0).coerceAtLeast(0)

        val settingsObj = root.optJSONObject("settings")
        val settings = if (settingsObj != null) {
            val theme = when (settingsObj.optString("theme", "light")) {
                "dark" -> ThemeMode.DARK
                "auto" -> ThemeMode.AUTO
                else -> ThemeMode.LIGHT
            }
            val palette = com.example.model.ColorPalette.fromKey(
                settingsObj.optString("palette", com.example.model.ColorPalette.NUDE_MOCHA.storageKey)
            )
            val digits = if (settingsObj.optString("digits", "fa") == "en") DigitsMode.EN else DigitsMode.FA
            GameSettings(
                theme = theme,
                palette = palette,
                sound = settingsObj.optBoolean("sound", true),
                digits = digits,
                anim = settingsObj.optBoolean("anim", true),
                fullScreenTouch = settingsObj.optBoolean("fullScreenTouch", true)
            )
        } else {
            GameSettings()
        }

        val statsObj = root.optJSONObject("stats")
        val stats = if (statsObj != null) {
            GameStats(
                games = statsObj.optInt("games", 0).coerceAtLeast(0),
                wins = statsObj.optInt("wins", 0).coerceAtLeast(0),
                bestTile = statsObj.optInt("bestTile", 0).coerceAtLeast(0),
                moves = statsObj.optInt("moves", 0).coerceAtLeast(0),
                seconds = statsObj.optInt("seconds", 0).coerceAtLeast(0)
            )
        } else {
            GameStats()
        }

        val sessionObj = root.optJSONObject("session")
        val session = if (sessionObj != null) {
            val tiles = decodeTiles(sessionObj.optJSONArray("tiles"), nextId)
            if (tiles.isNotEmpty()) {
                val over = sessionObj.optBoolean("over", false)
                val moves = sessionObj.optInt("moves", 0).coerceAtLeast(0)
                GameSession(
                    tiles = tiles,
                    score = sessionObj.optInt("score", 0).coerceAtLeast(0),
                    moves = moves,
                    won = sessionObj.optBoolean("won", false),
                    showWin = false,
                    keepPlaying = sessionObj.optBoolean("keepPlaying", false),
                    over = over,
                    recorded = sessionObj.optBoolean("recorded", false),
                    timeBank = sessionObj.optInt("seconds", 0).coerceAtLeast(0),
                    runSinceMillis = if (!over && moves > 0) System.currentTimeMillis() else null
                )
            } else {
                null
            }
        } else {
            null
        }

        val historyArr = root.optJSONArray("history")
        val history = ArrayList<GameSnapshot>()
        if (historyArr != null) {
            for (i in 0 until historyArr.length()) {
                val o = historyArr.optJSONObject(i) ?: continue
                val tiles = decodeTiles(o.optJSONArray("t"), nextId)
                if (tiles.isNotEmpty()) {
                    history.add(
                        GameSnapshot(
                            tiles = tiles,
                            score = o.optInt("score", 0).coerceAtLeast(0),
                            moves = o.optInt("moves", 0).coerceAtLeast(0),
                            won = o.optBoolean("won", false),
                            keepPlaying = o.optBoolean("keepPlaying", false),
                            over = o.optBoolean("over", false),
                            recorded = o.optBoolean("recorded", false),
                            timeBank = o.optInt("timeBank", 0).coerceAtLeast(0),
                            runSinceMillis = null
                        )
                    )
                }
            }
        }

        val recordsArr = root.optJSONArray("records")
        val records = ArrayList<GameRecord>()
        if (recordsArr != null) {
            for (i in 0 until recordsArr.length()) {
                val r = recordsArr.optJSONObject(i) ?: continue
                records.add(
                    GameRecord(
                        score = r.optInt("score", 0).coerceAtLeast(0),
                        tile = r.optInt("tile", 0).coerceAtLeast(0),
                        moves = r.optInt("moves", 0).coerceAtLeast(0),
                        seconds = r.optInt("seconds", 0).coerceAtLeast(0),
                        dateMillis = r.optLong("date", System.currentTimeMillis())
                    )
                )
            }
        }

        return PersistedState(
            best = best,
            settings = settings,
            stats = stats,
            session = session,
            history = history.takeLast(GameEngine.HISTORY_LIMIT),
            records = records.take(GameEngine.RECORD_LIMIT)
        )
    }

    private fun decodeTiles(arr: JSONArray?, nextId: () -> Long): List<Tile> {
        if (arr == null) return emptyList()
        val list = ArrayList<Tile>(arr.length())
        for (i in 0 until arr.length()) {
            val cell = arr.optJSONArray(i) ?: continue
            if (cell.length() < 3) continue
            val value = cell.optInt(0, 0)
            val r = cell.optInt(1, -1)
            val c = cell.optInt(2, -1)
            if (value > 0 && r in 0 until GameEngine.SIZE && c in 0 until GameEngine.SIZE) {
                list.add(Tile(id = nextId(), r = r, c = c, value = value))
            }
        }
        return list
    }
}
