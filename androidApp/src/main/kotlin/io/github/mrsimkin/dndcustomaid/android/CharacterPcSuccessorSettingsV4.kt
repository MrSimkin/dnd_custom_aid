package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomAttribute
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomMarker
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkillAbilityConfiguration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import kotlin.uuid.Uuid

@Composable
internal fun CharacterInspirationVisibilitySettingsV4() {
    val context = LocalCharacterPcSettingsContextV4.current ?: return
    SuccessorSettingCardV4(
        title = "Inspiración",
        description = "Controla si el valor canónico de Inspiración se muestra en la ficha. Ocultarlo no borra su estado.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(if (context.pcConfiguration.inspirationVisible) "Visible" else "Oculta")
            Switch(
                checked = context.pcConfiguration.inspirationVisible,
                onCheckedChange = { visible ->
                    context.onPcConfigurationChange(
                        context.pcConfiguration.copy(inspirationVisible = visible),
                    )
                },
            )
        }
    }
}

@Composable
internal fun CharacterCustomAttributesSettingsV4() {
    val context = LocalCharacterPcSettingsContextV4.current ?: return
    val state = context.successorState
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }
    var blockedDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    SuccessorSettingCardV4(
        title = "Características personalizadas",
        description = "Añade características homebrew con abreviatura, puntuación y salvación opcional.",
    ) {
        SettingsCardHeaderActionV4(
            empty = state.customAttributes.isEmpty(),
            emptyText = "Sin características personalizadas.",
            onAdd = { editorId = null; editorOpen = true },
        )
        state.customAttributes.sortedBy { it.sortOrder }.forEach { attribute ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${attribute.name} (${attribute.abbreviation})", style = MaterialTheme.typography.bodySmall)
                    Text(
                        buildString {
                            append("Puntuación ${attribute.score}")
                            if (attribute.savingThrowEnabled) append(" · Salvación activa")
                        },
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                TextButton(onClick = { editorId = attribute.id.toString(); editorOpen = true }) { Text("Editar") }
                TextButton(onClick = {
                    val referenced = state.customSkillAbilities.any { it.ability.customAttributeId == attribute.id } ||
                        state.spellcastingProfiles.any { it.ability.customAttributeId == attribute.id }
                    if (referenced) blockedDeleteId = attribute.id.toString() else deleteId = attribute.id.toString()
                }) { Text("Eliminar") }
            }
        }
    }

    if (editorOpen) {
        val existing = editorId?.let { id -> state.customAttributes.firstOrNull { it.id.toString() == id } }
        CustomAttributeEditorDialogV4(
            existing = existing,
            onDismiss = { editorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    state.customAttributes + saved.copy(sortOrder = state.customAttributes.size)
                } else {
                    state.customAttributes.map { item ->
                        if (item.id == existing.id) saved.copy(sortOrder = item.sortOrder) else item
                    }
                }
                context.onSuccessorStateChange(state.copy(customAttributes = updated))
                editorOpen = false
            },
        )
    }

    deleteId?.let { id ->
        val target = state.customAttributes.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "característica personalizada",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    context.onSuccessorStateChange(
                        state.copy(
                            customAttributes = state.customAttributes
                                .filterNot { it.id == target.id }
                                .mapIndexed { index, item -> item.copy(sortOrder = index) },
                        ),
                    )
                    deleteId = null
                },
            )
        }
    }

    blockedDeleteId?.let { id ->
        val target = state.customAttributes.firstOrNull { it.id.toString() == id }
        CharacterConfirmationDialog(
            title = "Característica en uso",
            message = "${target?.name ?: "Esta característica"} está asociada a una habilidad personalizada o a una fuente de conjuros. Reasigna esas referencias antes de eliminarla.",
            onDismissRequest = { blockedDeleteId = null },
            onConfirm = { blockedDeleteId = null },
            confirmLabel = "Cerrar",
        )
    }
}

