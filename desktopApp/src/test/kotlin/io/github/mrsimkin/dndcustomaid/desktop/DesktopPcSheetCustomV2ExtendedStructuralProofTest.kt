package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.awt.geom.AffineTransform
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.abs
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
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.util.Matrix

/**
 * Custom-v2 Extended structural design proof.
 *
 * This is deliberately NOT a production renderer and NOT an owner-approval candidate.
 * Its only purpose is to settle physical grammar before dense representative data is wired:
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
 * Cleanup is intentionally empty on every page in this proof.
 */
class DesktopPcSheetCustomV2ExtendedStructuralProofTest {
    @Test
    fun rendersSevenUniqueCustomV2ExtendedStructuralPages() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val pdf = File(proofDir, "custom-v2-extended-structural-proof-run1.pdf")
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
                "TIRADAS DE SALVACIÓN",
                "HABILIDADES PERSONALIZADAS",
                "RASGOS Y CARACTERÍSTICAS",
                "RECURSOS Y OPCIONES",
                "TESORO / OBJETOS / OTROS",
                "EQUIPO ESPECIAL",
            ).forEach { label ->
                assertTrue(text.contains(label), "Structural proof missing expected label: " + label)
            }
            assertFalse(text.contains("Especie", ignoreCase = true), "Product terminology contract is Raza, never Especie.")

            val renderer = PDFRenderer(doc)
            repeat(doc.numberOfPages) { index ->
                val image = renderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(ImageIO.write(image, "png", File(proofDir, "custom-v2-extended-structural-proof-run1-page-" + (index + 1) + ".png")))
            }
        }

        assertTrue(pdf.length() > 20_000L)
    }

    private fun renderPerAttribute(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X ATTR - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            val columns = listOf(14f, 207f, 400f)
            columns.forEach { x -> attributeModuleStructure(s, x, 106f, 184f, 286f, 6) }
            drawRule(s, 14f, 598f, 430f, 0.8f)
            repeat(3) { col ->
                val x = 14f + col * 194.7f
                drawRule(s, x, x + 184f, 469f, 0.65f)
                repeat(7) { row -> drawRule(s, x + 15f, x + 184f, 488f + row * 17f, 0.55f) }
            }
        }
        appendLayer(doc, layers, page, "V2X ATTR - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X ATTR - LABELS") { s ->
            pageTitle(s, r, "ESTADÍSTICAS PERSONALIZADAS")
            val labels = listOf("HONor", "RESolución", "SUErte")
            labels.forEachIndexed { index, title ->
                centered(s, r.corbelBold, TopRect(14f + index * 193f, 111f, 184f, 20f), title, 12.1f)
                textTop(s, r.corbel, 28f + index * 193f, 190f, "Tirada de Salvación", 7.75f)
            }
            centered(s, r.corbelBold, TopRect(14f, 438f, 584f, 22f), "HABILIDADES VINCULADAS A ATRIBUTOS ESTÁNDAR", 10.3f)
            listOf("INTeligencia", "DEStreza", "SABiduría").forEachIndexed { index, title ->
                centered(s, r.corbelBold, TopRect(14f + index * 194.7f, 448f, 184f, 20f), title, 9.7f)
            }
        }
        appendLayer(doc, layers, page, "V2X ATTR - VALUES") { s ->
            val values = listOf(
                AttributeSample("15", "+2", "+5", listOf("Etiqueta" to "+5", "Reputación" to "+5", "Protocolo" to "+2")),
                AttributeSample("12", "+1", "+1", listOf("Temple" to "+4", "Concentración" to "+1")),
                AttributeSample("18", "+4", "+7", listOf("Fortuna" to "+7", "Escapismo" to "+4", "Improvisación" to "+7")),
            )
            values.forEachIndexed { index, sample ->
                drawAttributeValues(s, r, 14f + index * 193f, 106f, sample)
            }
            val anchors = listOf(
                listOf("Ocultismo (INT)" to "+7", "Criptografía (INT)" to "+4"),
                listOf("Acrobacia aérea (DES)" to "+6"),
                listOf("Lectura corporal (SAB)" to "+4"),
            )
            anchors.forEachIndexed { col, rows ->
                rows.forEachIndexed { row, item ->
                    textAboveRule(s, r.fira, Rule(31f + col * 194.7f, 169f + col * 194.7f, 488f + row * 17f), item.first, 8.2f, 6.8f, 2.3f)
                    centeredAboveRule(s, r.firaSemibold, Rule(169f + col * 194.7f, 198f + col * 194.7f, 488f + row * 17f), item.second, 8.4f, 2.4f)
                }
            }
        }
        appendLayer(doc, layers, page, "V2X ATTR - MARKERS") { s ->
            listOf(0, 1, 2).forEach { col ->
                glyphInRect(s, r.symbol, 0xE211, TopRect(18f + col * 193f, 179f, 8.5f, 9f), 0.6f, 0.6f)
            }
            glyphInRect(s, r.symbol, 0xE211, TopRect(18f, 209f, 8.5f, 9f), 0.6f, 0.6f)
            glyphInRect(s, r.symbol, 0xE212, TopRect(211f, 209f, 8.5f, 9f), 0.6f, 0.6f)
            glyphInRect(s, r.symbol, 0xE211, TopRect(404f, 209f, 8.5f, 9f), 0.6f, 0.6f)
            glyphInRect(s, r.symbol, 0xE211, TopRect(18f, 477f, 8.5f, 9f), 0.6f, 0.6f)
            glyphInRect(s, r.symbol, 0xE211, TopRect(212.7f, 477f, 8.5f, 9f), 0.6f, 0.6f)
        }
    }

    private fun renderPerAbility(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X ABILITY - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            repeat(3) { col ->
                val x = 14f + col * 194.7f
                statCardStructure(s, x, 106f, 184f)
            }
            drawRule(s, 14f, 598f, 234f, 0.8f)
            // Saves.
            repeat(12) { row -> drawRule(s, 14f, 205f, 286f + row * 14.17f, 0.55f) }
            // Abilities.
            repeat(27) { row -> drawRule(s, 218f, 598f, 286f + row * 14.17f, 0.55f) }
        }
        appendLayer(doc, layers, page, "V2X ABILITY - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X ABILITY - LABELS") { s ->
            pageTitle(s, r, "ESTADÍSTICAS PERSONALIZADAS")
            val labels = listOf("HONor", "RESolución", "SUErte")
            labels.forEachIndexed { index, title ->
                centered(s, r.corbelBold, TopRect(14f + index * 194.7f, 111f, 184f, 18f), title, 12.1f)
            }
            centered(s, r.corbelBold, TopRect(14f, 244f, 191f, 25f), "TIRADAS DE SALVACIÓN", 10.8f)
            centered(s, r.corbelBold, TopRect(218f, 244f, 380f, 25f), "HABILIDADES PERSONALIZADAS", 10.8f)
        }
        appendLayer(doc, layers, page, "V2X ABILITY - VALUES") { s ->
            val scores = listOf("15" to "+2", "12" to "+1", "18" to "+4")
            scores.forEachIndexed { index, pair ->
                centered(s, r.firaSemibold, TopRect(28f + index * 194.7f, 137f, 64f, 35f), pair.first, 17f)
                centered(s, r.firaSemibold, TopRect(100f + index * 194.7f, 145f, 52f, 25f), pair.second, 14.5f)
            }
            val saves = listOf("Honor (HON)" to "+5", "Resolución (RES)" to "+1", "Suerte (SUE)" to "+7")
            saves.forEachIndexed { index, item ->
                textAboveRule(s, r.fira, Rule(31f, 164f, 286f + index * 14.17f), item.first, 8.2f, 6.8f, 2.1f)
                centeredAboveRule(s, r.firaSemibold, Rule(164f, 202f, 286f + index * 14.17f), item.second, 8.4f, 2.1f)
            }
            val abilities = listOf(
                "Etiqueta (HON)" to "+5",
                "Reputación (HON)" to "+5",
                "Temple (RES)" to "+4",
                "Fortuna (SUE)" to "+7",
                "Ocultismo (INT)" to "+7",
                "Acrobacia aérea (DES)" to "+6",
                "Lectura corporal (SAB)" to "+4",
            )
            abilities.forEachIndexed { index, item ->
                textAboveRule(s, r.fira, Rule(237f, 548f, 286f + index * 14.17f), item.first, 8.2f, 6.8f, 2.1f)
                centeredAboveRule(s, r.firaSemibold, Rule(548f, 595f, 286f + index * 14.17f), item.second, 8.4f, 2.1f)
            }
        }
        appendLayer(doc, layers, page, "V2X ABILITY - MARKERS") { s ->
            repeat(3) { row -> glyphInRect(s, r.symbol, 0xE211, TopRect(18f, 275.2f + row * 14.17f, 8.5f, 9f), 0.6f, 0.6f) }
            listOf(0xE211,0xE211,0xE212,0xE211,0xE211,0xE211,0xE211).forEachIndexed { row, cp ->
                glyphInRect(s, r.symbol, cp, TopRect(222f, 275.2f + row * 14.17f, 8.5f, 9f), 0.6f, 0.6f)
            }
        }
    }

    private fun renderTraits(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X TRAITS - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            repeat(35) { row ->
                drawRule(s, 14f, 291f, 126f + row * 17f, 0.55f)
                drawRule(s, 307f, 598f, 126f + row * 17f, 0.55f)
            }
        }
        appendLayer(doc, layers, page, "V2X TRAITS - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X TRAITS - LABELS") { s ->
            pageTitle(s, r, "RASGOS Y CARACTERÍSTICAS")
            centered(s, r.corbelBold, TopRect(14f, 96f, 277f, 24f), "CLASE / DOTES", 10.3f)
            centered(s, r.corbelBold, TopRect(307f, 96f, 291f, 24f), "RAZA / TRASFONDO / OTROS", 10.3f)
        }
        appendLayer(doc, layers, page, "V2X TRAITS - VALUES") { s ->
            featureEntry(s, r, 14f, 126f, 277f, "Portento", "Mago / Adivinación · Pasivo", "Usos: 1 / 2 · Descanso largo", "Puede sustituir una tirada apropiada por uno de los resultados registrados.")
            featureEntry(s, r, 14f, 228f, 277f, "Acción astuta", "Pícaro · Acción adicional", null, "Correr, Destrabarse u Ocultarse como acción adicional.")
            featureEntry(s, r, 307f, 126f, 291f, "Ascendencia feérica", "Raza · Elfo alto", null, "Ventaja contra ser hechizado; la magia no puede dormir al personaje.")
            featureEntry(s, r, 307f, 228f, 291f, "Investigador", "Trasfondo", null, "Sabe dónde buscar información académica y cómo acceder a archivos especializados.")
        }
        appendLayer(doc, layers, page, "V2X TRAITS - MARKERS") { }
    }

    private fun renderResources(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X RESOURCES - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            drawRule(s, 14f, 598f, 126f, 0.8f)
            repeat(10) { row -> drawRule(s, 14f, 598f, 160f + row * 34f, 0.55f) }
            drawRule(s, 14f, 598f, 520f, 0.8f)
            repeat(7) { row -> drawRule(s, 14f, 598f, 554f + row * 34f, 0.55f) }
            listOf(222f, 352f, 475f).forEach { x -> verticalRule(s, x, 126f, 466f, 0.45f) }
            listOf(140f, 287f, 470f).forEach { x -> verticalRule(s, x, 520f, 758f, 0.45f) }
        }
        appendLayer(doc, layers, page, "V2X RESOURCES - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X RESOURCES - LABELS") { s ->
            pageTitle(s, r, "RECURSOS Y OPCIONES")
            tableLabel(s, r, 14f, 105f, 208f, "RECURSO")
            tableLabel(s, r, 222f, 105f, 130f, "ACTUAL / MÁX.")
            tableLabel(s, r, 352f, 105f, 123f, "RESTABLECE")
            tableLabel(s, r, 475f, 105f, 123f, "ORIGEN")
            centered(s, r.corbelBold, TopRect(14f, 491f, 584f, 24f), "OPCIONES", 10.8f)
            tableLabel(s, r, 14f, 526f, 126f, "TIPO")
            tableLabel(s, r, 140f, 526f, 147f, "OPCIÓN")
            tableLabel(s, r, 287f, 526f, 183f, "EFECTO")
            tableLabel(s, r, 470f, 526f, 128f, "COSTE / NOTAS")
        }
        appendLayer(doc, layers, page, "V2X RESOURCES - VALUES") { s ->
            val rows = listOf(
                ResourceSample("Recuperación arcana", 1, 1, "Descanso largo", "Mago", "Usada tras estudiar el códice."),
                ResourceSample("Dados de portento", 1, 2, "Descanso largo", "Adivinación", "Un resultado disponible."),
                ResourceSample("Carga del monóculo", 2, 3, "Amanecer", "Objeto", "Una carga por lectura intensiva."),
                ResourceSample("Inspiración heroica", 1, 1, "Variable", "General", "Disponible."),
            )
            rows.forEachIndexed { index, row ->
                val top = 128f + index * 68f
                textTop(s, r.fira, 18f, top + 8f, row.name, 8.7f)
                textTop(s, r.fira, 18f, top + 25f, row.note, 7.1f)
                centered(s, r.firaSemibold, TopRect(226f, top + 2f, 44f, 22f), row.current.toString() + " / " + row.maximum, 9f)
                textTop(s, r.fira, 356f, top + 8f, row.recovery, 8f)
                textTop(s, r.fira, 479f, top + 8f, row.source, 8f)
            }
            val options = listOf(
                listOf("Clase","Tradición de Adivinación","Portento y capacidades relacionadas.","-"),
                listOf("Técnica","Metamagia improvisada","Modifica una conjuración puntual.","2 puntos"),
                listOf("Competencia","Herramientas de ladrón","Cerraduras, trampas y mecanismos.","-"),
                listOf("Objeto","Monóculo rúnico","Inspección arcana fina.","1 carga"),
            )
            options.forEachIndexed { index, row ->
                val y = 554f + index * 34f
                textAboveRule(s, r.fira, Rule(18f, 136f, y), row[0], 8f, 6.6f, 2.3f)
                textAboveRule(s, r.fira, Rule(144f, 283f, y), row[1], 8f, 6.6f, 2.3f)
                textAboveRule(s, r.fira, Rule(291f, 466f, y), row[2], 7.7f, 6.3f, 2.3f)
                textAboveRule(s, r.fira, Rule(474f, 594f, y), row[3], 7.7f, 6.3f, 2.3f)
            }
        }
        appendLayer(doc, layers, page, "V2X RESOURCES - MARKERS") { s ->
            val counters = listOf(1 to 1, 1 to 2, 2 to 3, 1 to 1)
            counters.forEachIndexed { row, pair ->
                drawSquareCounter(s, r.symbol, 278f, 139f + row * 68f, pair.first, pair.second)
            }
        }
    }

    private fun renderInventory(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X INVENTORY - STRUCTURE") { s ->
            pageHeaderStructure(s, r.forms[2])
            // Two wide equipment columns, source-family 17 pt rhythm.
            repeat(20) { row ->
                drawRule(s, 14f, 297f, 126f + row * 17f, 0.55f)
                drawRule(s, 307f, 598f, 126f + row * 17f, 0.55f)
            }
            drawRule(s, 14f, 598f, 492f, 0.8f)
            repeat(7) { row -> drawRule(s, 14f, 222f, 535f + row * 17f, 0.55f) }
            repeat(10) { row -> drawRule(s, 238f, 598f, 535f + row * 17f, 0.55f) }
            verticalRule(s, 510f, 518f, 705f, 0.45f)
        }
        appendLayer(doc, layers, page, "V2X INVENTORY - CLEANUP") { }
        appendLayer(doc, layers, page, "V2X INVENTORY - LABELS") { s ->
            pageTitle(s, r, "INVENTARIO / EQUIPO")
            centered(s, r.corbelBold, TopRect(14f, 96f, 584f, 24f), "EQUIPO - CONTINUACIÓN", 10.8f)
            centered(s, r.corbelBold, TopRect(14f, 500f, 208f, 24f), "TESORO / OBJETOS / OTROS", 9.7f)
            centered(s, r.corbelBold, TopRect(238f, 500f, 360f, 24f), "EQUIPO ESPECIAL", 10.3f)
            textTop(s, r.corbel, 512f, 518f, "ACTIVO", 6.8f)
        }
        appendLayer(doc, layers, page, "V2X INVENTORY - VALUES") { s ->
            val left = listOf(
                "12 x Clavos de hierro - Bolsa lateral - 0,1 lb",
                "8 x Tiza blanca - Estuche",
                "6 x Viales vacíos - Mochila - 0,1 lb",
                "2 x Tinta azul - Caja de escritura - 0,2 lb",
                "Espejo de acero - Mochila - 0,5 lb",
            )
            val right = listOf(
                "Manta de viaje - Mochila - 3 lb",
                "Cuaderno de campo - Bolsa - 0,5 lb",
                "Cuerda de seda - Exterior - 5 lb",
                "Linterna cubierta - Mochila - 2 lb",
                "4 x Aceite - Mochila - 1 lb",
            )
            left.forEachIndexed { i, value -> textAboveRule(s, r.fira, Rule(18f, 293f, 126f + i * 17f), value, 7.8f, 6.3f, 2.2f) }
            right.forEachIndexed { i, value -> textAboveRule(s, r.fira, Rule(311f, 594f, 126f + i * 17f), value, 7.8f, 6.3f, 2.2f) }

            val valuables = listOf("Gema lunar tallada - 120 po", "Broche élfico antiguo - 75 po", "Láminas de plata - 45 po")
            valuables.forEachIndexed { i, value -> textAboveRule(s, r.fira, Rule(18f, 218f, 535f + i * 17f), value, 7.7f, 6.2f, 2.2f) }

            val special = listOf(
                "Monóculo rúnico" to "Sintonizado; 2 de 3 cargas.",
                "Amuleto de Liria" to "Recuerdo y foco ceremonial.",
                "Daga de plata" to "Equipada; hoja tratada.",
                "Guante del escriba" to "Estabiliza la mano.",
            )
            special.forEachIndexed { i, item ->
                val y = 535f + i * 17f
                textAboveRule(s, r.fira, Rule(257f, 385f, y), item.first, 7.7f, 6.2f, 2.2f)
                textAboveRule(s, r.fira, Rule(390f, 506f, y), item.second, 7.0f, 5.8f, 2.2f)
            }
        }
        appendLayer(doc, layers, page, "V2X INVENTORY - MARKERS") { s ->
            repeat(3) { i -> glyphInRect(s, r.symbol, 0xE211, TopRect(516f, 523f + i * 17f, 8.5f, 9f), 0.6f, 0.6f) }
        }
    }

    private fun renderSpells(doc: PDDocument, layers: LayerUtility, page: PDPage, r: Resources) {
        appendLayer(doc, layers, page, "V2X SPELLS - STRUCTURE") { s ->
            s.drawForm(r.forms[3])
        }
        appendLayer(doc, layers, page, "V2X SPELLS - CLEANUP") { }
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
                "Continuar las notas de campaña sin cambiar la estructura original de v2.",
                "La puerta con sello azul coincide con las monedas halladas en la torre.",
                "Comparar el alfabeto parcial con las notas del profesor Vael.",
            )
            val right = listOf(
                "Ruta: entrada oeste, cámara de columnas, escalera rota y galería azul.",
                "Materiales: tinta, tiza, viales, espejo, cuerda y una linterna adicional.",
            )
            left.forEachIndexed { i, line -> textAboveRule(s, r.fira, Rule(14f, 302.5f, 104f + i * 17f), line, 8.2f, 6.6f, 2.6f) }
            right.forEachIndexed { i, line -> textAboveRule(s, r.fira, Rule(309f, 597.5f, 104f + i * 17f), line, 8.2f, 6.6f, 2.6f) }
        }
        appendLayer(doc, layers, page, "V2X NOTES - MARKERS") { }
    }

    private fun pageHeaderStructure(s: PDFormContentStream, logoSource: PDFormXObject) {
        drawSourceCrop(s, logoSource, 14f, 16f, 105f, 60f)
        drawRule(s, 126f, 598f, 79f, 0.6f)
    }

    private fun pageTitle(s: PDFormContentStream, r: Resources, title: String) {
        centered(s, r.corbelBold, TopRect(126f, 28f, 472f, 34f), title, 12.1f)
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

    private fun drawSquareCounter(s: PDFormContentStream, font: PDFont, startX: Float, centerTop: Float, current: Int, maximum: Int) {
        repeat(maximum.coerceAtMost(6)) { index ->
            val cp = if (index < current) 0xE204 else 0xE203
            glyphInRect(s, font, cp, TopRect(startX + index * 14f, centerTop - 5f, 10f, 10f), 0.7f, 0.7f)
        }
    }

    private fun tableLabel(s: PDFormContentStream, r: Resources, x: Float, top: Float, width: Float, label: String) {
        centered(s, r.corbelBold, TopRect(x, top, width, 18f), label, 7.7f)
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
        requireNotNull(DesktopPcSheetCustomV2ExtendedStructuralProofTest::class.java.classLoader.getResourceAsStream(path)) {
            "Missing test resource: " + path
        }

    private data class Resources(
        val forms: List<PDFormXObject>,
        val corbel: PDFont,
        val corbelBold: PDFont,
        val fira: PDFont,
        val firaSemibold: PDFont,
        val symbol: PDFont,
    ) {
        companion object {
            fun load(doc: PDDocument, source: PDDocument): Resources {
                val owner = DesktopPcSheetCustomV2ExtendedStructuralProofTest()
                val utility = LayerUtility(doc)
                val corbelRegular = owner.loadEmbeddedSourceFont(doc, source) { name ->
                    name.contains("Corbel", ignoreCase = true) && !name.contains("Bold", ignoreCase = true)
                }
                val corbelBold = owner.loadEmbeddedSourceFont(doc, source) { name ->
                    name.contains("Corbel", ignoreCase = true) && name.contains("Bold", ignoreCase = true)
                }
                return Resources(
                    forms = (0 until 5).map { utility.importPageAsForm(source, it) },
                    corbel = corbelRegular,
                    corbelBold = corbelBold,
                    fira = resourceFont(doc, FIRA_REGULAR),
                    firaSemibold = resourceFont(doc, FIRA_SEMIBOLD),
                    symbol = resourceFont(doc, SYMBOL_V8),
                )
            }

            private fun resourceFont(doc: PDDocument, path: String): PDFont =
                resource(path).use { PDType0Font.load(doc, it, false) }

            private fun resource(path: String): InputStream =
                requireNotNull(DesktopPcSheetCustomV2ExtendedStructuralProofTest::class.java.classLoader.getResourceAsStream(path)) {
                    "Missing test resource: " + path
                }
        }
    }

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
    }
}
