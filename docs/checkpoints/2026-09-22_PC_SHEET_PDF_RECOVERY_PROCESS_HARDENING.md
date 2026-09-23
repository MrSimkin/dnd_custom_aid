# Checkpoint - PDF recovery process hardening after lost owner message

**Date:** 2026-09-22  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 - DRAFT / DO NOT MERGE  
**Status:** PROCESS CONTRACT HARDENING ACTIVE

## Recovered owner requirements

A user message was lost/interrupted. Its recovered requirements are authoritative:

1. `Raza`, never `Especie`, was already a contract and must not be treated as a new request.
2. Already-solved defects are repeating; every iteration must be durably registered.
3. The agreed best Custom strategy is semantic layers; preserve it.
4. Audit X and Y positions before presenting/“printing” another proof because run-to-run renderer output is no longer trusted without measurement.
5. The application-designed family is not sufficiently similar to an official D&D sheet to be called Classic. The owner allows the design to remain, but not the name/claim.
6. Consolidate these protections into the repository because prior work/decisions were lost.
7. Read the results of every run; the run record must explain what was solved, what failed, and what changed.

## Durable implementation

- `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md` - visual/product rules.
- `docs/PC_SHEET_PDF_ITERATION_LEDGER.md` - append-only iteration evidence.
- `docs/PC_SHEET_PDF_GEOMETRY_GATES.md` - independent X/Y expectations.
- `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md` - mandatory run-reading and registration procedure.
- Legacy internal enum `CLASSIC_DND_STYLE` remains temporarily for code compatibility; owner-facing name is **Fantasy Sheet**.
- Historical checkpoints/files may keep “Classic” in filenames/titles as provenance; new owner-facing artifacts must not.

## Current technical state

Current head before this hardening sequence: `921989455f79f9ce55051b555faa34103c8297f5`.

Scaffold push #3255 / `35793266628` failed on one stale page-count assertion after physical-capacity routing correctly added a continuation page. Backend and hosted-database jobs passed.

Do not remove the continuation to satisfy the old assertion.

## Gate

No owner-facing proof is approval-ready until the next ledger entry records:

- green CI;
- ordered five-layer checks;
- terminology check;
- dynamic X/Y audit;
- Worker rendered-page inspection;
- exact issue-by-issue resolution.
