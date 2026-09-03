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

Phase 4 began with the Android character data foundation. That work expanded substantially through iterative owner QA and is now in a deliberate **Character Foundation Closure** cycle before any DM-feature implementation begins.

### Phase 4A — Character Foundation Closure

**Status:** In progress on `implementation/phase4-character-closure`.

The owner explicitly approved D-0047: one substantial final character-stage package combining retained QA fixes, new character functionality, UX/design improvements, official class/subclass identity including Artificer and supplemental official material, conditional reusable class modules, and first-class phone/tablet behavior.

Current general character surfaces include/target:

- General;
- Habilidades;
- Combate;
- Equipo;
- Trasfondo;
- Rasgos;
- conditional Conjuros;
- Notas;
- Gestión.

Conditional reusable modules identified by audit:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

The closure also includes the approved F01–F18, D01–D18 and I01–I22 sets recorded in D-0047, including global IME/action consistency, responsive phone/tablet layouts, Supercompact, Table mode, resources/conditions/rest management, structured defenses/senses/proficiencies, richer equipment/spell/trait workflows and own-format local backup/import.

The complete execution sequence is broken into recoverable batches under:

`docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md`

### Phase 4A exit criterion

Character closure is complete only when:

- D-0047 implementation is complete;
- migrations/persistence and automated gates are green;
- one exact closure APK candidate is recorded;
- owner QA passes on phone portrait/landscape and tablet portrait/landscape, including a representative larger text scale;
- blocking findings are resolved;
- continuity/governance housekeeping is complete;
- the owner explicitly accepts the result and approves the merge/closure.

**No DM-feature implementation begins before this exit criterion.**

### Phase 4B — DM combat / live-session work

**Status:** Blocked by Phase 4A.

This stage will consume the stable reusable character data foundation when Phase 4A is complete. Its detailed implementation work must not begin early merely because architecture/product ideas already exist.

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
