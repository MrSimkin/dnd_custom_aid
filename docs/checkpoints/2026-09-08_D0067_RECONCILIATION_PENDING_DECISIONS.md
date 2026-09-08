# D-0067 reconciliation — owner decisions resolved

**Date:** 2026-09-08  
**Status:** OWNER DECISIONS RESOLVED; reconciliation/design pass may proceed to final dependency/build plan  
**Canonical branch:** `main`  
**Product code changed:** no

## Purpose

D-0067 and the Stage A–F owner-audition backlog were audited against the current repository. Six questions remained whose answer materially changed persistent data shape or product semantics. The owner answered all six on 2026-09-08. This checkpoint replaces the earlier pending state and records the controlling decisions.

No product implementation is authorized by this checkpoint. The next step remains completion of one coherent dependency-aware implementation plan before creating a focused product branch from canonical `main`.

## R-01 — Custom attributes: approved as full ability-like statistics

Owner decision: **approved**.

A custom attribute is a genuine additional ability-like statistic with:

- name;
- short label/abbreviation;
- numeric score;
- D&D-style modifier derived from score by default;
- availability to custom skills;
- availability as a direct dice target;
- availability as a spellcasting ability when selected by a spellcasting source;
- an **optional** associated saving throw rather than automatically creating one for every custom attribute.

The six standard D&D abilities remain first-class built-ins. Custom attributes extend the model without pretending that every homebrew attribute automatically creates a standard saving throw.

## R-02 — Custom markers and Resources remain logically distinct, but share mechanics where appropriate

Owner decision: **do not collapse Custom Markers into Resources as one user-facing concept**.

The owner distinguishes their intended use cases:

- **Custom Markers** are character-level personal/state trackers configured structurally from `Ajustes del personaje`, such as `Puntos de destino` or `Puntos de estrés`;
- **Resources** remain the existing resource-domain concept and may represent things such as ammunition, food, charges, uses, or other consumable/trackable quantities.

Mechanically, both may use the same reusable tracking primitives where safe. Capabilities such as these should be generalized rather than separately reinvented:

- binary/boolean state;
- integer/counter state;
- current/max where applicable;
- short-rest / long-rest / manual recovery metadata where applicable;
- compact adjustment controls;
- one canonical value even when shown on multiple surfaces.

### Resource placement extension

Resources should gain configurable presentation placement so a resource can appear where it makes semantic sense, not only in one generic area. Examples explicitly approved by owner intent include:

- `General`;
- `Gestión`;
- `Equipo`;
- `Rasgos`;
- other relevant tabs when justified by the resource's role.

This is presentation of the **same resource**, never duplicated persisted values. Implementation should use a controlled set of valid display placements rather than arbitrary copied state.

Custom Markers retain their own PC Settings creation/configuration workflow even if their underlying tracker primitive is mechanically shared with Resources.

## R-03 — Multiclass spellcasting: spellcasting statistics belong to each spellcasting source

Owner decision: **approved; preferred over the original owner suggestion**.

Each `CharacterSpellcastingSource` becomes the authority for its casting context:

- selected spellcasting ability;
- derived `CD salv. conjuro`;
- derived `Mod. ataque mágico`;
- explicit adjustments only where needed for permissive/homebrew exceptions;
- linked class when applicable;
- manual/custom source support remains valid.

A class-linked source may receive the normal class casting ability as a default/suggestion, but the app remains permissive and editable.

Ordering:

1. class-linked spellcasting sources follow registered character-class order;
2. custom/non-class sources follow their manual source order after those class-linked sources.

### Clarity requirement

Any spellcasting information box/row must make clear **which class or source it belongs to**. Do not present multiple DCs/attack modifiers as anonymous global values.

### Conjuros footprint requirement

This per-source improvement must **not** create a larger permanent Conjuros header. The current implementation already stacks a persistent source-selector row above a second persistent `Conjuros`/Add/search/order/filter card, and owner phone-landscape QA established that this fixed stack can consume the complete practical content viewport.

The successor Conjuros design must therefore consolidate source selection, source context and collection actions instead of adding another per-source statistics panel.

#### Recommended Conjuros context-bar design for implementation/audition

Use one compact sticky spell-context bar rather than the current two-block fixed stack.

When one source is selected, the bar can communicate the essential casting context in one line, for example conceptually:

`Mago (INT) · CD 15 · Ataque +7   ▾   Buscar   Filtros 2   +`

Exact iconography/abbreviation remains responsive, but the semantic hierarchy is:

1. selected source/class + casting ability;
2. source-specific DC and spell-attack modifier;
3. compact source switch affordance;
4. compact search/filter/sort actions;
5. compact Add action.

When `Todos` is selected:

- show `Todos los conjuros` as the compact context;
- do **not** display every source's casting-stat block permanently;
- spells associated with multiple/different sources may use small source labels only where they aid understanding;
- source-specific casting details are available by selecting that source or opening transient source details.

