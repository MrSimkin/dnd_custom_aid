package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

@Serializable
data class ScenePayload(
    val purpose: String = "",
    val possibleNextScenes: List<String> = emptyList(),
    val references: List<String> = emptyList(),
    val notes: String = "",
    /** QA 0.7.0-preqa.2 (R4): the scene's own sections, keyed by [SceneSection.name]. */
    val sections: Map<String, String> = emptyMap(),
) {
    fun section(section: SceneSection): String = sections[section.name].orEmpty()

    fun withSection(section: SceneSection, text: String): ScenePayload =
        copy(sections = if (text.isBlank()) sections - section.name else sections + (section.name to text))
}

/**
 * The parts of a prepared scene, in the order the DM reads them at the table. [forPlayers] parts
 * can be read aloud or shown; the rest are for the DM only.
 */
enum class SceneSection(val label: String, val forPlayers: Boolean = false) {
    READ_ALOUD_SHORT("Para leer en voz alta · 30 s", forPlayers = true),
    READ_ALOUD("Para leer en voz alta · 2 min", forPlayers = true),
    SENSES("Qué se ve, se oye y se huele", forPlayers = true),
    TONE("Tono y actitud"),
    TRUTH("Qué está pasando realmente"),
    NPCS("PNJ que conviene usar"),
    BEATS("Momentos de la escena"),
    EVIDENCE("Pruebas y pistas"),
    CHECKS("Pruebas de característica útiles"),
    SUCCESS("Qué descubren si les va bien"),
    MISTAKES("Errores probables"),
    FAILURE("Qué puede salir mal"),
    LOOT("Botín y hallazgos"),
    NEXT("Cómo conecta con lo siguiente"),
    ALTERNATIVES("Variantes y otros lugares"),
    DM_HELP("Ayuda al DM"),
}

@Serializable
data class SceneContent(
    val item: ReusableContentItem,
    val payload: ScenePayload,
) {
    init {
        require(item.family == ReusableContentFamily.SCENE) {
            "SceneContent must wrap SCENE reusable content."
        }
    }
}
