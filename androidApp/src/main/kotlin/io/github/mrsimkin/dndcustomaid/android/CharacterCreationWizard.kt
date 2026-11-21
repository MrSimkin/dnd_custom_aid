package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.mrsimkin.dndcustomaid.dmui.NewCharacterSteps
import io.github.mrsimkin.dndcustomaid.dmui.rememberNewCharacterState
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.dm.CharacterBuild
import io.github.mrsimkin.dndcustomaid.shared.dm.SRD_RULES_FILE
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdRules
import io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

private object AndroidSrdRules {
    private var rules: SrdRules? = null
    suspend fun load(context: android.content.Context): SrdRules = rules ?: withContext(Dispatchers.IO) {
        runCatching { parseSrdRules(context.assets.open("srd/$SRD_RULES_FILE").bufferedReader(Charsets.UTF_8).use { it.readText() }) }
            .getOrDefault(SrdRules()).also { rules = it }
    }
}

/**
 * QA 0.7.0-preqa.2 (J1): a new PC step by step. The steps live in `dmUi` so Windows offers the same
 * ones (owner, 2026-11-18); here they go inside the tablet's keyboard-safe dialog.
 */
@Composable
internal fun CharacterCreationWizard(
    campaigns: List<Campaign>,
    defaultCampaignId: Uuid,
    onDismiss: () -> Unit,
    onCreate: (Uuid, String, CharacterBuild?) -> Unit,
) {
    val context = LocalContext.current
    val state = rememberNewCharacterState(campaigns, defaultCampaignId) { AndroidSrdRules.load(context) }

    CharacterImeSafeEditorDialog(
        title = "Nuevo personaje",
        supportingText = "Paso a paso desde el SRD o tu homebrew. Todo se puede cambiar después en la hoja.",
        onCancel = onDismiss,
        onSave = { onCreate(state.campaign.id, state.name.trim(), state.buildOrNull()) },
        saveLabel = "Crear personaje",
        saveEnabled = state.ready,
        expanded = true,
    ) {
        NewCharacterSteps(state)
    }
}
