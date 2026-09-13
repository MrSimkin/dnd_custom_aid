# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Latest physically tested candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7` — FAILED SHARED HP/UX ACCEPTANCE BOUNDARY  
**Current phase:** bounded P1/P2 + transversal presentation repair implemented through regression-locked R3; exact automation running; owner QA intentionally paused until the next monotonic repaired candidate  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Branch authority and authorization

This branch is the authoritative Player implementation/Phase 4A repair line. `main` is intentionally divergent and carries later global/Phase 5A/DM discovery records; it is not the latest Player runtime. See `docs/BRANCH_STATUS.md` and `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`.

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` authorizes P1–P17 repair/validation, QA packaging, and bounded repairs reopened by real QA evidence. Current work is inside that authorization. No P18 exists.

## Physical preqa.9 evidence

Preserved PASS: update-in-place, launch, existing campaign/character preservation, representative saved-data preservation, full close/reopen, max-HP increase without silent healing, damage/temp-HP arithmetic, healing cap, amount clearing, and Combate-operation→General projection.

Blocking findings:

- General HP required an extra global `Guardar` before Combate saw the change;
- lowering max HP could project invalid current > max (`20/10` observed);
- accepted subtle affected-HP feedback was absent;
- Combate `Establecer PV` did not change current HP while exact temp-HP correction worked;
- `Daño — Cantidad — Curar` was visually out of proportion, reopening transversal sizing/spacing consistency.

Exact owner/device record: `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

## Repair status

### R1 — canonical HP state — implemented + regression-locked

- `e0397146445c2cd78e7d017943bca1eb76101939` — canonical HP operation/invariants.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge preserves proposed max/current HP canonically.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9`, `49833bb64857376c4931e91c5af684bd287b2aba` — regression coverage including `20/10 -> 10/10` and no-silent-heal behavior.

### R2 — General HP propagation — implemented

- `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — ordinary save canonicalizes HP; valid General HP drafts flush canonically on navigation; max/current/temp resynchronize; transient invalid/incomplete typing remains local.

### R3 — accepted P2 feedback + Combat row proportion — implemented + regression-locked / automation in progress

Controlling P2 contract: `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`.

- `cc452b156d43967d9eb794a661162c3f3a05f336` — targeted brief HP-metric feedback and rebalanced `Daño | cantidad | Curar` geometry.
- `ef051af8d2e36a3b765a80487afd9754d5a67e17` — shared canonical HP-change impact classifier.
- `ff06bdf1552268f9805c9ff4a3108a3675a22fe5` — Combat HUD consumes the shared classifier.
- `ddd9d01dab4f0b45470174712a5c115de1112d90` — tests lock no-change/PV-only/Temp-only/both targeting.

**Exact current product boundary:** `ddd9d01dab4f0b45470174712a5c115de1112d90`.  
**Exact current qualification run:** Scaffold `34730363231` — IN PROGRESS at this update.

R1–R3 are not yet physically accepted, and the repair package is not declared automation-green until the exact current run completes successfully.

## Current gate

Do not continue exhaustive owner QA on failed `preqa.9`. After exact R3 automation is green, assign the next monotonic QA identity (current branch still declares `preqa.9 / 40900`; `40900` must not be reused), package/verify the exact artifact, then resume focused phone QA. P17 tablet QA remains paused until no hard shared/systemic failure makes tablet evidence misleading.

P3–P16 remain historically implemented/automation-qualified unless later physical evidence specifically reopens them. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.

## Historical failed candidate proof

`preqa.9 / 40900` candidate `cd0c203d337c062fa388010d300e875f2f54ced7` had Scaffold `34726572588` — SUCCESS, artifact `10307444450`, GitHub artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`. CI remains valid for its scope; physical QA exposed missing behavior outside that scope.

## Exact continuation point

Read `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`, then `docs/checkpoints/LATEST.md`. Continue at exact R3 run `34730363231`; do not restart R1/R2 or invent unrelated Player work.
