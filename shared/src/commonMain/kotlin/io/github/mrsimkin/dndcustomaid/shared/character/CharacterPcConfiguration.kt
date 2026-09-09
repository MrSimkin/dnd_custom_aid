package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Per-character presentation configuration introduced by the Phase 4A C2 settings redesign.
 * Inspiration value itself stays authoritative on CharacterSheet; this row only controls whether
 * that canonical value is surfaced in the character UI.
 */
@Serializable
data class CharacterPcConfiguration(
    val inspirationVisible: Boolean = true,
)

class CharacterPcConfigurationRepository(
    private val database: AppDatabase,
) {
    fun configuration(characterId: Uuid): CharacterPcConfiguration {
        requireCharacterExists(characterId)
        return database.characterPcConfigurationQueries.selectCharacterPcConfiguration(characterId.toString()) {
                _, inspirationVisible ->
            CharacterPcConfiguration(
                inspirationVisible = inspirationVisible != 0L,
            )
        }.executeAsOneOrNull() ?: CharacterPcConfiguration()
    }

    fun saveConfiguration(
        characterId: Uuid,
        configuration: CharacterPcConfiguration,
    ): CharacterPcConfiguration {
        requireCharacterExists(characterId)
        database.characterPcConfigurationQueries.upsertCharacterPcConfiguration(
            character_id = characterId.toString(),
            inspiration_visible = if (configuration.inspirationVisible) 1 else 0,
        )
        return configuration(characterId)
    }

    private fun requireCharacterExists(characterId: Uuid) {
        val exists = database.characterQueries.selectCharacterById(characterId.toString()).executeAsOneOrNull()
        require(exists != null) { "Character must already exist locally." }
    }
}
