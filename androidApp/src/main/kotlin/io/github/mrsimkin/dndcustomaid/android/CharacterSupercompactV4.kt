package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessProjection
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterDamage
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterHealing
import io.github.mrsimkin.dndcustomaid.shared.character.customSkillTotal
import io.github.mrsimkin.dndcustomaid.shared.character.projectCharacterQuickAccess
import kotlin.math.abs

private data class SupercompactTileV4(
    val label: String,
    val value: String,
)

@Composable
internal fun CharacterSupercompactV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    liveControlsEnabled: Boolean,
    onSheetChange: (CharacterSheet) -> Unit,
    onBack: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = when {
                maxWidth >= 1100.dp -> 7
                maxWidth >= 850.dp -> 6
                maxWidth >= 650.dp -> 5
                maxWidth >= 470.dp -> 4
                else -> 3
            }
            val favoriteColumns = if (maxWidth >= 760.dp) 2 else 1
            val favorites = projectCharacterQuickAccess(sheet, closureState)
            val slots = sheet.spellSlots
                .filter { it.totalSlots > 0 }
                .sortedBy { it.level }
            val tiles = buildList {
                add(SupercompactTileV4("CA", sheet.armorClass.toString()))
                add(
                    SupercompactTileV4(
                        "PG",
                        buildString {
                            append(sheet.currentHp)
                            append('/')
                            append(sheet.maxHp)
                            if (sheet.tempHp != 0) append(" +${sheet.tempHp} temp")
                        },
                    ),
                )
                add(SupercompactTileV4("Vel.", formatSupercompactSpeedV4(sheet.speed)))
                add(SupercompactTileV4("Inic.", formatSupercompactSignedV4(sheet.initiativeModifier)))
                add(SupercompactTileV4("Compet.", formatSupercompactSignedV4(sheet.finalProficiencyBonus)))
                add(SupercompactTileV4("Percep. pasiva", sheet.passivePerception.toString()))
                add(SupercompactTileV4("Inspiración", if (sheet.inspiration) "Sí" else "No"))
                if (sheet.currentHp <= 0 || sheet.deathSaveSuccesses > 0 || sheet.deathSaveFailures > 0) {
                    add(
                        SupercompactTileV4(
                            "Salv. muerte",
                            "${sheet.deathSaveSuccesses} ✓ · ${sheet.deathSaveFailures} ✕",
                        ),
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item(key = "supercompact-header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        StableBackIconButton(
                            onClick = onBack,
                            contentDescription = "Volver a Ajustes de personaje",
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Vista supercompacta", style = MaterialTheme.typography.titleLarge)
                            Text(
                                if (closureState.tableModeEnabled) {
                                    "Consulta rápida · Modo Mesa activo"
                                } else {
                                    "Consulta rápida · datos de la ficha"
                                },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }

                item(key = "supercompact-identity") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(sheet.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                sheet.classes.joinToString(" · ") { classLevel ->
                                    buildString {
                                        append(classLevel.name)
                                        append(' ')
                                        append(classLevel.level)
                                        classLevel.subclassName?.takeIf { it.isNotBlank() }?.let { subclass ->
                                            append(" · ")
                                            append(subclass)
                                        }
                                    }
                                }.ifBlank { "Sin clase registrada" },
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                "${supercompactStatusLabelV4(sheet.status)} · Nivel ${sheet.totalLevel}",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }

                if (!liveControlsEnabled) {
                    item(key = "supercompact-pending-warning") {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Hay cambios estructurales sin guardar. Guarda o descártalos para habilitar PG, recursos y espacios desde esta vista.",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }

                item(key = "supercompact-operational") {
                    SupercompactGridV4(tiles = tiles, columns = columns)
                }

                item(key = "supercompact-hp-controls") {
                    SupercompactHpControlsV4(
                        sheet = sheet,
                        liveControlsEnabled = liveControlsEnabled,
                        onSheetChange = onSheetChange,
                    )
                }

                if (slots.isNotEmpty()) {
                    item(key = "supercompact-slots") {
                        SupercompactSpellSlotsV4(
                            sheet = sheet,
                            liveControlsEnabled = liveControlsEnabled,
                            onSheetChange = onSheetChange,
                        )
                    }
                }

                item(key = "supercompact-favorites") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text("Favoritos", style = MaterialTheme.typography.titleSmall)
                        if (favorites.isEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Aún no hay accesos rápidos. Marca ★ en ataques, rasgos, conjuros, recursos, formas, compañeros u otros elementos de la ficha.",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        } else {
                            favorites.chunked(favoriteColumns).forEach { rowFavorites ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    rowFavorites.forEach { favorite ->
                                        SupercompactFavoriteCardV4(
                                            favorite = favorite,
                                            sheet = sheet,
                                            closureState = closureState,
                                            liveControlsEnabled = liveControlsEnabled,
                                            onSheetChange = onSheetChange,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    repeat(favoriteColumns - rowFavorites.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                item(key = "supercompact-abilities") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text("Características", style = MaterialTheme.typography.titleSmall)
                        val abilityTiles = CharacterAbility.entries.map { ability ->
                            SupercompactTileV4(
                                label = supercompactAbilityLabelV4(ability),
                                value = "${sheet.abilityScore(ability)} · ${formatSupercompactSignedV4(sheet.abilityModifier(ability))}",
                            )
                        }
                        SupercompactGridV4(tiles = abilityTiles, columns = columns.coerceAtMost(6))
                    }
                }

                item(key = "supercompact-note") {
                    Text(
                        "Esta vista no guarda una copia independiente: Favoritos, PG, recursos y espacios se leen y actualizan sobre los mismos datos persistidos de la ficha.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun SupercompactHpControlsV4(
    sheet: CharacterSheet,
    liveControlsEnabled: Boolean,
    onSheetChange: (CharacterSheet) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 9.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Puntos de Golpe", style = MaterialTheme.typography.labelMedium)
                Text(
                    buildString {
                        append(sheet.currentHp)
                        append('/')
                        append(sheet.maxHp)
                        if (sheet.tempHp > 0) append(" · ${sheet.tempHp} temporales")
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            TextButton(
                onClick = { onSheetChange(applyCharacterDamage(sheet, 1)) },
                enabled = liveControlsEnabled && (sheet.currentHp > 0 || sheet.tempHp > 0),
            ) { Text("−1") }
            TextButton(
                onClick = { onSheetChange(applyCharacterHealing(sheet, 1)) },
                enabled = liveControlsEnabled && sheet.currentHp < sheet.maxHp,
            ) { Text("+1") }
        }
    }
}

@Composable
private fun SupercompactSpellSlotsV4(
    sheet: CharacterSheet,
    liveControlsEnabled: Boolean,
    onSheetChange: (CharacterSheet) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text("Espacios de conjuro", style = MaterialTheme.typography.titleSmall)
        sheet.spellSlots
            .filter { it.totalSlots > 0 }
            .sortedBy { it.level }
            .forEach { slot ->
                val available = (slot.totalSlots - slot.spentSlots).coerceAtLeast(0)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 9.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Nivel ${slot.level}", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "$available/${slot.totalSlots} disponibles",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(
                            onClick = {
                                onSheetChange(
                                    sheet.copy(
                                        spellSlots = sheet.spellSlots.map { current ->
                                            if (current.level == slot.level) {
                                                current.copy(
                                                    spentSlots = (current.spentSlots + 1)
                                                        .coerceAtMost(current.totalSlots),
                                                )
                                            } else {
                                                current
                                            }
                                        },
                                    ),
                                )
                            },
                            enabled = liveControlsEnabled && available > 0,
                        ) { Text("Usar") }
                        TextButton(
                            onClick = {
                                onSheetChange(
                                    sheet.copy(
                                        spellSlots = sheet.spellSlots.map { current ->
                                            if (current.level == slot.level) {
                                                current.copy(spentSlots = (current.spentSlots - 1).coerceAtLeast(0))
                                            } else {
                                                current
                                            }
                                        },
                                    ),
                                )
                            },
                            enabled = liveControlsEnabled && slot.spentSlots > 0,
                        ) { Text("Recup.") }
                    }
                }
            }
    }
}

@Composable
private fun SupercompactFavoriteCardV4(
    favorite: CharacterQuickAccessProjection,
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    liveControlsEnabled: Boolean,
    onSheetChange: (CharacterSheet) -> Unit,
    modifier: Modifier = Modifier,
) {
    val detail = supercompactFavoriteDetailV4(favorite, sheet, closureState)
    val resource = if (favorite.kind == CharacterQuickAccessKind.RESOURCE) {
        sheet.resources.firstOrNull { it.id == favorite.targetId }
    } else {
        null
    }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(supercompactFavoriteKindLabelV4(favorite.kind), style = MaterialTheme.typography.labelSmall)
            Text(favorite.name, style = MaterialTheme.typography.titleSmall, maxLines = 2)
            detail?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 3)
            }
            if (resource != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            onSheetChange(
                                sheet.copy(
                                    resources = sheet.resources.map { current ->
                                        if (current.id == resource.id) {
                                            current.copy(currentValue = (current.currentValue - 1).coerceAtLeast(0))
                                        } else {
                                            current
                                        }
                                    },
                                ),
                            )
                        },
                        enabled = liveControlsEnabled && resource.currentValue > 0,
                    ) { Text("−1") }
                    TextButton(
                        onClick = {
                            onSheetChange(
                                sheet.copy(
                                    resources = sheet.resources.map { current ->
                                        if (current.id == resource.id) {
                                            current.copy(
                                                currentValue = current.maxValue?.let { max ->
                                                    (current.currentValue + 1).coerceAtMost(max)
                                                } ?: (current.currentValue + 1),
                                            )
                                        } else {
                                            current
                                        }
                                    },
                                ),
                            )
                        },
                        enabled = liveControlsEnabled && (resource.maxValue == null || resource.currentValue < resource.maxValue),
                    ) { Text("+1") }
                }
            }
        }
    }
}

