# A-0008 — Hosted durable database engine: PostgreSQL

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

## Decision

The shared hosted durable relational database engine will be **PostgreSQL**.

This decision selects the database engine, not the hosting/backend provider. It does **not** select Supabase, Neon, another PostgreSQL host, an authentication provider, API protocol/style, backend runtime, ORM/query layer, realtime transport, or client access strategy.

## Rationale

PostgreSQL is a strong fit for the approved domain because the application contains substantial relational data and integrity requirements: users, campaign memberships, campaign-scoped roles/permissions, characters, ownership/control relationships, reusable content relationships, encounters, audit history, invitations/moderation state, and synchronized combat metadata.

PostgreSQL also provides mature transactional behavior, constraints, JSON support where selective flexibility is useful, and a broad hosting/tooling ecosystem. Selecting PostgreSQL preserves the ability to compare multiple PostgreSQL hosting providers separately.

The owner also has strong practical SQL/PostgreSQL experience. This is a legitimate supporting factor because it reduces inspection, debugging, data-analysis, migration-review and operational friction. It is **not** the sole rationale for the choice.

## Portability / reversibility

This is a significant but revisable architecture decision under `docs/GOVERNANCE.md`.

- Moving between PostgreSQL hosting providers should remain a comparatively manageable migration target if provider-specific coupling is controlled.
- Moving later from PostgreSQL to a fundamentally different database technology is possible but may require substantial schema/query/constraint/backend rewrites.
- Repository-owned schema migrations and data-model definitions should therefore avoid unnecessary provider lock-in where practical.

## Consequences

- Hosted durable relational data is designed for PostgreSQL semantics.
- Future provider evaluation must compare how candidate services expose PostgreSQL, authentication/authorization, server-side logic, realtime/public projections, backups/export, migration, pricing/free-tier behavior and operational burden.
- The Android Room schema remains independent; A-0004 does not require Room and PostgreSQL schemas to mirror each other.
- The next architecture decision is the hosted backend/provider/service approach around PostgreSQL, including authentication/authorization and client-facing service boundaries.
