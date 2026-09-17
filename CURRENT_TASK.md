# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** validate merged Zone reusable-content payload + local persistence core
- **Execution state:** the bounded Zone payload/local-persistence package passed exact-head push and PR validation and PR #57 is merged into `main`; post-merge validation is healthy but still waiting on the long Kotlin build/test job; no Zone documentation closure branch has been created and no next Wave 6 implementation package has been selected
- **Integrated `main`:** `0b73d79e46edb7022ace2a2efd161149d9aefc73`
- **Zone branch:** `wave6/zone-payload-persistence`
- **Final Zone branch HEAD:** `5ea8fe49e770557b08418ff6dbf68152916c1305`
- **Zone commit:** `feat: add Wave 6 Zone payload persistence`
- **Exact diff:** 1 commit, 6 files, +620/-0; five new Zone persistence/test files plus one-line legacy Desktop fixture adjustment
- **Zone payload:** summary; area; presentation; space; exploration; interactives; clues; checks; consequences; encounter brief; DM guidance; player-safe text; paper references; tags
- **Zone semantic boundary:** prepared Area / Zone Brief domain from D-0072; not live Dungeon Turn movement-zone state
- **Persistence behavior:** Personal/Campaign create/read/copy/update/tombstone through existing reusable-content identity/provenance/revision seams; independent Personal -> Campaign copy; stale-write rejection; tombstone/non-resurrection; SQLDelight `zone_payload`; migration `23.sqm` with metadata-only `ZONE` backfill; explicit list serialization
- **Focused tests:** prepared Zone round-trip; independent campaign copy/provenance; stale-write and non-resurrection; migration `23.sqm`; database reopen persistence; legacy Desktop fixture update
- **Explicit deferrals:** no generalized Place/NPC/Zone/Encounter relationship/dependency graph; no Encounter dependency-copy machinery; no live Dungeon Turn zone state; no clocks/advisory-trigger engine; no media/object-storage references; no Manager UI; no hosted reusable-content sync; no import/export/AI/provider work; no universal payload abstraction
- **Push CI:** Scaffold `35262051406` — **SUCCESS**; backend, hosted-database, Kotlin build/tests and Android debug APK upload all **SUCCESS**
- **Zone PR:** #57 — `feat: add Wave 6 Zone payload persistence` — **MERGED** as `0b73d79e46edb7022ace2a2efd161149d9aefc73` from exact head `5ea8fe49e770557b08418ff6dbf68152916c1305`
- **Zone PR CI:** Scaffold `35262563057` — **SUCCESS**; backend, hosted-database, Kotlin build/tests and Android debug APK upload all **SUCCESS**
- **Zone post-merge CI:** Scaffold `35262999144` for exact `main` merge `0b73d79e46edb7022ace2a2efd161149d9aefc73` — **IN PROGRESS**; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin currently in `Build and test Kotlin surfaces`, Android debug APK upload pending
- **Session behavior:** owner wants work kept in bounded pieces where waiting-heavy CI could otherwise cause timeout.
- **Next action:** inspect post-merge Scaffold `35262999144` once after Kotlin completes. If fully green, create the short-lived Zone integration checkpoint/documentation closure branch and update the bounded docs/checkpoint set before selecting/starting the next Wave 6 implementation package.
- **Do not:** restart completed Zone work, broaden Zone into relationship infrastructure, start Encounter before Zone post-merge validation and documentation closure, start Wave 7 Manager UI, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or a universal arbitrary/executable payload model.
