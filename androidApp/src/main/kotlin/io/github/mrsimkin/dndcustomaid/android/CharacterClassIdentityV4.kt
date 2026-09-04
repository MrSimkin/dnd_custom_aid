package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassCatalog
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassCatalogEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSubclassCatalogEntry
import io.github.mrsimkin.dndcustomaid.shared.character.suggestedHitDieSidesForClassName
import kotlin.uuid.Uuid

private enum class ClassIdentityModeV4 {
    OFFICIAL,
    MANUAL,
}

private enum class SubclassIdentityModeV4 {
    NONE,
    OFFICIAL,
    MANUAL,
}

@Composable
internal fun CharacterClassIdentityCardV4(
    classes: List<ClassLevelDraftV4>,
    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,
) {
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Clases y Dados de Golpe", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "La lista oficial es una ayuda de identificación. Clases y subclases manuales/homebrew siguen siendo válidas.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                TextButton(
                    onClick = {
                        editorId = null
                        editorOpen = true
                    },
                ) { Text("+ Añadir") }
            }

            if (classes.isEmpty()) {
                Text("Sin clases registradas.", style = MaterialTheme.typography.bodySmall)
            } else {
                classes.forEach { classDraft ->
                    ClassIdentityRowV4(
                        draft = classDraft,
                        onOpen = {
                            editorId = classDraft.id.toString()
                            editorOpen = true
                        },
                        onDelete = { deleteId = classDraft.id.toString() },
                    )
                }
            }
        }
    }

    if (editorOpen) {
        val existing = editorId?.let { id -> classes.firstOrNull { it.id.toString() == id } }
        ClassIdentityEditorDialogV4(
            existing = existing,
            onDismiss = { editorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    classes + saved
                } else {
                    classes.map { if (it.id == existing.id) saved else it }
                }
                onClassesChange(updated)
                editorOpen = false
            },
        )
    }

    deleteId?.let { id ->
        val target = classes.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name.ifBlank { "clase sin nombre" },
                itemTypeLabel = "clase del personaje",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onClassesChange(classes.filterNot { it.id == target.id })
                    deleteId = null
                },
            )
        }
    }
}

@Composable
private fun ClassIdentityRowV4(
    draft: ClassLevelDraftV4,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    val subclass = draft.subclassName?.trim()?.takeIf { it.isNotEmpty() }
    val primary = buildString {
        append(draft.name.ifBlank { "Clase sin nombre" })
        if (subclass != null) append(" · $subclass")
        append(" · Nv. ${draft.level.ifBlank { "?" }}")
    }
    val identityRules = if (subclass != null) draft.subclassRulesFamily else draft.rulesFamily
    val identitySource = if (subclass != null) draft.subclassSource ?: draft.source else draft.source
    val hitDice = "DG restantes ${draft.hitDiceRemaining.ifBlank { "?" }} · d${draft.hitDieSides.ifBlank { "?" }}"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f).clickable(onClick = onOpen).padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                Text(primary, style = MaterialTheme.typography.labelLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                CharacterRulesSourceBadgesV4(
                    rulesFamily = identityRules,
                    source = identitySource,
                )
                Text(hitDice, style = MaterialTheme.typography.labelSmall)
            }
            TextButton(onClick = onDelete) { Text("Quitar") }
        }
    }
}

