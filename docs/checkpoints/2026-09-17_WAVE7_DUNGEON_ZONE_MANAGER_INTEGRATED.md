# Checkpoint — Wave 7 Desktop Dungeon/Zone Manager integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `5be90a994f453e5444ecf00762cd72407cfe790a`  
**PR:** #73 — `feat: add Desktop Dungeon and Zone Manager core`  
**Implementation head:** `1a8b875a567ed73fbcceee73872ec59702b4b3f0`  
**Push Scaffold:** `35285060523` — SUCCESS  
**PR Scaffold:** `35285359440` — SUCCESS  
**Post-merge Scaffold:** `35285629973` — SUCCESS

## Milestone status

The bounded **Desktop Dungeon/Zone Manager — local Zone Brief authoring core** is integrated into `main` as the next concrete Wave 7 authoring package.

The implementation reuses the existing Wave 6 Zone persistence rather than creating a duplicate Dungeon model or a new schema. The next bounded Wave 7 package is **Encounter Manager / Encounter Creator — local authoring core**.

## Integrated Zone Manager behavior

The Desktop Managers hub now includes a dedicated `Mazmorras / Zonas` surface with:

- Personal and active-Campaign Zone browse/search/create/open/edit;
- area and tag filtering plus full-text retrieval across Zone Brief content;
- deterministic Personal-before-Campaign browsing;
- explicit Personal -> Campaign independent copy with retained provenance;
- atomic display-name + Zone payload save under one optimistic revision;
- stale-write rejection and tombstone/non-resurrection semantics inherited from the reusable-content spine.

The editor preserves the approved Zone Brief framing while exposing the richer existing payload:

- **PRESENTAR** — name, summary, area, presentation, space/topology preparation and player-safe text;
- **INTERACTUAR** — exploration/flow, interactives, clues, checks and prepared consequences;
- **ENCUENTRO** — encounter brief;
- **APOYO DM** — DM guidance, paper references and tags.

Existing `space` and `exploration` fields carry bounded topology/flow preparation. No tactical geometry or generalized graph layer was introduced.

## Persistence and repository behavior

No SQLDelight schema change or migration was required. The package reuses the already-integrated `ZonePayload`, `ZoneContentRepository`, `zone_payload` table and migration `23.sqm`.

`ZoneContentRepository` now also exposes an atomic display-name + payload update operation. It validates the Zone family and persisted payload, updates reusable-content metadata and the Zone payload inside one revision-controlled mutation, and therefore keeps visible Manager semantics aligned with the previously integrated authoring Managers.

## Preserved semantics

The package preserves:

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

- unique local DM resolution for Personal authoring;
- Personal Zone creation;
- full Zone Brief payload editing;
- query + area + tag filtering;
- atomic display-name + payload mutation;
- stale-write rejection without partial rename/payload mutation;
- explicit Personal -> Campaign copy, provenance and independent object identity;
- later Personal edits do not alter the Campaign copy;
- tombstone/non-resurrection behavior while the Campaign copy remains active.

Exact evidence:

- implementation head `1a8b875a567ed73fbcceee73872ec59702b4b3f0`;
- push Scaffold `35285060523` — SUCCESS;
- PR Scaffold `35285359440` — SUCCESS;
- PR #73 merged as `5be90a994f453e5444ecf00762cd72407cfe790a`;
- post-merge Scaffold `35285629973` — SUCCESS, including backend, hosted database, Kotlin/shared/Desktop tests, Android debug assembly and APK upload.

## Preserved boundaries

This Zone Manager slice does **not** add:

- a new Zone/Dungeon persistence family or migration;
- tactical geometry or VTT maps;
- generalized graph infrastructure;
- clocks/readiness state;
- automated fictional consequences;
- hosted reusable-content synchronization;
- live Dungeon/Combat Desk behavior;
- provider/deployment changes.

## Next package — Encounter Manager / Encounter Creator

The next bounded package is **Encounter Manager / Encounter Creator — local authoring core**.

Wave 6 already integrated `EncounterPayload` and `EncounterContentRepository` with migration `24.sqm`. Current persisted Encounter data includes:

- summary;
- environment;
- context;
- DM guidance;
- participants;
- tags;
- notes.

Encounter participants already support:

- optional reusable Creature/NPC source IDs;
- free-text labels;
- quantity;
- `EXPECTED`, `RESERVE` and `CONDITIONAL` readiness;
- condition text;
- overrides;
- notes.

The repository already validates Creature/NPC dependencies and scope, and Personal -> Campaign Encounter copy performs domain-specific Creature/NPC dependency copy/remapping while deduplicating repeated source dependencies. No generalized dependency graph should be introduced merely to build the Manager.

Expected bounded work:

- Personal + active-Campaign Encounter browse/search/create/open/edit;
- participant authoring around the existing Creature/NPC dependency model plus label-only participants;
- explicit visibility of quantity/readiness/condition/overrides/notes;
- explicit Personal -> Campaign independent Encounter copy preserving current dependency-copy/remap semantics;
- atomic display-name + Encounter payload update under one optimistic revision;
- focused repository/controller coverage for dependency validation, stale writes, copying and tombstones;
- integration into the existing Desktop Managers surface with active Campaign context visible.

Do not turn this first authoring surface into live combat state, initiative automation, generalized dependency infrastructure, encounter balancing AI, or provider work unless a concrete approved workflow requires it.

PC Manager/Audit, Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## Provider/cost state

No Cloudflare, Descope, Neon deployment or object-storage action was required. Hard external-service budget remains USD $0.
