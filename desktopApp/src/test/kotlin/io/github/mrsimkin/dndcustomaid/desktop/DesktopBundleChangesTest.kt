package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.BundleNpc
import io.github.mrsimkin.dndcustomaid.shared.content.BundlePlace
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundle
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleImporter
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleParts
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.stableContentId
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner point 12: loading a package again never updated what the DM had edited, and nothing said
 * what had gone stale. The comparison must name it, and only change what the DM marks.
 */
class DesktopBundleChangesTest {
    private fun bundle(conceptRole: String, withPlace: Boolean) = ContentBundle(
        title = "Campus Celeste",
        npcs = listOf(BundleNpc(key = "pnj:barek", name = "Barek Surn", payload = NpcPayload(conceptRole = conceptRole))),
        places = if (withPlace) listOf(BundlePlace(key = "tienda:punta", name = "Punta y Compás", payload = PlacePayload(summary = "Tienda"))) else emptyList(),
    )

    @Test
    fun itSaysWhatIsNewWhatIsTheSameAndWhereThePackageDisagrees() {
        val dir = Files.createTempDirectory("dnd-custom-aid-changes-")
        val handle = DesktopDatabaseFactory(dir.resolve("mine.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        // What the DM has: the package of yesterday, with their own edit on the PNJ.
        ContentBundleImporter(database, InMemoryMediaBlobStore(), { 100 })
            .load(bundle("Capataz", withPlace = false), campaign.id, ContentBundleParts.DM_CONTENT, null) { null }
        val npcs = NpcContentRepository(database)
        val mineId = stableContentId(campaign.id, "pnj:barek")
        val mine = npcs.npc(mineId)!!
        npcs.update(mineId, mine.item.identity.revision, "Barek Surn", mine.payload.copy(secrets = "Falsea el conteo."), 200)

        val comparison = compareBundleWithCampaign(
            database = database,
            bundle = bundle("Capataz del turno de noche", withPlace = true),
            campaignId = campaign.id,
            campaignName = campaign.name,
            ownerAccountId = null,
            readFile = { null },
            nowEpochSeconds = 300,
        )

        assertEquals(listOf("Punta y Compás"), comparison.news.map { it.name })
        val different = comparison.different.single()
        assertEquals("Barek Surn", different.name)
        assertTrue(different.fields.contains("concepto"), different.fields.toString())

        // Marking it takes the package's version; the place that was not marked is not added.
        val applied = comparison.useTheirs(database, setOf(different.id), 400)
        assertEquals(1, applied)
        assertEquals("Capataz del turno de noche", npcs.npc(mineId)?.payload?.conceptRole)
        assertEquals(
            1,
            io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository(database)
                .listCampaign(campaign.id, io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily.NPC).size,
        )
        assertTrue(
            io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository(database)
                .listCampaign(campaign.id, io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily.PLACE).isEmpty(),
            "what was not marked is not brought in",
        )
        handle.close()
    }

    @Test
    fun theSamePackageTwiceHasNothingToChange() {
        val dir = Files.createTempDirectory("dnd-custom-aid-changes-same-")
        val handle = DesktopDatabaseFactory(dir.resolve("mine.db").toFile()).create()
        val campaign = CampaignRepository(handle.database).createCampaign("Serpiente Vieja")
        ContentBundleImporter(handle.database, InMemoryMediaBlobStore(), { 100 })
            .load(bundle("Capataz", withPlace = true), campaign.id, ContentBundleParts.DM_CONTENT, null) { null }

        val comparison = compareBundleWithCampaign(
            database = handle.database,
            bundle = bundle("Capataz", withPlace = true),
            campaignId = campaign.id,
            campaignName = campaign.name,
            ownerAccountId = null,
            readFile = { null },
            nowEpochSeconds = 300,
        )

        assertTrue(comparison.news.isEmpty(), comparison.news.toString())
        assertTrue(comparison.different.isEmpty(), comparison.different.toString())
        assertEquals(2, comparison.same)
        handle.close()
    }
}
