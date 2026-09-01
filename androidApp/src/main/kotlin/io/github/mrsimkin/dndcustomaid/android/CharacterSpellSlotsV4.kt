package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * UI-only projection of the character editor's authoritative spell-slot state.
 *
 * This is deliberately not persisted and is not part of CharacterSpellcastingDraftV4.
 * Quick Magic and Conjuros both mutate the same CharacterEditorDraftV4 slot records.
 */
internal data class CharacterSpellSlotUiV4(
    val level: Int,
    val total: Int,
    val spent: Int,
)

@Composable
internal fun CompactSpellSlotHeaderV4(
    slot: CharacterSpellSlotUiV4,
    onSpentChange: (Int) -> Unit,
) {
    val total = slot.total.coerceAtLeast(0)
    val spent = slot.spent.coerceIn(0, total)
    if (total == 0) return

    Row(
        modifier = Modifier
            .widthIn(max = 180.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        (0 until total).forEach { index ->
            val isSpent = index < spent
            val borderColor = if (isSpent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            Surface(
                modifier = Modifier
                    .size(20.dp)
                    .semantics {
                        contentDescription = if (isSpent) {
                            "Espacio ${index + 1} de $total, gastado"
                        } else {
                            "Espacio ${index + 1} de $total, disponible"
                        }
                    }
                    .clickable {
                        val newSpent = if (isSpent) index else index + 1
                        onSpentChange(newSpent.coerceIn(0, total))
                    },
                shape = CircleShape,
                border = BorderStroke(1.25.dp, borderColor),
                color = if (isSpent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            ) {}
        }
        Text("$spent/$total", style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
