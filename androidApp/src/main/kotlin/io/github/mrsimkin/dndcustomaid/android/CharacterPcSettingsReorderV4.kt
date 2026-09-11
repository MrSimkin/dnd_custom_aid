package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheetTabKey

/**
 * P6 proof consumer: a genuine one-dimensional pick-up-and-move viewport.
 *
 * The list stays in its normal layout. Long-pressing a row lifts that row in place; rendered slot
 * geometry drives the transient preview; canonical tab order changes only once, on successful
 * drop. The viewport owns its scroll state so the shared P6 engine can edge-auto-scroll rather
 * than trapping long drags inside one oversized parent item.
 */
@Composable
internal fun CharacterTabOrderDragSettingsV4(
    hapticsEnabled: Boolean,
    enabled: Boolean,
    onBack: () -> Unit,
) {
    val context = LocalCharacterPcSettingsContextV4.current ?: return
    val canonicalOrder = context.successorState.preferences.tabOrder
    val canonicalIds = canonicalOrder.map { it.name }
    val keyById = remember(canonicalOrder) { canonicalOrder.associateBy { it.name } }
    val listState = rememberLazyListState()
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    val reorderState = rememberCharacterSpatialReorderStateV4(
        canonicalOrder = canonicalIds,
        onCommitOrder = { committedIds ->
            val current = context.successorState
            val currentById = current.preferences.tabOrder.associateBy { it.name }
            val committed = committedIds.mapNotNull(currentById::get)
            if (committed.size == current.preferences.tabOrder.size && committed != current.preferences.tabOrder) {
                context.onSuccessorStateChange(
                    current.copy(
                        preferences = current.preferences.copy(tabOrder = committed),
                    ),
                )
            }
        },
        onHaptic = haptic,
        autoScrollBy = { delta -> listState.scrollBy(delta) },
    )
    CharacterSpatialReorderAutoScrollEffectV4(reorderState)

    val previewOrder = reorderState.previewOrder.mapNotNull(keyById::get)

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .characterSpatialReorderViewportV4(reorderState),
        contentPadding = PaddingValues(
            start = appSpacingV4(7.dp),
            end = appSpacingV4(7.dp),
            top = appSpacingV4(5.dp),
            bottom = appSpacingV4(28.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        item(key = "tab-order-header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                StableBackIconButton(onClick = onBack, contentDescription = "Volver a Ajustes de personaje")
                Column(modifier = Modifier.weight(1f)) {
                    Text("Orden de pestañas", style = MaterialTheme.typography.titleLarge)
                    Text(
                        if (enabled) "Mantén pulsada una fila y arrástrala."
                        else "Modo Mesa: orden en solo lectura.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        itemsIndexed(
            items = previewOrder,
            key = { _, key -> key.name },
        ) { index, key ->
            val id = key.name
            val active = reorderState.draggedId == id
            Surface(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .characterSpatialReorderItemV4(
                        state = reorderState,
                        id = id,
                        enabled = enabled,
                    )
                    .characterSpatialReorderVisualV4(reorderState, id),
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (active) 2.dp else 1.dp,
                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                ),
                tonalElevation = if (active) 3.dp else 0.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = appSpacingV4(8.dp),
                            vertical = appSpacingV4(8.dp),
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
