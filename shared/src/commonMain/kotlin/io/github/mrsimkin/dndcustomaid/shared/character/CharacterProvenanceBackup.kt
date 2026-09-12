package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/** Copies the P7 provenance slice from [source] onto this successor state. */
internal fun CharacterSuccessorState.withCharacterProvenanceFrom(
    source: CharacterSuccessorState,
): CharacterSuccessorState = copy(
    subclassIdentities = source.subclassIdentities,
    speciesIdentity = source.speciesIdentity,
    subraceIdentity = source.subraceIdentity,
    backgroundIdentity = source.backgroundIdentity,
    traitProvenance = source.traitProvenance,
)

/**
 * Restore-as-copy remapping for the P7 character-owned identity/provenance graph.
 *
 * Core import already remaps class and Trait ids. This layer gives every character-owned provenance
 * identity a fresh id too, then rewrites every structured provenance target to the imported graph.
 * Missing/deleted structured targets remain unresolved, but receive a fresh opaque id so a copied
 * character never reuses an identity from the source backup.
 */
internal fun remapCharacterProvenanceForImportedCopy(
    document: CharacterBackupDocument,
    plan: CharacterBackupImportPlan,
    idFactory: () -> Uuid,
): CharacterBackupImportPlan {
    val sourceSheet = document.character
    val importedSheet = plan.character
    val sourceState = document.successorState

    require(sourceSheet.classes.size == importedSheet.classes.size) {
        "Imported class structure must preserve source ordering for provenance remapping."
    }
    require(sourceSheet.traits.size == importedSheet.traits.size) {
        "Imported Trait structure must preserve source ordering for provenance remapping."
    }

    val classIds = sourceSheet.classes.zip(importedSheet.classes)
        .associate { (source, imported) -> source.id to imported.id }
    val traitIds = sourceSheet.traits.zip(importedSheet.traits)
        .associate { (source, imported) -> source.id to imported.id }

    val subclassIds = sourceState.subclassIdentities.associate { it.id to idFactory() }
    val speciesIds = sourceState.speciesIdentity?.let { mapOf(it.id to idFactory()) }.orEmpty()
    val subraceIds = sourceState.subraceIdentity?.let { mapOf(it.id to idFactory()) }.orEmpty()
    val backgroundIds = sourceState.backgroundIdentity?.let { mapOf(it.id to idFactory()) }.orEmpty()
    val unresolvedTargetIds = mutableMapOf<Uuid, Uuid>()

    fun remapStructuredTarget(kind: CharacterProvenanceKind, sourceId: Uuid): Uuid = when (kind) {
        CharacterProvenanceKind.CLASS -> classIds[sourceId]
        CharacterProvenanceKind.SUBCLASS -> subclassIds[sourceId]
        CharacterProvenanceKind.SPECIES_RACE -> speciesIds[sourceId]
        CharacterProvenanceKind.SUBRACE -> subraceIds[sourceId]
        CharacterProvenanceKind.BACKGROUND -> backgroundIds[sourceId]
        CharacterProvenanceKind.FEAT,
        CharacterProvenanceKind.GIFT_BLESSING,
        CharacterProvenanceKind.OTHER,
        -> null
    } ?: unresolvedTargetIds.getOrPut(sourceId, idFactory)

    val importedProvenance = sourceState.copy(
        subclassIdentities = sourceState.subclassIdentities.map { identity ->
            identity.copy(
                id = subclassIds.getValue(identity.id),
                parentClassId = classIds.getValue(identity.parentClassId),
            )
        },
        speciesIdentity = sourceState.speciesIdentity?.let { identity ->
            identity.copy(id = speciesIds.getValue(identity.id))
        },
        subraceIdentity = sourceState.subraceIdentity?.let { identity ->
            identity.copy(
                id = subraceIds.getValue(identity.id),
                parentSpeciesId = speciesIds[identity.parentSpeciesId]
                    ?: unresolvedTargetIds.getOrPut(identity.parentSpeciesId, idFactory),
            )
        },
        backgroundIdentity = sourceState.backgroundIdentity?.let { identity ->
            identity.copy(id = backgroundIds.getValue(identity.id))
        },
        traitProvenance = sourceState.traitProvenance.map { provenance ->
            val importedTraitId = requireNotNull(traitIds[provenance.traitId]) {
                "Trait provenance must reference a Trait contained in the backup."
            }
            provenance.copy(
                traitId = importedTraitId,
                targetId = if (provenance.kind.isStructuredCharacterProvenance()) {
                    provenance.targetId?.let { remapStructuredTarget(provenance.kind, it) }
                } else {
                    null
                },
            )
        },
    )

    return plan.copy(
        successorState = plan.successorState.withCharacterProvenanceFrom(importedProvenance),
    )
}
