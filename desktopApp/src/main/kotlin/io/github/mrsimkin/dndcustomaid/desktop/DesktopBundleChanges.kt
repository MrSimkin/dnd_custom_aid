package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundle
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleImporter
import io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleParts
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotCodec
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotDocument
import java.io.File
import java.nio.file.Files
import kotlin.uuid.Uuid

/*
 * «Cambios del paquete» (owner point 12 of the critical review): loading a package again never
 * updated what the DM had edited, and there was no way to see what had gone stale.
 *
 * The package is loaded into a scratch copy of the App's own database, with the same campaign, so
 * the comparison uses the real importer instead of a second guess at what it would write. Then,
 * object by object, the DM keeps theirs or takes the package's.
 */

internal enum class BundleChangeKind { NUEVO, IGUAL, DIFERENTE }

internal data class BundleChange(
    val id: Uuid,
    val family: String,
    val name: String,
    val kind: BundleChangeKind,
    /** The fields where the package and what you have disagree. */
    val fields: List<String> = emptyList(),
)

internal class BundleComparison(
    val changes: List<BundleChange>,
    private val theirs: Map<Uuid, DmContentSnapshotDocument>,
) {
    val news: List<BundleChange> get() = changes.filter { it.kind == BundleChangeKind.NUEVO }
    val different: List<BundleChange> get() = changes.filter { it.kind == BundleChangeKind.DIFERENTE }
    val same: Int get() = changes.count { it.kind == BundleChangeKind.IGUAL }

    /** Takes the package's version for the chosen objects. What is not chosen stays as it is. */
    fun useTheirs(database: AppDatabase, ids: Set<Uuid>, nowEpochSeconds: Long): Int {
        val codec = DmContentSnapshotCodec(database)
        var applied = 0
        database.transaction {
            ids.forEach { id ->
                val document = theirs[id] ?: return@forEach
                codec.applyHostedState(
                    document.copy(content = document.content.copy(updatedAtEpochSeconds = nowEpochSeconds)),
                    hostedDeletedAtEpochSeconds = null,
                )
                applied += 1
            }
        }
        return applied
    }

    fun spanishSummary(): String = buildString {
        append("${news.size} nuevos, ${different.size} distintos de lo tuyo, $same iguales.")
    }
}

/** Compares a package with the open campaign without touching anything. */
internal fun compareBundleWithCampaign(
    database: AppDatabase,
    bundle: ContentBundle,
    campaignId: Uuid,
    campaignName: String,
    ownerAccountId: Uuid?,
    readFile: (String) -> ByteArray?,
    nowEpochSeconds: Long,
): BundleComparison {
    val scratchDirectory = Files.createTempDirectory("dnd-custom-aid-bundle-diff-").toFile()
    val scratch = DesktopDatabaseFactory(File(scratchDirectory, "diff.db")).create()
    try {
        CampaignRepository(scratch.database).upsertCampaign(campaignId, campaignName)
        ContentBundleImporter(scratch.database, InMemoryMediaBlobStore(), { nowEpochSeconds })
            .load(bundle, campaignId, ContentBundleParts.DM_CONTENT, ownerAccountId, readFile)

        val theirCodec = DmContentSnapshotCodec(scratch.database)
        val mineCodec = DmContentSnapshotCodec(database)
        val changes = mutableListOf<BundleChange>()
        val documents = mutableMapOf<Uuid, DmContentSnapshotDocument>()

        ReusableContentRepository(scratch.database).listCampaign(campaignId).forEach { item ->
            val id = item.identity.id
            val their = theirCodec.export(id) ?: return@forEach
            documents[id] = their
            val mine = mineCodec.export(id)
            // Only what the DM would notice: the name and the content. When the package was loaded
            // and when it was written are not a difference.
            val kind = when {
                mine == null -> BundleChangeKind.NUEVO
                mine.content.displayName == their.content.displayName && samePayload(mine, their) -> BundleChangeKind.IGUAL
                else -> BundleChangeKind.DIFERENTE
            }
            changes += BundleChange(
                id = id,
                family = item.family.name,
                name = item.displayName,
                kind = kind,
                fields = if (kind == BundleChangeKind.DIFERENTE && mine != null) differingFields(mine, their) else emptyList(),
            )
        }
        return BundleComparison(changes.sortedWith(compareBy({ it.kind.ordinal }, { it.name })), documents)
    } finally {
        scratch.close()
        scratchDirectory.deleteRecursively()
    }
}

private fun samePayload(mine: DmContentSnapshotDocument, their: DmContentSnapshotDocument): Boolean =
    canonical(mine) == canonical(their)

private fun canonical(document: DmContentSnapshotDocument): List<String> =
    document.payload.entries.sortedBy { it.key }.map { "${it.key}=${it.value}" }

/** Which parts of the object disagree, in the words of the stored fields. */
private fun differingFields(mine: DmContentSnapshotDocument, their: DmContentSnapshotDocument): List<String> {
    val keys = (mine.payload.keys + their.payload.keys).sorted()
    return keys.filter { key -> mine.payload[key]?.toString() != their.payload[key]?.toString() }
        .map(::spanishField)
        .distinct()
}

private fun spanishField(key: String): String = when (key) {
    "conceptRole" -> "concepto"
    "appearanceFirstImpression" -> "apariencia"
    "personalityManner" -> "personalidad"
    "wantsFearsNeeds" -> "lo que quiere"
    "secrets" -> "secretos"
    "dmGuidance" -> "guía"
    "notes" -> "notas"
    "summary" -> "resumen"
    "presentation" -> "presentación"
    "hooks" -> "ganchos"
    "playerSafeText" -> "texto para jugadores"
    "dmNotes" -> "notas del DM"
    "purpose" -> "para qué sirve"
    "sections" -> "secciones"
    "participants" -> "criaturas"
    "links" -> "enlaces"
    "armorClass" -> "CA"
    "hitPoints" -> "PV"
    "actions" -> "acciones"
    else -> key
}
