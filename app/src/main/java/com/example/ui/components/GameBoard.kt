package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.R
import com.example.game.GameEngine
import com.example.model.DigitsMode
import com.example.model.Direction
import com.example.model.GameSession
import com.example.model.GhostTile
import com.example.model.Tile
import com.example.ui.MergeBurst
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VazirmatnFontFamily
import com.example.ui.theme.tileStyleFor
import com.example.util.FaFormat
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

private val SlideEasing = CubicBezierEasing(0.22f, 0.8f, 0.32f, 1f)
private val PopEasing = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1.25f)

@Composable
fun GameBoard(
    session: GameSession,
    digitsMode: DigitsMode,
    animEnabled: Boolean,
    ghosts: List<GhostTile>,
    ghostToken: Long,
    mergeBursts: List<MergeBurst>,
    mergeBurstToken: Long,
    swipeGlowDir: Direction?,
    swipeGlowToken: Long,
    comboCount: Int,
    comboToken: Long,
    nudgeDir: Direction?,
    nudgeToken: Long,
    onSwipe: (Direction) -> Unit,
    onContinueAfterWin: () -> Unit,
    onNewGame: () -> Unit,
    onUndo: () -> Unit,
    canUndo: Boolean,
    boardTouchEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val nudgeOffsetX = remember { Animatable(0f) }
    val nudgeOffsetY = remember { Animatable(0f) }
    val edgeGlowAlpha = remember { Animatable(0f) }
    var activeGlowDir by remember { mutableStateOf<Direction?>(null) }
    var activeGlowIsError by remember { mutableStateOf(false) }

    var showCombo by remember { mutableStateOf(false) }
    LaunchedEffect(comboToken) {
        if (comboToken > 0L && comboCount >= 2 && animEnabled) {
            showCombo = true
            delay(850L)
            showCombo = false
        }
    }

    LaunchedEffect(swipeGlowToken) {
        if (swipeGlowToken > 0L && swipeGlowDir != null && animEnabled) {
            activeGlowDir = swipeGlowDir
            activeGlowIsError = false
            edgeGlowAlpha.snapTo(0.75f)
            edgeGlowAlpha.animateTo(0f, tween(220))
            activeGlowDir = null
        }
    }

    LaunchedEffect(nudgeToken) {
        if (nudgeToken > 0L && nudgeDir != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            if (animEnabled) {
                activeGlowDir = nudgeDir
                activeGlowIsError = true
                edgeGlowAlpha.snapTo(0.85f)
                val dist = with(density) { 7.dp.toPx() }
                val returnSpec = spring<Float>(dampingRatio = 0.6f, stiffness = 950f)
                when (nudgeDir) {
                    Direction.LEFT -> {
                        nudgeOffsetX.animateTo(-dist, tween(55))
                        nudgeOffsetX.animateTo(0f, returnSpec)
                    }
                    Direction.RIGHT -> {
                        nudgeOffsetX.animateTo(dist, tween(55))
                        nudgeOffsetX.animateTo(0f, returnSpec)
                    }
                    Direction.UP -> {
                        nudgeOffsetY.animateTo(-dist, tween(55))
                        nudgeOffsetY.animateTo(0f, returnSpec)
                    }
                    Direction.DOWN -> {
                        nudgeOffsetY.animateTo(dist, tween(55))
                        nudgeOffsetY.animateTo(0f, returnSpec)
                    }
                }
                edgeGlowAlpha.animateTo(0f, tween(180))
                activeGlowDir = null
            }
        }
    }

    val boardShape = remember { RoundedCornerShape(22.dp) }
    val boardBrush = remember(colors.boardBgStart, colors.boardBgMid, colors.boardBgEnd) {
        Brush.linearGradient(listOf(colors.boardBgStart, colors.boardBgMid, colors.boardBgEnd))
    }
    val overlayVisible = session.over || session.showWin
    val swipeThresholdPx = with(density) { 18.dp.toPx() }
    val lockScrollSlopPx = with(density) { 4.dp.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                translationX = nudgeOffsetX.value
                translationY = nudgeOffsetY.value
            }
            .shadow(elevation = if (colors.isLight) 6.dp else 10.dp, shape = boardShape, clip = false)
            .clip(boardShape)
            .background(boardBrush)
            .border(1.25.dp, colors.boardBorder, boardShape)
            .testTag("game_board")
            .pointerInput(overlayVisible, boardTouchEnabled) {
                if (overlayVisible || !boardTouchEnabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    val startPos = down.position
                    var lastPos = startPos
                    var triggered = false
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: event.changes.firstOrNull()
                        if (change == null || !change.pressed) {
                            break
                        }
                        lastPos = change.position
                        val dx = lastPos.x - startPos.x
                        val dy = lastPos.y - startPos.y
                        val dist = hypot(dx, dy)
                        if (dist >= lockScrollSlopPx) {
                            change.consume()
                        }
                        if (!triggered && dist >= swipeThresholdPx) {
                            val ax = abs(dx)
                            val ay = abs(dy)
                            if (ax > ay * 1.08f || ay > ax * 1.08f) {
                                triggered = true
                                val dir = if (ax > ay) {
                                    if (dx > 0f) Direction.RIGHT else Direction.LEFT
                                } else {
                                    if (dy > 0f) Direction.DOWN else Direction.UP
                                }
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSwipe(dir)
                            }
                        }
                    }
                    if (!triggered && hypot(lastPos.x - startPos.x, lastPos.y - startPos.y) < lockScrollSlopPx * 2f) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val relX = startPos.x - centerX
                        val relY = startPos.y - centerY
                        val dir = if (abs(relX) >= abs(relY) * 0.55f) {
                            if (relX >= 0f) Direction.RIGHT else Direction.LEFT
                        } else {
                            if (relY >= 0f) Direction.DOWN else Direction.UP
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSwipe(dir)
                    }
                }
            }
    ) {
        val boardSize = maxWidth
        val gap = if (boardSize < 340.dp) 9.dp else 12.dp
        val tileSize = (boardSize - gap * (GameEngine.SIZE + 1)) / GameEngine.SIZE
        val cellRadius = if (boardSize < 340.dp) 10.dp else 12.dp

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            // Single GPU-cached pass draws all 16 static background slots with zero child layout nodes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val gapPx = gap.toPx()
                        val tilePx = tileSize.toPx()
                        val radiusPx = cellRadius.toPx()
                        val corner = CornerRadius(radiusPx, radiusPx)
                        val cellSize = Size(tilePx, tilePx)
                        val stroke = Stroke(width = 0.75.dp.toPx())
                        val cellColor = colors.cell
                        val borderColor = colors.cellLine
                        onDrawBehind {
                            for (r in 0 until GameEngine.SIZE) {
                                val y = gapPx + (tilePx + gapPx) * r
                                for (c in 0 until GameEngine.SIZE) {
                                    val x = gapPx + (tilePx + gapPx) * c
                                    val topLeft = Offset(x, y)
                                    drawRoundRect(
                                        color = cellColor,
                                        topLeft = topLeft,
                                        size = cellSize,
                                        cornerRadius = corner
                                    )
                                    drawRoundRect(
                                        color = borderColor,
                                        topLeft = topLeft,
                                        size = cellSize,
                                        cornerRadius = corner,
                                        style = stroke
                                    )
                                }
                            }
                        }
                    }
            ) {
                // Ghost tiles sliding into merge destination
                if (animEnabled && ghosts.isNotEmpty()) {
                    for (ghost in ghosts) {
                        key(ghostToken, ghost.id) {
                            GhostTileView(
                                ghost = ghost,
                                tileSize = tileSize,
                                gap = gap,
                                cellRadius = cellRadius,
                                digitsMode = digitsMode,
                                isLight = colors.isLight
                            )
                        }
                    }
                }

                // Active animated tiles (GPU-only translation via graphicsLayer — zero layout passes per frame)
                for (tile in session.tiles) {
                    key(tile.id) {
                        AnimatedTileView(
                            tile = tile,
                            tileSize = tileSize,
                            gap = gap,
                            cellRadius = cellRadius,
                            digitsMode = digitsMode,
                            animEnabled = animEnabled,
                            isLight = colors.isLight
                        )
                    }
                }

                // Lightweight merge shockwave & spark burst (only mounted during 300ms burst)
                if (animEnabled && mergeBursts.isNotEmpty()) {
                    MergeBurstOverlay(
                        bursts = mergeBursts,
                        burstToken = mergeBurstToken,
                        tileSize = tileSize,
                        gap = gap,
                        isLight = colors.isLight
                    )
                }

                // Directional edge glow pulse (only mounted during 200ms pulse)
                val glowDir = activeGlowDir
                if (animEnabled && glowDir != null) {
                    val glowTint = if (activeGlowIsError) colors.danger else colors.brand2
                    SwipeEdgeGlowCanvas(
                        direction = glowDir,
                        alphaProvider = { edgeGlowAlpha.value * if (colors.isLight) 0.42f else 0.50f },
                        color = glowTint
                    )
                }
            }
        }

        // Combo streak floating badge
        AnimatedVisibility(
            visible = showCombo && comboCount >= 2 && !overlayVisible,
            enter = fadeIn(tween(130)) + scaleIn(initialScale = 0.78f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 700f)) + slideInVertically { -it / 2 },
            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.92f) + slideOutVertically { -it / 2 },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .zIndex(8f)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(colors.brand, colors.brand3, colors.brand2)))
                        .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.combo_badge, FaFormat.fmt(comboCount, digitsMode)),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // Win / Game Over overlays
        AnimatedVisibility(
            visible = overlayVisible,
            enter = if (animEnabled) fadeIn(tween(200)) + scaleIn(initialScale = 0.95f) else fadeIn(snap()),
            exit = if (animEnabled) fadeOut(tween(140)) else fadeOut(snap()),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.overlayBg)
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (session.over) {
                        GameOverOverlay(
                            score = session.score,
                            maxTile = GameEngine.maxTileValue(session.tiles),
                            moves = session.moves,
                            seconds = session.timeBank,
                            digitsMode = digitsMode,
                            canUndo = canUndo,
                            onUndo = onUndo,
                            onNewGame = onNewGame
                        )
                    } else if (session.showWin) {
                        WinOverlay(
                            onContinue = onContinueAfterWin,
                            onNewGame = onNewGame
                        )
                    }
                }
            }
        }
    }
}

