# 2026-09-10 — DM Desk family consolidation

**Canonical branch:** `main`  
**Scope:** product/design discovery only  
**Implementation:** none  
**DM implementation gate:** still BLOCKED until Phase 4A is accepted and explicitly closed

## Purpose

This checkpoint records the close of the broad DM Desk-discovery pass and points to the approved durable decision in:

`docs/decisions/D-0069_DM_DESK_FAMILY_STAGE_DESK_AND_DM_SCREEN.md`

D-0068 remains controlling for the earlier Workspace/Dungeon/DM Attention Budget discovery. D-0069 refines and extends it.

## Approved Desk family

The currently approved live DM Desk family is:

1. **DM Screen** — neutral DM surface, PC Group/Quick/Full access, Party Lens, DM reference and Quick Rules Question;
2. **Stage Desk** — retrieval/navigation switchboard for the current broad adventure environment: Places, Shops, NPCs, adventure/plot-hook Scene Spine, area/function/search/recent routes;
3. **Dungeon Desk** — dynamic dangerous structured exploration, Zone Briefs, Dungeon Turns, Clocks/threats and encounter readiness;
4. **Combat Desk** — active combat operation plus encounter-wide and creature-specific tactical guidance.

No fifth Desk is currently required.

## DM Screen additions

### Party Lens

Approved as a derived, read-only, zero-maintenance party view. It may answer questions such as highest passive values, senses/darkvision, languages, proficiencies, speeds, AC and other useful comparative facts already known from PC sheets. Exact fields remain for later live-use refinement.

### Quick Rules Question — explicitly preserved

Quick Rules Question is an approved capability/placeholder and must not be forgotten in future DM work.

It is an AI-chatbox-like natural-language rules clarification tool. The intended broader experience is to ask against:

- supported official SRD 5.1 / SRD 5.2.1 material;
- DM-authored house rules;
- custom rules;
- homebrew;
- adopted variant rules/overrides.

The existing MVP boundary remains official-SRD-only under D-0041 / PRODUCT. Non-SRD rules are later maintained through the Desktop DM Manager rather than authored as live tablet bookkeeping. Future mixed-source answers must distinguish official SRD baseline from campaign/custom overrides.

The project may ingest/store its own supported open SRD corpus; this is not an attempt to replace D&D Beyond or reproduce closed rulebook content.

## Stage Desk contract

`Stage Desk` replaces the weaker Town/Hub working terminology.

Its controlling job is not to digitize the campaign or replace the owner's paper adventure aid. It is a fast multi-route retrieval companion: get the DM to the needed thing immediately by name/search, area, function/category, narrative context or recent/open/pinned context.

Core Stage concepts:

- one canonical Place, projected By Area / By Function / search / narrative links;
- area-navigation view supports following the party through the broad environment without requiring a cartographic map;
- opening another Place does not move the party in fiction;
- Shop is a specialized Place, not inventory-management software;
- Places link to NPCs; they do not own NPCs;
- Developed NPC ficha remains the rich source; Stage may project a compact `Run NPC` / `How to run` view;
- adventure/plot-hook **Scene Spine** preserves order/schema/branches and links without requiring full adventure migration;
- partial digital representation and paper references are valid by design;
- future few-click improvised NPC creation is valued but deferred, using the same dirty-live → keep → Manager-cleanup philosophy as creature improv.

## Dungeon/Combat refinements captured

D-0069 also records the later refinements agreed after D-0068:

- prepared dungeon areas are not structurally identical to Dungeon Turn movement zones;
- areas and encounters are not restricted 1:1;
- open dungeon areas behave like papers on the DM table;
- current Zone Brief alpha direction is `Presentar / Interactuar / Encuentro`;
- `Pruebas relevantes` + `Chequeos útiles` combine into one investigation/checks grouping;
- encounter-wide `How to run` guidance is projected into Combat Desk rather than independently duplicated;
- creature-specific tactical guidance may surface when that creature acts;
- Combat Desk is both tracker and tactical reminder assistant.

## Journey direction

Journey is a future candidate only. Because the owner currently runs travel as fast travel or roughly linear dungeon-like progression with possible/random encounters (including no event), future travel support should first be tested as a special Dungeon Desk mode/profile. A separate Journey Desk must earn existence through real distinct operational requirements.

## Exact continuation

The broad Desk-taxonomy question is now parked. Do not restart generic `what Desks do we need?` discovery by default.

Current technical execution remains **Increment F — Conjuros compact source-context redesign** under Phase 4A.

If DM product discovery resumes before Phase 4A QA becomes available, continue from a concrete second-layer slice such as Stage search/navigation ergonomics, Place/Shop/NPC live projections, Scene Spine minimum structure, Quick Rules Question interaction/source presentation, Party Lens content, or detailed Combat Desk UX.
