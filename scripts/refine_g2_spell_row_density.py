from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt")
text = path.read_text()
old = '''                if (selectedAssociation != null && selectedSourceId != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Checkbox(
                            checked = selectedAssociation.prepared,
                            onCheckedChange = onPreparedChange,
                        )
                        Text("Preparado", style = MaterialTheme.typography.labelSmall)
                    }
                }
                TextButton(
                    onClick = { onFavoriteChange(!favorite) },
                    enabled = favoriteEnabled,
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                ) {
                    Text(if (favorite) "★" else "☆")
                }
                TextButton(
                    onClick = onDuplicate,
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                ) {
                    Text("Duplicar")
                }
                StableRemoveIconButton(
                    onClick = onDelete,
                    contentDescription = "Eliminar ${spell.name}",
                )
'''
new = '''                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    if (selectedAssociation != null && selectedSourceId != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedAssociation.prepared,
                                onCheckedChange = onPreparedChange,
                            )
                            Text("Prep.", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = favoriteEnabled,
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                        ) {
                            Text(if (favorite) "★" else "☆")
                        }
                        StableRemoveIconButton(
                            onClick = onDelete,
                            contentDescription = "Eliminar ${spell.name}",
                        )
                    }
                    TextButton(
                        onClick = onDuplicate,
                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                    ) {
                        Text("Duplicar")
                    }
                }
'''
count = text.count(old)
if count != 1:
    raise SystemExit(f"Expected exactly one spell-row action block, found {count}")
path.write_text(text.replace(old, new, 1))
