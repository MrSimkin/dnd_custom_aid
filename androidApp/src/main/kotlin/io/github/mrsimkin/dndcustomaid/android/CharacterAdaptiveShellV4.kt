package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind

@Composable
internal fun CharacterAdaptiveShellV4(
    navigationPresentation: CharacterNavigationPresentationV4,
    selectedTab: CharacterTabV4,
    spellcasterEnabled: Boolean,
    visibleModules: Set<CharacterModuleKind>,
    onSelect: (CharacterTabV4) -> Unit,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // D01: the compact identity/save header remains outside all scrolling tab content.
        header()

        when (navigationPresentation) {
            CharacterNavigationPresentationV4.TOP_TABS -> {
                CharacterTopTabStripV4(
                    selectedTab = selectedTab,
                    spellcasterEnabled = spellcasterEnabled,
                    visibleModules = visibleModules,
                    onSelect = onSelect,
                )
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    content()
                }
            }

            CharacterNavigationPresentationV4.SIDE_RAIL -> {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    CharacterNavigationRailV4(
                        selectedTab = selectedTab,
                        spellcasterEnabled = spellcasterEnabled,
                        visibleModules = visibleModules,
                        onSelect = onSelect,
                    )
                    Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterNavigationRailV4(
    selectedTab: CharacterTabV4,
    spellcasterEnabled: Boolean,
    visibleModules: Set<CharacterModuleKind>,
    onSelect: (CharacterTabV4) -> Unit,
) {
    val tabs = visibleCharacterTabsV4(spellcasterEnabled, visibleModules)

    NavigationRail(
        modifier = Modifier.fillMaxHeight().width(164.dp),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(tabs, key = CharacterTabV4::name) { tab ->
                NavigationRailItem(
                    selected = tab == selectedTab,
                    onClick = { onSelect(tab) },
                    icon = {
                        Text(
                            text = characterTabRailMarkV4(tab),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    alwaysShowLabel = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                )
            }
        }
    }
}

private fun characterTabRailMarkV4(tab: CharacterTabV4): String = when (tab) {
    CharacterTabV4.OVERVIEW -> "GE"
    CharacterTabV4.SKILLS -> "HA"
    CharacterTabV4.COMBAT -> "CO"
    CharacterTabV4.MANAGEMENT -> "GT"
    CharacterTabV4.EQUIPMENT -> "EQ"
    CharacterTabV4.BACKGROUND -> "TR"
    CharacterTabV4.TRAITS -> "RA"
    CharacterTabV4.SPELLS -> "CJ"
    CharacterTabV4.ARTIFICER -> "AR"
    CharacterTabV4.FORMS -> "FO"
    CharacterTabV4.TECHNIQUES -> "TÉ"
    CharacterTabV4.METAMAGIC -> "ME"
    CharacterTabV4.PACTS -> "PA"
    CharacterTabV4.COMPANIONS -> "CP"
    CharacterTabV4.NOTES -> "NO"
}
