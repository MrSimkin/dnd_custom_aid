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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheetTabKey

@Composable
internal fun CharacterAdaptiveShellV4(
    layoutContext: CharacterLayoutContextV4,
    navigationPresentation: CharacterNavigationPresentationV4,
    selectedTab: CharacterTabV4,
    spellcasterEnabled: Boolean,
    visibleModules: Set<CharacterModuleKind>,
    tabOrder: List<CharacterSheetTabKey> = CharacterSheetTabKey.entries,
    onSelect: (CharacterTabV4) -> Unit,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val effectiveTabOrder = LocalCharacterPcSettingsContextV4.current
        ?.successorState
        ?.preferences
        ?.tabOrder
        ?: tabOrder
    val tabletStateHolder = rememberSaveableStateHolder()

    CompositionLocalProvider(LocalCharacterLayoutContextV4 provides layoutContext) {
        Column(modifier = Modifier.fillMaxSize()) {
            val combinePhoneLandscapeHeaderAndTabs =
                layoutContext.formFactor == CharacterFormFactorV4.PHONE_LANDSCAPE &&
                    navigationPresentation == CharacterNavigationPresentationV4.TOP_TABS &&
                    layoutContext.verticalSpace != CharacterVerticalSpaceV4.COMFORTABLE

            // Keep the identity/save controls persistent, but in shallow phone landscape use width
            // instead of spending a second full row of scarce vertical space.
            if (!combinePhoneLandscapeHeaderAndTabs) {
                header()
            }

            when (layoutContext.formFactor) {
            CharacterFormFactorV4.PHONE_PORTRAIT,
            CharacterFormFactorV4.PHONE_LANDSCAPE,
            -> when (navigationPresentation) {
                CharacterNavigationPresentationV4.TOP_TABS -> {
                    if (combinePhoneLandscapeHeaderAndTabs) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(modifier = Modifier.widthIn(max = 360.dp)) {
                                header()
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CharacterTopTabStripV4(
                                    selectedTab = selectedTab,
                                    spellcasterEnabled = spellcasterEnabled,
                                    visibleModules = visibleModules,
                                    tabOrder = effectiveTabOrder,
                                    onSelect = onSelect,
                                )
                            }
                        }
                    } else {
                        CharacterTopTabStripV4(
                            selectedTab = selectedTab,
                            spellcasterEnabled = spellcasterEnabled,
                            visibleModules = visibleModules,
                            tabOrder = effectiveTabOrder,
                            onSelect = onSelect,
                        )
                    }
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
                            tabOrder = effectiveTabOrder,
                            onSelect = onSelect,
                            railWidth = 112.dp,
                        )
                        Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
                            content()
                        }
                    }
                }
            }

            CharacterFormFactorV4.TABLET_PORTRAIT -> {
                CharacterTopTabStripV4(
                    selectedTab = selectedTab,
                    spellcasterEnabled = spellcasterEnabled,
                    visibleModules = visibleModules,
                    tabOrder = effectiveTabOrder,
                    onSelect = onSelect,
                )
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 1040.dp)
                            .fillMaxSize()
                            .padding(horizontal = appSpacingV4(8.dp)),
                    ) {
                        tabletStateHolder.SaveableStateProvider("tablet-tab-${selectedTab.name}") {
                            content()
                        }
                    }
                }
            }

            CharacterFormFactorV4.TABLET_LANDSCAPE -> {
                val fontScale = LocalDensity.current.fontScale
                val railWidth = when {
                    fontScale >= 1.60f -> 148.dp
                    fontScale >= 1.30f -> 132.dp
                    else -> 116.dp
                }
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    CharacterNavigationRailV4(
                        selectedTab = selectedTab,
                        spellcasterEnabled = spellcasterEnabled,
                        visibleModules = visibleModules,
                        tabOrder = effectiveTabOrder,
                        onSelect = onSelect,
                        railWidth = railWidth,
                    )
                    Box(
                        modifier = Modifier.fillMaxHeight().weight(1f),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 1480.dp)
                                .fillMaxSize()
                                .padding(horizontal = appSpacingV4(10.dp)),
                        ) {
                            tabletStateHolder.SaveableStateProvider("tablet-tab-${selectedTab.name}") {
                                content()
                            }
                        }
                    }
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
    tabOrder: List<CharacterSheetTabKey>,
    onSelect: (CharacterTabV4) -> Unit,
    railWidth: Dp,
) {
    val tabs = visibleCharacterTabsV4(spellcasterEnabled, visibleModules, tabOrder)

    NavigationRail(
        modifier = Modifier.fillMaxHeight().width(railWidth),
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
                            maxLines = 2,
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
    CharacterTabV4.DICE -> "TD"
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
