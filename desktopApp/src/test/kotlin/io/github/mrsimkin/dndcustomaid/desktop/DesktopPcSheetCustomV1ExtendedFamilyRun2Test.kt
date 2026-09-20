package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper

/**
 * Custom-v1 Extended Run 2.
 *
 * Run 1 was owner-rejected because it borrowed only superficial gray-band traits and did not
 * preserve the actual Custom-v1 page composition / typography / box grammar.
 *
 * Run 2 therefore treats the owner PDF itself as the extension design kit:
 * - source page structures are reused directly for Traits, Resources, Inventory, Spells and Notes;
 * - the Custom Statistics page keeps the source MAIN attribute-row composition and ornamental boxes;
 * - EnchantedLand and Gill Sans are loaded from the fonts already embedded in the owner source PDF;
 * - only generated character content uses the already-approved overlay typography.
 */
class DesktopPcSheetCustomV1ExtendedFamilyRun2Test {
    private val overflowDiagnostics = mutableListOf<String>()

    @Test
    fun rendersCustomV1ExtensionsFromActualSourcePageGrammar() {
        overflowDiagnostics.clear()
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }

        // Regenerate the exact owner-approved frozen base.
        DesktopPcSheetHybridStrategyRun7Test().rendersCompleteFivePageAllHybridCustomV1Draft()
        val approvedBase = File(proofDir, "hybrid-strategy1-run7-composite.pdf")
        assertTrue(approvedBase.isFile)

        val frozenRenders = Loader.loadPDF(approvedBase).use { doc ->
            val renderer = PDFRenderer(doc)
            (0 until 5).map { renderer.renderImageWithDPI(it, BASELINE_COMPARE_DPI, ImageType.RGB) }
        }

        val sourceBytes = resource(TEMPLATE).use { it.readBytes() }
        val output = File(proofDir, "custom-v1-complete-family-extended-run2.pdf")

        Loader.loadPDF(approvedBase).use { doc ->
            Loader.loadPDF(sourceBytes).use { source ->
                val layerUtility = LayerUtility(doc)
                val sourceForms = (0 until 5).map { layerUtility.importPageAsForm(source, it) }
                val originalFonts = OriginalFonts(
                    heading = loadEmbeddedSourceFont(doc, source, "EnchantedLand"),
                    label = loadEmbeddedSourceFont(doc, source, "GillSansMT"),
                )
                val generatedFonts = DesktopPdfFontRegistry(doc, RUN2_THEME)
                val p = DesktopPdfRenderingPrimitives(generatedFonts)

                drawCustomStatistics(doc, p, originalFonts, sourceForms[0])
                drawTraits(doc, p, originalFonts, sourceForms[2])
                drawResources(doc, p, originalFonts, sourceForms[1])
                drawInventory(doc, p, originalFonts, sourceForms[1])
                drawSpells(doc, p, originalFonts, sourceForms[3])
                drawNotes(doc, p, originalFonts, sourceForms[4])
            }
            doc.save(output)
        }

