package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcSyncBaselineRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRetryState
import io.github.mrsimkin.dndcustomaid.shared.hosted.PcDmCorrectionPatch
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.PcAuthority
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DesktopPcManagerControllerTest {
    @Test
    fun auditShowsCanonicalAuthorityAndSyncStateAndCorrectionPreservesThem() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-pc-manager-")
        val handle = DesktopDatabaseFactory(tempDir.resolve("manager.db").toFile()).create()
        try {
            val database = handle.database
            val campaign = CampaignRepository(database).createCampaign("PC Audit Campaign")
            val spine = IntegratedSpineRepository(database)
            val dm = AccountIdentity(Uuid.random(), displayName = "Dungeon Master")
            val player = AccountIdentity(Uuid.random(), displayName = "Player One")
            spine.upsertAccount(dm)
            spine.upsertAccount(player)
            spine.upsertMembership(CampaignMembership(campaign.id, dm.id, CampaignRole.DM))
            spine.upsertMembership(CampaignMembership(campaign.id, player.id, CampaignRole.PLAYER))

            val characters = CharacterRepository(database)
            val pc = characters.createCharacter(campaign.id, "Audit Hero")
            spine.setPcAuthority(PcAuthority(pc.id, player.id, player.id))
            spine.putSyncMetadata("PC", pc.id, SyncMetadata(revision = Revision(2)))
            HostedPcSyncBaselineRepository(database).record(
                pcId = pc.id,
                revision = Revision(2),
                snapshot = CharacterBackupRepository(database).exportCharacter(pc.id, 500),
            )

            var now = 800L
            val controller = DesktopPcManagerController(database) { now++ }

            assertEquals(dm.id, controller.activeDmAccountId(campaign.id))
            assertEquals(listOf(pc.id), controller.campaignPcs(campaign.id).map { it.id })
            assertEquals(listOf(pc.id), filterDesktopPcs(controller.campaignPcs(campaign.id), "audit").map { it.id })
            assertTrue(filterDesktopPcs(controller.campaignPcs(campaign.id), "missing").isEmpty())

            val before = assertNotNull(controller.details(pc.id))
            assertEquals(player.id, before.authority?.ownerAccountId)
            assertEquals(player.id, before.authority?.controllerAccountId)
            assertEquals("Player One", before.owner?.displayName)
            assertEquals(Revision(2), before.sync.revision)
            assertEquals(Revision(2), before.sync.baselineRevision)
            assertNull(before.sync.pendingRetryState)

            val correction = controller.correctAsDm(
                characterId = pc.id,
                dmAccountId = dm.id,
                patch = PcDmCorrectionPatch.from(before.character).copy(
                    name = "Audit Hero Corrected",
                    strength = 15,
                    currentHp = before.character.currentHp + 3,
                ),
                reason = "Correct values from the signed paper sheet.",
            )
            assertEquals("Corrección DM", correction.checkpoint.label)

            val after = assertNotNull(controller.details(pc.id))
            assertEquals("Audit Hero Corrected", after.character.name)
            assertEquals(15, after.character.strength)
            assertEquals(player.id, after.authority?.ownerAccountId)
            assertEquals(player.id, after.authority?.controllerAccountId)
            assertEquals(Revision(2), after.sync.revision)
            assertEquals(Revision(2), after.sync.baselineRevision)
            assertEquals(HostedRetryState.READY, after.sync.pendingRetryState)
            assertTrue(
                after.closure.reconciliationCheckpoints.any {
                    it.id == correction.checkpoint.id && it.notes.orEmpty().contains("signed paper sheet")
                },
            )
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
