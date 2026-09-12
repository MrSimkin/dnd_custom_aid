package io.github.mrsimkin.dndcustomaid.android

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

internal enum class CharacterFormFactorV4 {
    PHONE_PORTRAIT,
    PHONE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
}

/**
 * Presentation pressure derived from the height actually available to the caller.
 *
 * This intentionally does not replace form-factor classification: phone landscape keeps the
 * phone navigation model. It gives individual surfaces a second axis for deciding how much
 * persistent chrome they can afford (P16).
 */
internal enum class CharacterVerticalSpaceV4 {
    COMFORTABLE,
    REDUCED,
    CONSTRAINED,
}

internal data class CharacterLayoutContextV4(
    val formFactor: CharacterFormFactorV4,
    val availableWidthDp: Int,
    val availableHeightDp: Int,
) {
    val isPhone: Boolean
        get() = formFactor == CharacterFormFactorV4.PHONE_PORTRAIT ||
            formFactor == CharacterFormFactorV4.PHONE_LANDSCAPE

    val isTablet: Boolean
        get() = !isPhone

    val isLandscape: Boolean
        get() = formFactor == CharacterFormFactorV4.PHONE_LANDSCAPE ||
            formFactor == CharacterFormFactorV4.TABLET_LANDSCAPE

    val verticalSpace: CharacterVerticalSpaceV4
        get() = characterVerticalSpaceForHeightV4(availableHeightDp)

    val isVerticallyConstrained: Boolean
        get() = verticalSpace == CharacterVerticalSpaceV4.CONSTRAINED
}

internal val LocalCharacterLayoutContextV4 = compositionLocalOf<CharacterLayoutContextV4?> { null }

internal const val CHARACTER_TABLET_MIN_SHORT_SIDE_DP = 600

internal fun characterFormFactorV4(
    screenWidthDp: Int,
    screenHeightDp: Int,
    landscape: Boolean,
): CharacterFormFactorV4 {
    val tablet = minOf(screenWidthDp, screenHeightDp) >= CHARACTER_TABLET_MIN_SHORT_SIDE_DP
    return when {
        tablet && landscape -> CharacterFormFactorV4.TABLET_LANDSCAPE
        tablet -> CharacterFormFactorV4.TABLET_PORTRAIT
        landscape -> CharacterFormFactorV4.PHONE_LANDSCAPE
        else -> CharacterFormFactorV4.PHONE_PORTRAIT
    }
}

/**
 * Implementation thresholds are deliberately kept here rather than spread through screens.
 * Acceptance is behavioral (usable content must remain practical), not tied to these exact
 * numbers; they may be tuned from device QA without changing the product contract.
 */
internal fun characterVerticalSpaceForHeightV4(availableHeightDp: Int): CharacterVerticalSpaceV4 = when {
    availableHeightDp < 440 -> CharacterVerticalSpaceV4.CONSTRAINED
    availableHeightDp < 640 -> CharacterVerticalSpaceV4.REDUCED
    else -> CharacterVerticalSpaceV4.COMFORTABLE
}

@Composable
internal fun characterLayoutContextForAvailableSizeV4(
    availableWidthDp: Int,
    availableHeightDp: Int,
): CharacterLayoutContextV4 {
    val configuration = LocalConfiguration.current
    return CharacterLayoutContextV4(
        formFactor = characterFormFactorV4(
            screenWidthDp = configuration.screenWidthDp,
            screenHeightDp = configuration.screenHeightDp,
            landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
        ),
        availableWidthDp = availableWidthDp.coerceAtLeast(0),
        availableHeightDp = availableHeightDp.coerceAtLeast(0),
    )
}

@Composable
internal fun characterLayoutContextV4(): CharacterLayoutContextV4 {
    LocalCharacterLayoutContextV4.current?.let { return it }
    val configuration = LocalConfiguration.current
    return characterLayoutContextForAvailableSizeV4(
        availableWidthDp = configuration.screenWidthDp,
        availableHeightDp = configuration.screenHeightDp,
    )
}

internal fun characterNavigationPresentationForLayoutV4(
    context: CharacterLayoutContextV4,
): CharacterNavigationPresentationV4 = when (context.formFactor) {
    CharacterFormFactorV4.TABLET_LANDSCAPE -> CharacterNavigationPresentationV4.SIDE_RAIL
    CharacterFormFactorV4.PHONE_PORTRAIT,
    CharacterFormFactorV4.PHONE_LANDSCAPE,
    CharacterFormFactorV4.TABLET_PORTRAIT,
    -> CharacterNavigationPresentationV4.TOP_TABS
}
