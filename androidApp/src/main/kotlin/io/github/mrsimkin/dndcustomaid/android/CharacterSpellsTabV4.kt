package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterGeneralSpellcastingRow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterReorderResult
import io.github.mrsimkin.dndcustomaid.shared.character.characterAbilityReferenceAbbreviation
import kotlin.uuid.Uuid

internal data class SpellSourceClassOptionV4(
    val id: Uuid,
    val name: String,
)

@Composable
internal fun CharacterSpellsTabV4(
    draft: CharacterSpellcastingDraftV4,
    spellcastingRows: List<CharacterGeneralSpellcastingRow>,
    successorState: CharacterSuccessorState,
    projectionSheet: CharacterSheet,
    spellcastingProfiles: List<CharacterSpellcastingProfile>,
    slotStates: List<CharacterSpellSlotUiV4>,
    classOptions: List<SpellSourceClassOptionV4>,
    traits: List<CharacterTrait>,
    inventoryItems: List<CharacterInventoryItem>,
    background: CharacterBackground,
    closureState: CharacterClosureState,
    persistedSpellIds: Set<Uuid>,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    onSpellcastingProfilesChange: (List<CharacterSpellcastingProfile>) -> Unit,
    structuralEditingEnabled: Boolean,
    onSlotSpentChange: (Int, Int) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var selectedSourceId by rememberSaveable("spell-source-selection") { mutableStateOf<String?>(null) }
    var managerOpen by rememberSaveable("spell-source-manager") { mutableStateOf(false) }
    var editorOpen by rememberSaveable("spell-source-editor") { mutableStateOf(false) }
    var editingSourceId by rememberSaveable("spell-source-edit-id") { mutableStateOf<String?>(null) }
    var deleteSourceId by rememberSaveable("spell-source-delete-id") { mutableStateOf<String?>(null) }

    val sourceManagerHaptic = rememberCharacterHapticHookV4(hapticsEnabled)

    val selectedSource = selectedSourceId?.let { selectedId ->
        draft.sources.firstOrNull { it.id.toString() == selectedId }
    }

    fun updateSources(updated: List<CharacterSpellcastingSource>) {
        onDraftChange(
            draft.copy(
                sources = updated.mapIndexed { index, source -> source.copy(sortOrder = index) },
            ),
        )
    }

    fun beginAddSource() {
        editingSourceId = null
        managerOpen = false
        editorOpen = true
    }

    fun beginEditSource(source: CharacterSpellcastingSource) {
        editingSourceId = source.id.toString()
        managerOpen = false
        editorOpen = true
    }

    fun requestDeleteSource(source: CharacterSpellcastingSource) {
        deleteSourceId = source.id.toString()
        managerOpen = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        CharacterSpellListClosureV4(
            draft = draft,
            slotStates = slotStates,
            selectedSourceId = selectedSource?.id,
            closureState = closureState,
            persistedSpellIds = persistedSpellIds,
            onDraftChange = onDraftChange,
            structuralEditingEnabled = structuralEditingEnabled,
            onSlotSpentChange = onSlotSpentChange,
            onClosureStateChange = onClosureStateChange,
            wide = wide,
            hapticsEnabled = hapticsEnabled,
            sourceContextContent = {
                SpellSourceContextSelectorV4(
                    sources = draft.sources,
                    spellcastingRows = spellcastingRows,
                    successorState = successorState,
                    selectedSourceId = selectedSource?.id,
                    onSelectedSourceChange = { sourceId ->
                        selectedSourceId = sourceId?.toString()
                    },
                    onManageSources = if (structuralEditingEnabled) {
                        { managerOpen = true }
                    } else {
                        null
                    },
                )
            },
        )
    }

    if (managerOpen && structuralEditingEnabled) {
        SourceManagerDialogV4(
            sources = draft.sources,
            onReorder = { proposedIds ->
                val canonicalIds = draft.sources.map { it.id.toString() }
                val finalIds = applyCharacterReorderResult(canonicalIds, proposedIds)
                if (finalIds != canonicalIds) {
                    val sourceById = draft.sources.associateBy { it.id.toString() }
                    val reordered = finalIds.mapNotNull(sourceById::get)
                    if (reordered.size == draft.sources.size) updateSources(reordered)
                }
            },
            onAdd = ::beginAddSource,
            onEdit = ::beginEditSource,
            onDelete = ::requestDeleteSource,
            onDismiss = { managerOpen = false },
            onHaptic = sourceManagerHaptic,
        )
    }

    if (editorOpen && structuralEditingEnabled) {
        val existing = editingSourceId?.let { id ->
            draft.sources.firstOrNull { it.id.toString() == id }
        }
        val existingProfile = existing?.let { source ->
            spellcastingProfiles.firstOrNull { it.sourceId == source.id }
        }
        CharacterSpellSourceEditorV4(
            title = if (existing == null) "Añadir origen de conjuros" else "Editar origen de conjuros",
            existing = existing,
            existingProfile = existingProfile,
            classOptions = classOptions,
            traits = traits,
            inventoryItems = inventoryItems,
            background = background,
            customAttributes = successorState.customAttributes,
            projectionSheet = projectionSheet,
            successorState = successorState,
            nextSortOrder = draft.sources.size,
            onCancel = {
                editorOpen = false
                managerOpen = true
            },
            onApply = { source, profile ->
                val updated = if (existing == null) {
                    draft.sources + source
                } else {
                    draft.sources.map { if (it.id == existing.id) source else it }
                }
                updateSources(updated)
                onSpellcastingProfilesChange(
                    spellcastingProfiles.filterNot { it.sourceId == source.id } + profile,
                )
                editorOpen = false
                managerOpen = true
            },
        )
    }

    deleteSourceId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = draft.sources.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteSourceId = null
        } else {
            val associationCount = draft.spells.count { spell ->
                spell.sourceAssociations.any { it.sourceId == target.id }
            }
            val warning = if (associationCount > 0) {
                "«${target.name}» está asociada a $associationCount conjuros. Se eliminará esta fuente y solo sus asociaciones. Los conjuros conceptuales permanecerán en la ficha y conservarán sus otras fuentes."
            } else {
                "¿Eliminar la fuente «${target.name}»?"
            }
            CharacterConfirmationDialog(
                title = "Eliminar fuente",
                message = warning,
                onDismissRequest = {
                    deleteSourceId = null
                    managerOpen = true
                },
                onConfirm = {
                    val remainingSources = draft.sources
                        .filterNot { it.id == target.id }
                        .mapIndexed { index, source -> source.copy(sortOrder = index) }
                    val remainingSpells = draft.spells.map { spell ->
                        spell.copy(
                            sourceAssociations = spell.sourceAssociations.filterNot {
                                it.sourceId == target.id
                            },
                        )
                    }
                    onDraftChange(
                        draft.copy(
                            sources = remainingSources,
                            spells = remainingSpells,
                        ),
                    )
                    onSpellcastingProfilesChange(
                        spellcastingProfiles.filterNot { it.sourceId == target.id },
                    )
                    if (selectedSourceId == target.id.toString()) {
                        selectedSourceId = null
                    }
                    deleteSourceId = null
                    managerOpen = true
                },
                confirmLabel = "Eliminar",
                destructive = true,
            )
        }
    }
}

