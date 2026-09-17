# Branch status and repository-ordering map

**Updated:** 2026-09-17 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified Wave 6 Homebrew/Rule merge:** `fc870fe8303b0f9925c479bdc21c388ef5cc8450`  
**Post-merge Scaffold:** `35256531651` — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 foundation lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Creature payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 NPC payload lifecycle:** COMPLETE / INTEGRATED  
**Wave 6 Homebrew/Rule payload lifecycle:** COMPLETE / INTEGRATED  
**Current normal work:** Place payload + local persistence core from current `main`

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

PR #46 integrated the Wave 6 reusable-content persistence foundation. PR #48 integrated Creature payload persistence. PR #51 integrated NPC payload persistence. PR #53 `feat: add lightweight Homebrew Rule payload persistence` is merged as `fc870fe8303b0f9925c479bdc21c388ef5cc8450`; post-merge Scaffold `35256531651` passed.

## Completed Wave 5 branches

- `wave5/desktop-workbench-shell` — historical implementation ref, PR #42 merged/owner-QA accepted;
- `wave5/campaign-membership-administration-core` — historical implementation ref, PR #43 merged;
- `wave5/desktop-hosted-campaign-administration` — historical implementation ref, PR #44 merged/owner-QA accepted.

Do not resume them for normal implementation.

## Completed Wave 6 branches

- `wave6/reusable-content-persistence` — historical implementation ref, PR #46 merged;
- `wave6/creature-payload-persistence` — historical implementation ref, PR #48 merged;
- `wave6/npc-payload-persistence` — historical implementation ref, PR #51 merged;
- `wave6/homebrew-rule-payload-persistence` — historical implementation ref, PR #53 merged.

Do not resume these branches for new work. Their integrated scope is part of `main`.

## Current Wave 6 branch rule

After the Homebrew/Rule documentation closure is merged, create a new short-lived outcome-oriented branch from current `main` for the **Place payload + local persistence core**.

Use `ReusableContentFamily.PLACE`. The first Place payload should establish canonical reusable Place data while preserving the approved rule that a Shop is a specialized Place, not a separate top-level content family/system.

Keep this first package self-contained around human-facing retrieval/presentation data. Exact low-level fields are delegated engineering work, but useful bounded content includes summary, area/geographic context, function, presentation text, services/interactives, hooks, player-safe text, DM-only notes, paper references, tags and a simple Place kind supporting ordinary Places and Shops.

Do not introduce generalized Place <-> NPC/Scene/Zone/Encounter dependency-copy graphs, clocks, media/object-storage references, automatic reveal/publication behavior or Wave 7 Manager UI in this first Place package.

Do not create permanent Player/Server/Desktop/provider-specific silos. Do not pull hosted reusable-content sync, import/export/AI helper work, object storage or provider activation into this bounded package.

## Documentation closure branches

- `docs/wave6-creature-integrated` — historical after PR #50 merge;
- `docs/wave6-npc-integrated` — historical after PR #52 merge;
- `docs/wave6-homebrew-integrated` — current short-lived documentation/checkpoint branch recording PR #53 integration and the Place continuation point.

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state; current `main`, `LATEST` and the current checkpoint control.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Exact resume rule

After the Homebrew/Rule documentation closure is merged, resume from current `main` and `docs/checkpoints/2026-09-17_WAVE6_HOMEBREW_RULE_PAYLOAD_INTEGRATED.md`. Create a new short-lived Wave 6 Place payload branch and continue the local/shared persistence architecture. Do not redeploy Cloudflare for documentation or local/shared persistence-only changes.
