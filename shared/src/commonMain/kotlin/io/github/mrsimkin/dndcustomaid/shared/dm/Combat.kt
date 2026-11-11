package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterD20Roll
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContent
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipantReadiness
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContent
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.uuid.Uuid

/*
 * Base para DM, package 5: Mesa de Combate (owner decisions P11-P14, P19; D-0068 §4 and §13).
 *
 * One combat document per combat, kept on the DM's device (live table state, local-first). A
 * campaign may hold several combats. Life cycle: PREPARING («Preparar encuentro») → ACTIVE
 * («En curso») → ENDED. Starting a combat keeps everything prepared (P19).
 *
 * The App assists and never adjudicates: no balance checks, no complaints about numbers that do
 * not match the rules, nothing applied automatically. Base creatures are never edited here.
 */

@Serializable
enum class CombatStatus { PREPARING, ACTIVE, ENDED }

@Serializable
enum class CombatantKind { PC, CREATURE, CUSTOM }

/** Quick temporary tweaks (P19): each one, when set, replaces the computed value. */
@Serializable
data class CombatOverrides(
    val armorClass: Int? = null,
    val maxHp: Int? = null,
    val attackBonus: Int? = null,
    val damageDice: String? = null,
    val damageBonus: Int? = null,
) {
    val isEmpty: Boolean
        get() = armorClass == null && maxHp == null && attackBonus == null && damageDice == null && damageBonus == null
}

/** A structured attack the DM gave this combatant in the full editor (P32). */
@Serializable
data class CombatAttack(
    val name: String,
    val bonus: Int? = null,
    val damage: String = "",
    val damageType: String = "",
    val reach: String = "",
    val notes: String = "",
)

/**
 * The combatant's own version of its stat block (P32 «Editar completa»). A null field means «as in
 * the bestiary». Nothing here touches the base creature; «Guardar como criatura nueva» is the only
 * way a variant leaves the combat. Changing an ability score never recalculates AC or attacks.
 */
@Serializable
data class CreatureEdits(
    val abilityScores: CreatureAbilityScores? = null,
    val speed: String? = null,
    val armorClassDetails: String? = null,
    val defenses: String? = null,
    val attacks: List<CombatAttack> = emptyList(),
    val traits: String? = null,
    val actions: String? = null,
    val spells: String? = null,
) {
    val isEmpty: Boolean
        get() = this == CreatureEdits()
}

@Serializable
data class Combatant(
    val id: Uuid,
    val kind: CombatantKind,
    val label: String,
    /** Copies of the same stat block share a group, so a patch can go to the whole group. */
    val groupId: Uuid,
    val sourceContentId: Uuid? = null,
    val characterId: Uuid? = null,
    val baseArmorClass: Int? = null,
    val baseMaxHp: Int? = null,
    val hitDice: String = "",
    val dexterityModifier: Int = 0,
    val overrides: CombatOverrides = CombatOverrides(),
    val packageIds: List<Uuid> = emptyList(),
    /** Creature HP tracked by the App; null for PCs (their HP is on paper, P11). */
    val currentHp: Int? = null,
    /** Optional PC HP note for reference (P11); never required, never touches the PC sheet. */
    val pcHpNote: String = "",
    val initiative: Int? = null,
    val conditions: List<String> = emptyList(),
    val concentrating: Boolean = false,
    val defeated: Boolean = false,
    val reserve: Boolean = false,
    val notes: String = "",
    val edits: CreatureEdits = CreatureEdits(),
    /** The d20 behind [initiative] when the App rolled it; null when it was written. */
    val initiativeDie: Int? = null,
) {
    init {
        require(label.isNotBlank()) { "Combatant name must not be blank." }
    }

    /** Has its own tweaks, packages or editor changes (the ● of P32). */
    val hasOwnChanges: Boolean
        get() = !overrides.isEmpty || packageIds.isNotEmpty() || !edits.isEmpty
}

