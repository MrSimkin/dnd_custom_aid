package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRulesPassage
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiErrorCode
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAuthenticationUnavailableException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignAdministrationClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignBootstrapResult
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignBootstrapService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignMember
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignMemberRoster
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignModerationAction
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignModerationResult
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSyncResult
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedDmContentSyncService
import io.github.mrsimkin.dndcustomaid.shared.hosted.dmContentSyncScopes
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcAuthorityAdministrationClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcAuthorityUpdate
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import kotlin.uuid.Uuid

/**
 * Desktop platform/application adapter over the provider-neutral Shared hosted clients.
 *
 * This class deliberately owns no authentication credentials. It receives the Desktop-only
 * authentication controller only through the HostedAccessTokenProvider contract implemented by
 * that controller, and delegates canonical campaign convergence to HostedCampaignBootstrapService.
 */
internal class DesktopHostedCampaignAdministrationController(
    private val database: AppDatabase,
    accessTokens: DesktopHostedAuthController,
    private val apiClient: HostedApiClient = HostedApiClient(
        baseUrl = DesktopHostedDevelopmentEnvironment.HOSTED_API_BASE_URL,
        accessTokens = accessTokens,
    ),
    private val administrationClient: HostedCampaignAdministrationClient =
        HostedCampaignAdministrationClient(
            baseUrl = DesktopHostedDevelopmentEnvironment.HOSTED_API_BASE_URL,
            accessTokens = accessTokens,
        ),
    private val pcAuthorityClient: HostedPcAuthorityAdministrationClient =
        HostedPcAuthorityAdministrationClient(
            baseUrl = DesktopHostedDevelopmentEnvironment.HOSTED_API_BASE_URL,
            accessTokens = accessTokens,
        ),
    mediaBlobs: MediaBlobStore? = null,
) {
    private val bootstrapService = HostedCampaignBootstrapService(database, apiClient)
    private val spine = IntegratedSpineRepository(database)
    private val dmContentSync = HostedDmContentSyncService(database, apiClient, blobs = mediaBlobs)

    suspend fun bootstrap(): HostedCampaignBootstrapResult = bootstrapService.refresh()

    /** Package 8: a rules answer from the SRD passages found on this computer. */
    suspend fun answerRules(question: String, passages: List<HostedRulesPassage>): String =
        apiClient.answerRules(question, passages)

    /**
     * Base para DM, package 1: sends this App de PC's DM content (personal library and every
     * campaign where the account is an active DM) and receives changes made on the tablet.
     */
    suspend fun syncDmContent(
        nowEpochSeconds: Long = System.currentTimeMillis() / 1000,
        onProgress: (io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSyncProgress) -> Unit = {},
    ): DmContentSyncResult {
        val account = bootstrapService.refresh().account
        return dmContentSync.synchronize(dmContentSyncScopes(database, account.id), nowEpochSeconds, onProgress)
    }

    /**
     * Owner's D (2026-11-20): one «Sincronizar» runs them all. Until now Windows never delivered
     * the PC changes the DM had queued — only the tablet did — so a correction stayed here.
     */
    suspend fun syncEverything(
        nowEpochSeconds: Long = System.currentTimeMillis() / 1000,
        onProgress: (io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSyncProgress) -> Unit = {},
    ): DmContentSyncResult {
        val account = bootstrapService.refresh().account
        io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxDeliveryService(
            outbox = io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxRepository(database),
            api = apiClient,
        ).deliverReady(attemptedAtEpochSeconds = nowEpochSeconds)
        return dmContentSync.synchronize(dmContentSyncScopes(database, account.id), nowEpochSeconds, onProgress)
    }

    /** What is still waiting on this computer, without asking the server. */
    fun syncStatus(lastSyncEpochSeconds: Long?, missingImages: Int = 0) =
        io.github.mrsimkin.dndcustomaid.shared.hosted.SyncStatusReader(database)
            .status(lastSyncEpochSeconds, missingImages)

    /** «Nueva campaña»: created on the server with the open account as its DM. */
    suspend fun createHostedCampaign(name: String) {
        apiClient.createCampaign(mutationId = Uuid.random(), campaignId = Uuid.random(), name = name)
    }

    /** Refactor F5 (AGENTS §6.4): with test data and no account, a campaign that exists only here. */
    fun createLocalCampaignForTesting(name: String) {
        check(io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory.usesTestDataDirectory()) {
            "Only a test data folder creates campaigns without the server."
        }
        val repository = io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository(database)
        repository.setActiveCampaign(repository.createCampaign(name.trim()).id)
    }

    fun membership(campaignId: Uuid, accountId: Uuid): CampaignMembership? =
        spine.membership(campaignId, accountId)

    /** Everyone known on this computer for the campaign; empty for a campaign that exists only here. */
    fun memberships(campaignId: Uuid): List<CampaignMembership> = spine.memberships(campaignId)

    suspend fun roster(campaignId: Uuid): HostedCampaignMemberRoster {
        val roster = administrationClient.members(campaignId)
        require(roster.campaignId == campaignId) {
            "Hosted campaign roster identity did not match the requested campaign."
        }
        database.transaction {
            roster.members.forEach { member ->
                val existingAccount = spine.account(member.userId)
                spine.upsertAccount(
                    AccountIdentity(
                        id = member.userId,
                        externalSubject = existingAccount?.externalSubject,
                        displayName = member.displayName,
                    ),
                )
                spine.upsertMembership(
                    CampaignMembership(
                        campaignId = campaignId,
                        accountId = member.userId,
                        role = member.role,
                        status = member.status,
                    ),
                )
            }
        }
        return roster
    }

    suspend fun setPcAuthority(
        campaignId: Uuid,
        pcId: Uuid,
        ownerAccountId: Uuid?,
        controllerAccountId: Uuid?,
    ): HostedPcAuthorityUpdate {
        // Hydrate current membership/account state before the hosted mutation so a successful
        // server update can always converge locally without a second network request afterward.
        roster(campaignId)
        val result = pcAuthorityClient.setAuthority(
            campaignId = campaignId,
            pcId = pcId,
            ownerUserId = ownerAccountId,
            controllerUserId = controllerAccountId,
        )
        spine.setPcAuthority(result.authority)
        return result
    }

    /** Owner, 2026-11-18: the live invitation of the campaign, or null. */
    suspend fun invitation(campaignId: Uuid) = administrationClient.invitation(campaignId)

    /** A new code, which replaces whatever was live. */
    suspend fun createInvitation(campaignId: Uuid, daysValid: Int) =
        administrationClient.createInvitation(campaignId, daysValid)

    /** Stops the live code. */
    suspend fun revokeInvitation(campaignId: Uuid) = administrationClient.revokeInvitation(campaignId)

    /** Where a link made from a code points. */
    val invitationLinkBaseUrl: String = DesktopHostedDevelopmentEnvironment.HOSTED_API_BASE_URL

    suspend fun moderate(
        campaignId: Uuid,
        userId: Uuid,
        action: HostedCampaignModerationAction,
    ): HostedCampaignModerationResult =
        administrationClient.moderateMember(
            campaignId = campaignId,
            userId = userId,
            action = action,
        )

    fun close() {
        apiClient.close()
        administrationClient.close()
        pcAuthorityClient.close()
    }
}

