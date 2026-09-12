# Latest project checkpoint — global resume map

**Updated:** 2026-09-12  
**Branch:** `main`  
**Role:** canonical global navigation + current DM/Phase 5A discovery decisions  
**Player code authority:** `implementation/phase4a-successor-cycle`  
**Current Player QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**DM implementation:** blocked pending Phase 4A owner acceptance/closure

## Read first

1. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` — canonical cross-branch truth;
2. `docs/PROJECT_STATE.md` — current global state and execution rules;
3. `docs/BRANCH_STATUS.md` — branch roles/history;
4. for current Player code/QA, switch to `implementation/phase4a-successor-cycle` and read its `docs/checkpoints/LATEST.md` plus `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md`;
5. for current DM/Phase 5A product discovery, remain on `main` and preserve the accepted D-0068/D-0069/D-0070 line.

## Current branch topology

`main` and `implementation/phase4a-successor-cycle` are intentionally divergent.

`main` contains later DM/Phase 5A discovery decisions absent from the Player branch. The Player branch contains current Phase 4A Player implementation absent from `main`.

Do not overwrite either line with the other and do not infer current Player state from this default branch.

## Player Phase 4A status

Authoritative source: `implementation/phase4a-successor-cycle`.

Repaired product boundary:

`d630270f2f3d8fab94f3c1290963c2da7afaf06d`

Current QA candidate:

- `0.4.0-preqa.9 / 40900`;
- commit `cd0c203d337c062fa388010d300e875f2f54ced7`;
- Scaffold run `34726572588` — **SUCCESS**;
- artifact ID `10307444450`;
- artifact `dnd-custom-aid-debug-apk`;
- artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

Live interpretation:

- P1–P16 repair implementation: complete / automation-qualified;
- P17: physical tablet-QA gate policy already design-closed;
- physical phone/tablet acceptance: still pending;
- Phase 4A: not yet owner-closed.

The Player technical packaging step is complete. The next required evidence is physical owner/device QA of `preqa.9 / 40900`, not another invented implementation increment.

## DM / Phase 5A discovery status

The accepted main-line discovery direction remains valid and must not be lost when Player work is later integrated.

Important records include:

- D-0068 — DM live Workspace/Desk direction and DM Attention Budget;
- D-0069 — DM Screen / Stage Desk / Dungeon Desk / Combat Desk family;
- D-0070 — shared Player/DM rules-question capability.

This line is product discovery/design only. **Do not implement DM features before explicit Phase 4A closure.**

## Current authorization clarification

The 2026-09-11 Player repair authorization remains historically accurate: it authorized the bounded P1–P17 repair/validation cycle and did not itself authorize DM implementation or owner acceptance by automation.

The owner's 2026-09-12 instruction explicitly authorizes continuity files to be corrected on the correct branches, including `main`, and asks work to continue within the real existing authorizations. That covers the continuity repair and QA packaging now completed; it does not waive owner QA, authorize DM implementation, or authorize destructive history rewriting.

## Exact resume decision

- **Player implementation / QA:** switch to `implementation/phase4a-successor-cycle`; do not restart P1–P16; use `preqa.9 / 40900` for physical owner/device QA; reopen a repair only from actual QA evidence.
- **DM/Phase 5A discovery:** stay on `main`; continue only from accepted main-line decisions when explicitly requested.
- **DM implementation:** STOP until Phase 4A receives physical owner/device acceptance and explicit closure.
- **Repository integration:** preserve both divergent lines; reconcile explicitly rather than force-moving either branch.