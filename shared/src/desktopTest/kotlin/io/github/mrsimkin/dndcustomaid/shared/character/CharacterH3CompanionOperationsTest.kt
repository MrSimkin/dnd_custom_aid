package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterH3CompanionOperationsTest {
    private fun companion(
        name: String,
        order: Int,
        kind: String = "Constructo",
        source: String? = "Manual",
        active: Boolean = true,
    ) = CharacterCompanion(
        id = Uuid.random(),
        name = name,
        kind = kind,
        source = source,
        armorClass = 15,
        maxHp = 24,
        currentHp = 19,
        tempHp = 2,
        speed = "40 ft",
        abilitySummary = "FUE 14 · DES 12",
        sensesProficiencies = "Percepción +4",
        traitsActions = "Ataque de prueba",
        notes = "Notas $name",
        active = active,
        sortOrder = order,
    )

    @Test
    fun companionSearchFiltersAndAlphabeticalOrderArePresentationOnly() {
        val wolf = companion("Lobo", 8, kind = "Bestia", source = "Explorador")
        val defender = companion("Defensor de acero", 2, kind = "Constructo", source = "Artífice")
        val inactive = companion("Espíritu dormido", 4, kind = "Espíritu", source = "Druida", active = false)
        val companions = listOf(wolf, defender, inactive)

        val filtered = presentCharacterCompanions(
            companions = companions,
            order = CharacterPresentationOrder.ALPHABETICAL,
            query = CharacterCollectionQuery(
                searchText = "percepcion",
                activeFilterKeys = setOf(
                    CHARACTER_COMPANION_ACTIVE_FILTER_KEY,
                    CHARACTER_COMPANION_FAVORITE_FILTER_KEY,
                    characterCompanionKindFilterKey("Bestia"),
                    characterCompanionSourceFilterKey("Explorador"),
                ),
            ),
            isFavorite = { it.id == wolf.id },
        )

        assertEquals(listOf(wolf.id), filtered.map { it.id })
        assertEquals(listOf(8, 2, 4), companions.map { it.sortOrder })

        val alphabetical = presentCharacterCompanions(
            companions = listOf(wolf, defender),
            order = CharacterPresentationOrder.ALPHABETICAL,
        )
        assertEquals(listOf(defender.id, wolf.id), alphabetical.map { it.id })
        assertEquals(listOf(8, 2), listOf(wolf.sortOrder, defender.sortOrder))
    }

    @Test
    fun companionManualMoveAndDuplicatePreserveReferenceState() {
        val first = companion("Primero", 0)
        val second = companion("Segundo", 1)
        val third = companion("Tercero", 2)

        val moved = moveCharacterCompanionManual(listOf(first, second, third), first.id, 1)
            .sortedBy { it.sortOrder }
        assertEquals(listOf(second.id, first.id, third.id), moved.map { it.id })

        val newId = Uuid.random()
        val copy = duplicateCharacterCompanion(
            source = first,
            newId = newId,
            sortOrder = nextCharacterCompanionSortOrder(listOf(first, second, third)),
        )
        assertEquals(newId, copy.id)
        assertEquals("Primero (copia)", copy.name)
        assertEquals(3, copy.sortOrder)
        assertEquals(first.currentHp, copy.currentHp)
        assertEquals(first.tempHp, copy.tempHp)
        assertEquals(first.traitsActions, copy.traitsActions)
    }

    @Test
    fun multipleOfficialTriggersProduceOneCompanionModuleAlongsideOtherModules() {
        fun level(
            classKey: String,
            subclassKey: String,
            order: Int,
        ): CharacterClassLevel {
            val entry = CharacterClassCatalog.byKey(classKey)!!
            val subclass = CharacterClassCatalog.subclassByKey(subclassKey)!!
            return CharacterClassLevel(
                id = Uuid.random(),
                name = entry.nameEs,
                level = 5,
                hitDieSides = 8,
                hitDiceRemaining = 5,
                sortOrder = order,
                rulesFamily = entry.rulesFamily,
                source = entry.source,
                catalogKey = classKey,
                subclassName = subclass.name,
                subclassSource = subclass.source,
                subclassCatalogKey = subclassKey,
                subclassRulesFamily = subclass.rulesFamily,
            )
        }

        val classes = listOf(
            level("artificer-2025", "artificer-battle-smith-2025", 0),
            level("ranger-2024", "ranger-beast-master-2024", 1),
            level("warlock-2024", "warlock-vestige-2026", 2),
        )

        val visible = visibleCharacterModules(classes, emptyList())

        assertTrue(CharacterModuleKind.COMPANIONS in visible)
        assertTrue(CharacterModuleKind.ARTIFICER in visible)
        assertTrue(CharacterModuleKind.PACTS in visible)
        assertEquals(1, visible.count { it == CharacterModuleKind.COMPANIONS })
    }

    @Test
    fun companionManualOverrideCanHideOrExposeWithoutChangingOtherModuleRules() {
        val battleSmith = CharacterClassLevel(
            id = Uuid.random(),
            name = "Artífice",
            level = 5,
            hitDieSides = 8,
            hitDiceRemaining = 5,
            sortOrder = 0,
            rulesFamily = CharacterRulesFamily.DND_5_5E,
            catalogKey = "artificer-2025",
            subclassName = "Battle Smith",
            subclassCatalogKey = "artificer-battle-smith-2025",
        )

        val hidden = visibleCharacterModules(
            listOf(battleSmith),
            listOf(CharacterModuleOverride(CharacterModuleKind.COMPANIONS, CharacterModuleOverrideMode.FORCE_HIDE)),
        )
        assertFalse(CharacterModuleKind.COMPANIONS in hidden)
        assertTrue(CharacterModuleKind.ARTIFICER in hidden)

        val custom = CharacterClassLevel(
            id = Uuid.random(),
            name = "Domador homebrew",
            level = 5,
            hitDieSides = 8,
            hitDiceRemaining = 5,
            sortOrder = 0,
            rulesFamily = CharacterRulesFamily.CUSTOM,
        )
        val shown = visibleCharacterModules(
            listOf(custom),
            listOf(CharacterModuleOverride(CharacterModuleKind.COMPANIONS, CharacterModuleOverrideMode.FORCE_SHOW)),
        )
        assertTrue(CharacterModuleKind.COMPANIONS in shown)
    }

    @Test
    fun companionRoundTripPreservesFieldsAndSoftUnlinksRemovedClass() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val campaign = campaigns.createCampaign("H3")
            val created = characters.createCharacter(campaign.id, "Inventora")
            val classId = Uuid.random()
            val companion = companion("Defensor de acero", 0, source = "Battle Smith").copy(
                linkedClassId = classId,
                armorClass = 17,
                maxHp = 35,
                currentHp = 31,
                tempHp = 4,
                speed = "40 ft",
                abilitySummary = "Constructo durable",
                sensesProficiencies = "Visión en la oscuridad",
                traitsActions = "Reacción y ataque",
                notes = "Referencia de mesa",
            )

            val saved = characters.saveCharacter(
                created.copy(
                    classes = listOf(
                        CharacterClassLevel(
                            id = classId,
                            name = "Artífice",
                            level = 5,
                            hitDieSides = 8,
                            hitDiceRemaining = 5,
                            sortOrder = 0,
                            rulesFamily = CharacterRulesFamily.DND_5_5E,
                            catalogKey = "artificer-2025",
                            subclassName = "Battle Smith",
                            subclassCatalogKey = "artificer-battle-smith-2025",
                        ),
                    ),
                    companions = listOf(companion),
                ),
            )
            val reopened = characters.character(saved.id)!!
            val persisted = reopened.companions.single()

            assertEquals(classId, persisted.linkedClassId)
            assertEquals(17, persisted.armorClass)
            assertEquals(35, persisted.maxHp)
            assertEquals(31, persisted.currentHp)
            assertEquals(4, persisted.tempHp)
            assertEquals("Reacción y ataque", persisted.traitsActions)

            val withoutClass = characters.saveCharacter(reopened.copy(classes = emptyList()))
            val softUnlinked = withoutClass.companions.single()
            assertNull(softUnlinked.linkedClassId)
            assertEquals("Defensor de acero", softUnlinked.name)
            assertEquals(31, softUnlinked.currentHp)
            assertEquals("Referencia de mesa", softUnlinked.notes)
        } finally {
            driver.close()
        }
    }
}
