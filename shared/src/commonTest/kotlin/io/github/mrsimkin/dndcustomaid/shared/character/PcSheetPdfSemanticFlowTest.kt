package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.uuid.Uuid

class PcSheetPdfSemanticFlowTest {
    @Test
    fun semanticRecordRefsKeepDomainAssociation() {
        val trait = CharacterTrait(
            id = Uuid.random(),
            name = "Rasgo de prueba",
            source = "Clase",
            type = CharacterTraitType.CLASS,
            description = "Descripción",
            notes = null,
            maxUses = null,
            spentUses = 0,
            recovery = null,
            activation = null,
            sortOrder = 0,
        )
        val attack = CharacterCombatEntry(
            id = Uuid.random(),
            name = "Ataque de prueba",
            type = CharacterCombatEntryType.ATTACK,
            attackModifier = 5,
            damageEffect = "1d8",
            rangeText = "5 ft",
            notes = null,
            sortOrder = 0,
        )
        val ordinary = CharacterInventoryItem(
            id = Uuid.random(),
            name = "Antorcha",
            quantity = 2,
            weightLb = 1.0,
            equipped = false,
            notes = null,
            sortOrder = 0,
            special = false,
            description = null,
            location = null,
            attuned = false,
        )
        val special = ordinary.copy(
            id = Uuid.random(),
            name = "Amuleto",
            special = true,
        )

        assertEquals(PcSheetSemanticModule.TRAITS, trait.pcSheetSemanticRecordRef().module)
        assertEquals(PcSheetSemanticModule.COMBAT_ACTIONS, attack.pcSheetSemanticRecordRef().module)
        assertEquals(PcSheetSemanticModule.ORDINARY_EQUIPMENT, ordinary.pcSheetSemanticRecordRef().module)
        assertEquals(PcSheetSemanticModule.SPECIAL_EQUIPMENT, special.pcSheetSemanticRecordRef().module)
    }

    @Test
    fun continuationMarkersAreBidirectionalAndNumbered() {
        val record = PcSheetSemanticRecordRef(
            module = PcSheetSemanticModule.BACKGROUND_STORY,
            stableKey = "background-story",
            displayName = "Historia",
        )
        val continuation = PcSheetBidirectionalContinuation(
            record = record,
            source = PcSheetContinuationEndpoint(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                sectionName = "Historia",
                surface = PcSheetContinuationSurface.NORMAL,
            ),
            target = PcSheetContinuationEndpoint(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                sectionName = "Historia",
                surface = PcSheetContinuationSurface.EXTENDED,
                extendedIndex = 1,
            ),
        )

        assertEquals(
            "[continúa en sección extendida HISTORIA 01]",
            continuation.sourceMarker(),
        )
        assertEquals(
            "[proviene de sección normal HISTORIA]",
            continuation.targetMarker(),
        )
    }

    @Test
    fun continuationReferenceCanRetainNoteIdentity() {
        val endpoint = PcSheetContinuationEndpoint(
            module = PcSheetSemanticModule.NOTES,
            sectionName = "Notas",
            surface = PcSheetContinuationSurface.EXTENDED,
            extendedIndex = 2,
            recordLabel = "Nota 8",
        )

        assertEquals("sección extendida NOTAS 02 / Nota 8", endpoint.ownerFacingReference())
    }

    @Test
    fun writableTrackerPreservesRuntimeSnapshotWithoutConsumingPaperBlank() {
        val tracker = PcSheetWritableTrackerState.fromSpent(
            maximum = 3,
            spent = 1,
        )

        assertEquals(2, tracker.currentAvailable)
        assertEquals("____(2)/3", tracker.compactEditableLabel())
    }

    @Test
    fun semanticTextPolicyUsesCompressionThenWrapThenExplicitContinuationWithoutEllipsis() {
        val compressed = decidePcSheetSemanticTextFit(
            PcSheetSemanticTextFitInput(
                fitsNativeSingleLine = false,
                requiredSingleLineScale = 0.82f,
                wrappedLineCount = 2,
                availableWrappedLines = 2,
                wrappedUniformScale = 1f,
                minimumReadableScale = 0.75f,
            ),
        )
        assertEquals(PcSheetSemanticTextDisposition.UNIFORM_COMPRESSED_SINGLE_LINE, compressed.disposition)
        assertEquals(0.82f, compressed.uniformScale)
        assertFalse(compressed.usesSemanticEllipsis)

        val wrapped = decidePcSheetSemanticTextFit(
            PcSheetSemanticTextFitInput(
                fitsNativeSingleLine = false,
                requiredSingleLineScale = 0.60f,
                wrappedLineCount = 2,
                availableWrappedLines = 2,
                wrappedUniformScale = 0.80f,
                minimumReadableScale = 0.75f,
            ),
        )
        assertEquals(PcSheetSemanticTextDisposition.WRAPPED, wrapped.disposition)
        assertEquals(0.80f, wrapped.uniformScale)
        assertFalse(wrapped.usesSemanticEllipsis)

        val continued = decidePcSheetSemanticTextFit(
            PcSheetSemanticTextFitInput(
                fitsNativeSingleLine = false,
                requiredSingleLineScale = 0.60f,
                wrappedLineCount = 3,
                availableWrappedLines = 2,
                wrappedUniformScale = 0.80f,
                minimumReadableScale = 0.75f,
            ),
        )
        assertEquals(PcSheetSemanticTextDisposition.EXPLICIT_CONTINUATION, continued.disposition)
        assertEquals(null, continued.uniformScale)
        assertFalse(continued.usesSemanticEllipsis)
    }
}
