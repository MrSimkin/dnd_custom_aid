# Repository Manifest

This file maps authoritative project-control files and implemented areas so a fresh human or AI can resume without branch archaeology.

## Current authority model

The owner has authorized the integrated-MVP implementation. The former `main` + Player-successor split has been semantically converged and validated on `integration/mvp-baseline-convergence`.

Convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

Validation:

GitHub Actions `34917259324` / #1694 — **SUCCESS**.

After promotion, **`main` is the single normal integrated-MVP trunk**. `implementation/phase4a-successor-cycle` remains historical/frozen Player evidence only.

`docs/BRANCH_STATUS.md` controls branch lifecycle. Branch existence alone does not establish authority.

## Root control files

### `README.md`
Project entry point and mandatory read sequence.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. Routine low-level engineering is delegated; do not use the owner as a rubber stamp.

### `MANIFEST.md`
This inventory.

## Core current truth

### `docs/PROJECT_STATE.md`
Authoritative current global state and next implementation package.

### `docs/checkpoints/LATEST.md`
Practical resume pointer.

### `docs/checkpoints/2026-09-14_INTEGRATED_MVP_BASELINE_CONVERGENCE.md`
Current implementation/topology checkpoint: owner authorization, semantic convergence, validation evidence and next package.

### `docs/BRANCH_STATUS.md`
Canonical lifecycle map: `main` integrated trunk after promotion, old Player successor historical evidence.

### `docs/ROADMAP.md`
Current integrated implementation waves. Baseline convergence is validated; Shared Integrated-MVP Spine is next.

### `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`
Technical handoff for the shared spine, hosted foundation, sync, auth, backup, object storage and PC Sheet PDF export.

## Controlling product/architecture decisions

### `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`
Integrated Player + Server + DM architecture, paper/local/server authority, sync, permissions, recovery/backup, Desktop fallback, combat authority and SRD direction.

### `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`
Closed Desktop Live/Prepare/Admin product definition.

### `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`
Exact MVP boundary, implementation waves, Git strategy and owner-vs-technical responsibility.

### `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`
Complete cross-surface PC Sheet PDF-export behavior. In ordinary conversation, call this “the PDF export decision” unless the exact repository identifier matters.

### D-0068 / D-0069 / D-0070
DM Workspace/Desk family and shared Player/DM rules-question capability.

### `docs/DECISIONS.md` + `docs/DECISIONS_RECENT.md`
Historical chronological decision log plus recent decision index.

## Other mandatory/reference docs

- `docs/CONVENTIONS.md` — recurring conventions;
- `docs/PRODUCT.md` — approved product scope;
- `docs/WORKFLOW.md` — AI-led development/review workflow;
- `docs/ARCHITECTURE.md` — integrated architecture;
- `docs/TESTING.md` — verification strategy/evidence;
- `docs/TEST_DEVICES.md` — owner-confirmed physical devices.

Historical checkpoints remain evidence; old `next` instructions may be superseded by current `LATEST.md`.

## Character-sheet template assets

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf` — Custom v1 visual source;
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf` — Custom v2 source with two alternative main-page organizations;
- `assets/character-sheets/templates/README.md` — template/rendering guidance;
- `assets/character-sheets/templates/REFERENCE.md` — durable terminology/visual-family companion.

The PDFs are presentation artifacts, not the canonical PC model.

## Current implemented areas

### `shared/`
Mature Kotlin Multiplatform Player/domain/persistence foundation using SQLDelight and kotlinx.serialization. The integrated baseline now contains the authoritative Player successor evolution, migrations through at least 14, backup/provenance logic and extensive tests.

### `androidApp/`
Mature Player Android runtime from the successor line is now in the integrated baseline. Future work must preserve it rather than gratuitously rewrite it.

### `desktopApp/`
Compose Multiplatform Desktop scaffold. The real Desktop workbench remains to be implemented.

### `backend/`
TypeScript Cloudflare Worker scaffold; currently essentially `/health` plus type-checking. Real auth/domain/sync/storage/backup routes remain to be implemented.

### `database/`
Hosted PostgreSQL area is scaffold-only; real migrations remain to be implemented.

### `scripts/`
Permanent Player guard scripts imported from the successor and exercised by Scaffold CI.

## Historical Player evidence

Old successor head at convergence:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Frozen candidate:

- `0.4.0-preqa.13 / 41300`;
- `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- run `34801612526` / #1630 — SUCCESS;
- artifact `10331503478`;
- targeted physical cross-device revalidation pending at that old boundary.

This is historical evidence, not the normal development branch after convergence.

## Current integrated-MVP boundary

Target:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Protected scope includes Player hosted integration, full DM live Android/Desktop capability, combat authority resume, Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

Generalized VTT/realtime/ACL/CRDT/marketplace/homebrew-AI/enterprise infrastructure remains deferred unless a concrete approved requirement proves it necessary.

## Delegated technical direction

Current preferred implementation choices include:

- Ktor Client shared networking;
- versioned HTTP/JSON API;
- mutation IDs + optimistic revisions;
- SQLDelight outbox + scoped push/pull sync;
- Neon serverless driver initially;
- explicit SQL migrations;
- versioned JSONB hosted PC snapshots + relational auth/index metadata;
- server-side Descope token validation + app-owned authorization;
- R2 Standard as current object-storage recommendation, pending activation;
- versioned app-owned JSON import/export;
- on-demand versioned backup archive with manifest/checksums;
- canonical PC/export snapshot + shared PDF semantic/render plan with platform renderers.

## CI and verification

Validated convergence run `34917259324` / #1694 passed:

- all Player guards;
- shared desktop tests;
- Android debug build;
- Desktop build;
- backend type-check;
- APK artifact upload.

Expand CI proportionately as hosted migrations/auth/revisions/sync/assets/backup/PDF/combat authority become real.

## Exact current continuation

After promotion to `main`, branch from current `main` and implement the **Shared Integrated-MVP Spine**: identity, campaigns/membership/role, PC owner/controller, stable IDs, revisions, tombstones, scope/provenance and basic audit/sync invariants.

No additional owner approval is required for routine engineering. Escalate only material product/scope/security/privacy/cost/lock-in/destructive behavior, external account/service actions or manual/physical QA gates.

## Authority rule

If documents conflict:

1. later specific Approved decisions control older general prose;
2. `AGENTS.md` controls working governance;
3. `docs/BRANCH_STATUS.md` controls branch lifecycle;
4. `docs/PROJECT_STATE.md` controls current global state;
5. `docs/checkpoints/LATEST.md` controls the practical resume point;
6. the convergence checkpoint controls the current topology/validation facts;
7. historical checkpoints remain evidence only;
8. surface material contradictions instead of guessing.
