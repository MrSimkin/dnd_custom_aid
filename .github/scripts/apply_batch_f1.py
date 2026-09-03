from pathlib import Path

ROOT = Path('.')


def replace_once(path: str, old: str, new: str) -> None:
    p = ROOT / path
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f'{path}: expected exactly one match, found {count}: {old[:120]!r}')
    p.write_text(text.replace(old, new, 1))


def write_new(path: str, content: str) -> None:
    p = ROOT / path
    if p.exists():
        raise SystemExit(f'{path}: expected file to be absent')
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(content)


# 1) Add an explicit carried/stored state to the existing schema-7 inventory extension metadata.
replace_once(
    'shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureDomain.kt',
    '''enum class CharacterConsumableKind {\n    NONE,\n    CONSUMABLE,\n    AMMUNITION,\n}\n''',
    '''enum class CharacterConsumableKind {\n    NONE,\n    CONSUMABLE,\n    AMMUNITION,\n}\n\nenum class CharacterInventoryCarryState {\n    CARRIED,\n    STORED,\n}\n''',
)
replace_once(
    'shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureDomain.kt',
    '''data class CharacterInventoryUsage(\n    val itemId: Uuid,\n    val kind: CharacterConsumableKind = CharacterConsumableKind.NONE,\n    val quickUseAmount: Int = 1,\n)\n''',
    '''data class CharacterInventoryUsage(\n    val itemId: Uuid,\n    val kind: CharacterConsumableKind = CharacterConsumableKind.NONE,\n    val quickUseAmount: Int = 1,\n    val carryState: CharacterInventoryCarryState = CharacterInventoryCarryState.CARRIED,\n)\n''',
)

# 2) Schema/query definition for fresh databases + additive 7 -> 8 migration for existing databases.
replace_once(
    'shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/CharacterClosure.sq',
    '''CREATE TABLE character_inventory_usage (\n    character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,\n    item_id TEXT NOT NULL,\n    consumable_kind TEXT NOT NULL DEFAULT 'NONE',\n    quick_use_amount INTEGER NOT NULL DEFAULT 1,\n    PRIMARY KEY(character_id, item_id)\n);\n''',
    '''CREATE TABLE character_inventory_usage (\n    character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,\n    item_id TEXT NOT NULL,\n    consumable_kind TEXT NOT NULL DEFAULT 'NONE',\n    quick_use_amount INTEGER NOT NULL DEFAULT 1,\n    carry_state TEXT NOT NULL DEFAULT 'CARRIED',\n    PRIMARY KEY(character_id, item_id)\n);\n''',
)
replace_once(
    'shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/CharacterClosure.sq',
    '''upsertInventoryUsage:\nINSERT OR REPLACE INTO character_inventory_usage(character_id, item_id, consumable_kind, quick_use_amount)\nVALUES (?, ?, ?, ?);\n\nselectInventoryUsage:\nSELECT character_id, item_id, consumable_kind, quick_use_amount\nFROM character_inventory_usage\nWHERE character_id = ?\nORDER BY item_id;\n''',
    '''upsertInventoryUsage:\nINSERT OR REPLACE INTO character_inventory_usage(character_id, item_id, consumable_kind, quick_use_amount, carry_state)\nVALUES (?, ?, ?, ?, ?);\n\nselectInventoryUsage:\nSELECT character_id, item_id, consumable_kind, quick_use_amount, carry_state\nFROM character_inventory_usage\nWHERE character_id = ?\nORDER BY item_id;\n''',
)
write_new(
    'shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/8.sqm',
    "ALTER TABLE character_inventory_usage ADD COLUMN carry_state TEXT NOT NULL DEFAULT 'CARRIED';\n",
)

