# Mara 50800 — Phase 4 Fantasy-specific repair closure

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** PHASE 4 INTERNAL EXACT-ARTIFACT PROGRESSION PASS / MASTER MATRIX REMAINS OPEN  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Pre-code map:** `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE4_PRECODE_MAP.md`

## 1. Scope closed internally

Phase 4 was restricted to the Fantasy-specific failures from the real owner 50800 QA:

- M50800-13 — preserve Fantasy Combat/Actions semantic table grammar;
- M50800-26 — meaningful semantic identities wrap instead of renderer-introduced ellipsis;
- M50800-27 — Fantasy continuation streams reclaim exhausted sibling space and preserve semantic boundaries.

M50800-28 remains governing: page count is an observation, never a target.

No master-matrix item is formally closed by this checkpoint. Final-candidate reconfirmation remains required.

## 2. Exact validated heads and runs

Renderer/code head used for the final artifact:

- `c6c54540cf3e08ade2cf241d9897fb89f057b80b`
- workflow 4286 / `36617646946` — SUCCESS
- exact proof artifact `11056346834` (`pc-sheet-populated-template-proofs`)

Regression head:

- `cc0bbfc252bd6b481f8246bd08747a903cf21a3a`
- workflow 4287 / `36617673962` — SUCCESS
- Kotlin, backend and hosted-database all SUCCESS.

Exact Fantasy Mara PDF:

- `mara-50800-phase1-classic_dnd_style.pdf`
- 37 pages — observation only;
- SHA-256 `b982bba9fdc0608ecb763f0ccf26639067719403e82319fdd252a0c634886450`.

## 3. M50800-13 — Fantasy Combat/Actions

Exact rendered page 17 preserves the semantic table grammar:

- `TIPO / NOMBRE`
- `RANGO`
- `BONIF.`
- `DAÑO / EFECTO`
- `NOTAS`

All eight Mara combat identities are represented. The final reaction, `Técnica 8 — Descarga prismática`, wraps visibly inside the identity column rather than being flattened into prose.

Disposition: **positive exact-artifact evidence / internal PASS candidate**.

## 4. M50800-26 — semantic ellipsis

The real 50800 QA required meaningful generated identities to wrap instead of being silently replaced by `...`.

Final exact Fantasy artifact:

- text-layer `...` count: **0**;
- Unicode ellipsis `…` count: **0**;
- character name `Mara de los Siete Umbrales` is visible in full;
- Fantasy headers/metadata, Combat, Traits, Resources/Options and Equipment identities no longer use the previous semantic truncation helper;
- renderer contains no remaining `classicSingleLineExcerpt` path and no explicit `"..."` output string.

The Mara runtime regression now rejects renderer-introduced ellipsis explicitly.

Disposition: **positive exact-artifact evidence / internal PASS candidate**.

## 5. M50800-27 — adaptive continuation reclaim

### Resources / Options

Exact pages 26–27 use `RECURSOS - CONTINUACIÓN` full-page geometry after Options exhaust. The empty Options sibling panel is not retained.

### Inventory / Equipment

Exact pages 31–32 use `OBJETOS ESPECIALES / SINTONIZADOS - CONTINUACIÓN` without retaining an empty ordinary Inventory scaffold.

### Notes / References

An intermediate exact artifact exposed a remaining blocker: the final references-only page contained only the anonymous tail `cuando corresponda.`.

That artifact was **not accepted**.

The repair changed reference pagination from flat physical lines to whole logical reference groups. A record that does not fit in the current panel moves as a whole instead of leaving an anonymous tail.

Final exact artifact:

- references-only pages are 36–37;
- the previous orphan page 38 is gone;
- final page 37 begins with owning semantic identities such as `Movimiento`, `Sentido` and `Efecto temporal`;
- no exhausted `NOTAS DE CAMPAÑA` / map sibling scaffold remains on references-only pages.

The regression suite explicitly rejects an anonymous final reference tail.

Disposition: **positive exact-artifact evidence / internal PASS candidate**.

## 6. Semantic preservation

Exact-artifact/text-layer inspection plus regression coverage preserves the Mara semantic identities exercised by Phase 4, including:

- 8 Combat entries;
- 26 Traits;
- 10 Resources;
- 7 custom Markers;
- 8 Options;
- 34 Inventory identities;
- 9 Note titles.

The PDF extractor may interleave wrapped table-column text, so a logical identity may not always appear as one contiguous extraction string; exact rendered inspection was used where required (notably Combat entry 8).

## 7. Regression/Test alignment with owner QA

Historical tests were not allowed to override the real 50800 acceptance criteria.

One historical Fantasy Notes test still required `CROQUIS / MAPA` scaffolding and an exact six-page count. Those expectations conflicted with:

- M50800-27 adaptive reclaim; and
- M50800-28 no fixed page-count target.

The test was updated to validate preserved content and data-driven continuation instead.

New/updated Mara Fantasy regression gates now cover:

- M50800-13 semantic Combat table;
- M50800-26 no artificial ellipsis;
- M50800-27 Resources-only reclaim;
- M50800-27 special-only Inventory reclaim;
- M50800-27 references-only Notes reclaim;
- no anonymous final Notes reference tail;
- preservation of all Mara semantic identities listed above.

## 8. Phase disposition

**Phase 4 internal progression: PASS.**

This does **not** mean:

- the 32-item master matrix is closed;
- an owner APK is ready;
- a merge is authorized;
- 50800 may be reused;
- final candidate QA may be skipped.

## 9. Next bounded work

Resume at **Phase 5 — cross-family acceptance/candidate** from the canonical phase map:

1. all-four-family real-Mara generation;
2. inspect actual generated PDFs;
3. text/data preservation checks;
4. disposition all 32 matrix items from exact evidence;
5. assign a unique next candidate version/build identity;
6. only then prepare owner QA.

Phase 5 must re-confirm prior internal PASS items on the exact candidate rather than assuming earlier phase artifacts are sufficient.
