package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedMutationType
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxDeliveryService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcConflictResolutionQueueService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcPullConflictReason
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcSyncBaselineRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRetryState
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import kotlinx.coroutines.CancellationException

private const val CONFLICT_PC_SYNC_OBJECT_TYPE = "PC"

internal sealed interface AndroidHostedKeepLocalResolutionOutcome {
    data class Success(
        val pcName: String?,
        val reviewedHostedRevision: Long,
        val resultingRevision: Long,
    ) : AndroidHostedKeepLocalResolutionOutcome

    data class Refused(
        val message: String,
    ) : AndroidHostedKeepLocalResolutionOutcome

    data class DeliveryPending(
        val message: String,
    ) : AndroidHostedKeepLocalResolutionOutcome

    data class Failure(
        val message: String,
    ) : AndroidHostedKeepLocalResolutionOutcome
}

/**
 * Explicit user-controlled resolution for LOCAL_AND_HOSTED_CHANGED.
 *
 * This never runs as part of ordinary synchronization. The user must first review a concrete QA
 * conflict observation and choose to keep the local PC. The action then re-fetches hosted state and
 * requires the hosted revision to still equal the reviewed revision before it queues a local PUT.
 * The queued PUT uses that reviewed hosted revision as its expectedRevision, preserving server-side
 * compare-and-swap protection if another client advances again between the re-fetch and delivery.
 */
