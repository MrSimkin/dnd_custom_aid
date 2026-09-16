package io.github.mrsimkin.dndcustomaid.android

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.db.AndroidDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiErrorCode
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedMutationType
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcPullConflictReason
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRetryState
import kotlinx.coroutines.launch

/**
 * Debug-only owner QA harness.
 *
 * Authentication proof remains available here, but this surface also owns the reusable physical-QA
 * log. Future QA packages should extend the structured diagnostic report rather than introducing
 * unrelated one-off debug screens. Tokens and credentials must never be copied into that report.
 */
class HostedDevAuthActivity : ComponentActivity() {
    private val authController by lazy { AndroidHostedAuthController() }
    private val database by lazy { AndroidDatabaseFactory(applicationContext).create() }
    private val hostedOutbox by lazy { HostedOutboxRepository(database) }
    private val hostedBootstrap by lazy { AndroidHostedCampaignBootstrapController(database) }
    private val hostedConflictResolver by lazy { AndroidHostedPcConflictResolver(database) }
    private var lastQaOutcome: AndroidHostedCampaignBootstrapOutcome? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HostedDevAuthScreen(
                        authController = authController,
                        describeHostedOutbox = ::describeHostedOutbox,
                        retryBlockedPcValidationFailures = ::retryBlockedPcValidationFailures,
                        runHostedSyncQa = ::runHostedSyncQa,
                        keepLocalForLatestPcConflict = ::keepLocalForLatestPcConflict,
                        copyQaLog = ::copyQaLog,
                        shareQaLog = ::shareQaLog,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        hostedConflictResolver.close()
        hostedBootstrap.close()
        authController.close()
        super.onDestroy()
    }

    private fun describeHostedOutbox(): String {
        val mutations = hostedOutbox.allMutations()
        if (mutations.isEmpty()) {
            return "Outbox local: vacío. No hay cambios hospedados pendientes ni bloqueados."
        }

        val ready = mutations.count { it.retryState == HostedRetryState.READY }
        val blocked = mutations.count { it.retryState == HostedRetryState.BLOCKED }
        val details = mutations.joinToString(separator = "\n") { mutation ->
            val expectedRevision = mutation.expectedRevision?.toString() ?: "n/a"
            val errorCode = mutation.lastErrorCode ?: "sin error registrado"
            val errorMessage = mutation.lastErrorMessage?.replace('\n', ' ') ?: "sin mensaje"
            "${mutation.type} | ${mutation.retryState} | intentos=${mutation.attemptCount} | expectedRevision=$expectedRevision | error=$errorCode | mensaje=$errorMessage"
        }
        return "Outbox local: total=${mutations.size}, READY=$ready, BLOCKED=$blocked\n$details"
    }

    private suspend fun runHostedSyncQa(): String {
        val outcome = hostedBootstrap.refresh()
        lastQaOutcome = outcome
        return hostedQaReport(
            outcome = outcome,
            generatedAtEpochSeconds = System.currentTimeMillis() / 1_000L,
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            outboxDescription = describeHostedOutbox(),
        )
    }

    private suspend fun keepLocalForLatestPcConflict(): String {
        val success = lastQaOutcome as? AndroidHostedCampaignBootstrapOutcome.Success
        val candidates = success
            ?.pcConflictDiagnostics
            .orEmpty()
            .asSequence()
            .filter { diagnostic ->
                diagnostic.phase == AndroidHostedPcPullPhase.FINAL_PULL &&
                    diagnostic.reason == HostedPcPullConflictReason.LOCAL_AND_HOSTED_CHANGED
            }
            .distinctBy { it.pcId }
            .toList()

        val resolution = when (candidates.size) {
            1 -> hostedConflictResolver.keepLocal(candidates.single())
            0 -> AndroidHostedKeepLocalResolutionOutcome.Refused(
                "La última sincronización QA no contiene un conflicto LOCAL_AND_HOSTED_CHANGED resoluble.",
            )
            else -> AndroidHostedKeepLocalResolutionOutcome.Refused(
                "Hay ${candidates.size} PCs con conflicto. Esta acción QA de seguridad resuelve uno por vez; ejecuta una prueba focalizada antes de continuar.",
            )
        }
        val resolutionText = renderKeepLocalResolutionOutcome(resolution)
        val refreshedLog = runHostedSyncQa()
        return buildString {
            append(refreshedLog)
            appendLine()
            appendLine()
            appendLine("=== LAST EXPLICIT KEEP-LOCAL RESOLUTION ===")
            append(resolutionText)
        }
    }

    private fun copyQaLog(log: String) {
        val clipboard = getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(ClipData.newPlainText("D&D Custom Aid QA log", log))
    }

    private fun shareQaLog(log: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "D&D Custom Aid QA log")
            putExtra(Intent.EXTRA_TEXT, log)
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir log QA"))
    }

    private fun retryBlockedPcValidationFailures(): String {
        val candidates = hostedOutbox.allMutations().filter { mutation ->
            mutation.type == HostedMutationType.PC_SNAPSHOT_PUT &&
                mutation.retryState == HostedRetryState.BLOCKED &&
                mutation.lastErrorCode == HostedApiErrorCode.VALIDATION_FAILED.name
        }
        if (candidates.isEmpty()) {
            return "No hay mutaciones PC bloqueadas por VALIDATION_FAILED para reintentar."
        }

        var readyCount = 0
        candidates.forEach { mutation ->
            val payloadStillValid = runCatching {
                hostedOutbox.pcSnapshotPayload(mutation)
            }.isSuccess
            if (payloadStillValid) {
                hostedOutbox.markReady(mutation.mutationId)
                readyCount += 1
            }
        }

        return if (readyCount > 0) {
            "$readyCount mutación(es) PC válida(s) fueron marcadas READY. Vuelve al Player y pulsa «Sincronizar campañas hospedadas»."
        } else {
            "Las mutaciones bloqueadas no superaron la validación local y no fueron modificadas."
        }
    }
}

