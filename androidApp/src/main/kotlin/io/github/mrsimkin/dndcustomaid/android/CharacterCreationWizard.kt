package io.github.mrsimkin.dndcustomaid.android

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import io.github.mrsimkin.dndcustomaid.shared.dm.SRD_RULES_FILE
import io.github.mrsimkin.dndcustomaid.shared.dm.STANDARD_ARRAY
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdRules
import io.github.mrsimkin.dndcustomaid.shared.dm.buildScores
import io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdRules
import io.github.mrsimkin.dndcustomaid.shared.dm.pointBuyCost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

private enum class ScoreMethod(val label: String) { STANDARD("Arreglo estándar"), POINT_BUY("Compra de puntos (27)"), MANUAL("A mano o tiradas") }

private object AndroidSrdRules {
    private var rules: SrdRules? = null
    suspend fun load(context: android.content.Context): SrdRules = rules ?: withContext(Dispatchers.IO) {
        runCatching { parseSrdRules(context.assets.open("srd/$SRD_RULES_FILE").bufferedReader(Charsets.UTF_8).use { it.readText() }) }
            .getOrDefault(SrdRules()).also { rules = it }
    }
}

/**
 * QA 0.7.0-preqa.2 (J1): a new PC step by step: race, one or more classes (multiclass), background,
 * ability scores with their bonuses, skills and starting equipment. The SRD's rules fill the sheet
 * (hit points, saves, training, features, feat, gear); homebrew picks keep their text.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CharacterCreationWizard(
    campaigns: List<Campaign>,
    defaultCampaignId: Uuid,
    onDismiss: () -> Unit,
    onCreate: (Uuid, String, CharacterBuild?) -> Unit,
) {
    val context = LocalContext.current
    var rules by remember { mutableStateOf<SrdRules?>(null) }
    LaunchedEffect(Unit) { rules = AndroidSrdRules.load(context) }

    var name by rememberSaveable { mutableStateOf("") }
    var campaignIdText by rememberSaveable { mutableStateOf(defaultCampaignId.toString()) }
    val selectedCampaign = campaigns.firstOrNull { it.id.toString() == campaignIdText } ?: campaigns.first()
    var edition by rememberSaveable { mutableStateOf("5.5e") }
    var race by remember { mutableStateOf<RulesOption?>(null) }
    var subrace by remember { mutableStateOf<RulesOption?>(null) }
    var classes by remember { mutableStateOf(listOf<Pair<RulesOption, Int>>()) }
    var subclasses by remember { mutableStateOf(mapOf<Int, RulesOption>()) }
    var background by remember { mutableStateOf<RulesOption?>(null) }
    var method by remember { mutableStateOf(ScoreMethod.STANDARD) }
    var base by remember { mutableStateOf(ABILITY_CODES.associate { it.first to 10 }) }
    var bonusPlan by remember { mutableStateOf("2+1") }
    var plusTwo by remember { mutableStateOf<String?>(null) }
    var plusOne by remember { mutableStateOf<String?>(null) }
    var skills by remember { mutableStateOf(setOf<String>()) }
    var classEquipment by remember { mutableStateOf("A") }
    var backgroundEquipment by remember { mutableStateOf("A") }

    val r = rules
    fun srd(option: RulesOption?) = option?.takeIf { it.origin == RulesOrigin.SRD }
    val raceRules = srd(race)?.let { r?.race(it.edition, it.name) }
    val subraceRules = srd(subrace)?.let { r?.race(it.edition, it.name) }
    val backgroundRules = srd(background)?.let { r?.background(it.edition, it.name) }
    val classChoices = classes.mapIndexed { index, (option, level) ->
        val sub = subclasses[index]
        ClassChoice(
            name = option.name,
            level = level,
            edition = option.edition.ifBlank { edition },
            source = option.source,
            rules = srd(option)?.let { r?.characterClass(it.edition, it.name) },
            subclassName = sub?.name,
            subclassSource = sub?.source,
            subclassRules = srd(sub)?.let { r?.subclass(it.edition, it.name) },
            customText = listOfNotNull(option.takeIf { it.origin != RulesOrigin.SRD }?.body, sub?.takeIf { it.origin != RulesOrigin.SRD }?.body).joinToString("\n\n"),
            equipmentOption = classEquipment,
        )
    }
    val bonus = when {
        backgroundRules == null || backgroundRules.abilities.isEmpty() -> emptyMap()
        bonusPlan == "1+1+1" -> backgroundRules.abilities.associateWith { 1 }
        else -> buildMap { plusTwo?.let { put(it, 2) }; plusOne?.takeIf { it != plusTwo }?.let { put(it, 1) } }
    }
    val build = CharacterBuild(
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
        feat = backgroundRules?.feat?.takeIf { it.isNotBlank() }?.let { r?.feat(backgroundRules.edition, it) },
    )
    val scores = buildScores(build)

    CharacterImeSafeEditorDialog(
        title = "Nuevo personaje",
        supportingText = "Paso a paso desde el SRD o tu homebrew. Todo se puede cambiar después en la hoja.",
        onCancel = onDismiss,
        onSave = { onCreate(selectedCampaign.id, name.trim(), build.takeIf { classes.isNotEmpty() || race != null || background != null }) },
        saveLabel = "Crear personaje",
        saveEnabled = name.isNotBlank(),
        expanded = true,
    ) {
        Step("1. Nombre y campaña") {
            CharacterCompactOutlinedTextFieldV4(value = name, onValueChange = { name = it }, label = { Text("Nombre del personaje") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            CampaignSelectorV4(campaigns = campaigns, selected = selectedCampaign, onSelect = { campaignIdText = it.id.toString() })
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Reglas:", style = MaterialTheme.typography.bodySmall)
                listOf("5.5e" to "D&D 5.5e (2024)", "5e" to "D&D 5e (2014)").forEach { (key, label) ->
                    FilterChip(selected = edition == key, onClick = { edition = key }, label = { Text(label) })
                }
            }
        }

        Step("2. Raza") {
            Text(listOfNotNull(race?.name, subrace?.name).joinToString(" · ").ifBlank { "Sin elegir" }, fontWeight = FontWeight.SemiBold)
            raceRules?.let { rr ->
                if (rr.abilityIncreases.isNotEmpty()) Text("Bonos: " + rr.abilityIncreases.entries.joinToString { "${ABILITY_NAMES_ES[it.key]} +${it.value}" }, style = MaterialTheme.typography.bodySmall)
                Text("Rasgos: " + rr.traits.joinToString { it.name }, style = MaterialTheme.typography.bodySmall)
            }
            RulesPickButton(kinds = listOf(RulesEntryKind.SPECIES), label = "📖 Elegir raza", edition = edition, onPick = { race = it; subrace = null })
            if (race != null) RulesPickButton(kinds = listOf(RulesEntryKind.SUBSPECIES), label = "📖 Subraza o semirraza (opcional)", edition = edition, parent = race?.name, onPick = { subrace = it })
        }

        Step("3. Clases") {
            if (classes.isEmpty()) Text("Elige la clase principal. Para multiclase, agrega otra después.", style = MaterialTheme.typography.bodySmall)
            classes.forEachIndexed { index, (option, level) ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(if (index == 0) "${option.name} (principal)" else option.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            TextButton(onClick = { classes = classes.mapIndexed { i, c -> if (i == index) c.first to (c.second - 1).coerceAtLeast(1) else c } }) { Text("−") }
                            Text("Nivel $level")
                            TextButton(onClick = { classes = classes.mapIndexed { i, c -> if (i == index) c.first to (c.second + 1).coerceAtMost(20) else c } }) { Text("＋") }
                            TextButton(onClick = {
                                classes = classes.filterIndexed { i, _ -> i != index }
                                subclasses = subclasses.filterKeys { it != index }.mapKeys { (k, _) -> if (k > index) k - 1 else k }
                            }) { Text("Quitar") }
                        }
                        classChoices.getOrNull(index)?.rules?.let { cr ->
                            Text("Dado de golpe d${cr.hitDie}" + if (index == 0) " · Salvaciones: ${cr.saves.joinToString { ABILITY_NAMES_ES[it].orEmpty() }}" else " · Multiclase", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Subclase: ${subclasses[index]?.name ?: "sin elegir"}", style = MaterialTheme.typography.bodySmall)
                        RulesPickButton(kinds = listOf(RulesEntryKind.SUBCLASS), label = "📖 Elegir subclase", edition = option.edition.ifBlank { edition }, parent = option.name, onPick = { subclasses = subclasses + (index to it) })
                    }
                }
            }
            Text("Nivel total: ${classes.sumOf { it.second }}", style = MaterialTheme.typography.bodySmall)
            RulesPickButton(kinds = listOf(RulesEntryKind.CLASS), label = if (classes.isEmpty()) "📖 Elegir clase" else "＋ Añadir otra clase (multiclase)", edition = edition, onPick = { classes = classes + (it to 1) })
        }

        Step("4. Trasfondo") {
            Text(background?.name ?: "Sin elegir", fontWeight = FontWeight.SemiBold)
            backgroundRules?.let { b ->
                Text(listOfNotNull(
                    b.skills.takeIf { it.isNotEmpty() }?.let { "Habilidades: ${it.joinToString()}" },
                    b.feat.takeIf { it.isNotBlank() }?.let { "Dote: $it" },
                    b.tools.takeIf { it.isNotBlank() }?.let { "Herramientas: $it" },
                ).joinToString(" · "), style = MaterialTheme.typography.bodySmall)
            }
            RulesPickButton(kinds = listOf(RulesEntryKind.BACKGROUND), label = "📖 Elegir trasfondo", edition = edition, onPick = { background = it })
        }

        Step("5. Características") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ScoreMethod.entries.forEach { m ->
                    FilterChip(selected = method == m, onClick = {
                        method = m
                        base = if (m == ScoreMethod.POINT_BUY) ABILITY_CODES.associate { it.first to 8 } else base
                    }, label = { Text(m.label) })
                }
            }
            if (method == ScoreMethod.STANDARD) {
                OutlinedButton(onClick = {
                    // The class's main abilities get the highest values; the rest in the usual order.
                    val main = classChoices.firstOrNull()?.rules?.primary.orEmpty()
                    val order = (main + listOf("CON", "DEX", "STR", "WIS", "INT", "CHA")).distinct()
                    base = order.zip(STANDARD_ARRAY).toMap()
                }) { Text("Repartir 15, 14, 13, 12, 10, 8 según la clase") }
            }
            val spent = base.values.sumOf { pointBuyCost(it) }
            if (method == ScoreMethod.POINT_BUY) Text("Puntos usados: $spent de 27", color = if (spent > 27) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
            ABILITY_CODES.forEach { (code, _) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(ABILITY_NAMES_ES[code].orEmpty(), modifier = Modifier.width(110.dp))
                    TextButton(onClick = { base = base + (code to ((base[code] ?: 10) - 1).coerceAtLeast(if (method == ScoreMethod.POINT_BUY) 8 else 3)) }) { Text("−") }
                    Text("${base[code]}", fontWeight = FontWeight.Bold)
                    TextButton(onClick = { base = base + (code to ((base[code] ?: 10) + 1).coerceAtMost(if (method == ScoreMethod.POINT_BUY) 15 else 20)) }) { Text("＋") }
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
                    FilterChip(selected = bonusPlan == "2+1", onClick = { bonusPlan = "2+1" }, label = { Text("+2 y +1") })
                    FilterChip(selected = bonusPlan == "1+1+1", onClick = { bonusPlan = "1+1+1" }, label = { Text("+1 a las tres") })
                }
                if (bonusPlan == "2+1") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.Center) {
                        Text("+2:", modifier = Modifier.padding(top = 8.dp))
                        backgroundRules.abilities.forEach { a -> FilterChip(selected = plusTwo == a, onClick = { plusTwo = a }, label = { Text(ABILITY_NAMES_ES[a].orEmpty()) }) }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("+1:", modifier = Modifier.padding(top = 8.dp))
                        backgroundRules.abilities.filter { it != plusTwo }.forEach { a -> FilterChip(selected = plusOne == a, onClick = { plusOne = a }, label = { Text(ABILITY_NAMES_ES[a].orEmpty()) }) }
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
                        val chosen = skill in skills
                        FilterChip(
                            selected = chosen,
                            enabled = chosen || skills.size < main.skillChoose,
                            onClick = { skills = if (chosen) skills - skill else skills + skill },
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
                        FilterChip(selected = classEquipment == key, onClick = { classEquipment = key }, label = { Text("$key: $text") })
                    }
                }
                backgroundRules?.equipment?.takeIf { it.isNotEmpty() }?.let { options ->
                    Text("Del trasfondo:", fontWeight = FontWeight.SemiBold)
                    options.forEach { (key, text) ->
                        FilterChip(selected = backgroundEquipment == key, onClick = { backgroundEquipment = key }, label = { Text("$key: $text") })
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
                    name.ifBlank { null },
                    listOfNotNull(race?.name, subrace?.name).joinToString(" · ").ifBlank { null },
                    classChoices.joinToString(" / ") { "${it.name} ${it.level}" + (it.subclassName?.let { s -> " ($s)" } ?: "") }.ifBlank { null },
                    background?.name,
                    "PV $hp".takeIf { classChoices.isNotEmpty() },
                ).joinToString(" · "),
                fontWeight = FontWeight.SemiBold,
            )
            Text("Al crear, la hoja recibe: características con bonos, PV, salvaciones, habilidades, competencias, rasgos de raza y de clase hasta tu nivel, la dote del trasfondo y el equipo inicial.", style = MaterialTheme.typography.bodySmall)
        }
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
