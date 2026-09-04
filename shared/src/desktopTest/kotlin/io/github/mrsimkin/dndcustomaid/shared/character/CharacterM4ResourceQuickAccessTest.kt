package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class CharacterM4ResourceQuickAccessTest {
    @Test
    fun resourceFavoriteAddRemoveIsIdempotentAndPreservesOtherKinds() {
        val combatId = Uuid.random()
        val resourceId = Uuid.random()
        val initial = listOf(
            CharacterQuickAccessRef(CharacterQuickAccessKind.COMBAT_ENTRY, combatId, 4),
        )

        val favorited = setCharacterQuickAccessFavorite(
            quickAccess = initial,
            kind = CharacterQuickAccessKind.RESOURCE,
            targetId = resourceId,
            favorite = true,
        )
        assertEquals(
            listOf(CharacterQuickAccessKind.COMBAT_ENTRY, CharacterQuickAccessKind.RESOURCE),
            favorited.map { it.kind },
        )
        assertEquals(listOf(0, 1), favorited.map { it.sortOrder })

        val favoritedAgain = setCharacterQuickAccessFavorite(
            quickAccess = favorited,
            kind = CharacterQuickAccessKind.RESOURCE,
            targetId = resourceId,
            favorite = true,
        )
        assertEquals(favorited, favoritedAgain)

        val removed = setCharacterQuickAccessFavorite(
            quickAccess = favoritedAgain,
            kind = CharacterQuickAccessKind.RESOURCE,
            targetId = resourceId,
            favorite = false,
        )
        assertEquals(1, removed.size)
        assertEquals(CharacterQuickAccessKind.COMBAT_ENTRY, removed.single().kind)
        assertEquals(combatId, removed.single().targetId)
        assertEquals(0, removed.single().sortOrder)
    }

    @Test
    fun resourcePruningDropsOnlyMissingResourceTargetsAndDensifiesOrder() {
        val liveResourceId = Uuid.random()
        val removedResourceId = Uuid.random()
        val spellId = Uuid.random()
        val initial = listOf(
            CharacterQuickAccessRef(CharacterQuickAccessKind.RESOURCE, removedResourceId, 7),
            CharacterQuickAccessRef(CharacterQuickAccessKind.SPELL, spellId, 1),
            CharacterQuickAccessRef(CharacterQuickAccessKind.RESOURCE, liveResourceId, 4),
        )

        val pruned = pruneCharacterQuickAccessKind(
            quickAccess = initial,
            kind = CharacterQuickAccessKind.RESOURCE,
            liveTargetIds = setOf(liveResourceId),
        )

        assertEquals(2, pruned.size)
        assertEquals(listOf(CharacterQuickAccessKind.SPELL, CharacterQuickAccessKind.RESOURCE), pruned.map { it.kind })
        assertEquals(listOf(spellId, liveResourceId), pruned.map { it.targetId })
        assertEquals(listOf(0, 1), pruned.map { it.sortOrder })
    }
}
