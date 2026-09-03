# Phase 4 closure scope approved + class/subclass audit checkpoint

**Date:** 2026-09-03  
**Canonical branch:** `main` — untouched  
**Durable Phase 4 line:** `implementation/character-data-foundation`  
**Focused closure branch:** `implementation/phase4-character-closure`

## Status

**OWNER-APPROVED PRODUCT/DESIGN SCOPE. CLASS/SUBCLASS MODULE AUDIT COMPLETE. IMPLEMENTATION MAP IS NEXT.**

This checkpoint supersedes `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_PROPOSAL_REVIEW.md` as the continuation point.

The owner explicitly approved:

- every previously retained owner requirement in that proposal checkpoint;
- all agent-originated F01–F18 features;
- all agent-originated D01–D18 design directions;
- all agent-originated I01–I22 improvements;
- F05 Rest assistance as part of a new general character-management surface (working Spanish label `Gestión`), with related live-maintenance functions considered there;
- D14 haptic feedback as configurable from PC Settings;
- D16 context preservation as a general behavior wherever technically feasible, not tablet-only;
- character lifecycle status moved from General to PC Settings;
- phone + tablet as explicit targets of the next APK, including portrait and landscape on both;
- full official class/subclass audit including Artificer and official supplemental material outside the SRD corpus;
- conditional reusable class/subclass modules rather than one permanent tab per class/subclass.

The authoritative detailed product decision is `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md`.
The class/subclass mapping is `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`.

## 1. Previous correction APK is no longer the acceptance target

Historical correction candidate:

- tested code commit: `6ae415d8919efb865d7b22092d95b94b3fa7866a`;
- workflow run: `33710347091`;
- artifact ID: `9876725270`.

The owner began checking that APK and identified enough remaining/global issues plus desired closure expansion that **no new focused acceptance QA will be performed against it**.

Artifact `9876725270` remains useful historical evidence only. Do not instruct the owner to resume the old nine-step correction retest by default.

A future closure APK must have a new tested commit, workflow run, artifact ID and hash/identity before owner QA begins.

## 2. Prototype implementation already present on focused branch

Before the owner stopped implementation to require product/design proposals first, preliminary data/schema/catalog work had already been committed on the focused closure branch.

Relevant prototype head before design checkpointing:

- `89aad12a094476c7b6798f6f0626bf978a5d0831`;
- GitHub Actions run `33779104922` — PASS.

This prototype includes preliminary closure-domain/schema work. Its green automated result proves only that those changes built/tested at that commit.

**It is not automatically accepted implementation.** Reuse it only after comparing it to D-0047 and the class/subclass audit. Refactor, extend or discard pieces that do not match the approved design.

## 3. Approved new general management surface

Working Spanish label: **`Gestión`**.

Approved responsibilities include:

- conditions + Exhaustion;
- Concentration;
- Short/Long Rest preview and selective recovery;
- generic Resources;
- end-of-session reconciliation checkpoints;
- temporary session effects;
- other closely related live-maintenance operations where appropriate.

The label itself may still be visually/linguistically refined during QA if another Spanish term communicates the purpose better. The functional grouping is approved.

## 4. Approved PC Settings consolidation

PC Settings should contain character-wide configuration, including:

- lifecycle status;
- spellcasting visibility;
- conditional-module visibility/overrides using hide-not-delete semantics;
- haptic feedback setting;
- Supercompact access/configuration;
- Table/read-only mode configuration;
- XP/Milestone mode where applicable;
- entry to global App Settings;
- other true sheet-configuration controls discovered during implementation.

Do not move ordinary live gameplay data such as HP/resources/conditions into PC Settings.

## 5. Approved conditional module set

Base/general surfaces:

- General;
- Habilidades;
- Combate;
- Equipo;
- Trasfondo;
- Rasgos;
- Conjuros when enabled/relevant;
- Notas;
- Gestión.

Conditional reusable modules:

- Artífice / Artifice;
- Formas / Forms;
- Técnicas / Techniques;
- Metamagia / Metamagic;
- Pactos / Pacts;
- Compañeros / Companions.

The module set for a multiclass character is the union of relevant triggers. Manual PC Settings override allows custom/homebrew classes/subclasses to enable any module. Hiding a module never deletes its data.

## 6. Official-source audit status

As of 2026-09-03 the audit includes:

- Player's Handbook 2024 classes/subclasses;
- the revised Artificer and five Forge of the Artificer subclasses;
- Forgotten Realms: Heroes of Faerûn subclasses;
- Ravenloft: The Horrors Within subclasses, including Reanimator Artificer;
- Arcana Unleashed subclasses. Current official D&D Beyond source content is already available as of 2026-09-01, so it is included despite earlier September 15 promotional release wording;
- legacy 2014-era official subclass families relevant to backward-compatible/manual representation.

Unearthed Arcana/playtest and partnered third-party material are not classified as released Wizards official catalog content, but remain representable through the manual/custom path.

## 7. Phone + tablet QA boundary

The next owner-QA APK is explicitly **not phone-only**.

Minimum device-layout matrix:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger text-scale sampling where practical.

Adaptive behavior must use available width/window constraints. Tablet should materially exploit width with more columns, navigation rail and master-detail behavior where useful rather than merely stretching phone components.

The experimental Supercompact view is part of this matrix and may be refined/rejected visually after owner QA without invalidating the underlying character data model.

## 8. D16 clarification

D16 is now a general UX invariant:

> When a user opens and closes a child editor/detail view, the application should preserve the meaningful parent context — selected tab/module, list position, filters/search/sort where appropriate, and selected item where useful — whenever technically feasible.

This applies on phone and tablet. Tablet master-detail is only one visible use of the broader rule.

## 9. Exact next action

Do **not** immediately resume coding from the old prototype.

Next:

1. reconcile `docs/PROJECT_STATE.md`, `docs/PRODUCT.md` and the decision index/log with D-0047;
2. produce a detailed **implementation map/gate plan** for the approved scope, including schema/migration increments, Android surfaces, responsive/tablet increments, class/module wiring and QA gates;
3. compare prototype code at/after `89aad12...` to that implementation map;
4. then resume implementation in reviewable increments on the focused branch;
5. run automated gates after consequential increments;
6. designate exactly one final closure APK for owner phone+tablet QA;
7. only after owner acceptance prepare Phase 4 merge proposal and then move into the DM stage.

`main` remains untouched until explicit owner merge approval.
