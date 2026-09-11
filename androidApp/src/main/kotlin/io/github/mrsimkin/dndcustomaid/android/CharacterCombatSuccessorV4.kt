package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatDamageProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponent
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponentKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterReorderResult
import io.github.mrsimkin.dndcustomaid.shared.character.characterCombatEntryTypeSpanishLabel
import io.github.mrsimkin.dndcustomaid.shared.character.characterDamageSummary
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterUnsignedIntegerInput
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.uuid.Uuid

private val STANDARD_DIE_SIDES_V4 = listOf(4, 6, 8, 10, 12, 20)
private val STANDARD_DAMAGE_TYPES_V4 = listOf(
    "Ácido",
    "Contundente",
    "Cortante",
    "Frío",
    "Fuego",
    "Fuerza",
    "Necrótico",
    "Perforante",
    "Psíquico",
    "Radiante",
    "Relámpago",
    "Trueno",
    "Veneno",
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CharacterCombatSuccessorTabV4(
    armorClass: String,
    initiative: String,
    speed: String,
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    persistedEntryIds: Set<Uuid>,
    entries: List<CharacterCombatEntry>,
    damageProfiles: List<CharacterCombatDamageProfile>,
    onEntriesChange: (List<CharacterCombatEntry>) -> Unit,
    onDamageProfilesChange: (List<CharacterCombatDamageProfile>) -> Unit,
    onOperationalSheetChange: (CharacterSheet) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean = true,
) {
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable { mutableStateOf("") }
    var editorType by rememberSaveable { mutableStateOf(CharacterCombatEntryType.ATTACK.name) }
    var editorAttackModifier by rememberSaveable { mutableStateOf("") }
    var editorRange by rememberSaveable { mutableStateOf("") }
    var editorNotes by rememberSaveable { mutableStateOf("") }
    var editorDamageJson by rememberSaveable { mutableStateOf("[]") }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val listState = rememberLazyListState()
    val reorderCoordinator = rememberCharacterReorderCoordinatorV4()

    fun damageFor(entryId: Uuid): List<CharacterDamageComponent> =
        damageProfiles.firstOrNull { it.combatEntryId == entryId }?.components.orEmpty()

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        editingId = null
        editorName = ""
        editorType = CharacterCombatEntryType.ATTACK.name
        editorAttackModifier = ""
        editorRange = ""
        editorNotes = ""
        editorDamageJson = "[]"
        editorOpen = true
    }

    fun beginEdit(entry: CharacterCombatEntry) {
        if (!structuralEditingEnabled) return
        editingId = entry.id.toString()
        editorName = entry.name
        editorType = entry.type.name
        editorAttackModifier = entry.attackModifier?.toString().orEmpty()
        editorRange = entry.rangeText.orEmpty()
        editorNotes = entry.notes.orEmpty()
        editorDamageJson = characterDamageComponentsToJsonV4(damageFor(entry.id))
        editorOpen = true
    }

    val canonicalEntryIds = entries.map { it.id.toString() }
    val entryById = entries.associateBy { it.id.toString() }
    val reorderEnabled = structuralEditingEnabled && entries.size > 1
    val reorderSession = rememberCharacterReorderSessionV4(
        sessionKey = "combat-entries",
        canonicalOrder = canonicalEntryIds,
        enabled = reorderEnabled,
        coordinator = reorderCoordinator,
        onCommitOrder = { proposedIds ->
            val finalIds = applyCharacterReorderResult(canonicalEntryIds, proposedIds)
            if (finalIds != canonicalEntryIds) {
                val reordered = finalIds.mapNotNull(entryById::get)
                if (reordered.size == entries.size) {
                    onEntriesChange(reordered.mapIndexed { order, entry -> entry.copy(sortOrder = order) })
                }
            }
        },
        onHaptic = haptic,
        autoScrollBy = { delta -> listState.scrollBy(delta) },
    )
    CharacterReorderSessionAutoScrollEffectV4(reorderSession)
    val layoutEntries = if (reorderEnabled) {
        reorderSession.previewOrder.mapNotNull(entryById::get)
    } else {
        entries
    }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(
                start = appSpacingV4(if (wide) 8.dp else 5.dp),
                end = appSpacingV4(if (wide) 8.dp else 5.dp),
                top = appSpacingV4(4.dp),
            ),
        ) {
            CharacterCombatOperationalCardV4(
                armorClass = armorClass,
                initiative = initiative,
                speed = speed,
                sheet = sheet,
                onSheetChange = onOperationalSheetChange,
                hapticsEnabled = hapticsEnabled,
            )
        }

        CharacterReorderOverlayHostV4(
            session = reorderSession,
            modifier = Modifier.fillMaxWidth().weight(1f),
            liftedContent = { draggedId ->
                entryById[draggedId]?.let { entry ->
                    CharacterCombatSuccessorCardV4(
                        entry = entry,
                        damageComponents = damageFor(entry.id),
                        favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                        favoriteEnabled = false,
                        reorderSession = null,
                        structuralEditingEnabled = false,
                        onFavoriteChange = {},
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
                    .fillMaxSize()
                    .characterReorderSessionViewportV4(reorderSession),
                contentPadding = PaddingValues(
                    start = appSpacingV4(if (wide) 8.dp else 5.dp),
                    end = appSpacingV4(if (wide) 8.dp else 5.dp),
                    bottom = appSpacingV4(88.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            ) {
                if (sheet.currentHp <= 0) {
                    item(key = "combat-death-saves") {
                        CharacterCombatDeathSavesSectionV4(
                            sheet = sheet,
                            onSheetChange = onOperationalSheetChange,
                            hapticsEnabled = hapticsEnabled,
                        )
                    }
                }

                item(key = "combat-entries-header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Ataques y acciones", style = MaterialTheme.typography.titleSmall)
                        if (structuralEditingEnabled) {
                            TextButton(onClick = ::beginAdd) { Text("Añadir") }
                        }
                    }
                }

                if (entries.isEmpty()) {
                    item(key = "combat-empty") {
                        CharacterUsefulEmptyState(
                            title = "Sin ataques o acciones",
                            message = "Añade ataques, acciones, reacciones o referencias de combate.",
                            onAdd = if (structuralEditingEnabled) ::beginAdd else null,
                        )
                    }
                } else {
                    itemsIndexed(layoutEntries, key = { _, entry -> entry.id.toString() }) { _, entry ->
                        CharacterCombatSuccessorCardV4(
                            entry = entry,
                            damageComponents = damageFor(entry.id),
                            favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                            favoriteEnabled = structuralEditingEnabled && entry.id in persistedEntryIds,
                            reorderSession = reorderSession.takeIf { reorderEnabled },
                            structuralEditingEnabled = structuralEditingEnabled,
                            onFavoriteChange = { enabled ->
                                onClosureStateChange(
                                    closureState.withQuickAccess(
                                        CharacterQuickAccessKind.COMBAT_ENTRY,
                                        entry.id,
                                        enabled,
                                    ),
                                )
                            },
                            onEdit = { beginEdit(entry) },
                            onDelete = { deleteId = entry.id.toString() },
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    if (editorOpen && structuralEditingEnabled) {
        val selectedType = runCatching { CharacterCombatEntryType.valueOf(editorType) }
            .getOrDefault(CharacterCombatEntryType.ATTACK)
        val parsedAttack = editorAttackModifier.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        val attackValid = editorAttackModifier.trim().isEmpty() || parsedAttack != null
        val components = characterDamageComponentsFromJsonV4(editorDamageJson)
        val damageValid = components.all(::characterDamageComponentValidV4)
        val valid = editorName.trim().isNotEmpty() && attackValid && damageValid

        CharacterImeSafeEditorDialog(
            title = if (editingId == null) "Añadir ataque o acción" else "Editar ataque o acción",
            onCancel = { editorOpen = false },
            onSave = {
                val existing = editingId?.let { id -> entries.firstOrNull { it.id.toString() == id } }
                val entryId = existing?.id ?: Uuid.random()
                val normalizedComponents = components.map { component ->
                    component.copy(
                        expression = component.expression.trim(),
                        typeText = component.typeText?.trim()?.takeIf { it.isNotEmpty() },
                    )
                }
                val entry = CharacterCombatEntry(
                    id = entryId,
                    name = editorName.trim(),
                    type = selectedType,
                    attackModifier = parsedAttack,
                    damageEffect = characterDamageSummary(normalizedComponents),
                    rangeText = editorRange.trim().takeIf { it.isNotEmpty() },
                    notes = editorNotes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: entries.size,
                    pinned = existing?.pinned ?: false,
                )
                val updatedEntries = if (existing == null) {
                    entries + entry
                } else {
                    entries.map { if (it.id == existing.id) entry else it }
                }.mapIndexed { order, item -> item.copy(sortOrder = order) }
                val updatedDamage = damageProfiles.filterNot { it.combatEntryId == entryId } +
                    CharacterCombatDamageProfile(entryId, normalizedComponents)
                onEntriesChange(updatedEntries)
                onDamageProfilesChange(updatedDamage)
                editorOpen = false
            },
            saveEnabled = valid,
        ) {
            OutlinedTextField(
                value = editorName,
                onValueChange = { editorName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre") },
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CharacterCombatTypeSelectorV4(
                    selected = selectedType,
                    onSelected = { editorType = it.name },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = editorAttackModifier,
                    onValueChange = { editorAttackModifier = sanitizeSignedIntegerInputV4(it) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Ataque") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = editorRange,
                    onValueChange = { editorRange = it },
                    modifier = Modifier.weight(1.2f),
                    label = { Text("Alcance") },
                    singleLine = true,
                )
            }

            Text("Daño / efecto", style = MaterialTheme.typography.titleSmall)
            components.forEachIndexed { index, component ->
                CharacterDamageComponentEditorRowV4(
                    component = component,
                    onChange = { updated ->
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components.mapIndexed { componentIndex, current ->
                                if (componentIndex == index) updated else current
                            },
                        )
                    },
                    onRemove = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components.filterIndexed { componentIndex, _ -> componentIndex != index },
                        )
                    },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                TextButton(
                    onClick = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components + CharacterDamageComponent(CharacterDamageComponentKind.DICE, "1d8"),
                        )
                    },
                ) { Text("Añadir dados") }
                TextButton(
                    onClick = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components + CharacterDamageComponent(CharacterDamageComponentKind.FLAT, "0"),
                        )
                    },
                ) { Text("Añadir plano") }
                TextButton(
                    onClick = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components + CharacterDamageComponent(CharacterDamageComponentKind.TEXT, ""),
                        )
                    },
                ) { Text("Añadir efecto") }
            }
            CharacterInlineValidationMessage(
                when {
                    !attackValid -> "El modificador de ataque debe ser un entero o quedar vacío."
                    !damageValid -> "Revisa los componentes de daño incompletos."
                    else -> null
                },
            )
            OutlinedTextField(
                value = editorNotes,
                onValueChange = { editorNotes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notas") },
                minLines = characterCompactTextAreaMinLinesV4(2),
                maxLines = 5,
            )
        }
    }

    deleteId?.let { id ->
        val target = entries.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "ataque o acción",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onEntriesChange(
                        entries.filterNot { it.id == target.id }
                            .mapIndexed { order, item -> item.copy(sortOrder = order) },
                    )
                    onDamageProfilesChange(damageProfiles.filterNot { it.combatEntryId == target.id })
                    deleteId = null
                },
            )
        }
    }
}

