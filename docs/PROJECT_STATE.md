# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Latest physically tested candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7` — FAILED SHARED HP/UX ACCEPTANCE BOUNDARY  
**Current phase:** bounded P1/P2 + transversal presentation repair implemented through R3; exact R3 automation running; owner QA intentionally paused until the next monotonic repaired candidate  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Branch authority

This branch is the authoritative Player implementation/Phase 4A repair line. `main` is intentionally divergent and carries later global/Phase 5A/DM discovery records; it is not the latest Player runtime. Do not force either active line over the other. See `docs/BRANCH_STATUS.md` and `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`.

## Authorization

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` authorizes the accepted P1–P17 Player repair/validation cycle, QA packaging, and bounded repairs reopened by real QA evidence. The current work is inside that authorization. No P18 is created.

## Physical preqa.9 evidence

Preserved PASS: update-in-place, launch, existing campaign/character preservation, representative saved-data preservation, and full close/reopen.

Blocking physical findings that failed the next P1/P2 boundary:

- General HP was not canonical/visible in Combate without an extra global `Guardar`;
- lowering max HP could leave/project invalid current > max; owner observed `20/10`;
- accepted subtle affected-HP feedback was absent;
- Combate `Establecer PV` did not change current HP while exact temp-HP correction worked;
- `Daño — Cantidad — Curar` was visually out of proportion with surrounding Combate UI, reopening the transversal sizing/spacing consistency boundary.

Preserved passes include max-HP increase without silent healing, damage/temp-HP arithmetic, healing cap, amount clearing and Combate-operation→General projection.

Exact owner/device record: `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

## Bounded repair status

### R1 — canonical HP state — implemented + regression-locked

- `e0397146445c2cd78e7d017943bca1eb76101939` — canonical current/max HP operation and invariants.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge preserves proposed max HP and normalizes current against it.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9` — direct canonical HP tests.
- `49833bb64857376c4931e91c5af684bd287b2aba` — operational merge tests including `20/10 -> 10/10` and no-silent-heal behavior.

### R2 — General HP save/navigation propagation — implemented

- `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — ordinary General save canonicalizes HP; valid General HP drafts flush canonically when leaving General; max/current/temp resynchronize together; transient incomplete typing is not force-persisted.

### R3 — P2 feedback + Combat row proportion — implemented / exact automation in progress

- controlling P2 contract recovered from `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`;
- product commit `cc452b156d43967d9eb794a661162c3f3a05f336` restores short/subtle feedback on only the HP metric(s) actually changed and rebalances `Daño | cantidad | Curar` with symmetric action space, a narrower amount field, and a shared compact control height;
- R3 changes only `CharacterCombatOperationalV4.kt`;
- exact product Scaffold run `34730201935`: backend SUCCESS; Kotlin/shared/Android still IN PROGRESS at this state update.

R1–R3 are not yet declared automation-green as a package, and none is yet physically accepted.

## Current gate

Do not continue exhaustive owner QA on `preqa.9`; the owner and assistant explicitly agreed to preserve its observations and supersede it after repair.

Next sequence:

1. finish/inspect exact run `34730201935`, repairing any failure;
2. after green automation, assign a new monotonic QA identity (never reuse `40900`), build/package the exact candidate, and record commit/run/artifact/digest;
3. resume focused phone QA on the reopened P1/P2/presentation boundary;
4. only then resume representative P17 tablet QA if no hard shared/systemic failure remains.

P3–P16 remain historically implemented/automation-qualified unless later physical evidence specifically reopens a boundary. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.

## Historical failed candidate proof

`preqa.9 / 40900` candidate `cd0c203d337c062fa388010d300e875f2f54ced7` had normal Scaffold run `34726572588` — SUCCESS, artifact `10307444450`, GitHub artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`. CI remains valid for its tested scope; physical QA exposed gaps outside that scope.

## Exact continuation point

Read `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`, then `docs/checkpoints/LATEST.md`. Continue from R3 automation; do not restart R1/R2 or invent unrelated Player work.
