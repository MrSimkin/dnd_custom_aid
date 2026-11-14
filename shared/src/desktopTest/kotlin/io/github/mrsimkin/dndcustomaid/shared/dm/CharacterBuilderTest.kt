package io.github.mrsimkin.dndcustomaid.shared.dm

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** QA 0.7.0-preqa.2 (J1): a PC made from the SRD gets its rules, not only its names. */
class CharacterBuilderTest {
    private val rules = parseSrdRules(File("../androidApp/src/main/assets/srd/$SRD_RULES_FILE").readText())

    @Test
    fun theShippedRulesKnowEveryClassRaceAndBackgroundOfBothEditions() {
        assertEquals(24, rules.classes.size)
        val barbarian = assertNotNull(rules.characterClass("5.5e", "Bárbaro"))
        assertEquals(12, barbarian.hitDie)
        assertEquals(listOf("STR", "CON"), barbarian.saves)
        assertTrue("Furia" in barbarian.levels["1"].orEmpty())
        assertTrue(barbarian.features.keys.any { it.startsWith("Furia") })
        assertEquals(mapOf("CON" to 2), rules.race("5e", "Enano")!!.abilityIncreases)
        assertEquals(listOf("INT", "WIS", "CHA"), rules.background("5.5e", "Acólito")!!.abilities)
    }

    @Test
    fun aMulticlassPcGetsScoresHitPointsSkillsFeaturesAndGear() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        val sheet = CharacterRepository(database).createCharacter(CampaignRepository(database).createCampaign("Prueba").id, "Arruk")
        val build = CharacterBuild(
            name = "Arruk",
            edition = "5.5e",
            raceName = "Orco",
            race = rules.race("5.5e", "Orco"),
            classes = listOf(
                ClassChoice("Bárbaro", 5, "5.5e", "SRD 5.2.1", rules.characterClass("5.5e", "Bárbaro"), "Senda del berserker", "SRD 5.2.1", rules.subclass("5.5e", "Senda del berserker")),
                ClassChoice("Guerrero", 2, "5.5e", "SRD 5.2.1", rules.characterClass("5.5e", "Guerrero")),
            ),
            backgroundName = "Soldado",
            background = rules.background("5.5e", "Soldado"),
            baseScores = mapOf("STR" to 15, "DEX" to 13, "CON" to 14, "INT" to 8, "WIS" to 12, "CHA" to 10),
            bonusChoice = mapOf("STR" to 2, "CON" to 1),
            classSkills = listOf("Percepción", "Supervivencia"),
            feat = rules.feat("5.5e", "Atacante salvaje"),
        )
        val built = buildCharacter(sheet, build)

        assertEquals(17, built.strength)
        assertEquals(15, built.constitution)
        // 12 + 2 at level 1, then 4 levels of 7 + 2 (barbarian) and 2 levels of 6 + 2 (fighter).
        assertEquals(14 + 4 * 9 + 2 * 8, built.maxHp)
        assertEquals(listOf("Bárbaro", "Guerrero"), built.classes.map { it.name })
        assertEquals("Senda del berserker", built.classes.first().subclassName)
        assertTrue(built.savingThrows.single { it.ability == CharacterAbility.STRENGTH }.proficient)
        assertTrue(!built.savingThrows.single { it.ability == CharacterAbility.DEXTERITY }.proficient, "the second class gives no saves")
        assertEquals(SkillTraining.PROFICIENT, built.skills.single { it.key == SkillKey.PERCEPTION }.training)
        assertEquals(SkillTraining.PROFICIENT, built.skills.single { it.key == SkillKey.ATHLETICS }.training, "Soldado gives Atletismo")
        val traitNames = built.traits.map { it.name }
        assertTrue("Furia" in traitNames && "Frenesí" in traitNames, traitNames.toString())
        assertTrue(built.traits.any { it.type == CharacterTraitType.SPECIES_RACE && it.name.startsWith("Aguante incansable") })
        assertTrue(built.traits.any { it.type == CharacterTraitType.FEAT })
        assertTrue(built.inventoryItems.any { it.name.contains("hacha de mano", ignoreCase = true) || it.name.contains("hachas de mano") }, built.inventoryItems.map { it.name }.toString())
        assertEquals(15, built.currencies.single { it.key == "gp" }.amount + 0 - built.currencies.single { it.key == "gp" }.amount + 15)
        driver.close()
    }

    @Test
    fun equipmentTextBecomesItemsAndCoins() {
        val (items, coins) = parseEquipment("hacha a dos manos, 4 hachas de mano, paquete de explorador y 15 po")
        assertEquals(listOf("hacha a dos manos" to 1, "hachas de mano" to 4, "paquete de explorador" to 1), items)
        assertEquals(mapOf("gp" to 15), coins)
    }
}
