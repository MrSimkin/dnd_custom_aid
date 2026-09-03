package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground

private enum class BackgroundNarrativeFieldV4(val label: String) {
    PERSONALITY("Rasgos de personalidad"),
    IDEALS("Ideales"),
    BONDS("Vínculos"),
    FLAWS("Defectos"),
}

@Composable
internal fun CharacterBackgroundTabV4(
    background: CharacterBackground,
    onBackgroundChange: (CharacterBackground) -> Unit,
    wide: Boolean,
) {
    var editingFieldName by rememberSaveable { mutableStateOf<String?>(null) }
    var editorText by rememberSaveable { mutableStateOf("") }

    fun fieldValue(field: BackgroundNarrativeFieldV4): String = when (field) {
        BackgroundNarrativeFieldV4.PERSONALITY -> background.personalityTraits
        BackgroundNarrativeFieldV4.IDEALS -> background.ideals
        BackgroundNarrativeFieldV4.BONDS -> background.bonds
        BackgroundNarrativeFieldV4.FLAWS -> background.flaws
    }

    fun beginEdit(field: BackgroundNarrativeFieldV4) {
        editingFieldName = field.name
        editorText = fieldValue(field)
    }

    fun applyField(field: BackgroundNarrativeFieldV4, value: String) {
        onBackgroundChange(
            when (field) {
                BackgroundNarrativeFieldV4.PERSONALITY -> background.copy(personalityTraits = value)
                BackgroundNarrativeFieldV4.IDEALS -> background.copy(ideals = value)
                BackgroundNarrativeFieldV4.BONDS -> background.copy(bonds = value)
                BackgroundNarrativeFieldV4.FLAWS -> background.copy(flaws = value)
            },
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 5.dp,
            bottom = 170.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Text("Trasfondo", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = background.name,
                        onValueChange = { onBackgroundChange(background.copy(name = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre del trasfondo") },
                        singleLine = true,
                    )
                    if (wide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            OutlinedTextField(
                                value = background.race,
                                onValueChange = { onBackgroundChange(background.copy(race = it)) },
                                modifier = Modifier.weight(1f),
                                label = { Text("Raza") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = background.religionFaith,
                                onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },
                                modifier = Modifier.weight(1f),
                                label = { Text("Religión / Fe") },
                                singleLine = true,
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = background.race,
                            onValueChange = { onBackgroundChange(background.copy(race = it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Raza") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = background.religionFaith,
                            onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Religión / Fe") },
                            singleLine = true,
                        )
                    }
                    OutlinedTextField(
                        value = background.summary,
                        onValueChange = { onBackgroundChange(background.copy(summary = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción / resumen") },
                        minLines = 2,
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.Top,
            ) {
                CharacterImagePlaceholderV4(
                    title = "Imagen principal",
                    contentDescription = "Espacio reservado para imagen principal del personaje; función aún no disponible",
                    modifier = Modifier.weight(1f),
                )
                CharacterImagePlaceholderV4(
                    title = "Imagen secundaria",
                    contentDescription = "Espacio reservado para segunda imagen del personaje; función aún no disponible",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Perfil narrativo", style = MaterialTheme.typography.titleSmall)
                    val fields = BackgroundNarrativeFieldV4.entries
                    if (wide) {
                        fields.chunked(2).forEach { rowFields ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                rowFields.forEach { field ->
                                    BackgroundNarrativePreviewCardV4(
                                        title = field.label,
                                        value = fieldValue(field),
                                        onEdit = { beginEdit(field) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                repeat(2 - rowFields.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        fields.forEach { field ->
                            BackgroundNarrativePreviewCardV4(
                                title = field.label,
                                value = fieldValue(field),
                                onEdit = { beginEdit(field) },
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Historia del personaje", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Espacio amplio para la historia completa del personaje.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    OutlinedTextField(
                        value = background.story,
                        onValueChange = { onBackgroundChange(background.copy(story = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Historia") },
                        minLines = if (wide) 10 else 8,
                    )
                }
            }
        }
    }

    val editingField = editingFieldName?.let { name ->
        runCatching { BackgroundNarrativeFieldV4.valueOf(name) }.getOrNull()
    }
    if (editingField != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(editingField.label) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 500.dp)
                        .imePadding()
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    item {
                        OutlinedTextField(
                            value = editorText,
                            onValueChange = { editorText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(editingField.label) },
                            minLines = 7,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        applyField(editingField, editorText)
                        editingFieldName = null
                    },
                ) { Text("Aplicar") }
            },
            dismissButton = {
                TextButton(onClick = { editingFieldName = null }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun CharacterImagePlaceholderV4(
    title: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.aspectRatio(4f / 5f),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CharacterImagePlaceholderIconV4(contentDescription)
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(
                "Próximamente · sin almacenamiento de imágenes",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun CharacterImagePlaceholderIconV4(contentDescription: String) {
    val lineColor = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(
        modifier = Modifier
            .size(48.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        val stroke = size.minDimension * 0.055f
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.15f, size.height * 0.15f),
            end = Offset(size.width * 0.85f, size.height * 0.15f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.85f, size.height * 0.15f),
            end = Offset(size.width * 0.85f, size.height * 0.85f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.85f, size.height * 0.85f),
            end = Offset(size.width * 0.15f, size.height * 0.85f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.15f, size.height * 0.85f),
            end = Offset(size.width * 0.15f, size.height * 0.15f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = lineColor,
            radius = size.minDimension * 0.08f,
            center = Offset(size.width * 0.68f, size.height * 0.34f),
        )
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.24f, size.height * 0.70f),
            end = Offset(size.width * 0.44f, size.height * 0.48f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.44f, size.height * 0.48f),
            end = Offset(size.width * 0.60f, size.height * 0.65f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.60f, size.height * 0.65f),
            end = Offset(size.width * 0.72f, size.height * 0.54f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun BackgroundNarrativePreviewCardV4(
    title: String,
    value: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(
                value.ifBlank { "Sin contenido" },
                style = if (value.isBlank()) {
                    MaterialTheme.typography.labelSmall
                } else {
                    MaterialTheme.typography.bodySmall
                },
                maxLines = 2,
            )
            Text("Toca para editar", style = MaterialTheme.typography.labelSmall)
        }
    }
}
