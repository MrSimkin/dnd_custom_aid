# Repository Manifest

This file maps authoritative project-control files and implemented areas so a fresh human or AI can resume without branch archaeology.

## Current authority model

The owner has authorized integrated-MVP implementation. **`main` is the single normal integrated-MVP trunk.** The former Player successor and convergence line are historical evidence, not normal resume branches.

Current verified implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Post-merge GitHub Actions `34985799585` — **SUCCESS**.

`docs/BRANCH_STATUS.md` controls branch lifecycle. Branch existence alone does not establish authority.

## Start/resume control files

### `README.md`
Repository entry point and mandatory read sequence.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. Routine low-level engineering is delegated; do not use the owner as a rubber stamp.

### `MANIFEST.md`
This inventory.

### `docs/checkpoints/LATEST.md`
Exact practical resume pointer.

### `docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md`
Current hosted/sync implementation checkpoint. It records the integrated provider-neutral foundation, exact validated commit/run, and the first real external-provider activation boundary.

### `docs/PROJECT_STATE.md`
Authoritative current global implementation/product state and exact next dependency.

### `docs/BRANCH_STATUS.md`
Canonical branch lifecycle map.

### `docs/ROADMAP.md`
Overall integrated implementation waves and ordering.

### `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`
Current engineering handoff: shared/hosted foundation, sync, auth, PC snapshots, provider gate, object storage, backup and PDF direction.

### `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md`
Prepared safety/activation handoff for the first development Cloudflare + Neon + Descope environment. This file is not authorization to create accounts/resources.

## Controlling product/architecture decisions

### `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`
Integrated Player + Server + DM architecture, paper/local/server authority, sync, permissions, recovery/backup, Desktop fallback, combat authority and SRD direction.

### `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`
Desktop Live/Prepare/Admin product definition.

### `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`
MVP boundary, implementation waves, Git strategy and owner-vs-technical responsibility.

### `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`
Complete cross-surface PC Sheet PDF-export behavior.

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

## Current implemented areas

### `shared/`
Kotlin Multiplatform domain/persistence/networking/sync foundation using SQLDelight, kotlinx.serialization and Ktor. It contains the mature Player model/runtime persistence support, integrated spine, versioned backup/serialization, hosted transport, durable hosted outbox, campaign lifecycle sync and hosted PC snapshot synchronization/conflict logic.

### `androidApp/`
Mature Player Android runtime from the historical successor line is integrated and protected by permanent CI guards. At the current provider-boundary checkpoint, owner-facing Android composition remains local-only: real remembered Descope session acquisition and hosted campaign/PC wiring are the next external-environment-dependent package.

### `desktopApp/`
Compose Multiplatform Desktop foundation. The full approved DM Desktop workbench remains protected future integrated-MVP work after Player↔Server contracts are proven end-to-end.

### `backend/`
TypeScript Cloudflare Worker/API implementation, no longer scaffold-only. It includes `/v1` hosted domain routes, token-verifier boundaries, application-owned authorization, campaign/membership lifecycle behavior, PC snapshot behavior and hosted error contracts.

### `database/`
Explicit hosted PostgreSQL migration plus contract tests, including integrated spine, campaign membership lifecycle and PC snapshot authorization/revision/DM-authority contracts. CI validates these against PostgreSQL.

### `scripts/`
Permanent Player guard scripts exercised by Scaffold CI.

### `assets/character-sheets/templates/`
PC Sheet PDF source templates/visual authorities; presentation artifacts, not the canonical PC model.

## Current integrated implementation position

Wave 2 — Shared Integrated-MVP Spine — is complete.

Provider-neutral Wave 3 hosted work is integrated through PR #25:

- hosted API/database/shared transport;
- durable outbox and idempotent campaign delivery;
- hosted account/campaign bootstrap;
- explicit membership lifecycle/deletion reconciliation;
- hosted PC JSONB current-state snapshot foundation;
- PC authorization, revision/idempotency/conflict/tombstone semantics;
- durable PC snapshot delivery and safe same-identity reconciliation.

The next meaningful package is **real authenticated Player↔Server development integration**, which requires the first owner-controlled development Cloudflare + Neon + Descope environment.

Do not add speculative parallel infrastructure merely to defer this gate.

## External-provider boundary

The provider activation gate has now been reached, but provider activation remains an owner action.

Before activation, verify current plans, regions/data locations, pricing/quotas and security implications. Use development/test resources first. Never commit secrets.

R2 remains later for Media/Handouts/assets.

At checkpoint capture, GitHub repository metadata reports `private: false`; the owner should verify intended visibility before provider integration. Do not change visibility autonomously.

## Historical Player evidence

Old successor head at convergence:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Frozen candidate:

- `0.4.0-preqa.13 / 41300`;
- `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- run `34801612526` / #1630 — SUCCESS;
- artifact `10331503478`;
- targeted physical cross-device revalidation pending at that old boundary.

Current integrated CI does not retroactively establish physical acceptance of that historical candidate.

## Current integrated-MVP boundary

Target:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Protected scope includes Player hosted integration, full DM live Android/Desktop capability, combat authority resume, Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

Generalized VTT/realtime/ACL/CRDT/marketplace/homebrew-AI/enterprise infrastructure remains deferred unless a concrete approved requirement proves it necessary.

## Authority rule

If documents conflict:

1. later specific Approved decisions control older general prose;
2. `AGENTS.md` controls working governance;
3. `docs/BRANCH_STATUS.md` controls branch lifecycle;
4. `docs/PROJECT_STATE.md` controls current global state;
5. `docs/checkpoints/LATEST.md` controls the practical resume point;
6. the current checkpoint controls current implementation/validation facts;
7. historical checkpoints remain evidence only;
8. surface material contradictions instead of guessing.
