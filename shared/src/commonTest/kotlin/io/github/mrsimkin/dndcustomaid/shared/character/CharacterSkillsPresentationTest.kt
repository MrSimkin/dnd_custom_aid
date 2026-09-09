package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterSkillsPresentationTest {
    @Test
    fun arcanaUsesOwnerApprovedSpanishLabel() {
        assertEquals("Conocimiento Arcano", characterSkillSpanishLabel(SkillKey.ARCANA))
    }

    @Test
    fun bySkillsInterleavesCustomSkillsAlphabeticallyBySpanishLabel() {
        val custom = CharacterCustomSkill(
            id = Uuid.random(),
            name = "Botánica",
            ability = CharacterAbility.INTELLIGENCE,
            sortOrder = 0,
        )

        val labels = presentCharacterSkills(
            builtInSkills = emptyList(),
            customSkills = listOf(custom),
            successorState = CharacterSuccessorState(),
        ).map { it.label }

        assertEquals(
            listOf("Acrobacias", "Atletismo", "Botánica", "Conocimiento Arcano"),
            labels.take(4),
        )
        assertEquals(19, labels.size)
    }

    @Test
    fun customSkillUsesSuccessorCustomAttributeReference() {
        val attribute = CharacterCustomAttribute(
            id = Uuid.random(),
            name = "Razón",
            abbreviation = "RAZ",
            score = 14,
        )
        val custom = CharacterCustomSkill(
            id = Uuid.random(),
            name = "Ciencia extraña",
            ability = CharacterAbility.INTELLIGENCE,
        )
        val successorState = CharacterSuccessorState(
            customAttributes = listOf(attribute),
            customSkillAbilities = listOf(
                CharacterCustomSkillAbilityConfiguration(
                    customSkillId = custom.id,
                    ability = CharacterAbilityReference.custom(attribute.id),
                ),
            ),
        )

        val row = presentCharacterSkills(
            builtInSkills = emptyList(),
            customSkills = listOf(custom),
            successorState = successorState,
        ).first { it.customSkillId == custom.id }

        assertEquals(attribute.id, row.ability.customAttributeId)
        assertEquals("RAZ", characterAbilityReferenceAbbreviation(row.ability, successorState))
        assertTrue(row.isCustom)
    }
}
