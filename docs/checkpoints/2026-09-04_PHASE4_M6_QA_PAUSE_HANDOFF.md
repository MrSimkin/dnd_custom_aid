# Phase 4 M6 — owner QA pause / handoff checkpoint

**Date:** 2026-09-04  
**Status:** PAUSED / READY FOR OWNER REAL-DEVICE QA  
**Durable pre-QA branch:** `implementation/phase4-preqa-consolidation`  
**Durable state at pause:** `2951927ba2004f8cec8934e2f392023388db8aa5` before this documentation-only pause update  
**Canonical `main`:** untouched at `471c5570669a6007bea9796d8a2c25536b10be21`  
**Historical Batch L candidate:** preserved unchanged as historical evidence only

## Purpose

This checkpoint is the single resume point for the project while owner QA is deferred. All implementation-completeness work, bounded pre-QA cleanup and automated candidate validation are complete. No product work should resume before owner real-device QA unless the owner explicitly changes scope.

## Completed pre-QA state

- M1 scope traceability audit — COMPLETE;
- M2 code-health/static architecture audit — COMPLETE;
- M3 prior-batch implementation-completeness audit — COMPLETE;
- M4 inter-batch scope-hole closure — COMPLETE;
- M5 post-repair re-audit, bounded cleanup, full regression and exact replacement candidate freeze — GREEN / COMPLETE;
- M6 owner real-device QA — **NEXT, NOT YET STARTED**.

The six M3 scope holes are closed: F14 structured proficiencies/languages, F15 Resource Favorite/Quick Access, I18 richer character-list summary, I21 real-sheet settings preview, D06 rules/source badges and D07 state-badge consistency.

## Exact active M6 candidate

Use only this frozen candidate for owner QA:

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- tree `fd1f7feffde082b34cce41248e951a25eed7a004`;
- ordinary clean standard workflow `33911956696` — SUCCESS;
- independent exact-SHA validator workflow `33912322920` — SUCCESS;
- validator artifact ID `9951922423`;
- GitHub artifact name `phase4-m5-frozen-qa-apk`;
- artifact ZIP digest `sha256:5fb8d7f281dbf937def89db4377e9b4157c46343f07721912aa759bb52d6f9fa`;
- exact APK size `35,720,588` bytes;
- exact APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

Owner-facing filename prepared for the same APK:

`DND_Custom_Aid_Phase4_M6_QA_Candidate_2026-09-04.apk`

The frozen candidate branch must not be changed. Any production repair found during QA requires a new technical gate, a new frozen candidate identity and repetition of affected QA evidence.

## Critical first QA action

**Do not clear Android app data before the first test.**

The first owner QA action is the migration/data-preservation test:

1. keep the currently installed prior owner-QA app and its data;
2. install the exact M6 APK above over that installation;
3. open the app and verify existing campaigns, characters and representative General/Combate/Equipo/Conjuros/Notas data survive and reopen;
4. only after that upgrade test is recorded may a clean-install pass be performed.

This ordering is mandatory because a clean install cannot validate the real schema lineage.

## Required M6 device/layout matrix

- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape;
- representative larger application text scale.

Core QA coverage:

- upgrade/data preservation;
- main and conditional tabs;
- dirty / Save / Discard / unsaved-leave behavior;
- Table mode;
- Supercompact / Quick Access;
- backup export + import-as-copy;
- IME/keyboard action reachability;
- rotation/context retention;
- scrolling and larger text;
- adaptive/master-detail behavior;
- representative editing and persistence in General, Habilidades, Combate, Gestión, Equipo, Rasgos, Conjuros, Notas, Trasfondo and conditional modules.

## Explicitly deferred maintenance

These are not QA blockers and must not be mixed into the frozen candidate without a demonstrated QA defect:

- internal phone/wide duplication in `CharacterEquipmentClosureV4`;
- decomposition of large `CharacterEditorScreenV4` orchestration;
- migration of the deprecated Kotlin Multiplatform `androidLibrary` target declaration to the newer target API.

## Branch and governance invariants

- keep `main` untouched until owner QA acceptance, final governance housekeeping and explicit owner merge approval;
- keep `tmp/phase4-l-frozen-qa-candidate` untouched as historical evidence;
- keep `tmp/phase4-m5-frozen-qa-candidate` untouched as the active QA target;
- do not begin DM-side implementation before Phase 4 exit;
- preserve historical/focused/tmp branches until the post-QA unique-commit/merge-boundary audit.

## Documentation state at pause

At this pause checkpoint, current-state documentation is being reconciled so new sessions do not resume from historical Batch I/L instructions. `docs/PROJECT_STATE.md` remains the authoritative current-state snapshot; this checkpoint is the preferred practical resume entry while QA is paused.

The consolidated `docs/DECISIONS.md` reconciliation through detailed D-0044–D-0047 remains intentionally deferred until post-QA governance housekeeping, as already recorded in `PROJECT_STATE.md`.

## Exact resume instruction

When the owner returns, do **not** restart implementation or re-audit M1–M5 by default.

Resume with:

1. read this checkpoint;
2. verify the frozen branch still points to `adc286b3e1305ed706c2ed04d478a43652f6b365`;
3. verify the APK SHA-256 is `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`;
4. begin M6 with the in-place upgrade/data-preservation test;
5. record owner observations systematically;
6. if a blocking defect requires code change, repair only that demonstrated issue, run the complete gate, freeze a new exact candidate and repeat affected QA;
7. after owner QA acceptance, perform final documentation/governance reconciliation, unique-commit/merge-boundary audit and explicit owner merge/closure decision.

**Current stop point: M6 is ready, but owner QA has not started.**
