package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomAttribute
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkillAbilityConfiguration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOption
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOptionKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSavingThrow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorPreferences
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportPlanner
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper

class DesktopPcSheetWholeDraftRendererTest {
    @Test
    fun generatesWholeCustomFamilyFirstDraftsForOwnerReview() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregate()

        val families = listOf(
            Triple(PcSheetVisualFamily.CUSTOM_V1, "custom-v1-whole-draft", 5),
            Triple(PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE, "custom-v2-per-attribute-whole-draft", 4),
            Triple(PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY, "custom-v2-per-ability-whole-draft", 4),
        )

        families.forEach { (family, fileStem, expectedPages) ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(permanent = aggregate),
            )

            val pdf = File(proofDir, "$fileStem.pdf")
            pdf.outputStream().use { renderer.renderDraft(plan, it) }

            Loader.loadPDF(pdf).use { document ->
                assertEquals(expectedPages, document.numberOfPages)
                val extracted = PDFTextStripper().getText(document)
                if (family == PcSheetVisualFamily.CUSTOM_V1) {
                    val layerNames = document.documentCatalog.ocProperties
                        ?.getGroupNames()
                        ?.toList()
                        .orEmpty()
                    assertTrue(layerNames.any { it == "CustomV1 MAIN - Identification" })
                    assertTrue(layerNames.any { it == "CustomV1 MAIN - Skills DEX" })
                    assertTrue(layerNames.any { it == "CustomV1 SPELLS - Level 1" })

                    // Form/OCG text extraction is not a visual correctness contract.
                    // Direct residual pages still prove whole-sheet data coverage.
                    assertTrue(extracted.contains("Mochila de expedición"))
                    assertTrue(extracted.contains("Imagen múltiple"))
                } else {
                    assertTrue(extracted.contains("Aster Vale"))
                    assertTrue(extracted.contains("Mochila de expedición"))
                    assertTrue(extracted.contains("Sabio de la Academia"))
                    assertTrue(extracted.contains("Escudo"))
                }

                val pdfRenderer = PDFRenderer(document)
                repeat(document.numberOfPages) { pageIndex ->
                    val image = pdfRenderer.renderImageWithDPI(pageIndex, 120f, ImageType.RGB)
                    val png = File(proofDir, "$fileStem-page-${pageIndex + 1}.png")
                    assertTrue(ImageIO.write(image, "png", png))
                    assertTrue(png.length() > 0L)
                }
            }
            assertTrue(pdf.length() > 0L)
        }
    }

    @Test
    fun promotesOwnerApprovedCustomV2ExtendedCustomStatisticsFromRealPlanData() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithCustomStatistics()

        listOf(
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-per-attribute-production-pass1",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-per-ability-production-pass1",
        ).forEach { (family, stem) ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(permanent = aggregate),
            )

            val pdf = File(proofDir, "$stem.pdf")
            pdf.outputStream().use { renderer.renderDraft(plan, it) }

            Loader.loadPDF(pdf).use { document ->
                assertEquals(5, document.numberOfPages)
                val layerNames = document.documentCatalog.ocProperties
                    ?.getGroupNames()
                    ?.toList()
                    .orEmpty()
                if (family == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE) {
                    assertTrue(layerNames.contains("V2X ATTR - STRUCTURE"))
                    assertTrue(layerNames.contains("V2X ATTR - VALUES"))
                } else {
                    assertTrue(layerNames.contains("V2X ABILITY - STRUCTURE"))
                    assertTrue(layerNames.contains("V2X ABILITY - VALUES"))
                }

                val extracted = PDFTextStripper().getText(document)
                assertTrue(extracted.contains("ESTADÍSTICAS PERSONALIZADAS"))
                assertTrue(extracted.contains("HONor"))
                assertTrue(extracted.contains("VOLuntad"))
                assertTrue(extracted.contains("SUErte"))
                assertTrue(extracted.contains("Etiqueta"))
                assertTrue(extracted.contains("Criptografía"))
                assertTrue(extracted.contains("Acrobacia aérea"))

                val image = PDFRenderer(document).renderImageWithDPI(4, 220f, ImageType.RGB)
                val png = File(proofDir, "$stem-extended-page.png")
                assertTrue(ImageIO.write(image, "png", png))
                assertTrue(png.length() > 0L)
            }
            assertTrue(pdf.length() > 20_000L)
        }
    }

    @Test
    fun promotesOwnerApprovedCustomV2TraitsAndResourcesFromRealPlanData() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithExtendedManagement()

        listOf(
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-per-attribute-production-pass2",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-per-ability-production-pass2",
        ).forEach { (family, stem) ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(permanent = aggregate),
            )

            val pdf = File(proofDir, "$stem.pdf")
            pdf.outputStream().use { renderer.renderDraft(plan, it) }

            Loader.loadPDF(pdf).use { document ->
                assertEquals(7, document.numberOfPages)
                val layerNames = document.documentCatalog.ocProperties
                    ?.getGroupNames()
                    ?.toList()
                    .orEmpty()
                assertTrue(layerNames.contains("V2X TRAITS - STRUCTURE"))
                assertTrue(layerNames.contains("V2X TRAITS - VALUES"))
                assertTrue(layerNames.contains("V2X RESOURCES - STRUCTURE"))
                assertTrue(layerNames.contains("V2X RESOURCES - MARKERS"))

                val extracted = PDFTextStripper().getText(document)
                assertTrue(extracted.contains("RASGOS Y ATRIBUTOS"))
                assertTrue(extracted.contains("RECURSOS Y OPCIONES"))
                assertTrue(extracted.contains("Puntos de enfoque"))
                assertTrue(extracted.contains("Metamagia cuidadosa"))
                assertTrue(extracted.contains("7/12"))
                assertFalse(extracted.contains("Dados de portento", ignoreCase = true))
                assertFalse(extracted.contains("Especie", ignoreCase = true))

                listOf(5, 6).forEach { pageIndex ->
                    val image = PDFRenderer(document).renderImageWithDPI(pageIndex, 220f, ImageType.RGB)
                    val png = File(proofDir, "$stem-extended-page-${pageIndex + 1}.png")
                    assertTrue(ImageIO.write(image, "png", png))
                    assertTrue(png.length() > 0L)
                }
            }
            assertTrue(pdf.length() > 20_000L)
        }
    }

    private fun denseDraftAggregateWithExtendedManagement(): PcSheetExportAggregate {
        val base = denseDraftAggregateWithCustomStatistics()
        val traits = base.sheet.traits.toMutableList()
        traits[0] = traits[0].copy(
            maxUses = 2,
            spentUses = 1,
            recovery = "Descanso largo",
            notes = "El uso restante debe permanecer local al rasgo.",
        )

        val proficiencies = listOf(
            CharacterProficiency(
                id = uuid("83000000-0000-0000-0000-000000000001"),
                type = CharacterProficiencyType.TOOL,
                name = "Herramientas de ladrón",
                source = "Pícaro",
                sortOrder = 0,
            ),
            CharacterProficiency(
                id = uuid("83000000-0000-0000-0000-000000000002"),
                type = CharacterProficiencyType.LANGUAGE,
                name = "Élfico",
                source = "Raza",
                sortOrder = 1,
            ),
        )

        val resources = listOf(
            CharacterResource(
                id = uuid("84000000-0000-0000-0000-000000000001"),
                name = "Recuperación arcana",
                currentValue = 1,
                maxValue = 1,
                recovery = "Descanso largo",
                source = "Mago",
                sortOrder = 0,
            ),
            CharacterResource(
                id = uuid("84000000-0000-0000-0000-000000000002"),
                name = "Carga del monóculo",
                currentValue = 2,
                maxValue = 4,
                recovery = "Amanecer",
                source = "Objeto",
                sortOrder = 1,
            ),
            CharacterResource(
                id = uuid("84000000-0000-0000-0000-000000000003"),
                name = "Puntos de enfoque",
                currentValue = 7,
                maxValue = 12,
                recovery = "Descanso largo",
                source = "Clase",
                sortOrder = 2,
            ),
        )

        val options = listOf(
            CharacterClassOption(
                id = uuid("85000000-0000-0000-0000-000000000001"),
                kind = CharacterClassOptionKind.METAMAGIC,
                name = "Metamagia cuidadosa",
                source = "Prueba PDF",
                costText = "1 punto",
                effectSummary = "Protege objetivos elegidos durante una conjuración.",
                active = true,
                sortOrder = 0,
            ),
            CharacterClassOption(
                id = uuid("85000000-0000-0000-0000-000000000002"),
                kind = CharacterClassOptionKind.TECHNIQUE,
                name = "Lectura táctica",
                source = "Prueba PDF",
                costText = null,
                effectSummary = "Resume una técnica activa del personaje.",
                active = false,
                sortOrder = 1,
            ),
        )

        return base.copy(
            sheet = base.sheet.copy(
                traits = traits,
                proficiencies = proficiencies,
                resources = resources,
                classOptions = options,
            ),
        )
    }

    private fun denseDraftAggregateWithCustomStatistics(): PcSheetExportAggregate {
        val base = denseDraftAggregate()
        val honor = CharacterCustomAttribute(
            id = uuid("81000000-0000-0000-0000-000000000001"),
            name = "Honor",
            abbreviation = "HON",
            score = 15,
            savingThrowEnabled = true,
            savingThrowProficient = true,
            notes = "Presencia, reputación y protocolo. No sustituye CARisma salvo regla explícita.",
            sortOrder = 0,
        )
        val voluntad = CharacterCustomAttribute(
            id = uuid("81000000-0000-0000-0000-000000000002"),
            name = "Voluntad",
            abbreviation = "VOL",
            score = 12,
            savingThrowEnabled = true,
            savingThrowProficient = false,
            notes = "Temple, foco y resistencia mental. Las pruebas prolongadas pueden exigir concentración.",
            sortOrder = 1,
        )
        val suerte = CharacterCustomAttribute(
            id = uuid("81000000-0000-0000-0000-000000000003"),
            name = "Suerte",
            abbreviation = "SUE",
            score = 18,
            savingThrowEnabled = true,
            savingThrowProficient = true,
            notes = "Fortuna, azar e improvisación. Puede modificar consecuencias imprevistas.",
            sortOrder = 2,
        )

        fun customSkill(
            index: Int,
            name: String,
            builtIn: CharacterAbility,
            training: SkillTraining,
            adjustment: Int = 0,
        ) = CharacterCustomSkill(
            id = uuid("82000000-0000-0000-0000-${index.toString().padStart(12, '0')}"),
            name = name,
            ability = builtIn,
            training = training,
            adjustment = adjustment,
            source = "Prueba producción PDF",
            notes = null,
            sortOrder = index,
        )

        val etiqueta = customSkill(1, "Etiqueta", CharacterAbility.CHARISMA, SkillTraining.PROFICIENT)
        val reputacion = customSkill(2, "Reputación", CharacterAbility.CHARISMA, SkillTraining.PROFICIENT)
        val protocolo = customSkill(3, "Protocolo", CharacterAbility.CHARISMA, SkillTraining.NONE)
        val temple = customSkill(4, "Temple", CharacterAbility.WISDOM, SkillTraining.PROFICIENT)
        val concentracion = customSkill(5, "Concentración", CharacterAbility.CONSTITUTION, SkillTraining.NONE)
        val fortuna = customSkill(6, "Fortuna", CharacterAbility.WISDOM, SkillTraining.PROFICIENT)
        val escapismo = customSkill(7, "Escapismo", CharacterAbility.DEXTERITY, SkillTraining.NONE)
        val improvisacion = customSkill(8, "Improvisación", CharacterAbility.CHARISMA, SkillTraining.PROFICIENT)
        val ocultismo = customSkill(9, "Ocultismo", CharacterAbility.INTELLIGENCE, SkillTraining.PROFICIENT)
        val criptografia = customSkill(10, "Criptografía", CharacterAbility.INTELLIGENCE, SkillTraining.NONE)
        val acrobaciaAerea = customSkill(11, "Acrobacia aérea", CharacterAbility.DEXTERITY, SkillTraining.EXPERTISE)
        val lecturaCorporal = customSkill(12, "Lectura corporal", CharacterAbility.WISDOM, SkillTraining.PROFICIENT)

        val customSkills = listOf(
            etiqueta,
            reputacion,
            protocolo,
            temple,
            concentracion,
            fortuna,
            escapismo,
            improvisacion,
            ocultismo,
            criptografia,
            acrobaciaAerea,
            lecturaCorporal,
        )

        val customLinks = listOf(
            etiqueta to honor,
            reputacion to honor,
            protocolo to honor,
            temple to voluntad,
            concentracion to voluntad,
            fortuna to suerte,
            escapismo to suerte,
            improvisacion to suerte,
        ).map { (skill, attribute) ->
            CharacterCustomSkillAbilityConfiguration(
                customSkillId = skill.id,
                ability = CharacterAbilityReference.custom(attribute.id),
            )
        }

        return base.copy(
            closure = base.closure.copy(customSkills = customSkills),
            successor = base.successor.copy(
                customAttributes = listOf(honor, voluntad, suerte),
                customSkillAbilities = customLinks,
            ),
        )
    }

    private fun denseDraftAggregate(): PcSheetExportAggregate {
        val wizardId = uuid("10000000-0000-0000-0000-000000000001")
        val rogueId = uuid("10000000-0000-0000-0000-000000000002")
        val spellSourceId = uuid("20000000-0000-0000-0000-000000000001")

        val skills = SkillKey.entries.map { key ->
            CharacterSkill(
                key = key,
                adjustment = if (key == SkillKey.PERCEPTION) 1 else 0,
                training = when (key) {
                    SkillKey.ARCANA,
                    SkillKey.INVESTIGATION,
                    SkillKey.HISTORY,
                    SkillKey.ACROBATICS,
                    SkillKey.STEALTH,
                    -> SkillTraining.PROFICIENT
                    SkillKey.SLEIGHT_OF_HAND -> SkillTraining.EXPERTISE
                    else -> SkillTraining.NONE
                },
            )
        }
        val saves = CharacterAbility.entries.map { ability ->
            CharacterSavingThrow(
                ability = ability,
                proficient = ability == CharacterAbility.DEXTERITY || ability == CharacterAbility.INTELLIGENCE,
                adjustment = 0,
            )
        }

        val combat = listOf(
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000001"),
                name = "Bastón de fresno",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 3,
                damageEffect = "1d6 contundente",
                rangeText = "5 ft",
                notes = null,
                sortOrder = 0,
            ),
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000002"),
                name = "Rayo de fuego",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 7,
                damageEffect = "2d10 fuego",
                rangeText = "120 ft",
                notes = null,
                sortOrder = 1,
            ),
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000003"),
                name = "Daga de plata",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 6,
                damageEffect = "1d4+3 perforante",
                rangeText = "20/60 ft",
                notes = null,
                sortOrder = 2,
            ),
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000004"),
                name = "Arco corto",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 6,
                damageEffect = "1d6+3 perforante",
                rangeText = "80/320 ft",
                notes = null,
                sortOrder = 3,
            ),
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000005"),
                name = "Toque electrizante",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 7,
                damageEffect = "2d8 relámpago",
                rangeText = "Toque",
                notes = null,
                sortOrder = 4,
            ),
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000006"),
                name = "Honda",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 6,
                damageEffect = "1d4+3 contundente",
                rangeText = "30/120 ft",
                notes = null,
                sortOrder = 5,
            ),
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000007"),
                name = "Orbe cromático",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 7,
                damageEffect = "3d8 variable",
                rangeText = "90 ft",
                notes = null,
                sortOrder = 6,
            ),
            CharacterCombatEntry(
                id = uuid("30000000-0000-0000-0000-000000000008"),
                name = "Ataque furtivo",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 6,
                damageEffect = "+1d6 situacional",
                rangeText = "arma",
                notes = null,
                sortOrder = 7,
            ),
        )

        val traits = listOf(
            "Visión en la oscuridad" to "Ves en luz tenue y oscuridad como corresponde a tu linaje élfico.",
            "Trance" to "Cuatro horas de meditación sustituyen el descanso ordinario.",
            "Recuperación Arcana" to "Una vez al día recuperas espacios de conjuro después de un descanso corto.",
            "Ataque furtivo 1d6" to "Una vez por turno añades daño cuando cumples sus condiciones.",
            "Acción astuta" to "Puedes usar acción adicional para determinadas maniobras.",
            "Erudito arcano" to "Tu formación facilita investigar fenómenos y tradiciones mágicas.",
            "Paso feérico" to "Una breve traslación mágica útil para escapar de posiciones comprometidas.",
            "Lenguas élficas" to "Lees, escribes y hablas las lenguas aprendidas durante tu formación.",
            "Herramientas de ladrón" to "Entrenamiento práctico para mecanismos, cerraduras y trampas.",
            "Alerta académica" to "Mantienes notas rápidas sobre amenazas, símbolos y anomalías.",
            "Memoria de archivo" to "Recuerdas referencias y clasificaciones de documentos consultados.",
            "Afinidad ritual" to "Reconoces patrones comunes en procedimientos mágicos prolongados.",
            "Cartografía" to "Puedes reconstruir rutas y puntos de referencia con bastante precisión.",
            "Contacto de Academia" to "Conservas vínculos con investigadores y bibliotecarios de Liria.",
            "Observador" to "Prestas atención a pequeños cambios de escena y comportamiento.",
            "Código de campo" to "Utilizas marcas breves para registrar peligros y rutas seguras.",
            "Disciplina de estudio" to "Puedes mantener concentración durante largas sesiones de análisis.",
            "Improvisador" to "Combinas herramientas y recursos disponibles cuando falta el equipo ideal.",
        ).mapIndexed { index, (name, description) ->
            CharacterTrait(
                id = uuid("40000000-0000-0000-0000-${(index + 1).toString().padStart(12, '0')}"),
                name = name,
                source = "Prueba PDF",
                type = if (index < 2) CharacterTraitType.SPECIES_RACE else CharacterTraitType.CLASS,
                description = description,
                notes = null,
                maxUses = null,
                spentUses = 0,
                recovery = null,
                activation = CharacterActivationType.PASSIVE,
                sortOrder = index,
            )
        }

        val inventory = listOf(
            inventory(1, "Mochila de expedición", 1, 5.0, "Espalda", false, false, "Cuerda, yesquero y útiles de viaje."),
            inventory(2, "Libro de conjuros", 1, 3.0, "Mochila", false, false, "Notas arcanas y fórmulas personales."),
            inventory(3, "Bastón de fresno", 1, 4.0, "Mano derecha", true, true, "Foco arcano y arma improvisada."),
            inventory(4, "Daga de plata", 2, 1.0, "Cinturón", true, true, "Hoja ligera tratada con plata."),
            inventory(5, "Pociones de curación", 3, 0.5, "Mochila", false, false, "Frascos protegidos en estuche de cuero."),
            inventory(6, "Componentes arcanos", 1, 1.0, "Bolsa", false, false, "Materiales de lanzamiento de uso común."),
            inventory(7, "Raciones", 5, 2.0, "Mochila", false, false, "Comida seca para viaje."),
            inventory(8, "Odre", 1, 5.0, "Mochila", false, false, "Agua potable."),
            inventory(9, "Capa gris", 1, 2.0, "Hombros", false, true, "Capa de viaje con broche élfico."),
            inventory(10, "Anillo de la Academia", 1, null, "Mano izquierda", true, true, "Sello de acceso a archivos restringidos."),
            inventory(11, "Mapa del Valle", 1, null, "Tubo", false, false, "Anotaciones de rutas y ruinas antiguas."),
            inventory(12, "Tiza", 8, null, "Mochila", false, false, "Marcas para exploración."),
            inventory(13, "Pergaminos en blanco", 6, null, "Carpeta", false, false, "Hojas resistentes para copias de campo."),
            inventory(14, "Tinta azul", 2, 0.2, "Estuche", false, false, "Tinta para anotaciones permanentes."),
            inventory(15, "Lupa de latón", 1, 0.3, "Bolsillo", false, false, "Lente pequeña para inscripciones."),
            inventory(16, "Martillo pequeño", 1, 2.0, "Mochila", false, false, "Herramienta de exploración."),
            inventory(17, "Clavos de hierro", 12, 1.0, "Mochila", false, false, "Fijaciones y marcas de paso."),
            inventory(18, "Linterna cubierta", 1, 2.0, "Mochila", false, false, "Luz regulable para ruinas."),
            inventory(19, "Aceite", 4, 1.0, "Mochila", false, false, "Frascos para iluminación."),
            inventory(20, "Manta", 1, 3.0, "Mochila", false, false, "Protección para descanso."),
            inventory(21, "Cuerda de seda", 1, 5.0, "Mochila", false, false, "Cincuenta pies, compacta."),
            inventory(22, "Espejo de acero", 1, 0.5, "Bolsa", false, false, "Inspección de rincones y reflejos."),
            inventory(23, "Campanillas", 4, null, "Bolsa", false, false, "Avisos improvisados."),
            inventory(24, "Viales vacíos", 6, null, "Estuche", false, false, "Muestras y reactivos."),
            inventory(25, "Diadema del Archivo", 1, null, "Cabeza", true, true, "Marca ceremonial de acceso."),
            inventory(26, "Monóculo rúnico", 1, null, "Rostro", true, true, "Ayuda a inspeccionar glifos finos."),
            inventory(27, "Amuleto de Liria", 1, null, "Cuello", true, true, "Recuerdo de la Academia."),
            inventory(28, "Guante del escriba", 1, null, "Mano derecha", true, true, "Protege y estabiliza la mano."),
            inventory(29, "Brazal de cobre", 1, null, "Brazo izquierdo", true, true, "Conserva una carga menor."),
            inventory(30, "Brazal de plata", 1, null, "Brazo derecho", true, true, "Pareja del brazal de cobre."),
            inventory(31, "Chaleco de placas finas", 1, null, "Pecho", true, true, "Protección ligera bajo la ropa."),
            inventory(32, "Grebas del caminante", 1, null, "Piernas", true, true, "Refuerzo para largas marchas."),
            inventory(33, "Botas de senda", 1, null, "Pies", true, true, "Suela reforzada para terreno irregular."),
        )

        val currencies = listOf(
            CharacterCurrency("pt", "Piezas de platino", 4, 0, true),
            CharacterCurrency("po", "Piezas de oro", 137, 1, true),
            CharacterCurrency("pe", "Piezas de electrón", 2, 2, true),
            CharacterCurrency("pp", "Piezas de plata", 48, 3, true),
            CharacterCurrency("pc", "Piezas de cobre", 19, 4, true),
        )

        val spells = buildList {
            var index = 1
            fun addSpell(name: String, level: Int, prepared: Boolean) {
                add(spell(index++, name, level, spellSourceId, prepared))
            }
            addSpell("Luz", 0, true)
            addSpell("Mano de mago", 0, true)
            addSpell("Rayo de fuego", 0, true)
            addSpell("Prestidigitación", 0, false)

            addSpell("Escudo", 1, true)
            addSpell("Misil mágico", 1, true)
            addSpell("Detectar magia", 1, false)
            addSpell("Caída de pluma", 1, true)

            addSpell("Imagen múltiple", 2, true)
            addSpell("Paso brumoso", 2, true)
            addSpell("Invisibilidad", 2, false)
            addSpell("Levitar", 2, true)

            addSpell("Contrahechizo", 3, true)
            addSpell("Bola de fuego", 3, true)
            addSpell("Volar", 3, false)
            addSpell("Patrón hipnótico", 3, true)

            addSpell("Puerta dimensional", 4, true)
            addSpell("Invisibilidad superior", 4, false)
            addSpell("Ojo arcano", 4, true)
            addSpell("Polimorfar", 4, false)

            addSpell("Muro de fuerza", 5, true)
            addSpell("Telequinesis", 5, false)
            addSpell("Cono de frío", 5, true)

            addSpell("Desintegrar", 6, true)
            addSpell("Globo de invulnerabilidad", 6, false)
            addSpell("Visión verdadera", 6, true)

            addSpell("Teletransportar", 7, false)
            addSpell("Jaula de fuerza", 7, true)
            addSpell("Simulacro", 7, false)

            addSpell("Laberinto", 8, true)
            addSpell("Mente en blanco", 8, false)
            addSpell("Semiplano", 8, true)

            addSpell("Deseo", 9, true)
            addSpell("Detener el tiempo", 9, false)
            addSpell("Prisión", 9, true)
        }

        val sheet = CharacterSheet(
            id = uuid("00000000-0000-0000-0000-000000000001"),
            campaignId = uuid("00000000-0000-0000-0000-000000000002"),
            name = "Aster Vale",
            status = CharacterStatus.ACTIVE,
            updatedAtEpochSeconds = 1_700_000_000L,
            strength = 10,
            dexterity = 16,
            constitution = 14,
            intelligence = 18,
            wisdom = 12,
            charisma = 8,
            armorClass = 16,
            maxHp = 34,
            currentHp = 27,
            tempHp = 4,
            initiativeAdjustment = 1,
            speed = 30,
            proficiencyBonus = 3,
            savingThrows = saves,
            passivePerceptionAdjustment = 0,
            spellSaveDc = null,
            classes = listOf(
                CharacterClassLevel(wizardId, "Mago", 5, 6, 4, 0),
                CharacterClassLevel(rogueId, "Pícaro", 2, 8, 1, 1),
            ),
            skills = skills,
            proficiencyBonusAdjustment = 0,
            spellAttackModifier = null,
            spellSlots = listOf(
                CharacterSpellSlot(1, 4, 1),
                CharacterSpellSlot(2, 3, 2),
                CharacterSpellSlot(3, 3, 1),
                CharacterSpellSlot(4, 3, 2),
                CharacterSpellSlot(5, 2, 1),
                CharacterSpellSlot(6, 2, 2),
                CharacterSpellSlot(7, 1, 0),
                CharacterSpellSlot(8, 1, 1),
                CharacterSpellSlot(9, 1, 0),
            ),
            combatEntries = combat,
            inventoryItems = inventory,
            currencies = currencies,
            spellcasterEnabled = true,
            background = CharacterBackground(
                name = "Sabio de la Academia de Liria",
                summary = "Investigador de ruinas élficas y tradiciones arcanas antiguas.",
                race = "Elfo Alto",
                religionFaith = "Respeto académico por Corellon",
                personalityTraits = "Curiosidad académica: toma notas de todo fenómeno extraño y formula preguntas incluso en situaciones incómodas.",
                ideals = "Conocimiento. La información debe conservarse, contrastarse y compartirse con responsabilidad.",
                bonds = "Prometió devolver a la Academia un códice perdido y proteger a sus compañeros durante la búsqueda.",
                flaws = "Subestima los riesgos cuando aparece una oportunidad de estudiar magia desconocida.",
                story = "Aster abandonó temporalmente los archivos de Liria después de hallar referencias a una cámara sellada bajo el Valle del Viento. Viaja con un pequeño grupo de aventureros para reconstruir la ruta y comprobar si el códice asociado sobrevivió.",
            ),
            traits = traits,
            spellcastingSources = listOf(
                CharacterSpellcastingSource(
                    id = spellSourceId,
                    name = "Mago",
                    linkedClassId = wizardId,
                    sortOrder = 0,
                    originKind = CharacterSpellcastingOriginKind.CLASS,
                ),
            ),
            spells = spells,
            generalNotes = "Contactar a Maestra Elenya al regresar a Liria. No entregar el mapa original a terceros. Preparar tinta resistente al agua antes de entrar en las ruinas. Revisar el corredor norte antes de acampar. La puerta con sello azul responde al mismo patrón visto en la torre. Mantener una copia separada del alfabeto parcial. Registrar la posición de cada piedra marcada y comprobar si las distancias forman una secuencia. Comprar más aceite, cuerda fina y papel antes de la siguiente expedición. Recordar que el pasadizo oriental cambia de pendiente después de la tercera cámara.",
            noteCards = listOf(
                CharacterNote(uuid("60000000-0000-0000-0000-000000000001"), "Pista", "El sello azul aparece también en las monedas halladas en la torre.", 0),
                CharacterNote(uuid("60000000-0000-0000-0000-000000000002"), "Pendiente", "Comparar el alfabeto de la puerta norte con las notas del profesor Vael y marcar las coincidencias dudosas.", 1),
                CharacterNote(uuid("60000000-0000-0000-0000-000000000003"), "Ruta", "Entrada oeste, cámara de columnas, escalera rota, galería azul y archivo inferior. Evitar el corredor inundado.", 2),
                CharacterNote(uuid("60000000-0000-0000-0000-000000000004"), "Materiales", "Tinta, tiza, tres viales, espejo, cuerda, clavos y una linterna adicional.", 3),
            ),
            inspiration = true,
        )

        val closure = CharacterClosureState(
            progressMode = CharacterProgressMode.EXPERIENCE,
            experiencePoints = 23_000,
        )
        val successor = CharacterSuccessorState(
            spellcastingProfiles = listOf(
                CharacterSpellcastingProfile(
                    sourceId = spellSourceId,
                    ability = CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
                ),
            ),
            preferences = CharacterSuccessorPreferences(
                valuablesText = "Broche élfico antiguo (75 po); gema lunar tallada (120 po); tres láminas de plata grabadas (45 po); fragmento de mosaico con sello azul (25 po).",
            ),
        )
        return PcSheetExportAggregate(sheet = sheet, closure = closure, successor = successor)
    }

    private fun inventory(
        index: Int,
        name: String,
        quantity: Int,
        weight: Double?,
        location: String,
        special: Boolean,
        equipped: Boolean,
        description: String,
    ): CharacterInventoryItem = CharacterInventoryItem(
        id = uuid("50000000-0000-0000-0000-${index.toString().padStart(12, '0')}"),
        name = name,
        quantity = quantity,
        weightLb = weight,
        equipped = equipped,
        notes = null,
        sortOrder = index,
        special = special,
        description = description,
        location = location,
        attuned = special && equipped,
    )

    private fun spell(
        index: Int,
        name: String,
        level: Int,
        sourceId: Uuid,
        prepared: Boolean,
    ): CharacterSpell = CharacterSpell(
        id = uuid("70000000-0000-0000-0000-${index.toString().padStart(12, '0')}"),
        name = name,
        level = level,
        castingTime = "1 acción",
        rangeText = "60 ft",
        verbal = true,
        somatic = true,
        material = false,
        materialText = null,
        duration = "Instantáneo",
        concentration = false,
        ritual = false,
        description = "Descripción de prueba para el primer borrador de exportación PDF.",
        notes = null,
        sortOrder = index,
        sourceAssociations = listOf(CharacterSpellSourceAssociation(sourceId, prepared)),
    )

    private fun uuid(raw: String): Uuid = Uuid.parse(raw)
}
