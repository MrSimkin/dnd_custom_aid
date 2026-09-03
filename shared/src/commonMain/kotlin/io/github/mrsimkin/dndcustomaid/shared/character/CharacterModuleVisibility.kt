package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Resolves the character-module set shown by the UI without mutating any module-owned data.
 *
 * Catalog suggestions are convenience metadata only. Manual overrides always win so custom and
 * homebrew characters can expose or hide any reusable module without changing their class data.
 */
fun suggestedCharacterModules(classes: List<CharacterClassLevel>): Set<CharacterModuleKind> =
    CharacterClassCatalog.modulesFor(classes)

fun visibleCharacterModules(
    classes: List<CharacterClassLevel>,
    overrides: List<CharacterModuleOverride>,
): Set<CharacterModuleKind> {
    val suggested = suggestedCharacterModules(classes)
    val overrideByModule = overrides.associateBy(CharacterModuleOverride::module)

    return CharacterModuleKind.entries.filterTo(linkedSetOf()) { module ->
        when (overrideByModule[module]?.mode ?: CharacterModuleOverrideMode.AUTO) {
            CharacterModuleOverrideMode.AUTO -> module in suggested
            CharacterModuleOverrideMode.FORCE_SHOW -> true
            CharacterModuleOverrideMode.FORCE_HIDE -> false
        }
    }
}

fun CharacterClosureState.moduleOverrideMode(module: CharacterModuleKind): CharacterModuleOverrideMode =
    moduleOverrides.firstOrNull { it.module == module }?.mode ?: CharacterModuleOverrideMode.AUTO

fun CharacterClosureState.withModuleOverride(
    module: CharacterModuleKind,
    mode: CharacterModuleOverrideMode,
): CharacterClosureState {
    val remaining = moduleOverrides.filterNot { it.module == module }
    val normalized = if (mode == CharacterModuleOverrideMode.AUTO) {
        remaining
    } else {
        remaining + CharacterModuleOverride(module, mode)
    }
    return copy(moduleOverrides = normalized.sortedBy { it.module.ordinal })
}
