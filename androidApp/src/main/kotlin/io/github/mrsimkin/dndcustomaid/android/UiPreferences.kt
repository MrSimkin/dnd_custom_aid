package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleDownloadableFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.characterRulesFamilyBadgeLabel

internal enum class AppFontChoice(
    val label: String,
    val googleFontName: String? = null,
    val sourceLabel: String,
) {
    MANROPE("Manrope", "Manrope", "Google Fonts"),
    SORA("Sora", "Sora", "Google Fonts"),
    SOURCE_SANS_3("Source Sans 3", "Source Sans 3", "Adobe / Google Fonts"),
    ROBOTO_CONDENSED("Roboto Condensed", "Roboto Condensed", "Google"),
    ARCHIVO_NARROW("Archivo Narrow", "Archivo Narrow", "Omnibus-Type / Google Fonts"),
    IBM_PLEX_SANS_CONDENSED("IBM Plex Sans Condensed", "IBM Plex Sans Condensed", "IBM"),
    MONA_SANS_CONDENSED("Mona Sans Condensed", sourceLabel = "GitHub / Degarism"),
    GEIST("Geist", sourceLabel = "Vercel"),
    INTER("Inter", "Inter", "Rasmus Andersson"),
    FIGTREE("Figtree", "Figtree", "Erik Kennedy"),
    PUBLIC_SANS("Public Sans", "Public Sans", "U.S. Web Design System"),
    BARLOW_SEMI_CONDENSED("Barlow Semi Condensed", "Barlow Semi Condensed", "Jeremy Tribby"),
    SPACE_GROTESK("Space Grotesk", "Space Grotesk", "Florian Karsten"),
    RECURSIVE("Recursive", "Recursive", "Arrow Type"),
    CABIN_CONDENSED("Cabin Condensed", "Cabin Condensed", "Impallari Type"),
    ENCODE_SANS_CONDENSED("Encode Sans Condensed", "Encode Sans Condensed", "Impallari Type"),
    PT_SANS_NARROW("PT Sans Narrow", "PT Sans Narrow", "ParaType"),
    LEAGUE_SPARTAN("League Spartan", "League Spartan", "The League of Moveable Type"),
}

private val HIDDEN_FONT_CHOICES_V4 = setOf(
    AppFontChoice.INTER,
    AppFontChoice.FIGTREE,
    AppFontChoice.PUBLIC_SANS,
    AppFontChoice.CABIN_CONDENSED,
    AppFontChoice.ENCODE_SANS_CONDENSED,
)

private val SELECTABLE_FONT_CHOICES_V4 = AppFontChoice.entries.filterNot { it in HIDDEN_FONT_CHOICES_V4 }

internal enum class AppThemeChoice(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
    GRAY("Gris"),
    DARK_PURPLE("Púrpura"),
    DARK_CYAN("Cyan"),
    LIGHT_CYAN("Cyan claro"),
    NIGHT_BLUE("Noche"),
    LIGHT_NIGHT_BLUE("Noche despejada"),
    FOREST_GREEN("Bosque"),
    LIGHT_FOREST_GREEN("Oasis"),
    PARCHMENT("Pergamino"),
    CRIMSON("Carmesí"),
    AMBER("Ámbar"),
    GLACIER("Glaciar"),
    LAVENDER("Lavanda"),
    SLATE("Pizarra"),
    TERRACOTTA("Terracota"),
    HIGH_CONTRAST("Alto contraste"),
    MATRIX("Matrix"),
}

internal enum class SkillLayoutChoice(val label: String) {
    BY_SKILLS("Por habilidades"),
    BY_ATTRIBUTE("Por atributo"),
}

internal enum class DiceResultModeChoice(val label: String) {
    COMPACT("Resultado compacto"),
    VISIBLE_DICE("Dados visibles"),
}

internal data class UiPreferences(
    val fontScalePercent: Int = 100,
    val fontChoice: AppFontChoice = AppFontChoice.MANROPE,
    val themeChoice: AppThemeChoice = AppThemeChoice.SYSTEM,
    val skillLayoutChoice: SkillLayoutChoice = SkillLayoutChoice.BY_SKILLS,
    val phonePortraitColumns: Int = 1,
    val phoneLandscapeColumns: Int = 2,
    val tabletPortraitColumns: Int = 2,
    val tabletLandscapeColumns: Int = 3,
    val spacingScalePercent: Int = 100,
    val helpMode: CharacterHelpModeV4 = CharacterHelpModeV4.ALWAYS_VISIBLE,
    val diceResultMode: DiceResultModeChoice = DiceResultModeChoice.COMPACT,
)

internal val LocalUiPreferencesV4 = staticCompositionLocalOf { UiPreferences() }

