# Checkpoint — Wave 7 Desktop NPC Manager integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `58a565c3a33a433ce47e7fd4ac1185b5f980644f`  
**PR:** #63 — `feat: add Desktop NPC Manager authoring core`  
**Final implementation head:** `58b680e71ec59c871854eb9c083ff2bc6906fe88`  
**Push Scaffold:** `35269013875` — SUCCESS  
**PR Scaffold:** `35269163375` — SUCCESS  
**Post-merge Scaffold:** `35270643883` — SUCCESS

## Milestone status

The second visible **Wave 7 — Desktop authoring Managers** package is integrated into `main` on top of the completed Wave 6 reusable-content architecture and the integrated Creature/Monster Manager.

The existing Desktop `MANAGERS` destination now provides concrete Creature/Monster and NPC authoring surfaces without introducing a generalized all-domain Manager framework.

The next bounded Wave 7 package is **Desktop Homebrew & Rules Manager — lightweight rules local authoring core**.

## Integrated visible behavior

The NPC Manager supports:

- browsing/searching Personal NPCs and NPCs in the active Campaign;
- creating Personal or active-Campaign NPCs;
- opening/editing the existing Quick and Developed NPC fields;
- keeping incomplete NPCs valid;
- optionally adding, editing or removing combat mechanics through the existing `CreaturePayload` model;
- viewing scope, provenance and optimistic revision information;
- explicit Personal -> active Campaign copy as a new independent Campaign object;
- keeping active campaign context visible while authoring.

Campaign copies remain independent after copy. Later edits to the Personal master, including removal of combat mechanics, do not automatically alter the Campaign copy.

## Identity and revision semantics

Personal NPC authoring uses the same conservative rule as the Creature Manager: Desktop only uses a uniquely resolvable locally persisted active DM account. It does not invent or hard-code an owner identity.

If a unique local DM identity cannot be resolved, Personal creation is disabled rather than guessed; Campaign-local authoring remains available when an active local Campaign exists.

NPC display name and payload are saved atomically through one optimistic revision mutation. Stale or deleted content cannot be silently overwritten or resurrected.

## Persistence and validation

This package reuses the integrated Wave 6 NPC persistence model and the existing Creature stat-block payload; no database schema or migration was required.

The implementation adds:

- a concrete NPC selector/editor inside the existing Desktop Managers surface;
- `DesktopNpcManagerController` for Personal/Campaign browse, create, edit and copy flows;
- a bounded `NpcContentRepository.update(...)` operation that updates display name + NPC payload under one revision;
- focused Desktop coverage for Quick creation, Developed-field persistence, optional combat mechanics, atomic edit, stale-write rejection and independent Campaign copy/provenance.

The focused test also proves that removing combat mechanics from the Personal master later does not mutate the Campaign copy.

Validation evidence:

- implementation head `58b680e71ec59c871854eb9c083ff2bc6906fe88`;
- push Scaffold `35269013875` — SUCCESS;
- PR Scaffold `35269163375` — SUCCESS;
- PR #63 merged as `58a565c3a33a433ce47e7fd4ac1185b5f980644f`;
- post-merge Scaffold `35270643883` — SUCCESS, including backend, hosted database, Kotlin build/tests and Android debug APK upload.

## Preserved boundaries

This first NPC Manager slice does **not** add:

- NPC assistant / AI ideation;
- import/export;
- preserved-live-improvisation promotion workflow;
- media/object-storage integration;
- hosted reusable-content synchronization;
- generalized all-domain Manager abstractions.

## Next package — Desktop Homebrew & Rules Manager

The next bounded package is **Desktop Homebrew & Rules Manager — lightweight rules local authoring core**.

It should reuse the already-integrated `HomebrewRulePayload` and `HomebrewRuleContentRepository` rather than introducing a new persistence family. The first slice should expose D-0072's lightweight rule records:

- browse/search Personal and active-Campaign Homebrew/Rule records;
- create/open/edit title plus summary/body/category/rationale/examples/related references/tags/notes;
- support the existing Draft / Active / Retired lifecycle;
- display scope, provenance and revision;
- explicitly copy Personal -> active Campaign as an independent object;
- save display name + payload atomically under one optimistic revision;
- preserve stale-write/tombstone behavior and the same conservative Personal-owner identity rule used by Creature/NPC Managers.

Structured races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, hosted reusable-content sync and generalized Manager infrastructure remain later concrete packages. This sequencing implements the existing lightweight persistence first without claiming the full D-0072 Homebrew & Rules surface is complete.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required by this milestone or its documentation closure. Hard external-service budget remains USD $0.
