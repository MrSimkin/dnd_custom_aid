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


    /**
     * C228: a sheet's unmapped OTHER/NONE magic aptitude is not an absence
     * of a real spellcasting ability when an associated persisted source
     * explicitly names a built-in or custom attribute. P1/P4 already have
     * an approved source-specific cue; do not invent a universal attribute.
     * In particular, no spells or only unconfigured source profiles must
     * keep the original blank reference.
     */
    fun unmappedOverallAbilityFromRealSource(
        entries: List<PcSheetSpellbookEntry>,
        overallAbility: SpellcastingAbility,
    ): Set<String> {
        if (overallAbility != SpellcastingAbility.OTHER &&
            overallAbility != SpellcastingAbility.NONE) return emptySet()
        return if (entries.flatMap { it.sources }.distinctBy { it.sourceId }
            .any { it.castingAbility.configured }
        ) setOf("APTITUD") else emptySet()
    }

    /**
     * C229: no global CD/attack is not proof that an explicitly associated
     * casting source has no actual stat. If the source knows its own metric,
     * direct readers to that genuine source, using the already-approved cue.
     * An unknown source statistic must NOT produce a fabricated reference.
     */
    fun missingOverallMetricsFromRealSource(
        entries: List<PcSheetSpellbookEntry>,
        overallDc: Int?,
        overallAttackModifier: Int?,
    ): Set<String> {
        val sources = entries.flatMap { it.sources }.distinctBy { it.sourceId }
        return buildSet {
            if (overallDc == null && sources.any { it.saveDc != null }) add("CD SALVACIÓN")
            if (overallAttackModifier == null &&
                sources.any { it.spellAttackModifier != null }) add("ATAQUE MÁGICO")
        }
    }

    /** C225/C228/C229: common source-backed P1/P4 reference contract. */
    fun contradictoryOverallFields(
        entries: List<PcSheetSpellbookEntry>,
        overallAbility: SpellcastingAbility,
        overallDc: Int?,
        overallAttackModifier: Int?,
    ): Set<String> = conflictingReferenceFields(entries) +
        globalReferenceDisagreements(entries, overallDc, overallAttackModifier) +
        globalAbilityDisagreement(entries, overallAbility) +
        unmappedOverallAbilityFromRealSource(entries, overallAbility) +
        missingOverallMetricsFromRealSource(entries, overallDc, overallAttackModifier)

    /**
     * C230 (owner decision): when a contradictory P4 reference field cannot be
     * a single universal value, print each real source with its own value,
     * e.g. `Mago 15 / Hechicero 17`. A source that does not know the value
     * prints an explicit dash instead of a fabricated number. Returns null
     * when there are no sources (callers keep the previous cue).
     */
    fun perSourceReference(
        entries: List<PcSheetSpellbookEntry>,
        field: String,
        customAbbreviation: (Uuid) -> String?,
    ): String? = perSourceParts(entries, field, customAbbreviation)
        ?.joinToString(" / ")

    /** The same entries as [perSourceReference], one `Name value` per real source. */
    fun perSourceParts(
        entries: List<PcSheetSpellbookEntry>,
        field: String,
        customAbbreviation: (Uuid) -> String?,
    ): List<String>? {
        val sources = entries.flatMap { it.sources }.distinctBy { it.sourceId }
        if (sources.isEmpty()) return null
        val names = forSpells(entries)
        fun value(source: PcSheetSpellbookSourceProjection): String = when (field) {
            "APTITUD" -> source.castingAbility.builtIn?.let {
                when (it) {
                    CharacterAbility.STRENGTH -> "FUE"
                    CharacterAbility.DEXTERITY -> "DES"
                    CharacterAbility.CONSTITUTION -> "CON"
                    CharacterAbility.INTELLIGENCE -> "INT"
                    CharacterAbility.WISDOM -> "SAB"
                    CharacterAbility.CHARISMA -> "CAR"
                }
            } ?: source.castingAbility.customAttributeId?.let(customAbbreviation) ?: "—"
            "CD SALVACIÓN" -> source.saveDc?.toString() ?: "—"
            "ATAQUE MÁGICO" -> source.spellAttackModifier?.let {
                if (it >= 0) "+$it" else it.toString()
            } ?: "—"
            else -> "—"
        }
        return sources.map { source ->
            // Source names may carry CR/LF/TAB from user input; the printed
            // reference is a single physical line.
            names.getValue(source.sourceId).trim()
                .replace(Regex("\\s+"), " ") + " " + value(source)
        }
    }

    /**
     * C231 (owner decision 2026-10-26): P4 identifies a casting source by its
     * ORIGIN TYPE, with no specific names, except a class source, which keeps
     * its class name without subclass (e.g. "Mago", "Clérigo"). The type
     * labels are the same ones the app's source editor uses (D-0058 /
     * "Tipo de origen"). An association to a missing source record (no stored type) reads "Otro".
     */
    fun sourceLabel(source: PcSheetSpellbookSourceProjection): String {
        val name = source.sourceName?.trim()?.replace(Regex("\\s+"), " ")
            ?.takeIf { it.isNotEmpty() }
        return when (source.originKind) {
            CharacterSpellcastingOriginKind.CLASS -> name ?: "Clase"
            CharacterSpellcastingOriginKind.TRAIT -> "Rasgo"
            CharacterSpellcastingOriginKind.RACE -> "Raza"
            CharacterSpellcastingOriginKind.BACKGROUND -> "Trasfondo"
            CharacterSpellcastingOriginKind.FEAT -> "Dote"
            CharacterSpellcastingOriginKind.ITEM -> "Objeto"
            CharacterSpellcastingOriginKind.MAGIC_ITEM -> "Objeto mágico"
            CharacterSpellcastingOriginKind.GIFT -> "Don"
            CharacterSpellcastingOriginKind.OTHER -> "Otro"
            // An association whose source record no longer exists has no stored type.
            null -> "Otro"
        }
    }

    private fun isNameBased(source: PcSheetSpellbookSourceProjection): Boolean =
        source.originKind == null || source.originKind == CharacterSpellcastingOriginKind.CLASS

    fun forSpells(entries: List<PcSheetSpellbookEntry>): Map<Uuid, String> {
        val sources = entries.flatMap { it.sources }.distinctBy { it.sourceId }
        fun label(source: PcSheetSpellbookSourceProjection): String = sourceLabel(source)

        val byName = sources.groupBy { label(it).trim().lowercase() }
        return sources.associate { source ->
            val original = label(source)
            val siblings = byName.getValue(original.trim().lowercase())
            // Type labels (Dote, Raza...) may legitimately repeat: the owner wants
            // no names there, and each row still keeps its own preparation mark.
            // Only name-based labels get a collision-free UUID prefix.
            if (siblings.size == 1 || !isNameBased(source)) {
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
