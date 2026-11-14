package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSavingThrow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/**
 * The SRD's rules for creating a PC (QA 0.7.0-preqa.2, J1): classes, subclasses, races, backgrounds
 * and feats as facts, made from the official text by scripts/build_srd_rules.py.
 */
@Serializable
data class SrdNamedText(val name: String, val text: String = "")

@Serializable
data class SrdClassRules(
    val edition: String,
    val name: String,
    val hitDie: Int = 8,
    val primary: List<String> = emptyList(),
    val saves: List<String> = emptyList(),
    val skillChoose: Int = 2,
    val skillOptions: List<String> = emptyList(),
    val armor: String = "",
    val weapons: String = "",
    val tools: String = "",
    val equipment: Map<String, String> = emptyMap(),
    val multiclass: String = "",
    val levels: Map<String, List<String>> = emptyMap(),
    val features: Map<String, String> = emptyMap(),
)

@Serializable
data class SrdSubclassFeature(val level: Int, val name: String, val text: String = "")

@Serializable
data class SrdSubclassRules(val edition: String, val name: String, val className: String = "", val features: List<SrdSubclassFeature> = emptyList())

@Serializable
data class SrdRaceRules(
    val edition: String,
    val name: String,
    val parent: String = "",
    val abilityIncreases: Map<String, Int> = emptyMap(),
    val speedMeters: Double? = null,
    val size: String = "",
    val traits: List<SrdNamedText> = emptyList(),
)

@Serializable
data class SrdBackgroundRules(
    val edition: String,
    val name: String,
    val abilities: List<String> = emptyList(),
    val feat: String = "",
    val skills: List<String> = emptyList(),
    val tools: String = "",
    val languages: String = "",
    val equipment: Map<String, String> = emptyMap(),
    val features: List<SrdNamedText> = emptyList(),
)

@Serializable
data class SrdFeatRules(val edition: String, val name: String, val text: String = "")

@Serializable
data class SrdRules(
    val format: String = "dnd-custom-aid.srd-rules",
    val formatVersion: Int = 1,
    val classes: List<SrdClassRules> = emptyList(),
    val subclasses: List<SrdSubclassRules> = emptyList(),
    val species: List<SrdRaceRules> = emptyList(),
    val backgrounds: List<SrdBackgroundRules> = emptyList(),
    val feats: List<SrdFeatRules> = emptyList(),
) {
    fun characterClass(edition: String, name: String) = classes.firstOrNull { it.edition == edition && it.name.equals(name, ignoreCase = true) }
    fun subclass(edition: String, name: String) = subclasses.firstOrNull { it.edition == edition && it.name.equals(name, ignoreCase = true) }
    fun race(edition: String, name: String) = species.firstOrNull { it.edition == edition && it.name.equals(name, ignoreCase = true) }
    fun background(edition: String, name: String) = backgrounds.firstOrNull { it.edition == edition && it.name.equals(name, ignoreCase = true) }
    fun feat(edition: String, name: String) = feats.firstOrNull { it.edition == edition && spanishSortKey(it.name) == spanishSortKey(name.substringBefore(" (")) }
        ?: feats.firstOrNull { it.edition == edition && spanishSortKey(name).startsWith(spanishSortKey(it.name)) }
}

const val SRD_RULES_FILE = "srd_rules_es.json"

private val rulesJson = Json { ignoreUnknownKeys = true }

fun parseSrdRules(text: String): SrdRules = rulesJson.decodeFromString(SrdRules.serializer(), text)

val ABILITY_CODES: List<Pair<String, CharacterAbility>> = listOf(
    "STR" to CharacterAbility.STRENGTH, "DEX" to CharacterAbility.DEXTERITY, "CON" to CharacterAbility.CONSTITUTION,
    "INT" to CharacterAbility.INTELLIGENCE, "WIS" to CharacterAbility.WISDOM, "CHA" to CharacterAbility.CHARISMA,
)

val ABILITY_NAMES_ES: Map<String, String> = mapOf("STR" to "Fuerza", "DEX" to "Destreza", "CON" to "Constitución", "INT" to "Inteligencia", "WIS" to "Sabiduría", "CHA" to "Carisma")

