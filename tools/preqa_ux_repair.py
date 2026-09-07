from __future__ import annotations

from pathlib import Path
import re
import urllib.request

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, content: str) -> None:
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(content, encoding="utf-8")


def replace_once(path: str, old: str, new: str) -> None:
    text = read(path)
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{path}: expected exactly one literal match, found {count}: {old[:100]!r}")
    write(path, text.replace(old, new, 1))


def replace_all(path: str, old: str, new: str, minimum: int = 1) -> int:
    text = read(path)
    count = text.count(old)
    if count < minimum:
        raise RuntimeError(f"{path}: expected at least {minimum} matches, found {count}: {old[:100]!r}")
    write(path, text.replace(old, new))
    return count


def replace_regex(path: str, pattern: str, replacement: str, count: int = 1) -> None:
    text = read(path)
    updated, matches = re.subn(pattern, replacement, text, count=count, flags=re.S)
    if matches != count:
        raise RuntimeError(f"{path}: expected {count} regex matches, found {matches}: {pattern[:100]!r}")
    write(path, updated)


def download(url: str, path: str) -> None:
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    print(f"Downloading {url} -> {path}")
    with urllib.request.urlopen(url, timeout=60) as response:
        target.write_bytes(response.read())


# ---------------------------------------------------------------------------
# 1) Shared collection/drag/haptic primitives: visibly lifted drag object,
#    distinct Android haptics, and dense search/order/filter controls.
# ---------------------------------------------------------------------------
write(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCollectionPrimitivesV4.kt",
    r'''package io.github.mrsimkin.dndcustomaid.android

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder

internal data class CharacterFilterOptionV4(
    val key: String,
    val label: String,
    val count: Int? = null,
)

internal data class CharacterDragVisualStateV4(
    val active: Boolean = false,
    val offsetY: Float = 0f,
    val showDropBefore: Boolean = false,
    val showDropAfter: Boolean = false,
)

internal enum class CharacterHapticEventV4 {
    DRAG_PICKUP,
    DRAG_STEP,
    DRAG_DROP,
    RESOURCE,
    DESTRUCTIVE,
}

@Composable
internal fun rememberCharacterHapticHookV4(
    enabled: Boolean,
): (CharacterHapticEventV4) -> Unit {
    val view = LocalView.current
    return remember(enabled, view) {
        { event ->
            if (enabled) {
                val feedback = when (event) {
                    CharacterHapticEventV4.DRAG_PICKUP -> HapticFeedbackConstants.GESTURE_START
                    CharacterHapticEventV4.DRAG_STEP -> HapticFeedbackConstants.CLOCK_TICK
                    CharacterHapticEventV4.DRAG_DROP -> HapticFeedbackConstants.GESTURE_END
                    CharacterHapticEventV4.RESOURCE -> HapticFeedbackConstants.CONFIRM
                    CharacterHapticEventV4.DESTRUCTIVE -> HapticFeedbackConstants.LONG_PRESS
                }
                view.performHapticFeedback(feedback)
            }
        }
    }
}

@Composable
internal fun Modifier.characterDragFeedbackV4(
    state: CharacterDragVisualStateV4,
): Modifier {
    val scale = animateFloatAsState(
        targetValue = if (state.active) 1.045f else 1f,
        label = "character-drag-scale",
    ).value
    val elevation = animateFloatAsState(
        targetValue = if (state.active) 24f else 0f,
        label = "character-drag-elevation",
    ).value
    val alpha = animateFloatAsState(
        targetValue = if (state.active) 0.96f else 1f,
        label = "character-drag-alpha",
    ).value

    return this
        .zIndex(if (state.active) 20f else 0f)
        .graphicsLayer {
            translationY = state.offsetY
            scaleX = scale
            scaleY = scale
            shadowElevation = elevation.dp.toPx()
            this.alpha = alpha
        }
}

@Composable
internal fun CharacterDropIndicatorV4(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "character-drop-indicator",
    ).value
    val indicatorColor = MaterialTheme.colorScheme.primary
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 3.dp)
            .drawBehind {
                if (alpha > 0f) {
                    drawRoundRect(
                        color = indicatorColor.copy(alpha = alpha),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f),
                    )
                }
            },
    )
}

@Composable
private fun CharacterToolbarChipV4(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .heightIn(min = 30.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CharacterCompactSearchV4(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = 34.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isBlank()) {
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
internal fun CharacterCollectionToolbarV4(
    itemCount: Int,
    query: CharacterCollectionQuery,
    onQueryChange: (CharacterCollectionQuery) -> Unit,
    order: CharacterPresentationOrder? = null,
    onOrderChange: ((CharacterPresentationOrder) -> Unit)? = null,
    filters: List<CharacterFilterOptionV4> = emptyList(),
    searchLabel: String = "Buscar",
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CharacterCompactSearchV4(
                    value = query.searchText,
                    onValueChange = { onQueryChange(query.copy(searchText = it)) },
                    label = searchLabel,
                    modifier = Modifier.weight(1f),
                )
                Text(itemCount.toString(), style = MaterialTheme.typography.labelMedium)
            }

            if (order != null && onOrderChange != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CharacterToolbarChipV4(
                        text = "Manual",
                        selected = order == CharacterPresentationOrder.MANUAL,
                        onClick = { onOrderChange(CharacterPresentationOrder.MANUAL) },
                    )
                    CharacterToolbarChipV4(
                        text = "A–Z",
                        selected = order == CharacterPresentationOrder.ALPHABETICAL,
                        onClick = { onOrderChange(CharacterPresentationOrder.ALPHABETICAL) },
                    )
                }
            }

            if (filters.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    filters.forEach { filter ->
                        val active = filter.key in query.activeFilterKeys
                        val text = filter.count?.let { "${filter.label} ($it)" } ?: filter.label
                        CharacterToolbarChipV4(
                            text = text,
                            selected = active,
                            onClick = { onQueryChange(query.toggleFilter(filter.key)) },
                        )
                    }
                }
            }
        }
    }
}
''',
)

