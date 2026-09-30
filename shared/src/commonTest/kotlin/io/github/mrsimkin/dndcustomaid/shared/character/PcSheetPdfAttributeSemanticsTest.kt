package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals

class PcSheetPdfAttributeSemanticsTest {
    @Test
    fun customAttributeTitleUsesIntegratedNativeStyle() {
        assertEquals("ETEr", pcSheetIntegratedAttributeTitle("Éter", "ETE"))
        assertEquals("FORtuna", pcSheetIntegratedAttributeTitle("Fortuna", "FOR"))
        assertEquals("CORdura", pcSheetIntegratedAttributeTitle("Cordura", "COR"))
        assertEquals("RENombre", pcSheetIntegratedAttributeTitle("Renombre", "REN"))
    }

    @Test
    fun nonPrefixAbbreviationPreservesFullSemanticName() {
        assertEquals("Fortuna", pcSheetIntegratedAttributeTitle("Fortuna", "LUK"))
    }

    @Test
    fun perAttributeSkillDoesNotRepeatVisibleOwnerKey() {
        assertEquals(
            "Lectura de presagios",
            pcSheetContextualSkillIdentity(
                skillName = "Lectura de presagios",
                attributeKey = "ETE",
                ownerAttributeStructurallyVisible = true,
            ),
        )
    }

    @Test
    fun detachedOrPerAbilitySkillCarriesCompactOwnerKey() {
        assertEquals(
            "Lectura de presagios (ETE)",
            pcSheetContextualSkillIdentity(
                skillName = "Lectura de presagios",
                attributeKey = "ÉTE",
                ownerAttributeStructurallyVisible = false,
            ),
        )
        assertEquals(
            "Atletismo experimental (FUE)",
            pcSheetContextualSkillIdentity(
                skillName = "Atletismo experimental",
                attributeKey = "FUE",
                ownerAttributeStructurallyVisible = false,
            ),
        )
    }
}
