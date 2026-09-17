# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 5 merge:** `306377df1a453f531af4b670d2b231c88a3c9419`  
**Post-merge Scaffold:** `35168920031` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Next normal work:** short-lived Wave 6 branch from current `main`

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

PR #42, #43 and #44 Wave 5 packages are complete/merged. PR #44 merge commit `306377df1a453f531af4b670d2b231c88a3c9419` passed post-merge Scaffold `35168920031`.

## Completed Wave 5 branches

- `wave5/desktop-workbench-shell` — historical implementation ref, PR #42 merged/owner-QA accepted;
- `wave5/campaign-membership-administration-core` — historical implementation ref, PR #43 merged;
- `wave5/desktop-hosted-campaign-administration` — historical implementation ref, PR #44 merged/owner-QA accepted.

Do not resume them for normal implementation.

## Wave 6 branch rule

Create a short-lived outcome-oriented branch from current `main` for the first reusable-content architecture package. Do not create permanent Player/Server/Desktop/provider-specific silos.

The first package is reusable-content local persistence built on existing Shared scope/provenance/revision/tombstone primitives; Wave 7 Manager UI follows after that foundation.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state; current `main`, `LATEST` and the current checkpoint control.

## Accidental refs

During 2026-09-17 reconstruction:

- `__noop_should_not_create__` was 0 ahead / 2 behind `main`, with no PR;
- `__should_not_create__` was 0 ahead / 59 behind `main`, with no PR.

They have no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker. Do not delete legitimate historical implementation branches merely because they are inactive.

## Exact resume rule

Resume from current `main` and `docs/checkpoints/2026-09-17_WAVE5_INTEGRATED_WAVE6_READY.md`. Do not redeploy Cloudflare for documentation-only changes. Proceed to Wave 6 unless newer Git evidence supersedes this state.