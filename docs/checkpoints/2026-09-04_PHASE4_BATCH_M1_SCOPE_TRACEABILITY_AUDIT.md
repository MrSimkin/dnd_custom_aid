# Phase 4 Batch M1 — closure scope traceability audit

**Date:** 2026-09-04  
**Status:** M1 COMPLETE — Batch M continues with M2 code-health/static architecture audit  
**Audit branch:** `tmp/phase4-m-audit-safety`  
**Product baseline audited:** `implementation/phase4-preqa-consolidation` created from `implementation/phase4-character-closure` at `c486df837411107d900331649caf89f1cb642984`  
**Frozen historical L candidate:** preserve unchanged  
**Canonical `main`:** preserve unchanged

## 1. Purpose

Do not infer Phase 4 completeness from checkpoint names alone. Trace the owner-approved D-0047 closure scope to current implementation evidence before final owner QA.

Classification used here:

- **IMPLEMENTED** — direct implementation evidence exists; final real-device acceptance may still remain.
- **IMPLEMENTED / EVIDENCE WEAK** — implementation is present but final acceptance depends materially on real-device behavior or coverage is thinner than desirable.
- **PARTIAL** — the approved intent is only partly represented.
- **MISSING** — approved closure behavior is not currently reachable/represented as required.
- **INTENTIONAL / NON-GOAL** — deliberately retained boundary, not a defect.

## 2. Executive result

The Phase 4 closure is substantially implemented, but it is **not yet scope-complete**.

M1 found four concrete approved-scope gaps that must enter Batch N:

1. **F14 — structured languages/proficiencies/training:** shared durable data exists, but there is no current Android editing/management surface for `CharacterProficiency` / structured language-tool-weapon-armor-other proficiencies. The active General/Habilidades/PC Settings wiring does not expose these records.
2. **F15 — Favorite / Quick Access:** the shared Quick Access projection supports `RESOURCE`, but the current `Gestión` resource UI has no favorite/Quick Access control. Resources are explicitly named by F15.
3. **I18 — character list summary:** the current list card shows name, class names/levels, lifecycle status and total level, but not subclass, freshness/last-updated, or optional portrait as approved.
4. **I21 — settings preview:** the application settings dialog auditions fonts with generic `Aa Bb 123` text and themes with color swatches. D-0047 explicitly requires theme/font preview against a miniature real character-sheet sample rather than generic text alone.

M1 also found two design-fidelity areas for M2/N review:

- **D06 PARTIAL:** class/subclass rules generation and source are visible, but the active class row renders them as plain secondary text rather than the approved semantic source/rules badges.
- **D07 PARTIAL / inconsistent:** spell state uses explicit compact badges, while several other state surfaces represent comparable state as plain joined metadata text or isolated controls. M2 should decide the smallest safe normalization rather than starting a cosmetic rewrite.

No evidence was found that these gaps are intentional deferrals from D-0047.

## 3. F01–F18 traceability

| Requirement | Status | Direct evidence / note |
|---|---|---|
| F01 Conditions + Exhaustion | IMPLEMENTED | `CharacterClosureState`; `CharacterManagementTabV4` conditions/exhaustion editor |
| F02 Defenses | IMPLEMENTED | `CharacterDefense`; `CharacterGeneralClosureV4` structured defense editor |
| F03 Senses + special movement | IMPLEMENTED | `CharacterSense`, `CharacterMovement`; General closure editors |
| F04 Concentration | IMPLEMENTED | `CharacterConcentration`; Gestión start/change/clear flow |
| F05 Rest assistant | IMPLEMENTED | `CharacterRestOperations`; preview/selective apply tests; Gestión UI |
| F06 Generic resources | IMPLEMENTED | durable `CharacterResource`; Gestión current/max exact edit and ± operational controls |
| F07 Consumables/ammunition | IMPLEMENTED | inventory usage metadata; quick-use amount; Equipment quick consumption |
| F08 Containers/locations | IMPLEMENTED | item location + carry state; `Contenedor / ubicación` editor and filters |
| F09 Portrait/token | IMPLEMENTED / EVIDENCE WEAK | SAF `OpenDocument`, persisted read URI, render/clear in `CharacterGeneralClosureV4`; device behavior still belongs in final QA |
| F10 Reconciliation checkpoints | IMPLEMENTED | durable checkpoint metadata + create/view in Gestión |
| F11 Own-format backup | IMPLEMENTED / EVIDENCE WEAK | versioned codec/repository + Android import/export; repository tests strong, Android document-provider behavior remains device QA |
| F12 XP or Milestone | IMPLEMENTED | PC Settings mode + progress editor; durable closure state |
| F13 Custom skills | IMPLEMENTED | durable custom skill + Android editor + both Habilidades organization modes + d20 action |
| F14 Languages/proficiencies/training | **MISSING UI** | durable `CharacterProficiency` is tested, but active Android editor wiring has no `CharacterProficiency`/`weaponMasteries` management path |
| F15 Favorite/Quick Access | **PARTIAL** | attacks/traits/spells/forms/companions/class options supported; `RESOURCE` projection exists but Gestión resources cannot be marked favorite |
| F16 Table/read-only mode | IMPLEMENTED | explicit shared interaction policy; structural write guard; PC Settings switch; live controls retained |
| F17 Simple dice roller | IMPLEMENTED | d20 controls on attacks/saves/skills/custom skills; no automatic resolution |
| F18 Temporary effects | IMPLEMENTED | durable state + add/edit/toggle/delete in Gestión |

