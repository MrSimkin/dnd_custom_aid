# Project State — global repository navigation

**Last verified:** 2026-09-14  
**Canonical navigation/discovery branch:** `main`  
**Authoritative current Player implementation branch:** `implementation/phase4a-successor-cycle`  
**Current Player frozen physical candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Current Player evidence:** automation green; targeted cross-device physical revalidation pending on the successor branch  
**Current global design state:** integrated Player + Server + DM MVP architecture consolidated under D-0071  
**Implementation authorization:** not granted by the 2026-09-14 consolidation itself; detailed MVP/Desktop design continues before coding authorization

## 1. Two authoritative lines still exist

`main` remains the canonical place for global repository navigation and DM/product/architecture decisions. It intentionally does not contain the latest Player runtime implementation.

Current Player code/QA authority remains:

`implementation/phase4a-successor-cycle`

That branch's current `docs/checkpoints/LATEST.md` identifies `0.4.0-preqa.13 / 41300` at `92aa9b6...` as the exact frozen automation-green physical candidate. Do not replace that branch's evidence with old `preqa.9` global summaries or with chat memory.

The two lines must not be mechanically force-moved over one another. Future integration must preserve both current Player runtime work and later `main`-only DM/MVP design truth.

## 2. Current Player state

Authoritative source:

`implementation/phase4a-successor-cycle`

Current frozen candidate:

- version: `0.4.0-preqa.13`;
- build/versionCode: `41300`;
- commit: `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630: **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical revalidation: **pending**;
- Phase 4A on that branch: formally **OPEN**.

The 2026-09-14 architecture discussion also used a four-item bounded Player defect planning vocabulary: A10 narrow custom-dice overflow, B1 narrow/micro reorder moved-row visual inconsistency, J1 primary Save persistence/reload failure, and J2 collection-filter hit-count/behavior inconsistency. Preserve that discussion input, but before implementation reconcile it against the then-current authoritative successor checkpoint rather than silently overriding branch evidence.

Existing QA evidence remains valuable for the exact boundaries it exercised. Do not restart Player design or replay all historical QA merely because the next product cycle is broader.

## 3. Current global product/architecture direction — D-0071

The controlling new record is:

`docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`

Checkpoint:

`docs/checkpoints/2026-09-14_MVP_INTEGRATION_ARCHITECTURE_CONSOLIDATION.md`

Core direction:

- the next implementation cycle targets the **integrated MVP**, not an isolated DM prototype or a tiny server milestone;
- Player, hosted/shared services and DM clients are one coherent product/data ecosystem;
- internal slices/waves/parallel workstreams are engineering organization, not separate owner acceptance products;
- the next major owner-facing QA is intended to exercise Player + Server + DM end-to-end;
- local/paper-first authority, non-VTT scope and proportional architecture remain controlling;
- object storage is required this MVP cycle, provider still Pending;
- full server backup/export is required early in the hosted foundation even before a polished restore UI;
- SRD storage/provenance foundations begin early; the actual official-SRD retrieval + grounded AI feature is implemented last among the substantial MVP features;
- generic realtime/WebSockets/Durable Objects/queues/general ACL/general sync platforms remain deferred unless a concrete requirement proves them necessary.

## 4. DM product direction — existing Desks preserved, Desktop expanded

D-0068, D-0069 and D-0070 remain controlling for the DM live Workspace/Desk concepts and shared rules-question capability.

Approved live Desk family remains:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

D-0071 adds the following MVP Desktop direction:

- the Desktop program is now a **DM Desktop App**, not merely a small preparation/administration companion;
- all approved DM Desks must be functionally usable on Desktop so the laptop can replace the tablet operationally if needed;
- Desktop UI remains purpose-built for keyboard/mouse/large-screen use and need not mirror tablet layout;
- Desktop additionally provides rich authoring/management surfaces, explicitly including Monster Creator/Manager, NPC Creator/Manager, Homebrew Rules Input/Manager, Zone Creator/Manager and Encounter Creator/Manager;
- every persistent/content-oriented Desk capability needs sufficient Desktop authoring/management support for the data it consumes;
- PCs use a DM PC Manager/Audit model rather than implying a Player desktop application;
- the same Desktop App includes a permission-gated System Administration area distinct from campaign-DM authority.

## 5. Combat-device fallback now belongs to MVP

The previous `same authoritative device only; handoff later` MVP limitation is superseded.

Because Desktop must serve as a real DM fallback, the MVP must support explicit resume/handoff of an active encounter to another DM device from the latest synchronized state.

Controlling safety behavior:

- exactly one authoritative DM device at a time;
- resume is explicit;
- authority generation/epoch distinguishes the new authority from stale old-device writes;
- increasing combat sequence/version still orders changes within the authority;
- no simultaneous authoritative tablet/Desktop editing;
- no realtime/distributed-lock platform is required simply for this bounded recovery;
- unsynchronized state present only on a lost device cannot be reconstructed magically.

## 6. Server/sync/recovery direction

The server is the durable shared home/exchange point, not an always-live technical Game Session engine.

There is no required technical Session object.

Local Save remains network-independent. Desktop uses explicit Sync; Android may opportunistically retry while retaining manual Sync. Synchronization is scoped and its state is visible. Rare conflicts are surfaced to humans rather than hidden by generalized auto-merge.

PC digital freshness and sync freshness are different facts because paper may be newer than a perfectly synchronized digital copy.

Important durable records use meaningful grouped history/recovery rather than exhaustive telemetry. Restoring an older state creates a new current version; it does not erase later history.

Full server backup/export through the backend is part of the MVP foundation. Native clients never receive direct Neon/PostgreSQL credentials.

## 7. Identity/permission direction

- global persistent identity;
- campaign-scoped roles;
- same user may be DM and own/control a PC in the same campaign;
- remembered login/device behavior is required;
- DM/Player surface switching does not blur action authority;
- PC ownership and current control remain distinct;
- DM audit/correction authority does not imply ownership;
- other campaign Players see only PC name + portrait/avatar + current controlling Player/display identity by default;
- frozen PC remains visible read-only to its owner/controller;
- loss of campaign membership stops future hosted access but does not instantly erase local cached data;
- campaign moderation and global system administration remain separate.

## 8. Current implementation organization

D-0071 approves the technical organization around seven coordinated workstreams:

1. shared MVP spine/contracts;
2. Player stabilization/integration;
3. hosted foundation;
4. shared Kotlin data/sync;
5. DM Desktop product;
6. cross-client live-play exchange;
7. SRD retrieval/AI.

The work may proceed in dependency waves and in parallel where safe, but Player/Server/Desktop must integrate frequently. The main risk is semantic drift among independently evolving implementations.

No implementation has been started by this documentation checkpoint.

## 9. Exact continuation

The discussion is intentionally paused after the 2026-09-14 consolidation.

When the owner returns, continue on `main` from:

> **7D — detailed DM Desktop App product definition:** overall navigation/structure and then area-by-area Desk/live-use plus Creator/Manager/Admin behavior.

Do not restart generic Desk taxonomy; D-0068/D-0069 already define the current family.

After 7D is sufficiently defined, continue to:

> **7E — define exactly what remains outside the expanded MVP.**

Then derive final Git/development topology, implementation roadmap/gates and obtain explicit authorization before coding.

## 10. Release/acceptance status

The project remains development/debug and is not release-ready.

The current Player candidate is not physically owner-accepted merely because automation is green. The integrated-MVP design decision changes the planned next product cycle/acceptance strategy; it does not retroactively fabricate Player QA evidence or owner acceptance.