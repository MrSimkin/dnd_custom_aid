# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Failed physical candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current repaired physical-QA candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — AUTOMATION GREEN / REOPENED R1–R3 PHONE BOUNDARY PHYSICALLY PASSED  
**Exact candidate run:** `34730531791` — SUCCESS  
**Acceptance boundary:** remaining targeted phone regression → representative P17 tablet QA if no hard shared failure remains → explicit owner closure  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md` — exact current candidate, automation/artifact evidence, and physical R1–R3 recheck result.
2. `docs/PROJECT_STATE.md` — live Player authority and current remaining QA gate.
3. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — historical source failures and R1–R3 repair chain.
4. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling accepted P1–P17 behavior.
5. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` + `docs/BRANCH_STATUS.md` — dual-line authority.
6. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable authorization.
7. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md` — tablet gate policy.

## What preqa.9 established

`preqa.9 / 40900` passed update/persistence sanity, then failed the shared P1/P2 boundary: General HP required extra `Guardar`; max reduction allowed invalid current > max (`20/10` observed); subtle affected-HP feedback was absent; `Establecer PV` current correction failed while temp correction worked; and `Daño — Cantidad — Curar` was visibly out of proportion.

Owner testing on that known-bad candidate was intentionally stopped and its observations preserved.

## Repair status

- **R1:** canonical HP persistence/invariants repaired and regression-locked.
- **R2:** General save/navigation canonical propagation repaired.
- **R3:** targeted P2 feedback + Combat action-row proportion repaired and regression-locked, including machine-tested none/PV-only/Temp-only/both feedback targeting.
- Exact regression-locked R3 run `34730363231` on `ddd9d01dab4f0b45470174712a5c115de1112d90`: **SUCCESS**.

## Current candidate evidence

Dedicated checkpoint: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md`.

- `0.4.0-preqa.10 / 41000`
- commit `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`
- Scaffold `34730531791` — **SUCCESS**
- artifact ID `10308364110`
- artifact name `dnd-custom-aid-debug-apk`
- ZIP size `13,611,496` bytes
- GitHub artifact digest `sha256:ef44559ba61267837a286e996d74d5c00b55d89b003d7f6e69bf0902cff5dd32`
- independently downloaded ZIP SHA-256: exact same digest
- APK size `38,865,008` bytes
- independent APK SHA-256 `ee5db263883c8b1979b12cc9ca365ba9f190009b2b46e4e9b53681408c128c78`

## Physical preqa.10 repaired-boundary result

Owner report: **`1–7 OK`** for the focused repaired phone boundary.

Physical PASS covers:

1. General HP → Combate without requiring global `Guardar` merely to cross tabs;
2. max-HP reduction clamps current HP and does not project `current > max`;
3. temp-only damage highlights Temp only;
4. spillover damage highlights Temp + PV;
5. healing highlights PV when it changes;
6. `Establecer PV` exact correction changes/projects current/max HP canonically;
7. `Daño | cantidad | Curar` sizing/proportion/padding is coherent with the surrounding HUD.

These were the specific shared defects that invalidated `preqa.9`; they are now physically cleared on `preqa.10`. Do not repeat them unless later evidence contradicts this pass.

## Exact next action — remaining targeted phone regression

Begin with the compact/fixed Combate HUD under constrained vertical space and phone landscape behavior. Then continue representative P6 reorder persistence, P9 editor/IME reachability, Application/PC settings, P14 Table Mode, P15 Supercompact, P16 vertical-space behavior, Conjuros sticky/source context, and final persistence/reopen/canonical-state sanity.

If phone testing reveals no hard shared/systemic failure, proceed to representative P17 tablet portrait/landscape QA under the existing policy. A bounded/local phone defect does not automatically block unrelated tablet evidence.

No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.