val SKILL_NAMES_ES: Map<String, SkillKey> = mapOf(
    "Acrobacias" to SkillKey.ACROBATICS, "Trato con animales" to SkillKey.ANIMAL_HANDLING, "Arcanos" to SkillKey.ARCANA,
    "Atletismo" to SkillKey.ATHLETICS, "Engaño" to SkillKey.DECEPTION, "Historia" to SkillKey.HISTORY, "Perspicacia" to SkillKey.INSIGHT,
    "Intimidación" to SkillKey.INTIMIDATION, "Investigación" to SkillKey.INVESTIGATION, "Medicina" to SkillKey.MEDICINE,
    "Naturaleza" to SkillKey.NATURE, "Percepción" to SkillKey.PERCEPTION, "Interpretación" to SkillKey.PERFORMANCE,
    "Persuasión" to SkillKey.PERSUASION, "Religión" to SkillKey.RELIGION, "Juego de manos" to SkillKey.SLEIGHT_OF_HAND,
    "Sigilo" to SkillKey.STEALTH, "Supervivencia" to SkillKey.SURVIVAL,
)

/** The standard array of both editions. */
val STANDARD_ARRAY: List<Int> = listOf(15, 14, 13, 12, 10, 8)

/** Point-buy cost of a score (27 points, scores 8 to 15). */
fun pointBuyCost(score: Int): Int = when (score) {
    8 -> 0; 9 -> 1; 10 -> 2; 11 -> 3; 12 -> 4; 13 -> 5; 14 -> 7; 15 -> 9
    else -> 99
}

/** One class of the PC: the first one sets saving throws and full training; the others multiclass. */
data class ClassChoice(
    val name: String,
    val level: Int,
    val edition: String,
    val source: String,
    val rules: SrdClassRules? = null,
    val subclassName: String? = null,
    val subclassSource: String? = null,
    val subclassRules: SrdSubclassRules? = null,
    /** Homebrew class or subclass: its text, kept as a trait. */
    val customText: String = "",
    val equipmentOption: String? = "A",
)

data class CharacterBuild(
    val name: String,
    val edition: String = "5.5e",
    val raceName: String = "",
    val raceSource: String = "",
    val race: SrdRaceRules? = null,
    val subrace: SrdRaceRules? = null,
    val raceCustomText: String = "",
    val classes: List<ClassChoice> = emptyList(),
    val backgroundName: String = "",
    val backgroundSource: String = "",
    val background: SrdBackgroundRules? = null,
    val backgroundCustomText: String = "",
    val backgroundEquipment: String? = "A",
    /** Scores before bonuses, by STR…CHA code. */
    val baseScores: Map<String, Int> = ABILITY_CODES.associate { it.first to 10 },
    /** 5.5e background bonus (+2/+1 or +1/+1/+1 among its three abilities) or any extra bonus the DM gives. */
    val bonusChoice: Map<String, Int> = emptyMap(),
    val classSkills: List<String> = emptyList(),
    val feat: SrdFeatRules? = null,
)

data class BuiltScores(val base: Map<String, Int>, val bonuses: Map<String, Int>) {
    val final: Map<String, Int> get() = base.mapValues { (k, v) -> (v + (bonuses[k] ?: 0)).coerceAtMost(20) }
    fun modifier(code: String): Int = ((final[code] ?: 10) - 10).floorDiv(2)
}

/** Ability bonuses from the race (5e) and the chosen background or extra bonus. */
fun buildScores(build: CharacterBuild): BuiltScores {
    val bonuses = mutableMapOf<String, Int>()
    listOfNotNull(build.race, build.subrace).forEach { r -> r.abilityIncreases.forEach { (k, v) -> bonuses[k] = (bonuses[k] ?: 0) + v } }
    build.bonusChoice.forEach { (k, v) -> bonuses[k] = (bonuses[k] ?: 0) + v }
    return BuiltScores(build.baseScores, bonuses)
}

private val itemCount = Regex("""^(\d+)\s+(.+)$""")
private val coins = Regex("""^(\d+)\s*(ppt|po|pp|pe|pc)$""", RegexOption.IGNORE_CASE)

/** Spanish SRD coins to the sheet's keys: po oro, pp plata, pe electro, pc cobre, ppt platino. */
private val COIN_KEYS = mapOf("pc" to "cp", "pp" to "sp", "pe" to "ep", "po" to "gp", "ppt" to "pp")

