from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
text = path.read_text()

import_line = "import androidx.compose.ui.text.input.KeyboardType\n"
if text.count(import_line) != 1:
    raise SystemExit(f"KeyboardType import: expected 1, found {text.count(import_line)}")
text = text.replace(
    import_line,
    "import androidx.compose.ui.text.font.FontStyle\n" + import_line,
    1,
)

lines = text.splitlines()
markers = [i for i, line in enumerate(lines) if '"${row.label} ($abbreviation)"' in line]
if len(markers) != 1:
    raise SystemExit(f"custom skill label marker: expected 1, found {len(markers)}")
marker = markers[0]
style_matches = [
    i for i in range(marker + 1, min(marker + 6, len(lines)))
    if lines[i].strip() == "style = MaterialTheme.typography.bodySmall,"
]
if len(style_matches) != 1:
    raise SystemExit(f"custom skill style: expected 1 near marker, found {len(style_matches)}")
lines[style_matches[0]] = lines[style_matches[0]].replace(
    "style = MaterialTheme.typography.bodySmall,",
    "style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),",
)
path.write_text("\n".join(lines) + "\n")
