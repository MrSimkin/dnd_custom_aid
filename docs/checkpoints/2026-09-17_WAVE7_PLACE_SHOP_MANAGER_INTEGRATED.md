# Checkpoint — Wave 7 Desktop Place/Shop Manager integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`  
**PR:** #67 — `feat: add Desktop Place and Shop Manager authoring core`  
**Final implementation head:** `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`  
**Push Scaffold:** `35278740631` — SUCCESS  
**PR Scaffold:** `35279052405` — SUCCESS  
**Post-merge Scaffold:** `35279329344` — SUCCESS

## Milestone status

The fourth concrete **Wave 7 — Desktop authoring Managers** package is integrated into `main` on top of the completed Wave 6 reusable-content architecture and the integrated Creature/Monster, NPC and lightweight Homebrew/Rules Managers.

The Desktop `MANAGERS` destination now exposes concrete Creature/Monster, NPC, Homebrew/Rules and Place/Shop local authoring surfaces without introducing a generalized all-domain Manager framework.

The next bounded Wave 7 package is **Desktop Stage Manager — Place retrieval/organization core**. Scene Spine remains a later separate package.

## Integrated visible behavior

The Place/Shop Manager supports:

- browsing/searching Personal Places and Places in the active Campaign;
- creating Personal or active-Campaign content using the existing `PLACE` / `SHOP` specialization;
- opening/editing the full integrated Place payload: summary, area, function, presentation, services, interactives, hooks, player-safe text, DM notes, paper references and tags;
- viewing scope, provenance and optimistic revision information;
- explicit Personal -> active Campaign copy as a new independent Campaign object;
- keeping active Campaign context visible while authoring.

Campaign copies remain independent after copy. Later edits to the Personal master do not automatically alter the Campaign copy.

## Identity and revision semantics

Personal Place authoring uses the same conservative identity rule as the preceding Managers: Desktop only uses a uniquely resolvable locally persisted active DM account. It does not invent or hard-code an owner identity.

If a unique local DM identity cannot be resolved, Personal creation is disabled rather than guessed; Campaign-local authoring remains available when an active local Campaign exists.

Place display name and payload are saved atomically through one optimistic revision mutation. Stale or deleted content cannot be silently overwritten or resurrected.

## Persistence and validation

This package reuses the integrated Wave 6 `PlacePayload` / `PlaceContentRepository`; no database schema or migration was required.

The implementation adds:

- a concrete Place/Shop surface inside the existing Desktop Managers destination;
- a Desktop Place controller for Personal/Campaign browse, create, edit and copy flows;
- a bounded repository update operation that updates display name + Place payload under one revision;
- focused coverage for Shop creation, full payload persistence, atomic rename + payload edit, stale-write rejection, provenance/copy independence and deleted-write non-resurrection.

Validation evidence:

- implementation head `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`;
- push Scaffold `35278740631` — SUCCESS;
- PR Scaffold `35279052405` — SUCCESS;
- PR #67 merged as `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`;
- post-merge Scaffold `35279329344` — SUCCESS, including backend, hosted database, Kotlin build/tests and Android debug APK upload.

## Preserved boundaries

This Place/Shop slice does **not** add:

- Scene Spine persistence or a quest engine;
- Dungeon/Zone, Encounter, PC Manager/Audit or Media/Handouts authoring;
- richer structured Homebrew families;
- hosted reusable-content synchronization;
- object-storage/provider work;
- generalized all-domain Manager abstractions;
- live DM Desk/runtime behavior.

Shops remain specialized Places rather than a parallel persistence family.

## Next package — Desktop Stage Manager

The next bounded package is **Desktop Stage Manager — Place retrieval/organization core**.

It should build on the now-integrated Place/Shop Manager and existing Place payload rather than adding another persistent Stage object. The current Place model already carries the fields needed for a useful first Stage organization surface: kind, area, function, summary, presentation, services/interactives, hooks, notes, references and tags; reusable-content metadata already provides scope and updated timestamps.

Expected first slice:

- present Places/Shops as the Stage preparation collection rather than creating duplicate Stage records;
- provide richer retrieval/filter/grouping over existing Place data, especially kind, area, function, tags, scope and recent updates;
- preserve existing Place create/open/edit/copy semantics and revision guarantees;
- keep active Campaign context visible;
- add focused controller/UI-state coverage for retrieval/filtering and stable selection where needed;
- make only the smallest refactor required to share existing Place behavior.

Do **not** introduce Scene Spine in this Stage package. D-0072's lightweight Adventure/Scene Spine remains the following concrete package; source inspection confirms there is currently no Scene reusable-content family, so that later package can justify and own its minimal persistence extension explicitly.

Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required by this milestone or its documentation closure. Hard external-service budget remains USD $0.
