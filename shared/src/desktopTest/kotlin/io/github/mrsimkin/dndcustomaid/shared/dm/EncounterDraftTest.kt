package io.github.mrsimkin.dndcustomaid.shared.dm

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipantReadiness
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** Owner observation E1 (0.7.0-preqa.4): choose, change, make up and save an encounter before combat. */
class EncounterDraftTest {
    private fun db(block: (AppDatabase, kotlin.uuid.Uuid) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        try { block(database, campaign.id) } finally { driver.close() }
    }

    @Test
    fun aChangedEncounterEntersTheCombatAndTheSavedOneStaysAsItWas() = db { database, campaignId ->
        val creatures = CreatureContentRepository(database)
        val obrero = creatures.createCampaign(campaignId, "Obrero alterado", CreaturePayload(armorClass = 14, hitPoints = 31), 10)
        val encargado = creatures.createCampaign(campaignId, "Encargado", CreaturePayload(armorClass = 15, hitPoints = 44), 10)
        val patrulla = EncounterContentRepository(database).createCampaign(
            campaignId, "Patrulla",
            EncounterPayload(
                dmGuidance = "Empujan hacia el borde.",
                participants = listOf(
                    EncounterParticipant(sourceContentId = obrero.item.identity.id, quantity = 2),
                    EncounterParticipant(sourceContentId = encargado.item.identity.id, quantity = 1, readiness = EncounterParticipantReadiness.RESERVE),
                ),
            ),
            10,
        )

        val draft = EncounterDraft.fromEncounter(patrulla)
        assertFalse(draft.differsFrom(patrulla))
        val changed = draft.withQuantity(draft.entries.first().key, 3).plusImprovised("Perro guardián", armorClass = 12, hitPoints = 9)
        assertTrue(changed.differsFrom(patrulla))

        val byId = listOf(obrero, encargado).associateBy { it.item.identity.id }
        val combat = changed.applyTo(CombatEngine.new(campaignId, "Combate 1", 20), byId)
        assertEquals(3, combat.combatants.count { it.sourceContentId == obrero.item.identity.id && !it.reserve })
        assertTrue(combat.combatants.single { it.sourceContentId == encargado.item.identity.id }.reserve, "reserve stays in reserve")
        assertEquals(12, combat.combatants.single { it.label == "Perro guardián" }.baseArmorClass)
        assertEquals(patrulla.item.identity.id, combat.encounterId, "the guide still comes with it")

        // Using it in the combat did not touch the saved encounter.
        val saved = EncounterContentRepository(database).encounter(patrulla.item.identity.id)!!
        assertEquals(listOf(2, 1), saved.payload.participants.map { it.quantity })
    }

    @Test
    fun savingAsNewKeepsTheOriginalAndSavingChangesUpdatesIt() = db { database, campaignId ->
        val creatures = CreatureContentRepository(database)
        val goblin = creatures.createCampaign(campaignId, "Goblin", CreaturePayload(armorClass = 15, hitPoints = 7), 10)
        val emboscada = EncounterContentRepository(database).createCampaign(
            campaignId, "Emboscada",
            EncounterPayload(dmGuidance = "Desde los árboles.", participants = listOf(EncounterParticipant(sourceContentId = goblin.item.identity.id, quantity = 4))),
            10,
        )
        val draft = EncounterDraft.fromEncounter(emboscada).let { it.withQuantity(it.entries.first().key, 6) }

        val copy = saveEncounterDraft(database, campaignId, draft.copy(name = "Emboscada grande"), asNew = true, nowEpochSeconds = 20)
        assertNotEquals(emboscada.item.identity.id, copy.item.identity.id)
        assertEquals(6, copy.payload.participants.single().quantity)
        assertEquals("Desde los árboles.", copy.payload.dmGuidance, "a copy keeps the guide")
        assertEquals(4, EncounterContentRepository(database).encounter(emboscada.item.identity.id)!!.payload.participants.single().quantity)

        val updated = saveEncounterDraft(database, campaignId, draft, asNew = false, nowEpochSeconds = 30)
        assertEquals(emboscada.item.identity.id, updated.item.identity.id)
        assertEquals(6, updated.payload.participants.single().quantity)
    }

    @Test
    fun aNewEncounterFromScratchKeepsItsImprovisedNumbers() = db { database, campaignId ->
        val draft = EncounterDraft(name = "Ratas del sótano").plusImprovised("Rata gigante", armorClass = 12, hitPoints = 7, quantity = 5)
        val saved = saveEncounterDraft(database, campaignId, draft, asNew = true, nowEpochSeconds = 10)
        val participant = saved.payload.participants.single()
        assertEquals(5, participant.quantity)
        val rat = CreatureContentRepository(database).creature(participant.sourceContentId!!)!!
        assertEquals("Rata gigante", rat.item.displayName)
        assertEquals(7, rat.payload.hitPoints)
        assertEquals(2, ReusableContentRepository(database).listCampaign(campaignId, ReusableContentFamily.ENCOUNTER).size +
            ReusableContentRepository(database).listCampaign(campaignId, ReusableContentFamily.CREATURE).size)
    }
}
