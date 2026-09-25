# Checkpoint — PC Sheet cross-family runtime correctness repair WIP

**Date:** 2026-09-24 (Chile local time)  
**Base main:** `3453b2dac88644fd26ccb8b1c6ce47c634d99cef`  
**Active branch:** `fix/pc-sheet-cross-family-runtime-correctness`  
**Draft PR:** #103 — `fix: repair cross-family PC sheet runtime correctness`  
**Last implementation/code HEAD before pause:** `4e7f2f7127f607d9d97c7de22fa1fcc01514c1b9`  
**Status:** OWNER-REQUESTED PAUSE / IMPLEMENTATION WIP / DO NOT MERGE / DO NOT RESUME MANUAL QA

## 1. Resume rule

This file is the durable continuation authority for the paused repair.

Fresh session order:

`AGENTS.md -> RESUME.md -> docs/checkpoints/LATEST.md -> this checkpoint`

Then inspect **PR #103** and the active branch before changing anything.

Do **not** restart the repair from `main`, do not recreate already-landed fixes, and do not resume owner Android QA until the branch is green, the hosted Aldren fixture is reseeded with UTF-8 integrity, and a new distinguishable APK exists.

## 2. Why this repair exists

Owner Android runtime QA on `0.5.0-preqa.3` successfully saved/opened Aldren across all four visual families, then the cross-family review established a defect class rather than one isolated string bug.

Canonical evidence remains:

`docs/checkpoints/2026-09-24_PC_SHEET_ALDREN_CROSS_FAMILY_PDF_REVIEW.md`

Observed problems included:

- mojibake / Unicode corruption such as `ComÃºn`, `Ã‰lfico`, `â€“`;
- combat/action/structured-damage detail appearing under Traits/reference surfaces;
- inventory/equipment replay and ordinary currency misrouting;
- unnecessary spell/Notes pages;
- excessive page amplification;
- unclear one-use resource semantics in Fantasy and Custom-v2;
- Custom-v2 Equipo Especial / Ubicación typography mismatch.

The repair must preserve the owner-approved/frozen family visual baselines except where concrete runtime defects require bounded corrections.

## 3. Important root-cause findings

### 3.1 Unicode

The canonical Aldren JSON fixture itself contains correct Unicode, including:

- `Común`;
- `Élfico`;
- `Acólito`;
- `acción`;
- `versátil`;
- en/em dash characters.

The Worker response path explicitly returns `application/json; charset=utf-8`, and the Android hosted client uses Ktor JSON directly with no manual byte reinterpretation.

The guarded DEV seed used `psql \\copy` but did **not** previously force the psql client encoding. That is a plausible source for the exact mojibake pattern if the seed was executed from a non-UTF-8 client environment.

Current branch repair:

- `database/qa/seed_pc_sheet_runtime_characters.sql` now starts with `\\encoding UTF8`;
- the seed has an Aldren Unicode sentinel that verifies representative accented text and dash characters before COMMIT;
- renderer-level mojibake replacement hacks were deliberately **not** added.

**Still required:** rerun the corrected guarded DEV QA seed through the established owner/DEV procedure. The current hosted/local Aldren snapshot must not be assumed repaired merely because the SQL script is fixed.

### 3.2 Blank spell/Notes pages

The planner historically emitted these base roles too broadly.

Current branch now uses content-aware base-page selection:

- no Spell List page for a non-spellcaster;
- no separate Notes base page when short notes can be packed into a compatible narrative surface;
- Custom-v2 can place short notes in its existing story/narrative area.

### 3.3 Currency routing

Canonical currency keys are:

`cp / sp / ep / gp / pp`

Both Custom families now use that canonical set when deciding whether currency belongs on the normal base surface versus custom/extended treasure handling.

### 3.4 Combat is no longer a Traits concern

A shared semantic `COMBAT_AND_ACTIONS` continuation route exists.

Fantasy, Custom-v1 and Custom-v2 now have dedicated combat/action continuation rendering instead of injecting combat/action/structured damage into Traits.

A later correction tightened this further: continuation is **not unconditional**. It is emitted only when the base combat surface cannot fully represent required semantics, including examples such as:

- entries beyond family base capacity;
- non-attack actions whose action type would otherwise be lost;
- clipped/long base detail;
- meaningful notes;
- structured damage that adds information not already represented.

This prevents replacing the old generalized spill problem with a new “always add a combat page” problem.

## 4. Landed repair scope on PR #103

Do not reimplement these from scratch.

### Shared/planner

- content-aware active base pages;
- explicit `COMBAT_AND_ACTIONS` content/extension route;
- runtime regression coverage expanded around Aldren cross-family semantics.

### Fantasy

- planner spell-page suppression honored;
- dedicated combat/action continuation separate from Traits;
- combat continuation bounded to genuine unrepresented detail;
- one-use resources use explicit binary status only when max = 1;
- ordinary non-binary resources remain numeric;
- inventory continuation policy was corrected after CI exposed data loss:
  - a base item can still continue when its compact row cannot fully represent weight/state/detail;
  - `CARRIED` / `Llevado` state is explicitly preserved;
  - long special-item detail such as Aldren's `versátil 1d10` remains inside inventory/equipment continuation rather than being discarded or pushed into a generic Traits bucket.