## 4. D01–D18 traceability

| Requirement | Status | Direct evidence / note |
|---|---|---|
| D01 sticky mini character header | IMPLEMENTED | `CharacterAdaptiveShellV4` keeps header outside scrolling tab content; `EditorHeaderV4` shows identity/save state |
| D02 wide/tablet navigation rail | IMPLEMENTED | `CharacterAdaptiveShellV4` rail presentation |
| D03 wide master-detail | IMPLEMENTED | Equipment, Spells, Forms, Companions and other list-heavy surfaces use wide side editor/detail patterns |
| D04 progressive disclosure | IMPLEMENTED | compact list rows/cards with detail/editor on demand; Background long story collapse |
| D05 visual grammar | IMPLEMENTED, M2 REVIEW | row/card/panel structure is broadly present; M2 will assess duplication/inconsistency rather than re-theme |
| D06 rules/source badges | **PARTIAL** | class identity shows rules/source, but as plain secondary `Text`, not approved semantic badges |
| D07 state badges | **PARTIAL / INCONSISTENT** | spell badges are explicit; comparable equipment/class/other state presentation is not consistently badge-based |
| D08 contextual toolbar | IMPLEMENTED | reusable collection toolbar in Equipment/Traits/Spells/modules |
| D09 compact vs complex editors | IMPLEMENTED | compact operational controls plus larger dialogs/wide panels |
| D10 inline validation | IMPLEMENTED | reusable `CharacterInlineValidationMessage` and local field validation |
| D11 named destructive confirmation | IMPLEMENTED | reusable `CharacterNamedDeleteConfirmationDialog` |
| D12 useful empty state | IMPLEMENTED | reusable `CharacterUsefulEmptyState` used on major collections |
| D13 sticky group headers | IMPLEMENTED | spell levels use `stickyHeader` and collapse controls |
| D14 configurable haptic language | IMPLEMENTED / DEVICE QA | shared UI hook/events + PC Settings toggle; final tactile behavior requires device QA |
| D15 Guardado / Cambios sin guardar | IMPLEMENTED | sticky editor header |
| D16 preserve context | IMPLEMENTED / EVIDENCE WEAK | remember/saveable query/filter/selection state and wide side editors preserve local context; holistic rotation/reopen behavior remains final QA |
| D17 spacing hierarchy | IMPLEMENTED VISUALLY / M2 REVIEW | compact spacing is pervasive, but values are still locally repeated; M2 decides whether token extraction is justified |
| D18 emphasize operational info | IMPLEMENTED | Gestión, Combat operational card, Supercompact, compact list summaries |

## 5. I01–I22 traceability

| Requirement | Status | Direct evidence / note |
|---|---|---|
| I01 hit-die suggestions editable | IMPLEMENTED | official/manual class editor uses hit-die suggestion without legality enforcement |
| I02 class + subclass + rules/source | IMPLEMENTED | `ClassIdentityRowV4` |
| I03 Passive Insight/Investigation | IMPLEMENTED | `CharacterPassiveSkillsV4` |
| I04 custom skills in both layouts | IMPLEMENTED | `CharacterCustomSkillsV4` branches on both skill layout modes |
| I05 combat action/effect at glance | IMPLEMENTED | Combat row `glance` includes type, attack modifier and effect |
| I06 HP quick operations | IMPLEMENTED | damage/heal/temp-HP operational dialog plus exact structural fields |
| I07 contextual death saves | IMPLEMENTED | surfaced at 0 HP in Combat/Gestión |
| I08 total weight + attunement | IMPLEMENTED | Equipment header summary |
| I09 Equipment filters | IMPLEMENTED | collection query/filter infrastructure |
| I10 location/container label | IMPLEMENTED | Equipment metadata includes location and editor |
| I11 Trait grouping/counts | IMPLEMENTED | type/source grouping controls and counts |
| I12 Trait use meter | IMPLEMENTED | limited-use trait meter + spend/recover |
| I13 spell badges | IMPLEMENTED | V/S/M, concentration, ritual, prepared |
| I14 sticky/collapsible spell levels | IMPLEMENTED | `stickyHeader` + show/hide state |
| I15 spell filters | IMPLEMENTED | source/prepared/concentration/ritual and other filters without stored-order mutation |
| I16 note preview | IMPLEMENTED | titled note cards render compact preview before full editor |
| I17 Background compact + collapsible story | IMPLEMENTED | compact identity/personality; long story collapsed/expanded |
| I18 character list summary | **MISSING/PARTIAL** | current list lacks subclass, freshness and optional portrait |
| I19 remember last tab | IMPLEMENTED | persisted `CharacterNavigationPreferenceStore` used on editor open/change |
| I20 unsaved leave Save/Discard/Keep editing | IMPLEMENTED | explicit unsaved dialog + Back guarding |
| I21 real-sheet settings audition | **MISSING** | current font sample is generic text; theme sample is color swatches only |
| I22 one-tap slots/resources + exact edit | IMPLEMENTED | spell slot Use/Recover and resource ± controls; exact edit paths retained |

