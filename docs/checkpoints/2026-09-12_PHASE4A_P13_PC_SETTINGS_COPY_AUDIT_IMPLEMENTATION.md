# Phase 4A — P13 PC Settings / explanatory-copy implementation audit

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Authority: `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P13_ROUND2_CLOSED.md`

## Status

P13 IMPLEMENTATION AND FULL-APP COPY AUDIT COMPLETE — NORMAL SCAFFOLD VALIDATION PENDING AT THIS CHECKPOINT REVISION.

Primary PC Settings redesign source already present before this audit:

`0e13f2f6dd4f3e2d5cb74d2758927dd5779dbbf2` — `repair: redesign PC Settings information architecture`

Transversal copy repair:

`e0ca416d15f4141ceb5aa89bf132eeea56a6208f` — `repair: simplify P13 Player explanatory copy`

## PC Settings contract audit

Current `CharacterPcSettingsClosureV4.kt` satisfies the accepted P13 information architecture:

- grouped `Ficha y navegación`, `Contenido personalizado`, `Uso en mesa`, and `Personaje y datos` sections;
- global `Configuración de la aplicación` visibly separated from character-specific settings;
- focused subpages for tab ordering, custom attributes, custom skills, custom markers, and special modules;
- simple settings remain directly operable;
- concise state summaries/counts on navigation rows;
- wide layout uses paired section columns while phone stacks vertically;
- lifecycle confirmation behavior and backup availability messaging preserved;
- no page-level Save button;
- simplified header contains only navigation plus character context;
- P14 Table Mode activation mechanics remain governed separately by the P14 implementation.

## Full-app explanatory-copy audit

A temporary read-only workflow audited all production Android Player Kotlin UI files on the active branch.

Audit run: `34721239366`.

Inventory result:

- 72 production Android Kotlin UI files scanned;
- 136 string literals of 60+ characters inventoried for review;
- 19 `CharacterHelpV4` callsites observed at the audited revision;
- candidate permanent prose was separated from contextual help, confirmations, validation, state/integrity guidance and rule-boundary copy.

### Copy intentionally retained

The audit intentionally retained copy whose meaning is not safely removable, including:

- destructive/lifecycle confirmations;
- required-field and validation messages;
- pending-edit / save-discard guidance;
- A–Z/manual-order guidance that protects persisted reorder semantics;
- recovery/manual-review wording that protects state-integrity expectations;
- dice/rules boundary wording where the app must state what it does not adjudicate;
- unresolved/canonical provenance guidance;
- useful empty-state actions;
- detailed destination-page explanations in Application Settings;
- explanatory material already routed through P12 `CharacterHelpV4`.

### Demonstrated permanent-copy gaps repaired

Repeated split-editor narration was simplified where the UI behavior is already obvious:

- Artifice: `Selecciona un plan o dispositivo.`
- class-option modules: `Selecciona un registro para editarlo.`
- Companions: `Selecciona un compañero para editarlo.`
- Equipment: `Selecciona un objeto para editarlo.`
- Forms: `Selecciona una forma o añade una nueva.`
- Spells: `Selecciona un conjuro o añade uno nuevo.`

The repeated claims that search/filter/order/position remain preserved while editing were removed from permanent body copy.

Additional cleanup:

- removed the redundant Combat sentence stating that quick-reference values are not independent copies; the read-only reference labels already communicate the projection and canonical state is enforced by the implementation;
- shortened the Traits permanent scope subtitle;
- kept concise custom-skill and proficiency scope text while moving calculation/legal-boundary explanation into `CharacterHelpV4`;
- moved saving-throw interaction guidance and standard-vs-custom skill editing guidance from permanent body copy into `CharacterHelpV4`.

This follows the P13 rule: shortest permanent wording that preserves meaning, with genuinely useful explanation available contextually through P12 rather than occupying every normal screen.

## Scope safety

The P13 transversal repair is copy/presentation-only. It does not change character state, calculation rules, structural-edit ownership, Table Mode semantics, Supercompact behavior, persistence, reorder mechanics, or lifecycle behavior.

Temporary P13 audit/repair workflow and script files self-deleted in source commit `e0ca416d...`.

## Validation requirement

P13 is not automation-closed by this document alone. The normal Scaffold gate must pass on a descendant containing `e0ca416d...`, including backend typecheck, the exact Kotlin/shared/Android/Desktop gate, and APK upload. Owner/device acceptance remains part of the later aggregate/P17 process.