@Composable
internal fun CharacterCustomSkillsSettingsV4(
    closureState: CharacterClosureState,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
    val context = LocalCharacterPcSettingsContextV4.current ?: return
    val successorState = context.successorState
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val orderedSkills = closureState.customSkills.sortedBy { it.sortOrder }
    val skillById = remember(orderedSkills) { orderedSkills.associateBy { it.id.toString() } }
    val canonicalIds = orderedSkills.map { it.id.toString() }
    val coordinator = rememberCharacterReorderCoordinatorV4()
    val haptic = rememberCharacterHapticHookV4(closureState.hapticsEnabled)
    val reorderSession = rememberCharacterReorderSessionV4(
        sessionKey = "pc-custom-skills",
        canonicalOrder = canonicalIds,
        enabled = !closureState.tableModeEnabled,
        coordinator = coordinator,
        onCommitOrder = { committedIds ->
            val currentById = closureState.customSkills.associateBy { it.id.toString() }
            val reordered = committedIds.mapIndexedNotNull { index, id ->
                currentById[id]?.copy(sortOrder = index)
            }
            if (reordered.size == closureState.customSkills.size) {
                onClosureStateChange(closureState.copy(customSkills = reordered))
            }
        },
        onHaptic = haptic,
    )
    val previewSkills = reorderSession.previewOrder.mapNotNull(skillById::get)

    SuccessorSettingCardV4(
        title = "Habilidades personalizadas",
        description = "Administra habilidades homebrew y asígnalas a una característica estándar o personalizada.",
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .characterReorderSessionViewportV4(reorderSession),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
        ) {
            SettingsCardHeaderActionV4(
                empty = closureState.customSkills.isEmpty(),
                emptyText = "Sin habilidades personalizadas.",
                onAdd = { editorId = null; editorOpen = true },
            )
            previewSkills.forEach { skill ->
                val id = skill.id.toString()
                val reference = successorState.customSkillAbilities
                    .firstOrNull { it.customSkillId == skill.id }
                    ?.ability
                    ?: CharacterAbilityReference.builtIn(skill.ability)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .characterReorderSessionBoundsV4(reorderSession, id)
                        .characterReorderSessionSemanticsV4(reorderSession, id)
                        .characterReorderSessionVisualV4(reorderSession, id)
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .characterReorderSessionDragHandleV4(reorderSession, id),
                    ) {
                        Text(skill.name, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${abilityReferenceLabelV4(reference, successorState.customAttributes)} · ${trainingLabelSettingsV4(skill.training)}",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    TextButton(onClick = { editorId = id; editorOpen = true }) { Text("Editar") }
                    TextButton(onClick = { deleteId = id }) { Text("Eliminar") }
                }
            }
        }
    }

    if (editorOpen) {
        val existing = editorId?.let { id -> closureState.customSkills.firstOrNull { it.id.toString() == id } }
        val existingReference = existing?.let { skill ->
            successorState.customSkillAbilities.firstOrNull { it.customSkillId == skill.id }?.ability
                ?: CharacterAbilityReference.builtIn(skill.ability)
        }
        CustomSkillSettingsEditorDialogV4(
            existing = existing,
            existingReference = existingReference,
            customAttributes = successorState.customAttributes,
            onDismiss = { editorOpen = false },
            onSave = { saved, reference ->
                val updatedSkills = if (existing == null) {
                    closureState.customSkills + saved.copy(sortOrder = closureState.customSkills.size)
                } else {
                    closureState.customSkills.map { item ->
                        if (item.id == existing.id) saved.copy(sortOrder = item.sortOrder) else item
                    }
                }
                onClosureStateChange(closureState.copy(customSkills = updatedSkills))
                val updatedMappings = successorState.customSkillAbilities
                    .filterNot { it.customSkillId == saved.id } +
                    CharacterCustomSkillAbilityConfiguration(saved.id, reference)
                context.onSuccessorStateChange(successorState.copy(customSkillAbilities = updatedMappings))
                editorOpen = false
            },
        )
    }

    deleteId?.let { id ->
        val target = closureState.customSkills.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "habilidad personalizada",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onClosureStateChange(
                        closureState.copy(
                            customSkills = closureState.customSkills
                                .filterNot { it.id == target.id }
                                .mapIndexed { index, item -> item.copy(sortOrder = index) },
                        ),
                    )
                    context.onSuccessorStateChange(
                        successorState.copy(
                            customSkillAbilities = successorState.customSkillAbilities
                                .filterNot { it.customSkillId == target.id },
                        ),
                    )
                    deleteId = null
                },
            )
        }
    }
}