@Serializable
data class CombatDocument(
    val format: String = "dnd-custom-aid.dm-combat",
    val version: Int = 1,
    val id: Uuid,
    val campaignId: Uuid,
    val name: String,
    val status: CombatStatus = CombatStatus.PREPARING,
    val round: Int = 0,
    val activeCombatantId: Uuid? = null,
    val combatants: List<Combatant> = emptyList(),
    val createdAtEpochSeconds: Long,
    val updatedAtEpochSeconds: Long,
    /** Groups that act together on one initiative (P33 «Una para el grupo»). */
    val sharedInitiativeGroups: List<Uuid> = emptyList(),
    /** The prepared encounter this combat came from, for its guidance (D-0069 §10). */
    val encounterId: Uuid? = null,
    /**
     * P22: after a successful ambush, these combatants act in a surprise round (round 0) before
     * round 1; everyone else is surprised. Empty means no surprise round.
     */
    val surpriseActorIds: List<Uuid> = emptyList(),
) {
    init {
        require(name.isNotBlank()) { "Combat name must not be blank." }
        require(combatants.map { it.id }.distinct().size == combatants.size) { "Combatant ids must be unique." }
    }

    fun combatant(id: Uuid): Combatant? = combatants.firstOrNull { it.id == id }

    /** Combatants that take turns: not in reserve. Highest initiative first; ties by DEX, then name. */
    val turnOrder: List<Combatant>
        get() = combatants.filter { !it.reserve }.sortedWith(
            compareByDescending<Combatant> { it.initiative ?: Int.MIN_VALUE }
                .thenByDescending { it.dexterityModifier }
                .thenBy { it.groupId.toString() }
                .thenBy { it.label.lowercase() }
                .thenBy { it.id.toString() },
        )

    val reserve: List<Combatant>
        get() = combatants.filter { it.reserve }

    fun sharesInitiative(groupId: Uuid): Boolean = groupId in sharedInitiativeGroups

    /** Turns in order: a group with one shared initiative is a single turn (P33). */
    val turnSlots: List<List<Combatant>>
        get() {
            val slots = mutableListOf<List<Combatant>>()
            val seen = mutableSetOf<Uuid>()
            val order = turnOrder
            order.forEach { c ->
                if (sharesInitiative(c.groupId)) {
                    if (seen.add(c.groupId)) slots += order.filter { it.groupId == c.groupId }
                } else {
                    slots += listOf(c)
                }
            }
            return slots
        }

    /** Everyone acting now: the active combatant, or its whole group when the group shares a turn. */
    val actingIds: Set<Uuid>
        get() = turnSlots.firstOrNull { slot -> slot.any { it.id == activeCombatantId } }?.map { it.id }?.toSet().orEmpty()
}

/** Values the DM sees for a combatant: base + packages + overrides (an override always wins). */
data class EffectiveCombatant(
    val combatant: Combatant,
    val armorClass: Int?,
    val maxHp: Int?,
    val attackBonusDelta: Int,
    val attackBonusOverride: Int?,
    val damageDice: String?,
    val damageBonusDelta: Int,
    val damageBonusOverride: Int?,
    val packages: List<PackageContent>,
) {
    val addedPieces = packages.flatMap { it.payload.pieces }
    val removals = packages.flatMap { it.payload.removals }
}

fun Combatant.effective(packagesById: Map<Uuid, PackageContent>): EffectiveCombatant {
    val applied = packageIds.mapNotNull { packagesById[it] }
    fun sum(selector: (PackagePayload) -> Int) = applied.sumOf { selector(it.payload) }
    return EffectiveCombatant(
        combatant = this,
        armorClass = overrides.armorClass ?: baseArmorClass?.let { it + sum { p -> p.armorClassDelta } },
        maxHp = overrides.maxHp ?: baseMaxHp?.let { maxOf(1, it + sum { p -> p.hpDelta }) },
        attackBonusDelta = sum { it.attackBonusDelta },
        attackBonusOverride = overrides.attackBonus,
        damageDice = overrides.damageDice,
        damageBonusDelta = sum { it.damageBonusDelta },
        damageBonusOverride = overrides.damageBonus,
        packages = applied,
    )
}

