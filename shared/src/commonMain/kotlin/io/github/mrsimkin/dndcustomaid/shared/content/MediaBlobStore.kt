package io.github.mrsimkin.dndcustomaid.shared.content

/**
 * Device store for image files, addressed by their SHA-256 (package 4). The same image is stored
 * once however many handouts use it, and a file is never edited: a new image is a new checksum.
 */
interface MediaBlobStore {
    fun has(sha256: String): Boolean

    fun read(sha256: String): ByteArray?

    /** Stores [bytes] and returns their SHA-256. Rejects files larger than [HANDOUT_MAX_BYTES]. */
    fun write(bytes: ByteArray): String

    /** Platform path of a stored file, for image viewers; null when missing. */
    fun path(sha256: String): String?
}

/** Lowercase hex SHA-256 of [bytes]. */
expect fun sha256Hex(bytes: ByteArray): String

/** In-memory store for tests and previews. */
class InMemoryMediaBlobStore : MediaBlobStore {
    private val blobs = linkedMapOf<String, ByteArray>()

    override fun has(sha256: String): Boolean = sha256 in blobs

    override fun read(sha256: String): ByteArray? = blobs[sha256]?.copyOf()

    override fun write(bytes: ByteArray): String {
        require(bytes.isNotEmpty() && bytes.size <= HANDOUT_MAX_BYTES) { "Image file is empty or larger than 5 MiB." }
        val sha = sha256Hex(bytes)
        blobs.getOrPut(sha) { bytes.copyOf() }
        return sha
    }

    override fun path(sha256: String): String? = null
}
