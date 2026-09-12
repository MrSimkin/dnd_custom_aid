package io.github.mrsimkin.dndcustomaid.shared.character

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/** Gameplay provenance domain. Publication/book metadata deliberately does not belong here. */
@Serializable
enum class CharacterProvenanceKind {
    CLASS,
    SUBCLASS,
    SPECIES_RACE,
    SUBRACE,
    BACKGROUND,
    FEAT,
    GIFT_BLESSING,
    OTHER,
}

/**
 * Stable character-owned subclass acquisition.
 *
 * The parent class remains authoritative for the subclass name/catalog metadata. Keeping only a
 * stable acquisition id plus the parent class id avoids copying the display name while allowing a
 * dependent Trait to survive removal/replacement as an unresolved reference.
 */
@Serializable
data class CharacterOwnedSubclassIdentity(
    val id: Uuid,
    val parentClassId: Uuid,
    val definitionKey: String? = null,
)

/** Character-owned species/race identity. Custom and catalog-backed species use this same schema. */
@Serializable
data class CharacterOwnedSpeciesIdentity(
    val id: Uuid,
    val name: String,
    val definitionKey: String? = null,
    val rulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
)

/**
 * Character-owned child/variant of a species. The normal Player UI calls this `Subraza`, while the
 * model deliberately remains source-neutral enough for lineage/ancestry/legacy/custom variants.
 */
@Serializable
data class CharacterOwnedSubraceIdentity(
    val id: Uuid,
    val parentSpeciesId: Uuid,
    val name: String,
    val definitionKey: String? = null,
    val rulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
)

/** Character-owned background identity. Custom and catalog-backed backgrounds share this schema. */
@Serializable
data class CharacterOwnedBackgroundIdentity(
    val id: Uuid,
    val name: String,
    val definitionKey: String? = null,
    val rulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
)

/**
 * One Trait character instance -> one gameplay provenance relationship.
 *
 * Structured kinds use [targetId]. FEAT/GIFT_BLESSING/OTHER may keep [freeText] during this repair
 * and are intentionally ready for a future owned-entity target without changing this relationship
 * shape. [legacyText] preserves pre-P7 source text when automatic matching is unsafe or impossible.
 */
@Serializable
data class CharacterTraitProvenance(
    val traitId: Uuid,
    val kind: CharacterProvenanceKind,
    val targetId: Uuid? = null,
    val freeText: String? = null,
    val legacyText: String? = null,
)

@Serializable
data class CharacterProvenanceOption(
    val kind: CharacterProvenanceKind,
    val targetId: Uuid,
    val label: String,
    val parentLabel: String? = null,
) {
    val disambiguatedLabel: String
        get() = parentLabel?.takeIf { it.isNotBlank() }?.let { "$label ($it)" } ?: label
}

enum class CharacterProvenanceResolutionStatus {
    RESOLVED,
    UNRESOLVED,
    FREE_TEXT,
}

data class CharacterProvenanceResolution(
    val provenance: CharacterTraitProvenance,
    val status: CharacterProvenanceResolutionStatus,
    val label: String,
    val parentLabel: String? = null,
)

fun CharacterProvenanceKind.isStructuredCharacterProvenance(): Boolean = when (this) {
    CharacterProvenanceKind.CLASS,
    CharacterProvenanceKind.SUBCLASS,
    CharacterProvenanceKind.SPECIES_RACE,
    CharacterProvenanceKind.SUBRACE,
    CharacterProvenanceKind.BACKGROUND,
    -> true

    CharacterProvenanceKind.FEAT,
    CharacterProvenanceKind.GIFT_BLESSING,
    CharacterProvenanceKind.OTHER,
    -> false
}

fun CharacterTraitType.defaultCharacterProvenanceKind(): CharacterProvenanceKind = when (this) {
    CharacterTraitType.CLASS -> CharacterProvenanceKind.CLASS
    CharacterTraitType.SPECIES_RACE -> CharacterProvenanceKind.SPECIES_RACE
    CharacterTraitType.BACKGROUND -> CharacterProvenanceKind.BACKGROUND
    CharacterTraitType.FEAT -> CharacterProvenanceKind.FEAT
    CharacterTraitType.GIFT_BLESSING -> CharacterProvenanceKind.GIFT_BLESSING
    CharacterTraitType.OTHER -> CharacterProvenanceKind.OTHER
}

