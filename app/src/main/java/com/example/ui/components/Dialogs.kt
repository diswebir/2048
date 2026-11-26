package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.DigitsMode
import com.example.model.GameRecord
import com.example.model.GameSettings
import com.example.model.ThemeMode
import com.example.ui.theme.LocalAppColors
import com.example.util.FaFormat
import kotlin.math.sin
import kotlin.random.Random

@Composable
private fun StyledModalDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(22.dp)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                        .heightIn(max = 660.dp)
                        .clip(shape)
                        .background(Brush.linearGradient(listOf(colors.modalBgStart, colors.modalBgEnd)))
                        .border(1.dp, colors.lineStrong, shape)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.ExtraBold),
                            color = colors.text
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.glass2)
                                .border(1.dp, colors.line, RoundedCornerShape(12.dp))
                                .clickable(onClick = onDismiss)
                                .testTag("modal_close_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.action_close),
                                tint = colors.muted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    settings: GameSettings,
    onThemeChange: (ThemeMode) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onDigitsChange: (Boolean) -> Unit,
    onAnimChange: (Boolean) -> Unit,
    onRequestResetAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    StyledModalDialog(title = stringResource(R.string.settings_title), onDismiss = onDismiss) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // Theme segmented setting stacked cleanly so compact phones never crush labels
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column {
                    Text(stringResource(R.string.settings_theme_title), style = MaterialTheme.typography.titleSmall, color = colors.text)
                    Text(stringResource(R.string.settings_theme_desc), style = MaterialTheme.typography.bodySmall, color = colors.muted)
                }
                ThemeSegmentedControl(
                    selected = settings.theme,
                    onSelect = onThemeChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(color = colors.line)

            SettingSwitchRow(
                title = stringResource(R.string.settings_sound_title),
                subtitle = stringResource(R.string.settings_sound_desc),
                checked = settings.sound,
                onCheckedChange = onSoundChange,
                testTag = "setting_sound_switch"
            )

            HorizontalDivider(color = colors.line)

            SettingSwitchRow(
                title = stringResource(R.string.settings_digits_title),
                subtitle = stringResource(R.string.settings_digits_desc),
                checked = settings.digits == DigitsMode.FA,
                onCheckedChange = onDigitsChange,
                testTag = "setting_digits_switch"
            )

            HorizontalDivider(color = colors.line)

            SettingSwitchRow(
                title = stringResource(R.string.settings_anim_title),
                subtitle = stringResource(R.string.settings_anim_desc),
                checked = settings.anim,
                onCheckedChange = onAnimChange,
                testTag = "setting_anim_switch"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlassButton(
                text = stringResource(R.string.settings_reset_btn),
                isDanger = true,
                onClick = onRequestResetAll,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_reset_button")
            )

            Spacer(modifier = Modifier.height(14.dp))

            val context = LocalContext.current
            val telegramUrl = stringResource(R.string.creator_url)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.glass2)
                    .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                    .clickable {
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl)))
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_version),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted,
                    textAlign = TextAlign.Center
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.creator_credit),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.text
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.muted
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
    }
}

@Composable
private fun ThemeSegmentedControl(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val options = listOf(
        ThemeMode.LIGHT to stringResource(R.string.theme_light),
        ThemeMode.DARK to stringResource(R.string.theme_dark),
        ThemeMode.AUTO to stringResource(R.string.theme_auto)
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.glass2)
            .border(1.dp, colors.line, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { (mode, label) ->
            val active = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        if (active) Brush.linearGradient(listOf(colors.brand, colors.brand3))
                        else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { onSelect(mode) }
                    .padding(horizontal = 10.dp, vertical = 9.dp)
                    .testTag("theme_opt_${mode.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (active) Color.White else colors.muted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = colors.text)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.muted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.brand,
                uncheckedThumbColor = colors.text,
                uncheckedTrackColor = colors.glass2,
                uncheckedBorderColor = colors.lineStrong
            )
        )
    }
}

