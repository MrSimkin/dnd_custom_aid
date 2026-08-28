# Initial Product Picture — Discovery Input

**Date:** 2026-08-28  
**Status:** Historical provisional discovery input — clarified by `2026-08-28_CLARIFICATIONS_01.md`  
**Source:** Owner ideation during Phase 1 product discovery

## Purpose of this file

This file preserves the owner's initial picture of the product without converting brainstorming into requirements. It remains useful as historical rationale.

Confirmed conclusions from the subsequent discussion have been promoted into `docs/PRODUCT.md`, `docs/DECISIONS.md`, and `docs/CONVENTIONS.md`. Where this file differs from those authoritative records, the authoritative records win.

A future chat/agent must treat this file as **historical discovery material**, not as permission to implement features.

## Owner's initial picture

### Product language

- User-facing application is intended to be fully in Spanish.
- Technical project language was not yet decided at this initial stage; it was later clarified as English for source/technical documentation.

### Player experience

The owner initially imagined that players could:

- see their character sheet;
- update their character sheet;
- have those changes visible/auditable by the DM so the DM can verify that no inappropriate changes were made;
- export the character sheet to PDF;
- use one of several existing character-sheet formats/templates that the owner already has.

Later clarification established that physical printed sheets are preferred and the player app is mainly a digital backup/reference/reprint mechanism.

### Character change oversight

The owner's initial goal was for the DM to be able to verify player-made character-sheet changes and detect cheating or unauthorized changes.

This originally left open whether changes would require approval. Later clarification established that player changes take effect without pre-approval and the DM instead has audit, correction and reversal powers.

### Online data

The owner initially expected shared data to be stored online using a free or no-cost hosted database/backend option if practical.

Later clarification established that the system is intended for small personal use, should normally fit a no-cost tier, and may revisit hosting cost if scope grows. Neon/Postgres was mentioned as a candidate but not selected.

### DM experience — primarily tablet

The owner initially imagined the DM experience as tablet-first and including:

- access to every character sheet in the campaign;
- a quick-access view/tab containing important PC statistics so the DM does not need to ask players repeatedly;
- a battle/initiative tracker showing whose turn it is;
- convenient access to monster stat blocks during combat, especially when resolving monster/NPC turns.

Later clarification added a PC group quick view, an initial quick-stat set, and player-visible initiative with DM-hidden creatures excluded.

### Accounts and campaigns

The owner initially imagined:

- every user has an account;
- authentication must be reasonably secure for the intended use;
- a DM creates a campaign;
- player characters are enrolled/associated with that campaign.

Later clarification established one user identity with campaign-scoped roles and DM-controlled enrollment direction.

### Possible Windows desktop application

The owner initially introduced a possible Windows desktop companion application for campaign management.

Later clarification reframed the real need as a **desktop/laptop-friendly administration surface** for campaign/NPC/monster preparation and entry. Whether that becomes native Windows, web, local web, or another approach remains open.

### D&D SRD reference

The owner initially wanted direct consultation of both D&D fifth-edition SRD generations.

Official project terminology was later confirmed as:

- **SRD 5.1** = earlier/2014-era fifth-edition foundation;
- **SRD 5.2.1** = revised/2024-era fifth-edition foundation.

Later clarification established that the real desired outcome is quick rules clarification during play, not building a D&D Beyond replacement, automated character builder, or strict rules engine. Campaigns may mix both SRD generations and house rules/homebrew. AI-assisted clarification is only a candidate idea.

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

This behavior is now formally recorded in D-0013.

## Historical design themes visible from the first picture

1. **Shared authoritative state:** campaign, users, characters and DM visibility imply synchronized shared data.
2. **Auditability:** anti-cheating oversight requires character change history rather than only latest values.
3. **Role-aware UX:** player and DM experiences are substantially different even though they operate on shared data.
4. **Tablet-first DM workflow:** combat and campaign oversight benefit from information-dense layouts distinct from phone layouts.
5. **Character data vs PDF layout:** character data should remain independent from any one PDF output template.
6. **Rules-version coexistence:** official SRD references need source/version identity even though campaigns may mix them.
7. **Cross-surface administration:** desktop/laptop preparation is a workflow need, but technology remains undecided.

## Continue from the clarification round

Do not restart discovery from the old questions below. Continue from:

`docs/discovery/2026-08-28_CLARIFICATIONS_01.md`

and the active unresolved questions in `docs/PROJECT_STATE.md`.
