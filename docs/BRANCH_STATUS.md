# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 6 Creature merge:** `5762058645ba8af8fa470dcf185bd2b418af65e9`  
**Post-merge Scaffold:** `35253188344` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 foundation lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Creature payload lifecycle:** COMPLETE / INTEGRATED  
**Current normal work:** NPC payload + local persistence core from current `main`

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

PR #42, #43 and #44 Wave 5 packages are complete/merged. PR #46 integrated the Wave 6 reusable-content persistence foundation. PR #48 `feat: add Wave 6 Creature payload persistence` is also merged as `5762058645ba8af8fa470dcf185bd2b418af65e9`; post-merge Scaffold `35253188344` passed.

## Completed Wave 5 branches

- `wave5/desktop-workbench-shell` — historical implementation ref, PR #42 merged/owner-QA accepted;
- `wave5/campaign-membership-administration-core` — historical implementation ref, PR #43 merged;
- `wave5/desktop-hosted-campaign-administration` — historical implementation ref, PR #44 merged/owner-QA accepted.

Do not resume them for normal implementation.

## Completed Wave 6 branches

- `wave6/reusable-content-persistence` — historical implementation ref, PR #46 merged;
- `wave6/creature-payload-persistence` — historical implementation ref, PR #48 merged.

Do not resume these branches for new work. Their integrated scope is part of `main`.

## Current Wave 6 branch rule

Create a new short-lived outcome-oriented branch from current `main` for the **NPC payload + local persistence core**.

That package should reuse the integrated reusable-content identity/scope/provenance/revision/tombstone spine and, where NPC combat mechanics exist, reuse the Creature/stat-block machinery. It must not require combat mechanics for a valid NPC.

Do not create permanent Player/Server/Desktop/provider-specific silos. Do not pull Wave 7 Manager UI, hosted reusable-content sync, import/export/AI helper work, object storage or provider activation into this bounded package.

## Documentation closure branch

- `docs/wave6-creature-integrated` — short-lived documentation/checkpoint branch recording PR #48 integration and the NPC continuation point. Merge it after its own normal validation, then treat it as historical.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state; current `main`, `LATEST` and the current checkpoint control.

## Accidental refs

During 2026-09-17 reconstruction:

- `__noop_should_not_create__` was verified with no unique project work and no PR;
- `__should_not_create__` was verified with no unique project work and no PR.

They are non-authoritative. Safe deletion is optional housekeeping and not a development blocker. Do not delete legitimate historical implementation branches merely because they are inactive.

## Exact resume rule

After the Creature documentation closure is merged, resume from current `main` and `docs/checkpoints/2026-09-17_WAVE6_CREATURE_PAYLOAD_INTEGRATED.md`. Create a new short-lived Wave 6 NPC payload branch and continue the local/shared persistence architecture. Do not redeploy Cloudflare for documentation or local/shared persistence-only changes.
