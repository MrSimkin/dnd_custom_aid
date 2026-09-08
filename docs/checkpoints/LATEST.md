# Latest project checkpoint

**Updated:** 2026-09-08  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current owner development package:** D-0067 RECORDED  
**Current reconciliation state:** repository/code audit performed; six consequential owner decisions pending  
**Current product status:** debug / pre-QA; known defects remain  
**Latest technically verified product identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** Stages A–F sufficiently covered; visual acceptance NOT passed  
**Tablet acceptance:** not complete; tablet/wide UX is a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md` — **exact current interaction point** and recommended answers;
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

## Reconciliation audit conclusions already established

The repository answered most D-0067 questions without owner intervention:

- custom skills already exist durably;
- languages and armor proficiencies already share canonical proficiency data;
- Inspiration already has one canonical durable value;
- structured rest/recovery cadence already exists for Resources and should be reused/generalized;
- attack damage is currently one opaque string and therefore structured damage requires a real migration;
- the dice surface already derives standard attributes/saves/skills/custom skills/attacks and should be redesigned around one shared target engine;
- the current ability model is closed over the six standard abilities, so custom attributes are a genuine domain extension;
- spellcasting currently has one global ability/DC/attack modifier despite multiple spellcasting sources, so multiclass casting ownership needs correction;
- the Android app currently starts campaign-first and must be redesigned to character-first without inventing duplicate character-summary state;
- Trasfondo has two image placeholders; persistent background images need app-owned, backup-safe storage.

## Existing QA findings remain active

Do not rerun build `40700` screen-by-screen. Global findings already include app-wide density/row fragmentation, card/reorder interaction, shared IME problems, phone-landscape failure, separate tablet redesign, rotation context loss, fixed-area footprint, `Fuente` cleanup, Consumible/Munición UX, terminology corrections, help/ⓘ behavior, 40% spacing and typography follow-up.

## Exact next action

Answer the six pending product/model questions in:

`docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md`

They cover only:

- custom attribute semantics;
- custom marker/resource reuse;
- spellcasting ability ownership;
- what "armors in Defensas" means;
- `Gemas / arte` data shape;
- exact `Mitos de Cthulhu` condition source.

After those answers:

1. persist the decisions;
2. complete the dependency-aware D-0067 + QA implementation plan;
3. create one focused product branch from `main`;
4. implement in coherent increments rather than piecemeal patches;
5. run full automated gates and targeted owner retests;
6. defer formal M6 until the repaired phone/tablet baseline is acceptable.

No DM-feature implementation begins before the separate Phase 4A closure gate is later satisfied and explicitly approved.
