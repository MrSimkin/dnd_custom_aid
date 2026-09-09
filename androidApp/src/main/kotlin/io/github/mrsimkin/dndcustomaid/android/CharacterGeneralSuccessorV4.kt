package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomAttribute
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomMarker
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterGeneralResourceRow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.customSavingThrowTotal
import io.github.mrsimkin.dndcustomaid.shared.character.generalEquippedItemReferences
import io.github.mrsimkin.dndcustomaid.shared.character.generalLanguages
import io.github.mrsimkin.dndcustomaid.shared.character.generalResources
import io.github.mrsimkin.dndcustomaid.shared.character.generalSpellcastingRows
import kotlin.uuid.Uuid

@Composable
internal fun CharacterGeneralSuccessorCardsV4(
    projectionSheet: CharacterSheet,
    onInspirationChange: (Boolean) -> Unit,
    onResourceValueChange: (Uuid, Int) -> Unit,
) {
    val settingsContext = LocalCharacterPcSettingsContextV4.current ?: return
    val successorState = settingsContext.successorState

    GeneralIdentityProjectionCardV4(projectionSheet)
    GeneralDefensesProjectionCardV4(projectionSheet)

    if (successorState.customAttributes.isNotEmpty()) {
        GeneralCustomAttributesCardV4(
            sheet = projectionSheet,
            attributes = successorState.customAttributes,
        )
    }

    val resources = projectionSheet.generalResources(successorState)
    if (
        successorState.preferences.inspirationVisible ||
        successorState.customMarkers.isNotEmpty() ||
        resources.isNotEmpty()
    ) {
        GeneralQuickStateCardV4(
            sheet = projectionSheet,
            inspirationVisible = successorState.preferences.inspirationVisible,
            markers = successorState.customMarkers,
            resources = resources,
            onInspirationChange = onInspirationChange,
            onMarkerChange = { changed ->
                settingsContext.onSuccessorStateChange(
                    successorState.copy(
                        customMarkers = successorState.customMarkers.map { marker ->
                            if (marker.id == changed.id) changed else marker
                        },
                    ),
                )
            },
            onResourceValueChange = onResourceValueChange,
        )
    }

    if (projectionSheet.spellcasterEnabled) {
        GeneralSpellcastingCardV4(projectionSheet)
    }
}

@Composable
private fun GeneralIdentityProjectionCardV4(sheet: CharacterSheet) {
    val race = sheet.background.race.trim().ifBlank { "—" }
    val languages = sheet.generalLanguages().joinToString(", ") { it.name }.ifBlank { "—" }

    GeneralProjectionCardV4("Identidad") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            GeneralLabelValueV4("Raza", race, Modifier.weight(0.42f))
            GeneralLabelValueV4("Idiomas", languages, Modifier.weight(0.58f))
        }
    }
}

@Composable
private fun GeneralDefensesProjectionCardV4(sheet: CharacterSheet) {
    val equipped = sheet.generalEquippedItemReferences()
    val equippedText = equipped.joinToString(", ") { item ->
        if (item.quantity > 1) "${item.name} ×${item.quantity}" else item.name
    }.ifBlank { "—" }

    GeneralProjectionCardV4("Defensas") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            GeneralLabelValueV4("CA", sheet.armorClass.toString(), Modifier.weight(0.22f))
            GeneralLabelValueV4("Equipo equipado", equippedText, Modifier.weight(0.78f))
        }
        CharacterHelpV4(
            "La CA es el valor canónico del personaje. El inventario actual solo distingue equipo marcado como equipado; no clasifica todavía armadura y escudo de forma estructurada.",
        )
    }
}

