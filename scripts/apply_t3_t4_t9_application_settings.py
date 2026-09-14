#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
UI = ROOT / "UiPreferences.kt"
RESPONSIVE = ROOT / "CharacterResponsivePreferencesV4.kt"
HAPTIC_PREFS = ROOT / "CharacterHapticPreferencesV4.kt"
HAPTIC_HOOK = ROOT / "CharacterCollectionPrimitivesV4.kt"
MARKER = "enum class CharacterCardDensityV4"


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, observed {count}")
    return text.replace(old, new, 1)


def migrate_ui() -> None:
    text = UI.read_text(encoding="utf-8")
    if MARKER in text:
        print("UiPreferences T3/T4 migration already applied.")
        return

    text = replace_once(
        text,
        '''internal enum class DiceResultModeChoice(val label: String) {
    COMPACT("Resultado compacto"),
    VISIBLE_DICE("Dados visibles"),
}

internal data class UiPreferences(''',
        '''internal enum class DiceResultModeChoice(val label: String) {
    COMPACT("Resultado compacto"),
    VISIBLE_DICE("Dados visibles"),
}

internal enum class CharacterCardDensityV4(val label: String, val minCardWidthDp: Int) {
    COMFORTABLE("Cómodo", 420),
    BALANCED("Equilibrado", 340),
    COMPACT("Compacto", 280),
    DENSE("Denso", 230),
}

internal data class UiPreferences(''',
        "adaptive card-density enum",
    )

    text = replace_once(
        text,
        '''    val tabletPortraitColumns: Int = 2,
    val tabletLandscapeColumns: Int = 3,
    val spacingScalePercent: Int = 100,''',
        '''    val tabletPortraitColumns: Int = 2,
    val tabletLandscapeColumns: Int = 3,
    val portraitCardDensity: CharacterCardDensityV4 = CharacterCardDensityV4.BALANCED,
    val landscapeCardDensity: CharacterCardDensityV4 = CharacterCardDensityV4.BALANCED,
    val spacingScalePercent: Int = 100,''',
        "density preference fields",
    )

    text = replace_once(
        text,
        '''        val scale = preferences.getInt(KEY_FONT_SCALE, 100).takeIf { it in FONT_SCALE_OPTIONS } ?: 100''',
        '''        val storedScale = preferences.getInt(KEY_FONT_SCALE, 100)
        val scale = FONT_SCALE_OPTIONS.minByOrNull { abs(it - storedScale.coerceIn(50, 150)) } ?: 100''',
        "T4 nearest text-scale migration",
    )

    text = replace_once(
        text,
        '''        val tabletPortraitColumns = preferences.getInt(KEY_TABLET_PORTRAIT_COLUMNS, 2).coerceIn(1, 5)
        val tabletLandscapeColumns = preferences.getInt(KEY_TABLET_LANDSCAPE_COLUMNS, 3).coerceIn(1, 6)
        val storedSpacing = preferences.getInt(KEY_SPACING_SCALE, 100)''',
        '''        val tabletPortraitColumns = preferences.getInt(KEY_TABLET_PORTRAIT_COLUMNS, 2).coerceIn(1, 5)
        val tabletLandscapeColumns = preferences.getInt(KEY_TABLET_LANDSCAPE_COLUMNS, 3).coerceIn(1, 6)
        val portraitCardDensity = preferences.getString(KEY_PORTRAIT_CARD_DENSITY, null)
            ?.let { runCatching { CharacterCardDensityV4.valueOf(it) }.getOrNull() }
            ?: legacyCardDensityV4(phonePortraitColumns, tabletPortraitColumns, landscape = false)
        val landscapeCardDensity = preferences.getString(KEY_LANDSCAPE_CARD_DENSITY, null)
            ?.let { runCatching { CharacterCardDensityV4.valueOf(it) }.getOrNull() }
            ?: legacyCardDensityV4(phoneLandscapeColumns, tabletLandscapeColumns, landscape = true)
        val storedSpacing = preferences.getInt(KEY_SPACING_SCALE, 100)''',
        "legacy exact-column density migration",
    )

    text = replace_once(
        text,
        '''            tabletPortraitColumns = tabletPortraitColumns,
            tabletLandscapeColumns = tabletLandscapeColumns,
            spacingScalePercent = spacingScalePercent,''',
        '''            tabletPortraitColumns = tabletPortraitColumns,
            tabletLandscapeColumns = tabletLandscapeColumns,
            portraitCardDensity = portraitCardDensity,
            landscapeCardDensity = landscapeCardDensity,
            spacingScalePercent = spacingScalePercent,''',
        "loaded density preferences",
    )

    text = replace_once(
        text,
        '''            .putInt(KEY_TABLET_PORTRAIT_COLUMNS, value.tabletPortraitColumns)
            .putInt(KEY_TABLET_LANDSCAPE_COLUMNS, value.tabletLandscapeColumns)
            .putInt(KEY_SPACING_SCALE, value.spacingScalePercent)''',
        '''            .putInt(KEY_TABLET_PORTRAIT_COLUMNS, value.tabletPortraitColumns)
            .putInt(KEY_TABLET_LANDSCAPE_COLUMNS, value.tabletLandscapeColumns)
            .putString(KEY_PORTRAIT_CARD_DENSITY, value.portraitCardDensity.name)
            .putString(KEY_LANDSCAPE_CARD_DENSITY, value.landscapeCardDensity.name)
            .putInt(KEY_SPACING_SCALE, value.spacingScalePercent)''',
        "persist adaptive density preferences",
    )

    text = replace_once(
        text,
        '''        const val KEY_TABLET_PORTRAIT_COLUMNS = "tablet_portrait_columns"
        const val KEY_TABLET_LANDSCAPE_COLUMNS = "tablet_landscape_columns"
        const val KEY_SPACING_SCALE = "spacing_scale_percent"''',
        '''        const val KEY_TABLET_PORTRAIT_COLUMNS = "tablet_portrait_columns"
        const val KEY_TABLET_LANDSCAPE_COLUMNS = "tablet_landscape_columns"
        const val KEY_PORTRAIT_CARD_DENSITY = "portrait_card_density"
        const val KEY_LANDSCAPE_CARD_DENSITY = "landscape_card_density"
        const val KEY_SPACING_SCALE = "spacing_scale_percent"''',
        "adaptive density keys",
    )

    text = replace_once(
        text,
        '''internal val FONT_SCALE_OPTIONS = listOf(70, 80, 90, 100, 110, 120, 130, 145, 160, 180, 200)
internal val SPACING_SCALE_OPTIONS = (50..150 step 10).toList()''',
        '''internal fun legacyCardDensityV4(
    phoneColumns: Int,
    tabletColumns: Int,
    landscape: Boolean,
): CharacterCardDensityV4 {
    val score = phoneColumns.coerceAtLeast(1) + tabletColumns.coerceAtLeast(1)
    return if (landscape) {
        when {
            score <= 4 -> CharacterCardDensityV4.COMFORTABLE
            score <= 5 -> CharacterCardDensityV4.BALANCED
            score <= 7 -> CharacterCardDensityV4.COMPACT
            else -> CharacterCardDensityV4.DENSE
        }
    } else {
        when {
            score <= 2 -> CharacterCardDensityV4.COMFORTABLE
            score <= 3 -> CharacterCardDensityV4.BALANCED
            score <= 5 -> CharacterCardDensityV4.COMPACT
            else -> CharacterCardDensityV4.DENSE
        }
    }
}

internal val FONT_SCALE_OPTIONS = (50..150 step 10).toList()
internal val SPACING_SCALE_OPTIONS = (50..150 step 10).toList()''',
        "legacy mapping and symmetric font options",
    )

    old_layout = '''@Composable
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
            "Cada orientación conserva su propia preferencia. La miniatura muestra la distribución con el espaciado actual.",
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
'''
    new_layout = '''@Composable
private fun LayoutColumnSettingsV4(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        Text("Distribución de tarjetas", style = MaterialTheme.typography.labelLarge)
        Text(
            "Cada orientación conserva una preferencia adaptativa. La app calcula cuántas tarjetas caben con el ancho disponible, el tamaño de texto y la densidad de espacios; no promete un número exacto de columnas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SettingSelector(
            label = "Vertical",
            value = preferences.portraitCardDensity.label,
            options = CharacterCardDensityV4.entries,
            optionLabel = { it.label },
            onSelect = { onPreferencesChange(preferences.copy(portraitCardDensity = it)) },
        )
        SettingSelector(
            label = "Horizontal",
            value = preferences.landscapeCardDensity.label,
            options = CharacterCardDensityV4.entries,
            optionLabel = { it.label },
            onSelect = { onPreferencesChange(preferences.copy(landscapeCardDensity = it)) },
        )
        Text(
            "Cómodo prioriza tarjetas más anchas; Equilibrado es el valor normal; Compacto y Denso aprovechan progresivamente más ancho cuando sigue siendo legible.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
'''
    text = replace_once(text, old_layout, new_layout, "adaptive settings UI")

    text = replace_once(
        text,
        '''            "Intensidad y duración son preferencias globales de este dispositivo. Activar o desactivar la respuesta háptica sigue perteneciendo a cada ficha de personaje.",''',
        '''            "Ninguna desactiva la respuesta háptica generada por la app en este dispositivo. Intensidad y duración son globales; el interruptor de cada ficha sigue permitiendo desactivar sus hápticos individualmente.",''',
        "T9 settings help",
    )

    UI.write_text(text, encoding="utf-8")


