package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContent
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContent
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContent
import io.github.mrsimkin.dndcustomaid.shared.content.SceneSection
import java.io.File

/**
 * «Guardar PDF…» on a table card (owner, 2026-11-18, «J»). The same words the card shows, on one
 * sheet, so the table can run on paper.
 */
@Composable
internal fun TableCardPdfButton(
    card: TableCard,
    onMessage: (String) -> Unit,
    chooseFile: (String) -> File? = { suggested ->
        chooseFileWithExplorer(
            title = "Guardar la ficha en PDF",
            purpose = "fichas",
            campaignKey = null,
            extensions = listOf("pdf"),
            save = true,
            suggestedName = suggested,
        )
    },
) {
    TextButton(onClick = {
        val name = "ficha-" + card.title.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "sin-nombre" } + ".pdf"
        val file = chooseFile(name) ?: return@TextButton
        val outcome = runCatching { writeTableCardPdf(card, file) }
        onMessage(
            outcome.fold(
                onSuccess = { "Ficha guardada en ${file.name}." },
                onFailure = { "No se pudo guardar la ficha: ${it.message}" },
            ),
        )
    }) { Text("Guardar PDF…") }
}

/**
 * «Imprimir todo…» (owner's J, 2026-11-20): the scene or the place and everything that runs with
 * it, one card per sheet, in one PDF.
 */
@Composable
internal fun TableCardsPdfButton(
    name: String,
    what: String,
    cards: () -> List<TableCard>,
    onMessage: (String) -> Unit,
    chooseFile: (String) -> File? = { suggested ->
        chooseFileWithExplorer(
            title = "Guardar las fichas en PDF",
            purpose = "fichas",
            campaignKey = null,
            extensions = listOf("pdf"),
            save = true,
            suggestedName = suggested,
        )
    },
) {
    TextButton(onClick = {
        val all = cards()
        if (all.size <= 1) {
            onMessage("«$name» no tiene $what para imprimir: usa «Guardar PDF…».")
            return@TextButton
        }
        val file = chooseFile(
            "fichas-" + name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "sin-nombre" } + ".pdf",
        ) ?: return@TextButton
        val outcome = runCatching { writeTableCardsPdf(all, file) }
        onMessage(
            outcome.fold(
                onSuccess = { "${all.size} fichas guardadas en ${file.name}, una por hoja." },
                onFailure = { "No se pudieron guardar las fichas: ${it.message}" },
            ),
        )
    }) { Text("Imprimir todo…") }
}

internal fun npcTableCard(content: NpcContent, origin: String): TableCard {
    val payload = content.payload
    val combat = payload.combatMechanics
    return TableCard(
        title = content.item.displayName,
        subtitle = listOfNotNull("PNJ", payload.conceptRole.takeIf { it.isNotBlank() }, origin.takeIf { it.isNotBlank() }?.let { "en $it" })
            .joinToString(" · "),
        blocks = listOf(
            "Primera impresión" to payload.appearanceFirstImpression,
            "Manera" to payload.personalityManner,
            "Quiere, teme, necesita" to payload.wantsFearsNeeds,
            "Puede ofrecer" to payload.canOffer,
            "No hará" to payload.limitsRefusals,
            "Relación con el grupo" to payload.relationshipContext,
            "Secretos" to payload.secrets,
            "Guía" to payload.dmGuidance,
            "Notas" to payload.notes,
        ),
        stat = combat?.let { mechanics ->
            TableCardStatBlock(
                line = listOf(mechanics.size, mechanics.creatureType, mechanics.alignment).filter { it.isNotBlank() }.joinToString(", "),
                armorClass = listOfNotNull(mechanics.armorClass?.toString(), mechanics.armorClassDetails.takeIf { it.isNotBlank() })
                    .joinToString(" "),
                hitPoints = listOfNotNull(mechanics.hitPoints?.toString(), mechanics.hitDice.takeIf { it.isNotBlank() }?.let { "($it)" })
                    .joinToString(" "),
                speed = mechanics.speed,
                abilities = mechanics.abilityScores,
                savingThrows = mechanics.savingThrows,
                skills = mechanics.skills,
                senses = mechanics.senses,
                languages = mechanics.languages,
                challenge = mechanics.challengeRating,
                traits = mechanics.traits,
                actions = mechanics.actions,
                reactions = mechanics.reactions,
            )
        },
    )
}

/** One item per line, the way the card shows them. */
private val SEP = System.lineSeparator().let { "\n" }

internal fun placeTableCard(content: PlaceContent): TableCard = TableCard(
    title = content.item.displayName,
    subtitle = listOfNotNull("Lugar", content.payload.area.takeIf { it.isNotBlank() }).joinToString(" · "),
    blocks = listOf(
        "Qué es" to content.payload.summary,
        "Al llegar" to content.payload.presentation,
        "Función" to content.payload.function,
        "Servicios" to content.payload.services.joinToString(SEP),
        "Interacciones" to content.payload.interactives.joinToString(SEP),
        "Ganchos" to content.payload.hooks.joinToString(SEP),
        "Se puede leer en voz alta" to content.payload.playerSafeText,
        "Solo para ti" to content.payload.dmNotes,
    ),
)

internal fun sceneTableCard(content: SceneContent): TableCard = TableCard(
    title = content.item.displayName,
    subtitle = "Escena",
    blocks = buildList {
        add("Para qué sirve" to content.payload.purpose)
        SceneSection.entries.forEach { section ->
            add(section.label to content.payload.sections[section.name].orEmpty())
        }
    },
)

internal fun creatureTableCard(content: CreatureContent): TableCard {
    val payload = content.payload
    return TableCard(
        title = content.item.displayName,
        subtitle = listOfNotNull("Criatura", payload.challengeRating.takeIf { it.isNotBlank() }?.let { "VD $it" })
            .joinToString(" · "),
        blocks = listOf(
            "Táctica" to payload.tactics,
            "Notas" to payload.notes,
        ),
        stat = TableCardStatBlock(
            line = listOf(payload.size, payload.creatureType, payload.alignment).filter { it.isNotBlank() }.joinToString(", "),
            armorClass = listOfNotNull(payload.armorClass?.toString(), payload.armorClassDetails.takeIf { it.isNotBlank() })
                .joinToString(" "),
            hitPoints = listOfNotNull(payload.hitPoints?.toString(), payload.hitDice.takeIf { it.isNotBlank() }?.let { "($it)" })
                .joinToString(" "),
            speed = payload.speed,
            abilities = payload.abilityScores,
            savingThrows = payload.savingThrows,
            skills = payload.skills,
            senses = payload.senses,
            languages = payload.languages,
            challenge = payload.challengeRating,
            traits = payload.traits,
            actions = payload.actions,
            reactions = payload.reactions,
        ),
    )
}
