package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/**
 * Redesign A13 (owner decision D13, 2026-11-13): who plays each PC, as the DM names them, even
 * before that person has an account in the App. Kept on this device beside the sheet; the PC
 * sheet and its PDF (V11) are not changed.
 */
class CharacterPlayerRepository(private val database: AppDatabase) {
    fun player(characterId: Uuid): String? =
        database.characterPlayerQueries.selectPlayer(characterId.toString()).executeAsOneOrNull()

    fun setPlayer(characterId: Uuid, playerName: String?) {
        val clean = playerName?.trim().orEmpty()
        if (clean.isEmpty()) {
            database.characterPlayerQueries.deletePlayer(characterId.toString())
        } else {
            database.characterPlayerQueries.upsertPlayer(characterId.toString(), clean)
        }
    }
}
