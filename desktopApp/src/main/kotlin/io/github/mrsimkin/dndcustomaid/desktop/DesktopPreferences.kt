package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font as DesktopFont
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import java.awt.GraphicsEnvironment
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties
import kotlin.math.abs

internal enum class DesktopThemeChoice(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
    GRAY("Gris"),
    DARK_PURPLE("Púrpura"),
    DARK_CYAN("Cyan"),
    NIGHT_BLUE("Noche"),
    FOREST_GREEN("Bosque"),
    PARCHMENT("Pergamino"),
}

internal enum class DesktopFontChoice(
    val label: String,
    private val systemFamilyName: String? = null,
    private val bundledResource: String? = null,
) {
    // Preserve the three original Desktop choices for preference compatibility and predictable
    // logical-font fallbacks on every supported JVM.
    SANS_SERIF("Sistema · Sans serif"),
    SERIF("Sistema · Serif"),
    MONOSPACE("Sistema · Monoespaciada"),

    // Current Android-selectable application catalogue. Geist and Mona Sans Condensed are bundled
    // from the already-present Android assets; the remainder are offered only when that exact
    // family is installed on the Desktop machine. This prevents fake labels rendering one fallback.
    MANROPE("Manrope", systemFamilyName = "Manrope"),
    SORA("Sora", systemFamilyName = "Sora"),
    SOURCE_SANS_3("Source Sans 3", systemFamilyName = "Source Sans 3"),
    ROBOTO_CONDENSED("Roboto Condensed", systemFamilyName = "Roboto Condensed"),
    ARCHIVO_NARROW("Archivo Narrow", systemFamilyName = "Archivo Narrow"),
    IBM_PLEX_SANS_CONDENSED("IBM Plex Sans Condensed", systemFamilyName = "IBM Plex Sans Condensed"),
    MONA_SANS_CONDENSED(
        "Mona Sans Condensed",
        bundledResource = "fonts/mona_sans_condensed_vf.ttf",
    ),
    GEIST("Geist", bundledResource = "fonts/geist_vf.ttf"),
    BARLOW_SEMI_CONDENSED("Barlow Semi Condensed", systemFamilyName = "Barlow Semi Condensed"),
    SPACE_GROTESK("Space Grotesk", systemFamilyName = "Space Grotesk"),
    RECURSIVE("Recursive", systemFamilyName = "Recursive"),
    PT_SANS_NARROW("PT Sans Narrow", systemFamilyName = "PT Sans Narrow"),
    LEAGUE_SPARTAN("League Spartan", systemFamilyName = "League Spartan"),
    ;

    @OptIn(ExperimentalTextApi::class)
    val family: FontFamily
        get() = when (this) {
            SANS_SERIF -> FontFamily.SansSerif
            SERIF -> FontFamily.Serif
            MONOSPACE -> FontFamily.Monospace
            else -> bundledResource?.let { resource ->
                FontFamily(
                    DesktopFont(
                        resource,
                        FontWeight.Normal,
                        FontStyle.Normal,
                    ),
                )
            } ?: FontFamily(requireNotNull(systemFamilyName))
        }

    fun isAvailable(installedFamilies: Set<String> = installedDesktopFontFamilies()): Boolean = when {
        this == SANS_SERIF || this == SERIF || this == MONOSPACE -> true
        bundledResource != null -> true
        systemFamilyName != null -> installedFamilies.any { it.equals(systemFamilyName, ignoreCase = true) }
        else -> false
    }

    companion object {
        fun selectableChoices(installedFamilies: Set<String> = installedDesktopFontFamilies()): List<DesktopFontChoice> =
            entries.filter { it.isAvailable(installedFamilies) }
    }
}

internal enum class DesktopWorkspaceDensity(val label: String) {
    COMFORTABLE("Cómodo"),
    BALANCED("Equilibrado"),
    COMPACT("Compacto"),
    DENSE("Denso"),
}

internal data class DesktopPreferences(
    val fontScalePercent: Int = 100,
    val spacingScalePercent: Int = 100,
    val fontChoice: DesktopFontChoice = DesktopFontChoice.SANS_SERIF,
    val themeChoice: DesktopThemeChoice = DesktopThemeChoice.SYSTEM,
    val workspaceDensity: DesktopWorkspaceDensity = DesktopWorkspaceDensity.BALANCED,
)

internal val DESKTOP_FONT_SCALE_OPTIONS = (50..150 step 10).toList()
internal val DESKTOP_SPACING_SCALE_OPTIONS = (50..150 step 10).toList()

private val cachedInstalledDesktopFontFamilies: Set<String> by lazy {
    runCatching {
        GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .availableFontFamilyNames
            .toSet()
    }.getOrDefault(emptySet())
}

internal fun installedDesktopFontFamilies(): Set<String> = cachedInstalledDesktopFontFamilies

internal fun resolveDesktopFontChoice(
    raw: String?,
    installedFamilies: Set<String> = installedDesktopFontFamilies(),
): DesktopFontChoice {
    val parsed = raw?.let { stored ->
        DesktopFontChoice.entries.firstOrNull { it.name == stored }
    }
    return parsed?.takeIf { it.isAvailable(installedFamilies) }
        ?: DesktopFontChoice.SANS_SERIF
}

