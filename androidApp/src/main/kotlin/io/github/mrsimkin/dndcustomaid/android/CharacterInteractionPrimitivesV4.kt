package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { focusManager.clearFocus() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            )
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .fillMaxHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 5.dp,
                shadowElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = appSpacingV4(8.dp),
                            start = appSpacingV4(8.dp),
                            end = appSpacingV4(8.dp),
                            bottom = appSpacingV4(6.dp),
                        ),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                ) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    supportingText?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                    ) {
                        content()
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp), Alignment.End),
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

/**
 * Shared two-field row for naturally short editor controls. It is intentionally opt-in: callers
 * should use it only when both controls remain legible and useful side by side on a phone.
 */
@Composable
internal fun CharacterCompactFieldRowV4(
    first: @Composable (Modifier) -> Unit,
    second: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    firstWeight: Float = 1f,
    secondWeight: Float = 1f,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.Top,
    ) {
        first(Modifier.weight(firstWeight))
        second(Modifier.weight(secondWeight))
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
                modifier = Modifier.padding(appSpacingV4(8.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(message, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp), Alignment.End),
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
            modifier = Modifier.padding(appSpacingV4(8.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (onAdd != null) TextButton(onClick = onAdd) { Text(addLabel) }
        }
    }
}
