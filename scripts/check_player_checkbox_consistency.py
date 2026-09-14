#!/usr/bin/env python3
from __future__ import annotations

import pathlib
import re
import sys

ROOT = pathlib.Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android')
PRIMITIVE = 'CharacterCheckboxPrimitivesV4.kt'
RAW_IMPORT = 'import androidx.compose.material3.Checkbox'
RAW_CALL = re.compile(r'(?<![A-Za-z0-9_])Checkbox\s*\(')
SWITCH_CALL = re.compile(r'(?<![A-Za-z0-9_])Switch\s*\(')
TRISTATE_CALL = re.compile(r'(?<![A-Za-z0-9_])TriStateCheckbox\s*\(')

raw_imports: list[str] = []
raw_calls: list[str] = []
switch_calls: list[str] = []
tristate_calls: list[str] = []
for path in sorted(ROOT.glob('*.kt')):
    text = path.read_text(encoding='utf-8')
    if path.name != PRIMITIVE:
        if RAW_IMPORT in text:
            raw_imports.append(path.name)
        for match in RAW_CALL.finditer(text):
            raw_calls.append(f'{path.name}:{text.count(chr(10), 0, match.start()) + 1}')
    for match in SWITCH_CALL.finditer(text):
        switch_calls.append(f'{path.name}:{text.count(chr(10), 0, match.start()) + 1}')
    for match in TRISTATE_CALL.finditer(text):
        tristate_calls.append(f'{path.name}:{text.count(chr(10), 0, match.start()) + 1}')

errors: list[str] = []
if raw_imports:
    errors.append('raw Material Checkbox imports remain outside shared primitive: ' + ', '.join(raw_imports))
if raw_calls:
    errors.append('raw Material Checkbox call sites remain outside shared primitive: ' + ', '.join(raw_calls))

primitive = (ROOT / PRIMITIVE).read_text(encoding='utf-8')
for marker in (
    'CharacterCompactCheckboxItemV4(',
    'CharacterCompactCheckboxV4(',
    'CharacterCompactCheckboxPairV4(',
    'CharacterResponsiveCheckboxGroupV4(',
    'LocalMinimumInteractiveComponentSize provides 0.dp',
    '.heightIn(min = 48.dp)',
    '.sizeIn(minWidth = 48.dp, minHeight = 48.dp)',
):
    if marker not in primitive:
        errors.append(f'shared checkbox primitive lost required marker: {marker}')

spell = (ROOT / 'CharacterSpellListClosureV4.kt').read_text(encoding='utf-8')
if spell.count('CharacterResponsiveCheckboxGroupV4') < 2:
    errors.append('spell editor no longer proves responsive checkbox packing for source/component groups')
if 'CharacterCompactCheckboxPairV4(' not in spell:
    errors.append('spell source/prepared controls are no longer kept as a semantic checkbox pair')
for label in ('"V"', '"S"', '"M"', '"Concentración"', '"Ritual"'):
    if label not in spell:
        errors.append(f'spell editor lost expected checkbox label {label}')

equipment = (ROOT / 'CharacterEquipmentClosureV4.kt').read_text(encoding='utf-8')
if equipment.count('CharacterResponsiveCheckboxGroupV4') < 2:
    errors.append('equipment editor no longer proves responsive checkbox grouping in both editor presentations')
for label in ('"Equipado"', '"Equipo especial"', '"Sintonizado"'):
    if label not in equipment:
        errors.append(f'equipment editor lost expected checkbox label {label}')

for filename in ('CharacterManagementSuccessorV4.kt', 'CharacterManagementTabV4.kt'):
    management = (ROOT / filename).read_text(encoding='utf-8')
    if 'CharacterCompactCheckboxV4(' not in management:
        errors.append(f'{filename} rest preview is not using the shared icon-only checkbox primitive')

if errors:
    for error in errors:
        print('ERROR:', error, file=sys.stderr)
    raise SystemExit(1)

shared_item_calls = sum(
    path.read_text(encoding='utf-8').count('CharacterCompactCheckboxItemV4(')
    for path in ROOT.glob('*.kt')
)
print(
    'Player checkbox consistency guard PASS: '
    f'rawMaterialCheckboxes=0; sharedItemReferences={shared_item_calls}; '
    'spellPacking=responsive; equipmentPacking=responsive; managementRestSelectors=shared'
)
print(
    'Related toggle audit (informational; Switch/TriState are not blanket-migrated): '
    f'Switch={len(switch_calls)} [{", ".join(switch_calls) or "none"}]; '
    f'TriStateCheckbox={len(tristate_calls)} [{", ".join(tristate_calls) or "none"}]'
)
