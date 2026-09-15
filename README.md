# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use and a native Desktop DM application for rich preparation/administration plus complete operational DM fallback.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation or coding agent can resume from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files;
3. `docs/PROJECT_STATE.md` — global state/navigation;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md` — current Wave 4 checkpoint and exact continuation;
6. `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` — real hosted DEV environment/provider evidence;
7. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0` provider/budget rule and owner-guidance contract;
8. `docs/BRANCH_STATUS.md` — branch lifecycle map;
9. `docs/DECISIONS_RECENT.md` + relevant detailed decisions;
10. `docs/CONVENTIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`;
11. relevant checkpoints/feature files.

Reusable lost-chat/fresh-chat recovery prompt:

`docs/recovery/PROJECT_RECOVERY_PROMPT.md`

Hosted-provider activation record:

`docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md`

## Current repository authority

The owner has authorized integrated-MVP implementation. **`main` is the normal integrated-MVP development trunk.** Historical Player/convergence branches remain evidence, not normal resume points.

Provider-neutral implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Actions `34985799585` completed SUCCESS across Player guards, shared/Kotlin tests, Android, Desktop, backend, hosted PostgreSQL contracts and APK upload.

The Android hosted-session package was integrated through PR #30 at:

`bf5f843066a7c2f8674a4577918156e8a8d2c139`

Post-merge Actions `35020281492` / #1898 completed SUCCESS.

## Working relationship

AI/coding agents perform heavy technical execution. The owner decides actual product/workflow/UX/game-semantic/privacy/scope choices, external account/resource activation and meaningful cost/security/convenience/lock-in tradeoffs. Routine engineering must not be pushed back to the owner for ceremonial approval.

The owner is technically oriented and a heavy/power user, understands programming concepts and can perform substantial hands-on work, but is **not a professional software developer**. Owner-facing instructions must therefore teach while guiding:

- plain-language `what` and `why` first;
- real technical terminology explained rather than hidden;
- explicit ordered actions;
- what to expect to see after important steps;
- clear secret/billing/security stop conditions;
- ASCII diagrams, flows or wireframes when they improve understanding;
- clear separation between owner actions and implementation handled by the technical agent.

Do not patronize the owner and do not assume professional-developer fluency.

## Hard external-service budget

External-service operating budget is **USD $0** unless the owner explicitly changes it.

A headline free tier is not enough. Before any new provider/resource activation, current official provider information must be checked for payment-method requirements, automatic overage/billing behavior, hard quota/failure behavior, region/data-location consequences and migration/lock-in.

Prefer free services that fail/suspend/require explicit upgrade when exhausted. Never enable paid plans, paid add-ons, billing commitments or overage-enabled resources without explicit owner approval.

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
- Workers AI remains the approved official-SRD clarification direction while safely usable under the `$0` policy.
- Object storage is MVP but provider selection remains **deferred** until Media/Handouts/assets reach integration.
- Full verifiable server backup/export is MVP.
- PC Sheet PDF export is local/offline and cross-surface with one canonical semantic export path.

## Current implementation state

Wave 2 — Shared Integrated-MVP Spine — is complete.

Provider-neutral hosted work is integrated through PR #25, including hosted API/database contracts, native hosted transport, provider-neutral token boundary, durable hosted outbox, local-first campaign delivery, membership lifecycle reconciliation, hosted PC current-state snapshot persistence/authorization, optimistic revision/idempotency/conflict/tombstone handling and safe same-identity reconciliation.

The first real DEV provider activation is **COMPLETE / VERIFIED**:

```text
Neon PostgreSQL       ✅
Descope identity      ✅
Cloudflare Worker     ✅
Real JWT-auth /v1/me  ✅
Real Neon persistence ✅
Workers Free CPU gate ✅
```

The representative authenticated `/v1/me` path showed about 1 ms Worker CPU per visible invocation with no observed benchmark errors.

The first Android hosted-session edge is also **COMPLETE / VERIFIED / OWNER-PHYSICAL PASS**:

```text
Descope Android SDK       ✅
Remembered session        ✅
HostedAccessTokenProvider ✅
Real Android -> Worker    ✅
Restart/session reuse     ✅
Logout/session cleared    ✅
```

The separate `DnD Aid - Hosted DEV Auth` launcher is debug-only verification infrastructure. It is not the final product login UX and the ordinary Player launcher is not yet product-login-gated.

See `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md` for exact implementation/manual evidence and `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` for provider/environment evidence.

## Exact current continuation

Wave 4 — Player <-> Server end-to-end — is active.

The remembered Android Descope session/token and existing `HostedAccessTokenProvider` integration are complete. The next primary package is:

**owner-facing hosted account/campaign bootstrap**

Expected path:

```text
remembered Android Descope session/token       ✅
existing HostedAccessTokenProvider             ✅
        |
        v
hosted account/campaign bootstrap              NEXT
        |
        v
campaign create/select + durable hosted delivery
        |
        v
PC snapshot push/pull
        |
        v
second-device + offline/reconnect + revoke tests
```

Provider activation prerequisites are already satisfied. Do not repeat account setup or invent another auth/network/sync stack.

Future materially heavier Worker routes should still be profiled. The current CPU PASS applies to the representative authenticated path already tested.

R2/object storage is not part of this next package.

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
