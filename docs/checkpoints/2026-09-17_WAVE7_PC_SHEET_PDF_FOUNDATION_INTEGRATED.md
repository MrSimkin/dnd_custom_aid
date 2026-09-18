# Checkpoint — Wave 7 PC Sheet PDF semantic foundation integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Integrated implementation:** PR #83  
**Merge:** `f6350d34087aae55d5247f2ba23153814eeed04b`  
**Implementation head:** `dea22b0823d6c4ad54e24839943d5952e2020392`  
**Push Scaffold:** `35294590552` — SUCCESS  
**PR Scaffold:** `35294792657` — SUCCESS  
**Post-merge Scaffold:** `35295050340` — SUCCESS  
**Provider action:** none required

## Integrated scope

The shared PC Sheet PDF export foundation is now part of `main`.

It provides one platform-neutral, read-only export plan over the existing canonical PC aggregate:

- export-state selection: Permanent or caller-supplied Current Snapshot;
- explicit safe fallback to Permanent when a distinct Current Snapshot is unavailable;
- four visual families from D-0074;
- exact owner-template page mapping for Custom v1 and both Custom v2 alternatives;
- custom Attribute/custom Skill projection and existing derived totals;
- Extended / App Modified / combined custom-stat presentation modes;
- semantic overflow routes for custom statistics, traits/features, resources/options, inventory/equipment, spells and notes;
- portrait fit choice and non-blocking offline-missing portrait behavior;
- optional Spellbook from spells actually attached to the PC, preserving all known casting-source relationships and source-specific derived values.

## Important non-scope

This package does not:

- produce a physical PDF;
- define final coordinates/fonts/typography;
- expose export UI;
- alter PC persistence or synchronization;
- modify backend/API/provider code;
- trigger Cloudflare deployment;
- claim any visual template is owner-approved.

## Owner template rule

The approved source of truth for Custom v1/v2 rendering remains the owner's actual PDFs in `assets/character-sheets/templates/`.

They are not merely design inspiration and should not be unnecessarily redrawn.

When visual rendering reaches a reviewable state, the owner must receive populated dummy-data examples for:

1. Classic D&D-style;
2. Custom v1;
3. Custom v2 — per Attribute;
4. Custom v2 — per Ability.

Automated tests or empty-template rendering do not satisfy this visual/product approval gate.

## Next bounded package

**PC Sheet PDF Export — local renderer + authoritative template mapping**

Expected branch:

`wave7/pc-sheet-pdf-renderer-template-proof`

The next package should consume the shared plan, produce local/offline static PDF output, map actual owner template geometry, and reach a reviewable populated-example state without redefining canonical PC semantics.

## Deferred unrelated product gap

Freeze/unfreeze remains deferred until its behavior is explicitly defined. Do not equate it automatically with inactive status, deletion/tombstone or a blanket edit lock.
