package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResolvedD20Roll
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRulesPassage
import kotlin.math.abs

/*
 * Base para DM · the desks shared by the Android tablet and the Windows app.
 *
 * The sources in dmUi/src/kotlin are compiled into both apps, each with its own Compose version, so
 * the tablet keeps the Compose of the reviewed PC App. Everything that differs between them comes in
 * through [DmPlatform]: where small per-device settings live, how the bundled SRD is read, how images
 * are decoded, how dice results look (the PC sheet's own cards on Android) and how the App syncs.
 */

/** Small per-device settings (D-0068 §2: presentation state stays on the device). */
interface DmKeyValueStore {
    fun getString(key: String): String?

    fun putString(key: String, value: String?)
}

/** The result of «☁ Sincronizar»: a message for the DM when something needs attention. */
data class DmSyncResult(val synced: Boolean, val message: String? = null)

interface DmPlatform {
    val store: DmKeyValueStore

    /** Reads a bundled SRD file such as `quick_reference_es.json`. Called off the main thread. */
    fun readSrd(fileName: String): String

    /** Decodes a stored image, scaled down so its longer side is about [maxSide] pixels. */
    fun decodeImage(bytes: ByteArray, maxSide: Int): ImageBitmap?

    /** True on the Windows app: shows «← Atrás», scroll bars and the Esc / Alt+← keys (owner request 2026-11-12). */
    val isDesktop: Boolean
        get() = false

    /** How the rail names the way out of the DM desks. */
    val exitLabel: String
        get() = "← Campañas"

    /** «Dados visibles» in the App settings (P34); otherwise the compact result. */
    val visibleDice: Boolean

    /** Package 8: whether this App can ask the server for rules answers (it has a hosted connection). */
    val rulesAiAvailable: Boolean
        get() = false

    /** Package 8: an AI answer from the SRD passages found on the device; throws when unavailable. */
    suspend fun answerRules(question: String, passages: List<HostedRulesPassage>): String =
        throw UnsupportedOperationException("Rules answers are not available in this App.")

    /** Shows a d20 result the same way the PC sheet does on this platform (P34). */
    @Composable
    fun D20Result(roll: CharacterResolvedD20Roll) = DefaultD20Result(roll, visibleDice)
}

val LocalDmPlatform = staticCompositionLocalOf<DmPlatform> { error("DmPlatform not provided") }

@Composable
fun DefaultD20Result(roll: CharacterResolvedD20Roll, visibleDice: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text("Total ${roll.total}", style = MaterialTheme.typography.titleLarge)
            if (visibleDice) {
                val dice = listOfNotNull(roll.firstDie, roll.secondDie).joinToString(" · ") { "d20 $it" }
                Text("$dice ${signed(roll.modifier)}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

internal fun nowEpochSeconds(): Long = System.currentTimeMillis() / 1000

internal fun signed(value: Int) = if (value >= 0) "+$value" else value.toString()

/** Same distance text as the PC sheet: «30 ft (9 m)». */
internal fun formatFeet(feet: Int): String {
    val metricTenths = feet * 3
    val wholeMeters = metricTenths / 10
    val remainder = abs(metricTenths % 10)
    val metric = if (remainder == 0) wholeMeters.toString() else "$wholeMeters,$remainder"
    return "$feet ft ($metric m)"
}
