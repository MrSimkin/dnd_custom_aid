# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_QA_PREFLIGHT_ZERO_CAMPAIGNS.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Desktop live-QA preflight:** **OTP AUTH PASS / LOCAL DATA PASS / ZERO HOSTED CAMPAIGNS**  
**Current gate:** read-only Neon membership/identity discovery; no moderation mutation yet  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_QA_PREFLIGHT_ZERO_CAMPAIGNS.md`;
3. this file;
4. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`;
5. `docs/PROJECT_STATE.md`;
6. `docs/BRANCH_STATUS.md`;
7. `docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md` for the broader owner-QA sequence;
8. relevant implementation/decision checkpoints as needed.

Newer current-package records control over older operational prose. Historical checkpoints remain evidence for the state they recorded.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
Wave 5 Desktop shell + local campaign                 COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core                COMPLETE / MERGED (#43) / POST-MERGE CI PASS
        |
        v
Desktop hosted auth + real Campaign Administration   REPO IMPLEMENTED / CI VERIFIED / PR #44 DRAFT
        |
        v
DEV Worker deployment                                VERIFIED
  /health -> 200
  unauth roster route -> 401 UNAUTHENTICATED
        |
        v
Windows Desktop live-QA preflight                    PARTIAL PASS
  launch/local data -> PASS
  real email OTP login -> PASS
  hosted bootstrap -> 0 hosted campaigns
        |
        v
Read-only Neon membership/identity discovery         NEXT
        |
        +--> establish expected identity/membership context
        |
        v
Real roster retrieval                                BLOCKED UNTIL DISCOVERY
        |
        v
Bounded moderation QA if suitable Player exists      PENDING
        |
        v
Final settings/sign-out/relaunch QA                  REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified deployment state

The owner deployed exact repository head `a6c0532878e8ef49ddfb894fa71076c1af73587a` to the existing Cloudflare Worker `dnd-custom-aid-api` using repository-pinned Wrangler `4.127.1` and the expected existing authenticated Cloudflare account.

Cloudflare deployment completed successfully and reported Worker Version ID `130d35e7-7903-47b2-8203-d74f9ec3db55`.

Secret-free post-deployment probes passed:

- `GET /health` -> `200` with normal service health JSON;
- unauthenticated `GET /v1/campaigns/<zero-uuid>/members` -> `401 UNAUTHENTICATED`.

The `401` proves the new Campaign Administration member-roster route is live through route recognition and authentication enforcement.

## Desktop live-QA result so far

The current Windows Desktop build launched normally and preserved the existing local campaign `QA Wave 5 - 2026-09-16` with UUID `30609c9d-89f7-42ef-85dd-a7a35df3c506`.

Real Descope email-OTP authentication succeeded. Desktop reported hosted session `AUTHENTICATED`.

Bootstrap returned:

`Campañas alojadas: 0 · aplicadas: 0 · conflictos: 0`

Campaign Administration therefore correctly kept the existing campaign local-only and reported that the authenticated account has no hosted membership for it. No roster or moderation action was available.

Historical Wave 4 real-DEV evidence recorded an ACTIVE DM membership under a different application-user UUID. Android and Desktop use the same Descope project ID and same Worker, so the next step is read-only hosted-data discovery before deciding whether the difference is expected test-data separation or an identity/integration defect.

## Current owner/provider gate

Perform a **read-only** Neon query only. Determine:

1. whether the previously verified DM campaign/membership still exists and is ACTIVE;
2. whether the current Desktop-authenticated application user has any membership rows;
3. whether the current Desktop account and historical DM membership are distinct application users;
4. the relevant hosted campaign name/revision/deletion state.

Do not `INSERT`, `UPDATE` or `DELETE` yet. Do not expose database URLs, Descope subjects, passwords or provider credentials.

Do not run `KICK`, `BAN` or `LIFT_BAN` until a real suitable Player membership is visible and the identity/membership context has been understood.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit, paste, log or expose secrets/tokens/credentials/OTP codes;
- do not reset/delete databases, local campaigns, PCs, outboxes or application state to make QA pass;
- preserve membership/role/ownership/current-control distinctions;
- preserve stable identity, stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- DM authority is not PC ownership;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure;
- do not run `npm audit fix --force` blindly; the known 3 high-severity dependency findings remain a later explicit hardening item.