# 3) Repository mapping/persistence.
replace_once(
    'shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepository.kt',
    '''        val inventoryUsageRows = database.characterClosureQueries.selectInventoryUsage(id) {\n                _, itemId, kind, quickUseAmount ->\n            InventoryUsageRow(itemId, kind, quickUseAmount.toInt())\n        }.executeAsList()\n''',
    '''        val inventoryUsageRows = database.characterClosureQueries.selectInventoryUsage(id) {\n                _, itemId, kind, quickUseAmount, carryState ->\n            InventoryUsageRow(itemId, kind, quickUseAmount.toInt(), carryState)\n        }.executeAsList()\n''',
)
replace_once(
    'shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepository.kt',
    '''                CharacterInventoryUsage(\n                    itemId = parsedId,\n                    kind = enumOrDefault(row.kind, CharacterConsumableKind.NONE),\n                    quickUseAmount = row.quickUseAmount,\n                )\n''',
    '''                CharacterInventoryUsage(\n                    itemId = parsedId,\n                    kind = enumOrDefault(row.kind, CharacterConsumableKind.NONE),\n                    quickUseAmount = row.quickUseAmount,\n                    carryState = enumOrDefault(row.carryState, CharacterInventoryCarryState.CARRIED),\n                )\n''',
)
replace_once(
    'shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepository.kt',
    '''                database.characterClosureQueries.upsertInventoryUsage(\n                    character_id = id,\n                    item_id = item.itemId.toString(),\n                    consumable_kind = item.kind.name,\n                    quick_use_amount = item.quickUseAmount.toLong(),\n                )\n''',
    '''                database.characterClosureQueries.upsertInventoryUsage(\n                    character_id = id,\n                    item_id = item.itemId.toString(),\n                    consumable_kind = item.kind.name,\n                    quick_use_amount = item.quickUseAmount.toLong(),\n                    carry_state = item.carryState.name,\n                )\n''',
)
replace_once(
    'shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepository.kt',
    '''    private data class InventoryUsageRow(\n        val itemId: String,\n        val kind: String,\n        val quickUseAmount: Int,\n    )\n''',
    '''    private data class InventoryUsageRow(\n        val itemId: String,\n        val kind: String,\n        val quickUseAmount: Int,\n        val carryState: String,\n    )\n''',
)

