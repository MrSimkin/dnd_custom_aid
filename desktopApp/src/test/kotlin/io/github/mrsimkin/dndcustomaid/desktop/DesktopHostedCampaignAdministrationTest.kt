package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignMember
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignModerationAction
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DesktopHostedCampaignAdministrationTest {
    private val playerId = Uuid.parse("11111111-1111-4111-8111-111111111111")

    @Test
    fun activePlayerOffersKickAndBan() {
        val member = member(CampaignRole.PLAYER, CampaignMembershipStatus.ACTIVE)

        assertEquals(
            listOf(
                HostedCampaignModerationAction.KICK,
                HostedCampaignModerationAction.BAN,
            ),
            availableModerationActions(member),
        )
    }

    @Test
    fun kickedPlayerOnlyOffersBan() {
        val member = member(CampaignRole.PLAYER, CampaignMembershipStatus.KICKED)

        assertEquals(
            listOf(HostedCampaignModerationAction.BAN),
            availableModerationActions(member),
        )
    }

    @Test
    fun bannedPlayerOnlyOffersLiftBan() {
        val member = member(CampaignRole.PLAYER, CampaignMembershipStatus.BANNED)

        assertEquals(
            listOf(HostedCampaignModerationAction.LIFT_BAN),
            availableModerationActions(member),
        )
        assertTrue(
            HostedCampaignModerationAction.LIFT_BAN
                .confirmationText("Alyra")
                .contains("expulsado"),
        )
    }

    @Test
    fun pcAuthorityEligibilityUsesOnlyActiveMembersInDeterministicOrder() {
        val dmId = Uuid.parse("11111111-1111-4111-8111-111111111110")
        val secondPlayerId = Uuid.parse("11111111-1111-4111-8111-111111111112")
        val members = listOf(
            HostedCampaignMember(
                userId = playerId,
                displayName = "Zed",
                role = CampaignRole.PLAYER,
                status = CampaignMembershipStatus.ACTIVE,
            ),
            HostedCampaignMember(
                userId = Uuid.parse("11111111-1111-4111-8111-111111111113"),
                displayName = "Banned",
                role = CampaignRole.PLAYER,
                status = CampaignMembershipStatus.BANNED,
            ),
            HostedCampaignMember(
                userId = dmId,
                displayName = "Alyra",
                role = CampaignRole.DM,
                status = CampaignMembershipStatus.ACTIVE,
            ),
            HostedCampaignMember(
                userId = secondPlayerId,
                displayName = null,
                role = CampaignRole.PLAYER,
                status = CampaignMembershipStatus.ACTIVE,
            ),
        )

        assertEquals(
            listOf(secondPlayerId, dmId, playerId),
            eligiblePcAuthorityMembers(members).map { it.userId },
        )
    }

    @Test
    fun dmNeverReceivesPlayerModerationActions() {
        CampaignMembershipStatus.entries.forEach { status ->
            assertTrue(
                availableModerationActions(member(CampaignRole.DM, status)).isEmpty(),
                "DM must not receive moderation actions in state $status",
            )
        }
    }

    private fun member(
        role: CampaignRole,
        status: CampaignMembershipStatus,
    ): HostedCampaignMember = HostedCampaignMember(
        userId = playerId,
        displayName = "Alyra",
        role = role,
        status = status,
    )
}
