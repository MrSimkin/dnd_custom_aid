package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.dmui.DmKeyValueStore
import io.github.mrsimkin.dndcustomaid.dmui.handCursor
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAccount
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiErrorCode
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiException
import io.github.mrsimkin.dndcustomaid.shared.hosted.spanishSummary
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/** Where the App de PC stands with the account (owner decisions D2 and D4, 2026-11-13). */
internal enum class DesktopAccountGate {
    /** Reopening the remembered account. */
    STARTING,

    /** No account: the App asks for it before anything else. */
    SIGN_IN,

    /** An account is open (or remembered while offline). */
    READY,
}

/** The account's role in one campaign, as the App shows it. */
internal enum class DesktopCampaignAccess(val label: String) {
    DM("DM"),
    PLAYER("Jugador"),

    /** A campaign that exists only on this computer (made before accounts were required). */
    LOCAL_ONLY("Solo en este equipo"),
}

/**
 * One account for the whole App de PC: reopened at start, shown in the top bar, used by
 * «⟳ Sincronizar» and by every screen that depends on the role in the open campaign.
 */
internal class DesktopAccountSession(
    private val auth: DesktopHostedAuthController,
    private val hosted: DesktopHostedCampaignAdministrationController,
    private val store: DmKeyValueStore,
    private val now: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    var gate by mutableStateOf(DesktopAccountGate.STARTING)
        private set
    var email by mutableStateOf<String?>(null)
        private set
    var accountId by mutableStateOf(store.getString(LAST_ACCOUNT_KEY)?.let { runCatching { Uuid.parse(it) }.getOrNull() })
        private set
    var offline by mutableStateOf(false)
        private set
    var busy by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
    var lastSyncEpochSeconds by mutableStateOf(store.getString(LAST_SYNC_KEY)?.toLongOrNull())
        private set

    /** Bumped whenever campaigns or memberships may have changed, so lists reload. */
    var campaignsVersion by mutableStateOf(0)
        private set

    val canRemember: Boolean get() = auth.canRemember
    val pendingEmail: String? get() = auth.pendingLoginEmail()
    val codeSent: Boolean get() = auth.phase == DesktopHostedAuthPhase.OTP_SENT

    suspend fun start() {
        when (auth.restore()) {
            DesktopRestoreOutcome.NONE -> gate = DesktopAccountGate.SIGN_IN
            DesktopRestoreOutcome.EXPIRED -> {
                message = "Tu cuenta recordada venció. Vuelve a entrar."
                gate = DesktopAccountGate.SIGN_IN
            }
            DesktopRestoreOutcome.OFFLINE -> {
                email = auth.accountEmail
                offline = true
                gate = DesktopAccountGate.READY
            }
            DesktopRestoreOutcome.RESTORED -> {
                email = auth.accountEmail
                gate = DesktopAccountGate.READY
                refreshCampaigns()
            }
        }
    }

    suspend fun sendCode(address: String) = work {
        auth.sendEmailOtp(address)
        message = "Te enviamos un código a ${address.trim()}."
    }

    suspend fun verify(code: String) = work {
        auth.verifyEmailOtp(code)
        email = auth.accountEmail
        message = null
        gate = DesktopAccountGate.READY
        refreshCampaignsInside()
    }

    fun cancelCode() {
        auth.cancelPendingCode()
        message = null
    }

    /** Brings the campaigns and roles of this account from the server. */
    suspend fun refreshCampaigns() = work { refreshCampaignsInside() }

    /** «⟳ Sincronizar»: sends the DM content of this computer and receives the tablet's. */
    suspend fun synchronize() = work {
        val result = hosted.syncDmContent()
        offline = false
        lastSyncEpochSeconds = now().also { store.putString(LAST_SYNC_KEY, it.toString()) }
        message = result.spanishSummary()
        campaignsVersion++
    }

    /** «Nueva campaña»: created on the server with this account as its DM, then brought here. */
    suspend fun createCampaign(name: String): Boolean {
        var created = false
        work {
            hosted.createHostedCampaign(name)
            refreshCampaignsInside()
            message = "Campaña «${name.trim()}» creada. Eres su DM."
            created = true
        }
        return created
    }

    /** «Cerrar sesión» and «Cambiar usuario»: the App forgets the account and asks again. */
    suspend fun signOut(changingUser: Boolean) {
        auth.signOut()
        email = null
        accountId = null
        offline = false
        store.putString(LAST_ACCOUNT_KEY, null)
        message = if (changingUser) {
            "Entra con la otra cuenta. Tus campañas y datos de este equipo se conservan."
        } else {
            "Cerraste sesión. Tus campañas y datos de este equipo se conservan."
        }
        campaignsVersion++
        gate = DesktopAccountGate.SIGN_IN
    }

    fun access(campaignId: Uuid): DesktopCampaignAccess {
        val account = accountId ?: return DesktopCampaignAccess.LOCAL_ONLY
        val membership = hosted.membership(campaignId, account) ?: return DesktopCampaignAccess.LOCAL_ONLY
        return if (membership.role == CampaignRole.DM && membership.status == CampaignMembershipStatus.ACTIVE) {
            DesktopCampaignAccess.DM
        } else {
            DesktopCampaignAccess.PLAYER
        }
    }

    /** Short status for the top bar, next to «⟳ Sincronizar». */
    fun syncStatus(): String = when {
        busy -> "Sincronizando…"
        offline -> "Sin conexión: trabajas en este equipo"
        lastSyncEpochSeconds == null -> "Aún sin sincronizar"
        else -> "Sincronizado ${ago(now() - lastSyncEpochSeconds!!)}"
    }

    private suspend fun refreshCampaignsInside() {
        val result = hosted.bootstrap()
        accountId = result.account.id
        store.putString(LAST_ACCOUNT_KEY, result.account.id.toString())
        offline = false
        campaignsVersion++
    }

    private suspend fun work(block: suspend () -> Unit) {
        if (busy) return
        busy = true
        try {
            block()
        } catch (error: Throwable) {
            if (error.isNoConnection()) {
                offline = true
                message = "No hay conexión con el servidor. Puedes seguir trabajando en este equipo."
            } else {
                message = desktopHostedErrorMessage(error)
            }
            if (error is HostedApiException && error.code == HostedApiErrorCode.UNAUTHENTICATED) {
                signOut(changingUser = false)
                message = "Tu cuenta venció. Vuelve a entrar."
            }
        } finally {
            busy = false
        }
    }

    companion object {
        const val LAST_ACCOUNT_KEY = "account_id"
        const val LAST_SYNC_KEY = "account.lastSyncEpochSeconds"
    }
}

