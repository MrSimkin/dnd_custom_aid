package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

const val CHARACTER_ARTIFICE_FAVORITE_FILTER_KEY: String = "favorite"
const val CHARACTER_ARTIFICE_ACTIVE_FILTER_KEY: String = "active"
const val CHARACTER_ARTIFICE_PLAN_FILTER_KEY: String = "kind:ARTIFICER_PLAN"
const val CHARACTER_ARTIFICE_DEVICE_FILTER_KEY: String = "kind:ARTIFICER_DEVICE"

const val CHARACTER_FORM_FAVORITE_FILTER_KEY: String = "favorite"
private const val CHARACTER_FORM_SOURCE_FILTER_PREFIX = "source:"
private const val CHARACTER_FORM_EMPTY_SOURCE_KEY = "_none"

const val CHARACTER_CLASS_OPTION_FAVORITE_FILTER_KEY: String = "favorite"
const val CHARACTER_CLASS_OPTION_ACTIVE_FILTER_KEY: String = "active"
const val CHARACTER_PACT_CHOICE_FILTER_KEY: String = "kind:PACT_CHOICE"
const val CHARACTER_PACT_INVOCATION_FILTER_KEY: String = "kind:INVOCATION"
private const val CHARACTER_CLASS_OPTION_SOURCE_FILTER_PREFIX = "source:"
private const val CHARACTER_CLASS_OPTION_EMPTY_SOURCE_KEY = "_none"

private val ARTIFICE_OPTION_KINDS = setOf(
    CharacterClassOptionKind.ARTIFICER_PLAN,
    CharacterClassOptionKind.ARTIFICER_DEVICE,
)
private val TECHNIQUE_OPTION_KINDS = setOf(CharacterClassOptionKind.TECHNIQUE)
private val METAMAGIC_OPTION_KINDS = setOf(CharacterClassOptionKind.METAMAGIC)
private val PACT_OPTION_KINDS = setOf(
    CharacterClassOptionKind.INVOCATION,
    CharacterClassOptionKind.PACT_CHOICE,
)

fun isArtificeCharacterOption(option: CharacterClassOption): Boolean = option.kind in ARTIFICE_OPTION_KINDS

fun isTechniqueCharacterOption(option: CharacterClassOption): Boolean = option.kind in TECHNIQUE_OPTION_KINDS

fun isMetamagicCharacterOption(option: CharacterClassOption): Boolean = option.kind in METAMAGIC_OPTION_KINDS

fun isPactCharacterOption(option: CharacterClassOption): Boolean = option.kind in PACT_OPTION_KINDS

fun presentCharacterArtificeOptions(
    options: List<CharacterClassOption>,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    isFavorite: (CharacterClassOption) -> Boolean = { false },
): List<CharacterClassOption> = presentCharacterCollection(
    items = options.filter(::isArtificeCharacterOption),
    order = order,
    manualOrder = CharacterClassOption::sortOrder,
    label = CharacterClassOption::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { option ->
        listOf(
            option.name,
            option.source,
            option.costText,
            option.effectSummary,
            option.notes,
            characterClassOptionKindDisplayLabel(option.kind),
        )
    },
    filterMatches = { option, filters ->
        characterArtificeOptionFilterMatches(option, filters, isFavorite(option))
    },
)

fun characterArtificeOptionFilterMatches(
    option: CharacterClassOption,
    activeFilters: Set<String>,
    favorite: Boolean = false,
): Boolean {
    if (!isArtificeCharacterOption(option)) return false
    if (activeFilters.isEmpty()) return true

    val kindFilters = activeFilters.filterTo(mutableSetOf()) {
        it == CHARACTER_ARTIFICE_PLAN_FILTER_KEY || it == CHARACTER_ARTIFICE_DEVICE_FILTER_KEY
    }
    if (kindFilters.isNotEmpty()) {
        val ownKindKey = when (option.kind) {
            CharacterClassOptionKind.ARTIFICER_PLAN -> CHARACTER_ARTIFICE_PLAN_FILTER_KEY
            CharacterClassOptionKind.ARTIFICER_DEVICE -> CHARACTER_ARTIFICE_DEVICE_FILTER_KEY
            else -> return false
        }
        if (ownKindKey !in kindFilters) return false
    }

    if (CHARACTER_ARTIFICE_FAVORITE_FILTER_KEY in activeFilters && !favorite) return false
    if (CHARACTER_ARTIFICE_ACTIVE_FILTER_KEY in activeFilters && !option.active) return false
    return true
}

