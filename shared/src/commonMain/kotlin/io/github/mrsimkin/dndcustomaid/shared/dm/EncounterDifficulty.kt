package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

/*
 * Owner's G (2026-11-20): «dificultad del encuentro según el grupo». Until now the App showed how
 * many creatures an encounter had and nothing about what that means for this party, so the DM had
 * to work it out on paper every time.
 *
 * This is the SRD arithmetic: every creature's challenge rating is worth experience, several
 * creatures at once hit harder than their sum, and each character's level sets what their party can
 * take. The verdict is the DM's guide, never a rule: the App says what it counted so the DM can
 * disagree with it.
 */

/** How hard the encounter looks for this party. */
enum class EncounterDifficultyLevel(val spanish: String) {
    TRIVIAL("Trivial"),
    EASY("Fácil"),
    MEDIUM("Media"),
    HARD("Difícil"),
    DEADLY("Mortal"),
}

/** What the App counted, so the DM can see where the verdict came from. */
data class EncounterDifficultyVerdict(
    val level: EncounterDifficultyLevel,
    /** The plain sum of the creatures' experience. */
    val experience: Int,
    /** The sum once the number of creatures and the size of the party are taken into account. */
    val adjustedExperience: Int,
    val creatureCount: Int,
    val partySize: Int,
    /** What this party can take, from «Fácil» to «Mortal». */
    val thresholds: List<Int>,
    /** True when no creature had a challenge rating to count. */
    val unknown: Boolean,
) {
    /** One line for the DM: «Difícil para 4 PJ (1.800 PX ajustados)». */
    fun spanishSummary(): String = when {
        // Tablet walk of 0.7.0-preqa.6: an encounter with nothing in it read «Trivial», which
        // sounds like a verdict. Nothing to fight is not an easy fight.
        creatureCount == 0 -> "Sin criaturas todavía: no hay nada que medir."
        unknown -> "Sin VD en las criaturas: no se puede calcular la dificultad."
        partySize == 0 -> "Todavía no hay PJ en la campaña para comparar."
        else -> "${level.spanish} para $partySize PJ · ${adjustedExperience} PX ajustados"
    }
}

/** The experience a challenge rating is worth. «1/2», «0,5» and «0.5» all mean the same thing. */
fun experienceForChallengeRating(challengeRating: String): Int? {
    val text = challengeRating.trim().lowercase().replace(',', '.').removePrefix("vd ").removePrefix("cr ").trim()
    if (text.isEmpty()) return null
    val value = when (text) {
        "0", "0.0" -> 0.0
        "1/8", "0.125" -> 0.125
        "1/4", "0.25" -> 0.25
        "1/2", "0.5" -> 0.5
        else -> text.toDoubleOrNull() ?: return null
    }
    return CHALLENGE_RATING_EXPERIENCE[value]
}

private val CHALLENGE_RATING_EXPERIENCE: Map<Double, Int> = mapOf(
    0.0 to 10, 0.125 to 25, 0.25 to 50, 0.5 to 100,
    1.0 to 200, 2.0 to 450, 3.0 to 700, 4.0 to 1_100, 5.0 to 1_800,
    6.0 to 2_300, 7.0 to 2_900, 8.0 to 3_900, 9.0 to 5_000, 10.0 to 5_900,
    11.0 to 7_200, 12.0 to 8_400, 13.0 to 10_000, 14.0 to 11_500, 15.0 to 13_000,
    16.0 to 15_000, 17.0 to 18_000, 18.0 to 20_000, 19.0 to 22_000, 20.0 to 25_000,
    21.0 to 33_000, 22.0 to 41_000, 23.0 to 50_000, 24.0 to 62_000, 25.0 to 75_000,
    26.0 to 90_000, 27.0 to 105_000, 28.0 to 120_000, 29.0 to 135_000, 30.0 to 155_000,
)

