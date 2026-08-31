package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSavingThrow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.abilityModifierForScore
import io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

private enum class CharacterTabV4(val label: String) {
    OVERVIEW("General"),
    SKILLS("Habilidades"),
    COMBAT("Combate"),
    EQUIPMENT("Equipo"),
}

private val classNamesV4 = listOf(
    "Artífice",
    "Bárbaro",
    "Bardo",
    "Brujo",
    "Clérigo",
    "Druida",
    "Explorador",
    "Guerrero",
    "Hechicero",
    "Mago",
    "Monje",
    "Paladín",
    "Pícaro",
)

@Composable
internal fun CharacterEditorScreenV4(
    characterId: Uuid,
    repository: CharacterRepository,
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
) {
    var stored by remember(characterId) {
        mutableStateOf(requireNotNull(repository.character(characterId)))
    }
    var draft by rememberSaveable(
        characterId.toString(),
        stateSaver = CharacterEditorDraftV4.Saver,
    ) {
        mutableStateOf(CharacterEditorDraftV4.from(stored))
    }
    var combatDraftJson by rememberSaveable(characterId.toString()) {
        mutableStateOf(combatEntriesToJsonV4(stored.combatEntries))
    }
    var equipmentDraftJson by rememberSaveable(characterId.toString()) {
        mutableStateOf(
            equipmentDraftToJsonV4(
                CharacterEquipmentDraftV4(
                    items = stored.inventoryItems,
                    currencies = stored.currencies,
                ),
            ),
        )
    }
    var savedMessage by rememberSaveable(characterId.toString()) { mutableStateOf<String?>(null) }
    var selectedTabName by rememberSaveable(characterId.toString()) {
        mutableStateOf(CharacterTabV4.OVERVIEW.name)
    }
    var confirmBlankNumbers by rememberSaveable(characterId.toString()) { mutableStateOf(false) }
    val selectedTab = runCatching { CharacterTabV4.valueOf(selectedTabName) }
        .getOrDefault(CharacterTabV4.OVERVIEW)
    val combatEntries = remember(combatDraftJson) { combatEntriesFromJsonV4(combatDraftJson) }
    val equipmentDraft = remember(equipmentDraftJson) { equipmentDraftFromJsonV4(equipmentDraftJson) }
    val savable = draft.toSheetOrNull(stored, blankRequiredAsZero = true) != null

    fun updateDraft(updated: CharacterEditorDraftV4) {
        draft = updated
        savedMessage = null
    }

    fun updateCombatEntries(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry>) {
        combatDraftJson = combatEntriesToJsonV4(updated)
        savedMessage = null
    }

    fun updateEquipmentItems(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem>) {
        equipmentDraftJson = equipmentDraftToJsonV4(equipmentDraft.copy(items = updated))
        savedMessage = null
    }

    fun updateCurrencies(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency>) {
        equipmentDraftJson = equipmentDraftToJsonV4(equipmentDraft.copy(currencies = updated))
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {
        val equipment = equipmentDraftFromJsonV4(equipmentDraftJson)
        val integrated = candidate.copy(
            combatEntries = combatEntriesFromJsonV4(combatDraftJson),
            inventoryItems = equipment.items,
            currencies = equipment.currencies,
        )
        stored = repository.saveCharacter(integrated)
        draft = CharacterEditorDraftV4.from(stored)
        combatDraftJson = combatEntriesToJsonV4(stored.combatEntries)
        equipmentDraftJson = equipmentDraftToJsonV4(
            CharacterEquipmentDraftV4(
                items = stored.inventoryItems,
                currencies = stored.currencies,
            ),
        )
        savedMessage = "Guardado"
    }

    fun save() {
        if (draft.missingRequiredNumberLabels().isNotEmpty()) {
            confirmBlankNumbers = true
            return
        }
        val candidate = draft.toSheetOrNull(stored) ?: return
        persist(candidate)
    }

    fun saveBlankNumbersAsZero() {
        val candidate = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: return
        confirmBlankNumbers = false
        persist(candidate)
    }

    Scaffold { scaffoldPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
        ) {
            val wide = maxWidth >= 700.dp
            Column(modifier = Modifier.fillMaxSize()) {
                EditorHeaderV4(
                    characterName = draft.name,
                    stored = stored,
                    savedMessage = savedMessage,
                    savable = savable,
                    onBack = onBack,
                    onSave = ::save,
                    onOpenSettings = onOpenSettings,
                )
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CharacterTabV4.entries.forEach { tab ->
                        Tab(
                            selected = tab == selectedTab,
                            onClick = { selectedTabName = tab.name },
                            text = { Text(tab.label, maxLines = 1, softWrap = false) },
                        )
                    }
                }
                when (selectedTab) {
                    CharacterTabV4.OVERVIEW -> OverviewTabV4(
                        draft = draft,
                        stored = stored,
                        wide = wide,
                        onDraftChange = ::updateDraft,
                    )
                    CharacterTabV4.SKILLS -> SkillsTabV4(
                        draft = draft,
                        wide = wide,
                        skillLayoutChoice = preferences.skillLayoutChoice,
                        onSkillLayoutChange = {
                            onPreferencesChange(preferences.copy(skillLayoutChoice = it))
                        },
                        onDraftChange = ::updateDraft,
                    )
                    CharacterTabV4.COMBAT -> CharacterCombatTabV4(
                        armorClass = draft.armorClass,
                        initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),
                        speed = draft.speed,
                        currentHp = draft.currentHp,
                        maxHp = draft.maxHp,
                        tempHp = draft.tempHp,
                        entries = combatEntries,
                        onEntriesChange = ::updateCombatEntries,
                        wide = wide,
                    )
                    CharacterTabV4.EQUIPMENT -> CharacterEquipmentTabV4(
                        items = equipmentDraft.items,
                        currencies = equipmentDraft.currencies,
                        onItemsChange = ::updateEquipmentItems,
                        onCurrenciesChange = ::updateCurrencies,
                        wide = wide,
                    )
                }
            }
        }
    }

    if (confirmBlankNumbers) {
        val missing = draft.missingRequiredNumberLabels()
        AlertDialog(
            onDismissRequest = { confirmBlankNumbers = false },
            title = { Text("Guardar campos vacíos como 0") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Hay campos numéricos requeridos vacíos. Si continúas, se guardarán como 0.")
                    if (missing.isNotEmpty()) {
                        val shown = missing.take(8).joinToString(", ")
                        Text(
                            if (missing.size > 8) "$shown…" else shown,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = ::saveBlankNumbersAsZero) { Text("Guardar con 0") }
            },
            dismissButton = {
                TextButton(onClick = { confirmBlankNumbers = false }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun EditorHeaderV4(
    characterName: String,
    stored: CharacterSheet,
    savedMessage: String?,
    savable: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StableBackIconButton(onClick = onBack, contentDescription = "Volver a personajes")
        Column(modifier = Modifier.weight(1f)) {
            Text(
                characterName.ifBlank { "Ficha de personaje" },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                savedMessage ?: "Guardado: ${formatSavedAtV4(stored.updatedAtEpochSeconds)}",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
        StableSettingsIconButton(onClick = onOpenSettings)
        Button(onClick = onSave, enabled = savable) { Text("Guardar") }
    }
}

// Remaining implementation is unchanged from the current branch version.
