package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.game.GameEngine
import com.example.model.DigitsMode
import com.example.model.Direction
import com.example.ui.components.AllRecordsDialog
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.ControlsCard
import com.example.ui.components.GameBoard
import com.example.ui.components.GameToolbar
import com.example.ui.components.HeaderBar
import com.example.ui.components.HelpDialog
import com.example.ui.components.RecordsCard
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatusCard
import com.example.ui.components.ToastPill
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.Puzzle2048Theme
import com.example.util.FaFormat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.hypot

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSplash by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(state.isReady) {
        if (state.isReady && showSplash) {
            delay(1050L)
            showSplash = false
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.onAppPaused()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.onAppPaused()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onAppResumed()
    }

    BackHandler(enabled = state.dialog != GameDialog.None) {
        viewModel.dismissDialog()
    }

    Puzzle2048Theme(
        themeMode = state.settings.theme,
        palette = state.settings.palette
    ) {
        val colors = LocalAppColors.current
        val density = LocalDensity.current
        val haptic = LocalHapticFeedback.current
        val swipeThresholdPx = with(density) { 18.dp.toPx() }
        val tapSlopPx = with(density) { 10.dp.toPx() }

        val fullScreenGestureActive = state.settings.fullScreenTouch &&
            !showSplash &&
            state.dialog == GameDialog.None &&
            !state.game.over &&
            !state.game.showWin

        val bgGradient = remember(colors.bgBase, colors.bgMid) {
            Brush.verticalGradient(listOf(colors.bgBase, colors.bgMid, colors.bgBase))
        }

        Scaffold(
            containerColor = colors.bgBase,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgGradient)
                    .drawWithCache {
                        val pCenter = Offset(size.width * 0.86f, -size.height * 0.05f)
                        val pRadius = size.maxDimension * 0.56f
                        val pBrush = Brush.radialGradient(
                            colors = listOf(colors.bgGlowPrimary, Color.Transparent),
                            center = pCenter,
                            radius = pRadius
                        )
                        val cCenter = Offset(size.width * 0.06f, size.height * 0.10f)
                        val cRadius = size.maxDimension * 0.46f
                        val cBrush = Brush.radialGradient(
                            colors = listOf(colors.bgGlowSecondary, Color.Transparent),
                            center = cCenter,
                            radius = cRadius
                        )
                        val rCenter = Offset(size.width * 0.52f, size.height * 1.04f)
                        val rRadius = size.maxDimension * 0.50f
                        val rBrush = Brush.radialGradient(
                            colors = listOf(colors.bgGlowTertiary, Color.Transparent),
                            center = rCenter,
                            radius = rRadius
                        )
                        onDrawBehind {
                            drawCircle(brush = pBrush, radius = pRadius, center = pCenter)
                            drawCircle(brush = cBrush, radius = cRadius, center = cCenter)
                            drawCircle(brush = rBrush, radius = rRadius, center = rCenter)
                        }
                    }
                    .pointerInput(fullScreenGestureActive) {
                        if (!fullScreenGestureActive) return@pointerInput
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                            val startPos = down.position
                            var lastPos = startPos
                            var triggered = false
                            var childConsumedTap = down.isConsumed

                            while (true) {
                                val event = awaitPointerEvent(pass = PointerEventPass.Main)
                                val change = event.changes.firstOrNull { it.id == down.id } ?: event.changes.firstOrNull()
                                if (change == null) break
                                if (change.isConsumed) {
                                    childConsumedTap = true
                                }
                                if (!change.pressed) {
                                    lastPos = change.position
                                    break
                                }
                                lastPos = change.position
                                val dx = lastPos.x - startPos.x
                                val dy = lastPos.y - startPos.y
                                val dist = hypot(dx, dy)
                                if (!triggered && dist >= swipeThresholdPx) {
                                    val ax = abs(dx)
                                    val ay = abs(dy)
                                    if (ax > ay * 1.08f || ay > ax * 1.08f) {
                                        triggered = true
                                        change.consume()
                                        val dir = if (ax > ay) {
                                            if (dx > 0f) Direction.RIGHT else Direction.LEFT
                                        } else {
                                            if (dy > 0f) Direction.DOWN else Direction.UP
                                        }
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.move(dir)
                                    }
                                }
                            }

                            // Direct tap/click anywhere on the screen outside buttons moves tiles left/right (or up/down in center band)
                            val totalMove = hypot(lastPos.x - startPos.x, lastPos.y - startPos.y)
                            if (!triggered && !childConsumedTap && totalMove < tapSlopPx) {
                                val centerX = size.width / 2f
                                val dxFromCenter = startPos.x - centerX
                                val centerBand = size.width * 0.11f
                                val dir = if (abs(dxFromCenter) >= centerBand) {
                                    if (dxFromCenter > 0f) Direction.RIGHT else Direction.LEFT
                                } else {
                                    if (startPos.y < size.height * 0.42f) Direction.UP else Direction.DOWN
                                }
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.move(dir)
                            }
                        }
                    }
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
                            .verticalScroll(
                                state = scrollState,
                                enabled = !state.settings.fullScreenTouch
                            )
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
                                                mergeBursts = state.mergeBursts,
                                                mergeBurstToken = state.mergeBurstToken,
                                                swipeGlowDir = state.swipeGlowDir,
                                                swipeGlowToken = state.swipeGlowToken,
                                                comboCount = state.comboCount,
                                                comboToken = state.comboToken,
                                                nudgeDir = state.nudgeDir,
                                                nudgeToken = state.nudgeToken,
                                                onSwipe = viewModel::move,
                                                onContinueAfterWin = viewModel::continueAfterWin,
                                                onNewGame = viewModel::requestNewGame,
                                                onUndo = viewModel::undo,
                                                canUndo = state.undoCount > 0,
                                                boardTouchEnabled = !state.settings.fullScreenTouch
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.width(336.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        StatusCard(
                                            maxTile = maxTile,
                                            moves = state.game.moves,
                                            elapsedSecondsFlow = viewModel.elapsedSeconds,
                                            stats = state.stats,
                                            digitsMode = state.settings.digits,
                                            animEnabled = state.settings.anim
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
                                        mergeBursts = state.mergeBursts,
                                        mergeBurstToken = state.mergeBurstToken,
                                        swipeGlowDir = state.swipeGlowDir,
                                        swipeGlowToken = state.swipeGlowToken,
                                        comboCount = state.comboCount,
                                        comboToken = state.comboToken,
                                        nudgeDir = state.nudgeDir,
                                        nudgeToken = state.nudgeToken,
                                        onSwipe = viewModel::move,
                                        onContinueAfterWin = viewModel::continueAfterWin,
                                        onNewGame = viewModel::requestNewGame,
                                        onUndo = viewModel::undo,
                                        canUndo = state.undoCount > 0,
                                        boardTouchEnabled = !state.settings.fullScreenTouch
                                    )
                                    StatusCard(
                                        maxTile = maxTile,
                                        moves = state.game.moves,
                                        elapsedSecondsFlow = viewModel.elapsedSeconds,
                                        stats = state.stats,
                                        digitsMode = state.settings.digits,
                                        animEnabled = state.settings.anim
                                    )
                                    RecordsCard(
                                        records = state.records,
                                        digitsMode = state.settings.digits,
                                        onOpenAll = viewModel::openAllRecords
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                ConfettiOverlay(
                    triggerToken = state.confettiToken,
                    animEnabled = state.settings.anim
                )

                // Clean single navigationBarsPadding so bottom toast never gets double-padded
                AnimatedVisibility(
                    visible = state.toast != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    state.toast?.let { msg ->
                        ToastPill(text = msg.text)
                    }
                }

                // Soft animated entrance logo splash overlay
                AnimatedVisibility(
                    visible = showSplash,
                    enter = fadeIn(tween(180)),
                    exit = fadeOut(tween(420)) + scaleOut(targetScale = 1.05f, animationSpec = tween(420))
                ) {
                    SplashIntroOverlay(
                        digitsMode = state.settings.digits,
                        onSkip = { showSplash = false }
                    )
                }

                when (state.dialog) {
                    GameDialog.None -> Unit
                    GameDialog.Settings -> SettingsDialog(
                        settings = state.settings,
                        onThemeChange = viewModel::setThemeMode,
                        onPaletteChange = viewModel::setColorPalette,
                        onFullScreenTouchChange = viewModel::setFullScreenTouch,
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

@Composable
private fun SplashIntroOverlay(
    digitsMode: DigitsMode,
    onSkip: () -> Unit
) {
    val colors = LocalAppColors.current
    val logoScale = remember { Animatable(0.72f) }
    val logoAlpha = remember { Animatable(0f) }
    val haloScale = remember { Animatable(0.65f) }
    val tileScales = remember { List(4) { Animatable(0.2f) } }
    val textAlpha = remember { Animatable(0f) }
    val textSlideY = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        launch { logoAlpha.animateTo(1f, tween(340)) }
        launch {
            logoScale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            haloScale.animateTo(1.18f, tween(880, easing = FastOutSlowInEasing))
        }
        tileScales.forEachIndexed { i, anim ->
            launch {
                delay(70L * i)
                anim.animateTo(
                    1f,
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 620f)
                )
            }
        }
        launch {
            delay(200L)
            textAlpha.animateTo(1f, tween(380))
        }
        launch {
            delay(200L)
            textSlideY.animateTo(0f, spring(dampingRatio = 0.82f, stiffness = 420f))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(colors.bgBase, colors.bgMid, colors.bgBase)
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSkip
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(340.dp)
                .graphicsLayer {
                    scaleX = haloScale.value
                    scaleY = haloScale.value
                }
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.brand.copy(alpha = 0.30f),
                        colors.brand2.copy(alpha = 0.14f),
                        Color.Transparent
                    )
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val shape = RoundedCornerShape(30.dp)
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    }
                    .clip(shape)
                    .background(Brush.linearGradient(listOf(colors.brand, colors.brand3, colors.brand2)))
                    .border(1.25.dp, Color.White.copy(alpha = 0.45f), shape)
                    .padding(11.dp)
            ) {
                val alphas = listOf(0.95f, 0.58f, 0.76f, 0.38f)
                val creamTile = Color(0xFFFFFAF2)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxSize()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                        for (idx in 0..1) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .graphicsLayer {
                                        scaleX = tileScales[idx].value
                                        scaleY = tileScales[idx].value
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(creamTile.copy(alpha = alphas[idx]))
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                        for (idx in 2..3) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .graphicsLayer {
                                        scaleX = tileScales[idx].value
                                        scaleY = tileScales[idx].value
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(creamTile.copy(alpha = alphas[idx]))
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                    translationY = textSlideY.value
                }
            ) {
                Text(
                    text = FaFormat.toDigits("2048", digitsMode),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 36.sp),
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.text
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.splash_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.muted
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.glass2)
                        .border(1.dp, colors.line, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.splash_version),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.muted
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "${stringResource(R.string.creator_credit)} • ${stringResource(R.string.creator_handle)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.brand2
                )
            }
        }
    }
}
