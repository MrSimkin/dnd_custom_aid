# DEV Worker deployment handoff — Wave 5 PR #44

**Purpose:** bounded owner action for the first real deployment/integration of the Campaign Administration routes used by Desktop PR #44.

This file does not authorize new provider resources, paid plans, secret rotation, database resets or architecture changes. It deploys the already implemented Worker code to the already verified DEV Worker.

## Why this handoff exists

The repository and CI can verify Worker code, hosted database contracts and native clients, but the repository has no automatic Cloudflare deployment workflow. Therefore green CI is not proof that the current Campaign Administration routes are live on the real DEV Worker.

The active agent/chat must not loop on alternate Cloudflare access methods if it lacks authenticated provider capability. The owner performs this one authenticated action locally, then returns only non-secret evidence.

## Existing DEV target

- Worker name: `dnd-custom-aid-api`
- public DEV URL: `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`
- local repository: `D:\DnD_Aid\repo\dnd_custom_aid`
- Worker directory: `D:\DnD_Aid\repo\dnd_custom_aid\backend`

Existing provider configuration already holds the required secret values. Do not recreate the Worker and do not re-enter or rotate secrets merely for this deployment.

## Owner action

Open PowerShell or Windows Terminal.

### 1. Move to the repository and verify there is no unexpected local work

```powershell
cd D:\DnD_Aid\repo\dnd_custom_aid
git status --short
```

Expected: no unexpected modified/untracked project files. If there is work you care about, stop before switching or pulling.

### 2. Update the active PR #44 branch

```powershell
git fetch origin
git switch wave5/desktop-hosted-campaign-administration
git pull --ff-only origin wave5/desktop-hosted-campaign-administration
git rev-parse HEAD
```

The returned HEAD must match the current remote PR #44 head reported by the technical agent before deployment. If it does not, stop and return the hash instead of deploying a different revision.

### 3. Enter the Worker project and use the repository-pinned Wrangler

```powershell
cd backend
npm install --no-package-lock
npx wrangler --version
npx wrangler whoami
```

Expected Wrangler version from the current repository is `4.127.1` unless a later repo change intentionally updates it.

`whoami` must show the already authorized Cloudflare identity/account. If it reports unauthenticated, use `npx wrangler login` and complete the browser authorization locally, then run `npx wrangler whoami` again.

Do not send login tokens, browser authorization details or local Wrangler credential files to chat.

### 4. Optional local pre-deploy verification

```powershell
npm run check
```

CI already performs these checks, but this is safe if you want a local confirmation immediately before deployment.

### 5. Deploy the existing Worker code

```powershell
npm run deploy
```

Expected target: existing Worker `dnd-custom-aid-api` and its existing `workers.dev` URL.

The deployment must not require creating a new Worker, enabling a paid plan, adding a payment method, rotating credentials or pasting secret values. The Wrangler config declares `DATABASE_URL` and `DESCOPE_PROJECT_ID` as required secrets so a missing provider-side secret should fail the deployment rather than be silently ignored.

### 6. Run secret-free public verification

```powershell
node -e "fetch('https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/health').then(async r => console.log(r.status, await r.text()))"
node -e "fetch('https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/v1/campaigns/00000000-0000-0000-0000-000000000000/members').then(async r => console.log(r.status, await r.text()))"
```

Expected:

```text
/health                                      -> 200
/v1/campaigns/<zero-uuid>/members            -> 401 UNAUTHENTICATED
```

The second request intentionally has no JWT. `401` proves that the new route is live far enough to reach authentication. `404` means the deployed Worker still does not recognize the route.

## Stop conditions

Stop without improvising if any of these occur:

- `git status` shows local work you do not understand or do not want to lose;
- local HEAD differs from the current remote PR #44 head supplied by the agent;
- `wrangler whoami` shows an unexpected Cloudflare account;
- deployment proposes creating a different Worker/resource;
- Cloudflare asks for billing/payment/plan changes;
- Wrangler reports missing required secrets;
- deployment asks you to provide or rotate secret values;
- the deployed URL/Worker name differs from the existing DEV target;
- `/health` is not 200 after deployment;
- the Campaign Administration route returns 404 after deployment;
- any destructive database/provider action is suggested.

## Do not share

Do not paste into chat or Git:

- `DATABASE_URL`;
- Cloudflare API/OAuth tokens or local Wrangler credential files;
- Descope OTPs;
- Descope session/refresh JWTs;
- any password, private key or database credential.

## Return to the technical agent

Return only:

1. the output of `git rev-parse HEAD`;
2. the non-secret summary/output from `npx wrangler whoami` sufficient to confirm the expected account (redact anything you consider unnecessary);
3. the `npm run deploy` output, after checking that it contains no secret values;
4. both Node probe outputs from step 6.

After that evidence is validated, the next stage is real Desktop authentication/bootstrap/roster/moderation integration and owner Windows Desktop QA. Do not merge PR #44 before those gates pass.
