from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, content: str) -> None:
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(content, encoding="utf-8")


def replace_once(path: str, old: str, new: str) -> None:
    text = read(path)
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{path}: expected exactly one match, found {count}: {old[:140]!r}")
    write(path, text.replace(old, new, 1))


def replace_regex(path: str, pattern: str, replacement: str, count: int = 1) -> None:
    text = read(path)
    updated, matches = re.subn(pattern, replacement, text, count=count, flags=re.S)
    if matches != count:
        raise RuntimeError(f"{path}: expected {count} regex matches, found {matches}: {pattern[:140]!r}")
    write(path, updated)


# ---------------------------------------------------------------------------
# Version/build identity: this pass becomes the first explicitly named review build.
# ---------------------------------------------------------------------------
replace_once(
    "androidApp/build.gradle.kts",
    '        versionCode = 1\n        versionName = "0.1.0"',
    '        versionCode = 40200\n        versionName = "0.4.0-preqa.2"',
)
replace_once(
    "androidApp/build.gradle.kts",
    "    buildFeatures {\n        compose = true\n    }",
    "    buildFeatures {\n        compose = true\n        buildConfig = true\n    }",
)

# Give both the GitHub artifact and the APK payload an unmistakable identity.
workflow_path = ".github/workflows/scaffold-check.yml"
replace_once(
    workflow_path,
    """      - name: Upload Android debug APK\n        uses: actions/upload-artifact@v7\n        with:\n          name: dnd-custom-aid-debug-apk\n          path: androidApp/build/outputs/apk/debug/androidApp-debug.apk\n          if-no-files-found: error\n""",
    """      - name: Name Android review APK\n        run: cp androidApp/build/outputs/apk/debug/androidApp-debug.apk androidApp/build/outputs/apk/debug/DND_Custom_Aid_0.4.0-preqa.2_Build_40200_debug.apk\n      - name: Upload Android debug APK\n        uses: actions/upload-artifact@v7\n        with:\n          name: DND-Custom-Aid-0.4.0-preqa.2-build-40200-debug\n          path: androidApp/build/outputs/apk/debug/DND_Custom_Aid_0.4.0-preqa.2_Build_40200_debug.apk\n          if-no-files-found: error\n""",
)

# ---------------------------------------------------------------------------
# Proper-name input normalization, shared and tested.
# ---------------------------------------------------------------------------
write(
    "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterNameFormatting.kt",
    '''package io.github.mrsimkin.dndcustomaid.shared.character\n\n/**\n * Keeps the first visible character of a character name uppercase while preserving\n * the rest of the user's text exactly as entered. Leading whitespace is tolerated\n * during editing instead of being destructively trimmed.\n */\nfun characterProperNameInput(value: String): String {\n    val firstVisibleIndex = value.indexOfFirst { !it.isWhitespace() }\n    if (firstVisibleIndex < 0) return value\n    val current = value[firstVisibleIndex]\n    val upper = current.uppercaseChar()\n    if (current == upper) return value\n    return buildString(value.length) {\n        append(value, 0, firstVisibleIndex)\n        append(upper)\n        append(value, firstVisibleIndex + 1, value.length)\n    }\n}\n''',
)
write(
    "shared/src/commonTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterNameFormattingTest.kt",
    '''package io.github.mrsimkin.dndcustomaid.shared.character\n\nimport kotlin.test.Test\nimport kotlin.test.assertEquals\n\nclass CharacterNameFormattingTest {\n    @Test\n    fun firstVisibleCharacterIsUppercase() {\n        assertEquals("Gustavo", characterProperNameInput("gustavo"))\n        assertEquals("  Élara", characterProperNameInput("  élara"))\n    }\n\n    @Test\n    fun laterWordsAreNotRewritten() {\n        assertEquals("Juan de la cruz", characterProperNameInput("juan de la cruz"))\n        assertEquals("Árbol viejo", characterProperNameInput("Árbol viejo"))\n    }\n\n    @Test\n    fun blankEditingStateIsPreserved() {\n        assertEquals("", characterProperNameInput(""))\n        assertEquals("   ", characterProperNameInput("   "))\n    }\n}\n''',
)
editor_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"
replace_once(
    editor_path,
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus\nimport io.github.mrsimkin.dndcustomaid.shared.character.characterProperNameInput\n",
)
replace_once(
    editor_path,
    "onValueChange = { onDraftChange(draft.copy(name = it)) },",
    "onValueChange = { onDraftChange(draft.copy(name = characterProperNameInput(it))) },",
)

