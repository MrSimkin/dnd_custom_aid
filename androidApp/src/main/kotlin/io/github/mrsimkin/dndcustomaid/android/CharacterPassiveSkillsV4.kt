package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInsight
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInvestigation

@Composable
internal fun CharacterPassiveSkillsCardV4(sheet: CharacterSheet) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            PassiveSkillValueV4("Percepción", sheet.passivePerception)
            PassiveSkillValueV4("Perspicacia", sheet.passiveInsight)
            PassiveSkillValueV4("Investigación", sheet.passiveInvestigation)
        }
    }
}

@Composable
private fun PassiveSkillValueV4(label: String, value: Int) {
    androidx.compose.foundation.layout.Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value.toString(), style = MaterialTheme.typography.titleMedium)
    }
}
