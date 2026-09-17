# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** Creature reusable-content payload + local persistence core
- **Execution state:** implementation and bounded CI repair are pushed; replacement CI is currently in progress, so work is stopped at the async CI boundary
- **Integrated `main`:** `72f7553fdd7f03825fafa71a480565bb06358cdb`
- **Active branch:** `wave6/creature-payload-persistence`
- **Branch HEAD:** `8a9ec6425c697472a6c57982acbe8e4001b4fc5d`
- **PR:** #48 — `feat: add Wave 6 Creature payload persistence`
- **Previous CI:** Scaffold run `35246485381` — **FAILURE**
  - `backend`: SUCCESS
  - `hosted-database`: SUCCESS
  - `kotlin`: FAILURE
  - root cause: synthetic legacy Wave-5 Desktop fixture created the latest schema and removed `reusable_content` but left new `creature_payload`, so migration `19.sqm` collided with the leftover table
- **Repair commit:** `8a9ec6425c697472a6c57982acbe8e4001b4fc5d` — test fixture now drops `creature_payload` before `reusable_content`; production migration logic unchanged
- **Replacement CI:** Scaffold run `35252554882` for SHA `8a9ec6425c697472a6c57982acbe8e4001b4fc5d` — **IN PROGRESS** at the single inspection boundary
- **Next action:** inspect Scaffold run `35252554882` exactly once on resume; if complete and green, merge PR #48 and follow the normal post-merge validation/checkpoint flow; if failed, inspect the exact attributable failure and make only a bounded Creature-package correction; if still running, record the current status and stop again without polling.
- **Do not:** restart Creature implementation, start another Wave 6 package, repeatedly poll CI, merge PR #48 before green validation, or make Cloudflare/Neon/Descope/provider changes for this task.
