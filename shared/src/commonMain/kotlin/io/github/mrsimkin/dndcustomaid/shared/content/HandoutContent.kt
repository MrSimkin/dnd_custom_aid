package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

/*
 * Base para DM, package 4: images the DM prepares (handouts, maps, NPC portraits).
 *
 * The image itself is a content-addressed file (SHA-256) kept in the device's media store and,
 * once synchronized, in the hosted media store (Workers KV, owner decision P27). The payload here
 * is only its description, and it travels with the rest of the DM content (package 1).
 */

@Serializable
enum class HandoutKind {
    HANDOUT,
    MAP,
    NPC_PORTRAIT,
}

@Serializable
enum class HandoutVisibility {
    /** Only the DM sees it (default). */
    DM_ONLY,

    /** Safe to show the players (shown full screen on the DM tablet, P3). */
    PLAYER_SAFE,
}

/** Image formats the App accepts; the App de PC converts anything else before saving. */
val HANDOUT_MEDIA_TYPES: Set<String> = setOf("image/jpeg", "image/png", "image/webp")

/** Largest image file the App stores or uploads (the App de PC downsizes before this). */
const val HANDOUT_MAX_BYTES: Long = 5L * 1024 * 1024

private val HANDOUT_SHA256_HEX = Regex("^[0-9a-f]{64}$")

@Serializable
data class HandoutPayload(
    val kind: HandoutKind = HandoutKind.HANDOUT,
    val mediaType: String,
    val sizeBytes: Long,
    val sha256: String,
    val width: Int? = null,
    val height: Int? = null,
    val visibility: HandoutVisibility = HandoutVisibility.DM_ONLY,
    val caption: String = "",
    val notes: String = "",
    /**
     * Redesign A7 (owner observation G2): the DM sent it to the players; their App shows it. Only
     * meaningful for PLAYER_SAFE handouts: a DM_ONLY handout is never sent.
     */
    val sharedWithPlayers: Boolean = false,
) {
    init {
        require(mediaType in HANDOUT_MEDIA_TYPES) { "Handout image type $mediaType is not supported." }
        require(sizeBytes in 1..HANDOUT_MAX_BYTES) { "Handout image size must be between 1 byte and 5 MiB." }
        require(HANDOUT_SHA256_HEX.matches(sha256)) { "Handout image checksum must be a lowercase SHA-256 hex string." }
        require(width == null || width > 0) { "Handout image width must be positive." }
        require(height == null || height > 0) { "Handout image height must be positive." }
    }

}

@Serializable
data class HandoutContent(
    val item: ReusableContentItem,
    val payload: HandoutPayload,
) {
    init {
        require(item.family == ReusableContentFamily.HANDOUT) {
            "HandoutContent must wrap HANDOUT reusable content."
        }
    }
}
