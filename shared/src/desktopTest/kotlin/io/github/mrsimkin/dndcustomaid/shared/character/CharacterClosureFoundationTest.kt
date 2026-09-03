package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterClosureFoundationTest {
    @Test
    fun classSubclassProvenanceAndNewDomainsRoundTrip() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val campaign = campaigns.createCampaign("Closure")
            val original = characters.createCharacter(campaign.id, "Inventora")
            val classId = Uuid.random()
            val sourceId = Uuid.random()

            val saved = characters.saveCharacter(
                original.copy(
                    inspiration = true,
                    deathSaveSuccesses = 1,
                    deathSaveFailures = 2,
                    classes = listOf(
                        CharacterClassLevel(
                            id = classId,
                            name = "Artífice",
                            level = 7,
                            hitDieSides = 8,
                            hitDiceRemaining = 4,
                            sortOrder = 0,
                            rulesFamily = CharacterRulesFamily.DND_5_5E,
                            source = "Eberron: Forge of the Artificer",
                            catalogKey = "artificer-2025",
                            subclassName = "Battle Smith",
                            subclassSource = "Eberron: Forge of the Artificer",
                            subclassCatalogKey = "artificer-battle-smith-2025",
                            subclassRulesFamily = CharacterRulesFamily.DND_5_5E,
                        ),
                    ),
                    combatEntries = listOf(
                        CharacterCombatEntry(Uuid.random(), "Arcane Jolt", CharacterCombatEntryType.OTHER, null, "Resumen", null, null, 0, pinned = true),
                    ),
                    traits = listOf(
                        CharacterTrait(Uuid.random(), "Flash of Genius", "Artífice", CharacterTraitType.CLASS, "", null, 4, 1, "Descanso largo", CharacterActivationType.REACTION, 0, pinned = true),
                    ),
                    proficiencies = listOf(
                        CharacterProficiency(Uuid.random(), CharacterProficiencyType.TOOL, "Herramientas de ladrón", "Artífice"),
                    ),
                    weaponMasteries = listOf(
                        CharacterWeaponMastery(Uuid.random(), "Martillo de guerra", "Push", "D&D 5.5e"),
                    ),
                    resources = listOf(
                        CharacterResource(Uuid.random(), "Flash of Genius", 3, 4, "Descanso largo", "Artífice", pinned = true),
                    ),
                    classOptions = listOf(
                        CharacterClassOption(sourceId, classId, CharacterClassOptionKind.ARTIFICER_PLAN, "Bag of Holding", "Artífice", effectSummary = "Plan conocido"),
                    ),
                    forms = listOf(
                        CharacterForm(Uuid.random(), "Lobo", "Manual", "1/4", 13, 11, "40 ft", "Percepción", "Mordisco", pinned = true),
                    ),
                    companions = listOf(
                        CharacterCompanion(Uuid.random(), classId, "Defensor de acero", "Constructo", "Battle Smith", 17, 35, 31, 2, "40 ft", traitsActions = "Force-Empowered Rend"),
                    ),
                ),
            )

            val reopened = characters.character(saved.id)!!
            assertTrue(reopened.inspiration)
            assertEquals(1, reopened.deathSaveSuccesses)
            assertEquals(2, reopened.deathSaveFailures)
            assertEquals("Battle Smith", reopened.classes.single().subclassName)
            assertEquals("artificer-battle-smith-2025", reopened.classes.single().subclassCatalogKey)
            assertTrue(reopened.combatEntries.single().pinned)
            assertTrue(reopened.traits.single().pinned)
            assertEquals("Herramientas de ladrón", reopened.proficiencies.single().name)
            assertEquals("Push", reopened.weaponMasteries.single().masteryName)
            assertEquals(3, reopened.resources.single().currentValue)
            assertEquals(classId, reopened.classOptions.single().linkedClassId)
            assertEquals("Lobo", reopened.forms.single().name)
            assertEquals(classId, reopened.companions.single().linkedClassId)
        } finally {
            driver.close()
        }
    }

    @Test
    fun catalogEnablesOnlyMechanicallyUsefulConditionalModules() {
        val artificer = CharacterClassCatalog.byKey("artificer-2025")!!
        assertTrue(CharacterModuleKind.ARTIFICER in artificer.modules)
        val battleSmith = CharacterClassCatalog.subclassByKey("artificer-battle-smith-2025")!!
        assertTrue(CharacterModuleKind.COMPANIONS in battleSmith.modules)

        val druid = CharacterClassCatalog.byKey("druid-2024")!!
        assertTrue(CharacterModuleKind.FORMS in druid.modules)

        val fighter = CharacterClassLevel(
            id = Uuid.random(), name = "Guerrero", level = 3, hitDieSides = 10, hitDiceRemaining = 3, sortOrder = 0,
            rulesFamily = CharacterRulesFamily.DND_5_5E, catalogKey = "fighter-2024",
            subclassName = "Battle Master", subclassCatalogKey = "fighter-battle-master-2024",
        )
        val modules = CharacterClassCatalog.modulesFor(fighter)
        assertTrue(CharacterModuleKind.TECHNIQUES in modules)
        assertFalse(CharacterModuleKind.COMPANIONS in modules)
    }
}
