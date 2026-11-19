package io.github.mrsimkin.dndcustomaid.shared.qr

/*
 * A small QR writer (owner, 2026-11-18: the campaign invitation shows a code, a link «y/o» a QR
 * with its expiry). The App ships no barcode library, and an invitation is short, so this writes
 * byte mode with error correction M in versions 1 to 10 — up to 213 characters, plenty for a link.
 *
 * It follows ISO/IEC 18004. What a reader relies on is checked by the tests: every block's
 * remainder against its generator is zero, the modules read back as the codewords that went in,
 * and the fixed patterns sit where the standard puts them.
 */

/** A square of modules; true is dark. */
class QrCode internal constructor(val size: Int, private val modules: BooleanArray) {
    fun isDark(x: Int, y: Int): Boolean = modules[y * size + x]

    /** The rows as text, for tests and for a quick look: «█» is dark. */
    fun rows(): List<String> = (0 until size).map { y ->
        (0 until size).joinToString("") { x -> if (isDark(x, y)) "█" else " " }
    }
}

/** Error correction M: a reader still works with about 15% of the code damaged. */
private const val EC_LEVEL_BITS = 0b00

private data class VersionPlan(
    val ecCodewordsPerBlock: Int,
    val group1Blocks: Int,
    val group1DataCodewords: Int,
    val group2Blocks: Int,
    val group2DataCodewords: Int,
) {
    val dataCodewords: Int get() = group1Blocks * group1DataCodewords + group2Blocks * group2DataCodewords
    val blocks: Int get() = group1Blocks + group2Blocks
}

// Versions 1 to 10 with error correction M (ISO/IEC 18004 table 9).
private val PLANS = mapOf(
    1 to VersionPlan(10, 1, 16, 0, 0),
    2 to VersionPlan(16, 1, 28, 0, 0),
    3 to VersionPlan(26, 1, 44, 0, 0),
    4 to VersionPlan(18, 2, 32, 0, 0),
    5 to VersionPlan(24, 2, 43, 0, 0),
    6 to VersionPlan(16, 4, 27, 0, 0),
    7 to VersionPlan(18, 4, 31, 0, 0),
    8 to VersionPlan(22, 2, 38, 2, 39),
    9 to VersionPlan(22, 3, 36, 2, 37),
    10 to VersionPlan(26, 4, 43, 1, 44),
)

private val ALIGNMENT_CENTERS = mapOf(
    1 to emptyList<Int>(),
    2 to listOf(6, 18),
    3 to listOf(6, 22),
    4 to listOf(6, 26),
    5 to listOf(6, 30),
    6 to listOf(6, 34),
    7 to listOf(6, 22, 38),
    8 to listOf(6, 24, 42),
    9 to listOf(6, 26, 46),
    10 to listOf(6, 28, 50),
)

/** Writes [text] as a QR code. Throws when the text does not fit in version 10. */
fun qrCode(text: String): QrCode {
    val data = text.encodeToByteArray()
    val version = (1..10).firstOrNull { version ->
        val plan = PLANS.getValue(version)
        val lengthBits = if (version < 10) 8 else 16
        4 + lengthBits + data.size * 8 <= plan.dataCodewords * 8
    } ?: throw IllegalArgumentException("El texto es demasiado largo para un QR de esta App.")

    val plan = PLANS.getValue(version)
    val codewords = interleave(plan, dataCodewords(data, version, plan))
    val size = 17 + version * 4
    val modules = BooleanArray(size * size)
    val reserved = BooleanArray(size * size)

    drawFunctionPatterns(version, size, modules, reserved)
    placeCodewords(size, modules, reserved, codewords)

    val mask = (0..7).minByOrNull { candidate ->
        val tried = modules.copyOf()
        applyMask(size, tried, reserved, candidate)
        writeFormat(size, tried, candidate)
        penalty(size, tried)
    } ?: 0
    applyMask(size, modules, reserved, mask)
    writeFormat(size, modules, mask)
    if (version >= 7) writeVersion(size, modules, version)
    return QrCode(size, modules)
}