fun moveCharacterArtificeOptionManual(
    options: List<CharacterClassOption>,
    optionId: Uuid,
    offset: Int,
): List<CharacterClassOption> = moveCharacterClassOptionModuleManual(
    options = options,
    optionId = optionId,
    offset = offset,
    ownedKinds = ARTIFICE_OPTION_KINDS,
)

fun presentCharacterTechniqueOptions(
    options: List<CharacterClassOption>,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    isFavorite: (CharacterClassOption) -> Boolean = { false },
): List<CharacterClassOption> = presentCharacterClassOptionModuleOptions(
    options = options,
    ownedKinds = TECHNIQUE_OPTION_KINDS,
    order = order,
    query = query,
    isFavorite = isFavorite,
)

fun presentCharacterMetamagicOptions(
    options: List<CharacterClassOption>,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    isFavorite: (CharacterClassOption) -> Boolean = { false },
): List<CharacterClassOption> = presentCharacterClassOptionModuleOptions(
    options = options,
    ownedKinds = METAMAGIC_OPTION_KINDS,
    order = order,
    query = query,
    isFavorite = isFavorite,
)

fun presentCharacterPactOptions(
    options: List<CharacterClassOption>,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    isFavorite: (CharacterClassOption) -> Boolean = { false },
): List<CharacterClassOption> = presentCharacterClassOptionModuleOptions(
    options = options,
    ownedKinds = PACT_OPTION_KINDS,
    order = order,
    query = query,
    isFavorite = isFavorite,
    kindFilterKeys = mapOf(
        CharacterClassOptionKind.PACT_CHOICE to CHARACTER_PACT_CHOICE_FILTER_KEY,
        CharacterClassOptionKind.INVOCATION to CHARACTER_PACT_INVOCATION_FILTER_KEY,
    ),
)

fun characterClassOptionSourceFilterKey(source: String?): String =
    "$CHARACTER_CLASS_OPTION_SOURCE_FILTER_PREFIX${normalizeCharacterSearchText(source.orEmpty()).ifBlank { CHARACTER_CLASS_OPTION_EMPTY_SOURCE_KEY }}"

fun moveCharacterTechniqueOptionManual(
    options: List<CharacterClassOption>,
    optionId: Uuid,
    offset: Int,
): List<CharacterClassOption> = moveCharacterClassOptionModuleManual(
    options = options,
    optionId = optionId,
    offset = offset,
    ownedKinds = TECHNIQUE_OPTION_KINDS,
)

fun moveCharacterMetamagicOptionManual(
    options: List<CharacterClassOption>,
    optionId: Uuid,
    offset: Int,
): List<CharacterClassOption> = moveCharacterClassOptionModuleManual(
    options = options,
    optionId = optionId,
    offset = offset,
    ownedKinds = METAMAGIC_OPTION_KINDS,
)

fun moveCharacterPactOptionManual(
    options: List<CharacterClassOption>,
    optionId: Uuid,
    offset: Int,
): List<CharacterClassOption> = moveCharacterClassOptionModuleManual(
    options = options,
    optionId = optionId,
    offset = offset,
    ownedKinds = PACT_OPTION_KINDS,
)

