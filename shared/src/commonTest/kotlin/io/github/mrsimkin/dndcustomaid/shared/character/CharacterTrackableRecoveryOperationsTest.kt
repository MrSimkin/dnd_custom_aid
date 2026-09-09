package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterTrackableRecoveryOperationsTest {
    @Test
    fun mixedPreviewUsesOneMechanicWithoutMergingResourceAndMarkerIdentity() {
        val resourceId = Uuid.random()
        val markerId = Uuid.random()
        val resource = CharacterResource(
            id = resourceId,
            name = "Ki",
            currentValue = 1,
            maxValue = 4,
        )
        val marker = CharacterCustomMarker(
            id = markerId,
            name = "Impulso",
            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
            currentValue = 2,
            maxValue = 6,
            recovery = CharacterTrackableRecovery(
                cadence = CharacterRecoveryCadence.SHORT_REST,
                amountMode = CharacterRecoveryAmountMode.FIXED,
                fixedAmount = 3,
            ),
        )

        val preview = previewTrackableRecovery(
            rest = CharacterRestKind.SHORT,
            resources = listOf(resource),
            resourceRecoveryRules = listOf(
                CharacterResourceRecovery(
                    resourceId = resourceId,
                    cadence = CharacterRecoveryCadence.SHORT_REST,
                    amountMode = CharacterRecoveryAmountMode.TO_MAX,
                ),
            ),
            markers = listOf(marker),
        )

        assertEquals(2, preview.size)
        assertEquals(CharacterTrackableTargetKind.RESOURCE, preview[0].target.kind)
        assertEquals(4, preview[0].proposedValue)
        assertEquals(CharacterTrackableTargetKind.MARKER, preview[1].target.kind)
        assertEquals(5, preview[1].proposedValue)
        assertTrue(preview.all { it.hasAutomaticChange })
    }

    @Test
    fun equalUuidAcrossDomainsCannotCrossApply() {
        val sharedId = Uuid.random()
        val resource = CharacterResource(
            id = sharedId,
            name = "Dados de superioridad",
            currentValue = 1,
            maxValue = 4,
        )
        val marker = CharacterCustomMarker(
            id = sharedId,
            name = "Carga homebrew",
            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
            currentValue = 2,
            maxValue = 5,
            recovery = CharacterTrackableRecovery(
                cadence = CharacterRecoveryCadence.SHORT_OR_LONG_REST,
                amountMode = CharacterRecoveryAmountMode.TO_MAX,
            ),
        )
        val preview = previewTrackableRecovery(
            rest = CharacterRestKind.SHORT,
            resources = listOf(resource),
            resourceRecoveryRules = listOf(
                CharacterResourceRecovery(
                    resourceId = sharedId,
                    cadence = CharacterRecoveryCadence.SHORT_OR_LONG_REST,
                    amountMode = CharacterRecoveryAmountMode.TO_MAX,
                ),
            ),
            markers = listOf(marker),
        )

        val resourceTarget = CharacterTrackableTarget(CharacterTrackableTargetKind.RESOURCE, sharedId)
        val applied = applySelectedTrackableRecovery(
            resources = listOf(resource),
            markers = listOf(marker),
            preview = preview,
            selectedTargets = setOf(resourceTarget),
        )

        assertEquals(4, applied.resources.single().currentValue)
        assertEquals(2, applied.markers.single().currentValue)
    }

    @Test
    fun valueKindControlsEffectiveMaximumAndPlacementDoesNotControlRecovery() {
        val resourceId = Uuid.random()
        val resource = CharacterResource(
            id = resourceId,
            name = "Estado binario",
            currentValue = 0,
            maxValue = 99,
        )
        val preview = previewTrackableRecovery(
            rest = CharacterRestKind.LONG,
            resources = listOf(resource),
            resourceRecoveryRules = listOf(
                CharacterResourceRecovery(
                    resourceId = resourceId,
                    cadence = CharacterRecoveryCadence.LONG_REST,
                    amountMode = CharacterRecoveryAmountMode.FIXED,
                    fixedAmount = 50,
                ),
            ),
            resourceConfigurations = listOf(
                CharacterResourceSuccessorConfiguration(
                    resourceId = resourceId,
                    valueKind = CharacterTrackableValueKind.BINARY,
                    placements = setOf(CharacterResourcePlacement.EQUIPMENT),
                ),
            ),
        )

        val item = preview.single()
        assertEquals(CharacterTrackableValueKind.BINARY, item.valueKind)
        assertEquals(1, item.maxValue)
        assertEquals(1, item.proposedValue)
    }

    @Test
    fun automaticRecoveryNeverGuessesWhenMaximumOrRuleIsInsufficient() {
        val noMaximumMarker = CharacterCustomMarker(
            id = Uuid.random(),
            name = "Contador libre",
            valueKind = CharacterTrackableValueKind.COUNTER,
            currentValue = 3,
            recovery = CharacterTrackableRecovery(
                cadence = CharacterRecoveryCadence.LONG_REST,
                amountMode = CharacterRecoveryAmountMode.TO_MAX,
            ),
        )
        val manualMarker = CharacterCustomMarker(
            id = Uuid.random(),
            name = "Ritual casero",
            valueKind = CharacterTrackableValueKind.COUNTER,
            currentValue = 1,
            recovery = CharacterTrackableRecovery(
                cadence = CharacterRecoveryCadence.MANUAL,
                amountMode = CharacterRecoveryAmountMode.NONE,
            ),
        )

        val preview = previewTrackableRecovery(
            rest = CharacterRestKind.LONG,
            resources = emptyList(),
            resourceRecoveryRules = emptyList(),
            markers = listOf(noMaximumMarker, manualMarker),
        )

        assertEquals(2, preview.size)
        assertNull(preview[0].proposedValue)
        assertTrue(preview[0].requiresManualReview)
        assertEquals("Recuperar hasta el máximo", preview[0].detail)
        assertNull(preview[1].proposedValue)
        assertEquals("Recuperación manual", preview[1].detail)
    }

    @Test
    fun legacyResourceTextRemainsReviewOnlyAndIsNeverParsed() {
        val resource = CharacterResource(
            id = Uuid.random(),
            name = "Carga antigua",
            currentValue = 1,
            maxValue = 7,
            recovery = "Recupera 1d4+2 tras descansar",
        )

        val preview = previewTrackableRecovery(
            rest = CharacterRestKind.SHORT,
            resources = listOf(resource),
            resourceRecoveryRules = emptyList(),
        ).single()

        assertNull(preview.proposedValue)
        assertFalse(preview.hasAutomaticChange)
        assertTrue(preview.requiresManualReview)
        assertEquals("Recupera 1d4+2 tras descansar", preview.detail)
    }

    @Test
    fun applyIgnoresUnselectedAndReviewOnlyTargets() {
        val selectedId = Uuid.random()
        val untouchedId = Uuid.random()
        val reviewId = Uuid.random()
        val selected = CharacterCustomMarker(
            id = selectedId,
            name = "Seleccionado",
            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
            currentValue = 1,
            maxValue = 4,
            recovery = CharacterTrackableRecovery(
                cadence = CharacterRecoveryCadence.LONG_REST,
                amountMode = CharacterRecoveryAmountMode.TO_MAX,
            ),
        )
        val untouched = CharacterCustomMarker(
            id = untouchedId,
            name = "No seleccionado",
            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
            currentValue = 2,
            maxValue = 5,
            recovery = CharacterTrackableRecovery(
                cadence = CharacterRecoveryCadence.LONG_REST,
                amountMode = CharacterRecoveryAmountMode.TO_MAX,
            ),
        )
        val reviewOnly = CharacterCustomMarker(
            id = reviewId,
            name = "Manual",
            currentValue = 3,
            recovery = CharacterTrackableRecovery(cadence = CharacterRecoveryCadence.MANUAL),
        )
        val markers = listOf(selected, untouched, reviewOnly)
        val preview = previewTrackableRecovery(
            rest = CharacterRestKind.LONG,
            resources = emptyList(),
            resourceRecoveryRules = emptyList(),
            markers = markers,
        )

        val applied = applySelectedTrackableRecovery(
            resources = emptyList(),
            markers = markers,
            preview = preview,
            selectedTargets = setOf(
                CharacterTrackableTarget(CharacterTrackableTargetKind.MARKER, selectedId),
                CharacterTrackableTarget(CharacterTrackableTargetKind.MARKER, reviewId),
            ),
        )

        assertEquals(4, applied.markers[0].currentValue)
        assertEquals(2, applied.markers[1].currentValue)
        assertEquals(3, applied.markers[2].currentValue)
    }
}
