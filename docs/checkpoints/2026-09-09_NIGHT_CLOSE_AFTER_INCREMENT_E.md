# Night-close continuity — Phase 4A after Increment E

**Date:** 2026-09-09  
**Purpose:** stop-for-the-night checkpoint after completing Increment E, consolidating repository state, and leaving one exact continuation path  
**Canonical branch:** `main`  
**Continuation branch:** `implementation/phase4a-successor-cycle`  
**Owner acceptance:** NOT YET PERFORMED on the successor implementation  
**Release status:** debug/development; not release-ready; not formal M6

## 1. What was completed tonight

The repository successor cycle is complete through **Increment E**.

Completed/green increments:

- A — schema/domain/storage foundation;
- B — shared UX/responsive primitives;
- C — character-first navigation + PC Settings + General/Habilidades;
- D — Combat + Dice;
- E — Gestión + Markers + Resources + recovery/conditions.

Remaining planned increments:

- F — Conjuros compact source-context redesign;
- G — Equipo/Rasgos/conditional modules/Notas/Trasfondo;
- H — full-screen Application Settings/live previews/themes;
- I — separate tablet portrait/landscape redesign.

There are **four increments left**.

## 2. Increment E final evidence

Active Gestión successor wiring source:

`4b3ab53faada5af7b50f73ce951fe767c13ff63a`

Authoritative integrated validation commit:

`0587db5e65d89e809f138e83d053903659216886`

Workflow:

`34307068166` — **SUCCESS**

Passed together:

- backend/type-check;
- shared/Kotlin tests;
- Android debug compilation/assembly;
- desktop compilation/build;
- Android debug APK upload.

Artifact:

- ID `10087074946`;
- name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:55bba08d09918a3f6102e4e2694da50c0ee5bb6e9bf3b1ac4c8849f9203ac50`.

Important: this is automated development verification, **not owner visual/device acceptance**.

## 3. Increment E delivered behavior

- compact fixed Gestión operational surface;
- one-row death saves when PG is zero;
- canonical Inspiration projection consistent with General;
- explicit warning when General has unsaved HP draft values while Gestión is operating persisted state;
- no hidden save of unrelated General edits;
- live Custom Marker controls from canonical successor state;
- Resources remain a distinct concept but share tracker/recovery mechanics;
- Resource presentation placement can include General / Gestión / Equipo / Rasgos from one resource identity/value;
- Gestión shows placement-eligible resources;
- mixed Resource + Marker rest preview/apply uses typed identities and structured recovery only;
- legacy/free/manual recovery text is review-only;
- binary/current-max clamping and selected-only application are covered by tests;
- custom condition path remains available;
- predefined condition-catalog infrastructure supports source identity/help but contains no unapproved corpus text;
- Sandy Petersen Cthulhu Mythos content architecture remains supported without transcribing proprietary Spanish text;
- concentration help explains Constitution save, DC 10 or half damage received, whichever is higher, through the global contextual-help mode.

## 4. Owner decisions that remain controlling

Do not rediscover or dilute these on resume:

- one datum / one canonical state;
- app-wide compactness and row-efficiency rules;
- margins/padding should be reduced globally while preserving practical touch targets;
- card reorder should use whole-card long-press/drag where safe;
- movement feel is still pending owner-device judgment and may need geometry/sibling-motion refinement;
- `Raza`, never `Especie/raza`;
- `Electrum`, never `Electro`;
- Spanish class/subclass presentation;
- no pointless 5e/5.5e UI distinction without functional rules matching;
- contextual explanations are valuable and use `Siempre visible` / `ⓘ / tooltip` / `Oculto` from one canonical help text;
- provenance uses `Tipo de origen | Origen específico`, default `Clase`, only where it is semantically useful;
- generic `Fuente` schema leakage should not return;
- functional Conjuros source associations remain real and may be class-linked or custom;
- phone landscape is a phone interaction model;
- tablet/wide UX is a separate redesign target and is not a fallback for phone landscape.

## 5. Latest owner-auditioned build remains old

Do not confuse the new green development artifacts with owner acceptance.

Latest owner-auditioned practical build:

- `0.4.0-preqa.7` / build `40700` / debug;
- product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714`;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Its Stage A–F findings remain canonical input. Do not rerun that APK screen by screen.

## 6. Repository night-close consolidation

Owner explicitly requested repository ordering after finishing E.

The night-close operation must leave:

- `main` and `implementation/phase4a-successor-cycle` aligned at the same completed-E development baseline by normal fast-forward;
- no history rewrite;
- a durable archive of branch name → final SHA for deleted obsolete refs;
- obsolete non-frozen `tmp/*` refs removed from the visible branch list;
- `implementation/phase4a-successor-cycle-temp-invalid` removed after archival because it is an ancestor/superseded ref with no unique current work;
- `tmp/phase4-l-frozen-qa-candidate` preserved;
- `tmp/phase4-m5-frozen-qa-candidate` preserved;
- meaningful historical milestone branches preserved as labels;
- no temporary night-close workflow left in the final tree.

Branch archive target:

`docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md`

## 7. Exact resume point for tomorrow

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. this night-close checkpoint;
3. `docs/PROJECT_STATE.md`;
4. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md`, Increment F;
5. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_E_MANAGEMENT_RECOVERY.md` only if E implementation details are needed.

Then continue on:

`implementation/phase4a-successor-cycle`

with **Increment F — Conjuros compact source-context redesign**.

Protected F target:

`Mago (INT) · CD 15 · Ataque +7   ▾   Buscar   Filtros 2   +`

Conceptually one compact sticky context bar—not necessarily those exact icons/abbreviations.

Requirements:

- selected source/class context is immediately clear;
- ability/DC/attack visibly belong to that source;
- `Todos los conjuros` remains compact;
- search/filter/source details expand transiently;
- sticky level/slot information remains useful;
- spell cards remain visible in phone portrait and especially phone landscape;
- do not switch phone landscape to the old tablet layout.

After F, produce the planned early targeted Redmi Note 11 Pro 5G portrait + landscape interaction build/audition before blindly proceeding through all remaining surfaces.

## 8. Stop condition

No DM implementation. No formal M6 freeze. No claim of owner acceptance.

Tomorrow resumes with F only after verifying the repository heads/continuity if necessary.
