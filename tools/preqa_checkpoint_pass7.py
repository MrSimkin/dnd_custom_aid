from pathlib import Path
import os

ROOT = Path(os.environ["GITHUB_WORKSPACE"]).resolve()

project = ROOT / "docs/PROJECT_STATE.md"
text = project.read_text(encoding="utf-8")
old = "**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the M5/M6 candidate. Active work is iterative UX repair on `implementation/phase4-preqa-ux-repair`. Pass 06 produced review identity version `0.4.0-preqa.6` / build `40600`; formal M6 owner QA remains deferred until the owner says the replacement review build is ready."
new = "**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the M5/M6 candidate. The focused implementation repair line is technically stable through Pass 07 on `implementation/phase4-preqa-ux-repair`, review identity version `0.4.0-preqa.7` / build `40700`; owner visual/device audition and governance reconciliation remain before any replacement formal M6 candidate is frozen."
if text.count(old) != 1:
    raise RuntimeError("PROJECT_STATE current-position anchor mismatch")
project.write_text(text.replace(old, new, 1), encoding="utf-8")

checkpoint = ROOT / "docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md"
checkpoint.write_text(
    f"""# Phase 4 pre-QA UX repair — Pass 07

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; focused technical repair line stable; owner visual/device audition still pending  
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

## Pass 07 completed

- ran a repository-wide Android UI audit for remaining numeric `Arrangement.spacedBy(N.dp)` gaps after the focused Pass 03–06 repairs;
- converted every remaining raw numeric `Arrangement.spacedBy` in the Android UI package to `Arrangement.spacedBy(appSpacingV4(...))`, making child-to-child whitespace consistently honor the owner spacing preference across General, class identity, Habilidades helpers, Gestión internals, spell helpers, conditional modules, PC settings, dice UI, app/list surfaces and other residual character UI;
- deliberately did **not** mass-convert remaining `Modifier.padding` or `PaddingValues`: several are part of badge geometry, clickable row/control footprint, drawing geometry or other intrinsic sizing and therefore are outside the safe global whitespace sweep;
- preserved representative geometry boundaries explicitly, including the character-editor Canvas inset and semantic-badge intrinsic padding;
- preserved all Pass 06 readability caps: Combate/Rasgos/Notas max 4, special Equipo max 3, ordinary Equipo wide max 5, currencies under their dedicated 3/6 grid rule;
- re-confirmed the IME audit conclusion: the reusable collection search field remains the only text-input owner without its own local inset marker, and its current call sites are protected by their owning IME-safe surfaces; no nested inset was added;
- produced `docs/PREQA_OWNER_VISUAL_AUDITION.md`, a staged owner-device audition guide covering typography, application text scale, whitespace compactness, column maxima, rotation, keyboard behavior and the accumulated fixed/sticky footprint;
- removed the temporary Pass 07 audit/helper/transform/checkpoint infrastructure from the tested product commit;
- Android review identity advanced to version `{os.environ['REVIEW_VERSION']}` / build `{os.environ['REVIEW_BUILD']}`.

## Technical stopping rule reached

The automated repair line has now completed the planned fixed/sticky, spacing-scale, IME/window and card-column audits. Further speculative visual changes are **not recommended before owner device audition**: the remaining questions are perceptual/ergonomic and need phone/tablet observation rather than more static code inference.

This is not formal M6 acceptance. Automated green remains technical evidence only.

## Still open

1. reconcile stale current-stage prose in `README.md`, `MANIFEST.md`, `ROADMAP.md`, `TESTING.md`, `ARCHITECTURE.md` and the historical current-stage paragraph in `AGENTS.md` now that the focused implementation line is stable;
2. owner executes the staged visual audition in `docs/PREQA_OWNER_VISUAL_AUDITION.md` on phone and tablet and reports findings;
3. repair any concrete owner-observed blocking/regression findings, if any, with a new identified build rather than speculative polishing;
4. when the owner explicitly says the replacement build is ready, freeze that exact candidate and resume formal M6 QA from the refreshed governance state;
5. do **not** begin DM implementation before Phase 4 closure/merge approval.

## Verification

The Pass 07 helper gate executed successfully before the tested product commit: `git diff --check`, global raw-Arrangement elimination checks, preserved geometry/readability-cap checks, version/build identity checks, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend TypeScript check, identified APK copy/hash/size capture and artifact upload.

The preceding Pass 07 audit workflow inventoried residual raw Arrangement gaps, raw padding forms, current `appSpacingV4` adoption and IME ownership before implementation.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Reconcile current-stage governance prose, then use `docs/PREQA_OWNER_VISUAL_AUDITION.md` for owner device audition. Do not reopen speculative UX implementation or historical M6 unless concrete owner findings or an explicit owner QA-ready instruction require it.
""",
    encoding="utf-8",
)