@Composable
private fun HostedDevAuthScreen(
    authController: AndroidHostedAuthController,
    describeHostedOutbox: () -> String,
    retryBlockedPcValidationFailures: () -> String,
    runHostedSyncQa: suspend () -> String,
    keepLocalForLatestPcConflict: suspend () -> String,
    copyQaLog: (String) -> Unit,
    shareQaLog: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var otpRequested by remember { mutableStateOf(false) }
    var hasSession by remember { mutableStateOf(authController.hasRememberedSession()) }
    var busy by remember { mutableStateOf(false) }
    var qaLog by remember { mutableStateOf("") }
    var showKeepLocalConfirmation by remember { mutableStateOf(false) }
    var status by remember {
        mutableStateOf(
            if (hasSession) {
                "Se encontró una sesión recordada de Descope. Puedes ejecutar una sincronización QA completa."
            } else {
                "No hay una sesión recordada en este dispositivo."
            },
        )
    }
    val scope = rememberCoroutineScope()

    fun runAction(block: suspend () -> Unit) {
        if (busy) return
        scope.launch {
            busy = true
            try {
                block()
            } catch (_: Exception) {
                status = "No se pudo completar la operación. Revisa la conexión y vuelve a intentarlo."
            } finally {
                busy = false
            }
        }
    }

    if (showKeepLocalConfirmation) {
        AlertDialog(
            onDismissRequest = { showKeepLocalConfirmation = false },
            title = { Text("Conservar versión local") },
            text = {
                Text(
                    "Esta acción intentará subir el PC local por encima de la versión hospedada que acabas de revisar. " +
                        "Solo se ejecutará si el servidor sigue exactamente en esa misma revisión; si otro cliente cambió el PC otra vez, se detendrá sin sobrescribirlo.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showKeepLocalConfirmation = false
                        runAction {
                            qaLog = keepLocalForLatestPcConflict()
                            status = "Resolución explícita ejecutada. Revisa el log actualizado y compártelo antes de continuar."
                        }
                    },
                ) {
                    Text("Sí, conservar local")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKeepLocalConfirmation = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "QA / autenticación hospedada DEV",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Pantalla exclusiva de builds debug. La sincronización QA revisa todas las campañas hospedadas elegibles; la campaña activa del Player no limita este alcance.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
        )

        Button(
            enabled = !busy && hasSession,
            onClick = {
                runAction {
                    qaLog = runHostedSyncQa()
                    status = "Sincronización QA completada. Revisa el log y usa «Copiar log QA» para pegarlo en ChatGPT."
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (busy) "Procesando…" else "Ejecutar sincronización QA")
        }

        Button(
            enabled = !busy && hasSession && qaLog.isNotBlank(),
            onClick = { showKeepLocalConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Resolver conflicto: conservar PC local")
        }
        Text(
            text = "Usa esta acción solo después de revisar un LOCAL_AND_HOSTED_CHANGED en el log. Nunca se ejecuta automáticamente.",
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            enabled = !busy && qaLog.isNotBlank(),
            onClick = {
                copyQaLog(qaLog)
                status = "Log QA copiado al portapapeles."
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Copiar log QA")
        }

        Button(
            enabled = !busy && qaLog.isNotBlank(),
            onClick = { shareQaLog(qaLog) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Compartir log QA")
        }

        if (qaLog.isNotBlank()) {
            Text(
                text = qaLog,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Button(
            enabled = !busy,
            onClick = {
                status = describeHostedOutbox()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Diagnosticar outbox local")
        }

        Button(
            enabled = !busy,
            onClick = {
                status = retryBlockedPcValidationFailures()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Reintentar bloqueo PC de validación")
        }

        if (hasSession) {
            Button(
                enabled = !busy,
                onClick = {
                    runAction {
                        val account = authController.currentHostedAccount()
                        status = "Sesión válida. Worker respondió para la cuenta ${account.id}."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Probar sesión recordada")
            }

            TextButton(
                enabled = !busy,
                onClick = {
                    runAction {
                        authController.signOut()
                        hasSession = false
                        otpRequested = false
                        code = ""
                        qaLog = ""
                        lastQaOutcome = null
                        status = "Sesión cerrada en este dispositivo."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cerrar sesión DEV")
            }
        } else {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                singleLine = true,
                enabled = !busy && !otpRequested,
                modifier = Modifier.fillMaxWidth(),
            )

            if (!otpRequested) {
                Button(
                    enabled = !busy && email.isNotBlank(),
                    onClick = {
                        runAction {
                            val masked = authController.sendEmailOtp(email)
                            otpRequested = true
                            status = "Código enviado a $masked."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Enviar código")
                }
            } else {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Código de un solo uso") },
                    singleLine = true,
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    enabled = !busy && code.isNotBlank(),
                    onClick = {
                        runAction {
                            authController.verifyEmailOtp(email, code)
                            hasSession = true
                            code = ""
                            val account = authController.currentHostedAccount()
                            status = "Autenticación completa. Worker respondió para la cuenta ${account.id}. La sesión quedó recordada de forma segura por Descope."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Verificar y probar Worker")
                }

                TextButton(
                    enabled = !busy,
                    onClick = {
                        otpRequested = false
                        code = ""
                        status = "Solicitud de código cancelada."
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Usar otro correo")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "El log QA no incluye JWT, refresh tokens, cabeceras de autorización ni credenciales de base de datos.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
