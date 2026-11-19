package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignAdministrationClient

import kotlinx.coroutines.launch

/**
 * «Unirme con un código» (owner, 2026-11-18): the other half of the DM's invitation. Six letters
 * and the player is in the campaign; their PC is assigned by the DM afterwards.
 */
@Composable
internal fun AndroidJoinWithCodeDialog(
    onDismiss: () -> Unit,
    onJoined: (String) -> Unit,
    client: HostedCampaignAdministrationClient = remember {
        HostedCampaignAdministrationClient(
            baseUrl = HostedDevelopmentEnvironment.API_BASE_URL,
            accessTokens = DescopeHostedAccessTokenProvider(),
        )
    },
) {
    var code by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val clean = code.filter { it.isLetter() }.uppercase().take(6)

    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = { Text("Unirme con un código") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Escribe las seis letras que te dio el DM. También sirve el código del enlace o del QR.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                CharacterCompactOutlinedTextFieldV4(
                    value = clean,
                    onValueChange = { code = it },
                    label = { Text("Código") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                    ),
                )
                message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            Button(
                enabled = !busy && clean.length == 6,
                onClick = {
                    busy = true
                    scope.launch {
                        runCatching { client.joinWithCode(clean) }
                            .onSuccess { joined ->
                                busy = false
                                onJoined(
                                    if (joined.alreadyMember) {
                                        "Ya estabas en «${joined.campaignName}»."
                                    } else {
                                        "Entraste a «${joined.campaignName}». Sincroniza para ver lo tuyo."
                                    },
                                )
                            }
                            .onFailure {
                                busy = false
                                message = it.message ?: "No se pudo usar ese código."
                            }
                    }
                },
            ) { Text(if (busy) "Entrando…" else "Entrar") }
        },
        dismissButton = { TextButton(enabled = !busy, onClick = onDismiss) { Text("Cancelar") } },
    )
}
