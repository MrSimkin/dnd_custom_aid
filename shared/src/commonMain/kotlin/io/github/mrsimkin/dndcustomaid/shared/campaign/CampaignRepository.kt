package io.github.mrsimkin.dndcustomaid.shared.campaign

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

class CampaignRepository(
    private val database: AppDatabase,
) {
    fun listCampaigns(): List<Campaign> =
        database.campaignQueries.selectAllCampaigns { id, name ->
            Campaign(
                id = Uuid.parse(id),
                name = name,
            )
        }.executeAsList()

    fun campaign(id: Uuid): Campaign? =
        database.campaignQueries.selectCampaignById(id.toString()) { storedId, name ->
            Campaign(
                id = Uuid.parse(storedId),
                name = name,
            )
        }.executeAsOneOrNull()

    fun createCampaign(rawName: String): Campaign {
        val name = rawName.trim()
        require(name.isNotEmpty()) { "Campaign name must not be blank." }

        val campaign = Campaign(
            id = Uuid.random(),
            name = name,
        )

        database.campaignQueries.insertCampaign(
            id = campaign.id.toString(),
            name = campaign.name,
        )

        return campaign
    }

    fun upsertCampaign(id: Uuid, rawName: String): Campaign {
        val name = rawName.trim()
        require(name.isNotEmpty()) { "Campaign name must not be blank." }

        database.campaignQueries.upsertCampaign(
            id = id.toString(),
            name = name,
        )
        return Campaign(id = id, name = name)
    }

    fun setActiveCampaign(id: Uuid) {
        val storedCampaign = database.campaignQueries
            .selectCampaignById(id.toString())
            .executeAsOneOrNull()

        require(storedCampaign != null) { "Active campaign must already exist locally." }

        database.campaignQueries.setActiveCampaign(id.toString())
    }

    fun activeCampaign(): Campaign? =
        database.campaignQueries.selectActiveCampaign { id, name ->
            Campaign(
                id = Uuid.parse(id),
                name = name,
            )
        }.executeAsOneOrNull()
}