private fun Throwable.isNoConnection(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        if (current is java.io.IOException || current is java.nio.channels.UnresolvedAddressException) return true
        if (current is HostedApiException && current.code == HostedApiErrorCode.TRANSIENT_FAILURE) return true
        current = current.cause
    }
    return false
}

internal fun ago(seconds: Long): String = when {
    seconds < 60 -> "hace un momento"
    seconds < 3600 -> "hace ${seconds / 60} min"
    seconds < 86_400 -> "hace ${seconds / 3600} h"
    else -> "hace ${seconds / 86_400} d"
}

/** The start of the App de PC: reopens the account, or asks for it before anything else. */
@Composable
internal fun DesktopAccountGateScreen(
    session: DesktopAccountSession,
    onExit: () -> Unit,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(Unit) { if (session.gate == DesktopAccountGate.STARTING) session.start() }
    when (session.gate) {
        DesktopAccountGate.STARTING -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator()
                Text("Abriendo tu cuenta…")
            }
        }
        DesktopAccountGate.SIGN_IN -> DesktopSignInScreen(session = session, onExit = onExit)
        DesktopAccountGate.READY -> content()
    }
}

@Composable
internal fun DesktopSignInScreen(session: DesktopAccountSession, onExit: () -> Unit) {
    val scope = rememberCoroutineScope()
    var address by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Card(elevation = 4.dp, modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth()) {
            Column(Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("D&D Custom Aid", style = MaterialTheme.typography.overline)
                Text("Entra con tu cuenta", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                Text(
                    "Con tu cuenta, lo que preparas aquí llega a la tablet y la App sabe en qué campañas eres DM. " +
                        if (session.canRemember) "Este equipo la recuerda: solo la pides una vez." else "",
                    style = MaterialTheme.typography.body2,
                )
                session.message?.let { Text(it, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.primary) }

                if (!session.codeSent) {
                    TextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Tu correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            enabled = !session.busy && address.isNotBlank(),
                            onClick = { scope.launch { session.sendCode(address) } },
                        ) { Text(if (session.busy) "Enviando…" else "Enviar código") }
                        TextButton(onClick = onExit) { Text("Salir de la App") }
                    }
                } else {
                    Text("Escribe el código que llegó a ${session.pendingEmail ?: "tu correo"}.", style = MaterialTheme.typography.body2)
                    TextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Código") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            enabled = !session.busy && code.isNotBlank(),
                            onClick = { scope.launch { session.verify(code); code = "" } },
                        ) { Text(if (session.busy) "Entrando…" else "Entrar") }
                        TextButton(enabled = !session.busy, onClick = { session.cancelCode(); code = "" }) {
                            Text("Usar otro correo")
                        }
                    }
                }
            }
        }
    }
}

