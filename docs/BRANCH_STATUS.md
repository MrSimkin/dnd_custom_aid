# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime merge:** `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240` (PR #67)  
**Post-merge Scaffold:** `35279329344` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Wave 7 lifecycle:** ACTIVE  
**Current normal work:** Place/Shop Manager documentation closure, then Desktop Stage Manager — Place retrieval/organization core

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
- #67 Desktop Place/Shop Manager local authoring core — merged as `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`.

Place/Shop Manager validation: push Scaffold `35278740631`, PR Scaffold `35279052405`, post-merge Scaffold `35279329344` — all SUCCESS.

Do not restart completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager or Place/Shop Manager implementation without new defect evidence.

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
- `wave7/desktop-place-shop-manager-core` — PR #67 merged.

Integrated scope belongs to `main`; these refs are not continuation authority.

## Documentation closure branches

Historical/short-lived Wave 7 closure branches:

- `docs/wave7-creature-manager-integrated` — recorded Creature Manager integration and continuation to NPC Manager;
- `docs/wave7-npc-manager-integrated` — recorded NPC Manager integration and continuation to Homebrew & Rules;
- `docs/wave7-homebrew-rules-manager-integrated` — recorded Homebrew & Rules integration and continuation to Place/Shop;
- `docs/wave7-place-shop-manager-integrated` — records Place/Shop integration and continuation to Stage Manager.

After a closure merges, normal implementation starts from current `main`; do not continue coding on a docs branch.

## Wave 7 next branch direction

The next selected package is **Desktop Stage Manager — Place retrieval/organization core**.

Expected short-lived branch name:

`wave7/desktop-stage-manager-core`

Initial bounded scope:

- build the Stage preparation collection over the already-integrated Place/Shop content rather than inventing a parallel Stage persistence family;
- provide richer retrieval/filter/grouping for Places/Shops by existing fields such as kind, area, function, tags and scope, plus recent-update ordering from reusable-content metadata;
- preserve current Place create/open/edit and explicit Personal -> active Campaign independent-copy flows;
- preserve atomic display-name + payload saves, optimistic revisions, stale-write rejection and tombstone/non-resurrection semantics;
- keep active Campaign context visible;
- make only the smallest sharing/refactor needed to reuse existing Place behavior;
- add focused coverage for retrieval/filtering and stable selection/state.

Do not pull Scene Spine into this Stage package. Current source has no `SCENE` reusable-content family, Scene repository or Scene schema. D-0072's lightweight Adventure/Scene Spine is the following concrete package and should own any minimal persistence extension explicitly.

Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts and deferred richer Homebrew families remain later concrete packages.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
