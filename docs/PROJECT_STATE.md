# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Latest physically tested candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7` — FAILED SHARED HP/UX ACCEPTANCE BOUNDARY  
**Current phase:** P1/P2 + transversal presentation consistency reopened by physical owner/device QA; canonical HP and General navigation/save repairs implemented, P2 feedback + presentation repair and validation pending  
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

- General HP editing did not live-propagate to Combate without an explicit `Guardar`;
- reducing maximum HP did not clamp current HP; physical observation included invalid Combate display `20/10`;
- agreed subtle changed-state HP feedback was absent;
- Combat `Establecer PV` was ineffective for current HP while temporary-HP exact correction worked;
- `Daño — Cantidad — Curar` was out of proportion with surrounding Combate UI, reopening the transversal size/margin/padding consistency boundary.

Preserved passes: max-HP increase without silent healing, damage/temp-HP arithmetic, healing cap, amount clearing and Combat-operation-to-General projection.

Exact owner/device evidence: `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

## 4. Repair status

### R1 — canonical HP state boundary — implemented + regression-locked, automation pending

- `e0397146445c2cd78e7d017943bca1eb76101939` — shared exact hit-point update helpers now enforce non-negative max HP, `0 <= current <= max`, no silent healing on max increase, and clamp-on-max-reduction semantics.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge now preserves proposed max HP and clamps proposed current HP against that same canonical maximum. The prior implementation silently discarded proposed max HP and normalized current HP against the old persisted maximum.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9` — direct regression tests lock exact current/max normalization, no silent healing on max increase and clamp on max decrease.
- `49833bb64857376c4931e91c5af684bd287b2aba` — operational-merge regression tests now lock proposed max-HP persistence, `20/10 → 10/10`, max increase without healing and temp-HP preservation; the stale prior expectation that max HP was rejected as structural state was removed.

This directly repairs and regression-locks the identified source-level cause of Combat exact-current/max HP correction being lost. R1 is not yet automation-qualified or physically accepted.

### R2 — General HP navigation/save propagation — implemented, automation pending

- `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — General HP now uses the shared canonical HP operation on ordinary structural save; operational synchronization refreshes max/current/temp HP together; leaving General for another character tab flushes a valid HP draft canonically before navigation, so General → Combate no longer depends on a global `Guardar`; transient/unparseable numeric input is not force-persisted.
- The resulting source diff was independently inspected and changed only `CharacterEditorV4.kt` in the intended five HP-wiring locations. Temporary guarded patch machinery used because the connector exposes full-file replacement but no line-patch write was removed immediately after the product commit.

R2 addresses both previously open General no-global-`Guardar` propagation and normal structural-save normalization at source level. It is not yet automation-qualified or physically accepted.

### Still open

- the agreed subtle changed-state HP glow/pulse remains to be recovered from the accepted P2 contract and repaired consistently across relevant HP-changing actions;
- transversal presentation consistency remains to be classified against the historical audit/closure records and repaired, including the observed `Daño — Cantidad — Curar` proportion problem;
- focused validation, aggregate validation and new QA packaging remain pending.

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

1. recover and implement the accepted P2 subtle HP changed-state feedback contract;
2. classify and repair the transversal size/margin/padding presentation inconsistency, including the observed Combat action row;
3. run focused and aggregate validation covering R1/R2 and the presentation repair;
4. assign a new monotonic QA identity and artifact after green material code changes;
5. physically recheck the reopened phone boundary.

Representative P17 tablet QA may resume only after the hard shared defects no longer make tablet acceptance evidence misleading.

## 7. Global/DM line

Current Phase 5A/DM discovery decisions live on `main`. Preserve them during future integration. DM implementation remains blocked until Phase 4A is physically accepted and explicitly closed.

## 8. Exact continuation point

Resume from `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

Do not create P18 or unrelated Player features. Complete the bounded repair and validation, then produce the next monotonic APK for owner retest.
