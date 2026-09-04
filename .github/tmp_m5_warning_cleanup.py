from pathlib import Path
import subprocess

root = Path('.')
android = root / 'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android'

replacements = {
    android / 'CharacterClassIdentityV4.kt': [
        ('existing?.subclassCatalogKey != null -> SubclassIdentityModeV4.OFFICIAL',
         'existing.subclassCatalogKey != null -> SubclassIdentityModeV4.OFFICIAL'),
    ],
    android / 'CharacterEquipmentClosureV4.kt': [
        ('quantity = parsedEditorQuantity ?: 0,', 'quantity = parsedEditorQuantity,'),
    ],
    android / 'CharacterNavigationV4.kt': [
        ('import androidx.compose.material3.ScrollableTabRow', 'import androidx.compose.material3.PrimaryScrollableTabRow'),
        ('    ScrollableTabRow(\n', '    PrimaryScrollableTabRow(\n'),
    ],
    android / 'CharacterSpellListClosureV4.kt': [
        ('if (selectedAssociation != null && selectedSourceId != null) {', 'if (selectedAssociation != null) {'),
    ],
}

for path, pairs in replacements.items():
    text = path.read_text()
    for old, new in pairs:
        count = text.count(old)
        if count != 1:
            raise SystemExit(f'Expected exactly one occurrence of {old!r} in {path}; found {count}')
        text = text.replace(old, new)
    path.write_text(text)

lock = root / 'backend/package-lock.json'
# This lockfile was generated incidentally by the M5 gate; prove it did not belong to the M4 base.
probe = subprocess.run(['git', 'cat-file', '-e', '87e873fa7b9cdcb5c61b1d4ac78a71dd5ad380c2:backend/package-lock.json'])
if probe.returncode == 0:
    raise SystemExit('backend/package-lock.json existed in authoritative M4 base; refusing to delete it')
if not lock.exists():
    raise SystemExit('Expected incidental backend/package-lock.json is missing')
lock.unlink()

print('M5 warning cleanup patch PASS')
