# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; CROSS-DEVICE PHYSICAL DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation state:** consolidated repair IN PROGRESS; **Rounds 1–3 + T5 COMPLETE / AUTOMATION GREEN**; next = **T6 class-editor numeric keyboard + standard die / `Otro…` selector**  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work remains inside the durable P1–P17 repair/validation authorization. No P18 exists.

Owner product decisions required by the post-P17 audit are complete. In particular:

- T3 uses orientation-specific adaptive card-distribution/density preferences rather than exact promised column counts;
- T4 applies specifically to the asymmetric **text-size** control; spacing density is already symmetric and is not part of that repair;
- T9 requires an explicit haptics `None` option.

Consolidated implementation is authorized.

## Controlling continuity

Resume in this order:

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md` — latest completed repair family, compatibility contract and exact green steady-state automation evidence;
2. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md` — checkbox/responsive repair, toggle classification and green automation evidence;
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md` — T1 reorder repair + exact automation evidence;
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — structured-dice/signed-modifier repair;
5. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — owner decisions + implementation authorization;
6. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — complete source/root-cause audit, repair-family contracts and targeted physical revalidation matrix;
7. `docs/TESTING.md` — synchronized current testing position, testing policy and targeted revalidation route.

Supporting physical evidence remains:

- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — detailed phone findings;
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
9. Conjuros portrait FAIL / T5; **T5 implementation complete / automation green; targeted physical revalidation pending**;
10. Conjuros landscape FAIL / same T5; **implementation complete / automation green; targeted physical revalidation pending**;
11. representative non-spell editor/IME PASS + same phone 17.1 checkbox family; **Round 3 implementation complete / automation green; targeted physical revalidation pending**;
12. PC Settings PASS;
13. Application Settings responsiveness PASS;
14. Supercompact PASS;
15. Table Mode FAIL / T8;
16. larger text/density PASS;
17. cold persistence/reopen PASS;
18. Conjuros sticky was BLOCKED BY T5 on `preqa.12`; **T5 is now implemented/green, so this becomes targeted physical revalidation on the future consolidated candidate rather than a separate implementation defect**.

No further broad tablet QA is required on `preqa.12`.

## Consolidated repair progress

### Round 1 — structured dice / signed modifier foundation: COMPLETE / GREEN

Product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`. Authoritative Scaffold `34787688776` / run `1508` — **SUCCESS**.

Implemented shared `NdS±M` parsing/rolling, independent quantity/sides/modifier draft state, explicit signed serialization, direct compact sign-toggle behavior, focused tests and a durable control guard. Phone 7–9 are implemented but remain targeted-physical-revalidation pending. T2 remains partial because result silhouettes, Custom Throw die/modifier UX and Dice-tab display-mode ownership remain.

### Round 2 — T1 reorder target stability: COMPLETE / GREEN