@Composable
internal fun CharacterCustomMarkersSettingsV4() {
    val context = LocalCharacterPcSettingsContextV4.current ?: return
    val state = context.successorState
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    SuccessorSettingCardV4(
        title = "Marcadores personalizados",
        description = "Configura contadores, estados binarios o valores actual/máximo. El valor actual se usa después desde la ficha, no desde Ajustes.",
    ) {
        SettingsCardHeaderActionV4(
            empty = state.customMarkers.isEmpty(),
            emptyText = "Sin marcadores personalizados.",
            onAdd = { editorId = null; editorOpen = true },
        )
        state.customMarkers.sortedBy { it.sortOrder }.forEach { marker ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(marker.name, style = MaterialTheme.typography.bodySmall)
                    Text(markerKindLabelV4(marker.valueKind), style = MaterialTheme.typography.labelSmall)
                }
                TextButton(onClick = { editorId = marker.id.toString(); editorOpen = true }) { Text("Editar") }
                TextButton(onClick = { deleteId = marker.id.toString() }) { Text("Eliminar") }
            }
        }
    }

    if (editorOpen) {
        val existing = editorId?.let { id -> state.customMarkers.firstOrNull { it.id.toString() == id } }
        CustomMarkerEditorDialogV4(
            existing = existing,
            onDismiss = { editorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    state.customMarkers + saved.copy(sortOrder = state.customMarkers.size)
                } else {
                    state.customMarkers.map { item ->
                        if (item.id == existing.id) saved.copy(sortOrder = item.sortOrder) else item
                    }
                }
                context.onSuccessorStateChange(state.copy(customMarkers = updated))
                editorOpen = false
            },
        )
    }

    deleteId?.let { id ->
        val target = state.customMarkers.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "marcador personalizado",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    context.onSuccessorStateChange(
                        state.copy(
                            customMarkers = state.customMarkers
                                .filterNot { it.id == target.id }
                                .mapIndexed { index, item -> item.copy(sortOrder = index) },
                        ),
                    )
                    deleteId = null
                },
            )
        }
    }
}

