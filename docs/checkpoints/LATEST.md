# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; PHONE + TABLET DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current gate:** post-P17 cross-device repair plan ready; awaiting owner confirmation of T3 Columns-setting semantics before product implementation  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — **controlling source audit + consolidated repair plan**.
2. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery evidence; checks 16–17 PASS and 18 BLOCKED BY T5, not a new failure.
3. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — controlling detailed phone findings from the later 23-check pass.
4. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact preqa.12 candidate/run/artifact/digest evidence.
5. `docs/PROJECT_STATE.md` — live Player authority/current gate.
6. `docs/TESTING.md` — physical-QA and failure-handling policy.

## P17 tablet discovery — COMPLETE

Exact `preqa.12 / 41200` tablet results:

- 1–6 PASS;
- 7 FAIL / T7 — Combat landscape stretches full width instead of meaningful adaptive composition;
- 8 PASS;
- 9–10 FAIL / T5 — new Mago lacks usable spell-source/bootstrap path; landscape controls themselves looked good;
- 11 PASS + same systemic checkbox family as phone 17.1;
- 12–14 PASS;
- 15 FAIL / T8 — Table Mode exposes structural edit affordances whose changes cannot take effect;
- 16–17 PASS;
- 18 BLOCKED BY T5 — not a new failure.

Additional findings captured during P17:

- T1 reorder live-reflow/drag target instability across one-column and spatial layouts;
- T2 dice-result shape/custom throw/dice-mode ownership clarification;
- T3 misleading Columns-setting semantics;
- T4 asymmetric text-size scale;
- T5 spell-source/bootstrap mismatch;
- T6 legacy class editor level/hit-die inputs;
- T7 wide Combat composition;
- T8 Table Mode structural affordance inconsistency.

No further broad tablet testing is required on `preqa.12`.

## Phone evidence to preserve

Latest detailed phone pass on exact `preqa.12` remains authoritative:

- 1–6 PASS;
- 7–8 OPEN structured-damage modifier defects;
- 9 PASS + requested direct sign toggle;
- 10–16 PASS, with 16 optional compact-density refinement;
- 17.1–17.3 OPEN systemic checkbox/responsive grouping family;
- 18–20 PASS;
- 21 UNASSESSED;
- 22 PARTIAL/AMBIGUOUS;
- 23 PASS.

Do not replay unrelated accepted phone evidence.

## Cross-device source audit result

The post-P17 audit has confirmed and grouped the open work into source-level repair families:

1. structured dice/result family — phone 7–9 + T2, including a latent shared parser gap for `NdS±M`;
2. reorder target-stability family — T1 in both one-dimensional and spatial engines;
3. shared compact checkbox + responsive grouping family — phone 17.1–17.3 and tablet reproduction;
4. spell-source/bootstrap compatibility family — T5;
5. class-editor numeric/die selector family — T6;
6. wide Combat adaptive composition family — T7;
7. Table Mode structural-affordance enforcement — T8;
8. Application Settings semantics — T3/T4;
9. optional compact-field padding refinement — phone 16 only.

Root causes, affected source files, compatibility constraints, implementation order, automated verification and targeted physical revalidation are defined in the controlling audit/repair-plan checkpoint.

## One owner product decision before implementation

T3 cannot be solved truthfully as a cosmetic label patch because the app currently stores apparently exact column counts but clamps/ignores them differently by width, text scale and screen.

Recommended option: replace the four exact-count selectors with one adaptive **card distribution / density** preference; the app computes effective columns from available width, text scale and target minimum card width.

Fallback: keep context-specific selectors but define them explicitly as **maximum columns**, constrain ranges to real maxima and explain that available width/text scale can reduce the effective count.

All other repair directions are sufficiently grounded to implement once this choice is confirmed.

## Candidate evidence unchanged

- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

No product code, APK, version or artifact changed during P17 recording or the post-P17 source audit.

## Exact route

1. Owner confirms T3 setting semantics.
2. Implement the consolidated repair plan on this branch with focused tests/guards.
3. Run the normal aggregate Scaffold gate.
4. Create/freeze a new monotonic physical-QA candidate because product code will have changed materially.
5. Perform only the targeted cross-device revalidation defined in the audit plan, including previously-unassessed phone settings persistence and the previously-blocked tablet Conjuros sticky check.
6. Explicit owner Phase 4A closure only after repaired evidence is sufficient.

No P18 exists. DM implementation remains blocked until explicit Phase 4A closure.
