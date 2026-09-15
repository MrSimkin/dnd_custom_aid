package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * Shared Player checkbox visual.
 *
 * The Material checkbox is rendered compactly here, while interactive callers expose a >=48dp
 * touch envelope through the surrounding item/box. Raw Material Checkbox usage outside this file
 * is intentionally guarded by CI so Player editors keep one control language.
 */
@Composable
private fun CharacterCompactCheckboxIndicatorV4(
    checked: Boolean,
    enabled: Boolean,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            modifier = Modifier.size(24.dp),
        )
    }
}

/** Compact labelled checkbox with a touch-safe whole-row interaction target. */
@Composable
internal fun CharacterCompactCheckboxItemV4(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        )
    } else {
        Modifier
    }
    Row(
        modifier = modifier
            .then(interactionModifier)
            .heightIn(min = 48.dp)
            .padding(horizontal = appSpacingV4(2.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CharacterCompactCheckboxIndicatorV4(checked = checked, enabled = enabled)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

/** Compact icon-only checkbox for rows where the adjacent content already supplies the label. */
@Composable
internal fun CharacterCompactCheckboxV4(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .then(interactionModifier)
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        CharacterCompactCheckboxIndicatorV4(checked = checked, enabled = enabled)
    }
}

/** Keeps a semantically coupled pair together while still allowing the outer group to wrap. */
@Composable
internal fun CharacterCompactCheckboxPairV4(
    firstChecked: Boolean,
    firstOnCheckedChange: ((Boolean) -> Unit)?,
    firstLabel: String,
    secondChecked: Boolean,
    secondOnCheckedChange: ((Boolean) -> Unit)?,
    secondLabel: String,
    modifier: Modifier = Modifier,
    firstEnabled: Boolean = true,
    secondEnabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CharacterCompactCheckboxItemV4(
            checked = firstChecked,
            onCheckedChange = firstOnCheckedChange,
            label = firstLabel,
            enabled = firstEnabled,
        )
        CharacterCompactCheckboxItemV4(
            checked = secondChecked,
            onCheckedChange = secondOnCheckedChange,
            label = secondLabel,
            enabled = secondEnabled,
        )
    }
}

/**
 * Responsive checkbox packing used by editor families.
 * Items stay on one line whenever they fit and wrap only when the available width requires it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CharacterResponsiveCheckboxGroupV4(
    modifier: Modifier = Modifier,
    content: @Composable FlowRowScope.() -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
        content = content,
    )
}
