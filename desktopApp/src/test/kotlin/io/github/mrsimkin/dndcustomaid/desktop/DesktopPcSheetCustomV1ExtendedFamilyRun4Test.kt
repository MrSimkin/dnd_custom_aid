package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
import org.apache.pdfbox.util.Matrix

/**
 * Custom-v1 Extended Run 4 — full family restart.
 *
 * Run 3 remains rejected historical evidence. Run 4 rebuilds every added page (6-11) from the
 * frozen five-page base using explicit independent layers:
 *
 * 1. authentic source structure;
 * 2. bounded cleanup;
 * 3. labels;
 * 4. values;
 * 5. approved v8 proficiency/expertise symbols.
 *
 * A 30-page diagnostic artifact exposes all six extension roles at all five layer stages.
 */
class DesktopPcSheetCustomV1ExtendedFamilyRun4Test {
    private var layerSerial = 0

    @Test
    fun rendersFullLayeredExtendedFamily() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }

        DesktopPcSheetHybridStrategyRun7Test().rendersCompleteFivePageAllHybridCustomV1Draft()
        val approvedBase = File(proofDir, "hybrid-strategy1-run7-composite.pdf")
        assertTrue(approvedBase.isFile)

        val frozenBase = Loader.loadPDF(approvedBase).use { doc ->
            val renderer = PDFRenderer(doc)
            (0 until 5).map { renderer.renderImageWithDPI(it, BASELINE_COMPARE_DPI, ImageType.RGB) }
        }

        val sourceBytes = resource(TEMPLATE).use { it.readBytes() }
        val diagnostics = File(proofDir, "custom-v1-extended-run4-layer-diagnostics.pdf")
        renderDiagnostics(diagnostics, sourceBytes, proofDir)
        verifyDiagnosticStructuralGuards(diagnostics)

        val output = File(proofDir, "custom-v1-complete-family-extended-run4.pdf")
        Loader.loadPDF(approvedBase).use { doc ->
            Loader.loadPDF(sourceBytes).use { source ->
                val resources = LayerResources.load(doc, source)
                ExtensionRole.entries.forEach { role ->
                    val page = PDPage(PDRectangle(W, H)).also(doc::addPage)
                    renderExtensionPage(doc, page, resources, role, LayerStage.MARKERS)
                }
            }
            doc.save(output)
        }

        Loader.loadPDF(output).use { doc ->
            assertEquals(11, doc.numberOfPages)
            assertFalse(doc.isEncrypted)
            val renderer = PDFRenderer(doc)

            repeat(11) { index ->
                val image = renderer.renderImageWithDPI(index, REVIEW_DPI, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "custom-v1-complete-family-extended-run4-page-${index + 1}.png"),
                    ),
                )
            }

            repeat(5) { index ->
                val actual = renderer.renderImageWithDPI(index, BASELINE_COMPARE_DPI, ImageType.RGB)
                assertImagesEqual(
                    frozenBase[index],
                    actual,
                    "Frozen Custom-v1 base page ${index + 1} changed during Run 4.",
                )
            }
        }

        assertTrue(output.length() > 20_000L)
        assertTrue(diagnostics.length() > 20_000L)
    }

    private fun renderDiagnostics(
        output: File,
        sourceBytes: ByteArray,
        proofDir: File,
    ) {
        PDDocument().use { doc ->
            Loader.loadPDF(sourceBytes).use { source ->
                val resources = LayerResources.load(doc, source)
                ExtensionRole.entries.forEach { role ->
                    LayerStage.entries.forEach { stage ->
                        val page = PDPage(PDRectangle(W, H)).also(doc::addPage)
                        renderExtensionPage(doc, page, resources, role, stage)
                    }
                }
            }
            doc.save(output)
        }

        Loader.loadPDF(output).use { doc ->
            val renderer = PDFRenderer(doc)
            ExtensionRole.entries.forEachIndexed { roleIndex, role ->
                LayerStage.entries.forEachIndexed { stageIndex, stage ->
                    val pageIndex = roleIndex * LayerStage.entries.size + stageIndex
                    val image = renderer.renderImageWithDPI(pageIndex, DIAGNOSTIC_DPI, ImageType.RGB)
                    assertTrue(
                        ImageIO.write(
                            image,
                            "png",
                            File(
                                proofDir,
                                "custom-v1-extended-run4-${role.slug}-layer-${stageIndex + 1}-${stage.slug}.png",
                            ),
                        ),
                    )
                }
            }
        }
    }

    private fun verifyDiagnosticStructuralGuards(diagnostics: File) {
        Loader.loadPDF(diagnostics).use { doc ->
            val renderer = PDFRenderer(doc)
            ExtensionRole.entries.forEachIndexed { roleIndex, role ->
                val structureIndex = roleIndex * LayerStage.entries.size
                val structure = renderer.renderImageWithDPI(structureIndex, DIAGNOSTIC_DPI, ImageType.RGB)

                LayerStage.entries.drop(1).forEachIndexed { offset, stage ->
                    val candidate = renderer.renderImageWithDPI(structureIndex + offset + 1, DIAGNOSTIC_DPI, ImageType.RGB)
                    protectedRegions(role).forEachIndexed { regionIndex, region ->
                        assertRegionImagesEqual(
                            expected = structure,
                            actual = candidate,
                            region = region,
                            dpi = DIAGNOSTIC_DPI,
                            message = "Run-4 ${role.slug} ${stage.slug} altered protected source geometry region ${regionIndex + 1}.",
                        )
                    }
                }
            }
        }
    }

    private fun renderExtensionPage(
        doc: PDDocument,
        page: PDPage,
        r: LayerResources,
        role: ExtensionRole,
        stage: LayerStage,
    ) {
        appendLayer(doc, page, "Run4 1 - ${role.slug} - source structure") { s ->
            drawStructure(s, r, role)
        }
        if (stage >= LayerStage.CLEANUP) {
            appendLayer(doc, page, "Run4 2 - ${role.slug} - bounded cleanup") { s ->
                drawCleanup(s, role)
            }
        }
        if (stage >= LayerStage.LABELS) {
            appendLayer(doc, page, "Run4 3 - ${role.slug} - labels") { s ->
                drawLabels(s, r, role)
            }
        }
        if (stage >= LayerStage.VALUES) {
            appendLayer(doc, page, "Run4 4 - ${role.slug} - values") { s ->
                drawValues(s, r, role)
            }
        }
        if (stage >= LayerStage.MARKERS) {
            appendLayer(doc, page, "Run4 5 - ${role.slug} - markers") { s ->
                drawMarkers(s, r, role)
            }
        }
    }

    private fun drawStructure(s: PDFormContentStream, r: LayerResources, role: ExtensionRole) {
        when (role) {
            ExtensionRole.CUSTOM_STATISTICS -> drawStatisticsStructure(s, r.forms[0])
            ExtensionRole.TRAITS_FEATURES -> s.drawForm(r.forms[2])
            ExtensionRole.RESOURCES_OPTIONS -> s.drawForm(r.forms[1])
            ExtensionRole.INVENTORY_EQUIPMENT -> s.drawForm(r.forms[1])
            ExtensionRole.SPELLS -> s.drawForm(r.forms[3])
            ExtensionRole.NOTES -> s.drawForm(r.forms[4])
        }
    }

    private fun drawCleanup(s: PDFormContentStream, role: ExtensionRole) {
        when (role) {
            ExtensionRole.CUSTOM_STATISTICS -> drawStatisticsCleanup(s)
            ExtensionRole.TRAITS_FEATURES -> drawTraitsCleanup(s)
            ExtensionRole.RESOURCES_OPTIONS -> drawResourcesCleanup(s)
            ExtensionRole.INVENTORY_EQUIPMENT,
            ExtensionRole.SPELLS,
            ExtensionRole.NOTES -> Unit
        }
    }

    private fun drawLabels(s: PDFormContentStream, r: LayerResources, role: ExtensionRole) {
        when (role) {
            ExtensionRole.CUSTOM_STATISTICS -> drawStatisticsLabels(s, r)
            ExtensionRole.TRAITS_FEATURES -> drawTraitsLabels(s, r)
            ExtensionRole.RESOURCES_OPTIONS -> drawResourcesLabels(s, r)
            ExtensionRole.INVENTORY_EQUIPMENT -> centeredText(
                s, r.fira, 195f, 96f, 80f, 13f, "CONTINUACIÓN", 7.5f,
            )
            ExtensionRole.SPELLS,
            ExtensionRole.NOTES -> Unit
        }
    }

    private fun drawValues(s: PDFormContentStream, r: LayerResources, role: ExtensionRole) {
        when (role) {
            ExtensionRole.CUSTOM_STATISTICS -> drawStatisticsValues(s, r)
            ExtensionRole.TRAITS_FEATURES -> drawTraitsValues(s, r)
            ExtensionRole.RESOURCES_OPTIONS -> drawResourcesValues(s, r)
            ExtensionRole.INVENTORY_EQUIPMENT -> drawInventoryValues(s, r)
            ExtensionRole.SPELLS -> drawSpellValues(s, r)
            ExtensionRole.NOTES -> drawNotesValues(s, r)
        }
    }

    private fun drawMarkers(s: PDFormContentStream, r: LayerResources, role: ExtensionRole) {
        when (role) {
            ExtensionRole.CUSTOM_STATISTICS -> drawStatisticsMarkers(s, r.symbol)
            ExtensionRole.SPELLS -> drawSpellMarkers(s, r.symbol)
            else -> Unit
        }
    }

    private fun drawStatisticsStructure(s: PDFormContentStream, mainSource: PDFormXObject) {
        drawSourceCrop(s, mainSource, 20f, 18f, 170f, 74f)
        COLUMNS.forEachIndexed { index, column ->
            val sourceX = if (index % 2 == 0) 408.0f else 311.7f
            drawTranslatedSourceCrop(
                s, mainSource,
                sourceX = sourceX,
                sourceTop = SOURCE_BLOCK_TOP_WITH_MARGIN,
                width = column.width,
                height = SOURCE_BLOCK_HEIGHT_WITH_MARGIN,
                targetX = column.x,
                targetTop = SOURCE_BLOCK_TOP_WITH_MARGIN,
            )
        }
        listOf(25f to 181f, 215.291f to 396.708f, 402.378f to 583.795f)
            .forEach { (a, b) -> sourceBands(s, a, b, 160f, 4, 20f) }
        listOf(25f to 181f, 215.291f to 396.708f, 402.378f to 583.795f)
            .forEach { (a, b) -> sourceBands(s, a, b, 468f, 12, 20f) }
    }

    private fun drawStatisticsCleanup(s: PDFormContentStream) {
        COLUMNS.forEachIndexed { index, column ->
            val bg = if (column.gray) SOURCE_GRAY else Color.WHITE
            val scoreX = SCORE_X[index]
            val modX = MOD_X[index]
            fill(s, column.x + 8f, 245f, max(8f, column.width - 16f), 16f, bg)
            fill(s, scoreX - 18f, 271f, 36f, 17f, bg)
            fill(s, modX - 10f, 289f, 20f, 10f, bg)
            fill(s, column.x + column.width - 23f, 308f, 18f, 9f, bg)
            SKILL_ROW_TOPS.forEach { rowTop ->
                fill(s, column.x + 12f, rowTop + 0.5f, max(8f, column.width - 43f), 10f, bg)
                fill(s, column.x + column.width - 23f, rowTop + 0.5f, 18f, 9f, bg)
            }
        }
    }

    private fun drawStatisticsLabels(s: PDFormContentStream, r: LayerResources) {
        centeredText(s, r.heading, 215f, 55f, 365f, 28f, "Estadísticas Personalizadas", 18f)
        centeredText(
            s, r.fira, 215f, 87f, 365f, 14f,
            "ATRIBUTOS PERSONALIZADOS Y HABILIDADES VINCULADAS", 7.5f,
        )
        centeredText(s, r.heading, 24f, 116f, 564f, 24f, "Definiciones", 16f)
        centeredText(s, r.heading, 24f, 428f, 564f, 26f, "Notas de Estadísticas Personalizadas", 18f)
        COLUMNS.forEach { column ->
            centeredText(s, r.heading, column.x + 2f, 243f, column.width - 4f, 22f, column.title, 18.5f)
            column.skills.forEachIndexed { rowIndex, skill ->
                compressedSourceMatchedLabel(
                    s, r.fira,
                    column.x + 11.874f,
                    SKILL_ROW_TOPS[rowIndex] + 9.142f,
                    skill.first,
                    column.width - 42f,
                )
            }
        }
    }

    private fun drawStatisticsValues(s: PDFormContentStream, r: LayerResources) {
        ruleText(s, r.fira, Rule(25f,181f,160f), "Honor: reputación, deber y prestigio.", 8.2f)
        ruleText(s, r.fira, Rule(215.291f,396.708f,160f), "Resolución: autocontrol bajo presión.", 8.2f)
        ruleText(s, r.fira, Rule(402.378f,583.795f,160f), "Suerte: azar favorable y oportunidades.", 8.2f)
        ruleText(s, r.fira, Rule(25f,181f,488f), "Honor: reputación y deber.", 8.4f)
        ruleText(s, r.fira, Rule(25f,181f,508f), "Prestigio y posición social.", 8.4f)
        ruleText(s, r.fira, Rule(215.291f,396.708f,488f), "Resolución: presión, miedo o dolor.", 8.4f)
        ruleText(s, r.fira, Rule(402.378f,583.795f,488f), "Suerte: azar favorable y oportunidades.", 8.4f)
        ruleText(s, r.fira, Rule(25f,181f,548f), "Cada habilidad conserva su Atributo.", 8.2f)

        COLUMNS.forEachIndexed { index, column ->
            centeredText(s, r.gill, SCORE_X[index] - 18f, 271f, 36f, 17f, column.score, 17f)
            centeredText(s, r.gill, MOD_X[index] - 10f, 289f, 20f, 10f, column.modifier, 10.6f)
            centeredText(s, r.gill, column.x + column.width - 23f, 308f, 18f, 9f, column.save, 8.2f)
            column.skills.forEachIndexed { rowIndex, skill ->
                centeredText(
                    s, r.gill,
                    column.x + column.width - 23f,
                    SKILL_ROW_TOPS[rowIndex] + 0.5f,
                    18f, 9f, skill.second, 7.6f,
                )
            }
        }
    }

    private fun drawStatisticsMarkers(s: PDFormContentStream, symbolFont: PDFont) {
        COLUMNS.forEachIndexed { index, column ->
            if (index < 3) approvedV8Marker(s, symbolFont, column.x + 5.3f, 316.4f, 8f, false)
            column.training.forEachIndexed { rowIndex, training ->
                when (training) {
                    Training.NONE -> Unit
                    Training.PROFICIENT -> approvedV8Marker(
                        s, symbolFont, column.x + 5.3f, SKILL_ROW_TOPS[rowIndex] + 6.2f, 7.2f, false,
                    )
                    Training.EXPERTISE -> approvedV8Marker(
                        s, symbolFont, column.x + 5.3f, SKILL_ROW_TOPS[rowIndex] + 6.2f, 7.2f, true,
                    )
                }
            }
        }
    }

    private fun drawTraitsCleanup(s: PDFormContentStream) {
        headingInteriorMask(s, 24f, 66f, 156f, 35f)
        headingInteriorMask(s, 24f, 205f, 156f, 35f)
        headingInteriorMask(s, 24f, 344f, 156f, 35f)
        headingInteriorMask(s, 24f, 483f, 156f, 35f)
        headingInteriorMask(s, 24f, 621f, 156f, 35f)
        headingInteriorMask(s, 215f, 344f, 369f, 35f)
    }

    private fun drawTraitsLabels(s: PDFormContentStream, r: LayerResources) {
        centeredText(s, r.heading, 24f, 66f, 156f, 35f, "Rasgos de Clase", 18f)
        centeredText(s, r.heading, 24f, 205f, 156f, 35f, "Rasgos de Especie", 18f)
        centeredText(s, r.heading, 24f, 344f, 156f, 35f, "Dotes", 18f)
        centeredText(s, r.heading, 24f, 483f, 156f, 35f, "Competencias", 18f)
        centeredText(s, r.heading, 24f, 621f, 156f, 35f, "Idiomas", 18f)
        centeredText(s, r.heading, 215f, 344f, 369f, 35f, "Detalles de Rasgos", 18f)
    }

    private fun drawTraitsValues(s: PDFormContentStream, r: LayerResources) {
        ruledValues(s,r.fira,25f,181f,listOf(109.5f,129.5f,149.5f),
            listOf("Portento","Recuperación arcana","Acción astuta"),8.6f)
        ruledValues(s,r.fira,25f,181f,listOf(248.5f,268.5f,287.5f),
            listOf("Visión en la oscuridad","Ascendencia feérica","Trance"),8.6f)
        ruledValues(s,r.fira,25f,181f,listOf(387.5f,407.5f,426f),
            listOf("Observador","Mente aguda"),8.6f)
        ruledValues(s,r.fira,25f,181f,listOf(526.5f,546f,565f),
            listOf("Herramientas de ladrón","Caligrafía"),8.6f)
        ruledValues(s,r.fira,25f,181f,listOf(665.5f,685f,704f),
            listOf("Común","Élfico","Dracónico"),8.6f)
        ruledValues(
            s,r.fira,215.291f,396.708f,
            listOf(109.5f,129.5f,149.5f,169f,189f,209f),
            listOf("Investigador","Código de campo","Afinidad ritual","Contacto de Academia","Memoria de archivo","Disciplina de estudio"),
            8.8f,
        )
        ruledValues(
            s,r.fira,215.291f,583.795f,
            listOf(387.996f,407.839f,427.681f,447.524f),
            listOf(
                "Portento: conserva resultados y puede sustituir una tirada apropiada.",
                "Investigador: sabe dónde buscar información académica especializada.",
                "Código de campo: sistema abreviado para registrar rutas, sellos y peligros.",
                "Afinidad ritual: mantiene procedimientos y anotaciones para magia ritual.",
            ),
            8.5f,
        )
        ruledValues(
            s,r.fira,215.291f,583.795f,
            listOf(606f,625.5f,645.5f,665.5f,685f),
            listOf(
                "Espacio deliberadamente libre para nuevos rasgos.",
                "La continuación conserva la pauta de la página narrativa original.",
            ),
            8.5f,
        )
    }

    private fun drawResourcesCleanup(s: PDFormContentStream) {
        headingInteriorMask(s,160f,66f,250f,35f)
        headingInteriorMask(s,438f,66f,150f,35f)
        headingInteriorMask(s,446f,243f,142f,36f)
        headingInteriorMask(s,205f,460f,200f,35f)
        labelInteriorMask(s,27f,94f,110f,14f)
        labelInteriorMask(s,169.9f,94f,130f,14f)
        labelInteriorMask(s,311.7f,94f,130f,14f)
        labelInteriorMask(s,456f,274f,62f,16f)
        labelInteriorMask(s,548f,274f,40f,16f)
        labelInteriorMask(s,50f,490f,68f,16f)
        labelInteriorMask(s,150f,490f,78f,16f)
        labelInteriorMask(s,385f,490f,90f,16f)

        listOf(99f,119f,139f,159f,179f).forEach { top ->
            fill(s,456f,top,92f,12f,Color.WHITE)
            fill(s,520f,top,61f,12f,Color.WHITE)
        }
        listOf(522.5f,562f,602f,641.5f).forEach { ruleY ->
            fill(s,35f,ruleY - 17f,74f,14f,Color.WHITE)
        }
    }

    private fun drawResourcesLabels(s: PDFormContentStream, r: LayerResources) {
        centeredText(s,r.heading,160f,66f,250f,35f,"Recursos",18f)
        centeredText(s,r.heading,438f,66f,150f,35f,"Recuperación",18f)
        centeredText(s,r.heading,446f,243f,142f,36f,"Estados",18f)
        centeredText(s,r.heading,205f,460f,200f,35f,"Opciones",18f)
        centeredText(s,r.fira,27f,94f,110f,14f,"RECURSO",7.5f)
        centeredText(s,r.fira,169.9f,94f,130f,14f,"ACTUAL / MÁX.",7.5f)
        centeredText(s,r.fira,311.7f,94f,130f,14f,"RECUPERACIÓN",7.5f)
        centeredText(s,r.fira,456f,274f,62f,16f,"ESTADO",7.5f)
        centeredText(s,r.fira,548f,274f,40f,16f,"VALOR",7.5f)
        centeredText(s,r.fira,50f,490f,68f,16f,"TIPO",7.5f)
        centeredText(s,r.fira,150f,490f,78f,16f,"OPCIÓN",7.5f)
        centeredText(s,r.fira,385f,490f,90f,16f,"DESCRIPCIÓN",7.5f)
    }

    private fun drawResourcesValues(s: PDFormContentStream, r: LayerResources) {
        val rows = listOf(
            Triple("Recuperación arcana","1 / 1","Descanso largo"),
            Triple("Dados de portento","1 / 2","Descanso largo"),
            Triple("Carga del monóculo","2 / 3","Amanecer"),
            Triple("Inspiración heroica","1 / 1","Variable"),
        )
        val y = listOf(128.5f,168f,208f,247.5f,287.5f,327f)
        rows.forEachIndexed { i,row ->
            ruleText(s,r.fira,Rule(27.5f,137.5f,y[i]),row.first,8.5f)
            ruleText(s,r.fira,Rule(169.937f,300.331f,y[i]),row.second,8.5f)
            ruleText(s,r.fira,Rule(311.669f,442.063f,y[i]),row.third,8.5f)
        }

        val recoveryLabels = listOf("DESCANSO CORTO","DESCANSO LARGO","AMANECER","OTRO","VARIABLE")
        val recY = listOf(99f,119f,139f,159f,179f)
        recoveryLabels.forEachIndexed { i,label ->
            leftBaselineText(s,r.fira,456f,recY[i] + 10f,label,6.8f)
            centeredText(s,r.gill,520f,recY[i],61f,12f,listOf("0","2","1","0","1")[i],8.2f)
        }

        val states = listOf("Oculto" to "NO","Asustado" to "NO","Concentración" to "SÍ")
        val stateY = listOf(307f,347f,386.5f)
        states.forEachIndexed { i,st ->
            ruleText(s,r.fira,Rule(453.402f,546.945f,stateY[i]),st.first,8.2f)
            ruleText(s,r.fira,Rule(549.779f,583.795f,stateY[i]),st.second,8.2f)
        }

        val options = listOf(
            Triple("Clase","Tradición de Adivinación","Portento y capacidades de adivinación."),
            Triple("Competencia","Herramientas de ladrón","Cerraduras, trampas y mecanismos."),
            Triple("Objeto","Monóculo rúnico","Inspección arcana; consume cargas."),
            Triple("Trasfondo","Investigador","Acceso a redes y fuentes académicas."),
        )
        val optionY = listOf(522.5f,562f,602f,641.5f)
        options.forEachIndexed { i,row ->
            centeredText(s,r.heading,35f,optionY[i]-18f,74f,22f,row.first,13.5f)
            ruleText(s,r.fira,Rule(126f,238f,optionY[i]),row.second,8.2f)
            ruleText(s,r.fira,Rule(240.803f,583.795f,optionY[i]),row.third,8.2f)
        }
    }

    private fun drawInventoryValues(s: PDFormContentStream, r: LayerResources) {
        val items = listOf(
            "12 x Clavos","8 x Tiza","6 x Viales","2 x Tinta azul","Espejo de acero","Manta",
            "Martillo pequeño","Linterna cubierta","4 x Aceite","Cuerda de seda","Campanillas","Estuche de mapas",
        )
        val ys = listOf(128.5f,168f,208f,247.5f,287.5f,327f,366.5f,406.5f)
        var itemIndex = 0
        ys.forEach { yy ->
            listOf(27.5f to 137.5f,169.937f to 300.331f,311.669f to 442.063f).forEach { (a,b) ->
                if (itemIndex < items.size) ruleText(s,r.fira,Rule(a,b,yy),items[itemIndex++],8.6f)
            }
        }
        val valuables = listOf("Gema lunar" to "120","Broche élfico" to "75","Láminas de plata" to "45")
        listOf(307f,347f,386.5f).forEachIndexed { i,yy ->
            valuables.getOrNull(i)?.let {
                ruleText(s,r.fira,Rule(453.402f,546.945f,yy),it.first,8.2f)
                ruleText(s,r.gill,Rule(549.779f,583.795f,yy),it.second,8.2f)
            }
        }
        val special = listOf(
            "Capucha rúnica" to "Tela tratada para proteger inscripciones.",
            "Lente de búsqueda" to "Ayuda a inspeccionar detalles finos.",
            "Amuleto del archivo" to "Recuerdo y foco ceremonial.",
            "Anillo de cobre" to "Marca de acceso menor.",
        )
        listOf(522.5f,542.5f,562f,582f).forEachIndexed { i,yy ->
            special.getOrNull(i)?.let {
                ruleText(s,r.fira,Rule(126f,238f,yy),it.first,8.2f)
                ruleText(s,r.fira,Rule(240.803f,583.795f,yy),it.second,8.2f)
            }
        }
    }

    private fun drawSpellValues(s: PDFormContentStream, r: LayerResources) {
        val continuation = mapOf(
            0 to listOf("Mensaje","Ilusión menor"),
            1 to listOf("Identificar","Dormir","Alarma","Comprender idiomas"),
            2 to listOf("Telaraña","Abrir","Detectar pensamientos"),
            3 to listOf("Disipar magia","Relámpago","Círculo mágico","Clarividencia"),
            4 to listOf("Destierro","Confusión","Localizar criatura"),
            5 to listOf("Contacto con otro plano","Modificar memoria","Telepatía"),
            6 to listOf("Globo de invulnerabilidad","Visión verdadera"),
            7 to listOf("Jaula de fuerza","Simulacro"),
            8 to listOf("Laberinto","Mente en blanco"),
            9 to listOf("Prisión","Detener el tiempo"),
        )
        spellRows().forEach { block ->
            val spells = continuation[block.level].orEmpty()
            spells.take(block.rules.size).forEachIndexed { i,spell ->
                ruleText(s,r.fira,block.rules[i],spell,8.7f,11.5f)
            }
            block.slotRule?.let { rule ->
                val value = mapOf(1 to "4",2 to "3",3 to "3",4 to "3",5 to "2",6 to "2",7 to "1",8 to "1",9 to "1")[block.level]
                if (value != null) ruleText(s,r.gill,rule,value,9f)
            }
        }
    }

    private fun drawSpellMarkers(s: PDFormContentStream, symbolFont: PDFont) {
        spellRows().forEach { block ->
            if (block.level == 0) return@forEach
            block.checkboxCenters.forEachIndexed { i,center ->
                if (i % 2 == 0) approvedV8Marker(s,symbolFont,center.first,center.second,7f,false)
            }
        }
    }

    private fun drawNotesValues(s: PDFormContentStream, r: LayerResources) {
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
        left.forEachIndexed { i,value -> ruleText(s,r.fira,Rule(25f,267.5f,ys[i]),value,8.4f) }
        right.forEachIndexed { i,value -> ruleText(s,r.fira,Rule(311.669f,583.795f,ys[i]),value,8.4f) }
    }

    private fun appendLayer(
        doc: PDDocument,
        page: PDPage,
        name: String,
        draw: (PDFormContentStream) -> Unit,
    ) {
        val form = PDFormXObject(doc).apply {
            resources = PDResources()
            setBBox(PDRectangle(W,H))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            draw(stream)
        }
        LayerUtility(doc).appendFormAsLayer(
            page,
            form,
            AffineTransform(),
            "$name #${++layerSerial}",
        )
    }

    private fun headingInteriorMask(s: PDFormContentStream,x: Float,top: Float,width: Float,height: Float) =
        fill(s,x+3f,top+5f,width-6f,height-10f,Color.WHITE)

    private fun labelInteriorMask(s: PDFormContentStream,x: Float,top: Float,width: Float,height: Float) =
        fill(s,x+2f,top+2f,width-4f,height-4f,Color.WHITE)

    private fun ruledValues(
        s: PDFormContentStream,
        font: PDFont,
        startX: Float,
        endX: Float,
        rules: List<Float>,
        values: List<String>,
        size: Float,
    ) {
        rules.forEachIndexed { i,y ->
            val value = values.getOrElse(i) { "" }
            if (value.isNotBlank()) ruleText(s,font,Rule(startX,endX,y),value,size)
        }
    }

    private fun ruleText(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        value: String,
        size: Float,
        leftPadding: Float = 2f,
    ) {
        if (value.isBlank()) return
        val available = rule.endX - rule.startX - leftPadding - 1f
        var actual = size
        while (actual > 5.8f && font.getStringWidth(value) / 1000f * actual > available) actual -= 0.2f
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font,actual)
        s.newLineAtOffset(rule.startX + leftPadding,H - rule.topY + 3.2f)
        s.showText(value)
        s.endText()
    }

    private fun centeredText(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        var actual = size
        while (actual > 5.8f && font.getStringWidth(value) / 1000f * actual > width - 2f) actual -= 0.2f
        val textWidth = font.getStringWidth(value) / 1000f * actual
        val descriptor = requireNotNull(font.fontDescriptor)
        val ascent = descriptor.ascent / 1000f * actual
        val descent = descriptor.descent / 1000f * actual
        val boxY = H - top - height
        val baseline = boxY + (height - (ascent - descent)) / 2f - descent
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font,actual)
        s.newLineAtOffset(x + (width - textWidth) / 2f,baseline)
        s.showText(value)
        s.endText()
    }

    private fun leftBaselineText(s: PDFormContentStream,font: PDFont,x: Float,baselineTop: Float,value: String,size: Float) {
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font,size)
        s.newLineAtOffset(x,H-baselineTop)
        s.showText(value)
        s.endText()
    }

    private fun compressedSourceMatchedLabel(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        baselineTop: Float,
        value: String,
        maximumWidth: Float,
    ) {
        val size = 10f
        val rawWidth = font.getStringWidth(value) / 1000f * size
        val scale = minOf(60f, maximumWidth / rawWidth * 100f)
        require(scale >= 50f) {
            "Run-4 source-matched label requires excessive compression: '$value' ($scale%)"
        }
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font,size)
        s.setHorizontalScaling(scale)
        s.newLineAtOffset(x,H-baselineTop)
        s.showText(value)
        s.endText()
    }

    private fun approvedV8Marker(
        s: PDFormContentStream,
        font: PDFont,
        centerX: Float,
        centerTop: Float,
        size: Float,
        expertise: Boolean,
    ) {
        val glyph = if (expertise) V8Glyph(0xE212,390,289,1237,1358) else V8Glyph(0xE211,285,400,1350,1306)
        val designWidth = (glyph.xMax - glyph.xMin).toFloat()
        val designHeight = (glyph.yMax - glyph.yMin).toFloat()
        val fontSize = size * SYMBOL_UNITS_PER_EM / max(designWidth,designHeight)
        val centerY = H - centerTop
        val originX = centerX - ((glyph.xMin + glyph.xMax) / 2f / SYMBOL_UNITS_PER_EM) * fontSize
        val originY = centerY - ((glyph.yMin + glyph.yMax) / 2f / SYMBOL_UNITS_PER_EM) * fontSize
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font,fontSize)
        s.newLineAtOffset(originX,originY)
        s.showText(String(Character.toChars(glyph.codePoint)))
        s.endText()
    }

    private fun sourceBands(s: PDFormContentStream,x1: Float,x2: Float,top: Float,rows: Int,step: Float) {
        repeat(rows) { i ->
            val rowTop = top + i * step
            if (i % 2 == 0) fill(s,x1,rowTop-step+1f,x2-x1,step-1f,SOURCE_GRAY)
            s.saveGraphicsState()
            s.setStrokingColor(Color.BLACK)
            s.setLineWidth(0.65f)
            s.moveTo(x1,H-rowTop)
            s.lineTo(x2,H-rowTop)
            s.stroke()
            s.restoreGraphicsState()
        }
    }

    private fun drawSourceCrop(s: PDFormContentStream,form: PDFormXObject,x: Float,top: Float,width: Float,height: Float) {
        s.saveGraphicsState()
        s.addRect(x,H-top-height,width,height)
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
        s.addRect(targetX,H-targetTop-height,width,height)
        s.clip()
        s.transform(Matrix.getTranslateInstance(targetX-sourceX,sourceTop-targetTop))
        s.drawForm(form)
        s.restoreGraphicsState()
    }

    private fun fill(s: PDFormContentStream,x: Float,top: Float,width: Float,height: Float,color: Color) {
        s.saveGraphicsState()
        s.setNonStrokingColor(color)
        s.addRect(x,H-top-height,width,height)
        s.fill()
        s.restoreGraphicsState()
    }

    private fun spellRows(): List<SpellBlock> {
        fun block(level: Int,x: Float,firstTop: Float,count: Int,slotY: Float?): SpellBlock {
            val rules = (0 until count).map { i -> Rule(x+11.3f,x+174f,firstTop+12.6f+i*19.84f) }
            val centers = (0 until count).map { i -> (x+4.9f) to (firstTop+6.1f+i*19.84f) }
            val slotRule = slotY?.let { Rule(x+39f,x+79f,it) }
            return SpellBlock(level,rules,centers,slotRule)
        }
        return listOf(
            block(0,28.2f,117.4f,4,null), block(1,28.2f,327.2f,10,317f),
            block(2,28.2f,573.8f,9,563.7f), block(3,215.3f,117.4f,10,107.3f),
            block(4,215.3f,356.5f,10,346.5f), block(5,215.3f,592.6f,8,582.6f),
            block(6,408.1f,117.4f,8,107.3f), block(7,408.1f,315.8f,7,305.7f),
            block(8,408.1f,494.4f,6,484.3f), block(9,408.1f,653.2f,5,643.1f),
        )
    }

    private fun protectedRegions(role: ExtensionRole): List<Region> = when (role) {
        ExtensionRole.CUSTOM_STATISTICS -> SCORE_X.map { center ->
            Region(center-31f,SCORE_BORDER_PROTECTED_TOP,62f,5f)
        }
        ExtensionRole.TRAITS_FEATURES -> listOf(
            Region(24f,108.7f,157f,1.1f),Region(24f,247.7f,157f,1.1f),
            Region(214f,386.9f,370f,1.1f),Region(214f,605f,370f,1.1f),
        )
        ExtensionRole.RESOURCES_OPTIONS -> listOf(
            Region(26f,127.8f,417f,1.1f),Region(26f,207.3f,417f,1.1f),
            Region(452f,306.3f,132f,1.1f),Region(125f,521.8f,459f,1.1f),
        )
        ExtensionRole.INVENTORY_EQUIPMENT -> listOf(
            Region(26f,127.8f,417f,1.1f),Region(26f,326.3f,417f,1.1f),
            Region(452f,346.3f,132f,1.1f),Region(125f,561.3f,459f,1.1f),
        )
        ExtensionRole.SPELLS -> listOf(
            Region(39f,129.2f,165f,1f),Region(39f,339f,165f,1f),
            Region(226f,129.2f,164f,1f),Region(419f,129.2f,163f,1f),
        )
        ExtensionRole.NOTES -> listOf(
            Region(24f,108.8f,244f,1f),Region(311f,108.8f,273f,1f),
            Region(24f,168.3f,244f,1f),Region(311f,168.3f,273f,1f),
        )
    }

    private fun assertRegionImagesEqual(
        expected: BufferedImage,
        actual: BufferedImage,
        region: Region,
        dpi: Float,
        message: String,
    ) {
        val scale = dpi / 72f
        val x0 = (region.x * scale).toInt().coerceAtLeast(0)
        val y0 = (region.top * scale).toInt().coerceAtLeast(0)
        val x1 = ((region.x + region.width) * scale).toInt().coerceAtMost(expected.width - 1)
        val y1 = ((region.top + region.height) * scale).toInt().coerceAtMost(expected.height - 1)
        for (y in y0..y1) for (x in x0..x1) {
            val expectedRgb = expected.getRGB(x,y)
            if (isDarkGeometryPixel(expectedRgb)) {
                assertTrue(
                    isDarkGeometryPixel(actual.getRGB(x,y), actualThreshold = 205),
                    "$message Source dark geometry erased at ($x,$y)",
                )
            }
        }
    }

    private fun isDarkGeometryPixel(rgb: Int, actualThreshold: Int = 170): Boolean {
        val red = rgb shr 16 and 0xFF
        val green = rgb shr 8 and 0xFF
        val blue = rgb and 0xFF
        val luma = (red * 299 + green * 587 + blue * 114) / 1000
        return luma <= actualThreshold
    }

    private fun assertImagesEqual(expected: BufferedImage,actual: BufferedImage,message: String) {
        assertEquals(expected.width,actual.width,message)
        assertEquals(expected.height,actual.height,message)
        val a = IntArray(expected.width)
        val b = IntArray(expected.width)
        repeat(expected.height) { y ->
            expected.getRGB(0,y,expected.width,1,a,0,expected.width)
            actual.getRGB(0,y,actual.width,1,b,0,actual.width)
            assertTrue(a.contentEquals(b),"$message First differing raster row: $y")
        }
    }

    private fun loadEmbeddedSourceFont(target: PDDocument,source: PDDocument,token: String): PDFont {
        source.pages.forEach { page ->
            val resources: PDResources = page.resources ?: return@forEach
            resources.fontNames.forEach { name ->
                val font = resources.getFont(name)
                if (font.name.contains(token,ignoreCase=true)) {
                    val descriptor = requireNotNull(font.fontDescriptor)
                    val stream = descriptor.fontFile2 ?: descriptor.fontFile ?: descriptor.fontFile3
                    requireNotNull(stream) { "Source font $token is not embedded." }
                    val bytes = stream.createInputStream().use { it.readBytes() }
                    return PDType0Font.load(target,ByteArrayInputStream(bytes),false)
                }
            }
        }
        error("Embedded source font not found: $token")
    }

    private fun loadResourceFont(doc: PDDocument,path: String): PDFont =
        resource(path).use { PDType0Font.load(doc,it,true) }

    private fun resource(path: String): InputStream =
        requireNotNull(DesktopPcSheetCustomV1ExtendedFamilyRun4Test::class.java.classLoader.getResourceAsStream(path)) {
            "Missing test resource: $path"
        }

    private data class LayerResources(
        val forms: List<PDFormXObject>,
        val heading: PDFont,
        val gill: PDFont,
        val fira: PDFont,
        val symbol: PDFont,
    ) {
        companion object {
            fun load(doc: PDDocument,source: PDDocument): LayerResources {
                val owner = DesktopPcSheetCustomV1ExtendedFamilyRun4Test()
                val utility = LayerUtility(doc)
                return LayerResources(
                    forms = (0 until 5).map { utility.importPageAsForm(source,it) },
                    heading = owner.loadEmbeddedSourceFont(doc,source,"EnchantedLand"),
                    gill = owner.loadEmbeddedSourceFont(doc,source,"GillSansMT"),
                    fira = owner.loadResourceFont(doc,FIRA_RESOURCE),
                    symbol = owner.loadResourceFont(doc,SYMBOL_RESOURCE),
                )
            }
        }
    }

    private data class Rule(val startX: Float,val endX: Float,val topY: Float)
    private data class Region(val x: Float,val top: Float,val width: Float,val height: Float)
    private data class V8Glyph(val codePoint: Int,val xMin: Int,val yMin: Int,val xMax: Int,val yMax: Int)
    private data class SpellBlock(
        val level: Int,
        val rules: List<Rule>,
        val checkboxCenters: List<Pair<Float,Float>>,
        val slotRule: Rule?,
    )
    private data class AttributeColumn(
        val x: Float,
        val width: Float,
        val gray: Boolean,
        val title: String,
        val score: String,
        val modifier: String,
        val save: String,
        val skills: List<Pair<String,String>>,
        val training: List<Training>,
    )

    private enum class Training { NONE, PROFICIENT, EXPERTISE }
    private enum class ExtensionRole(val slug: String) {
        CUSTOM_STATISTICS("custom-statistics"),
        TRAITS_FEATURES("traits-features"),
        RESOURCES_OPTIONS("resources-options"),
        INVENTORY_EQUIPMENT("inventory-equipment"),
        SPELLS("spells"),
        NOTES("notes"),
    }
    private enum class LayerStage(val slug: String) {
        STRUCTURE("structure"),
        CLEANUP("cleanup"),
        LABELS("labels"),
        VALUES("values"),
        MARKERS("markers"),
    }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val BASELINE_COMPARE_DPI = 120f
        const val DIAGNOSTIC_DPI = 220f
        const val REVIEW_DPI = 220f
        const val SYMBOL_UNITS_PER_EM = 2048f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf"
        const val FIRA_RESOURCE = "fonts/pdf/text/FiraSans-Regular.ttf"
        const val SYMBOL_RESOURCE = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"
        const val SOURCE_BLOCK_TOP_WITH_MARGIN = 232f
        const val SOURCE_BLOCK_HEIGHT_WITH_MARGIN = 174f
        const val SCORE_BORDER_PROTECTED_TOP = 264f

        val SOURCE_GRAY = Color(211,210,210)
        val SCORE_X = listOf(58f,154.25f,250.75f,347.25f,445.5f,539.75f)
        val MOD_X = listOf(85f,184.25f,280.5f,376.75f,473.25f,569.5f)
        val SKILL_ROW_TOPS = listOf(324.1f,338.2f,352.4f,366.6f,380.8f)
        val COLUMNS = listOf(
            AttributeColumn(
                22.5f,96.4f,false,"Honor","15","+2","+5",
                listOf("Etiqueta cortesana" to "+5","Reputación" to "+5","Deber" to "+2","Protocolo" to "+5"),
                listOf(Training.PROFICIENT,Training.PROFICIENT,Training.NONE,Training.PROFICIENT),
            ),
            AttributeColumn(
                118.9f,96.4f,true,"Resolución","12","+1","+1",
                listOf("Concentración" to "+4","Resistir miedo" to "+4","Autocontrol" to "+1"),
                listOf(Training.PROFICIENT,Training.PROFICIENT,Training.NONE),
            ),
            AttributeColumn(
                215.3f,96.4f,false,"Suerte","18","+4","+7",
                listOf("Lectura de fortuna" to "+7","Escapismo" to "+10","Azar" to "+4"),
                listOf(Training.PROFICIENT,Training.EXPERTISE,Training.NONE),
            ),
            AttributeColumn(
                311.7f,96.4f,true,"INTeligencia","18","+4","+7",
                listOf("Cifras antiguas" to "+7","Análisis de runas" to "+7"),
                listOf(Training.PROFICIENT,Training.PROFICIENT),
            ),
            AttributeColumn(
                408.0f,96.4f,false,"SABiduría","12","+1","+1",
                listOf("Cartografía" to "+4","Orientación astral" to "+1"),
                listOf(Training.PROFICIENT,Training.NONE),
            ),
            AttributeColumn(
                504.4f,85.5f,true,"DEStreza","16","+3","+6",
                listOf("Acrobacia aérea" to "+6","Cerrajería fina" to "+9"),
                listOf(Training.PROFICIENT,Training.EXPERTISE),
            ),
        )
    }
}
