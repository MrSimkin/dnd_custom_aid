# Checkpoint — Mara Custom-v2 source-label micro-fit repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `4f484cbd30a2e67265dba710c1f2865b035e0082`  
**Active branch:** `fix/pc-sheet-mara-source-microfit`  
**Status:** OWNER RUNTIME DEFECT REPRODUCED / BOUNDED SOURCE-LABEL MICRO-FIT REPAIR ACTIVE

## Completed staged runtime before Mara

- Aldren Share — OWNER PASS.
- Ilyra Fantasy/DOTES repair — OWNER PASS.
- Ilyra staged Step 2 — OWNER PASS:
  - `Exportar sin guardar` reached and selected;
  - post-confirmation PDF rendering completed;
  - PDF contained unsaved `Ilyra Quill - QA TEST` and FUE 25;
  - after leaving without saving and reopening Ilyra, persisted baseline `Ilyra Quill` / FUE 8 returned.

## Mara 3A owner runtime defect

Owner began the intended Mara stress test on **`0.5.0-preqa.7` / build `50700`** with:

- PC: **Mara de los Siete Umbrales**;
- visual family: **Custom v2 · Atributo**;
- exported state: **Permanente**;
- custom-stat presentation: **Extended**;
- Spellbook: OFF.

On PDF export, rendering failed before a usable PDF was produced.

Exact diagnostic:

`Source-matched text does not fit: Manipulación de éter (49.48437 > 49.0)`

Mara 3A is therefore **BLOCKED / NOT PASS**. Mara 3B and Current Snapshot must not start until 3A is repaired and rerun.

## Root cause

`Manipulación de éter` is a real custom skill in the integrated Mara fixture.

In the Custom-v2 Attribute Extended page, linked-skill labels use the approved source-matched Corbel compact typography:

- nominal horizontal scale: **78%**;
- physical rule width after padding: **49 pt**.

At the approved nominal scale, this label measures about **49.484 pt**, exceeding the available rule by only ~0.484 pt (~0.98% of field width).

The renderer currently treats every source-font fixed-scale miss as fatal, even when a visually negligible sub-2-point scale adjustment would preserve the approved source typography and readability.

This is materially different from earlier excessive-compression defects (for example the Ilyra Potent Cantrip ~49.97% case). Mara does **not** require aggressive compression or page redesign.

## Generalized repair rule

Do **not** special-case Mara or `Manipulación de éter`.

Preserve:

- the approved/frozen Custom-v2 Extended geometry;
- the source Corbel font;
- nominal source-matched scales;
- the fail-closed fit guard.

Allow only a bounded source-label micro-fit:

1. render at the nominal source scale whenever it fits;
2. if it misses, calculate the exact scale needed to fit the existing rule;
3. permit at most **2 percentage points** below the nominal source scale;
4. if more than that is required, fail closed as before.

For Mara, 78% nominal becomes approximately 77.24%, safely inside the bounded tolerance.

## Implementation

The active branch now:

- applies the bounded micro-fit inside `textAboveRuleFixedScale`;
- keeps nominal scale unchanged for already-fitting labels;
- adds `SOURCE_MATCHED_MICRO_FIT_DELTA = 2f`;
- preserves the fail-closed guard beyond that tolerance;
- applies the same logic to Desktop and Android Custom-v2 Extended renderers;
- adds a real Mara regression for **both** Custom-v2 per Attribute and per Ability;
- requires preservation of:
  - `Mara de los Siete Umbrales`;
  - `Manipulación de éter`;
  - `Astrolabio de cobre con anillos concéntricos 1`;
  - `Protocolo de paradoja 1`;
  - `Reserva 10: Sello`.

## Branch validation discovery — Scaffold #3915

Scaffold **#3915** / run `36272581858` reached the full Kotlin/rendering suite.

The original owner blocker **`Manipulación de éter` did not recur** in the real-Mara regression, confirming that the bounded source-label micro-fit advanced rendering past that field.

The regression then exposed a second latent Mara blocker:

`Text does not fit: Rasgo extenso 01 — Umbral: recursos y anota el resultado. La segunda frase existe para forzar salto de línea`

Root cause:

- featured trait descriptions are first wrapped to fit their feature block;
- overflow lines beyond the first three are routed to the Traits continuation area;
- the old `featureOverflowLines` implementation prepended `trait.name + ": "` to the first already-wrapped overflow line **without re-wrapping after adding that prefix**;
- the resulting continuation line can therefore exceed the actual continuation-rule width even though the original overflow line was valid.

Generalized correction now active:

1. preserve the same feature-block preview and overflow semantics;
2. join only the already-routed overflow content;
3. prepend the trait name once;
4. **re-wrap the complete prefixed continuation text** at the actual 281-pt continuation width and 7.7 pt continuation font;
5. preserve all semantic content; do not truncate or add compression;
6. apply identically to Desktop and Android.

The real-Mara two-family regression remains the acceptance test. A fresh Scaffold on the corrected head is required.

## Branch validation discovery — Scaffold #3918

Scaffold **#3918** advanced beyond both earlier Mara blockers:

- the original source-matched `Manipulación de éter` failure did not recur;
- the prefixed featured-trait continuation line no longer failed after re-wrapping.

The real-Mara regression then exposed a third narrow fit edge in ordinary inventory continuation:

`Compact v2 label requires excessive compression: 2 x Frasco de tinta que recuerda la última palabra escrita 2 · 0.5 lb (77.72368%)`

This inventory helper intentionally targets a **78%** compact horizontal-scale floor. Mara needs only **77.72%**, a ~0.28 percentage-point miss.

Generalized correction now active:

1. keep **78%** as the target used during font-size fitting;
2. if the label still narrowly misses at minimum font size, allow at most the same **2 percentage-point micro-fit tolerance**;
3. preserve the fail-closed guard below **76%**;
4. do not special-case the item/string;
5. apply the same logic to Desktop and Android.

The real-Mara regression remains the acceptance gate. A fresh Scaffold is required.

## Owner boundary

No owner action is required on the current preqa.7 APK.

Do not retry Mara 3A until a distinguishable repaired Android QA candidate is repository-green, PR-integrated, and merged-main green.

After that, rerun **Mara 3A only**:

- Custom v2 · Atributo;
- Permanente;
- Extended;
- Guardar PDF;
- open/read PDF;
- verify no source-fit diagnostic and inspect Extended/custom-stat/overflow continuation behavior.

Only after Mara 3A PASS proceed to Mara 3B Custom v2 · Habilidad.

Current Snapshot and Media / Handouts remain pending.