/** The fifteen SRD conditions, in the Spanish SRD's alphabetical order. */
val COMBAT_CONDITIONS: List<String> = listOf(
    "Agarrado", "Apresado", "Asustado", "Aturdido", "Cansancio", "Cegado", "Derribado", "Ensordecido",
    "Envenenado", "Hechizado", "Incapacitado", "Inconsciente", "Invisible", "Paralizado", "Petrificado",
)

/** Dice such as «2d6+3», «1d8 - 1», «4d10». */
data class DiceExpression(val count: Int, val sides: Int, val bonus: Int) {
    val average: Int
        get() = maxOf(1, (count * (sides + 1)) / 2 + bonus)

    fun roll(random: Random): Int = maxOf(1, (1..count).sumOf { random.nextInt(1, sides + 1) } + bonus)

    companion object {
        private val pattern = Regex("""^\s*(\d{1,3})\s*[dD]\s*(\d{1,3})\s*(?:([+\-−])\s*(\d{1,4}))?\s*$""")

        fun parse(text: String): DiceExpression? {
            // Stat blocks often write «22 (4d8 + 4)»; use the part in parentheses when present.
            val inner = Regex("""\(([^)]*)\)""").find(text)?.groupValues?.get(1) ?: text
            val match = pattern.matchEntire(inner) ?: return null
            val (count, sides, sign, bonus) = match.destructured
            val n = count.toInt()
            val s = sides.toInt()
            if (n < 1 || s < 2) return null
            val b = bonus.toIntOrNull() ?: 0
            return DiceExpression(n, s, if (sign == "-" || sign == "−") -b else b)
        }
    }
}

private fun dexModifier(score: Int): Int = (score - 10).floorDiv(2)

/** «Garrote. +4 al impactar, 1,5 m. 1d6+2 contundente.» */
fun CombatAttack.describe(): String = buildString {
    append(name.trim().ifBlank { "Ataque" })
    val parts = listOfNotNull(
        bonus?.let { if (it >= 0) "+$it al impactar" else "$it al impactar" },
        reach.takeIf { it.isNotBlank() },
        listOf(damage, damageType).filter { it.isNotBlank() }.joinToString(" ").takeIf { it.isNotBlank() },
        notes.takeIf { it.isNotBlank() },
    )
    if (parts.isNotEmpty()) append(". ").append(parts.joinToString(", ")).append(".")
}

/** Pure operations on a combat document; every call returns a new document. */
object CombatEngine {
    fun new(campaignId: Uuid, name: String, nowEpochSeconds: Long, id: Uuid = Uuid.random()) =
        CombatDocument(id = id, campaignId = campaignId, name = name.trim(), createdAtEpochSeconds = nowEpochSeconds, updatedAtEpochSeconds = nowEpochSeconds)

    /** A creature from the bestiary, [quantity] copies in one group; HP starts at the stat block average (P13). */
    fun addCreature(doc: CombatDocument, creature: CreatureContent, quantity: Int = 1, reserve: Boolean = false, label: String? = null): CombatDocument {
        require(quantity in 1..50) { "Quantity must be between 1 and 50." }
        val groupId = Uuid.random()
        val base = (label ?: creature.item.displayName).trim()
        val existing = doc.combatants.count { it.sourceContentId == creature.item.identity.id }
        val maxHp = creature.payload.hitPoints ?: DiceExpression.parse(creature.payload.hitDice)?.average
        val added = (1..quantity).map { index ->
            Combatant(
                id = Uuid.random(),
                kind = CombatantKind.CREATURE,
                label = if (quantity + existing > 1) "$base ${existing + index}" else base,
                groupId = groupId,
                sourceContentId = creature.item.identity.id,
                baseArmorClass = creature.payload.armorClass,
                baseMaxHp = maxHp,
                hitDice = creature.payload.hitDice,
                dexterityModifier = dexModifier(creature.payload.abilityScores.dexterity),
                currentHp = maxHp,
                reserve = reserve,
            )
        }
        return doc.copy(combatants = doc.combatants + added)
    }

