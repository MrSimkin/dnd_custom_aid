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
    fun maraCustomV2StatisticsKeepNativeSixRowCapacityAndContextualAttributeKeys() {
        val document = fixture("03_mara_siete_umbrales_custom_extended.json")

        fun render(family: PcSheetVisualFamily): ByteArray {
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
            return ByteArrayOutputStream().use { output ->
                DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
                output.toByteArray()
            }
        }

        Loader.loadPDF(render(PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE)).use { pdf ->
            val pageTexts = (1..pdf.numberOfPages).map { pageNumber ->
                PDFTextStripper().apply {
                    startPage = pageNumber
                    endPage = pageNumber
                }.getText(pdf).replace(Regex("\\s+"), " ")
            }
            val attributePage = pageTexts.firstOrNull { page ->
                listOf("FORtuna", "CORdura", "ETEr", "RENombre").all(page::contains)
            }
            assertTrue(
                attributePage != null,
                "Mara's four custom attributes must share one native-scale Custom Statistics page.",
            )
            listOf(
                "EQUIPO ESPECIAL",
                "VÍNCULOS",
                "IDEALES",
                "HISTORIA",
                "PUNTOS DE VIDA",
            ).forEach { staleLabel ->
                assertTrue(
                    !requireNotNull(attributePage).contains(staleLabel),
                    "Custom Statistics page leaked stale source-template text: $staleLabel",
                )
            }
            val whole = pageTexts.joinToString(" ")
            assertTrue(!whole.contains("ETE · Éter"))
            assertTrue(whole.contains("Manipulación de éter"))
            assertTrue(!whole.contains("Manipulación de éter (ETE)"))
            assertTrue(whole.contains("Lectura de presagios (SAB)"))
        }

        Loader.loadPDF(render(PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY)).use { pdf ->
            val whole = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
            assertTrue(whole.contains("ETEr"))
            assertTrue(!whole.contains("ETE · Éter"))
            assertTrue(whole.contains("Manipulación de éter (ETE)"))
        }
    }

    @Test
    fun maraCustomV1WrapsLongCustomSkillNamesAtReadableSourceScale() {
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

        assertTrue(bytes.size > 20_000)
        Loader.loadPDF(bytes).use { pdf ->
            val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
            assertTrue(normalized.contains("Mara de los Siete Umbrales"))
            assertTrue(
                normalized.contains("Lectura de presagios"),
                "Custom-v1 must preserve the complete long custom-skill identity.",
            )
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
            assertTrue(normalized.contains("ARMAS Y ACCIONES"))
            assertTrue(normalized.contains("Técnica 8 — Descarga prismática"))
            assertTrue(
                !normalized.contains("Efecto / daño:"),
                "Fantasy combat continuation must preserve table grammar instead of prose serialization.",
            )
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
