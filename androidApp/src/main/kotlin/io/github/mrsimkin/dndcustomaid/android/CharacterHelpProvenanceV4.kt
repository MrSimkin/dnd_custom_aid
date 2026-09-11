package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

internal enum class CharacterHelpModeV4(val label: String) {
    ALWAYS_VISIBLE("Siempre visible"),
    INFO("Icono / tooltip"),
    HIDDEN("Oculto"),
}

internal val LocalCharacterHelpModeV4 = staticCompositionLocalOf { CharacterHelpModeV4.ALWAYS_VISIBLE }

@Composable
internal fun CharacterHelpModeProviderV4(
    mode: CharacterHelpModeV4,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalCharacterHelpModeV4 provides mode, content = content)
}

@Composable
internal fun CharacterHelpV4(
    text: String,
    modifier: Modifier = Modifier,
    mode: CharacterHelpModeV4 = LocalCharacterHelpModeV4.current,
) {
    when (mode) {
        // Approved P12 behavior: do not redesign the always-visible form.
        CharacterHelpModeV4.ALWAYS_VISIBLE -> Text(
            text = text,
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        CharacterHelpModeV4.INFO -> {
            var open by remember { mutableStateOf(false) }
            Box(modifier = modifier) {
                CharacterInfoIconButtonV4(
                    onClick = { open = !open },
                    contentDescription = if (open) "Ocultar ayuda" else "Mostrar ayuda",
                )
                DropdownMenu(
                    expanded = open,
                    onDismissRequest = { open = false },
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 180.dp, max = 320.dp)
                            .heightIn(max = 280.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                    ) {
                        Text(
                            text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }

        CharacterHelpModeV4.HIDDEN -> Unit
    }
}

/** Proper graphical info control; the visible glyph is drawn, not a text pseudo-icon. */
@Composable
private fun CharacterInfoIconButtonV4(
    onClick: () -> Unit,
    contentDescription: String,
) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Canvas(
            modifier = Modifier
                .size(18.dp)
                .semantics { this.contentDescription = contentDescription },
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val stroke = 1.8.dp.toPx()
            drawCircle(
                color = color,
                radius = size.minDimension * 0.43f,
                center = center,
                style = Stroke(width = stroke),
            )
            drawCircle(
                color = color,
                radius = 1.25.dp.toPx(),
                center = Offset(center.x, size.height * 0.31f),
            )
            drawLine(
                color = color,
                start = Offset(center.x, size.height * 0.45f),
                end = Offset(center.x, size.height * 0.70f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

internal enum class CharacterOriginTypeV4(val label: String) {
    CLASS("Clase"),
    FEAT("Dote"),
    PACT("Pacto"),
    ITEM("Objeto"),
    RACE("Raza"),
    BACKGROUND("Trasfondo"),
    GIFT("Don / bendición"),
    OTHER("Otro"),
}

internal data class CharacterOriginOptionV4(
    val key: String,
    val label: String,
)

private fun characterOriginAllowsFreeTextV4(type: CharacterOriginTypeV4): Boolean =
    type == CharacterOriginTypeV4.FEAT ||
        type == CharacterOriginTypeV4.GIFT ||
        type == CharacterOriginTypeV4.OTHER

@Composable
internal fun CharacterProvenanceRowV4(
    originType: CharacterOriginTypeV4 = CharacterOriginTypeV4.CLASS,
    originKey: String?,
    customOrigin: String,
    optionsForType: (CharacterOriginTypeV4) -> List<CharacterOriginOptionV4>,
    onOriginTypeChange: (CharacterOriginTypeV4) -> Unit,
    onOriginKeyChange: (String?) -> Unit,
    onCustomOriginChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    allowedTypes: List<CharacterOriginTypeV4> = CharacterOriginTypeV4.entries,
    allowCustomOriginOption: Boolean = false,
    customOriginSelected: Boolean = false,
    onCustomOriginSelectedChange: (Boolean) -> Unit = {},
) {
    var typeMenuOpen by remember { mutableStateOf(false) }
    var originMenuOpen by remember { mutableStateOf(false) }
    val options = optionsForType(originType)
    val selectedOrigin = originKey?.let { key -> options.firstOrNull { it.key == key } }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(0.42f)) {
            Text("Tipo de origen", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            Box {
                CompactMenuSurfaceV4(
                    text = originType.label,
                    onClick = { typeMenuOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                DropdownMenu(
                    expanded = typeMenuOpen,
                    onDismissRequest = { typeMenuOpen = false },
                ) {
                    allowedTypes.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                if (option != originType) {
                                    onOriginTypeChange(option)
                                    onOriginKeyChange(null)
                                    onCustomOriginSelectedChange(false)
                                }
                                typeMenuOpen = false
                            },
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.weight(0.58f)) {
            Text("Origen específico", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            val freeText = characterOriginAllowsFreeTextV4(originType) ||
                (allowCustomOriginOption && customOriginSelected)

            when {
                freeText -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 34.dp),
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(5.dp)),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (customOrigin.isBlank()) {
                                Text(
                                    when (originType) {
                                        CharacterOriginTypeV4.FEAT -> "Nombre de la dote"
                                        CharacterOriginTypeV4.GIFT -> "Don o bendición"
                                        else -> "Origen personalizado"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            BasicTextField(
                                value = customOrigin,
                                onValueChange = onCustomOriginChange,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            )
                        }
                    }
                }

                options.isEmpty() -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 34.dp),
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(5.dp)),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                "Sin origen canónico disponible",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                else -> {
                    Box {
                        CompactMenuSurfaceV4(
                            text = selectedOrigin?.label ?: "Seleccionar",
                            onClick = { originMenuOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        DropdownMenu(
                            expanded = originMenuOpen,
                            onDismissRequest = { originMenuOpen = false },
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        onCustomOriginSelectedChange(false)
                                        onOriginKeyChange(option.key)
                                        originMenuOpen = false
                                    },
                                )
                            }
                            if (allowCustomOriginOption) {
                                DropdownMenuItem(
                                    text = { Text("Personalizado…") },
                                    onClick = {
                                        onOriginKeyChange(null)
                                        onCustomOriginChange("")
                                        onCustomOriginSelectedChange(true)
                                        originMenuOpen = false
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
