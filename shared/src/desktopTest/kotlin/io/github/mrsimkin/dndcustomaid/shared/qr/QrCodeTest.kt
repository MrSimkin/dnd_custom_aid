package io.github.mrsimkin.dndcustomaid.shared.qr

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The App writes its own QR (owner, 2026-11-18: the invitation shows a code, a link and a QR).
 * There is no barcode library here, so the tests read the code back the way a reader does: they
 * find the mask in the format bits, undo it, read the modules in the standard's order, check that
 * every block divides by its generator with no remainder, and decode the text.
 */
class QrCodeTest {
    @Test
    fun aShortLinkComesBackOutOfTheModules() {
        val text = "https://dnd-custom-aid.app/unirme/ABCDEF"
        assertEquals(text, decode(qrCode(text)))
    }

    @Test
    fun aSixLetterCodeComesBackOutOfTheModules() {
        assertEquals("MARFIL", decode(qrCode("MARFIL")))
    }

    @Test
    fun aLongerInvitationStillReadsBack() {
        val text = "https://dnd-custom-aid.mrsimkin-dev.workers.dev/unirme?campana=Serpiente%20Vieja&codigo=MARFIL"
        val code = qrCode(text)
        assertEquals(text, decode(code))
        assertTrue(code.size > qrCode("MARFIL").size, "a longer text needs a bigger code: ${code.size}")
    }

    @Test
    fun theFixedPatternsSitWhereTheStandardPutsThem() {
        val code = qrCode("MARFIL")
        // The three finders, with their white ring.
        listOf(0 to 0, code.size - 7 to 0, 0 to code.size - 7).forEach { (originX, originY) ->
            assertTrue((0..6).all { code.isDark(originX + it, originY) }, "top line of the finder")
            assertTrue((2..4).all { x -> (2..4).all { y -> code.isDark(originX + x, originY + y) } }, "centre of the finder")
            assertTrue(!code.isDark(originX + 1, originY + 1), "white ring of the finder")
        }
        // The timing line alternates.
        assertTrue((8 until code.size - 8).all { code.isDark(it, 6) == (it % 2 == 0) }, "timing line")
        // The module that is always dark.
        assertTrue(code.isDark(8, code.size - 8))
    }

    @Test
    fun aTextTooLongForTheseVersionsIsRefusedInPlainWords() {
        val problem = runCatching { qrCode("x".repeat(400)) }
        assertTrue(problem.isFailure)
        assertTrue(problem.exceptionOrNull()?.message?.contains("demasiado largo") == true)
    }

    // ------------------------------------------------------------------ a reader, in the test

    private fun decode(code: QrCode): String {
        val version = (code.size - 17) / 4
        val reserved = reservedModules(version)
        val size = code.size

        // 1. the mask, from the format information next to the top-left finder
        val formatBits = (0..5).map { code.isDark(it, 8) } +
            listOf(code.isDark(7, 8), code.isDark(8, 8), code.isDark(8, 7)) +
            (9..14).map { code.isDark(8, 14 - it) }
        var format = 0
        formatBits.forEachIndexed { index, dark -> if (dark) format = format or (1 shl index) }
        val mask = ((format xor 0x5412) shr 10) and 0b111
        assertEquals(formatBits(mask), format, "the format information must describe the mask that was used")

        // 2. undo the mask and read the modules in the standard's order
        val bits = ArrayList<Boolean>(size * size)
        var column = size - 1
        while (column > 0) {
            if (column == 6) column -= 1
            val upwards = ((size - 1 - column) / 2) % 2 == 0
            for (step in 0 until size) {
                val y = if (upwards) size - 1 - step else step
                for (offset in 0..1) {
                    val x = column - offset
                    if (reserved[y * size + x]) continue
                    val dark = code.isDark(x, y) xor maskApplies(mask, x, y)
                    bits += dark
                }
            }
            column -= 2
        }
        val codewords = (0 until bits.size / 8).map { index ->
            var value = 0
            for (bit in 0 until 8) value = (value shl 1) or if (bits[index * 8 + bit]) 1 else 0
            value
        }

        // 3. put the blocks back together and check each one against its generator
        val (ec, group1Blocks, group1Data, group2Blocks, group2Data) = qrPlan(version).toList()
        val blockSizes = List(group1Blocks) { group1Data } + List(group2Blocks) { group2Data }
        val blocks = blockSizes.map { IntArray(it) }
        val corrections = blockSizes.map { IntArray(ec) }
        var at = 0
        for (index in 0 until blockSizes.max()) {
            blocks.forEach { block -> if (index < block.size) block[index] = codewords[at++] }
        }
        for (index in 0 until ec) {
            corrections.forEach { block -> block[index] = codewords[at++] }
        }
        blocks.forEachIndexed { index, block ->
            val whole = block + corrections[index]
            assertTrue(syndromesAreZero(whole, ec), "block $index does not divide by its generator")
        }

        // 4. the text itself
        val data = blocks.flatMap { it.toList() }
        val stream = data.flatMap { codeword -> (7 downTo 0).map { (codeword shr it) and 1 == 1 } }
        fun read(from: Int, length: Int): Int {
            var value = 0
            for (index in 0 until length) value = (value shl 1) or if (stream[from + index]) 1 else 0
            return value
        }
        assertEquals(0b0100, read(0, 4), "byte mode")
        val lengthBits = if (version < 10) 8 else 16
        val length = read(4, lengthBits)
        val bytes = ByteArray(length) { read(4 + lengthBits + it * 8, 8).toByte() }
        return bytes.decodeToString()
    }

    private fun syndromesAreZero(codewords: IntArray, count: Int): Boolean {
        val exp = IntArray(512)
        val log = IntArray(256)
        var value = 1
        for (index in 0 until 255) {
            exp[index] = value
            log[value] = index
            value = value shl 1
            if (value and 0x100 != 0) value = value xor 0x11D
        }
        for (index in 255 until 512) exp[index] = exp[index - 255]
        fun multiply(a: Int, b: Int) = if (a == 0 || b == 0) 0 else exp[log[a] + log[b]]

        return (0 until count).all { power ->
            var result = 0
            codewords.forEach { codeword -> result = multiply(result, exp[power]) xor codeword }
            result == 0
        }
    }
}
