package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal enum class CharacterTabV4(val label: String) {
    OVERVIEW("General"),
    SKILLS("Habilidades"),
    COMBAT("Combate"),
    MANAGEMENT("Gestión"),
    EQUIPMENT("Equipo"),
    BACKGROUND("Trasfondo"),
    TRAITS("Rasgos"),
    SPELLS("Conjuros"),
    NOTES("Notas"),
}

internal fun visibleCharacterTabsV4(spellcasterEnabled: Boolean): List<CharacterTabV4> =
    CharacterTabV4.entries.filter { tab -> spellcasterEnabled || tab != CharacterTabV4.SPELLS }

internal fun resolvedCharacterTabV4(
    savedTabName: String,
    spellcasterEnabled: Boolean,
): CharacterTabV4 {
    val candidate = runCatching { CharacterTabV4.valueOf(savedTabName) }
        .getOrDefault(CharacterTabV4.OVERVIEW)
    return candidate.takeIf { it in visibleCharacterTabsV4(spellcasterEnabled) }
        ?: CharacterTabV4.OVERVIEW
}

@Composable
internal fun CharacterTopTabStripV4(
    selectedTab: CharacterTabV4,
    spellcasterEnabled: Boolean,
    onSelect: (CharacterTabV4) -> Unit,
) {
    val tabs = visibleCharacterTabsV4(spellcasterEnabled)
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 0.dp,
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onSelect(tab) },
                text = {
                    Text(
                        text = tab.label,
                        maxLines = 1,
                        softWrap = false,
                    )
                },
            )
        }
    }
}
