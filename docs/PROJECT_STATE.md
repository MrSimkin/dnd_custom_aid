# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current phase:** repaired P1–P16 implementation is automation-qualified; P17 physical owner/device QA remains  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## 1. Read this branch as the Player code authority

This branch contains the current Player implementation. `main` is currently divergent and contains later global/DM product-discovery documentation that is not on this branch; `main` must not be mistaken for the latest Player runtime.

Cross-branch explanation: `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` and `docs/BRANCH_STATUS.md`.

## 2. Controlling repair authorization

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` records the owner's authorization for the accepted `preqa.8 / 40800` P1–P17 repair pass.

That authorization permits implementing the accepted Player repair scope, validation/build/checkpoint work, and automatic continuation inside the accepted repair blueprint. It does not authorize unrelated behavior, DM implementation, or claiming owner acceptance without physical QA.

The 2026-09-12 owner instruction additionally authorizes continuity documentation to be repaired on the correct branches, including `main`, solely to make current repository state unambiguous.

## 3. Current repair status

- P1–P12: implemented/closed through their recorded implementation and validation gates.
- P13: implementation and full-app explanatory-copy audit complete. Its checkpoint originally recorded the normal Scaffold gate as pending; that pending gate is now superseded by successful run `34721374190` on exact branch HEAD `d630270f2f3d8fab94f3c1290963c2da7afaf06d`.
- P14: Table Mode re-audit/repair closed.
- P15: Supercompact implementation/repair closed; related transversal consistency work belongs to proving this accepted P15, not to a second independent P15 cycle.
- P16: landscape/adaptive vertical-space implementation closed and automation-green.
- P17: design decision closed; this is the physical tablet-QA gate policy. Physical tablet QA itself has **not** been performed/accepted.

Do not restart P1–P16 because an older live pointer says work was pending.

## 4. Latest automation-qualified product boundary

Product/source commit:

`d630270f2f3d8fab94f3c1290963c2da7afaf06d`

Normal workflow:

- Scaffold checks run `34721374190`;
- event: push;
- conclusion: `success`;
- exact head SHA: `d630270f2f3d8fab94f3c1290963c2da7afaf06d`.

Published artifact:

- ID `10306416852`;
- name `dnd-custom-aid-debug-apk`;
- digest `sha256:c22e08deb3fbfd9a278b6e48cc4ac6d306229b6b1e354995eb7b25735eca645d`.

This proves the post-repair source passes the normal automated gate. It does not prove real-device UX acceptance.

## 5. Build identity caveat

`androidApp/build.gradle.kts` still identifies the current branch as:

- `versionName = "0.4.0-preqa.8"`;
- `versionCode = 40800`.

That is the same package identity as the physical owner-QA build that generated the repair backlog. Therefore the fresh successful artifact above is technically newer source but is not yet a clean monotonic owner-QA identity.

The next technical continuation step is to create a monotonic successor QA build identity from this repaired source, run the normal gate, publish/checkpoint its exact artifact, then hand that build to the owner.

## 6. P17 / owner-device gate

P17's durable rule is failure-type based rather than requiring a perfect phone PASS before tablet testing:

- hard shared/systemic primitive failure that would invalidate tablet evidence -> defer/stop tablet QA;
- soft PASS / soft FAIL / bounded/local defects -> tablet QA may proceed;
- uncertainty about phone-specific vs responsive/shared behavior -> tablet evidence may be diagnostically useful;
- no tablet PASS/FAIL may be inferred without physical tablet testing.

Representative physical tablet QA includes portrait/landscape, navigation/adaptive layout, Combat/P5/P16, Conjuros sticky behavior, representative editor/IME behavior, reorder, PC/Application Settings responsiveness, Supercompact, Table Mode, larger-text/density behavior, persistence/reopen and a cross-surface canonical-state sanity check.

A real-device defect may reopen the relevant P1–P16 point. Owner acceptance is explicit, never inferred from CI.

## 7. Global/DM line

Current Phase 5A/DM discovery decisions live on `main`, including the accepted Desk-family and shared Player/DM rules-question direction. Preserve them when future integration occurs.

DM implementation remains blocked until Phase 4A is physically accepted and explicitly closed.

## 8. Exact next action

1. Preserve the current repaired source behavior; do not add unrelated Player features.
2. Advance the QA package identity monotonically from `0.4.0-preqa.8 / 40800`.
3. Run normal Scaffold validation on that exact QA-build commit.
4. Record workflow/artifact/digest in a dedicated QA checkpoint and refresh `LATEST.md`.
5. Give the resulting build to the owner for targeted phone regression and then P17-governed tablet QA when meaningful.
6. If QA finds a defect, reopen only the relevant accepted repair boundary and repair it on this branch.
7. If owner/device QA accepts the repaired baseline, record explicit Phase 4A acceptance/closure before any DM implementation begins.