# ---------------------------------------------------------------------------
# 2) d20: keep the convenience action, remove the Material text-button footprint.
# ---------------------------------------------------------------------------
write(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterD20RollUiV4.kt",
    r'''package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.characterD20Roll
import kotlin.random.Random

@Composable
internal fun CharacterD20RollButtonV4(
    label: String,
    modifier: Int?,
    compactLabel: String = "d20",
) {
    if (modifier == null) return
    var dieResult by remember(label, modifier) { mutableStateOf<Int?>(null) }

    Surface(
        modifier = Modifier
            .size(34.dp)
            .semantics { contentDescription = "Tirar d20: $label" }
            .clickable { dieResult = Random.nextInt(1, 21) },
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(compactLabel, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }

    dieResult?.let { result ->
        val roll = characterD20Roll(result, modifier)
        AlertDialog(
            onDismissRequest = { dieResult = null },
            title = { Text("Tirada: $label") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("d20 ${roll.dieResult}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (roll.modifier >= 0) "+ ${roll.modifier}" else "− ${-roll.modifier}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text("= ${roll.total}", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        "Tirada simple de conveniencia. La app no interpreta ventaja/desventaja, críticos, daño, legalidad ni efectos de reglas.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { dieResult = Random.nextInt(1, 21) }) { Text("Tirar otra vez") }
            },
            dismissButton = {
                TextButton(onClick = { dieResult = null }) { Text("Cerrar") }
            },
        )
    }
}
''',
)

# ---------------------------------------------------------------------------
# 3) Compact field geometry.
# ---------------------------------------------------------------------------
write(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterLayoutV4.kt",
    r'''package io.github.mrsimkin.dndcustomaid.android

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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}
''',
)

# ---------------------------------------------------------------------------
# 4) Dialog/empty-state density. Actions remain textual where meaning matters.
# ---------------------------------------------------------------------------
write(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterInteractionPrimitivesV4.kt",
    r'''package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
internal fun CharacterImeSafeEditorDialog(
    title: String,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    saveLabel: String = "Guardar",
    cancelLabel: String = "Cancelar",
    saveEnabled: Boolean = true,
    supportingText: String? = null,
    content: @Composable () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = { focusManager.clearFocus() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            )
            Surface(
                modifier = modifier.fillMaxWidth().widthIn(max = 640.dp).heightIn(max = maxHeight),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 5.dp,
                shadowElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    supportingText?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier.weight(1f, fill = false).fillMaxWidth().verticalScroll(rememberScrollState()),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) { content() }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = onCancel) { Text(cancelLabel) }
                        Button(onClick = onSave, enabled = saveEnabled) { Text(saveLabel) }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CharacterInlineValidationMessage(
    message: String?,
    modifier: Modifier = Modifier,
) {
    message?.takeIf { it.isNotBlank() }?.let {
        Text(
            text = it,
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
internal fun CharacterConfirmationDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String,
    destructive: Boolean = false,
    cancelLabel: String = "Cancelar",
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp).navigationBarsPadding(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 5.dp,
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(message, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                ) {
                    TextButton(onClick = onDismissRequest) { Text(cancelLabel) }
                    Button(onClick = onConfirm) { Text(confirmLabel) }
                }
            }
        }
    }
}

@Composable
internal fun CharacterNamedDeleteConfirmationDialog(
    itemName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    itemTypeLabel: String = "elemento",
) {
    CharacterConfirmationDialog(
        title = "Eliminar $itemTypeLabel",
        message = "Se eliminará “$itemName”. Esta acción no se puede deshacer.",
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm,
        confirmLabel = "Eliminar",
        destructive = true,
    )
}

@Composable
internal fun CharacterUsefulEmptyState(
    title: String,
    message: String,
    onAdd: (() -> Unit)? = null,
    addLabel: String = "Añadir",
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (onAdd != null) TextButton(onClick = onAdd) { Text(addLabel) }
        }
    }
}
''',
)

