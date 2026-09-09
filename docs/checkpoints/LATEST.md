# Latest project checkpoint

**Updated:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Repository state:** night-close consolidation through Increment E  
**Phase:** Phase 4A successor repair/refinement cycle  
**Release status:** debug / development; NOT owner-accepted and NOT release-ready  
**Latest owner-auditioned practical identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner phone:** Redmi Note 11 Pro 5G  
**Tablet acceptance:** pending; tablet/wide redesign remains required  
**DM implementation:** blocked until Phase 4A is later accepted and explicitly closed

## Increment status

1. A — schema/domain/storage foundation: **COMPLETE / GREEN**;
2. B — shared UX/responsive primitives: **COMPLETE / GREEN**; drag feel still requires owner-device acceptance;
3. C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
4. D — Combat + Dice: **COMPLETE / GREEN**;
5. E — Gestión + Markers + Resources + cross-domain rest/conditions: **COMPLETE / GREEN**;
6. **F — Conjuros compact source-context redesign: NEXT**;
7. G — Equipo/Rasgos/conditional modules/Notas/Trasfondo;
8. H — full-screen Application Settings/live previews/themes;
9. I — separate tablet portrait/landscape redesign.

Four planned increments remain: **F, G, H and I**.

## Read next

1. `docs/checkpoints/2026-09-09_NIGHT_CLOSE_AFTER_INCREMENT_E.md` — continuity package for the next session;
2. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_E_MANAGEMENT_RECOVERY.md` — completed E scope and final green gate;
3. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_D_COMBAT_DICE.md`;
4. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_C5_HABILIDADES.md`;
5. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_C3_C4_GENERAL.md`;
6. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_C2_PC_SETTINGS_INFORMATION_ARCHITECTURE.md`;
7. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_B_SHARED_UX_PRIMITIVES.md`;
8. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling A–I plan;
9. `docs/PROJECT_STATE.md` — compact current-state snapshot;
10. `docs/BRANCH_STATUS.md` — branch interpretation and cleanup status.

## Latest automated product boundary — Increment E

Active successor Gestión wiring source:

`4b3ab53faada5af7b50f73ce951fe767c13ff63a`

Authoritative validation commit:

`0587db5e65d89e809f138e83d053903659216886`

Final integrated workflow:

`34307068166` — **SUCCESS**

Verified together:

- backend/type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10087074946`;
- name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:55bba08d09918a3f6102e4e2694da50c0ee5bb6e9bf3b1ac4c8849f9203ac50`.

This is a technically verified development boundary, **not owner visual acceptance** and not a formal M6 candidate.

## What Increment E now means

- compact fixed Gestión operational state;
- one-row death saves when applicable;
- General/Gestión canonical Inspiration and explicit unsaved-General-vs-persisted-state signaling;
- Custom Markers remain semantically distinct from Resources but share reusable tracker/recovery mechanics;
- Resources support controlled placement in General/Gestión/Equipo/Rasgos from one canonical value;
- mixed Resource + Marker rest preview/apply uses typed identities and explicit structured rules only;
- legacy/manual recovery text remains review-only;
- custom conditions remain available;
- predefined-condition catalog infrastructure exists with stable key/source/help but contains no unapproved corpus text;
- concentration help explains the Constitution-save DC reference through the global contextual-help mode.

## Protected owner directions

- `Raza`, never `Especie/raza`;
- `Electrum`, never `Electro`;
- one datum / one canonical state across tabs;
- phone landscape remains a phone interaction model;
- tablet/wide UI requires a separate redesign and is not the phone-landscape fallback;
- contextual help is one canonical explanation rendered as `Siempre visible`, `ⓘ / tooltip`, or `Oculto`;
- provenance uses `Tipo de origen | Origen específico`, default `Clase`, only where it has real user-facing value;
- generic `Fuente` schema leakage should not be reintroduced;
- app-wide compactness, row efficiency, margin/padding reduction and card movement feedback remain controlling;
- drag/card feel is still pending owner real-device judgment.

## Exact next action — Increment F

Do **not** restart A–E and do not retest build 40700 screen-by-screen.

Start from the reconciled plan's Increment F:

1. replace the separate permanent spell-source selector + toolbar stack with one compact sticky source-context bar;
2. selected source clearly owns ability / `CD salv. conjuro` / `Mod. ataque mágico`;
3. `Todos los conjuros` does not permanently display every source's statistics;
4. search/filter/source details expand transiently rather than consuming permanent rows;
5. preserve useful sticky level/slot context while keeping spell cards visible;
6. phone landscape must remain usable and must not fall back to the old tablet UI;
7. then produce the planned early targeted Redmi portrait + landscape interaction build/audition.

## Branch/night-close discipline

The night-close consolidation aligns canonical `main` and `implementation/phase4a-successor-cycle` at the same completed-E development baseline by normal fast-forward. Non-frozen obsolete `tmp/*` refs are archived by name/SHA and removed from the visible branch list; the explicitly frozen QA branches remain immutable.

On resume, verify branch heads if needed, then continue F on `implementation/phase4a-successor-cycle`. Do not begin DM work.
