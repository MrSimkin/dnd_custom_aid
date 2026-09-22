# Branch status and repository-ordering map

**Updated:** 2026-09-22 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified integrated `main`:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f` (PC Sheet PDF foundation docs closure PR #84)  
**Post-merge Scaffold:** `35295050340` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Wave 7 lifecycle:** ACTIVE  
**Current normal work:** PC Sheet PDF Export — recover exact frozen visual baselines after owner rejection

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
- #83 PC Sheet PDF Export shared semantic/render-plan foundation — merged as `f6350d34087aae55d5247f2ba23153814eeed04b`.

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

Active branch:

`wave7/pc-sheet-pdf-renderer-template-proof`

Draft PR:

**#85 — do not merge**

Current state:

- Strategy 1 / Hybrid is proven for the owner-authored Custom templates;
- Para Hoja de PJ Symbols v8 is OWNER APPROVED / FROZEN;
- Custom v1 Run-7 base sheet is OWNER APPROVED / FROZEN;
- Custom v2 per-Attribute Run-4 base sheet is OWNER APPROVED / FROZEN;
- Custom v2 per-Ability corrected Run-2 base sheet is OWNER APPROVED / FROZEN;
- Custom v1 Extended Run 1 is OWNER REJECTED / historical evidence only;
- Custom v1 Extended Run 2 is NOT APPROVED after owner review; it is close but has page-6 white-cut artifacts, block-selection issues and incorrect Ability/skill typography;
- Custom v1 Extended Run 3 was owner-reviewed and is NOT APPROVED / SUPERSEDED;
- Custom v1 Extended Run 4 was owner-reviewed and is NOT APPROVED / SUPERSEDED;
- Custom v1 Extended Run 5 was owner-reviewed and is NOT APPROVED / SUPERSEDED;
- Custom v1 Extended Run 6 is OWNER APPROVED / FROZEN at `69b308f3d5d493d06bd0107ac66c7524935aa9fa`;
- Run-6 Scaffold `35529317947` / #2885 is SUCCESS; proof artifact `10609869599`;
- the complete Custom-v1 visual family (base + all six Extended roles) is frozen and its integrated production audit is PASS at `5d09271231dd395e44f0c4c2a33cdb39509cc6b5`;
- Custom-v2 Extended Run 7 is owner-approved/frozen and its integrated production audit is PASS at `0295f30ca77214284902b0e13a563dcdaf58b501`;
- canonical Custom Extended layered methodology is `docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`;
- Custom v1 Extended Run-2 Scaffold `35484718817` / #2817 is SUCCESS;
- Classic corrected Run 2 complete family is owner-approved/frozen and its integrated production audit is PASS at `30ac4d073e9fab47a498f4e6dc3d9266c633110e` (Scaffold #3151 / `35677870304` SUCCESS; artifact `10674062891`); spent-slot controls remain blank/writable per the active renderer protocol;
- the application-owned optional Spellbook production gate is PASS at `ac0cbb2b26202bf934ac4926b56899014390613e` (Scaffold #3161 / `35679451757` SUCCESS; artifact `10674370966`); all preceding family pages remain unchanged when the appendix is appended;
- local portrait-byte handoff plus Crop/Fit production is PASS at `93490d2247da5ea46ef50563fd17da78840e659e` (Scaffold #3177 / `35683430947` SUCCESS; artifact `10676160917`); Classic, Custom v1 and both Custom-v2 first-page variants are covered, v2 decorative frames are preserved, and the taller per-Ability portrait geometry has an explicit regression guard;
- App Modified + continuation candidate `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d` / artifact `10713481125` is OWNER REJECTED / DO NOT USE because it regressed previously approved Classic/v1/v2 visual mechanics;
- Classic Run 1 is OWNER REJECTED / historical evidence only;
- Classic corrected Run 2 complete nine-page family is OWNER APPROVED / FROZEN;
- Classic baseline commit is `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`;
- Scaffold `35480871986` / #2793 is SUCCESS;
- stable pointer: `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
- PR #85 remains DRAFT / DO NOT MERGE.

Next continuation sequence:

1. recover/download the exact owner-approved Classic, Custom-v1 and Custom-v2 proof artifacts and treat them as the visual goldens;
2. compare production output against those exact goldens and reuse/extract approved renderer components where it diverged;
3. add golden visual regression protection before reintroducing App Modified + continuation cues;
4. do not begin Save/Share and do not merge PR #85 until the recovered output passes new owner visual QA.

Relevant durable records:

- `docs/checkpoints/2026-09-21_PC_SHEET_PDF_PORTRAIT_HANDOFF_PRODUCTION_PASS.md`;
- `docs/checkpoints/2026-09-21_PC_SHEET_PDF_SPELLBOOK_PRODUCTION_PASS.md`;
- `docs/checkpoints/2026-09-21_PC_SHEET_PDF_CLASSIC_INTEGRATED_PRODUCTION_AUDIT_PASS.md`;
- `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`;
- `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`;
- `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_APPROVED.md`;
- `docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`;
- `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_REVIEW.md`;
- `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN5_OWNER_REVIEW.md`;
- `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN4_OWNER_REVIEW.md`;
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_OWNER_REVIEW.md`;
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_WIP_SAFETY.md`;
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN2_OWNER_REVIEW.md`;
- `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
- `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
- `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
