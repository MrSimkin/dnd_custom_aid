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

Phase 4 began with the Android character data foundation. That work expanded substantially through iterative owner QA and entered a deliberate **Character Foundation Closure** cycle before any DM-feature implementation begins.

### Phase 4A — Character Foundation Closure

**Status:** Closure implementation + focused pre-QA repair line technically stable; **owner visual audition pending; formal M6 deferred**.

Active owner-requested pre-QA repair branch:

`implementation/phase4-preqa-ux-repair`

The owner approved D-0047: one substantial final character-stage package combining retained QA fixes, new character functionality, UX/design improvements, official class/subclass identity including Artificer and supplemental official material, conditional reusable class modules, and first-class phone/tablet behavior.

Implemented general character surfaces:

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

The closure includes the approved F01–F18, D01–D18 and I01–I22 sets recorded in D-0047, including global IME/action consistency, responsive phone/tablet layouts, Supercompact, Table mode, resources/conditions/rest management, structured defenses/senses/proficiencies, richer equipment/spell/trait workflows and own-format local backup/import.

Historical M1–M5 audits/consolidation are complete. After reviewing that candidate, the owner explicitly reopened pre-QA implementation. Focused UX repair Pass 03–07 is now technically green and stable.

Current owner-audition identity:

- version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

This review build is **not a frozen formal M6 candidate**. The next gate is the staged phone/tablet visual audition in `docs/PREQA_OWNER_VISUAL_AUDITION.md`. Only after the owner explicitly declares a build ready will one exact replacement candidate be frozen and formal M6 resume.

Current practical resume pointer:

`docs/checkpoints/LATEST.md`

### Phase 4A exit criterion

Character closure is complete only when:

- D-0047 implementation is complete — **done**;
- migrations/persistence and automated gates are green — **done**;
- focused automated pre-QA repair audits are green through Pass 07 — **done**;
- current-stage governance prose is reconciled through the Pass 07 state — **done**;
- owner staged visual audition on phone/tablet identifies an acceptable baseline with no unresolved blocking visual/IME/layout findings — **pending**;
- one exact replacement formal M6 candidate is explicitly frozen after owner readiness — **pending**;
- formal owner QA passes on phone portrait/landscape and tablet portrait/landscape, including representative larger text — **pending**;
- blocking findings are resolved — pending only if audition/QA finds any;
- the unique-commit/merge-boundary audit is complete — pending post-QA;
- the owner explicitly accepts the result and approves the merge/closure — pending.

**No DM-feature implementation begins before this exit criterion.**

### Phase 4B — DM combat / live-session work

**Status:** Blocked by Phase 4A owner acceptance and closure.

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