/** What one character of this level can take: «Fácil», «Media», «Difícil», «Mortal». */
private val LEVEL_THRESHOLDS: Map<Int, List<Int>> = mapOf(
    1 to listOf(25, 50, 75, 100),
    2 to listOf(50, 100, 150, 200),
    3 to listOf(75, 150, 225, 400),
    4 to listOf(125, 250, 375, 500),
    5 to listOf(250, 500, 750, 1_100),
    6 to listOf(300, 600, 900, 1_400),
    7 to listOf(350, 750, 1_100, 1_700),
    8 to listOf(450, 900, 1_400, 2_100),
    9 to listOf(550, 1_100, 1_600, 2_400),
    10 to listOf(600, 1_200, 1_900, 2_800),
    11 to listOf(800, 1_600, 2_400, 3_600),
    12 to listOf(1_000, 2_000, 3_000, 4_500),
    13 to listOf(1_100, 2_200, 3_400, 5_100),
    14 to listOf(1_250, 2_500, 3_800, 5_700),
    15 to listOf(1_400, 2_800, 4_300, 6_400),
    16 to listOf(1_600, 3_200, 4_800, 7_200),
    17 to listOf(2_000, 3_900, 5_900, 8_800),
    18 to listOf(2_100, 4_200, 6_300, 9_500),
    19 to listOf(2_400, 4_900, 7_300, 10_900),
    20 to listOf(2_800, 5_700, 8_500, 12_700),
)

/** Several creatures at once hit harder than their experience adds up to. */
private fun crowdFactor(creatures: Int, partySize: Int): Double {
    val step = when {
        creatures <= 1 -> 1
        creatures == 2 -> 2
        creatures <= 6 -> 3
        creatures <= 10 -> 4
        creatures <= 14 -> 5
        else -> 6
    }
    // A small party feels it more; a big one feels it less.
    val moved = when {
        partySize in 1..2 -> step + 1
        partySize >= 6 -> step - 1
        else -> step
    }.coerceIn(0, 6)
    return listOf(0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0)[moved]
}

object EncounterDifficulty {
    /** One challenge rating per creature that will fight, from an encounter's participants. */
    fun challengeRatings(
        participants: List<EncounterParticipant>,
        ratingOf: (Uuid) -> String?,
    ): List<String> = participants.flatMap { participant ->
        val rating = participant.sourceContentId?.let(ratingOf).orEmpty()
        List(participant.quantity) { rating }
    }

    /**
     * [partyLevels] is one level per character at the table, [creatures] one challenge rating per
     * creature that will fight, repeated as many times as it appears.
     */
    fun of(partyLevels: List<Int>, creatures: List<String>): EncounterDifficultyVerdict {
        val counted = creatures.mapNotNull(::experienceForChallengeRating)
        val experience = counted.sum()
        val thresholds = (0..3).map { column ->
            partyLevels.sumOf { level -> LEVEL_THRESHOLDS[level.coerceIn(1, 20)]!![column] }
        }
        if (creatures.isEmpty() || counted.isEmpty() || partyLevels.isEmpty()) {
            return EncounterDifficultyVerdict(
                level = EncounterDifficultyLevel.TRIVIAL,
                experience = experience,
                adjustedExperience = experience,
                creatureCount = creatures.size,
                partySize = partyLevels.size,
                thresholds = thresholds,
                unknown = creatures.isNotEmpty() && counted.isEmpty(),
            )
        }

        val adjusted = (experience * crowdFactor(creatures.size, partyLevels.size)).roundToInt()
        val level = when {
            adjusted >= thresholds[3] -> EncounterDifficultyLevel.DEADLY
            adjusted >= thresholds[2] -> EncounterDifficultyLevel.HARD
            adjusted >= thresholds[1] -> EncounterDifficultyLevel.MEDIUM
            adjusted >= thresholds[0] -> EncounterDifficultyLevel.EASY
            else -> EncounterDifficultyLevel.TRIVIAL
        }
        return EncounterDifficultyVerdict(
            level = level,
            experience = experience,
            adjustedExperience = adjusted,
            creatureCount = creatures.size,
            partySize = partyLevels.size,
            thresholds = thresholds,
            unknown = false,
        )
    }
}
