# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; CROSS-DEVICE PHYSICAL DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation state:** consolidated repair IN PROGRESS; **Round 1 structured dice COMPLETE / GREEN; Round 2 reorder stability COMPLETE / GREEN; Round 3 compact checkbox + responsive grouping COMPLETE / GREEN**; next = **T5 spell-source/bootstrap/source-context compatibility**  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work remains inside the durable P1–P17 repair/validation authorization. No P18 exists.

Owner product decisions required by the post-P17 audit are complete. In particular, T3 uses orientation-specific adaptive card-distribution/density preferences rather than exact promised column counts, T4 applies specifically to the asymmetric **text-size** control while spacing density is already symmetric and is not part of that repair, and T9 requires an explicit haptics `None` option. Consolidated implementation is authorized.

## Controlling continuity

Resume in this order:

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md` — latest completed implementation round, source-complete checkbox migration and exact green automation evidence;
2. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md` — T1 reorder repair + exact automation evidence;
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — structured-dice/signed-modifier repair;
4. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — owner decisions + implementation authorization;
5. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — complete source/root-cause audit, repair-family contracts and targeted physical revalidation matrix;
6. `docs/TESTING.md` — synchronized current testing position, testing policy and targeted revalidation route.

Supporting physical evidence remains:

- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — latest detailed phone findings;
- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery;
- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact frozen preqa.12 candidate/run/artifact identity.

## Physical evidence status — preserve accepted evidence

### Phone

Latest detailed 23-check pass on exact `preqa.12`:

- checks 1–6 PASS;
- 7–8 OPEN structured-damage modifier defects; **Round 1 implementation complete / automation green; targeted physical revalidation pending new consolidated candidate**;
- 9 PASS + direct sign-toggle UX request; **Round 1 implementation complete / automation green; targeted physical revalidation pending**;
- 10–16 PASS, with 16 only an optional compact-density refinement;
- 17.1–17.3 OPEN systemic checkbox/responsive grouping family; **Round 3 implementation complete / automation green; targeted physical revalidation pending**;
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
11. representative non-spell editor/IME PASS + same phone 17.1 checkbox family; **Round 3 implementation complete / automation green; targeted physical revalidation pending**;
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

Round product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`. Authoritative Scaffold `34787688776` / run `1508` — **SUCCESS**.

Implemented shared `NdS±M` parsing/rolling, independent quantity/sides/modifier draft state, explicit signed serialization, direct compact sign-toggle behavior, focused tests and a durable control guard. Phone 7–9 are implemented but remain targeted-physical-revalidation pending. T2 remains partial because result silhouettes, Custom Throw die/modifier UX and Dice-tab display-mode ownership remain.

### Round 2 — T1 reorder target stability: COMPLETE / GREEN

Round baseline `de5f2aeb55e6e3f4558d58103f0781b4baa960fa`; product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`.

Implemented:

- stable drag-start target geometry rather than animated preview-bound feedback;
- canonical-order preview generation instead of feeding preview order back into targeting;
- 12% geometric hysteresis/deadband around target boundaries;
- explicit stable-slot translation for real viewport auto-scroll;
- one-time capture for newly revealed lazy targets;
- same contract across one-dimensional/shared and spatial reorder engines;
- focused common tests and a persistent CI reorder-stability guard.

Authoritative Scaffold run `34788409987` / run number `1519` at `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb` — **SUCCESS**. Backend, compact geometry guard, reorder stability guard, Kotlin/shared tests, Android build and debug APK upload all passed.

T1 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Existing physical video remains the failure baseline; do not ask the owner to repeat or reattach it. T1 may not be physically closed from one-column evidence alone: targeted revalidation must cover **both one-column and multi-column/spatial reorder**, with no target-chasing reflow, plus applicable auto-scroll and final persisted order after leave/reopen.

### Round 3 — compact checkbox + responsive grouping: COMPLETE / GREEN

Round product/test HEAD `8455d8015e0bc6f7b4a6f813e56b03c5f9a2915c`. Authoritative steady-state Scaffold `34793215805` / run `1536` — **SUCCESS**.

Implemented:

- one shared compact/touch-safe Player checkbox language in `CharacterCheckboxPrimitivesV4.kt`;
- 24dp visible checkbox with >=48dp interaction envelope and whole-row toggle semantics where appropriate;
- consistent compact label typography/spacing and explicit enabled/read-only semantics;
- responsive `FlowRow` packing so groups share a line when they fit and wrap only when needed;
- semantic source/prepared checkbox pairs kept together;
- Equipment `Equipado` / `Especial` / `Equipo especial` / `Sintonizado` migration;
- Conjuros source/prepared groups plus V/S/M/Concentración/Ritual responsive grouping;
- complete source migration of **19 raw Material Checkbox calls across seven Player files**, including the additional legacy Gestión rest selector discovered during implementation;
- permanent source guard enforcing zero raw Material Checkbox use outside the shared primitive and the expected responsive/grouping contract;
- `Switch` controls left intact because audit evidence did not establish them as defective.

The temporary CI source-writer/migration helper used to land the seven-file conversion was retired after migration. Normal Scaffold is read-only again apart from standard build/artifact actions.

Authoritative run `1536` passed backend, compact geometry guard, reorder stability guard, checkbox consistency guard, Kotlin/shared tests, Android/Desktop build and debug APK upload. Artifact ID `10327879251`; GitHub Actions artifact digest `sha256:2af0eed7f7cb1de625d670beb6ddc2b4681dbf57d63b3e1a541f13d2a94c7b34`.

Phone 17.1–17.3 and the tablet checkbox reproduction are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. They are not physically PASS yet.

### Remaining repair families

- **Next / T5 spell source/bootstrap:** canonical class/origin availability must drive Conjuros source availability while preserving compatible source/profile overlays, IDs/associations, persistence/import/export and manual/homebrew sources.
- **T6 class editor controls:** numeric keyboards + standard die/`Otro…` selector.
- **Application Settings:** approved T3 adaptive portrait/landscape card-density semantics + **T4 text-size** symmetric around 100 + T9 explicit haptics None. Spacing density is already symmetric and is not a T4 target.
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

1. Execute **T5 spell-source/bootstrap/source-context compatibility repair** from the audited contract, preserving existing source/profile IDs and associations and avoiding destructive migration.
2. At the end of every bounded implementation/test round, update **all four** durable continuity surfaces before proceeding: the dedicated round checkpoint, this `PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, and `docs/TESTING.md` current testing status/route.
3. Continue remaining repair families in dependency-aware order, preserving accepted physical evidence.
4. Run the normal aggregate Scaffold gate over the completed consolidated repair.
5. Create/freeze a new monotonic physical-QA candidate after material product changes.
6. Perform only targeted cross-device revalidation for failed/touched/affected families, phone 21/affected phone 22, and tablet 18 once T5 is repaired.
7. Phase 4A may close only after repaired evidence is sufficient and the owner explicitly accepts/closes it.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. DM implementation remains blocked until explicit Phase 4A owner closure.
