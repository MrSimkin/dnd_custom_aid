package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContent
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope

internal enum class StagePlaceScopeFilter {
    ALL,
    PERSONAL,
    CAMPAIGN,
}

internal enum class StagePlaceSort {
    NAME,
    AREA,
    RECENT,
}

internal data class StagePlaceFilters(
    val query: String = "",
    val kind: PlaceKind? = null,
    val scope: StagePlaceScopeFilter = StagePlaceScopeFilter.ALL,
    val area: String = "",
    val function: String = "",
    val tag: String = "",
    val sort: StagePlaceSort = StagePlaceSort.NAME,
)

internal fun filterStagePlaces(
    places: List<PlaceContent>,
    filters: StagePlaceFilters,
): List<PlaceContent> {
    val normalizedQuery = filters.query.trim().lowercase()
    val normalizedArea = filters.area.trim().lowercase()
    val normalizedFunction = filters.function.trim().lowercase()
    val normalizedTag = filters.tag.trim().lowercase()

    val filtered = places
        .distinctBy { it.item.identity.id }
        .filter { content ->
            filters.kind == null || content.payload.kind == filters.kind
        }
        .filter { content ->
            when (filters.scope) {
                StagePlaceScopeFilter.ALL -> true
                StagePlaceScopeFilter.PERSONAL -> content.item.identity.scope is ContentScope.Personal
                StagePlaceScopeFilter.CAMPAIGN -> content.item.identity.scope is ContentScope.Campaign
            }
        }
        .filter { content ->
            normalizedArea.isEmpty() || content.payload.area.lowercase().contains(normalizedArea)
        }
        .filter { content ->
            normalizedFunction.isEmpty() || content.payload.function.lowercase().contains(normalizedFunction)
        }
        .filter { content ->
            normalizedTag.isEmpty() || content.payload.tags.any { it.lowercase().contains(normalizedTag) }
        }
        .filter { content ->
            normalizedQuery.isEmpty() || buildList {
                add(content.item.displayName)
                add(content.payload.kind.name)
                add(content.payload.summary)
                add(content.payload.area)
                add(content.payload.function)
                add(content.payload.presentation)
                add(content.payload.playerSafeText)
                add(content.payload.dmNotes)
                addAll(content.payload.services)
                addAll(content.payload.interactives)
                addAll(content.payload.hooks)
                addAll(content.payload.paperReferences)
                addAll(content.payload.tags)
            }.any { it.lowercase().contains(normalizedQuery) }
        }

    return when (filters.sort) {
        StagePlaceSort.NAME -> filtered.sortedWith(
            compareBy<PlaceContent>(
                { stagePlaceScopeSortKey(it) },
                { it.item.displayName.lowercase() },
            ),
        )
        StagePlaceSort.AREA -> filtered.sortedWith(
            compareBy<PlaceContent>(
                { it.payload.area.isBlank() },
                { it.payload.area.lowercase() },
                { it.item.displayName.lowercase() },
            ),
        )
        StagePlaceSort.RECENT -> filtered.sortedWith(
            compareByDescending<PlaceContent> { it.item.updatedAtEpochSeconds }
                .thenBy { it.item.displayName.lowercase() },
        )
    }
}

@Composable
internal fun StagePlaceFilterControls(
    filters: StagePlaceFilters,
    onFiltersChange: (StagePlaceFilters) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Organización Stage")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Tipo:")
            StageChoice(filters.kind == null, "Todos") { onFiltersChange(filters.copy(kind = null)) }
            StageChoice(filters.kind == PlaceKind.PLACE, "Lugares") {
                onFiltersChange(filters.copy(kind = PlaceKind.PLACE))
            }
            StageChoice(filters.kind == PlaceKind.SHOP, "Tiendas") {
                onFiltersChange(filters.copy(kind = PlaceKind.SHOP))
            }
            Text("Ámbito:")
            StageChoice(filters.scope == StagePlaceScopeFilter.ALL, "Todos") {
                onFiltersChange(filters.copy(scope = StagePlaceScopeFilter.ALL))
            }
            StageChoice(filters.scope == StagePlaceScopeFilter.PERSONAL, "Personal") {
                onFiltersChange(filters.copy(scope = StagePlaceScopeFilter.PERSONAL))
            }
            StageChoice(filters.scope == StagePlaceScopeFilter.CAMPAIGN, "Campaña") {
                onFiltersChange(filters.copy(scope = StagePlaceScopeFilter.CAMPAIGN))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = filters.area,
                onValueChange = { onFiltersChange(filters.copy(area = it)) },
                label = { Text("Filtrar área") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = filters.function,
                onValueChange = { onFiltersChange(filters.copy(function = it)) },
                label = { Text("Filtrar función") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = filters.tag,
                onValueChange = { onFiltersChange(filters.copy(tag = it)) },
                label = { Text("Filtrar etiqueta") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Orden:")
            StageChoice(filters.sort == StagePlaceSort.NAME, "Nombre") {
                onFiltersChange(filters.copy(sort = StagePlaceSort.NAME))
            }
            StageChoice(filters.sort == StagePlaceSort.AREA, "Área") {
                onFiltersChange(filters.copy(sort = StagePlaceSort.AREA))
            }
            StageChoice(filters.sort == StagePlaceSort.RECENT, "Recientes") {
                onFiltersChange(filters.copy(sort = StagePlaceSort.RECENT))
            }
            TextButton(
                onClick = {
                    onFiltersChange(
                        StagePlaceFilters(
                            query = filters.query,
                            sort = filters.sort,
                        ),
                    )
                },
            ) {
                Text("Limpiar filtros")
            }
        }
    }
}

@Composable
private fun StageChoice(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(onClick = onClick) { Text(label) }
    } else {
        TextButton(onClick = onClick) { Text(label) }
    }
}

private fun stagePlaceScopeSortKey(content: PlaceContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}
