# Phase 4A — Material 3 audit before P12

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Pre-audit implementation HEAD: `0e7ae6b8e07681273feb4abf952868c33f3e06a9`
Material-3 repair commit: `09a1a281abc2c9a90bd9b3866d8ab32efb019d5e`

## Scope and authority

This checkpoint records the PC-side P1–P11 Material 3 audit required before P12. It is not DM-side discovery. `Material 3` here means Jetpack Compose `androidx.compose.material3`.

The audit compares current production Compose code with the approved Phase 4A repair decisions. A component merely being Material 3 is not a defect; the question is whether stock M3 geometry or behavior is materially driving the PC UI away from the approved compact/responsive result.

## Results

| P | Surface | M3 relevant? | Classification | Action |
|---|---|---|---|---|
| P1 | Canonical HP state across General / Combate / shared state | No material geometry dependency | UNRELATED TO MATERIAL 3 | None. State synchronization is model/wiring work. |
| P2 | `Daño / Curar` operational row | Yes | SAFE WITH CUSTOMIZATION | Explicit compact field height, project spacing and bounded operational layout retained. |
| P3 | Habilidades ordinary/custom presentation rows | Yes | REPAIR RECOMMENDED → SAFE WITH CUSTOMIZATION | Ordinary M3 `OutlinedButton` reserves a 48dp interaction/layout footprint while the custom read-only projection had only a 44x34 footprint. The custom projection now reserves the matching 48dp footprint while keeping its visible 44x34 surface. No ownership/editing semantics were changed by this M3 audit. |
| P4 | Structured damage editor | Yes | REPAIR RECOMMENDED → SAFE WITH CUSTOMIZATION | Stock M3 controls were allowed to impose their default vertical geometry/padding on the compact damage grammar. Relevant dice/type controls are now explicitly constrained to a 48dp compact interaction height and selector content padding is reduced. Parsing/model/persistence semantics are unchanged. |
| P5 | Combate fixed quick-reference footprint | Yes, mostly container/decorative | SAFE WITH CUSTOMIZATION | Region sizing, padding and responsive behavior are project-controlled rather than left to stock component geometry. |
| P6 | Direct drag-and-drop reorder | Low | SAFE WITH CUSTOMIZATION | Drag geometry is Foundation/pointer/measurement driven; M3 surfaces do not control reorder mechanics. |
| P7 | Provenance / origin editor | Yes | SAFE WITH CUSTOMIZATION | Compact project surface + `BasicTextField` primitives already override stock text-field geometry; menus remain appropriate selection popups. |
| P8 | Trasfondo photo tile/viewer | Yes, container only | SAFE WITH CUSTOMIZATION | Tile aspect ratio and viewer bounds are explicit; the viewer uses a low-level full-window dialog rather than stock alert-dialog geometry. |
| P9 | Shared editor sizing / IME-safe editor | Yes | SAFE WITH CUSTOMIZATION | Uses low-level `Dialog(usePlatformDefaultWidth = false)` plus explicit width/height/IME/navigation bounds. |
| P10 | Settings density / pseudo-icons | Yes | SAFE WITH CUSTOMIZATION | Density deliberately scales project whitespace while preserving text/icon usability floors; graphical controls replace pseudo-icons. Global suppression of M3 touch floors would conflict with this decision. |
| P11 | Theme selector | Yes | SAFE WITH CUSTOMIZATION | Custom surface cards, explicit swatches/selection indicator and responsive 2/3/4-column layout avoid stock selector geometry. |

## Shared-root-cause conclusion

There is no evidence that Material 3 should be removed or globally suppressed. The recurring risk is using full stock control geometry where the approved PC surface is intentionally compact.

A global override of M3 minimum interactive sizing would be the wrong repair: it would trade viewport density for unsafe/smaller interaction targets and would conflict with P10's usability-floor decision. The durable rule is local: keep comfortable interaction footprints, but explicitly control visible geometry, padding, and bounded editor/dialog behavior where the PC contract requires compactness.

## Repairs applied before P12

1. **P3 — Habilidades geometry:** the custom-skill training projection now uses a 48dp outer layout footprint matching the ordinary M3 interactive selector while retaining its 44x34 visible surface. This removes the M3-specific row-height/column-footprint divergence without changing where custom-skill structural editing is owned.
2. **P4 — structured damage geometry:** the dice quantity/sign/die/modifier controls, custom-die field, damage-type selector/custom type, and flat-damage field now use an explicit 48dp compact interaction height; compact selector content padding is explicit as well.

The audit intentionally did **not** disable M3 minimum touch sizing globally and did not redesign unrelated UI.

## Verification and owner QA boundary

The source repair was made by the branch's established guarded, self-removing temporary-workflow pattern. Normal branch scaffold checks must run on this checkpoint commit, which includes the repaired source tree.

Physical owner-device QA remains pending and must not be inferred from automation. In particular, owner QA should still inspect:

- P3 ordinary/custom visual alignment at normal and larger font scales;
- P4 structured damage density and label clipping on phone landscape;
- the previously recorded P1–P11 owner-device acceptance items.

## Exact continuation point

After the scaffold checks for this checkpoint are green, continue directly with P12. `CharacterHelpV4` INFO mode still uses `DropdownMenu`; P12 must replace that opened-state behavior with the approved genuinely anchored/floating contextual rich tooltip/popover while preserving the compact graphical info control and `ALWAYS_VISIBLE` behavior.
