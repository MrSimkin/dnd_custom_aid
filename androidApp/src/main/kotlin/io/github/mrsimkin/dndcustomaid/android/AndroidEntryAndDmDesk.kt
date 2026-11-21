package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.spanishSummary
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/*
 * Base para DM, package 2: one App with one entry (P7, P25 «A · Opcional», V11-QA-006).
 *
 * The entry offers email + code or «Seguir sin cuenta». With an account the App opens «Tus
 * campañas»; each campaign shows the account's role and opens the Escritorio DM or the PCs.
 */

/** Small per-device memory of the entry choice and the last place opened. */
internal class AppEntryStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("dnd_custom_aid_entry", Context.MODE_PRIVATE)

    var localOnlyChosen: Boolean
        get() = prefs.getBoolean(KEY_LOCAL_ONLY, false)
        set(value) = prefs.edit().putBoolean(KEY_LOCAL_ONLY, value).apply()

    var accountId: Uuid?
        get() = prefs.getString(KEY_ACCOUNT, null)?.let { runCatching { Uuid.parse(it) }.getOrNull() }
        set(value) = prefs.edit().putString(KEY_ACCOUNT, value?.toString()).apply()

    /** The email typed at sign-in, shown as «Entraste como…» (never sent anywhere else). */
    var accountEmail: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    /** Last DM desk opened, so the App reopens it; null means the player area or none. */
    var lastDmCampaignId: Uuid?
        get() = prefs.getString(KEY_LAST_DM, null)?.let { runCatching { Uuid.parse(it) }.getOrNull() }
        set(value) = prefs.edit().putString(KEY_LAST_DM, value?.toString()).apply()

    fun clearAccount() {
        prefs.edit().remove(KEY_ACCOUNT).remove(KEY_EMAIL).remove(KEY_LAST_DM).putBoolean(KEY_LOCAL_ONLY, false).apply()
    }

    private companion object {
        const val KEY_LOCAL_ONLY = "local_only_chosen"
        const val KEY_ACCOUNT = "hosted_account_id"
        const val KEY_LAST_DM = "last_dm_campaign_id"
        const val KEY_EMAIL = "hosted_account_email"
    }
}

/** The role of the signed-in account in a campaign, or null when it has no active membership. */
internal fun campaignRoleFor(database: AppDatabase, campaignId: Uuid, accountId: Uuid?): CampaignRole? {
    if (accountId == null) return null
    val membership = IntegratedSpineRepository(database).membership(campaignId, accountId) ?: return null
    return membership.role.takeIf { membership.status == CampaignMembershipStatus.ACTIVE }
}

@Composable
internal fun AppEntryScreen(
    authController: AndroidHostedAuthController,
    onSignedIn: suspend (email: String) -> Unit,
    onContinueWithoutAccount: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var codeSentTo by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun run(action: suspend () -> Unit) {
        if (busy) return
        busy = true
        error = null
        scope.launch {
            try {
                action()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                error = if (codeSentTo == null) {
                    "No se pudo enviar el código. Revisa el correo y la conexión, y vuelve a intentarlo."
                } else {
                    "El código no sirvió o venció. Revísalo o pide uno nuevo."
                }
            } finally {
                busy = false
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(12.dp)),
        ) {
            Text(
                text = "D&D Custom Aid",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Para jugadores y DM",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            CharacterCompactOutlinedTextFieldV4(
                value = email,
                onValueChange = {
                    email = it
                    codeSentTo = null
                    code = ""
                },
                label = { Text("Tu correo") },
                singleLine = true,
                enabled = !busy,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )

            if (codeSentTo == null) {
                Button(
                    onClick = { run { codeSentTo = authController.sendEmailOtp(email) } },
                    enabled = !busy && email.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (busy) "Enviando…" else "Recibir código")
                }
            } else {
                Text(
                    text = "Te enviamos un código a ${codeSentTo}.",
                    style = MaterialTheme.typography.bodySmall,
                )
                CharacterCompactOutlinedTextFieldV4(
                    value = code,
                    onValueChange = { code = it.filter(Char::isDigit).take(8) },
                    label = { Text("Código") },
                    singleLine = true,
                    enabled = !busy,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        run {
                            authController.verifyEmailOtp(email, code)
                            onSignedIn(email.trim())
                        }
                    },
                    enabled = !busy && code.length >= 4,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (busy) "Entrando…" else "Entrar")
                }
                TextButton(
                    onClick = {
                        codeSentTo = null
                        code = ""
                    },
                    enabled = !busy,
                ) {
                    Text("Pedir otro código")
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            HorizontalDivider()

            OutlinedButton(
                onClick = onContinueWithoutAccount,
                enabled = !busy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Seguir sin cuenta")
            }
            Text(
                text = "Tus PJ quedan solo en este aparato. Puedes entrar con tu cuenta después, desde Campañas.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    }
}

@Composable
internal fun CampaignRoleBadge(role: CampaignRole) {
    val (label, container, content) = when (role) {
        CampaignRole.DM -> Triple("DM", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        CampaignRole.PLAYER -> Triple("Jugador", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    }
    Surface(color = container, contentColor = content, shape = MaterialTheme.shapes.small) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

internal fun ReusableContentFamily.spanishPlural(): String = when (this) {
    ReusableContentFamily.CREATURE -> "Criaturas"
    ReusableContentFamily.NPC -> "PNJ"
    ReusableContentFamily.HOMEBREW_RULE -> "Reglas caseras"
    ReusableContentFamily.PLACE -> "Lugares"
    ReusableContentFamily.ZONE -> "Zonas"
    ReusableContentFamily.ENCOUNTER -> "Encuentros"
    ReusableContentFamily.SCENE -> "Escenas"
    ReusableContentFamily.HANDOUT -> "Handouts e imágenes"
    ReusableContentFamily.PACKAGE -> "Paquetes"
    ReusableContentFamily.NOTE -> "Notas"
    ReusableContentFamily.DUNGEON -> "Dungeons"
    ReusableContentFamily.RULES_ENTRY -> "Reglas homebrew"
}
