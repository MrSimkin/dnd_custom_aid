package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/*
 * One search for the whole campaign (owner, 2026-11-18, «K»): «resultados resumidos con origen y
 * una forma de filtrar lo que es buscable o no, por ejemplo "excluir reglas"».
 *
 * Every result says what it is, one line of what it says, and where it lives, so the DM never has
 * to guess which screen a name came from. What is searched can be narrowed, because a campaign
 * with 770 house rules drowns everything else otherwise.
 */

data class CampaignSearchResult(
    val id: Uuid,
    val family: ReusableContentFamily,
    val name: String,
    /** One line of what it says. */
    val summary: String,
    /** Where it lives: the place or the scene that holds it, when it has one. */
    val origin: String,
)

/** The families the DM can switch off. The rules are off by default: there are hundreds. */
val SEARCHABLE_FAMILIES: List<ReusableContentFamily> = listOf(
    ReusableContentFamily.NPC,
    ReusableContentFamily.PLACE,
    ReusableContentFamily.SCENE,
    ReusableContentFamily.CREATURE,
    ReusableContentFamily.ENCOUNTER,
    ReusableContentFamily.ZONE,
    ReusableContentFamily.DUNGEON,
    ReusableContentFamily.HANDOUT,
    ReusableContentFamily.NOTE,
    ReusableContentFamily.HOMEBREW_RULE,
)

val DEFAULT_SEARCH_FAMILIES: Set<ReusableContentFamily> =
    (SEARCHABLE_FAMILIES - ReusableContentFamily.HOMEBREW_RULE).toSet()

fun spanishFamilyLabel(family: ReusableContentFamily): String = when (family) {
    ReusableContentFamily.NPC -> "PNJ"
    ReusableContentFamily.PLACE -> "Lugar"
    ReusableContentFamily.SCENE -> "Escena"
    ReusableContentFamily.CREATURE -> "Criatura"
    ReusableContentFamily.ENCOUNTER -> "Encuentro"
    ReusableContentFamily.ZONE -> "Zona"
    ReusableContentFamily.DUNGEON -> "Dungeon"
    ReusableContentFamily.HANDOUT -> "Handout"
    ReusableContentFamily.NOTE -> "Nota"
    ReusableContentFamily.PACKAGE -> "Paquete"
    ReusableContentFamily.HOMEBREW_RULE -> "Regla de la casa"
    ReusableContentFamily.RULES_ENTRY -> "Regla"
}

class CampaignSearch(
    private val database: AppDatabase,
    private val content: ReusableContentRepository = ReusableContentRepository(database),
    private val links: ContentLinksRepository = ContentLinksRepository(database),
) {
    private val npcs = NpcContentRepository(database, content)
    private val places = PlaceContentRepository(database, content)
    private val scenes = SceneContentRepository(database, content)
    private val creatures = CreatureContentRepository(database, content)
    private val encounters = EncounterContentRepository(database, content)
    private val notes = NoteContentRepository(database, content)
    private val rules = HomebrewRuleContentRepository(database, content)

    /**
     * What matches [query] in this campaign. A result matches by name or by what it says, and
     * the list keeps the families in the order a DM thinks of them.
     */
    fun search(
        campaignId: Uuid,
        query: String,
        families: Set<ReusableContentFamily> = DEFAULT_SEARCH_FAMILIES,
        limit: Int = 60,
    ): List<CampaignSearchResult> {
        val needle = query.trim().lowercase()
        if (needle.length < 2) return emptyList()

        val names = content.listCampaign(campaignId).associate { it.identity.id to it.displayName }
        // Read once: with two thousand objects, reading them per result turns the search quadratic.
        val allLinks = links.all()
        val results = mutableListOf<CampaignSearchResult>()

        SEARCHABLE_FAMILIES.filter { it in families }.forEach { family ->
            content.listCampaign(campaignId, family).forEach { item ->
                val summary = summaryOf(family, item.identity.id)
                val matches = item.displayName.lowercase().contains(needle) || summary.lowercase().contains(needle)
                if (!matches) return@forEach
                results += CampaignSearchResult(
                    id = item.identity.id,
                    family = family,
                    name = item.displayName,
                    summary = summary.take(140),
                    origin = originOf(item.identity.id, names, allLinks),
                )
            }
        }
        return results.take(limit)
    }

    /** One line of what the object says, in its own words. */
    private fun summaryOf(family: ReusableContentFamily, id: Uuid): String = when (family) {
        ReusableContentFamily.NPC -> npcs.npc(id)?.payload?.let { payload ->
            listOf(payload.conceptRole, payload.wantsFearsNeeds, payload.secrets, payload.notes)
                .firstOrNull { it.isNotBlank() }.orEmpty()
        }.orEmpty()
        ReusableContentFamily.PLACE -> places.place(id)?.payload?.let { payload ->
            listOf(payload.summary, payload.presentation, payload.function).firstOrNull { it.isNotBlank() }.orEmpty()
        }.orEmpty()
        ReusableContentFamily.SCENE -> scenes.scene(id)?.payload?.let { payload ->
            payload.purpose.ifBlank { payload.sections.values.firstOrNull { it.isNotBlank() }.orEmpty() }
        }.orEmpty()
        ReusableContentFamily.CREATURE -> creatures.creature(id)?.payload?.let { payload ->
            listOfNotNull(
                payload.armorClass?.let { "CA $it" },
                payload.hitPoints?.let { "PV $it" },
                payload.challengeRating?.takeIf { it.isNotBlank() }?.let { "VD $it" },
            ).joinToString(" · ")
        }.orEmpty()
        ReusableContentFamily.ENCOUNTER -> encounters.encounter(id)?.payload?.let { payload ->
            payload.dmGuidance.ifBlank { "${payload.participants.sumOf { it.quantity }} criatura(s)" }
        }.orEmpty()
        ReusableContentFamily.NOTE -> notes.note(id)?.payload?.text.orEmpty()
        ReusableContentFamily.HOMEBREW_RULE -> rules.rule(id)?.payload?.summary.orEmpty()
        else -> ""
    }

    /** Where it lives, taken from what it is linked to. */
    private fun originOf(id: Uuid, names: Map<Uuid, String>, allLinks: Map<Uuid, ContentLinks>): String {
        val own = allLinks[id] ?: ContentLinks()
        val parents = buildList {
            own.placeIds.forEach { names[it]?.let(::add) }
            own.sceneIds.forEach { names[it]?.let(::add) }
            own.zoneIds.forEach { names[it]?.let(::add) }
            if (own.dungeon.isNotBlank()) add(own.dungeon)
            if (own.adventure.isNotBlank()) add(own.adventure)
            addAll(own.groups)
        }
        if (parents.isNotEmpty()) return parents.distinct().take(2).joinToString(" · ")

        // Nothing of its own: whoever mentions it is where it lives.
        val mentioned = allLinks.entries.firstOrNull { (owner, value) ->
            owner != id && (id in value.npcIds || id in value.placeIds || id in value.sceneIds ||
                id in value.zoneIds || id in value.encounterIds)
        }
        return mentioned?.key?.let { names[it] }.orEmpty()
    }
}