        Loader.loadPDF(output).use { doc ->
            assertEquals(11, doc.numberOfPages)
            assertFalse(doc.isEncrypted)
            repeat(doc.numberOfPages) { i ->
                assertEquals(W, doc.getPage(i).mediaBox.width, 0.01f)
                assertEquals(H, doc.getPage(i).mediaBox.height, 0.01f)
            }

            val extracted = PDFTextStripper().getText(doc)
            listOf(
                "Estadísticas Personalizadas",
                "Rasgos de Clase",
                "Recursos",
                "Equipo",
                "Trucos",
                "Notas",
            ).forEach { token ->
                assertTrue(extracted.contains(token, ignoreCase = true), "Missing Custom-v1 Run-2 semantic token: $token")
            }

            val renderer = PDFRenderer(doc)
            repeat(11) { index ->
                val image = renderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "custom-v1-complete-family-extended-run2-page-${index + 1}.png"),
                    ),
                )
            }

            // Hard guard: extension work cannot touch the owner-approved five-page base.
            repeat(5) { index ->
                val actual = renderer.renderImageWithDPI(index, BASELINE_COMPARE_DPI, ImageType.RGB)
                assertImagesEqual(
                    frozenRenders[index],
                    actual,
                    "Frozen Custom-v1 base page ${index + 1} changed during Extended Run 2.",
                )
            }
        }

        if (overflowDiagnostics.isNotEmpty()) {
            File(proofDir, "custom-v1-complete-family-extended-run2-overflows.txt")
                .writeText(overflowDiagnostics.joinToString("\n"))
        }
        overflowDiagnostics.forEach { println("CUSTOM_V1_RUN2_OVERFLOW: $it") }
        check(overflowDiagnostics.isEmpty()) {
            "Custom-v1 Extended Run 2 overflow(s):\n" + overflowDiagnostics.joinToString("\n")
        }
        assertTrue(output.length() > 20_000L)
    }

    // Page 6: source MAIN grammar, not a generic extension card page.
    private fun drawCustomStatistics(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        original: OriginalFonts,
        mainSource: PDFormXObject,
    ) {
        val page = blankPage(doc)
        PDPageContentStream(doc, page).use { s ->
            s.drawForm(mainSource)

            // Preserve the real owner logo and entire six-column Attribute strip.
            // Keep the entire original logo; clear only the source MAIN content around it.
            white(s, 185f, 20f, 407f, 220f)
            white(s, 25f, 96f, 150f, 22f) // source "Puntos de Experiencia"
            white(s, 20f, 112f, 572f, 128f)
            white(s, 20f, 400f, 572f, 372f)

            centeredOriginal(s, original.heading, 185f, 55f, 395f, 28f, "Estadísticas Personalizadas", 18f)
            smallLabel(s, p, 185f, 87f, 395f, 14f, "ATRIBUTOS PERSONALIZADOS Y HABILIDADES VINCULADAS", 7.5f)

            centeredOriginal(s, original.heading, 24f, 116f, 564f, 24f, "Definiciones", 16f)
            val definitionCols = listOf(25f to 181f, 215.291f to 396.708f, 402.378f to 583.795f)
            definitionCols.forEach { (a, b) -> sourceBands(s, a, b, 160f, 4, 20f) }
            dataLine(s, p, 27f, 144f, 152f, "Honor: reputación, deber y prestigio.", 8.2f)
            dataLine(s, p, 217f, 144f, 177f, "Resolución: autocontrol bajo presión.", 8.2f)
            dataLine(s, p, 404f, 144f, 177f, "Suerte: azar favorable y oportunidades.", 8.2f)

            val columns = listOf(
                AttributeColumn(22.5f, 96.4f, false, "Honor", "15", "+2", "+5",
                    listOf("Etiqueta cortesana" to "+5", "Reputación" to "+5", "Deber" to "+2", "Protocolo" to "+5"),
                    listOf(Training.PROFICIENT, Training.PROFICIENT, Training.NONE, Training.PROFICIENT)),
                AttributeColumn(118.9f, 96.4f, true, "Resolución", "12", "+1", "+1",
                    listOf("Concentración" to "+4", "Resistir miedo" to "+4", "Autocontrol" to "+1"),
                    listOf(Training.PROFICIENT, Training.PROFICIENT, Training.NONE)),
                AttributeColumn(215.3f, 96.4f, false, "Suerte", "18", "+4", "+7",
                    listOf("Lectura de fortuna" to "+7", "Escape improvisado" to "+10", "Azar" to "+4"),
                    listOf(Training.PROFICIENT, Training.EXPERTISE, Training.NONE)),
                AttributeColumn(311.7f, 96.4f, true, "INTeligencia", "18", "+4", "+7",
                    listOf("Cifras antiguas" to "+7", "Análisis de runas" to "+7"),
                    listOf(Training.PROFICIENT, Training.PROFICIENT)),
                AttributeColumn(408.0f, 96.4f, false, "SABiduría", "12", "+1", "+1",
                    listOf("Cartografía" to "+4", "Orientación astral" to "+1"),
                    listOf(Training.PROFICIENT, Training.NONE)),
                AttributeColumn(504.4f, 85.5f, true, "DEStreza", "16", "+3", "+6",
                    listOf("Acrobacia con cuerda" to "+6", "Cerrajería fina" to "+9"),
                    listOf(Training.PROFICIENT, Training.EXPERTISE)),
            )

            columns.forEachIndexed { index, column ->
                redrawAttributeColumn(s, p, original, column, index)
            }

            centeredOriginal(s, original.heading, 24f, 428f, 564f, 26f, "Notas de Estadísticas Personalizadas", 18f)
            val noteCols = listOf(
                25f to 181f,
                215.291f to 396.708f,
                402.378f to 583.795f,
            )
            noteCols.forEach { (a, b) -> sourceBands(s, a, b, 468f, 12, 20f) }
            dataLine(s, p, 27f, 468f, 152f, "Honor: reputación y deber.", 8.4f)
            dataLine(s, p, 27f, 488f, 152f, "Prestigio y posición social.", 8.4f)
            dataLine(s, p, 217f, 468f, 177f, "Resolución: presión, miedo o dolor.", 8.4f)
            dataLine(s, p, 404f, 468f, 177f, "Suerte: azar favorable y oportunidades.", 8.4f)
            dataLine(s, p, 27f, 528f, 152f, "Cada habilidad conserva su Atributo.", 8.2f)
        }
    }

    // Page 7: exact page-3 composition, relabeled rather than redesigned.
    private fun drawTraits(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        original: OriginalFonts,
        narrativeSource: PDFormXObject,
    ) {
        val page = sourcePage(doc, narrativeSource)
        PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true).use { s ->
            replaceHeading(s, original.heading, 24f, 66f, 156f, 35f, "Rasgos de Clase", 18f)
            replaceHeading(s, original.heading, 24f, 205f, 156f, 35f, "Rasgos de Especie", 18f)
            replaceHeading(s, original.heading, 24f, 344f, 156f, 35f, "Dotes", 18f)
            replaceHeading(s, original.heading, 24f, 483f, 156f, 35f, "Competencias", 18f)
            replaceHeading(s, original.heading, 24f, 621f, 156f, 35f, "Idiomas", 18f)
            replaceHeading(s, original.heading, 215f, 344f, 369f, 35f, "Detalles de Rasgos", 18f)

            writeRuledLines(s, p, 25f, listOf(109.5f,129.5f,149.5f), 156f,
                listOf("Portento", "Recuperación arcana", "Acción astuta"))
            writeRuledLines(s, p, 25f, listOf(248.5f,268.5f,287.5f), 156f,
                listOf("Visión en la oscuridad", "Ascendencia feérica", "Trance"))
            writeRuledLines(s, p, 25f, listOf(387.5f,407.5f,426f), 156f,
                listOf("Observador", "Mente aguda"))
            writeRuledLines(s, p, 25f, listOf(526.5f,546f,565f), 156f,
                listOf("Herramientas de ladrón", "Caligrafía"))
            writeRuledLines(s, p, 25f, listOf(665.5f,685f,704f), 156f,
                listOf("Común", "Élfico", "Dracónico"))

            val rightRules = listOf(109.5f,129.5f,149.5f,169f,189f,209f)
            val rightNames = listOf(
                "Investigador", "Código de campo", "Afinidad ritual", "Contacto de Academia",
                "Memoria de archivo", "Disciplina de estudio",
            )
            rightRules.forEachIndexed { i, y ->
                dataAboveRule(s, p, Rule(215.291f, 396.708f, y), rightNames.getOrElse(i) { "" }, 8.8f)
            }

            val descriptions = listOf(
                "Portento: conserva resultados y puede sustituir una tirada apropiada.",
                "Investigador: sabe dónde buscar información académica especializada.",
                "Código de campo: sistema abreviado para registrar rutas, sellos y peligros.",
                "Afinidad ritual: mantiene procedimientos y anotaciones para magia ritual.",
            )
            listOf(387.996f,407.839f,427.681f,447.524f).forEachIndexed { i, y ->
                dataAboveRule(s, p, Rule(215.291f, 583.795f, y), descriptions.getOrElse(i) { "" }, 8.5f)
            }

            val notes = listOf(
                "Espacio deliberadamente libre para nuevos rasgos.",
                "La continuación conserva la misma pauta que la página narrativa original.",
            )
            listOf(606f,625.5f,645.5f,665.5f,685f).forEachIndexed { i, y ->
                dataAboveRule(s, p, Rule(215.291f, 583.795f, y), notes.getOrElse(i) { "" }, 8.5f)
            }
        }
    }

    // Page 8: exact page-2 composition reinterpreted as resources/options.
    private fun drawResources(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        original: OriginalFonts,
        equipmentSource: PDFormXObject,
    ) {
        val page = sourcePage(doc, equipmentSource)
        PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true).use { s ->
            replaceHeading(s, original.heading, 160f, 66f, 250f, 35f, "Recursos", 18f)
            replaceHeading(s, original.heading, 438f, 66f, 150f, 35f, "Recuperación", 18f)
            replaceHeading(s, original.heading, 446f, 243f, 142f, 36f, "Estados", 18f)
            replaceHeading(s, original.heading, 205f, 460f, 200f, 35f, "Opciones", 18f)

            smallLabel(s, p, 27f, 94f, 110f, 14f, "RECURSO", 7.5f)
            smallLabel(s, p, 169.9f, 94f, 130f, 14f, "ACTUAL / MÁX.", 7.5f)
            smallLabel(s, p, 311.7f, 94f, 130f, 14f, "RECUPERACIÓN", 7.5f)

            val resourceRows = listOf(
                Triple("Recuperación arcana", "1 / 1", "Descanso largo"),
                Triple("Dados de portento", "1 / 2", "Descanso largo"),
                Triple("Carga del monóculo", "2 / 3", "Amanecer"),
                Triple("Inspiración heroica", "1 / 1", "Variable"),
            )
            val y = listOf(128.5f,168f,208f,247.5f,287.5f,327f)
            resourceRows.forEachIndexed { i, r ->
                dataAboveRule(s, p, Rule(27.5f,137.5f,y[i]), r.first, 8.5f)
                dataAboveRule(s, p, Rule(169.937f,300.331f,y[i]), r.second, 8.5f)
                dataAboveRule(s, p, Rule(311.669f,442.063f,y[i]), r.third, 8.5f)
            }

            // Replace coin labels with recovery categories while keeping exact source row architecture.
            white(s, 450f, 94f, 145f, 108f)
            val recoveryLabels = listOf("DESCANSO CORTO", "DESCANSO LARGO", "AMANECER", "OTRO", "VARIABLE")
            val recY = listOf(99f,119f,139f,159f,179f)
            recoveryLabels.forEachIndexed { i, label ->
                smallData(s, p, 456f, recY[i], 92f, 14f, label, 6.8f)
                dataAboveRule(s, p, Rule(518f,583.8f,recY[i] + 10f), listOf("0","2","1","0","1")[i], 8.2f)
            }

            replaceSmallLabel(s, p, 456f, 274f, 62f, 16f, "ESTADO", 7.5f)
            replaceSmallLabel(s, p, 548f, 274f, 40f, 16f, "VALOR", 7.5f)
            val states = listOf("Oculto" to "NO", "Asustado" to "NO", "Concentración" to "SÍ")
            val stateY = listOf(307f,347f,386.5f)
            states.forEachIndexed { i, st ->
                dataAboveRule(s, p, Rule(453.402f,546.945f,stateY[i]), st.first, 8.2f)
                dataAboveRule(s, p, Rule(549.779f,583.795f,stateY[i]), st.second, 8.2f)
            }

            replaceSmallLabel(s, p, 50f, 490f, 68f, 16f, "TIPO", 7.5f)
            replaceSmallLabel(s, p, 150f, 490f, 78f, 16f, "OPCIÓN", 7.5f)
            replaceSmallLabel(s, p, 385f, 490f, 90f, 16f, "DESCRIPCIÓN", 7.5f)
            white(s, 24f, 505f, 88f, 260f)

            val options = listOf(
                Triple("Clase", "Tradición de Adivinación", "Portento y capacidades de adivinación."),
                Triple("Competencia", "Herramientas de ladrón", "Cerraduras, trampas y mecanismos."),
                Triple("Objeto", "Monóculo rúnico", "Inspección arcana; consume cargas."),
                Triple("Trasfondo", "Investigador", "Acceso a redes y fuentes académicas."),
            )
            val optionY = listOf(522.5f,562f,602f,641.5f)
            options.forEachIndexed { i, row ->
                leftOriginal(s, original.heading, 35f, optionY[i]-18f, 74f, 22f, row.first, 13.5f)
                dataAboveRule(s, p, Rule(126f,238f,optionY[i]), row.second, 8.2f)
                dataAboveRule(s, p, Rule(240.803f,583.795f,optionY[i]), row.third, 8.2f)
            }
        }
    }

    // Page 9: actual Equipment page repeated as continuation.
    private fun drawInventory(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        original: OriginalFonts,
        equipmentSource: PDFormXObject,
    ) {
        val page = sourcePage(doc, equipmentSource)
        PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true).use { s ->
            smallLabel(s, p, 195f, 96f, 80f, 13f, "CONTINUACIÓN", 7.5f)

            val items = listOf(
                "12 x Clavos", "8 x Tiza", "6 x Viales",
                "2 x Tinta azul", "Espejo de acero", "Manta",
                "Martillo pequeño", "Linterna cubierta", "4 x Aceite",
                "Cuerda de seda", "Campanillas", "Estuche de mapas",
            )
            val ys = listOf(128.5f,168f,208f,247.5f,287.5f,327f,366.5f,406.5f)
            var itemIndex = 0
            ys.forEach { yy ->
                listOf(
                    27.5f to 137.5f,
                    169.937f to 300.331f,
                    311.669f to 442.063f,
                ).forEach { (a,b) ->
                    if (itemIndex < items.size) {
                        dataAboveRule(s, p, Rule(a,b,yy), items[itemIndex++], 8.6f)
                    }
                }
            }

            val valuables = listOf("Gema lunar" to "120", "Broche élfico" to "75", "Láminas de plata" to "45")
            listOf(307f,347f,386.5f).forEachIndexed { i, yy ->
                valuables.getOrNull(i)?.let {
                    dataAboveRule(s, p, Rule(453.402f,546.945f,yy), it.first, 8.2f)
                    dataAboveRule(s, p, Rule(549.779f,583.795f,yy), it.second, 8.2f)
                }
            }

            val special = listOf(
                "Capucha rúnica" to "Tela tratada para proteger inscripciones.",
                "Lente de búsqueda" to "Ayuda a inspeccionar detalles finos.",
                "Amuleto del archivo" to "Recuerdo y foco ceremonial.",
                "Anillo de cobre" to "Marca de acceso menor.",
            )
            listOf(522.5f,542.5f,562f,582f).forEachIndexed { i, yy ->
                special.getOrNull(i)?.let {
                    dataAboveRule(s, p, Rule(126f,238f,yy), it.first, 8.2f)
                    dataAboveRule(s, p, Rule(240.803f,583.795f,yy), it.second, 8.2f)
                }
            }
        }
    }

    // Page 10: actual source spell page repeated; no generic spell-table redesign.
    private fun drawSpells(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        original: OriginalFonts,
        spellSource: PDFormXObject,
    ) {
        val page = sourcePage(doc, spellSource)
        PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true).use { s ->
            val continuation = mapOf(
                0 to listOf("Mensaje", "Ilusión menor"),
                1 to listOf("Identificar", "Dormir", "Alarma", "Comprender idiomas"),
                2 to listOf("Telaraña", "Abrir", "Detectar pensamientos"),
                3 to listOf("Disipar magia", "Relámpago", "Círculo mágico", "Clarividencia"),
                4 to listOf("Destierro", "Confusión", "Localizar criatura"),
                5 to listOf("Contacto con otro plano", "Modificar memoria", "Telepatía"),
                6 to listOf("Globo de invulnerabilidad", "Visión verdadera"),
                7 to listOf("Jaula de fuerza", "Simulacro"),
                8 to listOf("Laberinto", "Mente en blanco"),
                9 to listOf("Prisión", "Detener el tiempo"),
            )
            spellRows().forEach { block ->
                val spells = continuation[block.level].orEmpty()
                spells.take(block.rules.size).forEachIndexed { i, spell ->
                    dataAboveRule(s, p, block.rules[i], spell, 8.7f, leftPadding = 11.5f)
                    if (block.level > 0 && i % 2 == 0) {
                        marker(s, p, block.checkboxCenters[i].first, block.checkboxCenters[i].second, 7f, PdfMarkerKind.CHECK)
                    }
                }
                block.slotRule?.let { rule ->
                    val slotValue = mapOf(1 to "4",2 to "3",3 to "3",4 to "3",5 to "2",6 to "2",7 to "1",8 to "1",9 to "1")[block.level]
                    if (slotValue != null) dataAboveRule(s, p, rule, slotValue, 9f)
                }
            }
        }
    }

    // Page 11: actual Notes page repeated.
    private fun drawNotes(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        original: OriginalFonts,
        notesSource: PDFormXObject,
    ) {
        val page = sourcePage(doc, notesSource)
        PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true).use { s ->
            val left = listOf(
                "Contactar a Maestra Elenya al regresar a Liria.",
                "No entregar el mapa original a terceros.",
                "Preparar tinta resistente al agua.",
                "Revisar el corredor norte antes de acampar.",
                "El sello azul aparece también en las monedas de la torre.",
            )
            val right = listOf(
                "Comparar el alfabeto de la puerta norte con las notas de Vael.",
                "Ruta: entrada oeste, cámara de columnas, escalera rota.",
                "Guardar una carga del monóculo para el archivo inferior.",
                "Mantener una copia separada del alfabeto parcial.",
            )
            val ys = listOf(109.5f,129.5f,149.5f,169f,189f,209f,229f,248.5f,268.5f)
            left.forEachIndexed { i, value -> dataAboveRule(s, p, Rule(25f,267.5f,ys[i]), value, 8.4f) }
            right.forEachIndexed { i, value -> dataAboveRule(s, p, Rule(311.669f,583.795f,ys[i]), value, 8.4f) }
        }
    }

    private fun redrawAttributeColumn(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        original: OriginalFonts,
        column: AttributeColumn,
        index: Int,
    ) {
        val bg = if (column.gray) SOURCE_GRAY else Color.WHITE

        // Cover only source labels; preserve source ornamental boxes and global composition.
        fill(s, column.x - 1f, 243f, column.width + 2f, 29f, bg)
        fill(s, column.x + 10f, 321f, column.width - 36f, 73f, bg)

        centeredOriginal(s, original.heading, column.x - 1f, 243f, column.width + 2f, 27f, column.title, 18.5f)

        val scoreX = listOf(58f,154.25f,250.75f,347.25f,445.5f,539.75f)[index]
        val modX = listOf(85f,184.25f,280.5f,376.75f,473.25f,569.5f)[index]
        dataBox(s, p, scoreX - 28f, 265.5f, 56f, 31f, column.score, 17f)
        dataBox(s, p, modX - 16f, 285f, 32f, 20f, column.modifier, 10.8f)

        // Saving throw source row.
        if (index < 3) {
            marker(s, p, column.x + 5.3f, 316.4f, 8f, PdfMarkerKind.CHECK)
        }
        dataBox(s, p, column.x + column.width - 27f, 305f, 24f, 18f, column.save, 8.2f)

        val rowTops = listOf(324.1f,338.2f,352.4f,366.6f,380.8f)
        rowTops.forEachIndexed { rowIndex, rowTop ->
            val label = column.skills.getOrNull(rowIndex)
            val training = column.training.getOrNull(rowIndex) ?: Training.NONE

            marker(s, p, column.x + 5.3f, rowTop + 6.2f, 7.4f, PdfMarkerKind.SQUARE_OUTLINE)
            if (training == Training.PROFICIENT) {
                marker(s, p, column.x + 5.3f, rowTop + 6.2f, 7.2f, PdfMarkerKind.CHECK)
            } else if (training == Training.EXPERTISE) {
                marker(s, p, column.x + 5.3f, rowTop + 6.2f, 7.2f, PdfMarkerKind.DOUBLE_CHECK)
            }

            line(s, column.x + column.width - 28f, rowTop + 12.6f, column.x + column.width - 3f, rowTop + 12.6f, 0.5f)
            if (label != null) {
                // Use the full authentic gap between the source marker and numeric value rule.
                smallData(s, p, column.x + 11f, rowTop - 1f, column.width - 37f, 13f, label.first, 7.15f)
                dataBox(s, p, column.x + column.width - 27f, rowTop - 2f, 24f, 15f, label.second, 7.6f)
            }
        }
    }

    private fun writeRuledLines(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        rules: List<Float>,
        width: Float,
        values: List<String>,
    ) {
        rules.forEachIndexed { i, y ->
            dataAboveRule(s, p, Rule(x, x + width, y), values.getOrElse(i) { "" }, 8.6f)
        }
    }

    private fun sourceBands(
        s: PDPageContentStream,
        x1: Float,
        x2: Float,
        top: Float,
        rows: Int,
        step: Float,
    ) {
        repeat(rows) { i ->
            val rowTop = top + i * step
            if (i % 2 == 0) fill(s, x1, rowTop - step + 1f, x2 - x1, step - 1f, SOURCE_GRAY)
            line(s, x1, rowTop, x2, rowTop, 0.65f)
        }
    }

    private fun dataAboveRule(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        rule: Rule,
        value: String,
        size: Float,
        leftPadding: Float = 2f,
    ) {
        if (value.isBlank()) return
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = PdfRect(rule.startX + leftPadding, H - rule.topY + 2.2f, rule.endX - rule.startX - leftPadding - 1f, 14f),
                text = value,
                role = PdfTypographyRole.BODY,
                preferredSizePt = size,
                minimumSizePt = (size - 1.2f).coerceAtLeast(6.4f),
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.BOTTOM,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
                horizontalPaddingPt = 0.2f,
                verticalPaddingPt = 0.1f,
            ),
        )
        if (result.hasOverflow) overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
    }

    private fun dataLine(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        value: String,
        size: Float,
    ) {
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = PdfRect(x, H - top - 16f, width, 16f),
                text = value,
                role = PdfTypographyRole.BODY,
                preferredSizePt = size,
                minimumSizePt = (size - 1.2f).coerceAtLeast(6.4f),
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
            ),
        )
        if (result.hasOverflow) overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
    }

    private fun dataBox(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = PdfRect(x, H - top - height, width, height),
                text = value,
                role = PdfTypographyRole.NUMERIC_COMPACT,
                preferredSizePt = size,
                minimumSizePt = (size - 2f).coerceAtLeast(6f),
                horizontalAlignment = PdfHorizontalAlignment.CENTER,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
            ),
        )
        if (result.hasOverflow) overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
    }

    private fun replaceHeading(
        s: PDPageContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        white(s, x, top, width, height)
        centeredOriginal(s, font, x, top, width, height, value, size)
    }

    private fun replaceSmallLabel(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        white(s, x, top, width, height)
        smallLabel(s, p, x, top, width, height, value, size)
    }

    private fun smallLabel(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = PdfRect(x, H - top - height, width, height),
                text = value,
                role = PdfTypographyRole.COMPACT_TABLE,
                preferredSizePt = size,
                minimumSizePt = (size - 1f).coerceAtLeast(6f),
                horizontalAlignment = PdfHorizontalAlignment.CENTER,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
            ),
        )
        if (result.hasOverflow) overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
    }

    private fun smallData(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = PdfRect(x, H - top - height, width, height),
                text = value,
                role = PdfTypographyRole.BODY,
                preferredSizePt = size,
                minimumSizePt = (size - 1.5f).coerceAtLeast(5.6f),
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
                horizontalPaddingPt = 0.2f,
            ),
        )
        if (result.hasOverflow) overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
    }

    private fun centeredOriginal(
        s: PDPageContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        val textWidth = font.getStringWidth(value) / 1000f * size
        val descriptor = requireNotNull(font.fontDescriptor)
        val ascent = descriptor.ascent / 1000f * size
        val descent = descriptor.descent / 1000f * size
        val boxY = H - top - height
        val baseline = boxY + (height - (ascent - descent)) / 2f - descent
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, size)
        s.newLineAtOffset(x + (width - textWidth) / 2f, baseline)
        s.showText(value)
        s.endText()
    }

    private fun leftOriginal(
        s: PDPageContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        var actual = size
        while (actual > 5.8f && font.getStringWidth(value) / 1000f * actual > width) actual -= 0.2f
        val descriptor = requireNotNull(font.fontDescriptor)
        val ascent = descriptor.ascent / 1000f * actual
        val descent = descriptor.descent / 1000f * actual
        val boxY = H - top - height
        val baseline = boxY + (height - (ascent - descent)) / 2f - descent
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, actual)
        s.newLineAtOffset(x, baseline)
        s.showText(value)
        s.endText()
    }

    private fun loadEmbeddedSourceFont(target: PDDocument, source: PDDocument, token: String): PDFont {
        source.pages.forEach { page ->
            val resources: PDResources = page.resources ?: return@forEach
            resources.fontNames.forEach { name ->
                val font = resources.getFont(name)
                if (font.name.contains(token, ignoreCase = true)) {
                    val descriptor = requireNotNull(font.fontDescriptor)
                    val stream = descriptor.fontFile2 ?: descriptor.fontFile ?: descriptor.fontFile3
                    requireNotNull(stream) { "Source font $token is not embedded." }
                    val bytes = stream.createInputStream().use { it.readBytes() }
                    return PDType0Font.load(target, ByteArrayInputStream(bytes), false)
                }
            }
        }
        error("Embedded source font not found: $token")
    }

    private fun sourcePage(doc: PDDocument, form: PDFormXObject): PDPage =
        blankPage(doc).also { page ->
            PDPageContentStream(doc, page).use { it.drawForm(form) }
        }

    private fun blankPage(doc: PDDocument): PDPage =
        PDPage(PDRectangle(W, H)).also(doc::addPage)

    private fun white(s: PDPageContentStream, x: Float, top: Float, width: Float, height: Float) =
        fill(s, x, top, width, height, Color.WHITE)

    private fun fill(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        color: Color,
    ) {
        s.saveGraphicsState()
        s.setNonStrokingColor(color)
        s.addRect(x, H - top - height, width, height)
        s.fill()
        s.restoreGraphicsState()
    }

    private fun line(
        s: PDPageContentStream,
        x1: Float,
        top1: Float,
        x2: Float,
        top2: Float,
        width: Float,
    ) {
        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(width)
        s.moveTo(x1, H - top1)
        s.lineTo(x2, H - top2)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun marker(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        centerX: Float,
        centerTop: Float,
        size: Float,
        kind: PdfMarkerKind,
    ) {
        p.drawMarker(
            s,
            centerX = centerX,
            centerY = H - centerTop,
            sizePt = size,
            kind = kind,
            family = PdfSymbolFamily.V1_DERIVED,
        )
    }

    private fun spellRows(): List<SpellBlock> {
        fun block(level: Int, x: Float, firstTop: Float, count: Int, slotY: Float?): SpellBlock {
            val rules = (0 until count).map { i -> Rule(x + 11.3f, x + 174f, firstTop + 12.6f + i * 19.84f) }
            val centers = (0 until count).map { i -> (x + 4.9f) to (firstTop + 6.1f + i * 19.84f) }
            val slotRule = slotY?.let { Rule(x + 39f, x + 79f, it) }
            return SpellBlock(level, rules, centers, slotRule)
        }
        return listOf(
            block(0, 28.2f, 117.4f, 4, null),
            block(1, 28.2f, 327.2f, 10, 317f),
            block(2, 28.2f, 573.8f, 9, 563.7f),
            block(3, 215.3f, 117.4f, 10, 107.3f),
            block(4, 215.3f, 356.5f, 10, 346.5f),
            block(5, 215.3f, 592.6f, 8, 582.6f),
            block(6, 408.1f, 117.4f, 8, 107.3f),
            block(7, 408.1f, 315.8f, 7, 305.7f),
            block(8, 408.1f, 494.4f, 6, 484.3f),
            block(9, 408.1f, 653.2f, 5, 643.1f),
        )
    }

    private fun assertImagesEqual(expected: BufferedImage, actual: BufferedImage, message: String) {
        assertEquals(expected.width, actual.width, message)
        assertEquals(expected.height, actual.height, message)
        val a = IntArray(expected.width)
        val b = IntArray(expected.width)
        repeat(expected.height) { y ->
            expected.getRGB(0, y, expected.width, 1, a, 0, expected.width)
            actual.getRGB(0, y, actual.width, 1, b, 0, actual.width)
            assertTrue(a.contentEquals(b), "$message First differing raster row: $y")
        }
    }

    private fun resource(path: String): InputStream =
        requireNotNull(DesktopPcSheetCustomV1ExtendedFamilyRun2Test::class.java.classLoader.getResourceAsStream(path)) {
            "Missing test resource: $path"
        }

    private data class OriginalFonts(val heading: PDFont, val label: PDFont)
    private data class Rule(val startX: Float, val endX: Float, val topY: Float)
    private data class AttributeColumn(
        val x: Float,
        val width: Float,
        val gray: Boolean,
        val title: String,
        val score: String,
        val modifier: String,
        val save: String,
        val skills: List<Pair<String, String>>,
        val training: List<Training>,
    )
    private data class SpellBlock(
        val level: Int,
        val rules: List<Rule>,
        val checkboxCenters: List<Pair<Float, Float>>,
        val slotRule: Rule?,
    )
    private enum class Training { NONE, PROFICIENT, EXPERTISE }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val BASELINE_COMPARE_DPI = 120f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf"

        val SOURCE_GRAY = Color(211, 210, 210)

        val RUN2_THEME = PdfTypographyTheme(
            id = "custom-v1-extended-family-run2",
            resourcesByRole = mapOf(
                PdfTypographyRole.CHARACTER_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
                PdfTypographyRole.HANDWRITTEN_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
                PdfTypographyRole.PRIMARY_VALUE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
                PdfTypographyRole.SECONDARY_VALUE to "fonts/pdf/text/FiraSans-SemiBold.ttf",
                PdfTypographyRole.BODY to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.COMPACT_TABLE to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.NUMERIC_COMPACT to "fonts/pdf/text/FiraSans-SemiBold.ttf",
                PdfTypographyRole.NOTE_TEXT to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.SPELL_NAME to "fonts/pdf/text/FiraSans-SemiBold.ttf",
                PdfTypographyRole.OPTIONAL_DECORATIVE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
            ),
        )
    }
}
