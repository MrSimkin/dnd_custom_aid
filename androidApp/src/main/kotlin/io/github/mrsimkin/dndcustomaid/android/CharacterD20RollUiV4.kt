package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    OutlinedButton(
        onClick = { dieResult = Random.nextInt(1, 21) },
        modifier = Modifier.heightIn(min = 36.dp),
        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 0.dp),
    ) {
        Text(compactLabel, style = MaterialTheme.typography.labelMedium)
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
