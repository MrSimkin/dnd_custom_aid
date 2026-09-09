# Latest project checkpoint

**Updated:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Repository state:** Increment F repair implemented and automated-green; targeted owner-device repair retest pending before G  
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
6. **F — Conjuros compact source-context redesign: REPAIR AUTOMATED GREEN / TARGETED OWNER RETEST PENDING**;
7. G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: pending and blocked on F repair retest;
8. H — full-screen Application Settings/live previews/themes: pending;
9. I — separate tablet portrait/landscape redesign: pending.

## Read next

1. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` — original F implementation, failed first owner retest, completed repair, full automated evidence, and exact targeted Redmi retest scope;
2. `docs/checkpoints/2026-09-09_NIGHT_CLOSE_AFTER_INCREMENT_E.md` — prior continuity package;
3. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_E_MANAGEMENT_RECOVERY.md`;
4. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling A–I plan;
5. `docs/PROJECT_STATE.md` — compact broader project-state snapshot;
6. `docs/BRANCH_STATUS.md` — branch interpretation and cleanup status.

## Latest automated product boundary — repaired Increment F

Repaired product source commit:

`8bb328153bb0f73ef220afdbf0f53407efe9b2e6`

Integrated validation head:

`6f0d09b2bf00b3f9ace8c10af0dd56dd87ed97a6`

The validation head contains the repaired product source plus only the transient residual-audit helper; that helper subsequently self-removed without changing product code.

Final repaired integrated workflow:

`34392690411` — **SUCCESS**

Verified together:

- backend/type-check: PASS;
- `:shared:desktopTest`: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10120351510`;
- name `dnd-custom-aid-debug-apk`;
- ZIP size `13,230,630` bytes;
- ZIP digest `sha256:6461c90cde69ffa0b3e255721f040553da3acec7041dbeb5216e3e801c49d83d`;
- extracted APK size `37,718,132` bytes;
- extracted APK SHA-256 `5ec1e17298aa6d31fbdb84be0c7bbdd2a8c2ebf7c1fb47994e572207cb998d72`.

This automated-green boundary is **not owner acceptance**.

## F repair summary

The first physical Increment F retest failed and its evidence remains preserved in the F checkpoint. The repaired code now addresses the diagnosed blockers:

- whole-card long-press spell reorder uses shared measured stale-callback-safe interaction logic;
- no visible spell drag handle is required;
- one pointer update cannot intentionally execute several logical reorder steps;
- raw Unicode favorite controls were replaced by shared stable icon controls;
- spell favorite/duplicate/remove controls no longer occupy unnecessary vertical action tiers;
- Conjuros expanded search receives the toolbar row rather than compressing all ancillary controls beside it;
- compact sort preserves Manual/A–Z behavior;
- spell short editor fields are paired (`Nivel + Tiempo`, `Alcance + Duración`);
- shared editor geometry keeps a weighted scrolling body above fixed save/cancel actions with IME insets;
- equivalent obvious compactness improvements were applied conservatively across related character surfaces;
- the residual audit found zero raw favorite controls, zero explicit Android `fontSize` overrides, and zero remaining text `Duplicar` controls.

A residual density audit also found raw fixed paddings elsewhere. They are not mass-rewritten automatically because some are interactive surfaces where required touch geometry must not be degraded merely for visual compactness.

## Owner text-size observation

The app-wide text-size preference is structurally applied through `LocalDensity.fontScale` above `MaterialTheme`, and the Android UI audit found no explicit `fontSize` overrides. Dialog/window text is therefore expected to inherit the same application text scale.

This remains a physical verification item: compare actual glyph size inside an editor/window at 70% and 160%. If glyphs do not change, investigate a Compose dialog/window boundary issue. If glyphs do change but text fields remain visually tall, treat that separately as Material field-frame/internal-padding geometry rather than creating a second typography authority.

## Exact next action — targeted F repair owner retest

Do **not** begin G.

On the Redmi Note 11 Pro 5G, test the repaired APK with focus on:

1. Conjuros portrait source/search/sort/filter/add footprint;
2. Conjuros landscape with ordinary spell-list content still visible;
3. search opening within the same toolbar footprint and collapsed active-search indication;
4. per-source ability/CD/attack display and `Todos` without fake global casting stats;
5. linked-class and custom/unlinked source behavior plus Prepared/filter behavior;
6. whole-card long-press reorder without a handle, without card skipping, and with acceptable physical drag feel;
7. compact spell favorite/duplicate/remove presentation;
8. spell editor short-field rows and keyboard reachability, including fixed save/cancel actions;
9. spell-level `0 -> 5` input behavior through shared numeric normalization;
10. materially reduced window/editor vertical whitespace without impractical touch targets;
11. actual in-window glyph-size comparison at text scale 70% versus 160%.

This is an **early targeted F-repair retest**, not final Phase 4A acceptance and not tablet acceptance.

## Protected owner directions

- one datum / one canonical state across tabs;
- phone landscape remains a phone interaction model;
- tablet/wide UI requires a separate redesign;
- app-wide compactness means naturally compatible controls share rows rather than stacking by default;
- whole-card drag is the intended interaction where safe; a visible handle is not the required initiation mechanism;
- do not shrink required touch targets merely to make layouts look compact;
- contextual help remains one canonical explanation rendered as `Siempre visible`, `ⓘ / tooltip`, or `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- no DM feature implementation before explicit Phase 4A closure.