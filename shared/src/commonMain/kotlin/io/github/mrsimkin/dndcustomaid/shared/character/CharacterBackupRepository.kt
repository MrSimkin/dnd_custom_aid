package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/**
 * Repository-level bridge for the app-owned character backup format.
 *
 * V1 import semantics are deliberately restore-as-copy: importing never targets an existing
 * character row and therefore cannot silently overwrite one. The entire restore is wrapped in a
 * database transaction so a stricter persistence invariant cannot leave a placeholder behind.
 */
class CharacterBackupRepository(
    private val database: AppDatabase,
) {
    private val characters = CharacterRepository(database)
    private val closure = CharacterClosureRepository(database)

    fun exportCharacter(
        characterId: Uuid,
        exportedAtEpochSeconds: Long,
    ): CharacterBackupDocument {
        require(exportedAtEpochSeconds >= 0) { "Backup export time must not be negative." }
        val character = requireNotNull(characters.character(characterId)) {
            "Character must already exist locally."
        }
        val document = CharacterBackupDocument(
            exportedAtEpochSeconds = exportedAtEpochSeconds,
            character = character,
            closureState = closure.state(characterId),
        )
        val validation = characterBackupValidationMessage(document)
        require(validation == null) { validation ?: "Invalid character backup." }
        return document
    }

    fun importAsCopy(
        document: CharacterBackupDocument,
        destinationCampaignId: Uuid,
        importedAtEpochSeconds: Long,
        idFactory: () -> Uuid = { Uuid.random() },
    ): CharacterBackupImportResult {
        require(importedAtEpochSeconds >= 0) { "Backup import time must not be negative." }
        val validation = characterBackupValidationMessage(document)
        require(validation == null) { validation ?: "Invalid character backup." }

        var result: CharacterBackupImportResult? = null
        database.transaction {
            // Creating the row first lets the existing authoritative repositories enforce all of
            // their normal local persistence rules. The outer transaction guarantees rollback if
            // either aggregate rejects the candidate afterwards.
            val placeholder = characters.createCharacter(
                campaignId = destinationCampaignId,
                rawName = document.character.name,
            )
            val plan = prepareCharacterBackupImport(
                document = document,
                destinationCampaignId = destinationCampaignId,
                targetCharacterId = placeholder.id,
                idFactory = idFactory,
            )
            val savedCharacter = characters.saveCharacter(plan.character)
            val importCheckpoint = CharacterReconciliationCheckpoint(
                id = idFactory(),
                createdAtEpochSeconds = importedAtEpochSeconds,
                characterUpdatedAtEpochSeconds = savedCharacter.updatedAtEpochSeconds,
                label = "Importado desde respaldo",
                notes = "Respaldo v${document.version} · personaje de origen ${document.character.id}",
            )
            val savedClosure = closure.saveState(
                savedCharacter.id,
                plan.closureState.copy(
                    reconciliationCheckpoints = plan.closureState.reconciliationCheckpoints + importCheckpoint,
                ),
            )
            result = CharacterBackupImportResult(
                sourceCharacterId = plan.sourceCharacterId,
                sourceCampaignId = plan.sourceCampaignId,
                character = savedCharacter,
                closureState = savedClosure,
                importCheckpoint = requireNotNull(
                    savedClosure.reconciliationCheckpoints.firstOrNull { it.id == importCheckpoint.id },
                ),
            )
        }
        return requireNotNull(result)
    }
}

data class CharacterBackupImportResult(
    val sourceCharacterId: Uuid,
    val sourceCampaignId: Uuid,
    val character: CharacterSheet,
    val closureState: CharacterClosureState,
    val importCheckpoint: CharacterReconciliationCheckpoint,
)
