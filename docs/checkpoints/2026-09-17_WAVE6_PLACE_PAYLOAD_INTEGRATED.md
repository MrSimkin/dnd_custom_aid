# Checkpoint — Wave 6 Place payload persistence integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `d978a4191054227a03b32ecca3e7ceadc5d6e869`  
**PR:** #55 — `feat: add Wave 6 Place payload persistence`  
**Final branch head:** `99fe6151c253803dd3dffdc6a16fbf6058133c47`  
**Push Scaffold:** `35258393882` — SUCCESS  
**PR Scaffold:** `35258423034` — SUCCESS  
**Post-merge Scaffold:** `35259027937` — SUCCESS

## Milestone status

The bounded Wave 6 Place reusable-content payload + local persistence core is integrated into `main` on top of the reusable-content foundation, Creature, NPC and lightweight Homebrew/Rule payload packages.

Wave 6 remains active. The next bounded implementation package has **not** been selected in this closure; selection is the next explicit planning action after this documentation branch is integrated.

## Integrated Place semantics

PR #55 establishes canonical reusable Place data while preserving D-0072's rule that a Shop is a specialized Place rather than a separate top-level content family/system.

Integrated payload semantics include:

- `PlaceKind.PLACE` for ordinary Places;
- `PlaceKind.SHOP` for Shops as specialized Places;
- summary;
- area/geographic context;
- function/purpose;
- presentation/atmosphere text;
- services;
- interactives;
- hooks;
- player-safe text;
- DM-only notes;
- paper references;
- tags.

The reusable-content display name remains the canonical Place name. The payload is intentionally self-contained and human-facing; it does not embed generalized cross-object graph semantics.

## Integrated persistence scope

PR #55 adds:

- `PlaceKind`, `PlacePayload` and `PlaceContent` domain types;
- Personal and Campaign create/read flows;
- explicit Personal -> Campaign independent copy with retained provenance;
- source/copy independence after copy;
- payload mutation through existing optimistic revision/stale-write/tombstone semantics;
- SQLDelight `place_payload` persistence;
- explicit list serialization for services, interactives, hooks, paper references and tags;
- migration `22.sqm`, including metadata-only Place backfill with ordinary `PLACE` defaults;
- database-reopen, migration, copy, revision and tombstone/non-resurrection coverage;
- the bounded Desktop legacy-fixture adjustment required to simulate the verified pre-Wave-6 schema.

No hosted API, Worker deployment, provider or object-storage change was required.

## Verification evidence

The implementation was committed as one bounded package at `99fe6151c253803dd3dffdc6a16fbf6058133c47`.

Validation evidence:

- push Scaffold `35258393882` — SUCCESS;
- pull-request Scaffold `35258423034` — SUCCESS;
- PR #55 merged as `d978a4191054227a03b32ecca3e7ceadc5d6e869`;
- post-merge Scaffold `35259027937` — SUCCESS, including backend, hosted-database, Kotlin build/tests and Android debug APK upload.

## Preserved architecture boundaries

This package does **not** introduce:

- Stage/Place Manager UI;
- generalized Place <-> NPC/Scene/Zone/Encounter relationship or dependency-copy graphs;
- clocks;
- media/object-storage references;
- automatic reveal/publication behavior;
- hosted reusable-content synchronization;
- import/export or AI helpers;
- provider activation;
- a universal arbitrary/executable content payload abstraction.

Those concerns remain deliberately deferred until a concrete dependent domain or later wave requires them.

## Next bounded Wave 6 selection

Do not choose a new implementation package merely from branch chronology. After this documentation closure is integrated, re-read the current D-0072/D-0073 authority and select the next dependency-safe bounded Wave 6 package from the integrated state.

No next implementation branch has been created by this closure.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required for this documentation closure. The hard external-service operating budget remains USD $0.

## Continuation rule

After this documentation closure is integrated, resume from current `main`, review the current Wave 6 authority, and select one bounded dependency-safe continuation package. Preserve Personal/Campaign independent-copy semantics, stable identity, revisions, tombstones and provenance; do not introduce generalized relationship infrastructure or provider work without a concrete requirement.
