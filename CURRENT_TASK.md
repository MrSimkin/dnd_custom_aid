# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** Place reusable-content payload + local persistence core
- **Execution state:** Homebrew/Rule implementation and documentation closure are fully merged/green; Place implementation is complete as one atomic commit and PR #55 is open; push/PR Scaffold validation is currently the only gate
- **Integrated `main`:** `226fcdc0de5606169fa4c007890de0712a2a024b`
- **Homebrew documentation closure:** PR #54 merged as `226fcdc0de5606169fa4c007890de0712a2a024b`; PR Scaffold `35257723293` — **SUCCESS**; post-merge Scaffold `35257964535` — **SUCCESS**
- **Active branch:** `wave6/place-payload-persistence`
- **Branch HEAD:** `99fe6151c253803dd3dffdc6a16fbf6058133c47`
- **PR:** #55 — `feat: add Wave 6 Place payload persistence` — **OPEN**
- **Exact diff:** 1 commit, 6 files, +603/-0; five new Place persistence files plus one-line legacy Desktop fixture adjustment
- **Place scope:** typed `PlaceKind` (`PLACE` / specialized `SHOP`); summary, area, function, presentation, services/interactives, hooks, player-safe text, DM notes, paper references and tags; Personal/Campaign create/read/copy/update/tombstone behavior through existing identity/provenance/revision seams; SQLDelight `place_payload`; migration `22.sqm`; metadata-only PLACE backfill; focused round-trip/copy/stale-write/non-resurrection/migration/reopen tests
- **Explicit deferrals:** no Place/NPC/Scene/Zone/Encounter relationship/dependency graph; no clocks; no media/object-storage references; no automatic reveal/publication behavior; no Manager UI; no hosted reusable-content sync/import-export/AI/provider work
- **Push CI:** Scaffold `35258393882` — **IN PROGRESS**; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin in `Build and test Kotlin surfaces`
- **PR CI:** Scaffold `35258423034` — **IN PROGRESS**; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin in Gradle setup before guards/build/test
- **Session behavior:** owner wants continuation across safe durable boundaries but to stop when remaining work becomes waiting-heavy or reasonably risks running too long.
- **Next action:** inspect push run `35258393882` and PR run `35258423034`; if an exact bounded Place-package failure appears, repair only that failure; if both are green and PR #55 remains mergeable, merge it, validate post-merge `main`, then close the Place integration checkpoint before selecting the next bounded Wave 6 package.
- **Do not:** broaden Place into relationship/dependency infrastructure, start Wave 7 Manager UI, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or a universal payload abstraction.
