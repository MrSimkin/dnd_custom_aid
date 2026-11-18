package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mrsimkin.dndcustomaid.dmui.DmPlatform
import io.github.mrsimkin.dndcustomaid.dmui.LocalRulesSources
import io.github.mrsimkin.dndcustomaid.dmui.NewCharacterSteps
import io.github.mrsimkin.dndcustomaid.dmui.RulesSources
import io.github.mrsimkin.dndcustomaid.dmui.SrdCatalogAssets
import io.github.mrsimkin.dndcustomaid.dmui.rememberNewCharacterState
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.CharacterBuild
import io.github.mrsimkin.dndcustomaid.shared.dm.SRD_RULES_FILE
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdRules
import io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

/**
 * «＋ Nuevo personaje» on Windows (owner, 2026-11-18). The steps are the tablet's, from `dmUi`;
 * this file is only the window around them and where the SRD and the homebrew come from.
 */
@Composable
internal fun DesktopNewCharacterDialog(
    campaigns: List<Campaign>,
    database: AppDatabase,
    platform: DmPlatform,
    accountId: Uuid?,
    onDismiss: () -> Unit,
    onCreate: (Uuid, String, CharacterBuild?) -> Unit,
) {
    val sources = remember(database, platform, accountId) {
        RulesSources(
            loadCatalogs = { SrdCatalogAssets.catalogs(platform) },
            homebrew = {
                val repository = RulesEntryContentRepository(database)
                val personal = accountId?.let { runCatching { repository.listPersonal(it) }.getOrDefault(emptyList()) }.orEmpty()
                val campaign = campaigns.firstOrNull()?.id?.let { runCatching { repository.listCampaign(it) }.getOrDefault(emptyList()) }.orEmpty()
                (personal + campaign).distinctBy { it.item.identity.id }
            },
        )
    }
    val state = rememberNewCharacterState(campaigns, campaigns.first().id) { DesktopSrdRules.load(platform) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = MaterialTheme.shapes.large, tonalElevation = 4.dp, modifier = Modifier.widthIn(max = 760.dp).padding(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Nuevo personaje", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Paso a paso desde el SRD o tu homebrew. Todo se puede cambiar después en la hoja.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Box(Modifier.weight(1f, fill = false)) {
                    Column(
                        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        CompositionLocalProvider(LocalRulesSources provides sources) {
                            NewCharacterSteps(state)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(enabled = state.ready, onClick = { onCreate(state.campaign.id, state.name.trim(), state.buildOrNull()) }) {
                        Text("Crear personaje")
                    }
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    if (!state.ready) Text("Ponle un nombre para poder crearlo.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/** Reads the SRD rules file once per run of the App. */
private object DesktopSrdRules {
    private var rules: SrdRules? = null

    suspend fun load(platform: DmPlatform): SrdRules = rules ?: withContext(Dispatchers.IO) {
        runCatching { parseSrdRules(platform.readSrd(SRD_RULES_FILE)) }.getOrDefault(SrdRules()).also { rules = it }
    }
}

