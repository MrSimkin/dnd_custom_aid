package io.github.mrsimkin.dndcustomaid.shared.dm

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryPayload
import io.github.mrsimkin.dndcustomaid.shared.content.RulesOrigin
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Redesign R5 (owner decisions D8, D9, D13): pick from the SRD, then homebrew, into the PC sheet. */
class RulesPickTest {
    private fun catalog(name: String): SrdCatalog {
        val root = generateSequence(File(".").absoluteFile) { it.parentFile }.first { File(it, "androidApp").isDirectory }
        return parseSrdCatalog(File(root, "androidApp/src/main/assets/srd/$name").readText())
    }

    private val srd55 by lazy { catalog("srd_catalog_5_2_1_es.json") }
    private val srd5 by lazy { catalog("srd_catalog_5_1_es.json") }

    @Test
    fun theCatalogsHaveEveryFamilyThePcEditorPicksFrom() {
        listOf(srd5, srd55).forEach { catalog ->
            listOf(RulesEntryKind.SPECIES, RulesEntryKind.CLASS, RulesEntryKind.SUBCLASS, RulesEntryKind.BACKGROUND, RulesEntryKind.FEAT, RulesEntryKind.SPELL, RulesEntryKind.WEAPON, RulesEntryKind.ARMOR)
                .forEach { kind -> assertTrue(catalog.of(kind).isNotEmpty(), "${catalog.version} has $kind") }
            assertEquals(12, catalog.of(RulesEntryKind.CLASS).size)
            assertTrue(catalog.of(RulesEntryKind.SPELL).size > 300, "${catalog.version} spells")
        }
    }

    @Test
    fun aSpellFillsEveryFieldOfAñadirConjuro() {
        val fireball = rulesOptions(RulesEntryKind.SPELL, listOf(srd55), emptyList(), query = "Bola de fuego").first { it.name == "Bola de fuego" }
        val pick = fireball.toSpellPick()
        assertEquals(3, pick.level)
        assertEquals("Evocación", pick.school)
        assertTrue(pick.verbal && pick.somatic && pick.material)
        assertTrue(pick.materialText.isNotBlank())
        assertFalse(pick.concentration)
        assertTrue(pick.description.contains("fuego"))

        val bless = rulesOptions(RulesEntryKind.SPELL, listOf(srd5), emptyList()).first { it.name == "Bendición" }.toSpellPick()
        assertTrue(bless.concentration, "«Concentración, hasta 1 minuto» marks concentration")
        assertEquals("Hasta 1 minuto", bless.duration)
    }

    @Test
    fun aWeaponFillsAnAttackAndAnItem() {
        val hammer = rulesOptions(RulesEntryKind.WEAPON, listOf(srd55), emptyList()).first { it.name == "Martillo de guerra" }
        assertEquals("1d8 contundente", hammer.toAttackPick().damageEffect)
        assertTrue(hammer.toAttackPick().notes.contains("Maestría"))
        assertEquals(5.0, hammer.toItemPick().weightLb, "2,5 kg in the Spanish SRD is 5 lb on the sheet")

        val dagger = rulesOptions(RulesEntryKind.WEAPON, listOf(srd5), emptyList()).first { it.name == "Daga" }.toAttackPick()
        assertEquals("6/18 m", dagger.rangeText)

        val mail = rulesOptions(RulesEntryKind.ARMOR, listOf(srd5), emptyList()).first { it.name == "Cota de malla" }.toItemPick()
        assertTrue(mail.description.startsWith("CA 16"))
    }

    @Test
    fun srdComesFirstThenTheHomebrewAndThePcEditionLeads() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val homebrew = RulesEntryContentRepository(database)
        homebrew.createCampaign(campaign.id, "Brujo (Manual Simkin)", RulesEntryPayload(RulesEntryKind.CLASS, summary = "Pacto con un patrón"), 100)
        val options = rulesOptions(RulesEntryKind.CLASS, listOf(srd5, srd55), homebrew.listCampaign(campaign.id), preferredEdition = "5e")
        assertEquals(RulesOrigin.SRD, options.first().origin)
        assertEquals("5e", options.first().edition, "the PC's edition first")
        assertEquals(RulesOrigin.HOMEBREW, options.last().origin)
        assertEquals("Brujo (Manual Simkin)", options.last().name)
        assertEquals("Bárbaro", options.first().name, "Spanish alphabetical order")
    }

    private fun db(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        driver.execute(null, "PRAGMA foreign_keys = ON", 0)
        try {
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
