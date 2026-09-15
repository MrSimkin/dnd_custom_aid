# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Provider-neutral implementation validation:** Actions `34985799585` — **SUCCESS**  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / VERIFIED / OWNER-PHYSICAL PASS**  
**Android hosted campaign bootstrap:** **COMPLETE / VERIFIED / OWNER-PHYSICAL PASS**  
**Android hosted campaign + PC sync batch:** **INTEGRATED / AUTOMATED VERIFIED / OWNER-PHYSICAL PASS THROUGH RECOVERED PC DELIVERY**  
**Final unchanged-sync no-op physical confirmation:** **PENDING ONE OWNER CHECK**  
**Integrated campaign + PC sync commit:** `75d5acf354b41185255ff7d1a5eb4a689f300721`  
**Post-merge validation:** Actions `35028893643` / #1940 — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `README.md` — repository entry point;
3. `MANIFEST.md` — project-memory/navigation map;
4. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md` — current Wave 4 checkpoint, corrected physical-evidence boundary and exact continuation;
5. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_BOOTSTRAP_COMPLETE.md` — completed ordinary-Player hosted bootstrap proof;
6. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md` — completed Android hosted-session edge;
7. `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` — hosted DEV provider/environment evidence;
8. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and owner-guidance policy;
9. `docs/PROJECT_STATE.md` — current global product/engineering state;
10. `docs/BRANCH_STATUS.md` — branch lifecycle/resume rule;
11. `docs/ROADMAP.md` — implementation-wave sequence;
12. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` — provider-neutral engineering contracts;
13. `docs/recovery/PROJECT_RECOVERY_PROMPT.md` — reusable whole-project fresh-chat recovery prompt.

If older operational prose conflicts with this file or the current specific checkpoint, the newer specific checkpoint controls unless an even later approved decision/checkpoint supersedes it.

## Important supersession/corrections

The GitHub repository is intentionally **public** under D-0075. Any older wording treating `private: false` as an unexpected security/privacy discrepancy is superseded.

The first Cloudflare + Neon + Descope DEV activation is complete. Do not restart provider activation.

The Android Descope remembered-session edge is complete. Do not create a second authentication/network abstraction.

The ordinary Player hosted account/campaign bootstrap is complete. Do not treat read/bootstrap as pending.

Campaign creation + durable hosted delivery and PC snapshot push/pull are integrated through PR #34. The owner physically verified delivery through recovery/acknowledgement of the real blocked PC mutation and confirmed the outbox was empty afterward. One explicit physical no-op repeat-sync check remains before that particular proof is called fully closed.

The real Neon database name is `dnd-custom-aid-dev` with hyphens.

The deployed Worker contract uses `DATABASE_URL` + `DESCOPE_PROJECT_ID`, with optional `DESCOPE_BASE_URL`.

Secret hygiene remains strict regardless of repository visibility.

## Hard external-service budget

External-service operating budget remains **USD $0** unless the owner explicitly changes it.

The current hosted integration uses Neon Free, Descope Free and Cloudflare Workers Free. Do not silently enable paid plans, overage-enabled resources or billable add-ons.

Object storage remains deferred until real Media/Handouts/assets integration requires it. Workers AI remains later/conditional under D-0075.

## Current implementation state

`main` is the sole normal integrated-MVP development trunk. Historical Player/convergence branches are evidence only.

Completed/integrated:

- baseline convergence;
- Wave 2 Shared Integrated-MVP Spine;
- provider-neutral hosted foundation through PR #25;
- real Neon DEV migration and contract proof;
- real Descope DEV OTP authentication;
- deployed Cloudflare DEV Worker;
- real authenticated `/v1/me` -> application identity -> Neon persistence;
- representative Workers Free CPU/runtime proof for `/v1/me`;
- PR #30 Android remembered/refreshable Descope session integration and physical login/restart/logout proof;
- PR #32 ordinary-Player hosted account/campaign bootstrap and physical safe-refresh proof;
- PR #34 local-first campaign creation + durable hosted delivery;
- PR #34 PC snapshot push/pull + hosted read-back;
- real diagnosis and repair of a PC wire-envelope `VALIDATION_FAILED` caused by omitted default-valued backup metadata;
- durable recovery of the same blocked PC mutation after the serializer repair;
- owner physical confirmation that the repaired mutation was acknowledged/removed and the outbox became empty;
- automated implementation/CI coverage for the unchanged-sync no-op behavior.

