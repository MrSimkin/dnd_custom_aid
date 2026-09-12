# D-0071 — Dungeon creature/NPC live projection boundary

**Status:** Proposed — owner review required before canonical acceptance  
**Date:** 2026-09-12  
**Decision owner:** Project owner  
**Implementation status:** product/design proposal only; DM implementation remains BLOCKED until Phase 4A is accepted and explicitly closed

## Purpose

This proposal continues the interrupted P12 Dungeon Desk discovery after the Material 3 audit recorded in `docs/checkpoints/2026-09-12_P1_P11_MATERIAL3_AUDIT_AND_P12_RECOVERY.md`.

If approved, it clarifies D-0068 section 12 and the D-0069 NPC projection language without changing the Phase 4A implementation gate.

The central correction is:

> A rich prepared NPC dossier is source material, not a universal live UI or mandatory NPC schema. Stage, Dungeon and Combat must project the same reusable material differently according to their operational purpose.

---

## 1. Material 3 clarification

The owner-supplied `Ejemplo NPC desarrollado.docx` is a valuable **rich prepared NPC example/source** for the current discovery work.

It must **not** be interpreted as:

- a mandatory universal NPC field list;
- the required storage schema for every NPC;
- the default live Dungeon/Stage/Combat layout;
- evidence that narrative identity and mechanical stat block must be one inseparable object.

This proposal therefore narrows the D-0069 phrase “authoritative rich source” to mean authoritative prepared source content where that owner-authored dossier is being used, **not authoritative universal product structure**.

---

## 2. Four conceptual layers

Dungeon creature/NPC use should preserve four distinct layers.

### A. Canonical prepared entity/reference

The reusable campaign source for an NPC/creature and its authored material.

It may contain rich narrative preparation, relationships, presentation cues and links to mechanics. It does not need to be complete merely to exist.

### B. Dungeon contextual presence

A lightweight relationship/state expressing why that entity matters in the current Dungeon Desk.

Examples may include:

- expected/placed here;
- roaming/relevant to the dungeon;
- introduced unexpectedly;
- current or last-known area when the DM chooses to track it;
- a tiny live-context note or state when immediately useful.

This layer must not duplicate the full dossier.

### C. Encounter Readiness staged variant

When possible combat needs mechanical staging, D-0068 section 13 applies.

This layer owns temporary composition and mechanical changes such as quantities, reserves, substitution, reusable packages and dirty live overrides.

### D. Combat live instance

Only when combat actually starts does Combat Desk own active combat working state such as initiative, live HP, conditions, concentration and defeated/removed state.

The staged state should hand off rather than be reconstructed.

---

## 3. NPC identity and mechanical stat blocks are related, not forced 1:1

A developed NPC dossier and a stat block may be shown together in preparation, but the product should not hard-code that presentation accident as the domain rule.

Approved-direction candidate:

- an NPC may exist with no combat-ready stat block;
- an NPC may reference a reusable stat block when mechanics are needed;
- a useful mechanical stat block may be reused/reskinned for multiple creatures/NPCs;
- Encounter Readiness may patch a staged mechanical representation without rewriting the canonical NPC;
- future context-specific mechanical representations remain possible if real play justifies them.

This is deliberately **not** a generalized inheritance/template system. C-0009 remains controlling.

---

## 4. P12 operational goal

P12 should optimize for this live task:

> Give the DM immediate access to the people/creatures and PC information relevant to dangerous exploration, with just enough current context and run-now guidance to act without opening a full dossier unless needed.

The normal path should answer, with minimal interaction:

1. **Who/what matters here?**
2. **Why are they relevant / where are they?**
3. **How do I run or portray them right now?**
4. **Which exploration-relevant mechanical facts matter now?**
5. **How do I open the full source or stage possible combat immediately?**

---

## 5. Fast-access working sets, not mandatory classifications

D-0068 already requires very fast access to expected/placed, roaming, unexpected/improvised creatures and PC references.

For live UX, useful working sets may include concepts such as:

- **Here / current area**;
- **Expected / nearby**;
- **Roaming / dungeon-wide**;
- **Recent / newly introduced**;
- **Search / library**.

These are **candidate retrieval projections**, not required permanent taxonomy fields.

The application should infer membership from prepared placement, current context and recent actions wherever possible. The DM should not maintain categories simply to keep the interface tidy.

A creature may appear in more than one useful projection without creating duplicate canonical records.

---

## 6. Compact live creature/NPC projection

Exact fields and exact layout remain Pending, but the compact presentation should be purpose-driven rather than dossier-driven.

