package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomAttribute
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterSignedIntegerInput
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
import kotlin.uuid.Uuid

@Composable
internal fun CharacterSpellSourceEditorV4(
    title: String,
    existing: CharacterSpellcastingSource?,
    existingProfile: CharacterSpellcastingProfile?,
    classOptions: List<SpellSourceClassOptionV4>,
    traits: List<CharacterTrait>,
    inventoryItems: List<CharacterInventoryItem>,
    background: CharacterBackground,
    customAttributes: List<CharacterCustomAttribute>,
    projectionSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    nextSortOrder: Int,
    onCancel: () -> Unit,
    onApply: (CharacterSpellcastingSource, CharacterSpellcastingProfile) -> Unit,
) {
    val sourceIdText = rememberSaveable(existing?.id?.toString(), "spell-origin-source-id") {
        mutableStateOf(existing?.id?.toString() ?: Uuid.random().toString())
    }
    val sourceId = runCatching { Uuid.parse(sourceIdText.value) }.getOrElse { existing?.id ?: Uuid.random() }
    var originKindName by rememberSaveable(existing?.id?.toString(), "spell-origin-kind") {
        mutableStateOf((existing?.originKind ?: CharacterSpellcastingOriginKind.CLASS).name)
    }
    var referenceIdText by rememberSaveable(existing?.id?.toString(), "spell-origin-reference") {
        val initial = when (existing?.originKind) {
            CharacterSpellcastingOriginKind.CLASS -> existing.linkedClassId
            else -> existing?.originReferenceId
        }?.toString() ?: if (existing == null && classOptions.size == 1) classOptions.single().id.toString() else ""
        mutableStateOf(initial)
    }
    var otherText by rememberSaveable(existing?.id?.toString(), "spell-origin-other") {
        mutableStateOf(if (existing?.originKind == CharacterSpellcastingOriginKind.OTHER) existing.name else "")
    }
    var abilityKey by rememberSaveable(existing?.id?.toString(), "spell-origin-ability") {
        mutableStateOf(spellOriginAbilityKeyV4(existingProfile?.ability ?: CharacterAbilityReference.NONE))
    }
    var saveAdjustmentText by rememberSaveable(existing?.id?.toString(), "spell-origin-save-adjust") {
        mutableStateOf((existingProfile?.saveDcAdjustment ?: 0).toString())
    }
    var attackAdjustmentText by rememberSaveable(existing?.id?.toString(), "spell-origin-attack-adjust") {
        mutableStateOf((existingProfile?.spellAttackAdjustment ?: 0).toString())
    }

    val originKind = runCatching { CharacterSpellcastingOriginKind.valueOf(originKindName) }
        .getOrDefault(CharacterSpellcastingOriginKind.CLASS)
    val referenceId = referenceIdText.takeIf { it.isNotBlank() }?.let { runCatching { Uuid.parse(it) }.getOrNull() }
    val availableTraits = when (originKind) {
        CharacterSpellcastingOriginKind.FEAT -> traits.filter { it.type == CharacterTraitType.FEAT }
        CharacterSpellcastingOriginKind.GIFT -> traits.filter { it.type == CharacterTraitType.GIFT_BLESSING }
        CharacterSpellcastingOriginKind.TRAIT -> traits.filter {
            it.type != CharacterTraitType.FEAT && it.type != CharacterTraitType.GIFT_BLESSING
        }
        else -> emptyList()
    }
    val sourceName = when (originKind) {
        CharacterSpellcastingOriginKind.CLASS -> classOptions.firstOrNull { it.id == referenceId }?.name.orEmpty().trim()
        CharacterSpellcastingOriginKind.TRAIT,
        CharacterSpellcastingOriginKind.FEAT,
        CharacterSpellcastingOriginKind.GIFT,
        -> availableTraits.firstOrNull { it.id == referenceId }?.name.orEmpty().trim()
        CharacterSpellcastingOriginKind.RACE -> background.race.trim()
        CharacterSpellcastingOriginKind.BACKGROUND -> background.name.trim()
        CharacterSpellcastingOriginKind.ITEM,
        CharacterSpellcastingOriginKind.MAGIC_ITEM,
        -> inventoryItems.firstOrNull { it.id == referenceId }?.name.orEmpty().trim()
        CharacterSpellcastingOriginKind.OTHER -> otherText.trim()
    }
    val ability = spellOriginAbilityFromKeyV4(abilityKey)
    val saveAdjustment = saveAdjustmentText.toIntOrNull()
    val attackAdjustment = attackAdjustmentText.toIntOrNull()
    val profile = CharacterSpellcastingProfile(
        sourceId = sourceId,
        ability = ability,
        saveDcAdjustment = saveAdjustment ?: 0,
        spellAttackAdjustment = attackAdjustment ?: 0,
        legacySaveDcOverride = null,
        legacySpellAttackOverride = null,
    )
    val previewState = successorState.copy(
        spellcastingProfiles = successorState.spellcastingProfiles.filterNot { it.sourceId == sourceId } + profile,
    )
    val previewDc = if (ability.configured && saveAdjustment != null) projectionSheet.spellSaveDc(profile, previewState) else null
    val previewAttack = if (ability.configured && attackAdjustment != null) {
        projectionSheet.spellAttackModifier(profile, previewState)
    } else null
    val originReady = sourceName.isNotBlank() && when (originKind) {
        CharacterSpellcastingOriginKind.CLASS,
        CharacterSpellcastingOriginKind.TRAIT,
        CharacterSpellcastingOriginKind.FEAT,
        CharacterSpellcastingOriginKind.ITEM,
        CharacterSpellcastingOriginKind.MAGIC_ITEM,
        CharacterSpellcastingOriginKind.GIFT,
        -> referenceId != null
        else -> true
    }
    val saveEnabled = originReady && ability.configured && saveAdjustment != null && attackAdjustment != null

    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onCancel,
        onSave = {
            val source = CharacterSpellcastingSource(
                id = sourceId,
                name = sourceName,
                linkedClassId = if (originKind == CharacterSpellcastingOriginKind.CLASS) referenceId else null,
                sortOrder = existing?.sortOrder ?: nextSortOrder,
                originKind = originKind,
                originReferenceId = when (originKind) {
                    CharacterSpellcastingOriginKind.TRAIT,
                    CharacterSpellcastingOriginKind.FEAT,
                    CharacterSpellcastingOriginKind.ITEM,
                    CharacterSpellcastingOriginKind.MAGIC_ITEM,
                    CharacterSpellcastingOriginKind.GIFT,
                    -> referenceId
                    else -> null
                },
            )
            onApply(source, profile)
        },
        saveEnabled = saveEnabled,
    ) {
        SpellOriginDropdownV4(
            label = "Origen",
            current = spellSourceOriginLabelV4(originKind),
            options = CharacterSpellcastingOriginKind.entries.map { it.name to spellSourceOriginLabelV4(it) },
            onSelect = { selected ->
                originKindName = selected
                referenceIdText = if (selected == CharacterSpellcastingOriginKind.CLASS.name && classOptions.size == 1) {
                    classOptions.single().id.toString()
                } else {
                    ""
                }
            },
        )

        when (originKind) {
            CharacterSpellcastingOriginKind.CLASS -> SpellOriginDropdownV4(
                label = "Clase",
                current = classOptions.firstOrNull { it.id == referenceId }?.name ?: "Seleccionar clase",
                options = classOptions.map { it.id.toString() to it.name },
                onSelect = { referenceIdText = it },
            )
            CharacterSpellcastingOriginKind.TRAIT,
            CharacterSpellcastingOriginKind.FEAT,
            CharacterSpellcastingOriginKind.GIFT,
            -> SpellOriginDropdownV4(
                label = spellSourceOriginLabelV4(originKind),
                current = availableTraits.firstOrNull { it.id == referenceId }?.name ?: "Seleccionar ${spellSourceOriginLabelV4(originKind).lowercase()}",
                options = availableTraits.map { it.id.toString() to it.name },
                onSelect = { referenceIdText = it },
            )
            CharacterSpellcastingOriginKind.ITEM,
            CharacterSpellcastingOriginKind.MAGIC_ITEM,
            -> SpellOriginDropdownV4(
                label = spellSourceOriginLabelV4(originKind),
                current = inventoryItems.firstOrNull { it.id == referenceId }?.name ?: "Seleccionar objeto",
                options = inventoryItems.map { it.id.toString() to it.name },
                onSelect = { referenceIdText = it },
            )
            CharacterSpellcastingOriginKind.RACE -> SpellOriginReadOnlyV4("Raza", background.race, "Configura Raza en Trasfondo antes de usar este origen.")
            CharacterSpellcastingOriginKind.BACKGROUND -> SpellOriginReadOnlyV4("Trasfondo", background.name, "Configura Trasfondo antes de usar este origen.")
            CharacterSpellcastingOriginKind.OTHER -> OutlinedTextField(
                value = otherText,
                onValueChange = { otherText = it },
                label = { Text("Especificar origen") },
                modifier = Modifier.fillMaxWidth().heightIn(min = characterCompactSingleLineFieldHeightV4()),
                singleLine = true,
            )
        }

        Text("Lanzamiento de conjuros", style = MaterialTheme.typography.titleSmall)
        SpellOriginDropdownV4(
            label = "Aptitud",
            current = spellOriginAbilityLabelV4(ability, customAttributes),
            options = buildList {
                CharacterAbility.entries.forEach { add("BUILTIN:${it.name}" to spellOriginBuiltInAbilityLabelV4(it)) }
                customAttributes.sortedBy { it.sortOrder }.forEach { attribute ->
                    add("CUSTOM:${attribute.id}" to "${attribute.name} (${attribute.abbreviation})")
                }
            },
            onSelect = { abilityKey = it },
        )
        CharacterCompactFieldRowV4(
            first = { modifier ->
                OutlinedTextField(
                    value = saveAdjustmentText,
                    onValueChange = { saveAdjustmentText = normalizeCharacterSignedIntegerInput(it) },
                    label = { Text("Ajuste CD") },
                    modifier = modifier.heightIn(min = characterCompactSingleLineFieldHeightV4()),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            },
            second = { modifier ->
                OutlinedTextField(
                    value = attackAdjustmentText,
                    onValueChange = { attackAdjustmentText = normalizeCharacterSignedIntegerInput(it) },
                    label = { Text("Ajuste ataque") },
                    modifier = modifier.heightIn(min = characterCompactSingleLineFieldHeightV4()),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            },
        )
        Text(
            "CD ${previewDc ?: "—"} · Ataque ${previewAttack?.spellOriginSignedV4() ?: "—"}",
            style = MaterialTheme.typography.bodySmall,
        )
        CharacterHelpV4(
            "La CD se calcula como 8 + modificador de Aptitud + competencia + Ajuste CD. El ataque usa modificador de Aptitud + competencia + Ajuste ataque.",
        )
        CharacterInlineValidationMessage(
            when {
                !originReady -> "Selecciona o completa el origen de conjuros."
                !ability.configured -> "Selecciona la aptitud de lanzamiento."
                saveAdjustment == null || attackAdjustment == null -> "Los ajustes deben ser números enteros."
                else -> null
            },
        )
    }
}

@Composable
private fun SpellOriginDropdownV4(
    label: String,
    current: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(
                onClick = { open = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = options.isNotEmpty(),
            ) { Text(if (current.isBlank()) "Sin opciones" else current, maxLines = 1) }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            onSelect(key)
                            open = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SpellOriginReadOnlyV4(label: String, value: String, missingMessage: String) {
    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value.ifBlank { "Sin configurar" }, style = MaterialTheme.typography.bodyMedium)
        if (value.isBlank()) CharacterInlineValidationMessage(missingMessage)
    }
}

internal fun spellSourceOriginLabelV4(kind: CharacterSpellcastingOriginKind): String = when (kind) {
    CharacterSpellcastingOriginKind.CLASS -> "Clase"
    CharacterSpellcastingOriginKind.TRAIT -> "Rasgo"
    CharacterSpellcastingOriginKind.RACE -> "Raza"
    CharacterSpellcastingOriginKind.BACKGROUND -> "Trasfondo"
    CharacterSpellcastingOriginKind.FEAT -> "Dote"
    CharacterSpellcastingOriginKind.ITEM -> "Objeto"
    CharacterSpellcastingOriginKind.MAGIC_ITEM -> "Objeto mágico"
    CharacterSpellcastingOriginKind.GIFT -> "Don"
    CharacterSpellcastingOriginKind.OTHER -> "Otro"
}

private fun spellOriginAbilityKeyV4(reference: CharacterAbilityReference): String {
    val builtIn = reference.builtIn
    if (builtIn != null) return "BUILTIN:${builtIn.name}"
    val customAttributeId = reference.customAttributeId
    if (customAttributeId != null) return "CUSTOM:$customAttributeId"
    return "NONE"
}

private fun spellOriginAbilityFromKeyV4(key: String): CharacterAbilityReference = when {
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

private fun spellOriginAbilityLabelV4(reference: CharacterAbilityReference, customAttributes: List<CharacterCustomAttribute>): String {
    reference.builtIn?.let { return spellOriginBuiltInAbilityLabelV4(it) }
    reference.customAttributeId?.let { id ->
        return customAttributes.firstOrNull { it.id == id }
            ?.let { "${it.name} (${it.abbreviation})" }
            ?: "Característica no disponible"
    }
    return "Seleccionar aptitud"
}

private fun spellOriginBuiltInAbilityLabelV4(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "Fuerza (FUE)"
    CharacterAbility.DEXTERITY -> "Destreza (DES)"
    CharacterAbility.CONSTITUTION -> "Constitución (CON)"
    CharacterAbility.INTELLIGENCE -> "Inteligencia (INT)"
    CharacterAbility.WISDOM -> "Sabiduría (SAB)"
    CharacterAbility.CHARISMA -> "Carisma (CAR)"
}

private fun Int.spellOriginSignedV4(): String = if (this >= 0) "+$this" else toString()
