# Hosted DEV provider activation — completion checkpoint

**Captured:** 2026-09-15 (Chile local time)  
**Status:** COMPLETE / VERIFIED  
**Normal implementation trunk:** `main`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Last pre-activation consolidated main:** `a6bb965cf08a14878150a74d41191023dd70d552`  
**Pre-activation main validation:** Actions `34993181692` — **SUCCESS**  
**Owner integrated-MVP implementation authorization:** **GRANTED**

This checkpoint closes the first real hosted-development provider activation boundary. It records verified external-provider facts, end-to-end evidence, security residuals, owner/local workflow, and the exact project continuation after activation.

It supersedes older operational text that still says Cloudflare + Neon + Descope resources must be created before work can continue. Historical checkpoints remain evidence for what was true at their capture time.

## 1. Project-wide position

The project remains one integrated MVP:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Completed foundations remain valid and must not be restarted without a concrete defect or newer approved requirement:

- integrated baseline convergence;
- Wave 2 Shared Integrated-MVP Spine;
- provider-neutral hosted API/database/native transport foundation;
- durable hosted outbox and local-first campaign delivery;
- campaign membership lifecycle reconciliation;
- hosted PC current-state snapshot persistence/authorization;
- optimistic revisions, mutation idempotency, stale-write handling and tombstone/non-resurrection rules;
- safe same-identity hosted reconciliation distinct from user-facing backup restore-as-copy.

The hosted DEV stack is now real and operational. Provider activation itself is no longer the next dependency.

## 2. Verified architecture

The real DEV path is:

```text
Android / Desktop clients
        |
        | authenticated API
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Security/authority rule remains unchanged:

- Descope proves identity;
- the application/backend owns campaign/domain authorization;
- native clients never receive Neon/PostgreSQL credentials.

## 3. Neon DEV state

Current development resource:

- organization: `MrSimkin_Org`;
- organization ID: `org-curly-snow-14655863`;
- project: `dnd-custom-aid-dev`;
- project ID: `holy-meadow-19010740`;
- region: `aws-sa-east-1` (São Paulo);
- branch: `production`;
- branch ID: `br-solitary-resonance-acgbex27`;
- database: `dnd-custom-aid-dev`;
- owner role: `dnd-custom-aid-dev_owner`;
- PostgreSQL major version: 17;
- observed server version during activation: 17.11.

Important naming correction: the actual Neon-created database name contains hyphens: `dnd-custom-aid-dev`. Older hypothetical underscore spelling is not the real hosted database name.

### 3.1 Migration proof

The real migration:

`database/migrations/0001_integrated_mvp_spine.sql`

was applied successfully to the Neon DEV database.

### 3.2 Real contract-test proof

The real hosted database contract-test family was executed successfully against Neon:

- `0001_integrated_mvp_spine_contract.sql`;
- `0002_campaign_membership_lifecycle_contract.sql`;
- `0003_pc_snapshot_contract.sql`;
- `0004_pc_dm_authority_contract.sql`.

The Neon CLI environment did not have native `psql` on PATH and fell back to its embedded TypeScript psql client. That client could execute `\i` but did not accept the test files' leading `\set ON_ERROR_STOP on` through include. Temporary local copies removed only that psql meta-command; repository test files were not modified.

The four contract tests were executed inside one transaction and finished successfully, then `ROLLBACK` was issued. The application tables were confirmed empty afterward.

### 3.3 Real persistence proof

A later real authenticated `/v1/me` request created/resolved exactly one expected `app_user` record in Neon. This demonstrates real Worker -> Neon application persistence rather than only migration/test connectivity.

Do not record the real user's application UUID or Descope subject in durable project documentation; they are not needed for continuity.

## 4. Descope DEV state

Current development resource:

- project: `dnd-custom-aid-dev`;
- project ID: `P3JNKAUazZAxRXF4uM7nKzaAiy7Y`;
- DEV base URL: `https://api.descope.com`;
- current DEV region implication: default US Descope service region;
- OIDC discovery: `https://api.descope.com/P3JNKAUazZAxRXF4uM7nKzaAiy7Y/.well-known/openid-configuration`;
- JWKS: `https://api.descope.com/P3JNKAUazZAxRXF4uM7nKzaAiy7Y/.well-known/jwks.json`.

