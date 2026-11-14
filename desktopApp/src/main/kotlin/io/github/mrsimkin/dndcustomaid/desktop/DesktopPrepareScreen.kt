package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.dmui.handCursor
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign

/**
 * What a DM prepares before a session (redesign R2, owner observations P3 and P6). The order is
 * the order of preparing a session: where it happens, who is there, what happens, the dungeons,
 * the fights, then the house rules and reusable packs.
 */
internal enum class PrepareSection(val title: String, val purpose: String) {
    PLACES("Lugares y tiendas", "Distritos, edificios y tiendas, con quién atiende y lo que venden."),
    NPCS("PNJ", "Quién es, dónde está, a qué grupo pertenece y en qué escenas aparece."),
    SCENES("Aventuras y escenas", "Las escenas de cada aventura en orden, con dónde ocurren y quién está."),
    DUNGEONS("Dungeons", "Zonas unidas por pasajes, con lo que hay en cada una."),
    ENCOUNTERS("Encuentros", "Combates listos para la Mesa de Combate."),
    CREATURES("Criaturas", "Monstruos y criaturas propias para tus encuentros."),
    HOUSE_RULES("Reglas de la casa", "Tus reglas y aclaraciones para esta mesa."),
    NOTES("Notas", "Todas las notas de la campaña, agrupadas por dónde las escribiste en la mesa."),
    PACKAGES("Paquetes", "Conjuntos para reutilizar contenido entre campañas."),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DesktopPrepareScreen(
    creatureController: DesktopCreatureManagerController,
    npcController: DesktopNpcManagerController,
    homebrewRuleController: DesktopHomebrewRuleManagerController,
    placeController: DesktopPlaceManagerController,
    dungeonController: DesktopDungeonEditorController,
    database: io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var open by remember { mutableStateOf<PrepareSection?>(null) }
    // «Abrir ficha completa» from the dungeon plan opens that zone's full sheet.
    var zoneSheetId by remember { mutableStateOf<kotlin.uuid.Uuid?>(null) }
    val section = open

    if (section == null) {
        Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp))) {
            WorkbenchHeading(title = "Preparar", subtitle = DesktopDestination.PREPARE.purpose)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp)),
                verticalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp)),
            ) {
                PrepareSection.entries.forEach { candidate ->
                    Card(
                        elevation = 2.dp,
                        modifier = Modifier
                            .width(260.dp)
                            .height(118.dp)
                            .handCursor()
                            .clickable {
                                open = candidate
                                onQaEvent("Preparar: ${candidate.title}")
                            },
                    ) {
                        Column(Modifier.padding(desktopSpacing(16.dp)), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(candidate.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                            Text(candidate.purpose, style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }
            Text(
                "Lo que preparas aquí llega a la tablet con «⟳ Sincronizar», arriba.",
                style = MaterialTheme.typography.caption,
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp))) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp))) {
            TextButton(
                onClick = { if (zoneSheetId != null) zoneSheetId = null else open = null },
                modifier = Modifier.handCursor(),
            ) { Text(if (zoneSheetId != null) "← Plano del dungeon" else "← Preparar") }
            Column {
                Text(section.title, style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                Text(section.purpose, style = MaterialTheme.typography.body2)
            }
        }
        Divider()
        when (section) {
            PrepareSection.PLACES -> DesktopPlaceManagerScreen(placeController, activeCampaign, onQaEvent)
            PrepareSection.NPCS -> NpcManagerScreen(npcController, activeCampaign, onQaEvent)
            PrepareSection.SCENES -> DesktopSceneManagerScreen(placeController.sceneManagerController, activeCampaign, onQaEvent)
            PrepareSection.DUNGEONS -> if (zoneSheetId != null) {
                DesktopZoneManagerScreen(placeController.zoneManagerController, activeCampaign, onQaEvent, initialSelectedId = zoneSheetId)
            } else {
                DesktopDungeonEditorScreen(dungeonController, activeCampaign, onOpenZone = { zoneSheetId = it }, onQaEvent = onQaEvent)
            }
            PrepareSection.ENCOUNTERS -> DesktopEncounterManagerScreen(placeController.encounterManagerController, activeCampaign, onQaEvent)
            PrepareSection.CREATURES -> CreatureManagerScreen(creatureController, activeCampaign, onQaEvent)
            PrepareSection.HOUSE_RULES -> HomebrewRuleManagerScreen(homebrewRuleController, activeCampaign, onQaEvent)
            PrepareSection.NOTES -> DesktopNotesScreen(database, activeCampaign)
            PrepareSection.PACKAGES -> DesktopPackageManagerScreen(placeController.packageManagerController, activeCampaign, onQaEvent)
        }
    }
}
