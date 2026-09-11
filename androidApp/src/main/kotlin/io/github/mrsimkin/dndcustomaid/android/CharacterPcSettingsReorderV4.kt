package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheetTabKey

/** P6/P10: tab order uses the same whole-row long-press drag grammar as Player collections. */
@Composable
internal fun CharacterTabOrderDragSettingsV4(
    hapticsEnabled: Boolean,
) {
    val context = LocalCharacterPcSettingsContextV4.current ?: return
    val order = context.successorState.preferences.tabOrder
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
    ) {
        Text(
            "Mantén pulsada una fila y arrástrala para cambiar su posición.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        order.forEachIndexed { index, key ->
            var dragState by remember(key) { mutableStateOf(CharacterDragVisualStateV4()) }
            CharacterDropIndicatorV4(visible = dragState.showDropBefore)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .characterMeasuredReorderDragV4(
                        enabled = true,
                        onHaptic = haptic,
                        onMove = { offset ->
                            val currentOrder = context.successorState.preferences.tabOrder
                            val currentIndex = currentOrder.indexOf(key)
                            val target = currentIndex + offset
                            if (currentIndex !in currentOrder.indices || target !in currentOrder.indices) {
                                false
                            } else {
                                val changed = currentOrder.toMutableList()
                                val item = changed.removeAt(currentIndex)
                                changed.add(target, item)
                                context.onSuccessorStateChange(
                                    context.successorState.copy(
                                        preferences = context.successorState.preferences.copy(tabOrder = changed),
                                    ),
                                )
                                true
                            }
                        },
                        onVisualStateChange = { dragState = it },
                    )
                    .characterDragFeedbackV4(dragState),
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (dragState.active) 2.dp else 1.dp,
                    color = if (dragState.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                ),
                tonalElevation = if (dragState.active) 3.dp else 0.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = appSpacingV4(8.dp),
                        vertical = appSpacingV4(7.dp),
                    ),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        (index + 1).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(characterTabOrderLabelV4(key), style = MaterialTheme.typography.bodyMedium)
                }
            }
            CharacterDropIndicatorV4(visible = dragState.showDropAfter)
        }
    }
}

private fun characterTabOrderLabelV4(key: CharacterSheetTabKey): String = when (key) {
    CharacterSheetTabKey.OVERVIEW -> "General"
    CharacterSheetTabKey.SKILLS -> "Habilidades"
    CharacterSheetTabKey.COMBAT -> "Combate"
    CharacterSheetTabKey.DICE -> "Dados"
    CharacterSheetTabKey.MANAGEMENT -> "Gestión"
    CharacterSheetTabKey.EQUIPMENT -> "Equipo"
    CharacterSheetTabKey.BACKGROUND -> "Trasfondo"
    CharacterSheetTabKey.TRAITS -> "Rasgos"
    CharacterSheetTabKey.SPELLS -> "Conjuros"
    CharacterSheetTabKey.ARTIFICER -> "Artífice"
    CharacterSheetTabKey.FORMS -> "Formas"
    CharacterSheetTabKey.TECHNIQUES -> "Técnicas"
    CharacterSheetTabKey.METAMAGIC -> "Metamagia"
    CharacterSheetTabKey.PACTS -> "Pactos"
    CharacterSheetTabKey.COMPANIONS -> "Compañeros"
    CharacterSheetTabKey.NOTES -> "Notas"
}
