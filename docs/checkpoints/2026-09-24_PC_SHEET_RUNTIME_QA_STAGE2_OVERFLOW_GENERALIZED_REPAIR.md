# Checkpoint — PC Sheet Android runtime QA Stage 2 overflow and generalized bounded-text repair route

**Date:** 2026-09-24 (Chile local time)  
**Integrated main entering this checkpoint:** `289eafda731c4b1c61e76a947a86ab5d948c537c`  
**Current integrated Android QA build entering repair:** `0.5.0-preqa.2` / build `50200`  
**Repair candidate Android QA build:** `0.5.0-preqa.3` / build `50300`  
**Active product area:** PC Sheet PDF Export — Android runtime QA  
**Status:** GENERALIZED REPAIR IMPLEMENTED / BRANCH CI PASS / AWAITING INTEGRATION THEN OWNER STAGE-2 RERUN

## 1. What is already proven

### Stage 0 — build/update

Owner installed the QA APK as an update on the existing Android emulator without uninstalling local state.

`0.5.0-preqa.1` initially installed and reported correctly.

### Stage 1 — in-app DEV tools defect and repair

On `0.5.0-preqa.1`, tapping:

`Configuración de la aplicación -> Herramientas DEV -> Abrir QA / diagnóstico DEV`

immediately crashed the app.

Root cause was an explicit Intent that derived the activity class from the application ID:

`io.github.mrsimkin.dndcustomaid.HostedDevAuthActivity`

while the actual Android activity class lives at:

`io.github.mrsimkin.dndcustomaid.android.HostedDevAuthActivity`.

PR #98 repaired the target, added a regression guard, advanced the QA build to `0.5.0-preqa.2` / `50200`, merged as:

`289eafda731c4b1c61e76a947a86ab5d948c537c`.

Merged-main Scaffold run `36060380623` passed.

Owner runtime verification on `0.5.0-preqa.2`:

- in-app DEV/QA screen opens: PASS;
- remembered Gmail Player session check: PASS;
- the session was then accidentally closed by the owner because the QA screen had no ordinary back affordance; the same Gmail Player session was restored successfully;
- hosted QA sync after restoration: PASS / converged.

Observed hosted-sync summary:

- hosted campaigns: 1;
- eligible campaigns: 1;
- applied/reconciled campaigns: 1;
- campaign conflicts: 0;
- hosted PCs: 3;
- applied PCs: 0;
- unchanged PCs: 3;
- tombstoned PCs: 0;
- PC conflicts: 0;
- queued PC snapshots: 0;
- acknowledged mutations: 0;
- retryable mutations: 0;
- blocked mutations: 0;
- local hosted outbox: empty.

Therefore the DEV-tools launcher repair is runtime-proven and the hosted Gmail Player state remains cleanly converged.

## 2. Stage 2 — Aldren Fantasy Sheet Save failure

Owner opened:

`Aldren Vale -> Ajustes de personaje -> Hoja de personaje PDF`

and configured:

- visual family: Fantasy Sheet;
- state: Permanente;
- other options left at baseline/default values.

The PDF configuration screen and baseline selection passed.

On the actual Save/generation attempt, production stopped on the bounded-routing safety guard before a normal successful PDF save.

Owner-visible diagnostic begins:

`Fantasy Sheet production encountered content outside its bounded base/continuation routing:`

The newly exposed value is equipment/weapon descriptive content shaped approximately as:

`Peso 3 · Equipado · Mano derecha · 1d8 cortante · versátil 1d10 · Arma marcial ...`

This is distinct from the earlier PR #96 Aldren overflow observations, which involved compact combat/action values and duplicated resource recovery text.

Do not proceed to Aldren Share, Ilyra, Mara, Current Snapshot or the physical-device final gate until this generation defect is repaired and Aldren Save passes.

## 3. Engineering conclusion — do not patch the next string

The second runtime overflow after the first narrow repair is evidence of a **defect class**, not justification for another equipment-only truncation.

Risk to avoid:

`overflow in section A -> patch A -> overflow in section B -> patch B -> repeat`.

The repair must generalize the Fantasy Sheet bounded-text contract.

### Required generalized rule

No bounded base-sheet field may receive arbitrary-length text directly.

Every bounded field must be explicitly classified/routed as one of:

1. **Naturally bounded/short** — safe direct rendering under a proven geometry contract.
2. **Potentially long with a valid full-detail destination** — base page renders a safe measured/compact preview while the complete information is preserved in the appropriate detail/continuation surface.
3. **Potentially long without a valid full-detail destination** — must gain an explicit destination or continue to fail the safety guard. Silent clipping/data loss is prohibited.

The full semantic information must survive. A generalized repair is not permission to globally truncate content.

## 4. Repair scope

Before editing the currently failing equipment line in isolation:

1. inventory every Fantasy Sheet bounded base-field write;
2. identify where arbitrary-length values can enter;
3. map each to the three categories above;
4. centralize/reuse bounded-preview/full-detail routing where geometries and semantics allow it;
5. preserve the existing overflow guard as a final invariant;
6. verify complete detail survives outside compact previews;
7. run real fixture-based regression over at least:
   - Aldren Vale baseline Fantasy Sheet;
   - Mara de los Siete Umbrales stress content where semantically applicable;
8. ensure earlier PR #96 combat/resource fixes and approved page-count/pagination baselines do not regress.

The implementation should prefer semantic routing plus measured rendering over magic string-specific exceptions.

## 4.1 Repair implementation and branch validation

Repair branch:

`fix/fantasy-sheet-generalized-bounded-text-routing`

Validated implementation head:

`29138e7322feae111b6207acb45da896a207d749`

Validated Scaffold run:

`36065636679` — **SUCCESS** across backend, hosted-database and Kotlin jobs.

The implementation keeps Desktop as renderer authority and regenerates/synchronizes the Android renderer. The final overflow safety guard remains enabled.

The repair generalizes compact/bounded projections rather than special-casing Aldren's sword. It now explicitly bounds and routes potentially long content across the affected Fantasy surfaces, including:

- special/synchronized equipment rows;
- inventory names/state/location previews;
- resource names/recovery/source plus detail notes;
- class-option names/source plus detail body;
- trait/proficiency continuation headers;
- base languages and allies/treasure previews where needed;
- long spell names and spellcasting labels;
- custom-stat titles and custom-skill names.

Complete semantic values are preserved in continuation/detail surfaces when compact previews are used. Silent clipping is not the policy.

### Fixture evidence

Aldren real-fixture regression now requires the generated PDF to preserve, among other prior combat/resource details:

- `Peso 3`;
- `Mano derecha`;
- `1d8 cortante; versátil 1d10.`;
- `Arma marcial.`.

Mara's full stress fixture is also rendered through Fantasy Sheet and verifies long special-item, option and resource content without triggering unrouted overflow.

### CI learning retained

The first generalized candidate exposed two test/geometry issues before acceptance:

1. promoting short base languages to reference merely because they had a source expanded an approved test from 6 to 8 pages; this was reverted so only genuinely long/detail-bearing base-language content is promoted;
2. Mara PDF text extraction split long special-item semantics across physical continuation rows, so the regression was corrected to test semantic preservation rather than require punctuation to remain adjacent in PDF text extraction.

The final branch run restores approved pagination baselines and passes all 78 Desktop tests plus the repository's Android/renderer-sync/build guards.

## 5. Acceptance boundary

Repository/CI acceptance requires:

- regression reproduces the owner-observed equipment overflow before the repair or otherwise directly covers the same real fixture path;
- no unrouted bounded overflow for Aldren Fantasy Sheet;
- complete equipment/combat/resource details remain present somewhere appropriate in the produced PDF;
- approved visual/pagination tests remain green;
- generated Android renderer remains synchronized with Desktop authority;
- full Scaffold passes.

After this repair is merged and merged-main Scaffold passes, owner/manual continuation is:

1. update the existing emulator app without uninstalling;
2. verify `0.5.0-preqa.3` / build `50300`;
3. re-open Aldren Fantasy Sheet / Permanente;
4. repeat `Guardar PDF`;
5. require file picker + readable populated PDF with no bounded-routing diagnostic;
6. only then test Share and continue later QA stages.

## 6. QA ergonomics follow-up

The accidental session close is not part of this overflow repair unless a tiny safe change is naturally coupled. It is recorded as UX evidence: the debug QA activity lacks an obvious ordinary back affordance. Do not allow this note to expand the bounded rendering repair into unrelated redesign.

## 7. Resume instruction

Fresh sessions must enter through:

`RESUME.md -> docs/checkpoints/LATEST.md -> this checkpoint`.

Do not resume from PR #96, PR #98, the hosted-player setup checkpoint, or stale Wave 6 root prose as if they were the current task.
