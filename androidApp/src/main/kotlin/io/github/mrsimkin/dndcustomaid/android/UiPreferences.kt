package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleDownloadableFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

internal enum class AppFontChoice(val label: String, val googleFontName: String) {
    MANROPE("Manrope", "Manrope"),
    SORA("Sora", "Sora"),
    BARLOW_CONDENSED("Barlow Condensed", "Barlow Condensed"),
    IBM_PLEX_SANS_CONDENSED("IBM Plex Sans Condensed", "IBM Plex Sans Condensed"),
}

internal enum class AppThemeChoice(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
    LIGHT_GRAY("Gris claro"),
    DARK_PURPLE("Morado oscuro"),
}

internal enum class SkillLayoutChoice(val label: String) {
    BY_SKILLS("Por habilidades"),
    BY_ATTRIBUTE("Por atributo"),
}

internal data class UiPreferences(
    val fontScalePercent: Int = 100,
    val fontChoice: AppFontChoice = AppFontChoice.MANROPE,
    val themeChoice: AppThemeChoice = AppThemeChoice.SYSTEM,
    val skillLayoutChoice: SkillLayoutChoice = SkillLayoutChoice.BY_SKILLS,
)

internal class UiPreferencesStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): UiPreferences {
        val scale = preferences.getInt(KEY_FONT_SCALE, 100).takeIf { it in FONT_SCALE_OPTIONS } ?: 100
        val font = preferences.getString(KEY_FONT, null)
            ?.let { runCatching { AppFontChoice.valueOf(it) }.getOrNull() }
            ?: AppFontChoice.MANROPE
        val theme = preferences.getString(KEY_THEME, null)
            ?.let { runCatching { AppThemeChoice.valueOf(it) }.getOrNull() }
            ?: AppThemeChoice.SYSTEM
        val skillLayout = preferences.getString(KEY_SKILL_LAYOUT, null)
            ?.let { runCatching { SkillLayoutChoice.valueOf(it) }.getOrNull() }
            ?: SkillLayoutChoice.BY_SKILLS

        return UiPreferences(
            fontScalePercent = scale,
            fontChoice = font,
            themeChoice = theme,
            skillLayoutChoice = skillLayout,
        )
    }

    fun save(value: UiPreferences) {
        preferences.edit()
            .putInt(KEY_FONT_SCALE, value.fontScalePercent)
            .putString(KEY_FONT, value.fontChoice.name)
            .putString(KEY_THEME, value.themeChoice.name)
            .putString(KEY_SKILL_LAYOUT, value.skillLayoutChoice.name)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "ui_preferences"
        const val KEY_FONT_SCALE = "font_scale"
        const val KEY_FONT = "font_family"
        const val KEY_THEME = "theme"
        const val KEY_SKILL_LAYOUT = "skill_layout"
    }
}

internal val FONT_SCALE_OPTIONS = listOf(80, 90, 100, 115, 130)

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

private val manropeFamily by lazy { downloadableFontFamily(AppFontChoice.MANROPE.googleFontName) }
private val soraFamily by lazy { downloadableFontFamily(AppFontChoice.SORA.googleFontName) }
private val barlowCondensedFamily by lazy { downloadableFontFamily(AppFontChoice.BARLOW_CONDENSED.googleFontName) }
private val ibmPlexSansCondensedFamily by lazy { downloadableFontFamily(AppFontChoice.IBM_PLEX_SANS_CONDENSED.googleFontName) }

private fun AppFontChoice.family(): FontFamily = when (this) {
    AppFontChoice.MANROPE -> manropeFamily
    AppFontChoice.SORA -> soraFamily
    AppFontChoice.BARLOW_CONDENSED -> barlowCondensedFamily
    AppFontChoice.IBM_PLEX_SANS_CONDENSED -> ibmPlexSansCondensedFamily
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

    CompositionLocalProvider(LocalDensity provides adjustedDensity) {
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
    AppThemeChoice.LIGHT_GRAY -> lightColorScheme(
        primary = Color(0xFF3F566B),
        onPrimary = Color.White,
        background = Color(0xFFD9DDE2),
        surface = Color(0xFFE5E8EC),
        surfaceVariant = Color(0xFFCCD2D9),
        onBackground = Color(0xFF191C20),
        onSurface = Color(0xFF191C20),
        onSurfaceVariant = Color(0xFF40464D),
        outline = Color(0xFF70777F),
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
internal fun AppSettingsDialog(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustes") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SettingSelector(
                    label = "Tamaño de texto",
                    value = "${preferences.fontScalePercent}%",
                    options = FONT_SCALE_OPTIONS,
                    optionLabel = { "$it%" },
                    onSelect = { onPreferencesChange(preferences.copy(fontScalePercent = it)) },
                )
                SettingSelector(
                    label = "Tipografía",
                    value = preferences.fontChoice.label,
                    options = AppFontChoice.entries,
                    optionLabel = { it.label },
                    onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },
                )
                SettingSelector(
                    label = "Tema",
                    value = preferences.themeChoice.label,
                    options = AppThemeChoice.entries,
                    optionLabel = { it.label },
                    onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
                )
                Text(
                    "La organización de habilidades se configura desde la ficha.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Listo") }
        },
    )
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
        verticalArrangement = Arrangement.spacedBy(4.dp),
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