# 4) Pure inventory presentation/operation foundation for F2 UI.
write_new(
    'shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterInventoryOperations.kt',
    r'''package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterInventoryFilterKey(val key: String) {
    CARRIED("carried"),
    STORED("stored"),
    EQUIPPED("equipped"),
    SPECIAL("special"),
    CONSUMABLE("consumable"),
    AMMUNITION("ammunition"),
    LOCATED("located"),
}

fun CharacterClosureState.inventoryUsageFor(itemId: Uuid): CharacterInventoryUsage =
    inventoryUsage.firstOrNull { it.itemId == itemId } ?: CharacterInventoryUsage(itemId)

fun CharacterClosureState.withInventoryUsage(usage: CharacterInventoryUsage): CharacterClosureState {
    require(usage.quickUseAmount > 0) { "Quick-use amount must be positive." }
    val remaining = inventoryUsage.filterNot { it.itemId == usage.itemId }
    val isDefault = usage.kind == CharacterConsumableKind.NONE &&
        usage.quickUseAmount == 1 &&
        usage.carryState == CharacterInventoryCarryState.CARRIED
    return copy(inventoryUsage = if (isDefault) remaining else remaining + usage)
}

fun effectiveInventoryCarryState(
    item: CharacterInventoryItem,
    usage: CharacterInventoryUsage,
): CharacterInventoryCarryState = if (item.equipped) {
    CharacterInventoryCarryState.CARRIED
} else {
    usage.carryState
}

fun presentCharacterInventorySection(
    items: List<CharacterInventoryItem>,
    special: Boolean,
    order: CharacterPresentationOrder,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage = { CharacterInventoryUsage(it.id) },
): List<CharacterInventoryItem> = presentCharacterCollection(
    items = items.filter { it.special == special },
    order = order,
    manualOrder = CharacterInventoryItem::sortOrder,
    label = CharacterInventoryItem::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { item -> listOf(item.name, item.location, item.notes, item.description) },
    filterMatches = { item, activeFilters -> inventoryFilterMatches(item, usageFor(item), activeFilters) },
)

fun inventoryFilterMatches(
    item: CharacterInventoryItem,
    usage: CharacterInventoryUsage,
    activeFilters: Set<String>,
): Boolean {
    if (activeFilters.isEmpty()) return true

    val active = CharacterInventoryFilterKey.entries.filter { it.key in activeFilters }.toSet()
    if (active.isEmpty()) return true

    val carryFilters = active.intersect(
        setOf(CharacterInventoryFilterKey.CARRIED, CharacterInventoryFilterKey.STORED),
    )
    if (carryFilters.isNotEmpty()) {
        val effectiveCarry = effectiveInventoryCarryState(item, usage)
        val carryMatches =
            (CharacterInventoryFilterKey.CARRIED in carryFilters && effectiveCarry == CharacterInventoryCarryState.CARRIED) ||
                (CharacterInventoryFilterKey.STORED in carryFilters && effectiveCarry == CharacterInventoryCarryState.STORED)
        if (!carryMatches) return false
    }

    val kindFilters = active.intersect(
        setOf(CharacterInventoryFilterKey.CONSUMABLE, CharacterInventoryFilterKey.AMMUNITION),
    )
    if (kindFilters.isNotEmpty()) {
        val kindMatches =
            (CharacterInventoryFilterKey.CONSUMABLE in kindFilters && usage.kind == CharacterConsumableKind.CONSUMABLE) ||
                (CharacterInventoryFilterKey.AMMUNITION in kindFilters && usage.kind == CharacterConsumableKind.AMMUNITION)
        if (!kindMatches) return false
    }

    if (CharacterInventoryFilterKey.EQUIPPED in active && !item.equipped) return false
    if (CharacterInventoryFilterKey.SPECIAL in active && !item.special) return false
    if (CharacterInventoryFilterKey.LOCATED in active && item.location.isNullOrBlank()) return false
    return true
}

fun carriedInventoryWeightLb(
    items: List<CharacterInventoryItem>,
    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage = { CharacterInventoryUsage(it.id) },
): Double = items
    .filter { effectiveInventoryCarryState(it, usageFor(it)) == CharacterInventoryCarryState.CARRIED }
    .sumOf(CharacterInventoryItem::carriedWeightLb)

fun consumeInventoryItem(
    items: List<CharacterInventoryItem>,
    usage: CharacterInventoryUsage,
): List<CharacterInventoryItem> {
    require(usage.quickUseAmount > 0) { "Quick-use amount must be positive." }
    if (usage.kind == CharacterConsumableKind.NONE) return items
    return items.map { item ->
        if (item.id == usage.itemId) {
            item.copy(quantity = (item.quantity - usage.quickUseAmount).coerceAtLeast(0))
        } else {
            item
        }
    }
}

fun duplicateInventoryItem(
    source: CharacterInventoryItem,
    newId: Uuid,
    sortOrder: Int,
): CharacterInventoryItem = source.copy(
    id = newId,
    name = source.name.trim().let { if (it.isEmpty()) "Copia" else "$it (copia)" },
    sortOrder = sortOrder,
)

fun duplicateInventoryUsage(
    source: CharacterInventoryUsage,
    newItemId: Uuid,
): CharacterInventoryUsage = source.copy(itemId = newItemId)
''',
)

