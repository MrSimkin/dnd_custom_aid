# P1–P11 Material 3 audit and P12 recovery

**Date:** 2026-09-12  
**Branch:** `discovery/p12-material3-audit`  
**Canonical baseline audited:** `main` at `de3930a8c0357bbbaa77c423f0011041f5cfd111`  
**Scope:** DM product/design discovery only  
**DM implementation status:** BLOCKED until Phase 4A is accepted and explicitly closed

## 1. Why this audit exists

The previous discovery conversation was interrupted while working on the item referred to in chat as **P12**. The owner had identified a process defect: the discussion had begun to use **Material 3** too mechanically even though that material had already been recognized as potentially unsuitable as a direct live-product model.

The owner requested a full backward audit of P1–P11 before P12 continued, specifically to determine whether earlier conclusions had also copied Material 3 blindly, whether their solutions were actually appropriate, and whether they could reproduce the same design problem later.

No durable repository record demonstrated that this requested audit had been completed before the interruption. Under D-0004/D-0012/C-0005, chat-only work is not sufficient operative memory. This checkpoint therefore reconstructs and performs the audit from primary repository decisions plus the surviving owner-supplied source documents.

## 2. Reconstruction of “P12” and “Material 3”

The repository does not contain literal `P1`–`P12` or `Material 3` labels. The strongest reconstruction is:

- the numbered P sequence maps to the numbered design sequence now persisted in D-0068;
- D-0068 section 12 is **Creatures/NPCs inside Dungeon Desk**;
- the owner-supplied source batch from 2026-09-10 was, in upload order:
  1. `Ejemplo lugares.docx`;
  2. `Casa de Huéspedes del Tercer Patio.docx`;
  3. `Ejemplo NPC desarrollado.docx`;
  4. `Aventura 1.docx`;
- therefore the strongest available reconstruction is **Material 3 = `Ejemplo NPC desarrollado.docx`**.

This identification is high-confidence rather than mathematically provable because the lost chat's informal numbering was not persisted. It is also substantively consistent: Material 3 is the rich Liora Vael NPC dossier, and P12 is exactly the point where Dungeon Desk creature/NPC presentation becomes the active topic.

## 3. Material 3: what it is good for, and what it must not become

`Ejemplo NPC desarrollado.docx` is valuable prepared content. It contains, in one rich artifact:

- physical description;
- short and rich presentation summaries;
- attitude and voice;
- apparent vs real nature;
- motivation and DM secret;
- what the NPC wants, offers and blocks;
- access conditions and likely scene use;
- campaign/adventure relationships;
- visual/prompt material;
- full mechanical statistics, traits, actions, reactions and spell/resources.

That makes it an excellent **authoring/preparation source and reference example**.

It does **not** follow that:

- every NPC must have all those fields;
- those fields define the universal canonical NPC schema;
- Dungeon Desk should render the dossier field-for-field;
- Stage, Dungeon and Combat should use identical NPC projections;
- narrative NPC identity and reusable mechanical stat block must be one inseparable object;
- a full two-page dossier is the right live interaction surface.

The controlling DM Attention Budget requires the opposite discipline: rich prepared content may exist, while live Desks project only the slice that earns attention in the current operational task.

## 4. P1–P11 audit

| Point | Persisted topic | Material 3 dependency | Audit result | Risk / action |
|---|---|---|---|---|
| P1 | DM Attention Budget / assistant principle | None | **KEEP** | This principle actively protects against blindly reproducing a rich dossier in live UI. |
| P2 | Campaign → Workspace → flat Desks | None | **KEEP** | Grounded in live workflow and future evolution, not NPC document structure. |
| P3 | Desk definition | None | **KEEP** | Explicitly prevents an NPC/monster/room from becoming a Desk merely because it is an entity. This is a useful anti-overfit boundary. |
| P4 | Combat Desk conceptual boundary | None | **KEEP** | Grounded in earlier approved combat requirements. No Material 3 schema leakage identified. |
| P5 | Dungeon Desk operational goal | None | **KEEP** | Synthesizes the owner's dungeon workflow, Dungeon Turns and prepared dungeon needs. No reason to roll back. |
| P6 | Topological / flowchart dungeon structure | None | **KEEP** | Directly grounded in the owner's stated mapping practice and reinforced by `Aventura 1.docx`; not derived from Material 3. |
| P7 | Zone/room prepared operational brief | None | **KEEP WITH GUARDRAIL** | Grounded primarily in `Aventura 1.docx`. Richness should be preserved without making that one adventure's field list mandatory. Later `Presentar / Interactuar / Encuentro` remains an alpha projection/organization, not a universal storage schema. |
| P8 | Lightweight contextual live notes | None | **KEEP** | Generic contextual-note design follows the Attention Budget and avoids per-entity note silos. |
| P9 | Dungeon Turns beta / application boundary | None | **KEEP** | Directly grounded in `turno de dungeon.docx`; beta uncertainty is already preserved. |
| P10 | Bounded Dungeon Turn log | None | **KEEP** | Properly derives a useful log from actual turn operation instead of inventing a general activity ledger. |
| P11 | Generic advisory clocks/counters | None material | **KEEP WITH PROPORTIONALITY** | Clocks are supported by adventure examples but were generalized deliberately and kept advisory. Do not evolve them into an automation engine without real need. |

### Audit conclusion for P1–P11

**No P1–P11 rollback is required on Material 3 grounds.**

The earlier decisions did not copy the developed-NPC dossier into unrelated product structures. Several of them in fact establish the exact safeguards needed to prevent that error: purpose-specific Desks, minimal live input, prepared-vs-live separation, flexible authored richness, advisory rather than automatic behavior, and explicit Pending UX where real play must teach the answer.

