package io.github.mrsimkin.dndcustomaid.android

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

internal enum class CharacterFormFactorV4 {
    PHONE_PORTRAIT,
    PHONE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
}

internal data class CharacterLayoutContextV4(
    val formFactor: CharacterFormFactorV4,
) {
    val isPhone: Boolean
        get() = formFactor == CharacterFormFactorV4.PHONE_PORTRAIT ||
            formFactor == CharacterFormFactorV4.PHONE_LANDSCAPE

    val isTablet: Boolean
        get() = !isPhone

    val isLandscape: Boolean
        get() = formFactor == CharacterFormFactorV4.PHONE_LANDSCAPE ||
            formFactor == CharacterFormFactorV4.TABLET_LANDSCAPE
}

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

@Composable
internal fun characterLayoutContextV4(): CharacterLayoutContextV4 {
    val configuration = LocalConfiguration.current
    return CharacterLayoutContextV4(
        formFactor = characterFormFactorV4(
            screenWidthDp = configuration.screenWidthDp,
            screenHeightDp = configuration.screenHeightDp,
            landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
        ),
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
