package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/**
 * Bounded convenience metadata for canonical base classes whose spellcasting identity is known.
 *
 * This intentionally does not model spell lists, subclass progression, multiclass legality, or
 * other spell rules. Manual/custom classes remain valid and simply receive no automatic source.
 */
data class CharacterClassSpellcastingMetadata(
    val ability: CharacterAbility,
)

object CharacterSpellcastingCatalog {
    private val metadataByClassCatalogKey: Map<String, CharacterClassSpellcastingMetadata> = mapOf(
        "artificer-2025" to CharacterClassSpellcastingMetadata(CharacterAbility.INTELLIGENCE),
        "artificer-5e" to CharacterClassSpellcastingMetadata(CharacterAbility.INTELLIGENCE),
        "bard-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.CHARISMA),
        "cleric-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.WISDOM),
        "druid-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.WISDOM),
        "paladin-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.CHARISMA),
        "ranger-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.WISDOM),
        "sorcerer-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.CHARISMA),
        "warlock-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.CHARISMA),
        "wizard-2024" to CharacterClassSpellcastingMetadata(CharacterAbility.INTELLIGENCE),
    )

    fun metadataFor(classLevel: CharacterClassLevel): CharacterClassSpellcastingMetadata? {
        val key = classLevel.catalogKey ?: return null
        // Only keys that still exist in the canonical class catalog may bootstrap a source.
        if (CharacterClassCatalog.byKey(key) == null) return null
        return metadataByClassCatalogKey[key]
    }

    fun isCanonicalSpellcaster(classLevel: CharacterClassLevel): Boolean = metadataFor(classLevel) != null
}

data class CharacterSpellcastingBootstrapResult(
    val sources: List<CharacterSpellcastingSource>,
    val profiles: List<CharacterSpellcastingProfile>,
    val hasCanonicalSpellcastingClass: Boolean,
)

/**
 * Reconciles canonical spellcasting class ownership with the richer spell-source/profile overlay.
 *
 * Existing sources and profiles are preserved by identity. A canonical class only creates a source
 * when no CLASS source is already linked to that exact class instance. Existing configured profile
 * abilities are never overwritten. An unconfigured/missing profile receives the bounded catalog
 * ability while all adjustments and legacy overrides are retained.
 *
 * Manual/homebrew and non-class sources are retained verbatim. Removing a canonical class does not
 * destructively delete an old source here; the existing repository normalization remains the owner
 * of invalid-link cleanup, which preserves data rather than guessing at destructive migrations.
 */
fun reconcileCharacterSpellcastingBootstrap(
    classes: List<CharacterClassLevel>,
    existingSources: List<CharacterSpellcastingSource>,
    existingProfiles: List<CharacterSpellcastingProfile>,
    newSourceId: () -> Uuid = { Uuid.random() },
): CharacterSpellcastingBootstrapResult {
    val canonicalClasses = classes
        .sortedBy { it.sortOrder }
        .mapNotNull { classLevel ->
            CharacterSpellcastingCatalog.metadataFor(classLevel)?.let { metadata -> classLevel to metadata }
        }

    if (canonicalClasses.isEmpty()) {
        return CharacterSpellcastingBootstrapResult(
            sources = existingSources,
            profiles = existingProfiles,
            hasCanonicalSpellcastingClass = false,
        )
    }

    val sources = existingSources.toMutableList()
    val profiles = existingProfiles.toMutableList()
    val usedSourceIds = sources.mapTo(mutableSetOf()) { it.id }
    var nextSortOrder = (sources.maxOfOrNull { it.sortOrder } ?: -1) + 1

    fun allocateSourceId(): Uuid {
        repeat(100) {
            val candidate = newSourceId()
            if (usedSourceIds.add(candidate)) return candidate
        }
        error("Unable to allocate a distinct spellcasting source identity.")
    }

    canonicalClasses.forEach { (classLevel, metadata) ->
        val source = sources.firstOrNull { source ->
            source.originKind == CharacterSpellcastingOriginKind.CLASS && source.linkedClassId == classLevel.id
        } ?: CharacterSpellcastingSource(
            id = allocateSourceId(),
            name = classLevel.name.trim().ifBlank {
                CharacterClassCatalog.byKey(classLevel.catalogKey)?.nameEs ?: "Clase"
            },
            linkedClassId = classLevel.id,
            sortOrder = nextSortOrder++,
            originKind = CharacterSpellcastingOriginKind.CLASS,
            originReferenceId = null,
        ).also(sources::add)

        val profileIndex = profiles.indexOfFirst { it.sourceId == source.id }
        val defaultAbility = CharacterAbilityReference.builtIn(metadata.ability)
        if (profileIndex < 0) {
            profiles += CharacterSpellcastingProfile(
                sourceId = source.id,
                ability = defaultAbility,
            )
        } else {
            val current = profiles[profileIndex]
            if (!current.ability.configured) {
                profiles[profileIndex] = current.copy(ability = defaultAbility)
            }
        }
    }

    return CharacterSpellcastingBootstrapResult(
        sources = sources,
        profiles = profiles,
        hasCanonicalSpellcastingClass = true,
    )
}

fun suggestedCharacterSpellcasterEnabled(classes: List<CharacterClassLevel>): Boolean =
    classes.any(CharacterSpellcastingCatalog::isCanonicalSpellcaster)
