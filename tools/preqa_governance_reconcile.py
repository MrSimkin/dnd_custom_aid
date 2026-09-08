from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, text: str) -> None:
    (ROOT / path).write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one anchor, found {count}")
    return text.replace(old, new, 1)


def replace_section(text: str, start: str, end: str, replacement: str, label: str) -> str:
    start_i = text.find(start)
    if start_i < 0:
        raise RuntimeError(f"{label}: start anchor not found")
    end_i = text.find(end, start_i)
    if end_i < 0:
        raise RuntimeError(f"{label}: end anchor not found")
    return text[:start_i] + replacement.rstrip() + "\n\n" + text[end_i:]


# README — current entry point and active review state.
path = "README.md"
text = read(path)
text = replace_once(
    text,
    "4. `docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md` — current practical resume checkpoint while owner QA is paused.",
    "4. `docs/checkpoints/LATEST.md` — stable current resume pointer; it names the exact active checkpoint/build and next action.",
    "README resume pointer",
)
text = replace_once(
    text,
    "11. `docs/TESTING.md` — verification rules, current frozen QA candidate, and owner QA matrix.",
    "11. `docs/TESTING.md` — verification rules, current pre-QA review identity, and the formal owner-QA matrix that will apply after a replacement candidate is frozen.",
    "README testing description",
)
text = replace_once(
    text,
    "The approved closure scope is D-0047. Automated implementation-completeness and code-health work is complete. The next stage is owner real-device QA.",
    "The approved closure scope is D-0047. After the historical M5 freeze, the owner explicitly reopened pre-QA implementation and the focused UX repair line completed Pass 03–07. Automated repair gates are green through review build `0.4.0-preqa.7` / `40700`. The next stage is staged owner phone/tablet visual audition; formal M6 remains deferred until the owner explicitly freezes a replacement candidate.",
    "README implemented-foundation status",
)
text = replace_section(
    text,
    "## Current Phase 4 position — M6 owner QA pending",
    "## Build and verification commands",
    """## Current Phase 4 position — pre-QA repair line stable; owner audition next

Active owner-requested repair branch:

`implementation/phase4-preqa-ux-repair`

Current technically verified review identity:

- version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- checkpoint/branch head `4c6da4577b57e472819e096ecff55bd6750e026d`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK size `36,161,616` bytes;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Current resume pointer:

`docs/checkpoints/LATEST.md`

Detailed current checkpoint:

`docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`

Owner audition guide:

`docs/PREQA_OWNER_VISUAL_AUDITION.md`

The September 4 M5 frozen candidate remains immutable historical evidence, but it is **not the active owner-QA target** after the owner explicitly reopened implementation. Build `40700` is a pre-QA visual-audition build, not yet a frozen formal M6 candidate.

Further speculative UX implementation is stopped. The owner should now perform the staged phone/tablet audition; concrete blocking findings may produce a focused successor build. Formal M6 resumes only after the owner explicitly says an exact replacement build is ready to freeze.

**DM feature implementation remains blocked until Phase 4 owner QA is accepted, final governance/merge-boundary housekeeping is complete, and the owner explicitly approves closure/merge.**""",
    "README current Phase4 section",
)
write(path, text)


