package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContent
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipantReadiness
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/*
 * Owner observation E1 on 0.7.0-preqa.4: «La seleccion de encuentros preparados no puede ser una
 * ventana poptup, debe haber una ventana intermedia selector, para poder improvisar y modificar una,
 * o crear un encuentro nuevo».
 *
 * An encounter as the DM sees it in the «Elegir encuentro» screen before it enters a combat: taken
 * from a prepared encounter, changed, or made up. Using it in a combat never changes the saved one;
 * saving is a separate, explicit choice.
 */

/** One line of the draft: a creature of the bestiary, or an improvised one with its own CA and PV. */
data class EncounterDraftEntry(
    val key: String = Uuid.random().toString(),
    val creatureId: Uuid? = null,
    val label: String = "",
    val quantity: Int = 1,
    val reserve: Boolean = false,
    val armorClass: Int? = null,
    val hitPoints: Int? = null,
) {
    init {
        require(quantity in 1..50) { "Quantity must be between 1 and 50." }
        require(creatureId != null || label.isNotBlank()) { "An improvised line needs a name." }
    }

    val improvised: Boolean get() = creatureId == null
}

data class EncounterDraft(
    /** The saved encounter this draft started from, or null for a new one. */
    val baseId: Uuid? = null,
    val name: String = "",
    val entries: List<EncounterDraftEntry> = emptyList(),
) {
    val creatureCount: Int get() = entries.sumOf { it.quantity }

    fun withQuantity(key: String, quantity: Int): EncounterDraft =
        copy(entries = entries.map { if (it.key == key) it.copy(quantity = quantity.coerceIn(1, 50)) else it })

    fun without(key: String): EncounterDraft = copy(entries = entries.filterNot { it.key == key })

    fun withReserve(key: String, reserve: Boolean): EncounterDraft =
        copy(entries = entries.map { if (it.key == key) it.copy(reserve = reserve) else it })

    /** The same creature again adds to its line instead of making a second one. */
    fun plusCreature(creature: CreatureContent, quantity: Int = 1): EncounterDraft {
        val id = creature.item.identity.id
        val existing = entries.firstOrNull { it.creatureId == id && !it.reserve }
        return if (existing != null) withQuantity(existing.key, existing.quantity + quantity)
        else copy(entries = entries + EncounterDraftEntry(creatureId = id, quantity = quantity.coerceIn(1, 50)))
    }

    fun plusImprovised(label: String, armorClass: Int?, hitPoints: Int?, quantity: Int = 1): EncounterDraft =
        copy(entries = entries + EncounterDraftEntry(label = label.trim(), quantity = quantity.coerceIn(1, 50), armorClass = armorClass, hitPoints = hitPoints))

    /** True when the draft no longer matches the saved encounter it came from (or is new and not empty). */
    fun differsFrom(encounter: EncounterContent?): Boolean {
        if (encounter == null) return entries.isNotEmpty()
        if (name.trim() != encounter.item.displayName) return true
        return comparable(entries) != comparable(fromEncounter(encounter).entries)
    }

    /** The draft enters the combat: quantities expand and reserve lines stay in reserve. */
    fun applyTo(doc: CombatDocument, creatures: Map<Uuid, CreatureContent>): CombatDocument =
        entries.fold(doc.copy(encounterId = doc.encounterId ?: baseId)) { acc, entry ->
            val creature = entry.creatureId?.let(creatures::get)
            val label = entry.label.ifBlank { null }
            when {
                creature != null -> CombatEngine.addCreature(acc, creature, entry.quantity, entry.reserve, label)
                entry.label.isNotBlank() -> {
                    val withCustom = CombatEngine.addCustom(acc, entry.label, entry.hitPoints, entry.armorClass, entry.quantity)
                    if (!entry.reserve) withCustom else {
                        val newIds = withCustom.combatants.drop(acc.combatants.size).map { it.id }.toSet()
                        withCustom.copy(combatants = withCustom.combatants.map { c -> if (c.id in newIds) c.copy(reserve = true) else c })
                    }
                }
                else -> acc
            }
        }

    companion object {
        fun fromEncounter(encounter: EncounterContent): EncounterDraft = EncounterDraft(
            baseId = encounter.item.identity.id,
            name = encounter.item.displayName,
            entries = encounter.payload.participants.map { p ->
                EncounterDraftEntry(
                    creatureId = p.sourceContentId,
                    label = p.label,
                    quantity = p.quantity,
                    reserve = p.readiness != EncounterParticipantReadiness.EXPECTED,
                )
            },
        )

        private fun comparable(entries: List<EncounterDraftEntry>) =
            entries.map { listOf(it.creatureId, it.label.trim(), it.quantity, it.reserve, it.armorClass, it.hitPoints) }
    }
}

/**
 * Saves the draft in the campaign: as changes to the encounter it came from ([asNew] false and a
 * base exists) or as a new encounter. An improvised line with CA or PV becomes a creature of the
 * campaign's bestiary first, so the saved encounter keeps those numbers.
 */
fun saveEncounterDraft(
    database: AppDatabase,
    campaignId: Uuid,
    draft: EncounterDraft,
    asNew: Boolean,
    nowEpochSeconds: Long,
): EncounterContent {
    val name = draft.name.trim()
    require(name.isNotEmpty()) { "Ponle un nombre al encuentro." }
    val creatures = CreatureContentRepository(database)
    val encounters = EncounterContentRepository(database)
    var result: EncounterContent? = null
    database.transaction {
        val participants = draft.entries.map { entry ->
            val creatureId = entry.creatureId ?: if (entry.armorClass != null || entry.hitPoints != null) {
                creatures.createCampaign(
                    campaignId,
                    entry.label,
                    CreaturePayload(armorClass = entry.armorClass, hitPoints = entry.hitPoints),
                    nowEpochSeconds,
                ).item.identity.id
            } else null
            EncounterParticipant(
                sourceContentId = creatureId,
                label = if (creatureId == null) entry.label else "",
                quantity = entry.quantity,
                readiness = if (entry.reserve) EncounterParticipantReadiness.RESERVE else EncounterParticipantReadiness.EXPECTED,
            )
        }
        val base = draft.baseId?.takeUnless { asNew }?.let { encounters.encounter(it) }
        result = if (base == null) {
            val template = draft.baseId?.let { encounters.encounter(it) }?.payload ?: EncounterPayload()
            encounters.createCampaign(campaignId, name, template.copy(participants = participants), nowEpochSeconds)
        } else {
            val decision = encounters.update(
                id = base.item.identity.id,
                expectedRevision = base.item.identity.revision,
                rawDisplayName = name,
                payload = base.payload.copy(participants = participants),
                updatedAtEpochSeconds = nowEpochSeconds,
            )
            check(decision is io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision.Accepted) {
                "El encuentro cambió en otro aparato. Vuelve a abrirlo e inténtalo otra vez."
            }
            checkNotNull(encounters.encounter(base.item.identity.id))
        }
    }
    return checkNotNull(result)
}
