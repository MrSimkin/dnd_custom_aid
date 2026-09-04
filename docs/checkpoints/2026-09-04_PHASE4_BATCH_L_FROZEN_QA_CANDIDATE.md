# Phase 4 — Batch L frozen phone+tablet QA candidate

**Date:** 2026-09-04  
**Status:** GREEN / FROZEN  
**Frozen candidate branch:** `tmp/phase4-l-frozen-qa-candidate`  
**Canonical `main`:** untouched

## Freeze rule

This checkpoint freezes one exact APK candidate for Phase 4 owner phone+tablet QA.

The candidate branch must not receive silent code, test, build, or documentation changes after this identity is recorded. Any code change required by owner QA invalidates this candidate and must produce a new focused repair batch followed by a new candidate identity.

Repository continuity documentation may continue on `implementation/phase4-character-closure`; that does not change the frozen candidate branch or APK.

## Exact candidate source identity

Frozen branch:

`tmp/phase4-l-frozen-qa-candidate`

Exact commit:

`5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`

Exact tree:

`b0e25a194ba0ed1926422230f3c29f70bfcd4e24`

The candidate commit is the K-complete durable tree plus the Batch K checkpoint documentation. There is no candidate-only product change.

## Controlling candidate gate

Workflow:

`33887576972`

Result: **PASS**

Verified on exact candidate commit `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`:

- all shared/Kotlin tests — PASS;
- owner schema-5 -> current migration regression — PASS as part of the shared suite;
- SQLDelight generated/migration surfaces — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- backend dependency install — PASS;
- backend Worker type-check — PASS;
- Android debug APK upload — PASS.

## Exact artifact identity

GitHub Actions artifact:

- artifact ID: `9942595794`;
- artifact name: `dnd-custom-aid-debug-apk`;
- artifact size reported by GitHub: `12498598` bytes;
- GitHub artifact ZIP digest: `sha256:04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b`.

The artifact ZIP was downloaded independently and hashed locally.

Independent ZIP SHA-256:

`04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b`

The independent ZIP hash exactly matches GitHub's reported artifact digest.

The ZIP contains exactly one file:

- `androidApp-debug.apk`;
- extracted APK size: `36146572` bytes.

Independent extracted APK SHA-256:

`73282b433c519840e73ef9f8c8e63a311dcdf7bd9352c299469a0f7c290be079`

These commit/workflow/artifact/hash identifiers jointly define the Phase 4 QA candidate. A different APK is not this candidate even if its source code appears equivalent.

## Batch boundary

Batch L is complete. The candidate is frozen and eligible for **Batch M — owner real-device QA**.

Automated gates are technical evidence only. Phase 4 is **not closed** and must not be merged to `main` until owner QA is completed and accepted.

Batch M must exercise at minimum:

- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape;
- representative larger text;
- installation/upgrade over the prior owner-QA lineage without protected-data loss;
- key closure interactions including save/dirty behavior, Table mode, Supercompact/Quick Access, conditional modules, backup export and import-as-copy.

Any blocking QA finding creates a focused repair batch and a new frozen candidate. No DM implementation begins before successful owner QA and explicit owner approval of Phase 4 closure/merge.