# MANIFEST — authoritative current pointers.
path = "MANIFEST.md"
text = read(path)
text = replace_once(
    text,
    "### `docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md`\nCurrent practical resume checkpoint while owner QA is paused. It records the exact frozen M6 candidate, APK identity, mandatory first migration test, QA matrix, branch invariants and exact resume sequence.",
    "### `docs/checkpoints/LATEST.md`\nStable current resume pointer. It names the active checkpoint, review identity and exact next action without requiring historical reconstruction.\n\n### `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`\nCurrent technical repair checkpoint. Pass 07 marks the focused fixed/sticky, spacing-scale, IME/window and card-column audit line technically stable at build `40700`; formal M6 remains deferred.\n\n### `docs/PREQA_OWNER_VISUAL_AUDITION.md`\nStaged owner phone/tablet visual-audition guide for typography, text scale, spacing, columns/rotation, IME behavior and fixed/sticky footprint before any replacement formal M6 candidate is frozen.",
    "MANIFEST checkpoint entry",
)
text = replace_once(
    text,
    "Chronological significant-decision log. Detailed later Phase 4 decisions under `docs/decisions/` remain authoritative where the consolidated master log has not yet been reconciled. Full reconciliation through D-0044–D-0047 is intentionally deferred to post-QA governance housekeeping.",
    "Chronological significant-decision log, reconciled through D-0047. Detailed records under `docs/decisions/` remain the authoritative source for full rationale and approved nuance.",
    "MANIFEST decisions description",
)
text = replace_once(
    text,
    "Development phases and current Phase 4 closure boundary. Phase 4A implementation is complete; M6 owner real-device QA is the next gate.",
    "Development phases and current Phase 4 closure boundary. The focused pre-QA repair line is technically stable through Pass 07; staged owner visual audition precedes freezing a replacement formal M6 candidate.",
    "MANIFEST roadmap description",
)
text = replace_once(
    text,
    "Verification policy, commands, exact frozen M6 QA candidate and current phone/tablet owner-QA matrix.",
    "Verification policy, commands, current pre-QA review identity, owner visual-audition boundary and the formal phone/tablet M6 matrix to use after a replacement candidate is explicitly frozen.",
    "MANIFEST testing description",
)
text = replace_once(
    text,
    "Durable implementation, QA and handoff checkpoints. The current resume entry point is `2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md`. Historical batch checkpoints remain evidence and must not override the current resume point.",
    "Durable implementation, QA and handoff checkpoints. The current resume entry point is always `LATEST.md`; historical batch/M6-pause checkpoints remain evidence and must not override the current pointer.",
    "MANIFEST checkpoints description",
)
text = replace_once(
    text,
    "The Phase 4 character editor includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact/Table mode and all six approved conditional class/subclass module families, with phone/tablet adaptive behavior. Phase 4A implementation is now frozen for M6 owner QA.",
    "The Phase 4 character editor includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact/Table mode and all six approved conditional class/subclass module families, with phone/tablet adaptive behavior. The focused pre-QA repair line is technically stable through Pass 07; it is awaiting owner visual audition rather than frozen formal M6 QA.",
    "MANIFEST android status",
)
text = replace_section(
    text,
    "## Active frozen QA candidate",
    "## Character-sheet assets",
    """## Current pre-QA review identity

The current owner-audition build is:

- branch `implementation/phase4-preqa-ux-repair`;
- review version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- checkpoint head `4c6da4577b57e472819e096ecff55bd6750e026d`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

This is **not yet a frozen formal M6 candidate**. Use `docs/PREQA_OWNER_VISUAL_AUDITION.md` first. The historical M5 and Batch L frozen branches remain immutable evidence and are not the active audition target.""",
    "MANIFEST active candidate section",
)
text = replace_once(
    text,
    "6. `docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md` is the practical resume checkpoint while QA is paused;",
    "6. `docs/checkpoints/LATEST.md` is the stable practical resume pointer;",
    "MANIFEST authority resume",
)
write(path, text)


# ROADMAP — distinguish audition from formal M6.
path = "docs/ROADMAP.md"
text = read(path)
text = replace_once(
    text,
    "**Status:** Implementation complete; **M6 owner real-device QA pending**.",
    "**Status:** Closure implementation + focused pre-QA repair line technically stable; **owner visual audition pending; formal M6 deferred**.",
    "ROADMAP Phase4A status",
)
text = replace_once(
    text,
    "Durable pre-QA branch:\n\n`implementation/phase4-preqa-consolidation`",
    "Active owner-requested pre-QA repair branch:\n\n`implementation/phase4-preqa-ux-repair`",
    "ROADMAP active branch",
)
text = replace_section(
    text,
    "Pre-QA audits and consolidation are complete through M5. The exact frozen M6 candidate is:",
    "### Phase 4A exit criterion",
    """Historical M1–M5 audits/consolidation are complete. After reviewing that candidate, the owner explicitly reopened pre-QA implementation. Focused UX repair Pass 03–07 is now technically green and stable.

Current owner-audition identity:

- version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

This review build is **not a frozen formal M6 candidate**. The next gate is the staged phone/tablet visual audition in `docs/PREQA_OWNER_VISUAL_AUDITION.md`. Only after the owner explicitly declares a build ready will one exact replacement candidate be frozen and formal M6 resume.

Current practical resume pointer:

`docs/checkpoints/LATEST.md`""",
    "ROADMAP preQA block",
)
text = replace_once(
    text,
    "- one exact closure APK candidate is recorded — **done**;\n- owner QA passes on phone portrait/landscape and tablet portrait/landscape, including a representative larger text scale — **pending**;\n- blocking findings are resolved — pending only if QA finds any;\n- continuity/governance housekeeping is complete — final post-QA pass pending;",
    "- focused automated pre-QA repair audits are green through Pass 07 — **done**;\n- current-stage governance prose is reconciled through the Pass 07 state — **done**;\n- owner staged visual audition on phone/tablet identifies an acceptable baseline with no unresolved blocking visual/IME/layout findings — **pending**;\n- one exact replacement formal M6 candidate is explicitly frozen after owner readiness — **pending**;\n- formal owner QA passes on phone portrait/landscape and tablet portrait/landscape, including representative larger text — **pending**;\n- blocking findings are resolved — pending only if audition/QA finds any;",
    "ROADMAP exit criteria",
)
write(path, text)


