package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDFormContentStream
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.util.Matrix

/**
 * Custom-v2 Extended owner-evaluation Run 7.
 *
 * This candidate repairs the owner-reviewed Run-6 visual defects while preserving the
 * concrete Custom-v1 Extended Run-6 solution mapping and the frozen Custom-v2 visual
 * authority. Direct v2 equivalents reuse v2 typography and geometry. Source ornaments
 * are carried through a transparent source-derived layer instead of opaque crops.
 *
 * It is deliberately not a production renderer and can reach PASS FOR OWNER REVIEW
 * only; OWNER APPROVED remains an explicit owner-only gate.
 *
 * Seven-page evaluation packet:
 *
 * 1. Custom Statistics - per Attribute;
 * 2. Custom Statistics - per Ability;
 * 3. shared Traits & Features;
 * 4. shared Resources & Options;
 * 5. shared Inventory / Equipment;
 * 6. shared Spells;
 * 7. shared Notes.
 *
 * Architecture follows the owner-approved Extended methodology:
 * STRUCTURE -> CLEANUP -> LABELS -> VALUES -> MARKERS.
 *
 * Cleanup is empty unless a bounded source interior genuinely needs occlusion.
 * Every page keeps five independently toggleable OCG layers.
 */
class DesktopPcSheetCustomV2ExtendedEvaluationRun7Test {
    @Test
    fun rendersSevenPageCustomV2ExtendedEvaluationPacket() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val pdf = File(proofDir, "custom-v2-extended-evaluation-run7.pdf")
        val templateBytes = resource(TEMPLATE).use { it.readBytes() }

        Loader.loadPDF(templateBytes).use { source ->
            PDDocument().use { doc ->
                repeat(7) { doc.addPage(PDPage(PDRectangle(W, H))) }
                val r = Resources.load(doc, source)
                val layers = LayerUtility(doc)

                renderPerAttribute(doc, layers, doc.getPage(0), r)
                renderPerAbility(doc, layers, doc.getPage(1), r)
                renderTraits(doc, layers, doc.getPage(2), r)
                renderResources(doc, layers, doc.getPage(3), r)
                renderInventory(doc, layers, doc.getPage(4), r)
                renderSpells(doc, layers, doc.getPage(5), r)
                renderNotes(doc, layers, doc.getPage(6), r)

                doc.save(pdf)
            }
        }

        Loader.loadPDF(pdf).use { doc ->
            assertEquals(7, doc.numberOfPages)
            assertFalse(doc.isEncrypted)
            val oc = assertNotNull(doc.documentCatalog.ocProperties)
            assertTrue(oc.getGroupNames().count() >= 35, "Every structural-proof page must keep five semantic layers.")

            val text = PDFTextStripper().getText(doc)
            listOf(
                "ESTADÍSTICAS PERSONALIZADAS",
                "VOLuntad",
                "TIRADAS DE SALVACIÓN",
                "HABILIDADES",
                "RASGOS Y ATRIBUTOS",
                "RECURSOS Y OPCIONES",
                "TESORO / OBJETOS / OTROS",
                "EQUIPO ESPECIAL",
            ).forEach { label ->
                assertTrue(text.contains(label), "Evaluation packet missing expected label: " + label)
            }
            assertFalse(text.contains("Especie", ignoreCase = true), "Product terminology contract is Raza, never Especie.")
            assertFalse(text.contains("Dados de portento", ignoreCase = true), "Trait-local Portento uses must not be duplicated as a CharacterResource.")
            assertTrue(text.contains("7/12"), "Evaluation must demonstrate X/Y fallback for a maximum that does not fit as squares.")
            assertFalse(text.contains("Atributo personalizado"), "Run 7 must not expose placeholder QA labels in the owner packet.")

            val renderer = PDFRenderer(doc)
            repeat(doc.numberOfPages) { index ->
                val image = renderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(ImageIO.write(image, "png", File(proofDir, "custom-v2-extended-evaluation-run7-page-" + (index + 1) + ".png")))
            }
        }