# ---------------------------------------------------------------------------
# 5) Small icon controls, plus icon-only Duplicate beside Delete.
# ---------------------------------------------------------------------------
icon_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/IconControls.kt"
replace_all(icon_path, "IconButton(onClick = onClick) {", "IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {", minimum=5)
replace_once(icon_path, ".size(48.dp)\n            .semantics", ".size(36.dp)\n            .semantics")
insert_duplicate = r'''
@Composable
internal fun StableDuplicateIconButton(
    onClick: () -> Unit,
    contentDescription: String = "Duplicar",
) {
    val color = MaterialTheme.colorScheme.onSurface
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Canvas(
            modifier = Modifier
                .size(20.dp)
                .semantics { this.contentDescription = contentDescription },
        ) {
            val stroke = 1.8.dp.toPx()
            drawRect(
                color = color,
                topLeft = Offset(size.width * 0.18f, size.height * 0.18f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.52f, size.height * 0.52f),
                style = Stroke(width = stroke),
            )
            drawRect(
                color = color,
                topLeft = Offset(size.width * 0.32f, size.height * 0.32f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.52f, size.height * 0.52f),
                style = Stroke(width = stroke),
            )
        }
    }
}

'''
replace_once(icon_path, "@Composable\ninternal fun StableRemoveIconButton(", insert_duplicate + "@Composable\ninternal fun StableRemoveIconButton(")

# ---------------------------------------------------------------------------
# 6) Responsive column preferences: phone/tablet + portrait/landscape.
# ---------------------------------------------------------------------------
write(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterResponsivePreferencesV4.kt",
    r'''package io.github.mrsimkin.dndcustomaid.android

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
''',
)

