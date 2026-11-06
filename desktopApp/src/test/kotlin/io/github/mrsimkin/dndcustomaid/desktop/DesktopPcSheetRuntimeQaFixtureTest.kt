package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportPlanner
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper

class DesktopPcSheetRuntimeQaFixtureTest {
    @Test
    fun aldrenFantasySheetRendersWithoutUnroutedOverflow() {
        val document = fixture("01_aldren_vale_srd5_1_champion_fighter.json")
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = document.character,
                    closure = document.closureState,
                    successor = document.successorState,
                ),
            ),
        )

        val bytes = ByteArrayOutputStream().use { output ->
            DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
            output.toByteArray()
        }

        assertTrue(bytes.size > 20_000)

        Loader.loadPDF(bytes).use { pdf ->
            val extracted = PDFTextStripper().getText(pdf)
            val normalized = extracted.replace(Regex("\\s+"), " ")
            // Compact base previews may truncate, but the canonical detail must survive through
            // the Fantasy continuation/reference routing.
            assertTrue(normalized.contains("Dueling incluido; dos ataques con la acción Atacar."))
            assertTrue(normalized.contains("Munición; recarga."))
            assertTrue(normalized.contains("Recupera 1d10 + 5 PG"))
            assertTrue(normalized.contains("Una acción adicional este turno"))
            assertTrue(normalized.contains("Descanso corto/largo"))
            assertTrue(!normalized.contains("Disponible"))
            assertTrue(!normalized.contains("Gastado"))
            assertTrue(!normalized.contains("A máximo"))
            // Base Equipment owns quantity/weight. Special continuation owns location/state/detail
            // without replaying already-visible compact inventory facts.
            assertTrue(normalized.contains("Mano derecha"))
            assertTrue(normalized.contains("1d8 cortante; versátil 1d10."))
            assertTrue(normalized.contains("Arma marcial."))
        }
    }

    @Test
    fun aldrenCustomFamiliesDoNotReplayAmmunitionMetadataAsEquipmentOverflow() {
        val document = fixture("01_aldren_vale_srd5_1_champion_fighter.json")
        val families = listOf(
            PcSheetVisualFamily.CUSTOM_V1,
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        )

        families.forEach { family ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(
                    permanent = PcSheetExportAggregate(
                        sheet = document.character,
                        closure = document.closureState,
                        successor = document.successorState,
                    ),
                ),
            )
            val bytes = ByteArrayOutputStream().use { output ->
                DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
                output.toByteArray()
            }

            Loader.loadPDF(bytes).use { pdf ->
                val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
                assertEquals(
                    1,
                    Regex("\\bVirotes\\b").findAll(normalized).count(),
                    "$family must represent Virotes once, using native Equipment capacity",
                )
                assertTrue(
                    !normalized.contains("Estado: Munición"),
                    "$family must not allocate Equipment continuation solely for ammunition metadata",
                )
                if (family != PcSheetVisualFamily.CUSTOM_V1) {
                    assertTrue(normalized.contains("1 / 1"))
                    assertTrue(!normalized.contains("Disponible"))
                    assertTrue(!normalized.contains("Gastado"))
                }
            }
        }
    }

    @Test
    fun ilyraFantasySheetBoundsFeatPreviewAndPreservesFullContinuation() {
        val document = fixture("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = document.character,
                    closure = document.closureState,
                    successor = document.successorState,
                ),
            ),
        )

        val bytes = ByteArrayOutputStream().use { output ->
            DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
            output.toByteArray()
        }

        assertTrue(bytes.size > 20_000)
        Loader.loadPDF(bytes).use { pdf ->
            val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
            assertTrue(normalized.contains("Ability Score Improvement"))
            assertTrue(normalized.contains("puntuaciones finales INT 18 y DES 14"))
            assertTrue(normalized.contains("Memorize Spell"))
        }
    }

    @Test
    fun ilyraCustomV2FamiliesWrapCombatReferenceRowsWithoutExcessiveCompression() {
        val document = fixture("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val families = listOf(
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        )

        families.forEach { family ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = true,
                ),
                sources = PcSheetExportSources(
                    permanent = PcSheetExportAggregate(
                        sheet = document.character,
                        closure = document.closureState,
                        successor = document.successorState,
                    ),
                ),
            )

            val bytes = ByteArrayOutputStream().use { output ->
                DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
                output.toByteArray()
            }

            assertTrue(bytes.size > 20_000)
            Loader.loadPDF(bytes).use { pdf ->
                val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
                assertTrue(normalized.contains("Fire Bolt"))
                assertTrue(normalized.contains("Potent Cantrip de Evoker"))
                assertTrue(normalized.contains("SRD 5.2.1"))
                assertTrue(normalized.contains("Memorize Spell"))
                assertTrue(normalized.contains("Libro de 100 páginas"))
                assertTrue(normalized.contains("incluidos los añadidos por Evocation Savant"))
                assertTrue(normalized.contains("Contiene la selección legal de conjuros de Ilyra hasta nivel 5"))
            }
        }
    }

    @Test
    fun maraCustomV1GeneratesAndWrapsALongSkillLabelAtOneScale() {
        // M50800-02 (owner 2026-09-28; clarification B1-01, 2026-09-30): Mara must generate, a long
        // skill name may wrap, and every wrapped line of one label keeps one uniform scale.
        val document = fixture("03_mara_siete_umbrales_custom_extended.json")
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = document.character,
                    closure = document.closureState,
                    successor = document.successorState,
                ),
            ),
        )
        val bytes = ByteArrayOutputStream().use { output ->
            DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
            output.toByteArray()
        }
        Loader.loadPDF(bytes).use { pdf ->
            val runs = mutableListOf<List<org.apache.pdfbox.text.TextPosition>>()
            object : PDFTextStripper() {
                override fun writeString(
                    text: String,
                    textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                ) {
                    runs += textPositions.toList()
                }
            }.getText(pdf)
            fun text(run: List<org.apache.pdfbox.text.TextPosition>) = run.joinToString("") { it.unicode }
            val first = runs.first { text(it).trim() == "Lectura de" }
            val second = runs.first { text(it).trim() == "presagios" }
            fun widthOf(run: List<org.apache.pdfbox.text.TextPosition>, letter: String) =
                run.first { it.unicode == letter }.widthDirAdj
            assertTrue(kotlin.math.abs(widthOf(first, "e") - widthOf(second, "e")) < 0.01f,
                "Both lines of one label keep one horizontal scale.")
            assertTrue(kotlin.math.abs(widthOf(first, "a") - widthOf(second, "a")) < 0.01f,
                "Both lines of one label keep one horizontal scale.")
            assertTrue(kotlin.math.abs(first.first().xDirAdj - second.first().xDirAdj) < 0.01f,
                "Both lines start at the same left edge.")
            // A long combat note wraps whole, at one scale, inside one page.
            val combatPages = (1..pdf.numberOfPages).filter { n ->
                PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf).contains("Combate / Acciones")
            }
            assertTrue(combatPages.isNotEmpty(), "Mara has the combat table.")
            val combat = combatPages.joinToString(" ") { n ->
                PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
            }.replace(Regex("\\s+"), " ")
            listOf("Entrada de combate custom 1", "Daño: 2d6", "Entrada de combate custom 8")
                .forEach { assertTrue(combat.contains(it), "$it prints in the combat table.") }
            val note = runs.first { text(it).trim().startsWith("Entrada de combate custom 1") }
            val noteEnd = runs.first { text(it).contains("Daño: 2d6") }
            assertTrue(kotlin.math.abs(widthOf(note, "e") - widthOf(noteEnd, "e")) < 0.01f,
                "All lines of one combat note keep one horizontal scale.")
            // M50800-16 and M50800-26: compact identity, wrapped, without weight or state.
            val wholeText = PDFTextStripper().getText(pdf)
            assertTrue(runs.none { text(it).contains("Frasco de tinta") && text(it).contains(" lb") },
                "Ordinary Equipment shows no weight.")
            assertFalse(wholeText.contains("Estado: "), "Ordinary Equipment shows no state line.")
            assertTrue(runs.any { text(it).trim().startsWith("2 x Frasco de tinta") }, "The identity keeps its quantity.")
            val normalized = wholeText.replace(Regex("\\s+"), " ")
            listOf("Mara de los Siete Umbrales", "Manipulación de éter", "Protocolo de paradoja 1", "Reserva 10: Sello")
                .forEach { assertTrue(normalized.contains(it), "$it stays in the sheet.") }
        }
    }

    @Test
    fun maraCustomV2FamiliesMicroFitSourceLabelsAndPreserveExtendedContent() {
        val document = fixture("03_mara_siete_umbrales_custom_extended.json")
        val families = listOf(
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        )

        families.forEach { family ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(
                    permanent = PcSheetExportAggregate(
                        sheet = document.character,
                        closure = document.closureState,
                        successor = document.successorState,
                    ),
                ),
            )

            val bytes = ByteArrayOutputStream().use { output ->
                DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
                output.toByteArray()
            }

            assertTrue(bytes.size > 20_000)
            Loader.loadPDF(bytes).use { pdf ->
                val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
                assertTrue(normalized.contains("Mara de los Siete Umbrales"))
                assertTrue(normalized.contains("Manipulación de éter"))
                assertTrue(normalized.contains("Astrolabio de cobre con anillos concéntricos 1"))
                assertTrue(normalized.contains("Protocolo de paradoja 1"))
                assertTrue(normalized.contains("Reserva 10: Sello"))
            }
        }
    }

    @Test
    fun maraFantasySheetRendersStressContentWithoutUnroutedOverflow() {
        val document = fixture("03_mara_siete_umbrales_custom_extended.json")
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = document.character,
                    closure = document.closureState,
                    successor = document.successorState,
                ),
            ),
        )

        val bytes = ByteArrayOutputStream().use { output ->
            DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
            output.toByteArray()
        }

        assertTrue(bytes.size > 20_000)

        Loader.loadPDF(bytes).use { pdf ->
            val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
            assertTrue(normalized.contains("Mara de los Siete Umbrales"))
            assertTrue(normalized.contains("Astrolabio de cobre con anillos concéntricos 1"))
            // A long special-item detail may cross physical continuation rows; require both
            // semantic halves rather than pretending PDF extraction keeps them adjacent.
            assertTrue(normalized.contains("Descripción suficientemente larga"))
            assertTrue(normalized.contains("del objeto 1"))
            assertTrue(normalized.contains("Protocolo de paradoja 1"))
            assertTrue(normalized.contains("Reserva 10: Sello"))
        }
    }

    private fun fixture(name: String) = assertIs<CharacterBackupDecodeResult.Success>(
        CharacterBackupCodec.decode(File(fixtureDirectory(), name).readText()),
    ).document

    private fun fixtureDirectory(): File {
        val start = File(System.getProperty("user.dir")).absoluteFile
        return generateSequence(start) { it.parentFile }
            .map { File(it, "qa/pc-sheet/fixtures") }
            .firstOrNull { it.isDirectory }
            ?: error("Could not locate qa/pc-sheet/fixtures from ${start.path}")
    }
}
