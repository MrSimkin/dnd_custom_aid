package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
    val haptic = LocalHapticFeedback.current
    return remember(enabled, haptic) {
        { _ ->
            if (enabled) {
                // A single conservative cross-version haptic primitive is used here. The semantic
                // event remains explicit so later Android tuning can vary intensity without changing
                // every caller.
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }
}

@Composable
internal fun Modifier.characterDragFeedbackV4(
    state: CharacterDragVisualStateV4,
): Modifier {
    val scale = animateFloatAsState(
        targetValue = if (state.active) 1.02f else 1f,
        label = "character-drag-scale",
    ).value
    val elevation = animateFloatAsState(
        targetValue = if (state.active) 12f else 0f,
        label = "character-drag-elevation",
    ).value

    return this
        .zIndex(if (state.active) 2f else 0f)
        .graphicsLayer {
            translationY = state.offsetY
            scaleX = scale
            scaleY = scale
            shadowElevation = elevation.dp.toPx()
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
            .height(4.dp)
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
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = query.searchText,
                    onValueChange = { onQueryChange(query.copy(searchText = it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text(searchLabel) },
                    singleLine = true,
                )
                Text("$itemCount", style = MaterialTheme.typography.labelLarge)
            }

            if (order != null && onOrderChange != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (order == CharacterPresentationOrder.MANUAL) {
                        Button(onClick = { onOrderChange(CharacterPresentationOrder.MANUAL) }) {
                            Text("Manual")
                        }
                    } else {
                        OutlinedButton(onClick = { onOrderChange(CharacterPresentationOrder.MANUAL) }) {
                            Text("Manual")
                        }
                    }
                    if (order == CharacterPresentationOrder.ALPHABETICAL) {
                        Button(onClick = { onOrderChange(CharacterPresentationOrder.ALPHABETICAL) }) {
                            Text("A–Z")
                        }
                    } else {
                        OutlinedButton(onClick = { onOrderChange(CharacterPresentationOrder.ALPHABETICAL) }) {
                            Text("A–Z")
                        }
                    }
                }
            }

            if (filters.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    filters.forEach { filter ->
                        val active = filter.key in query.activeFilterKeys
                        val text = filter.count?.let { "${filter.label} ($it)" } ?: filter.label
                        if (active) {
                            Button(onClick = { onQueryChange(query.toggleFilter(filter.key)) }) {
                                Text(text, maxLines = 1)
                            }
                        } else {
                            OutlinedButton(onClick = { onQueryChange(query.toggleFilter(filter.key)) }) {
                                Text(text, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
