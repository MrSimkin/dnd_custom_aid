# Phase 4A — preqa.8 — P15 Supercompact Round 2

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P15 ROUND 2 CLOSED / DESIGN CONTINUES  
**Implementation authorization:** NOT YET

## Context

P15 redesigns Supercompact away from the current responsive tile/dashboard grammar toward a coherent PC stat-block-style play reference inspired by modern 5.5e information hierarchy without reproducing copyrighted layout/artwork.

Round 1 already established the stable high-level structure: identity, compact combat summary, compact ability table, useful reference lines, and Favorites as an accelerator rather than the structural backbone.

## Round 2 — owner-approved decisions

### 7. Combat entries follow real action economy

Supercompact groups combat entries into their natural action categories rather than a generic Favorites/Combat bucket. At minimum this includes ordinary `ACCIONES`, `ACCIONES ADICIONALES`, and `REACCIONES` when such entries exist. Other meaningful action categories supported by the canonical data model may receive their corresponding section rather than being forced into `Acciones`.

Empty action sections are omitted.

### 8. Show all configured combat actions

Supercompact is useful even with zero Favorites. All configured combat actions appear in their natural section. Favorite state may elevate/order/highlight an item within that section, but lack of Favorite status must not remove it from Supercompact.

### 9. Dense natural `RASGOS` section

Traits live in a dedicated dense `RASGOS` section rather than generic Favorite cards.

- mechanical name/summary comes first;
- useful state such as uses/rest cadence may be included compactly;
- long prose is not permanently expanded by default;
- tapping/activating a trait may reveal its fuller detail when needed;
- Favorite state may elevate/order an important trait but does not determine inclusion.

### 10. Dedicated spellcasting section

Spellcasting receives its own compact section rather than being scattered through Favorites and generic resources.

The section should expose the character's relevant spellcasting summary, including when available:

- spell save DC;
- spell attack modifier;
- casting ability/source;
- compact spell-slot availability;
- cantrips;
- prepared/known spells or equivalent canonical grouping.

Multiple spellcasting sources must retain their distinctions when they differ; Supercompact must not flatten several canonical sources into one misleading shared DC/ability/source.

Full spell descriptions are not permanently expanded. Tapping/activating a spell can reveal its normal useful detail.

### 11. Compact direct operation of slots/resources

Spell slots and bounded resources remain directly operational from Supercompact, but the UI must not permanently allocate a full card with separate `Usar / Recup.` buttons to every slot level/resource.

Preferred grammar is a dense state line/table such as:

`Espacios   1º 4/4   2º 3/3   3º 2/3   4º 1/1`

Tapping/activating a level exposes an appropriate compact spend/recover interaction.

Likewise resources such as Inspiration, class resources, sorcery points, etc. may be represented in dense state rows and remain operational where their canonical interaction allows it.

### 12. Supercompact is a play surface, not a structural editor

Accepted principle:

> **Use things here; define/edit them elsewhere.**

Direct Supercompact interactions are limited to legitimate play/session operations such as:

- damage/healing through the shared P2 grammar;
- spend/recover spell slots;
- spend/recover resources;
- Inspiration/session state;
- other already-defined operational actions.

Structural edits remain in the normal sheet/settings surfaces. Supercompact does not become an alternate editor for attack definitions, trait/provenance definitions, spell lists, resource maxima/definitions, names, etc.

This distinction aligns with the P14 Table Mode interaction policy: presentation and operational interactions may remain usable while structural mutation is blocked.

## Regression / design boundary added by Round 2

Implementation and QA for P15 must prove at minimum:

- action sections use canonical action-economy categories and omit empty categories;
- non-favorite attacks/actions remain visible;
- Favorite state influences convenience/order rather than canonical inclusion;
- traits are dense and expandable rather than permanently verbose;
- spellcasting preserves multiple-source distinctions;
- slot/resource operations remain available without recreating a card/button dashboard;
- Supercompact does not expose structural-definition editing controls;
- operational state updates the same canonical Player data rather than a Supercompact-specific copy.

## Explicit non-goals for this round

- final responsive/phone/tablet column behavior is not closed here;
- exact handling of conditions, death saves, concentration, temporary effects and quick-access presentation remains for later P15 reconciliation if needed;
- this round does not authorize implementation.
