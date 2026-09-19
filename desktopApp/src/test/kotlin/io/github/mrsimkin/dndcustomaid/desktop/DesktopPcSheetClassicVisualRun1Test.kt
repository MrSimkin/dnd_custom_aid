package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer

/**
 * First owner-review candidate for the application-designed Classic D&D-style family.
 *
 * D-0074 requires a familiar D&D information organization without copying an official published
 * sheet. This QA proof is therefore generated from blank Letter pages: no external character-sheet
 * template is used.
 */
class DesktopPcSheetClassicVisualRun1Test {
    private val overflowDiagnostics = mutableListOf<String>()

    @Test
    fun rendersClassicDndStyleVisualRun1() {
        overflowDiagnostics.clear()
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val pdf = File(proofDir, "classic-dnd-style-run1.pdf")

        PDDocument().use { doc ->
            val fonts = DesktopPdfFontRegistry(doc, CLASSIC_THEME)
            val p = DesktopPdfRenderingPrimitives(fonts)

            drawMain(doc, p)
            drawEquipment(doc, p)
            drawNarrative(doc, p)
            drawSpells(doc, p)
            drawNotes(doc, p)
            drawExtendedCustomStatistics(doc, p)

            doc.save(pdf)
        }

        Loader.loadPDF(pdf).use { doc ->
            assertEquals(6, doc.numberOfPages)
            assertFalse(doc.isEncrypted)
            repeat(doc.numberOfPages) { index ->
                val page = doc.getPage(index)
                assertEquals(W, page.mediaBox.width, 0.01f)
                assertEquals(H, page.mediaBox.height, 0.01f)
            }
            val renderer = PDFRenderer(doc)
            repeat(doc.numberOfPages) { index ->
                val image = renderer.renderImageWithDPI(index, 180f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "classic-dnd-style-run1-page-${index + 1}.png"),
                    ),
                )
            }
        }

        if (overflowDiagnostics.isNotEmpty()) {
            File(proofDir, "classic-dnd-style-run1-overflows.txt")
                .writeText(overflowDiagnostics.joinToString("\n"))
        }
        assertTrue(pdf.length() > 10_000L)
    }

    private fun drawMain(doc: PDDocument, p: DesktopPdfRenderingPrimitives) {
        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            pageHeader(s, p, "Aster Vale", "CLASSIC  •  CHARACTER RECORD", "Mago 5 / Pícaro 2   •   Elfo Alto   •   Nivel 7")

            section(s, p, 24f, 94f, 160f, 184f, "PORTRAIT")
            portraitPlaceholder(s, 34f, 121f, 140f, 142f)
            labelValue(s, p, 24f, 286f, 76f, 54f, "FUERZA", "10", "+0")
            labelValue(s, p, 108f, 286f, 76f, 54f, "DESTREZA", "16", "+3")
            labelValue(s, p, 24f, 348f, 76f, 54f, "CONSTITUCIÓN", "14", "+2")
            labelValue(s, p, 108f, 348f, 76f, 54f, "INTELIGENCIA", "18", "+4")
            labelValue(s, p, 24f, 410f, 76f, 54f, "SABIDURÍA", "12", "+1")
            labelValue(s, p, 108f, 410f, 76f, 54f, "CARISMA", "8", "-1")

            section(s, p, 24f, 476f, 160f, 92f, "PROFICIENCY")
            miniStat(s, p, 32f, 505f, 66f, 46f, "BONUS", "+3")
            markerStat(s, p, 104f, 505f, 72f, 46f, "INSPIRATION", PdfMarkerKind.STAR_FILLED)

            section(s, p, 196f, 94f, 198f, 206f, "SAVING THROWS")
            val saves = listOf(
                "Strength" to "+0", "Dexterity" to "+6", "Constitution" to "+2",
                "Intelligence" to "+7", "Wisdom" to "+1", "Charisma" to "-1",
            )
            saves.forEachIndexed { i, (name, value) ->
                row(s, p, 204f, 126f + i * 27f, 182f, name, value, checked = i == 1 || i == 3)
            }

            section(s, p, 196f, 312f, 198f, 338f, "SKILLS")
            val skills = listOf(
                "Acrobatics" to "+6", "Arcana" to "+7", "Athletics" to "+0", "Deception" to "-1",
                "History" to "+7", "Insight" to "+1", "Investigation" to "+7", "Medicine" to "+1",
                "Nature" to "+4", "Perception" to "+4", "Performance" to "-1", "Persuasion" to "-1",
                "Religion" to "+7", "Sleight of Hand" to "+6", "Stealth" to "+9", "Survival" to "+1",
            )
            skills.forEachIndexed { i, (name, value) ->
                row(s, p, 204f, 344f + i * 18.2f, 182f, name, value, checked = i in setOf(0, 1, 4, 6, 9, 13, 14), compact = true)
            }

            section(s, p, 406f, 94f, 182f, 152f, "COMBAT")
            miniStat(s, p, 414f, 126f, 50f, 50f, "AC", "16")
            miniStat(s, p, 470f, 126f, 50f, 50f, "INIT", "+4")
            miniStat(s, p, 526f, 126f, 54f, 50f, "SPEED", "30")
            miniStat(s, p, 414f, 184f, 80f, 48f, "HP", "27 / 34")
            miniStat(s, p, 500f, 184f, 80f, 48f, "HIT DICE", "4d6 / 1d8")

            section(s, p, 406f, 258f, 182f, 242f, "ATTACKS & ACTIONS")
            attackRow(s, p, 414f, 291f, "Bastón de fresno", "+3", "1d6 B")
            attackRow(s, p, 414f, 332f, "Rayo de fuego", "+7", "2d10 fuego")
            attackRow(s, p, 414f, 373f, "Daga de plata", "+6", "1d4+3 P")
            attackRow(s, p, 414f, 414f, "Arco corto", "+6", "1d6+3 P")
            attackRow(s, p, 414f, 455f, "Orbe cromático", "+7", "3d8 variable")

            section(s, p, 406f, 512f, 182f, 138f, "QUICK RESOURCES")
            resourceLine(s, p, 414f, 544f, "Arcane Recovery", "1 / 1")
            resourceLine(s, p, 414f, 573f, "Potion charges", "3 / 3")
            resourceLine(s, p, 414f, 602f, "Sneak Attack", "1d6")

            footer(s, p, 1, "MAIN • COMBAT • SKILLS")
        }
    }

    private fun drawEquipment(doc: PDDocument, p: DesktopPdfRenderingPrimitives) {
        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            pageHeader(s, p, "Aster Vale", "CLASSIC  •  EQUIPMENT & RESOURCES", "Carried gear, valuables, special equipment and expendable resources")

            section(s, p, 24f, 96f, 564f, 76f, "COIN & VALUE")
            val coins = listOf("CP" to "4", "SP" to "137", "EP" to "0", "GP" to "48", "PP" to "19")
            coins.forEachIndexed { i, (label, value) ->
                miniStat(s, p, 34f + i * 109f, 126f, 98f, 34f, label, value)
            }

            section(s, p, 24f, 184f, 350f, 338f, "EQUIPMENT")
            val gear = listOf(
                "Mochila de expedición", "Libro de conjuros", "Componentes arcanos", "Raciones x5",
                "Odre", "Capa gris", "Mapa del Valle", "Tiza x8", "Pergaminos x6", "Tinta azul x2",
                "Lupa de latón", "Martillo pequeño", "Clavos de hierro x12", "Linterna cubierta",
                "Aceite x4", "Manta", "Cuerda de seda", "Espejo de acero", "Viales vacíos x6",
                "Carcaj", "Cuaderno de campo", "Pluma fina",
            )
            gear.forEachIndexed { i, item ->
                val col = i / 11
                val row = i % 11
                simpleLine(s, p, 34f + col * 168f, 217f + row * 25.5f, 158f, item)
            }

            section(s, p, 386f, 184f, 202f, 166f, "VALUABLES")
            listOf(
                "Broche élfico antiguo" to "75 gp",
                "Gema lunar tallada" to "120 gp",
                "Láminas de plata" to "45 gp",
                "Mosaico con sello azul" to "25 gp",
            ).forEachIndexed { i, (name, value) ->
                itemWithValue(s, p, 396f, 218f + i * 31f, 182f, name, value)
            }

            section(s, p, 386f, 362f, 202f, 160f, "RESOURCES")
            resourceLine(s, p, 396f, 396f, "Arcane Recovery", "1 / 1")
            resourceLine(s, p, 396f, 428f, "Healing Potions", "3")
            resourceLine(s, p, 396f, 460f, "Hit Dice", "4d6 / 1d8")
            resourceLine(s, p, 396f, 492f, "Inspiration", "available")

            section(s, p, 24f, 534f, 564f, 170f, "SPECIAL / ATTUNED EQUIPMENT")
            val special = listOf(
                "Diadema del Archivo" to "Marca ceremonial de acceso.",
                "Monóculo rúnico" to "Ayuda a inspeccionar glifos finos.",
                "Amuleto de Liria" to "Recuerdo de la Academia.",
                "Anillo académico" to "Sello para archivos restringidos.",
                "Daga de plata" to "Hoja ligera tratada con plata.",
            )
            special.forEachIndexed { i, (name, note) ->
                val top = 568f + i * 25f
                checkbox(s, p, 34f, top + 2f, checked = i < 4)
                text(s, p, 52f, top, 170f, 18f, name, PdfTypographyRole.SPELL_NAME, 9.2f, 8f)
                text(s, p, 228f, top, 348f, 18f, note, PdfTypographyRole.BODY, 8.8f, 7.8f)
                hairline(s, 52f, top + 20f, 576f, top + 20f)
            }

            footer(s, p, 2, "EQUIPMENT • VALUE • RESOURCES")
        }
    }

    private fun drawNarrative(doc: PDDocument, p: DesktopPdfRenderingPrimitives) {
        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            pageHeader(s, p, "Aster Vale", "CLASSIC  •  FEATURES & STORY", "Background, personality, proficiencies and character features")

            section(s, p, 24f, 96f, 252f, 142f, "BACKGROUND")
            paragraph(s, p, 34f, 128f, 232f, 96f,
                "Sabio de la Academia de Liria. Aster dejó temporalmente los archivos tras hallar referencias a una cámara sellada bajo el Valle del Viento. Viaja para reconstruir la ruta y recuperar un códice perdido.")

            section(s, p, 24f, 250f, 252f, 108f, "PERSONALITY")
            paragraph(s, p, 34f, 282f, 232f, 62f,
                "Toma notas incluso en situaciones absurdas. Prefiere confirmar una teoría antes de actuar, pero rara vez abandona a un compañero.")

            section(s, p, 24f, 370f, 252f, 90f, "IDEAL")
            paragraph(s, p, 34f, 402f, 232f, 44f,
                "Conocimiento y responsabilidad: la información peligrosa debe estudiarse antes de compartirse.")

            section(s, p, 24f, 472f, 252f, 90f, "BOND")
            paragraph(s, p, 34f, 504f, 232f, 44f,
                "Prometió devolver a la Academia el códice perdido y proteger a sus compañeros durante la búsqueda.")

            section(s, p, 24f, 574f, 252f, 90f, "FLAW")
            paragraph(s, p, 34f, 606f, 232f, 44f,
                "Puede seguir investigando un detalle mucho después de que el resto del grupo haya decidido continuar.")

            section(s, p, 288f, 96f, 300f, 350f, "TRAITS & FEATURES")
            val features = listOf(
                "Darkvision" to "Ve en oscuridad hasta 60 ft.",
                "Trance" to "Meditación élfica en lugar de sueño normal.",
                "Arcane Recovery" to "Recupera espacios de conjuro tras un descanso corto.",
                "Sneak Attack" to "1d6 adicional una vez por turno cuando se cumplen sus condiciones.",
                "Cunning Action" to "Dash, Disengage o Hide como acción bonus.",
                "Ritual Casting" to "Puede lanzar como ritual conjuros apropiados del libro.",
                "Fey Ancestry" to "Ventaja contra ser hechizado; magia no puede dormirlo.",
                "Thieves' Cant" to "Código y signos usados por pícaros.",
            )
            features.forEachIndexed { i, (name, desc) ->
                val top = 130f + i * 38f
                text(s, p, 298f, top, 92f, 18f, name, PdfTypographyRole.SPELL_NAME, 9.5f, 8.5f)
                text(s, p, 394f, top, 184f, 31f, desc, PdfTypographyRole.BODY, 8.4f, 7.4f, wrap = true, maxLines = 2)
                if (i < features.lastIndex) hairline(s, 298f, top + 34f, 578f, top + 34f)
            }

            section(s, p, 288f, 458f, 300f, 98f, "PROFICIENCIES & LANGUAGES")
            paragraph(s, p, 298f, 490f, 280f, 52f,
                "Armas simples, espadas cortas, arcos cortos, herramientas de ladrón, caligrafía. Idiomas: Común, Élfico, Dracónico.")

            section(s, p, 288f, 568f, 300f, 96f, "APPEARANCE & NOTES")
            paragraph(s, p, 298f, 600f, 280f, 50f,
                "Cabello negro, ojos grises, capa de viaje. El sello azul de las ruinas coincide con símbolos hallados en monedas antiguas.")

            footer(s, p, 3, "FEATURES • STORY • PROFICIENCIES")
        }
    }

    private fun drawSpells(doc: PDDocument, p: DesktopPdfRenderingPrimitives) {
        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            pageHeader(s, p, "Aster Vale", "CLASSIC  •  SPELL LIST", "Wizard spellcasting • INT 18 • Save DC 15 • Attack +7")

            section(s, p, 24f, 96f, 564f, 70f, "SPELLCASTING")
            miniStat(s, p, 34f, 126f, 92f, 30f, "ABILITY", "INT")
            miniStat(s, p, 132f, 126f, 92f, 30f, "SAVE DC", "15")
            miniStat(s, p, 230f, 126f, 92f, 30f, "ATTACK", "+7")
            text(s, p, 340f, 124f, 238f, 34f, "Prepared checks are shown at left. Spent slots intentionally stay blank on generated sheets.",
                PdfTypographyRole.BODY, 8.2f, 7.2f, wrap = true, maxLines = 2)

            section(s, p, 24f, 178f, 564f, 70f, "SPELL SLOTS")
            val slots = listOf("1" to "4", "2" to "3", "3" to "3", "4" to "3", "5" to "2", "6" to "1", "7" to "1", "8" to "1", "9" to "1")
            slots.forEachIndexed { i, (level, total) ->
                val x = 34f + i * 59.8f
                miniStat(s, p, x, 208f, 52f, 30f, "L$level", total)
            }

            val columns = listOf(24f, 214f, 404f)
            val widths = 184f
            spellBlock(s, p, columns[0], 260f, widths, "CANTRIPS",
                listOf("Luz", "Mano de mago", "Rayo de fuego", "Prestidigitación", "Mensaje"), checks = false)
            spellBlock(s, p, columns[0], 432f, widths, "LEVEL 1",
                listOf("Escudo", "Misil mágico", "Detectar magia", "Caída de pluma", "Armadura de mago"), checks = true)

            spellBlock(s, p, columns[1], 260f, widths, "LEVEL 2",
                listOf("Imagen múltiple", "Paso brumoso", "Invisibilidad", "Levitar"), checks = true)
            spellBlock(s, p, columns[1], 432f, widths, "LEVEL 3",
                listOf("Contrahechizo", "Bola de fuego", "Volar", "Patrón hipnótico"), checks = true)

            spellBlock(s, p, columns[2], 260f, widths, "LEVEL 4",
                listOf("Puerta dimensional", "Invisibilidad superior", "Ojo arcano", "Polimorfar"), checks = true)
            spellBlock(s, p, columns[2], 432f, widths, "LEVEL 5+",
                listOf("Muro de fuerza", "Telequinesis", "Cono de frío", "Desintegrar", "Teletransportar", "Deseo"), checks = true)

            footer(s, p, 4, "SPELLS • SLOTS • PREPARATION")
        }
    }

    private fun drawNotes(doc: PDDocument, p: DesktopPdfRenderingPrimitives) {
        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            pageHeader(s, p, "Aster Vale", "CLASSIC  •  NOTES & REFERENCE", "Writable paper space is intentionally preserved")

            section(s, p, 24f, 96f, 274f, 542f, "CAMPAIGN NOTES")
            val noteText = listOf(
                "Contactar a Maestra Elenya al regresar a Liria.",
                "No entregar el mapa original a terceros.",
                "La puerta con sello azul responde al patrón visto en la torre.",
                "Comparar el alfabeto de la puerta norte con las notas del profesor Vael.",
                "Ruta: entrada oeste → cámara de columnas → escalera rota → galería azul → archivo inferior.",
                "Materiales: tinta, tiza, viales, espejo, cuerda y una linterna adicional.",
            )
            noteText.forEachIndexed { i, value ->
                text(s, p, 34f, 132f + i * 57f, 254f, 42f, value, PdfTypographyRole.NOTE_TEXT, 9f, 8f, wrap = true, maxLines = 3)
                hairline(s, 34f, 178f + i * 57f, 288f, 178f + i * 57f)
            }
            for (i in 0..5) hairline(s, 34f, 492f + i * 23f, 288f, 492f + i * 23f)

            section(s, p, 310f, 96f, 278f, 220f, "QUICK REFERENCE")
            val refs = listOf(
                "Passive Perception" to "14",
                "Passive Investigation" to "17",
                "Proficiency Bonus" to "+3",
                "Spell Save DC" to "15",
                "Spell Attack" to "+7",
            )
            refs.forEachIndexed { i, (name, value) ->
                itemWithValue(s, p, 322f, 132f + i * 33f, 254f, name, value)
            }

            section(s, p, 310f, 328f, 278f, 310f, "SKETCH / MAP")
            grid(s, 322f, 364f, 254f, 250f, 10, 10)

            footer(s, p, 5, "NOTES • REFERENCE • MAP")
        }
    }

    private fun drawExtendedCustomStatistics(doc: PDDocument, p: DesktopPdfRenderingPrimitives) {
        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            pageHeader(s, p, "Aster Vale", "CLASSIC  •  EXTENDED — CUSTOM STATISTICS", "Family-matched extension page • complete custom-stat reference")

            section(s, p, 24f, 102f, 564f, 132f, "CUSTOM ATTRIBUTES")
            customAttribute(s, p, 36f, 138f, 164f, "HONOR", "15", "+2", "+5")
            customAttribute(s, p, 218f, 138f, 164f, "RESOLVE", "12", "+1", "+1")
            customAttribute(s, p, 400f, 138f, 164f, "LUCK", "18", "+4", "+7")

            section(s, p, 24f, 246f, 564f, 344f, "CUSTOM ABILITIES / SKILLS")
            val customSkills = listOf(
                Triple("Court Etiquette", "HONOR", "+5"),
                Triple("Reputation", "HONOR", "+5"),
                Triple("Concentration", "RESOLVE", "+4"),
                Triple("Endure Fear", "RESOLVE", "+4"),
                Triple("Fortune Reading", "LUCK", "+7"),
                Triple("Improvised Escape", "LUCK", "+7"),
                Triple("Ancient Ciphers", "INT", "+7"),
                Triple("Field Cartography", "WIS", "+4"),
            )
            customSkills.forEachIndexed { i, (name, attr, total) ->
                val top = 282f + i * 34f
                checkbox(s, p, 36f, top + 2f, checked = i in setOf(0, 2, 4, 6))
                text(s, p, 54f, top, 264f, 20f, name, PdfTypographyRole.BODY, 9.4f, 8.2f)
                text(s, p, 330f, top, 84f, 20f, attr, PdfTypographyRole.SECONDARY_VALUE, 9f, 8f,
                    align = PdfHorizontalAlignment.CENTER)
                text(s, p, 430f, top, 134f, 20f, total, PdfTypographyRole.NUMERIC_COMPACT, 11f, 9f,
                    align = PdfHorizontalAlignment.RIGHT)
                hairline(s, 54f, top + 24f, 564f, top + 24f)
            }

            section(s, p, 24f, 602f, 564f, 80f, "EXTENSION SEMANTIC")
            paragraph(s, p, 36f, 634f, 540f, 34f,
                "This page demonstrates the Classic family’s extension language: same typography, border rhythm and spacing, while preserving a complete custom-stat reference without shrinking the base sheet below its readability floor.")

            footer(s, p, 6, "EXTENDED • CUSTOM STATISTICS")
        }
    }

    private fun pageHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        title: String,
        subtitle: String,
    ) {
        fillRect(s, 24f, 24f, 564f, 56f, HEADER_FILL)
        strokeRect(s, 24f, 24f, 564f, 56f, 1.1f)
        text(s, p, 36f, 32f, 260f, 28f, name, PdfTypographyRole.CHARACTER_NAME, 22f, 18f)
        text(s, p, 306f, 31f, 270f, 18f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 12f, 10f,
            align = PdfHorizontalAlignment.RIGHT)
        text(s, p, 306f, 52f, 270f, 16f, subtitle, PdfTypographyRole.BODY, 8f, 7f,
            align = PdfHorizontalAlignment.RIGHT)
    }

    private fun section(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        title: String,
    ) {
        fillRect(s, x, top, width, 24f, SECTION_FILL)
        strokeRect(s, x, top, width, height, 0.9f)
        hairline(s, x, top + 24f, x + width, top + 24f)
        text(s, p, x + 8f, top + 4f, width - 16f, 15f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 10.5f, 9f)
    }

    private fun portraitPlaceholder(s: PDPageContentStream, x: Float, top: Float, width: Float, height: Float) {
        val y = H - top - height
        s.saveGraphicsState()
        s.setStrokingColor(Color(80, 80, 80))
        s.setLineWidth(0.9f)
        s.addRect(x, y, width, height)
        s.stroke()
        val cx = x + width / 2f
        val headCy = y + height * 0.67f
        val r = 18f
        circle(s, cx, headCy, r)
        s.stroke()
        s.moveTo(cx, headCy - r)
        s.lineTo(cx, y + height * 0.28f)
        s.moveTo(cx, y + height * 0.53f)
        s.lineTo(cx - 34f, y + height * 0.38f)
        s.moveTo(cx, y + height * 0.53f)
        s.lineTo(cx + 34f, y + height * 0.38f)
        s.moveTo(cx, y + height * 0.28f)
        s.lineTo(cx - 28f, y + 8f)
        s.moveTo(cx, y + height * 0.28f)
        s.lineTo(cx + 28f, y + 8f)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun labelValue(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        score: String,
        modifier: String,
    ) {
        fillRect(s, x, top, width, height, CARD_FILL)
        strokeRect(s, x, top, width, height, 0.8f)
        text(s, p, x + 4f, top + 4f, width - 8f, 12f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7.4f, 6.5f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 6f, top + 17f, width - 34f, 30f, score, PdfTypographyRole.PRIMARY_VALUE, 19f, 17f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + width - 31f, top + 21f, 25f, 22f, modifier, PdfTypographyRole.SECONDARY_VALUE, 10.5f, 9f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun miniStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
    ) {
        fillRect(s, x, top, width, height, CARD_FILL)
        strokeRect(s, x, top, width, height, 0.7f)
        text(s, p, x + 3f, top + 3f, width - 6f, 12f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.8f, 6f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 4f, top + 15f, width - 8f, height - 18f, value, PdfTypographyRole.PRIMARY_VALUE, 15f, 10f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun markerStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        kind: PdfMarkerKind,
    ) {
        fillRect(s, x, top, width, height, CARD_FILL)
        strokeRect(s, x, top, width, height, 0.7f)
        text(s, p, x + 3f, top + 3f, width - 6f, 12f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.8f, 6f,
            align = PdfHorizontalAlignment.CENTER)
        p.drawMarker(
            s,
            centerX = x + width / 2f,
            centerY = H - top - 31f,
            sizePt = 13f,
            kind = kind,
            family = PdfSymbolFamily.V1_DERIVED,
        )
    }

    private fun row(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        name: String,
        value: String,
        checked: Boolean,
        compact: Boolean = false,
    ) {
        val h = if (compact) 15.5f else 22f
        checkbox(s, p, x, top + if (compact) 1.5f else 3f, checked)
        text(s, p, x + 18f, top, width - 52f, h, name, PdfTypographyRole.BODY,
            if (compact) 7.6f else 9f, if (compact) 6.8f else 8f)
        text(s, p, x + width - 34f, top, 30f, h, value, PdfTypographyRole.NUMERIC_COMPACT,
            if (compact) 8.2f else 10f, if (compact) 7.2f else 9f, align = PdfHorizontalAlignment.RIGHT)
        hairline(s, x + 18f, top + h + 1f, x + width, top + h + 1f)
    }

    private fun attackRow(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        name: String,
        attack: String,
        damage: String,
    ) {
        text(s, p, x, top, 100f, 16f, name, PdfTypographyRole.SPELL_NAME, 8.6f, 7.6f)
        text(s, p, x + 104f, top, 28f, 16f, attack, PdfTypographyRole.NUMERIC_COMPACT, 9f, 8f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 136f, top, 34f, 28f, damage, PdfTypographyRole.BODY, 7.4f, 6.6f, wrap = true, maxLines = 2)
        hairline(s, x, top + 32f, x + 170f, top + 32f)
    }

    private fun resourceLine(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        name: String,
        value: String,
    ) {
        text(s, p, x, top, 108f, 18f, name, PdfTypographyRole.BODY, 8.3f, 7.4f)
        text(s, p, x + 112f, top, 54f, 18f, value, PdfTypographyRole.NUMERIC_COMPACT, 9.5f, 8f,
            align = PdfHorizontalAlignment.RIGHT)
        hairline(s, x, top + 21f, x + 166f, top + 21f)
    }

    private fun simpleLine(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        value: String,
    ) {
        text(s, p, x, top, width, 18f, value, PdfTypographyRole.BODY, 8.6f, 7.5f)
        hairline(s, x, top + 21f, x + width, top + 21f)
    }

    private fun itemWithValue(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        name: String,
        value: String,
    ) {
        text(s, p, x, top, width - 68f, 18f, name, PdfTypographyRole.BODY, 8.5f, 7.5f)
        text(s, p, x + width - 64f, top, 64f, 18f, value, PdfTypographyRole.NUMERIC_COMPACT, 8.8f, 7.8f,
            align = PdfHorizontalAlignment.RIGHT)
        hairline(s, x, top + 21f, x + width, top + 21f)
    }

    private fun spellBlock(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        title: String,
        spells: List<String>,
        checks: Boolean,
    ) {
        section(s, p, x, top, width, 160f, title)
        spells.take(6).forEachIndexed { i, spell ->
            val rowTop = top + 32f + i * 20f
            if (checks) checkbox(s, p, x + 8f, rowTop + 2f, checked = i % 3 != 2)
            text(s, p, x + if (checks) 27f else 10f, rowTop, width - if (checks) 37f else 20f, 17f,
                spell, PdfTypographyRole.SPELL_NAME, 8.8f, 7.7f)
            hairline(s, x + if (checks) 27f else 10f, rowTop + 19f, x + width - 10f, rowTop + 19f)
        }
    }

    private fun customAttribute(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        label: String,
        score: String,
        mod: String,
        save: String,
    ) {
        fillRect(s, x, top, width, 76f, CARD_FILL)
        strokeRect(s, x, top, width, 76f, 0.8f)
        text(s, p, x + 8f, top + 5f, width - 16f, 16f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 9.5f, 8f)
        text(s, p, x + 8f, top + 22f, 60f, 38f, score, PdfTypographyRole.PRIMARY_VALUE, 22f, 18f,
            align = PdfHorizontalAlignment.CENTER)
        miniStat(s, p, x + 76f, top + 25f, 38f, 38f, "MOD", mod)
        miniStat(s, p, x + 120f, top + 25f, 36f, 38f, "SAVE", save)
    }

    private fun checkbox(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        checked: Boolean,
    ) {
        val cy = H - top - 6f
        p.drawMarker(
            s,
            centerX = x + 6f,
            centerY = cy,
            sizePt = 10f,
            kind = if (checked) PdfMarkerKind.CHECK else PdfMarkerKind.SQUARE_OUTLINE,
            family = PdfSymbolFamily.V1_DERIVED,
        )
    }

    private fun paragraph(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
    ) {
        text(s, p, x, top, width, height, value, PdfTypographyRole.BODY, 8.8f, 7.6f, wrap = true, maxLines = 8,
            vertical = PdfVerticalAlignment.TOP)
    }

    private fun text(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        role: PdfTypographyRole,
        preferred: Float,
        minimum: Float,
        wrap: Boolean = false,
        maxLines: Int = 1,
        align: PdfHorizontalAlignment = PdfHorizontalAlignment.LEFT,
        vertical: PdfVerticalAlignment = PdfVerticalAlignment.CENTER,
    ) {
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = rect(x, top, width, height),
                text = value,
                role = role,
                preferredSizePt = preferred,
                minimumSizePt = minimum,
                horizontalAlignment = align,
                verticalAlignment = vertical,
                wrapPolicy = if (wrap) PdfWrapPolicy.WORD_WRAP else PdfWrapPolicy.SINGLE_LINE,
                maximumLines = maxLines,
                horizontalPaddingPt = 1f,
                verticalPaddingPt = 0.5f,
            ),
        )
        if (result.hasOverflow) {
            overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
        }
    }

    private fun footer(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        pageNumber: Int,
        label: String,
    ) {
        hairline(s, 24f, 742f, 588f, 742f)
        text(s, p, 24f, 748f, 360f, 16f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7.5f, 6.5f)
        text(s, p, 506f, 748f, 82f, 16f, "PAGE $pageNumber", PdfTypographyRole.OPTIONAL_DECORATIVE, 7.5f, 6.5f,
            align = PdfHorizontalAlignment.RIGHT)
    }

    private fun grid(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        columns: Int,
        rows: Int,
    ) {
        val y = H - top - height
        s.saveGraphicsState()
        s.setStrokingColor(GRID_COLOR)
        s.setLineWidth(0.35f)
        s.addRect(x, y, width, height)
        s.stroke()
        repeat(columns - 1) { i ->
            val xx = x + width * (i + 1) / columns
            s.moveTo(xx, y)
            s.lineTo(xx, y + height)
        }
        repeat(rows - 1) { i ->
            val yy = y + height * (i + 1) / rows
            s.moveTo(x, yy)
            s.lineTo(x + width, yy)
        }
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun fillRect(
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

    private fun strokeRect(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        lineWidth: Float,
    ) {
        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(lineWidth)
        s.addRect(x, H - top - height, width, height)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun hairline(
        s: PDPageContentStream,
        x1: Float,
        top1: Float,
        x2: Float,
        top2: Float,
    ) {
        s.saveGraphicsState()
        s.setStrokingColor(LINE_COLOR)
        s.setLineWidth(0.35f)
        s.moveTo(x1, H - top1)
        s.lineTo(x2, H - top2)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun circle(s: PDPageContentStream, cx: Float, cy: Float, r: Float) {
        val c = r * 0.552284749831f
        s.moveTo(cx + r, cy)
        s.curveTo(cx + r, cy + c, cx + c, cy + r, cx, cy + r)
        s.curveTo(cx - c, cy + r, cx - r, cy + c, cx - r, cy)
        s.curveTo(cx - r, cy - c, cx - c, cy - r, cx, cy - r)
        s.curveTo(cx + c, cy - r, cx + r, cy - c, cx + r, cy)
        s.closePath()
    }

    private fun rect(x: Float, top: Float, width: Float, height: Float) =
        PdfRect(x, H - top - height, width, height)

    private fun addPage(doc: PDDocument): PDPage =
        PDPage(PDRectangle(W, H)).also(doc::addPage)

    private companion object {
        const val W = 612f
        const val H = 792f

        val HEADER_FILL = Color(232, 232, 232)
        val SECTION_FILL = Color(241, 241, 241)
        val CARD_FILL = Color(249, 249, 249)
        val LINE_COLOR = Color(145, 145, 145)
        val GRID_COLOR = Color(205, 205, 205)

        val CLASSIC_THEME = PdfTypographyTheme(
            id = "classic-dnd-style-run1",
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
