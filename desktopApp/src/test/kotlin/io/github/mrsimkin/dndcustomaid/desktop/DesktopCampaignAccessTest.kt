package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/** QA 0.7.0-preqa.2, A2: an account that is not part of a campaign never gets the DM's tools. */
class DesktopCampaignAccessTest {
    private val campaign = Uuid.random()
    private val gmail = Uuid.random()
    private val outlook = Uuid.random()
    private val dmOnly = listOf(CampaignMembership(campaign, gmail, CampaignRole.DM))

    @Test
    fun anotherAccountsCampaignIsBlockedLikeAPlayersOne() {
        assertEquals(DesktopCampaignAccess.DM, desktopCampaignAccess(gmail, dmOnly))
        val outsider = desktopCampaignAccess(outlook, dmOnly)
        assertEquals(DesktopCampaignAccess.NOT_MEMBER, outsider)
        assertTrue(outsider.blocksDm)
        val player = desktopCampaignAccess(outlook, dmOnly + CampaignMembership(campaign, outlook, CampaignRole.PLAYER))
        assertEquals(DesktopCampaignAccess.PLAYER, player)
        assertTrue(player.blocksDm)
    }

    @Test
    fun aCampaignWithNobodyOnTheServerStaysLocal() {
        val local = desktopCampaignAccess(outlook, emptyList())
        assertEquals(DesktopCampaignAccess.LOCAL_ONLY, local)
        assertFalse(local.blocksDm)
    }
}
