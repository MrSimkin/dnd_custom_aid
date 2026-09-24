package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcPullConflictReason
import kotlin.uuid.Uuid

/**
 * Small tester-facing diagnostic projection used by the debug QA surface.
 *
 * Keep this deliberately separate from authentication/network payloads: it must never contain
 * access tokens, refresh tokens, authorization headers, provider secrets or database credentials.
 * Future physical-QA packages can extend these projections with the minimum evidence needed for
 * their gate instead of creating a new ad-hoc diagnostic screen each time.
 */
internal enum class AndroidHostedPcPullPhase {
    INITIAL_PULL,
    FINAL_PULL,
}

internal data class AndroidHostedCampaignSyncDiagnostic(
    val campaignId: Uuid,
    val campaignName: String?,
    val returnedAsActiveHostedCampaign: Boolean,
    val appliedByBootstrap: Boolean,
    val eligibleForPcSync: Boolean,
    val conflictReason: String? = null,
    val localRevision: Long? = null,
    val hostedRevision: Long? = null,
)

internal data class AndroidHostedPcConflictDiagnostic(
    val phase: AndroidHostedPcPullPhase,
    val campaignId: Uuid,
    val campaignName: String?,
    val pcId: Uuid,
    val pcName: String?,
    val reason: HostedPcPullConflictReason,
    val localRevision: Long,
    val hostedRevision: Long,
    val baselinePresent: Boolean,
    val baselineRevision: Long?,
    val localDiffersFromBaseline: Boolean?,
    val localEqualsHosted: Boolean?,
    val pendingOutboxState: String?,
)

internal fun hostedQaReport(
    outcome: AndroidHostedCampaignBootstrapOutcome,
    generatedAtEpochSeconds: Long,
    appVersionName: String,
    appVersionCode: Int,
    outboxDescription: String,
): String = buildString {
    appendLine("D&D Custom Aid — QA Diagnostic Log")
    appendLine("Generated epoch seconds: $generatedAtEpochSeconds")
    appendLine("App version: $appVersionName ($appVersionCode)")
    appendLine("Build: DEBUG QA")
    appendLine("Secrets/tokens: NOT INCLUDED")
    appendLine()

    when (outcome) {
        AndroidHostedCampaignBootstrapOutcome.NoRememberedSession -> {
            appendLine("=== HOSTED SYNC RESULT ===")
            appendLine("Result: NO_REMEMBERED_SESSION")
        }

        is AndroidHostedCampaignBootstrapOutcome.Failure -> {
            appendLine("=== HOSTED SYNC RESULT ===")
            appendLine("Result: FAILURE")
            appendLine("Phase: ${outcome.phase.name}")
            appendLine("Diagnostic: ${outcome.diagnostic}")
            appendLine("Message: ${outcome.message.replace('\n', ' ')}")
        }

        is AndroidHostedCampaignBootstrapOutcome.Success -> {
            appendLine("=== HOSTED SYNC SUMMARY ===")
            appendLine("Hosted campaigns: ${outcome.hostedCampaignCount}")
            appendLine("Eligible campaigns: ${outcome.eligibleCampaignCount}")
            appendLine("Applied/reconciled campaigns: ${outcome.appliedCampaignCount}")
            appendLine("Campaign conflicts: ${outcome.campaignConflictCount}")
            appendLine()
            appendLine("Hosted PCs: ${outcome.hostedPcCount}")
            appendLine("Applied PCs: ${outcome.appliedPcCount}")
            appendLine("Unchanged PCs: ${outcome.unchangedPcCount}")
            appendLine("Tombstoned PCs: ${outcome.tombstonedPcCount}")
            appendLine("PC conflicts (final pull): ${outcome.pcConflictCount}")
            appendLine("PC conflict observations (all pull phases): ${outcome.pcConflictDiagnostics.size}")
            appendLine()
            appendLine("Queued PC snapshots: ${outcome.queuedPcSnapshotCount}")
            appendLine("Acknowledged mutations: ${outcome.acknowledgedMutationCount}")
            appendLine("Retryable mutations: ${outcome.retryableMutationCount}")
            appendLine("Blocked mutations: ${outcome.blockedMutationCount}")

            if (outcome.campaignDiagnostics.isNotEmpty()) {
                outcome.campaignDiagnostics.forEach { campaign ->
                    appendLine()
                    appendLine("=== CAMPAIGN ===")
                    appendLine("Campaign: ${campaign.campaignName ?: "UNKNOWN"}")
                    appendLine("Campaign ID: ${campaign.campaignId}")
                    appendLine("Returned as active hosted campaign: ${yesNo(campaign.returnedAsActiveHostedCampaign)}")
                    appendLine("Applied by membership bootstrap: ${yesNo(campaign.appliedByBootstrap)}")
                    appendLine("Eligible for PC sync: ${yesNo(campaign.eligibleForPcSync)}")
                    appendLine("Campaign conflict: ${campaign.conflictReason ?: "NONE"}")
                    appendLine("Local revision: ${campaign.localRevision ?: "n/a"}")
                    appendLine("Hosted revision: ${campaign.hostedRevision ?: "n/a"}")
                }
            }

            if (outcome.pcConflictDiagnostics.isNotEmpty()) {
                outcome.pcConflictDiagnostics.forEach { conflict ->
                    appendLine()
                    appendLine("=== PC CONFLICT ===")
                    appendLine("Phase: ${conflict.phase}")
                    appendLine("Campaign: ${conflict.campaignName ?: "UNKNOWN"}")
                    appendLine("Campaign ID: ${conflict.campaignId}")
                    appendLine("PC: ${conflict.pcName ?: "UNKNOWN"}")
                    appendLine("PC ID: ${conflict.pcId}")
                    appendLine("Conflict reason: ${conflict.reason}")
                    appendLine("Local sync revision: ${conflict.localRevision}")
                    appendLine("Hosted revision: ${conflict.hostedRevision}")
                    appendLine("Baseline present: ${yesNo(conflict.baselinePresent)}")
                    appendLine("Baseline revision: ${conflict.baselineRevision ?: "NONE"}")
                    appendLine("Local differs from baseline: ${triState(conflict.localDiffersFromBaseline)}")
                    appendLine("Local equals current hosted: ${triState(conflict.localEqualsHosted)}")
                    appendLine("Pending outbox mutation: ${yesNo(conflict.pendingOutboxState != null)}")
                    appendLine("Outbox state: ${conflict.pendingOutboxState ?: "NONE"}")
                }
            } else {
                appendLine()
                appendLine("=== PC CONFLICTS ===")
                appendLine("NONE")
            }
        }
    }

    appendLine()
    appendLine("=== LOCAL HOSTED OUTBOX ===")
    appendLine(outboxDescription.trim())
}.trimEnd()

private fun yesNo(value: Boolean): String = if (value) "YES" else "NO"

private fun triState(value: Boolean?): String = when (value) {
    true -> "YES"
    false -> "NO"
    null -> "UNKNOWN"
}
