#!/usr/bin/env python3
from __future__ import annotations

import pathlib
import re

ROOT = pathlib.Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android')
FILES = {
    'equipment': ROOT / 'CharacterEquipmentClosureV4.kt',
    'spells': ROOT / 'CharacterSpellListClosureV4.kt',
    'companions': ROOT / 'CharacterCompanionsModuleV4.kt',
    'class_options': ROOT / 'CharacterClassOptionModulesV4.kt',
    'artifice': ROOT / 'CharacterArtificeModuleV4.kt',
    'management_successor': ROOT / 'CharacterManagementSuccessorV4.kt',
    'management_legacy': ROOT / 'CharacterManagementTabV4.kt',
}
RAW_IMPORT = 'import androidx.compose.material3.Checkbox\n'
RAW_CALL = re.compile(r'(?<![A-Za-z0-9_])Checkbox\s*\(')


def sub_required(text: str, pattern: str, replacement: str, expected: int, *, flags: int = re.MULTILINE | re.DOTALL) -> str:
    updated, count = re.subn(pattern, replacement, text, flags=flags)
    if count != expected:
        raise RuntimeError(f'expected {expected} replacements for pattern, observed {count}: {pattern[:100]}')
    return updated


def migrate_equipment(text: str) -> str:
    text = sub_required(
        text,
        r'''(?P<indent>^[ \t]*)Row\(verticalAlignment = Alignment\.CenterVertically\) \{\s*
[ \t]*Checkbox\(checked = equipped, onCheckedChange = onEquippedChange\)\s*
[ \t]*Text\("Equipado"\)\s*
[ \t]*Checkbox\(checked = special, onCheckedChange = onSpecialChange\)\s*
[ \t]*Text\("(?P<special_label>Especial|Equipo especial)"\)\s*
[ \t]*\}''',
        r'''\g<indent>CharacterResponsiveCheckboxGroupV4 {
\g<indent>    CharacterCompactCheckboxItemV4(
\g<indent>        checked = equipped,
\g<indent>        onCheckedChange = onEquippedChange,
\g<indent>        label = "Equipado",
\g<indent>    )
\g<indent>    CharacterCompactCheckboxItemV4(
\g<indent>        checked = special,
\g<indent>        onCheckedChange = onSpecialChange,
\g<indent>        label = "\g<special_label>",
\g<indent>    )
\g<indent>}''',
        2,
    )
    text = sub_required(
        text,
        r'''(?P<indent>^[ \t]*)Row\(verticalAlignment = Alignment\.CenterVertically\) \{\s*
[ \t]*Checkbox\(checked = attuned, onCheckedChange = onAttunedChange\)\s*
[ \t]*Text\("Sintonizado"\)\s*
[ \t]*\}''',
        r'''\g<indent>CharacterCompactCheckboxItemV4(
\g<indent>    checked = attuned,
\g<indent>    onCheckedChange = onAttunedChange,
\g<indent>    label = "Sintonizado",
\g<indent>)''',
        2,
    )
    return text


def migrate_spells(text: str) -> str:
    text = sub_required(
        text,
        r'''Checkbox\(\s*checked = selectedAssociation\.prepared,\s*enabled = structuralEditingEnabled && !lifted,\s*onCheckedChange = onPreparedChange,\s*\)\s*Text\("Prep\.", style = MaterialTheme\.typography\.labelSmall\)''',
        '''CharacterCompactCheckboxItemV4(
                            checked = selectedAssociation.prepared,
                            enabled = structuralEditingEnabled && !lifted,
                            onCheckedChange = onPreparedChange,
                            label = "Prep.",
                        )''',
        1,
    )
    text = sub_required(
        text,
        r'''sources\.forEach \{ source ->\s*
[ \t]*val key = source\.id\.toString\(\)\s*
[ \t]*val included = key in associatedSourceIds\s*
[ \t]*Row\(modifier = Modifier\.fillMaxWidth\(\), verticalAlignment = Alignment\.CenterVertically\) \{\s*
[ \t]*Checkbox\(checked = included, onCheckedChange = \{ onAssociationChange\(source\.id, it\) \}\)\s*
[ \t]*Text\(source\.name, modifier = Modifier\.weight\(1f\)\)\s*
[ \t]*Checkbox\(\s*checked = key in preparedSourceIds,\s*enabled = included,\s*onCheckedChange = \{ onPreparedChange\(source\.id, it\) \},\s*\)\s*
[ \t]*Text\("Preparado", style = MaterialTheme\.typography\.labelSmall\)\s*
[ \t]*\}\s*
[ \t]*\}''',
        '''CharacterResponsiveCheckboxGroupV4(modifier = Modifier.fillMaxWidth()) {
        sources.forEach { source ->
            val key = source.id.toString()
            val included = key in associatedSourceIds
            CharacterCompactCheckboxPairV4(
                firstChecked = included,
                firstOnCheckedChange = { onAssociationChange(source.id, it) },
                firstLabel = source.name,
                secondChecked = key in preparedSourceIds,
                secondOnCheckedChange = { onPreparedChange(source.id, it) },
                secondLabel = "Preparado",
                secondEnabled = included,
            )
        }
    }''',
        1,
    )
    text = sub_required(
        text,
        r'''Row\(verticalAlignment = Alignment\.CenterVertically\) \{\s*
[ \t]*Checkbox\(verbal, onVerbalChange\); Text\("V"\)\s*
[ \t]*Checkbox\(somatic, onSomaticChange\); Text\("S"\)\s*
[ \t]*Checkbox\(material, onMaterialChange\); Text\("M"\)\s*
[ \t]*\}''',
        '''CharacterResponsiveCheckboxGroupV4(modifier = Modifier.fillMaxWidth()) {
        CharacterCompactCheckboxItemV4(verbal, onVerbalChange, "V")
        CharacterCompactCheckboxItemV4(somatic, onSomaticChange, "S")
        CharacterCompactCheckboxItemV4(material, onMaterialChange, "M")
        CharacterCompactCheckboxItemV4(concentration, onConcentrationChange, "Concentración")
        CharacterCompactCheckboxItemV4(ritual, onRitualChange, "Ritual")
    }''',
        1,
    )
    text = sub_required(
        text,
        r'''\s*Row\(verticalAlignment = Alignment\.CenterVertically\) \{\s*
[ \t]*Checkbox\(concentration, onConcentrationChange\); Text\("Concentración"\)\s*
[ \t]*Checkbox\(ritual, onRitualChange\); Text\("Ritual"\)\s*
[ \t]*\}\s*''',
        '\n',
        1,
    )
    return text


