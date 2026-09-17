# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** validate the implemented Zone reusable-content payload + local persistence core
- **Execution state:** Place implementation/documentation are fully merged and green; Zone was selected from D-0072/D-0073 authority and the bounded Zone payload/local-persistence package is now implemented as one atomic commit; push Scaffold validation is the only current gate and no Zone PR has been opened yet
- **Integrated `main`:** `e07739366143ff0bfe89669c75563cd9105b640b`
- **Zone branch:** `wave6/zone-payload-persistence`
- **Zone branch HEAD:** `5ea8fe49e770557b08418ff6dbf68152916c1305`
- **Zone commit:** `feat: add Wave 6 Zone payload persistence`
- **Exact diff:** 1 commit, 6 files, +620/-0; five new Zone persistence/test files plus one-line legacy Desktop fixture adjustment
- **Zone payload:** summary; area; presentation; space; exploration; interactives; clues; checks; consequences; encounter brief; DM guidance; player-safe text; paper references; tags
- **Zone semantic boundary:** this is the prepared Area / Zone Brief domain from D-0072, preserving presentation / interaction / encounter orientation; it is not live Dungeon Turn movement-zone state
- **Persistence behavior:** Personal/Campaign create/read/copy/update/tombstone through existing reusable-content identity/provenance/revision seams; independent Personal -> Campaign copy; stale-write rejection; tombstone/non-resurrection; SQLDelight `zone_payload`; migration `23.sqm` with metadata-only `ZONE` backfill; explicit list serialization
- **Focused tests:** prepared Zone round-trip; independent campaign copy/provenance; stale-write and non-resurrection behavior; migration `23.sqm`; database reopen persistence; legacy Desktop fixture updated to omit the new table when simulating Wave 5
- **Explicit deferrals:** no Place/NPC/Zone/Encounter relationship/dependency graph; no Encounter dependency-copy machinery; no live Dungeon Turn zone state; no clocks/advisory-trigger engine; no media/object-storage references; no Manager UI; no hosted reusable-content sync; no import/export/AI/provider work; no universal payload abstraction
- **Push CI:** Scaffold `35262051406` for exact head `5ea8fe49e770557b08418ff6dbf68152916c1305` — **QUEUED**
- **Session behavior:** owner wants work kept in bounded pieces where waiting-heavy CI could otherwise cause timeout.
- **Next action:** inspect push Scaffold `35262051406` once. If an exact bounded Zone failure appears, repair only that failure. If green, review exact branch diff and open the Zone PR in a later bounded step.
- **Do not:** broaden Zone into relationship infrastructure, clocks/triggers/media, jump ahead to Encounter implementation before Zone is validated/integrated, start Wave 7 Manager UI, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or a universal arbitrary/executable payload model.