# UiPreferences: mixed-origin font audition + four column settings.
ui_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt"
replace_once(ui_path, "import androidx.compose.runtime.CompositionLocalProvider\n", "import androidx.compose.runtime.CompositionLocalProvider\nimport androidx.compose.runtime.staticCompositionLocalOf\n")
replace_once(ui_path, "import androidx.compose.ui.text.font.FontFamily\n", "import androidx.compose.ui.text.font.Font\nimport androidx.compose.ui.text.font.FontFamily\n")
replace_regex(
    ui_path,
    r'internal enum class AppFontChoice\(val label: String, val googleFontName: String\) \{.*?\n\}',
    r'''internal enum class AppFontChoice(
    val label: String,
    val googleFontName: String? = null,
    val sourceLabel: String,
) {
    MANROPE("Manrope", "Manrope", "Google Fonts"),
    SORA("Sora", "Sora", "Google Fonts"),
    SOURCE_SANS_3("Source Sans 3", "Source Sans 3", "Adobe / Google Fonts"),
    ROBOTO_CONDENSED("Roboto Condensed", "Roboto Condensed", "Google"),
    ARCHIVO_NARROW("Archivo Narrow", "Archivo Narrow", "Omnibus-Type / Google Fonts"),
    IBM_PLEX_SANS_CONDENSED("IBM Plex Sans Condensed", "IBM Plex Sans Condensed", "IBM"),
    MONA_SANS_CONDENSED("Mona Sans Condensed", sourceLabel = "GitHub / Degarism"),
    GEIST("Geist", sourceLabel = "Vercel"),
}''',
)
replace_regex(
    ui_path,
    r'internal data class UiPreferences\(.*?\n\)',
    r'''internal data class UiPreferences(
    val fontScalePercent: Int = 100,
    val fontChoice: AppFontChoice = AppFontChoice.MANROPE,
    val themeChoice: AppThemeChoice = AppThemeChoice.SYSTEM,
    val skillLayoutChoice: SkillLayoutChoice = SkillLayoutChoice.BY_SKILLS,
    val phonePortraitColumns: Int = 1,
    val phoneLandscapeColumns: Int = 2,
    val tabletPortraitColumns: Int = 2,
    val tabletLandscapeColumns: Int = 3,
)

internal val LocalUiPreferencesV4 = staticCompositionLocalOf { UiPreferences() }''',
)
replace_once(
    ui_path,
    """        val skillLayout = preferences.getString(KEY_SKILL_LAYOUT, null)\n            ?.let { runCatching { SkillLayoutChoice.valueOf(it) }.getOrNull() }\n            ?: SkillLayoutChoice.BY_SKILLS\n\n        return UiPreferences(\n            fontScalePercent = scale,\n            fontChoice = font,\n            themeChoice = theme,\n            skillLayoutChoice = skillLayout,\n        )\n""",
    """        val skillLayout = preferences.getString(KEY_SKILL_LAYOUT, null)\n            ?.let { runCatching { SkillLayoutChoice.valueOf(it) }.getOrNull() }\n            ?: SkillLayoutChoice.BY_SKILLS\n        val phonePortraitColumns = preferences.getInt(KEY_PHONE_PORTRAIT_COLUMNS, 1).coerceIn(1, 3)\n        val phoneLandscapeColumns = preferences.getInt(KEY_PHONE_LANDSCAPE_COLUMNS, 2).coerceIn(1, 4)\n        val tabletPortraitColumns = preferences.getInt(KEY_TABLET_PORTRAIT_COLUMNS, 2).coerceIn(1, 4)\n        val tabletLandscapeColumns = preferences.getInt(KEY_TABLET_LANDSCAPE_COLUMNS, 3).coerceIn(1, 4)\n\n        return UiPreferences(\n            fontScalePercent = scale,\n            fontChoice = font,\n            themeChoice = theme,\n            skillLayoutChoice = skillLayout,\n            phonePortraitColumns = phonePortraitColumns,\n            phoneLandscapeColumns = phoneLandscapeColumns,\n            tabletPortraitColumns = tabletPortraitColumns,\n            tabletLandscapeColumns = tabletLandscapeColumns,\n        )\n""",
)
replace_once(
    ui_path,
    """            .putString(KEY_THEME, value.themeChoice.name)\n            .putString(KEY_SKILL_LAYOUT, value.skillLayoutChoice.name)\n            .apply()\n""",
    """            .putString(KEY_THEME, value.themeChoice.name)\n            .putString(KEY_SKILL_LAYOUT, value.skillLayoutChoice.name)\n            .putInt(KEY_PHONE_PORTRAIT_COLUMNS, value.phonePortraitColumns)\n            .putInt(KEY_PHONE_LANDSCAPE_COLUMNS, value.phoneLandscapeColumns)\n            .putInt(KEY_TABLET_PORTRAIT_COLUMNS, value.tabletPortraitColumns)\n            .putInt(KEY_TABLET_LANDSCAPE_COLUMNS, value.tabletLandscapeColumns)\n            .apply()\n""",
)
replace_once(
    ui_path,
    """        const val KEY_THEME = \"theme\"\n        const val KEY_SKILL_LAYOUT = \"skill_layout\"\n""",
    """        const val KEY_THEME = \"theme\"\n        const val KEY_SKILL_LAYOUT = \"skill_layout\"\n        const val KEY_PHONE_PORTRAIT_COLUMNS = \"phone_portrait_columns\"\n        const val KEY_PHONE_LANDSCAPE_COLUMNS = \"phone_landscape_columns\"\n        const val KEY_TABLET_PORTRAIT_COLUMNS = \"tablet_portrait_columns\"\n        const val KEY_TABLET_LANDSCAPE_COLUMNS = \"tablet_landscape_columns\"\n""",
)
replace_regex(
    ui_path,
    r'private val fontFamilies: Map<AppFontChoice, FontFamily> by lazy \{.*?private fun AppFontChoice\.family\(\): FontFamily = requireNotNull\(fontFamilies\[this\]\)',
    r'''private val downloadableFontFamilies: Map<AppFontChoice, FontFamily> by lazy {
    AppFontChoice.entries.mapNotNull { choice ->
        choice.googleFontName?.let { choice to downloadableFontFamily(it) }
    }.toMap()
}

private val monaSansCondensedFamily by lazy {
    FontFamily(
        Font(R.font.mona_sans_condensed_vf, FontWeight.Normal),
        Font(R.font.mona_sans_condensed_vf, FontWeight.Medium),
        Font(R.font.mona_sans_condensed_vf, FontWeight.SemiBold),
        Font(R.font.mona_sans_condensed_vf, FontWeight.Bold),
    )
}

private val geistFamily by lazy {
    FontFamily(
        Font(R.font.geist_vf, FontWeight.Normal),
        Font(R.font.geist_vf, FontWeight.Medium),
        Font(R.font.geist_vf, FontWeight.SemiBold),
        Font(R.font.geist_vf, FontWeight.Bold),
    )
}

private fun AppFontChoice.family(): FontFamily = when (this) {
    AppFontChoice.MONA_SANS_CONDENSED -> monaSansCondensedFamily
    AppFontChoice.GEIST -> geistFamily
    else -> requireNotNull(downloadableFontFamilies[this])
}''',
)
replace_once(
    ui_path,
    """    CompositionLocalProvider(LocalDensity provides adjustedDensity) {\n        MaterialTheme(\n""",
    """    CompositionLocalProvider(\n        LocalDensity provides adjustedDensity,\n        LocalUiPreferencesV4 provides preferences,\n    ) {\n        MaterialTheme(\n""",
)
replace_once(
    ui_path,
    """                item {\n                    FontChoicePicker(\n                        selected = preferences.fontChoice,\n                        onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },\n                    )\n                }\n                item {\n                    ThemeChoicePicker(\n""",
    """                item {\n                    FontChoicePicker(\n                        selected = preferences.fontChoice,\n                        onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },\n                    )\n                }\n                item {\n                    LayoutColumnSettingsV4(\n                        preferences = preferences,\n                        onPreferencesChange = onPreferencesChange,\n                    )\n                }\n                item {\n                    ThemeChoicePicker(\n""",
)
layout_settings = r'''
@Composable
private fun LayoutColumnSettingsV4(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text("Columnas de tarjetas · audición", style = MaterialTheme.typography.labelLarge)
        Text(
            "Define el máximo deseado por formato/orientación. Cada pantalla conserva límites razonables cuando una tarjeta necesita más ancho.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SettingSelector(
            label = "Teléfono · vertical",
            value = preferences.phonePortraitColumns.toString(),
            options = (1..3).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(phonePortraitColumns = it)) },
        )
        SettingSelector(
            label = "Teléfono · horizontal",
            value = preferences.phoneLandscapeColumns.toString(),
            options = (1..4).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(phoneLandscapeColumns = it)) },
        )
        SettingSelector(
            label = "Tablet · vertical",
            value = preferences.tabletPortraitColumns.toString(),
            options = (1..4).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(tabletPortraitColumns = it)) },
        )
        SettingSelector(
            label = "Tablet · horizontal",
            value = preferences.tabletLandscapeColumns.toString(),
            options = (1..4).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(tabletLandscapeColumns = it)) },
        )
    }
}

'''
replace_once(ui_path, "@Composable\nprivate fun SettingsSheetPreview", layout_settings + "@Composable\nprivate fun SettingsSheetPreview")
replace_once(
    ui_path,
    '"${choice.label} · Aa Bb 123",',
    '"${choice.label} · Aa Bb 123 · ${choice.sourceLabel}",',
)

