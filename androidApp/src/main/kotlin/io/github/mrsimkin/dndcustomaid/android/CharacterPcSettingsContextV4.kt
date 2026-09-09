package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import kotlin.uuid.Uuid

internal data class CharacterPcSettingsContextV4(
    val successorState: CharacterSuccessorState,
    val onSuccessorStateChange: (CharacterSuccessorState) -> Unit,
)

internal val LocalCharacterPcSettingsContextV4 =
    staticCompositionLocalOf<CharacterPcSettingsContextV4?> { null }

@Composable
internal fun CharacterPcSettingsStateProviderV4(
    characterId: Uuid,
    successorRepository: CharacterSuccessorRepository,
    content: @Composable () -> Unit,
) {
    var successorState by remember(characterId) {
        mutableStateOf(successorRepository.state(characterId))
    }
    val androidContext = LocalContext.current.applicationContext
    val hapticStore = remember(androidContext) { CharacterHapticPreferencesStore(androidContext) }

    val settingsContext = CharacterPcSettingsContextV4(
        successorState = successorState,
        onSuccessorStateChange = { updated ->
            if (updated != successorState) {
                successorState = successorRepository.saveState(characterId, updated)
            }
        },
    )

    CharacterHapticSettingsProviderV4(store = hapticStore) {
        CompositionLocalProvider(LocalCharacterPcSettingsContextV4 provides settingsContext) {
            content()
        }
    }
}
