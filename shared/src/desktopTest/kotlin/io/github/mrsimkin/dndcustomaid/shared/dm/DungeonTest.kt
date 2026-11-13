package io.github.mrsimkin.dndcustomaid.shared.dm

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRulePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePassage
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DungeonTest {
    private val ana = Uuid.random()
    private val bo = Uuid.random()
    private val cy = Uuid.random()
    private val names = mapOf(ana to "Ana", bo to "Bo", cy to "Cy")
    private val activities = DungeonRules.betaActivities

    @Test
    fun theBetaListHasFifteenActivitiesPlusTheTwoStarredOnes() {
        assertEquals(17, activities.size)
        assertEquals(listOf("Exploración normal *", "Ocultarse *"), activities.filter { it.starred }.map { it.displayName })
    }

    @Test
    fun theSlowestActivitySetsTheAdvanceAndATurnIsLoggedNotCarriedOver() {
        val zone = Uuid.random()
        var doc = DungeonEngine.new(Uuid.random(), "Cripta", names.map { it.key to it.value }, zone, 100)
        doc = DungeonEngine.declare(doc, ActivityDeclaration(ana, "avanzar"))
        doc = DungeonEngine.declare(doc, ActivityDeclaration(bo, "silencio"))
        assertEquals(DungeonAdvance.REDUCED, DungeonEngine.groupAdvance(doc, activities, doc.groups.single()))
        doc = DungeonEngine.declare(doc, ActivityDeclaration(cy, "buscar", focus = "Secretos", thorough = true))
        assertEquals(DungeonAdvance.NONE, DungeonEngine.groupAdvance(doc, activities, doc.groups.single()), "a thorough search keeps the group in place")
        doc = DungeonEngine.declare(doc, ActivityDeclaration(ana, "ayudar", helpsCharacterId = bo))
        assertEquals(DungeonAdvance.REDUCED, DungeonEngine.advanceOf(doc, activities, doc.declaration(ana)!!), "helping takes the helped Activity's cost")

        val closed = DungeonEngine.closeTurn(doc, activities, names, mapOf(zone to "Antesala"))
        val after = closed.document
        assertEquals(2, after.turn)
        assertTrue(after.declarations.isEmpty(), "nothing is carried over")
        assertEquals("buscar", after.previousOf(cy)?.activityKey, "the previous Activity stays as reference")
        val record = after.log.single().groups.single()
        assertEquals("Antesala", record.zoneName)
        assertEquals(DungeonAdvance.NONE, record.advance)
        assertTrue("Cy: Buscar (Secretos, a fondo)" in record.activities)
        assertTrue("Ana: Ayudar (a Bo)" in record.activities)
    }

    @Test
    fun clocksTickPerTurnAndOnlyWarnAndAnAmbushIsReported() {
        var doc = DungeonEngine.new(Uuid.random(), "Cripta", names.map { it.key to it.value }, null, 100)
        doc = DungeonEngine.addClock(doc, "Patrulla", 2, perTurn = true)
        doc = DungeonEngine.addClock(doc, "Ritual", 4, perTurn = false)
        doc = DungeonEngine.declare(doc, ActivityDeclaration(bo, "emboscar"))
        val first = DungeonEngine.closeTurn(doc, activities, names, emptyMap())
        assertTrue(first.clocksFilled.isEmpty())
        assertEquals(listOf(bo), first.ambusherIds)
        val second = DungeonEngine.closeTurn(first.document, activities, names, emptyMap())
        assertEquals(listOf("Patrulla"), second.clocksFilled.map { it.name }, "a clock warns once when it fills")
        assertEquals(0, second.document.clocks.first { it.name == "Ritual" }.filled, "manual clocks only move by hand")
        val third = DungeonEngine.closeTurn(second.document, activities, names, emptyMap())
        assertTrue(third.clocksFilled.isEmpty())
    }

    @Test
    fun theGroupSplitsAndMergesAndNothingMovesByItself() {
        val a = Uuid.random()
        val b = Uuid.random()
        var doc = DungeonEngine.new(Uuid.random(), "Cripta", names.map { it.key to it.value }, a, 100)
        val main = doc.groups.single()
        doc = DungeonEngine.split(doc, main.id, listOf(cy), "Exploradora")
        assertEquals(2, doc.groups.size)
        assertEquals(a, doc.groupOf(cy)?.zoneId, "a new group starts where it was")
        doc = DungeonEngine.moveGroup(doc, doc.groupOf(cy)!!.id, b)
        assertEquals(a, doc.groupOf(ana)?.zoneId)
        doc = DungeonEngine.interruptByCombat(doc, activities, names, emptyMap(), completedIds = listOf(ana))
        assertTrue(doc.log.single().interruptedByCombat)
        assertEquals(listOf("Ana"), doc.log.single().completed)
        doc = DungeonEngine.merge(doc, doc.groupOf(cy)!!.id, main.id)
        assertEquals(1, doc.groups.size)
        doc = DungeonEngine.toggleMark(doc, a, ZoneMark.EXPLORED)
        doc = DungeonEngine.toggleMark(doc, a, ZoneMark.LOOTED)
        assertEquals(listOf(ZoneMark.EXPLORED, ZoneMark.LOOTED), doc.marks(a))
        doc = DungeonEngine.toggleMark(doc, a, ZoneMark.EXPLORED)
        assertEquals(listOf(ZoneMark.LOOTED), doc.marks(a))
    }

    @Test
    fun theDiagramComesFromTheZonesAndADmRuleCanReplaceTheActivities() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val zones = ZoneContentRepository(database)
        val hall = zones.createCampaign(campaign.id, "Antesala", ZonePayload(), 100)
        val crypt = zones.createCampaign(campaign.id, "Cripta", ZonePayload(), 100)
        val loose = zones.createCampaign(campaign.id, "Mercado", ZonePayload(), 100)
        val links = ContentLinksRepository(database)
        links.save(hall.item.identity.id, ContentLinks(dungeon = "Catacumbas", diagramX = 0.2, diagramY = 0.5, passages = listOf(ZonePassage(crypt.item.identity.id, secret = true))))
        links.save(crypt.item.identity.id, ContentLinks(dungeon = "catacumbas"))
        links.save(loose.item.identity.id, ContentLinks(placeIds = emptyList()))

        val index = SceneIndex.load(database, campaign.id)
        assertEquals(listOf("Catacumbas"), index.dungeons(), "names that differ only in capitals are one dungeon")
        val map = index.dungeonMap("Catacumbas")
        assertEquals(setOf("Antesala", "Cripta"), map.map { it.zone.item.displayName }.toSet())
        assertEquals(0.2, map.first { it.zone.item.displayName == "Antesala" }.x)
        val cryptOnMap = map.first { it.zone.item.displayName == "Cripta" }
        assertTrue(cryptOnMap.passages.single().secret, "passages show from both ends")
        assertEquals(hall.item.identity.id, cryptOnMap.passages.single().toZoneId)

        assertEquals(DungeonRules.betaActivities, dungeonActivities(database, campaign.id))
        val rule = HomebrewRuleContentRepository(database).createCampaign(campaign.id, "Turnos de dungeon", HomebrewRulePayload(), 100)
        links.save(rule.item.identity.id, ContentLinks(activities = listOf(DungeonActivity("rezar", "Rezar", DungeonAdvance.NONE))))
        assertEquals(listOf("Rezar"), dungeonActivities(database, campaign.id).map { it.name })

        val repo = DmDungeonRepository(database)
        val saved = repo.save(DungeonEngine.new(campaign.id, "Catacumbas", emptyList(), hall.item.identity.id, 100), 120)
        assertEquals(listOf(saved.id), repo.list(campaign.id).map { it.id })
        repo.delete(saved.id)
        assertNull(repo.dungeon(saved.id))
    }

    private fun db(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        driver.execute(null, "PRAGMA foreign_keys = ON", 0)
        try {
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
