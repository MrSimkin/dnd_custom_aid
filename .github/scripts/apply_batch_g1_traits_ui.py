from pathlib import Path

PATH = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt')
text = PATH.read_text()


def once(old: str, new: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise SystemExit(f'expected exactly one match, found {count}: {old[:160]!r}')
    text = text.replace(old, new, 1)


if 'CharacterTraitsClosureTabV4(' in text:
    raise SystemExit('G1 traits UI appears to be already wired')

once(
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n',
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n',
)

once(
    '''        stored = repository.saveCharacter(integrated)\n        closureState = closureRepository.saveState(\n            characterId,\n            closureState.copy(inventoryUsage = equipment.inventoryUsage),\n        )\n''',
    '''        stored = repository.saveCharacter(integrated)\n        val liveTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id }\n        val prunedQuickAccess = closureState.quickAccess\n            .filter { reference ->\n                reference.kind != CharacterQuickAccessKind.TRAIT || reference.targetId in liveTraitIds\n            }\n            .mapIndexed { index, reference -> reference.copy(sortOrder = index) }\n        closureState = closureRepository.saveState(\n            characterId,\n            closureState.copy(\n                inventoryUsage = equipment.inventoryUsage,\n                quickAccess = prunedQuickAccess,\n            ),\n        )\n''',
)

once(
    '''                        CharacterTabV4.TRAITS -> CharacterTraitsTabV4(\n                            traits = traitsDraft,\n                            onTraitsChange = ::updateTraits,\n                            wide = wide,\n                        )\n''',
    '''                        CharacterTabV4.TRAITS -> CharacterTraitsClosureTabV4(\n                            traits = traitsDraft,\n                            closureState = closureState,\n                            persistedTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id },\n                            onTraitsChange = ::updateTraits,\n                            onClosureStateChange = ::persistClosureState,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )\n''',
)

PATH.write_text(text)