// ---------------------------------------------------------------- data

private fun dataCodewords(data: ByteArray, version: Int, plan: VersionPlan): IntArray {
    val bits = ArrayList<Boolean>(plan.dataCodewords * 8)
    fun push(value: Int, length: Int) {
        for (bit in length - 1 downTo 0) bits += (value shr bit) and 1 == 1
    }
    push(0b0100, 4) // byte mode
    push(data.size, if (version < 10) 8 else 16)
    data.forEach { push(it.toInt() and 0xFF, 8) }

    val capacity = plan.dataCodewords * 8
    repeat(minOf(4, capacity - bits.size)) { bits += false }
    while (bits.size % 8 != 0) bits += false
    val codewords = IntArray(plan.dataCodewords)
    for (index in codewords.indices) {
        var value = 0
        for (bit in 0 until 8) {
            val position = index * 8 + bit
            value = (value shl 1) or if (position < bits.size && bits[position]) 1 else 0
        }
        codewords[index] = value
    }
    var pad = 0xEC
    for (index in (bits.size + 7) / 8 until codewords.size) {
        codewords[index] = pad
        pad = if (pad == 0xEC) 0x11 else 0xEC
    }
    return codewords
}

/** The blocks' data and error correction, in the order the standard writes them. */
private fun interleave(plan: VersionPlan, data: IntArray): IntArray {
    val blocks = mutableListOf<IntArray>()
    val corrections = mutableListOf<IntArray>()
    var at = 0
    repeat(plan.group1Blocks) {
        val block = data.copyOfRange(at, at + plan.group1DataCodewords)
        at += plan.group1DataCodewords
        blocks += block
        corrections += errorCorrection(block, plan.ecCodewordsPerBlock)
    }
    repeat(plan.group2Blocks) {
        val block = data.copyOfRange(at, at + plan.group2DataCodewords)
        at += plan.group2DataCodewords
        blocks += block
        corrections += errorCorrection(block, plan.ecCodewordsPerBlock)
    }

    val result = ArrayList<Int>(data.size + plan.blocks * plan.ecCodewordsPerBlock)
    val longest = blocks.maxOf { it.size }
    for (index in 0 until longest) {
        blocks.forEach { block -> if (index < block.size) result += block[index] }
    }
    for (index in 0 until plan.ecCodewordsPerBlock) {
        corrections.forEach { block -> result += block[index] }
    }
    return result.toIntArray()
}

// Reed-Solomon over GF(256) with the QR primitive 0x11D.
private val EXP = IntArray(512)
private val LOG = IntArray(256)

private fun initTables() {
    if (EXP[0] != 0) return
    var value = 1
    for (index in 0 until 255) {
        EXP[index] = value
        LOG[value] = index
        value = value shl 1
        if (value and 0x100 != 0) value = value xor 0x11D
    }
    for (index in 255 until 512) EXP[index] = EXP[index - 255]
}

private fun multiply(a: Int, b: Int): Int {
    if (a == 0 || b == 0) return 0
    initTables()
    return EXP[LOG[a] + LOG[b]]
}

internal fun generatorPolynomial(degree: Int): IntArray {
    initTables()
    var polynomial = intArrayOf(1)
    for (index in 0 until degree) {
        val next = IntArray(polynomial.size + 1)
        for (position in polynomial.indices) {
            next[position] = next[position] xor polynomial[position]
            next[position + 1] = next[position + 1] xor multiply(polynomial[position], EXP[index])
        }
        polynomial = next
    }
    return polynomial
}

internal fun errorCorrection(data: IntArray, count: Int): IntArray {
    val generator = generatorPolynomial(count)
    val remainder = IntArray(count)
    data.forEach { codeword ->
        val factor = codeword xor remainder[0]
        for (index in 0 until remainder.size - 1) remainder[index] = remainder[index + 1]
        remainder[remainder.size - 1] = 0
        if (factor != 0) {
            for (index in remainder.indices) {
                remainder[index] = remainder[index] xor multiply(generator[index + 1], factor)
            }
        }
    }
    return remainder
}

