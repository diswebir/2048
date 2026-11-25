package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.game.GameEngine
import com.example.model.Direction
import com.example.ui.components.AllRecordsDialog
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.ControlsCard
import com.example.ui.components.GameBoard
import com.example.ui.components.GameToolbar
import com.example.ui.components.HeaderBar
import com.example.ui.components.HelpDialog
import com.example.ui.components.PrivacyFooter
import com.example.ui.components.QuickTipsCard
import com.example.ui.components.RecordsCard
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatusCard
import com.example.ui.components.ToastPill
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.Puzzle2048Theme

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.onAppPaused()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onAppResumed()
    }

    BackHandler(enabled = state.dialog != GameDialog.None) {
        viewModel.dismissDialog()
    }

    Puzzle2048Theme(themeMode = state.settings.theme) {
        val colors = LocalAppColors.current
        Scaffold(
            containerColor = colors.bgBase,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(colors.bgBase, colors.bgMid, colors.bgBase)
                        )
                    )
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        if (state.dialog != GameDialog.None) {
                            if (event.key == Key.Escape) {
                                viewModel.dismissDialog()
                                return@onPreviewKeyEvent true
                            }
                            return@onPreviewKeyEvent false
                        }
                        when (event.key) {
                            Key.DirectionUp, Key.W -> { viewModel.move(Direction.UP); true }
                            Key.DirectionDown, Key.S -> { viewModel.move(Direction.DOWN); true }
                            Key.DirectionLeft, Key.A -> { viewModel.move(Direction.LEFT); true }
                            Key.DirectionRight, Key.D -> { viewModel.move(Direction.RIGHT); true }
                            Key.U -> { viewModel.undo(); true }
                            Key.N -> { viewModel.requestNewGame(); true }
                            Key.M -> { viewModel.toggleSound(); true }
                            Key.F1 -> { viewModel.openHelp(); true }
                            else -> false
                        }
                    }
            ) {
                // Ambient radial background glows matching index.html
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.bgGlowPurple, Color.Transparent),
                            center = Offset(size.width * 0.88f, -size.height * 0.06f),
                            radius = size.maxDimension * 0.55f
                        ),
                        radius = size.maxDimension * 0.55f,
                        center = Offset(size.width * 0.88f, -size.height * 0.06f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.bgGlowCyan, Color.Transparent),
                            center = Offset(size.width * 0.04f, size.height * 0.08f),
                            radius = size.maxDimension * 0.45f
                        ),
                        radius = size.maxDimension * 0.45f,
                        center = Offset(size.width * 0.04f, size.height * 0.08f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.bgGlowCoral, Color.Transparent),
                            center = Offset(size.width * 0.52f, size.height * 1.05f),
                            radius = size.maxDimension * 0.50f
                        ),
                        radius = size.maxDimension * 0.50f,
                        center = Offset(size.width * 0.52f, size.height * 1.05f)
                    )
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val isWide = maxWidth >= 820.dp
                    val compactHeader = maxWidth < 460.dp
                    val scrollState = rememberScrollState()
                    val maxTile = GameEngine.maxTileValue(state.game.tiles)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = if (compactHeader) 14.dp else 22.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth()) {
                            HeaderBar(
                                score = state.game.score,
                                best = state.best,
                                digitsMode = state.settings.digits,
                                animEnabled = state.settings.anim,
                                scoreGain = state.scoreGain,
                                scoreGainToken = state.scoreGainToken,
                                bestPulseToken = state.bestPulseToken,
                                compactHeader = compactHeader
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (isWide) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Main Play Column
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Column(modifier = Modifier.widthIn(max = 536.dp).fillMaxWidth()) {
                                            GameToolbar(
                                                undoCount = state.undoCount,
                                                soundEnabled = state.settings.sound,
                                                digitsMode = state.settings.digits,
                                                onNewGame = viewModel::requestNewGame,
                                                onUndo = viewModel::undo,
                                                onToggleSound = viewModel::toggleSound,
                                                onOpenSettings = viewModel::openSettings,
                                                onOpenHelp = viewModel::openHelp
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            GameBoard(
                                                session = state.game,
                                                digitsMode = state.settings.digits,
                                                animEnabled = state.settings.anim,
                                                ghosts = state.ghosts,
                                                ghostToken = state.ghostToken,
                                                nudgeDir = state.nudgeDir,
                                                nudgeToken = state.nudgeToken,
                                                onSwipe = viewModel::move,
                                                onContinueAfterWin = viewModel::continueAfterWin,
                                                onNewGame = viewModel::requestNewGame,
                                                onUndo = viewModel::undo,
                                                canUndo = state.undoCount > 0
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = stringResource(R.string.board_hint),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colors.muted,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    // Side Cards Column
                                    Column(
                                        modifier = Modifier.width(336.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        StatusCard(
                                            maxTile = maxTile,
                                            moves = state.game.moves,
                                            elapsedSeconds = state.elapsedSeconds,
                                            stats = state.stats,
                                            digitsMode = state.settings.digits
                                        )
                                        ControlsCard(
                                            canUndo = state.undoCount > 0,
                                            onUndo = viewModel::undo,
                                            onNewGame = viewModel::requestNewGame
                                        )
                                        RecordsCard(
                                            records = state.records,
                                            digitsMode = state.settings.digits,
                                            onOpenAll = viewModel::openAllRecords
                                        )
                                        QuickTipsCard()
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier.widthIn(max = 536.dp).align(Alignment.CenterHorizontally).fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    GameToolbar(
                                        undoCount = state.undoCount,
                                        soundEnabled = state.settings.sound,
                                        digitsMode = state.settings.digits,
                                        onNewGame = viewModel::requestNewGame,
                                        onUndo = viewModel::undo,
                                        onToggleSound = viewModel::toggleSound,
                                        onOpenSettings = viewModel::openSettings,
                                        onOpenHelp = viewModel::openHelp
                                    )
                                    GameBoard(
                                        session = state.game,
                                        digitsMode = state.settings.digits,
                                        animEnabled = state.settings.anim,
                                        ghosts = state.ghosts,
                                        ghostToken = state.ghostToken,
                                        nudgeDir = state.nudgeDir,
                                        nudgeToken = state.nudgeToken,
                                        onSwipe = viewModel::move,
                                        onContinueAfterWin = viewModel::continueAfterWin,
                                        onNewGame = viewModel::requestNewGame,
                                        onUndo = viewModel::undo,
                                        canUndo = state.undoCount > 0
                                    )
                                    Text(
                                        text = stringResource(R.string.board_hint),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.muted,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    StatusCard(
                                        maxTile = maxTile,
                                        moves = state.game.moves,
                                        elapsedSeconds = state.elapsedSeconds,
                                        stats = state.stats,
                                        digitsMode = state.settings.digits
                                    )
                                    ControlsCard(
                                        canUndo = state.undoCount > 0,
                                        onUndo = viewModel::undo,
                                        onNewGame = viewModel::requestNewGame
                                    )
                                    RecordsCard(
                                        records = state.records,
                                        digitsMode = state.settings.digits,
                                        onOpenAll = viewModel::openAllRecords
                                    )
                                    QuickTipsCard()
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            PrivacyFooter()
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Confetti celebration
                ConfettiOverlay(
                    triggerToken = state.confettiToken,
                    animEnabled = state.settings.anim
                )

                // Bottom toast notification
                AnimatedVisibility(
                    visible = state.toast != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    state.toast?.let { msg ->
                        ToastPill(text = msg.text)
                    }
                }

                // Modal Dialogs
                when (state.dialog) {
                    GameDialog.None -> Unit
                    GameDialog.Settings -> SettingsDialog(
                        settings = state.settings,
                        onThemeChange = viewModel::setThemeMode,
                        onSoundChange = viewModel::setSound,
                        onDigitsChange = viewModel::setDigitsMode,
                        onAnimChange = viewModel::setAnimEnabled,
                        onRequestResetAll = viewModel::requestResetAll,
                        onDismiss = viewModel::dismissDialog
                    )
                    GameDialog.Help -> HelpDialog(
                        digitsMode = state.settings.digits,
                        onDismiss = viewModel::dismissDialog
                    )
                    GameDialog.AllRecords -> AllRecordsDialog(
                        records = state.records,
                        digitsMode = state.settings.digits,
                        onRequestClear = viewModel::requestClearRecords,
                        onDismiss = viewModel::dismissDialog
                    )
                    GameDialog.ConfirmNewGame -> ConfirmDialog(
                        title = stringResource(R.string.confirm_new_title),
                        body = stringResource(R.string.confirm_new_text),
                        confirmText = stringResource(R.string.confirm_new_ok),
                        cancelText = stringResource(R.string.action_continue),
                        isDanger = false,
                        onConfirm = viewModel::confirmNewGame,
                        onDismiss = viewModel::dismissDialog
                    )
                    GameDialog.ConfirmClearRecords -> ConfirmDialog(
                        title = stringResource(R.string.confirm_clear_records_title),
                        body = stringResource(R.string.confirm_clear_records_text),
                        confirmText = stringResource(R.string.confirm_clear_records_ok),
                        cancelText = stringResource(R.string.action_cancel),
                        isDanger = true,
                        onConfirm = viewModel::confirmClearRecords,
                        onDismiss = viewModel::dismissDialog
                    )
                    GameDialog.ConfirmResetAll -> ConfirmDialog(
                        title = stringResource(R.string.confirm_reset_title),
                        body = stringResource(R.string.confirm_reset_text),
                        confirmText = stringResource(R.string.confirm_reset_ok),
                        cancelText = stringResource(R.string.action_cancel),
                        isDanger = true,
                        onConfirm = viewModel::confirmResetAll,
                        onDismiss = viewModel::dismissDialog
                    )
                }
            }
        }
    }
}
