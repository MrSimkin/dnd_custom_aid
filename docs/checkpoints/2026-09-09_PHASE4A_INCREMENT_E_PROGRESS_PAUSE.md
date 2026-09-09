# Phase 4A — Increment E progress pause

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** INCREMENT E IN PROGRESS — shared recovery foundation and successor Gestión surface implemented/compiled; active Gestión wiring NOT yet applied  
**Pre-checkpoint branch head:** `a0458ee0842c0bcbb158a308e9f3f85d2e3a818a`

## Purpose

This checkpoint records the exact safe resume state before retrying the failed guarded wiring of the new Gestión surface. It exists specifically so a later session must not infer that the successor Gestión implementation is already active merely because the new file compiles.

## Completed before this checkpoint

### Increment D

Increment D is formally complete and green. Its closing checkpoint is:

- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_D_COMBAT_DICE.md`
- D close commit: `2eb65208faa6fb25b9a7f12387be55ea4e84ffb9`
- authoritative D workflow: `34304392913` — SUCCESS

### E shared Resource/Marker recovery foundation

Implemented one canonical mixed recovery operation layer while preserving Resource and Custom Marker as distinct domain identities.

Key commits:

- `5aeb4977c5164e6e921a8e7a37629425793fdcd8` — unified Resource/Marker recovery operations;
- `87e4a213da42f19ba45ca001270e7a1006241fe2` — focused mixed-recovery regression coverage.

Protected semantics now covered include:

- typed Resource vs Marker identities so equal UUID values cannot collide;
- Resource compatibility wrappers remain available rather than duplicating the recovery engine;
- automatic proposals only from explicit structured recovery rules;
- legacy/free recovery text remains review-only;
- Marker notes are never reinterpreted as recovery rules;
- selected-only application;
- binary/counter/current-max normalization and clamping;
- ambiguous/non-numeric recovery remains non-automatic;
- Resource placement does not create duplicate state or alter recovery identity.

Automated gate for this foundation:

- workflow `34305427239` — SUCCESS;
- backend: PASS;
- shared/Kotlin tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- APK upload: PASS.

## Successor Gestión implementation staged

New Android surface:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementSuccessorV4.kt`
- implementation commit: `3c5be8d499d176c37496df59ed5ae1219e4a572b`

The staged successor surface is intentionally separate from the legacy `CharacterManagementTabV4` implementation so the new operational composition could compile independently before activation.

Implemented/staged behavior includes:

- materially more compact operational state;
- compact horizontal death-save treatment instead of the previous two full rows;
- canonical Inspiration projection;
- Custom Marker live controls appropriate to binary/counter/current-max kinds;
- Resource projection filtered by canonical successor placement configuration;
- Resource editing that carries trackable value kind, placements and structured recovery semantics;
- one mixed rest preview/apply path for eligible Resources + Markers;
- no automatic inference from free/manual recovery text;
- explicit warning when an unsaved General HP draft differs from the persisted/canonical operational sheet, without silently saving unrelated General edits;
- existing conditions, concentration, temporary effects and reconciliation retained in the successor composition;
- new editor paths avoid adding another generic `Fuente` UI field while preserving existing stored legacy provenance where necessary for compatibility.

Standalone successor-surface automated gate:

- workflow `34306095086` — SUCCESS;
- backend: PASS;
- shared/Kotlin tests/build: PASS;
- Android debug compilation/assembly: PASS;
- desktop build: PASS;
- APK upload: PASS;
- artifact ID `10086746569`;
- artifact name `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:8210319be7a508d2eef8192dc68c5ff89041530a502bc3cc1ded13a96b831559`.

This proves the staged file compiles. It does **not** prove that the active character Gestión tab is using it.

## Failed activation attempt — safe failure

A temporary guarded workflow was added at branch head:

- commit `a0458ee0842c0bcbb158a308e9f3f85d2e3a818a`;
- workflow run `34306315649`;
- job `patch` — FAILURE at `Apply exact guarded wiring patch`.

The failure occurred because the exact textual guard did not match the current `CharacterEditorV4.kt` anchor. The guard failed before the replacement/validation/commit steps, therefore:

- no partial `CharacterEditorV4.kt` wiring change was committed;
- the active Gestión path must still be treated as the legacy `CharacterManagementTabV4` call;
- the staged `CharacterManagementSuccessorTabV4` is compiled but **not active**;
- the temporary workflow file remains branch-local and should be removed as part of the corrected wiring retry once it has served its purpose.

Do not claim Increment E is complete at this checkpoint.

## Increment E status by plan item

- **E1 — compact operational state:** staged/compiled, activation pending;
- **E2 — Inspiration + Custom Markers:** shared model exists; Gestión live projection staged/compiled, activation/integration gate pending;
- **E3 — Resources across domains:** canonical placement/value-kind foundation exists; Gestión placement-aware projection/editor staged; cross-tab canonical integration must still be verified after activation;
- **E4 — rest integration:** mixed Resource/Marker recovery engine tested; Gestión mixed preview/apply staged; integrated active-tab verification pending;
- **E5 — conditions + concentration:** existing/custom condition and concentration behavior retained in staged surface, but the planned predefined safe catalog/source-help infrastructure and explicit concentration check/DC explanation still require completion/verification before closing E.

## Exact safe resume action

1. inspect the actual current `CharacterEditorV4.kt` Gestión call at branch head instead of relying on the failed exact-text anchor;
2. wire the active `CharacterTabV4.MANAGEMENT` branch to `CharacterManagementSuccessorTabV4` using the current persisted sheet, successor state and General draft projection needed for coherence signaling;
3. preserve canonical save boundaries: operational changes may save operational state, but must not silently persist unrelated unsaved General draft edits;
4. remove the temporary one-shot wiring workflow after the corrected branch commit is made;
5. run the integrated full gate;
6. finish/verify remaining E5 condition-catalog and concentration-help work;
7. run the final E regression gate and write the Increment E completion checkpoint;
8. only then proceed to Increment F — Conjuros compact source-context redesign.

## Branch / governance

- `main` remains the canonical untouched baseline for this successor cycle;
- successor work remains isolated on `implementation/phase4a-successor-cycle`;
- no owner-device visual acceptance has been performed on the successor implementation yet;
- the last owner-auditioned practical build remains `0.4.0-preqa.7` / build `40700` / debug;
- do not begin DM implementation;
- do not reuse or mutate old frozen QA candidates.
