# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** validate integrated Place payload package, then close its checkpoint/documentation
- **Execution state:** Homebrew/Rule implementation and documentation closure are fully merged/green; Place implementation passed both exact-head push and PR validation and PR #55 is merged; post-merge Scaffold on the exact `main` merge commit is now the only gate
- **Integrated `main`:** `d978a4191054227a03b32ecca3e7ceadc5d6e869`
- **Place implementation branch:** `wave6/place-payload-persistence`
- **Final Place branch HEAD:** `99fe6151c253803dd3dffdc6a16fbf6058133c47`
- **Place PR:** #55 — `feat: add Wave 6 Place payload persistence` — **MERGED** as `d978a4191054227a03b32ecca3e7ceadc5d6e869`
- **Place validation:** push Scaffold `35258393882` — **SUCCESS**; PR Scaffold `35258423034` — **SUCCESS**
- **Post-merge validation:** Scaffold `35259027937` for `d978a4191054227a03b32ecca3e7ceadc5d6e869` — **IN PROGRESS**; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin in `Build and test Kotlin surfaces`
- **Integrated Place scope:** typed `PlaceKind` (`PLACE` / specialized `SHOP`); summary, area, function, presentation, services/interactives, hooks, player-safe text, DM notes, paper references and tags; Personal/Campaign create/read/copy/update/tombstone behavior through existing identity/provenance/revision seams; SQLDelight `place_payload`; migration `22.sqm`; metadata-only PLACE backfill; focused round-trip/copy/stale-write/non-resurrection/migration/reopen tests; one-line legacy Desktop fixture adjustment
- **Explicit deferrals:** no Place/NPC/Scene/Zone/Encounter relationship/dependency graph; no clocks; no media/object-storage references; no automatic reveal/publication behavior; no Manager UI; no hosted reusable-content sync/import-export/AI/provider work
- **Session behavior:** owner wants continuation across safe durable boundaries but to stop when remaining work becomes waiting-heavy or reasonably risks running too long.
- **Next action:** inspect post-merge Scaffold `35259027937`; if green, create/merge the Place integration checkpoint/documentation closure and only then select the next bounded Wave 6 package from current authority. If the run fails, inspect only the exact attributable failure. If it remains materially running, stop cleanly rather than repeatedly poll.
- **Do not:** restart completed Place implementation, broaden Place into relationship/dependency infrastructure, start Wave 7 Manager UI, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or a universal payload abstraction.
