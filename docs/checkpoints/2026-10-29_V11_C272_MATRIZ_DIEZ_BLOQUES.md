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

## C282 — Escuela de magia en el Libro (Q14 / Q14b, 2026-10-29)

- **Propietario:** «Sí, con escuela»; luego, «aun no hemos importado el SRD … estará en el SRD, debe usar A». Corrige la afirmación del Worker de que la escuela «no estaba en el SRD»: el SRD aún no se importa.
- **App:** campo nuevo `CharacterSpell.school` (texto libre opcional), columna `character_spell.school` (migración `26.sqm`), editor de conjuros de Android («Escuela (opcional)»), búsqueda, borrador del editor, copias de seguridad y sincronización. Las copias y la sincronización guardan la ficha completa en JSON y el servidor no interpreta los conjuros: **no hay paso del propietario en el proveedor**. El escritorio no tiene editor de conjuros.
- **PDF (ambos renderizadores):** la escuela real va bajo el nombre como el golden (mayúsculas, Barlow Condensed Bold 7 pt, #657d90, en la franja del encabezado). Sin escuela, no hay línea.
- **Pruebas:** `CharacterSpellSchoolTest` (persistencia, migración desde la versión 26, JSON antiguo sin escuela), `CharacterBackupTest` (la escuela viaja en la copia), `g3Bk06LibroCardPrintsTheRealSchoolUnderTheNameOnlyWhenTheAppHasOneAB` (A/B: posición, tamaño y fuente; sin escuela no se imprime) y el tripwire `test_v11_spellbook_essential_card_contract.py` (paridad Android/Escritorio).
- **Inspección visual:** `qa/evidence/c282_escuela/Libro_escuela_golden_vs_app.png`.
- **Pendiente técnico:** el importador del SRD, cuando exista, debe llenar `school`.

## Pendiente del propietario

- **Q14:** resuelta (ver arriba).
- **Fuentes del golden (Noto Sans, EB Garamond):** incorporarlas requiere agregar sus archivos de fuente a la app.
