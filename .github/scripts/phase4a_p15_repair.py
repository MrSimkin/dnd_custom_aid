from pathlib import Path
import re

SUPER_PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSupercompactV4.kt")
EDITOR_PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
FORMATTER_PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDistanceFormatV4.kt")
WORKFLOW_PATH = Path(".github/workflows/phase4a-p15-repair.yml")
SCRIPT_PATH = Path(".github/scripts/phase4a_p15_repair.py")


def replace_once(source: str, old: str, new: str, label: str) -> str:
    count = source.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    return source.replace(old, new, 1)


def regex_once(source: str, pattern: str, replacement: str, label: str) -> str:
    updated, count = re.subn(pattern, replacement, source, count=1, flags=re.S)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one regex match, found {count}")
    return updated


text = SUPER_PATH.read_text()

text = replace_once(
    text,
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait\n",
    "successor-state import",
)
text = replace_once(
    text,
    "import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterHealing\nimport io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\nimport io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterUnsignedIntegerInput\nimport kotlin.math.abs\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterHealing\nimport io.github.mrsimkin.dndcustomaid.shared.character.characterAbilityReferenceAbbreviation\nimport io.github.mrsimkin.dndcustomaid.shared.character.customSavingThrowTotal\nimport io.github.mrsimkin.dndcustomaid.shared.character.generalSpellcastingRows\nimport io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\nimport io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterUnsignedIntegerInput\n",
    "projection imports",
)
text = replace_once(
    text,
    "    val hpAmount = hpAmountText.toIntOrNull()\n\n    fun applyOperational(updated: CharacterSheet) {",
    "    val hpAmount = hpAmountText.toIntOrNull()\n    val successorState = LocalCharacterPcSettingsContextV4.current?.successorState ?: CharacterSuccessorState()\n\n    fun applyOperational(updated: CharacterSheet) {",
    "successor-state acquisition",
)
text = replace_once(
    text,
    '                    SupercompactIdentityHeaderV4(sheet = sheet, closureState = closureState, onBack = onBack)',
    '''                    SupercompactIdentityHeaderV4(
                        sheet = sheet,
                        successorState = successorState,
                        closureState = closureState,
                        onBack = onBack,
                    )''',
    "identity call",
)

text = text.replace("SupercompactAbilitiesV4(sheet)", "SupercompactAbilitiesV4(sheet, successorState)")
if text.count("SupercompactAbilitiesV4(sheet, successorState)") != 2:
    raise SystemExit("abilities calls: expected exactly two updated call sites")

text = replace_once(
    text,
    '''                                SupercompactSpellcastingV4(
                                    sheet = sheet,
                                    closureState = closureState,''',
    '''                                SupercompactSpellcastingV4(
                                    sheet = sheet,
                                    successorState = successorState,
                                    closureState = closureState,''',
    "wide spellcasting call",
)
text = replace_once(
    text,
    '''                        SupercompactSpellcastingV4(
                            sheet = sheet,
                            closureState = closureState,''',
    '''                        SupercompactSpellcastingV4(
                            sheet = sheet,
                            successorState = successorState,
                            closureState = closureState,''',
    "narrow spellcasting call",
)

text = replace_once(
    text,
    '''private fun SupercompactIdentityHeaderV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,''',
    '''private fun SupercompactIdentityHeaderV4(
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    closureState: CharacterClosureState,''',
    "identity signature",
)
text = replace_once(
    text,
    "                sheet.background.race.trim().takeIf { it.isNotEmpty() }?.let(::add)\n",
    '''                val species = successorState.speciesIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
                    ?: sheet.background.race.trim().takeIf { it.isNotEmpty() }
                val subrace = successorState.subraceIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
                listOfNotNull(species, subrace)
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString(" / ")
                    ?.let(::add)
''',
    "canonical species/subrace",
)
text = replace_once(
    text,
    "                sheet.background.name.trim().takeIf { it.isNotEmpty() }?.let(::add)\n",
    '''                (successorState.backgroundIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
                    ?: sheet.background.name.trim().takeIf { it.isNotEmpty() })?.let(::add)
''',
    "canonical background",
)