def migrate_active_row(text: str, label: str) -> str:
    escaped = re.escape(label)
    pattern = rf'''(?P<indent>^[ \t]*)Row\(\s*(?:modifier = Modifier\.fillMaxWidth\(\),\s*)?verticalAlignment = Alignment\.CenterVertically,?\s*\) \{{\s*
[ \t]*Checkbox\(checked = active, onCheckedChange = onActiveChange\)\s*
[ \t]*Text\("{escaped}"\)\s*
[ \t]*\}}'''
    replacement = (
        r'''\g<indent>CharacterCompactCheckboxItemV4(''' + '\n' +
        r'''\g<indent>    checked = active,'''+ '\n' +
        r'''\g<indent>    onCheckedChange = onActiveChange,'''+ '\n' +
        rf'''\g<indent>    label = "{label}",'''+ '\n' +
        r'''\g<indent>    modifier = Modifier.fillMaxWidth(),'''+ '\n' +
        r'''\g<indent>)'''
    )
    return sub_required(text, pattern, replacement, 1)


def migrate_management_successor(text: str) -> str:
    return sub_required(
        text,
        r'''Checkbox\(\s*checked = key in selectedKeys,\s*onCheckedChange = \{ checked ->\s*onSelectedKeysChange\(if \(checked\) selectedKeys \+ key else selectedKeys - key\)\s*\},\s*\)''',
        '''CharacterCompactCheckboxV4(
                checked = key in selectedKeys,
                onCheckedChange = { checked ->
                    onSelectedKeysChange(if (checked) selectedKeys + key else selectedKeys - key)
                },
            )''',
        1,
    )


def migrate_management_legacy(text: str) -> str:
    return sub_required(
        text,
        r'''Checkbox\(\s*checked = item\.resourceId\.toString\(\) in selected,\s*onCheckedChange = \{ checked ->\s*selected = if \(checked\) selected \+ item\.resourceId\.toString\(\) else selected - item\.resourceId\.toString\(\)\s*\},\s*\)''',
        '''CharacterCompactCheckboxV4(
                            checked = item.resourceId.toString() in selected,
                            onCheckedChange = { checked ->
                                selected = if (checked) selected + item.resourceId.toString() else selected - item.resourceId.toString()
                            },
                        )''',
        1,
    )


def main() -> None:
    original = {name: path.read_text(encoding='utf-8') for name, path in FILES.items()}
    raw_before = sum(len(RAW_CALL.findall(text)) for text in original.values())
    if raw_before == 0:
        print('Round 3 checkbox migration already applied; no changes required.')
        return
    if raw_before != 19:
        raise RuntimeError(f'expected source-complete baseline of 19 raw Checkbox calls, observed {raw_before}')

    for name, text in original.items():
        if RAW_IMPORT not in text:
            raise RuntimeError(f'{name}: expected raw Material Checkbox import at migration baseline')
        original[name] = text.replace(RAW_IMPORT, '', 1)

    updated = dict(original)
    updated['equipment'] = migrate_equipment(updated['equipment'])
    updated['spells'] = migrate_spells(updated['spells'])
    updated['companions'] = migrate_active_row(updated['companions'], 'Activo / disponible')
    updated['class_options'] = migrate_active_row(updated['class_options'], 'Activo / disponible')
    updated['artifice'] = migrate_active_row(updated['artifice'], 'Activo / creado / disponible')
    updated['management_successor'] = migrate_management_successor(updated['management_successor'])
    updated['management_legacy'] = migrate_management_legacy(updated['management_legacy'])

    raw_after = sum(len(RAW_CALL.findall(text)) for text in updated.values())
    imports_after = [name for name, text in updated.items() if RAW_IMPORT.strip() in text]
    if raw_after != 0 or imports_after:
        raise RuntimeError(f'migration incomplete: raw calls={raw_after}, raw imports={imports_after}')

    for name, path in FILES.items():
        path.write_text(updated[name], encoding='utf-8')

    print('Round 3 checkbox migration applied: rawMaterialCheckboxes 19 -> 0 across seven Player files.')


if __name__ == '__main__':
    main()
