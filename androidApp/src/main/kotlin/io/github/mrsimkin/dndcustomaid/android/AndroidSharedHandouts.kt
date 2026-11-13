package io.github.mrsimkin.dndcustomaid.android

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedSharedHandout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

/**
 * «Handouts de la campaña» (redesign A7, owner observation G2): what the DM sent to the players,
 * on the player's own App. Only images the DM marked «Se puede mostrar» and then sent arrive here;
 * the DM's notes never do.
 */
@Composable
internal fun AndroidSharedHandoutsScreen(
    campaignName: String,
    campaignId: Uuid,
    hostedBootstrap: AndroidHostedCampaignBootstrapController,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var handouts by remember { mutableStateOf<List<HostedSharedHandout>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val images = remember { mutableStateMapOf<Uuid, ImageBitmap?>() }
    var open by remember { mutableStateOf<HostedSharedHandout?>(null) }

    fun load() {
        error = null
        scope.launch {
            runCatching { withContext(Dispatchers.IO) { hostedBootstrap.sharedHandouts(campaignId) } }
                .onSuccess { handouts = it }
                .onFailure { error = "No se pudieron traer los handouts: revisa tu conexión y que entraste con tu cuenta." }
        }
    }
    LaunchedEffect(campaignId) { load() }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Atrás") }
            Column(Modifier.weight(1f)) {
                Text("Handouts · $campaignName", style = MaterialTheme.typography.headlineSmall)
                Text("Lo que el DM te envió. Toca una imagen para verla completa.", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = ::load) { Text("⟳ Actualizar") }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        val list = handouts
        when {
            list == null && error == null -> Text("Cargando…")
            list != null && list.isEmpty() -> Text("El DM todavía no envió handouts a los jugadores en esta campaña.")
            list != null -> LazyVerticalGrid(columns = GridCells.Adaptive(200.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(list, key = { it.id.toString() }) { handout ->
                    LaunchedEffect(handout.id, handout.revision) {
                        if (!images.containsKey(handout.id)) {
                            images[handout.id] = runCatching {
                                withContext(Dispatchers.IO) {
                                    val bytes = hostedBootstrap.sharedHandoutImage(handout)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                                }
                            }.getOrNull()
                        }
                    }
                    Column(Modifier.clickable { open = handout }, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            Modifier.size(200.dp, 140.dp).background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center,
                        ) {
                            val image = images[handout.id]
                            if (image != null) {
                                Image(image, contentDescription = handout.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                Text("Imagen pendiente", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text(handout.name, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                    }
                }
            }
        }
    }

    open?.let { handout ->
        Dialog(onDismissRequest = { open = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black, contentColor = Color.White) {
                Box(Modifier.fillMaxSize().clickable { open = null }) {
                    images[handout.id]?.let {
                        Image(it, contentDescription = handout.name, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                    } ?: Text("La imagen todavía no llegó.", modifier = Modifier.align(Alignment.Center))
                    Button(onClick = { open = null }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) { Text("Cerrar") }
                    if (handout.caption.isNotBlank()) {
                        Text(
                            handout.caption,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color(0x99000000)).padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}
