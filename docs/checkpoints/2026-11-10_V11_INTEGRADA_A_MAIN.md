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

**Paquete 1 (sincronizar el contenido del DM): implementado** en la rama `feature/dm-content-sync`. El detalle está en `docs/technical/DM_CONTENT_SYNC.md`, e incluye:

- migración Neon `0002_dm_content.sql` y su prueba SQL `0008`;
- rutas del Worker;
- SQLDelight 28 y el servicio Shared;
- botón en la App de PC;
- paso en la sincronización Android.

Pendiente del paquete 1, como paso del propietario en los proveedores (AGENTS §5). En este computador Wrangler no tiene sesión iniciada (2026-11-10):

1. Neon (consola web → proyecto DEV → SQL Editor): pegar y ejecutar completo `database/migrations/0002_dm_content.sql`. Resultado esperado: `COMMIT` sin errores. Evidencia que devolver: la lista de tablas muestra `dm_content`. No compartir la cadena de conexión.
2. Cloudflare, desde `D:\DnD_Aid\repo\backend`:
   - `npm install`
   - `npx wrangler login` (inicia sesión en el navegador)
   - `npx wrangler deploy`

   Resultado esperado: se publica el Worker existente `dnd-custom-aid-api` con la URL de siempre. No crear un Worker nuevo. Los secretos se conservan. No compartir tokens.

   Evidencia que devolver: la línea «Current Version ID» y que `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/health` responde `ok`.

Si el paso 1 falla, detenerse y no publicar el paso 2. El orden importa: primero la tabla y después el Worker. Costo: USD $0 (ningún plan nuevo).

**Paquete 2 (modo DM en la App Android): implementado** en la rama `feature/dm-mode-android`, construida sobre el paquete 1. El detalle está en `docs/technical/DM_MODE_ANDROID.md`.

- Incluye la entrada para jugadores y DM, con la cuenta opcional (P25 «A · Opcional», cubre V11-QA-006), «Tus campañas» con la marca DM o Jugador y el Escritorio DM con sus cuatro mesas y el contenido sincronizado.
- V11-QA-006 sigue OPEN hasta el QA del propietario.

**Paquete 3 (Pantalla del DM): implementado** en la rama `feature/dm-screen`, construida sobre el paquete 2. El detalle está en `docs/technical/DM_SCREEN.md`.

- Incluye Hojas de PJ, la Vista Grupo (P9), la Referencia rápida con 10 fichas en 5e y 5.5e (P10) y Consultar reglas con búsqueda sin conexión (P4).
- Los dos SRD oficiales en español van completos dentro de la App (P26).

**Paquete 4 (Archivos e imágenes): implementado** en la rama `feature/dm-media`, construida sobre el paquete 3. El detalle está en `docs/technical/DM_MEDIA.md`.

- El servicio de archivos es Workers KV (P27, tras la revisión USD $0).
- Los handouts viajan como contenido del DM, y su imagen va por suma de control.
- En la App de PC se importan desde «Medios / Handouts», y la tablet los muestra a pantalla completa.

El paso del propietario en los proveedores ahora suma la creación del espacio KV (`DM_MEDIA.md` §5). Va todo junto antes del QA:

1. Neon: aplicar la migración 0002.
2. Cloudflare: `wrangler login`, crear el espacio KV y `wrangler deploy`.

**Paquete 5 (Mesa de Combate): implementado** en la rama `feature/dm-combat`, construida sobre el paquete 4. El detalle está en `docs/technical/DM_COMBAT.md`.

- Incluye Preparar encuentro con reserva, ajustes y paquetes, iniciativa escrita o tirada, PV de monstruos con promedio o tirados, la nota de PV de los PJ, estados, turnos y rondas.
- Los paquetes se arman en la App de PC (Gestores → Paquetes).

**Revisión del diseño de uso (2026-11-11).** El propietario detuvo el trabajo al ver en el emulador que las pantallas DM no seguían el diseño de D-0068/D-0069.

- Se revisaron y **aprobaron una a una** cinco propuestas (P28 a P37): Escritorio con barra lateral, Pantalla del DM, Mesa de Combate en 4 pasos con editor completo, Mesa de Escena y Mesa de Dungeon.
- El diseño aprobado está en `docs/technical/DM_UX_APROBADA.md` y manda sobre las pantallas actuales.

**Corrección de las pantallas hecha (2026-11-11, rama `feature/dm-ux-redesign`).** Las tres pantallas ya construidas siguen ahora `DM_UX_APROBADA.md`:

1. **Escritorio:** barra lateral con vista general, mesas fijas, combates abiertos, «＋ Nueva mesa», herramientas como panel al costado y estado de sincronización; pestañas arriba como opción en ⚙.
2. **Pantalla del DM:** hoja de grupo (Fichas | Vista Grupo), hoja rápida y hoja completa en lectura al costado, «Pregunta una regla…» y Referencia rápida con «Consultadas hace poco».
3. **Mesa de Combate en 4 pasos:**
   - Preparar, con bloques − y +, «aplicar a: los N | uno» y lo propio en naranja;
   - Iniciativa, una vez, con «Una para el grupo» o «Cada uno la suya»;
   - En curso, con orden, criatura tocada y «Ahora actúa»;
   - el editor completo, con ataques estructurados y «Guardar como criatura nueva».

Probado en el emulador de tablet (solo lectura).

**Tablet y Windows con las mismas mesas (2026-11-11).** El propietario pidió que todo lo del DM esté también en la App de Windows.

- Las mesas están en `dmUi/src/kotlin` y las compilan las dos Apps, cada una con su Compose.
- En Windows se abren desde «Escritorio DM (mesa en vivo)».
- Detalle en `docs/technical/DM_ESCENA.md`.

**Paquete 6, Mesa de Escena (2026-11-11), en la tablet y en Windows:**

- búsqueda por tipo y por idea;
- 📌 Fijados y 🕘 Recientes;
- recorrer por zona, por función, por aventura y en el mapa;
- ficha con partes para jugadores o solo DM, la tienda con precios y ⭐, y «PNJ aquí»;
- lo relacionado a la derecha.

En la App de PC, los vínculos se eligen de una lista (P36) y las marcas se ponen sobre el mapa (P35). Se probó con pruebas automáticas y con imágenes de la App de Windows.

**Paquete 7, Mesa de Dungeon (2026-11-11), en la tablet y en Windows:**

- mapa de zonas con pasajes (los secretos, punteados), con su estado, y el grupo y las criaturas puestos a mano;
- turno de 10 minutos con una actividad por toque (las 15 de la regla beta más dos con *), la anterior de referencia y el avance solo como dato;
- cerrar turno, separar grupo e interrumpido por combate, con registro automático;
- fichas de zona en pestañas, con secciones y marcas de un toque, y «Preparar ▸ ⚔»;
- relojes que solo avisan;
- notas que se pegan al contexto y viajan a la App de PC;
- una emboscada exitosa propone la ronda sorpresa en la Mesa de Combate.

En la App de PC: mazmorra, diagrama, pasajes y encuentro de cada zona, y lista de actividades en las reglas caseras. Detalle en `docs/technical/DM_DUNGEON.md`.

**Paquete 8, Consultar reglas con IA (2026-11-11), en la tablet, en Windows y en el Worker.**

- La búsqueda sigue siendo local y sin conexión.
- «Responder con IA» envía la pregunta y los 6 primeros fragmentos del SRD a `POST /v1/rules/answer`.
- Workers AI responde en español y cita [1], [2]…
- Sin IA o sin cupo, la App sigue mostrando el SRD.
- El costo está revisado: USD $0 en el plan Workers Free.
- Detalle en `docs/technical/DM_REGLAS_IA.md`.

Los 8 paquetes de la «Base para DM» están construidos en la tablet y en Windows.

**Paso de proveedores hecho (2026-11-11).** El propietario autorizó las sesiones y el Worker hizo el resto:

- en Neon DEV, la migración 0002 quedó aplicada (`dm_content` y sus 5 funciones);
- en Cloudflare, se creó el espacio KV `dnd-custom-aid-media` y se publicó `dnd-custom-aid-api` con `MEDIA` y `AI` (versión `a63a59c5-8b27-451c-8bf8-b1185b6aa1f3`);
- `/health` responde ok y las rutas nuevas piden sesión.

La evidencia está en `DM_REGLAS_IA.md` §5.

**QA único «Base para DM» entregado (2026-11-11).** Candidato:

- APK `0.6.0-preqa.1` (build 60100), con SHA-256 `b2243e9f…`;
- App de Windows `0.6.1`, con SHA-256 `e714303e…`;
- guía en `docs/technical/QA_BASE_DM_0.6.0-preqa.1.md`.

El control previo al QA de V11 sigue bloqueado por la hoja de PJ, que no cambió y queda fuera de este QA, con la excepción del propietario igual que preqa.13 y preqa.14.

**Siguiente paso:** recibir el resultado del QA del propietario y registrar cada observación antes de corregir (AGENTS §6.3).
