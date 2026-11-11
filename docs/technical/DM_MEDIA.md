# Base para DM · Paquete 4 · Archivos e imágenes

Estado: implementado (paquete 4). El espacio KV `dnd-custom-aid-media` (enlace `MEDIA`) se creó y se publicó el 2026-11-11; la evidencia está en `DM_REGLAS_IA.md` §5. Lo verifica el QA único de la «Base para DM».

## Decisiones del propietario

- **P3 «En la tablet del DM»:** el handout se muestra a pantalla completa en la tablet. En este QA no se envía a los teléfonos.
- **P5 «Viajan solas»:** handouts, mapas y retratos suben y bajan solos por un servicio de archivos en internet, tras la revisión de costo USD $0.
- **P27 «Workers KV (Recommended)» (2026-11-10):** el servicio de archivos es Workers KV, en la misma cuenta gratis de Cloudflare del Worker.

## Revisión USD $0 (D-0075, AGENTS §10), 2026-11-10

| Opción | Medio de pago | Al pasar el cupo gratis | Cupo gratis | Proveedor nuevo |
|---|---|---|---|---|
| **Workers KV** (elegida) | No; es la cuenta Free del Worker actual | Las operaciones fallan con error; no hay cobro («further operations of that type will fail with an error») | 1 GB; máximo 25 MiB por valor; 1.000 escrituras y 100.000 lecturas al día | No |
| Cloudflare R2 | Sí, aun dentro del cupo | Se cobra el exceso, sin tope | 10 GB al mes | No, pero exige tarjeta |
| Backblaze B2 | No para registrarse | Topes configurables, no para todo | 10 GB | Sí |
| Supabase Storage | No | Restringe, y el proyecto se pausa tras una semana sin uso | 1 GB | Sí |

Fuentes:
- Workers KV, oficiales: [límites](https://developers.cloudflare.com/kv/platform/limits/) y [precios](https://developers.cloudflare.com/workers/platform/pricing/).
- R2: [precios](https://developers.cloudflare.com/r2/pricing) y [comunidad de Cloudflare](https://community.cloudflare.com/t/why-using-r2-free-tier-involves-giving-card-info/945179).
- Backblaze: [blog de Backblaze](https://www.backblaze.com/blog/free-isnt-always-free-a-guide-to-free-cloud-tiers/).
- Supabase: [guía de terceros](https://reeve.page/blog/supabase-free-plan-limits).

Sobre la dependencia del proveedor:
- Las imágenes se nombran por su suma de control y viven detrás del Worker. Pasar a R2 u otro servicio solo cambia `backend/src/mediaStore.ts`.
- El tope de 1 GB alcanza para unas 3.000 imágenes de alrededor de 300 KB.
- Sobrepasar el cupo diario hace fallar una subida, que se reintenta en la siguiente sincronización.

## Cómo funciona

- **Un handout es contenido del DM** (familia `HANDOUT`). Su descripción viaja por la sincronización del paquete 1, y la imagen viaja aparte:
  - **descripción** (`HandoutPayload`): tipo (Handout, Mapa o Retrato de PNJ), formato (JPEG, PNG o WebP), tamaño, SHA-256, medidas, visibilidad (Solo DM, o Se puede mostrar a los jugadores), texto al pie y notas del DM;
  - **imagen:** un archivo nombrado por su SHA-256 en el almacén del aparato (`MediaBlobStore`) y en KV (`media/<sha256>`). Se guarda una sola vez por suma de control y nunca se edita.
- **Sincronización** (`HostedDmContentSyncService` con `blobs`):
  - antes de enviar la descripción de un handout, sube su imagen, si no estaba ya;
  - después de recibir, baja la imagen de cada handout activo que no la tenga en el aparato;
  - si la suma de control no coincide, la imagen se descarta y el handout queda como «imagen pendiente»;
  - sin conexión, la imagen se reintenta en la siguiente pasada.
- **Worker:**
  - `PUT /v1/media/{sha256}`: solo un DM activo de alguna campaña. Acepta JPEG, PNG o WebP de hasta 5 MiB, verifica que la suma de control coincida, y una segunda subida no escribe nada;
  - `GET /v1/media/{sha256}?content={id}`: solo quien puede leer ese handout activo que nombra esa imagen. Si no puede, la respuesta es la misma que si no existiera (404);
  - sin el espacio KV configurado, las dos rutas responden 503 y el resto del servidor no cambia.
- **App de PC** (destino «Medios / Handouts»):
  - importa JPG, PNG o WebP, eligiendo campaña activa o biblioteca personal;
  - las imágenes de más de 2048 px o más de 1,5 MB se reducen: a PNG si tienen transparencia, a JPEG con calidad 85 si no;
  - el editor permite cambiar nombre, tipo, visibilidad, texto al pie y notas, y borrar con confirmación;
  - las imágenes se guardan en `~/.dnd_custom_aid/media`.
- **Tablet:**
  - en el Escritorio DM, «Handouts e imágenes» muestra miniaturas con tipo y «Solo DM»;
  - al tocar una, se abre a pantalla completa sobre fondo negro, con el texto al pie y «Cerrar»;
  - un handout Solo DM lleva una etiqueta roja; las notas del DM nunca aparecen en esa pantalla;
  - las imágenes se guardan en los archivos privados de la App (`files/media`).

## Verificación

- `HostedDmContentSyncTest`:
  - la imagen viaja con su descripción, y se comprueba el tipo y la visibilidad;
  - una imagen alterada en el servidor se rechaza.
- `backend/test/media.test.mjs`:
  - una sola escritura por imagen;
  - rechazo de quien no es DM, de formatos no aceptados, de una suma de control equivocada y de archivos de más de 5 MiB;
  - lectura autorizada, y lo no autorizado responde igual que lo inexistente;
  - 503 sin KV.
- `database/tests/0009_media_authorization_contract.sql` (CI, PostgreSQL 17): DM activo, jugador, suma de control equivocada, handout borrado y DM expulsado.
- `DesktopHandoutImagesTest`: PNG pequeño intacto, imagen grande reducida a JPEG de 2048 px, transparencia conservada en PNG, y archivo que no es imagen.
- En el emulador de tablet en modo de solo lectura, con dos handouts sembrados en datos desechables (uno Solo DM y un mapa visible): miniaturas, las dos pantallas completas y Cerrar.

## Paso del propietario (pendiente, junto con el del paquete 1)

Desde `D:\DnD_Aid\repo\backend`, con `npx wrangler login` ya hecho:

1. Ejecuta `npx wrangler kv namespace create dnd-custom-aid-media`. Si pregunta si agrega la configuración, responde que sí con el nombre de enlace `MEDIA`. Si no lo pregunta, devuelve el `id` que muestra; el id no es secreto.
2. `npx wrangler deploy` (el mismo Worker `dnd-custom-aid-api`).
3. Devuelve el `id` del espacio KV y la línea «Current Version ID». El Worker deja el id registrado en `wrangler.jsonc`.

Costo: USD $0. Si en algún momento Cloudflare pide tarjeta o un plan pago, detente y avisa.
