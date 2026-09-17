# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime merge:** `12a62288457ebe5892f90f637fe41c142b094591` (PR #61)  
**Post-merge Scaffold:** `35267674641` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Wave 7 lifecycle:** ACTIVE  
**Current normal work:** Creature Manager documentation closure, then Desktop NPC Manager local authoring core

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

- #61 Desktop Creature/Monster Manager local authoring core — merged as `12a62288457ebe5892f90f637fe41c142b094591`.

Creature Manager validation: push Scaffold `35267065066`, PR Scaffold `35267241770`, post-merge Scaffold `35267674641` — all SUCCESS.

Do not restart completed Wave 5, Wave 6, or integrated Creature Manager implementation without new defect evidence.

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

Wave 7 historical implementation branch:

- `wave7/desktop-creature-manager-core` — PR #61 merged.

Integrated scope belongs to `main`; these refs are not continuation authority.

## Documentation closure branches

Historical closures include the Wave 6 family closures through `docs/wave6-encounter-integrated`.

Current short-lived closure branch:

- `docs/wave7-creature-manager-integrated` — records the first Wave 7 Manager integration and the continuation to NPC Manager.

After this closure merges, normal implementation starts from current `main`; do not continue coding on the docs branch.

## Wave 7 next branch direction

The next selected package is **Desktop NPC Manager — local authoring core**.

Expected short-lived branch name:

`wave7/desktop-npc-manager-core`

Initial bounded scope:

- expose NPC authoring within the Desktop Managers surface without introducing a universal Manager framework;
- browse/search Personal and active-Campaign NPCs;
- create/open/edit existing Quick/Developed NPC data;
- keep incomplete NPCs valid and combat mechanics optional;
- reuse `CreaturePayload` for optional full mechanics;
- show scope/provenance/revision;
- explicitly copy Personal -> active Campaign;
- preserve revisions/stale-write/tombstone semantics.

Defer NPC assistant/AI ideation, import/export, live-improvisation promotion, media/object storage, hosted reusable-content sync and generalized Manager abstractions to later concrete packages.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
