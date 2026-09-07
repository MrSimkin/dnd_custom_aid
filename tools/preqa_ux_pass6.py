from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, text: str) -> None:
    (ROOT / path).write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one anchor, found {count}")
    return text.replace(old, new, 1)


def scale_layout_spacing(path: str, minimum_arrangement_replacements: int = 0) -> None:
    text = read(path)

    # Arrangement gaps are pure layout spacing; minimum sizes and working dimensions are untouched.
    text, arrangement_count = re.subn(
        r"Arrangement\.spacedBy\((\d+)\.dp\)",
        r"Arrangement.spacedBy(appSpacingV4(\1.dp))",
        text,
    )
    if arrangement_count < minimum_arrangement_replacements:
        raise RuntimeError(
            f"{path}: expected at least {minimum_arrangement_replacements} raw Arrangement gaps, found {arrangement_count}"
        )

    # Scale container Modifier.padding forms only. PaddingValues (often control internals) are handled explicitly below.
    text = re.sub(
        r"\.padding\(\s*horizontal = (\d+)\.dp,\s*vertical = (\d+)\.dp,?\s*\)",
        r".padding(horizontal = appSpacingV4(\1.dp), vertical = appSpacingV4(\2.dp))",
        text,
    )
    text = re.sub(
        r"\.padding\((\d+)\.dp\)",
        r".padding(appSpacingV4(\1.dp))",
        text,
    )
    text = re.sub(
        r"\.padding\(top = (\d+)\.dp\)",
        r".padding(top = appSpacingV4(\1.dp))",
        text,
    )

    write(path, text)


# Review identity.
gradle_path = "androidApp/build.gradle.kts"
gradle = read(gradle_path)
gradle = replace_once(gradle, "versionCode = 40500", "versionCode = 40600", "versionCode")
gradle = replace_once(
    gradle,
    'versionName = "0.4.0-preqa.5"',
    'versionName = "0.4.0-preqa.6"',
    "versionName",
)
write(gradle_path, gradle)

# Column-maxima audit result: only ordinary Equipment is dense enough to safely honor one more wide column.
equipment_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt"
equipment = read(equipment_path)
equipment = replace_once(
    equipment,
    "wideMax = if (special) 3 else 4,",
    "wideMax = if (special) 3 else 5,",
    "ordinary equipment wide column cap",
)
write(equipment_path, equipment)

# Residual spacing propagation on the audited character surfaces. This intentionally does not touch
# heightIn/width/size/minLines/maxLines or other intrinsic/editor/touch dimensions.
scale_layout_spacing(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt",
    minimum_arrangement_replacements=8,
)
scale_layout_spacing(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt",
    minimum_arrangement_replacements=8,
)
scale_layout_spacing(equipment_path, minimum_arrangement_replacements=10)
scale_layout_spacing(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt",
    minimum_arrangement_replacements=2,
)
scale_layout_spacing(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSupercompactV4.kt",
    minimum_arrangement_replacements=10,
)

# Main Combat list margins were still outside the spacing scale.
combat_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt"
combat = read(combat_path)
combat = replace_once(
    combat,
    """            contentPadding = PaddingValues(\n                start = if (wide) 10.dp else 5.dp,\n                end = if (wide) 10.dp else 5.dp,\n                top = 0.dp,\n                bottom = 88.dp,\n            ),""",
    """            contentPadding = PaddingValues(\n                start = appSpacingV4(if (wide) 10.dp else 5.dp),\n                end = appSpacingV4(if (wide) 10.dp else 5.dp),\n                top = 0.dp,\n                bottom = appSpacingV4(88.dp),\n            ),""",
    "Combat list content padding",
)
write(combat_path, combat)

# Supercompact is deliberately dense, but its whitespace should still obey the global spacing preference.
super_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSupercompactV4.kt"
super_text = read(super_path)
super_text = replace_once(
    super_text,
    "contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),",
    "contentPadding = PaddingValues(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(8.dp)),",
    "Supercompact list content padding",
)
write(super_path, super_text)

# Compact menu keeps its minimum 34dp target but its interior whitespace follows the preference.
layout_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterLayoutV4.kt"
layout = read(layout_path)
layout = replace_once(
    layout,
    "modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 4.dp),",
    "modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(3.dp), vertical = appSpacingV4(4.dp)),",
    "CompactMenuSurfaceV4 padding",
)
write(layout_path, layout)

# Fail closed on the conclusions of the audit: rich-card caps stay intact and specialized grids stay specialized.
checks = {
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt": [
        "constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)",
        ".imePadding()",
    ],
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt": [
        "constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)",
        'stickyHeader(key = "traits-tools")',
        ".imePadding()",
    ],
    equipment_path: [
        "phoneMax = if (special) 2 else 3,",
        "wideMax = if (special) 3 else 5,",
        'stickyHeader(key = "equipment-tools")',
        "val columns = if (wide) 6 else 3",
    ],
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt": [
        "constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)",
        ".imePadding()",
    ],
    super_path: [
        "maxWidth >= 1100.dp -> 7",
        "columns.coerceAtMost(6)",
    ],
}
for path, anchors in checks.items():
    text = read(path)
    for anchor in anchors:
        if anchor not in text:
            raise RuntimeError(f"{path}: missing preserved audit anchor: {anchor}")

# Raw Arrangement.spacedBy numeric literals should be gone from the deliberately focused surfaces.
for path in [
    combat_path,
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt",
    equipment_path,
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt",
    super_path,
]:
    if re.search(r"Arrangement\.spacedBy\(\d+\.dp\)", read(path)):
        raise RuntimeError(f"{path}: raw Arrangement spacing remains after Pass 06 transform")

print("Pass 06 transform applied successfully.")
