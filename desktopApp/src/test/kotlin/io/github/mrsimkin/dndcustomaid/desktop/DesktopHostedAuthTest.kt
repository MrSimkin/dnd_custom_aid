package io.github.mrsimkin.dndcustomaid.desktop

import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopHostedAuthTest {
    @Test
    fun jwtWithComfortableRemainingLifetimeDoesNotNeedRefresh() {
        val now = 1_800_000_000L
        val jwt = jwtWithExpiry(now + 300)

        assertFalse(jwtExpiresSoon(jwt, nowEpochSeconds = now))
    }

    @Test
    fun jwtWithinRefreshWindowNeedsRefresh() {
        val now = 1_800_000_000L
        val jwt = jwtWithExpiry(now + 30)

        assertTrue(jwtExpiresSoon(jwt, nowEpochSeconds = now))
    }

    @Test
    fun malformedJwtFailsClosedIntoRefreshPath() {
        assertTrue(jwtExpiresSoon("not-a-jwt", nowEpochSeconds = 1_800_000_000L))
    }

    private fun jwtWithExpiry(expiry: Long): String {
        val header = encode("{\"alg\":\"none\"}")
        val payload = encode("{\"exp\":$expiry}")
        return "$header.$payload.signature"
    }

    private fun encode(value: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(Charsets.UTF_8))
}
