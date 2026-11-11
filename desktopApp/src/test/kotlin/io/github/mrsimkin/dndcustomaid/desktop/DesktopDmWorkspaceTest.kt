package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.test.runComposeUiTest
import io.github.mrsimkin.dndcustomaid.dmui.DmSyncResult
import io.github.mrsimkin.dndcustomaid.dmui.DmWorkspaceScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutKind
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutPayload
import io.github.mrsimkin.dndcustomaid.shared.content.MapPin
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ScenePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ShopItem
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePayload
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The Escritorio DM on Windows: the same shared desks as the tablet, with the desktop's own platform. */
@OptIn(ExperimentalTestApi::class)
class DesktopDmWorkspaceTest {
    @Test
    fun desktopPlatformKeepsSettingsAndReadsTheBundledSrd() {
        val dir = Files.createTempDirectory("dnd-custom-aid-dm-store-")
        val file = dir.resolve("dm_workspace.properties").toFile()
        DesktopDmKeyValueStore(file).putString("nav_style", "TOP_TABS")
        assertEquals("TOP_TABS", DesktopDmKeyValueStore(file).getString("nav_style"), "settings survive a restart")

        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(file))
        assertTrue(platform.readSrd("quick_reference_es.json").contains("\"cards\""), "the SRD ships inside the Windows app")
    }

    @Test
    fun theEscritorioOpensTheDmScreenAndACombatOnWindows() = runComposeUiTest {
        val dir = Files.createTempDirectory("dnd-custom-aid-dm-desk-")
        val handle = DesktopDatabaseFactory(dir.resolve("desk.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val goblin = CreatureContentRepository(database).createCampaign(
            campaign.id,
            "Goblin",
            CreaturePayload(armorClass = 15, hitPoints = 7, hitDice = "2d6", abilityScores = CreatureAbilityScores(dexterity = 14)),
            100,
        )
        val combat = CombatEngine.addCreature(CombatEngine.new(campaign.id, "Puente", 200), goblin, 3)
        DmCombatRepository(database).save(combat, 200)
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                DmWorkspaceScreen(
                    campaign = campaign,
                    database = database,
                    mediaBlobs = InMemoryMediaBlobStore(),
                    platform = platform,
                    accountId = null,
                    onSync = { DmSyncResult(synced = true) },
                    onExitToCampaigns = {},
                    onOpenAppSettings = {},
                )
            }
        }

        onAllNodesWithText("Pantalla del DM")[0].performClick()
        onNodeWithText("Referencia rápida").assertExists()
        onAllNodesWithText("⚔ Puente")[0].performClick()
        onNodeWithText("Paso 1 · Preparar").assertExists()
        onAllNodesWithText("Goblin ×3", useUnmergedTree = true)[0].performClick()
        onAllNodesWithText("Goblin ×3", useUnmergedTree = true)[0].assertExists()
        onAllNodesWithText("Editar completa")[0].assertExists()
        handle.close()
    }

    /** Renders the Windows desks at a laptop size into build/dm-desk-proofs for visual review. */
    @Test
    fun theWindowsDesksRenderForVisualReview() = runDesktopComposeUiTest(width = 1600, height = 960) {
        val out = File(System.getProperty("pcSheetProofDir") ?: "build/pc-sheet-proofs").parentFile.resolve("dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-dm-proof-")
        val handle = DesktopDatabaseFactory(dir.resolve("proof.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        listOf(Triple("Aria", 16, 14), Triple("Borin", 18, 10), Triple("Cael", 13, 16)).forEach { (name, ac, wis) ->
            val pc = characters.createCharacter(campaign.id, name)
            characters.saveCharacter(pc.copy(armorClass = ac, maxHp = ac * 2, currentHp = ac * 2, wisdom = wis, speed = 30))
        }
        val goblin = CreatureContentRepository(database).createCampaign(
            campaign.id,
            "Goblin jefe",
            CreaturePayload(
                armorClass = 15, hitPoints = 22, hitDice = "5d6+5", speed = "9 m", challengeRating = "1",
                abilityScores = CreatureAbilityScores(dexterity = 14),
                actions = "Cimitarra. +4 al ataque, 1d6+2 cortante.", tactics = "Ataca desde la cobertura.",
            ),
            100,
        )
        val combat = CombatEngine.addPcs(CombatEngine.addCreature(CombatEngine.new(campaign.id, "Puente", 200), goblin, 3), characters.listCharacters(campaign.id))
        DmCombatRepository(database).save(combat, 200)
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                DmWorkspaceScreen(
                    campaign = campaign,
                    database = database,
                    mediaBlobs = InMemoryMediaBlobStore(),
                    platform = platform,
                    accountId = null,
                    onSync = { DmSyncResult(synced = true) },
                    onExitToCampaigns = {},
                    onOpenAppSettings = {},
                )
            }
        }

        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }
        shot("w1_escritorio")
        onAllNodesWithText("Pantalla del DM")[0].performClick()
        onAllNodesWithText("Borin", useUnmergedTree = true)[0].performClick()
        shot("w2_pantalla_dm")
        onAllNodesWithText("⚔ Puente")[0].performClick()
        onAllNodesWithText("Goblin jefe ×3", useUnmergedTree = true)[0].performClick()
        shot("w3_preparar")
        onAllNodesWithText("Empezar combate ▸")[0].performClick()
        shot("w4_iniciativa")
        handle.close()
    }

    /** The Mesa de Escena on Windows with a small linked campaign: search by idea, a shop, an adventure, a map. */
    @Test
    fun theSceneDeskRendersForVisualReview() = runDesktopComposeUiTest(width = 1600, height = 960) {
        val out = File(System.getProperty("pcSheetProofDir") ?: "build/pc-sheet-proofs").parentFile.resolve("dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-scene-proof-")
        val handle = DesktopDatabaseFactory(dir.resolve("scene.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val blobs = InMemoryMediaBlobStore()
        val market = ZoneContentRepository(database).createCampaign(campaign.id, "Mercado viejo", ZonePayload(summary = "Plaza de tenderetes bajo lonas", playerSafeText = "Huele a pan y a hierro caliente."), 100)
        val forge = PlaceContentRepository(database).createCampaign(
            campaign.id,
            "La Fragua Roja",
            PlacePayload(kind = PlaceKind.SHOP, function = "Herrería", presentation = "Un yunque al frente y chispas en la puerta.", paperReferences = listOf("Carpeta B, pág. 17"), dmNotes = "Bruna fía a quien ayude al gremio."),
            100,
        )
        val inn = PlaceContentRepository(database).createCampaign(campaign.id, "El Pato Cojo", PlacePayload(function = "Posada"), 100)
        val npcs = NpcContentRepository(database)
        val bruna = npcs.createCampaign(
            campaign.id,
            "Bruna Hierro",
            NpcPayload(conceptRole = "Herrera gruñona", appearanceFirstImpression = "Brazos tatuados. Huele a carbón.", personalityManner = "Corta y directa; respeta a quien regatea bien.", canOffer = "Armas y armaduras a buen precio."),
            100,
        )
        val tom = npcs.createCampaign(campaign.id, "Tom", NpcPayload(conceptRole = "Posadero chismoso", appearanceFirstImpression = "Delantal con manchas de vino."), 100)
        val scenes = SceneContentRepository(database)
        val arrival = scenes.createCampaign(campaign.id, "Llegada a la ciudad", ScenePayload(purpose = "Presentar el mercado"), 100)
        val ambush = scenes.createCampaign(campaign.id, "Emboscada en el callejón", ScenePayload(purpose = "Primer combate"), 100)
        val thief = scenes.createCampaign(campaign.id, "Ladrón en el mercado", ScenePayload(purpose = "Persecución opcional"), 100)

        val png = java.io.ByteArrayOutputStream().also { bytes ->
            val image = java.awt.image.BufferedImage(800, 500, java.awt.image.BufferedImage.TYPE_INT_RGB)
            image.createGraphics().apply {
                color = java.awt.Color(0xD8C9A3); fillRect(0, 0, 800, 500)
                color = java.awt.Color(0x8B6B3D); fillRect(80, 80, 260, 160); fillRect(440, 260, 280, 170)
            }
            ImageIO.write(image, "png", bytes)
        }.toByteArray()
        val sha = blobs.write(png)
        val map = HandoutContentRepository(database).createCampaign(
            campaign.id, "Plano del mercado",
            HandoutPayload(kind = HandoutKind.MAP, mediaType = "image/png", sizeBytes = png.size.toLong(), sha256 = sha, width = 800, height = 500),
            100,
        )
        val links = ContentLinksRepository(database)
        links.save(forge.item.identity.id, ContentLinks(zoneIds = listOf(market.item.identity.id), npcIds = listOf(bruna.item.identity.id), shopItems = listOf(
            ShopItem("Cota de malla", "75 po", highlighted = true), ShopItem("Espada larga", "15 po"), ShopItem("Escudo", "10 po", note = "con el emblema del gremio"),
        )))
        links.save(inn.item.identity.id, ContentLinks(zoneIds = listOf(market.item.identity.id), npcIds = listOf(tom.item.identity.id)))
        links.save(bruna.item.identity.id, ContentLinks(groups = listOf("Gremio de herreros")))
        links.save(market.item.identity.id, ContentLinks(mapHandoutId = map.item.identity.id))
        links.save(map.item.identity.id, ContentLinks(pins = listOf(MapPin(0.26, 0.32, forge.item.identity.id, "Fragua"), MapPin(0.72, 0.68, inn.item.identity.id, "Posada"))))
        links.save(arrival.item.identity.id, ContentLinks(adventure = "La ciudad", order = 1, sceneIds = listOf(ambush.item.identity.id), zoneIds = listOf(market.item.identity.id)))
        links.save(ambush.item.identity.id, ContentLinks(adventure = "La ciudad", order = 2))
        links.save(thief.item.identity.id, ContentLinks(adventure = "La ciudad", order = 2, detour = true))
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                DmWorkspaceScreen(
                    campaign = campaign,
                    database = database,
                    mediaBlobs = blobs,
                    platform = platform,
                    accountId = null,
                    onSync = { DmSyncResult(synced = true) },
                    onExitToCampaigns = {},
                    onOpenAppSettings = {},
                )
            }
        }

        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }
        onAllNodesWithText("Escena")[0].performClick()
        onAllNodesWithText("PNJ")[0].performClick()
        onAllNodes(hasSetTextAction())[0].performTextInput("tatuados")
        onAllNodesWithText("Herrera gruñona", useUnmergedTree = true)[0].performClick()
        shot("w5_escena_pnj_por_idea")
        onAllNodesWithText("📍 La Fragua Roja", useUnmergedTree = true)[0].performClick()
        shot("w6_escena_tienda")
        onAllNodesWithText("Recorrer")[0].performClick()
        onAllNodesWithText("Por aventura")[0].performClick()
        onAllNodesWithText("1. Llegada a la ciudad", useUnmergedTree = true)[0].performClick()
        shot("w7_escena_aventura")
        onAllNodesWithText("🗺 Mapa")[0].performClick()
        onAllNodesWithText("🗺 Mercado viejo", useUnmergedTree = true)[0].performClick()
        shot("w8_escena_mapa")
        handle.close()
    }
}
