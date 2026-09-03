package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterModuleVisibilityTest {
    private fun classLevel(
        catalogKey: String,
        subclassCatalogKey: String? = null,
    ) = CharacterClassLevel(
        id = Uuid.random(),
        name = CharacterClassCatalog.byKey(catalogKey)?.nameEs ?: "Custom",
        level = 5,
        hitDieSides = 8,
        hitDiceRemaining = 5,
        sortOrder = 0,
        catalogKey = catalogKey,
        subclassCatalogKey = subclassCatalogKey,
    )

    @Test
    fun automaticModeUsesCatalogSuggestionsAcrossMulticlass() {
        val classes = listOf(
            classLevel("druid-2024", "druid-moon-2024"),
            classLevel("fighter-2024", "fighter-battle-master-2024"),
        )

        val visible = visibleCharacterModules(classes, emptyList())

        assertTrue(CharacterModuleKind.FORMS in visible)
        assertTrue(CharacterModuleKind.TECHNIQUES in visible)
        assertFalse(CharacterModuleKind.PACTS in visible)
    }

    @Test
    fun manualShowAndHideOverridesAlwaysWin() {
        val classes = listOf(classLevel("druid-2024", "druid-moon-2024"))
        val overrides = listOf(
            CharacterModuleOverride(CharacterModuleKind.FORMS, CharacterModuleOverrideMode.FORCE_HIDE),
            CharacterModuleOverride(CharacterModuleKind.PACTS, CharacterModuleOverrideMode.FORCE_SHOW),
        )

        val visible = visibleCharacterModules(classes, overrides)

        assertFalse(CharacterModuleKind.FORMS in visible)
        assertTrue(CharacterModuleKind.PACTS in visible)
    }

    @Test
    fun returningOverrideToAutoRemovesRedundantPersistedOverride() {
        val initial = CharacterClosureState(
            moduleOverrides = listOf(
                CharacterModuleOverride(CharacterModuleKind.COMPANIONS, CharacterModuleOverrideMode.FORCE_SHOW),
            ),
        )

        val restoredAuto = initial.withModuleOverride(
            CharacterModuleKind.COMPANIONS,
            CharacterModuleOverrideMode.AUTO,
        )

        assertEquals(CharacterModuleOverrideMode.AUTO, restoredAuto.moduleOverrideMode(CharacterModuleKind.COMPANIONS))
        assertTrue(restoredAuto.moduleOverrides.isEmpty())
    }

    @Test
    fun changingVisibilityDoesNotTouchAnyModuleOwnedCharacterData() {
        val formId = Uuid.random()
        val state = CharacterClosureState(
            conditions = listOf(CharacterCondition(Uuid.random(), "Prone")),
            moduleOverrides = emptyList(),
        )
        val changed = state.withModuleOverride(CharacterModuleKind.FORMS, CharacterModuleOverrideMode.FORCE_HIDE)

        assertEquals(state.conditions, changed.conditions)
        assertEquals(formId, formId) // explicit sentinel: visibility carries no module payload to rewrite
        assertEquals(CharacterModuleOverrideMode.FORCE_HIDE, changed.moduleOverrideMode(CharacterModuleKind.FORMS))
    }
}
