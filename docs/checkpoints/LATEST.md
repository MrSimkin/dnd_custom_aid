# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign bootstrap:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign + PC sync:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Unchanged-sync/no-op confirmation:** **OWNER-PHYSICAL PASS**  
**Multi-client PC convergence safety:** **IMPLEMENTED / AUTOMATED VERIFIED / PHYSICAL GATE INCONCLUSIVE; DIAGNOSTIC QA NEXT**  
**Convergence base PR:** #39  
**Diagnostic/recovery PR:** #40  
**QA build:** `0.4.0-preqa.14` / `41400`  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-15_HOSTED_SYNC_QA_LOG_AND_LEGACY_BASELINE_RECOVERY_READY_FOR_PHYSICAL_QA.md` — current Wave 4 checkpoint and exact next owner action;
3. `docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md` — PR #39 convergence design and original physical gate;
4. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md` — completed campaign/PC delivery physical evidence;
5. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_BOOTSTRAP_COMPLETE.md` — completed ordinary-Player hosted bootstrap proof;
6. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md` — completed Android hosted-session edge;
7. `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` — hosted DEV provider/environment evidence;
8. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and owner-guidance policy;
9. `docs/PROJECT_STATE.md` and `docs/BRANCH_STATUS.md` — broader state/lifecycle context; newer specific checkpoints control where older prose is stale;
10. `docs/ROADMAP.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md` and `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` as needed.

If older operational prose conflicts with this file or the current specific checkpoint, the newer specific checkpoint controls unless an even later approved decision/checkpoint supersedes it.

## Important corrections carried forward

- The repository is intentionally **public** under D-0075.
- External-service operating budget remains **USD $0** unless the owner explicitly changes it.
- Neon + Descope + Cloudflare DEV activation is complete; do not restart provider activation.
- Android remembered Descope session and `HostedAccessTokenProvider` are complete; do not create a second authentication/network abstraction.
- Ordinary Player hosted account/campaign bootstrap is complete.
- Campaign local-first creation + durable hosted delivery and PC push/pull are complete.
- The prior PC wire-envelope `VALIDATION_FAILED` defect was repaired; the blocked mutation was recovered and acknowledged.
- An additional unchanged Player sync kept the outbox empty, so the earlier no-op physical gate is **PASS**.
- The real Neon database name is `dnd-custom-aid-dev` with hyphens.
- Secret hygiene remains strict regardless of repository visibility.

## Current Wave 4 state

```text
remembered Android Descope session/token           COMPLETE
owner-facing hosted account/campaign bootstrap     COMPLETE
campaign create + durable hosted delivery          COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery       COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync no-op confirmation                   COMPLETE / OWNER-PHYSICAL PASS
        |
        v
multi-client PC convergence safety                  IMPLEMENTED / AUTOMATED VERIFIED
        |
        v
first physical convergence attempt                  INCONCLUSIVE: observability insufficient
        |
        v
permanent QA log + safe legacy baseline recovery   IMPLEMENTED / AUTOMATED VERIFIED
        |
        v
DIAGNOSTIC OWNER PHYSICAL SYNC                      NEXT
        |
        v
complete remaining convergence scenarios           AFTER LOG EVIDENCE
        |
        v
membership revoke + Player/DM authorization        FOLLOWING SEPARATE BOUNDARY
```

## What changed after the first physical convergence attempt

The owner observed a generic preserved-local-conflict message while the hosted outbox was empty. The app did not expose the exact PC conflict reason, revisions or baseline state, so that observation cannot be classified as a convergence PASS or FAIL.

The current leading explanation is the designed legacy-upgrade condition: an existing client can have old hosted revision metadata but no durable PR #39 PC baseline. If another client advances the hosted PC before the legacy device establishes an equal-revision baseline, the client correctly refuses to guess and reports `SYNC_BASELINE_MISSING`.

PR #40 adds a bounded recovery only when the complete normalized local PC state already equals the complete newer hosted state. In that unambiguous case the client may advance the revision and establish the baseline. If local and hosted differ, `SYNC_BASELINE_MISSING` remains and local state is preserved.

An empty outbox is **not** treated as proof that the local PC is clean.

## Permanent QA console

Debug builds now expose the launcher:

`DnD Aid - QA DEV`

For hosted sync the QA console can run the real synchronization and produce a copyable/shareable plain-text report with per-campaign and per-PC evidence, including exact conflict reason, local/hosted revision, baseline presence/revision, local-vs-baseline/current-hosted comparison, pull phase and outbox state.

The log excludes authentication tokens, authorization headers, provider secrets and database credentials.

This facility is permanent QA infrastructure. Future physical gates should extend its structured diagnostic projection instead of creating unrelated one-off debug screens.

## Campaign selection semantics

The active campaign selector controls local Player context only. Hosted synchronization reviews **all eligible hosted campaigns**. The normal Campaigns screen now states this explicitly and labels the active campaign as local-use state.

## Automated evidence

Implementation head `cf11c88cf7a424793c40f3d7bcb57b859e058b1e` passed Scaffold run `35043186839`, including:

- Shared desktop tests;
- Android debug assemble;
- Desktop build;
- Player guard scripts;
- backend type-check;
- hosted database migrations/contracts;
- Android debug APK upload.

A later non-behavioral launcher-label change renames the debug entry point to `DnD Aid - QA DEV`; use the final branch Scaffold result when selecting the APK artifact.

## Exact next owner gate

Install `0.4.0-preqa.14` over the existing phone installation **without uninstalling or clearing app data**.

Then:

1. open `DnD Aid - QA DEV`;
2. use the remembered hosted session or authenticate if required;
3. tap `Ejecutar sincronización QA`;
4. tap `Copiar log QA`;
5. paste the complete log into the active technical-assistant chat.

Do not clear the outbox, reset the database, recreate PCs/campaigns or reinstall before this diagnostic sync.

The next action after that log depends on its exact conflict reason and revision/baseline evidence. Do not continue by guessing.

Membership revoke and Player/DM authorization enforcement remain the following separate boundary.

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
PC snapshot push/pull       COMPLETE / OWNER-PHYSICAL PASS
No-op repeat sync           OWNER-PHYSICAL PASS
Multi-client convergence    AUTOMATED VERIFIED / DIAGNOSTIC PHYSICAL QA NEXT
```

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

These are visible residuals, not a reason to reopen completed provider/session/bootstrap/campaign/PC packages.

## Historical Player evidence remains bounded

Current integrated CI and hosted physical proofs do not retroactively establish physical owner acceptance of the historical frozen Player candidate. Preserve historical QA evidence for exactly what it tested.
