from pathlib import Path
import subprocess

root = Path('.')
android = root / 'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android'
editor_path = android / 'CharacterEditorV4.kt'

owners = {
    'CharacterEquipmentTabV4': android / 'CharacterEquipmentTabV4.kt',
    'CharacterTraitsTabV4': android / 'CharacterTraitsTabV4.kt',
    'CharacterSpellListV4': android / 'CharacterSpellListV4.kt',
    'CharacterDomainShellV4': android / 'CharacterDomainShellsV4.kt',
    'StatusSelectorV4': editor_path,
    'ClassesCardV4': editor_path,
    'ClassRowV4': editor_path,
    'ClassSelectorV4': editor_path,
    'HitDieSelectorV4': editor_path,
    'classNamesV4': editor_path,
}

def grep(symbol: str):
    result = subprocess.run(['git', 'grep', '-n', '-F', symbol, '--', '*.kt'], text=True, capture_output=True)
    if result.returncode not in (0, 1):
        raise SystemExit(result.stderr)
    return [line for line in result.stdout.splitlines() if line.strip()]

# Fail closed: each obsolete top-level symbol may occur only in its owning obsolete source.
for symbol, owner in owners.items():
    refs = grep(symbol)
    external = [line for line in refs if not line.startswith(str(owner) + ':')]
    if external:
        raise SystemExit(f'Obsolete symbol {symbol} still has external references:\n' + '\n'.join(external))

# Exact source cleanup.
for name in ['CharacterEquipmentTabV4.kt', 'CharacterTraitsTabV4.kt', 'CharacterSpellListV4.kt', 'CharacterDomainShellsV4.kt']:
    path = android / name
    if not path.exists():
        raise SystemExit(f'Expected obsolete file missing before cleanup: {path}')
    path.unlink()

text = editor_path.read_text()
class_start = text.index('private val classNamesV4 = listOf(\n')
class_end = text.index('\n)\n\n', class_start) + len('\n)\n\n')
text = text[:class_start] + text[class_end:]

dead_start = text.index('@Composable\nprivate fun StatusSelectorV4(')
live_marker = '@Composable\nprivate fun AbilitiesCardV4('
dead_end = text.index(live_marker, dead_start)
text = text[:dead_start] + text[dead_end:]
editor_path.write_text(text)

# Re-audit the six M4 closures on reachable current surfaces.
def require(path: Path, needles):
    data = path.read_text()
    for needle in needles:
        if needle not in data:
            raise SystemExit(f'M4 traceability regression: {needle!r} missing from {path}')

require(editor_path, ['proficiencyDraftJson', 'CharacterProficienciesCardV4(', 'proficiencies = proficiencies'])
require(android / 'CharacterManagementTabV4.kt', ['CharacterQuickAccessKind.RESOURCE', 'setCharacterQuickAccessFavorite', 'favoriteResourceIds'])
require(android / 'CharacterUi.kt', ['characterListClassSummary(', 'characterListFreshnessLabel(', 'portraitRef'])
require(android / 'UiPreferences.kt', ['SettingsSheetPreview(', 'Alyra Voss', 'Texto '])
require(android / 'CharacterClassIdentityV4.kt', ['CharacterSemanticBadgeV4(', 'CharacterSemanticBadgeKindV4.RULES', 'CharacterSemanticBadgeKindV4.SOURCE'])
require(android / 'CharacterSpellListClosureV4.kt', ['CharacterSemanticBadgeKindV4.STATE', 'contentDescription'])
require(android / 'CharacterEquipmentClosureV4.kt', ['CharacterSemanticBadgeKindV4.STATE', 'Transportado', 'Sintonizado'])
require(android / 'CharacterManagementTabV4.kt', ['CharacterSemanticBadgeKindV4.STATE', 'Concentración activa'])

# The owner-lineage migration regression must remain in the repository and is covered by desktopTest below.
lineage = list((root / 'shared/src').rglob('*CharacterOwnerLineageMigrationTest*'))
if not lineage:
    # fallback: class name may live in a differently named file
    found = subprocess.run(['git', 'grep', '-l', '-F', 'CharacterOwnerLineageMigrationTest', '--', 'shared/src/**'], text=True, capture_output=True)
    if found.returncode not in (0, 1) or not found.stdout.strip():
        raise SystemExit('CharacterOwnerLineageMigrationTest not found')

# Obsolete symbols/files must now be absent.
for symbol in owners:
    if grep(symbol):
        raise SystemExit(f'Obsolete symbol survived cleanup: {symbol}')

print('M5 bounded cleanup and M4 traceability assertions PASS')