# ---------------------------------------------------------------------------
# App Settings: wider text-scale audition, 10 new fonts, explicit spacing setting,
# larger opt-in column ranges and an unambiguous About build surface.
# ---------------------------------------------------------------------------
ui_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt"
replace_once(
    ui_path,
    "import androidx.compose.foundation.layout.heightIn\n",
    "import androidx.compose.foundation.layout.heightIn\nimport androidx.compose.foundation.layout.imePadding\nimport androidx.compose.foundation.layout.navigationBarsPadding\n",
)
replace_once(
    ui_path,
    "import androidx.compose.ui.platform.LocalDensity\n",
    "import androidx.compose.ui.platform.LocalConfiguration\nimport androidx.compose.ui.platform.LocalDensity\n",
)
replace_once(
    ui_path,
    "import androidx.compose.ui.unit.Density\n",
    "import androidx.compose.ui.unit.Density\nimport androidx.compose.ui.unit.Dp\n",
)
replace_once(
    ui_path,
    '''    MONA_SANS_CONDENSED("Mona Sans Condensed", sourceLabel = "GitHub / Degarism"),\n    GEIST("Geist", sourceLabel = "Vercel"),\n''',
    '''    MONA_SANS_CONDENSED("Mona Sans Condensed", sourceLabel = "GitHub / Degarism"),\n    GEIST("Geist", sourceLabel = "Vercel"),\n    INTER("Inter", "Inter", "Rasmus Andersson"),\n    FIGTREE("Figtree", "Figtree", "Erik Kennedy"),\n    PUBLIC_SANS("Public Sans", "Public Sans", "U.S. Web Design System"),\n    BARLOW_SEMI_CONDENSED("Barlow Semi Condensed", "Barlow Semi Condensed", "Jeremy Tribby"),\n    SPACE_GROTESK("Space Grotesk", "Space Grotesk", "Florian Karsten"),\n    RECURSIVE("Recursive", "Recursive", "Arrow Type"),\n    CABIN_CONDENSED("Cabin Condensed", "Cabin Condensed", "Impallari Type"),\n    ENCODE_SANS_CONDENSED("Encode Sans Condensed", "Encode Sans Condensed", "Impallari Type"),\n    PT_SANS_NARROW("PT Sans Narrow", "PT Sans Narrow", "ParaType"),\n    LEAGUE_SPARTAN("League Spartan", "League Spartan", "The League of Moveable Type"),\n''',
)
replace_once(
    ui_path,
    "    val tabletLandscapeColumns: Int = 3,\n)",
    "    val tabletLandscapeColumns: Int = 3,\n    val spacingScalePercent: Int = 100,\n)",
)
replace_once(
    ui_path,
    """        val phonePortraitColumns = preferences.getInt(KEY_PHONE_PORTRAIT_COLUMNS, 1).coerceIn(1, 3)\n        val phoneLandscapeColumns = preferences.getInt(KEY_PHONE_LANDSCAPE_COLUMNS, 2).coerceIn(1, 4)\n        val tabletPortraitColumns = preferences.getInt(KEY_TABLET_PORTRAIT_COLUMNS, 2).coerceIn(1, 4)\n        val tabletLandscapeColumns = preferences.getInt(KEY_TABLET_LANDSCAPE_COLUMNS, 3).coerceIn(1, 4)\n""",
    """        val phonePortraitColumns = preferences.getInt(KEY_PHONE_PORTRAIT_COLUMNS, 1).coerceIn(1, 4)\n        val phoneLandscapeColumns = preferences.getInt(KEY_PHONE_LANDSCAPE_COLUMNS, 2).coerceIn(1, 5)\n        val tabletPortraitColumns = preferences.getInt(KEY_TABLET_PORTRAIT_COLUMNS, 2).coerceIn(1, 5)\n        val tabletLandscapeColumns = preferences.getInt(KEY_TABLET_LANDSCAPE_COLUMNS, 3).coerceIn(1, 6)\n        val spacingScalePercent = preferences.getInt(KEY_SPACING_SCALE, 100)\n            .takeIf { it in SPACING_SCALE_OPTIONS } ?: 100\n""",
)
replace_once(
    ui_path,
    """            tabletPortraitColumns = tabletPortraitColumns,\n            tabletLandscapeColumns = tabletLandscapeColumns,\n        )\n""",
    """            tabletPortraitColumns = tabletPortraitColumns,\n            tabletLandscapeColumns = tabletLandscapeColumns,\n            spacingScalePercent = spacingScalePercent,\n        )\n""",
)
replace_once(
    ui_path,
    """            .putInt(KEY_TABLET_PORTRAIT_COLUMNS, value.tabletPortraitColumns)\n            .putInt(KEY_TABLET_LANDSCAPE_COLUMNS, value.tabletLandscapeColumns)\n            .apply()\n""",
    """            .putInt(KEY_TABLET_PORTRAIT_COLUMNS, value.tabletPortraitColumns)\n            .putInt(KEY_TABLET_LANDSCAPE_COLUMNS, value.tabletLandscapeColumns)\n            .putInt(KEY_SPACING_SCALE, value.spacingScalePercent)\n            .apply()\n""",
)
replace_once(
    ui_path,
    "        const val KEY_TABLET_LANDSCAPE_COLUMNS = \"tablet_landscape_columns\"\n",
    "        const val KEY_TABLET_LANDSCAPE_COLUMNS = \"tablet_landscape_columns\"\n        const val KEY_SPACING_SCALE = \"spacing_scale_percent\"\n",
)
replace_once(
    ui_path,
    "internal val FONT_SCALE_OPTIONS = listOf(80, 90, 100, 115, 130)\n",
    """internal val FONT_SCALE_OPTIONS = listOf(70, 80, 90, 100, 110, 120, 130, 145, 160, 180, 200)\ninternal val SPACING_SCALE_OPTIONS = listOf(100, 90, 80, 70, 60)\n\n@Composable\ninternal fun appSpacingV4(value: Dp): Dp =\n    value * (LocalUiPreferencesV4.current.spacingScalePercent / 100f)\n""",
)

