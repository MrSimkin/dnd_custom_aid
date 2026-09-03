package io.github.mrsimkin.dndcustomaid.android

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

/**
 * Reusable character editor dialog for Phase 4 closure.
 *
 * The dialog owns IME/navigation insets. Only the editable body scrolls; the action row remains
 * reachable above the keyboard. Back/outside dismissal clears keyboard focus instead of discarding
 * the editor draft. Only the explicit Cancel action leaves the editor without saving.
 */
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
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .heightIn(max = maxHeight),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(title, style = MaterialTheme.typography.headlineSmall)
                    supportingText?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            content()
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = onCancel) {
                            Text(cancelLabel)
                        }
                        Button(
                            onClick = onSave,
                            enabled = saveEnabled,
                        ) {
                            Text(saveLabel)
                        }
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
internal fun CharacterNamedDeleteConfirmationDialog(
    itemName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    itemTypeLabel: String = "elemento",
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .navigationBarsPadding(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Eliminar $itemTypeLabel", style = MaterialTheme.typography.headlineSmall)
                Text("Se eliminará “$itemName”. Esta acción no se puede deshacer.")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(onClick = onDismissRequest) { Text("Cancelar") }
                    Button(onClick = onConfirm) { Text("Eliminar") }
                }
            }
        }
    }
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
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (onAdd != null) {
                TextButton(onClick = onAdd) { Text(addLabel) }
            }
        }
    }
}
