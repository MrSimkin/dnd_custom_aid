# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 6 runtime merge:** `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd`  
**Post-merge Scaffold:** `35265162945` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Current normal work:** Encounter documentation closure, then Wave 7 Desktop Creature/Monster Manager local authoring core

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
- #59 Encounter payload persistence, merged as `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd`.

Encounter validation: push Scaffold `35264310753`, PR Scaffold `35264620723`, post-merge Scaffold `35265162945` — all SUCCESS.

Do not restart completed Wave 5 or Wave 6 implementation without new defect evidence.

## Completed Wave 5 branches

- `wave5/desktop-workbench-shell` — historical, PR #42 merged/owner-QA accepted;
- `wave5/campaign-membership-administration-core` — historical, PR #43 merged;
- `wave5/desktop-hosted-campaign-administration` — historical, PR #44 merged/owner-QA accepted.

## Completed Wave 6 implementation branches

- `wave6/reusable-content-persistence` — PR #46 merged;
- `wave6/creature-payload-persistence` — PR #48 merged;
- `wave6/npc-payload-persistence` — PR #51 merged;
- `wave6/homebrew-rule-payload-persistence` — PR #53 merged;
- `wave6/place-payload-persistence` — PR #55 merged;
- `wave6/zone-payload-persistence` — PR #57 merged;
- `wave6/encounter-payload-persistence` — PR #59 merged.

These are historical implementation refs. Their integrated scope belongs to `main`.

## Documentation closure branches

Historical closures:

- `docs/wave6-creature-integrated`;
- `docs/wave6-npc-integrated`;
- `docs/wave6-homebrew-integrated`;
- `docs/wave6-place-integrated`;
- `docs/wave6-zone-integrated`.

Current short-lived closure branch:

- `docs/wave6-encounter-integrated` — records Encounter integration and the Wave 6 -> Wave 7 transition.

After this closure merges, normal implementation starts from current `main`; do not continue coding on the docs branch.

## Wave 7 branch direction

The first selected Wave 7 package is **Desktop Creature/Monster Manager — local authoring core**.

Expected short-lived branch name: `wave7/desktop-creature-manager-core`.

Initial bounded scope:

- activate the existing Desktop `MANAGERS` destination;
- browse/search Personal and active-Campaign Creature records;
- create/open/edit the existing human-complete Creature payload;
- show scope/provenance;
- explicitly copy Personal -> active Campaign;
- preserve existing revisions/stale-write/tombstone semantics.

Defer Official/SRD catalog integration, import/export, Creator Assistant, media/object storage, hosted reusable-content sync and generalized all-manager abstractions to later concrete packages.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
