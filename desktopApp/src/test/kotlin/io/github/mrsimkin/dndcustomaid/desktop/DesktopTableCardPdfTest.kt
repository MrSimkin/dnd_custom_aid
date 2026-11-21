package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner, 2026-11-18 («J»): the table card printed on one sheet, «pero necesito formato para el
 * stat-block si aplica». The test reads the PDF back and checks the words and their order.
 */
class DesktopTableCardPdfTest {
    private fun textOf(card: TableCard): String {
        val file = Files.createTempFile("dnd-custom-aid-card-", ".pdf").toFile()
        writeTableCardPdf(card, file)
        val text = Loader.loadPDF(file).use { PDFTextStripper().getText(it) }
        file.delete()
        return text
    }

    @Test
    fun aPlainCardPrintsItsBlocksInOrder() {
        val text = textOf(
            TableCard(
                title = "Barek Surn",
                subtitle = "PNJ · Capataz · en Punta y Compás",
                blocks = listOf(
                    "Primera impresión" to "Hombre ancho, manos quemadas.",
                    "Vacío" to "",
                    "Secretos" to "Falsea el conteo desde hace dos meses.",
                ),
            ),
        )

        assertTrue(text.contains("Barek Surn"), text)
        assertTrue(text.contains("PNJ · Capataz · en Punta y Compás") || text.contains("PNJ"), text)
        assertTrue(text.indexOf("PRIMERA IMPRESIÓN") < text.indexOf("SECRETOS"), text)
        assertTrue(!text.contains("VACÍO"), "an empty block is not printed")
    }

    @Test
    fun aCreaturePrintsItsStatBlockLikeTheSrd() {
        val text = textOf(
            TableCard(
                title = "Obrero alterado",
                subtitle = "Criatura · VD 1/2",
                blocks = listOf("Táctica" to "Empuja hacia el borde de carga."),
                stat = TableCardStatBlock(
                    line = "Mediano humanoide, neutral",
                    armorClass = "14 (cuero tachonado)",
                    hitPoints = "31 (7d8)",
                    speed = "9 m",
                    abilities = CreatureAbilityScores(16, 12, 14, 8, 10, 7),
                    savingThrows = "FUE +5",
                    skills = "Atletismo +5",
                    senses = "Percepción pasiva 10",
                    languages = "común",
                    challenge = "1/2 (100 PX)",
                    traits = "Hábito de turno. Nunca suelta la carga.",
                    actions = "Gancho. Ataque con arma cuerpo a cuerpo: +5, 1d8 + 3 perforante.",
                ),
            ),
        )

        assertTrue(text.contains("BLOQUE DE COMBATE"), text)
        assertTrue(text.contains("Mediano humanoide, neutral"), text)
        assertTrue(text.contains("Clase de Armadura  14 (cuero tachonado)"), text)
        assertTrue(text.contains("Puntos de golpe  31 (7d8)"), text)
        assertTrue(text.contains("FUE 16 (+3)") && text.contains("CAR 7 (-2)"), text)
        assertTrue(text.indexOf("RASGOS") < text.indexOf("ACCIONES"), text)
        assertTrue(text.contains("Valor de desafío  1/2 (100 PX)"), text)
    }

    @Test
    fun aLongCardKeepsGoingOnMoreSheets() {
        val card = TableCard(
            title = "Escena larga",
            subtitle = "Escena",
            blocks = List(12) { index -> "Parte $index" to ("Texto de la parte $index. " + "palabra ".repeat(120)) },
        )
        val file = Files.createTempFile("dnd-custom-aid-long-", ".pdf").toFile()
        writeTableCardPdf(card, file)
        val pages = Loader.loadPDF(file).use { it.numberOfPages }
        file.delete()

        assertTrue(pages > 1, "a long card does not get cut: $pages pages")
    }

    @Test
    fun whatTheDmPastedFromAPdfStillPrints() {
        val text = textOf(
            TableCard(
                title = "Liora Vael",
                subtitle = "PNJ",
                blocks = listOf("Notas" to "Dijo “no” —con calma— y se fue…"),
            ),
        )
        assertTrue(text.contains("Dijo \"no\" -con calma- y se fue..."), text)
        assertEquals(true, text.isNotBlank())
    }
}
