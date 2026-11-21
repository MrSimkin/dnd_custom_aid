package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * F2 refactor (one style for the tablet and the App de PC): the heading of every screen, with its
 * one-line purpose, an optional «← Atrás» and the short «? ¿Cómo se usa?» help.
 */
@Composable
fun DmScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    help: String? = null,
    onBack: (() -> Unit)? = null,
) {
    var showHelp by remember(title) { mutableStateOf(false) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            onBack?.let { TextButton(onClick = it, modifier = Modifier.handCursor()) { Text("← Atrás") } }
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (help != null) {
                TextButton(onClick = { showHelp = !showHelp }, modifier = Modifier.handCursor()) { Text("? ¿Cómo se usa?") }
            }
        }
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (showHelp && help != null) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.small) {
                Text(help, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(10.dp))
            }
        }
    }
}
