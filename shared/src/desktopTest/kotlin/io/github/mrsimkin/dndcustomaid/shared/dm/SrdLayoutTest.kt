package io.github.mrsimkin.dndcustomaid.shared.dm

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** QA 0.7.0-preqa.2 (A5, L1, L2): the SRD is shown with its headings, tables and labels. */
class SrdLayoutTest {
    private fun layout(name: String) = parseSrdLayout(File("../androidApp/src/main/assets/srd/$name").readText())

    @Test
    fun markdownTurnsIntoHeadingsTablesBulletsAndBoldLabels() {
        val blocks = srdBlocks(
            """
            ## Rasgos de bárbaro

            | Nivel | Bonificador por competencia | Rasgos de clase |
            |---|---|---|
            | 1 | +2 | Defensa sin armadura, Furia |

            • Obtienes los rasgos de nivel 1.

            **Alcance:** 45 m
            """.trimIndent(),
        )
        assertEquals(SrdBlock.Heading(2, "Rasgos de bárbaro"), blocks[0])
        val table = blocks[1] as SrdBlock.Table
        assertEquals(listOf("Nivel", "Bonificador por competencia", "Rasgos de clase"), table.header)
        assertEquals(listOf(listOf("1", "+2", "Defensa sin armadura, Furia")), table.rows)
        assertTrue(blocks[2] is SrdBlock.Bullet)
        assertEquals(listOf(SrdSpan("Alcance:", bold = true), SrdSpan(" 45 m")), (blocks[3] as SrdBlock.Paragraph).spans)
    }

    @Test
    fun aSentenceBrokenByThePdfLayoutReadsAsOneParagraph() {
        val blocks = srdBlocks(
            "**Resistencia al daño.** Tienes resistencia al daño\n\ncontundente, cortante y perforante.\n\n" +
                "**Sin concentración ni conjuros.** No puedes mante‑\n\nner la concentración.\n\nUna frase nueva.",
        )
        assertEquals(3, blocks.size, blocks.toString())
        val first = (blocks[0] as SrdBlock.Paragraph).spans.joinToString("") { it.text }
        assertEquals("Resistencia al daño. Tienes resistencia al daño contundente, cortante y perforante.", first)
        assertEquals("Sin concentración ni conjuros. No puedes mantener la concentración.", (blocks[1] as SrdBlock.Paragraph).spans.joinToString("") { it.text })
        assertEquals(SrdSpan("Resistencia al daño.", bold = true), (blocks[0] as SrdBlock.Paragraph).spans.first())
    }

    @Test
    fun anEntryStopsWhereTheNextEntryStarts() {
        for (file in SRD_LAYOUT_FILES) {
            val fireball = assertNotNull(layout(file).entry("SPELL", "Bola de fuego"), file)
            assertTrue("explosión retardada" !in fireball, "$file: «Bola de fuego» carried the next spell")
            assertTrue("8d6" in fireball, file)
        }
    }

    @Test
    fun theShippedSrdHasTheBarbarianLevelTableAsATableInBothEditions() {
        for (file in SRD_LAYOUT_FILES) {
            val srd = layout(file)
            val barbarian = assertNotNull(srd.entry("CLASS", "Bárbaro"), "$file has the Bárbaro entry")
            // SRD 5.1 prints the table in two columns, so it may come as two tables: together they have every level.
            val levels = srdBlocks(barbarian).filterIsInstance<SrdBlock.Table>().flatMap { it.rows }.mapNotNull { it.firstOrNull()?.toIntOrNull() }.toSet()
            assertEquals((1..20).toSet(), levels, "$file: one row per level")
            assertTrue(srd.pages.size > 300, "$file keeps every page")
        }
        val fireball = assertNotNull(layout("srd_5_2_1_es.layout.json").entry("SPELL", "Bola de fuego"))
        assertTrue(fireball.contains("**Alcance:** 45"), fireball.take(300))
    }
}
