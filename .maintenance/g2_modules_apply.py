from pathlib import Path


def replace_between(path: str, start: str, end: str, replacement: str) -> None:
    p = Path(path)
    text = p.read_text()
    if text.count(start) != 1:
        raise SystemExit(f"{path}: expected one start marker, found {text.count(start)}")
    start_i = text.index(start)
    end_i = text.index(end, start_i)
    text = text[:start_i] + replacement + text[end_i:]
    p.write_text(text)


def compact_block(key: str, title: str, description: str, search_label: str, help_key: str) -> str:
    return f'''        stickyHeader(key = "{key}") {{
            CharacterCollectionToolbarV4(
                itemCount = visible.size,
                query = query,
                onQueryChange = onQueryChange,
                order = order,
                onOrderChange = onOrderChange,
                filters = filters,
                searchLabel = {search_label},
                collapsibleSearch = true,
                showItemCount = false,
                compactOrderControl = true,
                contextContent = {{
                    Text(
                        {title},
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }},
                onAdd = if (structuralEditingEnabled) onAdd else null,
            )
        }}

        item(key = "{help_key}") {{
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {{
                CharacterHelpV4({description})
                if (!canReorder && visible.isNotEmpty()) {{
                    Text(
                        if (order == CharacterPresentationOrder.ALPHABETICAL) {{
                            "A–Z es solo una vista. Vuelve a Manual para arrastrar sin perder el orden guardado."
                        }} else {{
                            "Limpia búsqueda y filtros para reordenar manualmente."
                        }},
                        style = MaterialTheme.typography.labelSmall,
                    )
                }}
            }}
        }}
'''

replace_between(
    'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterArtificeModuleV4.kt',
    '        stickyHeader(key = "h1-artifice-tools") {',
    '\n        if (artificeOptions.isEmpty()) {',
    compact_block(
        'h1-artifice-tools',
        '"Artífice"',
        '"Planes y dispositivos persistentes. Recursos, conjuros, equipo y compañeros mantienen sus propios datos."',
        '"Buscar en Artífice"',
        'h1-artifice-help',
    ),
)

replace_between(
    'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterFormsModuleV4.kt',
    '        stickyHeader(key = "h1-forms-tools") {',
    '\n        if (forms.isEmpty()) {',
    compact_block(
        'h1-forms-tools',
        '"Formas"',
        '"Biblioteca de transformaciones y formas alternativas. Consultarlas no cambia automáticamente la ficha base."',
        '"Buscar formas"',
        'h1-forms-help',
    ),
)

replace_between(
    'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCompanionsModuleV4.kt',
    '        stickyHeader(key = "h3-companions-tools") {',
    '\n        if (companions.isEmpty()) {',
    compact_block(
        'h3-companions-tools',
        '"Compañeros"',
        '"Entidades persistentes del personaje. El combate del DM mantiene su propio estado de encuentro."',
        '"Buscar en Compañeros"',
        'h3-companions-help',
    ),
)

replace_between(
    'androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassOptionModulesV4.kt',
    '        stickyHeader(key = "h2-${config.stateKey}-tools") {',
    '\n        if (ownedOptions.isEmpty()) {',
    compact_block(
        'h2-${config.stateKey}-tools',
        'config.title',
        'config.description',
        'config.searchLabel',
        'h2-${config.stateKey}-help',
    ),
)
