package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSavingThrow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
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
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper

class DesktopPcSheetTemplateProofRendererTest {
    @Test
    fun generatesReviewableCustomMainPageProofsFromAuthoritativeOwnerTemplates() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val renderer = DesktopPcSheetTemplateProofRenderer()
        val aggregate = representativeAggregate()

        val families = listOf(
            PcSheetVisualFamily.CUSTOM_V1 to "custom-v1-main-proof",
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE to "custom-v2-per-attribute-main-proof",
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY to "custom-v2-per-ability-main-proof",
        )

        families.forEach { (family, fileStem) ->
            val plan = PcSheetPdfExportPlanner.plan(
                request = PcSheetPdfExportRequest(
                    visualFamily = family,
                    stateSelection = PcSheetExportStateSelection.PERMANENT,
                ),
                sources = PcSheetExportSources(permanent = aggregate),
            )

            val pdf = File(proofDir, "$fileStem.pdf")
            pdf.outputStream().use { renderer.renderMainPage(plan, it) }

            Loader.loadPDF(pdf).use { document ->
                assertEquals(1, document.numberOfPages)
                val extracted = PDFTextStripper().getText(document)
                assertTrue(extracted.contains("Aster Vale"))
                assertTrue(extracted.contains("Mago 5"))
                assertTrue(extracted.contains("Elfo Alto"))

                val image = PDFRenderer(document).renderImageWithDPI(0, 144f, ImageType.RGB)
                val png = File(proofDir, "$fileStem.png")
                assertTrue(ImageIO.write(image, "png", png))
                assertTrue(png.length() > 0L)
            }

            assertTrue(pdf.length() > 0L)
        }
    }

    private fun representativeAggregate(): PcSheetExportAggregate {
        val wizardId = uuid("10000000-0000-0000-0000-000000000001")
        val rogueId = uuid("10000000-0000-0000-0000-000000000002")
        val spellSourceId = uuid("20000000-0000-0000-0000-000000000001")

        val skills = SkillKey.entries.map { key ->
            val training = when (key) {
                SkillKey.ARCANA,
                SkillKey.INVESTIGATION,
                SkillKey.HISTORY,
                SkillKey.ACROBATICS,
                SkillKey.STEALTH,
                -> SkillTraining.PROFICIENT

                SkillKey.SLEIGHT_OF_HAND -> SkillTraining.EXPERTISE
                else -> SkillTraining.NONE
            }
            CharacterSkill(key = key, adjustment = 0, training = training)
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
                name = "Bastón",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 3,
                damageEffect = "1d6",
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
                name = "Daga",
                type = CharacterCombatEntryType.ATTACK,
                attackModifier = 6,
                damageEffect = "1d4+3 perforante",
                rangeText = "20/60 ft",
                notes = null,
                sortOrder = 2,
            ),
        )

        val traitNames = listOf(
            "Visión en la oscuridad",
            "Trance",
            "Recuperación Arcana",
            "Ataque furtivo 1d6",
            "Acción astuta",
            "Erudito arcano",
        )
        val traits = traitNames.mapIndexed { index, name ->
            CharacterTrait(
                id = uuid("40000000-0000-0000-0000-00000000000" + (index + 1)),
                name = name,
                source = "Prueba PDF",
                type = if (index < 2) CharacterTraitType.SPECIES_RACE else CharacterTraitType.CLASS,
                description = "Dato representativo para validar la ubicación visual.",
                notes = null,
                maxUses = null,
                spentUses = 0,
                recovery = null,
                activation = CharacterActivationType.PASSIVE,
                sortOrder = index,
            )
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
            tempHp = 0,
            initiativeAdjustment = 1,
            speed = 30,
            proficiencyBonus = 3,
            savingThrows = saves,
            passivePerceptionAdjustment = 0,
            spellSaveDc = null,
            classes = listOf(
                CharacterClassLevel(
                    id = wizardId,
                    name = "Mago",
                    level = 5,
                    hitDieSides = 6,
                    hitDiceRemaining = 4,
                    sortOrder = 0,
                ),
                CharacterClassLevel(
                    id = rogueId,
                    name = "Pícaro",
                    level = 2,
                    hitDieSides = 8,
                    hitDiceRemaining = 1,
                    sortOrder = 1,
                ),
            ),
            skills = skills,
            proficiencyBonusAdjustment = 0,
            spellAttackModifier = null,
            spellSlots = listOf(
                CharacterSpellSlot(level = 1, totalSlots = 4, spentSlots = 1),
                CharacterSpellSlot(level = 2, totalSlots = 3, spentSlots = 2),
                CharacterSpellSlot(level = 3, totalSlots = 3, spentSlots = 0),
                CharacterSpellSlot(level = 4, totalSlots = 1, spentSlots = 1),
            ),
            combatEntries = combat,
            spellcasterEnabled = true,
            background = CharacterBackground(
                name = "Sabio",
                race = "Elfo Alto",
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
        )
        return PcSheetExportAggregate(sheet = sheet, closure = closure, successor = successor)
    }

    private fun uuid(raw: String): Uuid = Uuid.parse(raw)
}
