package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
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
    fun maraCustomV1GeneratesWithoutExcessiveSourceLabelCompression() {
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
            assertTrue(normalized.contains("Lectura de presagios"))
        }
    }

    @Test
    fun maraOrdinaryEquipmentProjectionOmitsWeightConsumibleAndDescriptionsAcrossFamilies() {
        val document = fixture("03_mara_siete_umbrales_custom_extended.json")
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val families = listOf(
            PcSheetVisualFamily.CLASSIC_DND_STYLE,
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
            File(
                proofDir,
                "mara-50800-phase1-${family.name.lowercase()}.pdf",
            ).writeBytes(bytes)

            Loader.loadPDF(bytes).use { pdf ->
                val normalized = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
                assertTrue(
                    !normalized.contains("Consumible"),
                    "$family must not project Consumible into Equipment",
                )
                assertTrue(
                    !Regex("""\b\d+(?:[.,]\d+)?\s*lb\b""").containsMatchIn(normalized),
                    "$family must not project equipment weight values",
                )
                assertTrue(
                    !normalized.contains("Descripción suficientemente larga del objeto 2. Debe"),
                    "$family must not project ordinary Equipment prose descriptions",
                )
                if (
                    family == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ||
                    family == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY
                ) {
                    assertTrue(
                        normalized.contains("Descripción suficientemente larga del objeto 1."),
                        "$family must preserve Special Equipment description in its dedicated domain",
                    )
                }
                assertTrue(
                    normalized.contains("Frasco de tinta que recuerda"),
                    "$family must preserve recognizable ordinary item identity in the native Equipment module",
                )
                assertTrue(
                    normalized.contains("Cuaderno de fórmulas"),
                    "$family must preserve the leading identity of the final Special Equipment item",
                )
                assertTrue(
                    Regex("""objeto\s+29\b""", RegexOption.IGNORE_CASE).containsMatchIn(normalized),
                    "$family must preserve unique semantic content from the final Special Equipment item",
                )
                // M50800-19 is a rendered-cell acceptance item. PDFTextStripper does not
                // reliably preserve the custom location cell in Custom-v1 even when the same
                // Special Equipment object's identity/detail text survives. Keep the item
                // semantically anchored here and verify "Bolsa lateral" on the rendered artifact.
                if (
                    family == PcSheetVisualFamily.CUSTOM_V1 ||
                    family == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ||
                    family == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY
                ) {
                    assertTrue(
                        !normalized.contains("INVENTARIO / EQUIPO"),
                        "$family must reuse the native Equipment page instead of the rejected generic inventory layout",
                    )
                    assertTrue(
                        normalized.contains("Descripción suficientemente larga del objeto 1."),
                        "$family must preserve Special Equipment descriptions in the native module",
                    )
                }

                if (
                    family == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ||
                    family == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY
                ) {
                    val pageTexts = (1..pdf.numberOfPages).map { pageNumber ->
                        PDFTextStripper().apply {
                            startPage = pageNumber
                            endPage = pageNumber
                        }.getText(pdf).replace(Regex("\\s+"), " ")
                    }
                    assertTrue(
                        pageTexts.any { it.contains("EQUIPO ESPECIAL", ignoreCase = true) },
                        "$family must preserve the native Equipment/Trasfondo source page",
                    )
                    assertEquals(
                        1,
                        pageTexts.count { it.contains("CLASE Y NIVEL", ignoreCase = true) },
                        "$family must contain exactly one main-sheet source page; Equipment continuation must not duplicate it",
                    )
                }
            }
        }
    }

    @Test
    fun maraCustomV2StatisticsUseOneCleanNativeScalePageWithoutSourceLeak() {
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
                val pageTexts = (1..pdf.numberOfPages).map { pageNumber ->
                    PDFTextStripper().apply {
                        startPage = pageNumber
                        endPage = pageNumber
                    }.getText(pdf).replace(Regex("\\s+"), " ")
                }
                val statsPages = pageTexts.filter {
                    it.contains("ESTADÍSTICAS PERSONALIZADAS", ignoreCase = true)
                }
                assertEquals(
                    1,
                    statsPages.size,
                    "$family must place Mara's four real custom attributes on one statistics page",
                )
                val stats = statsPages.single()
                listOf("Fortuna", "Cordura", "Éter", "Renombre").forEach { attributeName ->
                    assertTrue(
                        stats.contains(attributeName, ignoreCase = true),
                        "$family statistics page must contain real attribute $attributeName",
                    )
                }
                assertTrue(
                    !stats.contains("ETE · Éter", ignoreCase = true),
                    "$family must render clean Éter rather than the ambiguous keyed fallback",
                )
                listOf("UBICACIÓN", "EQUIPO ESPECIAL", "CLASE Y NIVEL").forEach { leakedSourceLabel ->
                    assertTrue(
                        !stats.contains(leakedSourceLabel, ignoreCase = true),
                        "$family statistics page must not leak source-underlay text: $leakedSourceLabel",
                    )
                }

                val normalized = pageTexts.joinToString(" ")
                assertTrue(normalized.contains("Mara de los Siete Umbrales"))
                assertTrue(normalized.contains("Manipulación de éter"))
                assertTrue(normalized.contains("Astrolabio de cobre con anillos concéntricos 1"))
                assertTrue(normalized.contains("Protocolo de paradoja 1"))
                assertTrue(normalized.contains("Reserva 10: Sello"))
            }
        }
    }

    @Test
    fun maraCustomV2TraitsUseAdaptiveNativeRowsAndPreserveCategoryOrder() {
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

            Loader.loadPDF(bytes).use { pdf ->
                val pageTexts = (1..pdf.numberOfPages).map { pageNumber ->
                    PDFTextStripper().apply {
                        startPage = pageNumber
                        endPage = pageNumber
                    }.getText(pdf).replace(Regex("\\s+"), " ")
                }
                val traitPages = pageTexts.filter {
                    it.contains("RASGOS Y ATRIBUTOS", ignoreCase = true) &&
                        it.contains("CONTINUACIÓN", ignoreCase = true)
                }
                assertTrue(traitPages.isNotEmpty(), "$family must emit native Traits continuation")
                assertTrue(
                    traitPages.none { it.contains("OTROS RASGOS", ignoreCase = true) },
                    "$family must not reproduce the rejected fixed Traits scaffold",
                )
                assertTrue(
                    traitPages.none { it.contains("DETALLES / NOTAS", ignoreCase = true) },
                    "$family must not constrain late Traits continuation to the rejected detail panel",
                )

                val fullPdfText = pageTexts.joinToString(" ")
                document.character.traits
                    .sortedBy { it.sortOrder }
                    .forEach { trait ->
                        assertTrue(
                            fullPdfText.contains(trait.name),
                            "$family must preserve trait identity ${trait.name} somewhere in the full PDF",
                        )
                    }

                val resourceNames = document.character.resources
                    .map { it.name.trim().lowercase() }
                    .filter { it.isNotEmpty() }
                    .toSet()
                val actionNames = document.character.combatEntries
                    .filter { it.type != CharacterCombatEntryType.ATTACK }
                    .map { it.name.trim().lowercase() }
                    .filter { it.isNotEmpty() }
                    .toSet()
                val traitsOwnedByThisSurface = document.character.traits
                    .filterNot { trait ->
                        trait.type == CharacterTraitType.SPECIES_RACE &&
                            trait.name.equals(document.character.background.race, ignoreCase = true)
                    }
                    .filterNot { trait ->
                        val normalizedName = trait.name.trim().lowercase()
                        normalizedName in resourceNames || normalizedName in actionNames
                    }
                val extendedTraitsText = traitPages.joinToString(" ")

                val primary = traitsOwnedByThisSurface
                    .filter {
                        it.type == CharacterTraitType.CLASS ||
                            it.type == CharacterTraitType.FEAT ||
                            it.type == CharacterTraitType.GIFT_BLESSING
                    }
                    .sortedBy { it.sortOrder }
                    .map { it.name }
                val secondary = traitsOwnedByThisSurface
                    .filterNot {
                        it.type == CharacterTraitType.CLASS ||
                            it.type == CharacterTraitType.FEAT ||
                            it.type == CharacterTraitType.GIFT_BLESSING
                    }
                    .sortedBy { it.sortOrder }
                    .map { it.name }

                fun assertOrdered(names: List<String>) {
                    var previous = -1
                    names.forEach { name ->
                        val current = extendedTraitsText.indexOf(name)
                        assertTrue(current >= 0, "$family missing trait $name")
                        assertTrue(
                            current > previous,
                            "$family must preserve source sortOrder inside each accepted category",
                        )
                        previous = current
                    }
                }
                assertOrdered(primary)
                assertOrdered(secondary)
            }
        }
    }

    @Test
    fun customV2BackgroundOverflowUsesNativeNarrativeSections() {
        val document = fixture("03_mara_siete_umbrales_custom_extended.json")
        val stressedBackground = document.character.background.copy(
            summary = document.character.background.summary + " " +
                List(18) { "Resumen narrativo adicional ${it + 1}." }.joinToString(" "),
            bonds = document.character.background.bonds + " " +
                List(14) { "Vínculo adicional ${it + 1}." }.joinToString(" "),
            ideals = document.character.background.ideals + " " +
                List(14) { "Ideal adicional ${it + 1}." }.joinToString(" "),
            story = document.character.background.story + " " +
                List(60) { "Historia adicional ${it + 1} para continuidad." }.joinToString(" "),
        )
        val stressedSheet = document.character.copy(background = stressedBackground)
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = stressedSheet,
                    closure = document.closureState,
                    successor = document.successorState,
                ),
            ),
        )

        val bytes = ByteArrayOutputStream().use { output ->
            DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
            output.toByteArray()
        }

        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        File(
            proofDir,
            "mara-50800-phase2b1-background-overflow.pdf",
        ).writeBytes(bytes)

        Loader.loadPDF(bytes).use { pdf ->
            val pageTexts = (1..pdf.numberOfPages).map { pageNumber ->
                PDFTextStripper().apply {
                    startPage = pageNumber
                    endPage = pageNumber
                }.getText(pdf).replace(Regex("\\s+"), " ")
            }
            val backgroundPages = pageTexts.filter {
                it.contains("TRASFONDO / HISTORIA", ignoreCase = true) &&
                    it.contains("CONTINUACIÓN", ignoreCase = true)
            }
            assertEquals(
                1,
                backgroundPages.size,
                "Stressed native narrative overflow must reclaim both columns before adding another page",
            )
            val backgroundText = backgroundPages.single()
            listOf("TRASFONDO", "VÍNCULOS", "IDEALES", "HISTORIA").forEach { section ->
                assertTrue(
                    backgroundText.contains(section, ignoreCase = true),
                    "Native narrative continuation must preserve $section identity",
                )
            }
            assertTrue(
                backgroundPages.none { it.contains("DETALLES / NOTAS", ignoreCase = true) },
                "Background overflow must not be routed through the generic Traits detail panel",
            )
            assertTrue(
                backgroundText.contains("Historia adicional 60"),
                "Native narrative packing must preserve the tail of the stressed Historia content",
            )
        }
    }

    @Test
    fun maraCustomV2NotesPreserveNativeRecordBoundariesAndContinuationIdentity() {
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

            Loader.loadPDF(bytes).use { pdf ->
                val pageTexts = (1..pdf.numberOfPages).map { pageNumber ->
                    PDFTextStripper().apply {
                        startPage = pageNumber
                        endPage = pageNumber
                    }.getText(pdf).replace(Regex("\\s+"), " ").trim()
                }
                val fullText = pageTexts.joinToString(" ")
                val orderedTitles = document.character.noteCards
                    .sortedBy { it.sortOrder }
                    .map { it.title.trim() }
                    .filter { it.isNotEmpty() }

                orderedTitles.forEach { title ->
                    assertTrue(
                        fullText.contains(title),
                        "$family must preserve note-card identity $title",
                    )
                }

                var previousIndex = -1
                orderedTitles.forEach { title ->
                    val index = fullText.indexOf(title)
                    assertTrue(index > previousIndex, "$family must preserve note-card sortOrder")
                    previousIndex = index
                }

                val notesPages = pageTexts.filter { text ->
                    text.contains("NOTAS", ignoreCase = true) &&
                        (
                            text.contains("Nota ", ignoreCase = true) ||
                                text.contains("Notas generales", ignoreCase = true) ||
                                text.contains("continuación", ignoreCase = true)
                        )
                }
                assertTrue(notesPages.size >= 2, "$family must exercise real Mara Notes overflow")
                val overflowText = notesPages.drop(1).joinToString(" ")
                assertTrue(
                    Regex("""Nota\\s+\\d+""", RegexOption.IGNORE_CASE).containsMatchIn(overflowText),
                    "$family Notes overflow must preserve explicit note-record identities",
                )
                assertTrue(
                    overflowText.contains("Rasgos de personalidad", ignoreCase = true),
                    "$family must preserve background-derived Notes records",
                )
                assertTrue(
                    overflowText.contains("Subclase", ignoreCase = true),
                    "$family must preserve subclass-derived Notes records",
                )
            }
        }
    }

    @Test
    fun customV2LongNoteRepeatsIdentityAcrossNativeContinuationBoundary() {
        val document = fixture("03_mara_siete_umbrales_custom_extended.json")
        val first = document.character.noteCards.sortedBy { it.sortOrder }.first()
        val stressedFirst = first.copy(
            content = first.content + " " +
                List(90) { "Fragmento prolongado de nota ${it + 1} para forzar continuidad nativa." }
                    .joinToString(" "),
        )
        val stressedCards = document.character.noteCards.map { note ->
            if (note.id == first.id) stressedFirst else note
        }
        val stressedSheet = document.character.copy(noteCards = stressedCards)
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = stressedSheet,
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
            assertTrue(
                normalized.contains(first.title + " (continuación)", ignoreCase = true),
                "A note split across a native column/page boundary must repeat its identity",
            )
            assertTrue(
                normalized.contains("Fragmento prolongado de nota 90", ignoreCase = true),
                "Long-note continuation must preserve the final semantic tail",
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
