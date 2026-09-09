from pathlib import Path

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")


def replace_once(path: Path, old: str, new: str, label: str) -> None:
    text = path.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected 1 occurrence, found {count}")
    path.write_text(text.replace(old, new, 1), encoding="utf-8")


# Forms: flatten action controls and pair Fuente + CR.
p = ROOT / "CharacterFormsModuleV4.kt"
replace_once(
    p,
    '''                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StableFavoriteIconButton(
                            selected = favorite,
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = favoriteEnabled,
                        )
                        if (structuralEditingEnabled) {
                            StableRemoveIconButton(
                                onClick = onDelete,
                                contentDescription = "Eliminar ${form.name}",
                            )
                        }
                    }
                    if (structuralEditingEnabled) {
                        TextButton(
                            onClick = onDuplicate,
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                        ) { Text("Duplicar") }
                    }
                }
''',
    '''                Row(verticalAlignment = Alignment.CenterVertically) {
                    StableFavoriteIconButton(
                        selected = favorite,
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = favoriteEnabled,
                        contentDescription = if (favorite) "Quitar ${form.name} de Favoritos" else "Añadir ${form.name} a Favoritos",
                    )
                    if (structuralEditingEnabled) {
                        StableDuplicateIconButton(
                            onClick = onDuplicate,
                            contentDescription = "Duplicar ${form.name}",
                        )
                        StableRemoveIconButton(
                            onClick = onDelete,
                            contentDescription = "Eliminar ${form.name}",
                        )
                    }
                }
''',
    "forms action row",
)
replace_once(
    p,
    '''    OutlinedTextField(
        value = source,
        onValueChange = onSourceChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Fuente / procedencia") },
        singleLine = true,
    )
    OutlinedTextField(
        value = cr,
        onValueChange = onCrChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("CR / referencia") },
        singleLine = true,
    )
''',
    '''    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        OutlinedTextField(
            value = source,
            onValueChange = onSourceChange,
            modifier = Modifier.weight(1.6f),
            label = { Text("Fuente / procedencia") },
            singleLine = true,
        )
        OutlinedTextField(
            value = cr,
            onValueChange = onCrChange,
            modifier = Modifier.weight(1f),
            label = { Text("CR / referencia") },
            singleLine = true,
        )
    }
''',
    "forms source/cr pair",
)

# Companions: flatten action controls. Numeric short fields are already paired.
p = ROOT / "CharacterCompanionsModuleV4.kt"
replace_once(
    p,
    '''                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StableFavoriteIconButton(
                            selected = favorite,
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = structuralEditingEnabled && favoriteEnabled,
                        )
                        if (structuralEditingEnabled) {
                            StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${companion.name}")
                        }
                    }
                    if (structuralEditingEnabled) {
                        TextButton(onClick = onDuplicate, contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)) {
                            Text("Duplicar")
                        }
                    }
                }
''',
    '''                Row(verticalAlignment = Alignment.CenterVertically) {
                    StableFavoriteIconButton(
                        selected = favorite,
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = structuralEditingEnabled && favoriteEnabled,
                        contentDescription = if (favorite) "Quitar ${companion.name} de Favoritos" else "Añadir ${companion.name} a Favoritos",
                    )
                    if (structuralEditingEnabled) {
                        StableDuplicateIconButton(
                            onClick = onDuplicate,
                            contentDescription = "Duplicar ${companion.name}",
                        )
                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${companion.name}")
                    }
                }
''',
    "companions action row",
)

