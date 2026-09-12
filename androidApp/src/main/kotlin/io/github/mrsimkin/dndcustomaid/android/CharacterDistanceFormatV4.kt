package io.github.mrsimkin.dndcustomaid.android

import kotlin.math.abs

/** Canonical V4 character distance presentation: imperial first, metric companion in decimetre precision. */
internal fun formatCharacterDistanceFeetV4(feet: Int): String {
    val metricTenths = feet * 3
    val wholeMeters = metricTenths / 10
    val remainder = abs(metricTenths % 10)
    val metric = if (remainder == 0) wholeMeters.toString() else "$wholeMeters,$remainder"
    return "$feet ft ($metric m)"
}

internal fun formatCharacterDistanceFeetV4(raw: String): String {
    val feet = raw.trim().toIntOrNull() ?: return raw.ifBlank { "—" }
    return formatCharacterDistanceFeetV4(feet)
}
