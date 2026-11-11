package io.github.mrsimkin.dndcustomaid.shared.dm

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipantReadiness
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePayload
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePiece
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePieceKind
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CombatTest {
    @Test
    fun dicePartsAverageAndRollWithinRange() {
        assertEquals(DiceExpression(2, 6, 0), DiceExpression.parse("2d6"))
        assertEquals(DiceExpression(4, 8, 4), DiceExpression.parse("22 (4d8 + 4)"))
        assertEquals(DiceExpression(1, 10, -1), DiceExpression.parse("1d10 − 1"))
        assertNull(DiceExpression.parse("mucho"))
        assertEquals(22, DiceExpression(4, 8, 4).average)
        val random = Random(7)
        repeat(50) { assertTrue(DiceExpression(2, 6, 1).roll(random) in 3..13) }
    }

    @Test
    fun aPreparedEncounterEntersWholeAndStartingKeepsThePreparation() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val goblin = CreatureContentRepository(database).createCampaign(campaign.id, "Goblin", goblinPayload(), 100)
        val boss = CreatureContentRepository(database).createCampaign(campaign.id, "Jefe", goblinPayload().copy(hitPoints = 40, armorClass = 17), 100)
        val encounter = EncounterContentRepository(database).createCampaign(
            campaign.id,
            "Emboscada",
            EncounterPayload(participants = listOf(
                EncounterParticipant(sourceContentId = goblin.item.identity.id, quantity = 3),
                EncounterParticipant(sourceContentId = boss.item.identity.id, readiness = EncounterParticipantReadiness.RESERVE),
                EncounterParticipant(label = "Lobo", quantity = 2),
            )),
            110,
        )
        var doc = CombatEngine.new(campaign.id, "Puente", 200)
        doc = CombatEngine.addEncounter(doc, encounter, mapOf(goblin.item.identity.id to goblin, boss.item.identity.id to boss))

        assertEquals(listOf("Goblin 1", "Goblin 2", "Goblin 3", "Jefe", "Lobo 1", "Lobo 2"), doc.combatants.map { it.label })
        assertEquals(listOf("Jefe"), doc.reserve.map { it.label }, "reserve stays out of the order")
        assertEquals(7, doc.combatants.first().currentHp, "HP start at the stat block average (P13)")
        assertEquals(1, doc.combatants.filter { it.label.startsWith("Goblin") }.map { it.groupId }.distinct().size)

        val first = doc.combatants.first()
        doc = CombatEngine.setOverrides(doc, first.id, CombatOverrides(armorClass = 18, attackBonus = 9), wholeGroup = true)
        doc = CombatEngine.start(doc)
        assertEquals(CombatStatus.ACTIVE, doc.status)
        assertEquals(1, doc.round)
        assertTrue(doc.combatants.filter { it.label.startsWith("Goblin") }.all { it.overrides.armorClass == 18 }, "start keeps the prepared tweaks (P19)")
        assertEquals(listOf("Jefe"), doc.reserve.map { it.label })
    }

    @Test
    fun packagesAddAndAdjustWithoutTouchingTheBaseAndAreReversible() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val goblin = CreatureContentRepository(database).createCampaign(campaign.id, "Goblin", goblinPayload(), 100)
        val brute = PackageContentRepository(database).createCampaign(
            campaign.id,
            "Bruto",
            PackagePayload(
                pieces = listOf(PackagePiece(PackagePieceKind.ACTION, "Golpe brutal", "+2d6 contundente")),
                removals = listOf("Huida ágil"),
                hpDelta = 10,
                armorClassDelta = 1,
                attackBonusDelta = 2,
            ),
            110,
        )
        val packages = mapOf(brute.item.identity.id to brute)
        var doc = CombatEngine.addCreature(CombatEngine.new(campaign.id, "Prueba", 200), goblin, quantity = 2)
        val one = doc.combatants.first()

        doc = CombatEngine.applyPackage(doc, one.id, brute, wholeGroup = true)
        val effective = doc.combatants.map { it.effective(packages) }
        assertTrue(doc.combatants.all { it.currentHp == 17 }, "+10 HP also raises the current HP")
        assertTrue(effective.all { it.armorClass == 16 && it.maxHp == 17 && it.attackBonusDelta == 2 })
        assertEquals(listOf("Golpe brutal"), effective.first().addedPieces.map { it.name })
        assertEquals(listOf("Huida ágil"), effective.first().removals)
        assertEquals(15, CreatureContentRepository(database).creature(goblin.item.identity.id)!!.payload.armorClass, "base untouched")

        doc = CombatEngine.setOverrides(doc, one.id, CombatOverrides(armorClass = 12))
        assertEquals(12, doc.combatant(one.id)!!.effective(packages).armorClass, "an override always wins")

        doc = CombatEngine.resetPatches(doc, one.id, wholeGroup = true)
        assertTrue(doc.combatants.all { it.packageIds.isEmpty() && it.overrides.isEmpty })
        assertEquals(15, doc.combatant(one.id)!!.effective(packages).armorClass)
    }

    @Test
    fun initiativeHpAndTurnsWorkTheWayTheTableNeeds() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val goblin = CreatureContentRepository(database).createCampaign(campaign.id, "Goblin", goblinPayload(), 100)
        var doc = CombatEngine.addCreature(CombatEngine.new(campaign.id, "Prueba", 200), goblin, quantity = 2)
        doc = CombatEngine.addCustom(doc, "Bandido", maxHp = 11, armorClass = 12)
        val (g1, g2, bandit) = doc.combatants

        doc = CombatEngine.rollInitiative(doc, Random(3))
        assertTrue(doc.combatants.all { it.initiative != null }, "«Tirar por mí» for everyone (P12)")
        doc = CombatEngine.setInitiative(doc, g1.id, 20)
        doc = CombatEngine.setInitiative(doc, g2.id, 5)
        doc = CombatEngine.setInitiative(doc, bandit.id, 12)
        assertEquals(listOf(g1.id, bandit.id, g2.id), doc.turnOrder.map { it.id })

        doc = CombatEngine.start(doc)
        assertEquals(g1.id, doc.activeCombatantId)
        doc = CombatEngine.setDefeated(doc, bandit.id, true)
        doc = CombatEngine.nextTurn(doc)
        assertEquals(g2.id, doc.activeCombatantId, "defeated combatants are skipped")
        doc = CombatEngine.nextTurn(doc)
        assertEquals(g1.id, doc.activeCombatantId)
        assertEquals(2, doc.round)

        doc = CombatEngine.damage(doc, g1.id, 10)
        assertEquals(0, doc.combatant(g1.id)!!.currentHp, "HP never go below 0")
        doc = CombatEngine.heal(doc, g1.id, 50, maxHp = 7)
        assertEquals(7, doc.combatant(g1.id)!!.currentHp, "healing stops at the maximum")
        doc = CombatEngine.rollHp(doc, g2.id, Random(1))
        assertTrue(doc.combatant(g2.id)!!.currentHp!! in 2..12, "«Tirar» rolls 2d6 (P13)")

        doc = CombatEngine.toggleCondition(doc, g2.id, "Derribado")
        assertEquals(listOf("Derribado"), doc.combatant(g2.id)!!.conditions)
        doc = CombatEngine.toggleCondition(doc, g2.id, "Derribado")
        assertTrue(doc.combatant(g2.id)!!.conditions.isEmpty())
    }

    @Test
    fun pcsAreAddedOnceWithAnOptionalHpNoteAndTheirInitiativeIsWritten() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository(database)
        val bram = characters.createCharacter(campaign.id, "Bram")
        var doc = CombatEngine.addPcs(CombatEngine.new(campaign.id, "Prueba", 200), listOf(bram))
        doc = CombatEngine.addPcs(doc, listOf(bram))
        assertEquals(1, doc.combatants.size, "a PC is never added twice")
        val pc = doc.combatants.single()
        assertNull(pc.currentHp, "PC HP stay on paper (P11)")
        doc = CombatEngine.rollInitiative(doc, Random(1))
        assertNull(doc.combatant(pc.id)!!.initiative, "PC initiative is written by hand (P12)")
        doc = CombatEngine.setPcHpNote(doc, pc.id, "12/20")
        assertEquals("12/20", doc.combatant(pc.id)!!.pcHpNote)
    }

    @Test
    fun combatsAreSavedPerCampaignAndSeveralCanCoexist() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repo = DmCombatRepository(database)
        val a = repo.save(CombatEngine.new(campaign.id, "Puente", 100), 100)
        val b = repo.save(CombatEngine.start(CombatEngine.new(campaign.id, "Cripta", 110)), 120)
        assertEquals(listOf(b.id, a.id), repo.list(campaign.id).map { it.id }, "most recent first")
        assertEquals(CombatStatus.ACTIVE, repo.combat(b.id)!!.status)
        repo.delete(a.id)
        assertEquals(listOf(b.id), repo.list(campaign.id).map { it.id })
    }

    private fun goblinPayload() = CreaturePayload(
        armorClass = 15,
        hitPoints = 7,
        hitDice = "2d6",
        abilityScores = CreatureAbilityScores(dexterity = 14),
        actions = "Cimitarra. +4 al ataque.",
    )

    private fun db(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}

@Suppress("unused")
private fun CreatureContent.name() = item.displayName