# TESTING — current review build vs future formal M6 candidate.
path = "docs/TESTING.md"
text = read(path)
text = replace_section(
    text,
    "## Current status",
    "## 1. Core rule",
    """## Current status

Phases 0–3 are complete. Phase 4 Character Foundation Closure implementation is complete, historical M1–M5 audits/consolidation are complete, and the owner-reopened pre-QA UX repair line is technically green through Pass 07.

Current position:

- M1 scope traceability — COMPLETE;
- M2 code-health/static architecture audit — COMPLETE;
- M3 historical implementation-completeness audit — COMPLETE;
- M4 six approved inter-batch scope holes — COMPLETE;
- M5 bounded cleanup/full regression/historical replacement freeze — GREEN / historical frozen evidence;
- pre-QA UX repair Pass 03–07 — GREEN; focused technical repair line stable;
- owner staged phone/tablet visual audition — **NEXT**;
- M6 owner formal real-device QA — **DEFERRED / NOT ACTIVE until an exact replacement candidate is explicitly frozen**.

Current owner-audition identity is build `40700`; it is **not** a formal frozen M6 candidate. The historical M5 and Batch L frozen branches remain immutable evidence only.

Green CI is technical evidence, not owner acceptance.""",
    "TESTING current status",
)
text = replace_section(
    text,
    "## 3. Exact active M6 candidate",
    "## 4. Automated evidence already complete",
    """## 3. Current pre-QA owner-audition identity

Use the following build for the staged visual audition described in `docs/PREQA_OWNER_VISUAL_AUDITION.md`:

- branch `implementation/phase4-preqa-ux-repair`;
- review version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- helper workflow `34171466714` — SUCCESS;
- checkpoint/branch head `4c6da4577b57e472819e096ecff55bd6750e026d`;
- artifact ID `10035895186`, name `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK size `36,161,616` bytes;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

This build is a **pre-QA visual-audition build**, not a frozen formal M6 target. Do not label it accepted merely because the automated gate is green.

### Historical M5 frozen candidate

The September 4 M5 candidate remains immutable historical evidence:

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- exact commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- exact tree `fd1f7feffde082b34cce41248e951a25eed7a004`;
- validator artifact `9951922423` / `phase4-m5-frozen-qa-apk`;
- APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

It ceased to be the active QA target when the owner explicitly reopened implementation on 2026-09-07. Do not mutate it and do not resume QA against it unless the owner explicitly requests historical comparison.""",
    "TESTING current candidate section",
)
text = replace_once(
    text,
    "M5 automated evidence includes:",
    "Historical M5 automated evidence includes:",
    "TESTING M5 evidence label",
)
text = replace_once(
    text,
    "No schema or persistence migration was added by M4/M5. The current closure schema remains the tested schema 9 line.",
    "No schema or persistence migration was added by M4/M5 or by pre-QA UX repair Pass 03–07. The current closure schema remains the tested schema 9 line. Pass 07 additionally completed the full shared/Kotlin, Android, Desktop and backend gate before producing build `40700`.",
    "TESTING schema evidence",
)
text = replace_once(
    text,
    "## 5. Critical first QA rule — migration before clean install",
    "## 5. Critical first formal-M6 rule — migration before clean install",
    "TESTING migration heading",
)
text = replace_once(
    text,
    "**Do not clear app data before the first owner QA test.**\n\nThe first test must exercise the real owner upgrade path:",
    "**When a replacement formal M6 candidate is frozen, do not clear app data before its first owner QA test.**\n\nThe first formal-M6 test must exercise the real owner upgrade path:",
    "TESTING migration rule",
)
text = replace_once(
    text,
    "2. install the exact M6 candidate over it;",
    "2. install the exact frozen replacement M6 candidate over it;",
    "TESTING migration candidate wording",
)
text = replace_section(
    text,
    "## 11. Current exact continuation",
    "No DM-feature implementation begins before successful Phase 4 owner QA, final governance/merge-boundary housekeeping and explicit owner closure/merge approval.",
    """## 11. Current exact continuation

Current resume pointer:

`docs/checkpoints/LATEST.md`

Current detailed checkpoint:

`docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`

Current owner action guide:

`docs/PREQA_OWNER_VISUAL_AUDITION.md`

Next sequence:

1. use exact build `0.4.0-preqa.7` / `40700` for staged phone/tablet visual audition;
2. record concrete visual/IME/layout findings using the guide;
3. if a blocking finding exists, repair it on the durable pre-QA line and run the complete automated gate with a new identified build;
4. when the owner explicitly says a build is ready, freeze that exact build as the replacement formal M6 candidate;
5. only then begin formal M6, starting with the in-place upgrade/data-preservation test before any clean install;
6. preserve historical M5/L branches as immutable evidence.

Further speculative UX polishing is not the next action.""",
    "TESTING continuation",
)
write(path, text)