Product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`. Authoritative Scaffold `34788409987` / run `1519` — **SUCCESS**.

Implemented stable drag-start target geometry, canonical-order preview generation, 12% hysteresis/deadband, explicit stable-slot translation during real viewport auto-scroll, one-time capture for newly revealed lazy targets, the same behavioral contract across one-dimensional and spatial reorder engines, focused common tests and a persistent CI guard.

T1 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. The existing owner video remains the failure baseline and must not be requested again. Physical closure requires targeted evidence for **both one-column and multi-column/spatial reorder**, no target-chasing preview reflow, applicable drag auto-scroll and final persisted order after leave/reopen.

### Round 3 — compact checkbox + responsive grouping: COMPLETE / GREEN

Committed migrated product source `582a809bafbc4d7d38836283ed0eb5fe33d94624`; final product/test HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; authoritative Scaffold `34793253151` / run `1537` — **SUCCESS**.

Implemented shared compact/touch-safe checkbox primitives, responsive Equipment/Conjuros packing, semantic source/prepared pairs, source-complete migration of 19 raw Material Checkbox calls across seven Player files, permanent raw-checkbox guard, and explicit related-toggle classification (11 legitimate Material `Switch` sites; 0 `TriStateCheckbox`).

Phone 17.1–17.3 and the tablet checkbox reproduction are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

### T5 — spell-source bootstrap / source-context compatibility: COMPLETE / GREEN

Core editor integration `2e7fda2852425594971f7df47b433d642eb2119a`; final tested steady-state HEAD `3774c53f5189ebd535cc1b73ec18493e268e5d9f`. Authoritative Scaffold `34794589758` / run `1560` — **SUCCESS**.

Implemented a bounded canonical base-caster metadata/reconciliation layer and editor integration:

- canonical class ownership now ensures source availability by exact `linkedClassId`;
- existing compatible source IDs and spell associations are preserved;
- existing configured source profiles remain unchanged;
- missing/unconfigured canonical profiles receive the bounded default casting ability;
- manual/homebrew `OTHER` sources are retained;
- old/new canonical casters receive a non-destructive source/profile projection in the editor and normal Save persists it;
- Conjuros becomes available while a persisted canonical source is missing, solving the Mago bootstrap trap without turning the app into a spell-legality engine;
- class-change reconciliation consumes the existing projected domain class model rather than duplicating editor-draft conversion;
- focused shared tests plus a permanent T5 source guard protect the contract.

Supported bounded base-caster metadata: Artificer (2025/5e) INT; Bard CHA; Cleric WIS; Druid WIS; Paladin CHA; Ranger WIS; Sorcerer CHA; Warlock CHA; Wizard/Mago INT. Fighter/Rogue subclass casting and broader spell legality remain intentionally outside T5.

Run `1560` passed backend, compact geometry guard, reorder stability guard, checkbox consistency guard, T5 bootstrap guard, shared tests, Android/Desktop build and APK upload. Artifact `10329072751`; GitHub Actions artifact digest `sha256:26fe76650eb90a56ac2b5b239a3fd75ccb1b70bc2faf41003fac316ed5a04aae`.

T5 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. It is not physically PASS yet.

## Remaining repair families

- **Next / T6 class editor controls:** numeric keyboards + standard die/`Otro…` selector.
- **Application Settings:** approved T3 adaptive portrait/landscape card-density semantics + **T4 text-size** symmetric around 100 + T9 explicit haptics None. Spacing density is already symmetric and is not a T4 target.
- **Wide Combat composition:** T7 adaptive wide/card layout using stabilized reorder/layout primitives.
- **Table Mode:** T8 structural affordances hidden/disabled while operational controls remain enabled.
- **T2 remaining integration:** die-specific result silhouettes, Custom Throw die + custom sides + signed modifier, display-mode control moved to Dice tab.
- **Optional phone-16 compact-field refinement:** only if visual comparison proves safe benefit.

## Frozen candidate identity remains unchanged

The repair code is **not yet a frozen physical-QA candidate**. Until the consolidated repair is complete and versioned, the exact frozen candidate remains:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Do not physically revalidate each intermediate round independently unless a repair uncovers a hard blocker. The intended route remains one consolidated new monotonic candidate followed by targeted cross-device revalidation.

## Exact route / next action

1. Execute **T6 class-editor numeric keyboard + standard die / `Otro…` selector** from the audited contract.
2. At the end of every bounded implementation/test round, update **all four** durable continuity surfaces before proceeding: the dedicated round checkpoint, this `PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, and `docs/TESTING.md` current testing status/route.
3. Continue remaining repair families in dependency-aware order, preserving accepted physical evidence.
4. Run the normal aggregate Scaffold gate over the completed consolidated repair.
5. Create/freeze a new monotonic physical-QA candidate after material product changes.
6. Perform only targeted cross-device revalidation for failed/touched/affected families, phone 21/affected phone 22, and tablet 18 now that T5 is repaired.
7. Phase 4A may close only after repaired evidence is sufficient and the owner explicitly accepts/closes it.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. DM implementation remains blocked until explicit Phase 4A owner closure.
