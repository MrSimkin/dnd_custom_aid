package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdBlock
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdLayout
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdSpan
import io.github.mrsimkin.dndcustomaid.shared.dm.SRD_LAYOUT_FILES
import io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdLayout
import io.github.mrsimkin.dndcustomaid.shared.dm.srdBlocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** The SRD with its layout, loaded once per App run (QA 0.7.0-preqa.2, A5, L1, L2). */
internal object SrdLayoutAssets {
    private var layouts: List<SrdLayout>? = null

    suspend fun layouts(platform: DmPlatform): List<SrdLayout> = layouts ?: withContext(Dispatchers.Default) {
        SRD_LAYOUT_FILES.mapNotNull { runCatching { parseSrdLayout(platform.readSrd(it)) }.getOrNull() }.also { layouts = it }
    }
}

/** Draws SRD text with headings, bold labels, bullets and real tables. */
@Composable
internal fun SrdMarkdown(md: String, modifier: Modifier = Modifier) {
    val blocks = remember(md) { srdBlocks(md) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        blocks.forEach { block ->
            when (block) {
                is SrdBlock.Heading -> Text(
                    block.text,
                    style = when (block.level) {
                        1 -> MaterialTheme.typography.headlineSmall
                        2 -> MaterialTheme.typography.titleLarge
                        3 -> MaterialTheme.typography.titleMedium
                        else -> MaterialTheme.typography.titleSmall
                    },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = if (block.level <= 2) 8.dp else 4.dp),
                )
                is SrdBlock.Paragraph -> Text(spanned(block.spans), style = MaterialTheme.typography.bodyMedium)
                is SrdBlock.Bullet -> Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("•", style = MaterialTheme.typography.bodyMedium)
                    Text(spanned(block.spans), style = MaterialTheme.typography.bodyMedium)
                }
                is SrdBlock.Table -> SrdTable(block)
            }
        }
    }
}

private fun spanned(spans: List<SrdSpan>) = buildAnnotatedString {
    spans.forEach { span ->
        if (span.bold) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(span.text) } else append(span.text)
    }
}

@Composable
private fun SrdTable(table: SrdBlock.Table) {
    val columns = maxOf(table.header?.size ?: 0, table.rows.maxOfOrNull { it.size } ?: 0)
    if (columns == 0) return
    if (columns == 2 && table.header == null) {
        SrdLabelValueTable(table)
        return
    }
    // Wider columns for longer text (the «Rasgos de clase» column), narrow ones for numbers. The cells
    // set the width; a long header («Bonificador por competencia») wraps instead of widening its column.
    fun dp(chars: Int) = (chars * 7 + 16).dp
    val natural = (0 until columns).map { c ->
        val cells = table.rows.mapNotNull { it.getOrNull(c) }
        val header = table.header?.getOrNull(c).orEmpty()
        val longestCell = cells.maxOfOrNull { it.length } ?: header.length
        val longestHeaderWord = header.split(' ').maxOfOrNull { it.length } ?: 0
        // Header words are bold, so they need a little more room per letter than the cells.
        maxOf(dp(longestCell.coerceIn(4, 46)), (longestHeaderWord * 9 + 18).dp)
    }
    val line = MaterialTheme.colorScheme.outlineVariant
    androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth()) {
    // Agent check of 0.7.0-preqa.4 (F1): when the table is wider than the pane, the wide text columns
    // shrink (their text wraps) so the whole table is seen; only a table that still does not fit scrolls.
    var widths = natural
    repeat(columns) {
        val excess = widths.fold(0.dp) { sum, w -> sum + w } - maxWidth
        if (excess <= 0.dp) return@repeat
        val widest = widths.indices.maxByOrNull { widths[it].value } ?: return@repeat
        val second = widths.filterIndexed { i, _ -> i != widest }.maxByOrNull { it.value } ?: 0.dp
        val target = maxOf(widths[widest] - excess, second, 110.dp)
        if (target >= widths[widest]) return@repeat
        widths = widths.mapIndexed { i, w -> if (i == widest) target else w }
    }
    val scrolls = widths.fold(0.dp) { sum, w -> sum + w } > maxWidth
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
    if (scrolls) {
        Text(
            "↔ Desliza la tabla para ver todas las columnas.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Column(Modifier.horizontalScroll(rememberScrollState()).border(1.dp, line, MaterialTheme.shapes.small)) {
        table.header?.let { header ->
            Row(Modifier.background(MaterialTheme.colorScheme.secondaryContainer)) {
                (0 until columns).forEach { c ->
                    Text(
                        header.getOrElse(c) { "" },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(widths[c]).padding(horizontal = 8.dp, vertical = 6.dp),
                    )
                }
            }
        }
        table.rows.forEachIndexed { index, row ->
            Row(Modifier.background(if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface)) {
                (0 until columns).forEach { c ->
                    Text(
                        row.getOrElse(c) { "" },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (table.header == null && c == 0) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.width(widths[c]).padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
    }
    }
}

/**
 * Agent check of 0.7.0-preqa.4 (F1): a «label · value» table («Atributos básicos») fills the width
 * and wraps each value, instead of fixed columns that cut the value at the edge of the pane.
 */
@Composable
private fun SrdLabelValueTable(table: SrdBlock.Table) {
    val line = MaterialTheme.colorScheme.outlineVariant
    Column(Modifier.fillMaxWidth().border(1.dp, line, MaterialTheme.shapes.small)) {
        table.rows.forEachIndexed { index, row ->
            Row(
                Modifier.fillMaxWidth()
                    .background(if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface),
            ) {
                Text(
                    row.getOrElse(0) { "" },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.4f).padding(horizontal = 8.dp, vertical = 4.dp),
                )
                Text(
                    row.getOrElse(1) { "" },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.6f).padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

/** The SRD monsters, loaded once per App run (QA 0.7.0-preqa.2, C1 and D6). */
internal object SrdMonsterAssets {
    private var monsters: List<io.github.mrsimkin.dndcustomaid.shared.dm.SrdMonster>? = null

    suspend fun monsters(platform: DmPlatform): List<io.github.mrsimkin.dndcustomaid.shared.dm.SrdMonster> = monsters ?: withContext(Dispatchers.Default) {
        runCatching {
            io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdMonsters(platform.readSrd(io.github.mrsimkin.dndcustomaid.shared.dm.SRD_MONSTERS_FILE))
        }.getOrDefault(emptyList()).also { monsters = it }
    }
}