def migrate_responsive() -> None:
    text = RESPONSIVE.read_text(encoding="utf-8")
    if "adaptiveCardColumnsV4" in text:
        print("Responsive T3 migration already applied.")
        return
    RESPONSIVE.write_text(
        '''package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
internal fun requestedCardColumnsV4(): Int {
    val layoutContext = characterLayoutContextV4()
    return adaptiveCardColumnsV4(
        preferences = LocalUiPreferencesV4.current,
        layoutContext = layoutContext,
        wide = layoutContext.isTablet,
        phoneMax = 4,
        wideMax = 6,
        effectiveFontScale = LocalDensity.current.fontScale,
    )
}

@Composable
internal fun constrainedCardColumnsV4(
    wide: Boolean,
    phoneMax: Int = 2,
    wideMax: Int = 4,
): Int = adaptiveCardColumnsV4(
    preferences = LocalUiPreferencesV4.current,
    layoutContext = characterLayoutContextV4(),
    wide = wide,
    phoneMax = phoneMax,
    wideMax = wideMax,
    effectiveFontScale = LocalDensity.current.fontScale,
)

internal fun adaptiveCardColumnsV4(
    preferences: UiPreferences,
    layoutContext: CharacterLayoutContextV4,
    wide: Boolean,
    phoneMax: Int,
    wideMax: Int,
    effectiveFontScale: Float,
): Int {
    val density = if (layoutContext.isLandscape) {
        preferences.landscapeCardDensity
    } else {
        preferences.portraitCardDensity
    }
    val baseMax = if (layoutContext.isTablet && wide) wideMax else phoneMax
    val textPressure = effectiveFontScale.coerceIn(0.75f, 1.80f)
    val spacingPressure = preferences.spacingScalePercent.coerceIn(50, 150) / 100f
    val uiPressure = 0.90f + (0.10f * spacingPressure)
    val minimumUsableCardWidthDp = density.minCardWidthDp * textPressure * uiPressure
    val widthBound = (layoutContext.availableWidthDp.coerceAtLeast(1) / minimumUsableCardWidthDp)
        .toInt()
        .coerceAtLeast(1)
    return widthBound.coerceIn(1, baseMax.coerceAtLeast(1))
}
''',
        encoding="utf-8",
    )