@Composable
private fun ClassIdentityEditorDialogV4(
    existing: ClassLevelDraftV4?,
    onDismiss: () -> Unit,
    onSave: (ClassLevelDraftV4) -> Unit,
) {
    val id = existing?.id ?: remember { Uuid.random() }
    var classModeName by rememberSaveable(existing?.id?.toString(), "class-mode") {
        mutableStateOf(if (existing?.catalogKey != null) ClassIdentityModeV4.OFFICIAL.name else if (existing == null) ClassIdentityModeV4.OFFICIAL.name else ClassIdentityModeV4.MANUAL.name)
    }
    var className by rememberSaveable(existing?.id?.toString(), "class-name") { mutableStateOf(existing?.name.orEmpty()) }
    var classCatalogKey by rememberSaveable(existing?.id?.toString(), "class-key") { mutableStateOf(existing?.catalogKey.orEmpty()) }
    var classRulesName by rememberSaveable(existing?.id?.toString(), "class-rules") {
        mutableStateOf((existing?.rulesFamily ?: CharacterRulesFamily.UNSPECIFIED).name)
    }
    var classSource by rememberSaveable(existing?.id?.toString(), "class-source") { mutableStateOf(existing?.source.orEmpty()) }

    val initialSubclassMode = when {
        existing?.subclassName.isNullOrBlank() -> SubclassIdentityModeV4.NONE
        existing?.subclassCatalogKey != null -> SubclassIdentityModeV4.OFFICIAL
        else -> SubclassIdentityModeV4.MANUAL
    }
    var subclassModeName by rememberSaveable(existing?.id?.toString(), "subclass-mode") { mutableStateOf(initialSubclassMode.name) }
    var subclassName by rememberSaveable(existing?.id?.toString(), "subclass-name") { mutableStateOf(existing?.subclassName.orEmpty()) }
    var subclassCatalogKey by rememberSaveable(existing?.id?.toString(), "subclass-key") { mutableStateOf(existing?.subclassCatalogKey.orEmpty()) }
    var subclassRulesName by rememberSaveable(existing?.id?.toString(), "subclass-rules") {
        mutableStateOf((existing?.subclassRulesFamily ?: existing?.rulesFamily ?: CharacterRulesFamily.UNSPECIFIED).name)
    }
    var subclassSource by rememberSaveable(existing?.id?.toString(), "subclass-source") { mutableStateOf(existing?.subclassSource.orEmpty()) }

    var level by rememberSaveable(existing?.id?.toString(), "level") { mutableStateOf(existing?.level ?: "1") }
    var hitDiceRemaining by rememberSaveable(existing?.id?.toString(), "remaining") { mutableStateOf(existing?.hitDiceRemaining ?: "1") }
    var hitDieSides by rememberSaveable(existing?.id?.toString(), "die") { mutableStateOf(existing?.hitDieSides.orEmpty()) }

    val classMode = enumValueOrDefaultV4(classModeName, ClassIdentityModeV4.OFFICIAL)
    val subclassMode = enumValueOrDefaultV4(subclassModeName, SubclassIdentityModeV4.NONE)
    val selectedClass = CharacterClassCatalog.byKey(classCatalogKey.takeIf { it.isNotBlank() })
    val selectedSubclass = selectedClass?.subclasses?.firstOrNull { it.key == subclassCatalogKey }
    val classRules = enumValueOrDefaultV4(classRulesName, CharacterRulesFamily.UNSPECIFIED)
    val subclassRules = enumValueOrDefaultV4(subclassRulesName, classRules)
    val parsedLevel = level.toIntOrNull()
    val parsedRemaining = hitDiceRemaining.toIntOrNull()
    val parsedDie = hitDieSides.toIntOrNull()
    val suggestion = suggestedHitDieSidesForClassName(className)

    val classIdentityValid = when (classMode) {
        ClassIdentityModeV4.OFFICIAL -> selectedClass != null
        ClassIdentityModeV4.MANUAL -> className.trim().isNotEmpty()
    }
    val subclassIdentityValid = when (subclassMode) {
        SubclassIdentityModeV4.NONE -> true
        SubclassIdentityModeV4.OFFICIAL -> selectedClass != null && selectedSubclass != null
        SubclassIdentityModeV4.MANUAL -> subclassName.trim().isNotEmpty()
    }
    val valid = classIdentityValid && subclassIdentityValid &&
        parsedLevel != null && parsedLevel >= 0 &&
        parsedRemaining != null && parsedRemaining >= 0 &&
        parsedDie != null && parsedDie >= 0

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir clase" else "Clase y subclase",
        onCancel = onDismiss,
        onSave = {
            val authoritativeClass = if (classMode == ClassIdentityModeV4.OFFICIAL) selectedClass else null
            val savedClassName = authoritativeClass?.nameEs ?: className.trim()
            val savedClassRules = authoritativeClass?.rulesFamily ?: classRules
            val savedClassSource = authoritativeClass?.source ?: classSource.trim().takeIf { it.isNotEmpty() }
            val savedClassKey = authoritativeClass?.key

            val authoritativeSubclass = if (subclassMode == SubclassIdentityModeV4.OFFICIAL) selectedSubclass else null
            val savedSubclassName = when (subclassMode) {
                SubclassIdentityModeV4.NONE -> null
                SubclassIdentityModeV4.OFFICIAL -> authoritativeSubclass?.name
                SubclassIdentityModeV4.MANUAL -> subclassName.trim().takeIf { it.isNotEmpty() }
            }
            val savedSubclassSource = when (subclassMode) {
                SubclassIdentityModeV4.NONE -> null
                SubclassIdentityModeV4.OFFICIAL -> authoritativeSubclass?.source
                SubclassIdentityModeV4.MANUAL -> subclassSource.trim().takeIf { it.isNotEmpty() }
            }
            val savedSubclassKey = if (subclassMode == SubclassIdentityModeV4.OFFICIAL) authoritativeSubclass?.key else null
            val savedSubclassRules = when (subclassMode) {
                SubclassIdentityModeV4.NONE -> CharacterRulesFamily.UNSPECIFIED
                SubclassIdentityModeV4.OFFICIAL -> authoritativeSubclass?.rulesFamily ?: savedClassRules
                SubclassIdentityModeV4.MANUAL -> subclassRules
            }

            onSave(
                ClassLevelDraftV4(
                    id = id,
                    name = savedClassName,
                    level = parsedLevel?.toString() ?: level,
                    hitDieSides = parsedDie?.toString() ?: hitDieSides,
                    hitDiceRemaining = parsedRemaining?.toString() ?: hitDiceRemaining,
                    rulesFamily = savedClassRules,
                    source = savedClassSource,
                    catalogKey = savedClassKey,
                    subclassName = savedSubclassName,
                    subclassSource = savedSubclassSource,
                    subclassCatalogKey = savedSubclassKey,
                    subclassRulesFamily = savedSubclassRules,
                ),
            )
        },
        saveEnabled = valid,
        supportingText = "La selección oficial solo rellena identidad y fuente. No valida legalidad, nivel, multiclass ni reglas de construcción.",
    ) {
        IdentityModeSelectorV4(
            label = "Tipo de clase",
            current = classMode,
            onSelect = { mode ->
                classModeName = mode.name
                if (mode == ClassIdentityModeV4.MANUAL) {
                    classCatalogKey = ""
                    if (subclassMode == SubclassIdentityModeV4.OFFICIAL) {
                        subclassModeName = if (subclassName.isBlank()) SubclassIdentityModeV4.NONE.name else SubclassIdentityModeV4.MANUAL.name
                        subclassCatalogKey = ""
                    }
                }
            },
        )

        if (classMode == ClassIdentityModeV4.OFFICIAL) {
            OfficialClassSelectorV4(
                selected = selectedClass,
                onSelect = { entry ->
                    classCatalogKey = entry.key
                    className = entry.nameEs
                    classRulesName = entry.rulesFamily.name
                    classSource = entry.source
                    subclassModeName = SubclassIdentityModeV4.NONE.name
                    subclassName = ""
                    subclassCatalogKey = ""
                    subclassSource = ""
                    subclassRulesName = entry.rulesFamily.name
                },
            )
            selectedClass?.let { entry ->
                CharacterRulesSourceBadgesV4(
                    rulesFamily = entry.rulesFamily,
                    source = entry.source,
                )
            }
        } else {
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                label = { Text("Clase") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            RulesFamilySelectorV4(
                label = "Familia de reglas",
                current = classRules,
                onSelect = { classRulesName = it.name },
            )
            OutlinedTextField(
                value = classSource,
                onValueChange = { classSource = it },
                label = { Text("Fuente opcional") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        SubclassModeSelectorV4(
            current = subclassMode,
            officialAvailable = selectedClass?.subclasses?.isNotEmpty() == true,
            onSelect = { mode ->
                subclassModeName = mode.name
                when (mode) {
                    SubclassIdentityModeV4.NONE -> {
                        subclassName = ""
                        subclassCatalogKey = ""
                        subclassSource = ""
                    }
                    SubclassIdentityModeV4.OFFICIAL -> {
                        subclassName = ""
                        subclassCatalogKey = ""
                        subclassSource = ""
                        subclassRulesName = selectedClass?.rulesFamily?.name ?: classRules.name
                    }
                    SubclassIdentityModeV4.MANUAL -> {
                        subclassCatalogKey = ""
                        if (subclassRulesName == CharacterRulesFamily.UNSPECIFIED.name) {
                            subclassRulesName = (selectedClass?.rulesFamily ?: classRules).name
                        }
                    }
                }
            },
        )

        when (subclassMode) {
            SubclassIdentityModeV4.NONE -> Unit
            SubclassIdentityModeV4.OFFICIAL -> {
                OfficialSubclassSelectorV4(
                    classEntry = selectedClass,
                    selected = selectedSubclass,
                    onSelect = { entry ->
                        subclassCatalogKey = entry.key
                        subclassName = entry.name
                        subclassRulesName = entry.rulesFamily.name
                        subclassSource = entry.source
                    },
                )
                selectedSubclass?.let { entry ->
                    CharacterRulesSourceBadgesV4(
                        rulesFamily = entry.rulesFamily,
                        source = entry.source,
                    )
                }
            }
            SubclassIdentityModeV4.MANUAL -> {
                OutlinedTextField(
                    value = subclassName,
                    onValueChange = { subclassName = it },
                    label = { Text("Subclase") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                RulesFamilySelectorV4(
                    label = "Reglas de subclase",
                    current = subclassRules,
                    onSelect = { subclassRulesName = it.name },
                )
                OutlinedTextField(
                    value = subclassSource,
                    onValueChange = { subclassSource = it },
                    label = { Text("Fuente de subclase opcional") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            NumericClassFieldV4("Nivel", level, { level = it }, Modifier.weight(1f))
            NumericClassFieldV4("DG restantes", hitDiceRemaining, { hitDiceRemaining = it }, Modifier.weight(1f))
        }
        NumericClassFieldV4("Tipo de DG (caras)", hitDieSides, { hitDieSides = it }, Modifier.fillMaxWidth())

        if (suggestion != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Sugerencia habitual para ${className.ifBlank { "esta clase" }}: d$suggestion", style = MaterialTheme.typography.labelSmall)
                if (hitDieSides != suggestion.toString()) {
                    TextButton(onClick = { hitDieSides = suggestion.toString() }) { Text("Aplicar d$suggestion") }
                }
            }
        }

        CharacterInlineValidationMessage(
            when {
                !classIdentityValid -> "Selecciona o escribe una clase válida."
                !subclassIdentityValid -> "Completa la subclase seleccionada."
                parsedLevel == null || parsedLevel < 0 -> "El nivel debe ser un número igual o mayor que 0."
                parsedRemaining == null || parsedRemaining < 0 -> "Los Dados de Golpe restantes deben ser 0 o más."
                parsedDie == null || parsedDie < 0 -> "El tipo de Dado de Golpe debe ser un número válido."
                else -> null
            },
        )
    }
}

@Composable
private fun IdentityModeSelectorV4(
    label: String,
    current: ClassIdentityModeV4,
    onSelect: (ClassIdentityModeV4) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (current == ClassIdentityModeV4.OFFICIAL) "Lista oficial" else "Manual / homebrew")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Lista oficial") }, onClick = { onSelect(ClassIdentityModeV4.OFFICIAL); expanded = false })
                DropdownMenuItem(text = { Text("Manual / homebrew") }, onClick = { onSelect(ClassIdentityModeV4.MANUAL); expanded = false })
            }
        }
    }
}

@Composable
private fun OfficialClassSelectorV4(
    selected: CharacterClassCatalogEntry?,
    onSelect: (CharacterClassCatalogEntry) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Clase oficial", style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.let(::officialClassLabelV4) ?: "Elegir clase oficial", maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CharacterClassCatalog.classes
                    .sortedWith(compareBy<CharacterClassCatalogEntry> { it.nameEs }.thenBy { it.source })
                    .forEach { entry ->
                        DropdownMenuItem(
                            text = { Text(officialClassLabelV4(entry), maxLines = 3) },
                            onClick = { onSelect(entry); expanded = false },
                        )
                    }
            }
        }
    }
}

