package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import io.github.mrsimkin.dndcustomaid.shared.character.effectiveInventoryCarryState

/** 2D proof/production consumer for the P6 spatial pick-up-and-move engine. */
@Composable
internal fun CharacterEquipmentSpatialSectionV4(
    title: String,
    items: List<CharacterInventoryItem>,
    order: CharacterPresentationOrder,
    onOrderChange: (CharacterPresentationOrder) -> Unit,
    collapsed: Boolean,
    onCollapsedChange: (Boolean) -> Unit,
    reorderAvailable: Boolean,
    reorderState: CharacterSpatialReorderStateV4,
    queryActive: Boolean,
    wide: Boolean,
    special: Boolean,
    selectedId: String?,
    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage,
    onEdit: (CharacterInventoryItem) -> Unit,
    onQuickUse: (CharacterInventoryItem, CharacterInventoryUsage) -> Unit,
    onDuplicate: (CharacterInventoryItem) -> Unit,
    onDelete: (CharacterInventoryItem) -> Unit,
    structuralEditingEnabled: Boolean,
) {
    val itemById = items.associateBy { it.id.toString() }
    val displayIds = if (reorderAvailable) {
        reorderState.previewOrder.filter(itemById::containsKey)
    } else {
        items.map { it.id.toString() }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = appSpacingV4(5.dp),
                vertical = appSpacingV4(4.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("$title (${items.size})", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                EquipmentSpatialOrderButtonV4("Manual", order == CharacterPresentationOrder.MANUAL) {
                    onOrderChange(CharacterPresentationOrder.MANUAL)
                }
                EquipmentSpatialOrderButtonV4("A–Z", order == CharacterPresentationOrder.ALPHABETICAL) {
                    onOrderChange(CharacterPresentationOrder.ALPHABETICAL)
                }
                TextButton(onClick = { onCollapsedChange(!collapsed) }) {
                    Text(if (collapsed) "Mostrar" else "Ocultar")
                }
            }

            if (order == CharacterPresentationOrder.MANUAL && queryActive) {
                Text(
                    "Limpia búsqueda y filtros para reordenar.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!collapsed) {
                if (items.isEmpty()) {
                    Text("Sin elementos visibles.", style = MaterialTheme.typography.bodySmall)
                } else {
                    val columns = constrainedCardColumnsV4(
                        wide = wide,
                        phoneMax = if (special) 2 else 3,
                        wideMax = if (special) 3 else 5,
                    )
                    CharacterSpatialGridV4(
                        ids = displayIds,
                        columns = columns,
                        reorderState = reorderState,
                        reorderEnabled = reorderAvailable,
                    ) { id, pickupModifier ->
                        val item = itemById[id] ?: return@CharacterSpatialGridV4
                        CharacterEquipmentSpatialCardV4(
                            item = item,
                            usage = usageFor(item),
                            pickupModifier = pickupModifier,
                            special = special,
                            selected = selectedId == id,
                            onEdit = { onEdit(item) },
                            onQuickUse = { onQuickUse(item, usageFor(item)) },
                            onDuplicate = { onDuplicate(item) },
                            onDelete = { onDelete(item) },
                            structuralEditingEnabled = structuralEditingEnabled,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentSpatialOrderButtonV4(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(onClick = onClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) { Text(label) }
    }
}

@Composable
private fun CharacterEquipmentSpatialCardV4(
    item: CharacterInventoryItem,
    usage: CharacterInventoryUsage,
    pickupModifier: Modifier,
    special: Boolean,
    selected: Boolean,
    onEdit: () -> Unit,
    onQuickUse: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    structuralEditingEnabled: Boolean,
) {
    val carry = effectiveInventoryCarryState(item, usage)
    val stateLabels = buildList {
        add(if (carry == CharacterInventoryCarryState.CARRIED) "Transportado" else "Guardado")
        if (item.equipped) add("Equipado")
        if (item.attuned) add("Sintonizado")
    }
    val meta = buildList {
        when (usage.kind) {
            CharacterConsumableKind.CONSUMABLE -> add("Consumible −${usage.quickUseAmount}")
            CharacterConsumableKind.AMMUNITION -> add("Munición −${usage.quickUseAmount}")
            CharacterConsumableKind.NONE -> Unit
        }
        item.location?.takeIf { it.isNotBlank() }?.let { add(it) }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        tonalElevation = if (special) 1.dp else 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // The body is both the normal edit target and the deliberate long-press pickup region.
            // Operational child controls below are intentionally outside this modifier.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(pickupModifier)
                    .clickable(enabled = structuralEditingEnabled, onClick = onEdit)
                    .padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
                        Text(item.name, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (special) {
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                            ) {
                                stateLabels.forEach { label ->
                                    CharacterSemanticBadgeV4(label = label, kind = CharacterSemanticBadgeKindV4.STATE)
                                }
                            }
                        } else {
                            Text(
                                stateLabels.joinToString(" · "),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (meta.isNotEmpty()) {
                            Text(
                                meta.joinToString(" · "),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("×${item.quantity}", style = MaterialTheme.typography.labelLarge)
                        item.weightLb?.let { Text("${formatCompactEquipmentSpatialV4(it)} lb/u", style = MaterialTheme.typography.labelSmall) }
                    }
                }
                if (special) {
                    item.description?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = appSpacingV4(3.dp), vertical = appSpacingV4(1.dp)),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (usage.kind != CharacterConsumableKind.NONE && item.quantity > 0) {
                    TextButton(
                        onClick = onQuickUse,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    ) { Text("Usar −${usage.quickUseAmount}") }
                }
                if (structuralEditingEnabled) {
                    StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${item.name}")
                    StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${item.name}")
                }
            }
        }
    }
}

private fun formatCompactEquipmentSpatialV4(value: Double): String {
    val rounded = if (kotlin.math.abs(value - value.toLong()) < 0.000001) {
        value.toLong().toString()
    } else {
        "%.3f".format(java.util.Locale.US, value).trimEnd('0').trimEnd('.')
    }
    return rounded.replace('.', ',')
}
