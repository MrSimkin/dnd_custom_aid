# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** lightweight Homebrew/Rule reusable-content payload + local persistence core
- **Execution state:** NPC implementation and documentation closure are integrated/green; Homebrew/Rule implementation is complete as one bounded commit, branch and PR #53 are open, and push/PR Scaffold validation is running
- **Integrated `main`:** `5ede8c6050cc5d597f3fd53020fb79659052f1ab`
- **NPC implementation:** PR #51 merged as `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`; post-merge Scaffold `35254527744` — **SUCCESS**
- **NPC documentation closure:** PR #52 merged as `5ede8c6050cc5d597f3fd53020fb79659052f1ab`; PR Scaffold `35255027154` — **SUCCESS**; post-merge Scaffold `35255226417` — **SUCCESS**
- **Active branch:** `wave6/homebrew-rule-payload-persistence`
- **Branch HEAD:** `0ef3b3466582482fc89f3025c1adf50a5fd40580`
- **PR:** #53 — `feat: add lightweight Homebrew Rule payload persistence` — **OPEN**
- **Homebrew/Rule scope:** typed `DRAFT / ACTIVE / RETIRED` lifecycle; summary/body plus optional category/rationale, examples, related references and tags; Personal/Campaign create/read/copy; optimistic revision/stale-write/tombstone behavior; SQLDelight payload table; migration `21.sqm`; legacy Desktop fixture adjustment; focused persistence/copy/revision/migration/reopen tests
- **Exact diff:** 6 files, +574/-0; no unrelated project files
- **Push CI:** Scaffold `35256210643` — **IN PROGRESS** at latest inspection
- **PR CI:** Scaffold `35256230033` — **IN PROGRESS** at latest inspection
- **Session override:** owner explicitly authorized polling/continuing past the normal async-CI stop rule for this session, until timeout risk becomes reasonably high. This does not permanently change the repository protocol.
- **Next action:** continue push/PR validation; if a bounded Homebrew-package failure appears, repair only that failure; if exact-head/PR CI is green and PR #53 remains mergeable, merge it, validate post-merge `main`, then record the Homebrew/Rule integration checkpoint before selecting the next Wave 6 package.
- **Do not:** flatten structured races/classes/subclasses/backgrounds/feats/spells/items into this lightweight rule payload, introduce Place/Zone/Encounter dependency graphs, start Manager UI, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or a universal payload abstraction.
