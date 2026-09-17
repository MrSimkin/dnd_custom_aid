# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 6 foundation merge:** `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`  
**Post-merge Scaffold:** `35220099721` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 foundation lifecycle:** COMPLETE / INTEGRATED  
**Current normal work:** determine the next bounded Wave 6 package from current `main`

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

PR #42, #43 and #44 Wave 5 packages are complete/merged. PR #46 `feat: add Wave 6 reusable content persistence foundation` is also merged; merge commit `013abbb9e57af0ba04fe1e8b678e8ed29522bedd` passed post-merge Scaffold `35220099721`.

## Completed Wave 5 branches

- `wave5/desktop-workbench-shell` — historical implementation ref, PR #42 merged/owner-QA accepted;
- `wave5/campaign-membership-administration-core` — historical implementation ref, PR #43 merged;
- `wave5/desktop-hosted-campaign-administration` — historical implementation ref, PR #44 merged/owner-QA accepted.

Do not resume them for normal implementation.

## Completed Wave 6 foundation branch

- `wave6/reusable-content-persistence` — historical implementation ref, PR #46 merged.

Do not resume it for new work. The integrated foundation is now part of `main`.

## Wave 6 branch rule

Wave 6 remains active. Determine the next bounded package from the current architecture, decisions and roadmap, then create a new short-lived outcome-oriented branch from current `main`.

Do not create permanent Player/Server/Desktop/provider-specific silos, and do not assume a later Wave 6 implementation already exists merely because the foundation is integrated.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state; current `main`, `LATEST` and the current checkpoint control.

## Accidental refs

During 2026-09-17 reconstruction:

- `__noop_should_not_create__` was verified with no unique project work and no PR;
- `__should_not_create__` was verified with no unique project work and no PR.

They are non-authoritative. Safe deletion is optional housekeeping and not a development blocker. Do not delete legitimate historical implementation branches merely because they are inactive.

## Exact resume rule

Resume from current `main` and `docs/checkpoints/2026-09-17_WAVE6_REUSABLE_CONTENT_FOUNDATION_INTEGRATED.md`. Do not redeploy Cloudflare for documentation-only changes. Determine the next bounded Wave 6 package from current authority before implementation.