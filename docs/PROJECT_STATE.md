# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4  
**Phase 4 durable line:** `implementation/character-data-foundation`  
**Focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure — expanded scope approved and class/subclass audit complete  
**Open review:** implementation map/gate plan not yet frozen  
**Status:** Product/design scope for the new closure build is owner-approved. Do not resume owner QA against the previous correction APK. Next action is implementation planning, then incremental implementation and a new phone+tablet QA APK.

## 0. Current continuity checkpoint

Primary resume document:

`docs/checkpoints/2026-09-03_PHASE4_CLOSURE_SCOPE_APPROVED_AND_AUDITED.md`

Authoritative supporting records:

- `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved closure product/design scope;
- `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — official class/subclass audit and conditional-module map;
- `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_PROPOSAL_REVIEW.md` — historical proposal checkpoint, explicitly superseded;
- `docs/handoffs/2026-09-03_PHASE4_QA_DEFERRED_CHECKPOINT.md` — historical state before the owner began checking the correction APK;
- `docs/handoffs/2026-09-03_PHASE4_CORRECTION_CANDIDATE.md` — historical correction-candidate evidence.

## 1. Why the old QA continuation is obsolete

The previous correction APK was:

- tested commit `6ae415d8919efb865d7b22092d95b94b3fa7866a`;
- workflow `33710347091`;
- artifact `9876725270`.

The owner began checking it and identified cross-cutting issues plus a desired final expansion of the PC foundation. The owner therefore does **not** intend to run another focused acceptance pass on that APK.

Artifact `9876725270` is historical evidence only. Do not tell the owner to resume the old nine-step focused correction retest unless they explicitly request historical testing.

The next owner QA target must be a newly built closure APK with its own exact tested commit/workflow/artifact identity.

## 2. Approved closure scope

The owner approved all previously retained QA/new requirements plus all agent proposal groups:

- F01–F18 features;
- D01–D18 design directions;
- I01–I22 improvements.

Important owner refinements:

- character lifecycle status moves from General to PC Settings;
- F05 Short/Long Rest support belongs in a new general character-management surface with related live-maintenance features (working Spanish label `Gestión`);
- D14 haptic feedback is configurable in PC Settings;
- D16 context preservation is a general UX rule wherever technically feasible, not tablet-only;
- the next APK is explicitly a phone **and tablet** build and must be QA-tested in portrait and landscape on both;
- the experimental `Vista supercompacta` is included and judged during QA rather than treated as visually final beforehand;
- official classes/subclasses, including Artificer and supplemental official material outside the SRDs, are first-class identity/catalog input while remaining permissive for custom/manual content.

Full detail: `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md`.

## 3. Approved general/conditional character surfaces

General reusable surfaces:

- General;
- Habilidades;
- Combate;
- Equipo;
- Trasfondo;
- Rasgos;
- Conjuros when enabled/relevant;
- Notas;
- Gestión (working label).

Conditional reusable modules identified by audit:

- Artífice / Artifice;
- Formas / Forms;
- Técnicas / Techniques;
- Metamagia / Metamagic;
- Pactos / Pacts;
- Compañeros / Companions.

Multiclass characters use the union of relevant modules. PC Settings permits manual module overrides for custom/homebrew characters. Hidden modules retain data.

## 4. Class/subclass audit state

Audit completed on 2026-09-03 against current official-source availability and legacy official material needed for representation.

Current official-source families explicitly included:

- Player's Handbook 2024;
- Eberron: Forge of the Artificer 2025;
- Forgotten Realms: Heroes of Faerûn 2025;
- Ravenloft: The Horrors Within 2026;
- Arcana Unleashed official D&D Beyond source content, already present as of 2026-09-01;
- legacy 2014-era official subclass families for backward-compatible/manual representation.

Playtest Unearthed Arcana and partnered third-party products are not treated as released Wizards official catalog content, but remain representable through the custom/manual path.

Detailed mapping: `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`.

## 5. Phone + tablet acceptance boundary

Minimum manual QA matrix for the future closure APK:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger app text scale where practical.

Responsive behavior must be based on available window width. Tablet should exploit width with additional columns, navigation rail and master-detail layouts where useful rather than stretching phone UI.

## 6. Focused branch prototype state

Before the owner stopped implementation to require design proposals first, preliminary data/schema/catalog work was committed on `implementation/phase4-character-closure`.

Prototype code checkpoint:

- `89aad12a094476c7b6798f6f0626bf978a5d0831`;
- CI run `33779104922` — PASS.

That green result is useful technical evidence but **not product acceptance**. Before reusing prototype code, compare it to D-0047 and the class/subclass audit. Existing implementation must be changed or discarded where it does not match the approved design.

Subsequent closure-branch commits are documentation/consolidation unless explicitly recorded otherwise.

## 7. Existing Phase 4 baseline that must be preserved/regressed

The durable Phase 4 character work already contains the character editor and persistent domains developed before this expansion, including:

- General and Habilidades;
- Combate;
- Equipo;
- Trasfondo with persisted Raza and Religión / Fe;
- Rasgos;
- conditional Conjuros with sources, prepared state and shared slots;
- Notas;
- PC Settings and spellcasting hide-not-delete behavior;
- derived-value/adjustment model under D-0046;
- SQLDelight persistence/migrations and prior automated regression coverage.

The closure build extends and repairs this foundation; it must not silently regress existing persistence or migration safety.

## 8. Governance/continuity cleanup still relevant

Long-lived root/core documents contain some Phase 3/early-Phase-4 status drift. Reconcile relevant current-status prose before eventual Phase 4 merge proposal.

The older debug-signing wording contradiction also remains to be reconciled before merge: CI uses a stable development-only debug signing identity for update-in-place migration testing while older broad wording prohibits committed signing-key material. Do not expose key material or silently invent a new policy.

Historical/tmp branches remain preserved until the eventual post-merge unique-commit audit.

## 9. Merge boundary

Do not merge Phase 4 to `main` merely because D-0047 is approved.

Before a merge proposal:

- implement the approved closure scope on the focused branch in reviewable increments;
- run the appropriate automated gates including migration/persistence regression;
- identify exactly one final closure QA candidate APK;
- complete owner phone+tablet QA;
- resolve blocking failures and rerun only the affected/regression scope as appropriate;
- reconcile remaining continuity/governance drift;
- preserve exact accepted commit/APK identity;
- obtain explicit owner merge approval.

## 10. Exact continuation rule

**Next action: produce the Phase 4 closure implementation map and gate plan.**

That map must cover:

1. schema/domain/migration increments;
2. PC Settings consolidation;
3. Gestión/live-maintenance surface;
4. class/subclass structured identity and conditional module wiring;
5. new F01–F18 domains;
6. D01–D18 reusable UX/adaptive patterns;
7. I01–I22 existing-surface improvements;
8. global IME/editor/action consistency corrections;
9. phone/tablet responsive increments;
10. final automated gate and phone+tablet owner-QA matrix.

Only after that map is recorded should coding resume from the prototype branch.