/** «hacha a dos manos, 4 hachas de mano, paquete de explorador y 15 po» → items and coins. */
fun parseEquipment(text: String): Pair<List<Pair<String, Int>>, Map<String, Int>> {
    val items = mutableListOf<Pair<String, Int>>()
    val money = mutableMapOf<String, Int>()
    text.replace(";", ",").replace(" y ", ", ").split(",").map { it.trim().trimEnd('.') }.filter { it.isNotBlank() }.forEach { raw ->
        val piece = raw.removePrefix("Elige A o B:").removePrefix("(A)").removePrefix("(B)").trim()
        coins.matchEntire(piece)?.let {
            val key = COIN_KEYS.getValue(it.groupValues[2].lowercase())
            money[key] = (money[key] ?: 0) + it.groupValues[1].toInt()
            return@forEach
        }
        val counted = itemCount.matchEntire(piece)
        if (counted != null) items += counted.groupValues[2] to counted.groupValues[1].toInt() else items += piece.removePrefix("un ").removePrefix("una ") to 1
    }
    return items to money
}

/**
 * Turns the wizard's choices into the PC: classes with hit dice, scores with bonuses, hit points,
 * saving throws, skills, training, race and class features up to the chosen levels, the
 * background's feat and features, and the starting equipment. Everything stays editable.
 */
