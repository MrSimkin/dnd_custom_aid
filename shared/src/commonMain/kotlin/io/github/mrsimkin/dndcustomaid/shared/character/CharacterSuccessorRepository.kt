package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/**
 * Additive persistence boundary for the Phase 4A successor data model.
 *
 * Existing CharacterRepository / CharacterClosureRepository remain authoritative for their current
 * domains while migration-9 data is introduced. This repository owns only new successor state and
 * projects compatibility state from old fields when no successor row exists yet.
 */
class CharacterSuccessorRepository(
    private val database: AppDatabase,
) {
    fun state(characterId: Uuid): CharacterSuccessorState {
        val sheet = requireNotNull(CharacterRepository(database).character(characterId)) {
            "Character must already exist locally."
        }
        val id = characterId.toString()

        val customAttributes = database.characterSuccessorQueries.selectCustomAttributes(id) {
                rowId, _, name, abbreviation, score, saveEnabled, saveProficient, saveAdjustment, notes, sortOrder ->
            CharacterCustomAttribute(
                id = Uuid.parse(rowId),
                name = name,
                abbreviation = abbreviation,
                score = score.toInt(),
                savingThrowEnabled = saveEnabled != 0L,
                savingThrowProficient = saveProficient != 0L,
                savingThrowAdjustment = saveAdjustment.toInt(),
                notes = notes,
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()
        val customAttributeIds = customAttributes.map { it.id }.toSet()

        val storedProfiles = database.characterSuccessorQueries.selectSpellSourceCasting(id) {
                sourceId, _, abilityKind, abilityValue, saveAdjustment, attackAdjustment,
                legacySaveDc, legacyAttack ->
            val parsedSourceId = parseUuidOrNull(sourceId) ?: return@selectSpellSourceCasting null
            parsedSourceId to CharacterSpellcastingProfile(
                sourceId = parsedSourceId,
                ability = parseAbilityReference(abilityKind, abilityValue, customAttributeIds),
                saveDcAdjustment = saveAdjustment.toInt(),
                spellAttackAdjustment = attackAdjustment.toInt(),
                legacySaveDcOverride = legacySaveDc?.toInt(),
                legacySpellAttackOverride = legacyAttack?.toInt(),
            )
        }.executeAsList().filterNotNull().toMap()

        val sourceIds = sheet.spellcastingSources.map { it.id }.toSet()
        val spellcastingProfiles = sheet.spellcastingSources.map { source ->
            storedProfiles[source.id] ?: legacySpellcastingProfile(source.id, sheet)
        }.filter { it.sourceId in sourceIds }

        val damageRows = database.characterSuccessorQueries.selectCombatDamageComponents(id) {
                combatEntryId, _, componentOrder, componentKind, expression, typeText ->
            val entryId = parseUuidOrNull(combatEntryId) ?: return@selectCombatDamageComponents null
            DamageRow(
                combatEntryId = entryId,
                order = componentOrder.toInt(),
                component = CharacterDamageComponent(
                    kind = enumOrDefault(componentKind, CharacterDamageComponentKind.TEXT),
                    expression = expression,
                    typeText = typeText,
                ),
            )
        }.executeAsList().filterNotNull().groupBy { it.combatEntryId }

        val combatDamage = sheet.combatEntries.map { entry ->
            val persisted = damageRows[entry.id]
                .orEmpty()
                .sortedBy { it.order }
                .map { it.component }
            val components = if (persisted.isNotEmpty()) {
                persisted
            } else {
                entry.damageEffect.trim().takeIf { it.isNotEmpty() }
                    ?.let { listOf(CharacterDamageComponent(CharacterDamageComponentKind.TEXT, it)) }
                    .orEmpty()
            }
            CharacterCombatDamageProfile(entry.id, components)
        }

        val customMarkers = database.characterSuccessorQueries.selectCustomMarkers(id) {
                rowId, _, name, valueKind, currentValue, maxValue,
                recoveryCadence, recoveryAmountMode, recoveryFixedAmount, notes, sortOrder ->
            CharacterCustomMarker(
                id = Uuid.parse(rowId),
                name = name,
                valueKind = enumOrDefault(valueKind, CharacterTrackableValueKind.COUNTER),
                currentValue = currentValue.toInt(),
                maxValue = maxValue?.toInt(),
                recovery = CharacterTrackableRecovery(
                    cadence = enumOrDefault(recoveryCadence, CharacterRecoveryCadence.NONE),
                    amountMode = enumOrDefault(recoveryAmountMode, CharacterRecoveryAmountMode.NONE),
                    fixedAmount = recoveryFixedAmount?.toInt(),
                ),
                notes = notes,
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val storedResourceConfigurations = database.characterSuccessorQueries.selectResourceSuccessorConfigs(id) {
                resourceId, _, valueKind, placements ->
            val parsedId = parseUuidOrNull(resourceId) ?: return@selectResourceSuccessorConfigs null
            parsedId to CharacterResourceSuccessorConfiguration(
                resourceId = parsedId,
                valueKind = enumOrDefault(valueKind, CharacterTrackableValueKind.CURRENT_MAX),
                placements = parsePlacements(placements),
            )
        }.executeAsList().filterNotNull().toMap()

        val resourceConfigurations = sheet.resources.map { resource ->
            storedResourceConfigurations[resource.id] ?: CharacterResourceSuccessorConfiguration(
                resourceId = resource.id,
                valueKind = if (resource.maxValue == null) {
                    CharacterTrackableValueKind.COUNTER
                } else {
                    CharacterTrackableValueKind.CURRENT_MAX
                },
                placements = setOf(CharacterResourcePlacement.MANAGEMENT),
            )
        }

        return CharacterSuccessorState(
            customAttributes = customAttributes,
            spellcastingProfiles = spellcastingProfiles,
            combatDamage = combatDamage,
            customMarkers = customMarkers,
            resourceConfigurations = resourceConfigurations,
        )
    }

    fun saveState(characterId: Uuid, state: CharacterSuccessorState): CharacterSuccessorState {
        val sheet = requireNotNull(CharacterRepository(database).character(characterId)) {
            "Character must already exist locally."
        }
        validate(sheet, state)
        val id = characterId.toString()

        database.transaction {
            database.characterSuccessorQueries.deleteSpellSourceCasting(id)
            database.characterSuccessorQueries.deleteCombatDamageComponents(id)
            database.characterSuccessorQueries.deleteResourceSuccessorConfigs(id)
            database.characterSuccessorQueries.deleteCustomMarkers(id)
            database.characterSuccessorQueries.deleteCustomAttributes(id)

            state.customAttributes.forEachIndexed { index, item ->
                database.characterSuccessorQueries.insertCustomAttribute(
                    id = item.id.toString(),
                    character_id = id,
                    name = item.name.trim(),
                    abbreviation = item.abbreviation.trim(),
                    score = item.score.toLong(),
                    saving_throw_enabled = if (item.savingThrowEnabled) 1 else 0,
                    saving_throw_proficient = if (item.savingThrowProficient) 1 else 0,
                    saving_throw_adjustment = item.savingThrowAdjustment.toLong(),
                    notes = item.notes,
                    sort_order = index.toLong(),
                )
            }

            state.spellcastingProfiles.forEach { item ->
                val storedAbility = serializeAbilityReference(item.ability)
                database.characterSuccessorQueries.upsertSpellSourceCasting(
                    source_id = item.sourceId.toString(),
                    character_id = id,
                    ability_kind = storedAbility.first,
                    ability_value = storedAbility.second,
                    save_dc_adjustment = item.saveDcAdjustment.toLong(),
                    spell_attack_adjustment = item.spellAttackAdjustment.toLong(),
                    legacy_save_dc_override = item.legacySaveDcOverride?.toLong(),
                    legacy_spell_attack_override = item.legacySpellAttackOverride?.toLong(),
                )
            }

            state.combatDamage.forEach { profile ->
                profile.components.forEachIndexed { index, component ->
                    database.characterSuccessorQueries.insertCombatDamageComponent(
                        combat_entry_id = profile.combatEntryId.toString(),
                        character_id = id,
                        component_order = index.toLong(),
                        component_kind = component.kind.name,
                        expression = component.expression.trim(),
                        type_text = component.typeText?.trim()?.takeIf { it.isNotEmpty() },
                    )
                }
            }

            state.customMarkers.forEachIndexed { index, item ->
                database.characterSuccessorQueries.insertCustomMarker(
                    id = item.id.toString(),
                    character_id = id,
                    name = item.name.trim(),
                    value_kind = item.valueKind.name,
                    current_value = item.currentValue.toLong(),
                    max_value = item.maxValue?.toLong(),
                    recovery_cadence = item.recovery.cadence.name,
                    recovery_amount_mode = item.recovery.amountMode.name,
                    recovery_fixed_amount = item.recovery.fixedAmount?.toLong(),
                    notes = item.notes,
                    sort_order = index.toLong(),
                )
            }

            state.resourceConfigurations.forEach { item ->
                database.characterSuccessorQueries.upsertResourceSuccessorConfig(
                    resource_id = item.resourceId.toString(),
                    character_id = id,
                    value_kind = item.valueKind.name,
                    placements = item.placements
                        .sortedBy { it.ordinal }
                        .joinToString(",") { it.name },
                )
            }
        }

        return state(characterId)
    }

    private fun validate(sheet: CharacterSheet, state: CharacterSuccessorState) {
        requireDistinctIds(state.customAttributes.map { it.id }, "Custom attributes")
        state.customAttributes.forEach { item ->
            require(item.name.trim().isNotEmpty()) { "Custom attribute name must not be blank." }
            require(item.abbreviation.trim().isNotEmpty()) { "Custom attribute abbreviation must not be blank." }
            require(item.score >= 0) { "Custom attribute score must not be negative." }
        }
        val customAttributeIds = state.customAttributes.map { it.id }.toSet()

        val sourceIds = sheet.spellcastingSources.map { it.id }.toSet()
        requireDistinctIds(state.spellcastingProfiles.map { it.sourceId }, "Spellcasting profiles")
        state.spellcastingProfiles.forEach { item ->
            require(item.sourceId in sourceIds) { "Spellcasting profile must reference an existing source." }
            validateAbilityReference(item.ability, customAttributeIds)
        }

        val combatEntryIds = sheet.combatEntries.map { it.id }.toSet()
        requireDistinctIds(state.combatDamage.map { it.combatEntryId }, "Combat damage profiles")
        state.combatDamage.forEach { profile ->
            require(profile.combatEntryId in combatEntryIds) { "Combat damage must reference an existing combat entry." }
            profile.components.forEach { component ->
                require(component.expression.trim().isNotEmpty()) { "Damage component expression must not be blank." }
            }
        }

        requireDistinctIds(state.customMarkers.map { it.id }, "Custom markers")
        state.customMarkers.forEach { item ->
            require(item.name.trim().isNotEmpty()) { "Custom marker name must not be blank." }
            require(item.currentValue >= 0) { "Custom marker current value must not be negative." }
            require(item.maxValue == null || item.maxValue >= 0) { "Custom marker maximum must not be negative." }
            when (item.valueKind) {
                CharacterTrackableValueKind.BINARY -> {
                    require(item.currentValue in 0..1) { "Binary marker value must be zero or one." }
                    require(item.maxValue == null || item.maxValue == 1) { "Binary marker maximum may only be one." }
                }
                CharacterTrackableValueKind.COUNTER -> Unit
                CharacterTrackableValueKind.CURRENT_MAX -> {
                    val maximum = requireNotNull(item.maxValue) { "Current/max marker requires a maximum." }
                    require(item.currentValue <= maximum) { "Marker current value must not exceed maximum." }
                }
            }
            require(item.recovery.fixedAmount == null || item.recovery.fixedAmount >= 0) {
                "Marker fixed recovery amount must not be negative."
            }
            require(
                item.recovery.amountMode != CharacterRecoveryAmountMode.FIXED || item.recovery.fixedAmount != null,
            ) { "Fixed marker recovery requires a fixed amount." }
        }

        val resourceIds = sheet.resources.map { it.id }.toSet()
        requireDistinctIds(state.resourceConfigurations.map { it.resourceId }, "Resource configurations")
        state.resourceConfigurations.forEach { item ->
            require(item.resourceId in resourceIds) { "Resource configuration must reference an existing resource." }
            require(item.placements.isNotEmpty()) { "Resource must have at least one presentation placement." }
        }
    }

    private fun legacySpellcastingProfile(sourceId: Uuid, sheet: CharacterSheet): CharacterSpellcastingProfile {
        val builtIn = sheet.spellcastingAbility.toBuiltInCharacterAbilityOrNull()
        if (builtIn == null) {
            return CharacterSpellcastingProfile(
                sourceId = sourceId,
                ability = CharacterAbilityReference.NONE,
                legacySaveDcOverride = sheet.spellSaveDc,
                legacySpellAttackOverride = sheet.spellAttackModifier,
            )
        }

        val abilityModifier = sheet.abilityModifier(builtIn)
        val baseSaveDc = 8 + abilityModifier + sheet.finalProficiencyBonus
        val baseAttack = abilityModifier + sheet.finalProficiencyBonus
        return CharacterSpellcastingProfile(
            sourceId = sourceId,
            ability = CharacterAbilityReference.builtIn(builtIn),
            saveDcAdjustment = sheet.spellSaveDc?.minus(baseSaveDc) ?: 0,
            spellAttackAdjustment = sheet.spellAttackModifier?.minus(baseAttack) ?: 0,
        )
    }

    private fun validateAbilityReference(reference: CharacterAbilityReference, customAttributeIds: Set<Uuid>) {
        require(reference.builtIn == null || reference.customAttributeId == null) {
            "Ability reference cannot target built-in and custom abilities at the same time."
        }
        reference.customAttributeId?.let {
            require(it in customAttributeIds) { "Ability reference must target an existing custom attribute." }
        }
    }

    private fun serializeAbilityReference(reference: CharacterAbilityReference): Pair<String, String?> = when {
        reference.builtIn != null -> "BUILT_IN" to reference.builtIn.name
        reference.customAttributeId != null -> "CUSTOM" to reference.customAttributeId.toString()
        else -> "NONE" to null
    }

    private fun parseAbilityReference(
        kind: String,
        value: String?,
        customAttributeIds: Set<Uuid>,
    ): CharacterAbilityReference = when (kind) {
        "BUILT_IN" -> value
            ?.let { runCatching { CharacterAbility.valueOf(it) }.getOrNull() }
            ?.let(CharacterAbilityReference::builtIn)
            ?: CharacterAbilityReference.NONE
        "CUSTOM" -> value
            ?.let(::parseUuidOrNull)
            ?.takeIf { it in customAttributeIds }
            ?.let(CharacterAbilityReference::custom)
            ?: CharacterAbilityReference.NONE
        else -> CharacterAbilityReference.NONE
    }

    private fun parsePlacements(raw: String): Set<CharacterResourcePlacement> {
        val parsed = raw.split(',')
            .mapNotNull { token -> runCatching { CharacterResourcePlacement.valueOf(token.trim()) }.getOrNull() }
            .toSet()
        return parsed.ifEmpty { setOf(CharacterResourcePlacement.MANAGEMENT) }
    }

    private fun parseUuidOrNull(value: String): Uuid? = runCatching { Uuid.parse(value) }.getOrNull()

    private inline fun <reified T : Enum<T>> enumOrDefault(raw: String, default: T): T =
        runCatching { enumValueOf<T>(raw) }.getOrDefault(default)

    private fun requireDistinctIds(ids: List<Uuid>, label: String) {
        require(ids.distinct().size == ids.size) { "$label must have distinct identity." }
    }

    private data class DamageRow(
        val combatEntryId: Uuid,
        val order: Int,
        val component: CharacterDamageComponent,
    )
}
