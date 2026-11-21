package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind
import io.github.mrsimkin.dndcustomaid.shared.content.RulesOrigin
import io.github.mrsimkin.dndcustomaid.shared.dm.ABILITY_CODES
import io.github.mrsimkin.dndcustomaid.shared.dm.ABILITY_NAMES_ES
import io.github.mrsimkin.dndcustomaid.shared.dm.CharacterBuild
import io.github.mrsimkin.dndcustomaid.shared.dm.ClassChoice
import io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption
import io.github.mrsimkin.dndcustomaid.shared.dm.STANDARD_ARRAY
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdRules
import io.github.mrsimkin.dndcustomaid.shared.dm.buildScores
import io.github.mrsimkin.dndcustomaid.shared.dm.pointBuyCost
import kotlin.uuid.Uuid

/*
 * QA 0.7.0-preqa.2 (J1): a new PC step by step: race, one or more classes (multiclass), background,
 * ability scores with their bonuses, skills and starting equipment. The SRD's rules fill the sheet
 * (hit points, saves, training, features, feat, gear); homebrew picks keep their text.
 *
 * Owner, 2026-11-18: this must exist on Windows too, so the steps live here and each App only puts
 * its own window around them ([NewCharacterSteps] inside the platform's dialog).
 */

internal enum class ScoreMethod(val label: String) { STANDARD("Arreglo estándar"), POINT_BUY("Compra de puntos (27)"), MANUAL("A mano o tiradas") }

/** What the wizard has gathered so far. The App reads [name], [campaign] and [build] to create. */
class NewCharacterState internal constructor(
    val campaigns: List<Campaign>,
    defaultCampaignId: Uuid,
) {
    internal var rules by mutableStateOf<SrdRules?>(null)
    var name by mutableStateOf("")
    internal var campaignIdText by mutableStateOf(defaultCampaignId.toString())
    internal var edition by mutableStateOf("5.5e")
    internal var race by mutableStateOf<RulesOption?>(null)
    internal var subrace by mutableStateOf<RulesOption?>(null)
    internal var classes by mutableStateOf(listOf<Pair<RulesOption, Int>>())
    internal var subclasses by mutableStateOf(mapOf<Int, RulesOption>())
    internal var background by mutableStateOf<RulesOption?>(null)
    internal var method by mutableStateOf(ScoreMethod.STANDARD)
    internal var base by mutableStateOf(ABILITY_CODES.associate { it.first to 10 })
    internal var bonusPlan by mutableStateOf("2+1")
    internal var plusTwo by mutableStateOf<String?>(null)
    internal var plusOne by mutableStateOf<String?>(null)
    internal var skills by mutableStateOf(setOf<String>())
    internal var classEquipment by mutableStateOf("A")
    internal var backgroundEquipment by mutableStateOf("A")

    val campaign: Campaign get() = campaigns.firstOrNull { it.id.toString() == campaignIdText } ?: campaigns.first()

    /** True once the PC has a name; everything else can be filled in later on the sheet. */
    val ready: Boolean get() = name.isNotBlank()

    private fun srd(option: RulesOption?) = option?.takeIf { it.origin == RulesOrigin.SRD }

    internal val raceRules get() = srd(race)?.let { rules?.race(it.edition, it.name) }
    internal val subraceRules get() = srd(subrace)?.let { rules?.race(it.edition, it.name) }
    internal val backgroundRules get() = srd(background)?.let { rules?.background(it.edition, it.name) }

    internal val classChoices: List<ClassChoice>
        get() = classes.mapIndexed { index, (option, level) ->
            val sub = subclasses[index]
            ClassChoice(
                name = option.name,
                level = level,
                edition = option.edition.ifBlank { edition },
                source = option.source,
                rules = srd(option)?.let { rules?.characterClass(it.edition, it.name) },
                subclassName = sub?.name,
                subclassSource = sub?.source,
                subclassRules = srd(sub)?.let { rules?.subclass(it.edition, it.name) },
                customText = listOfNotNull(
                    option.takeIf { it.origin != RulesOrigin.SRD }?.body,
                    sub?.takeIf { it.origin != RulesOrigin.SRD }?.body,
                ).joinToString("\n\n"),
                equipmentOption = classEquipment,
            )
        }

    /** Everything the wizard knows, ready for `buildCharacter`. */
    val build: CharacterBuild
        get() {
            val backgroundRules = backgroundRules
            val bonus = when {
                backgroundRules == null || backgroundRules.abilities.isEmpty() -> emptyMap()
                bonusPlan == "1+1+1" -> backgroundRules.abilities.associateWith { 1 }
                else -> buildMap { plusTwo?.let { put(it, 2) }; plusOne?.takeIf { it != plusTwo }?.let { put(it, 1) } }
            }
            return CharacterBuild(
                name = name.trim(),
                edition = edition,
                raceName = race?.name.orEmpty(),
                raceSource = race?.source.orEmpty(),
                race = raceRules,
                subrace = subraceRules,
                raceCustomText = race?.takeIf { it.origin != RulesOrigin.SRD }?.body.orEmpty(),
                classes = classChoices,
                backgroundName = background?.name.orEmpty(),
                backgroundSource = background?.source.orEmpty(),
                background = backgroundRules,
                backgroundCustomText = background?.takeIf { it.origin != RulesOrigin.SRD }?.body.orEmpty(),
                backgroundEquipment = backgroundEquipment,
                baseScores = base,
                bonusChoice = bonus,
                classSkills = skills.toList(),
                feat = backgroundRules?.feat?.takeIf { it.isNotBlank() }?.let { rules?.feat(backgroundRules.edition, it) },
            )
        }

    /** The build to apply, or null when nothing was picked and the PC starts empty. */
    fun buildOrNull(): CharacterBuild? = build.takeIf { classes.isNotEmpty() || race != null || background != null }
}

