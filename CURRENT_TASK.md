# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 7 — Desktop authoring Managers
- **Current task:** validate and integrate the first visible Desktop Creature/Monster Manager local authoring core
- **Execution state:** Wave 6 core reusable-content architecture is fully closed, documented and post-merge green. The first Wave 7 implementation has been prepared as one reviewed commit and activated on its short-lived branch. Push Scaffold validation is running; backend and hosted database are already green and Kotlin is the remaining compiler/test gate.
- **Integrated `main`:** `504d85ca69c0af0a60f17b2ba2ffbfdfd6c74923`
- **Wave 6 closure:** docs PR #60 — **MERGED** as `504d85ca69c0af0a60f17b2ba2ffbfdfd6c74923` from exact head `7df2e13e17e4842421e7633a51f1a0c1be949bf0`; PR Scaffold `35267708440` — **SUCCESS**; post-merge Scaffold `35266185306` — **SUCCESS**
- **Operating rule:** routine green engineering boundaries do not require owner confirmation. Continue automatically through implementation/CI/PR/merge/post-merge/docs closure while scope/risk is unchanged; stop only for a real failure/judgment, owner product/scope/risk decision, provider/manual action, destructive/cost/security ambiguity, or genuine async wait with nothing safe/useful left to do.
- **Active branch:** `wave7/desktop-creature-manager-core`
- **Branch HEAD:** `38d68dc832188f29c76ec40990297f53a85e9bed`
- **Commit:** `feat: add Desktop Creature Manager authoring core`
- **Exact diff:** 1 commit, 4 files, +770/-4: new `DesktopCreatureManager.kt`; focused `DesktopCreatureManagerControllerTest.kt`; small `Main.kt` routing/wiring update; bounded `CreatureContentRepository` atomic name+payload save extension
- **Visible Manager scope:** existing Desktop `MANAGERS` destination now opens a Creature/Monster Manager; browse/search Personal plus active-Campaign Creatures; create Personal or Campaign Creatures; open/edit the complete existing Creature payload; show scope/provenance/revision; explicit Personal -> active Campaign copy; active campaign context panel remains visible
- **Identity rule:** Personal authoring uses only a uniquely resolvable locally persisted active DM account; no hard-coded/fake owner identity is created. If identity is ambiguous/unavailable, Personal creation is disabled while Campaign-local content remains usable.
- **Revision rule:** display name and Creature payload save atomically through one optimistic revision mutation; stale/deleted decisions are surfaced and cannot silently overwrite/resurrect content.
- **Focused test:** resolves local DM ownership, creates Personal Creature, atomically edits name/stat block, verifies stale-write rejection, then copies independently into Campaign with retained provenance.
- **Deferred from this first slice:** Official/SRD browsing, import/export, Creature Creator Assistant/advisory balance helpers, media/object storage, hosted reusable-content sync, generalized all-Manager abstractions, live combat behavior.
- **Wave 7 push CI:** Scaffold `35267065066` for exact head `38d68dc832188f29c76ec40990297f53a85e9bed` — **IN PROGRESS**; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin in setup/build and no failure evidence yet.
- **Environment note:** local container cannot resolve GitHub, so repository CI is the authoritative compile/test gate for this connector-native patch.
- **Next action:** inspect Scaffold `35267065066` after natural progress. If green, review exact diff once and open the Creature Manager PR immediately; continue through PR CI/merge/post-merge validation automatically. If Kotlin exposes a concrete compile/test failure, repair only that failure.
- **Do not:** reopen Wave 6 work, broaden the first Creature Manager slice into import/export/AI/media/hosted sync, invent owner identities, or generalize every future Manager before a concrete need.