@Composable
fun HelpDialog(
    digitsMode: DigitsMode,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    val rules = listOf(
        stringResource(R.string.help_rule_1_title) to stringResource(R.string.help_rule_1_body),
        stringResource(R.string.help_rule_2_title) to stringResource(R.string.help_rule_2_body),
        stringResource(R.string.help_rule_3_title) to stringResource(R.string.help_rule_3_body),
        stringResource(R.string.help_rule_4_title) to stringResource(R.string.help_rule_4_body)
    )
    val shortcuts = listOf(
        stringResource(R.string.shortcut_move) to "↑ ← ↓ → / WASD",
        stringResource(R.string.shortcut_undo) to "U",
        stringResource(R.string.shortcut_new) to "N",
        stringResource(R.string.shortcut_sound) to "M",
        stringResource(R.string.shortcut_close) to "Esc"
    )

    StyledModalDialog(title = stringResource(R.string.help_title), onDismiss = onDismiss) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rules.forEachIndexed { index, (head, body) ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(22.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(colors.glass2)
                                .border(1.dp, colors.line, RoundedCornerShape(7.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = FaFormat.fmt(index + 1, digitsMode),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.brand2
                            )
                        }
                        Text(
                            text = "$head $body",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.muted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.help_shortcuts_title),
                style = MaterialTheme.typography.titleSmall,
                color = colors.muted
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                shortcuts.forEach { (label, keyLabel) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(11.dp))
                            .background(colors.glass2)
                            .border(1.dp, colors.line, RoundedCornerShape(11.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = colors.muted)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.kbdBg)
                                .border(1.dp, colors.line, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = keyLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.text
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            PrimaryGradientButton(
                text = stringResource(R.string.action_got_it),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().testTag("help_confirm_button")
            )
        }
    }
}

@Composable
fun AllRecordsDialog(
    records: List<GameRecord>,
    digitsMode: DigitsMode,
    onRequestClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    StyledModalDialog(title = stringResource(R.string.records_all_title), onDismiss = onDismiss) {
        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.lineStrong, RoundedCornerShape(12.dp))
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.records_empty_modal),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(records) { index, rec ->
                    RecordRow(rank = index + 1, record = rec, digitsMode = digitsMode)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassButton(
                text = stringResource(R.string.records_clear_btn),
                isDanger = true,
                enabled = records.isNotEmpty(),
                onClick = onRequestClear,
                modifier = Modifier.weight(1f).testTag("clear_records_button")
            )
            GlassButton(
                text = stringResource(R.string.action_close),
                onClick = onDismiss,
                modifier = Modifier.weight(1f).testTag("records_close_button")
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    confirmText: String,
    cancelText: String,
    isDanger: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    StyledModalDialog(title = title, onDismiss = onDismiss) {
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.muted
        )
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isDanger) {
                GlassButton(
                    text = confirmText,
                    isDanger = true,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f).testTag("confirm_ok_button")
                )
            } else {
                PrimaryGradientButton(
                    text = confirmText,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f).testTag("confirm_ok_button")
                )
            }
            GlassButton(
                text = cancelText,
                onClick = onDismiss,
                modifier = Modifier.weight(1f).testTag("confirm_cancel_button")
            )
        }
    }
}

@Composable
fun ToastPill(
    text: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.toastBg)
            .border(1.dp, colors.toastBorder, CircleShape)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.toastText,
            textAlign = TextAlign.Center
        )
    }
}

private enum class ConfettiShape { RIBBON, CIRCLE, DIAMOND }

private data class ConfettiParticle(
    val xFraction: Float,
    val speed: Float,
    val drift: Float,
    val wobbleFreq: Float,
    val wobbleAmp: Float,
    val rotSpeed: Float,
    val color: Color,
    val delayFraction: Float,
    val shape: ConfettiShape,
    val scale: Float
)

@Composable
fun ConfettiOverlay(
    triggerToken: Long,
    animEnabled: Boolean
) {
    if (triggerToken <= 0L || !animEnabled) return
    var active by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val palette = remember {
        listOf(
            Color(0xFFF6E7D4),
            Color(0xFFE7C49A),
            Color(0xFFCF9A6E),
            Color(0xFFB57E56),
            Color(0xFF8E5D3E),
            Color(0xFFDDB792),
            Color(0xFF6B432C)
        )
    }

    val particles = remember(triggerToken) {
        val rnd = Random(triggerToken)
        val shapes = ConfettiShape.entries
        List(72) { idx ->
            ConfettiParticle(
                xFraction = rnd.nextFloat(),
                speed = 0.65f + rnd.nextFloat() * 0.65f,
                drift = (rnd.nextFloat() * 2f - 1f) * 140f,
                wobbleFreq = 4f + rnd.nextFloat() * 6f,
                wobbleAmp = 14f + rnd.nextFloat() * 24f,
                rotSpeed = (rnd.nextFloat() * 2f - 1f) * 720f,
                color = palette[idx % palette.size],
                delayFraction = rnd.nextFloat() * 0.22f,
                shape = shapes[idx % shapes.size],
                scale = 0.7f + rnd.nextFloat() * 0.65f
            )
        }
    }

    LaunchedEffect(triggerToken) {
        active = true
        progress = 0f
        val durationNanos = 3_200_000_000L
        val start = withFrameNanos { it }
        while (true) {
            val now = withFrameNanos { it }
            val p = ((now - start).toFloat() / durationNanos).coerceIn(0f, 1f)
            progress = p
            if (p >= 1f) break
        }
        active = false
    }

    if (!active) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (p in particles) {
            val localP = ((progress - p.delayFraction) / (1f - p.delayFraction)).coerceIn(0f, 1f)
            if (localP <= 0f) continue
            val gravityY = localP * (0.45f + 0.55f * localP)
            val wobble = sin(localP * p.wobbleFreq) * p.wobbleAmp
            val x = p.xFraction * size.width + p.drift * localP + wobble
            val y = -36f + gravityY * p.speed * (size.height + 140f)
            val rot = p.rotSpeed * localP
            val alpha = if (localP > 0.75f) ((1f - localP) / 0.25f).coerceIn(0f, 1f) else 0.96f
            val c = p.color.copy(alpha = alpha)

            rotate(degrees = rot, pivot = Offset(x, y)) {
                when (p.shape) {
                    ConfettiShape.RIBBON -> drawRect(
                        color = c,
                        topLeft = Offset(x - 9f * p.scale, y - 18f * p.scale),
                        size = Size(18f * p.scale, 36f * p.scale)
                    )
                    ConfettiShape.CIRCLE -> drawCircle(
                        color = c,
                        radius = 10f * p.scale,
                        center = Offset(x, y)
                    )
                    ConfettiShape.DIAMOND -> {
                        val r = 13f * p.scale
                        val path = Path().apply {
                            moveTo(x, y - r)
                            lineTo(x + r * 0.75f, y)
                            lineTo(x, y + r)
                            lineTo(x - r * 0.75f, y)
                            close()
                        }
                        drawPath(path = path, color = c)
                    }
                }
            }
        }
    }
}