# ---------------------------------------------------------------------------
# 7) General sheet: core sheet first; portrait/token/defenses/senses/movement last.
#    Header and compact primitives give the sheet back its vertical core.
# ---------------------------------------------------------------------------
editor_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"
old_overview = '''        item {\n            IdentityCardV4(draft, stored, onDraftChange)\n        }\n        item {\n            CharacterGeneralClosureCardsV4(\n                state = closureState,\n                onStateChange = onClosureStateChange,\n                wide = wide,\n            )\n        }\n        item {\n            CharacterClassIdentityCardV4(\n                classes = draft.classes,\n                onClassesChange = { onDraftChange(draft.copy(classes = it)) },\n            )\n        }\n        item {\n            AbilitiesCardV4(draft, onDraftChange)\n        }\n        item {\n            CombatCardV4(draft, wide, onDraftChange)\n        }\n        if (stored.spellcasterEnabled) {\n            item {\n                QuickMagicCardV4(draft, onDraftChange)\n            }\n        }\n'''
new_overview = '''        item {\n            IdentityCardV4(draft, stored, onDraftChange)\n        }\n        item {\n            CharacterClassIdentityCardV4(\n                classes = draft.classes,\n                onClassesChange = { onDraftChange(draft.copy(classes = it)) },\n            )\n        }\n        item {\n            AbilitiesCardV4(draft, onDraftChange)\n        }\n        item {\n            CombatCardV4(draft, wide, onDraftChange)\n        }\n        if (stored.spellcasterEnabled) {\n            item {\n                QuickMagicCardV4(draft, onDraftChange)\n            }\n        }\n        item {\n            CharacterGeneralClosureCardsV4(\n                state = closureState,\n                onStateChange = onClosureStateChange,\n                wide = wide,\n            )\n        }\n'''
replace_once(editor_path, old_overview, new_overview)
replace_regex(
    editor_path,
    r'@Composable\nprivate fun EditorHeaderV4\(.*?\n\}\n\n@Composable\nprivate fun OverviewTabV4',
    r'''@Composable
private fun EditorHeaderV4(
    characterName: String,
    stored: CharacterSheet,
    savedMessage: String?,
    hasUnsavedChanges: Boolean,
    savable: Boolean,
    tableModeEnabled: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StableBackIconButton(onClick = onBack, contentDescription = "Volver a personajes")
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    characterName.ifBlank { "Ficha de personaje" },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                )
                when {
                    hasUnsavedChanges -> Text("Cambios sin guardar", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    tableModeEnabled -> Text("Modo Mesa", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    savedMessage != null -> Text(savedMessage, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }
            StableSettingsIconButton(onClick = onOpenSettings)
            TextButton(
                onClick = onSave,
                enabled = savable && !tableModeEnabled,
                contentPadding = PaddingValues(horizontal = 7.dp, vertical = 2.dp),
            ) { Text("Guardar", style = MaterialTheme.typography.labelMedium) }
        }
    }
}

@Composable
private fun OverviewTabV4''',
)
replace_once(
    editor_path,
    '''        OutlinedTextField(\n            value = draft.name,\n            onValueChange = { onDraftChange(draft.copy(name = it)) },\n            label = { Text("Nombre") },\n            modifier = Modifier.fillMaxWidth(),\n            singleLine = true,\n        )\n''',
    '''        Text("Nombre", style = MaterialTheme.typography.labelSmall)\n        CompactTextFieldV4(\n            value = draft.name,\n            onValueChange = { onDraftChange(draft.copy(name = it)) },\n            modifier = Modifier.fillMaxWidth(),\n        )\n''',
)
replace_once(editor_path, ".padding(horizontal = 5.dp, vertical = 4.dp),\n            verticalArrangement = Arrangement.spacedBy(4.dp),", ".padding(horizontal = 4.dp, vertical = 3.dp),\n            verticalArrangement = Arrangement.spacedBy(3.dp),")
replace_all(editor_path, ".heightIn(min = 38.dp)", ".heightIn(min = 34.dp)", minimum=4)
replace_all(editor_path, "padding(horizontal = 4.dp, vertical = 6.dp)", "padding(horizontal = 3.dp, vertical = 4.dp)", minimum=3)
replace_once(editor_path, "padding(horizontal = 5.dp, vertical = 6.dp)", "padding(horizontal = 4.dp, vertical = 4.dp)")