/** Holds the wizard's answers and loads the SRD once. */
@Composable
fun rememberNewCharacterState(
    campaigns: List<Campaign>,
    defaultCampaignId: Uuid,
    loadRules: suspend () -> SrdRules,
): NewCharacterState {
    val state = remember(campaigns, defaultCampaignId) { NewCharacterState(campaigns, defaultCampaignId) }
    LaunchedEffect(state) { state.rules = loadRules() }
    return state
}

/** The steps themselves. Each App puts its own window, title and «Crear personaje» around this. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewCharacterSteps(state: NewCharacterState) {
    val scores = buildScores(state.build)
    val backgroundRules = state.backgroundRules
    val classChoices = state.classChoices

    Step("1. Nombre y campaña") {
        DmTextField(
            value = state.name,
            onValueChange = { state.name = it },
            label = { Text("Nombre del personaje") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        if (state.campaigns.size > 1) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                state.campaigns.forEach { campaign ->
                    FilterChip(
                        selected = state.campaign.id == campaign.id,
                        onClick = { state.campaignIdText = campaign.id.toString() },
                        label = { Text(campaign.name) },
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Reglas:", style = MaterialTheme.typography.bodySmall)
            listOf("5.5e" to "D&D 5.5e (2024)", "5e" to "D&D 5e (2014)").forEach { (key, label) ->
                FilterChip(selected = state.edition == key, onClick = { state.edition = key }, label = { Text(label) })
            }
        }
    }

    Step("2. Raza") {
        Text(listOfNotNull(state.race?.name, state.subrace?.name).joinToString(" · ").ifBlank { "Sin elegir" }, fontWeight = FontWeight.SemiBold)
        state.raceRules?.let { rr ->
            if (rr.abilityIncreases.isNotEmpty()) {
                Text("Bonos: " + rr.abilityIncreases.entries.joinToString { "${ABILITY_NAMES_ES[it.key]} +${it.value}" }, style = MaterialTheme.typography.bodySmall)
            }
            Text("Rasgos: " + rr.traits.joinToString { it.name }, style = MaterialTheme.typography.bodySmall)
        }
        RulesPickButton(kinds = listOf(RulesEntryKind.SPECIES), label = "📖 Elegir raza", edition = state.edition, onPick = { state.race = it; state.subrace = null })
        if (state.race != null) {
            RulesPickButton(
                kinds = listOf(RulesEntryKind.SUBSPECIES),
                label = "📖 Subraza o semirraza (opcional)",
                edition = state.edition,
                parent = state.race?.name,
                onPick = { state.subrace = it },
            )
        }
    }

    Step("3. Clases") {
        if (state.classes.isEmpty()) Text("Elige la clase principal. Para multiclase, agrega otra después.", style = MaterialTheme.typography.bodySmall)
        state.classes.forEachIndexed { index, (option, level) ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(if (index == 0) "${option.name} (principal)" else option.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        TextButton(onClick = { state.classes = state.classes.mapIndexed { i, c -> if (i == index) c.first to (c.second - 1).coerceAtLeast(1) else c } }) { Text("−") }
                        Text("Nivel $level")
                        TextButton(onClick = { state.classes = state.classes.mapIndexed { i, c -> if (i == index) c.first to (c.second + 1).coerceAtMost(20) else c } }) { Text("＋") }
                        TextButton(onClick = {
                            state.classes = state.classes.filterIndexed { i, _ -> i != index }
                            state.subclasses = state.subclasses.filterKeys { it != index }.mapKeys { (k, _) -> if (k > index) k - 1 else k }
                        }) { Text("Quitar") }
                    }
                    classChoices.getOrNull(index)?.rules?.let { cr ->
                        Text(
                            "Dado de golpe d${cr.hitDie}" + if (index == 0) " · Salvaciones: ${cr.saves.joinToString { ABILITY_NAMES_ES[it].orEmpty() }}" else " · Multiclase",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text("Subclase: ${state.subclasses[index]?.name ?: "sin elegir"}", style = MaterialTheme.typography.bodySmall)
                    RulesPickButton(
                        kinds = listOf(RulesEntryKind.SUBCLASS),
                        label = "📖 Elegir subclase",
                        edition = option.edition.ifBlank { state.edition },
                        parent = option.name,
                        onPick = { state.subclasses = state.subclasses + (index to it) },
                    )
                }
            }
        }
        Text("Nivel total: ${state.classes.sumOf { it.second }}", style = MaterialTheme.typography.bodySmall)
        RulesPickButton(
            kinds = listOf(RulesEntryKind.CLASS),
            label = if (state.classes.isEmpty()) "📖 Elegir clase" else "＋ Añadir otra clase (multiclase)",
            edition = state.edition,
            onPick = { state.classes = state.classes + (it to 1) },
        )
    }

    Step("4. Trasfondo") {
        Text(state.background?.name ?: "Sin elegir", fontWeight = FontWeight.SemiBold)
        backgroundRules?.let { b ->
            Text(
                listOfNotNull(
                    b.skills.takeIf { it.isNotEmpty() }?.let { "Habilidades: ${it.joinToString()}" },
                    b.feat.takeIf { it.isNotBlank() }?.let { "Dote: $it" },
                    b.tools.takeIf { it.isNotBlank() }?.let { "Herramientas: $it" },
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        RulesPickButton(kinds = listOf(RulesEntryKind.BACKGROUND), label = "📖 Elegir trasfondo", edition = state.edition, onPick = { state.background = it })
    }

    Step("5. Características") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ScoreMethod.entries.forEach { m ->
                FilterChip(
                    selected = state.method == m,
                    onClick = {
                        state.method = m
                        state.base = if (m == ScoreMethod.POINT_BUY) ABILITY_CODES.associate { it.first to 8 } else state.base
                    },
                    label = { Text(m.label) },
                )
            }
        }
        if (state.method == ScoreMethod.STANDARD) {
            OutlinedButton(onClick = {
                // The class's main abilities get the highest values; the rest in the usual order.
                val main = classChoices.firstOrNull()?.rules?.primary.orEmpty()
                val order = (main + listOf("CON", "DEX", "STR", "WIS", "INT", "CHA")).distinct()
                state.base = order.zip(STANDARD_ARRAY).toMap()
            }) { Text("Repartir 15, 14, 13, 12, 10, 8 según la clase") }
        }
        val spent = state.base.values.sumOf { pointBuyCost(it) }
        if (state.method == ScoreMethod.POINT_BUY) {
            Text("Puntos usados: $spent de 27", color = if (spent > 27) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
        }
        ABILITY_CODES.forEach { (code, _) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(ABILITY_NAMES_ES[code].orEmpty(), modifier = Modifier.width(110.dp))
                TextButton(onClick = {
                    state.base = state.base + (code to ((state.base[code] ?: 10) - 1).coerceAtLeast(if (state.method == ScoreMethod.POINT_BUY) 8 else 3))
                }) { Text("−") }
                Text("${state.base[code]}", fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    state.base = state.base + (code to ((state.base[code] ?: 10) + 1).coerceAtMost(if (state.method == ScoreMethod.POINT_BUY) 15 else 20))
                }) { Text("＋") }
                val extra = scores.bonuses[code] ?: 0
                Text(
                    (if (extra != 0) "+$extra → " else "→ ") + "${scores.final[code]} (${scores.modifier(code).let { if (it >= 0) "+$it" else "$it" }})",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        if (backgroundRules != null && backgroundRules.abilities.isNotEmpty()) {
            Text("Bono del trasfondo (${backgroundRules.abilities.joinToString { ABILITY_NAMES_ES[it].orEmpty() }})", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = state.bonusPlan == "2+1", onClick = { state.bonusPlan = "2+1" }, label = { Text("+2 y +1") })
                FilterChip(selected = state.bonusPlan == "1+1+1", onClick = { state.bonusPlan = "1+1+1" }, label = { Text("+1 a las tres") })
            }
            if (state.bonusPlan == "2+1") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.Center) {
                    Text("+2:", modifier = Modifier.padding(top = 8.dp))
                    backgroundRules.abilities.forEach { a ->
                        FilterChip(selected = state.plusTwo == a, onClick = { state.plusTwo = a }, label = { Text(ABILITY_NAMES_ES[a].orEmpty()) })
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("+1:", modifier = Modifier.padding(top = 8.dp))
                    backgroundRules.abilities.filter { it != state.plusTwo }.forEach { a ->
                        FilterChip(selected = state.plusOne == a, onClick = { state.plusOne = a }, label = { Text(ABILITY_NAMES_ES[a].orEmpty()) })
                    }
                }
            }
        }
    }

    val main = classChoices.firstOrNull()?.rules
    if (main != null) {
        Step("6. Habilidades de ${main.name} (elige ${main.skillChoose})") {
            val fromBackground = backgroundRules?.skills.orEmpty()
            if (fromBackground.isNotEmpty()) Text("Del trasfondo: ${fromBackground.joinToString()}", style = MaterialTheme.typography.bodySmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                main.skillOptions.filter { it !in fromBackground }.forEach { skill ->
                    val chosen = skill in state.skills
                    FilterChip(
                        selected = chosen,
                        enabled = chosen || state.skills.size < main.skillChoose,
                        onClick = { state.skills = if (chosen) state.skills - skill else state.skills + skill },
                        label = { Text(skill) },
                    )
                }
            }
        }
    }

    if (main != null && main.equipment.isNotEmpty() || backgroundRules?.equipment?.isNotEmpty() == true) {
        Step("7. Equipo inicial") {
            main?.equipment?.takeIf { it.isNotEmpty() }?.let { options ->
                Text("De ${main.name}:", fontWeight = FontWeight.SemiBold)
                options.forEach { (key, text) ->
                    FilterChip(selected = state.classEquipment == key, onClick = { state.classEquipment = key }, label = { Text("$key: $text") })
                }
            }
            backgroundRules?.equipment?.takeIf { it.isNotEmpty() }?.let { options ->
                Text("Del trasfondo:", fontWeight = FontWeight.SemiBold)
                options.forEach { (key, text) ->
                    FilterChip(selected = state.backgroundEquipment == key, onClick = { state.backgroundEquipment = key }, label = { Text("$key: $text") })
                }
            }
        }
    }

    Step("Resumen") {
        val conMod = scores.modifier("CON")
        var hp = 0
        classChoices.forEachIndexed { i, c -> repeat(c.level) { s -> val d = c.rules?.hitDie ?: 8; hp += if (i == 0 && s == 0) d + conMod else d / 2 + 1 + conMod } }
        Text(
            listOfNotNull(
                state.name.ifBlank { null },
                listOfNotNull(state.race?.name, state.subrace?.name).joinToString(" · ").ifBlank { null },
                classChoices.joinToString(" / ") { "${it.name} ${it.level}" + (it.subclassName?.let { s -> " ($s)" } ?: "") }.ifBlank { null },
                state.background?.name,
                "PV $hp".takeIf { classChoices.isNotEmpty() },
            ).joinToString(" · "),
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Al crear, la hoja recibe: características con bonos, PV, salvaciones, habilidades, competencias, rasgos de raza y de clase hasta tu nivel, la dote del trasfondo y el equipo inicial.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun Step(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            content()
        }
    }
}
