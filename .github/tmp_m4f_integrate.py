from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    assert count == 1, f"{label}: expected exactly one match, found {count}"
    return text.replace(old, new, 1)


# Spells: route neutral/state badges through the shared primitive and make Favorite accessible.
spell_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt')
spell = spell_path.read_text(encoding='utf-8')
spell = replace_once(
    spell,
    '''import androidx.compose.ui.platform.LocalDensity\nimport androidx.compose.ui.text.input.KeyboardType\n''',
    '''import androidx.compose.ui.platform.LocalDensity\nimport androidx.compose.ui.semantics.contentDescription\nimport androidx.compose.ui.semantics.semantics\nimport androidx.compose.ui.text.input.KeyboardType\n''',
    'spell accessibility imports',
)
spell = replace_once(
    spell,
    '''                        if (spell.verbal) SpellBadgeG2("V")\n                        if (spell.somatic) SpellBadgeG2("S")\n                        if (spell.material) SpellBadgeG2("M")\n                        if (spell.concentration) SpellBadgeG2("Conc.")\n                        if (spell.ritual) SpellBadgeG2("Ritual")\n                        if (selectedSourceId != null && selectedAssociation?.prepared == true) {\n                            SpellBadgeG2("Preparado")\n                        }\n                        if (selectedSourceId == null && spell.sourceAssociations.isNotEmpty()) {\n                            val preparedCount = spell.sourceAssociations.count { it.prepared }\n                            SpellBadgeG2("Prep. $preparedCount/${spell.sourceAssociations.size}")\n                        }\n''',
    '''                        if (spell.verbal) SpellBadgeG2("V")\n                        if (spell.somatic) SpellBadgeG2("S")\n                        if (spell.material) SpellBadgeG2("M")\n                        if (spell.concentration) SpellBadgeG2("Concentración", state = true)\n                        if (spell.ritual) SpellBadgeG2("Ritual", state = true)\n                        if (selectedSourceId != null && selectedAssociation?.prepared == true) {\n                            SpellBadgeG2("Preparado", state = true)\n                        }\n                        if (selectedSourceId == null && spell.sourceAssociations.isNotEmpty()) {\n                            val preparedCount = spell.sourceAssociations.count { it.prepared }\n                            SpellBadgeG2("Preparado $preparedCount/${spell.sourceAssociations.size}", state = true)\n                        }\n''',
    'spell semantic state badges',
)
spell = replace_once(
    spell,
    '''                        TextButton(\n                            onClick = { onFavoriteChange(!favorite) },\n                            enabled = structuralEditingEnabled && favoriteEnabled,\n                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                        ) {\n''',
    '''                        TextButton(\n                            onClick = { onFavoriteChange(!favorite) },\n                            enabled = structuralEditingEnabled && favoriteEnabled,\n                            modifier = Modifier.semantics {\n                                contentDescription = if (favorite) {\n                                    "Quitar ${spell.name} de Favoritos"\n                                } else {\n                                    "Añadir ${spell.name} a Favoritos"\n                                }\n                            },\n                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                        ) {\n''',
    'spell favorite accessibility',
)
spell = replace_once(
    spell,
    '''@Composable\nprivate fun SpellBadgeG2(label: String) {\n    Surface(\n        shape = MaterialTheme.shapes.small,\n        color = MaterialTheme.colorScheme.surfaceVariant,\n        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),\n    ) {\n        Text(\n            label,\n            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),\n            style = MaterialTheme.typography.labelSmall,\n            maxLines = 1,\n        )\n    }\n}\n''',
    '''@Composable\nprivate fun SpellBadgeG2(label: String, state: Boolean = false) {\n    CharacterSemanticBadgeV4(\n        label = label,\n        kind = if (state) CharacterSemanticBadgeKindV4.STATE else CharacterSemanticBadgeKindV4.NEUTRAL,\n    )\n}\n''',
    'spell shared badge primitive',
)
assert 'SpellBadgeG2("Concentración", state = true)' in spell
assert 'Quitar ${spell.name} de Favoritos' in spell
spell_path.write_text(spell, encoding='utf-8')


