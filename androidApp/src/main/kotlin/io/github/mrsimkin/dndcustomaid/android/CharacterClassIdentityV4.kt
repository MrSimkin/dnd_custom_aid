package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable

/**
 * Compatibility entry point retained for the existing editor shell.
 *
 * The old class editor exposed rules-family/source/version bookkeeping directly and duplicated
 * hit-die presentation. C3 routes the same authoritative class draft through the compact,
 * version-neutral successor component instead; persisted catalog metadata remains intact.
 */
@Composable
internal fun CharacterClassIdentityCardV4(
    classes: List<ClassLevelDraftV4>,
    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,
) {
    CharacterClassIdentitySuccessorCardV4(
        classes = classes,
        onClassesChange = onClassesChange,
    )
}