// ---------------------------------------------------------------- matrix

private fun drawFunctionPatterns(version: Int, size: Int, modules: BooleanArray, reserved: BooleanArray) {
    fun set(x: Int, y: Int, dark: Boolean) {
        if (x in 0 until size && y in 0 until size) {
            modules[y * size + x] = dark
            reserved[y * size + x] = true
        }
    }

    fun finder(originX: Int, originY: Int) {
        for (y in -1..7) {
            for (x in -1..7) {
                val dark = (x in 0..6 && (y == 0 || y == 6)) ||
                    (y in 0..6 && (x == 0 || x == 6)) ||
                    (x in 2..4 && y in 2..4)
                set(originX + x, originY + y, dark)
            }
        }
    }
    finder(0, 0)
    finder(size - 7, 0)
    finder(0, size - 7)

    for (index in 8 until size - 8) {
        val dark = index % 2 == 0
        set(index, 6, dark)
        set(6, index, dark)
    }

    val centers = ALIGNMENT_CENTERS.getValue(version)
    centers.forEach { centerY ->
        centers.forEach { centerX ->
            val nearFinder = (centerX <= 8 && centerY <= 8) ||
                (centerX <= 8 && centerY >= size - 9) ||
                (centerX >= size - 9 && centerY <= 8)
            if (nearFinder) return@forEach
            for (y in -2..2) {
                for (x in -2..2) {
                    set(centerX + x, centerY + y, kotlin.math.max(kotlin.math.abs(x), kotlin.math.abs(y)) != 1)
                }
            }
        }
    }

    set(8, size - 8, true) // the module that is always dark
    for (index in 0..8) {
        if (index != 6) {
            reserved[8 * size + index] = true
            reserved[index * size + 8] = true
        }
    }
    for (index in 0 until 8) {
        reserved[8 * size + (size - 1 - index)] = true
        reserved[(size - 1 - index) * size + 8] = true
    }
    if (version >= 7) {
        for (index in 0 until 18) {
            val x = index / 3
            val y = size - 11 + index % 3
            reserved[y * size + x] = true
            reserved[x * size + y] = true
        }
    }
}

private fun placeCodewords(size: Int, modules: BooleanArray, reserved: BooleanArray, codewords: IntArray) {
    var bit = 0
    var column = size - 1
    while (column > 0) {
        if (column == 6) column -= 1
        val upwards = ((size - 1 - column) / 2) % 2 == 0
        for (step in 0 until size) {
            val y = if (upwards) size - 1 - step else step
            for (offset in 0..1) {
                val x = column - offset
                if (reserved[y * size + x]) continue
                val dark = bit < codewords.size * 8 &&
                    (codewords[bit / 8] shr (7 - bit % 8)) and 1 == 1
                modules[y * size + x] = dark
                bit += 1
            }
        }
        column -= 2
    }
}

internal fun maskApplies(mask: Int, x: Int, y: Int): Boolean = when (mask) {
    0 -> (x + y) % 2 == 0
    1 -> y % 2 == 0
    2 -> x % 3 == 0
    3 -> (x + y) % 3 == 0
    4 -> (y / 2 + x / 3) % 2 == 0
    5 -> (x * y) % 2 + (x * y) % 3 == 0
    6 -> ((x * y) % 2 + (x * y) % 3) % 2 == 0
    else -> ((x + y) % 2 + (x * y) % 3) % 2 == 0
}

private fun applyMask(size: Int, modules: BooleanArray, reserved: BooleanArray, mask: Int) {
    for (y in 0 until size) {
        for (x in 0 until size) {
            if (reserved[y * size + x]) continue
            if (maskApplies(mask, x, y)) modules[y * size + x] = !modules[y * size + x]
        }
    }
}

internal fun formatBits(mask: Int): Int {
    val data = (EC_LEVEL_BITS shl 3) or mask
    var remainder = data shl 10
    for (shift in 4 downTo 0) {
        if ((remainder shr (shift + 10)) and 1 == 1) remainder = remainder xor (0x537 shl shift)
    }
    return ((data shl 10) or remainder) xor 0x5412
}