    /** An improvised participant: a free name with HP and AC written by hand (P14). */
    fun addCustom(doc: CombatDocument, name: String, maxHp: Int?, armorClass: Int?, quantity: Int = 1): CombatDocument {
        require(quantity in 1..50) { "Quantity must be between 1 and 50." }
        val groupId = Uuid.random()
        val added = (1..quantity).map { index ->
            Combatant(
                id = Uuid.random(),
                kind = CombatantKind.CUSTOM,
                label = if (quantity > 1) "${name.trim()} $index" else name.trim(),
                groupId = groupId,
                baseArmorClass = armorClass,
                baseMaxHp = maxHp,
                currentHp = maxHp,
            )
        }
        return doc.copy(combatants = doc.combatants + added)
    }

    /** The campaign PCs not yet in the combat. Their initiative is written by hand (P12). */
    fun addPcs(doc: CombatDocument, characters: List<CharacterSheet>): CombatDocument {
        val present = doc.combatants.mapNotNull { it.characterId }.toSet()
        val added = characters.filter { it.id !in present }.map { sheet ->
            Combatant(
                id = Uuid.random(),
                kind = CombatantKind.PC,
                label = sheet.name,
                groupId = Uuid.random(),
                characterId = sheet.id,
                baseArmorClass = sheet.armorClass,
                baseMaxHp = sheet.maxHp,
                dexterityModifier = dexModifier(sheet.dexterity),
            )
        }
        return doc.copy(combatants = doc.combatants + added)
    }

    /** A prepared encounter enters whole (P14): quantities expand, «reserva» stays in reserve. */
    fun addEncounter(doc: CombatDocument, encounter: EncounterContent, creatures: Map<Uuid, CreatureContent>): CombatDocument =
        encounter.payload.participants.fold(doc.copy(encounterId = doc.encounterId ?: encounter.item.identity.id)) { acc, participant ->
            val creature = participant.sourceContentId?.let(creatures::get)
            val reserve = participant.readiness != EncounterParticipantReadiness.EXPECTED
            when {
                creature != null -> addCreature(acc, creature, participant.quantity, reserve, participant.label.ifBlank { null })
                participant.label.isNotBlank() -> {
                    val withCustom = addCustom(acc, participant.label, null, null, participant.quantity)
                    val newIds = withCustom.combatants.drop(acc.combatants.size).map { it.id }.toSet()
                    withCustom.copy(combatants = withCustom.combatants.map { c -> if (c.id in newIds) c.copy(reserve = reserve) else c })
                }
                else -> acc
            }
        }

    fun duplicate(doc: CombatDocument, id: Uuid): CombatDocument {
        val source = doc.combatant(id) ?: return doc
        val sameGroup = doc.combatants.count { it.groupId == source.groupId }
        val stem = source.label.replace(Regex("""\s+\d+$"""), "")
        val copy = source.copy(
            id = Uuid.random(),
            label = "$stem ${sameGroup + 1}",
            currentHp = source.baseMaxHp?.let { source.overrides.maxHp ?: it } ?: source.currentHp,
            initiative = null,
            initiativeDie = null,
            conditions = emptyList(),
            concentrating = false,
            defeated = false,
        )
        return doc.copy(combatants = doc.combatants + copy)
    }

    /** Swaps the stat block behind one combatant (P19 «cambiar por otra criatura»); patches are dropped. */
    fun substitute(doc: CombatDocument, id: Uuid, creature: CreatureContent): CombatDocument {
        val current = doc.combatant(id) ?: return doc
        val maxHp = creature.payload.hitPoints ?: DiceExpression.parse(creature.payload.hitDice)?.average
        return replace(doc, current.copy(
            label = creature.item.displayName,
            kind = CombatantKind.CREATURE,
            sourceContentId = creature.item.identity.id,
            groupId = Uuid.random(),
            baseArmorClass = creature.payload.armorClass,
            baseMaxHp = maxHp,
            hitDice = creature.payload.hitDice,
            dexterityModifier = dexModifier(creature.payload.abilityScores.dexterity),
            overrides = CombatOverrides(),
            packageIds = emptyList(),
            currentHp = maxHp,
        ))
    }

