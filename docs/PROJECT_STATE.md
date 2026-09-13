# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Latest physically tested candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7` — FAILED SHARED HP/UX ACCEPTANCE BOUNDARY  
**Current phase:** P1/P2 + transversal presentation consistency reopened by physical owner/device QA; canonical HP repair layer implemented, remaining UI/presentation repair and validation pending  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## 1. Branch authority

This branch contains the current Player implementation and all Phase 4A physical-QA-driven repairs. `main` is intentionally divergent and contains later global/Phase 5A/DM product-discovery records that are not on this branch. `main` must not be mistaken for the latest Player runtime, and this branch must not overwrite valid later discovery work on `main`.

Cross-branch authority is documented in `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` and `docs/BRANCH_STATUS.md`.

## 2. Authorization boundary

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` records the owner's durable authorization for the accepted `preqa.8 / 40800` P1–P17 Player repair cycle. That authorization permits repairs reopened by real QA evidence, their validation, QA packaging and checkpoints.

The current repair does not require a new P-number or new authorization. It is a bounded reopening of already-authorized accepted behavior.

## 3. Physical `preqa.9` evidence

### Preserved PASS

The owner physically confirmed update-in-place/persistence sanity for `preqa.9 / 40900`: launch, campaign/character preservation, representative saved data and full reopen all passed.

### Shared/blocking FAIL

The next physical boundary exposed defects that automated testing had not caught:

- General HP editing does not live-propagate to Combate without an explicit `Guardar`;
- reducing maximum HP does not clamp current HP; physical observation included invalid Combate display `20/10`;
- agreed subtle changed-state HP feedback is absent;
- Combat `Establecer PV` is ineffective for current HP while temporary-HP exact correction works;
- `Daño — Cantidad — Curar` is out of proportion with surrounding Combate UI, reopening the transversal size/margin/padding consistency boundary.

Preserved passes: max-HP increase without silent healing, damage/temp-HP arithmetic, healing cap, amount clearing and Combat-operation-to-General projection.

Exact owner/device evidence: `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

## 4. Repair status

### R1 — canonical HP state boundary — implemented, validation pending

- `e0397146445c2cd78e7d017943bca1eb76101939` — shared exact hit-point update helpers now enforce non-negative max HP, `0 <= current <= max`, no silent healing on max increase, and clamp-on-max-reduction semantics.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge now preserves proposed max HP and clamps proposed current HP against that same canonical maximum. The prior implementation silently discarded proposed max HP and normalized current HP against the old persisted maximum.

This directly repairs the identified source-level cause of Combat exact-current/max HP correction being lost. R1 is not yet automation-qualified or physically accepted.

### Still open

- General HP fields remain draft-only and therefore still require UI wiring for no-global-`Guardar` canonical propagation;
- normal structural save path still requires canonical HP normalization;
- changed-state HP glow/pulse remains to be implemented;
- transversal presentation consistency remains to be classified against historical audit and repaired;
- regression tests, focused validation, aggregate validation and new QA packaging remain pending.

P3–P16 remain historically implemented/automation-qualified unless later physical QA specifically reopens them. P17 tablet physical QA remains PAUSED while these shared defects are open.

## 5. Failed candidate identity and historical automated proof

`preqa.9 / 40900`:

- candidate commit: `cd0c203d337c062fa388010d300e875f2f54ced7`;
- normal Scaffold run: `34726572588` — SUCCESS;
- artifact ID: `10307444450`;
- artifact digest: `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

CI remains valid for its tested scope; physical QA showed that scope did not cover all required interaction/invariant behavior.

## 6. Current repair gate

Before physical QA resumes:

1. add regression coverage for the canonical HP helper/merge repair;
2. implement General field-level canonical commit without a global Save requirement while preserving transient typing drafts;
3. normalize HP through the normal structural save route;
4. implement/verify changed-state feedback and transversal presentation consistency;
5. run focused and aggregate validation;
6. assign a new monotonic QA identity and artifact after green material code changes;
7. physically recheck the reopened phone boundary.

Representative P17 tablet QA may resume only after the hard shared defects no longer make tablet acceptance evidence misleading.

## 7. Global/DM line

Current Phase 5A/DM discovery decisions live on `main`. Preserve them during future integration. DM implementation remains blocked until Phase 4A is physically accepted and explicitly closed.

## 8. Exact continuation point

Resume from `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

Do not create P18 or unrelated Player features. Complete the bounded repair and validation, then produce the next monotonic APK for owner retest.