# Compact portrait/token implementation and quieter reference groups.
general_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterGeneralClosureV4.kt"
replace_regex(
    general_path,
    r'@Composable\nprivate fun CharacterMediaCardV4\(.*?\n@Composable\nprivate fun CharacterDefensesSensesMovementCardV4',
    r'''@Composable
private fun CharacterMediaCardV4(
    state: CharacterClosureState,
    onStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
) {
    val context = LocalContext.current
    fun persistReadPermission(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    val portraitLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            persistReadPermission(it)
            onStateChange(state.copy(portraitRef = it.toString()))
        }
    }
    val tokenLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            persistReadPermission(it)
            onStateChange(state.copy(tokenRef = it.toString()))
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("Retrato y token", style = MaterialTheme.typography.titleSmall)
            if (wide) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CharacterImageReferenceV4(
                        title = "Retrato",
                        uriRef = state.portraitRef,
                        onChoose = { portraitLauncher.launch(arrayOf("image/*")) },
                        onClear = { onStateChange(state.copy(portraitRef = null)) },
                        round = false,
                        imageSize = 56.dp,
                        modifier = Modifier.weight(1f),
                    )
                    CharacterImageReferenceV4(
                        title = "Token",
                        uriRef = state.tokenRef,
                        onChoose = { tokenLauncher.launch(arrayOf("image/*")) },
                        onClear = { onStateChange(state.copy(tokenRef = null)) },
                        round = true,
                        imageSize = 56.dp,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                CharacterImageReferenceV4(
                    title = "Retrato",
                    uriRef = state.portraitRef,
                    onChoose = { portraitLauncher.launch(arrayOf("image/*")) },
                    onClear = { onStateChange(state.copy(portraitRef = null)) },
                    round = false,
                    imageSize = 44.dp,
                )
                CharacterImageReferenceV4(
                    title = "Token",
                    uriRef = state.tokenRef,
                    onChoose = { tokenLauncher.launch(arrayOf("image/*")) },
                    onClear = { onStateChange(state.copy(tokenRef = null)) },
                    round = true,
                    imageSize = 44.dp,
                )
            }
        }
    }
}

@Composable
private fun CharacterImageReferenceV4(
    title: String,
    uriRef: String?,
    onChoose: () -> Unit,
    onClear: () -> Unit,
    round: Boolean,
    imageSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap = remember(uriRef) {
        uriRef?.let { raw ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(raw))?.use { input ->
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(imageSize).clip(if (round) CircleShape else MaterialTheme.shapes.small),
                shape = if (round) CircleShape else MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = title,
                        modifier = Modifier.size(imageSize),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text("—", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Text(title, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            TextButton(
                onClick = onChoose,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 5.dp, vertical = 1.dp),
            ) { Text(if (uriRef == null) "Elegir" else "Cambiar", style = MaterialTheme.typography.labelSmall) }
            if (uriRef != null) {
                StableRemoveIconButton(onClick = onClear, contentDescription = "Quitar $title")
            }
        }
    }
}

@Composable
private fun CharacterDefensesSensesMovementCardV4''',
)
replace_once(
    general_path,
    '''            Text(\n                "Referencia estructurada para la ficha y para futuras vistas rápidas del DM. No aplica reglas automáticamente.",\n                style = MaterialTheme.typography.labelSmall,\n            )\n''',
    "",
)
replace_once(general_path, "Column(modifier = Modifier.fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(3.dp))", "Column(modifier = Modifier.fillMaxWidth().padding(4.dp), verticalArrangement = Arrangement.spacedBy(2.dp))")
replace_once(
    general_path,
    '''                    TextButton(onClick = { onDelete(entry) }) { Text("Eliminar") }\n''',
    '''                    StableRemoveIconButton(onClick = { onDelete(entry) }, contentDescription = "Eliminar ${label(entry)}")\n''',
)

# ---------------------------------------------------------------------------
# 8) Tablet/wide behavior: earlier rail, thinner rail, narrower side editors.
# ---------------------------------------------------------------------------
replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNavigationStateV4.kt",
    "internal const val CHARACTER_NAVIGATION_RAIL_MIN_WIDTH_DP = 900f",
    "internal const val CHARACTER_NAVIGATION_RAIL_MIN_WIDTH_DP = 760f",
)
replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterAdaptiveShellV4.kt",
    "modifier = Modifier.fillMaxHeight().width(164.dp),",
    "modifier = Modifier.fillMaxHeight().width(112.dp),",
)

