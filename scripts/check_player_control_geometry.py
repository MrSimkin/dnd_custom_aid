#!/usr/bin/env python3
from __future__ import annotations
import pathlib
import re
import sys

ROOT = pathlib.Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android')
raw_pattern = re.compile(r'(?<![A-Za-z0-9_])OutlinedTextField\s*\(')
raw_sites = []
material_imports = []
compact_calls = 0
for path in sorted(ROOT.glob('*.kt')):
    text = path.read_text(encoding='utf-8')
    for match in raw_pattern.finditer(text):
        raw_sites.append(f'{path.name}:{text.count(chr(10), 0, match.start()) + 1}')
    if 'import androidx.compose.material3.OutlinedTextField' in text:
        material_imports.append(path.name)
    compact_calls += text.count('CharacterCompactOutlinedTextFieldV4(')

errors = []
if raw_sites:
    errors.append('raw Material OutlinedTextField call sites remain: ' + ', '.join(raw_sites))
if material_imports:
    errors.append('raw Material OutlinedTextField imports remain: ' + ', '.join(material_imports))
# 160 migrated call sites plus the shared primitive declaration at the current repair boundary.
if compact_calls < 161:
    errors.append(f'expected at least 160 compact Player field usages; observed count including declaration={compact_calls}')

combat = (ROOT / 'CharacterCombatSuccessorV4.kt').read_text(encoding='utf-8')
marker = 'var signExpanded by remember { mutableStateOf(false) }'
start = combat.find(marker)
end = combat.find('DropdownMenu(', start)
if start < 0 or end < 0:
    errors.append('structured-dice sign selector boundary not found')
else:
    sign_block = combat[start:end]
    if 'CharacterCompactGlyphSelectorV4(' not in sign_block:
        errors.append('structured-dice +/- selector is not using the compact glyph selector')
    if 'OutlinedButton(' in sign_block:
        errors.append('structured-dice +/- selector regressed to raw OutlinedButton geometry')

if errors:
    for error in errors:
        print('ERROR:', error, file=sys.stderr)
    raise SystemExit(1)
print(f'Player compact-control geometry guard PASS: compactFieldCount={compact_calls - 1}; rawMaterialFields=0; diceSign=shared-compact-glyph')
