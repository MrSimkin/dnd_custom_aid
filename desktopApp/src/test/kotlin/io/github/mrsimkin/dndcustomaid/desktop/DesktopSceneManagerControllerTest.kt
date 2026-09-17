package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ScenePayload
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
import kotlin.uuid.Uuid

class DesktopSceneManagerControllerTest {
    @Test
    fun lightweightSceneAuthoringEditCopyAndDeleteUseReusableContentSemantics() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-scene-manager-")
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

            var now = 500L
            val controller = DesktopSceneManagerController(database) { now++ }

            assertEquals(ownerId, controller.personalOwnerAccountId())

            val personal = controller.createPersonal(ownerId, "The Sealed Observatory")
            assertEquals(ContentScope.Personal(ownerId), personal.item.identity.scope)
            assertEquals(1, controller.personalScenes(ownerId).size)

            val payload = ScenePayload(
                purpose = "Discover why the observatory was sealed and decide which lead to follow.",
                possibleNextScenes = listOf(
                    "Follow the star-map trail into the old market",
                    "Question the night watch captain",
                ),
                references = listOf("Place: Sealed Observatory", "NPC: Night Watch Captain"),
                notes = "Preparation orientation only; no automatic quest transitions.",
            )
            val accepted = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "The Sealed Observatory — First Visit",
                payload = payload,
            )
            assertIs<RevisionDecision.Accepted>(accepted)

            val updated = assertNotNull(controller.scene(personal.item.identity.id))
            assertEquals("The Sealed Observatory — First Visit", updated.item.displayName)
            assertEquals(payload, updated.payload)

            val stale = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Stale overwrite",
                payload = ScenePayload(purpose = "Wrong"),
            )
            assertIs<RevisionDecision.Stale>(stale)
            assertEquals(payload, controller.scene(personal.item.identity.id)?.payload)

            val copied = controller.copyPersonalToCampaign(personal.item.identity.id, campaign.id)
            assertNotEquals(personal.item.identity.id, copied.item.identity.id)
            assertEquals(ContentScope.Campaign(campaign.id), copied.item.identity.scope)
            assertEquals(personal.item.identity.id, copied.item.identity.provenance?.sourceObjectId)
            assertEquals(payload, copied.payload)
            assertEquals(1, controller.campaignScenes(campaign.id).size)

            val changedPersonalPayload = payload.copy(
                purpose = "The observatory has changed since the campaign copy was made.",
                possibleNextScenes = listOf("Investigate the reopened lower archive"),
            )
            val changePersonal = controller.update(
                id = updated.item.identity.id,
                expectedRevision = updated.item.identity.revision,
                displayName = updated.item.displayName,
                payload = changedPersonalPayload,
            )
            assertIs<RevisionDecision.Accepted>(changePersonal)
            assertEquals(changedPersonalPayload, controller.scene(personal.item.identity.id)?.payload)
            assertEquals(payload, controller.scene(copied.item.identity.id)?.payload)

            val latestPersonal = assertNotNull(controller.scene(personal.item.identity.id))
            val repository = SceneContentRepository(database)
            val deleted = assertIs<RevisionDecision.Accepted>(
                repository.tombstone(
                    id = latestPersonal.item.identity.id,
                    expectedRevision = latestPersonal.item.identity.revision,
                    deletedAtEpochSeconds = now++,
                ),
            )

            val deletedWrite = controller.update(
                id = latestPersonal.item.identity.id,
                expectedRevision = deleted.nextRevision,
                displayName = "Resurrected",
                payload = ScenePayload(purpose = "Must not come back"),
            )
            assertIs<RevisionDecision.Deleted>(deletedWrite)
            assertEquals(null, controller.scene(personal.item.identity.id))
            assertNotNull(controller.scene(copied.item.identity.id))
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
