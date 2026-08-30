# A-0009 — Initial hosted application platform: Supabase

**Status:** Proposed / NOT approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Session status:** Discussion paused before owner decision

## Proposal

Use **Supabase** as the initial hosted application platform around the already approved PostgreSQL database engine (A-0008).

The proposal would use Supabase selectively for managed capabilities that the project already requires or is likely to require in MVP infrastructure, including:

- PostgreSQL hosting;
- authentication/account recovery;
- PostgreSQL-integrated authorization/RLS where appropriate;
- trusted server-side operations/functions for security-sensitive or authority-sensitive workflows;
- realtime delivery where appropriate, especially player-facing synchronized projections.

This proposal does **not** authorize implementation and must not be treated as approved merely because it is documented here.

## Why Supabase was recommended

Compared with a PostgreSQL-first host such as Neon, Supabase currently bundles more of the application infrastructure the project needs into one managed platform. This can reduce the number of separately hosted/maintained components for a personal-scale, AI-assisted, $0-target project.

The project already requires or expects:

- email-based login/recovery;
- multiple independent clients (Android, web, later native desktop);
- campaign-scoped authorization and moderation boundaries;
- trusted handling of invitations/account/campaign operations;
- authoritative DM combat synchronization operations that must not be unrestricted table writes;
- player-facing synchronized public combat projections.

An integrated platform can reduce operational burden compared with assembling database hosting, auth, backend runtime and realtime infrastructure independently.

## Required coupling boundary if approved

Supabase must **not** become the application's domain model.

Clients should depend on application/domain repositories or services rather than scattering provider-specific table calls and concepts throughout UI/domain code.

Critical live-combat authority/reconciliation operations under A-0005/A-0006 must go through explicit trusted service operations that enforce authority, operation identity, ordering/revision and repair semantics. They must not rely on unrestricted direct client mutation of hosted combat tables.

This containment is intended to reduce later provider-migration cost.

## Important operational limitation identified

The Supabase Free plan was verified during the 2026-08-30 architecture discussion as having inactivity-pausing behavior for sufficiently inactive free projects. A paused project may require the owner to manually resume it from the Supabase dashboard before shared online functionality is available again.

This matters for a personal D&D application because real use may naturally have gaps of one or more weeks between sessions.

If A-0009 is reconsidered later, current vendor documentation/pricing must be re-checked because free-tier behavior can change.

## Neon alternative considered

**Neon** remains a serious alternative around the approved PostgreSQL engine.

Advantages identified during the comparison:

- PostgreSQL-first architecture;
- strong scale-to-zero/idle behavior suited to infrequently used personal projects;
- comparatively clean database-provider role;
- improving managed authentication/data-service capabilities.

Trade-off identified:

- for this project, choosing Neon would more likely require assembling/hosting more surrounding infrastructure ourselves (client-facing API/backend operations, auth integration details, realtime/public projection delivery, and related operational pieces) than the Supabase proposal.

This may offer cleaner separation but increases MVP architecture and maintenance burden.

## Reversibility / migration cost

If approved, Supabase would be a significant but revisable decision under `docs/GOVERNANCE.md`.

Relatively portable pieces:

- PostgreSQL tables/data;
- ordinary PostgreSQL schema/SQL where provider-specific extensions are avoided;
- domain concepts kept outside provider SDKs.

Potentially more expensive pieces to migrate:

- authentication/user-account integration;
- RLS/provider-auth assumptions;
- realtime subscriptions/channels;
- server/edge functions;
- provider-specific client SDK integration.

Therefore, if approved later, repository-owned migrations and explicit provider adapters/service boundaries should be preferred over unnecessary provider lock-in.

## Resume point

The owner stopped the session before approving or rejecting this proposal.

On resume:

1. treat **PostgreSQL (A-0008) as approved**;
2. treat **Supabase (A-0009) as proposed only**;
3. do not implement or assume Supabase;
4. briefly re-check current Supabase and Neon official documentation/free-tier behavior if enough time has passed for provider details to change;
5. continue the owner-facing decision from the Supabase-vs-Neon/other-hosted-foundation comparison, including the inactivity-pause trade-off and migration/coupling implications.