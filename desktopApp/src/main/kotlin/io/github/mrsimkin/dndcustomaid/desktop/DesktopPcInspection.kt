package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.hosted.PcDmCorrectionPatch
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid
// Refactor F3: the technical inspection and correction of a PC (sync state, collections, closure, successor), moved out of DesktopPcManager.kt; F4 takes it out of the owner's screen.

@Composable
internal fun PcCorrectionEditor(
    draft: PcCorrectionDraft,
    reason: String,
    onDraftChange: (PcCorrectionDraft) -> Unit,
    onReasonChange: (String) -> Unit,
    onApply: () -> Unit,
) {
    PcAuditSection("Corrección DM — núcleo del PC") {
        Text(
            "Acción explícita y auditada: modifica campos nucleares, agrega un checkpoint de reconciliación " +
                "y encola el snapshot. Las colecciones complejas permanecen sólo en inspección en este corte.",
            style = MaterialTheme.typography.bodyMedium,
        )
        PcCorrectionField("Nombre", draft.name) { onDraftChange(draft.copy(name = it)) }
        Text("Estado", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CharacterStatus.entries.forEach { status ->
                if (draft.status == status) {
                    Button(onClick = { onDraftChange(draft.copy(status = status)) }) { Text(pcStatusLabel(status)) }
                } else {
                    TextButton(onClick = { onDraftChange(draft.copy(status = status)) }) { Text(pcStatusLabel(status)) }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactField("FUE", draft.strength) { onDraftChange(draft.copy(strength = it)) }
            CompactField("DES", draft.dexterity) { onDraftChange(draft.copy(dexterity = it)) }
            CompactField("CON", draft.constitution) { onDraftChange(draft.copy(constitution = it)) }
            CompactField("INT", draft.intelligence) { onDraftChange(draft.copy(intelligence = it)) }
            CompactField("SAB", draft.wisdom) { onDraftChange(draft.copy(wisdom = it)) }
            CompactField("CAR", draft.charisma) { onDraftChange(draft.copy(charisma = it)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactField("CA", draft.armorClass) { onDraftChange(draft.copy(armorClass = it)) }
            CompactField("PG máx.", draft.maxHp) { onDraftChange(draft.copy(maxHp = it)) }
            CompactField("PG actual", draft.currentHp) { onDraftChange(draft.copy(currentHp = it)) }
            CompactField("PG temp.", draft.tempHp) { onDraftChange(draft.copy(tempHp = it)) }
            CompactField("Velocidad", draft.speed) { onDraftChange(draft.copy(speed = it)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactField("Aj. iniciativa", draft.initiativeAdjustment) {
                onDraftChange(draft.copy(initiativeAdjustment = it))
            }
            CompactField("Aj. competencia", draft.proficiencyBonusAdjustment) {
                onDraftChange(draft.copy(proficiencyBonusAdjustment = it))
            }
            CompactField("Aj. Percepción", draft.passivePerceptionAdjustment) {
                onDraftChange(draft.copy(passivePerceptionAdjustment = it))
            }
        }
        if (draft.inspiration) {
            Button(onClick = { onDraftChange(draft.copy(inspiration = false)) }) { Text("Inspiración: sí") }
        } else {
            TextButton(onClick = { onDraftChange(draft.copy(inspiration = true)) }) { Text("Inspiración: no") }
        }
        PcCorrectionField("Notas generales", draft.generalNotes, false, 4) {
            onDraftChange(draft.copy(generalNotes = it))
        }
        PcCorrectionField("Motivo de la corrección (obligatorio)", reason, false, 3, onReasonChange)
        Button(
            enabled = draft.name.isNotBlank() && reason.isNotBlank(),
            onClick = onApply,
        ) { Text("Aplicar corrección DM y encolar sync") }
    }
}

@Composable
internal fun PcCoreInspection(sheet: CharacterSheet) {
    PcAuditSection("Ficha canónica — núcleo") {
        PcAuditLine("Nombre", sheet.name)
        PcAuditLine("Estado", pcStatusLabel(sheet.status))
        PcAuditLine(
            "Atributos",
            "FUE " + sheet.strength + " · DES " + sheet.dexterity + " · CON " + sheet.constitution +
                " · INT " + sheet.intelligence + " · SAB " + sheet.wisdom + " · CAR " + sheet.charisma,
        )
        PcAuditLine(
            "Combate",
            "CA " + sheet.armorClass + " · PG " + sheet.currentHp + "/" + sheet.maxHp +
                " (+" + sheet.tempHp + " temp.) · Iniciativa " + signed(sheet.initiativeModifier) +
                " · Velocidad " + sheet.speed,
        )
        PcAuditLine(
            "Competencia",
            "base " + sheet.standardProficiencyBonus + " · ajuste " + signed(sheet.proficiencyBonusAdjustment) +
                " · final " + signed(sheet.finalProficiencyBonus),
        )
        PcAuditLine("Percepción Pasiva", sheet.passivePerception.toString())
        PcAuditLine("Inspiración", if (sheet.inspiration) "sí" else "no")
        PcAuditLine(
            "Salvaciones de muerte",
            sheet.deathSaveSuccesses.toString() + " éxitos / " + sheet.deathSaveFailures + " fallos",
        )
        PcAuditLine("Notas generales", sheet.generalNotes.ifBlank { "—" })
        PcAuditLine(
            "Background",
            listOf(sheet.background.name, sheet.background.race, sheet.background.religionFaith)
                .filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "—" },
        )
        PcAuditLine(
            "Clases",
            sheet.classes.joinToString(" · ") {
                it.name + " " + it.level + (it.subclassName?.takeIf(String::isNotBlank)?.let { sub -> " / " + sub } ?: "")
            }.ifBlank { "—" },
        )
        PcAuditLine(
            "Tiradas de salvación",
            sheet.savingThrows.joinToString(" · ") {
                it.ability.name + ": " + signed(sheet.savingThrowTotal(it.ability)) +
                    (if (it.proficient) " P" else "")
            },
        )
        PcAuditLine(
            "Habilidades",
            sheet.skills.joinToString(" · ") {
                it.key.name + ": " + signed(sheet.skillTotal(it.key)) + " (" + it.training.name + ")"
            },
        )
    }
}

@Composable
internal fun PcCollectionsInspection(sheet: CharacterSheet) {
    PcAuditSection("Ficha canónica — colecciones") {
        PcAuditLine("Acciones / ataques", listSummary(sheet.combatEntries) { it.name + " [" + it.type.name + "]" })
        PcAuditLine("Inventario", listSummary(sheet.inventoryItems) { it.name + " ×" + it.quantity })
        PcAuditLine("Monedas", listSummary(sheet.currencies) { it.name + ": " + it.amount })
        PcAuditLine("Rasgos", listSummary(sheet.traits) { it.name + " [" + it.type.name + "]" })
        PcAuditLine("Fuentes mágicas", listSummary(sheet.spellcastingSources) { it.name })
        PcAuditLine("Conjuros", listSummary(sheet.spells) { it.name + " (nivel " + it.level + ")" })
        PcAuditLine(
            "Slots",
            listSummary(sheet.spellSlots) { "N" + it.level + ": " + (it.totalSlots - it.spentSlots) + "/" + it.totalSlots },
        )
        PcAuditLine("Notas tituladas", listSummary(sheet.noteCards) { it.title })
        PcAuditLine("Competencias", listSummary(sheet.proficiencies) { it.type.name + ": " + it.name })
        PcAuditLine("Maestrías", listSummary(sheet.weaponMasteries) { it.weaponName + ": " + it.masteryName })
        PcAuditLine(
            "Recursos",
            listSummary(sheet.resources) {
                it.name + ": " + it.currentValue + (it.maxValue?.let { max -> "/" + max } ?: "")
            },
        )
        PcAuditLine("Opciones de clase", listSummary(sheet.classOptions) { it.kind.name + ": " + it.name })
        PcAuditLine("Formas", listSummary(sheet.forms) { it.name })
        PcAuditLine("Compañeros", listSummary(sheet.companions) { it.name + " (" + it.kind + ")" })
    }
}

@Composable
internal fun PcClosureInspection(state: CharacterClosureState) {
    PcAuditSection("Estado extendido / cierre") {
        PcAuditLine("Agotamiento", state.exhaustionLevel.toString())
        PcAuditLine(
            "Concentración",
            state.concentration?.let { it.name + (it.notes?.let { note -> " · " + note } ?: "") } ?: "—",
        )
        PcAuditLine(
            "Progreso",
            state.progressMode.name + " · XP " + state.experiencePoints + " · " + state.milestoneProgress,
        )
        PcAuditLine("Condiciones", listSummary(state.conditions) { it.name })
        PcAuditLine("Defensas", listSummary(state.defenses) { it.type.name + ": " + it.name })
        PcAuditLine("Movimientos", listSummary(state.movements) { it.name + ": " + (it.speedFeet?.toString() ?: "—") })
        PcAuditLine("Sentidos", listSummary(state.senses) { it.name + ": " + (it.rangeFeet?.toString() ?: "—") })
        PcAuditLine(
            "Recuperación de recursos",
            listSummary(state.resourceRecovery) {
                it.resourceId.toString().take(8) + "… " + it.cadence.name + "/" + it.amountMode.name
            },
        )
        PcAuditLine(
            "Uso de inventario",
            listSummary(state.inventoryUsage) {
                it.itemId.toString().take(8) + "… " + it.kind.name + "/" + it.carryState.name
            },
        )
        PcAuditLine(
            "Habilidades personalizadas",
            listSummary(state.customSkills) { it.name + " (" + it.ability.name + "/" + it.training.name + ")" },
        )
        PcAuditLine(
            "Efectos temporales",
            listSummary(state.temporaryEffects) { it.name + if (it.active) " [activo]" else "" },
        )
        PcAuditLine("Overrides de módulo", listSummary(state.moduleOverrides) { it.module.name + ": " + it.mode.name })
        PcAuditLine(
            "Accesos rápidos",
            listSummary(state.quickAccess) { it.kind.name + ":" + it.targetId.toString().take(8) + "…" },
        )
        PcAuditLine("Retrato", state.portraitRef ?: "—")
        PcAuditLine("Token", state.tokenRef ?: "—")
    }
}

@Composable
internal fun PcSuccessorInspection(state: CharacterSuccessorState) {
    PcAuditSection("Estado sucesor / identidad y extensiones") {
        PcAuditLine(
            "Atributos personalizados",
            listSummary(state.customAttributes) { it.name + " (" + it.abbreviation + ")=" + it.score },
        )
        PcAuditLine(
            "Habilidades custom configuradas",
            state.customSkillAbilities.size.toString(),
        )
        PcAuditLine("Perfiles de lanzamiento", state.spellcastingProfiles.size.toString())
        PcAuditLine("Perfiles de daño", state.combatDamage.size.toString())
        PcAuditLine(
            "Marcadores",
            listSummary(state.customMarkers) {
                it.name + ": " + it.currentValue + (it.maxValue?.let { max -> "/" + max } ?: "")
            },
        )
        PcAuditLine("Configuraciones de recursos", state.resourceConfigurations.size.toString())
        PcAuditLine("Raza", state.speciesIdentity?.name ?: "—")
        PcAuditLine("Subraza", state.subraceIdentity?.name ?: "—")
        PcAuditLine("Background canónico", state.backgroundIdentity?.name ?: "—")
        PcAuditLine("Subclases con identidad", state.subclassIdentities.size.toString())
        PcAuditLine("Relaciones de procedencia de rasgos", state.traitProvenance.size.toString())
        PcAuditLine(
            "Imágenes de background",
            listSummary(state.backgroundImages) { it.slot.name + ": " + (it.originalName ?: it.mimeType) },
        )
        PcAuditLine(
            "Preferencias",
            "inspiración visible=" + state.preferences.inspirationVisible +
                "; pestañas=" + state.preferences.tabOrder.size +
                "; valores='" + state.preferences.valuablesText.take(80) + "'",
        )
    }
}

@Composable
internal fun PcAuditSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = androidx.compose.material3.CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
internal fun PcAuditLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun PcCorrectionField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
    )
}

@Composable
internal fun CompactField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.width(112.dp),
    )
}

internal data class PcCorrectionDraft(
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
    val inspiration: Boolean,
    val generalNotes: String,
) {
    fun toPatch(): PcDmCorrectionPatch {
        fun parse(label: String, raw: String): Int =
            requireNotNull(raw.trim().toIntOrNull()) { label + " debe ser un número entero." }

        require(name.trim().isNotEmpty()) { "El nombre del PC no puede quedar vacío." }
        return PcDmCorrectionPatch(
            name = name,
            status = status,
            strength = parse("FUE", strength),
            dexterity = parse("DES", dexterity),
            constitution = parse("CON", constitution),
            intelligence = parse("INT", intelligence),
            wisdom = parse("SAB", wisdom),
            charisma = parse("CAR", charisma),
            armorClass = parse("CA", armorClass),
            maxHp = parse("PG máximos", maxHp),
            currentHp = parse("PG actuales", currentHp),
            tempHp = parse("PG temporales", tempHp),
            initiativeAdjustment = parse("Ajuste de iniciativa", initiativeAdjustment),
            speed = parse("Velocidad", speed),
            proficiencyBonusAdjustment = parse("Ajuste de competencia", proficiencyBonusAdjustment),
            passivePerceptionAdjustment = parse("Ajuste de Percepción Pasiva", passivePerceptionAdjustment),
            inspiration = inspiration,
            generalNotes = generalNotes,
        )
    }

    companion object {
        fun from(sheet: CharacterSheet): PcCorrectionDraft = PcCorrectionDraft(
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
            inspiration = sheet.inspiration,
            generalNotes = sheet.generalNotes,
        )
    }
}

internal fun pcAccountLabel(account: AccountIdentity?, id: Uuid?): String =
    account?.displayName?.trim()?.takeIf { it.isNotEmpty() }
        ?: id?.let { "Cuenta " + it.toString().take(8) + "…" }
        ?: "Sin asignar"

internal fun pcStatusLabel(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

internal fun formatPcEpoch(epochSeconds: Long): String =
    runCatching {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .format(Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()))
    }.getOrElse { epochSeconds.toString() }

internal fun signed(value: Int): String = if (value >= 0) "+" + value else value.toString()

internal fun <T> listSummary(items: List<T>, render: (T) -> String): String =
    items.joinToString(" · ", transform = render).ifBlank { "—" }
