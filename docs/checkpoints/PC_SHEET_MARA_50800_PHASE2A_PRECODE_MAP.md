# Mara 50800 — Phase 2A pre-code QA/source map

**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** PRE-CODE MAP / PHASE 2A ACTIVE  
**Acceptance authority:** `2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Phase 1B handoff:** `2026-09-29_PC_SHEET_MARA_50800_PHASE1B_CLOSURE.md`

## Exact 50800 evidence reopened before code

Owner artifact inputs reopened from the conversation:

- `Mara_PCSheet_CustomV2_Atributo_preqa8_build50800.pdf`: pages 1, 5 and 6;
- `Mara_PCSheet_CustomV2_Habilidad_preqa8_build50800.pdf`: pages 1 and 5.

Observed facts that govern this phase:

1. Page 1 already fits the six standard attributes using the native compact Custom-v2 attribute grammar.
2. Atributo page 5 shows only Fortuna, Cordura and Éter in three oversized generic columns.
3. Atributo page 6 exists only to place Renombre, while two empty/fake attribute shells remain visible.
4. Habilidad page 5 contains the four real custom attributes but still paints two empty/fake attribute shells.
5. Both 50800 Custom Statistics outputs expose `ETE · Éter`; the acceptance target is an unambiguous clean semantic `Éter`.
6. The extracted text of the 50800 Extended pages contains unrelated source-page strings such as `EQUIPO`, `TRASFONDO`, `EQUIPO ESPECIAL` and `UBICACIÓN`, even though they are not visibly part of the Custom Statistics layout.
7. The page-1 character name `Mara de los Siete Umbrales` is forced into one ribbon line; owner direction requires horizontal + vertical centering in the ribbon and a two-line centered fallback when one line is not clean.

## Matrix scope

This phase is restricted to:

- M50800-03 — stale source-underlay text leakage;
- M50800-04 — reuse native Custom Statistics attribute grammar;
- M50800-05 — all four Mara attributes together at native scale;
- M50800-06 — no phantom attribute shells;
- M50800-07 — clean `Éter`;
- M50800-25 — character-name ribbon centering / wrap.

No Traits, Notes, Equipment packing, Fantasy layout or general adaptive-packing work belongs in this phase.

## Current implementation mismatch

### Custom Statistics

`DesktopCustomV2ExtendedRenderer.kt` currently has two separate Extended grammars:

- per-Attribute: three 184-pt wide generic cards, each 244 pt high, with `ATTRIBUTE_COLUMNS_PER_PAGE = 3`;
- per-Ability: a six-slot vertical attribute column, but STRUCTURE paints all six slots even when only four real attributes exist.

The 50800 owner QA rejects both behaviors as currently rendered.

### Page-1 native authority

`DesktopPcSheetTemplateProofRenderer.kt` is the page-1 authority.

It demonstrates:

- six native attribute modules on one page;
- native score/modifier scale;
- native compact save/skill row grammar;
- distinct per-Attribute vs per-Ability semantics.

Phase 2A must preserve those semantic differences while using the same native-scale attribute module vocabulary.

### Source-underlay leak

`DesktopCustomV2ExtendedRenderer.Resources.buildVectorLogoForm` currently creates a clipped form by drawing an imported **whole source page** inside a small logo bbox.

Visual clipping hides the rest of the page, but text extraction can still see the source page's unrelated text. That is the confirmed M50800-03 mechanism.

Planned correction: make the header logo a raster crop of the logo region only, so unrelated source text is not embedded behind the page.

### Character ribbon

`DesktopPcSheetTemplateProofRenderer.drawCustomV2Common` calls `fillCenteredTextPx`, whose current policy is `SINGLE_LINE / maximumLines=1`.

Planned correction: a dedicated Custom-v2 name-ribbon helper using the native ribbon center, centered horizontal/vertical alignment, `WORD_WRAP`, maximum 2 lines, and no optical offset hack.

## Fixture facts relevant to capacity

Mara has four custom attributes:

- Fortuna (FOR);
- Cordura (COR);
- Éter (ETE);
- Renombre (REN).

Only three custom skills are linked directly to custom attributes:

- Contratos arcanos → Fortuna;
- Sincronía → Cordura;
- Manipulación de éter → Éter.

Renombre has no custom-linked skill.

The remaining custom skills are linked to standard INT/SAB/CAR abilities.

Therefore there is no data-driven reason for a second Custom Statistics page for Mara.

## Implementation rules

### Per-Attribute

- one page for Mara;
- native-scale compact attribute modules, vertically packed as page 1 demonstrates;
- render only actual attributes;
- each module contains its own save + custom-linked skill rows;
- standard-bound custom skills stay in a separate compact section;
- definitions/notes remain separate from attribute identity;
- no `keyedName` fallback that turns Éter into `ETE · Éter`.

### Per-Ability

- retain the semantic three-section model: attributes / saving throws / skills;
- render only the four real attribute modules in the left native-scale column;
- do not paint slots 5–6 when there is no attribute;
- clean attribute names.

### Shared header

- logo crop contains only logo pixels;
- no source-page text leakage into Extended text extraction.

### Ribbon

- page-1 ribbon only;
- same native ribbon geometry;
- center horizontally and vertically;
- use up to two centered lines before reducing below the preferred readable size.

## Acceptance gates before Phase 2A closure

For each real Mara Custom-v2 family:

1. exactly one page contains `ESTADÍSTICAS PERSONALIZADAS`;
2. that page contains Fortuna, Cordura, Éter and Renombre;
3. it does not contain `ETE · Éter`;
4. it does not contain unrelated source-underlay text such as `EQUIPO ESPECIAL` / `UBICACIÓN`;
5. rendered page shows no phantom fifth/sixth attribute shell;
6. modules are visually native-scale relative to page 1;
7. page-1 ribbon is visually centered and wraps to two lines if needed;
8. Desktop/Android renderer sync remains green;
9. exact CI artifact is inspected before any M50800 item is marked phase-pass.

Tests may support these gates, but rendered PDFs remain authoritative for M50800-04/05/06/25.
