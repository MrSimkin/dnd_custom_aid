package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.mrsimkin.dndcustomaid.dmui.DmKeyValueStore
import io.github.mrsimkin.dndcustomaid.dmui.DmPlatform
import io.github.mrsimkin.dndcustomaid.dmui.DmSyncResult
import io.github.mrsimkin.dndcustomaid.dmui.DmWorkspaceScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResolvedD20Roll
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRulesPassage
import kotlin.uuid.Uuid

/*
 * Base para DM on the Android tablet. The desks themselves live in the shared `dmUi` module (the
 * same ones the Windows app shows); this file supplies what is Android's own: settings storage, the
 * bundled SRD assets, the PC sheet's dice cards and the hosted sync.
 */

internal class AndroidDmPlatform(context: Context, private val hostedBootstrap: AndroidHostedCampaignBootstrapController) : DmPlatform {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("dnd_custom_aid_dm_workspace", Context.MODE_PRIVATE)

    override val store = object : DmKeyValueStore {
        override fun getString(key: String): String? = prefs.getString(key, null)

        override fun putString(key: String, value: String?) {
            prefs.edit().apply { if (value == null) remove(key) else putString(key, value) }.apply()
        }
    }

    override fun readSrd(fileName: String): String =
        appContext.assets.open("srd/$fileName").bufferedReader(Charsets.UTF_8).use { it.readText() }

    /** Sampled down so a thumbnail never holds a full-size bitmap. */
    override fun decodeImage(bytes: ByteArray, maxSide: Int): ImageBitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })?.asImageBitmap()
    }

    override val visibleDice: Boolean
        get() = UiPreferencesStore(appContext).load().diceResultMode == DiceResultModeChoice.VISIBLE_DICE

    /** Package 8: only with a signed-in hosted session; otherwise the offline search stands alone. */
    override val rulesAiAvailable: Boolean
        get() = hostedBootstrap.hasRememberedSession()

    override suspend fun answerRules(question: String, passages: List<HostedRulesPassage>): String =
        hostedBootstrap.answerRules(question, passages)

    @Composable
    override fun D20Result(roll: CharacterResolvedD20Roll) {
        if (visibleDice) CharacterVisibleDiceResultCardV4(roll) else CharacterD20ResultCardV4(roll)
    }
}

@Composable
internal fun AndroidDmWorkspaceScreen(
    campaign: Campaign,
    database: AppDatabase,
    mediaBlobs: MediaBlobStore,
    hostedBootstrap: AndroidHostedCampaignBootstrapController,
    accountId: Uuid?,
    onExitToCampaigns: () -> Unit,
    onOpenAppSettings: () -> Unit,
) {
    val context = LocalContext.current
    val platform = remember { AndroidDmPlatform(context, hostedBootstrap) }
    DmWorkspaceScreen(
        campaign = campaign,
        database = database,
        mediaBlobs = mediaBlobs,
        platform = platform,
        accountId = accountId,
        onSync = {
            when (val outcome = hostedBootstrap.refresh()) {
                is AndroidHostedCampaignBootstrapOutcome.Success ->
                    DmSyncResult(synced = true, message = outcome.dmContentError?.let { "El contenido del DM quedó guardado para reintentar." })
                AndroidHostedCampaignBootstrapOutcome.NoRememberedSession -> DmSyncResult(false, "Entra con tu cuenta para sincronizar.")
                is AndroidHostedCampaignBootstrapOutcome.Failure -> DmSyncResult(false, outcome.message)
            }
        },
        onExitToCampaigns = onExitToCampaigns,
        onOpenAppSettings = onOpenAppSettings,
    )
}
