# D-0036 — DM desktop administration uses Kotlin + Compose Multiplatform Desktop

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The DM desktop/laptop preparation and administration companion will be a native desktop application implemented in **Kotlin** with **Compose Multiplatform Desktop**.

## Rationale

The owner strongly prefers avoiding unnecessary continuous-online dependency. Native packaging, installer/update handling and desktop distribution overhead are explicitly accepted and are **not considered material disadvantages** for this personal-scale project.

A browser-only administration surface was considered and is not selected. The owner values the ability for the desktop application to support meaningful local/offline operation more highly than the deployment simplicity of a web-only frontend.

## Consequences

- Android remains native Kotlin + Jetpack Compose under D-0035; this decision does not turn the Android application into a generic cross-platform UI.
- Android and desktop may share Kotlin domain/business/networking/synchronization code selectively where that reduces duplication without forcing UI parity or inappropriate coupling.
- The desktop UI remains purpose-built for keyboard/mouse/large-screen DM preparation and administration rather than copying the Android UI.
- The desktop application should permit meaningful local/offline operation rather than unnecessarily requiring continuous connectivity.
- **Native does not itself guarantee offline behavior.** Exact desktop local persistence technology, offline data scope, synchronization, conflict handling and reconciliation remain separate architecture decisions.
- Desktop combat tracking, player desktop support and Android/desktop full feature parity remain outside MVP unless separately approved.
- Descope remains the selected authentication provider. Native desktop authentication may use standards-based browser/OIDC/OAuth flows as appropriate; exact implementation remains an implementation-level decision unless it introduces a consequential new dependency.
- Cloudflare remains selected for suitable backend/API/object-storage/realtime infrastructure under D-0034, but no desktop web frontend hosting is required by this decision.

## Alternatives considered

- Browser-based web administration: technically strong and simple to deploy, but rejected because continuous-online dependency is undesirable to the owner and deployment/update overhead is not a meaningful concern.
- Tauri/web-shell desktop: adds a native packaging layer without enough benefit over a native Kotlin desktop application for this project.
- Local web application: adds local-server/installation complexity without enough advantage over Compose Multiplatform Desktop.

This resolves the desktop/laptop administration delivery-form portion of D-0009. D-0009 remains Pending until the remaining consequential architecture choices are approved.

> Safety checkpoint note: this decision is stored as a dedicated decision file on the active architecture branch so it is durable immediately. It should also be consolidated into the chronological `docs/DECISIONS.md` log before the architecture branch is merged.