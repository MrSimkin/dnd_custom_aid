package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class CharacterSuccessorCustomSkillTest {
    @Test
    fun existingBuiltInCustomSkillProjectsAndCanMoveToCustomAttribute() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val closure = CharacterClosureRepository(database)
            val successor = CharacterSuccessorRepository(database)

            val campaign = campaigns.createCampaign("Habilidades")
            val character = characters.createCharacter(campaign.id, "Inventora")
            val skillId = Uuid.random()
            closure.saveState(
                character.id,
                CharacterClosureState(
                    customSkills = listOf(
                        CharacterCustomSkill(
                            id = skillId,
                            name = "Ingeniería arcana",
                            ability = CharacterAbility.INTELLIGENCE,
                            training = SkillTraining.PROFICIENT,
                        ),
                    ),
                ),
            )

            val projected = successor.state(character.id)
            assertEquals(CharacterAbility.INTELLIGENCE, projected.customSkillAbilities.single().ability.builtIn)

            val customAttributeId = Uuid.random()
            val saved = successor.saveState(
                character.id,
                projected.copy(
                    customAttributes = listOf(
                        CharacterCustomAttribute(
                            id = customAttributeId,
                            name = "Cordura",
                            abbreviation = "COR",
                            score = 14,
                        ),
                    ),
                    customSkillAbilities = listOf(
                        CharacterCustomSkillAbilityConfiguration(
                            customSkillId = skillId,
                            ability = CharacterAbilityReference.custom(customAttributeId),
                        ),
                    ),
                ),
            )

            assertEquals(customAttributeId, saved.customSkillAbilities.single().ability.customAttributeId)
            assertEquals(
                customAttributeId,
                closure.state(character.id).customSkills.single().abilityReference(saved).customAttributeId,
            )
            // Legacy V4 compatibility snapshot remains intact until the successor Habilidades UI takes over.
            assertEquals(CharacterAbility.INTELLIGENCE, closure.state(character.id).customSkills.single().ability)
        } finally {
            driver.close()
        }
    }
}
