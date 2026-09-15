package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun StableForwardIndicatorV4(
    contentDescription: String = "Abrir",
) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(
        modifier = Modifier
            .size(20.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        val stroke = 2.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.36f, size.height * 0.24f),
            end = Offset(size.width * 0.65f, size.height * 0.50f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.65f, size.height * 0.50f),
            end = Offset(size.width * 0.36f, size.height * 0.76f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}