abilities_replacement = '''@Composable
private fun SupercompactAbilitiesV4(
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
) {
    SupercompactSectionV4(title = "CARACTERÍSTICAS") {
        Row(modifier = Modifier.fillMaxWidth()) {
            CharacterAbility.entries.forEach { ability ->
                Text(
                    supercompactAbilityLabelV4(ability),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            CharacterAbility.entries.forEach { ability ->
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(sheet.abilityScore(ability).toString(), style = MaterialTheme.typography.bodyMedium)
                    Text(formatSupercompactSignedV4(sheet.abilityModifier(ability)), style = MaterialTheme.typography.labelSmall)
                    Text(
                        "Salv. ${formatSupercompactSignedV4(sheet.savingThrowTotal(ability))}",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                    )
                }
            }
        }
        val customAttributes = successorState.customAttributes.sortedBy { it.sortOrder }
        if (customAttributes.isNotEmpty()) {
            HorizontalDivider()
            customAttributes.forEachIndexed { index, attribute ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${attribute.name} (${attribute.abbreviation})",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                    )
                    Text(attribute.score.toString(), style = MaterialTheme.typography.bodyMedium)
                    Text("Mod ${formatSupercompactSignedV4(attribute.modifier)}", style = MaterialTheme.typography.labelSmall)
                    if (attribute.savingThrowEnabled) {
                        val savingThrow = sheet.customSavingThrowTotal(attribute)
                        Text(
                            "Salv. ${savingThrow?.let(::formatSupercompactSignedV4) ?: "—"}",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    }
                }
                if (index < customAttributes.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SupercompactReferenceV4'''
text = regex_once(
    text,
    r"@Composable\nprivate fun SupercompactAbilitiesV4\(sheet: CharacterSheet\) \{.*?\n\}\n\n@Composable\nprivate fun SupercompactReferenceV4",
    abilities_replacement,
    "abilities projection",
)

# Remove the old local formatter before renaming its call sites.
text = regex_once(
    text,
    r"\nprivate fun formatSupercompactSpeedV4\(feet: Int\): String \{.*?\n\}\s*$",
    "\n",
    "local supercompact distance formatter",
)
text = text.replace("formatSupercompactSpeedV4(", "formatCharacterDistanceFeetV4(")
if "formatSupercompactSpeedV4(" in text:
    raise SystemExit("legacy supercompact distance formatter remains")

text = replace_once(
    text,
    '''private fun SupercompactSpellcastingV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,''',
    '''private fun SupercompactSpellcastingV4(
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    closureState: CharacterClosureState,''',
    "spellcasting signature",
)

