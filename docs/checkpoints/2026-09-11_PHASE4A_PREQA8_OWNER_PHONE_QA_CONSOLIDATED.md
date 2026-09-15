# Phase 4A — preqa.8 consolidated owner phone QA

**Date:** 2026-09-11  
**Status:** OWNER PHONE QA COMPLETE ENOUGH TO DEFINE REPAIR PASS / TABLET QA DEFERRED  
**Branch:** `implementation/phase4a-successor-cycle`  
**QA build:** `0.4.0-preqa.8` / `40800` / debug  
**Primary physical device:** Redmi Note 11 Pro 5G  
**Release status:** NOT ACCEPTED / NOT RELEASE-READY  
**DM implementation:** still blocked until explicit Phase 4A closure

## 1. Purpose and stopping boundary

This checkpoint records the consolidated owner/device QA performed on the current Player build `0.4.0-preqa.8 / 40800`.

The pass covered:

- in-place upgrade/data preservation;
- phone portrait baseline/navigation;
- representative editors/IME/persistence;
- General/Habilidades/Combate/Gestión live-state behavior;
- Equipo/Monedas, Rasgos, Conjuros, Notas and Trasfondo;
- PC Settings and Application Settings;
- Table mode and Supercompact;
- phone landscape responsive behavior;
- representative larger application text scale;
- representative conditional-module and backup/export behavior.

Physical tablet portrait/landscape QA was intentionally **deferred** after the phone pass exposed multiple shared/systemic defects. The owner explicitly judged tablet QA inefficient before those defects are repaired.

This checkpoint therefore does **not** claim tablet acceptance evidence. Instead, it establishes a repair-scope rule:

> Any defect whose cause is shared layout, shared component, shared interaction primitive, shared state authority or shared product concept must be repaired across phone and tablet surfaces unless there is a demonstrated device-specific reason not to do so.

Do not interpret the findings below as phone-only merely because they were observed first on phone.

## 2. Overall owner verdict

`preqa.8 / 40800` is substantially healthier than the earlier auditioned build and many major areas now work correctly, but it is **not acceptable for Phase 4A closure**.

The remaining issues are not a request for broad new Player feature scope. They cluster into bounded repair themes:

1. canonical shared-state correctness;
2. high-frequency Combat UX;
3. responsive vertical-space/sticky behavior;
4. direct drag-and-drop reorder interaction;
5. Rasgos provenance selection from canonical character data;
6. restoration of unnecessarily redesigned UX where the previous interaction was already preferred;
7. PC Settings/Application Settings visual and density refinements;
8. Table mode activation correctness;
9. Supercompact conceptual redesign into a stat-block-like at-table reference;
10. adaptive sizing of shared editor surfaces.

No DM implementation is authorized by this checkpoint.

## 3. PASS — upgrade and persistence boundary

The owner installed `preqa.8 / 40800` **over the existing prior QA installation/data** without uninstalling or clearing app data.

Result: **PASS**.

Verified on-device:

- existing campaigns remained available;
- existing characters remained available;
- representative General / Combate / Equipo-Monedas / Conjuros / Notas data survived;
- the app fully closed and reopened correctly;
- persisted data remained intact after restart.

This satisfies the critical first-upgrade safety requirement for this build.

## 4. PASS — phone portrait baseline

Representative phone portrait navigation and general layout were acceptable across:

- General;
- Habilidades;
- Combate;
- Gestión;
- Equipo/Monedas;
- Rasgos;
- Conjuros;
- Notas;
- Trasfondo.

Result: **PASS as a baseline**, subject to the specific defects below.

## 5. Editors / IME

### Functional result

Representative simple and dense/multiline editors passed:

- keyboard usability;
- Save;
- Cancel;
- representative Delete;
- save/reopen persistence.

Functional result: **PASS**.

### Systemic UX defect — editor surfaces are effectively full-screen regardless of content

The owner observed that short/simple editors still occupy essentially the full available screen.

Current shared `CharacterImeSafeEditorDialog` deliberately uses a fill-height surface for IME safety. Functionally this works, but visually and ergonomically it is excessive for small editors.

Classification: **shared UX repair required**.

Repair direction:

- preserve IME safety and button reachability;
- allow short/simple editors to size more naturally/adaptively;
- retain larger/full-height behavior where content genuinely requires it;
- apply the repair to every phone/tablet caller of the shared primitive rather than patching isolated dialogs.

## 6. General / Habilidades / Combate / Gestión findings

### 6.1 BLOCKER — HP canonical-state inconsistency

Owner observation:

- changing HP from General did not propagate correctly to Combate.

This violates the controlling `one datum / one canonical state` rule.

Classification: **Phase 4A functional/state blocker**.

Repair requirement:

- General, Combate and every other HP surface must read/write the same authoritative character HP state;
- no screen-local or stale duplicate must be able to diverge;
- automated coverage must verify cross-surface propagation, not merely independent persistence.

### 6.2 UX defect — custom Habilidad spacing differs from ordinary skills

Owner observation:

- custom skills have visibly different margin/padding from ordinary skills in both `Por habilidades` and `Por atributo`/characteristic grouping.

This contradicts the approved rule that custom skills participate inline as ordinary skills, with italics as the intended visual distinction rather than a different container geometry.

Classification: **non-functional but required UX correction**.

### 6.3 Attack structured-damage editor — works, but interaction is awkward

Current behavior supports typed expressions such as `1d8`, `2d6`, `3d4`, flat values and text components.

Owner observation:

- the dice field being labelled with an example such as `1d8` rather than a semantic label feels strange;
- dice combinations must be typed rather than selected/manipulated through a direct dice-oriented control;
- flat field presentation such as `+3` also reads more like an example than a clear interaction model;
- the functionality works, including rolling, but the UX feels wrong.

Classification: **owner-rejected attack-damage UX; repair required before closure**.

Repair discussion should prefer a more direct compact component grammar while preserving the structured damage model and support for arbitrary valid expressions.

### 6.4 BLOCKER — damage/healing registration workflow

Owner observation:

- `Daño` / `Curar` is uncomfortable for a frequent in-combat operation;
- the current path effectively opens a large editor/dialog, requires typing an amount and saving.

Classification: **Phase 4A high-frequency core UX blocker**.

Repair direction:

- damage/healing must be much faster and more direct;
- retain canonical HP operations and correct temp-HP/healing rules underneath;
- do not sacrifice correctness for speed;
- apply the improved operational interaction anywhere the same shared HP operation is exposed, including tablet.

### 6.5 BLOCKER — Combate fixed quick-reference footprint

Owner observation in portrait:

- at normal `Compactación de espacios = 100%`, the fixed Combate reference region occupies roughly 60% of the screen.

This is not solved by telling the user to use 40% compactness. `100%` is the default/baseline spacing setting and must remain usable.

Classification: **Phase 4A responsive/viewport blocker**.

This later became even more severe in phone landscape and is part of the systemic sticky/vertical-space repair theme below.

## 7. Equipo / Monedas / Rasgos / reorder

### 7.1 PASS — currency

Owner result: **currency behavior is acceptable**.

### 7.2 BLOCKER — current reorder system is owner-rejected

Owner verdict:

> the new reorder system is plain awful

The implemented fallback temporarily changes configured multicolumn browsing into a one-column reorder mode behind `Reordenar`, then requires `Listo` to return.

This is not the desired interaction.

Definitive owner direction:

- cards should be directly reorderable through drag-and-drop in their **normal presentation/layout**;
- do not transform the collection into a special one-column mode merely to reorder;
- avoid `Reordenar -> transformed layout -> Listo` when direct manipulation can safely express the action;
- whole-card drag is preferred where safe;
- preserve useful visual/haptic movement feedback;
- support the actual active one-column or multicolumn layout rather than hiding the problem by changing layout first.

Classification: **transversal Phase 4A UX blocker**.

Scope:

- Equipo;
- Rasgos;
- Notas;
- any other collection using the same reorder primitive/pattern;
- phone and tablet surfaces.

The owner did not need to repeat the same rejection separately in every module because the interaction is shared.

### 7.3 BLOCKER — Rasgos provenance still requires redundant typing

Owner observation:

- after selecting an origin type, the UI still asks the user to type the actual origin in cases where the character sheet already knows it;
- this feels like an inappropriate copy of the Conjuros source-association interaction.

This directly conflicts with the intended post-audition stabilization claim that known provenance would come from canonical character identity data.

Definitive owner rule:

- `Clase` -> never free text; select from class(es) actually registered for this character;
- `Subclase` -> never free text; select from subclass(es) actually registered for this character;
- `Raza` -> never free text; select the character's registered race;
- `Trasfondo` -> never free text; select the character's registered background;
- `Otro` -> free text allowed;
- `Don` -> free text allowed while it is not modeled as a canonical structured entity.

Example of the contradiction this must prevent:

- character says `Clase = Mago`;
- Rasgo provenance must not allow the user to manually type `Clase = Hechicero` as a parallel contradictory truth.