internal class UiPreferencesStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): UiPreferences {
        val scale = preferences.getInt(KEY_FONT_SCALE, 100).takeIf { it in FONT_SCALE_OPTIONS } ?: 100
        val resolvedFont = when (val stored = preferences.getString(KEY_FONT, null)) {
            "IBM_PLEX_SANS_CONDENSED", "BARLOW_CONDENSED" -> AppFontChoice.ROBOTO_CONDENSED
            "LEXEND" -> AppFontChoice.SORA
            "OSWALD" -> AppFontChoice.MANROPE
            else -> stored
                ?.let { runCatching { AppFontChoice.valueOf(it) }.getOrNull() }
                ?: AppFontChoice.MANROPE
        }
        val font = resolvedFont.takeUnless { it in HIDDEN_FONT_CHOICES_V4 } ?: AppFontChoice.MANROPE
        val theme = when (val stored = preferences.getString(KEY_THEME, null)) {
            "LIGHT_GRAY" -> AppThemeChoice.GRAY
            else -> stored
                ?.let { runCatching { AppThemeChoice.valueOf(it) }.getOrNull() }
                ?: AppThemeChoice.SYSTEM
        }
        val skillLayout = preferences.getString(KEY_SKILL_LAYOUT, null)
            ?.let { runCatching { SkillLayoutChoice.valueOf(it) }.getOrNull() }
            ?: SkillLayoutChoice.BY_SKILLS
        val phonePortraitColumns = preferences.getInt(KEY_PHONE_PORTRAIT_COLUMNS, 1).coerceIn(1, 4)
        val phoneLandscapeColumns = preferences.getInt(KEY_PHONE_LANDSCAPE_COLUMNS, 2).coerceIn(1, 5)
        val tabletPortraitColumns = preferences.getInt(KEY_TABLET_PORTRAIT_COLUMNS, 2).coerceIn(1, 5)
        val tabletLandscapeColumns = preferences.getInt(KEY_TABLET_LANDSCAPE_COLUMNS, 3).coerceIn(1, 6)
        val spacingScalePercent = preferences.getInt(KEY_SPACING_SCALE, 100)
            .takeIf { it in SPACING_SCALE_OPTIONS } ?: 100
        val helpMode = preferences.getString(KEY_HELP_MODE, null)
            ?.let { runCatching { CharacterHelpModeV4.valueOf(it) }.getOrNull() }
            ?: CharacterHelpModeV4.ALWAYS_VISIBLE
        val diceResultMode = preferences.getString(KEY_DICE_RESULT_MODE, null)
            ?.let { runCatching { DiceResultModeChoice.valueOf(it) }.getOrNull() }
            ?: DiceResultModeChoice.COMPACT

        return UiPreferences(
            fontScalePercent = scale,
            fontChoice = font,
            themeChoice = theme,
            skillLayoutChoice = skillLayout,
            phonePortraitColumns = phonePortraitColumns,
            phoneLandscapeColumns = phoneLandscapeColumns,
            tabletPortraitColumns = tabletPortraitColumns,
            tabletLandscapeColumns = tabletLandscapeColumns,
            spacingScalePercent = spacingScalePercent,
            helpMode = helpMode,
            diceResultMode = diceResultMode,
        )
    }

    fun save(value: UiPreferences) {
        preferences.edit()
            .putInt(KEY_FONT_SCALE, value.fontScalePercent)
            .putString(KEY_FONT, value.fontChoice.name)
            .putString(KEY_THEME, value.themeChoice.name)
            .putString(KEY_SKILL_LAYOUT, value.skillLayoutChoice.name)
            .putInt(KEY_PHONE_PORTRAIT_COLUMNS, value.phonePortraitColumns)
            .putInt(KEY_PHONE_LANDSCAPE_COLUMNS, value.phoneLandscapeColumns)
            .putInt(KEY_TABLET_PORTRAIT_COLUMNS, value.tabletPortraitColumns)
            .putInt(KEY_TABLET_LANDSCAPE_COLUMNS, value.tabletLandscapeColumns)
            .putInt(KEY_SPACING_SCALE, value.spacingScalePercent)
            .putString(KEY_HELP_MODE, value.helpMode.name)
            .putString(KEY_DICE_RESULT_MODE, value.diceResultMode.name)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "ui_preferences"
        const val KEY_FONT_SCALE = "font_scale"
        const val KEY_FONT = "font_family"
        const val KEY_THEME = "theme"
        const val KEY_SKILL_LAYOUT = "skill_layout"
        const val KEY_PHONE_PORTRAIT_COLUMNS = "phone_portrait_columns"
        const val KEY_PHONE_LANDSCAPE_COLUMNS = "phone_landscape_columns"
        const val KEY_TABLET_PORTRAIT_COLUMNS = "tablet_portrait_columns"
        const val KEY_TABLET_LANDSCAPE_COLUMNS = "tablet_landscape_columns"
        const val KEY_SPACING_SCALE = "spacing_scale_percent"
        const val KEY_HELP_MODE = "help_mode"
        const val KEY_DICE_RESULT_MODE = "dice_result_mode"
    }
}

