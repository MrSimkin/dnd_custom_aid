# Phase 4 — Batch J backup/import completion

**Date:** 2026-09-04  
**Status:** GREEN  
**Safety branch:** `tmp/phase4-j-backup-import`  
**Canonical `main`:** untouched

## Scope closed

Batch J completes the owner-approved own-format character backup/import and reconciliation requirement without introducing a second character model or any DM-owned state.

Delivered behavior:

- versioned app-owned character backup document and codec;
- richly populated character + closure-state round trip;
- controlled rejection of empty, malformed, wrong-format, unsupported-version and invalid payloads;
- decoder type-safety for non-primitive `format` / `version` headers;
- restore-as-copy semantics into a selected destination campaign;
- fresh character identity and remapped nested identifiers/references on every import;
- no silent overwrite of the source or an existing local character;
- repeated imports produce independent copies;
- atomic repository restore with transaction rollback on failure;
- reconciliation checkpoint integration for imported copies;
- Android Storage Access Framework import from the campaign character list;
- Android Storage Access Framework export from PC Settings;
- export is disabled while structural draft changes are pending, so the file represents the persisted character authority;
- successful import explicitly tells the user that a new local copy was created.

No SQLDelight schema migration was added for Batch J. Backup/import joins and reuses the existing authoritative character and closure persistence aggregates.

## Guarded implementation evidence

### J2 repository restore-as-copy

The stale generated test fixture was repaired only to match the current constructor fields. No production import semantics were changed for that repair.

Focused repository verification covered:

- restore as a new copy;
- repeated independent imports;
- source preservation;
- identifier/reference remapping;
- reconciliation checkpoint creation;
- transactional rollback.

The exact J2 product commit `86befdc15e6e1c08a73b46714c1b75c860f6fee1` subsequently passed the full Scaffold gate.

### J3 Android file flow

The guarded Android batch compiled and passed the broad Kotlin/shared/Android/Desktop/backend gate before product files were committed. Product commit:

`b40fe1d2c812ee48b44470468b43dfca6c42de5b`

The Storage Access Framework behavior itself remains part of later real-device owner QA; CI proves compilation/integration, not human interaction with external document providers.

### Header robustness

A final focused regression made valid JSON with non-primitive backup headers fail safely rather than reaching a primitive accessor exception.

Final Batch J product commit:

`7f93fc5268e9c0a9c26a2642fdbc2348b5f08501`

Final product tree:

`8cb3941f33f29ee6d2cf8c2d247944ebaaef8efd`

## Controlling exact-clean Batch J gate

Workflow:

`33879662226`

Result: **PASS**

Verified on exact product commit `7f93fc5268e9c0a9c26a2642fdbc2348b5f08501`:

- backend dependency install — PASS;
- backend Worker type-check — PASS;
- shared/Kotlin tests — PASS;
- SQLDelight surfaces exercised through the shared build — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- Android debug APK upload — PASS.

Artifact:

- name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9939406708`;
- artifact ZIP digest reported by GitHub: `sha256:94332b1325fa8cc64a4d21520c119d6cc9f3b3fee40666a8abb2704137cccf41`.

This artifact is Batch J technical evidence only. It is **not** the frozen Batch L owner-QA candidate.

## Batch boundary

Batch J is complete. The next approved work is **Integration Batch K — closure candidate stabilization**:

- full migration regression from the owner APK lineage;
- all shared tests;
- Android debug assemble;
- Desktop build;
- backend type-check;
- focused cross-tab integration fixes only if the stabilization gate exposes defects;
- no unrelated feature scope.

`main` remains untouched. DM feature implementation remains blocked until the complete Phase 4 exit gate, phone+tablet owner QA and explicit owner merge approval.
