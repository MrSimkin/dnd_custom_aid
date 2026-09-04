package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCondition
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConcentration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterReconciliationCheckpoint
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRestKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTemporaryEffect
import io.github.mrsimkin.dndcustomaid.shared.character.applySelectedResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.previewResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.pruneCharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterQuickAccessFavorite
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid

@Composable
internal fun CharacterManagementTabV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    onSheetChange: (CharacterSheet) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
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
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 14.dp else 6.dp,
            end = if (wide) 14.dp else 6.dp,
            top = 8.dp,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "management-state") {
            ManagementPairV4(
                wide = wide,
                first = {
                    ConditionsExhaustionCardV4(
                        state = closureState,
                        onExhaustionChange = { level ->
                            onClosureStateChange(closureState.copy(exhaustionLevel = level.coerceAtLeast(0)))
                        },
                        onAddCondition = {
                            editingConditionId = null
                            conditionEditorOpen = true
                        },
                        onEditCondition = { condition ->
                            editingConditionId = condition.id.toString()
                            conditionEditorOpen = true
                        },
                        onDeleteCondition = { condition -> deletingConditionId = condition.id.toString() },
                    )
                },
                second = {
                    ConcentrationCardV4(
                        concentration = closureState.concentration,
                        onEdit = { concentrationEditorOpen = true },
                        onClear = {
                            onClosureStateChange(closureState.copy(concentration = null))
                        },
                    )
                },
            )
        }

        item(key = "management-operational") {
            OperationalStateCardV4(
                sheet = sheet,
                onSheetChange = onSheetChange,
                onHaptic = haptic,
            )
        }

        item(key = "management-resources") {
            ResourcesCardV4(
                resources = sheet.resources,
                favoriteResourceIds = closureState.quickAccess
                    .filter { it.kind == CharacterQuickAccessKind.RESOURCE }
                    .mapTo(mutableSetOf()) { it.targetId },
                structuralEditingEnabled = structuralEditingEnabled,
                onAdd = {
                    editingResourceId = null
                    resourceEditorOpen = true
                },
                onEdit = { resource ->
                    editingResourceId = resource.id.toString()
                    resourceEditorOpen = true
                },
                onDelete = { resource -> deletingResourceId = resource.id.toString() },
                onFavoriteChange = { resource, favorite ->
                    if (structuralEditingEnabled) {
                        onClosureStateChange(
                            closureState.copy(
                                quickAccess = setCharacterQuickAccessFavorite(
                                    quickAccess = closureState.quickAccess,
                                    kind = CharacterQuickAccessKind.RESOURCE,
                                    targetId = resource.id,
                                    favorite = favorite,
                                ),
                            ),
                        )
                    }
                },
                onAdjust = { resource, delta ->
                    val maximum = resource.maxValue
                    val changed = (resource.currentValue + delta).coerceAtLeast(0).let { value ->
                        maximum?.let { value.coerceAtMost(it) } ?: value
                    }
                    if (changed != resource.currentValue) {
                        haptic(CharacterHapticEventV4.RESOURCE)
                        onSheetChange(
                            sheet.copy(
                                resources = sheet.resources.map {
                                    if (it.id == resource.id) it.copy(currentValue = changed) else it
                                },
                            ),
                        )
                    }
                },
            )
        }

        item(key = "management-rest") {
            RestAssistantCardV4(
                onShortRest = { restKindName = CharacterRestKind.SHORT.name },
                onLongRest = { restKindName = CharacterRestKind.LONG.name },
            )
        }

        item(key = "management-effects") {
            TemporaryEffectsCardV4(
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
                            temporaryEffects = closureState.temporaryEffects.map {
                                if (it.id == effect.id) it.copy(active = active) else it
                            },
                        ),
                    )
                },
            )
        }

        item(key = "management-checkpoints") {
            ReconciliationCardV4(
                checkpoints = closureState.reconciliationCheckpoints,
                onAdd = { checkpointEditorOpen = true },
            )
        }
    }

    if (conditionEditorOpen) {
        val existing = editingConditionId?.let { id ->
            closureState.conditions.firstOrNull { it.id.toString() == id }
        }
        ConditionEditorDialogV4(
            existing = existing,
            onDismiss = { conditionEditorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    closureState.conditions + saved.copy(sortOrder = closureState.conditions.size)
                } else {
                    closureState.conditions.map { if (it.id == existing.id) saved.copy(sortOrder = it.sortOrder) else it }
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
        ConcentrationEditorDialogV4(
            existing = closureState.concentration,
            onDismiss = { concentrationEditorOpen = false },
            onSave = { concentration ->
                onClosureStateChange(closureState.copy(concentration = concentration))
                concentrationEditorOpen = false
            },
        )
    }

    if (resourceEditorOpen && structuralEditingEnabled) {
        val existing = editingResourceId?.let { id -> sheet.resources.firstOrNull { it.id.toString() == id } }
        val existingRule = existing?.let { resource ->
            closureState.resourceRecovery.firstOrNull { it.resourceId == resource.id }
        }
        ResourceEditorDialogV4(
            existing = existing,
            existingRule = existingRule,
            onDismiss = { resourceEditorOpen = false },
            onSave = { resource, rule ->
                val updatedResources = if (existing == null) {
                    sheet.resources + resource.copy(sortOrder = sheet.resources.size)
                } else {
                    sheet.resources.map { if (it.id == existing.id) resource.copy(sortOrder = it.sortOrder) else it }
                }
                onSheetChange(sheet.copy(resources = updatedResources))
                val retainedRules = closureState.resourceRecovery.filterNot { it.resourceId == resource.id }
                onClosureStateChange(
                    closureState.copy(resourceRecovery = if (rule == null) retainedRules else retainedRules + rule),
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
                    onSheetChange(
                        sheet.copy(
                            resources = sheet.resources.filterNot { it.id == target.id }
                                .mapIndexed { index, item -> item.copy(sortOrder = index) },
                        ),
                    )
                    val liveResourceIds = sheet.resources
                        .filterNot { it.id == target.id }
                        .mapTo(mutableSetOf()) { it.id }
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
                    deletingResourceId = null
                },
            )
        }
    }

    restKindName?.let { name ->
        val rest = runCatching { CharacterRestKind.valueOf(name) }.getOrDefault(CharacterRestKind.SHORT)
        RestPreviewDialogV4(
            rest = rest,
            sheet = sheet,
            closureState = closureState,
            onDismiss = { restKindName = null },
            onApply = { selectedIds ->
                val preview = previewResourceRecovery(rest, sheet.resources, closureState.resourceRecovery)
                val recovered = applySelectedResourceRecovery(sheet.resources, preview, selectedIds)
                if (recovered != sheet.resources) {
                    haptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(sheet.copy(resources = recovered))
                }
                restKindName = null
            },
        )
    }

    if (effectEditorOpen) {
        val existing = editingEffectId?.let { id ->
            closureState.temporaryEffects.firstOrNull { it.id.toString() == id }
        }
        TemporaryEffectEditorDialogV4(
            existing = existing,
            onDismiss = { effectEditorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    closureState.temporaryEffects + saved.copy(sortOrder = closureState.temporaryEffects.size)
                } else {
                    closureState.temporaryEffects.map {
                        if (it.id == existing.id) saved.copy(sortOrder = it.sortOrder) else it
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
        ReconciliationEditorDialogV4(
            onDismiss = { checkpointEditorOpen = false },
            onSave = { label, notes ->
                val now = System.currentTimeMillis() / 1000L
                val checkpoint = CharacterReconciliationCheckpoint(
                    id = Uuid.random(),
                    createdAtEpochSeconds = now,
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
private fun ManagementPairV4(
    wide: Boolean,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    if (wide) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(modifier = Modifier.weight(1f)) { first() }
            Box(modifier = Modifier.weight(1f)) { second() }
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            first()
            second()
        }
    }
}

@Composable
private fun ManagementCardV4(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun ConditionsExhaustionCardV4(
    state: CharacterClosureState,
    onExhaustionChange: (Int) -> Unit,
    onAddCondition: () -> Unit,
    onEditCondition: (CharacterCondition) -> Unit,
    onDeleteCondition: (CharacterCondition) -> Unit,
) {
    ManagementCardV4("Condiciones y agotamiento") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Agotamiento", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(onClick = { onExhaustionChange(state.exhaustionLevel - 1) }, enabled = state.exhaustionLevel > 0) {
                    Text("−")
                }
                Text(state.exhaustionLevel.toString(), style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { onExhaustionChange(state.exhaustionLevel + 1) }) { Text("+") }
            }
        }
        if (state.conditions.isEmpty()) {
            CharacterUsefulEmptyState(
                title = "Sin condiciones registradas",
                message = "Añade solo las condiciones que quieras mantener visibles en la ficha.",
                onAdd = onAddCondition,
                addLabel = "Añadir condición",
            )
        } else {
            state.conditions.forEach { condition ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { onEditCondition(condition) }, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(condition.name, style = MaterialTheme.typography.labelLarge)
                            condition.source?.takeIf { it.isNotBlank() }?.let {
                                Text(it, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    TextButton(onClick = { onDeleteCondition(condition) }) { Text("Eliminar") }
                }
            }
            TextButton(onClick = onAddCondition) { Text("+ Añadir condición") }
        }
    }
}

@Composable
private fun ConcentrationCardV4(
    concentration: CharacterConcentration?,
    onEdit: () -> Unit,
    onClear: () -> Unit,
) {
    ManagementCardV4("Concentración") {
        if (concentration == null) {
            Text("Sin concentración activa.", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = onEdit) { Text("Iniciar concentración") }
        } else {
            CharacterSemanticBadgeV4(
                label = "Concentración activa",
                kind = CharacterSemanticBadgeKindV4.STATE,
            )
            Text(concentration.name, style = MaterialTheme.typography.titleSmall)
            concentration.notes?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = onEdit) { Text("Cambiar") }
                TextButton(onClick = onClear) { Text("Terminar") }
            }
        }
    }
}

@Composable
private fun OperationalStateCardV4(
    sheet: CharacterSheet,
    onSheetChange: (CharacterSheet) -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    ManagementCardV4("Estado operativo") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Inspiración", style = MaterialTheme.typography.labelLarge)
                Text(if (sheet.inspiration) "Disponible" else "No disponible", style = MaterialTheme.typography.labelSmall)
            }
            Switch(
                checked = sheet.inspiration,
                onCheckedChange = {
                    onHaptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(sheet.copy(inspiration = it))
                },
            )
        }
        Text("PG ${sheet.currentHp}/${sheet.maxHp}${if (sheet.tempHp > 0) " · ${sheet.tempHp} temporales" else ""}")
        if (sheet.currentHp == 0) {
            Text("Salvaciones de muerte", style = MaterialTheme.typography.titleSmall)
            DeathSaveRowV4(
                label = "Éxitos",
                value = sheet.deathSaveSuccesses,
                onChange = { value ->
                    onHaptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(sheet.copy(deathSaveSuccesses = value.coerceIn(0, 3)))
                },
            )
            DeathSaveRowV4(
                label = "Fallos",
                value = sheet.deathSaveFailures,
                onChange = { value ->
                    onHaptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(sheet.copy(deathSaveFailures = value.coerceIn(0, 3)))
                },
            )
        } else {
            Text(
                "Las salvaciones de muerte aparecen aquí cuando el personaje llega a 0 PG.",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun DeathSaveRowV4(label: String, value: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedButton(onClick = { onChange(value - 1) }, enabled = value > 0) { Text("−") }
            Text("$value/3")
            OutlinedButton(onClick = { onChange(value + 1) }, enabled = value < 3) { Text("+") }
        }
    }
}

@Composable
private fun ResourcesCardV4(
    resources: List<CharacterResource>,
    favoriteResourceIds: Set<Uuid>,
    structuralEditingEnabled: Boolean,
    onAdd: () -> Unit,
    onEdit: (CharacterResource) -> Unit,
    onDelete: (CharacterResource) -> Unit,
    onFavoriteChange: (CharacterResource, Boolean) -> Unit,
    onAdjust: (CharacterResource, Int) -> Unit,
) {
    ManagementCardV4("Recursos") {
        if (resources.isEmpty()) {
            CharacterUsefulEmptyState(
                title = "Sin recursos",
                message = "Úsalos para dados, puntos, cargas o cualquier contador de clase, subclase o homebrew.",
                onAdd = if (structuralEditingEnabled) onAdd else null,
                addLabel = "Añadir recurso",
            )
        } else {
            resources.sortedBy { it.sortOrder }.forEach { resource ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { onEdit(resource) }, enabled = structuralEditingEnabled, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(resource.name, style = MaterialTheme.typography.labelLarge)
                            Text(
                                buildString {
                                    append(resource.currentValue)
                                    resource.maxValue?.let { append(" / $it") }
                                    resource.source?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
                                },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    OutlinedButton(onClick = { onAdjust(resource, -1) }, enabled = resource.currentValue > 0) { Text("−") }
                    OutlinedButton(
                        onClick = { onAdjust(resource, 1) },
                        enabled = resource.maxValue?.let { max -> resource.currentValue < max } ?: true,
                    ) { Text("+") }
                    val favorite = resource.id in favoriteResourceIds
                    TextButton(
                        onClick = { onFavoriteChange(resource, !favorite) },
                        enabled = structuralEditingEnabled,
                    ) {
                        Text(if (favorite) "★" else "☆")
                    }
                    TextButton(onClick = { onDelete(resource) }, enabled = structuralEditingEnabled) { Text("Eliminar") }
                }
            }
            TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text("+ Añadir recurso") }
        }
    }
}

@Composable
private fun RestAssistantCardV4(onShortRest: () -> Unit, onLongRest: () -> Unit) {
    ManagementCardV4("Descanso") {
        Text(
            "Previsualiza recuperaciones configuradas y aplica solo las que confirmes. Las reglas manuales nunca se ejecutan solas.",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onShortRest) { Text("Descanso corto") }
            Button(onClick = onLongRest) { Text("Descanso largo") }
        }
    }
}

