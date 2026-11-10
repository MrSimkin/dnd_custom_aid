# Checkpoint 2026-11-10 — V11 integrada a `main` y ruta siguiente

**Fecha:** 2026-11-10. **Autor:** el Worker, por decisión del propietario.

## Decisiones del propietario (literales)

- 2026-11-10, tras entregar `preqa.14`: «vamos a hacer un pequeño alto, dado el criterio de bueno es lo enemigo de lo perfecto. Veamos que es lo que continua, y sigamos con los siguientes desarrollos de la App y vamos perfeccionando la exportación entremedio, porque porfin logramos suficiencia para continuar y solo hay que pulir detalles, te parece?»
- Preguntado si se integra primero la V11 a `main` antes de empezar lo nuevo: **«Sí, integrar primero»**.

- Tras la integración: «si, muestramelo, pero estamos aprobando la QA, excepto por las observaciones dadas y anotadas que vamos a ir arreglando para las siguientes etapas de desarrollo.» El QA de la hoja V11 queda aprobado en conjunto; las observaciones anotadas siguen abiertas y se arreglan entre medio (`owner_overall_qa_decision` en `docs/pc-sheet-v11/qa/REGISTRO_QA_V11.json`).

Estas decisiones reemplazan la regla anterior de la ruta V11 («no fusionar antes de G4 completo»). Antes, el 2026-11-08, el propietario ya había decidido pasar al QA sin más revisiones independientes.

## Estado de la hoja V11 al integrarse

- Exportación V11 A/B, la única en Android desde `preqa.14`. Las cuatro familias antiguas ya no se ofrecen en la pantalla de exportación.
- Último candidato: `0.5.0-preqa.14`, build 51400, commit `930812da`; ver `docs/pc-sheet-v11/qa/ACTA_CANDIDATO_PREQA14.md`.
- QA del propietario de `preqa.14`: pendiente. Guía en `docs/pc-sheet-v11/qa/GUIA_QA_PROPIETARIO_PREQA14.md`.
- Comentarios abiertos en `docs/pc-sheet-v11/qa/REGISTRO_QA_V11.json`: diez arreglos en IMPLEMENTED_UNVERIFIED; V11-QA-004, 005 y 006 en OPEN.
- G1 49/49, G2 90/90 y G3 126/126, atados a `930812da`. G4 0/9; el propietario decidió seguir sin él.
- El historial detallado de la V11 sigue en `docs/checkpoints/2026-10-29_V11_C283_FUENTES_GOLDEN.md` (C283–C473).

## Ruta siguiente

Cada paquete sale de `main` en una rama corta propia. La exportación V11 se pule entre medio, con los comentarios del QA del propietario: se registran literales, se arreglan en ramas cortas y van en el siguiente APK.

1. **Resolución de conflictos de sincronización de PJ (V11-QA-004):** ver diferencias local / servidor, poder traer la versión del servidor y su QA. Antes de programar, se presenta el diseño con imágenes y las preguntas de comportamiento de a una.
2. **Pantalla de login clara para jugadores y DM (V11-QA-006):** los detalles se acuerdan con el propietario.
3. **Pantalla DEV más clara (V11-QA-005).**
4. **Wave 7: Media / Handouts** (`docs/ROADMAP.md`), con revisión de costo USD $0 antes de elegir almacenamiento.

## Siguiente acción exacta

Presentar al propietario el diseño de la resolución de conflictos (paso 1), con imágenes y una pregunta por vez. No programar antes de su respuesta.
