# Repository Manifest

This file maps the authoritative project-control files and implemented areas so a fresh human or AI can orient without reconstructing history from branch names or old checkpoints.

## Current authority model

Until the planned convergence is executed there are **two active authoritative lines**:

- `main` — canonical global integrated-MVP product/design/architecture/governance truth;
- `implementation/phase4a-successor-cycle` — authoritative current Player runtime, repair and QA line.

D-0073 defines the future convergence. After explicit implementation authorization, a dedicated convergence branch must preserve valid work from both lines, be validated, and only then merge to `main`. After successful convergence, `main` becomes the normal integrated-MVP trunk.

`docs/BRANCH_STATUS.md` controls lifecycle. Branch existence alone does not make a branch active.

## Root control files

### `README.md`
Project entry point and mandatory read sequence.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. It now incorporates D-0073's owner-vs-technical decision boundary: routine low-level engineering is delegated and should not be pushed to the owner for rubber-stamping.

### `MANIFEST.md`
This inventory.

## Core `docs/` truth

### `docs/PROJECT_STATE.md`
Current global state, Player authority pointer and implementation authorization boundary.

### `docs/checkpoints/LATEST.md`
Stable practical global resume pointer.

### `docs/checkpoints/2026-09-14_PC_SHEET_PDF_EXPORT_PRODUCT_CLOSURE.md`
Latest product/continuity checkpoint. Closes the PDF-export gap discovered after technical readiness and records the bounded technical alignment before coding authorization.

### `docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`
Technical-readiness checkpoint. Records branch divergence, existing scaffold/runtime reality, protected Player technical assets, delegated engineering recommendations, readiness risks and the exact next owner intervention.

### `docs/checkpoints/2026-09-14_DM_DESKTOP_MVP_SCOPE_AND_IMPLEMENTATION_GOVERNANCE_CONSOLIDATION.md`
Product/MVP/governance consolidation immediately preceding the technical-readiness pass.

### `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`
Controls the complete cross-surface PC Sheet PDF-export capability: visual families, v2 alternative first-page variants, custom-stat completeness/presentation, design-specific Extended pages, portraits, Permanent vs Current Snapshot, static/offline Save/Share behavior, overflow/readability and optional Spellbook.

### `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`
Controls exact integrated-MVP boundary, implementation waves, Git convergence direction and owner-vs-technical responsibility.

### `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`
Closed Desktop product/Manager definition.

### `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`
Controlling integrated architecture: paper/local/server authority, sync, permissions, backup/recovery, Desktop operational fallback, combat authority recovery, object storage and SRD direction.

### D-0068 / D-0069 / D-0070
Control the DM Workspace/Desk family and shared Player/DM rules-question capability.

### `docs/DECISIONS.md`
Historical chronological decision log through its earlier sequence.

### `docs/DECISIONS_RECENT.md`
Navigation bridge for recent detailed decisions. Detailed decision files remain authoritative.

### `docs/CONVENTIONS.md`
Approved recurring conventions. Routine new low-level implementation conventions may now be chosen/documented by technical agents under D-0073 unless they carry material owner-level consequences.

### `docs/PRODUCT.md`
Approved product scope. Later specific approved decisions such as D-0074 control older/general PDF prose where they conflict.

### `docs/ROADMAP.md`
Integrated-MVP roadmap and dependency-wave direction.

### `docs/WORKFLOW.md`
Current AI-led development/review workflow, including delegated technical decision responsibility.

### `docs/ARCHITECTURE.md`
Integrated Player/Server/DM architecture.

### `docs/TESTING.md`
Current Player evidence pointer plus continuous/integrated-MVP verification strategy.

### `docs/BRANCH_STATUS.md`
Canonical branch-lifecycle map and planned convergence transition.

### `docs/TEST_DEVICES.md`
Owner-confirmed physical test devices.

### `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`
Compact first-Worker technical handoff. Includes the D-0074 PDF-export implementation direction: one canonical PC/export snapshot, shared export semantics/render planning and platform renderers capable of template overlay plus generated Modified/Extended/Spellbook pages.

## Character-sheet template assets

### `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`
Authoritative visual source for **Custom v1**.

### `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`
Authoritative visual source for **Custom v2**, including the two alternative main-page organizations selected at export time.

### `assets/character-sheets/templates/README.md`
Operational guidance for template use/rendering under D-0074.

### `assets/character-sheets/templates/REFERENCE.md`
Durable terminology/page/visual-family companion to the binary PDFs, including v2 variant interpretation and D-0074 extension/overflow rules.

## Current Player authority/evidence

Before convergence, use:

`implementation/phase4a-successor-cycle`

Observed branch HEAD during technical readiness:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Current frozen candidate:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending.

