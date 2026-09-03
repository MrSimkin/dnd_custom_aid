package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharacterPresentationTest {
    private data class Item(
        val id: String,
        val name: String,
        val sortOrder: Int,
        val source: String,
        val tags: Set<String> = emptySet(),
    )

    private val items = listOf(
        Item("b", "Éter", 0, "Mago", setOf("prepared")),
        Item("a", "Ácido", 2, "Artífice", setOf("ritual")),
        Item("c", "Bendición", 1, "Clérigo", setOf("prepared", "concentration")),
    )

    @Test
    fun manualPresentationUsesStoredOrderWithoutMutatingInput() {
        val original = items.toList()
        val presented = presentCharacterCollection(
            items = items,
            order = CharacterPresentationOrder.MANUAL,
            manualOrder = Item::sortOrder,
            label = Item::name,
            stableKey = Item::id,
        )

        assertEquals(listOf("Éter", "Bendición", "Ácido"), presented.map(Item::name))
        assertEquals(original, items)
    }

    @Test
    fun alphabeticalPresentationDoesNotDestroyManualOrder() {
        val alphabetical = presentCharacterCollection(
            items = items,
            order = CharacterPresentationOrder.ALPHABETICAL,
            manualOrder = Item::sortOrder,
            label = Item::name,
            stableKey = Item::id,
        )
        val restoredManual = presentCharacterCollection(
            items = items,
            order = CharacterPresentationOrder.MANUAL,
            manualOrder = Item::sortOrder,
            label = Item::name,
            stableKey = Item::id,
        )

        assertEquals(listOf("Ácido", "Bendición", "Éter"), alphabetical.map(Item::name))
        assertEquals(listOf("Éter", "Bendición", "Ácido"), restoredManual.map(Item::name))
        assertEquals(listOf(0, 2, 1), items.map(Item::sortOrder))
    }

    @Test
    fun searchIsCaseAndCommonAccentInsensitiveAcrossMultipleFields() {
        val acid = presentCharacterCollection(
            items = items,
            order = CharacterPresentationOrder.MANUAL,
            manualOrder = Item::sortOrder,
            label = Item::name,
            stableKey = Item::id,
            query = CharacterCollectionQuery(searchText = "acido"),
            searchableText = { listOf(it.name, it.source) },
        )
        val artificer = presentCharacterCollection(
            items = items,
            order = CharacterPresentationOrder.MANUAL,
            manualOrder = Item::sortOrder,
            label = Item::name,
            stableKey = Item::id,
            query = CharacterCollectionQuery(searchText = "ARTÍFICE"),
            searchableText = { listOf(it.name, it.source) },
        )

        assertEquals(listOf("Ácido"), acid.map(Item::name))
        assertEquals(listOf("Ácido"), artificer.map(Item::name))
        assertTrue(characterSearchMatches(normalizeCharacterSearchText("bendicion"), "Bendición"))
        assertFalse(characterSearchMatches(normalizeCharacterSearchText("druida"), "Bendición", "Clérigo"))
    }

    @Test
    fun queryFilterStateIsImmutableAndComposable() {
        val initial = CharacterCollectionQuery(searchText = "  eTer ")
        val prepared = initial.toggleFilter("prepared")
        val concentration = prepared.toggleFilter("concentration")
        val preparedRemoved = concentration.toggleFilter("prepared")

        assertEquals("eter", initial.normalizedSearchText)
        assertTrue(initial.activeFilterKeys.isEmpty())
        assertEquals(setOf("prepared"), prepared.activeFilterKeys)
        assertEquals(setOf("prepared", "concentration"), concentration.activeFilterKeys)
        assertEquals(setOf("concentration"), preparedRemoved.activeFilterKeys)
        assertEquals(CharacterCollectionQuery(), preparedRemoved.clear())
    }

    @Test
    fun filtersAndSearchDoNotRewriteStoredOrdering() {
        val presented = presentCharacterCollection(
            items = items,
            order = CharacterPresentationOrder.ALPHABETICAL,
            manualOrder = Item::sortOrder,
            label = Item::name,
            stableKey = Item::id,
            query = CharacterCollectionQuery(
                searchText = "",
                activeFilterKeys = setOf("prepared"),
            ),
            searchableText = { listOf(it.name, it.source) },
            filterMatches = { item, filters -> filters.all { it in item.tags } },
        )

        assertEquals(listOf("Bendición", "Éter"), presented.map(Item::name))
        assertEquals(listOf(0, 2, 1), items.map(Item::sortOrder))
    }
}
