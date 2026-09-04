package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import kotlin.uuid.Uuid

internal enum class CharacterNavigationPresentationV4 {
    TOP_TABS,
    SIDE_RAIL,
}

internal const val CHARACTER_NAVIGATION_RAIL_MIN_WIDTH_DP = 840f

internal fun characterNavigationPresentationForWidthV4(
    availableWidthDp: Float,
): CharacterNavigationPresentationV4 =
    if (availableWidthDp >= CHARACTER_NAVIGATION_RAIL_MIN_WIDTH_DP) {
        CharacterNavigationPresentationV4.SIDE_RAIL
    } else {
        CharacterNavigationPresentationV4.TOP_TABS
    }

internal class CharacterNavigationPreferenceStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences(
        "character_navigation_preferences_v4",
        Context.MODE_PRIVATE,
    )

    fun loadLastTabName(characterId: Uuid): String? =
        preferences.getString(lastTabKey(characterId), null)

    fun saveLastTabName(characterId: Uuid, tabName: String) {
        preferences.edit()
            .putString(lastTabKey(characterId), tabName)
            .apply()
    }

    private fun lastTabKey(characterId: Uuid): String =
        "last_tab_${characterId}"
}