private data class Spark(
    val cosA: Float,
    val sinA: Float,
    val speed: Float,
    val radius: Float
)

@Composable
private fun MergeBurstOverlay(
    bursts: List<MergeBurst>,
    burstToken: Long,
    tileSize: Dp,
    gap: Dp,
    isLight: Boolean
) {
    val colors = LocalAppColors.current
    val density = LocalDensity.current
    val tilePx = with(density) { tileSize.toPx() }
    val gapPx = with(density) { gap.toPx() }
    val sparkDensityScale = density.density * 0.58f

    val sparkMap = remember(burstToken) {
        bursts.associate { b ->
            val rnd = Random(b.id * 31L + burstToken)
            val count = if (b.value >= 256) 8 else 6
            b.id to List(count) { idx ->
                val angle = (2.0 * PI * idx / count).toFloat() + (rnd.nextFloat() - 0.5f) * 0.38f
                Spark(
                    cosA = cos(angle),
                    sinA = sin(angle),
                    speed = 0.58f + rnd.nextFloat() * 0.52f,
                    radius = 2.4f + rnd.nextFloat() * 2.6f
                )
            }
        }
    }

    var progress by remember(burstToken) { mutableFloatStateOf(0f) }

    LaunchedEffect(burstToken) {
        val durationNanos = 300_000_000L
        val start = withFrameNanos { it }
        while (true) {
            val now = withFrameNanos { it }
            val p = ((now - start).toFloat() / durationNanos).coerceIn(0f, 1f)
            progress = p
            if (p >= 1f) break
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(5f)
    ) {
        val eased = 1f - (1f - progress) * (1f - progress)
        val fade = (1f - progress).coerceIn(0f, 1f)
        if (fade <= 0.02f) return@Canvas

        for (burst in bursts) {
            val cx = gapPx + (tilePx + gapPx) * burst.c + tilePx * 0.5f
            val cy = gapPx + (tilePx + gapPx) * burst.r + tilePx * 0.5f
            val color = tileStyleFor(burst.value, isLight, colors.palette).particleColor

            val ringRadius = tilePx * (0.36f + eased * 0.48f)
            val strokeW = (tilePx * 0.065f * fade).coerceAtLeast(1.5f)
            drawCircle(
                color = color.copy(alpha = fade * 0.5f),
                radius = ringRadius,
                center = Offset(cx, cy),
                style = Stroke(width = strokeW)
            )

            val sparks = sparkMap[burst.id].orEmpty()
            val maxTravel = tilePx * if (burst.value >= 256) 0.82f else 0.66f
            for (s in sparks) {
                val dist = eased * maxTravel * s.speed
                val r = s.radius * (0.45f + 0.55f * fade) * sparkDensityScale
                drawCircle(
                    color = color.copy(alpha = fade * 0.85f),
                    radius = r,
                    center = Offset(cx + s.cosA * dist, cy + s.sinA * dist)
                )
            }
        }
    }
}

@Composable
private fun SwipeEdgeGlowCanvas(
    direction: Direction,
    alphaProvider: () -> Float,
    color: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(4f)
    ) {
        val alpha = alphaProvider()
        if (alpha <= 0.01f) return@Canvas
        val thickness = size.minDimension * 0.12f
        val brush = when (direction) {
            Direction.LEFT -> Brush.horizontalGradient(
                colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                startX = 0f,
                endX = thickness
            )
            Direction.RIGHT -> Brush.horizontalGradient(
                colors = listOf(Color.Transparent, color.copy(alpha = alpha)),
                startX = size.width - thickness,
                endX = size.width
            )
            Direction.UP -> Brush.verticalGradient(
                colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                startY = 0f,
                endY = thickness
            )
            Direction.DOWN -> Brush.verticalGradient(
                colors = listOf(Color.Transparent, color.copy(alpha = alpha)),
                startY = size.height - thickness,
                endY = size.height
            )
        }
        drawRect(brush = brush)
    }
}

