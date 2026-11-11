package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Base para DM, package 5: «paquetes» (owner decision P19, D-0068 §13 Packages).
 *
 * A package is a deliberately simple, reusable bundle of mechanical patches applied with one tap
 * to a staged or live creature (or its whole group): pieces it adds (actions, traits, saves,
 * spells, reactions), named things it removes, and small numeric adjustments. It is not a class or
 * template engine; applying it never changes the base creature, and removing it undoes it.
 */

@Serializable
enum class PackagePieceKind {
    ACTION,
    TRAIT,
    SAVE,
    SPELL,
    REACTION,
}

@Serializable
data class PackagePiece(
    val kind: PackagePieceKind,
    val name: String,
    val text: String = "",
) {
    init {
        require(name.isNotBlank()) { "Package piece name must not be blank." }
    }
}

@Serializable
data class PackagePayload(
    val summary: String = "",
    val pieces: List<PackagePiece> = emptyList(),
    /** Names of things the package takes away (shown crossed out; the base text is never edited). */
    val removals: List<String> = emptyList(),
    val hpDelta: Int = 0,
    val armorClassDelta: Int = 0,
    val attackBonusDelta: Int = 0,
    val damageBonusDelta: Int = 0,
    val notes: String = "",
)

@Serializable
data class PackageContent(
    val item: ReusableContentItem,
    val payload: PackagePayload,
) {
    init {
        require(item.family == ReusableContentFamily.PACKAGE) { "PackageContent must wrap PACKAGE reusable content." }
    }
}

class PackageContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
) {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun createCampaign(campaignId: Uuid, rawDisplayName: String, payload: PackagePayload, nowEpochSeconds: Long, id: Uuid = Uuid.random()): PackageContent =
        create(payload) { reusableContent.createCampaign(campaignId, ReusableContentFamily.PACKAGE, rawDisplayName, nowEpochSeconds, id) }

    fun createPersonal(ownerAccountId: Uuid, rawDisplayName: String, payload: PackagePayload, nowEpochSeconds: Long, id: Uuid = Uuid.random()): PackageContent =
        create(payload) { reusableContent.createPersonal(ownerAccountId, ReusableContentFamily.PACKAGE, rawDisplayName, nowEpochSeconds, id) }

    fun pkg(id: Uuid, includeDeleted: Boolean = false): PackageContent? {
        val item = reusableContent.content(id, includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.PACKAGE) return null
        return PackageContent(item, requireNotNull(payload(id)) { "Package reusable content is missing its payload." })
    }

    fun listCampaign(campaignId: Uuid): List<PackageContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.PACKAGE).mapNotNull { pkg(it.identity.id) }

    fun listPersonal(ownerAccountId: Uuid): List<PackageContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.PACKAGE).mapNotNull { pkg(it.identity.id) }

    fun update(id: Uuid, expectedRevision: Revision, rawDisplayName: String, payload: PackagePayload, updatedAtEpochSeconds: Long): RevisionDecision {
        val name = rawDisplayName.trim()
        require(name.isNotEmpty()) { "Package display name must not be blank." }
        return reusableContent.mutateContent(id, expectedRevision, updatedAtEpochSeconds) { current ->
            require(current.family == ReusableContentFamily.PACKAGE) { "Package updates require PACKAGE reusable content." }
            database.reusableContentQueries.updateReusableContentName(name, updatedAtEpochSeconds, id.toString())
            updatePayloadRow(id, payload)
        }
    }

    fun tombstone(id: Uuid, expectedRevision: Revision, deletedAtEpochSeconds: Long): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) { "Package must exist before it can be deleted." }
        require(current.family == ReusableContentFamily.PACKAGE) { "Package deletion requires PACKAGE reusable content." }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    internal fun payload(id: Uuid): PackagePayload? =
        database.packagePayloadQueries.selectPackagePayloadByContentId(id.toString()).executeAsOneOrNull()
            ?.let { json.decodeFromString(PackagePayload.serializer(), it) }

    internal fun insertPayload(id: Uuid, payload: PackagePayload) {
        database.packagePayloadQueries.insertPackagePayload(id.toString(), json.encodeToString(PackagePayload.serializer(), payload))
    }

    internal fun updatePayloadRow(id: Uuid, payload: PackagePayload) {
        database.packagePayloadQueries.updatePackagePayload(json.encodeToString(PackagePayload.serializer(), payload), id.toString())
    }

    private fun create(payload: PackagePayload, createItem: () -> ReusableContentItem): PackageContent {
        var result: PackageContent? = null
        database.transaction {
            val item = createItem()
            insertPayload(item.identity.id, payload)
            result = PackageContent(item, payload)
        }
        return checkNotNull(result)
    }
}
