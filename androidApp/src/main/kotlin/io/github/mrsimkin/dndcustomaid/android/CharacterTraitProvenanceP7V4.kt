package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProvenanceKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitProvenance
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.isStructuredCharacterProvenance
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal data class CharacterTraitProvenanceOptionP7V4(
    val kind: CharacterProvenanceKind,
    val targetId: Uuid,
    val label: String,
    val parentLabel: String? = null,
) {
    val displayLabel: String
        get() = parentLabel?.takeIf { it.isNotBlank() }?.let { "$label ($it)" } ?: label
}

internal fun characterTraitProvenanceToJsonP7V4(
    items: List<CharacterTraitProvenance>,
): String = JSONArray().apply {
    items.forEach { item ->
        put(
            JSONObject().apply {
                put("traitId", item.traitId.toString())
                put("kind", item.kind.name)
                put("targetId", item.targetId?.toString() ?: JSONObject.NULL)
                put("freeText", item.freeText ?: JSONObject.NULL)
                put("legacyText", item.legacyText ?: JSONObject.NULL)
            },
        )
    }
}.toString()

internal fun characterTraitProvenanceFromJsonP7V4(
    raw: String,
): List<CharacterTraitProvenance> = runCatching {
    val array = JSONArray(raw)
    buildList {
        repeat(array.length()) { index ->
            val item = array.getJSONObject(index)
            val traitId = Uuid.parse(item.getString("traitId"))
            val kind = runCatching { CharacterProvenanceKind.valueOf(item.getString("kind")) }
                .getOrDefault(CharacterProvenanceKind.OTHER)
            add(
                CharacterTraitProvenance(
                    traitId = traitId,
                    kind = kind,
                    targetId = item.optNullableStringP7V4("targetId")?.let(Uuid::parse),
                    freeText = item.optNullableStringP7V4("freeText"),
                    legacyText = item.optNullableStringP7V4("legacyText"),
                ),
            )
        }
    }
}.getOrDefault(emptyList())

private fun JSONObject.optNullableStringP7V4(key: String): String? =
    if (!has(key) || isNull(key)) null else optString(key).takeIf { it.isNotBlank() }

internal fun traitTypeForProvenanceKindP7V4(kind: CharacterProvenanceKind): CharacterTraitType = when (kind) {
    CharacterProvenanceKind.CLASS,
    CharacterProvenanceKind.SUBCLASS,
    -> CharacterTraitType.CLASS
    CharacterProvenanceKind.SPECIES_RACE,
    CharacterProvenanceKind.SUBRACE,
    -> CharacterTraitType.SPECIES_RACE
    CharacterProvenanceKind.BACKGROUND -> CharacterTraitType.BACKGROUND
    CharacterProvenanceKind.FEAT -> CharacterTraitType.FEAT
    CharacterProvenanceKind.GIFT_BLESSING -> CharacterTraitType.GIFT_BLESSING
    CharacterProvenanceKind.OTHER -> CharacterTraitType.OTHER
}

private fun defaultProvenanceKindP7V4(type: CharacterTraitType): CharacterProvenanceKind = when (type) {
    CharacterTraitType.CLASS -> CharacterProvenanceKind.CLASS
    CharacterTraitType.SPECIES_RACE -> CharacterProvenanceKind.SPECIES_RACE
    CharacterTraitType.BACKGROUND -> CharacterProvenanceKind.BACKGROUND
    CharacterTraitType.FEAT -> CharacterProvenanceKind.FEAT
    CharacterTraitType.GIFT_BLESSING -> CharacterProvenanceKind.GIFT_BLESSING
    CharacterTraitType.OTHER -> CharacterProvenanceKind.OTHER
}

internal fun traitProvenanceKindLabelP7V4(kind: CharacterProvenanceKind): String = when (kind) {
    CharacterProvenanceKind.CLASS -> "Clase"
    CharacterProvenanceKind.SUBCLASS -> "Subclase"
    CharacterProvenanceKind.SPECIES_RACE -> "Raza"
    CharacterProvenanceKind.SUBRACE -> "Subraza"
    CharacterProvenanceKind.BACKGROUND -> "Trasfondo"
    CharacterProvenanceKind.FEAT -> "Dote"
    CharacterProvenanceKind.GIFT_BLESSING -> "Don / bendición"
    CharacterProvenanceKind.OTHER -> "Otro"
}