### Custom v1

- canonical currency filtering;
- combat/action/structured-damage removed from Traits supplements;
- dedicated combat continuation;
- continuation bounded to actual overflow/reference need;
- existing one-use circle semantics preserved;
- five semantic extension layers retained for new combat pages:
  `STRUCTURE / CLEANUP / LABELS / VALUES / MARKERS`.

### Custom v2

- canonical currency filtering includes `ep`;
- short notes can pack into the narrative surface;
- combat/action/structured-damage removed from Traits supplements;
- dedicated bounded combat continuation;
- combat pages use the complete five-layer semantic stack;
- one-use resources now present an explicit binary marker plus `Disponible` / `Usado` instead of an unexplained filled square;
- the pre-interruption branch already contained the Equipo Especial / Ubicación typography repair and base-equipment capacity work; keep it, but final acceptance still depends on green CI + owner rerun.

### Desktop / Android parity

The touched Desktop renderer authority changes have been mirrored into the generated Android renderer files during this repair.

A previous intermediate CI run confirmed the generated-renderer sync guard could pass after synchronization. The final paused head still requires complete exact-head CI confirmation.

## 5. CI history that matters

An intermediate repair head `d00079b726dac8c09486a89835d453462a517baa` reached the real Gradle test suite after:

- resume-route guard PASS;
- Player static guards PASS;
- Android renderer-sync guard PASS;
- Android PDF delivery guard PASS;
- backend PASS;
- hosted-database PASS.

Its Desktop test suite reported **79 tests / 7 failures**.

The failures were useful and were not all stale assertions:

1. Aldren Fantasy lost full special-equipment detail such as `versátil 1d10`.
2. Aldren cross-family Unicode/runtime semantic regression assertion failed for Fantasy.
3. Custom combat continuation pages lacked the full semantic layer stack.
4. Current Snapshot Fantasy lost inventory operational state such as `Almacenado`.
5. Several historical exact page-count assertions increased because combat continuation had initially been emitted too broadly.
6. A Classic “base-only” fixture was not actually base-only because it inherited dense combat detail.

Subsequent branch changes before the pause addressed those issues by:

- preserving clipped base-item detail/state in Inventory continuation;
- preserving carried/stored state;
- adding complete Custom combat semantic layers;
- bounding combat continuation by actual need;
- isolating the historical Classic base proof fixture to genuinely base-representable attacks;
- hardening the DEV seed to UTF-8.

## 6. Validation state at the moment of pause

At implementation/code HEAD:

`4e7f2f7127f607d9d97c7de22fa1fcc01514c1b9`

the latest exact-head Scaffold runs had started:

- push run `36085757016`;
- PR run `36085760604`.

At the moment the owner requested the pause, those runs were **in progress**. There is therefore **NO final PASS claim** for the paused implementation head.

Documentation-only consolidation commits made after that head may trigger additional CI runs. Treat those as lifecycle noise unless they expose a real docs/resume-route problem.

## 7. Exact next steps when project work resumes

Resume from PR #103 and perform these in order:

1. Verify current `main`, branch HEAD, PR #103 and latest exact-code CI state.
2. Read the most recent failed/successful Kotlin job rather than rerunning work blindly.
3. If CI still fails:
   - separate genuine data/semantic regressions from intentionally superseded historical assertions;
   - fix product behavior first;
   - update tests only when their old assumption is no longer part of the approved contract.
4. Reconfirm generated Android renderer parity.
5. Run/obtain a fully green Scaffold for the coherent repair.
6. Inspect generated Aldren PDFs programmatically and visually where possible:
   - exact Unicode survives;
   - no combat/action detail appears under Traits;
   - ordinary currencies stay out of inventory continuation;
   - non-spellcaster has no blank spell page;
   - one-use resource meaning is explicit;
   - special/ordinary inventory detail is not lost;
   - page growth is driven by real overflow, not unconditional continuation.
7. Through the established DEV QA procedure, rerun the corrected guarded UTF-8 seed so hosted Aldren is actually repaired. Do not expose or commit secrets.
8. Confirm the corrected hosted revision can be pulled to the emulator cleanly with no conflict/outbox problem.
9. Only after code + hosted QA data are coherent:
   - bump to the next QA version;
   - build a clearly named replacement APK;
   - keep PR #103 draft until all branch checks are green.
10. Resume owner QA with **Aldren across all four families first**. Do not jump ahead to Ilyra, Mara, Current Snapshot, Share/final physical-device gate until that repaired Aldren survey passes.

## 8. Explicit pause boundary

Owner requested a stop on 2026-09-24.

Until the owner explicitly resumes the project:

- do not make further functional/code changes;
- do not merge PR #103;
- do not bump version/build a replacement APK;
- do not reseed hosted DEV data;
- do not ask the owner for more PDF inspection;
- do not advance to later QA fixtures.

This checkpoint is intentionally sufficient for a new chat to continue without relying on conversation memory.
