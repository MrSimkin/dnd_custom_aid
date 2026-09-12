# Latest project checkpoint — global resume map

**Updated:** 2026-09-12  
**Branch:** `main`  
**Role:** canonical global navigation + current DM/Phase 5A discovery decisions  
**Player code authority:** `implementation/phase4a-successor-cycle`  
**DM implementation:** blocked pending Phase 4A owner acceptance/closure

## Read first

1. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` — canonical cross-branch truth;
2. `docs/PROJECT_STATE.md` — current global state and execution rules;
3. `docs/BRANCH_STATUS.md` — branch roles/history;
4. for current Player code/QA preparation, switch to `implementation/phase4a-successor-cycle` and read its `docs/checkpoints/LATEST.md`;
5. for current DM/Phase 5A product discovery, remain on `main` and preserve the accepted D-0068/D-0069/D-0070 line.

## Current branch topology

`main` and `implementation/phase4a-successor-cycle` are intentionally divergent.

Before this continuity documentation commit:

- `main` = `de3930a8c0357bbbaa77c423f0011041f5cfd111`;
- Player successor = `d630270f2f3d8fab94f3c1290963c2da7afaf06d`.

`main` contains later DM/Phase 5A discovery decisions absent from the Player branch. The Player branch contains current Phase 4A Player implementation absent from `main`.

Do not overwrite either line with the other and do not infer current Player state from this default branch.

## Player Phase 4A status

Authoritative source: `implementation/phase4a-successor-cycle`.

Verified post-repair product boundary:

`d630270f2f3d8fab94f3c1290963c2da7afaf06d`

Normal Scaffold run `34721374190`: **SUCCESS**.

Artifact:

- ID `10306416852`;
- `dnd-custom-aid-debug-apk`;
- digest `sha256:c22e08deb3fbfd9a278b6e48cc4ac6d306229b6b1e354995eb7b25735eca645d`.

Live interpretation:

- P1–P16 repair implementation: complete / automation-qualified;
- P17: physical tablet-QA gate policy already design-closed;
- physical phone/tablet acceptance: still pending;
- Phase 4A: not yet owner-closed.

The Player branch still carries package identity `0.4.0-preqa.8 / 40800`, so its next technical step is a monotonic successor QA package identity followed by the normal gate and explicit owner/device QA.

## DM / Phase 5A discovery status

The accepted main-line discovery direction remains valid and must not be lost when Player work is later integrated.

Important records include:

- D-0068 — DM live Workspace/Desk direction and DM Attention Budget;
- D-0069 — DM Screen / Stage Desk / Dungeon Desk / Combat Desk family;
- D-0070 — shared Player/DM rules-question capability.

This line is product discovery/design only. **Do not implement DM features before explicit Phase 4A closure.**

## Current authorization clarification

The 2026-09-11 Player repair authorization remains historically accurate: it authorized the bounded P1–P17 repair pass and did not itself authorize `main` integration.

The owner's 2026-09-12 instruction now explicitly authorizes continuity files to be corrected on the correct branches, including `main`, so future continuation does not require archaeological reconstruction. That newer authorization covers this continuity repair; it does not waive owner QA, authorize DM implementation, or authorize destructive history rewriting.

## Exact resume decision

- **Player implementation / QA preparation:** switch to `implementation/phase4a-successor-cycle`; do not restart P1–P16.
- **DM/Phase 5A discovery:** stay on `main`; continue only from accepted main-line decisions when explicitly requested.
- **DM implementation:** STOP until Phase 4A receives physical owner/device acceptance and explicit closure.
- **Repository integration:** preserve both divergent lines; reconcile explicitly rather than force-moving either branch.