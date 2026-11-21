package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.stableContentId
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/**
 * The SRD monsters as ready-to-use creatures (QA 0.7.0-preqa.2, C1 and D6: «no puedo agregar
 * criaturas, no hay»). Made from the official PDFs by scripts/build_srd_monsters.py.
 */
@Serializable
data class SrdMonster(val edition: String, val name: String, val page: Int = 0, val payload: CreaturePayload) {
    val source: String get() = "SRD ${if (edition == "5e") "5.1" else "5.2.1"}, p. $page"
}

@Serializable
data class SrdMonsters(val format: String, val formatVersion: Int = 1, val monsters: List<SrdMonster> = emptyList())

const val SRD_MONSTERS_FILE = "srd_monsters_es.json"

private val monstersJson = Json { ignoreUnknownKeys = true }

fun parseSrdMonsters(text: String): List<SrdMonster> = monstersJson.decodeFromString(SrdMonsters.serializer(), text).monsters

/** Monsters whose name or challenge rating matches, the campaign's edition first. */
fun searchSrdMonsters(monsters: List<SrdMonster>, query: String, preferredEdition: String = "5.5e", limit: Int = 60): List<SrdMonster> {
    val needle = spanishSortKey(query.trim())
    return monsters
        .filter { needle.isEmpty() || spanishSortKey(it.name).contains(needle) || it.payload.challengeRating == query.trim() }
        .sortedWith(compareBy<SrdMonster>({ it.edition != preferredEdition }, { spanishSortKey(it.name) }))
        .take(limit)
}

/**
 * The campaign's own copy of an SRD monster: created the first time it is used and reused after,
 * with the same id on every device, so it syncs like any creature and the DM can adjust it.
 */
fun adoptSrdMonster(database: AppDatabase, campaignId: Uuid, monster: SrdMonster, nowEpochSeconds: Long): CreatureContent {
    val reusable = ReusableContentRepository(database)
    val creatures = CreatureContentRepository(database, reusable)
    val id = stableContentId(campaignId, "srd-monster:${monster.edition}:${monster.name}")
    creatures.creature(id)?.let { return it }
    val payload = monster.payload.copy(notes = listOf(monster.payload.notes, monster.source).filter { it.isNotBlank() }.joinToString(" · "))
    val name = if (monster.edition == "5e") "${monster.name} (5e)" else monster.name
    return creatures.createCampaign(campaignId, name, payload, nowEpochSeconds, id)
}