Search/filter/sort should not remain expanded as multiple permanent rows. The persistent bar should expose compact actions and active-state indicators (for example `Filtros 2`); expanded controls should open transiently or collapse when not in active use.

Phone portrait and phone landscape both preserve a phone interaction model. In landscape the spell list must remain practically visible; source/filter controls must not consume the complete vertical viewport.

Sticky spell-level/slot headers remain useful but must stay compact and coexist with the single context bar.

A tap on source/casting-stat context may open a transient detail surface containing:

- full source name;
- linked class, if any;
- selected casting ability;
- `CD salv. conjuro`;
- `Mod. ataque mágico`;
- circled-`i` formula explanations.

General may separately show all spellcasting sources because it is not the permanently stacked Conjuros control region. A compact General presentation can use one row per source, e.g.:

`Mago (INT) | CD 15 | Ataque +7`

`Clérigo (SAB) | CD 14 | Ataque +6`

This Conjuros proposal is the recommended implementation/audition direction and directly addresses both the per-source model and the previously recorded fixed-footprint failure.

## R-04 — `Defensas`: owner means current worn armor / AC-related information

Owner clarification: `Defensas` should surface **currently worn armor and AC-related information**, not armor proficiencies.

Rules:

- use canonical equipped inventory / AC character data;
- do not duplicate armor proficiency data into Defensas merely because it exists;
- do not invent automatic AC calculations until the equipment/domain model contains enough structured armor semantics to support them safely.

The current inventory model has an `equipped` state but does not yet contain a complete structured armor-rules model. Therefore the reconciliation plan must distinguish:

1. immediately surfacing current AC + equipped armor/shield references from canonical data;
2. any later automatic AC calculation, which would require explicit structured armor semantics/rule source.

## R-05 — `Gemas / arte`: compact free-form valuables box

Owner accepted the recommended proportional solution.

For this cycle, `Gemas / arte` is a **compact free-form valuables field/box** in Equipo/wealth presentation.

If an individual gem/art object requires detailed quantity, weight, value, location or other tracking, it can be represented as an ordinary inventory item.

Do not build a second structured valuables/inventory subsystem for this requirement in this cycle.

## R-06 — `Mitos de Cthulhu` source identified

Owner source identification:

**Sandy Petersen's Cthulhu Mythos for D&D 5e**, using the owner's Spanish copy at their tables.

This resolves which product/source family the owner means, but **does not by itself authorize reproducing proprietary Spanish rules text in the repository/app**.

Implementation direction:

- design the condition catalog so this source can coexist with ordinary official/SRD/custom condition sets;
- exact Spanish condition names/descriptions/help text may be populated from owner-provided material or another project-appropriate licensed source;
- do not scrape or silently reproduce proprietary book text;
- lack of bundled Cthulhu descriptions does not block the ordinary condition-catalog architecture or the rest of the implementation plan.

## Repository/domain audit conclusions retained

The repository audit established that:

- custom skills already exist durably and can be moved to PC Settings while rendered inline in Habilidades;
- languages and armor proficiencies already share canonical `CharacterProficiency` data;
- structured rest cadence/amount metadata already exists for generic resources and should be generalized/reused rather than duplicated;
- Inspiration already has one canonical durable Boolean;
- attack damage is currently one opaque `damageEffect: String`, so structured damage components require a real model/schema/backup migration;
- the current dice surface already derives standard attributes, saves, skills, custom skills and attacks from character data, so the redesign should reuse one target-selection engine;
- the current ability model is closed over the six standard `CharacterAbility` enum values, so custom attributes are a consequential domain extension rather than a cosmetic UI field;
- spellcasting currently has one global `spellcastingAbility`, one spell-save DC and one spell-attack modifier even though spellcasting sources are already separate objects, so R-03 requires a deliberate ownership/schema migration;
- the current initial Android flow is campaign-first; the desired character-first start surface therefore needs an application-navigation redesign but not a separate character-summary authority;
- current Trasfondo contains two explicit image placeholders, while General already demonstrates Android document selection for portrait/token; background image persistence still needs app-owned/backup-safe storage rather than merely transient external references.

## Decisions not requiring more owner questions

Unless later contradicted by the owner, reconciliation proceeds with these directions:

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
- Notes search/filter is UI/collection work with no new notes persistence model;
- haptic strength/duration use bounded understandable levels mapped to device capabilities;
- cross-domain rest recovery extends the existing structured recovery concepts rather than creating per-tab rest engines;
- official-existing-vs-custom flows remain conditional on actual available licensed corpus data.

## Exact continuation

All six consequential owner questions are now resolved.

Next:

1. complete the D-0067 + Stage A–F + Fuente dependency map;
2. identify the shared domain/storage primitives to migrate first;
3. split the cycle into coherent schema/domain, shared UX primitive, surface redesign, responsive and testing increments;
4. define build/checkpoint boundaries and targeted owner retest scope;
5. only after that plan is coherent, create one focused implementation branch from canonical `main`;
6. do not implement product code piecemeal before the plan is closed.
