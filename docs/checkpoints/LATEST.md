# Latest project checkpoint — global resume map

**Updated:** 2026-09-14 (Chile local time)  
**Current implementation branch:** `integration/mvp-baseline-convergence` until promotion to `main`  
**Convergence commit:** `5bed85cbb3e86ae63eac79149fadc5e56e61b256`  
**Convergence validation:** Actions run `34917259324` / #1694 — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**  
**Next canonical trunk after promotion:** `main`

## Read first

1. `docs/checkpoints/2026-09-14_INTEGRATED_MVP_BASELINE_CONVERGENCE.md` — current implementation/topology checkpoint;
2. `docs/PROJECT_STATE.md` — current global state and execution rules;
3. `docs/BRANCH_STATUS.md` — branch lifecycle after convergence;
4. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` — engineering handoff for the next packages;
5. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md` — MVP/implementation governance;
6. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md` — PC Sheet PDF-export contract;
7. D-0071/D-0072 — integrated architecture and Desktop/Manager product definition.

## Current repository state

The former split authority has been deliberately reconciled.

Source refs at convergence:

- `main`: `fc567dac969847717ba94517b6a9dc9c968460e5`;
- Player successor: `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`.

Semantic merge commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

It preserves:

- successor Player runtime, SQLDelight migrations, Player tests, guard scripts and historical Player evidence;
- current integrated product/architecture/governance from `main`;
- current character-sheet template/PDF-export documentation from `main`;
- historical Player checkpoints needed for traceability.

GitHub Actions run `34917259324` / #1694 completed **SUCCESS** with all Player guards, shared tests, Android build, Desktop build, backend type-check and APK artifact generation.

After this convergence branch is promoted, `main` is the normal integrated-MVP trunk. `implementation/phase4a-successor-cycle` remains historical/frozen evidence, not the normal development branch.

## Historical Player evidence remains bounded

Frozen candidate retained for historical QA evidence:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical cross-device revalidation was still pending on that historical line.

Do not reinterpret the new integrated convergence CI as retroactive physical acceptance of that old candidate.

## Product scope remains closed for implementation

The integrated MVP remains:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Protected scope includes Player hosted integration, project-specific sync/auth/permissions, full DM live Android/Desktop capability, combat authority resume/handoff, authoring Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

Generalized VTT/realtime/ACL/CRDT/marketplace/homebrew-AI/enterprise infrastructure remains outside MVP unless a concrete approved requirement proves it necessary.

## Exact next technical package

Proceed without another owner rubber-stamp to the **Shared Integrated-MVP Spine**:

- identity/account;
- campaign + membership + role;
- PC owner vs controller;
- stable IDs;
- revisions/stale-write prevention;
- tombstones/non-resurrection;
- Personal/Campaign/System scope semantics where applicable;
- independent-copy provenance;
- basic audit/sync metadata and invariant tests.

Then continue into the hosted foundation and Player↔Server end-to-end integration in dependency order.

Routine schema/API/class/migration/test/package decisions are delegated. Return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, required external account/service actions, or manual/physical QA gates.

## External services

No provider activation occurred during convergence. R2 remains a technical recommendation pending a later owner/service action when object storage is actually needed. Cloudflare/Neon/Descope secrets remain outside Git.