@Composable
internal fun CharacterHapticProfileSettingsV4(
    closureState: CharacterClosureState,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
    val hapticContext = LocalCharacterHapticSettingsV4.current

    SuccessorSettingCardV4(
        title = "Respuesta háptica",
        description = "La activación pertenece a esta ficha. La intensidad y la duración son preferencias del dispositivo y se cambian en Configuración de la aplicación.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(if (closureState.hapticsEnabled) "Activada" else "Desactivada")
            Switch(
                checked = closureState.hapticsEnabled,
                onCheckedChange = { enabled ->
                    onClosureStateChange(closureState.copy(hapticsEnabled = enabled))
                },
            )
        }
        Text(
            "Perfil del dispositivo · Intensidad ${hapticContext.preferences.strength.label} · Duración ${hapticContext.preferences.duration.label}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CustomAttributeEditorDialogV4(
    existing: CharacterCustomAttribute?,
    onDismiss: () -> Unit,
    onSave: (CharacterCustomAttribute) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var abbreviation by rememberSaveable { mutableStateOf(existing?.abbreviation.orEmpty()) }
    var scoreText by rememberSaveable { mutableStateOf(existing?.score?.toString() ?: "10") }
    var saveEnabled by rememberSaveable { mutableStateOf(existing?.savingThrowEnabled ?: false) }
    var saveProficient by rememberSaveable { mutableStateOf(existing?.savingThrowProficient ?: false) }
    var adjustmentText by rememberSaveable { mutableStateOf(existing?.savingThrowAdjustment?.toString() ?: "0") }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    val score = scoreText.toIntOrNull()
    val adjustment = adjustmentText.toIntOrNull()
    val valid = name.trim().isNotEmpty() && abbreviation.trim().isNotEmpty() && score != null && score >= 0 && adjustment != null

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir característica" else "Editar característica",
        onCancel = onDismiss,
        onSave = {
            onSave(
                CharacterCustomAttribute(
                    id = existing?.id ?: Uuid.random(),
                    name = name.trim(),
                    abbreviation = abbreviation.trim(),
                    score = score ?: 10,
                    savingThrowEnabled = saveEnabled,
                    savingThrowProficient = saveEnabled && saveProficient,
                    savingThrowAdjustment = if (saveEnabled) adjustment ?: 0 else 0,
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = valid,
    ) {
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        ) {
            OutlinedTextField(
                abbreviation,
                { abbreviation = it.take(8) },
                label = { Text("Abreviatura") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                scoreText,
                { raw -> scoreText = raw.filter(Char::isDigit) },
                label = { Text("Puntuación") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        SettingsSwitchRowV4("Tirada de salvación", saveEnabled) { saveEnabled = it }
        if (saveEnabled) {
            SettingsSwitchRowV4("Competencia en salvación", saveProficient) { saveProficient = it }
            OutlinedTextField(
                adjustmentText,
                { adjustmentText = sanitizeSignedSettingsIntV4(it) },
                label = { Text("Ajuste adicional de salvación") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
private fun CustomSkillSettingsEditorDialogV4(
    existing: CharacterCustomSkill?,
    existingReference: CharacterAbilityReference?,
    customAttributes: List<CharacterCustomAttribute>,
    onDismiss: () -> Unit,
    onSave: (CharacterCustomSkill, CharacterAbilityReference) -> Unit,
) {
    val initialReference = existingReference ?: CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE)
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var abilityKey by rememberSaveable { mutableStateOf(abilityReferenceKeyV4(initialReference)) }
    var trainingName by rememberSaveable { mutableStateOf((existing?.training ?: SkillTraining.NONE).name) }
    var adjustmentText by rememberSaveable { mutableStateOf(existing?.adjustment?.toString() ?: "0") }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    val reference = abilityReferenceFromKeyV4(abilityKey)
    val training = runCatching { SkillTraining.valueOf(trainingName) }.getOrDefault(SkillTraining.NONE)
    val adjustment = adjustmentText.toIntOrNull()
    val valid = name.trim().isNotEmpty() && reference.configured && adjustment != null

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir habilidad personalizada" else "Editar habilidad personalizada",
        onCancel = onDismiss,
        onSave = {
            val fallbackAbility = reference.builtIn ?: existing?.ability ?: CharacterAbility.INTELLIGENCE
            onSave(
                CharacterCustomSkill(
                    id = existing?.id ?: Uuid.random(),
                    name = name.trim(),
                    ability = fallbackAbility,
                    training = training,
                    adjustment = adjustment ?: 0,
                    source = existing?.source,
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: 0,
                ),
                reference,
            )
        },
        saveEnabled = valid,
    ) {
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        SettingsChoiceDropdownV4(
            label = "Característica",
            current = abilityReferenceLabelV4(reference, customAttributes),
            options = buildList {
                CharacterAbility.entries.forEach { ability -> add("BUILTIN:${ability.name}" to abilitySettingsLabelV4(ability)) }
                customAttributes.sortedBy { it.sortOrder }.forEach { attribute ->
                    add("CUSTOM:${attribute.id}" to "${attribute.name} (${attribute.abbreviation})")
                }
            },
            onSelect = { abilityKey = it },
        )
        SettingsChoiceDropdownV4(
            label = "Entrenamiento",
            current = trainingLabelSettingsV4(training),
            options = SkillTraining.entries.map { it.name to trainingLabelSettingsV4(it) },
            onSelect = { trainingName = it },
        )
        OutlinedTextField(
            adjustmentText,
            { adjustmentText = sanitizeSignedSettingsIntV4(it) },
            label = { Text("Ajuste adicional") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
private fun CustomMarkerEditorDialogV4(
    existing: CharacterCustomMarker?,
    onDismiss: () -> Unit,
    onSave: (CharacterCustomMarker) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var kindName by rememberSaveable { mutableStateOf((existing?.valueKind ?: CharacterTrackableValueKind.COUNTER).name) }
    var maxText by rememberSaveable { mutableStateOf(existing?.maxValue?.toString() ?: "1") }
    var cadenceName by rememberSaveable { mutableStateOf((existing?.recovery?.cadence ?: CharacterRecoveryCadence.NONE).name) }
    var amountModeName by rememberSaveable { mutableStateOf((existing?.recovery?.amountMode ?: CharacterRecoveryAmountMode.NONE).name) }
    var fixedText by rememberSaveable { mutableStateOf(existing?.recovery?.fixedAmount?.toString() ?: "1") }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    val kind = runCatching { CharacterTrackableValueKind.valueOf(kindName) }.getOrDefault(CharacterTrackableValueKind.COUNTER)
    val cadence = runCatching { CharacterRecoveryCadence.valueOf(cadenceName) }.getOrDefault(CharacterRecoveryCadence.NONE)
    val amountMode = runCatching { CharacterRecoveryAmountMode.valueOf(amountModeName) }.getOrDefault(CharacterRecoveryAmountMode.NONE)
    val maximum = maxText.toIntOrNull()
    val fixed = fixedText.toIntOrNull()
    val valid = name.trim().isNotEmpty() &&
        (kind != CharacterTrackableValueKind.CURRENT_MAX || (maximum != null && maximum >= 0)) &&
        (amountMode != CharacterRecoveryAmountMode.FIXED || (fixed != null && fixed >= 0))

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir marcador" else "Editar marcador",
        onCancel = onDismiss,
        onSave = {
            val maxValue = when (kind) {
                CharacterTrackableValueKind.BINARY -> 1
                CharacterTrackableValueKind.COUNTER -> null
                CharacterTrackableValueKind.CURRENT_MAX -> maximum ?: 0
            }
            val current = when (kind) {
                CharacterTrackableValueKind.BINARY -> (existing?.currentValue ?: 0).coerceIn(0, 1)
                CharacterTrackableValueKind.COUNTER -> (existing?.currentValue ?: 0).coerceAtLeast(0)
                CharacterTrackableValueKind.CURRENT_MAX -> (existing?.currentValue ?: 0).coerceIn(0, maxValue ?: 0)
            }
            onSave(
                CharacterCustomMarker(
                    id = existing?.id ?: Uuid.random(),
                    name = name.trim(),
                    valueKind = kind,
                    currentValue = current,
                    maxValue = maxValue,
                    recovery = CharacterTrackableRecovery(
                        cadence = cadence,
                        amountMode = amountMode,
                        fixedAmount = if (amountMode == CharacterRecoveryAmountMode.FIXED) fixed else null,
                    ),
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = valid,
    ) {
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        SettingsChoiceDropdownV4(
            label = "Tipo",
            current = markerKindLabelV4(kind),
            options = CharacterTrackableValueKind.entries.map { it.name to markerKindLabelV4(it) },
            onSelect = { kindName = it },
        )
        if (kind == CharacterTrackableValueKind.CURRENT_MAX) {
            OutlinedTextField(
                maxText,
                { raw -> maxText = raw.filter(Char::isDigit) },
                label = { Text("Máximo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        SettingsChoiceDropdownV4(
            label = "Recuperación",
            current = recoveryCadenceLabelV4(cadence),
            options = CharacterRecoveryCadence.entries.map { it.name to recoveryCadenceLabelV4(it) },
            onSelect = { cadenceName = it },
        )
        SettingsChoiceDropdownV4(
            label = "Cantidad recuperada",
            current = recoveryAmountLabelV4(amountMode),
            options = CharacterRecoveryAmountMode.entries.map { it.name to recoveryAmountLabelV4(it) },
            onSelect = { amountModeName = it },
        )
        if (amountMode == CharacterRecoveryAmountMode.FIXED) {
            OutlinedTextField(
                fixedText,
                { raw -> fixedText = raw.filter(Char::isDigit) },
                label = { Text("Cantidad fija") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = characterCompactTextAreaMinLinesV4(2))
    }
}

@Composable
private fun SettingsChoiceDropdownV4(
    label: String,
    current: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(current) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = { onSelect(key); expanded = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRowV4(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsCardHeaderActionV4(
    empty: Boolean,
    emptyText: String,
    onAdd: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (empty) Text(emptyText, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
        else Text("", modifier = Modifier.weight(1f))
        TextButton(onClick = onAdd) { Text("Añadir") }
    }
}

@Composable
private fun SuccessorSettingCardV4(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(5.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.labelSmall)
            content()
        }
    }
}

private fun abilitySettingsLabelV4(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "Fuerza (FUE)"
    CharacterAbility.DEXTERITY -> "Destreza (DES)"
    CharacterAbility.CONSTITUTION -> "Constitución (CON)"
    CharacterAbility.INTELLIGENCE -> "Inteligencia (INT)"
    CharacterAbility.WISDOM -> "Sabiduría (SAB)"
    CharacterAbility.CHARISMA -> "Carisma (CAR)"
}

private fun abilityReferenceLabelV4(
    reference: CharacterAbilityReference,
    customAttributes: List<CharacterCustomAttribute>,
): String {
    val builtIn = reference.builtIn
    if (builtIn != null) return abilitySettingsLabelV4(builtIn)
    val customAttributeId = reference.customAttributeId
    return if (customAttributeId != null) {
        customAttributes
            .firstOrNull { it.id == customAttributeId }
            ?.let { "${it.name} (${it.abbreviation})" }
            ?: "Característica no disponible"
    } else {
        "Sin configurar"
    }
}

private fun abilityReferenceKeyV4(reference: CharacterAbilityReference): String {
    val builtIn = reference.builtIn
    if (builtIn != null) return "BUILTIN:${builtIn.name}"
    val customAttributeId = reference.customAttributeId
    return if (customAttributeId != null) "CUSTOM:$customAttributeId" else "NONE"
}

private fun abilityReferenceFromKeyV4(key: String): CharacterAbilityReference = when {
    key.startsWith("BUILTIN:") -> key.substringAfter(':')
        .let { runCatching { CharacterAbility.valueOf(it) }.getOrNull() }
        ?.let(CharacterAbilityReference::builtIn)
        ?: CharacterAbilityReference.NONE
    key.startsWith("CUSTOM:") -> key.substringAfter(':')
        .let { runCatching { Uuid.parse(it) }.getOrNull() }
        ?.let(CharacterAbilityReference::custom)
        ?: CharacterAbilityReference.NONE
    else -> CharacterAbilityReference.NONE
}

private fun trainingLabelSettingsV4(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "Sin competencia"
    SkillTraining.PROFICIENT -> "Competente"
    SkillTraining.EXPERTISE -> "Pericia"
}

private fun markerKindLabelV4(kind: CharacterTrackableValueKind): String = when (kind) {
    CharacterTrackableValueKind.BINARY -> "Binario"
    CharacterTrackableValueKind.COUNTER -> "Contador"
    CharacterTrackableValueKind.CURRENT_MAX -> "Actual / máximo"
}

private fun recoveryCadenceLabelV4(cadence: CharacterRecoveryCadence): String = when (cadence) {
    CharacterRecoveryCadence.NONE -> "Sin recuperación automática"
    CharacterRecoveryCadence.SHORT_REST -> "Descanso corto"
    CharacterRecoveryCadence.LONG_REST -> "Descanso largo"
    CharacterRecoveryCadence.SHORT_OR_LONG_REST -> "Descanso corto o largo"
    CharacterRecoveryCadence.MANUAL -> "Manual"
}

private fun recoveryAmountLabelV4(mode: CharacterRecoveryAmountMode): String = when (mode) {
    CharacterRecoveryAmountMode.NONE -> "Sin cantidad"
    CharacterRecoveryAmountMode.TO_MAX -> "Hasta el máximo"
    CharacterRecoveryAmountMode.FIXED -> "Cantidad fija"
}

private fun sanitizeSignedSettingsIntV4(raw: String): String {
    if (raw.isBlank()) return ""
    val sign = raw.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString().orEmpty()
    val digits = raw.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
    return sign + digits
}
