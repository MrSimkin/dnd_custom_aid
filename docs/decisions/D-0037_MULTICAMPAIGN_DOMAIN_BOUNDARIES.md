# D-0037 — Multicampaign domain boundaries use one shared relational model with explicit campaign scope

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The application will use one shared relational PostgreSQL domain model with explicit application-global and campaign-scoped relationships. Separate databases, separate PostgreSQL schemas per campaign, provider-specific tenant models and giant campaign blobs are not selected.

## Core boundaries

- A user exists once globally inside the application.
- External Descope identity remains separate from the internal application user identity under D-0034.
- Campaign participation is represented through campaign-membership/role relationships; role is not a permanent property of the global user.
- MVP may enforce one active DM per campaign as a business rule, but the relational model must not hard-code a single `dm_user_id` when a general membership/role model is straightforward.
- Campaign moderation state remains separate from application-global account administration/freeze state.

## Characters

- A PC-style character belongs to exactly one campaign.
- Character existence is separate from ownership and current control.
- Ownership and current control may be absent or change without deleting/recreating the character.
- Copying a character into another campaign creates a new independent campaign character rather than a live cross-campaign shared record.
- Grouped character audit/history belongs to the character and records the acting user; history survives changes in ownership/control and membership continuity.

## Reusable and official content

- Mutable reusable personal NPC/creature/rule-library content is distinct from campaign-specific content.
- Using mutable personal-library content in a campaign normally creates an independent campaign copy that may retain provenance to its source; later edits to the personal master must not silently rewrite an existing campaign.
- Immutable/versioned official SRD material may be referenced canonically where appropriate. Customizing official content creates an independent campaign/personal copy while retaining source/version provenance.

## Encounters and live state

- Saved encounter templates and live encounters are distinct entities under D-0031; starting a saved encounter creates an independent live working copy.
- Durable character-sheet state, grouped mechanical audit/history and live-combat working state remain distinct domains.
- Live combat state must not silently mutate the durable character sheet merely because the same PC participates in combat.

## Persistence and synchronization consequences

- Mutable domain entities should use stable globally unique identities suitable for Android, desktop and hosted synchronization without depending on Neon-local numeric identifiers.
- Relational campaign ownership/scope should remain explicit enough that clients can synchronize/cache selected campaigns without treating the entire account as one undifferentiated blob.
- Exact revision/version fields, deletion/tombstone strategy, outbox design, conflict rules, local database technology and reconciliation behavior remain separate architecture decisions.

## Rationale

The owner explicitly confirmed that this model matches an already-established design preference: normal relational SQL boundaries, explicit ownership and foreign-key relationships, clear campaign isolation, and avoidance of hidden/provider-specific tenancy abstractions. PostgreSQL and SQL are intentionally treated as the natural domain-storage model for this project rather than an implementation inconvenience to abstract away.

This resolves the multicampaign domain/data-model-boundaries portion of D-0009. D-0009 remains Pending until the remaining consequential architecture choices are approved.

> Safety checkpoint note: this decision is stored as a dedicated decision file on the active architecture branch so it is durable immediately. It should be consolidated into the chronological `docs/DECISIONS.md` log before the architecture branch is merged.