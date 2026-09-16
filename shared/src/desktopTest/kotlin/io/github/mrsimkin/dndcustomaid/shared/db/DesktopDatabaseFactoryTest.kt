package io.github.mrsimkin.dndcustomaid.shared.db

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DesktopDatabaseFactoryTest {
    @Test
    fun createsPersistentDatabaseAndReopensExistingSchema() {
        val directory = createTempDirectory("dnd-custom-aid-desktop-").toFile()
        val databaseFile = File(directory, "desktop.db")

        try {
            DesktopDatabaseFactory(databaseFile).create().use { firstHandle ->
                val repository = CampaignRepository(firstHandle.database)
                val campaign = repository.createCampaign("Desktop Campaign")
                repository.setActiveCampaign(campaign.id)

                assertTrue(databaseFile.exists())
                assertEquals(campaign, repository.activeCampaign())
            }

            DesktopDatabaseFactory(databaseFile).create().use { secondHandle ->
                val repository = CampaignRepository(secondHandle.database)

                assertEquals(listOf("Desktop Campaign"), repository.listCampaigns().map { it.name })
                assertEquals("Desktop Campaign", repository.activeCampaign()?.name)
            }
        } finally {
            directory.deleteRecursively()
        }
    }
}
