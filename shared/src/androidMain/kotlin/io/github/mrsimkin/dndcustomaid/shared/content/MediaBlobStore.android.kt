package io.github.mrsimkin.dndcustomaid.shared.content

import java.io.File
import java.security.MessageDigest

actual fun sha256Hex(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

/** Files under [root] named by checksum; writes go through a temporary file so none is half-written. */
class FileMediaBlobStore(private val root: File) : MediaBlobStore {
    private val sha = Regex("^[0-9a-f]{64}$")

    override fun has(sha256: String): Boolean = file(sha256)?.isFile == true

    override fun read(sha256: String): ByteArray? = file(sha256)?.takeIf { it.isFile }?.readBytes()

    override fun write(bytes: ByteArray): String {
        require(bytes.isNotEmpty() && bytes.size <= HANDOUT_MAX_BYTES) { "Image file is empty or larger than 5 MiB." }
        val digest = sha256Hex(bytes)
        val target = requireNotNull(file(digest))
        if (!target.isFile) {
            root.mkdirs()
            val temp = File(root, "$digest.tmp")
            temp.writeBytes(bytes)
            if (!temp.renameTo(target)) {
                temp.copyTo(target, overwrite = true)
                temp.delete()
            }
        }
        return digest
    }

    override fun path(sha256: String): String? = file(sha256)?.takeIf { it.isFile }?.absolutePath

    private fun file(sha256: String): File? = if (sha.matches(sha256)) File(root, sha256) else null
}