@Composable
private fun SubclassModeSelectorV4(
    current: SubclassIdentityModeV4,
    officialAvailable: Boolean,
    onSelect: (SubclassIdentityModeV4) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Subclase", style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    when (current) {
                        SubclassIdentityModeV4.NONE -> "Sin subclase"
                        SubclassIdentityModeV4.OFFICIAL -> "Subclase oficial"
                        SubclassIdentityModeV4.MANUAL -> "Manual / homebrew"
                    },
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Sin subclase") }, onClick = { onSelect(SubclassIdentityModeV4.NONE); expanded = false })
                DropdownMenuItem(
                    text = { Text(if (officialAvailable) "Subclase oficial" else "Subclase oficial (elige una clase oficial primero)") },
                    enabled = officialAvailable,
                    onClick = { onSelect(SubclassIdentityModeV4.OFFICIAL); expanded = false },
                )
                DropdownMenuItem(text = { Text("Manual / homebrew") }, onClick = { onSelect(SubclassIdentityModeV4.MANUAL); expanded = false })
            }
        }
    }
}

@Composable
private fun OfficialSubclassSelectorV4(
    classEntry: CharacterClassCatalogEntry?,
    selected: CharacterSubclassCatalogEntry?,
    onSelect: (CharacterSubclassCatalogEntry) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Subclase oficial", style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = classEntry != null && classEntry.subclasses.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(selected?.let(::officialSubclassLabelV4) ?: "Elegir subclase", maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                classEntry?.subclasses
                    ?.sortedWith(compareBy<CharacterSubclassCatalogEntry> { it.name }.thenBy { it.source })
                    ?.forEach { entry ->
                        DropdownMenuItem(
                            text = { Text(officialSubclassLabelV4(entry), maxLines = 3) },
                            onClick = { onSelect(entry); expanded = false },
                        )
                    }
            }
        }
    }
}

