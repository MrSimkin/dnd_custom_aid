package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DesktopCreatureManagerControllerTest {
    @Test
    fun personalAuthoringAndIndependentCampaignCopyUsePersistedWave6Semantics() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-creature-manager-")
        val handle = DesktopDatabaseFactory(tempDir.resolve("manager.db").toFile()).create()
        try {
            val database = handle.database
            val campaigns = CampaignRepository(database)
            val spine = IntegratedSpineRepository(database)
            val ownerId = Uuid.random()
            val campaign = campaigns.createCampaign("The Test Campaign")
            campaigns.setActiveCampaign(campaign.id)
            spine.upsertAccount(AccountIdentity(id = ownerId, displayName = "DM"))
            spine.upsertMembership(
                CampaignMembership(
                    campaignId = campaign.id,
                    accountId = ownerId,
                    role = CampaignRole.DM,
                ),
            )

            var now = 100L
            val controller = DesktopCreatureManagerController(database) { now++ }

            assertEquals(ownerId, controller.personalOwnerAccountId())

            val personal = controller.createPersonal(ownerId, "Young Dragon")
            assertTrue(personal.item.identity.scope is ContentScope.Personal)
            assertEquals(1, controller.personalCreatures(ownerId).size)

            val accepted = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Young Blue Dragon",
                payload = CreaturePayload(
                    creatureType = "dragon",
                    armorClass = 18,
                    hitPoints = 152,
                    challengeRating = "9",
                    actions = "Multiattack. Bite and claws.",
                    tactics = "Uses mobility and lightning breath before committing.",
                ),
            )
            assertIs<RevisionDecision.Accepted>(accepted)

            val updated = assertNotNull(controller.creature(personal.item.identity.id))
            assertEquals("Young Blue Dragon", updated.item.displayName)
            assertEquals(18, updated.payload.armorClass)
            assertEquals("9", updated.payload.challengeRating)

            val stale = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Stale overwrite",
                payload = CreaturePayload(hitPoints = 1),
            )
            assertIs<RevisionDecision.Stale>(stale)
            assertEquals("Young Blue Dragon", controller.creature(personal.item.identity.id)?.item?.displayName)

            val copied = controller.copyPersonalToCampaign(personal.item.identity.id, campaign.id)
            assertNotEquals(personal.item.identity.id, copied.item.identity.id)
            assertEquals(ContentScope.Campaign(campaign.id), copied.item.identity.scope)
            assertEquals(personal.item.identity.id, copied.item.identity.provenance?.sourceObjectId)
            assertEquals("Young Blue Dragon", copied.item.displayName)
            assertEquals(18, copied.payload.armorClass)
            assertEquals(1, controller.campaignCreatures(campaign.id).size)
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
