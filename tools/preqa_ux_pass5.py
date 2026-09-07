from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly 1 match, found {count}")
    return text.replace(old, new, 1)


def prepare_sticky_collection(rel: str, function_name: str, tools_key: str, label: str) -> None:
    s = read(rel)
    s = replace_once(
        s,
        "import androidx.compose.foundation.BorderStroke\n",
        "import androidx.compose.foundation.BorderStroke\nimport androidx.compose.foundation.ExperimentalFoundationApi\n",
        f"{label} experimental import",
    )
    s = replace_once(
        s,
        f"@Composable\nprivate fun {function_name}(",
        f"@OptIn(ExperimentalFoundationApi::class)\n@Composable\nprivate fun {function_name}(",
        f"{label} opt-in",
    )
    s = replace_once(
        s,
        f'        item(key = "{tools_key}") {{',
        f'        stickyHeader(key = "{tools_key}") {{',
        f"{label} sticky tools",
    )
    s = replace_once(
        s,
        "contentPadding = PaddingValues(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 88.dp),\n        verticalArrangement = Arrangement.spacedBy(5.dp),",
        "contentPadding = PaddingValues(\n            start = appSpacingV4(6.dp),\n            end = appSpacingV4(6.dp),\n            top = appSpacingV4(5.dp),\n            bottom = appSpacingV4(88.dp),\n        ),\n        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),",
        f"{label} collection spacing",
    )
    s = replace_once(
        s,
        "modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),\n                    verticalArrangement = Arrangement.spacedBy(6.dp),",
        "modifier = Modifier.fillMaxWidth().padding(\n                        horizontal = appSpacingV4(7.dp),\n                        vertical = appSpacingV4(6.dp),\n                    ),\n                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),",
        f"{label} tools spacing",
    )
    write(rel, s)


# Distinct review identity.
gradle_path = "androidApp/build.gradle.kts"
s = read(gradle_path)
s = replace_once(s, "versionCode = 40400", "versionCode = 40500", "versionCode")
s = replace_once(s, 'versionName = "0.4.0-preqa.4"', 'versionName = "0.4.0-preqa.5"', "versionName")
write(gradle_path, s)

# Shared conditional-module collections: one sticky control pattern, no domain changes.
prepare_sticky_collection(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassOptionModulesV4.kt",
    "ClassOptionCollectionH2",
    "h2-${config.stateKey}-tools",
    "class options",
)
prepare_sticky_collection(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterArtificeModuleV4.kt",
    "ArtificeCollectionH1",
    "h1-artifice-tools",
    "artifice",
)
prepare_sticky_collection(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterFormsModuleV4.kt",
    "FormsCollectionH1",
    "h1-forms-tools",
    "forms",
)
prepare_sticky_collection(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCompanionsModuleV4.kt",
    "CompanionCollectionH3",
    "h3-companions-tools",
    "companions",
)

# Scale only layout spacing in persistent wide/tablet side editors; preserve functional widths.
side_specs = [
    (
        "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassOptionModulesV4.kt",
        "400.dp",
        "class options",
    ),
    (
        "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterArtificeModuleV4.kt",
        "400.dp",
        "artifice",
    ),
    (
        "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterFormsModuleV4.kt",
        "400.dp",
        "forms",
    ),
    (
        "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCompanionsModuleV4.kt",
        "420.dp",
        "companions",
    ),
]
for rel, width, label in side_specs:
    s = read(rel)
    s = replace_once(
        s,
        "horizontalArrangement = Arrangement.spacedBy(8.dp),\n        ) {\n            collection(Modifier.weight(1f))",
        "horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),\n        ) {\n            collection(Modifier.weight(1f))",
        f"{label} wide split spacing",
    )
    compact_surface = f"modifier = Modifier.width({width}).fillMaxHeight().padding(top = 5.dp, end = 8.dp, bottom = 8.dp),"
    expanded_surface = f"""modifier = Modifier
                    .width({width})
                    .fillMaxHeight()
                    .padding(top = 5.dp, end = 8.dp, bottom = 8.dp),"""
    scaled_expanded = f"""modifier = Modifier
                    .width({width})
                    .fillMaxHeight()
                    .padding(
                        top = appSpacingV4(5.dp),
                        end = appSpacingV4(8.dp),
                        bottom = appSpacingV4(8.dp),
                    ),"""
    if compact_surface in s:
        s = replace_once(
            s,
            compact_surface,
            f"modifier = Modifier.width({width}).fillMaxHeight().padding(\n                    top = appSpacingV4(5.dp),\n                    end = appSpacingV4(8.dp),\n                    bottom = appSpacingV4(8.dp),\n                ),",
            f"{label} side surface spacing",
        )
    elif expanded_surface in s:
        s = replace_once(s, expanded_surface, scaled_expanded, f"{label} side surface spacing")
    else:
        raise RuntimeError(f"{label} side surface spacing: no known layout matched")
    s = replace_once(
        s,
        "contentPadding = PaddingValues(10.dp),\n                        verticalArrangement = Arrangement.spacedBy(8.dp),",
        "contentPadding = PaddingValues(appSpacingV4(10.dp)),\n                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),",
        f"{label} side editor spacing",
    )
    s = replace_once(
        s,
        "modifier = Modifier.fillMaxSize().padding(16.dp),",
        "modifier = Modifier.fillMaxSize().padding(appSpacingV4(16.dp)),",
        f"{label} side empty spacing",
    )
    write(rel, s)