The owner explicitly accepted the default Free-region placement for DEV/test users. Reassess production-region suitability before production release.

Current observed Descope console navigation for OTP configuration:

`Build -> Authentication Methods -> One-time Password`

Email OTP via API/SDK was enabled for the DEV project.

A real email OTP login succeeded. The returned session JWT was kept transiently in the local probe process, was not printed/persisted, and successfully authenticated against the deployed Worker.

## 5. Cloudflare DEV state

Current development resource:

- account ID: `4d1c90ecd50545d937e80c4e8f320682`;
- Worker name: `dnd-custom-aid-api`;
- DEV URL: `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`;
- public health endpoint: `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/health`.

The Worker uses the repository's existing `backend/` implementation and `backend/wrangler.jsonc`.

Current code requires:

- `DATABASE_URL` — secret;
- `DESCOPE_PROJECT_ID` — non-secret identifier currently stored through Worker secret configuration for operational simplicity;
- optional `DESCOPE_BASE_URL` — omitted in current US DEV configuration because code defaults to `https://api.descope.com`.

Older handoff text mentioning `APP_ENV`, `AUTH_MODE` or `DESCOPE_JWKS_URL` as required deployed bindings does not describe the current `backend/src/index.ts` contract and is superseded for this DEV deployment.

### 5.1 Real hosted endpoint proof

Verified against the deployed Worker:

- `/health` -> HTTP 200;
- `/v1/me` without bearer authentication -> HTTP 401 with `UNAUTHENTICATED`;
- `/v1/me` with a real valid Descope session JWT -> HTTP 200;
- the valid request resolved/persisted the corresponding application identity in Neon.

### 5.2 Workers Free CPU/runtime proof

The representative authenticated path was exercised repeatedly against the real deployed environment.

Cloudflare Observability showed real individual `GET /v1/me` invocations with approximately:

- CPU time: about **1 ms** per visible invocation;
- wall time: varying and sometimes materially higher because network/database waiting is included there;
- errors: 0 for the observed benchmark batch.

The tested representative authenticated JWT verification + Neon access path is therefore comfortably below the applicable Workers Free CPU ceiling and the early runtime gate is **PASS**.

This proof applies to the tested path. Materially heavier future endpoints should still be profiled rather than assuming all future work has identical CPU cost.

Cloudflare Observability is enabled under the Free plan. The UI displayed the Free allowance of 200K events/day during activation.

## 6. End-to-end proof now achieved

The project has demonstrated the real hosted chain:

```text
email OTP
   -> Descope authentication
   -> session JWT
   -> Cloudflare Worker
   -> JWKS/signature/claim validation
   -> application identity resolution
   -> Neon PostgreSQL
   -> persistent app_user
```

This is no longer a provider-neutral simulation or local-only authentication proof.

## 7. Hard $0 policy result

D-0075 remains controlling.

The first DEV provider activation completed without authorizing paid plans or paid infrastructure:

- Neon DEV: Free-plan development use;
- Descope DEV: Free plan;
- Cloudflare Worker: Workers Free;
- Cloudflare Observability: Free allowance;
- no object storage activation;
- no Workers AI activation as part of this gate.

If a future provider/resource requires payment, automatic billable overage, a payment method with material charge risk or a paid upgrade, stop and return to the owner rather than silently changing the budget policy.

## 8. Owner/local development workflow

Owner local project root:

`D:\DnD_Aid`

Local Git clone:

`D:\DnD_Aid\repo\dnd_custom_aid`

Owner development credential file:

`D:\DnD_Aid\dnd_custom_aid_dev_credentials.md`

That file is intentionally outside the repository and is plaintext by explicit owner decision. Do not read, copy, ingest, commit or reproduce its contents. Do not replace this simple workflow with a vault/password-manager migration unless the owner asks for it.

Relevant local tooling observed during activation:

- Node.js 22.22.2;
- npm 10.9.7;
- Neon CLI;
- Wrangler 4.127.1.

Do not upgrade these merely because a newer version is advertised; upgrade when justified and tested.

### 8.1 Local TLS caveat

On the owner's Windows machine, PowerShell `Invoke-RestMethod` and Windows `curl.exe` failed TLS negotiation against the `workers.dev` endpoint through Windows SChannel.

Node `fetch()` and Vivaldi accessed the same endpoint successfully, proving this was a local Windows/SChannel path issue rather than a Worker/certificate defect.

