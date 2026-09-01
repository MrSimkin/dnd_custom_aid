package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility

internal fun CharacterSheet.hasMeaningfulSpellcastingDataV4(): Boolean =
    spellSaveDc != null ||
        spellAttackModifier != null ||
        spellcastingAbility != SpellcastingAbility.NONE ||
        spellSlots.any { it.totalSlots > 0 } ||
        spellcastingSources.isNotEmpty() ||
        spells.isNotEmpty()

@Composable
internal fun CharacterPcSettingsV4(
    characterName: String,
    spellcasterEnabled: Boolean,
    onBack: () -> Unit,
    onSpellcasterEnabledChange: (Boolean) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                StableBackIconButton(
                    onClick = onBack,
                    contentDescription = "Volver a la ficha",
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ajustes de personaje",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = characterName.ifBlank { "Ficha de personaje" },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "Lanzador de conjuros",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = if (spellcasterEnabled) {
                                "Muestra Quick Magic y la pestaña Conjuros."
                            } else {
                                "Oculta Quick Magic y la pestaña Conjuros sin borrar sus datos."
                            },
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Switch(
                        checked = spellcasterEnabled,
                        onCheckedChange = onSpellcasterEnabledChange,
                    )
                }
            }
        }
    }
}
