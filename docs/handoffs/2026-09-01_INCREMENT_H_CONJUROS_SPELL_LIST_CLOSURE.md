# Increment H — Conjuros spell list/details closure

**Date:** 2026-09-01  
**Recovery branch:** `tmp/increment-h-spell-list`  
**Recovered baseline:** `d71a47822bcbeb0d0aa9ba404ba4fd01af81b510` — Increment G closed after green Gate G  
**Gate H tested head:** `df4d9c9ce9dc1744b40c28ba457d214cc5faf7b3`

## Recovery conclusion

The crashed work session had already completed Increment G cleanly. A full audit of surviving branches, repository checkpoints/handoffs, the Git graph, pull requests, authoritative decisions, and the consolidated implementation package found no later accepted Increment H or Increment I implementation that superseded that boundary.

Work therefore resumed from the exact Increment G closure commit rather than from an older named milestone branch or an unverified temporary checkpoint.

## Increment H completed scope

Increment H now implements the approved conceptual spell list/details layer for `Conjuros`:

- conceptual spell list grouped as `Trucos`, then `Nivel 1` through `Nivel 9`;
- collapsible populated level sections and de-emphasized empty levels;
- source-aware filtering for `Todos` and individual spellcasting-source views;
- compact search constrained to the selected view;
- conceptual spell add/edit/delete;
- one-or-more source associations per spell;
- source-specific `Preparado` state;
- `Todos` displays source-specific prepared indicators rather than a misleading universal prepared checkbox;
- multi-source spells remain one conceptual spell and therefore appear once in `Todos`;
- manual long-press drag ordering within each spell level;
- approved permissive/manual fields: name, level, casting time, range, V/S/M, optional material text, duration, concentration, ritual, description, optional notes;
- existing recreation-safe `CharacterSpellcastingDraftV4` and central character Save transaction remain the persistence boundary;
- no database migration was needed because Increment C already established the approved spell/source/association schema.

Quick Magic/shared spell-slot integration is intentionally not part of this increment; that remains Increment I.

## Regression coverage added

`CharacterSpellListPersistenceTest` now exercises:

- complete spell-detail persistence;
- multi-source associations;
- source-specific prepared state;
- within-level spell ordering;
- edit/update persistence;
- conceptual-spell deletion and association cleanup.

The association assertion is intentionally order-independent. SQLDelight returns spell/source associations ordered by `source_id`; association list position has no product meaning. An earlier order-sensitive assertion produced a randomized pass/fail depending on generated UUID ordering and was corrected before closure.

## Gate H evidence

Authoritative corrected run:

- GitHub Actions workflow: `Scaffold checks`
- run ID: `33464451836`
- tested head: `df4d9c9ce9dc1744b40c28ba457d214cc5faf7b3`
- backend job: **PASS**
- shared/Kotlin tests: **PASS**
- Android debug build: **PASS**
- desktop build: **PASS**
- Android debug APK upload: **PASS**
- APK artifact ID: `9784375686`
- APK artifact name: `dnd-custom-aid-debug-apk`
- APK artifact digest: `sha256:0ac5df19f46fecae8d41ee3d332f324289dc85817c80b7acaeb2a752a77d9c47`

An immediately preceding corrected-code build also passed end-to-end. The clean checkpoint run then exposed the randomized order-sensitive test assertion described above; after fixing the assertion to match actual repository semantics, the authoritative run above passed completely.

## Safety/recovery notes

Several safeguards fired during recovery and all failed closed rather than modifying unexpected production state:

1. the first narrow source-tab wiring matcher was too whitespace-specific and aborted before a source edit;
2. the first Android compile exposed one trailing-comma syntax error in the new editor validation expression; the exact defect was corrected;
3. one temporary cleanup workflow had invalid YAML caused by embedded multiline text and was rejected before any job ran;
4. temporary self-edit workflow/trigger files used for narrow recovery patches were removed before the clean candidate;
5. the final Gate H failure was a regression-test ordering assumption, not a production persistence defect, and the corrected deterministic test then passed.

## Acceptance boundary

Automated Gate H is green and Increment H is technically closed.

Manual owner/device QA is still a distinct acceptance boundary for touch ergonomics and presentation, including:

- long-press drag feel on a phone;
- responsive density/spacing;
- keyboard/IME behavior in the spell editor;
- visual clarity of collapsed levels, source indicators, and prepared controls.

Those items are not represented as automated interaction PASS.

## Promotion boundary

This closure is suitable for fast-forward promotion to `implementation/character-data-foundation` after confirming the promotion is a descendant-only update. It must **not** be merged or pushed to `main` without explicit owner approval.

## Next implementation boundary

**Increment I — Quick Magic/shared spell slots.**

Increment I should consume the now-stable conceptual spell/source model and implement the approved shared slot pool plus Quick Magic runtime interaction without duplicating spell ownership or slot state.