# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 6 NPC merge:** `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`  
**Post-merge Scaffold:** `35254527744` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 foundation lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Creature payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 NPC payload lifecycle:** COMPLETE / INTEGRATED  
**Current normal work:** lightweight Homebrew/Rule payload + local persistence core from current `main`

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

PR #46 integrated the Wave 6 reusable-content persistence foundation. PR #48 integrated Creature payload persistence. PR #51 `feat: add Wave 6 NPC payload persistence` is merged as `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`; post-merge Scaffold `35254527744` passed.

## Completed Wave 5 branches

- `wave5/desktop-workbench-shell` — historical implementation ref, PR #42 merged/owner-QA accepted;
- `wave5/campaign-membership-administration-core` — historical implementation ref, PR #43 merged;
- `wave5/desktop-hosted-campaign-administration` — historical implementation ref, PR #44 merged/owner-QA accepted.

Do not resume them for normal implementation.

## Completed Wave 6 branches

- `wave6/reusable-content-persistence` — historical implementation ref, PR #46 merged;
- `wave6/creature-payload-persistence` — historical implementation ref, PR #48 merged;
- `wave6/npc-payload-persistence` — historical implementation ref, PR #51 merged.

Do not resume these branches for new work. Their integrated scope is part of `main`.

## Current Wave 6 branch rule

After the NPC documentation closure is merged, create a new short-lived outcome-oriented branch from current `main` for the **lightweight Homebrew/Rule payload + local persistence core**.

Use `ReusableContentFamily.HOMEBREW_RULE`. Keep the first payload self-contained around rule/ruling/custom-system content with summary/body, optional category/rationale, examples, related references, tags and `DRAFT / ACTIVE / RETIRED` lifecycle.

Do not treat structured races/classes/subclasses/backgrounds/feats/spells/items as generic rule text merely to fit this package. Do not introduce Place/Zone/Encounter relationship graphs here.

Do not create permanent Player/Server/Desktop/provider-specific silos. Do not pull Wave 7 Manager UI, hosted reusable-content sync, import/export/AI helper work, object storage or provider activation into this bounded package.

## Documentation closure branches

- `docs/wave6-creature-integrated` — historical after PR #50 merge;
- `docs/wave6-npc-integrated` — current short-lived documentation/checkpoint branch recording PR #51 integration and the Homebrew/Rule continuation point.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state; current `main`, `LATEST` and the current checkpoint control.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Exact resume rule

After the NPC documentation closure is merged, resume from current `main` and `docs/checkpoints/2026-09-17_WAVE6_NPC_PAYLOAD_INTEGRATED.md`. Create a new short-lived Wave 6 Homebrew/Rule payload branch and continue the local/shared persistence architecture. Do not redeploy Cloudflare for documentation or local/shared persistence-only changes.
