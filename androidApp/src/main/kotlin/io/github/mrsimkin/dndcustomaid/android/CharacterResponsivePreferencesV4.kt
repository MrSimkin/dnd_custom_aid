package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable

@Composable
internal fun requestedCardColumnsV4(): Int {
    val layoutContext = characterLayoutContextV4()
    val preferences = LocalUiPreferencesV4.current
    return when (layoutContext.formFactor) {
        CharacterFormFactorV4.PHONE_PORTRAIT -> preferences.phonePortraitColumns
        CharacterFormFactorV4.PHONE_LANDSCAPE -> preferences.phoneLandscapeColumns
        CharacterFormFactorV4.TABLET_PORTRAIT -> preferences.tabletPortraitColumns
        CharacterFormFactorV4.TABLET_LANDSCAPE -> preferences.tabletLandscapeColumns
    }
}

@Composable
internal fun constrainedCardColumnsV4(
    wide: Boolean,
    phoneMax: Int = 2,
    wideMax: Int = 4,
): Int {
    val layoutContext = characterLayoutContextV4()
    val effectiveMax = if (layoutContext.isTablet) wideMax else phoneMax
    return requestedCardColumnsV4().coerceIn(1, effectiveMax)
}
