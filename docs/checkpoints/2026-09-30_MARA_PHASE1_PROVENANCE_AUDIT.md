# Phase 1 provenance audit — Mara 50800 runtime mismatch

**Date:** 2026-09-30 (Chile local time)  
**Phase:** 1 — binary/source provenance and baseline  
**Branch:** `audit/mara-phase1-provenance`  
**Base main:** `bc02d6bc3a0cf564af1c092e1cbc8e62ad0b55f4`  
**Status:** COMPLETE / OWNER APK IDENTIFIED EXACTLY / PHASE 1 CLOSED  
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

Therefore the owner's exact `Lectura de presagios` failure was already strong evidence that the owner runtime did not exercise the final repaired Custom-v1 path.

That inference is now confirmed by the exact owner-supplied APK fingerprint below.

## Owner-supplied APK — conclusive match

On 2026-09-30 the owner supplied the exact APK file previously delivered to and installed by the owner for the failed 50800 re-QA:

`dnd-custom-aid-0.5.0-preqa.8-build50800.apk`

Measured locally from the supplied bytes:

- size: **69,499,416 bytes**;
- SHA-256: `ff367e9b7b44d1844bd3358dbf6f5979087536a5ae404a3fe36623a3550a93c6`.

Byte-for-byte comparison (`cmp`) against recovered GitHub workflow artifacts:

- equals artifact `10917523331` extracted `androidApp-debug.apk`: **YES**;
- equals artifact `10943700272` extracted `androidApp-debug.apk`: **NO**.

Therefore the owner-tested 50800 APK is conclusively:

- source commit: `15f86ec8285e69969054d40defaa2c16259b8dce`;
- branch at build time: `main`;
- Scaffold: #3929 / `36276643170`;
- artifact: `10917523331`;
- APK SHA-256: `ff367e9b7b44d1844bd3358dbf6f5979087536a5ae404a3fe36623a3550a93c6`.

It is **not** the final adaptive-repair APK from `ce695c...` / artifact `10943700272`.

## Consequence for the repair branch

The 2026-09-28 owner QA is valid primary evidence of defects in the exact `15f86ec...` runtime baseline. It **did not test the later adaptive repair branch**.

Therefore:

- do not describe `ce695c...` as having failed owner runtime QA;
- describe it as **repository/CI/Worker-preflight green but not yet owner-runtime verified**;
- do not discard its 100+ repair commits merely because the owner observed defects in the older APK;
- do not assume those commits satisfy the owner QA either;
- every 2026-09-28 owner observation remains in the acceptance ledger and must be audited against the repair implementation and later against the exact next candidate.

The root process defect is now proven: materially different APKs shared the same owner-facing `0.5.0-preqa.8 / 50800` identity, and the older one was the binary delivered/tested by the owner.

## Phase-1 conclusion

Repository-side provenance is now bounded:

1. materially different APKs shared `0.5.0-preqa.8 / 50800`;
2. two key APK binaries are fingerprinted exactly above;
3. the final repair branch has an internally coherent source → run → APK artifact → proof-artifact chain;
4. the owner runtime materially disagrees with that final proof chain;
5. the exact Custom-v1 failure strongly matches the pre-fix defect path and was specifically repaired before `ce695c...`;
6. the owner-supplied APK matches the pre-adaptive `15f86ec...` artifact exactly and does not match the final adaptive-repair APK.

Phase 1 provenance is therefore closed. No new renderer implementation has been authorized or started.

## Phase-1 closure

M50800-01 provenance is resolved for the failed 50800 runtime.

No owner question remains for Phase 1.

The next phase is **not started automatically**. When Phase 2 begins, it must audit every 2026-09-28 observation against the existing adaptive-repair implementation using KEEP / MODIFY / REPLACE (or equivalent) classification before any new renderer code is written.

## Next gate

Phase 1 is closed. The complete 2026-09-28 QA matrix remains binding.

Do not begin new renderer implementation merely because provenance is resolved. Phase 2 must first audit the existing adaptive-repair work against every owner observation and the later owner clarifications, retaining useful work and identifying gaps before code changes.
