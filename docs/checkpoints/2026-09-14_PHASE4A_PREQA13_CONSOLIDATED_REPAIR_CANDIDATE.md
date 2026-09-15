# Phase 4A — preqa.13 consolidated-repair physical-QA candidate

**Date:** 2026-09-14  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** FROZEN / AUTOMATION GREEN / TARGETED CROSS-DEVICE PHYSICAL REVALIDATION PENDING

## Candidate identity

- versionName: `0.4.0-preqa.13`
- versionCode: `41300`
- exact candidate commit: `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`
- candidate commit message: `build: advance consolidated repair candidate to preqa.13`

This is the monotonic successor to physical-discovery candidate `0.4.0-preqa.12 / 41200`. The `preqa.12` identity is not reused.

## Why this candidate exists

`preqa.12` completed broad phone + tablet discovery and exposed the post-P17 repair families. Those findings were repaired in bounded, guarded rounds while preserving unrelated accepted physical evidence.

The consolidated repaired line now contains automation-green implementations for:

- Round 1 structured dice / signed modifier;
- Round 2 T1 reorder stability;
- Round 3 compact checkbox + responsive grouping;
- T5 spell-source bootstrap / source context;
- T6 class-editor numeric / hit-die controls;
- T3/T4/T9 Application Settings semantics;
- T7 wide Combat adaptive composition;
- T8 Table Mode structural-affordance enforcement;
- T2 die-result presentation / Custom Throw / Dice display-mode ownership.

The optional phone-check-16 compact-density refinement was deliberately **not taken**: check 16 was already PASS and no later evidence demonstrated enough material benefit to justify pre-candidate visual churn.

## Aggregate repaired-line proof before version freeze

Final migration-free consolidated repair HEAD before versioning: `4d09e9eca648e5ca82af896dab8d16b986faae5b`.

Authoritative aggregate Scaffold:

- workflow: `Scaffold checks`
- run ID: `34801201294`
- run number: `1625`
- conclusion: **SUCCESS**

That run executed normal read-only Scaffold after all temporary T2 migration machinery had been removed and passed:

- backend install/type-check;
- compact Player geometry guard;
- reorder stability guard;
- checkbox consistency guard;
- spellcasting-bootstrap guard;
- class-editor-controls guard;
- application-settings semantics guard;
- wide-Combat composition guard;
- Table Mode affordance guard;
- T2 dice presentation / Custom Throw guard;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload.

## Exact versioned-candidate automation proof

The version bump was applied directly on the synchronized repaired line and created exact candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`.

Candidate Scaffold:

- workflow: `Scaffold checks`
- run ID: `34801612526`
- run number: `1630`
- head SHA: `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`
- conclusion: **SUCCESS**

The exact candidate revision passed:

- backend install/type-check;
- every permanent Player repair guard, including T2;
- stable CI debug-keystore preparation;
- shared tests;
- Android debug build;
- Desktop build;
- Android debug APK upload.

No writable migration workflow exists in the frozen candidate tree. Normal Scaffold remains `contents: read`.

## Artifact evidence

- artifact ID: `10331503478`
- artifact name: `dnd-custom-aid-debug-apk`
- GitHub Actions archive size: `13,693,984` bytes
- GitHub Actions artifact digest: `sha256:13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893`
- independently downloaded ZIP SHA-256: `13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893` — exact match
- APK filename: `androidApp-debug.apk`
- APK size: `39,094,384` bytes
- independent APK SHA-256: `81fbc20525221d791e6a0f786dec4b05709505e052e0e5ffba4ebfa5eb02391e`

## Physical-evidence status

`preqa.13` is **not physically PASS**. It is the frozen candidate on which targeted repaired/touched/affected cross-device revalidation must now be executed.

The prior `preqa.12` evidence remains historical authority for discovery and accepted PASS scope. Do not replay unrelated PASS coverage merely because the candidate identity advanced.

## Targeted revalidation route

Do **not** repeat the full historical phone/tablet suites.

Required targeted coverage:

1. **Round 1 + T2 dice family** — phone 7–9; ordinary d20 visible result sanity; representative non-d20 Custom Throw; `Otro…` valid/invalid sides; positive/negative modifiers; die-identity visuals including damage and flat fallback; Dice-tab display-mode ownership and persistence/reopen.
2. **T1 reorder** — representative one-column reorder; spatial/multi-column reorder; no preview-target chasing; auto-scroll where applicable; final order persists after leave/reopen. Reuse the existing failure video as historical baseline; do not request it again.
3. **Round 3 checkbox/responsive grouping** — representative Equipment migrated checkbox plus another migrated surface; Conjuros V/S/M and Concentración/Ritual packing; source/prepared grouping; portrait/landscape/wide behavior where applicable.
4. **T5 spell source** — canonical caster source availability; default casting ability unless existing configuration overrides it; add/associate/save/leave/reopen; stable source identity; manual/homebrew source coexistence; former tablet check 18 sticky/source-context behavior.
5. **T6 class controls** — numeric keyboard behavior; standard hit-die selection; `Otro…` custom sides; persistence after save/leave/reopen.
6. **T3/T4/T9 settings** — Vertical/Horizontal density semantics; safe column response under text pressure; symmetric text-size behavior; representative migration; haptics `Ninguna` vs non-none; preservation of an existing non-none installation after upgrade where evidence is available.
7. **T7 wide Combat** — tablet landscape width use; bounded HUD; adaptive packing; multi-column reorder/auto-scroll; persisted order; normal child actions outside drag; no accidental child action during drag; one narrow-phone Combat sanity check.
8. **T8 Table Mode** — structural controls genuinely read-only/non-opening; class/general structural dialogs unavailable; Skills structural controls gated while presentation selector remains usable; HP/inspiration/current resources remain operational and persist; one narrow-phone Table Mode sanity check.
9. **Previously unresolved evidence** — phone 21; affected slice of phone 22; tablet 18.

Preserve all unrelated accepted PASS evidence.

## Failure handling

If targeted physical QA finds a material defect:

- classify it before changing code;
- audit source before claiming shared/systemic cause;
- reopen only the relevant bounded repair family;
- preserve storage/import/export, IDs/associations and accepted evidence unless the approved repair requires otherwise;
- rerun focused + aggregate automation;
- advance to another monotonic candidate identity if the frozen candidate changes materially;
- checkpoint exact evidence before another owner pass.

Do not invent a new numbered repair merely because the project is at a manual evidence gate.

## Current gate

Phase 4A remains **OPEN**. The next work is owner/device **targeted cross-device physical revalidation on this exact `preqa.13 / 41300` candidate**.

Phase 4A closes only after sufficient targeted evidence and explicit owner acceptance. DM implementation remains blocked until that explicit closure. No P18 exists.