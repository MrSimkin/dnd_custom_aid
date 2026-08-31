package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
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
    SOURCE_SANS_3("Source Sans 3", "Source Sans 3"),
    ROBOTO_CONDENSED("Roboto Condensed", "Roboto Condensed"),
    ARCHIVO_NARROW("Archivo Narrow", "Archivo Narrow"),
}

internal enum class AppThemeChoice(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
    GRAY("Gris"),
    DARK_PURPLE("Morado oscuro"),
    DARK_CYAN("Cian oscuro"),
    LIGHT_CYAN("Cian claro"),
    NIGHT_BLUE("Azul noche"),
    LIGHT_NIGHT_BLUE("Azul noche claro"),
    FOREST_GREEN("Verde bosque"),
    LIGHT_FOREST_GREEN("Verde bosque claro"),
    PARCHMENT("Pergamino"),
    HIGH_CONTRAST("Alto contraste"),
    MATRIX("Matrix"),
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
        val font = when (val stored = preferences.getString(KEY_FONT, null)) {
            "IBM_PLEX_SANS_CONDENSED", "BARLOW_CONDENSED" -> AppFontChoice.ROBOTO_CONDENSED
            "LEXEND" -> AppFontChoice.SORA
            "OSWALD" -> AppFontChoice.MANROPE
            else -> stored
                ?.let { runCatching { AppFontChoice.valueOf(it) }.getOrNull() }
                ?: AppFontChoice.MANROPE
        }
        val theme = when (val stored = preferences.getString(KEY_THEME, null)) {
            "LIGHT_GRAY" -> AppThemeChoice.GRAY
            else -> stored
                ?.let { runCatching { AppThemeChoice.valueOf(it) }.getOrNull() }
                ?: AppThemeChoice.SYSTEM
        }
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

private val fontFamilies: Map<AppFontChoice, FontFamily> by lazy {
    AppFontChoice.entries.associateWith { downloadableFontFamily(it.googleFontName) }
}

private fun AppFontChoice.family(): FontFamily = requireNotNull(fontFamilies[this])

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
internal fun AppSettingsDialog(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustes") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    SettingSelector(
                        label = "Tamaño de texto",
                        value = "${preferences.fontScalePercent}%",
                        options = FONT_SCALE_OPTIONS,
                        optionLabel = { "$it%" },
                        onSelect = { onPreferencesChange(preferences.copy(fontScalePercent = it)) },
                    )
                }
                item {
                    FontChoicePicker(
                        selected = preferences.fontChoice,
                        onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },
                    )
                }
                item {
                    ThemeChoicePicker(
                        selected = preferences.themeChoice,
                        onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
                    )
                }
                item {
                    Text(
                        "La organización de habilidades se configura desde la ficha. Las tipografías mostradas aquí ya están depuradas; futuras sustituciones sólo se añadirán después de una audición específica.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Listo") }
        },
    )
}

@Composable
private fun FontChoicePicker(
    selected: AppFontChoice,
    onSelect: (AppFontChoice) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text("Tipografía · audición", style = MaterialTheme.typography.labelLarge)
        AppFontChoice.entries.forEach { choice ->
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${choice.label} · Aa Bb 123",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall.copy(fontFamily = choice.family()),
                        maxLines = 1,
                    )
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
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text("Tema · audición", style = MaterialTheme.typography.labelLarge)
        AppThemeChoice.entries.chunked(2).forEach { rowThemes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
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
    val scheme = resolveColorScheme(choice)
    Surface(
        modifier = modifier.clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(choice.label, style = MaterialTheme.typography.labelMedium, maxLines = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(scheme.background, MaterialTheme.shapes.extraSmall),
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(scheme.surfaceVariant, MaterialTheme.shapes.extraSmall),
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(scheme.primary, MaterialTheme.shapes.extraSmall),
                )
            }
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
