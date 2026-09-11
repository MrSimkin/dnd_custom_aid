# Phase 4A — preqa.8 point-by-point repair decisions

**Opened:** 2026-09-11  
**Status:** IN PROGRESS — OWNER DESIGN RECONCILIATION  
**Branch:** `implementation/phase4a-successor-cycle`  
**QA source:** `0.4.0-preqa.8` / build `40800` / debug  
**Controlling QA checkpoint:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`  
**Implementation authorization:** NOT YET — discussion/consolidation first

## Purpose

This checkpoint is the durable consolidation record for the acceptance-repair decisions produced from the `40800` owner QA.

The owner explicitly requires the findings to be discussed **one point at a time**, not grouped into repair packages during the design discussion.

For each point:

1. inspect the exact QA finding and relevant current implementation/context;
2. discuss the intended behavior in enough detail to remove implementation ambiguity;
3. do not broaden the discussion into unrelated findings;
4. when the owner explicitly closes the point, record the resulting decision here;
5. only then proceed to the next point.

A closed point may identify shared/transversal implementation consequences, but that does not merge its design discussion with other open points.

## Consolidation format for each closed point

Each closed item must record:

- QA point / observed problem;
- owner-approved target behavior;
- important interaction/layout/state rules;
- phone/tablet scope where relevant;
- explicit non-goals or rejected alternatives where useful;
- automated/regression boundary needed to prove the repair;
- status: `CLOSED / READY FOR REPAIR SPEC`.

Do not mark implementation complete in this document merely because the design decision is closed.

## Closed decisions

### P1 — Canonical HP state across General / Combate / shared surfaces

**QA problem:** `preqa.8 / 40800` allowed HP shown/edited in General and HP shown in Combate to diverge. This violated the protected `one datum / one canonical state` rule.

**Owner-approved target behavior:**

- `HP actual`, `HP máximo` and `HP temporal` each have one authoritative character state shared by General, Combate and every other projection/operation.
- A direct `HP actual` edit in General is an exact administrative/set operation. It does **not** simulate receiving damage or healing and therefore does not invoke combat damage/healing semantics.
- Editing `HP máximo` changes only the maximum. Increasing maximum HP does not silently heal the character.
- Invariant: `HP actual` may never exceed `HP máximo`. If maximum HP is reduced below the current value, current HP is clamped down to the new maximum.
- `HP temporal` is likewise one canonical value. Direct editing sets that value exactly; operational damage handling is separate and will apply the appropriate temporary-HP rule.
- Once an HP change is committed, all visible projections update immediately from the same authoritative state. No tab switch, reopen, refresh or duplicate save cycle is required.
- A text field may temporarily contain an uncommitted editing draft while the owner is typing, but after commit there is only one canonical value.

**Explicitly not decided in P1:**

- the visual/interaction design of the frequent-combat `Daño / Curar` operation;
- death-save / unconscious-state presentation and any automatic coupling to HP transitions.

Those are separate discussion points and must not be inferred from this state decision.

**Phone/tablet scope:** shared state rule applies to every phone/tablet surface using HP.

**Regression boundary:** automated coverage must prove cross-surface propagation from one authoritative HP state, maximum-HP clamping, no silent healing when maximum increases, and canonical temporary-HP projection. Independent per-screen persistence tests are insufficient.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P2 — `Daño / Curar` high-frequency combat interaction

**QA problem:** `preqa.8 / 40800` routes a frequent in-combat HP operation through a large editor/dialog, amount entry and explicit save. The owner rejected this as too slow and intrusive for normal combat use.

**Owner-approved target behavior:**

- Combate presents one compact operational HP block containing the visible current/max HP, temporary HP and the frequent `Daño / Curar` controls.
- The primary interaction is a compact three-part operation row: `Daño` action, one shared numeric amount field, and `Curar` action.
- The owner enters the amount and taps `Daño` or `Curar`; the operation applies immediately. There is no additional Save step and no confirmation dialog in the normal path.
- After application, the amount field clears and is ready for the next operation.
- Damage/healing uses the canonical P1 HP state and normal HP semantics underneath, including temporary-HP absorption for damage and maximum-HP capping for healing.
- Feedback is intentionally minimal and non-intrusive: affected HP display box(es) receive only a brief/subtle visual glow or pulse. There is no snackbar/toast-style operation message required.
- The visual feedback follows what actually changed: e.g. damage fully absorbed by temporary HP highlights only temporary HP; spillover damage may highlight temporary HP and current HP; healing highlights current HP when it changes.
- There is **no Undo operation** for damage/healing. If the owner enters an incorrect operation, correction is performed explicitly through the ordinary game-facing operations (e.g. heal back damage and/or restore temporary HP) or through an explicit exact-state correction where appropriate.
- Combat also provides a secondary exact-correction path for current HP. Tapping/activating the visible current/max HP display may expose an explicit `Establecer PV` correction action. This is secondary to `Daño / Curar` and must be clearly presented as exact state correction, not as another form of healing.
- `Establecer PV` uses the same canonical state and P1 invariants; it does not invoke damage/healing semantics.

**Explicitly not decided in P2:**

- death-save / unconscious-state presentation or automatic coupling to HP transitions;
- any broader redesign of the full Combate fixed/sticky region beyond keeping this HP operation compact;
- a separate richer workflow for granting/replacing temporary HP, beyond preserving canonical temporary-HP state and normal damage interaction.

**Phone/tablet scope:** the same high-frequency interaction principle applies to phone and tablet wherever this shared HP operation is exposed. Layout may adapt to available width, but it must remain compact and direct.

**Regression boundary:** automated/integration coverage must prove immediate damage/healing application without a save dialog, correct temp-HP-first damage behavior, healing capped by max HP, amount-field reset, exact-correction separation from damage/healing semantics, canonical cross-surface propagation, and affected-state visual-feedback triggering. No Undo control should be exposed.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P3 — Custom Habilidades geometry and custom Atributos participation

**QA problem:** `preqa.8 / 40800` renders custom skills with visibly different margin/padding from ordinary skills in both `Por habilidades` and `Por atributo`, despite the intended inline behavior.

**Owner-approved target behavior for custom Habilidades:**

- A custom Habilidad uses the same row geometry, alignment, padding, spacing, controls and interaction model as an ordinary Habilidad.
- This applies in both `Por habilidades` and `Por atributo` grouping.
- Italicized naming is the intended visual distinction for a custom Habilidad; it must not receive a special card, badge, indentation, background, row height or container geometry merely because it is custom.
- A custom Habilidad is therefore a normal participant in the skill system, not a visually separate secondary object.

**Owner clarification discovered during P3 — custom Atributos:**

This was not an observed `40800` QA failure and had not previously been explicitly discussed/tested. The owner confirms it as required Player behavior rather than leaving it as an assumption:

- custom Atributos follow the same native-participant principle as the six basic Atributos: use the same interaction/visual grammar rather than a special custom-only container style;
- custom Atributos are additional to the six basic Atributos and may occupy additional row(s) beyond the standard six rather than displacing or masquerading as one of them;
- custom Habilidades may be assigned to a custom Atributo;
- when the Habilidades view is grouped `Por atributo`, those custom Habilidades must group under their assigned custom Atributo exactly as standard skills group under standard Atributos;
- the data model/UI must therefore not assume that the parent Atributo of a Habilidad can only be one of the six basic attributes.

This clarification is part of the acceptance boundary even though it was surfaced outside the original QA checklist, because leaving it implicit would create an avoidable implementation ambiguity.

**Phone/tablet scope:** identical semantic behavior across phone and tablet; responsive wrapping may differ, but custom versus standard geometry must remain coherent within each layout.

**Regression boundary:** visual/integration coverage must compare ordinary and custom Habilidad geometry in both grouping modes. Additional coverage must prove creation/display of custom Atributos beyond the six basics and assignment/grouping of custom Habilidades under a custom Atributo. This behavior must be tested rather than merely assumed.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P4 — Attack structured-damage editor UX

**QA problem:** `preqa.8 / 40800` supports structured attack damage and rolling, but the interaction requires manually typing expressions such as `1d8` and presents fields such as `1d8` / `+3` in a way that reads like examples rather than direct semantic controls. The owner rejected that interaction even though the underlying model works.

**Owner-approved target behavior:**

- Attack damage is edited as a list of structured damage components rather than as one opaque raw formula.
- The ordinary direct component grammar is compact and selector-oriented, conceptually: `[cantidad] d [caras] [modificador plano] [tipo de daño]`, with appropriate omission of parts that are not used.
- Dice quantity and die size are separate direct controls. Normal attacks must not require typing `2d6` or similar expressions.
- Standard die sizes are directly selectable, and the die-size control also includes `Otro`, allowing unusual positive die sizes such as `d3`, `d5` or other homebrew dice.
- Dice quantity is not artificially limited to a small preset ceiling; it is a positive integer when dice are present.
- A component may also contain only flat damage, with no die at all. Flat-only components are valid first-class components rather than a workaround.
- Flat modifiers may be positive, zero or negative.
- Multiple components may use the same damage type and remain separate. Example: `1d8+3 Cortante +3 Fuego +1d4 Fuego` is represented as three structured components, not collapsed into one typed expression.
- Damage type uses a direct selector for standard damage types and includes `Otro` for custom/homebrew types.
- Multiple structured components remain independently represented but the saved attack provides a convenient roll-all-damage action.
- The attack card displays saved damage compactly and can roll configured damage without reopening the editor; editing and rolling are separate interactions.

**Signed component rule / unusual arithmetic:**

- Dice components themselves may be positive **or negative** in the overall damage expression. This supports cases such as a cursed weapon with `1d8 - 1d4`.
- The sign belongs to the component arithmetic; the quantity of dice and number of faces remain positive values.
- Negative dice therefore do not require a raw-expression escape hatch. A component can explicitly contribute `-1d4` just as another contributes `+1d8`.
- Flat contributions likewise support positive or negative values.
- The structured list must therefore be capable of expressing combinations such as `1d8 - 1d4`, `2d6 - 3`, `-1d4`, `+3 Fuego`, or `-2 Radiante` without falling back to free-form formula text.

**Roll-result semantics:**

- The roller reports the arithmetic damage result exactly as produced by the configured signed components.
- A negative total is **not** automatically reinterpreted as healing or relabelled by the application.
- Example: if `1d8 - 1d4` evaluates to `-2`, the result is presented simply as `Daño: -2`.
- The app remains mechanically neutral at this layer; any narrative or game-rule interpretation of a negative damage result is left to the user/table.

**Explicit non-goal / rejected alternative:**

- No general raw `Expresión personalizada` text mode is required merely to support unusual dice arithmetic. The accepted structured-component model is intended to cover those cases directly.
- Do not reduce support back to only `NdX + flat` with positive dice components; signed dice components are part of the accepted grammar.

**Phone/tablet scope:** the same structured model and editing capabilities apply on phone and tablet. Layout may adapt, but the interaction must remain direct and compact rather than reverting to manual expression typing on narrower surfaces.

**Regression boundary:** automated/integration coverage must prove direct quantity/die selection, `Otro` die sizes such as `d3`/`d5`, flat-only components, positive and negative flat values, positive and negative dice components, repeated damage types across separate components, custom damage type through `Otro`, compact saved rendering, roll-all behavior, and exact arithmetic reporting including negative totals such as `Daño: -2`.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P5 — Combate fixed quick-reference footprint / compact fixed-region principle

**QA problem:** at `Compactación de espacios = 100%`, the fixed Combate quick-reference region in `40800` consumed roughly 60% of the phone portrait viewport and became even more obstructive in landscape. The owner explicitly rejected treating a lower global compactness setting as the solution.

**Owner-approved Combate HUD target:**

- Replace the vertically stacked `Referencia rápida` card grammar with a genuinely compact fixed combat HUD.
- Remove the redundant `Referencia rápida` heading from the fixed region.
- Keep `CA`, `Iniciativa` and `Velocidad` permanently visible in one compact inline reference row rather than separate vertically expensive mini-cards.
- Combine current and maximum HP into one compact projection such as `PV 37/52` rather than separate `PG actuales` and `PG máximos` boxes.
- Keep temporary HP visible compactly as `Temp. 8`.
- The permanent `PG temp.` action button is removed. Tapping/activating the visible temporary-HP value provides the explicit exact-value edit path instead.
- Keep the P2 high-frequency operation as a compact permanent row: `Daño | cantidad | Curar`.
- Tapping/activating the combined current/max HP projection exposes the secondary exact `Establecer PV` correction path defined in P2.
- Death-save controls must **not** enlarge the permanent fixed HUD. When relevant, they appear as the first normal scrollable Combate section immediately below the HUD.
- The fixed HUD must leave attacks/actions as the dominant scrollable content area at the normal `100%` spacing setting. The repair must not depend on telling the owner to lower application compactness.

**Accepted conceptual shape:**

`CA 18     Inic. +4     Vel. 30 ft (9 m)`  
`PV 37/52     Temp. 8`  
`Daño | cantidad | Curar`

The exact responsive arrangement may adapt, but this compact information grammar is the target.

**General fixed/sticky-region principle clarified during P5:**

- Across the application, fixed/sticky informational regions should be compact by default because permanent viewport occupation directly reduces the working/content area.
- A fixed region should behave more like a HUD/status strip than like an ordinary spacious scrollable card.
- Prefer inline label/value presentation over stacked mini-cards where the information remains clear.
- Remove redundant headings when surrounding navigation/context already explains the region.
- Minimize vertical stacking and unnecessary internal padding while preserving legibility.
- Interactive elements must retain safe/usable touch targets; `compact` must not mean tiny or difficult to operate.
- Normal scrollable cards may remain more spacious because they do not permanently consume viewport space.
- Available **vertical height**, not only width/orientation name, must be considered when deciding how aggressively a persistent region compacts/adapts. The separate global landscape/sticky-policy QA point will refine that rule later rather than being silently collapsed into P5.

**Global speed-formatting clarification discovered during P5:**

- Character speed is imperial-first everywhere it is projected, with the quick 5.5e-style metric conversion shown consistently in parentheses.
- Example: `30 ft (9 m)`, `25 ft (7.5 m)`, `5 ft (1.5 m)`.
- This is a shared presentation rule, not a General-tab-only feature. General, Combate, Supercompact, Table Mode and any other current/future Player reference projection of the same canonical speed should use the same formatter/policy rather than reimplementing it independently.

**Explicitly not decided in P5:**

- death-save mechanics/state-transition semantics beyond moving their controls outside the permanent HUD;
- the broader global phone-landscape/sticky policy, which remains its own QA point;
- unrelated Combate attack/action card layout beyond ensuring the fixed HUD leaves that content usable.

**Phone/tablet scope:** the compact fixed-region principle and Combate HUD information grammar apply across phone/tablet. Layout may reflow with available width/height, but tablet must not revert to the old vertically wasteful stacked mini-card model merely because more width exists.

**Regression boundary:** visual/integration coverage must prove the compact HUD retains `CA`, `Iniciativa`, `Velocidad`, combined current/max HP, temporary HP and direct `Daño/Curar` operation; removes the redundant heading and permanent temp-HP button; keeps death saves outside the fixed HUD; leaves a meaningful scrollable attacks/actions area at 100% spacing; preserves tappable exact-HP/temp-HP correction paths; and applies the shared imperial-first speed formatter consistently across representative Player surfaces.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P6 — Direct drag-and-drop reorder in normal collection layout

**QA problem:** `preqa.8 / 40800` uses a special `Reordenar -> transformed one-column layout -> Listo` workflow for shared collections. The owner rejected this because reordering should happen directly in the same layout in which the collection is normally used, including multicolumn presentation.

**Owner-approved interaction:**

- Reordering starts with **long-press + drag on the non-interactive body of the card itself**.
- A normal tap retains the card's ordinary action such as opening/editing; ordinary scrolling must remain available.
- No separate `Reordenar` mode, transformed reorder-only layout, or final `Listo` step is used.
- Interactive child controls inside a card—buttons, checkboxes, menus, favorite controls and similar elements—keep their own actions and do **not** initiate card dragging.
- A permanent drag handle is not required for the normal case. The whole non-interactive card body is the drag target.

**Behavioral reference:**

- The owner explicitly points to the Vivaldi mobile browser Tab Switcher card interaction as the desired feel/reference: long-press a card/tab, drag it directly through the visible collection, and release it at the new location.
- Official Vivaldi Android documentation confirms that the Tab Switcher reorder interaction is long-press followed by drag-and-drop to the new location. This is a behavioral reference, not a requirement to reproduce Vivaldi's visual styling literally.

**Multicolumn ordering:**

- Reordering remains in the collection's actual active one-, two- or multicolumn layout; the collection must never collapse to one column merely to support drag-and-drop.
- Multicolumn manual order is row-major/reading order: `1 -> 2 -> 3`, then `4 -> 5 -> 6`, and so on.
- The dragged card moves through those actual positions and neighboring cards reflow live to communicate the insertion destination.
- The user must be able to move a card across both rows and columns, not merely vertically within its current column.

**Persistence and ordering mode:**

- On drop, the new manual order is committed/persisted immediately. There is no separate Done/Save step for the reorder itself.
- Direct drag reorder is available only while presentation order is `Manual`.
- If `A–Z` or another deterministic non-manual ordering is active, dragging must not silently switch the collection into Manual mode or mutate hidden manual order behind the owner's back.
- If the user attempts to reorder while a non-manual order is active, the UI should provide a small/non-intrusive indication that manual ordering requires `Manual` rather than changing the setting automatically.

**Search/filter boundary:**

- Direct reorder is disabled while a search or filter is active because the visible subset no longer represents the full manual sequence and hidden-item insertion would be ambiguous.
- Clearing the search/filter restores direct reorder availability.
- Do not infer or rewrite hidden-item order from a filtered subset.

**Visual/haptic feedback:**

- Pickup produces a subtle visual lift/scale and one pickup haptic.
- Movement provides live insertion/reflow feedback in the real collection layout.
- Crossing into a new candidate position produces a light haptic step; it must be subtle enough not to become intrusive during a long drag.
- Drop produces one final haptic and commits the new order.
- All reorder haptics respect the existing global haptic preference.

**Shared scope:** this is a transversal shared-collection interaction. It applies to Equipo, Rasgos, Notas and any other Player collection that exposes manual ordering through the same collection/reorder primitive. Phone and tablet must use the same direct-manipulation semantics, adapted to their actual column count and available space.

**Explicit non-goals / rejected alternatives:**

- no `Reordenar -> special mode -> Listo` workflow;
- no forced one-column fallback for multicolumn collections;
- no automatic switch from `A–Z` to `Manual` merely because a long-press occurred;
- no reorder while search/filter hides part of the sequence;
- no requirement to copy Vivaldi's exact artwork or chrome—only the direct long-press/drag/drop behavioral grammar is the reference.

**Regression boundary:** automated/UI coverage must prove long-press direct pickup in the normal layout, normal tap/edit behavior remains intact, child controls do not start dragging, row-major multicolumn movement including cross-column moves, live reflow/insertion feedback, immediate persistence on drop, no separate reorder mode/Done step, `Manual`-only reorder semantics, no silent sort-mode switching, disabled reorder under active search/filter, and pickup/step/drop haptics respecting the global haptic setting.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P7 — Canonical provenance / character-owned origin references / catalog readiness

**Original QA problem:** `preqa.8 / 40800` still allows Rasgos to request redundant/free-text provenance even when the character already owns canonical class/race/background identity data. Current shared provenance behavior can also fall back to free text when canonical options are absent, creating a second potentially conflicting copy of structured character identity.

**Owner-approved Rasgos provenance rules:**

- `Subclase` is an explicit structured provenance kind and must not be flattened into `Clase`.
- A structured origin with no eligible canonical value on this character does **not** fall back to arbitrary free text. Example: choosing `Raza` when this PC has no race/species registered must explain that no such identity is registered and must not allow inventing one inside the dependent Rasgo editor.
- If a structured provenance kind has exactly one eligible canonical character entity, that entity is selected automatically rather than requiring a redundant extra choice.
- In multiclass cases, subclass choices are disambiguated with their parent class, conceptually `Evocación (Mago)`.
- Structured provenance binds to stable character-owned identity, not to a copied display string. Renaming the referenced entity updates dependent projections rather than leaving stale duplicated text.
- If a referenced character-owned canonical entity is deleted, the dependent Rasgo survives with a soft/unresolved reference state. It is not silently remapped, deleted, or converted to unrelated arbitrary free text.
- Legacy text provenance is migrated conservatively: an exact/unambiguous match to an eligible entity actually owned/registered on this PC may be linked automatically; ambiguous or unmatched provenance remains preserved for owner correction and the old text is not discarded.
- `Dote`, `Don / bendición` and `Otro` remain free-text capable for the current repair. `Dote` in particular must be ready to become SRD/custom-catalog-backed soon rather than being architecturally trapped as permanent free text.
- One **Rasgo character instance** has exactly one provenance source.

**Same conceptual Rasgo acquired from multiple sources:**

- The same underlying Rasgo definition may legitimately be acquired more than once through different character sources.
- Those acquisitions are represented as **separate Rasgo character instances**, not collapsed into one record with hidden or multiple provenance.
- Example: if the same conceptual Rasgo is gained from Elf and from Class, the sheet may contain two Rasgo entries pointing to the same underlying definition: one with source `Elf`, one with source `Class`.
- Each instance still obeys `one Rasgo instance -> one source`.
- The app must not silently deduplicate those two acquisitions merely because their definition/name is the same.

**Definition / character ownership / provenance separation:**

The shared architecture distinguishes three different facts:

1. the reusable **definition** of a game object (for example what Fireball is as a spell);
2. the **character-owned/selected instance or acquisition** stating that this specific PC actually has that object;
3. the **relationship/provenance** explaining why/through what character element the PC has it.

A definition existing in an SRD/custom catalog never implies that the PC owns it.

**SRD/custom schema rule:**

- Official/SRD and custom/homebrew content normally share the same domain schema. A custom spell is still a Spell; a custom feat is still a Feat; a custom background is still a Background; a custom species is still a Species; a custom magic item is still a Magic Item.
- Custom/homebrew is primarily a definition-origin distinction, not a second generic `custom thing` object universe.
- Truly schema-breaking content is an extreme exception and must not distort the normal architecture.
- The owner identifies adapted 3.5e `Weapons of Legacy` and legacy 2e-style spell levels 10–13 as rare examples near that edge. Of these, only high-level spells are presently intended for near-term implementation. This note does **not** authorize Weapons of Legacy implementation during the current QA repair.

**Species/race hierarchy:**

- The model supports a base species/race plus a more specific child/variant, e.g. `Elf -> Drow` or `Genasi -> Air`.
- Internally, the child relationship must remain generic enough for source-rule concepts such as subrace, lineage, ancestry, legacy or similar variants; the data model must not assume every rules source literally calls the child a subrace.
- **The owner-facing sheet terminology is nevertheless always `Subraza` / subrace.** Source-specific vocabulary is not required on the normal sheet.
- Provenance may target either the base species/race or the specific subrace/child when that distinction matters.
- Prefer the most-specific direct source and derive its parent chain rather than storing redundant ancestry. Example: a Drow-specific effect references Drow and derives Elf; a subclass feature references the subclass and derives its parent class.

**Gameplay provenance versus publication reference:**

- Gameplay provenance means `why does this PC have this?`, with answers such as Elf, Mago/Wizard, Battle Master, Background, Feat, etc.
- Book/publication metadata means `where can I find the written rule?`, such as `Tasha p. 12` or `Manual de Gustavo p. 127`.
- These are separate concepts.
- Gameplay provenance may matter to the character model/sheet.
- Book/page/original-publication information should normally remain hidden from the sheet and appear only as optional detail/Notes/reference metadata when useful.
- Internal catalog metadata may know whether a definition came from SRD, another source or custom content without adding pervasive SRD/custom badges to the sheet.

**Transversal canonical-reference rule with domain-specific cardinality:**

- The same identity/reference discipline applies to analogous structured domains such as backgrounds, classes/subclasses, species/subraces, feats, spells, items and other naturally canonical character data.
- This does **not** impose identical relationship counts everywhere.
- Rasgo: one source per character instance, with duplicate instances allowed when the same definition is acquired from different sources.
- Spell: may retain multiple approved spellcasting/source associations where the spell model requires them.
- Subclass: belongs to one parent class instance.
- Subrace/variant: belongs to one parent species/race instance for that character.
- Other domains keep whatever ownership/cardinality is legitimate for that kind of D&D object.

**Future catalog readiness:**

- Natural canonical domains such as Class, Subclass, Species/Race, Subrace/variant, Background, Feat, Spell, Item and analogous future entities must be capable of stable reusable-definition identity plus stable character-owned identity even if the full SRD/custom catalog UI is implemented later.
- The current repair must avoid hard-coding permanent name-only identity that would immediately have to be discarded for the upcoming SRD/custom work.
- This readiness requirement does **not** expand the current repair into building the full catalog now.
- A global catalog match alone can never fabricate character ownership/provenance. Automatic migration/binding may occur only when this PC actually has one unambiguous eligible owned/registered entity.

**Rename/deletion/catalog-loss behavior:**

- Renaming a linked definition or character-owned entity updates linked projections rather than leaving stale copied labels.
- Removing a character-owned source leaves dependent records unresolved rather than deleting or silently remapping them.
- If an external/catalog definition becomes unavailable, character-owned data must retain enough stable/snapshot information to remain understandable rather than becoming blank or being destroyed.

**Compatibility with prior project decisions:**

- D-0058's spellcasting-source model remains valid: stable character-owned spell sources may support multiple associations for one conceptual spell.
- D-0063's conservative/soft reference failure is preserved and generalized.
- The earlier Fuente redundancy audit's structured-origin direction is preserved.
- D-0059's older `Rasgos.Fuente = free text` decision is superseded for structured provenance. Existing text must be migrated/preserved conservatively rather than silently lost.

**Phone/tablet scope:** this is primarily a shared data/interaction architecture rule and therefore applies identically across phone/tablet. Responsive selector presentation may differ, but the eligible choices, identity binding, unresolved-reference behavior and acquisition semantics may not diverge by device class.

**Regression boundary:** automated/integration coverage must prove: no free-text fallback for Class/Subclass/Race/Subrace/Background structured origins; auto-selection of a sole eligible source; subclass disambiguation by parent class; canonical rename propagation; unresolved preservation after source deletion; conservative legacy matching limited to entities this PC actually owns; structural base-species/subrace relationships; owner-facing `Subraza` terminology; separation of definition versus character acquisition; absence of catalog-implies-ownership behavior; custom definitions participating in the same domain schemas; and two separate Rasgo character instances when the same definition is acquired from two different sources, each preserving exactly one provenance source.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P8 — Trasfondo photo UX: restore simple image tiles while preserving durable storage

**QA problem:** `preqa.8 / 40800` replaced an already-preferred Trasfondo photo presentation with a more elaborate persistence-era card containing permanent title/action chrome, filename and storage explanation. The owner rejected that redesign. Persistence correctness was required; the presentation redesign was not.

**Historical/reference boundary:**

- The original approved Trasfondo visual grammar used two simple side-by-side portrait-oriented image tiles with a `4:5` aspect ratio.
- The repository history does not contain a fully recoverable earlier functioning photo-picker interaction before the durable G4 implementation; older committed V4 states were still placeholders.
- Therefore the repair restores the owner-approved simple tile grammar and the interaction decisions made during this reconciliation, rather than claiming to reproduce an unrecoverable historical implementation byte-for-byte.

**Owner-approved target behavior:**

- Trasfondo presents exactly two simple side-by-side photo tiles labelled `Imagen principal` and `Imagen secundaria`.
- Each tile has a fixed `4:5` presentation footprint. Loading, changing or displaying a photo must **not increase or otherwise change the tile's size**.
- Do not keep the persistence-era outer card/header/control row, filename underneath, or permanent storage/persistence explanation in the normal sheet presentation.
- When a slot is empty, the **entire tile** is the add/select target. A normal tap opens the image picker; no separate permanent `Añadir` button is required.
- When a slot contains an image, a normal tap opens a larger image viewer. It does **not** immediately replace the picture.
- The normal filled tile remains visually clean; `Cambiar` and `Eliminar` controls are not permanently displayed around or above it.
- In the enlarged viewer, image-management controls such as change/replace and remove/delete appear **overlaid inside the image/viewer**, rather than consuming a separate permanent control row in the Trasfondo sheet.
- Structural-editing restrictions still apply: when structural editing is unavailable (for example the relevant Table Mode/read-only state), change/remove controls are not exposed as active structural actions.

**Crop / viewer behavior:**

- The fixed `4:5` sheet tile uses crop-to-fill / center-crop behavior so its geometry remains stable regardless of the source image's aspect ratio.
- A wide or tall source image may therefore be cropped at its edges in the sheet thumbnail.
- Tapping the filled tile opens the larger viewer showing the **complete uncropped image**, preserving the source image's aspect ratio within the available viewing area.
- The larger viewer is the place for inspection plus overlaid change/remove controls; the thumbnail is the compact sheet projection.

**Persistence/storage boundary that must be retained:**

- Keep the newer app-owned durable image persistence/storage semantics underneath the restored presentation.
- Images must survive save/reopen, full app restart and normal character persistence.
- Backup/export/import must continue carrying the images safely rather than reverting to fragile external URI/file references.
- The app may continue resizing/compressing/copying images internally as required for durable ownership; those implementation details must not force permanent explanatory UI into the character sheet.

**Explicit non-goals / rejected alternatives:**

- do not let the image's original dimensions/aspect ratio expand the sheet tile;
- do not restore external-file-dependent persistence merely to simplify the UI;
- do not keep permanent `Añadir/Cambiar/Eliminar` chrome outside the image tile;
- do not display the original filename as permanent sheet content;
- do not use `Personaje / Grupo` or `Imagen de personaje / Imagen de grupo` as the slot labels; the accepted labels are `Imagen principal / Imagen secundaria`.

**Phone/tablet scope:** the same two-slot semantics, fixed-aspect tile grammar, viewer behavior and durable storage apply on phone and tablet. Responsive width may alter the physical tile size, but an image must not change the layout footprint after loading and the two slots must remain visually coherent.

**Regression boundary:** automated/UI coverage must prove fixed `4:5` tile geometry before and after image selection; whole-empty-tile add interaction; filled-tile tap opening the viewer rather than picker; center-cropped thumbnail versus full uncropped viewer; viewer-contained replace/delete controls; absence of permanent filename/storage/control chrome on the normal sheet; correct `Imagen principal / Imagen secundaria` labels; structural-editing restrictions; image persistence across reopen/restart; and backup/export/import survival using app-owned image data.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

## Current discussion point

**P9 — Shared editor sizing / adaptive `CharacterImeSafeEditorDialog`.**

Discuss only the `40800` shared-editor sizing defect: short/simple editors currently consume essentially the full available screen even when their content does not require it. Preserve IME safety and button reachability while defining an adaptive/natural sizing rule shared across phone/tablet callers; do not mix this point with unrelated PC Settings or landscape/sticky redesigns.

## Remaining boundary

After all required QA points are individually closed:

- convert the consolidated decisions into one bounded Phase 4A acceptance-repair implementation plan;
- implement only the accepted scope;
- run strengthened automated validation for the affected boundaries;
- produce the next monotonic successor QA build;
- perform targeted phone regression/acceptance retest;
- then perform physical Player tablet portrait and landscape QA;
- freeze a replacement formal M6 candidate only after the repaired baseline is owner-acceptable;
- explicitly close Phase 4A before any DM implementation begins.