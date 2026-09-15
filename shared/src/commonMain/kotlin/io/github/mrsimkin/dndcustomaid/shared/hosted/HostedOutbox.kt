package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_BACKUP_FORMAT
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_BACKUP_VERSION
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

private const val PC_SYNC_OBJECT_TYPE = "PC"

@Serializable
enum class HostedMutationType {
    CAMPAIGN_CREATE,
    PC_SNAPSHOT_PUT,
}

@Serializable
enum class HostedRetryState {
    READY,
    BLOCKED,
}

@Serializable
data class HostedCampaignCreatePayload(
    val mutationId: Uuid,
    val campaignId: Uuid,
    val name: String,
) {
    init {
        require(name.isNotBlank()) { "Campaign name must not be blank." }
    }
}

@Serializable
data class HostedPcSnapshotPutPayload(
    val mutationId: Uuid,
    val pcId: Uuid,
    val campaignId: Uuid,
    val expectedRevision: Long,
    val snapshot: CharacterBackupDocument,
) {
    init {
        require(expectedRevision >= 0) { "Expected PC revision must not be negative." }
        require(snapshot.format == CHARACTER_BACKUP_FORMAT) { "PC snapshot format is unsupported." }
        require(snapshot.version in 1..CHARACTER_BACKUP_VERSION) { "PC snapshot version is unsupported." }
        require(snapshot.character.id == pcId) { "PC snapshot identity must match its queued object." }
        require(snapshot.character.campaignId == campaignId) { "PC snapshot campaign must match its queued campaign." }
    }
}

data class HostedOutboxMutation(
    val mutationId: Uuid,
    val campaignId: Uuid,
    val type: HostedMutationType,
    val objectId: Uuid,
    val expectedRevision: Long?,
    val payloadJson: String,
    val retryState: HostedRetryState,
    val attemptCount: Long,
    val createdAtEpochSeconds: Long,
    val lastAttemptAtEpochSeconds: Long?,
    val lastErrorCode: String?,
    val lastErrorMessage: String?,
) {
    init {
        require(expectedRevision == null || expectedRevision >= 0) {
            "Expected revision must not be negative."
        }
        require(payloadJson.isNotBlank()) { "Hosted mutation payload must not be blank." }
        require(attemptCount >= 0) { "Attempt count must not be negative." }
        require(createdAtEpochSeconds >= 0) { "Creation timestamp must not be negative." }
        require(lastAttemptAtEpochSeconds == null || lastAttemptAtEpochSeconds >= 0) {
            "Last-attempt timestamp must not be negative."
        }
    }
}

