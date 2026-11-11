package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.MaterialTheme
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

internal class DesktopDmPlatform(override val store: DmKeyValueStore) : DmPlatform {
    override fun readSrd(fileName: String): String =
        checkNotNull(DesktopDmPlatform::class.java.getResourceAsStream("/srd/$fileName")) { "SRD $fileName no está en la App." }
            .bufferedReader(Charsets.UTF_8).use { it.readText() }

    override fun decodeImage(bytes: ByteArray, maxSide: Int): ImageBitmap? =
        runCatching { Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()

    // The desktop has no dice setting yet; it shows the compact result.
    override val visibleDice: Boolean = false
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
    // The desktop workbench uses Material 2; the shared desks use Material 3 in the same light or dark.
    val dark = !androidx.compose.material.MaterialTheme.colors.isLight
    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
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
                    onFailure = { DmSyncResult(synced = false, message = "No se pudo sincronizar. Revisa que entraste con tu cuenta en «Administración de campaña».") },
                )
            },
            onExitToCampaigns = onExit,
            onOpenAppSettings = onOpenAppSettings,
        )
    }
}
