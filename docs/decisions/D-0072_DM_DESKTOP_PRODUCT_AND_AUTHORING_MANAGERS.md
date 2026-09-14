# D-0072 — DM Desktop App product definition and authoring/management surfaces

**Status:** Approved product direction  
**Date:** 2026-09-14  
**Decision owner:** Project owner  
**Implementation authorization:** no code implementation is authorized by this record alone

## Purpose

This decision closes the detailed product-definition pass for the DM Desktop App that began after D-0071. It preserves the already-approved live DM Desk family and defines the richer Desktop workbench, persistent-content managers, campaign administration, sole-admin system administration and media/handout support required by the integrated MVP.

The Desktop App is one program with the same DM game semantics as the DM Android/tablet experience. Desktop receives richer keyboard/mouse/wide-screen workflows and authoring/administration capabilities, but does not become a separate product with different domain behavior.

---

## 1. Overall Desktop structure

The approved high-level structure is:

```text
DM DESKTOP APP
|
+-- LIVE / WORKSPACE
|   +-- DM Screen
|   +-- Stage Desks
|   +-- Dungeon Desks
|   +-- Combat Desks
|
+-- PREPARE / MANAGE
|   +-- PCs
|   +-- NPCs
|   +-- Monsters
|   +-- Homebrew & Rules
|   +-- Stages / Places
|   +-- Dungeons / Zones
|   +-- Encounters
|   +-- Media / Handouts
|   +-- other campaign material where justified
|
+-- ADMINISTRATION
    +-- Campaign
    +-- Members / roles / invitations
    +-- Synchronization / backup surfaces
    +-- System Administration
```

Persistent campaign context and quiet but visible synchronization state should remain available in the Desktop shell. Working tabs/documents are allowed. Opening an entity in a working tab does not create a live Desk.

Live answers: **what do I need now?**  
Prepare/Manage answers: **what am I creating or maintaining?**  
Administration answers: **how is this campaign/application configured and operated?**

---

## 2. Desktop Live/Workspace parity

All approved live DM Desks are available on Desktop:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

The Desktop versions use the same domain data, game semantics and live behaviors as the Android/tablet DM App. Desktop-specific work should solve presentation/usability differences rather than creating alternate rules.

Desktop remains a complete operational fallback/client. If the tablet becomes unavailable, Desktop can explicitly resume the latest synchronized combat state under the one-authoritative-DM-device contract defined by D-0071.

---

## 3. Common Prepare/Manage model

Persistent DM content follows three conceptual states/scopes where applicable:

```text
PERSONAL LIBRARY
Reusable DM-owned material
      |
      | explicit use/copy
      v
CAMPAIGN CONTENT
Independent campaign-specific copy
      |
      | preparation / live use
      v
LIVE / WORKING STATE
Actual play state, dirty overrides and temporary copies
```

Managers share common interaction language where appropriate:

- browse/search;
- create/open/edit;
- duplicate;
- delete/recover where applicable;
- see current scope;
- see provenance;
- explicitly use/copy into a campaign;
- organize/filter;
- review/promote preserved dirty improvisation.

Creation/editing normally lives inside each domain Manager rather than as a separate top-level Creator application.

Personal masters and campaign copies become independent after explicit copy/use. Provenance may remain visible, but there is no automatic inheritance/update relationship in the MVP.

---

## 4. Monster Manager / Monster Creator

The Monster Manager MVP includes:

- browse/search/filter;
- complete human-usable monster/stat-block editing;
- Personal, Campaign and canonical Official/SRD source distinctions;
- duplicate/customize flows;
- explicit Personal -> Campaign copy with provenance;
- campaign-specific independent edits;
- DM tactical/run guidance;
- preserved dirty-improvisation review/cleanup/promotion;
- simple reusable mechanical Packages;
- import/export;
- assisted creature creation.

### 4.1 Human-complete, selectively structured

Core mechanical facts should be structured where software genuinely benefits from them, while traits/actions/reactions/legendary actions and unusual mechanics may retain formatted rich text. The application does not attempt to recreate a complete executable D&D rules engine.

### 4.2 Import/export

The MVP includes a downloadable/importable monster template, example data, validation, preview before commit, warnings/errors, destination selection and export/round-trip capability. Bulk or richer adapters may be added where useful, but support for every third-party ecosystem format is not an MVP obligation.

The exact canonical file format is a technical implementation choice.

### 4.3 Creature Creator Assistant

The assistant helps the DM design rather than declaring mathematical truth. Useful capabilities include:

- start blank, from existing, or assisted;
- intended challenge/profile input;
- combat-role/profile concepts;
- derived-value assistance with manual override;
- advisory durability/damage/control/mobility/complexity diagnostics;
- comparison to suitable official/SRD benchmarks;
- warnings when a value is unusual for the selected profile;
- reusable actions/traits/Packages;
- suggestions that remain advisory.

The DM always wins. The tool does not emit a hard `balanced/unbalanced` judgment and does not automatically repair intentional choices.

AI-generated monsters are not required for the MVP. Deterministic guidance, SRD comparisons and transparent advisory calculations are the foundation. AI may later assist ideation/text where genuinely useful.

