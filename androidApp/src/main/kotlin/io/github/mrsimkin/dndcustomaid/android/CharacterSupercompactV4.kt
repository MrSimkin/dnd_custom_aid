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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import kotlin.math.abs

private data class SupercompactTileV4(
    val label: String,
    val value: String,
)

@Composable
internal fun CharacterSupercompactV4(
    sheet: CharacterSheet,
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
                            Text("Experimental · consulta rápida", style = MaterialTheme.typography.labelSmall)
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

                item(key = "supercompact-operational") {
                    SupercompactGridV4(tiles = tiles, columns = columns)
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
                        "Esta vista usa los mismos datos de la ficha; no mantiene una copia independiente. Favoritos y controles operativos se integrarán sobre esta misma superficie.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
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
