package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DesktopHandoutImagesTest {
    @Test
    fun aSmallPngIsKeptAsIs() {
        val bytes = png(BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB))
        val prepared = prepareHandoutImage(bytes, "mapa.png")
        assertEquals("image/png", prepared.mediaType)
        assertTrue(prepared.bytes.contentEquals(bytes))
        assertEquals(400, prepared.width)
        assertEquals(300, prepared.height)
    }

    @Test
    fun aLargeOpaqueImageIsScaledToTheTabletSizeAsJpeg() {
        val prepared = prepareHandoutImage(png(BufferedImage(3000, 2000, BufferedImage.TYPE_INT_RGB)), "grande.png")
        assertEquals("image/jpeg", prepared.mediaType)
        assertEquals(HANDOUT_MAX_SIDE, prepared.width)
        assertEquals(1365, prepared.height)
    }

    @Test
    fun aLargeTransparentImageStaysPng() {
        val prepared = prepareHandoutImage(png(BufferedImage(2500, 2500, BufferedImage.TYPE_INT_ARGB)), "token.png")
        assertEquals("image/png", prepared.mediaType)
        assertEquals(HANDOUT_MAX_SIDE, prepared.width)
    }

    @Test
    fun aNonImageIsRefusedInSpanish() {
        val error = assertFailsWith<HandoutImageException> { prepareHandoutImage("hola".toByteArray(), "nota.txt") }
        assertTrue(error.message!!.contains("imagen"))
    }

    private fun png(image: BufferedImage): ByteArray = ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
}