---

## 5. NPC Manager / NPC Creator

NPCs are not merely monsters with biography fields.

The same NPC can progress naturally through increasing detail:

```text
Quick NPC
   -> Developed NPC
   -> optionally add simple/full combat mechanics
```

A valid NPC never requires a combat stat block.

### 5.1 Quick NPC

A quick NPC may contain only enough to run immediately, such as:

- name;
- short concept/role;
- appearance/first impression;
- personality/manner;
- wants/fears/needs;
- what they can offer;
- limits/refusals;
- relationship/context;
- useful notes.

### 5.2 Developed NPC

A richer dossier may add identity, voice/mannerisms, motivations, values, relationships, history, secrets, knowledge, goals, resources, affiliations, places, adventure/scene links, DM guidance and optional mechanics.

The same Monster/stat-block machinery should be reused for full mechanical NPCs rather than building a second incompatible combat model.

### 5.3 NPC creation assistant

The helper should help the DM discover/use the NPC rather than dumping a complete novel. It may ask targeted questions, identify useful missing information and suggest ideas on demand. Incomplete NPCs remain valid; there is no completion score.

AI assistance is acceptable for ideation such as fears, mannerisms or motivations, but should build from the owner's authored context and present editable suggestions rather than replacing authorship.

### 5.4 Import/export and live promotion

NPCs support the same general import/export pattern. A live/improvised NPC can be preserved, reviewed and developed later without recreation.

---

## 6. Homebrew & Rules Manager

The previously narrower Homebrew Rules surface is expanded into a broader **Homebrew & Rules Manager**.

It supports reusable Personal and Campaign content including, as applicable:

- house rules / variants / rulings / custom subsystems;
- races and sub-races;
- classes and subclasses;
- backgrounds;
- feats;
- spells;
- ordinary items;
- magic items;
- other justified structured game-content families.

### 6.1 Rules

Simple rules can remain lightweight records with title/summary/rich text and optional category, rationale, examples, related references and tags.

A minimal lifecycle of Draft / Active / Retired is sufficient.

Longer custom systems may use rich headings/sections rather than being over-normalized.

### 6.2 Structured game content

Each content family receives an appropriate editor. Structure data where the application genuinely uses it; preserve rich human-readable mechanics where automated interpretation is unnecessary.

Custom classes/subclasses may represent progression/features sufficiently for the permissive PC model without requiring a complete automated character-builder engine.

### 6.3 Integration with the rest of the app

Campaign homebrew should become selectable/usable by the relevant Player/DM surfaces: spells in spell-related PC data, feats in feat data, items in inventory, races/classes/backgrounds in character identity/reference, etc.

This does not imply automatic legality enforcement or automatic execution of every rule consequence.

### 6.4 Import/export

Every practical family supports template-based import/export and preview/validation. Bulk import is desirable/required where practical. Rich package import containing several content families may be supported without creating a public publishing marketplace.

Canonical official/SRD content remains read-only; customization creates an independent personal/campaign copy with provenance.

### 6.5 AI boundary

Storing/using homebrew is MVP. Official-SRD rules clarification remains official-SRD-only in this MVP. Homebrew-aware mixed-source AI reasoning is deferred.

---

## 7. Stage / Place / Dungeon / Zone preparation

Adventure/location preparation is divided into a Stage Manager and Dungeon Manager while allowing references between them.

### 7.1 Stage Manager

Stage preparation supports fast future retrieval of Places, Shops, linked NPCs, area/function organization and Adventure/Scene Spine material.

One canonical Place can be retrieved by name, geography/area, function, narrative context, linked NPC, recent/open/pinned state or search. A Shop is a specialized Place, not a separate top-level Desk/system.

Places may range from tiny records to rich records containing presentation, services/interactives, hooks, linked NPCs, images, player-safe material, DM-only notes, scene/adventure links, clocks and paper references.

### 7.2 Scene Spine

The Adventure/Scene Spine remains a lightweight orientation structure rather than a quest engine. Scenes can contain only the title/purpose/possible next scenes and links to Places, NPCs, Encounters, clocks, handouts or paper/document references.

Partial digital representation is explicitly valid.

### 7.3 Dungeon Manager / Zone Creator

The Dungeon Manager authors topological/flowchart-like exploration structure, not tactical geometry or VTT maps.

Prepared Areas/Zone Briefs remain conceptually different from Dungeon Turn movement zones.

Zone Briefs preserve the live grouping:

- PRESENTAR;
- INTERACTUAR;
- ENCUENTRO.

Desktop authoring may expose richer optional sections for atmosphere/presentation, space, exploration, clues, checks, consequences, DM guidance, encounter purpose/tactics/loot/references, etc.

Encounter Readiness, clocks and advisory triggers belong naturally here. Triggers/reminders may suggest consequences; they do not automatically decide fiction.

---

## 8. Encounter Manager / Encounter Creator

Saved encounters are reusable preparation and remain distinct from live encounters/combat working state.

The Encounter Manager supports:

- blank creation/duplicate;
- participants/groups;
- encounter-specific overrides without mutating source monsters/NPCs;
- Expected / Reserve / Conditional participants;
- environment/context;
- DM encounter guidance;
- links to Places/Zones/rules/clocks/handouts;
- creation from preserved live/Encounter Readiness state;
- archive vs save-as-new-template distinction after play;
- Personal reusable encounter templates;
- explicit Personal -> Campaign copy.

A Personal Encounter may reference Personal Monster/NPC dependencies. `Use in campaign` may create the necessary independent campaign dependency copies and reconnect the new campaign encounter to them.

Import/export is included. Import preview must handle unresolved/referenced dependencies sensibly rather than silently guessing.

There is no automatic encounter-balance authority or combat simulation requirement.

---

## 9. PC Manager / Audit

Desktop is not a Player desktop character-builder. PC Manager exists for DM inspection, audit and administration over the same canonical PC records used by the Player App.

It includes:

- campaign PC overview;
- complete PC inspection;
- explicit distinction between data freshness and sync freshness;
- meaningful grouped audit/history;
- compensating corrections that preserve history;
- explicit `Correct / Edit as DM` action rather than silently impersonating the Player;
- ownership and controller administration;
- freeze/unfreeze;
- lifecycle administration using canonical states;
- duplication where appropriate;
- PDF export using the approved baseline/full-state concepts.

A formal rich suggestion/approval queue is not required for the MVP. Direct DM correction is required.

---

## 10. Campaign Manager

Campaign Administration manages the shared digital environment rather than campaign content itself.

It includes:

- campaign identity/basic lifecycle;
- member list;
- simple campaign roles;
- reusable invitation lifecycle;
- kick/ban/lift-ban;
- PC ownership/control assignment shortcuts;
- campaign-wide sync/status/navigation;
- links/counts to domain Managers.

Campaign membership, campaign role, PC ownership and PC control remain separate concepts.

The MVP roles remain intentionally simple (DM/Player) while the model should not make future co-DM support structurally impossible.

Archived campaigns remain preserved. Permanent deletion is intentionally conservative and not a casual operation.

---

## 11. System Administration — sole-owner operator console

System Administration is a distinct global authority area. The current owner intends to be the only system administrator.

The Desktop App may therefore serve as a practical sole-admin operator console rather than pretending to be a corporate multi-admin IT product.

Expected capabilities include:

- global account status/freeze/unfreeze;
- full server backup/export;
- lightweight application/system health;
- storage/data overview;
- recovery/maintenance operations as they become justified;
- useful provider-assisted administration where it materially improves convenience.

### 11.1 Credentials/provider access

Routine administration should preferably use the project backend/API where practical. However, direct provider administrative integration is permitted when it offers a concrete operational benefit.

Locally stored administrative credentials are permitted when required, provided they are protected using appropriate OS/local secure-credential facilities and are not hard-coded, committed to Git or casually stored as plaintext configuration.

Prefer scoped/least-privilege provider tokens over broad account credentials when possible.

The Desktop App does not need to rebuild Cloudflare/Neon/Descope dashboards. Deep/unusual provider operations may remain in provider dashboards/CLI.

### 11.2 Backup/recovery

Full verifiable server backup/export remains MVP. A polished destructive whole-server restore UI may follow later, while normal accidental deletion should use object-level history/recovery rather than whole-server rollback.

---

## 12. Media & Handouts Manager

A lightweight Media & Handouts capability closes the object-storage/product gap.

It supports persistent images, maps, documents and handouts that may be linked to multiple entities without duplicate uploads.

Useful capabilities include:

- upload/preview;
- rename/title/description/type;
- search/tags where useful;
- see where used;
- replace underlying file without breaking logical references;
- download/export;
- delete/recover where appropriate.

Assets can be marked DM-only or Player-safe. `Player-safe` means safe to reveal, not automatically visible. Revealing/showing to Players remains an explicit DM action where supported.

Managers such as NPC, Place, Zone or Monster may upload/attach media directly; visiting the central Media Manager is not a mandatory workflow step.

---

## 13. Cross-cutting Desktop capabilities

The following are cross-cutting UX/infrastructure rather than new top-level Managers:

- global search/navigation;
- working tabs/recent items;
- visible sync/conflict state and conflict-resolution entry points;
- preferences;
- import dialogs;
- local credential configuration;
- notifications/status.

They should not create additional product areas merely for architectural neatness.

---

## 14. Explicit non-goals preserved

This decision does not authorize:

- a VTT/grid/LOS/fog-of-war engine;
- a complete automatic D&D legality/rules engine;
- mandatory stat blocks for NPCs;
- mathematical `balanced/unbalanced` authority;
- automatic rules/homebrew execution;
- generic workflow/trigger scripting;
- a public/community content marketplace;
- divergent Desktop-vs-Android DM game semantics.

---

## 15. Closure

The detailed DM Desktop product-definition pass (7D) is closed at the product-design level by this record.

The next controlling scope record is D-0073, which captures the exact integrated-MVP boundary, implementation-wave/Git direction and owner-vs-technical decision boundary established immediately after this product-design closure.
