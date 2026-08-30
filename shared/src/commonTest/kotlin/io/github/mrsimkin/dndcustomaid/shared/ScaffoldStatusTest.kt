package io.github.mrsimkin.dndcustomaid.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class ScaffoldStatusTest {
    @Test
    fun exposesExpectedScaffoldMessage() {
        assertEquals("dnd_custom_aid scaffold OK", ScaffoldStatus.message)
    }
}
