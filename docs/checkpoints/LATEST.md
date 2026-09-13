# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Product boundary:** failed physical `preqa.9` QA reopened P1/P2 + transversal presentation consistency; bounded repair is implemented and regression-locked through R3  
**Failed QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current product boundary:** `ddd9d01dab4f0b45470174712a5c115de1112d90`  
**Current validation:** Scaffold `34730363231` on exact current product boundary — IN PROGRESS at this update  
**Acceptance boundary:** green R1–R3 → next monotonic APK → focused owner phone recheck → then P17 tablet if no hard shared failure remains  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — exact physical findings + R1–R3 repair evidence.
2. `docs/PROJECT_STATE.md` — live Player authority/gate.
3. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling P1/P2 contract.
4. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md` — failed-candidate evidence.
5. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` + `docs/BRANCH_STATUS.md` — dual-line authority.
6. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable authorization.
7. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md` — tablet gate policy.

## Physical evidence

`preqa.9 / 40900` passed update/persistence sanity, then failed the shared P1/P2 boundary: General HP required extra `Guardar`; max reduction allowed invalid current > max (`20/10` observed); subtle affected-HP feedback was absent; `Establecer PV` current correction failed while temp correction worked; and `Daño — Cantidad — Curar` was visibly out of proportion.

Owner testing on that known-bad candidate is intentionally stopped. Preserve its observations; do not ask for exhaustive QA on it.

## Repair status

- **R1:** canonical HP persistence/invariants repaired and regression-locked (`e0397146…`, `9f3c888b…`, `f327b685…`, `49833bb6…`).
- **R2:** General save/navigation canonical propagation repaired at `da57a1c1e2fcb952892c75b3f1819954baaa5ce6`.
- **R3:** targeted P2 feedback + Combat action-row proportion repaired at `cc452b156d43967d9eb794a661162c3f3a05f336`, then hardened through shared impact classification `ef051af8…`, UI consumption `ff06bdf1…`, and regression tests `ddd9d01d…`.

The accepted P2 contract requires a compact `Daño | cantidad | Curar` row and brief/subtle feedback on only affected HP display box(es). The current implementation machine-locks the targeting cases: none, PV only, Temp only, and both on spillover.

## Exact next action

Finish exact Scaffold run `34730363231`. If it fails, repair and revalidate. If green, assign the next monotonic QA identity (current declared identity remains `preqa.9 / 40900`; do not reuse `40900`), package the exact candidate, record commit/run/artifact/digest evidence, and hand that APK to the owner for focused phone retest.

P17 tablet QA remains paused. No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.

## Historical automated proof for failed preqa.9

`cd0c203d337c062fa388010d300e875f2f54ced7` had Scaffold `34726572588` — SUCCESS, artifact `10307444450`, digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`. Historical CI remains valid for its tested scope but does not override the physical failures.
