package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterHpChangeImpact
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterDamage
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterHealing
import io.github.mrsimkin.dndcustomaid.shared.character.characterHpChangeImpact
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterUnsignedIntegerInput
import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterTemporaryHp
import kotlinx.coroutines.delay

private enum class CharacterHpExactEditorV4 {
    HIT_POINTS,
    TEMP_HP,
}

/**
 * Compact persistent combat HUD (P2/P5/P16).
 *
 * Damage/healing is a high-frequency inline operation: one shared amount field, immediate apply,
 * then clear. Exact administrative HP edits remain secondary and are reached by tapping the HP
 * or temporary-HP metric.
 */
@Composable
internal fun CharacterCombatOperationalCardV4(
    armorClass: String,
    initiative: String,
    speed: String,
    sheet: CharacterSheet,
    onSheetChange: (CharacterSheet) -> Unit,
    hapticsEnabled: Boolean,
) {
    var amountText by rememberSaveable { mutableStateOf("") }
    var exactEditor by rememberSaveable { mutableStateOf<String?>(null) }
    var hpFeedback by remember { mutableStateOf(CharacterHpChangeImpact.NONE) }
    var hpFeedbackEpoch by remember { mutableIntStateOf(0) }
    val amount = amountText.toIntOrNull()
    val validAmount = amount != null && amount > 0
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val layoutContext = characterLayoutContextV4()

    fun triggerHpFeedback(updated: CharacterSheet) {
        hpFeedback = characterHpChangeImpact(sheet, updated)
        if (hpFeedback != CharacterHpChangeImpact.NONE) hpFeedbackEpoch += 1
    }

    LaunchedEffect(hpFeedbackEpoch) {
        if (hpFeedbackEpoch > 0) {
            delay(420)
            hpFeedback = CharacterHpChangeImpact.NONE
        }
    }

    fun applyOperational(updated: CharacterSheet) {
        if (updated != sheet) {
            triggerHpFeedback(updated)
            haptic(CharacterHapticEventV4.RESOURCE)
            onSheetChange(updated)
        }
        amountText = ""
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val singleMetricRow = maxWidth >= 620.dp &&
                layoutContext.verticalSpace != CharacterVerticalSpaceV4.COMFORTABLE
            val hitPointsHighlighted = hpFeedback == CharacterHpChangeImpact.HIT_POINTS ||
                hpFeedback == CharacterHpChangeImpact.BOTH
            val temporaryHpHighlighted = hpFeedback == CharacterHpChangeImpact.TEMPORARY_HP ||
                hpFeedback == CharacterHpChangeImpact.BOTH
            val controlHeight = characterCompactSingleLineFieldHeightV4()

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                if (singleMetricRow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OperationalInlineMetricV4("CA", armorClass)
                        OperationalInlineMetricV4("Inic.", initiative.ifBlank { "—" })
                        OperationalInlineMetricV4("Vel.", formatCharacterDistanceFeetV4(speed))
                        OperationalInlineMetricV4(
                            "PV",
                            "${sheet.currentHp}/${sheet.maxHp}",
                            modifier = Modifier.clickable { exactEditor = CharacterHpExactEditorV4.HIT_POINTS.name },
                            highlighted = hitPointsHighlighted,
                        )
                        OperationalInlineMetricV4(
                            "Temp.",
                            sheet.tempHp.toString(),
                            modifier = Modifier.clickable { exactEditor = CharacterHpExactEditorV4.TEMP_HP.name },
                            highlighted = temporaryHpHighlighted,
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OperationalInlineMetricV4("CA", armorClass, Modifier.weight(1f))
                        OperationalInlineMetricV4("Inic.", initiative.ifBlank { "—" }, Modifier.weight(1f))
                        OperationalInlineMetricV4("Vel.", formatCharacterDistanceFeetV4(speed), Modifier.weight(1.45f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OperationalInlineMetricV4(
                            "PV",
                            "${sheet.currentHp}/${sheet.maxHp}",
                            modifier = Modifier.weight(1f).clickable {
                                exactEditor = CharacterHpExactEditorV4.HIT_POINTS.name
                            },
                            highlighted = hitPointsHighlighted,
                        )
                        OperationalInlineMetricV4(
                            "Temp.",
                            sheet.tempHp.toString(),
                            modifier = Modifier.weight(1f).clickable {
                                exactEditor = CharacterHpExactEditorV4.TEMP_HP.name
                            },
                            highlighted = temporaryHpHighlighted,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = { amount?.let { applyOperational(applyCharacterDamage(sheet, it)) } },
                        enabled = validAmount && (sheet.currentHp > 0 || sheet.tempHp > 0),
                        modifier = Modifier.weight(1f).heightIn(min = controlHeight),
                    ) { Text("Daño") }
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = normalizeCharacterUnsignedIntegerInput(it) },
                        modifier = Modifier.weight(0.72f).heightIn(min = controlHeight),
                        label = { Text("Cantidad") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    TextButton(
                        onClick = { amount?.let { applyOperational(applyCharacterHealing(sheet, it)) } },
                        enabled = validAmount && sheet.currentHp < sheet.maxHp,
                        modifier = Modifier.weight(1f).heightIn(min = controlHeight),
                    ) { Text("Curar") }
                }
            }
        }
    }

    when (exactEditor?.let { runCatching { CharacterHpExactEditorV4.valueOf(it) }.getOrNull() }) {
        CharacterHpExactEditorV4.HIT_POINTS -> CharacterExactHitPointsEditorV4(
            sheet = sheet,
            onDismiss = { exactEditor = null },
            onApply = { currentHp, maxHp ->
                val normalizedMax = maxHp.coerceAtLeast(0)
                val normalizedCurrent = currentHp.coerceIn(0, normalizedMax)
                val updated = sheet.copy(currentHp = normalizedCurrent, maxHp = normalizedMax)
                if (updated != sheet) {
                    triggerHpFeedback(updated)
                    haptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(updated)
                }
                exactEditor = null
            },
        )
        CharacterHpExactEditorV4.TEMP_HP -> CharacterExactTemporaryHpEditorV4(
            sheet = sheet,
            onDismiss = { exactEditor = null },
            onApply = { value ->
                val updated = setCharacterTemporaryHp(sheet, value.coerceAtLeast(0))
                if (updated != sheet) {
                    triggerHpFeedback(updated)
                    haptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(updated)
                }
                exactEditor = null
            },
        )
        null -> Unit
    }
}

@Composable
private fun OperationalInlineMetricV4(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val feedbackColor by animateColorAsState(
        targetValue = if (highlighted) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 140),
        label = "combat-hp-feedback",
    )
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = feedbackColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
    }
}

