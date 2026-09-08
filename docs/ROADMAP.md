# Roadmap

This roadmap defines development stages, not a fixed feature list. Product content within each stage remains subject to owner approval.

## Phase 0 — Project Foundation

**Status:** Complete. Merged through PR #1 on 2026-08-28.

---

## Phase 1 — Product Discovery and Design

**Status:** Complete. Approved product/MVP baseline merged through PR #2 on 2026-08-29.

---

## Phase 2 — Technical Foundation

**Status:** Complete. Architecture checkpoint merged through PR #3; audited minimal scaffold merged through PR #4 on 2026-08-30.

The canonical foundation provides one shared Kotlin Multiplatform module, Android Jetpack Compose, Compose Multiplatform Desktop, SQLDelight, a TypeScript Cloudflare Worker area, PostgreSQL migration/data-loading area and one simple GitHub Actions workflow.

C-0009 remains controlling: do not activate speculative infrastructure without a concrete approved feature need.

---

## Phase 3 — First Vertical Slice

**Status:** Complete. Local Android campaign creation and active-campaign selection merged through PR #5 on 2026-08-30.

The slice proved Android Material 3 UI, shared Kotlin behavior, SQLDelight persistence and basic phone/tablet usability.

---

## Phase 4 — MVP Buildout

**Status:** Current.

Phase 4 began with the Android character data foundation and expanded into a deliberate **Character Foundation Closure** cycle before DM-focused implementation.

### Phase 4A — Character Foundation Closure

**Status:** implementation broadly complete; current debug baseline consolidated into `main`; owner-driven UX/repair cycle remains open; formal acceptance deferred.

D-0047 remains the controlling character-closure scope. It includes retained QA fixes, F01–F18, D01–D18, I01–I22, official/custom class/subclass identity, all six approved conditional modules, Gestión, PC Settings, Supercompact/Table mode, backup/import and first-class phone/tablet intent.

Implemented general character surfaces include:

- General;
- Habilidades;
- Combate;
- Gestión;
- Equipo / Monedas;
- Trasfondo;
- Rasgos;
- conditional Conjuros;
- Notas.

Implemented conditional reusable modules:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

Historical implementation/audit lines A1–L and M1–M5 plus the focused pre-QA repair Pass 03–07 remain durable evidence.

D-0066 changed the **repository merge boundary**, not the acceptance boundary: the owner explicitly approved promoting the current in-progress Phase 4 state into canonical `main` to eliminate branch sprawl and make one coherent development baseline.

Therefore:

- `main` now contains the canonical current Phase 4 development reality;
- the current baseline is still debug/pre-QA and known to have defects;
- old implementation/tmp branches are historical, not alternate current baselines;
- future repair work starts from `main` on a focused branch;
- presence on `main` does not satisfy the Phase 4A exit criterion.

Current latest technically verified product identity:

- version `0.4.0-preqa.7` / build `40700` / `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

The owner phone audition has sufficiently covered Stages A–F for this build and produced a substantial repair backlog rather than acceptance. No physical owner tablet acceptance has been completed.

Current next step is **not** more speculative testing of build `40700`. The owner is compiling additional non-QA observations to combine with the existing audition backlog. After those are recorded, one coherent successor repair batch should be designed and implemented from canonical `main`.

### Phase 4A exit criterion

Phase 4A is complete only when:

- D-0047 implementation is present — **done**;
- migrations/persistence and automated gates are green at the relevant tested build — **done for build 40700**;
- current owner phone-audition findings are durably recorded — **done for the current build**;
- additional owner observations intended for the same repair cycle are captured/reconciled — **pending**;
- the known cross-cutting repair families are implemented and automatically verified in a successor build — **pending**;
- phone retesting demonstrates the repaired baseline is acceptable — **pending**;
- tablet portrait/landscape acceptance is completed on an owner device or another explicitly approved acceptance method — **pending**;
- one exact replacement formal M6 candidate is explicitly frozen when ready — **pending**;
- formal owner QA passes, including upgrade/persistence and relevant regression coverage — **pending**;
- blocking findings are resolved — **pending**;
- the owner explicitly accepts/closes Phase 4A — **pending**.

The repository is already consolidated into `main` under D-0066, so "merge to main" is no longer itself an exit criterion. **Acceptance and closure remain separate.**

**No DM-feature implementation begins before Phase 4A acceptance/closure.**

### Phase 4B — DM combat / live-session work

**Status:** Blocked by Phase 4A owner acceptance and closure.

This stage will consume the stable reusable character data foundation only after Phase 4A is explicitly closed. Its detailed implementation must not begin merely because architecture/product ideas already exist.

The durable character sheet and future live combat state remain separate under D-0025/D-0026.

---

## Phase 5 — MVP Hardening

**Goal:** make the first release dependable enough for real use.

Potential areas, only as observed/needed:

- regression testing;
- real phone/tablet usability;
- desktop administration workflows;
- multicampaign isolation;
- local-first/offline/reconnection behavior;
- PDF/export verification;
- accessibility;
- data migration/recovery;
- performance where measured to matter;
- crash handling;
- proportionate privacy/security review;
- packaging/release process.

---

## Phase 6 — Post-MVP Evolution

**Goal:** add features based on actual priorities/usage while preserving continuity.

Possible later directions include broader Android/desktop parity, player desktop, desktop combat, co-DMs, explicit DM-device combat handoff, house-rule-aware clarification, realtime transport if proven useful, and other owner-approved expansions.
