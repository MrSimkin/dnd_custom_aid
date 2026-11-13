package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.mrsimkin.dndcustomaid.dmui.LocalDmPlatform
import io.github.mrsimkin.dndcustomaid.dmui.RulesLibraryScreen
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/** «Reglas» on the tablet (redesign R4): the SRD to read and the DM's homebrew, as on Windows. */
@Composable
internal fun AndroidRulesLibraryScreen(
    database: AppDatabase,
    hostedBootstrap: AndroidHostedCampaignBootstrapController,
    accountId: Uuid?,
    campaignId: Uuid?,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val platform = remember { AndroidDmPlatform(context, hostedBootstrap) }
    CompositionLocalProvider(LocalDmPlatform provides platform) {
        RulesLibraryScreen(
            platform = platform,
            database = database,
            ownerAccountId = accountId,
            campaignId = campaignId,
            onBack = onBack,
        )
    }
}
