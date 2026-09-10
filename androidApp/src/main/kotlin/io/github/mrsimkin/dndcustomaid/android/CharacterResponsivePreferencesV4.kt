package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

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
    val requested = requestedCardColumnsV4()
    val baseMax = if (layoutContext.isTablet && wide) wideMax else phoneMax
    if (!layoutContext.isTablet || !wide) {
        return requested.coerceIn(1, baseMax.coerceAtLeast(1))
    }

    val fontScale = LocalDensity.current.fontScale
    val tabletReadableMax = when (layoutContext.formFactor) {
        CharacterFormFactorV4.TABLET_PORTRAIT -> when {
            fontScale >= 1.60f -> 1
            fontScale >= 1.35f -> minOf(2, baseMax)
            else -> baseMax
        }
        CharacterFormFactorV4.TABLET_LANDSCAPE -> when {
            fontScale >= 1.80f -> 1
            fontScale >= 1.50f -> minOf(2, baseMax)
            fontScale >= 1.30f -> minOf(3, baseMax)
            else -> baseMax
        }
        else -> baseMax
    }
    return requested.coerceIn(1, tabletReadableMax.coerceAtLeast(1))
}