/** A small rounded label: the role in a campaign, or the account. */
@Composable
internal fun AccessChip(access: DesktopCampaignAccess) {
    val (background, foreground) = when (access) {
        DesktopCampaignAccess.DM -> Color(0xFF2E7D5B).copy(alpha = 0.18f) to Color(0xFF3FA57A)
        DesktopCampaignAccess.PLAYER -> Color(0xFF3D63B8).copy(alpha = 0.18f) to Color(0xFF6F93E0)
        DesktopCampaignAccess.LOCAL_ONLY -> MaterialTheme.colors.onSurface.copy(alpha = 0.10f) to MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
    }
    Text(
        access.label,
        color = foreground,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.caption,
        modifier = Modifier
            .background(background, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp),
    )
}

/**
 * The top bar of every workbench screen: the open campaign with your role, «⟳ Sincronizar»
 * with when it last ran, and your account with «Cambiar usuario» and «Cerrar sesión».
 */
@Composable
internal fun DesktopAccountBar(
    session: DesktopAccountSession,
    campaignName: String?,
    access: DesktopCampaignAccess?,
) {
    val scope = rememberCoroutineScope()
    var menuOpen by remember { mutableStateOf(false) }

    Surface(elevation = 4.dp, color = MaterialTheme.colors.primarySurface()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("D&D Custom Aid", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
            Text(
                campaignName?.let { "· $it" } ?: "· Elige una campaña",
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 360.dp),
            )
            access?.let { AccessChip(it) }
            Spacer(Modifier.weight(1f))
            OutlinedButton(
                enabled = !session.busy,
                onClick = { scope.launch { session.synchronize() } },
                modifier = Modifier.handCursor(),
            ) { Text("⟳ Sincronizar") }
            Text(session.syncStatus(), style = MaterialTheme.typography.caption)
            Box {
                OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.handCursor()) {
                    Text(
                        "● ${session.email ?: "Tu cuenta"} ▾",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 260.dp),
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    Text(
                        "Entraste como ${session.email ?: "tu cuenta"}",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                    DropdownMenuItem(onClick = {
                        menuOpen = false
                        scope.launch { session.signOut(changingUser = true) }
                    }) { Text("Cambiar usuario") }
                    DropdownMenuItem(onClick = {
                        menuOpen = false
                        scope.launch { session.signOut(changingUser = false) }
                    }) { Text("Cerrar sesión") }
                }
            }
        }
    }
}

/** Explains, in place, why a DM-only screen does not open with this account. */
@Composable
internal fun NotDmNotice(
    campaignName: String,
    session: DesktopAccountSession,
    what: String,
) {
    val scope = rememberCoroutineScope()
    Card(elevation = 2.dp, modifier = Modifier.widthIn(max = 720.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("$what no se abre con esta cuenta", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
            Text(
                "En «$campaignName» eres Jugador con ${session.email ?: "esta cuenta"}. " +
                    "Para prepararla o dirigirla, entra con la cuenta que es DM de esa campaña.",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { scope.launch { session.signOut(changingUser = true) } }) { Text("Cambiar usuario") }
                TextButton(onClick = { scope.launch { session.refreshCampaigns() } }) { Text("Volver a comprobar") }
            }
        }
    }
}

/** The account's messages (sync result, no connection…) under the top bar. */
@Composable
internal fun DesktopAccountMessage(session: DesktopAccountSession) {
    val text = session.message ?: return
    Surface(color = MaterialTheme.colors.secondary.copy(alpha = 0.12f), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, style = MaterialTheme.typography.body2, modifier = Modifier.weight(1f))
            TextButton(onClick = { session.message = null }) { Text("Cerrar") }
        }
    }
}

@Composable
private fun androidx.compose.material.Colors.primarySurface(): Color =
    if (isLight) primary.copy(alpha = 0.10f).compositeOverSurface(surface) else surface

private fun Color.compositeOverSurface(surface: Color): Color = Color(
    red = red * alpha + surface.red * (1 - alpha),
    green = green * alpha + surface.green * (1 - alpha),
    blue = blue * alpha + surface.blue * (1 - alpha),
    alpha = 1f,
)
