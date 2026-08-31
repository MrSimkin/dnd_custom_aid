# D-0059 — Rasgos domain

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

The next-build character-sheet expansion adds a dedicated `Rasgos` tab. The supplied custom paper sheets establish a broad `Rasgos y Atributos` / `Otros Rasgos y Atributos` concept, but do not define a digital data model. The owner approved a structured digital representation rather than one large free-text field.

## 1. Structured trait/feature cards

`Rasgos` is a persistent ordered collection of individual entries.

Each entry must support at minimum:

- `Nombre`;
- `Fuente`;
- `Tipo`;
- `Descripción`;
- optional `Notas`;
- optional usage tracker;
- optional activation/action type;
- explicit manual order.

The compact phone presentation should use cards rather than one giant text box. A collapsed card prioritizes:

- `Nombre`;
- `Fuente · Tipo` when present;
- one/two-line description preview.

Tap/expand opens the complete detail/editor. Wider layouts may use multiple columns when width genuinely supports them.

## 2. Fuente

`Fuente` is free text rather than a closed enum.

Examples include class, species/race, background, feat, blessing/gift, homebrew or another campaign-specific source.

The app must not infer legality or reject unusual combinations.

## 3. Tipo

Use an organized but permissive category selector with at least:

- `Rasgo de clase`;
- `Rasgo de especie / raza`;
- `Rasgo de trasfondo`;
- `Dote`;
- `Don / bendición`;
- `Otro`.

`Objeto` is not a default Rasgos category because magic/special-item descriptions already belong to `Equipo especial`; `Otro` remains available for exceptions.

## 4. Optional manual usage tracker

A trait may optionally expose limited-use state:

- `Usos máximos`;
- `Usos gastados` or an equivalent tappable-pip representation;
- optional `Recuperación` free text.

Examples of recovery text may include `Descanso corto`, `Descanso largo`, `Al amanecer`, `Manual`, or arbitrary homebrew wording.

Usage tracking is manual:

- do not infer maximum uses from class/level/ability;
- do not automatically restore uses on rests;
- do not enforce recovery rules;
- persisted spent/remaining state must survive Save/reopen/restart.

## 5. Optional activation/action type

A trait may optionally record its activation form:

- `Pasivo`;
- `Acción`;
- `Acción adicional`;
- `Reacción`;
- `Otro`.

This is organizational/reference metadata only. It does not replace `Combate`: frequently used attacks/actions may still have a condensed combat entry while the authoritative long trait description remains in `Rasgos`.

## 6. Ordering

Rasgos preserve explicit user-defined order.

Use the same direct drag-and-drop direction approved for other ordered phone lists rather than arrow-character buttons. Persist the resulting order exactly; do not alphabetize automatically.

## Product boundary

`Rasgos` is a flexible character reference and tracker, not a rules engine or character builder. It must support official, homebrew, multiclass and campaign-specific features without inferring legality.
