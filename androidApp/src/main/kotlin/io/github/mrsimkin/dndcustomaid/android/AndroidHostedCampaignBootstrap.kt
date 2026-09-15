package io.github.mrsimkin.dndcustomaid.android

import com.descope.Descope
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiErrorCode
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAuthenticationUnavailableException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignBootstrapService

internal sealed interface AndroidHostedCampaignBootstrapOutcome {
    data class Success(
        val hostedCampaignCount: Int,
        val appliedCampaignCount: Int,
        val conflictCount: Int,
        val accountDisplayName: String?,
    ) : AndroidHostedCampaignBootstrapOutcome

    data object NoRememberedSession : AndroidHostedCampaignBootstrapOutcome

    data class Failure(
        val message: String,
    ) : AndroidHostedCampaignBootstrapOutcome
}

/**
 * Android platform adapter for the existing shared hosted campaign bootstrap.
 *
 * It deliberately owns no campaign reconciliation rules: those remain in
 * [HostedCampaignBootstrapService]. This class only supplies the real DEV API/token edge and
 * translates expected hosted failures into bounded owner-facing messages.
 */
internal class AndroidHostedCampaignBootstrapController(
    database: AppDatabase,
    private val apiClient: HostedApiClient = HostedApiClient(
        baseUrl = HostedDevelopmentEnvironment.API_BASE_URL,
        accessTokens = DescopeHostedAccessTokenProvider(),
    ),
) {
    private val bootstrap = HostedCampaignBootstrapService(
        database = database,
        api = apiClient,
    )

    fun hasRememberedSession(): Boolean =
        Descope.sessionManager.session?.refreshToken?.isExpired == false

    suspend fun refresh(): AndroidHostedCampaignBootstrapOutcome {
        if (!hasRememberedSession()) {
            return AndroidHostedCampaignBootstrapOutcome.NoRememberedSession
        }

        return try {
            val result = bootstrap.refresh()
            AndroidHostedCampaignBootstrapOutcome.Success(
                hostedCampaignCount = result.hostedCampaignCount,
                appliedCampaignCount = result.appliedCampaignIds.size,
                conflictCount = result.conflicts.size,
                accountDisplayName = result.account.displayName,
            )
        } catch (_: HostedAuthenticationUnavailableException) {
            AndroidHostedCampaignBootstrapOutcome.NoRememberedSession
        } catch (error: HostedApiException) {
            AndroidHostedCampaignBootstrapOutcome.Failure(
                message = when (error.code) {
                    HostedApiErrorCode.UNAUTHENTICATED ->
                        "La sesión hospedada ya no es válida. Vuelve a autenticarte y reintenta."
                    HostedApiErrorCode.FORBIDDEN ->
                        "La cuenta autenticada no tiene acceso a esta información hospedada."
                    else ->
                        "No se pudo actualizar desde el servidor. Reintenta cuando tengas conexión."
                },
            )
        } catch (_: Exception) {
            AndroidHostedCampaignBootstrapOutcome.Failure(
                message = "No se pudo actualizar desde el servidor. Reintenta cuando tengas conexión.",
            )
        }
    }

    fun close() {
        apiClient.close()
    }
}