internal val FONT_SCALE_OPTIONS = listOf(70, 80, 90, 100, 110, 120, 130, 145, 160, 180, 200)
internal val SPACING_SCALE_OPTIONS = listOf(40, 60, 70, 80, 90, 100)

@Composable
internal fun appSpacingV4(value: Dp): Dp =
    value * (LocalUiPreferencesV4.current.spacingScalePercent / 100f)

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private fun downloadableFontFamily(name: String): FontFamily = FontFamily(
    GoogleDownloadableFont(
        googleFont = GoogleFont(name),
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal,
    ),
    GoogleDownloadableFont(
        googleFont = GoogleFont(name),
        fontProvider = googleFontProvider,
        weight = FontWeight.Medium,
    ),
    GoogleDownloadableFont(
        googleFont = GoogleFont(name),
        fontProvider = googleFontProvider,
        weight = FontWeight.SemiBold,
    ),
    GoogleDownloadableFont(
        googleFont = GoogleFont(name),
        fontProvider = googleFontProvider,
        weight = FontWeight.Bold,
    ),
)

private val downloadableFontFamilies: Map<AppFontChoice, FontFamily> by lazy {
    AppFontChoice.entries.mapNotNull { choice ->
        choice.googleFontName?.let { choice to downloadableFontFamily(it) }
    }.toMap()
}

private val monaSansCondensedFamily by lazy {
    FontFamily(
        Font(R.font.mona_sans_condensed_vf, FontWeight.Normal),
        Font(R.font.mona_sans_condensed_vf, FontWeight.Medium),
        Font(R.font.mona_sans_condensed_vf, FontWeight.SemiBold),
        Font(R.font.mona_sans_condensed_vf, FontWeight.Bold),
    )
}

private val geistFamily by lazy {
    FontFamily(
        Font(R.font.geist_vf, FontWeight.Normal),
        Font(R.font.geist_vf, FontWeight.Medium),
        Font(R.font.geist_vf, FontWeight.SemiBold),
        Font(R.font.geist_vf, FontWeight.Bold),
    )
}

private fun AppFontChoice.family(): FontFamily = when (this) {
    AppFontChoice.MONA_SANS_CONDENSED -> monaSansCondensedFamily
    AppFontChoice.GEIST -> geistFamily
    else -> requireNotNull(downloadableFontFamilies[this])
}

@Composable
internal fun DndCustomAidTheme(
    preferences: UiPreferences,
    content: @Composable () -> Unit,
) {
    val currentDensity = LocalDensity.current
    val adjustedDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale * (preferences.fontScalePercent / 100f),
    )
    val family = preferences.fontChoice.family()
    val typography = remember(family) { typographyWithFamily(family) }
    val colorScheme = resolveColorScheme(preferences.themeChoice)

    CompositionLocalProvider(
        LocalDensity provides adjustedDensity,
        LocalUiPreferencesV4 provides preferences,
        LocalCharacterHelpModeV4 provides preferences.helpMode,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }
}

