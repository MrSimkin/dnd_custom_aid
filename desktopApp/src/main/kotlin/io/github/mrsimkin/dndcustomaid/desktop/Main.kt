package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "dnd_custom_aid",
    ) {
        MaterialTheme {
            Text("dnd_custom_aid desktop")
        }
    }
}
