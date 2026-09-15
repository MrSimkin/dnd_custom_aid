package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedOutboxRepositoryTest {
    @Test
    fun campaignCreationKeepsStableMutationAndCampaignIdentity() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = HostedOutboxRepository(database)
        val mutationId = Uuid.random()

        val mutation = repository.enqueueCampaignCreation(
            campaignId = campaign.id,
            rawName = "  Terramore  ",
            createdAtEpochSeconds = 100,
            mutationId = mutationId,
        )
        val payload = repository.campaignCreationPayload(mutation)

        assertEquals(mutationId, mutation.mutationId)
        assertEquals(campaign.id, mutation.campaignId)
        assertEquals(campaign.id, mutation.objectId)
        assertEquals(HostedMutationType.CAMPAIGN_CREATE, mutation.type)
        assertEquals(HostedRetryState.READY, mutation.retryState)
        assertEquals(0, mutation.attemptCount)
        assertEquals(mutationId, payload.mutationId)
        assertEquals(campaign.id, payload.campaignId)
        assertEquals("Terramore", payload.name)
    }

    @Test
    fun retryableFailureKeepsSameMutationReadyForRetry() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = HostedOutboxRepository(database)
        val mutation = repository.enqueueCampaignCreation(
            campaignId = campaign.id,
            rawName = campaign.name,
            createdAtEpochSeconds = 100,
        )

        repository.recordFailure(
            mutationId = mutation.mutationId,
            attemptedAtEpochSeconds = 120,
            retryable = true,
            errorCode = "TRANSIENT_FAILURE",
            errorMessage = "Try later.",
        )

        val stored = requireNotNull(repository.mutation(mutation.mutationId))
        assertEquals(mutation.mutationId, stored.mutationId)
        assertEquals(HostedRetryState.READY, stored.retryState)
        assertEquals(1, stored.attemptCount)
        assertEquals(120, stored.lastAttemptAtEpochSeconds)
        assertEquals("TRANSIENT_FAILURE", stored.lastErrorCode)
        assertEquals(listOf(mutation.mutationId), repository.readyMutations().map { it.mutationId })
    }

    @Test
    fun nonRetryableFailureBlocksAutomaticRetryUntilExplicitlyReleased() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = HostedOutboxRepository(database)
        val mutation = repository.enqueueCampaignCreation(
            campaignId = campaign.id,
            rawName = campaign.name,
            createdAtEpochSeconds = 100,
        )

        repository.recordFailure(
            mutationId = mutation.mutationId,
            attemptedAtEpochSeconds = 130,
            retryable = false,
            errorCode = "CONFLICT_MUTATION_REUSE",
            errorMessage = "Mutation identity reused.",
        )

        assertTrue(repository.readyMutations().isEmpty())
        assertEquals(HostedRetryState.BLOCKED, repository.mutation(mutation.mutationId)?.retryState)

        repository.markReady(mutation.mutationId)

        val ready = requireNotNull(repository.mutation(mutation.mutationId))
        assertEquals(HostedRetryState.READY, ready.retryState)
        assertEquals(1, ready.attemptCount)
        assertNull(ready.lastErrorCode)
        assertNull(ready.lastErrorMessage)
    }

    @Test
    fun acknowledgementRemovesOnlyTheAcknowledgedMutation() = withDatabase { database ->
        val campaigns = CampaignRepository(database)
        val first = campaigns.createCampaign("First")
        val second = campaigns.createCampaign("Second")
        val repository = HostedOutboxRepository(database)
        val firstMutation = repository.enqueueCampaignCreation(first.id, first.name, 100)
        val secondMutation = repository.enqueueCampaignCreation(second.id, second.name, 101)

        repository.acknowledge(firstMutation.mutationId)

        assertNull(repository.mutation(firstMutation.mutationId))
        assertEquals(secondMutation.mutationId, repository.readyMutations().single().mutationId)
    }

    @Test
    fun readyQueueIsStableOrderedAndBounded() = withDatabase { database ->
        val campaigns = CampaignRepository(database)
        val repository = HostedOutboxRepository(database)
        val later = campaigns.createCampaign("Later")
        val first = campaigns.createCampaign("First")
        val second = campaigns.createCampaign("Second")

        repository.enqueueCampaignCreation(later.id, later.name, 200)
        val firstMutation = repository.enqueueCampaignCreation(first.id, first.name, 100)
        val secondMutation = repository.enqueueCampaignCreation(second.id, second.name, 101)

        assertEquals(
            listOf(firstMutation.mutationId, secondMutation.mutationId),
            repository.readyMutations(limit = 2).map { it.mutationId },
        )
    }

    @Test
    fun mutationIdentityCannotBeSilentlyReusedForDifferentEnvelope() = withDatabase { database ->
        val campaigns = CampaignRepository(database)
        val first = campaigns.createCampaign("First")
        val second = campaigns.createCampaign("Second")
        val repository = HostedOutboxRepository(database)
        val mutationId = Uuid.random()

        repository.enqueueCampaignCreation(first.id, first.name, 100, mutationId)

        assertFails {
            repository.enqueueCampaignCreation(second.id, second.name, 101, mutationId)
        }
        assertEquals(first.id, repository.mutation(mutationId)?.campaignId)
    }

    @Test
    fun migration16AddsOutboxWithoutTouchingExistingCampaignData() {
        val file = File.createTempFile("dnd-custom-aid-outbox-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000201"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'Legacy')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 16, newVersion = 17)
            driver.close()

            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT name FROM campaign WHERE id = '$campaignId'").use { result ->
                        assertTrue(result.next())
                        assertEquals("Legacy", result.getString(1))
                    }
                    statement.executeQuery(
                        "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'hosted_outbox'",
                    ).use { result ->
                        assertTrue(result.next())
                        assertEquals("hosted_outbox", result.getString(1))
                    }
                }
            }
        } finally {
            file.delete()
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
