# Phase 1 provenance audit — Mara 50800 runtime mismatch

**Date:** 2026-09-30 (Chile local time)  
**Phase:** 1 — binary/source provenance and baseline  
**Branch:** `audit/mara-phase1-provenance`  
**Base main:** `bc02d6bc3a0cf564af1c092e1cbc8e62ad0b55f4`  
**Status:** PARTIAL / REPOSITORY-SIDE EVIDENCE COMPLETE / OWNER-LOCAL APK FINGERPRINT REQUIRED  
**No renderer/product code changed in this phase.**

## Governing QA authority

This phase does not replace or summarize away the 2026-09-28 owner QA.

The complete repair acceptance authority remains:

- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`;
- `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`.

Every observation in that ledger remains OPEN unless later actual-candidate evidence proves it FIXED. In particular, this provenance audit must not be used to dismiss the owner-observed layout defects merely because the wrong/stale binary may have been installed.

## Owner runtime evidence that triggered Phase 1

Owner-facing filename/identity used:

`dnd-custom-aid-0.5.0-preqa.8-build-50800.apk`

Observed runtime output:

- Fantasy: **45 pages** — SHA-256 `a4a5864dcb4a1d39570609e671ea860d2de440077bc8d6b62e5be22668909bf7`;
- Custom v1: **generation FAIL** — `Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`;
- Custom v2 · Atributo: **28 pages** — SHA-256 `74ccaf54f29ef03f1d95724cc63728c05cd5f09ef3c22aaacd739dceb9ae6b96`;
- Custom v2 · Habilidad: **27 pages** — SHA-256 `bd0cc116031372526f0b160133598f8b69992d8936b1c3b383e35b300d2897a6`.

The repaired proof path instead claimed **29 / 18 / 16 / 15**.

## Repository evidence: at least two materially different 50800 APKs exist

### A. Integrated pre-adaptive-repair 50800

Source:

- commit: `15f86ec8285e69969054d40defaa2c16259b8dce`;
- branch: `main`;
- Scaffold run: #3929 / `36276643170`;
- debug APK artifact: `10917523331`;
- artifact ZIP digest reported by GitHub: `sha256:e1fac68e5f9edfa4cca1817249afbdbd199f75b312be7ad96ad890e60fe747f1`;
- extracted `androidApp-debug.apk` SHA-256: `ff367e9b7b44d1844bd3358dbf6f5979087536a5ae404a3fe36623a3550a93c6`;
- extracted APK size: `69,499,416` bytes;
- Gradle identity: `0.5.0-preqa.8 / 50800`.

The populated-proof artifact from the same run is `10917681135`. It predates the later four-family Mara adaptive stress-proof harness and does **not** contain the `mara-*-stress-baseline.pdf` four-family set.

### B. Final adaptive-repair 50800

Source:

- commit: `ce695c40782e1847ed70a2256885b5b07113feae`;
- branch: `repair/pc-sheet-adaptive-continuations-cross-family`;
- Scaffold run: #4088 / `36354213574`;
- debug APK artifact: `10943700272`;
- artifact ZIP digest reported by GitHub: `sha256:e50511549079a5595c66d941d1d0a4216c12b0c5f6568bc5712b3600b28909d0`;
- extracted `androidApp-debug.apk` SHA-256: `68bbe5a055920a0c9ffc481f1c73c38a7dbc87867e78bcb72fabe3ff4ce00042`;
- extracted APK size: `69,630,488` bytes;
- Gradle identity: **also** `0.5.0-preqa.8 / 50800`.

Therefore filename/version/build identity cannot establish provenance.

## Final repair proof evidence

Run #4088 populated-proof artifact:

- artifact ID: `10942589698`;
- artifact digest: `sha256:6592148eb9c5254c65b6ec0cb33ffe09bf6efb8f9b97af61f3fe09797c1d6bb7`;
- `mara-cross-family-failures.txt`: empty;
- `mara-cross-family-page-counts.txt`:
  - Fantasy: 29;
  - Custom v1: 18;
  - Custom v2 · Atributo: 16;
  - Custom v2 · Habilidad: 15.

Exact proof PDF SHA-256:

- Fantasy: `9da2d727bfd7d7a1959be7cb79d71a474eb8e0bb9bb3108c10582af02ee9977a`;
- Custom v1: `057263a509413decd09cda08b3c118299cfc08276d265a3f0af506aac23a154f`;
- Custom v2 · Atributo: `6d64f1860fcc29233f1eb8fb84ba51804a63fc4cef23863f773098aa12cbfe19`;
- Custom v2 · Habilidad: `23756f9096b55e9d4de9fc0fded5f44926476590bdc38b2e04df1834175bfe5a`.

These prove what the repository/CI path at `ce695c...` generated. They do **not** prove which APK the owner installed.

## Strong diagnostic clue: Custom-v1 failure

The exact runtime failure observed by the owner was also encountered by the adaptive repair itself during the first cross-family diagnostic Scaffold (#3942):

`Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`

The later repair added physical wrapping of long Custom-v1 skill names. In particular, commit:

`ab7356320450caa2dd2410490beb8eb0339c5d2f` — `fix: wrap Custom-v1 stat skills into readable rows`

changes the Android renderer so long skills are converted with `flatMap(::skillLines)` into multiple physical rows at the approved source-matched scale, keeping the total/proficiency marker only on the first physical row.

That change is part of the final repaired head `ce695c...` and is absent from `15f86ec...`.

Therefore the owner's exact `Lectura de presagios` failure is **strong evidence that the owner runtime did not exercise the final repaired Custom-v1 path**.

This is not yet conclusive proof of which APK was installed. A runtime-path divergence inside the final binary must remain logically possible until the installed APK fingerprint is known.

## Phase-1 conclusion so far

Repository-side provenance is now bounded:

1. materially different APKs shared `0.5.0-preqa.8 / 50800`;
2. two key APK binaries are fingerprinted exactly above;
3. the final repair branch has an internally coherent source → run → APK artifact → proof-artifact chain;
4. the owner runtime materially disagrees with that final proof chain;
5. the exact Custom-v1 failure strongly matches the pre-fix defect path and was specifically repaired before `ce695c...`;
6. **the missing fact is the SHA-256 of the exact APK actually installed/tested by the owner**.

No speculative renderer repair is authorized yet.

## Owner evidence needed to close Phase 1

Preferred evidence: SHA-256 of the exact local APK file that was installed for the failed 50800 re-QA.

If the original file still exists on Windows, run:

```powershell
Get-FileHash "FULL_PATH_TO_THE_EXACT_APK_YOU_INSTALLED" -Algorithm SHA256
```

Return only the SHA-256 value (or upload the exact APK file instead).

Known direct matches:

- `ff367e9b7b44d1844bd3358dbf6f5979087536a5ae404a3fe36623a3550a93c6` → pre-adaptive-repair `15f86ec...` / artifact `10917523331`;
- `68bbe5a055920a0c9ffc481f1c73c38a7dbc87867e78bcb72fabe3ff4ce00042` → final adaptive-repair `ce695c...` / artifact `10943700272`.

If neither matches, Phase 1 must identify the matching intermediate 50800 artifact/commit before implementation proceeds.

## Next gate after owner evidence

Once the installed APK fingerprint is known:

1. classify the owner runtime binary as pre-repair / final-repair / intermediate;
2. map it to exact source commit and workflow artifact where possible;
3. compare its Android renderer against `ce695c...`;
4. update M50800-01 with the resolved provenance finding;
5. only then start Phase 2/3 audit work.

The complete 2026-09-28 QA matrix remains binding regardless of which binary is identified.
