# Repository Manifest

This file maps authoritative project-control files and implemented areas so a fresh human or AI can resume without branch archaeology.

## Current authority model

The owner has authorized integrated-MVP implementation. **`main` is the single normal integrated-MVP trunk.** Historical Player/convergence lines are evidence only.

Provider-neutral implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Actions `34985799585` — SUCCESS.

Hosted DEV provider activation is **COMPLETE / VERIFIED**.

Android remembered-session, ordinary hosted campaign bootstrap, campaign hosted delivery and PC snapshot delivery/recovery are integrated and physically exercised. The latest integrated Player <-> hosted batch is PR #34 at `75d5acf354b41185255ff7d1a5eb4a689f300721`.

`docs/checkpoints/LATEST.md` controls the practical resume point. The current exact handoff is `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md`.

`docs/BRANCH_STATUS.md` controls branch lifecycle.

## Start/resume control files

### `README.md`
Repository entry point and read sequence.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. Routine engineering is delegated; owner escalation is for real consequential decisions/gates.

### `docs/checkpoints/LATEST.md`
Exact practical resume pointer and current supersession/correction rules.

### `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md`
Current Wave 4 handoff. Records PR #34 integration, the real blocked PC `VALIDATION_FAILED` defect, serializer repair, successful recovery of the same preserved mutation, the exact one residual owner no-op observation carried forward, the requested batched test cadence and the next multi-client convergence batch.

### `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md`
Historical completion record for PR #34. It remains evidence, but the newer handoff supersedes its claim that the final unchanged repeat-sync owner observation had already been separately reported.

### `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_BOOTSTRAP_COMPLETE.md`
Completed normal-Player hosted account/campaign bootstrap proof.

### `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md`
Completed Android Descope remembered-session and `HostedAccessTokenProvider` proof.

### `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`
Hosted DEV environment truth: real Neon/Descope/Cloudflare resources, migration/auth/persistence/runtime evidence, local-development caveats and security residuals.

### `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`
Controlling owner policy for:

- external-service budget = **USD $0** unless explicitly changed;
- free-tier billing/overage/hard-cap evaluation;
- provider revalidation;
- intentional public GitHub repository visibility;
- strict secret hygiene;
- owner-facing guidance style.

### `docs/recovery/PROJECT_RECOVERY_PROMPT.md`
Reusable whole-project fresh-chat recovery prompt, refreshed through PR #34 and the multi-client convergence handoff.

### `docs/PROJECT_STATE.md`
Current global implementation/product state.

### `docs/BRANCH_STATUS.md`
Canonical branch lifecycle map and normal resume rule.

### `docs/ROADMAP.md`
Integrated implementation waves and ordering.

### `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`
Provider-neutral engineering contracts. Newer checkpoints control operational state where older baseline prose still describes provider/session/client wiring as pending.

### `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md`
Historical/operational record of the first Cloudflare + Neon + Descope activation gate, now completed.

## Controlling product/architecture decisions

- `D-0071` — integrated Player + Server + DM architecture, authority/sync/recovery/Desktop direction;
- `D-0072` — DM Desktop product and authoring/management surfaces;
- `D-0073` — integrated MVP boundary and implementation governance;
- `D-0074` — cross-surface PC Sheet PDF export definition;
- `D-0075` — zero-budget provider policy, provider revalidation, repository visibility clarification and owner-guidance contract.

Use `docs/DECISIONS_RECENT.md` for navigation to detailed decision records.

## Other mandatory/reference docs

- `docs/CONVENTIONS.md` — recurring conventions;
- `docs/PRODUCT.md` — approved product scope;
- `docs/WORKFLOW.md` — AI-led development/review workflow;
- `docs/ARCHITECTURE.md` — integrated architecture; older exact-resume prose may be historical where superseded by `LATEST`;
- `docs/TESTING.md` — verification strategy/evidence;
- `docs/TEST_DEVICES.md` — owner-confirmed physical devices.

