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
import androidx.compose.ui.Alignment
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
import io.github.mrsimkin.dndcustomaid.shared.hosted.spanishSummary
import kotlinx.coroutines.launch

private data class PendingModeration(
    val member: HostedCampaignMember,
    val action: HostedCampaignModerationAction,
)

/**
 * «Jugadores de la campaña» (redesign R1): who is in the open campaign and their state, with the
 * DM's moderation actions. The account and «⟳ Sincronizar» now live in the top bar.
 */
@Composable
internal fun DesktopCampaignPlayersScreen(
    activeCampaign: Campaign?,
    access: DesktopCampaignAccess?,
    accountSession: DesktopAccountSession,
    hostedController: DesktopHostedCampaignAdministrationController,
    onOpenCampaigns: () -> Unit,
    onQaEvent: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var roster by remember { mutableStateOf<HostedCampaignMemberRoster?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var pendingModeration by remember { mutableStateOf<PendingModeration?>(null) }

    fun launchHostedOperation(operation: suspend () -> Unit) {
        if (busy) return
        busy = true
        errorMessage = null
        scope.launch {
            try {
                operation()
            } catch (error: Throwable) {
                errorMessage = desktopHostedErrorMessage(error)
                onQaEvent("Jugadores: operación fallida (${error::class.simpleName ?: "error"})")
            } finally {
                busy = false
            }
        }
    }

    fun refreshRoster() {
        val campaign = activeCampaign ?: return
        launchHostedOperation {
            roster = hostedController.roster(campaign.id)
            statusMessage = null
        }
    }

    LaunchedEffect(activeCampaign?.id, access) {
        roster = null
        if (activeCampaign != null && access == DesktopCampaignAccess.DM) refreshRoster()
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
                            roster = hostedController.roster(campaign.id)
                            statusMessage = if (result.applied) {
                                "${pending.action.spanishLabel()}: hecho."
                            } else {
                                "No hacía falta cambiar nada."
                            }
                            onQaEvent("Jugadores: ${pending.action.name}; aplicada=${result.applied}")
                        }
                    },
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(enabled = !busy, onClick = { pendingModeration = null }) { Text("Cancelar") }
            },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp)),
    ) {
        item { WorkbenchHeading(title = "Jugadores de la campaña", subtitle = "Quién está en la campaña abierta y su estado.") }

        if (activeCampaign == null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                    Text("Primero abre una campaña.")
                    Button(onClick = onOpenCampaigns) { Text("Ir a Campañas") }
                }
            }
            return@LazyColumn
        }

        when (access) {
            DesktopCampaignAccess.LOCAL_ONLY -> item {
                Text("«${activeCampaign.name}» está solo en este equipo: no tiene jugadores en el servidor.")
            }
            DesktopCampaignAccess.PLAYER -> item {
                NotDmNotice(campaignName = activeCampaign.name, session = accountSession, what = "La lista de jugadores")
            }
            else -> {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(enabled = !busy, onClick = ::refreshRoster) { Text(if (busy) "Actualizando…" else "Actualizar lista") }
                        statusMessage?.let { Text(it, style = MaterialTheme.typography.body2) }
                    }
                }
                errorMessage?.let { message ->
                    item { Text(message, color = MaterialTheme.colors.error) }
                }
                roster?.let { currentRoster ->
                    items(items = currentRoster.members, key = { it.userId.toString() }) { member ->
                        HostedMemberCard(
                            member = member,
                            busy = busy,
                            onModerate = { action -> pendingModeration = PendingModeration(member, action) },
                        )
                    }
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
                Text("Es el DM de la campaña.", style = MaterialTheme.typography.caption)
            }
        }
    }
}
