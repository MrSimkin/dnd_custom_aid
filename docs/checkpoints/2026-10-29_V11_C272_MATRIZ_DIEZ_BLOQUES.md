# C272–C281 — Diez bloques: evidencia de la matriz (2026-10-29)

PR #173 DRAFT/NO MERGE. Anterior: [C255–C271](2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md).

**Instrucción del propietario:** «10 bloques más por favor». La parte más atrasada era la matriz (39/242). Esta tanda registra evidencia demostrada y agrega pruebas allí donde faltaba demostración. Ninguna regla se marcó sin una prueba que la compruebe; las que la prueba no cubre quedan abiertas, con el motivo anotado.

| Bloque | Commit | Etapa | Reglas | Base |
|---|---|---|---|---|
| B39 / C272 | `34aa5122` | G3 | RSG01, RSG02, RSG03, RSG04, P302 | 48 rasgos con descripción y notas centinela: solo nombre, tipo y fuente; cada ID una sola vez; nada en P3. |
| B40 / C273 | `434e89e1` | G3 | BK01, CN301, IN05, P203, P206, P210 | Libro solo si se pide y hay conjuros; sin C-N3 cuando no hay notas; silueta sin retrato; reservas manuscritas en P2. |
| B41 / C274 | `aa4c936b` | G3 | P101, P110, P303, C201, C203 | Módulos de P1; 8 tarjetas de combate; campos de P3; sin C2 si el contenido cabe; módulos que comparten C2 sin notas. |
| B42 / C275 | `58e5318e` | prueba nueva | — | `g3PhysicalModuleOrderAndLetterSizeHoldOnEveryPageAB`: todas las páginas en carta y el orden aprobado de los módulos. |
| B43 / C276 | `fde047d0` | G3 | GLB01, BK02, IN03 | Con la prueba nueva: carta en toda página; Libro después de C-N3 y antes de la hoja NOTAS final; la exportación no modifica la ficha. |
| B44 / C277 | `feda8983` | G2 | GLB01, GLB06, GLB07, P304, P402 | Pruebas de composición compartidas: bordes de página, sin solapes, líneas en blanco, narrativa sin pérdida, tres columnas. |
| B45 / C278 | `14c755bd` | G1 | IN02, IN07, P108, P116, P209, P204, CN301, BK01 | Pruebas semánticas compartidas. |
| B46 / C279 | `8da92dc0` | pruebas nuevas | — | Municiones con 0, 1, 5, 20, 21 y 35; logo y nombre en cada página sin repetir el nombre en P1. |
| B47 / C280 | este bloque | G3 | P117, GLB03 | Con las pruebas nuevas. |
| B48 / C281 | este commit | docs | — | Checkpoint, ruta y pregunta pendiente. |

## Resultado

- **Matriz:** de 39 a **73 de 242** comprobaciones por etapa en PASS. Toda la evidencia cita la CI exacta: Scaffold `40924595257`, `40926825057` o `40928367211`, con sus artefactos de PDF y auditoría.
- **Reglas globales en PASS:** 0 de 106. G4 sigue bloqueado: una regla pasa solo cuando todas sus etapas pasan y hay QA del propietario.
- **Abiertas con motivo:**
  - RSG05: el orden de los rasgos no está comprobado.
  - IN04: no se mide la geometría Fit/Crop del retrato.
  - P204: no se prueban las monedas personalizadas.
  - C202: no se demuestra que se evite una columna gigante.
  - GLB06 en G3: no hay un control de recorte de tinta.
- **Verificación:** las pruebas nuevas pasan en local y en la CI exacta; `check_v11_pre_qa_gate.py` y `check_v11_qa_registry.py` pasan.

## Pendiente del propietario

- **Q14 (2026-10-29):** ¿las fichas del Libro deben mostrar la escuela de magia, como el golden, aunque la ficha esencial aprobada (BK06) no la incluya? Ver [`DECISIONES_PENDIENTES_PROPIETARIO.md`](../pc-sheet-v11/DECISIONES_PENDIENTES_PROPIETARIO.md).
- **Fuentes del golden (Noto Sans, EB Garamond):** incorporarlas requiere agregar sus archivos de fuente a la app.
