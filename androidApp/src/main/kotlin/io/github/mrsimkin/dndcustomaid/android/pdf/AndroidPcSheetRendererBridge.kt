package io.github.mrsimkin.dndcustomaid.android.pdf

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import java.io.ByteArrayOutputStream

internal class AndroidPcSheetRendererBridge(
    private val portraitBytesLoader: (String) -> ByteArray? = { null },
) {
    fun render(plan: PcSheetPdfRenderPlan): ByteArray =
        ByteArrayOutputStream().use { output ->
            AndroidPcSheetWholeDraftRenderer(
                resourceLoader = AndroidPcSheetAssetLoader::open,
                portraitBytesLoader = portraitBytesLoader,
            ).renderDraft(plan, output)
            output.toByteArray()
        }
}