# 5) Focused pure tests.
write_new(
    'shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterInventoryOperationsTest.kt',
    r'''package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterInventoryOperationsTest {
    private val swordId = Uuid.parse("00000000-0000-0000-0000-000000000101")
    private val arrowsId = Uuid.parse("00000000-0000-0000-0000-000000000102")
    private val chestId = Uuid.parse("00000000-0000-0000-0000-000000000103")

    private val items = listOf(
        CharacterInventoryItem(swordId, "Espada", 1, 3.0, true, null, 2, false, null, "Cinturón", false),
        CharacterInventoryItem(arrowsId, "Flechas", 20, 0.05, false, null, 0, false, null, "Carcaj", false),
        CharacterInventoryItem(chestId, "Cofre de viaje", 1, 10.0, false, null, 1, true, "Guardado en la posada", "Posada", false),
    )

    private val usageById = mapOf(
        arrowsId to CharacterInventoryUsage(
            arrowsId,
            CharacterConsumableKind.AMMUNITION,
            quickUseAmount = 2,
            carryState = CharacterInventoryCarryState.CARRIED,
        ),
        chestId to CharacterInventoryUsage(
            chestId,
            CharacterConsumableKind.NONE,
            carryState = CharacterInventoryCarryState.STORED,
        ),
    )

    private fun usage(item: CharacterInventoryItem): CharacterInventoryUsage =
        usageById[item.id] ?: CharacterInventoryUsage(item.id)

    @Test
    fun ordinaryAndSpecialOrderingAreIndependentPresentationOnly() {
        val manual = presentCharacterInventorySection(
            items,
            special = false,
            order = CharacterPresentationOrder.MANUAL,
            usageFor = ::usage,
        )
        val alphabetical = presentCharacterInventorySection(
            items,
            special = false,
            order = CharacterPresentationOrder.ALPHABETICAL,
            usageFor = ::usage,
        )
        val special = presentCharacterInventorySection(
            items,
            special = true,
            order = CharacterPresentationOrder.ALPHABETICAL,
            usageFor = ::usage,
        )

        assertEquals(listOf("Flechas", "Espada"), manual.map { it.name })
        assertEquals(listOf("Espada", "Flechas"), alphabetical.map { it.name })
        assertEquals(listOf("Cofre de viaje"), special.map { it.name })
        assertEquals(listOf(2, 0, 1), items.map { it.sortOrder })
    }

    @Test
    fun searchAndGroupedFiltersDoNotChangeStoredOrder() {
        val stored = presentCharacterInventorySection(
            items,
            special = true,
            order = CharacterPresentationOrder.MANUAL,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(CharacterInventoryFilterKey.STORED.key)),
            usageFor = ::usage,
        )
        val ammo = presentCharacterInventorySection(
            items,
            special = false,
            order = CharacterPresentationOrder.MANUAL,
            query = CharacterCollectionQuery(
                searchText = "carcaj",
                activeFilterKeys = setOf(CharacterInventoryFilterKey.AMMUNITION.key),
            ),
            usageFor = ::usage,
        )

        assertEquals(listOf("Cofre de viaje"), stored.map { it.name })
        assertEquals(listOf("Flechas"), ammo.map { it.name })
        assertEquals(listOf(2, 0, 1), items.map { it.sortOrder })
    }

    @Test
    fun equippedItemIsEffectivelyCarriedAndStoredItemsDoNotAddCarriedWeight() {
        val contradictorySword = CharacterInventoryUsage(swordId, carryState = CharacterInventoryCarryState.STORED)
        val weight = carriedInventoryWeightLb(items) { item ->
            if (item.id == swordId) contradictorySword else usage(item)
        }

        assertEquals(CharacterInventoryCarryState.CARRIED, effectiveInventoryCarryState(items[0], contradictorySword))
        assertEquals(4.0, weight, absoluteTolerance = 0.000001)
    }

    @Test
    fun quickUseIsBoundedAndNonConsumablesRemainUnchanged() {
        val afterArrows = consumeInventoryItem(items, usageById.getValue(arrowsId))
        val depleted = consumeInventoryItem(
            afterArrows,
            CharacterInventoryUsage(arrowsId, CharacterConsumableKind.AMMUNITION, quickUseAmount = 50),
        )
        val unchanged = consumeInventoryItem(items, CharacterInventoryUsage(swordId))

        assertEquals(18, afterArrows.first { it.id == arrowsId }.quantity)
        assertEquals(0, depleted.first { it.id == arrowsId }.quantity)
        assertEquals(items, unchanged)
    }

    @Test
    fun defaultUsageIsSparseAndDuplicateKeepsMetadataForNewIdentity() {
        val defaultState = CharacterClosureState().withInventoryUsage(CharacterInventoryUsage(swordId))
        assertTrue(defaultState.inventoryUsage.isEmpty())

        val duplicateId = Uuid.parse("00000000-0000-0000-0000-000000000199")
        val duplicate = duplicateInventoryItem(items[1], duplicateId, sortOrder = 3)
        val duplicateUsage = duplicateInventoryUsage(usageById.getValue(arrowsId), duplicateId)

        assertEquals("Flechas (copia)", duplicate.name)
        assertEquals(3, duplicate.sortOrder)
        assertEquals(duplicateId, duplicateUsage.itemId)
        assertEquals(CharacterConsumableKind.AMMUNITION, duplicateUsage.kind)
        assertEquals(CharacterInventoryCarryState.CARRIED, duplicateUsage.carryState)
    }
}
''',
)

