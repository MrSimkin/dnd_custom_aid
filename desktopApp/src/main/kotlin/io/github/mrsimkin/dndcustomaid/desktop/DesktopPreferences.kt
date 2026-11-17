package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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
            io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory.defaultDataDirectory().path,
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

/**
 * F2 refactor (owner 2026-11-14, «un solo estilo»): the whole App de PC uses Material 3, with the
 * palettes chosen in Configuración, the same components as the tablet's desks.
 */
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

    CompositionLocalProvider(
        LocalDensity provides adjustedDensity,
        LocalDesktopSpacingScale provides preferences.spacingScalePercent,
        // QA 0.7.0-preqa.2 (general comment): on a computer there is no finger to make room for, so
        // checkboxes, radio buttons and small buttons drop the 48 dp touch margin around them.
        androidx.compose.material3.LocalMinimumInteractiveComponentSize provides androidx.compose.ui.unit.Dp.Unspecified,
    ) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = desktopColorScheme(desktopPalette(preferences.themeChoice)),
            typography = desktopTypography(preferences.fontChoice.family),
            content = content,
        )
    }
}

/** The colours of one theme of Configuración (the values the App always had). */
internal data class DesktopPalette(
    val isLight: Boolean,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color,
)

private fun lightPalette(
    primary: Color = Color(0xFF6200EE),
    primaryVariant: Color = Color(0xFF3700B3),
    secondary: Color = Color(0xFF03DAC6),
    background: Color = Color.White,
    surface: Color = Color.White,
    onBackground: Color = Color.Black,
    onSurface: Color = Color.Black,
) = DesktopPalette(true, primary, primaryVariant, secondary, Color(0xFF018786), background, surface, Color(0xFFB00020),
    Color.White, Color.Black, onBackground, onSurface, Color.White)

private fun darkPalette(
    primary: Color = Color(0xFFBB86FC),
    primaryVariant: Color = Color(0xFF3700B3),
    secondary: Color = Color(0xFF03DAC6),
    background: Color = Color(0xFF121212),
    surface: Color = Color(0xFF121212),
) = DesktopPalette(false, primary, primaryVariant, secondary, secondary, background, surface, Color(0xFFCF6679),
    Color.Black, Color.Black, Color.White, Color.White, Color.Black)

@Composable
internal fun desktopPalette(choice: DesktopThemeChoice): DesktopPalette = when (choice) {
    DesktopThemeChoice.SYSTEM -> if (isSystemInDarkTheme()) darkPalette() else lightPalette()
    DesktopThemeChoice.LIGHT -> lightPalette()
    DesktopThemeChoice.DARK -> darkPalette()
    DesktopThemeChoice.GRAY -> lightPalette(
        primary = Color(0xFF424242), primaryVariant = Color(0xFF303030), secondary = Color(0xFF616161),
        background = Color(0xFFB8B8B8), surface = Color(0xFFD2D2D2), onBackground = Color(0xFF181818), onSurface = Color(0xFF181818),
    )
    DesktopThemeChoice.DARK_PURPLE -> darkPalette(Color(0xFFD5B3FF), Color(0xFF5A2392), Color(0xFFC8B4E3), Color(0xFF120B1F), Color(0xFF1B1229))
    DesktopThemeChoice.DARK_CYAN -> darkPalette(Color(0xFF63E6E2), Color(0xFF00504D), Color(0xFFA8CECC), Color(0xFF071616), Color(0xFF0D2020))
    DesktopThemeChoice.NIGHT_BLUE -> darkPalette(Color(0xFF8FC3FF), Color(0xFF0D4D82), Color(0xFFABC8E8), Color(0xFF061A33), Color(0xFF0B2749))
    DesktopThemeChoice.FOREST_GREEN -> darkPalette(Color(0xFFA4D49E), Color(0xFF285125), Color(0xFFB8CCB3), Color(0xFF0B160B), Color(0xFF131F13))
    DesktopThemeChoice.PARCHMENT -> lightPalette(
        primary = Color(0xFF5F3D16), primaryVariant = Color(0xFF76572F), secondary = Color(0xFF76572F),
        background = Color(0xFFE8D2A6), surface = Color(0xFFFFF3D0), onBackground = Color(0xFF2B2114), onSurface = Color(0xFF2B2114),
    )
}

/** Material 3 colours from a palette; containers are blends of the palette's own colours. */
internal fun desktopColorScheme(c: DesktopPalette): androidx.compose.material3.ColorScheme {
    val containerOf = { tint: Color -> lerp(c.surface, tint, if (c.isLight) 0.18f else 0.30f) }
    val surfaceVariant = lerp(c.surface, c.onSurface, 0.10f)
    val base = if (c.isLight) androidx.compose.material3.lightColorScheme() else androidx.compose.material3.darkColorScheme()
    return base.copy(
        primary = c.primary, onPrimary = c.onPrimary,
        primaryContainer = containerOf(c.primary), onPrimaryContainer = c.onSurface,
        inversePrimary = c.primaryVariant,
        secondary = c.secondary, onSecondary = c.onSecondary,
        secondaryContainer = containerOf(c.secondary), onSecondaryContainer = c.onSurface,
        tertiary = c.secondaryVariant,
        background = c.background, onBackground = c.onBackground,
        surface = c.surface, onSurface = c.onSurface,
        surfaceVariant = surfaceVariant, onSurfaceVariant = c.onSurface.copy(alpha = 0.74f),
        surfaceTint = c.primary,
        surfaceContainerLowest = c.background,
        surfaceContainerLow = lerp(c.background, c.surface, 0.5f),
        surfaceContainer = c.surface,
        surfaceContainerHigh = lerp(c.surface, c.onSurface, 0.05f),
        surfaceContainerHighest = surfaceVariant,
        error = c.error, onError = c.onError,
        errorContainer = containerOf(c.error), onErrorContainer = c.onSurface,
        outline = c.onSurface.copy(alpha = 0.42f),
        outlineVariant = c.onSurface.copy(alpha = 0.18f),
    )
}

/** Material 3 text styles in the font chosen in Configuración. */
internal fun desktopTypography(family: androidx.compose.ui.text.font.FontFamily?): androidx.compose.material3.Typography {
    val t = androidx.compose.material3.Typography()
    if (family == null) return t
    fun androidx.compose.ui.text.TextStyle.f() = copy(fontFamily = family)
    return t.copy(
        displayLarge = t.displayLarge.f(), displayMedium = t.displayMedium.f(), displaySmall = t.displaySmall.f(),
        headlineLarge = t.headlineLarge.f(), headlineMedium = t.headlineMedium.f(), headlineSmall = t.headlineSmall.f(),
        titleLarge = t.titleLarge.f(), titleMedium = t.titleMedium.f(), titleSmall = t.titleSmall.f(),
        bodyLarge = t.bodyLarge.f(), bodyMedium = t.bodyMedium.f(), bodySmall = t.bodySmall.f(),
        labelLarge = t.labelLarge.f(), labelMedium = t.labelMedium.f(), labelSmall = t.labelSmall.f(),
    )
}
