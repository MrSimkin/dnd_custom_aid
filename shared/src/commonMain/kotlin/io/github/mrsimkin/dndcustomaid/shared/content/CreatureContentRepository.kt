package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.uuid.Uuid

class CreatureContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
) {
    fun createPersonal(
        ownerAccountId: Uuid,
        rawDisplayName: String,
        payload: CreaturePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): CreatureContent {
        var result: CreatureContent? = null
        database.transaction {
            val item = reusableContent.createPersonal(
                ownerAccountId = ownerAccountId,
                family = ReusableContentFamily.CREATURE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = CreatureContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: CreaturePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): CreatureContent {
        var result: CreatureContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(
                campaignId = campaignId,
                family = ReusableContentFamily.CREATURE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = CreatureContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun creature(id: Uuid, includeDeleted: Boolean = false): CreatureContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.CREATURE) return null
        val payload = requireNotNull(payload(id)) {
            "Creature reusable content is missing its persisted Creature payload."
        }
        return CreatureContent(item, payload)
    }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): CreatureContent {
        var result: CreatureContent? = null
        database.transaction {
            val source = requireNotNull(creature(sourceId)) {
                "Creature source must exist and be active before it can be copied."
            }
            val copiedItem = reusableContent.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
                newId = newId,
            )
            require(copiedItem.family == ReusableContentFamily.CREATURE) {
                "Creature copy must retain the CREATURE family."
            }
            insertPayload(copiedItem.identity.id, source.payload)
            result = CreatureContent(copiedItem, source.payload)
        }
        return checkNotNull(result)
    }

    fun updatePayload(
        id: Uuid,
        expectedRevision: Revision,
        payload: CreaturePayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision = reusableContent.mutateContent(
        id = id,
        expectedRevision = expectedRevision,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
    ) { current ->
        require(current.family == ReusableContentFamily.CREATURE) {
            "Creature payload updates require CREATURE reusable content."
        }
        require(this.payload(id) != null) {
            "Creature reusable content is missing its persisted Creature payload."
        }
        updatePayloadRow(id, payload)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Creature reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.CREATURE) {
            "Creature deletion requires CREATURE reusable content."
        }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    private fun payload(id: Uuid): CreaturePayload? =
        database.creaturePayloadQueries.selectCreaturePayloadByContentId(
            content_id = id.toString(),
            mapper = ::mapPayload,
        ).executeAsOneOrNull()

    private fun insertPayload(id: Uuid, payload: CreaturePayload) {
        database.creaturePayloadQueries.insertCreaturePayload(
            content_id = id.toString(),
            size = payload.size,
            creature_type = payload.creatureType,
            alignment = payload.alignment,
            armor_class = payload.armorClass?.toLong(),
            armor_class_details = payload.armorClassDetails,
            hit_points = payload.hitPoints?.toLong(),
            hit_dice = payload.hitDice,
            speed = payload.speed,
            strength = payload.abilityScores.strength.toLong(),
            dexterity = payload.abilityScores.dexterity.toLong(),
            constitution = payload.abilityScores.constitution.toLong(),
            intelligence = payload.abilityScores.intelligence.toLong(),
            wisdom = payload.abilityScores.wisdom.toLong(),
            charisma = payload.abilityScores.charisma.toLong(),
            saving_throws = payload.savingThrows,
            skills = payload.skills,
            damage_vulnerabilities = payload.damageVulnerabilities,
            damage_resistances = payload.damageResistances,
            damage_immunities = payload.damageImmunities,
            condition_immunities = payload.conditionImmunities,
            senses = payload.senses,
            languages = payload.languages,
            challenge_rating = payload.challengeRating,
            proficiency_bonus = payload.proficiencyBonus?.toLong(),
            traits = payload.traits,
            actions = payload.actions,
            bonus_actions = payload.bonusActions,
            reactions = payload.reactions,
            legendary_actions = payload.legendaryActions,
            lair_actions = payload.lairActions,
            tactics = payload.tactics,
            notes = payload.notes,
        )
    }

    private fun updatePayloadRow(id: Uuid, payload: CreaturePayload) {
        database.creaturePayloadQueries.updateCreaturePayload(
            size = payload.size,
            creature_type = payload.creatureType,
            alignment = payload.alignment,
            armor_class = payload.armorClass?.toLong(),
            armor_class_details = payload.armorClassDetails,
            hit_points = payload.hitPoints?.toLong(),
            hit_dice = payload.hitDice,
            speed = payload.speed,
            strength = payload.abilityScores.strength.toLong(),
            dexterity = payload.abilityScores.dexterity.toLong(),
            constitution = payload.abilityScores.constitution.toLong(),
            intelligence = payload.abilityScores.intelligence.toLong(),
            wisdom = payload.abilityScores.wisdom.toLong(),
            charisma = payload.abilityScores.charisma.toLong(),
            saving_throws = payload.savingThrows,
            skills = payload.skills,
            damage_vulnerabilities = payload.damageVulnerabilities,
            damage_resistances = payload.damageResistances,
            damage_immunities = payload.damageImmunities,
            condition_immunities = payload.conditionImmunities,
            senses = payload.senses,
            languages = payload.languages,
            challenge_rating = payload.challengeRating,
            proficiency_bonus = payload.proficiencyBonus?.toLong(),
            traits = payload.traits,
            actions = payload.actions,
            bonus_actions = payload.bonusActions,
            reactions = payload.reactions,
            legendary_actions = payload.legendaryActions,
            lair_actions = payload.lairActions,
            tactics = payload.tactics,
            notes = payload.notes,
            content_id = id.toString(),
        )
    }

    private fun mapPayload(
        contentId: String,
        size: String,
        creatureType: String,
        alignment: String,
        armorClass: Long?,
        armorClassDetails: String,
        hitPoints: Long?,
        hitDice: String,
        speed: String,
        strength: Long,
        dexterity: Long,
        constitution: Long,
        intelligence: Long,
        wisdom: Long,
        charisma: Long,
        savingThrows: String,
        skills: String,
        damageVulnerabilities: String,
        damageResistances: String,
        damageImmunities: String,
        conditionImmunities: String,
        senses: String,
        languages: String,
        challengeRating: String,
        proficiencyBonus: Long?,
        traits: String,
        actions: String,
        bonusActions: String,
        reactions: String,
        legendaryActions: String,
        lairActions: String,
        tactics: String,
        notes: String,
    ): CreaturePayload {
        require(contentId.isNotBlank()) { "Creature payload content id must not be blank." }
        return CreaturePayload(
            size = size,
            creatureType = creatureType,
            alignment = alignment,
            armorClass = armorClass?.toInt(),
            armorClassDetails = armorClassDetails,
            hitPoints = hitPoints?.toInt(),
            hitDice = hitDice,
            speed = speed,
            abilityScores = CreatureAbilityScores(
                strength = strength.toInt(),
                dexterity = dexterity.toInt(),
                constitution = constitution.toInt(),
                intelligence = intelligence.toInt(),
                wisdom = wisdom.toInt(),
                charisma = charisma.toInt(),
            ),
            savingThrows = savingThrows,
            skills = skills,
            damageVulnerabilities = damageVulnerabilities,
            damageResistances = damageResistances,
            damageImmunities = damageImmunities,
            conditionImmunities = conditionImmunities,
            senses = senses,
            languages = languages,
            challengeRating = challengeRating,
            proficiencyBonus = proficiencyBonus?.toInt(),
            traits = traits,
            actions = actions,
            bonusActions = bonusActions,
            reactions = reactions,
            legendaryActions = legendaryActions,
            lairActions = lairActions,
            tactics = tactics,
            notes = notes,
        )
    }
}
