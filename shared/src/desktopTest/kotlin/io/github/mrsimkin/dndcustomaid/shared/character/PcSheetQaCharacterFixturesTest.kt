package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetQaCharacterFixturesTest {
    private val fixtureNames = listOf(
        "01_aldren_vale_srd5_1_champion_fighter.json",
        "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
        "03_mara_siete_umbrales_custom_extended.json",
    )

    @Test
    fun allQaFixturesDecodeAndCanRestoreAsIndependentCopies() {
        fixtureNames.forEachIndexed { index, name ->
            val document = fixture(name)
            assertEquals(CHARACTER_BACKUP_FORMAT, document.format)
            assertEquals(CHARACTER_BACKUP_VERSION, document.version)
            assertEquals(document, assertIs<CharacterBackupDecodeResult.Success>(
                CharacterBackupCodec.decode(CharacterBackupCodec.encode(document)),
            ).document)

            val destination = Uuid.parse("7b000000-0000-4000-8000-000000000001")
            val target = Uuid.parse(
                "7b000000-0000-4000-8000-${(index + 101).toString().padStart(12, '0')}",
            )
            val imported = prepareCharacterBackupImport(
                document = document,
                destinationCampaignId = destination,
                targetCharacterId = target,
            )
            assertEquals(target, imported.character.id)
            assertEquals(destination, imported.character.campaignId)
            assertNotEquals(document.character.id, imported.character.id)
        }
    }

    @Test
    fun aldrenCanApplyAsAuthoritativeHostedCurrentState() {
        assertCanApplyAsAuthoritativeHostedCurrentState(fixtureNames[0])
    }

    @Test
    fun ilyraCanApplyAsAuthoritativeHostedCurrentState() {
        assertCanApplyAsAuthoritativeHostedCurrentState(fixtureNames[1])
    }

    @Test
    fun maraCanApplyAsAuthoritativeHostedCurrentState() {
        assertCanApplyAsAuthoritativeHostedCurrentState(fixtureNames[2])
    }

    @Test
    fun aldrenIsTheAuditedSrd51Level5ChampionBaseline() {
        val document = fixture(fixtureNames[0])
        val sheet = document.character
        val clazz = sheet.classes.single()

        assertEquals("Aldren Vale", sheet.name)
        assertEquals(CharacterRulesFamily.DND_5E, clazz.rulesFamily)
        assertEquals(5, clazz.level)
        assertEquals("Champion", clazz.subclassName)
        assertEquals(18, sheet.strength)
        assertEquals(44, sheet.maxHp)
        assertEquals(18, sheet.armorClass)
        assertEquals(3, sheet.finalProficiencyBonus)
        assertTrue(sheet.savingThrow(CharacterAbility.STRENGTH).proficient)
        assertTrue(sheet.savingThrow(CharacterAbility.CONSTITUTION).proficient)
        assertEquals(SkillTraining.PROFICIENT, sheet.skill(SkillKey.ATHLETICS).training)
        assertEquals(SkillTraining.PROFICIENT, sheet.skill(SkillKey.PERCEPTION).training)
        assertEquals(SkillTraining.PROFICIENT, sheet.skill(SkillKey.INSIGHT).training)
        assertEquals(SkillTraining.PROFICIENT, sheet.skill(SkillKey.RELIGION).training)
        assertTrue(sheet.traits.any { it.name == "Improved Critical" })
        assertTrue(sheet.traits.any { it.name == "Extra Attack" })
        assertFalse(sheet.traits.any { it.name.contains("Remarkable Athlete", ignoreCase = true) })
        assertTrue(sheet.spells.isEmpty())
        assertFalse(sheet.spellcasterEnabled)
        val longsword = assertNotNull(sheet.combatEntries.firstOrNull { it.name == "Espada larga" })
        assertEquals(7, longsword.attackModifier)
        assertTrue(longsword.damageEffect.contains("+ 6"))
    }

    @Test
    fun ilyraIsTheAuditedSrd521Level5EvokerBaseline() {
        val document = fixture(fixtureNames[1])
        val sheet = document.character
        val clazz = sheet.classes.single()
        val wizardSource = assertNotNull(sheet.spellcastingSources.firstOrNull { it.linkedClassId == clazz.id })
        val elfSource = assertNotNull(sheet.spellcastingSources.firstOrNull { it.name.startsWith("High Elf") })
        val sageSource = assertNotNull(sheet.spellcastingSources.firstOrNull { it.name.startsWith("Sage") })

        assertEquals("Ilyra Quill", sheet.name)
        assertEquals(CharacterRulesFamily.DND_5_5E, clazz.rulesFamily)
        assertEquals(5, clazz.level)
        assertEquals("Evoker", clazz.subclassName)
        assertEquals(18, sheet.intelligence)
        assertEquals(14, sheet.dexterity)
        assertEquals(14, sheet.constitution)
        assertEquals(32, sheet.maxHp)
        assertEquals(12, sheet.armorClass)
        assertEquals(3, sheet.finalProficiencyBonus)
        assertEquals(15, sheet.spellSaveDc)
        assertEquals(7, sheet.spellAttackModifier)
        assertEquals(
            listOf(1 to 4, 2 to 3, 3 to 2),
            sheet.spellSlots.map { it.level to it.totalSlots },
        )
        assertEquals(SkillTraining.EXPERTISE, sheet.skill(SkillKey.ARCANA).training)

        val wizardCantrips = sheet.spells.filter { spell ->
            spell.level == 0 && spell.sourceAssociations.any { it.sourceId == wizardSource.id }
        }
        assertEquals(4, wizardCantrips.size)

        val wizardPrepared = sheet.spells.filter { spell ->
            spell.level > 0 && spell.sourceAssociations.any {
                it.sourceId == wizardSource.id && it.prepared
            }
        }
        assertEquals(9, wizardPrepared.size)

        assertEquals(
            setOf("Detect Magic", "Misty Step"),
            sheet.spells.filter { spell ->
                spell.level > 0 && spell.sourceAssociations.any {
                    it.sourceId == elfSource.id && it.prepared
                }
            }.map { it.name }.toSet(),
        )
        assertEquals(
            setOf("Light", "Message", "Comprehend Languages"),
            sheet.spells.filter { spell ->
                spell.sourceAssociations.any { it.sourceId == sageSource.id && it.prepared }
            }.map { it.name }.toSet(),
        )
        assertTrue(sheet.traits.any { it.name == "Evocation Savant" })
        assertTrue(sheet.traits.any { it.name == "Potent Cantrip" })
        assertTrue(sheet.traits.any { it.name == "Memorize Spell" })
        assertFalse(sheet.traits.any { it.name.contains("Sculpt Spells", ignoreCase = true) })
    }

    @Test
    fun maraIsAHighVolumeCustomFixtureThatMandatesCustomStatisticsExtendedPage() {
        val document = fixture(fixtureNames[2])
        val sheet = document.character
        val closure = document.closureState
        val successor = document.successorState

        assertEquals("Mara de los Siete Umbrales", sheet.name)
        assertEquals(CharacterRulesFamily.CUSTOM, sheet.classes.single().rulesFamily)
        assertTrue(successor.customAttributes.size >= 4)
        assertTrue(closure.customSkills.size >= 10)
        assertTrue(sheet.traits.size >= 26)
        assertTrue(sheet.resources.size >= 10)
        assertTrue(sheet.classOptions.size >= 8)
        assertTrue(sheet.inventoryItems.size >= 34)
        assertTrue(sheet.spells.size >= 24)
        assertTrue(sheet.noteCards.size >= 9)
        assertTrue(successor.customMarkers.size >= 7)

        val renderPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
                customStatisticsPresentation = PcSheetCustomStatisticsPresentation.EXTENDED_PAGE,
                includeSpellDescriptions = true,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = sheet,
                    closure = closure,
                    successor = successor,
                ),
            ),
        )
        assertTrue(PcSheetExtendedPageKind.CUSTOM_STATISTICS in renderPlan.mandatoryExtendedPages)
        assertNotNull(renderPlan.snapshot.spellbook)
        assertEquals(sheet.spells.size, renderPlan.snapshot.spellbook?.entries?.size)
        assertTrue(renderPlan.overflowRoutes.map { it.extensionKind }.containsAll(
            listOf(
                PcSheetExtendedPageKind.TRAITS_AND_FEATURES,
                PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS,
                PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT,
                PcSheetExtendedPageKind.SPELLS,
                PcSheetExtendedPageKind.NOTES,
            ),
        ))
    }

    private fun assertCanApplyAsAuthoritativeHostedCurrentState(name: String) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val backups = CharacterBackupRepository(database)
            val document = fixture(name)

            campaigns.upsertCampaign(
                id = document.character.campaignId,
                rawName = "QA - PC Sheet PDF Runtime",
            )

            val applied = backups.applyCurrentState(document)
            assertEquals(document.character.id, applied.character.id)
            assertEquals(document.character.campaignId, applied.character.campaignId)
            assertEquals(document.character.name, applied.character.name)

            val exported = backups.exportCharacter(
                characterId = document.character.id,
                exportedAtEpochSeconds = document.exportedAtEpochSeconds,
            )
            assertEquals(document.character.id, exported.character.id)
            assertEquals(document.character.campaignId, exported.character.campaignId)
            assertEquals(document.character.name, exported.character.name)
            assertIs<CharacterBackupDecodeResult.Success>(
                CharacterBackupCodec.decode(CharacterBackupCodec.encode(exported)),
            )
        } finally {
            driver.close()
        }
    }

    private fun fixture(name: String): CharacterBackupDocument {
        val raw = File(fixtureDirectory(), name).readText()
        return assertIs<CharacterBackupDecodeResult.Success>(CharacterBackupCodec.decode(raw)).document
    }

    private fun fixtureDirectory(): File {
        val start = File(System.getProperty("user.dir")).absoluteFile
        return generateSequence(start) { it.parentFile }
            .map { File(it, "qa/pc-sheet/fixtures") }
            .firstOrNull { it.isDirectory }
            ?: error("Could not locate qa/pc-sheet/fixtures from ${start.path}")
    }
}