internal fun traitProvenanceOptionsP7V4(
    kind: CharacterProvenanceKind,
    classes: List<CharacterClassLevel>,
    successorState: CharacterSuccessorState,
): List<CharacterTraitProvenanceOptionP7V4> = when (kind) {
    CharacterProvenanceKind.CLASS -> classes
        .sortedBy { it.sortOrder }
        .mapNotNull { classLevel ->
            classLevel.name.trim().takeIf { it.isNotEmpty() }?.let { name ->
                CharacterTraitProvenanceOptionP7V4(kind, classLevel.id, name)
            }
        }
    CharacterProvenanceKind.SUBCLASS -> successorState.subclassIdentities
        .mapNotNull { identity ->
            val parent = classes.firstOrNull { it.id == identity.parentClassId } ?: return@mapNotNull null
            val subclassName = parent.subclassName?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            CharacterTraitProvenanceOptionP7V4(
                kind = kind,
                targetId = identity.id,
                label = subclassName,
                parentLabel = parent.name.trim().takeIf { it.isNotEmpty() },
            )
        }
        .sortedBy { it.displayLabel.lowercase() }
    CharacterProvenanceKind.SPECIES_RACE -> successorState.speciesIdentity
        ?.takeIf { it.name.isNotBlank() }
        ?.let { listOf(CharacterTraitProvenanceOptionP7V4(kind, it.id, it.name.trim())) }
        .orEmpty()
    CharacterProvenanceKind.SUBRACE -> successorState.subraceIdentity
        ?.takeIf { child ->
            successorState.speciesIdentity?.takeIf { it.name.isNotBlank() }?.id == child.parentSpeciesId &&
                child.name.isNotBlank()
        }
        ?.let { child ->
            listOf(
                CharacterTraitProvenanceOptionP7V4(
                    kind = kind,
                    targetId = child.id,
                    label = child.name.trim(),
                    parentLabel = successorState.speciesIdentity?.name?.trim()?.takeIf { it.isNotEmpty() },
                ),
            )
        }
        .orEmpty()
    CharacterProvenanceKind.BACKGROUND -> successorState.backgroundIdentity
        ?.takeIf { it.name.isNotBlank() }
        ?.let { listOf(CharacterTraitProvenanceOptionP7V4(kind, it.id, it.name.trim())) }
        .orEmpty()
    CharacterProvenanceKind.FEAT,
    CharacterProvenanceKind.GIFT_BLESSING,
    CharacterProvenanceKind.OTHER,
    -> emptyList()
}

internal fun newTraitProvenanceForKindP7V4(
    traitId: Uuid,
    kind: CharacterProvenanceKind,
    classes: List<CharacterClassLevel>,
    successorState: CharacterSuccessorState,
): CharacterTraitProvenance {
    val soleOption = if (kind.isStructuredCharacterProvenance()) {
        traitProvenanceOptionsP7V4(kind, classes, successorState).singleOrNull()
    } else {
        null
    }
    return CharacterTraitProvenance(
        traitId = traitId,
        kind = kind,
        targetId = soleOption?.targetId,
        legacyText = soleOption?.displayLabel,
    )
}

internal fun traitProvenanceDraftP7V4(
    trait: CharacterTrait,
    existing: CharacterTraitProvenance?,
    classes: List<CharacterClassLevel>,
    successorState: CharacterSuccessorState,
): CharacterTraitProvenance {
    if (existing != null) return existing.copy(traitId = trait.id)

    val legacy = trait.source.trim().takeIf { it.isNotEmpty() }
    val defaultKind = defaultProvenanceKindP7V4(trait.type)
    if (!defaultKind.isStructuredCharacterProvenance()) {
        return CharacterTraitProvenance(
            traitId = trait.id,
            kind = defaultKind,
            freeText = legacy,
            legacyText = legacy,
        )
    }

    val candidateKinds = when (defaultKind) {
        CharacterProvenanceKind.CLASS -> listOf(CharacterProvenanceKind.CLASS, CharacterProvenanceKind.SUBCLASS)
        CharacterProvenanceKind.SPECIES_RACE -> listOf(CharacterProvenanceKind.SPECIES_RACE, CharacterProvenanceKind.SUBRACE)
        else -> listOf(defaultKind)
    }
    val matched = legacy?.let { text ->
        candidateKinds
            .flatMap { traitProvenanceOptionsP7V4(it, classes, successorState) }
            .filter { option ->
                option.label.equals(text, ignoreCase = true) ||
                    option.displayLabel.equals(text, ignoreCase = true)
            }
            .singleOrNull()
    }
    return CharacterTraitProvenance(
        traitId = trait.id,
        kind = matched?.kind ?: defaultKind,
        targetId = matched?.targetId,
        legacyText = matched?.displayLabel ?: legacy,
    )
}

internal fun normalizeTraitProvenanceP7V4(
    provenance: CharacterTraitProvenance,
): CharacterTraitProvenance = if (provenance.kind.isStructuredCharacterProvenance()) {
    provenance.copy(
        freeText = null,
        legacyText = provenance.legacyText?.trim()?.takeIf { it.isNotEmpty() },
    )
} else {
    val text = provenance.freeText?.trim()?.takeIf { it.isNotEmpty() }
    provenance.copy(
        targetId = null,
        freeText = text,
        legacyText = provenance.legacyText?.trim()?.takeIf { it.isNotEmpty() } ?: text,
    )
}