replace_regex(
    ui_path,
    r'@Composable\ninternal fun AppSettingsDialog\(.*?\n\}\n\n\n@Composable\nprivate fun LayoutColumnSettingsV4',
    r'''@Composable
internal fun AppSettingsDialog(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    var showAbout by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val phoneLike = minOf(configuration.screenWidthDp, configuration.screenHeightDp) < 600
    val veryLargePhoneText = phoneLike && preferences.fontScalePercent >= 145

    AlertDialog(
        modifier = Modifier.imePadding().navigationBarsPadding(),
        onDismissRequest = onDismiss,
        title = { Text("Ajustes") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp),
                contentPadding = PaddingValues(bottom = appSpacingV4(12.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(12.dp)),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Tamaño de texto",
                            value = "${preferences.fontScalePercent}%",
                            options = FONT_SCALE_OPTIONS,
                            optionLabel = { "$it%" },
                            onSelect = { onPreferencesChange(preferences.copy(fontScalePercent = it)) },
                        )
                        if (veryLargePhoneText) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Text(
                                    "Advertencia para teléfono: ${preferences.fontScalePercent}% reduce mucho el área útil. La app conservará el valor y recurrirá a scroll cuando sea necesario.",
                                    modifier = Modifier.padding(appSpacingV4(7.dp)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                )
                            }
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Compactación adicional de espacios",
                            value = "${preferences.spacingScalePercent}%",
                            options = SPACING_SCALE_OPTIONS,
                            optionLabel = { "$it%" },
                            onSelect = { onPreferencesChange(preferences.copy(spacingScalePercent = it)) },
                        )
                        Text(
                            "No reemplaza la vista Supercompacta. Reduce todavía más los márgenes, paddings y separaciones que controla la app; iconos y touch targets conservan su tamaño.",
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
                item {
                    Text(
                        "La audición tipográfica mezcla candidatos de distintos orígenes; el distribuidor no decide la selección final.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp))) {
                TextButton(onClick = { showAbout = true }) { Text("Acerca de") }
                Button(onClick = onDismiss) { Text("Listo") }
            }
        },
    )

    if (showAbout) {
        AboutBuildDialogV4(onDismiss = { showAbout = false })
    }
}

@Composable
private fun AboutBuildDialogV4(onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.imePadding().navigationBarsPadding(),
        onDismissRequest = onDismiss,
        title = { Text("Acerca de D&D Custom Aid") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp))) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(
                        modifier = Modifier.padding(appSpacingV4(12.dp)),
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
private fun LayoutColumnSettingsV4''',
)

