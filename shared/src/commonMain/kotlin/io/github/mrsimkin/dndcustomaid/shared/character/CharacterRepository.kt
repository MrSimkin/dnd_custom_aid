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
        require(sheet.deathSaveSuccesses in 0..3) { "Death-save successes must be between zero and three." }
        require(sheet.deathSaveFailures in 0..3) { "Death-save failures must be between zero and three." }

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

        requireDistinctIds(sheet.combatEntries.map { it.id }, "Combat entries")
        sheet.combatEntries.forEach { require(it.name.trim().isNotEmpty()) { "Combat entry name must not be blank." } }

        requireDistinctIds(sheet.inventoryItems.map { it.id }, "Inventory items")
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

        requireDistinctIds(sheet.traits.map { it.id }, "Character traits")
        sheet.traits.forEach { trait ->
            require(trait.name.trim().isNotEmpty()) { "Trait name must not be blank." }
            require(trait.maxUses == null || trait.maxUses >= 0) { "Trait maximum uses must not be negative." }
            require(trait.spentUses >= 0) { "Trait spent uses must not be negative." }
            require(trait.maxUses != null || trait.spentUses == 0) { "Trait spent uses require a configured maximum." }
            trait.maxUses?.let { maximum ->
                require(trait.spentUses <= maximum) { "Trait spent uses must not exceed maximum uses." }
            }
        }

        requireDistinctIds(sheet.noteCards.map { it.id }, "Titled notes")
        sheet.noteCards.forEach { require(it.title.trim().isNotEmpty()) { "Titled note title must not be blank." } }

        val sourceIds = sheet.spellcastingSources.map { it.id }
        require(sourceIds.distinct().size == sourceIds.size) { "Spellcasting sources must have distinct identity." }
        sheet.spellcastingSources.forEach { require(it.name.trim().isNotEmpty()) { "Spellcasting source name must not be blank." } }
        val normalizedSources = sheet.spellcastingSources.map { source ->
            source.copy(linkedClassId = source.linkedClassId?.takeIf { it in classIdSet })
        }
        val normalizedSourceIds = normalizedSources.map { it.id }.toSet()

        requireDistinctIds(sheet.spells.map { it.id }, "Spells")
        sheet.spells.forEach { spell ->
            require(spell.name.trim().isNotEmpty()) { "Spell name must not be blank." }
            require(spell.level in 0..9) { "Spell level must be between 0 and 9." }
            val associationIds = spell.sourceAssociations.map { it.sourceId }
            require(associationIds.distinct().size == associationIds.size) { "A spell may associate with each spellcasting source only once." }
        }
        val normalizedSpells = sheet.spells.map { spell ->
            spell.copy(sourceAssociations = spell.sourceAssociations.filter { it.sourceId in normalizedSourceIds })
        }

        requireDistinctIds(sheet.proficiencies.map { it.id }, "Proficiencies")
        sheet.proficiencies.forEach { require(it.name.trim().isNotEmpty()) { "Proficiency name must not be blank." } }
        requireDistinctIds(sheet.weaponMasteries.map { it.id }, "Weapon masteries")
        sheet.weaponMasteries.forEach {
            require(it.weaponName.trim().isNotEmpty()) { "Weapon mastery weapon name must not be blank." }
            require(it.masteryName.trim().isNotEmpty()) { "Weapon mastery name must not be blank." }
        }
        requireDistinctIds(sheet.resources.map { it.id }, "Resources")
        sheet.resources.forEach { resource ->
            require(resource.name.trim().isNotEmpty()) { "Resource name must not be blank." }
            require(resource.currentValue >= 0) { "Resource current value must not be negative." }
            require(resource.maxValue == null || resource.maxValue >= 0) { "Resource maximum must not be negative." }
            require(resource.maxValue == null || resource.currentValue <= resource.maxValue) { "Resource current value must not exceed maximum." }
        }
        requireDistinctIds(sheet.classOptions.map { it.id }, "Class options")
        val normalizedClassOptions = sheet.classOptions.map { option ->
            require(option.name.trim().isNotEmpty()) { "Class option name must not be blank." }
            option.copy(linkedClassId = option.linkedClassId?.takeIf { it in classIdSet })
        }
        requireDistinctIds(sheet.forms.map { it.id }, "Forms")
        sheet.forms.forEach {
            require(it.name.trim().isNotEmpty()) { "Form name must not be blank." }
            require(it.armorClass == null || it.armorClass >= 0) { "Form armor class must not be negative." }
            require(it.hitPoints == null || it.hitPoints >= 0) { "Form hit points must not be negative." }
        }
        requireDistinctIds(sheet.companions.map { it.id }, "Companions")
        val normalizedCompanions = sheet.companions.map { companion ->
            require(companion.name.trim().isNotEmpty()) { "Companion name must not be blank." }
            require(companion.armorClass == null || companion.armorClass >= 0) { "Companion armor class must not be negative." }
            require(companion.maxHp == null || companion.maxHp >= 0) { "Companion max HP must not be negative." }
            require(companion.currentHp == null || companion.currentHp >= 0) { "Companion current HP must not be negative." }
            require(companion.tempHp >= 0) { "Companion temporary HP must not be negative." }
            companion.copy(linkedClassId = companion.linkedClassId?.takeIf { it in classIdSet })
        }

        val standardProficiency = standardProficiencyBonusForLevel(sheet.totalLevel)
        val adjustmentImpliedByCompatibilitySnapshot = sheet.proficiencyBonus - standardProficiency
        val proficiencyAdjustment = if (
            sheet.proficiencyBonus == standardProficiency + sheet.proficiencyBonusAdjustment
        ) sheet.proficiencyBonusAdjustment else adjustmentImpliedByCompatibilitySnapshot
        val finalProficiency = standardProficiency + proficiencyAdjustment

        database.transaction {
            database.characterQueries.updateCharacterCore(
                name = sheet.name.trim(),
                status = sheet.status.name,
                strength = sheet.strength.toLong(),
                dexterity = sheet.dexterity.toLong(),
                constitution = sheet.constitution.toLong(),
                intelligence = sheet.intelligence.toLong(),
                wisdom = sheet.wisdom.toLong(),
                charisma = sheet.charisma.toLong(),
                armor_class = sheet.armorClass.toLong(),
                max_hp = sheet.maxHp.toLong(),
                current_hp = sheet.currentHp.toLong(),
                temp_hp = sheet.tempHp.toLong(),
                initiative_adjustment = sheet.initiativeAdjustment.toLong(),
                speed = sheet.speed.toLong(),
                proficiency_bonus = finalProficiency.toLong(),
                proficiency_bonus_adjustment = proficiencyAdjustment.toLong(),
                passive_perception_adjustment = sheet.passivePerceptionAdjustment.toLong(),
                spell_save_dc = sheet.spellSaveDc?.toLong(),
                spell_attack_modifier = sheet.spellAttackModifier?.toLong(),
                spellcasting_ability = sheet.spellcastingAbility.name,
                spellcaster_enabled = if (sheet.spellcasterEnabled) 1 else 0,
                general_notes = sheet.generalNotes,
                inspiration = if (sheet.inspiration) 1 else 0,
                death_save_successes = sheet.deathSaveSuccesses.toLong(),
                death_save_failures = sheet.deathSaveFailures.toLong(),
                id = sheet.id.toString(),
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
                    rules_family = classLevel.rulesFamily.name,
                    source = classLevel.source,
                    catalog_key = classLevel.catalogKey,
                    subclass_name = classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() },
                    subclass_source = classLevel.subclassSource,
                    subclass_catalog_key = classLevel.subclassCatalogKey,
                    subclass_rules_family = classLevel.subclassRulesFamily.name,
                )
            }

            CharacterAbility.entries.forEach { ability ->
                val save = savesByAbility[ability] ?: CharacterSavingThrow(ability, false, 0)
                database.characterQueries.insertCharacterSave(sheet.id.toString(), ability.name, if (save.proficient) 1 else 0, save.adjustment.toLong())
            }

            SkillKey.entries.forEach { key ->
                val skill = skillsByKey[key] ?: CharacterSkill(key, 0, SkillTraining.NONE)
                database.characterQueries.insertCharacterSkill(sheet.id.toString(), key.name, skill.training.name, skill.adjustment.toLong())
            }

            database.characterQueries.deleteCharacterSpellSlots(sheet.id.toString())
            sheet.spellSlots.sortedBy { it.level }.forEach { slot ->
                database.characterQueries.insertCharacterSpellSlot(sheet.id.toString(), slot.level.toLong(), slot.totalSlots.toLong(), slot.spentSlots.toLong())
            }

            database.characterQueries.deleteCharacterCombatEntries(sheet.id.toString())
            sheet.combatEntries.forEachIndexed { index, entry ->
                database.characterQueries.insertCharacterCombatEntry(
                    id = entry.id.toString(), character_id = sheet.id.toString(), name = entry.name.trim(), type = entry.type.name,
                    attack_modifier = entry.attackModifier?.toLong(), damage_effect = entry.damageEffect, range_text = entry.rangeText,
                    notes = entry.notes, sort_order = index.toLong(), pinned = if (entry.pinned) 1 else 0,
                )
            }

            database.characterQueries.deleteCharacterInventoryItems(sheet.id.toString())
            sheet.inventoryItems.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterInventoryItem(
                    id = item.id.toString(), character_id = sheet.id.toString(), name = item.name.trim(), quantity = item.quantity.toLong(),
                    weight_lb = item.weightLb, equipped = if (item.equipped) 1 else 0, notes = item.notes, sort_order = index.toLong(),
                    special = if (item.special) 1 else 0, description = item.description, location = item.location, attuned = if (item.attuned) 1 else 0,
                )
            }

            database.characterQueries.deleteCharacterCurrencies(sheet.id.toString())
            sheet.currencies.forEachIndexed { index, currency ->
                database.characterQueries.insertCharacterCurrency(sheet.id.toString(), currency.key, currency.name.trim(), currency.amount.toLong(), index.toLong(), if (currency.isDefault) 1 else 0)
            }

            database.characterQueries.upsertCharacterBackground(
                character_id = sheet.id.toString(), background_name = sheet.background.name, summary = sheet.background.summary,
                race = sheet.background.race, religion_faith = sheet.background.religionFaith, personality_traits = sheet.background.personalityTraits,
                ideals = sheet.background.ideals, bonds = sheet.background.bonds, flaws = sheet.background.flaws, story = sheet.background.story,
            )

            database.characterQueries.deleteCharacterTraits(sheet.id.toString())
            sheet.traits.forEachIndexed { index, trait ->
                database.characterQueries.insertCharacterTrait(
                    id = trait.id.toString(), character_id = sheet.id.toString(), name = trait.name.trim(), source = trait.source.trim(),
                    trait_type = trait.type.name, description = trait.description, notes = trait.notes, max_uses = trait.maxUses?.toLong(),
                    spent_uses = trait.spentUses.toLong(), recovery = trait.recovery, activation = trait.activation?.name,
                    sort_order = index.toLong(), pinned = if (trait.pinned) 1 else 0,
                )
            }

            database.characterQueries.deleteCharacterNotes(sheet.id.toString())
            sheet.noteCards.forEachIndexed { index, note ->
                database.characterQueries.insertCharacterNote(note.id.toString(), sheet.id.toString(), note.title.trim(), note.content, index.toLong())
            }

            database.characterMaintenanceQueries.deleteSpellSourceAssociationsForCharacter(sheet.id.toString())
            database.characterQueries.deleteCharacterSpells(sheet.id.toString())
            database.characterQueries.deleteCharacterSpellSources(sheet.id.toString())
            normalizedSources.forEachIndexed { index, source ->
                database.characterQueries.insertCharacterSpellSource(source.id.toString(), sheet.id.toString(), source.name.trim(), source.linkedClassId?.toString(), index.toLong())
            }
            val spellOrderByLevel = mutableMapOf<Int, Int>()
            normalizedSpells.forEach { spell ->
                val levelOrder = spellOrderByLevel[spell.level] ?: 0
                database.characterQueries.insertCharacterSpell(
                    id = spell.id.toString(), character_id = sheet.id.toString(), name = spell.name.trim(), spell_level = spell.level.toLong(),
                    casting_time = spell.castingTime, range_text = spell.rangeText, has_verbal = if (spell.verbal) 1 else 0,
                    has_somatic = if (spell.somatic) 1 else 0, has_material = if (spell.material) 1 else 0, material_text = spell.materialText,
                    duration = spell.duration, concentration = if (spell.concentration) 1 else 0, ritual = if (spell.ritual) 1 else 0,
                    description = spell.description, notes = spell.notes, sort_order = levelOrder.toLong(), pinned = if (spell.pinned) 1 else 0,
                )
                spellOrderByLevel[spell.level] = levelOrder + 1
                spell.sourceAssociations.forEach { association ->
                    database.characterQueries.insertCharacterSpellSourceAssociation(spell.id.toString(), association.sourceId.toString(), if (association.prepared) 1 else 0)
                }
            }

            database.characterQueries.deleteCharacterProficiencies(sheet.id.toString())
            sheet.proficiencies.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterProficiency(item.id.toString(), sheet.id.toString(), item.type.name, item.name.trim(), item.source, item.notes, index.toLong())
            }
            database.characterQueries.deleteCharacterWeaponMasteries(sheet.id.toString())
            sheet.weaponMasteries.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterWeaponMastery(item.id.toString(), sheet.id.toString(), item.weaponName.trim(), item.masteryName.trim(), item.source, item.notes, index.toLong())
            }
            database.characterQueries.deleteCharacterResources(sheet.id.toString())
            sheet.resources.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterResource(item.id.toString(), sheet.id.toString(), item.name.trim(), item.currentValue.toLong(), item.maxValue?.toLong(), item.recovery, item.source, item.notes, if (item.pinned) 1 else 0, index.toLong())
            }
            database.characterQueries.deleteCharacterClassOptions(sheet.id.toString())
            normalizedClassOptions.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterClassOption(
                    item.id.toString(), sheet.id.toString(), item.linkedClassId?.toString(), item.kind.name, item.name.trim(), item.source,
                    item.costText, item.effectSummary, item.notes, if (item.active) 1 else 0, if (item.pinned) 1 else 0, index.toLong(),
                )
            }
            database.characterQueries.deleteCharacterForms(sheet.id.toString())
            sheet.forms.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterForm(
                    item.id.toString(), sheet.id.toString(), item.name.trim(), item.source, item.challengeRatingText,
                    item.armorClass?.toLong(), item.hitPoints?.toLong(), item.movement, item.senses, item.actionSummary, item.notes,
                    if (item.pinned) 1 else 0, index.toLong(),
                )
            }
            database.characterQueries.deleteCharacterCompanions(sheet.id.toString())
            normalizedCompanions.forEachIndexed { index, item ->
                database.characterQueries.insertCharacterCompanion(
                    item.id.toString(), sheet.id.toString(), item.linkedClassId?.toString(), item.name.trim(), item.kind, item.source,
                    item.armorClass?.toLong(), item.maxHp?.toLong(), item.currentHp?.toLong(), item.tempHp.toLong(), item.speed,
                    item.abilitySummary, item.sensesProficiencies, item.traitsActions, item.notes, if (item.active) 1 else 0, index.toLong(),
                )
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
                id, _, name, level, hitDieSides, hitDiceRemaining, sortOrder, rulesFamily, source, catalogKey,
                subclassName, subclassSource, subclassCatalogKey, subclassRulesFamily ->
            CharacterClassLevel(
                id = Uuid.parse(id), name = name, level = level.toInt(), hitDieSides = hitDieSides.toInt(),
                hitDiceRemaining = hitDiceRemaining.toInt(), sortOrder = sortOrder.toInt(),
                rulesFamily = rulesFamily.toRulesFamily(), source = source, catalogKey = catalogKey,
                subclassName = subclassName, subclassSource = subclassSource, subclassCatalogKey = subclassCatalogKey,
                subclassRulesFamily = subclassRulesFamily.toRulesFamily(),
            )
        }.executeAsList()
        val classIdSet = classes.map { it.id }.toSet()

        val storedSaves = database.characterQueries.selectCharacterSaves(core.id.toString()) { _, abilityKey, proficient, adjustment ->
            CharacterSavingThrow(CharacterAbility.valueOf(abilityKey), proficient != 0L, adjustment.toInt())
        }.executeAsList().associateBy { it.ability }

        val storedSkills = database.characterQueries.selectCharacterSkills(core.id.toString()) { _, skillKey, training, adjustment ->
            CharacterSkill(SkillKey.valueOf(skillKey), adjustment.toInt(), SkillTraining.valueOf(training))
        }.executeAsList().associateBy { it.key }

        val spellSlots = database.characterQueries.selectCharacterSpellSlots(core.id.toString()) { _, level, total, spent ->
            CharacterSpellSlot(level.toInt(), total.toInt(), spent.toInt())
        }.executeAsList()

        val combatEntries = database.characterQueries.selectCharacterCombatEntries(core.id.toString()) {
                id, _, name, type, attackModifier, damageEffect, rangeText, notes, sortOrder, pinned ->
            CharacterCombatEntry(Uuid.parse(id), name, CharacterCombatEntryType.valueOf(type), attackModifier?.toInt(), damageEffect, rangeText, notes, sortOrder.toInt(), pinned != 0L)
        }.executeAsList()

        val inventoryItems = database.characterQueries.selectCharacterInventoryItems(core.id.toString()) {
                id, _, name, quantity, weightLb, equipped, notes, sortOrder, special, description, location, attuned ->
            CharacterInventoryItem(Uuid.parse(id), name, quantity.toInt(), weightLb, equipped != 0L, notes, sortOrder.toInt(), special != 0L, description, location, attuned != 0L)
        }.executeAsList()

        val currencies = database.characterQueries.selectCharacterCurrencies(core.id.toString()) { _, key, name, amount, sortOrder, isDefault ->
            CharacterCurrency(key, name, amount.toInt(), sortOrder.toInt(), isDefault != 0L)
        }.executeAsList()

        val background = database.characterQueries.selectCharacterBackground(core.id.toString()) {
                _, name, summary, race, religionFaith, personalityTraits, ideals, bonds, flaws, story ->
            CharacterBackground(name, summary, race, religionFaith, personalityTraits, ideals, bonds, flaws, story)
        }.executeAsOneOrNull() ?: CharacterBackground()

        val traits = database.characterQueries.selectCharacterTraits(core.id.toString()) {
                id, _, name, source, type, description, notes, maxUses, spentUses, recovery, activation, sortOrder, pinned ->
            CharacterTrait(
                Uuid.parse(id), name, source, runCatching { CharacterTraitType.valueOf(type) }.getOrDefault(CharacterTraitType.OTHER),
                description, notes, maxUses?.toInt(), spentUses.toInt(), recovery,
                activation?.let { runCatching { CharacterActivationType.valueOf(it) }.getOrDefault(CharacterActivationType.OTHER) },
                sortOrder.toInt(), pinned != 0L,
            )
        }.executeAsList()

        val noteCards = database.characterQueries.selectCharacterNotes(core.id.toString()) { id, _, title, content, sortOrder ->
            CharacterNote(Uuid.parse(id), title, content, sortOrder.toInt())
        }.executeAsList()

        val spellcastingSources = database.characterQueries.selectCharacterSpellSources(core.id.toString()) { id, _, name, linkedClassId, sortOrder ->
            val parsed = linkedClassId?.let { runCatching { Uuid.parse(it) }.getOrNull() }?.takeIf { it in classIdSet }
            CharacterSpellcastingSource(Uuid.parse(id), name, parsed, sortOrder.toInt())
        }.executeAsList()

        val spellAssociations = database.characterQueries.selectCharacterSpellSourceAssociations(core.id.toString()) { spellId, sourceId, prepared ->
            Uuid.parse(spellId) to CharacterSpellSourceAssociation(Uuid.parse(sourceId), prepared != 0L)
        }.executeAsList().groupBy(keySelector = { it.first }, valueTransform = { it.second })

        val spells = database.characterQueries.selectCharacterSpells(core.id.toString()) {
                id, _, name, spellLevel, castingTime, rangeText, hasVerbal, hasSomatic, hasMaterial, materialText,
                duration, concentration, ritual, description, notes, sortOrder, pinned ->
            val spellId = Uuid.parse(id)
            CharacterSpell(
                id = spellId, name = name, level = spellLevel.toInt(), castingTime = castingTime, rangeText = rangeText,
                verbal = hasVerbal != 0L, somatic = hasSomatic != 0L, material = hasMaterial != 0L, materialText = materialText,
                duration = duration, concentration = concentration != 0L, ritual = ritual != 0L, description = description,
                notes = notes, sortOrder = sortOrder.toInt(), sourceAssociations = spellAssociations[spellId].orEmpty(), pinned = pinned != 0L,
            )
        }.executeAsList()

        val proficiencies = database.characterQueries.selectCharacterProficiencies(core.id.toString()) { id, _, type, name, source, notes, sortOrder ->
            CharacterProficiency(Uuid.parse(id), runCatching { CharacterProficiencyType.valueOf(type) }.getOrDefault(CharacterProficiencyType.OTHER), name, source, notes, sortOrder.toInt())
        }.executeAsList()

        val weaponMasteries = database.characterQueries.selectCharacterWeaponMasteries(core.id.toString()) { id, _, weaponName, masteryName, source, notes, sortOrder ->
            CharacterWeaponMastery(Uuid.parse(id), weaponName, masteryName, source, notes, sortOrder.toInt())
        }.executeAsList()

        val resources = database.characterQueries.selectCharacterResources(core.id.toString()) { id, _, name, current, max, recovery, source, notes, pinned, sortOrder ->
            CharacterResource(Uuid.parse(id), name, current.toInt(), max?.toInt(), recovery, source, notes, pinned != 0L, sortOrder.toInt())
        }.executeAsList()

        val classOptions = database.characterQueries.selectCharacterClassOptions(core.id.toString()) {
                id, _, linkedClassId, kind, name, source, costText, effectSummary, notes, active, pinned, sortOrder ->
            CharacterClassOption(
                id = Uuid.parse(id), linkedClassId = linkedClassId.toValidClassId(classIdSet),
                kind = runCatching { CharacterClassOptionKind.valueOf(kind) }.getOrDefault(CharacterClassOptionKind.OTHER),
                name = name, source = source, costText = costText, effectSummary = effectSummary, notes = notes,
                active = active != 0L, pinned = pinned != 0L, sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        val forms = database.characterQueries.selectCharacterForms(core.id.toString()) {
                id, _, name, source, cr, armorClass, hitPoints, movement, senses, actionSummary, notes, pinned, sortOrder ->
            CharacterForm(Uuid.parse(id), name, source, cr, armorClass?.toInt(), hitPoints?.toInt(), movement, senses, actionSummary, notes, pinned != 0L, sortOrder.toInt())
        }.executeAsList()

        val companions = database.characterQueries.selectCharacterCompanions(core.id.toString()) {
                id, _, linkedClassId, name, kind, source, armorClass, maxHp, currentHp, tempHp, speed,
                abilitySummary, sensesProficiencies, traitsActions, notes, active, sortOrder ->
            CharacterCompanion(
                id = Uuid.parse(id), linkedClassId = linkedClassId.toValidClassId(classIdSet), name = name, kind = kind, source = source,
                armorClass = armorClass?.toInt(), maxHp = maxHp?.toInt(), currentHp = currentHp?.toInt(), tempHp = tempHp.toInt(),
                speed = speed, abilitySummary = abilitySummary, sensesProficiencies = sensesProficiencies, traitsActions = traitsActions,
                notes = notes, active = active != 0L, sortOrder = sortOrder.toInt(),
            )
        }.executeAsList()

        return CharacterSheet(
            id = core.id, campaignId = core.campaignId, name = core.name, status = core.status,
            updatedAtEpochSeconds = core.updatedAtEpochSeconds, strength = core.strength, dexterity = core.dexterity,
            constitution = core.constitution, intelligence = core.intelligence, wisdom = core.wisdom, charisma = core.charisma,
            armorClass = core.armorClass, maxHp = core.maxHp, currentHp = core.currentHp, tempHp = core.tempHp,
            initiativeAdjustment = core.initiativeAdjustment, speed = core.speed, proficiencyBonus = core.legacyProficiencyBonus,
            savingThrows = CharacterAbility.entries.map { storedSaves[it] ?: CharacterSavingThrow(it, false, 0) },
            passivePerceptionAdjustment = core.passivePerceptionAdjustment, spellSaveDc = core.spellSaveDc, classes = classes,
            skills = SkillKey.entries.map { storedSkills[it] ?: CharacterSkill(it, 0, SkillTraining.NONE) },
            proficiencyBonusAdjustment = core.proficiencyBonusAdjustment, spellAttackModifier = core.spellAttackModifier,
            spellcastingAbility = core.spellcastingAbility, spellSlots = spellSlots, combatEntries = combatEntries,
            inventoryItems = inventoryItems, currencies = currencies, spellcasterEnabled = core.spellcasterEnabled,
            background = background, traits = traits, spellcastingSources = spellcastingSources, spells = spells,
            generalNotes = core.generalNotes, noteCards = noteCards, inspiration = core.inspiration,
            deathSaveSuccesses = core.deathSaveSuccesses, deathSaveFailures = core.deathSaveFailures,
            proficiencies = proficiencies, weaponMasteries = weaponMasteries, resources = resources,
            classOptions = classOptions, forms = forms, companions = companions,
        )
    }

    private fun mapCore(
        id: String, campaignId: String, name: String, status: String, updatedAtEpochSeconds: Long,
        strength: Long, dexterity: Long, constitution: Long, intelligence: Long, wisdom: Long, charisma: Long,
        armorClass: Long, maxHp: Long, currentHp: Long, tempHp: Long, initiativeAdjustment: Long, speed: Long,
        legacyProficiencyBonus: Long, proficiencyBonusAdjustment: Long, passivePerceptionAdjustment: Long,
        spellSaveDc: Long?, spellAttackModifier: Long?, spellcastingAbility: String, spellcasterEnabled: Long,
        generalNotes: String, inspiration: Long, deathSaveSuccesses: Long, deathSaveFailures: Long,
    ) = CharacterCore(
        id = Uuid.parse(id), campaignId = Uuid.parse(campaignId), name = name,
        status = runCatching { CharacterStatus.valueOf(status) }.getOrDefault(CharacterStatus.ACTIVE),
        updatedAtEpochSeconds = updatedAtEpochSeconds, strength = strength.toInt(), dexterity = dexterity.toInt(),
        constitution = constitution.toInt(), intelligence = intelligence.toInt(), wisdom = wisdom.toInt(), charisma = charisma.toInt(),
        armorClass = armorClass.toInt(), maxHp = maxHp.toInt(), currentHp = currentHp.toInt(), tempHp = tempHp.toInt(),
        initiativeAdjustment = initiativeAdjustment.toInt(), speed = speed.toInt(), legacyProficiencyBonus = legacyProficiencyBonus.toInt(),
        proficiencyBonusAdjustment = proficiencyBonusAdjustment.toInt(), passivePerceptionAdjustment = passivePerceptionAdjustment.toInt(),
        spellSaveDc = spellSaveDc?.toInt(), spellAttackModifier = spellAttackModifier?.toInt(),
        spellcastingAbility = runCatching { SpellcastingAbility.valueOf(spellcastingAbility) }.getOrDefault(SpellcastingAbility.NONE),
        spellcasterEnabled = spellcasterEnabled != 0L, generalNotes = generalNotes, inspiration = inspiration != 0L,
        deathSaveSuccesses = deathSaveSuccesses.toInt(), deathSaveFailures = deathSaveFailures.toInt(),
    )

    private data class CharacterCore(
        val id: Uuid, val campaignId: Uuid, val name: String, val status: CharacterStatus, val updatedAtEpochSeconds: Long,
        val strength: Int, val dexterity: Int, val constitution: Int, val intelligence: Int, val wisdom: Int, val charisma: Int,
        val armorClass: Int, val maxHp: Int, val currentHp: Int, val tempHp: Int, val initiativeAdjustment: Int, val speed: Int,
        val legacyProficiencyBonus: Int, val proficiencyBonusAdjustment: Int, val passivePerceptionAdjustment: Int,
        val spellSaveDc: Int?, val spellAttackModifier: Int?, val spellcastingAbility: SpellcastingAbility,
        val spellcasterEnabled: Boolean, val generalNotes: String, val inspiration: Boolean,
        val deathSaveSuccesses: Int, val deathSaveFailures: Int,
    )

    private fun requireDistinctIds(ids: List<Uuid>, label: String) {
        require(ids.distinct().size == ids.size) { "$label must have distinct identity." }
    }

    private fun String?.toValidClassId(classIdSet: Set<Uuid>): Uuid? =
        this?.let { runCatching { Uuid.parse(it) }.getOrNull() }?.takeIf { it in classIdSet }

    private fun String.toRulesFamily(): CharacterRulesFamily =
        runCatching { CharacterRulesFamily.valueOf(this) }.getOrDefault(CharacterRulesFamily.UNSPECIFIED)

    private companion object {
        val defaultCurrencies = listOf(
            CharacterCurrency("cp", "Cobre", 0, 0, true), CharacterCurrency("sp", "Plata", 0, 1, true),
            CharacterCurrency("ep", "Electro", 0, 2, true), CharacterCurrency("gp", "Oro", 0, 3, true),
            CharacterCurrency("pp", "Platino", 0, 4, true),
        )
    }
}
