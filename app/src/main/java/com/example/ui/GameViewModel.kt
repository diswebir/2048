package com.example.ui

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.audio.SoundEngine
import com.example.data.StateRepository
import com.example.game.GameEngine
import com.example.model.ColorPalette
import com.example.model.DigitsMode
import com.example.model.Direction
import com.example.model.GameRecord
import com.example.model.GameSession
import com.example.model.GameSettings
import com.example.model.GameSnapshot
import com.example.model.GameStats
import com.example.model.GhostTile
import com.example.model.PersistedState
import com.example.model.ThemeMode
import com.example.util.FaFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

sealed interface GameDialog {
    data object None : GameDialog
    data object Settings : GameDialog
    data object Help : GameDialog
    data object AllRecords : GameDialog
    data object ConfirmNewGame : GameDialog
    data object ConfirmClearRecords : GameDialog
    data object ConfirmResetAll : GameDialog
}

@Immutable
data class ToastMessage(
    val id: Long,
    val text: String
)

@Immutable
data class MergeBurst(
    val id: Long,
    val r: Int,
    val c: Int,
    val value: Int
)

@Immutable
data class GameUiState(
    val isReady: Boolean = false,
    val game: GameSession = GameSession(
        tiles = emptyList(),
        score = 0,
        moves = 0,
        won = false,
        showWin = false,
        keepPlaying = false,
        over = false,
        recorded = false,
        timeBank = 0,
        runSinceMillis = null
    ),
    val best: Int = 0,
    val settings: GameSettings = GameSettings(),
    val stats: GameStats = GameStats(),
    val records: List<GameRecord> = emptyList(),
    val undoCount: Int = 0,
    val ghosts: List<GhostTile> = emptyList(),
    val ghostToken: Long = 0L,
    val mergeBursts: List<MergeBurst> = emptyList(),
    val mergeBurstToken: Long = 0L,
    val swipeGlowDir: Direction? = null,
    val swipeGlowToken: Long = 0L,
    val comboCount: Int = 0,
    val comboToken: Long = 0L,
    val nudgeDir: Direction? = null,
    val nudgeToken: Long = 0L,
    val scoreGain: Int = 0,
    val scoreGainToken: Long = 0L,
    val bestPulseToken: Long = 0L,
    val confettiToken: Long = 0L,
    val toast: ToastMessage? = null,
    val dialog: GameDialog = GameDialog.None
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val repository = StateRepository(appContext)
    private val sound = SoundEngine()
    private val idCounter = AtomicLong(1L)
    private val toastCounter = AtomicLong(1L)
    private val burstCounter = AtomicLong(1L)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Dedicated lightweight flow for timer ticks so 1s updates ONLY recompose the single timer text cell
    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val undoStack = ArrayDeque<GameSnapshot>(GameEngine.HISTORY_LIMIT + 1)
    private val queuedDirs = ArrayDeque<Direction>(3)
    private var paintBusy = false
    private var moveToken = 0L
    private var lastRecordToastMillis = 0L
    private var isInForeground = true

    private var tickerJob: Job? = null
    private var persistJob: Job? = null
    private var lockJob: Job? = null
    private var spawnJob: Job? = null
    private var toastJob: Job? = null
    private var ghostClearJob: Job? = null
    private var burstClearJob: Job? = null

    init {
        viewModelScope.launch {
            val loaded = repository.load(::nextTileId)
            undoStack.clear()
            for (snap in loaded.history.takeLast(GameEngine.HISTORY_LIMIT)) {
                undoStack.addLast(snap)
            }
            val session = loaded.session ?: GameEngine.freshGame(::nextTileId)
            val maxTile = GameEngine.maxTileValue(session.tiles)
            val updatedStats = loaded.stats.copy(
                bestTile = maxOf(loaded.stats.bestTile, maxTile)
            )
            sound.enabled = loaded.settings.sound
            val initialElapsed = computeElapsed(session)
            _elapsedSeconds.value = initialElapsed
            _uiState.update {
                it.copy(
                    isReady = true,
                    game = session,
                    best = loaded.best,
                    settings = loaded.settings,
                    stats = updatedStats,
                    records = sortedRecords(loaded.records),
                    undoCount = undoStack.size
                )
            }
            syncTickerState()
        }
    }

    private fun nextTileId(): Long = idCounter.getAndIncrement()

    // Ticker runs ONLY in foreground while a game has active moves; 0 wakeups at idle or background
    private fun syncTickerState() {
        val state = _uiState.value
        val session = state.game
        val shouldRun = isInForeground &&
            state.isReady &&
            !session.over &&
            !session.showWin &&
            session.runSinceMillis != null

        if (!shouldRun) {
            tickerJob?.cancel()
            tickerJob = null
            return
        }
        if (tickerJob?.isActive == true) return
        tickerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val s = _uiState.value.game
                if (!isInForeground || s.over || s.showWin || s.runSinceMillis == null) {
                    break
                }
                val sec = computeElapsed(s)
                if (sec != _elapsedSeconds.value) {
                    _elapsedSeconds.value = sec
                }
            }
            tickerJob = null
        }
    }

    fun onAppPaused() {
        isInForeground = false
        tickerJob?.cancel()
        tickerJob = null
        val state = _uiState.value
        if (!state.isReady) return
        val session = state.game
        if (session.runSinceMillis != null) {
            val elapsed = computeElapsed(session)
            _elapsedSeconds.value = elapsed
            val paused = session.copy(timeBank = elapsed, runSinceMillis = null)
            _uiState.update { it.copy(game = paused) }
        }
        persistImmediate()
    }

    fun onAppResumed() {
        isInForeground = true
        val state = _uiState.value
        val session = state.game
        if (!state.isReady || session.over || session.showWin) {
            syncTickerState()
            return
        }
        if (session.runSinceMillis == null && session.moves > 0) {
            val resumed = session.copy(runSinceMillis = System.currentTimeMillis())
            _uiState.update { it.copy(game = resumed) }
        }
        syncTickerState()
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
        persistImmediate()
        sound.release()
    }

    fun move(dir: Direction) {
        val state = _uiState.value
        if (!state.isReady) return
        if (state.dialog != GameDialog.None) return
        val session = state.game
        if (session.over || session.showWin) return

        if (paintBusy) {
            if (queuedDirs.size >= 2) queuedDirs.removeFirst()
            queuedDirs.addLast(dir)
            return
        }

        val before = GameEngine.toSnapshot(session)
        val res = GameEngine.simulateMove(session.tiles, dir, ::nextTileId)
        if (!res.moved) {
            triggerNudge(dir)
            sound.nope()
            return
        }

        pushUndo(before)

        val now = System.currentTimeMillis()
        val prevBest = state.best
        val newScore = session.score + res.gained
        val newBest = maxOf(prevBest, newScore)
        val newStats = state.stats.copy(moves = state.stats.moves + 1)
        val bursts = if (state.settings.anim) {
            res.tiles.filter { it.merged }.map {
                MergeBurst(id = burstCounter.getAndIncrement(), r = it.r, c = it.c, value = it.value)
            }
        } else {
            emptyList()
        }
        val mergedOccurred = res.gained > 0
        val nextCombo = if (mergedOccurred) state.comboCount + 1 else 0

        if (mergedOccurred) {
            val mergedMax = res.tiles.maxOfOrNull { if (it.merged) it.value else 0 }?.coerceAtLeast(2) ?: 2
            sound.merge(mergedMax)
        } else {
            sound.move()
        }

        val updatedSession = session.copy(
            tiles = res.tiles,
            score = newScore,
            moves = session.moves + 1,
            runSinceMillis = if (session.moves == 0 && session.runSinceMillis == null) now else session.runSinceMillis
        )

        _uiState.update { cur ->
            cur.copy(
                game = updatedSession,
                best = newBest,
                stats = newStats,
                undoCount = undoStack.size,
                ghosts = if (cur.settings.anim) res.ghosts else emptyList(),
                ghostToken = if (cur.settings.anim && res.ghosts.isNotEmpty()) cur.ghostToken + 1 else cur.ghostToken,
                mergeBursts = bursts,
                mergeBurstToken = if (bursts.isNotEmpty()) cur.mergeBurstToken + 1 else cur.mergeBurstToken,
                swipeGlowDir = dir,
                swipeGlowToken = cur.swipeGlowToken + 1,
                comboCount = nextCombo,
                comboToken = if (nextCombo >= 2) cur.comboToken + 1 else cur.comboToken,
                scoreGain = if (res.gained > 0) res.gained else cur.scoreGain,
                scoreGainToken = if (res.gained > 0) cur.scoreGainToken + 1 else cur.scoreGainToken,
                bestPulseToken = if (newBest > prevBest && prevBest > 0) cur.bestPulseToken + 1 else cur.bestPulseToken
            )
        }
        if (state.settings.anim && res.ghosts.isNotEmpty()) scheduleGhostClear()
        if (bursts.isNotEmpty()) scheduleBurstClear()
        syncTickerState()

        if (newBest > prevBest && prevBest > 0 && now - lastRecordToastMillis > 4000L) {
            lastRecordToastMillis = now
            val formatted = FaFormat.fmt(newBest, state.settings.digits)
            showToast(appContext.getString(R.string.toast_new_best, formatted))
        }

        beginInputLock()

        val token = ++moveToken
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            delay(45L)
            if (token != moveToken) return@launch
            val curState = _uiState.value
            val curSession = curState.game
            if (curSession.over) return@launch

            val withSpawn = GameEngine.spawnTile(curSession.tiles, count = 1, nextId = ::nextTileId)
            val afterSpawn = curSession.copy(tiles = withSpawn)
            afterMoveCheck(afterSpawn)
            beginInputLock()
            persistDebounced()
        }
    }

    private fun moveInstant(dir: Direction) {
        val state = _uiState.value
        if (!state.isReady || state.dialog != GameDialog.None) return
        val session = state.game
        if (session.over || session.showWin) return

        val before = GameEngine.toSnapshot(session)
        val res = GameEngine.simulateMove(session.tiles, dir, ::nextTileId)
        if (!res.moved) {
            triggerNudge(dir)
            sound.nope()
            return
        }

        pushUndo(before)
        moveToken++
        spawnJob?.cancel()

        val now = System.currentTimeMillis()
        val prevBest = state.best
        val newScore = session.score + res.gained
        val newBest = maxOf(prevBest, newScore)
        val newStats = state.stats.copy(moves = state.stats.moves + 1)
        val bursts = if (state.settings.anim) {
            res.tiles.filter { it.merged }.map {
                MergeBurst(id = burstCounter.getAndIncrement(), r = it.r, c = it.c, value = it.value)
            }
        } else {
            emptyList()
        }
        val mergedOccurred = res.gained > 0
        val nextCombo = if (mergedOccurred) state.comboCount + 1 else 0

        if (mergedOccurred) {
            val mergedMax = res.tiles.maxOfOrNull { if (it.merged) it.value else 0 }?.coerceAtLeast(2) ?: 2
            sound.merge(mergedMax)
        } else {
            sound.move()
        }

        val withSpawn = GameEngine.spawnTile(res.tiles, count = 1, nextId = ::nextTileId)
        val updatedSession = session.copy(
            tiles = withSpawn,
            score = newScore,
            moves = session.moves + 1,
            runSinceMillis = if (session.moves == 0 && session.runSinceMillis == null) now else session.runSinceMillis
        )

        _uiState.update { cur ->
            cur.copy(
                best = newBest,
                stats = newStats,
                undoCount = undoStack.size,
                ghosts = if (cur.settings.anim) res.ghosts else emptyList(),
                ghostToken = if (cur.settings.anim && res.ghosts.isNotEmpty()) cur.ghostToken + 1 else cur.ghostToken,
                mergeBursts = bursts,
                mergeBurstToken = if (bursts.isNotEmpty()) cur.mergeBurstToken + 1 else cur.mergeBurstToken,
                swipeGlowDir = dir,
                swipeGlowToken = cur.swipeGlowToken + 1,
                comboCount = nextCombo,
                comboToken = if (nextCombo >= 2) cur.comboToken + 1 else cur.comboToken,
                scoreGain = if (res.gained > 0) res.gained else cur.scoreGain,
                scoreGainToken = if (res.gained > 0) cur.scoreGainToken + 1 else cur.scoreGainToken,
                bestPulseToken = if (newBest > prevBest && prevBest > 0) cur.bestPulseToken + 1 else cur.bestPulseToken
            )
        }
        if (state.settings.anim && res.ghosts.isNotEmpty()) scheduleGhostClear()
        if (bursts.isNotEmpty()) scheduleBurstClear()

        afterMoveCheck(updatedSession)
        syncTickerState()
        beginInputLock()
        persistDebounced()
    }

    private fun scheduleGhostClear() {
        ghostClearJob?.cancel()
        ghostClearJob = viewModelScope.launch {
            delay(180L)
            _uiState.update { if (it.ghosts.isEmpty()) it else it.copy(ghosts = emptyList()) }
        }
    }

    private fun scheduleBurstClear() {
        burstClearJob?.cancel()
        burstClearJob = viewModelScope.launch {
            delay(340L)
            _uiState.update { if (it.mergeBursts.isEmpty()) it else it.copy(mergeBursts = emptyList()) }
        }
    }

    private fun beginInputLock() {
        paintBusy = true
        lockJob?.cancel()
        lockJob = viewModelScope.launch {
            delay(60L)
            paintBusy = false
            val next = queuedDirs.removeFirstOrNull()
            if (next != null) {
                moveInstant(next)
            }
        }
    }

    private fun afterMoveCheck(sessionInput: GameSession) {
        val cur = _uiState.value
        val maxTile = GameEngine.maxTileValue(sessionInput.tiles)
        var stats = cur.stats.copy(bestTile = maxOf(cur.stats.bestTile, maxTile))
        var session = sessionInput
        var confettiBump = 0L
        var needsImmediateSave = false

        if (maxTile >= GameEngine.TARGET && !session.won) {
            stats = stats.copy(wins = stats.wins + 1)
            session = session.copy(won = true)
            needsImmediateSave = true
            if (!session.keepPlaying) {
                val elapsed = computeElapsed(session)
                session = session.copy(
                    showWin = true,
                    timeBank = elapsed,
                    runSinceMillis = null
                )
                sound.win()
                confettiBump = 1L
                showToast(appContext.getString(R.string.toast_win))
            }
        }

        var records = cur.records
        var best = cur.best

        if (!GameEngine.hasMoves(session.tiles) && !session.over) {
            val seconds = computeElapsed(session)
            needsImmediateSave = true
            session = session.copy(
                over = true,
                timeBank = seconds,
                runSinceMillis = null
            )
            if (!session.recorded) {
                session = session.copy(recorded = true)
                stats = stats.copy(
                    games = stats.games + 1,
                    seconds = stats.seconds + seconds,
                    bestTile = maxOf(stats.bestTile, maxTile)
                )
                best = maxOf(best, session.score)
                if (session.score > 0) {
                    val rec = GameRecord(
                        score = session.score,
                        tile = maxTile,
                        moves = session.moves,
                        seconds = seconds,
                        dateMillis = System.currentTimeMillis()
                    )
                    records = sortedRecords(records + rec).take(GameEngine.RECORD_LIMIT)
                    val formatted = FaFormat.fmt(session.score, cur.settings.digits)
                    showToast(appContext.getString(R.string.toast_over_saved, formatted))
                } else {
                    showToast(appContext.getString(R.string.toast_over))
                }
                sound.over()
            }
        }

        _elapsedSeconds.value = computeElapsed(session)
        _uiState.update { state ->
            state.copy(
                game = session,
                best = best,
                stats = stats,
                records = records,
                confettiToken = state.confettiToken + confettiBump
            )
        }
        syncTickerState()
        if (needsImmediateSave) {
            persistImmediate()
        }
    }

    fun continueAfterWin() {
        val state = _uiState.value
        val s = state.game
        if (!s.showWin) return
        sound.click()
        val resumed = s.copy(
            showWin = false,
            keepPlaying = true,
            runSinceMillis = if (!s.over && s.runSinceMillis == null) System.currentTimeMillis() else s.runSinceMillis
        )
        _uiState.update { it.copy(game = resumed) }
        syncTickerState()
        persistImmediate()
        showToast(appContext.getString(R.string.toast_continue))
    }

    fun requestNewGame() {
        val state = _uiState.value
        sound.click()
        if (state.game.moves > 0 && !state.game.over) {
            _uiState.update { it.copy(dialog = GameDialog.ConfirmNewGame) }
        } else {
            startNewGame()
        }
    }

    fun confirmNewGame() {
        startNewGame()
    }

    private fun startNewGame() {
        moveToken++
        spawnJob?.cancel()
        lockJob?.cancel()
        ghostClearJob?.cancel()
        burstClearJob?.cancel()
        paintBusy = false
        queuedDirs.clear()
        undoStack.clear()
        val fresh = GameEngine.freshGame(::nextTileId)
        _elapsedSeconds.value = 0
        _uiState.update {
            it.copy(
                game = fresh,
                undoCount = 0,
                ghosts = emptyList(),
                mergeBursts = emptyList(),
                comboCount = 0,
                dialog = GameDialog.None
            )
        }
        syncTickerState()
        persistImmediate()
        sound.click()
        showToast(appContext.getString(R.string.toast_new_game))
    }

    fun undo() {
        val state = _uiState.value
        if (state.dialog != GameDialog.None) return
        if (undoStack.isEmpty()) {
            showToast(appContext.getString(R.string.toast_undo_empty))
            sound.nope()
            return
        }
        val snap = undoStack.removeLast()
        moveToken++
        spawnJob?.cancel()
        lockJob?.cancel()
        ghostClearJob?.cancel()
        burstClearJob?.cancel()
        paintBusy = false
        queuedDirs.clear()

        val restored = GameEngine.fromSnapshot(snap, System.currentTimeMillis())
        val newStats = state.stats.copy(moves = (state.stats.moves - 1).coerceAtLeast(0))
        _elapsedSeconds.value = computeElapsed(restored)
        _uiState.update {
            it.copy(
                game = restored,
                stats = newStats,
                undoCount = undoStack.size,
                ghosts = emptyList(),
                mergeBursts = emptyList(),
                comboCount = 0
            )
        }
        syncTickerState()
        persistDebounced()
        sound.click()
        showToast(appContext.getString(R.string.toast_undo))
    }

    fun toggleSound() {
        val cur = _uiState.value.settings
        val next = !cur.sound
        sound.enabled = next
        _uiState.update { it.copy(settings = cur.copy(sound = next)) }
        persistImmediate()
        if (next) {
            sound.click()
            showToast(appContext.getString(R.string.toast_sound_on))
        } else {
            showToast(appContext.getString(R.string.toast_sound_off))
        }
    }

    fun setSound(enabled: Boolean) {
        val cur = _uiState.value.settings
        if (cur.sound == enabled) return
        sound.enabled = enabled
        _uiState.update { it.copy(settings = cur.copy(sound = enabled)) }
        persistImmediate()
        if (enabled) sound.click()
    }

    fun setThemeMode(mode: ThemeMode) {
        val cur = _uiState.value.settings
        if (cur.theme == mode) return
        sound.click()
        _uiState.update { it.copy(settings = cur.copy(theme = mode)) }
        persistImmediate()
    }

    fun setColorPalette(palette: ColorPalette) {
        val cur = _uiState.value.settings
        if (cur.palette == palette) return
        sound.click()
        _uiState.update { it.copy(settings = cur.copy(palette = palette)) }
        persistImmediate()
    }

    fun setFullScreenTouch(enabled: Boolean) {
        val cur = _uiState.value.settings
        if (cur.fullScreenTouch == enabled) return
        sound.click()
        _uiState.update { it.copy(settings = cur.copy(fullScreenTouch = enabled)) }
        persistImmediate()
    }

    fun setDigitsMode(usePersian: Boolean) {
        val cur = _uiState.value.settings
        val mode = if (usePersian) DigitsMode.FA else DigitsMode.EN
        if (cur.digits == mode) return
        sound.click()
        _uiState.update { it.copy(settings = cur.copy(digits = mode)) }
        persistImmediate()
    }

    fun setAnimEnabled(enabled: Boolean) {
        val cur = _uiState.value.settings
        if (cur.anim == enabled) return
        sound.click()
        _uiState.update { it.copy(settings = cur.copy(anim = enabled)) }
        persistImmediate()
    }

    fun openSettings() {
        sound.click()
        _uiState.update { it.copy(dialog = GameDialog.Settings) }
    }

    fun openHelp() {
        sound.click()
        _uiState.update { it.copy(dialog = GameDialog.Help) }
    }

    fun openAllRecords() {
        sound.click()
        _uiState.update { it.copy(dialog = GameDialog.AllRecords) }
    }

    fun requestClearRecords() {
        sound.click()
        _uiState.update { it.copy(dialog = GameDialog.ConfirmClearRecords) }
    }

    fun confirmClearRecords() {
        sound.click()
        _uiState.update {
            it.copy(
                records = emptyList(),
                best = 0,
                dialog = GameDialog.None
            )
        }
        persistImmediate()
        showToast(appContext.getString(R.string.toast_records_cleared))
    }

    fun requestResetAll() {
        sound.click()
        _uiState.update { it.copy(dialog = GameDialog.ConfirmResetAll) }
    }

    fun confirmResetAll() {
        moveToken++
        spawnJob?.cancel()
        lockJob?.cancel()
        ghostClearJob?.cancel()
        burstClearJob?.cancel()
        persistJob?.cancel()
        paintBusy = false
        queuedDirs.clear()
        undoStack.clear()
        val freshSettings = GameSettings()
        sound.enabled = freshSettings.sound
        val fresh = GameEngine.freshGame(::nextTileId)
        _elapsedSeconds.value = 0
        _uiState.update {
            it.copy(
                game = fresh,
                best = 0,
                settings = freshSettings,
                stats = GameStats(bestTile = GameEngine.maxTileValue(fresh.tiles)),
                records = emptyList(),
                undoCount = 0,
                ghosts = emptyList(),
                mergeBursts = emptyList(),
                comboCount = 0,
                dialog = GameDialog.None
            )
        }
        syncTickerState()
        viewModelScope.launch {
            repository.clearAll()
            persistImmediate()
        }
        sound.click()
        showToast(appContext.getString(R.string.toast_data_cleared))
    }

    fun dismissDialog() {
        val cur = _uiState.value.dialog
        val next = when (cur) {
            GameDialog.ConfirmResetAll -> GameDialog.Settings
            GameDialog.ConfirmClearRecords -> GameDialog.AllRecords
            else -> GameDialog.None
        }
        _uiState.update { it.copy(dialog = next) }
    }

    private fun pushUndo(snapshot: GameSnapshot) {
        undoStack.addLast(snapshot)
        while (undoStack.size > GameEngine.HISTORY_LIMIT) {
            undoStack.removeFirst()
        }
    }

    private fun triggerNudge(dir: Direction) {
        _uiState.update { it.copy(nudgeDir = dir, nudgeToken = it.nudgeToken + 1) }
    }

    private fun showToast(text: String) {
        val msg = ToastMessage(id = toastCounter.getAndIncrement(), text = text)
        _uiState.update { it.copy(toast = msg) }
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            delay(2500L)
            _uiState.update { cur ->
                if (cur.toast?.id == msg.id) cur.copy(toast = null) else cur
            }
        }
    }

    private fun computeElapsed(session: GameSession): Int {
        val live = session.runSinceMillis?.let { ((System.currentTimeMillis() - it) / 1000L).toInt().coerceAtLeast(0) } ?: 0
        return (session.timeBank + live).coerceAtLeast(0)
    }

    private fun sortedRecords(list: List<GameRecord>): List<GameRecord> {
        return list.sortedWith(compareByDescending<GameRecord> { it.score }.thenBy { it.dateMillis })
    }

    private fun persistDebounced() {
        persistJob?.cancel()
        persistJob = viewModelScope.launch {
            delay(550L)
            persistImmediate()
        }
    }

    private fun persistImmediate() {
        persistJob?.cancel()
        val state = _uiState.value
        if (!state.isReady) return
        val persisted = PersistedState(
            best = state.best,
            settings = state.settings,
            stats = state.stats,
            session = state.game,
            history = undoStack.toList(),
            records = state.records
        )
        val elapsed = computeElapsed(state.game)
        repository.saveAsync(persisted, elapsed)
    }
}
