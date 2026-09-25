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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterWeaponMastery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTemporaryEffect
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSense
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterMovementType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterMovement
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterForm
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefenseType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefense
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCondition
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConcentration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCompanion
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomAttribute
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkillAbilityConfiguration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomMarker
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceRecovery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceSuccessorConfiguration
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
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBasePageRole
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomStatisticsPresentation
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBaseLayoutMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportPlanner
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPortraitFitMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition

class DesktopPcSheetWholeDraftRendererTest {
    @Test
    fun rendersOwnerApprovedClassicBaseFromRealPlanData() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate()
        val shortTraits = base.sheet.traits.take(8).mapIndexed { index, trait ->
            val (name, description, type) = when (index) {
                0 -> Triple("Paso seguro", "Equilibrio seguro.", CharacterTraitType.CLASS)
                1 -> Triple("Lectura táctica", "Detecta cobertura.", CharacterTraitType.CLASS)
                2 -> Triple("Estudio rápido", "Resume pistas.", CharacterTraitType.CLASS)
                3 -> Triple("Maniobra cauta", "Cruce cauteloso.", CharacterTraitType.CLASS)
                4 -> Triple("Oído atento", "", CharacterTraitType.SPECIES_RACE)
                5 -> Triple("Paso firme", "", CharacterTraitType.SPECIES_RACE)
                6 -> Triple("Memoria local", "", CharacterTraitType.SPECIES_RACE)
                else -> Triple("Observadora", "Presta especial atención a detalles.", CharacterTraitType.FEAT)
            }
            trait.copy(
                name = name,
                source = "",
                type = type,
                description = description,
                notes = null,
                maxUses = null,
                spentUses = 0,
                recovery = null,
                activation = null,
                sortOrder = index,
            )
        }
        val inventory = base.sheet.inventoryItems.take(7).mapIndexed { index, item ->
            item.copy(
                name = if (index == 0) "Equipo de campaña" else item.name,
                weightLb = null,
                equipped = index == 0,
                notes = null,
                special = false,
                description = null,
                location = if (index == 0) "Mochila" else "Equipo",
                attuned = false,
                sortOrder = index,
            )
        }
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                name = "Iria Noctis",
                status = CharacterStatus.ACTIVE,
                tempHp = 0,
                deathSaveSuccesses = 1,
                deathSaveFailures = 0,
                passivePerceptionAdjustment = 0,
                classes = listOf(
                    base.sheet.classes.first().copy(
                        name = "Maga cartógrafa",
                        level = 5,
                        hitDiceRemaining = 3,
                        sortOrder = 0,
                        source = null,
                        subclassName = "Guardiana de umbrales",
                        subclassSource = null,
                    ),
                ),
                combatEntries = base.sheet.combatEntries.take(4).mapIndexed { index, entry ->
                    if (index == 0) entry.copy(name = "Lanza de cobre", sortOrder = index)
                    else entry.copy(sortOrder = index)
                },
                inventoryItems = inventory,
                background = CharacterBackground(
                    name = "Cartógrafa de frontera",
                    summary = "Explora pasos olvidados y registra rutas seguras.",
                    race = "Humana",
                    religionFaith = "",
                    personalityTraits = "Anota cada desvío importante.",
                    ideals = "Precisión y prudencia.",
                    bonds = "Protege a su expedición.",
                    flaws = "Revisa los mapas demasiadas veces.",
                    story = "Busca un antiguo paso entre montañas.",
                ),
                traits = shortTraits,
                spells = base.sheet.spells.filter { it.level <= 5 },
                generalNotes = "",
                noteCards = emptyList(),
                proficiencies = listOf(
                    CharacterProficiency(
                        id = uuid("94000000-0000-0000-0000-000000000001"),
                        type = CharacterProficiencyType.LANGUAGE,
                        name = "Enano",
                        source = null,
                        notes = null,
                        sortOrder = 0,
                    ),
                ),
                weaponMasteries = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                forms = emptyList(),
                companions = emptyList(),
                inspiration = true,
            ),
            closure = base.closure.copy(
                customSkills = emptyList(),
                exhaustionLevel = 0,
                concentration = null,
                conditions = emptyList(),
                defenses = emptyList(),
                movements = emptyList(),
                senses = emptyList(),
                inventoryUsage = emptyList(),
                temporaryEffects = emptyList(),
            ),
            successor = base.successor.copy(
                customAttributes = emptyList(),
                customSkillAbilities = emptyList(),
                combatDamage = emptyList(),
                customMarkers = emptyList(),
                resourceConfigurations = emptyList(),
                speciesIdentity = null,
                subraceIdentity = null,
                backgroundIdentity = null,
                traitProvenance = emptyList(),
                preferences = base.successor.preferences.copy(
                    valuablesText = "Mapa sellado (30 po)",
                ),
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        assertEquals(
            listOf(
                PcSheetBasePageRole.MAIN,
                PcSheetBasePageRole.EQUIPMENT_AND_NARRATIVE,
                PcSheetBasePageRole.SPELL_LIST,
            ),
            plan.basePages.map { it.role },
        )

        val pdf = File(proofDir, "fantasy-production-base-pass1.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages >= plan.basePages.size)
            repeat(document.numberOfPages) { index ->
                assertEquals(612f, document.getPage(index).mediaBox.width, 0.01f)
                assertEquals(792f, document.getPage(index).mediaBox.height, 0.01f)
            }
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Iria Noctis"))
            assertTrue(extracted.contains("Cartógrafa de frontera"))
            assertTrue(extracted.contains("Guardiana de umbrales"))
            assertTrue(extracted.contains("Lanza de cobre"))
            assertTrue(extracted.contains("Equipo de campaña"))
            assertTrue(extracted.contains("Enano"))
            assertFalse(extracted.contains("Aster Vale"))
            assertFalse(extracted.contains("Sabio de Liria"))
            assertFalse(extracted.contains("Tradición de Adivinación"))
            assertFalse(extracted.contains("Cabello negro"))
            assertFalse(extracted.contains("CONTINÚA EN EXTENSIÓN"))

            val pdfRenderer = PDFRenderer(document)
            repeat(document.numberOfPages) { index ->
                val image = pdfRenderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "fantasy-production-base-pass1-page-${index + 1}.png"),
                    ),
                )
            }
        }
        assertTrue(pdf.length() > 20_000L)

        val customAttributes = (1..4).map { index ->
            CharacterCustomAttribute(
                id = uuid("95000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                name = "Atributo $index",
                abbreviation = "A$index",
                score = 10 + index * 2,
                savingThrowEnabled = true,
                savingThrowProficient = index % 2 == 0,
                savingThrowAdjustment = index,
                notes = "Definición canónica del atributo personalizado $index.",
                sortOrder = index,
            )
        }
        val customLinkedSkills = customAttributes.flatMapIndexed { attributeIndex, attribute ->
            (1..5).map { skillIndex ->
                val serial = attributeIndex * 10 + skillIndex
                CharacterCustomSkill(
                    id = uuid("96000000-0000-0000-0000-" + serial.toString().padStart(12, '0')),
                    name = "Técnica ${attributeIndex + 1}-$skillIndex",
                    ability = CharacterAbility.INTELLIGENCE,
                    training = if (skillIndex == 5) SkillTraining.EXPERTISE else SkillTraining.PROFICIENT,
                    adjustment = skillIndex,
                    source = "Fuente ${attributeIndex + 1}",
                    notes = "Nota $skillIndex",
                    sortOrder = serial,
                )
            }
        }
        val standardLinkedSkills = (1..5).map { index ->
            CharacterCustomSkill(
                id = uuid("97000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                name = "Rastreo especial $index",
                ability = CharacterAbility.WISDOM,
                training = SkillTraining.PROFICIENT,
                adjustment = index,
                source = "Exploración",
                notes = null,
                sortOrder = 100 + index,
            )
        }
        val customSkills = customLinkedSkills + standardLinkedSkills
        val customMappings = buildList {
            customAttributes.forEachIndexed { attributeIndex, attribute ->
                customLinkedSkills
                    .filter { it.sortOrder / 10 == attributeIndex }
                    .forEach { skill ->
                        add(
                            CharacterCustomSkillAbilityConfiguration(
                                customSkillId = skill.id,
                                ability = CharacterAbilityReference.custom(attribute.id),
                            ),
                        )
                    }
            }
            standardLinkedSkills.forEach { skill ->
                add(
                    CharacterCustomSkillAbilityConfiguration(
                        customSkillId = skill.id,
                        ability = CharacterAbilityReference.builtIn(CharacterAbility.WISDOM),
                    ),
                )
            }
        }
        val extendedAggregate = aggregate.copy(
            closure = aggregate.closure.copy(customSkills = customSkills),
            successor = aggregate.successor.copy(
                customAttributes = customAttributes,
                customSkillAbilities = customMappings,
            ),
        )
        val extendedPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = extendedAggregate),
        )
        assertEquals(
            listOf(PcSheetExtendedPageKind.CUSTOM_STATISTICS),
            extendedPlan.mandatoryExtendedPages,
        )

        val extendedPdf = File(proofDir, "fantasy-production-custom-stats-pass2.pdf")
        extendedPdf.outputStream().use { renderer.renderDraft(extendedPlan, it) }

        Loader.loadPDF(extendedPdf).use { document ->
            assertTrue(document.numberOfPages >= extendedPlan.basePages.size)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("ESTADÍSTICAS PERSONALIZADAS"))
            assertTrue(Regex("Atributo\\s+4").containsMatchIn(extracted))
            assertTrue(Regex("Técnica\\s+4-5").containsMatchIn(extracted))
            assertTrue(Regex("Rastreo\\s+especial\\s+5").containsMatchIn(extracted))
            assertFalse(extracted.contains("HONOR"))
            assertFalse(extracted.contains("RESOLUCIÓN"))
            assertFalse(extracted.contains("SUERTE"))

            val pdfRenderer = PDFRenderer(document)
            (3 until document.numberOfPages).forEach { index ->
                val image = pdfRenderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(
                            proofDir,
                            "fantasy-production-custom-stats-pass2-page-${index + 1}.png",
                        ),
                    ),
                )
            }
        }
        assertTrue(extendedPdf.length() > pdf.length())

        val extraTraits = listOf(
            shortTraits[0].copy(
                id = uuid("98000000-0000-0000-0000-000000000001"),
                name = "Disciplina nocturna",
                source = "Clase de frontera",
                type = CharacterTraitType.CLASS,
                description = "Mantiene la vigilancia durante una marcha prolongada.",
                notes = "Solo cuando la expedición permanece unida.",
                maxUses = 2,
                spentUses = 1,
                recovery = "Descanso corto",
                activation = CharacterActivationType.BONUS_ACTION,
                sortOrder = 20,
            ),
            shortTraits[0].copy(
                id = uuid("98000000-0000-0000-0000-000000000002"),
                name = "Contactos de frontera",
                source = "Trasfondo",
                type = CharacterTraitType.BACKGROUND,
                description = "Conoce guías y archivistas en varios puestos remotos.",
                notes = null,
                maxUses = null,
                spentUses = 0,
                recovery = null,
                activation = null,
                sortOrder = 21,
            ),
            shortTraits[0].copy(
                id = uuid("98000000-0000-0000-0000-000000000003"),
                name = "Paso montés",
                source = "Humana",
                type = CharacterTraitType.SPECIES_RACE,
                description = "Se desplaza con confianza por senderos estrechos.",
                notes = null,
                maxUses = null,
                spentUses = 0,
                recovery = null,
                activation = null,
                sortOrder = 22,
            ),
            shortTraits[0].copy(
                id = uuid("98000000-0000-0000-0000-000000000004"),
                name = "Cartografía experta",
                source = "Dote",
                type = CharacterTraitType.FEAT,
                description = "Interpreta mapas incompletos y referencias parciales.",
                notes = null,
                maxUses = null,
                spentUses = 0,
                recovery = null,
                activation = null,
                sortOrder = 23,
            ),
            shortTraits[0].copy(
                id = uuid("98000000-0000-0000-0000-000000000005"),
                name = "Juramento del mapa",
                source = "Campaña",
                type = CharacterTraitType.OTHER,
                description = "Conserva el mapa original y registra cada corrección.",
                notes = "No entregar el original.",
                maxUses = null,
                spentUses = 0,
                recovery = null,
                activation = null,
                sortOrder = 24,
            ),
        )
        val traitProficiencies = buildList {
            (1..6).forEach { index ->
                add(
                    CharacterProficiency(
                        id = uuid("99000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                        type = CharacterProficiencyType.LANGUAGE,
                        name = "Lengua $index",
                        source = "Viajes",
                        notes = null,
                        sortOrder = index,
                    ),
                )
            }
            add(
                CharacterProficiency(
                    id = uuid("99000000-0000-0000-0000-000000000020"),
                    type = CharacterProficiencyType.TOOL,
                    name = "Herramientas de navegante",
                    source = "Formación",
                    notes = "Uso habitual en expediciones.",
                    sortOrder = 20,
                ),
            )
        }
        val traitsAggregate = aggregate.copy(
            sheet = aggregate.sheet.copy(
                traits = shortTraits + extraTraits,
                proficiencies = traitProficiencies,
            ),
        )
        val traitsPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = traitsAggregate),
        )
        val traitsPdf = File(proofDir, "fantasy-production-traits-pass3.pdf")
        traitsPdf.outputStream().use { renderer.renderDraft(traitsPlan, it) }

        Loader.loadPDF(traitsPdf).use { document ->
            assertTrue(document.numberOfPages >= traitsPlan.basePages.size)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("RASGOS Y CARACTERÍSTICAS"))
            assertTrue(Regex("Juramento\\s+del\\s+mapa").containsMatchIn(extracted))
            assertTrue(Regex("Lengua\\s+6").containsMatchIn(extracted))
            assertTrue(extracted.contains("Herramientas de navegante"))
            assertTrue(extracted.contains("Descanso corto"))

            val pdfRenderer = PDFRenderer(document)
            (3 until document.numberOfPages).forEach { index ->
                val image = pdfRenderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "fantasy-production-traits-pass3-page-${index + 1}.png"),
                    ),
                )
            }
        }
        assertTrue(traitsPdf.length() > pdf.length())

        val resources = (1..9).map { index ->
            CharacterResource(
                id = uuid("9a000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                name = "Recurso $index",
                currentValue = index,
                maxValue = 12,
                recovery = if (index % 2 == 0) "Amanecer" else null,
                source = "Fuente $index",
                notes = "Nota operacional $index",
                pinned = true,
                sortOrder = index,
            )
        }
        val resourceRecovery = listOf(
            CharacterResourceRecovery(
                resourceId = resources.first().id,
                cadence = CharacterRecoveryCadence.SHORT_REST,
                amountMode = CharacterRecoveryAmountMode.FIXED,
                fixedAmount = 1,
                notes = "Recupera una unidad.",
            ),
            CharacterResourceRecovery(
                resourceId = resources.last().id,
                cadence = CharacterRecoveryCadence.LONG_REST,
                amountMode = CharacterRecoveryAmountMode.TO_MAX,
                fixedAmount = null,
                notes = "Restablecimiento completo.",
            ),
        )
        val resourceConfigurations = listOf(
            CharacterResourceSuccessorConfiguration(
                resourceId = resources[0].id,
                valueKind = CharacterTrackableValueKind.BINARY,
            ),
            CharacterResourceSuccessorConfiguration(
                resourceId = resources[1].id,
                valueKind = CharacterTrackableValueKind.COUNTER,
            ),
        )
        val customMarkers = (1..2).map { index ->
            CharacterCustomMarker(
                id = uuid("9b000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                name = "Marcador $index",
                valueKind = CharacterTrackableValueKind.CURRENT_MAX,
                currentValue = index,
                maxValue = 3,
                recovery = CharacterTrackableRecovery(
                    cadence = CharacterRecoveryCadence.LONG_REST,
                    amountMode = CharacterRecoveryAmountMode.TO_MAX,
                ),
                notes = "Marcador persistente $index",
                sortOrder = 20 + index,
            )
        }
        val classOptions = (1..7).map { index ->
            CharacterClassOption(
                id = uuid("9c000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                linkedClassId = null,
                kind = if (index % 2 == 0) {
                    CharacterClassOptionKind.TECHNIQUE
                } else {
                    CharacterClassOptionKind.OTHER
                },
                name = "Opción $index",
                source = "Clase $index",
                costText = if (index == 7) "2 cargas" else null,
                effectSummary = "Efecto canónico de la opción $index.",
                notes = if (index == 7) "Opción terminal de auditoría." else null,
                active = index != 6,
                pinned = false,
                sortOrder = index,
            )
        }
        val resourcesAggregate = aggregate.copy(
            sheet = aggregate.sheet.copy(
                resources = resources,
                classOptions = classOptions,
            ),
            closure = aggregate.closure.copy(resourceRecovery = resourceRecovery),
            successor = aggregate.successor.copy(
                customMarkers = customMarkers,
                resourceConfigurations = resourceConfigurations,
            ),
        )
        val resourcesPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = resourcesAggregate),
        )
        val resourcesPdf = File(proofDir, "fantasy-production-resources-pass4.pdf")
        resourcesPdf.outputStream().use { renderer.renderDraft(resourcesPlan, it) }

        Loader.loadPDF(resourcesPdf).use { document ->
            assertEquals(6, document.numberOfPages)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("RECURSOS Y OPCIONES"))
            assertTrue(Regex("Recurso\\s+9").containsMatchIn(extracted))
            assertTrue(Regex("Marcador\\s+2").containsMatchIn(extracted))
            assertTrue(Regex("Opción\\s+7").containsMatchIn(extracted))
            assertTrue(extracted.contains("Descanso largo"))
            assertTrue(extracted.contains("Opción terminal de auditoría"))

            val pdfRenderer = PDFRenderer(document)
            (3 until document.numberOfPages).forEach { index ->
                val image = pdfRenderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "fantasy-production-resources-pass4-page-${index + 1}.png"),
                    ),
                )
            }
        }
        assertTrue(resourcesPdf.length() > pdf.length())

        val inventoryItems = (1..19).map { index ->
            CharacterInventoryItem(
                id = uuid("9d000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                name = "Objeto de campaña $index",
                quantity = if (index % 3 == 0) 2 else 1,
                weightLb = if (index <= 7) null else index / 10.0,
                equipped = false,
                notes = if (index == 16) "Terminal de inventario." else null,
                sortOrder = index,
                special = index >= 17,
                description = if (index == 15) "Descripción canónica quince." else null,
                location = if (index <= 7) "Equipo" else "Mochila $index",
                attuned = index == 18,
            )
        }
        val inventoryUsage = listOf(
            CharacterInventoryUsage(
                itemId = inventoryItems[7].id,
                kind = CharacterConsumableKind.NONE,
                quickUseAmount = 1,
                carryState = CharacterInventoryCarryState.STORED,
            ),
            CharacterInventoryUsage(
                itemId = inventoryItems[8].id,
                kind = CharacterConsumableKind.AMMUNITION,
                quickUseAmount = 2,
                carryState = CharacterInventoryCarryState.CARRIED,
            ),
        )
        val inventoryCurrencies = aggregate.sheet.currencies + CharacterCurrency(
            key = "obs",
            name = "Piezas de obsidiana",
            amount = 17,
            sortOrder = 90,
            isDefault = false,
        )
        val inventoryAggregate = aggregate.copy(
            sheet = aggregate.sheet.copy(
                inventoryItems = inventoryItems,
                currencies = inventoryCurrencies,
            ),
            closure = aggregate.closure.copy(inventoryUsage = inventoryUsage),
            successor = aggregate.successor.copy(
                preferences = aggregate.successor.preferences.copy(
                    valuablesText = "Valor base;Gema test 2;Reliquia terminal",
                ),
            ),
        )
        val inventoryPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = inventoryAggregate),
        )
        val inventoryPdf = File(proofDir, "fantasy-production-inventory-pass5.pdf")
        inventoryPdf.outputStream().use { renderer.renderDraft(inventoryPlan, it) }

        Loader.loadPDF(inventoryPdf).use { document ->
            assertTrue(
                document.numberOfPages >= 4,
                "Fantasy Sheet inventory overflow must append at least one continuation page.",
            )
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("INVENTARIO / EQUIPO"))
            assertTrue(Regex("Objeto\\s+de\\s+campaña\\s+8").containsMatchIn(extracted))
            assertTrue(Regex("Objeto\\s+de\\s+campaña\\s+16").containsMatchIn(extracted))
            assertTrue(Regex("Objeto\\s+de\\s+campaña\\s+18").containsMatchIn(extracted))
            assertTrue(extracted.contains("Almacenado"))
            assertTrue(extracted.contains("Munición"))
            assertTrue(Regex("Piezas\\s+de\\s+obsidiana:\\s+17").containsMatchIn(extracted))
            assertTrue(extracted.contains("Gema test 2"))
            assertTrue(extracted.contains("Reliquia terminal"))

            val continuationText = (4..document.numberOfPages).joinToString("\n") { pageNumber ->
                PDFTextStripper().apply {
                    startPage = pageNumber
                    endPage = pageNumber
                }.getText(document)
            }
            assertTrue(Regex("Objeto\\s+de\\s+campaña\\s+8").containsMatchIn(continuationText))
            assertTrue(Regex("Objeto\\s+de\\s+campaña\\s+16").containsMatchIn(continuationText))
            assertTrue(Regex("Objeto\\s+de\\s+campaña\\s+18").containsMatchIn(continuationText))
            assertFalse(continuationText.contains("(cont.)"))

            val pdfRenderer = PDFRenderer(document)
            (3 until document.numberOfPages).forEach { index ->
                val image = pdfRenderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "fantasy-production-inventory-pass5-page-${index + 1}.png"),
                    ),
                )
            }
        }
        assertTrue(inventoryPdf.length() > pdf.length())

        val sourceId = aggregate.sheet.spellcastingSources.first().id
        val spellSeed = aggregate.sheet.spells.first()
        val spellOverflow = (1..31).map { index ->
            spellSeed.copy(
                id = uuid("9e000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                name = "Conjuro canónico $index",
                level = 1,
                sortOrder = index,
                sourceAssociations = listOf(
                    CharacterSpellSourceAssociation(
                        sourceId = sourceId,
                        prepared = index == 14 || index == 31,
                    ),
                ),
            )
        }
        val spellAggregate = aggregate.copy(
            sheet = aggregate.sheet.copy(
                spells = spellOverflow,
                spellcasterEnabled = true,
            ),
        )
        val spellPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = spellAggregate),
        )
        val spellPdf = File(proofDir, "fantasy-production-spells-pass6.pdf")
        spellPdf.outputStream().use { renderer.renderDraft(spellPlan, it) }

        Loader.loadPDF(spellPdf).use { document ->
            assertEquals(5, document.numberOfPages)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("CONJUROS"))
            assertTrue(Regex("Conjuro\\s+canónico\\s+14").containsMatchIn(extracted))
            assertTrue(Regex("Conjuro\\s+canónico\\s+22").containsMatchIn(extracted))
            assertTrue(Regex("Conjuro\\s+canónico\\s+23").containsMatchIn(extracted))
            assertTrue(Regex("Conjuro\\s+canónico\\s+31").containsMatchIn(extracted))

            val page4 = PDFTextStripper().apply {
                startPage = 4
                endPage = 4
            }.getText(document)
            val page5 = PDFTextStripper().apply {
                startPage = 5
                endPage = 5
            }.getText(document)
            assertTrue(Regex("Conjuro\\s+canónico\\s+14").containsMatchIn(page4))
            assertTrue(Regex("Conjuro\\s+canónico\\s+22").containsMatchIn(page4))
            assertTrue(Regex("Conjuro\\s+canónico\\s+23").containsMatchIn(page5))
            assertTrue(Regex("Conjuro\\s+canónico\\s+31").containsMatchIn(page5))

            val pdfRenderer = PDFRenderer(document)
            (3 until document.numberOfPages).forEach { index ->
                val image = pdfRenderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "fantasy-production-spells-pass6-page-${index + 1}.png"),
                    ),
                )
            }
        }
        assertTrue(spellPdf.length() > pdf.length())

        val notes = (1..30).map { index ->
            CharacterNote(
                id = uuid("9f000000-0000-0000-0000-" + index.toString().padStart(12, '0')),
                title = "Nota canónica $index",
                content = "Contenido persistente de campaña $index.",
                sortOrder = index,
            )
        }
        val notesAggregate = aggregate.copy(
            sheet = aggregate.sheet.copy(
                generalNotes = "Recordatorio general canónico.",
                noteCards = notes,
            ),
        )
        val notesPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = notesAggregate),
        )
        val notesPdf = File(proofDir, "fantasy-production-notes-pass7.pdf")
        notesPdf.outputStream().use { renderer.renderDraft(notesPlan, it) }

        Loader.loadPDF(notesPdf).use { document ->
            assertEquals(6, document.numberOfPages)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("NOTAS DE CAMPAÑA"))
            assertTrue(Regex("Nota\\s+canónica\\s+1").containsMatchIn(extracted))
            assertTrue(Regex("Nota\\s+canónica\\s+30").containsMatchIn(extracted))
            assertTrue(extracted.contains("Recordatorio general canónico"))
            assertTrue(extracted.contains("CROQUIS / MAPA"))
            assertTrue(extracted.contains("REFERENCIAS Y RECORDATORIOS"))

            val page4 = PDFTextStripper().apply {
                startPage = 4
                endPage = 4
            }.getText(document)
            val page6 = PDFTextStripper().apply {
                startPage = 6
                endPage = 6
            }.getText(document)
            assertTrue(Regex("Nota\\s+canónica\\s+1").containsMatchIn(page4))
            assertTrue(Regex("Nota\\s+canónica\\s+30").containsMatchIn(page6))

            val pdfRenderer = PDFRenderer(document)
            (3 until document.numberOfPages).forEach { index ->
                val image = pdfRenderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "fantasy-production-notes-pass7-page-${index + 1}.png"),
                    ),
                )
            }
        }
        assertTrue(notesPdf.length() > pdf.length())

    }

    @Test
    fun appendsApplicationOwnedSpellbookWithIndexSourcesAndCompleteLongDescription() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate()
        val primarySource = base.sheet.spellcastingSources.first()
        val primaryProfile = base.successor.spellcastingProfiles.first()
        val secondarySource = CharacterSpellcastingSource(
            id = uuid("aa000000-0000-0000-0000-000000000001"),
            name = "Dote del umbral",
            linkedClassId = null,
            sortOrder = 50,
            originKind = CharacterSpellcastingOriginKind.FEAT,
        )
        val secondaryProfile = CharacterSpellcastingProfile(
            sourceId = secondarySource.id,
            ability = primaryProfile.ability,
            saveDcAdjustment = 1,
            spellAttackAdjustment = 2,
        )
        val seed = base.sheet.spells.first()
        val cantrip = seed.copy(
            id = uuid("aa000000-0000-0000-0000-000000000010"),
            name = "Llama menor",
            level = 0,
            castingTime = "1 acción",
            rangeText = "60 ft",
            verbal = true,
            somatic = true,
            material = false,
            materialText = null,
            duration = "Instantáneo",
            concentration = false,
            ritual = false,
            description = "Una llama breve ilumina el objetivo sin perder la descripción registrada.",
            notes = "Referencia de truco.",
            sortOrder = 30,
            sourceAssociations = listOf(
                CharacterSpellSourceAssociation(primarySource.id, prepared = true),
            ),
        )
        val absorb = seed.copy(
            id = uuid("aa000000-0000-0000-0000-000000000011"),
            name = "Absorber energía",
            level = 1,
            castingTime = "1 reacción",
            rangeText = "Personal",
            verbal = false,
            somatic = true,
            material = true,
            materialText = "un fragmento de cobre",
            duration = "1 ronda",
            concentration = false,
            ritual = false,
            description = "Conserva la descripción completa del conjuro asociado al personaje.",
            notes = null,
            sortOrder = 20,
            sourceAssociations = listOf(
                CharacterSpellSourceAssociation(primarySource.id, prepared = true),
                CharacterSpellSourceAssociation(secondarySource.id, prepared = false),
            ),
        )
        val longDescription = (1..90).joinToString(" ") { index ->
            "Detalle arcano $index mantiene la referencia completa durante la prueba de paginación."
        } + " MARCADOR TERMINAL DEL SPELLBOOK"
        val longSpell = seed.copy(
            id = uuid("aa000000-0000-0000-0000-000000000012"),
            name = "Zancada interminable",
            level = 1,
            castingTime = "10 minutos",
            rangeText = "Toque",
            verbal = true,
            somatic = true,
            material = true,
            materialText = "una cinta de plata grabada",
            duration = "Concentración, hasta 1 hora",
            concentration = true,
            ritual = true,
            description = longDescription,
            notes = "Conservar también esta nota final de autoría.",
            sortOrder = 10,
            sourceAssociations = listOf(
                CharacterSpellSourceAssociation(secondarySource.id, prepared = true),
            ),
        )
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                spellcastingSources = listOf(primarySource, secondarySource),
                spells = listOf(longSpell, absorb, cantrip),
            ),
            successor = base.successor.copy(
                spellcastingProfiles = listOf(primaryProfile, secondaryProfile),
            ),
        )

        fun plan(includeSpellbook: Boolean) = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = includeSpellbook,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val baselinePdf = File(proofDir, "spellbook-append-baseline.pdf")
        baselinePdf.outputStream().use { renderer.renderDraft(plan(false), it) }
        val spellbookPdf = File(proofDir, "spellbook-application-owned-proof.pdf")
        spellbookPdf.outputStream().use { renderer.renderDraft(plan(true), it) }

        Loader.loadPDF(baselinePdf).use { baselineDocument ->
            Loader.loadPDF(spellbookPdf).use { document ->
                val baselinePages = baselineDocument.numberOfPages
                assertTrue(document.numberOfPages >= baselinePages + 3)

                val indexPage = baselinePages + 1
                val indexText = PDFTextStripper().apply {
                    startPage = indexPage
                    endPage = indexPage
                }.getText(document)
                assertTrue(indexText.contains("ÍNDICE DE CONJUROS"))
                assertTrue(indexText.indexOf("Llama menor") < indexText.indexOf("Absorber energía"))
                assertTrue(indexText.indexOf("Absorber energía") < indexText.indexOf("Zancada interminable"))

                val extracted = PDFTextStripper().getText(document)
                assertTrue(extracted.contains("LIBRO DE CONJUROS"))
                assertTrue(extracted.contains("Dote del umbral"))
                assertTrue(extracted.contains("Preparado"))
                assertTrue(extracted.contains("No preparado"))
                assertTrue(extracted.contains("Aptitud INT"))
                assertTrue(extracted.contains("Componentes: S, M (un fragmento de cobre)"))
                assertTrue(extracted.contains("Concentración"))
                assertTrue(extracted.contains("Ritual"))
                assertTrue(extracted.contains("Zancada interminable (continuación)"))
                assertTrue(extracted.contains("MARCADOR TERMINAL DEL SPELLBOOK"))
                assertTrue(extracted.contains("Conservar también esta nota final de autoría."))

                fun firstSpellPage(name: String): Int =
                    ((indexPage + 1)..document.numberOfPages).first { pageNumber ->
                        PDFTextStripper().apply {
                            startPage = pageNumber
                            endPage = pageNumber
                        }.getText(document).contains(name)
                    }

                val cantripPage = firstSpellPage("Llama menor")
                val absorbPage = firstSpellPage("Absorber energía")
                val longSpellPage = firstSpellPage("Zancada interminable")
                assertTrue(indexText.contains(cantripPage.toString()))
                assertTrue(indexText.contains(absorbPage.toString()))
                assertTrue(indexText.contains(longSpellPage.toString()))

                val baselineRenderer = PDFRenderer(baselineDocument)
                val appendedRenderer = PDFRenderer(document)
                listOf(0, baselinePages - 1).distinct().forEach { pageIndex ->
                    val baselineImage = baselineRenderer.renderImageWithDPI(pageIndex, 72f, ImageType.RGB)
                    val appendedImage = appendedRenderer.renderImageWithDPI(pageIndex, 72f, ImageType.RGB)
                    assertEquals(baselineImage.width, appendedImage.width)
                    assertEquals(baselineImage.height, appendedImage.height)
                    val baselinePixels = baselineImage.getRGB(
                        0, 0, baselineImage.width, baselineImage.height,
                        null, 0, baselineImage.width,
                    )
                    val appendedPixels = appendedImage.getRGB(
                        0, 0, appendedImage.width, appendedImage.height,
                        null, 0, appendedImage.width,
                    )
                    assertTrue(
                        baselinePixels.contentEquals(appendedPixels),
                        "Spellbook append must not alter already-rendered sheet pages.",
                    )
                }
            }
        }
    }

    @Test
    fun overlaysLocallyResolvedPortraitBytesAcrossFamiliesWithCropAndFit() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val portraitRef = "portrait://local-pdf-proof"
        val portraitBytes = ByteArrayOutputStream().use { buffer ->
            val image = BufferedImage(720, 180, BufferedImage.TYPE_INT_RGB)
            val graphics = image.createGraphics()
            graphics.color = Color(220, 55, 55)
            graphics.fillRect(0, 0, 240, 180)
            graphics.color = Color(55, 150, 75)
            graphics.fillRect(240, 0, 240, 180)
            graphics.color = Color(45, 80, 205)
            graphics.fillRect(480, 0, 240, 180)
            graphics.dispose()
            assertTrue(ImageIO.write(image, "png", buffer))
            buffer.toByteArray()
        }
        val renderer = DesktopPcSheetWholeDraftRenderer(
            portraitBytesLoader = { ref -> portraitBytes.takeIf { ref == portraitRef } },
        )
        val dense = denseDraftAggregate()
        val aggregate = dense.copy(
            sheet = dense.sheet.copy(
                traits = dense.sheet.traits.take(4),
                combatEntries = dense.sheet.combatEntries.take(4),
                inventoryItems = dense.sheet.inventoryItems.take(4),
                proficiencies = dense.sheet.proficiencies.take(3),
                weaponMasteries = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                forms = emptyList(),
                companions = emptyList(),
                generalNotes = "",
                noteCards = emptyList(),
                spells = dense.sheet.spells.take(4),
            ),
            closure = dense.closure.copy(
                portraitRef = portraitRef,
                exhaustionLevel = 0,
                concentration = null,
                conditions = emptyList(),
                defenses = emptyList(),
                movements = emptyList(),
                senses = emptyList(),
                resourceRecovery = emptyList(),
                inventoryUsage = emptyList(),
                customSkills = emptyList(),
                temporaryEffects = emptyList(),
            ),
            successor = dense.successor.copy(
                customAttributes = emptyList(),
                customSkillAbilities = emptyList(),
                combatDamage = emptyList(),
                customMarkers = emptyList(),
                resourceConfigurations = emptyList(),
            ),
        )

        fun plan(
            family: PcSheetVisualFamily,
            mode: PcSheetPortraitFitMode,
            available: Boolean,
        ) = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = family,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
                portraitFitMode = mode,
            ),
            sources = PcSheetExportSources(
                permanent = aggregate,
                locallyAvailablePortraitRefs = if (available) setOf(portraitRef) else emptySet(),
            ),
        )

        val families = listOf(
            PcSheetVisualFamily.CLASSIC_DND_STYLE,
            PcSheetVisualFamily.CUSTOM_V1,
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        )
        families.forEach { family ->
            val slug = family.name.lowercase()
            val baselinePdf = File(proofDir, "portrait-$slug-baseline.pdf")
            val cropPdf = File(proofDir, "portrait-$slug-crop.pdf")
            val fitPdf = File(proofDir, "portrait-$slug-fit.pdf")

            baselinePdf.outputStream().use {
                renderer.renderDraft(
                    plan(family, PcSheetPortraitFitMode.CROP_TO_FILL, available = false),
                    it,
                )
            }
            cropPdf.outputStream().use {
                renderer.renderDraft(
                    plan(family, PcSheetPortraitFitMode.CROP_TO_FILL, available = true),
                    it,
                )
            }
            fitPdf.outputStream().use {
                renderer.renderDraft(
                    plan(family, PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE, available = true),
                    it,
                )
            }

            Loader.loadPDF(baselinePdf).use { baselineDocument ->
                Loader.loadPDF(cropPdf).use { cropDocument ->
                    Loader.loadPDF(fitPdf).use { fitDocument ->
                        assertEquals(baselineDocument.numberOfPages, cropDocument.numberOfPages)
                        assertEquals(baselineDocument.numberOfPages, fitDocument.numberOfPages)

                        val targetIndex = if (family == PcSheetVisualFamily.CLASSIC_DND_STYLE) 1 else 0
                        val stableIndex = if (targetIndex == 0) 1 else 0
                        val baselineRenderer = PDFRenderer(baselineDocument)
                        val cropRenderer = PDFRenderer(cropDocument)
                        val fitRenderer = PDFRenderer(fitDocument)

                        fun pixels(pdfRenderer: PDFRenderer, pageIndex: Int): IntArray {
                            val image = pdfRenderer.renderImageWithDPI(pageIndex, 72f, ImageType.RGB)
                            return image.getRGB(0, 0, image.width, image.height, null, 0, image.width)
                        }

                        val baselineTarget = pixels(baselineRenderer, targetIndex)
                        val cropTarget = pixels(cropRenderer, targetIndex)
                        val fitTarget = pixels(fitRenderer, targetIndex)
                        assertFalse(
                            baselineTarget.contentEquals(cropTarget),
                            "$family Crop portrait must change the portrait page.",
                        )
                        assertFalse(
                            baselineTarget.contentEquals(fitTarget),
                            "$family Fit portrait must change the portrait page.",
                        )
                        assertFalse(
                            cropTarget.contentEquals(fitTarget),
                            "$family Crop and Fit must produce different portrait placement.",
                        )
                        if (
                            family == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ||
                            family == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY
                        ) {
                            val topFrameChanged = (25 until 33).any { y ->
                                (220 until 311).any { x ->
                                    val pixelIndex = y * 612 + x
                                    baselineTarget[pixelIndex] != cropTarget[pixelIndex]
                                }
                            }
                            assertFalse(
                                topFrameChanged,
                                "$family portrait overlay must preserve the decorative top frame.",
                            )
                        }
                        if (family == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY) {
                            val lowerPortraitChanged = (125 until 160).any { y ->
                                (205 until 326).any { x ->
                                    val pixelIndex = y * 612 + x
                                    baselineTarget[pixelIndex] != cropTarget[pixelIndex]
                                }
                            }
                            assertTrue(
                                lowerPortraitChanged,
                                "Custom v2 per-Ability Crop must use its taller portrait field.",
                            )
                        }
                        assertTrue(
                            pixels(baselineRenderer, stableIndex)
                                .contentEquals(pixels(cropRenderer, stableIndex)),
                            "$family portrait overlay must not alter non-portrait pages.",
                        )

                        assertTrue(
                            ImageIO.write(
                                cropRenderer.renderImageWithDPI(targetIndex, 180f, ImageType.RGB),
                                "png",
                                File(proofDir, "portrait-$slug-crop.png"),
                            ),
                        )
                        assertTrue(
                            ImageIO.write(
                                fitRenderer.renderImageWithDPI(targetIndex, 180f, ImageType.RGB),
                                "png",
                                File(proofDir, "portrait-$slug-fit.png"),
                            ),
                        )
                    }
                }
            }
        }
    }

    @Test
    fun classicAppModifiedKeepsPortraitOnAspectPageAfterInsertedStatisticsPage() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val portraitRef = "portrait://app-modified-fantasy"
        val portraitBytes = ByteArrayOutputStream().use { buffer ->
            val image = BufferedImage(480, 320, BufferedImage.TYPE_INT_RGB)
            val graphics = image.createGraphics()
            graphics.color = Color(72, 112, 188)
            graphics.fillRect(0, 0, image.width, image.height)
            graphics.color = Color(235, 215, 120)
            graphics.fillOval(125, 45, 230, 230)
            graphics.dispose()
            assertTrue(ImageIO.write(image, "png", buffer))
            buffer.toByteArray()
        }
        val renderer = DesktopPcSheetWholeDraftRenderer(
            portraitBytesLoader = { ref -> portraitBytes.takeIf { ref == portraitRef } },
        )
        val base = denseDraftAggregateWithCustomStatistics()
        val aggregate = base.copy(
            closure = base.closure.copy(portraitRef = portraitRef),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
                customStatisticsPresentation = PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET,
                portraitFitMode = PcSheetPortraitFitMode.CROP_TO_FILL,
            ),
            sources = PcSheetExportSources(
                permanent = aggregate,
                locallyAvailablePortraitRefs = setOf(portraitRef),
            ),
        )

        val pdf = File(proofDir, "owner-review-app-modified-fantasy-with-portrait.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages > plan.basePages.size)
            val pageTexts = (1..document.numberOfPages).map { pageNumber ->
                PDFTextStripper().apply {
                    startPage = pageNumber
                    endPage = pageNumber
                }.getText(document)
            }
            val aspectPageIndex = pageTexts.indexOfFirst { it.contains("ASPECTO", ignoreCase = true) }
            assertTrue(aspectPageIndex >= 2, "Inserted App Modified statistics page must precede Classic ASPECTO.")
            assertTrue(pageTexts[1].contains("HOJA MODIFICADA", ignoreCase = true))

            val pdfRenderer = PDFRenderer(document)
            val modifiedImage = pdfRenderer.renderImageWithDPI(1, 96f, ImageType.RGB)
            val aspectImage = pdfRenderer.renderImageWithDPI(aspectPageIndex, 96f, ImageType.RGB)
            val modifiedBluePixels = countPortraitBluePixels(modifiedImage)
            val aspectBluePixels = countPortraitBluePixels(aspectImage)
            assertTrue(
                aspectBluePixels > modifiedBluePixels * 4 + 100,
                "Classic portrait ink must remain on the ASPECTO page after App Modified insertion.",
            )

            assertTrue(
                ImageIO.write(
                    pdfRenderer.renderImageWithDPI(aspectPageIndex, 180f, ImageType.RGB),
                    "png",
                    File(proofDir, "owner-review-app-modified-fantasy-with-portrait-aspect.png"),
                ),
            )
        }
    }

    private fun countPortraitBluePixels(image: BufferedImage): Int {
        var count = 0
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val rgb = image.getRGB(x, y)
                val red = rgb shr 16 and 0xFF
                val green = rgb shr 8 and 0xFF
                val blue = rgb and 0xFF
                if (blue > 135 && blue > red + 35 && blue > green + 10) {
                    count += 1
                }
            }
        }
        return count
    }

    @Test
    fun classicContinuesLongCanonicalBaseContentWithoutSilentLoss() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate()
        val sourceId = base.sheet.spellcastingSources.single().id
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                name = "Iria Noctis Cartógrafa Mayor De La Frontera Septentrional",
                classes = listOf(
                    base.sheet.classes.first().copy(
                        name = "Maga Cartógrafa De Los Umbrales Septentrionales",
                        subclassName = "Guardiana Mayor De Senderos Sellados",
                        sortOrder = 0,
                    ),
                ),
                combatEntries = base.sheet.combatEntries.take(5).mapIndexed { index, entry ->
                    if (index == 4) {
                        entry.copy(
                            name = "Acción terminal Classic",
                            type = CharacterCombatEntryType.ACTION,
                            notes = "Entrada preservada fuera de las cuatro filas base.",
                            sortOrder = index,
                        )
                    } else {
                        entry.copy(sortOrder = index, notes = null)
                    }
                },
                inventoryItems = emptyList(),
                background = CharacterBackground(
                    name = "Exploradora de archivos y rutas antiguas de frontera",
                    summary = "Conserva mapas incompletos, compara testimonios y registra cada cambio encontrado durante la expedición.",
                    race = "Humana",
                    religionFaith = "Tradición cartográfica de la antigua Academia del Norte",
                    personalityTraits = "Anota cada variación del terreno incluso cuando el grupo tiene prisa y deja una copia de seguridad. COLA RASGO AUDITADA",
                    ideals = "El conocimiento debe sobrevivir a quien lo descubre y permanecer verificable para futuros viajeros. COLA IDEAL AUDITADA",
                    bonds = "Prometió devolver el mapa original y proteger a quienes ayudaron a reconstruir la ruta perdida. COLA VINCULO AUDITADA",
                    flaws = "Puede detener una retirada para comprobar una inscripción que considere irrepetible. COLA DEFECTO AUDITADA",
                    story = "La expedición siguió señales parciales durante semanas, corrigió tres mapas incompatibles y finalmente encontró una galería sellada. La última anotación confirma que el corredor norte continúa más allá del archivo inferior. COLA HISTORIA AUDITADA",
                ),
                traits = listOf(
                    base.sheet.traits.first().copy(
                        name = "Memoria cartográfica",
                        source = "",
                        type = CharacterTraitType.CLASS,
                        description = "Conserva referencias de rutas, hitos, distancias, símbolos y cambios observados durante viajes prolongados. Puede comparar mapas contradictorios y mantener una versión trazable del recorrido. COLA RASGO LARGO AUDITADA",
                        notes = null,
                        maxUses = null,
                        spentUses = 0,
                        recovery = null,
                        activation = null,
                        sortOrder = 0,
                    ),
                ),
                proficiencies = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                spellSlots = base.sheet.spellSlots.map { slot ->
                    if (slot.level == 1) slot.copy(totalSlots = 7, spentSlots = 3) else slot
                },
                spells = listOf(
                    spell(901, "Luz", 0, sourceId, true),
                    spell(902, "Escudo", 1, sourceId, true),
                ),
                generalNotes = "",
                noteCards = emptyList(),
                weaponMasteries = emptyList(),
                forms = emptyList(),
                companions = emptyList(),
            ),
            closure = base.closure.copy(
                customSkills = emptyList(),
                exhaustionLevel = 0,
                concentration = null,
                conditions = emptyList(),
                defenses = emptyList(),
                movements = emptyList(),
                senses = emptyList(),
                inventoryUsage = emptyList(),
                temporaryEffects = emptyList(),
            ),
            successor = base.successor.copy(
                customAttributes = emptyList(),
                customSkillAbilities = emptyList(),
                combatDamage = emptyList(),
                customMarkers = emptyList(),
                resourceConfigurations = emptyList(),
                speciesIdentity = null,
                subraceIdentity = null,
                backgroundIdentity = null,
                traitProvenance = emptyList(),
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )
        val pdf = File(proofDir, "fantasy-canonical-overflow-audit.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages > 3)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(Regex("COLA\\s+HISTORIA\\s+AUDITADA").containsMatchIn(extracted))
            assertTrue(Regex("COLA\\s+RASGO\\s+AUDITADA").containsMatchIn(extracted))
            assertTrue(Regex("COLA\\s+IDEAL\\s+AUDITADA").containsMatchIn(extracted))
            assertTrue(Regex("COLA\\s+VINCULO\\s+AUDITADA").containsMatchIn(extracted))
            assertTrue(Regex("COLA\\s+DEFECTO\\s+AUDITADA").containsMatchIn(extracted))
            assertTrue(Regex("COLA\\s+RASGO\\s+LARGO\\s+AUDITADA").containsMatchIn(extracted))
            assertTrue(extracted.contains("Acción terminal Classic"))
            assertTrue(extracted.contains("7 totales"))
            assertTrue(Regex("3\\s+gastados").containsMatchIn(extracted))
            assertTrue(extracted.contains("Tradición cartográfica"))
            assertTrue(Regex("Iria\\s+Noctis\\s+Cartógrafa\\s+Mayor").containsMatchIn(extracted))
        }
        assertTrue(pdf.length() > 20_000L)
    }

    @Test
    fun generatesWholeCustomFamilyFirstDraftsForOwnerReview() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregate()

        val families = listOf(
            Triple(PcSheetVisualFamily.CUSTOM_V1, "custom-v1-whole-draft", 5),
            Triple(PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE, "custom-v2-per-attribute-whole-draft", 6),
            Triple(PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY, "custom-v2-per-ability-whole-draft", 6),
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
                // These are minimum frozen base-family page counts. Data-driven Extended roles may
                // legitimately add pages as production promotion advances.
                assertTrue(document.numberOfPages >= expectedPages)
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
                    assertTrue(extracted.contains("Curiosidad académica"))
                    assertTrue(extracted.contains("Subestima los riesgos"))
                    assertTrue(extracted.contains("Corellon"))
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
    fun promotesOwnerApprovedCustomV1ExtendedCustomStatisticsFromRealPlanData() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithCustomStatistics()
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val pdf = File(proofDir, "custom-v1-production-extended-stats-pass1.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages >= 6)
            val layerNames = document.documentCatalog.ocProperties
                ?.getGroupNames()
                ?.toList()
                .orEmpty()
            assertTrue(layerNames.any { it.startsWith("V1X STATS P1 - STRUCTURE") })
            assertTrue(layerNames.any { it.startsWith("V1X STATS P1 - VALUES") })
            assertTrue(layerNames.any { it.startsWith("V1X STATS P1 - MARKERS") })

            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Estadísticas Personalizadas"))
            assertTrue(extracted.contains("HONor"))
            assertTrue(extracted.contains("VOLuntad"))
            assertTrue(extracted.contains("SUErte"))
            assertTrue(extracted.contains("Etiqueta"))
            assertTrue(extracted.contains("Criptografía"))
            assertTrue(extracted.contains("Acrobacia aérea"))

            val image = PDFRenderer(document).renderImageWithDPI(5, 220f, ImageType.RGB)
            val png = File(proofDir, "custom-v1-production-extended-stats-pass1-page-6.png")
            assertTrue(ImageIO.write(image, "png", png))
            assertTrue(png.length() > 0L)
        }
        assertTrue(pdf.length() > 20_000L)
    }

    @Test
    fun paginatesCustomV1ExtendedCustomStatisticsWithoutDroppingCanonicalRows() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithCustomStatisticsOverflow()
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val pdf = File(proofDir, "custom-v1-custom-stats-overflow.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages >= 8)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Vínculo 7-7"))
            val layerNames = document.documentCatalog.ocProperties
                ?.getGroupNames()
                ?.toList()
                .orEmpty()
            assertTrue(layerNames.any { it.startsWith("V1X STATS P3 - VALUES") })
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
                assertTrue(document.numberOfPages >= 7)
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
    fun promotesOwnerApprovedCustomV1TraitsContinuationWithoutCustomStatistics() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate()

        val firstTrait = base.sheet.traits.first().copy(
            source = "Fuente primaria de rasgo",
            maxUses = 2,
            spentUses = 1,
            recovery = "Descanso largo",
            notes = "El uso restante debe conservarse en la continuación v1.",
        )
        val sourceOnlyTrait = base.sheet.traits[1].copy(
            source = "Fuente canónica sin otros metadatos",
            notes = null,
            maxUses = null,
            spentUses = 0,
            recovery = null,
            activation = null,
        )
        val overflowTrait = base.sheet.traits.first().copy(
            id = uuid("8c100000-0000-0000-0000-000000000001"),
            name = "Rasgo de desborde v1",
            source = "Prueba de continuación",
            type = CharacterTraitType.OTHER,
            description = "Descripción canónica que debe sobrevivir fuera de los seis espacios base.",
            notes = "Metadato de desborde.",
            sortOrder = 999,
        )
        val proficiencies = listOf(
            CharacterProficiency(
                id = uuid("8c200000-0000-0000-0000-000000000001"),
                type = CharacterProficiencyType.TOOL,
                name = "Competencia extendida de prueba",
                source = "Fuente de prueba",
                notes = "Nota de competencia que debe conservarse",
                sortOrder = 0,
            ),
            CharacterProficiency(
                id = uuid("8c200000-0000-0000-0000-000000000002"),
                type = CharacterProficiencyType.LANGUAGE,
                name = "Lengua extendida",
                source = "Fuente de prueba",
                sortOrder = 1,
            ),
        )
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                traits = listOf(firstTrait, sourceOnlyTrait) + base.sheet.traits.drop(2) + overflowTrait,
                proficiencies = proficiencies,
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val pdf = File(proofDir, "custom-v1-production-extended-traits-pass2.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages >= 6)
            val layers = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
            assertTrue(layers.any { it.startsWith("V1X TRAITS P1 - STRUCTURE") })
            assertTrue(layers.any { it.startsWith("V1X TRAITS P1 - VALUES") })
            assertTrue(layers.any { it.startsWith("V1X TRAITS P2 - STRUCTURE") })
            assertFalse(layers.any { it.startsWith("V1X STATS") })

            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Rasgo de desborde v1"))
            assertTrue(extracted.contains("Ves en luz tenue y oscuridad"))
            assertTrue(extracted.contains("Combinas herramientas y recursos disponibles"))
            assertTrue(extracted.contains("Fuente primaria de rasgo"))
            assertFalse(
                extracted.contains("Fuente canónica sin otros metadatos"),
                "Source-only metadata on an already represented trait must not create duplicate trait detail.",
            )
            assertTrue(extracted.contains("Competencia extendida de prueba"))
            assertTrue(Regex("Nota\\s+de\\s+competencia").containsMatchIn(extracted))
            assertTrue(extracted.contains("Lengua extendida"))
            assertTrue(extracted.contains("Usos 1 / 2"))

            val image = PDFRenderer(document).renderImageWithDPI(5, 220f, ImageType.RGB)
            val png = File(proofDir, "custom-v1-production-extended-traits-pass2-page-6.png")
            assertTrue(ImageIO.write(image, "png", png))
            assertTrue(png.length() > 0L)
        }
        assertTrue(pdf.length() > 20_000L)
    }

    @Test
    fun promotesOwnerApprovedCustomV1ResourcesAndOptionsFromRealPlanData() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate()
        val management = denseDraftAggregateWithExtendedManagement()
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                inventoryItems = emptyList(),
                currencies = emptyList(),
                resources = management.sheet.resources.mapIndexed { index, resource ->
                    if (index == 2) {
                        resource.copy(notes = "Nota persistente del recurso.")
                    } else {
                        resource
                    }
                },
                classOptions = management.sheet.classOptions.mapIndexed { index, option ->
                    if (index == 0) {
                        option.copy(notes = "Nota persistente de opción.")
                    } else {
                        option
                    }
                },
            ),
            successor = base.successor.copy(
                customMarkers = management.successor.customMarkers,
                preferences = base.successor.preferences.copy(valuablesText = ""),
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val pdf = File(proofDir, "custom-v1-production-extended-resources-pass3.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            val layers = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
            assertTrue(layers.any { it.startsWith("V1X RESOURCES P1 - STRUCTURE") })
            assertTrue(layers.any { it.startsWith("V1X RESOURCES P1 - VALUES") })
            assertTrue(layers.any { it.startsWith("V1X RESOURCES P1 - MARKERS") })
            assertFalse(layers.any { it.startsWith("V1X STATS") })

            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Recursos"))
            assertTrue(extracted.contains("Puntos de enfoque"))
            assertTrue(Regex("Nota\\s+persistente\\s+del\\s+recurso").containsMatchIn(extracted))
            assertTrue(extracted.contains("Metamagia cuidadosa"))
            assertTrue(extracted.contains("1 punto"))
            assertTrue(extracted.contains("Prueba PDF"))
            assertTrue(Regex("Nota\\s+persistente\\s+de\\s+opción").containsMatchIn(extracted))
            assertTrue(extracted.contains("Puntos de destino"))
            assertTrue(Regex("Sólo\\s+se\\s+recupera").containsMatchIn(extracted))
            assertTrue(extracted.contains("7/12"))

            val pageIndex = document.numberOfPages - 1
            val image = PDFRenderer(document).renderImageWithDPI(pageIndex, 220f, ImageType.RGB)
            val png = File(proofDir, "custom-v1-production-extended-resources-pass3-page-${pageIndex + 1}.png")
            assertTrue(ImageIO.write(image, "png", png))
            assertTrue(png.length() > 0L)
        }
        assertTrue(pdf.length() > 20_000L)
    }

    @Test
    fun promotesOwnerApprovedCustomV1InventoryContinuationFromRealPlanData() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate().withoutV1SemanticSupplements()
        val ordinary = base.sheet.inventoryItems.first { !it.special }.copy(
            equipped = true,
            notes = "Nota persistente del equipo ordinario.",
        )
        val special = base.sheet.inventoryItems.first { it.special }.copy(
            attuned = true,
            weightLb = 1.5,
            notes = "Nota persistente del equipo especial.",
        )
        val customCurrency = CharacterCurrency(
            key = "pm",
            name = "Piezas de mithril",
            amount = 7,
            sortOrder = 99,
            isDefault = false,
        )
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                inventoryItems = listOf(ordinary, special),
                currencies = base.sheet.currencies + customCurrency,
                traits = emptyList(),
                proficiencies = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                generalNotes = "",
                noteCards = emptyList(),
            ),
            closure = base.closure.copy(
                inventoryUsage = listOf(
                    CharacterInventoryUsage(
                        itemId = ordinary.id,
                        kind = CharacterConsumableKind.CONSUMABLE,
                        quickUseAmount = 2,
                        carryState = CharacterInventoryCarryState.STORED,
                    ),
                ),
            ),
            successor = base.successor.copy(
                customMarkers = emptyList(),
                preferences = base.successor.preferences.copy(
                    valuablesText = (1..9).joinToString("; ") { index ->
                        "Tesoro canónico $index ($index po)"
                    },
                ),
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val pdf = File(proofDir, "custom-v1-production-extended-inventory-pass4.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            // Page count is intentionally data-driven. Guard the semantic continuation itself
            // rather than freezing the pre-repair pagination topology.
            assertTrue(document.numberOfPages >= plan.basePages.size)
            val layers = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
            listOf("STRUCTURE", "CLEANUP", "LABELS", "VALUES", "MARKERS").forEach { role ->
                assertTrue(
                    layers.any { it.startsWith("V1X INVENTORY P1 - $role") },
                    "Missing v1 inventory semantic layer $role",
                )
            }
            assertFalse(layers.any { it.startsWith("V1X TRAITS") })
            assertFalse(layers.any { it.startsWith("V1X RESOURCES") })

            val extracted = PDFTextStripper().getText(document)
            assertFalse(Regex("Nota\\s+persistente\\s+del\\s+equipo\\s+ordinario").containsMatchIn(extracted))
            assertFalse(extracted.contains("Consumible"))
            assertFalse(Regex("Uso\\s+rápido\\s+2").containsMatchIn(extracted))
            assertFalse(extracted.contains("Almacenado"))
            assertTrue(extracted.contains("Sintonizado"))
            assertTrue(Regex("Nota\\s+persistente\\s+del\\s+equipo\\s+especial").containsMatchIn(extracted))
            assertTrue(
                Regex("Piezas\\s+de\\s+mithril[\\s\\S]*?7").containsMatchIn(extracted),
                "Custom currency name and amount must survive in the native Monedas rows.",
            )
            assertTrue(extracted.contains("Tesoro canónico 9"))

            // The inventory continuation layer itself is the stable contract; its physical
            // page number may change when compatible Notes/overflow content is packed differently.
            val image = PDFRenderer(document).renderImageWithDPI(document.numberOfPages - 1, 220f, ImageType.RGB)
            val png = File(proofDir, "custom-v1-production-extended-inventory-pass4-last-page.png")
            assertTrue(ImageIO.write(image, "png", png))
            assertTrue(png.length() > 0L)
        }
        assertTrue(pdf.length() > 20_000L)
    }

    @Test
    fun paginatesOwnerApprovedCustomV1SpellContinuationWithoutDroppingCanonicalSpells() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate().withoutV1SemanticSupplements()
        val sourceId = base.sheet.spellcastingSources.single().id
        val spells = (1..30).map { index ->
            spell(
                index = 100 + index,
                name = "Hechizo canónico $index",
                level = 1,
                sourceId = sourceId,
                prepared = index == 11 || index == 30,
            )
        }
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                inventoryItems = emptyList(),
                currencies = emptyList(),
                traits = emptyList(),
                proficiencies = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                spells = spells,
                generalNotes = "",
                noteCards = emptyList(),
            ),
            successor = base.successor.copy(
                customMarkers = emptyList(),
                preferences = base.successor.preferences.copy(valuablesText = ""),
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val pdf = File(proofDir, "custom-v1-production-extended-spells-pass5.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            val layers = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
            assertTrue(layers.any { it.startsWith("V1X SPELLS P1 - STRUCTURE") })
            assertTrue(layers.any { it.startsWith("V1X SPELLS P2 - STRUCTURE") })
            assertTrue(layers.any { it.startsWith("V1X SPELLS P2 - MARKERS") })
            assertFalse(layers.any { it.startsWith("V1X TRAITS") })
            assertFalse(layers.any { it.startsWith("V1X RESOURCES") })
            assertFalse(layers.any { it.startsWith("V1X INVENTORY") })

            val extracted = PDFTextStripper().getText(document)
            listOf(11, 20, 21, 30).forEach { index ->
                assertTrue(
                    Regex("Hechizo\\s+canónico\\s+$index").containsMatchIn(extracted),
                    "Missing canonical continuation spell $index.",
                )
            }

            val firstIndex = document.numberOfPages - 2
            val secondIndex = document.numberOfPages - 1
            val first = PDFRenderer(document).renderImageWithDPI(firstIndex, 220f, ImageType.RGB)
            val second = PDFRenderer(document).renderImageWithDPI(secondIndex, 220f, ImageType.RGB)
            assertTrue(
                ImageIO.write(
                    first,
                    "png",
                    File(proofDir, "custom-v1-production-extended-spells-pass5-page-${firstIndex + 1}.png"),
                ),
            )
            assertTrue(
                ImageIO.write(
                    second,
                    "png",
                    File(proofDir, "custom-v1-production-extended-spells-pass5-page-${secondIndex + 1}.png"),
                ),
            )
        }
        assertTrue(pdf.length() > 20_000L)
    }

    @Test
    fun paginatesOwnerApprovedCustomV1NotesWithoutDroppingCanonicalLines() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate().withoutV1SemanticSupplements()
        val notes = (1..40).map { index ->
            CharacterNote(
                id = uuid("91000000-0000-0000-0000-${index.toString().padStart(12, '0')}"),
                title = "Nota canónica $index",
                content = "Contenido $index",
                sortOrder = index,
            )
        }
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                inventoryItems = emptyList(),
                currencies = emptyList(),
                traits = emptyList(),
                proficiencies = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                spells = emptyList(),
                generalNotes = "",
                noteCards = notes,
            ),
            successor = base.successor.copy(
                customMarkers = emptyList(),
                preferences = base.successor.preferences.copy(valuablesText = ""),
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )

        val pdf = File(proofDir, "custom-v1-production-extended-notes-pass6.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            val layers = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
            assertTrue(plan.basePages.any { it.role == PcSheetBasePageRole.NOTES })
            assertFalse(
                layers.any { it.startsWith("V1X NOTES") },
                "Forty short notes fit in the native narrative Notes area plus the dedicated Notes page.",
            )
            assertFalse(layers.any { it.startsWith("V1X TRAITS") })
            assertFalse(layers.any { it.startsWith("V1X RESOURCES") })
            assertFalse(layers.any { it.startsWith("V1X INVENTORY") })
            assertFalse(layers.any { it.startsWith("V1X SPELLS") })

            val extracted = PDFTextStripper().getText(document)
            listOf(35, 40).forEach { index ->
                assertTrue(
                    Regex("Nota\\s+canónica\\s+$index").containsMatchIn(extracted),
                    "Missing canonical continuation note $index.",
                )
            }

            val pageIndex = document.numberOfPages - 1
            val image = PDFRenderer(document).renderImageWithDPI(pageIndex, 220f, ImageType.RGB)
            assertTrue(
                ImageIO.write(
                    image,
                    "png",
                    File(proofDir, "custom-v1-production-extended-notes-pass6-page-${pageIndex + 1}.png"),
                ),
            )
        }
        assertTrue(pdf.length() > 20_000L)
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
                assertTrue(document.numberOfPages >= 8)
                val layerNames = document.documentCatalog.ocProperties
                    ?.getGroupNames()
                    ?.toList()
                    .orEmpty()
                listOf("V2X TRAITS", "V2X RESOURCES").forEach { prefix ->
                    listOf("STRUCTURE", "CLEANUP", "LABELS", "VALUES", "MARKERS").forEach { role ->
                        assertTrue(
                            layerNames.contains("$prefix - $role"),
                            "Missing frozen semantic layer $prefix - $role",
                        )
                    }
                }

                val extracted = PDFTextStripper().getText(document)
                assertTrue(extracted.contains("RASGOS Y ATRIBUTOS"))
                assertTrue(extracted.contains("RECURSOS Y OPCIONES"))
                assertTrue(extracted.contains("Puntos de enfoque"))
                assertTrue(extracted.contains("Metamagia cuidadosa"))
                assertFalse(
                    extracted.contains("Una vez al día recuperas espacios de conjuro"),
                    "Resource-backed trait prose must not be replayed in Traits.",
                )
                assertTrue(extracted.contains("Puntos de destino"))
                assertTrue(extracted.contains("Sólo se recupera"))
                assertTrue(extracted.contains("narrativo"))
                assertTrue(extracted.contains("7/12"))
                assertFalse(extracted.contains("Dados de portento", ignoreCase = true))
                assertFalse(extracted.contains("Especie", ignoreCase = true))

                (4 until document.numberOfPages).forEach { pageIndex ->
                    val image = PDFRenderer(document).renderImageWithDPI(pageIndex, 220f, ImageType.RGB)
                    val png = File(proofDir, "$stem-extended-page-${pageIndex + 1}.png")
                    assertTrue(ImageIO.write(image, "png", png))
                    assertTrue(png.length() > 0L)
                }
            }
            assertTrue(pdf.length() > 20_000L)
        }
    }

    @Test
    fun promotesDataWarrantedCustomV2InventorySpellAndNotesContinuations() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithOverflowContinuations()

        listOf(
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-per-attribute-production-pass3",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-per-ability-production-pass3",
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
                assertTrue(document.numberOfPages >= 8)
                val layerNames = document.documentCatalog.ocProperties
                    ?.getGroupNames()
                    ?.toList()
                    .orEmpty()
                listOf("V2X INVENTORY", "V2X SPELLS", "V2X NOTES").forEach { prefix ->
                    listOf("STRUCTURE", "CLEANUP", "LABELS", "VALUES", "MARKERS").forEach { role ->
                        assertTrue(
                            layerNames.contains("$prefix - $role"),
                            "Missing frozen semantic layer $prefix - $role",
                        )
                    }
                }

                val extracted = PDFTextStripper().getText(document)
                assertFalse(extracted.contains("TESORO / OBJETOS / OTROS"))
                assertTrue(extracted.contains("137"))
                assertTrue(extracted.contains("Sintonizado"))
                assertTrue(extracted.contains("Viales vacíos"))
                assertFalse(extracted.contains("Muestras y"))
                assertFalse(extracted.contains("reactivos."))
                assertTrue(extracted.contains("Peso 4 lb"))
                assertTrue(extracted.contains("Foco arcano y arma improvisada."))
                assertTrue(extracted.contains("Conjuro adicional 9"))
                assertTrue(extracted.contains("Nota de continuación 45"))
                assertFalse(extracted.contains("Especie", ignoreCase = true))

                ((document.numberOfPages - 3) until document.numberOfPages).forEach { pageIndex ->
                    val image = PDFRenderer(document).renderImageWithDPI(pageIndex, 220f, ImageType.RGB)
                    val png = File(proofDir, "$stem-extended-page-${pageIndex + 1}.png")
                    assertTrue(ImageIO.write(image, "png", png))
                    assertTrue(png.length() > 0L)
                }
            }
            assertTrue(pdf.length() > 20_000L)
        }
    }

    private fun denseDraftAggregateWithOverflowContinuations(): PcSheetExportAggregate {
        val base = denseDraftAggregate()
        val sourceId = base.sheet.spellcastingSources.single().id
        val extraSpells = (1..9).map { index ->
            spell(
                index = 100 + index,
                name = "Conjuro adicional $index",
                level = 1,
                sourceId = sourceId,
                prepared = index % 2 == 1,
            )
        }
        val longNotes = (1..55).joinToString("\n") { index ->
            "Nota de continuación $index: registro de desborde."
        }

        return base.copy(
            sheet = base.sheet.copy(
                spells = base.sheet.spells + extraSpells,
                generalNotes = longNotes,
                noteCards = emptyList(),
            ),
        )
    }

    @Test
    fun exportsCurrentSnapshotOperationalAndReferenceSemanticsInCustomV1() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val permanent = denseDraftAggregate().copy(
            sheet = denseDraftAggregate().sheet.copy(
                inventoryItems = emptyList(),
                traits = emptyList(),
                proficiencies = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                spells = emptyList(),
                generalNotes = "",
                noteCards = emptyList(),
            ),
        )
        val ammunition = inventory(
            index = 200,
            name = "Flechas de prueba",
            quantity = 20,
            weight = 0.05,
            location = "Carcaj",
            special = false,
            equipped = false,
            description = "Munición actual.",
        )
        val carriedTool = ammunition.copy(
            id = uuid("50000000-0000-0000-0000-000000000201"),
            name = "Herramienta llevada",
            quantity = 1,
            weightLb = null,
            description = null,
            location = null,
            sortOrder = 201,
        )
        val current = permanent.copy(
            sheet = permanent.sheet.copy(
                currentHp = 11,
                tempHp = 6,
                deathSaveSuccesses = 2,
                deathSaveFailures = 1,
                passivePerceptionAdjustment = 2,
                inventoryItems = listOf(ammunition, carriedTool),
                weaponMasteries = listOf(
                    CharacterWeaponMastery(
                        id = uuid("8c000000-0000-0000-0000-000000000001"),
                        weaponName = "Espada larga",
                        masteryName = "Empujar",
                        source = "Guerrero",
                        notes = "Sólo con esta arma.",
                    ),
                ),
                forms = listOf(
                    CharacterForm(
                        id = uuid("8d000000-0000-0000-0000-000000000001"),
                        name = "Forma de lobo",
                        source = "Rasgo",
                        challengeRatingText = "1/4",
                        armorClass = 13,
                        hitPoints = 18,
                        movement = "40 ft",
                        senses = "Percepción aguda",
                        actionSummary = "Mordisco",
                        notes = "Forma registrada.",
                    ),
                ),
                companions = listOf(
                    CharacterCompanion(
                        id = uuid("8e000000-0000-0000-0000-000000000001"),
                        name = "Nim",
                        kind = "Familiar",
                        source = "Conjuro",
                        armorClass = 12,
                        maxHp = 9,
                        currentHp = 7,
                        tempHp = 1,
                        speed = "30 ft",
                        abilitySummary = "Explorador",
                        sensesProficiencies = "Visión en la oscuridad",
                        traitsActions = "Ayudar",
                        notes = "Compañero actual.",
                    ),
                ),
            ),
            closure = permanent.closure.copy(
                progressMode = CharacterProgressMode.MILESTONE,
                milestoneProgress = "3 de 5 hitos",
                exhaustionLevel = 2,
                concentration = CharacterConcentration(
                    name = "Volar",
                    notes = "Concentración activa",
                ),
                conditions = listOf(
                    CharacterCondition(
                        id = uuid("8f000000-0000-0000-0000-000000000001"),
                        name = "Asustado",
                        source = "Efecto actual",
                        notes = "Hasta final del turno.",
                    ),
                ),
                defenses = listOf(
                    CharacterDefense(
                        id = uuid("90000000-0000-0000-0000-000000000001"),
                        type = CharacterDefenseType.RESISTANCE,
                        name = "Fuego",
                        source = "Objeto",
                    ),
                ),
                movements = listOf(
                    CharacterMovement(
                        id = uuid("91000000-0000-0000-0000-000000000001"),
                        type = CharacterMovementType.FLY,
                        name = "Vuelo mágico",
                        speedFeet = 60,
                        notes = "Mientras concentra.",
                    ),
                ),
                senses = listOf(
                    CharacterSense(
                        id = uuid("92000000-0000-0000-0000-000000000001"),
                        name = "Visión verdadera",
                        rangeFeet = 30,
                    ),
                ),
                temporaryEffects = listOf(
                    CharacterTemporaryEffect(
                        id = uuid("93000000-0000-0000-0000-000000000001"),
                        name = "Bendición temporal",
                        summary = "+1 a una prueba",
                        durationText = "10 minutos",
                        source = "Aliado",
                        notes = "Activo",
                        active = true,
                    ),
                ),
                inventoryUsage = listOf(
                    CharacterInventoryUsage(
                        itemId = ammunition.id,
                        kind = CharacterConsumableKind.AMMUNITION,
                        quickUseAmount = 2,
                        carryState = CharacterInventoryCarryState.STORED,
                    ),
                    CharacterInventoryUsage(
                        itemId = carriedTool.id,
                        kind = CharacterConsumableKind.NONE,
                        quickUseAmount = 1,
                        carryState = CharacterInventoryCarryState.CARRIED,
                    ),
                ),
            ),
        )

        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.CURRENT_SNAPSHOT,
            ),
            sources = PcSheetExportSources(
                permanent = permanent,
                currentSnapshot = current,
            ),
        )
        val pdf = File(proofDir, "custom-v1-current-snapshot-semantics.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages >= 5)
            val layers = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
            assertTrue(layers.any { it.startsWith("V1X TRAITS P1 - VALUES") })
            assertFalse(
                layers.any { it.startsWith("V1X INVENTORY P1 - VALUES") },
                "Ammunition/status metadata alone must not allocate a v1 Equipment continuation.",
            )
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("3 de 5 hitos"))
            assertTrue(extracted.contains("Inspiración"))
            assertTrue(extracted.contains("PG temporales"))
            assertTrue(extracted.contains("Salvaciones de muerte"))
            assertTrue(extracted.contains("Percepción pasiva"))
            assertTrue(extracted.contains("Agotamiento"))
            assertTrue(extracted.contains("Asustado"))
            assertTrue(extracted.contains("Resistencia"))
            assertTrue(extracted.contains("Vuelo mágico"))
            assertTrue(extracted.contains("Visión verdadera"))
            assertTrue(extracted.contains("Bendición temporal"))
            assertTrue(extracted.contains("Espada larga"))
            assertTrue(extracted.contains("Forma de lobo"))
            assertTrue(extracted.contains("Nim"))
            assertEquals(1, Regex("\\bFlechas de prueba\\b").findAll(extracted).count())
            assertFalse(extracted.contains("Estado: Munición"))
            assertFalse(extracted.contains("Uso rápido"))
            assertFalse(extracted.contains("Almacenado"))
        }

        val classicBackground = CharacterBackground(
            name = "Exploradora de prueba",
            summary = "Resumen breve para validar Current Snapshot.",
            race = "Humana",
            religionFaith = "",
            personalityTraits = "Observadora.",
            ideals = "Prudencia.",
            bonds = "Protege al grupo.",
            flaws = "Duda demasiado.",
            story = "Historia breve.",
        )
        val classicPermanent = permanent.copy(
            sheet = permanent.sheet.copy(
                background = classicBackground,
                combatEntries = permanent.sheet.combatEntries.take(4).mapIndexed { index, entry ->
                    entry.copy(sortOrder = index, notes = null)
                },
            ),
        )
        val classicCurrent = current.copy(
            sheet = current.sheet.copy(
                background = classicBackground,
                combatEntries = current.sheet.combatEntries.take(4).mapIndexed { index, entry ->
                    entry.copy(sortOrder = index, notes = null)
                } + CharacterCombatEntry(
                    id = uuid("94000000-0000-0000-0000-000000000099"),
                    name = "Reacción de cobertura",
                    type = CharacterCombatEntryType.REACTION,
                    attackModifier = null,
                    damageEffect = "Protege a un aliado cercano.",
                    rangeText = null,
                    notes = "Entrada fuera de la capacidad base.",
                    sortOrder = 99,
                ),
                spellSlots = current.sheet.spellSlots.map { slot ->
                    if (slot.level == 1) slot.copy(spentSlots = 3.coerceAtMost(slot.totalSlots)) else slot
                },
                companions = current.sheet.companions + CharacterCompanion(
                    id = uuid("8e000000-0000-0000-0000-000000000002"),
                    name = "Eco",
                    kind = "Compañero",
                    sortOrder = 1,
                ),
            ),
        )
        val classicPermanentPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = classicPermanent),
        )
        val classicPermanentPdf = File(proofDir, "fantasy-permanent-slot-baseline.pdf")
        classicPermanentPdf.outputStream().use { renderer.renderDraft(classicPermanentPlan, it) }

        val classicPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.CURRENT_SNAPSHOT,
            ),
            sources = PcSheetExportSources(
                permanent = classicPermanent,
                currentSnapshot = classicCurrent,
            ),
        )
        val classicPdf = File(proofDir, "fantasy-current-snapshot-semantics.pdf")
        classicPdf.outputStream().use { renderer.renderDraft(classicPlan, it) }

        Loader.loadPDF(classicPdf).use { document ->
            assertTrue(document.numberOfPages >= 5)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(Regex("3\\s+de\\s+5\\s+hitos").containsMatchIn(extracted))
            assertTrue(extracted.contains("PG temporales"))
            assertTrue(extracted.contains("Agotamiento"))
            assertTrue(extracted.contains("Asustado"))
            assertTrue(extracted.contains("Resistencia"))
            assertTrue(extracted.contains("Vuelo mágico"))
            assertTrue(extracted.contains("Visión verdadera"))
            assertTrue(extracted.contains("Bendición temporal"))
            assertTrue(extracted.contains("Espada larga"))
            assertTrue(extracted.contains("Forma de lobo"))
            assertTrue(extracted.contains("Nim"))
            assertTrue(extracted.contains("Eco"))
            assertTrue(extracted.contains("Reacción de cobertura"))
            assertTrue(extracted.contains("Flechas de prueba"))
            assertTrue(extracted.contains("Munición"))
            assertTrue(extracted.contains("Almacenado"))
            assertTrue(extracted.contains("Herramienta llevada"))
        }

        Loader.loadPDF(classicPermanentPdf).use { permanentDocument ->
            Loader.loadPDF(classicPdf).use { currentDocument ->
                val permanentSpellPage = PDFRenderer(permanentDocument)
                    .renderImageWithDPI(2, 72f, ImageType.RGB)
                val currentSpellPage = PDFRenderer(currentDocument)
                    .renderImageWithDPI(2, 72f, ImageType.RGB)
                val permanentPixels = permanentSpellPage.getRGB(
                    0, 0, permanentSpellPage.width, permanentSpellPage.height,
                    null, 0, permanentSpellPage.width,
                )
                val currentPixels = currentSpellPage.getRGB(
                    0, 0, currentSpellPage.width, currentSpellPage.height,
                    null, 0, currentSpellPage.width,
                )
                assertTrue(
                    permanentPixels.contentEquals(currentPixels),
                    "Classic spell page must keep spent-slot markers empty for paper tracking.",
                )
            }
        }
    }


    @Test
    fun exportsCurrentSnapshotOperationalAndReferenceSemanticsInCustomV2() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val permanent = denseDraftAggregate().copy(
            sheet = denseDraftAggregate().sheet.copy(
                inventoryItems = emptyList(),
                traits = emptyList(),
                proficiencies = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                spells = emptyList(),
                generalNotes = "",
                noteCards = emptyList(),
            ),
        )
        val ammunition = inventory(
            index = 200,
            name = "Flechas de prueba",
            quantity = 20,
            weight = 0.05,
            location = "Carcaj",
            special = false,
            equipped = false,
            description = "Munición actual.",
        )
        val current = permanent.copy(
            sheet = permanent.sheet.copy(
                currentHp = 11,
                tempHp = 6,
                deathSaveSuccesses = 2,
                deathSaveFailures = 1,
                passivePerceptionAdjustment = 2,
                spellSlots = permanent.sheet.spellSlots.map { slot ->
                    if (slot.level == 1) slot.copy(spentSlots = 3.coerceAtMost(slot.totalSlots)) else slot
                },
                inventoryItems = listOf(ammunition),
                weaponMasteries = listOf(
                    CharacterWeaponMastery(
                        id = uuid("8c000000-0000-0000-0000-000000000001"),
                        weaponName = "Espada larga",
                        masteryName = "Empujar",
                        source = "Guerrero",
                        notes = "Sólo con esta arma.",
                    ),
                ),
                forms = listOf(
                    CharacterForm(
                        id = uuid("8d000000-0000-0000-0000-000000000001"),
                        name = "Forma de lobo",
                        source = "Rasgo",
                        challengeRatingText = "1/4",
                        armorClass = 13,
                        hitPoints = 18,
                        movement = "40 ft",
                        senses = "Percepción aguda",
                        actionSummary = "Mordisco",
                        notes = "Forma registrada.",
                    ),
                ),
                companions = listOf(
                    CharacterCompanion(
                        id = uuid("8e000000-0000-0000-0000-000000000001"),
                        name = "Nim",
                        kind = "Familiar",
                        source = "Conjuro",
                        armorClass = 12,
                        maxHp = 9,
                        currentHp = 7,
                        tempHp = 1,
                        speed = "30 ft",
                        abilitySummary = "Explorador",
                        sensesProficiencies = "Visión en la oscuridad",
                        traitsActions = "Ayudar",
                        notes = "Compañero actual.",
                    ),
                ),
            ),
            closure = permanent.closure.copy(
                progressMode = CharacterProgressMode.MILESTONE,
                milestoneProgress = "3 de 5 hitos",
                exhaustionLevel = 2,
                concentration = CharacterConcentration(
                    name = "Volar",
                    notes = "Concentración activa",
                ),
                conditions = listOf(
                    CharacterCondition(
                        id = uuid("8f000000-0000-0000-0000-000000000001"),
                        name = "Asustado",
                        source = "Efecto actual",
                        notes = "Hasta final del turno.",
                    ),
                ),
                defenses = listOf(
                    CharacterDefense(
                        id = uuid("90000000-0000-0000-0000-000000000001"),
                        type = CharacterDefenseType.RESISTANCE,
                        name = "Fuego",
                        source = "Objeto",
                    ),
                ),
                movements = listOf(
                    CharacterMovement(
                        id = uuid("91000000-0000-0000-0000-000000000001"),
                        type = CharacterMovementType.FLY,
                        name = "Vuelo mágico",
                        speedFeet = 60,
                        notes = "Mientras concentra.",
                    ),
                ),
                senses = listOf(
                    CharacterSense(
                        id = uuid("92000000-0000-0000-0000-000000000001"),
                        name = "Visión verdadera",
                        rangeFeet = 30,
                    ),
                ),
                temporaryEffects = listOf(
                    CharacterTemporaryEffect(
                        id = uuid("93000000-0000-0000-0000-000000000001"),
                        name = "Bendición temporal",
                        summary = "+1 a una prueba",
                        durationText = "10 minutos",
                        source = "Aliado",
                        notes = "Activo",
                        active = true,
                    ),
                ),
                inventoryUsage = listOf(
                    CharacterInventoryUsage(
                        itemId = ammunition.id,
                        kind = CharacterConsumableKind.AMMUNITION,
                        quickUseAmount = 2,
                        carryState = CharacterInventoryCarryState.STORED,
                    ),
                ),
            ),
        )

        val permanentPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = permanent),
        )
        val permanentPdf = File(proofDir, "custom-v2-permanent-slot-baseline.pdf")
        permanentPdf.outputStream().use { renderer.renderDraft(permanentPlan, it) }

        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
                stateSelection = PcSheetExportStateSelection.CURRENT_SNAPSHOT,
            ),
            sources = PcSheetExportSources(
                permanent = permanent,
                currentSnapshot = current,
            ),
        )
        val pdf = File(proofDir, "custom-v2-current-snapshot-semantics.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages >= 5)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("3 de 5 hitos"))
            assertTrue(extracted.contains("PG temporales"))
            assertTrue(extracted.contains("Salvaciones de muerte"))
            assertTrue(extracted.contains("Percepción pasiva"))
            assertTrue(extracted.contains("Agotamiento"))
            assertTrue(extracted.contains("Asustado"))
            assertTrue(extracted.contains("Resistencia"))
            assertTrue(extracted.contains("Vuelo mágico"))
            assertTrue(extracted.contains("Visión verdadera"))
            assertTrue(extracted.contains("Bendición temporal"))
            assertTrue(extracted.contains("Espada larga"))
            assertTrue(extracted.contains("Forma de lobo"))
            assertTrue(extracted.contains("Nim"))
            assertEquals(1, Regex("\\bFlechas de prueba\\b").findAll(extracted).count())
            assertFalse(extracted.contains("Estado: Munición"))
            assertFalse(extracted.contains("Uso rápido"))
            assertFalse(extracted.contains("Almacenado"))
        }

        Loader.loadPDF(permanentPdf).use { permanentDocument ->
            Loader.loadPDF(pdf).use { currentDocument ->
                val permanentSpellPage = PDFRenderer(permanentDocument)
                    .renderImageWithDPI(2, 72f, ImageType.RGB)
                val currentSpellPage = PDFRenderer(currentDocument)
                    .renderImageWithDPI(2, 72f, ImageType.RGB)
                val permanentPixels = permanentSpellPage.getRGB(
                    0, 0, permanentSpellPage.width, permanentSpellPage.height,
                    null, 0, permanentSpellPage.width,
                )
                val currentPixels = currentSpellPage.getRGB(
                    0, 0, currentSpellPage.width, currentSpellPage.height,
                    null, 0, currentSpellPage.width,
                )
                assertTrue(
                    permanentPixels.contentEquals(currentPixels),
                    "Custom v2 spell page must keep ESPACIOS GASTADOS blank for paper tracking.",
                )
            }
        }
    }

    @Test
    fun paginatesCustomV2CustomStatisticsWithoutDroppingAttributesOrSkills() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithCustomStatisticsOverflow()

        listOf(
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-custom-stats-overflow-attribute",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-custom-stats-overflow-ability",
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
                val extracted = PDFTextStripper().getText(document)
                assertTrue(extracted.contains("ATR"))
                assertTrue(extracted.contains("Vínculo 7-7"))
                val layers = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
                if (family == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE) {
                    assertTrue(document.numberOfPages >= 9)
                    assertTrue(layers.contains("V2X ATTR 2 - VALUES"))
                } else {
                    assertTrue(document.numberOfPages >= 6)
                    assertTrue(layers.contains("V2X ABILITY 2 - VALUES"))
                }
            }
        }
    }

    private fun PcSheetExportAggregate.withoutV1SemanticSupplements(): PcSheetExportAggregate =
        copy(
            sheet = sheet.copy(
                status = CharacterStatus.ACTIVE,
                tempHp = 0,
                deathSaveSuccesses = 0,
                deathSaveFailures = 0,
                passivePerceptionAdjustment = 0,
                combatEntries = sheet.combatEntries
                    .sortedBy { it.sortOrder }
                    .take(5)
                    .map { it.copy(type = CharacterCombatEntryType.ATTACK, notes = null) },
                weaponMasteries = emptyList(),
                forms = emptyList(),
                companions = emptyList(),
                inspiration = false,
                background = sheet.background.copy(
                    summary = "",
                    religionFaith = "",
                    personalityTraits = "",
                    ideals = "",
                    bonds = "",
                    flaws = "",
                    story = "",
                ),
                classes = sheet.classes.map { it.copy(subclassName = null) },
            ),
            closure = closure.copy(
                progressMode = CharacterProgressMode.EXPERIENCE,
                exhaustionLevel = 0,
                concentration = null,
                conditions = emptyList(),
                defenses = emptyList(),
                movements = emptyList(),
                senses = emptyList(),
                temporaryEffects = emptyList(),
            ),
            successor = successor.copy(
                combatDamage = emptyList(),
                spellcastingProfiles = successor.spellcastingProfiles.take(1),
                speciesIdentity = null,
                subraceIdentity = null,
                backgroundIdentity = null,
            ),
        )

    private fun denseDraftAggregateWithCustomStatisticsOverflow(): PcSheetExportAggregate {
        val base = denseDraftAggregate()
        val attributes = (1..7).map { index ->
            CharacterCustomAttribute(
                id = uuid("8a000000-0000-0000-0000-${index.toString().padStart(12, '0')}"),
                name = "Atributo $index",
                abbreviation = "ATR$index",
                score = 10 + index,
                savingThrowEnabled = true,
                savingThrowProficient = index % 2 == 0,
                notes = if (index == 1) {
                    (1..80).joinToString(" ") { "nota$it" }
                } else {
                    "Definición canónica del atributo $index."
                },
                sortOrder = index,
            )
        }

        val skills = mutableListOf<CharacterCustomSkill>()
        val links = mutableListOf<CharacterCustomSkillAbilityConfiguration>()
        var skillIndex = 1
        attributes.forEachIndexed { attrIndex, attribute ->
            repeat(7) { localIndex ->
                val skill = CharacterCustomSkill(
                    id = uuid("8b000000-0000-0000-0000-${skillIndex.toString().padStart(12, '0')}"),
                    name = "Vínculo ${attrIndex + 1}-${localIndex + 1}",
                    ability = CharacterAbility.WISDOM,
                    training = when (localIndex % 3) {
                        0 -> SkillTraining.NONE
                        1 -> SkillTraining.PROFICIENT
                        else -> SkillTraining.EXPERTISE
                    },
                    adjustment = localIndex,
                    source = "Stress PDF",
                    notes = null,
                    sortOrder = skillIndex,
                )
                skills += skill
                links += CharacterCustomSkillAbilityConfiguration(
                    customSkillId = skill.id,
                    ability = CharacterAbilityReference.custom(attribute.id),
                )
                skillIndex += 1
            }
        }

        return base.copy(
            sheet = base.sheet.copy(
                inventoryItems = emptyList(),
                traits = emptyList(),
                proficiencies = emptyList(),
                resources = emptyList(),
                classOptions = emptyList(),
                spells = emptyList(),
                generalNotes = "",
                noteCards = emptyList(),
            ),
            closure = base.closure.copy(customSkills = skills),
            successor = base.successor.copy(
                customAttributes = attributes,
                customSkillAbilities = links,
                customMarkers = emptyList(),
                preferences = base.successor.preferences.copy(valuablesText = ""),
            ),
        )
    }

    @Test
    fun paginatesCustomV2ResourcesAndOptionsWithoutDroppingCanonicalRows() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val base = denseDraftAggregate()
        val resources = (1..12).map { index ->
            CharacterResource(
                id = uuid("87000000-0000-0000-0000-${index.toString().padStart(12, '0')}"),
                name = "Recurso canónico $index",
                currentValue = index,
                maxValue = index + 2,
                recovery = if (index % 2 == 0) "Descanso largo" else null,
                source = "Fuente $index",
                notes = "Nota de recurso $index",
                sortOrder = index,
            )
        }
        val options = (1..20).map { index ->
            CharacterClassOption(
                id = uuid("88000000-0000-0000-0000-${index.toString().padStart(12, '0')}"),
                kind = CharacterClassOptionKind.TECHNIQUE,
                name = "Opción canónica $index",
                source = "Clase $index",
                costText = "Coste $index",
                effectSummary = "Efecto $index",
                notes = "Nota $index",
                active = index % 2 == 0,
                sortOrder = index,
            )
        }
        val aggregate = base.copy(
            sheet = base.sheet.copy(
                inventoryItems = emptyList(),
                traits = emptyList(),
                proficiencies = emptyList(),
                resources = resources,
                classOptions = options,
                spells = emptyList(),
                generalNotes = "",
                noteCards = emptyList(),
            ),
            successor = base.successor.copy(
                customMarkers = listOf(
                    CharacterCustomMarker(
                        id = uuid("89000000-0000-0000-0000-000000000001"),
                        name = "Estrés",
                        valueKind = CharacterTrackableValueKind.BINARY,
                        currentValue = 1,
                        recovery = CharacterTrackableRecovery(
                            cadence = CharacterRecoveryCadence.SHORT_REST,
                            amountMode = CharacterRecoveryAmountMode.TO_MAX,
                        ),
                        notes = "Marcador sucesor",
                        sortOrder = 13,
                    ),
                ),
                preferences = base.successor.preferences.copy(valuablesText = ""),
            ),
        )

        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(permanent = aggregate),
        )
        val pdf = File(proofDir, "custom-v2-resources-pagination-audit.pdf")
        pdf.outputStream().use { renderer.renderDraft(plan, it) }

        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages >= 6)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Recurso canónico 12"))
            assertTrue(extracted.contains("Opción canónica 20"))
            assertTrue(extracted.contains("Estrés"))
            assertTrue(extracted.contains("Marcador sucesor"))
            val layerNames = document.documentCatalog.ocProperties?.getGroupNames()?.toList().orEmpty()
            assertTrue(layerNames.contains("V2X RESOURCES - VALUES"))
            assertTrue(layerNames.contains("V2X RESOURCES 2 - VALUES"))
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

        val marker = CharacterCustomMarker(
            id = uuid("86000000-0000-0000-0000-000000000001"),
            name = "Puntos de destino",
            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
            currentValue = 2,
            maxValue = 5,
            recovery = CharacterTrackableRecovery(
                cadence = CharacterRecoveryCadence.MANUAL,
                amountMode = CharacterRecoveryAmountMode.FIXED,
                fixedAmount = 1,
            ),
            notes = "Sólo se recupera al cerrar un arco narrativo.",
            sortOrder = 3,
        )

        return base.copy(
            sheet = base.sheet.copy(
                traits = traits,
                proficiencies = proficiencies,
                resources = resources,
                classOptions = options,
            ),
            successor = base.successor.copy(
                customMarkers = listOf(marker),
            ),
        )
    }

    @Test
    fun rendersOwnerFacingAppModifiedAndCombinedCustomStatisticsAcrossAllFamilies() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithCustomStatistics()
        val families = listOf(
            PcSheetVisualFamily.CLASSIC_DND_STYLE to "fantasy",
            PcSheetVisualFamily.CUSTOM_V1 to "custom-v1",
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-attribute",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-ability",
        )
        val modes = listOf(
            PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET to "modified",
            PcSheetCustomStatisticsPresentation.MODIFIED_SHEET_AND_COMPLETE_EXTENDED_PAGE to "modified-plus-extended",
        )

        families.forEach { (family, familySlug) ->
            modes.forEach { (mode, modeSlug) ->
                val plan = PcSheetPdfExportPlanner.plan(
                    request = PcSheetPdfExportRequest(
                        visualFamily = family,
                        stateSelection = PcSheetExportStateSelection.PERMANENT,
                        customStatisticsPresentation = mode,
                    ),
                    sources = PcSheetExportSources(permanent = aggregate),
                )
                assertEquals(PcSheetBaseLayoutMode.APP_MODIFIED, plan.baseLayoutMode)
                if (mode == PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET) {
                    assertTrue(plan.mandatoryExtendedPages.isEmpty())
                } else {
                    assertEquals(
                        listOf(PcSheetExtendedPageKind.CUSTOM_STATISTICS),
                        plan.mandatoryExtendedPages,
                    )
                }

                val pdf = File(proofDir, "owner-review-app-modified-$familySlug-$modeSlug.pdf")
                pdf.outputStream().use { renderer.renderDraft(plan, it) }

                Loader.loadPDF(pdf).use { document ->
                    assertTrue(document.numberOfPages > plan.basePages.size)
                    val firstPageText = PDFTextStripper().apply {
                        startPage = 1
                        endPage = 1
                    }.getText(document)
                    val modifiedPageText = PDFTextStripper().apply {
                        startPage = 2
                        endPage = 2
                    }.getText(document)
                    val allText = PDFTextStripper().getText(document)

                    assertFalse(firstPageText.contains("HOJA MODIFICADA SIGUIENTE"))
                    assertTrue(modifiedPageText.contains("HOJA MODIFICADA"))
                    assertTrue(
                        modifiedPageText.contains("Estadísticas Personalizadas", ignoreCase = true) ||
                            modifiedPageText.contains("ESTADÍSTICAS PERSONALIZADAS"),
                    )
                    assertTrue(allText.contains("Honor", ignoreCase = true))
                    assertTrue(allText.contains("Etiqueta"))
                    assertTrue(allText.contains("Criptografía"))

                    val statsPages = (1..document.numberOfPages).count { pageNumber ->
                        val pageText = PDFTextStripper().apply {
                            startPage = pageNumber
                            endPage = pageNumber
                        }.getText(document)
                        pageText.contains("Estadísticas Personalizadas", ignoreCase = true) ||
                            pageText.contains("ESTADÍSTICAS PERSONALIZADAS")
                    }
                    if (mode == PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET) {
                        assertTrue(statsPages >= 1)
                    } else {
                        assertTrue(statsPages >= 2)
                        assertTrue(allText.contains("EXTENSIÓN:"))
                        assertTrue(allText.contains("ESTADÍSTICAS"))
                    }

                    (0..minOf(1, document.numberOfPages - 1)).forEach { pageIndex ->
                        val image = PDFRenderer(document).renderImageWithDPI(pageIndex, 180f, ImageType.RGB)
                        assertTrue(
                            ImageIO.write(
                                image,
                                "png",
                                File(
                                    proofDir,
                                    "owner-review-app-modified-$familySlug-$modeSlug-page-${pageIndex + 1}.png",
                                ),
                            ),
                        )
                    }
                }
                assertTrue(pdf.length() > 20_000L)
            }
        }
    }

    @Test
    fun marksRealExtendedContinuationsOnTheirOriginatingBasePages() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithOverflowContinuations()
        val families = listOf(
            PcSheetVisualFamily.CLASSIC_DND_STYLE to "fantasy",
            PcSheetVisualFamily.CUSTOM_V1 to "custom-v1",
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-attribute",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-ability",
        )

        families.forEach { (family, slug) ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(permanent = aggregate),
            )
            val pdf = File(proofDir, "owner-review-continuation-cues-$slug.pdf")
            pdf.outputStream().use { renderer.renderDraft(plan, it) }

            Loader.loadPDF(pdf).use { document ->
                assertTrue(document.numberOfPages > plan.basePages.size)
                val baseText = (1..plan.basePages.size).joinToString("\n") { pageNumber ->
                    PDFTextStripper().apply {
                        startPage = pageNumber
                        endPage = pageNumber
                    }.getText(document)
                }
                assertTrue(baseText.contains("EXTENSIÓN:"))
                assertTrue(baseText.contains("CONJUROS"))
                if (plan.basePages.any { it.role == PcSheetBasePageRole.NOTES }) {
                    assertTrue(baseText.contains("NOTAS"))
                }
                assertFalse(baseText.contains(" - CONTINÚA EN EXTENSIÓN"))

                repeat(plan.basePages.size) { pageIndex ->
                    footerCueY(document, pageIndex)?.let { cueY ->
                        assertTrue(
                            cueY > 770f,
                            "Continuation cue must remain in the bottom margin, y=$cueY",
                        )
                    }
                    val image = PDFRenderer(document).renderImageWithDPI(pageIndex, 160f, ImageType.RGB)
                    assertTrue(
                        ImageIO.write(
                            image,
                            "png",
                            File(proofDir, "owner-review-continuation-cues-$slug-base-${pageIndex + 1}.png"),
                        ),
                    )
                }
            }
        }
    }

    @Test
    fun auditsOwnerFacingXYLayerOrderAndTerminologyBeforeProofPromotion() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetWholeDraftRenderer()
        val aggregate = denseDraftAggregateWithOverflowContinuations()
        val report = mutableListOf(
            "family\tlabel\tpage\tcenter_x\tmin_y\tmax_y\texpected_center_x\texpected_y_min\texpected_y_max\tstatus",
        )

        val families = listOf(
            PcSheetVisualFamily.CLASSIC_DND_STYLE to "fantasy",
            PcSheetVisualFamily.CUSTOM_V1 to "custom-v1",
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-attribute",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-ability",
        )

        families.forEach { (family, slug) ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(permanent = aggregate),
            )
            val pdf = File(proofDir, "preprint-xy-audit-$slug.pdf")
            pdf.outputStream().use { renderer.renderDraft(plan, it) }

            Loader.loadPDF(pdf).use { document ->
                val allText = PDFTextStripper().getText(document)
                assertFalse(
                    allText.contains("Especie", ignoreCase = true),
                    "TERM-001 regression in $slug: user-facing output must use Raza, never Especie.",
                )

                val layerNames = document.documentCatalog.ocProperties
                    ?.getGroupNames()
                    ?.toList()
                    .orEmpty()
                val semanticPrefixes = layerNames
                    .mapNotNull { name ->
                        val role = listOf("STRUCTURE", "CLEANUP", "LABELS", "VALUES", "MARKERS")
                            .firstOrNull { name.endsWith(" - $it") }
                        role?.let { name.removeSuffix(" - $it") }
                    }
                    .distinct()
                    .filter { it.startsWith("V1X ") || it.startsWith("V2X ") }
                semanticPrefixes.forEach { prefix ->
                    assertSemanticLayerOrder(layerNames, prefix)
                }

                fun audit(
                    label: String,
                    expectedCenterX: Float,
                    expectedYMin: Float,
                    expectedYMax: Float,
                    centerTolerance: Float = 4f,
                    yTolerance: Float = 3f,
                ) {
                    val located = locateTextBounds(
                        document,
                        label,
                        expectedYCenter = (expectedYMin + expectedYMax) / 2f,
                    )
                    val centerX = (located.bounds.minX + located.bounds.maxX) / 2f
                    val xOk = kotlin.math.abs(centerX - expectedCenterX) <= centerTolerance
                    val yOk = located.bounds.minY >= expectedYMin - yTolerance &&
                        located.bounds.maxY <= expectedYMax + yTolerance
                    report += listOf(
                        slug,
                        label,
                        (located.pageIndex + 1).toString(),
                        "%.2f".format(java.util.Locale.ROOT, centerX),
                        "%.2f".format(java.util.Locale.ROOT, located.bounds.minY),
                        "%.2f".format(java.util.Locale.ROOT, located.bounds.maxY),
                        "%.2f".format(java.util.Locale.ROOT, expectedCenterX),
                        "%.2f".format(java.util.Locale.ROOT, expectedYMin),
                        "%.2f".format(java.util.Locale.ROOT, expectedYMax),
                        if (xOk && yOk) "PASS" else "FAIL",
                    ).joinToString("\t")
                    assertTrue(
                        xOk && yOk,
                        "XY-001 $slug '$label' measured centerX=$centerX y=" +
                            "${located.bounds.minY}..${located.bounds.maxY}; expected centerX=" +
                            "$expectedCenterX +/- $centerTolerance, y=$expectedYMin..$expectedYMax.",
                    )
                }

                when (family) {
                    PcSheetVisualFamily.CLASSIC_DND_STYLE -> {
                        audit("HISTORIA Y PERSONALIDAD", 137f, 434f, 458f)
                        audit("IDIOMAS", 342f, 578f, 602f)
                        audit("ALIADOS Y TESORO", 510f, 578f, 602f)
                        audit("RASGOS DE RAZA / TRASFONDO / OTROS", 450f, 110f, 136f)
                        // The standalone Fantasy Notes page is now content-aware. Keep its XY
                        // contract when present, but do not require a redundant page to exist.
                        if (allText.contains("NOTAS DE CAMPAÑA")) {
                            audit("NOTAS DE CAMPAÑA", 204f, 110f, 136f)
                            audit("REFERENCIAS Y RECORDATORIOS", 493f, 416f, 442f)
                        }
                    }

                    PcSheetVisualFamily.CUSTOM_V1 -> {
                        // Frozen source page places this heading at center x ~= 306 and y ~= 467..488.
                        audit("Equipo Especial", 306f, 466f, 490f, centerTolerance = 3f)
                    }

                    PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
                    PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
                    -> {
                        audit("RASGOS Y ATRIBUTOS", 362f, 26f, 56f)
                        audit("CLASE / DOTES", 152.5f, 97f, 119f)
                        audit("RAZA / TRASFONDO / OTROS", 452.5f, 97f, 119f)
                        audit("EQUIPO ESPECIAL", 306f, 487f, 511f)
                    }
                }
            }
        }

        File(proofDir, "pc-sheet-preprint-xy-audit.tsv").writeText(
            report.joinToString(System.lineSeparator()) + System.lineSeparator(),
        )
    }

    private fun assertSemanticLayerOrder(layerNames: List<String>, prefix: String) {
        val roles = listOf("STRUCTURE", "CLEANUP", "LABELS", "VALUES", "MARKERS")
        val indices = roles.map { role ->
            layerNames.indexOf("$prefix - $role").also { index ->
                assertTrue(index >= 0, "ARCH-001 missing semantic layer $prefix - $role")
            }
        }
        assertEquals(
            indices.sorted(),
            indices,
            "ARCH-001 semantic layer order drift for $prefix: $indices",
        )
    }

    private fun locateTextBounds(
        document: PDDocument,
        label: String,
        expectedYCenter: Float,
    ): LocatedTextBounds {
        val needle = normalizeAuditLabel(label)
        val matches = mutableListOf<LocatedTextBounds>()
        repeat(document.numberOfPages) { pageIndex ->
            object : PDFTextStripper() {
                init {
                    startPage = pageIndex + 1
                    endPage = pageIndex + 1
                    sortByPosition = true
                }

                override fun writeString(text: String, textPositions: MutableList<TextPosition>) {
                    if (
                        needle.isNotEmpty() &&
                        normalizeAuditLabel(text).contains(needle) &&
                        textPositions.isNotEmpty()
                    ) {
                        matches += LocatedTextBounds(
                            pageIndex = pageIndex,
                            bounds = TextBounds(
                                minX = textPositions.minOf { it.xDirAdj },
                                minY = textPositions.minOf { it.yDirAdj - it.heightDir },
                                maxX = textPositions.maxOf { it.xDirAdj + it.widthDirAdj },
                                maxY = textPositions.maxOf { it.yDirAdj },
                            ),
                        )
                    }
                    super.writeString(text, textPositions)
                }
            }.getText(document)
        }
        if (matches.isEmpty()) error("XY-001 label not found in rendered PDF: $label")
        return matches.minBy { located ->
            val centerY = (located.bounds.minY + located.bounds.maxY) / 2f
            kotlin.math.abs(centerY - expectedYCenter)
        }
    }

    private fun normalizeAuditLabel(value: String): String =
        value.uppercase()
            .replace(Regex("[^A-ZÁÉÍÓÚÜÑ0-9]+"), "")

    private data class TextBounds(
        val minX: Float,
        val minY: Float,
        val maxX: Float,
        val maxY: Float,
    )

    private data class LocatedTextBounds(
        val pageIndex: Int,
        val bounds: TextBounds,
    )

    private fun footerCueY(document: PDDocument, pageIndex: Int): Float? {
        var result: Float? = null
        object : PDFTextStripper() {
            init {
                startPage = pageIndex + 1
                endPage = pageIndex + 1
                sortByPosition = true
            }

            override fun writeString(text: String, textPositions: MutableList<TextPosition>) {
                if ("EXTENSIÓN:" in text && textPositions.isNotEmpty()) {
                    result = textPositions.minOf { it.yDirAdj }
                }
                super.writeString(text, textPositions)
            }
        }.getText(document)
        return result
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
