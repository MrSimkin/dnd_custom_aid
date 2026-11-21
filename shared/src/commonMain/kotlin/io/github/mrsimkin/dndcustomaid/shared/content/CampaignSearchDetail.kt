package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/*
 * Owner's I (2026-11-20): the search now also works on the tablet, and there a result must be
 * readable where it is found. On the PC a result jumps to its section of «Preparar»; the tablet has
 * no «Preparar», so the search itself shows what the object says.
 *
 * This is the reading sheet of a result: the same labels the DM sees on the PC, in the order they
 * are read at the table, and never an empty line. It lives in `shared` so both Apps read the same
 * thing and so it can be tested without a screen.
 */

/** One labelled line of a result's reading sheet. */
data class CampaignSearchDetailLine(val label: String, val text: String)

class CampaignSearchDetail(
    private val database: AppDatabase,
    private val content: ReusableContentRepository = ReusableContentRepository(database),
) {
    private val npcs = NpcContentRepository(database, content)
    private val places = PlaceContentRepository(database, content)
    private val scenes = SceneContentRepository(database, content)
    private val zones = ZoneContentRepository(database, content)
    private val dungeons = DungeonContentRepository(database, content)
    private val creatures = CreatureContentRepository(database, content)
    private val encounters = EncounterContentRepository(database, content)
    private val handouts = HandoutContentRepository(database, content)
    private val notes = NoteContentRepository(database, content)
    private val rules = HomebrewRuleContentRepository(database, content)

    /** What this object says, by family, with nothing empty in between. */
    fun lines(id: Uuid, family: ReusableContentFamily): List<CampaignSearchDetailLine> = when (family) {
        ReusableContentFamily.NPC -> npcs.npc(id)?.payload?.let { payload ->
            listOf(
                "Papel" to payload.conceptRole,
                "Cómo se ve" to payload.appearanceFirstImpression,
                "Cómo se comporta" to payload.personalityManner,
                "Quiere, teme, necesita" to payload.wantsFearsNeeds,
                "Puede ofrecer" to payload.canOffer,
                "No va a hacer" to payload.limitsRefusals,
                "Voz y gestos" to payload.voiceMannerisms,
                "Secretos (solo DM)" to payload.secrets,
                "Lo que sabe" to payload.knowledge,
                "Notas" to payload.notes,
            )
        }
        ReusableContentFamily.PLACE -> places.place(id)?.payload?.let { payload ->
            listOf(
                "Qué es" to payload.summary,
                "Área" to payload.area,
                "Para qué sirve" to payload.function,
                "Cómo presentarlo" to payload.presentation,
                "Servicios" to payload.services.joinToString(" · "),
                "Qué se puede tocar" to payload.interactives.joinToString(" · "),
                "Ganchos" to payload.hooks.joinToString(" · "),
                "Para los jugadores" to payload.playerSafeText,
                "Notas del DM" to payload.dmNotes,
            )
        }
        ReusableContentFamily.SCENE -> scenes.scene(id)?.payload?.let { payload ->
            listOf("Para qué es la escena" to payload.purpose) +
                SceneSection.entries.map { section -> section.label to payload.section(section) } +
                listOf(
                    "Puede seguir en" to payload.possibleNextScenes.joinToString(" · "),
                    "Notas" to payload.notes,
                )
        }
        ReusableContentFamily.ZONE -> zones.zone(id)?.payload?.let { payload ->
            listOf(
                "Qué es" to payload.summary,
                "Área" to payload.area,
                "Cómo presentarla" to payload.presentation,
                "El espacio" to payload.space,
                "Explorar" to payload.exploration,
                "Qué se puede tocar" to payload.interactives.joinToString(" · "),
                "Pistas" to payload.clues.joinToString(" · "),
                "Tiradas" to payload.checks.joinToString(" · "),
                "Consecuencias" to payload.consequences.joinToString(" · "),
                "Encuentro previsto" to payload.encounterBrief,
                "Para el DM" to payload.dmGuidance,
                "Para los jugadores" to payload.playerSafeText,
            )
        }
        ReusableContentFamily.DUNGEON -> dungeons.dungeon(id)?.payload?.let { payload ->
            listOf(
                "Qué es" to payload.summary,
                "Para los jugadores" to payload.playerSafeText,
            )
        }
        ReusableContentFamily.CREATURE -> creatures.creature(id)?.payload?.let { payload ->
            listOf(
                "Qué es" to listOf(payload.size, payload.creatureType, payload.alignment)
                    .filter { it.isNotBlank() }.joinToString(", "),
                "Defensa" to listOfNotNull(
                    payload.armorClass?.let { "CA $it" + payload.armorClassDetails.takeIf { d -> d.isNotBlank() }?.let { d -> " ($d)" }.orEmpty() },
                    payload.hitPoints?.let { "PV $it" + payload.hitDice.takeIf { d -> d.isNotBlank() }?.let { d -> " ($d)" }.orEmpty() },
                ).joinToString(" · "),
                "Velocidad" to payload.speed,
                "Características" to payload.abilityScores.let { scores ->
                    "FUE ${scores.strength} · DES ${scores.dexterity} · CON ${scores.constitution} · " +
                        "INT ${scores.intelligence} · SAB ${scores.wisdom} · CAR ${scores.charisma}"
                },
                "Salvaciones" to payload.savingThrows,
                "Habilidades" to payload.skills,
                "Resistencias" to payload.damageResistances,
                "Inmunidades" to listOf(payload.damageImmunities, payload.conditionImmunities)
                    .filter { it.isNotBlank() }.joinToString(" · "),
                "Sentidos" to payload.senses,
                "Idiomas" to payload.languages,
                "Valor de desafío" to payload.challengeRating,
                "Rasgos" to payload.traits,
                "Acciones" to payload.actions,
                "Acciones adicionales" to payload.bonusActions,
                "Reacciones" to payload.reactions,
                "Acciones legendarias" to payload.legendaryActions,
            )
        }
        ReusableContentFamily.ENCOUNTER -> encounters.encounter(id)?.let { encounter ->
            val payload = encounter.payload
            listOf(
                "Qué es" to payload.summary,
                "Dónde" to payload.environment,
                "Situación" to payload.context,
                "Quiénes" to payload.participants.joinToString(" · ") { participant ->
                    val name = participant.label.ifBlank {
                        participant.sourceContentId?.let { creatures.creature(it)?.item?.displayName } ?: "Criatura"
                    }
                    if (participant.quantity > 1) "$name ×${participant.quantity}" else name
                },
                "Para el DM" to payload.dmGuidance,
                "Notas" to payload.notes,
            )
        }
        ReusableContentFamily.HANDOUT -> handouts.handout(id)?.payload?.let { payload ->
            listOf(
                "Qué es" to payload.caption,
                "Carpeta" to payload.folder,
                "Quién puede verlo" to if (payload.visibility == HandoutVisibility.PLAYER_SAFE) {
                    if (payload.sharedWithPlayers) "Los jugadores ya lo tienen" else "Se puede enviar a los jugadores"
                } else {
                    "Solo el DM"
                },
                "Notas" to payload.notes,
            )
        }
        ReusableContentFamily.NOTE -> notes.note(id)?.payload?.let { payload ->
            listOf(
                "Nota" to payload.text,
                "Escrita en" to payload.contextLabel,
            )
        }
        ReusableContentFamily.HOMEBREW_RULE -> rules.rule(id)?.payload?.let { payload ->
            listOf(
                "En una línea" to payload.summary,
                "La regla" to payload.body,
                "Categoría" to payload.category,
                "Por qué" to payload.rationale,
                "Ejemplos" to payload.examples.joinToString(" · "),
                "Notas" to payload.notes,
            )
        }
        else -> null
    }.orEmpty().filter { (_, text) -> text.isNotBlank() }.map { (label, text) -> CampaignSearchDetailLine(label, text) }
}