    fun remove(doc: CombatDocument, id: Uuid): CombatDocument = doc.copy(
        combatants = doc.combatants.filter { it.id != id },
        activeCombatantId = doc.activeCombatantId.takeIf { it != id },
    )

    fun setReserve(doc: CombatDocument, id: Uuid, reserve: Boolean): CombatDocument = update(doc, id) { it.copy(reserve = reserve) }

    /** Sets quick tweaks on one combatant or, with [wholeGroup], on every copy of its stat block. */
    fun setOverrides(doc: CombatDocument, id: Uuid, overrides: CombatOverrides, wholeGroup: Boolean = false): CombatDocument =
        forTargets(doc, id, wholeGroup) { c ->
            val oldMax = c.overrides.maxHp ?: c.baseMaxHp
            val newMax = overrides.maxHp ?: c.baseMaxHp
            // A combatant still at full HP follows a new maximum; a wounded one keeps its HP.
            val hp = if (c.kind != CombatantKind.PC && c.currentHp == oldMax) newMax else c.currentHp
            c.copy(overrides = overrides, currentHp = hp)
        }

    /**
     * Applies a package. Its HP change also moves the current HP by the same amount (a package of
     * +10 HP gives 10 more HP now), unless the DM wrote the maximum HP by hand.
     */
    fun applyPackage(doc: CombatDocument, id: Uuid, pkg: PackageContent, wholeGroup: Boolean = false): CombatDocument {
        val packageId = pkg.item.identity.id
        return forTargets(doc, id, wholeGroup) { c ->
            if (packageId in c.packageIds) c else c.copy(packageIds = c.packageIds + packageId, currentHp = shiftHp(c, pkg.payload.hpDelta))
        }
    }

    fun removePackage(doc: CombatDocument, id: Uuid, pkg: PackageContent, wholeGroup: Boolean = false): CombatDocument {
        val packageId = pkg.item.identity.id
        return forTargets(doc, id, wholeGroup) { c ->
            if (packageId !in c.packageIds) c else c.copy(packageIds = c.packageIds - packageId, currentHp = shiftHp(c, -pkg.payload.hpDelta))
        }
    }

    private fun shiftHp(c: Combatant, delta: Int): Int? {
        val hp = c.currentHp ?: return null
        return if (c.overrides.maxHp != null || c.kind == CombatantKind.PC) hp else maxOf(0, hp + delta)
    }

    /** Undoes every tweak and package (D-0068 §13 «reset/reversibility»). */
    fun resetPatches(doc: CombatDocument, id: Uuid, wholeGroup: Boolean = false): CombatDocument =
        forTargets(doc, id, wholeGroup) { c -> c.copy(overrides = CombatOverrides(), packageIds = emptyList()) }

    /** Rolls the HP dice of a creature (P13); without dice it keeps the current value. */
    fun rollHp(doc: CombatDocument, id: Uuid, random: Random): CombatDocument = update(doc, id) { c ->
        val dice = DiceExpression.parse(c.hitDice) ?: return@update c
        val rolled = dice.roll(random)
        c.copy(overrides = c.overrides.copy(maxHp = rolled), currentHp = rolled)
    }

    fun setInitiative(doc: CombatDocument, id: Uuid, value: Int?): CombatDocument = update(doc, id) { it.copy(initiative = value, initiativeDie = null) }

    /** P33: «Una para el grupo» (one number, one turn) or «Cada uno la suya». */
    fun setInitiativeShared(doc: CombatDocument, groupId: Uuid, shared: Boolean): CombatDocument {
        val groups = if (shared) (doc.sharedInitiativeGroups + groupId).distinct() else doc.sharedInitiativeGroups - groupId
        val withMode = doc.copy(sharedInitiativeGroups = groups)
        if (!shared) return withMode
        // Joining a group gives everyone the first member's number, so the group really acts together.
        val first = doc.combatants.firstOrNull { it.groupId == groupId && it.initiative != null } ?: return withMode
        return setGroupInitiative(withMode, groupId, first.initiative, first.initiativeDie)
    }

