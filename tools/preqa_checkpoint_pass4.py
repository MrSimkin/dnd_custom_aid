from pathlib import Path
import os

ROOT = Path(__file__).resolve().parents[1]

project = ROOT / "docs/PROJECT_STATE.md"
text = project.read_text(encoding="utf-8")
old = "**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the M5/M6 candidate. Active work is iterative UX repair on `implementation/phase4-preqa-ux-repair`. Pass 03 produced review identity version `0.4.0-preqa.3` / build `40300`; formal M6 owner QA remains deferred until the owner says the replacement review build is ready."
new = "**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the M5/M6 candidate. Active work is iterative UX repair on `implementation/phase4-preqa-ux-repair`. Pass 04 produced review identity version `0.4.0-preqa.4` / build `40400`; formal M6 owner QA remains deferred until the owner says the replacement review build is ready."
if text.count(old) != 1:
    raise RuntimeError("PROJECT_STATE current-position anchor mismatch")
project.write_text(text.replace(old, new, 1), encoding="utf-8")

checkpoint = ROOT / "docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_04.md"
checkpoint.write_text(
    f"""# Phase 4 pre-QA UX repair — Pass 04

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; owner visual/device review still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `{os.environ['PRODUCT_SHA']}`  
**Tested product tree:** `{os.environ['PRODUCT_TREE']}`  
**Helper workflow run:** `{os.environ['GITHUB_RUN_ID']}`  
**Review version:** `{os.environ['REVIEW_VERSION']}`  
**Review build:** `{os.environ['REVIEW_BUILD']}`  
**Review APK filename:** `{os.environ['REVIEW_APK']}`  
**Review artifact name:** `{os.environ['REVIEW_ARTIFACT']}`  
**APK SHA-256:** `{os.environ['APK_SHA256']}`  
**APK size:** `{os.environ['APK_SIZE']}` bytes

## Pass 04 completed

- continued the tab-by-tab fixed/sticky audit using a deliberately narrow rule: pin only high-value control/reference bands, not ordinary section headings;
- **Habilidades:** moved the view selector and the three passive reference values outside the detail `LazyColumn`; the detailed ability/save/skill/custom/proficiency content now scrolls independently below them;
- **Gestión:** moved `Estado operativo` outside the management `LazyColumn`, keeping Inspiration, HP reference and contextual death-save controls available while conditions, concentration, resources, rest assistant, temporary effects and reconciliation checkpoints scroll below;
- **Equipo:** promoted the existing summary + Add + search/filter control card to a sticky tools header while object sections and currencies remain scrollable;
- **Rasgos:** promoted the existing summary + Add + search/filter + grouping control card to a sticky tools header while trait groups remain scrollable;
- **Conjuros:** preserved the already-fixed source selector and existing sticky level headers; intentionally did not introduce a competing second sticky hierarchy for search/filter tools in this pass;
- propagated the user spacing-scale preference through the newly fixed bands and the main Habilidades, Gestión, Equipo, Rasgos and Conjuros list margins/gaps touched by this pass, while retaining minimum control sizes/touch targets;
- confirmed the six Gestión editors/previews already use `CharacterImeSafeEditorDialog`; no redundant editor rewrite was introduced;
- removed the temporary Pass 03/Pass 04 helper workflows and transform/checkpoint tools from the tested product commit so the repair branch does not accumulate execution scaffolding;
- Android review identity advanced to version `{os.environ['REVIEW_VERSION']}` / build `{os.environ['REVIEW_BUILD']}`.

## Still open — next repair pass

1. continue the long-collection fixed-control audit for Conjuros as a special nested-sticky case, plus Notas and conditional module collections where the controls genuinely benefit from staying visible;
2. continue spacing-scale propagation through remaining tab-specific margins/paddings/gaps, avoiding intrinsic sizes and minimum touch targets;
3. continue the IME/window audit outside the already-verified reusable dialogs, especially persistent wide/tablet editor panels and any remaining inline editors;
4. exercise expanded column maxima surface-by-surface and cap only layouts that become genuinely unreadable;
5. owner visual audition of fonts/text-size/compactness combinations on phone and tablet;
6. reconcile stale current-stage prose in `README.md`, `MANIFEST.md`, `ROADMAP.md`, `TESTING.md`, `ARCHITECTURE.md` and the historical current-stage paragraph in `AGENTS.md`; `docs/PROJECT_STATE.md` + `docs/checkpoints/LATEST.md` remain the active current-state authority meanwhile;
7. do **not** resume formal M6 until the owner explicitly says the replacement build is ready.

## Verification

The Pass 04 helper gate executed successfully before the tested product commit: `git diff --check`, explicit fixed/sticky-shape checks, version/build identity checks, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend TypeScript check, identified APK copy/hash/size capture and artifact upload.

Automated green is technical evidence only. Phone/tablet visual acceptance has not yet been performed for this pass.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Continue with the remaining long-collection fixed-control audit, spacing-scale propagation and IME/window audit. Do not return to historical M6 unless the owner explicitly requests comparison.
""",
    encoding="utf-8",
)

latest = ROOT / "docs/checkpoints/LATEST.md"
latest.write_text(
    f"""# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `{os.environ['REVIEW_VERSION']}` / build `{os.environ['REVIEW_BUILD']}`  
**Current pass:** Phase 4 pre-QA UX repair Pass 04  
**Detailed checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_04.md`

The owner explicitly reopened pre-QA implementation after the historical M6 pause. Formal M6 QA is deferred. Resume with the detailed checkpoint above and continue the remaining long-collection fixed-control audit, spacing-scale propagation and IME/window audit.
""",
    encoding="utf-8",
)

print("Pass 04 checkpoint written.")
