package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
internal fun CharacterUnsavedChangesDialogV4(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onKeepEditing: () -> Unit,
    saveEnabled: Boolean,
) {
    Dialog(onDismissRequest = onKeepEditing) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .navigationBarsPadding(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("Cambios sin guardar", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Hay cambios en la ficha que todavía no se han guardado. Puedes guardarlos, descartarlos o continuar editando.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onKeepEditing) {
                        Text("Seguir editando")
                    }
                    TextButton(onClick = onDiscard) {
                        Text("Descartar")
                    }
                    Button(
                        onClick = onSave,
                        enabled = saveEnabled,
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
