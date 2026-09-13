# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Previous physical candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7` — FAILED SHARED HP/UX ACCEPTANCE BOUNDARY  
**Current repaired physical-QA candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — AUTOMATION GREEN / READY FOR FOCUSED OWNER PHONE RETEST  
**Current phase:** R1–R3 repaired, regression-locked and automation-green; focused repaired phone QA is next  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Branch authority and authorization

This branch is the authoritative Player implementation/Phase 4A repair line. `main` is intentionally divergent and carries later global/Phase 5A/DM discovery records; it is not the latest Player runtime. See `docs/BRANCH_STATUS.md` and `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`.

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` authorizes P1–P17 repair/validation, QA packaging, and bounded repairs reopened by real QA evidence. Current work is inside that authorization. No P18 exists.

## Physical preqa.9 evidence

Preserved PASS: update-in-place, launch, existing campaign/character preservation, representative saved-data preservation, full close/reopen, max-HP increase without silent healing, damage/temp-HP arithmetic, healing cap, amount clearing, and Combate-operation→General projection.

Blocking findings were:

- General HP required an extra global `Guardar` before Combate saw the change;
- lowering max HP could project invalid current > max (`20/10` observed);
- accepted subtle affected-HP feedback was absent;
- Combate `Establecer PV` did not change current HP while exact temp-HP correction worked;
- `Daño — Cantidad — Curar` was visually out of proportion, reopening transversal sizing/spacing consistency.

The owner and assistant agreed not to complete exhaustive QA on known-bad `preqa.9`.

## Repair status

### R1 — canonical HP state — implemented + regression-locked

- `e0397146445c2cd78e7d017943bca1eb76101939` — canonical HP operation/invariants.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge preserves proposed max/current HP canonically.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9`, `49833bb64857376c4931e91c5af684bd287b2aba` — regression coverage including `20/10 -> 10/10` and no-silent-heal behavior.

### R2 — General HP propagation — implemented

- `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — ordinary save canonicalizes HP; valid General HP drafts flush canonically on navigation; max/current/temp resynchronize; transient invalid/incomplete typing remains local.

### R3 — accepted P2 feedback + Combat row proportion — implemented + regression-locked + automation-green

- `cc452b156d43967d9eb794a661162c3f3a05f336` — targeted brief affected-HP feedback and rebalanced `Daño | cantidad | Curar` geometry.
- `ef051af8d2e36a3b765a80487afd9754d5a67e17` — shared canonical HP-change impact classifier.
- `ff06bdf1552268f9805c9ff4a3108a3675a22fe5` — Combat HUD consumes the shared classifier.
- `ddd9d01dab4f0b45470174712a5c115de1112d90` — tests lock no-change/PV-only/Temp-only/both targeting.
- Scaffold `34730363231` on `ddd9d01d…`: **SUCCESS**.

## Current repaired candidate

Authoritative candidate evidence: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md`.

- versionName `0.4.0-preqa.10`;
- versionCode `41000`;
- exact candidate commit `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`;
- exact candidate Scaffold `34730531791` — **SUCCESS**;
- artifact `10308364110`, `dnd-custom-aid-debug-apk`;
- GitHub artifact ZIP digest `sha256:ef44559ba61267837a286e996d74d5c00b55d89b003d7f6e69bf0902cff5dd32`, independently matched after download;
- APK size `38,865,008` bytes;
- independent APK SHA-256 `ee5db263883c8b1979b12cc9ca365ba9f190009b2b46e4e9b53681408c128c78`.

The only product delta between the already-green regression boundary `ddd9d01d…` and candidate `a0d7dbd8…` is the Android version identity; intervening non-product changes are continuity documentation.

## Current gate

Resume focused owner phone QA on `preqa.10` at the reopened P1/P2/presentation failures. Do not restart every previously passed check first. If the focused repair boundary passes, continue the remaining targeted phone regression and then representative P17 tablet QA, provided no hard shared/systemic failure remains.

P3–P16 remain historically implemented/automation-qualified unless later physical evidence specifically reopens them. P17 tablet QA remains paused until the focused phone repair boundary is cleared. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.

## Exact continuation point

Read `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md`, then `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`, then `docs/checkpoints/LATEST.md`. Next action is physical focused phone QA on the exact `preqa.10 / 41000` artifact. Do not restart R1–R3 or invent unrelated Player work.
