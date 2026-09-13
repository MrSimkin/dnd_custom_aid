# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; CROSS-DEVICE PHYSICAL DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current phase:** post-P17 cross-device source audit complete; consolidated repair plan ready; waiting only for owner confirmation of T3 Columns-setting semantics before implementation  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work remains inside the durable P1–P17 repair/validation authorization. No P18 exists.

## Controlling continuity

Resume from:

`docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md`

That checkpoint contains the complete post-P17 source/root-cause audit, repair-family grouping, compatibility constraints, implementation order, automated verification and targeted physical revalidation matrix.

Supporting physical evidence remains:

- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — latest detailed phone findings;
- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery;
- `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact candidate/run/artifact identity.

## Physical evidence status

### Phone — preserve accepted evidence

Latest detailed 23-check pass on exact `preqa.12`:

- checks 1–6 PASS;
- 7–8 OPEN structured-damage modifier defects;
- 9 PASS + direct sign-toggle UX request;
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

## Post-P17 source audit — confirmed repair families

The audit confirms these source-level families rather than treating each physical symptom as an isolated screen patch:

- **Structured dice/result:** phone 7–9 + T2. Positive modifier serialization currently turns `1d8` + `2` into `1d82`; the shared roller also parses only bare `NdS`, so optional signed modifiers must be repaired end-to-end.
- **Reorder target stability:** T1 is one shared live-geometry feedback family across one-dimensional and spatial reorder engines; preview animation changes bounds that are immediately reused as retarget input.
- **Checkbox/responsive toggle grouping:** phone 17.1–17.3 is systemic. Raw Material Checkbox sites were confirmed across active Equipment, Spells, Companions, class-option, Artifice and Management UI; implement a shared compact primitive + responsive packing + durable source guard.
- **Spell source/bootstrap:** T5 is an architectural mismatch. Canonical classes exist independently of manually managed spellcasting sources, unlike Rasgos provenance. Canonical origins should drive source availability while existing spellcasting source/profile data remains a compatibility-safe configuration overlay.
- **Class editor controls:** T6 uses legacy text inputs for level/remaining hit dice/hit die; replace with numeric keypad and standard-die + `Otro…` selector while preserving catalog preselection.
- **Wide Combat composition:** T7 comes from the active successor screen rendering attack/action cards full-width with no adaptive wide composition.
- **Table Mode:** T8 is an affordance bug. Shared policy already defines structural editing as disabled while operational actions stay enabled; structural edit controls must stop appearing as normal actionable editors.
- **Application Settings:** T3 exact column selectors are semantically misleading because effective columns are clamped/ignored differently by form factor, text scale and screen; T4 text-size options are asymmetric around 100 while spacing density is already symmetric.
- **Optional compact-field refinement:** phone 16 is non-blocking and should change only if preview proves a safe visual benefit.

## T3 owner choice required before product implementation

Recommended: replace the four exact-count Columns selectors with one adaptive **card distribution / density** preference. The runtime computes effective columns from available width, text scale and target minimum card width. This matches the adaptive layout model and avoids promising an exact count the runtime may legally clamp.

Fallback: retain context-specific selectors but define them explicitly as **maximum columns**, constrain values to actual maxima and explain that width/text scale can reduce the effective count.

This is the only current repair family requiring an explicit owner product-semantics decision. The remaining repair directions are grounded in existing contracts, source behavior and physical evidence.

## Candidate identity remains unchanged

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

No product source, APK, version or artifact changed during P17 recording or this source audit.

## Exact route / next action

1. Owner selects the T3 Columns-setting semantics.
2. Implement the dependency-aware consolidated repair plan on this branch with focused tests and guards.
3. Run the normal aggregate Scaffold gate.
4. Create/freeze a new monotonic physical-QA candidate after product changes.
5. Targeted revalidation only: failed/touched/affected phone + tablet families, phone 21 settings persistence, affected slice of phone 22, and tablet 18 once T5 is repaired. Do not replay unrelated accepted PASS evidence.
6. Phase 4A may close only after repaired evidence is sufficient and the owner explicitly accepts/closes it.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. DM implementation remains blocked until explicit Phase 4A owner closure.
