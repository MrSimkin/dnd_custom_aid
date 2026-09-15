package io.github.mrsimkin.dndcustomaid.android

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
import kotlinx.coroutines.launch

/**
 * Debug-only owner test harness for the first real Android -> Descope -> Worker -> Neon proof.
 *
 * This activity is deliberately separate from the normal Player UI so the temporary DEV
 * email-OTP path does not silently become the final product login UX.
 */
class HostedDevAuthActivity : ComponentActivity() {
    private val authController by lazy { AndroidHostedAuthController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HostedDevAuthScreen(authController)
                }
            }
        }
    }

    override fun onDestroy() {
        authController.close()
        super.onDestroy()
    }
}

@Composable
private fun HostedDevAuthScreen(authController: AndroidHostedAuthController) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var otpRequested by remember { mutableStateOf(false) }
    var hasSession by remember { mutableStateOf(authController.hasRememberedSession()) }
    var busy by remember { mutableStateOf(false) }
    var status by remember {
        mutableStateOf(
            if (hasSession) {
                "Se encontró una sesión recordada de Descope. Puedes probarla contra el Worker."
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Prueba DEV de autenticación alojada",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Esta pantalla existe solo en builds debug. Prueba una sesión real de Descope contra el Worker y Neon sin cambiar todavía el flujo normal del Player.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
        )

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
            text = "No se muestran ni registran JWT, refresh tokens ni credenciales de base de datos.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
