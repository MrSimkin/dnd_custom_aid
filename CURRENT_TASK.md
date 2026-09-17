# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 7 — Desktop authoring Managers
- **Current task:** validate and integrate the Desktop NPC Manager local authoring core
- **Execution state:** Wave 6 is fully closed. The first Wave 7 Creature/Monster Manager is implemented, documented and post-merge green. The second Wave 7 package, Desktop NPC Manager, is active as one reviewed implementation commit; push and PR exact-head validation are running with backend and hosted-database already green and Kotlin as the remaining compiler/test gate.
- **Integrated `main`:** `999489e4693a53c3e6a41f810f6151e0b22869d3`
- **Creature Manager implementation:** PR #61 — **MERGED** as `12a62288457ebe5892f90f637fe41c142b094591`; push Scaffold `35267065066`, PR Scaffold `35267241770`, post-merge Scaffold `35267674641` — all **SUCCESS**.
- **Creature Manager docs closure:** PR #62 — **MERGED** as `999489e4693a53c3e6a41f810f6151e0b22869d3` from exact head `3b4b7a2be6fda6a79bbb89c9e94d765943b0c865`; PR Scaffold `35268367588` and post-merge Scaffold `35268526297` — **SUCCESS**.
- **Operating rule:** routine green engineering boundaries do not require owner confirmation. Continue automatically through implementation/CI/PR/merge/post-merge/docs closure while scope/risk is unchanged; stop only for a real failure/judgment, owner product/scope/risk decision, provider/manual action, destructive/cost/security ambiguity, or genuine async wait with nothing safe/useful left to do.
- **Active branch:** `wave7/desktop-npc-manager-core`
- **NPC implementation HEAD:** `58b680e71ec59c871854eb9c083ff2bc6906fe88`
- **Commit:** `feat: add Desktop NPC Manager authoring core`
- **Exact diff:** 1 commit, 4 files, +954/-4: new `DesktopNpcManager.kt`; focused `DesktopNpcManagerControllerTest.kt`; small `Main.kt` wiring update; bounded `NpcContentRepository` atomic name+payload save extension. No schema/migration changes.
- **Visible NPC scope:** concrete Creature/PNJ selector inside existing `MANAGERS`; browse/search Personal + active-Campaign NPCs; create/open/edit existing Quick and Developed NPC fields; incomplete NPCs remain valid; optional full combat mechanics reuse `CreaturePayload`; show scope/provenance/revision; explicit Personal -> active Campaign independent copy; active campaign context remains visible.
- **Identity/revision rules:** same conservative locally persisted unique-DM Personal-owner rule as Creature Manager; no invented owner identity. Display name + NPC payload update atomically under one optimistic revision; stale/deleted writes cannot silently overwrite/resurrect content.
- **Focused test:** Quick NPC creation; Developed fields; optional combat mechanics; atomic update; stale-write rejection; independent Campaign copy/provenance; removal of combat from Personal master without mutating Campaign copy.
- **Deferred:** NPC assistant/AI ideation, import/export, preserved-live-improvisation promotion workflow, media/object storage, hosted reusable-content sync, generalized all-Managers framework.
- **NPC push CI:** Scaffold `35269013875` — **IN PROGRESS**; backend **SUCCESS**, hosted-database **SUCCESS**, Kotlin in `Build and test Kotlin surfaces`, APK upload pending.
- **NPC PR:** #63 — `feat: add Desktop NPC Manager authoring core` — **OPEN**, non-draft, base `main`, exact head `58b680e71ec59c871854eb9c083ff2bc6906fe88`.
- **NPC PR CI:** Scaffold `35269163375` — **IN PROGRESS** on the exact same head.
- **Environment note:** local container cannot resolve GitHub, so repository CI is the authoritative compile/test gate for connector-native patches.
- **Next action:** inspect push `35269013875`, PR `35269163375`, and PR #63 mergeability after natural progress. If exact-head validation is green, merge #63 with expected head `58b680e71ec59c871854eb9c083ff2bc6906fe88`, then continue through post-merge validation and NPC Manager docs closure automatically. If a concrete compiler/test failure appears, repair only that failure.
- **Do not:** reopen completed Wave 6/Creature work, broaden NPC Manager into assistant/import/export/media/hosted sync, invent owner identities, or introduce generalized Manager infrastructure without concrete need.
