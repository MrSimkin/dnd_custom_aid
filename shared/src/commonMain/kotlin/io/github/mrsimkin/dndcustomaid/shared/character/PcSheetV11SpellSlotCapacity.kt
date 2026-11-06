package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * C445 (owner 2026-11-06): each spell level has a maximum whatever the class, and P1 always draws
 * that many boxes, with the character's real count beside them or a space to write it.
 *
 * The maximum is the character-level 20 row of the SRD 5.1 and 5.2.1 spell slot tables
 * (4, 3, 3, 3, 3, 2, 2, 1, 1). Level 5 is 4 because a level 17+ Warlock has four level 5 Pact Magic slots.
 * Levels 10..12 (C448) have no SRD table; they print only with epic levels enabled. Their count is the
 * golden's three boxes, provisional until the owner decides.
 */
object PcSheetV11SpellSlotCapacity {
    private val MAXIMUM = listOf(4, 3, 3, 3, 4, 2, 2, 1, 1, 3, 3, 3)

    fun maximum(level: Int): Int {
        require(level in 1..12) { "Spell level out of the 1..12 grid: $level" }
        return MAXIMUM[level - 1]
    }

    /** A saved total above the table still gets one box per real slot, as far as the cell allows. */
    fun boxes(level: Int, savedTotal: Int?): Int =
        maxOf(maximum(level), savedTotal?.takeIf { it in 0..6 } ?: 0)
}
