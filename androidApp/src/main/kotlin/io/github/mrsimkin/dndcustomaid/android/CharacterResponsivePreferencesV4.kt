package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
internal fun requestedCardColumnsV4(): Int {
    val layoutContext = characterLayoutContextV4()
    return adaptiveCardColumnsV4(
        preferences = LocalUiPreferencesV4.current,
        layoutContext = layoutContext,
        wide = layoutContext.isTablet,
        phoneMax = 4,
        wideMax = 6,
        effectiveFontScale = LocalDensity.current.fontScale,
    )
}

@Composable
internal fun constrainedCardColumnsV4(
    wide: Boolean,
    phoneMax: Int = 2,
    wideMax: Int = 4,
): Int = adaptiveCardColumnsV4(
    preferences = LocalUiPreferencesV4.current,
    layoutContext = characterLayoutContextV4(),
    wide = wide,
    phoneMax = phoneMax,
    wideMax = wideMax,
    effectiveFontScale = LocalDensity.current.fontScale,
)

internal fun adaptiveCardColumnsV4(
    preferences: UiPreferences,
    layoutContext: CharacterLayoutContextV4,
    wide: Boolean,
    phoneMax: Int,
    wideMax: Int,
    effectiveFontScale: Float,
): Int {
    val density = if (layoutContext.isLandscape) {
        preferences.landscapeCardDensity
    } else {
        preferences.portraitCardDensity
    }
    val baseMax = if (layoutContext.isTablet && wide) wideMax else phoneMax
    val textPressure = effectiveFontScale.coerceIn(0.75f, 1.80f)
    val spacingPressure = preferences.spacingScalePercent.coerceIn(50, 150) / 100f
    val uiPressure = 0.90f + (0.10f * spacingPressure)
    val minimumUsableCardWidthDp = density.minCardWidthDp * textPressure * uiPressure
    val widthBound = (layoutContext.availableWidthDp.coerceAtLeast(1) / minimumUsableCardWidthDp)
        .toInt()
        .coerceAtLeast(1)
    return widthBound.coerceIn(1, baseMax.coerceAtLeast(1))
}
