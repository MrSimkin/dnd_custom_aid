package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
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
    database: AppDatabase,
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
) {
    private val bootstrapService = HostedCampaignBootstrapService(database, apiClient)
    private val spine = IntegratedSpineRepository(database)

    suspend fun bootstrap(): HostedCampaignBootstrapResult = bootstrapService.refresh()

    fun membership(campaignId: Uuid, accountId: Uuid): CampaignMembership? =
        spine.membership(campaignId, accountId)

    suspend fun roster(campaignId: Uuid): HostedCampaignMemberRoster =
        administrationClient.members(campaignId)

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
