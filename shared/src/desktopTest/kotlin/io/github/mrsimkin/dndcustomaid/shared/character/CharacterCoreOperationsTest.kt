package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class CharacterCoreOperationsTest {
    @Test
    fun passiveInsightAndInvestigationReuseExistingSkillMath() {
        val sheet = sheet(
            wisdom = 14,
            intelligence = 16,
            skills = listOf(
                CharacterSkill(SkillKey.INSIGHT, adjustment = 0, training = SkillTraining.PROFICIENT),
                CharacterSkill(SkillKey.INVESTIGATION, adjustment = 1, training = SkillTraining.NONE),
            ),
        )

        assertEquals(15, sheet.passiveInsight)
        assertEquals(14, sheet.passiveInvestigation)
    }

    @Test
    fun customSkillUsesAbilityTrainingAndAdjustmentWithoutChangingStandardSkills() {
        val sheet = sheet(intelligence = 14)
        val custom = CharacterCustomSkill(
            id = Uuid.random(),
            name = "Ingeniería arcana",
            ability = CharacterAbility.INTELLIGENCE,
            training = SkillTraining.EXPERTISE,
            adjustment = 1,
        )

        assertEquals(9, sheet.customSkillTotal(custom))
        assertEquals(2, sheet.skillTotal(SkillKey.ARCANA))
    }

    @Test
    fun quickDamageConsumesTemporaryHpBeforeCurrentHp() {
        val original = sheet(currentHp = 10, maxHp = 20, tempHp = 5)
        val changed = applyCharacterDamage(original, 7)

        assertEquals(0, changed.tempHp)
        assertEquals(8, changed.currentHp)
        assertEquals(10, original.currentHp)
        assertEquals(5, original.tempHp)
    }

    @Test
    fun healingCapsAtMaxAndTemporaryHpCanBeSetExactly() {
        val original = sheet(currentHp = 8, maxHp = 20, tempHp = 2)
        val healed = applyCharacterHealing(original, 50)
        val withTemp = setCharacterTemporaryHp(healed, 7)

        assertEquals(20, healed.currentHp)
        assertEquals(2, healed.tempHp)
        assertEquals(7, withTemp.tempHp)
        assertFailsWith<IllegalArgumentException> { applyCharacterHealing(original, -1) }
        assertFailsWith<IllegalArgumentException> { setCharacterTemporaryHp(original, -1) }
    }

    @Test
    fun quickAccessToggleIsIdempotentAndKeepsStableDenseOrder() {
        val attackId = Uuid.random()
        val spellId = Uuid.random()
        val initial = CharacterClosureState()
            .withQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, attackId, enabled = true)
            .withQuickAccess(CharacterQuickAccessKind.SPELL, spellId, enabled = true)
            .withQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, attackId, enabled = true)

        assertTrue(initial.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, attackId))
        assertEquals(2, initial.quickAccess.size)
        assertEquals(listOf(0, 1), initial.quickAccess.map { it.sortOrder })

        val removed = initial.withQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, attackId, enabled = false)
        assertFalse(removed.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, attackId))
        assertEquals(listOf(0), removed.quickAccess.map { it.sortOrder })
    }

    @Test
    fun boundedD20RollAddsOnlyTheSuppliedModifier() {
        val roll = characterD20Roll(dieResult = 17, modifier = 5)
        assertEquals(22, roll.total)
        assertFailsWith<IllegalArgumentException> { characterD20Roll(0, 5) }
        assertFailsWith<IllegalArgumentException> { characterD20Roll(21, 5) }
    }

    @Test
    fun hitDieSuggestionsAreConvenienceOnlyForRecognizedNames() {
        assertEquals(12, suggestedHitDieSidesForClassName("Bárbaro"))
        assertEquals(10, suggestedHitDieSidesForClassName("fighter"))
        assertEquals(6, suggestedHitDieSidesForClassName("Hechicero"))
        assertNull(suggestedHitDieSidesForClassName("Cronomante casero"))
    }

    @Test
    fun classIdentityPresentationIncludesSubclassRulesAndSourceWhenKnown() {
        val classLevel = CharacterClassLevel(
            id = Uuid.random(),
            name = "Guerrero",
            level = 7,
            hitDieSides = 10,
            hitDiceRemaining = 4,
            sortOrder = 0,
            rulesFamily = CharacterRulesFamily.DND_5_5E,
            source = "Player's Handbook (2024)",
            subclassName = "Battle Master",
            subclassSource = "Player's Handbook (2024)",
            subclassRulesFamily = CharacterRulesFamily.DND_5_5E,
        )

        val presented = presentCharacterClassIdentity(classLevel)

        assertEquals("Guerrero · Battle Master · Nv. 7", presented.primary)
        assertEquals("D&D 5.5e · Player's Handbook (2024)", presented.secondary)
    }

    @Test
    fun d0046ProficiencyAdjustmentSemanticsRemainUnchanged() {
        val sheet = sheet(proficiencyBonusAdjustment = 1)
        assertEquals(3, sheet.standardProficiencyBonus)
        assertEquals(4, sheet.finalProficiencyBonus)
    }

    private fun sheet(
        wisdom: Int = 10,
        intelligence: Int = 10,
        currentHp: Int = 10,
        maxHp: Int = 20,
        tempHp: Int = 0,
        proficiencyBonusAdjustment: Int = 0,
        skills: List<CharacterSkill> = emptyList(),
    ): CharacterSheet = CharacterSheet(
        id = Uuid.random(),
        campaignId = Uuid.random(),
        name = "Prueba",
        status = CharacterStatus.ACTIVE,
        updatedAtEpochSeconds = 0,
        strength = 10,
        dexterity = 10,
        constitution = 10,
        intelligence = intelligence,
        wisdom = wisdom,
        charisma = 10,
        armorClass = 10,
        maxHp = maxHp,
        currentHp = currentHp,
        tempHp = tempHp,
        initiativeAdjustment = 0,
        speed = 30,
        proficiencyBonus = 3 + proficiencyBonusAdjustment,
        savingThrows = emptyList(),
        passivePerceptionAdjustment = 0,
        spellSaveDc = null,
        classes = listOf(
            CharacterClassLevel(
                id = Uuid.random(),
                name = "Guerrero",
                level = 5,
                hitDieSides = 10,
                hitDiceRemaining = 5,
                sortOrder = 0,
            ),
        ),
        skills = skills,
        proficiencyBonusAdjustment = proficiencyBonusAdjustment,
    )
}
