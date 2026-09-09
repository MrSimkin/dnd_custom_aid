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

Phase 4 expanded into a deliberate **Character Foundation Closure** cycle before DM-focused implementation.

### Phase 4A — Character Foundation Closure

**Status:** successor repair/refinement implementation in progress; increments A–E complete/green; formal owner acceptance deferred.

D-0047 remains the controlling broad character-closure scope. D-0066 controls repository consolidation. D-0067 plus the September 2026 owner audition/refinement package controls the current repair/refinement direction.

The repository may consolidate technically verified development boundaries into `main` before acceptance when the owner explicitly requests repository ordering. **Presence on `main` is not a Phase 4A acceptance criterion.**

#### Successor implementation sequence

The current reconciled plan contains nine increments:

1. **A — schema/domain/storage foundation:** COMPLETE / GREEN;
2. **B — shared UX/responsive primitives:** COMPLETE / GREEN; drag feel pending owner-device judgment;
3. **C — character-first navigation + PC Settings + General/Habilidades:** COMPLETE / GREEN;
4. **D — Combat + Dice:** COMPLETE / GREEN;
5. **E — Gestión + Markers + Resources + recovery/conditions:** COMPLETE / GREEN;
6. **F — Conjuros compact source-context redesign:** NEXT;
7. **G — Equipo/Rasgos/conditional modules/Notas/Trasfondo:** pending;
8. **H — full-screen Application Settings/live previews/themes:** pending;
9. **I — separate tablet portrait/landscape redesign:** pending.

Four increments remain: **F, G, H and I**.

#### Latest automated successor boundary

Increment E final integrated validation:

- active Gestión wiring source commit `4b3ab53faada5af7b50f73ce951fe767c13ff63a`;
- validation commit `0587db5e65d89e809f138e83d053903659216886`;
- workflow `34307068166` — SUCCESS;
- artifact `10087074946` / `dnd-custom-aid-debug-apk`;
- ZIP digest `55bba08d09918a3f6102e4e2694da50c0ee5bb6e9bf3b1ac4c8849f9203ac50`.

This is a development verification boundary, not an owner-auditioned build and not a formal M6 candidate.

#### Latest owner-auditioned practical build

- version `0.4.0-preqa.7` / build `40700` / `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

The owner phone audition sufficiently covered Stages A–F for that build and produced a repair backlog rather than acceptance. No physical owner tablet acceptance has been completed.

#### Next build/audition boundary

After Increment F, produce the planned **interaction build** for targeted Redmi Note 11 Pro 5G portrait + landscape retest of the highest-risk repaired flows, especially:

- one-row Conjuros source-context bar and usable spell viewport;
- phone-landscape spell visibility;
- representative editor/keyboard behavior;
- representative whole-card drag feel;
- Gestión operational/death-save footprint;
- Habilidades passive row;
- 40% spacing option.

Do not repeat the full build-40700 audition screen by screen.

### Phase 4A exit criterion

Phase 4A is complete only when:

- D-0047 implementation exists — **done**;
- successor schema/domain/storage and current automated gates are green — **done through Increment E**;
- owner observations and cross-cutting directions are durably captured — **done for the current package**;
- remaining successor increments F–I are implemented and verified — **pending**;
- targeted owner phone retesting demonstrates the repaired baseline is acceptable — **pending**;
- tablet portrait/landscape acceptance is completed on a redesigned tablet surface — **pending**;
- one exact replacement formal M6 candidate is explicitly frozen when ready — **pending**;
- upgrade/persistence and relevant formal regression QA pass — **pending**;
- blocking findings are resolved — **pending**;
- the owner explicitly accepts/closes Phase 4A — **pending**.

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
