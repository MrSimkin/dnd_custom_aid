# Checkpoint — Wave 7 PC authority DEV deployment verified

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk before this documentation closure:** `main@8693f834e9f663fcabcfad33c0c6afc193486ae5`  
**Implementation PR:** #79 — merged as `2e12400ee18026c702d6727793a3aea5d23d07b4`  
**Repository documentation closure PR:** #80 — merged as `8693f834e9f663fcabcfad33c0c6afc193486ae5`  
**PR #79 post-merge Scaffold:** `35291685597` — SUCCESS  
**PR #80 post-merge Scaffold:** `35292356409` — SUCCESS  
**DEV Worker:** `dnd-custom-aid-api`  
**Verified deployed Worker version:** `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`  
**Deployment gate:** CLOSED

## Owner-executed deployment

The owner deployed from the project laptop repository:

`D:\DnD_Aid\repo\dnd_custom_aid\backend`

Command:

`npm run deploy`

Wrangler reported a successful upload/deployment of `dnd-custom-aid-api` and activated version:

`ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`

No provider secret value was copied into Git or chat.

## Live smoke verification

Immediately after deployment, the owner executed three secret-free checks against:

`https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`

Observed results:

1. `GET /health`
   - HTTP `200`
   - body: `{"status":"ok","service":"dnd-custom-aid-api"}`

2. unauthenticated Campaign Administration route:
   - `GET /v1/campaigns/00000000-0000-0000-0000-000000000000/members`
   - HTTP `401`
   - body: `{"code":"UNAUTHENTICATED","message":"Authentication required."}`

3. unauthenticated PC authority route:
   - `GET /v1/pcs/00000000-0000-0000-0000-000000000000/authority`
   - HTTP `401`
   - body: `{"code":"UNAUTHENTICATED","message":"Authentication required."}`

## What this proves

The smoke check proves:

- the Worker deployment succeeded;
- the public health route remains healthy;
- the pre-existing protected Campaign-members route remains present;
- the new PC authority path is present in the deployed Worker;
- both protected paths still fail closed at the authentication boundary.

The PC authority request used `GET`, while the actual mutation contract is `PUT /v1/pcs/{pcId}/authority`. The important route-presence result is that the deployed path reaches authentication and returns 401 rather than falling through to 404.

## What this does not claim

This smoke check did not exercise a live authenticated owner/controller mutation against DEV campaign data.

The semantics of the mutation itself remain supported by the already-green backend/API tests, hosted PostgreSQL contract tests, shared Kotlin client tests, Desktop tests, PR gate and post-merge CI.

Therefore the **deployment/route/auth-boundary gate is closed** without overstating that a destructive or state-changing live provider test was performed.

## PC authority package status

PC ownership/controller administration is now:

- repository-integrated;
- CI green;
- DEV Worker deployed;
- public health verified;
- old protected route presence verified;
- new protected authority route presence verified;
- authentication fail-closed behavior verified.

No further provider action is required merely to continue Wave 7.

## Next bounded package

**PC Sheet PDF Export — shared semantic/render-plan foundation** under D-0074.

Expected branch:

`wave7/pc-sheet-pdf-export-foundation`

Initial bounded direction remains:

- canonical export snapshot;
- Permanent vs Current Snapshot export-state selection;
- selected visual-family model;
- custom-stat presentation mode;
- overflow/Extended-page semantic render plan;
- optional portrait input behavior;
- optional appended Spellbook contract;
- shared/platform-neutral semantics first;
- local/offline generation.

## Deferred PC administration clarification

Freeze/unfreeze remains required by D-0072, but it is not implementation-ready because the durable product records do not define what freezing must block and the repository has no freeze field/contract.

Do not equate freeze automatically with `CharacterStatus.INACTIVE`, hosted deletion, or a blanket edit lock.

## Cost/security state

- hard external-service budget remains USD $0;
- repository is intentionally public;
- no secret values were exposed in the deployment evidence;
- object-storage selection remains deferred until Media/Handouts/assets require it.
