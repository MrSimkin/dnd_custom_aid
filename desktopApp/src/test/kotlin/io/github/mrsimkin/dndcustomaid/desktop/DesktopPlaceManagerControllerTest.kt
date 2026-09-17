package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
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

class DesktopPlaceManagerControllerTest {
    @Test
    fun shopAuthoringAtomicEditAndIndependentCampaignCopyUsePersistedWave6Semantics() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-place-manager-")
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

            var now = 400L
            val controller = DesktopPlaceManagerController(database) { now++ }

            assertEquals(ownerId, controller.personalOwnerAccountId())

            val personal = controller.createPersonal(ownerId, "The Brass Lantern", PlaceKind.SHOP)
            assertTrue(personal.item.identity.scope is ContentScope.Personal)
            assertEquals(PlaceKind.SHOP, personal.payload.kind)
            assertEquals(1, controller.personalPlaces(ownerId).size)

            val shopPayload = PlacePayload(
                kind = PlaceKind.SHOP,
                summary = "A cramped adventuring-supplies shop under a brass lantern.",
                area = "Old Market",
                function = "Supplies, rumors, and expedition provisioning.",
                presentation = "Warm lamplight, cedar shelves, and too many hanging packs.",
                services = listOf("General adventuring gear", "Map copying", "Special orders"),
                interactives = listOf("Ask about the locked display case", "Haggle over damaged gear"),
                hooks = listOf("A returned expedition left an unclaimed sealed map."),
                playerSafeText = "A brass lantern marks a narrow shop packed with expedition gear.",
                dmNotes = "The proprietor knows which caravan vanished last week.",
                paperReferences = listOf("DM notebook p. 42"),
                tags = listOf("shop", "market", "supplies"),
            )

            val accepted = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "The Brass Lantern — Outfitters",
                payload = shopPayload,
            )
            assertIs<RevisionDecision.Accepted>(accepted)

            val updated = assertNotNull(controller.place(personal.item.identity.id))
            assertEquals("The Brass Lantern — Outfitters", updated.item.displayName)
            assertEquals(PlaceKind.SHOP, updated.payload.kind)
            assertEquals("Old Market", updated.payload.area)
            assertEquals(listOf("General adventuring gear", "Map copying", "Special orders"), updated.payload.services)
            assertEquals(listOf("shop", "market", "supplies"), updated.payload.tags)

            val stale = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Stale overwrite",
                payload = PlacePayload(summary = "Wrong"),
            )
            assertIs<RevisionDecision.Stale>(stale)
            assertEquals("The Brass Lantern — Outfitters", controller.place(personal.item.identity.id)?.item?.displayName)

            val copied = controller.copyPersonalToCampaign(personal.item.identity.id, campaign.id)
            assertNotEquals(personal.item.identity.id, copied.item.identity.id)
            assertEquals(ContentScope.Campaign(campaign.id), copied.item.identity.scope)
            assertEquals(personal.item.identity.id, copied.item.identity.provenance?.sourceObjectId)
            assertEquals(PlaceKind.SHOP, copied.payload.kind)
            assertEquals(shopPayload.presentation, copied.payload.presentation)
            assertEquals(1, controller.campaignPlaces(campaign.id).size)

            val changePersonal = controller.update(
                id = updated.item.identity.id,
                expectedRevision = updated.item.identity.revision,
                displayName = "The Brass Lantern — Closed Storefront",
                payload = updated.payload.copy(
                    kind = PlaceKind.PLACE,
                    summary = "The former shop is now an empty storefront.",
                    services = emptyList(),
                ),
            )
            assertIs<RevisionDecision.Accepted>(changePersonal)
            assertEquals(PlaceKind.PLACE, controller.place(personal.item.identity.id)?.payload?.kind)
            assertEquals(emptyList(), controller.place(personal.item.identity.id)?.payload?.services)
            assertEquals(PlaceKind.SHOP, controller.place(copied.item.identity.id)?.payload?.kind)
            assertEquals(shopPayload.services, controller.place(copied.item.identity.id)?.payload?.services)

            val latestPersonal = assertNotNull(controller.place(personal.item.identity.id))
            val repository = PlaceContentRepository(database)
            val deleted = repository.tombstone(
                id = latestPersonal.item.identity.id,
                expectedRevision = latestPersonal.item.identity.revision,
                deletedAtEpochSeconds = now++,
            )
            assertIs<RevisionDecision.Accepted>(deleted)

            val deletedWrite = controller.update(
                id = latestPersonal.item.identity.id,
                expectedRevision = latestPersonal.item.identity.revision,
                displayName = "Resurrected",
                payload = PlacePayload(summary = "Must not come back"),
            )
            assertIs<RevisionDecision.Deleted>(deletedWrite)
            assertEquals(null, controller.place(personal.item.identity.id))
            assertNotNull(controller.place(copied.item.identity.id))
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
