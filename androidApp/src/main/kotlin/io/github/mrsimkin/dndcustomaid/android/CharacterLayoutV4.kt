package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun CompactFieldLabelV4(
    text: String,
    modifier: Modifier = Modifier,
) {
    val fontScale = LocalDensity.current.fontScale
    val labelSlotHeight = when {
        fontScale >= 1.25f -> 40.dp
        fontScale >= 1.10f -> 34.dp
        else -> 29.dp
    }
    Text(
        text = text,
        modifier = modifier.height(labelSlotHeight),
        style = MaterialTheme.typography.labelSmall,
        maxLines = 2,
    )
}

@Composable
internal fun characterCompactTextAreaMinLinesV4(preferredLines: Int = 2): Int {
    val preferred = preferredLines.coerceAtLeast(1)
    val spacingFraction = LocalUiPreferencesV4.current.spacingScalePercent.coerceIn(40, 100) / 100f
    return (preferred * spacingFraction).roundToInt().coerceIn(1, preferred)
}

@Composable
internal fun CompactMenuSurfaceV4(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .heightIn(min = 34.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(3.dp), vertical = appSpacingV4(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}
