# Phase 4 Batch M4b — F15 Resource Favorite / Quick Access

**Date:** 2026-09-04  
**Status:** IMPLEMENTED — clean standard gate pending at checkpoint creation  
**Safety branch:** `tmp/phase4-m4b-resource-favorites`  
**Authoritative M4a base:** `78d601390590822211f65fecabc4e587098a9d5b`  
**Accepted product commit:** `efcc74c9d5f151ac5f39c571c4eb23b372ba56a4`  
**Accepted product tree:** `f4c1993e039dcad073931d3154d33370d28bdb1f`  
**Canonical `main`:** untouched  
**Historical L candidate:** untouched

## Purpose

Close M3 hole F15 for generic Resources: the shared Quick Access model and Supercompact projection already supported `CharacterQuickAccessKind.RESOURCE`, but Gestión did not expose a way to mark a durable Resource as Favorite / Quick Access.

This batch closes implementation completeness only. Real-device interaction/visual acceptance remains M6 QA.

## Shared Quick Access operations

Added `CharacterQuickAccessOperations.kt` with two bounded helpers:

- `setCharacterQuickAccessFavorite(...)`
  - adds/removes one `kind + targetId` reference;
  - add is idempotent;
  - preserves all other Quick Access kinds;
  - rewrites dense `sortOrder` deterministically;
- `pruneCharacterQuickAccessKind(...)`
  - removes stale targets only for the requested kind;
  - preserves unrelated Quick Access references;
  - rewrites dense `sortOrder`.

Commit:

- `8cb04fa0162aec29622f9f018ce8ee9ccc16a9e1`.

No schema or duplicate Favorite storage was introduced.

## Focused tests

Added `CharacterM4ResourceQuickAccessTest.kt`.

Coverage:

- Resource Favorite add/remove is idempotent;
- an unrelated Combat-entry Favorite survives Resource Favorite changes;
- Resource pruning removes only missing Resource targets;
- live Resource and unrelated Spell references survive pruning;
- resulting Quick Access order is dense.

Commit:

- `a4e3073eff77dcdaf30f6567386b8ca3131dd723`.

## Gestión integration

Accepted product commit:

- `efcc74c9d5f151ac5f39c571c4eb23b372ba56a4`.

`CharacterManagementTabV4` now:

- derives the Favorite Resource ID set from authoritative `closureState.quickAccess` entries of kind `RESOURCE`;
- shows `★` for a Favorite Resource and `☆` otherwise;
- toggles the existing Quick Access reference rather than storing a new Resource-side Favorite flag;
- allows Favorite configuration only while structural editing is enabled, therefore Table mode blocks Favorite configuration;
- leaves existing `−` / `+` Resource operations unchanged and operational in Table mode;
- on successful Resource deletion, removes the Resource recovery metadata and prunes stale Resource Quick Access targets against the surviving Resource IDs;
- preserves Quick Access entries of every other kind.

Because Gestión receives the persisted authoritative `sheet.resources`, the Favorite control only targets durable Resource IDs. No unsaved newly-created structural Resource can create an orphan Favorite reference.

Supercompact requires no product change: its existing Quick Access projection already resolves `RESOURCE` references against authoritative `sheet.resources` and uses the same live Resource authority.

## Guarded integration verification

Temporary exact-match integration workflow:

- workflow `33896436799` — **SUCCESS**.

The workflow verified before creating the accepted product commit:

- all intended source seams matched exactly once;
- Gestión derives Resource favorites from closure Quick Access;
- Favorite toggling calls the shared Quick Access helper;
- Resource deletion calls targeted stale-reference pruning;
- the visible `★` / `☆` Resource control is reachable;
- full shared/Kotlin desktop tests PASS;
- Android debug assemble PASS;
- Desktop build PASS;
- backend dependency install/type-check PASS.

The temporary integration script/workflow self-deleted in the accepted product commit and are not part of the accepted product tree.

## Boundary

M4b does not alter resource operational semantics, Rest semantics, Supercompact state ownership, database schema, or the historical frozen candidate.

At checkpoint creation, one ordinary `scaffold-check.yml` run on this clean helper-free product tree plus this documentation checkpoint remains required before marking M4b GREEN and promoting it to the authoritative pre-QA branch.