fun moveCharacterClassOptionModuleManual(
    options: List<CharacterClassOption>,
    optionId: Uuid,
    offset: Int,
    ownedKinds: Set<CharacterClassOptionKind>,
): List<CharacterClassOption> {
    val ordered = options.sortedWith(
        compareBy<CharacterClassOption> { it.sortOrder }
            .thenBy { it.id.toString() },
    )
    if (offset == 0 || ordered.size < 2 || ownedKinds.isEmpty()) {
        return normalizeCharacterClassOptionOrders(ordered)
    }
    if (ordered.none { it.id == optionId && it.kind in ownedKinds }) {
        return normalizeCharacterClassOptionOrders(ordered)
    }

    val visiblePositions = ordered.indices.filter { index -> ordered[index].kind in ownedKinds }
    val visibleItems = visiblePositions.map(ordered::get).toMutableList()
    val visibleIndex = visibleItems.indexOfFirst { it.id == optionId }
    val target = visibleIndex + offset
    if (visibleIndex < 0 || target !in visibleItems.indices) {
        return normalizeCharacterClassOptionOrders(ordered)
    }

    val moved = visibleItems.removeAt(visibleIndex)
    visibleItems.add(target, moved)
    val replacementByPosition = visiblePositions.zip(visibleItems).toMap()
    return ordered
        .mapIndexed { index, option -> replacementByPosition[index] ?: option }
        .mapIndexed { index, option -> option.copy(sortOrder = index) }
}

fun normalizeCharacterClassOptionOrders(options: List<CharacterClassOption>): List<CharacterClassOption> =
    options.sortedWith(
        compareBy<CharacterClassOption> { it.sortOrder }
            .thenBy { it.id.toString() },
    ).mapIndexed { index, option -> option.copy(sortOrder = index) }

fun duplicateCharacterClassOption(
    source: CharacterClassOption,
    newId: Uuid,
    sortOrder: Int,
): CharacterClassOption = source.copy(
    id = newId,
    name = source.name.trim().let { if (it.isEmpty()) "Copia" else "$it (copia)" },
    sortOrder = sortOrder,
)

fun nextCharacterClassOptionSortOrder(options: List<CharacterClassOption>): Int =
    options.maxOfOrNull(CharacterClassOption::sortOrder)?.plus(1) ?: 0

fun characterClassOptionKindDisplayLabel(kind: CharacterClassOptionKind): String = when (kind) {
    CharacterClassOptionKind.ARTIFICER_PLAN -> "Plan"
    CharacterClassOptionKind.ARTIFICER_DEVICE -> "Dispositivo"
    CharacterClassOptionKind.SUBCLASS_STATE -> "Estado de subclase"
    CharacterClassOptionKind.TECHNIQUE -> "Técnica"
    CharacterClassOptionKind.METAMAGIC -> "Metamagia"
    CharacterClassOptionKind.INVOCATION -> "Invocación"
    CharacterClassOptionKind.PACT_CHOICE -> "Pacto / elección"
    CharacterClassOptionKind.OTHER -> "Otro"
}

fun characterArtificeOptionKindDisplayLabel(kind: CharacterClassOptionKind): String =
    characterClassOptionKindDisplayLabel(kind)

private fun presentCharacterClassOptionModuleOptions(
    options: List<CharacterClassOption>,
    ownedKinds: Set<CharacterClassOptionKind>,
    order: CharacterPresentationOrder,
    query: CharacterCollectionQuery,
    isFavorite: (CharacterClassOption) -> Boolean,
    kindFilterKeys: Map<CharacterClassOptionKind, String> = emptyMap(),
): List<CharacterClassOption> = presentCharacterCollection(
    items = options.filter { option -> option.kind in ownedKinds },
    order = order,
    manualOrder = CharacterClassOption::sortOrder,
    label = CharacterClassOption::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { option ->
        listOf(
            option.name,
            option.source,
            option.costText,
            option.effectSummary,
            option.notes,
            characterClassOptionKindDisplayLabel(option.kind),
        )
    },
    filterMatches = { option, activeFilters ->
        characterClassOptionModuleFilterMatches(
            option = option,
            ownedKinds = ownedKinds,
            activeFilters = activeFilters,
            favorite = isFavorite(option),
            kindFilterKeys = kindFilterKeys,
        )
    },
)

