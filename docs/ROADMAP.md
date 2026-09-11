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

**Status:** A–I successor engineering and post-A–I stabilization are complete/automated-green. Consolidated owner phone QA of `0.4.0-preqa.8 / 40800` has been performed and **failed acceptance with bounded repair findings**. The project is now in **point-by-point QA design reconciliation before the acceptance-repair implementation pass**.

D-0047 remains the controlling broad character-closure scope. D-0066 controls repository consolidation. D-0067 plus the September 2026 owner audition/refinement package controls the repair/refinement direction. The controlling current QA evidence is `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`.

The repository may consolidate technically verified development boundaries into `main` before acceptance when the owner explicitly requests repository ordering. **Presence on `main` is not a Phase 4A acceptance criterion.**

#### Successor implementation sequence

The reconciled implementation plan contained nine increments:

1. **A — schema/domain/storage foundation:** COMPLETE / GREEN;
2. **B — shared UX/responsive primitives:** COMPLETE / GREEN;
3. **C — character-first navigation + PC Settings + General/Habilidades:** COMPLETE / GREEN;
4. **D — Combat + Dice:** COMPLETE / GREEN;
5. **E — Gestión + Markers + Resources + recovery/conditions:** COMPLETE / GREEN;
6. **F — Conjuros compact source-context redesign:** COMPLETE / GREEN;
7. **G — Equipo/Rasgos/conditional modules/Notas/Trasfondo:** COMPLETE / GREEN;
8. **H — full-screen Application Settings/live previews/themes:** COMPLETE / GREEN;
9. **I — separate tablet portrait/landscape redesign:** COMPLETE / GREEN.

There is **no planned Increment J**. After A–I, owner-audition findings drove a focused stabilization pass covering Rasgos provenance, multi-column reorder behavior, settings/font/theme refinement, app-wide multiline density and residual Player acceptance auditing.

#### Current consolidated owner-QA build

- version `0.4.0-preqa.8` / build `40800` / `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- authoritative validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- workflow `34430548061` — SUCCESS;
- artifact `10134364621` / `dnd-custom-aid-debug-apk`;
- ZIP digest `b7ead12a7501bbef96f861321b5bebfd64c631647423b8eab9faec9580699a`;
- APK SHA-256 `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

This is the latest technically verified and owner-audited Player build. It is **not** a frozen formal M6 candidate and **not** an accepted Phase 4A baseline.

The owner successfully completed the update-in-place/data-preservation test and a consolidated phone pass covering portrait, landscape and representative larger text. The pass established meaningful PASS evidence but also exposed acceptance blockers. Physical tablet QA is intentionally deferred until shared/systemic findings are repaired.

Build `40700` is historical evidence only and is not the current QA target.

#### Current design-reconciliation rule

Before implementation of the acceptance repair pass, review the `40800` QA findings **one point at a time** with the owner.

For each point:

- discuss the exact defect and intended interaction;
- resolve only the design details needed for that point;
- once closed by the owner, consolidate that decision durably in repository documentation;
- then move to the next point;
- do not collapse unrelated findings into one discussion package unless the owner explicitly requests it.

When the point-by-point discussion is complete, convert the closed decisions into a bounded acceptance-repair implementation plan.

### Phase 4A exit criterion

Phase 4A is complete only when:

- D-0047 implementation exists — **done**;
- successor A–I engineering exists and automated gates are green — **done**;
- post-audition Player stabilization is implemented and full-gate green — **done**;
- consolidated physical owner phone QA has been executed — **done for build 40800, but build not accepted**;
- current QA findings have been individually reconciled with the owner and converted into an accepted repair specification — **in progress**;
- blocking findings are repaired and targeted phone retest passes — **pending**;
- tablet portrait/landscape acceptance is completed on the repaired Player tablet surface — **pending**;
- one exact replacement formal M6 candidate is explicitly frozen when ready — **pending**;
- upgrade/persistence and relevant formal regression QA pass — **pending**;
- the owner explicitly accepts/closes Phase 4A — **pending**.

**No DM-feature implementation begins before Phase 4A acceptance/closure.**

DM-side discovery/design may be discussed and documented while Phase 4A remains open, but documentation is not implementation permission.

### Phase 4B — DM combat / live-session work

**Status:** Product/UX discovery baseline captured; implementation still blocked by Phase 4A owner acceptance and closure.

The detailed owner-approved discovery baseline is `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md`.

The current Phase 4B direction is a **private tablet-landscape DM Combat Desk**, not a VTT. Core confirmed requirements include:

- initiative remains always visible and independent from which stat block/reference is open;
- modular hide/show Reference Desk, Combat State, Encounter Notes/Rules of Engagement, Clocks, Markers and Quick Rules;
- individual PC/NPC/monster references plus first-class Party Overview and Creature Overview (tree vs forest);
- reusable creature definitions separated from live encounter instances;
- deliberate DM encounter overrides/improvisation for HP, AC, participants and actions without silently rewriting reusable definitions;
- quick official-rules checking as a memory aid, never forced rules validation;
- DM-private state by default; any future player-visible combat projection must be explicit/sanitized and its exact initiative model remains unresolved;
- explicit non-goals: grids, maps, tokens, automatic targeting/range, encounter balancing and VTT-style combat execution;
- live Combat Desk target is tablet landscape only; phone/portrait live-combat layouts are not required.

D-0068 also preserves wireframe candidates, reversibility/history ideas and the explicit open-question list that must be resolved before coding.

This stage will consume the stable reusable character data foundation only after Phase 4A is explicitly closed. The durable character sheet and future live combat state remain separate under D-0025/D-0026 and the later refinements captured in D-0068.

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
