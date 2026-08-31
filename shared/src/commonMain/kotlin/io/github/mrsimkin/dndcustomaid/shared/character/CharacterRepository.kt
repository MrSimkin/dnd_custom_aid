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
            require(classLevel.level > 0) { "Class level must be positive." }
            require(classLevel.hitDieSides > 0) { "Hit die size must be positive." }
            require(classLevel.hitDiceRemaining >= 0) { "Remaining hit dice must not be negative." }
        }

        val savesByAbility = sheet.savingThrows.associateBy { it.ability }
        require(savesByAbility.size == sheet.savingThrows.size) { "Each saving throw may appear only once." }

        val skillsByKey = sheet.skills.associateBy { it.key }
        require(skillsByKey.size == sheet.skills.size) { "Each skill may appear only once." }

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
                sheet.proficiencyBonus.toLong(),
                sheet.passivePerceptionAdjustment.toLong(),
                sheet.spellSaveDc?.toLong(),
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
            proficiencyBonus = core.proficiencyBonus,
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
        proficiencyBonus: Long,
        passivePerceptionAdjustment: Long,
        spellSaveDc: Long?,
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
        proficiencyBonus = proficiencyBonus.toInt(),
        passivePerceptionAdjustment = passivePerceptionAdjustment.toInt(),
        spellSaveDc = spellSaveDc?.toInt(),
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
        val proficiencyBonus: Int,
        val passivePerceptionAdjustment: Int,
        val spellSaveDc: Int?,
    )
}
