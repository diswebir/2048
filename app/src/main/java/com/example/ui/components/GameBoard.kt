package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VazirmatnFontFamily
import com.example.ui.theme.tileStyleFor
import com.example.util.FaFormat
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt

private val SlideEasing = CubicBezierEasing(0.22f, 0.8f, 0.32f, 1f)
private val PopEasing = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1.25f)

@Composable
fun GameBoard(
    session: GameSession,
    digitsMode: DigitsMode,
    animEnabled: Boolean,
    ghosts: List<GhostTile>,
    ghostToken: Long,
    nudgeDir: Direction?,
    nudgeToken: Long,
    onSwipe: (Direction) -> Unit,
    onContinueAfterWin: () -> Unit,
    onNewGame: () -> Unit,
    onUndo: () -> Unit,
    canUndo: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val nudgeOffsetX = remember { Animatable(0f) }
    val nudgeOffsetY = remember { Animatable(0f) }

    LaunchedEffect(nudgeToken) {
        if (nudgeToken > 0L && nudgeDir != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            if (animEnabled) {
                val dist = with(density) { 8.dp.toPx() }
                val returnSpec = spring<Float>(dampingRatio = 0.55f, stiffness = 950f)
                when (nudgeDir) {
                    Direction.LEFT -> {
                        nudgeOffsetX.animateTo(-dist, tween(65))
                        nudgeOffsetX.animateTo(0f, returnSpec)
                    }
                    Direction.RIGHT -> {
                        nudgeOffsetX.animateTo(dist, tween(65))
                        nudgeOffsetX.animateTo(0f, returnSpec)
                    }
                    Direction.UP -> {
                        nudgeOffsetY.animateTo(-dist, tween(65))
                        nudgeOffsetY.animateTo(0f, returnSpec)
                    }
                    Direction.DOWN -> {
                        nudgeOffsetY.animateTo(dist, tween(65))
                        nudgeOffsetY.animateTo(0f, returnSpec)
                    }
                }
            }
        }
    }

    val boardShape = RoundedCornerShape(22.dp)
    val overlayVisible = session.over || session.showWin
    val swipeThresholdPx = with(density) { 18.dp.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .offset {
                IntOffset(nudgeOffsetX.value.roundToInt(), nudgeOffsetY.value.roundToInt())
            }
            .shadow(elevation = if (colors.isLight) 16.dp else 24.dp, shape = boardShape, clip = false)
            .clip(boardShape)
            .background(
                Brush.linearGradient(
                    listOf(colors.boardBgStart, colors.boardBgMid, colors.boardBgEnd)
                )
            )
            .border(1.dp, colors.lineStrong, boardShape)
            .testTag("game_board")
            .pointerInput(overlayVisible) {
                if (overlayVisible) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    val startPos = down.position
                    var triggered = false
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: event.changes.firstOrNull()
                        if (change == null || !change.pressed) {
                            break
                        }
                        if (!triggered) {
                            val dx = change.position.x - startPos.x
                            val dy = change.position.y - startPos.y
                            val dist = hypot(dx, dy)
                            if (dist >= swipeThresholdPx) {
                                val ax = abs(dx)
                                val ay = abs(dy)
                                if (ax > ay * 1.1f || ay > ax * 1.1f) {
                                    triggered = true
                                    change.consume()
                                    val dir = if (ax > ay) {
                                        if (dx > 0f) Direction.RIGHT else Direction.LEFT
                                    } else {
                                        if (dy > 0f) Direction.DOWN else Direction.UP
                                    }
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSwipe(dir)
                                }
                            }
                        } else {
                            change.consume()
                        }
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
            }
        }

        AnimatedVisibility(
            visible = overlayVisible,
            enter = if (animEnabled) fadeIn(tween(220)) + scaleIn(initialScale = 0.95f, animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f)) else fadeIn(snap()),
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
    val targetXPx = with(density) { (gap + (tileSize + gap) * tile.c).toPx().roundToInt() }
    val targetYPx = with(density) { (gap + (tileSize + gap) * tile.r).toPx().roundToInt() }

    val animatedOffset by animateIntOffsetAsState(
        targetValue = IntOffset(targetXPx, targetYPx),
        animationSpec = if (animEnabled) {
            spring(
                dampingRatio = 0.88f,
                stiffness = 860f,
                visibilityThreshold = IntOffset.VisibilityThreshold
            )
        } else {
            snap()
        },
        label = "tileOffset_${tile.id}"
    )

    val scaleAnim = remember { Animatable(if (animEnabled && (tile.spawned || tile.merged)) 0.25f else 1f) }
    val alphaAnim = remember { Animatable(if (animEnabled && (tile.spawned || tile.merged)) 0.1f else 1f) }

    LaunchedEffect(tile.id, tile.merged, animEnabled) {
        if (!animEnabled) {
            scaleAnim.snapTo(1f)
            alphaAnim.snapTo(1f)
            return@LaunchedEffect
        }
        if (tile.spawned) {
            alphaAnim.animateTo(1f, tween(130))
        } else if (tile.merged) {
            alphaAnim.animateTo(1f, tween(100))
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
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 750f)
            )
        } else if (tile.merged) {
            scaleAnim.animateTo(1.12f, tween(115, delayMillis = 45, easing = PopEasing))
            scaleAnim.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = 800f))
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
    modifier: Modifier = Modifier
) {
    val style = tileStyleFor(value, isLight)
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
                elevation = if (value >= 128) 12.dp else 4.dp,
                shape = shape,
                ambientColor = style.glowColor,
                spotColor = style.glowColor
            )
            .clip(shape)
            .background(style.background)
            .then(
                if (style.borderColor != null) {
                    Modifier.border(1.dp, style.borderColor, shape)
                } else {
                    Modifier.border(0.75.dp, Color.White.copy(alpha = if (isLight && value <= 4) 0.7f else 0.14f), shape)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = FaFormat.fmtTile(value, digitsMode),
            color = style.textColor,
            fontFamily = VazirmatnFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSizeSp,
            lineHeight = fontSizeSp,
            maxLines = 1
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
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(text = "🏆", fontSize = 38.sp)
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
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(text = "🧩", fontSize = 38.sp)
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
