# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current physical-evidence candidate:** `0.4.0-preqa.11 / 41100` at `21dc2b0eed4afc261b89578da424cd28d9894500` — AUTOMATION GREEN; OWNER FOCUSED RECHECK 1–7 + 9 PASS / 8 FAIL  
**Acceptance boundary:** full-app equivalent-control vertical padding/spacing audit + repair → focused geometry recheck → remaining phone/tablet evidence  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md` — **current owner physical evidence, transversal failure and exact continuation point**.
2. `docs/PROJECT_STATE.md` — live Player authority/current repair gate.
3. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_QA_CANDIDATE.md` — exact preqa.11 candidate/run/artifact/digest evidence and partial physical result.
4. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md` — product repair chain that produced preqa.11.
5. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling accepted P4/P5 and related repair principles.
6. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md` — controlling full-app vertical-space/combined-footprint policy.
7. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable repair/validation authorization.

## Physical PASS preserved — do not repeat from scratch

Earlier `preqa.10` R1–R3 remains PASS.

On `preqa.11`, the owner reported **checks 1–7 OK and 9 OK**. Preserve these physical passes for their tested scope:

- portrait `Cantidad` padding/height repair;
- phone-landscape combined header/tab/HUD footprint;
- portrait↔landscape rotation sanity;
- standard die selection;
- `Otro…` custom die editing;
- incomplete dice draft editing without neighboring-state wipe;
- numeric visibility/no clipping;
- Save/Cancel and valid component persistence.

## Current blocker — check 8 FAIL / transversal full-app audit reopened

The owner supplied an annotated portrait `Editar ataque o acción` screenshot showing multiple equivalent controls with excessive vertical internal padding / empty space and clarified that this class of defect also applies in landscape.

This must **not** be repaired only in that dialog. The owner had previously required a full-app size/margin/padding audit; recurrence across equivalent objects means the earlier audit was incomplete or ineffective. The next pass must inventory and correct equivalent/shared Player controls across the app.

Required repair policy:

- vertical padding/margins first;
- preserve usable touch targets and legibility;
- outer size only afterward if genuinely required;
- no solution based on making dialogs/containers larger;
- prefer shared primitives/policies over isolated per-screen tweaks;
- cover both portrait and landscape.

The structured dice **`+ / −` selector** is explicitly included for inspection of glyph/icon choice, padding, alignment, touch-target geometry and consistency with equivalent compact controls. Do not infer a redesign until comparable controls/source have been audited.

## Exact next action

Pause broad owner QA and P17 tablet QA. On `implementation/phase4a-successor-cycle`:

1. inventory all shared/equivalent Player form/editor/selector controls capable of the observed excessive vertical padding/margins;
2. determine what shared primitives/usages escaped the earlier full-app audit;
3. include the `+ / −` dice selector in the audit;
4. implement the smallest transversal repair consistent with P5/P16 usable-height principles and relevant shared editor/P9 geometry contracts;
5. update `2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md`, `PROJECT_STATE.md` and this `LATEST.md` after each material repair step;
6. run focused regression + aggregate Scaffold validation;
7. if product code changes materially, issue the next monotonic candidate after `preqa.11 / 41100`, freeze exact commit/run/artifact/digest evidence, and recheck only the affected geometry boundary before broader phone/tablet QA.

No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure. Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change.
