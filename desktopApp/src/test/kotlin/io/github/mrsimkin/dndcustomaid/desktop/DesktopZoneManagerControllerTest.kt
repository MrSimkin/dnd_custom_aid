package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePayload
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

class DesktopZoneManagerControllerTest {
    @Test
    fun zoneBriefAuthoringFilteringCopyAndDeleteUseReusableContentSemantics() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-zone-manager-")
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

            var now = 700L
            val controller = DesktopZoneManagerController(database) { now++ }
            assertEquals(ownerId, controller.personalOwnerAccountId())

            val personal = controller.createPersonal(ownerId, "Flooded Archive")
            assertEquals(ContentScope.Personal(ownerId), personal.item.identity.scope)

            val payload = ZonePayload(
                summary = "A half-submerged archive hides a route into old cisterns.",
                area = "Lower Ward / East drainage district",
                presentation = "Cold water reflects rows of leaning shelves.",
                space = "Entry gallery, raised catalogue balcony, flooded stacks, west sluice gate.",
                exploration = "Movement through the stacks is slow; the balcony offers a dry overview.",
                interactives = listOf("Operate the west sluice wheel", "Search catalogue drawers"),
                clues = listOf("Fresh wax marks a route toward the cistern stairs"),
                checks = listOf("Athletics to force the corroded sluice"),
                consequences = listOf("Sustained loud work may attract scavengers"),
                encounterBrief = "Scavengers prefer to bargain before fighting.",
                dmGuidance = "Use arrival as advisory pressure rather than an automatic clock.",
                playerSafeText = "The old archive is flooded but still partly accessible.",
                paperReferences = listOf("Dungeon binder p. 18"),
                tags = listOf("archive", "flooded", "exploration"),
            )
            assertIs<RevisionDecision.Accepted>(
                controller.update(
                    id = personal.item.identity.id,
                    expectedRevision = personal.item.identity.revision,
                    displayName = "Flooded Municipal Archive",
                    payload = payload,
                ),
            )

            val updated = assertNotNull(controller.zone(personal.item.identity.id))
            assertEquals("Flooded Municipal Archive", updated.item.displayName)
            assertEquals(payload, updated.payload)

            val filtered = filterDesktopZones(
                zones = controller.personalZones(ownerId),
                filters = DesktopZoneFilters(
                    query = "sluice",
                    area = "lower ward",
                    tag = "flood",
                ),
            )
            assertEquals(listOf(updated.item.identity.id), filtered.map { it.item.identity.id })
            assertEquals(
                emptyList(),
                filterDesktopZones(
                    controller.personalZones(ownerId),
                    DesktopZoneFilters(tag = "forest"),
                ),
            )

            val stale = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Stale Rename",
                payload = ZonePayload(summary = "Wrong"),
            )
            assertIs<RevisionDecision.Stale>(stale)
            assertEquals(payload, controller.zone(personal.item.identity.id)?.payload)

            val copied = controller.copyPersonalToCampaign(personal.item.identity.id, campaign.id)
            assertNotEquals(personal.item.identity.id, copied.item.identity.id)
            assertEquals(ContentScope.Campaign(campaign.id), copied.item.identity.scope)
            assertEquals(personal.item.identity.id, copied.item.identity.provenance?.sourceObjectId)
            assertEquals(payload, copied.payload)
            assertEquals(1, controller.campaignZones(campaign.id).size)

            val changedPersonalPayload = payload.copy(
                exploration = "The lower stacks are now fully drained.",
                tags = payload.tags + "drained",
            )
            val changePersonal = controller.update(
                id = updated.item.identity.id,
                expectedRevision = updated.item.identity.revision,
                displayName = updated.item.displayName,
                payload = changedPersonalPayload,
            )
            assertIs<RevisionDecision.Accepted>(changePersonal)
            assertEquals(changedPersonalPayload, controller.zone(personal.item.identity.id)?.payload)
            assertEquals(payload, controller.zone(copied.item.identity.id)?.payload)

            val latestPersonal = assertNotNull(controller.zone(personal.item.identity.id))
            val repository = ZoneContentRepository(database)
            val deleted = assertIs<RevisionDecision.Accepted>(
                repository.tombstone(
                    id = latestPersonal.item.identity.id,
                    expectedRevision = latestPersonal.item.identity.revision,
                    deletedAtEpochSeconds = now++,
                ),
            )
            assertIs<RevisionDecision.Deleted>(
                controller.update(
                    id = latestPersonal.item.identity.id,
                    expectedRevision = deleted.nextRevision,
                    displayName = "Resurrected Archive",
                    payload = ZonePayload(summary = "Must not return"),
                ),
            )
            assertEquals(null, controller.zone(personal.item.identity.id))
            assertNotNull(controller.zone(copied.item.identity.id))
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
