# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current physical-evidence candidate:** `0.4.0-preqa.11 / 41100` at `21dc2b0eed4afc261b89578da424cd28d9894500` — AUTOMATION GREEN; FOCUSED OWNER RECHECK 1–7 + 9 PASS / 8 FAIL  
**Current phase:** transversal full-app equivalent-control vertical padding/spacing audit + repair, including `+ / −` selector inspection  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work is inside the durable P1–P17 repair/validation authorization. No P18 exists.

## Current controlling evidence

Read first:

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md` — **current physical owner evidence and exact next repair boundary**;
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_QA_CANDIDATE.md` — exact candidate/run/artifact/digest evidence and partial physical result;
3. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md` — engineering source repair chain that produced preqa.11;
4. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling P4/P5 and related accepted repair principles;
5. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md` — P16 full-app usable-height/combined-footprint contract;
6. `docs/checkpoints/LATEST.md`.

## Preserved physical PASS — do not restart

Earlier `preqa.10` R1–R3 PASS remains valid: canonical General↔Combate HP propagation, max clamp, targeted HP feedback, exact `Establecer PV`, and the prior gross Combat operation-row repair.

On `preqa.11`, the owner physically reported **1–7 OK and 9 OK** for the focused P16/P4 recheck. This physically clears, for the tested scope:

- portrait Combat `Cantidad` excessive-padding defect;
- phone-landscape header/tab/HUD combined-footprint defect;
- portrait↔landscape rotation sanity;
- standard die selection;
- `Otro…` custom die sides;
- incomplete quantity/sides/signed-modifier editing without state wipe;
- numeric clipping/visibility defect;
- Save/Cancel and persistence of a valid edited damage component.

Do not rerun those checks absent contradictory evidence or a later repair that directly changes their relevant control/behavior.

## Current physical FAIL — transversal geometry audit reopened

Focused check 8 failed. The owner supplied an annotated portrait screenshot of `Editar ataque o acción` showing multiple controls with excessive internal vertical padding / empty vertical space relative to their contents and clarified that the same class applies in **portrait and landscape**.

This is not an isolated P4-dialog defect. The owner had previously required a **full-app size/margin/padding audit**, so continued recurrence in equivalent controls means that audit was incomplete in coverage or ineffective in outcome. The next repair must therefore audit and correct **every equivalent/shared Player object and usage across the app**, not just the screenshot surface.

Repair constraints:

- reduce/correct unnecessary internal vertical padding and margins first;
- preserve legibility and safe touch targets;
- adjust outer box/control size only if still necessary afterward;
- do not solve by enlarging dialogs/containers;
- enforce coherent proportional geometry across portrait and landscape through shared primitives/policies where possible rather than one-off screen patches.

The owner also explicitly requested inspection of the structured-dice **`+ / −` selector** for glyph/icon choice, padding, alignment, touch-target geometry and consistency with equivalent compact controls. This is an audit item, not permission to invent a redesign without source comparison.

This reopens the transversal presentation/usable-height boundary under the accepted P5/P16 principles and any relevant shared editor/P9 geometry primitives discovered during source inspection. Exact source mapping must be established before implementation.

## Candidate evidence

`preqa.11 / 41100` remains the current physical-evidence candidate:

- exact candidate commit `21dc2b0eed4afc261b89578da424cd28d9894500`;
- Scaffold `34732621381` — **SUCCESS**;
- artifact `10309348779`;
- ZIP digest `sha256:752302327bc4af330f5a1ea6e11b6ad61e4324e8cc230675f6d8d2da48fff391`;
- independent APK SHA-256 `1eebaffbed5e2f0d4479f288575186a916dde0a124e26e72948144bda032ff3a`.

It is **not** Phase 4A accepted because the transversal spacing/padding boundary remains physically failed.

## Exact next engineering action

Do not continue broad phone QA and do not start P17 tablet QA yet.

1. inventory shared/equivalent Player UI controls across the full app that can produce the observed excessive vertical padding/margins;
2. identify shared primitives and one-off usages that escaped/defeated the earlier audit;
3. inspect the `+ / −` structured-dice selector alongside equivalent compact selector/action controls;
4. define and implement the smallest shared repair that enforces proportional geometry in portrait and landscape while keeping safe touch targets and existing accepted semantics;
5. update the current QA checkpoint and live continuity after each material repair step;
6. run focused regression plus aggregate Scaffold validation;
7. if product code changes materially, advance monotonically after `preqa.11 / 41100`, freeze exact candidate/run/artifact/digest evidence, and ask the owner to recheck only the affected transversal geometry boundary before broader phone/tablet QA.

Portrait relocation of long-card action buttons remains only a prior design consideration, not an approved automatic change. P17 tablet QA remains pending. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.
