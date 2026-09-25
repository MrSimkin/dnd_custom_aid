# Branch status and repository-ordering map

**Updated:** 2026-09-24 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified functional `main`:** `fd781262abfeb47003298562e540721a8515071a` (PR #100 generalized Fantasy bounded-text repair)  
**Post-merge Scaffold:** `36067012766` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Wave 7 lifecycle:** ACTIVE  
**Current normal work:** PC Sheet PDF Export — OWNER-REQUESTED PAUSE. Active WIP is preserved on draft PR #103 / `fix/pc-sheet-cross-family-runtime-correctness`; last implementation/code HEAD before pause is `4e7f2f7127f607d9d97c7de22fa1fcc01514c1b9`. Do not change code, merge, reseed DEV or resume manual QA until the owner explicitly resumes. Canonical continuation is `docs/checkpoints/2026-09-24_PC_SHEET_CROSS_FAMILY_RUNTIME_CORRECTNESS_REPAIR_WIP.md`

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

Current branch direction:

No historical implementation branch is continuation authority. Integrated `main` includes:

- PR #96 — first Aldren Fantasy compact combat/resource runtime overflow repair — merged as `eece864e5867f477b8bdd57596a1bee1638b4ceb`;
- PR #98 — in-app DEV/QA tools launch repair + `0.5.0-preqa.2` — merged as `289eafda731c4b1c61e76a947a86ab5d948c537c`; post-merge Scaffold `36060380623` SUCCESS.

Owner runtime QA on `0.5.0-preqa.2` proves:

- DEV/QA screen opens from application settings;
- Gmail Player session can authenticate;
- hosted campaign count = 1;
- hosted PC count = 3;
- applied PCs = 0 / unchanged PCs = 3;
- campaign/PC conflicts = 0;
- queued/retryable/blocked mutations = 0;
- local hosted outbox is empty.

The subsequent Aldren Fantasy Sheet / Permanente Save attempt still fails before a usable PDF is saved, now on long equipment/weapon descriptive text. Because this follows the earlier compact combat/resource overflow, the active engineering route is **generalized Fantasy Sheet bounded-text routing**, not an equipment-only patch.

Completed repair branch:

`fix/fantasy-sheet-generalized-bounded-text-routing`

PR #100 merged as `fd781262abfeb47003298562e540721a8515071a`; merged-main Scaffold `36067012766` — SUCCESS.

Integrated scope implements generalized compact-preview/full-detail routing, preserves the final overflow guard, synchronizes the generated Android renderer, advances the QA build to `0.5.0-preqa.3` / `50300`, exercises Aldren plus Mara stress content, and preserves approved pagination baselines.

The previous generalized Fantasy bounded-text defect is integrated, but the cross-family Aldren survey opened a new active WIP repair branch. That branch is `fix/pc-sheet-cross-family-runtime-correctness`, tracked by draft PR #103, and is currently OWNER-PAUSED rather than completed or merge-ready.

Do **not** proceed to Aldren Share until Save passes; do not proceed to Ilyra, Mara, Current Snapshot, Media/Handouts or the final physical-device gate before the owner-observed Aldren Save pass.

Canonical resume authority:

`RESUME.md -> docs/checkpoints/LATEST.md -> docs/checkpoints/2026-09-24_PC_SHEET_CROSS_FAMILY_RUNTIME_CORRECTNESS_REPAIR_WIP.md`

On resume, inspect PR #103 and exact-head CI first. Do not restart from the older Stage-2 generalized-repair checkpoint.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
