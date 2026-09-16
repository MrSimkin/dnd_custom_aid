package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAccount
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignMember
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignMemberRoster
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignModerationAction
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import kotlinx.coroutines.launch

private data class PendingModeration(
    val member: HostedCampaignMember,
    val action: HostedCampaignModerationAction,
)

@Composable
internal fun HostedCampaignAdministrationScreen(
    activeCampaign: Campaign?,
    authController: DesktopHostedAuthController,
    hostedController: DesktopHostedCampaignAdministrationController,
    onOpenCampaigns: () -> Unit,
    onLocalCampaignsChanged: () -> Unit,
    onQaEvent: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var authPhase by remember { mutableStateOf(authController.phase) }
    var account by remember { mutableStateOf<HostedAccount?>(null) }
    var membership by remember { mutableStateOf<CampaignMembership?>(null) }
    var roster by remember { mutableStateOf<HostedCampaignMemberRoster?>(null) }
    var bootstrapSummary by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var pendingModeration by remember { mutableStateOf<PendingModeration?>(null) }

    fun updateMembershipContext() {
        membership = if (activeCampaign != null && account != null) {
            hostedController.membership(activeCampaign.id, account!!.id)
        } else {
            null
        }
    }

    fun launchHostedOperation(operation: suspend () -> Unit) {
        if (busy) return
        busy = true
        errorMessage = null
        scope.launch {
            try {
                operation()
            } catch (error: Throwable) {
                errorMessage = desktopHostedErrorMessage(error)
                onQaEvent("Operación alojada fallida: ${error::class.simpleName ?: "error"}")
            } finally {
                authPhase = authController.phase
                busy = false
            }
        }
    }

    fun applyBootstrapResult() {
        launchHostedOperation {
            val result = hostedController.bootstrap()
            account = result.account
            onLocalCampaignsChanged()
            bootstrapSummary =
                "Campañas alojadas: ${result.hostedCampaignCount} · aplicadas: ${result.appliedCampaignIds.size} · " +
                    "conflictos: ${result.conflicts.size}"
            updateMembershipContext()
            roster = null
            statusMessage = if (result.conflicts.isEmpty()) {
                "Estado alojado actualizado sin conflictos."
            } else {
                "La actualización conservó ${result.conflicts.size} conflicto(s) local(es) para resolución explícita."
            }
            onQaEvent(
                "Bootstrap alojado: campañas=${result.hostedCampaignCount}; " +
                    "aplicadas=${result.appliedCampaignIds.size}; conflictos=${result.conflicts.size}",
            )
        }
    }

    fun refreshRoster() {
        val campaign = activeCampaign ?: return
        launchHostedOperation {
            val refreshed = hostedController.roster(campaign.id)
            roster = refreshed
            statusMessage =
                "Miembros alojados actualizados · revisión de campaña ${refreshed.campaignRevision}."
            onQaEvent(
                "Roster alojado actualizado: miembros=${refreshed.members.size}; " +
                    "revisión=${refreshed.campaignRevision}",
            )
        }
    }

    LaunchedEffect(activeCampaign?.id, account?.id) {
        updateMembershipContext()
        roster = null
    }

    LaunchedEffect(Unit) {
        if (authController.hasSession()) {
            busy = true
            try {
                val result = hostedController.bootstrap()
                account = result.account
                bootstrapSummary =
                    "Campañas alojadas: ${result.hostedCampaignCount} · aplicadas: ${result.appliedCampaignIds.size} · " +
                        "conflictos: ${result.conflicts.size}"
                onLocalCampaignsChanged()
                updateMembershipContext()
            } catch (error: Throwable) {
                errorMessage = desktopHostedErrorMessage(error)
            } finally {
                authPhase = authController.phase
                busy = false
            }
        }
    }

    pendingModeration?.let { pending ->
        val label = hostedMemberLabel(pending.member)
        AlertDialog(
            onDismissRequest = { if (!busy) pendingModeration = null },
            title = { Text(pending.action.spanishLabel()) },
            text = { Text(pending.action.confirmationText(label)) },
            confirmButton = {
                Button(
                    enabled = !busy && activeCampaign != null,
                    onClick = {
                        val campaign = activeCampaign ?: return@Button
                        pendingModeration = null
                        launchHostedOperation {
                            val result = hostedController.moderate(
                                campaignId = campaign.id,
                                userId = pending.member.userId,
                                action = pending.action,
                            )
                            val refreshed = hostedController.roster(campaign.id)
                            roster = refreshed
                            statusMessage = if (result.applied) {
                                "${pending.action.spanishLabel()} aplicada y estado del servidor actualizado."
                            } else {
                                "El servidor confirmó que no era necesario cambiar el estado; roster actualizado."
                            }
                            onQaEvent(
                                "Moderación alojada: acción=${pending.action.name}; aplicada=${result.applied}; " +
                                    "revisión=${refreshed.campaignRevision}",
                            )
                        }
                    },
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !busy,
                    onClick = { pendingModeration = null },
                ) {
                    Text("Cancelar")
                }
            },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp)),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(4.dp))) {
                Text(
                    text = "Administración de campaña",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Sesión alojada, contexto canónico de campaña y moderación de membresías.",
                    style = MaterialTheme.typography.body1,
                )
            }
        }

        item {
            HostedSessionCard(
                authPhase = authPhase,
                account = account,
                email = email,
                code = code,
                busy = busy,
                onEmailChange = { email = it },
                onCodeChange = { code = it },
                onSendOtp = {
                    launchHostedOperation {
                        authController.sendEmailOtp(email)
                        authPhase = authController.phase
                        statusMessage = "Código de acceso enviado."
                        onQaEvent("Código de acceso alojado solicitado")
                    }
                },
                onVerifyOtp = {
                    launchHostedOperation {
                        authController.verifyEmailOtp(code)
                        authPhase = authController.phase
                        code = ""
                        val result = hostedController.bootstrap()
                        account = result.account
                        onLocalCampaignsChanged()
                        bootstrapSummary =
                            "Campañas alojadas: ${result.hostedCampaignCount} · aplicadas: ${result.appliedCampaignIds.size} · " +
                                "conflictos: ${result.conflicts.size}"
                        updateMembershipContext()
                        roster = null
                        statusMessage = "Sesión iniciada y campañas alojadas actualizadas."
                        onQaEvent(
                            "Sesión alojada autenticada; bootstrap campañas=${result.hostedCampaignCount}; " +
                                "conflictos=${result.conflicts.size}",
                        )
                    }
                },
                onRefreshHosted = ::applyBootstrapResult,
                onSignOut = {
                    authController.signOut()
                    authPhase = authController.phase
                    account = null
                    membership = null
                    roster = null
                    bootstrapSummary = null
                    statusMessage = "Sesión alojada cerrada. Los datos locales se conservaron."
                    errorMessage = null
                    code = ""
                    onQaEvent("Sesión alojada cerrada")
                },
            )
        }

        bootstrapSummary?.let { summary ->
            item {
                Card(modifier = Modifier.fillMaxWidth(), elevation = 1.dp) {
                    Text(
                        text = summary,
                        modifier = Modifier.padding(desktopSpacing(14.dp)),
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }

        statusMessage?.let { message ->
            item { Text(message, style = MaterialTheme.typography.body2) }
        }

        errorMessage?.let { message ->
            item {
                Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                    Column(
                        modifier = Modifier.padding(desktopSpacing(14.dp)),
                        verticalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp)),
                    ) {
                        Text("No se pudo completar la operación", fontWeight = FontWeight.Bold)
                        Text(message)
                    }
                }
            }
        }

        item {
            HostedCampaignContextCard(
                activeCampaign = activeCampaign,
                authPhase = authPhase,
                account = account,
                membership = membership,
                busy = busy,
                onOpenCampaigns = onOpenCampaigns,
                onRefreshRoster = ::refreshRoster,
            )
        }

        roster?.let { currentRoster ->
            item {
                Text(
                    "Miembros alojados · revisión ${currentRoster.campaignRevision}",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(
                items = currentRoster.members,
                key = { it.userId.toString() },
            ) { member ->
                HostedMemberCard(
                    member = member,
                    busy = busy,
                    onModerate = { action ->
                        pendingModeration = PendingModeration(member, action)
                    },
                )
            }
        }

        item {
            Text(
                "La sesión alojada de Desktop se mantiene sólo mientras la aplicación está abierta. " +
                    "Cerrar sesión o cerrar la aplicación no elimina campañas, personajes ni otros datos locales.",
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
private fun HostedSessionCard(
    authPhase: DesktopHostedAuthPhase,
    account: HostedAccount?,
    email: String,
    code: String,
    busy: Boolean,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onRefreshHosted: () -> Unit,
    onSignOut: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp)),
        ) {
            Text("Cuenta alojada", style = MaterialTheme.typography.h6)

            when (authPhase) {
                DesktopHostedAuthPhase.SIGNED_OUT -> {
                    Text("Desconectado. El trabajo local sigue disponible sin iniciar sesión.")
                    TextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Correo de la cuenta DEV") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        enabled = !busy && email.isNotBlank(),
                        onClick = onSendOtp,
                    ) {
                        Text(if (busy) "Conectando…" else "Enviar código")
                    }
                }

                DesktopHostedAuthPhase.OTP_SENT -> {
                    Text("Código enviado a ${email.ifBlank { "la cuenta indicada" }}.")
                    TextField(
                        value = code,
                        onValueChange = onCodeChange,
                        label = { Text("Código de acceso") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                        Button(
                            enabled = !busy && code.isNotBlank(),
                            onClick = onVerifyOtp,
                        ) {
                            Text(if (busy) "Verificando…" else "Verificar e iniciar sesión")
                        }
                        TextButton(enabled = !busy, onClick = onSignOut) {
                            Text("Cancelar")
                        }
                    }
                }

                DesktopHostedAuthPhase.AUTHENTICATED -> {
                    Text("Conectado")
                    Text(
                        account?.displayName?.takeIf { it.isNotBlank() } ?: "Cuenta alojada autenticada",
                        fontWeight = FontWeight.Bold,
                    )
                    account?.let {
                        SelectionContainer {
                            Text("ID de cuenta: ${it.id}", style = MaterialTheme.typography.caption)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                        Button(enabled = !busy, onClick = onRefreshHosted) {
                            Text(if (busy) "Actualizando…" else "Actualizar campañas alojadas")
                        }
                        TextButton(enabled = !busy, onClick = onSignOut) {
                            Text("Cerrar sesión")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HostedCampaignContextCard(
    activeCampaign: Campaign?,
    authPhase: DesktopHostedAuthPhase,
    account: HostedAccount?,
    membership: CampaignMembership?,
    busy: Boolean,
    onOpenCampaigns: () -> Unit,
    onRefreshRoster: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 900.dp),
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
        ) {
            Text("Contexto de campaña", style = MaterialTheme.typography.h6)

            if (activeCampaign == null) {
                Text("No hay una campaña activa seleccionada.")
                Button(onClick = onOpenCampaigns) { Text("Elegir campaña") }
                return@Column
            }

            Text(activeCampaign.name, fontWeight = FontWeight.Bold)
            SelectionContainer {
                Text("ID de campaña: ${activeCampaign.id}", style = MaterialTheme.typography.caption)
            }
            Divider()

            if (authPhase != DesktopHostedAuthPhase.AUTHENTICATED || account == null) {
                Text("Esta campaña continúa disponible localmente. Inicia sesión para comprobar su contexto alojado.")
                return@Column
            }

            if (membership == null) {
                Text(
                    "Esta campaña no tiene una membresía alojada para la cuenta autenticada. " +
                        "Se mantiene como campaña local y no se simula administración remota.",
                )
                return@Column
            }

            Text("Rol alojado: ${membership.role.spanishLabel()}")
            Text("Estado de membresía: ${membership.status.spanishLabel()}")

            when {
                membership.status != CampaignMembershipStatus.ACTIVE ->
                    Text("La membresía no está activa; el servidor no debe permitir administración de esta campaña.")
                membership.role != CampaignRole.DM ->
                    Text("La cuenta tiene rol Jugador. La administración de miembros requiere una membresía DM activa.")
                else ->
                    Button(enabled = !busy, onClick = onRefreshRoster) {
                        Text(if (busy) "Actualizando…" else "Actualizar miembros alojados")
                    }
            }
        }
    }
}

@Composable
private fun HostedMemberCard(
    member: HostedCampaignMember,
    busy: Boolean,
    onModerate: (HostedCampaignModerationAction) -> Unit,
) {
    val actions = availableModerationActions(member)

    Card(modifier = Modifier.fillMaxWidth(), elevation = 1.dp) {
        Column(
            modifier = Modifier.padding(desktopSpacing(16.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
        ) {
            Text(hostedMemberLabel(member), style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp))) {
                Text("Rol: ${member.role.spanishLabel()}")
                Text("Estado: ${member.status.spanishLabel()}")
            }
            SelectionContainer {
                Text("ID: ${member.userId}", style = MaterialTheme.typography.caption)
            }

            if (actions.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                    actions.forEach { action ->
                        Button(
                            enabled = !busy,
                            onClick = { onModerate(action) },
                        ) {
                            Text(action.spanishLabel())
                        }
                    }
                }
            } else if (member.role == CampaignRole.DM) {
                Text("Las acciones de moderación de Jugadores no se aplican a miembros DM.", style = MaterialTheme.typography.caption)
            }
        }
    }
}
