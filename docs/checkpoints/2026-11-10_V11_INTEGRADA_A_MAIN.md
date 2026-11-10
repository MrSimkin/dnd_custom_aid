# Checkpoint 2026-11-10 — V11 integrada a `main` y ruta siguiente

**Fecha:** 2026-11-10. **Autor:** el Worker, por decisión del propietario.

## Decisiones del propietario (literales)

- 2026-11-10, tras entregar `preqa.14`: «vamos a hacer un pequeño alto, dado el criterio de bueno es lo enemigo de lo perfecto. Veamos que es lo que continua, y sigamos con los siguientes desarrollos de la App y vamos perfeccionando la exportación entremedio, porque porfin logramos suficiencia para continuar y solo hay que pulir detalles, te parece?»
- Preguntado si se integra primero la V11 a `main` antes de empezar lo nuevo: **«Sí, integrar primero»**.

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

## Decisión del propietario sobre las etapas siguientes (2026-11-10)

Literal: «A, B y D en ese orden y eso irá en el siguiente QA, que es la Base para DM. Esto fue visto con otra IA, asique te pediré que leas la documentación y me hagas preguntas que estimes pertinente para aclarar puntos y propongas correcciones y mejoras a las ideas antes de proceder y lo hablemos».

- **Orden:**
  - A · Media / Handouts (cierre de la Ola 7);
  - B · Mesa del DM en vivo (Ola 8);
  - D · Reglas SRD y consultas (Ola 10).
- **QA:** las tres van juntas en el siguiente QA del propietario, la **«Base para DM»**.
- **Antes de programar:** leer la documentación aprobada (D-0041, D-0068 a D-0073, D-0075 y `docs/PRODUCT.md`), hacer al propietario las preguntas pertinentes y proponer correcciones y mejoras. Se conversan una por una y se registran antes de implementar.
- **Diseño de conflictos de sincronización:** queda cerrado (D1–D4) en `docs/technical/PC_SYNC_CONFLICT_RESOLUTION_DESIGN.md`, en la rama `feature/pc-sync-conflict-resolution`, y se implementa en el carril de correcciones.

## Siguiente acción exacta

El diseño de la «Base para DM» quedó cerrado con 24 respuestas del propietario (P1–P24) en `docs/technical/BASE_DM_REVISION_Y_PROPUESTAS.md`.

**Siguiente paso:** construir el paquete 1, sincronizar el contenido del DM (App de PC → servidor → tablet), en una rama corta desde `main`. Después siguen los paquetes 2 a 8 de la sección 7 de ese documento, y al final un solo QA del propietario.
