# Phase 4 — Batch H3 Compañeros closure

**Date:** 2026-09-03 owner local time / 2026-09-04 UTC  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN — Batch H3 complete  
**Canonical `main`:** untouched

## Purpose

Close the final conditional reusable character-module family approved by D-0047 by adding the Android `Compañeros` surface, joining companion data to the existing structural character draft/Save/Discard flow, and preserving the previously verified multiclass/module-union behavior.

## Durable ownership boundary

The preceding H3 audit remains controlling:

- `CharacterCompanion` is durable character-sheet reference/state;
- it is not a DM initiative/combatant authority;
- a future live encounter may project/copy/reference a companion, but encounter state remains separate;
- the existing durable companion model is sufficient;
- no SQL schema migration was required for H3.

## H3a shared gate

H3a was already complete before Android UI integration resumed.

Controlling H3a head:

- `bba16529c1a50317a377f6da9ee8c72d54926522` — `test: cover Batch H3 companions and module union`.

Verification:

- workflow `33827147845` — PASS;
- pure companion search/filter/Manual-A–Z/reorder/duplicate operations covered;
- representative companion module triggers and module-union behavior covered;
- manual show/hide override regressions covered;
- repository round-trip and linked-class soft-unlink behavior covered.

## Android implementation

Implementation was developed on safety branch `tmp/phase4-h3-companions-ui` and fast-forward promoted only after the exact integrated head passed the full repository gate.

Productive commits:

- `60bd67bae66c49c837e260aa0d69d43b74ef95dd` — add `Compañeros` Android collection/editor UI;
- `0d19650723cd7788c6d500a337ce56f6ef50974e` — extend the existing conditional-module structural draft codec with companions;
- `7ddff4c790089760a6ac0b8db4f5ed5dfb64eadb` — expose conditional `Compañeros` navigation;
- `4590ec0e584b8b72fe7b4ce82eb01a00d44de2c8` — wire companion draft/persistence/Quick Access pruning and module surface into the character editor.

Delivered behavior:

- one conditional `Compañeros` destination driven by the existing class/subclass suggestion + PC Settings override union;
- Manual and A–Z presentation; A–Z never rewrites saved manual order;
- search across companion reference fields;
- Active, Favorite, source and kind/type filters;
- visible drag/reflow + configurable haptics only in clean Manual mode;
- duplicate with fresh UUID and appended manual order;
- named delete confirmation;
- row tap edits;
- Favorite/Quick Access via `CharacterQuickAccessKind.COMPANION`;
- new/duplicated unsaved companions cannot be Favorited until they are durably saved;
- phone uses the reusable IME-safe editor dialog;
- wide/tablet uses list + persistent right-side editor while preserving list search/filter/order context;
- optional linked class;
- freeform kind/type and source/provenance;
- optional non-negative CA, max/current HP and non-negative temporary HP;
- speed, ability/stat summary, senses/proficiencies, traits/actions, notes and active state;
- no automatic companion rules derivation or legality enforcement.

## Structural draft and Save ownership

H3 extends the existing structural module draft rather than creating a second authority.

The draft now carries:

- all class options used by Artífice/Técnicas/Metamagia/Pactos;
- Formas;
- Compañeros.

Consequences verified by implementation structure:

- companion add/edit/delete participates in global `Cambios sin guardar`;
- Save persists companions together with the other structural module data;
- Discard remains reversible because unsaved companion deletion does not immediately prune its Favorite reference;
- after successful Save, stale `COMPANION` Quick Access references are pruned against the newly persisted companion IDs;
- PC Settings hide/show remains separate from the structural draft and therefore remains hide-not-delete.

The historical technical name `h1ModuleDraft` remains temporarily for risk minimization; its conceptual ownership is now the complete conditional-module structural draft.

## Safety-branch validation note

The navigation-only intermediate commit `7ddff4c790089760a6ac0b8db4f5ed5dfb64eadb` produced workflow `33829484236`, which failed Android Kotlin compilation because the newly added enum value `COMPANIONS` intentionally existed before the following editor-wiring commit added the exhaustive `when` branch.

This was an expected transient safety-branch state, not a durable product defect. The complete integrated head superseded it and passed the full gate below. The durable closure branch was never moved to the incomplete intermediate state.

## Controlling H3 gate

Exact tested implementation head:

- `4590ec0e584b8b72fe7b4ce82eb01a00d44de2c8`.

Workflow:

- `33829736046` — **PASS**.

Verified by the standard repository gate:

- backend install/type-check — PASS;
- full shared/Kotlin tests — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- Android debug APK upload — PASS.

Integration artifact:

- name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9921290105`;
- artifact ZIP digest: `sha256:119ffc2376b77ef5ab4dcd1580b03f9deb1fab30a547bf30646bef83efa0199f`.

This artifact is integration evidence only. It is not the future Batch L frozen owner-QA candidate.

## Diff cleanliness check

Comparing the H3a base `bba16529...` to the integrated H3 implementation head showed:

- `CharacterEditorV4.kt`: 18 additions, 0 deletions;
- `CharacterNavigationV4.kt`: 2 additions, 0 deletions;
- the remaining changes are the new companion UI file and the intentional companion draft-codec extension.

This check was performed specifically because the GitHub connector writes full file contents rather than line patches; it confirms that the editor/navigation replacement did not accidentally remove or rewrite unrelated A–H behavior.

## Batch H result

All approved reusable conditional module families are now implemented through H1/H2/H3:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

The existing module-union + manual override/hide-not-delete architecture remains controlling.

## Exact continuation

Proceed to **Batch I1 — adaptive shell**.

I1 must be a holistic completion pass, not a rewrite of responsive work already delivered in F/G/H. Focus on the remaining cross-cutting requirements:

1. available-width-driven phone/tablet shell behavior;
2. navigation rail on suitable wide layouts;
3. sticky compact character header;
4. preserve/reuse existing master-detail implementations for list-heavy surfaces and fill only real gaps;
5. verify/complete D16 context preservation, including last-open-tab behavior required by Gate I.

Do not begin Batch I2, backup/import, stabilization or DM work until the dependent gates are reached.