@Composable
private fun CharacterCombatSuccessorCardV4(
    entry: CharacterCombatEntry,
    damageComponents: List<CharacterDamageComponent>,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    reorderSession: CharacterReorderSessionV4?,
    structuralEditingEnabled: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    lifted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val id = entry.id.toString()
    val damage = characterDamageSummary(damageComponents).ifBlank { "Sin daño / efecto" }
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
        modifier = modifier.then(geometryModifier),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = appSpacingV4(7.dp),
                vertical = appSpacingV4(5.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(pickupModifier)
                    .clickable(
                        enabled = structuralEditingEnabled && !lifted && !activePlaceholder,
                        onClick = onEdit,
                    ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        buildString {
                            append(entry.name)
                            entry.attackModifier?.let { append(" (${formatSignedCombatSuccessorV4(it)})") }
                        },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    damage,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    listOfNotNull(
                        characterCombatEntryTypeSpanishLabel(entry.type),
                        entry.rangeText?.trim()?.takeIf { it.isNotEmpty() }?.let { "Alcance $it" },
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                entry.notes?.trim()?.takeIf { it.isNotEmpty() }?.let { notes ->
                    Text(
                        notes,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StableFavoriteIconButton(
                    selected = favorite,
                    onClick = { onFavoriteChange(!favorite) },
                    enabled = !lifted && favoriteEnabled,
                )
                if (structuralEditingEnabled && !lifted) {
                    TextButton(onClick = onEdit) { Text("Editar") }
                    TextButton(onClick = onDelete) { Text("Eliminar") }
                }
            }
        }
    }
}

@Composable
private fun CharacterCombatTypeSelectorV4(
    selected: CharacterCombatEntryType,
    onSelected: (CharacterCombatEntryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(characterCombatEntryTypeSpanishLabel(selected), maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CharacterCombatEntryType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(characterCombatEntryTypeSpanishLabel(type)) },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

private data class CharacterDiceComponentDraftV4(
    val negativeDice: Boolean,
    val quantity: String,
    val sides: String,
    val modifier: String,
)

private val DICE_COMPONENT_REGEX_V4 = Regex("^([+-]?)([0-9]+)[dD]([0-9]+)([+-][0-9]+)?$")

private fun parseDiceComponentDraftV4(expression: String): CharacterDiceComponentDraftV4 {
    val match = DICE_COMPONENT_REGEX_V4.matchEntire(expression.trim())
    return if (match == null) {
        CharacterDiceComponentDraftV4(false, "", "", "")
    } else {
        CharacterDiceComponentDraftV4(
            negativeDice = match.groupValues[1] == "-",
            quantity = match.groupValues[2],
            sides = match.groupValues[3],
            modifier = match.groupValues[4],
        )
    }
}

private fun buildDiceComponentExpressionV4(draft: CharacterDiceComponentDraftV4): String = buildString {
    if (draft.negativeDice) append('-')
    append(draft.quantity)
    append('d')
    append(draft.sides)
    append(draft.modifier)
}

@Composable
private fun CharacterDamageComponentEditorRowV4(
    component: CharacterDamageComponent,
    onChange: (CharacterDamageComponent) -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(appSpacingV4(5.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(characterDamageKindLabelV4(component.kind), style = MaterialTheme.typography.labelLarge)
                StableRemoveIconButton(onClick = onRemove, contentDescription = "Eliminar componente de daño")
            }

            when (component.kind) {
                CharacterDamageComponentKind.DICE -> DiceDamageFieldsV4(component = component, onChange = onChange)
                CharacterDamageComponentKind.FLAT -> FlatDamageFieldsV4(component = component, onChange = onChange)
                CharacterDamageComponentKind.TEXT -> OutlinedTextField(
                    value = component.expression,
                    onValueChange = { onChange(component.copy(expression = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Efecto") },
                    minLines = 1,
                    maxLines = 3,
                )
            }
        }
    }
}

@Composable
private fun DiceDamageFieldsV4(
    component: CharacterDamageComponent,
    onChange: (CharacterDamageComponent) -> Unit,
) {
    val parsed = parseDiceComponentDraftV4(component.expression)
    var dieMenuExpanded by remember { mutableStateOf(false) }
    var otherSides by remember(component.kind) {
        mutableStateOf(parsed.sides.toIntOrNull()?.let { it !in STANDARD_DIE_SIDES_V4 } ?: false)
    }

    fun updateDraft(updated: CharacterDiceComponentDraftV4) {
        onChange(component.copy(expression = buildDiceComponentExpressionV4(updated)))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var signExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.weight(0.55f)) {
            OutlinedButton(onClick = { signExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (parsed.negativeDice) "−" else "+")
            }
            DropdownMenu(expanded = signExpanded, onDismissRequest = { signExpanded = false }) {
                listOf(false to "+", true to "−").forEach { (negative, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            updateDraft(parsed.copy(negativeDice = negative))
                            signExpanded = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            value = parsed.quantity,
            onValueChange = { value ->
                updateDraft(parsed.copy(quantity = normalizeCharacterUnsignedIntegerInput(value)))
            },
            modifier = Modifier.weight(0.9f),
            label = { Text("Cant.") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(onClick = { dieMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                val sides = parsed.sides.toIntOrNull()
                Text(if (!otherSides && sides in STANDARD_DIE_SIDES_V4) "d$sides" else "Otro…")
            }
            DropdownMenu(expanded = dieMenuExpanded, onDismissRequest = { dieMenuExpanded = false }) {
                STANDARD_DIE_SIDES_V4.forEach { sides ->
                    DropdownMenuItem(
                        text = { Text("d$sides") },
                        onClick = {
                            otherSides = false
                            updateDraft(parsed.copy(sides = sides.toString()))
                            dieMenuExpanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Otro…") },
                    onClick = {
                        otherSides = true
                        updateDraft(parsed.copy(sides = ""))
                        dieMenuExpanded = false
                    },
                )
            }
        }
        OutlinedTextField(
            value = parsed.modifier,
            onValueChange = { value -> updateDraft(parsed.copy(modifier = sanitizeSignedIntegerInputV4(value))) },
            modifier = Modifier.weight(1f),
            label = { Text("Mod.") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }

    if (otherSides) {
        OutlinedTextField(
            value = parsed.sides,
            onValueChange = { value -> updateDraft(parsed.copy(sides = normalizeCharacterUnsignedIntegerInput(value))) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Caras del dado") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }

    DamageTypeSelectorV4(component = component, onChange = onChange)
}

@Composable
private fun FlatDamageFieldsV4(
    component: CharacterDamageComponent,
    onChange: (CharacterDamageComponent) -> Unit,
) {
    OutlinedTextField(
        value = component.expression,
        onValueChange = { onChange(component.copy(expression = sanitizeSignedIntegerInputV4(it))) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Daño plano") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
    DamageTypeSelectorV4(component = component, onChange = onChange)
}

@Composable
private fun DamageTypeSelectorV4(
    component: CharacterDamageComponent,
    onChange: (CharacterDamageComponent) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val current = component.typeText?.trim().orEmpty()
    val standard = STANDARD_DAMAGE_TYPES_V4.firstOrNull { it.equals(current, ignoreCase = true) }
    var customMode by remember(component.kind) { mutableStateOf(current.isNotEmpty() && standard == null) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(when {
                    customMode -> "Otro…"
                    standard != null -> standard
                    else -> "Tipo de daño"
                })
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                STANDARD_DAMAGE_TYPES_V4.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            customMode = false
                            onChange(component.copy(typeText = type))
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Otro…") },
                    onClick = {
                        customMode = true
                        onChange(component.copy(typeText = null))
                        expanded = false
                    },
                )
            }
        }
        if (customMode) {
            OutlinedTextField(
                value = current,
                onValueChange = { onChange(component.copy(typeText = it)) },
                modifier = Modifier.weight(1f),
                label = { Text("Otro tipo") },
                singleLine = true,
            )
        }
    }
}

private fun characterDamageComponentValidV4(component: CharacterDamageComponent): Boolean = when (component.kind) {
    CharacterDamageComponentKind.DICE -> {
        val parsed = parseDiceComponentDraftV4(component.expression)
        val quantity = parsed.quantity.toIntOrNull()
        val sides = parsed.sides.toIntOrNull()
        val modifierValid = parsed.modifier.isEmpty() || parsed.modifier.toIntOrNull() != null
        quantity != null && quantity > 0 && sides != null && sides > 0 && modifierValid
    }
    CharacterDamageComponentKind.FLAT -> component.expression.trim().toIntOrNull() != null
    CharacterDamageComponentKind.TEXT -> component.expression.isNotBlank()
}

private fun characterDamageKindLabelV4(kind: CharacterDamageComponentKind): String = when (kind) {
    CharacterDamageComponentKind.DICE -> "Dados"
    CharacterDamageComponentKind.FLAT -> "Plano"
    CharacterDamageComponentKind.TEXT -> "Efecto"
}

private fun formatSignedCombatSuccessorV4(value: Int): String = if (value >= 0) "+$value" else value.toString()
