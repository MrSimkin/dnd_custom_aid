package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContent
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind
import io.github.mrsimkin.dndcustomaid.shared.content.RulesOrigin
import io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdCatalog
import io.github.mrsimkin.dndcustomaid.shared.dm.rulesOptions

/*
 * Redesign R5 (owner decisions D8 and D9, 2026-11-13): every «Añadir» of the PC editor can fill
 * its fields from the SRD first, then the DM's homebrew; «Custom» is writing by hand as before.
 */

/** Where the PC editor finds rules to pick from. Provided once at the top of the App. */
class RulesSources(
    val loadCatalogs: suspend () -> List<SrdCatalog>,
    val homebrew: () -> List<RulesEntryContent>,
    /** «5e» or «5.5e»: the SRD of the PC's edition is offered first. */
    val preferredEdition: () -> String? = { null },
)

val LocalRulesSources = staticCompositionLocalOf<RulesSources?> { null }

/**
 * «📖 Elegir del SRD o tu homebrew»: a button above the fields of an «Añadir» form. Picking fills
 * the form; the user can still change anything before saving (that is «Custom»).
 */
@Composable
fun RulesPickButton(
    kinds: List<RulesEntryKind>,
    onPick: (RulesOption) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "📖 Elegir del SRD o tu homebrew",
) {
    val sources = LocalRulesSources.current ?: return
    var open by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { open = true }, modifier = modifier.fillMaxWidth()) { Text(label) }
    if (open) {
        RulesPickDialog(sources = sources, kinds = kinds, onDismiss = { open = false }, onPick = { open = false; onPick(it) })
    }
}

@Composable
private fun RulesPickDialog(
    sources: RulesSources,
    kinds: List<RulesEntryKind>,
    onDismiss: () -> Unit,
    onPick: (RulesOption) -> Unit,
) {
    var catalogs by remember { mutableStateOf<List<SrdCatalog>?>(null) }
    LaunchedEffect(Unit) { catalogs = sources.loadCatalogs() }
    var kind by remember { mutableStateOf(kinds.first()) }
    var origin by remember { mutableStateOf(RulesOrigin.SRD) }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<RulesOption?>(null) }
    val homebrew = remember { sources.homebrew() }
    val options = catalogs?.let { loaded ->
        rulesOptions(kind, loaded, homebrew, sources.preferredEdition(), query).filter { it.origin == origin }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elegir ${kind.label.lowercase()}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(RulesOrigin.SRD, RulesOrigin.HOMEBREW, RulesOrigin.CUSTOM).forEach { candidate ->
                        FilterChip(
                            selected = origin == candidate,
                            onClick = { origin = candidate; selected = null },
                            label = { Text(candidate.label) },
                        )
                    }
                }
                if (kinds.size > 1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        kinds.forEach { candidate ->
                            FilterChip(selected = kind == candidate, onClick = { kind = candidate; selected = null }, label = { Text(candidate.plural) })
                        }
                    }
                }
                if (origin == RulesOrigin.CUSTOM) {
                    Text("Escribe tú los datos en el formulario: cierra esta ventana y completa los campos a mano.", style = MaterialTheme.typography.bodyMedium)
                    return@Column
                }
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                when {
                    options == null -> Text("Cargando…")
                    options.isEmpty() && origin == RulesOrigin.HOMEBREW ->
                        Text("Tu homebrew aún no tiene ${kind.plural.lowercase()}. Agrégalos en «Reglas → Homebrew».", style = MaterialTheme.typography.bodyMedium)
                    options.isEmpty() -> Text("Nada coincide con la búsqueda.", style = MaterialTheme.typography.bodyMedium)
                    else -> LazyColumn(Modifier.heightIn(max = 320.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(options) { option ->
                            val isSelected = option == selected
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth().clickable { selected = option },
                            ) {
                                Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(option.name, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        listOfNotNull(
                                            option.source,
                                            option.parent.takeIf { it.isNotBlank() },
                                            option.fields["nivel"]?.let { if (it == "0") "truco" else "nivel $it" },
                                            option.fields["dano"],
                                            option.fields["ca"]?.let { "CA $it" },
                                        ).joinToString(" · "),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                    }
                }
                selected?.let { Text(it.summary, style = MaterialTheme.typography.bodySmall, maxLines = 4) }
            }
        },
        confirmButton = {
            Button(enabled = selected != null, onClick = { selected?.let(onPick) }) { Text("Usar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