@Composable
private fun resolveColorScheme(choice: AppThemeChoice): ColorScheme = when (choice) {
    AppThemeChoice.SYSTEM -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    AppThemeChoice.LIGHT -> lightColorScheme()
    AppThemeChoice.DARK -> darkColorScheme()
    AppThemeChoice.GRAY -> lightColorScheme(
        primary = Color(0xFF424242),
        onPrimary = Color.White,
        primaryContainer = Color(0xFF9E9E9E),
        onPrimaryContainer = Color(0xFF171717),
        secondary = Color(0xFF555555),
        secondaryContainer = Color(0xFFAAAAAA),
        background = Color(0xFFB8B8B8),
        surface = Color(0xFFCECECE),
        surfaceVariant = Color(0xFFA6A6A6),
        onBackground = Color(0xFF181818),
        onSurface = Color(0xFF181818),
        onSurfaceVariant = Color(0xFF292929),
        outline = Color(0xFF626262),
        outlineVariant = Color(0xFF8A8A8A),
    )
    AppThemeChoice.DARK_PURPLE -> darkColorScheme(
        primary = Color(0xFFD5B3FF),
        onPrimary = Color(0xFF2B0052),
        primaryContainer = Color(0xFF5A2392),
        onPrimaryContainer = Color(0xFFF0DDFF),
        secondary = Color(0xFFC8B4E3),
        secondaryContainer = Color(0xFF46345E),
        background = Color(0xFF120B1F),
        onBackground = Color(0xFFEDE4F7),
        surface = Color(0xFF1B1229),
        onSurface = Color(0xFFEDE4F7),
        surfaceVariant = Color(0xFF342448),
        onSurfaceVariant = Color(0xFFD8C8EA),
        outline = Color(0xFF9B86B2),
    )
    AppThemeChoice.DARK_CYAN -> darkColorScheme(
        primary = Color(0xFF63E6E2),
        onPrimary = Color(0xFF003735),
        primaryContainer = Color(0xFF00504D),
        onPrimaryContainer = Color(0xFF8FF4F0),
        secondary = Color(0xFFA8CECC),
        secondaryContainer = Color(0xFF294B4A),
        background = Color(0xFF071616),
        onBackground = Color(0xFFDCEDEC),
        surface = Color(0xFF0D2020),
        onSurface = Color(0xFFDCEDEC),
        surfaceVariant = Color(0xFF223838),
        onSurfaceVariant = Color(0xFFBDD0CF),
        outline = Color(0xFF829A99),
    )
    AppThemeChoice.LIGHT_CYAN -> lightColorScheme(
        primary = Color(0xFF006A67),
        onPrimary = Color.White,
        primaryContainer = Color(0xFF9CF1ED),
        onPrimaryContainer = Color(0xFF00201F),
        secondary = Color(0xFF4A6361),
        secondaryContainer = Color(0xFFCCE8E5),
        background = Color(0xFFECFBFA),
        onBackground = Color(0xFF161D1C),
        surface = Color(0xFFF7FFFE),
        onSurface = Color(0xFF161D1C),
        surfaceVariant = Color(0xFFDAE5E3),
        onSurfaceVariant = Color(0xFF3F4948),
        outline = Color(0xFF6F7978),
    )
    AppThemeChoice.NIGHT_BLUE -> darkColorScheme(
        primary = Color(0xFF8FC3FF),
        onPrimary = Color(0xFF00315C),
        primaryContainer = Color(0xFF0D4D82),
        onPrimaryContainer = Color(0xFFD1E4FF),
        secondary = Color(0xFFABC8E8),
        secondaryContainer = Color(0xFF294866),
        background = Color(0xFF061A33),
        onBackground = Color(0xFFD8E9FF),
        surface = Color(0xFF0B2749),
        onSurface = Color(0xFFD8E9FF),
        surfaceVariant = Color(0xFF173A65),
        onSurfaceVariant = Color(0xFFC1D7F0),
        outline = Color(0xFF839DBA),
    )
    AppThemeChoice.LIGHT_NIGHT_BLUE -> lightColorScheme(
        primary = Color(0xFF24558A),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD1E4FF),
        onPrimaryContainer = Color(0xFF001D35),
        secondary = Color(0xFF526070),
        secondaryContainer = Color(0xFFD6E4F5),
        background = Color(0xFFEEF4FF),
        onBackground = Color(0xFF171C22),
        surface = Color(0xFFF9FBFF),
        onSurface = Color(0xFF171C22),
        surfaceVariant = Color(0xFFDCE3ED),
        onSurfaceVariant = Color(0xFF404751),
        outline = Color(0xFF707883),
    )
    AppThemeChoice.FOREST_GREEN -> darkColorScheme(
        primary = Color(0xFFA4D49E),
        onPrimary = Color(0xFF10380F),
        primaryContainer = Color(0xFF285125),
        onPrimaryContainer = Color(0xFFC0F1B9),
        secondary = Color(0xFFB8CCB3),
        secondaryContainer = Color(0xFF354A33),
        background = Color(0xFF0B160B),
        onBackground = Color(0xFFE0EBDD),
        surface = Color(0xFF131F13),
        onSurface = Color(0xFFE0EBDD),
        surfaceVariant = Color(0xFF293A28),
        onSurfaceVariant = Color(0xFFC3D1BF),
        outline = Color(0xFF8D9C89),
    )
    AppThemeChoice.LIGHT_FOREST_GREEN -> lightColorScheme(
        primary = Color(0xFF3E6540),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC3EABD),
        onPrimaryContainer = Color(0xFF002106),
        secondary = Color(0xFF52634F),
        secondaryContainer = Color(0xFFD5E8CF),
        background = Color(0xFFF2F8EF),
        onBackground = Color(0xFF181D17),
        surface = Color(0xFFFCFFF9),
        onSurface = Color(0xFF181D17),
        surfaceVariant = Color(0xFFDFE4DA),
        onSurfaceVariant = Color(0xFF43483F),
        outline = Color(0xFF74796E),
    )
    AppThemeChoice.PARCHMENT -> lightColorScheme(
        primary = Color(0xFF5F3D16),
        onPrimary = Color(0xFFFFF5E3),
        primaryContainer = Color(0xFFD7B277),
        onPrimaryContainer = Color(0xFF241300),
        secondary = Color(0xFF76572F),
        secondaryContainer = Color(0xFFE8CFA0),
        background = Color(0xFFE8D2A6),
        onBackground = Color(0xFF2B2114),
        surface = Color(0xFFFFF3D0),
        onSurface = Color(0xFF2B2114),
        surfaceVariant = Color(0xFFD7BC86),
        onSurfaceVariant = Color(0xFF51442F),
        outline = Color(0xFF735D3C),
        outlineVariant = Color(0xFFB59B6A),
    )
    AppThemeChoice.CRIMSON -> darkColorScheme(
        primary = Color(0xFFFFB3B8),
        onPrimary = Color(0xFF650019),
        primaryContainer = Color(0xFF8E1D35),
        onPrimaryContainer = Color(0xFFFFDADD),
        secondary = Color(0xFFE6BDC0),
        secondaryContainer = Color(0xFF5A3F42),
        background = Color(0xFF1B0C10),
        onBackground = Color(0xFFF6DDE0),
        surface = Color(0xFF251216),
        onSurface = Color(0xFFF6DDE0),
        surfaceVariant = Color(0xFF52383C),
        onSurfaceVariant = Color(0xFFDCC2C5),
        outline = Color(0xFFA78C90),
    )
    AppThemeChoice.AMBER -> lightColorScheme(
        primary = Color(0xFF765800),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDEA3),
        onPrimaryContainer = Color(0xFF251A00),
        secondary = Color(0xFF6D5D3F),
        secondaryContainer = Color(0xFFF7E0B2),
        background = Color(0xFFFFF8E8),
        onBackground = Color(0xFF211B10),
        surface = Color(0xFFFFFBF2),
        onSurface = Color(0xFF211B10),
        surfaceVariant = Color(0xFFF2E3C1),
        onSurfaceVariant = Color(0xFF504733),
        outline = Color(0xFF7C715E),
        outlineVariant = Color(0xFFD0C5AA),
    )
    AppThemeChoice.GLACIER -> lightColorScheme(
        primary = Color(0xFF285D78),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC7E8F8),
        onPrimaryContainer = Color(0xFF001E2B),
        secondary = Color(0xFF4E616B),
        secondaryContainer = Color(0xFFD1E6F0),
        background = Color(0xFFF1FAFF),
        onBackground = Color(0xFF151D21),
        surface = Color(0xFFFAFDFF),
        onSurface = Color(0xFF151D21),
        surfaceVariant = Color(0xFFDCE5E9),
        onSurfaceVariant = Color(0xFF40484C),
        outline = Color(0xFF70787C),
    )
    AppThemeChoice.LAVENDER -> lightColorScheme(
        primary = Color(0xFF66558A),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE9DDFF),
        onPrimaryContainer = Color(0xFF211047),
        secondary = Color(0xFF625B70),
        secondaryContainer = Color(0xFFE8DEF8),
        background = Color(0xFFFBF8FF),
        onBackground = Color(0xFF1D1A20),
        surface = Color(0xFFFFF8FF),
        onSurface = Color(0xFF1D1A20),
        surfaceVariant = Color(0xFFE8E0EB),
        onSurfaceVariant = Color(0xFF4A454E),
        outline = Color(0xFF7B757F),
    )
    AppThemeChoice.SLATE -> darkColorScheme(
        primary = Color(0xFF8FD3FF),
        onPrimary = Color(0xFF00344B),
        primaryContainer = Color(0xFF22506B),
        onPrimaryContainer = Color(0xFFC8E6FF),
        secondary = Color(0xFFAEC8D8),
        secondaryContainer = Color(0xFF304955),
        background = Color(0xFF0B1218),
        onBackground = Color(0xFFDDE8EF),
        surface = Color(0xFF14212B),
        onSurface = Color(0xFFDDE8EF),
        surfaceVariant = Color(0xFF2A3C49),
        onSurfaceVariant = Color(0xFFC1D1DC),
        outline = Color(0xFF89A7B8),
        outlineVariant = Color(0xFF465E6C),
    )
    AppThemeChoice.TERRACOTTA -> lightColorScheme(
        primary = Color(0xFFA23F28),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFD7CC),
        onPrimaryContainer = Color(0xFF3C0800),
        secondary = Color(0xFF8A4F3E),
        secondaryContainer = Color(0xFFFFDCD2),
        background = Color(0xFFFFF3ED),
        onBackground = Color(0xFF271712),
        surface = Color(0xFFFFFAF7),
        onSurface = Color(0xFF271712),
        surfaceVariant = Color(0xFFF3D3C8),
        onSurfaceVariant = Color(0xFF5B3E35),
        outline = Color(0xFF966A5C),
        outlineVariant = Color(0xFFD9B8AD),
    )
    AppThemeChoice.HIGH_CONTRAST -> darkColorScheme(
        primary = Color(0xFFFFFF00),
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF333300),
        onPrimaryContainer = Color(0xFFFFFF66),
        secondary = Color.White,
        onSecondary = Color.Black,
        background = Color.Black,
        onBackground = Color.White,
        surface = Color.Black,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF161616),
        onSurfaceVariant = Color.White,
        outline = Color.White,
        outlineVariant = Color(0xFFBDBDBD),
    )
    AppThemeChoice.MATRIX -> darkColorScheme(
        primary = Color(0xFF48FF73),
        onPrimary = Color(0xFF001B07),
        primaryContainer = Color(0xFF073D16),
        onPrimaryContainer = Color(0xFF79FF96),
        secondary = Color(0xFF8DDB9C),
        secondaryContainer = Color(0xFF183A20),
        background = Color(0xFF020703),
        onBackground = Color(0xFFC8FFD1),
        surface = Color(0xFF071009),
        onSurface = Color(0xFFC8FFD1),
        surfaceVariant = Color(0xFF102516),
        onSurfaceVariant = Color(0xFFA5DCAF),
        outline = Color(0xFF55A666),
    )
}