internal class DesktopPreferencesStore(
    private val file: Path = defaultPreferencesFile(),
) {
    fun load(): DesktopPreferences {
        if (!Files.exists(file)) return DesktopPreferences()

        val properties = Properties()
        runCatching {
            Files.newInputStream(file).use(properties::load)
        }.getOrElse {
            return DesktopPreferences()
        }

        return DesktopPreferences(
            fontScalePercent = nearestPercent(
                properties.getProperty(KEY_FONT_SCALE)?.toIntOrNull() ?: 100,
                DESKTOP_FONT_SCALE_OPTIONS,
            ),
            spacingScalePercent = nearestPercent(
                properties.getProperty(KEY_SPACING_SCALE)?.toIntOrNull() ?: 100,
                DESKTOP_SPACING_SCALE_OPTIONS,
            ),
            fontChoice = resolveDesktopFontChoice(properties.getProperty(KEY_FONT)),
            themeChoice = properties.enumValue(KEY_THEME, DesktopThemeChoice.SYSTEM),
            workspaceDensity = properties.enumValue(KEY_WORKSPACE_DENSITY, DesktopWorkspaceDensity.BALANCED),
        )
    }

    fun save(value: DesktopPreferences) {
        file.parent?.let { Files.createDirectories(it) }
        val properties = Properties().apply {
            setProperty(KEY_FONT_SCALE, value.fontScalePercent.toString())
            setProperty(KEY_SPACING_SCALE, value.spacingScalePercent.toString())
            setProperty(KEY_FONT, value.fontChoice.name)
            setProperty(KEY_THEME, value.themeChoice.name)
            setProperty(KEY_WORKSPACE_DENSITY, value.workspaceDensity.name)
        }
        Files.newOutputStream(file).use { output ->
            properties.store(output, "D&D Custom Aid Desktop preferences")
        }
    }

    companion object {
        private const val KEY_FONT_SCALE = "font_scale_percent"
        private const val KEY_SPACING_SCALE = "spacing_scale_percent"
        private const val KEY_FONT = "font_choice"
        private const val KEY_THEME = "theme_choice"
        private const val KEY_WORKSPACE_DENSITY = "workspace_density"

        fun defaultPreferencesFile(): Path = Paths.get(
            System.getProperty("user.home"),
            ".dnd_custom_aid",
            "desktop_preferences.properties",
        )
    }
}

private inline fun <reified T : Enum<T>> Properties.enumValue(key: String, fallback: T): T =
    getProperty(key)
        ?.let { raw -> enumValues<T>().firstOrNull { it.name == raw } }
        ?: fallback

private fun nearestPercent(value: Int, options: List<Int>): Int {
    val bounded = value.coerceIn(options.first(), options.last())
    return options.minByOrNull { abs(it - bounded) } ?: 100
}

private val LocalDesktopSpacingScale = staticCompositionLocalOf { 100 }

@Composable
internal fun desktopSpacing(value: Dp): Dp {
    val percent = LocalDesktopSpacingScale.current.coerceIn(50, 150)
    val signed = (percent - 100) / 100f
    return value * (1f + signed * 0.70f)
}

@Composable
internal fun DesktopAppTheme(
    preferences: DesktopPreferences,
    content: @Composable () -> Unit,
) {
    val currentDensity = LocalDensity.current
    val adjustedDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale * (preferences.fontScalePercent / 100f),
    )
    val typography = Typography(defaultFontFamily = preferences.fontChoice.family)

    CompositionLocalProvider(
        LocalDensity provides adjustedDensity,
        LocalDesktopSpacingScale provides preferences.spacingScalePercent,
    ) {
        androidx.compose.material.MaterialTheme(
            colors = desktopColors(preferences.themeChoice),
            typography = typography,
            content = content,
        )
    }
}

@Composable
internal fun desktopColors(choice: DesktopThemeChoice): Colors = when (choice) {
    DesktopThemeChoice.SYSTEM -> if (isSystemInDarkTheme()) darkColors() else lightColors()
    DesktopThemeChoice.LIGHT -> lightColors()
    DesktopThemeChoice.DARK -> darkColors()
    DesktopThemeChoice.GRAY -> lightColors(
        primary = Color(0xFF424242),
        primaryVariant = Color(0xFF303030),
        secondary = Color(0xFF616161),
        background = Color(0xFFB8B8B8),
        surface = Color(0xFFD2D2D2),
        onBackground = Color(0xFF181818),
        onSurface = Color(0xFF181818),
    )
    DesktopThemeChoice.DARK_PURPLE -> darkColors(
        primary = Color(0xFFD5B3FF),
        primaryVariant = Color(0xFF5A2392),
        secondary = Color(0xFFC8B4E3),
        background = Color(0xFF120B1F),
        surface = Color(0xFF1B1229),
    )
    DesktopThemeChoice.DARK_CYAN -> darkColors(
        primary = Color(0xFF63E6E2),
        primaryVariant = Color(0xFF00504D),
        secondary = Color(0xFFA8CECC),
        background = Color(0xFF071616),
        surface = Color(0xFF0D2020),
    )
    DesktopThemeChoice.NIGHT_BLUE -> darkColors(
        primary = Color(0xFF8FC3FF),
        primaryVariant = Color(0xFF0D4D82),
        secondary = Color(0xFFABC8E8),
        background = Color(0xFF061A33),
        surface = Color(0xFF0B2749),
    )
    DesktopThemeChoice.FOREST_GREEN -> darkColors(
        primary = Color(0xFFA4D49E),
        primaryVariant = Color(0xFF285125),
        secondary = Color(0xFFB8CCB3),
        background = Color(0xFF0B160B),
        surface = Color(0xFF131F13),
    )
    DesktopThemeChoice.PARCHMENT -> lightColors(
        primary = Color(0xFF5F3D16),
        primaryVariant = Color(0xFF76572F),
        secondary = Color(0xFF76572F),
        background = Color(0xFFE8D2A6),
        surface = Color(0xFFFFF3D0),
        onBackground = Color(0xFF2B2114),
        onSurface = Color(0xFF2B2114),
    )
}
