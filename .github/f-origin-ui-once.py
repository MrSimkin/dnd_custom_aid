from pathlib import Path


def replace_once(path, old, new):
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"guard failed for {path}: expected one occurrence, got {count}\n--- needle ---\n{old[:500]}")
    p.write_text(text.replace(old, new, 1))

# 1) Draft codec: preserve typed source origin metadata and stage spellcasting profiles.
codec = 'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellcastingDraftCodecV4.kt'
replace_once(codec,
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource\n''',
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource\n''')
replace_once(codec,
'''                .put("name", source.name)\n                .put("linkedClassId", source.linkedClassId?.toString() ?: JSONObject.NULL)\n                .put("sortOrder", index),\n''',
'''                .put("name", source.name)\n                .put("linkedClassId", source.linkedClassId?.toString() ?: JSONObject.NULL)\n                .put("originKind", source.originKind.name)\n                .put("originReferenceId", source.originReferenceId?.toString() ?: JSONObject.NULL)\n                .put("sortOrder", index),\n''')
replace_once(codec,
'''            add(\n                CharacterSpellcastingSource(\n                    id = runCatching { Uuid.parse(item.getString("id")) }.getOrElse { Uuid.random() },\n                    name = item.optString("name", ""),\n                    linkedClassId = if (item.isNull("linkedClassId")) {\n                        null\n                    } else {\n                        runCatching { Uuid.parse(item.getString("linkedClassId")) }.getOrNull()\n                    },\n                    sortOrder = index,\n                ),\n            )\n''',
'''            val linkedClassId = if (item.isNull("linkedClassId")) {\n                null\n            } else {\n                runCatching { Uuid.parse(item.getString("linkedClassId")) }.getOrNull()\n            }\n            val originKind = item.optString("originKind", "")\n                .takeIf { it.isNotBlank() }\n                ?.let { runCatching { CharacterSpellcastingOriginKind.valueOf(it) }.getOrNull() }\n                ?: if (linkedClassId != null) CharacterSpellcastingOriginKind.CLASS else CharacterSpellcastingOriginKind.OTHER\n            val originReferenceId = if (item.isNull("originReferenceId")) {\n                null\n            } else {\n                runCatching { Uuid.parse(item.getString("originReferenceId")) }.getOrNull()\n            }\n            add(\n                CharacterSpellcastingSource(\n                    id = runCatching { Uuid.parse(item.getString("id")) }.getOrElse { Uuid.random() },\n                    name = item.optString("name", ""),\n                    linkedClassId = linkedClassId,\n                    sortOrder = index,\n                    originKind = originKind,\n                    originReferenceId = originReferenceId,\n                ),\n            )\n''')
append = r'''

internal fun characterSpellcastingProfilesToJsonV4(profiles: List<CharacterSpellcastingProfile>): String {
    val array = JSONArray()
    profiles.forEach { profile ->
        array.put(
            JSONObject()
                .put("sourceId", profile.sourceId.toString())
                .put("abilityBuiltIn", profile.ability.builtIn?.name ?: JSONObject.NULL)
                .put("abilityCustomAttributeId", profile.ability.customAttributeId?.toString() ?: JSONObject.NULL)
                .put("saveDcAdjustment", profile.saveDcAdjustment)
                .put("spellAttackAdjustment", profile.spellAttackAdjustment)
                .put("legacySaveDcOverride", profile.legacySaveDcOverride ?: JSONObject.NULL)
                .put("legacySpellAttackOverride", profile.legacySpellAttackOverride ?: JSONObject.NULL),
        )
    }
    return array.toString()
}

internal fun characterSpellcastingProfilesFromJsonV4(raw: String): List<CharacterSpellcastingProfile> = runCatching {
    val array = JSONArray(raw)
    buildList {
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val sourceId = runCatching { Uuid.parse(item.getString("sourceId")) }.getOrNull() ?: continue
            val builtIn = if (item.isNull("abilityBuiltIn")) null else {
                runCatching { CharacterAbility.valueOf(item.getString("abilityBuiltIn")) }.getOrNull()
            }
            val customAttributeId = if (item.isNull("abilityCustomAttributeId")) null else {
                runCatching { Uuid.parse(item.getString("abilityCustomAttributeId")) }.getOrNull()
            }
            add(
                CharacterSpellcastingProfile(
                    sourceId = sourceId,
                    ability = CharacterAbilityReference(builtIn = builtIn, customAttributeId = customAttributeId),
                    saveDcAdjustment = item.optInt("saveDcAdjustment", 0),
                    spellAttackAdjustment = item.optInt("spellAttackAdjustment", 0),
                    legacySaveDcOverride = if (item.isNull("legacySaveDcOverride")) null else item.optInt("legacySaveDcOverride"),
                    legacySpellAttackOverride = if (item.isNull("legacySpellAttackOverride")) null else item.optInt("legacySpellAttackOverride"),
                ),
            )
        }
    }
}.getOrDefault(emptyList())
'''
Path(codec).write_text(Path(codec).read_text() + append)

