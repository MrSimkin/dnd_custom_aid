package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/*
 * Owner point 5 of the critical review: «lo de un aparato no siempre está en el otro». The table of
 * what is done where is public and the same in both Apps, so there is one answer and not two.
 * It is kept beside `docs/technical/QUE_SE_HACE_DONDE.md`.
 */

internal data class WhereRow(val task: String, val windows: String, val tablet: String, val player: String)

internal val WHERE_TO_DO_IT: List<WhereRow> = listOf(
    WhereRow("Preparar lugares, PNJ, escenas, dungeons, encuentros y criaturas", "Sí", "No", "No"),
    WhereRow("Papelera de lo preparado", "Sí", "No", "No"),
    WhereRow("Cargar un paquete de contenido", "Todo", "Solo los PJ", "No"),
    WhereRow("Ver qué cambiaría el paquete antes de cargarlo", "Sí", "No", "No"),
    WhereRow("Copia de seguridad de la campaña", "Sí", "No", "No"),
    WhereRow("Mesas en vivo: combate, dungeon y escena", "Sí", "Sí", "No"),
    WhereRow("Pantalla del DM", "Sí", "Sí", "No"),
    WhereRow("Crear un personaje paso a paso", "Sí", "Sí", "Sí"),
    WhereRow("Editar la hoja a fondo", "No", "Sí", "Sí"),
    WhereRow("Ver la hoja y guardarla en PDF", "Sí", "Sí", "Sí"),
    WhereRow("Handouts y mapas", "Preparar y enviar", "Enviar", "Recibir"),
    WhereRow("Reglas del SRD y tu homebrew", "Sí", "Sí", "Sí"),
    WhereRow("Notas de la mesa", "Ver todas", "Escribir", "No"),
    WhereRow("Jugadores e invitaciones", "Sí", "No", "Unirse"),
    WhereRow("Sincronizar", "Sí", "Sí", "Sí"),
)

/** The same table in both Apps: what is done on Windows, on the tablet and on a player's phone. */
@Composable
fun WhereToDoItCard(modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Qué se hace en cada aparato", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Esta es la regla, sin excepciones. Si algo aparece donde dice que no va, o falta donde dice que sí, avísame.",
                style = MaterialTheme.typography.bodySmall,
            )
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("", modifier = Modifier.weight(1f))
                Text("Windows", modifier = Modifier.width(96.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("Tablet", modifier = Modifier.width(96.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("Jugador", modifier = Modifier.width(80.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
            WHERE_TO_DO_IT.forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(row.task, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    Text(row.windows, modifier = Modifier.width(96.dp), style = MaterialTheme.typography.bodySmall)
                    Text(row.tablet, modifier = Modifier.width(96.dp), style = MaterialTheme.typography.bodySmall)
                    Text(row.player, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
