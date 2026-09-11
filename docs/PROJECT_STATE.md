# Project State

**Last verified:** 2026-09-11  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Current implementation boundary:** planned successor A–I plus post-audition Player stabilization remain automated-green; owner phone QA has now identified acceptance blockers requiring a bounded repair pass  
**Current phase:** Phase 4A — successor acceptance repair / closure preparation  
**Current QA build:** `0.4.0-preqa.8` / `40800` / debug  
**Release status:** not owner-accepted; not release-ready  
**Owner phone QA:** consolidated pass completed through portrait, landscape and representative larger text  
**Player tablet QA:** intentionally deferred until shared/systemic phone findings are repaired  
**DM work:** design/discovery may remain documented; implementation is blocked until explicit Phase 4A closure

## 1. Canonical repository reality

`main` remains the canonical baseline and has not been advanced by the later successor implementation/stabilization sequence. The active continuation branch contains the current A–I successor implementation, post-audition stabilization, the future-DM design baseline D-0068 and the newly recorded owner/device QA findings.

Use:

- `docs/checkpoints/LATEST.md` for the exact continuation position;
- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md` for the controlling owner/device QA findings and repair scope;
- `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md` for exact `preqa.8 / 40800` product/audit/full-gate evidence;
- `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md` for future DM design truth only;
- `docs/TESTING.md` for the acceptance contract.

Automated-green, documented or canonical does not mean owner-accepted or release-ready.

## 2. Successor implementation status

The planned A–I implementation sequence remains complete/automated-green:

- A — schema/domain/storage foundation;
- B — shared UX/responsive primitives;
- C — character-first navigation + PC Settings + General/Habilidades;
- D — Combat + Dice;
- E — Gestión + Markers + Resources + recovery/conditions;
- F — compact Conjuros source-context redesign;
- G — Equipo/Rasgos/conditional modules/Notas/Trasfondo;
- H — full-screen Application Settings/live previews/themes;
- I — separate tablet portrait/landscape redesign;
- post-A–I stabilization — Rasgos provenance, reorder fallback, Settings/font/theme refinements, app-wide multiline density and residual audit.

The owner/device QA of `preqa.8 / 40800` demonstrated that several automated-green assumptions did not satisfy the intended interaction contract. The next work is therefore a **bounded acceptance-repair pass**, not a new speculative feature increment.

There is no authorized Player Increment J and no authorized DM implementation increment.

## 3. Latest technically verified build

Current consolidated Player QA build:

- version `0.4.0-preqa.8`;
- build `40800`;
- type `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- workflow `34430548061` — **SUCCESS**;
- artifact ID `10134364621` / `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:b7ead12a7501bbef96f861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK digest `sha256:bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

This remains the latest technically verified build. It is **not** an accepted M6 candidate.

## 4. Owner/device QA completed on preqa.8

### PASS evidence

The owner physically verified:

- in-place install over the previous QA app/data without clearing data;
- campaigns/characters and representative Player data survived;
- full close/reopen persistence succeeded;
- phone portrait baseline navigation is broadly good;
- representative editor keyboard / Save / Cancel / Delete / persistence function works;
- currency works;
- Conjuros portrait source/context behavior is good;
- Notas normal browsing/editing/search is good;
- Application Settings is functionally understandable;
- representative conditional modules work;
- representative backup/export works;
- phone landscape remains a phone interaction model;
- representative larger application text is usable.

### Acceptance blockers / repair themes

Owner QA established the following repair requirements:

1. **Canonical HP/state:** General HP changes do not reliably propagate to Combate. One datum must have one authoritative state.
2. **Damage/healing UX:** current frequent combat workflow is too cumbersome.
3. **Combate fixed reference footprint:** consumes too much viewport, especially in short-height landscape.
4. **Direct reorder:** reject the temporary one-column `Reordenar -> Listo` system; reorder must be direct drag-and-drop in the normal active card layout.
5. **Rasgos provenance:** `Clase`, `Subclase`, `Raza` and `Trasfondo` must be selected from this character's canonical data and must not require retyping; `Otro` and `Don` may be free text.
6. **Trasfondo photos:** restore the previous preferred photo UX while keeping the new durable persistence/storage/backup behavior.
7. **Shared editor sizing:** preserve IME safety but stop making nearly every short editor effectively full-height.
8. **Attack damage editor UX:** structured model works, but the labels/interaction are awkward and need a more direct compact dice/component grammar.
9. **Compactness:** current slider disproportionately changes inter-box gaps relative to the rest of the interface.
10. **Theme selector:** retain the current good representative preview but restore a simple three-color palette shorthand alongside the theme name.
11. **Contextual help icons:** behavior is good; `ⓘ` positioning/alignment needs refinement.
12. **PC Settings:** functionally sound but visually/information-architecturally poor; perform one coherent page-level redesign rather than many micro-patches.
13. **Modo mesa:** cannot be activated even after saving/cleaning the sheet; this is a functional blocker.
14. **Vista supercompacta:** current concept is rejected; redesign it as a dense at-table PC reference using the scanning/information grammar of a modern D&D 5.5e monster/NPC stat block rather than primarily a Favorites dashboard.
15. **Phone landscape sticky/fixed UI:** sticky regions consume too much or effectively all usable height.
16. **Conjuros landscape:** sticky `Nivel` box becomes obstructive.
17. **Vertical spacing:** margins/padding already inconvenient in portrait become a major problem in landscape; responsive density must consider available **height**, not only width/orientation.
18. **Custom Habilidad layout:** custom skills must visually match ordinary skills except for the approved italic distinction.

