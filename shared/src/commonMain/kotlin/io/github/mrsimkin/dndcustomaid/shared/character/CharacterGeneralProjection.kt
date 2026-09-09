package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Canonical, presentation-only projections consumed by the successor General surface.
 *
 * These helpers never persist a second copy of character data. They select or calculate from the
 * existing core / closure / successor aggregates so General and the operational tabs can share one
 * authoritative state.
 */
data class CharacterGeneralResourceRow(
    val resource: CharacterResource,
    val configuration: CharacterResourceSuccessorConfiguration,
)

data class CharacterGeneralSpellcastingRow(
    val source: CharacterSpellcastingSource,
    val profile: CharacterSpellcastingProfile?,
    val saveDc: Int?,
    val spellAttackModifier: Int?,
)

fun CharacterSheet.generalLanguages(): List<CharacterProficiency> =
    proficiencies
        .filter { it.type == CharacterProficiencyType.LANGUAGE }
        .sortedWith(compareBy<CharacterProficiency> { it.sortOrder }.thenBy { it.name.lowercase() })

/**
 * The current inventory schema has an equipped flag but no armor/shield category. Until the
 * Equipment domain gains a typed category, General may safely surface equipped item references but
 * must not guess armor/shield identity from free-text names.
 */
fun CharacterSheet.generalEquippedItemReferences(): List<CharacterInventoryItem> =
    inventoryItems
        .filter { it.equipped }
        .sortedWith(compareBy<CharacterInventoryItem> { it.sortOrder }.thenBy { it.name.lowercase() })

fun CharacterSheet.generalResources(successorState: CharacterSuccessorState): List<CharacterGeneralResourceRow> {
    val configurationById = successorState.resourceConfigurations.associateBy { it.resourceId }
    return resources.mapNotNull { resource ->
        val configuration = configurationById[resource.id] ?: return@mapNotNull null
        if (CharacterResourcePlacement.GENERAL !in configuration.placements) return@mapNotNull null
        CharacterGeneralResourceRow(resource, configuration)
    }
}

fun CharacterSheet.generalSpellcastingRows(successorState: CharacterSuccessorState): List<CharacterGeneralSpellcastingRow> {
    val profileBySource = successorState.spellcastingProfiles.associateBy { it.sourceId }
    return spellcastingSources.sortedBy { it.sortOrder }.map { source ->
        val profile = profileBySource[source.id]
        CharacterGeneralSpellcastingRow(
            source = source,
            profile = profile,
            saveDc = profile?.let { spellSaveDc(it, successorState) },
            spellAttackModifier = profile?.let { spellAttackModifier(it, successorState) },
        )
    }
}
