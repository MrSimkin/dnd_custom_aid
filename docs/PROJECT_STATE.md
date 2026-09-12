# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current phase:** P1–P16 repaired and automation-qualified; physical owner/device QA remains under P17  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## 1. Branch authority

This branch contains the current Player implementation. `main` is intentionally divergent and contains later global/Phase 5A/DM product-discovery records that are not on this branch. `main` must not be mistaken for the latest Player runtime, and this branch must not overwrite valid later discovery work on `main`.

Cross-branch authority is documented in:

- `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`;
- `docs/BRANCH_STATUS.md`.

## 2. Authorization boundary

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` records the owner's durable authorization for the accepted `preqa.8 / 40800` P1–P17 Player repair cycle.

That permits the accepted Player repair scope, validation, QA packaging and checkpoints. It does not permit unrelated Player feature invention, DM implementation, destructive history rewriting, or claiming physical acceptance without actual owner/device QA.

The owner's 2026-09-12 instruction additionally authorizes continuity documentation to be corrected on all appropriate branches, including `main`, and continuation under the real existing authorizations. That newer instruction does not waive P17 or the final Phase 4A owner-closure gate.

## 3. Repair status

- P1–P12: implemented/closed through their recorded implementation and validation evidence.
- P13: implementation/full-app copy audit complete; its formerly pending normal Scaffold condition is superseded by later successful normal gates.
- P14: Table Mode repair/re-audit closed.
- P15: Supercompact repair implemented/closed; associated transversal presentation consistency fixes belong to proving this accepted P15, not to a second independent P15 cycle.
- P16: adaptive landscape/vertical-space repair implemented/closed and automation-green.
- P17: design decision closed; it is the physical tablet-QA gate policy, not another code-repair item.

Do not restart P1–P16 absent new physical QA evidence.

## 4. Current QA candidate

Candidate identity:

- versionName: `0.4.0-preqa.9`;
- versionCode/build: `40900`;
- candidate commit: `cd0c203d337c062fa388010d300e875f2f54ced7`.

The monotonic version/build bump distinguishes this repaired candidate from the earlier physical owner-QA `0.4.0-preqa.8 / 40800` build that generated the repair backlog.

No new Player behavior was introduced by the candidate identity commit. The accepted repaired behavior was already present at `d630270f2f3d8fab94f3c1290963c2da7afaf06d`; later changes before the candidate gate were continuity documentation and package identity only.

## 5. Automated evidence

Normal Scaffold run:

`34726572588` — **SUCCESS** on exact candidate SHA `cd0c203d337c062fa388010d300e875f2f54ced7`.

Verified in the run:

- backend dependency install / Worker type-check: success;
- stable CI debug keystore preparation: success;
- Kotlin/shared/Android/Desktop build-and-test surface: success;
- Android debug APK upload: success.

Published artifact:

- ID `10307444450`;
- name `dnd-custom-aid-debug-apk`;
- size `13608921` bytes;
- GitHub Actions artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

Detailed candidate checkpoint:

`docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md`.

## 6. Physical owner-QA boundary

Automation is complete for this candidate. Owner/device acceptance is not.

P17's durable rule is failure-type based:

- hard shared/systemic primitive failure that would invalidate tablet evidence -> defer/stop tablet QA;
- soft PASS / soft FAIL / bounded local defects -> tablet QA may proceed;
- uncertain phone-specific vs responsive/shared issue -> tablet evidence may be diagnostically useful;
- no tablet PASS/FAIL may be inferred without actual tablet testing.

Targeted phone regression should cover representative shared repaired boundaries, especially canonical HP/Combat, reorder, adaptive editors/IME, settings, Table Mode, Supercompact, P16 vertical-space/landscape, Conjuros sticky/source-context behavior and persistence.

Representative physical tablet QA should cover portrait/landscape navigation/adaptive layout, Combat/P5/P16, Conjuros, P9 editor/IME, P6 reorder, PC/Application Settings, P15 Supercompact, P14 Table Mode, larger text/density, persistence/reopen and a canonical-state sanity check.

A physical failure may reopen the relevant P1–P16 point. CI cannot close this gate.

## 7. Global/DM line

Current Phase 5A/DM discovery decisions live on `main`, including the accepted Desk-family and shared Player/DM rules-question direction. Preserve them during future integration.

DM **implementation** remains blocked until Phase 4A is physically accepted and explicitly closed.

## 8. Exact continuation point

There is no remaining pre-QA implementation item that should be invented merely to keep development moving.

The next required evidence is physical owner/device QA of `0.4.0-preqa.9 / 40900`.

If QA finds a defect, reopen only the relevant accepted repair boundary and repair it on this branch. If QA accepts the repaired baseline, record explicit Phase 4A owner acceptance/closure. Only after that closure may DM implementation begin.