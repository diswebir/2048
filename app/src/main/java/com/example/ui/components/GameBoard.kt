package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.roundToInt
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
            delay(950L)
            showCombo = false
        }
    }

    LaunchedEffect(swipeGlowToken) {
        if (swipeGlowToken > 0L && swipeGlowDir != null && animEnabled) {
            activeGlowDir = swipeGlowDir
            activeGlowIsError = false
            edgeGlowAlpha.snapTo(0.85f)
            edgeGlowAlpha.animateTo(0f, tween(280))
        }
    }

    LaunchedEffect(nudgeToken) {
        if (nudgeToken > 0L && nudgeDir != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            if (animEnabled) {
                activeGlowDir = nudgeDir
                activeGlowIsError = true
                edgeGlowAlpha.snapTo(0.9f)
                val dist = with(density) { 8.dp.toPx() }
                val returnSpec = spring<Float>(dampingRatio = 0.55f, stiffness = 950f)
                when (nudgeDir) {
                    Direction.LEFT -> {
                        nudgeOffsetX.animateTo(-dist, tween(60))
                        nudgeOffsetX.animateTo(0f, returnSpec)
                    }
                    Direction.RIGHT -> {
                        nudgeOffsetX.animateTo(dist, tween(60))
                        nudgeOffsetX.animateTo(0f, returnSpec)
                    }
                    Direction.UP -> {
                        nudgeOffsetY.animateTo(-dist, tween(60))
                        nudgeOffsetY.animateTo(0f, returnSpec)
                    }
                    Direction.DOWN -> {
                        nudgeOffsetY.animateTo(dist, tween(60))
                        nudgeOffsetY.animateTo(0f, returnSpec)
                    }
                }
                edgeGlowAlpha.animateTo(0f, tween(220))
            }
        }
    }

    val boardShape = RoundedCornerShape(22.dp)
    val overlayVisible = session.over || session.showWin
    val swipeThresholdPx = with(density) { 18.dp.toPx() }
    val lockScrollSlopPx = with(density) { 4.dp.toPx() }

    // Single shared sheen phase for all high-value tiles (>= 512)
    val sheenTransition = rememberInfiniteTransition(label = "tileSheen")
    val sheenProgress by sheenTransition.animateFloat(
        initialValue = -0.6f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sheenProgress"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .offset {
                IntOffset(nudgeOffsetX.value.roundToInt(), nudgeOffsetY.value.roundToInt())
            }
            .shadow(elevation = if (colors.isLight) 14.dp else 24.dp, shape = boardShape, clip = false)
            .clip(boardShape)
            .background(
                Brush.linearGradient(
                    listOf(colors.boardBgStart, colors.boardBgMid, colors.boardBgEnd)
                )
            )
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
            Box(modifier = Modifier.fillMaxSize()) {
                // Background empty cell grid
                for (r in 0 until GameEngine.SIZE) {
                    for (c in 0 until GameEngine.SIZE) {
                        val x = gap + (tileSize + gap) * c
                        val y = gap + (tileSize + gap) * r
                        Box(
                            modifier = Modifier
                                .offset(x = x, y = y)
                                .size(tileSize)
                                .clip(RoundedCornerShape(cellRadius))
                                .background(colors.cell)
                                .border(0.75.dp, colors.cellLine, RoundedCornerShape(cellRadius))
                        )
                    }
                }

                // Ghost tiles sliding into merge destination
                if (animEnabled) {
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

                // Active animated tiles
                for (tile in session.tiles) {
                    key(tile.id) {
                        AnimatedTileView(
                            tile = tile,
                            tileSize = tileSize,
                            gap = gap,
                            cellRadius = cellRadius,
                            digitsMode = digitsMode,
                            animEnabled = animEnabled,
                            isLight = colors.isLight,
                            sheenProgress = sheenProgress
                        )
                    }
                }

                // Merge shockwave & radial particle burst canvas
                if (animEnabled && mergeBursts.isNotEmpty()) {
                    MergeBurstOverlay(
                        bursts = mergeBursts,
                        burstToken = mergeBurstToken,
                        tileSize = tileSize,
                        gap = gap,
                        isLight = colors.isLight
                    )
                }

                // Directional edge glow pulse
                if (animEnabled && activeGlowDir != null && edgeGlowAlpha.value > 0.01f) {
                    val glowTint = if (activeGlowIsError) colors.danger else colors.brand2
                    SwipeEdgeGlowCanvas(
                        direction = activeGlowDir!!,
                        alpha = edgeGlowAlpha.value * if (colors.isLight) 0.45f else 0.55f,
                        color = glowTint
                    )
                }
            }
        }

        // Combo streak floating badge
        AnimatedVisibility(
            visible = showCombo && comboCount >= 2 && !overlayVisible,
            enter = fadeIn(tween(140)) + scaleIn(initialScale = 0.7f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 700f)) + slideInVertically { -it / 2 },
            exit = fadeOut(tween(240)) + scaleOut(targetScale = 0.9f) + slideOutVertically { -it / 2 },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .zIndex(8f)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(
                    modifier = Modifier
                        .shadow(10.dp, CircleShape, ambientColor = colors.brand, spotColor = colors.gold)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(colors.brand, colors.brand3, colors.brand2)))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
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
            enter = if (animEnabled) fadeIn(tween(220)) + scaleIn(initialScale = 0.94f, animationSpec = spring(dampingRatio = 0.78f, stiffness = 500f)) else fadeIn(snap()),
            exit = if (animEnabled) fadeOut(tween(150)) else fadeOut(snap()),
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
    val angle: Float,
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
    val density = LocalDensity.current
    val tilePx = with(density) { tileSize.toPx() }
    val gapPx = with(density) { gap.toPx() }

    val sparkMap = remember(burstToken) {
        bursts.associate { b ->
            val rnd = Random(b.id * 31L + burstToken)
            val count = when {
                b.value >= 1024 -> 18
                b.value >= 128 -> 14
                else -> 10
            }
            b.id to List(count) { idx ->
                val baseAngle = (2.0 * PI * idx / count).toFloat()
                Spark(
                    angle = baseAngle + (rnd.nextFloat() - 0.5f) * 0.45f,
                    speed = 0.58f + rnd.nextFloat() * 0.62f,
                    radius = 2.5f + rnd.nextFloat() * 3.5f
                )
            }
        }
    }

    var progress by remember(burstToken) { mutableFloatStateOf(0f) }

    LaunchedEffect(burstToken) {
        val durationNanos = 460_000_000L
        val start = withFrameNanos { it }
        while (true) {
            val now = withFrameNanos { it }
            val p = ((now - start).toFloat() / durationNanos).coerceIn(0f, 1f)
            progress = p
            if (p >= 1f) break
        }
    }

    val colors = LocalAppColors.current
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(5f)
    ) {
        val eased = 1f - (1f - progress) * (1f - progress)
        val fade = (1f - progress).coerceIn(0f, 1f)
        if (fade <= 0.01f) return@Canvas

        for (burst in bursts) {
            val cx = gapPx + (tilePx + gapPx) * burst.c + tilePx / 2f
            val cy = gapPx + (tilePx + gapPx) * burst.r + tilePx / 2f
            val style = tileStyleFor(burst.value, isLight, colors.palette)
            val color = style.particleColor

            // Expanding shockwave ring
            val ringRadius = tilePx * (0.36f + eased * 0.54f)
            val strokeW = (tilePx * 0.075f * fade).coerceAtLeast(1.5f)
            drawCircle(
                color = color.copy(alpha = fade * 0.55f),
                radius = ringRadius,
                center = Offset(cx, cy),
                style = Stroke(width = strokeW)
            )

            // Radial glowing sparks
            val sparks = sparkMap[burst.id].orEmpty()
            val maxTravel = tilePx * if (burst.value >= 256) 0.92f else 0.72f
            for (s in sparks) {
                val dist = eased * maxTravel * s.speed
                val sx = cx + cos(s.angle) * dist
                val sy = cy + sin(s.angle) * dist
                val r = s.radius * (0.4f + 0.6f * fade) * density.density * 0.6f
                drawCircle(
                    color = color.copy(alpha = fade * 0.9f),
                    radius = r,
                    center = Offset(sx, sy)
                )
            }
        }
    }
}

