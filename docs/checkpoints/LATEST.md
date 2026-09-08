# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `0.4.0-preqa.7` / build `40700` / `debug`  
**Current technical state:** focused pre-QA UX repair Pass 07 green/stable; no new product build started during owner audition  
**Current governance state:** current-stage prose reconciled through D-0047/Pass 07  
**Owner audition state:** IN PROGRESS — primary phone Stage A/B/C/D/E complete; Stage F in progress through Gestión + Habilidades  
**Detailed Stage F checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md`  
**Stage E checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_E.md`  
**Prior Stage D checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_D.md`  
**Owner test devices:** `docs/TEST_DEVICES.md`  
**Owner audition guide:** `docs/PREQA_OWNER_VISUAL_AUDITION.md`

Primary recorded phone test device: **Redmi Note 11 Pro 5G**. Build `0.4.0-preqa.7` / `40700` installed and launched successfully, and `Ajustes -> Acerca de` confirmed the expected version/build/type.

### Recorded phone evidence

- Typography: Mona Sans Condensed rejected as too compressed/difficult to read; League Spartan rejected as visually unpleasant; PT Sans Narrow remains provisional pending a satisfactory Bold presentation; other sampled candidates pass this elimination stage.
- Text scale: very large sizes are difficult but the warning is clear/acceptable; very small sizes are intentionally very small and the owner wants the range retained.
- Spacing: 100% has too much whitespace; 80% still has a lot but is acceptable; 60% is good for the owner; owner wants to audition 40% in a future build.
- Dialog/window spacing: compactness does not apply consistently; fixed/framework dialog spacing remains a later repair target.
- Stage D portrait density: 1–2 columns comfortable; 3+ cramped for readability; buttons remain comfortable; no overlap/clipping.
- Stage D phone landscape: major responsive-mode finding — vertical tabs and tablet-like/master-detail presentation appear on phone landscape. Current code confirms side rail is selected from width alone at `>=760dp`.
- Stage D reordering: cards in multi-column presentation can be moved vertically but not laterally; later UX/implementation audit required.
- Stage D rotation: selected tab survives, but scroll/list position resets to the start.
- Stage E1 general Notes editor: PASS.
- Stage E2-E7: titled Notes, Equipo, Rasgos, Conjuros, conditional modules and Gestión reproduce the same major IME/editor family: lower content/actions cannot all remain practically reachable with keyboard visible, and active editor composition is disrupted by rotation. Stage E is complete for the phone with findings recorded.
- Stage E5 also records a minor spell-level leading-zero replacement/normalization defect.
- Stage F Gestión: fixed `Estado operativo` is too large, especially on phone landscape. Death saves use excessive vertical space and should be redesigned toward one compact row with clearer controls/icons.
- Stage F Gestión cross-tab coherence: General can show edited PG above 0 while Gestión may still render persisted 0-HP/death-save state before save; later repair must distinguish unsaved-draft coherence from any saved-state functional defect.
- Stage F Habilidades: fixed selector/passive-reference band earns its place, but Percepción/Perspicacia/Investigación should explicitly communicate that they are **pasivas**. Owner prefers horizontal label+value presentation with separators to recover a few vertical pixels.
- Cross-cutting UX direction: do not consume two rows when one clearer row is practical; preserve accessibility/tapability.
- Cross-cutting help-text direction: App Settings should allow explanatory text as **Siempre visible / tooltip circled-i / Oculto**.
- Cross-cutting navigation direction: App Settings should allow user reordering of character tabs while conditional-module visibility logic remains intact.

The accumulated next-build backlog includes 40% spacing, dialog/window spacing consistency, PT Sans Narrow Bold verification, Settings UX/credits polish, phone-landscape responsive behavior, scroll-position preservation, compact sticky/fixed footprints, shared modal-editor IME/orientation repair, help-text presentation preference, tab-order preference, and compact death-save/passive-skill layouts.

### Owner QA workflow rule

Continue the staged QA/audition and **record findings instead of implementing each one immediately**. Batch recorded findings into a later identified repair build only when the owner explicitly requests it or a blocking defect prevents meaningful continuation.

Build `40700` remains the active technically verified pre-QA owner-audition build, not a frozen formal M6 candidate.

**Next action:** continue Stage F with **F3 — Conjuros** on the Redmi Note 11 Pro 5G. Inspect the combined permanent footprint of source selector + spell toolbar + sticky current-level header in portrait first, then landscape, and report which layers earn their space or should be merged/condensed.

Historical M5/L frozen branches remain immutable evidence. `main` remains untouched. DM work remains blocked until Phase 4 QA/closure/merge is accepted and explicitly approved.