# ARCHITECTURE — current stage without rewriting historical architecture.
path = "docs/ARCHITECTURE.md"
text = read(path)
text = replace_once(
    text,
    "**Architecture state:** foundational choices approved; current work is additive character/adaptive implementation, not a stack redesign.  \n**Active branch:** `implementation/phase4-character-closure`",
    "**Architecture state:** foundational choices approved; Phase 4 character/adaptive implementation and the focused pre-QA UX repair line are technically stable; no stack redesign is active.  \n**Active branch:** `implementation/phase4-preqa-ux-repair`",
    "ARCHITECTURE status",
)
text = replace_once(
    text,
    "Batches A1 through H3 are implemented. The current execution position is Batch I1 adaptive-shell completion; do not reopen schema/domain architecture merely because the closure has moved into holistic responsive work.",
    "Batches A1–L, historical M1–M5 and the owner-reopened pre-QA repair Pass 03–07 are implemented/verified at their recorded levels. The current execution position is owner visual audition plus governance/acceptance preparation; do not reopen schema/domain architecture for perceptual UI questions or speculative cleanup.",
    "ARCHITECTURE current execution",
)
text = replace_once(
    text,
    "- Batch I1 may introduce an adaptive navigation shell/rail using ordinary Compose state/layout primitives; this does not require a new navigation framework or architecture layer.",
    "- The implemented adaptive navigation shell/rail uses ordinary Compose state/layout primitives and did not require a new navigation framework or architecture layer.",
    "ARCHITECTURE Android adaptive wording",
)
text = replace_once(
    text,
    "No further schema work is currently implied by Batch I1/I2. H1/H2/H3 confirmed that Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros can use the existing reusable durable domains without one hard-coded persistence subsystem per subclass.",
    "The current tested closure line is schema 9. No schema migration was added by pre-QA repair Pass 03–07. H1/H2/H3 confirmed that Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros can use the existing reusable durable domains without one hard-coded persistence subsystem per subclass.",
    "ARCHITECTURE schema wording",
)
text = replace_once(
    text,
    "For Batch I1:\n\n- available-width shell decisions are UI behavior, not character-domain data;\n- per-character last-open-tab state may be persisted as local UI preference/navigation state rather than added to the character rules/domain schema;",
    "The implemented boundary remains:\n\n- available-width shell decisions are UI behavior, not character-domain data;\n- per-character last-open-tab state is persisted as local UI preference/navigation state rather than added to the character rules/domain schema;",
    "ARCHITECTURE UI boundary wording",
)
text = replace_section(
    text,
    "## Current implementation consequence",
    "## Architecture gate consequence",
    """## Current implementation consequence

The architecture consequence of the current pre-QA state is deliberately small:

- keep the existing shared Kotlin + SQLDelight + Compose character foundation intact unless a concrete owner-observed defect demonstrates a need to change it;
- treat build `0.4.0-preqa.7` / `40700` as the current visual-audition identity, not as permission for further speculative architecture or UX framework work;
- use the staged phone/tablet audition to resolve remaining perceptual questions about typography, density, fixed/sticky footprint, high-column layouts and keyboard ergonomics;
- any concrete blocking finding receives a focused repair plus the existing complete automated gate and a new identified build;
- freeze a replacement formal M6 candidate only after explicit owner readiness;
- continue using Desktop compilation and backend type-check as regression checks even when the active change is Android presentation;
- do not introduce a new service, synchronization layer, realtime mechanism, navigation framework or architecture framework for this closure work.

DM-feature implementation remains blocked until Phase 4 formal owner QA, governance/merge-boundary completion and explicit owner closure/merge approval.""",
    "ARCHITECTURE current consequence",
)
write(path, text)