        assertTrue(pdf.length() > 20_000L)
    }

    private fun renderPerAttribute(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X ATTR - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            val columns = listOf(14f, 207f, 400f)
            columns.forEach { x ->
                fill(s, x, 104f, 184f, 244f, SOURCE_GRAY_LIGHT)
                attributeBandStructure(s, r, x, 104f, 184f, 244f, 6)
            }

            drawRule(s, 14f, 598f, 365f, 0.8f)
            columns.forEachIndexed { index, x ->
                fill(s, x, 392f, 184f, 110f, if (index % 2 == 0) SOURCE_GRAY_LIGHT else SOURCE_GRAY_DARK)
                drawRule(s, x + 16f, x + 174f, 430f, 0.55f)
                repeat(4) { row -> drawRule(s, x + 16f, x + 174f, 447f + row * 17f, 0.55f) }
            }

            drawRule(s, 14f, 598f, 522f, 0.8f)
            columns.forEachIndexed { col, x ->
                bandedRows(s, x, x + 184f, 562f, 10, 17f, col)
            }
        }
        appendLayer(doc, layers, page, "V2X ATTR - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X ATTR - LABELS") { s ->
            pageTitle(s, r, "ESTADÍSTICAS PERSONALIZADAS")
            listOf("HONor", "VOLuntad", "SUErte").forEachIndexed { index, title ->
                textTopFixedScale(s, r.corbelBold, 18f + index * 193f, 111f, title, 12.12f, SOURCE_CORBEL_ATTRIBUTE_SCALE)
                textAboveRuleFixedScale(
                    s, r.corbel,
                    Rule(108f + index * 193f, 159f + index * 193f, 151f),
                    "Tirada de Salvación", 7.75f, 2.0f, SOURCE_CORBEL_COMPACT_SCALE,
                )
            }
            centeredFixedScale(s, r.corbelBold, TopRect(14f, 367f, 584f, 22f), "HABILIDADES VINCULADAS A ATRIBUTOS ESTÁNDAR", 10.2f, SOURCE_CORBEL_HEADING_SCALE)
            listOf("INTeligencia", "DEStreza", "SABiduría").forEachIndexed { index, title ->
                centeredFixedScale(s, r.corbelBold, TopRect(14f + index * 193f, 397f, 184f, 22f), title, 12.12f, SOURCE_CORBEL_ATTRIBUTE_SCALE)
            }
            centeredFixedScale(s, r.corbelBold, TopRect(14f, 524f, 584f, 22f), "DEFINICIONES / NOTAS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
        }
        appendLayer(doc, layers, page, "V2X ATTR - VALUES") { s ->
            val samples = listOf(
                AttributeSample("15", "+2", "+5", listOf("Etiqueta" to "+5", "Reputación" to "+5", "Protocolo" to "+2")),
                AttributeSample("12", "+1", "+1", listOf("Temple" to "+4", "Concentración" to "+1")),
                AttributeSample("18", "+4", "+7", listOf("Fortuna" to "+7", "Escapismo" to "+4", "Improvisación" to "+7")),
            )
            samples.forEachIndexed { index, sample ->
                drawAttributeBandValues(s, r, 14f + index * 193f, 104f, sample)
            }

            val anchors = listOf(
                listOf("Ocultismo" to "+7", "Criptografía" to "+4"),
                listOf("Acrobacia aérea" to "+6", "Cerrajería fina" to "+9"),
                listOf("Lectura corporal" to "+4", "Orientación astral" to "+1"),
            )
            anchors.forEachIndexed { col, rows ->
                rows.forEachIndexed { row, item ->
                    val y = 447f + row * 17f
                    textAboveRuleFixedScale(s, r.corbel, Rule(38f + col * 193f, 164f + col * 193f, y), item.first, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE)
                    centeredAboveRule(s, r.firaSemibold, Rule(164f + col * 193f, 196f + col * 193f, y), item.second, 8.8f, 2.2f)
                }
            }

            val notes = listOf(
                listOf("HON: presencia, reputación y protocolo.", "No sustituye CARisma salvo regla explícita."),
                listOf("VOL: temple, foco y resistencia mental.", "Las pruebas prolongadas pueden exigir concentración."),
                listOf("SUE: fortuna, azar e improvisación.", "La fortuna puede modificar consecuencias imprevistas."),
            )
            notes.forEachIndexed { col, rows ->
                rows.forEachIndexed { row, value ->
                    textAboveRule(s, r.fira, Rule(18f + col * 193f, 194f + col * 193f, 562f + row * 17f), value, 8.8f, 7.4f, 2.2f)
                }
            }
        }
        appendLayer(doc, layers, page, "V2X ATTR - MARKERS") { s ->
            repeat(3) { col ->
                drawV2TrainingBox(s, r.symbol, TopRect(98.5f + col * 193f, 141.5f, 8.5f, 9f), Training.PROFICIENT)
                repeat(6) { row ->
                    val training = when {
                        col == 0 && row == 0 -> Training.PROFICIENT
                        col == 1 && row == 0 -> Training.EXPERTISE
                        col == 2 && row == 0 -> Training.PROFICIENT
                        else -> Training.NONE
                    }
                    drawV2TrainingBox(s, r.symbol, TopRect(98.5f + col * 193f, 157f + row * 17f, 8.5f, 9f), training)
                }
            }
            repeat(3) { col ->
                repeat(4) { row ->
                    val training = when {
                        row == 0 -> Training.PROFICIENT
                        col == 1 && row == 1 -> Training.EXPERTISE
                        else -> Training.NONE
                    }
                    drawV2TrainingBox(s, r.symbol, TopRect(18f + col * 193f, 434f + row * 17f, 8.5f, 9f), training)
                }
            }
        }
    }

    private fun renderPerAbility(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X ABILITY - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            fill(s, 14f, 104f, 174f, 30f, SOURCE_GRAY_LIGHT)
            fill(s, 202f, 104f, 150f, 30f, SOURCE_GRAY_LIGHT)
            fill(s, 366f, 104f, 232f, 30f, SOURCE_GRAY_LIGHT)

            repeat(6) { index ->
                val top = 136f + index * 96f
                fill(s, 14f, top, 174f, 94f, if (index % 2 == 0) SOURCE_GRAY_LIGHT else SOURCE_GRAY_DARK)
                drawAttributeOrnament(s, r.attributeOrnament, 14.3f, top + 24f)
                drawRule(s, 22f, 180f, top + 94f, 0.55f)
            }
            bandedRows(s, 202f, 352f, 154f, 30, 17f, 1)
            bandedRows(s, 366f, 598f, 154f, 34, 17f, 0)
        }
        appendLayer(doc, layers, page, "V2X ABILITY - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X ABILITY - LABELS") { s ->
            pageTitle(s, r, "ESTADÍSTICAS PERSONALIZADAS")
            centeredFixedScale(s, r.corbelBold, TopRect(14f, 108f, 174f, 22f), "ATRIBUTOS", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, r.corbelBold, TopRect(202f, 108f, 150f, 22f), "TIRADAS DE SALVACIÓN", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, r.corbelBold, TopRect(366f, 108f, 232f, 22f), "HABILIDADES", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            listOf("HONor", "VOLuntad", "SUErte").forEachIndexed { index, title ->
                textTopFixedScale(s, r.corbelBold, 14f, 142f + index * 96f, title, 12.12f, SOURCE_CORBEL_ATTRIBUTE_SCALE)
            }
        }
        appendLayer(doc, layers, page, "V2X ABILITY - VALUES") { s ->
            val scores = listOf("15" to "+2", "12" to "+1", "18" to "+4")
            scores.forEachIndexed { index, pair ->
                val ornamentTop = 160f + index * 96f
                centered(s, r.firaSemibold, TopRect(31.8f, ornamentTop + 8f, 25.5f, 18f), pair.first, 17f)
                centered(s, r.firaSemibold, TopRect(62.3f, ornamentTop + 27f, 23f, 16.5f), pair.second, 15.5f)
            }

            val saves = listOf("HONor" to "+5", "VOLuntad" to "+1", "SUErte" to "+7")
            saves.forEachIndexed { index, item ->
                val y = 154f + index * 17f
                textAboveRuleFixedScale(s, r.corbel, Rule(235f, 308f, y), item.first, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE)
                centeredAboveRule(s, r.firaSemibold, Rule(308f, 342f, y), item.second, 8.8f, 2.2f)
            }

            val abilities = listOf(
                "Etiqueta (HON)" to "+5",
                "Reputación (HON)" to "+5",
                "Temple (VOL)" to "+4",
                "Fortuna (SUE)" to "+7",
                "Ocultismo (INT)" to "+7",
                "Acrobacia aérea (DES)" to "+6",
                "Lectura corporal (SAB)" to "+4",
            )
            abilities.forEachIndexed { index, item ->
                val y = 154f + index * 17f
                textAboveRuleFixedScale(s, r.corbel, Rule(399f, 548f, y), item.first, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE)
                centeredAboveRule(s, r.firaSemibold, Rule(548f, 588f, y), item.second, 8.8f, 2.2f)
            }
        }
        appendLayer(doc, layers, page, "V2X ABILITY - MARKERS") { s ->
            repeat(30) { row ->
                val training = if (row < 3) Training.PROFICIENT else Training.NONE
                drawV2TrainingBox(s, r.symbol, TopRect(220f, 142f + row * 17f, 8.5f, 9f), training)
            }
            repeat(34) { row ->
                val training = when (row) {
                    0, 1, 3, 4, 5, 6 -> Training.PROFICIENT
                    2 -> Training.EXPERTISE
                    else -> Training.NONE
                }
                drawV2TrainingBox(s, r.symbol, TopRect(384f, 142f + row * 17f, 8.5f, 9f), training)
            }
        }
    }

    private fun renderTraits(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X TRAITS - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            fill(s, 14f, 96f, 277f, 24f, SOURCE_GRAY_LIGHT)
            fill(s, 307f, 96f, 291f, 24f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 291f, 137f, 35, 17f, 0)
            bandedRows(s, 307f, 598f, 137f, 35, 17f, 1)
            drawRule(s, 14f, 291f, 358f, 0.8f)
            drawRule(s, 307f, 598f, 358f, 0.8f)
            drawRule(s, 14f, 291f, 579f, 0.8f)
            drawRule(s, 307f, 598f, 579f, 0.8f)
        }
        appendLayer(doc, layers, page, "V2X TRAITS - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X TRAITS - LABELS") { s ->
            pageTitle(s, r, "RASGOS Y ATRIBUTOS")
            centeredFixedScale(s, r.corbelBold, TopRect(14f, 98f, 277f, 20f), "CLASE / DOTES", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, r.corbelBold, TopRect(307f, 98f, 291f, 20f), "RAZA / TRASFONDO / OTROS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            textTop(s, r.corbelBold, 18f, 365f, "OTROS RASGOS", 9.5f)
            textTop(s, r.corbelBold, 311f, 365f, "DETALLES / NOTAS", 9.5f)
            textTop(s, r.corbelBold, 18f, 586f, "COMPETENCIAS / IDIOMAS", 9.5f)
            textTop(s, r.corbelBold, 311f, 586f, "CONTINUACIÓN", 9.5f)
        }
        appendLayer(doc, layers, page, "V2X TRAITS - VALUES") { s ->
            featureEntry(s, r, 14f, 137f, 277f, "Portento", "Mago / Adivinación · Pasivo", "Usos: 1 / 2 · Descanso largo", "Puede sustituir una tirada apropiada por uno de los resultados registrados.")
            featureEntry(s, r, 14f, 239f, 277f, "Acción astuta", "Pícaro · Acción adicional", null, "Correr, Destrabarse u Ocultarse como acción adicional.")
            featureEntry(s, r, 307f, 137f, 291f, "Ascendencia feérica", "Raza · Elfo alto", null, "Ventaja contra ser hechizado; la magia no puede dormir al personaje.")
            featureEntry(s, r, 307f, 239f, 291f, "Investigador", "Trasfondo", null, "Sabe dónde buscar información académica y cómo acceder a archivos especializados.")

            listOf("Recuperación arcana", "Observador", "Mente aguda", "Afinidad ritual").forEachIndexed { i, value ->
                textAboveRule(s, r.fira, Rule(18f, 287f, 392f + i * 17f), value, 8.1f, 6.5f, 2.2f)
            }
            listOf(
                "Portento: conserva dos resultados al finalizar un descanso largo.",
                "Investigador: conoce archivos, contactos y fuentes académicas.",
                "Afinidad ritual: mantiene procedimientos y anotaciones arcanas.",
                "Observador: destaca detalles sutiles en escenas y documentos.",
            ).forEachIndexed { i, value ->
                textAboveRule(s, r.fira, Rule(311f, 594f, 392f + i * 17f), value, 7.7f, 6.2f, 2.2f)
            }
            listOf("Herramientas de ladrón", "Caligrafía", "Común", "Élfico", "Dracónico").forEachIndexed { i, value ->
                textAboveRule(s, r.fira, Rule(18f, 287f, 613f + i * 17f), value, 8.0f, 6.4f, 2.2f)
            }
            listOf("Código de campo: registra rutas, sellos y peligros.", "Memoria de archivo: conserva referencias y nombres clave.", "Contacto de Academia: acceso limitado a especialistas.").forEachIndexed { i, value ->
                textAboveRule(s, r.fira, Rule(311f, 594f, 613f + i * 17f), value, 9.0f, 8.2f, 2.3f)
            }
        }
        appendLayer(doc, layers, page, "V2X TRAITS - MARKERS") { }
    }

    private fun renderResources(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X RESOURCES - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            fill(s, 14f, 96f, 584f, 22f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 598f, 150f, 10, 17f, 0)
            listOf(222f, 352f, 475f).forEach { x -> verticalRule(s, x, 120f, 303f, 0.45f) }

            drawRule(s, 14f, 598f, 329f, 0.8f)
            fill(s, 14f, 337f, 584f, 22f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 598f, 398f, 18, 17f, 1)
            listOf(30f, 118f, 258f).forEach { x -> verticalRule(s, x, 362f, 704f, 0.45f) }
        }
        appendLayer(doc, layers, page, "V2X RESOURCES - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X RESOURCES - LABELS") { s ->
            pageTitle(s, r, "RECURSOS Y OPCIONES")
            centeredFixedScale(s, r.corbelBold, TopRect(14f, 97f, 584f, 20f), "RECURSOS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabelFira(s, r, 14f, 121f, 208f, "RECURSO")
            tableLabelFira(s, r, 222f, 121f, 130f, "ACTUAL / MÁX.")
            tableLabelFira(s, r, 352f, 121f, 123f, "RESTABLECE")
            tableLabelFira(s, r, 475f, 121f, 123f, "ORIGEN")

            centeredFixedScale(s, r.corbelBold, TopRect(14f, 338f, 584f, 20f), "OPCIONES", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabelFira(s, r, 14f, 364f, 16f, "")
            tableLabelFira(s, r, 30f, 364f, 88f, "TIPO")
            tableLabelFira(s, r, 118f, 364f, 140f, "OPCIÓN")
            tableLabelFira(s, r, 258f, 364f, 340f, "DESCRIPCIÓN / COSTE")
        }
        appendLayer(doc, layers, page, "V2X RESOURCES - VALUES") { s ->
            val rows = listOf(
                ResourceSample("Recuperación arcana", 1, 1, "Descanso largo", "Mago", ""),
                ResourceSample("Carga del monóculo", 2, 4, "Amanecer", "Objeto", ""),
                ResourceSample("Reserva de inspiración", 1, 3, "Sesión", "Regla", ""),
                ResourceSample("Puntos de enfoque", 7, 12, "Descanso largo", "Clase", ""),
            )
            rows.forEachIndexed { index, row ->
                val ruleTop = 150f + index * 17f
                textAboveRule(s, r.fira, Rule(18f, 218f, ruleTop), row.name, 9.0f, 8.2f, 2.3f)
                if (row.maximum >= 10) {
                    centeredAboveRule(s, r.firaSemibold, Rule(226f, 348f, ruleTop), row.current.toString() + "/" + row.maximum, 8.5f, 2.2f)
                }
                textAboveRule(s, r.fira, Rule(356f, 471f, ruleTop), row.recovery, 8.5f, 7.5f, 2.3f)
                textAboveRule(s, r.fira, Rule(479f, 594f, ruleTop), row.source, 8.5f, 7.5f, 2.3f)
            }

            val options = listOf(
                listOf("Clase", "Tradición de Adivinación", "Portento y capacidades relacionadas."),
                listOf("Técnica", "Metamagia improvisada", "Modifica una conjuración puntual · 2 puntos."),
                listOf("Competencia", "Herramientas de ladrón", "Cerraduras, trampas y mecanismos."),
                listOf("Objeto", "Monóculo rúnico", "Inspección arcana fina · 1 carga."),
                listOf("Trasfondo", "Investigador", "Acceso a redes y fuentes académicas."),
            )
            options.forEachIndexed { index, row ->
                val y = 398f + index * 17f
                textAboveRule(s, r.fira, Rule(34f, 114f, y), row[0], 8.5f, 7.5f, 2.3f)
                textAboveRuleScaled(s, r.fira, Rule(122f, 254f, y), row[1], 8.5f, 7.5f, 2.3f, 72f)
                textAboveRule(s, r.fira, Rule(262f, 594f, y), row[2], 8.5f, 7.4f, 2.3f)
            }
        }
        appendLayer(doc, layers, page, "V2X RESOURCES - MARKERS") { s ->
            val counters = listOf(1 to 1, 2 to 4, 1 to 3, 7 to 12)
            counters.forEachIndexed { row, pair ->
                if (pair.second < 10) {
                    drawSquareCounter(s, r.symbol, 236f, 141.5f + row * 17f, pair.first, pair.second)
                }
            }
            repeat(18) { row ->
                val state = if (row < 5) Training.PROFICIENT else Training.NONE
                drawV2TrainingBox(s, r.symbol, TopRect(16f, 386f + row * 17f, 8.5f, 9f), state)
            }
        }
    }

    private fun renderInventory(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X INVENTORY - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            fill(s, 14f, 96f, 411f, 22f, SOURCE_GRAY_LIGHT)
            fill(s, 431f, 96f, 167f, 22f, SOURCE_GRAY_LIGHT)

            val equipmentCols = listOf(14f to 147f, 153f to 286f, 292f to 425f)
            equipmentCols.forEachIndexed { index, col ->
                bandedRows(s, col.first, col.second, 139f, 19, 17f, index)
            }
            bandedRows(s, 431f, 598f, 139f, 19, 17f, 1)

            drawRule(s, 14f, 598f, 480f, 0.8f)
            fill(s, 14f, 488f, 584f, 22f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 598f, 548f, 12, 17f, 0)
            listOf(30f, 130f, 310f).forEach { x -> verticalRule(s, x, 512f, 752f, 0.45f) }
        }
        appendLayer(doc, layers, page, "V2X INVENTORY - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X INVENTORY - LABELS") { s ->
            pageTitle(s, r, "INVENTARIO / EQUIPO")
            centeredFixedScale(s, r.corbelBold, TopRect(14f, 97f, 411f, 20f), "EQUIPO - CONTINUACIÓN", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, r.corbelBold, TopRect(431f, 97f, 167f, 20f), "TESORO / OBJETOS / OTROS", 10.2f, SOURCE_CORBEL_HEADING_SCALE)

            centeredFixedScale(s, r.corbelBold, TopRect(14f, 489f, 584f, 20f), "EQUIPO ESPECIAL", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabelFira(s, r, 14f, 514f, 16f, "")
            tableLabelFira(s, r, 30f, 514f, 100f, "UBICACIÓN")
            tableLabelFira(s, r, 130f, 514f, 180f, "NOMBRE")
            tableLabelFira(s, r, 310f, 514f, 288f, "DESCRIPCIÓN / ESTADO")
        }
        appendLayer(doc, layers, page, "V2X INVENTORY - VALUES") { s ->
            val columns = listOf(
                listOf("12 x Clavos de hierro", "8 x Tiza blanca", "6 x Viales vacíos", "2 x Tinta azul", "Espejo de acero", "Manta de viaje"),
                listOf("Cuaderno de campo", "Cuerda de seda", "Linterna cubierta", "4 x Aceite", "Mapa del Valle", "Tubo de mapas"),
                listOf("Pluma fina", "Sellos de cera", "Guantes finos", "Piedra de afilar", "Raciones x5", "Odre"),
            )
            columns.forEachIndexed { col, values ->
                val x1 = listOf(18f, 157f, 296f)[col]
                val x2 = listOf(143f, 282f, 421f)[col]
                values.forEachIndexed { row, value ->
                    textAboveRule(s, r.fira, Rule(x1, x2, 139f + row * 17f), value, 9.0f, 8.2f, 2.3f)
                }
            }

            listOf(
                "Gema lunar tallada · 120 po",
                "Broche élfico · 75 po",
                "Láminas de plata · 45 po",
                "Mosaico del sello azul · 25 po",
            ).forEachIndexed { row, value ->
                textAboveRule(s, r.fira, Rule(435f, 594f, 139f + row * 17f), value, 8.5f, 7.5f, 2.3f)
            }

            val special = listOf(
                Triple("Cabeza", "Monóculo rúnico", "Sintonizado · 2 de 4 cargas."),
                Triple("Cuello", "Amuleto de Liria", "Recuerdo y foco ceremonial."),
                Triple("Cinturón", "Daga de plata", "Equipada · hoja tratada."),
                Triple("Manos", "Guante del escriba", "Estabiliza la mano."),
                Triple("Mochila", "Brazal de cobre", "Reserva una carga menor."),
            )
            special.forEachIndexed { row, item ->
                val y = 548f + row * 17f
                textAboveRule(s, r.fira, Rule(34f, 126f, y), item.first, 8.5f, 7.5f, 2.3f)
                textAboveRule(s, r.fira, Rule(134f, 306f, y), item.second, 8.8f, 7.8f, 2.3f)
                textAboveRule(s, r.fira, Rule(314f, 594f, y), item.third, 8.5f, 7.5f, 2.3f)
            }
        }
        appendLayer(doc, layers, page, "V2X INVENTORY - MARKERS") { s ->
            repeat(12) { row ->
                drawV2TrainingBox(
                    s, r.symbol,
                    TopRect(16f, 536f + row * 17f, 8.5f, 9f),
                    if (row < 3) Training.PROFICIENT else Training.NONE,
                )
            }
        }
    }

    private fun renderSpells(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X SPELLS - STRUCTURE") { s ->
            s.drawForm(r.forms[3])
        }
        appendLayer(doc, layers, page, "V2X SPELLS - CLEANUP") { s ->
            continuationSpellHeaderMasks().forEach { region ->
                fill(s, region.x, region.top, region.width, region.height, Color.WHITE)
            }
        }
        appendLayer(doc, layers, page, "V2X SPELLS - LABELS") { }
        appendLayer(doc, layers, page, "V2X SPELLS - VALUES") { s ->
            val samples = listOf(
                SpellSample(Rule(25.5f, 203.5f, 375f), "Identificar"),
                SpellSample(Rule(25.5f, 203.5f, 392f), "Alarma"),
                SpellSample(Rule(221f, 399f, 142.5f), "Contrahechizo superior"),
                SpellSample(Rule(416.5f, 594.5f, 327f), "Teletransportar"),
                SpellSample(Rule(416.5f, 594.5f, 508.5f), "Mente en blanco"),
            )
            samples.forEach { textAboveRule(s, r.fira, it.rule, it.name, 9.25f, 8.0f, 2.8f) }
        }
        appendLayer(doc, layers, page, "V2X SPELLS - MARKERS") { s ->
            listOf(
                TopRect(14f, 363f, 8.5f, 8.5f),
                TopRect(14f, 380f, 8.5f, 8.5f),
                TopRect(209.5f, 130f, 8.5f, 8.5f),
                TopRect(405f, 314.5f, 8.5f, 8.5f),
            ).forEach { glyphInRect(s, r.symbol, 0xE211, it, 0.5f, 0.5f) }
        }
    }

    private fun renderNotes(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X NOTES - STRUCTURE") { s ->
            s.drawForm(r.forms[4])
        }
        appendLayer(doc, layers, page, "V2X NOTES - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X NOTES - LABELS") { }
        appendLayer(doc, layers, page, "V2X NOTES - VALUES") { s ->
            val left = listOf(
                "Retomar las notas de campaña desde la última entrada del archivo.",
                "La puerta con sello azul coincide con las monedas halladas en la torre.",
                "Comparar el alfabeto parcial con las notas del profesor Vael.",
            )
            val right = listOf(
                "Ruta: entrada oeste, cámara de columnas, escalera rota y galería azul.",
                "Materiales: tinta, tiza, viales, espejo, cuerda y una linterna adicional.",
            )
            left.forEachIndexed { i, line -> textAboveRule(s, r.fira, Rule(14f, 302.5f, 104f + i * 17f), line, 9.25f, 8.5f, 2.8f) }
            right.forEachIndexed { i, line -> textAboveRule(s, r.fira, Rule(309f, 597.5f, 104f + i * 17f), line, 9.25f, 8.5f, 2.8f) }
        }
        appendLayer(doc, layers, page, "V2X NOTES - MARKERS") { }
    }

    private fun continuationSpellHeaderMasks(): List<TopRect> = listOf(
        TopRect(76f, 304f, 129f, 43f),
        TopRect(76f, 536f, 129f, 43f),
        TopRect(271f, 71f, 129f, 43f),
        TopRect(271f, 304f, 129f, 43f),
        TopRect(271f, 536f, 129f, 43f),
        TopRect(466f, 71f, 132f, 43f),
        TopRect(466f, 256f, 132f, 43f),
        TopRect(466f, 437f, 132f, 43f),
        TopRect(466f, 604f, 132f, 43f),
    )

    private fun pageHeaderStructure(s: PDFormContentStream, logoSource: PDFormXObject) {
        drawSourceCrop(s, logoSource, 14f, 16f, 105f, 60f)
        drawRule(s, 126f, 598f, 79f, 0.6f)
    }

    private fun pageTitle(s: PDFormContentStream, r: Resources, title: String) {
        centeredFixedScale(s, r.corbelBold, TopRect(126f, 28f, 472f, 34f), title, 12.12f, SOURCE_CORBEL_HEADING_SCALE)
    }

    private fun attributeBandStructure(
        s: PDFormContentStream,
        r: Resources,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        rows: Int,
    ) {
        drawAttributeOrnament(s, r.attributeOrnament, x + 0.3f, top + 34f)
        drawRule(s, x + 94f, x + width - 9f, top + 47f, 0.65f)
        repeat(rows) { row -> drawRule(s, x + 94f, x + width - 9f, top + 64f + row * 17f, 0.55f) }
    }

    private fun compactAttributeStructure(
        s: PDFormContentStream,
        r: Resources,
        x: Float,
        top: Float,
        width: Float,
    ) {
        drawAttributeOrnament(s, r.attributeOrnament, x + 0.3f, top + 33f)
        drawRule(s, x + 8f, x + width - 8f, top + 106f, 0.55f)
    }

    private fun drawAttributeOrnament(
        s: PDFormContentStream,
        ornament: PDImageXObject,
        targetX: Float,
        targetTop: Float,
    ) {
        s.drawImage(
            ornament,
            targetX,
            H - targetTop - ATTRIBUTE_ORNAMENT_HEIGHT,
            ATTRIBUTE_ORNAMENT_WIDTH,
            ATTRIBUTE_ORNAMENT_HEIGHT,
        )
    }

    private fun attributeModuleStructure(s: PDFormContentStream, x: Float, top: Float, width: Float, height: Float, rows: Int) {
        drawRect(s, x, top, width, height, 0.75f)
        drawRect(s, x + 14f, top + 34f, 58.5f, 28f, 0.65f)
        drawOval(s, x + 101f, top + 36f, 44f, 22.5f, 0.65f)
        drawRule(s, x + 15f, x + width - 10f, top + 83f, 0.65f)
        repeat(rows) { row -> drawRule(s, x + 15f, x + width - 10f, top + 113f + row * 17f, 0.55f) }
    }

    private fun statCardStructure(s: PDFormContentStream, x: Float, top: Float, width: Float) {
        drawRect(s, x, top, width, 108f, 0.75f)
        drawRect(s, x + 14f, top + 31f, 64f, 35f, 0.65f)
        drawOval(s, x + 100f, top + 36f, 52f, 25f, 0.65f)
    }

    private fun drawAttributeBandValues(s: PDFormContentStream, r: Resources, x: Float, top: Float, sample: AttributeSample) {
        centered(s, r.firaSemibold, TopRect(x + 17.8f, top + 42f, 25.5f, 18f), sample.score, 17f)
        centered(s, r.firaSemibold, TopRect(x + 48.3f, top + 61f, 23f, 16.5f), sample.modifier, 15.5f)
        centeredAboveRule(s, r.firaSemibold, Rule(x + 145f, x + 174f, top + 47f), sample.save, 8.8f, 2.0f)
        sample.skills.forEachIndexed { row, item ->
            val y = top + 64f + row * 17f
            textAboveRuleFixedScale(s, r.corbel, Rule(x + 94f, x + 145f, y), item.first, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE)
            centeredAboveRule(s, r.firaSemibold, Rule(x + 145f, x + 174f, y), item.second, 8.8f, 2.2f)
        }
    }

    private fun drawAttributeValues(s: PDFormContentStream, r: Resources, x: Float, top: Float, sample: AttributeSample) {
        centered(s, r.firaSemibold, TopRect(x + 14f, top + 34f, 58.5f, 28f), sample.score, 17f)
        centered(s, r.firaSemibold, TopRect(x + 101f, top + 36f, 44f, 22.5f), sample.modifier, 14.5f)
        centeredAboveRule(s, r.firaSemibold, Rule(x + 145f, x + 174f, top + 83f), sample.save, 8.4f, 2.3f)
        sample.skills.forEachIndexed { row, item ->
            val y = top + 113f + row * 17f
            textAboveRule(s, r.fira, Rule(x + 31f, x + 145f, y), item.first, 8.0f, 6.5f, 2.3f)
            centeredAboveRule(s, r.firaSemibold, Rule(x + 145f, x + 174f, y), item.second, 8.2f, 2.3f)
        }
    }

    private fun featureEntry(s: PDFormContentStream, r: Resources, x: Float, top: Float, width: Float, name: String, meta: String, uses: String?, description: String) {
        textAboveRule(s, r.firaSemibold, Rule(x + 4f, x + width - 4f, top), name, 9.0f, 7.4f, 2.7f)
        textAboveRule(s, r.fira, Rule(x + 4f, x + width - 4f, top + 17f), meta, 7.3f, 6.2f, 2.5f)
        val descTop = if (uses != null) top + 51f else top + 34f
        uses?.let { textAboveRule(s, r.fira, Rule(x + 4f, x + width - 4f, top + 34f), it, 7.3f, 6.2f, 2.5f) }
        wrapByWidth(r.fira, description, 7.4f, width - 8f).take(3).forEachIndexed { index, line ->
            textAboveRule(s, r.fira, Rule(x + 4f, x + width - 4f, descTop + index * 17f), line, 7.4f, 6.2f, 2.5f)
        }
    }

    private fun maskCombinedOrnamentValues(
        s: PDFormContentStream,
        x: Float,
        top: Float,
    ) {
        // Measured safe white interiors of the complete imported ornament. These masks
        // remove only the source's "10" and "+0"; they remain well inside all borders.
        fill(s, x + 17.5f, top + 8.0f, 25.5f, 18.0f, Color.WHITE)
        fill(s, x + 48.0f, top + 27.0f, 23.0f, 16.5f, Color.WHITE)
    }

    private fun drawV2TrainingBox(
        s: PDFormContentStream,
        font: PDFont,
        rect: TopRect,
        training: Training,
    ) {
        val cp = when (training) {
            Training.NONE -> 0xE303
            Training.PROFICIENT -> 0xE313
            Training.EXPERTISE -> 0xE314
        }
        glyphInRect(s, font, cp, rect, 0.7f, 0.7f)
    }

    private fun drawSquareCounter(s: PDFormContentStream, font: PDFont, startX: Float, centerTop: Float, current: Int, maximum: Int) {
        require(maximum in 1..9)
        require(current in 0..maximum)
        repeat(maximum) { index ->
            val cp = if (index < current) 0xE304 else 0xE303
            glyphInRect(s, font, cp, TopRect(startX + index * 13f, centerTop - 5f, 10f, 10f), 0.7f, 0.7f)
        }
    }

    private fun tableLabelFira(s: PDFormContentStream, r: Resources, x: Float, top: Float, width: Float, label: String) {
        if (label.isNotBlank()) centeredFixedScale(s, r.corbel, TopRect(x, top, width, 18f), label, 7.79f, SOURCE_CORBEL_TABLE_SCALE)
    }

    private fun bandedRows(
        s: PDFormContentStream,
        x1: Float,
        x2: Float,
        firstRuleTop: Float,
        rows: Int,
        step: Float,
        phase: Int,
    ) {
        repeat(rows) { row ->
            val ruleTop = firstRuleTop + row * step
            val tone = if ((row + phase) % 2 == 0) SOURCE_GRAY_DARK else SOURCE_GRAY_LIGHT
            fill(s, x1, ruleTop - step + 0.5f, x2 - x1, step - 0.5f, tone)
            drawRule(s, x1, x2, ruleTop, 0.55f)
        }
    }

    private fun appendLayer(
        doc: PDDocument,
        layers: LayerUtility,
        page: PDPage,
        name: String,
        draw: (PDFormContentStream) -> Unit,
    ) {
        val form = PDFormXObject(doc).apply {
            resources = PDResources()
            setBBox(PDRectangle(page.cropBox.width, page.cropBox.height))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            stream.setStrokingColor(Color.BLACK)
            draw(stream)
        }
        layers.appendFormAsLayer(page, form, AffineTransform(), name)
    }

    private fun drawSourceCrop(s: PDFormContentStream, form: PDFormXObject, x: Float, top: Float, width: Float, height: Float) {
        s.saveGraphicsState()
        s.addRect(x, H - top - height, width, height)
        s.clip()
        s.drawForm(form)
        s.restoreGraphicsState()
    }

    private fun drawTranslatedSourceCrop(
        s: PDFormContentStream,
        form: PDFormXObject,
        sourceX: Float,
        sourceTop: Float,
        width: Float,
        height: Float,
        targetX: Float,
        targetTop: Float,
    ) {
        s.saveGraphicsState()
        s.addRect(targetX, H - targetTop - height, width, height)
        s.clip()
        s.transform(Matrix.getTranslateInstance(targetX - sourceX, sourceTop - targetTop))
        s.drawForm(form)
        s.restoreGraphicsState()
    }

    private fun fill(s: PDFormContentStream, x: Float, top: Float, width: Float, height: Float, color: Color) {
        s.saveGraphicsState()
        s.setNonStrokingColor(color)
        s.addRect(x, H - top - height, width, height)
        s.fill()
        s.restoreGraphicsState()
    }

    private fun drawRule(s: PDFormContentStream, x1: Float, x2: Float, top: Float, width: Float) {
        s.saveGraphicsState()
        s.setLineWidth(width)
        s.moveTo(x1, H - top)
        s.lineTo(x2, H - top)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun verticalRule(s: PDFormContentStream, x: Float, top: Float, bottomTop: Float, width: Float) {
        s.saveGraphicsState()
        s.setLineWidth(width)
        s.moveTo(x, H - top)
        s.lineTo(x, H - bottomTop)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun drawRect(s: PDFormContentStream, x: Float, top: Float, width: Float, height: Float, stroke: Float) {
        s.saveGraphicsState()
        s.setLineWidth(stroke)
        s.addRect(x, H - top - height, width, height)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun drawOval(s: PDFormContentStream, x: Float, top: Float, width: Float, height: Float, stroke: Float) {
        val k = 0.5522848f
        val left = x
        val right = x + width
        val bottom = H - top - height
        val topY = bottom + height
        val cx = x + width / 2f
        val cy = bottom + height / 2f
        val rx = width / 2f
        val ry = height / 2f
        s.saveGraphicsState()
        s.setLineWidth(stroke)
        s.moveTo(cx + rx, cy)
        s.curveTo(cx + rx, cy + k * ry, cx + k * rx, cy + ry, cx, cy + ry)
        s.curveTo(cx - k * rx, cy + ry, cx - rx, cy + k * ry, cx - rx, cy)
        s.curveTo(cx - rx, cy - k * ry, cx - k * rx, cy - ry, cx, cy - ry)
        s.curveTo(cx + k * rx, cy - ry, cx + rx, cy - k * ry, cx + rx, cy)
        s.closePath()
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun textTop(s: PDFormContentStream, font: PDFont, x: Float, top: Float, text: String, size: Float) {
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(x, H - top - ascent)
        s.showText(text)
        s.endText()
    }

    private fun textTopFixedScale(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        text: String,
        size: Float,
        horizontalScale: Float,
    ) {
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
        s.newLineAtOffset(x, H - top - ascent)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun textAboveRuleFixedScale(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        size: Float,
        clearance: Float,
        horizontalScale: Float,
    ) {
        val available = rule.endX - rule.startX - 2f
        val scaledWidth = textWidth(font, text, size) * horizontalScale / 100f
        require(scaledWidth <= available + 0.05f) {
            "Source-matched text does not fit: " + text + " (" + scaledWidth + " > " + available + ")"
        }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
        s.newLineAtOffset(rule.startX + 1f, baseline)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun centeredFixedScale(
        s: PDFormContentStream,
        font: PDFont,
        rect: TopRect,
        text: String,
        size: Float,
        horizontalScale: Float,
    ) {
        val width = textWidth(font, text, size) * horizontalScale / 100f
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        val descent = abs(font.fontDescriptor?.descent?.takeIf { it < 0 } ?: -250f) / 1000f * size
        val bottom = H - (rect.top + rect.height)
        val baseline = bottom + (rect.height - ascent - descent) / 2f + descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
        s.newLineAtOffset(rect.x + (rect.width - width) / 2f, baseline)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun textAboveRule(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        clearance: Float,
    ) {
        var size = preferredSize
        val available = rule.endX - rule.startX - 2f
        while (size > minimumSize && textWidth(font, text, size) > available) size -= 0.25f
        require(textWidth(font, text, size) <= available + 0.05f) { "Text does not fit: " + text }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + 1f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun textAboveRuleScaled(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        clearance: Float,
        minimumHorizontalScale: Float,
    ) {
        var size = preferredSize
        val available = rule.endX - rule.startX - 2f
        while (size > minimumSize && textWidth(font, text, size) > available / (minimumHorizontalScale / 100f)) {
            size -= 0.25f
        }
        val rawWidth = textWidth(font, text, size)
        val scale = minOf(100f, available / rawWidth * 100f)
        require(scale >= minimumHorizontalScale) {
            "Compact v2 label requires excessive compression: " + text + " (" + scale + "%)"
        }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(scale)
        s.newLineAtOffset(rule.startX + 1f, baseline)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun centeredAboveRule(s: PDFormContentStream, font: PDFont, rule: Rule, text: String, size: Float, clearance: Float) {
        val width = textWidth(font, text, size)
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + (rule.endX - rule.startX - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun centered(s: PDFormContentStream, font: PDFont, rect: TopRect, text: String, size: Float) {
        val width = textWidth(font, text, size)
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        val descent = abs(font.fontDescriptor?.descent?.takeIf { it < 0 } ?: -250f) / 1000f * size
        val bottom = H - (rect.top + rect.height)
        val baseline = bottom + (rect.height - ascent - descent) / 2f + descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rect.x + (rect.width - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun glyphInRect(
        s: PDFormContentStream,
        font: PDFont,
        cp: Int,
        rect: TopRect,
        insetX: Float,
        insetY: Float,
    ) {
        val glyph = String(Character.toChars(cp))
        val normalizedWidth = font.getStringWidth(glyph) / 1000f
        val descriptor = requireNotNull(font.fontDescriptor)
        val normalizedAscent = descriptor.ascent / 1000f
        val normalizedDescent = descriptor.descent / 1000f
        val normalizedHeight = normalizedAscent - normalizedDescent
        val targetWidth = rect.width - insetX * 2f
        val targetHeight = rect.height - insetY * 2f
        val scaleX = targetWidth / normalizedWidth
        val scaleY = targetHeight / normalizedHeight
        val left = rect.x + insetX
        val targetBottom = H - (rect.top + rect.height) + insetY
        val baseline = targetBottom - normalizedDescent * scaleY
        s.beginText()
        s.setFont(font, 1f)
        s.setTextMatrix(Matrix(scaleX, 0f, 0f, scaleY, left, baseline))
        s.showText(glyph)
        s.endText()
    }

    private fun wrapByWidth(font: PDFont, text: String, size: Float, maxWidth: Float): List<String> {
        val out = mutableListOf<String>()
        var current = ""
        text.trim().split(Regex("\\s+")).forEach { word ->
            val candidate = if (current.isBlank()) word else current + " " + word
            if (textWidth(font, candidate, size) <= maxWidth) current = candidate
            else {
                if (current.isNotBlank()) out += current
                current = word
            }
        }
        if (current.isNotBlank()) out += current
        return out
    }

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private fun loadEmbeddedSourceFont(target: PDDocument, source: PDDocument, predicate: (String) -> Boolean): PDFont {
        source.pages.forEach { page ->
            val resources = page.resources ?: return@forEach
            resources.fontNames.forEach { name ->
                val font = resources.getFont(name)
                if (predicate(font.name)) {
                    val descriptor = requireNotNull(font.fontDescriptor)
                    val stream = descriptor.fontFile2 ?: descriptor.fontFile ?: descriptor.fontFile3
                    requireNotNull(stream) { "Source font is not embedded: " + font.name }
                    val bytes = stream.createInputStream().use { it.readBytes() }
                    return PDType0Font.load(target, ByteArrayInputStream(bytes), false)
                }
            }
        }
        error("Requested source font not found.")
    }

    private fun resource(path: String): InputStream =
        requireNotNull(DesktopPcSheetCustomV2ExtendedEvaluationRun7Test::class.java.classLoader.getResourceAsStream(path)) {
            "Missing test resource: " + path
        }

    private data class Resources(
        val forms: List<PDFormXObject>,
        val attributeOrnament: PDImageXObject,
        val corbel: PDFont,
        val corbelBold: PDFont,
        val fira: PDFont,
        val firaSemibold: PDFont,
        val symbol: PDFont,
    ) {
        companion object {
            fun load(doc: PDDocument, source: PDDocument): Resources {
                val owner = DesktopPcSheetCustomV2ExtendedEvaluationRun7Test()
                val utility = LayerUtility(doc)
                val corbelRegular = owner.loadEmbeddedSourceFont(doc, source) { name ->
                    name.contains("Corbel", ignoreCase = true) && !name.contains("Bold", ignoreCase = true)
                }
                val corbelBold = owner.loadEmbeddedSourceFont(doc, source) { name ->
                    name.contains("Corbel", ignoreCase = true) && name.contains("Bold", ignoreCase = true)
                }
                return Resources(
                    forms = (0 until 5).map { utility.importPageAsForm(source, it) },
                    attributeOrnament = buildTransparentAttributeOrnament(doc, source),
                    corbel = corbelRegular,
                    corbelBold = corbelBold,
                    fira = resourceFont(doc, FIRA_REGULAR),
                    firaSemibold = resourceFont(doc, FIRA_SEMIBOLD),
                    symbol = resourceFont(doc, SYMBOL_V8),
                )
            }

            private fun buildTransparentAttributeOrnament(doc: PDDocument, source: PDDocument): PDImageXObject {
                val dpi = 288f
                val scale = dpi / 72f
                val sourceImage = PDFRenderer(source).renderImageWithDPI(1, dpi, ImageType.RGB)
                val x0 = (ATTRIBUTE_ORNAMENT_SOURCE_X * scale).roundToInt()
                val y0 = (ATTRIBUTE_ORNAMENT_SOURCE_TOP * scale).roundToInt()
                val width = (ATTRIBUTE_ORNAMENT_WIDTH * scale).roundToInt()
                val height = (ATTRIBUTE_ORNAMENT_HEIGHT * scale).roundToInt()
                val transparent = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

                fun insideValueMask(px: Int, py: Int): Boolean {
                    val x = px / scale
                    val y = py / scale
                    val score = x in 17.5f..43.0f && y in 8.0f..26.0f
                    val modifier = x in 48.0f..71.0f && y in 27.0f..43.5f
                    return score || modifier
                }

                for (y in 0 until height) {
                    for (x in 0 until width) {
                        if (insideValueMask(x, y)) {
                            transparent.setRGB(x, y, 0)
                            continue
                        }
                        val rgb = sourceImage.getRGB(x0 + x, y0 + y)
                        val red = rgb shr 16 and 0xFF
                        val green = rgb shr 8 and 0xFF
                        val blue = rgb and 0xFF
                        val luma = (red * 299 + green * 587 + blue * 114) / 1000
                        val alpha = when {
                            luma >= 190 -> 0
                            luma <= 120 -> 255
                            else -> ((190 - luma) * 255 / 70).coerceIn(0, 255)
                        }
                        transparent.setRGB(x, y, alpha shl 24)
                    }
                }
                return LosslessFactory.createFromImage(doc, transparent)
            }

            private fun resourceFont(doc: PDDocument, path: String): PDFont =
                resource(path).use { PDType0Font.load(doc, it, false) }

            private fun resource(path: String): InputStream =
                requireNotNull(DesktopPcSheetCustomV2ExtendedEvaluationRun7Test::class.java.classLoader.getResourceAsStream(path)) {
                    "Missing test resource: " + path
                }
        }
    }

    private enum class Training { NONE, PROFICIENT, EXPERTISE }

    private data class Rule(val startX: Float, val endX: Float, val topY: Float)
    private data class TopRect(val x: Float, val top: Float, val width: Float, val height: Float)
    private data class AttributeSample(val score: String, val modifier: String, val save: String, val skills: List<Pair<String,String>>)
    private data class ResourceSample(val name: String, val current: Int, val maximum: Int, val recovery: String, val source: String, val note: String)
    private data class SpellSample(val rule: Rule, val name: String)

    private companion object {
        const val W = 612f
        const val H = 792f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf"
        const val FIRA_REGULAR = "fonts/pdf/text/FiraSans-Regular.ttf"
        const val FIRA_SEMIBOLD = "fonts/pdf/text/FiraSans-SemiBold.ttf"
        const val SYMBOL_V8 = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"
        const val ATTRIBUTE_ORNAMENT_SOURCE_X = 14.32f
        const val ATTRIBUTE_ORNAMENT_SOURCE_TOP = 164.68f
        const val ATTRIBUTE_ORNAMENT_WIDTH = 80.40f
        const val ATTRIBUTE_ORNAMENT_HEIGHT = 47.76f
        const val SOURCE_CORBEL_ATTRIBUTE_SCALE = 79f
        const val SOURCE_CORBEL_COMPACT_SCALE = 78f
        const val SOURCE_CORBEL_HEADING_SCALE = 81f
        const val SOURCE_CORBEL_TABLE_SCALE = 86f
        val SOURCE_GRAY_DARK: Color = Color(200, 199, 199)
        val SOURCE_GRAY_LIGHT: Color = Color(227, 227, 227)
    }
}
