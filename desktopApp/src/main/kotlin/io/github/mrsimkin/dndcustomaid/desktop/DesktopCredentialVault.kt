package io.github.mrsimkin.dndcustomaid.desktop

import com.sun.jna.platform.win32.Crypt32Util
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * The account the App de PC remembers between launches (owner decision D2, 2026-11-13:
 * «al abrirse, y con "recordar" automático»).
 *
 * Only the refresh token and the email the owner typed are kept. The email is shown in the
 * account menu; the token never leaves this vault except to renew the session with Descope.
 */
internal data class RememberedLogin(
    val email: String,
    val refreshJwt: String,
)

internal interface DesktopCredentialVault {
    /** True when this computer can keep the account between launches. */
    val canRemember: Boolean

    fun load(): RememberedLogin?

    fun save(login: RememberedLogin)

    fun clear()
}

/**
 * Windows: the login is encrypted with DPAPI for the current Windows user (CryptProtectData), so
 * the file is useless to another user or on another computer. It is never written in plain text.
 */
internal class WindowsDpapiCredentialVault(private val file: File) : DesktopCredentialVault {
    override val canRemember: Boolean = true

    override fun load(): RememberedLogin? {
        if (!file.isFile) return null
        return runCatching {
            val plain = Crypt32Util.cryptUnprotectData(file.readBytes())
            val json = Json.parseToJsonElement(plain.toString(Charsets.UTF_8)).jsonObject
            RememberedLogin(
                email = requireNotNull(json["email"]).jsonPrimitive.content,
                refreshJwt = requireNotNull(json["refreshJwt"]).jsonPrimitive.content,
            )
        }.getOrElse {
            // Unreadable (another Windows user, corrupted file): forget it instead of failing the start.
            clear()
            null
        }
    }

    override fun save(login: RememberedLogin) {
        file.parentFile?.mkdirs()
        val plain = buildJsonObject {
            put("email", JsonPrimitive(login.email))
            put("refreshJwt", JsonPrimitive(login.refreshJwt))
        }.toString().toByteArray(Charsets.UTF_8)
        val protected = Crypt32Util.cryptProtectData(plain)
        val temporary = File(file.parentFile, "${file.name}.tmp")
        temporary.writeBytes(protected)
        Files.move(
            temporary.toPath(),
            file.toPath(),
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.ATOMIC_MOVE,
        )
    }

    override fun clear() {
        file.delete()
    }
}

/** Other systems: no protected store available, so the account lasts only while the App is open. */
internal object SessionOnlyCredentialVault : DesktopCredentialVault {
    override val canRemember: Boolean = false

    override fun load(): RememberedLogin? = null

    override fun save(login: RememberedLogin) = Unit

    override fun clear() = Unit
}

/** Tests: keeps the login in memory. */
internal class InMemoryCredentialVault(var stored: RememberedLogin? = null) : DesktopCredentialVault {
    override val canRemember: Boolean = true

    override fun load(): RememberedLogin? = stored

    override fun save(login: RememberedLogin) {
        stored = login
    }

    override fun clear() {
        stored = null
    }
}

internal fun defaultDesktopCredentialVault(directory: File): DesktopCredentialVault =
    if (System.getProperty("os.name").orEmpty().startsWith("Windows", ignoreCase = true)) {
        WindowsDpapiCredentialVault(File(directory, "account.vault"))
    } else {
        SessionOnlyCredentialVault
    }