@Composable
private fun TemporaryEffectsCardV4(
    effects: List<CharacterTemporaryEffect>,
    onAdd: () -> Unit,
    onEdit: (CharacterTemporaryEffect) -> Unit,
    onDelete: (CharacterTemporaryEffect) -> Unit,
    onToggle: (CharacterTemporaryEffect, Boolean) -> Unit,
) {
    ManagementCardV4("Efectos temporales") {
        if (effects.isEmpty()) {
            CharacterUsefulEmptyState(
                title = "Sin efectos temporales",
                message = "Registra aquí bonificadores, penalizadores o estados de sesión que no pertenezcan a la ficha permanente.",
                onAdd = onAdd,
                addLabel = "Añadir efecto",
            )
        } else {
            effects.sortedBy { it.sortOrder }.forEach { effect ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { onEdit(effect) }, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(effect.name, style = MaterialTheme.typography.labelLarge)
                            Text(
                                listOfNotNull(
                                    effect.summary.takeIf { it.isNotBlank() },
                                    effect.durationText?.takeIf { it.isNotBlank() },
                                ).joinToString(" · ").ifBlank { "Sin resumen" },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    Switch(checked = effect.active, onCheckedChange = { onToggle(effect, it) })
                    TextButton(onClick = { onDelete(effect) }) { Text("Eliminar") }
                }
            }
            TextButton(onClick = onAdd) { Text("+ Añadir efecto") }
        }
    }
}

