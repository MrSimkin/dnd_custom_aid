package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleLifecycle
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRulePayload
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

class DesktopHomebrewRuleManagerControllerTest {
    @Test
    fun lifecycleAtomicEditAndIndependentCampaignCopyUsePersistedWave6Semantics() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-homebrew-manager-")
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

            var now = 300L
            val controller = DesktopHomebrewRuleManagerController(database) { now++ }

            assertEquals(ownerId, controller.personalOwnerAccountId())

            val personal = controller.createPersonal(ownerId, "Heroic Recovery")
            assertTrue(personal.item.identity.scope is ContentScope.Personal)
            assertEquals(HomebrewRuleLifecycle.DRAFT, personal.payload.lifecycle)
            assertEquals(1, controller.personalRules(ownerId).size)

            val activeRule = HomebrewRulePayload(
                summary = "A downed hero may spend Inspiration to stand after stabilizing.",
                body = "After succeeding on a death saving throw, spend Inspiration to regain 1 hit point and stand.",
                category = "Combat / Recovery",
                rationale = "Make Inspiration a meaningful emergency resource without replacing healing.",
                examples = listOf("Tamsin rolls 17 on a death save, spends Inspiration, and rises with 1 HP."),
                relatedReferences = listOf("PHB: Inspiration", "PHB: Death Saving Throws"),
                tags = listOf("combat", "inspiration", "recovery"),
                lifecycle = HomebrewRuleLifecycle.ACTIVE,
                notes = "Review after three sessions.",
            )

            val accepted = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Heroic Recovery — Campaign Rule",
                payload = activeRule,
            )
            assertIs<RevisionDecision.Accepted>(accepted)

            val updated = assertNotNull(controller.rule(personal.item.identity.id))
            assertEquals("Heroic Recovery — Campaign Rule", updated.item.displayName)
            assertEquals(HomebrewRuleLifecycle.ACTIVE, updated.payload.lifecycle)
            assertEquals("Combat / Recovery", updated.payload.category)
            assertEquals(listOf("combat", "inspiration", "recovery"), updated.payload.tags)
            assertEquals(1, updated.payload.examples.size)

            val stale = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Stale overwrite",
                payload = HomebrewRulePayload(summary = "Wrong"),
            )
            assertIs<RevisionDecision.Stale>(stale)
            assertEquals("Heroic Recovery — Campaign Rule", controller.rule(personal.item.identity.id)?.item?.displayName)

            val copied = controller.copyPersonalToCampaign(personal.item.identity.id, campaign.id)
            assertNotEquals(personal.item.identity.id, copied.item.identity.id)
            assertEquals(ContentScope.Campaign(campaign.id), copied.item.identity.scope)
            assertEquals(personal.item.identity.id, copied.item.identity.provenance?.sourceObjectId)
            assertEquals(HomebrewRuleLifecycle.ACTIVE, copied.payload.lifecycle)
            assertEquals(activeRule.body, copied.payload.body)
            assertEquals(1, controller.campaignRules(campaign.id).size)

            val retirePersonal = controller.update(
                id = updated.item.identity.id,
                expectedRevision = updated.item.identity.revision,
                displayName = updated.item.displayName,
                payload = updated.payload.copy(
                    lifecycle = HomebrewRuleLifecycle.RETIRED,
                    summary = "Retired Personal master.",
                ),
            )
            assertIs<RevisionDecision.Accepted>(retirePersonal)
            assertEquals(HomebrewRuleLifecycle.RETIRED, controller.rule(personal.item.identity.id)?.payload?.lifecycle)
            assertEquals("Retired Personal master.", controller.rule(personal.item.identity.id)?.payload?.summary)
            assertEquals(HomebrewRuleLifecycle.ACTIVE, controller.rule(copied.item.identity.id)?.payload?.lifecycle)
            assertEquals(activeRule.summary, controller.rule(copied.item.identity.id)?.payload?.summary)
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
