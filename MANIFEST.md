# Repository Manifest

This file maps authoritative project-control files and implemented areas so a fresh human or AI can resume without branch archaeology.

## Current authority model

The owner has authorized integrated-MVP implementation. **`main` is the single normal integrated-MVP trunk.** Historical Player/convergence lines are evidence only.

Provider-neutral implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Actions `34985799585` — SUCCESS.

Hosted DEV provider activation is **COMPLETE / VERIFIED** and the first Android hosted-session edge is **COMPLETE / VERIFIED / OWNER-PHYSICAL PASS**.

`docs/checkpoints/LATEST.md` controls the practical resume point. The current implementation checkpoint is `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md`; `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` remains the hosted-environment/provider evidence checkpoint.

`docs/BRANCH_STATUS.md` controls branch lifecycle.

## Start/resume control files

### `README.md`
Repository entry point and read sequence.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. Routine engineering is delegated; owner escalation is for real consequential decisions/gates.

### `docs/checkpoints/LATEST.md`
Exact practical resume pointer and current supersession/correction rules.

### `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md`
Current Wave 4 implementation truth: Android Descope session-manager integration, real `HostedAccessTokenProvider` adapter, automated validation, owner physical login/restart/logout proof, debug-harness boundary and exact hosted account/campaign bootstrap continuation.

### `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`
Hosted DEV environment truth: real Neon/Descope/Cloudflare resources, migration/auth/persistence/runtime evidence, local-development caveats and security residuals.

### `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`
Controlling owner policy for:

- external-service budget = **USD $0** unless explicitly changed;
- free-tier billing/overage/hard-cap evaluation;
- provider revalidation;
- intentional public GitHub repository visibility;
- strict secret hygiene;
- owner-facing guidance style for a technically oriented power user who is not a professional developer.

### `docs/recovery/PROJECT_RECOVERY_PROMPT.md`
Reusable whole-project fresh-chat/lost-chat recovery prompt. It must reconstruct the full project rather than merely the last provider/auth task.

### `docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md`
Historical provider-boundary checkpoint capturing the state immediately before real provider activation. Its old `next` instructions are superseded.

### `docs/PROJECT_STATE.md`
Current global implementation/product state.

### `docs/BRANCH_STATUS.md`
Canonical branch lifecycle map and current normal resume rule.

### `docs/ROADMAP.md`
Integrated implementation waves and ordering.

### `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`
Provider-neutral engineering handoff/contracts. Where it still describes provider creation/session wiring as pending, newer checkpoints control the current operational state.

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
- `docs/ARCHITECTURE.md` — integrated architecture; older exact-resume text may be historical where superseded by `LATEST`;
- `docs/TESTING.md` — verification strategy/evidence;
- `docs/TEST_DEVICES.md` — owner-confirmed physical devices.

Historical checkpoints remain evidence; old `next` instructions may be superseded by current `LATEST.md`.

## Current implemented areas

### `shared/`
Kotlin Multiplatform domain/persistence/networking/sync foundation with SQLDelight, kotlinx.serialization and Ktor. Includes stable identities/revisions, hosted transport, durable outbox, campaign lifecycle sync and hosted PC snapshot reconciliation/conflict logic.

### `androidApp/`
Mature Player Android runtime protected by permanent CI guards. The real Descope remembered-session edge and `HostedAccessTokenProvider` adapter are now integrated and physically verified. A debug-only hosted-auth harness exists for DEV verification; owner-facing hosted account/campaign bootstrap is the next active integration package.

### `desktopApp/`
Compose Multiplatform Desktop foundation. Full approved DM Desktop workbench remains protected integrated-MVP work after Player <-> Server contracts are proven end-to-end.

### `backend/`
TypeScript Cloudflare Worker/API with `/v1` hosted routes, Descope/JWKS verification, application-owned authorization, campaign/membership lifecycle and PC snapshot behavior. The DEV Worker is deployed at the public workers.dev endpoint recorded in the hosted completion checkpoint.

### `database/`
Explicit hosted PostgreSQL migration/contracts validated in CI and additionally exercised against the real Neon DEV database during provider activation.

### `scripts/`
Permanent Player guard scripts exercised by Scaffold CI.

### `assets/character-sheets/templates/`
PC Sheet PDF visual authorities; not the canonical PC model.

## Current integrated implementation position

Wave 2 Shared Integrated-MVP Spine is complete. Provider-neutral hosted foundation is integrated through PR #25. The first real hosted DEV environment is activated and verified.

Wave 4 is now active. Its Android remembered-session/token edge is complete through PR #30 (`bf5f843066a7c2f8674a4577918156e8a8d2c139`; post-merge Actions `35020281492` SUCCESS), including owner physical proof of login, restart/session reuse and logout/session clearing.

The next meaningful package is **owner-facing hosted account/campaign bootstrap**, using the already active Cloudflare + Neon + Descope DEV resources and the already-integrated Android session edge.

Do not restart provider activation or Android session acquisition. Do not create speculative parallel infrastructure.

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
4. `docs/PROJECT_STATE.md` controls current global state except where a newer specific decision/checkpoint explicitly supersedes stale prose;
5. `docs/checkpoints/LATEST.md` controls the practical resume point;
6. detailed decisions/checkpoints control their specific approved facts;
7. historical checkpoints remain evidence only;
8. surface material contradictions instead of guessing.
