package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipantReadiness
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid

class DesktopEncounterManagerControllerTest {
    @Test
    fun encounterAuthoringSourcesFilteringDependencyCopyAndDeletePreserveSemantics() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-encounter-manager-")
        val handle = DesktopDatabaseFactory(tempDir.resolve("manager.db").toFile()).create()
        try {
            val database = handle.database
            val campaigns = CampaignRepository(database)
            val spine = IntegratedSpineRepository(database)
            val ownerId = Uuid.random()
            val campaign = campaigns.createCampaign("The Test Campaign")
            campaigns.setActiveCampaign(campaign.id)
            spine.upsertAccount(AccountIdentity(id = ownerId, displayName = "DM"))
            spine.upsertMembership(
                CampaignMembership(
                    campaignId = campaign.id,
                    accountId = ownerId,
                    role = CampaignRole.DM,
                ),
            )

            val creatureRepository = CreatureContentRepository(database)
            val npcRepository = NpcContentRepository(database)
            val personalCreature = creatureRepository.createPersonal(
                ownerAccountId = ownerId,
                rawDisplayName = "Canal Ghoul",
                payload = CreaturePayload(notes = "Personal source creature"),
                nowEpochSeconds = 600,
            )
            val personalNpc = npcRepository.createPersonal(
                ownerAccountId = ownerId,
                rawDisplayName = "Archivist Mara",
                payload = NpcPayload(conceptRole = "Archive guide"),
                nowEpochSeconds = 601,
            )
            creatureRepository.createCampaign(
                campaignId = campaign.id,
                rawDisplayName = "Campaign-only Sentry",
                payload = CreaturePayload(),
                nowEpochSeconds = 602,
            )

            var now = 700L
            val controller = DesktopEncounterManagerController(database) { now++ }
            assertEquals(ownerId, controller.personalOwnerAccountId())

            val personalSources = controller.participantSources(ContentScope.Personal(ownerId))
            assertEquals(
                setOf(personalCreature.item.identity.id, personalNpc.item.identity.id),
                personalSources.map { it.id }.toSet(),
            )
            assertEquals(
                setOf(ReusableContentFamily.CREATURE, ReusableContentFamily.NPC),
                personalSources.map { it.family }.toSet(),
            )

            val personal = controller.createPersonal(ownerId, "Archive Ambush")
            assertEquals(ContentScope.Personal(ownerId), personal.item.identity.scope)

            val payload = EncounterPayload(
                summary = "Canal ghouls corner the party around a frightened archivist.",
                environment = "Flooded stacks, raised balcony and west sluice wheel.",
                context = "Noise drew the ghouls from the old cistern.",
                dmGuidance = "Keep negotiation and retreat viable.",
                participants = listOf(
                    EncounterParticipant(
                        sourceContentId = personalCreature.item.identity.id,
                        label = "Canal ghouls",
                        quantity = 2,
                        readiness = EncounterParticipantReadiness.EXPECTED,
                    ),
                    EncounterParticipant(
                        sourceContentId = personalCreature.item.identity.id,
                        label = "Reserve ghoul",
                        readiness = EncounterParticipantReadiness.RESERVE,
                        notes = "Arrives only after sustained noise.",
                    ),
                    EncounterParticipant(
                        sourceContentId = personalNpc.item.identity.id,
                        label = "Archivist Mara",
                        readiness = EncounterParticipantReadiness.CONDITIONAL,
                        condition = "Present if Mara has not escaped.",
                        overrides = "Unwilling to fight.",
                    ),
                    EncounterParticipant(
                        label = "Dock thugs",
                        quantity = 3,
                        readiness = EncounterParticipantReadiness.RESERVE,
                    ),
                ),
                tags = listOf("archive", "canal", "ambush"),
                notes = "Prepared encounter, not live combat state.",
            )
            assertIs<RevisionDecision.Accepted>(
                controller.update(
                    id = personal.item.identity.id,
                    expectedRevision = personal.item.identity.revision,
                    displayName = "Flooded Archive Ambush",
                    payload = payload,
                ),
            )

            val updated = assertNotNull(controller.encounter(personal.item.identity.id))
            assertEquals("Flooded Archive Ambush", updated.item.displayName)
            assertEquals(payload, updated.payload)

            val filtered = filterDesktopEncounters(
                encounters = controller.personalEncounters(ownerId),
                filters = DesktopEncounterFilters(query = "sluice", tag = "ambush"),
            )
            assertEquals(listOf(updated.item.identity.id), filtered.map { it.item.identity.id })
            assertEquals(
                emptyList(),
                filterDesktopEncounters(controller.personalEncounters(ownerId), DesktopEncounterFilters(tag = "forest")),
            )

            val stale = controller.update(
                id = personal.item.identity.id,
                expectedRevision = personal.item.identity.revision,
                displayName = "Stale Rename",
                payload = EncounterPayload(summary = "Wrong"),
            )
            assertIs<RevisionDecision.Stale>(stale)
            assertEquals(payload, controller.encounter(personal.item.identity.id)?.payload)

            val copied = controller.copyPersonalToCampaign(personal.item.identity.id, campaign.id)
            assertNotEquals(personal.item.identity.id, copied.item.identity.id)
            assertEquals(ContentScope.Campaign(campaign.id), copied.item.identity.scope)
            assertEquals(personal.item.identity.id, copied.item.identity.provenance?.sourceObjectId)
            assertEquals(payload.copy(participants = copied.payload.participants), copied.payload)

            val copiedCreatureA = copied.payload.participants[0].sourceContentId
            val copiedCreatureB = copied.payload.participants[1].sourceContentId
            val copiedNpc = copied.payload.participants[2].sourceContentId
            assertEquals(copiedCreatureA, copiedCreatureB)
            assertNotEquals(personalCreature.item.identity.id, copiedCreatureA)
            assertNotEquals(personalNpc.item.identity.id, copiedNpc)
            assertEquals(null, copied.payload.participants[3].sourceContentId)
            assertEquals(1, controller.campaignEncounters(campaign.id).size)

            val campaignSources = controller.participantSources(ContentScope.Campaign(campaign.id))
            assertNotNull(campaignSources.firstOrNull { it.id == copiedCreatureA })
            assertNotNull(campaignSources.firstOrNull { it.id == copiedNpc })

            val changedPersonalPayload = payload.copy(notes = "Personal template changed later.")
            assertIs<RevisionDecision.Accepted>(
                controller.update(
                    id = updated.item.identity.id,
                    expectedRevision = updated.item.identity.revision,
                    displayName = updated.item.displayName,
                    payload = changedPersonalPayload,
                ),
            )
            assertEquals(changedPersonalPayload, controller.encounter(personal.item.identity.id)?.payload)
            assertEquals(copied.payload, controller.encounter(copied.item.identity.id)?.payload)

            val latestPersonal = assertNotNull(controller.encounter(personal.item.identity.id))
            val repository = EncounterContentRepository(database)
            val deleted = assertIs<RevisionDecision.Accepted>(
                repository.tombstone(
                    id = latestPersonal.item.identity.id,
                    expectedRevision = latestPersonal.item.identity.revision,
                    deletedAtEpochSeconds = now++,
                ),
            )
            assertIs<RevisionDecision.Deleted>(
                controller.update(
                    id = latestPersonal.item.identity.id,
                    expectedRevision = deleted.nextRevision,
                    displayName = "Resurrected Encounter",
                    payload = EncounterPayload(summary = "Must not return"),
                ),
            )
            assertEquals(null, controller.encounter(personal.item.identity.id))
            assertNotNull(controller.encounter(copied.item.identity.id))
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
