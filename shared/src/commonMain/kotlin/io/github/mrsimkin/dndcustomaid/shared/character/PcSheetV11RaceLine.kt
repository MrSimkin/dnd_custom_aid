package io.github.mrsimkin.dndcustomaid.shared.character

/** D-0076 3.4: the race of the identity row, with its subrace when one exists: `Elfo — Alto elfo`. */
object PcSheetV11RaceLine {
    fun of(background: CharacterBackground, successor: CharacterSuccessorState): String {
        val race = successor.speciesIdentity?.name?.trim().orEmpty().ifBlank { background.race.trim() }
        val subrace = successor.subraceIdentity?.name?.trim().orEmpty()
        return when {
            subrace.isBlank() -> race
            race.isBlank() -> subrace
            race.contains(subrace, ignoreCase = true) -> race
            else -> race + " — " + subrace
        }
    }
}
