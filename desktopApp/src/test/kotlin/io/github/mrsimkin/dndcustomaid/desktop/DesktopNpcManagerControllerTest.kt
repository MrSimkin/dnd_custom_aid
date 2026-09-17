package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
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
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DesktopNpcManagerControllerTest {
    @Test
    fun quickDevelopedCombatAndIndependentCampaignCopyUsePersistedWave6Semantics() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-npc-manager-")
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

            var now = 200L
            val controller = DesktopNpcManagerController(database) { now++ }

            assertEquals(ownerId, controller.personalOwnerAccountId())

            val personal = controller.createPersonal(ownerId, "Mara")
            assertTrue(personal.item.identity.scope is ContentScope.Personal)
            assertEquals(1, controller.personalNpcs(ownerId).size)
            assertNull(personal.payload.combatMechanics)

            val developedWithCombat = NpcPayload(
                conceptRole = "Harbor fixer",
                appearanceFirstImpression = "Weathered coat and bright silver rings.",
                personalityManner = "Talks quickly and never sits with her back to a door.",
                wantsFearsNeeds = "Wants safe passage for her younger brother.",
                relationships = "Knows the dockmaster and distrusts the customs captain.",
                secrets = "She has a duplicate customs seal.",
                dmGuidance = "Useful source of rumors; becomes defensive if the seal is mentioned.",
                combatMechanics = CreaturePayload(
                    armorClass = 15,
                    hitPoints = 27,
                    speed = "30 ft.",
                    actions = "Dagger. +4 to hit, 1d4+2 piercing.",
                    tactics = "Avoids a fair fight and tries to escape.",
                ),
                notes = "First met near the east warehouse.",
            )

            val accepted = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Mara Venn",
                payload = developedWithCombat,
            )
            assertIs<RevisionDecision.Accepted>(accepted)

            val updated = assertNotNull(controller.npc(personal.item.identity.id))
            assertEquals("Mara Venn", updated.item.displayName)
            assertEquals("Harbor fixer", updated.payload.conceptRole)
            assertEquals("She has a duplicate customs seal.", updated.payload.secrets)
            assertEquals(15, updated.payload.combatMechanics?.armorClass)
            assertEquals(27, updated.payload.combatMechanics?.hitPoints)

            val stale = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Stale overwrite",
                payload = NpcPayload(conceptRole = "Wrong"),
            )
            assertIs<RevisionDecision.Stale>(stale)
            assertEquals("Mara Venn", controller.npc(personal.item.identity.id)?.item?.displayName)

            val copied = controller.copyPersonalToCampaign(personal.item.identity.id, campaign.id)
            assertNotEquals(personal.item.identity.id, copied.item.identity.id)
            assertEquals(ContentScope.Campaign(campaign.id), copied.item.identity.scope)
            assertEquals(personal.item.identity.id, copied.item.identity.provenance?.sourceObjectId)
            assertEquals(15, copied.payload.combatMechanics?.armorClass)
            assertEquals(1, controller.campaignNpcs(campaign.id).size)

            val removeCombat = controller.update(
                id = updated.item.identity.id,
                expectedRevision = updated.item.identity.revision,
                displayName = updated.item.displayName,
                payload = updated.payload.copy(combatMechanics = null),
            )
            assertIs<RevisionDecision.Accepted>(removeCombat)
            assertNull(controller.npc(personal.item.identity.id)?.payload?.combatMechanics)
            assertEquals(15, controller.npc(copied.item.identity.id)?.payload?.combatMechanics?.armorClass)
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
