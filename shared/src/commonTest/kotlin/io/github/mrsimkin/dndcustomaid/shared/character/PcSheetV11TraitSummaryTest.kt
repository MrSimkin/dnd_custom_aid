package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.uuid.Uuid

/** D-0076 §3.9 and owner 2026-10-16; phase G1 paper projection guard. */
class PcSheetV11TraitSummaryTest {
    private val sample = CharacterTrait(
        id = Uuid.random(),
        name = "Pericia",
        source = "Pícaro",
        type = CharacterTraitType.CLASS,
        description = "TEXTO_DESCRIPTIVO_QUE_NO_VA_AL_PDF",
        notes = "NOTA_LARGA_QUE_NO_VA_AL_PDF",
        maxUses = null,
        spentUses = 0,
        recovery = null,
        activation = null,
        sortOrder = 0,
    )

    @Test
    fun classTraitHasRealNameCategorySourceButNeverDescriptionOrNotes() {
        assertEquals("Pericia - Rasgo de Clase: Pícaro",
            PcSheetV11TraitSummary.text(sample))
        assertEquals("TEXTO_DESCRIPTIVO_QUE_NO_VA_AL_PDF", sample.description,
            "The App must preserve complete source data.")
        assertFalse(PcSheetV11TraitSummary.text(sample).contains(sample.description))
        assertFalse(PcSheetV11TraitSummary.text(sample).contains(sample.notes!!))
    }

    @Test
    fun everyRealCategoryUsesItsOwnNonInventedLabel() {
        val expected = mapOf(
            CharacterTraitType.CLASS to "Rasgo de Clase",
            CharacterTraitType.SPECIES_RACE to "Rasgo de Raza",
            CharacterTraitType.BACKGROUND to "Rasgo de Trasfondo",
            CharacterTraitType.FEAT to "Dote",
            CharacterTraitType.GIFT_BLESSING to "Don / Bendición",
            CharacterTraitType.OTHER to "Otro",
        )
        for ((type, label) in expected) {
            assertEquals("Pericia - $label: Pícaro",
                PcSheetV11TraitSummary.text(sample.copy(type = type)),
                "Never assume all Rasgos came from a character class.")
        }
    }

    @Test
    fun missingSourceIsOmittedNotInvented() {
        assertEquals("Pericia - Rasgo de Clase",
            PcSheetV11TraitSummary.text(sample.copy(source = "   ")))
        assertEquals("Pericia - Rasgo de Clase: Pícaro",
            PcSheetV11TraitSummary.text(sample.copy(name = "  Pericia  ", source = "  Pícaro  ")))
    }

    @Test
    fun changingDescriptionAndNotesCannotChangePrintableSummary() {
        val previous = PcSheetV11TraitSummary.text(sample)
        assertEquals(previous, PcSheetV11TraitSummary.text(sample.copy(
            description = "3000 palabras nuevas...",
            notes = "lo mismo, no debe figurar en el papel",
        )))
    }
}
