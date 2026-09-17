# Checkpoint — Wave 6 Zone payload persistence integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `0b73d79e46edb7022ace2a2efd161149d9aefc73`  
**PR:** #57 — `feat: add Wave 6 Zone payload persistence`  
**Final branch head:** `5ea8fe49e770557b08418ff6dbf68152916c1305`  
**Push Scaffold:** `35262051406` — SUCCESS  
**PR Scaffold:** `35262563057` — SUCCESS  
**Post-merge Scaffold:** `35262999144` — SUCCESS

## Milestone status

The bounded Wave 6 prepared Zone / Zone Brief reusable-content payload + local persistence core is integrated into `main` on top of the reusable-content foundation, Creature, NPC, lightweight Homebrew/Rule and Place payload packages.

Wave 6 remains active. The next bounded implementation package is intentionally selected only after this documentation closure itself is integrated, from current D-0072/D-0073 authority and the now-integrated Zone state.

## Integrated Zone semantics

PR #57 establishes canonical reusable prepared Zone data while preserving D-0072's distinction between authored Area / Zone Brief preparation and live Dungeon Turn movement-zone state.

Integrated payload semantics include:

- reusable-content display name as the canonical Zone name;
- summary;
- area/context;
- presentation/atmosphere;
- space/layout description;
- exploration guidance;
- interactives;
- clues;
- checks;
- consequences;
- encounter brief/orientation;
- DM guidance;
- player-safe text;
- paper references;
- tags.

The payload remains human-facing and self-contained. It supports the prepared Zone Brief orientation around presentation, interaction and encounter use without introducing a generalized graph or live-state model.

## Integrated persistence scope

PR #57 adds:

- `ZonePayload` and `ZoneContent` domain types;
- Personal and Campaign create/read flows;
- explicit Personal -> Campaign independent copy with retained provenance;
- source/copy independence after copy;
- payload mutation through existing optimistic revision/stale-write/tombstone semantics;
- SQLDelight `zone_payload` persistence;
- explicit list serialization for bounded list fields;
- migration `23.sqm`, including metadata-only `ZONE` backfill;
- database-reopen, migration, copy, revision and tombstone/non-resurrection coverage;
- the bounded Desktop legacy-fixture adjustment required to simulate the verified pre-Wave-6 schema.

No hosted API, Worker deployment, provider or object-storage change was required.

## Verification evidence

The implementation was committed as one bounded package at `5ea8fe49e770557b08418ff6dbf68152916c1305`.

Validation evidence:

- push Scaffold `35262051406` — SUCCESS;
- pull-request Scaffold `35262563057` — SUCCESS;
- PR #57 merged as `0b73d79e46edb7022ace2a2efd161149d9aefc73`;
- post-merge Scaffold `35262999144` — SUCCESS, including backend, hosted-database, Kotlin build/tests and Android debug APK upload.

## Preserved architecture boundaries

This package does **not** introduce:

- Dungeon/Zone Manager UI;
- generalized Place/NPC/Zone/Encounter relationship or dependency-copy graphs;
- Encounter dependency-copy machinery;
- live Dungeon Turn movement-zone state;
- clocks or advisory-trigger execution;
- media/object-storage references;
- hosted reusable-content synchronization;
- import/export or AI helpers;
- provider activation;
- a universal arbitrary/executable content payload abstraction.

Those concerns remain deliberately deferred until a concrete dependent domain or later wave requires them.

## Next bounded Wave 6 selection

After this documentation closure is integrated, re-read the current D-0072/D-0073 authority and select the next dependency-safe bounded Wave 6 package from the integrated state. Encounter is now the remaining reserved rich reusable family, but its exact first bounded package must be derived from current authority rather than assumed to require the full future relationship/dependency graph at once.

No next implementation branch is created by this checkpoint itself.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required for this documentation closure. The hard external-service operating budget remains USD $0.

## Continuation rule

After this documentation closure is integrated, resume from current `main`, select the next bounded dependency-safe Wave 6 package, and continue autonomously through routine safe engineering gates under the coherent-task continuation rule. Preserve Personal/Campaign independent-copy semantics, stable identity, revisions, tombstones and provenance; do not introduce generalized relationship infrastructure or provider work without a concrete requirement.
