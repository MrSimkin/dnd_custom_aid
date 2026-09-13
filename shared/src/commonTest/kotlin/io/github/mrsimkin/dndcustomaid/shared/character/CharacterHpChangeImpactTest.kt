package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterHpChangeImpactTest {
    @Test
    fun unchangedHpTargetsNoFeedback() {
        assertEquals(
            CharacterHpChangeImpact.NONE,
            characterHpChangeImpact(20, 30, 5, 20, 30, 5),
        )
    }

    @Test
    fun healingOrExactHitPointChangeTargetsHitPointsOnly() {
        assertEquals(
            CharacterHpChangeImpact.HIT_POINTS,
            characterHpChangeImpact(20, 30, 5, 25, 30, 5),
        )
        assertEquals(
            CharacterHpChangeImpact.HIT_POINTS,
            characterHpChangeImpact(20, 30, 5, 20, 40, 5),
        )
    }

    @Test
    fun fullyAbsorbedDamageTargetsTemporaryHpOnly() {
        assertEquals(
            CharacterHpChangeImpact.TEMPORARY_HP,
            characterHpChangeImpact(20, 30, 5, 20, 30, 2),
        )
    }

    @Test
    fun spilloverDamageTargetsBothHpProjections() {
        assertEquals(
            CharacterHpChangeImpact.BOTH,
            characterHpChangeImpact(20, 30, 2, 17, 30, 0),
        )
    }
}
