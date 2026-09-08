# Latest project checkpoint

**Updated:** 2026-09-08  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current owner development package:** D-0067 RECORDED  
**Current reconciliation state:** six consequential owner decisions RESOLVED; dependency/build plan is the exact next task  
**Current product status:** debug / pre-QA; known defects remain  
**Latest technically verified product identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** Stages A–F sufficiently covered; visual acceptance NOT passed  
**Tablet acceptance:** not complete; tablet/wide UX is a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md` — despite the historical filename, this file now records the **resolved controlling owner decisions**, including the compact Conjuros source-context proposal;
2. `docs/decisions/D-0067_OWNER_NEXT_CYCLE_CHARACTER_UX_AND_FEATURE_REFINEMENTS.md` — full owner non-QA development package;
3. `docs/PROJECT_STATE.md` — broader authoritative current-state snapshot;
4. `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md` — detailed latest QA/audition findings;
5. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — provenance/source information-architecture follow-up;
6. D-0066 / `docs/BRANCH_STATUS.md` only when repository-history interpretation matters.

## Canonical baseline

D-0066 consolidated the in-progress Phase 4 development reality into `main` by normal non-force fast-forward. Canonical does **not** mean accepted/release-ready.

Old implementation/tmp branches are historical evidence. Frozen QA branches remain immutable.

## Latest verified product code

The full automated gate last passed for product commit:

`43ca1f5662123ce4d355d9d618b0bfba66d17697`

Identity:

- version `0.4.0-preqa.7`;
- build `40700`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

All later canonical changes through this checkpoint are documentation/governance only unless a later checkpoint explicitly records product-code changes.

## Resolved owner decisions

### Custom attributes

Approved as full ability-like statistics with name, abbreviation, score, default D&D-style modifier, custom-skill/dice/spellcasting participation and **optional** saving throw support.

### Custom Markers vs Resources

They remain **logically distinct user-facing concepts**:

- Custom Markers are configured from PC Settings for character-level state such as destiny/stress points;
- Resources remain the existing resource concept and may represent ammunition, food, charges, uses, etc.

They should share reusable tracking/recovery mechanics where appropriate. Resources additionally gain controlled presentation placement so the same canonical resource may appear in General, Gestión, Equipo, Rasgos or another justified tab without duplicated state.

### Multiclass spellcasting

Casting ability, derived `CD salv. conjuro` and `Mod. ataque mágico` belong **per spellcasting source**, including custom/non-class sources. Class-linked sources follow registered class order.

Conjuros must not add another permanent statistics block. The recommended successor design consolidates the current source selector and collection toolbar into one compact sticky source-context bar, for example conceptually:

`Mago (INT) · CD 15 · Ataque +7   ▾   Buscar   Filtros 2   +`

When `Todos` is selected, do not permanently render every source's casting stats. Expanded source details, formulas and filters should be transient/collapsible so the spell list remains visible, especially on phone landscape.

### Defensas

Owner means **currently worn armor / AC-related information**, not armor proficiencies. Surface canonical AC and equipped armor/shield references without inventing automatic AC calculations unsupported by the current structured equipment model.

### Gemas / arte

Use a compact free-form valuables box for this cycle. Detailed valuables may remain ordinary inventory items when individual tracking is needed.

### Mitos de Cthulhu

Requested source identified as **Sandy Petersen's Cthulhu Mythos for D&D 5e**, using the owner's Spanish copy. The condition architecture can support that source, but proprietary Spanish descriptions are not copied/scraped merely because the owner possesses the book; exact bundled text requires owner-provided/project-appropriate source material.

## Reconciliation audit conclusions already established

- custom skills already exist durably;
- languages and armor proficiencies already share canonical proficiency data;
- Inspiration already has one canonical durable value;
- structured rest/recovery cadence already exists for Resources and should be generalized;
- attack damage is currently one opaque string and therefore structured damage requires a real migration;
- the dice surface already derives standard attributes/saves/skills/custom skills/attacks and should be redesigned around one shared target engine;
- the current ability model is closed over the six standard abilities, so custom attributes are a genuine domain extension;
- spellcasting currently has one global ability/DC/attack modifier despite multiple spellcasting sources, so source-specific casting requires a schema/domain migration;
- the Android app currently starts campaign-first and must be redesigned to character-first without inventing duplicate character-summary state;
- Trasfondo background images require app-owned, backup-safe storage.

## Existing QA findings remain active

Do not rerun build `40700` screen-by-screen. Global findings already include app-wide density/row fragmentation, card/reorder interaction, shared IME problems, phone-landscape failure, separate tablet redesign, rotation context loss, fixed-area footprint, `Fuente` cleanup, Consumible/Munición UX, terminology corrections, help/ⓘ behavior, 40% spacing and typography follow-up.

## Exact next action

No further owner product/model question currently blocks planning.

Now complete one coherent dependency-aware implementation plan that combines D-0067, Stage A–F and the Fuente audit. It must define:

1. schema/domain/storage migrations first;
2. shared UX primitives repaired once;
3. surface redesign groups and dependencies;
4. separate phone portrait / phone landscape / tablet portrait / tablet landscape strategy;
5. build/checkpoint boundaries;
6. automated regression coverage and targeted owner real-device retests;
7. conditional/deferred corpus-backed items.

Only after that plan is coherent should one focused product branch be created from `main`. Implement in coherent increments rather than piecemeal patches. Formal M6 remains deferred until the repaired phone/tablet baseline is acceptable.

No DM-feature implementation begins before the separate Phase 4A closure gate is later satisfied and explicitly approved.
