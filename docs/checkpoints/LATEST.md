# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Product boundary:** P1–P16 repaired and automation-qualified  
**QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Acceptance boundary:** physical owner/device QA under P17 — IN PROGRESS  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

Read first:

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — live physical owner/device QA evidence and exact next test boundary;
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md` — exact candidate, automated evidence and full QA scope;
3. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` — cross-branch truth and authorization boundary;
4. `docs/PROJECT_STATE.md` — current Player state;
5. `docs/BRANCH_STATUS.md` — branch roles and historical refs;
6. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable P1–P17 repair authorization;
7. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md` — P17 physical tablet-QA gate policy.

Historical decision/audit/implementation checkpoints remain evidence. Older `LATEST`/state instructions that say P13 validation, aggregate validation, a version bump, P1–P16 implementation, or the initial preqa.9 installation/persistence check are still pending are superseded by this live pointer.

## Current automated proof

QA candidate commit:

`cd0c203d337c062fa388010d300e875f2f54ced7`

Identity:

`0.4.0-preqa.9 / 40900`

Normal Scaffold run:

`34726572588` — **SUCCESS** on the exact candidate SHA.

Artifact:

- ID `10307444450`;
- name `dnd-custom-aid-debug-apk`;
- size `13608921` bytes;
- artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

The run demonstrates backend type-check success, Kotlin/shared/Android/Desktop build-and-test success, and Android debug APK upload success.

## Current physical QA evidence

The owner installed `preqa.9 / 40900` over the previous QA installation without clearing app data and reported the initial steps 1–6 as **PASS**.

Recorded evidence includes:

- update-in-place installation succeeded;
- application launched normally;
- existing campaign and test character remained present;
- representative saved data remained present across General, Combate, Equipo/Monedas, Conjuros and Notas sanity inspection;
- full close/reopen succeeded with checked data preserved.

This establishes the initial upgrade/persistence sanity boundary for `preqa.9` as real physical owner/device PASS evidence. It does not imply broader Phase 4A acceptance.

## Current interpretation

- P1–P16 accepted repairs: implemented / automation-qualified;
- preqa.9 initial upgrade/persistence sanity: **PHYSICAL OWNER PASS**;
- P17 design decision: closed;
- remaining targeted phone QA: in progress;
- P17 physical tablet evidence: pending;
- owner/device acceptance: pending;
- Phase 4A explicit closure: pending;
- DM implementation: blocked until that explicit closure.

P17 is a QA gate policy, not a hidden code-repair increment.

## Exact next action

Continue physical QA of `0.4.0-preqa.9 / 40900` with **canonical HP synchronization and damage/healing behavior across General and Combate**.

If that boundary passes, continue through the remaining representative repaired areas. If a physical defect is found, reopen only the relevant P1–P16 repair boundary and repair it on this branch. A bounded/local phone defect does not automatically invalidate tablet evidence; a hard shared/systemic failure may defer tablet QA under the recorded P17 policy.

Do not create unrelated Player work simply to continue coding. If the owner accepts the repaired baseline after the required physical evidence, record explicit Phase 4A acceptance/closure before any DM implementation.

## Cross-branch reminder

`main` is intentionally divergent and carries later global/Phase 5A/DM discovery decisions absent here. This branch carries current Player runtime work absent from `main`.

Do not force one branch over the other. Future integration must preserve both valid lines.