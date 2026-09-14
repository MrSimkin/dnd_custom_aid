# 2026-09-14 — Integrated MVP architecture and DM Desktop App consolidation

**Working branch:** `docs/mvp-integration-consolidation-2026-09-14`  
**Target authoritative branch:** `main`  
**Scope:** owner-approved product/architecture consolidation; no implementation code  
**Primary durable decision:** `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`

## Purpose

The owner deliberately paused before the next implementation cycle and requested that **everything agreed in the architecture discussion be persisted in Git before continuing**. This checkpoint is the resume map; D-0071 contains the detailed controlling decision.

Do not reconstruct this discussion from chat. Read D-0071 plus the existing D-0068/D-0069/D-0070 Desk decisions.

## Current Player authority remains separate

Current Player runtime/QA authority remains:

`implementation/phase4a-successor-cycle`

Its 2026-09-14 `docs/checkpoints/LATEST.md` identifies the exact frozen physical candidate as:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 SUCCESS;
- targeted cross-device physical revalidation pending;
- Phase 4A still formally open on that branch.

The architecture discussion also referenced a later/current planning gate in terms of four bounded Player defect labels (A10, B1, J1, J2). Preserve those labels as discussion input in D-0071, but **before coding, reconcile any label/state mismatch against the then-current authoritative Player checkpoint rather than overwriting proven branch evidence from memory**.

## Main strategic change

The owner no longer wants the next product milestone to be a tiny server slice or an isolated DM prototype.

The next implementation cycle is intended to reach the **integrated MVP**:

```text
Player Android <-> Server/shared services <-> DM tablet/Desktop
```

Implementation may use internal dependency waves and parallel workstreams, with frequent engineering integration checks, but the next major owner-facing product acceptance QA is intended to test the complete integrated MVP.

This means older roadmap language that treated desktop combat, DM-device handoff, or all DM implementation as automatically post-MVP/post-Phase-4A is no longer the current product-plan direction. However, **this checkpoint itself is documentation/design authorization only**. Do not begin coding merely because the future implementation scope is now defined more broadly.

## Core authority model preserved

- paper is the normal live PC truth;
- app/local digital state is an operative reflection and durable intentionally reconciled backup;
- server is the durable shared home/exchange point, not an always-live game-session engine;
- no formal technical Game Session object is required;
- most tabletop behavior remains local and can tolerate disconnection;
- active combat is local-first on one authoritative DM device;
- Player combat view is a public projection of the latest successfully synchronized DM state;
- application remains an assistant, not a VTT/rules engine.

## Synchronization/recovery consolidation

Approved current direction includes:

- local Save independent from network;
- Desktop explicit Sync; Android may opportunistically retry plus manual Sync;
- visible sync state;
- stable IDs, revisions, idempotent mutations and tombstones;
- explicit/human conflict handling instead of generalized auto-merge;
- scoped synchronization rather than mirroring everything everywhere;
- distinguish digital-data freshness from server-sync freshness;
- meaningful grouped history, not exhaustive telemetry;
- object-type-specific recovery strength;
- restoration creates a new current version rather than erasing later history;
- soft deletion/recovery for important durable records;
- full server backup/export is part of the MVP foundation **now**, even though a polished full-restore UI is not.

## Identity/permission consolidation

- global identity; campaign-scoped Player/DM roles;
- same account may be DM and own/control a PC in the same campaign;
- DM/Player surface switching does not change the authority under which an action is performed;
- remembered login/trusted device behavior is required so users do not authenticate every launch;
- Owner/Editor/Viewer is useful vocabulary, but MVP permissions are domain-derived rather than a generic ACL system;
- PC ownership and current control remain separate;
- DM audit/correction authority does not imply PC ownership;
- frozen PC remains visible read-only to owner/controller;
- campaign membership loss stops future hosted access but does not instantly wipe local cached data;
- campaign moderation remains distinct from global system administration.

## PC campaign-visible identity

Other campaign Players receive only the deliberately tiny default PC identity projection:

- character name;
- portrait/avatar;
- current controlling Player/display identity.

The portrait/avatar is always visible within that minimum identity. Mechanical/full-sheet data does not become campaign-public by default.

## Object storage

Object storage is required in this MVP cycle. The provider is **not selected**; Cloudflare R2 is only one possible candidate.

The domain should use stable asset identity/metadata and keep provider-specific integration localized rather than exposing provider keys throughout business models.

The first concrete shared-file need is PC portrait/avatar synchronization.

## Infrastructure triage

Required now in bounded project-specific form:

- domain-specific authorization;
- hosted PostgreSQL/API implementation;
- sync/revision/idempotency/tombstone behavior;
- object storage;
- basic meaningful audit/history;
- backup/export;
- HTTP/polling sufficient for approved workflows.

Deferred unless a concrete requirement proves otherwise:

- WebSockets;
- Durable Objects/equivalent room coordinators;
- queues;
- generalized realtime architecture;
- generic ACL framework;
- elaborate history-retention service;
- generalized sync platform;
- public/community homebrew publishing.

## SRD / AI ordering

Official-SRD grounded natural-language clarification remains MVP for both Player and DM.

The architecture remains PostgreSQL versioned/provenance-preserving SRD 5.1 + 5.2.1 chunks, PostgreSQL full-text retrieval first, replaceable LLM, initially Workers AI.

