package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCondition
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConcentration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomMarker
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterReconciliationCheckpoint
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourcePlacement
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceSuccessorConfiguration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRestKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTemporaryEffect
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableRestPreview
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableTarget
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableTargetKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.applySelectedTrackableRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.previewTrackableRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.pruneCharacterQuickAccessKind
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid

/**
 * Successor Gestión surface. Structural configuration and live values remain on their canonical
 * repositories; this composable only projects and coordinates those existing domains.
 */
@Composable
internal fun CharacterManagementSuccessorTabV4(
    sheet: CharacterSheet,
    generalDraftSheet: CharacterSheet,
    closureState: CharacterClosureState,
    onSheetChange: (CharacterSheet) -> Unit,
    onStructuralSheetChange: (CharacterSheet) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    val settingsContext = LocalCharacterPcSettingsContextV4.current ?: return
    val successorState = settingsContext.successorState
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    var editingConditionId by rememberSaveable { mutableStateOf<String?>(null) }
    var conditionEditorOpen by rememberSaveable { mutableStateOf(false) }
    var deletingConditionId by rememberSaveable { mutableStateOf<String?>(null) }
    var concentrationEditorOpen by rememberSaveable { mutableStateOf(false) }
    var editingResourceId by rememberSaveable { mutableStateOf<String?>(null) }
    var resourceEditorOpen by rememberSaveable { mutableStateOf(false) }
    var deletingResourceId by rememberSaveable { mutableStateOf<String?>(null) }
    var restKindName by rememberSaveable { mutableStateOf<String?>(null) }
    var editingEffectId by rememberSaveable { mutableStateOf<String?>(null) }
    var effectEditorOpen by rememberSaveable { mutableStateOf(false) }
    var deletingEffectId by rememberSaveable { mutableStateOf<String?>(null) }
    var checkpointEditorOpen by rememberSaveable { mutableStateOf(false) }

    val resourceConfigurations = successorState.resourceConfigurations.associateBy { it.resourceId }
    val managementResources = sheet.resources
        .filter { resource ->
            val configuration = resourceConfigurations[resource.id]
            configuration == null || CharacterResourcePlacement.MANAGEMENT in configuration.placements
        }
        .sortedBy { it.sortOrder }
    val generalOperationalDraftDiffers =
        generalDraftSheet.currentHp != sheet.currentHp ||
            generalDraftSheet.maxHp != sheet.maxHp ||
            generalDraftSheet.tempHp != sheet.tempHp

    Column(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
    ) {
        CompactOperationalStateV4(
            sheet = sheet,
            inspirationVisible = successorState.preferences.inspirationVisible,
            onInspirationChange = { enabled ->
                haptic(CharacterHapticEventV4.RESOURCE)
                onSheetChange(sheet.copy(inspiration = enabled))
            },
            onDeathSaveSuccessChange = { value ->
                haptic(CharacterHapticEventV4.RESOURCE)
                onSheetChange(sheet.copy(deathSaveSuccesses = value.coerceIn(0, 3)))
            },
            onDeathSaveFailureChange = { value ->
                haptic(CharacterHapticEventV4.RESOURCE)
                onSheetChange(sheet.copy(deathSaveFailures = value.coerceIn(0, 3)))
            },
            wide = wide,
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(
                start = appSpacingV4(if (wide) 12.dp else 4.dp),
                end = appSpacingV4(if (wide) 12.dp else 4.dp),
                top = 0.dp,
                bottom = appSpacingV4(88.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            if (generalOperationalDraftDiffers) {
                item(key = "management-general-draft-warning") {
                    ManagementNoticeV4(
                        "General tiene cambios de PG sin guardar (${generalDraftSheet.currentHp}/${generalDraftSheet.maxHp}). " +
                            "Gestión muestra y opera el estado guardado (${sheet.currentHp}/${sheet.maxHp}) hasta que uses Guardar.",
                    )
                }
            }

            item(key = "management-trackables") {
                ManagementTrackablesCardV4(
                    markers = successorState.customMarkers,
                    resources = managementResources,
                    configurations = resourceConfigurations,
                    structuralEditingEnabled = structuralEditingEnabled,
                    onMarkerChange = { changed ->
                        haptic(CharacterHapticEventV4.RESOURCE)
                        settingsContext.onSuccessorStateChange(
                            successorState.copy(
                                customMarkers = successorState.customMarkers.map { marker ->
                                    if (marker.id == changed.id) changed else marker
                                },
                            ),
                        )
                    },
                    onResourceValueChange = { resourceId, value ->
                        val existing = sheet.resources.firstOrNull { it.id == resourceId } ?: return@ManagementTrackablesCardV4
                        if (existing.currentValue != value) {
                            haptic(CharacterHapticEventV4.RESOURCE)
                            onSheetChange(
                                sheet.copy(
                                    resources = sheet.resources.map { resource ->
                                        if (resource.id == resourceId) resource.copy(currentValue = value) else resource
                                    },
                                ),
                            )
                        }
                    },
                    onAddResource = {
                        editingResourceId = null
                        resourceEditorOpen = true
                    },
                    onEditResource = { resource ->
                        editingResourceId = resource.id.toString()
                        resourceEditorOpen = true
                    },
                    onDeleteResource = { resource -> deletingResourceId = resource.id.toString() },
                )
            }

            item(key = "management-rest") {
                CompactRestCardV4(
                    onShortRest = { restKindName = CharacterRestKind.SHORT.name },
                    onLongRest = { restKindName = CharacterRestKind.LONG.name },
                )
            }

            item(key = "management-state") {
                ManagementResponsivePairV4(
                    wide = wide,
                    first = {
                        CompactConditionsCardV4(
                            state = closureState,
                            onExhaustionChange = { level ->
                                onClosureStateChange(closureState.copy(exhaustionLevel = level.coerceAtLeast(0)))
                            },
                            onAdd = {
                                editingConditionId = null
                                conditionEditorOpen = true
                            },
                            onEdit = { condition ->
                                editingConditionId = condition.id.toString()
                                conditionEditorOpen = true
                            },
                            onDelete = { condition -> deletingConditionId = condition.id.toString() },
                        )
                    },
                    second = {
                        CompactConcentrationCardV4(
                            concentration = closureState.concentration,
                            onEdit = { concentrationEditorOpen = true },
                            onClear = { onClosureStateChange(closureState.copy(concentration = null)) },
                        )
                    },
                )
            }

            item(key = "management-effects") {
                CompactTemporaryEffectsCardV4(
                    effects = closureState.temporaryEffects,
                    onAdd = {
                        editingEffectId = null
                        effectEditorOpen = true
                    },
                    onEdit = { effect ->
                        editingEffectId = effect.id.toString()
                        effectEditorOpen = true
                    },
                    onDelete = { effect -> deletingEffectId = effect.id.toString() },
                    onToggle = { effect, active ->
                        onClosureStateChange(
                            closureState.copy(
                                temporaryEffects = closureState.temporaryEffects.map { item ->
                                    if (item.id == effect.id) item.copy(active = active) else item
                                },
                            ),
                        )
                    },
                )
            }

            item(key = "management-checkpoints") {
                CompactReconciliationCardV4(
                    checkpoints = closureState.reconciliationCheckpoints,
                    onAdd = { checkpointEditorOpen = true },
                )
            }
        }
    }

    if (resourceEditorOpen && structuralEditingEnabled) {
        val existing = editingResourceId?.let { id -> sheet.resources.firstOrNull { it.id.toString() == id } }
        val existingRule = existing?.let { resource -> closureState.resourceRecovery.firstOrNull { it.resourceId == resource.id } }
        val existingConfiguration = existing?.let { resource -> resourceConfigurations[resource.id] }
        SuccessorResourceEditorDialogV4(
            existing = existing,
            existingRule = existingRule,
            existingConfiguration = existingConfiguration,
            onDismiss = { resourceEditorOpen = false },
            onSave = { resource, rule, configuration ->
                val updatedResources = if (existing == null) {
                    sheet.resources + resource.copy(sortOrder = sheet.resources.size)
                } else {
                    sheet.resources.map { item ->
                        if (item.id == existing.id) resource.copy(sortOrder = item.sortOrder) else item
                    }
                }
                // Parent Resource must exist before successor FK-backed configuration is persisted.
                onStructuralSheetChange(sheet.copy(resources = updatedResources))

                val retainedRules = closureState.resourceRecovery.filterNot { it.resourceId == resource.id }
                onClosureStateChange(
                    closureState.copy(resourceRecovery = if (rule == null) retainedRules else retainedRules + rule),
                )

                val retainedConfigurations = successorState.resourceConfigurations.filterNot { it.resourceId == resource.id }
                settingsContext.onSuccessorStateChange(
                    successorState.copy(resourceConfigurations = retainedConfigurations + configuration),
                )
                resourceEditorOpen = false
            },
        )
    }

    deletingResourceId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = sheet.resources.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deletingResourceId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "recurso",
                onDismissRequest = { deletingResourceId = null },
                onConfirm = {
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                    val updatedResources = sheet.resources.filterNot { it.id == target.id }
                        .mapIndexed { index, item -> item.copy(sortOrder = index) }
                    onStructuralSheetChange(sheet.copy(resources = updatedResources))
                    val liveResourceIds = updatedResources.mapTo(mutableSetOf()) { it.id }
                    onClosureStateChange(
                        closureState.copy(
                            resourceRecovery = closureState.resourceRecovery.filterNot { it.resourceId == target.id },
                            quickAccess = pruneCharacterQuickAccessKind(
                                quickAccess = closureState.quickAccess,
                                kind = CharacterQuickAccessKind.RESOURCE,
                                liveTargetIds = liveResourceIds,
                            ),
                        ),
                    )
                    settingsContext.onSuccessorStateChange(
                        successorState.copy(
                            resourceConfigurations = successorState.resourceConfigurations.filterNot { it.resourceId == target.id },
                        ),
                    )
                    deletingResourceId = null
                },
            )
        }
    }

    restKindName?.let { restName ->
        val rest = runCatching { CharacterRestKind.valueOf(restName) }.getOrDefault(CharacterRestKind.SHORT)
        SuccessorRestPreviewDialogV4(
            rest = rest,
            sheet = sheet,
            closureState = closureState,
            successorStateMarkers = successorState.customMarkers,
            resourceConfigurations = successorState.resourceConfigurations,
            onDismiss = { restKindName = null },
            onApply = { selectedTargets ->
                val preview = previewTrackableRecovery(
                    rest = rest,
                    resources = sheet.resources,
                    resourceRecoveryRules = closureState.resourceRecovery,
                    resourceConfigurations = successorState.resourceConfigurations,
                    markers = successorState.customMarkers,
                )
                val applied = applySelectedTrackableRecovery(
                    resources = sheet.resources,
                    markers = successorState.customMarkers,
                    preview = preview,
                    selectedTargets = selectedTargets,
                )
                if (applied.resources != sheet.resources) {
                    haptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(sheet.copy(resources = applied.resources))
                }
                if (applied.markers != successorState.customMarkers) {
                    haptic(CharacterHapticEventV4.RESOURCE)
                    settingsContext.onSuccessorStateChange(successorState.copy(customMarkers = applied.markers))
                }
                restKindName = null
            },
        )
    }

    if (conditionEditorOpen) {
        val existing = editingConditionId?.let { id -> closureState.conditions.firstOrNull { it.id.toString() == id } }
        SuccessorConditionEditorDialogV4(
            existing = existing,
            onDismiss = { conditionEditorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    closureState.conditions + saved.copy(sortOrder = closureState.conditions.size)
                } else {
                    closureState.conditions.map { item ->
                        if (item.id == existing.id) saved.copy(sortOrder = item.sortOrder) else item
                    }
                }
                onClosureStateChange(closureState.copy(conditions = updated))
                conditionEditorOpen = false
            },
        )
    }

    deletingConditionId?.let { id ->
        val target = closureState.conditions.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deletingConditionId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "condición",
                onDismissRequest = { deletingConditionId = null },
                onConfirm = {
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                    onClosureStateChange(
                        closureState.copy(
                            conditions = closureState.conditions.filterNot { it.id == target.id }
                                .mapIndexed { index, item -> item.copy(sortOrder = index) },
                        ),
                    )
                    deletingConditionId = null
                },
            )
        }
    }

    if (concentrationEditorOpen) {
        SuccessorConcentrationEditorDialogV4(
            existing = closureState.concentration,
            onDismiss = { concentrationEditorOpen = false },
            onSave = { concentration ->
                onClosureStateChange(closureState.copy(concentration = concentration))
                concentrationEditorOpen = false
            },
        )
    }

    if (effectEditorOpen) {
        val existing = editingEffectId?.let { id -> closureState.temporaryEffects.firstOrNull { it.id.toString() == id } }
        SuccessorTemporaryEffectEditorDialogV4(
            existing = existing,
            onDismiss = { effectEditorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    closureState.temporaryEffects + saved.copy(sortOrder = closureState.temporaryEffects.size)
                } else {
                    closureState.temporaryEffects.map { item ->
                        if (item.id == existing.id) saved.copy(sortOrder = item.sortOrder) else item
                    }
                }
                onClosureStateChange(closureState.copy(temporaryEffects = updated))
                effectEditorOpen = false
            },
        )
    }

    deletingEffectId?.let { id ->
        val target = closureState.temporaryEffects.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deletingEffectId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "efecto temporal",
                onDismissRequest = { deletingEffectId = null },
                onConfirm = {
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                    onClosureStateChange(
                        closureState.copy(
                            temporaryEffects = closureState.temporaryEffects.filterNot { it.id == target.id }
                                .mapIndexed { index, item -> item.copy(sortOrder = index) },
                        ),
                    )
                    deletingEffectId = null
                },
            )
        }
    }

    if (checkpointEditorOpen) {
        SuccessorReconciliationEditorDialogV4(
            onDismiss = { checkpointEditorOpen = false },
            onSave = { label, notes ->
                val checkpoint = CharacterReconciliationCheckpoint(
                    id = Uuid.random(),
                    createdAtEpochSeconds = System.currentTimeMillis() / 1000L,
                    characterUpdatedAtEpochSeconds = sheet.updatedAtEpochSeconds,
                    label = label.trim().takeIf { it.isNotEmpty() },
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                )
                onClosureStateChange(
                    closureState.copy(reconciliationCheckpoints = closureState.reconciliationCheckpoints + checkpoint),
                )
                checkpointEditorOpen = false
            },
        )
    }
}

