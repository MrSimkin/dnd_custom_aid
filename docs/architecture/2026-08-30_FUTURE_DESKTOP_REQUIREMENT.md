# Future Native Desktop Product Requirement

**Date:** 2026-08-30  
**Status:** Approved product requirement / Phase 2 evaluation input  
**Architecture status:** Not selected  

## Owner direction

A **true native desktop application** is an expected future product feature. It is not part of the MVP, but it should be treated as a real planned evolution rather than a merely hypothetical possibility.

The expected long-term desktop direction includes both:

- a web-capable desktop administration surface; and
- a true native desktop application in a later product phase.

The MVP may still use a local-browser/localhost desktop administration surface if that is later selected during architecture evaluation. A future hosted web version and a future native desktop application should be able to coexist if the chosen architecture supports that cleanly.

## Architecture consequence without selecting architecture

Phase 2 architecture alternatives must be evaluated against the fact that multiple client surfaces are expected over time: Android, local/hosted web administration, and a later true native desktop client.

This does **not** approve or select:

- an API protocol or style (REST, GraphQL, RPC, etc.);
- a backend framework/provider;
- a desktop framework/language;
- a web framework;
- a database/provider;
- an authentication implementation;
- any specific deployment topology.

A client-independent service/API boundary is an important candidate architectural property because it could allow multiple clients to share the same domain/data/services without duplicating the backend. Whether that boundary is implemented as REST, GraphQL, RPC, another protocol, or a combination remains part of architecture evaluation.

## Evaluation rule

Do not choose an MVP architecture that unnecessarily couples durable domain/backend behavior to one browser UI or makes a later native desktop client require replacing the shared backend/domain model without a strong reason.
