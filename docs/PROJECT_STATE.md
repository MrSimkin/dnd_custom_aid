# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Last physically tested candidate:** `0.4.0-preqa.11 / 41100` — owner checks 1–7 + 9 PASS / check 8 FAIL  
**Current engineering identity:** `0.4.0-preqa.12 / 41200` — transversal geometry repair incorporated; exact versioned candidate automation/evidence to be frozen next  
**Current phase:** automation-freeze `preqa.12`, then owner recheck of the affected transversal geometry boundary only  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Authority / authorization

This branch remains authoritative for the Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work remains inside the durable P1–P17 repair/validation authorization. No P18 exists.

## Preserved physical evidence

Do not restart valid physical evidence from scratch.

Earlier `preqa.10` R1–R3 PASS remains valid. On `preqa.11`, the owner physically reported **checks 1–7 and 9 PASS, check 8 FAIL**. The accepted tested scope includes:

- portrait Combat `Cantidad` geometry;
- phone-landscape shell combined footprint;
- portrait↔landscape rotation sanity;
- standard/custom dice editing;
- incomplete structured-damage edit stability;
- numeric clipping/visibility;
- Save/Cancel and valid component persistence.

Check 8 reopened only the broader full-app equivalent-control padding/spacing boundary.

## Transversal geometry repair — implemented

The full-app source audit found **160 raw Material `OutlinedTextField` sites across 29 Player Kotlin files**. These bypassed any shared compact internal-padding policy, while older localized repairs and P9 dialog sizing existed separately. This split policy explained the recurrence.

The repair now:

- routes all 160 raw Player fields through shared `CharacterCompactOutlinedTextFieldV4`;
- keeps the actual editable single-line field at a safe 48dp+ interaction envelope while reducing unnecessary internal label/value whitespace;
- preserves multiline growth, numeric keyboards, enabled/read-only/error/supporting/placeholder/prefix semantics used by current call sites;
- changes the structured-dice `+ / −` selector to the shared compact glyph grammar with a safe interaction envelope;
- leaves accepted custom compact controls intact;
- does not globally resize cards or enlarge dialogs.

Key commits:

- `087f6b9cd72b8bc376d9553d71be7ad48eb3a9ff` — shared compact field/glyph primitives;
- `cc187f46efe73515953f84ad31b0162b208d86ea` — 160-site full-app migration + dice sign migration;
- `ac6794ea64a3cefedb872e9047d5d1ece00266b7` — persistent geometry source guard;
- `869e20526755b854f2711e319be1d4aed2ba38f6` — guard added to Scaffold;
- `05c638f67dfbb8504575b175feac9c520763e744` — temporary repair workflow removed.

## Automated proof

Focused repair run `34775917100`: **SUCCESS**.

- migrated exactly 160 raw Material fields;
- focused geometry guard: PASS (`compactFieldCount=160; rawMaterialFields=0; diceSign=shared-compact-glyph`);
- focused Android assemble: PASS.

Cleaned authoritative branch at `05c638f67dfbb8504575b175feac9c520763e744`:

- Scaffold `34776384008`: **SUCCESS**;
- backend typecheck: PASS;
- persistent geometry guard: PASS;
- shared desktop tests / Android assemble / desktop build: PASS;
- APK upload: PASS.

This is the pre-version aggregate proof for the new candidate.

## Candidate state

The product repair is material, so the candidate advances monotonically:

- versionName: `0.4.0-preqa.12`
- versionCode: `41200`

Do not call `preqa.12` automation-green or physically accepted until its exact versioned Scaffold run succeeds and its artifact evidence is frozen. `preqa.11` remains the last physically tested candidate.

## Exact next action

1. run/observe normal Scaffold on the exact `preqa.12 / 41200` versioned commit;
2. if green, freeze exact candidate commit, run, artifact ID/name/size, GitHub digest, independently verified ZIP SHA-256 and APK SHA-256 in a new candidate checkpoint;
3. update this file, the owner-QA progress checkpoint and `docs/checkpoints/LATEST.md` with that frozen evidence;
4. ask the owner for a focused physical recheck only of the transversal field/control geometry boundary in representative portrait + landscape surfaces and the structured-dice `+ / −` selector;
5. preserve prior physical PASS unless contradictory evidence appears;
6. only after the affected boundary passes resume broader phone QA, then P17 tablet QA.

Phase 4A remains open. Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. DM implementation remains blocked until explicit owner acceptance/closure.
