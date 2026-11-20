package io.github.mrsimkin.dndcustomaid.android

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Owner's C (2026-11-20): the tablet App had no tests at all. This is the first one, and it covers
 * what opens the App from an invitation link or a QR (owner's A).
 */
class InvitationLinkTest {
    @Test
    fun theAppsOwnLinkCarriesTheCode() {
        assertEquals("MARFIL", invitationCodeFromLink("dndcustomaid://unirme/MARFIL"))
    }

    @Test
    fun theLinkOfThePageCarriesTheCodeInAnyCase() {
        assertEquals("MARFIL", invitationCodeFromLink("https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/unirme/marfil"))
        assertEquals("MARFIL", invitationCodeFromLink("https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/unirme/MaRfIl/"))
    }

    @Test
    fun whatComesAfterTheCodeIsIgnored() {
        assertEquals("MARFIL", invitationCodeFromLink("https://ejemplo.test/unirme/MARFIL?de=whatsapp"))
        assertEquals("MARFIL", invitationCodeFromLink("https://ejemplo.test/unirme/MARFIL#arriba"))
    }

    @Test
    fun anythingThatIsNotSixLettersOpensNothing() {
        assertNull(invitationCodeFromLink("https://ejemplo.test/unirme/MARFI"))
        assertNull(invitationCodeFromLink("https://ejemplo.test/unirme/MARFIL7"))
        // The first run of this test caught the parser answering «UNIRME» here.
        assertNull(invitationCodeFromLink("https://ejemplo.test/unirme/"))
        assertNull(invitationCodeFromLink("https://ejemplo.test/unirme"))
        assertNull(invitationCodeFromLink("https://ejemplo.test/otra-cosa"))
        assertNull(invitationCodeFromLink(""))
        assertNull(invitationCodeFromLink(null))
    }
}
