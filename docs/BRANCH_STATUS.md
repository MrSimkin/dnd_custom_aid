# Branch status and repository-ordering map

**Updated:** 2026-10-06 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified functional `main`:** `15f86ec8285e69969054d40defaa2c16259b8dce` (PR #107 Mara Custom-v2 Extended overflow repair integrated)  
**Post-merge Scaffold:** `36276643170` / #3929 — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Wave 7 lifecycle:** ACTIVE  
**Current normal work:** PC Sheet PDF Export — **Phase 3 ACTIVE / Narrative + Traits + Combat/Actions + Notes + semantic survival VALIDATED; general bidirectional continuity audit NEXT**. Validated production HEAD: `d4e42818223100d96de32e0fe4a278dc0356153e`; Scaffold #4521 / `37557543660` SUCCESS. Proof HEAD: `59b039bdccc8d33a79252536c1d5d7bd10470e0e`; Scaffold #4522 / `37558318363` SUCCESS; proof artifact `11455609450`, digest `sha256:72cb4aba21442a76aa887605cdcc2a05a117c50475f159a8d5471f268529ec97`. M50800-26/29 are demonstrated in current Fantasy + Custom-family outputs; three front CI cycles reduced OPEN to zero and did not trigger anti-loop. Historical source/footer residue remains pending M50800-03, not hidden as fixed. **Next action: read-only general bidirectional continuity audit; code only if a real split-flow directionality gap is demonstrated.** Remaining exact-candidate/owner gates stay blocked.**

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

Integrated Wave 6 implementation PRs:

- #46 reusable-content persistence foundation;
- #48 Creature payload persistence;
- #51 NPC payload persistence;
- #53 lightweight Homebrew/Rule payload persistence;
- #55 Place payload persistence;
- #57 Zone payload persistence;
- #59 Encounter payload persistence.

Integrated Wave 7 implementation PRs:

- #61 Desktop Creature/Monster Manager local authoring core — merged as `12a62288457ebe5892f90f637fe41c142b094591`;
- #63 Desktop NPC Manager local authoring core — merged as `58a565c3a33a433ce47e7fd4ac1185b5f980644f`;
- #65 Desktop Homebrew & Rules Manager lightweight local authoring core — merged as `6febe3f936593999834189b92aeda9d209385fa7`;
- #67 Desktop Place/Shop Manager local authoring core — merged as `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`;
- #69 Desktop Stage Manager Place retrieval/organization core — merged as `a99f03bf53637494695cc39b39d077ea1ef61ada`;
- #71 Adventure/Scene Spine lightweight local core — merged as `a84a8857102806f8a9ac588545167d697ea8a311`;
- #73 Desktop Dungeon/Zone Manager local authoring core — merged as `5be90a994f453e5444ecf00762cd72407cfe790a`;
- #75 Desktop Encounter Manager local authoring core — merged as `7000535b78df2b2a7149b019796ff3d5903fdb3d`;
- #77 Desktop PC Manager inspection/audit core — merged as `e14784390971f2e27025dd2fff1f5000658eb2f0`;
- #79 PC ownership/controller administration repository core — merged as `2e12400ee18026c702d6727793a3aea5d23d07b4`;
- #83 PC Sheet PDF Export shared semantic/render-plan foundation — merged as `f6350d34087aae55d5247f2ba23153814eeed04b`;
- #85 PC Sheet PDF renderer/visual gate closure — merged as `49dfc78f132c9db9763513c591e877a1749285d5`;
- #86 Desktop PC Sheet Save/Share integration — merged as `c28ad548113b368413e479de544c85aa8c924ef4`.
- #88 Android PC Sheet generated renderer bridge — merged as `c5963881bdff2597770d3f6a26992b8567b2a35b`;
- #89 Android Player / authorized-DM PC Sheet Save/Share — merged as `e6e153a53bba8aa532b5c371dcc16849a901a541`.

PC authority administration validation: corrected push Scaffold `35291183960`, PR Scaffold `35291417403`, post-merge Scaffold `35291685597` — all SUCCESS. Initial push `35290905581` failed on narrow backend row-typing and Kotlin visibility compile issues while the hosted-database contract passed.

Do not restart completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager, Place/Shop Manager, Stage retrieval, Scene Spine, Dungeon/Zone Manager, Encounter Manager, PC Manager inspection/audit or PC authority repository implementation without new defect evidence.

## Completed implementation branches

Wave 5 historical branches:

- `wave5/desktop-workbench-shell`;
- `wave5/campaign-membership-administration-core`;
- `wave5/desktop-hosted-campaign-administration`.

Wave 6 historical branches:

- `wave6/reusable-content-persistence`;
- `wave6/creature-payload-persistence`;
- `wave6/npc-payload-persistence`;
- `wave6/homebrew-rule-payload-persistence`;
- `wave6/place-payload-persistence`;
- `wave6/zone-payload-persistence`;
- `wave6/encounter-payload-persistence`.

Wave 7 historical implementation branches:

- `wave7/desktop-creature-manager-core` — PR #61 merged;
- `wave7/desktop-npc-manager-core` — PR #63 merged;
- `wave7/desktop-homebrew-rules-manager-core` — PR #65 merged;
- `wave7/desktop-place-shop-manager-core` — PR #67 merged;
- `wave7/desktop-stage-manager-core` — PR #69 merged;
- `wave7/adventure-scene-spine-core` — PR #71 merged;
- `wave7/desktop-dungeon-zone-manager-core` — PR #73 merged;
- `wave7/desktop-encounter-manager-core` — PR #75 merged;
- `wave7/desktop-pc-manager-audit-core` — PR #77 merged;
- `wave7/desktop-pc-authority-administration-core` — PR #79 merged;
- `wave7/pc-sheet-pdf-export-foundation` — PR #83 merged.

Integrated scope belongs to `main`; these refs are not continuation authority.

## Documentation closure branches

Historical/short-lived Wave 7 closure branches:

- `docs/wave7-creature-manager-integrated` — Creature -> NPC;
- `docs/wave7-npc-manager-integrated` — NPC -> Homebrew & Rules;
- `docs/wave7-homebrew-rules-manager-integrated` — Homebrew & Rules -> Place/Shop;
- `docs/wave7-place-shop-manager-integrated` — Place/Shop -> Stage;
- `docs/wave7-stage-manager-integrated` — Stage -> Adventure/Scene Spine;
- `docs/wave7-scene-spine-integrated` — Scene Spine -> Dungeon/Zone Manager;
- `docs/wave7-dungeon-zone-manager-integrated` — Dungeon/Zone Manager -> Encounter Manager / Encounter Creator;
- `docs/wave7-encounter-manager-integrated` — Encounter Manager -> PC Manager / Audit;
- `docs/wave7-pc-manager-audit-integrated` — PC Manager inspection/audit -> ownership/controller administration;
- `docs/wave7-pc-authority-repo-integrated` — authority repository merge -> provider deployment gate;
- `docs/wave7-pc-sheet-pdf-foundation-integrated` — PDF semantic foundation -> physical renderer/template mapping.

After a closure merges, normal implementation starts from current `main`; do not continue coding on a docs branch.

## Current branch direction

**Active implementation branch:** `repair/mara-phase3-semantic-flow-compositor` — sole non-main continuation authority for Phase 3.

**Completed audit branch:** `audit/mara-phase2-existing-repair` — Phase-2 historical evidence only.

**Historical repair evidence:** `repair/pc-sheet-adaptive-continuations-cross-family` at `ce695c...` remains evidence for reusable primitives identified by the Phase-2 burn-down; it is not acceptance authority and must not be resumed wholesale.

Current authority:
- canonical resume checkpoint: `docs/checkpoints/2026-10-06_MARA_PHASE3_HEALTH_CONSOLIDATION.md`;
- Phase-3 implementation authority: `docs/checkpoints/2026-09-30_MARA_PHASE3_IMPLEMENTATION.md`;
- binding owner-intent burn-down: `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`;
- acceptance matrix: `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`;
- Phase-1 provenance: owner-tested 50800 APK = `15f86ec...` / SHA-256 `ff367e...`;
- do not reuse `0.5.0-preqa.8 / 50800`;
- canonical interruption/recovery prompt: `docs/checkpoints/2026-10-06_MARA_PHASE3_CONTINUATION_RECOVERY_PROMPT.md`;
- owner objective: continue bounded Phase-3 burn-down autonomously toward a new uniquely versioned owner QA candidate once all hard gates pass;
- Current Snapshot and Media/Handouts remain blocked.

Phase-3 validated package state:
1. shared semantic-flow contracts — GREEN;
2. native-module contracts — GREEN;
3. global Extended-page composer contract — GREEN;
4. Custom-v2 Inventory native/semantic package — GREEN at `d9032bc7...` / Scaffold #4499;
5. Custom-v2 Resources/Options compositor + cleanup — GREEN at `29102383...`;
6. Custom-v1 Resources/Options compositor — GREEN at `da623415...`;
7. Fantasy Resources/Options compositor + semantic wrap — GREEN at `f4dd69da...`;
8. Custom-v2 Traits native compositor — GREEN at `d8a4e4a8...`;
9. Fantasy Traits native compositor — GREEN at `fdad9f87...`;
10. Custom-v1 Narrative semantic association — GREEN at `e6e07a13...` / run `37537059037`;
11. anti-loop health result — `CONTINUE_WITH_GUARDRAIL`;
12. current Custom-v1 Traits native-module package — OPEN at `92e460f0...`; first CI cycle #4516 / `37543504407` RED in kotlin, no proof artifact; next allowed attempt is cycle 2/4 and must remain inside this front.

No owner visual handoff occurs until exact-candidate all-four-family internal acceptance passes.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