For this cycle:

- begin SRD schema/provenance/loading foundations as soon as the hosted database work permits;
- implement the actual retrieval + grounded AI + user-facing Player/DM clarification feature **last among the substantial features of this MVP build**;
- homebrew rules may be authored/stored in MVP, but MVP AI remains official-SRD-only.

## Implementation organization approved

D-0071 records seven coordinated workstreams:

1. shared MVP spine/contracts;
2. Player stabilization/integration;
3. hosted foundation;
4. shared Kotlin data/sync;
5. DM Desktop product;
6. cross-client live-play exchange;
7. SRD retrieval/AI.

It also records Waves 0–4. These are engineering organization/dependency controls, **not separate product acceptance milestones**.

The major engineering risk is semantic drift among Player/Server/Desktop. Shared domain meaning, an evolving common API/database contract, shared sync primitives where useful, and frequent real integration are controlling.

## DM Desktop App — major scope expansion

Do not resume from the old mental model `Desktop = small DM administration companion`.

The MVP Desktop product now has two roles:

1. rich authoring/preparation/campaign/system administration;
2. **complete operational DM fallback/client** if the tablet is lost/unavailable.

Desktop does not need identical UI to tablet; it should exploit keyboard/mouse/large-screen patterns. But the DM must remain able to run the game from the laptop.

### All approved DM Desks on Desktop

The current D-0069 live Desk family remains:

- DM Screen;
- Stage Desk;
- Dungeon Desk;
- Combat Desk.

All must have functional Desktop counterparts.

### Desktop creator/manager requirements explicitly added by owner

At minimum:

- Monster Creator/Manager;
- NPC Creator/Manager;
- Homebrew Rules Input/Manager;
- Zone Creator/Manager;
- Encounter Creator/Manager.

General rule: every persistent/content-oriented DM Desk capability must have sufficient desktop authoring/management support for the data it consumes. Additional Stage/Dungeon authoring surfaces (Places/Shops, Scene Spine, clocks/triggers/notes, etc.) should be derived when those concrete contracts are designed rather than inventing redundant screens by naming symmetry.

PC counterpart is primarily PC Manager/Audit because PCs are Player-owned. Combat is an operational Desk rather than a `Combat Creator`.

### Desktop System Administration

The same Desktop program includes a distinct System Administration area visible only to application administrators. It may include global account freeze/unfreeze, full backup/export, useful diagnostics/storage information and later approved recovery/maintenance capabilities.

Do not hard-code system admin = campaign DM.

## New combat-device fallback requirement

Because Desktop must replace the tablet operationally if necessary, explicit DM-device combat authority resume/handoff is now an MVP requirement.

The owner does **not** want simultaneous authoritative tablet+desktop combat editing.

Approved safety contract:

- one authoritative device at a time;
- resume on another DM device is explicit;
- resume uses the latest synchronized hosted combat state;
- an authority generation/epoch can invalidate stale writes from the old device;
- per-authority increasing sequence/version still orders combat changes;
- no realtime/distributed locking platform is required simply to provide this bounded recovery.

Unsynchronized actions that existed only on a lost device cannot be recovered magically.

## Existing Desk discovery is not reopened

D-0068 and D-0069 remain controlling for:

- DM Attention Budget;
- Campaign -> Workspace -> flat Desks;
- no formal Session ritual;
- multiple concurrent Desks/Combat Desks where appropriate;
- Stage retrieval-first behavior, Places/Shops/NPCs and Scene Spine;
- Dungeon Zone Briefs, Dungeon Turns, notes, clocks, Encounter Readiness;
- dirty live improvisation -> preserve snapshot -> Desktop cleanup/promotion;
- Combat Desk as tracker + tactical guidance assistant;
- non-VTT/non-rules-engine boundaries.

Journey remains a future candidate unless later separately approved.

## What remains deliberately unresolved

Do not silently choose these during a future continuation without the appropriate design/implementation step:

- object-storage provider;
- detailed Desktop navigation/information architecture;
- detailed authoring forms/schemas for the creator/manager areas;
- exact UX/implementation of combat authority resume;
- backup package format and later restoration UI/process;
- object-specific conflict comparison UX;
- final shared rules-question product name/placement;
- complete outer post-MVP boundary.

## Exact resume point

The discussion is intentionally paused **here**.

When the owner returns, resume at:

> **7D — DM Desktop App detailed product definition:** overall navigation/structure, then area-by-area Desk/live-use and Creator/Manager/Admin behavior.

Do **not** jump directly to implementation or restart generic Desk taxonomy.

After 7D is sufficiently defined, continue to:

> **7E — define exactly what remains outside the expanded MVP.**

Then derive final Git/development topology, implementation roadmap/gates and obtain explicit coding authorization.

## Verification performed for this checkpoint

Documentation-only checkpoint. No application code, database migration, backend endpoint or automated test was changed/executed.

Before writing, the following current repository sources were re-read on `main`: mandatory continuity/governance docs, D-0068, D-0069, D-0070, and the 2026-09-10 DM Desk family checkpoint. The current `implementation/phase4a-successor-cycle` `LATEST.md` was also re-read to preserve the actual preqa.13 Player candidate identity rather than the older preqa.9 global summary.