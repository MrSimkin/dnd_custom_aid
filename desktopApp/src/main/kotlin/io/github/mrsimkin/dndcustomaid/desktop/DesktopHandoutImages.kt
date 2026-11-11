package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.content.HANDOUT_MAX_BYTES
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

/** Longest side, in pixels, the App keeps: enough for a tablet at full screen. */
internal const val HANDOUT_MAX_SIDE = 2048

/** Files larger than this are re-encoded even when their size in pixels is fine. */
internal const val HANDOUT_REENCODE_ABOVE_BYTES = 1_500_000

internal data class PreparedHandoutImage(
    val bytes: ByteArray,
    val mediaType: String,
    val width: Int?,
    val height: Int?,
)

internal class HandoutImageException(message: String) : Exception(message)

/**
 * Prepares an imported image for the hosted free plan (P27): JPEG/PNG stay as they are when small;
 * large ones are scaled to [HANDOUT_MAX_SIDE] and re-encoded (PNG when transparent, JPEG otherwise).
 * WebP is kept as is when it already fits, since the JVM cannot decode it.
 */
internal fun prepareHandoutImage(bytes: ByteArray, fileName: String): PreparedHandoutImage {
    if (bytes.isEmpty()) throw HandoutImageException("El archivo está vacío.")
    val image = ImageIO.read(ByteArrayInputStream(bytes))
    if (image == null) {
        if (fileName.lowercase().endsWith(".webp") && isWebp(bytes)) {
            if (bytes.size > HANDOUT_MAX_BYTES) throw HandoutImageException("La imagen WebP pesa más de 5 MB.")
            return PreparedHandoutImage(bytes, "image/webp", null, null)
        }
        throw HandoutImageException("Ese archivo no es una imagen JPG, PNG o WebP.")
    }
    val originalType = when {
        isPng(bytes) -> "image/png"
        isJpeg(bytes) -> "image/jpeg"
        else -> null
    }
    val tooLarge = maxOf(image.width, image.height) > HANDOUT_MAX_SIDE || bytes.size > HANDOUT_REENCODE_ABOVE_BYTES
    if (originalType != null && !tooLarge) {
        return PreparedHandoutImage(bytes, originalType, image.width, image.height)
    }
    val scaled = scaleToFit(image, HANDOUT_MAX_SIDE)
    val transparent = scaled.colorModel.hasAlpha()
    val encoded = if (transparent) encodePng(scaled) else encodeJpeg(scaled, 0.85f)
    if (encoded.size > HANDOUT_MAX_BYTES) throw HandoutImageException("La imagen sigue pesando más de 5 MB después de reducirla.")
    return PreparedHandoutImage(encoded, if (transparent) "image/png" else "image/jpeg", scaled.width, scaled.height)
}

private fun scaleToFit(image: BufferedImage, maxSide: Int): BufferedImage {
    val longest = maxOf(image.width, image.height)
    val factor = if (longest > maxSide) maxSide.toDouble() / longest else 1.0
    val width = maxOf(1, (image.width * factor).toInt())
    val height = maxOf(1, (image.height * factor).toInt())
    val type = if (image.colorModel.hasAlpha()) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
    val target = BufferedImage(width, height, type)
    val graphics = target.createGraphics()
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    graphics.drawImage(image, 0, 0, width, height, null)
    graphics.dispose()
    return target
}

private fun encodePng(image: BufferedImage): ByteArray =
    ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()

private fun encodeJpeg(image: BufferedImage, quality: Float): ByteArray {
    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val output = ByteArrayOutputStream()
    ImageIO.createImageOutputStream(output).use { stream ->
        writer.output = stream
        val params = writer.defaultWriteParam.apply {
            compressionMode = ImageWriteParam.MODE_EXPLICIT
            compressionQuality = quality
        }
        writer.write(null, IIOImage(image, null, null), params)
    }
    writer.dispose()
    return output.toByteArray()
}

private fun isPng(bytes: ByteArray) = bytes.size > 8 &&
    bytes[0] == 0x89.toByte() && bytes[1] == 'P'.code.toByte() && bytes[2] == 'N'.code.toByte() && bytes[3] == 'G'.code.toByte()

private fun isJpeg(bytes: ByteArray) = bytes.size > 3 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte()

private fun isWebp(bytes: ByteArray) = bytes.size > 12 &&
    String(bytes, 0, 4, Charsets.US_ASCII) == "RIFF" && String(bytes, 8, 4, Charsets.US_ASCII) == "WEBP"