# AGENTS — current project stage/resume authority.
path = "AGENTS.md"
text = read(path)
text = replace_section(
    text,
    "## 13. Current project stage",
    "__END__",
    """## 13. Current project stage

**Phases 0–3 are complete. Phase 4 — MVP Buildout / Character Foundation Closure is current.**

The active owner-requested pre-QA line is:

`implementation/phase4-preqa-ux-repair`

D-0047 remains the controlling approved closure scope. Historical implementation batches A1–L and M1–M5 remain durable evidence. After reviewing the historical M5 candidate, the owner explicitly reopened implementation; focused UX repair Pass 03–07 is now technically green and stable at review version `0.4.0-preqa.7` / build `40700`.

Current execution entry point:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`;
3. `docs/PREQA_OWNER_VISUAL_AUDITION.md`.

The next action is **owner phone/tablet visual audition**, not speculative UX implementation and not historical M6. Build `40700` is an identified pre-QA audition build, not yet a frozen formal M6 candidate.

If the owner observes a concrete blocking/regression finding, repair it on the durable pre-QA line, run the complete automated gate and identify the successor build. When the owner explicitly says an exact build is ready, freeze that replacement candidate and resume formal M6 beginning with the in-place upgrade/data-preservation test.

Historical frozen branches, including the September 4 M5 candidate, remain immutable evidence and must not be repurposed as the active target after implementation was reopened.

**Do not begin DM-feature implementation until the Phase 4 character closure is fully implemented, automatically verified, accepted through final owner phone + tablet QA, governance/merge-boundary housekeeping is complete, and the owner explicitly approves merge/closure.**""",
    "AGENTS current stage",
)
# replace_section needs an actual end anchor; section 13 is EOF.
if text.endswith("\n\n__END__\n"):
    text = text[:-len("\n\n__END__\n")] + "\n"
elif text.endswith("\n\n__END__"):
    text = text[:-len("\n\n__END__")] + "\n"
else:
    raise RuntimeError("AGENTS EOF sentinel handling failed")
write(path, text)


# QA_CHECKLIST — retire obsolete V4 build-specific suite, keep reusable core and route current audition/formal M6 correctly.
path = "docs/QA_CHECKLIST.md"
text = read(path)
text = replace_section(
    text,
    "## 3. Current feature suite — Phase 4 character data foundation V4",
    "## 4. Result recording",
    """## 3. Current Phase 4 pre-QA manual entry point

The old V4 build-specific checklist has been retired from the active suite. Its assumptions about text-scale steps, font candidates and presentation controls were superseded by the later D-0047 closure and Pass 03–07 repair line.

Current pre-QA owner review uses:

- exact review identity `0.4.0-preqa.7` / build `40700`;
- `docs/PREQA_OWNER_VISUAL_AUDITION.md` for staged typography, text-scale, spacing, columns/rotation, keyboard and fixed/sticky-footprint review;
- the persistent regression core above for install/launch/campaign/durable-data sanity.

Before recording the visual audition as complete, also smoke-check:

1. a representative existing campaign and character open normally;
2. General, Habilidades, Combate, Gestión, Equipo, Trasfondo, Rasgos, Conjuros and Notas remain reachable as applicable;
3. representative conditional modules still appear/hide without deleting stored data;
4. save/reopen and full app restart preserve representative edits;
5. phone and tablet portrait/landscape remain operable;
6. representative editors remain usable with the software keyboard visible;
7. sticky/fixed controls in Habilidades, Gestión, Equipo, Rasgos, Conjuros and conditional collections do not obscure required content/actions;
8. ordinary Equipo remains readable at the new wide maximum of 5 columns;
9. spacing 60% does not visibly shrink intrinsic control/touch targets;
10. a large text-scale sample remains scrollable and actionable.

This is still **pre-QA visual audition**, not formal M6 acceptance.

When the owner explicitly freezes a replacement formal M6 candidate, use the full acceptance matrix in `docs/TESTING.md`, beginning with the mandatory in-place upgrade/data-preservation test before any clean install. Do not reuse the historical M5 candidate as active merely because it remains frozen evidence.""",
    "QA_CHECKLIST active suite",
)
write(path, text)


# DECISIONS — reconcile the consolidated master through D-0047 without renumbering history.
path = "docs/DECISIONS.md"
text = read(path)
if "## D-0044 —" in text or "## D-0047 —" in text:
    raise RuntimeError("DECISIONS already contains D-0044/D-0047; refusing duplicate append")
