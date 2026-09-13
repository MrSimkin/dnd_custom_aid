# Phase 4A — preqa.12 transversal-geometry QA candidate

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** AUTOMATION-GREEN EXACT PHYSICAL-QA CANDIDATE / AFFECTED TRANSVERSAL GEOMETRY RECHECK PENDING

## Candidate identity

- versionName: `0.4.0-preqa.12`
- versionCode: `41200`
- exact candidate commit: `abfc7e4a1519a27117f194721a425d75cb5df68a`
- candidate commit message: `build: advance transversal geometry candidate to preqa.12`

This is the monotonic successor to physical-evidence candidate `preqa.11 / 41100`; build `41100` is not reused.

## Why this candidate exists

`preqa.11` physically passed focused checks 1–7 and 9 but failed check 8. The owner showed recurring excessive vertical internal whitespace/padding in equivalent Player controls and explicitly required a **full-app equivalent-control repair**, not a one-dialog patch. The structured-dice `+ / −` selector was also explicitly included in the audit.

All prior physical PASS remains preserved for its tested scope.

## Repair scope contained by this candidate

The full-app source inventory found **160 raw Material `OutlinedTextField` call sites across 29 current Player Kotlin files**. Those usages bypassed a shared compact internal-padding policy, explaining why localized compact-control repairs had not removed the defect everywhere.

Contained repair chain:

- `087f6b9cd72b8bc376d9553d71be7ad48eb3a9ff` — shared `CharacterCompactOutlinedTextFieldV4` and compact glyph-selector primitives. The editable single-line control owns a safe 48dp+ interaction envelope while its decoration manages compact internal label/value whitespace.
- `cc187f46efe73515953f84ad31b0162b208d86ea` — migrated all 160 raw Material Player fields across 29 files to the shared primitive and moved the structured-dice `+ / −` control to the shared compact glyph selector.
- `ac6794ea64a3cefedb872e9047d5d1ece00266b7` — durable geometry regression guard `scripts/check_player_control_geometry.py`.
- `869e20526755b854f2711e319be1d4aed2ba38f6` — persistent guard wired into normal Scaffold.
- `05c638f67dfbb8504575b175feac9c520763e744` — temporary one-shot repair workflow removed.

The repair does **not** globally resize cards, enlarge dialogs, or rewrite already-accepted custom compact controls.

## Automated proof

Focused guarded migration:

- repair run `34775917100` — **SUCCESS**;
- exact guard result: `compactFieldCount=160; rawMaterialFields=0; diceSign=shared-compact-glyph`;
- focused `:androidApp:assembleDebug` — **SUCCESS** before product migration push.

Cleaned pre-version aggregate proof:

- commit `05c638f67dfbb8504575b175feac9c520763e744`;
- Scaffold run `34776384008` — **SUCCESS**;
- backend typecheck, persistent geometry guard, shared/Kotlin tests, Android assemble, desktop build and APK upload all passed.

Exact versioned candidate proof:

- run `34776627282` — **SUCCESS**;
- exact head SHA `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- backend typecheck — PASS;
- persistent compact-control geometry guard — PASS;
- shared/Kotlin tests + Android assemble + desktop build — PASS;
- APK upload — PASS.

## Artifact evidence

- artifact ID: `10323602038`
- artifact name: `dnd-custom-aid-debug-apk`
- ZIP/archive size: `13,627,800` bytes
- GitHub Actions artifact digest: `sha256:0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`
- independently downloaded ZIP SHA-256: `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7` — exact match
- APK filename: `androidApp-debug.apk`
- APK size: `38,914,160` bytes
- independent APK SHA-256: `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`

## Physical evidence state

`preqa.12` has **no owner/device PASS or FAIL yet**. Do not infer physical acceptance from automation.

Preserve from earlier candidates:

- `preqa.10` R1–R3 physical PASS;
- `preqa.11` checks 1–7 and 9 physical PASS;
- `preqa.11` check 8 physical FAIL is the boundary repaired by this candidate.

## Focused owner recheck for preqa.12

Do **not** rerun broad QA from zero. First recheck only the affected transversal geometry boundary:

1. In portrait, open representative ordinary labelled/editable Player fields, including the prior `Editar ataque o acción` example. Confirm vertical internal whitespace is materially reduced and content is proportionate rather than floating inside oversized field interiors.
2. Repeat representative equivalent fields in landscape. Confirm the compact geometry remains coherent and does not create clipping or awkward alignment.
3. In structured damage, inspect the `+ / −` sign selector. Confirm it looks compact/aligned consistently with neighboring compact controls **and remains easy to tap**.
4. Edit representative text and numeric fields to ensure the shared primitive remains practically focusable/editable; include one multiline field if convenient so content growth is sanity-checked.
5. Perform a short Save/Cancel sanity check in an affected editor. This is not a rerun of all preqa.11 functional checks; it only guards against a presentation repair breaking basic interaction.

If this affected boundary passes physically, record that result, then resume the remaining broader phone QA and afterward P17 tablet QA. Phase 4A remains open until explicit owner acceptance/closure. No P18 exists; DM implementation remains blocked.
