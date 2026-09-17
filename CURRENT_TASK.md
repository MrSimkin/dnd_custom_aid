# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** finish Homebrew/Rule documentation closure validation, then begin Place payload + local persistence core
- **Execution state:** Homebrew/Rule implementation and its documentation/checkpoint closure are both merged; PR #54 validation was green before merge, and post-merge Scaffold is now running on the exact documentation merge commit. Place implementation has been scoped/prepared but no Place branch/code has been started yet.
- **Integrated `main`:** `226fcdc0de5606169fa4c007890de0712a2a024b`
- **Homebrew implementation:** PR #53 merged as `fc870fe8303b0f9925c479bdc21c388ef5cc8450`; push Scaffold `35256210643` — **SUCCESS**; PR Scaffold `35256230033` — **SUCCESS**; post-merge Scaffold `35256531651` — **SUCCESS**
- **Homebrew documentation closure:** PR #54 — `docs: record Wave 6 Homebrew Rule integration and Place continuation` — **MERGED** as `226fcdc0de5606169fa4c007890de0712a2a024b`
- **PR #54 validation:** Scaffold `35257723293` — **SUCCESS** before merge
- **Post-merge documentation validation:** Scaffold `35257964535` for `226fcdc0de5606169fa4c007890de0712a2a024b` — **IN PROGRESS** at latest inspection; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin is in `Build and test Kotlin surfaces`
- **Next bounded package after green docs validation:** Place payload + local persistence core using `ReusableContentFamily.PLACE`
- **Prepared Place shape:** domain-specific `PlacePayload`; typed `PlaceKind` preserving ordinary Place vs specialized Shop semantics; self-contained descriptive/retrieval fields only; Personal/Campaign create/read/copy/update/tombstone using existing reusable-content identity/provenance/revision seams; SQLDelight `place_payload`; migration `22.sqm`; metadata-only Place backfill; legacy Desktop fixture update; focused persistence/copy/revision/migration/reopen tests
- **Place fields deliberately in-scope for first package:** kind, summary/description, geography-or-area, function/purpose, narrative context, presentation/atmosphere, services, interactives, hooks, player-safe text, DM notes, paper references and tags, with exact low-level naming delegated during implementation
- **Explicit Place deferrals:** no linked NPC/Scene/Zone/Encounter object IDs or dependency-copy graph; no clocks; no media/object-storage references; no automatic reveal/publication behavior; no Stage Manager UI; no hosted reusable-content sync/import-export/AI helper work
- **Session behavior:** owner wants continuation across safe durable boundaries but to stop when remaining work becomes waiting-heavy or reasonably risks running too long. The temporary prior polling override does not permanently change repository protocol.
- **Next action:** inspect post-merge Scaffold `35257964535`; if green, create a short-lived `wave6/place-payload-persistence` branch from current `main` and implement the bounded Place package atomically. If it fails, inspect only the exact attributable failure. If still materially running, stop cleanly rather than repeatedly poll.
- **Do not:** restart completed Homebrew work, start Place before documentation post-merge validation is green, introduce generalized relationship/dependency infrastructure, start Wave 7 Manager UI, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or a universal payload abstraction.