## 6. Owner-mandatory closure requirements outside F/D/I numbering

Direct inspection supports implementation of:

- app-wide reusable IME-safe editor primitive;
- substantially compact Monedas;
- visible drag feedback + haptic reorder hooks;
- dense ordinary Equipment rows;
- independent Manual/A–Z Equipment sections;
- Manual/A–Z spell presentation without destroying stored order;
- reorder suppression during automatic sort/filter/search states;
- available-width responsive layout choices and navigation rail;
- Application Settings entry from PC Settings;
- lifecycle status moved to PC Settings;
- Supercompact entry from PC Settings;
- structured official/manual class/subclass identity and provenance;
- permissive manual/custom escape path;
- conditional module union/override architecture.

Phone/tablet portrait/landscape, larger text, drag feel, SAF provider behavior and several context/IME details remain **acceptance evidence**, not reasons to restart implementation now.

## 7. Preliminary M2 code-health findings already discovered while tracing scope

These are not the full M2 result yet.

### Confirmed dead transitional UI in `CharacterEditorV4.kt`

The active Overview calls `CharacterClassIdentityCardV4` and lifecycle status is handled in PC Settings, but `CharacterEditorV4.kt` still contains the older private `StatusSelectorV4`, `ClassesCardV4`, `ClassRowV4`, `ClassSelectorV4` and `HitDieSelectorV4` chain. Exhaustive text search of the current file found `ClassesCardV4(` and `StatusSelectorV4(` only at their definitions, not at active call sites.

Classification: **N-safe cleanup**, subject to focused compile/regression gate.

### Central editor mixed responsibility

`CharacterEditorScreenV4` currently coordinates:

- repository-backed sheet + closure state;
- multiple JSON draft codecs;
- dirty-state comparison;
- Back/unsaved behavior;
- tab persistence;
- PC Settings/Supercompact routing;
- backup SAF export;
- Table mode structural policy;
- conditional module visibility;
- persistence synchronization across structural and operational paths.

This is a demonstrated mixed-responsibility hotspot, but **large size alone does not authorize a broad rewrite**. M2 must identify low-risk extraction seams protected by tests before any Batch N refactor is accepted.

### Historical Trasfondo image placeholders are intentional evidence, not dead code by default

`CharacterBackgroundTabV4` still contains two visibly non-functional image placeholders. D-0058 explicitly approved reserving one/two image areas and allowed placeholders until attachment behavior was designed. D-0047 later added a separate working portrait/token reference feature in General. Therefore the Trasfondo placeholders must not be deleted merely because real portrait/token now exists; M2/O should clarify their relationship unless the owner later changes that earlier direction.

## 8. Batch N scope already forced by M1

At minimum Batch N must address:

1. F14 reachable structured proficiency/language management UI over the existing durable model;
2. F15 Resource favorite/Quick Access control using existing `CharacterQuickAccessKind.RESOURCE`;
3. I18 richer character-list card: subclass + freshness + optional portrait;
4. I21 miniature real-sheet settings audition for font/theme/text-scale choices;
5. D06/D07 smallest safe semantic badge normalization necessary to match approved design intent;
6. removal of confirmed dead transitional class/status editor code after focused verification.

M2 may add further remediation items, but it must not widen Batch N into an architecture rewrite without demonstrated need.

## 9. Exact next action

Proceed to **Batch M2 — code-health/static architecture audit**. Inspect duplication, mixed responsibilities, dead/transitional paths, deprecated/TODO markers, repository/persistence concentration, and safe extraction seams. Do not modify production code yet.
