package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefenseType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterDamage
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterHealing
import io.github.mrsimkin.dndcustomaid.shared.character.characterAbilityReferenceAbbreviation
import io.github.mrsimkin.dndcustomaid.shared.character.customSavingThrowTotal
import io.github.mrsimkin.dndcustomaid.shared.character.generalSpellcastingRows
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterUnsignedIntegerInput
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun CharacterSupercompactV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    liveControlsEnabled: Boolean,
    onSheetChange: (CharacterSheet) -> Unit,
    onBack: () -> Unit,
) {
    var hpAmountText by rememberSaveable { mutableStateOf("") }
    var selectedSlotLevel by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedResourceId by rememberSaveable { mutableStateOf<String?>(null) }
    var expandedTraitId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAllSpells by rememberSaveable { mutableStateOf(false) }
    val hpAmount = hpAmountText.toIntOrNull()
    val successorState = LocalCharacterPcSettingsContextV4.current?.successorState ?: CharacterSuccessorState()

    fun applyOperational(updated: CharacterSheet) {
        if (updated != sheet) onSheetChange(updated)
        hpAmountText = ""
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val wide = maxWidth >= 760.dp
            LazyColumn(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = appSpacingV4(if (wide) 12.dp else 7.dp),
                    end = appSpacingV4(if (wide) 12.dp else 7.dp),
                    top = appSpacingV4(5.dp),
                    bottom = appSpacingV4(28.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
            ) {
                item(key = "supercompact-header") {
                    SupercompactIdentityHeaderV4(
                        sheet = sheet,
                        successorState = successorState,
                        closureState = closureState,
                        onBack = onBack,
                    )
                }

                item(key = "supercompact-combat-summary") {
                    SupercompactCombatSummaryV4(
                        sheet = sheet,
                        closureState = closureState,
                        liveControlsEnabled = liveControlsEnabled,
                        hpAmountText = hpAmountText,
                        onHpAmountChange = { hpAmountText = normalizeCharacterUnsignedIntegerInput(it) },
                        onDamage = {
                            hpAmount?.takeIf { it > 0 }?.let { amount ->
                                applyOperational(applyCharacterDamage(sheet, amount))
                            }
                        },
                        onHeal = {
                            hpAmount?.takeIf { it > 0 }?.let { amount ->
                                applyOperational(applyCharacterHealing(sheet, amount))
                            }
                        },
                        onSheetChange = onSheetChange,
                    )
                }

                if (wide) {
                    item(key = "supercompact-wide-body") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(12.dp)),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(0.92f),
                                verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                            ) {
                                SupercompactAbilitiesV4(sheet, successorState)
                                SupercompactReferenceV4(sheet, closureState)
                            }
                            Column(
                                modifier = Modifier.weight(1.35f),
                                verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                            ) {
                                SupercompactActionsV4(sheet, closureState)
                                SupercompactTraitsV4(
                                    sheet = sheet,
                                    closureState = closureState,
                                    expandedTraitId = expandedTraitId,
                                    onExpandedTraitChange = { expandedTraitId = it },
                                )
                                SupercompactSpellcastingV4(
                                    sheet = sheet,
                                    successorState = successorState,
                                    closureState = closureState,
                                    liveControlsEnabled = liveControlsEnabled,
                                    selectedSlotLevel = selectedSlotLevel,
                                    onSelectedSlotLevelChange = { selectedSlotLevel = it },
                                    showAllSpells = showAllSpells,
                                    onShowAllSpellsChange = { showAllSpells = it },
                                    onSheetChange = onSheetChange,
                                )
                                SupercompactResourcesV4(
                                    sheet = sheet,
                                    closureState = closureState,
                                    liveControlsEnabled = liveControlsEnabled,
                                    selectedResourceId = selectedResourceId,
                                    onSelectedResourceIdChange = { selectedResourceId = it },
                                    onSheetChange = onSheetChange,
                                )
                            }
                        }
                    }
                } else {
                    item(key = "supercompact-abilities") { SupercompactAbilitiesV4(sheet, successorState) }
                    item(key = "supercompact-reference") { SupercompactReferenceV4(sheet, closureState) }
                    item(key = "supercompact-actions") { SupercompactActionsV4(sheet, closureState) }
                    item(key = "supercompact-traits") {
                        SupercompactTraitsV4(
                            sheet = sheet,
                            closureState = closureState,
                            expandedTraitId = expandedTraitId,
                            onExpandedTraitChange = { expandedTraitId = it },
                        )
                    }
                    item(key = "supercompact-spellcasting") {
                        SupercompactSpellcastingV4(
                            sheet = sheet,
                            successorState = successorState,
                            closureState = closureState,
                            liveControlsEnabled = liveControlsEnabled,
                            selectedSlotLevel = selectedSlotLevel,
                            onSelectedSlotLevelChange = { selectedSlotLevel = it },
                            showAllSpells = showAllSpells,
                            onShowAllSpellsChange = { showAllSpells = it },
                            onSheetChange = onSheetChange,
                        )
                    }
                    item(key = "supercompact-resources") {
                        SupercompactResourcesV4(
                            sheet = sheet,
                            closureState = closureState,
                            liveControlsEnabled = liveControlsEnabled,
                            selectedResourceId = selectedResourceId,
                            onSelectedResourceIdChange = { selectedResourceId = it },
                            onSheetChange = onSheetChange,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SupercompactIdentityHeaderV4(
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    closureState: CharacterClosureState,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StableBackIconButton(onClick = onBack, contentDescription = "Volver a Ajustes de personaje")
        Column(modifier = Modifier.weight(1f)) {
            Text(sheet.name.ifBlank { "Personaje" }, style = MaterialTheme.typography.titleLarge)
            val identity = buildList {
                val species = successorState.speciesIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
                    ?: sheet.background.race.trim().takeIf { it.isNotEmpty() }
                val subrace = successorState.subraceIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
                listOfNotNull(species, subrace)
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString(" / ")
                    ?.let(::add)
                val classes = sheet.classes.sortedBy { it.sortOrder }.joinToString(" / ") { classLevel ->
                    buildString {
                        append(classLevel.name)
                        append(' ')
                        append(classLevel.level)
                        classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() }?.let { subclass ->
                            append(" (")
                            append(subclass)
                            append(')')
                        }
                    }
                }
                if (classes.isNotBlank()) add(classes)
            }.joinToString(" · ")
            if (identity.isNotBlank()) {
                Text(
                    identity,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val secondary = buildList {
                (successorState.backgroundIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
                    ?: sheet.background.name.trim().takeIf { it.isNotEmpty() })?.let(::add)
                when (sheet.status) {
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.RETIRED -> add("Retirado")
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.DEAD -> add("Muerto")
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.INACTIVE -> add("Inactivo")
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.ACTIVE -> Unit
                }
                if (closureState.tableModeEnabled) add("Modo Mesa")
            }.joinToString(" · ")
            if (secondary.isNotBlank()) {
                Text(
                    secondary,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SupercompactCombatSummaryV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    liveControlsEnabled: Boolean,
    hpAmountText: String,
    onHpAmountChange: (String) -> Unit,
    onDamage: () -> Unit,
    onHeal: () -> Unit,
    onSheetChange: (CharacterSheet) -> Unit,
) {
    SupercompactSectionV4 {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SupercompactInlineStatV4("CA", sheet.armorClass.toString(), Modifier.weight(0.8f))
            SupercompactInlineStatV4("PV", "${sheet.currentHp}/${sheet.maxHp}", Modifier.weight(1.15f))
            SupercompactInlineStatV4("Temp.", sheet.tempHp.toString(), Modifier.weight(0.9f))
            SupercompactInlineStatV4("Inic.", formatSupercompactSignedV4(sheet.initiativeModifier), Modifier.weight(0.9f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SupercompactInlineStatV4("Vel.", formatCharacterDistanceFeetV4(sheet.speed), Modifier.weight(1.25f))
            SupercompactInlineStatV4("Compet.", formatSupercompactSignedV4(sheet.finalProficiencyBonus), Modifier.weight(0.9f))
            SupercompactInlineStatV4("Inspiración", if (sheet.inspiration) "Sí" else "No", Modifier.weight(1f))
        }

        if (closureState.concentration != null || closureState.conditions.isNotEmpty() ||
            closureState.exhaustionLevel > 0 || closureState.temporaryEffects.any { it.active }
        ) {
            HorizontalDivider()
            closureState.concentration?.let {
                SupercompactReferenceLineV4("Concentración", it.name)
            }
            if (closureState.conditions.isNotEmpty()) {
                SupercompactReferenceLineV4(
                    "Condiciones",
                    closureState.conditions.sortedBy { it.sortOrder }.joinToString(" · ") { it.name },
                )
            }
            if (closureState.exhaustionLevel > 0) {
                SupercompactReferenceLineV4("Agotamiento", closureState.exhaustionLevel.toString())
            }
            val effects = closureState.temporaryEffects.filter { it.active }.sortedBy { it.sortOrder }
            if (effects.isNotEmpty()) {
                SupercompactReferenceLineV4("Efectos", effects.joinToString(" · ") { it.name })
            }
        }

        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onDamage,
                enabled = liveControlsEnabled && hpAmountText.toIntOrNull()?.let { it > 0 } == true &&
                    (sheet.currentHp > 0 || sheet.tempHp > 0),
            ) { Text("Daño") }
            OutlinedTextField(
                value = hpAmountText,
                onValueChange = onHpAmountChange,
                modifier = Modifier.weight(1f),
                label = { Text("Cantidad") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            TextButton(
                onClick = onHeal,
                enabled = liveControlsEnabled && hpAmountText.toIntOrNull()?.let { it > 0 } == true &&
                    sheet.currentHp < sheet.maxHp,
            ) { Text("Curar") }
        }

        if (sheet.currentHp <= 0 || sheet.deathSaveSuccesses > 0 || sheet.deathSaveFailures > 0) {
            HorizontalDivider()
            Text("SALVACIONES DE MUERTE", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(10.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SupercompactDeathSaveControlV4(
                    label = "Éxitos",
                    value = sheet.deathSaveSuccesses,
                    enabled = liveControlsEnabled,
                    onChange = { value -> onSheetChange(sheet.copy(deathSaveSuccesses = value.coerceIn(0, 3))) },
                    modifier = Modifier.weight(1f),
                )
                SupercompactDeathSaveControlV4(
                    label = "Fallos",
                    value = sheet.deathSaveFailures,
                    enabled = liveControlsEnabled,
                    onChange = { value -> onSheetChange(sheet.copy(deathSaveFailures = value.coerceIn(0, 3))) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SupercompactDeathSaveControlV4(
    label: String,
    value: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$label ${value.coerceIn(0, 3)}/3", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = { onChange(value - 1) }, enabled = enabled && value > 0) { Text("−1") }
        TextButton(onClick = { onChange(value + 1) }, enabled = enabled && value < 3) { Text("+1") }
    }
}

@Composable
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
private fun SupercompactReferenceV4(sheet: CharacterSheet, closureState: CharacterClosureState) {
    val languages = sheet.proficiencies
        .filter { it.type == CharacterProficiencyType.LANGUAGE }
        .sortedBy { it.sortOrder }
        .joinToString(", ") { it.name }
    val otherProficiencies = sheet.proficiencies
        .filter { it.type != CharacterProficiencyType.LANGUAGE }
        .sortedBy { it.sortOrder }
        .joinToString("; ") { it.name }
    val senses = closureState.senses.sortedBy { it.sortOrder }.joinToString("; ") { sense ->
        sense.rangeFeet?.let { "${sense.name} ${formatCharacterDistanceFeetV4(it)}" } ?: sense.name
    }
    val resistances = closureState.defenses.filter { it.type == CharacterDefenseType.RESISTANCE }
        .sortedBy { it.sortOrder }.joinToString(", ") { it.name }
    val immunities = closureState.defenses.filter { it.type == CharacterDefenseType.IMMUNITY }
        .sortedBy { it.sortOrder }.joinToString(", ") { it.name }
    val vulnerabilities = closureState.defenses.filter { it.type == CharacterDefenseType.VULNERABILITY }
        .sortedBy { it.sortOrder }.joinToString(", ") { it.name }

    SupercompactSectionV4(title = "REFERENCIA") {
        SupercompactReferenceLineV4("Percepción pasiva", sheet.passivePerception.toString())
        if (senses.isNotBlank()) SupercompactReferenceLineV4("Sentidos", senses)
        if (languages.isNotBlank()) SupercompactReferenceLineV4("Idiomas", languages)
        if (otherProficiencies.isNotBlank()) SupercompactReferenceLineV4("Competencias", otherProficiencies)
        if (resistances.isNotBlank()) SupercompactReferenceLineV4("Resistencias", resistances)
        if (immunities.isNotBlank()) SupercompactReferenceLineV4("Inmunidades", immunities)
        if (vulnerabilities.isNotBlank()) SupercompactReferenceLineV4("Vulnerabilidades", vulnerabilities)
    }
}

@Composable
private fun SupercompactActionsV4(sheet: CharacterSheet, closureState: CharacterClosureState) {
    if (sheet.combatEntries.isEmpty()) return
    val grouped = listOf(
        CharacterCombatEntryType.ATTACK to "ATAQUES",
        CharacterCombatEntryType.ACTION to "ACCIONES",
        CharacterCombatEntryType.BONUS_ACTION to "ACCIONES ADICIONALES",
        CharacterCombatEntryType.REACTION to "REACCIONES",
        CharacterCombatEntryType.OTHER to "OTRAS ACCIONES",
    )
    grouped.forEach { (type, title) ->
        val entries = sheet.combatEntries.filter { it.type == type }
            .sortedWith(compareByDescending<CharacterCombatEntry> {
                closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, it.id)
            }.thenBy { it.sortOrder })
        if (entries.isNotEmpty()) {
            SupercompactSectionV4(title = title) {
                entries.forEachIndexed { index, entry ->
                    SupercompactNaturalEntryV4(
                        favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                        title = entry.name,
                        primary = listOfNotNull(
                            entry.attackModifier?.let { "Ataque ${formatSupercompactSignedV4(it)}" },
                            entry.damageEffect.takeIf { it.isNotBlank() },
                            entry.rangeText?.takeIf { it.isNotBlank() },
                        ).joinToString(" · "),
                        secondary = entry.notes,
                    )
                    if (index < entries.lastIndex) HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun SupercompactTraitsV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    expandedTraitId: String?,
    onExpandedTraitChange: (String?) -> Unit,
) {
    if (sheet.traits.isEmpty()) return
    val traits = sheet.traits.sortedWith(compareByDescending<CharacterTrait> {
        closureState.hasQuickAccess(CharacterQuickAccessKind.TRAIT, it.id)
    }.thenBy { it.sortOrder })
    SupercompactSectionV4(title = "RASGOS") {
        traits.forEachIndexed { index, trait ->
            val expanded = expandedTraitId == trait.id.toString()
            Column(
                modifier = Modifier.fillMaxWidth().clickable {
                    onExpandedTraitChange(if (expanded) null else trait.id.toString())
                },
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (closureState.hasQuickAccess(CharacterQuickAccessKind.TRAIT, trait.id)) {
                        SupercompactFavoriteIndicatorV4()
                    }
                    Text(trait.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                    trait.maxUses?.let { max ->
                        Text(
                            "${(max - trait.spentUses).coerceAtLeast(0)}/$max",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                val mechanics = listOfNotNull(
                    trait.recovery?.takeIf { it.isNotBlank() },
                    trait.activation?.name?.replace('_', ' ')?.lowercase()?.replaceFirstChar { it.uppercase() },
                    trait.source.takeIf { it.isNotBlank() },
                ).joinToString(" · ")
                if (mechanics.isNotBlank()) {
                    Text(mechanics, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                trait.description.takeIf { it.isNotBlank() }?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                    )
                }
            }
            if (index < traits.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun SupercompactSpellcastingV4(
    sheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    closureState: CharacterClosureState,
    liveControlsEnabled: Boolean,
    selectedSlotLevel: Int?,
    onSelectedSlotLevelChange: (Int?) -> Unit,
    showAllSpells: Boolean,
    onShowAllSpellsChange: (Boolean) -> Unit,
    onSheetChange: (CharacterSheet) -> Unit,
) {
    if (!sheet.spellcasterEnabled && sheet.spells.isEmpty() && sheet.spellSlots.none { it.totalSlots > 0 }) return
    val spells = sheet.spells.sortedWith(compareByDescending<CharacterSpell> {
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

        val slots = sheet.spellSlots.filter { it.totalSlots > 0 }.sortedBy { it.level }
        if (slots.isNotEmpty()) {
            Text("Espacios", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                slots.forEach { slot ->
                    val available = (slot.totalSlots - slot.spentSlots).coerceAtLeast(0)
                    Surface(
                        modifier = Modifier.clickable {
                            onSelectedSlotLevelChange(if (selectedSlotLevel == slot.level) null else slot.level)
                        },
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(
                            1.dp,
                            if (selectedSlotLevel == slot.level) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        ),
                    ) {
                        Text(
                            "${slot.level}º $available/${slot.totalSlots}",
                            modifier = Modifier.padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(3.dp)),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            selectedSlotLevel?.let { level ->
                val slot = slots.firstOrNull { it.level == level }
                if (slot != null) {
                    val available = (slot.totalSlots - slot.spentSlots).coerceAtLeast(0)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Nivel ${slot.level}: $available/${slot.totalSlots}", style = MaterialTheme.typography.labelSmall)
                        TextButton(
                            onClick = {
                                onSheetChange(
                                    sheet.copy(
                                        spellSlots = sheet.spellSlots.map { current ->
                                            if (current.level == slot.level) current.copy(
                                                spentSlots = (current.spentSlots + 1).coerceAtMost(current.totalSlots),
                                            ) else current
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
                                            if (current.level == slot.level) current.copy(
                                                spentSlots = (current.spentSlots - 1).coerceAtLeast(0),
                                            ) else current
                                        },
                                    ),
                                )
                            },
                            enabled = liveControlsEnabled && slot.spentSlots > 0,
                        ) { Text("Recuperar") }
                    }
                }
            }
        }

        if (spells.isNotEmpty()) {
            val cantrips = spells.filter { it.level == 0 }
            if (cantrips.isNotEmpty()) {
                SupercompactSpellNamesV4("Trucos", cantrips, sourceNamesById)
            }
            val leveled = spells.filter { it.level > 0 }
            if (leveled.isNotEmpty()) {
                val shown = if (showAllSpells) leveled else leveled.take(8)
                SupercompactSpellNamesV4("Preparados / conocidos", shown, sourceNamesById)
                if (leveled.size > shown.size) {
                    TextButton(onClick = { onShowAllSpellsChange(true) }) {
                        Text("Ver todos (${leveled.size})")
                    }
                } else if (showAllSpells && leveled.size > 8) {
                    TextButton(onClick = { onShowAllSpellsChange(false) }) { Text("Mostrar menos") }
                }
            }
        }
    }
}

@Composable
private fun SupercompactSpellNamesV4(
    label: String,
    spells: List<CharacterSpell>,
    sourceNamesById: Map<kotlin.uuid.Uuid, String>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        spells.forEach { spell ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    spell.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val metadata = buildList {
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
            }
        }
    }
}

@Composable
private fun SupercompactResourcesV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    liveControlsEnabled: Boolean,
    selectedResourceId: String?,
    onSelectedResourceIdChange: (String?) -> Unit,
    onSheetChange: (CharacterSheet) -> Unit,
) {
    if (sheet.resources.isEmpty()) return
    val resources = sheet.resources.sortedWith(compareByDescending<CharacterResource> {
        closureState.hasQuickAccess(CharacterQuickAccessKind.RESOURCE, it.id)
    }.thenBy { it.sortOrder })
    SupercompactSectionV4(title = "RECURSOS") {
        resources.forEachIndexed { index, resource ->
            val selected = selectedResourceId == resource.id.toString()
            Column(
                modifier = Modifier.fillMaxWidth().clickable {
                    onSelectedResourceIdChange(if (selected) null else resource.id.toString())
                },
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(1.dp)),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (closureState.hasQuickAccess(CharacterQuickAccessKind.RESOURCE, resource.id)) {
                        SupercompactFavoriteIndicatorV4()
                    }
                    Text(resource.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        resource.maxValue?.let { "${resource.currentValue}/$it" } ?: resource.currentValue.toString(),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                val detail = listOfNotNull(resource.recovery?.takeIf { it.isNotBlank() }, resource.source?.takeIf { it.isNotBlank() })
                    .joinToString(" · ")
                if (detail.isNotBlank()) {
                    Text(detail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (selected) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(
                            onClick = {
                                onSheetChange(
                                    sheet.copy(
                                        resources = sheet.resources.map { current ->
                                            if (current.id == resource.id) current.copy(
                                                currentValue = (current.currentValue - 1).coerceAtLeast(0),
                                            ) else current
                                        },
                                    ),
                                )
                            },
                            enabled = liveControlsEnabled && resource.currentValue > 0,
                        ) { Text("Usar") }
                        TextButton(
                            onClick = {
                                onSheetChange(
                                    sheet.copy(
                                        resources = sheet.resources.map { current ->
                                            if (current.id == resource.id) current.copy(
                                                currentValue = current.maxValue?.let { max ->
                                                    (current.currentValue + 1).coerceAtMost(max)
                                                } ?: current.currentValue + 1,
                                            ) else current
                                        },
                                    ),
                                )
                            },
                            enabled = liveControlsEnabled && (resource.maxValue?.let { resource.currentValue < it } ?: true),
                        ) { Text("Recuperar") }
                    }
                }
            }
            if (index < resources.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun SupercompactSectionV4(
    title: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
    ) {
        if (!title.isNullOrBlank()) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider()
        }
        content()
    }
}

@Composable
private fun SupercompactInlineStatV4(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
    }
}

@Composable
private fun SupercompactReferenceLineV4(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.Top,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SupercompactNaturalEntryV4(
    favorite: Boolean,
    title: String,
    primary: String,
    secondary: String?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(1.dp)),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (favorite) SupercompactFavoriteIndicatorV4()
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        }
        if (primary.isNotBlank()) {
            Text(primary, style = MaterialTheme.typography.bodySmall)
        }
        secondary?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
private fun SupercompactFavoriteIndicatorV4() {
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.size(15.dp).padding(end = 2.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outer = size.minDimension * 0.45f
        val inner = outer * 0.46f
        val path = Path()
        repeat(10) { index ->
            val angle = -Math.PI / 2.0 + index * Math.PI / 5.0
            val radius = if (index % 2 == 0) outer else inner
            val point = Offset(
                center.x + cos(angle).toFloat() * radius,
                center.y + sin(angle).toFloat() * radius,
            )
            if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        path.close()
        drawPath(path, color)
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

private fun supercompactSpellcastingAbilityLabelV4(ability: SpellcastingAbility): String? = when (ability) {
    SpellcastingAbility.STRENGTH -> "FUE"
    SpellcastingAbility.DEXTERITY -> "DES"
    SpellcastingAbility.CONSTITUTION -> "CON"
    SpellcastingAbility.INTELLIGENCE -> "INT"
    SpellcastingAbility.WISDOM -> "SAB"
    SpellcastingAbility.CHARISMA -> "CAR"
    SpellcastingAbility.OTHER -> "Otra característica"
    SpellcastingAbility.NONE -> null
}

private fun formatSupercompactSignedV4(value: Int): String = if (value >= 0) "+$value" else value.toString()
