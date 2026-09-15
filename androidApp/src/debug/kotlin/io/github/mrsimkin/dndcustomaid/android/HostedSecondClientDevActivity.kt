package io.github.mrsimkin.dndcustomaid.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AndroidDatabaseFactory
import kotlinx.coroutines.launch

/**
 * Debug-only isolated local client used to prove multi-client convergence on one physical device.
 *
 * It intentionally uses a different SQLite file from the normal Player while sharing only the
 * remembered DEV identity/session and the real hosted API. This gives the owner two independent
 * local sync states without pretending they are two production launchers.
 */
class HostedSecondClientDevActivity : ComponentActivity() {
    private val database by lazy {
        AndroidDatabaseFactory(applicationContext).create(SECOND_CLIENT_DATABASE_NAME)
    }
    private val campaigns by lazy { CampaignRepository(database) }
    private val characters by lazy { CharacterRepository(database) }
    private val hosted by lazy { AndroidHostedCampaignBootstrapController(database) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HostedSecondClientDevScreen(
                        hasSession = hosted::hasRememberedSession,
                        describeLocalState = ::describeLocalState,
                        synchronize = hosted::refresh,
                        renameFirstPc = ::renameFirstPc,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        hosted.close()
        super.onDestroy()
    }

    private fun describeLocalState(): String {
        val localCampaigns = campaigns.listCampaigns()
        if (localCampaigns.isEmpty()) {
            return "Cliente B local: sin campañas ni personajes."
        }

        val lines = mutableListOf("Cliente B local:")
        for (campaign in localCampaigns.sortedBy { it.name }) {
            val pcs = characters.listCharacters(campaign.id).sortedBy { it.name }
            lines += "• ${campaign.name}: ${pcs.size} PC(s)"
            pcs.forEach { pc -> lines += "  - ${pc.name}" }
        }
        return lines.joinToString("\n")
    }

    private fun renameFirstPc(rawName: String): String {
        val name = rawName.trim()
        require(name.isNotEmpty()) { "El nombre no puede estar vacío." }
        val first = campaigns.listCampaigns()
            .asSequence()
            .sortedBy { it.name }
            .flatMap { campaign -> characters.listCharacters(campaign.id).sortedBy { it.name }.asSequence() }
            .firstOrNull()
            ?: return "Cliente B todavía no tiene un PC local para editar. Sincroniza primero."

        characters.saveCharacter(first.copy(name = name))
        return "Cambio local guardado solo en Cliente B: «${first.name}» → «$name». Todavía no se ha sincronizado."
    }

    private companion object {
        const val SECOND_CLIENT_DATABASE_NAME = "dnd_custom_aid_second_client_dev.db"
    }
}

@Composable
private fun HostedSecondClientDevScreen(
    hasSession: () -> Boolean,
    describeLocalState: () -> String,
    synchronize: suspend () -> AndroidHostedCampaignBootstrapOutcome,
    renameFirstPc: (String) -> String,
) {
    var status by remember {
        mutableStateOf(
            if (hasSession()) {
                "Sesión DEV disponible. Este cliente usa una base local aislada del Player normal."
            } else {
                "No hay sesión DEV recordada. Autentícate primero con DnD Aid - Hosted DEV Auth."
            },
        )
    }
    var localState by remember { mutableStateOf(describeLocalState()) }
    var newName by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun refreshLocalState() {
        localState = describeLocalState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Prueba DEV — Cliente B aislado",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Simula un segundo dispositivo con otra base SQLite local. Comparte la identidad DEV y el servidor real, pero no la base local del Player normal.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(status, style = MaterialTheme.typography.bodyMedium)
        Text(localState, style = MaterialTheme.typography.bodySmall)

        Button(
            enabled = !busy && hasSession(),
            onClick = {
                scope.launch {
                    busy = true
                    try {
                        status = when (val outcome = synchronize()) {
                            is AndroidHostedCampaignBootstrapOutcome.Success -> buildString {
                                append("Sincronización Cliente B completa. ")
                                append("Campañas hospedadas=${outcome.hostedCampaignCount}; ")
                                append("PC hospedados=${outcome.hostedPcCount}; ")
                                append("PC aplicados=${outcome.appliedPcCount}; ")
                                append("conflictos PC=${outcome.pcConflictCount}; ")
                                append("cambios confirmados=${outcome.acknowledgedMutationCount}.")
                            }
                            AndroidHostedCampaignBootstrapOutcome.NoRememberedSession ->
                                "No hay sesión DEV recordada."
                            is AndroidHostedCampaignBootstrapOutcome.Failure -> outcome.message
                        }
                        refreshLocalState()
                    } finally {
                        busy = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (busy) "Sincronizando…" else "Sincronizar Cliente B")
        }

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Nuevo nombre del primer PC listado en Cliente B") },
            singleLine = true,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            enabled = !busy && newName.isNotBlank(),
            onClick = {
                status = try {
                    renameFirstPc(newName)
                } catch (_: IllegalArgumentException) {
                    "No se pudo guardar el cambio local de Cliente B."
                }
                newName = ""
                refreshLocalState()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar cambio local en Cliente B")
        }

        Text(
            text = "Esta pantalla es solo de depuración. No borra ni modifica la base local del Player normal.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
