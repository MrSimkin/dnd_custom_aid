package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CharacterSemanticLabelsTest {
    @Test
    fun rulesFamilyLabelsRemainExplicitAndColorIndependent() {
        assertEquals("5e", characterRulesFamilyBadgeLabel(CharacterRulesFamily.DND_5E))
        assertEquals("5.5e", characterRulesFamilyBadgeLabel(CharacterRulesFamily.DND_5_5E))
        assertEquals("Custom", characterRulesFamilyBadgeLabel(CharacterRulesFamily.CUSTOM))
        assertEquals("Sin especificar", characterRulesFamilyBadgeLabel(CharacterRulesFamily.UNSPECIFIED))
    }

    @Test
    fun sourceLabelIsDistinctTrimmedAndOptional() {
        assertEquals("Fuente · Player's Handbook", characterSourceBadgeLabel("  Player's Handbook  "))
        assertNull(characterSourceBadgeLabel(null))
        assertNull(characterSourceBadgeLabel("   "))
    }
}
