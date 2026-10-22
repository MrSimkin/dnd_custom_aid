package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/**
 * Retain distinct persisted spellcasting-source identities on P4 even when the
 * user assigned the same display name to multiple IDs. Unique names retain
 * their previous appearance. Only ambiguous names gain a collision-free
 * prefix of their real UUID, not a fabricated presentation ordinal.
 */
object PcSheetV11SpellSourceLabels {
    /**
     * C221: a universal P4 reference MUST NOT present one score/ability
     * as if it applied to multiple genuinely different casting sources.
     * Null/UNKNOWN is not silently collapsed into a known source value.
     * A stable UUID is the identity; preparation and equal visible names
     * are irrelevant to this particular three-field contradiction.
     */
    fun conflictingReferenceFields(entries: List<PcSheetSpellbookEntry>): Set<String> {
        val profiles = entries.flatMap { it.sources }.distinctBy { it.sourceId }
        if (profiles.size <= 1) return emptySet()
        return buildSet {
            if (profiles.map { it.castingAbility }.distinct().size > 1) add("APTITUD")
            if (profiles.map { it.saveDc }.distinct().size > 1) add("CD SALVACIÓN")
            if (profiles.map { it.spellAttackModifier }.distinct().size > 1)
                add("ATAQUE MÁGICO")
        }
    }


    /**
     * C223: an overall character value is not an authoritative value for a
     * specific spellcasting source when the persisted source profile explicitly
     * disagrees. Null source metrics do NOT prove a disagreement; C221 separately
     * handles mixed known/unknown per-source profiles. The owner has not yet
     * approved a per-source numeric card layout, so reuse its truthful cue.
     */
    fun globalReferenceDisagreements(
        entries: List<PcSheetSpellbookEntry>,
        overallDc: Int?,
        overallAttackModifier: Int?,
    ): Set<String> {
        val sources = entries.flatMap { it.sources }.distinctBy { it.sourceId }
        return buildSet {
            if (overallDc != null && sources.any { it.saveDc != null && it.saveDc != overallDc })
                add("CD SALVACIÓN")
            if (overallAttackModifier != null && sources.any {
                it.spellAttackModifier != null &&
                    it.spellAttackModifier != overallAttackModifier
            }) add("ATAQUE MÁGICO")
        }
    }


    /**
     * C224: one global built-in casting aptitude cannot describe explicitly
     * configured source aptitudes that disagree. Unknown source ability cannot
     * prove disagreement; global NONE/OTHER is not a mapped built-in.
     */
    fun globalAbilityDisagreement(
        entries: List<PcSheetSpellbookEntry>,
        overallAbility: SpellcastingAbility,
    ): Set<String> {
        val expected = when (overallAbility) {
            SpellcastingAbility.STRENGTH -> CharacterAbility.STRENGTH
            SpellcastingAbility.DEXTERITY -> CharacterAbility.DEXTERITY
            SpellcastingAbility.CONSTITUTION -> CharacterAbility.CONSTITUTION
            SpellcastingAbility.INTELLIGENCE -> CharacterAbility.INTELLIGENCE
            SpellcastingAbility.WISDOM -> CharacterAbility.WISDOM
            SpellcastingAbility.CHARISMA -> CharacterAbility.CHARISMA
            SpellcastingAbility.OTHER, SpellcastingAbility.NONE -> null
        } ?: return emptySet()
        return if (entries.flatMap { it.sources }.distinctBy { it.sourceId }
            .any { it.castingAbility.configured && it.castingAbility.builtIn != expected }
        ) setOf("APTITUD") else emptySet()
    }


    /** C225: identical semantic contract for source-backed P1 and P4 cards. */
    fun contradictoryOverallFields(
        entries: List<PcSheetSpellbookEntry>,
        overallAbility: SpellcastingAbility,
        overallDc: Int?,
        overallAttackModifier: Int?,
    ): Set<String> = conflictingReferenceFields(entries) +
        globalReferenceDisagreements(entries, overallDc, overallAttackModifier) +
        globalAbilityDisagreement(entries, overallAbility)

    fun forSpells(entries: List<PcSheetSpellbookEntry>): Map<Uuid, String> {
        val sources = entries.flatMap { it.sources }.distinctBy { it.sourceId }
        fun label(source: PcSheetSpellbookSourceProjection): String =
            source.sourceName?.takeIf { it.isNotBlank() } ?: source.sourceId.toString()

        val byName = sources.groupBy { label(it).trim().lowercase() }
        return sources.associate { source ->
            val original = label(source)
            val siblings = byName.getValue(original.trim().lowercase())
            if (siblings.size == 1) {
                source.sourceId to original
            } else {
                val id = source.sourceId.toString()
                val length = (6..id.length).first { size ->
                    val prefix = id.take(size)
                    siblings.none { other ->
                        other.sourceId != source.sourceId &&
                            other.sourceId.toString().startsWith(prefix)
                    }
                }
                source.sourceId to "${original.trim()} (#${id.take(length)})"
            }
        }
    }

    fun perSpell(
        entry: PcSheetSpellbookEntry,
        namesById: Map<Uuid, String>,
    ): String = entry.sources.joinToString(" / ") { association ->
        val name = namesById.getValue(association.sourceId)
        "[" + (if (association.prepared) "X" else " ") + "] " + name
    }
}