Still pending physically:

- one unchanged Player synchronization followed by one more empty-outbox diagnostic, with no intervening local changes.

See `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md` for the exact current proof boundary.

## Hosted DEV status

```text
Neon PostgreSQL             COMPLETE / VERIFIED
Descope identity            COMPLETE / VERIFIED
Cloudflare Worker           COMPLETE / VERIFIED
Real auth round trip        VERIFIED
Real Neon persistence       VERIFIED
Workers Free CPU gate       PASS for tested representative path
Android session edge        COMPLETE / OWNER-PHYSICAL PASS
Android campaign bootstrap  COMPLETE / OWNER-PHYSICAL PASS
Campaign hosted delivery    COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull       INTEGRATED / PHYSICALLY VERIFIED THROUGH ACK
No-op repeat sync           AUTOMATED VERIFIED / OWNER PHYSICAL CHECK PENDING
```

Future materially heavier Worker routes should still receive representative CPU/runtime profiling.

## Exact current continuation

Wave 4 — Player <-> Server end-to-end — remains active.

```text
remembered Android Descope session/token           COMPLETE
existing HostedAccessTokenProvider                 COMPLETE
owner-facing hosted account/campaign bootstrap     COMPLETE
campaign create + durable hosted delivery          COMPLETE
PC snapshot push/pull + recovered blocked delivery INTEGRATED / PHYSICALLY VERIFIED
final unchanged-sync no-op physical confirmation   NEXT OWNER CHECK
        |
        v
second-client observation                          NEXT DEVELOPMENT BATCH
        +
offline edit / reconnect / convergence safety      NEXT DEVELOPMENT BATCH
        |
        v
membership revoke + Player/DM authorization        FOLLOWING BOUNDARY
```

### First action after resuming

Do not reinstall, clear data, create a new campaign or create a new PC.

1. Without changing the current campaign/PC, open the ordinary Player and press `Sincronizar con servidor` once.
2. Open `DnD Aid - Hosted DEV Auth` and press `Diagnosticar outbox local`.
3. If the outbox is still empty, record the unchanged-sync no-op physical gate as PASS.

Then begin the next primary development batch: **multi-client PC convergence safety**.

Do not ask the owner for an immediate one-change/one-test loop. Accumulate the closely related implementation first, with automated CI after each coherent step, then stop at the next natural physical gate.

The key correctness case to harden is:

> A server-newer PC revision must not silently overwrite an unsent local edit on another client.

Add local knowledge of the last synchronized PC snapshot/revision so the client can distinguish:

- old local copy unchanged since last sync -> safe to apply a newer hosted snapshot;
- old local copy changed locally while hosted state also advanced -> preserve local data and report an explicit conflict;
- offline local edit with no remote change -> reconnect and deliver normally;
- fresh second-client state -> pull the same stable hosted campaign/PC identity.

Preserve local-first behavior, stable IDs, revisions, idempotency, tombstones, explicit conflicts, DM authority vs PC ownership, owner vs controller distinction and non-destructive local recovery.

The debug-only `DnD Aid - Hosted DEV Auth` launcher remains verification infrastructure, not the final Player login UX.

## Security residuals carried forward

Important follow-up topics remain:

- JWT/fail-closed verification review;
- object-level authorization regression coverage;
- SQL/query safety;
- error/log secret leakage;
- replay/idempotency authorization;
- exact investigation of the locally reported **3 high severity npm vulnerabilities** — do not run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role;
- production-region/identity configuration review before release.

These are visible residuals, not a reason to reopen completed provider/session/bootstrap/campaign/PC implementation packages.

## Historical Player evidence remains bounded

Current integrated CI and hosted physical proofs do not retroactively establish physical owner acceptance of the historical frozen Player candidate. Preserve historical QA evidence for exactly what it tested.