append = r'''

---

## D-0044 — Phase 4 begins with character data foundation, then combat tracker

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed record:** `docs/decisions/D-0044_PHASE4_CHARACTER_FOUNDATION_ORDER.md`

Phase 4 establishes a stable, usable Android/local character data foundation before DM combat work consumes character records. Persistent character-sheet state and future live combat working state remain separate. The application stays permissive: durable character values may represent homebrew/house-rule exceptions and the character foundation is not a legality engine.

---

## D-0045 — Character-sheet presentation preferences

**Status:** Approved where explicitly stated in the detailed record; recorded pending items remain pending  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed record:** `docs/decisions/D-0045_CHARACTER_SHEET_PRESENTATION.md`

Character-sheet presentation is digital/adaptive rather than a literal copy of the paper references. The sheet supports the approved alternative skills/ability organizations, compact progressive presentation, and application-level presentation settings that are not character mechanics. The detailed record contains the successive owner QA clarifications; later approved closure decisions/current checkpoints control where early QA candidate details were superseded.

---

## D-0046 — Character derived values and explicit adjustments

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed record:** `docs/decisions/D-0046_CHARACTER_DERIVED_VALUES_AND_ADJUSTMENTS.md`

Ordinary deterministic character arithmetic is calculated from known source values while optional explicit adjustments preserve gifts, homebrew, house rules and other exceptions. This covers ability modifiers, skills, saving throws, Passive Perception, Initiative and proficiency bonus under the detailed formulas. The principle is calculation assistance, not rules enforcement or character-build legality checking.

---

## D-0047 — Phase 4 character closure expansion

**Status:** Approved  
**Date:** 2026-09-03  
**Decision owner:** Project owner  
**Detailed record:** `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md`

Before DM-focused implementation begins, Phase 4 receives one substantial character-foundation closure package combining retained owner QA corrections, new owner requirements, F01–F18, D01–D18, I01–I22, verified official class/subclass identity, reusable conditional class modules and first-class phone/tablet portrait/landscape behavior.

The closure keeps the product permissive rather than turning it into a guided/legal character builder. It establishes Gestión, PC Settings responsibilities, structured live-maintenance/reference domains, backup/import, responsive/adaptive behavior, Supercompact/Table mode, reusable conditional modules, consistent collection/editor UX and the explicit owner acceptance boundary recorded in the detailed decision.
'''
text = text.rstrip() + append + "\n"
write(path, text)


