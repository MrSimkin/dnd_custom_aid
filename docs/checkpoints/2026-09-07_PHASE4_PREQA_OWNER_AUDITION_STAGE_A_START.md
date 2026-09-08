# Phase 4 pre-QA owner visual audition — Stage A start

**Date:** 2026-09-07  
**Status:** OWNER VISUAL AUDITION STARTED — Stage A typography next  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review version/build:** `0.4.0-preqa.7` / `40700` / `debug`  
**Product identity:** unchanged from Pass 07

## Owner physical-device evidence

Primary phone test device is now durably recorded in `docs/TEST_DEVICES.md`:

- owner-reported model: **Redmi Note 11 Pro 5G**;
- role: primary owner phone test device;
- exact build `0.4.0-preqa.7` / `40700` installed successfully;
- application launched successfully;
- `Ajustes -> Acerca de` version/build/type identity confirmed by owner;
- owner has a representative populated character ready for the audition.

No Android-version/system-display assumptions are recorded yet.

## Precondition result

The phone-side preconditions from `docs/PREQA_OWNER_VISUAL_AUDITION.md` are sufficiently satisfied to begin Stage A.

This remains **pre-QA visual audition**, not formal M6. Formal M6 is still deferred until the owner explicitly approves freezing an exact replacement candidate.

## Exact next action — Stage A typography

On the Redmi Note 11 Pro 5G, begin in **phone portrait** with:

- application text: **100%**;
- spacing: **80%**;
- card columns: phone defaults (**1 portrait / 2 landscape**; remain in portrait for the initial comparison);
- representative populated character open.

Compare the first eight representative font candidates:

1. Manrope;
2. Source Sans 3;
3. Roboto Condensed;
4. IBM Plex Sans Condensed;
5. Mona Sans Condensed;
6. Geist;
7. Inter;
8. Sora.

Inspect several real surfaces rather than one sample sentence: General, a dense Habilidades/Combate area, Equipo or Rasgos, and Conjuros/Notas when populated. Judge long Spanish names, numbers, badges/chips, small metadata and overall reading comfort.

Return a shortlist of **2–3 finalists**, plus any font that should be rejected immediately and why. Do not test the remaining font catalog unless the first eight fail to produce a satisfactory shortlist.

## Resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint and `docs/PREQA_OWNER_VISUAL_AUDITION.md`. Continue Stage A typography on the recorded Redmi Note 11 Pro 5G. Do not begin new speculative implementation while Stage A evidence is pending.
