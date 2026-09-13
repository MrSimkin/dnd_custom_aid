# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Last physical-evidence candidate:** `0.4.0-preqa.11 / 41100` — owner checks 1–7 + 9 PASS / check 8 FAIL  
**Current engineering identity:** `0.4.0-preqa.12 / 41200` — transversal repair incorporated; exact versioned automation/artifact evidence pending freeze  
**Acceptance boundary:** focused owner recheck of repaired full-app equivalent-control geometry, then resume remaining phone/tablet evidence  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md` — preserved physical evidence plus completed transversal repair chain and next owner boundary.
2. `docs/PROJECT_STATE.md` — live Player authority/current gate.
3. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_QA_CANDIDATE.md` — prior exact candidate evidence and physical partial result.
4. `scripts/check_player_control_geometry.py` — persistent guard preventing regression to raw Material field geometry or the old dice-sign button path.
5. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md` — earlier P16/P4 repair chain whose physical passes remain preserved.
6. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` and `2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md` — controlling compact/usable-height principles.

## Physical PASS preserved — do not restart

Earlier `preqa.10` R1–R3 remains PASS.

On `preqa.11`, the owner reported **checks 1–7 OK and 9 OK; check 8 FAIL**. Preserve the PASS scope for portrait `Cantidad`, phone-landscape combined footprint, rotation sanity, standard/custom dice editing, incomplete-draft stability, numeric visibility, and Save/Cancel + valid persistence.

The only reopened boundary from that owner session was the broader full-app equivalent-control vertical padding/spacing consistency illustrated by `Editar ataque o acción`, plus inspection of the structured-dice `+ / −` selector.

## Transversal repair completed

The 2026-09-13 audit found the systemic escape hatch: **160 raw Material `OutlinedTextField` usages across 29 current Player files** were not governed by a shared compact internal-padding policy.

Repair outcome:

- all 160 sites migrated to shared `CharacterCompactOutlinedTextFieldV4`;
- actual editable single-line controls retain a safe 48dp+ interaction envelope while visual/internal whitespace is compacted;
- current multiline/numeric/read-only/error/supporting-text semantics are preserved;
- structured-dice `+ / −` now uses the shared compact glyph selector with a safe hit envelope;
- cards/dialogs were not globally enlarged or indiscriminately resized;
- regression guard is now part of normal Scaffold.

Key product/guard commits: `087f6b9c…`, `cc187f46…`, `ac6794ea…`, `869e2052…`, cleanup `05c638f6…`.

## Automated proof already green

Focused repair run `34775917100`: **SUCCESS**.

Cleaned pre-version aggregate at `05c638f67dfbb8504575b175feac9c520763e744`:

- Scaffold `34776384008` — **SUCCESS**;
- backend typecheck — PASS;
- persistent geometry guard — PASS;
- shared/Kotlin tests, Android assemble, desktop build — PASS;
- APK upload — PASS.

## Exact next action

The material repair requires a new monotonic candidate, now identified as `0.4.0-preqa.12 / 41200`.

Next:

1. obtain aggregate Scaffold success for the exact versioned `preqa.12` commit;
2. freeze exact commit/run/artifact/digest evidence in a new `preqa.12` QA-candidate checkpoint;
3. synchronize this `LATEST.md`, `PROJECT_STATE.md`, and the owner-QA progress checkpoint with the frozen evidence;
4. ask the owner to recheck **only the affected transversal geometry boundary first**: representative ordinary fields in portrait + landscape, the prior attack/action editor example, and the structured-dice `+ / −` selector, with quick editability/Save-Cancel sanity;
5. preserve prior PASS; do not restart broad QA from zero;
6. after this affected boundary physically passes, resume broader phone QA and then P17 tablet QA.

No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.
