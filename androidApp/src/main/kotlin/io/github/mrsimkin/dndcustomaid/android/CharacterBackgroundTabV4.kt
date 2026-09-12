package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackgroundImage
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackgroundImageSlot
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

private const val BACKGROUND_IMAGE_MAX_EDGE_G4 = 1600
private const val BACKGROUND_IMAGE_JPEG_QUALITY_G4 = 86

private enum class BackgroundNarrativeFieldV4(val label: String) {
    PERSONALITY("Rasgos de personalidad"),
    IDEALS("Ideales"),
    BONDS("Vínculos"),
    FLAWS("Defectos"),
}

@Composable
internal fun CharacterBackgroundTabV4(
    background: CharacterBackground,
    canonicalOrigins: CharacterCanonicalOriginsDraftP7V4,
    onBackgroundChange: (CharacterBackground) -> Unit,
    onCanonicalOriginsChange: (CharacterCanonicalOriginsDraftP7V4) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
) {
    var editingFieldName by rememberSaveable { mutableStateOf<String?>(null) }
    var editorText by rememberSaveable { mutableStateOf("") }
    var storyExpanded by rememberSaveable("background-story-expanded") { mutableStateOf(false) }
    var imageErrorMessage by rememberSaveable("background-image-error") { mutableStateOf<String?>(null) }
    val androidContext = LocalContext.current
    val settingsContext = LocalCharacterPcSettingsContextV4.current
    val successorState = settingsContext?.successorState
    val primaryImage = successorState?.backgroundImages?.firstOrNull { it.slot == CharacterBackgroundImageSlot.PRIMARY }
    val secondaryImage = successorState?.backgroundImages?.firstOrNull { it.slot == CharacterBackgroundImageSlot.SECONDARY }
    val imageEditingEnabled = structuralEditingEnabled && settingsContext != null

    fun updateImage(slot: CharacterBackgroundImageSlot, image: CharacterBackgroundImage?) {
        val currentContext = settingsContext ?: return
        val current = currentContext.successorState
        val updatedImages = buildList {
            addAll(current.backgroundImages.filterNot { it.slot == slot })
            image?.let(::add)
        }.sortedBy { it.slot.ordinal }
        currentContext.onSuccessorStateChange(current.copy(backgroundImages = updatedImages))
    }

    fun importImage(slot: CharacterBackgroundImageSlot, uri: Uri) {
        val result = runCatching { characterBackgroundImageFromUriV4(androidContext, uri, slot) }
        result.onSuccess { image ->
            updateImage(slot, image)
            imageErrorMessage = null
        }.onFailure {
            imageErrorMessage = "No se pudo importar la imagen seleccionada. Prueba con otra imagen compatible."
        }
    }

    val primaryImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { importImage(CharacterBackgroundImageSlot.PRIMARY, it) }
    }
    val secondaryImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { importImage(CharacterBackgroundImageSlot.SECONDARY, it) }
    }

    fun fieldValue(field: BackgroundNarrativeFieldV4): String = when (field) {
        BackgroundNarrativeFieldV4.PERSONALITY -> background.personalityTraits
        BackgroundNarrativeFieldV4.IDEALS -> background.ideals
        BackgroundNarrativeFieldV4.BONDS -> background.bonds
        BackgroundNarrativeFieldV4.FLAWS -> background.flaws
    }

    fun beginEdit(field: BackgroundNarrativeFieldV4) {
        if (!structuralEditingEnabled) return
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
            start = appSpacingV4(if (wide) 10.dp else 5.dp),
            end = appSpacingV4(if (wide) 10.dp else 5.dp),
            top = appSpacingV4(5.dp),
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(5.dp)),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
                ) {
                    Text("Trasfondo", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = background.name,
                        onValueChange = { value ->
                            onBackgroundChange(background.copy(name = value))
                            onCanonicalOriginsChange(canonicalOrigins.withBackgroundName(value))
                        },
                        enabled = structuralEditingEnabled,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre del trasfondo") },
                        singleLine = true,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    ) {
                        OutlinedTextField(
                            value = background.race,
                            onValueChange = { value ->
                                onBackgroundChange(background.copy(race = value))
                                onCanonicalOriginsChange(canonicalOrigins.withSpeciesName(value))
                            },
                            enabled = structuralEditingEnabled,
                            modifier = Modifier.weight(1f),
                            label = { Text("Raza") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = canonicalOrigins.subraceIdentity?.name.orEmpty(),
                            onValueChange = { value ->
                                onCanonicalOriginsChange(canonicalOrigins.withSubraceName(value))
                            },
                            enabled = structuralEditingEnabled && canonicalOrigins.speciesIdentity?.name?.isNotBlank() == true,
                            modifier = Modifier.weight(1f),
                            label = { Text("Subraza") },
                            singleLine = true,
                            supportingText = if (canonicalOrigins.speciesIdentity?.name?.isBlank() != false) {
                                { Text("Configura primero la Raza") }
                            } else {
                                null
                            },
                        )
                    }
                    OutlinedTextField(
                        value = background.religionFaith,
                        onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },
                        enabled = structuralEditingEnabled,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Religión / Fe") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = background.summary,
                        onValueChange = { onBackgroundChange(background.copy(summary = it)) },
                        enabled = structuralEditingEnabled,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción / resumen") },
                        minLines = characterCompactTextAreaMinLinesV4(2),
                    )
                }
            }
        }

        item(key = "background-images") {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp))) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
                    verticalAlignment = Alignment.Top,
                ) {
                    CharacterBackgroundImageCardV4(
                        title = "Principal",
                        image = primaryImage,
                        editingEnabled = imageEditingEnabled,
                        onPick = { primaryImageLauncher.launch("image/*") },
                        onRemove = { updateImage(CharacterBackgroundImageSlot.PRIMARY, null) },
                        modifier = Modifier.weight(1f),
                    )
                    CharacterBackgroundImageCardV4(
                        title = "Secundaria",
                        image = secondaryImage,
                        editingEnabled = imageEditingEnabled,
                        onPick = { secondaryImageLauncher.launch("image/*") },
                        onRemove = { updateImage(CharacterBackgroundImageSlot.SECONDARY, null) },
                        modifier = Modifier.weight(1f),
                    )
                }
                imageErrorMessage?.let { message ->
                    Text(message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(5.dp)),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    Text("Perfil narrativo", style = MaterialTheme.typography.titleSmall)
                    val fields = BackgroundNarrativeFieldV4.entries
                    if (wide) {
                        fields.chunked(2).forEach { rowFields ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
                                verticalAlignment = Alignment.Top,
                            ) {
                                rowFields.forEach { field ->
                                    BackgroundNarrativePreviewCardV4(
                                        title = field.label,
                                        value = fieldValue(field),
                                        onEdit = { beginEdit(field) },
                                        editingEnabled = structuralEditingEnabled,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                repeat(2 - rowFields.size) { Spacer(modifier = Modifier.weight(1f)) }
                            }
                        }
                    } else {
                        fields.forEach { field ->
                            BackgroundNarrativePreviewCardV4(
                                title = field.label,
                                value = fieldValue(field),
                                onEdit = { beginEdit(field) },
                                editingEnabled = structuralEditingEnabled,
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
                        .padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(5.dp)),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Historia del personaje", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { storyExpanded = !storyExpanded }) {
                            Text(
                                if (storyExpanded) "Ocultar"
                                else if (background.story.isBlank() && structuralEditingEnabled) "Añadir"
                                else "Mostrar",
                            )
                        }
                    }
                    if (storyExpanded) {
                        OutlinedTextField(
                            value = background.story,
                            onValueChange = { onBackgroundChange(background.copy(story = it)) },
                            enabled = structuralEditingEnabled,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Historia") },
                            minLines = characterCompactTextAreaMinLinesV4(3),
                            maxLines = 10,
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable { storyExpanded = true },
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                background.story.ifBlank { "Sin historia registrada" },
                                modifier = Modifier.fillMaxWidth().padding(
                                    horizontal = appSpacingV4(7.dp),
                                    vertical = appSpacingV4(5.dp),
                                ),
                                style = if (background.story.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }

    val editingField = editingFieldName?.let { name ->
        runCatching { BackgroundNarrativeFieldV4.valueOf(name) }.getOrNull()
    }
    if (editingField != null && structuralEditingEnabled) {
        CharacterImeSafeEditorDialog(
            title = editingField.label,
            onCancel = { editingFieldName = null },
            onSave = {
                applyField(editingField, editorText)
                editingFieldName = null
            },
        ) {
            OutlinedTextField(
                value = editorText,
                onValueChange = { editorText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(editingField.label) },
                minLines = characterCompactTextAreaMinLinesV4(3),
                maxLines = 8,
            )
        }
    }
}

@Composable
private fun CharacterBackgroundImageCardV4(
    title: String,
    image: CharacterBackgroundImage?,
    editingEnabled: Boolean,
    onPick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var viewerOpen by rememberSaveable(image?.id?.toString(), "viewer") { mutableStateOf(false) }
    val bitmap = remember(image?.id, image?.encodedData) { image?.let(::decodeCharacterBackgroundImageV4) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clickable(
                    enabled = image != null || editingEnabled,
                    onClick = {
                        if (image != null) viewerOpen = true else if (editingEnabled) onPick()
                    },
                ),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(appSpacingV4(8.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(if (image == null) "Sin imagen" else "Imagen no disponible", style = MaterialTheme.typography.bodySmall)
                    if (editingEnabled && image == null) {
                        Text("Toca para seleccionar", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    if (viewerOpen && image != null && bitmap != null) {
        CharacterBackgroundImageViewerV4(
            title = title,
            bitmap = bitmap,
            editingEnabled = editingEnabled,
            onDismiss = { viewerOpen = false },
            onChange = {
                viewerOpen = false
                onPick()
            },
            onRemove = {
                viewerOpen = false
                onRemove()
            },
        )
    }
}

@Composable
private fun CharacterBackgroundImageViewerV4(
    title: String,
    bitmap: ImageBitmap,
    editingEnabled: Boolean,
    onDismiss: () -> Unit,
    onChange: () -> Unit,
    onRemove: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(appSpacingV4(8.dp)),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(appSpacingV4(8.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
                if (editingEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = onChange) { Text("Cambiar") }
                        TextButton(onClick = onRemove) { Text("Eliminar") }
                    }
                }
            }
        }
    }
}

private fun characterBackgroundImageFromUriV4(
    context: Context,
    uri: Uri,
    slot: CharacterBackgroundImageSlot,
): CharacterBackgroundImage {
    val decoded = decodeSampledBackgroundBitmapV4(context, uri)
    val longestEdge = max(decoded.width, decoded.height)
    val prepared = if (longestEdge > BACKGROUND_IMAGE_MAX_EDGE_G4) {
        val scale = BACKGROUND_IMAGE_MAX_EDGE_G4.toFloat() / longestEdge.toFloat()
        Bitmap.createScaledBitmap(
            decoded,
            (decoded.width * scale).roundToInt().coerceAtLeast(1),
            (decoded.height * scale).roundToInt().coerceAtLeast(1),
            true,
        )
    } else {
        decoded
    }
    val hasAlpha = prepared.hasAlpha()
    val format = if (hasAlpha) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
    val mimeType = if (hasAlpha) "image/png" else "image/jpeg"
    val encoded = ByteArrayOutputStream().use { output ->
        check(prepared.compress(format, if (hasAlpha) 100 else BACKGROUND_IMAGE_JPEG_QUALITY_G4, output)) {
            "Image compression failed."
        }
        Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }
    if (prepared !== decoded) decoded.recycle()
    prepared.recycle()
    return CharacterBackgroundImage(
        id = Uuid.random(),
        slot = slot,
        mimeType = mimeType,
        encodedData = encoded,
        originalName = characterBackgroundImageOriginalNameV4(context, uri),
    )
}

private fun decodeSampledBackgroundBitmapV4(context: Context, uri: Uri): Bitmap {
    val resolver = context.contentResolver
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri).use { input ->
        BitmapFactory.decodeStream(requireNotNull(input) { "Unable to open image." }, null, bounds)
    }
    require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Unsupported image." }

    var sampleSize = 1
    val decodeTarget = BACKGROUND_IMAGE_MAX_EDGE_G4 * 2
    while (max(bounds.outWidth, bounds.outHeight) / sampleSize > decodeTarget) {
        sampleSize *= 2
    }
    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return resolver.openInputStream(uri).use { input ->
        requireNotNull(
            BitmapFactory.decodeStream(requireNotNull(input) { "Unable to reopen image." }, null, options),
        ) { "Unable to decode image." }
    }
}

private fun decodeCharacterBackgroundImageV4(image: CharacterBackgroundImage): ImageBitmap? = runCatching {
    val bytes = Base64.decode(image.encodedData, Base64.DEFAULT)
    requireNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)).asImageBitmap()
}.getOrNull()

private fun characterBackgroundImageOriginalNameV4(context: Context, uri: Uri): String? = runCatching {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    }
}.getOrNull()?.trim()?.takeIf(String::isNotEmpty)

@Composable
private fun BackgroundNarrativePreviewCardV4(
    title: String,
    value: String,
    onEdit: () -> Unit,
    editingEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = editingEnabled, onClick = onEdit),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(5.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(
                value.ifBlank { "Sin contenido" },
                style = if (value.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                maxLines = 2,
            )
        }
    }
}
