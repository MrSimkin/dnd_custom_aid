# Product Evolution Requirements

**Status:** Approved future product direction / architecture constraint  
**Date:** 2026-08-30  
**Decision owner:** Project owner

This file records expected future product capabilities that are not necessarily MVP implementation scope but must be considered when choosing durable domain boundaries.

## 1. Cross-campaign reusable NPCs and creatures

The same conceptual NPC may participate in more than one campaign, including campaigns that are active simultaneously.

Example: one recurring world NPC may appear in Campaign A and Campaign B without the application forcing those appearances to be treated as completely unrelated people.

The architecture should permit a reusable/canonical NPC or creature identity where useful while also permitting **campaign-specific context/state**, such as party relationship, local notes, current situation, or campaign-specific variation.

Exact storage schema and editing UX remain architecture/implementation decisions.

## 2. Reuse modes must be intentional

Reusable personal-library material should eventually be capable of supporting more than one relationship with campaign content. Expected future behaviors include:

1. **Independent copy** — adopt/copy an NPC, monster, or similar record and allow it to diverge independently.
2. **Linked with manual update** — retain provenance/linkage and allow an explicit **Update from Library** workflow.
3. **Selected live link** — for some records, deliberately keep campaign use linked so approved source-library changes can propagate automatically.

Automatic propagation must be **explicitly selected**, not an accidental side effect of sharing a database row or source identity.

Different campaigns or records may use different reuse modes for the same source material.

These capabilities are expected future direction; they do not all need to be implemented in the MVP unless separately promoted into MVP scope.

## 3. Characters may be copied or moved between campaigns

Future product evolution must not assume that a PC can only ever belong to one campaign identity for its entire existence.

The owner expects both possibilities to remain feasible:

- **copy** a character into another campaign, creating a distinct resulting character where appropriate; and
- **move/transfer the same character** between campaigns while preserving its durable identity and relevant continuity/history rather than forcing duplication.

The exact rules for historical campaign association, audit visibility, ownership/control, simultaneous participation, and transfer permissions remain future product/design decisions.

The current architecture must avoid a structural dead end that makes explicit character movement require replacing the entire character model.

## 4. Live encounters remain isolated working state

These cross-campaign reuse capabilities do **not** change the approved live-combat rule.

A live encounter remains an independent runtime working state. Updating a personal-library NPC/monster, a campaign record, or a source definition must not silently rewrite an encounter already in progress.

## 5. Design principle

The intended principle is:

> Keep ownership/context boundaries explicit, while allowing deliberate sharing, linking, updating, copying, and transfer where the product calls for it.

Do not achieve convenience by creating hidden coupling between campaigns. Do not achieve isolation by permanently preventing legitimate cross-campaign continuity.

This is an evolution constraint, not a frozen database schema. Future migrations and model refinements are permitted when new requirements justify them.