internal class AndroidHostedPcConflictResolver(
    database: AppDatabase,
    private val apiClient: HostedApiClient = HostedApiClient(
        baseUrl = HostedDevelopmentEnvironment.API_BASE_URL,
        accessTokens = DescopeHostedAccessTokenProvider(),
    ),
) {
    private val outbox = HostedOutboxRepository(database)
    private val backups = CharacterBackupRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val baselines = HostedPcSyncBaselineRepository(database)
    private val queue = HostedPcConflictResolutionQueueService(
        database = database,
        backups = backups,
        spine = spine,
        outbox = outbox,
    )
    private val delivery = HostedOutboxDeliveryService(
        outbox = outbox,
        api = apiClient,
    )

    suspend fun keepLocal(candidate: AndroidHostedPcConflictDiagnostic): AndroidHostedKeepLocalResolutionOutcome {
        if (candidate.phase != AndroidHostedPcPullPhase.FINAL_PULL) {
            return AndroidHostedKeepLocalResolutionOutcome.Refused(
                "La resolución requiere el conflicto de la lectura final más reciente.",
            )
        }
        if (candidate.reason != HostedPcPullConflictReason.LOCAL_AND_HOSTED_CHANGED) {
            return AndroidHostedKeepLocalResolutionOutcome.Refused(
                "Este conflicto no admite la acción «conservar local» automática.",
            )
        }

        return try {
            val pendingMutations = outbox.allMutations()
            val pendingForPc = pendingMutations.firstOrNull { mutation ->
                mutation.type == HostedMutationType.PC_SNAPSHOT_PUT && mutation.objectId == candidate.pcId
            }
            if (pendingForPc != null) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "Ya existe una mutación hospedada para este PC (${pendingForPc.retryState}). No se creó otra.",
                )
            }
            if (pendingMutations.any { it.retryState == HostedRetryState.READY }) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "Hay otras mutaciones READY pendientes. Ejecuta primero una sincronización QA normal para que esta resolución quede aislada.",
                )
            }

            val metadata = spine.syncMetadata(CONFLICT_PC_SYNC_OBJECT_TYPE, candidate.pcId)
            if (metadata.isDeleted) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "El PC local está marcado como eliminado; no se puede subir como estado activo.",
                )
            }
            if (metadata.revision.value != candidate.localRevision) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "La revisión local cambió desde el diagnóstico. Ejecuta una nueva sincronización QA antes de resolver.",
                )
            }

            val baseline = baselines.baseline(candidate.pcId)
                ?: return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "Ya no existe la línea base sincronizada. Ejecuta una nueva sincronización QA antes de resolver.",
                )
            if (baseline.revision.value != candidate.localRevision) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "La línea base ya no corresponde a la revisión local diagnosticada.",
                )
            }

            val localDocument = backups.exportCharacter(
                characterId = candidate.pcId,
                exportedAtEpochSeconds = currentConflictEpochSeconds(),
            )
            if (localDocument.character.campaignId != candidate.campaignId) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "El PC local ya no pertenece a la campaña diagnosticada.",
                )
            }
            if (normalizedConflictSnapshot(localDocument) == baseline.snapshot) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "El PC local ya no contiene el cambio que originó el conflicto.",
                )
            }

            val currentRemote = apiClient.campaignPcs(candidate.campaignId)
                .singleOrNull { it.id == candidate.pcId }
                ?: return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "El PC ya no está disponible en el servidor. Ejecuta una nueva sincronización QA.",
                )
            if (currentRemote.deletedAtEpochSeconds != null) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "El PC fue eliminado en el servidor. No se sobrescribió esa eliminación.",
                )
            }
            if (currentRemote.revision != candidate.hostedRevision) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "El servidor cambió otra vez: revisión diagnosticada ${candidate.hostedRevision}, revisión actual ${currentRemote.revision}. Ejecuta una nueva sincronización QA y revisa el conflicto actualizado.",
                )
            }
            if (normalizedConflictSnapshot(localDocument) == normalizedConflictSnapshot(currentRemote.snapshot)) {
                return AndroidHostedKeepLocalResolutionOutcome.Refused(
                    "El PC local ya coincide con el servidor; no es necesario sobrescribirlo.",
                )
            }

            val now = currentConflictEpochSeconds()
            val queued = queue.queueLocalSnapshotAgainstHostedRevision(
                characterId = candidate.pcId,
                expectedHostedRevision = Revision(candidate.hostedRevision),
                createdAtEpochSeconds = now,
                exportedAtEpochSeconds = now,
            )
            delivery.deliverReady(attemptedAtEpochSeconds = currentConflictEpochSeconds())

            val remaining = outbox.mutation(queued.mutation.mutationId)
            if (remaining != null) {
                AndroidHostedKeepLocalResolutionOutcome.DeliveryPending(
                    "La copia local quedó preservada en el outbox (${remaining.retryState}); código=${remaining.lastErrorCode ?: "sin código"}. No se confirmó una sobrescritura del servidor.",
                )
            } else {
                AndroidHostedKeepLocalResolutionOutcome.Success(
                    pcName = localDocument.character.name,
                    reviewedHostedRevision = candidate.hostedRevision,
                    resultingRevision = spine.syncMetadata(CONFLICT_PC_SYNC_OBJECT_TYPE, candidate.pcId).revision.value,
                )
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            AndroidHostedKeepLocalResolutionOutcome.Failure(
                "No se pudo completar la resolución. La copia local se conservó; ejecuta una nueva sincronización QA para revisar el estado.",
            )
        }
    }

    fun close() {
        apiClient.close()
    }
}

internal fun renderKeepLocalResolutionOutcome(outcome: AndroidHostedKeepLocalResolutionOutcome): String = when (outcome) {
    is AndroidHostedKeepLocalResolutionOutcome.Success ->
        "SUCCESS | PC=${outcome.pcName ?: "UNKNOWN"} | reviewedHostedRevision=${outcome.reviewedHostedRevision} | resultingRevision=${outcome.resultingRevision}"
    is AndroidHostedKeepLocalResolutionOutcome.Refused -> "REFUSED | ${outcome.message}"
    is AndroidHostedKeepLocalResolutionOutcome.DeliveryPending -> "PENDING | ${outcome.message}"
    is AndroidHostedKeepLocalResolutionOutcome.Failure -> "FAILURE | ${outcome.message}"
}

private fun normalizedConflictSnapshot(document: CharacterBackupDocument): CharacterBackupDocument =
    document.copy(exportedAtEpochSeconds = 0)

private fun currentConflictEpochSeconds(): Long = System.currentTimeMillis() / 1_000L