old_spell_header = '''    val spells = sheet.spells.sortedWith(compareByDescending<CharacterSpell> {
        closureState.hasQuickAccess(CharacterQuickAccessKind.SPELL, it.id)
    }.thenBy { it.level }.thenBy { it.sortOrder })

    SupercompactSectionV4(title = "CONJUROS") {
        val castingSummary = buildList {
            sheet.spellSaveDc?.let { add("CD $it") }
            sheet.spellAttackModifier?.let { add("Ataque ${formatSupercompactSignedV4(it)}") }
            supercompactSpellcastingAbilityLabelV4(sheet.spellcastingAbility)?.let(::add)
        }.joinToString(" · ")
        if (castingSummary.isNotBlank()) {
            Text(castingSummary, style = MaterialTheme.typography.bodySmall)
        }
'''
new_spell_header = '''    val spells = sheet.spells.sortedWith(compareByDescending<CharacterSpell> {
        closureState.hasQuickAccess(CharacterQuickAccessKind.SPELL, it.id)
    }.thenBy { it.level }.thenBy { it.sortOrder })
    val castingRows = sheet.generalSpellcastingRows(successorState)
    val sourceNamesById = sheet.spellcastingSources.associate { it.id to it.name }

    SupercompactSectionV4(title = "CONJUROS") {
        if (castingRows.isNotEmpty()) {
            castingRows.forEach { row ->
                val castingSummary = buildList {
                    row.profile?.let { profile ->
                        characterAbilityReferenceAbbreviation(profile.ability, successorState)
                            .takeIf { it != "—" }
                            ?.let(::add)
                    }
                    row.saveDc?.let { add("CD $it") }
                    row.spellAttackModifier?.let { add("Ataque ${formatSupercompactSignedV4(it)}") }
                }.joinToString(" · ")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(row.source.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    if (castingSummary.isNotBlank()) {
                        Text(castingSummary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            val legacyCastingSummary = buildList {
                sheet.spellSaveDc?.let { add("CD $it") }
                sheet.spellAttackModifier?.let { add("Ataque ${formatSupercompactSignedV4(it)}") }
                supercompactSpellcastingAbilityLabelV4(sheet.spellcastingAbility)?.let(::add)
            }.joinToString(" · ")
            if (legacyCastingSummary.isNotBlank()) {
                Text(legacyCastingSummary, style = MaterialTheme.typography.bodySmall)
            }
        }
'''
text = replace_once(text, old_spell_header, new_spell_header, "source-aware spellcasting summary")
text = replace_once(
    text,
    'SupercompactSpellNamesV4("Trucos", cantrips)',
    'SupercompactSpellNamesV4("Trucos", cantrips, sourceNamesById)',
    "cantrip source wiring",
)
text = replace_once(
    text,
    'SupercompactSpellNamesV4("Preparados / conocidos", shown)',
    'SupercompactSpellNamesV4("Preparados / conocidos", shown, sourceNamesById)',
    "leveled-spell source wiring",
)
text = replace_once(
    text,
    '''private fun SupercompactSpellNamesV4(label: String, spells: List<CharacterSpell>) {
    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {''',
    '''private fun SupercompactSpellNamesV4(
    label: String,
    spells: List<CharacterSpell>,
    sourceNamesById: Map<kotlin.uuid.Uuid, String>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {''',
    "spell-list signature",
)
text = replace_once(
    text,
    '''                val flags = buildList {
                    if (spell.concentration) add("C")
                    if (spell.ritual) add("R")
                }.joinToString(" · ")
                if (flags.isNotBlank()) {
                    Text(flags, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
''',
    '''                val metadata = buildList {
                    if (spell.sourceAssociations.any { it.prepared }) add("Prep.")
                    if (spell.concentration) add("C")
                    if (spell.ritual) add("R")
                    spell.sourceAssociations
                        .mapNotNull { sourceNamesById[it.sourceId] }
                        .distinct()
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(" / ")
                        ?.let(::add)
                }.joinToString(" · ")
                if (metadata.isNotBlank()) {
                    Text(metadata, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
''',
    "spell source metadata",
)
SUPER_PATH.write_text(text)

editor = EDITOR_PATH.read_text()
editor = regex_once(
    editor,
    r"\nprivate fun formatSpeedV4\(raw: String\): String \{.*?\n\}\n",
    "\n",
    "editor local distance formatter",
)
editor_call_count = editor.count("formatSpeedV4(")
if editor_call_count < 1:
    raise SystemExit("editor distance calls: expected at least one call")
editor = editor.replace("formatSpeedV4(", "formatCharacterDistanceFeetV4(")
EDITOR_PATH.write_text(editor)

FORMATTER_PATH.write_text('''package io.github.mrsimkin.dndcustomaid.android

import kotlin.math.abs

/** Canonical V4 character distance presentation: imperial first, metric companion in decimetre precision. */
internal fun formatCharacterDistanceFeetV4(feet: Int): String {
    val metricTenths = feet * 3
    val wholeMeters = metricTenths / 10
    val remainder = abs(metricTenths % 10)
    val metric = if (remainder == 0) wholeMeters.toString() else "$wholeMeters,$remainder"
    return "$feet ft ($metric m)"
}

internal fun formatCharacterDistanceFeetV4(raw: String): String {
    val feet = raw.trim().toIntOrNull() ?: return raw.ifBlank { "—" }
    return formatCharacterDistanceFeetV4(feet)
}
''')

super_after = SUPER_PATH.read_text()
editor_after = EDITOR_PATH.read_text()
required = [
    "successorState.customAttributes.sortedBy { it.sortOrder }",
    "sheet.customSavingThrowTotal(attribute)",
    "sheet.generalSpellcastingRows(successorState)",
    "characterAbilityReferenceAbbreviation(profile.ability, successorState)",
    "successorState.speciesIdentity",
    "successorState.subraceIdentity",
    "successorState.backgroundIdentity",
    "formatCharacterDistanceFeetV4(sheet.speed)",
]
missing = [token for token in required if token not in super_after]
if missing:
    raise SystemExit(f"P15 invariant tokens missing: {missing}")
if "formatSupercompactSpeedV4(" in super_after or "formatSpeedV4(" in editor_after:
    raise SystemExit("A local speed/distance formatter remains after canonicalization")

# Temporary repair machinery must not survive the repair commit.
WORKFLOW_PATH.unlink()
SCRIPT_PATH.unlink()
