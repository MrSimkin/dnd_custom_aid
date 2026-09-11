# Project State

**Last verified:** 2026-09-11  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Current implementation boundary:** planned successor A–I plus post-A–I Player stabilization remain automated-green; owner phone QA of `preqa.8 / 40800` identified acceptance blockers  
**Current phase:** Phase 4A — owner design reconciliation before bounded acceptance repair  
**Current QA build:** `0.4.0-preqa.8` / `40800` / debug  
**Release status:** not owner-accepted; not release-ready  
**Owner phone QA:** consolidated pass completed through portrait, landscape and representative larger text  
**Player tablet QA:** intentionally deferred until shared/systemic findings are repaired  
**Current work rule:** discuss QA findings one point at a time; consolidate every closed point before moving on  
**DM work:** design/discovery may remain documented; implementation is blocked until explicit Phase 4A closure

## 1. Canonical repository reality

`main` remains the canonical repository baseline under D-0066 but is older than the live successor acceptance line. The active continuation branch contains the current A–I successor implementation, post-A–I stabilization, `preqa.8 / 40800` owner-QA evidence, the point-by-point repair-decision log and future-DM design documentation.

Use:

- `docs/checkpoints/LATEST.md` for the exact continuation position;
- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md` for the controlling owner/device QA findings and repair scope;
- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` for individually closed repair decisions;
- `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md` for exact `preqa.8 / 40800` product/audit/full-gate evidence;
- `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md` for future DM design truth only;
- `docs/TESTING.md` for the acceptance contract.

Automated-green, documented or canonical does not mean owner-accepted or release-ready.

Build `40700` is historical evidence only and is not the current QA target or resume point.

## 2. Successor implementation status

The planned A–I implementation sequence remains complete/automated-green:

- A — schema/domain/storage foundation;
- B — shared UX/responsive primitives;
- C — character-first navigation + PC Settings + General/Habilidades;
- D — Combat + Dice;
- E — Gestión + Markers + Resources + recovery/conditions;
- F — Conjuros compact source-context redesign;
- G — Equipo/Rasgos/conditional modules/Notas/Trasfondo;
- H — full-screen Application Settings/live previews/themes;
- I — separate tablet portrait/landscape redesign;
- post-A–I stabilization — Rasgos provenance, reorder fallback, Settings/font/theme refinements, app-wide multiline density and residual audit.

The owner/device QA of `preqa.8 / 40800` demonstrated that several automated-green assumptions did not satisfy the intended interaction contract. The next implementation will therefore be a **bounded acceptance-repair pass**, but implementation is not yet the immediate step.

There is no authorized Player Increment J and no authorized DM implementation increment.

## 3. Latest technically verified build

Current consolidated Player QA build:

- version `0.4.0-preqa.8`;
- build `40800`;
- type `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- workflow `34430548061` — **SUCCESS**;
- artifact ID `10134364621` / `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:b7ead12a7501bbef96f861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK digest `sha256:bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

This remains the latest technically verified build. It is **not** an accepted M6 candidate.

## 4. Owner/device QA completed on preqa.8

The owner physically verified meaningful PASS boundaries including update-in-place/data preservation, persistence, phone portrait baseline navigation, representative editor/IME operations, currency, Conjuros portrait, normal Notas use, Application Settings functionality, representative conditional modules, backup/export, phone-landscape retention of the phone interaction model and representative larger application text.

The same pass found acceptance blockers involving canonical HP synchronization, Table mode activation, high-frequency damage/healing UX, Combat viewport footprint, direct reorder interaction, Rasgos provenance, Supercompact concept, short-height sticky/fixed behavior, Trasfondo photo regression, shared editor sizing, attack-damage editing, PC Settings presentation, compactness proportionality, theme presentation, custom-skill geometry and minor help-icon alignment.

The authoritative detailed findings are in `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`.

## 5. Current design-reconciliation protocol

The owner requires the QA findings to be handled **one point at a time**, not grouped during discussion.

For every point:

1. inspect the exact QA observation and relevant current implementation/context;
2. discuss only that point in enough detail to remove implementation ambiguity;
3. identify phone/tablet/shared implications where they belong to that point;
4. once the owner explicitly closes the point, record the approved behavior, constraints, rejected alternatives and required regression boundary in `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`;
5. only then move to the next point.

A point may have transversal implementation consequences without being merged conceptually with other open findings.

Do not begin the acceptance-repair implementation pass until the owner has completed the required point-by-point reconciliation or explicitly changes this instruction.

## 6. Cross-device repair rule

Physical tablet portrait/landscape QA has **not yet been performed** on `preqa.8`.

Shared/systemic defects found on phone are repair requirements across phone and tablet wherever they share state, component, primitive, spacing policy, special-mode logic or product concept.

Evidence must remain precise:

- **repair scope:** phone + tablet for shared/systemic causes;
- **physical evidence:** phone only so far;
- **tablet acceptance:** still pending and must be performed on the repaired build.

## 7. Automated-boundary corrections required

Owner QA exposed areas where static/model validation was too weak. The next repair pass must add stronger validation for actual behavior, especially:

- HP propagation across General/Combate/shared surfaces;
- real Table-mode activation from a clean persisted state;
- Rasgos no-retyping structured-origin flow;
- direct normal-layout drag-and-drop reorder behavior;
- short-height phone-landscape sticky/fixed viewport behavior.

Additional closed points may add their own focused regression requirements to the repair-decision log.

## 8. Future DM boundary

D-0068 remains future Phase 4B design truth. No DM product code was implemented during Player QA.

No DM implementation begins before explicit Phase 4A closure.

## 9. Protected owner directions

These remain controlling:

- one datum / one canonical state;
- compact compatible controls rather than unnecessary vertical stacks;
- reduce unnecessary margins/padding without shrinking required touch targets;
- responsive policy must consider available height as well as width;
- phone landscape remains a phone interaction model, not tablet UI;
- tablet portrait and landscape remain independently designed first-class compositions;
- shared/systemic defects are repaired across phone and tablet, not patched only where first observed;
- direct drag-and-drop in the normal layout is the reorder target;
- known structured Rasgos provenance comes from canonical character data; `Otro` and `Don` may be free text;
- restore preferred old UX when persistence was the actual requested change;
- Supercompact should read like a dense PC stat block inspired by the 5.5e monster/NPC reading grammar;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- contextual help remains `Siempre visible` / `ⓘ / tooltip` / `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- no DM implementation before explicit Phase 4A closure.

## 10. Exact next position

Next project session:

1. read `docs/checkpoints/LATEST.md`;
2. read `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`;
3. read `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`;
4. discuss the next unresolved `40800` QA point with the owner, by itself;
5. when explicitly closed, consolidate it in the repair-decision log;
6. repeat point by point until the required design reconciliation is complete;
7. then define and implement the bounded acceptance-repair plan;
8. strengthen the exact automated boundaries required by the closed decisions;
9. produce the next monotonic successor QA build;
10. perform targeted phone retest of repaired blockers;
11. once shared/systemic phone issues are acceptable, perform tablet portrait and tablet landscape physical QA on the repaired build;
12. freeze the replacement formal M6 candidate only after owner-audited behavior is acceptable;
13. complete final regression/upgrade acceptance;
14. explicitly close Phase 4A;
15. only then may DM implementation begin.
