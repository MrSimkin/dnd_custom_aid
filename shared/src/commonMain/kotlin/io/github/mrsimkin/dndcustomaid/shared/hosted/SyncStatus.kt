package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase

/*
 * Owner's D (2026-11-20), point 3 of the critical review: «hay varias sincronizaciones… y hay tres
 * botones distintos que no corren todo». One «Sincronizar» runs them all, and this says, in plain
 * words, what is still waiting.
 */

data class SyncStatus(
    /** PC sheets and campaign creations queued on this device. */
    val pendingPcChanges: Int,
    /** Prepared content queued on this device. */
    val pendingDmChanges: Int,
    /** Things that changed here and on another device, waiting for the DM to choose (owner's B). */
    val differencesToDecide: Int,
    /** Changes the server refused; they stay here until the DM looks. */
    val blockedChanges: Int,
    /** Images named by a handout that are not on this device yet. */
    val missingImages: Int,
    val lastSyncEpochSeconds: Long?,
) {
    val everythingSent: Boolean
        get() = pendingPcChanges == 0 && pendingDmChanges == 0 && blockedChanges == 0

    val nothingToDo: Boolean
        get() = everythingSent && differencesToDecide == 0 && missingImages == 0

    /** What the DM reads: one line, in their words. */
    fun spanishSummary(nowEpochSeconds: Long? = null): String {
        val parts = buildList {
            if (pendingPcChanges > 0) add("$pendingPcChanges cambio(s) de personajes esperando")
            if (pendingDmChanges > 0) add("$pendingDmChanges cambio(s) de lo preparado esperando")
            if (blockedChanges > 0) add("$blockedChanges cambio(s) que el servidor no aceptó")
            if (differencesToDecide > 0) add("$differencesToDecide diferencia(s) que decidir")
            if (missingImages > 0) add("$missingImages imagen(es) que faltan")
        }
        if (parts.isEmpty()) {
            val last = lastSyncEpochSeconds
            return if (last == null) {
                "Todo al día en este aparato. Aún no has sincronizado."
            } else if (nowEpochSeconds != null) {
                "Todo al día. Última sincronización: ${agoInSpanish(nowEpochSeconds - last)}."
            } else {
                "Todo al día."
            }
        }
        return parts.joinToString(" · ")
    }
}

/** Reads what is waiting, without talking to the server. */
class SyncStatusReader(
    private val database: AppDatabase,
    private val pcOutbox: HostedOutboxRepository = HostedOutboxRepository(database),
    private val dmOutbox: HostedDmContentOutboxRepository = HostedDmContentOutboxRepository(database),
) {
    fun status(lastSyncEpochSeconds: Long?, missingImages: Int = 0): SyncStatus {
        val dmMutations = dmOutbox.allMutations()
        return SyncStatus(
            pendingPcChanges = pcOutbox.allMutations().count { it.retryState != HostedRetryState.BLOCKED },
            pendingDmChanges = dmMutations.count { it.retryState != HostedRetryState.BLOCKED },
            differencesToDecide = database.dmContentConflictQueries.selectDmContentConflicts().executeAsList().size,
            blockedChanges = pcOutbox.allMutations().count { it.retryState == HostedRetryState.BLOCKED } +
                dmMutations.count { it.retryState == HostedRetryState.BLOCKED },
            missingImages = missingImages,
            lastSyncEpochSeconds = lastSyncEpochSeconds,
        )
    }
}

internal fun agoInSpanish(seconds: Long): String = when {
    seconds < 60 -> "hace un momento"
    seconds < 3_600 -> "hace ${seconds / 60} min"
    seconds < 86_400 -> "hace ${seconds / 3_600} h"
    else -> "hace ${seconds / 86_400} d"
}
