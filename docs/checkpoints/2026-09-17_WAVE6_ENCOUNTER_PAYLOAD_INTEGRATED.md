# Checkpoint — Wave 6 Encounter payload persistence integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd`  
**PR:** #59 — `feat: add Wave 6 Encounter payload persistence`  
**Final implementation head:** `cc4e339775b2b34920a46d581bebe83759570a45`  
**Push Scaffold:** `35264310753` — SUCCESS  
**PR Scaffold:** `35264620723` — SUCCESS  
**Post-merge Scaffold:** `35265162945` — SUCCESS

## Milestone status

The bounded reusable Encounter payload + local persistence core is integrated into `main` on top of the Wave 6 reusable-content foundation and the Creature, NPC, lightweight Homebrew/Rule, Place and Zone payload packages.

This completes the **core Wave 6 reusable-content architecture milestone** required to begin visible Desktop authoring Managers. Later concrete Manager packages may still add family-specific supporting data where required; this checkpoint does not claim that every future Scene, clock, media, structured Homebrew or live-state concept has already been modeled.

The next engineering wave is **Wave 7 — Desktop authoring Managers**. The first selected package is the **Desktop Creature/Monster Manager local authoring core**.

## Integrated Encounter semantics

PR #59 adds saved/prepared Encounters distinct from live combat state.

The payload includes:

- summary;
- environment;
- context;
- DM guidance;
- participant entries;
- tags;
- notes.

Participant entries support:

- optional reusable Creature/NPC `sourceContentId`;
- freeform/ad-hoc participants when no reusable dependency exists;
- label and positive quantity;
- readiness `EXPECTED / RESERVE / CONDITIONAL`;
- optional condition text;
- encounter-local overrides;
- notes.

Referenced dependencies must be active Creature/NPC content in the same Personal or Campaign scope as the Encounter.

## Personal -> Campaign dependency semantics

A Personal Encounter copied into a Campaign:

1. validates its referenced Personal Creature/NPC dependencies;
2. copies each unique referenced dependency exactly once into the Campaign using the existing domain repositories;
3. remaps Encounter participant references to those new Campaign object IDs;
4. creates a new independent Campaign Encounter ID;
5. retains provenance;
6. leaves all Personal masters independent after copy.

No generalized dependency graph was introduced.

## Persistence and validation

The package adds:

- `EncounterPayload`, `EncounterParticipant`, readiness and content types;
- `EncounterContentRepository`;
- Personal/Campaign create/read/update/tombstone behavior;
- dependency scope/family validation;
- domain-specific Personal -> Campaign dependency copy/remap/deduplication;
- SQLDelight `encounter_payload` persistence;
- migration `24.sqm` with metadata-only `ENCOUNTER` backfill;
- participant/tag JSON serialization;
- migration, reopen, revision, stale-write, tombstone/non-resurrection and dependency-copy tests;
- the bounded synthetic legacy Desktop fixture adjustment.

Validation evidence:

- push Scaffold `35264310753` — SUCCESS;
- PR Scaffold `35264620723` — SUCCESS;
- PR #59 merged as `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd`;
- post-merge Scaffold `35265162945` — SUCCESS, including backend, hosted database, Kotlin build/tests and Android debug APK upload.

## Preserved boundaries

This milestone does **not** add:

- live combat/encounter runtime state;
- generalized Place/Zone/rule/clock/handout relationship graphs;
- automatic encounter-balance authority or simulation;
- clocks/advisory-trigger execution;
- archive/live lifecycle machinery beyond the saved reusable Encounter record;
- Manager UI;
- hosted reusable-content synchronization;
- import/export or AI helpers;
- object-storage/provider work;
- a universal arbitrary/executable payload abstraction.

Those concerns stay attached to concrete later Manager/live packages rather than being generalized prematurely.

## Wave 6 -> Wave 7 transition

Current D-0072/D-0073 authority defines Wave 7 as Desktop authoring Managers on top of Wave 6 persistent foundations. The reserved reusable families now all have a local persistence core: Creature, NPC, Homebrew/Rule, Place, Zone and Encounter.

Accordingly, do not extend Wave 6 merely to pre-model all later product concepts. Add further Scene, clocks/readiness, media or structured Homebrew foundations when their approved Manager package concretely needs them.

The first selected Wave 7 package is:

**Desktop Creature/Monster Manager — local authoring core**

Initial bounded scope:

- activate the existing Desktop `MANAGERS` destination;
- browse/search Personal and active-Campaign Creature records;
- create/open/edit the existing human-complete Creature payload;
- show scope and provenance;
- explicitly copy Personal Creature -> active Campaign;
- preserve existing revision/tombstone semantics.

Deferred from that first slice: Official/SRD catalog browsing, import/export, Creature Creator Assistant, media attachments, hosted reusable-content sync and broad Manager framework generalization.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required by this milestone or documentation closure. Hard external-service budget remains USD $0.
