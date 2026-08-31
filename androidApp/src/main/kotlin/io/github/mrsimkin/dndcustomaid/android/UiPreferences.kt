package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    ATKINSON("Atkinson Hyperlegible Next", "Atkinson Hyperlegible Next"),
    BARLOW_CONDENSED("Barlow Condensed", "Barlow Condensed"),
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
private val atkinsonFamily by lazy { downloadableFontFamily(AppFontChoice.ATKINSON.googleFontName) }
private val barlowCondensedFamily by lazy { downloadableFontFamily(AppFontChoice.BARLOW_CONDENSED.googleFontName) }

private fun AppFontChoice.family(): FontFamily = when (this) {
    AppFontChoice.MANROPE -> manropeFamily
    AppFontChoice.ATKINSON -> atkinsonFamily
    AppFontChoice.BARLOW_CONDENSED -> barlowCondensedFamily
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
        background = Color(0xFFF0F1F3),
        surface = Color(0xFFE7E9ED),
        surfaceVariant = Color(0xFFDDE0E5),
        onBackground = Color(0xFF1B1B1F),
        onSurface = Color(0xFF1B1B1F),
    )
    AppThemeChoice.DARK_PURPLE -> darkColorScheme(
        primary = Color(0xFFD7B8FF),
        onPrimary = Color(0xFF35105A),
        primaryContainer = Color(0xFF4D1D78),
        onPrimaryContainer = Color(0xFFF0DBFF),
        secondary = Color(0xFFCBB7D9),
        background = Color(0xFF160E1E),
        onBackground = Color(0xFFF0E7F4),
        surface = Color(0xFF21152B),
        onSurface = Color(0xFFF0E7F4),
        surfaceVariant = Color(0xFF33223F),
        onSurfaceVariant = Color(0xFFD8C7DF),
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                    "La vista de habilidades se configura desde la propia ficha.",
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
        Column {
            OutlinedButton(onClick = { expanded = true }) {
                Text("Cambiar")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
