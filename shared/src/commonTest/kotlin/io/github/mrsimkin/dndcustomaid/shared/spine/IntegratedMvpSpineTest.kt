package io.github.mrsimkin.dndcustomaid.shared.spine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class IntegratedMvpSpineTest {
    @Test
    fun campaignRoleAndPcAuthorityRemainDistinctConcepts() {
        val campaignId = Uuid.random()
        val dmAccountId = Uuid.random()
        val playerAccountId = Uuid.random()
        val characterId = Uuid.random()

        val dmMembership = CampaignMembership(campaignId, dmAccountId, CampaignRole.DM)
        val playerMembership = CampaignMembership(campaignId, playerAccountId, CampaignRole.PLAYER)
        val authority = PcAuthority(
            characterId = characterId,
            ownerAccountId = playerAccountId,
            controllerAccountId = dmAccountId,
        )

        assertEquals(CampaignRole.DM, dmMembership.role)
        assertEquals(CampaignRole.PLAYER, playerMembership.role)
        assertEquals(playerAccountId, authority.ownerAccountId)
        assertEquals(dmAccountId, authority.controllerAccountId)
        assertNotEquals(authority.ownerAccountId, authority.controllerAccountId)
    }

    @Test
    fun inactiveMembershipCannotUseHostedCampaign() {
        assertTrue(CampaignMembership(Uuid.random(), Uuid.random(), CampaignRole.PLAYER).canUseHostedCampaign)
        assertEquals(
            false,
            CampaignMembership(
                Uuid.random(),
                Uuid.random(),
                CampaignRole.PLAYER,
                CampaignMembershipStatus.KICKED,
            ).canUseHostedCampaign,
        )
    }

    @Test
    fun matchingRevisionAdvancesExactlyOnce() {
        val metadata = SyncMetadata(revision = Revision(7))
        val accepted = assertIs<RevisionDecision.Accepted>(metadata.checkMutation(Revision(7)))

        assertEquals(Revision(8), accepted.nextRevision)
    }

    @Test
    fun staleRevisionDoesNotProduceAnAcceptedWrite() {
        val metadata = SyncMetadata(revision = Revision(8))
        val stale = assertIs<RevisionDecision.Stale>(metadata.checkMutation(Revision(7)))

        assertEquals(Revision(7), stale.expected)
        assertEquals(Revision(8), stale.actual)
    }

    @Test
    fun tombstonedObjectCannotBeMutatedEvenAtMatchingRevision() {
        val metadata = SyncMetadata(revision = Revision(4), deletedAtEpochSeconds = 1234)
        val decision = metadata.checkMutation(Revision(4))

        assertIs<RevisionDecision.Deleted>(decision)
    }

    @Test
    fun revisionsRejectNegativeValues() {
        assertFailsWith<IllegalArgumentException> { Revision(-1) }
    }

    @Test
    fun personalToCampaignCopyGetsIndependentIdentityAndRevision() {
        val ownerId = Uuid.random()
        val sourceId = Uuid.random()
        val campaignId = Uuid.random()
        val copyId = Uuid.random()
        val source = ScopedObjectIdentity(
            id = sourceId,
            scope = ContentScope.Personal(ownerId),
            revision = Revision(9),
        )

        val copy = source.independentCampaignCopy(
            newId = copyId,
            campaignId = campaignId,
            copiedAtEpochSeconds = 500,
        )

        assertEquals(copyId, copy.id)
        assertEquals(ContentScope.Campaign(campaignId), copy.scope)
        assertEquals(Revision(0), copy.revision)
        assertEquals(sourceId, copy.provenance?.sourceObjectId)
        assertEquals(ContentScope.Personal(ownerId), copy.provenance?.sourceScope)
        assertEquals(500, copy.provenance?.copiedAtEpochSeconds)
    }

    @Test
    fun independentCopyCannotReuseSourceIdentity() {
        val sourceId = Uuid.random()
        val source = ScopedObjectIdentity(sourceId, ContentScope.Personal(Uuid.random()))

        assertFailsWith<IllegalArgumentException> {
            source.independentCampaignCopy(sourceId, Uuid.random())
        }
    }
}
