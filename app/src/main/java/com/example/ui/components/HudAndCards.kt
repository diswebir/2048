package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.GameEngine
import com.example.model.DigitsMode
import com.example.model.GameRecord
import com.example.model.GameStats
import com.example.ui.theme.LocalAppColors
import com.example.util.FaFormat
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun HeaderBar(
    score: Int,
    best: Int,
    digitsMode: DigitsMode,
    animEnabled: Boolean,
    scoreGain: Int,
    scoreGainToken: Long,
    bestPulseToken: Long,
    compactHeader: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compactHeader) 10.dp else 12.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            BrandMark(size = if (compactHeader) 46.dp else 56.dp)
            Column {
                Text(
                    text = FaFormat.toDigits("2048", digitsMode),
                    style = if (compactHeader) {
                        MaterialTheme.typography.headlineSmall.copy(fontSize = 21.sp)
                    } else {
                        MaterialTheme.typography.displaySmall
                    },
                    color = colors.text,
                    maxLines = 1,
                    softWrap = false
                )
                if (!compactHeader) {
                    Text(
                        text = stringResource(R.string.brand_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.muted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(if (compactHeader) 8.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreCard(
                label = stringResource(R.string.label_score),
                value = score,
                isBest = false,
                animEnabled = animEnabled,
                pulseToken = scoreGainToken,
                gainAmount = scoreGain,
                gainToken = scoreGainToken,
                digitsMode = digitsMode,
                compact = compactHeader,
                modifier = Modifier.testTag("score_card")
            )
            ScoreCard(
                label = stringResource(R.string.label_best),
                value = best,
                isBest = true,
                animEnabled = animEnabled,
                pulseToken = bestPulseToken,
                gainAmount = 0,
                gainToken = 0L,
                digitsMode = digitsMode,
                compact = compactHeader,
                modifier = Modifier.testTag("best_card")
            )
        }
    }
}

@Composable
fun BrandMark(
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val cornerRadius = if (size <= 48.dp) 15.dp else if (size <= 64.dp) 19.dp else 28.dp
    val shape = RoundedCornerShape(cornerRadius)
    val innerPad = (size.value * 0.11f).dp
    val cellGap = (size.value * 0.06f).dp
    val cellRadius = (size.value * 0.09f).dp
    Box(
        modifier = modifier
            .size(size)
            .shadow(14.dp, shape, ambientColor = colors.brand, spotColor = colors.brand2)
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.brand, colors.brand3, colors.brand2)))
            .border(1.dp, Color.White.copy(alpha = 0.44f), shape)
            .padding(innerPad)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(cellGap), modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(cellGap), modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(cellRadius)).background(Color.White.copy(alpha = 0.94f)))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(cellRadius)).background(Color.White.copy(alpha = 0.54f)))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(cellGap), modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(cellRadius)).background(Color.White.copy(alpha = 0.74f)))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(cellRadius)).background(Color.White.copy(alpha = 0.36f)))
            }
        }
    }
}

@Composable
private fun ScoreCard(
    label: String,
    value: Int,
    isBest: Boolean,
    animEnabled: Boolean,
    pulseToken: Long,
    gainAmount: Int,
    gainToken: Long,
    digitsMode: DigitsMode,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val pulseScale = remember { Animatable(1f) }
    val gainProgress = remember { Animatable(0f) }
    var visibleGain by remember { mutableStateOf<Int?>(null) }

    val displayedValue by animateIntAsState(
        targetValue = value,
        animationSpec = if (animEnabled) tween(durationMillis = 240) else snap(),
        label = "scoreRoll"
    )
    val valueText = FaFormat.fmt(displayedValue, digitsMode)
    val valueFontSize = when {
        valueText.length >= 7 -> if (compact) 14.sp else 16.sp
        valueText.length >= 5 -> if (compact) 16.sp else 18.sp
        else -> if (compact) 18.sp else 20.sp
    }

    LaunchedEffect(pulseToken) {
        if (pulseToken > 0L && animEnabled) {
            pulseScale.snapTo(1f)
            pulseScale.animateTo(1.08f, tween(130))
            pulseScale.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = 700f))
        }
    }

    LaunchedEffect(gainToken) {
        if (gainToken > 0L && gainAmount > 0 && animEnabled) {
            visibleGain = gainAmount
            gainProgress.snapTo(0f)
            gainProgress.animateTo(1f, tween(780))
            visibleGain = null
        }
    }

    val shape = RoundedCornerShape(16.dp)
    val bgBrush = if (isBest) {
        Brush.linearGradient(listOf(colors.goldBgStart, colors.glass))
    } else {
        Brush.linearGradient(listOf(colors.glass, colors.glass))
    }
    val borderColor = if (isBest) colors.goldBorder else colors.line

    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .defaultMinSize(minWidth = if (compact) 78.dp else 94.dp)
                .graphicsLayer {
                    scaleX = pulseScale.value
                    scaleY = pulseScale.value
                }
                .clip(shape)
                .background(bgBrush)
                .border(1.dp, borderColor, shape)
                .padding(horizontal = if (compact) 10.dp else 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isBest) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = stringResource(R.string.cd_trophy),
                        tint = colors.gold,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted,
                    maxLines = 1,
                    softWrap = false
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valueText,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = valueFontSize),
                color = if (isBest) colors.goldText else colors.text,
                maxLines = 1,
                softWrap = false
            )
        }

        visibleGain?.let { amt ->
            val p = gainProgress.value
            val alpha = if (p < 0.25f) p / 0.25f else (1f - (p - 0.25f) / 0.75f).coerceIn(0f, 1f)
            val yOffset = (-8 - (p * 32)).roundToInt()
            Text(
                text = "+${FaFormat.fmt(amt, digitsMode)}",
                color = colors.good,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .offset { IntOffset(0, yOffset) }
                    .alpha(alpha)
            )
        }
    }
}