@Composable
private fun CharacterExactHitPointsEditorV4(
    sheet: CharacterSheet,
    onDismiss: () -> Unit,
    onApply: (currentHp: Int, maxHp: Int) -> Unit,
) {
    var currentText by rememberSaveable(sheet.currentHp) { mutableStateOf(sheet.currentHp.toString()) }
    var maxText by rememberSaveable(sheet.maxHp) { mutableStateOf(sheet.maxHp.toString()) }
    val current = currentText.toIntOrNull()
    val max = maxText.toIntOrNull()
    val valid = current != null && current >= 0 && max != null && max >= 0

    CharacterImeSafeEditorDialog(
        title = "Establecer puntos de vida",
        onCancel = onDismiss,
        onSave = { if (valid) onApply(requireNotNull(current), requireNotNull(max)) },
        saveEnabled = valid,
    ) {
        CharacterCompactFieldRowV4(
            first = { modifier ->
                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = normalizeCharacterUnsignedIntegerInput(it) },
                    modifier = modifier,
                    label = { Text("PV actuales") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            },
            second = { modifier ->
                OutlinedTextField(
                    value = maxText,
                    onValueChange = { maxText = normalizeCharacterUnsignedIntegerInput(it) },
                    modifier = modifier,
                    label = { Text("PV máximos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            },
        )
        CharacterInlineValidationMessage(
            if (currentText.isNotBlank() && maxText.isNotBlank() && !valid) {
                "Usa valores enteros iguales o mayores que 0."
            } else null,
        )
    }
}

@Composable
private fun CharacterExactTemporaryHpEditorV4(
    sheet: CharacterSheet,
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit,
) {
    var valueText by rememberSaveable(sheet.tempHp) { mutableStateOf(sheet.tempHp.toString()) }
    val value = valueText.toIntOrNull()
    val valid = value != null && value >= 0

    CharacterImeSafeEditorDialog(
        title = "Establecer PV temporales",
        onCancel = onDismiss,
        onSave = { if (valid) onApply(requireNotNull(value)) },
        saveEnabled = valid,
    ) {
        OutlinedTextField(
            value = valueText,
            onValueChange = { valueText = normalizeCharacterUnsignedIntegerInput(it) },
            label = { Text("PV temporales") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        CharacterInlineValidationMessage(
            if (valueText.isNotBlank() && !valid) "Usa un entero igual o mayor que 0." else null,
        )
    }
}

/** Death saves are deliberately scrollable content, not part of the persistent Combat HUD (P5). */
@Composable
internal fun CharacterCombatDeathSavesSectionV4(
    sheet: CharacterSheet,
    onSheetChange: (CharacterSheet) -> Unit,
    hapticsEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (sheet.currentHp > 0) return
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(appSpacingV4(7.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Text("Salvaciones de muerte", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DeathSaveCounterV4(
                    label = "Éxitos",
                    value = sheet.deathSaveSuccesses,
                    onChange = { value ->
                        haptic(CharacterHapticEventV4.RESOURCE)
                        onSheetChange(sheet.copy(deathSaveSuccesses = value.coerceIn(0, 3)))
                    },
                    modifier = Modifier.weight(1f),
                )
                DeathSaveCounterV4(
                    label = "Fallos",
                    value = sheet.deathSaveFailures,
                    onChange = { value ->
                        haptic(CharacterHapticEventV4.RESOURCE)
                        onSheetChange(sheet.copy(deathSaveFailures = value.coerceIn(0, 3)))
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DeathSaveCounterV4(
    label: String,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Row(
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { onChange(value - 1) }, enabled = value > 0) { Text("−") }
            Text("${value.coerceIn(0, 3)}/3", style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = { onChange(value + 1) }, enabled = value < 3) { Text("+") }
        }
    }
}