private fun supercompactFavoriteDetailV4(
    favorite: CharacterQuickAccessProjection,
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
): String? = when (favorite.kind) {
    CharacterQuickAccessKind.COMBAT_ENTRY -> sheet.combatEntries
        .firstOrNull { it.id == favorite.targetId }
        ?.let { entry ->
            listOfNotNull(
                supercompactCombatTypeLabelV4(entry.type),
                entry.attackModifier?.let { formatSupercompactSignedV4(it) },
                entry.damageEffect.takeIf { it.isNotBlank() },
            ).joinToString(" · ")
        }
    CharacterQuickAccessKind.TRAIT -> sheet.traits
        .firstOrNull { it.id == favorite.targetId }
        ?.let { trait ->
            trait.maxUses?.let { max ->
                "Usos ${(max - trait.spentUses).coerceAtLeast(0)}/$max"
            } ?: trait.source.takeIf { it.isNotBlank() }
        }
    CharacterQuickAccessKind.SPELL -> sheet.spells
        .firstOrNull { it.id == favorite.targetId }
        ?.let { spell ->
            buildList {
                add(if (spell.level == 0) "Truco" else "Nivel ${spell.level}")
                if (spell.concentration) add("Concentración")
                if (spell.ritual) add("Ritual")
            }.joinToString(" · ")
        }
    CharacterQuickAccessKind.RESOURCE -> sheet.resources
        .firstOrNull { it.id == favorite.targetId }
        ?.let { resource ->
            resource.maxValue?.let { "${resource.currentValue}/$it" } ?: resource.currentValue.toString()
        }
    CharacterQuickAccessKind.CLASS_OPTION -> sheet.classOptions
        .firstOrNull { it.id == favorite.targetId }
        ?.let { option -> option.effectSummary.takeIf { it.isNotBlank() } ?: option.source }
    CharacterQuickAccessKind.FORM -> sheet.forms
        .firstOrNull { it.id == favorite.targetId }
        ?.let { form ->
            listOfNotNull(
                form.challengeRatingText?.takeIf { it.isNotBlank() }?.let { "CR $it" },
                form.armorClass?.let { "CA $it" },
                form.hitPoints?.let { "PG $it" },
            ).joinToString(" · ").takeIf { it.isNotBlank() }
        }
    CharacterQuickAccessKind.COMPANION -> sheet.companions
        .firstOrNull { it.id == favorite.targetId }
        ?.let { companion ->
            listOfNotNull(
                companion.kind.takeIf { it.isNotBlank() },
                companion.currentHp?.let { current ->
                    companion.maxHp?.let { max -> "$current/$max PG" } ?: "$current PG"
                },
                companion.source?.takeIf { it.isNotBlank() },
            ).joinToString(" · ").takeIf { it.isNotBlank() }
        }
    CharacterQuickAccessKind.CUSTOM_SKILL -> closureState.customSkills
        .firstOrNull { it.id == favorite.targetId }
        ?.let { skill ->
            "${supercompactAbilityLabelV4(skill.ability)} · ${formatSupercompactSignedV4(sheet.customSkillTotal(skill))}"
        }
    CharacterQuickAccessKind.TEMPORARY_EFFECT -> closureState.temporaryEffects
        .firstOrNull { it.id == favorite.targetId }
        ?.let { effect ->
            listOfNotNull(
                effect.summary.takeIf { it.isNotBlank() },
                effect.durationText?.takeIf { it.isNotBlank() },
            ).joinToString(" · ").takeIf { it.isNotBlank() }
        }
    CharacterQuickAccessKind.OTHER -> null
}

