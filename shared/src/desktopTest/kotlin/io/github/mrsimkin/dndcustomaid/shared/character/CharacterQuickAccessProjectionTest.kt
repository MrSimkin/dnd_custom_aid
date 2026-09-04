package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterQuickAccessProjectionTest {
    @Test
    fun projectionPreservesQuickAccessOrderAcrossAllBackedKindsAndIgnoresStaleRefs() {
        val combatId = Uuid.random()
        val traitId = Uuid.random()
        val spellId = Uuid.random()
        val resourceId = Uuid.random()
        val optionId = Uuid.random()
        val formId = Uuid.random()
        val companionId = Uuid.random()
        val customSkillId = Uuid.random()
        val effectId = Uuid.random()
        val staleId = Uuid.random()

        val sheet = baseSheet().copy(
            combatEntries = listOf(
                CharacterCombatEntry(
                    id = combatId,
                    name = "Espadazo",
                    type = CharacterCombatEntryType.ATTACK,
                    attackModifier = 5,
                    damageEffect = "1d8+3",
                    rangeText = null,
                    notes = null,
                    sortOrder = 0,
                ),
            ),
            traits = listOf(
                CharacterTrait(
                    id = traitId,
                    name = "Segundo aliento",
                    source = "Guerrero",
                    type = CharacterTraitType.CLASS,
                    description = "Recuperación",
                    notes = null,
                    maxUses = 1,
                    spentUses = 0,
                    recovery = "Descanso corto",
                    activation = CharacterActivationType.BONUS_ACTION,
                    sortOrder = 0,
                ),
            ),
            spells = listOf(
                CharacterSpell(
                    id = spellId,
                    name = "Escudo",
                    level = 1,
                    castingTime = "1 reacción",
                    rangeText = "Personal",
                    verbal = true,
                    somatic = true,
                    material = false,
                    materialText = null,
                    duration = "1 ronda",
                    concentration = false,
                    ritual = false,
                    description = "Protección",
                    notes = null,
                    sortOrder = 0,
                ),
            ),
            resources = listOf(
                CharacterResource(
                    id = resourceId,
                    name = "Dados de superioridad",
                    currentValue = 3,
                    maxValue = 4,
                ),
            ),
            classOptions = listOf(
                CharacterClassOption(
                    id = optionId,
                    kind = CharacterClassOptionKind.TECHNIQUE,
                    name = "Ataque preciso",
                ),
            ),
            forms = listOf(CharacterForm(id = formId, name = "Lobo")),
            companions = listOf(CharacterCompanion(id = companionId, name = "Chispa")),
        )
        val closure = CharacterClosureState(
            customSkills = listOf(
                CharacterCustomSkill(
                    id = customSkillId,
                    name = "Pilotaje",
                    ability = CharacterAbility.DEXTERITY,
                ),
            ),
            temporaryEffects = listOf(
                CharacterTemporaryEffect(
                    id = effectId,
                    name = "Bendición breve",
                ),
            ),
            quickAccess = listOf(
                CharacterQuickAccessRef(CharacterQuickAccessKind.TEMPORARY_EFFECT, effectId, 8),
                CharacterQuickAccessRef(CharacterQuickAccessKind.COMPANION, companionId, 6),
                CharacterQuickAccessRef(CharacterQuickAccessKind.COMBAT_ENTRY, combatId, 0),
                CharacterQuickAccessRef(CharacterQuickAccessKind.RESOURCE, resourceId, 3),
                CharacterQuickAccessRef(CharacterQuickAccessKind.TRAIT, traitId, 1),
                CharacterQuickAccessRef(CharacterQuickAccessKind.FORM, formId, 5),
                CharacterQuickAccessRef(CharacterQuickAccessKind.SPELL, spellId, 2),
                CharacterQuickAccessRef(CharacterQuickAccessKind.CLASS_OPTION, optionId, 4),
                CharacterQuickAccessRef(CharacterQuickAccessKind.CUSTOM_SKILL, customSkillId, 7),
                // Stale and unbacked refs must never produce invented/cached display values.
                CharacterQuickAccessRef(CharacterQuickAccessKind.SPELL, staleId, 9),
                CharacterQuickAccessRef(CharacterQuickAccessKind.OTHER, Uuid.random(), 10),
            ),
        )

        val projected = projectCharacterQuickAccess(sheet, closure)

        assertEquals(
            listOf(
                "Espadazo",
                "Segundo aliento",
                "Escudo",
                "Dados de superioridad",
                "Ataque preciso",
                "Lobo",
                "Chispa",
                "Pilotaje",
                "Bendición breve",
            ),
            projected.map { it.name },
        )
        assertEquals((0..8).toList(), projected.map { it.sortOrder })
        assertEquals(
            listOf(
                CharacterQuickAccessKind.COMBAT_ENTRY,
                CharacterQuickAccessKind.TRAIT,
                CharacterQuickAccessKind.SPELL,
                CharacterQuickAccessKind.RESOURCE,
                CharacterQuickAccessKind.CLASS_OPTION,
                CharacterQuickAccessKind.FORM,
                CharacterQuickAccessKind.COMPANION,
                CharacterQuickAccessKind.CUSTOM_SKILL,
                CharacterQuickAccessKind.TEMPORARY_EFFECT,
            ),
            projected.map { it.kind },
        )
    }

    @Test
    fun emptyQuickAccessProjectsToEmptyList() {
        assertTrue(projectCharacterQuickAccess(baseSheet(), CharacterClosureState()).isEmpty())
    }

    private fun baseSheet(): CharacterSheet = CharacterSheet(
        id = Uuid.random(),
        campaignId = Uuid.random(),
        name = "I2",
        status = CharacterStatus.ACTIVE,
        updatedAtEpochSeconds = 0,
        strength = 10,
        dexterity = 10,
        constitution = 10,
        intelligence = 10,
        wisdom = 10,
        charisma = 10,
        armorClass = 10,
        maxHp = 10,
        currentHp = 10,
        tempHp = 0,
        initiativeAdjustment = 0,
        speed = 30,
        proficiencyBonus = 2,
        savingThrows = CharacterAbility.entries.map {
            CharacterSavingThrow(ability = it, proficient = false, adjustment = 0)
        },
        passivePerceptionAdjustment = 0,
        spellSaveDc = null,
        classes = emptyList(),
        skills = SkillKey.entries.map {
            CharacterSkill(key = it, adjustment = 0, training = SkillTraining.NONE)
        },
    )
}