fun buildCharacter(sheet: CharacterSheet, build: CharacterBuild): CharacterSheet {
    val scores = buildScores(build)
    val conMod = scores.modifier("CON")
    fun family(edition: String, custom: Boolean) = when {
        custom -> CharacterRulesFamily.CUSTOM
        edition == "5e" -> CharacterRulesFamily.DND_5E
        else -> CharacterRulesFamily.DND_5_5E
    }
    val classes = build.classes.mapIndexed { index, c ->
        CharacterClassLevel(
            id = Uuid.random(),
            name = c.name,
            level = c.level,
            hitDieSides = c.rules?.hitDie ?: 8,
            hitDiceRemaining = c.level,
            sortOrder = index,
            rulesFamily = family(c.edition, c.rules == null),
            source = c.source,
            subclassName = c.subclassName,
            subclassSource = c.subclassSource,
            subclassRulesFamily = family(c.edition, c.subclassRules == null && c.subclassName != null),
        )
    }
    // Hit points: the first class's die at level 1, then the fixed average per level, plus Constitution.
    var hp = 0
    build.classes.forEachIndexed { index, c ->
        val die = c.rules?.hitDie ?: 8
        repeat(c.level) { step -> hp += if (index == 0 && step == 0) die + conMod else (die / 2 + 1) + conMod }
    }
    val first = build.classes.firstOrNull()
    val saves = first?.rules?.saves.orEmpty().mapNotNull { code -> ABILITY_CODES.firstOrNull { it.first == code }?.second }.toSet()
    val skillNames = (build.classSkills + build.background?.skills.orEmpty()).toSet()
    val skills = SkillKey.entries.map { key ->
        val trained = SKILL_NAMES_ES.any { (name, k) -> k == key && name in skillNames }
        sheet.skills.firstOrNull { it.key == key }?.copy(training = if (trained) SkillTraining.PROFICIENT else SkillTraining.NONE)
            ?: CharacterSkill(key, 0, if (trained) SkillTraining.PROFICIENT else SkillTraining.NONE)
    }

    var order = 0
    val proficiencies = mutableListOf<CharacterProficiency>()
    fun proficiency(type: CharacterProficiencyType, text: String, source: String) {
        text.trim().trimEnd('.').takeIf { it.isNotBlank() && !it.equals("ninguna", ignoreCase = true) }?.let {
            proficiencies += CharacterProficiency(Uuid.random(), type, it, source, sortOrder = order++)
        }
    }
    build.classes.forEachIndexed { index, c ->
        val r = c.rules ?: return@forEachIndexed
        if (index == 0) {
            proficiency(CharacterProficiencyType.ARMOR, r.armor, c.name)
            proficiency(CharacterProficiencyType.WEAPON, r.weapons, c.name)
            proficiency(CharacterProficiencyType.TOOL, r.tools, c.name)
        } else if (r.multiclass.isNotBlank()) {
            proficiency(CharacterProficiencyType.OTHER, "Multiclase (${c.name}): ${r.multiclass.replace("\n", " ")}", c.name)
        }
    }
    build.background?.let { b ->
        proficiency(CharacterProficiencyType.TOOL, b.tools, b.name)
        proficiency(CharacterProficiencyType.LANGUAGE, b.languages, b.name)
    }

    val traits = mutableListOf<CharacterTrait>()
    fun trait(name: String, source: String, type: CharacterTraitType, text: String) {
        traits += CharacterTrait(Uuid.random(), name, source, type, text.trim(), null, null, 0, null, CharacterActivationType.PASSIVE, traits.size)
    }
    listOfNotNull(build.race, build.subrace).forEach { r -> r.traits.forEach { t -> trait(t.name, "${r.name} (${sourceLabel(r.edition)})", CharacterTraitType.SPECIES_RACE, t.text) } }
    if (build.race == null && build.raceCustomText.isNotBlank()) trait(build.raceName, build.raceSource, CharacterTraitType.SPECIES_RACE, build.raceCustomText)
    build.classes.forEach { c ->
        val r = c.rules
        if (r == null) {
            if (c.customText.isNotBlank()) trait(c.name, c.source, CharacterTraitType.CLASS, c.customText)
            return@forEach
        }
        (1..c.level).forEach { level ->
            r.levels[level.toString()].orEmpty()
                .filterNot { it.startsWith("Mejora de caracter", ignoreCase = true) || it.startsWith("Rasgo de", ignoreCase = true) || it.startsWith("Subclase de", ignoreCase = true) }
                .forEach { feature ->
                    val text = r.features.entries.firstOrNull { spanishSortKey(it.key) == spanishSortKey(feature) }?.value
                        ?: r.features.entries.firstOrNull { spanishSortKey(feature).startsWith(spanishSortKey(it.key)) }?.value.orEmpty()
                    trait(feature, "${c.name} $level (${sourceLabel(c.edition)})", CharacterTraitType.CLASS, text)
                }
        }
        c.subclassRules?.features?.filter { it.level <= c.level }?.forEach { f ->
            trait(f.name, "${c.subclassName} ${f.level} (${sourceLabel(c.edition)})", CharacterTraitType.CLASS, f.text)
        }
        if (c.subclassRules == null && c.subclassName != null && c.customText.isNotBlank()) trait(c.subclassName, c.subclassSource ?: c.source, CharacterTraitType.CLASS, c.customText)
    }
    build.background?.features?.forEach { trait(it.name, build.background.name, CharacterTraitType.BACKGROUND, it.text) }
    if (build.background == null && build.backgroundCustomText.isNotBlank()) trait(build.backgroundName, build.backgroundSource, CharacterTraitType.BACKGROUND, build.backgroundCustomText)
    build.feat?.let { trait(it.name, "Dote de origen · ${build.backgroundName}", CharacterTraitType.FEAT, it.text) }

    val items = mutableListOf<CharacterInventoryItem>()
    val money = mutableMapOf<String, Int>()
    fun equip(text: String?, source: String) {
        if (text.isNullOrBlank()) return
        val (parsed, coinsFound) = parseEquipment(text)
        parsed.forEach { (name, qty) -> items += CharacterInventoryItem(Uuid.random(), name, qty, null, false, "Equipo inicial: $source", items.size, false, null, null, false) }
        coinsFound.forEach { (k, v) -> money[k] = (money[k] ?: 0) + v }
    }
    first?.let { c -> equip(c.rules?.equipment?.get(c.equipmentOption ?: "A"), c.name) }
    build.background?.let { b -> equip(b.equipment[build.backgroundEquipment ?: "A"], b.name) }
    val currencies = sheet.currencies.map { cur -> cur.copy(amount = cur.amount + (money[cur.key] ?: 0)) }
        .ifEmpty { money.entries.mapIndexed { i, (k, v) -> CharacterCurrency(k, k.uppercase(), v, i, true) } }

    val speed = (build.subrace?.speedMeters ?: build.race?.speedMeters)?.let { (it / 0.3).toInt() } ?: sheet.speed
    val f = scores.final
    return sheet.copy(
        name = build.name.ifBlank { sheet.name },
        strength = f["STR"] ?: 10, dexterity = f["DEX"] ?: 10, constitution = f["CON"] ?: 10,
        intelligence = f["INT"] ?: 10, wisdom = f["WIS"] ?: 10, charisma = f["CHA"] ?: 10,
        maxHp = hp.coerceAtLeast(1), currentHp = hp.coerceAtLeast(1),
        speed = speed,
        classes = classes,
        savingThrows = CharacterAbility.entries.map { a ->
            sheet.savingThrows.firstOrNull { it.ability == a }?.copy(proficient = a in saves) ?: CharacterSavingThrow(a, a in saves, 0)
        },
        skills = skills,
        proficiencies = sheet.proficiencies + proficiencies,
        traits = sheet.traits + traits,
        inventoryItems = sheet.inventoryItems + items,
        currencies = currencies,
        background = sheet.background.copy(
            name = build.backgroundName,
            race = listOf(build.raceName, build.subrace?.name).filter { !it.isNullOrBlank() }.joinToString(" · "),
        ),
    )
}

private fun sourceLabel(edition: String) = if (edition == "5e") "SRD 5.1" else "SRD 5.2.1"
