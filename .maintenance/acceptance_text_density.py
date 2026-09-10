from pathlib import Path
import re

root = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
layout = root / "CharacterLayoutV4.kt"
text = layout.read_text()
if "characterCompactTextAreaMinLinesV4" in text:
    raise SystemExit("compact text-area helper already present")
text = text.replace(
    "import androidx.compose.ui.unit.dp\n",
    "import androidx.compose.ui.unit.dp\nimport kotlin.math.roundToInt\n",
    1,
)
anchor = '''@Composable
internal fun CompactMenuSurfaceV4('''
helper = '''@Composable
internal fun characterCompactTextAreaMinLinesV4(preferredLines: Int = 2): Int {
    val preferred = preferredLines.coerceAtLeast(1)
    val spacingFraction = LocalUiPreferencesV4.current.spacingScalePercent.coerceIn(40, 100) / 100f
    return (preferred * spacingFraction).roundToInt().coerceIn(1, preferred)
}

@Composable
internal fun CompactMenuSurfaceV4('''
if text.count(anchor) != 1:
    raise SystemExit("CharacterLayout helper anchor mismatch")
layout.write_text(text.replace(anchor, helper, 1))

fixed_files = [
    "CharacterArtificeModuleV4.kt",
    "CharacterClassOptionModulesV4.kt",
    "CharacterCompanionsModuleV4.kt",
    "CharacterFormsModuleV4.kt",
    "CharacterNotesTabV4.kt",
]
removed_inline = 0
inline_pattern = re.compile(
    r"modifier = Modifier\.fillMaxWidth\(\)\.heightIn\(min = \d+\.dp, max = \d+\.dp\),"
)
for name in fixed_files:
    p = root / name
    current = p.read_text()
    current, count = inline_pattern.subn("modifier = Modifier.fillMaxWidth(),", current)
    removed_inline += count
    p.write_text(current)
if removed_inline != 10:
    raise SystemExit(f"expected 10 same-line multiline height caps, removed {removed_inline}")

# Notes has one additional large editor whose modifier chain is split across lines.
notes_path = root / "CharacterNotesTabV4.kt"
notes_text = notes_path.read_text()
notes_split_pattern = re.compile(
    r"modifier = Modifier\n\s*\.fillMaxWidth\(\)\n\s*\.heightIn\(min = 220\.dp, max = 340\.dp\),"
)
notes_text, notes_split_count = notes_split_pattern.subn(
    "modifier = Modifier.fillMaxWidth(),",
    notes_text,
    count=1,
)
if notes_split_count != 1:
    raise SystemExit(f"expected one split Notes content height cap, removed {notes_split_count}")
notes_path.write_text(notes_text)

# Two large freeform surfaces used a multiline modifier-level heightIn block.
for name, expected_min_fragment in [
    ("CharacterBackgroundTabV4.kt", "min = if (wide) 260.dp else 220.dp"),
    ("CharacterNotesTabV4.kt", "min = if (wide) 220.dp else 180.dp"),
]:
    p = root / name
    current = p.read_text()
    pattern = re.compile(
        r"modifier = Modifier\n\s*\.fillMaxWidth\(\)\n\s*\.heightIn\(\n\s*min = if \(wide\) \d+\.dp else \d+\.dp,\n\s*max = if \(wide\) \d+\.dp else \d+\.dp,\n\s*\),"
    )
    if expected_min_fragment not in current:
        raise SystemExit(f"{name}: expected large freeform height block missing")
    current, count = pattern.subn("modifier = Modifier.fillMaxWidth(),", current, count=1)
    if count != 1:
        raise SystemExit(f"{name}: failed to remove large freeform height block")
    p.write_text(current)

# Normalize unusually large line reservations first.
for p in sorted(root.glob("*.kt")):
    current = p.read_text()
    current = current.replace(
        "minLines = if (wide) 10 else 8,",
        "minLines = characterCompactTextAreaMinLinesV4(3),",
    )
    current = current.replace(
        "minLines = if (wide) 9 else 7,",
        "minLines = characterCompactTextAreaMinLinesV4(3),",
    )
    current = current.replace(
        "maxLines = if (wide) 20 else 16,",
        "maxLines = 10,",
    )
    current = current.replace(
        "maxLines = if (wide) 18 else 14,",
        "maxLines = 10,",
    )
    current = current.replace("maxLines = 16,", "maxLines = 8,")
    current = current.replace("maxLines = 14,", "maxLines = 8,")
    p.write_text(current)

# Make every explicit multiline minimum respond to Espacios. Kotlin call sites
# use both `minLines = 2,` and inline `minLines = 2)` forms, so match the
# numeric value without consuming either delimiter. A one-line field stays one.
converted = 0
numeric_min = re.compile(r"minLines = (\d+)(?=,|\))")
for p in sorted(root.glob("*.kt")):
    current = p.read_text()

    def repl(match):
        value = int(match.group(1))
        if value < 2:
            return match.group(0)
        preferred = 2 if value <= 3 else 3
        return f"minLines = characterCompactTextAreaMinLinesV4({preferred})"

    updated, count = numeric_min.subn(repl, current)
    converted += count
    p.write_text(updated)
if converted < 45:
    raise SystemExit(f"expected broad multiline conversion, converted only {converted}")

# Guard the high-value owner complaints explicitly.
background = (root / "CharacterBackgroundTabV4.kt").read_text()
notes = (root / "CharacterNotesTabV4.kt").read_text()
for forbidden in ("260.dp else 220.dp", "420.dp else 360.dp", "minLines = 7,", "maxLines = 14,"):
    if forbidden in background:
        raise SystemExit(f"Background residual fixed text-area geometry: {forbidden}")
for forbidden in ("220.dp else 180.dp", "380.dp else 300.dp", "heightIn(min = 220.dp, max = 340.dp)", "minLines = 8,", "maxLines = 16,"):
    if forbidden in notes:
        raise SystemExit(f"Notes residual fixed text-area geometry: {forbidden}")

# Ensure no old fixed multiline min/max height pair remains in the audited modules.
for name in fixed_files:
    current = (root / name).read_text()
    if re.search(r"fillMaxWidth\(\)\.heightIn\(min = (?:90|110|120|130|220)\.dp, max =", current):
        raise SystemExit(f"{name}: residual hard multiline height")
