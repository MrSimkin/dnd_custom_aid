package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.util.prefs.Preferences

/*
 * Redesign A5 (owner observation P7, 2026-11-13): «quiero que el explorador se parezca al de
 * windows, o por lo menos que me permita guardar o setear carpetas». The App now opens the
 * Windows file explorer itself (with its «Acceso rápido», favourites and recent folders) and
 * starts in the last folder used for that campaign, so the campaign folder is one click away.
 */

private val folderMemory: Preferences = Preferences.userRoot().node("dnd-custom-aid/folders")

/** The last folder used for [purpose] in [campaignKey], if it still exists. */
internal fun rememberedFolder(purpose: String, campaignKey: String?): File? =
    listOfNotNull(campaignKey?.let { "$purpose.$it" }, purpose)
        .mapNotNull { folderMemory.get(it, null) }
        .map(::File)
        .firstOrNull { it.isDirectory }

internal fun rememberFolder(purpose: String, campaignKey: String?, folder: File) {
    runCatching {
        folderMemory.put(purpose, folder.absolutePath)
        campaignKey?.let { folderMemory.put("$purpose.$it", folder.absolutePath) }
        folderMemory.flush()
    }
}

/**
 * Opens the system's own file dialog (the Windows explorer on Windows). Returns null when the
 * user cancels. [extensions] filters the files shown, e.g. listOf("png", "jpg").
 */
internal fun chooseFileWithExplorer(
    title: String,
    purpose: String,
    campaignKey: String? = null,
    extensions: List<String> = emptyList(),
    save: Boolean = false,
    suggestedName: String? = null,
): File? {
    val dialog = FileDialog(null as Frame?, title, if (save) FileDialog.SAVE else FileDialog.LOAD)
    rememberedFolder(purpose, campaignKey)?.let { dialog.directory = it.absolutePath }
    if (extensions.isNotEmpty()) {
        // Windows reads the pattern from the file name box; other systems use the filter.
        if (!save) dialog.file = extensions.joinToString(";") { "*.$it" }
        dialog.setFilenameFilter { _, name -> extensions.any { name.endsWith(".$it", ignoreCase = true) } }
    }
    suggestedName?.let { dialog.file = it }
    dialog.isVisible = true
    val name = dialog.file ?: return null
    val directory = dialog.directory ?: return null
    val chosen = File(directory, name)
    chosen.parentFile?.let { rememberFolder(purpose, campaignKey, it) }
    return chosen
}