    /** Writes (or, with [die], records a roll for) the same number for every member of a group. */
    fun setGroupInitiative(doc: CombatDocument, groupId: Uuid, value: Int?, die: Int? = null): CombatDocument = doc.copy(
        combatants = doc.combatants.map { c -> if (c.groupId == groupId) c.copy(initiative = value, initiativeDie = die) else c },
    )

    /** A d20 already rolled ([die], 1-20) plus the combatant's DEX, through the PC dice engine (P34). */
    fun rollInitiativeFor(doc: CombatDocument, id: Uuid, die: Int): CombatDocument = update(doc, id) { c ->
        c.copy(initiative = resolveCharacterD20Roll(firstDie = die, modifier = c.dexterityModifier).total, initiativeDie = die)
    }

    fun rollGroupInitiative(doc: CombatDocument, groupId: Uuid, die: Int): CombatDocument {
        val first = doc.combatants.firstOrNull { it.groupId == groupId } ?: return doc
        return setGroupInitiative(doc, groupId, resolveCharacterD20Roll(firstDie = die, modifier = first.dexterityModifier).total, die)
    }

    /** «Tirar todas las criaturas»: one roll per shared group, one per creature otherwise. PCs are written. */
    fun rollAllCreatureInitiative(doc: CombatDocument, d20: () -> Int): CombatDocument {
        var result = doc
        doc.combatants.filter { it.kind != CombatantKind.PC }.map { it.groupId }.distinct().forEach { groupId ->
            result = if (doc.sharesInitiative(groupId)) {
                rollGroupInitiative(result, groupId, d20())
            } else {
                result.combatants.filter { it.groupId == groupId }.fold(result) { acc, c -> rollInitiativeFor(acc, c.id, d20()) }
            }
        }
        return result
    }

    /** − / + on a block (P32 «Goblin jefe ×3»): adds copies of the last member, or removes the last ones. */
    fun setGroupQuantity(doc: CombatDocument, groupId: Uuid, quantity: Int): CombatDocument {
        val members = doc.combatants.filter { it.groupId == groupId }
        if (members.isEmpty() || quantity < 1 || quantity > 50 || quantity == members.size) return doc
        if (quantity > members.size) {
            return (members.size until quantity).fold(doc) { acc, _ ->
                val last = acc.combatants.last { it.groupId == groupId }
                duplicate(acc, last.id)
            }
        }
        val drop = members.reversed().take(members.size - quantity).map { it.id }.toSet()
        return doc.copy(
            combatants = doc.combatants.filter { it.id !in drop },
            activeCombatantId = doc.activeCombatantId.takeIf { it !in drop },
        )
    }

    fun setEdits(doc: CombatDocument, id: Uuid, edits: CreatureEdits, wholeGroup: Boolean = false): CombatDocument =
        forTargets(doc, id, wholeGroup) { it.copy(edits = edits) }

    fun setLabel(doc: CombatDocument, id: Uuid, label: String): CombatDocument =
        if (label.isBlank()) doc else update(doc, id) { it.copy(label = label.trim()) }

    /** «Deshacer todo» on one combatant or its group: tweaks, packages and editor changes. */
    fun resetAll(doc: CombatDocument, id: Uuid, wholeGroup: Boolean = false): CombatDocument {
        val target = doc.combatant(id) ?: return doc
        return doc.copy(combatants = doc.combatants.map { c ->
            if (c.id == id || (wholeGroup && c.groupId == target.groupId)) {
                val oldMax = c.overrides.maxHp ?: c.baseMaxHp
                val hp = if (c.kind != CombatantKind.PC && c.currentHp == oldMax) c.baseMaxHp else c.currentHp
                c.copy(overrides = CombatOverrides(), packageIds = emptyList(), edits = CreatureEdits(), currentHp = hp)
            } else {
                c
            }
        })
    }

