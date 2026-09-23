package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportPlanner
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal data class DesktopPcSheetGeneratedPdf(
    val plan: PcSheetPdfRenderPlan,
    val bytes: ByteArray,
)

/**
 * Desktop delivery seam for the already owner-approved production renderer.
 *
 * This class deliberately owns no PDF layout. Both Save and Share receive the exact byte stream
 * produced by DesktopPcSheetWholeDraftRenderer from PcSheetPdfRenderPlan.
 */
internal class DesktopPcSheetExportService(
    private val portraitBytesLoader: (String) -> ByteArray? = ::loadLocalPortraitBytes,
) {
    fun generate(
        request: PcSheetPdfExportRequest,
        sources: PcSheetExportSources,
    ): DesktopPcSheetGeneratedPdf {
        val localPortraitBytes = mutableMapOf<String, ByteArray>()
        val candidatePortraitRefs = buildList {
            sources.permanent.closure.portraitRef?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            sources.currentSnapshot?.closure?.portraitRef?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        }.distinct()

        candidatePortraitRefs.forEach { ref ->
            runCatching { portraitBytesLoader(ref) }.getOrNull()?.let { bytes ->
                localPortraitBytes[ref] = bytes
            }
        }

        val effectiveSources = sources.copy(
            locallyAvailablePortraitRefs =
                sources.locallyAvailablePortraitRefs + localPortraitBytes.keys,
        )
        val plan = PcSheetPdfExportPlanner.plan(request, effectiveSources)
        val renderer = DesktopPcSheetWholeDraftRenderer(
            portraitBytesLoader = { ref ->
                localPortraitBytes[ref] ?: runCatching { portraitBytesLoader(ref) }.getOrNull()
            },
        )
        val bytes = ByteArrayOutputStream().use { buffer ->
            renderer.renderDraft(plan, buffer)
            buffer.toByteArray()
        }
        return DesktopPcSheetGeneratedPdf(plan = plan, bytes = bytes)
    }

    fun save(
        generated: DesktopPcSheetGeneratedPdf,
        requestedTarget: File,
    ): File {
        val target = ensurePdfExtension(requestedTarget)
        val absolute = target.toPath().toAbsolutePath()
        val parent = requireNotNull(absolute.parent) { "El destino PDF no tiene directorio padre." }
        Files.createDirectories(parent)

        val temporary = Files.createTempFile(parent, ".dnd-custom-aid-", ".pdf.tmp")
        try {
            Files.write(temporary, generated.bytes)
            try {
                Files.move(
                    temporary,
                    absolute,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE,
                )
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporary, absolute, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temporary)
        }
        return absolute.toFile()
    }

    fun createShareFile(
        generated: DesktopPcSheetGeneratedPdf,
        characterName: String,
        directory: File = defaultShareDirectory(),
    ): File {
        Files.createDirectories(directory.toPath())
        val target = File(directory, suggestedFileName(characterName, generated.plan.request.visualFamily))
        val saved = save(generated, target)
        saved.deleteOnExit()
        return saved
    }

    fun copyFileToClipboard(file: File) {
        require(file.isFile) { "El PDF para compartir no existe: ${file.absolutePath}" }
        Toolkit.getDefaultToolkit().systemClipboard.setContents(
            FileListTransferable(listOf(file)),
            null,
        )
    }

    fun suggestedFileName(
        characterName: String,
        family: PcSheetVisualFamily,
    ): String {
        val safeName = characterName
            .trim()
            .replace(Regex("""[\\/:*?"<>|]+"""), "_")
            .replace(Regex("""\s+"""), " ")
            .ifBlank { "Personaje" }
        return "$safeName - Hoja de PJ - ${familyFileLabel(family)}.pdf"
    }

    private fun ensurePdfExtension(file: File): File =
        if (file.extension.equals("pdf", ignoreCase = true)) {
            file
        } else {
            File(file.parentFile, file.name + ".pdf")
        }

    private fun defaultShareDirectory(): File =
        File(System.getProperty("java.io.tmpdir"), "dnd-custom-aid/share")

    private fun familyFileLabel(family: PcSheetVisualFamily): String = when (family) {
        PcSheetVisualFamily.CLASSIC_DND_STYLE -> "Fantasy Sheet"
        PcSheetVisualFamily.CUSTOM_V1 -> "Custom v1"
        PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE -> "Custom v2 - Atributo"
        PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY -> "Custom v2 - Habilidad"
    }

    private class FileListTransferable(
        private val files: List<File>,
    ) : Transferable {
        override fun getTransferDataFlavors(): Array<DataFlavor> =
            arrayOf(DataFlavor.javaFileListFlavor)

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean =
            flavor == DataFlavor.javaFileListFlavor

        override fun getTransferData(flavor: DataFlavor): Any {
            require(isDataFlavorSupported(flavor)) { "Formato de portapapeles no soportado: $flavor" }
            return files
        }
    }

    companion object {
        private fun loadLocalPortraitBytes(ref: String): ByteArray? = runCatching {
            val file = when {
                ref.startsWith("file:", ignoreCase = true) -> File(URI(ref))
                else -> File(ref)
            }
            file.takeIf { it.isFile }?.readBytes()
        }.getOrNull()
    }
}