Use Node/browser testing as the known-good local path unless the Windows TLS issue is separately investigated.

## 9. Secret handling state

Verified operational rules:

- `DATABASE_URL` is stored server-side in Cloudflare Worker secret storage;
- no native client receives the Neon credential;
- Wrangler OAuth credentials remain in Wrangler's local credential storage;
- OTP values and session JWTs are not durable repository data;
- no real credential value belongs in Git, issues, PR bodies or documentation;
- public identifiers/project IDs/public discovery/JWKS URLs may be documented.

The GitHub repository is intentionally public under D-0075.

## 10. Security residuals — not blockers to this completed activation, but not closed

Provider activation success is not equivalent to a completed security audit. Preserve these follow-up topics:

1. review JWT verification robustness/fail-closed behavior, including claim expectations and key rotation behavior;
2. continue testing server-side object-level authorization across campaign/member/DM/PC boundaries;
3. verify SQL parameterization/injection safety as endpoints expand;
4. verify error/log handling never leaks credentials/tokens/provider internals;
5. review replay/idempotency authorization behavior;
6. add proportional HTTP/request hardening as real client traffic expands;
7. investigate the backend dependency audit findings rather than blind-fixing them;
8. determine whether the runtime Worker should use a dedicated least-privilege Neon role instead of the current project-owner credential;
9. reassess DEV-vs-PROD Descope settings before production release;
10. periodically re-check secret hygiene in tracked/generated files and Git history.

Specific known dependency item: local `npm install --no-package-lock` reported **3 high severity vulnerabilities**. No `npm audit fix --force` was run. Inspect exact packages/reachability/fixed versions before remediation.

Specific DB hardening item: the current Worker connection string is associated with the Neon project owner role. A dedicated least-privilege runtime role is a sensible future security review target, but no live role/credential rotation is performed by this checkpoint.

## 11. Local/generated artifact caution

Local backend setup ran `npm install --no-package-lock` and `npm run check`; `wrangler types` may have created/updated `backend/worker-configuration.d.ts` locally, and `node_modules` exists locally.

Temporary database-test copies and the temporary OTP auth probe were created outside the repository under the owner's `D:\DnD_Aid` workspace.

Do not blindly commit local generated/temporary files. Repository policy and diffs must decide what belongs in Git.

## 12. Deferred provider work remains deferred

Do not activate R2 merely because Cloudflare is now configured. Object-storage provider selection remains deferred until Media/Handouts/assets reach real integration and receive a fresh `$0` review.

Workers AI remains the approved later official-SRD clarification direction only while it can be used safely under D-0075. It is not part of the next Player↔Server package.

## 13. Exact next implementation package

The next primary package is now:

**real authenticated Player <-> Server development integration**

Provider activation prerequisites are satisfied.

The next implementation should reuse the existing provider-neutral boundaries rather than inventing another stack:

```text
real Descope remembered Android session/token
        |
        v
existing HostedAccessTokenProvider
        |
        v
hosted account/campaign bootstrap
        |
        v
campaign create/select + durable hosted delivery
        |
        v
PC snapshot push/pull
        |
        v
second-device observation
        |
        v
offline/reconnect/convergence + revoke enforcement
```

The current Android owner-facing composition remains intentionally local-only until this package wires the real remembered session and hosted flows.

Continue using the tested Cloudflare/Neon/Descope DEV environment. Do not repeat provider account creation or activation unless a later concrete need requires a new resource.

## 14. Resume rule

A fresh implementation worker/chat should start from current remote `main`, then read:

1. `AGENTS.md`;
2. `README.md`;
3. `MANIFEST.md`;
4. `docs/PROJECT_STATE.md`;
5. `docs/checkpoints/LATEST.md`;
6. this checkpoint;
7. `docs/BRANCH_STATUS.md`;
8. D-0075 and relevant D-0071..D-0074 decisions;
9. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` for provider-neutral engineering contracts;
10. `docs/recovery/PROJECT_RECOVERY_PROMPT.md` when rebuilding context from a lost/fresh chat.

Where older operational documents still describe provider activation as pending, this checkpoint and `LATEST.md` supersede that old continuation state.

Historical Player physical acceptance remains separate; hosted activation success does not retroactively turn the old frozen Player candidate into an owner physical PASS.
