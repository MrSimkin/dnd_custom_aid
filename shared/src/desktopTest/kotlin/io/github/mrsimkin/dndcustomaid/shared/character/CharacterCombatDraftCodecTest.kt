package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Owner's C (2026-11-20). This codec used to live in the tablet App, where nothing could test it,
 * and the owner's QA of 0.7.0-preqa.4 found it losing the weapon an attack was linked to.
 */
class CharacterCombatDraftCodecTest {
    private val axe = Uuid.parse("40000000-0000-4000-8000-000000000001")
    private val fireball = Uuid.parse("40000000-0000-4000-8000-000000000002")

    private fun attack(
        name: String,
        linkedItemId: Uuid? = null,
        linkedSpellId: Uuid? = null,
        pinned: Boolean = false,
    ) = CharacterCombatEntry(
        id = Uuid.random(),
        name = name,
        type = CharacterCombatEntryType.ATTACK,
        attackModifier = 6,
        damageEffect = "1d12 + 3 cortante",
        rangeText = "cuerpo a cuerpo",
        notes = "a dos manos",
        sortOrder = 0,
        pinned = pinned,
        linkedItemId = linkedItemId,
        linkedSpellId = linkedSpellId,
    )

    @Test
    fun whatGoesInComesBackOut() {
        val entries = listOf(
            attack("Hacha a dos manos", linkedItemId = axe, pinned = true),
            attack("Bola de fuego", linkedSpellId = fireball),
        )

        val back = combatEntriesFromJson(combatEntriesToJson(entries))

        assertEquals(entries.map { it.name }, back.map { it.name })
        assertEquals(axe, back[0].linkedItemId, "the weapon link survives the draft (QA 0.7.0-preqa.4)")
        assertEquals(fireball, back[1].linkedSpellId, "and so does the spell link")
        assertEquals(true, back[0].pinned)
        assertEquals(6, back[0].attackModifier)
        assertEquals("1d12 + 3 cortante", back[0].damageEffect)
        assertEquals("cuerpo a cuerpo", back[0].rangeText)
        assertEquals("a dos manos", back[0].notes)
    }

    @Test
    fun theOrderOnScreenIsTheOrderThatIsKept() {
        val entries = listOf(attack("Primero"), attack("Segundo"), attack("Tercero"))

        val back = combatEntriesFromJson(combatEntriesToJson(entries))

        assertEquals(listOf(0, 1, 2), back.map { it.sortOrder })
        assertEquals(listOf("Primero", "Segundo", "Tercero"), back.map { it.name })
    }

    @Test
    fun anAttackWithoutLinksKeepsThemEmpty() {
        val back = combatEntriesFromJson(combatEntriesToJson(listOf(attack("Puñetazo")))).single()

        assertNull(back.linkedItemId)
        assertNull(back.linkedSpellId)
    }

    @Test
    fun somethingUnreadableLeavesThePcWithoutAttacksInsteadOfBreakingTheTable() {
        assertTrue(combatEntriesFromJson("no es json").isEmpty())
        assertTrue(combatEntriesFromJson("").isEmpty())
        assertTrue(combatEntriesFromJson("{}").isEmpty())
    }

    @Test
    fun aDraftFromAnotherVersionKeepsWhatItCanRead() {
        // An attack written by a newer App, with a field this one does not know, and one whose
        // kind this version has never heard of.
        val raw = """
            [
              {"id":"${Uuid.random()}","name":"Garras","type":"ATTACK","damageEffect":"1d6","futuro":"algo"},
              {"id":"${Uuid.random()}","name":"Rugido","type":"UNA_COSA_NUEVA","damageEffect":""},
              {"id":"no-es-un-id","name":"Roto","type":"ATTACK","damageEffect":""}
            ]
        """.trimIndent()

        val back = combatEntriesFromJson(raw)

        assertEquals(listOf("Garras"), back.map { it.name })
    }
}
