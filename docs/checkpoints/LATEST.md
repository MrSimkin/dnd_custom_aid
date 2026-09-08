# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `0.4.0-preqa.7` / build `40700` / `debug`  
**Current technical state:** focused pre-QA UX repair Pass 07 green/stable; no new product build started during owner audition  
**Current governance state:** current-stage prose reconciled through D-0047/Pass 07  
**Owner audition state:** IN PROGRESS — primary phone Stage A/B/C recorded; Stage D next  
**Detailed checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_ABC.md`  
**Owner test devices:** `docs/TEST_DEVICES.md`  
**Owner audition guide:** `docs/PREQA_OWNER_VISUAL_AUDITION.md`

Primary recorded phone test device: **Redmi Note 11 Pro 5G**. Build `0.4.0-preqa.7` / `40700` installed and launched successfully, and `Ajustes -> Acerca de` confirmed the expected version/build/type.

### Recorded phone evidence

- Typography: Mona Sans Condensed rejected as too compressed/difficult to read; League Spartan rejected as visually unpleasant; PT Sans Narrow remains provisional pending a satisfactory Bold presentation; other sampled candidates pass this elimination stage.
- Text scale: very large sizes are difficult but the warning is clear/acceptable; very small sizes are intentionally very small and the owner wants the range retained.
- Spacing: 100% has too much whitespace; 80% still has a lot but is acceptable; 60% is good for the owner; owner wants to audition 40% in a future build.
- Dialog/window spacing: owner observed that compactness appears not to apply consistently. A read-only technical audit confirmed credible fixed/framework spacing sources in dialogs. Audit workflow `34174202234` succeeded; its temporary workflow was removed; **no Pass 08 product implementation/build was started**.

The detailed checkpoint contains the accumulated next-build backlog, including 40% spacing, dialog/window spacing consistency, PT Sans Narrow Bold verification and the single deferred final Settings UX/credits note.

### Owner QA workflow rule

Continue the staged QA/audition and **record findings instead of implementing each one immediately**. Batch recorded findings into a later identified repair build only when the owner explicitly requests it or a blocking defect prevents meaningful continuation.

Build `40700` remains the active technically verified pre-QA owner-audition build, not a frozen formal M6 candidate.

**Next action:** Stage D card-column opt-in and rotation on the Redmi Note 11 Pro 5G. Test portrait defaults and higher user-selected column counts on representative card surfaces, then landscape/reflow/context preservation. Record readability/tappability limits and surface-specific behavior without repairing during the stage.

Historical M5/L frozen branches remain immutable evidence. `main` remains untouched. DM work remains blocked until Phase 4 QA/closure/merge is accepted and explicitly approved.
