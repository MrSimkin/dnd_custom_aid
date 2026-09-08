package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CharacterInputNormalizationTest {
    @Test
    fun singleDigitInputPrefersLatestDigitSoDefaultZeroCanBeReplaced() {
        assertEquals("5", normalizeCharacterUnsignedIntegerInput("05", maxDigits = 1))
        assertEquals("7", normalizeCharacterUnsignedIntegerInput("57", maxDigits = 1))
        assertEquals("0", normalizeCharacterUnsignedIntegerInput("0", maxDigits = 1))
        assertEquals("", normalizeCharacterUnsignedIntegerInput("", maxDigits = 1))
    }

    @Test
    fun ordinaryUnsignedInputRemovesUnnecessaryLeadingZeroes() {
        assertEquals("5", normalizeCharacterUnsignedIntegerInput("005"))
        assertEquals("0", normalizeCharacterUnsignedIntegerInput("000"))
        assertEquals("120", normalizeCharacterUnsignedIntegerInput("12x0"))
        assertEquals("12", normalizeCharacterUnsignedIntegerInput("001234", maxDigits = 2))
    }

    @Test
    fun signedInputKeepsSignAndNormalizesDigits() {
        assertEquals("-5", normalizeCharacterSignedIntegerInput("-005"))
        assertEquals("+7", normalizeCharacterSignedIntegerInput("+007"))
        assertEquals("-", normalizeCharacterSignedIntegerInput("-"))
        assertEquals("", normalizeCharacterSignedIntegerInput(""))
    }

    @Test
    fun maxDigitsMustBePositive() {
        assertFailsWith<IllegalArgumentException> {
            normalizeCharacterUnsignedIntegerInput("1", maxDigits = 0)
        }
    }
}
