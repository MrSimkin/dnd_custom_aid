package io.github.mrsimkin.dndcustomaid.android

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
internal fun requestedCardColumnsV4(): Int {
    val configuration = LocalConfiguration.current
    val preferences = LocalUiPreferencesV4.current
    val tabletLike = minOf(configuration.screenWidthDp, configuration.screenHeightDp) >= 600
    val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    return when {
        tabletLike && landscape -> preferences.tabletLandscapeColumns
        tabletLike -> preferences.tabletPortraitColumns
        landscape -> preferences.phoneLandscapeColumns
        else -> preferences.phonePortraitColumns
    }
}

@Composable
internal fun constrainedCardColumnsV4(
    wide: Boolean,
    phoneMax: Int = 2,
    wideMax: Int = 4,
): Int = requestedCardColumnsV4().coerceIn(1, if (wide) wideMax else phoneMax)
