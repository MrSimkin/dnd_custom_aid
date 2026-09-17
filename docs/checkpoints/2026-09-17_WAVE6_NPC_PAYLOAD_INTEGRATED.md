# Checkpoint — Wave 6 NPC payload persistence integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`  
**PR:** #51 — `feat: add Wave 6 NPC payload persistence`  
**Final branch head:** `ce2f956324eedefaf4c4f34d4f4c462b49387eae`  
**Push Scaffold:** `35254216489` — SUCCESS  
**PR Scaffold:** `35254260799` — SUCCESS  
**Post-merge Scaffold:** `35254527744` — SUCCESS

## Milestone status

The bounded Wave 6 NPC reusable-content payload + local persistence core is integrated into `main` on top of the reusable-content foundation and Creature payload package.

Wave 6 remains active. The next bounded technical package is the lightweight Homebrew/Rule payload + local persistence core.

## Integrated NPC semantics

The package preserves the approved D-0072 model:

- Quick NPCs and Developed NPCs are both valid;
- an NPC never requires combat mechanics;
- narrative/detail fields may remain blank without a completion score;
- Developed NPCs can accumulate identity, voice, motivations, values, relationships, history, secrets, knowledge, goals, resources, affiliations, places, scene links and DM guidance incrementally;
- optional full combat mechanics reuse the existing `CreaturePayload` model rather than defining a second incompatible stat-block model.

The optional Creature mechanics are serialized inside the NPC payload instead of creating a hidden second reusable Creature object. This preserves a self-contained NPC copy and avoids introducing cross-object dependency-copy semantics before Encounter/relationship work actually requires them.

## Integrated persistence scope

PR #51 adds:

- `NpcPayload` and `NpcContent` domain types;
- Personal and Campaign NPC creation/read flows;
- explicit Personal -> Campaign independent copy with retained provenance;
- source/copy independence after copy;
- payload mutation through the existing optimistic revision/stale-write/tombstone semantics;
- SQLDelight `npc_payload` persistence;
- migration `20.sqm`, including safe default backfill for metadata-only NPC rows;
- database-reopen persistence coverage;
- migration coverage preserving existing campaign data;
- stale-write and deleted-object non-resurrection tests;
- a bounded Desktop legacy-fixture update so the synthetic pre-Wave-6 database removes both `npc_payload` and `creature_payload` before migration.

## Verification evidence

The implementation was intentionally committed as one atomic package at `ce2f956324eedefaf4c4f34d4f4c462b49387eae`.

Validation evidence:

- push Scaffold `35254216489` — SUCCESS;
- pull-request Scaffold `35254260799` — SUCCESS;
- PR #51 merged as `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`;
- post-merge Scaffold `35254527744` — SUCCESS, including backend, hosted-database, Kotlin build/tests and Android debug APK upload.

No provider/deployment action was required.

## Preserved architecture boundaries

This package does **not** introduce:

- NPC Manager/Creator UI;
- import/export;
- AI NPC helper flows;
- hosted reusable-content synchronization;
- object-storage/provider activation;
- generalized dependency graphs;
- a universal payload abstraction.

Those remain later packages/waves unless newer integrated authority changes the plan.

## Next bounded package — lightweight Homebrew/Rule payload core

Proceed with a self-contained **Homebrew/Rule payload + local persistence core** using `ReusableContentFamily.HOMEBREW_RULE`.

The first bounded payload should implement the lightweight rule/ruling/custom-system record explicitly approved in D-0072, including:

- summary;
- rich/human-readable body text;
- optional category;
- optional rationale;
- examples;
- related references;
- tags;
- simple lifecycle `DRAFT / ACTIVE / RETIRED`;
- optional notes where useful.

Personal/Campaign create/read/copy/update/tombstone behavior should reuse the same integrated reusable-content identity/provenance/revision seams.

This package should **not** attempt to model every structured homebrew family (race, class, subclass, background, feat, spell, item, magic item) at once. Those require family-appropriate structure and should remain separate bounded packages/decisions rather than being collapsed into one universal JSON record.

### Sequencing rationale

Homebrew/Rule is the next low-dependency domain because the lightweight rule record is independently useful and does not require the reference/dependency graph that Places/Zones/Encounters will eventually need. Place/Zone and especially Encounter should follow when relationship/dependency semantics are deliberately introduced rather than smuggled into an unrelated payload package.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required for this package. The hard external-service operating budget remains USD $0.

## Continuation rule

Resume from current `main` after this documentation closure is integrated. Create a short-lived outcome branch for the lightweight Homebrew/Rule payload + local persistence core. Preserve domain-specific payloads and avoid a universal executable-content model.