Important future-SRD direction:

- do not replace free text with a global static list of every D&D class/race/background;
- first derive available structured origins from **this character's canonical data**;
- future official/SRD catalog linkage may auto-associate official features and progressively reduce manual provenance entry;
- manual free text remains for genuinely custom/unmodeled origins.

Classification: **Phase 4A design/state-integrity blocker**.

Automated-boundary correction required:

- static evidence that canonical-origin resolver functions exist is insufficient;
- future tests must validate the actual user flow: selecting a structured origin type exposes/selects canonical character origins without requiring redundant typing.

## 8. Conjuros

### Phone portrait

Owner result: **PASS**.

The following were acceptable on-device:

- source-context behavior;
- `Todos los conjuros` versus specific source;
- selected-source casting stats ownership;
- source/filter/search interaction;
- spell-list usability;
- general portrait density.

This confirms that Conjuros' source model itself is legitimate and should not be collapsed merely because Rasgos provenance needs a different interaction.

### Phone landscape

See the landscape section below: the sticky `Nivel` region becomes obstructive when vertical space is short.

## 9. Notas / Trasfondo

### 9.1 PASS — Notas normal behavior

Normal Notas browsing/editing/search behavior was acceptable.

Reorder remains failed through the shared transversal reorder finding above and should not be counted as an independent Notas defect.

### 9.2 BLOCKER / UX regression — Trasfondo photos were unnecessarily redesigned

Owner verdict:

- the previous photo interaction/presentation was already good/perfect for their needs;
- the new photo UX should not have replaced it simply because persistence was being added.

Correct repair direction:

> **old preferred photo UX + new durable image persistence/storage**

Requirements:

- restore the previous preferred photo interaction/presentation as closely as practical;
- retain the new app-owned durable storage/persistence;
- retain reopen/restart survival;
- retain backup/import safety;
- do not couple persistence correctness to an unwanted presentation redesign.

Classification: **owner-rejected regression; Phase 4A repair required**.

## 10. PC Settings / Application Settings

### 10.1 Application Settings functionality

Owner result:

- settings are understandable;
- controls function correctly overall.

Functional result: **PASS**.

### 10.2 UX defect — compactness slider is disproportionate

Owner observation:

- spacing between boxes/cards is affected much more strongly than many internal UI dimensions;
- compactness therefore feels like it mostly collapses inter-card whitespace instead of proportionally compacting the interface.

Classification: **systemic density UX defect**.

Repair requirement:

- compactness changes should feel coherent/proportional across related margins, internal padding and vertical rhythm;
- do not shrink required touch targets below acceptable sizes;
- do not tune only phone; shared spacing primitives must produce sensible tablet density as well.

### 10.3 Theme selector — restore the three-color shorthand while keeping the good preview

Owner history/direction:

- the original simple three-color representation was useful for understanding the scheme;
- the later intermediate preview was bad;
- the current realistic example is good;
- showing only the theme name is worse for quick scheme recognition.

Target presentation:

- theme name;
- simple three-color palette/swatch shorthand;
- current useful representative/live example.

Do **not** revert to the rejected intermediate miniature design.

Classification: **required presentation repair**.

### 10.4 Minor UX — `ⓘ` icon positioning

Owner observation:

- the contextual-help behavior is good;
- the `ⓘ` icons are not ideally positioned/aligned inside their boxes.

Classification: **minor UX correction**, not a functional failure.

### 10.5 PC Settings — functional PASS, visual/IA redesign required

Owner observation:

- PC Settings functions correctly and is understandable;
- visually it looks bad enough that a coherent UX redesign is preferred.

Classification:

- functionality: **PASS**;
- visual/information-architecture presentation: **owner-rejected / redesign required for acceptance**.

Repair approach:

- treat it as one coherent page-level UX/layout pass;
- do not respond with dozens of isolated padding patches;
- preserve working controls and canonical state behavior underneath.

## 11. Table mode / Supercompact / conditional modules / backup

### 11.1 BLOCKER — Modo mesa cannot be activated

Owner observation:

- `Modo mesa` could not be activated at any point;
- saving the sheet did not make activation possible.

Intended behavior from the implementation checkpoint is that Table mode is blocked only while an older structural draft is dirty; once structurally clean it should activate and lock structural edits while preserving operational controls.

Observed behavior therefore differs from the intended contract.

Classification: **Phase 4A functional blocker**.

Repair/test requirement:

- verify activation from a genuinely clean persisted character;
- verify explanatory UI when a dirty structural draft blocks activation;
- verify structural edits are blocked once active;
- verify operational interactions remain available;
- verify the mode can be exited normally;
- include real integration/UI behavior in the automated boundary rather than policy-only tests.

### 11.2 BLOCKER / conceptual redesign — Vista supercompacta is not useful

Owner verdict:

- current Supercompact is effectively useless for the intended at-table purpose.

Current concept emphasizes compact core stats plus Quick Access/Favorites and selected operational controls. The owner now defines the desired concept more precisely:

> **Vista supercompacta should read almost like a 5.5e Monster Manual monster/NPC stat block, but populated from the PC's canonical character data.**

This is a conceptual redesign, not a minor spacing tweak.

Target information grammar:

- strong compact identity header;
- immediate defensive/operational values such as CA, HP, speed, initiative and similar at-table essentials;
- compact attributes and relevant modifiers/saves;
- concise passive perception/senses/proficiencies/languages/reference data where useful;
- highly scannable combat/action/resource information;
- dense vertical hierarchy optimized for rapid table reference;
- Quick Access/Favorites may contribute useful content but must **not define the whole purpose of the view**;
- live controls should remain only where they are compact and genuinely useful.

The target is to borrow the **information density and scanning grammar** of a modern D&D 5.5e stat block, not necessarily to reproduce a copyrighted Monster Manual page literally.

Important cross-project consistency:

- future DM work already prefers the current 5.5e monster stat-block reading model;
- Player Supercompact should use a coherent stat-block-like grammar without creating a second character-state authority.

Classification: **Phase 4A acceptance blocker / concept redesign**.

### 11.3 PASS — conditional modules representative behavior

Representative conditional-module visibility/edit/save/reopen behavior was acceptable.

### 11.4 PASS — backup/export representative behavior

Representative backup/export flow completed normally.

No destructive import-over-current-state exercise was required during this pass.

## 12. Phone landscape

Phone landscape was intentionally tested as a **phone interaction model**, not as a tablet substitute.

### 12.1 PASS — phone model retained

The app did not incorrectly switch into the tablet interaction model merely because the phone was rotated.

General navigation/editing behavior was otherwise acceptable.

### 12.2 BLOCKER — sticky/fixed boxes consume too much or all usable vertical space

Owner observation:

- sticky/fixed boxes block too much of the content viewport in landscape;
- in some surfaces they can effectively consume all useful vertical space.

This confirms and strengthens the portrait Combate fixed-region problem.

Classification: **systemic responsive Phase 4A blocker**.

### 12.3 BLOCKER — Conjuros sticky `Nivel` box becomes obstructive

Owner observation:

- the sticky level box that is acceptable/useful in portrait becomes a problem in landscape because of the short viewport.

Repair requirement:

- sticky behavior must be responsive to **available height**, not only width or orientation;
- a control being useful enough to stay fixed in portrait does not imply it should remain fixed in short-height landscape;
- allow collapse, unstick, reduce, move or otherwise adapt when the fixed region would dominate the content viewport.

### 12.4 SYSTEMIC DEFECT — vertical margins/padding become a major landscape problem

Owner observation:

- excessive vertical margin/padding is already inconvenient in portrait;
- in landscape it becomes a major usability problem because each extra vertical gap consumes a much larger share of the viewport.

Repair principle:

> Vertical-space policy must react to available height, not merely device width or orientation label.

This must be addressed through shared spacing/layout policy where possible rather than per-screen one-off reductions.

## 13. Larger text scale

Representative larger application text-scale testing on phone covered General, Combate, Conjuros and an editor.

Owner result: **PASS**.

No clipping/overlap failure severe enough to reject the larger-text boundary was observed.

This PASS does not waive the independent sticky/vertical-space defects above.

## 14. Tablet scope rule after phone QA

Physical tablet portrait/landscape acceptance remains **not yet performed**.

The owner explicitly requested that repair work **not be limited to phone** because shared defects will predictably affect tablet too.

Therefore the next repair pass must assume cross-device scope for all shared causes, including at minimum:

- canonical HP/state authority;
- shared editor sizing;
- direct drag-and-drop reorder primitives;
- Rasgos provenance selection model;
- damage/healing interaction primitives;
- compactness/spacing system;
- PC Settings shared UX/IA;
- theme selector presentation;
- Table mode logic;
- Supercompact concept;
- any shared sticky/fixed-region primitive or responsive height policy;
- restored Trasfondo photo interaction where the same UX is used on tablet.

Important evidence distinction:

