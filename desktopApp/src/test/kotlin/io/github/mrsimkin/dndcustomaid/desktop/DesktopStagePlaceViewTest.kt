package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContent
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentItem
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.ScopedObjectIdentity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class DesktopStagePlaceViewTest {
    @Test
    fun filtersAndSortsExistingPlacesWithoutCreatingASecondStageModel() {
        val personalShop = place(
            name = "Herbalist",
            kind = PlaceKind.SHOP,
            scope = ContentScope.Personal(Uuid.random()),
            area = "Old Market",
            function = "Healing and remedies",
            tags = listOf("shop", "herbs"),
            updatedAt = 150L,
        )
        val personalPlace = place(
            name = "Old Keep",
            kind = PlaceKind.PLACE,
            scope = ContentScope.Personal(Uuid.random()),
            area = "North Ridge",
            function = "Fortress",
            tags = listOf("ruin"),
            hooks = listOf("A sealed map is hidden behind the fallen altar."),
            updatedAt = 100L,
        )
        val campaignPlace = place(
            name = "Harbor Docks",
            kind = PlaceKind.PLACE,
            scope = ContentScope.Campaign(Uuid.random()),
            area = "Harbor",
            function = "Transit and cargo",
            tags = listOf("water", "travel"),
            updatedAt = 200L,
        )
        val campaignShop = place(
            name = "The Brass Lantern",
            kind = PlaceKind.SHOP,
            scope = ContentScope.Campaign(Uuid.random()),
            area = "Old Market",
            function = "Supplies and expedition provisioning",
            tags = listOf("shop", "market", "supplies"),
            updatedAt = 300L,
        )
        val places = listOf(campaignShop, personalPlace, campaignPlace, personalShop)

        assertEquals(
            listOf("Herbalist", "Old Keep", "Harbor Docks", "The Brass Lantern"),
            filterStagePlaces(places, StagePlaceFilters()).map { it.item.displayName },
        )

        assertEquals(
            listOf("Herbalist", "The Brass Lantern"),
            filterStagePlaces(
                places,
                StagePlaceFilters(
                    kind = PlaceKind.SHOP,
                    area = "old market",
                    tag = "shop",
                ),
            ).map { it.item.displayName },
        )

        assertEquals(
            listOf("Harbor Docks"),
            filterStagePlaces(
                places,
                StagePlaceFilters(
                    scope = StagePlaceScopeFilter.CAMPAIGN,
                    function = "transit",
                ),
            ).map { it.item.displayName },
        )

        assertEquals(
            listOf("Old Keep"),
            filterStagePlaces(
                places,
                StagePlaceFilters(query = "sealed map"),
            ).map { it.item.displayName },
        )

        assertEquals(
            listOf("The Brass Lantern", "Harbor Docks", "Herbalist", "Old Keep"),
            filterStagePlaces(
                places,
                StagePlaceFilters(sort = StagePlaceSort.RECENT),
            ).map { it.item.displayName },
        )

        assertEquals(
            listOf("Harbor Docks", "Old Keep", "Herbalist", "The Brass Lantern"),
            filterStagePlaces(
                places,
                StagePlaceFilters(sort = StagePlaceSort.AREA),
            ).map { it.item.displayName },
        )
    }

    private fun place(
        name: String,
        kind: PlaceKind,
        scope: ContentScope,
        area: String,
        function: String,
        tags: List<String>,
        hooks: List<String> = emptyList(),
        updatedAt: Long,
    ): PlaceContent = PlaceContent(
        item = ReusableContentItem(
            identity = ScopedObjectIdentity(
                id = Uuid.random(),
                scope = scope,
            ),
            family = ReusableContentFamily.PLACE,
            displayName = name,
            createdAtEpochSeconds = 10L,
            updatedAtEpochSeconds = updatedAt,
        ),
        payload = PlacePayload(
            kind = kind,
            area = area,
            function = function,
            hooks = hooks,
            tags = tags,
        ),
    )
}
