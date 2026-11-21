package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSavingThrow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.abilityModifierForScore
import io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject
// Refactor F3: the PC editor's working copy (draft) and its parts, moved out of CharacterEditorV4.kt.

internal data class ClassLevelDraftV4(
    val id: Uuid,
    val name: String,
    val level: String,
    val hitDieSides: String,
    val hitDiceRemaining: String,
    val rulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
    val source: String? = null,
    val catalogKey: String? = null,
    val subclassName: String? = null,
    val subclassSource: String? = null,
    val subclassCatalogKey: String? = null,
    val subclassRulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
    val epicLevelsEnabled: Boolean = false,
) {
    fun withManualName(value: String): ClassLevelDraftV4 = if (value == name) {
        copy(name = value)
    } else {
        copy(
            name = value,
            rulesFamily = CharacterRulesFamily.UNSPECIFIED,
            source = null,
            catalogKey = null,
            subclassName = null,
            subclassSource = null,
            subclassCatalogKey = null,
            subclassRulesFamily = CharacterRulesFamily.UNSPECIFIED,
        )
    }
}

internal data class SaveDraftV4(
    val ability: CharacterAbility,
    val proficient: Boolean,
    val adjustment: String,
)

internal data class SkillDraftV4(
    val key: SkillKey,
    val adjustment: String,
    val training: SkillTraining,
)

internal data class SpellSlotDraftV4(
    val level: Int,
    val total: String,
    val spent: Int,
)

