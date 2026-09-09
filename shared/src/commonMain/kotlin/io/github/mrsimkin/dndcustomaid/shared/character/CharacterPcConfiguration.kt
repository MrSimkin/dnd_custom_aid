package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Per-character presentation/interaction configuration introduced by the Phase 4A C2 settings
 * redesign. Existing domain owners remain authoritative for character values themselves:
 * Inspiration value stays on CharacterSheet and haptics-enabled stays on CharacterClosureState.
 */
@Serializable
data class CharacterPcConfiguration(
    val inspirationVisible: Boolean = true,
    val hapticStrength: CharacterHapticStrength = CharacterHapticStrength.MEDIUM,
    val hapticDuration: CharacterHapticDuration = CharacterHapticDuration.SHORT,
)

class CharacterPcConfigurationRepository(
    private val database: AppDatabase,
) {
    fun configuration(characterId: Uuid): CharacterPcConfiguration {
        requireCharacterExists(characterId)
        return database.characterPcConfigurationQueries.selectCharacterPcConfiguration(characterId.toString()) {
                _, inspirationVisible, hapticStrength, hapticDuration ->
            CharacterPcConfiguration(
                inspirationVisible = inspirationVisible != 0L,
                hapticStrength = enumOrDefault(hapticStrength, CharacterHapticStrength.MEDIUM),
                hapticDuration = enumOrDefault(hapticDuration, CharacterHapticDuration.SHORT),
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
            haptic_strength = configuration.hapticStrength.name,
            haptic_duration = configuration.hapticDuration.name,
        )
        return configuration(characterId)
    }

    private fun requireCharacterExists(characterId: Uuid) {
        val exists = database.characterQueries.selectCharacterById(characterId.toString()).executeAsOneOrNull()
        require(exists != null) { "Character must already exist locally." }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String, default: T): T =
        runCatching { enumValueOf<T>(value) }.getOrDefault(default)
}