@Composable
private fun GeneralCustomAttributesCardV4(
    sheet: CharacterSheet,
    attributes: List<CharacterCustomAttribute>,
) {
    GeneralProjectionCardV4("Características personalizadas") {
        attributes.sortedBy { it.sortOrder }.forEach { attribute ->
            val modifier = attribute.modifier.generalSignedV4()
            val save = if (attribute.savingThrowEnabled) {
                sheet.customSavingThrowTotal(attribute)?.generalSignedV4() ?: "—"
            } else {
                "—"
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(1.dp)),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${attribute.name} (${attribute.abbreviation})",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text("${attribute.score}", style = MaterialTheme.typography.labelLarge)
                Text("Mod $modifier", style = MaterialTheme.typography.labelSmall)
                if (attribute.savingThrowEnabled) {
                    Text("Salv. $save", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun GeneralQuickStateCardV4(
    sheet: CharacterSheet,
    inspirationVisible: Boolean,
    markers: List<CharacterCustomMarker>,
    resources: List<CharacterGeneralResourceRow>,
    onInspirationChange: (Boolean) -> Unit,
    onMarkerChange: (CharacterCustomMarker) -> Unit,
    onResourceValueChange: (Uuid, Int) -> Unit,
) {
    GeneralProjectionCardV4("Estado rápido") {
        if (inspirationVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Inspiración", style = MaterialTheme.typography.bodySmall)
                GeneralToggleV4(
                    selected = sheet.inspiration,
                    onClick = { onInspirationChange(!sheet.inspiration) },
                    selectedLabel = "Sí",
                    unselectedLabel = "No",
                )
            }
        }

        markers.sortedBy { it.sortOrder }.forEach { marker ->
            GeneralMarkerRowV4(marker, onMarkerChange)
        }

        resources.forEach { row ->
            GeneralResourceRowV4(row, onResourceValueChange)
        }
    }
}

@Composable
private fun GeneralMarkerRowV4(
    marker: CharacterCustomMarker,
    onChange: (CharacterCustomMarker) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(1.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            marker.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (marker.valueKind) {
            CharacterTrackableValueKind.BINARY -> GeneralToggleV4(
                selected = marker.currentValue > 0,
                onClick = { onChange(marker.copy(currentValue = if (marker.currentValue > 0) 0 else 1)) },
                selectedLabel = "Activo",
                unselectedLabel = "Inactivo",
            )

            CharacterTrackableValueKind.COUNTER,
            CharacterTrackableValueKind.CURRENT_MAX,
            -> GeneralCounterV4(
                current = marker.currentValue,
                maximum = marker.maxValue,
                onChange = { value -> onChange(marker.copy(currentValue = value)) },
            )
        }
    }
}

@Composable
private fun GeneralResourceRowV4(
    row: CharacterGeneralResourceRow,
    onValueChange: (Uuid, Int) -> Unit,
) {
    val resource = row.resource
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(1.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            resource.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (row.configuration.valueKind) {
            CharacterTrackableValueKind.BINARY -> GeneralToggleV4(
                selected = resource.currentValue > 0,
                onClick = { onValueChange(resource.id, if (resource.currentValue > 0) 0 else 1) },
                selectedLabel = "Activo",
                unselectedLabel = "Inactivo",
            )

            CharacterTrackableValueKind.COUNTER,
            CharacterTrackableValueKind.CURRENT_MAX,
            -> GeneralCounterV4(
                current = resource.currentValue,
                maximum = resource.maxValue,
                onChange = { value -> onValueChange(resource.id, value) },
            )
        }
    }
}

@Composable
private fun GeneralSpellcastingCardV4(sheet: CharacterSheet) {
    val settingsContext = LocalCharacterPcSettingsContextV4.current ?: return
    val successorState = settingsContext.successorState
    val rows = sheet.generalSpellcastingRows(successorState)

    GeneralProjectionCardV4("Lanzamiento de Conjuros") {
        if (rows.isEmpty()) {
            Text("Sin fuentes de conjuros configuradas.", style = MaterialTheme.typography.bodySmall)
        } else {
            rows.forEach { row ->
                val ability = row.profile?.ability?.let { reference ->
                    generalAbilityReferenceLabelV4(reference, successorState.customAttributes)
                } ?: "Sin configurar"
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(1.dp)),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        row.source.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(ability, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    GeneralCompactMetricV4("CD salv.", row.saveDc?.toString() ?: "—")
                    GeneralCompactMetricV4("Ataque", row.spellAttackModifier?.generalSignedV4() ?: "—")
                }
            }
        }
        CharacterHelpV4(
            "CD salv. conjuro = 8 + modificador de Aptitud mágica + bono de competencia + ajuste de la fuente. Mod. ataque mágico = modificador de Aptitud mágica + bono de competencia + ajuste de la fuente.",
        )
    }
}

@Composable
private fun GeneralProjectionCardV4(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}

@Composable
private fun GeneralLabelValueV4(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun GeneralCompactMetricV4(label: String, value: String) {
    Column(horizontalAlignment = Alignment.End) {
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        Text(value, style = MaterialTheme.typography.labelLarge, maxLines = 1)
    }
}

@Composable
private fun GeneralToggleV4(
    selected: Boolean,
    onClick: () -> Unit,
    selectedLabel: String,
    unselectedLabel: String,
) {
    Surface(
        modifier = Modifier.heightIn(min = 32.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(if (selected) selectedLabel else unselectedLabel, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun GeneralCounterV4(
    current: Int,
    maximum: Int?,
    onChange: (Int) -> Unit,
) {
    val upper = maximum ?: Int.MAX_VALUE
    Row(
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GeneralCounterButtonV4("−", enabled = current > 0) { onChange((current - 1).coerceAtLeast(0)) }
        Text(
            if (maximum == null) current.toString() else "$current/$maximum",
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
        )
        GeneralCounterButtonV4("+", enabled = current < upper) { onChange((current + 1).coerceAtMost(upper)) }
    }
}

@Composable
private fun GeneralCounterButtonV4(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun generalAbilityReferenceLabelV4(
    reference: CharacterAbilityReference,
    customAttributes: List<CharacterCustomAttribute>,
): String {
    reference.builtIn?.let { ability ->
        return when (ability) {
            CharacterAbility.STRENGTH -> "FUE"
            CharacterAbility.DEXTERITY -> "DES"
            CharacterAbility.CONSTITUTION -> "CON"
            CharacterAbility.INTELLIGENCE -> "INT"
            CharacterAbility.WISDOM -> "SAB"
            CharacterAbility.CHARISMA -> "CAR"
        }
    }
    reference.customAttributeId?.let { id ->
        return customAttributes.firstOrNull { it.id == id }
            ?.let { it.abbreviation.ifBlank { it.name } }
            ?: "Personalizada"
    }
    return "Sin configurar"
}

private fun Int.generalSignedV4(): String = if (this >= 0) "+$this" else toString()
