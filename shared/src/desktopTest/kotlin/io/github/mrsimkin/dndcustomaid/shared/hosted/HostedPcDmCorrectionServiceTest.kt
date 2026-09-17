package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.PcAuthority
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedPcDmCorrectionServiceTest {
    @Test
    fun correctionPreservesAuthorityAppendsAuditCheckpointAndQueuesCurrentSnapshot() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val campaign = campaigns.createCampaign("Correction Campaign")
            val spine = IntegratedSpineRepository(database)
            val dm = AccountIdentity(Uuid.random(), displayName = "DM")
            val player = AccountIdentity(Uuid.random(), displayName = "Player")
            spine.upsertAccount(dm)
            spine.upsertAccount(player)
            spine.upsertMembership(CampaignMembership(campaign.id, dm.id, CampaignRole.DM))
            spine.upsertMembership(CampaignMembership(campaign.id, player.id, CampaignRole.PLAYER))

            val characters = CharacterRepository(database)
            val original = characters.createCharacter(campaign.id, "Original Hero")
            spine.setPcAuthority(PcAuthority(original.id, player.id, player.id))
            spine.putSyncMetadata("PC", original.id, SyncMetadata(revision = Revision(4)))

            val patch = PcDmCorrectionPatch.from(original).copy(
                name = "Corrected Hero",
                status = CharacterStatus.INACTIVE,
                strength = 14,
                armorClass = 17,
                maxHp = 40,
                currentHp = 31,
                initiativeAdjustment = 2,
                inspiration = true,
                generalNotes = "Corrected by the DM after reviewing the paper sheet.",
            )

            val result = HostedPcDmCorrectionService(database).correct(
                characterId = original.id,
                dmAccountId = dm.id,
                patch = patch,
                rawReason = "Paper sheet and app diverged after level-up",
                correctedAtEpochSeconds = 900,
                correctionId = Uuid.parse("00000000-0000-0000-0000-000000009001"),
                mutationId = Uuid.parse("00000000-0000-0000-0000-000000009002"),
            )

            val corrected = assertNotNull(characters.character(original.id))
            assertEquals("Corrected Hero", corrected.name)
            assertEquals(CharacterStatus.INACTIVE, corrected.status)
            assertEquals(14, corrected.strength)
            assertEquals(17, corrected.armorClass)
            assertEquals(40, corrected.maxHp)
            assertEquals(31, corrected.currentHp)
            assertTrue(corrected.inspiration)

            assertEquals(PcAuthority(original.id, player.id, player.id), spine.pcAuthority(original.id))
            assertEquals(Revision(4), spine.syncMetadata("PC", original.id).revision)

            val checkpoints = CharacterClosureRepository(database).state(original.id).reconciliationCheckpoints
            val checkpoint = assertNotNull(checkpoints.singleOrNull { it.id == result.checkpoint.id })
            assertEquals("Corrección DM", checkpoint.label)
            assertTrue(checkpoint.notes.orEmpty().contains("Paper sheet and app diverged"))
            assertTrue(checkpoint.notes.orEmpty().contains("nombre:"))
            assertTrue(checkpoint.notes.orEmpty().contains("CA: 10 → 17"))
            assertTrue(checkpoint.notes.orEmpty().contains("Revisión sincronizada base: 4"))

            val outbox = HostedOutboxRepository(database)
            val queued = assertNotNull(outbox.mutation(result.mutation.mutationId))
            assertEquals(HostedMutationType.PC_SNAPSHOT_PUT, queued.type)
            assertEquals(4, queued.expectedRevision)
            val payload = outbox.pcSnapshotPayload(queued)
            assertEquals("Corrected Hero", payload.snapshot.character.name)
            assertEquals(CharacterStatus.INACTIVE, payload.snapshot.character.status)
            assertTrue(
                payload.snapshot.closureState.reconciliationCheckpoints.any { it.id == checkpoint.id },
            )
        }
    }

    @Test
    fun correctionRequiresActiveDmAndRollsBackWhenAnotherPcMutationNeedsResolution() {
        withDatabase { database ->
            val campaign = CampaignRepository(database).createCampaign("Guard Campaign")
            val spine = IntegratedSpineRepository(database)
            val dm = AccountIdentity(Uuid.random())
            val player = AccountIdentity(Uuid.random())
            spine.upsertAccount(dm)
            spine.upsertAccount(player)
            spine.upsertMembership(CampaignMembership(campaign.id, dm.id, CampaignRole.DM))
            spine.upsertMembership(CampaignMembership(campaign.id, player.id, CampaignRole.PLAYER))
            val characters = CharacterRepository(database)
            val original = characters.createCharacter(campaign.id, "Guarded Hero")
            val service = HostedPcDmCorrectionService(database)
            val patch = PcDmCorrectionPatch.from(original).copy(currentHp = original.currentHp + 1)

            assertFailsWith<IllegalArgumentException> {
                service.correct(
                    characterId = original.id,
                    dmAccountId = player.id,
                    patch = patch,
                    rawReason = "Player must not use DM correction",
                    correctedAtEpochSeconds = 1000,
                )
            }
            assertEquals(original, characters.character(original.id))

            HostedPcSnapshotQueueService(database).queueCurrentSnapshot(
                characterId = original.id,
                createdAtEpochSeconds = 1001,
            )
            assertFailsWith<IllegalArgumentException> {
                service.correct(
                    characterId = original.id,
                    dmAccountId = dm.id,
                    patch = patch,
                    rawReason = "Must resolve pending sync first",
                    correctedAtEpochSeconds = 1002,
                )
            }

            assertEquals(original, characters.character(original.id))
            assertFalse(
                CharacterClosureRepository(database).state(original.id).reconciliationCheckpoints
                    .any { it.label == "Corrección DM" },
            )
        }
    }

    private fun withDatabase(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
