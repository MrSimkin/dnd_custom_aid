from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected one match, found {count}")
    return text.replace(old, new, 1)


ui_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterBackgroundTabV4.kt')
ui = ui_path.read_text()
ui = replace_once(
    ui,
    'package io.github.mrsimkin.dndcustomaid.android\n\n',
    '''package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
''',
    'background Android imports',
)
ui = ui.replace('import androidx.compose.foundation.Canvas\n', '')
ui = replace_once(ui, 'import androidx.compose.foundation.BorderStroke\n', 'import androidx.compose.foundation.BorderStroke\nimport androidx.compose.foundation.Image\n', 'background Image import')
ui = ui.replace('import androidx.compose.foundation.layout.size\n', '')
ui = replace_once(ui, 'import androidx.compose.runtime.mutableStateOf\n', 'import androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember\n', 'background remember import')
ui = ui.replace('import androidx.compose.ui.geometry.Offset\n', '')
ui = ui.replace('import androidx.compose.ui.graphics.StrokeCap\n', '')
ui = ui.replace('import androidx.compose.ui.semantics.contentDescription\n', '')
ui = ui.replace('import androidx.compose.ui.semantics.semantics\n', '')
ui = replace_once(
    ui,
    'import androidx.compose.ui.Alignment\nimport androidx.compose.ui.Modifier\n',
    '''import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
''',
    'background Compose image imports',
)
ui = replace_once(
    ui,
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground\n',
    '''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackgroundImage
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackgroundImageSlot
''',
    'background successor imports',
)
ui = replace_once(
    ui,
    'private enum class BackgroundNarrativeFieldV4',
    '''import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

private const val BACKGROUND_IMAGE_MAX_EDGE_G4 = 1600
private const val BACKGROUND_IMAGE_JPEG_QUALITY_G4 = 86

private enum class BackgroundNarrativeFieldV4''',
    'background utility imports and constants',
)

state_marker = '    var storyExpanded by rememberSaveable("background-story-expanded") { mutableStateOf(false) }\n'
state_block = '''    var storyExpanded by rememberSaveable("background-story-expanded") { mutableStateOf(false) }
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
'''
ui = replace_once(ui, state_marker, state_block, 'background image state')

old_images = '''        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
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
'''
new_images = '''        item(key = "background-images") {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp))) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
                    verticalAlignment = Alignment.Top,
                ) {
                    CharacterBackgroundImageCardV4(
                        title = "Imagen principal",
                        image = primaryImage,
                        editingEnabled = imageEditingEnabled,
                        onPick = { primaryImageLauncher.launch("image/*") },
                        onRemove = { updateImage(CharacterBackgroundImageSlot.PRIMARY, null) },
                        modifier = Modifier.weight(1f),
                    )
                    CharacterBackgroundImageCardV4(
                        title = "Imagen secundaria",
                        image = secondaryImage,
                        editingEnabled = imageEditingEnabled,
                        onPick = { secondaryImageLauncher.launch("image/*") },
                        onRemove = { updateImage(CharacterBackgroundImageSlot.SECONDARY, null) },
                        modifier = Modifier.weight(1f),
                    )
                }
                CharacterHelpV4(
                    "La app copia, reduce y guarda cada imagen dentro del personaje; después no depende del archivo o enlace externo original.",
                )
                imageErrorMessage?.let { message ->
                    Text(message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
'''
ui = replace_once(ui, old_images, new_images, 'background image cards')

