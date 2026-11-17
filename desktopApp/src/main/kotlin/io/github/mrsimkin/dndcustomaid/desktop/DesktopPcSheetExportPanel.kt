package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant
import java.io.File
import javax.swing.JOptionPane
// Refactor F3: the V11 sheet export panel of Personajes, moved out of DesktopPcManager.kt.

@Composable
internal fun PcSheetPdfExportPanel(
    busy: Boolean,
    v11ProofVariant: PcSheetV11Variant,
    onV11VariantChange: (PcSheetV11Variant) -> Unit,
    onSaveV11Proof: () -> Unit,
    onShareV11Proof: () -> Unit,
) {
    // QA 0.7.0-preqa.2 (J8): V11 is the only sheet, as on the tablet; the old families are gone.
    PcAuditSection("Exportar hoja de PJ (PDF)") {
        Text("Hoja V11, la misma de la tablet. Se genera en este equipo, sin conexión.", style = MaterialTheme.typography.bodyMedium)
        PcSheetExportChoiceRow(
            label = "Hoja",
            values = PcSheetV11Variant.entries,
            selected = v11ProofVariant,
            render = { if (it == PcSheetV11Variant.A) "A · Habilidades por atributo" else "B · Habilidades en orden alfabético" },
            onSelect = onV11VariantChange,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(enabled = !busy, onClick = onSaveV11Proof) { Text(if (busy) "Generando…" else "Guardar PDF…") }
            Button(enabled = !busy, onClick = onShareV11Proof) { Text(if (busy) "Generando…" else "Compartir PDF") }
        }
        Text(
            "Compartir prepara un PDF temporal y lo coloca en el portapapeles del sistema como archivo; pégalo en un destino compatible.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
internal fun <T> PcSheetExportChoiceRow(
    label: String,
    values: List<T>,
    selected: T,
    render: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            values.forEach { value ->
                if (value == selected) {
                    Button(onClick = { onSelect(value) }) { Text(render(value)) }
                } else {
                    TextButton(onClick = { onSelect(value) }) { Text(render(value)) }
                }
            }
        }
    }
}

internal fun choosePcSheetSaveTarget(suggestedName: String): File? {
    val selected = chooseFileWithExplorer(
        title = "Guardar hoja de PJ como PDF",
        purpose = "pc-sheets",
        extensions = listOf("pdf"),
        save = true,
        suggestedName = suggestedName,
    ) ?: return null
    val target = if (selected.extension.equals("pdf", ignoreCase = true)) {
        selected
    } else {
        File(selected.parentFile, selected.name + ".pdf")
    }
    if (target.exists()) {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "El archivo ya existe. ¿Deseas reemplazarlo?",
            "Reemplazar PDF",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
        )
        if (answer != JOptionPane.YES_OPTION) return null
    }
    return target
}
