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
            assertTrue(normalized.contains("A máximo"))
            assertTrue(normalized.contains("Descanso corto o largo"))
            // Stage-2 owner runtime regression: the complete special-equipment detail must
            // survive outside compact cells instead of relying on Android font shrink.
            assertTrue(normalized.contains("Peso 3"))
            assertTrue(normalized.contains("Mano derecha"))
            assertTrue(normalized.contains("1d8 cortante; versátil 1d10."))
            assertTrue(normalized.contains("Arma marcial."))
        }
    }

    @Test
    fun aldrenCrossFamilyExportPreservesUnicodeAndUsesSemanticContinuation() {
        val document = fixture("01_aldren_vale_srd5_1_champion_fighter.json")
        val aggregate = PcSheetExportAggregate(
            sheet = document.character,
            closure = document.closureState,
            successor = document.successorState,
        )
        val oldOwnerObservedPageCounts = mapOf(
            PcSheetVisualFamily.CUSTOM_V1 to 13,
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to 8,
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to 8,
            PcSheetVisualFamily.CLASSIC_DND_STYLE to 18,
        )

        oldOwnerObservedPageCounts.forEach { (family, oldPageCount) ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(permanent = aggregate),
            )

            val bytes = ByteArrayOutputStream().use { output ->
                DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
                output.toByteArray()
            }

            Loader.loadPDF(bytes).use { pdf ->
                assertTrue(
                    pdf.numberOfPages < oldPageCount,
                    "$family should use existing compatible capacity before reproducing the owner-observed $oldPageCount-page export.",
                )

                val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
                listOf("Acólito", "acción", "versátil", "Común", "Élfico").forEach { expected ->
                    assertTrue(normalized.contains(expected), "$family lost Unicode text '$expected'.")
                }
                assertFalse(normalized.contains("ComÃ"), "$family contains UTF-8 mojibake.")
                assertFalse(normalized.contains("Ã‰"), "$family contains UTF-8 mojibake.")
                assertFalse(normalized.contains("â€“"), "$family contains mojibake punctuation.")
                assertFalse(normalized.contains("â€”"), "$family contains mojibake punctuation.")

                assertFalse(
                    Regex("\\bTrucos\\b", RegexOption.IGNORE_CASE).containsMatchIn(normalized),
                    "$family emitted a spell-list page for non-spellcaster Aldren.",
                )
                assertFalse(normalized.contains("Acción / ataque"))
                assertFalse(normalized.contains("Oro: 15"))

                val pageTexts = (1..pdf.numberOfPages).map { pageNumber ->
                    PDFTextStripper().apply {
                        startPage = pageNumber
                        endPage = pageNumber
                    }.getText(pdf).replace(Regex("\\s+"), " ")
                }
                pageTexts
                    .filter { it.uppercase().contains("RASGOS") }
                    .forEach { traitsPage ->
                        assertFalse(
                            traitsPage.contains("Daño estructurado"),
                            "$family still routes structured combat damage through Traits.",
                        )
                        assertFalse(
                            traitsPage.contains("Acción / ataque"),
                            "$family still routes combat/action references through Traits.",
                        )
                    }
                pageTexts
                    .filter { it.uppercase().contains("RECURSOS") }
                    .forEach { resourcesPage ->
                        assertFalse(
                            resourcesPage.contains("1 / 1"),
                            "$family still uses numeric 1 / 1 for a one-use resource.",
                        )
                    }

                val backpackOccurrences = Regex("Mochila de explorador").findAll(normalized).count()
                assertTrue(
                    backpackOccurrences <= 2,
                    "$family still blindly replays ordinary equipment in continuation pages.",
                )
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