@Composable
private fun AnimatedTileView(
    tile: Tile,
    tileSize: Dp,
    gap: Dp,
    cellRadius: Dp,
    digitsMode: DigitsMode,
    animEnabled: Boolean,
    isLight: Boolean
) {
    val density = LocalDensity.current
    val targetXPx = with(density) { (gap + (tileSize + gap) * tile.c).toPx() }
    val targetYPx = with(density) { (gap + (tileSize + gap) * tile.r).toPx() }

    val animSpec = remember(animEnabled) {
        if (animEnabled) {
            spring<Float>(dampingRatio = 0.88f, stiffness = 920f, visibilityThreshold = 0.5f)
        } else {
            snap()
        }
    }
    val animX by animateFloatAsState(targetValue = targetXPx, animationSpec = animSpec, label = "tx")
    val animY by animateFloatAsState(targetValue = targetYPx, animationSpec = animSpec, label = "ty")

    val scaleAnim = remember { Animatable(if (animEnabled && (tile.spawned || tile.merged)) 0.25f else 1f) }
    val alphaAnim = remember { Animatable(if (animEnabled && (tile.spawned || tile.merged)) 0.15f else 1f) }

    LaunchedEffect(tile.id, tile.merged, animEnabled) {
        if (!animEnabled) {
            scaleAnim.snapTo(1f)
            alphaAnim.snapTo(1f)
            return@LaunchedEffect
        }
        if (tile.spawned) {
            alphaAnim.animateTo(1f, tween(115))
        } else if (tile.merged) {
            alphaAnim.animateTo(1f, tween(85))
        } else {
            alphaAnim.snapTo(1f)
        }
    }

    LaunchedEffect(tile.id, tile.merged, animEnabled) {
        if (!animEnabled) {
            scaleAnim.snapTo(1f)
            return@LaunchedEffect
        }
        if (tile.spawned) {
            scaleAnim.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 780f)
            )
        } else if (tile.merged) {
            scaleAnim.animateTo(1.13f, tween(95, delayMillis = 30, easing = PopEasing))
            scaleAnim.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = 860f))
        } else {
            scaleAnim.snapTo(1f)
        }
    }

    TileFace(
        value = tile.value,
        tileSize = tileSize,
        cellRadius = cellRadius,
        digitsMode = digitsMode,
        isLight = isLight,
        modifier = Modifier
            .zIndex(if (tile.merged) 3f else 2f)
            .graphicsLayer {
                translationX = animX
                translationY = animY
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
                alpha = alphaAnim.value
            }
    )
}

