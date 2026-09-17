# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 6 Place merge:** `d978a4191054227a03b32ecca3e7ceadc5d6e869`  
**Post-merge Scaffold:** `35259027937` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 foundation lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Creature payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 NPC payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Homebrew/Rule payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Place payload lifecycle:** COMPLETE / INTEGRATED  
**Current normal work:** Place documentation/checkpoint closure; next Wave 6 implementation package not yet selected

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

PR #46 integrated the Wave 6 reusable-content persistence foundation. PR #48 integrated Creature payload persistence. PR #51 integrated NPC payload persistence. PR #53 integrated lightweight Homebrew/Rule payload persistence. PR #55 `feat: add Wave 6 Place payload persistence` is merged as `d978a4191054227a03b32ecca3e7ceadc5d6e869`; post-merge Scaffold `35259027937` passed.

## Completed Wave 5 branches

- `wave5/desktop-workbench-shell` — historical implementation ref, PR #42 merged/owner-QA accepted;
- `wave5/campaign-membership-administration-core` — historical implementation ref, PR #43 merged;
- `wave5/desktop-hosted-campaign-administration` — historical implementation ref, PR #44 merged/owner-QA accepted.

Do not resume them for normal implementation.

## Completed Wave 6 branches

- `wave6/reusable-content-persistence` — historical implementation ref, PR #46 merged;
- `wave6/creature-payload-persistence` — historical implementation ref, PR #48 merged;
- `wave6/npc-payload-persistence` — historical implementation ref, PR #51 merged;
- `wave6/homebrew-rule-payload-persistence` — historical implementation ref, PR #53 merged;
- `wave6/place-payload-persistence` — historical implementation ref, PR #55 merged.

Do not resume these branches for new work. Their integrated scope is part of `main`.

## Current Wave 6 branch rule

The current short-lived branch is the documentation/checkpoint closure for the integrated Place package.

After this closure is merged, select the next dependency-safe bounded Wave 6 implementation package from the current D-0072/D-0073 authority and current integrated state. Do not infer the package merely from branch chronology, and do not create its implementation branch until that selection is explicit.

Preserve the established architecture boundaries: Personal/Campaign independent copies with retained provenance; optimistic revisions/stale-write rejection; tombstones/non-resurrection; domain-specific payloads rather than one universal arbitrary model; no provider work without a concrete requirement.

## Documentation closure branches

- `docs/wave6-creature-integrated` — historical after PR #50 merge;
- `docs/wave6-npc-integrated` — historical after PR #52 merge;
- `docs/wave6-homebrew-integrated` — historical after PR #54 merge;
- `docs/wave6-place-integrated` — current short-lived documentation/checkpoint branch recording PR #55 integration and the post-Place continuation point.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state; current `main`, `LATEST` and the current checkpoint control.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Exact resume rule

Finish and merge `docs/wave6-place-integrated`. Then resume from current `main` and `docs/checkpoints/2026-09-17_WAVE6_PLACE_PAYLOAD_INTEGRATED.md`, review current D-0072/D-0073 authority, and select one bounded dependency-safe Wave 6 continuation package. Do not redeploy Cloudflare for documentation or local/shared persistence-only changes.
