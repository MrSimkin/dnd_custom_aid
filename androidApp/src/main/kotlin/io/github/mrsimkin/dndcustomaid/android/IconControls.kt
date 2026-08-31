package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun StableBackIconButton(
    onClick: () -> Unit,
    contentDescription: String = "Volver",
) {
    val color = MaterialTheme.colorScheme.onSurface
    IconButton(onClick = onClick) {
        Canvas(
            modifier = Modifier
                .size(24.dp)
                .semantics { this.contentDescription = contentDescription },
        ) {
            val stroke = 2.2.dp.toPx()
            val y = size.height / 2f
            drawLine(
                color = color,
                start = Offset(size.width * 0.18f, y),
                end = Offset(size.width * 0.84f, y),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(size.width * 0.18f, y),
                end = Offset(size.width * 0.43f, size.height * 0.25f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(size.width * 0.18f, y),
                end = Offset(size.width * 0.43f, size.height * 0.75f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
internal fun StableSettingsIconButton(
    onClick: () -> Unit,
    contentDescription: String = "Ajustes",
) {
    val color = MaterialTheme.colorScheme.onSurface
    IconButton(onClick = onClick) {
        Canvas(
            modifier = Modifier
                .size(24.dp)
                .semantics { this.contentDescription = contentDescription },
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension * 0.28f
            val innerRadius = size.minDimension * 0.105f
            val stroke = 2.dp.toPx()

            drawCircle(
                color = color,
                radius = outerRadius,
                center = center,
                style = Stroke(width = stroke),
            )
            drawCircle(
                color = color,
                radius = innerRadius,
                center = center,
                style = Stroke(width = stroke),
            )

            repeat(8) { index ->
                val angle = Math.PI * index / 4.0
                val startRadius = outerRadius * 1.08f
                val endRadius = outerRadius * 1.45f
                val start = Offset(
                    center.x + cos(angle).toFloat() * startRadius,
                    center.y + sin(angle).toFloat() * startRadius,
                )
                val end = Offset(
                    center.x + cos(angle).toFloat() * endRadius,
                    center.y + sin(angle).toFloat() * endRadius,
                )
                drawLine(
                    color = color,
                    start = start,
                    end = end,
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
