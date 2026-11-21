package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignInvitation
import io.github.mrsimkin.dndcustomaid.shared.qr.QrCode
import io.github.mrsimkin.dndcustomaid.shared.qr.qrCode
import kotlinx.coroutines.launch

/**
 * «Invitar a un jugador» (owner, 2026-11-18): the DM shows a code to dictate, a link to send
 * «y/o» a QR to scan, all three carrying the same six letters and the same expiry date.
 */
@Composable
internal fun DesktopInvitationCard(
    campaign: Campaign,
    invitation: HostedCampaignInvitation?,
    loading: Boolean,
    message: String?,
    onCreate: (days: Int) -> Unit,
    onRevoke: () -> Unit,
    onCopy: (String) -> Unit,
    linkBaseUrl: String,
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(desktopSpacing(16.dp)), verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
            Text("Invitar a un jugador", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Dale a quien quieras jugar contigo el código, el enlace o el QR. Con cualquiera de los tres entra a " +
                    "«${campaign.name}» como jugador. Después le asignas su personaje aquí mismo.",
                style = MaterialTheme.typography.bodyMedium,
            )

            if (invitation == null) {
                Text(
                    if (loading) "Preguntando al servidor…" else "Ahora mismo no hay ninguna invitación viva.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                    Button(enabled = !loading, onClick = { onCreate(7) }) { Text("Crear invitación (7 días)") }
                    OutlinedButton(enabled = !loading, onClick = { onCreate(30) }) { Text("30 días") }
                }
            } else {
                val link = invitation.link(linkBaseUrl)
                Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp)), verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp)), modifier = Modifier.weight(1f)) {
                        Text("Código", style = MaterialTheme.typography.labelMedium)
                        Text(
                            invitation.code.toList().joinToString(" "),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text("Vence el " + formatPcEpoch(invitation.expiresAtEpochSeconds).take(10), style = MaterialTheme.typography.bodySmall)
                        Text("Enlace", style = MaterialTheme.typography.labelMedium)
                        Text(link, style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                            OutlinedButton(onClick = { onCopy(invitation.code) }) { Text("Copiar el código") }
                            OutlinedButton(onClick = { onCopy(link) }) { Text("Copiar el enlace") }
                        }
                    }
                    QrImage(text = link)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                    OutlinedButton(enabled = !loading, onClick = { onCreate(7) }) { Text("Cambiar el código") }
                    TextButton(enabled = !loading, onClick = onRevoke) { Text("Anular la invitación") }
                }
            }
            message?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
        }
    }
}

/** The QR, drawn module by module: the App writes its own, without any barcode library. */
@Composable
internal fun QrImage(text: String, sizeDp: Int = 168) {
    val code: QrCode? = remember(text) { runCatching { qrCode(text) }.getOrNull() }
    if (code == null) {
        Text("No se pudo dibujar el QR.", style = MaterialTheme.typography.bodySmall)
        return
    }
    // A reader expects dark on white, whatever the theme of the App is.
    val dark = Color.Black
    Canvas(Modifier.size(sizeDp.dp)) {
        val quiet = 2
        val modules = code.size + quiet * 2
        val cell = size.minDimension / modules
        drawRect(color = Color.White, topLeft = Offset.Zero, size = Size(size.minDimension, size.minDimension))
        for (y in 0 until code.size) {
            for (x in 0 until code.size) {
                if (!code.isDark(x, y)) continue
                drawRect(
                    color = dark,
                    topLeft = Offset((x + quiet) * cell, (y + quiet) * cell),
                    size = Size(cell, cell),
                )
            }
        }
    }
}

/** Loads the live invitation once and keeps the card's own state. */
@Composable
internal fun DesktopInvitationSection(
    campaign: Campaign,
    load: suspend () -> HostedCampaignInvitation?,
    create: suspend (Int) -> HostedCampaignInvitation,
    revoke: suspend () -> Boolean,
    copy: (String) -> Unit,
    linkBaseUrl: String,
) {
    var invitation by remember(campaign.id) { mutableStateOf<HostedCampaignInvitation?>(null) }
    var loading by remember(campaign.id) { mutableStateOf(true) }
    var message by remember(campaign.id) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(campaign.id) {
        loading = true
        runCatching { load() }
            .onSuccess { invitation = it; message = null }
            .onFailure { message = "No se pudo preguntar por la invitación: ${it.message}" }
        loading = false
    }

    DesktopInvitationCard(
        campaign = campaign,
        invitation = invitation,
        loading = loading,
        message = message,
        linkBaseUrl = linkBaseUrl,
        onCopy = { value ->
            copy(value)
            message = "Copiado: $value"
        },
        onCreate = { days ->
            loading = true
            scope.launch {
                runCatching { create(days) }
                    .onSuccess { invitation = it; message = "Listo. El código vale ${days} días." }
                    .onFailure { message = "No se pudo crear la invitación: ${it.message}" }
                loading = false
            }
        },
        onRevoke = {
            loading = true
            scope.launch {
                runCatching { revoke() }
                    .onSuccess { invitation = null; message = "La invitación quedó anulada." }
                    .onFailure { message = "No se pudo anular: ${it.message}" }
                loading = false
            }
        },
    )
}