@Composable
private fun GhostTileView(
    ghost: GhostTile,
    tileSize: Dp,
    gap: Dp,
    cellRadius: Dp,
    digitsMode: DigitsMode,
    isLight: Boolean
) {
    val density = LocalDensity.current
    val fromX = with(density) { (gap + (tileSize + gap) * ghost.fromC).toPx() }
    val fromY = with(density) { (gap + (tileSize + gap) * ghost.fromR).toPx() }
    val toX = with(density) { (gap + (tileSize + gap) * ghost.toC).toPx() }
    val toY = with(density) { (gap + (tileSize + gap) * ghost.toR).toPx() }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(ghost.id) {
        progress.animateTo(1f, tween(115, easing = SlideEasing))
    }

    TileFace(
        value = ghost.value,
        tileSize = tileSize,
        cellRadius = cellRadius,
        digitsMode = digitsMode,
        isLight = isLight,
        modifier = Modifier
            .zIndex(1f)
            .graphicsLayer {
                val p = progress.value
                translationX = fromX + (toX - fromX) * p
                translationY = fromY + (toY - fromY) * p
                val fade = (1f - ((p - 0.35f) / 0.65f).coerceIn(0f, 1f))
                alpha = fade
                val s = 1f - 0.14f * (1f - fade)
                scaleX = s
                scaleY = s
            }
    )
}

