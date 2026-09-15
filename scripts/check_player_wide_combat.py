#!/usr/bin/env python3
from pathlib import Path

COMBAT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatSuccessorV4.kt")
RESPONSIVE = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterResponsivePreferencesV4.kt")
SPATIAL = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpatialGridV4.kt")


def require(text: str, token: str, label: str) -> None:
    if token not in text:
        raise SystemExit(f"T7 guard failed: missing {label}: {token!r}")


def main() -> None:
    combat = COMBAT.read_text(encoding="utf-8")
    responsive = RESPONSIVE.read_text(encoding="utf-8")
    spatial = SPATIAL.read_text(encoding="utf-8")

    # T7 must repair the active successor screen, not an older Combat implementation.
    require(combat, "internal fun CharacterCombatSuccessorTabV4(", "active Combat successor")

    # Narrow phone behavior remains on the existing one-dimensional engine.
    require(combat, "val narrowReorderEnabled = reorderEnabled && !wide", "narrow reorder gate")
    require(combat, "CharacterReorderOverlayHostV4(", "narrow lifted overlay")
    require(combat, ".characterReorderSessionViewportV4(reorderSession)", "narrow reorder viewport")
    require(combat, "itemsIndexed(narrowLayoutEntries", "narrow one-column card list")

    # Wide/tablet behavior must use T3 adaptive density plus stabilized spatial reorder.
    require(combat, "val wideReorderEnabled = reorderEnabled && wide", "wide reorder gate")
    require(combat, "constrainedCardColumnsV4(wide = true, phoneMax = 1, wideMax = 3)", "T3-driven Combat columns")
    require(combat, "rememberCharacterSpatialReorderStateV4(", "wide spatial reorder state")
    require(combat, "CharacterSpatialReorderAutoScrollEffectV4(spatialReorderState)", "wide spatial auto-scroll")
    require(combat, ".characterSpatialReorderViewportV4(spatialReorderState)", "wide spatial viewport")
    require(combat, "CharacterSpatialGridV4(", "wide adaptive card grid")
    require(combat, "spatialPickupModifier = pickupModifier", "safe spatial pickup surface")
    require(combat, "spatialDragging = spatialReorderState.draggedId == id", "active spatial interaction block")

    # HUD is deliberately bounded rather than stretched across the tablet.
    require(combat, "Modifier.widthIn(max = 840.dp).fillMaxWidth()", "bounded wide Combat HUD")

    # Reorder persistence remains one shared canonical path across narrow and wide layouts.
    require(combat, "fun commitEntryOrder(proposedIds: List<String>)", "shared reorder commit")
    require(combat, "applyCharacterReorderResult(canonicalEntryIds, proposedIds)", "canonical reorder merge")
    require(combat, "entry.copy(sortOrder = order)", "persisted sort order")

    # The reused foundations must retain their adaptive and stable-spatial contracts.
    require(responsive, "minimumUsableCardWidthDp", "responsive minimum usable card width")
    require(responsive, "layoutContext.availableWidthDp", "available-width column bound")
    require(spatial, "maxItemsInEachRow = safeColumns", "responsive spatial row packing")
    require(spatial, ".characterSpatialReorderBoundsV4(reorderState, id)", "spatial bounds registration")
    require(spatial, ".characterSpatialReorderVisualV4(reorderState, id)", "spatial lifted visual")

    # The card must block operational child actions while its spatial body is actively dragged.
    require(combat, "val interactionBlocked = lifted || activePlaceholder || spatialDragging", "drag interaction suppression")
    require(combat, "enabled = favoriteEnabled && !interactionBlocked", "favorite drag suppression")
    require(combat, "TextButton(onClick = onEdit, enabled = !interactionBlocked)", "edit drag suppression")
    require(combat, "TextButton(onClick = onDelete, enabled = !interactionBlocked)", "delete drag suppression")

    print("T7 wide Combat guard passed: bounded HUD, adaptive wide grid, stable spatial reorder, narrow path preserved.")


if __name__ == "__main__":
    main()
