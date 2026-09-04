# Phase 4 Batch M4d — I21 real-sheet Application Settings audition

**Date:** 2026-09-04  
**Status:** GREEN / COMPLETE  
**Safety branch:** `tmp/phase4-m4d-settings-preview`  
**Authoritative M4c base:** `ed5231ed0fbfb90257e75710602592ad10009263`  
**Accepted product commit:** `ba4b92623224e1d704834ab34b949d177c81c543`  
**Accepted product tree:** `faa266b85f1b687f069989e6b53940d589fbd340`  
**Canonical `main`:** untouched  
**Historical L candidate:** untouched

## Purpose

Close M3 hole I21 by augmenting Application Settings with a miniature representative character-sheet sample instead of relying only on generic font audition rows and theme swatches.

The preview is presentation-only. It owns no character identity or persistence and does not introduce a new domain model, schema, repository, or backend dependency.

## Delivered

`AppSettingsDialog` now includes a reachable `SettingsSheetPreview(preferences)` beneath the existing text-size/font/theme controls.

The sample visibly exercises:

- the currently selected global font through ordinary `MaterialTheme.typography`;
- the currently selected global theme through ordinary `MaterialTheme.colorScheme`;
- the currently selected text scale through the dialog's existing global density/theme recomposition plus an explicit `Texto N%` label;
- compact identity/header information (`Alyra Voss`, `Maga 7 · Evocación`);
- representative stat/value cells (`CA`, `PG`, `CD`);
- representative spell/reference text;
- textual semantic badges (`5.5e`, `Preparado`) so state/rules semantics are not judged from color alone.

The sample values are fixed preview data and are never written to character storage.

## Scope boundary

M4d does not attempt to close D06/D07 globally. Its preview badge treatment is intentionally local and lightweight. M4e/D06 remains responsible for reusable rules-generation/source badges on the real class/subclass identity surface; M4f/D07 remains responsible for reusable state-badge grammar on approved state-rich real surfaces.

## Guarded integration gate

Temporary exact-match workflow:

- workflow `33900810621` — **SUCCESS**.

Verified before accepting the product commit:

- preview is reachable from Application Settings;
- representative identity text is present;
- representative `CA` stat cell is present;
- textual `Preparado` badge is present;
- selected text-scale value is represented;
- full shared/Kotlin desktop tests PASS;
- Android debug assemble PASS;
- Desktop build PASS;
- backend dependency install/type-check PASS.

The temporary patch script/workflow self-deleted in accepted product commit `ba4b92623224e1d704834ab34b949d177c81c543` and are not part of the accepted product tree.

## Clean standard gate

Ordinary clean-tree workflow:

- workflow `33901226737` — **SUCCESS**;
- shared/Kotlin desktop tests — PASS;
- Android debug assemble + APK upload — PASS;
- Desktop build — PASS;
- backend install/type-check — PASS;
- artifact `9947820017` (`dnd-custom-aid-debug-apk`);
- artifact ZIP digest `sha256:d2ada42e3c1b3fae8db74c4de51af621ecaacdf7d48779b90dfcbe0d2dd754f7`.

This clean standard gate is the controlling final technical evidence for M4d.

## Boundary

M4d closes I21 implementation completeness. Visual usefulness at the owner's chosen fonts/themes/text sizes remains M6 real-device QA.

Next slice after clean promotion: **M4e / D06 rules-generation and source/provenance semantic badges**.
