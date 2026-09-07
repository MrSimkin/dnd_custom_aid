package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterNameFormattingTest {
    @Test
    fun firstVisibleCharacterIsUppercase() {
        assertEquals("Gustavo", characterProperNameInput("gustavo"))
        assertEquals("  Élara", characterProperNameInput("  élara"))
    }

    @Test
    fun laterWordsAreNotRewritten() {
        assertEquals("Juan de la cruz", characterProperNameInput("juan de la cruz"))
        assertEquals("Árbol viejo", characterProperNameInput("Árbol viejo"))
    }

    @Test
    fun blankEditingStateIsPreserved() {
        assertEquals("", characterProperNameInput(""))
        assertEquals("   ", characterProperNameInput("   "))
    }
}