/** Eligible provenance targets are only entities this PC actually owns/has registered. */
fun characterProvenanceOptions(
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    kind: CharacterProvenanceKind,
): List<CharacterProvenanceOption> = when (kind) {
    CharacterProvenanceKind.CLASS -> sheet.classes
        .sortedBy { it.sortOrder }
        .map { classLevel ->
            CharacterProvenanceOption(
                kind = kind,
                targetId = classLevel.id,
                label = classLevel.name.trim(),
            )
        }

    CharacterProvenanceKind.SUBCLASS -> successorState.subclassIdentities
        .mapNotNull { identity ->
            val parent = sheet.classes.firstOrNull { it.id == identity.parentClassId } ?: return@mapNotNull null
            val subclassName = parent.subclassName?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            CharacterProvenanceOption(
                kind = kind,
                targetId = identity.id,
                label = subclassName,
                parentLabel = parent.name.trim(),
            )
        }
        .sortedWith(compareBy<CharacterProvenanceOption> { it.parentLabel.orEmpty().lowercase() }.thenBy { it.label.lowercase() })

    CharacterProvenanceKind.SPECIES_RACE -> successorState.speciesIdentity
        ?.takeIf { it.name.isNotBlank() }
        ?.let { listOf(CharacterProvenanceOption(kind, it.id, it.name.trim())) }
        .orEmpty()

    CharacterProvenanceKind.SUBRACE -> successorState.subraceIdentity
        ?.takeIf { subrace ->
            subrace.name.isNotBlank() && successorState.speciesIdentity?.id == subrace.parentSpeciesId
        }
        ?.let { subrace ->
            listOf(
                CharacterProvenanceOption(
                    kind = kind,
                    targetId = subrace.id,
                    label = subrace.name.trim(),
                    parentLabel = successorState.speciesIdentity?.name?.trim(),
                ),
            )
        }
        .orEmpty()

    CharacterProvenanceKind.BACKGROUND -> successorState.backgroundIdentity
        ?.takeIf { it.name.isNotBlank() }
        ?.let { listOf(CharacterProvenanceOption(kind, it.id, it.name.trim())) }
        .orEmpty()

    CharacterProvenanceKind.FEAT,
    CharacterProvenanceKind.GIFT_BLESSING,
    CharacterProvenanceKind.OTHER,
    -> emptyList()
}

/** Exactly one eligible canonical target is selected automatically; zero/many require no guess. */
fun soleCharacterProvenanceTargetOrNull(
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    kind: CharacterProvenanceKind,
): Uuid? = characterProvenanceOptions(sheet, successorState, kind)
    .singleOrNull()
    ?.targetId

fun resolveCharacterTraitProvenance(
    provenance: CharacterTraitProvenance,
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
): CharacterProvenanceResolution {
    if (!provenance.kind.isStructuredCharacterProvenance()) {
        val text = provenance.freeText?.trim().orEmpty()
        return CharacterProvenanceResolution(
            provenance = provenance,
            status = CharacterProvenanceResolutionStatus.FREE_TEXT,
            label = text.ifBlank { provenance.legacyText?.trim().orEmpty() },
        )
    }

    val option = provenance.targetId?.let { targetId ->
        characterProvenanceOptions(sheet, successorState, provenance.kind)
            .firstOrNull { it.targetId == targetId }
    }
    if (option != null) {
        return CharacterProvenanceResolution(
            provenance = provenance,
            status = CharacterProvenanceResolutionStatus.RESOLVED,
            label = option.label,
            parentLabel = option.parentLabel,
        )
    }

    return CharacterProvenanceResolution(
        provenance = provenance,
        status = CharacterProvenanceResolutionStatus.UNRESOLVED,
        label = provenance.legacyText?.trim().orEmpty(),
    )
}

/**
 * Conservative one-time migration from the old copied `CharacterTrait.source` string.
 *
 * Structured data links only when exactly one owned target matches. Global catalog knowledge is
 * deliberately irrelevant. Ambiguous/unmatched text remains preserved in [legacyText].
 */
fun migrateLegacyCharacterTraitProvenance(
    trait: CharacterTrait,
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
): CharacterTraitProvenance {
    val legacy = trait.source.trim().takeIf { it.isNotEmpty() }
    val defaultKind = trait.type.defaultCharacterProvenanceKind()

    if (!defaultKind.isStructuredCharacterProvenance()) {
        return CharacterTraitProvenance(
            traitId = trait.id,
            kind = defaultKind,
            freeText = legacy,
            legacyText = legacy,
        )
    }

    val candidateKinds = when (defaultKind) {
        CharacterProvenanceKind.CLASS -> listOf(CharacterProvenanceKind.CLASS, CharacterProvenanceKind.SUBCLASS)
        CharacterProvenanceKind.SPECIES_RACE -> listOf(CharacterProvenanceKind.SPECIES_RACE, CharacterProvenanceKind.SUBRACE)
        else -> listOf(defaultKind)
    }
    val candidates = candidateKinds.flatMap { kind -> characterProvenanceOptions(sheet, successorState, kind) }
    val matched = legacy?.let { text ->
        candidates.filter { option ->
            option.label.equals(text, ignoreCase = true) ||
                option.disambiguatedLabel.equals(text, ignoreCase = true)
        }.singleOrNull()
    }

    return CharacterTraitProvenance(
        traitId = trait.id,
        kind = matched?.kind ?: defaultKind,
        targetId = matched?.targetId,
        legacyText = legacy,
    )
}

/** Derive parent provenance instead of storing redundant parent links on the dependent Trait. */
fun characterProvenanceParentOption(
    provenance: CharacterTraitProvenance,
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
): CharacterProvenanceOption? = when (provenance.kind) {
    CharacterProvenanceKind.SUBCLASS -> provenance.targetId
        ?.let { id -> successorState.subclassIdentities.firstOrNull { it.id == id } }
        ?.let { identity ->
            sheet.classes.firstOrNull { it.id == identity.parentClassId }
        }
        ?.let { parent -> CharacterProvenanceOption(CharacterProvenanceKind.CLASS, parent.id, parent.name.trim()) }

    CharacterProvenanceKind.SUBRACE -> provenance.targetId
        ?.let { id -> successorState.subraceIdentity?.takeIf { it.id == id } }
        ?.let { child -> successorState.speciesIdentity?.takeIf { it.id == child.parentSpeciesId } }
        ?.let { parent -> CharacterProvenanceOption(CharacterProvenanceKind.SPECIES_RACE, parent.id, parent.name.trim()) }

    else -> null
}
