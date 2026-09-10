from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt")
text = path.read_text()

def replace_once(old: str, new: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"expected one match, found {count}: {old[:120]!r}")
    text = text.replace(old, new, 1)

replace_once(
    "import androidx.compose.material3.OutlinedButton\nimport androidx.compose.material3.Surface",
    "import androidx.compose.material3.OutlinedButton\nimport androidx.compose.material3.OutlinedTextField\nimport androidx.compose.material3.Surface",
)
replace_once(
    '''internal enum class AppThemeChoice(val label: String) {''',
    '''private val HIDDEN_FONT_CHOICES_V4 = setOf(
    AppFontChoice.INTER,
    AppFontChoice.FIGTREE,
    AppFontChoice.PUBLIC_SANS,
    AppFontChoice.CABIN_CONDENSED,
    AppFontChoice.ENCODE_SANS_CONDENSED,
)

private val SELECTABLE_FONT_CHOICES_V4 = AppFontChoice.entries.filterNot { it in HIDDEN_FONT_CHOICES_V4 }

internal enum class AppThemeChoice(val label: String) {''',
)
replace_once(
    '''        val font = when (val stored = preferences.getString(KEY_FONT, null)) {
            "IBM_PLEX_SANS_CONDENSED", "BARLOW_CONDENSED" -> AppFontChoice.ROBOTO_CONDENSED
            "LEXEND" -> AppFontChoice.SORA
            "OSWALD" -> AppFontChoice.MANROPE
            else -> stored
                ?.let { runCatching { AppFontChoice.valueOf(it) }.getOrNull() }
                ?: AppFontChoice.MANROPE
        }''',
    '''        val resolvedFont = when (val stored = preferences.getString(KEY_FONT, null)) {
            "IBM_PLEX_SANS_CONDENSED", "BARLOW_CONDENSED" -> AppFontChoice.ROBOTO_CONDENSED
            "LEXEND" -> AppFontChoice.SORA
            "OSWALD" -> AppFontChoice.MANROPE
            else -> stored
                ?.let { runCatching { AppFontChoice.valueOf(it) }.getOrNull() }
                ?: AppFontChoice.MANROPE
        }
        val font = resolvedFont.takeUnless { it in HIDDEN_FONT_CHOICES_V4 } ?: AppFontChoice.MANROPE''',
)
replace_once(
    '''    AppThemeChoice.AMBER -> darkColorScheme(
        primary = Color(0xFFFFCC80),
        onPrimary = Color(0xFF452B00),
        primaryContainer = Color(0xFF624000),
        onPrimaryContainer = Color(0xFFFFDDB0),
        secondary = Color(0xFFD9C3A4),
        secondaryContainer = Color(0xFF504532),
        background = Color(0xFF181108),
        onBackground = Color(0xFFF0E0CA),
        surface = Color(0xFF22180D),
        onSurface = Color(0xFFF0E0CA),
        surfaceVariant = Color(0xFF4A4032),
        onSurfaceVariant = Color(0xFFD2C4B1),
        outline = Color(0xFF9C8F7B),
    )''',
    '''    AppThemeChoice.AMBER -> lightColorScheme(
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
    )''',
)
replace_once(
    '''    AppThemeChoice.SLATE -> darkColorScheme(
        primary = Color(0xFFA9C7E5),
        onPrimary = Color(0xFF123149),
        primaryContainer = Color(0xFF294961),
        onPrimaryContainer = Color(0xFFCDE5FF),
        secondary = Color(0xFFBAC8D5),
        secondaryContainer = Color(0xFF354550),
        background = Color(0xFF10171E),
        onBackground = Color(0xFFDCE3EA),
        surface = Color(0xFF17212A),
        onSurface = Color(0xFFDCE3EA),
        surfaceVariant = Color(0xFF3F4851),
        onSurfaceVariant = Color(0xFFC1C7CE),
        outline = Color(0xFF8B9299),
    )''',
    '''    AppThemeChoice.SLATE -> darkColorScheme(
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
    )''',
)
replace_once(
    '''    AppThemeChoice.TERRACOTTA -> lightColorScheme(
        primary = Color(0xFF8A4633),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDBD1),
        onPrimaryContainer = Color(0xFF351000),
        secondary = Color(0xFF77574D),
        secondaryContainer = Color(0xFFFFDBD1),
        background = Color(0xFFFFF8F5),
        onBackground = Color(0xFF241914),
        surface = Color(0xFFFFF8F5),
        onSurface = Color(0xFF241914),
        surfaceVariant = Color(0xFFF5DED7),
        onSurfaceVariant = Color(0xFF53433E),
        outline = Color(0xFF85736D),
    )''',
    '''    AppThemeChoice.TERRACOTTA -> lightColorScheme(
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
    )''',
)
replace_once(
    '''                Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
                    Text("Bola de fuego · Nivel 3", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Concentración · V/S/M",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    "${preferences.themeChoice.label}''',
    '''                Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
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
                    "${preferences.themeChoice.label}''',
)
replace_once(
    '''        AppFontChoice.entries.forEach { choice ->''',
    '''        SELECTABLE_FONT_CHOICES_V4.forEach { choice ->''',
)
marker = '''@Composable
private fun ThemePreviewCard(
    choice: AppThemeChoice,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {'''
start = text.find(marker)
if start < 0:
    raise SystemExit("ThemePreviewCard marker missing")
next_marker = "\n@Composable\n"
end = text.find(next_marker, start + len(marker))
if end < 0:
    raise SystemExit("ThemePreviewCard end marker missing")
new_theme_card = r'''@Composable
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
'''
text = text[:start] + new_theme_card + text[end:]
path.write_text(text)