# Equipment: separate carry/equipped/attuned state from operational/location metadata.
equipment_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt')
equipment = equipment_path.read_text(encoding='utf-8')
equipment = replace_once(
    equipment,
    '''import androidx.compose.foundation.clickable\nimport androidx.compose.foundation.rememberScrollState\n''',
    '''import androidx.compose.foundation.clickable\nimport androidx.compose.foundation.horizontalScroll\nimport androidx.compose.foundation.rememberScrollState\n''',
    'equipment horizontal scroll import',
)
equipment = replace_once(
    equipment,
    '''    val carry = effectiveInventoryCarryState(item, usage)\n    val meta = buildList {\n        add(if (carry == CharacterInventoryCarryState.CARRIED) "Transportado" else "Guardado")\n        if (item.equipped) add("Equipado")\n        if (item.attuned) add("Sintonizado")\n        when (usage.kind) {\n            CharacterConsumableKind.CONSUMABLE -> add("Consumible −${usage.quickUseAmount}")\n            CharacterConsumableKind.AMMUNITION -> add("Munición −${usage.quickUseAmount}")\n            CharacterConsumableKind.NONE -> Unit\n        }\n        item.location?.takeIf { it.isNotBlank() }?.let { add(it) }\n    }\n''',
    '''    val carry = effectiveInventoryCarryState(item, usage)\n    val stateLabels = buildList {\n        add(if (carry == CharacterInventoryCarryState.CARRIED) "Transportado" else "Guardado")\n        if (item.equipped) add("Equipado")\n        if (item.attuned) add("Sintonizado")\n    }\n    val meta = buildList {\n        when (usage.kind) {\n            CharacterConsumableKind.CONSUMABLE -> add("Consumible −${usage.quickUseAmount}")\n            CharacterConsumableKind.AMMUNITION -> add("Munición −${usage.quickUseAmount}")\n            CharacterConsumableKind.NONE -> Unit\n        }\n        item.location?.takeIf { it.isNotBlank() }?.let { add(it) }\n    }\n''',
    'equipment split state metadata',
)
equipment = replace_once(
    equipment,
    '''                    Column(modifier = Modifier.weight(1f)) {\n                        Text(item.name, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)\n                        Text(\n                            meta.joinToString(" · "),\n                            style = MaterialTheme.typography.labelSmall,\n                            maxLines = 2,\n                            overflow = TextOverflow.Ellipsis,\n                        )\n                    }\n''',
    '''                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {\n                        Text(item.name, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)\n                        Row(\n                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),\n                            horizontalArrangement = Arrangement.spacedBy(4.dp),\n                        ) {\n                            stateLabels.forEach { label ->\n                                CharacterSemanticBadgeV4(\n                                    label = label,\n                                    kind = CharacterSemanticBadgeKindV4.STATE,\n                                )\n                            }\n                        }\n                        if (meta.isNotEmpty()) {\n                            Text(\n                                meta.joinToString(" · "),\n                                style = MaterialTheme.typography.labelSmall,\n                                maxLines = 2,\n                                overflow = TextOverflow.Ellipsis,\n                            )\n                        }\n                    }\n''',
    'equipment semantic state row',
)
assert 'stateLabels.forEach' in equipment
assert 'kind = CharacterSemanticBadgeKindV4.STATE' in equipment
equipment_path.write_text(equipment, encoding='utf-8')


# Management: active concentration uses the same state vocabulary/primitive.
management_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementTabV4.kt')
management = management_path.read_text(encoding='utf-8')
management = replace_once(
    management,
    '''        } else {\n            Text(concentration.name, style = MaterialTheme.typography.titleSmall)\n            concentration.notes?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }\n            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {\n''',
    '''        } else {\n            CharacterSemanticBadgeV4(\n                label = "Concentración activa",\n                kind = CharacterSemanticBadgeKindV4.STATE,\n            )\n            Text(concentration.name, style = MaterialTheme.typography.titleSmall)\n            concentration.notes?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }\n            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {\n''',
    'active concentration state badge',
)
assert 'label = "Concentración activa"' in management
management_path.write_text(management, encoding='utf-8')
