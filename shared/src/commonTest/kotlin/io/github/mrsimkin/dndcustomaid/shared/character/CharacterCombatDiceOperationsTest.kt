package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterCombatDiceOperationsTest {
    @Test
    fun d20ModesChooseTheExpectedDie() {
        val normal = resolveCharacterD20Roll(firstDie = 7, secondDie = 19, mode = CharacterD20Mode.NORMAL, modifier = 3)
        val advantage = resolveCharacterD20Roll(firstDie = 7, secondDie = 19, mode = CharacterD20Mode.ADVANTAGE, modifier = 3)
        val disadvantage = resolveCharacterD20Roll(firstDie = 7, secondDie = 19, mode = CharacterD20Mode.DISADVANTAGE, modifier = 3)

        assertEquals(7, normal.chosenDie)
        assertEquals(10, normal.total)
        assertEquals(19, advantage.chosenDie)
        assertEquals(22, advantage.total)
        assertEquals(7, disadvantage.chosenDie)
        assertEquals(10, disadvantage.total)
    }

    @Test
    fun structuredDamageSummaryAndRollUseTheSameComponents() {
        val components = listOf(
            CharacterDamageComponent(CharacterDamageComponentKind.DICE, "2d6", "fuego"),
            CharacterDamageComponent(CharacterDamageComponentKind.FLAT, "2"),
            CharacterDamageComponent(CharacterDamageComponentKind.TEXT, "enciende al objetivo"),
        )
        val rolls = ArrayDeque(listOf(3, 5))
        val result = resolveCharacterDamageRoll(components) { sides ->
            assertEquals(6, sides)
            rolls.removeFirst()
        }

        assertEquals("2d6 fuego · +2 · enciende al objetivo", characterDamageSummary(components))
        assertEquals(listOf(3, 5), result.components.first().diceResults)
        assertEquals(10, result.numericTotal)
        assertTrue(result.hasNumericDamage)
        assertEquals("enciende al objetivo", result.components.last().component.expression)
    }

    @Test
    fun legacyDamageEffectFallsBackToTextWithoutSpeculativeParsing() {
        val entry = combatEntry(damageEffect = "1d8+3 cortante, quizá empuja")
        val components = CharacterSuccessorState().combatDamageComponents(entry)

        assertEquals(1, components.size)
        assertEquals(CharacterDamageComponentKind.TEXT, components.single().kind)
        assertEquals("1d8+3 cortante, quizá empuja", components.single().expression)
        assertFalse(resolveCharacterDamageRoll(components) { 1 }.hasNumericDamage)
    }

    @Test
    fun diceTargetsUseOneGeneralizedCharacterAwareProjection() {
        val customAttribute = CharacterCustomAttribute(
            id = Uuid.random(),
            name = "Razón",
            abbreviation = "RAZ",
            score = 14,
            savingThrowEnabled = true,
            savingThrowProficient = true,
        )
        val customSkill = CharacterCustomSkill(
            id = Uuid.random(),
            name = "Botánica",
            ability = CharacterAbility.INTELLIGENCE,
            training = SkillTraining.PROFICIENT,
        )
        val attack = combatEntry(name = "Espada larga", attackModifier = 5)
        val source = CharacterSpellcastingSource(
            id = Uuid.random(),
            name = "Mago",
            linkedClassId = null,
            sortOrder = 0,
        )
        val sheet = sampleSheet(
            intelligence = 16,
            combatEntries = listOf(attack),
            spellcastingSources = listOf(source),
        )
        val successorState = CharacterSuccessorState(
            customAttributes = listOf(customAttribute),
            customSkillAbilities = listOf(
                CharacterCustomSkillAbilityConfiguration(
                    customSkillId = customSkill.id,
                    ability = CharacterAbilityReference.custom(customAttribute.id),
                ),
            ),
            spellcastingProfiles = listOf(
                CharacterSpellcastingProfile(
                    sourceId = source.id,
                    ability = CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
                ),
            ),
        )

        val targets = characterDiceTargets(
            sheet = sheet,
            closureState = CharacterClosureState(customSkills = listOf(customSkill)),
            successorState = successorState,
        )

        assertEquals("Conocimiento Arcano", targets.first { it.key == "skill-ARCANA" }.label)
        val customSkillTarget = targets.first { it.key == "custom-skill-${customSkill.id}" }
        assertEquals(CharacterDiceTargetCategory.SKILL, customSkillTarget.category)
        assertEquals("Botánica", customSkillTarget.label)
        assertEquals("Habilidad · RAZ", customSkillTarget.context)
        assertEquals(4, customSkillTarget.modifier)

        val customSave = targets.first { it.key == "custom-save-${customAttribute.id}" }
        assertEquals(4, customSave.modifier)

        assertEquals(5, targets.first { it.key == "attack-${attack.id}" }.modifier)
        val spellAttack = targets.first { it.key == "spell-attack-${source.id}" }
        assertEquals("Mago", spellAttack.label)
        assertEquals(5, spellAttack.modifier)
        assertNotNull(targets.firstOrNull { it.category == CharacterDiceTargetCategory.CUSTOM })
    }

    @Test
    fun damageProfileReplacementNormalizesAndKeepsOneProfilePerEntry() {
        val entryId = Uuid.random()
        val otherId = Uuid.random()
        val state = CharacterSuccessorState(
            combatDamage = listOf(
                CharacterCombatDamageProfile(entryId, listOf(CharacterDamageComponent(CharacterDamageComponentKind.TEXT, "old"))),
                CharacterCombatDamageProfile(otherId, listOf(CharacterDamageComponent(CharacterDamageComponentKind.TEXT, "keep"))),
            ),
        )

        val updated = state.withCombatDamageComponents(
            entryId,
            listOf(
                CharacterDamageComponent(CharacterDamageComponentKind.DICE, " 1d8 ", " cortante "),
                CharacterDamageComponent(CharacterDamageComponentKind.TEXT, "   "),
            ),
        )

        assertEquals(2, updated.combatDamage.size)
        val profile = updated.combatDamage.single { it.combatEntryId == entryId }
        assertEquals(1, profile.components.size)
        assertEquals("1d8", profile.components.single().expression)
        assertEquals("cortante", profile.components.single().typeText)
        assertEquals("keep", updated.combatDamage.single { it.combatEntryId == otherId }.components.single().expression)
    }

    private fun combatEntry(
        name: String = "Ataque",
        attackModifier: Int? = null,
        damageEffect: String = "",
    ) = CharacterCombatEntry(
        id = Uuid.random(),
        name = name,
        type = CharacterCombatEntryType.ATTACK,
        attackModifier = attackModifier,
        damageEffect = damageEffect,
        rangeText = null,
        notes = null,
        sortOrder = 0,
    )

    private fun sampleSheet(
        intelligence: Int = 10,
        combatEntries: List<CharacterCombatEntry> = emptyList(),
        spellcastingSources: List<CharacterSpellcastingSource> = emptyList(),
    ) = CharacterSheet(
        id = Uuid.random(),
        campaignId = Uuid.random(),
        name = "PJ",
        status = CharacterStatus.ACTIVE,
        updatedAtEpochSeconds = 0,
        strength = 10,
        dexterity = 10,
        constitution = 10,
        intelligence = intelligence,
        wisdom = 10,
        charisma = 10,
        armorClass = 10,
        maxHp = 10,
        currentHp = 10,
        tempHp = 0,
        initiativeAdjustment = 0,
        speed = 30,
        proficiencyBonus = 2,
        savingThrows = emptyList(),
        passivePerceptionAdjustment = 0,
        spellSaveDc = null,
        classes = emptyList(),
        skills = emptyList(),
        combatEntries = combatEntries,
        spellcastingSources = spellcastingSources,
    )
}