    /**
     * «Guardar como criatura nueva» (P32, D-0068 §14): the base stat block with this combatant's own
     * values written in. Structured attacks are added to the actions text. The base creature is not touched.
     */
    fun variantPayload(base: CreaturePayload?, combatant: Combatant, effective: EffectiveCombatant): CreaturePayload {
        val start = base ?: CreaturePayload()
        val e = combatant.edits
        val attacksText = e.attacks.joinToString("\n") { it.describe() }
        val actions = listOf(e.actions ?: start.actions, attacksText).filter { it.isNotBlank() }.joinToString("\n")
        val traits = listOf(e.traits ?: start.traits, e.spells?.let { "Conjuros. $it" }.orEmpty()).filter { it.isNotBlank() }.joinToString("\n")
        val defenses = e.defenses
        return start.copy(
            armorClass = effective.armorClass ?: start.armorClass,
            armorClassDetails = e.armorClassDetails ?: start.armorClassDetails,
            hitPoints = effective.maxHp ?: start.hitPoints,
            speed = e.speed ?: start.speed,
            abilityScores = e.abilityScores ?: start.abilityScores,
            damageResistances = defenses ?: start.damageResistances,
            traits = traits,
            actions = actions,
            notes = listOf(start.notes, combatant.notes).filter { it.isNotBlank() }.joinToString("\n"),
        )
    }

    /** d20 + DEX for one creature, or for every creature and improvised participant (P12). PCs are written. */
    fun rollInitiative(doc: CombatDocument, random: Random, id: Uuid? = null): CombatDocument = doc.copy(
        combatants = doc.combatants.map { c ->
            if ((id == null || c.id == id) && c.kind != CombatantKind.PC) c.copy(initiative = random.nextInt(1, 21) + c.dexterityModifier) else c
        },
    )

    fun damage(doc: CombatDocument, id: Uuid, amount: Int): CombatDocument = update(doc, id) { c ->
        val hp = c.currentHp ?: return@update c
        c.copy(currentHp = maxOf(0, hp - maxOf(0, amount)))
    }

    fun heal(doc: CombatDocument, id: Uuid, amount: Int, maxHp: Int?): CombatDocument = update(doc, id) { c ->
        val hp = c.currentHp ?: return@update c
        val healed = hp + maxOf(0, amount)
        c.copy(currentHp = if (maxHp != null) minOf(maxHp, healed) else healed)
    }

    fun setCurrentHp(doc: CombatDocument, id: Uuid, value: Int?): CombatDocument = update(doc, id) { it.copy(currentHp = value?.coerceAtLeast(0)) }

    fun setPcHpNote(doc: CombatDocument, id: Uuid, note: String): CombatDocument = update(doc, id) { it.copy(pcHpNote = note.take(40)) }

    fun toggleCondition(doc: CombatDocument, id: Uuid, condition: String): CombatDocument = update(doc, id) { c ->
        c.copy(conditions = if (condition in c.conditions) c.conditions - condition else c.conditions + condition)
    }

    fun setConcentrating(doc: CombatDocument, id: Uuid, value: Boolean) = update(doc, id) { it.copy(concentrating = value) }

    fun setDefeated(doc: CombatDocument, id: Uuid, value: Boolean) = update(doc, id) { it.copy(defeated = value) }

    fun setNotes(doc: CombatDocument, id: Uuid, notes: String) = update(doc, id) { it.copy(notes = notes) }

    /** «Empezar combate»: the prepared state is kept; round 1 starts at the top of the order. */
    fun start(doc: CombatDocument): CombatDocument {
        val surprise = doc.surpriseActorIds.any { id -> doc.combatant(id)?.reserve == false }
        val started = doc.copy(status = CombatStatus.ACTIVE, round = if (surprise) 0 else 1)
        return started.copy(activeCombatantId = started.slotsThisRound().firstNotNullOfOrNull { slot -> slot.firstOrNull { !it.defeated } }?.id)
    }

    /** P22: who acts in the surprise round, set or changed by the DM (empty = start without surprise). */
    fun setSurpriseActors(doc: CombatDocument, ids: List<Uuid>): CombatDocument =
        doc.copy(surpriseActorIds = ids.filter { doc.combatant(it) != null }.distinct())

    /** In the surprise round only its actors take turns; afterwards, everyone. */
    private fun CombatDocument.slotsThisRound(): List<List<Combatant>> =
        if (round == 0) turnSlots.filter { slot -> slot.any { it.id in surpriseActorIds } } else turnSlots