replace_regex(
    ui_path,
    r'@Composable\nprivate fun LayoutColumnSettingsV4\(.*?\n\}\n\n@Composable\nprivate fun SettingsSheetPreview',
    r'''@Composable
private fun LayoutColumnSettingsV4(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Columnas de tarjetas · opt-in", style = MaterialTheme.typography.labelLarge)
        Text(
            "Son máximos elegidos por el usuario. Puedes pedir más columnas que las predeterminadas en teléfono o tablet; cada superficie conserva sus propios límites de legibilidad cuando corresponda.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SettingSelector(
            label = "Teléfono · vertical",
            value = preferences.phonePortraitColumns.toString(),
            options = (1..4).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(phonePortraitColumns = it)) },
        )
        SettingSelector(
            label = "Teléfono · horizontal",
            value = preferences.phoneLandscapeColumns.toString(),
            options = (1..5).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(phoneLandscapeColumns = it)) },
        )
        SettingSelector(
            label = "Tablet · vertical",
            value = preferences.tabletPortraitColumns.toString(),
            options = (1..5).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(tabletPortraitColumns = it)) },
        )
        SettingSelector(
            label = "Tablet · horizontal",
            value = preferences.tabletLandscapeColumns.toString(),
            options = (1..6).toList(),
            optionLabel = Int::toString,
            onSelect = { onPreferencesChange(preferences.copy(tabletLandscapeColumns = it)) },
        )
    }
}

@Composable
private fun SettingsSheetPreview''',
)
replace_once(
    ui_path,
    '"${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}%",',
    '"${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}%",',
)

# Apply the new spacing preference to the most reused app-owned primitives in pass 2.
for path in [
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCollectionPrimitivesV4.kt",
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterInteractionPrimitivesV4.kt",
]:
    text = read(path)
    # Deliberately target padding/gaps only; do not shrink size/height/width/touch targets.
    text = re.sub(r'Arrangement\.spacedBy\((\d+)\.dp\)', r'Arrangement.spacedBy(appSpacingV4(\1.dp))', text)
    text = re.sub(r'Modifier\.padding\((\d+)\.dp\)', r'Modifier.padding(appSpacingV4(\1.dp))', text)
    text = re.sub(
        r'padding\(horizontal = (\d+)\.dp, vertical = (\d+)\.dp\)',
        r'padding(horizontal = appSpacingV4(\1.dp), vertical = appSpacingV4(\2.dp))',
        text,
    )
    write(path, text)

# Main character General outer list and identity/class blocks get the spacing control now;
# subsequent passes will propagate it through every remaining tab-specific interior.
text = read(editor_path)
text = text.replace(
    "verticalArrangement = Arrangement.spacedBy(5.dp),",
    "verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),",
)
text = text.replace(
    "modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp),",
    "modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(4.dp), vertical = appSpacingV4(3.dp)),",
)
write(editor_path, text)

# ---------------------------------------------------------------------------
# IME policy: activity already uses adjustResize; make global settings and the
# campaign-name dialog explicitly inset-aware as well. Character editor dialogs
# already use CharacterImeSafeEditorDialog with imePadding + scroll.
# ---------------------------------------------------------------------------
main_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt"
replace_once(
    main_path,
    "import androidx.compose.foundation.layout.fillMaxWidth\n",
    "import androidx.compose.foundation.layout.fillMaxWidth\nimport androidx.compose.foundation.layout.imePadding\nimport androidx.compose.foundation.layout.navigationBarsPadding\n",
)
replace_once(
    main_path,
    """    AlertDialog(\n        onDismissRequest = onDismiss,\n        title = { Text(\"Nueva campaña\") },\n""",
    """    AlertDialog(\n        modifier = Modifier.imePadding().navigationBarsPadding(),\n        onDismissRequest = onDismiss,\n        title = { Text(\"Nueva campaña\") },\n""",
)

# ---------------------------------------------------------------------------
# Durable operating rule: every pass updates a stable LATEST checkpoint pointer.
# ---------------------------------------------------------------------------
agents_path = "AGENTS.md"
replace_once(
    agents_path,
    """A checkpoint may be a normal implementation/documentation commit or a focused checkpoint file. It must say what was completed, what remains, verification status and the exact next action.\n\n## 11. Technical quality, credentials and signing material\n""",
    """A checkpoint may be a normal implementation/documentation commit or a focused checkpoint file. It must say what was completed, what remains, verification status and the exact next action.\n\n### Stable latest-checkpoint rule\n\nThe owner requires a checkpoint update after **every implementation/review pass**, even when several passes happen inside one chat. `docs/checkpoints/LATEST.md` is the stable resume pointer and must be refreshed before a pass is declared complete. A new chat receiving only “retoma proyecto / revisa checkpoint” must be able to read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, and recover the active branch, exact tested build identity, completed work, unresolved work and next action without depending on chat memory.\n\nDo not leave `LATEST.md` pointing at a historical frozen-QA state after the owner explicitly reopens implementation.\n\n## 11. Technical quality, credentials and signing material\n""",
)

print("Pre-QA UX repair pass 2 transformations applied.")
