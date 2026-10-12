package io.github.mrsimkin.dndcustomaid.android.pdf

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomStatisticsPresentation
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticData
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportPlanner
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPortraitFitMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant
import java.io.File
import java.net.URI

internal data class AndroidPcSheetExportOptions(
    val visualFamily: PcSheetVisualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
    val stateSelection: PcSheetExportStateSelection = PcSheetExportStateSelection.PERMANENT,
    val customStatisticsPresentation: PcSheetCustomStatisticsPresentation =
        PcSheetCustomStatisticsPresentation.EXTENDED_PAGE,
    val portraitFitMode: PcSheetPortraitFitMode = PcSheetPortraitFitMode.CROP_TO_FILL,
    val includeSpellDescriptions: Boolean = false,
    /** QA-only opt-in; null preserves all four existing PDF exporters exactly. */
    val v11ProofVariant: PcSheetV11Variant? = null,
) {
    fun request(): PcSheetPdfExportRequest = PcSheetPdfExportRequest(
        visualFamily = visualFamily,
        stateSelection = stateSelection,
        customStatisticsPresentation = customStatisticsPresentation,
        portraitFitMode = portraitFitMode,
        includeSpellDescriptions = includeSpellDescriptions,
    )
}

internal data class AndroidPcSheetGeneratedPdf(
    val plan: PcSheetPdfRenderPlan,
    val bytes: ByteArray,
)

/** Internal QA-only actual PDF bytes, not a production V11 visual export. */
internal data class AndroidPcSheetV11PdfProof(
    val semantic: PcSheetV11SemanticData,
    val bytes: ByteArray,
)

internal class AndroidPcSheetExportService(
    context: Context,
) {
    private val appContext = context.applicationContext

    /** Internal V11 PDF proof, separate from all four production renderer families. */
    fun generateV11Proof(
        request: PcSheetV11ExportRequest,
        sources: PcSheetExportSources,
    ): AndroidPcSheetV11PdfProof {
        AndroidPcSheetAssetLoader.initialize(appContext)
        val selected = if (request.stateSelection ==
            io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection.CURRENT_SNAPSHOT
        ) sources.currentSnapshot ?: sources.permanent else sources.permanent
        val portraitRef = selected.closure.portraitRef?.trim()?.takeIf { it.isNotBlank() }
        val portraitBytes = portraitRef?.let { loadLocalPortraitBytes(it) }
        val effectiveSources = if (portraitRef != null && portraitBytes != null) {
            sources.copy(locallyAvailablePortraitRefs =
                sources.locallyAvailablePortraitRefs + portraitRef)
        } else sources
        val semantic = PcSheetV11SemanticMapper.map(request, effectiveSources)
        val bytes = java.io.ByteArrayOutputStream().use { buffer ->
            AndroidPcSheetV11PdfProofRenderer().render(semantic, buffer, portraitBytes)
            buffer.toByteArray()
        }
        check(bytes.isNotEmpty())
        return AndroidPcSheetV11PdfProof(semantic, bytes)
    }

    fun generate(
        request: PcSheetPdfExportRequest,
        sources: PcSheetExportSources,
    ): AndroidPcSheetGeneratedPdf {
        val portraitBytes = mutableMapOf<String, ByteArray>()
        buildList {
            sources.permanent.closure.portraitRef?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            sources.currentSnapshot?.closure?.portraitRef?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        }.distinct().forEach { ref ->
            loadLocalPortraitBytes(ref)?.let { bytes -> portraitBytes[ref] = bytes }
        }

        val effectiveSources = sources.copy(
            locallyAvailablePortraitRefs = sources.locallyAvailablePortraitRefs + portraitBytes.keys,
        )
        val plan = PcSheetPdfExportPlanner.plan(request, effectiveSources)
        val bytes = AndroidPcSheetRendererBridge(
            portraitBytesLoader = { ref ->
                portraitBytes[ref] ?: loadLocalPortraitBytes(ref)
            },
        ).render(plan)
        return AndroidPcSheetGeneratedPdf(plan = plan, bytes = bytes)
    }

    fun stage(
        generated: AndroidPcSheetGeneratedPdf,
        characterName: String,
        purpose: String,
    ): File {
        val directory = File(appContext.cacheDir, "pc-sheet-pdf/$purpose")
        directory.mkdirs()
        directory.listFiles()?.forEach { stale -> stale.delete() }

        val target = File(
            directory,
            suggestedFileName(characterName, generated.plan.request.visualFamily),
        )
        val temporary = File(directory, target.name + ".tmp")
        temporary.writeBytes(generated.bytes)
        if (target.exists()) target.delete()
        check(temporary.renameTo(target)) {
            "No se pudo preparar el PDF local para $purpose."
        }
        return target
    }

    /** Explicitly stamped, local debug-preview PDF staged via the SAME Save/Share path. */
    fun stageV11Proof(
        generated: AndroidPcSheetV11PdfProof,
        characterName: String,
        purpose: String,
    ): File {
        val directory = File(appContext.cacheDir, "pc-sheet-pdf/" + purpose)
        directory.mkdirs()
        directory.listFiles()?.forEach { stale -> stale.delete() }
        val target = File(directory, suggestedV11ProofFileName(characterName, generated.semantic.request.variant))
        val temporary = File(directory, target.name + ".tmp")
        temporary.writeBytes(generated.bytes)
        if (target.exists()) target.delete()
        check(temporary.renameTo(target)) { "No se pudo preparar el PDF de diagnóstico." }
        return target
    }

    fun suggestedV11ProofFileName(
        characterName: String,
        variant: PcSheetV11Variant,
    ): String {
        val safe = characterName.trim()
            .replace(Regex("""[\\/:*?"<>|]+"""), "_")
            .replace(Regex("""\s+"""), " ")
            .ifBlank { "Personaje" }
        return "$safe - Hoja de PJ - PREQA V11 $variant.pdf"
    }

    fun writeToDocument(staged: File, destination: Uri) {
        val output = requireNotNull(appContext.contentResolver.openOutputStream(destination, "w")) {
            "No se pudo abrir el archivo de destino."
        }
        output.use { stream -> staged.inputStream().use { input -> input.copyTo(stream) } }
    }

    fun shareUri(staged: File): Uri =
        FileProvider.getUriForFile(
            appContext,
            appContext.packageName + ".pc-sheet-fileprovider",
            staged,
        )

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

    private fun loadLocalPortraitBytes(ref: String): ByteArray? = runCatching {
        when {
            ref.startsWith("content:", ignoreCase = true) -> {
                appContext.contentResolver.openInputStream(Uri.parse(ref))?.use { it.readBytes() }
            }
            ref.startsWith("file:", ignoreCase = true) -> {
                File(URI(ref)).takeIf { it.isFile }?.readBytes()
            }
            else -> File(ref).takeIf { it.isFile }?.readBytes()
        }
    }.getOrNull()

    private fun familyFileLabel(family: PcSheetVisualFamily): String = when (family) {
        PcSheetVisualFamily.CLASSIC_DND_STYLE -> "Fantasy Sheet"
        PcSheetVisualFamily.CUSTOM_V1 -> "Custom v1"
        PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE -> "Custom v2 - Atributo"
        PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY -> "Custom v2 - Habilidad"
    }
}