# Artifice: flatten action controls and pair Fuente + Coste.
p = ROOT / "CharacterArtificeModuleV4.kt"
replace_once(
    p,
    '''                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StableFavoriteIconButton(
                            selected = favorite,
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = favoriteEnabled,
                        )
                        if (structuralEditingEnabled) {
                            StableRemoveIconButton(
                                onClick = onDelete,
                                contentDescription = "Eliminar ${option.name}",
                            )
                        }
                    }
                    if (structuralEditingEnabled) {
                        TextButton(
                            onClick = onDuplicate,
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                        ) { Text("Duplicar") }
                    }
                }
''',
    '''                Row(verticalAlignment = Alignment.CenterVertically) {
                    StableFavoriteIconButton(
                        selected = favorite,
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = favoriteEnabled,
                        contentDescription = if (favorite) "Quitar ${option.name} de Favoritos" else "Añadir ${option.name} a Favoritos",
                    )
                    if (structuralEditingEnabled) {
                        StableDuplicateIconButton(
                            onClick = onDuplicate,
                            contentDescription = "Duplicar ${option.name}",
                        )
                        StableRemoveIconButton(
                            onClick = onDelete,
                            contentDescription = "Eliminar ${option.name}",
                        )
                    }
                }
''',
    "artifice action row",
)
replace_once(
    p,
    '''    OutlinedTextField(
        value = source,
        onValueChange = onSourceChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Fuente / procedencia") },
        singleLine = true,
    )
    OutlinedTextField(
        value = cost,
        onValueChange = onCostChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Coste / referencia") },
        singleLine = true,
    )
''',
    '''    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        OutlinedTextField(
            value = source,
            onValueChange = onSourceChange,
            modifier = Modifier.weight(1f),
            label = { Text("Fuente / procedencia") },
            singleLine = true,
        )
        OutlinedTextField(
            value = cost,
            onValueChange = onCostChange,
            modifier = Modifier.weight(1f),
            label = { Text("Coste / referencia") },
            singleLine = true,
        )
    }
''',
    "artifice source/cost pair",
)

# Generic class-option modules: same compact actions and Fuente + Coste pairing.
p = ROOT / "CharacterClassOptionModulesV4.kt"
replace_once(
    p,
    '''                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StableFavoriteIconButton(
                            selected = favorite,
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = structuralEditingEnabled && favoriteEnabled,
                        )
                        if (structuralEditingEnabled) {
                            StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${option.name}")
                        }
                    }
                    if (structuralEditingEnabled) {
                        TextButton(onClick = onDuplicate, contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)) {
                            Text("Duplicar")
                        }
                    }
                }
''',
    '''                Row(verticalAlignment = Alignment.CenterVertically) {
                    StableFavoriteIconButton(
                        selected = favorite,
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = structuralEditingEnabled && favoriteEnabled,
                        contentDescription = if (favorite) "Quitar ${option.name} de Favoritos" else "Añadir ${option.name} a Favoritos",
                    )
                    if (structuralEditingEnabled) {
                        StableDuplicateIconButton(
                            onClick = onDuplicate,
                            contentDescription = "Duplicar ${option.name}",
                        )
                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${option.name}")
                    }
                }
''',
    "class option action row",
)
replace_once(
    p,
    '''    OutlinedTextField(
        value = source,
        onValueChange = onSourceChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Fuente / procedencia") },
        singleLine = true,
    )
    OutlinedTextField(
        value = cost,
        onValueChange = onCostChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Coste / referencia") },
        singleLine = true,
    )
''',
    '''    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        OutlinedTextField(
            value = source,
            onValueChange = onSourceChange,
            modifier = Modifier.weight(1f),
            label = { Text("Fuente / procedencia") },
            singleLine = true,
        )
        OutlinedTextField(
            value = cost,
            onValueChange = onCostChange,
            modifier = Modifier.weight(1f),
            label = { Text("Coste / referencia") },
            singleLine = true,
        )
    }
''',
    "class option source/cost pair",
)

# Guard the intended outcome before compilation.
for filename in [
    "CharacterFormsModuleV4.kt",
    "CharacterCompanionsModuleV4.kt",
    "CharacterArtificeModuleV4.kt",
    "CharacterClassOptionModulesV4.kt",
]:
    text = (ROOT / filename).read_text(encoding="utf-8")
    if 'Text("Duplicar")' in text:
        raise RuntimeError(f"{filename}: text Duplicate control still remains in targeted module")

print("Compact follow-up transformed Forms, Companions, Artifice and class-option modules")
