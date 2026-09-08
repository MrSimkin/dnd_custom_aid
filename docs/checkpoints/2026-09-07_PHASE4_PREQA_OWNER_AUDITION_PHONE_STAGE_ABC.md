# Phase 4 pre-QA owner audition — phone Stage A/B/C

**Date:** 2026-09-07  
**Status:** OWNER PHONE AUDITION IN PROGRESS; typography, text-scale and spacing evidence recorded  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary phone:** Redmi Note 11 Pro 5G

## Preconditions already passed

- APK installed and launched successfully on the owner phone;
- `Ajustes -> Acerca de` confirmed expected version/build/type;
- owner has a representative populated character available for review.

## Stage A — typography evidence

Owner observations:

- **Mona Sans Condensed:** rejected — too tight/compressed and difficult to read;
- **League Spartan:** rejected — visually unpleasant to the owner;
- **PT Sans Narrow:** provisional only — the owner likes the underlying direction but considers the current presentation too light; keep it as a candidate only if a satisfactory Bold presentation is available/usable;
- **all other sampled candidates:** pass at this stage.

Do not force a final font winner yet. This remains an elimination/approval pass rather than a final branding choice.

## Stage B — application text scale evidence

- very large application zoom/text sizes are difficult to read/use, but the in-app warning is clear and the owner considers that behavior acceptable;
- very small application sizes are genuinely very small, but the owner explicitly likes having those options and wants the range retained;
- no text-scale range reduction is requested from this observation.

Stage B is sufficiently reviewed on the primary phone for this pre-QA pass.

## Stage C — whitespace compactness evidence

Owner comparison on the primary phone:

- **100% spacing:** too much whitespace;
- **80% spacing:** still a lot of whitespace, but acceptable;
- **60% spacing:** good for the owner;
- **40% spacing:** not currently available; the owner explicitly wants to try it in a future build before closing the preferred lower bound.

The owner also observed that the spacing preference appears not to apply consistently to windows/dialogs.

### Technical audit performed during QA

A read-only branch audit was run after the owner observation to determine whether the perceived dialog inconsistency had a code basis. It did **not** modify product code.

Audit evidence:

- direct `Dialog`/`AlertDialog` callsites were inventoried across the Android UI;
- the shared IME-safe editor dialog already scales its outer margin and vertical content gaps, but retains fixed internal padding (`12/12/12/8 dp`) and fixed action-row gaps;
- the unsaved-changes dialog retains fixed internal padding/gaps;
- several Material `AlertDialog` callsites use framework dialog chrome/internal spacing that is not controlled by the application `spacingScalePercent`, even when inner content already uses `appSpacingV4`;
- therefore the owner's observation is technically credible: application spacing is not yet uniformly reflected in dialog/window whitespace.

Audit workflow `34174202234` completed successfully and its temporary workflow file was removed afterwards. No Pass 08 product implementation/build was started.

Stage C is considered sufficiently reviewed on build `40700` for the current phone audition. The missing 40% option and dialog/window consistency are **recorded findings for a later repair build**, not reasons to interrupt the current QA sequence.

## Accumulated findings/backlog for the next repair build

These are recorded only. Do not implement them piecemeal during the ongoing QA unless the owner explicitly changes the workflow.

1. **Spacing lower-bound audition:** add a 40% application-spacing option so the owner can compare it against the currently preferred 60%.
2. **Dialog/window spacing consistency:** make app-owned dialog/window margins, internal paddings and gaps respond consistently to the application spacing preference where safe, while preserving intrinsic control sizes, minimum field sizes and touch targets.
3. **PT Sans Narrow weight check:** verify whether a satisfactory Bold presentation is available/usable before deciding whether PT Sans Narrow remains in the candidate set.
4. **One deferred final Settings UX polish note:** improve visual examples for text-size choices, reduce explanatory/source text around font choices, and retain appropriate acknowledgements/credits/licensing for original font/material authors. Treat this as one bounded final-polish item rather than multiple immediate tasks.
5. **Typography rejects from phone audition:** Mona Sans Condensed and League Spartan are not owner-preferred candidates. Do not promote them as finalists; whether they are later removed from the menu is a cleanup decision, not a current QA blocker.

## QA workflow rule reinforced by owner

During owner QA/audition:

- run the requested test stage;
- record owner answers and concrete findings;
- add technical observations only when they help characterize/reproduce the finding;
- accumulate the repair list for the next identified build;
- do **not** stop the QA flow to implement each finding immediately;
- only produce a repair build when the owner explicitly asks to batch the recorded findings or when a blocking defect prevents meaningful continuation.

## Next action

Proceed to **Stage D — card columns and rotation** on the Redmi Note 11 Pro 5G using build `40700`.

Start with portrait and the default column setting, then test user-selected higher column counts on representative card surfaces. Rotate to landscape and confirm practical reflow/context preservation. Record readability/tappability limits and any surface-specific behavior; do not repair findings during the stage.
