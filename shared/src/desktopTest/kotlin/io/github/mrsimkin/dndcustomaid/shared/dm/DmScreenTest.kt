package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSense
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DmScreenTest {
    private val assets = File("../androidApp/src/main/assets/srd")

    @Test
    fun partyLensMarksTheBestNumbersAndKeepsTextRowsPlain() {
        val lens = partyLens(
            members = listOf(
                member("Bram", armorClass = 18, maxHp = 44, wisdom = 12, speed = 25, languages = listOf("Común", "Enano"),
                    senses = listOf(CharacterSense(Uuid.random(), "Visión en la oscuridad", 60))),
                member("Aldren", armorClass = 16, maxHp = 44, wisdom = 16, speed = 30, languages = listOf("Común")),
            ),
            formatFeet = { "${it / 5 * 1.5} m" },
        )
        assertEquals(listOf("Aldren", "Bram"), lens.columns.map { it.name })
        val rows = lens.rows.associateBy { it.kind }
        assertEquals(listOf(false, true), rows.getValue(PartyLensRowKind.ARMOR_CLASS).cells.map { it.best })
        assertEquals(listOf(true, true), rows.getValue(PartyLensRowKind.MAX_HIT_POINTS).cells.map { it.best }, "ties share the mark")
        assertEquals(listOf(13, 11), rows.getValue(PartyLensRowKind.PASSIVE_PERCEPTION).cells.map { it.number })
        assertEquals(listOf("9.0 m", "7.5 m"), rows.getValue(PartyLensRowKind.SPEED).cells.map { it.text })
        assertEquals(listOf("—", "Visión en la oscuridad 18.0 m"), rows.getValue(PartyLensRowKind.SENSES).cells.map { it.text })
        assertEquals(listOf("Común", "Común, Enano"), rows.getValue(PartyLensRowKind.LANGUAGES).cells.map { it.text })
        assertTrue(rows.getValue(PartyLensRowKind.LANGUAGES).cells.none { it.best })
        assertEquals(PartyLensRowKind.entries.toList(), lens.rows.map { it.kind }, "P9 row order")
    }

    @Test
    fun aSinglePcHasNoBestMark() {
        val lens = partyLens(listOf(member("Solo", 15, 20, 10, 30)), formatFeet = Int::toString)
        assertTrue(lens.rows.flatMap { it.cells }.none { it.best })
    }

    @Test
    fun bothSpanishSrdsShipCompleteWithTheirLicence() {
        val corpora = listOf("srd_5_1_es.json", "srd_5_2_1_es.json").map { parseSrdCorpus(File(assets, it).readText()) }
        assertEquals(listOf(SrdVersion.SRD_5_1, SrdVersion.SRD_5_2_1), corpora.map { it.srdVersion })
        corpora.forEach { corpus ->
            assertEquals("CC-BY-4.0", corpus.license)
            assertTrue(corpus.attribution.contains("Wizards of the Coast LLC"))
            assertTrue(corpus.pages.size > 350, "whole SRD, not an excerpt: ${corpus.pages.size} pages")
            // Page 1-2 of 5.1 are its legal notice, which legitimately names the document.
            assertTrue(corpus.pages.none { "Prohibida la reventa" in it.text })
            assertTrue(corpus.pages.filter { it.page > 2 }.none { "Documento de referencia del sistema 5." in it.text.take(60) })
        }
    }

    @Test
    fun theTenQuickReferenceCardsHaveTextInBothVersions() {
        val quick = parseSrdQuickReference(File(assets, "quick_reference_es.json").readText())
        assertEquals(
            listOf("Estados", "Cobertura", "Concentración", "CD típicas", "Luz y visión", "Caídas y asfixia",
                "Agarrar y derribar", "Ocultarse", "Descansos", "Viaje"),
            quick.cards.map { it.title },
            "P10 cards and order",
        )
        quick.cards.forEach { card ->
            SrdVersion.entries.forEach { version ->
                val sections = card.sections(version)
                assertTrue(sections.isNotEmpty() && sections.all { it.text.length > 40 }, "${card.id} ${version.key}")
            }
        }
        val states521 = quick.cards.first { it.id == "estados" }.sections(SrdVersion.SRD_5_2_1)
        assertEquals(15, states521.size, "the 15 conditions of the 5.2.1 glossary")
        assertTrue(states521.none { "Arrastrarse" in it.text || "Aventura" in it.text }, "no neighbouring glossary entry leaks in")
        assertTrue(quick.cards.first { it.id == "cd_tipicas" }.sections(SrdVersion.SRD_5_2_1).single().text.contains("Casi imposible 30"))
        SrdVersion.entries.forEach { assertTrue(quick.attribution(it).contains("CC") || quick.attribution(it).contains("Creative Commons")) }
    }

    @Test
    fun offlineSearchIgnoresAccentsAndCitesVersionAndPage() {
        val corpora = listOf("srd_5_1_es.json", "srd_5_2_1_es.json").map { parseSrdCorpus(File(assets, it).readText()) }
        val hits = searchSrd(corpora, "cobertura tres cuartos")
        assertTrue(hits.isNotEmpty())
        assertTrue(hits.map { it.version }.toSet() == SrdVersion.entries.toSet(), "both versions answer")
        assertTrue(hits.all { normalizeForSearch(it.paragraph).contains("cobertura") && it.page > 0 })
        assertEquals(searchSrd(corpora, "visión ciega").size, searchSrd(corpora, "VISION CIEGA").size)
        assertTrue(searchSrd(corpora, "a").isEmpty(), "single letters are not a search")
    }

    private fun member(
        name: String,
        armorClass: Int,
        maxHp: Int,
        wisdom: Int,
        speed: Int,
        languages: List<String> = emptyList(),
        senses: List<CharacterSense> = emptyList(),
    ): PartyLensMember = PartyLensMember(
        sheet = CharacterSheet(
            id = Uuid.random(),
            campaignId = Uuid.random(),
            name = name,
            status = CharacterStatus.ACTIVE,
            updatedAtEpochSeconds = 0,
            strength = 10,
            dexterity = 10,
            constitution = 10,
            intelligence = 10,
            wisdom = wisdom,
            charisma = 10,
            armorClass = armorClass,
            maxHp = maxHp,
            currentHp = maxHp,
            tempHp = 0,
            initiativeAdjustment = 0,
            speed = speed,
            proficiencyBonus = 2,
            savingThrows = emptyList(),
            passivePerceptionAdjustment = 0,
            spellSaveDc = null,
            classes = listOf(CharacterClassLevel(Uuid.random(), "Guerrero", 3, 10, 3, 0)),
            skills = emptyList(),
            proficiencies = languages.mapIndexed { index, language ->
                CharacterProficiency(Uuid.random(), CharacterProficiencyType.LANGUAGE, language, sortOrder = index)
            },
        ),
        senses = senses,
    )
}
