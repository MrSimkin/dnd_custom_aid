package io.github.mrsimkin.dndcustomaid.shared.campaign

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class CampaignRepositoryTest {
    @Test
    fun createCampaignTrimsAndPersistsName() = withInMemoryRepository { repository ->
        val campaign = repository.createCampaign("  Terramore  ")

        assertEquals("Terramore", campaign.name)
        assertEquals(listOf(campaign), repository.listCampaigns())
    }

    @Test
    fun blankCampaignNameIsRejected() = withInMemoryRepository { repository ->
        assertFailsWith<IllegalArgumentException> {
            repository.createCampaign(" \n\t ")
        }
    }

    @Test
    fun duplicateDisplayNamesKeepDistinctIdentity() = withInMemoryRepository { repository ->
        val first = repository.createCampaign("Terramore")
        val second = repository.createCampaign("Terramore")

        assertNotEquals(first.id, second.id)
        assertEquals(2, repository.listCampaigns().size)
    }

    @Test
    fun activeCampaignCanBeChanged() = withInMemoryRepository { repository ->
        val first = repository.createCampaign("First")
        val second = repository.createCampaign("Second")

        assertNull(repository.activeCampaign())

        repository.setActiveCampaign(first.id)
        assertEquals(first, repository.activeCampaign())

        repository.setActiveCampaign(second.id)
        assertEquals(second, repository.activeCampaign())
    }

    @Test
    fun campaignsAndActiveSelectionSurviveDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-campaign", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"

        try {
            val firstDriver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.create(firstDriver)
            val firstRepository = CampaignRepository(AppDatabase(firstDriver))

            val campaign = firstRepository.createCampaign("Persistent Campaign")
            firstRepository.setActiveCampaign(campaign.id)
            firstDriver.close()

            val secondDriver = JdbcSqliteDriver(jdbcUrl)
            val secondRepository = CampaignRepository(AppDatabase(secondDriver))

            assertEquals(listOf(campaign), secondRepository.listCampaigns())
            assertEquals(campaign, secondRepository.activeCampaign())
            secondDriver.close()
        } finally {
            file.delete()
        }
    }

    private fun withInMemoryRepository(block: (CampaignRepository) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)

        try {
            block(CampaignRepository(AppDatabase(driver)))
        } finally {
            driver.close()
        }
    }
}
