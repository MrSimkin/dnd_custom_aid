# Checkpoint — Aldren Fantasy Sheet Android runtime overflow repair

**Date:** 2026-09-24 (Chile local time)  
**Integrated main entering this repair:** `799c0327dc81a30376a82182df21cb137e6ad1cf`  
**Integrated repair:** PR #96 merged into `main` as `eece864e5867f477b8bdd57596a1bee1638b4ceb`  
**Final repair head:** `931c179d2973d87f4a5fa6f4a4265e1c93fea65a`  
**Validation:** push Scaffold `36056511437` SUCCESS; PR Scaffold `36056515058` SUCCESS; post-merge Scaffold `36057383682` SUCCESS  
**Manual gate:** NOT CLOSED — Aldren Save/Share must be rerun with the repaired APK.

## Why this repair exists

The hosted Player-path preparation itself succeeded and converged, but the first real Android PDF runtime attempt exposed a bounded Fantasy Sheet rendering defect before a PDF file could be produced.

This is a runtime correction against the already-approved Fantasy visual grammar. It does not reopen the owner-approved visual design or authorize unrelated visual recalibration.

## Hosted Player-path evidence before the PDF failure

The Android Virtual Device authenticated successfully through the real DEV Worker as the deliberate Gmail QA Player.

After the corrected Mara fixture was reseeded/repaired, hosted sync returned:

- hosted campaigns: 1;
- eligible campaigns: 1;
- applied/reconciled campaigns: 1;
- campaign conflicts: 0;
- hosted PCs: 3;
- PC conflicts: 0;
- blocked/retryable mutations: 0;
- local hosted outbox: empty.

A second immediate sync proved convergence:

- applied PCs: 0;
- unchanged PCs: 3;
- queued PC snapshots: 0;
- acknowledged mutations: 0;
- PC conflicts: 0;
- outbox: empty.

Therefore the Aldren PDF failure was not an authentication, Worker, membership, hosted-PC visibility or convergence failure.

## Owner-observed Android runtime failure

Fixture:

- Aldren Vale;
- Fantasy Sheet;
- Permanent state;
- Android Virtual Device;
- normal Android PDF export surface.

Instead of reaching the document picker/share chooser, generation stopped with:

`Fantasy Sheet production encountered content outside its bounded base/continuation routing`

The renderer safety guard correctly refused to save a PDF containing unrouted/clipped content.

Manual result at that point:

- PDF generation: **FAIL**;
- Save: **BLOCKED before document creation**;
- Share: **BLOCKED before chooser/file creation**.

## Exact automated reproduction

PR #96 added a regression using the canonical fixture:

`qa/pc-sheet/fixtures/01_aldren_vale_srd5_1_champion_fighter.json`

The regression drives the real shared planner into the Desktop Fantasy production authority, which is mechanically ported to Android.

The first CI reproduction failed on exactly six overflow observations:

1. `1d8 + 6 cortante · 5 pies · Due...`;
2. `1d8 + 2 perforante · 80/320 pie...`;
3. `Recupera 1d10 + 5 PG · Personal...`;
4. `Una acción adicional este turno...`;
5. Aldren resource recovery `Descanso corto o largo · Descanso corto/largo · A máximo`;
6. the same recovery shape for the second Fighter resource.

No unrelated inventory/special-item overflow was reproduced.

## Repair semantics

Desktop remains the renderer authority. Android is regenerated from it; do not patch the Android layout independently.

The repair is deliberately narrow:

### Combat/action base preview

The Fantasy base combat table is a compact preview. Full action/attack detail is already routed to the Fantasy reference continuation when required.

The compact physical preview budget is reduced so the base cell itself fits. A regression additionally checks that canonical Aldren detail remains present in the produced PDF, including:

- Dueling / two attacks;
- ammunition/reload detail;
- Second Wind recovery amount;
- Action Surge additional-action detail.

This is not silent data loss: the base preview is bounded while the full reference detail remains in continuation routing.

### Resource recovery

When structured recovery metadata exists, its cadence/amount is authoritative for the Recovery column. Legacy free-text recovery remains visible as review detail in Notes rather than being concatenated into the narrow Recovery cell.

The recovery cell may wrap within its existing multi-line physical row, while note continuation remains bounded by the existing continuation machinery.

The regression checks that `Descanso corto/largo`, `A máximo`, and Aldren's legacy `Descanso corto o largo` review text all survive in output.

## QA ergonomics accepted during this repair

The owner observed that DEV/debug functions would be cleaner inside the app instead of as a second launcher.

PR #96 adopts that observation:

- `HostedDevAuthActivity` remains a debug-only activity;
- its standalone launcher intent/filter is removed;
- it is non-exported;
- normal debug app settings show:
  - `Herramientas DEV`;
  - `Abrir QA / diagnóstico DEV`;
- that settings entry is gated by `BuildConfig.DEBUG` and therefore does not appear in non-debug builds.

The diagnostic machinery itself is not rewritten.

## Android feature-line version

The owner also observed that PC Sheet PDF Export is now a material new user-facing feature beyond the earlier 0.4.0 pre-QA line.

PR #96 advances the Android package to:

- version name: `0.5.0-preqa.1`;
- version code: `50100`.

Rationale:

- this is a feature/minor transition rather than a patch-only change;
- the `preqa` suffix remains because the integrated MVP/runtime gate is not yet closed;
- this does **not** claim a final `0.5.0` release.

## Remaining manual boundary

PR #96 is integrated and its merged-main Scaffold is green. The remaining owner action is:

1. install/update the fresh `dnd-custom-aid-debug-apk` on the existing Android Virtual Device **without uninstalling**, preserving the current local/Descope state;
2. expect only the normal application launcher;
3. if QA diagnostics are needed, use:
   `Configuración de la aplicación -> Herramientas DEV -> Abrir QA / diagnóstico DEV`;
4. reopen Aldren Vale;
5. `Ajustes de personaje -> Hoja de personaje PDF`;
6. choose Fantasy Sheet + Permanent;
7. execute **Save** through the Android document picker and open the resulting PDF;
8. verify Aldren identity/content is readable and no bounded-routing diagnostic appears;
9. execute **Share** and verify the Android chooser plus readable PDF URI/file;
10. record PASS or exact defect.

Only after Aldren passes should runtime QA continue with:

- Ilyra: Spellbook + harmless unsaved edit + `Exportar sin guardar` non-persistence;
- Mara: Custom-v2 Attribute/Ability + Extended-page stress;
- one Current Snapshot request to verify the known Permanent fallback notice.

A Virtual Device is acceptable for iterative QA. The previously defined final physical-device/runtime gate remains separate where real-device behavior is still required.

Do not start Media / Handouts before the PC Sheet runtime gate is recorded.
