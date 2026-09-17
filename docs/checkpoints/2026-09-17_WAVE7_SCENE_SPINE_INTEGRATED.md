# Checkpoint — Wave 7 Adventure/Scene Spine integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `a84a8857102806f8a9ac588545167d697ea8a311`  
**PR:** #71 — `feat: add lightweight Adventure Scene Spine`  
**Final implementation head:** `face568e7985125975731fef5e275e333ef79b9d`  
**Push Scaffold:** `35281913166` — SUCCESS  
**PR Scaffold:** `35282188319` — SUCCESS  
**Post-merge Scaffold:** `35282483851` — SUCCESS

## Milestone status

The lightweight **Adventure/Scene Spine** is integrated into `main` as the sixth concrete Wave 7 authoring package.

Scene is now a real reusable-content family with local persistence and a minimal Desktop authoring surface. The implementation deliberately remains an orientation aid rather than a quest engine.

The next bounded Wave 7 package is **Desktop Dungeon/Zone Manager — local Zone Brief authoring core**.

## Integrated Scene behavior

Scene records support:

- title/display name;
- purpose;
- possible next Scene cues;
- preparation references/links;
- DM notes;
- Personal and active-Campaign browse/search/create/open/edit;
- explicit Personal -> Campaign independent copy;
- scope, provenance and revision visibility;
- atomic title + Scene payload update under one optimistic revision.

`possibleNextScenes` and preparation references are intentionally stored as human-readable strings in this first slice. They orient the DM without creating executable quest-state transitions, recursive copy/remap behavior or a generalized dependency graph.

## Persistence and migration

The package adds:

- `ReusableContentFamily.SCENE`;
- `ScenePayload` / `SceneContent`;
- `SceneContentRepository`;
- SQLDelight `scene_payload` storage;
- migration `25.sqm`, including safe backfill for any metadata-only SCENE rows;
- Desktop Adventure/Scenes authoring alongside the existing Stage Place/Shop surface.

The generic `reusable_content` table did not need to change because it was already family-agnostic.

The legacy-unversioned Desktop migration fixture was updated to drop the newly current `scene_payload` table before reconstructing the verified pre-Wave-6 schema. The production legacy normalization code itself remained unchanged and family-agnostic.

## Preserved semantics

Scene uses the same proven reusable-content guarantees as the earlier families:

- stable object identity;
- Personal/Campaign scope;
- explicit Personal -> Campaign copy with a new independent object ID;
- retained provenance;
- no automatic inheritance after copy;
- optimistic revisions and stale-write rejection;
- tombstones/non-resurrection;
- conservative locally resolvable DM identity for Personal Desktop authoring.

## Validation evidence

Focused coverage verifies:

- Scene round-trip persistence;
- SQLDelight migration `25.sqm` backfill;
- database reopen persistence;
- atomic rename + payload mutation;
- stale-write rejection;
- explicit Personal -> Campaign copy and copy independence;
- tombstone/non-resurrection;
- Desktop controller authoring/copy behavior;
- continued legacy-unversioned Desktop migration behavior.

Exact evidence:

- implementation head `face568e7985125975731fef5e275e333ef79b9d`;
- push Scaffold `35281913166` — SUCCESS;
- PR Scaffold `35282188319` — SUCCESS;
- PR #71 merged as `a84a8857102806f8a9ac588545167d697ea8a311`;
- post-merge Scaffold `35282483851` — SUCCESS, including backend, hosted database, Kotlin/shared/Desktop tests, Android debug assembly and APK upload.

## Preserved boundaries

This Scene slice does **not** add:

- quest-state execution or automatic narrative transitions;
- clocks/readiness state;
- recursive Scene dependency copy/remap;
- generalized dependency graphs;
- media/object storage;
- hosted reusable-content synchronization;
- live Stage/Dungeon/Combat Desk behavior;
- provider/deployment changes.

## Next package — Desktop Dungeon/Zone Manager

The next bounded package is **Desktop Dungeon/Zone Manager — local Zone Brief authoring core**.

D-0072 places Dungeon/Zone immediately after Stage/Scene preparation. Existing Wave 6 Zone persistence already provides a rich payload:

- summary and area;
- presentation;
- space;
- exploration;
- interactives;
- clues;
- checks;
- consequences;
- encounter brief;
- DM guidance;
- player-safe text;
- paper references;
- tags.

The first Manager slice should therefore remain migration-free unless implementation evidence proves otherwise.

Expected bounded work:

- Personal + active-Campaign Zone browse/search/create/open/edit;
- editor framing that preserves the approved Zone Brief groupings **PRESENTAR / INTERACTUAR / ENCUENTRO** while exposing the richer existing fields;
- area/tag/search retrieval appropriate to Dungeon preparation;
- explicit Personal -> Campaign independent copy with provenance;
- atomic display-name + Zone payload update under one optimistic revision;
- focused stale-write/copy/tombstone/controller coverage;
- integration into the existing Desktop Managers surface with active Campaign context visible.

The existing `exploration` / `space` fields may carry topology/flow preparation in this bounded slice. Do not invent tactical geometry, VTT maps, a generalized graph framework, automated fictional consequences, clocks, or readiness machinery merely to satisfy the first Manager package. Richer topology tooling can be added when a concrete workflow requires it.

Encounter Manager, PC Manager/Audit, Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## Provider/cost state

No Cloudflare, Descope, Neon deployment or object-storage action was required. Hard external-service budget remains USD $0.
