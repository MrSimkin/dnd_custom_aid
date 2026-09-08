# D-0067 reconciliation — pending owner decisions

**Date:** 2026-09-08  
**Status:** PENDING OWNER DECISIONS; repository/code audit complete enough to ask only consequential questions  
**Canonical branch:** `main`  
**Product code changed:** no

## Purpose

D-0067 and the Stage A–F owner-audition backlog can largely be reconciled without further owner input. This checkpoint records only the remaining questions whose answer materially changes persistent data shape or product semantics.

The repository audit already established that:

- custom skills already exist durably and can be moved to PC Settings while rendered inline in Habilidades;
- languages and armor proficiencies already share canonical `CharacterProficiency` data;
- structured rest cadence/amount metadata already exists for generic resources and should be generalized/reused rather than duplicated;
- Inspiration already has one canonical durable Boolean;
- attack damage is currently one opaque `damageEffect: String`, so structured damage components require a real model/schema/backup migration;
- the current dice surface already derives standard attributes, saves, skills, custom skills and attacks from character data, so the redesign should reuse one target-selection engine;
- the current ability model is closed over the six standard `CharacterAbility` enum values, so custom attributes are a consequential domain extension rather than a cosmetic UI field;
- spellcasting currently has one global `spellcastingAbility`, one spell-save DC and one spell-attack modifier even though spellcasting sources are already separate objects, so multiclass/source-specific casting requires a deliberate ownership correction;
- the current initial Android flow is campaign-first; the desired character-first start surface therefore needs an application-navigation redesign but not a separate character-summary authority;
- current Trasfondo contains two explicit image placeholders, while General already demonstrates Android document selection for portrait/token; background image persistence still needs app-owned/backup-safe storage rather than merely transient external references.

## P-01 — Custom attribute semantics

Owner requirement: custom attributes can be added from PC Settings and participate where attributes are relevant.

### Recommended model

Treat a custom attribute as a real additional ability-like stat with:

- name;
- short label/abbreviation;
- numeric score;
- D&D-style modifier derived from score by default;
- availability to custom skills;
- availability as a direct dice target;
- optional saving-throw configuration rather than automatically creating a saving throw for every custom attribute;
- availability as a spellcasting ability for custom/spellcasting sources where selected.

This keeps D&D's six standard saves intact while allowing homebrew abilities such as Honor/Sanity-like stats without hard-coding every custom attribute into all standard systems.

### Owner decision needed

Should custom attributes behave as the recommended full ability-like model above, including an **optional** associated saving throw, or should they be limited to score/modifier + custom skills/dice only?

## P-02 — Custom General/Gestión markers: reuse Resources or create a separate model

Owner requirement: user-defined binary/integer markers such as `Puntos de destino` / `Puntos de estrés`, configured from PC Settings and shown in General + Gestión.

### Recommended model

Reuse/extend the existing generic `CharacterResource` authority instead of creating a parallel marker table.

Add presentation/config metadata such as:

- value kind: `BINARIO` or `CONTADOR`;
- `mostrarEnGeneral`;
- existing current/max/recovery semantics when applicable.

PC Settings owns structure/configuration; General and Gestión expose the same live value. This directly follows the approved one-datum/one-state rule and allows the existing rest-recovery infrastructure to work for counters that recover on rests.

### Owner decision needed

Approve this reuse of the generic Resource model, or do you want custom markers to be a concept intentionally separate from Resources?

## P-03 — Multiclass spellcasting ability ownership

Owner requirement: when multiclassing produces multiple spellcasting abilities, show all in registered-class order.

Current model has one global `spellcastingAbility`, while Conjuros already has multiple `CharacterSpellcastingSource` objects that may be linked to a class or fully custom.

### Recommended model

Move casting-stat ownership to each **spellcasting source**:

- each source has its own selected spellcasting ability;
- a source linked to a known class can default/suggest the normal class ability, but remains editable/permissive;
- custom/non-class sources can choose any standard or custom attribute;
- `CD salv. conjuro` and `Mod. ataque mágico` are derived per source from that ability + proficiency, with explicit adjustment fields only if needed for homebrew exceptions;
- presentation orders linked-class sources by registered class order, followed by custom sources by their manual source order.

This fits the existing source architecture better than putting multiple casting abilities back onto one global field.

### Owner decision needed

Approve spellcasting ability/stat ownership **per spellcasting source** as recommended?

## P-04 — What exactly should `Defensas` include regarding armor?

The repository already stores armor **proficiencies** as `CharacterProficiencyType.ARMOR`. Current `Defensas` itself stores resistance/immunity/vulnerability-style references.

The phrase "Defensas should include armors" is therefore ambiguous at the product level.

### Owner decision needed

Do you mean:

- **armor proficiencies** (`armadura ligera`, `media`, `pesada`, `escudos`, etc.) should also be visible in the Defensas area from the same proficiency data; or
- **currently worn/owned armor and AC-related information** should be surfaced there; or
- both?

No duplicate persistence will be created whichever presentation is chosen.

## P-05 — `Gemas / arte` data shape

Owner requested a `Gemas / arte` box under Equipo.

### Recommended model under project proportionality

Start as a compact free-form valuables field/box rather than building a full second inventory subsystem. Ordinary gems/art objects can still be represented as inventory items when detailed quantity/value tracking matters.

A structured gem/art collection would only be justified if the owner wants per-entry name, quantity, value and/or total-wealth calculations.

### Owner decision needed

Should `Gemas / arte` be:

- a **simple free-form box** (recommended for this cycle); or
- a **structured list** of valuables with per-entry fields?

## P-06 — `Mitos de Cthulhu` condition set/source

The repository contains no authoritative `Mitos de Cthulhu` condition catalog or explanatory text.

### Owner decision needed

Which exact game/book/source do you mean by the `Mitos de Cthulhu` conditions? If you want their rule explanations bundled, identify/provide the source material or a source/license appropriate for inclusion.

This question does not block the ordinary SRD condition-catalog design; it only blocks the specific Cthulhu condition names/descriptions.

## Decisions not requiring more owner questions

Unless later contradicted by the owner, the reconciliation will proceed with these implementation directions:

- character-first home screen listing characters across campaigns, with campaign filtering/administration secondary;
- no duplicate character-summary authority: list rows project canonical character + campaign data;
- structured attack damage components become the one source for attack-card damage and damage rolling;
- standard/custom roll selection reuses one character-aware dice-target engine;
- custom skills move to PC Settings structurally and render inline/italic in Habilidades;
- languages receive a dedicated visible presentation but remain the same `CharacterProficiency` records;
- background images use app-owned durable storage and are included/remapped in own-format backup/import;
- tab order is a per-character presentation preference; conditional hidden tabs retain their stored position;
- phone landscape gets a phone interaction composition independent from tablet redesign;
- tablet portrait/landscape receive a separate full UX redesign/optimization pass;
- stepped text/spacing controls with live preview, including 40% spacing;
- notes search/filter is UI/collection work with no new notes persistence model;
- haptic strength/duration use bounded understandable levels mapped to device capabilities;
- cross-domain rest recovery extends the existing structured recovery concepts rather than creating per-tab rest engines;
- official-existing-vs-custom flows remain conditional on actual available licensed corpus data.

## Exact continuation

Await owner answers to P-01 through P-06. Then:

1. record those answers durably;
2. complete the D-0067 + QA dependency map;
3. split the cycle into coherent schema/domain, shared UX primitive, surface redesign and responsive/testing increments;
4. create one focused implementation branch from canonical `main`;
5. do not implement piecemeal before the plan is closed.