@Composable
fun GameToolbar(
    undoCount: Int,
    soundEnabled: Boolean,
    digitsMode: DigitsMode,
    onNewGame: () -> Unit,
    onUndo: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrimaryGradientButton(
            text = stringResource(R.string.action_new_game),
            icon = Icons.Rounded.Add,
            onClick = onNewGame,
            modifier = Modifier.weight(1f).testTag("new_game_button")
        )

        Box {
            ToolbarIconButton(
                icon = Icons.AutoMirrored.Rounded.Undo,
                contentDescription = stringResource(R.string.cd_undo),
                enabled = undoCount > 0,
                onClick = onUndo,
                modifier = Modifier.testTag("undo_button")
            )
            if (undoCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-3).dp, y = (-3).dp)
                        .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(colors.brand, colors.brand3)))
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = FaFormat.fmt(undoCount, digitsMode),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }

        ToolbarIconButton(
            icon = if (soundEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
            contentDescription = stringResource(if (soundEnabled) R.string.cd_sound_on else R.string.cd_sound_off),
            tint = if (soundEnabled) colors.text else colors.muted,
            onClick = onToggleSound,
            modifier = Modifier.testTag("sound_button")
        )

        ToolbarIconButton(
            icon = Icons.Rounded.Settings,
            contentDescription = stringResource(R.string.cd_settings),
            onClick = onOpenSettings,
            modifier = Modifier.testTag("settings_button")
        )

        ToolbarIconButton(
            icon = Icons.AutoMirrored.Rounded.HelpOutline,
            contentDescription = stringResource(R.string.cd_help),
            onClick = onOpenHelp,
            modifier = Modifier.testTag("help_button")
        )
    }
}

@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: androidx.compose.ui.unit.Dp = 48.dp
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 900f),
        label = "primaryBtnScale"
    )
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .height(height)
            .shadow(10.dp, shape, ambientColor = colors.brand3, spotColor = colors.brand3)
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.brand, colors.brand3)))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isDanger: Boolean = false,
    height: androidx.compose.ui.unit.Dp = 48.dp
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(14.dp)
    val bg = if (isDanger) colors.dangerBg else colors.glass2
    val border = if (isDanger) colors.dangerBorder else colors.line
    val fg = if (isDanger) colors.dangerText else colors.text
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.965f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 900f),
        label = "glassBtnScale"
    )

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .height(height)
            .alpha(if (enabled) 1f else 0.42f)
            .clip(shape)
            .background(bg)
            .border(1.dp, border, shape)
            .clickable(enabled = enabled, interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = fg,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun ToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = null
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 900f),
        label = "iconBtnScale"
    )
    Box(
        modifier = modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .background(colors.glass2)
            .border(1.dp, colors.line, shape)
            .clickable(enabled = enabled, interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint ?: colors.muted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun GlassCard(
    title: String,
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.glass)
            .border(1.dp, colors.line, shape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.muted
            )
            trailingAction?.invoke()
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun StatusCard(
    maxTile: Int,
    moves: Int,
    elapsedSeconds: Int,
    stats: GameStats,
    digitsMode: DigitsMode,
    animEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val targetProgress = ((ln(max(2, maxTile).toDouble()) / ln(2.0)) / 11.0).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = if (animEnabled) spring(dampingRatio = 0.85f, stiffness = 300f) else snap(),
        label = "milestoneProgress"
    )
    val sheenTransition = rememberInfiniteTransition(label = "barSheen")
    val barSheen by sheenTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "barSheenX"
    )

    GlassCard(title = stringResource(R.string.section_status), modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatCell(
                    label = stringResource(R.string.stat_best_tile),
                    value = FaFormat.fmtTile(maxTile, digitsMode),
                    modifier = Modifier.weight(1f)
                )
                StatCell(
                    label = stringResource(R.string.stat_moves),
                    value = FaFormat.fmt(moves, digitsMode),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatCell(
                    label = stringResource(R.string.stat_time),
                    value = FaFormat.fmtTime(elapsedSeconds, digitsMode),
                    modifier = Modifier.weight(1f)
                )
                StatCell(
                    label = stringResource(R.string.stat_games),
                    value = FaFormat.fmt(stats.games, digitsMode),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.progress_path_prefix),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted
                )
                Text(
                    text = FaFormat.fmtTile(GameEngine.TARGET, digitsMode),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.goldText
                )
            }
            Text(
                text = "${FaFormat.fmtTile(maxTile, digitsMode)} / ${FaFormat.fmtTile(GameEngine.TARGET, digitsMode)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(CircleShape)
                .background(colors.cell)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(colors.brand2, colors.brand, colors.gold)))
            ) {
                if (animEnabled) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width * barSheen
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.42f), Color.Transparent),
                                startX = cx - size.height * 4f,
                                endX = cx + size.height * 4f
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.glass2)
            .border(1.dp, colors.line, shape)
            .padding(horizontal = 11.dp, vertical = 9.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = colors.muted,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.ExtraBold),
            color = colors.text,
            maxLines = 1
        )
    }
}