# 2) New typed origin editor. Only OTHER exposes custom free text.
Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellSourceEditorV4.kt').write_text(r'''package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
                modifier = Modifier.fillMaxWidth(),
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
                    modifier = modifier,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            },
            second = { modifier ->
                OutlinedTextField(
                    value = attackAdjustmentText,
                    onValueChange = { attackAdjustmentText = normalizeCharacterSignedIntegerInput(it) },
                    label = { Text("Ajuste ataque") },
                    modifier = modifier,
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

private fun spellOriginAbilityKeyV4(reference: CharacterAbilityReference): String = when {
    reference.builtIn != null -> "BUILTIN:${reference.builtIn.name}"
    reference.customAttributeId != null -> "CUSTOM:${reference.customAttributeId}"
    else -> "NONE"
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
''')

# 3) Spells tab: replace arbitrary name/class editor with typed origin editor + profile callback.
spells = 'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellsTabV4.kt'
replace_once(spells,
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterGeneralSpellcastingRow\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState\n''',
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterGeneralSpellcastingRow\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait\n''')
replace_once(spells,
'''    successorState: CharacterSuccessorState,\n    slotStates: List<CharacterSpellSlotUiV4>,\n    classOptions: List<SpellSourceClassOptionV4>,\n    closureState: CharacterClosureState,\n''',
'''    successorState: CharacterSuccessorState,\n    projectionSheet: CharacterSheet,\n    spellcastingProfiles: List<CharacterSpellcastingProfile>,\n    slotStates: List<CharacterSpellSlotUiV4>,\n    classOptions: List<SpellSourceClassOptionV4>,\n    traits: List<CharacterTrait>,\n    inventoryItems: List<CharacterInventoryItem>,\n    background: CharacterBackground,\n    closureState: CharacterClosureState,\n''')
replace_once(spells,
'''    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n''',
'''    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,\n    onSpellcastingProfilesChange: (List<CharacterSpellcastingProfile>) -> Unit,\n    structuralEditingEnabled: Boolean,\n''')
replace_once(spells,
'''    var editingSourceId by rememberSaveable("spell-source-edit-id") { mutableStateOf<String?>(null) }\n    var editorName by rememberSaveable("spell-source-edit-name") { mutableStateOf("") }\n    var editorLinkedClassId by rememberSaveable("spell-source-edit-class") { mutableStateOf<String?>(null) }\n    var deleteSourceId by rememberSaveable("spell-source-delete-id") { mutableStateOf<String?>(null) }\n''',
'''    var editingSourceId by rememberSaveable("spell-source-edit-id") { mutableStateOf<String?>(null) }\n    var deleteSourceId by rememberSaveable("spell-source-delete-id") { mutableStateOf<String?>(null) }\n''')
replace_once(spells,
'''    fun beginAddSource() {\n        editingSourceId = null\n        editorName = ""\n        editorLinkedClassId = null\n        managerOpen = false\n        editorOpen = true\n    }\n\n    fun beginEditSource(source: CharacterSpellcastingSource) {\n        editingSourceId = source.id.toString()\n        editorName = source.name\n        editorLinkedClassId = source.linkedClassId?.toString()\n        managerOpen = false\n        editorOpen = true\n    }\n''',
'''    fun beginAddSource() {\n        editingSourceId = null\n        managerOpen = false\n        editorOpen = true\n    }\n\n    fun beginEditSource(source: CharacterSpellcastingSource) {\n        editingSourceId = source.id.toString()\n        managerOpen = false\n        editorOpen = true\n    }\n''')
replace_once(spells,
'''        SourceManagerDialogV4(\n            sources = draft.sources,\n            classOptions = classOptions,\n''',
'''        SourceManagerDialogV4(\n            sources = draft.sources,\n''')
old_editor = '''    if (editorOpen && structuralEditingEnabled) {\n        SourceEditorDialogV4(\n            title = if (editingSourceId == null) "Añadir fuente" else "Editar fuente",\n            name = editorName,\n            linkedClassId = editorLinkedClassId,\n            classOptions = classOptions,\n            onNameChange = { editorName = it },\n            onLinkedClassChange = { editorLinkedClassId = it },\n            onCancel = {\n                editorOpen = false\n                managerOpen = true\n            },\n            onApply = {\n                val existing = editingSourceId?.let { id ->\n                    draft.sources.firstOrNull { it.id.toString() == id }\n                }\n                val linkedClass = editorLinkedClassId?.let { id ->\n                    classOptions.firstOrNull { it.id.toString() == id }?.id\n                }\n                val source = CharacterSpellcastingSource(\n                    id = existing?.id ?: Uuid.random(),\n                    name = editorName.trim(),\n                    linkedClassId = linkedClass,\n                    sortOrder = existing?.sortOrder ?: draft.sources.size,\n                )\n                val updated = if (existing == null) {\n                    draft.sources + source\n                } else {\n                    draft.sources.map { if (it.id == existing.id) source else it }\n                }\n                updateSources(updated)\n                editorOpen = false\n                managerOpen = true\n            },\n        )\n    }\n'''
new_editor = '''    if (editorOpen && structuralEditingEnabled) {\n        val existing = editingSourceId?.let { id ->\n            draft.sources.firstOrNull { it.id.toString() == id }\n        }\n        val existingProfile = existing?.let { source ->\n            spellcastingProfiles.firstOrNull { it.sourceId == source.id }\n        }\n        CharacterSpellSourceEditorV4(\n            title = if (existing == null) "Añadir origen de conjuros" else "Editar origen de conjuros",\n            existing = existing,\n            existingProfile = existingProfile,\n            classOptions = classOptions,\n            traits = traits,\n            inventoryItems = inventoryItems,\n            background = background,\n            customAttributes = successorState.customAttributes,\n            projectionSheet = projectionSheet,\n            successorState = successorState,\n            nextSortOrder = draft.sources.size,\n            onCancel = {\n                editorOpen = false\n                managerOpen = true\n            },\n            onApply = { source, profile ->\n                val updated = if (existing == null) {\n                    draft.sources + source\n                } else {\n                    draft.sources.map { if (it.id == existing.id) source else it }\n                }\n                updateSources(updated)\n                onSpellcastingProfilesChange(\n                    spellcastingProfiles.filterNot { it.sourceId == source.id } + profile,\n                )\n                editorOpen = false\n                managerOpen = true\n            },\n        )\n    }\n'''
replace_once(spells, old_editor, new_editor)
replace_once(spells,
'''                    onDraftChange(\n                        draft.copy(\n                            sources = remainingSources,\n                            spells = remainingSpells,\n                        ),\n                    )\n''',
'''                    onDraftChange(\n                        draft.copy(\n                            sources = remainingSources,\n                            spells = remainingSpells,\n                        ),\n                    )\n                    onSpellcastingProfilesChange(\n                        spellcastingProfiles.filterNot { it.sourceId == target.id },\n                    )\n''')
replace_once(spells, 'text = { Text("Gestionar fuentes…") },', 'text = { Text("Gestionar orígenes…") },')
replace_once(spells,
'''private fun SourceManagerDialogV4(\n    sources: List<CharacterSpellcastingSource>,\n    classOptions: List<SpellSourceClassOptionV4>,\n''',
'''private fun SourceManagerDialogV4(\n    sources: List<CharacterSpellcastingSource>,\n''')
replace_once(spells, 'title = { Text("Fuentes de conjuros") },', 'title = { Text("Orígenes de conjuros") },')
replace_once(spells,
'''                        "Las fuentes organizan una sola colección de conjuros. Pueden vincularse opcionalmente a una clase o ser completamente personalizadas.",\n''',
'''                        "Cada origen organiza una sola colección de conjuros y conserva su propia aptitud, CD y ataque de lanzamiento.",\n''')
replace_once(spells, 'TextButton(onClick = onAdd) { Text("Añadir fuente") }', 'TextButton(onClick = onAdd) { Text("Añadir origen") }')
replace_once(spells,
'''                        SourceManagerRowV4(\n                            source = source,\n                            classOptions = classOptions,\n''',
'''                        SourceManagerRowV4(\n                            source = source,\n''')
replace_once(spells,
'''private fun SourceManagerRowV4(\n    source: CharacterSpellcastingSource,\n    classOptions: List<SpellSourceClassOptionV4>,\n''',
'''private fun SourceManagerRowV4(\n    source: CharacterSpellcastingSource,\n''')
replace_once(spells,
'''    val linkedClassName = source.linkedClassId?.let { linkedId ->\n        classOptions.firstOrNull { it.id == linkedId }?.name?.ifBlank { "Clase sin nombre" }\n    }\n\n''',
'''\n''')
replace_once(spells,
'''                Text(\n                    linkedClassName?.let { "Clase vinculada: $it" } ?: "Fuente personalizada / sin clase vinculada",\n                    style = MaterialTheme.typography.labelSmall,\n                )\n''',
'''                Text(\n                    "Origen: ${spellSourceOriginLabelV4(source.originKind)}",\n                    style = MaterialTheme.typography.labelSmall,\n                )\n''')
# Remove obsolete old source editor function wholesale.
start = Path(spells).read_text().find('\n@Composable\nprivate fun SourceEditorDialogV4(')
if start < 0:
    raise SystemExit('old SourceEditorDialogV4 not found')
text = Path(spells).read_text()
Path(spells).write_text(text[:start].rstrip() + '\n')

# 4) Editor: stage profile state alongside source/spell draft so Save/Discard remain atomic.
editor = 'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt'
replace_once(editor,
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus\n''',
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus\n''')
replace_once(editor,
'''    var combatDamageDraftJson by rememberSaveable(characterId.toString(), "combat-damage") {\n        mutableStateOf(characterCombatDamageProfilesToJsonV4(successorState.combatDamage))\n    }\n''',
'''    var combatDamageDraftJson by rememberSaveable(characterId.toString(), "combat-damage") {\n        mutableStateOf(characterCombatDamageProfilesToJsonV4(successorState.combatDamage))\n    }\n    var spellcastingProfilesDraftJson by rememberSaveable(characterId.toString(), "spellcasting-profiles") {\n        mutableStateOf(characterSpellcastingProfilesToJsonV4(successorState.spellcastingProfiles))\n    }\n''')
replace_once(editor,
'''    val combatDamageProfiles = remember(combatDamageDraftJson) {\n        characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)\n    }\n''',
'''    val combatDamageProfiles = remember(combatDamageDraftJson) {\n        characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)\n    }\n    val spellcastingProfiles = remember(spellcastingProfilesDraftJson) {\n        characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson)\n    }\n    val projectedSuccessorState = remember(successorState, spellcastingProfiles) {\n        successorState.copy(spellcastingProfiles = spellcastingProfiles)\n    }\n''')
replace_once(editor,
'''    val storedCombatDamageDraftJson = remember(successorState.combatDamage) {\n        characterCombatDamageProfilesToJsonV4(successorState.combatDamage)\n    }\n''',
'''    val storedCombatDamageDraftJson = remember(successorState.combatDamage) {\n        characterCombatDamageProfilesToJsonV4(successorState.combatDamage)\n    }\n    val storedSpellcastingProfilesDraftJson = remember(successorState.spellcastingProfiles) {\n        characterSpellcastingProfilesToJsonV4(successorState.spellcastingProfiles)\n    }\n''')
replace_once(editor,
'''            combatDamageDraftJson != storedCombatDamageDraftJson ||\n            equipmentDraftJson != storedEquipmentDraftJson ||\n''',
'''            combatDamageDraftJson != storedCombatDamageDraftJson ||\n            spellcastingProfilesDraftJson != storedSpellcastingProfilesDraftJson ||\n            equipmentDraftJson != storedEquipmentDraftJson ||\n''')
replace_once(editor,
'''    fun updateCombatDamageProfiles(updated: List<CharacterCombatDamageProfile>) {\n        if (!structuralEditingEnabled) return\n        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(updated)\n        savedMessage = null\n    }\n''',
'''    fun updateCombatDamageProfiles(updated: List<CharacterCombatDamageProfile>) {\n        if (!structuralEditingEnabled) return\n        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(updated)\n        savedMessage = null\n    }\n\n    fun updateSpellcastingProfiles(updated: List<CharacterSpellcastingProfile>) {\n        if (!structuralEditingEnabled) return\n        spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(updated)\n        savedMessage = null\n    }\n''')
replace_once(editor,
'''        val savedDamageProfiles = characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)\n            .filter { it.combatEntryId in liveCombatEntryIds }\n        pcSettingsContext?.onSuccessorStateChange?.invoke(\n            successorState.copy(combatDamage = savedDamageProfiles),\n        )\n        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(savedDamageProfiles)\n''',
'''        val savedDamageProfiles = characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)\n            .filter { it.combatEntryId in liveCombatEntryIds }\n        val liveSpellSourceIds = stored.spellcastingSources.mapTo(mutableSetOf()) { it.id }\n        val savedSpellcastingProfiles = characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson)\n            .filter { it.sourceId in liveSpellSourceIds }\n        pcSettingsContext?.onSuccessorStateChange?.invoke(\n            successorState.copy(\n                combatDamage = savedDamageProfiles,\n                spellcastingProfiles = savedSpellcastingProfiles,\n            ),\n        )\n        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(savedDamageProfiles)\n        spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(savedSpellcastingProfiles)\n''')
replace_once(editor,
'''                            spellcastingRows = overviewProjectionSheet.generalSpellcastingRows(successorState),\n                            successorState = successorState,\n                            slotStates = draft.spellSlots.map { slot ->\n''',
'''                            spellcastingRows = overviewProjectionSheet.generalSpellcastingRows(projectedSuccessorState),\n                            successorState = projectedSuccessorState,\n                            projectionSheet = overviewProjectionSheet,\n                            spellcastingProfiles = spellcastingProfiles,\n                            slotStates = draft.spellSlots.map { slot ->\n''')
replace_once(editor,
'''                            classOptions = draft.classes.map { SpellSourceClassOptionV4(it.id, it.name) },\n                            closureState = closureState,\n''',
'''                            classOptions = draft.classes.map { SpellSourceClassOptionV4(it.id, it.name) },\n                            traits = traitsDraft,\n                            inventoryItems = equipmentDraft.items,\n                            background = backgroundDraft,\n                            closureState = closureState,\n''')
replace_once(editor,
'''                            onDraftChange = ::updateSpellcasting,\n                            structuralEditingEnabled = structuralEditingEnabled,\n''',
'''                            onDraftChange = ::updateSpellcasting,\n                            onSpellcastingProfilesChange = ::updateSpellcastingProfiles,\n                            structuralEditingEnabled = structuralEditingEnabled,\n''')

# Ensure staged profiles reset from the durable state after any successful save path.
replace_once(editor,
'''        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(\n            CharacterSpellcastingDraftV4(\n                sources = stored.spellcastingSources,\n                spells = stored.spells,\n            ),\n        )\n''',
'''        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(\n            CharacterSpellcastingDraftV4(\n                sources = stored.spellcastingSources,\n                spells = stored.spells,\n            ),\n        )\n        spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(savedSpellcastingProfiles)\n''')

print('Origin editor/profile staging transformation complete')
