package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/**
 * Additional genuine character records available to V11 continuation modules.
 * The full source aggregate remains present in PcSheetV11SemanticData.
 */
data class PcSheetV11ExtensionData(
    val classes: List<CharacterClassLevel>,
    val proficiencies: List<CharacterProficiency>,
    val weaponMasteries: List<CharacterWeaponMastery>,
    val resources: List<CharacterResource>,
    val classOptions: List<CharacterClassOption>,
    val forms: List<CharacterForm>,
    val companions: List<CharacterCompanion>,
    val customMarkers: List<CharacterCustomMarker>,
)

fun PcSheetV11SemanticData.extensionData(): PcSheetV11ExtensionData {
    val sheet = source.sheet
    fun <T> ordered(rows: List<T>, sortOrder: (T) -> Int, id: (T) -> Uuid): List<T> =
        rows.sortedWith(compareBy<T> { sortOrder(it) }.thenBy { id(it).toString() })

    return PcSheetV11ExtensionData(
        classes = ordered(sheet.classes, { it.sortOrder }, { it.id }),
        proficiencies = ordered(sheet.proficiencies, { it.sortOrder }, { it.id }),
        weaponMasteries = ordered(sheet.weaponMasteries, { it.sortOrder }, { it.id }),
        resources = ordered(sheet.resources, { it.sortOrder }, { it.id }),
        classOptions = ordered(sheet.classOptions, { it.sortOrder }, { it.id }),
        forms = ordered(sheet.forms, { it.sortOrder }, { it.id }),
        companions = ordered(sheet.companions, { it.sortOrder }, { it.id }),
        customMarkers = ordered(source.successor.customMarkers, { it.sortOrder }, { it.id }),
    )
}

/**
 * UUID-bearing input provenance, not a claim that the physical PDF drew the record.
 * The G2 compositor must account for which items it renders, where, and how often.
 * Built-in attributes/skills, currencies (string key) and general notes (no UUID)
 * are represented directly in the typed semantic fields instead of inventing IDs.
 */
enum class PcSheetV11InputKind {
    CLASS,
    CUSTOM_ATTRIBUTE,
    CUSTOM_SKILL,
    COMBAT_ENTRY,
    TRAIT,
    ORDINARY_EQUIPMENT,
    SPECIAL_EQUIPMENT,
    SPELL,
    SPELLCASTING_SOURCE,
    DIGITAL_NOTE,
    PROFICIENCY,
    WEAPON_MASTERY,
    RESOURCE,
    CLASS_OPTION,
    FORM,
    COMPANION,
    CUSTOM_MARKER,
}

data class PcSheetV11InputRecord(
    val kind: PcSheetV11InputKind,
    val sourceId: Uuid,
)

/**
 * Each source row appears once in the semantic ledger. A spell remains one
 * semantic entry when it will eventually appear in both quick list and Book.
 * A repeated source ID in the input is intentionally not silently de-duplicated.
 */
fun PcSheetV11SemanticData.inputRecords(): List<PcSheetV11InputRecord> {
    val extra = extensionData()
    return buildList {
        fun capture(kind: PcSheetV11InputKind, ids: List<Uuid>) {
            ids.forEach { id -> add(PcSheetV11InputRecord(kind, id)) }
        }
        capture(PcSheetV11InputKind.CLASS, extra.classes.map { it.id })
        capture(PcSheetV11InputKind.CUSTOM_ATTRIBUTE, attributes.mapNotNull { it.reference.customAttributeId })
        capture(PcSheetV11InputKind.CUSTOM_SKILL, skills.mapNotNull { it.customSkillId })
        capture(PcSheetV11InputKind.COMBAT_ENTRY, combatEntries.map { it.id })
        capture(PcSheetV11InputKind.TRAIT, traits.map { it.id })
        capture(PcSheetV11InputKind.ORDINARY_EQUIPMENT, ordinaryEquipment.map { it.sourceId })
        capture(PcSheetV11InputKind.SPECIAL_EQUIPMENT, specialEquipment.map { it.sourceId })
        capture(PcSheetV11InputKind.SPELL, spells.map { it.spell.id })
        capture(PcSheetV11InputKind.SPELLCASTING_SOURCE, spellcastingSources.map { it.id })
        capture(PcSheetV11InputKind.DIGITAL_NOTE, digitalNotes.mapNotNull { it.sourceId })
        capture(PcSheetV11InputKind.PROFICIENCY, extra.proficiencies.map { it.id })
        capture(PcSheetV11InputKind.WEAPON_MASTERY, extra.weaponMasteries.map { it.id })
        capture(PcSheetV11InputKind.RESOURCE, extra.resources.map { it.id })
        capture(PcSheetV11InputKind.CLASS_OPTION, extra.classOptions.map { it.id })
        capture(PcSheetV11InputKind.FORM, extra.forms.map { it.id })
        capture(PcSheetV11InputKind.COMPANION, extra.companions.map { it.id })
        capture(PcSheetV11InputKind.CUSTOM_MARKER, extra.customMarkers.map { it.id })
    }
}
