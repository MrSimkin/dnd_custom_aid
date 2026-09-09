from pathlib import Path
import re

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
files = sorted(ROOT.glob("*.kt"))
texts = {p.name: p.read_text(encoding="utf-8") for p in files}


def fail(message: str) -> None:
    raise RuntimeError(message)


def function_slice(text: str, start_marker: str, end_marker: str) -> str:
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise RuntimeError(f"Missing audit marker: {exc}") from exc
    return text[start:end]

# Critical F repair invariants.
spell_text = texts["CharacterSpellListClosureV4.kt"]
spell_row = function_slice(spell_text, "private fun SpellRowG2(", "private fun SpellBadgeG2(")
for forbidden in ["detectDragGesturesAfterLongPress", "StableDragHandle", "66.dp", "while ("]:
    if forbidden in spell_row:
        fail(f"SpellRowG2 still contains forbidden legacy drag pattern: {forbidden}")
if ".characterMeasuredReorderDragV4(" not in spell_row:
    fail("SpellRowG2 does not use whole-card measured reorder primitive")
for control in ["StableFavoriteIconButton", "StableDuplicateIconButton", "StableRemoveIconButton"]:
    if control not in spell_row:
        fail(f"SpellRowG2 missing compact shared action control: {control}")

card_interaction = texts["CharacterCardInteractionV4.kt"]
for required in ["rememberUpdatedState", "characterMeasuredReorderDragV4", "onSizeChanged"]:
    if required not in card_interaction:
        fail(f"Shared reorder primitive missing {required}")
measured = function_slice(card_interaction, "internal fun Modifier.characterMeasuredReorderDragV4(", "}") if False else card_interaction
if "while (" in card_interaction:
    fail("Shared card interaction contains multi-step while loop")

interaction = texts["CharacterInteractionPrimitivesV4.kt"]
ime = function_slice(interaction, "internal fun CharacterImeSafeEditorDialog(", "/**\n * Shared two-field row")
for required in [".imePadding()", ".fillMaxHeight()", ".weight(1f)", ".verticalScroll(scrollState)"]:
    if required not in ime:
        fail(f"IME-safe dialog missing required geometry: {required}")
if "fill = false" in ime:
    fail("IME-safe dialog still uses non-filling weighted body")

# Spell toolbar/editor repair invariants.
for required in [
    "collapsibleSearch = true",
    "showItemCount = false",
    "compactOrderControl = true",
    "sourceContextContent = sourceContextContent",
    "normalizeCharacterUnsignedIntegerInput(it, maxDigits = 1)",
]:
    if required not in spell_text:
        fail(f"Conjuros repair invariant missing: {required}")
if spell_text.count("CharacterCompactFieldRowV4(") < 2:
    fail("Spell editor no longer has the expected compact short-field rows")

toolbar = texts["CharacterCollectionPrimitivesV4.kt"]
if "if (!collapsibleSearch || !searchExpanded)" not in toolbar:
    fail("Expanded collapsible search does not suppress ancillary toolbar controls")

# App-wide favorite control regression guard.
raw_favorite = []
raw_font_size = []
duplicate_text = []
raw_pair_padding = []
ime_callers = []
for name, text in texts.items():
    for match in re.finditer(r'Text\(if \([^\n]+\)\s*"★"\s*else\s*"☆"\)', text):
        raw_favorite.append((name, text.count("\n", 0, match.start()) + 1))
    for match in re.finditer(r'\bfontSize\s*=', text):
        raw_font_size.append((name, text.count("\n", 0, match.start()) + 1))
    for match in re.finditer(r'Text\("Duplicar"\)', text):
        duplicate_text.append((name, text.count("\n", 0, match.start()) + 1))
    for match in re.finditer(r'padding\(horizontal\s*=\s*\d+(?:\.\d+)?\.dp,\s*vertical\s*=\s*\d+(?:\.\d+)?\.dp\)', text):
        raw_pair_padding.append((name, text.count("\n", 0, match.start()) + 1, match.group(0)))
    if "CharacterImeSafeEditorDialog(" in text and name != "CharacterInteractionPrimitivesV4.kt":
        ime_callers.append(name)

if raw_favorite:
    fail("Raw Unicode favorite button controls remain: " + repr(raw_favorite))
if raw_font_size:
    fail("Explicit Android fontSize overrides remain and may bypass app typography scaling: " + repr(raw_font_size))

# Targeted secondary cards must not regress to a text duplicate action tier.
for target in [
    "CharacterFormsModuleV4.kt",
    "CharacterCompanionsModuleV4.kt",
    "CharacterArtificeModuleV4.kt",
    "CharacterClassOptionModulesV4.kt",
]:
    if any(name == target for name, _ in duplicate_text):
        fail(f"{target} still contains a text Duplicar control after compact action migration")

# Report non-blocking residuals for human judgment.
print("F REPAIR RESIDUAL AUDIT: critical invariants PASS")
print(f"Android Kotlin files scanned: {len(files)}")
print(f"IME-safe editor caller files: {len(ime_callers)}")
print(f"Raw Unicode favorite controls: {len(raw_favorite)}")
print(f"Explicit fontSize overrides: {len(raw_font_size)}")
print(f"Remaining Text(\\\"Duplicar\\\") occurrences: {len(duplicate_text)}")
for item in duplicate_text:
    print("  duplicate-text residual:", item)
print(f"Raw horizontal+vertical dp padding pairs: {len(raw_pair_padding)}")
for item in raw_pair_padding[:40]:
    print("  raw-padding residual:", item)

font_scale_refs = []
for name, text in texts.items():
    if "fontScale" in text:
        font_scale_refs.append(name)
print("fontScale reference files:", font_scale_refs)
