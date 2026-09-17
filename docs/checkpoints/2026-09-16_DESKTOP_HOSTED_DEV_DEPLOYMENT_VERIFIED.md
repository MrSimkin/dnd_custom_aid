# Wave 5 — Desktop hosted DEV Worker deployment verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Deployed repository head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Exact-head Scaffold:** `35163179550` — **SUCCESS**  
**Cloudflare Worker:** `dnd-custom-aid-api`  
**Deployment Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Status:** DEV WORKER DEPLOYMENT / ROUTE-PRESENCE VERIFIED; REAL DESKTOP QA NEXT

## Result

The external Cloudflare deployment gate for Wave 5 PR #44 is complete.

The owner deployed the already implemented repository code to the existing DEV Worker using the repository-pinned Wrangler `4.127.1` and the already authenticated local Wrangler OAuth session. No new Worker, provider resource, paid plan, payment method, secret rotation or database reset was required.

The local checkout was confirmed at the exact expected deployed head:

`a6c0532878e8ef49ddfb894fa71076c1af73587a`

The deployment updated the existing Worker at:

`https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`

Cloudflare reported current deployment Version ID:

`130d35e7-7903-47b2-8203-d74f9ec3db55`

## Verification evidence

### Repository / CI

Exact-head Scaffold `35163179550` completed **SUCCESS** across:

- backend type-check / tests;
- hosted PostgreSQL migrations/contracts;
- Kotlin/Shared/Desktop tests and build;
- Android build and permanent Player guards;
- Android debug APK upload.

### Provider deployment

Wrangler authenticated successfully to the already expected Cloudflare account and deployed the existing `dnd-custom-aid-api` Worker.

Secret values were not re-entered or exposed. Existing provider-side `DATABASE_URL` and `DESCOPE_PROJECT_ID` configuration remained in place.

### Public secret-free probes

After deployment:

```text
GET /health
-> 200 {"status":"ok","service":"dnd-custom-aid-api"}
```

and the deliberately unauthenticated Campaign Administration route probe:

```text
GET /v1/campaigns/00000000-0000-0000-0000-000000000000/members
-> 401 {"code":"UNAUTHENTICATED","message":"Authentication required."}
```

The `401 UNAUTHENTICATED` response proves that the deployed Worker recognizes the Campaign Administration member-roster route and reaches the authentication boundary. The previous pre-route Worker behavior would have returned `404` for an unknown route.

## What this proves

This checkpoint proves:

- current PR #44 backend code was deployed to the existing DEV Worker;
- the Worker remains healthy after deployment;
- the Campaign Administration roster route is live at least through route recognition and authentication enforcement;
- required Worker configuration remained available after deployment;
- no new provider/billing/resource action was needed.

## What this does NOT yet prove

This checkpoint does **not** yet claim:

- successful real Desktop email-OTP authentication against the newly deployed Worker;
- successful authenticated hosted campaign bootstrap from Desktop;
- successful real hosted member-roster retrieval from Desktop;
- successful Kick/Ban/Lift-Ban mutations against a real Player membership;
- owner acceptance of the new Desktop UI/settings behavior;
- persistence/sign-out/relaunch acceptance.

Those are the next owner Windows Desktop QA gate.

## Dependency residual

The owner-local `npm install --no-package-lock` again reported the already-known `3 high severity vulnerabilities`. No blind `npm audit fix --force` was run. This remains an explicit security-hardening residual and is not newly introduced by this deployment.

## Next exact gate

The next owner action is the bounded Windows Desktop live-QA preflight documented in:

`docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md`

The first manual pass should verify launch, real email-OTP login, hosted bootstrap/canonical campaign context and real roster retrieval. If a real Player row is present, the later moderation sequence can proceed. If no Player membership is available, stop after roster evidence rather than inventing data, editing Neon ad hoc or expanding scope into invitation/rejoin functionality.

PR #44 must remain draft/unmerged until the required real Desktop integration and owner QA pass.