@Composable
private fun SpellSourceContextSelectorV4(
    sources: List<CharacterSpellcastingSource>,
    spellcastingRows: List<CharacterGeneralSpellcastingRow>,
    successorState: CharacterSuccessorState,
    selectedSourceId: Uuid?,
    onSelectedSourceChange: (Uuid?) -> Unit,
    onManageSources: (() -> Unit)?,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val rowBySource = remember(spellcastingRows) {
        spellcastingRows.associateBy { it.source.id }
    }
    val selectedSource = selectedSourceId?.let { sourceId ->
        sources.firstOrNull { it.id == sourceId }
    }
    val selectedRow = selectedSource?.let { rowBySource[it.id] }
    val selectedAbility = selectedRow?.profile?.ability
        ?.let { characterAbilityReferenceAbbreviation(it, successorState) }
        ?: "—"
    val selectedSaveDc = selectedRow?.saveDc?.toString() ?: "—"
    val selectedAttack = selectedRow?.spellAttackModifier?.spellSourceSignedV4() ?: "—"

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 34.dp)
                .clickable { menuOpen = true },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(4.dp)),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    selectedSource?.name ?: "Todos",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (selectedSource != null) {
                    Text(
                        "($selectedAbility) · CD $selectedSaveDc · Ataque $selectedAttack",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                    )
                }
                Text("▾", style = MaterialTheme.typography.labelSmall)
            }
        }
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false },
        ) {
            DropdownMenuItem(
                text = { Text("Todos los conjuros") },
                onClick = {
                    onSelectedSourceChange(null)
                    menuOpen = false
                },
            )
            sources.sortedBy { it.sortOrder }.forEach { source ->
                val row = rowBySource[source.id]
                val ability = row?.profile?.ability
                    ?.let { characterAbilityReferenceAbbreviation(it, successorState) }
                    ?: "—"
                val saveDc = row?.saveDc?.toString() ?: "—"
                val attack = row?.spellAttackModifier?.spellSourceSignedV4() ?: "—"
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(source.name.ifBlank { "Fuente sin nombre" })
                            Text(
                                "$ability · CD $saveDc · Ataque $attack",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    },
                    onClick = {
                        onSelectedSourceChange(source.id)
                        menuOpen = false
                    },
                )
            }
            if (onManageSources != null) {
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Gestionar orígenes…") },
                    onClick = {
                        menuOpen = false
                        onManageSources()
                    },
                )
            }
            HorizontalDivider()
            CharacterHelpV4(
                "La aptitud, la CD de salvación y el ataque de conjuro pertenecen a cada fuente de conjuros.",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

private fun Int.spellSourceSignedV4(): String = if (this >= 0) "+$this" else toString()

@Composable
private fun SourceManagerDialogV4(
    sources: List<CharacterSpellcastingSource>,
    onReorder: (List<String>) -> Unit,
    onAdd: () -> Unit,
    onEdit: (CharacterSpellcastingSource) -> Unit,
    onDelete: (CharacterSpellcastingSource) -> Unit,
    onDismiss: () -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    val dialogEnvironment = characterDialogEnvironmentV4()
    val listState = rememberLazyListState()
    val reorderCoordinator = rememberCharacterReorderCoordinatorV4()
    val canonicalIds = sources.map { it.id.toString() }
    val sourceById = sources.associateBy { it.id.toString() }
    val reorderEnabled = sources.size > 1
    val reorderSession = rememberCharacterReorderSessionV4(
        sessionKey = "spell-sources",
        canonicalOrder = canonicalIds,
        enabled = reorderEnabled,
        coordinator = reorderCoordinator,
        onCommitOrder = onReorder,
        onHaptic = onHaptic,
        autoScrollBy = { delta -> listState.scrollBy(delta) },
    )
    CharacterReorderSessionAutoScrollEffectV4(reorderSession)
    val layoutSources = if (reorderEnabled) {
        reorderSession.previewOrder.mapNotNull(sourceById::get)
    } else {
        sources
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { dialogEnvironment.Provide { Text("Orígenes de conjuros") } },
        text = {
            dialogEnvironment.Provide {
                CharacterReorderOverlayHostV4(
                    session = reorderSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .navigationBarsPadding(),
                    liftedContent = { draggedId ->
                        sourceById[draggedId]?.let { source ->
                            SourceManagerRowV4(
                                source = source,
                                reorderSession = null,
                                onEdit = {},
                                onDelete = {},
                                lifted = true,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    },
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .characterReorderSessionViewportV4(reorderSession),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                        contentPadding = PaddingValues(bottom = 32.dp),
                    ) {
                        item {
                            CharacterHelpV4(
                                "Cada origen organiza una sola colección de conjuros y conserva su propia aptitud, CD y ataque de lanzamiento.",
                            )
                        }
                        item {
                            TextButton(onClick = onAdd) { Text("Añadir origen") }
                        }
                        if (sources.isEmpty()) {
                            item {
                                Text("Sin fuentes registradas.", style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            items(
                                count = layoutSources.size,
                                key = { index -> layoutSources[index].id.toString() },
                            ) { index ->
                                val source = layoutSources[index]
                                SourceManagerRowV4(
                                    source = source,
                                    reorderSession = reorderSession.takeIf { reorderEnabled },
                                    onEdit = { onEdit(source) },
                                    onDelete = { onDelete(source) },
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            dialogEnvironment.Provide { TextButton(onClick = onDismiss) { Text("Cerrar") } }
        },
    )
}

@Composable
private fun SourceManagerRowV4(
    source: CharacterSpellcastingSource,
    reorderSession: CharacterReorderSessionV4?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    lifted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val id = source.id.toString()
    val geometryModifier = if (reorderSession != null && !lifted) {
        Modifier
            .characterReorderSessionBoundsV4(reorderSession, id)
            .characterReorderPlaceholderV4(reorderSession, id)
            .characterReorderSessionSemanticsV4(reorderSession, id)
    } else {
        Modifier
    }
    val pickupModifier = if (reorderSession != null && !lifted) {
        Modifier.characterReorderSessionDragHandleV4(reorderSession, id)
    } else {
        Modifier
    }
    val activePlaceholder = reorderSession?.draggedId == id

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(geometryModifier),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(pickupModifier)
                    .clickable(enabled = !lifted && !activePlaceholder, onClick = onEdit),
            ) {
                Text(source.name, style = MaterialTheme.typography.labelLarge)
                Text(
                    "Origen: ${spellSourceOriginLabelV4(source.originKind)}",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (!lifted) {
                StableRemoveIconButton(
                    onClick = onDelete,
                    contentDescription = "Eliminar fuente ${source.name}",
                )
            }
        }
    }
}