The run was independently rechecked during technical readiness. Automation remains technical evidence, not owner/device acceptance.

Do not use older `preqa.9` summaries as current authority.

## Current integrated-MVP boundary

The approved next cycle targets:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

D-0072 closes detailed Desktop product design. D-0073 closes exact MVP/post-MVP scope and implementation governance. D-0074 closes the cross-surface PC Sheet PDF-export contract.

Required MVP areas include Player hosted integration, complete DM live Android/Desktop capability, combat authority resume, full Desktop Managers, structured homebrew/import/export, object storage/media, PC audit, **PC Sheet PDF export**, Campaign/System Administration, meaningful audit/recovery, full backup/export and official-SRD clarification.

Explicitly generalized/deferred directions include full VTT behavior, comprehensive automatic character/legality engine, simultaneous authoritative co-DM combat, generalized realtime/Durable Objects/queues by default, generic ACL/sync platforms, executable homebrew engine, homebrew-aware AI, public marketplace/community systems, every third-party import ecosystem, polished whole-server restore UI and enterprise observability.

## Technical-readiness findings

### `shared/`
Substantial existing Kotlin Multiplatform domain/persistence foundation using SQLDelight and kotlinx.serialization. The Player successor contains the authoritative latest Player evolution, including migrations through at least 14 and provenance/backup improvements.

### `androidApp/`
Mature Player runtime lives on the successor line. Future integrated work must preserve it rather than gratuitously rewrite it.

### `desktopApp/`
Compose Multiplatform Desktop scaffold only. Current placeholder implementation does not constrain the D-0072 Desktop product design.

### `backend/`
TypeScript Cloudflare Worker scaffold only; current service effectively provides `/health`. Real auth/authorization/sync/backup/storage routes remain to be built.

### `database/`
Hosted PostgreSQL area remains scaffold-only; real application migrations remain to be built.

### Current delegated technical direction

The technical-readiness/PDF-closure checkpoints record these preferred implementation choices:

- Ktor Client in shared Kotlin for Android/Desktop HTTP networking;
- small versioned HTTP/JSON API with stable mutation IDs and optimistic revision checks;
- SQLDelight outbox + project-specific scoped push/pull sync;
- Neon serverless driver from Cloudflare Worker initially, with no Hyperdrive unless measured need appears;
- explicit hosted SQL migrations rather than introducing an ORM solely for schema ownership;
- versioned JSONB PC snapshot plus relational authorization/index metadata rather than mirroring every local Player SQLDelight table into PostgreSQL;
- server-side Descope token validation and application-owned campaign/domain authorization;
- Cloudflare R2 Standard as the preferred first object-storage provider, pending owner/service activation;
- versioned application-owned JSON as canonical import/export family;
- on-demand versioned full backup archive with manifest/checksums rather than queues by default;
- one canonical PC/export snapshot and shared PDF-export semantic/render-plan layer across Player Android, DM Android/tablet and Desktop, with platform rendering for static templates plus generated Modified/Extended/Spellbook pages.

These are technical recommendations/delegated choices, not additional owner product decisions unless a later implementation fact creates a material cost/security/product tradeoff.

## CI and verification

Before convergence, the Player successor workflow contains permanent Player guard scripts in addition to the aggregate Kotlin/Android/Desktop build and backend type-check.

The integrated baseline must preserve those guards and then expand testing proportionately for hosted migrations, auth/authorization, revisions/idempotency/tombstones, sync, assets, backup, PDF export semantics/rendering and later combat authority.

## Current authorization boundary

Technical readiness plus D-0074 product/technical alignment are complete enough to begin implementation, but the owner has not yet given the explicit product-code go-ahead merely by requesting the reviews/documentation closures.

The next genuine owner intervention is:

> authorize beginning integrated-MVP implementation, starting with the protected `main` + Player successor convergence.

After authorization, routine engineering proceeds without owner rubber-stamping. Future owner actions should be limited to material product/scope/security/cost choices and required external account/service actions such as enabling R2 or securely configuring provider project secrets.

## Authority rule

If documents conflict:

1. later specific Approved decisions control older general prose;
2. D-0071/D-0072/D-0073/D-0074 control the current integrated-MVP direction;
3. `AGENTS.md` controls current working governance after its D-0073 reconciliation;
4. `docs/BRANCH_STATUS.md` controls branch lifecycle;
5. `docs/PROJECT_STATE.md` controls current global state;
6. `docs/checkpoints/LATEST.md` controls the practical global resume point;
7. before convergence, the Player successor's own `LATEST.md` controls exact Player source/evidence;
8. the technical-readiness and PDF-closure checkpoints control current engineering-readiness findings/recommendations;
9. historical checkpoints remain evidence but their old `next` instructions may be superseded;
10. surface material contradictions instead of guessing.