private fun typographyWithFamily(family: FontFamily): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = family),
        displayMedium = base.displayMedium.copy(fontFamily = family),
        displaySmall = base.displaySmall.copy(fontFamily = family),
        headlineLarge = base.headlineLarge.copy(fontFamily = family),
        headlineMedium = base.headlineMedium.copy(fontFamily = family),
        headlineSmall = base.headlineSmall.copy(fontFamily = family),
        titleLarge = base.titleLarge.copy(fontFamily = family),
        titleMedium = base.titleMedium.copy(fontFamily = family),
        titleSmall = base.titleSmall.copy(fontFamily = family),
        bodyLarge = base.bodyLarge.copy(fontFamily = family),
        bodyMedium = base.bodyMedium.copy(fontFamily = family),
        bodySmall = base.bodySmall.copy(fontFamily = family),
        labelLarge = base.labelLarge.copy(fontFamily = family),
        labelMedium = base.labelMedium.copy(fontFamily = family),
        labelSmall = base.labelSmall.copy(fontFamily = family),
    )
}

@Composable
internal fun AppSettingsScreen(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    var showAbout by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val phoneLike = minOf(configuration.screenWidthDp, configuration.screenHeightDp) < 600
    val veryLargePhoneText = phoneLike && preferences.fontScalePercent >= 145

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                StableBackIconButton(
                    onClick = onDismiss,
                    contentDescription = "Volver desde Configuración de la aplicación",
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("Configuración de la aplicación", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Cambios inmediatos · preferencias de este dispositivo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(onClick = { showAbout = true }) { Text("Acerca de") }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = appSpacingV4(8.dp),
                    end = appSpacingV4(8.dp),
                    top = appSpacingV4(4.dp),
                    bottom = appSpacingV4(18.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(9.dp)),
            ) {
                item {
                    SteppedPercentSettingV4(
                        label = "Tamaño de texto",
                        value = preferences.fontScalePercent,
                        options = FONT_SCALE_OPTIONS,
                        onSelect = { onPreferencesChange(preferences.copy(fontScalePercent = it)) },
                        previewTitle = "Ejemplo de texto",
                        previewPrimary = "Alyra Voss · Maga 7",
                        previewSecondary = "CD 15 · CA 17 · 1d20 + 7",
                    )
                    if (veryLargePhoneText) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                "Advertencia para teléfono: ${preferences.fontScalePercent}% reduce mucho el área útil. La app conserva el valor y recurre a scroll cuando sea necesario.",
                                modifier = Modifier.padding(appSpacingV4(7.dp)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    }
                }
                item {
                    SteppedPercentSettingV4(
                        label = "Compactación de espacios",
                        value = preferences.spacingScalePercent,
                        options = SPACING_SCALE_OPTIONS,
                        onSelect = { onPreferencesChange(preferences.copy(spacingScalePercent = it)) },
                        previewTitle = "Ejemplo de espaciado",
                        previewPrimary = "Tarjeta compacta",
                        previewSecondary = "Margen · separación · contenido",
                    )
                    Text(
                        "No reemplaza la vista Supercompacta. Reduce márgenes, paddings y separaciones controlados por la app; iconos y touch targets conservan su tamaño.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Ayuda contextual",
                            value = preferences.helpMode.label,
                            options = CharacterHelpModeV4.entries,
                            optionLabel = { it.label },
                            onSelect = { onPreferencesChange(preferences.copy(helpMode = it)) },
                        )
                        Text(
                            "Controla si las explicaciones aparecen siempre, desde ⓘ, o se ocultan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item { HapticDeviceSettingsV4() }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Resultados de dados",
                            value = preferences.diceResultMode.label,
                            options = DiceResultModeChoice.entries,
                            optionLabel = { it.label },
                            onSelect = { onPreferencesChange(preferences.copy(diceResultMode = it)) },
                        )
                        Text(
                            "El resultado compacto prioriza densidad. Dados visibles muestra los d20 de forma prominente; ambos conservan la misma descomposición matemática.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    FontChoicePicker(
                        selected = preferences.fontChoice,
                        onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },
                    )
                }
                item {
                    LayoutColumnSettingsV4(
                        preferences = preferences,
                        onPreferencesChange = onPreferencesChange,
                    )
                }
                item {
                    ThemeChoicePicker(
                        selected = preferences.themeChoice,
                        onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
                    )
                }
                item { SettingsSheetPreview(preferences) }
            }
        }
    }

    if (showAbout) {
        AboutBuildDialogV4(onDismiss = { showAbout = false })
    }
}

