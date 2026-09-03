package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterRestOperationsTest {
    private fun resource(
        name: String,
        current: Int,
        max: Int? = null,
        recovery: String? = null,
    ) = CharacterResource(
        id = Uuid.random(),
        name = name,
        currentValue = current,
        maxValue = max,
        recovery = recovery,
    )

    @Test
    fun shortRestIncludesMatchingAndManualResourcesButNotLongRestOnly() {
        val short = resource("Dados", 1, 4)
        val long = resource("Carga", 0, 1)
        val manual = resource("Homebrew", 2, 5, "Según la escena")
        val rules = listOf(
            CharacterResourceRecovery(short.id, CharacterRecoveryCadence.SHORT_REST, CharacterRecoveryAmountMode.TO_MAX),
            CharacterResourceRecovery(long.id, CharacterRecoveryCadence.LONG_REST, CharacterRecoveryAmountMode.TO_MAX),
        )

        val preview = previewResourceRecovery(CharacterRestKind.SHORT, listOf(short, long, manual), rules)

        assertEquals(listOf(short.id, manual.id), preview.map { it.resourceId })
        assertEquals(4, preview.first().proposedValue)
        assertTrue(preview.last().requiresManualReview)
        assertEquals("Según la escena", preview.last().detail)
    }

    @Test
    fun fixedRecoveryCapsAtKnownMaximum() {
        val resource = resource("Puntos", 4, 5)
        val rules = listOf(
            CharacterResourceRecovery(
                resource.id,
                CharacterRecoveryCadence.SHORT_OR_LONG_REST,
                CharacterRecoveryAmountMode.FIXED,
                fixedAmount = 3,
            ),
        )

        val preview = previewResourceRecovery(CharacterRestKind.LONG, listOf(resource), rules).single()

        assertEquals(5, preview.proposedValue)
        assertTrue(preview.hasAutomaticChange)
    }

    @Test
    fun manualStructuredRuleNeverProposesNumericMutation() {
        val resource = resource("Reserva extraña", 2, 10, "Texto heredado")
        val preview = previewResourceRecovery(
            CharacterRestKind.LONG,
            listOf(resource),
            listOf(
                CharacterResourceRecovery(
                    resource.id,
                    CharacterRecoveryCadence.MANUAL,
                    CharacterRecoveryAmountMode.NONE,
                    notes = "Recupera según decisión del DM",
                ),
            ),
        ).single()

        assertNull(preview.proposedValue)
        assertTrue(preview.requiresManualReview)
        assertEquals("Recupera según decisión del DM", preview.detail)
    }

    @Test
    fun applyChangesOnlyExplicitlySelectedNumericRows() {
        val first = resource("Primero", 1, 4)
        val second = resource("Segundo", 2, 6)
        val manual = resource("Manual", 3, 8, "Editar a mano")
        val rules = listOf(
            CharacterResourceRecovery(first.id, CharacterRecoveryCadence.LONG_REST, CharacterRecoveryAmountMode.TO_MAX),
            CharacterResourceRecovery(second.id, CharacterRecoveryCadence.LONG_REST, CharacterRecoveryAmountMode.TO_MAX),
        )
        val resources = listOf(first, second, manual)
        val preview = previewResourceRecovery(CharacterRestKind.LONG, resources, rules)

        val applied = applySelectedResourceRecovery(
            resources = resources,
            preview = preview,
            selectedResourceIds = setOf(first.id, manual.id),
        )

        assertEquals(4, applied[0].currentValue)
        assertEquals(2, applied[1].currentValue)
        assertEquals(3, applied[2].currentValue)
        assertFalse(applied[1].currentValue == second.maxValue)
    }

    @Test
    fun alreadyFullResourceCanAppearWithoutClaimingAChange() {
        val resource = resource("Completo", 3, 3)
        val preview = previewResourceRecovery(
            CharacterRestKind.SHORT,
            listOf(resource),
            listOf(
                CharacterResourceRecovery(
                    resource.id,
                    CharacterRecoveryCadence.SHORT_REST,
                    CharacterRecoveryAmountMode.TO_MAX,
                ),
            ),
        ).single()

        assertEquals(3, preview.proposedValue)
        assertFalse(preview.hasAutomaticChange)
    }
}
