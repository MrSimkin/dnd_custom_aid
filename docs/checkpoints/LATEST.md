# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `0.4.0-preqa.7` / build `40700` / `debug`  
**Current technical state:** focused pre-QA UX repair Pass 07 green/stable; no new product build started during owner audition  
**Current governance state:** current-stage prose reconciled through D-0047/Pass 07  
**Owner audition state:** IN PROGRESS — primary phone Stage A/B/C/D recorded; Stage E in progress through E4  
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
- Stage E1 general Notes editor: PASS on the phone.
- Stage E2 titled-note editor: extremely long text remains reachable by scrolling, but required action buttons are unreachable with the IME visible; editor disappears in landscape and returns in portrait — major.
- Stage E3 Equipo editor: same major pattern, with stronger form-navigation evidence: higher-field focus leaves lower fields hidden by the keyboard; focusing a lower field can reveal it but leaves Save/Cancel covered. Keyboard dismissal is required to traverse the whole editor comfortably. Rotation reproduces E2.
- Stage E4 Rasgos editor: same E3 behavior and rotation failure — major.
- Historical B1a intended the shared `CharacterImeSafeEditorDialog` to keep editable content scrollable while `Guardar`/`Cancelar` remain reachable above the keyboard. Current E2/E3/E4 device evidence shows that runtime guarantee is not holding across multiple modal editor families; later repair analysis should treat this as a shared infrastructure/regression candidate rather than isolated surface fixes.

The detailed checkpoints contain the accumulated next-build backlog, including 40% spacing, dialog/window spacing consistency, PT Sans Narrow Bold verification, Settings UX/credits polish, phone-landscape responsive behavior, scroll-position preservation, fixed-footprint issues and shared modal-editor IME/orientation failures.

### Owner QA workflow rule

Continue the staged QA/audition and **record findings instead of implementing each one immediately**. Batch recorded findings into a later identified repair build only when the owner explicitly requests it or a blocking defect prevents meaningful continuation.

Build `40700` remains the active technically verified pre-QA owner-audition build, not a frozen formal M6 candidate.

**Next action:** continue Stage E with **E5 — Conjuro editor** on the Redmi Note 11 Pro 5G. With the keyboard visible, move between upper and lower spell fields, verify full field plus Save/Cancel reachability, and rotate portrait -> landscape -> portrait to determine whether the shared E2/E3/E4 failure pattern repeats.

Historical M5/L frozen branches remain immutable evidence. `main` remains untouched. DM work remains blocked until Phase 4 QA/closure/merge is accepted and explicitly approved.
