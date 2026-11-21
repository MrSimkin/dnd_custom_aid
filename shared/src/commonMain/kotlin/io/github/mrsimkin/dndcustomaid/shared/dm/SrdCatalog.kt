package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContent
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind
import io.github.mrsimkin.dndcustomaid.shared.content.RulesOrigin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/*
 * Redesign R4/R5: the SRD as lists to read and to pick from (scripts/build_srd_catalog.py).
 * Separate from the DM's homebrew (D13). The pickers of the PC editor always offer SRD first,
 * then homebrew, then custom (D9).
 */

@Serializable
data class SrdCatalogEntry(
    val kind: RulesEntryKind,
    val name: String,
    val summary: String = "",
    val body: String = "",
    val fields: Map<String, String> = emptyMap(),
    val page: Int = 0,
    val parent: String = "",
)

@Serializable
data class SrdCatalog(
    val format: String,
    val formatVersion: Int,
    val version: String,
    val edition: String,
    val license: String,
    val attribution: String,
    val entries: List<SrdCatalogEntry>,
) {
    fun of(kind: RulesEntryKind): List<SrdCatalogEntry> = entries.filter { it.kind == kind }.sortedBy { spanishSortKey(it.name) }
}

private val catalogJson = Json { ignoreUnknownKeys = true }

fun parseSrdCatalog(json: String): SrdCatalog = catalogJson.decodeFromString(SrdCatalog.serializer(), json)

/** The bundled catalog files, 5e first. */
val SRD_CATALOG_FILES = listOf("srd_catalog_5_1_es.json", "srd_catalog_5_2_1_es.json")

/** One choice in a picker of the PC editor or the rules library. */
data class RulesOption(
    val origin: RulesOrigin,
    val kind: RulesEntryKind,
    val name: String,
    val summary: String,
    val fields: Map<String, String>,
    val body: String,
    /** «SRD 5.1 p. 120», «Manual Simkin 1.4»… */
    val source: String,
    val edition: String,
    val parent: String = "",
)

/**
 * Every choice of one kind, SRD first (the campaign's edition before the other), then homebrew.
 * Custom is not a list: the picker offers it as free text after these.
 */
fun rulesOptions(
    kind: RulesEntryKind,
    catalogs: List<SrdCatalog>,
    homebrew: List<RulesEntryContent>,
    preferredEdition: String? = null,
    query: String = "",
): List<RulesOption> {
    val needle = query.trim().lowercase()
    fun matches(name: String, summary: String) = needle.isEmpty() || name.lowercase().contains(needle) || summary.lowercase().contains(needle)
    val orderedCatalogs = catalogs.sortedBy { if (it.edition == preferredEdition) 0 else 1 }
    val srd = orderedCatalogs.flatMap { catalog ->
        catalog.of(kind).filter { matches(it.name, it.summary) }.map {
            RulesOption(
                origin = RulesOrigin.SRD,
                kind = kind,
                name = it.name,
                summary = it.summary,
                fields = it.fields,
                body = it.body,
                source = "SRD ${catalog.version}${if (it.page > 0) ", p. ${it.page}" else ""}",
                edition = catalog.edition,
                parent = it.parent,
            )
        }
    }
    val own = homebrew.filter { it.payload.kind == kind && matches(it.item.displayName, it.payload.summary) }.map {
        RulesOption(
            origin = RulesOrigin.HOMEBREW,
            kind = kind,
            name = it.item.displayName,
            summary = it.payload.summary,
            fields = it.payload.fields,
            body = it.payload.body,
            source = it.payload.source.ifBlank { "Homebrew" },
            edition = it.payload.edition,
            parent = it.payload.parent,
        )
    }.sortedBy { spanishSortKey(it.name) }
    return srd + own
}

/** Alphabetical order as a Spanish reader expects it: «Bárbaro» before «Bardo» and «Brujo». */
fun spanishSortKey(text: String): String = text.lowercase()
    .replace('á', 'a').replace('é', 'e').replace('í', 'i').replace('ó', 'o').replace('ú', 'u').replace('ü', 'u')
    .replace("ñ", "n~")