### Collapsed identity/reference

A collapsed row/card should usually need only enough to distinguish and act on the entity, such as:

- name/label;
- compact role/type cue when useful;
- current contextual presence/location when tracked;
- one highly relevant state/cue if it earns the space.

### `Run now` expansion

A one-action expansion may project a very small subset of prepared material such as:

- portrayal/voice/attitude cue;
- immediate motivation/goal or current intent;
- one secret/pressure/behavior cue only when useful in this context;
- exploration-relevant mechanics such as senses, movement or another truly relevant quick fact;
- creature-specific tactical/exploration guidance where appropriate.

The exact content should depend on what exists and what the current context needs. Empty optional categories should not create blank UI.

### Full source remains immediate

From the live projection, the DM should be able to reach in one action where practical:

- full prepared NPC/creature dossier;
- full linked stat block;
- Encounter Readiness staging;
- relevant contextual notes.

The full rich source remains available without being the default live surface.

---

## 7. Location/presence behavior

Creature/NPC location remains DM-authored/adjudicated state, not simulation.

Useful operations may include one-tap/manual actions such as:

- place/move to current area;
- move to another prepared area;
- mark location unknown / no longer tracked;
- remove from current dungeon relevance;
- add an unplanned creature/NPC to the current context.

Exact action wording and interaction remain Pending.

Clocks, Dungeon Turns or triggers may **suggest** movement or attention, but they do not silently relocate a creature.

No schedule-conflict, patrol-legality or simulation warning should block the DM from placing a creature wherever fiction/pacing requires.

---

## 8. Boundary with Encounter Readiness

P12 must not duplicate P13.

P12 may expose `Stage for encounter` / equivalent contextual action, but once the DM begins changing combat composition or mechanics, Encounter Readiness owns that staged working state.

Therefore P12 should not grow its own parallel controls for:

- encounter quantities;
- reserves;
- package application;
- temporary HP/AC/attack/damage patches;
- group-vs-individual combat overrides.

This separation prevents two different pre-combat truths from emerging inside the same Dungeon Desk.

---

## 9. PC reference inside Dungeon Desk

Dungeon Desk should retain:

- compact PC-group reference;
- one-action access to any full PC sheet.

The dungeon-specific quick projection should prioritize stable or clearly fresh exploration-relevant facts rather than copying the Combat Desk quick view mechanically.

Candidate facts for later testing may include senses, passive values, movement, languages, selected proficiencies/tools or other exploration-relevant capabilities.

Because normal play is paper-first, potentially stale digital transient values must not be silently treated as current table truth.

The exact field set remains **Pending real-table refinement**.

---

## 10. What P12 explicitly does not decide

This proposal does not freeze:

- exact screen layout;
- exact collapsed-card fields;
- exact `Run now` fields;
- a universal NPC schema;
- whether every NPC needs mechanics;
- a generalized creature inheritance/template system;
- automatic patrols/schedules;
- combat staging mechanics already owned by P13;
- detailed Combat Desk creature presentation.

---

## 11. Alpha interaction scenarios to test next

Before exact UI is approved, P12 should be evaluated against representative live scenarios:

1. **Prepared guard in current room** — DM needs identity, intent and a couple of exploration facts immediately; no combat yet.
2. **Roaming patrol becomes relevant** — DM moves/associates it manually after a noise/clock prompt.
3. **Unexpected NPC from Stage material enters the dungeon** — existing canonical NPC is reused without copying its dossier into Dungeon Desk.
4. **Improvised monster not in preparation** — DM finds/reuses a mechanical block quickly and may later stage it.
5. **Social NPC suddenly becomes combat-capable** — full/linked stat block or Encounter Readiness is reachable without mutating the rich source.
6. **Same generic mechanical block reused for several creatures** — reskin does not create unnecessary permanent definitions.
7. **Possible encounter never becomes combat** — no Combat Desk is created merely because material was kept ready.
8. **Party asks an exploration question** — PC group quick reference answers it without forcing several full-sheet openings.

A candidate interaction design should be rejected if it only works well for the rich Liora dossier but fails these cases.

---

## 12. Approval effect

If the owner approves this proposal:

- it clarifies D-0068 section 12;
- it clarifies D-0069's Developed NPC source language;
- it becomes the controlling guardrail for later Stage/Dungeon/Combat NPC projections where they intersect;
- detailed visual UX remains a future design/playtest question;
- DM implementation remains blocked until Phase 4A closure.

No product code change is authorized by approving this design decision alone.
