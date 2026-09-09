# Latest project checkpoint

**Updated:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Repository state:** Increment G implemented and integrated automated-green; owner/device acceptance remains pending  
**Phase:** Phase 4A successor repair/refinement cycle  
**Release status:** debug / development; NOT owner-accepted and NOT release-ready  
**Primary owner phone:** Redmi Note 11 Pro 5G  
**Tablet acceptance:** pending; tablet/wide redesign remains required  
**DM implementation:** blocked until Phase 4A is later accepted and explicitly closed

## Increment status

1. A — schema/domain/storage foundation: **COMPLETE / GREEN**;
2. B — shared UX/responsive primitives: **COMPLETE / GREEN**; drag feel still requires owner-device acceptance;
3. C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
4. D — Combat + Dice: **COMPLETE / GREEN**;
5. E — Gestión + Markers + Resources + cross-domain rest/conditions: **COMPLETE / GREEN**;
6. F — Conjuros compact source-context redesign + repair: **AUTOMATED GREEN / TARGETED OWNER REPAIR RETEST STILL PENDING**;
7. **G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: COMPLETE / INTEGRATED AUTOMATED GREEN / OWNER ACCEPTANCE PENDING**;
8. H — full-screen Application Settings/live previews/themes: pending;
9. I — separate tablet portrait/landscape redesign: pending.

## Read next

1. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_G_COLLECTION_CONTENT_REPAIRS.md` — complete G1–G4 implementation, focused evidence and integrated gate;
2. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` — repaired F and outstanding targeted phone retest scope;
3. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling A–I plan;
4. `docs/PROJECT_STATE.md` — current broader snapshot;
5. `docs/BRANCH_STATUS.md` — historical branch interpretation/cleanup context.

## Latest automated product boundary — Increment G

Latest G product commit:

`3b6118e490edc8d57a0097c60d196d4de112722a`

Integrated validation head:

`aa7e57647e4a1b467de028f9d0a0d13e4a1ef0bd`

Normal `Scaffold checks` workflow:

`34413644371` — **SUCCESS**

Verified together:

- backend dependency install/type-check: PASS;
- shared/Kotlin desktop tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10128271891`;
- name `dnd-custom-aid-debug-apk`;
- ZIP size `13,294,132` bytes;
- digest `sha256:1a3aab50c9c5122f94ddaa2b7de173cddd046b74b80f8be58b0ed4ec564bca2f`.

This automated-green boundary is **not owner acceptance**.

## Increment G summary

- Equipo projects canonical CA/equipped state and Equipo-placed Resources, adds `Gemas / arte`, clarifies consumable semantics and uses `Electrum`;
- Rasgos projects Rasgos-placed Resources, uses structured provenance and shared measured multi-column drag;
- Artífice/Formas/Técnicas/Metamagia/Pactos/Compañeros use the compact shared collection toolbar grammar without changing their module-owned state or hide-not-delete behavior;
- Notas now supports title/content search, presentation-only content filters and Manual/A–Z without rewriting stored order; multi-column notes use measured 2-D whole-card drag;
- Trasfondo primary/secondary images now use the existing app-owned successor payload, with resize/transcode, persistent rendering, add/change/remove, save/reopen regression and backup/import regression.

## Outstanding owner boundary from repaired F

The owner directed implementation to continue into G, but no later physical evidence has been recorded that closes repaired F's targeted phone retest. Therefore do not infer or write that F is owner-accepted.

That targeted Redmi check remains useful before practical acceptance, especially:

- Conjuros portrait/landscape usable content footprint;
- source/search/sort/filter/add behavior;
- whole-card spell drag feel/no skipping;
- representative editor/keyboard behavior;
- actual 70% vs 160% glyph-size behavior.

This is acceptance debt, not an automated-G failure.

## Exact next engineering position

Increment **H** is the next implementation increment:

- full-screen Application Settings rather than modal constraints;
- stepped text-size control with explicit value and live preview;
- stepped spacing/compactness control including 40%, with live preview;
- visual column-setting mini-grid previews;
- normal font selection with immediate preview;
- global help-mode setting;
- dice-result presentation setting;
- bounded haptic strength/duration choices with fallback;
- theme renames plus six audition themes: Carmesí, Ámbar, Glaciar, Lavanda, Pizarra and Terracota.

Increment I remains the separate tablet portrait/landscape redesign after H.

## Protected owner directions

- one datum / one canonical state across tabs;
- phone landscape remains a phone interaction model;
- tablet/wide UI requires a separate redesign;
- app-wide compactness means naturally compatible controls share rows rather than stacking by default;
- whole-card drag is the intended interaction where safe;
- do not shrink required touch targets merely for visual compactness;
- contextual help remains one canonical explanation rendered as `Siempre visible`, `ⓘ / tooltip`, or `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- no DM feature implementation before explicit Phase 4A closure.
