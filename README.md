# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use and a native Desktop DM application for rich preparation/administration plus complete operational DM fallback.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation or coding agent can resume from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files;
3. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
4. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md` — current Wave 4 handoff, physical evidence correction and exact continuation;
5. `docs/PROJECT_STATE.md` — current global state/navigation;
6. `docs/BRANCH_STATUS.md` — branch lifecycle map;
7. `docs/ROADMAP.md` — integrated implementation-wave sequence;
8. `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` — real hosted DEV provider/environment evidence when needed;
9. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0` provider/budget rule and owner-guidance contract;
10. relevant decisions, architecture/testing/workflow docs and older checkpoints as needed.

Reusable fresh-chat recovery prompt:

`docs/recovery/PROJECT_RECOVERY_PROMPT.md`

## Current repository authority

The owner has authorized integrated-MVP implementation. **`main` is the normal integrated-MVP development trunk.** Historical Player/convergence branches remain evidence, not normal resume points.

Provider-neutral implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Actions `34985799585` completed SUCCESS across Player guards, shared/Kotlin tests, Android, Desktop, backend, hosted PostgreSQL contracts and APK upload.

The latest integrated Player <-> hosted batch is PR #34:

`75d5acf354b41185255ff7d1a5eb4a689f300721`

with exact-head Actions `35027987125` / #1939 SUCCESS and post-merge Actions `35028893643` / #1940 SUCCESS.

## Working relationship

AI/coding agents perform heavy technical execution. The owner decides actual product/workflow/UX/game-semantic/privacy/scope choices, external account/resource activation and meaningful cost/security/convenience/lock-in tradeoffs. Routine engineering must not be pushed back to the owner for ceremonial approval.

The owner is technically oriented and a heavy/power user, understands programming concepts and can perform substantial hands-on work, but is **not a professional software developer**. Owner-facing instructions should explain what/why, use real terminology with plain explanations, give ordered actions and expected results, and clearly mark meaningful security/billing/destructive risks.

The owner explicitly prefers **batched development/testing**: accumulate several closely related implementation steps behind automated CI, then stop at one natural physical Android gate rather than demanding an APK install after every small change.

## Hard external-service budget

External-service operating budget is **USD $0** unless the owner explicitly changes it.

A headline free tier is not enough. Before any new provider/resource activation, current official provider information must be checked for payment-method requirements, automatic overage/billing behavior, hard quota/failure behavior, region/data-location consequences and migration/lock-in.

Never enable paid plans, paid add-ons, billing commitments or overage-enabled resources without explicit owner approval.

## Repository visibility and security

The GitHub repository is intentionally **public**. `private: false` is expected and is not a security discrepancy.

Never commit database credentials, provider API/admin/deployment tokens, access/refresh/session tokens, private keys or other confidentiality-dependent material.

## Approved architecture snapshot

- Android: Kotlin + Jetpack Compose, minimum Android 11 / API 30.
- DM Desktop: Kotlin + Compose Multiplatform Desktop.
- Local persistence: SQLite + SQLDelight where local/offline behavior matters.
- Hosted relational database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, TypeScript.
- Authentication proof: **Descope**; application/domain authorization remains project-owned.
- Native clients do not connect directly to Neon or hold DB credentials.
- Shared native HTTP: Ktor Client.
- Project-specific sync: stable IDs, revisions, idempotent mutations, durable outbox, tombstones, scoped pull/push and explicit conflict handling.
- Hosted PC current state: versioned application-owned JSONB snapshot plus relational authorization/revision/lifecycle metadata.
- Object storage is MVP but provider selection remains deferred until Media/Handouts/assets reach integration.
- Workers AI remains later/conditional under the `$0` policy.
- Full verifiable server backup/export remains MVP.
- PC Sheet PDF export remains local/offline and cross-surface with one canonical semantic export path.

## Current implementation state

Completed and integrated:

```text
Shared Integrated-MVP Spine                    ✅
Provider-neutral hosted foundation             ✅
Real Neon + Descope + Cloudflare DEV           ✅
Android remembered hosted session              ✅ owner physical pass
Ordinary Player hosted campaign bootstrap      ✅ owner physical pass
Campaign local-first hosted delivery           ✅ owner physical pass
PC snapshot push/pull + blocked retry recovery ✅ owner physical pass
```

A real Android PC snapshot defect was found during the batched PR #34 gate: one durable outbox row became `PC_SNAPSHOT_PUT | BLOCKED | error=VALIDATION_FAILED` because default-valued character-backup `format`/`version` fields could be omitted from the Ktor JSON wire payload. The serializer was repaired, regression coverage added, the **same preserved mutation** retried, and the owner confirmed the outbox became empty.

One exact physical observation is intentionally carried forward rather than overclaimed: an unchanged repeat Player sync followed by another empty-outbox diagnostic was requested but not separately reported before consolidation. Include it in the next batched physical gate.

## Exact current continuation

Wave 4 — Player <-> Server end-to-end — remains active.

The next primary implementation batch is:

**multi-client PC convergence safety**

```text
current hosted Player path                     ✅
        |
        v
last-synchronized PC baseline knowledge        NEXT
        +
second-client observation                      NEXT BATCH
        +
offline edit / reconnect safety                NEXT BATCH
        +
remote-newer vs local-edit conflict handling   NEXT BATCH
        |
        v
membership revoke + Player/DM authorization    FOLLOWING BOUNDARY
```

Key correctness rule:

> A server-newer PC revision must not silently overwrite an unsent local edit on another client.

Do not restart provider activation, Android session acquisition, campaign bootstrap or first campaign/PC delivery. Do not invent another auth/network/sync stack.

The debug-only `DnD Aid - Hosted DEV Auth` launcher remains verification infrastructure, not final product login UX.

## Integrated MVP scope

The target remains:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Desktop is both a rich authoring/admin workbench and complete operational DM fallback. Protected MVP scope includes Player hosted integration, DM live surfaces, combat authority resume, Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

The project remains paper-first and is intentionally not a generalized VTT, automatic legality engine, CRDT platform, marketplace/social product or enterprise infrastructure exercise.

## Build and verification

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Backend:

```bash
cd backend
npm install
npm run check
```

Hosted PostgreSQL contracts are also exercised by Scaffold CI. See `docs/TESTING.md` for verification strategy.
