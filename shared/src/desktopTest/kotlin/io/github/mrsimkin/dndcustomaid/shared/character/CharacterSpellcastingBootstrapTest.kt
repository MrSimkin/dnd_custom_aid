package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterSpellcastingBootstrapTest {
    @Test
    fun wizardCreatesLinkedSourceAndIntelligenceProfile() {
        val classId = Uuid.random()
        val sourceId = Uuid.random()
        val wizard = canonicalClass(classId, "Mago", "wizard-2024")

        val result = reconcileCharacterSpellcastingBootstrap(
            classes = listOf(wizard),
            existingSources = emptyList(),
            existingProfiles = emptyList(),
            newSourceId = { sourceId },
        )

        assertTrue(result.hasCanonicalSpellcastingClass)
        assertEquals(1, result.sources.size)
        assertEquals(sourceId, result.sources.single().id)
        assertEquals(classId, result.sources.single().linkedClassId)
        assertEquals(CharacterSpellcastingOriginKind.CLASS, result.sources.single().originKind)
        assertEquals("Mago", result.sources.single().name)
        assertEquals(
            CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
            result.profiles.single().ability,
        )
        assertEquals(sourceId, result.profiles.single().sourceId)
    }

    @Test
    fun existingLinkedIdentityAndConfiguredProfileArePreserved() {
        val classId = Uuid.random()
        val sourceId = Uuid.random()
        val manualId = Uuid.random()
        val wizard = canonicalClass(classId, "Mago", "wizard-2024")
        val linked = CharacterSpellcastingSource(
            id = sourceId,
            name = "Mi grimorio",
            linkedClassId = classId,
            sortOrder = 2,
            originKind = CharacterSpellcastingOriginKind.CLASS,
        )
        val manual = CharacterSpellcastingSource(
            id = manualId,
            name = "Don casero",
            linkedClassId = null,
            sortOrder = 7,
            originKind = CharacterSpellcastingOriginKind.OTHER,
        )
        val configured = CharacterSpellcastingProfile(
            sourceId = sourceId,
            ability = CharacterAbilityReference.builtIn(CharacterAbility.CHARISMA),
            saveDcAdjustment = 2,
            spellAttackAdjustment = -1,
        )
        val manualProfile = CharacterSpellcastingProfile(
            sourceId = manualId,
            ability = CharacterAbilityReference.builtIn(CharacterAbility.WISDOM),
        )

        val result = reconcileCharacterSpellcastingBootstrap(
            classes = listOf(wizard),
            existingSources = listOf(linked, manual),
            existingProfiles = listOf(configured, manualProfile),
            newSourceId = { error("Existing canonical link must be reused") },
        )

        assertEquals(listOf(linked, manual), result.sources)
        assertEquals(listOf(configured, manualProfile), result.profiles)
        assertFalse(needsCharacterSpellcastingBootstrap(listOf(wizard), result.sources))
    }

    @Test
    fun unconfiguredExistingProfileGetsCatalogAbilityWithoutLosingAdjustments() {
        val classId = Uuid.random()
        val sourceId = Uuid.random()
        val cleric = canonicalClass(classId, "Clérigo", "cleric-2024")
        val source = CharacterSpellcastingSource(
            id = sourceId,
            name = "Clérigo",
            linkedClassId = classId,
            sortOrder = 0,
            originKind = CharacterSpellcastingOriginKind.CLASS,
        )
        val legacy = CharacterSpellcastingProfile(
            sourceId = sourceId,
            ability = CharacterAbilityReference.NONE,
            saveDcAdjustment = 3,
            spellAttackAdjustment = -2,
            legacySaveDcOverride = 17,
            legacySpellAttackOverride = 9,
        )

        val result = reconcileCharacterSpellcastingBootstrap(
            classes = listOf(cleric),
            existingSources = listOf(source),
            existingProfiles = listOf(legacy),
        )

        val profile = result.profiles.single()
        assertEquals(CharacterAbilityReference.builtIn(CharacterAbility.WISDOM), profile.ability)
        assertEquals(3, profile.saveDcAdjustment)
        assertEquals(-2, profile.spellAttackAdjustment)
        assertEquals(17, profile.legacySaveDcOverride)
        assertEquals(9, profile.legacySpellAttackOverride)
    }

    @Test
    fun nonCasterAndCustomClassDoNotInventSources() {
        val barbarian = canonicalClass(Uuid.random(), "Bárbaro", "barbarian-2024")
        val custom = CharacterClassLevel(
            id = Uuid.random(),
            name = "Clase casera",
            level = 5,
            hitDieSides = 8,
            hitDiceRemaining = 5,
            sortOrder = 1,
            rulesFamily = CharacterRulesFamily.CUSTOM,
            catalogKey = CharacterClassCatalog.CUSTOM_KEY,
        )

        val result = reconcileCharacterSpellcastingBootstrap(
            classes = listOf(barbarian, custom),
            existingSources = emptyList(),
            existingProfiles = emptyList(),
        )

        assertFalse(result.hasCanonicalSpellcastingClass)
        assertFalse(needsCharacterSpellcastingBootstrap(listOf(barbarian, custom), emptyList()))
        assertTrue(result.sources.isEmpty())
        assertTrue(result.profiles.isEmpty())
    }

    @Test
    fun missingWizardSourceRequiresBootstrap() {
        val wizard = canonicalClass(Uuid.random(), "Mago", "wizard-2024")
        assertTrue(needsCharacterSpellcastingBootstrap(listOf(wizard), emptyList()))
    }

    @Test
    fun reconciliationIsIdempotentAndRetainsManualSources() {
        val wizard = canonicalClass(Uuid.random(), "Mago", "wizard-2024")
        val bard = canonicalClass(Uuid.random(), "Bardo", "bard-2024", sortOrder = 1)
        val manual = CharacterSpellcastingSource(
            id = Uuid.random(),
            name = "Objeto mágico casero",
            linkedClassId = null,
            sortOrder = 8,
            originKind = CharacterSpellcastingOriginKind.OTHER,
        )
        val allocated = ArrayDeque(listOf(Uuid.random(), Uuid.random()))

        val first = reconcileCharacterSpellcastingBootstrap(
            classes = listOf(wizard, bard),
            existingSources = listOf(manual),
            existingProfiles = emptyList(),
            newSourceId = { allocated.removeFirst() },
        )
        val second = reconcileCharacterSpellcastingBootstrap(
            classes = listOf(wizard, bard),
            existingSources = first.sources,
            existingProfiles = first.profiles,
            newSourceId = { error("Idempotent reconciliation must not allocate again") },
        )

        assertEquals(first, second)
        assertEquals(manual, second.sources.first())
        assertEquals(3, second.sources.size)
        assertEquals(2, second.profiles.size)
    }

    private fun canonicalClass(
        id: Uuid,
        name: String,
        catalogKey: String,
        sortOrder: Int = 0,
    ): CharacterClassLevel = CharacterClassLevel(
        id = id,
        name = name,
        level = 10,
        hitDieSides = 8,
        hitDiceRemaining = 10,
        sortOrder = sortOrder,
        rulesFamily = CharacterRulesFamily.DND_5_5E,
        catalogKey = catalogKey,
    )
}
