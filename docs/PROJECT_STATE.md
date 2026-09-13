# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; CROSS-DEVICE PHYSICAL DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation state:** consolidated repair IN PROGRESS; **Round 1 structured dice COMPLETE / AUTOMATION GREEN**; next = Round 2 T1 reorder target stability  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work remains inside the durable P1–P17 repair/validation authorization. No P18 exists.

Owner product decisions required by the post-P17 audit are complete. In particular, T3 uses orientation-specific adaptive card-distribution/density preferences rather than exact promised column counts, and T9 requires an explicit haptics `None` option. Consolidated implementation is authorized.

## Controlling continuity

Resume in this order:

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — latest completed implementation round and exact automation evidence;
2. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — owner decisions + implementation authorization;
3. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — complete source/root-cause audit, repair-family contracts and targeted physical revalidation matrix.

Supporting physical evidence remains:

- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — latest detailed phone findings;
- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery;
- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact frozen preqa.12 candidate/run/artifact identity.

## Physical evidence status — preserve accepted evidence

### Phone

Latest detailed 23-check pass on exact `preqa.12`:

- checks 1–6 PASS;
- 7–8 OPEN structured-damage modifier defects; **Round 1 implementation complete, targeted physical revalidation pending new consolidated candidate**;
- 9 PASS + direct sign-toggle UX request; **Round 1 implementation complete, targeted physical revalidation pending**;
- 10–16 PASS, with 16 only an optional compact-density refinement;
- 17.1–17.3 OPEN systemic checkbox/responsive grouping family;
- 18–20 PASS;
- 21 UNASSESSED;
- 22 PARTIAL/AMBIGUOUS;
- 23 PASS.

The earlier complete-phone CLOSED/PASS interpretation remains superseded, but individual valid PASS evidence remains preserved.

### Tablet P17 — discovery COMPLETE

Exact `preqa.12` tablet evidence:

1. install/update + launch PASS;
2. campaign baseline PASS;
3. portrait navigation/adaptive shell PASS;
4. landscape navigation/adaptive shell PASS;
5. rotation/state sanity PASS;
6. Combat portrait PASS;
7. Combat landscape FAIL / T7;
8. canonical HP synchronization PASS;
9. Conjuros portrait FAIL / T5;
10. Conjuros landscape FAIL / same T5; visible landscape controls otherwise good;
11. representative non-spell editor/IME PASS + same phone 17.1 checkbox family;
12. PC Settings PASS;
13. Application Settings responsiveness PASS;
14. Supercompact PASS;
15. Table Mode FAIL / T8;
16. larger text/density PASS;
17. cold persistence/reopen PASS;
18. Conjuros sticky BLOCKED BY T5, not a new failure.

No further broad tablet QA is required on `preqa.12`.

## Consolidated repair progress

### Round 1 — structured dice / signed modifier foundation: COMPLETE / GREEN

Round baseline: `80126079f79d55c34724a3b596c066d63fe665e5`.

Product/test chain:

- `5ea3f521049d2143fb4f6a6e326139e0b21788cb` — shared `NdS±M` parser/result support;
- `570304505f9d2ba89a3666839c3a06178cf7d0db` — focused positive/negative/bare/incomplete parser and rolling tests;
- `046d549bdc0dd8564c0d532e8cc021835bda731c` — Android structured modifier serialization/state + direct sign control;
- `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0` — durable geometry/source guard updated to enforce the new direct-sign contract.

Authoritative green Scaffold: run `34787688776` / run number `1508` at `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0` — **SUCCESS**. Guard, Kotlin/shared tests, Android build/APK upload and backend all passed.

The earlier run `34787610695` failed only because the old guard still required the intentionally removed sign-dropdown marker; that stale guard was corrected in the same round.

Round 1 fixes the implementation basis for phone 7–9 and the shared signed-modifier part of T2. It does not yet complete T2 die silhouettes, Custom Throw die/modifier UX or Dice-tab display-mode ownership.

### Remaining repair families

- **Round 2 / next:** T1 reorder target stability across one-dimensional and spatial engines.
- **Checkbox/responsive toggle grouping:** phone 17.1–17.3 and tablet reproduction; shared compact primitive + migration + guard + responsive packing.
- **Spell source/bootstrap:** T5; canonical class origins drive source availability while preserving compatible source/profile overlays and IDs.
- **Class editor controls:** T6 numeric keyboards + standard die/`Otro…` selector.
- **Application Settings:** approved T3 adaptive portrait/landscape card-density semantics + T4 symmetric text scale + T9 explicit haptics None.
- **Wide Combat composition:** T7 adaptive wide/card layout using stabilized reorder/layout primitives.
- **Table Mode:** T8 structural affordances hidden/disabled while operational controls remain enabled.
- **T2 remaining integration:** die-specific result silhouettes, Custom Throw die + custom sides + signed modifier, display-mode control moved to Dice tab.
- **Optional phone-16 compact-field refinement:** only if visual comparison proves safe benefit.

## Frozen candidate identity remains unchanged

The new repair code is **not yet a frozen physical-QA candidate**. Until the consolidated repair is complete and versioned, the exact frozen candidate remains:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Do not physically revalidate each intermediate round independently unless a repair uncovers a hard blocker. The intended route remains one consolidated new monotonic candidate followed by targeted cross-device revalidation.

## Exact route / next action

1. Execute **Round 2: T1 reorder target stability** with focused tests.
2. At the end of every bounded implementation/test round, update a durable round checkpoint, this `PROJECT_STATE.md`, and `docs/checkpoints/LATEST.md` before proceeding.
3. Continue remaining repair families in dependency-aware order, preserving accepted physical evidence.
4. Run the normal aggregate Scaffold gate over the completed consolidated repair.
5. Create/freeze a new monotonic physical-QA candidate after material product changes.
6. Perform only targeted cross-device revalidation for failed/touched/affected families, phone 21/affected phone 22, and tablet 18 once T5 is repaired.
7. Phase 4A may close only after repaired evidence is sufficient and the owner explicitly accepts/closes it.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. DM implementation remains blocked until explicit Phase 4A owner closure.