@Composable
private fun RulesFamilySelectorV4(
    label: String,
    current: CharacterRulesFamily,
    onSelect: (CharacterRulesFamily) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(rulesFamilyLabelClassV4(current))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CharacterRulesFamily.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(rulesFamilyLabelClassV4(option)) },
                        onClick = { onSelect(option); expanded = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun NumericClassFieldV4(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter(Char::isDigit)) },
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

private fun officialClassLabelV4(entry: CharacterClassCatalogEntry): String =
    "${entry.nameEs} · ${rulesFamilyLabelClassV4(entry.rulesFamily)} · ${entry.source}"

private fun officialSubclassLabelV4(entry: CharacterSubclassCatalogEntry): String =
    "${entry.name} · ${rulesFamilyLabelClassV4(entry.rulesFamily)} · ${entry.source}"

private fun rulesFamilyLabelClassV4(value: CharacterRulesFamily): String = when (value) {
    CharacterRulesFamily.DND_5E -> "D&D 5e"
    CharacterRulesFamily.DND_5_5E -> "D&D 5.5e"
    CharacterRulesFamily.CUSTOM -> "Custom"
    CharacterRulesFamily.UNSPECIFIED -> "Sin especificar"
}

private inline fun <reified T : Enum<T>> enumValueOrDefaultV4(raw: String, fallback: T): T =
    runCatching { enumValueOf<T>(raw) }.getOrDefault(fallback)
