package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.characterRulesFamilyBadgeLabel
import io.github.mrsimkin.dndcustomaid.shared.character.characterSourceBadgeLabel

internal enum class CharacterSemanticBadgeKindV4 {
    RULES,
    SOURCE,
    STATE,
    NEUTRAL,
}

@Composable
internal fun CharacterSemanticBadgeV4(
    label: String,
    kind: CharacterSemanticBadgeKindV4,
    modifier: Modifier = Modifier,
) {
    val containerColor = when (kind) {
        CharacterSemanticBadgeKindV4.RULES -> MaterialTheme.colorScheme.primaryContainer
        CharacterSemanticBadgeKindV4.SOURCE -> MaterialTheme.colorScheme.secondaryContainer
        CharacterSemanticBadgeKindV4.STATE -> MaterialTheme.colorScheme.tertiaryContainer
        CharacterSemanticBadgeKindV4.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (kind) {
        CharacterSemanticBadgeKindV4.RULES -> MaterialTheme.colorScheme.onPrimaryContainer
        CharacterSemanticBadgeKindV4.SOURCE -> MaterialTheme.colorScheme.onSecondaryContainer
        CharacterSemanticBadgeKindV4.STATE -> MaterialTheme.colorScheme.onTertiaryContainer
        CharacterSemanticBadgeKindV4.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Composable
internal fun CharacterRulesSourceBadgesV4(
    rulesFamily: CharacterRulesFamily,
    source: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CharacterSemanticBadgeV4(
            label = characterRulesFamilyBadgeLabel(rulesFamily),
            kind = CharacterSemanticBadgeKindV4.RULES,
        )
        characterSourceBadgeLabel(source)?.let { sourceLabel ->
            CharacterSemanticBadgeV4(
                label = sourceLabel,
                kind = CharacterSemanticBadgeKindV4.SOURCE,
            )
        }
    }
}
