# Checkpoint — Wave 6 Creature payload persistence integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `5762058645ba8af8fa470dcf185bd2b418af65e9`  
**PR:** #48 — `feat: add Wave 6 Creature payload persistence`  
**Final branch head:** `8a9ec6425c697472a6c57982acbe8e4001b4fc5d`  
**Replacement PR Scaffold:** `35252554882` — SUCCESS  
**Post-merge Scaffold:** `35253188344` — SUCCESS

## Milestone status

The bounded Wave 6 Creature reusable-content payload + local persistence package is integrated into `main` on top of the reusable-content foundation from PR #46.

Wave 6 remains active. The next bounded package is the NPC payload + local persistence core; this is a technical sequencing decision within the already-approved Wave 6 scope and D-0072/D-0073 delegation.

## Integrated scope

PR #48 adds a Creature-specific human-complete, selectively structured payload rather than a universal executable content model. It includes:

- Creature payload/domain representation linked to the existing reusable-content object identity;
- local SQLDelight persistence for Creature payloads;
- Personal and Campaign Creature creation/read flows through the existing reusable-content foundation;
- explicit Personal -> Campaign independent copy with retained provenance;
- payload mutation using the existing optimistic revision/stale-write/tombstone semantics;
- migration `19.sqm`, including safe backfill behavior for pre-existing metadata-only Creature rows;
- focused Creature persistence/invariant tests;
- a bounded Desktop legacy-migration fixture correction required because the synthetic Wave-5 fixture initially left the new `creature_payload` table behind while simulating the old schema.

The fixture repair did **not** change production migration semantics.

## Verification evidence

The original PR Scaffold `35246485381` failed only in the synthetic legacy Desktop migration test. The Kotlin failure was traced to the fixture constructing the latest schema, dropping `reusable_content`, but leaving the new `creature_payload` table in place before migration `19.sqm` attempted to create it.

Repair commit `8a9ec6425c697472a6c57982acbe8e4001b4fc5d` makes that fixture drop `creature_payload` before `reusable_content`, accurately representing the pre-Wave-6 schema being simulated.

Replacement PR Scaffold `35252554882` passed. PR #48 then merged as `5762058645ba8af8fa470dcf185bd2b418af65e9`, and post-merge Scaffold `35253188344` also passed.

## Preserved architecture boundaries

This package does not introduce:

- large Monster Manager / Creature Creator UI;
- hosted reusable-content synchronization;
- object-storage/provider activation;
- import/export;
- assistant/SRD comparison features;
- encounter dependency graphs;
- a universal executable content payload abstraction.

Those remain later packages/waves unless a newer integrated checkpoint supersedes this state.

## Next bounded package

Proceed with **NPC payload + local persistence core** from current `main`.

The NPC model must preserve the approved D-0072 semantics:

- Quick NPC and Developed NPC are both valid;
- an NPC does not require a combat stat block;
- richer identity/personality/motivation/context fields may be added without forcing completeness scoring;
- optional combat mechanics should reuse the Creature/stat-block machinery rather than creating a second incompatible combat model;
- Personal -> Campaign copies remain independent after explicit copy/use, with retained provenance;
- Manager UI, import/export, hosted sync and AI helpers remain outside this bounded Wave 6 payload package.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage provider action is required for this local/shared persistence work. The hard external-service operating budget remains USD $0.

## Continuation rule

Resume from current `main` and create a short-lived outcome-oriented branch for the NPC payload + local persistence core. Reuse the existing reusable-content and Creature persistence/revision seams; do not rebuild the foundation or generalize all domain payloads into one universal model.
