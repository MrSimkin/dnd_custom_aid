from pathlib import Path

path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementTabV4.kt')
text = path.read_text(encoding='utf-8')


def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    assert count == 1, f'{label}: expected exactly one match, found {count}'
    text = text.replace(old, new, 1)


replace_once(
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCondition
''',
'''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCondition
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
''',
'Quick Access kind import',
)

replace_once(
'''import io.github.mrsimkin.dndcustomaid.shared.character.applySelectedResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.previewResourceRecovery
''',
'''import io.github.mrsimkin.dndcustomaid.shared.character.applySelectedResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.previewResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.pruneCharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterQuickAccessFavorite
''',
'Quick Access helper imports',
)

replace_once(
'''            ResourcesCardV4(
                resources = sheet.resources,
                structuralEditingEnabled = structuralEditingEnabled,
                onAdd = {
                    editingResourceId = null
                    resourceEditorOpen = true
                },
                onEdit = { resource ->
                    editingResourceId = resource.id.toString()
                    resourceEditorOpen = true
                },
                onDelete = { resource -> deletingResourceId = resource.id.toString() },
                onAdjust = { resource, delta ->
''',
'''            ResourcesCardV4(
                resources = sheet.resources,
                favoriteResourceIds = closureState.quickAccess
                    .filter { it.kind == CharacterQuickAccessKind.RESOURCE }
                    .mapTo(mutableSetOf()) { it.targetId },
                structuralEditingEnabled = structuralEditingEnabled,
                onAdd = {
                    editingResourceId = null
                    resourceEditorOpen = true
                },
                onEdit = { resource ->
                    editingResourceId = resource.id.toString()
                    resourceEditorOpen = true
                },
                onDelete = { resource -> deletingResourceId = resource.id.toString() },
                onFavoriteChange = { resource, favorite ->
                    if (structuralEditingEnabled) {
                        onClosureStateChange(
                            closureState.copy(
                                quickAccess = setCharacterQuickAccessFavorite(
                                    quickAccess = closureState.quickAccess,
                                    kind = CharacterQuickAccessKind.RESOURCE,
                                    targetId = resource.id,
                                    favorite = favorite,
                                ),
                            ),
                        )
                    }
                },
                onAdjust = { resource, delta ->
''',
'Resource Favorite wiring',
)

replace_once(
'''                    onClosureStateChange(
                        closureState.copy(
                            resourceRecovery = closureState.resourceRecovery.filterNot { it.resourceId == target.id },
                        ),
                    )
''',
'''                    val liveResourceIds = sheet.resources
                        .filterNot { it.id == target.id }
                        .mapTo(mutableSetOf()) { it.id }
                    onClosureStateChange(
                        closureState.copy(
                            resourceRecovery = closureState.resourceRecovery.filterNot { it.resourceId == target.id },
                            quickAccess = pruneCharacterQuickAccessKind(
                                quickAccess = closureState.quickAccess,
                                kind = CharacterQuickAccessKind.RESOURCE,
                                liveTargetIds = liveResourceIds,
                            ),
                        ),
                    )
''',
'Resource deletion Quick Access pruning',
)

replace_once(
'''private fun ResourcesCardV4(
    resources: List<CharacterResource>,
    structuralEditingEnabled: Boolean,
    onAdd: () -> Unit,
    onEdit: (CharacterResource) -> Unit,
    onDelete: (CharacterResource) -> Unit,
    onAdjust: (CharacterResource, Int) -> Unit,
) {
''',
'''private fun ResourcesCardV4(
    resources: List<CharacterResource>,
    favoriteResourceIds: Set<Uuid>,
    structuralEditingEnabled: Boolean,
    onAdd: () -> Unit,
    onEdit: (CharacterResource) -> Unit,
    onDelete: (CharacterResource) -> Unit,
    onFavoriteChange: (CharacterResource, Boolean) -> Unit,
    onAdjust: (CharacterResource, Int) -> Unit,
) {
''',
'Resource card signature',
)

replace_once(
'''                    OutlinedButton(
                        onClick = { onAdjust(resource, 1) },
                        enabled = resource.maxValue?.let { max -> resource.currentValue < max } ?: true,
                    ) { Text("+") }
                    TextButton(onClick = { onDelete(resource) }, enabled = structuralEditingEnabled) { Text("Eliminar") }
''',
'''                    OutlinedButton(
                        onClick = { onAdjust(resource, 1) },
                        enabled = resource.maxValue?.let { max -> resource.currentValue < max } ?: true,
                    ) { Text("+") }
                    val favorite = resource.id in favoriteResourceIds
                    TextButton(
                        onClick = { onFavoriteChange(resource, !favorite) },
                        enabled = structuralEditingEnabled,
                    ) {
                        Text(if (favorite) "★" else "☆")
                    }
                    TextButton(onClick = { onDelete(resource) }, enabled = structuralEditingEnabled) { Text("Eliminar") }
''',
'Resource Favorite control',
)

assert 'favoriteResourceIds = closureState.quickAccess' in text
assert 'setCharacterQuickAccessFavorite(' in text
assert 'pruneCharacterQuickAccessKind(' in text
assert 'Text(if (favorite) "★" else "☆")' in text

path.write_text(text, encoding='utf-8')
