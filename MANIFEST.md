# Repository Manifest

This file maps authoritative project-control files and implemented areas so a fresh human or AI can resume without branch archaeology.

## Current authority model

The owner has authorized integrated-MVP implementation. **`main` is the single normal integrated-MVP trunk.** Historical Player/convergence lines are evidence only.

Verified implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Actions `34985799585` — SUCCESS. PR #26 later consolidated the provider boundary; post-merge Actions `34986965813` — SUCCESS.

`docs/BRANCH_STATUS.md` controls branch lifecycle. `docs/checkpoints/LATEST.md` controls the practical resume point.

## Start/resume control files

### `README.md`
Repository entry point and read sequence.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. Routine engineering is delegated; owner escalation is for real consequential decisions/gates.

### `docs/checkpoints/LATEST.md`
Exact practical resume pointer and current supersession/correction rules.

### `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`
Controlling owner policy for:

- external-service budget = **USD $0** unless explicitly changed;
- free-tier billing/overage/hard-cap evaluation;
- September 2026 provider revalidation;
- intentional public GitHub repository visibility;
- strict secret hygiene;
- owner-facing guidance style for a technically oriented power user who is not a professional developer.

### `docs/recovery/PROJECT_RECOVERY_PROMPT.md`
Reusable fresh-chat/lost-chat recovery prompt carrying current resume rules, `$0` policy, security clarification, owner guidance style and provider boundary.

### `docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md`
Provider-neutral hosted/sync implementation checkpoint and first real external-provider activation boundary. Where its old repository-visibility paragraph conflicts with D-0075/LATEST, D-0075 controls.

### `docs/PROJECT_STATE.md`
Global implementation/product state. Older visibility-discrepancy wording is superseded by D-0075.

### `docs/BRANCH_STATUS.md`
Canonical branch lifecycle map. Older visibility-discrepancy wording is superseded by D-0075.

### `docs/ROADMAP.md`
Integrated implementation waves and ordering.

### `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`
Engineering handoff. Older visibility-discrepancy or assumed-R2 wording is superseded by D-0075 where it conflicts.

### `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md`
Prepared safety/activation handoff for the first Cloudflare + Neon + Descope development environment, now hardened around the controlling `$0` policy and owner-guidance contract.

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
- `docs/ARCHITECTURE.md` — integrated architecture;
- `docs/TESTING.md` — verification strategy/evidence;
- `docs/TEST_DEVICES.md` — owner-confirmed physical devices.

Historical checkpoints remain evidence; old `next` instructions may be superseded by current `LATEST.md`.

## Current implemented areas

### `shared/`
Kotlin Multiplatform domain/persistence/networking/sync foundation with SQLDelight, kotlinx.serialization and Ktor. Includes stable identities/revisions, hosted transport, durable outbox, campaign lifecycle sync and hosted PC snapshot reconciliation/conflict logic.

### `androidApp/`
Mature Player Android runtime protected by permanent CI guards. Owner-facing composition remains intentionally local-only at the current provider boundary; remembered hosted authentication/session and real campaign/PC hosted wiring are the next external-environment-dependent package.

### `desktopApp/`
Compose Multiplatform Desktop foundation. Full approved DM Desktop workbench remains protected integrated-MVP work after Player↔Server contracts are proven end-to-end.

### `backend/`
TypeScript Cloudflare Worker/API with `/v1` hosted routes, token-verifier boundaries, application-owned authorization, campaign/membership lifecycle and PC snapshot behavior.

### `database/`
Explicit hosted PostgreSQL migrations/contracts validated in CI.

### `scripts/`
Permanent Player guard scripts exercised by Scaffold CI.

### `assets/character-sheets/templates/`
PC Sheet PDF visual authorities; not the canonical PC model.

## Current integrated implementation position

Wave 2 Shared Integrated-MVP Spine is complete. Provider-neutral Wave 3 hosted work is integrated through PR #25.

The next meaningful package is **real authenticated Player↔Server development integration**, requiring owner-controlled development/test Cloudflare + Neon + Descope resources that satisfy D-0075's `$0` policy.

Provider direction after revalidation:

- Cloudflare Workers — KEEP, early free-tier CPU/runtime proof required;
- Neon PostgreSQL — KEEP;
- Descope — KEEP, current Free-plan payment/region confirmation required;
- Workers AI — KEEP while safely usable at `$0`;
- object storage — provider DEFERRED until asset integration.

Do not create speculative parallel infrastructure merely to defer the provider gate.

## Security / repository visibility

The GitHub repository is intentionally **public**. This is expected configuration, not a security discrepancy.

Never commit provider credentials, database credentials, access/session tokens, private keys or other secrets.

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
