# Hosted provider activation gate — development environment handoff

**Status:** COMPLETE / VERIFIED  
**Prepared:** 2026-09-15  
**Completed:** 2026-09-15  
**Controlling owner policy:** `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Completion checkpoint:** `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`

This document originally defined the first external-service activation boundary for the integrated MVP. That boundary has now been crossed successfully for the DEV environment. It remains as the compact activation/operational record; the completion checkpoint contains the detailed evidence.

## 1. Hard budget gate — satisfied for current DEV activation

The project external-service operating budget remains **USD $0** unless the owner explicitly changes it.

The first development activation completed without authorizing paid plans, paid add-ons, billing commitments or overage-enabled resources.

Current DEV stack:

- Cloudflare Workers Free;
- Neon Free development project;
- Descope Free development project;
- Cloudflare Observability within the Free allowance.

This does not pre-authorize future paid services. Any new provider/resource must still be checked against D-0075 immediately before activation.

## 2. Activated development providers

The real DEV path is now:

```text
Android / Desktop
      |
      v
Cloudflare Worker/API <---- Descope identity proof
      |
      v
Neon PostgreSQL
```

Current resources:

### Neon

- project: `dnd-custom-aid-dev`;
- project ID: `holy-meadow-19010740`;
- region: `aws-sa-east-1`;
- database: `dnd-custom-aid-dev`;
- PostgreSQL 17.

### Descope

- project: `dnd-custom-aid-dev`;
- project ID: `P3JNKAUazZAxRXF4uM7nKzaAiy7Y`;
- DEV base URL: `https://api.descope.com`;
- public discovery: `https://api.descope.com/P3JNKAUazZAxRXF4uM7nKzaAiy7Y/.well-known/openid-configuration`;
- public JWKS: `https://api.descope.com/P3JNKAUazZAxRXF4uM7nKzaAiy7Y/.well-known/jwks.json`.

### Cloudflare

- Worker: `dnd-custom-aid-api`;
- DEV URL: `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`;
- health: `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/health`.

Object storage remains deliberately unactivated. R2 is still only a candidate for later Media/Handouts work and must receive a fresh `$0` review at that time.

## 3. Existing provider-neutral foundation remains authoritative

Repository code already provides:

- Worker `/v1` request/auth/domain handling;
- application-owned authorization;
- Descope/JWKS verification path;
- explicit PostgreSQL migrations/contracts;
- campaign/membership lifecycle contracts;
- hosted PC snapshot contracts and authorization;
- shared Ktor hosted client;
- `HostedAccessTokenProvider` seam;
- durable local hosted outbox;
- idempotency/retry classification;
- campaign reconciliation;
- PC snapshot reconciliation/conflict handling.

Do not duplicate or replace these systems merely because the providers are now real.

## 4. Current Worker configuration contract

The current deployed `backend/src/index.ts` contract is:

- `DATABASE_URL` — required, **secret**;
- `DESCOPE_PROJECT_ID` — required, non-secret identifier currently stored via Worker secret configuration for operational simplicity;
- `DESCOPE_BASE_URL` — optional; current DEV deployment omits it because code defaults to `https://api.descope.com`.

Older pre-activation text referencing `APP_ENV`, `AUTH_MODE` or `DESCOPE_JWKS_URL` as required deployed bindings is superseded by the actual current code.

Native Android/Desktop clients must never receive `DATABASE_URL` or other database credentials.

## 5. Secret handling

Never commit or place in durable public documentation:

- database passwords/connection credentials;
- access/refresh/session tokens;
- OTP values;
- private keys;
- provider API/admin/deployment tokens.

Use provider/runtime secret storage or ignored local configuration.

The repository is intentionally public under D-0075. Public visibility is expected and does not weaken secret-handling rules.

## 6. Activation verification — completed

Verified against the real DEV environment:

1. Neon migration `database/migrations/0001_integrated_mvp_spine.sql` applied successfully;
2. hosted PostgreSQL contract tests 0001–0004 passed against real Neon inside a transaction and were rolled back;
3. real Descope email OTP authentication succeeded;
4. public Worker `/health` returned HTTP 200;
5. protected `/v1/me` without auth returned HTTP 401 `UNAUTHENTICATED`;
6. `/v1/me` with a real Descope session JWT returned HTTP 200;
7. the authenticated identity resolved/persisted through real Neon;
8. repeated authenticated `/v1/me` invocations showed about 1 ms Worker CPU per visible request with no observed benchmark errors.

The representative Cloudflare Workers Free CPU/runtime gate therefore **PASSED** for the tested authenticated path.

Future materially heavier routes must still be profiled rather than inheriting this result automatically.

## 7. Local owner workflow

Known owner paths:

- project root: `D:\DnD_Aid`;
- clone: `D:\DnD_Aid\repo\dnd_custom_aid`;
- development credential file: `D:\DnD_Aid\dnd_custom_aid_dev_credentials.md`.

The credential file is intentionally outside Git and plaintext by explicit owner decision. Do not read/copy/commit its contents and do not force a vault/password-manager migration unless requested.

Known local tooling during activation:

- Node.js 22.22.2;
- npm 10.9.7;
- Neon CLI;
- Wrangler 4.127.1.

Known Windows caveat: PowerShell `Invoke-RestMethod` and Windows `curl.exe` failed TLS negotiation to the workers.dev endpoint through SChannel, while Node `fetch()` and Vivaldi worked. Treat Node/browser as the known-good local endpoint-test path unless Windows TLS is separately investigated.

## 8. Security residuals after activation

Activation success is not a completed security audit. Carry forward:

- JWT/fail-closed verification review;
- object-level authorization regression coverage;
- SQL/query safety;
- error/log leakage prevention;
- replay/idempotency authorization;
- request/API hardening;
- dependency-audit investigation;
- least-privilege Neon runtime-role evaluation;
- production Descope region/configuration review;
- continued tracked/generated-file secret hygiene.

Known concrete items:

- local backend install reported **3 high severity npm vulnerabilities**; no `npm audit fix --force` was run;
- the current Worker database credential is associated with the Neon project owner role; a dedicated least-privilege runtime role should be evaluated later.

Do not rotate live credentials or alter live database privileges casually; perform such changes only through a deliberate security-hardening package.

## 9. Next package after activation

The next primary package is now:

**real authenticated Player <-> Server development integration**

Proceed using the existing DEV environment and existing provider-neutral contracts:

```text
remembered Android Descope session/token
        |
        v
HostedAccessTokenProvider
        |
        v
hosted campaign bootstrap/create/select
        |
        v
PC snapshot push/pull
        |
        v
second-device + offline/reconnect + revoke validation
```

Do not repeat provider account creation. Do not activate R2. Do not silently move to a paid Worker plan if later heavier routes exceed Free limits; reassess architecture under D-0075 instead.

## 10. Owner guidance contract remains active

When a future external/provider/manual action is needed, explain:

```text
WHAT this component/action does
        |
WHY the project needs it
        |
WHAT the owner must click/type
        |
WHAT the owner should expect to see
        |
WHAT must remain secret / when to STOP
```

Routine engineering remains delegated and should not be pushed back to the owner for ceremonial approval.
