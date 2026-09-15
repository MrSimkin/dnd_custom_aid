package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedCampaignSyncTest {
    @Test
    fun localCreationPersistsCampaignAndOutboxAtomically() = withDatabase { database ->
        val campaigns = CampaignRepository(database)
        val outbox = HostedOutboxRepository(database)
        val service = HostedCampaignCreationService(database, campaigns, outbox)
        val mutationId = Uuid.random()

        val queued = service.createCampaign(
            rawName = "  Terramore  ",
            createdAtEpochSeconds = 100,
            mutationId = mutationId,
        )

        assertEquals("Terramore", queued.campaign.name)
        assertEquals(listOf(queued.campaign), campaigns.listCampaigns())
        assertEquals(mutationId, queued.mutation.mutationId)
        assertEquals(queued.campaign.id, queued.mutation.campaignId)
        assertEquals(
            HostedCampaignCreatePayload(mutationId, queued.campaign.id, "Terramore"),
            outbox.campaignCreationPayload(queued.mutation),
        )
    }

    @Test
    fun outboxFailureRollsBackTheLocalCampaignInsert() = withDatabase { database ->
        val campaigns = CampaignRepository(database)
        val outbox = HostedOutboxRepository(database)
        val service = HostedCampaignCreationService(database, campaigns, outbox)
        val mutationId = Uuid.random()
        val existing = service.createCampaign("Existing", 100, mutationId)

        assertFails {
            service.createCampaign("Must Roll Back", 101, mutationId)
        }

        assertEquals(listOf(existing.campaign), campaigns.listCampaigns())
        assertEquals(listOf(mutationId), outbox.allMutations().map { it.mutationId })
    }

    @Test
    fun confirmedIdempotentReplayAcknowledgesQueueWithoutRemovingLocalCampaign() = withDatabase { database ->
        val campaigns = CampaignRepository(database)
        val outbox = HostedOutboxRepository(database)
        val queued = HostedCampaignCreationService(database, campaigns, outbox)
            .createCampaign("Terramore", 100)
        var deliveredMutationId: Uuid? = null

        val delivery = HostedOutboxDeliveryService(outbox) { mutationId, campaignId, name ->
            deliveredMutationId = mutationId
            HostedCampaignCreation(
                campaign = hostedCampaign(campaignId, name),
                created = false,
            )
        }

        val report = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 120) }

        assertEquals(HostedOutboxDeliveryReport(1, 1, 0, 0), report)
        assertEquals(queued.mutation.mutationId, deliveredMutationId)
        assertNull(outbox.mutation(queued.mutation.mutationId))
        assertEquals(listOf(queued.campaign), campaigns.listCampaigns())
    }

    @Test
    fun transientFailureRetriesWithTheSameMutationIdentity() = withDatabase { database ->
        val outbox = HostedOutboxRepository(database)
        val queued = HostedCampaignCreationService(database, outbox = outbox)
            .createCampaign("Terramore", 100)
        val deliveredIds = mutableListOf<Uuid>()
        var shouldFail = true

        val delivery = HostedOutboxDeliveryService(outbox) { mutationId, campaignId, name ->
            deliveredIds += mutationId
            if (shouldFail) {
                shouldFail = false
                throw HostedApiException(
                    statusCode = 503,
                    code = HostedApiErrorCode.TRANSIENT_FAILURE,
                    message = "Try later.",
                )
            }
            HostedCampaignCreation(hostedCampaign(campaignId, name), created = true)
        }

        val firstReport = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 120) }
        val afterFailure = requireNotNull(outbox.mutation(queued.mutation.mutationId))

        assertEquals(HostedOutboxDeliveryReport(1, 0, 1, 0), firstReport)
        assertEquals(HostedRetryState.READY, afterFailure.retryState)
        assertEquals(1L, afterFailure.attemptCount)
        assertEquals(120L, afterFailure.lastAttemptAtEpochSeconds)
        assertEquals(HostedApiErrorCode.TRANSIENT_FAILURE.name, afterFailure.lastErrorCode)

        val secondReport = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 130) }

        assertEquals(HostedOutboxDeliveryReport(1, 1, 0, 0), secondReport)
        assertEquals(listOf(queued.mutation.mutationId, queued.mutation.mutationId), deliveredIds)
        assertNull(outbox.mutation(queued.mutation.mutationId))
    }

    @Test
    fun unavailableAuthenticationPreservesMutationForLaterRetry() = withDatabase { database ->
        val outbox = HostedOutboxRepository(database)
        val queued = HostedCampaignCreationService(database, outbox = outbox)
            .createCampaign("Terramore", 100)
        val delivery = HostedOutboxDeliveryService(outbox) { _, _, _ ->
            throw HostedAuthenticationUnavailableException()
        }

        val report = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 120) }
        val stored = requireNotNull(outbox.mutation(queued.mutation.mutationId))

        assertEquals(HostedOutboxDeliveryReport(1, 0, 1, 0), report)
        assertEquals(HostedRetryState.READY, stored.retryState)
        assertEquals(1L, stored.attemptCount)
        assertEquals("AUTHENTICATION_UNAVAILABLE", stored.lastErrorCode)
    }

    @Test
    fun permanentApiConflictBlocksAutomaticRetry() = withDatabase { database ->
        val outbox = HostedOutboxRepository(database)
        val queued = HostedCampaignCreationService(database, outbox = outbox)
            .createCampaign("Terramore", 100)
        val delivery = HostedOutboxDeliveryService(outbox) { _, _, _ ->
            throw HostedApiException(
                statusCode = 409,
                code = HostedApiErrorCode.CONFLICT_MUTATION_REUSE,
                message = "Mutation identity reused.",
            )
        }

        val report = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 120) }
        val stored = requireNotNull(outbox.mutation(queued.mutation.mutationId))

        assertEquals(HostedOutboxDeliveryReport(1, 0, 0, 1), report)
        assertEquals(HostedRetryState.BLOCKED, stored.retryState)
        assertEquals(1L, stored.attemptCount)
        assertEquals(HostedApiErrorCode.CONFLICT_MUTATION_REUSE.name, stored.lastErrorCode)
        assertTrue(outbox.readyMutations().isEmpty())
    }

    @Test
    fun mismatchedHostedIdentityIsBlockedInsteadOfAcknowledged() = withDatabase { database ->
        val outbox = HostedOutboxRepository(database)
        val queued = HostedCampaignCreationService(database, outbox = outbox)
            .createCampaign("Terramore", 100)
        val delivery = HostedOutboxDeliveryService(outbox) { _, _, name ->
            HostedCampaignCreation(hostedCampaign(Uuid.random(), name), created = true)
        }

        val report = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 120) }
        val stored = requireNotNull(outbox.mutation(queued.mutation.mutationId))

        assertEquals(HostedOutboxDeliveryReport(1, 0, 0, 1), report)
        assertEquals(HostedRetryState.BLOCKED, stored.retryState)
        assertEquals("HOSTED_RESPONSE_IDENTITY_MISMATCH", stored.lastErrorCode)
    }

    private fun hostedCampaign(id: Uuid, name: String): HostedCampaign = HostedCampaign(
        id = id,
        name = name,
        role = CampaignRole.DM,
        revision = 0,
    )

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
