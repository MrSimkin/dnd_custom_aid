# Base para DM · Paquete 1 · Sincronización del contenido del DM

Estado: implementado en la rama `feature/dm-content-sync` (2026-11-10). Falta el paso del propietario en los proveedores, descrito en la sección 6.

## 1. Qué hace

El contenido del DM (criaturas, PNJ, reglas caseras, lugares, zonas, encuentros y escenas) viaja de la **App de PC** al servidor, y del servidor a la **tablet**, en los dos sentidos. Se sincronizan los dos ámbitos que ya usa la App de PC:

- **Biblioteca personal**: solo la lee y la cambia su dueño.
- **Contenido de campaña**: solo lo lee y lo cambia un DM ACTIVO de esa campaña. Los jugadores no lo leen; los handouts para jugadores son de un paquete posterior.

Reglas iguales a las de los PJ: identificadores estables, revisión del servidor, rechazo de escrituras viejas, cola durable con identificador de cambio estable (idempotencia), borrados que nunca reviven, y conflictos que nunca pisan trabajo local.

## 2. Servidor (Worker + Neon)

- Migración `database/migrations/0002_dm_content.sql`:
  - tabla `dm_content`, una fila por objeto, con ámbito inmutable;
  - funciones `dm_content_put`, `dm_content_delete`, `dm_content_list_campaign` y `dm_content_list_personal`.
- La autorización, las revisiones, los recibos de cambio y los borrados viven en esas funciones. El Worker las llama tal cual, y la prueba `database/tests/0008_dm_content_contract.sql` prueba el mismo código contra PostgreSQL 17 en CI.
- Errores con SQLSTATE privado `DCnnn`, traducidos por `backend/src/dmContentStore.ts` a los errores alojados de siempre (403, 404, 409 con revisión actual, 410, 409 de reutilización, 400).
- Rutas nuevas:

| Método y ruta | Uso |
|---|---|
| `GET /v1/me/content` | biblioteca personal del usuario (incluye borrados) |
| `GET /v1/campaigns/{id}/content` | contenido de campaña, solo DM activo (incluye borrados) |
| `PUT /v1/content/{id}` | crear (revisión esperada 0) o actualizar; cuerpo `{mutationId, expectedRevision, snapshot}` |
| `DELETE /v1/content/{id}` | borrar (lápida); cuerpo `{mutationId, expectedRevision}` |

- El documento viaja con formato `dnd-custom-aid.dm-content`, versión 1: `{format, version, content:{id, family, displayName, scope:{kind, ref}, provenance, createdAtEpochSeconds, updatedAtEpochSeconds}, payload}`. Tamaño máximo: 1 MB.
- La familia es texto abierto (`^[A-Z][A-Z0-9_]{0,63}$`). Así las familias futuras (paquetes, actividades de mazmorra) usan el mismo contrato. Una App más antigua informa esas familias y no las toca.

## 3. Apps (Shared)

- SQLDelight 28:
  - `dm_content_sync_baseline`: última revisión y documento normalizado que confirmó el servidor;
  - `hosted_dm_content_outbox`: cola durable, un cambio pendiente por objeto.
- `HostedDmContent.kt`:
  - `DmContentSnapshotCodec` lee y escribe el documento;
  - `HostedDmContentOutboxRepository` maneja la cola;
  - `HostedDmContentSyncService` hace una pasada completa.
- Cada pasada tiene tres pasos:
  1. Pone en cola lo cambiado localmente. Compara con la línea base sin la hora de edición, así que guardar sin cambios no envía nada.
  2. Envía la cola en orden.
  3. Recibe lo nuevo del servidor.
- La revisión local sigue siendo el contador de edición local. Al recibir un cambio, la revisión local avanza, de modo que un editor abierto con la versión anterior queda rechazado por desactualizado y no la pisa.
- Sin conexión, o si el servidor todavía no tiene estas rutas (404), la cola queda lista para reintentar y esa pasada no recibe nada.
- Conflictos: el objeto queda bloqueado y se informa, sin perder nada.
  - Si cambió aquí y en el servidor, gana la versión del servidor y la local se conserva.
  - Si cambió aquí y otro aparato lo borró, la versión local también se conserva.
  - La forma de resolverlos seguirá el diseño D1–D4 de `PC_SYNC_CONFLICT_RESOLUTION_DESIGN.md` cuando se programe.

## 4. Dónde se usa

- **App de PC**: pantalla de la campaña alojada, botón «Sincronizar contenido del DM» (solo con membresía DM activa).
- **Android (tablet)**: la sincronización normal de la cuenta agrega este paso si la cuenta es DM de alguna campaña. Un fallo del contenido del DM no oculta ni rompe la sincronización de los PJ. El informe QA alojado muestra los conteos. La pantalla propia del modo DM es el paquete 2.

## 5. Pruebas

- `backend/test/dm-content.test.mjs`: rutas, validación y traducción de errores (8 pruebas).
- `database/tests/0008_dm_content_contract.sql`: crear, repetir el mismo cambio (idempotencia), actualizar, ámbito personal, permisos de jugador y de DM expulsado, revisión vieja, cambio de familia o ámbito, objeto desconocido, reutilización del identificador de cambio, borrado, no revivir.
- `HostedDmContentSyncTest`: ida y vuelta entre App de PC, servidor y tablet con el contenido completo, la procedencia y la referencia de encuentro a criatura. También ediciones, borrados sin revivir, conflicto sin pisar, sin conexión, respuesta perdida sin doble aplicación, servidor sin rutas, guardar sin cambios y familia de una App más nueva.

## 6. Paso del propietario (pendiente)

En este computador Wrangler no tiene sesión iniciada (2026-11-10). Para que la tablet reciba de verdad hacen falta dos cosas:

1. aplicar `0002_dm_content.sql` en Neon;
2. publicar el Worker existente `dnd-custom-aid-api`. No se crea uno nuevo, y se conservan los secretos configurados.

El paquete de instrucciones está en el checkpoint canónico. Hasta entonces las Apps funcionan igual que antes: el contenido del DM queda en cola, sin perderse.
