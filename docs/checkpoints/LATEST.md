# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Product boundary:** physical `preqa.9` QA reopened P1/P2 + transversal presentation consistency; bounded source repair is implemented through R3  
**Failed QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current product repair commit:** `cc452b156d43967d9eb794a661162c3f3a05f336`  
**Current validation:** Scaffold `34730201935` on exact R3 product commit — backend SUCCESS, Kotlin/shared/Android IN PROGRESS at this update  
**Acceptance boundary:** green bounded repair → new monotonic APK → focused owner phone recheck → then P17 tablet if no hard shared failure remains  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

Read in this order:

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — exact physical findings and R1–R3 repair status;
2. `docs/PROJECT_STATE.md` — live Player authority/gate;
3. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling accepted P1/P2 contract;
4. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md` — failed-candidate automated evidence;
5. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` and `docs/BRANCH_STATUS.md` — dual-line authority;
6. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable repair authorization;
7. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md` — tablet-QA gate policy.

## Physical evidence

`preqa.9 / 40900` passed update/persistence sanity, then failed the shared P1/P2 boundary: General HP needed extra `Guardar`; max reduction allowed invalid current > max (`20/10` observed); accepted subtle changed-HP feedback was absent; `Establecer PV` current correction was ineffective while temp correction worked; and `Daño — Cantidad — Curar` was visibly out of proportion.

Owner testing on this known-bad candidate is intentionally stopped. Preserve these observations as evidence; do not ask the owner to complete the obsolete candidate's exhaustive QA.

## Repair status

- **R1:** canonical HP state/persistence repaired and regression-locked by `e0397146…`, `9f3c888b…`, `f327b685…`, `49833bb6…`.
- **R2:** General save/navigation canonical propagation repaired at `da57a1c1e2fcb952892c75b3f1819954baaa5ce6`.
- **R3:** accepted P2 affected-display feedback and Combat action-row proportion repaired at `cc452b156d43967d9eb794a661162c3f3a05f336`. The feedback targets only PV, Temp, or both according to actual before/after state change; the action row gives symmetric action space, narrows the amount field, and shares compact control height.

The controlling P2 record explicitly requires a compact `Daño | cantidad | Curar` row and a brief/subtle glow/pulse on only affected HP display box(es), with no snackbar/toast or Undo. R3 follows that existing contract.

## Exact next action

Finish exact Scaffold run `34730201935`. If it fails, repair the failure on this branch and revalidate. If green, create the next monotonic QA identity (do not reuse `40900`), build/package the exact candidate, record exact commit/run/artifact/digest evidence, and then hand that APK to the owner for focused phone retest.

P17 tablet QA remains paused. No P18 exists. Phase 4A remains open and DM implementation remains blocked pending explicit owner closure.

## Historical automated proof for failed preqa.9

`cd0c203d337c062fa388010d300e875f2f54ced7` had Scaffold `34726572588` — SUCCESS, artifact `10307444450`, digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`. This remains valid historical CI evidence but does not override the later physical failures.