def migrate_haptics() -> None:
    text = HAPTIC_PREFS.read_text(encoding="utf-8")
    if 'NONE("Ninguna", 0)' not in text:
        text = replace_once(
            text,
            '''internal enum class CharacterHapticStrengthV4(val label: String, val amplitude: Int) {
    SOFT("Suave", 72),''',
            '''internal enum class CharacterHapticStrengthV4(val label: String, val amplitude: Int) {
    NONE("Ninguna", 0),
    SOFT("Suave", 72),''',
            "T9 None strength",
        )
        HAPTIC_PREFS.write_text(text, encoding="utf-8")

    hook = HAPTIC_HOOK.read_text(encoding="utf-8")
    if "hapticPreferences.strength != CharacterHapticStrengthV4.NONE" not in hook:
        hook = replace_once(
            hook,
            '''            if (enabled) {
                val vibrator = characterVibratorV4(context)''',
            '''            if (enabled && hapticPreferences.strength != CharacterHapticStrengthV4.NONE) {
                val vibrator = characterVibratorV4(context)''',
            "T9 dispatch short-circuit",
        )
        HAPTIC_HOOK.write_text(hook, encoding="utf-8")


def main() -> None:
    migrate_ui()
    migrate_responsive()
    migrate_haptics()
    print(
        "T3/T4/T9 application settings migration applied: adaptive portrait/landscape card density, "
        "50-150 symmetric text scale, and global haptic None short-circuit."
    )


if __name__ == "__main__":
    main()