# ---------------------------------------------------------------------------
# 9) High-frequency card density + configurable columns + icon Duplicate.
# ---------------------------------------------------------------------------
equipment_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt"
replace_once(
    equipment_path,
    '''                    val columns = when {\n                        !wide -> 1\n                        special -> 2\n                        else -> 3\n                    }\n''',
    '''                    val columns = constrainedCardColumnsV4(\n                        wide = wide,\n                        phoneMax = if (special) 2 else 3,\n                        wideMax = if (special) 3 else 4,\n                    )\n''',
)
replace_once(equipment_path, "val reorderStepPx = with(LocalDensity.current) { 40.dp.toPx() }", "val reorderStepPx = with(LocalDensity.current) { (if (special) 72.dp else 62.dp).toPx() }")
replace_once(equipment_path, ".widthIn(min = 320.dp, max = 440.dp)", ".widthIn(min = 280.dp, max = 360.dp)")
replace_once(
    equipment_path,
    '''                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                        ) { Text("Duplicar") }\n                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${item.name}")\n''',
    '''                        StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${item.name}")\n                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${item.name}")\n''',
)
replace_once(
    equipment_path,
    '''                        Row(\n                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),\n                            horizontalArrangement = Arrangement.spacedBy(4.dp),\n                        ) {\n                            stateLabels.forEach { label ->\n                                CharacterSemanticBadgeV4(\n                                    label = label,\n                                    kind = CharacterSemanticBadgeKindV4.STATE,\n                                )\n                            }\n                        }\n''',
    '''                        if (special) {\n                            Row(\n                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),\n                                horizontalArrangement = Arrangement.spacedBy(3.dp),\n                            ) {\n                                stateLabels.forEach { label ->\n                                    CharacterSemanticBadgeV4(\n                                        label = label,\n                                        kind = CharacterSemanticBadgeKindV4.STATE,\n                                    )\n                                }\n                            }\n                        } else {\n                            Text(\n                                stateLabels.joinToString(" · "),\n                                style = MaterialTheme.typography.labelSmall,\n                                maxLines = 1,\n                                overflow = TextOverflow.Ellipsis,\n                            )\n                        }\n''',
)
replace_all(equipment_path, "padding(horizontal = 6.dp, vertical = 5.dp)", "padding(horizontal = 5.dp, vertical = 4.dp)", minimum=1)

combat_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt"
replace_once(combat_path, "val columns = if (wide) 2 else 1", "val columns = constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)")
replace_once(combat_path, "val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }", "val reorderStepPx = with(LocalDensity.current) { 66.dp.toPx() }")
replace_once(
    combat_path,
    '''    val glance = listOfNotNull(\n        combatEntryTypeLabelV4(entry.type),\n        entry.attackModifier?.let { "Ataque ${formatSignedCombatV4(it)}" },\n        entry.damageEffect.trim().takeIf { it.isNotEmpty() },\n    ).joinToString(" · ")\n''',
    '''    val glance = listOfNotNull(\n        combatEntryTypeLabelV4(entry.type),\n        entry.attackModifier?.let { "Ataque ${formatSignedCombatV4(it)}" },\n        entry.damageEffect.trim().takeIf { it.isNotEmpty() },\n        entry.rangeText?.takeIf { it.isNotBlank() }?.let { "Alcance $it" },\n    ).joinToString(" · ")\n''',
)
replace_once(
    combat_path,
    '''                if (entry.damageEffect.isNotBlank()) {\n                    Text(entry.damageEffect, style = MaterialTheme.typography.bodySmall)\n                }\n                entry.rangeText?.takeIf { it.isNotBlank() }?.let {\n                    Text("Alcance: $it", style = MaterialTheme.typography.labelSmall)\n                }\n''',
    "",
)
replace_once(
    combat_path,
    '''                    TextButton(\n                        onClick = { onFavoriteChange(!favorite) },\n                        enabled = favoriteEnabled,\n                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                    ) {\n                        Text(if (favorite) "★" else "☆")\n                    }\n                }\n''',
    '''                    TextButton(\n                        onClick = { onFavoriteChange(!favorite) },\n                        enabled = favoriteEnabled,\n                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),\n                    ) {\n                        Text(if (favorite) "★" else "☆")\n                    }\n                    if (structuralEditingEnabled) {\n                        StableRemoveIconButton(\n                            onClick = onDelete,\n                            contentDescription = "Eliminar ${entry.name}",\n                        )\n                    }\n                }\n''',
)
replace_once(
    combat_path,
    '''                Row(\n                    modifier = Modifier.fillMaxWidth(),\n                    horizontalArrangement = Arrangement.End,\n                ) {\n                    if (structuralEditingEnabled) {\n                        StableRemoveIconButton(\n                            onClick = onDelete,\n                            contentDescription = "Eliminar ${entry.name}",\n                        )\n                    }\n                }\n''',
    "",
)

traits_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt"
replace_once(traits_path, "val columns = if (wide) 2 else 1", "val columns = constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)")
replace_once(traits_path, "val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }", "val reorderStepPx = with(LocalDensity.current) { 68.dp.toPx() }")
replace_once(
    traits_path,
    '''                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                        ) { Text("Duplicar") }\n                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${trait.name}")\n''',
    '''                        StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${trait.name}")\n                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${trait.name}")\n''',
)