private fun supercompactFavoriteKindLabelV4(kind: CharacterQuickAccessKind): String = when (kind) {
    CharacterQuickAccessKind.COMBAT_ENTRY -> "Combate"
    CharacterQuickAccessKind.TRAIT -> "Rasgo"
    CharacterQuickAccessKind.SPELL -> "Conjuro"
    CharacterQuickAccessKind.RESOURCE -> "Recurso"
    CharacterQuickAccessKind.CLASS_OPTION -> "Opción de clase"
    CharacterQuickAccessKind.FORM -> "Forma"
    CharacterQuickAccessKind.COMPANION -> "Compañero"
    CharacterQuickAccessKind.CUSTOM_SKILL -> "Habilidad"
    CharacterQuickAccessKind.TEMPORARY_EFFECT -> "Efecto temporal"
    CharacterQuickAccessKind.OTHER -> "Otro"
}

private fun supercompactCombatTypeLabelV4(type: CharacterCombatEntryType): String = when (type) {
    CharacterCombatEntryType.ATTACK -> "Ataque"
    CharacterCombatEntryType.ACTION -> "Acción"
    CharacterCombatEntryType.BONUS_ACTION -> "Acción adicional"
    CharacterCombatEntryType.REACTION -> "Reacción"
    CharacterCombatEntryType.OTHER -> "Otro"
}

@Composable
private fun SupercompactGridV4(
    tiles: List<SupercompactTileV4>,
    columns: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        tiles.chunked(columns.coerceAtLeast(1)).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Top,
            ) {
                rowTiles.forEach { tile ->
                    Card(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(tile.label, style = MaterialTheme.typography.labelSmall, maxLines = 2)
                            Text(tile.value, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                        }
                    }
                }
                repeat(columns - rowTiles.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun supercompactAbilityLabelV4(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "FUE"
    CharacterAbility.DEXTERITY -> "DES"
    CharacterAbility.CONSTITUTION -> "CON"
    CharacterAbility.INTELLIGENCE -> "INT"
    CharacterAbility.WISDOM -> "SAB"
    CharacterAbility.CHARISMA -> "CAR"
}

private fun supercompactStatusLabelV4(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

private fun formatSupercompactSignedV4(value: Int): String = if (value >= 0) "+$value" else value.toString()

private fun formatSupercompactSpeedV4(feet: Int): String {
    val metricTenths = feet * 3
    val wholeMeters = metricTenths / 10
    val remainder = abs(metricTenths % 10)
    val metric = if (remainder == 0) wholeMeters.toString() else "$wholeMeters,$remainder"
    return "$feet ft · $metric m"
}
