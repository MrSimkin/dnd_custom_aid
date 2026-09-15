# Latest project checkpoint — global resume map

**Updated:** 2026-09-14  
**Branch:** `main` after this documentation pass is merged  
**Role:** canonical global integrated-MVP navigation/product/architecture/governance  
**Player code authority:** `implementation/phase4a-successor-cycle` until planned convergence is executed  
**Observed Player branch HEAD:** `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`  
**Frozen Player candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Current decisions:** D-0071 + D-0072 + D-0073 + D-0074  
**Technical readiness:** COMPLETE, including post-review PDF-export alignment  
**Implementation authorization:** PENDING — no product-code/convergence work authorized by this documentation pass

## Read first

1. `docs/checkpoints/2026-09-14_PC_SHEET_PDF_EXPORT_PRODUCT_CLOSURE.md` — latest product closure and post-readiness alignment;
2. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md` — exact cross-surface PC Sheet PDF-export contract;
3. `docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md` — technical readiness, recommendations and risks;
4. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md` — exact MVP boundary, implementation governance and Git convergence;
5. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md` — closed Desktop product/Manager definition;
6. `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md` — controlling integrated architecture;
7. `docs/PROJECT_STATE.md` — global state and execution rules;
8. `docs/BRANCH_STATUS.md` — branch roles and planned transition;
9. D-0068 / D-0069 / D-0070 — live Workspace/Desk family and shared rules-question direction;
10. for exact Player code/QA before convergence, switch to `implementation/phase4a-successor-cycle` and read that branch's `docs/checkpoints/LATEST.md`.

## Current branch topology

Two authoritative lines remain until the planned convergence is deliberately executed:

- `main` = global product/design/architecture/governance truth;
- `implementation/phase4a-successor-cycle` = current Player runtime/QA authority.

Observed Player branch HEAD during technical readiness:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

D-0073 defines the intended transition: after explicit implementation authorization, create a dedicated convergence branch from current `main`, deliberately reconcile the authoritative Player successor runtime into it, validate the result, then merge the coherent baseline to `main`.

Semantic precedence:

- Player runtime/local migrations/tests/guards/evidence -> successor;
- later integrated product/architecture/governance -> current `main`;
- CI/navigation/shared governance -> deliberate reconciliation.

After successful convergence, `main` becomes the normal integrated-MVP trunk and the Player successor becomes historical evidence.

## Current Player evidence

Frozen candidate:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending;
- Phase 4A formally open on the historical Player line.

The GitHub Actions run was independently rechecked during technical readiness and is completed/successful against the exact candidate SHA.

Automation is not physical owner acceptance. Preserve the evidence; do not replay historical repair cycles without new evidence.

## Product/design state — CLOSED for implementation start

### 7D — DM Desktop product definition — CLOSED

Desktop is one workbench with:

```text
LIVE / WORKSPACE
PREPARE / MANAGE
ADMINISTRATION
```

Live uses the same DM game/domain semantics as Android/tablet for DM Screen, Stage Desk, Dungeon Desk and Combat Desk.

Prepare/Manage definitions are closed for Monster/Creature assistance, NPCs, Homebrew & Rules, Stages/Places/Scene Spine, Dungeons/Zones/Encounter Readiness, Encounters, PC Manager/Audit and Media/Handouts.

Administration includes Campaign Manager plus the sole-admin System Administration/operator console.

### 7E — exact integrated-MVP boundary — CLOSED

The next cycle remains the substantial real integrated MVP:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Do not silently trim approved Desktop live parity, combat handoff/resume, Managers, structured homebrew/import/export, object storage/media, PC audit, Campaign/System Admin, backup/export, official-SRD clarification **or PC Sheet PDF export**.

Generalized VTT/realtime/ACL/sync/event-sourcing/marketplace/homebrew-AI/etc. directions remain outside MVP unless concrete evidence shows one is the simplest safe way to satisfy an approved requirement.

### D-0074 — PC Sheet PDF export — CLOSED

The post-readiness product gap is now closed.

Protected behavior includes:

- Player Android + DM Android/tablet + DM Desktop export surfaces;
- Classic D&D-style, Custom v1, Custom v2-per-Attribute and Custom v2-per-Ability visual families;
- mandatory custom-Attribute/custom-Ability completeness;
- Extended Page, App Modified Sheet, and Modified Sheet + complete Extended Page custom-stat modes;
- design-specific Extended-page families;
- portrait Crop/Fit choice;
- Permanent vs Current Snapshot;
- local/offline static PDF generation, Save/Share only;
- preserved writable blank space, readability floor and visible overflow cues;
- optional complete Spellbook appendix.

The two existing custom PDFs under `assets/character-sheets/templates/` remain the authoritative v1/v2 visual references.

## Implementation planning state

### 8A — dependency-driven implementation strategy — CLOSED

Use integrated waves and frequent cross-client integration rather than isolated app silos.

### 8B — Git/repository convergence strategy — CLOSED

Converge `main` + Player successor through a dedicated validated branch, then use `main` as the integrated trunk with short-lived outcome-oriented branches.

### 8C — first shared-spine direction — CLOSED / delegated technical scope

The first technical foundation covers the already-approved common semantics: identity, campaign membership/roles, PC owner/controller, stable IDs, revisions, tombstones, scope and provenance.

Low-level schema/classes/endpoints/tests are delegated engineering decisions.

### Technical-readiness review — COMPLETE

The review confirmed:

- backend is a health-only scaffold;
- hosted PostgreSQL migrations are scaffold-only;
- Desktop is a placeholder shell;
- the mature technical asset to protect is the Player/shared Kotlin + SQLDelight implementation;
- branch divergence is technically reconcilable under D-0073;
- stale mandatory governance/navigation instructions were found and repaired by the readiness pass;
- existing versioned Player backup serialization is a strong basis for hosted PC snapshots;
- Ktor Client is the preferred shared native networking layer;
- Neon serverless driver is the preferred initial Worker->PostgreSQL path;
- project-specific outbox/revision/idempotency/tombstone sync remains the chosen direction;
- Cloudflare R2 Standard is the preferred first object-storage provider, pending owner/service activation;
- no unresolved low-level technical question currently requires owner choice.

The PDF-export closure does not invalidate these findings. Its additional technical alignment is bounded: use one canonical PC/export snapshot and shared export semantics across clients, with platform PDF rendering capable of faithful template overlay plus generated Modified/Extended/Spellbook pages. Exact renderer/library/layout mechanics remain delegated.

## Owner-vs-technical rule

The owner decides product behavior/workflow, visibility/privacy, MVP/later scope, user-facing destructive/safety behavior and meaningful cost/security/convenience tradeoffs.

Technical agents decide routine schema/API/class/migration/sync/serialization/test/package/rendering details and document them.

Do not ask the owner to rubber-stamp implementation minutiae. Escalate only when a technical choice materially changes product behavior, security/privacy, cost, irreversible lock-in or approved scope.

## Exact next action — OWNER INTERVENTION REQUIRED

The product definition and technical readiness are now coherent enough to begin implementation, including the newly closed PDF-export capability.

The next meaningful owner action is a single project-level decision:

> **Authorize beginning the integrated-MVP implementation, starting with the protected `main` + Player-successor convergence.**

Until that authorization is explicit:

- do not create/execute the product-code convergence;
- do not begin hosted/DM/PDF feature implementation;
- documentation/readiness corrections are allowed.

After authorization:

1. refresh both active refs;
2. execute and validate the dedicated convergence branch;
3. merge the coherent baseline to `main`;
4. proceed through delegated technical implementation packages without owner rubber-stamping;
5. place PC Sheet PDF export after the shared PC/domain state is coherent enough for one canonical export snapshot, then expose the same semantics on all approved surfaces;
6. return to the owner only for material product/scope/security/cost decisions, required external account/service actions, or manual/physical QA gates.

A likely early external owner action after implementation begins is enabling Cloudflare R2 if it is not already enabled, plus securely configuring required Cloudflare/Neon/Descope project secrets. Those are account/service actions, not engineering-design approvals.
