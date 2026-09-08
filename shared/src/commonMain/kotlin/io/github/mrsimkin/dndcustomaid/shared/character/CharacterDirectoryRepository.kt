package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

class CharacterDirectoryRepository(
    private val database: AppDatabase,
    private val characterRepository: CharacterRepository,
) {
    fun listAllCharacters(): List<CharacterSheet> =
        database.characterDirectoryQueries.selectAllCharacterIds()
            .executeAsList()
            .mapNotNull { rawId ->
                runCatching { Uuid.parse(rawId) }
                    .getOrNull()
                    ?.let(characterRepository::character)
            }
}