The audit did identify two broader guardrails worth preserving:

1. **Material 4 / one adventure must not become a universal Zone Brief schema either.** The current grouping is useful as an alpha projection, but optional content must remain optional and storage should not be forced to mimic one example document.
2. **Reusable generic concepts must stay proportional.** Notes, clocks and triggers are useful only while they reduce DM cognitive load; they must not grow into generalized automation/ontology systems merely because the domain could support them.

## 5. The actual Material 3 contamination risk

The dangerous boundary begins at P12, not P1–P11.

D-0069 currently says that the existing Developed NPC ficha is the “authoritative rich source” and that Stage may project a compact Run NPC / How to run view. The projection idea is correct, but the wording is vulnerable to misreading: a future agent could interpret the supplied Liora document as the mandatory canonical field model or as the live-layout template.

That interpretation would contradict:

- the DM Attention Budget;
- the paper-companion / partial-digital-representation principle;
- the rule that entities are reusable across contexts;
- the separation between preparation and live operation;
- the explicit non-goal of complete creature authoring during live Encounter Readiness.

A clarification is therefore required before P12 continues: **Material 3 is a rich preparation example/source, not a universal NPC schema or live UI specification.**

## 6. Correct P12 problem statement

P12 should answer:

> How does Dungeon Desk give the DM extremely fast, context-aware access to PCs, NPCs and creatures needed during dangerous exploration without turning the rich preparation dossier into a live form, duplicating canonical entities, or stealing Encounter Readiness / Combat Desk responsibilities?

It should **not** answer:

> How do we reproduce every field in the developed NPC document on the tablet?

## 7. Required separation before detailed P12 UX

P12 should preserve four distinct conceptual layers:

1. **Canonical prepared entity/reference**  
   Reusable NPC/creature identity, authored material and any linked mechanical reference. This is where rich preparation can live.

2. **Dungeon contextual presence**  
   Why/where that NPC or creature currently matters to this Dungeon Desk: expected/placed, roaming, newly introduced, current/last-known area when useful, and small live context. This must not duplicate the canonical dossier.

3. **Encounter Readiness staged variant**  
   Only when pre-combat mechanical staging is useful: quantities, reserves, substitutions, packages and dirty temporary mechanical overrides. This is P13's responsibility.

4. **Combat live instance**  
   Only once combat actually begins: initiative/live HP/conditions/concentration/etc. under Combat Desk authority.

The same source entity may participate in more than one layer, but the layers must not be collapsed into one giant mutable record.

## 8. NPC identity and mechanical stat block must not be forced 1:1

Material 3 visually combines a rich narrative NPC dossier and a complete mechanical stat block. The product should not infer from that presentation that they must always be one inseparable schema object.

The safe product direction is relational/compositional:

- an NPC may exist without a combat-ready stat block;
- an NPC may link to a reusable mechanical stat block when needed;
- the same useful mechanical block may be reskinned/reused for several creatures/NPCs;
- live staging may temporarily patch the mechanical representation without rewriting the rich NPC source;
- future special cases may justify another mechanical representation without forcing duplication of narrative identity.

This is a product/domain guardrail, not authorization to implement a generalized inheritance/template framework.

## 9. P12 live projection direction

Exact fields and exact UI remain pending real-table refinement, but a good Dungeon Desk projection should optimize for immediate questions such as:

- **Who/what is relevant here right now?**
- **Where are they / why are they relevant to this dungeon context?**
- **How do I portray or run them in the next few seconds?**
- **Which exploration-relevant mechanical facts do I need without opening a full stat block?**
- **Can I open the full dossier or full stat block immediately if the situation deepens?**
- **Can I stage them for possible combat without rebuilding them?**

Possible compact sources of value include identity/role, one or two portrayal/goal cues, location/context, senses/movement or other exploration-relevant mechanical facts, and immediate links to full prepared material. **These are candidate projection categories, not a mandatory universal field list.**

The full Material 3 dossier remains reachable when relevant, but it should not consume the normal live surface by default.

## 10. P12 vs P13 boundary

P12 owns **fast creature/NPC/PC access and dungeon-context presence**.

P13 Encounter Readiness owns **pre-combat mechanical composition and dirty mechanical staging**, including quantities, add/remove/substitute, reserves, packages and temporary HP/AC/attack/damage overrides.

P12 may provide the shortcut that sends a relevant creature/NPC into Encounter Readiness. It should not duplicate those editing mechanics merely to keep the creature “inside Dungeon Desk.”

## 11. Recommended continuation

The next concrete P12 design pass should test the fast-access workflow rather than enumerate dossier fields. A useful alpha question set is:

1. How does the DM see **Here / Expected / Roaming / Recent or newly introduced** material without maintaining classifications manually?
2. What is the smallest useful collapsed creature/NPC row/card?
3. What single expansion gives a practical `Run now` projection?
4. How are full dossier, full stat block and Encounter Readiness reached in one action?
5. What location/presence changes deserve a one-tap action, and which should remain purely narrative/manual?
6. Which PC-group facts are genuinely exploration-useful, with paper-first freshness respected?

Do not freeze exact fields or layout until these questions are reviewed against real live-DM use.

## 12. Verification / repository effect

This audit changes **no product code**, schema, build configuration or Phase 4A implementation state.

No automated product build is required for this documentation-only recovery checkpoint.

`main` remains untouched. The technical implementation resume point remains **Increment F — Conjuros** on `implementation/phase4a-successor-cycle`.

The audit and P12 recovery work live on `discovery/p12-material3-audit` pending owner review/approval before any canonical merge.
