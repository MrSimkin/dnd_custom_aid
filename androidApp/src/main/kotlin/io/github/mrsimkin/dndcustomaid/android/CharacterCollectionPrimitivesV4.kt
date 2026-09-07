package io.github.mrsimkin.dndcustomaid.android

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
