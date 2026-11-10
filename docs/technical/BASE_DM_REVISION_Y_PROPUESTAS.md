# «Base para DM» — revisión de la documentación, correcciones y propuestas

**Fecha:** 2026-11-10. **Hecha por:** el Worker, a pedido del propietario. Textual: «leas la documentación y me hagas preguntas que estimes pertinente para aclarar puntos y propongas correcciones y mejoras a las ideas antes de proceder y lo hablemos».

**Alcance:**
- A · Media / Handouts;
- B · Mesa del DM en vivo (Ola 8);
- D · Reglas SRD y consultas (Ola 10).

**Fuentes leídas:** D-0041, D-0068, D-0069, D-0070, D-0071 (§13 almacenamiento), D-0072 (§1, §12), D-0073, D-0075, `docs/PRODUCT.md` y `docs/ROADMAP.md`.

## 1. Lo que ya está aprobado (resumen)

- **A · Media / Handouts** (D-0072 §12, PRODUCT §13.7):
  - biblioteca liviana de imágenes, mapas y documentos;
  - se sube una vez y se usa en muchos lados;
  - renombrar, buscar, ver dónde se usa, reemplazar sin romper enlaces, exportar, borrar y recuperar;
  - cada archivo es «solo DM» o «apto para jugadores» («apto» no significa visible: mostrarlo es una acción explícita del DM);
  - el retrato del PJ es la primera necesidad de almacenamiento en el servidor (D-0071 §13).
- **Almacenamiento** (D-0071 §13, D-0075): hace falta un almacenamiento de archivos y su proveedor sigue pendiente. Cloudflare R2 es candidato, no elegido. Hay que hacer una revisión aparte de costo USD $0, porque un cupo gratis no basta si el exceso puede cobrarse. Nada de archivos dentro de PostgreSQL.
- **B · Mesa del DM** (D-0068, D-0069):
  - la App es un asistente y tiene un «presupuesto de atención del DM»: nada de formularios durante la partida;
  - una campaña tiene un espacio de trabajo con «mesas» planas;
  - las mesas son cuatro:
    - **Pantalla del DM:** hojas de PJ, Party Lens (vista derivada del grupo), referencia rápida y la entrada a la consulta de reglas;
    - **Mesa de escena:** lugares, tiendas, PNJ y la secuencia de escenas;
    - **Mesa de mazmorra:** fichas de zona y turnos de mazmorra beta;
    - **Mesa de combate:** iniciativa, participantes, estado, condiciones; varios combates a la vez; un solo aparato con autoridad.
- **D · Reglas** (D-0041, D-0070):
  - corpus oficial SRD 5.1 y SRD 5.2.1 en español, con versión y procedencia;
  - búsqueda de texto de PostgreSQL y luego un modelo de IA reemplazable (Cloudflare Workers AI);
  - respuestas en español, apoyadas en el texto recuperado;
  - disponible para jugadores y para el DM;
  - las reglas caseras quedan después del MVP.

## 2. Correcciones a la documentación

1. **Frases de bloqueo viejas.** D-0068 §20 y D-0069 §12 dicen «no empezar la implementación del DM; la Fase 4A sigue abierta». D-0071 y D-0073 ya autorizaron el MVP integrado, y el propietario eligió las Olas 7, 8 y 10. Hay que anotar en esos documentos que el bloqueo está superado, para que ningún agente lo lea como vigente.
2. **El contenido del DM no viaja al servidor.**
   - Hoy el servidor sincroniza campañas, miembros y PJ.
   - Criaturas, PNJ, lugares, zonas y encuentros viven solo en el escritorio del DM.
   - La documentación supone que el DM juega en la tablet con lo que preparó en el escritorio, y eso exige sincronizar todo ese contenido. Esa pieza no figura como paquete propio en el plan.
3. **Corpus SRD en español.** D-0041 da por hecho un SRD 5.1 y un SRD 5.2.1 oficiales en español. Hay que confirmar que existen, su licencia (CC BY 4.0) y su formato antes de construir. Si alguno no existe en español, cambia el diseño.
4. **Costo de Workers AI.** D-0075 lo mantiene «si sigue dentro del cupo gratis y falla sin cobrar al agotarse». Hay que comprobarlo con las condiciones vigentes antes de activarlo.

## 3. Propuestas de mejora

1. **Empezar la Mesa del DM en el escritorio.** El contenido ya está ahí, así que la «Base para DM» puede probarse sin construir antes la sincronización de todo el contenido. La tablet viene después, con esa sincronización como paquete propio.
2. **Partir la Mesa del DM en cortes.** Primero la Pantalla del DM y la Mesa de combate, que son lo más usado y lo más maduro en la documentación. Después la Mesa de escena y la de mazmorra, con su descubrimiento pendiente.
3. **Media primero local.**
   - La biblioteca funciona en el aparato del DM sin servidor y sin costo.
   - El servidor se suma cuando haga falta: mostrar un handout en el teléfono de un jugador, o el retrato del PJ entre aparatos. Para entonces ya estará hecha la revisión USD $0 del proveedor.
4. **Reglas con cita visible.** Cada respuesta muestra el fragmento del SRD en que se apoya, con su versión (5e o 5.5e). Así el DM puede comprobarla en la mesa.
5. **Búsqueda de reglas sin conexión.** El SRD también se guarda en el aparato y se puede buscar por texto sin IA ni red, como respaldo cuando no hay conexión o se agota el cupo de IA. Esto cumple el principio de funcionar primero en local.

## 4. Preguntas al propietario (de a una)

| # | Pregunta | Estado |
|---|---|---|
| P1 | ¿Dónde diriges la partida para la «Base para DM»: escritorio (notebook) o tablet Android? | **«Tablet desde ya»**. La propuesta 1 (escritorio primero) queda descartada. La «Base para DM» necesita antes un paquete nuevo: **sincronizar el contenido del DM** (criaturas, PNJ, lugares, zonas, encuentros, reglas caseras) entre el escritorio, el servidor y la tablet, y un **modo DM en la App Android** para tablet. |
| P2 | ¿Qué mesas entran en la «Base para DM»? | **«Las cuatro»**: Pantalla del DM, Mesa de combate, Mesa de escena y Mesa de mazmorra. La propuesta 2 (partir la Mesa del DM) queda descartada. Antes de programar escena, mazmorra y el detalle de combate hay que conversar sus puntos abiertos (D-0068 §18, D-0069 §13) con el propietario, de a uno. |
| P3 | ¿En este QA los jugadores deben recibir handouts en sus teléfonos, o basta con mostrarlos desde la pantalla del DM? | **«En la tablet del DM»**. El handout se muestra a pantalla completa en la tablet. No se envía a los teléfonos en este QA, ni se elige todavía el proveedor de almacenamiento. Pendiente aparte: cómo viajan los archivos del escritorio a la tablet, y el retrato del PJ (P5). |
| P4 | ¿Las respuestas de reglas muestran siempre el texto del SRD citado, y se agrega la búsqueda sin conexión? | Pendiente |
| P5 | ¿Cómo llegan a la tablet los archivos del DM (handouts) y el retrato del PJ? | Pendiente |

## 5. Orden de trabajo tras P1

1. **C0 · Sincronización del contenido del DM:**
   - endpoints y tablas en el servidor;
   - outbox y conflictos en Shared;
   - el escritorio sube su contenido y la tablet lo recibe.
   Usa las mismas reglas que los PJ: revisiones, borrados que no reviven, autoridad del DM de la campaña.
2. **A · Media / Handouts:** según P3.
3. **B · Mesa del DM en la tablet:** según P2.
4. **D · Reglas SRD y consultas:** según P4.
