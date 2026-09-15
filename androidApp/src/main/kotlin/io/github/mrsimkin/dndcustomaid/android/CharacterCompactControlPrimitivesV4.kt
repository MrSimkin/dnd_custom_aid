package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

/**
 * Shared compact Player field geometry for Phase 4A.
 *
 * The previous raw Material OutlinedTextField path left equivalent editor/form fields with
 * disproportionate internal vertical whitespace. The editable BasicTextField itself owns the
 * safe 48dp-or-larger single-line interaction envelope; the decoration box then controls label,
 * value and padding geometry explicitly. Multiline fields grow from their content/line contract.
 */
@Composable
internal fun CharacterCompactOutlinedTextFieldV4(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    val resolvedMinLines = if (singleLine) 1 else minLines.coerceAtLeast(1)
    val resolvedMaxLines = if (singleLine) 1 else maxLines.coerceAtLeast(resolvedMinLines)
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }
    val secondaryColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        isError -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderColor = when {
        !enabled -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        isError -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (singleLine) {
                        Modifier.heightIn(min = characterCompactSingleLineFieldHeightV4())
                    } else {
                        Modifier
                    },
                ),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = contentColor),
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            minLines = resolvedMinLines,
            maxLines = resolvedMaxLines,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraSmall,
                    border = BorderStroke(1.dp, borderColor),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = if (singleLine) 3.dp else 5.dp,
                            ),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        label?.let { labelContent ->
                            CompositionLocalProvider(LocalContentColor provides secondaryColor) {
                                ProvideTextStyle(MaterialTheme.typography.labelSmall) {
                                    labelContent()
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                        ) {
                            prefix?.let { prefixContent ->
                                CompositionLocalProvider(LocalContentColor provides contentColor) {
                                    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                                        prefixContent()
                                    }
                                }
                                Box(modifier = Modifier.padding(horizontal = 2.dp))
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                if (value.isEmpty()) {
                                    placeholder?.let { placeholderContent ->
                                        CompositionLocalProvider(LocalContentColor provides secondaryColor) {
                                            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                                                placeholderContent()
                                            }
                                        }
                                    }
                                }
                                innerTextField()
                            }
                        }
                    }
                }
            },
        )
        supportingText?.let { supportingContent ->
            Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 2.dp)) {
                CompositionLocalProvider(LocalContentColor provides secondaryColor) {
                    ProvideTextStyle(MaterialTheme.typography.labelSmall) {
                        supportingContent()
                    }
                }
            }
        }
    }
}

/**
 * Compact visible glyph selector with a safe interaction envelope.
 *
 * The +/- face follows the existing 30-34dp compact step/action grammar while the clickable
 * envelope remains at least 48dp high, so reducing empty visual padding does not create a tiny
 * touch target.
 */
@Composable
internal fun CharacterCompactGlyphSelectorV4(
    glyph: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }
    val borderColor = if (enabled) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
    }
    Box(
        modifier = modifier
            .heightIn(min = characterCompactSingleLineFieldHeightV4())
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().heightIn(min = 34.dp),
            shape = MaterialTheme.shapes.extraSmall,
            border = BorderStroke(1.dp, borderColor),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    glyph,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    maxLines = 1,
                )
            }
        }
    }
}
