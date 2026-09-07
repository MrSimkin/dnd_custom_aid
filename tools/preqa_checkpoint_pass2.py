from __future__ import annotations

from pathlib import Path
import os
import re

ROOT = Path(__file__).resolve().parents[1]
VERSION = os.environ.get("REVIEW_VERSION", "0.4.0-preqa.2")
BUILD = os.environ.get("REVIEW_BUILD", "40200")
APK = os.environ.get("REVIEW_APK", "DND_Custom_Aid_0.4.0-preqa.2_Build_40200_debug.apk")
PRODUCT_SHA = os.environ.get("PRODUCT_SHA", "PENDING")
PRODUCT_TREE = os.environ.get("PRODUCT_TREE", "PENDING")
RUN_ID = os.environ.get("GITHUB_RUN_ID", "PENDING")


def reconcile_project_state() -> None:
    path = ROOT / "docs/PROJECT_STATE.md"
    text = path.read_text(encoding="utf-8")
    text = re.sub(
        r"^\*\*Last verified:\*\*.*$",
        "**Last verified:** 2026-09-07 owner local time / 2026-09-07 UTC",
        text,
        count=1,
        flags=re.M,
    )
    text = re.sub(
        r"^\*\*Current execution position:\*\*.*$",
        "**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the M5/M6 candidate. Active work is iterative UX repair on `implementation/phase4-preqa-ux-repair`; formal M6 owner QA remains deferred until a new replacement review build is accepted for QA.",
        text,
        count=1,
        flags=re.M,
    )
    if "**Active owner-requested repair branch:**" not in text:
        text = text.replace(
            "**Durable pre-QA branch:** `implementation/phase4-preqa-consolidation`\n",
            "**Durable pre-QA branch:** `implementation/phase4-preqa-consolidation`  \n**Active owner-requested repair branch:** `implementation/phase4-preqa-ux-repair`\n",
            1,
        )
    section = """## 0. Primary resume order

1. `docs/checkpoints/LATEST.md` — stable owner-required resume pointer; always read this first;
2. the concrete checkpoint referenced by `LATEST.md` — active repair-pass state, exact version/build and next action;
3. `docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md` — historical pause state only; its instruction to begin M6 was superseded when the owner explicitly reopened implementation on 2026-09-07;
4. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M5_PREQA_CONSOLIDATION.md` — historical pre-repair closure evidence;
5. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` and `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — controlling approved scope and historical execution decomposition.

"""
    text, count = re.subn(r"## 0\. Primary resume order\n.*?(?=## 1\.)", section, text, count=1, flags=re.S)
    if count != 1:
        raise RuntimeError("Could not reconcile PROJECT_STATE primary resume order")
    path.write_text(text, encoding="utf-8")


def write_checkpoint() -> None:
    checkpoints = ROOT / "docs/checkpoints"
    checkpoints.mkdir(parents=True, exist_ok=True)
    detail = checkpoints / "2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_02.md"
    detail.write_text(
        f"""# Phase 4 pre-QA UX repair — Pass 02

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; owner visual/device review still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `{PRODUCT_SHA}`  
**Tested product tree:** `{PRODUCT_TREE}`  
**Helper workflow run:** `{RUN_ID}`  
**Review version:** `{VERSION}`  
**Review build:** `{BUILD}`  
**Review APK filename:** `{APK}`

## Why implementation resumed

The historical 2026-09-04 M6 pause checkpoint said owner QA was next. On 2026-09-07 the owner explicitly reviewed the candidate, found additional UX issues and reopened pre-QA implementation. Formal M6 QA is therefore deferred until these repair passes produce a new replacement review candidate.

## Pass 02 completed

- explicit Android version/build identity: version `{VERSION}`, build `{BUILD}`;
- APK and GitHub artifact naming include both version and build;
- Application Settings exposes an explicit **Acerca de** dialog showing VERSION, BUILD and BUILD TYPE prominently;
- text-size audition expanded to 70–200%; 145%+ shows a phone-specific warning rather than blocking the choice;
- ten new font candidates added to the existing audition set: Inter, Figtree, Public Sans, Barlow Semi Condensed, Space Grotesk, Recursive, Cabin Condensed, Encode Sans Condensed, PT Sans Narrow and League Spartan;
- typography candidates keep their design/source provenance visible; selection is not restricted to one distributor/ecosystem;
- a separate **Compactación adicional de espacios** preference (100/90/80/70/60%) was added; it is explicitly distinct from Supercompact and begins by scaling app-owned padding/gaps in shared collection/dialog primitives plus the General shell, without shrinking icon/touch-target geometry;
- user-controlled column maxima expanded to phone portrait 1–4, phone landscape 1–5, tablet portrait 1–5 and tablet landscape 1–6; individual surfaces may still cap unsafe layouts;
- character-name input forces the first visible character uppercase while preserving the rest of the entered name; shared tests cover normal, accented and temporary blank input;
- the activity already used `adjustResize`; Settings and campaign-name dialogs explicitly add IME/navigation-bar insets, while the reusable character editor dialog retains IME-safe scroll behavior;
- `AGENTS.md` requires `docs/checkpoints/LATEST.md` to be refreshed after every implementation/review pass;
- `docs/PROJECT_STATE.md` points to the active repair branch/LATEST checkpoint instead of incorrectly resuming historical M6.

## Still open — next repair pass

1. remove embedded d20 controls from cramped sheet rows and create the dedicated **Tirada de dados** tab, with explicit roll context/name;
2. audit every tab and pin/sticky the highest-value reference/actions while the remainder scrolls;
3. continue propagating the extra spacing-scale preference through every tab-specific margin/padding/gap not yet covered by the shared primitives;
4. audit all remaining dialog/window surfaces for IME-safe resizing + internal scroll, not only the reusable editor/settings/campaign surfaces;
5. exercise the expanded column maxima surface-by-surface and tune safe caps where cards become unreadable;
6. owner visual audition of the enlarged font set, including phone/tablet and compactness combinations;
7. continue iterative pre-QA repair; do **not** resume formal M6 until owner says the replacement build is ready.

## Verification

The helper gate passed `git diff --check`, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build` and backend TypeScript check before the tested product commit was created.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Continue with the dedicated dice-roll tab + per-tab sticky-important-elements audit. Do not return to the historical M6 APK unless the owner explicitly asks for historical comparison.
""",
        encoding="utf-8",
    )
    (checkpoints / "LATEST.md").write_text(
        f"""# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `{VERSION}` / build `{BUILD}`  
**Current pass:** Phase 4 pre-QA UX repair Pass 02  
**Detailed checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_02.md`

The owner explicitly reopened pre-QA implementation after the historical M6 pause. Formal M6 QA is deferred. Resume by reading the detailed checkpoint above and continue with the dedicated **Tirada de dados** tab and the per-tab sticky-important-elements audit.
""",
        encoding="utf-8",
    )


reconcile_project_state()
write_checkpoint()
print("Project state + pass 2 checkpoint written.")
