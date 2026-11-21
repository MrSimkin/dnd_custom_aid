package io.github.mrsimkin.dndcustomaid.desktop

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import javax.swing.JOptionPane

/**
 * QA 0.7.0-preqa.2 (M4): two copies of the App de PC shared the remembered account and the
 * database, and one copy's renewal could make the other one fail to synchronize. The App now
 * opens only once per computer user; a second copy says so and closes.
 */
internal object DesktopSingleInstance {
    private var channel: FileChannel? = null
    private var lock: FileLock? = null

    /** True when this is the only copy running; the lock is held until the process ends. */
    fun acquire(dataDirectory: File): Boolean {
        dataDirectory.mkdirs()
        val opened = runCatching { RandomAccessFile(File(dataDirectory, "app.lock"), "rw").channel }.getOrNull() ?: return true
        val held = runCatching { opened.tryLock() }.getOrNull()
        if (held == null) {
            opened.close()
            return false
        }
        channel = opened
        lock = held
        return true
    }

    fun tellAlreadyOpen() {
        runCatching {
            JOptionPane.showMessageDialog(
                null,
                "D&D Custom Aid ya está abierta en este equipo.\nUsa la ventana que ya tienes abierta: dos copias a la vez se pisan la cuenta y los datos.",
                "D&D Custom Aid",
                JOptionPane.INFORMATION_MESSAGE,
            )
        }
    }
}