class HostedOutboxRepository(
    private val database: AppDatabase,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    },
) {
    fun enqueueCampaignCreation(
        campaignId: Uuid,
        rawName: String,
        createdAtEpochSeconds: Long,
        mutationId: Uuid = Uuid.random(),
    ): HostedOutboxMutation {
        val name = rawName.trim()
        require(name.isNotEmpty()) { "Campaign name must not be blank." }
        require(createdAtEpochSeconds >= 0) { "Creation timestamp must not be negative." }

        val payload = HostedCampaignCreatePayload(
            mutationId = mutationId,
            campaignId = campaignId,
            name = name,
        )
        database.hostedOutboxQueries.insertMutation(
            mutation_id = mutationId.toString(),
            campaign_id = campaignId.toString(),
            mutation_type = HostedMutationType.CAMPAIGN_CREATE.name,
            object_id = campaignId.toString(),
            expected_revision = null,
            payload_json = json.encodeToString(payload),
            created_at_epoch_seconds = createdAtEpochSeconds,
        )
        return requireNotNull(mutation(mutationId))
    }

    fun enqueuePcSnapshot(
        campaignId: Uuid,
        pcId: Uuid,
        expectedRevision: Long,
        snapshot: CharacterBackupDocument,
        createdAtEpochSeconds: Long,
        mutationId: Uuid = Uuid.random(),
    ): HostedOutboxMutation {
        require(expectedRevision >= 0) { "Expected PC revision must not be negative." }
        require(createdAtEpochSeconds >= 0) { "Creation timestamp must not be negative." }
        require(
            allMutations().none {
                it.type == HostedMutationType.PC_SNAPSHOT_PUT &&
                    it.objectId == pcId &&
                    it.retryState == HostedRetryState.READY
            },
        ) { "A ready hosted PC snapshot mutation already exists for this PC." }

        val payload = HostedPcSnapshotPutPayload(
            mutationId = mutationId,
            pcId = pcId,
            campaignId = campaignId,
            expectedRevision = expectedRevision,
            snapshot = snapshot,
        )
        database.hostedOutboxQueries.insertMutation(
            mutation_id = mutationId.toString(),
            campaign_id = campaignId.toString(),
            mutation_type = HostedMutationType.PC_SNAPSHOT_PUT.name,
            object_id = pcId.toString(),
            expected_revision = expectedRevision,
            payload_json = json.encodeToString(payload),
            created_at_epoch_seconds = createdAtEpochSeconds,
        )
        return requireNotNull(mutation(mutationId))
    }

    fun mutation(mutationId: Uuid): HostedOutboxMutation? =
        database.hostedOutboxQueries.selectMutation(
            mutation_id = mutationId.toString(),
            mapper = ::mapMutation,
        ).executeAsOneOrNull()

    fun readyMutations(limit: Long = 50): List<HostedOutboxMutation> {
        require(limit > 0) { "Outbox query limit must be positive." }
        return database.hostedOutboxQueries.selectReadyMutations(
            row_limit = limit,
            mapper = ::mapMutation,
        ).executeAsList()
    }

    fun allMutations(): List<HostedOutboxMutation> =
        database.hostedOutboxQueries.selectAllMutations(mapper = ::mapMutation).executeAsList()

    fun campaignCreationPayload(mutation: HostedOutboxMutation): HostedCampaignCreatePayload {
        require(mutation.type == HostedMutationType.CAMPAIGN_CREATE) {
            "Hosted mutation is not a campaign creation."
        }
        val payload = json.decodeFromString<HostedCampaignCreatePayload>(mutation.payloadJson)
        require(payload.mutationId == mutation.mutationId) {
            "Hosted mutation payload changed mutation identity."
        }
        require(payload.campaignId == mutation.objectId && payload.campaignId == mutation.campaignId) {
            "Hosted mutation payload changed campaign identity."
        }
        return payload
    }

    fun pcSnapshotPayload(mutation: HostedOutboxMutation): HostedPcSnapshotPutPayload {
        require(mutation.type == HostedMutationType.PC_SNAPSHOT_PUT) {
            "Hosted mutation is not a PC snapshot update."
        }
        val payload = json.decodeFromString<HostedPcSnapshotPutPayload>(mutation.payloadJson)
        require(payload.mutationId == mutation.mutationId) {
            "Hosted PC mutation payload changed mutation identity."
        }
        require(payload.pcId == mutation.objectId && payload.campaignId == mutation.campaignId) {
            "Hosted PC mutation payload changed object or campaign identity."
        }
        require(payload.expectedRevision == mutation.expectedRevision) {
            "Hosted PC mutation payload changed expected revision."
        }
        return payload
    }

    fun recordFailure(
        mutationId: Uuid,
        attemptedAtEpochSeconds: Long,
        retryable: Boolean,
        errorCode: String?,
        errorMessage: String?,
    ) {
        require(attemptedAtEpochSeconds >= 0) { "Attempt timestamp must not be negative." }
        require(mutation(mutationId) != null) { "Hosted mutation must exist before recording an attempt." }

        database.hostedOutboxQueries.recordAttemptFailure(
            retry_state = if (retryable) HostedRetryState.READY.name else HostedRetryState.BLOCKED.name,
            last_attempt_at_epoch_seconds = attemptedAtEpochSeconds,
            last_error_code = errorCode?.takeIf { it.isNotBlank() },
            last_error_message = errorMessage?.takeIf { it.isNotBlank() },
            mutation_id = mutationId.toString(),
        )
    }

    fun markReady(mutationId: Uuid) {
        require(mutation(mutationId) != null) { "Hosted mutation must exist before it can be unblocked." }
        database.hostedOutboxQueries.markReady(mutationId.toString())
    }

    fun acknowledge(mutationId: Uuid) {
        database.hostedOutboxQueries.acknowledgeMutation(mutationId.toString())
    }

    fun acknowledgePcSnapshot(
        mutationId: Uuid,
        pcId: Uuid,
        resultingRevision: Long,
        deletedAtEpochSeconds: Long? = null,
    ) {
        require(resultingRevision >= 0) { "Resulting PC revision must not be negative." }
        require(deletedAtEpochSeconds == null || deletedAtEpochSeconds >= 0) {
            "Hosted PC deletion timestamp must not be negative."
        }
        val stored = requireNotNull(mutation(mutationId)) { "Hosted PC mutation must exist before acknowledgement." }
        require(stored.type == HostedMutationType.PC_SNAPSHOT_PUT && stored.objectId == pcId) {
            "Hosted PC acknowledgement identity does not match the queued mutation."
        }

        database.transaction {
            database.integratedSpineQueries.upsertObjectSyncState(
                object_type = PC_SYNC_OBJECT_TYPE,
                object_id = pcId.toString(),
                revision = resultingRevision,
                deleted_at_epoch_seconds = deletedAtEpochSeconds,
            )
            database.hostedOutboxQueries.acknowledgeMutation(mutationId.toString())
        }
    }

    private fun mapMutation(
        mutation_id: String,
        campaign_id: String,
        mutation_type: String,
        object_id: String,
        expected_revision: Long?,
        payload_json: String,
        retry_state: String,
        attempt_count: Long,
        created_at_epoch_seconds: Long,
        last_attempt_at_epoch_seconds: Long?,
        last_error_code: String?,
        last_error_message: String?,
    ): HostedOutboxMutation = HostedOutboxMutation(
        mutationId = Uuid.parse(mutation_id),
        campaignId = Uuid.parse(campaign_id),
        type = HostedMutationType.valueOf(mutation_type),
        objectId = Uuid.parse(object_id),
        expectedRevision = expected_revision,
        payloadJson = payload_json,
        retryState = HostedRetryState.valueOf(retry_state),
        attemptCount = attempt_count,
        createdAtEpochSeconds = created_at_epoch_seconds,
        lastAttemptAtEpochSeconds = last_attempt_at_epoch_seconds,
        lastErrorCode = last_error_code,
        lastErrorMessage = last_error_message,
    )
}
