package io.github.mrsimkin.dndcustomaid.android

/**
 * The six letters an invitation link or QR carries (owner's A, 2026-11-20). Plain text in and
 * plain text out, so it can be tested without a device (owner's C).
 *
 * It takes «dndcustomaid://unirme/MARFIL» and «https://…/unirme/marfil» alike, and answers null
 * for anything else, so a stray link never opens the join dialog. The first test written for this
 * App caught the original version answering «UNIRME» for a link that ended at «/unirme/».
 */
internal fun invitationCodeFromLink(link: String?): String? {
    val text = link?.trim().orEmpty()
    if (text.isEmpty()) return null
    val marker = "/unirme/"
    val at = text.lowercase().indexOf(marker)
    if (at < 0) return null
    val code = text.substring(at + marker.length)
        .substringBefore('?')
        .substringBefore('#')
        .substringBefore('/')
        .uppercase()
    return code.takeIf { Regex("^[A-Z]{6}$").matches(it) }
}