@Composable
fun ControlsCard(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    GlassCard(title = stringResource(R.string.section_controls), modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GlassButton(
                text = stringResource(R.string.action_undo),
                icon = Icons.AutoMirrored.Rounded.Undo,
                enabled = canUndo,
                onClick = onUndo,
                modifier = Modifier.weight(1f).testTag("card_undo_button")
            )
            GlassButton(
                text = stringResource(R.string.action_new_game),
                icon = Icons.Rounded.Add,
                onClick = onNewGame,
                modifier = Modifier.weight(1f).testTag("card_new_game_button")
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.controls_note),
            style = MaterialTheme.typography.labelSmall,
            color = colors.muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RecordsCard(
    records: List<GameRecord>,
    digitsMode: DigitsMode,
    onOpenAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    GlassCard(
        title = stringResource(R.string.section_records),
        modifier = modifier,
        trailingAction = if (records.isNotEmpty()) {
            {
                Text(
                    text = stringResource(R.string.action_all),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.brand2,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onOpenAll)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("all_records_button")
                )
            }
        } else null
    ) {
        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.glass2)
                    .border(1.dp, colors.lineStrong, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.records_empty_card),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                records.take(5).forEachIndexed { index, rec ->
                    RecordRow(rank = index + 1, record = rec, digitsMode = digitsMode)
                }
            }
        }
    }
}

@Composable
fun RecordRow(
    rank: Int,
    record: GameRecord,
    digitsMode: DigitsMode
) {
    val colors = LocalAppColors.current
    val isTop = rank <= 3
    val rankShape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(rankShape)
                .background(
                    if (isTop) {
                        Brush.linearGradient(listOf(colors.goldBgStart, colors.glass2))
                    } else {
                        Brush.linearGradient(listOf(colors.glass2, colors.glass2))
                    }
                )
                .border(1.dp, if (isTop) colors.goldBorder else colors.line, rankShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = FaFormat.fmt(rank, digitsMode),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isTop) colors.goldText else colors.muted
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = FaFormat.fmt(record.score, digitsMode),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold),
                color = colors.text,
                maxLines = 1
            )
            Text(
                text = stringResource(
                    R.string.record_meta_format,
                    FaFormat.fmtTile(record.tile, digitsMode),
                    FaFormat.fmt(record.moves, digitsMode),
                    FaFormat.fmtTime(record.seconds, digitsMode)
                ),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                color = colors.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = FaFormat.formatDate(record.dateMillis, digitsMode),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            color = colors.muted.copy(alpha = 0.85f),
            maxLines = 1
        )
    }
}

@Composable
fun QuickTipsCard(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val tips = listOf(
        stringResource(R.string.tip_1),
        stringResource(R.string.tip_2),
        stringResource(R.string.tip_3),
        stringResource(R.string.tip_4)
    )
    GlassCard(title = stringResource(R.string.section_quick_tips), modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tips.forEach { tip ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(colors.brand2, colors.brand)))
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.muted
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyFooter(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val telegramUrl = stringResource(R.string.creator_url)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = stringResource(R.string.cd_lock),
                tint = colors.muted.copy(alpha = 0.75f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.footer_privacy),
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.glass)
                .border(1.dp, colors.line, CircleShape)
                .clickable {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl)))
                    }
                }
                .padding(horizontal = 14.dp, vertical = 7.dp)
                .testTag("creator_credit_link"),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.creator_credit),
                style = MaterialTheme.typography.labelSmall,
                color = colors.muted
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.labelSmall,
                color = colors.muted.copy(alpha = 0.5f)
            )
            Text(
                text = stringResource(R.string.creator_handle),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.brand2
            )
        }
    }
}