internal fun refreshResolvedTraitProvenanceLabelsP7V4(
    items: List<CharacterTraitProvenance>,
    classes: List<CharacterClassLevel>,
    successorState: CharacterSuccessorState,
): List<CharacterTraitProvenance> = items.map { item ->
    if (!item.kind.isStructuredCharacterProvenance()) {
        normalizeTraitProvenanceP7V4(item)
    } else {
        val option = item.targetId?.let { targetId ->
            traitProvenanceOptionsP7V4(item.kind, classes, successorState)
                .firstOrNull { it.targetId == targetId }
        }
        normalizeTraitProvenanceP7V4(
            if (option != null) item.copy(legacyText = option.displayLabel) else item,
        )
    }
}

internal fun traitProvenanceIsValidP7V4(
    provenance: CharacterTraitProvenance,
    classes: List<CharacterClassLevel>,
    successorState: CharacterSuccessorState,
): Boolean {
    if (!provenance.kind.isStructuredCharacterProvenance()) return true
    val options = traitProvenanceOptionsP7V4(provenance.kind, classes, successorState)
    if (provenance.targetId != null && options.any { it.targetId == provenance.targetId }) return true
    return !provenance.legacyText.isNullOrBlank()
}

internal fun traitProvenanceDisplaySourceP7V4(
    provenance: CharacterTraitProvenance,
    classes: List<CharacterClassLevel>,
    successorState: CharacterSuccessorState,
): String {
    if (!provenance.kind.isStructuredCharacterProvenance()) {
        return provenance.freeText?.trim().orEmpty()
    }
    val resolved = provenance.targetId?.let { targetId ->
        traitProvenanceOptionsP7V4(provenance.kind, classes, successorState)
            .firstOrNull { it.targetId == targetId }
    }
    return resolved?.displayLabel ?: provenance.legacyText?.trim().orEmpty()
}

@Composable
internal fun CharacterTraitProvenanceEditorP7V4(
    provenance: CharacterTraitProvenance,
    classes: List<CharacterClassLevel>,
    successorState: CharacterSuccessorState,
    onChange: (CharacterTraitProvenance) -> Unit,
) {
    var typeMenuOpen by remember { mutableStateOf(false) }
    var sourceMenuOpen by remember { mutableStateOf(false) }
    val options = traitProvenanceOptionsP7V4(provenance.kind, classes, successorState)
    val selected = provenance.targetId?.let { id -> options.firstOrNull { it.targetId == id } }
    val unresolved = provenance.kind.isStructuredCharacterProvenance() &&
        selected == null && !provenance.legacyText.isNullOrBlank()

    Column {
        Text("Tipo de origen", style = MaterialTheme.typography.labelSmall)
        androidx.compose.foundation.layout.Box {
            OutlinedButton(
                onClick = { typeMenuOpen = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(traitProvenanceKindLabelP7V4(provenance.kind))
            }
            DropdownMenu(expanded = typeMenuOpen, onDismissRequest = { typeMenuOpen = false }) {
                CharacterProvenanceKind.entries.forEach { kind ->
                    DropdownMenuItem(
                        text = { Text(traitProvenanceKindLabelP7V4(kind)) },
                        onClick = {
                            onChange(
                                newTraitProvenanceForKindP7V4(
                                    traitId = provenance.traitId,
                                    kind = kind,
                                    classes = classes,
                                    successorState = successorState,
                                ),
                            )
                            typeMenuOpen = false
                        },
                    )
                }
            }
        }

        if (provenance.kind.isStructuredCharacterProvenance()) {
            Text("Origen específico", style = MaterialTheme.typography.labelSmall)
            androidx.compose.foundation.layout.Box {
                OutlinedButton(
                    onClick = { sourceMenuOpen = true },
                    enabled = options.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        selected?.displayLabel
                            ?: provenance.legacyText?.takeIf { unresolved }
                                ?.let { "No disponible · $it" }
                            ?: if (options.isEmpty()) "Sin orígenes configurados" else "Seleccionar origen",
                    )
                }
                DropdownMenu(expanded = sourceMenuOpen, onDismissRequest = { sourceMenuOpen = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayLabel) },
                            onClick = {
                                onChange(
                                    provenance.copy(
                                        kind = option.kind,
                                        targetId = option.targetId,
                                        freeText = null,
                                        legacyText = option.displayLabel,
                                    ),
                                )
                                sourceMenuOpen = false
                            },
                        )
                    }
                }
            }
            if (options.isEmpty()) {
                Text(
                    "Configura primero este origen en los datos canónicos del personaje; no se crea una segunda copia de texto aquí.",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (unresolved) {
                Text(
                    "El origen anterior ya no pertenece al personaje. Puedes conservar la relación sin resolver o elegir uno de los orígenes actuales.",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        } else {
            OutlinedTextField(
                value = provenance.freeText.orEmpty(),
                onValueChange = { value ->
                    onChange(provenance.copy(targetId = null, freeText = value))
                },
                label = { Text("Origen específico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        CharacterHelpV4(
            "Clase, Subclase, Raza, Subraza y Trasfondo referencian únicamente orígenes que este personaje posee. Dote, Don / bendición y Otro pueden usar texto libre. La procedencia describe origen de juego, no libro o publicación.",
        )
    }
}
