package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleImporter
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleParts
import io.github.mrsimkin.dndcustomaid.shared.content.FileMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.parseContentBundle
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * F2 refactor: the whole App de PC in its one Material 3 style. Walks every workbench screen with
 * content, saves a screenshot of each for review, and fails if a screen does not open.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopWorkbenchTourTest {
    @Test
    fun everyWorkbenchScreenOpensInTheOneStyle() = runDesktopComposeUiTest(width = 1500, height = 950) {
        val out = File("build/workbench-tour").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-tour-")
        val handle = DesktopDatabaseFactory(dir.resolve("tour.db").toFile()).create()
        val database = handle.database
        val campaigns = CampaignRepository(database)
        val campaign = campaigns.createCampaign("Serpiente Vieja")
        campaigns.setActiveCampaign(campaign.id)
        val blobs = FileMediaBlobStore(dir.resolve("media").toFile())
        ContentBundleImporter(database, blobs) { 100 }.load(
            parseContentBundle(
                """
                {"places": [{"key": "d:baja", "name": "Ciudad Baja", "payload": {"kind": "PLACE", "summary": "Mercado y tabernas"}},
                            {"key": "t:fragua", "name": "La Fragua", "payload": {"kind": "SHOP"}, "parentKey": "d:baja", "shopItems": [{"name": "Espada larga", "price": "15 po"}]}],
                 "npcs": [{"key": "p:olga", "name": "Olga", "payload": {"conceptRole": "Herrera"}}],
                 "creatures": [{"key": "c:obrero", "name": "Obrero alterado", "payload": {"armorClass": 14, "hitPoints": 31}}],
                 "encounters": [{"key": "e:1", "name": "Patrulla", "participants": [{"creatureKey": "c:obrero", "quantity": 2}]}],
                 "dungeons": [{"key": "dg:1", "name": "La Cueva", "zones": [{"key": "z:1", "name": "Entrada", "encounterKeys": ["e:1"]}, {"key": "z:2", "name": "Galería"}],
                               "passages": [{"from": "z:1", "to": "z:2"}]}],
                 "scenes": [{"key": "s:1", "name": "Llegada", "adventure": "La Cueva", "order": 1, "payload": {"purpose": "Llegar", "sections": {"READ_ALOUD_SHORT": "La grieta se abre."}}}],
                 "notes": [{"key": "n:1", "text": "Gancho: luces en la cueva."}],
                 "characters": [{"key": "pj:ana", "name": "Ana", "player": "Daniela", "classes": [{"name": "Pícaro", "level": 3, "hitDie": 8, "edition": "5.5e"}]}]}
                """.trimIndent(),
            ),
            campaign.id, ContentBundleParts.ALL, null,
        ) { null }

        val auth = DesktopHostedAuthController(vault = InMemoryCredentialVault())
        val hosted = DesktopHostedCampaignAdministrationController(database, accessTokens = auth, mediaBlobs = blobs)
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))
        val session = DesktopAccountSession(auth, hosted, platform.store)
        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.NIGHT_BLUE)) {
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                    ProvideDesktopContentLinks(DesktopContentLinksController(database, blobs)) {
                        DesktopWorkbench(
                            database = database,
                            mediaBlobs = blobs,
                            dmPlatform = platform,
                            campaignRepository = campaigns,
                            creatureManagerController = DesktopCreatureManagerController(database),
                            npcManagerController = DesktopNpcManagerController(database),
                            homebrewRuleManagerController = DesktopHomebrewRuleManagerController(database),
                            placeManagerController = DesktopPlaceManagerController(database),
                            handoutManagerController = DesktopHandoutManagerController(database, blobs),
                            dungeonEditorController = DesktopDungeonEditorController(database, blobs),
                            pcManagerController = DesktopPcManagerController(database),
                            accountSession = session,
                            hostedCampaignController = hosted,
                            preferences = DesktopPreferences(themeChoice = DesktopThemeChoice.NIGHT_BLUE),
                            onPreferencesChange = {},
                        )
                    }
                }
            }
        }

        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }
        fun open(label: String, expect: String, name: String) {
            onAllNodesWithText(label)[0].performClick()
            waitForIdle()
            shot(name)
            assertTrue(onAllNodesWithText(expect, substring = true, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty(), "$label shows «$expect»")
        }

        shot("t01_inicio")
        open("Preparar", "Lugares y tiendas", "t02_preparar")
        open("Lugares y tiendas", "La Fragua", "t03_lugares")
        open("← Preparar", "Dungeons", "t04_preparar_otra_vez")
        open("Dungeons", "La Cueva", "t05_dungeons")
        open("Personajes", "Ana", "t06_personajes")
        // Refactor F4 (point 6): the PC opens in plain words; the technical lines stay closed.
        open("Ana", "La hoja: lo principal", "t06b_personaje_ana")
        val technical = hasText("Datos técnicos (para diagnóstico)", substring = true)
        val lists = onAllNodes(hasScrollToNodeAction())
        (0 until lists.fetchSemanticsNodes().size).forEach { runCatching { lists[it].performScrollToNode(technical) } }
        waitForIdle()
        assertTrue(onAllNodesWithText("Revisión sync local", substring = true).fetchSemanticsNodes().isEmpty(), "sync audit is closed")
        onAllNodes(technical)[0].performClick()
        waitForIdle()
        (0 until lists.fetchSemanticsNodes().size).forEach { runCatching { lists[it].performScrollToNode(hasText("Revisión sync local", substring = true)) } }
        shot("t06c_personaje_datos_tecnicos")
        assertTrue(onAllNodesWithText("Revisión sync local", substring = true).fetchSemanticsNodes().isNotEmpty(), "technical data opens on demand")
        open("Reglas", "SRD 5.5e", "t07_reglas")
        open("Handouts y mapas", "Importar imagen", "t08_handouts")
        // Refactor F4 (point 5): loading a package lives in Campañas, as on the tablet.
        open("Campañas", "Cargar un paquete preparado en «Serpiente Vieja»", "t09_campanas")
        open("Configuración", "Tamaño de texto", "t10_configuracion")
        handle.close()
    }
}
