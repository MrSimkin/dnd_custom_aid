package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.mrsimkin.dndcustomaid.shared.ScaffoldStatus

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "dnd_custom_aid",
    ) {
        MaterialTheme {
            Text(ScaffoldStatus.message)
        }
    }
}
