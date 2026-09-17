# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** validate and integrate the implemented Encounter reusable-content payload + local persistence core
- **Execution state:** Zone implementation and Zone documentation closure are fully merged and green. Encounter has been selected, implemented as one atomic commit, reviewed against exact `main`, and activated on its short-lived branch. Push Scaffold validation is the only current gate; no Encounter PR has been opened yet.
- **Integrated `main`:** `739f437c02059af9616c88da8ef9abbc7afbd9dc`
- **Zone implementation:** PR #57 — **MERGED** as `0b73d79e46edb7022ace2a2efd161149d9aefc73`; push Scaffold `35262051406`, PR Scaffold `35262563057`, post-merge Scaffold `35262999144` — all **SUCCESS**
- **Zone documentation closure:** PR #58 — **MERGED** as `739f437c02059af9616c88da8ef9abbc7afbd9dc` from exact head `4f8654a32b4a901d1b30429a63ee503cac7b7f29`; PR Scaffold `35263592437` — **SUCCESS**; post-merge Scaffold `35263788412` — **SUCCESS**, including backend, hosted-database, Kotlin build/tests and Android debug APK upload
- **Operating rule:** coherent-task continuation is now durable in `START_HERE.md`, `AGENTS.md`, and `docs/recovery/INTERRUPTION_RECOVERY.md`. Routine green boundaries do not require owner confirmation; continue automatically until a genuine failure/judgment, owner-level product/scope/risk decision, provider/manual action, destructive/cost/security ambiguity, or real async wait with nothing else safe/useful to do.
- **Encounter branch:** `wave6/encounter-payload-persistence`
- **Encounter branch HEAD:** `cc4e339775b2b34920a46d581bebe83759570a45`
- **Encounter commit:** `feat: add Wave 6 Encounter payload persistence`
- **Exact diff:** 1 commit, 6 files; five new Encounter persistence/test files plus one-line legacy Desktop fixture adjustment
- **Encounter payload:** summary; environment; context; DM guidance; participants; tags; notes. Participant entries support optional Creature/NPC `sourceContentId`, label, quantity, readiness `EXPECTED / RESERVE / CONDITIONAL`, condition text, encounter-local overrides and notes. Freeform participants without reusable dependencies are valid.
- **Dependency semantics:** referenced dependencies must be active Creature/NPC reusable content in the exact same Personal/Campaign scope as the Encounter. Personal -> Campaign Encounter copy copies each unique referenced Personal Creature/NPC dependency exactly once through the existing domain repositories, remaps participant references to the new independent Campaign IDs, preserves provenance, and leaves source masters independent. No generalized relation/dependency graph is introduced.
- **Persistence/testing:** SQLDelight `encounter_payload`; migration `24.sqm` with metadata-only `ENCOUNTER` backfill; JSON participant/tag serialization; create/read/copy/update/tombstone; stale-write/non-resurrection; dependency scope validation; dependency-copy/remap/deduplication; migration/reopen persistence; legacy Desktop fixture adjustment.
- **Explicit deferrals:** no live encounter/combat state; no Place/Zone/rule/clock/handout generalized link graph; no clocks/advisory triggers; no archive/live lifecycle machinery; no Manager UI; no hosted reusable-content sync; no import/export/AI/provider/object-storage work; no generalized dependency framework; no universal payload abstraction.
- **Encounter push CI:** Scaffold `35264310753` for exact head `cc4e339775b2b34920a46d581bebe83759570a45` — **IN PROGRESS**; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin in `Build and test Kotlin surfaces`, APK upload pending; no failure evidence.
- **Next action:** continue productive review while push CI runs; recheck `35264310753` after natural progress. If green, open the Encounter PR immediately and continue through PR CI/merge/post-merge/docs closure under the coherent-task rule. If a concrete Encounter failure appears, repair only that failure.
- **Do not:** restart completed Zone work, broaden Encounter into live combat or a generic graph/dependency engine, start Wave 7 Manager UI prematurely, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or a universal arbitrary/executable payload model.