internal data class CharacterEditorDraftV4(
    val name: String,
    val status: CharacterStatus,
    val strength: String,
    val dexterity: String,
    val constitution: String,
    val intelligence: String,
    val wisdom: String,
    val charisma: String,
    val armorClass: String,
    val maxHp: String,
    val currentHp: String,
    val tempHp: String,
    val initiativeAdjustment: String,
    val speed: String,
    val proficiencyBonusAdjustment: String,
    val passivePerceptionAdjustment: String,
    val spellSaveDc: String,
    val spellAttackModifier: String,
    val spellcastingAbility: SpellcastingAbility,
    val spellSlots: List<SpellSlotDraftV4>,
    val classes: List<ClassLevelDraftV4>,
    val saves: List<SaveDraftV4>,
    val skills: List<SkillDraftV4>,
) {
    fun abilityValue(ability: CharacterAbility): String = when (ability) {
        CharacterAbility.STRENGTH -> strength
        CharacterAbility.DEXTERITY -> dexterity
        CharacterAbility.CONSTITUTION -> constitution
        CharacterAbility.INTELLIGENCE -> intelligence
        CharacterAbility.WISDOM -> wisdom
        CharacterAbility.CHARISMA -> charisma
    }

    fun withAbilityValue(ability: CharacterAbility, value: String): CharacterEditorDraftV4 = when (ability) {
        CharacterAbility.STRENGTH -> copy(strength = value)
        CharacterAbility.DEXTERITY -> copy(dexterity = value)
        CharacterAbility.CONSTITUTION -> copy(constitution = value)
        CharacterAbility.INTELLIGENCE -> copy(intelligence = value)
        CharacterAbility.WISDOM -> copy(wisdom = value)
        CharacterAbility.CHARISMA -> copy(charisma = value)
    }

    fun abilityModifier(ability: CharacterAbility): Int? = abilityValue(ability).toIntOrNull()?.let(::abilityModifierForScore)

    fun totalLevel(): Int = classes.sumOf { it.level.toIntOrNull() ?: 0 }

    fun standardProficiencyBonus(): Int = standardProficiencyBonusForLevel(totalLevel())

    fun finalProficiencyBonus(): Int? {
        val adjustment = parseOptionalAdjustmentV4(proficiencyBonusAdjustment) ?: return null
        return standardProficiencyBonus() + adjustment
    }

    fun saveFor(ability: CharacterAbility): SaveDraftV4 =
        saves.firstOrNull { it.ability == ability } ?: SaveDraftV4(ability, false, "0")

    fun withSave(changed: SaveDraftV4): CharacterEditorDraftV4 = copy(
        saves = CharacterAbility.entries.map { ability ->
            if (ability == changed.ability) changed else saveFor(ability)
        },
    )

    fun withSkill(changed: SkillDraftV4): CharacterEditorDraftV4 = copy(
        skills = SkillKey.entries.map { key ->
            if (key == changed.key) changed else skills.firstOrNull { it.key == key }
                ?: SkillDraftV4(key, "0", SkillTraining.NONE)
        },
    )

    fun spellSlotFor(level: Int): SpellSlotDraftV4 =
        spellSlots.firstOrNull { it.level == level } ?: SpellSlotDraftV4(level, "0", 0)

    fun withSpellSlot(changed: SpellSlotDraftV4): CharacterEditorDraftV4 = copy(
        spellSlots = (1..9).map { level ->
            if (level == changed.level) changed else spellSlotFor(level)
        },
    )

    fun savingThrowTotal(ability: CharacterAbility): Int? {
        val modifier = abilityModifier(ability) ?: return null
        val proficiency = finalProficiencyBonus() ?: return null
        val save = saveFor(ability)
        val adjustment = parseOptionalAdjustmentV4(save.adjustment) ?: return null
        return modifier + (if (save.proficient) proficiency else 0) + adjustment
    }

    fun skillTotal(key: SkillKey): Int? {
        val modifier = abilityModifier(key.ability) ?: return null
        val proficiency = finalProficiencyBonus() ?: return null
        val skill = skills.firstOrNull { it.key == key } ?: return null
        val adjustment = parseOptionalAdjustmentV4(skill.adjustment) ?: return null
        val contribution = when (skill.training) {
            SkillTraining.NONE -> 0
            SkillTraining.PROFICIENT -> proficiency
            SkillTraining.EXPERTISE -> proficiency * 2
        }
        return modifier + contribution + adjustment
    }

    fun initiativeTotal(): Int? {
        val dexterityModifier = abilityModifier(CharacterAbility.DEXTERITY) ?: return null
        val adjustment = parseOptionalAdjustmentV4(initiativeAdjustment) ?: return null
        return dexterityModifier + adjustment
    }

    fun passivePerceptionTotal(): Int? {
        val perception = skillTotal(SkillKey.PERCEPTION) ?: return null
        val adjustment = parseOptionalAdjustmentV4(passivePerceptionAdjustment) ?: return null
        return 10 + perception + adjustment
    }

    fun missingRequiredNumberLabels(): List<String> = buildList {
        listOf(
            "FUE" to strength,
            "DES" to dexterity,
            "CON" to constitution,
            "INT" to intelligence,
            "SAB" to wisdom,
            "CAR" to charisma,
            "CA" to armorClass,
            "PG máximos" to maxHp,
            "PG actuales" to currentHp,
            "PG temporales" to tempHp,
            "Velocidad" to speed,
        ).forEach { (label, value) ->
            if (isMissingNumericTokenV4(value)) add(label)
        }
        classes.forEachIndexed { index, classDraft ->
            if (isMissingNumericTokenV4(classDraft.level)) add("Nv. clase ${index + 1}")
            if (isMissingNumericTokenV4(classDraft.hitDiceRemaining)) add("DG clase ${index + 1}")
            if (isMissingNumericTokenV4(classDraft.hitDieSides)) add("Tipo DG clase ${index + 1}")
        }
    }

    fun toSheetOrNull(
        original: CharacterSheet,
        blankRequiredAsZero: Boolean = false,
    ): CharacterSheet? {
        fun parsedRequired(value: String): Int? {
            if (isMissingNumericTokenV4(value)) return if (blankRequiredAsZero) 0 else null
            return value.trim().toIntOrNull()
        }
        fun parsedAdjustment(value: String): Int? = parseOptionalAdjustmentV4(value)
        fun parsedOptionalInt(value: String): Int? {
            if (isMissingNumericTokenV4(value)) return null
            return value.trim().toIntOrNull()
        }

        val normalizedName = name.trim().takeIf { it.isNotEmpty() } ?: return null
        val parsedClasses = classes.mapIndexed { index, classDraft ->
            CharacterClassLevel(
                id = classDraft.id,
                name = classDraft.name.trim().takeIf { it.isNotEmpty() } ?: return null,
                level = parsedRequired(classDraft.level)?.takeIf { it >= 0 } ?: return null,
                hitDieSides = parsedRequired(classDraft.hitDieSides)?.takeIf { it >= 0 } ?: return null,
                hitDiceRemaining = parsedRequired(classDraft.hitDiceRemaining)?.takeIf { it >= 0 } ?: return null,
                sortOrder = index,
                rulesFamily = classDraft.rulesFamily,
                source = classDraft.source,
                catalogKey = classDraft.catalogKey,
                subclassName = classDraft.subclassName?.trim()?.takeIf { it.isNotEmpty() },
                subclassSource = classDraft.subclassSource,
                subclassCatalogKey = classDraft.subclassCatalogKey,
                subclassRulesFamily = classDraft.subclassRulesFamily,
                epicLevelsEnabled = classDraft.epicLevelsEnabled,
            )
        }
        val parsedSaves = CharacterAbility.entries.map { ability ->
            val save = saveFor(ability)
            CharacterSavingThrow(
                ability = ability,
                proficient = save.proficient,
                adjustment = parsedAdjustment(save.adjustment) ?: return null,
            )
        }
        val parsedSkills = SkillKey.entries.map { key ->
            val skill = skills.firstOrNull { it.key == key } ?: return null
            CharacterSkill(
                key = key,
                adjustment = parsedAdjustment(skill.adjustment) ?: return null,
                training = skill.training,
            )
        }
        val parsedSpellSlots = (1..9).mapNotNull { level ->
            val slot = spellSlotFor(level)
            val total = if (isMissingNumericTokenV4(slot.total)) 0 else slot.total.toIntOrNull() ?: return null
            if (total <= 0) {
                null
            } else {
                CharacterSpellSlot(
                    level = level,
                    totalSlots = total,
                    spentSlots = slot.spent.coerceIn(0, total),
                )
            }
        }
        val spellDc = parsedOptionalInt(spellSaveDc)
        if (spellSaveDc.isNotBlank() && spellDc == null) return null
        val spellAttack = parsedOptionalInt(spellAttackModifier)
        if (spellAttackModifier.isNotBlank() && spellAttack == null) return null
        val proficiencyAdjustment = parsedAdjustment(proficiencyBonusAdjustment) ?: return null
        val finalProficiency = standardProficiencyBonusForLevel(parsedClasses.sumOf { it.level }) + proficiencyAdjustment

        return original.copy(
            name = normalizedName,
            status = status,
            strength = parsedRequired(strength) ?: return null,
            dexterity = parsedRequired(dexterity) ?: return null,
            constitution = parsedRequired(constitution) ?: return null,
            intelligence = parsedRequired(intelligence) ?: return null,
            wisdom = parsedRequired(wisdom) ?: return null,
            charisma = parsedRequired(charisma) ?: return null,
            armorClass = parsedRequired(armorClass) ?: return null,
            maxHp = parsedRequired(maxHp) ?: return null,
            currentHp = parsedRequired(currentHp) ?: return null,
            tempHp = parsedRequired(tempHp) ?: return null,
            initiativeAdjustment = parsedAdjustment(initiativeAdjustment) ?: return null,
            speed = parsedRequired(speed) ?: return null,
            proficiencyBonus = finalProficiency,
            proficiencyBonusAdjustment = proficiencyAdjustment,
            savingThrows = parsedSaves,
            passivePerceptionAdjustment = parsedAdjustment(passivePerceptionAdjustment) ?: return null,
            spellSaveDc = spellDc,
            spellAttackModifier = spellAttack,
            spellcastingAbility = spellcastingAbility,
            spellSlots = parsedSpellSlots,
            classes = parsedClasses,
            skills = parsedSkills,
        )
    }

    fun toJson(): String = JSONObject().apply {
        put("name", name)
        put("status", status.name)
        put("strength", strength)
        put("dexterity", dexterity)
        put("constitution", constitution)
        put("intelligence", intelligence)
        put("wisdom", wisdom)
        put("charisma", charisma)
        put("armorClass", armorClass)
        put("maxHp", maxHp)
        put("currentHp", currentHp)
        put("tempHp", tempHp)
        put("initiativeAdjustment", initiativeAdjustment)
        put("speed", speed)
        put("proficiencyBonusAdjustment", proficiencyBonusAdjustment)
        put("passivePerceptionAdjustment", passivePerceptionAdjustment)
        put("spellSaveDc", spellSaveDc)
        put("spellAttackModifier", spellAttackModifier)
        put("spellcastingAbility", spellcastingAbility.name)
        put("spellSlots", JSONArray().apply {
            spellSlots.forEach { item ->
                put(JSONObject().apply {
                    put("level", item.level)
                    put("total", item.total)
                    put("spent", item.spent)
                })
            }
        })
        put("classes", JSONArray().apply {
            classes.forEach { item ->
                put(JSONObject().apply {
                    put("id", item.id.toString())
                    put("name", item.name)
                    put("level", item.level)
                    put("die", item.hitDieSides)
                    put("remaining", item.hitDiceRemaining)
                    put("rulesFamily", item.rulesFamily.name)
                    put("source", item.source ?: JSONObject.NULL)
                    put("catalogKey", item.catalogKey ?: JSONObject.NULL)
                    put("subclassName", item.subclassName ?: JSONObject.NULL)
                    put("subclassSource", item.subclassSource ?: JSONObject.NULL)
                    put("subclassCatalogKey", item.subclassCatalogKey ?: JSONObject.NULL)
                    put("subclassRulesFamily", item.subclassRulesFamily.name)
                    put("epicLevelsEnabled", item.epicLevelsEnabled)
                })
            }
        })
        put("saves", JSONArray().apply {
            saves.forEach { item ->
                put(JSONObject().apply {
                    put("ability", item.ability.name)
                    put("proficient", item.proficient)
                    put("adjustment", item.adjustment)
                })
            }
        })
        put("skills", JSONArray().apply {
            skills.forEach { item ->
                put(JSONObject().apply {
                    put("key", item.key.name)
                    put("training", item.training.name)
                    put("adjustment", item.adjustment)
                })
            }
        })
    }.toString()

    companion object {
        val Saver: Saver<CharacterEditorDraftV4, String> = Saver(
            save = { it.toJson() },
            restore = { fromJson(it) },
        )

        fun from(sheet: CharacterSheet) = CharacterEditorDraftV4(
            name = sheet.name,
            status = sheet.status,
            strength = sheet.strength.toString(),
            dexterity = sheet.dexterity.toString(),
            constitution = sheet.constitution.toString(),
            intelligence = sheet.intelligence.toString(),
            wisdom = sheet.wisdom.toString(),
            charisma = sheet.charisma.toString(),
            armorClass = sheet.armorClass.toString(),
            maxHp = sheet.maxHp.toString(),
            currentHp = sheet.currentHp.toString(),
            tempHp = sheet.tempHp.toString(),
            initiativeAdjustment = sheet.initiativeAdjustment.toString(),
            speed = sheet.speed.toString(),
            proficiencyBonusAdjustment = sheet.proficiencyBonusAdjustment.toString(),
            passivePerceptionAdjustment = sheet.passivePerceptionAdjustment.toString(),
            spellSaveDc = sheet.spellSaveDc?.toString().orEmpty(),
            spellAttackModifier = sheet.spellAttackModifier?.toString().orEmpty(),
            spellcastingAbility = sheet.spellcastingAbility,
            spellSlots = (1..9).map { level ->
                val stored = sheet.spellSlots.firstOrNull { it.level == level }
                SpellSlotDraftV4(
                    level = level,
                    total = stored?.totalSlots?.toString() ?: "0",
                    spent = stored?.spentSlots ?: 0,
                )
            },
            classes = sheet.classes.map { classLevel ->
                ClassLevelDraftV4(
                    id = classLevel.id,
                    name = classLevel.name,
                    level = classLevel.level.toString(),
                    hitDieSides = classLevel.hitDieSides.toString(),
                    hitDiceRemaining = classLevel.hitDiceRemaining.toString(),
                    rulesFamily = classLevel.rulesFamily,
                    source = classLevel.source,
                    catalogKey = classLevel.catalogKey,
                    subclassName = classLevel.subclassName,
                    subclassSource = classLevel.subclassSource,
                    subclassCatalogKey = classLevel.subclassCatalogKey,
                    subclassRulesFamily = classLevel.subclassRulesFamily,
                    epicLevelsEnabled = classLevel.epicLevelsEnabled,
                )
            },
            saves = CharacterAbility.entries.map { ability ->
                val save = sheet.savingThrow(ability)
                SaveDraftV4(ability, save.proficient, save.adjustment.toString())
            },
            skills = SkillKey.entries.map { key ->
                val skill = sheet.skill(key)
                SkillDraftV4(key, skill.adjustment.toString(), skill.training)
            },
        )

        private fun fromJson(raw: String): CharacterEditorDraftV4? = runCatching {
            val json = JSONObject(raw)
            val classesJson = json.getJSONArray("classes")
            val classes = buildList {
                for (index in 0 until classesJson.length()) {
                    val item = classesJson.getJSONObject(index)
                    fun optionalString(key: String): String? =
                        if (item.has(key) && !item.isNull(key)) item.getString(key) else null
                    val rulesFamily = runCatching {
                        CharacterRulesFamily.valueOf(
                            item.optString("rulesFamily", CharacterRulesFamily.UNSPECIFIED.name),
                        )
                    }.getOrDefault(CharacterRulesFamily.UNSPECIFIED)
                    val subclassRulesFamily = runCatching {
                        CharacterRulesFamily.valueOf(
                            item.optString("subclassRulesFamily", rulesFamily.name),
                        )
                    }.getOrDefault(rulesFamily)
                    add(
                        ClassLevelDraftV4(
                            id = Uuid.parse(item.getString("id")),
                            name = item.getString("name"),
                            level = item.getString("level"),
                            hitDieSides = item.getString("die"),
                            hitDiceRemaining = item.getString("remaining"),
                            rulesFamily = rulesFamily,
                            source = optionalString("source"),
                            catalogKey = optionalString("catalogKey"),
                            subclassName = optionalString("subclassName"),
                            subclassSource = optionalString("subclassSource"),
                            subclassCatalogKey = optionalString("subclassCatalogKey"),
                            subclassRulesFamily = subclassRulesFamily,
                            epicLevelsEnabled = item.optBoolean("epicLevelsEnabled", false),
                        ),
                    )
                }
            }
            val savesJson = json.getJSONArray("saves")
            val saves = buildList {
                for (index in 0 until savesJson.length()) {
                    val item = savesJson.getJSONObject(index)
                    add(
                        SaveDraftV4(
                            ability = CharacterAbility.valueOf(item.getString("ability")),
                            proficient = item.getBoolean("proficient"),
                            adjustment = item.getString("adjustment"),
                        ),
                    )
                }
            }
            val skillsJson = json.getJSONArray("skills")
            val skills = buildList {
                for (index in 0 until skillsJson.length()) {
                    val item = skillsJson.getJSONObject(index)
                    add(
                        SkillDraftV4(
                            key = SkillKey.valueOf(item.getString("key")),
                            adjustment = item.getString("adjustment"),
                            training = SkillTraining.valueOf(item.getString("training")),
                        ),
                    )
                }
            }
            val proficiencyAdjustment = if (json.has("proficiencyBonusAdjustment")) {
                json.getString("proficiencyBonusAdjustment")
            } else {
                val legacyFinal = json.optString("proficiencyBonus", "2").toIntOrNull() ?: 2
                val totalLevel = classes.sumOf { it.level.toIntOrNull() ?: 0 }
                (legacyFinal - standardProficiencyBonusForLevel(totalLevel)).toString()
            }
            val slots = if (json.has("spellSlots")) {
                val array = json.getJSONArray("spellSlots")
                val restored = mutableMapOf<Int, SpellSlotDraftV4>()
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    val level = item.getInt("level")
                    restored[level] = SpellSlotDraftV4(
                        level = level,
                        total = item.getString("total"),
                        spent = item.getInt("spent"),
                    )
                }
                (1..9).map { level -> restored[level] ?: SpellSlotDraftV4(level, "0", 0) }
            } else {
                (1..9).map { level -> SpellSlotDraftV4(level, "0", 0) }
            }
            CharacterEditorDraftV4(
                name = json.getString("name"),
                status = CharacterStatus.valueOf(json.getString("status")),
                strength = json.getString("strength"),
                dexterity = json.getString("dexterity"),
                constitution = json.getString("constitution"),
                intelligence = json.getString("intelligence"),
                wisdom = json.getString("wisdom"),
                charisma = json.getString("charisma"),
                armorClass = json.getString("armorClass"),
                maxHp = json.getString("maxHp"),
                currentHp = json.getString("currentHp"),
                tempHp = json.getString("tempHp"),
                initiativeAdjustment = json.getString("initiativeAdjustment"),
                speed = json.getString("speed"),
                proficiencyBonusAdjustment = proficiencyAdjustment,
                passivePerceptionAdjustment = json.getString("passivePerceptionAdjustment"),
                spellSaveDc = json.getString("spellSaveDc"),
                spellAttackModifier = json.optString("spellAttackModifier", ""),
                spellcastingAbility = runCatching {
                    SpellcastingAbility.valueOf(json.optString("spellcastingAbility", SpellcastingAbility.NONE.name))
                }.getOrDefault(SpellcastingAbility.NONE),
                spellSlots = slots,
                classes = classes,
                saves = saves,
                skills = skills,
            )
        }.getOrNull()
    }
}
