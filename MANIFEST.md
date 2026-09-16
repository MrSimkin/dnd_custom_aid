# Repository Manifest

This file maps authoritative project-control files and implemented areas so a fresh human or AI can resume without branch archaeology.

## Current authority model

The owner has authorized integrated-MVP implementation. **`main` is the sole normal integrated-MVP trunk.** Historical Player/convergence lines are evidence only.

Current practical truth is controlled by:

- `docs/checkpoints/LATEST.md` — exact resume pointer;
- `docs/PROJECT_STATE.md` — current global implementation/product state;
- `docs/BRANCH_STATUS.md` — branch lifecycle;
- the current checkpoint referenced by `LATEST.md` — package-specific implementation/verification evidence.

As of 2026-09-16, Wave 4 is complete. Wave 5 is active. The current draft PR #44 repository implementation is complete/CI-verified and is stopped at explicit real DEV Worker deployment/integration before owner Windows Desktop QA.

## Start/resume control files

### `README.md`
Repository entry point and read sequence.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents, including the external-provider capability boundary.

### `docs/checkpoints/LATEST.md`
Exact practical resume pointer and current supersession rules.

### `docs/PROJECT_STATE.md`
Current global implementation/product state.

### `docs/BRANCH_STATUS.md`
Canonical branch lifecycle map and current resume rule.

### `docs/recovery/PROJECT_RECOVERY_PROMPT.md`
Reusable whole-project fresh-chat/lost-chat recovery prompt.

### `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md`
Reusable prompt for continuing autonomous repository work while stopping cleanly at inaccessible Cloudflare/Descope/Neon/provider actions.

### `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`
Controlling `$0` external-service budget, provider revalidation, intentional public-repository visibility, secret hygiene and owner-guidance contract.

### `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`
Historical evidence for the first real Neon + Descope + Cloudflare DEV activation. Provider activation is complete; do not repeat it.

### `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md`
Historical evidence for the first Android hosted-session integration and owner physical login/restart/logout proof.

### `docs/ROADMAP.md`
Integrated implementation waves and ordering.

### `docs/WORKFLOW.md`
AI-led development/review workflow and external-provider handoff protocol.

### `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`
Provider-neutral engineering handoff/contracts. Older exact-resume prose inside technical baselines is superseded by current state docs.

## Controlling product/architecture decisions

- `D-0071` — integrated Player + Server + DM architecture, authority/sync/recovery/Desktop direction;
- `D-0072` — DM Desktop product and authoring/management surfaces;
- `D-0073` — integrated MVP boundary and implementation governance;
- `D-0074` — cross-surface PC Sheet PDF export definition;
- `D-0075` — zero-budget provider policy, provider revalidation, repository visibility and owner-guidance contract.

Use `docs/DECISIONS_RECENT.md` for navigation.

## Other mandatory/reference docs

- `docs/CONVENTIONS.md` — recurring conventions;
- `docs/PRODUCT.md` — approved product scope;
- `docs/ARCHITECTURE.md` — integrated architecture;
- `docs/TESTING.md` — verification strategy/evidence;
- `docs/TEST_DEVICES.md` — owner-confirmed physical devices.

Historical checkpoints remain evidence; old `next` instructions may be superseded by current `LATEST.md`.

## Current implemented areas

### `shared/`
Kotlin Multiplatform domain/persistence/networking/sync foundation with SQLDelight, kotlinx.serialization and Ktor. Includes stable identities/revisions, hosted transport, durable outbox, campaign lifecycle sync, hosted PC snapshot reconciliation/conflict logic and provider-neutral hosted Campaign Administration clients.

### `androidApp/`
Mature Player Android runtime protected by permanent CI guards. Real Descope remembered-session integration, campaign bootstrap/delivery, PC hosted synchronization/convergence/conflict behavior and Wave 4 authorization QA are integrated for their recorded scope.

### `desktopApp/`
Compose Multiplatform Desktop workbench with persistent local campaign context. Current PR #44 adds Desktop hosted Descope email-OTP/session acquisition, hosted campaign bootstrap, real Campaign Administration roster/moderation consumption, diagnostics and font/theme preview settings.

### `backend/`
TypeScript Cloudflare Worker/API with `/v1` routes, Descope/JWKS verification, application-owned authorization, campaign/membership lifecycle, Campaign Administration and PC snapshot behavior. The historical DEV Worker exists, but new PR #43 Campaign Administration routes must not be described as live until explicit current deployment/integration evidence exists.

### `database/`
Explicit hosted PostgreSQL migrations/contracts validated in CI and previously exercised against the real Neon DEV database. Current Campaign Administration contracts include migration/contract `0006`.

### `scripts/`
Permanent Player guard scripts exercised by Scaffold CI.

### `assets/character-sheets/templates/`
PC Sheet PDF visual authorities; not the canonical PC model.

## Current integrated implementation position

```text
Wave 1 baseline convergence                         COMPLETE
Wave 2 Shared Integrated-MVP Spine                  COMPLETE
Wave 3 hosted foundation / real DEV activation      COMPLETE
Wave 4 Player <-> Server                            COMPLETE
Wave 5 Desktop shell/local campaign                 COMPLETE / owner-QA pass
Wave 5 hosted membership administration core       COMPLETE / merged #43
Wave 5 Desktop hosted Campaign Administration       REPO IMPLEMENTED / CI VERIFIED / PR #44
Real DEV deployment/integration                     NEXT
Owner Windows Desktop QA                            REQUIRED BEFORE MERGE
```

Do not restart completed provider activation, Android session work, Wave 4, PR #42 or PR #43.

## External-provider capability rule

If an agent cannot actually authenticate to a required provider, it must not loop on alternate connection attempts. Finish safe repository preparation, produce one owner-action packet, stop, and resume from non-secret evidence. See `AGENTS.md`, `docs/WORKFLOW.md` and `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md`.

## Security / repository visibility

The GitHub repository is intentionally **public**. Never commit provider/database credentials, API/admin/deployment tokens, access/session tokens, private keys or other secrets.

## Current integrated-MVP boundary

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Protected scope includes Player hosted integration, full DM live Android/Desktop capability, combat authority resume, Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

## Authority rule

If documents conflict:

1. later specific approved decisions control older general prose;
2. `AGENTS.md` controls working governance;
3. `docs/BRANCH_STATUS.md` controls branch lifecycle;
4. `docs/PROJECT_STATE.md` controls current global state;
5. `docs/checkpoints/LATEST.md` controls practical continuation;
6. the current specific checkpoint controls its milestone evidence;
7. historical checkpoints remain evidence only;
8. surface material contradictions instead of guessing.