@Composable
private fun ReconciliationCardV4(
    checkpoints: List<CharacterReconciliationCheckpoint>,
    onAdd: () -> Unit,
) {
    ManagementCardV4("Reconciliación con ficha física") {
        Text(
            "Crea un punto de control cuando confirmes que la ficha digital refleja intencionalmente tu ficha de mesa.",
            style = MaterialTheme.typography.bodySmall,
        )
        checkpoints.sortedByDescending { it.createdAtEpochSeconds }.take(5).forEach { checkpoint ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text(checkpoint.label ?: "Punto de control", style = MaterialTheme.typography.labelLarge)
                Text(formatCheckpointTimeV4(checkpoint.createdAtEpochSeconds), style = MaterialTheme.typography.labelSmall)
                checkpoint.notes?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
        if (checkpoints.isEmpty()) {
            Text("Aún no hay puntos de control.", style = MaterialTheme.typography.labelSmall)
        }
        TextButton(onClick = onAdd) { Text("Crear punto de control") }
    }
}

@Composable
private fun ConditionEditorDialogV4(
    existing: CharacterCondition?,
    onDismiss: () -> Unit,
    onSave: (CharacterCondition) -> Unit,
) {
    var name by rememberSaveable(existing?.id?.toString()) { mutableStateOf(existing?.name.orEmpty()) }
    var source by rememberSaveable(existing?.id?.toString(), "source") { mutableStateOf(existing?.source.orEmpty()) }
    var notes by rememberSaveable(existing?.id?.toString(), "notes") { mutableStateOf(existing?.notes.orEmpty()) }
    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir condición" else "Editar condición",
        onCancel = onDismiss,
        onSave = {
            onSave(
                CharacterCondition(
                    id = existing?.id ?: Uuid.random(),
                    name = name.trim(),
                    source = source.trim().takeIf { it.isNotEmpty() },
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = name.trim().isNotEmpty(),
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Condición") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = source, onValueChange = { source = it }, label = { Text("Fuente opcional") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    }
}

@Composable
private fun ConcentrationEditorDialogV4(
    existing: CharacterConcentration?,
    onDismiss: () -> Unit,
    onSave: (CharacterConcentration) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Iniciar concentración" else "Cambiar concentración",
        onCancel = onDismiss,
        onSave = { onSave(CharacterConcentration(spellId = existing?.spellId, name = name.trim(), notes = notes.trim().takeIf { it.isNotEmpty() })) },
        saveEnabled = name.trim().isNotEmpty(),
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Conjuro o efecto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    }
}

@Composable
private fun ResourceEditorDialogV4(
    existing: CharacterResource?,
    existingRule: CharacterResourceRecovery?,
    onDismiss: () -> Unit,
    onSave: (CharacterResource, CharacterResourceRecovery?) -> Unit,
) {
    val resourceId = existing?.id ?: remember { Uuid.random() }
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var current by rememberSaveable { mutableStateOf(existing?.currentValue?.toString() ?: "0") }
    var maximum by rememberSaveable { mutableStateOf(existing?.maxValue?.toString().orEmpty()) }
    var source by rememberSaveable { mutableStateOf(existing?.source.orEmpty()) }
    var recoveryText by rememberSaveable { mutableStateOf(existing?.recovery.orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    var cadenceName by rememberSaveable { mutableStateOf((existingRule?.cadence ?: CharacterRecoveryCadence.NONE).name) }
    var amountModeName by rememberSaveable { mutableStateOf((existingRule?.amountMode ?: CharacterRecoveryAmountMode.NONE).name) }
    var fixedAmount by rememberSaveable { mutableStateOf(existingRule?.fixedAmount?.toString().orEmpty()) }
    var recoveryNotes by rememberSaveable { mutableStateOf(existingRule?.notes.orEmpty()) }

    val parsedCurrent = current.toIntOrNull()
    val parsedMaximum = maximum.takeIf { it.isNotBlank() }?.toIntOrNull()
    val cadence = runCatching { CharacterRecoveryCadence.valueOf(cadenceName) }.getOrDefault(CharacterRecoveryCadence.NONE)
    val amountMode = runCatching { CharacterRecoveryAmountMode.valueOf(amountModeName) }.getOrDefault(CharacterRecoveryAmountMode.NONE)
    val parsedFixed = fixedAmount.takeIf { it.isNotBlank() }?.toIntOrNull()
    val automaticCadence = cadence != CharacterRecoveryCadence.NONE && cadence != CharacterRecoveryCadence.MANUAL
    val valid = name.trim().isNotEmpty() && parsedCurrent != null && parsedCurrent >= 0 &&
        (maximum.isBlank() || (parsedMaximum != null && parsedMaximum >= parsedCurrent)) &&
        (!automaticCadence || amountMode != CharacterRecoveryAmountMode.FIXED || (parsedFixed != null && parsedFixed >= 0))

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir recurso" else "Editar recurso",
        onCancel = onDismiss,
        onSave = {
            val resource = CharacterResource(
                id = resourceId,
                name = name.trim(),
                currentValue = parsedCurrent ?: 0,
                maxValue = parsedMaximum,
                recovery = recoveryText.trim().takeIf { it.isNotEmpty() },
                source = source.trim().takeIf { it.isNotEmpty() },
                notes = notes.trim().takeIf { it.isNotEmpty() },
                pinned = existing?.pinned ?: true,
                sortOrder = existing?.sortOrder ?: 0,
            )
            val normalizedCadence = cadence
            val normalizedAmount = if (normalizedCadence == CharacterRecoveryCadence.MANUAL || normalizedCadence == CharacterRecoveryCadence.NONE) {
                CharacterRecoveryAmountMode.NONE
            } else {
                amountMode
            }
            val rule = if (
                normalizedCadence == CharacterRecoveryCadence.NONE &&
                normalizedAmount == CharacterRecoveryAmountMode.NONE &&
                recoveryNotes.isBlank()
            ) {
                null
            } else {
                CharacterResourceRecovery(
                    resourceId = resourceId,
                    cadence = normalizedCadence,
                    amountMode = normalizedAmount,
                    fixedAmount = if (normalizedAmount == CharacterRecoveryAmountMode.FIXED) parsedFixed else null,
                    notes = recoveryNotes.trim().takeIf { it.isNotEmpty() },
                )
            }
            onSave(resource, rule)
        },
        saveEnabled = valid,
        supportingText = "La recuperación automática es opcional. Si eliges Manual, el descanso solo mostrará la indicación y nunca cambiará el contador.",
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = current, onValueChange = { current = it.filter(Char::isDigit) }, label = { Text("Actual") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = maximum, onValueChange = { maximum = it.filter(Char::isDigit) }, label = { Text("Máximo opcional") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        CharacterInlineValidationMessage(if (maximum.isNotBlank() && parsedMaximum != null && parsedCurrent != null && parsedMaximum < parsedCurrent) "El máximo no puede ser menor que el valor actual." else null)
        OutlinedTextField(value = source, onValueChange = { source = it }, label = { Text("Fuente opcional") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = recoveryText, onValueChange = { recoveryText = it }, label = { Text("Descripción de recuperación") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        RecoveryCadenceSelectorV4(cadence = cadence, onChange = { cadenceName = it.name })
        if (cadence != CharacterRecoveryCadence.NONE && cadence != CharacterRecoveryCadence.MANUAL) {
            RecoveryAmountSelectorV4(amountMode = amountMode, onChange = { amountModeName = it.name })
            if (amountMode == CharacterRecoveryAmountMode.FIXED) {
                OutlinedTextField(value = fixedAmount, onValueChange = { fixedAmount = it.filter(Char::isDigit) }, label = { Text("Cantidad a recuperar") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
        OutlinedTextField(value = recoveryNotes, onValueChange = { recoveryNotes = it }, label = { Text("Nota del descanso") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas del recurso") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    }
}

@Composable
private fun RecoveryCadenceSelectorV4(cadence: CharacterRecoveryCadence, onChange: (CharacterRecoveryCadence) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Cuándo se recupera", style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(recoveryCadenceLabelV4(cadence)) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CharacterRecoveryCadence.entries.forEach { option ->
                    DropdownMenuItem(text = { Text(recoveryCadenceLabelV4(option)) }, onClick = { onChange(option); expanded = false })
                }
            }
        }
    }
}

@Composable
private fun RecoveryAmountSelectorV4(amountMode: CharacterRecoveryAmountMode, onChange: (CharacterRecoveryAmountMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Cuánto se recupera", style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(recoveryAmountLabelV4(amountMode)) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CharacterRecoveryAmountMode.entries.forEach { option ->
                    DropdownMenuItem(text = { Text(recoveryAmountLabelV4(option)) }, onClick = { onChange(option); expanded = false })
                }
            }
        }
    }
}

@Composable
private fun RestPreviewDialogV4(
    rest: CharacterRestKind,
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    onDismiss: () -> Unit,
    onApply: (Set<Uuid>) -> Unit,
) {
    val preview = remember(rest, sheet.resources, closureState.resourceRecovery) {
        previewResourceRecovery(rest, sheet.resources, closureState.resourceRecovery)
    }
    var selected by rememberSaveable(rest.name, preview.size) {
        mutableStateOf(preview.filter { it.hasAutomaticChange }.map { it.resourceId.toString() }.toSet())
    }

    CharacterImeSafeEditorDialog(
        title = if (rest == CharacterRestKind.SHORT) "Descanso corto" else "Descanso largo",
        onCancel = onDismiss,
        onSave = { onApply(selected.mapNotNull { runCatching { Uuid.parse(it) }.getOrNull() }.toSet()) },
        saveLabel = "Aplicar seleccionados",
        supportingText = "Nada cambia hasta que confirmes. Los elementos marcados como revisión manual son solo informativos.",
    ) {
        if (preview.isEmpty()) {
            Text("No hay recursos con recuperación configurada para este descanso.")
        } else {
            preview.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (item.hasAutomaticChange) {
                        Checkbox(
                            checked = item.resourceId.toString() in selected,
                            onCheckedChange = { checked ->
                                selected = if (checked) selected + item.resourceId.toString() else selected - item.resourceId.toString()
                            },
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.resourceName, style = MaterialTheme.typography.labelLarge)
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
        }
        if (sheet.classes.isNotEmpty()) {
            Text("Dados de Golpe", style = MaterialTheme.typography.titleSmall)
            sheet.classes.forEach { classLevel ->
                Text(
                    "${classLevel.name}: ${classLevel.hitDiceRemaining}/${classLevel.level} d${classLevel.hitDieSides} · revisar manualmente según tus reglas de mesa",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun TemporaryEffectEditorDialogV4(
    existing: CharacterTemporaryEffect?,
    onDismiss: () -> Unit,
    onSave: (CharacterTemporaryEffect) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var summary by rememberSaveable { mutableStateOf(existing?.summary.orEmpty()) }
    var duration by rememberSaveable { mutableStateOf(existing?.durationText.orEmpty()) }
    var source by rememberSaveable { mutableStateOf(existing?.source.orEmpty()) }
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
                    source = source.trim().takeIf { it.isNotEmpty() },
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    active = active,
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = name.trim().isNotEmpty(),
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Resumen / modificador") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duración") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = source, onValueChange = { source = it }, label = { Text("Fuente") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Activo")
            Switch(checked = active, onCheckedChange = { active = it })
        }
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    }
}

@Composable
private fun ReconciliationEditorDialogV4(
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
        supportingText = "Registra que la ficha digital quedó reconciliada intencionalmente con tu referencia física en este momento.",
    ) {
        OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Etiqueta opcional") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas opcionales") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    }
}

private fun recoveryCadenceLabelV4(value: CharacterRecoveryCadence): String = when (value) {
    CharacterRecoveryCadence.NONE -> "Sin automatizar"
    CharacterRecoveryCadence.SHORT_REST -> "Descanso corto"
    CharacterRecoveryCadence.LONG_REST -> "Descanso largo"
    CharacterRecoveryCadence.SHORT_OR_LONG_REST -> "Descanso corto o largo"
    CharacterRecoveryCadence.MANUAL -> "Manual / solo referencia"
}

private fun recoveryAmountLabelV4(value: CharacterRecoveryAmountMode): String = when (value) {
    CharacterRecoveryAmountMode.NONE -> "Revisión manual"
    CharacterRecoveryAmountMode.TO_MAX -> "Hasta el máximo"
    CharacterRecoveryAmountMode.FIXED -> "Cantidad fija"
}

private fun formatCheckpointTimeV4(epochSeconds: Long): String = runCatching {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).format(formatter)
}.getOrElse { "—" }