The full detailed classification and repair rules are in `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`.

## 5. Cross-device repair rule

Physical tablet portrait/landscape QA has **not yet been performed** on `preqa.8`.

The owner explicitly requested that the next fixes **not be limited to phone**. Shared/systemic defects found on phone are therefore repair requirements across phone and tablet wherever they share state, component, primitive, spacing policy, special-mode logic or product concept.

This includes at minimum:

- canonical state authority;
- editor sizing;
- reorder behavior;
- provenance selection;
- damage/healing operations;
- compactness/spacing;
- PC Settings UX/IA;
- theme selector presentation;
- Table mode;
- Supercompact;
- sticky/fixed-region responsive height policy;
- Trasfondo photo interaction where shared.

Evidence must remain precise:

- **repair scope:** phone + tablet for shared/systemic causes;
- **physical evidence:** phone only so far;
- **tablet acceptance:** still pending and must be performed on the repaired build.

## 6. Automated-boundary corrections required

Owner QA exposed areas where static/model validation was too weak. The next repair pass must add stronger validation for actual behavior:

- HP propagation across General/Combate/shared surfaces;
- real Table-mode activation from a clean persisted state;
- Rasgos no-retyping structured-origin flow;
- direct normal-layout drag-and-drop reorder behavior;
- short-height phone-landscape sticky/fixed viewport behavior.

Automated tests must support owner acceptance rather than merely prove internal helpers exist.

## 7. Future DM boundary

D-0068 remains the future Phase 4B design baseline. No DM product code was implemented during Player QA.

Protected DM directions remain:

- live DM Combat Desk is tablet-landscape only;
- initiative remains always visible but independent of selected reference;
- current 5.5e monster-stat-block reading grammar is preferred;
- DM state is private by default;
- grids/maps/tokens/automatic targeting/range/encounter balancing/VTT combat execution remain non-goals.

Do not begin DM implementation before explicit Phase 4A closure.

## 8. Protected owner directions

These are controlling:

- one datum / one canonical state;
- compact compatible controls rather than unnecessary vertical stacks;
- reduce unnecessary margins/padding without shrinking required touch targets;
- responsive policy must consider available height as well as width;
- phone landscape remains a phone interaction model, not tablet UI;
- tablet portrait and landscape remain independently designed first-class compositions;
- shared/systemic defects are repaired across phone and tablet, not patched only where first observed;
- direct drag-and-drop in the normal layout is the reorder target;
- known structured Rasgos provenance comes from canonical character data; `Otro` and `Don` may be free text;
- restore preferred old UX when persistence was the actual requested change;
- Supercompact should read like a dense PC stat block inspired by the 5.5e monster/NPC reading grammar;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- contextual help remains `Siempre visible` / `ⓘ / tooltip` / `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- no DM implementation before explicit Phase 4A closure.

## 9. Exact next position

Next project session:

1. read `docs/checkpoints/LATEST.md`;
2. read `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`;
3. discuss the grouped QA findings with the owner and settle only the remaining implementation details;
4. define a bounded acceptance-repair plan across shared phone/tablet surfaces;
5. implement only those accepted repairs on `implementation/phase4a-successor-cycle`;
6. strengthen the exact automated boundaries listed above;
7. produce the next monotonic successor QA build;
8. perform targeted phone retest of repaired blockers;
9. once shared/systemic phone issues are acceptable, perform tablet portrait and tablet landscape physical QA on the repaired build;
10. freeze the replacement formal M6 candidate only after owner-audited behavior is acceptable;
11. complete final regression/upgrade acceptance;
12. explicitly close Phase 4A;
13. only then may DM implementation begin.
