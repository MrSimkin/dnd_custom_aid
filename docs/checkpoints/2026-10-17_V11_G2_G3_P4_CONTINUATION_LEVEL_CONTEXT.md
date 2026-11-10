# V11 G2/G3 — Contexto de nivel en páginas físicas de continuación P4

**Fecha:** 2026-10-17 Chile. **Autoridad:** [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) `docs/pc-sheet-v11-approved-implementation-handoff`, DRAFT/abierta/sin fusionar. Commit funcional: [`a63bd6c8c239d0fd544311f240330ab1167fa303`](https://github.com/MrSimkin/dnd_custom_aid/commit/a63bd6c8c239d0fd544311f240330ab1167fa303). `main` sigue en `0c009f10d5e66880d68fa3732491c6fc3e172f22` (sin alteración).

## Defecto concreto del contrato P4

El paginador por medidas imprime `spell-level:<nivel>` una vez en el flujo de origen; si **un nivel continúa en una nueva página física**, dicha página antes podía comenzar directamente con conjuros y fuentes/preparación, sin identificación del nivel al que pertenecen. No se atribuye esto a una observación del propietario: es un incumplimiento de contexto legible exigido por la agrupación/continuación del contrato P4. Este bloque no reabre el diseño V11 ni modifica pruebas previas.

## Código implementado y alcance

- En ambos `DesktopPcSheetV11PdfProofRenderer.kt` y `AndroidPcSheetV11PdfProofRenderer.kt`, `addSection` inspecciona la **primera celda real** de cada página P4 posterior a la primera, identifica el encabezado `spell-level` precedente en el flujo persistido y, **solo si la página empieza dentro de ese mismo nivel**, dibuja `NIVEL N / CONTINUACIÓN` (o `TRUCOS / NIVEL 0 / CONTINUACIÓN` para 0).
- Se dibuja con la fuente Fira auténtica a 7 pt en la franja física previamente libre entre tarjetas mágicas (límite inferior y=617pt) y el flujo P4 (borde superior y=607pt), baseline y=610pt. No reserva nuevas líneas, modifica fuente, cambia páginas, duplica un encabezado que realmente empieza esa página, crea slots ni inventa progresión.
- No se cambiaron modelos, datos guardados, maquetas golden, portadas/Libro, otras familias de exportación, ni `main`.
- Prueba física Desktop real A/B: `g3DenseRealLevelOneSpellsKeepLevelContextOnP4ContinuationPagesAB`. Parte del backup auténtico Ilyra; **solamente para estrés técnico** sustituye la lista de conjuros por 175 registros de nivel 1 con UUID individuales, conservando asociaciones de origen reales. No afirma que ese personaje tenga 175 conjuros registrados.

## Evidencia verificable — mismo commit

- [Scaffold #39081392551](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39081392551) **SUCCESS** sobre `a63bd6c8c239d0fd544311f240330ab1167fa303`: Kotlin, tests Shared/Desktop nativos, compilación Android y controles backend/hosted database.
- [QA traceability #39081392536](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39081392536) **SUCCESS**, control documental, no aceptación del propietario.
- [Artefacto PDF real #12119331435](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39081392551/artifacts/12119331435): ZIP SHA-256 `ed79b5d0afa2e3ba2d46d0c40391a720cdafdec3bad3ffb1c2a6892a3a561223`, 36 PDF, CRC ZIP OK.
- Comparación independiente del ZIP anterior de [34 PDF #12118394108](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39080139256/artifacts/12118394108): **34/34 sin diferencias** en número/medidas Carta de **318 páginas**, palabras y coordenadas a 0,01pt y cantidad de dibujos vectoriales por página. No implica identidad byte a byte ni igualdad de cada operador vectorial.
- Dos PDF A/B nuevos de diez páginas Carta cada uno: `V11-INTERNAL-PROOF-P4-dense-single-level-A.pdf` SHA256 `d8e332780acb76ce28815f87f031d30fed99b3901845e873283de871ec8e0e5b`; B SHA256 `6c1f8a2d77a3c85c886ebbd01452eec52ad3938d6b1b8687631954d8b2f90eaf`.
- Cada A/B contiene **dos páginas físicas P4**, primera p4 y continuación p5. En p5 el texto `NIVEL 1 / CONTINUACIÓN` aparece sobre las celdas sin desplazar el flujo; `QAP4UNO001` a `QAP4UNO175`, **175/175 una sola vez**, y última hoja física `NOTAS` sin conjuros. Las zonas superiores P4 p5 en A y B fueron rasterizadas e inspeccionadas visualmente: sin colisión ni recorte aparente para el caso demostrado.

## Puertas conservadas y trabajo siguiente

**PASS LOCAL acotado de G2/G3 para continuación P4 entre páginas físicas**; no es PASS global de una regla, aprobación técnica/visual del propietario ni prueba instalada en Android. La matriz conserva **105 reglas globales NOT_VERIFIED y 12 stage checks PASS históricos**. `active_candidate=null`, **G4 BLOCKED**, G5/G6 pendientes. QA V11-QA-001/002 y 32 M50800 no alterados. No se inició RCR-1. PR #173 sigue DRAFT y sin merge.

**Siguiente bloque previsto:** concentrarse en una brecha distinta y demostrable de legibilidad/continuidad restante de P4 (en particular, salto entre las **tres columnas físicas** dentro de una misma página, que este slice no probó) o, si ya cumple, avanzar a otra obligación global todavía sin evidencia. No repetir las pruebas aquí cerradas sin motivo. Antes de G5 o QA del propietario, G4 integral debe confrontar 105 reglas, D-0076, los ocho golden, regresión legacy y candidato exacto; nunca declarar READY por un PASS local.
