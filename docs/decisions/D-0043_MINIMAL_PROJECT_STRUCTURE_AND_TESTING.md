# D-0043 — Minimal project structure, backend language, testing and CI

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Clarified:** 2026-08-30 by the pre-main proportionality audit

The initial implementation uses a deliberately small project structure suitable for a personal/small-scale application. The goal is to keep the repository understandable to the owner and to AI/coding agents without either mixing everything together or creating enterprise-style module sprawl.

## Initial code areas

The scaffold should contain the equivalent of these few clear areas:

- **shared** — Kotlin code genuinely shared by Android and desktop, such as domain models/rules, SQLDelight persistence, synchronization/reconciliation logic, and shared network contracts where useful;
- **androidApp** — Android-specific application and Jetpack Compose UI;
- **desktopApp** — desktop-specific application and Compose Multiplatform Desktop UI;
- **backend** — Cloudflare Worker/API code;
- **database** — PostgreSQL schema/migrations and related data-loading scripts where appropriate.

Exact Gradle module names or folder names may vary during scaffolding if a standard tool-generated structure makes a small adjustment sensible. The architectural boundary matters more than the literal names.

## Shared-code boundary

Android and desktop share logic only where it genuinely reduces duplication. They are **not** required to share UI, screen structure, navigation or feature parity.

Synchronization rules, local persistence behavior and combat-authority rules are good candidates for shared Kotlin code. Android and desktop presentation remain independently designed for their actual use cases.

## Backend implementation language

Cloudflare Worker/backend code uses **TypeScript**, following Cloudflare's normal supported runtime/tooling rather than forcing Kotlin onto the backend.

The implementation languages are intentionally straightforward:

- Android/Desktop: Kotlin;
- Cloudflare backend: TypeScript;
- relational database/schema work: SQL.

## Initial infrastructure activation

Selecting Cloudflare as the complementary infrastructure provider does **not** mean scaffolding every Cloudflare service.

Initial hosted application infrastructure should be essentially:

**native clients → Cloudflare Worker/API → Neon PostgreSQL**

plus Descope for authentication and Workers AI only for the approved SRD clarification flow when that feature is implemented.

Do **not** create R2 buckets, Durable Objects, WebSocket infrastructure, queues, distributed caches or similar services merely because they are available. Add one only when an implemented feature has a concrete requirement that the simpler Worker/HTTP approach cannot satisfy.

## Provider replaceability

Keep vendor-specific integration code reasonably localized so a provider can be replaced later if needed. This means sensible code organization, **not** building provider factories, generalized adapter frameworks or abstract infrastructure layers for hypothetical future migrations.

## Synchronization scope

D-0038 controls synchronization behavior. In particular:

- Desktop MVP uses local **Save** plus explicit **Sync**, not a continuous background-sync platform.
- DM combat is local-first and uses one authoritative DM device plus an increasing combat sequence for MVP.
- Player offline combat convenience is ephemeral and is never uploaded.
- Ordinary HTTP request/response and simple refresh/polling are preferred before realtime transport.

## Testing scope

Automated tests initially concentrate on failures that would materially hurt this project, especially:

- shared domain and synchronization/reconciliation behavior that actually exists in MVP;
- DM live-combat sequence/authority rules;
- stale-revision and idempotent-mutation behavior;
- SQLDelight schema/migration correctness;
- consequential backend authentication/authorization and synchronization behavior.

Android/desktop visual quality and normal UX rely heavily on practical manual testing on the actual relevant devices rather than a large speculative automated UI-testing system.

## CI

Use one simple **GitHub Actions** workflow that, on relevant pushes/pull requests, performs the practical build/test checks for the Kotlin applications/shared code and the TypeScript Worker backend.

CI is a safety check only. It does not deploy production automatically.

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
- generalized provider-abstraction frameworks;
- a generalized synchronization platform;
- universal offline support for every feature;
- realtime infrastructure before ordinary HTTP proves inadequate;
- generalized infrastructure for hypothetical future scale.

Add modules, tests, services or automation later only when concrete project complexity or observed risk justifies them under C-0009.

## Architecture gate consequence

With D-0034 through D-0043 approved, the consequential architecture questions identified by D-0009 are sufficiently resolved to begin implementation scaffolding after the owner-reviewed merge into canonical `main`. Remaining low-level/reversible implementation details may be selected during scaffolding under D-0008/C-0003 as appropriate, while genuinely new consequential choices must still return to the owner.

This closes the Phase 2 architecture-selection gate for the initial scaffold. D-0009 is resolved/Approved.

> Detail-record note: this file preserves the fuller rationale for D-0043. The chronological authoritative entry is consolidated in `docs/DECISIONS.md`.
