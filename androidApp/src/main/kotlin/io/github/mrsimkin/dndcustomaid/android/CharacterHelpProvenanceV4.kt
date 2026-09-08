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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

internal enum class CharacterHelpModeV4(val label: String) {
    ALWAYS_VISIBLE("Siempre visible"),
    INFO("ⓘ / tooltip"),
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
        CharacterHelpModeV4.ALWAYS_VISIBLE -> Text(
            text = text,
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        CharacterHelpModeV4.INFO -> {
            var open by remember { mutableStateOf(false) }
            Box(modifier = modifier) {
                Surface(
                    modifier = Modifier
                        .heightIn(min = 34.dp)
                        .clickable { open = true },
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("ⓘ", style = MaterialTheme.typography.labelLarge)
                    }
                }
                DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        },
                        onClick = { open = false },
                    )
                }
            }
        }

        CharacterHelpModeV4.HIDDEN -> Unit
    }
}

internal enum class CharacterOriginTypeV4(val label: String) {
    CLASS("Clase"),
    FEAT("Dote"),
    PACT("Pacto"),
    ITEM("Objeto"),
    RACE("Raza"),
    BACKGROUND("Trasfondo"),
    OTHER("Otro"),
}

internal data class CharacterOriginOptionV4(
    val key: String,
    val label: String,
)

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
                    CharacterOriginTypeV4.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                if (option != originType) {
                                    onOriginTypeChange(option)
                                    onOriginKeyChange(null)
                                    if (option != CharacterOriginTypeV4.OTHER) onCustomOriginChange("")
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
            if (originType == CharacterOriginTypeV4.OTHER) {
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
                                "Origen personalizado",
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
            } else {
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
                        if (options.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Sin opciones disponibles") },
                                onClick = { originMenuOpen = false },
                            )
                        } else {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        onOriginKeyChange(option.key)
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
