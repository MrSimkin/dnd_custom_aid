# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use and a native Desktop DM application for rich preparation/administration plus complete operational DM fallback.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation or coding agent can resume from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files;
3. `docs/PROJECT_STATE.md` — global state/navigation;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. the current checkpoint referenced by `LATEST.md`;
6. `docs/BRANCH_STATUS.md` — branch lifecycle map;
7. `docs/DECISIONS_RECENT.md` + relevant detailed decisions;
8. `docs/CONVENTIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`;
9. relevant historical checkpoints only when their evidence is needed.

Reusable recovery prompt:

`docs/recovery/PROJECT_RECOVERY_PROMPT.md`

Reusable external-provider handoff prompt:

`docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md`

## Current repository authority

The owner has authorized integrated-MVP implementation. **`main` is the sole normal integrated-MVP trunk.** Historical Player/convergence branches remain evidence, not normal resume points.

Current implementation work is on the short-lived Wave 5 branch recorded by `docs/checkpoints/LATEST.md` and `docs/BRANCH_STATUS.md`.

## Working relationship

AI/coding agents perform heavy technical execution. The owner decides actual product/workflow/UX/game-semantic/privacy/scope choices, external account/resource activation and meaningful cost/security/convenience/lock-in tradeoffs. Routine engineering must not be pushed back to the owner for ceremonial approval.

The owner is technically oriented and a heavy/power user, but is not a professional software developer. Owner-facing instructions must explain the plain-language `what` and `why`, use real technical terms with explanation, provide explicit ordered actions and expected results, and clearly separate owner actions from agent implementation.

## External-provider handoff rule

Cloudflare/Descope/Neon or other provider work is an explicit capability boundary.

If the current agent environment cannot actually authenticate to or operate the provider, it must **not keep searching for alternate connection paths**. It must finish safe repo/CI preparation, give the owner one exact action packet, stop at that boundary, and resume from the owner's non-secret evidence.

Never paste provider secrets, credentials, OTPs or session tokens into chat or Git.

See `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md` and `docs/WORKFLOW.md`.

## Hard external-service budget

External-service operating budget is **USD $0** unless the owner explicitly changes it. Never enable paid plans, paid add-ons, billing commitments or overage-enabled resources without explicit owner approval.

## Repository visibility and security

The GitHub repository is intentionally **public**. Never commit database credentials, provider API/admin/deployment tokens, access/refresh/session tokens, private keys or other confidentiality-dependent material.

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
- Full verifiable server backup/export and cross-surface PC Sheet PDF export remain protected MVP scope.

## Current implementation state

Completed/integrated foundations include the Shared Integrated-MVP Spine, hosted foundation and real DEV provider activation. Wave 4 Player <-> Server is complete for its recorded scope.

Wave 5 is active:

```text
Desktop workbench + local campaign                 ✅ complete / merged / owner-QA pass
hosted Campaign membership administration core    ✅ complete / merged (#43)
Desktop hosted auth + Campaign Administration      ✅ repository implementation / CI verified
DEV Worker deployment                              ✅ verified
owner Windows Desktop live-QA preflight            NEXT
real moderation QA                                 after roster evidence
final owner QA / PR #44 merge                      after all gates pass
```

The current PR #44 implementation includes Desktop Descope email-OTP/session acquisition, hosted campaign bootstrap, real hosted member roster/moderation consumption, non-secret diagnostics and the approved font/theme preview settings follow-up.

The DEV Worker deployment is now explicit evidence rather than an outstanding provider gate. The current next action is real Windows Desktop QA against that deployed Worker. See `docs/checkpoints/LATEST.md` and `docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md`.

## Integrated MVP scope

The target remains:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Desktop is both a rich authoring/admin workbench and complete operational DM fallback. Protected scope includes Player hosted integration, DM live surfaces, combat authority resume, Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

The project remains paper-first and is intentionally not a generalized VTT, automatic legality engine, CRDT platform, marketplace/social product or enterprise infrastructure exercise.

## Build and verification

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Backend:

```bash
cd backend
npm install --no-package-lock
npm run check
```

Hosted PostgreSQL contracts are exercised by Scaffold CI. See `docs/TESTING.md` for verification strategy.