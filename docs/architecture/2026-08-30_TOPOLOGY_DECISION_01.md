# Phase 2 Architecture Decision 01 — Multi-client target shape

**Date:** 2026-08-30  
**Status:** Approved by project owner  
**Working branch:** `architecture/phase2-topology`  
**Scope:** Overall application topology / client relationship only

## Approved decision

The target product shape is a **multi-client architecture** in which the major client surfaces are independent applications around the same durable shared domain/backend rather than one client being treated as the implementation foundation for the others.

Expected client surfaces over the product lifetime are:

1. a dedicated Android client, which remains the primary live/table surface;
2. a web-capable desktop administration client;
3. a **true native desktop client**, which is an expected future product feature rather than a merely hypothetical possibility.

The MVP does not need to implement all three. The future native desktop client is outside MVP, and the exact delivery form of the MVP desktop administration surface remains to be selected later in Phase 2.

## Architectural intent

- The durable campaign/domain/backend must not be conceptually owned by the web UI, Android UI, or future native desktop UI.
- Replacing or substantially rewriting one client should not inherently require replacing the shared domain/backend.
- Android, web and future native desktop may have intentionally different UX and capabilities; maximizing UI-code reuse is **not** itself an architecture goal.
- Shared contracts/services between clients and the durable backend are expected to be explicit enough to support multiple clients coherently.
- The architecture should allow the web client and future true native desktop client to coexist rather than assuming one must replace the other.

## Explicitly not decided by this decision

This decision does **not** select:

- Android language/framework/UI toolkit;
- web framework;
- native desktop framework/language;
- local-web vs hosted-web MVP delivery;
- REST, GraphQL, RPC, WebSockets, generated SDKs, or any other API/protocol style;
- backend framework or hosting provider;
- database/provider;
- authentication provider;
- local persistence technology;
- synchronization implementation;
- PDF technology;
- SRD retrieval/AI technology.

A client-independent API/service boundary remains a strong candidate architectural principle, but its exact form is still part of the ongoing evaluation.

## Rationale

The approved product surfaces are intentionally asymmetric. Android must prioritize phone/tablet UX and local-first live combat resilience. Desktop administration prioritizes large-screen data entry and preparation. A future native desktop application is expected as a genuine native client. Treating these as independent clients around shared durable services preserves freedom to optimize each surface without forcing feature parity or coupling the product to one presentation technology.

## Next architecture question

Continue Phase 2 by determining the **MVP desktop delivery model and relationship to the shared backend**, including whether the web administration client runs locally via `localhost`, is hosted, or supports staged evolution from local-web to hosted-web, while preserving this multi-client target shape.
