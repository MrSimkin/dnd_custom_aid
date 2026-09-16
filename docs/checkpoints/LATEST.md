# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_AUTH_SLICE_VERIFIED.md`  
**Latest verified implementation head:** `9c6994cff2782e2c3c8f37e08d4f0e483ae13322`  
**Latest verified Scaffold:** `35146769605` — **SUCCESS**  
**Current gate:** continue package implementation; later explicit DEV deployment/integration and owner Windows Desktop QA before merge  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_AUTH_SLICE_VERIFIED.md`;
3. this file;
4. `docs/BRANCH_STATUS.md`;
5. `docs/PROJECT_STATE.md`;
6. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md` for package opening rationale;
7. relevant decisions and `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md` as needed.

Newer current-package records control over older operational prose. Historical checkpoints remain evidence for the state they recorded.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
Wave 5 Desktop shell + local campaign                 COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core                COMPLETE / MERGED (#43) / POST-MERGE CI PASS
        |
        v
Desktop hosted auth + real Campaign Administration   ACTIVE / PR #44 DRAFT
        +-- provider/session adapter                  IMPLEMENTED / CI VERIFIED
        +-- hosted campaign bootstrap/selection       NEXT
        +-- real member roster + moderation           PENDING
        +-- font/theme preview settings follow-up     PENDING
        +-- diagnostics/tests                         PARTIAL
        |
        v
DEV Worker deployment/integration -> owner QA         REQUIRED BEFORE MERGE
```

## Latest verified package slice

At exact head `9c6994cff2782e2c3c8f37e08d4f0e483ae13322`, Desktop now has a provider-specific Descope email-OTP/session adapter outside Shared. Shared receives only the provider-neutral `HostedAccessTokenProvider` seam.

Session/refresh JWTs remain memory-only, are not stored in ordinary Desktop preferences or diagnostics, and are refreshed before use when near expiry. Sign-out clears only hosted session state.

Scaffold `35146769605` completed **SUCCESS** across backend, hosted database and Kotlin/Desktop/Android/Shared surfaces.

The package remains incomplete: the workbench UI is not yet wired to this auth adapter, hosted bootstrap/roster/moderation are not yet exposed in Desktop, and the font/theme preview follow-up remains pending.

## Deployment boundary

The repository still has no automatic Cloudflare Worker deployment workflow. PR #43 proves the repository/API contract, but the Campaign Administration routes must not be described as live on the real DEV Worker until explicit deployment evidence exists.

This package owns that deployment/integration gate because it is the first real Desktop consumer of those routes.

If deployment requires owner-local/provider credentials unavailable safely to the Work environment, finish all safe repository work first and stop only at that narrow gate. Never request secrets in chat or Git.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit, paste, log or expose secrets/tokens/credentials/OTP codes;
- do not reset/delete databases, local campaigns, PCs, outboxes or application state to make QA pass;
- preserve membership/role/ownership/current-control distinctions;
- preserve stable identity, stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- DM authority is not PC ownership;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure.
