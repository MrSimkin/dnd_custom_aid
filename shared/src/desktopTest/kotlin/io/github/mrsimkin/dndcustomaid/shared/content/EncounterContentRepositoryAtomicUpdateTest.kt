package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class EncounterContentRepositoryAtomicUpdateTest {
    @Test
    fun atomicUpdateRenamesEncounterAndPersistsPayloadUnderOneRevision() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            val database = AppDatabase(driver)
            val owner = AccountIdentity(Uuid.random())
            IntegratedSpineRepository(database).upsertAccount(owner)
            val repository = EncounterContentRepository(database)
            val original = repository.createPersonal(
                ownerAccountId = owner.id,
                rawDisplayName = "Archive Ambush",
                payload = EncounterPayload(summary = "Original summary"),
                nowEpochSeconds = 100,
            )
            val updatedPayload = EncounterPayload(
                summary = "Scavengers corner the party among flooded catalogue stacks.",
                environment = "Raised balcony, waist-deep water and a corroded sluice wheel.",
                context = "Noise in the lower archive drew scavengers from the cistern.",
                dmGuidance = "Let negotiation or retreat remain valid alternatives to combat.",
                participants = listOf(
                    EncounterParticipant(
                        label = "Dock scavengers",
                        quantity = 3,
                        readiness = EncounterParticipantReadiness.RESERVE,
                        condition = "Arrive only after sustained noise.",
                        overrides = "One carries a lantern instead of a shield.",
                        notes = "Prepared encounter state only.",
                    ),
                ),
                tags = listOf("archive", "ambush"),
                notes = "Not live combat state.",
            )

            val accepted = assertIs<RevisionDecision.Accepted>(
                repository.update(
                    id = original.item.identity.id,
                    expectedRevision = Revision(0),
                    rawDisplayName = "Flooded Archive Ambush",
                    payload = updatedPayload,
                    updatedAtEpochSeconds = 200,
                ),
            )
            assertEquals(Revision(1), accepted.nextRevision)
            assertEquals("Flooded Archive Ambush", repository.encounter(original.item.identity.id)?.item?.displayName)
            assertEquals(updatedPayload, repository.encounter(original.item.identity.id)?.payload)

            val stale = assertIs<RevisionDecision.Stale>(
                repository.update(
                    id = original.item.identity.id,
                    expectedRevision = Revision(0),
                    rawDisplayName = "Stale Rename",
                    payload = EncounterPayload(summary = "Stale payload"),
                    updatedAtEpochSeconds = 250,
                ),
            )
            assertEquals(Revision(1), stale.actual)
            assertEquals("Flooded Archive Ambush", repository.encounter(original.item.identity.id)?.item?.displayName)
            assertEquals(updatedPayload, repository.encounter(original.item.identity.id)?.payload)
        } finally {
            driver.close()
        }
    }
}