# PROJECT_STATE — current truth while preserving historical batch evidence.
path = "docs/PROJECT_STATE.md"
text = read(path)
text = replace_once(
    text,
    "**Current phase:** Phase 4 Character Foundation Closure — owner QA pending",
    "**Current phase:** Phase 4 Character Foundation Closure — owner visual audition pending; formal M6 deferred",
    "PROJECT_STATE phase",
)
text = replace_once(
    text,
    "**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the M5/M6 candidate. The focused implementation repair line is technically stable through Pass 07 on `implementation/phase4-preqa-ux-repair`, review identity version `0.4.0-preqa.7` / build `40700`; owner visual/device audition and governance reconciliation remain before any replacement formal M6 candidate is frozen.",
    "**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the historical M5/M6 candidate. The focused implementation repair line is technically stable through Pass 07 on `implementation/phase4-preqa-ux-repair`, review identity version `0.4.0-preqa.7` / build `40700`. Current-stage governance prose is reconciled through D-0047/Pass 07. Next action is owner phone/tablet visual audition; formal M6 remains deferred until the owner explicitly freezes an exact replacement candidate.",
    "PROJECT_STATE execution position",
)
text = replace_once(
    text,
    "Current-truth documentation was refreshed. Known remaining governance item: consolidated `docs/DECISIONS.md` still ends at D-0043 while detailed D-0044–D-0047 files exist. Reconcile before merge proposal without renumbering historical decisions.",
    "Current-truth documentation is refreshed through the owner-reopened Pass 07 line. The consolidated `docs/DECISIONS.md` is reconciled through D-0047 without renumbering historical decisions. The remaining later governance boundary is the post-acceptance unique-commit/merge audit and final merge proposal.",
    "PROJECT_STATE Batch0 governance",
)
text = replace_once(
    text,
    "## 3. Expanded Batch M — pre-QA complete; owner QA next",
    "## 3. Expanded Batch M — historical pre-repair audit line; owner later reopened implementation",
    "PROJECT_STATE section3 heading",
)
text = replace_once(
    text,
    "Exact active M6 candidate identity:",
    "Historical M5 frozen candidate identity (superseded as the active target when the owner reopened implementation on 2026-09-07):",
    "PROJECT_STATE M5 candidate label",
)
text = replace_section(
    text,
    "### M6 — owner real-device QA — NEXT",
    "## 4. Remaining approved execution sequence",
    """### M6 — owner real-device QA — DEFERRED / NOT ACTIVE

The September 4 M5 freeze remains immutable historical evidence, but the owner explicitly reopened implementation before starting formal M6. Do not resume formal QA against that historical target unless the owner explicitly requests comparison.

### Owner-reopened pre-QA UX repair Pass 03–07 — GREEN / TECHNICALLY STABLE

The reopened repair line addressed owner-observed presentation/ergonomic concerns before formal QA. Across Pass 03–07 it completed the planned fixed/sticky controls, application text/font/spacing options, spacing-scale propagation, IME/window audit, conditional long-collection controls, card-column readability audit and final residual spacing sweep.

Current review identity:

- branch `implementation/phase4-preqa-ux-repair`;
- version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK size `36,161,616` bytes;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`;
- checkpoint/branch head `4c6da4577b57e472819e096ecff55bd6750e026d`.

Pass 07 established a technical stopping rule: further speculative visual code changes are not recommended before owner phone/tablet observation. Build `40700` is therefore the current **visual-audition build**, not a frozen formal M6 candidate.

Use `docs/PREQA_OWNER_VISUAL_AUDITION.md`. Any concrete blocking finding receives a focused repair/new identified build. Formal M6 resumes only after the owner explicitly declares an exact build ready to freeze.""",
    "PROJECT_STATE M6/reopened repair section",
)
text = replace_section(
    text,
    "## 4. Remaining approved execution sequence",
    "## 5. Existing baseline that must not regress",
    """## 4. Remaining approved execution sequence

From the current position:

- historical M1–M5 — **COMPLETE/GREEN** at their recorded levels; historical frozen branches remain immutable evidence;
- pre-QA UX repair Pass 03–07 — **GREEN / TECHNICALLY STABLE**;
- current-state governance reconciliation through D-0047/Pass 07 — **COMPLETE**;
- **NEXT:** owner staged phone/tablet visual audition using build `40700` and `docs/PREQA_OWNER_VISUAL_AUDITION.md`;
- blocking audition finding requiring production change -> focused repair -> complete automated gate -> new identified review build -> repeat affected audition evidence;
- when owner explicitly says the replacement build is ready -> freeze one exact formal M6 candidate with commit/tree/workflow/artifact/hash identity;
- formal M6 begins on that frozen candidate with the in-place upgrade/data-preservation test before any clean install;
- after owner QA acceptance -> complete the unique-commit/merge-boundary audit and prepare the Phase 4 merge proposal;
- merge to `main` only after explicit owner closure/merge approval.

No DM feature implementation begins before that explicit Phase 4 exit decision.""",
    "PROJECT_STATE remaining sequence",
)
text = replace_section(
    text,
    "## 6. Final acceptance boundary",
    "## 7. Exact continuation",
    """## 6. Final acceptance boundary

Historical Batch L and M5 frozen branches remain evidence of their respective pre-repair trees. Neither is the active QA target after the owner reopened implementation. Build `40700` is the current owner-audition identity and is not yet formal M6.

Phase 4 remains open until:

1. staged owner phone/tablet visual audition is complete with no unresolved blocking visual/IME/layout findings;
2. one exact replacement formal M6 candidate is explicitly frozen after owner readiness;
3. M6 owner phone+tablet QA is completed and accepted on that exact replacement candidate;
4. blocking findings are resolved and affected evidence repeated when necessary;
5. the unique-commit/merge-boundary audit and final continuity housekeeping are complete;
6. the owner explicitly approves merge/closure.

Implementation-completeness audits are not substitutes for QA, and automated green is not owner acceptance.""",
    "PROJECT_STATE acceptance boundary",
)
text = replace_section(
    text,
    "## 7. Exact continuation",
    "__END__",
    """## 7. Exact continuation

**Next action: owner staged phone/tablet visual audition on build `40700`.**

Resume in this order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`;
3. `docs/PREQA_OWNER_VISUAL_AUDITION.md`.

Current audition identity:

- branch `implementation/phase4-preqa-ux-repair`;
- version `0.4.0-preqa.7` / build `40700`;
- product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Do not begin formal M6 yet. Do not clear app data for a hypothetical M6 migration test until an exact replacement candidate is actually frozen. Preserve the historical M5/L branches unchanged. Keep `main` untouched. Do not begin DM work before successful Phase 4 exit and explicit owner approval.""",
    "PROJECT_STATE exact continuation",
)
if text.endswith("\n\n__END__\n"):
    text = text[:-len("\n\n__END__\n")] + "\n"
