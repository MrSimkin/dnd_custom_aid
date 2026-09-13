# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Latest physically tested candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7` — FAILED SHARED HP/UX ACCEPTANCE BOUNDARY  
**Current phase:** P1/P2 + transversal presentation consistency reopened by physical owner/device QA; bounded repair/revalidation in progress  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## 1. Branch authority

This branch contains the current Player implementation and all Phase 4A physical-QA-driven repairs. `main` is intentionally divergent and contains later global/Phase 5A/DM product-discovery records that are not on this branch. `main` must not be mistaken for the latest Player runtime, and this branch must not overwrite valid later discovery work on `main`.

Cross-branch authority is documented in:

- `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`;
- `docs/BRANCH_STATUS.md`.

## 2. Authorization boundary

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` records the owner's durable authorization for the accepted `preqa.8 / 40800` P1–P17 Player repair cycle.

That authorization explicitly permits repairs reopened by real QA evidence, their validation, QA packaging and checkpoints. It does not permit unrelated Player feature invention, DM implementation, destructive history rewriting, or claiming physical acceptance without actual owner/device QA.

The current P1/P2/presentation repair therefore does not require a new P-number or new authorization. It is a bounded reopening of already-authorized accepted behavior.

## 3. Physical `preqa.9` evidence

### Preserved PASS

The owner physically confirmed update-in-place/persistence sanity for `preqa.9 / 40900`: launch, campaign/character preservation, representative saved data and full reopen all passed.

### Shared/blocking FAIL

The next physical boundary exposed defects that automated testing had not caught:

- General HP editing does not live-propagate to Combate without an explicit `Guardar`, contrary to the owner-approved interaction;
- reducing maximum HP does not clamp current HP; the physical observation included an invalid Combate display of `20/10`;
- the agreed subtle changed-state HP feedback is absent;
- Combat `Establecer PV` is ineffective for current HP while temporary-HP exact correction works;
- the `Daño — Cantidad — Curar` row is out of proportion with the surrounding Combate UI, reopening the required transversal size/margin/padding consistency boundary.

Within that same test, max-HP increase without silent healing, damage/temp-HP arithmetic, healing cap, amount clearing and Combat-operation-to-General projection passed.

Exact owner/device evidence is recorded in `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

## 4. Repair status

Historical automation-qualified status remains evidence but no longer equals acceptance:

- **P1:** REOPENED by physical QA — canonical HP propagation/invariants;
- **P2:** REOPENED by physical QA — exact-current-HP correction and changed-state visual feedback;
- **transversal presentation consistency:** REOPENED by physical QA — exact historical P classification must be confirmed from accepted presentation audit before implementation; do not invent a new P number;
- P3–P16: remain historically implemented/automation-qualified unless later physical QA specifically reopens them;
- P17 design decision remains closed as a gate policy, but tablet physical QA is PAUSED while shared/systemic HP defects make acceptance evidence premature.

## 5. Failed candidate identity and historical automated proof

`preqa.9 / 40900`:

- candidate commit: `cd0c203d337c062fa388010d300e875f2f54ced7`;
- normal Scaffold run: `34726572588` — SUCCESS;
- artifact ID: `10307444450`;
- artifact digest: `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

The CI result remains true for its automated scope. The physical failures prove that the automated suite did not sufficiently cover the required interaction/invariant behavior. `preqa.9` must therefore remain historical physical evidence, not an acceptable Phase 4A baseline.

## 6. Current repair gate

Before physical QA resumes:

1. inspect the exact implementation paths and accepted P1/P2 + presentation-consistency records;
2. implement only the bounded reopened repairs;
3. add/strengthen regression tests for live General→canonical propagation, max-HP clamping, Combat exact-current-HP correction, changed-state feedback and the relevant shared presentation geometry;
4. run focused and aggregate validation;
5. assign a new monotonic QA identity if product code changes materially;
6. produce a new physical-QA artifact and recheck the reopened phone boundary.

Representative P17 tablet QA may resume only after the hard shared defects no longer make tablet acceptance evidence misleading.

## 7. Global/DM line

Current Phase 5A/DM discovery decisions live on `main`, including the accepted Desk-family and shared Player/DM rules-question direction. Preserve them during future integration.

DM **implementation** remains blocked until Phase 4A is physically accepted and explicitly closed.

## 8. Exact continuation point

Resume from the physical evidence checkpoint, not from the former assumption that P1–P16 were acceptance-complete:

`docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`

The next legitimate work is the bounded P1/P2/transversal-presentation repair on this branch. Do not create P18 or unrelated Player features. After repair and green automation, provide the new monotonic APK for owner retest; only then continue the remaining physical QA sequence.
