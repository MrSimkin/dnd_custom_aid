package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/**
 * Persistence boundary for the additive Phase 4 closure domains.
 *
 * The existing CharacterRepository remains responsible for the already-tested core sheet domains.
 * This repository deliberately owns schema-7-only state so those older save/rewrite paths remain
 * stable while the Android closure UI is built. Soft references are normalized against current
 * character rows on both save and load.
 */
class CharacterClosureRepository(
    private val database: AppDatabase,
) {
    fun state(characterId: Uuid): CharacterClosureState {
        requireCharacterExists(characterId)
        val id = characterId.toString()
        val resourceIds = currentResourceIds(id)
        val inventoryIds = currentInventoryIds(id)
        val spellIds = currentSpellIds(id)

        val settings = database.characterClosureQueries.selectClosureSettings(id) {
                _, exhaustionLevel, concentrationSpellId, concentrationName, concentrationNotes,
                portraitRef, tokenRef, progressMode, experiencePoints, milestoneProgress,
                tableModeEnabled, hapticsEnabled ->
            ClosureSettingsRow(
                exhaustionLevel = exhaustionLevel.toInt(),
                concentrationSpellId = concentrationSpellId?.let(::parseUuidOrNull)?.takeIf { it in spellIds },
                concentrationName = concentrationName,
                concentrationNotes = concentrationNotes,
                portraitRef = portraitRef,
                tokenRef = tokenRef,
                progressMode = enumOrDefault(progressMode, CharacterProgressMode.MILESTONE),
                experiencePoints = experiencePoints.toInt(),
                milestoneProgress = milestoneProgress,
                tableModeEnabled = tableModeEnabled != 0L,
                hapticsEnabled = hapticsEnabled != 0L,
            )
        }.executeAsOneOrNull() ?: ClosureSettingsRow()

        val conditions = database.characterClosureQueries.selectConditions(id) {
                rowId, _, name, source, notes, sortOrder ->
            CharacterCondition(Uuid.parse(rowId), name, source, notes, sortOrder.toInt())
        }.executeAsList()

        val defenses = database.characterClosureQueries.selectDefenses(id) {
                rowId, _, type, name, source, notes, sortOrder ->
            CharacterDefense(
                Uuid.parse(rowId),
                enumOrDefault(type, CharacterDefenseType.RESISTANCE),
                name,
                source,
                notes,
                sortOrder.toInt(),
            )
        }.executeAsList()

        val movements = database.characterClosureQueries.selectMovements(id) {
                rowId, _, type, name, speedFeet, notes, sortOrder ->
            CharacterMovement(
                Uuid.parse(rowId),
                enumOrDefault(type, CharacterMovementType.OTHER),
                name,
                speedFeet?.toInt(),
                notes,
                sortOrder.toInt(),
            )
        }.executeAsList()

        val senses = database.characterClosureQueries.selectSenses(id) {
                rowId, _, name, rangeFeet, notes, sortOrder ->
            CharacterSense(Uuid.parse(rowId), name, rangeFeet?.toInt(), notes, sortOrder.toInt())
        }.executeAsList()

        val resourceRecoveryRows = database.characterClosureQueries.selectResourceRecovery(id) {
                _, resourceId, cadence, amountMode, fixedAmount, notes ->
            ResourceRecoveryRow(resourceId, cadence, amountMode, fixedAmount?.toInt(), notes)
        }.executeAsList()
        val resourceRecovery = resourceRecoveryRows.mapNotNull { row ->
            val parsedId = parseUuidOrNull(row.resourceId)
            if (parsedId == null || parsedId !in resourceIds) {
                null
            } else {
                CharacterResourceRecovery(
                    resourceId = parsedId,
                    cadence = enumOrDefault(row.cadence, CharacterRecoveryCadence.NONE),
                    amountMode = enumOrDefault(row.amountMode, CharacterRecoveryAmountMode.NONE),
                    fixedAmount = row.fixedAmount,
                    notes = row.notes,
                )
            }
        }

        val inventoryUsageRows = database.characterClosureQueries.selectInventoryUsage(id) {
                _, itemId, kind, quickUseAmount ->
            InventoryUsageRow(itemId, kind, quickUseAmount.toInt())
        }.executeAsList()
        val inventoryUsage = inventoryUsageRows.mapNotNull { row ->
            val parsedId = parseUuidOrNull(row.itemId)
            if (parsedId == null || parsedId !in inventoryIds) {
                null
            } else {
                CharacterInventoryUsage(
                    itemId = parsedId,
                    kind = enumOrDefault(row.kind, CharacterConsumableKind.NONE),
                    quickUseAmount = row.quickUseAmount,
                )
            }
        }

        val checkpoints = database.characterClosureQueries.selectReconciliationCheckpoints(id) {
                rowId, _, createdAt, characterUpdatedAt, label, notes ->
            CharacterReconciliationCheckpoint(
                id = Uuid.parse(rowId),
                createdAtEpochSeconds = createdAt,
                characterUpdatedAtEpochSeconds = characterUpdatedAt,
                label = label,
                notes = notes,
            )
        }.executeAsList()

        val customSkills = database.characterClosureQueries.selectCustomSkills(id) {
                rowId, _, name, ability, training, adjustment, source, notes, sortOrder ->
            CharacterCustomSkill(
                id = Uuid.parse(rowId),
                name = name,
                ability = enumOrDefault(ability, CharacterAbility.INTELLIGENCE),
                training = enumOrDefault(training, SkillTraining.NONE),
                adjustment = adjustment.toInt(),
                source = source,
                notes = notes,
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val temporaryEffects = database.characterClosureQueries.selectTemporaryEffects(id) {
                rowId, _, name, summary, durationText, source, notes, active, sortOrder ->
            CharacterTemporaryEffect(
                id = Uuid.parse(rowId),
                name = name,
                summary = summary,
                durationText = durationText,
                source = source,
                notes = notes,
                active = active != 0L,
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val moduleOverrideRows = database.characterClosureQueries.selectModuleOverrides(id) {
                _, moduleKind, overrideMode ->
            ModuleOverrideRow(moduleKind, overrideMode)
        }.executeAsList()
        val moduleOverrides = moduleOverrideRows.mapNotNull { row ->
            val module = runCatching { CharacterModuleKind.valueOf(row.moduleKind) }.getOrNull()
            module?.let {
                CharacterModuleOverride(
                    module = it,
                    mode = enumOrDefault(row.overrideMode, CharacterModuleOverrideMode.AUTO),
                )
            }
        }

        val quickAccessRows = database.characterClosureQueries.selectQuickAccess(id) {
                _, targetKind, targetId, sortOrder ->
            QuickAccessRow(targetKind, targetId, sortOrder.toInt())
        }.executeAsList()
        val quickAccess = quickAccessRows.mapNotNull { row ->
            val parsedId = parseUuidOrNull(row.targetId)
            val kind = runCatching { CharacterQuickAccessKind.valueOf(row.targetKind) }.getOrNull()
            if (parsedId == null || kind == null) null else CharacterQuickAccessRef(kind, parsedId, row.sortOrder)
        }

        val concentration = settings.concentrationName
            ?.takeIf { it.isNotBlank() }
            ?.let { CharacterConcentration(settings.concentrationSpellId, it, settings.concentrationNotes) }

        return CharacterClosureState(
            exhaustionLevel = settings.exhaustionLevel,
            concentration = concentration,
            portraitRef = settings.portraitRef,
            tokenRef = settings.tokenRef,
            progressMode = settings.progressMode,
            experiencePoints = settings.experiencePoints,
            milestoneProgress = settings.milestoneProgress,
            tableModeEnabled = settings.tableModeEnabled,
            hapticsEnabled = settings.hapticsEnabled,
            conditions = conditions,
            defenses = defenses,
            movements = movements,
            senses = senses,
            resourceRecovery = resourceRecovery,
            inventoryUsage = inventoryUsage,
            reconciliationCheckpoints = checkpoints,
            customSkills = customSkills,
            temporaryEffects = temporaryEffects,
            moduleOverrides = moduleOverrides,
            quickAccess = quickAccess,
        )
    }

    fun saveState(characterId: Uuid, state: CharacterClosureState): CharacterClosureState {
        requireCharacterExists(characterId)
        validate(state)

        val id = characterId.toString()
        val resourceIds = currentResourceIds(id)
        val inventoryIds = currentInventoryIds(id)
        val spellIds = currentSpellIds(id)

        val normalizedConcentration = state.concentration?.let { concentration ->
            concentration.copy(spellId = concentration.spellId?.takeIf { it in spellIds })
        }
        val normalizedRecovery = state.resourceRecovery.filter { it.resourceId in resourceIds }
        val normalizedUsage = state.inventoryUsage.filter { it.itemId in inventoryIds }

        database.transaction {
            database.characterClosureQueries.upsertClosureSettings(
                character_id = id,
                exhaustion_level = state.exhaustionLevel.toLong(),
                concentration_spell_id = normalizedConcentration?.spellId?.toString(),
                concentration_name = normalizedConcentration?.name?.trim(),
                concentration_notes = normalizedConcentration?.notes,
                portrait_ref = state.portraitRef?.trim()?.takeIf { it.isNotEmpty() },
                token_ref = state.tokenRef?.trim()?.takeIf { it.isNotEmpty() },
                progress_mode = state.progressMode.name,
                experience_points = state.experiencePoints.toLong(),
                milestone_progress = state.milestoneProgress,
                table_mode_enabled = if (state.tableModeEnabled) 1 else 0,
                haptics_enabled = if (state.hapticsEnabled) 1 else 0,
            )

            database.characterClosureQueries.deleteConditions(id)
            state.conditions.forEachIndexed { index, item ->
                database.characterClosureQueries.insertCondition(
                    item.id.toString(), id, item.name.trim(), item.source, item.notes, index.toLong(),
                )
            }

            database.characterClosureQueries.deleteDefenses(id)
            state.defenses.forEachIndexed { index, item ->
                database.characterClosureQueries.insertDefense(
                    item.id.toString(), id, item.type.name, item.name.trim(), item.source, item.notes, index.toLong(),
                )
            }

            database.characterClosureQueries.deleteMovements(id)
            state.movements.forEachIndexed { index, item ->
                database.characterClosureQueries.insertMovement(
                    item.id.toString(), id, item.type.name, item.name.trim(), item.speedFeet?.toLong(), item.notes, index.toLong(),
                )
            }

            database.characterClosureQueries.deleteSenses(id)
            state.senses.forEachIndexed { index, item ->
                database.characterClosureQueries.insertSense(
                    item.id.toString(), id, item.name.trim(), item.rangeFeet?.toLong(), item.notes, index.toLong(),
                )
            }

            database.characterClosureQueries.deleteResourceRecovery(id)
            normalizedRecovery.forEach { item ->
                database.characterClosureQueries.upsertResourceRecovery(
                    character_id = id,
                    resource_id = item.resourceId.toString(),
                    recovery_cadence = item.cadence.name,
                    amount_mode = item.amountMode.name,
                    fixed_amount = item.fixedAmount?.toLong(),
                    notes = item.notes,
                )
            }

            database.characterClosureQueries.deleteInventoryUsage(id)
            normalizedUsage.forEach { item ->
                database.characterClosureQueries.upsertInventoryUsage(
                    character_id = id,
                    item_id = item.itemId.toString(),
                    consumable_kind = item.kind.name,
                    quick_use_amount = item.quickUseAmount.toLong(),
                )
            }

            database.characterClosureQueries.deleteReconciliationCheckpoints(id)
            state.reconciliationCheckpoints.forEach { item ->
                database.characterClosureQueries.insertReconciliationCheckpoint(
                    item.id.toString(), id, item.createdAtEpochSeconds, item.characterUpdatedAtEpochSeconds, item.label, item.notes,
                )
            }

            database.characterClosureQueries.deleteCustomSkills(id)
            state.customSkills.forEachIndexed { index, item ->
                database.characterClosureQueries.insertCustomSkill(
                    item.id.toString(), id, item.name.trim(), item.ability.name, item.training.name,
                    item.adjustment.toLong(), item.source, item.notes, index.toLong(),
                )
            }

            database.characterClosureQueries.deleteTemporaryEffects(id)
            state.temporaryEffects.forEachIndexed { index, item ->
                database.characterClosureQueries.insertTemporaryEffect(
                    item.id.toString(), id, item.name.trim(), item.summary, item.durationText, item.source, item.notes,
                    if (item.active) 1 else 0, index.toLong(),
                )
            }

            database.characterClosureQueries.deleteModuleOverrides(id)
            state.moduleOverrides.forEach { item ->
                database.characterClosureQueries.upsertModuleOverride(id, item.module.name, item.mode.name)
            }

            database.characterClosureQueries.deleteQuickAccess(id)
            state.quickAccess.forEachIndexed { index, item ->
                database.characterClosureQueries.insertQuickAccess(id, item.kind.name, item.targetId.toString(), index.toLong())
            }
        }

        return state(characterId)
    }

    private fun validate(state: CharacterClosureState) {
        require(state.exhaustionLevel >= 0) { "Exhaustion level must not be negative." }
        require(state.experiencePoints >= 0) { "Experience points must not be negative." }
        state.concentration?.let { require(it.name.trim().isNotEmpty()) { "Concentration name must not be blank." } }

        requireDistinctIds(state.conditions.map { it.id }, "Conditions")
        state.conditions.forEach { require(it.name.trim().isNotEmpty()) { "Condition name must not be blank." } }

        requireDistinctIds(state.defenses.map { it.id }, "Defenses")
        state.defenses.forEach { require(it.name.trim().isNotEmpty()) { "Defense name must not be blank." } }

        requireDistinctIds(state.movements.map { it.id }, "Movements")
        state.movements.forEach {
            require(it.name.trim().isNotEmpty()) { "Movement name must not be blank." }
            require(it.speedFeet == null || it.speedFeet >= 0) { "Movement speed must not be negative." }
        }

        requireDistinctIds(state.senses.map { it.id }, "Senses")
        state.senses.forEach {
            require(it.name.trim().isNotEmpty()) { "Sense name must not be blank." }
            require(it.rangeFeet == null || it.rangeFeet >= 0) { "Sense range must not be negative." }
        }

        val recoveryIds = state.resourceRecovery.map { it.resourceId }
        require(recoveryIds.distinct().size == recoveryIds.size) { "Each resource may have only one recovery rule." }
        state.resourceRecovery.forEach {
            require(it.fixedAmount == null || it.fixedAmount >= 0) { "Fixed recovery amount must not be negative." }
            require(it.amountMode != CharacterRecoveryAmountMode.FIXED || it.fixedAmount != null) {
                "Fixed recovery mode requires a fixed amount."
            }
        }

        val usageIds = state.inventoryUsage.map { it.itemId }
        require(usageIds.distinct().size == usageIds.size) { "Each inventory item may have only one usage rule." }
        state.inventoryUsage.forEach { require(it.quickUseAmount > 0) { "Quick-use amount must be positive." } }

        requireDistinctIds(state.reconciliationCheckpoints.map { it.id }, "Reconciliation checkpoints")
        state.reconciliationCheckpoints.forEach {
            require(it.createdAtEpochSeconds >= 0) { "Checkpoint creation time must not be negative." }
            require(it.characterUpdatedAtEpochSeconds >= 0) { "Checkpoint character time must not be negative." }
        }

        requireDistinctIds(state.customSkills.map { it.id }, "Custom skills")
        state.customSkills.forEach { require(it.name.trim().isNotEmpty()) { "Custom skill name must not be blank." } }

        requireDistinctIds(state.temporaryEffects.map { it.id }, "Temporary effects")
        state.temporaryEffects.forEach { require(it.name.trim().isNotEmpty()) { "Temporary effect name must not be blank." } }

        val overrideModules = state.moduleOverrides.map { it.module }
        require(overrideModules.distinct().size == overrideModules.size) { "Each module may have only one override." }

        val quickKeys = state.quickAccess.map { it.kind to it.targetId }
        require(quickKeys.distinct().size == quickKeys.size) { "Quick Access references must be unique." }
    }

    private fun requireCharacterExists(characterId: Uuid) {
        val exists = database.characterQueries.selectCharacterById(characterId.toString()).executeAsOneOrNull()
        require(exists != null) { "Character must already exist locally." }
    }

    private fun currentResourceIds(characterId: String): Set<Uuid> =
        database.characterQueries.selectCharacterResources(characterId) {
                rowId, _, _, _, _, _, _, _, _, _ -> Uuid.parse(rowId)
        }.executeAsList().toSet()

    private fun currentInventoryIds(characterId: String): Set<Uuid> =
        database.characterQueries.selectCharacterInventoryItems(characterId) {
                rowId, _, _, _, _, _, _, _, _, _, _, _ -> Uuid.parse(rowId)
        }.executeAsList().toSet()

    private fun currentSpellIds(characterId: String): Set<Uuid> =
        database.characterQueries.selectCharacterSpells(characterId) {
                rowId, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> Uuid.parse(rowId)
        }.executeAsList().toSet()

    private fun requireDistinctIds(ids: List<Uuid>, label: String) {
        require(ids.distinct().size == ids.size) { "$label must have distinct identity." }
    }

    private fun parseUuidOrNull(value: String): Uuid? = runCatching { Uuid.parse(value) }.getOrNull()

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String, default: T): T =
        runCatching { enumValueOf<T>(value) }.getOrDefault(default)

    private data class ClosureSettingsRow(
        val exhaustionLevel: Int = 0,
        val concentrationSpellId: Uuid? = null,
        val concentrationName: String? = null,
        val concentrationNotes: String? = null,
        val portraitRef: String? = null,
        val tokenRef: String? = null,
        val progressMode: CharacterProgressMode = CharacterProgressMode.MILESTONE,
        val experiencePoints: Int = 0,
        val milestoneProgress: String = "",
        val tableModeEnabled: Boolean = false,
        val hapticsEnabled: Boolean = true,
    )

    private data class ResourceRecoveryRow(
        val resourceId: String,
        val cadence: String,
        val amountMode: String,
        val fixedAmount: Int?,
        val notes: String?,
    )

    private data class InventoryUsageRow(
        val itemId: String,
        val kind: String,
        val quickUseAmount: Int,
    )

    private data class ModuleOverrideRow(
        val moduleKind: String,
        val overrideMode: String,
    )

    private data class QuickAccessRow(
        val targetKind: String,
        val targetId: String,
        val sortOrder: Int,
    )
}
