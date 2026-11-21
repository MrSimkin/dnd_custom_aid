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

    @Test
    fun aGroupWithOneInitiativeActsAsOneTurnAndRollsRespectEachGroupsChoice() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val goblin = CreatureContentRepository(database).createCampaign(campaign.id, "Goblin", goblinPayload(), 100)
        val wolf = CreatureContentRepository(database).createCampaign(campaign.id, "Lobo", goblinPayload(), 100)
        var doc = CombatEngine.new(campaign.id, "Puente", 200)
        doc = CombatEngine.addCreature(doc, goblin, 3)
        doc = CombatEngine.addCreature(doc, wolf, 2)
        val goblins = doc.combatants.first().groupId
        val wolves = doc.combatants.last().groupId

        doc = CombatEngine.setInitiativeShared(doc, goblins, true)
        val dice = ArrayDeque(listOf(15, 4, 9))
        doc = CombatEngine.rollAllCreatureInitiative(doc) { dice.removeFirst() }
        assertTrue(dice.isEmpty(), "one roll for the shared goblins, one per wolf")
        val goblinInitiatives = doc.combatants.filter { it.groupId == goblins }.map { it.initiative }.distinct()
        assertEquals(listOf(17), goblinInitiatives, "d20 15 + DEX 2 for the whole group")
        assertEquals(15, doc.combatants.first().initiativeDie)
        assertEquals(listOf(6, 11), doc.combatants.filter { it.groupId == wolves }.map { it.initiative })

        doc = CombatEngine.start(doc)
        assertEquals(3, doc.actingIds.size, "the three goblins act together")
        doc = CombatEngine.nextTurn(doc)
        assertEquals("Lobo 2", doc.combatant(doc.activeCombatantId!!)!!.label)
        doc = CombatEngine.nextTurn(doc)
        doc = CombatEngine.nextTurn(doc)
        assertEquals(2, doc.round)
        assertTrue(doc.activeCombatantId in doc.combatants.filter { it.groupId == goblins }.map { it.id })
    }

    @Test
    fun aSuccessfulAmbushOpensWithASurpriseRoundForTheAmbushersOnly() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val goblin = CreatureContentRepository(database).createCampaign(campaign.id, "Goblin", goblinPayload(), 100)
        var doc = CombatEngine.addCreature(CombatEngine.new(campaign.id, "Cripta", 200), goblin, 2)
        doc = CombatEngine.addCustom(doc, "Ana", null, 14)
        doc = CombatEngine.addCustom(doc, "Bo", null, 12)
        val ana = doc.combatants.first { it.label == "Ana" }
        val bo = doc.combatants.first { it.label == "Bo" }
        listOf(ana.id to 5, bo.id to 18).forEach { (id, value) -> doc = CombatEngine.setInitiative(doc, id, value) }
        doc.combatants.filter { it.label.startsWith("Goblin") }.forEach { doc = CombatEngine.setInitiative(doc, it.id, 10) }

        doc = CombatEngine.setSurpriseActors(doc, listOf(ana.id))
        doc = CombatEngine.start(doc)
        assertEquals(0, doc.round, "round 0 is the surprise round")
        assertEquals(ana.id, doc.activeCombatantId, "only the ambusher acts, even with a lower initiative")
        doc = CombatEngine.nextTurn(doc)
        assertEquals(1, doc.round)
        assertEquals(bo.id, doc.activeCombatantId, "round 1 starts at the top of the full order")

        val noSurprise = CombatEngine.start(CombatEngine.setSurpriseActors(doc.copy(status = CombatStatus.PREPARING), emptyList()))
        assertEquals(1, noSurprise.round, "the DM can always start without surprise")
    }

    @Test
    fun blockQuantityAndAVariantSavedAsANewCreatureLeaveTheBaseUntouched() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val creatures = CreatureContentRepository(database)
        val goblin = creatures.createCampaign(campaign.id, "Goblin", goblinPayload(), 100)
        var doc = CombatEngine.addCreature(CombatEngine.new(campaign.id, "Puente", 200), goblin, 2)
        val group = doc.combatants.first().groupId
        doc = CombatEngine.setGroupQuantity(doc, group, 4)
        assertEquals(4, doc.combatants.count { it.groupId == group })
        doc = CombatEngine.setGroupQuantity(doc, group, 3)
        assertEquals(listOf("Goblin 1", "Goblin 2", "Goblin 3"), doc.combatants.map { it.label })

        val boss = doc.combatants.first()
        doc = CombatEngine.setEdits(doc, boss.id, CreatureEdits(
            abilityScores = CreatureAbilityScores(strength = 16, dexterity = 14),
            attacks = listOf(CombatAttack("Hacha", bonus = 5, damage = "1d8+3", damageType = "cortante", reach = "1,5 m")),
        ))
        doc = CombatEngine.setOverrides(doc, boss.id, CombatOverrides(maxHp = 20))
        val edited = doc.combatant(boss.id)!!
        assertTrue(edited.hasOwnChanges)
        assertTrue(!doc.combatants[1].hasOwnChanges, "only the edited goblin carries the ●")

        val payload = CombatEngine.variantPayload(goblin.payload, edited, edited.effective(emptyMap()))
        val saved = creatures.createCampaign(campaign.id, "Goblin jefe", payload, 300)
        assertEquals(20, saved.payload.hitPoints)
        assertEquals(16, saved.payload.abilityScores.strength)
        assertTrue("Hacha. +5 al impactar, 1,5 m, 1d8+3 cortante." in saved.payload.actions)
        assertEquals(7, creatures.creature(goblin.item.identity.id)!!.payload.hitPoints, "the bestiary creature does not change")

        doc = CombatEngine.resetAll(doc, boss.id)
        assertTrue(!doc.combatant(boss.id)!!.hasOwnChanges)
        assertEquals(7, doc.combatant(boss.id)!!.currentHp)
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
