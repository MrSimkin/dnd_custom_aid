# Phase 4A — Increment A Data Foundation

Date: 2026-09-08

## Status

**Increment A automated foundation checkpoint: COMPLETE / GREEN.**

This checkpoint closes the schema/domain/storage foundation required by the reconciled Phase 4A successor plan before broad UI migration.

It does **not** imply owner visual acceptance of the successor UI and does **not** replace the last owner-auditioned practical build (`0.4.0-preqa.7`, build 40700).

## Branch and verified product head

Implementation branch:

`implementation/phase4a-successor-cycle`

Verified product head:

`7bfdeed2fe353dad609060d4b933ee22eab30f2e`

Full-gate workflow:

`34286023454`

Result:

- backend check: PASS;
- `:shared:desktopTest`: PASS;
- Android debug compilation/assembly: PASS;
- desktop build: PASS;
- Android debug APK artifact upload: PASS.

`main` remained untouched at:

`698d40b7da75bb7535d83f834db7044ef3e626a8`

## A1 — Generalized abilities and custom attributes

Implemented an extensible `CharacterAbilityReference` that can target either:

- one of the six built-in abilities; or
- a custom character attribute by durable ID.

Custom attributes now support:

- name;
- abbreviation;
- score;
- normal modifier arithmetic;
- optional saving-throw participation;
- optional save proficiency;
- save adjustment;
- notes/order.

Custom skills receive successor-owned ability mapping rather than being permanently limited to the legacy six-value enum. The legacy built-in ability field remains as a compatibility snapshot while successor surfaces migrate.

Successor-aware helpers now calculate:

- custom ability modifier;
- custom-skill total;
- enabled custom saving-throw total.

The Dice target resolver has been generalized to understand custom attributes, enabled custom saves and custom-skill ability references. The current legacy `CharacterEditorV4` still calls Dice without injecting persisted successor state; that UI wiring is intentionally deferred to the later successor surface rebuild rather than replacing a very large legacy editor file during the storage checkpoint. The generalized domain/resolver architecture and arithmetic are present and regression-tested.

## A2 — Spellcasting statistics per source

Each spellcasting source can now own successor casting configuration:

- ability reference;
- save-DC adjustment;
- spell-attack adjustment.

Legacy global spellcasting values are projected conservatively when no successor source configuration exists.

If an old ability is identifiable, the successor profile preserves the displayed total through computed adjustments. If it is not identifiable, compatibility overrides preserve the old stored totals rather than guessing a formula.

## A3 — Structured combat damage

Combat entries now support ordered structured damage components with types including:

- dice;
- flat;
- text.

Legacy `damageEffect` free text is preserved safely as a single text component when no structured successor data exists. The migration does not attempt to parse ambiguous old user text into invented dice/type semantics.

## A4 — trackable state, Markers and Resources

Added reusable successor trackable semantics:

- binary;
- counter;
- current/max;
- structured recovery cadence/mode/fixed amount.

Custom Markers remain a separate concept rather than becoming aliases for Resources.

Resources gain successor presentation configuration so one canonical Resource can be projected to controlled domains:

- General;
- Gestión;
- Equipo;
- Rasgos.

The same Resource identity/value remains the authority regardless of where it is later rendered.

## A5 — valuables, tab order and app-owned background media

Added per-character successor preferences for:

- free-text `Gemas / arte` / valuables content;
- durable character-tab order using stable shared tab keys.

Added two persistent Trasfondo image slots:

- `PRIMARY`;
- `SECONDARY`.

Images use app-owned encoded payload metadata rather than depending permanently on an external gallery URI. Android UI work later in the plan remains responsible for resizing/compressing selected images before persistence.

This design deliberately favors reliable reopen/restart and own-format backup/import for the two character images.

## A6 — migrations, backup/import and compatibility

### SQLDelight migrations

Successor evolution is additive and preserves already-generated intermediate upgrade paths:

- migration 9: initial successor structures;
- migration 10: successor preferences + background media;
- migration 11: compatibility ownership correction for successor child extensions.

Migrations 9/10 were not rewritten after they had already produced CI APKs; later corrections use a new migration number.

### Critical compatibility defect found and repaired

During the Increment A audit, the legacy repositories were found to rebuild several child collections using delete + reinsert while reusing durable IDs.

The first successor schema made some extension rows child-FK-owned with `ON DELETE CASCADE`. That could silently erase successor state during an ordinary legacy save for:

- custom-skill ability configuration;
- spell-source casting configuration;
- structured combat damage;
- Resource successor configuration.

Migration 11 corrects this architecture.

Those extension rows are now:

- owned by `character_id` with character-delete cascade;
- logically keyed by durable child ID;
- validated against current live child IDs by `CharacterSuccessorRepository`;
- not cascade-owned by legacy child rows that are routinely rebuilt.

Regression tests perform actual legacy core and closure delete/reinsert saves and verify the successor extensions survive.

### Character backup format

Own-format character backup evolved from v1 to v2.

New v2 backups include `CharacterSuccessorState` explicitly.

Compatibility rules:

- v1 backups remain decodable/importable;
- missing successor state in v1 safely defaults empty and compatibility projection rebuilds legacy-derived successor views;
- imported characters are restored as independent copies;
- successor relational IDs are remapped rather than reused;
- custom attributes, custom skills, spell sources, attacks, Resources, Markers and background image IDs are remapped consistently;
- background image payload/MIME/slot/original filename are preserved while image identity changes;
- repeated import of the same backup produces independent copies.

## Representative automated coverage

Increment A tests now cover at least:

- custom ability resolution/modifier math;
- custom-skill totals using custom attributes;
- optional custom saving throws;
- per-source spellcasting compatibility and round-trip;
- legacy free-text combat damage preservation;
- structured damage round-trip;
- Marker binary/current-max/recovery validation;
- Resource multi-domain placement;
- dangling successor-reference rejection;
- migration 9/10/11 preservation;
- legacy repository save/reopen survival;
- valuables/tab-order persistence;
- two persistent background image slots;
- backup v1 compatibility;
- backup v2 successor remapping;
- independent repeated imports.

## Acceptance boundary

This checkpoint means:

- domain/schema/storage for Increment A is green;
- compatibility paths are explicitly tested;
- successor structures are safe enough to begin shared UI primitive work.

It does **not** mean:

- successor UI has been owner-auditioned;
- custom attributes are already visible throughout the legacy editor;
- the old 40700 visual findings are repaired;
- phone landscape/tablet presentation is accepted;
- a new formal M6 candidate exists.

## Exact next action

Proceed to **Increment B — shared UX and responsive primitives**, beginning with the form-factor-aware shell and centralized successor density/spacing primitives before broad tab conversion.
