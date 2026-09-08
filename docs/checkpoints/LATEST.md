# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `0.4.0-preqa.7` / build `40700` / `debug`  
**Current technical state:** focused pre-QA UX repair Pass 07 green/stable; no new product build started during owner audition  
**Current governance state:** current-stage prose reconciled through D-0047/Pass 07  
**Owner audition state:** IN PROGRESS — primary phone Stage A/B/C/D recorded; Stage E in progress through corrected E2  
**Detailed checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_E.md`  
**Prior Stage D checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_D.md`  
**Owner test devices:** `docs/TEST_DEVICES.md`  
**Owner audition guide:** `docs/PREQA_OWNER_VISUAL_AUDITION.md`

Primary recorded phone test device: **Redmi Note 11 Pro 5G**. Build `0.4.0-preqa.7` / `40700` installed and launched successfully, and `Ajustes -> Acerca de` confirmed the expected version/build/type.

### Recorded phone evidence

- Typography: Mona Sans Condensed rejected as too compressed/difficult to read; League Spartan rejected as visually unpleasant; PT Sans Narrow remains provisional pending a satisfactory Bold presentation; other sampled candidates pass this elimination stage.
- Text scale: very large sizes are difficult but the warning is clear/acceptable; very small sizes are intentionally very small and the owner wants the range retained.
- Spacing: 100% has too much whitespace; 80% still has a lot but is acceptable; 60% is good for the owner; owner wants to audition 40% in a future build.
- Dialog/window spacing: owner observed that compactness appears not to apply consistently. A read-only technical audit confirmed credible fixed/framework spacing sources in dialogs. Audit workflow `34174202234` succeeded; its temporary workflow was removed; **no Pass 08 product implementation/build was started**.
- Stage D portrait density: 1–2 columns comfortable; 3+ cramped for readability; buttons remain comfortable; no overlap/clipping.
- Stage D phone landscape: major responsive-mode finding — vertical tabs and tablet-like/master-detail presentation appear on phone landscape. In Equipo, a large persistent `Equipo nuevo` right-side pane consumes disproportionate space.
- Stage D reordering: cards in multi-column presentation can be moved vertically but not laterally; later UX/implementation audit required.
- Stage D rotation: selected tab survives, but scroll/list position resets to the start.
- Stage D Gestión: fixed `Estado operativo` occupies almost the entire phone-landscape viewport, leaving effectively no visible room for the rest of the tab.
- Stage E1 general Notes editor: PASS on the phone. Owner corrected the earlier attribution; no E1-specific action or rotation defect is recorded.
- Stage E2 titled-note editor: extremely long text remains reachable by scrolling with the IME visible.
- Stage E2 action reachability: required editor buttons are unreachable while the software keyboard remains visible — major.
- Stage E2 rotation: active titled-note editor disappears in landscape and reappears after returning to portrait — major; state appears to survive but the editing surface is not continuously available.

The detailed checkpoints contain the accumulated next-build backlog, including 40% spacing, dialog/window spacing consistency, PT Sans Narrow Bold verification, Settings UX/credits polish, phone-landscape responsive behavior, scroll-position preservation, fixed-footprint issues and titled-note IME/editor-orientation failures.

### Owner QA workflow rule

Continue the staged QA/audition and **record findings instead of implementing each one immediately**. Batch recorded findings into a later identified repair build only when the owner explicitly requests it or a blocking defect prevents meaningful continuation.

Build `40700` remains the active technically verified pre-QA owner-audition build, not a frozen formal M6 candidate.

**Next action:** continue Stage E with the Equipo editor on the Redmi Note 11 Pro 5G. Test editing with the software keyboard visible, verify focused-field and cancel/save reachability, and rotate once with the editor open to determine whether the titled-note disappear/reappear behavior is shared.

Historical M5/L frozen branches remain immutable evidence. `main` remains untouched. DM work remains blocked until Phase 4 QA/closure/merge is accepted and explicitly approved.
