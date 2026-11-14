package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

/**
 * The compact field of the PC sheet (CharacterCompactOutlinedTextFieldV4), shared by the DM desks
 * on the tablet and on Windows: a 48dp single-line envelope, small label on top, no extra padding.
 */
@Composable
internal fun DmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    minLines: Int = 1,
) {
    val resolvedMinLines = if (singleLine) 1 else minLines.coerceAtLeast(1)
    val secondaryColor = MaterialTheme.colorScheme.onSurfaceVariant
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.then(if (singleLine) Modifier.heightIn(min = 48.dp) else Modifier),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        minLines = resolvedMinLines,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraSmall,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = if (singleLine) 3.dp else 5.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    label?.let { labelContent ->
                        CompositionLocalProvider(LocalContentColor provides secondaryColor) {
                            ProvideTextStyle(MaterialTheme.typography.labelSmall) { labelContent() }
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth()) { innerTextField() }
                }
            }
        },
    )
}
