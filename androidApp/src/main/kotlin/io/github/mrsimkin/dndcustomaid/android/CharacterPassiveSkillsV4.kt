package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInsight
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInvestigation

@Composable
internal fun CharacterPassiveSkillsCardV4(sheet: CharacterSheet) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(4.dp)),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PassiveSkillValueV4("Per. pasiva", sheet.passivePerception, Modifier.weight(1f))
            Text("|", style = MaterialTheme.typography.labelSmall)
            PassiveSkillValueV4("Persp. pasiva", sheet.passiveInsight, Modifier.weight(1f))
            Text("|", style = MaterialTheme.typography.labelSmall)
            PassiveSkillValueV4("Inv. pasiva", sheet.passiveInvestigation, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PassiveSkillValueV4(label: String, value: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$label $value", style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