notes_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt"
replace_once(
    notes_path,
    '''                    } else if (wide) {\n                        draft.cards.chunked(2).forEach { rowCards ->\n                            Row(\n                                modifier = Modifier.fillMaxWidth(),\n                                horizontalArrangement = Arrangement.spacedBy(7.dp),\n                                verticalAlignment = Alignment.Top,\n                            ) {\n                                rowCards.forEach { note ->\n                                    val index = draft.cards.indexOfFirst { it.id == note.id }\n                                    CharacterNoteCardV4(\n                                        note = note,\n                                        onEdit = { beginEdit(note) },\n                                        onDuplicate = { duplicate(note) },\n                                        onDelete = { deleteId = note.id.toString() },\n                                        onMove = { offset -> move(index, offset) },\n                                        structuralEditingEnabled = structuralEditingEnabled,\n                                        onHaptic = haptic,\n                                        modifier = Modifier.weight(1f),\n                                    )\n                                }\n                                repeat(2 - rowCards.size) {\n                                    Spacer(modifier = Modifier.weight(1f))\n                                }\n                            }\n                        }\n                    } else {\n                        draft.cards.forEachIndexed { index, note ->\n                            CharacterNoteCardV4(\n                                note = note,\n                                onEdit = { beginEdit(note) },\n                                onDuplicate = { duplicate(note) },\n                                onDelete = { deleteId = note.id.toString() },\n                                onMove = { offset -> move(index, offset) },\n                                structuralEditingEnabled = structuralEditingEnabled,\n                                onHaptic = haptic,\n                            )\n                        }\n                    }\n''',
    '''                    } else {\n                        val columns = constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)\n                        draft.cards.chunked(columns).forEach { rowCards ->\n                            Row(\n                                modifier = Modifier.fillMaxWidth(),\n                                horizontalArrangement = Arrangement.spacedBy(6.dp),\n                                verticalAlignment = Alignment.Top,\n                            ) {\n                                rowCards.forEach { note ->\n                                    val index = draft.cards.indexOfFirst { it.id == note.id }\n                                    CharacterNoteCardV4(\n                                        note = note,\n                                        onEdit = { beginEdit(note) },\n                                        onDuplicate = { duplicate(note) },\n                                        onDelete = { deleteId = note.id.toString() },\n                                        onMove = { offset -> move(index, offset) },\n                                        structuralEditingEnabled = structuralEditingEnabled,\n                                        onHaptic = haptic,\n                                        modifier = Modifier.weight(1f),\n                                    )\n                                }\n                                repeat(columns - rowCards.size) { Spacer(modifier = Modifier.weight(1f)) }\n                            }\n                        }\n                    }\n''',
)
replace_once(notes_path, "val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }", "val reorderStepPx = with(LocalDensity.current) { 68.dp.toPx() }")
replace_once(
    notes_path,
    '''                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                        ) {\n                            Text("Duplicar")\n                        }\n''',
    '''                        StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${note.title}")\n''',
)

spells_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt"
replace_once(spells_path, ".width(390.dp)", ".width(340.dp)")
replace_once(spells_path, "val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }", "val reorderStepPx = with(LocalDensity.current) { 66.dp.toPx() }")
replace_once(
    spells_path,
    '''                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                        ) {\n                            Text("Duplicar")\n                        }\n''',
    '''                        StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${spell.name}")\n''',
)

# ---------------------------------------------------------------------------
# 10) Fonts are bundled for deterministic/offline audition. Both are OFL.
# ---------------------------------------------------------------------------
download(
    "https://raw.githubusercontent.com/github/mona-sans/main/fonts/variable/MonaSansVF-Condensed%5Bopsz%2Cwght%2Cital%5D.ttf",
    "androidApp/src/main/res/font/mona_sans_condensed_vf.ttf",
)
download(
    "https://raw.githubusercontent.com/vercel/geist-font/main/fonts/Geist/variable/Geist%5Bwght%5D.ttf",
    "androidApp/src/main/res/font/geist_vf.ttf",
)
download(
    "https://raw.githubusercontent.com/github/mona-sans/main/OFL.txt",
    "androidApp/src/main/assets/licenses/MONA_SANS_OFL.txt",
)
download(
    "https://raw.githubusercontent.com/vercel/geist-font/main/OFL.txt",
    "androidApp/src/main/assets/licenses/GEIST_OFL.txt",
)
write(
    "androidApp/src/main/assets/licenses/FONT_SOURCES.txt",
    """Bundled font audition sources\n\nMona Sans Condensed\nSource: https://github.com/github/mona-sans\nLicense: SIL Open Font License 1.1 (see MONA_SANS_OFL.txt)\n\nGeist\nSource: https://github.com/vercel/geist-font\nLicense: SIL Open Font License 1.1 (see GEIST_OFL.txt)\n""",
)

print("Pre-QA UX repair transformations applied successfully.")
