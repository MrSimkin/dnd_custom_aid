# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Hosted/sync implementation checkpoint:** `734477b4e276810de1581dbc2d0a8458ad953f85`  
**Checkpoint validation:** Actions run `34979121449` — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `docs/checkpoints/2026-09-15_HOSTED_SYNC_FOUNDATION.md` — current implementation/provider-activation checkpoint;
2. `docs/PROJECT_STATE.md` — current global state and execution rules;
3. `docs/BRANCH_STATUS.md` — current branch lifecycle/resume rule;
4. `docs/ROADMAP.md` — current implementation wave status;
5. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` — engineering baseline;
6. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md` — MVP/implementation governance;
7. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md` — PC Sheet PDF-export contract;
8. D-0071/D-0072 — integrated architecture and Desktop/Manager product definition.

## Current repository state

The former Player/integrated split is already reconciled. `main` is the sole normal integrated-MVP development trunk. `implementation/phase4a-successor-cycle` and `integration/mvp-baseline-convergence` are historical evidence, not ordinary resume branches.

Wave 2 — Shared Integrated-MVP Spine — is complete and integrated.

Wave 3 — Hosted foundation — is **IN PROGRESS**. PRs #15–#21 have already established the shared spine, hosted API/auth/domain foundation, PostgreSQL contracts and validation, shared HTTP transport, durable SQLDelight outbox, local-first campaign-create delivery, and authenticated hosted account/campaign bootstrap.

The latest implemented package, PR #21, merged as:

`734477b4e276810de1581dbc2d0a8458ad953f85`

Post-merge Actions run `34979121449` completed **SUCCESS** across:

- all preserved Player guard scripts;
- shared/Kotlin tests;
- Android debug build;
- Desktop build;
- APK artifact upload;
- backend TypeScript checks;
- hosted PostgreSQL migration/contract checks.

## Exact next technical package

Continue Wave 3 with **explicit hosted campaign/membership lifecycle and scoped change semantics**.

Clients must be able to consume real membership/removal changes without guessing `KICKED`, `BANNED`, deletion or other lifecycle meaning from simple absence in a bootstrap/list response.

Keep this project-specific. Do not introduce generalized event sourcing, CRDTs, a generic sync platform, queues or realtime infrastructure without a concrete requirement.

After that, continue toward hosted PC current-state/snapshot sync and then the Wave 4 Player↔Server end-to-end path.

## External-service activation

**Do not create provider resources yet.** The current local/shared/API/database contracts can continue to be implemented and tested without speculative external setup.

The first activation gate is the first real authenticated end-to-end hosted development environment, after the remaining campaign/membership change semantics are stable and before remembered Player authentication/real hosted PC sync is wired into the owner-facing flow.

At that point activate, in a controlled development environment:

- Cloudflare Worker/API runtime;
- Neon PostgreSQL;
- Descope authentication.

R2 remains later, when Media/Handouts/assets actually need object storage.

Accounts/resources remain owner-controlled; start with development/test resources; keep secrets outside Git and place them in provider/runtime secret stores or ignored local development configuration. Present material plan/region/cost/security/privacy/lock-in choices to the owner immediately before activation.

## Historical Player evidence remains bounded

Frozen historical candidate remains:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical cross-device revalidation was still pending at that historical boundary.

Do not reinterpret current integrated CI as retroactive physical acceptance of that historical candidate.

## Product scope remains closed for implementation

The integrated MVP remains:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Protected scope remains controlled by the approved decisions and roadmap. Routine schema/API/class/migration/test/package decisions are delegated. Return to the owner for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, required external account/service actions, or manual/physical QA gates.
