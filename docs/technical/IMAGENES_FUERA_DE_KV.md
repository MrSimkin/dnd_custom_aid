# Las imágenes fuera de KV (punto «N»)

Punto 15 de la revisión crítica y `PRODUCT.md` §18. El propietario pidió avanzar con esto el 18-11-2026.

## El problema real

Hoy las imágenes (retratos, mapas, handouts) viven en **Workers KV**. En el plan gratis, KV permite **1000 escrituras al día** ([Cloudflare, KV pricing](https://developers.cloudflare.com/kv/platform/pricing)). Cargar un set grande con muchas imágenes puede chocar con ese tope en una sola tarde, y cuando choca, la carga falla hasta el día siguiente.

## Qué propongo, y por qué no es Drive ni Dropbox

El plan B que conversamos decía «las imágenes en tu Google Drive o Dropbox». Al implementarlo, eso choca con un invariante del proyecto (`AGENTS.md` §3): **los clientes nativos no pueden guardar credenciales del proveedor de almacenamiento**, y la App es la dueña de los permisos. Con Drive o Dropbox, cada aparato tendría que iniciar sesión con tu cuenta de Google o Dropbox y manejar sus enlaces: más entradas, más cosas que se rompen y el control de quién ve qué se escapa de la App.

**Cloudflare R2** hace lo mismo sin ninguno de esos problemas: es almacenamiento de objetos del mismo proveedor que ya usas, lo lee y lo escribe el Worker (nunca el teléfono ni el PC), y tus jugadores siguen pidiendo las imágenes a la API, que decide si pueden verlas. Es, además, lo que `PRODUCT.md` §18 ya recomendaba.

**Costo (verificado el 18-11-2026, [R2 pricing](https://developers.cloudflare.com/r2/pricing/)):** el nivel gratuito de R2 incluye **10 GB de almacenamiento, 1 millón de escrituras y 10 millones de lecturas al mes, y la salida de datos no se cobra**. Para una campaña de handouts y mapas eso sobra. Sigue siendo **USD $0**, dentro del presupuesto de `AGENTS.md` §10.

## Qué está hecho (en el repositorio)

- El servidor ya no depende de KV: `MediaBytesStore` es la pieza, con dos implementaciones — `MediaStore` (KV, lo de hoy) y `BucketMediaStore` (almacenamiento de objetos).
- El Worker usa el *bucket* si está conectado (`MEDIA_BUCKET`) y, si no, sigue con KV. **Las imágenes que ya están en KV se siguen sirviendo desde ahí**, así que no hay que mover nada el primer día.
- La API no cambia: las Apps nunca saben dónde están los bytes.
- Pruebas: guarda una sola vez por suma de verificación, conserva el tipo de imagen, rechaza un tipo que no es imagen, responde vacío si no está, y la ruta `/v1/media/{sha}` entrega la imagen igual que con KV.

## Lo que tienes que hacer tú (acción en el proveedor, AGENTS §5 y §6)

1. En el panel de Cloudflare, **R2 → Create bucket**, nombre `dnd-custom-aid-media`, región automática.
2. En `backend/wrangler.jsonc`, agrega la conexión:

```json
"r2_buckets": [
  { "binding": "MEDIA_BUCKET", "bucket_name": "dnd-custom-aid-media" }
]
```

3. Desde `D:\DnD_Aid\repo\backend`, despliega:

```powershell
npx wrangler deploy
```

**Resultado esperado:** el despliegue termina sin error y nombra el *binding* `MEDIA_BUCKET`. Después, al enviar un handout nuevo, la imagen se guarda en R2 y se ve igual en la tablet.

**No compartas** tus tokens de Cloudflare ni el `DATABASE_URL`; para avisar que quedó listo basta con el nombre del bucket y la línea de salida del despliegue.

**Detente y avísame si:** el panel pide activar un plan de pago, la creación del bucket pide una tarjeta, o el despliegue dice que el *binding* no existe.

## El traslado de lo que ya está en KV (hecho el 20-11-2026, punto «K»)

Había un problema en lo anterior: si conectabas el *bucket*, el Worker dejaba de mirar KV, así que **las imágenes viejas habrían dejado de verse**. Eso ya está corregido, y el traslado tiene dos caminos que no se estorban:

1. **Solo: cada imagen se muda cuando alguien la mira.** Con los dos conectados, lo nuevo se guarda en R2 y lo que todavía está en KV se sirve igual y, de paso, se copia a R2. No hay que hacer nada y no hay un día en que algo se deje de ver. Si la copia falla, la imagen se entrega igual y se reintenta la próxima vez.
2. **De una vez: una pasada por tandas.** `POST /v1/media:migrate?limit=25` copia una tanda y te dice dónde seguir (`cursor`). Repites con `?cursor=...` hasta que responda `"done": true`. Solo un DM activo puede pedirlo.

**Nada se borra de KV.** La copia vieja se queda hasta que tú digas que el traslado terminó; leerla no cuesta nada y así no hay forma de perder una imagen a mitad de camino.

## Después, cuando quieras

Cuando el *bucket* exista y me digas, te paso las llamadas exactas de la pasada completa para tu set. Borrar lo viejo de KV es una decisión tuya y la dejo para el final.
