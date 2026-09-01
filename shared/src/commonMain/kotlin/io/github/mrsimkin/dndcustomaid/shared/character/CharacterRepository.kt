package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

class CharacterRepository(
    private val database: AppDatabase,
) {
    fun listCharacters(campaignId: Uuid): List<CharacterSheet> =
        database.characterQueries.selectCharactersByCampaign(campaignId.toString(), ::mapCore)
            .executeAsList()
            .map(::hydrate)

    fun character(id: Uuid): CharacterSheet? =
        database.characterQueries.selectCharacterById(id.toString(), ::mapCore)
            .executeAsOneOrNull()
            ?.let(::hydrate)

    fun createCharacter(campaignId: Uuid, rawName: String): CharacterSheet {
        requireCampaignExists(campaignId)
        val name = rawName.trim()
        require(name.isNotEmpty()) { "Character name must not be blank." }

        val id = Uuid.random()
        database.transaction {
            database.characterQueries.insertCharacter(
                id = id.toString(),
                campaign_id = campaignId.toString(),
                name = name,
                status = CharacterStatus.ACTIVE.name,
            )
            CharacterAbility.entries.forEach { ability ->
                database.characterQueries.insertCharacterSave(
                    character_id = id.toString(),
                    ability_key = ability.name,
                    proficient = 0,
                    adjustment = 0,
                )
            }
            SkillKey.entries.forEach { key ->
                database.characterQueries.insertCharacterSkill(
                    character_id = id.toString(),
                    skill_key = key.name,
                    training = SkillTraining.NONE.name,
                    adjustment = 0,
                )
            }
            defaultCurrencies.forEachIndexed { index, currency ->
                database.characterQueries.insertCharacterCurrency(
                    character_id = id.toString(),
                    currency_key = currency.key,
                    name = currency.name,
                    amount = 0,
                    sort_order = index.toLong(),
                    is_default = 1,
                )
            }
        }

        return requireNotNull(character(id))
    }

    fun saveCharacter(sheet: CharacterSheet): CharacterSheet {
        require(character(sheet.id) != null) { "Character must already exist locally." }
        require(sheet.name.trim().isNotEmpty()) { "Character name must not be blank." }

        val classIds = sheet.classes.map { it.id }
        require(classIds.distinct().size == classIds.size) { "Character class entries must have distinct identity." }
        sheet.classes.forEach { classLevel ->
            require(classLevel.name.trim().isNotEmpty()) { "Class name must not be blank." }
            require(classLevel.level >= 0) { "Class level must not be negative." }
            require(classLevel.hitDieSides >= 0) { "Hit die size must not be negative." }
            require(classLevel.hitDiceRemaining >= 0) { "Remaining hit dice must not be negative." }
        }
        val classIdSet = classIds.toSet()

        val savesByAbility = sheet.savingThrows.associateBy { it.ability }
        require(savesByAbility.size == sheet.savingThrows.size) { "Each saving throw may appear only once." }

        val skillsByKey = sheet.skills.associateBy { it.key }
        require(skillsByKey.size == sheet.skills.size) { "Each skill may appear only once." }

        val spellSlotsByLevel = sheet.spellSlots.associateBy { it.level }
        require(spellSlotsByLevel.size == sheet.spellSlots.size) { "Each spell level may appear only once." }
        sheet.spellSlots.forEach { slot ->
            require(slot.level in 1..9) { "Spell-slot level must be between 1 and 9." }
            require(slot.totalSlots >= 0) { "Total spell slots must not be negative." }
            require(slot.spentSlots in 0..slot.totalSlots) { "Spent spell slots must be between zero and the configured total." }
        }

        val combatIds = sheet.combatEntries.map { it.id }
        require(combatIds.distinct().size == combatIds.size) { "Combat entries must have distinct identity." }
        sheet.combatEntries.forEach { entry ->
            require(entry.name.trim().isNotEmpty()) { "Combat entry name must not be blank." }
        }

        val inventoryIds = sheet.inventoryItems.map { it.id }
        require(inventoryIds.distinct().size == inventoryIds.size) { "Inventory items must have distinct identity." }
        sheet.inventoryItems.forEach { item ->
            require(item.name.trim().isNotEmpty()) { "Inventory item name must not be blank." }
            require(item.quantity >= 0) { "Inventory quantity must not be negative." }
            require(item.weightLb == null || item.weightLb >= 0.0) { "Inventory unit weight must not be negative." }
        }

        val currencyKeys = sheet.currencies.map { it.key }
        require(currencyKeys.distinct().size == currencyKeys.size) { "Currency keys must be unique per character." }
        sheet.currencies.forEach { currency ->
            require(currency.key.isNotBlank()) { "Currency key must not be blank." }
            require(currency.name.trim().isNotEmpty()) { "Currency name must not be blank." }
        }

        val traitIds = sheet.traits.map { it.id }
        require(traitIds.distinct().size == traitIds.size) { "Character traits must have distinct identity." }
        sheet.traits.forEach { trait ->
            require(trait.name.trim().isNotEmpty()) { "Trait name must not be blank." }
            require(trait.maxUses == null || trait.maxUses >= 0) { "Trait maximum uses must not be negative." }
            require(trait.spentUses >= 0) { "Trait spent uses must not be negative." }
            require(trait.maxUses != null || trait.spentUses == 0) { "Trait spent uses require a configured maximum." }
            trait.maxUses?.let { maximum ->
                require(trait.spentUses <= maximum) { "Trait spent uses must not exceed maximum uses." }
            }
        }

        val noteIds = sheet.noteCards.map { it.id }
        require(noteIds.distinct().size == noteIds.size) { "Titled notes must have distinct identity." }
        sheet.noteCards.forEach { note ->
            require(note.title.trim().isNotEmpty()) { "Titled note title must not be blank." }
        }

        val sourceIds = sheet.spellcastingSources.map { it.id }
        require(sourceIds.distinct().size == sourceIds.size) { "Spellcasting sources must have distinct identity." }
        sheet.spellcastingSources.forEach { source ->
            require(source.name.trim().isNotEmpty()) { "Spellcasting source name must not be blank." }
        }
        val normalizedSources = sheet.spellcastingSources.map { source ->
            source.copy(linkedClassId = source.linkedClassId?.takeIf { it in classIdSet })
        }
        val normalizedSourceIds = normalizedSources.map { it.id }.toSet()

        val spellIds = sheet.spells.map { it.id }
        require(spellIds.distinct().size == spellIds.size) { "Spells must have distinct identity." }
        sheet.spells.forEach { spell ->
            require(spell.name.trim().isNotEmpty()) { "Spell name must not be blank." }
            require(spell.level in 0..9) { "Spell level must be between 0 and 9." }
            val associationIds = spell.sourceAssociations.map { it.sourceId }
            require(associationIds.distinct().size == associationIds.size) {
                "A spell may associate with each spellcasting source only once."
            }
        }
        val normalizedSpells = sheet.spells.map { spell ->
            spell.copy(
                sourceAssociations = spell.sourceAssociations.filter { it.sourceId in normalizedSourceIds },
            )
        }

        val standardProficiency = standardProficiencyBonusForLevel(sheet.totalLevel)
        val adjustmentImpliedByCompatibilitySnapshot = sheet.proficiencyBonus - standardProficiency
        val proficiencyAdjustment = if (
            sheet.proficiencyBonus == standardProficiency + sheet.proficiencyBonusAdjustment
        ) {
            sheet.proficiencyBonusAdjustment
        } else {
            adjustmentImpliedByCompatibilitySnapshot
        }
        val finalProficiency = standardProficiency + proficiencyAdjustment

        database.transaction {
            database.characterQueries.updateCharacterCore(
                sheet.name.trim(),
                sheet.status.name,
                sheet.strength.toLong(),
                sheet.dexterity.toLong(),
                sheet.constitution.toLong(),
                sheet.intelligence.toLong(),
                sheet.wisdom.toLong(),
                sheet.charisma.toLong(),
                sheet.armorClass.toLong(),
                sheet.maxHp.toLong(),
                sheet.currentHp.toLong(),
                sheet.tempHp.toLong(),
                sheet.initiativeAdjustment.toLong(),
                sheet.speed.toLong(),
                finalProficiency.toLong(),
                proficiencyAdjustment.toLong(),
                sheet.passivePerceptionAdjustment.toLong(),
                sheet.spellSaveDc?.toLong(),
                sheet.spellAttackModifier?.toLong(),
                sheet.spellcastingAbility.name,
                if (sheet.spellcasterEnabled) 1 else 0,
                sheet.generalNotes,
                sheet.id.toString(),
            )

            database.characterQueries.deleteCharacterClasses(sheet.id.toString())
            sheet.classes.forEachIndexed { index, classLevel ->
                database.characterQueries.insertCharacterClass(
                    id = classLevel.id.toString(),
                    character_id = sheet.id.toString(),
                    name = classLevel.name.trim(),
                    level = classLevel.level.toLong(),
                    hit_die_sides = classLevel.hitDieSides.toLong(),
                    hit_dice_remaining = classLevel.hitDiceRemaining.toLong(),
                    sort_order = index.toLong(),
                )
            }

            CharacterAbility.entries.forEach { ability ->
                val save = savesByAbility[ability]
                    ?: CharacterSavingThrow(ability = ability, proficient = false, adjustment = 0)
                database.characterQueries.insertCharacterSave(
                    character_id = sheet.id.toString(),
                    ability_key = ability.name,
                    proficient = if (save.proficient) 1 else 0,
                    adjustment = save.adjustment.toLong(),
                )
            }

            SkillKey.entries.forEach { key ->
                val skill = skillsByKey[key]
                    ?: CharacterSkill(key = key, adjustment = 0, training = SkillTraining.NONE)
                database.characterQueries.insertCharacterSkill(
                    character_id = sheet.id.toString(),
                    skill_key = key.name,
                    training = skill.training.name,
                    adjustment = skill.adjustment.toLong(),
                )
            }

            database.characterQueries.deleteCharacterSpellSlots(sheet.id.toString())
            sheet.spellSlots.sortedBy { it.level }.forEach { slot ->
                database.characterQueries.insertCharacterSpellSlot(
                    character_id = sheet.id.toString(),
                    spell_level = slot.level.toLong(),
                    total_slots = slot.totalSlots.toLong(),
                    spent_slots = slot.spentSlots.toLong(),
                )
            }

            database.characterQueries.deleteCharacterCombatEntries(sheet.id.toString())
            sheet.combatEntries.forEachIndexed { index, entry ->
                database.characterQueries.insertCharacterCombatEntry(
                    id = entry.id.toString(),
                    character_id = sheet.id.toString(),
                    name = entry.name.trim(),
                    type = entry.type.name,
                    attack_modifier = entry.attackModifier?.toLong(),
                    damage_effect = entry.damageEffect,
                    range_text = entry.rangeText,
                    notes = entry.notes,
                    sort_order = index.toLong(),
                )
            }

            database.characterQueries.deleteCharacterInventoryItems(sheet.id.toString())
            sheet.inventoryItems.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterInventoryItem(
                    id = item.id.toString(),
                    character_id = sheet.id.toString(),
                    name = item.name.trim(),
                    quantity = item.quantity.toLong(),
                    weight_lb = item.weightLb,
                    equipped = if (item.equipped) 1 else 0,
                    notes = item.notes,
                    sort_order = index.toLong(),
                    special = if (item.special) 1 else 0,
                    description = item.description,
                    location = item.location,
                    attuned = if (item.attuned) 1 else 0,
                )
            }

            database.characterQueries.deleteCharacterCurrencies(sheet.id.toString())
            sheet.currencies.forEachIndexed { index, currency ->
                database.characterQueries.insertCharacterCurrency(
                    character_id = sheet.id.toString(),
                    currency_key = currency.key,
                    name = currency.name.trim(),
                    amount = currency.amount.toLong(),
                    sort_order = index.toLong(),
                    is_default = if (currency.isDefault) 1 else 0,
                )
            }

            database.characterQueries.upsertCharacterBackground(
                character_id = sheet.id.toString(),
                background_name = sheet.background.name,
                summary = sheet.background.summary,
                personality_traits = sheet.background.personalityTraits,
                ideals = sheet.background.ideals,
                bonds = sheet.background.bonds,
                flaws = sheet.background.flaws,
                story = sheet.background.story,
            )

            database.characterQueries.deleteCharacterTraits(sheet.id.toString())
            sheet.traits.forEachIndexed { index, trait ->
                database.characterQueries.insertCharacterTrait(
                    id = trait.id.toString(),
                    character_id = sheet.id.toString(),
                    name = trait.name.trim(),
                    source = trait.source.trim(),
                    trait_type = trait.type.name,
                    description = trait.description,
                    notes = trait.notes,
                    max_uses = trait.maxUses?.toLong(),
                    spent_uses = trait.spentUses.toLong(),
                    recovery = trait.recovery,
                    activation = trait.activation?.name,
                    sort_order = index.toLong(),
                )
            }

            database.characterQueries.deleteCharacterNotes(sheet.id.toString())
            sheet.noteCards.forEachIndexed { index, note ->
                database.characterQueries.insertCharacterNote(
                    id = note.id.toString(),
                    character_id = sheet.id.toString(),
                    title = note.title.trim(),
                    content = note.content,
                    sort_order = index.toLong(),
                )
            }

            database.characterQueries.deleteCharacterSpells(sheet.id.toString())
            database.characterQueries.deleteCharacterSpellSources(sheet.id.toString())
            normalizedSources.forEachIndexed { index, source ->
                database.characterQueries.insertCharacterSpellSource(
                    id = source.id.toString(),
                    character_id = sheet.id.toString(),
                    name = source.name.trim(),
                    linked_class_id = source.linkedClassId?.toString(),
                    sort_order = index.toLong(),
                )
            }

            val spellOrderByLevel = mutableMapOf<Int, Int>()
            normalizedSpells.forEach { spell ->
                val levelOrder = spellOrderByLevel[spell.level] ?: 0
                database.characterQueries.insertCharacterSpell(
                    id = spell.id.toString(),
                    character_id = sheet.id.toString(),
                    name = spell.name.trim(),
                    spell_level = spell.level.toLong(),
                    casting_time = spell.castingTime,
                    range_text = spell.rangeText,
                    has_verbal = if (spell.verbal) 1 else 0,
                    has_somatic = if (spell.somatic) 1 else 0,
                    has_material = if (spell.material) 1 else 0,
                    material_text = spell.materialText,
                    duration = spell.duration,
                    concentration = if (spell.concentration) 1 else 0,
                    ritual = if (spell.ritual) 1 else 0,
                    description = spell.description,
                    notes = spell.notes,
                    sort_order = levelOrder.toLong(),
                )
                spellOrderByLevel[spell.level] = levelOrder + 1
                spell.sourceAssociations.forEach { association ->
                    database.characterQueries.insertCharacterSpellSourceAssociation(
                        spell_id = spell.id.toString(),
                        source_id = association.sourceId.toString(),
                        prepared = if (association.prepared) 1 else 0,
                    )
                }
            }
        }

        return requireNotNull(character(sheet.id))
    }

    private fun requireCampaignExists(campaignId: Uuid) {
        val campaign = database.campaignQueries.selectCampaignById(campaignId.toString()).executeAsOneOrNull()
        require(campaign != null) { "Character campaign must already exist locally." }
    }

    private fun hydrate(core: CharacterCore): CharacterSheet {
        val classes = database.characterQueries.selectCharacterClasses(core.id.toString()) {
                id, _, name, level, hitDieSides, hitDiceRemaining, sortOrder ->
            CharacterClassLevel(
                id = Uuid.parse(id),
                name = name,
                level = level.toInt(),
                hitDieSides = hitDieSides.toInt(),
                hitDiceRemaining = hitDiceRemaining.toInt(),
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()
        val classIdSet = classes.map { it.id }.toSet()

        val storedSaves = database.characterQueries.selectCharacterSaves(core.id.toString()) {
                _, abilityKey, proficient, adjustment ->
            CharacterSavingThrow(
                ability = CharacterAbility.valueOf(abilityKey),
                proficient = proficient != 0L,
                adjustment = adjustment.toInt(),
            )
        }.executeAsList().associateBy { it.ability }

        val storedSkills = database.characterQueries.selectCharacterSkills(core.id.toString()) {
                _, skillKey, training, adjustment ->
            CharacterSkill(
                key = SkillKey.valueOf(skillKey),
                adjustment = adjustment.toInt(),
                training = SkillTraining.valueOf(training),
            )
        }.executeAsList().associateBy { it.key }

        val spellSlots = database.characterQueries.selectCharacterSpellSlots(core.id.toString()) {
                _, spellLevel, totalSlots, spentSlots ->
            CharacterSpellSlot(
                level = spellLevel.toInt(),
                totalSlots = totalSlots.toInt(),
                spentSlots = spentSlots.toInt(),
            )
        }.executeAsList()

        val combatEntries = database.characterQueries.selectCharacterCombatEntries(core.id.toString()) {
                id, _, name, type, attackModifier, damageEffect, rangeText, notes, sortOrder ->
            CharacterCombatEntry(
                id = Uuid.parse(id),
                name = name,
                type = CharacterCombatEntryType.valueOf(type),
                attackModifier = attackModifier?.toInt(),
                damageEffect = damageEffect,
                rangeText = rangeText,
                notes = notes,
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val inventoryItems = database.characterQueries.selectCharacterInventoryItems(core.id.toString()) {
                id, _, name, quantity, weightLb, equipped, notes, sortOrder, special, description, location, attuned ->
            CharacterInventoryItem(
                id = Uuid.parse(id),
                name = name,
                quantity = quantity.toInt(),
                weightLb = weightLb,
                equipped = equipped != 0L,
                notes = notes,
                sortOrder = sortOrder.toInt(),
                special = special != 0L,
                description = description,
                location = location,
                attuned = attuned != 0L,
            )
        }.executeAsList()

        val currencies = database.characterQueries.selectCharacterCurrencies(core.id.toString()) {
                _, key, name, amount, sortOrder, isDefault ->
            CharacterCurrency(
                key = key,
                name = name,
                amount = amount.toInt(),
                sortOrder = sortOrder.toInt(),
                isDefault = isDefault != 0L,
            )
        }.executeAsList()

        val background = database.characterQueries.selectCharacterBackground(core.id.toString()) {
                _, name, summary, personalityTraits, ideals, bonds, flaws, story ->
            CharacterBackground(
                name = name,
                summary = summary,
                personalityTraits = personalityTraits,
                ideals = ideals,
                bonds = bonds,
                flaws = flaws,
                story = story,
            )
        }.executeAsOneOrNull() ?: CharacterBackground()

        val traits = database.characterQueries.selectCharacterTraits(core.id.toString()) {
                id, _, name, source, type, description, notes, maxUses, spentUses, recovery, activation, sortOrder ->
            CharacterTrait(
                id = Uuid.parse(id),
                name = name,
                source = source,
                type = runCatching { CharacterTraitType.valueOf(type) }.getOrDefault(CharacterTraitType.OTHER),
                description = description,
                notes = notes,
                maxUses = maxUses?.toInt(),
                spentUses = spentUses.toInt(),
                recovery = recovery,
                activation = activation?.let {
                    runCatching { CharacterActivationType.valueOf(it) }.getOrDefault(CharacterActivationType.OTHER)
                },
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val noteCards = database.characterQueries.selectCharacterNotes(core.id.toString()) {
                id, _, title, content, sortOrder ->
            CharacterNote(
                id = Uuid.parse(id),
                title = title,
                content = content,
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val spellcastingSources = database.characterQueries.selectCharacterSpellSources(core.id.toString()) {
                id, _, name, linkedClassId, sortOrder ->
            val parsedClassId = linkedClassId?.let { runCatching { Uuid.parse(it) }.getOrNull() }
            CharacterSpellcastingSource(
                id = Uuid.parse(id),
                name = name,
                linkedClassId = parsedClassId?.takeIf { it in classIdSet },
                sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val spellAssociations = database.characterQueries.selectCharacterSpellSourceAssociations(core.id.toString()) {
                spellId, sourceId, prepared ->
            Uuid.parse(spellId) to CharacterSpellSourceAssociation(
                sourceId = Uuid.parse(sourceId),
                prepared = prepared != 0L,
            )
        }.executeAsList().groupBy(
            keySelector = { it.first },
            valueTransform = { it.second },
        )

        val spells = database.characterQueries.selectCharacterSpells(core.id.toString()) {
                id, _, name, spellLevel, castingTime, rangeText,
                hasVerbal, hasSomatic, hasMaterial, materialText, duration,
                concentration, ritual, description, notes, sortOrder ->
            val spellId = Uuid.parse(id)
            CharacterSpell(
                id = spellId,
                name = name,
                level = spellLevel.toInt(),
                castingTime = castingTime,
                rangeText = rangeText,
                verbal = hasVerbal != 0L,
                somatic = hasSomatic != 0L,
                material = hasMaterial != 0L,
                materialText = materialText,
                duration = duration,
                concentration = concentration != 0L,
                ritual = ritual != 0L,
                description = description,
                notes = notes,
                sortOrder = sortOrder.toInt(),
                sourceAssociations = spellAssociations[spellId].orEmpty(),
            )
        }.executeAsList()

        return CharacterSheet(
            id = core.id,
            campaignId = core.campaignId,
            name = core.name,
            status = core.status,
            updatedAtEpochSeconds = core.updatedAtEpochSeconds,
            strength = core.strength,
            dexterity = core.dexterity,
            constitution = core.constitution,
            intelligence = core.intelligence,
            wisdom = core.wisdom,
            charisma = core.charisma,
            armorClass = core.armorClass,
            maxHp = core.maxHp,
            currentHp = core.currentHp,
            tempHp = core.tempHp,
            initiativeAdjustment = core.initiativeAdjustment,
            speed = core.speed,
            proficiencyBonus = core.legacyProficiencyBonus,
            savingThrows = CharacterAbility.entries.map { ability ->
                storedSaves[ability]
                    ?: CharacterSavingThrow(ability = ability, proficient = false, adjustment = 0)
            },
            passivePerceptionAdjustment = core.passivePerceptionAdjustment,
            spellSaveDc = core.spellSaveDc,
            classes = classes,
            skills = SkillKey.entries.map { key ->
                storedSkills[key]
                    ?: CharacterSkill(key = key, adjustment = 0, training = SkillTraining.NONE)
            },
            proficiencyBonusAdjustment = core.proficiencyBonusAdjustment,
            spellAttackModifier = core.spellAttackModifier,
            spellcastingAbility = core.spellcastingAbility,
            spellSlots = spellSlots,
            combatEntries = combatEntries,
            inventoryItems = inventoryItems,
            currencies = currencies,
            spellcasterEnabled = core.spellcasterEnabled,
            background = background,
            traits = traits,
            spellcastingSources = spellcastingSources,
            spells = spells,
            generalNotes = core.generalNotes,
            noteCards = noteCards,
        )
    }

    private fun mapCore(
        id: String,
        campaignId: String,
        name: String,
        status: String,
        updatedAtEpochSeconds: Long,
        strength: Long,
        dexterity: Long,
        constitution: Long,
        intelligence: Long,
        wisdom: Long,
        charisma: Long,
        armorClass: Long,
        maxHp: Long,
        currentHp: Long,
        tempHp: Long,
        initiativeAdjustment: Long,
        speed: Long,
        legacyProficiencyBonus: Long,
        proficiencyBonusAdjustment: Long,
        passivePerceptionAdjustment: Long,
        spellSaveDc: Long?,
        spellAttackModifier: Long?,
        spellcastingAbility: String,
        spellcasterEnabled: Long,
        generalNotes: String,
    ) = CharacterCore(
        id = Uuid.parse(id),
        campaignId = Uuid.parse(campaignId),
        name = name,
        status = CharacterStatus.valueOf(status),
        updatedAtEpochSeconds = updatedAtEpochSeconds,
        strength = strength.toInt(),
        dexterity = dexterity.toInt(),
        constitution = constitution.toInt(),
        intelligence = intelligence.toInt(),
        wisdom = wisdom.toInt(),
        charisma = charisma.toInt(),
        armorClass = armorClass.toInt(),
        maxHp = maxHp.toInt(),
        currentHp = currentHp.toInt(),
        tempHp = tempHp.toInt(),
        initiativeAdjustment = initiativeAdjustment.toInt(),
        speed = speed.toInt(),
        legacyProficiencyBonus = legacyProficiencyBonus.toInt(),
        proficiencyBonusAdjustment = proficiencyBonusAdjustment.toInt(),
        passivePerceptionAdjustment = passivePerceptionAdjustment.toInt(),
        spellSaveDc = spellSaveDc?.toInt(),
        spellAttackModifier = spellAttackModifier?.toInt(),
        spellcastingAbility = runCatching { SpellcastingAbility.valueOf(spellcastingAbility) }
            .getOrDefault(SpellcastingAbility.NONE),
        spellcasterEnabled = spellcasterEnabled != 0L,
        generalNotes = generalNotes,
    )

    private data class CharacterCore(
        val id: Uuid,
        val campaignId: Uuid,
        val name: String,
        val status: CharacterStatus,
        val updatedAtEpochSeconds: Long,
        val strength: Int,
        val dexterity: Int,
        val constitution: Int,
        val intelligence: Int,
        val wisdom: Int,
        val charisma: Int,
        val armorClass: Int,
        val maxHp: Int,
        val currentHp: Int,
        val tempHp: Int,
        val initiativeAdjustment: Int,
        val speed: Int,
        val legacyProficiencyBonus: Int,
        val proficiencyBonusAdjustment: Int,
        val passivePerceptionAdjustment: Int,
        val spellSaveDc: Int?,
        val spellAttackModifier: Int?,
        val spellcastingAbility: SpellcastingAbility,
        val spellcasterEnabled: Boolean,
        val generalNotes: String,
    )

    private companion object {
        val defaultCurrencies = listOf(
            CharacterCurrency("cp", "Cobre", 0, 0, true),
            CharacterCurrency("sp", "Plata", 0, 1, true),
            CharacterCurrency("ep", "Electro", 0, 2, true),
            CharacterCurrency("gp", "Oro", 0, 3, true),
            CharacterCurrency("pp", "Platino", 0, 4, true),
        )
    }
}
