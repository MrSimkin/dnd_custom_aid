# Latest project checkpoint

**Updated:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Repository state:** planned successor engineering A–I implemented and integrated automated-green; owner/device acceptance remains pending  
**Phase:** Phase 4A successor acceptance / closure preparation  
**Release status:** debug / development; NOT owner-accepted and NOT release-ready  
**Primary owner phone:** Redmi Note 11 Pro 5G  
**Tablet acceptance:** pending; redesigned tablet portrait/landscape implementation is automated-green  
**DM implementation:** blocked until Phase 4A is later accepted and explicitly closed

## Increment status

1. A — schema/domain/storage foundation: **COMPLETE / GREEN**;
2. B — shared UX/responsive primitives: **COMPLETE / GREEN**; drag feel remains owner-auditionable;
3. C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
4. D — Combat + Dice: **COMPLETE / GREEN**;
5. E — Gestión + Markers + Resources + cross-domain rest/conditions: **COMPLETE / GREEN**;
6. F — Conjuros compact source-context redesign + repair: **AUTOMATED GREEN / TARGETED OWNER REPAIR RETEST STILL PENDING**;
7. G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: **COMPLETE / INTEGRATED AUTOMATED GREEN / OWNER ACCEPTANCE PENDING**;
8. H — full-screen Application Settings/live previews/themes: **COMPLETE / INTEGRATED AUTOMATED GREEN / OWNER ACCEPTANCE PENDING**;
9. **I — separate tablet portrait/landscape redesign: COMPLETE / INTEGRATED AUTOMATED GREEN / PHYSICAL TABLET ACCEPTANCE PENDING**.

There is no planned Increment J. Do not invent additional feature scope before owner acceptance/closure.

## Read next

1. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_I_TABLET_REDESIGN.md` — final planned A–I engineering increment, layout inspection and integrated gate;
2. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_H_APPLICATION_SETTINGS.md` — full-screen Application Settings implementation;
3. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_G_COLLECTION_CONTENT_REPAIRS.md` — collection/content implementation boundary;
4. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` — repaired F and outstanding targeted phone retest;
5. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling A–I plan;
6. `docs/PROJECT_STATE.md` — broader current snapshot.

## Latest automated product boundary — Increment I / A–I integration

Latest I product commit:

`c11ed70260f054a99c7ab5e38f2f772a697db886`

Integrated validation head:

`7fd61ee281404e4f62f41e2b1fb126ca45a2a199`

Normal `Scaffold checks` workflow:

`34420873078` — **SUCCESS**

Verified together:

- backend dependency install/type-check: PASS;
- shared/Kotlin desktop tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10130893215`;
- name `dnd-custom-aid-debug-apk`;
- ZIP size `13,308,575` bytes;
- digest `sha256:9d65bb1e78b5a875ef5d381d56fa5a8038ce1c0ca9b6f87130d80b21d6660194`.

This is the integrated automated-green successor audition boundary. It is **not owner acceptance** and is not a formal M6 candidate.

## Increment I summary

- the existing 600dp short-side classifier still distinguishes phone/tablet and portrait/landscape; no width-only phone-landscape regression was introduced;
- tablet portrait now uses scrollable top tabs and a centered portrait canvas rather than spending portrait width on the old generic side rail;
- tablet landscape uses an adaptive-width side rail and a wider centered canvas;
- the same saveable tab subtree is reused across tablet compositions so the redesign does not create duplicate editor/domain state;
- high text scales reduce effective tablet card columns without changing the saved per-orientation preference;
- browsing no longer reserves idle editor panes in Conjuros, Equipo, Artífice, Formas, Técnicas/Metamagia/Pactos or Compañeros;
- tablet landscape exposes master/detail only while those editors are actually open;
- tablet portrait uses the modal/IME-safe editor path for those collections;
- existing useful wide layouts such as multi-column Notas/Rasgos and Trasfondo's simultaneous content remain intact.

## Outstanding owner/device boundaries

No automated successor gate through I is owner visual/device acceptance.

Repaired F's targeted Redmi Note 11 Pro 5G test is still open because no passing physical evidence has been recorded. The consolidated phone audition should now cover representative G/H/I-adjacent behavior as applicable: Conjuros footprint/editor/drag, persistent Trasfondo images, Notes search/reorder, cross-tab Resources, full-screen Application Settings, slider extremes, themes and haptic differences.

Tablet portrait/landscape implementation is now redesigned and automated-green, but physical owner tablet acceptance remains pending. Repository-level layout inspection was completed; no dedicated tablet screenshot/emulator regression harness currently exists in the repo.

## Exact next engineering position

Planned A–I implementation is complete. The next work is **acceptance and closure**, not another feature increment:

1. expose/install the integrated successor APK for owner audition;
2. complete the outstanding targeted/consolidated phone checks;
3. inspect redesigned tablet portrait/landscape on a representative physical tablet when available;
4. repair only acceptance-blocking defects that are actually observed;
5. freeze a new formal M6 candidate when the owner-audited baseline is ready;
6. perform required regression/upgrade QA;
7. explicitly close Phase 4A.

Only after explicit Phase 4A closure may DM implementation begin.

## Protected owner directions

- one datum / one canonical state across tabs;
- phone landscape remains a phone interaction model;
- tablet portrait/landscape are independently designed rather than stretched phone layouts;
- extra tablet width must provide useful context, not permanent empty panes;
- compact compatible controls rather than unnecessary vertical stacks;
- whole-card drag where safe, without shrinking required touch targets;
- contextual help remains one canonical explanation rendered as `Siempre visible`, `ⓘ / tooltip`, or `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- no DM feature implementation before explicit Phase 4A closure.
