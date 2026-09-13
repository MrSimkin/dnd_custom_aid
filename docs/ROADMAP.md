# Roadmap

This roadmap defines development stages, not a fixed feature list. Product content within each stage remains subject to owner approval.

## Phase 0 — Project Foundation

**Status:** Complete.

---

## Phase 1 — Product Discovery and Design

**Status:** Complete.

---

## Phase 2 — Technical Foundation

**Status:** Complete.

The approved foundation uses Kotlin Multiplatform shared code, Android Jetpack Compose, Compose Multiplatform Desktop, SQLDelight/SQLite, a TypeScript Cloudflare Worker area and PostgreSQL migration/data-loading infrastructure.

C-0009 remains controlling: do not activate speculative infrastructure without a concrete approved need.

---

## Phase 3 — First Vertical Slice

**Status:** Complete.

---

## Phase 4 — MVP Buildout

**Status:** Current.

Phase 4 expanded into a deliberate **Character Foundation Closure** cycle before DM-focused implementation.

### Phase 4A — Character Foundation Closure

**Status:** accepted Player repair implementation complete / automation-qualified; physical owner/device acceptance pending.

Authoritative Player branch:

`implementation/phase4a-successor-cycle`

Current QA candidate:

- version `0.4.0-preqa.9`;
- versionCode/build `40900`;
- candidate commit `cd0c203d337c062fa388010d300e875f2f54ced7`;
- Scaffold run `34726572588` — **SUCCESS**;
- artifact ID `10307444450` / `dnd-custom-aid-debug-apk`;
- artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

The earlier A–I successor engineering and the later `preqa.8 / 40800` physical phone QA are historical inputs to the accepted repair cycle. The accepted repair plan was then implemented through P1–P16 and automation-qualified. P17 is the physical tablet-QA gate policy, not another coding increment.

There is no unfinished Increment J and no hidden P1–P16 coding backlog.

#### Current Phase 4A gate

The next required evidence is **physical owner/device QA of `0.4.0-preqa.9 / 40900`**.

Start with targeted phone regression/acceptance around representative repaired shared boundaries. If no hard shared/systemic failure would invalidate tablet evidence, continue with physical Player tablet portrait/landscape QA under P17.

A real QA failure may reopen only the relevant accepted repair boundary on `implementation/phase4a-successor-cycle`.

Do not invent unrelated Player features merely to keep implementation moving while this gate is open.

### Phase 4A exit criterion

Phase 4A is complete only when:

- D-0047 character-foundation scope exists — **done**;
- successor engineering and the accepted repair package are implemented — **done**;
- P1–P16 automated validation is green — **done**;
- a unique repaired QA candidate exists — **done: `preqa.9 / 40900`**;
- targeted physical phone QA of the repaired candidate is acceptable — **pending**;
- physical Player tablet portrait/landscape QA is completed under P17 — **pending**;
- blocking findings, if any, are repaired and revalidated — **pending as needed**;
- the owner explicitly accepts/closes Phase 4A — **pending**.

**No DM feature implementation begins before Phase 4A owner acceptance and explicit closure.**

### Phase 4B / Phase 5A — DM live-session product line

**Status:** product/discovery decisions active on `main`; implementation blocked.

`main` carries the later valid DM/Phase 5A discovery/design line, including the approved Desk-family direction and shared Player/DM rules-question capability.

This line is allowed to continue as product discovery/design when explicitly requested. It is **not** implementation authorization while Phase 4A remains open.

Future DM implementation must begin from the accepted `main` discovery decisions only after explicit Phase 4A closure, while preserving the completed Player foundation.

---

## Phase 5 — MVP Hardening

**Goal:** make the first release dependable enough for real use after the relevant implementation lines are accepted.

Potential areas, only as observed/needed:

- regression testing;
- real phone/tablet usability;
- desktop administration workflows;
- multicampaign isolation;
- local-first/offline/reconnection behavior;
- PDF/export verification;
- accessibility;
- data migration/recovery;
- measured performance work;
- crash handling;
- proportionate privacy/security review;
- packaging/release process.

---

## Phase 6 — Post-MVP Evolution

**Goal:** add features based on actual priorities/usage while preserving continuity.

Possible later directions include broader Android/desktop parity, player desktop, desktop combat, co-DMs, explicit DM-device combat handoff, house-rule-aware clarification, realtime transport if proven useful, and other owner-approved expansions.

## Branch/continuity rule

The roadmap does not define branch authority by itself. Current branch roles and lifecycle are controlled by `docs/BRANCH_STATUS.md`; exact practical resume points are controlled by `docs/checkpoints/LATEST.md` on each active authoritative line.