# 6) Repository round-trip and real 7 -> 8 migration regression.
replace_once(
    'shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepositoryTest.kt',
    '''                        CharacterInventoryUsage(itemId, CharacterConsumableKind.AMMUNITION, 1),\n''',
    '''                        CharacterInventoryUsage(\n                            itemId,\n                            CharacterConsumableKind.AMMUNITION,\n                            1,\n                            CharacterInventoryCarryState.STORED,\n                        ),\n''',
)
replace_once(
    'shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepositoryTest.kt',
    '''            assertEquals(CharacterConsumableKind.AMMUNITION, saved.inventoryUsage.single().kind)\n''',
    '''            assertEquals(CharacterConsumableKind.AMMUNITION, saved.inventoryUsage.single().kind)\n            assertEquals(CharacterInventoryCarryState.STORED, saved.inventoryUsage.single().carryState)\n''',
)

repo_test = ROOT / 'shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepositoryTest.kt'
text = repo_test.read_text()
start_marker = '''    @Test\n    fun migrationSevenIsAdditiveAndPreservesExistingCharacterRows() {\n'''
end_marker = '''    private fun withRepositories(\n'''
start = text.find(start_marker)
end = text.find(end_marker, start)
if start < 0 or end < 0:
    raise SystemExit('CharacterClosureRepositoryTest.kt: migration function markers not found')
replacement = r'''    @Test
    fun migrationEightAddsCarriedStateWithoutLosingExistingInventoryUsage() {
        val file = File.createTempFile("dnd-custom-aid-schema8", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000077"
        val itemId = "00000000-0000-0000-0000-000000000078"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("PRAGMA foreign_keys=ON")
                    statement.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate("INSERT INTO character(id, name) VALUES ('$characterId', 'Conservado')")
                    statement.executeUpdate(
                        """CREATE TABLE character_inventory_usage (
                            character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                            item_id TEXT NOT NULL,
                            consumable_kind TEXT NOT NULL DEFAULT 'NONE',
                            quick_use_amount INTEGER NOT NULL DEFAULT 1,
                            PRIMARY KEY(character_id, item_id)
                        )""".trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO character_inventory_usage(character_id, item_id, consumable_kind, quick_use_amount) " +
                            "VALUES ('$characterId', '$itemId', 'AMMUNITION', 2)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 7,
                newVersion = AppDatabase.Schema.version,
            )
            driver.close()

            DriverManager.getConnection(jdbcUrl).use { connection ->
                val row = connection.createStatement().use { statement ->
                    statement.executeQuery(
                        "SELECT consumable_kind, quick_use_amount, carry_state FROM character_inventory_usage WHERE item_id = '$itemId'",
                    ).use { result ->
                        result.next()
                        Triple(result.getString(1), result.getInt(2), result.getString(3))
                    }
                }
                val preservedName = connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT name FROM character WHERE id = '$characterId'").use { result ->
                        result.next()
                        result.getString(1)
                    }
                }

                assertEquals("Conservado", preservedName)
                assertEquals("AMMUNITION", row.first)
                assertEquals(2, row.second)
                assertEquals("CARRIED", row.third)
            }
        } finally {
            file.delete()
        }
    }

'''
repo_test.write_text(text[:start] + replacement + text[end:])

print('Batch F1 guarded patch applied successfully.')
