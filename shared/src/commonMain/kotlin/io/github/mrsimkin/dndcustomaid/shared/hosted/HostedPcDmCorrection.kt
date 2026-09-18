package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterReconciliationCheckpoint
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import kotlin.uuid.Uuid

private const val PC_DM_CORRECTION_SYNC_TYPE = "PC"

data class PcDmCorrectionPatch(
    val name: String,
    val status: CharacterStatus,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
    val armorClass: Int,
    val maxHp: Int,
    val currentHp: Int,
    val tempHp: Int,
    val initiativeAdjustment: Int,
    val speed: Int,
    val proficiencyBonusAdjustment: Int,
    val passivePerceptionAdjustment: Int,
    val inspiration: Boolean,
    val generalNotes: String,
) {
    fun applyTo(current: CharacterSheet): CharacterSheet = current.copy(
        name = name.trim(),
        status = status,
        strength = strength,
        dexterity = dexterity,
        constitution = constitution,
        intelligence = intelligence,
        wisdom = wisdom,
        charisma = charisma,
        armorClass = armorClass,
        maxHp = maxHp,
        currentHp = currentHp,
        tempHp = tempHp,
        initiativeAdjustment = initiativeAdjustment,
        speed = speed,
        proficiencyBonusAdjustment = proficiencyBonusAdjustment,
        inspiration = inspiration,
        passivePerceptionAdjustment = passivePerceptionAdjustment,
        generalNotes = generalNotes,
    )

    companion object {
        fun from(character: CharacterSheet): PcDmCorrectionPatch = PcDmCorrectionPatch(
            name = character.name,
            status = character.status,
            strength = character.strength,
            dexterity = character.dexterity,
            constitution = character.constitution,
            intelligence = character.intelligence,
            wisdom = character.wisdom,
            charisma = character.charisma,
            armorClass = character.armorClass,
            maxHp = character.maxHp,
            currentHp = character.currentHp,
            tempHp = character.tempHp,
            initiativeAdjustment = character.initiativeAdjustment,
            speed = character.speed,
            proficiencyBonusAdjustment = character.proficiencyBonusAdjustment,
            passivePerceptionAdjustment = character.passivePerceptionAdjustment,
            inspiration = character.inspiration,
            generalNotes = character.generalNotes,
        )
    }
}

data class QueuedPcDmCorrection(
    val character: CharacterSheet,
    val checkpoint: CharacterReconciliationCheckpoint,
    val mutation: HostedOutboxMutation,
)

