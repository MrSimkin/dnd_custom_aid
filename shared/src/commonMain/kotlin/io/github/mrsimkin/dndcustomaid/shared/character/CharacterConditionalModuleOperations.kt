package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

const val CHARACTER_ARTIFICE_FAVORITE_FILTER_KEY: String = "favorite"
const val CHARACTER_ARTIFICE_ACTIVE_FILTER_KEY: String = "active"
const val CHARACTER_ARTIFICE_PLAN_FILTER_KEY: String = "kind:ARTIFICER_PLAN"
const val CHARACTER_ARTIFICE_DEVICE_FILTER_KEY: String = "kind:ARTIFICER_DEVICE"

const val CHARACTER_FORM_FAVORITE_FILTER_KEY: String = "favorite"
private const val CHARACTER_FORM_SOURCE_FILTER_PREFIX = "source:"
private const val CHARACTER_FORM_EMPTY_SOURCE_KEY = "_none"

fun isArtificeCharacterOption(option: CharacterClassOption): Boolean =
    option.kind == CharacterClassOptionKind.ARTIFICER_PLAN ||
        option.kind == CharacterClassOptionKind.ARTIFICER_DEVICE

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
): List<CharacterClassOption> {
    val ordered = options.sortedWith(
        compareBy<CharacterClassOption> { it.sortOrder }
            .thenBy { it.id.toString() },
    )
    if (offset == 0 || ordered.size < 2) return normalizeCharacterClassOptionOrders(ordered)
    if (ordered.none { it.id == optionId && isArtificeCharacterOption(it) }) {
        return normalizeCharacterClassOptionOrders(ordered)
    }

    val visiblePositions = ordered.indices.filter { index -> isArtificeCharacterOption(ordered[index]) }
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
    CharacterClassOptionKind.OTHER -> "Otro"
    else -> kind.name
}

fun characterArtificeOptionKindDisplayLabel(kind: CharacterClassOptionKind): String =
    characterClassOptionKindDisplayLabel(kind)

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