@Composable
private fun SteppedPercentSettingV4(
    label: String,
    value: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit,
    previewTitle: String,
    previewPrimary: String,
    previewSecondary: String,
) {
    val currentIndex = options.indexOf(value).coerceAtLeast(0)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text("$value%", style = MaterialTheme.typography.titleSmall)
        }
        Slider(
            value = currentIndex.toFloat(),
            onValueChange = { rawIndex ->
                val index = rawIndex.roundToInt().coerceIn(options.indices)
                val selected = options[index]
                if (selected != value) onSelect(selected)
            },
            valueRange = 0f..options.lastIndex.toFloat(),
            steps = (options.size - 2).coerceAtLeast(0),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("${options.first()}%", style = MaterialTheme.typography.labelSmall)
            Text("${options.last()}%", style = MaterialTheme.typography.labelSmall)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier.padding(appSpacingV4(7.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                Text(previewTitle, style = MaterialTheme.typography.labelSmall)
                Text(previewPrimary, style = MaterialTheme.typography.titleSmall)
                Text(previewSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun HapticDeviceSettingsV4() {
    val hapticContext = LocalCharacterHapticSettingsV4.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Respuesta háptica · dispositivo", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SettingSelector(
                    label = "Intensidad",
                    value = hapticContext.preferences.strength.label,
                    options = CharacterHapticStrengthV4.entries,
                    optionLabel = { it.label },
                    onSelect = { option ->
                        hapticContext.onChange(hapticContext.preferences.copy(strength = option))
                    },
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                SettingSelector(
                    label = "Duración",
                    value = hapticContext.preferences.duration.label,
                    options = CharacterHapticDurationV4.entries,
                    optionLabel = { it.label },
                    onSelect = { option ->
                        hapticContext.onChange(hapticContext.preferences.copy(duration = option))
                    },
                )
            }
        }
        CharacterHelpV4(
            "Intensidad y duración son preferencias globales de este dispositivo. Activar o desactivar la respuesta háptica sigue perteneciendo a cada ficha de personaje.",
        )
    }
}

@Composable
private fun AboutBuildDialogV4(onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.imePadding().navigationBarsPadding(),
        onDismissRequest = onDismiss,
        title = { Text("Acerca de D&D Custom Aid") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(
                        modifier = Modifier.padding(appSpacingV4(8.dp)),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                    ) {
                        Text("VERSIÓN", style = MaterialTheme.typography.labelMedium)
                        Text(BuildConfig.VERSION_NAME, style = MaterialTheme.typography.titleLarge)
                        Text("BUILD", style = MaterialTheme.typography.labelMedium)
                        Text(BuildConfig.VERSION_CODE.toString(), style = MaterialTheme.typography.titleLarge)
                        Text("TIPO", style = MaterialTheme.typography.labelMedium)
                        Text(BuildConfig.BUILD_TYPE.uppercase(), style = MaterialTheme.typography.titleMedium)
                    }
                }
                Text(
                    "Revisión actual: versión ${BuildConfig.VERSION_NAME} · build ${BuildConfig.VERSION_CODE} · ${BuildConfig.BUILD_TYPE}.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(BuildConfig.APPLICATION_ID, style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Cerrar") } },
    )
}