class HostedPcDmCorrectionService(
    private val database: AppDatabase,
    private val characters: CharacterRepository = CharacterRepository(database),
    private val closure: CharacterClosureRepository = CharacterClosureRepository(database),
    private val backups: CharacterBackupRepository = CharacterBackupRepository(database),
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
    private val outbox: HostedOutboxRepository = HostedOutboxRepository(database),
) {
    fun correct(
        characterId: Uuid,
        dmAccountId: Uuid,
        patch: PcDmCorrectionPatch,
        rawReason: String,
        correctedAtEpochSeconds: Long,
        correctionId: Uuid = Uuid.random(),
        mutationId: Uuid = Uuid.random(),
    ): QueuedPcDmCorrection {
        val reason = rawReason.trim()
        require(reason.isNotEmpty()) { "DM correction reason must not be blank." }
        require(correctedAtEpochSeconds >= 0) { "DM correction time must not be negative." }

        return database.transactionWithResult {
            val current = requireNotNull(characters.character(characterId)) {
                "PC must already exist locally before it can be corrected."
            }
            val membership = spine.membership(current.campaignId, dmAccountId)
            require(
                membership?.role == CampaignRole.DM &&
                    membership.status == CampaignMembershipStatus.ACTIVE,
            ) {
                "DM correction requires an active DM membership in the PC campaign."
            }

            val syncMetadata = spine.syncMetadata(PC_DM_CORRECTION_SYNC_TYPE, characterId)
            require(!syncMetadata.isDeleted) {
                "A locally tombstoned PC cannot be corrected."
            }
            require(
                outbox.allMutations().none {
                    it.type == HostedMutationType.PC_SNAPSHOT_PUT && it.objectId == characterId
                },
            ) {
                "Resolve the existing hosted PC mutation before applying another DM correction."
            }

            val candidate = patch.applyTo(current)
            require(candidate != current) {
                "DM correction must change at least one supported PC field."
            }
            require(candidate.id == current.id && candidate.campaignId == current.campaignId) {
                "DM correction cannot change PC or campaign identity."
            }

            val saved = characters.saveCharacter(candidate)
            val changeSummary = summarizePcDmCorrection(current, saved)
            val previousClosure = closure.state(characterId)
            val checkpoint = CharacterReconciliationCheckpoint(
                id = correctionId,
                createdAtEpochSeconds = correctedAtEpochSeconds,
                characterUpdatedAtEpochSeconds = saved.updatedAtEpochSeconds,
                label = "Corrección DM",
                notes = buildString {
                    append("Motivo: ")
                    append(reason)
                    append("\nCambios: ")
                    append(changeSummary)
                    append("\nRevisión sincronizada base: ")
                    append(syncMetadata.revision.value)
                },
            )
            val savedClosure = closure.saveState(
                characterId,
                previousClosure.copy(
                    reconciliationCheckpoints = previousClosure.reconciliationCheckpoints + checkpoint,
                ),
            )
            val persistedCheckpoint = requireNotNull(
                savedClosure.reconciliationCheckpoints.firstOrNull { it.id == correctionId },
            )

            val snapshot = backups.exportCharacter(
                characterId = characterId,
                exportedAtEpochSeconds = correctedAtEpochSeconds,
            )
            val mutation = outbox.enqueuePcSnapshot(
                campaignId = saved.campaignId,
                pcId = saved.id,
                expectedRevision = syncMetadata.revision.value,
                snapshot = snapshot,
                createdAtEpochSeconds = correctedAtEpochSeconds,
                mutationId = mutationId,
            )

            QueuedPcDmCorrection(
                character = saved,
                checkpoint = persistedCheckpoint,
                mutation = mutation,
            )
        }
    }
}

private fun summarizePcDmCorrection(
    before: CharacterSheet,
    after: CharacterSheet,
): String = buildList {
    if (before.name != after.name) add("nombre: «${before.name}» → «${after.name}»")
    if (before.status != after.status) add("estado: ${before.status.name} → ${after.status.name}")
    if (before.strength != after.strength) add("FUE: ${before.strength} → ${after.strength}")
    if (before.dexterity != after.dexterity) add("DES: ${before.dexterity} → ${after.dexterity}")
    if (before.constitution != after.constitution) add("CON: ${before.constitution} → ${after.constitution}")
    if (before.intelligence != after.intelligence) add("INT: ${before.intelligence} → ${after.intelligence}")
    if (before.wisdom != after.wisdom) add("SAB: ${before.wisdom} → ${after.wisdom}")
    if (before.charisma != after.charisma) add("CAR: ${before.charisma} → ${after.charisma}")
    if (before.armorClass != after.armorClass) add("CA: ${before.armorClass} → ${after.armorClass}")
    if (before.maxHp != after.maxHp) add("PG máx.: ${before.maxHp} → ${after.maxHp}")
    if (before.currentHp != after.currentHp) add("PG actuales: ${before.currentHp} → ${after.currentHp}")
    if (before.tempHp != after.tempHp) add("PG temp.: ${before.tempHp} → ${after.tempHp}")
    if (before.initiativeAdjustment != after.initiativeAdjustment) {
        add("ajuste iniciativa: ${before.initiativeAdjustment} → ${after.initiativeAdjustment}")
    }
    if (before.speed != after.speed) add("velocidad: ${before.speed} → ${after.speed}")
    if (before.proficiencyBonusAdjustment != after.proficiencyBonusAdjustment) {
        add("ajuste competencia: ${before.proficiencyBonusAdjustment} → ${after.proficiencyBonusAdjustment}")
    }
    if (before.passivePerceptionAdjustment != after.passivePerceptionAdjustment) {
        add("ajuste Percepción Pasiva: ${before.passivePerceptionAdjustment} → ${after.passivePerceptionAdjustment}")
    }
    if (before.inspiration != after.inspiration) add("inspiración: ${before.inspiration} → ${after.inspiration}")
    if (before.generalNotes != after.generalNotes) add("notas generales modificadas")
}.joinToString("; ")
