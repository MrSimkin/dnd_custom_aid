package io.github.mrsimkin.dndcustomaid.shared.hosted

import kotlinx.serialization.json.Json

/**
 * Canonical JSON contract for hosted API traffic.
 *
 * CharacterBackupDocument carries required wire envelope fields (`format` and `version`) as
 * Kotlin defaults. Hosted requests must therefore encode defaults explicitly; otherwise a valid
 * in-memory snapshot can lose those fields on the wire and be rejected by the Worker.
 */
internal fun hostedWireJson(): Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}