@Composable
private fun LayoutColumnSettingsV4(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        Text("Columnas de tarjetas", style = MaterialTheme.typography.labelLarge)
        Text(
            "Cada orientación conserva su propia preferencia. La miniatura muestra la distribución solicitada con el espaciado actual.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ColumnCountSettingV4(
            label = "Teléfono · vertical",
            value = preferences.phonePortraitColumns,
            options = (1..4).toList(),
            onSelect = { onPreferencesChange(preferences.copy(phonePortraitColumns = it)) },
        )
        ColumnCountSettingV4(
            label = "Teléfono · horizontal",
            value = preferences.phoneLandscapeColumns,
            options = (1..5).toList(),
            onSelect = { onPreferencesChange(preferences.copy(phoneLandscapeColumns = it)) },
        )
        ColumnCountSettingV4(
            label = "Tablet · vertical",
            value = preferences.tabletPortraitColumns,
            options = (1..5).toList(),
            onSelect = { onPreferencesChange(preferences.copy(tabletPortraitColumns = it)) },
        )
        ColumnCountSettingV4(
            label = "Tablet · horizontal",
            value = preferences.tabletLandscapeColumns,
            options = (1..6).toList(),
            onSelect = { onPreferencesChange(preferences.copy(tabletLandscapeColumns = it)) },
        )
    }
}