private fun characterClassOptionModuleFilterMatches(
    option: CharacterClassOption,
    ownedKinds: Set<CharacterClassOptionKind>,
    activeFilters: Set<String>,
    favorite: Boolean,
    kindFilterKeys: Map<CharacterClassOptionKind, String>,
): Boolean {
    if (option.kind !in ownedKinds) return false
    if (activeFilters.isEmpty()) return true

    if (CHARACTER_CLASS_OPTION_FAVORITE_FILTER_KEY in activeFilters && !favorite) return false
    if (CHARACTER_CLASS_OPTION_ACTIVE_FILTER_KEY in activeFilters && !option.active) return false

    val sourceFilters = activeFilters.filterTo(mutableSetOf()) {
        it.startsWith(CHARACTER_CLASS_OPTION_SOURCE_FILTER_PREFIX)
    }
    if (sourceFilters.isNotEmpty() && characterClassOptionSourceFilterKey(option.source) !in sourceFilters) {
        return false
    }

    val kindFilters = activeFilters.filterTo(mutableSetOf()) { it in kindFilterKeys.values }
    if (kindFilters.isNotEmpty() && kindFilterKeys[option.kind] !in kindFilters) return false
    return true
}

fun characterFormSourceFilterKey(source: String?): String =
    "$CHARACTER_FORM_SOURCE_FILTER_PREFIX${normalizeCharacterSearchText(source.orEmpty()).ifBlank { CHARACTER_FORM_EMPTY_SOURCE_KEY }}"

fun presentCharacterForms(
    forms: List<CharacterForm>,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    isFavorite: (CharacterForm) -> Boolean = { false },
): List<CharacterForm> = presentCharacterCollection(
    items = forms,
    order = order,
    manualOrder = CharacterForm::sortOrder,
    label = CharacterForm::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { form ->
        listOf(
            form.name,
            form.source,
            form.challengeRatingText,
            form.movement,
            form.senses,
            form.actionSummary,
            form.notes,
        )
    },
    filterMatches = { form, filters ->
        characterFormFilterMatches(form, filters, isFavorite(form))
    },
)

fun characterFormFilterMatches(
    form: CharacterForm,
    activeFilters: Set<String>,
    favorite: Boolean = false,
): Boolean {
    if (activeFilters.isEmpty()) return true
    if (CHARACTER_FORM_FAVORITE_FILTER_KEY in activeFilters && !favorite) return false

    val sourceFilters = activeFilters.filterTo(mutableSetOf()) {
        it.startsWith(CHARACTER_FORM_SOURCE_FILTER_PREFIX)
    }
    if (sourceFilters.isNotEmpty() && characterFormSourceFilterKey(form.source) !in sourceFilters) return false
    return true
}

fun moveCharacterFormManual(
    forms: List<CharacterForm>,
    formId: Uuid,
    offset: Int,
): List<CharacterForm> {
    val ordered = forms.sortedWith(
        compareBy<CharacterForm> { it.sortOrder }
            .thenBy { it.id.toString() },
    )
    if (offset == 0 || ordered.size < 2) return normalizeCharacterFormOrders(ordered)
    val index = ordered.indexOfFirst { it.id == formId }
    val target = index + offset
    if (index < 0 || target !in ordered.indices) return normalizeCharacterFormOrders(ordered)

    val mutable = ordered.toMutableList()
    val moved = mutable.removeAt(index)
    mutable.add(target, moved)
    return mutable.mapIndexed { newIndex, form -> form.copy(sortOrder = newIndex) }
}

fun normalizeCharacterFormOrders(forms: List<CharacterForm>): List<CharacterForm> =
    forms.sortedWith(
        compareBy<CharacterForm> { it.sortOrder }
            .thenBy { it.id.toString() },
    ).mapIndexed { index, form -> form.copy(sortOrder = index) }

fun duplicateCharacterForm(
    source: CharacterForm,
    newId: Uuid,
    sortOrder: Int,
): CharacterForm = source.copy(
    id = newId,
    name = source.name.trim().let { if (it.isEmpty()) "Copia" else "$it (copia)" },
    sortOrder = sortOrder,
)

fun nextCharacterFormSortOrder(forms: List<CharacterForm>): Int =
    forms.maxOfOrNull(CharacterForm::sortOrder)?.plus(1) ?: 0
