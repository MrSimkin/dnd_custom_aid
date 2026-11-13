package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import io.github.mrsimkin.dndcustomaid.dmui.DmKeyValueStore
import io.github.mrsimkin.dndcustomaid.dmui.DmPlatform
import io.github.mrsimkin.dndcustomaid.dmui.DmSyncResult
import io.github.mrsimkin.dndcustomaid.dmui.DmWorkspaceScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRulesPassage
import io.github.mrsimkin.dndcustomaid.shared.hosted.spanishSummary
import java.io.File
import java.util.Properties
import kotlin.uuid.Uuid

/*
 * Base para DM on Windows: the same Escritorio, Pantalla del DM and Mesas as the Android tablet
 * (shared `dmUi` module). This file supplies what is the desktop's own: a small settings file next
 * to the database, the SRD bundled as resources and the hosted sync of the App de PC.
 */

/** Per-device DM settings in `dm_workspace.properties`, next to the local database. */
internal class DesktopDmKeyValueStore(private val file: File) : DmKeyValueStore {
    private val values = Properties().apply {
        if (file.isFile) file.inputStream().use { load(it) }
    }

    @Synchronized
    override fun getString(key: String): String? = values.getProperty(key)

    @Synchronized
    override fun putString(key: String, value: String?) {
        if (value == null) values.remove(key) else values.setProperty(key, value)
        file.parentFile?.mkdirs()
        file.outputStream().use { values.store(it, "D&D Custom Aid · Escritorio DM") }
    }
}

internal class DesktopDmPlatform(
    override val store: DmKeyValueStore,
    /** Package 8: the hosted rules answer; null in tests and without a server connection. */
    private val rules: (suspend (String, List<HostedRulesPassage>) -> String)? = null,
) : DmPlatform {
    override val rulesAiAvailable: Boolean
        get() = rules != null

    override suspend fun answerRules(question: String, passages: List<HostedRulesPassage>): String =
        requireNotNull(rules) { "Rules answers are not available." }(question, passages)

    override fun readSrd(fileName: String): String =
        checkNotNull(DesktopDmPlatform::class.java.getResourceAsStream("/srd/$fileName")) { "SRD $fileName no está en la App." }
            .bufferedReader(Charsets.UTF_8).use { it.readText() }

    override fun decodeImage(bytes: ByteArray, maxSide: Int): ImageBitmap? =
        runCatching { Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()

    // The desktop has no dice setting yet; it shows the compact result.
    override val visibleDice: Boolean = false

    // A PC has no system back gesture: «← Atrás», Esc and Alt+←, scroll bars and the hand cursor.
    override val isDesktop: Boolean = true

    override val exitLabel: String = "← Mesa de trabajo"
}

/**
 * One palette across the Windows app (owner request 2026-11-12): the DM desks take the theme and
 * the font chosen in Configuración, the same ones the workbench (Material 2) already uses, instead
 * of Material 3's own defaults. Container colors are blends of the theme's own colors.
 */
@Composable
internal fun desktopDmColorScheme(): ColorScheme {
    val c = androidx.compose.material.MaterialTheme.colors
    val containerOf = { tint: Color -> lerp(c.surface, tint, if (c.isLight) 0.18f else 0.30f) }
    val surfaceVariant = lerp(c.surface, c.onSurface, 0.10f)
    val base = if (c.isLight) lightColorScheme() else darkColorScheme()
    return base.copy(
        primary = c.primary,
        onPrimary = c.onPrimary,
        primaryContainer = containerOf(c.primary),
        onPrimaryContainer = c.onSurface,
        inversePrimary = c.primaryVariant,
        secondary = c.secondary,
        onSecondary = c.onSecondary,
        secondaryContainer = containerOf(c.secondary),
        onSecondaryContainer = c.onSurface,
        tertiary = c.secondaryVariant,
        background = c.background,
        onBackground = c.onBackground,
        surface = c.surface,
        onSurface = c.onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = c.onSurface.copy(alpha = 0.74f),
        surfaceTint = c.primary,
        surfaceContainerLowest = c.background,
        surfaceContainerLow = lerp(c.background, c.surface, 0.5f),
        surfaceContainer = c.surface,
        surfaceContainerHigh = lerp(c.surface, c.onSurface, 0.05f),
        surfaceContainerHighest = surfaceVariant,
        error = c.error,
        onError = c.onError,
        errorContainer = containerOf(c.error),
        onErrorContainer = c.onSurface,
        outline = c.onSurface.copy(alpha = 0.42f),
        outlineVariant = c.onSurface.copy(alpha = 0.18f),
    )
}

/** The same font family as the workbench, on every Material 3 text style. */
@Composable
internal fun desktopDmTypography(): Typography {
    val family = androidx.compose.material.MaterialTheme.typography.body1.fontFamily
    val t = Typography()
    if (family == null) return t
    fun TextStyle.f() = copy(fontFamily = family)
    return t.copy(
        displayLarge = t.displayLarge.f(), displayMedium = t.displayMedium.f(), displaySmall = t.displaySmall.f(),
        headlineLarge = t.headlineLarge.f(), headlineMedium = t.headlineMedium.f(), headlineSmall = t.headlineSmall.f(),
        titleLarge = t.titleLarge.f(), titleMedium = t.titleMedium.f(), titleSmall = t.titleSmall.f(),
        bodyLarge = t.bodyLarge.f(), bodyMedium = t.bodyMedium.f(), bodySmall = t.bodySmall.f(),
        labelLarge = t.labelLarge.f(), labelMedium = t.labelMedium.f(), labelSmall = t.labelSmall.f(),
    )
}

@Composable
internal fun DesktopDmWorkspaceScreen(
    campaign: Campaign,
    database: AppDatabase,
    mediaBlobs: MediaBlobStore,
    platform: DesktopDmPlatform,
    hostedController: DesktopHostedCampaignAdministrationController,
    onExit: () -> Unit,
    onOpenAppSettings: () -> Unit,
) {
    var accountId by remember { mutableStateOf(platform.store.getString("account_id")?.let { runCatching { Uuid.parse(it) }.getOrNull() }) }
    // The workbench uses Material 2; the shared desks use Material 3 with the very same theme and font.
    MaterialTheme(colorScheme = desktopDmColorScheme(), typography = desktopDmTypography()) {
        DmWorkspaceScreen(
            campaign = campaign,
            database = database,
            mediaBlobs = mediaBlobs,
            platform = platform,
            accountId = accountId,
            onSync = {
                runCatching {
                    val account = hostedController.bootstrap().account
                    accountId = account.id
                    platform.store.putString("account_id", account.id.toString())
                    hostedController.syncDmContent()
                }.fold(
                    onSuccess = { result ->
                        val attention = result.offline || result.conflicts.isNotEmpty() || result.mediaMissingCount > 0
                        DmSyncResult(synced = !result.offline, message = result.spanishSummary().takeIf { attention })
                    },
                    onFailure = { DmSyncResult(synced = false, message = "No se pudo sincronizar: no hay conexión con el servidor o tu cuenta venció. Lo preparado sigue en este equipo.") },
                )
            },
            onExitToCampaigns = onExit,
            onOpenAppSettings = onOpenAppSettings,
        )
    }
}
