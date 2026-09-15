# Checkpoint — integrated MVP baseline convergence

**Owner authorization:** granted explicitly on 2026-09-14 (Chile local time)  
**Convergence branch:** `integration/mvp-baseline-convergence`  
**Source `main`:** `fc567dac969847717ba94517b6a9dc9c968460e5`  
**Source Player successor:** `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`  
**Semantic convergence commit:** `5bed85cbb3e86ae63eac79149fadc5e56e61b256`  
**Validation:** GitHub Actions run `34917259324` / #1694 — **SUCCESS**

## Purpose

This checkpoint records the first implementation action after the owner authorized the integrated-MVP build. It closes the temporary two-authority repository topology by deliberately reconciling the current integrated product/governance line with the mature Player runtime line.

## What was reconciled

The convergence follows the already-approved semantic precedence rather than a blind textual merge:

- Player runtime, SQLDelight migrations, Player tests, permanent Player guard scripts and Player implementation evidence come from `implementation/phase4a-successor-cycle`;
- current integrated product, architecture, governance, technical handoff and PC Sheet PDF-export decisions come from `main`;
- the current main template/assets documentation remains authoritative;
- historical Player checkpoints from the successor are preserved under `docs/checkpoints/` so Player evidence is not lost;
- backend/database/Desktop scaffolds remain intact because there was no competing mature implementation to reconcile.

The convergence commit has both source lines as parents. The old Player successor therefore remains traceable historical evidence rather than being copied as an unrelated snapshot.

## CI/verification result

The convergence branch uses the richer successor Scaffold workflow. Run `34917259324` / #1694 completed successfully and verified:

- all permanent Player guard scripts;
- `:shared:desktopTest`;
- `:androidApp:assembleDebug`;
- `:desktopApp:build`;
- Android debug APK artifact generation/upload;
- backend dependency install and `npm run check`.

No automated failure remains at this convergence gate.

Automation does not retroactively fabricate the historical physical Player acceptance that was still pending for the frozen `preqa.13` candidate. That evidence remains preserved at its original scope.

## Repository topology after promotion

After this validated convergence branch is merged to `main`:

- `main` becomes the single normal integrated-MVP development trunk;
- `implementation/phase4a-successor-cycle` becomes historical/frozen Player evidence and is not the normal resume point;
- new implementation work should use short-lived outcome-oriented branches from current `main` rather than permanent Player/Server/Desktop silos.

## Next implementation package

The next technical package is the shared integrated-MVP semantic spine already defined by the implementation baseline:

- global identity/account;
- campaign + membership + campaign role;
- PC owner vs current controller;
- stable IDs;
- revisions/stale-write protection;
- tombstones/non-resurrection;
- Personal/Campaign/System scope semantics where applicable;
- provenance for independent copies;
- basic audit/sync metadata/invariants.

Exact schema/classes/API/migrations/tests remain delegated technical work. No additional owner approval is required unless implementation exposes a material product, security/privacy, cost, destructive-behavior or lock-in decision.

## External-service note

No provider activation or secret configuration was performed during convergence. Cloudflare R2 remains a technical recommendation pending a later owner/service action when implementation actually reaches object storage. Neon/Cloudflare/Descope secrets also remain outside Git.

## Current continuation

Promote the validated convergence branch to `main`, update branch/navigation truth, then proceed with the shared integrated-MVP spine and hosted foundation in dependency order.
