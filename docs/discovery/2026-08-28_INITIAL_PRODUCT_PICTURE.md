# Initial Product Picture — Discovery Input

**Date:** 2026-08-28  
**Status:** Provisional discovery input — NOT an approved specification  
**Source:** Owner ideation during Phase 1 product discovery

## Purpose of this file

This file preserves the owner's initial picture of the product without converting brainstorming into requirements. Every item below must be discussed, clarified, challenged where useful, and explicitly confirmed before it becomes approved product scope.

A future chat/agent must treat this file as **discovery material**, not as permission to implement features.

## Owner's current picture

### Product language

- User-facing application is intended to be fully in Spanish.
- Whether this applies identically to a possible Windows companion application should be confirmed when that idea is evaluated.

### Player experience

The owner currently imagines that players could:

- see their character sheet;
- update their character sheet;
- have those changes visible/auditable by the DM so the DM can verify that no inappropriate changes were made;
- export the character sheet to PDF;
- use one of several existing character-sheet formats/templates that the owner already has.

### Character change oversight

The owner's goal is for the DM to be able to verify player-made character-sheet changes and detect cheating or unauthorized changes.

This implies a likely need for some form of change history/audit trail, but the exact behavior is **not decided**. Questions include:

- Does the DM simply inspect history?
- Should some changes trigger alerts?
- Should some or all changes require DM approval?
- What information should an audit entry contain (field, previous value, new value, timestamp, user, reason/comment, etc.)?
- Can the DM edit/correct a player sheet directly, and if so how is that recorded?

### Online data

The owner currently expects shared data to be stored online using a free or no-cost hosted database/backend option if practical.

The specific provider and technology must **not** be chosen during product discovery merely because a free tier exists. It should be evaluated later against the approved design, including authentication, audit history, security, synchronization, backups, limits, maintenance burden, and future cost.

### DM experience — primarily tablet

The owner currently imagines the DM experience as tablet-first and including:

- access to every character sheet in the campaign;
- a quick-access view/tab containing important PC statistics so the DM does not need to ask players repeatedly;
- a battle/initiative tracker showing whose turn it is;
- convenient access to monster stat blocks during combat, especially when resolving monster/NPC turns.

The exact set of quick-access PC statistics is not yet defined.

### Accounts and campaigns

The owner currently imagines:

- every user has an account;
- authentication must be reasonably secure for the intended use;
- a DM creates a campaign;
- player characters are enrolled/associated with that campaign.

Open questions include account identity, invitations/join codes, multiple campaigns per user, multiple characters per user/campaign, DM/co-DM roles, player permissions, leaving/transferring campaigns, and recovery of account access.

### Possible Windows desktop application

The owner has introduced a possible Windows desktop companion application.

Current idea:

- Windows desktop;
- oriented toward general/basic campaign management;
- examples mentioned: organizing PCs and assignments.

This is an **idea to evaluate**, not approved platform scope yet. We need to determine whether Windows solves a real workflow better than the tablet/phone app and whether its benefits justify the additional product/maintenance surface.

### D&D SRD reference

The owner wants direct in-app consultation of the D&D rules reference for both the older fifth-edition rules and the revised 2024-era fifth-edition rules (colloquially referred to in discussion as D&D 5.0 and 5.5).

Current official SRD naming should be used during design/implementation discussions:

- SRD 5.1 corresponds to the earlier fifth-edition rules foundation;
- SRD 5.2.1 corresponds to the revised 2024-era fifth-edition rules foundation.

The owner wants the app itself to be Spanish-only, so Spanish SRD availability/licensing and how the content is stored, searched, attributed, updated, and separated by rules version must be evaluated carefully.

No implementation approach is approved yet (bundled local data, server-hosted structured content, direct web consultation, import pipeline, etc.).

## Discovery behavior explicitly requested by the owner

The owner is intentionally providing ideas in an unorganized, exploratory form and expects the agent/chat to actively help shape them.

During Phase 1, the agent must:

- ask clarifying questions;
- reorganize raw ideas into coherent product concepts;
- identify hidden consequences and dependencies;
- present realistic alternatives;
- recommend options and explain why;
- suggest useful ideas the owner may not have considered;
- challenge weak or contradictory ideas constructively;
- distinguish brainstorming from approved requirements;
- ask for explicit confirmation before promoting a material idea into approved scope;
- persist important discussion outcomes and unresolved questions in Git.

## Important design themes already visible

These are observations for discussion, not decisions:

1. **Shared authoritative state:** campaign, users, characters and DM visibility imply synchronized shared data.
2. **Auditability:** anti-cheating oversight likely requires immutable or tamper-resistant change history rather than only the current sheet values.
3. **Role-aware UX:** player and DM experiences are substantially different even though they operate on shared data.
4. **Tablet-first DM workflow:** combat and campaign oversight may benefit from information-dense layouts distinct from phone layouts.
5. **Character data vs PDF layout:** it may be beneficial to treat character data as structured information independent of any single PDF sheet format, then render/export into selected templates.
6. **Rules-version coexistence:** supporting both SRD generations likely requires explicit version tagging so 5.1 and 5.2.1 content/rules are not mixed silently.
7. **Cross-platform implications:** a Windows companion could affect later technology choices, but stack selection remains deferred until product/design scope is clear.

## Questions to work through next

The next discovery conversation should prioritize user workflows and authority rules before technical implementation. In particular:

- What exactly constitutes a character-sheet change that the DM must be able to audit?
- Which changes, if any, need DM approval?
- What are the most important PC statistics for the DM quick view?
- How should campaign joining/enrollment work from the user's perspective?
- What does the owner mean by assignments in Windows campaign management?
- Is the Windows app essential, desirable, or merely an option worth evaluating?
- What does direct SRD consultation need to feel like: searchable encyclopedia, contextual links from sheets/stat blocks, both, or something else?
