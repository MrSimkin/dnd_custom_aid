package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

internal enum class CharacterHapticStrengthV4(val label: String, val amplitude: Int) {
    SOFT("Suave", 72),
    MEDIUM("Media", 140),
    STRONG("Fuerte", 220),
}

internal enum class CharacterHapticDurationV4(val label: String, val milliseconds: Long) {
    SHORT("Corta", 18L),
    MEDIUM("Media", 32L),
    LONG("Larga", 50L),
}

internal data class CharacterHapticPreferencesV4(
    val strength: CharacterHapticStrengthV4 = CharacterHapticStrengthV4.MEDIUM,
    val duration: CharacterHapticDurationV4 = CharacterHapticDurationV4.SHORT,
)

internal data class CharacterHapticSettingsContextV4(
    val preferences: CharacterHapticPreferencesV4,
    val onChange: (CharacterHapticPreferencesV4) -> Unit,
)

internal val LocalCharacterHapticSettingsV4 = staticCompositionLocalOf {
    CharacterHapticSettingsContextV4(CharacterHapticPreferencesV4()) {}
}

internal class CharacterHapticPreferencesStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): CharacterHapticPreferencesV4 = CharacterHapticPreferencesV4(
        strength = preferences.getString(KEY_STRENGTH, null)
            ?.let { runCatching { CharacterHapticStrengthV4.valueOf(it) }.getOrNull() }
            ?: CharacterHapticStrengthV4.MEDIUM,
        duration = preferences.getString(KEY_DURATION, null)
            ?.let { runCatching { CharacterHapticDurationV4.valueOf(it) }.getOrNull() }
            ?: CharacterHapticDurationV4.SHORT,
    )

    fun save(value: CharacterHapticPreferencesV4) {
        preferences.edit()
            .putString(KEY_STRENGTH, value.strength.name)
            .putString(KEY_DURATION, value.duration.name)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "character_haptic_preferences"
        const val KEY_STRENGTH = "strength"
        const val KEY_DURATION = "duration"
    }
}

@Composable
internal fun CharacterHapticSettingsProviderV4(
    store: CharacterHapticPreferencesStore,
    content: @Composable () -> Unit,
) {
    var preferences by remember { mutableStateOf(store.load()) }
    val context = CharacterHapticSettingsContextV4(
        preferences = preferences,
        onChange = { updated ->
            preferences = updated
            store.save(updated)
        },
    )
    CompositionLocalProvider(LocalCharacterHapticSettingsV4 provides context) {
        content()
    }
}
