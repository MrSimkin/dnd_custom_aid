package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterPhase4FinalRegressionTest {
    @Test
    fun legacyAndPhase4DomainsPersistTogetherAcrossDiskReopen() {
        val file = File.createTempFile("dnd-custom-aid-phase4-final", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        var characterId: Uuid? = null

        val classId = Uuid.random()
        val sourceId = Uuid.random()
        val customSourceId = Uuid.random()
        val spellId = Uuid.random()
        val traitId = Uuid.random()
        val noteId = Uuid.random()
        val combatId = Uuid.random()
        val itemId = Uuid.random()

        try {
            JdbcSqliteDriver(jdbcUrl).use { driver ->
                AppDatabase.Schema.create(driver)
                val database = AppDatabase(driver)
                val campaigns = CampaignRepository(database)
                val characters = CharacterRepository(database)
                val campaign = campaigns.createCampaign("Final Phase 4")
                val created = characters.createCharacter(campaign.id, "Vanya integral")
                characterId = created.id

                val savingThrows = CharacterAbility.entries.map { ability ->
                    CharacterSavingThrow(
                        ability = ability,
                        proficient = ability == CharacterAbility.INTELLIGENCE || ability == CharacterAbility.WISDOM,
                        adjustment = if (ability == CharacterAbility.WISDOM) 1 else 0,
                    )
                }
                val skills = SkillKey.entries.map { key ->
                    CharacterSkill(
                        key = key,
                        adjustment = if (key == SkillKey.ARCANA) 2 else 0,
                        training = when (key) {
                            SkillKey.ARCANA -> SkillTraining.EXPERTISE
                            SkillKey.PERCEPTION -> SkillTraining.PROFICIENT
                            else -> SkillTraining.NONE
                        },
                    )
                }
                val currencies = created.currencies.mapIndexed { index, currency ->
                    currency.copy(amount = (index + 1) * 10)
                } + CharacterCurrency(
                    key = Uuid.random().toString(),
                    name = "Astral",
                    amount = 7,
                    sortOrder = created.currencies.size,
                    isDefault = false,
                )

                val saved = characters.saveCharacter(
                    created.copy(
                        name = "Vanya integral",
                        status = CharacterStatus.INACTIVE,
                        strength = 9,
                        dexterity = 14,
                        constitution = 13,
                        intelligence = 18,
                        wisdom = 16,
                        charisma = 11,
                        armorClass = 17,
                        maxHp = 48,
                        currentHp = 31,
                        tempHp = 6,
                        initiativeAdjustment = 2,
                        speed = 35,
                        proficiencyBonus = 4,
                        savingThrows = savingThrows,
                        passivePerceptionAdjustment = 2,
                        spellSaveDc = 16,
                        classes = listOf(
                            CharacterClassLevel(
                                id = classId,
                                name = "Mago",
                                level = 7,
                                hitDieSides = 6,
                                hitDiceRemaining = 4,
                                sortOrder = 0,
                            ),
                        ),
                        skills = skills,
                        spellAttackModifier = 8,
                        spellcastingAbility = SpellcastingAbility.INTELLIGENCE,
                        spellSlots = listOf(
                            CharacterSpellSlot(level = 1, totalSlots = 4, spentSlots = 1),
                            CharacterSpellSlot(level = 3, totalSlots = 3, spentSlots = 2),
                        ),
                        combatEntries = listOf(
                            CharacterCombatEntry(
                                id = combatId,
                                name = "Bastón arcano",
                                type = CharacterCombatEntryType.ATTACK,
                                attackModifier = 5,
                                damageEffect = "1d6+1 contundente",
                                rangeText = "5 ft",
                                notes = "Referencia run-180",
                                sortOrder = 0,
                            ),
                        ),
                        inventoryItems = listOf(
                            CharacterInventoryItem(
                                id = itemId,
                                name = "Amuleto integral",
                                quantity = 2,
                                weightLb = 1.5,
                                equipped = true,
                                notes = "Inventario legado",
                                sortOrder = 0,
                                special = true,
                                description = "Objeto especial persistente.",
                                location = "Cuello",
                                attuned = true,
                            ),
                        ),
                        currencies = currencies,
                        spellcasterEnabled = true,
                        background = CharacterBackground(
                            name = "Erudito",
                            summary = "Investigador",
                            race = "Humano",
                            religionFaith = "Ioun",
                            personalityTraits = "Curioso",
                            ideals = "Conocimiento",
                            bonds = "Biblioteca",
                            flaws = "Obsesivo",
                            story = "Historia persistente de Phase 4.",
                        ),
                        traits = listOf(
                            CharacterTrait(
                                id = traitId,
                                name = "Recuperación arcana",
                                source = "Mago",
                                type = CharacterTraitType.CLASS,
                                description = "Recupera energía.",
                                notes = "Rasgo estructurado",
                                maxUses = 2,
                                spentUses = 1,
                                recovery = "Descanso largo",
                                activation = CharacterActivationType.ACTION,
                                sortOrder = 0,
                            ),
                        ),
                        spellcastingSources = listOf(
                            CharacterSpellcastingSource(
                                id = sourceId,
                                name = "Mago",
                                linkedClassId = classId,
                                sortOrder = 0,
                            ),
                            CharacterSpellcastingSource(
                                id = customSourceId,
                                name = "Fuente personalizada",
                                linkedClassId = null,
                                sortOrder = 1,
                            ),
                        ),
                        spells = listOf(
                            CharacterSpell(
                                id = spellId,
                                name = "Detectar magia",
                                level = 1,
                                castingTime = "1 acción",
                                rangeText = "Personal",
                                verbal = true,
                                somatic = true,
                                material = false,
                                materialText = null,
                                duration = "10 minutos",
                                concentration = true,
                                ritual = true,
                                description = "Detecta magia cercana.",
                                notes = "Conjuro conceptual",
                                sortOrder = 0,
                                sourceAssociations = listOf(
                                    CharacterSpellSourceAssociation(sourceId, prepared = true),
                                    CharacterSpellSourceAssociation(customSourceId, prepared = false),
                                ),
                            ),
                        ),
                        generalNotes = "Notas generales integrales.\nSegunda línea.",
                        noteCards = listOf(
                            CharacterNote(
                                id = noteId,
                                title = "Pendiente",
                                content = "Volver a la torre.",
                                sortOrder = 0,
                            ),
                        ),
                    ),
                )

                assertEquals(7, saved.totalLevel)
                assertEquals(4, saved.finalProficiencyBonus)
                assertEquals(3.0, saved.carriedWeightLb, 0.000001)
                assertEquals(1, saved.attunedItemCount)
                assertTrue(saved.spellcasterEnabled)
            }

            JdbcSqliteDriver(jdbcUrl).use { driver ->
                val reopened = CharacterRepository(AppDatabase(driver)).character(requireNotNull(characterId))
                assertNotNull(reopened)

                assertEquals("Vanya integral", reopened.name)
                assertEquals(CharacterStatus.INACTIVE, reopened.status)
                assertEquals(listOf(9, 14, 13, 18, 16, 11), listOf(
                    reopened.strength,
                    reopened.dexterity,
                    reopened.constitution,
                    reopened.intelligence,
                    reopened.wisdom,
                    reopened.charisma,
                ))
                assertEquals(17, reopened.armorClass)
                assertEquals(48, reopened.maxHp)
                assertEquals(31, reopened.currentHp)
                assertEquals(6, reopened.tempHp)
                assertEquals(35, reopened.speed)
                assertEquals(4, reopened.finalProficiencyBonus)
                assertTrue(reopened.savingThrow(CharacterAbility.WISDOM).proficient)
                assertEquals(1, reopened.savingThrow(CharacterAbility.WISDOM).adjustment)
                assertEquals(SkillTraining.EXPERTISE, reopened.skill(SkillKey.ARCANA).training)
                assertEquals(2, reopened.skill(SkillKey.ARCANA).adjustment)
                assertEquals(2, reopened.passivePerceptionAdjustment)

                assertEquals(classId, reopened.classes.single().id)
                assertEquals(7, reopened.classes.single().level)
                assertEquals(4, reopened.classes.single().hitDiceRemaining)
                assertEquals(16, reopened.spellSaveDc)
                assertEquals(8, reopened.spellAttackModifier)
                assertEquals(SpellcastingAbility.INTELLIGENCE, reopened.spellcastingAbility)
                assertEquals(listOf(1, 3), reopened.spellSlots.map { it.level })
                assertEquals(listOf(1, 2), reopened.spellSlots.map { it.spentSlots })

                assertEquals(combatId, reopened.combatEntries.single().id)
                assertEquals("Referencia run-180", reopened.combatEntries.single().notes)
                assertEquals(itemId, reopened.inventoryItems.single().id)
                assertTrue(reopened.inventoryItems.single().special)
                assertTrue(reopened.inventoryItems.single().equipped)
                assertTrue(reopened.inventoryItems.single().attuned)
                assertEquals("Cuello", reopened.inventoryItems.single().location)
                assertEquals(3.0, reopened.carriedWeightLb, 0.000001)
                assertEquals(listOf(10, 20, 30, 40, 50, 7), reopened.currencies.map { it.amount })
                assertEquals("Astral", reopened.currencies.last().name)

                assertTrue(reopened.spellcasterEnabled)
                assertEquals("Erudito", reopened.background.name)
                assertEquals("Humano", reopened.background.race)
                assertEquals("Ioun", reopened.background.religionFaith)
                assertEquals("Historia persistente de Phase 4.", reopened.background.story)
                assertEquals(traitId, reopened.traits.single().id)
                assertEquals(1, reopened.traits.single().spentUses)
                assertEquals(CharacterActivationType.ACTION, reopened.traits.single().activation)

                assertEquals(listOf(sourceId, customSourceId), reopened.spellcastingSources.map { it.id })
                assertEquals(classId, reopened.spellcastingSources.first().linkedClassId)
                val spell = reopened.spells.single()
                assertEquals(spellId, spell.id)
                assertEquals("Detectar magia", spell.name)
                assertEquals(
                    mapOf(sourceId to true, customSourceId to false),
                    spell.sourceAssociations.associate { it.sourceId to it.prepared },
                )

                assertEquals("Notas generales integrales.\nSegunda línea.", reopened.generalNotes)
                assertEquals(noteId, reopened.noteCards.single().id)
                assertEquals("Pendiente", reopened.noteCards.single().title)
                assertEquals("Volver a la torre.", reopened.noteCards.single().content)

                val hidden = CharacterRepository(AppDatabase(driver)).saveCharacter(
                    reopened.copy(spellcasterEnabled = false),
                )
                assertFalse(hidden.spellcasterEnabled)
                assertEquals(2, hidden.spellcastingSources.size)
                assertEquals(1, hidden.spells.size)
                assertEquals(2, hidden.spellSlots.size)
                assertEquals("Erudito", hidden.background.name)
                assertEquals("Pendiente", hidden.noteCards.single().title)
            }
        } finally {
            file.delete()
        }
    }
}
