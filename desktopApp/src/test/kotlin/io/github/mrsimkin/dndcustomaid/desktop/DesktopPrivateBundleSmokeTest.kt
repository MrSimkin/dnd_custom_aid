package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleImporter
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleParts
import io.github.mrsimkin.dndcustomaid.shared.content.FileMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.parseContentBundle
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Redesign R7: loads a DM's real content bundle into a throw-away database. The bundle is private
 * and never in the repository, so this runs only when DND_CONTENT_BUNDLE names the file.
 */
class DesktopPrivateBundleSmokeTest {
    @Test
    fun aPrivateBundleLoadsWithoutProblems() {
        val path = System.getenv("DND_CONTENT_BUNDLE") ?: return
        val file = File(path)
        val dir = Files.createTempDirectory("dnd-custom-aid-bundle-")
        val handle = DesktopDatabaseFactory(dir.resolve("bundle.db").toFile()).create()
        val campaign = CampaignRepository(handle.database).createCampaign("Prueba del paquete")
        val report = ContentBundleImporter(handle.database, FileMediaBlobStore(dir.resolve("media").toFile())) { 100 }
            .load(parseContentBundle(file.readText()), campaign.id, ContentBundleParts.ALL, null) { name ->
                File(file.parentFile, name).takeIf { it.isFile }?.readBytes()
            }
        println("PAQUETE: " + report.spanishSummary())
        println("CREADOS: " + report.created)
        report.problems.forEach { println("PROBLEMA: $it") }
        handle.close()
        assertTrue(report.problems.isEmpty(), report.problems.joinToString("\n"))
    }
}