- **repair scope:** phone + tablet where shared/systemic;
- **physical acceptance evidence:** phone only so far;
- **tablet physical QA:** deferred until after repair build so testing evaluates the intended repaired product rather than known-bad shared behavior.

Do not mark tablet PASS or FAIL based solely on inference.

## 15. Consolidated classification

### Functional/state blockers

1. HP canonical-state synchronization across General/Combate/shared surfaces.
2. Table mode activation from saved/clean state.

### Core/high-frequency UX blockers

3. damage/healing registration workflow;
4. Combate fixed quick-reference footprint;
5. direct normal-layout drag-and-drop reorder replacing special one-column reorder mode;
6. Rasgos structured provenance selection from canonical character data;
7. Supercompact conceptual redesign into stat-block-like at-table reference.

### Responsive/systemic blockers

8. sticky/fixed regions in short-height landscape;
9. Conjuros sticky `Nivel` behavior in landscape;
10. excessive vertical margins/padding and height-insensitive spacing policy.

### Owner-rejected regressions / required design corrections

11. restore previous preferred Trasfondo photo UX while retaining new persistence;
12. adaptive shared editor sizing instead of nearly-always full-height windows;
13. attack structured-damage editor interaction/labels;
14. PC Settings coherent visual/IA redesign;
15. compactness slider proportionality;
16. theme choice should show name + three-color shorthand + current good representative preview;
17. custom Habilidad layout should match ordinary skills;
18. `ⓘ` positioning/alignment refinement.

### Confirmed PASS areas

- in-place upgrade/data preservation and restart persistence;
- phone portrait baseline/navigation;
- representative editor IME/save/cancel/delete function;
- currency;
- Conjuros portrait behavior/source model;
- Notas normal browsing/editing/search;
- Application Settings functionality/understandability;
- representative conditional modules;
- representative backup/export;
- phone landscape remains phone model;
- representative larger text scale.

## 16. Repair-pass governance

Do not immediately turn every bullet above into an isolated patch.

Before implementation, group and discuss the findings into coherent repair packages, likely around:

1. **canonical state + operational Combat interactions**;
2. **shared responsive/height/density primitives**;
3. **shared collection reorder primitive**;
4. **Rasgos provenance/reference model**;
5. **shared editor sizing**;
6. **PC/Application Settings presentation**;
7. **Trasfondo photo UX restoration**;
8. **Table mode correctness**;
9. **Supercompact stat-block redesign**;
10. **small integration/polish fixes** such as custom-skill spacing and `ⓘ` alignment.

Repair only the accepted findings. Do not invent unrelated Player feature scope or a speculative Increment J.

Automated validation should be strengthened where this QA exposed false confidence. In particular:

- Rasgos tests must validate the actual no-retyping structured-origin interaction;
- Table-mode tests must validate real activation behavior from clean state;
- HP tests must verify cross-surface canonical propagation;
- responsive tests should explicitly cover short-height phone landscape for sticky/fixed regions;
- reorder tests should reflect the owner-approved direct normal-layout interaction rather than only proving a vertical fallback works.

## 17. Exact next project position

At the next project session:

1. read this checkpoint first after `LATEST.md`;
2. review the grouped findings with the owner and resolve only the remaining design details needed for implementation;
3. define a bounded Phase 4A acceptance-repair plan across phone and tablet shared surfaces;
4. implement the repair plan on the active continuation branch;
5. run exact automated validation for the repaired boundaries;
6. produce the next monotonic successor QA build;
7. perform **targeted phone retest first** for repaired blockers;
8. once phone shared/systemic blockers are acceptable, perform tablet portrait and tablet landscape physical QA on the repaired build;
9. only then freeze a replacement formal M6 candidate and complete final regression/upgrade acceptance;
10. explicitly close Phase 4A;
11. only after that may DM implementation begin.

## 18. Protected owner directions reaffirmed by this QA

- one datum / one canonical state;
- phone landscape remains a phone interaction model;
- phone and tablet share repair responsibility for shared/systemic defects;
- tablet portrait/landscape remain first-class compositions and still require later physical acceptance;
- reduce unnecessary vertical margins/padding without sacrificing required touch targets;
- responsive policy must consider available **height** as well as width/form factor;
- direct drag-and-drop in the normal card layout is the reorder target;
- structured known provenance must use canonical character data, not redundant typing;
- `Otro` and `Don` may remain free text provenance paths;
- restore preferred old UX when persistence was the only requested underlying change;
- Supercompact should read like a dense PC stat block inspired by the 5.5e monster/NPC reading grammar;
- no DM implementation before explicit Phase 4A closure.
