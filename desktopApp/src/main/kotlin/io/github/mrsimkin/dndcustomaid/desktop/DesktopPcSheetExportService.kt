package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticData
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticMapper
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/** The V11 sheet's PDF bytes with the semantic data they were made from. */
internal data class DesktopPcSheetV11PdfProof(
    val semantic: PcSheetV11SemanticData,
    val bytes: ByteArray,
)

internal class DesktopPcSheetExportService(
    private val portraitBytesLoader: (String) -> ByteArray? = ::loadLocalPortraitBytes,
) {

    /** The V11 sheet (C473: the only sheet), generated locally from the shared V11 contract. */
    fun generateV11Proof(
        request: PcSheetV11ExportRequest,
        sources: PcSheetExportSources,
    ): DesktopPcSheetV11PdfProof {
        val selected = if (request.stateSelection ==
            io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection.CURRENT_SNAPSHOT
        ) sources.currentSnapshot ?: sources.permanent else sources.permanent
        val portraitRef = selected.closure.portraitRef?.trim()?.takeIf { it.isNotBlank() }
        val portraitBytes = portraitRef?.let { ref ->
            runCatching { portraitBytesLoader(ref) }.getOrNull()
        }
        val effectiveSources = if (portraitRef != null && portraitBytes != null) {
            sources.copy(locallyAvailablePortraitRefs =
                sources.locallyAvailablePortraitRefs + portraitRef)
        } else sources
        val semantic = PcSheetV11SemanticMapper.map(request, effectiveSources)
        val bytes = ByteArrayOutputStream().use { buffer ->
            DesktopPcSheetV11PdfProofRenderer().render(semantic, buffer, portraitBytes)
            buffer.toByteArray()
        }
        check(bytes.isNotEmpty())
        return DesktopPcSheetV11PdfProof(semantic, bytes)
    }

    /** Atomically save ONLY the clearly stamped V11 internal PDF proof. */
    fun saveV11Proof(
        generated: DesktopPcSheetV11PdfProof,
        requestedTarget: File,
    ): File {
        val target = ensurePdfExtension(requestedTarget)
        val absolute = target.toPath().toAbsolutePath()
        val parent = requireNotNull(absolute.parent)
        Files.createDirectories(parent)
        val temporary = Files.createTempFile(parent, ".dnd-v11-internal-", ".pdf.tmp")
        try {
            Files.write(temporary, generated.bytes)
            try {
                Files.move(temporary, absolute,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporary, absolute, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temporary)
        }
        return absolute.toFile()
    }

    fun createShareV11Proof(
        generated: DesktopPcSheetV11PdfProof,
        characterName: String,
        directory: File = defaultShareDirectory(),
    ): File {
        Files.createDirectories(directory.toPath())
        val target = File(directory,
            suggestedV11ProofFileName(characterName, generated.semantic.request.variant))
        return saveV11Proof(generated, target).also { it.deleteOnExit() }
    }

    fun suggestedV11ProofFileName(
        characterName: String,
        variant: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant,
    ): String {
        val name = characterName.trim()
            .replace(Regex("""[\\/:*?"<>|]+"""), "_")
            .replace(Regex("""\s+"""), " ")
            .ifBlank { "Personaje" }
        return "$name - Hoja de PJ V11 $variant.pdf"
    }

    fun copyFileToClipboard(file: File) {
        require(file.isFile) { "El PDF para compartir no existe: ${file.absolutePath}" }
        Toolkit.getDefaultToolkit().systemClipboard.setContents(
            FileListTransferable(listOf(file)),
            null,
        )
    }

    private fun ensurePdfExtension(file: File): File =
        if (file.extension.equals("pdf", ignoreCase = true)) {
            file
        } else {
            File(file.parentFile, file.name + ".pdf")
        }

    private fun defaultShareDirectory(): File =
        File(System.getProperty("java.io.tmpdir"), "dnd-custom-aid/share")

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
