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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
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
                assertTrue(extracted.contains("Aster Vale"))
                assertTrue(extracted.contains("Mochila de expedición"))
                assertTrue(extracted.contains("Curiosidad académica"))
                assertTrue(extracted.contains("Escudo"))

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
        )

        val traits = listOf(
            "Visión en la oscuridad" to "Ves en luz tenue y oscuridad como corresponde a tu linaje élfico.",
            "Trance" to "Cuatro horas de meditación sustituyen el descanso ordinario.",
            "Recuperación Arcana" to "Una vez al día recuperas espacios de conjuro después de un descanso corto.",
            "Ataque furtivo 1d6" to "Una vez por turno añades daño cuando cumples sus condiciones.",
            "Acción astuta" to "Puedes usar acción adicional para determinadas maniobras.",
            "Erudito arcano" to "Tu formación facilita investigar fenómenos y tradiciones mágicas.",
        ).mapIndexed { index, (name, description) ->
            CharacterTrait(
                id = uuid("40000000-0000-0000-0000-00000000000${index + 1}"),
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
        )

        val currencies = listOf(
            CharacterCurrency("pt", "Piezas de platino", 4, 0, true),
            CharacterCurrency("po", "Piezas de oro", 137, 1, true),
            CharacterCurrency("pe", "Piezas de electrón", 2, 2, true),
            CharacterCurrency("pp", "Piezas de plata", 48, 3, true),
            CharacterCurrency("pc", "Piezas de cobre", 19, 4, true),
        )

        val spells = listOf(
            spell(1, "Luz", 0, spellSourceId, true),
            spell(2, "Mano de mago", 0, spellSourceId, true),
            spell(3, "Rayo de fuego", 0, spellSourceId, true),
            spell(4, "Escudo", 1, spellSourceId, true),
            spell(5, "Misil mágico", 1, spellSourceId, true),
            spell(6, "Detectar magia", 1, spellSourceId, false),
            spell(7, "Imagen múltiple", 2, spellSourceId, true),
            spell(8, "Paso brumoso", 2, spellSourceId, true),
            spell(9, "Contrahechizo", 3, spellSourceId, true),
            spell(10, "Bola de fuego", 3, spellSourceId, true),
            spell(11, "Puerta dimensional", 4, spellSourceId, true),
            spell(12, "Invisibilidad superior", 4, spellSourceId, false),
            spell(13, "Teletransportar", 7, spellSourceId, false),
        )

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
                CharacterSpellSlot(3, 3, 0),
                CharacterSpellSlot(4, 1, 1),
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
            generalNotes = "Contactar a Maestra Elenya al regresar a Liria. No entregar el mapa original a terceros. Preparar tinta resistente al agua antes de entrar en las ruinas.",
            noteCards = listOf(
                CharacterNote(uuid("60000000-0000-0000-0000-000000000001"), "Pista", "El sello azul aparece también en las monedas halladas en la torre.", 0),
                CharacterNote(uuid("60000000-0000-0000-0000-000000000002"), "Pendiente", "Comparar el alfabeto de la puerta norte con las notas del profesor Vael.", 1),
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
                valuablesText = "Broche élfico antiguo (75 po); gema lunar sin tasar; tres láminas de plata grabadas; fragmento de mosaico con sello azul.",
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