    /** Next turn with someone still standing (a shared group is one turn); passing the end starts a new round. */
    fun nextTurn(doc: CombatDocument): CombatDocument {
        if (doc.round == 0) {
            val surpriseSlots = doc.slotsThisRound()
            val index = surpriseSlots.indexOfFirst { slot -> slot.any { it.id == doc.activeCombatantId } }
            val next = surpriseSlots.drop(index + 1).firstOrNull { slot -> slot.any { !it.defeated } }
            if (next != null) return doc.copy(activeCombatantId = next.first { !it.defeated }.id)
            val firstOfRoundOne = doc.turnSlots.firstOrNull { slot -> slot.any { !it.defeated } } ?: return doc.copy(activeCombatantId = null, round = 1)
            return doc.copy(activeCombatantId = firstOfRoundOne.first { !it.defeated }.id, round = 1)
        }
        val slots = doc.turnSlots.filter { slot -> slot.any { !it.defeated } }
        if (slots.isEmpty()) return doc.copy(activeCombatantId = null)
        val all = doc.turnSlots
        val currentIndex = all.indexOfFirst { slot -> slot.any { it.id == doc.activeCombatantId } }
        val after = if (currentIndex < 0) emptyList() else all.drop(currentIndex + 1).filter { slot -> slot.any { !it.defeated } }
        return if (after.isNotEmpty()) {
            doc.copy(activeCombatantId = after.first().first { !it.defeated }.id)
        } else {
            doc.copy(activeCombatantId = slots.first().first { !it.defeated }.id, round = doc.round + 1)
        }
    }

    fun end(doc: CombatDocument): CombatDocument = doc.copy(status = CombatStatus.ENDED, activeCombatantId = null)

    fun rename(doc: CombatDocument, name: String): CombatDocument = if (name.isBlank()) doc else doc.copy(name = name.trim())

    private fun update(doc: CombatDocument, id: Uuid, change: (Combatant) -> Combatant): CombatDocument =
        doc.copy(combatants = doc.combatants.map { if (it.id == id) change(it) else it })

    private fun replace(doc: CombatDocument, combatant: Combatant): CombatDocument =
        doc.copy(combatants = doc.combatants.map { if (it.id == combatant.id) combatant else it })

    private fun forTargets(doc: CombatDocument, id: Uuid, wholeGroup: Boolean, change: (Combatant) -> Combatant): CombatDocument {
        val target = doc.combatant(id) ?: return doc
        return doc.copy(combatants = doc.combatants.map { c ->
            if (c.id == id || (wholeGroup && c.groupId == target.groupId)) change(c) else c
        })
    }
}

/** Combats of a campaign on this device. */
class DmCombatRepository(private val database: AppDatabase) {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun list(campaignId: Uuid): List<CombatDocument> =
        database.dmCombatQueries.selectCombatsByCampaign(campaignId.toString()).executeAsList()
            .mapNotNull { runCatching { json.decodeFromString(CombatDocument.serializer(), it) }.getOrNull() }

    fun combat(id: Uuid): CombatDocument? =
        database.dmCombatQueries.selectCombat(id.toString()).executeAsOneOrNull()
            ?.let { json.decodeFromString(CombatDocument.serializer(), it) }

    /** Saves the document as it is now; every change on the table is saved at once. */
    fun save(doc: CombatDocument, nowEpochSeconds: Long): CombatDocument {
        val stamped = doc.copy(updatedAtEpochSeconds = maxOf(nowEpochSeconds, doc.createdAtEpochSeconds))
        database.dmCombatQueries.upsertCombat(
            id = stamped.id.toString(),
            campaign_id = stamped.campaignId.toString(),
            status = stamped.status.name,
            updated_at_epoch_seconds = stamped.updatedAtEpochSeconds,
            document_json = json.encodeToString(CombatDocument.serializer(), stamped),
        )
        return stamped
    }

    fun delete(id: Uuid) {
        database.dmCombatQueries.deleteCombat(id.toString())
    }
}