# Conjuros: fixed operational controls outside the scrolling level list; level sticky headers remain independent.
spell_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt"
s = read(spell_path)
function_marker = "@OptIn(ExperimentalFoundationApi::class)\n@Composable\nprivate fun SpellCollectionG2("
function_pos = s.index(function_marker)
body_marker = ") {\n    LazyColumn("
body_pos = s.index(body_marker, function_pos)
content_marker = "        if (draft.spells.isEmpty()) {"
content_pos = s.index(content_marker, body_pos)
new_body = ''') {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = appSpacingV4(6.dp),
                    end = appSpacingV4(6.dp),
                    top = appSpacingV4(5.dp),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = appSpacingV4(7.dp),
                        vertical = appSpacingV4(6.dp),
                    ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Conjuros", style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
                }
                CharacterCollectionToolbarV4(
                    itemCount = visibleCount,
                    query = query,
                    onQueryChange = onQueryChange,
                    order = order,
                    onOrderChange = onOrderChange,
                    filters = spellFiltersG2(selectedSourceId),
                    searchLabel = "Buscar conjuros",
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = appSpacingV4(6.dp),
                end = appSpacingV4(6.dp),
                top = 0.dp,
                bottom = appSpacingV4(88.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            if (!canReorder && visibleCount > 0) {
                item(key = "spell-g2-order-help") {
                    Text(
                        if (order == CharacterPresentationOrder.ALPHABETICAL) {
                            "A–Z es solo una vista. Vuelve a Manual para arrastrar sin perder el orden guardado."
                        } else {
                            "Limpia búsqueda y filtros para reordenar manualmente."
                        },
                        modifier = Modifier.padding(horizontal = appSpacingV4(3.dp)),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

'''
s = s[:body_pos] + new_body + s[content_pos:]
level_marker = "\n@Composable\nprivate fun SpellLevelStickyHeaderG2("
level_pos = s.index(level_marker, body_pos)
prefix = s[:level_pos]
last_function_close = prefix.rfind("\n}")
if last_function_close < 0:
    raise RuntimeError("spell collection closing brace not found")
prefix = prefix[:last_function_close] + "\n    }" + prefix[last_function_close:]
s = prefix + s[level_pos:]

# Scale persistent spell-editor spacing without changing its 340dp working width.
s = replace_once(
    s,
    "horizontalArrangement = Arrangement.spacedBy(8.dp),\n        ) {\n            collection(Modifier.weight(1f))",
    "horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),\n        ) {\n            collection(Modifier.weight(1f))",
    "spell wide split spacing",
)
s = replace_once(
    s,
    ".padding(top = 5.dp, end = 8.dp, bottom = 8.dp),",
    ".padding(\n                        top = appSpacingV4(5.dp),\n                        end = appSpacingV4(8.dp),\n                        bottom = appSpacingV4(8.dp),\n                    ),",
    "spell side surface spacing",
)
s = replace_once(
    s,
    ".padding(10.dp),\n                        verticalArrangement = Arrangement.spacedBy(8.dp),",
    ".padding(appSpacingV4(10.dp)),\n                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),",
    "spell side editor spacing",
)
s = replace_once(
    s,
    "modifier = Modifier.fillMaxSize().padding(16.dp),",
    "modifier = Modifier.fillMaxSize().padding(appSpacingV4(16.dp)),",
    "spell side empty spacing",
)
write(spell_path, s)

# Notas: spacing-scale propagation only. Keep editor heights/minimums intact and keep general notes scrollable.
notes_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt"
s = read(notes_path)
s = replace_once(
    s,
    '''        contentPadding = PaddingValues(
            start = if (wide) 14.dp else 5.dp,
            end = if (wide) 14.dp else 5.dp,
            top = 7.dp,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),''',
    '''        contentPadding = PaddingValues(
            start = appSpacingV4(if (wide) 14.dp else 5.dp),
            end = appSpacingV4(if (wide) 14.dp else 5.dp),
            top = appSpacingV4(7.dp),
            bottom = appSpacingV4(88.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),''',
    "notes list spacing",
)
s = replace_once(
    s,
    ".padding(horizontal = if (wide) 12.dp else 7.dp, vertical = 8.dp),\n                    verticalArrangement = Arrangement.spacedBy(6.dp),",
    ".padding(\n                            horizontal = appSpacingV4(if (wide) 12.dp else 7.dp),\n                            vertical = appSpacingV4(8.dp),\n                        ),\n                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),",
    "general notes spacing",
)
s = replace_once(
    s,
    ".padding(horizontal = if (wide) 12.dp else 7.dp, vertical = 7.dp),\n                    verticalArrangement = Arrangement.spacedBy(6.dp),",
    ".padding(\n                            horizontal = appSpacingV4(if (wide) 12.dp else 7.dp),\n                            vertical = appSpacingV4(7.dp),\n                        ),\n                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),",
    "titled notes spacing",
)
s = replace_once(
    s,
    "horizontalArrangement = Arrangement.spacedBy(6.dp),\n                                verticalAlignment = Alignment.Top,",
    "horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),\n                                verticalAlignment = Alignment.Top,",
    "note grid spacing",
)
write(notes_path, s)

print("Pass 05 transformation applied successfully.")
