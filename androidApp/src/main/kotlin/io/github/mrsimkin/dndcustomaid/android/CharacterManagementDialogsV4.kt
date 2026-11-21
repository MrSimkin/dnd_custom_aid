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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConcentration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomMarker
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
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
import io.github.mrsimkin.dndcustomaid.shared.character.previewTrackableRecovery
import kotlin.uuid.Uuid
// Refactor F3: the editing dialogs of the PC's Gestión tab (rest, conditions, concentration, resources, effects, reconciliation), moved out of CharacterManagementSuccessorV4.kt.

@Composable
internal fun SuccessorRestPreviewDialogV4(
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
internal fun RestPreviewRowV4(
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
            CharacterCompactCheckboxV4(
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
internal fun SuccessorConditionEditorDialogV4(
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
            CharacterCompactOutlinedTextFieldV4(
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
        CharacterCompactOutlinedTextFieldV4(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = characterCompactTextAreaMinLinesV4(2),
        )
    }
}

@Composable
internal fun SuccessorConcentrationEditorDialogV4(
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
        CharacterCompactOutlinedTextFieldV4(name, { name = it }, label = { Text("Conjuro o efecto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        CharacterCompactOutlinedTextFieldV4(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
internal fun SuccessorResourceEditorDialogV4(
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
        CharacterCompactOutlinedTextFieldV4(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

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
                CharacterCompactOutlinedTextFieldV4(
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
                    CharacterCompactOutlinedTextFieldV4(
                        currentText,
                        { currentText = it.filter(Char::isDigit) },
                        label = { Text("Actual") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    CharacterCompactOutlinedTextFieldV4(
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
            CharacterCompactOutlinedTextFieldV4(
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
        CharacterCompactOutlinedTextFieldV4(
            recoveryDescription,
            { recoveryDescription = it },
            label = { Text("Descripción de recuperación") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        if (cadence != CharacterRecoveryCadence.NONE) {
            CharacterCompactOutlinedTextFieldV4(
                recoveryNotes,
                { recoveryNotes = it },
                label = { Text("Nota del descanso") },
                modifier = Modifier.fillMaxWidth(),
                minLines = characterCompactTextAreaMinLinesV4(2),
            )
        }
        CharacterCompactOutlinedTextFieldV4(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
internal fun SuccessorTemporaryEffectEditorDialogV4(
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
        CharacterCompactOutlinedTextFieldV4(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        CharacterCompactOutlinedTextFieldV4(summary, { summary = it }, label = { Text("Resumen / modificador") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        CharacterCompactOutlinedTextFieldV4(duration, { duration = it }, label = { Text("Duración") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Activo")
            Switch(checked = active, onCheckedChange = { active = it })
        }
        CharacterCompactOutlinedTextFieldV4(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
internal fun SuccessorReconciliationEditorDialogV4(
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
        CharacterCompactOutlinedTextFieldV4(label, { label = it }, label = { Text("Etiqueta") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        CharacterCompactOutlinedTextFieldV4(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}
