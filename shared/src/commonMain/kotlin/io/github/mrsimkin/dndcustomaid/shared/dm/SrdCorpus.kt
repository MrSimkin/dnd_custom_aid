package io.github.mrsimkin.dndcustomaid.shared.dm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/*
 * Base para DM: the official Spanish SRDs inside the App (owner decisions P4, P10 and
 * «todo debe ir en la App, usaremos todo lo que esté disponible»).
 *
 * The corpus files are built by scripts/build_srd_corpus.py from the CC BY 4.0 PDFs of Wizards of
 * the Coast. This file only reads them and searches them offline, without AI or network.
 */

enum class SrdVersion(val key: String, val edition: String) {
    SRD_5_1("5.1", "5e"),
    SRD_5_2_1("5.2.1", "5.5e"),
    ;

    val other: SrdVersion
        get() = if (this == SRD_5_1) SRD_5_2_1 else SRD_5_1

    /** Owner-facing label, e.g. «5.5e (SRD 5.2.1)». */
    val spanishLabel: String
        get() = "$edition (SRD $key)"

    companion object {
        fun fromKey(key: String?): SrdVersion? = entries.firstOrNull { it.key == key }
    }
}

@Serializable
data class SrdCorpusPage(
    val page: Int,
    val text: String,
)

@Serializable
data class SrdCorpus(
    val format: String,
    val formatVersion: Int,
    val language: String,
    val version: String,
    val edition: String,
    val title: String,
    val license: String,
    val attribution: String,
    val sourceUrl: String,
    val sourceSha256: String,
    val pages: List<SrdCorpusPage>,
) {
    init {
        require(format == "dnd-custom-aid.srd-corpus") { "Unsupported SRD corpus format." }
        require(formatVersion == 1) { "Unsupported SRD corpus format version." }
    }

    val srdVersion: SrdVersion
        get() = requireNotNull(SrdVersion.fromKey(version)) { "Unknown SRD version $version." }
}

@Serializable
data class SrdQuickReferenceSection(
    val heading: String,
    val page: Int,
    val text: String,
)

@Serializable
data class SrdQuickReferenceCard(
    val id: String,
    val title: String,
    val versions: Map<String, List<SrdQuickReferenceSection>>,
) {
    fun sections(version: SrdVersion): List<SrdQuickReferenceSection> = versions[version.key].orEmpty()
}

@Serializable
data class SrdQuickReference(
    val format: String,
    val formatVersion: Int,
    val language: String,
    val attribution: Map<String, String>,
    val cards: List<SrdQuickReferenceCard>,
) {
    init {
        require(format == "dnd-custom-aid.srd-quick-reference") { "Unsupported quick reference format." }
        require(formatVersion == 1) { "Unsupported quick reference format version." }
    }

    fun attribution(version: SrdVersion): String = attribution[version.key].orEmpty()
}

private val srdJson = Json { ignoreUnknownKeys = true }

fun parseSrdCorpus(json: String): SrdCorpus = srdJson.decodeFromString(SrdCorpus.serializer(), json)

fun parseSrdQuickReference(json: String): SrdQuickReference =
    srdJson.decodeFromString(SrdQuickReference.serializer(), json)

/** One paragraph that matched an offline search, with its citation. */
data class SrdSearchHit(
    val version: SrdVersion,
    val page: Int,
    val heading: String?,
    val paragraph: String,
    val score: Int,
)

/**
 * Offline word search (P4): every word must appear in the paragraph, ignoring case and accents.
 * Paragraphs that contain the whole phrase, or start with a word, rank higher. Returns at most
 * [limit] hits, best first, then by version and page.
 */
fun searchSrd(corpora: List<SrdCorpus>, query: String, limit: Int = 30): List<SrdSearchHit> {
    val normalizedQuery = normalizeForSearch(query)
    val terms = normalizedQuery.split(' ').filter { it.length >= 2 }.distinct()
    if (terms.isEmpty()) return emptyList()
    val hits = mutableListOf<SrdSearchHit>()
    for (corpus in corpora) {
        val version = corpus.srdVersion
        for (page in corpus.pages) {
            var heading: String? = null
            for (paragraph in page.text.split('\n')) {
                val trimmed = paragraph.trim()
                if (trimmed.isEmpty()) continue
                if (looksLikeHeading(trimmed)) heading = trimmed
                val normalized = normalizeForSearch(trimmed)
                if (terms.all { normalized.contains(it) }) {
                    var score = terms.size
                    if (terms.size > 1 && normalized.contains(normalizedQuery)) score += 5
                    if (terms.any { normalized.startsWith(it) }) score += 3
                    if (looksLikeHeading(trimmed)) score += 2
                    hits += SrdSearchHit(version, page.page, heading?.takeIf { it != trimmed }, trimmed, score)
                }
            }
        }
    }
    return hits
        .sortedWith(compareByDescending<SrdSearchHit> { it.score }.thenBy { it.version.ordinal }.thenBy { it.page })
        .take(limit)
}

fun normalizeForSearch(text: String): String {
    val builder = StringBuilder(text.length)
    for (char in text.lowercase()) {
        builder.append(
            when (char) {
                'á', 'à', 'ä', 'â' -> 'a'
                'é', 'è', 'ë', 'ê' -> 'e'
                'í', 'ì', 'ï', 'î' -> 'i'
                'ó', 'ò', 'ö', 'ô' -> 'o'
                'ú', 'ù', 'ü', 'û' -> 'u'
                'ñ' -> 'n'
                else -> if (char.isLetterOrDigit()) char else ' '
            },
        )
    }
    return builder.toString().split(' ').filter { it.isNotEmpty() }.joinToString(" ")
}

private fun looksLikeHeading(paragraph: String): Boolean =
    paragraph.length <= 44 && paragraph[0].isUpperCase() && !paragraph.endsWith('.') && ". " !in paragraph