Historical checkpoints remain evidence; old `next` instructions may be superseded by current `LATEST.md`.

## Current implemented areas

### `shared/`
Kotlin Multiplatform domain/persistence/networking/sync foundation with SQLDelight, kotlinx.serialization and Ktor. Includes stable identities/revisions, hosted transport, durable outbox, campaign lifecycle sync, hosted PC snapshot reconciliation/conflict logic and the repaired hosted JSON envelope encoding needed by real PC snapshot delivery.

### `androidApp/`
Mature Player Android runtime protected by permanent CI guards. Real Descope remembered-session, ordinary hosted campaign bootstrap, campaign delivery, PC snapshot push/pull and debug-only hosted outbox diagnostics/recovery are integrated.

The next active package is multi-client PC convergence safety, not another provider/auth/bootstrap pass.

### `desktopApp/`
Compose Multiplatform Desktop foundation. Full approved DM Desktop workbench remains protected integrated-MVP work after Player <-> Server contracts are hardened end-to-end.

### `backend/`
TypeScript Cloudflare Worker/API with `/v1` hosted routes, Descope/JWKS verification, application-owned authorization, campaign/membership lifecycle and PC snapshot behavior. The DEV Worker is deployed at the endpoint recorded in the hosted completion checkpoint.

### `database/`
Explicit hosted PostgreSQL migration/contracts validated in CI and additionally exercised against real Neon DEV during activation.

### `scripts/`
Permanent Player guard scripts exercised by Scaffold CI.

### `assets/character-sheets/templates/`
PC Sheet PDF visual authorities; not the canonical PC model.

## Current integrated implementation position

Wave 2 Shared Integrated-MVP Spine is complete. Provider-neutral hosted foundation is integrated through PR #25. The first real hosted DEV environment is activated and verified.

Wave 4 completed/integrated edges now include:

- PR #30 — remembered Android Descope session + token provider;
- PR #32 — normal Player hosted account/campaign bootstrap;
- PR #34 — local-first hosted campaign delivery + PC snapshot push/pull + repaired wire-envelope serializer + durable recovery of the real blocked PC mutation.

PR #34 validation:

- exact-head Actions `35027987125` / #1939 — SUCCESS;
- post-merge Actions `35028893643` / #1940 — SUCCESS.

Owner physical evidence includes successful recovery of the same preserved blocked PC mutation until the debug outbox reported empty.

One final unchanged-repeat no-op owner observation is carried forward into the next consolidated physical gate; do not overclaim it as separately observed already.

The next meaningful implementation batch is **multi-client PC convergence safety**.

Key rule:

> A server-newer PC revision must not silently overwrite an unsent local edit on another client.

The owner prefers batched implementation/testing. Accumulate related implementation behind CI, then stop at a natural physical gate rather than demanding an install after every small change.

Object-storage provider selection remains deferred until real asset integration. Workers AI remains later/conditional under the `$0` policy.

## Security / repository visibility

The GitHub repository is intentionally **public**. This is expected configuration, not a security discrepancy.

Never commit provider credentials, database credentials, access/session tokens, private keys or other secrets.

Security residuals visible at the current checkpoint include dependency-audit review, least-privilege Neon runtime-role evaluation, continued object-level authorization/JWT/query/log hardening and later production Descope-region/configuration review.

## Current integrated-MVP boundary

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Protected scope includes Player hosted integration, full DM live Android/Desktop capability, combat authority resume, Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

## Authority rule

If documents conflict:

1. later specific Approved decisions control older general prose;
2. `AGENTS.md` controls working governance;
3. `docs/BRANCH_STATUS.md` controls branch lifecycle;
4. `docs/checkpoints/LATEST.md` controls the practical resume point;
5. the current exact handoff controls its specific implementation/physical-evidence facts;
6. `docs/PROJECT_STATE.md` controls current global state except where a newer specific decision/checkpoint supersedes it;
7. detailed decisions/checkpoints control their specific approved facts;
8. historical checkpoints remain evidence only;
9. surface material contradictions instead of guessing.
