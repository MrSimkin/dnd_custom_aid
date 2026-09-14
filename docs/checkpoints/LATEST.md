# Latest project checkpoint — global resume map

**Updated:** 2026-09-14  
**Branch authority after integration:** `main`  
**Current documentation work branch:** `docs/mvp-integration-consolidation-2026-09-14` until this checkpoint is merged  
**Role:** canonical global navigation + current DM/MVP product/architecture decisions  
**Player code authority:** `implementation/phase4a-successor-cycle`  
**Current Player frozen physical candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Current global design decision:** D-0071 — integrated Player + Server + DM MVP  
**Implementation authorization:** not granted by this documentation checkpoint

## Read first

1. `docs/checkpoints/2026-09-14_MVP_INTEGRATION_ARCHITECTURE_CONSOLIDATION.md` — exact current architecture/product resume point;
2. `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md` — detailed controlling integrated-MVP decision;
3. `docs/PROJECT_STATE.md` — global state and execution rules;
4. `docs/BRANCH_STATUS.md` — branch roles/history;
5. D-0068 / D-0069 / D-0070 — existing DM Workspace/Desk family and shared rules-question direction;
6. for current Player code/QA, switch to `implementation/phase4a-successor-cycle` and read that branch's `docs/checkpoints/LATEST.md`.

## Current branch topology

`main` and `implementation/phase4a-successor-cycle` remain intentionally divergent authoritative lines until a future explicitly designed integration.

- `main` = global navigation, product/design/architecture truth, including D-0068 through D-0071 after this documentation checkpoint is integrated;
- `implementation/phase4a-successor-cycle` = current Player runtime/QA authority.

Do not overwrite either line with the other. Future integration must preserve both the current Player runtime work and the later main-only DM/MVP decisions.

## Current Player source/evidence

Authoritative branch:

`implementation/phase4a-successor-cycle`

Current frozen candidate according to that branch's 2026-09-14 `LATEST.md`:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation — **pending**;
- Phase 4A — formally **OPEN** on that branch.

Do not infer physical PASS from green automation.

The architecture discussion also carried four bounded Player defect labels (A10, B1, J1, J2). Preserve those as planning input in D-0071, but reconcile against the then-current successor-branch checkpoint before implementation instead of replacing exact branch evidence with chat recollection.

## Current integrated-MVP direction

The owner explicitly changed the next-cycle philosophy:

> The next implementation build should aim for the real integrated MVP, not stop at an isolated DM prototype or a tiny server slice.

The next major owner-facing acceptance QA is intended to exercise:

```text
Player Android <-> hosted/shared services <-> DM tablet/Desktop
```

Internal slices/waves/tests remain necessary, but they are engineering controls rather than separate product acceptance milestones.

In-scope foundations/directions include Player completion, authentication/multicampaign roles, server/API/database, synchronization/conflicts, object storage, PC sharing/audit, DM reusable/campaign content, saved/live encounters, combat/public projection, full DM Desktop capability, backup/export, SRD foundations and final official-SRD AI clarification.

## Desktop DM App — do not resume from old companion-only model

Desktop is now both:

1. rich DM authoring/preparation/campaign/system administration;
2. a complete operational DM fallback/client if the tablet is unavailable.

All approved live DM Desks must therefore function on Desktop:

- DM Screen;
- Stage Desk;
- Dungeon Desk;
- Combat Desk.

Desktop additionally requires deliberate authoring/manager surfaces including at minimum:

- Monster Creator/Manager;
- NPC Creator/Manager;
- Homebrew Rules Input/Manager;
- Zone Creator/Manager;
- Encounter Creator/Manager;
- PC Manager/Audit;
- Campaign/member/permission management;
- permission-gated System Administration.

Every persistent/content-oriented Desk capability needs enough Desktop authoring support to create/maintain its data, without forcing identical tablet/Desktop UI.

## Combat authority fallback

Because Desktop must be able to replace the tablet operationally, explicit combat authority resume/handoff is now part of the MVP direction.

Only one DM device remains authoritative at a time. Resume uses the latest synchronized hosted state and must invalidate stale old-authority writes, conceptually through an authority generation/epoch plus increasing combat sequence/version. Simultaneous authoritative multi-device combat is not required.

## Hosted infrastructure summary

Required in this MVP in bounded form:

- domain-specific auth/authorization;
- project-specific sync/revisions/idempotency/tombstones;
- PostgreSQL/API implementation;
- object storage (provider Pending; not necessarily R2);
- meaningful audit/history/recovery;
- full server backup/export;
- SRD schema/provenance/loading foundation;
- ordinary HTTP/polling sufficient for approved flows.

Deferred unless evidence requires them: generalized realtime/WebSockets, Durable Objects, queues, generic ACL platform, generalized sync platform, elaborate retention system, public homebrew publishing.

SRD retrieval + grounded AI remains in this MVP, but its user-facing implementation is deliberately the **last substantial feature of the cycle** after the foundations and core Player/Server/DM path exist.

## Exact resume point

The owner intentionally paused after requesting this consolidation.

When discussion resumes:

1. continue **7D — DM Desktop App detailed product definition**;
2. define overall Desktop navigation/structure;
3. walk area-by-area through live Desks plus Creator/Manager/Campaign/Admin surfaces;
4. preserve D-0068/D-0069 rather than reopening generic Desk taxonomy;
5. once 7D is sufficiently defined, continue to **7E — exact outside-MVP boundary**;
6. only then derive final Git/development topology, implementation gates and explicit coding authorization.

No application implementation was performed by this checkpoint.