@Composable
private fun SwipeEdgeGlowCanvas(
    direction: Direction,
    alpha: Float,
    color: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(4f)
    ) {
        val thickness = size.minDimension * 0.14f
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
    isLight: Boolean,
    sheenProgress: Float
) {
    val density = LocalDensity.current
    val targetXPx = with(density) { (gap + (tileSize + gap) * tile.c).toPx().roundToInt() }
    val targetYPx = with(density) { (gap + (tileSize + gap) * tile.r).toPx().roundToInt() }

    val animatedOffset by animateIntOffsetAsState(
        targetValue = IntOffset(targetXPx, targetYPx),
        animationSpec = if (animEnabled) {
            spring(
                dampingRatio = 0.86f,
                stiffness = 880f,
                visibilityThreshold = IntOffset.VisibilityThreshold
            )
        } else {
            snap()
        },
        label = "tileOffset_${tile.id}"
    )

    val scaleAnim = remember { Animatable(if (animEnabled && (tile.spawned || tile.merged)) 0.22f else 1f) }
    val alphaAnim = remember { Animatable(if (animEnabled && (tile.spawned || tile.merged)) 0.12f else 1f) }

    LaunchedEffect(tile.id, tile.merged, animEnabled) {
        if (!animEnabled) {
            scaleAnim.snapTo(1f)
            alphaAnim.snapTo(1f)
            return@LaunchedEffect
        }
        if (tile.spawned) {
            alphaAnim.animateTo(1f, tween(130))
        } else if (tile.merged) {
            alphaAnim.animateTo(1f, tween(95))
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
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 760f)
            )
        } else if (tile.merged) {
            scaleAnim.animateTo(1.15f, tween(110, delayMillis = 40, easing = PopEasing))
            scaleAnim.animateTo(1f, spring(dampingRatio = 0.68f, stiffness = 820f))
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
        animEnabled = animEnabled,
        sheenProgress = sheenProgress,
        modifier = Modifier
            .offset { animatedOffset }
            .zIndex(if (tile.merged) 3f else 2f)
            .graphicsLayer {
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

    val xAnim = remember { Animatable(fromX) }
    val yAnim = remember { Animatable(fromY) }
    val alphaAnim = remember { Animatable(1f) }
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(ghost.id) {
        xAnim.animateTo(toX, tween(115, easing = SlideEasing))
    }
    LaunchedEffect(ghost.id) {
        yAnim.animateTo(toY, tween(115, easing = SlideEasing))
    }
    LaunchedEffect(ghost.id) {
        alphaAnim.animateTo(0f, tween(110, delayMillis = 45))
    }
    LaunchedEffect(ghost.id) {
        scaleAnim.animateTo(0.84f, tween(110, delayMillis = 45))
    }

    TileFace(
        value = ghost.value,
        tileSize = tileSize,
        cellRadius = cellRadius,
        digitsMode = digitsMode,
        isLight = isLight,
        animEnabled = false,
        sheenProgress = 0f,
        modifier = Modifier
            .offset { IntOffset(xAnim.value.roundToInt(), yAnim.value.roundToInt()) }
            .zIndex(1f)
            .scale(scaleAnim.value)
            .alpha(alphaAnim.value)
    )
}

@Composable
private fun TileFace(
    value: Int,
    tileSize: Dp,
    cellRadius: Dp,
    digitsMode: DigitsMode,
    isLight: Boolean,
    animEnabled: Boolean,
    sheenProgress: Float,
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
    val shape = RoundedCornerShape(cellRadius)

    Box(
        modifier = modifier
            .size(tileSize)
            .shadow(
                elevation = when {
                    value >= 128 -> 11.dp
                    isLight -> 5.dp
                    else -> 4.dp
                },
                shape = shape,
                ambientColor = if (style.glowColor != Color.Transparent) style.glowColor else Color.Black.copy(alpha = 0.22f),
                spotColor = if (style.glowColor != Color.Transparent) style.glowColor else Color.Black.copy(alpha = 0.28f)
            )
            .clip(shape)
            .background(style.background)
            .then(
                if (style.borderColor != null) {
                    Modifier.border(1.dp, style.borderColor, shape)
                } else {
                    Modifier.border(0.75.dp, Color.White.copy(alpha = 0.18f), shape)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle glossy diagonal sheen on high-value milestone tiles
        if (animEnabled && style.hasSheen) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val diag = size.width * sheenProgress
                val band = size.width * 0.42f
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.28f),
                            Color.Transparent
                        ),
                        start = Offset(diag - band, diag - band),
                        end = Offset(diag + band, diag + band)
                    )
                )
            }
        }

        Text(
            text = FaFormat.fmtTile(value, digitsMode),
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
