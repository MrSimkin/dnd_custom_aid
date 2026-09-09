package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import kotlin.uuid.Uuid

/** UI-only compatibility view; persistence remains owned by CharacterSuccessorPreferences. */
internal data class CharacterPcPresentationV4(
    val inspirationVisible: Boolean,
)

internal data class CharacterPcSettingsContextV4(
    val characterId: Uuid,
    val successorState: CharacterSuccessorState,
    val onSuccessorStateChange: (CharacterSuccessorState) -> Unit,
    /** Fresh read of the authoritative core sheet; this callback never owns or persists a copy. */
    val loadCanonicalSheet: () -> CharacterSheet?,
) {
    val pcConfiguration: CharacterPcPresentationV4
        get() = CharacterPcPresentationV4(successorState.preferences.inspirationVisible)

    val onPcConfigurationChange: (CharacterPcPresentationV4) -> Unit
        get() = { updated ->
            onSuccessorStateChange(
                successorState.copy(
                    preferences = successorState.preferences.copy(
                        inspirationVisible = updated.inspirationVisible,
                    ),
                ),
            )
        }
}

internal val LocalCharacterPcSettingsContextV4 =
    staticCompositionLocalOf<CharacterPcSettingsContextV4?> { null }

@Composable
internal fun CharacterPcSettingsStateProviderV4(
    characterId: Uuid,
    characterRepository: CharacterRepository,
    successorRepository: CharacterSuccessorRepository,
    content: @Composable () -> Unit,
) {
    var successorState by remember(characterId) {
        mutableStateOf(successorRepository.state(characterId))
    }
    val androidContext = LocalContext.current.applicationContext
    val hapticStore = remember(androidContext) { CharacterHapticPreferencesStore(androidContext) }

    val settingsContext = CharacterPcSettingsContextV4(
        characterId = characterId,
        successorState = successorState,
        onSuccessorStateChange = { updated ->
            if (updated != successorState) {
                successorState = successorRepository.saveState(characterId, updated)
            }
        },
        loadCanonicalSheet = { characterRepository.character(characterId) },
    )

    CharacterHapticSettingsProviderV4(store = hapticStore) {
        CompositionLocalProvider(LocalCharacterPcSettingsContextV4 provides settingsContext) {
            content()
        }
    }
}