elif text.endswith("\n\n__END__"):
    text = text[:-len("\n\n__END__")] + "\n"
else:
    raise RuntimeError("PROJECT_STATE EOF sentinel handling failed")
write(path, text)


# Governance checkpoint + latest pointer. Product identity remains Pass 07 / 40700.
checkpoint = ROOT / "docs/checkpoints/2026-09-07_PHASE4_PREQA_GOVERNANCE_RECONCILIATION.md"
checkpoint.write_text(
    """# Phase 4 pre-QA governance reconciliation after Pass 07

**Date:** 2026-09-07  
**Status:** CURRENT-STAGE GOVERNANCE RECONCILED; no product code/version change  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current tested review product:** `43ca1f5662123ce4d355d9d618b0bfba66d17697`  
**Current review version/build:** `0.4.0-preqa.7` / `40700`  
**Current review artifact:** `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`  
**Current APK SHA-256:** `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`

## Reconciled

- README, MANIFEST, ROADMAP, TESTING, ARCHITECTURE and AGENTS now describe the owner-reopened Pass 07 state instead of presenting the September 4 M5 candidate/handoff as the active next action;
- `docs/PROJECT_STATE.md` preserves historical M1–M5 evidence but marks formal M6 deferred and adds the active Pass 03–07 repair/audition line;
- `docs/DECISIONS.md` is reconciled through D-0047 without renumbering or replacing the detailed approved records;
- `docs/QA_CHECKLIST.md` retires the obsolete V4 build-specific active suite and routes current visual audition to `docs/PREQA_OWNER_VISUAL_AUDITION.md`, while formal M6 remains governed by `docs/TESTING.md` after a future explicit freeze;
- historical M5 and Batch L frozen branches/candidates remain immutable evidence only;
- no Android/Kotlin/backend/domain/schema/version change was made by this reconciliation.

## Current boundary

The technical repair stopping rule from Pass 07 remains in force. The next action is owner staged visual audition on exact build `40700`. Further UX changes should answer concrete owner findings, not speculative static polishing.

Build `40700` is not a formal M6 candidate. Formal M6 begins only after the owner explicitly says an exact replacement build is ready and that identity is frozen. The first formal-M6 test remains the in-place upgrade/data-preservation test before any clean install.

## Remaining governance after owner acceptance

- unique-commit/merge-boundary audit;
- final merge proposal/continuity check;
- explicit owner Phase 4 closure/merge approval.

DM implementation remains blocked until that exit boundary.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint, then `docs/PREQA_OWNER_VISUAL_AUDITION.md`. Perform owner phone/tablet visual audition using build `40700`. Do not resume historical M6 or speculative UX work unless the owner explicitly directs it or reports a concrete finding.
""",
    encoding="utf-8",
)

latest = ROOT / "docs/checkpoints/LATEST.md"
latest.write_text(
    """# Latest project checkpoint

**Updated:** 2026-09-07  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Current review identity:** version `0.4.0-preqa.7` / build `40700`  
**Current technical state:** focused pre-QA UX repair Pass 07 green/stable  
**Current governance state:** current-stage prose reconciled through D-0047/Pass 07  
**Detailed checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_GOVERNANCE_RECONCILIATION.md`  
**Technical Pass 07 checkpoint:** `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`  
**Owner audition guide:** `docs/PREQA_OWNER_VISUAL_AUDITION.md`

The owner explicitly reopened implementation after the historical September 4 M5 freeze. Build `40700` is the current technically verified owner-audition build, not a frozen formal M6 candidate. The planned speculative technical repair audits are complete and current-stage governance prose is reconciled.

**Next action:** owner staged phone/tablet visual audition. Record concrete findings. Repair only demonstrated blockers/regressions. Freeze a replacement formal M6 candidate only after explicit owner readiness; then begin formal M6 with the in-place upgrade/data-preservation test before any clean install.

Historical M5/L frozen branches remain immutable evidence. `main` remains untouched. DM work remains blocked until Phase 4 QA/closure/merge is accepted and explicitly approved.
""",
    encoding="utf-8",
)

print("Post-Pass7 governance reconciliation applied successfully.")
