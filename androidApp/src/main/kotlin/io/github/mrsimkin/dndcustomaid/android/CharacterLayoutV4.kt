package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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

/**
 * Reserves the same vertical label slot for adjacent compact controls.
 *
 * At 115%/130% an individual two-line label must not push only its own field
 * downward while neighboring fields stay higher. The slot scales with the
 * effective font scale (including Android accessibility scaling).
 */
@Composable
internal fun CompactFieldLabelV4(
    text: String,
    modifier: Modifier = Modifier,
) {
    val fontScale = LocalDensity.current.fontScale
    val labelSlotHeight = when {
        fontScale >= 1.25f -> 46.dp
        fontScale >= 1.10f -> 40.dp
        else -> 34.dp
    }
    Text(
        text = text,
        modifier = modifier.heightIn(min = labelSlotHeight),
        style = MaterialTheme.typography.labelSmall,
        maxLines = 2,
    )
}

/** Compact selector geometry matching the custom compact numeric/text fields. */
@Composable
internal fun CompactMenuSurfaceV4(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .heightIn(min = 38.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
    }
}
