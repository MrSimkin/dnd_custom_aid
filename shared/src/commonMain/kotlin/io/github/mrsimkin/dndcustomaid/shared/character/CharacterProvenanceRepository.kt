package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/**
 * Additive persistence boundary for P7 character-owned identity and gameplay provenance.
 *
 * Target ids are deliberately soft references. Source deletion/replacement therefore leaves a
 * dependent Trait unresolved instead of cascading, remapping, or destroying the relationship.
 */
class CharacterProvenanceRepository(
    private val database: AppDatabase,
) {
    private val characters = CharacterRepository(database)

    fun state(
        characterId: Uuid,
        baseState: CharacterSuccessorState = CharacterSuccessorState(),
    ): CharacterSuccessorState {
        val sheet = requireNotNull(characters.character(characterId)) {
            "Character must already exist locally."
        }
        val id = characterId.toString()

        val storedSubclassIdentities = database.characterProvenanceQueries.selectSubclassIdentities(id) {
                rowId, _, parentClassId, definitionKey ->
            CharacterOwnedSubclassIdentity(
                id = Uuid.parse(rowId),
                parentClassId = Uuid.parse(parentClassId),
                definitionKey = definitionKey,
            )
        }.executeAsList()

        val subclassIdentities = sheet.classes
            .filter { !it.subclassName.isNullOrBlank() }
            .map { classLevel ->
                val existing = storedSubclassIdentities.firstOrNull { it.parentClassId == classLevel.id }
                if (existing != null && existing.definitionKey == classLevel.subclassCatalogKey) {
                    existing
                } else {
                    CharacterOwnedSubclassIdentity(
                        id = Uuid.random(),
                        parentClassId = classLevel.id,
                        definitionKey = classLevel.subclassCatalogKey,
                    )
                }
            }

        val storedSpecies = database.characterProvenanceQueries.selectSpeciesIdentity(id) {
                _, rowId, name, definitionKey, rulesFamily ->
            CharacterOwnedSpeciesIdentity(
                id = Uuid.parse(rowId),
                name = name,
                definitionKey = definitionKey,
                rulesFamily = enumOrDefault(rulesFamily, CharacterRulesFamily.UNSPECIFIED),
            )
        }.executeAsOneOrNull()
        val currentRaceName = sheet.background.race.trim().takeIf { it.isNotEmpty() }
        val speciesIdentity = currentRaceName?.let { name ->
            storedSpecies?.copy(name = name)
                ?: CharacterOwnedSpeciesIdentity(id = Uuid.random(), name = name)
        }

        val storedSubrace = database.characterProvenanceQueries.selectSubraceIdentity(id) {
                _, rowId, parentSpeciesId, name, definitionKey, rulesFamily ->
            CharacterOwnedSubraceIdentity(
                id = Uuid.parse(rowId),
                parentSpeciesId = Uuid.parse(parentSpeciesId),
                name = name,
                definitionKey = definitionKey,
                rulesFamily = enumOrDefault(rulesFamily, CharacterRulesFamily.UNSPECIFIED),
            )
        }.executeAsOneOrNull()
        val subraceIdentity = storedSubrace?.takeIf { identity ->
            speciesIdentity != null && identity.parentSpeciesId == speciesIdentity.id
        }

        val storedBackground = database.characterProvenanceQueries.selectBackgroundIdentity(id) {
                _, rowId, name, definitionKey, rulesFamily ->
            CharacterOwnedBackgroundIdentity(
                id = Uuid.parse(rowId),
                name = name,
                definitionKey = definitionKey,
                rulesFamily = enumOrDefault(rulesFamily, CharacterRulesFamily.UNSPECIFIED),
            )
        }.executeAsOneOrNull()
        val currentBackgroundName = sheet.background.name.trim().takeIf { it.isNotEmpty() }
        val backgroundIdentity = currentBackgroundName?.let { name ->
            storedBackground?.copy(name = name)
                ?: CharacterOwnedBackgroundIdentity(id = Uuid.random(), name = name)
        }

        var projected = baseState.copy(
            subclassIdentities = subclassIdentities,
            speciesIdentity = speciesIdentity,
            subraceIdentity = subraceIdentity,
            backgroundIdentity = backgroundIdentity,
        )

        val storedProvenance = database.characterProvenanceQueries.selectTraitProvenance(id) {
                traitId, _, kind, targetId, freeText, legacyText ->
            val parsedTraitId = Uuid.parse(traitId)
            parsedTraitId to CharacterTraitProvenance(
                traitId = parsedTraitId,
                kind = enumOrDefault(kind, CharacterProvenanceKind.OTHER),
                targetId = targetId?.let(Uuid::parse),
                freeText = freeText,
                legacyText = legacyText,
            )
        }.executeAsList().toMap()

        val traitProvenance = sheet.traits.map { trait ->
            storedProvenance[trait.id] ?: migrateLegacyCharacterTraitProvenance(
                trait = trait,
                sheet = sheet,
                successorState = projected,
            )
        }
        projected = projected.copy(traitProvenance = traitProvenance)

        // Persist bootstrap/reconciliation results immediately so generated owned identities,
        // canonical renames/deletions and migration choices are stable across reopen.
        val requiresBootstrapWrite =
            subclassIdentities != storedSubclassIdentities ||
                speciesIdentity != storedSpecies ||
                subraceIdentity != storedSubrace ||
                backgroundIdentity != storedBackground ||
                traitProvenance.size != storedProvenance.size ||
                traitProvenance.any { storedProvenance[it.traitId] != it }
        if (requiresBootstrapWrite) {
            persist(characterId, projected)
        }

        return projected
    }

    fun saveState(characterId: Uuid, state: CharacterSuccessorState): CharacterSuccessorState {
        validate(characterId, state)
        persist(characterId, state)
        return state(characterId, state)
    }

    private fun persist(characterId: Uuid, state: CharacterSuccessorState) {
        val id = characterId.toString()
        database.transaction {
            database.characterProvenanceQueries.deleteTraitProvenance(id)
            database.characterProvenanceQueries.deleteSubclassIdentities(id)
            database.characterProvenanceQueries.deleteSubraceIdentity(id)
            database.characterProvenanceQueries.deleteSpeciesIdentity(id)
            database.characterProvenanceQueries.deleteBackgroundIdentity(id)

            state.subclassIdentities.forEach { item ->
                database.characterProvenanceQueries.insertSubclassIdentity(
                    id = item.id.toString(),
                    character_id = id,
                    parent_class_id = item.parentClassId.toString(),
                    definition_key = item.definitionKey,
                )
            }
            state.speciesIdentity?.let { item ->
                database.characterProvenanceQueries.upsertSpeciesIdentity(
                    character_id = id,
                    id = item.id.toString(),
                    name = item.name.trim(),
                    definition_key = item.definitionKey,
                    rules_family = item.rulesFamily.name,
                )
            }
            state.subraceIdentity?.let { item ->
                database.characterProvenanceQueries.upsertSubraceIdentity(
                    character_id = id,
                    id = item.id.toString(),
                    parent_species_id = item.parentSpeciesId.toString(),
                    name = item.name.trim(),
                    definition_key = item.definitionKey,
                    rules_family = item.rulesFamily.name,
                )
            }
            state.backgroundIdentity?.let { item ->
                database.characterProvenanceQueries.upsertBackgroundIdentity(
                    character_id = id,
                    id = item.id.toString(),
                    name = item.name.trim(),
                    definition_key = item.definitionKey,
                    rules_family = item.rulesFamily.name,
                )
            }
            state.traitProvenance.forEach { item ->
                database.characterProvenanceQueries.insertTraitProvenance(
                    trait_id = item.traitId.toString(),
                    character_id = id,
                    provenance_kind = item.kind.name,
                    target_id = item.targetId?.toString(),
                    free_text = item.freeText?.trim()?.takeIf { it.isNotEmpty() },
                    legacy_text = item.legacyText?.trim()?.takeIf { it.isNotEmpty() },
                )
            }
        }
    }

    private fun validate(characterId: Uuid, state: CharacterSuccessorState) {
        val sheet = requireNotNull(characters.character(characterId)) {
            "Character must already exist locally."
        }
        require(state.subclassIdentities.map { it.id }.distinct().size == state.subclassIdentities.size) {
            "Subclass identities must have distinct identity."
        }
        require(state.subclassIdentities.map { it.parentClassId }.distinct().size == state.subclassIdentities.size) {
            "Each character class may own at most one current subclass identity."
        }
        val classIds = sheet.classes.mapTo(mutableSetOf()) { it.id }
        state.subclassIdentities.forEach { identity ->
            require(identity.parentClassId in classIds) {
                "Current subclass identity must belong to a class owned by this character."
            }
        }

        state.speciesIdentity?.let { identity ->
            require(identity.name.trim().isNotEmpty()) { "Species/race identity name must not be blank." }
        }
        state.subraceIdentity?.let { identity ->
            require(identity.name.trim().isNotEmpty()) { "Subrace identity name must not be blank." }
            state.speciesIdentity?.let { species ->
                require(identity.parentSpeciesId == species.id) {
                    "Subrace identity must belong to this character's current species/race identity."
                }
            }
        }
        state.backgroundIdentity?.let { identity ->
            require(identity.name.trim().isNotEmpty()) { "Background identity name must not be blank." }
        }

        val traitIds = sheet.traits.mapTo(mutableSetOf()) { it.id }
        require(state.traitProvenance.map { it.traitId }.distinct().size == state.traitProvenance.size) {
            "Each Trait instance may have at most one provenance relationship."
        }
        state.traitProvenance.forEach { provenance ->
            require(provenance.traitId in traitIds) {
                "Trait provenance must belong to a Trait owned by this character."
            }
            if (provenance.kind.isStructuredCharacterProvenance()) {
                require(provenance.freeText.isNullOrBlank()) {
                    "Structured Trait provenance cannot use arbitrary free text."
                }
            }
        }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(raw: String, fallback: T): T =
        enumValues<T>().firstOrNull { it.name == raw } ?: fallback
}
