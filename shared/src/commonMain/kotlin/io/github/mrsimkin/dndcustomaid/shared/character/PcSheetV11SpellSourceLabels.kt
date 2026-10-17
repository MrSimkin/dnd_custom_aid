package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/**
 * Retain distinct persisted spellcasting-source identities on P4 even when the
 * user assigned the same display name to multiple IDs. Unique names retain
 * their previous appearance. Only ambiguous names gain a collision-free
 * prefix of their real UUID, not a fabricated presentation ordinal.
 */
object PcSheetV11SpellSourceLabels {
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
