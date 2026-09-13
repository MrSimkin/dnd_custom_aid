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
dice_start = combat.find('private fun DiceDamageFieldsV4(')
dice_end = combat.find('@Composable\nprivate fun FlatDamageFieldsV4(', dice_start)
if dice_start < 0 or dice_end < 0:
    errors.append('structured-dice editor boundary not found')
else:
    dice_block = combat[dice_start:dice_end]
    if 'CharacterCompactGlyphSelectorV4(' not in dice_block:
        errors.append('structured-dice +/- selector is not using the compact glyph selector')
    if 'modifierSign = nextDiceModifierSignV4(parsed)' not in dice_block:
        errors.append('structured-dice sign selector is not a direct sign toggle')
    if 'value = parsed.modifierMagnitude' not in dice_block:
        errors.append('structured-dice modifier is not kept in an independent magnitude field')
    if 'signExpanded' in dice_block:
        errors.append('structured-dice sign selector regressed to dropdown state')

build_start = combat.find('private fun buildDiceComponentExpressionV4(')
build_end = combat.find('private fun nextDiceModifierSignV4(', build_start)
if build_start < 0 or build_end < 0:
    errors.append('structured-dice serializer boundary not found')
else:
    build_block = combat[build_start:build_end]
    if 'append(draft.modifierSign.token)' not in build_block or 'append(draft.modifierMagnitude)' not in build_block:
        errors.append('structured-dice serializer no longer emits an explicit signed modifier token')

if errors:
    for error in errors:
        print('ERROR:', error, file=sys.stderr)
    raise SystemExit(1)
print(f'Player compact-control geometry guard PASS: compactFieldCount={compact_calls - 1}; rawMaterialFields=0; diceSign=shared-direct-compact-glyph; diceModifier=independent-signed-field')
