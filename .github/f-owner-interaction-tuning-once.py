from pathlib import Path


def replace_once(path, old, new):
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f'guard failed for {path}: expected one occurrence, got {count}')
    p.write_text(text.replace(old, new, 1))

collection = 'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCollectionPrimitivesV4.kt'
replace_once(collection,
'''import androidx.compose.foundation.layout.padding\n''',
'''import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.layout.widthIn\n''')
replace_once(collection,
'''private fun CharacterToolbarChipV4(\n    text: String,\n    selected: Boolean,\n    onClick: () -> Unit,\n) {\n    Surface(\n        modifier = Modifier\n            .heightIn(min = 34.dp)\n''',
'''private fun CharacterToolbarChipV4(\n    text: String,\n    selected: Boolean,\n    onClick: () -> Unit,\n    modifier: Modifier = Modifier,\n) {\n    Surface(\n        modifier = modifier\n            .heightIn(min = 34.dp)\n''')
replace_once(collection,
'''                if (onAdd != null) {\n                    CharacterToolbarChipV4(text = "+", selected = false, onClick = onAdd)\n                }\n''',
'''                if (onAdd != null) {\n                    CharacterToolbarChipV4(\n                        text = "+",\n                        selected = false,\n                        onClick = onAdd,\n                        modifier = Modifier.widthIn(min = 38.dp),\n                    )\n                }\n''')

spells = 'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt'
replace_once(spells,
'''import androidx.compose.foundation.BorderStroke\n''',
'''import androidx.compose.animation.core.tween\nimport androidx.compose.foundation.BorderStroke\n''')
replace_once(spells,
'''                    SpellRowG2(\n                        spell = spell,\n''',
'''                    SpellRowG2(\n                        spell = spell,\n                        modifier = Modifier.animateItem(\n                            fadeInSpec = null,\n                            placementSpec = tween(durationMillis = 75),\n                            fadeOutSpec = null,\n                        ),\n''')
replace_once(spells,
'''private fun SpellRowG2(\n    spell: CharacterSpell,\n    sourceById: Map<Uuid, CharacterSpellcastingSource>,\n''',
'''private fun SpellRowG2(\n    spell: CharacterSpell,\n    modifier: Modifier = Modifier,\n    sourceById: Map<Uuid, CharacterSpellcastingSource>,\n''')
replace_once(spells,
'''    Column(modifier = Modifier.fillMaxWidth()) {\n        CharacterDropIndicatorV4(visible = dragState.showDropBefore)\n''',
'''    Column(modifier = modifier.fillMaxWidth()) {\n        CharacterDropIndicatorV4(visible = dragState.showDropBefore)\n''')
replace_once(spells,
'''                    onMove = onMove,\n                    onVisualStateChange = { state ->\n''',
'''                    onMove = onMove,\n                    onVisualStateChange = { state ->\n''')
# Insert a spell-scoped threshold without changing the shared default used by other surfaces.
needle = '''                    onVisualStateChange = { state ->\n                        dragging = state.active\n                        accumulatedDrag = state.offsetY\n                    },\n                )\n                .characterDragFeedbackV4(dragState)\n'''
replacement = '''                    onVisualStateChange = { state ->\n                        dragging = state.active\n                        accumulatedDrag = state.offsetY\n                    },\n                    thresholdFraction = 0.65f,\n                )\n                .characterDragFeedbackV4(dragState)\n'''
replace_once(spells, needle, replacement)

print('Owner interaction tuning staged: wider add, spell threshold 0.65, 75ms placement')