@Composable
private fun CompactOperationalStateV4(
    sheet: CharacterSheet,
    inspirationVisible: Boolean,
    onInspirationChange: (Boolean) -> Unit,
    onDeathSaveSuccessChange: (Int) -> Unit,
    onDeathSaveFailureChange: (Int) -> Unit,
    wide: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(
            start = appSpacingV4(if (wide) 12.dp else 4.dp),
            end = appSpacingV4(if (wide) 12.dp else 4.dp),
            top = appSpacingV4(3.dp),
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(3.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "PG ${sheet.currentHp}/${sheet.maxHp}${if (sheet.tempHp > 0) " · Temp ${sheet.tempHp}" else ""}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                )
                if (inspirationVisible) {
                    CompactToggleChipV4(
                        label = if (sheet.inspiration) "Inspiración ✓" else "Inspiración —",
                        selected = sheet.inspiration,
                        onClick = { onInspirationChange(!sheet.inspiration) },
                    )
                }
            }
            if (sheet.currentHp == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Salv. muerte", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    CompactCounterV4(
                        label = "Éxitos",
                        current = sheet.deathSaveSuccesses,
                        maximum = 3,
                        onChange = onDeathSaveSuccessChange,
                        modifier = Modifier.weight(1f),
                    )
                    CompactCounterV4(
                        label = "Fallos",
                        current = sheet.deathSaveFailures,
                        maximum = 3,
                        onChange = onDeathSaveFailureChange,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ManagementTrackablesCardV4(
    markers: List<CharacterCustomMarker>,
    resources: List<CharacterResource>,
    configurations: Map<Uuid, CharacterResourceSuccessorConfiguration>,
    structuralEditingEnabled: Boolean,
    onMarkerChange: (CharacterCustomMarker) -> Unit,
    onResourceValueChange: (Uuid, Int) -> Unit,
    onAddResource: () -> Unit,
    onEditResource: (CharacterResource) -> Unit,
    onDeleteResource: (CharacterResource) -> Unit,
) {
    CompactManagementCardV4("Marcadores y recursos") {
        if (markers.isNotEmpty()) {
            Text("Marcadores", style = MaterialTheme.typography.labelMedium)
            markers.sortedBy { it.sortOrder }.forEach { marker ->
                TrackableRowV4(
                    name = marker.name,
                    kind = marker.valueKind,
                    current = marker.currentValue,
                    maximum = marker.maxValue,
                    onChange = { value -> onMarkerChange(marker.copy(currentValue = value)) },
                )
            }
        }

        if (resources.isNotEmpty()) {
            Text("Recursos", style = MaterialTheme.typography.labelMedium)
            resources.forEach { resource ->
                val configuration = configurations[resource.id] ?: CharacterResourceSuccessorConfiguration(
                    resourceId = resource.id,
                    valueKind = if (resource.maxValue == null) CharacterTrackableValueKind.COUNTER else CharacterTrackableValueKind.CURRENT_MAX,
                    placements = setOf(CharacterResourcePlacement.MANAGEMENT),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TrackableRowV4(
                        name = resource.name,
                        kind = configuration.valueKind,
                        current = resource.currentValue,
                        maximum = resource.maxValue,
                        onChange = { value -> onResourceValueChange(resource.id, value) },
                        modifier = Modifier.weight(1f),
                    )
                    if (structuralEditingEnabled) {
                        ManagementRowMenuV4(
                            onEdit = { onEditResource(resource) },
                            onDelete = { onDeleteResource(resource) },
                        )
                    }
                }
            }
        }

        if (markers.isEmpty() && resources.isEmpty()) {
            Text("Sin marcadores ni recursos visibles en Gestión.", style = MaterialTheme.typography.bodySmall)
        }
        if (structuralEditingEnabled) {
            TextButton(onClick = onAddResource) { Text("+ Recurso") }
        }
    }
}

@Composable
private fun TrackableRowV4(
    name: String,
    kind: CharacterTrackableValueKind,
    current: Int,
    maximum: Int?,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = appSpacingV4(1.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (kind) {
            CharacterTrackableValueKind.BINARY -> CompactToggleChipV4(
                label = if (current > 0) "Activo" else "Inactivo",
                selected = current > 0,
                onClick = { onChange(if (current > 0) 0 else 1) },
            )
            CharacterTrackableValueKind.COUNTER -> CompactCounterV4(
                label = null,
                current = current,
                maximum = null,
                onChange = onChange,
            )
            CharacterTrackableValueKind.CURRENT_MAX -> CompactCounterV4(
                label = null,
                current = current,
                maximum = maximum,
                onChange = onChange,
            )
        }
    }
}

@Composable
private fun CompactCounterV4(
    label: String?,
    current: Int,
    maximum: Int?,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        label?.let { Text(it, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
        CompactStepButtonV4("−", enabled = current > 0) { onChange((current - 1).coerceAtLeast(0)) }
        Text(
            maximum?.let { "$current/$it" } ?: current.toString(),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
        )
        CompactStepButtonV4("+", enabled = maximum?.let { current < it } ?: true) {
            val next = current + 1
            onChange(maximum?.let { next.coerceAtMost(it) } ?: next)
        }
    }
}

@Composable
private fun CompactStepButtonV4(text: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), contentAlignment = Alignment.Center) {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun CompactToggleChipV4(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        Box(modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}

@Composable
private fun CompactRestCardV4(onShortRest: () -> Unit, onLongRest: () -> Unit) {
    CompactManagementCardV4("Descanso") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Previsualiza y confirma cambios.",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
            OutlinedButton(onClick = onShortRest) { Text("Corto") }
            OutlinedButton(onClick = onLongRest) { Text("Largo") }
        }
    }
}

@Composable
private fun SuccessorRestPreviewDialogV4(
    rest: CharacterRestKind,
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    successorStateMarkers: List<CharacterCustomMarker>,
    resourceConfigurations: List<CharacterResourceSuccessorConfiguration>,
    onDismiss: () -> Unit,
    onApply: (Set<CharacterTrackableTarget>) -> Unit,
) {
    val preview = remember(rest, sheet.resources, closureState.resourceRecovery, successorStateMarkers, resourceConfigurations) {
        previewTrackableRecovery(
            rest = rest,
            resources = sheet.resources,
            resourceRecoveryRules = closureState.resourceRecovery,
            resourceConfigurations = resourceConfigurations,
            markers = successorStateMarkers,
        )
    }
    var selectedKeys by rememberSaveable(rest.name, preview.size) {
        mutableStateOf(preview.filter { it.hasAutomaticChange }.map { it.target.selectionKeyV4() }.toSet())
    }

    CharacterImeSafeEditorDialog(
        title = if (rest == CharacterRestKind.SHORT) "Descanso corto" else "Descanso largo",
        onCancel = onDismiss,
        onSave = { onApply(preview.filter { it.target.selectionKeyV4() in selectedKeys }.mapTo(mutableSetOf()) { it.target }) },
        saveLabel = "Aplicar seleccionados",
        supportingText = "Nada cambia hasta confirmar. Revisión manual nunca modifica un valor automáticamente.",
    ) {
        if (preview.isEmpty()) {
            Text("No hay recursos ni marcadores con recuperación aplicable a este descanso.")
        } else {
            preview.forEach { item -> RestPreviewRowV4(item, selectedKeys) { selectedKeys = it } }
        }
        if (sheet.classes.isNotEmpty()) {
            Text("Dados de Golpe", style = MaterialTheme.typography.titleSmall)
            sheet.classes.forEach { classLevel ->
                Text(
                    "${classLevel.name}: ${classLevel.hitDiceRemaining}/${classLevel.level} d${classLevel.hitDieSides} · revisión manual",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun RestPreviewRowV4(
    item: CharacterTrackableRestPreview,
    selectedKeys: Set<String>,
    onSelectedKeysChange: (Set<String>) -> Unit,
) {
    val key = item.target.selectionKeyV4()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.hasAutomaticChange) {
            Checkbox(
                checked = key in selectedKeys,
                onCheckedChange = { checked ->
                    onSelectedKeysChange(if (checked) selectedKeys + key else selectedKeys - key)
                },
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${if (item.target.kind == CharacterTrackableTargetKind.RESOURCE) "Recurso" else "Marcador"} · ${item.name}",
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                if (item.proposedValue == null) {
                    "${item.currentValue}${item.maxValue?.let { "/$it" }.orEmpty()} · ${item.detail} · revisión manual"
                } else {
                    "${item.currentValue} → ${item.proposedValue}${item.maxValue?.let { "/$it" }.orEmpty()} · ${item.detail}"
                },
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun CompactConditionsCardV4(
    state: CharacterClosureState,
    onExhaustionChange: (Int) -> Unit,
    onAdd: () -> Unit,
    onEdit: (CharacterCondition) -> Unit,
    onDelete: (CharacterCondition) -> Unit,
) {
    CompactManagementCardV4("Condiciones") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Agotamiento", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
            CompactCounterV4(label = null, current = state.exhaustionLevel, maximum = null, onChange = onExhaustionChange)
        }
        state.conditions.sortedBy { it.sortOrder }.forEach { condition ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(condition.name, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    condition.notes?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                ManagementRowMenuV4(onEdit = { onEdit(condition) }, onDelete = { onDelete(condition) })
            }
        }
        TextButton(onClick = onAdd) { Text("+ Condición") }
    }
}

@Composable
private fun CompactConcentrationCardV4(
    concentration: CharacterConcentration?,
    onEdit: () -> Unit,
    onClear: () -> Unit,
) {
    CompactManagementCardV4("Concentración") {
        if (concentration == null) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Sin concentración activa", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onEdit) { Text("Iniciar") }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(concentration.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, maxLines = 1)
                TextButton(onClick = onEdit) { Text("Cambiar") }
                TextButton(onClick = onClear) { Text("Terminar") }
            }
        }
        CharacterHelpV4(
            "Referencia de cálculo: prueba de concentración = salvación de Constitución; CD 10 o la mitad del daño recibido, lo que sea mayor. Esta ficha no automatiza variantes de mesa.",
        )
    }
}

private data class ConditionCatalogEntryV4(
    val key: String,
    val name: String,
    val sourceIdentity: String,
    val helpText: String?,
)

/**
 * Deliberately empty until an approved project corpus supplies predefined condition text.
 * The UI/editor already supports source identity + contextual help when entries are later added.
 */
private val approvedConditionCatalogV4: List<ConditionCatalogEntryV4> = emptyList()

@Composable
private fun SuccessorConditionEditorDialogV4(
    existing: CharacterCondition?,
    onDismiss: () -> Unit,
    onSave: (CharacterCondition) -> Unit,
) {
    var catalogKey by rememberSaveable(existing?.id?.toString(), "catalog") { mutableStateOf("CUSTOM") }
    var name by rememberSaveable(existing?.id?.toString(), "name") { mutableStateOf(existing?.name.orEmpty()) }
    var notes by rememberSaveable(existing?.id?.toString(), "notes") { mutableStateOf(existing?.notes.orEmpty()) }
    val catalogEntry = approvedConditionCatalogV4.firstOrNull { it.key == catalogKey }

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir condición" else "Editar condición",
        onCancel = onDismiss,
        onSave = {
            onSave(
                CharacterCondition(
                    id = existing?.id ?: Uuid.random(),
                    name = catalogEntry?.name ?: name.trim(),
                    source = catalogEntry?.let { "${it.sourceIdentity}#${it.key}" } ?: existing?.source,
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = catalogEntry != null || name.trim().isNotEmpty(),
    ) {
        if (approvedConditionCatalogV4.isNotEmpty()) {
            ManagementChoiceDropdownV4(
                label = "Catálogo",
                current = catalogEntry?.name ?: "Personalizada",
                options = listOf("CUSTOM" to "Personalizada") + approvedConditionCatalogV4.map { it.key to it.name },
                onSelect = { selected ->
                    catalogKey = selected
                    approvedConditionCatalogV4.firstOrNull { it.key == selected }?.let { name = it.name }
                },
            )
        }
        if (catalogEntry == null) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Condición") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        } else {
            catalogEntry.helpText?.let { CharacterHelpV4(it) }
            Text("Fuente de catálogo: ${catalogEntry.sourceIdentity}", style = MaterialTheme.typography.labelSmall)
        }
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = characterCompactTextAreaMinLinesV4(2),
        )
    }
}

@Composable
private fun SuccessorConcentrationEditorDialogV4(
    existing: CharacterConcentration?,
    onDismiss: () -> Unit,
    onSave: (CharacterConcentration) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Iniciar concentración" else "Cambiar concentración",
        onCancel = onDismiss,
        onSave = {
            onSave(
                CharacterConcentration(
                    spellId = existing?.spellId,
                    name = name.trim(),
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                ),
            )
        },
        saveEnabled = name.trim().isNotEmpty(),
    ) {
        OutlinedTextField(name, { name = it }, label = { Text("Conjuro o efecto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
private fun SuccessorResourceEditorDialogV4(
    existing: CharacterResource?,
    existingRule: CharacterResourceRecovery?,
    existingConfiguration: CharacterResourceSuccessorConfiguration?,
    onDismiss: () -> Unit,
    onSave: (CharacterResource, CharacterResourceRecovery?, CharacterResourceSuccessorConfiguration) -> Unit,
) {
    val resourceId = existing?.id ?: remember { Uuid.random() }
    val initialKind = existingConfiguration?.valueKind
        ?: if (existing?.maxValue == null) CharacterTrackableValueKind.COUNTER else CharacterTrackableValueKind.CURRENT_MAX
    val initialPlacements = existingConfiguration?.placements ?: setOf(CharacterResourcePlacement.MANAGEMENT)

    var name by rememberSaveable(resourceId.toString(), "name") { mutableStateOf(existing?.name.orEmpty()) }
    var kindName by rememberSaveable(resourceId.toString(), "kind") { mutableStateOf(initialKind.name) }
    var currentText by rememberSaveable(resourceId.toString(), "current") { mutableStateOf(existing?.currentValue?.toString() ?: "0") }
    var maxText by rememberSaveable(resourceId.toString(), "max") { mutableStateOf(existing?.maxValue?.toString() ?: "1") }
    var binaryActive by rememberSaveable(resourceId.toString(), "binary") { mutableStateOf((existing?.currentValue ?: 0) > 0) }
    var placements by rememberSaveable(resourceId.toString(), "placements") { mutableStateOf(initialPlacements.map { it.name }.toSet()) }
    var cadenceName by rememberSaveable(resourceId.toString(), "cadence") { mutableStateOf((existingRule?.cadence ?: CharacterRecoveryCadence.NONE).name) }
    var amountName by rememberSaveable(resourceId.toString(), "amount") { mutableStateOf((existingRule?.amountMode ?: CharacterRecoveryAmountMode.NONE).name) }
    var fixedText by rememberSaveable(resourceId.toString(), "fixed") { mutableStateOf(existingRule?.fixedAmount?.toString() ?: "1") }
    var recoveryDescription by rememberSaveable(resourceId.toString(), "recovery-description") { mutableStateOf(existing?.recovery.orEmpty()) }
    var recoveryNotes by rememberSaveable(resourceId.toString(), "recovery-notes") { mutableStateOf(existingRule?.notes.orEmpty()) }
    var notes by rememberSaveable(resourceId.toString(), "notes") { mutableStateOf(existing?.notes.orEmpty()) }

    val kind = runCatching { CharacterTrackableValueKind.valueOf(kindName) }.getOrDefault(CharacterTrackableValueKind.COUNTER)
    val cadence = runCatching { CharacterRecoveryCadence.valueOf(cadenceName) }.getOrDefault(CharacterRecoveryCadence.NONE)
    val amountMode = runCatching { CharacterRecoveryAmountMode.valueOf(amountName) }.getOrDefault(CharacterRecoveryAmountMode.NONE)
    val parsedCurrent = currentText.toIntOrNull()
    val parsedMax = maxText.toIntOrNull()
    val parsedFixed = fixedText.toIntOrNull()
    val resolvedCurrent = when (kind) {
        CharacterTrackableValueKind.BINARY -> if (binaryActive) 1 else 0
        CharacterTrackableValueKind.COUNTER -> parsedCurrent
        CharacterTrackableValueKind.CURRENT_MAX -> parsedCurrent
    }
    val resolvedMax = when (kind) {
        CharacterTrackableValueKind.BINARY -> 1
        CharacterTrackableValueKind.COUNTER -> null
        CharacterTrackableValueKind.CURRENT_MAX -> parsedMax
    }
    val selectedPlacements = placements.mapNotNull { nameValue ->
        runCatching { CharacterResourcePlacement.valueOf(nameValue) }.getOrNull()
    }.toSet()
    val automaticCadence = cadence != CharacterRecoveryCadence.NONE && cadence != CharacterRecoveryCadence.MANUAL
    val valid = name.trim().isNotEmpty() &&
        resolvedCurrent != null && resolvedCurrent >= 0 &&
        (kind != CharacterTrackableValueKind.CURRENT_MAX || (resolvedMax != null && resolvedMax >= resolvedCurrent)) &&
        selectedPlacements.isNotEmpty() &&
        (!automaticCadence || amountMode != CharacterRecoveryAmountMode.FIXED || (parsedFixed != null && parsedFixed >= 0))

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir recurso" else "Editar recurso",
        onCancel = onDismiss,
        onSave = {
            val resource = CharacterResource(
                id = resourceId,
                name = name.trim(),
                currentValue = resolvedCurrent ?: 0,
                maxValue = resolvedMax,
                recovery = recoveryDescription.trim().takeIf { it.isNotEmpty() },
                source = existing?.source,
                notes = notes.trim().takeIf { it.isNotEmpty() },
                pinned = existing?.pinned ?: true,
                sortOrder = existing?.sortOrder ?: 0,
            )
            val normalizedAmount = if (cadence == CharacterRecoveryCadence.NONE || cadence == CharacterRecoveryCadence.MANUAL) {
                CharacterRecoveryAmountMode.NONE
            } else amountMode
            val rule = if (cadence == CharacterRecoveryCadence.NONE) {
                null
            } else {
                CharacterResourceRecovery(
                    resourceId = resourceId,
                    cadence = cadence,
                    amountMode = normalizedAmount,
                    fixedAmount = if (normalizedAmount == CharacterRecoveryAmountMode.FIXED) parsedFixed else null,
                    notes = recoveryNotes.trim().takeIf { it.isNotEmpty() },
                )
            }
            onSave(
                resource,
                rule,
                CharacterResourceSuccessorConfiguration(
                    resourceId = resourceId,
                    valueKind = kind,
                    placements = selectedPlacements,
                ),
            )
        },
        saveEnabled = valid,
        supportingText = "Un recurso conserva un solo valor canónico aunque se muestre en varias pestañas. La recuperación manual o ambigua nunca se aplica sola.",
    ) {
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        ManagementChoiceDropdownV4(
            label = "Tipo",
            current = trackableKindLabelV4(kind),
            options = CharacterTrackableValueKind.entries.map { it.name to trackableKindLabelV4(it) },
            onSelect = { kindName = it },
        )

        when (kind) {
            CharacterTrackableValueKind.BINARY -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Estado actual", style = MaterialTheme.typography.bodySmall)
                    Switch(checked = binaryActive, onCheckedChange = { binaryActive = it })
                }
            }
            CharacterTrackableValueKind.COUNTER -> {
                OutlinedTextField(
                    currentText,
                    { currentText = it.filter(Char::isDigit) },
                    label = { Text("Actual") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            CharacterTrackableValueKind.CURRENT_MAX -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                    OutlinedTextField(
                        currentText,
                        { currentText = it.filter(Char::isDigit) },
                        label = { Text("Actual") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        maxText,
                        { maxText = it.filter(Char::isDigit) },
                        label = { Text("Máximo") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }
        }

        Text("Mostrar en", style = MaterialTheme.typography.labelSmall)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
            CharacterResourcePlacement.entries.forEach { placement ->
                val selected = placement.name in placements
                Surface(
                    modifier = Modifier.weight(1f).heightIn(min = 32.dp).clickable {
                        placements = if (selected) placements - placement.name else placements + placement.name
                    },
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                ) {
                    Box(modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                        Text(resourcePlacementLabelV4(placement), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }
        }
        if (selectedPlacements.isEmpty()) {
            Text("Selecciona al menos una pestaña.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.weight(1f)) {
                ManagementChoiceDropdownV4(
                    label = "Recuperación",
                    current = recoveryCadenceLabelSuccessorV4(cadence),
                    options = CharacterRecoveryCadence.entries.map { it.name to recoveryCadenceLabelSuccessorV4(it) },
                    onSelect = { cadenceName = it },
                )
            }
            if (automaticCadence) {
                Box(modifier = Modifier.weight(1f)) {
                    ManagementChoiceDropdownV4(
                        label = "Cantidad",
                        current = recoveryAmountLabelSuccessorV4(amountMode),
                        options = CharacterRecoveryAmountMode.entries.map { it.name to recoveryAmountLabelSuccessorV4(it) },
                        onSelect = { amountName = it },
                    )
                }
            }
        }
        if (automaticCadence && amountMode == CharacterRecoveryAmountMode.FIXED) {
            OutlinedTextField(
                fixedText,
                { fixedText = it.filter(Char::isDigit) },
                label = { Text("Cantidad fija") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        if (automaticCadence && amountMode == CharacterRecoveryAmountMode.TO_MAX && kind == CharacterTrackableValueKind.COUNTER) {
            Text("Este contador no tiene máximo: el descanso lo mostrará para revisión, sin cambio automático.", style = MaterialTheme.typography.labelSmall)
        }
        OutlinedTextField(
            recoveryDescription,
            { recoveryDescription = it },
            label = { Text("Descripción de recuperación") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        if (cadence != CharacterRecoveryCadence.NONE) {
            OutlinedTextField(
                recoveryNotes,
                { recoveryNotes = it },
                label = { Text("Nota del descanso") },
                modifier = Modifier.fillMaxWidth(),
                minLines = characterCompactTextAreaMinLinesV4(2),
            )
        }
        OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
private fun CompactTemporaryEffectsCardV4(
    effects: List<CharacterTemporaryEffect>,
    onAdd: () -> Unit,
    onEdit: (CharacterTemporaryEffect) -> Unit,
    onDelete: (CharacterTemporaryEffect) -> Unit,
    onToggle: (CharacterTemporaryEffect, Boolean) -> Unit,
) {
    CompactManagementCardV4("Efectos temporales") {
        effects.sortedBy { it.sortOrder }.forEach { effect ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(effect.name, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    val summary = listOfNotNull(effect.summary.takeIf { it.isNotBlank() }, effect.durationText?.takeIf { it.isNotBlank() }).joinToString(" · ")
                    if (summary.isNotBlank()) Text(summary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Switch(checked = effect.active, onCheckedChange = { onToggle(effect, it) })
                ManagementRowMenuV4(onEdit = { onEdit(effect) }, onDelete = { onDelete(effect) })
            }
        }
        if (effects.isEmpty()) Text("Sin efectos temporales.", style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onAdd) { Text("+ Efecto") }
    }
}

@Composable
private fun SuccessorTemporaryEffectEditorDialogV4(
    existing: CharacterTemporaryEffect?,
    onDismiss: () -> Unit,
    onSave: (CharacterTemporaryEffect) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var summary by rememberSaveable { mutableStateOf(existing?.summary.orEmpty()) }
    var duration by rememberSaveable { mutableStateOf(existing?.durationText.orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    var active by rememberSaveable { mutableStateOf(existing?.active ?: true) }
    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir efecto temporal" else "Editar efecto temporal",
        onCancel = onDismiss,
        onSave = {
            onSave(
                CharacterTemporaryEffect(
                    id = existing?.id ?: Uuid.random(),
                    name = name.trim(),
                    summary = summary.trim(),
                    durationText = duration.trim().takeIf { it.isNotEmpty() },
                    source = existing?.source,
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    active = active,
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = name.trim().isNotEmpty(),
    ) {
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(summary, { summary = it }, label = { Text("Resumen / modificador") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(duration, { duration = it }, label = { Text("Duración") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Activo")
            Switch(checked = active, onCheckedChange = { active = it })
        }
        OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
private fun CompactReconciliationCardV4(
    checkpoints: List<CharacterReconciliationCheckpoint>,
    onAdd: () -> Unit,
) {
    CompactManagementCardV4("Reconciliación") {
        checkpoints.sortedByDescending { it.createdAtEpochSeconds }.take(3).forEach { checkpoint ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp))) {
                Text(checkpoint.label ?: "Punto de control", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, maxLines = 1)
                Text(formatCheckpointTimeSuccessorV4(checkpoint.createdAtEpochSeconds), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }
        if (checkpoints.isEmpty()) Text("Sin puntos de control.", style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onAdd) { Text("+ Punto de control") }
    }
}

@Composable
private fun SuccessorReconciliationEditorDialogV4(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var label by rememberSaveable { mutableStateOf("Fin de sesión") }
    var notes by rememberSaveable { mutableStateOf("") }
    CharacterImeSafeEditorDialog(
        title = "Crear punto de control",
        onCancel = onDismiss,
        onSave = { onSave(label, notes) },
        saveLabel = "Crear",
    ) {
        OutlinedTextField(label, { label = it }, label = { Text("Etiqueta") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
private fun CompactManagementCardV4(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}

@Composable
private fun ManagementResponsivePairV4(
    wide: Boolean,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    if (wide) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.weight(1f)) { first() }
            Box(modifier = Modifier.weight(1f)) { second() }
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
            first()
            second()
        }
    }
}

@Composable
private fun ManagementNoticeV4(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(text, modifier = Modifier.padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(4.dp)), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ManagementRowMenuV4(onEdit: () -> Unit, onDelete: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { open = true }) { Text("⋮") }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(text = { Text("Editar") }, onClick = { open = false; onEdit() })
            DropdownMenuItem(text = { Text("Eliminar") }, onClick = { open = false; onDelete() })
        }
    }
}

@Composable
private fun ManagementChoiceDropdownV4(
    label: String,
    current: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(current, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEach { (key, value) ->
                    DropdownMenuItem(text = { Text(value) }, onClick = { onSelect(key); open = false })
                }
            }
        }
    }
}

private fun CharacterTrackableTarget.selectionKeyV4(): String = "${kind.name}:$id"

private fun trackableKindLabelV4(kind: CharacterTrackableValueKind): String = when (kind) {
    CharacterTrackableValueKind.BINARY -> "Estado"
    CharacterTrackableValueKind.COUNTER -> "Contador"
    CharacterTrackableValueKind.CURRENT_MAX -> "Actual / máximo"
}

private fun resourcePlacementLabelV4(placement: CharacterResourcePlacement): String = when (placement) {
    CharacterResourcePlacement.GENERAL -> "General"
    CharacterResourcePlacement.MANAGEMENT -> "Gestión"
    CharacterResourcePlacement.EQUIPMENT -> "Equipo"
    CharacterResourcePlacement.TRAITS -> "Rasgos"
}

private fun recoveryCadenceLabelSuccessorV4(value: CharacterRecoveryCadence): String = when (value) {
    CharacterRecoveryCadence.NONE -> "Sin automatizar"
    CharacterRecoveryCadence.SHORT_REST -> "Descanso corto"
    CharacterRecoveryCadence.LONG_REST -> "Descanso largo"
    CharacterRecoveryCadence.SHORT_OR_LONG_REST -> "Corto o largo"
    CharacterRecoveryCadence.MANUAL -> "Manual"
}

private fun recoveryAmountLabelSuccessorV4(value: CharacterRecoveryAmountMode): String = when (value) {
    CharacterRecoveryAmountMode.NONE -> "Revisión manual"
    CharacterRecoveryAmountMode.TO_MAX -> "Hasta máximo"
    CharacterRecoveryAmountMode.FIXED -> "Cantidad fija"
}

private fun formatCheckpointTimeSuccessorV4(epochSeconds: Long): String = runCatching {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).format(formatter)
}.getOrElse { "—" }
