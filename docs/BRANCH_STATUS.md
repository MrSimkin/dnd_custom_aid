# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 6 Zone merge:** `0b73d79e46edb7022ace2a2efd161149d9aefc73`  
**Post-merge Scaffold:** `35262999144` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 foundation lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Creature payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 NPC payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Homebrew/Rule payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Place payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Zone payload lifecycle:** COMPLETE / INTEGRATED  
**Current normal work:** Zone documentation/checkpoint closure; next Wave 6 implementation package selected only after this closure integrates

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

PR #46 integrated the Wave 6 reusable-content persistence foundation. PR #48 integrated Creature payload persistence. PR #51 integrated NPC payload persistence. PR #53 integrated lightweight Homebrew/Rule payload persistence. PR #55 integrated Place payload persistence. PR #57 `feat: add Wave 6 Zone payload persistence` is merged as `0b73d79e46edb7022ace2a2efd161149d9aefc73`; post-merge Scaffold `35262999144` passed.

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
- `wave6/place-payload-persistence` — historical implementation ref, PR #55 merged;
- `wave6/zone-payload-persistence` — historical implementation ref, PR #57 merged.

Do not resume these branches for new work. Their integrated scope is part of `main`.

## Current Wave 6 branch rule

The current short-lived branch is `docs/wave6-zone-integrated`, recording the integrated Zone package and exact post-Zone continuation point.

After this closure is merged, select the next dependency-safe bounded Wave 6 implementation package from current D-0072/D-0073 authority and the integrated foundation + Creature + NPC + Homebrew/Rule + Place + Zone state. Encounter is the remaining reserved rich reusable family, but do not infer that its entire future relationship/dependency graph belongs in the first package.

Preserve the established architecture boundaries: Personal/Campaign independent copies with retained provenance; optimistic revisions/stale-write rejection; tombstones/non-resurrection; domain-specific payloads rather than one universal arbitrary model; no provider work without a concrete requirement.

Under the coherent-task continuation rule, do not turn ordinary branch creation, CI, PR creation, merge and post-merge validation into separate owner-confirmation turns when scope/risk is unchanged.

## Documentation closure branches

- `docs/wave6-creature-integrated` — historical after PR #50 merge;
- `docs/wave6-npc-integrated` — historical after PR #52 merge;
- `docs/wave6-homebrew-integrated` — historical after PR #54 merge;
- `docs/wave6-place-integrated` — historical after PR #56 merge;
- `docs/wave6-zone-integrated` — current short-lived documentation/checkpoint branch recording PR #57 integration and the post-Zone continuation point.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state; current `main`, `LATEST` and the current checkpoint control.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Exact resume rule

Finish and merge `docs/wave6-zone-integrated`. Then resume from current `main` and `docs/checkpoints/2026-09-17_WAVE6_ZONE_PAYLOAD_INTEGRATED.md`, review current D-0072/D-0073 authority, select the next bounded dependency-safe Wave 6 continuation package, and proceed autonomously through routine safe gates. Do not redeploy Cloudflare for documentation or local/shared persistence-only changes.
