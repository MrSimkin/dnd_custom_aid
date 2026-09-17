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

class ZoneContentRepositoryAtomicUpdateTest {
    @Test
    fun atomicUpdateRenamesZoneAndPersistsPayloadUnderOneRevision() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            val database = AppDatabase(driver)
            val owner = AccountIdentity(Uuid.random())
            IntegratedSpineRepository(database).upsertAccount(owner)
            val repository = ZoneContentRepository(database)
            val original = repository.createPersonal(
                ownerAccountId = owner.id,
                rawDisplayName = "Archive",
                payload = ZonePayload(summary = "Original summary"),
                nowEpochSeconds = 100,
            )
            val updatedPayload = ZonePayload(
                summary = "A flooded archive conceals a route into the old cisterns.",
                area = "Lower Ward",
                presentation = "Cold water reflects leaning shelves.",
                space = "Entry gallery, raised balcony, flooded stacks and west sluice.",
                exploration = "The balcony provides a dry overview of the available routes.",
                interactives = listOf("Operate the west sluice wheel"),
                clues = listOf("Fresh wax marks the cistern stair"),
                checks = listOf("Investigation to reconstruct the damaged index"),
                consequences = listOf("Loud work can attract nearby scavengers"),
                encounterBrief = "Scavengers prefer to bargain before fighting.",
                dmGuidance = "Keep pressure advisory rather than automatic.",
                playerSafeText = "The municipal archive is flooded but partly accessible.",
                paperReferences = listOf("Dungeon binder p. 18"),
                tags = listOf("archive", "flooded"),
            )

            val accepted = assertIs<RevisionDecision.Accepted>(
                repository.update(
                    id = original.item.identity.id,
                    expectedRevision = Revision(0),
                    rawDisplayName = "Flooded Archive",
                    payload = updatedPayload,
                    updatedAtEpochSeconds = 200,
                ),
            )
            assertEquals(Revision(1), accepted.nextRevision)
            assertEquals("Flooded Archive", repository.zone(original.item.identity.id)?.item?.displayName)
            assertEquals(updatedPayload, repository.zone(original.item.identity.id)?.payload)

            val stale = assertIs<RevisionDecision.Stale>(
                repository.update(
                    id = original.item.identity.id,
                    expectedRevision = Revision(0),
                    rawDisplayName = "Stale Rename",
                    payload = ZonePayload(summary = "Stale payload"),
                    updatedAtEpochSeconds = 250,
                ),
            )
            assertEquals(Revision(1), stale.actual)
            assertEquals("Flooded Archive", repository.zone(original.item.identity.id)?.item?.displayName)
            assertEquals(updatedPayload, repository.zone(original.item.identity.id)?.payload)
        } finally {
            driver.close()
        }
    }
}
