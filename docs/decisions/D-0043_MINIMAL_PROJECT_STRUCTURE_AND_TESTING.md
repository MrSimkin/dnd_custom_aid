# D-0043 — Minimal project structure, backend language, testing and CI

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The initial implementation will use a deliberately small project structure suitable for a personal/small-scale application. The goal is to keep the repository understandable to the owner and to AI/coding agents without either mixing everything together or creating enterprise-style module sprawl.

## Initial code areas

The scaffold should contain the equivalent of these few clear areas:

- **shared** — Kotlin code genuinely shared by Android and desktop, such as domain models/rules, SQLDelight persistence, synchronization/reconciliation logic, and shared network contracts where useful;
- **androidApp** — Android-specific application and Jetpack Compose UI;
- **desktopApp** — desktop-specific application and Compose Multiplatform Desktop UI;
- **backend** — Cloudflare Worker/API code;
- **database** — PostgreSQL schema/migrations and related data-loading scripts where appropriate.

Exact Gradle module names or folder names may vary during scaffolding if a standard tool-generated structure makes a small adjustment sensible. The architectural boundary matters more than the literal names.

## Shared-code boundary

Android and desktop should share logic only where it genuinely reduces duplication. They are **not** required to share UI, screen structure, navigation or feature parity.

In particular, synchronization rules, conflict/revision handling, local persistence behavior and combat-authority rules are good candidates for shared Kotlin code. Android and desktop presentation remain independently designed for their actual use cases.

## Backend implementation language

Cloudflare Worker/backend code will use **TypeScript**, following Cloudflare's normal supported runtime/tooling rather than forcing Kotlin onto the backend.

The resulting implementation languages are intentionally straightforward:

- Android/Desktop: Kotlin;
- Cloudflare backend: TypeScript;
- relational database/schema work: SQL.

## Testing scope

Automated tests should initially concentrate on failures that would materially hurt this project, especially:

- shared domain and synchronization/reconciliation behavior;
- DM live-combat authority rules;
- stale-revision/conflict and idempotent-mutation behavior;
- SQLDelight schema/migration correctness;
- consequential backend authentication/authorization and synchronization behavior.

Android/desktop visual quality and normal UX should rely heavily on practical manual testing on the actual relevant devices rather than a large speculative automated UI-testing system.

## CI

Use one simple **GitHub Actions** workflow that, on relevant pushes/pull requests, performs the practical build/test checks for the Kotlin applications/shared code and the TypeScript Worker backend.

CI is a safety check only. It does not need to deploy production automatically.

## Explicit non-requirements

The initial project does not require:

- coverage-percentage gates;
- SonarQube or similar enterprise quality platforms;
- emulator/device farms or large Android API matrices;
- staging infrastructure merely for process formality;
- automatic production deployment;
- automated installer/release pipelines before they provide real value;
- enterprise Clean Architecture/module proliferation;
- extensive UI screenshot testing;
- generalized infrastructure for hypothetical future scale.

Add modules, tests or automation later only when concrete project complexity or observed risk justifies them under C-0009.

## Architecture gate consequence

With D-0034 through D-0043 approved, the consequential architecture questions identified by D-0009 are sufficiently resolved to begin implementation scaffolding. Remaining low-level/reversible implementation details may be selected during scaffolding under D-0008/C-0003 as appropriate, while genuinely new consequential choices must still return to the owner.

This closes the Phase 2 architecture-selection gate for the initial scaffold. D-0009 should be treated as resolved/Approved; chronological-log consolidation may update its older Pending wording without reopening the decision.

> Safety checkpoint note: this decision is stored as a dedicated decision file on the active architecture branch and should be consolidated into `docs/DECISIONS.md` before the branch is merged.