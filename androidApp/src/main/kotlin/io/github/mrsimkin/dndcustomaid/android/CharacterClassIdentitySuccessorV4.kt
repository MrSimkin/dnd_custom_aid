package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassCatalog
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassCatalogEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSubclassCatalogEntry
import kotlin.uuid.Uuid

@Composable
internal fun CharacterClassIdentitySuccessorCardV4(
    classes: List<ClassLevelDraftV4>,
    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,
) {
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Clases", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                CompactClassActionV4("+ Clase") {
                    editorId = null
                    editorOpen = true
                }
            }

            if (classes.isEmpty()) {
                Text("Sin clases configuradas.", style = MaterialTheme.typography.bodySmall)
            } else {
                classes.forEach { item ->
                    val className = classDisplayNameEsV4(item)
                    val subclassName = subclassDisplayNameEsV4(item)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(1.dp)),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            buildString {
                                append(className)
                                subclassName?.let { append(" — "); append(it) }
                                append(" · Nv. "); append(item.level.ifBlank { "0" })
                                append(" · DG "); append(item.hitDiceRemaining.ifBlank { "0" })
                                append("/"); append(item.level.ifBlank { "0" })
                                append(" d"); append(item.hitDieSides.ifBlank { "0" })
                            },
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        CompactClassActionV4("Editar") {
                            editorId = item.id.toString()
                            editorOpen = true
                        }
                        CompactClassActionV4("Quitar") { deleteId = item.id.toString() }
                    }
                }
            }
        }
    }

    if (editorOpen) {
        val existing = editorId?.let { id -> classes.firstOrNull { it.id.toString() == id } }
        CharacterClassIdentitySuccessorEditorV4(
            existing = existing,
            onDismiss = { editorOpen = false },
            onSave = { saved ->
                onClassesChange(
                    if (existing == null) {
                        classes + saved
                    } else {
                        classes.map { item -> if (item.id == existing.id) saved else item }
                    },
                )
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
                itemName = classDisplayNameEsV4(target),
                itemTypeLabel = "clase",
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
private fun CharacterClassIdentitySuccessorEditorV4(
    existing: ClassLevelDraftV4?,
    onDismiss: () -> Unit,
    onSave: (ClassLevelDraftV4) -> Unit,
) {
    var draft by remember(existing?.id) {
        mutableStateOf(
            existing ?: ClassLevelDraftV4(
                id = Uuid.random(),
                name = "",
                level = "1",
                hitDieSides = "8",
                hitDiceRemaining = "1",
            ),
        )
    }
    var manualClass by remember(existing?.id) { mutableStateOf(existing?.catalogKey == null) }
    var manualSubclass by remember(existing?.id) {
        mutableStateOf(existing?.subclassName != null && existing.subclassCatalogKey == null)
    }
    var customSubclassName by remember(existing?.id) { mutableStateOf(existing?.subclassName.orEmpty()) }

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir clase" else "Editar clase",
        saveEnabled = draft.name.trim().isNotEmpty(),
        onCancel = onDismiss,
        onSave = {
            val level = draft.level.toIntOrNull()?.coerceAtLeast(0) ?: 0
            val remaining = draft.hitDiceRemaining.toIntOrNull()?.coerceIn(0, level) ?: 0
            onSave(
                draft.copy(
                    level = level.toString(),
                    hitDiceRemaining = remaining.toString(),
                ),
            )
        },
    ) {
        ClassChoiceSuccessorV4(
            draft = draft,
            manual = manualClass,
            onManualChange = { manualClass = it },
            onDraftChange = { updated ->
                draft = updated
                manualSubclass = updated.subclassName != null && updated.subclassCatalogKey == null
                customSubclassName = updated.subclassName.orEmpty()
            },
        )

        if (manualClass) {
            CompactLabeledTextInputSuccessorV4(
                label = "Clase",
                value = draft.name,
                onValueChange = { value -> draft = draft.withManualName(value) },
            )
        }

        SubclassChoiceSuccessorV4(
            draft = draft,
            manual = manualSubclass,
            customName = customSubclassName,
            onManualChange = { manualSubclass = it },
            onCustomNameChange = { value ->
                customSubclassName = value
                draft = draft.copy(
                    subclassName = value.trim().takeIf(String::isNotEmpty),
                    subclassSource = null,
                    subclassCatalogKey = null,
                    subclassRulesFamily = CharacterRulesFamily.UNSPECIFIED,
                )
            },
            onDraftChange = { draft = it },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            CompactLabeledNumberInputSuccessorV4(
                label = "Nivel",
                value = draft.level,
                onValueChange = { value ->
                    val clean = value.filter(Char::isDigit)
                    val level = clean.toIntOrNull()
                    val remaining = draft.hitDiceRemaining.toIntOrNull()
                    draft = draft.copy(
                        level = clean,
                        hitDiceRemaining = if (level != null && remaining != null && remaining > level) level.toString() else draft.hitDiceRemaining,
                    )
                },
                modifier = Modifier.weight(1f),
            )
            CompactLabeledNumberInputSuccessorV4(
                label = "DG restantes",
                value = draft.hitDiceRemaining,
                onValueChange = { value ->
                    val clean = value.filter(Char::isDigit)
                    val level = draft.level.toIntOrNull()
                    val remaining = clean.toIntOrNull()
                    draft = draft.copy(
                        hitDiceRemaining = if (level != null && remaining != null) remaining.coerceAtMost(level).toString() else clean,
                    )
                },
                modifier = Modifier.weight(1f),
            )
            CompactLabeledNumberInputSuccessorV4(
                label = "Dado",
                value = draft.hitDieSides,
                onValueChange = { value -> draft = draft.copy(hitDieSides = value.filter(Char::isDigit)) },
                prefix = "d",
                modifier = Modifier.weight(1f),
            )
        }
        CharacterHelpV4("Los DG máximos se derivan del nivel de esta clase; solo se guarda cuántos quedan disponibles y el tipo de dado.")
    }
}

@Composable
private fun ClassChoiceSuccessorV4(
    draft: ClassLevelDraftV4,
    manual: Boolean,
    onManualChange: (Boolean) -> Unit,
    onDraftChange: (ClassLevelDraftV4) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val currentCatalog = CharacterClassCatalog.findClass(draft.catalogKey)
    val display = if (manual) "Personalizada" else currentCatalog?.nameEs ?: classDisplayNameEsV4(draft)

    Column {
        Text("Clase", style = MaterialTheme.typography.labelSmall)
        Box {
            CompactClassMenuV4(display) { expanded = true }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                preferredClassEntriesV4().forEach { entry ->
                    DropdownMenuItem(
                        text = { Text(entry.nameEs) },
                        onClick = {
                            val hitDie = officialHitDieV4(entry.nameEs)
                            onManualChange(false)
                            onDraftChange(
                                draft.copy(
                                    name = entry.nameEs,
                                    rulesFamily = entry.rulesFamily,
                                    source = entry.source,
                                    catalogKey = entry.key,
                                    hitDieSides = hitDie?.toString() ?: draft.hitDieSides,
                                    subclassName = null,
                                    subclassSource = null,
                                    subclassCatalogKey = null,
                                    subclassRulesFamily = CharacterRulesFamily.UNSPECIFIED,
                                ),
                            )
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Personalizada") },
                    onClick = {
                        onManualChange(true)
                        onDraftChange(
                            draft.copy(
                                rulesFamily = CharacterRulesFamily.UNSPECIFIED,
                                source = null,
                                catalogKey = null,
                                subclassName = null,
                                subclassSource = null,
                                subclassCatalogKey = null,
                                subclassRulesFamily = CharacterRulesFamily.UNSPECIFIED,
                            ),
                        )
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SubclassChoiceSuccessorV4(
    draft: ClassLevelDraftV4,
    manual: Boolean,
    customName: String,
    onManualChange: (Boolean) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onDraftChange: (ClassLevelDraftV4) -> Unit,
) {
    val classEntry = CharacterClassCatalog.findClass(draft.catalogKey)
    var expanded by remember { mutableStateOf(false) }
    val current = classEntry?.subclasses?.firstOrNull { it.key == draft.subclassCatalogKey }
    val label = when {
        manual -> "Personalizada"
        current != null -> subclassNameEsV4(current.name)
        draft.subclassName.isNullOrBlank() -> "Sin subclase"
        else -> subclassNameEsV4(draft.subclassName.orEmpty())
    }

    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
        Text("Subclase", style = MaterialTheme.typography.labelSmall)
        Box {
            CompactClassMenuV4(label) { expanded = true }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Sin subclase") },
                    onClick = {
                        onManualChange(false)
                        onDraftChange(
                            draft.copy(
                                subclassName = null,
                                subclassSource = null,
                                subclassCatalogKey = null,
                                subclassRulesFamily = CharacterRulesFamily.UNSPECIFIED,
                            ),
                        )
                        expanded = false
                    },
                )
                preferredSubclassEntriesV4(classEntry).forEach { entry ->
                    DropdownMenuItem(
                        text = { Text(subclassNameEsV4(entry.name)) },
                        onClick = {
                            onManualChange(false)
                            onDraftChange(
                                draft.copy(
                                    subclassName = subclassNameEsV4(entry.name),
                                    subclassSource = entry.source,
                                    subclassCatalogKey = entry.key,
                                    subclassRulesFamily = entry.rulesFamily,
                                ),
                            )
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Personalizada") },
                    onClick = {
                        onManualChange(true)
                        onDraftChange(
                            draft.copy(
                                subclassName = customName.trim().takeIf(String::isNotEmpty),
                                subclassSource = null,
                                subclassCatalogKey = null,
                                subclassRulesFamily = CharacterRulesFamily.UNSPECIFIED,
                            ),
                        )
                        expanded = false
                    },
                )
            }
        }
        if (manual) {
            CompactLabeledTextInputSuccessorV4(
                label = "Nombre de subclase",
                value = customName,
                onValueChange = onCustomNameChange,
            )
        }
    }
}

@Composable
private fun CompactClassActionV4(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Box(modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun CompactClassMenuV4(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(min = 34.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Box(modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp), contentAlignment = Alignment.CenterStart) {
            Text(text, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CompactLabeledTextInputSuccessorV4(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
    }
}

@Composable
private fun CompactLabeledNumberInputSuccessorV4(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String = "",
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            prefix = if (prefix.isBlank()) null else ({ Text(prefix) }),
        )
    }
}

private fun preferredClassEntriesV4(): List<CharacterClassCatalogEntry> =
    CharacterClassCatalog.classes
        .groupBy { it.nameEs.lowercase() }
        .values
        .map { group -> group.first() }
        .sortedBy { it.nameEs }

private fun preferredSubclassEntriesV4(classEntry: CharacterClassCatalogEntry?): List<CharacterSubclassCatalogEntry> =
    classEntry?.subclasses
        .orEmpty()
        .groupBy { subclassNameEsV4(it.name).lowercase() }
        .values
        .map { group -> group.first() }
        .sortedBy { subclassNameEsV4(it.name) }

private fun classDisplayNameEsV4(item: ClassLevelDraftV4): String =
    CharacterClassCatalog.findClass(item.catalogKey)?.nameEs ?: item.name

private fun subclassDisplayNameEsV4(item: ClassLevelDraftV4): String? {
    val catalog = CharacterClassCatalog.findClass(item.catalogKey)
        ?.subclasses
        ?.firstOrNull { it.key == item.subclassCatalogKey }
    return when {
        catalog != null -> subclassNameEsV4(catalog.name)
        item.subclassName.isNullOrBlank() -> null
        else -> subclassNameEsV4(item.subclassName.orEmpty())
    }
}

private fun officialHitDieV4(classNameEs: String): Int? = when (classNameEs) {
    "Bárbaro" -> 12
    "Guerrero", "Paladín", "Explorador" -> 10
    "Artífice", "Bardo", "Clérigo", "Druida", "Monje", "Pícaro", "Brujo" -> 8
    "Hechicero", "Mago" -> 6
    else -> null
}

private fun subclassNameEsV4(raw: String): String = subclassNamesEsV4[raw] ?: raw

private val subclassNamesEsV4 = mapOf(
    "Alchemist" to "Alquimista",
    "Armorer" to "Armero",
    "Artillerist" to "Artillero",
    "Battle Smith" to "Herrero de Batalla",
    "Cartographer" to "Cartógrafo",
    "Reanimator" to "Reanimador",
    "Berserker" to "Berserker",
    "Wild Heart" to "Corazón Salvaje",
    "World Tree" to "Árbol del Mundo",
    "Zealot" to "Fanático",
    "Totem Warrior" to "Guerrero Totémico",
    "Battlerager" to "Furibundo Acorazado",
    "Ancestral Guardian" to "Guardián Ancestral",
    "Storm Herald" to "Heraldo de la Tormenta",
    "Beast" to "Bestia",
    "Wild Magic" to "Magia Salvaje",
    "Giant" to "Gigante",
    "Dance" to "Danza",
    "Glamour" to "Glamur",
    "Lore" to "Conocimiento",
    "Valor" to "Valor",
    "Swords" to "Espadas",
    "Whispers" to "Susurros",
    "Creation" to "Creación",
    "Eloquence" to "Elocuencia",
    "Spirits" to "Espíritus",
    "Moon" to "Luna",
    "Life" to "Vida",
    "Light" to "Luz",
    "Trickery" to "Engaño",
    "War" to "Guerra",
    "Knowledge" to "Conocimiento",
    "Nature" to "Naturaleza",
    "Tempest" to "Tempestad",
    "Death" to "Muerte",
    "Arcana" to "Arcanos",
    "Forge" to "Forja",
    "Grave" to "Tumba",
    "Order" to "Orden",
    "Peace" to "Paz",
    "Twilight" to "Crepúsculo",
    "Land" to "Tierra",
    "Sea" to "Mar",
    "Stars" to "Estrellas",
    "Dreams" to "Sueños",
    "Shepherd" to "Pastor",
    "Spores" to "Esporas",
    "Wildfire" to "Fuego Salvaje",
    "Battle Master" to "Maestro de Batalla",
    "Champion" to "Campeón",
    "Eldritch Knight" to "Caballero Arcano",
    "Psi Warrior" to "Guerrero Psiónico",
    "Arcane Archer" to "Arquero Arcano",
    "Cavalier" to "Caballero",
    "Samurai" to "Samurái",
    "Rune Knight" to "Caballero Rúnico",
    "Echo Knight" to "Caballero del Eco",
    "Purple Dragon Knight" to "Caballero del Dragón Púrpura",
    "Banneret" to "Estandarte",
    "Mercy" to "Misericordia",
    "Shadow" to "Sombra",
    "Elements" to "Elementos",
    "Open Hand" to "Mano Abierta",
    "Four Elements" to "Cuatro Elementos",
    "Long Death" to "Muerte Prolongada",
    "Sun Soul" to "Alma Solar",
    "Drunken Master" to "Maestro Borracho",
    "Kensei" to "Kensei",
    "Astral Self" to "Yo Astral",
    "Ascendant Dragon" to "Dragón Ascendente",
    "Warrior of the Mystic Arts" to "Guerrero de las Artes Místicas",
    "Devotion" to "Devoción",
    "Glory" to "Gloria",
    "Ancients" to "Antiguos",
    "Vengeance" to "Venganza",
    "Oathbreaker" to "Rompejuramentos",
    "Crown" to "Corona",
    "Conquest" to "Conquista",
    "Redemption" to "Redención",
    "Watchers" to "Vigilantes",
    "Noble Genies" to "Genios Nobles",
    "Beast Master" to "Maestro de Bestias",
    "Fey Wanderer" to "Vagabundo Feérico",
    "Gloom Stalker" to "Acechador Sombrío",
    "Hunter" to "Cazador",
    "Horizon Walker" to "Caminante del Horizonte",
    "Monster Slayer" to "Cazador de Monstruos",
    "Swarmkeeper" to "Guardián del Enjambre",
    "Drakewarden" to "Guardián de Dracos",
    "Assassin" to "Asesino",
    "Soulknife" to "Cuchillo del Alma",
    "Thief" to "Ladrón",
    "Arcane Trickster" to "Embaucador Arcano",
    "Inquisitive" to "Inquisitivo",
    "Mastermind" to "Mente Maestra",
    "Scout" to "Explorador",
    "Swashbuckler" to "Espadachín",
    "Phantom" to "Fantasma",
    "Aberrant Mind" to "Mente Aberrante",
    "Clockwork Soul" to "Alma Mecánica",
    "Draconic Sorcery" to "Hechicería Dracónica",
    "Draconic Bloodline" to "Linaje Dracónico",
    "Divine Soul" to "Alma Divina",
    "Shadow Magic" to "Magia Sombría",
    "Storm Sorcery" to "Hechicería de Tormenta",
    "Lunar Sorcery" to "Hechicería Lunar",
    "Archfey" to "Archifey",
    "Celestial" to "Celestial",
    "Fiend" to "Infernal",
    "Great Old One" to "Gran Antiguo",
    "Hexblade" to "Hoja Maldita",
    "Fathomless" to "Insondable",
    "Genie" to "Genio",
    "Undead" to "No Muerto",
    "Undying" to "Imperecedero",
    "Bladesinging" to "Canto de Espadas",
    "Divination" to "Adivinación",
    "Evocation" to "Evocación",
    "Abjuration" to "Abjuración",
    "Conjuration" to "Conjuración",
    "Enchantment" to "Encantamiento",
    "Illusion" to "Ilusión",
    "Necromancy" to "Nigromancia",
    "Transmutation" to "Transmutación",
    "War Magic" to "Magia de Guerra",
    "Chronurgy" to "Cronurgia",
    "Graviturgy" to "Graviturgia",
    "Scribes" to "Escribas",
)
