# Checkpoint — Wave 7 Desktop Homebrew & Rules Manager integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `6febe3f936593999834189b92aeda9d209385fa7`  
**PR:** #65 — `feat: add Desktop Homebrew and Rules Manager core`  
**Final implementation head:** `17f4cd686114f734ab0bc50f453de9d579a52c79`  
**Push Scaffold:** `35272269481` — SUCCESS  
**PR Scaffold:** `35272560206` — SUCCESS  
**Post-merge Scaffold:** `35277359425` — SUCCESS

## Milestone status

The third concrete **Wave 7 — Desktop authoring Managers** package is integrated into `main` on top of the completed Wave 6 reusable-content architecture and the integrated Creature/Monster and NPC Managers.

The existing Desktop `MANAGERS` destination now provides concrete Creature/Monster, NPC and lightweight Homebrew/Rules authoring surfaces without introducing a generalized all-domain Manager framework.

The next bounded Wave 7 package is **Desktop Place/Shop Manager — local authoring core**.

## Integrated visible behavior

The Homebrew & Rules Manager supports:

- browsing/searching Personal lightweight rule records and rule records in the active Campaign;
- creating Personal or active-Campaign rules;
- opening/editing title, summary/body, category, rationale, examples, related references, tags and notes;
- using the existing Draft / Active / Retired lifecycle;
- viewing scope, provenance and optimistic revision information;
- explicit Personal -> active Campaign copy as a new independent Campaign object;
- keeping active Campaign context visible while authoring.

Campaign copies remain independent after copy. Later edits to the Personal master do not automatically alter the Campaign copy.

## Identity and revision semantics

Personal Homebrew/Rule authoring uses the same conservative rule as the Creature and NPC Managers: Desktop only uses a uniquely resolvable locally persisted active DM account. It does not invent or hard-code an owner identity.

If a unique local DM identity cannot be resolved, Personal creation is disabled rather than guessed; Campaign-local authoring remains available when an active local Campaign exists.

Rule display name and payload are saved atomically through one optimistic revision mutation. Stale or deleted content cannot be silently overwritten or resurrected.

## Persistence and validation

This package reuses the integrated Wave 6 `HomebrewRulePayload` / `HomebrewRuleContentRepository`; no database schema or migration was required.

The implementation adds:

- a concrete Homebrew/Rules selector/editor inside the existing Desktop Managers surface;
- a Desktop Homebrew/Rules controller for Personal/Campaign browse, create, edit and copy flows;
- a bounded repository update operation that updates display name + Homebrew/Rule payload under one revision;
- focused Desktop coverage for lifecycle persistence, atomic edit, stale-write rejection, provenance and independent Campaign-copy behavior.

Validation evidence:

- implementation head `17f4cd686114f734ab0bc50f453de9d579a52c79`;
- push Scaffold `35272269481` — SUCCESS;
- PR Scaffold `35272560206` — SUCCESS;
- PR #65 merged as `6febe3f936593999834189b92aeda9d209385fa7`;
- post-merge Scaffold `35277359425` — SUCCESS, including backend, hosted database, Kotlin build/tests and Android debug APK upload.

## Preserved boundaries

This lightweight Homebrew/Rules slice does **not** claim the full D-0072 Homebrew surface. It does not add:

- structured races/sub-races, classes/subclasses, backgrounds, feats, spells or items;
- official/SRD customization;
- import/export;
- homebrew-aware AI;
- media/object-storage integration;
- hosted reusable-content synchronization;
- generalized all-domain Manager abstractions.

These remain later concrete packages and should be introduced only when their integrated requirements justify the additional model/UI surface.

## Next package — Desktop Place/Shop Manager

The next bounded package is **Desktop Place/Shop Manager — local authoring core**.

It should reuse the already-integrated Wave 6 Place persistence family rather than creating a new persistence family. Under D-0072, Shops are specialized Places, so the first slice should expose the existing Place model and only add Shop-specific behavior if the current persisted payload already supports it or concrete implementation evidence requires a bounded extension.

Expected first slice:

- browse/search Personal and active-Campaign Places;
- create/open/edit the existing Place payload;
- show scope, provenance and revision;
- explicitly copy Personal -> active Campaign as a new independent object;
- save display name + payload atomically under one optimistic revision;
- preserve stale-write/tombstone behavior and the same conservative Personal-owner identity rule used by the preceding Managers;
- add focused controller coverage for persistence, atomic edit, stale-write rejection, provenance and copy independence.

Scene Spine is not pulled into this first package: it does not yet have the same integrated persistence family and would turn a bounded Manager slice into a new architecture/schema package. Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts and richer Stage/Scene work remain later Wave 7 packages.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required by this milestone or its documentation closure. Hard external-service budget remains USD $0.