latest = ROOT / "docs/checkpoints/LATEST.md"
latest.write_text(
    f"""# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `{os.environ['REVIEW_VERSION']}` / build `{os.environ['REVIEW_BUILD']}`  
**Current pass:** Phase 4 pre-QA UX repair Pass 07 — focused technical repair line stable  
**Detailed checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`
**Owner audition guide:** `docs/PREQA_OWNER_VISUAL_AUDITION.md`

The owner explicitly reopened pre-QA implementation after the historical M6 pause. The planned automated UX repair audits are complete through Pass 07. Formal M6 remains deferred. Resume by reconciling stale current-stage governance prose, then run the staged owner phone/tablet visual audition. Further UX code should respond to concrete owner findings rather than speculative polishing.
""",
    encoding="utf-8",
)

audition = ROOT / "docs/PREQA_OWNER_VISUAL_AUDITION.md"
audition.write_text(
    f"""# Pre-QA owner visual audition

**Prepared:** 2026-09-07  
**Target review identity:** `{os.environ['REVIEW_VERSION']}` / build `{os.environ['REVIEW_BUILD']}`  
**Purpose:** controlled visual/ergonomic audition before an exact replacement candidate is frozen for formal M6 QA.

This is intentionally **not** the full M6 functional checklist. It answers the visual questions that automated tests cannot settle: density, readability, fixed-control footprint, orientation behavior, keyboard ergonomics and whether owner-selected high column counts remain useful.

## 1. Preconditions

- install the exact identified APK from the Pass 07 checkpoint;
- open **Ajustes -> Acerca de** and verify version `{os.environ['REVIEW_VERSION']}` and build `{os.environ['REVIEW_BUILD']}` before recording findings;
- use a representative character with enough data to populate Equipo, Rasgos, Conjuros, Notas, Gestión and at least one conditional module;
- perform the audition on one phone and one tablet when available, in both portrait and landscape;
- do not treat a preference combination as a defect merely because it is personally unattractive; record a defect when content overlaps, clips, becomes unusable, loses required controls or creates an unreasonable permanent footprint.

## 2. Stage A — typography shortlist

Keep application text at **100%**, spacing at **80%** and card columns at the defaults (phone 1/2, tablet 2/3). Compare these representative families first:

1. Manrope — current baseline;
2. Source Sans 3 — conventional high-legibility sans;
3. Roboto Condensed — compact reference;
4. IBM Plex Sans Condensed — alternate compact reference;
5. Mona Sans Condensed — bundled condensed candidate;
6. Geist — bundled modern candidate;
7. Inter — neutral UI reference;
8. Sora — geometric contrast candidate.

Shortlist no more than 2–3 before continuing. The remaining available fonts can be sampled only if none of these is satisfactory.

Check especially: long spell/trait names, numeric fields, badges, toolbar chips, accented Spanish text, disabled text and small metadata.

## 3. Stage B — application text scale

Using the top 1–2 font candidates, test **70%, 100%, 130%, 160% and 200%**. Intermediate values can be used later to refine a preferred result.

At each scale inspect:

- General and class identity summaries;
- Habilidades fixed selector/passive references plus scrolled detail;
- Gestión fixed operational state plus scrolled sections;
- Equipo and Rasgos sticky toolbars;
- Conjuros source selector + fixed toolbar + sticky level header together;
- Notas general text editor and titled cards;
- one conditional collection (Técnicas/Metamagia/Pactos, Artífice, Formas or Compañeros);
- PC Settings and the global Settings dialog itself;
- Supercompact.

Pass criterion: no overlap or inaccessible required action; scrolling/wrapping at large scales is acceptable.

## 4. Stage C — whitespace compactness

Return text scale to the preferred/near-100 value. Compare spacing **100%, 80% and 60%**.

Check that 60% reduces whitespace without shrinking intrinsic icon/control targets. Pay special attention to adjacent destructive/duplicate/favorite controls and to dense Gestión/Equipo rows.

## 5. Stage D — card-column opt-in and rotation

Test defaults first, then the user-selectable maxima:

| Form factor | Default | User maximum |
| --- | ---: | ---: |
| Phone portrait | 1 | 4 |
| Phone landscape | 2 | 5 |
| Tablet portrait | 2 | 5 |
| Tablet landscape | 3 | 6 |

Surface-specific readability caps are intentional. In particular:

- Combate: max 4;
- Rasgos: max 4;
- Notas: max 4;
- Equipo especial: max 3;
- Equipo ordinario: phone max 3, wide max 5;
- Monedas, ability grids and Supercompact use dedicated layout rules rather than the generic card-column preference.

The main new observation for Pass 07 is **ordinary Equipo at 5 columns on a wide device**. Verify that name, quantity/weight, state/meta and quick actions remain understandable and tappable.

Rotate the device with a scrolled list and an active search/filter. Confirm the layout reflows without losing the selected tab or creating overlap.

## 6. Stage E — keyboard and editor window behavior

On phone and tablet, open representative editors with the software keyboard visible:

- long Notas general content;
- titled note editor;
- Equipo editor;
- Rasgo editor;
- Conjuro editor;
- one conditional-module editor;
- a Gestión editor/preview.

Pass criterion: the focused field can be reached by scrolling and the cancel/save/apply action remains reachable. On wide/tablet persistent side panels, verify the panel itself scrolls above the IME rather than requiring the keyboard to be dismissed.

## 7. Stage F — fixed/sticky footprint

This is the most important perceptual check after Pass 03–07.

### Habilidades
The view selector and passive Perception/Investigation/Insight references remain visible while detail scrolls. Confirm the fixed band earns its height.

### Gestión
`Estado operativo` remains visible while conditions/resources/effects scroll. Check both normal HP and the 0-HP/death-save state, where the band grows.

### Conjuros
The source selector, compact fixed spell toolbar and current sticky level header can all coexist. This is the highest-risk permanent footprint. Test phone portrait at 100% and at a large text scale before accepting it.

### Long collections
Equipo, Rasgos and conditional-module toolbars remain sticky. Confirm they do not obscure the first visible row or consume unreasonable height when filters are active.

## 8. Finding format

Record each concrete finding with:

- device + approximate screen size;
- portrait/landscape;
- font + text scale + spacing scale + column preferences;
- tab/surface;
- exact visible problem;
- severity: **blocking**, **major**, **minor**, or **preference only**;
- screenshot when the problem is visual.

Do not combine unrelated observations into one finding. A preference-only item does not block formal M6 unless the owner explicitly promotes it.

## 9. Exit rule

The audition is complete when phone and tablet have each covered Stages A–F sufficiently to select a comfortable baseline and there are no unresolved blocking visual/IME/layout findings.

After that, the owner may explicitly request that the current build (or a repair successor) be frozen as the replacement formal M6 candidate. Until that instruction, this remains a pre-QA repair/audition line.
""",
    encoding="utf-8",
)

print("Pass 07 checkpoint and owner audition guide written.")
