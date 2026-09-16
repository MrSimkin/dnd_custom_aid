# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_IMPLEMENTED.md`  
**Latest verified implementation head:** `58dc05933e35d47bc8f65f0249a2a0b74d6206c4`  
**Latest verified implementation Scaffold:** `35150382923` — **SUCCESS**  
**Checkpoint-documentation Scaffold:** `35150597857` — **SUCCESS**  
**Current gate:** explicit DEV Worker deployment/integration evidence, then owner Windows Desktop QA before merge  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_IMPLEMENTED.md`;
3. this file;
4. `docs/BRANCH_STATUS.md`;
5. `docs/PROJECT_STATE.md`;
6. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md` for package opening rationale;
7. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_AUTH_SLICE_VERIFIED.md` for the earlier auth-slice boundary;
8. relevant decisions and `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md` as needed.

Newer current-package records control over older operational prose. Historical checkpoints remain evidence for the state they recorded.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
Wave 5 Desktop shell + local campaign                 COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core                COMPLETE / MERGED (#43) / POST-MERGE CI PASS
        |
        v
Desktop hosted auth + real Campaign Administration   IMPLEMENTED / CI VERIFIED / PR #44 DRAFT
        +-- provider/session adapter                  IMPLEMENTED / CI VERIFIED
        +-- hosted campaign bootstrap/selection       IMPLEMENTED / CI VERIFIED
        +-- real member roster + moderation           IMPLEMENTED / CI VERIFIED
        +-- font/theme preview settings follow-up     IMPLEMENTED / CI VERIFIED
        +-- diagnostics/tests                         IMPLEMENTED / CI VERIFIED
        |
        v
DEV Worker deployment/integration                    NEXT / EXPLICIT EVIDENCE REQUIRED
        |
        v
Owner Windows Desktop QA                             REQUIRED BEFORE MERGE
```

## Latest verified package state

At exact implementation head `58dc05933e35d47bc8f65f0249a2a0b74d6206c4`, the bounded repository implementation is complete and Scaffold `35150382923` completed **SUCCESS** across backend, hosted database and Kotlin/Desktop/Android/Shared surfaces.

Desktop now has provider-specific Descope email-OTP/session acquisition outside Shared, canonical hosted campaign bootstrap, real hosted member roster consumption, server-authoritative Player moderation and non-secret hosted diagnostics. Session/refresh JWTs remain memory-only; OTPs/tokens are not persisted or exposed through ordinary diagnostics.

The owner-approved Desktop settings follow-up is also implemented: font/theme preview cards are reachable from the real Settings destination, Geist and Mona Sans Condensed are bundled from existing repository assets with license provenance, other Android catalogue names are offered only when the exact system family is available, and preference fallback/persistence is covered by focused tests.

## External verification still pending

Repository/CI completion is **not** evidence that the Campaign Administration routes are live on the real DEV Worker.

The repository still has no automatic Cloudflare Worker deployment workflow. Explicit DEV deployment/integration evidence is required before describing those routes as live or before treating real Desktop authentication/roster/moderation as externally verified.

Owner Windows Desktop QA also remains required after DEV integration. It must cover real email-OTP authentication, hosted campaign convergence, roster/moderation behavior, sign-out/relaunch semantics, preservation of local-only work/data and the font/theme preview/persistence follow-up.

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
