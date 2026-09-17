# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime merge:** `6febe3f936593999834189b92aeda9d209385fa7` (PR #65)  
**Post-merge Scaffold:** `35277359425` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Wave 7 lifecycle:** ACTIVE  
**Current normal work:** Homebrew & Rules Manager documentation closure, then Desktop Place/Shop Manager local authoring core

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
- #65 Desktop Homebrew & Rules Manager lightweight local authoring core — merged as `6febe3f936593999834189b92aeda9d209385fa7`.

Homebrew Manager validation: push Scaffold `35272269481`, PR Scaffold `35272560206`, post-merge Scaffold `35277359425` — all SUCCESS.

Do not restart completed Wave 5, Wave 6, Creature Manager, NPC Manager or Homebrew/Rules Manager implementation without new defect evidence.

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
- `wave7/desktop-homebrew-rules-manager-core` — PR #65 merged.

Integrated scope belongs to `main`; these refs are not continuation authority.

## Documentation closure branches

Historical/short-lived Wave 7 closure branches:

- `docs/wave7-creature-manager-integrated` — recorded Creature Manager integration and continuation to NPC Manager;
- `docs/wave7-npc-manager-integrated` — recorded NPC Manager integration and continuation to Homebrew & Rules;
- `docs/wave7-homebrew-rules-manager-integrated` — records Homebrew & Rules integration and continuation to Place/Shop.

After a closure merges, normal implementation starts from current `main`; do not continue coding on a docs branch.

## Wave 7 next branch direction

The next selected package is **Desktop Place/Shop Manager — local authoring core**.

Expected short-lived branch name:

`wave7/desktop-place-shop-manager-core`

Initial bounded scope:

- expose Place authoring within the existing Desktop Managers surface without introducing a universal Manager framework;
- browse/search Personal and active-Campaign Places;
- create/open/edit the integrated Place data;
- expose Shop specialization only where the existing Place payload supports it, or where concrete implementation evidence requires a bounded extension;
- show scope/provenance/revision;
- explicitly copy Personal -> active Campaign as an independent object;
- save display name + payload atomically under one optimistic revision;
- preserve stale-write/tombstone semantics and the conservative uniquely-resolvable local DM identity rule;
- add focused controller tests for persistence, atomic edit, stale rejection, provenance and independent copy behavior.

Do not pull Scene Spine into this first Place package. Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts, richer Stage/Scene work and deferred richer Homebrew families remain later concrete packages.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
