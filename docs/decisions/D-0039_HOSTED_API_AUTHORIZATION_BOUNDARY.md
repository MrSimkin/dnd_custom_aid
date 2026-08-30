# D-0039 — Hosted access uses Cloudflare as the gateway with proportional personal-scale security

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

Native Android and desktop clients do **not** connect directly to Neon and do not hold PostgreSQL credentials. Hosted application reads, writes and synchronization flow through project-owned Cloudflare backend/API endpoints, which validate the Descope identity/session and map it to the application's internal user identity before applying domain authorization and synchronization rules.

## Minimum required boundary

- Cloudflare is the remote application gateway between native clients and Neon PostgreSQL.
- Descope is used for authentication only; campaign/application authorization remains project-owned.
- Database credentials remain server-side and are never distributed to native clients.
- PostgreSQL constraints and foreign keys enforce ordinary relational integrity.
- Offline outbox mutations, revisions and DM-authoritative combat synchronization continue to follow D-0038.

## Proportionality constraint

This is a personal, deliberately small-scale project, not an enterprise or commercial SaaS product. Security and authorization implementation must therefore remain **minimum-sufficient and conservative** rather than accumulating enterprise-style layers preemptively.

- Do not add elaborate database-role hierarchies, duplicated authorization systems, complex RLS policy networks, extensive audit/security infrastructure or other defense-in-depth machinery unless a concrete project risk or implementation need justifies it.
- PostgreSQL Row Level Security may be introduced selectively if it materially simplifies or protects a real boundary, but it is **not required as a blanket second authorization system** for MVP.
- A simple least-privilege runtime database credential is preferred where practical; do not over-design database privilege separation for hypothetical operators or organizational roles that do not exist.
- Application-administrator and campaign-DM authority remain conceptually separate under existing product decisions, but their implementation should be as simple as safely possible at the project's actual scale.

## Rationale

The owner approved the Cloudflare-gateway model but explicitly reiterated that the project must not drift into enterprise-grade architecture. Future agents must evaluate safeguards by concrete value versus maintenance/implementation burden, not by whether they would be standard in a large commercial system.

This resolves the remaining hosted API/data-access and authorization-enforcement portion of D-0009. D-0009 remains Pending until the remaining consequential architecture choices are approved.

> Safety checkpoint note: this decision is stored as a dedicated decision file on the active architecture branch and should be consolidated into the chronological `docs/DECISIONS.md` log before merge.
