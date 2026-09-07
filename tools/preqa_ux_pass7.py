from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android"


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one anchor, found {count}")
    return text.replace(old, new, 1)


# Review identity.
gradle_path = ROOT / "androidApp/build.gradle.kts"
gradle = gradle_path.read_text(encoding="utf-8")
gradle = replace_once(gradle, "versionCode = 40600", "versionCode = 40700", "versionCode")
gradle = replace_once(
    gradle,
    'versionName = "0.4.0-preqa.6"',
    'versionName = "0.4.0-preqa.7"',
    "versionName",
)
gradle_path.write_text(gradle, encoding="utf-8")

# Final unambiguous whitespace sweep: Arrangement.spacedBy only changes the gap between children.
# It does not change child min sizes, editor working dimensions, icon sizes or touch-target geometry.
total_replacements = 0
changed_files = []
pattern = re.compile(r"Arrangement\.spacedBy\((\d+)\.dp\)")
for path in sorted(ANDROID.glob("*.kt")):
    text = path.read_text(encoding="utf-8")
    updated, count = pattern.subn(r"Arrangement.spacedBy(appSpacingV4(\1.dp))", text)
    if count:
        path.write_text(updated, encoding="utf-8")
        total_replacements += count
        changed_files.append((path.name, count))

if total_replacements < 80:
    raise RuntimeError(f"Residual spacing audit drift: expected at least 80 raw Arrangement gaps, found {total_replacements}")

# The final product should have no numeric raw Arrangement.spacedBy literals in the Android UI package.
remaining = []
for path in sorted(ANDROID.glob("*.kt")):
    text = path.read_text(encoding="utf-8")
    if pattern.search(text):
        remaining.append(path.name)
if remaining:
    raise RuntimeError(f"Raw Arrangement spacing remains: {remaining}")

# Preserve the intentional geometry/touch boundary: do not mass-convert Modifier.padding or PaddingValues.
# Representative intrinsic/control anchors must remain untouched.
editor = (ANDROID / "CharacterEditorV4.kt").read_text(encoding="utf-8")
if "Canvas(modifier = Modifier.padding(9.dp))" not in editor:
    raise RuntimeError("CharacterEditor drawing geometry anchor changed unexpectedly")
semantic = (ANDROID / "CharacterSemanticBadgesV4.kt").read_text(encoding="utf-8")
if "modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)" not in semantic:
    raise RuntimeError("Semantic badge intrinsic padding anchor changed unexpectedly")

# Preserve the Pass 06 readability-cap decisions.
equipment = (ANDROID / "CharacterEquipmentClosureV4.kt").read_text(encoding="utf-8")
for anchor in [
    "phoneMax = if (special) 2 else 3,",
    "wideMax = if (special) 3 else 5,",
    "val columns = if (wide) 6 else 3",
]:
    if anchor not in equipment:
        raise RuntimeError(f"Equipment readability anchor missing: {anchor}")
for filename, anchor in [
    ("CharacterCombatTabV4.kt", "constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)"),
    ("CharacterTraitsClosureV4.kt", "constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)"),
    ("CharacterNotesTabV4.kt", "constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)"),
]:
    if anchor not in (ANDROID / filename).read_text(encoding="utf-8"):
        raise RuntimeError(f"Readability cap drift in {filename}")

print(f"Pass 07 transformed {total_replacements} residual Arrangement gaps across {len(changed_files)} files.")
for name, count in changed_files:
    print(f"  {name}: {count}")
