# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Failed physical candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current repaired physical-QA candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — AUTOMATION GREEN  
**Exact candidate run:** `34730531791` — SUCCESS  
**Acceptance boundary:** focused owner phone recheck of repaired P1/P2/presentation failures → remaining targeted phone regression → P17 tablet if no hard shared failure remains  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md` — exact current candidate, automation and artifact evidence, focused physical recheck boundary.
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — source physical failures and R1–R3 repair chain.
3. `docs/PROJECT_STATE.md` — live Player authority/gate.
4. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling P1/P2 contract.
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

## Exact next action — focused phone repair recheck

On the exact `preqa.10` APK, test the repaired failures first:

1. General HP → Combate without global `Guardar` merely to cross tabs;
2. max-HP reduction clamps current HP; no `current > max` state;
3. temp-only damage highlights Temp only;
4. spillover damage highlights Temp + PV;
5. healing highlights PV;
6. `Establecer PV` exact correction changes/project current/max HP canonically;
7. `Daño | cantidad | Curar` sizing/proportion/padding is coherent with the surrounding HUD.

Do not restart all prior passes before this repair recheck. If this boundary passes, continue remaining targeted phone regression and then P17 tablet QA according to policy.

P17 tablet QA remains paused until the focused phone repair boundary clears. No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.
