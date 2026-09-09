package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class CharacterSpellcastingOriginTest {
    @Test
    fun legacyLinkedClassSourceDefaultsToClassOrigin() {
        val classId = Uuid.random()
        val source = CharacterSpellcastingSource(
            id = Uuid.random(),
            name = "Paladín",
            linkedClassId = classId,
            sortOrder = 0,
        )
        assertEquals(CharacterSpellcastingOriginKind.CLASS, source.originKind)
        assertEquals(classId, source.linkedClassId)
        assertNull(source.originReferenceId)
    }

    @Test
    fun legacyUnlinkedSourceDefaultsToOtherOrigin() {
        val source = CharacterSpellcastingSource(
            id = Uuid.random(),
            name = "Fuente heredada",
            linkedClassId = null,
            sortOrder = 0,
        )
        assertEquals(CharacterSpellcastingOriginKind.OTHER, source.originKind)
    }

    @Test
    fun canonicalOriginKindsKeepOwnerOrder() {
        assertEquals(
            listOf(
                CharacterSpellcastingOriginKind.CLASS,
                CharacterSpellcastingOriginKind.TRAIT,
                CharacterSpellcastingOriginKind.RACE,
                CharacterSpellcastingOriginKind.BACKGROUND,
                CharacterSpellcastingOriginKind.FEAT,
                CharacterSpellcastingOriginKind.ITEM,
                CharacterSpellcastingOriginKind.MAGIC_ITEM,
                CharacterSpellcastingOriginKind.GIFT,
                CharacterSpellcastingOriginKind.OTHER,
            ),
            CharacterSpellcastingOriginKind.entries,
        )
    }
}