@Composable
private fun ColumnCountSettingV4(
    label: String,
    value: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
    ) {
        SettingSelector(
            label = label,
            value = value.toString(),
            options = options,
            optionLabel = Int::toString,
            onSelect = onSelect,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(appSpacingV4(4.dp)),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                repeat(value) { index ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Text(
                            if (index == 0) "Aa" else "${index + 1}",
                            modifier = Modifier.padding(horizontal = appSpacingV4(3.dp), vertical = appSpacingV4(4.dp)),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSheetPreview(preferences: UiPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Vista previa · ficha", style = MaterialTheme.typography.labelLarge)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(appSpacingV4(6.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(4.dp)),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                    ) {
                        Text(
                            "Alyra Voss",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            "Maga 7 · Evocación",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                            CharacterSemanticBadgeV4(
                                label = characterRulesFamilyBadgeLabel(CharacterRulesFamily.DND_5_5E),
                                kind = CharacterSemanticBadgeKindV4.RULES,
                            )
                            CharacterSemanticBadgeV4(
                                label = "Preparado",
                                kind = CharacterSemanticBadgeKindV4.STATE,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    SettingsPreviewStatCell("CA", "17", Modifier.weight(1f))
                    SettingsPreviewStatCell("PG", "42 / 42", Modifier.weight(1f))
                    SettingsPreviewStatCell("CD", "15", Modifier.weight(1f))
                }

                Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
                    Text("Bola de fuego · Nivel 3", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Concentración · V/S/M",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                OutlinedTextField(
                    value = "Texto libre para historia, notas o descripciones largas. Este ejemplo muestra un área abierta real, no una tarjeta.",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Texto libre") },
                    minLines = 2,
                    maxLines = 3,
                )

                Text(
                    "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · Ayuda ${preferences.helpMode.label} · Dados ${preferences.diceResultMode.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingsPreviewStatCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(3.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun FontChoicePicker(
    selected: AppFontChoice,
    onSelect: (AppFontChoice) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Tipografía", style = MaterialTheme.typography.labelLarge)
        SELECTABLE_FONT_CHOICES_V4.forEach { choice ->
            val isSelected = choice == selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(choice) },
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                ),
                tonalElevation = if (isSelected) 2.dp else 0.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(5.dp)),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
                    ) {
                        Text(
                            choice.label,
                            style = MaterialTheme.typography.titleSmall.copy(fontFamily = choice.family()),
                            maxLines = 1,
                        )
                        Text(
                            "Aventura · CD 15 · CA 17 · 1d20 + 7",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = choice.family()),
                            maxLines = 1,
                        )
                    }
                    if (isSelected) {
                        Text("Seleccionada", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeChoicePicker(
    selected: AppThemeChoice,
    onSelect: (AppThemeChoice) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Tema", style = MaterialTheme.typography.labelLarge)
        AppThemeChoice.entries.chunked(2).forEach { rowThemes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                rowThemes.forEach { choice ->
                    ThemePreviewCard(
                        choice = choice,
                        selected = choice == selected,
                        onSelect = { onSelect(choice) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(2 - rowThemes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ThemePreviewCard(
    choice: AppThemeChoice,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(5.dp)),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(choice.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, maxLines = 2)
            if (selected) Text("✓", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun <T> SettingSelector(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(value, maxLines = 2)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option), maxLines = 2) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}