private fun writeFormat(size: Int, modules: BooleanArray, mask: Int) {
    val bits = formatBits(mask)
    fun bit(index: Int) = (bits shr index) and 1 == 1
    for (index in 0..5) modules[8 * size + index] = bit(index)
    modules[8 * size + 7] = bit(6)
    modules[8 * size + 8] = bit(7)
    modules[7 * size + 8] = bit(8)
    for (index in 9..14) modules[(14 - index) * size + 8] = bit(index)

    for (index in 0..7) modules[(size - 1 - index) * size + 8] = bit(index)
    for (index in 8..14) modules[8 * size + (size - 15 + index)] = bit(index)
    modules[(size - 8) * size + 8] = true
}

internal fun versionBits(version: Int): Int {
    var remainder = version shl 12
    for (shift in 5 downTo 0) {
        if ((remainder shr (shift + 12)) and 1 == 1) remainder = remainder xor (0x1F25 shl shift)
    }
    return (version shl 12) or remainder
}

private fun writeVersion(size: Int, modules: BooleanArray, version: Int) {
    val bits = versionBits(version)
    for (index in 0 until 18) {
        val dark = (bits shr index) and 1 == 1
        val x = index / 3
        val y = size - 11 + index % 3
        modules[y * size + x] = dark
        modules[x * size + y] = dark
    }
}

/** How ugly a candidate is for a reader (the standard's four rules); the smallest wins. */
private fun penalty(size: Int, modules: BooleanArray): Int {
    fun dark(x: Int, y: Int) = modules[y * size + x]
    var score = 0

    for (y in 0 until size) {
        var run = 1
        for (x in 1 until size) {
            if (dark(x, y) == dark(x - 1, y)) run += 1 else { if (run >= 5) score += 3 + (run - 5); run = 1 }
        }
        if (run >= 5) score += 3 + (run - 5)
    }
    for (x in 0 until size) {
        var run = 1
        for (y in 1 until size) {
            if (dark(x, y) == dark(x, y - 1)) run += 1 else { if (run >= 5) score += 3 + (run - 5); run = 1 }
        }
        if (run >= 5) score += 3 + (run - 5)
    }
    for (y in 0 until size - 1) {
        for (x in 0 until size - 1) {
            val value = dark(x, y)
            if (value == dark(x + 1, y) && value == dark(x, y + 1) && value == dark(x + 1, y + 1)) score += 3
        }
    }
    val pattern = booleanArrayOf(true, false, true, true, true, false, true, false, false, false, false)
    val reversed = pattern.reversedArray()
    for (y in 0 until size) {
        for (x in 0 until size - 10) {
            if ((0 until 11).all { dark(x + it, y) == pattern[it] } || (0 until 11).all { dark(x + it, y) == reversed[it] }) score += 40
        }
    }
    for (x in 0 until size) {
        for (y in 0 until size - 10) {
            if ((0 until 11).all { dark(x, y + it) == pattern[it] } || (0 until 11).all { dark(x, y + it) == reversed[it] }) score += 40
        }
    }
    val darkCount = modules.count { it }
    val percent = darkCount * 100 / (size * size)
    score += 10 * (kotlin.math.abs(percent - 50) / 5)
    return score
}

/** For the tests: which modules belong to the fixed patterns of this version. */
internal fun reservedModules(version: Int): BooleanArray {
    val size = 17 + version * 4
    val modules = BooleanArray(size * size)
    val reserved = BooleanArray(size * size)
    drawFunctionPatterns(version, size, modules, reserved)
    return reserved
}

/** For the tests: [error correction codewords, blocks and data codewords of each group]. */
internal fun qrPlan(version: Int): IntArray = PLANS.getValue(version).let {
    intArrayOf(it.ecCodewordsPerBlock, it.group1Blocks, it.group1DataCodewords, it.group2Blocks, it.group2DataCodewords)
}
