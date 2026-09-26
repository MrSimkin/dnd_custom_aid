# Checkpoint — staged Android runtime smoke: Ilyra PASS / Mara active

**Date:** 2026-09-26 (Chile local time)  
**Normal trunk:** `main`  
**Current Android QA:** `0.5.0-preqa.7` / build `50700`  
**Status:** ALDREN SHARE PASS / ILYRA STEP 2 PASS / MARA ACTIVE

## Completed staged runtime evidence

### Step 1 — Aldren Share

**OWNER PASS.**

The shared PDF opened correctly on the second PC and no corruption/readability failure was reported.

### Step 2 — Ilyra Custom-v2 + unsaved-export persistence

**OWNER PASS.**

Final observed sequence on `0.5.0-preqa.7` / build `50700`:

1. owner made two unsaved edits to Ilyra:
   - name: `Ilyra Quill - QA TEST`;
   - Strength / FUE: `25`;
2. Custom v2 · Atributo export path was used;
3. `Exportar sin guardar` was presented and explicitly selected;
4. post-confirmation PDF rendering completed successfully;
5. the PDF opened/readable;
6. both unsaved edits appeared in the generated PDF;
7. owner then left the dirty editor without saving, returned to the character list and reopened Ilyra;
8. persisted character values were restored to the stored baseline:
   - name: `Ilyra Quill`;
   - FUE: `8`.

Therefore all Step-2 assertions are PASS:

- unsaved changes detected — PASS;
- `Exportar sin guardar` presented — PASS;
- owner selected `Exportar sin guardar` — PASS;
- post-confirmation rendering — PASS;
- PDF reflects current unsaved draft — PASS;
- unsaved draft is not persisted to character storage — PASS.

The earlier Ilyra Fantasy/DOTES runtime repair also remains OWNER PASS.

## Current exact action — Step 3: Mara

Use **Mara de los Siete Umbrales** from the hosted Player-path QA campaign.

Run the two Custom-v2 variants separately, one at a time:

### 3A — Custom v2 · Atributo

1. Open Mara.
2. Open **Hoja de personaje PDF**.
3. Select **Custom v2 · Atributo**.
4. Select **Permanente**.
5. Generate/Guardar PDF.
6. Open the PDF.
7. Require:
   - export completes without fail-closed routing/compression diagnostics;
   - PDF opens/readable;
   - Extended pages are present for Mara's high-volume content;
   - custom-stat content is represented;
   - overflow/continuation content is not silently lost or clipped.
8. Record PASS or the exact observed defect.

### 3B — Custom v2 · Habilidad

After 3A PASS:

1. repeat with **Custom v2 · Habilidad**;
2. use **Permanente**;
3. Generate/Guardar PDF;
4. open/read the PDF;
5. require the same Extended/custom-stat/overflow-continuation behavior;
6. record PASS or exact defect.

Do not combine the two results: record each variant independently.

## What to inspect in Mara

Mara is deliberately the high-volume Extended-page stress fixture. Runtime inspection should focus on:

- custom statistics;
- Traits/Features continuation;
- Resources/Options continuation;
- Inventory/Equipment continuation;
- long special-item detail;
- Notes/overflow continuation;
- no missing tail text, unreadable forced compression, or fail-closed renderer diagnostic.

Representative fixture semantics known to exist include:

- `Mara de los Siete Umbrales`;
- `Astrolabio de cobre con anillos concéntricos 1`;
- long object descriptions;
- `Protocolo de paradoja 1`;
- `Reserva 10: Sello`.

The owner does not need to perform forensic text extraction; this is a manual runtime/readability/routing smoke.

## Remaining staged runtime after Mara

After both Mara variants PASS:

4. **Current Snapshot:** request it once and verify the known user-visible fallback notice and Permanent fallback.

**Media / Handouts remains blocked** until Mara and Current Snapshot are recorded.

No new repository implementation is required before starting Mara unless Mara exposes a new runtime defect.