internal fun availableModerationActions(
    member: HostedCampaignMember,
): List<HostedCampaignModerationAction> {
    if (member.role != CampaignRole.PLAYER) return emptyList()

    return when (member.status) {
        CampaignMembershipStatus.ACTIVE -> listOf(
            HostedCampaignModerationAction.KICK,
            HostedCampaignModerationAction.BAN,
        )
        CampaignMembershipStatus.KICKED -> listOf(HostedCampaignModerationAction.BAN)
        CampaignMembershipStatus.BANNED -> listOf(HostedCampaignModerationAction.LIFT_BAN)
    }
}

internal fun HostedCampaignModerationAction.spanishLabel(): String = when (this) {
    HostedCampaignModerationAction.KICK -> "Expulsar"
    HostedCampaignModerationAction.BAN -> "Bloquear"
    HostedCampaignModerationAction.LIFT_BAN -> "Levantar bloqueo"
}

internal fun HostedCampaignModerationAction.confirmationText(memberLabel: String): String = when (this) {
    HostedCampaignModerationAction.KICK ->
        "¿Expulsar a $memberLabel de la campaña? La membresía quedará como expulsada; no se borrarán personajes ni datos locales."
    HostedCampaignModerationAction.BAN ->
        "¿Bloquear a $memberLabel? La membresía quedará bloqueada hasta que se levante el bloqueo. No se borrarán personajes."
    HostedCampaignModerationAction.LIFT_BAN ->
        "¿Levantar el bloqueo de $memberLabel? La membresía volverá a estado expulsado, no activo. El reingreso pertenece al flujo de invitación posterior."
}

internal fun CampaignRole.spanishLabel(): String = when (this) {
    CampaignRole.DM -> "DM"
    CampaignRole.PLAYER -> "Jugador"
}

internal fun CampaignMembershipStatus.spanishLabel(): String = when (this) {
    CampaignMembershipStatus.ACTIVE -> "Activo"
    CampaignMembershipStatus.KICKED -> "Expulsado"
    CampaignMembershipStatus.BANNED -> "Bloqueado"
}

internal fun hostedMemberLabel(member: HostedCampaignMember): String =
    member.displayName?.trim()?.takeIf { it.isNotEmpty() }
        ?: "Cuenta ${member.userId.toString().take(8)}…"

internal fun desktopHostedErrorMessage(error: Throwable): String = when (error) {
    is HostedAuthenticationUnavailableException ->
        "No hay una sesión alojada activa. Inicia sesión nuevamente."
    is HostedApiException -> when (error.code) {
        HostedApiErrorCode.UNAUTHENTICATED ->
            "La sesión alojada ya no es válida. Inicia sesión nuevamente."
        HostedApiErrorCode.FORBIDDEN ->
            "La cuenta autenticada no tiene autorización para realizar esta acción."
        HostedApiErrorCode.NOT_FOUND ->
            "El servidor no encontró la campaña o membresía solicitada."
        HostedApiErrorCode.VALIDATION_FAILED ->
            "El servidor rechazó la solicitud porque sus datos no son válidos."
        HostedApiErrorCode.CONFLICT_STALE_REVISION,
        HostedApiErrorCode.CONFLICT_MUTATION_REUSE ->
            "El estado alojado cambió. Actualiza la campaña y vuelve a intentarlo."
        HostedApiErrorCode.GONE ->
            "El recurso alojado ya no está disponible."
        HostedApiErrorCode.TRANSIENT_FAILURE ->
            "El servicio alojado no está disponible temporalmente. Vuelve a intentarlo."
        HostedApiErrorCode.INTERNAL_ERROR ->
            error.message.takeIf { it.isNotBlank() }
                ?: "El servicio alojado devolvió un error inesperado."
    }
    else -> error.message?.takeIf { it.isNotBlank() }
        ?: "Ocurrió un error inesperado en la operación alojada."
}
