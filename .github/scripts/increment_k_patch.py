from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    file_path = Path(path)
    text = file_path.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}")
    file_path.write_text(text.replace(old, new, 1), encoding="utf-8")


spells = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellsTabV4.kt"
editor = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"

replace_once(
    spells,
    '''import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement''',
    '''import androidx.compose.foundation.layout.Arrangement''',
)
replace_once(
    spells,
    '''import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn''',
    '''import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState''',
)
replace_once(
    spells,
    '''import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue''',
    '''import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue''',
)
replace_once(
    spells,
    '''import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp''',
    '''import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp''',
)
replace_once(
    spells,
    '''    val selectedSource = selectedSourceId?.let { selectedId ->
        draft.sources.firstOrNull { it.id.toString() == selectedId }
    }
    fun updateSources(updated: List<CharacterSpellcastingSource>) {''',
    '''    val selectedSource = selectedSourceId?.let { selectedId ->
        draft.sources.firstOrNull { it.id.toString() == selectedId }
    }
    val sourceListState = rememberLazyListState()
    val selectedSourceIndex = selectedSource?.let { source ->
        draft.sources.indexOfFirst { it.id == source.id }.takeIf { it >= 0 }?.plus(1)
    } ?: 0
    LaunchedEffect(selectedSource?.id, draft.sources.map { it.id }) {
        sourceListState.animateScrollToItem(selectedSourceIndex)
    }

    fun updateSources(updated: List<CharacterSpellcastingSource>) {''',
)
replace_once(
    spells,
    '''            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selectedSource == null) {
                    Button(onClick = { selectedSourceId = null }) { Text("Todos", maxLines = 1) }
                } else {
                    OutlinedButton(onClick = { selectedSourceId = null }) { Text("Todos", maxLines = 1) }
                }
                draft.sources.forEach { source ->
                    if (selectedSource?.id == source.id) {
                        Button(onClick = { selectedSourceId = source.id.toString() }) {
                            Text(source.name, maxLines = 1)
                        }
                    } else {
                        OutlinedButton(onClick = { selectedSourceId = source.id.toString() }) {
                            Text(source.name, maxLines = 1)
                        }
                    }
                }
            }''',
    '''            LazyRow(
                modifier = Modifier.weight(1f),
                state = sourceListState,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item(key = "all-sources") {
                    if (selectedSource == null) {
                        Button(onClick = { selectedSourceId = null }) { Text("Todos", maxLines = 1) }
                    } else {
                        OutlinedButton(onClick = { selectedSourceId = null }) { Text("Todos", maxLines = 1) }
                    }
                }
                items(
                    count = draft.sources.size,
                    key = { index -> draft.sources[index].id.toString() },
                ) { index ->
                    val source = draft.sources[index]
                    val sourceLabel: @Composable () -> Unit = {
                        Text(
                            source.name,
                            modifier = Modifier.widthIn(max = 180.dp),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (selectedSource?.id == source.id) {
                        Button(onClick = { selectedSourceId = source.id.toString() }) {
                            sourceLabel()
                        }
                    } else {
                        OutlinedButton(onClick = { selectedSourceId = source.id.toString() }) {
                            sourceLabel()
                        }
                    }
                }
            }''',
)

replace_once(
    editor,
    '''            Column {
                CompactFieldLabelV4("")
                CompactMenuSurfaceV4("×", onRemove, Modifier.width(34.dp))
            }''',
    '''            Column {
                CompactFieldLabelV4("")
                StableRemoveIconButton(
                    onClick = onRemove,
                    contentDescription = "Eliminar clase ${draft.name.ifBlank { "sin nombre" }}",
                )
            }''',
)
replace_once(
    editor,
    '''                CompactMenuSurfaceV4(
                    text = "▾",
                    onClick = {
                        customMode = false
                        expanded = true
                    },
                    modifier = Modifier.width(30.dp),
                )''',
    '''                StableDropdownIconButton(
                    onClick = {
                        customMode = false
                        expanded = true
                    },
                    contentDescription = "Abrir lista de clases",
                )''',
)
replace_once(
    editor,
    '''                CompactMenuSurfaceV4(
                    text = "▾",
                    onClick = {
                        customMode = false
                        expanded = true
                    },
                    modifier = Modifier.width(28.dp),
                )''',
    '''                StableDropdownIconButton(
                    onClick = {
                        customMode = false
                        expanded = true
                    },
                    contentDescription = "Abrir lista de dados de golpe",
                )''',
)

print("Increment K asserted navigation/icon patches applied successfully")
