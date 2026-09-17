# Checkpoint — Wave 7 Desktop Creature/Monster Manager integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `12a62288457ebe5892f90f637fe41c142b094591`  
**PR:** #61 — `feat: add Desktop Creature Manager authoring core`  
**Final implementation head:** `38d68dc832188f29c76ec40990297f53a85e9bed`  
**Push Scaffold:** `35267065066` — SUCCESS  
**PR Scaffold:** `35267241770` — SUCCESS  
**Post-merge Scaffold:** `35267674641` — SUCCESS

## Milestone status

The first visible **Wave 7 — Desktop authoring Managers** package is integrated into `main` on top of the completed Wave 6 reusable-content architecture.

The Desktop `MANAGERS` destination now exposes a usable local Creature/Monster Manager rather than a deferred placeholder.

The next bounded Wave 7 package is **Desktop NPC Manager — local authoring core**.

## Integrated visible behavior

The Creature/Monster Manager supports:

- browsing and searching Personal Creatures;
- browsing and searching Creatures in the active Campaign;
- creating Personal or active-Campaign Creatures;
- opening and editing the existing human-complete Creature/stat-block payload;
- viewing scope, provenance and optimistic revision information;
- explicit Personal -> active Campaign copy as a new independent Campaign object;
- keeping active campaign context visible while authoring.

Campaign copies remain independent after copy. Later edits to the Personal master do not automatically alter the Campaign copy.

## Identity and revision semantics

Personal authoring uses only a uniquely resolvable locally persisted active DM account. The first slice does not invent or hard-code an owner identity.

If Desktop cannot resolve a unique local DM identity, Personal creation is disabled rather than guessed; Campaign-local authoring remains available when an active local Campaign exists.

Creature display name and payload are saved atomically through one optimistic revision mutation. Stale or deleted content cannot be silently overwritten or resurrected.

## Persistence and validation

This package deliberately reuses the integrated Wave 6 Creature persistence model; no new database schema or migration was required.

The implementation adds:

- `DesktopCreatureManagerController` and the visible Compose Manager surface;
- Desktop shell routing/wiring for the existing `MANAGERS` destination;
- a bounded `CreatureContentRepository.update(...)` operation that updates display name + payload under one revision;
- focused Desktop coverage for DM identity resolution, Personal creation, atomic edit, stale-write rejection and independent Campaign copy with retained provenance.

Validation evidence:

- push Scaffold `35267065066` — SUCCESS;
- PR Scaffold `35267241770` — SUCCESS;
- PR #61 merged as `12a62288457ebe5892f90f637fe41c142b094591`;
- post-merge Scaffold `35267674641` — SUCCESS, including backend, hosted database, Kotlin build/tests and Android debug APK upload.

## Preserved boundaries

This first Manager slice does **not** add:

- Official/SRD catalog browsing;
- import/export;
- Creature Creator Assistant or advisory balance tooling;
- media/object-storage integration;
- hosted reusable-content synchronization;
- generalized all-domain Manager abstractions;
- live combat behavior.

Those remain attached to later concrete Wave 7/8 packages.

## Next package — Desktop NPC Manager

The next bounded package is **Desktop NPC Manager — local authoring core**.

It should reuse the already-integrated `NpcPayload` and `NpcContentRepository` and preserve D-0072 semantics:

- Quick NPC -> Developed NPC -> optional combat mechanics is a natural progression, not a required completion ladder;
- incomplete NPCs remain valid;
- a combat stat block is optional;
- full combat mechanics reuse `CreaturePayload` rather than creating a second combat model.

Initial bounded behavior:

- browse/search Personal and active-Campaign NPCs;
- create/open/edit the existing NPC payload from Quick through Developed fields;
- optionally add/remove/edit combat mechanics using the existing Creature payload;
- display scope, provenance and revision;
- explicitly copy Personal NPC -> active Campaign as an independent object;
- preserve stale-write/tombstone semantics.

Deferred from this first NPC slice: NPC assistant/AI ideation, import/export, preserved-live-improvisation promotion workflow, media/object storage, hosted reusable-content sync and generalized Manager infrastructure.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required by this milestone or its documentation closure. Hard external-service budget remains USD $0.