placeholder_start = ui.index('@Composable\nprivate fun CharacterImagePlaceholderV4(')
preview_start = ui.index('\n@Composable\nprivate fun BackgroundNarrativePreviewCardV4(', placeholder_start)
new_helpers = '''@Composable
private fun CharacterBackgroundImageCardV4(
    title: String,
    image: CharacterBackgroundImage?,
    editingEnabled: Boolean,
    onPick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bitmap = remember(image?.id, image?.encodedData) {
        image?.let(::decodeCharacterBackgroundImageV4)
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(appSpacingV4(6.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                Text(
                    title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (editingEnabled) {
                    TextButton(onClick = onPick) { Text(if (image == null) "Añadir" else "Cambiar") }
                    if (image != null) {
                        StableRemoveIconButton(onClick = onRemove, contentDescription = "Eliminar $title")
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .clickable(enabled = editingEnabled, onClick = onPick),
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surface,
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = image.originalName?.let { "$title: $it" } ?: title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(appSpacingV4(8.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            if (image == null) "Sin imagen" else "Imagen no disponible",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        if (editingEnabled) {
                            Text("Toca para seleccionar", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            image?.originalName?.takeIf(String::isNotBlank)?.let { originalName ->
                Text(
                    originalName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
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
'''
ui = ui[:placeholder_start] + new_helpers + ui[preview_start:]
ui_path.write_text(ui)

# Persistence regression: app-owned image payload must survive repository reopen.
repo_test_path = Path('shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSuccessorRepositoryTest.kt')
repo_test = repo_test_path.read_text()
repo_marker = '    @Test\n    fun successorSaveRejectsDanglingReferences()'
repo_test_block = '''    @Test
    fun backgroundImagesRoundTripAsAppOwnedPayloads() = withRepositories { campaigns, characters, successor ->
        val campaign = campaigns.createCampaign("Imágenes")
        val character = characters.createCharacter(campaign.id, "Retrato")
        val image = CharacterBackgroundImage(
            id = Uuid.random(),
            slot = CharacterBackgroundImageSlot.PRIMARY,
            mimeType = "image/png",
            encodedData = "cG5nLXBheWxvYWQ=",
            originalName = "retrato.png",
        )

        val saved = successor.saveState(
            character.id,
            successor.state(character.id).copy(backgroundImages = listOf(image)),
        )
        val reopened = successor.state(character.id)

        assertEquals(listOf(image), saved.backgroundImages)
        assertEquals(listOf(image), reopened.backgroundImages)
    }

'''
repo_test = replace_once(repo_test, repo_marker, repo_test_block + repo_marker, 'background persistence test')
repo_test_path.write_text(repo_test)

# Backup regression: the payload travels with the backup and gets a new local identity on import.
backup_test_path = Path('shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupV2Test.kt')
backup_test = backup_test_path.read_text()
backup_marker = '    private fun withDatabase(block: (AppDatabase) -> Unit)'
backup_test_block = '''    @Test
    fun backgroundImagePayloadSurvivesBackupCodecAndImportWithFreshIdentity() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen imagen")
            val destinationCampaign = campaigns.createCampaign("Destino imagen")
            val character = characters.createCharacter(sourceCampaign.id, "Retrato")
            val image = CharacterBackgroundImage(
                id = Uuid.random(),
                slot = CharacterBackgroundImageSlot.SECONDARY,
                mimeType = "image/png",
                encodedData = "cG9ydGFibGUtaW1hZ2U=",
                originalName = "secundaria.png",
            )
            successor.saveState(
                character.id,
                successor.state(character.id).copy(backgroundImages = listOf(image)),
            )

            val encodedBackup = CharacterBackupCodec.encode(backups.exportCharacter(character.id, 400))
            val decoded = assertIs<CharacterBackupDecodeResult.Success>(CharacterBackupCodec.decode(encodedBackup)).document
            val imported = backups.importAsCopy(decoded, destinationCampaign.id, 500)
            val importedImage = imported.successorState.backgroundImages.single()

            assertNotEquals(image.id, importedImage.id)
            assertEquals(image.slot, importedImage.slot)
            assertEquals(image.mimeType, importedImage.mimeType)
            assertEquals(image.encodedData, importedImage.encodedData)
            assertEquals(image.originalName, importedImage.originalName)
        }
    }

'''
backup_test = replace_once(backup_test, backup_marker, backup_test_block + backup_marker, 'background backup test')
backup_test_path.write_text(backup_test)