@Composable
private fun TileFace(
    value: Int,
    tileSize: Dp,
    cellRadius: Dp,
    digitsMode: DigitsMode,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val style = tileStyleFor(value, isLight, colors.palette)
    val len = value.toString().length
    val fontFactor = when (len) {
        1, 2 -> 0.42f
        3 -> 0.345f
        4 -> 0.275f
        5 -> 0.225f
        6 -> 0.185f
        else -> 0.16f
    }
    val fontSizeSp = (tileSize.value * fontFactor).sp
    val shape = remember(cellRadius) { RoundedCornerShape(cellRadius) }
    val text = remember(value, digitsMode) { FaFormat.fmtTile(value, digitsMode) }

    Box(
        modifier = modifier
            .size(tileSize)
            .clip(shape)
            .background(style.background)
            .then(
                if (style.hasSheen) {
                    Modifier.drawWithCache {
                        val sheenBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.24f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                        onDrawBehind {
                            drawRect(brush = sheenBrush)
                        }
                    }
                } else {
                    Modifier
                }
            )
            .border(
                width = if (style.hasSheen) 1.25.dp else 1.dp,
                color = style.borderColor ?: Color.White.copy(alpha = 0.18f),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = style.textColor,
            fontFamily = VazirmatnFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSizeSp,
            lineHeight = fontSizeSp,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun WinOverlay(
    onContinue: () -> Unit,
    onNewGame: () -> Unit
) {
    val colors = LocalAppColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(0.92f)
    ) {
        Text(text = "🏆", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.overlay_win_title),
            style = MaterialTheme.typography.headlineSmall,
            color = colors.text,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.overlay_win_body),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.muted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            PrimaryGradientButton(
                text = stringResource(R.string.action_continue),
                onClick = onContinue,
                modifier = Modifier.testTag("overlay_continue_button")
            )
            GlassButton(
                text = stringResource(R.string.action_new_game),
                onClick = onNewGame,
                modifier = Modifier.testTag("overlay_new_game_button")
            )
        }
    }
}

@Composable
private fun GameOverOverlay(
    score: Int,
    maxTile: Int,
    moves: Int,
    seconds: Int,
    digitsMode: DigitsMode,
    canUndo: Boolean,
    onUndo: () -> Unit,
    onNewGame: () -> Unit
) {
    val colors = LocalAppColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(0.92f)
    ) {
        Text(text = "🧩", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.overlay_over_title),
            style = MaterialTheme.typography.headlineSmall,
            color = colors.text,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = FaFormat.fmt(score, digitsMode),
            style = MaterialTheme.typography.displaySmall,
            color = colors.text,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(
                R.string.overlay_over_summary,
                FaFormat.fmtTile(maxTile, digitsMode),
                FaFormat.fmt(moves, digitsMode),
                FaFormat.fmtTime(seconds, digitsMode)
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.muted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            if (canUndo) {
                GlassButton(
                    text = stringResource(R.string.action_undo),
                    onClick = onUndo,
                    modifier = Modifier.testTag("overlay_undo_button")
                )
            }
            PrimaryGradientButton(
                text = stringResource(R.string.action_new_game),
                onClick = onNewGame,
                modifier = Modifier.testTag("overlay_over_new_game_button")
            )
        }
    }
}
