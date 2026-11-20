package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/** Owner's G (2026-11-20): «dificultad del encuentro según el grupo». */
class EncounterDifficultyTest {
    private val party = listOf(3, 3, 3, 3)

    @Test
    fun aChallengeRatingIsWorthWhatTheSrdSays() {
        assertEquals(10, experienceForChallengeRating("0"))
        assertEquals(25, experienceForChallengeRating("1/8"))
        assertEquals(100, experienceForChallengeRating("1/2"))
        assertEquals(100, experienceForChallengeRating("0,5"), "the DM may write it with a comma")
        assertEquals(700, experienceForChallengeRating(" VD 3 "))
        assertEquals(155_000, experienceForChallengeRating("30"))
        assertEquals(null, experienceForChallengeRating(""))
        assertEquals(null, experienceForChallengeRating("muy duro"))
    }

    @Test
    fun oneCreatureAgainstFourLevelThreeCharacters() {
        // Four level 3: easy 300, medium 600, hard 900, deadly 1600.
        val verdict = EncounterDifficulty.of(party, listOf("2"))

        assertEquals(listOf(300, 600, 900, 1_600), verdict.thresholds)
        assertEquals(450, verdict.experience)
        assertEquals(450, verdict.adjustedExperience, "one creature counts for what it is worth")
        assertEquals(EncounterDifficultyLevel.EASY, verdict.level)
        assertEquals("Fácil para 4 PJ · 450 PX ajustados", verdict.spanishSummary())
    }

    @Test
    fun severalCreaturesAtOnceHitHarderThanTheirSum() {
        val four = EncounterDifficulty.of(party, List(4) { "1" })

        assertEquals(800, four.experience)
        assertEquals(1_600, four.adjustedExperience, "four at once count double")
        assertEquals(EncounterDifficultyLevel.DEADLY, four.level)
    }

    @Test
    fun theSameEncounterIsWorseForTwoAndCalmerForSix() {
        val creatures = List(3) { "1" }
        val two = EncounterDifficulty.of(listOf(3, 3), creatures)
        val six = EncounterDifficulty.of(List(6) { 3 }, creatures)

        assertTrue(two.adjustedExperience > six.adjustedExperience, "${two.adjustedExperience} vs ${six.adjustedExperience}")
        assertEquals(EncounterDifficultyLevel.DEADLY, two.level)
        assertEquals(EncounterDifficultyLevel.MEDIUM, six.level)
    }

    @Test
    fun withoutChallengeRatingsOrWithoutCharactersItSaysSoInsteadOfGuessing() {
        val noRatings = EncounterDifficulty.of(party, listOf("", ""))
        assertTrue(noRatings.unknown)
        assertEquals("Sin VD en las criaturas: no se puede calcular la dificultad.", noRatings.spanishSummary())

        val noParty = EncounterDifficulty.of(emptyList(), listOf("5"))
        assertEquals(0, noParty.partySize)
        assertEquals("Todavía no hay PJ en la campaña para comparar.", noParty.spanishSummary())
    }

    @Test
    fun anEncountersParticipantsBecomeOneRatingPerCreature() {
        val goblin = Uuid.random()
        val ogre = Uuid.random()
        val ratings = EncounterDifficulty.challengeRatings(
            listOf(
                EncounterParticipant(sourceContentId = goblin, quantity = 4),
                EncounterParticipant(sourceContentId = ogre, quantity = 1),
                EncounterParticipant(label = "Rata improvisada", quantity = 2),
            ),
        ) { id -> if (id == goblin) "1/4" else "2" }

        assertEquals(listOf("1/4", "1/4", "1/4", "1/4", "2", "", ""), ratings)
        val verdict = EncounterDifficulty.of(party, ratings)
        assertEquals(650, verdict.experience, "the improvised ones add nothing, because nobody said what they are worth")
        assertEquals(7, verdict.creatureCount, "but they are still bodies on the table")
    }
}
