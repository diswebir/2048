package com.example.ui.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
private fun StyledModalDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(20.dp)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
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
                    .heightIn(max = 640.dp)
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
            // Theme segmented setting
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(stringResource(R.string.settings_theme_title), style = MaterialTheme.typography.titleSmall, color = colors.text)
                    Text(stringResource(R.string.settings_theme_desc), style = MaterialTheme.typography.bodySmall, color = colors.muted)
                }
                ThemeSegmentedControl(
                    selected = settings.theme,
                    onSelect = onThemeChange
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassButton(
                    text = stringResource(R.string.settings_reset_btn),
                    isDanger = true,
                    onClick = onRequestResetAll,
                    modifier = Modifier.testTag("settings_reset_button")
                )
                Text(
                    text = stringResource(R.string.settings_version),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted
                )
            }
        }
    }
}

@Composable
private fun ThemeSegmentedControl(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    val colors = LocalAppColors.current
    val options = listOf(
        ThemeMode.DARK to stringResource(R.string.theme_dark),
        ThemeMode.LIGHT to stringResource(R.string.theme_light),
        ThemeMode.AUTO to stringResource(R.string.theme_auto)
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.glass2)
            .border(1.dp, colors.line, RoundedCornerShape(12.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { (mode, label) ->
            val active = mode == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        if (active) Brush.linearGradient(listOf(colors.brand, colors.brand3))
                        else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { onSelect(mode) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .testTag("theme_opt_${mode.name.lowercase()}")
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (active) Color.White else colors.muted
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassButton(
                text = stringResource(R.string.records_clear_btn),
                isDanger = true,
                enabled = records.isNotEmpty(),
                onClick = onRequestClear,
                modifier = Modifier.testTag("clear_records_button")
            )
            GlassButton(
                text = stringResource(R.string.action_close),
                onClick = onDismiss,
                modifier = Modifier.testTag("records_close_button")
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
            .clip(RoundedCornerShape(99.dp))
            .background(colors.toastBg)
            .border(1.dp, colors.toastBorder, RoundedCornerShape(99.dp))
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

private data class ConfettiParticle(
    val xFraction: Float,
    val speed: Float,
    val drift: Float,
    val rotSpeed: Float,
    val color: Color,
    val delayFraction: Float
)

@Composable
fun ConfettiOverlay(
    triggerToken: Long,
    animEnabled: Boolean
) {
    if (triggerToken <= 0L || !animEnabled) return
    var active by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    val palette = remember {
        listOf(
            Color(0xFFFFD166),
            Color(0xFF7C5CFF),
            Color(0xFF22D3EE),
            Color(0xFFFF6B6B),
            Color(0xFF4ADE80),
            Color(0xFFF472B6),
            Color(0xFFFFAB3D)
        )
    }

    val particles = remember(triggerToken) {
        val rnd = Random(triggerToken)
        List(48) { idx ->
            ConfettiParticle(
                xFraction = rnd.nextFloat(),
                speed = 0.7f + rnd.nextFloat() * 0.6f,
                drift = (rnd.nextFloat() * 2f - 1f) * 140f,
                rotSpeed = (rnd.nextFloat() * 2f - 1f) * 540f,
                color = palette[idx % palette.size],
                delayFraction = rnd.nextFloat() * 0.15f
            )
        }
    }

    LaunchedEffect(triggerToken) {
        active = true
        progress = 0f
        val steps = 120
        val stepDelay = 2800L / steps
        for (i in 1..steps) {
            progress = i.toFloat() / steps
            delay(stepDelay)
        }
        active = false
    }

    if (!active) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (p in particles) {
            val localP = ((progress - p.delayFraction) / (1f - p.delayFraction)).coerceIn(0f, 1f)
            if (localP <= 0f) continue
            val x = p.xFraction * size.width + p.drift * localP
            val y = -30f + localP * p.speed * (size.height + 120f)
            val rot = p.rotSpeed * localP
            rotate(degrees = rot, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color.copy(alpha = (1f - localP * 0.35f).coerceIn(0f, 1f)),
                    topLeft = Offset(x - 10f, y - 18f),
                    size = Size(20f, 34f)
                )
            }
        }
    }
}
