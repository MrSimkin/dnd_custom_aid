# D-0047 — Quick Magic spellcasting reference

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

V4 phone QA showed that `CD de salvación de conjuros` is poorly placed as a lone spellcasting value inside `Referencia de combate`.

The owner supplied a crop from the custom paper PC sheet as design/grouping inspiration. That reference groups quick spellcasting information into one compact area: spell-slot tracking by level together with spell save DC, spell attack modifier and spellcasting ability.

This is a presentation/reference decision, not a request to turn the character sheet into a guided spellcasting or character-building system.

## Approved direction

For the next character-sheet follow-up build:

- remove `CD de salvación de conjuros` from `Referencia de combate`;
- add a separate compact **Quick Magic** reference block;
- use the supplied paper-sheet crop as grouping inspiration, not as a literal Android layout specification;
- include spell-slot tracking by spell level with a compact way to represent available/total slots and mark spent slots during play;
- include **CD de salvación de conjuros**;
- include **Modificador de ataque mágico**;
- include **Aptitud mágica** / spellcasting ability.

## Placement

The owner considered placing Quick Magic either at the bottom of the general character view or in a new dedicated tab and delegated the choice to assistant recommendation.

**Approved placement for the next build: bottom of `Resumen`.**

Rationale:

- Quick Magic is currently a compact **reference** block, not a spell-management workspace;
- keeping it in `Resumen` preserves the at-a-glance character-sheet role and avoids creating a third top-level tab for a small amount of data;
- placing it at the bottom keeps spellcasting-specific information semantically separate from general combat reference without cluttering the upper core-reference area;
- if spellcasting later expands materially (spell lists, prepared spells, more detailed resource management, etc.), a dedicated tab may be reconsidered then rather than being introduced prematurely.

## Automation boundary

For this slice the Quick Magic data remains **manual**.

Do not infer slots from class/level, do not auto-calculate spell save DC or spell attack modifier, and do not add spell-list management, preparation legality, multiclass spell-slot calculation or other spellcasting-builder behavior merely because this block exists.

The purpose is fast reference and better semantic grouping.

Future deterministic spellcasting calculations may be considered separately if they later provide clear value and remain consistent with the project principle of calculation assistance rather than rules enforcement.

## Relationship to combat reference

`Referencia de combate` remains focused on immediate general combat/health reference. Spellcasting-specific values move to Quick Magic rather than competing for space inside the general combat cluster.

## Consequence for V4 QA

The current run #107 APK is not expected to contain Quick Magic. Current-build QA continues unchanged. This decision is an approved **next-build change** and must be tested on the subsequent APK before final Phase 4 acceptance.
