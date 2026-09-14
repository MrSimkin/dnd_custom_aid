#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatSuccessorV4.kt")
MARKER = "wideCombatColumnsV4"


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, observed {count}")
    return text.replace(old, new, 1)


def main() -> None:
    text = PATH.read_text(encoding="utf-8")
    if MARKER in text:
        print("T7 wide Combat migration already applied; no changes required.")
        return

    text = replace_once(
        text,
        "import androidx.compose.foundation.layout.padding\n"
        "import androidx.compose.foundation.lazy.LazyColumn\n",
        "import androidx.compose.foundation.layout.padding\n"
        "import androidx.compose.foundation.layout.widthIn\n"
        "import androidx.compose.foundation.lazy.LazyColumn\n",
        "widthIn import",
    )

    start = text.index("    val canonicalEntryIds = entries.map { it.id.toString() }")
    end = text.index("    if (editorOpen && structuralEditingEnabled) {", start)
    old_layout = text[start:end]
    new_layout = '''    val canonicalEntryIds = entries.map { it.id.toString() }
    val entryById = entries.associateBy { it.id.toString() }
    val reorderEnabled = structuralEditingEnabled && entries.size > 1
    val narrowReorderEnabled = reorderEnabled && !wide
    val wideReorderEnabled = reorderEnabled && wide

    fun commitEntryOrder(proposedIds: List<String>) {
        val finalIds = applyCharacterReorderResult(canonicalEntryIds, proposedIds)
        if (finalIds != canonicalEntryIds) {
            val reordered = finalIds.mapNotNull(entryById::get)
            if (reordered.size == entries.size) {
                onEntriesChange(reordered.mapIndexed { order, entry -> entry.copy(sortOrder = order) })
            }
        }
    }

    val reorderSession = rememberCharacterReorderSessionV4(
        sessionKey = "combat-entries",
        canonicalOrder = canonicalEntryIds,
        enabled = narrowReorderEnabled,
        coordinator = reorderCoordinator,
        onCommitOrder = ::commitEntryOrder,
        onHaptic = haptic,
        autoScrollBy = { delta -> listState.scrollBy(delta) },
    )
    val spatialReorderState = rememberCharacterSpatialReorderStateV4(
        canonicalOrder = canonicalEntryIds,
        onCommitOrder = ::commitEntryOrder,
        onHaptic = haptic,
        autoScrollBy = { delta -> listState.scrollBy(delta) },
    )
    CharacterReorderSessionAutoScrollEffectV4(reorderSession)
    CharacterSpatialReorderAutoScrollEffectV4(spatialReorderState)

    val narrowLayoutEntries = if (narrowReorderEnabled) {
        reorderSession.previewOrder.mapNotNull(entryById::get)
    } else {
        entries
    }
    val wideLayoutIds = if (wideReorderEnabled) spatialReorderState.previewOrder else canonicalEntryIds
    val wideCombatColumnsV4 = if (wide) {
        constrainedCardColumnsV4(wide = true, phoneMax = 1, wideMax = 3)
    } else {
        1
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
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = if (wide) {
                    Modifier.widthIn(max = 840.dp).fillMaxWidth()
                } else {
                    Modifier.fillMaxWidth()
                },
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
        }

        if (wide) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .characterSpatialReorderViewportV4(spatialReorderState),
                contentPadding = PaddingValues(
                    start = appSpacingV4(8.dp),
                    end = appSpacingV4(8.dp),
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
                    item(key = "combat-entries-spatial-grid") {
                        CharacterSpatialGridV4(
                            ids = wideLayoutIds,
                            columns = wideCombatColumnsV4,
                            reorderState = spatialReorderState,
                            reorderEnabled = wideReorderEnabled,
                        ) { id, pickupModifier ->
                            val entry = entryById[id] ?: return@CharacterSpatialGridV4
                            CharacterCombatSuccessorCardV4(
                                entry = entry,
                                damageComponents = damageFor(entry.id),
                                favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                                favoriteEnabled = structuralEditingEnabled && entry.id in persistedEntryIds,
                                reorderSession = null,
                                spatialPickupModifier = pickupModifier,
                                spatialDragging = spatialReorderState.draggedId == id,
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
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        } else {
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
                        start = appSpacingV4(5.dp),
                        end = appSpacingV4(5.dp),
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
                        itemsIndexed(narrowLayoutEntries, key = { _, entry -> entry.id.toString() }) { _, entry ->
                            CharacterCombatSuccessorCardV4(
                                entry = entry,
                                damageComponents = damageFor(entry.id),
                                favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                                favoriteEnabled = structuralEditingEnabled && entry.id in persistedEntryIds,
                                reorderSession = reorderSession.takeIf { narrowReorderEnabled },
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
    }

'''
    text = text[:start] + new_layout + text[end:]

    text = replace_once(
        text,
        "    reorderSession: CharacterReorderSessionV4?,\n"
        "    structuralEditingEnabled: Boolean,\n",
        "    reorderSession: CharacterReorderSessionV4?,\n"
        "    spatialPickupModifier: Modifier = Modifier,\n"
        "    spatialDragging: Boolean = false,\n"
        "    structuralEditingEnabled: Boolean,\n",
        "combat card spatial parameters",
    )
    text = replace_once(
        text,
        "    val pickupModifier = if (reorderSession != null && !lifted) {\n"
        "        Modifier.characterReorderSessionDragHandleV4(reorderSession, id)\n"
        "    } else {\n"
        "        Modifier\n"
        "    }\n"
        "    val activePlaceholder = reorderSession?.draggedId == id\n",
        "    val pickupModifier = if (reorderSession != null && !lifted) {\n"
        "        Modifier.characterReorderSessionDragHandleV4(reorderSession, id)\n"
        "    } else {\n"
        "        spatialPickupModifier\n"
        "    }\n"
        "    val activePlaceholder = reorderSession?.draggedId == id\n"
        "    val interactionBlocked = lifted || activePlaceholder || spatialDragging\n",
        "combat card pickup state",
    )
    text = replace_once(
        text,
        "                        enabled = structuralEditingEnabled && !lifted && !activePlaceholder,\n",
        "                        enabled = structuralEditingEnabled && !interactionBlocked,\n",
        "combat card body interaction block",
    )
    text = replace_once(
        text,
        "                    enabled = !lifted && favoriteEnabled,\n",
        "                    enabled = favoriteEnabled && !interactionBlocked,\n",
        "combat favorite interaction block",
    )
    text = replace_once(
        text,
        "                if (structuralEditingEnabled && !lifted) {\n"
        "                    TextButton(onClick = onEdit) { Text(\"Editar\") }\n"
        "                    TextButton(onClick = onDelete) { Text(\"Eliminar\") }\n"
        "                }\n",
        "                if (structuralEditingEnabled && !lifted) {\n"
        "                    TextButton(onClick = onEdit, enabled = !interactionBlocked) { Text(\"Editar\") }\n"
        "                    TextButton(onClick = onDelete, enabled = !interactionBlocked) { Text(\"Eliminar\") }\n"
        "                }\n",
        "combat card action interaction block",
    )

    PATH.write_text(text, encoding="utf-8")
    print(
        "T7 wide Combat applied: bounded HUD, adaptive T3-driven wide grid, stabilized spatial reorder, "
        "and preserved narrow one-dimensional Combat path."
    )


if __name__ == "__main__":
    main()
