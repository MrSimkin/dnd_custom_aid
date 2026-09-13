# Phase 4A — Repair Round 1: structured dice / signed modifier foundation

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Round baseline HEAD:** `80126079f79d55c34724a3b596c066d63fe665e5`  
**Round product/test HEAD:** `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`  
**Physical baseline remains:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** ROUND 1 COMPLETE / AUTOMATION GREEN / PHYSICAL REVALIDATION DEFERRED TO CONSOLIDATED CANDIDATE

## 1. Scope

This round implements the structured-dice foundation required by phone findings 7–9 and the shared parser part of T2. It intentionally does **not** yet complete T2 die-shape rendering, Custom Throw die selection or Dice-tab ownership of display mode; those remain later repair work.

The physical phone findings are therefore not marked physically PASS here. They remain pending targeted revalidation on the new consolidated QA candidate.

## 2. Root cause repaired

The preqa.12 Combat structured-damage editor kept quantity, sides and modifier visually separate but serialized a positive modifier without an explicit sign. Typing modifier `2` after `1d8` produced `1d82`; reparsing then legitimately interpreted `82` as die sides. That single serialization defect explained both the standard-die selector changing to `Otro…` and modifier digits appearing under custom die sides.

The source audit also found a latent shared-domain defect: `parseCharacterDiceExpression()` accepted only bare `NdS`, so even correctly stored `NdS+M` / `NdS-M` expressions were not included correctly in damage-roll totals.

## 3. Implementation

### Shared parser / roller

Commit `5ea3f521049d2143fb4f6a6e326139e0b21788cb` — `fix: support signed structured damage modifiers`

- `CharacterDiceExpression` now carries `modifier`, defaulting to `0`.
- Shared parsing accepts bare `NdS`, `NdS+M` and `NdS-M`.
- Invalid/incomplete expressions remain non-rollable rather than being guessed.
- Dice damage totals now include the parsed signed modifier.

### Focused shared tests

Commit `570304505f9d2ba89a3666839c3a06178cf7d0db` — `test: cover signed structured damage expressions`

Coverage now proves:

- bare `1d8`;
- positive `1d8+2`;
- negative `2d6-1`;
- implicit count `d12`;
- incomplete/malformed `1d8+`, `1d8-`, `1d`, invalid text and zero count;
- deterministic positive/negative modifier totals;
- malformed dice components do not invoke the die roller.

### Android structured editor

Commit `046d549bdc0dd8564c0d532e8cc021835bda731c` — `fix: stabilize structured damage modifier editing`

- quantity, die sides, modifier sign and modifier magnitude are independent draft state;
- serialization emits an explicit `+` or `-` before a non-empty modifier;
- typing a positive magnitude therefore produces `1d8+2`, never `1d82`;
- clearing the magnitude returns the sign state to none;
- incomplete `1d`, `d8`, `1d8+` and `1d8-` drafts remain stable while editing, with strict save-time validation;
- sign UI is now a direct compact glyph control rather than a dropdown;
- explicit sign states are none / plus / minus; with a populated modifier, direct taps toggle plus/minus;
- the standard-die selector and `Otro…` sides field remain independent from modifier editing.

### Durable geometry/source guard update

The first Scaffold run after the Android edit (`34787610695`) failed before compilation because the existing guard still looked for the intentionally removed `signExpanded` dropdown marker. The product change itself was not reverted.

Commit `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0` — `test: update compact control guard for direct dice sign toggle`

The guard now enforces the new contract instead of the obsolete UI shape:

- compact glyph selector is present;
- sign action calls the direct toggle helper;
- modifier uses the independent magnitude field;
- obsolete sign-dropdown state is absent;
- serializer emits explicit sign token plus magnitude;
- existing raw `OutlinedTextField` and compact-field-count protections remain intact.

## 4. Automated verification

Authoritative green run for this round:

- Workflow: `Scaffold checks`
- Run ID: `34787688776`
- Run number: `1508`
- Head SHA: `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`
- Conclusion: **SUCCESS**
- Backend job: **SUCCESS**
- Compact Player control geometry guard: **SUCCESS**
- Kotlin/shared build and tests: **SUCCESS**
- Android build: **SUCCESS**
- Debug APK upload step: **SUCCESS**

The earlier run `34787610695` is retained as useful evidence of the stale-guard mismatch; it is superseded by the successful run above for Round 1 verification.

## 5. QA status after Round 1

Automation supports the intended repair, but no new physical candidate has been frozen yet. Therefore:

- phone 7–8 remain **OPEN / IMPLEMENTED, TARGETED PHYSICAL REVALIDATION PENDING**;
- phone 9 direct sign-toggle request is **IMPLEMENTED, TARGETED PHYSICAL REVALIDATION PENDING**;
- the shared signed-modifier roller gap is **AUTOMATION-VERIFIED**;
- T2 remains **PARTIALLY IMPLEMENTED** because die-result silhouettes, Custom Throw die/modifier UX and Dice-tab display-mode ownership still remain.

Accepted unrelated phone/tablet PASS evidence remains preserved and must not be replayed.

## 6. Project gate

Phase 4A remains **OPEN**. The exact frozen physical candidate remains preqa.12 until the consolidated repair receives a new monotonic QA identity. DM implementation remains blocked pending explicit owner Phase 4A closure. No P18 exists.

## 7. Next round

**Round 2: T1 reorder target stability.**

Implement the audited one-dimensional + spatial drag/reorder stabilization: remove live preview-geometry feedback as a retarget trigger, use stable active-drag slot geometry / pointer-driven retargeting with appropriate hysteresis, preserve auto-scroll re-evaluation and final persisted order, and add focused target-stability tests before updating status again.
