# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — PHONE + TABLET DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation gate:** consolidated post-P17 repair IN PROGRESS; **Round 1 structured dice COMPLETE / AUTOMATION GREEN**; next = **Round 2 T1 reorder stability**  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — **latest completed implementation round; exact commits + green automation evidence**.
2. `docs/PROJECT_STATE.md` — live Player authority/current implementation state and next round.
3. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — owner decisions + explicit implementation authorization.
4. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit + repair-family contracts + targeted revalidation matrix.
5. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery evidence.
6. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — controlling detailed phone findings.
7. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact preqa.12 candidate/run/artifact/digest evidence.
8. `docs/TESTING.md` — physical-QA and failure-handling policy.

## Round 1 — COMPLETE / AUTOMATION GREEN

Scope: phone 7–9 structured-damage signed-modifier foundation + shared `NdS±M` parser/roller gap.

Implemented chain:

- `5ea3f521049d2143fb4f6a6e326139e0b21788cb` — shared parser/result supports bare, positive and negative modifiers;
- `570304505f9d2ba89a3666839c3a06178cf7d0db` — focused parser/roller tests for bare, `+`, `-`, malformed and incomplete expressions;
- `046d549bdc0dd8564c0d532e8cc021835bda731c` — Android quantity/sides/modifier-sign/modifier-magnitude state separated; explicit signed serialization; direct compact sign toggle;
- `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0` — durable control guard updated to enforce the new direct-toggle/independent-modifier contract.

Authoritative verification: Scaffold run `34787688776` / run number `1508` at `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0` — **SUCCESS**. Guard, shared/Kotlin tests, Android build/APK upload and backend passed.

The previous run `34787610695` failed only because the old geometry guard still required the intentionally removed dropdown marker; the guard was repaired in the same round and the superseding run is green.

Physical findings 7–9 are **implemented but not yet physically revalidated**. Do not mark them PASS until the new consolidated candidate is frozen and targeted physical QA is performed.

T2 is only partial after Round 1: die-specific result silhouettes, Custom Throw die/custom-sides/signed-modifier UX, and moving dice-display-mode ownership to the Dice tab remain later work.

## Next round — T1 reorder target stability

Use the already-audited root cause and existing physical video evidence; do not ask the owner to re-explain or reattach it.

Repair both active reorder paths so preview animation/recomposition bounds do not continuously retarget the active drag. Retarget from pointer motion and explicit auto-scroll/viewport changes using stable active-drag target geometry and appropriate hysteresis/deadband. Preserve final persisted order and one-column + spatial behavior. Land focused target-stability tests and update status again at the end of the round before proceeding.

## Owner decisions controlling later rounds

- **T3:** separate Portrait/Landscape adaptive card-distribution preferences (`Comfortable / Balanced / Compact / Dense` semantics; `Balanced` normal default). Runtime computes safe effective columns from width, orientation preference, text/UI density and minimum card width. Legacy exact-count prefs map compatibly.
- **T9:** explicit haptics `None`; existing users retain their prior non-none choice across upgrade.
- **T4:** main text-size scale should be symmetric around 100; preferred normal range `50,60,70,80,90,100,110,120,130,140,150` with compatibility-safe mapping.

## Physical evidence preserved

Phone exact preqa.12: 1–6 PASS; 7–8 OPEN/now implemented pending revalidation; 9 functional PASS + now-implemented direct-toggle refinement pending targeted revalidation; 10–16 PASS; 17.1–17.3 OPEN; 18–20 PASS; 21 UNASSESSED; 22 PARTIAL/AMBIGUOUS; 23 PASS.

Tablet exact preqa.12: 1–6 PASS; 7 FAIL/T7; 8 PASS; 9–10 FAIL/T5; 11 PASS + checkbox family; 12–14 PASS; 15 FAIL/T8; 16–17 PASS; 18 BLOCKED BY T5.

Do not replay unrelated accepted evidence.

## Frozen candidate evidence unchanged until a new consolidated build is versioned

- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

## Exact route

1. Round 2 T1 reorder stabilization + focused tests + round status update.
2. Continue remaining dependency-aware repair rounds, updating durable status after each.
3. Run aggregate Scaffold over the completed consolidated repair.
4. Freeze a new monotonic physical-QA candidate.
5. Perform targeted cross-device revalidation only for failed/touched/affected families plus phone 21/affected phone 22 and tablet 18 after T5.
6. Explicit owner Phase 4A closure only after repaired evidence is sufficient.

No P18 exists. DM implementation remains blocked until explicit Phase 4A closure.
