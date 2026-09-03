package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal data class CharacterEquipmentDraftV4(
    val items: List<CharacterInventoryItem>,
    val currencies: List<CharacterCurrency>,
    val inventoryUsage: List<CharacterInventoryUsage> = emptyList(),
)

internal fun equipmentDraftToJsonV4(draft: CharacterEquipmentDraftV4): String = JSONObject().apply {
    put("items", JSONArray().apply {
        draft.items.forEachIndexed { index, item ->
            put(JSONObject().apply {
                put("id", item.id.toString())
                put("name", item.name)
                put("quantity", item.quantity)
                put("weightLb", item.weightLb ?: JSONObject.NULL)
                put("equipped", item.equipped)
                put("notes", item.notes ?: JSONObject.NULL)
                put("sortOrder", index)
                put("special", item.special)
                put("description", item.description ?: JSONObject.NULL)
                put("location", item.location ?: JSONObject.NULL)
                put("attuned", item.attuned)
            })
        }
    })
    put("currencies", JSONArray().apply {
        draft.currencies.forEachIndexed { index, currency ->
            put(JSONObject().apply {
                put("key", currency.key)
                put("name", currency.name)
                put("amount", currency.amount)
                put("sortOrder", index)
                put("isDefault", currency.isDefault)
            })
        }
    })
    put("inventoryUsage", JSONArray().apply {
        draft.inventoryUsage.forEach { usage ->
            put(JSONObject().apply {
                put("itemId", usage.itemId.toString())
                put("kind", usage.kind.name)
                put("quickUseAmount", usage.quickUseAmount)
                put("carryState", usage.carryState.name)
            })
        }
    })
}.toString()

internal fun equipmentDraftFromJsonV4(raw: String): CharacterEquipmentDraftV4 = runCatching {
    val json = JSONObject(raw)
    val itemsJson = json.getJSONArray("items")
    val items = buildList {
        for (index in 0 until itemsJson.length()) {
            val item = itemsJson.getJSONObject(index)
            add(
                CharacterInventoryItem(
                    id = Uuid.parse(item.getString("id")),
                    name = item.getString("name"),
                    quantity = item.getInt("quantity"),
                    weightLb = if (item.isNull("weightLb")) null else item.getDouble("weightLb"),
                    equipped = item.getBoolean("equipped"),
                    notes = if (item.isNull("notes")) null else item.getString("notes"),
                    sortOrder = index,
                    special = item.getBoolean("special"),
                    description = if (item.isNull("description")) null else item.getString("description"),
                    location = if (item.isNull("location")) null else item.getString("location"),
                    attuned = item.getBoolean("attuned"),
                ),
            )
        }
    }
    val currenciesJson = json.getJSONArray("currencies")
    val currencies = buildList {
        for (index in 0 until currenciesJson.length()) {
            val currency = currenciesJson.getJSONObject(index)
            add(
                CharacterCurrency(
                    key = currency.getString("key"),
                    name = currency.getString("name"),
                    amount = currency.getInt("amount"),
                    sortOrder = index,
                    isDefault = currency.getBoolean("isDefault"),
                ),
            )
        }
    }
    val usageJson = json.optJSONArray("inventoryUsage")
    val inventoryUsage = buildList {
        if (usageJson != null) {
            for (index in 0 until usageJson.length()) {
                val usage = usageJson.getJSONObject(index)
                val kind = runCatching {
                    CharacterConsumableKind.valueOf(usage.optString("kind", CharacterConsumableKind.NONE.name))
                }.getOrDefault(CharacterConsumableKind.NONE)
                val carryState = runCatching {
                    CharacterInventoryCarryState.valueOf(
                        usage.optString("carryState", CharacterInventoryCarryState.CARRIED.name),
                    )
                }.getOrDefault(CharacterInventoryCarryState.CARRIED)
                add(
                    CharacterInventoryUsage(
                        itemId = Uuid.parse(usage.getString("itemId")),
                        kind = kind,
                        quickUseAmount = usage.optInt("quickUseAmount", 1).coerceAtLeast(1),
                        carryState = carryState,
                    ),
                )
            }
        }
    }
    CharacterEquipmentDraftV4(
        items = items,
        currencies = currencies,
        inventoryUsage = inventoryUsage,
    )
}.getOrElse { CharacterEquipmentDraftV4(emptyList(), emptyList(), emptyList()) }
