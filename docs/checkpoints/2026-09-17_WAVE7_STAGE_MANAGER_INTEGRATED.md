# Checkpoint — Wave 7 Desktop Stage Manager integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `a99f03bf53637494695cc39b39d077ea1ef61ada`  
**PR:** #69 — `feat: add Desktop Stage place retrieval core`  
**Final implementation head:** `a01eb18a8385807d973c9f2059ff32779c0f6897`  
**Push Scaffold:** `35280359257` — SUCCESS  
**PR Scaffold:** `35280486923` — SUCCESS  
**Post-merge Scaffold:** `35280636549` — SUCCESS

## Milestone status

The fifth concrete **Wave 7 — Desktop authoring Managers** package is integrated into `main`.

The existing Place/Shop Manager now doubles as the first Stage preparation surface. This package intentionally organizes the existing persisted Place family instead of introducing duplicate Stage records or a parallel Stage persistence model.

The next bounded Wave 7 package is **Adventure/Scene Spine — lightweight local core**.

## Integrated Stage behavior

The Stage preparation surface adds richer retrieval over existing Places/Shops:

- kind filter: all / Place / Shop;
- scope filter: all / Personal / active Campaign;
- dedicated area, function and tag filters;
- full-text search across the existing Place fields;
- deterministic ordering by Name, Area or Recent update;
- preserved Personal-first name ordering where applicable;
- visible Stage framing inside the existing Places/Shops authoring surface.

The existing Place/Shop create/open/edit/copy behavior remains in place. The package did not replace the editor or persistence layer.

## Preserved persistence semantics

This package reuses the integrated Wave 6 Place persistence and the Wave 7 Place/Shop Manager without schema or repository changes.

Preserved guarantees include:

- Personal and Campaign-local scope;
- explicit Personal -> Campaign independent copy;
- provenance visibility;
- atomic display-name + Place payload saves;
- optimistic revisions and stale-write rejection;
- tombstones/non-resurrection;
- conservative local DM identity resolution for Personal authoring.

## Implementation and validation

The implementation is intentionally small:

- existing `DesktopPlaceManager.kt` receives the Stage framing and delegates retrieval to the new pure filter function;
- `DesktopStagePlaceView.kt` contains Stage filter/sort state plus the visible filter controls;
- `DesktopStagePlaceViewTest.kt` verifies combined kind/area/tag filtering, Campaign/function filtering, full-text hook retrieval, recent ordering and area ordering.

Validation evidence:

- implementation head `a01eb18a8385807d973c9f2059ff32779c0f6897`;
- push Scaffold `35280359257` — SUCCESS;
- PR Scaffold `35280486923` — SUCCESS;
- PR #69 merged as `a99f03bf53637494695cc39b39d077ea1ef61ada`;
- post-merge Scaffold `35280636549` — SUCCESS, including backend, hosted database, Kotlin build/tests and Android debug APK upload.

## Preserved boundaries

This Stage slice does **not** add:

- a Stage persistence family or Stage schema;
- Scene Spine persistence;
- a quest engine;
- Dungeon/Zone, Encounter, PC Manager/Audit or Media/Handouts authoring;
- hosted reusable-content synchronization;
- object-storage/provider work;
- generalized all-domain Manager abstractions;
- live DM Desk/runtime behavior.

## Next package — Adventure/Scene Spine

The next bounded package is **Adventure/Scene Spine — lightweight local core**.

Source inspection confirms there is currently no hidden Scene implementation to expose:

- `ReusableContentFamily` has Creature, NPC, Homebrew/Rule, Place, Zone and Encounter, but no Scene family;
- the shared spine package contains generic identity/revision/scope primitives rather than a Scene domain model;
- there is no Scene payload repository or Scene SQL schema;
- current SQLDelight migrations run through `24.sqm` (Encounter);
- the generic `reusable_content` table is already family-agnostic.

The Scene package should therefore own any minimal persistence extension explicitly rather than pre-modeling it elsewhere.

Keep the model lightweight and orientation-focused, consistent with D-0072:

- title/display name;
- purpose/summary;
- possible next Scenes;
- lightweight references needed for preparation/navigation;
- Personal and Campaign scope using existing reusable-content semantics;
- explicit Personal -> Campaign independent copy;
- optimistic revisions, stale-write rejection and tombstone/non-resurrection behavior;
- a minimal Desktop authoring/browse surface with focused persistence/migration tests.

Do **not** turn Scene into a quest engine. Do not introduce clocks, media storage, generalized dependency graphs, live-state orchestration or broad cross-domain refactors unless concrete evidence in this package requires them.

Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## Provider/cost state

No Cloudflare, Descope, Neon deployment or object-storage action is required by this milestone or its documentation closure. Hard external-service budget remains USD $0.
