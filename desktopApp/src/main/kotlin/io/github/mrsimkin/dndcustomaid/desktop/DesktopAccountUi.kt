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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSyncProgressText
import io.github.mrsimkin.dndcustomaid.shared.hosted.fraction
import androidx.compose.material3.LinearProgressIndicator
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

/**
 * The open account's access to a campaign. A campaign with nobody on the server is local; one that
 * has members but not this account belongs to another account (QA 0.7.0-preqa.2, A2).
 */
internal fun desktopCampaignAccess(accountId: Uuid?, memberships: List<io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership>): DesktopCampaignAccess {
    if (memberships.isEmpty()) return DesktopCampaignAccess.LOCAL_ONLY
    val mine = accountId?.let { id -> memberships.firstOrNull { it.accountId == id } } ?: return DesktopCampaignAccess.NOT_MEMBER
    return if (mine.role == CampaignRole.DM && mine.status == CampaignMembershipStatus.ACTIVE) DesktopCampaignAccess.DM else DesktopCampaignAccess.PLAYER
}

/** The account's role in one campaign, as the App shows it. */
internal enum class DesktopCampaignAccess(val label: String) {
    DM("DM"),
    PLAYER("Jugador"),

    /** A campaign that exists only on this computer (made before accounts were required). */
    LOCAL_ONLY("Solo en este equipo"),

    /** QA 0.7.0-preqa.2 (A2): a campaign on the server where the open account is not a member. */
    NOT_MEMBER("No eres parte"),
    ;

    /** Escritorio DM, Preparar and the player list are only for the campaign's DM (or local campaigns). */
    val blocksDm: Boolean get() = this == PLAYER || this == NOT_MEMBER
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

    /** While «⟳ Sincronizar» runs: how much is done («Enviando 535 de 1.090 · faltan unos 7 min»). */
    var syncProgress by mutableStateOf<String?>(null)
        private set

    /** The same progress as a fraction for the bar, or null when there is nothing to count yet. */
    var syncFraction by mutableStateOf<Float?>(null)
        private set

    /** Bumped whenever campaigns or memberships may have changed, so lists reload. */
    var campaignsVersion by mutableStateOf(0)
        private set

    init {
        // QA 0.7.0-preqa.2 (M4): a refused account sends the App back to «Entra con tu cuenta» with
        // a clear reason, instead of failing every sync with a generic message.
        auth.onAccountExpired = {
            email = null
            message = "Tu cuenta venció en este equipo. Vuelve a entrar; lo que preparaste sigue aquí."
            gate = DesktopAccountGate.SIGN_IN
        }
    }

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

    /**
     * Refactor F5 (AGENTS §6.4): only when the App runs on a test data folder, the agent walks it
     * without the owner's account. Everything stays on this computer, as «Solo en este equipo».
     */
    fun continueWithoutAccountForTesting() {
        check(io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory.usesTestDataDirectory()) {
            "Only a test data folder can be opened without an account."
        }
        email = null
        message = null
        gate = DesktopAccountGate.READY
    }

    fun cancelCode() {
        auth.cancelPendingCode()
        message = null
    }

    /** Brings the campaigns and roles of this account from the server. */
    suspend fun refreshCampaigns() = work { refreshCampaignsInside() }

    /** «⟳ Sincronizar»: sends the DM content of this computer and receives the tablet's. */
    suspend fun synchronize() = work {
        val texts = DmContentSyncProgressText { System.currentTimeMillis() }
        val result = try {
            hosted.syncDmContent { progress ->
                syncProgress = texts.text(progress)
                syncFraction = progress.fraction
            }
        } finally {
            syncProgress = null
            syncFraction = null
        }
        offline = false
        lastSyncEpochSeconds = now().also { store.putString(LAST_SYNC_KEY, it.toString()) }
        message = result.spanishSummary()
        campaignsVersion++
    }

    /** «Nueva campaña»: created on the server with this account as its DM, then brought here. */
    suspend fun createCampaign(name: String): Boolean {
        var created = false
        if (email == null && io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory.usesTestDataDirectory()) {
            hosted.createLocalCampaignForTesting(name)
            campaignsVersion++
            message = "Campaña «${name.trim()}» creada solo en este equipo (datos de prueba)."
            return true
        }
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

    fun access(campaignId: Uuid): DesktopCampaignAccess =
        desktopCampaignAccess(accountId, hosted.memberships(campaignId))

    /** Short status for the top bar, next to «⟳ Sincronizar». */
    fun syncStatus(): String = when {
        busy -> syncProgress ?: "Sincronizando…"
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
        Card(elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp), modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth()) {
            Column(Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("D&D Custom Aid", style = MaterialTheme.typography.labelSmall)
                Text("Entra con tu cuenta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Con tu cuenta, lo que preparas aquí llega a la tablet y la App sabe en qué campañas eres DM. " +
                        if (session.canRemember) "Este equipo la recuerda: solo la pides una vez." else "",
                    style = MaterialTheme.typography.bodyMedium,
                )
                session.message?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary) }

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
                    if (io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory.usesTestDataDirectory()) {
                        TextButton(onClick = { session.continueWithoutAccountForTesting() }) {
                            Text("Seguir sin cuenta (datos de prueba)")
                        }
                    }
                } else {
                    Text("Escribe el código que llegó a ${session.pendingEmail ?: "tu correo"}.", style = MaterialTheme.typography.bodyMedium)
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
        DesktopCampaignAccess.LOCAL_ONLY -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f) to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        DesktopCampaignAccess.NOT_MEMBER -> Color(0xFFB3342B).copy(alpha = 0.16f) to Color(0xFFE07A70)
    }
    Text(
        access.label,
        color = foreground,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodySmall,
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

    Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.primaryContainer) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("D&D Custom Aid", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                campaignName?.let { "· $it" } ?: "· Elige una campaña",
                style = MaterialTheme.typography.titleMedium,
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(session.syncStatus(), style = MaterialTheme.typography.bodySmall)
                session.syncFraction?.let { LinearProgressIndicator(progress = it, modifier = Modifier.width(220.dp)) }
            }
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
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                    DropdownMenuItem(text = { Text("Cambiar usuario") }, onClick = {
                        menuOpen = false
                        scope.launch { session.signOut(changingUser = true) }
                    })
                    DropdownMenuItem(text = { Text("Cerrar sesión") }, onClick = {
                        menuOpen = false
                        scope.launch { session.signOut(changingUser = false) }
                    })
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
    access: DesktopCampaignAccess = DesktopCampaignAccess.PLAYER,
) {
    val scope = rememberCoroutineScope()
    Card(elevation = androidx.compose.material3.CardDefaults.cardElevation(2.dp), modifier = Modifier.widthIn(max = 720.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("$what no se abre con esta cuenta", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                if (access == DesktopCampaignAccess.NOT_MEMBER) {
                    "Con ${session.email ?: "esta cuenta"} no eres parte de «$campaignName»: la campaña es de otra cuenta. " +
                        "Lo que está en este equipo no se muestra ni se cambia con esta cuenta. Para prepararla o dirigirla, entra con la cuenta que es DM."
                } else {
                    "En «$campaignName» eres Jugador con ${session.email ?: "esta cuenta"}. " +
                        "Para prepararla o dirigirla, entra con la cuenta que es DM de esa campaña."
                },
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
    Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = { session.message = null }) { Text("Cerrar") }
        }
    }
}


