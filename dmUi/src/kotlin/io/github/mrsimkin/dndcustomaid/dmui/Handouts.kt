package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContent
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutKind
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutVisibility
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * Base para DM, package 4 on the tablet: the DM's handouts, maps and NPC portraits (P3: shown full
 * screen on the DM tablet, not sent to phones in this QA).
 */

internal fun HandoutKind.spanishLabel(): String = when (this) {
    HandoutKind.HANDOUT -> "Handout"
    HandoutKind.MAP -> "Mapa"
    HandoutKind.NPC_PORTRAIT -> "Retrato de PNJ"
}

/** Decodes a stored image, sampled down so a thumbnail never holds a full-size bitmap. */
private suspend fun loadBitmap(platform: DmPlatform, blobs: MediaBlobStore, sha256: String, maxSide: Int): ImageBitmap? = withContext(Dispatchers.Default) {
    val bytes = blobs.read(sha256) ?: return@withContext null
    platform.decodeImage(bytes, maxSide)
}

/** Thumbnails of the campaign handouts; tapping one opens it full screen. */
@Composable
internal fun HandoutStrip(
    handouts: List<HandoutContent>,
    blobs: MediaBlobStore,
    /** Redesign A7: the DM sends a PLAYER_SAFE handout to the players' App, or stops sending it. */
    onShare: ((HandoutContent, Boolean) -> Unit)? = null,
) {
    var open by remember { mutableStateOf<HandoutContent?>(null) }
    if (handouts.isEmpty()) {
        Text("Todavía no hay handouts ni mapas en este aparato.", style = MaterialTheme.typography.bodyMedium)
    } else {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            handouts.forEach { handout ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    HandoutThumbnail(handout, blobs, onOpen = { open = handout })
                    if (onShare != null && handout.payload.visibility == HandoutVisibility.PLAYER_SAFE) {
                        val shared = handout.payload.sharedWithPlayers
                        TextButton(onClick = { onShare(handout, !shared) }, modifier = Modifier.handCursor()) {
                            Text(if (shared) "✓ Enviado · dejar de enviar" else "📤 Enviar a los jugadores")
                        }
                    }
                }
            }
        }
    }
    open?.let { HandoutFullScreen(it, blobs, onClose = { open = null }) }
}

@Composable
private fun HandoutThumbnail(handout: HandoutContent, blobs: MediaBlobStore, onOpen: () -> Unit) {
    var bitmap by remember(handout.payload.sha256) { mutableStateOf<ImageBitmap?>(null) }
    val platform = LocalDmPlatform.current
    LaunchedEffect(handout.payload.sha256) { bitmap = loadBitmap(platform, blobs, handout.payload.sha256, 320) }
    Column(
        modifier = Modifier.width(180.dp).clickable(onClick = onOpen),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(180.dp, 120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center,
        ) {
            val image = bitmap
            if (image != null) {
                Image(image, contentDescription = handout.item.displayName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Text("Imagen pendiente", style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(handout.item.displayName, style = MaterialTheme.typography.titleSmall, maxLines = 1)
        Text(
            text = handout.payload.kind.spanishLabel() + when {
                handout.payload.visibility == HandoutVisibility.DM_ONLY -> " · Solo DM"
                handout.payload.sharedWithPlayers -> " · Enviado a los jugadores"
                else -> ""
            },
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

/**
 * Full-screen view to show the players. DM-only images say so on top, so the DM never shows one
 * by mistake; DM notes are never on this screen.
 */
@Composable
private fun HandoutFullScreen(handout: HandoutContent, blobs: MediaBlobStore, onClose: () -> Unit) {
    var bitmap by remember(handout.payload.sha256) { mutableStateOf<ImageBitmap?>(null) }
    val platform = LocalDmPlatform.current
    LaunchedEffect(handout.payload.sha256) { bitmap = loadBitmap(platform, blobs, handout.payload.sha256, 2048) }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black, contentColor = Color.White) {
            Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClose)) {
                val image = bitmap
                if (image != null) {
                    Image(image, contentDescription = handout.item.displayName, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                } else {
                    Text("La imagen todavía no llegó a este aparato. Sincroniza y vuelve a abrirla.", modifier = Modifier.align(Alignment.Center))
                }
                if (handout.payload.visibility == HandoutVisibility.DM_ONLY) {
                    Text(
                        text = "Solo DM",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(Color(0xCC8B1E1E), MaterialTheme.shapes.small)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
                TextButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    Text("Cerrar", color = Color.White)
                }
                if (handout.payload.caption.isNotBlank()) {
                    Text(
                        text = handout.payload.caption,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color(0x99000000))
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}
