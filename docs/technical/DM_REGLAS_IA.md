# Base para DM · Consultar reglas con IA (paquete 8)

**Estado:** implementado el 2026-11-11 en la tablet, en Windows y en el Worker, que ya está publicado con IA (evidencia en la sección 5). Lo verifica el QA único de la «Base para DM».

## Cómo funciona (D-0041, D-0070, P4)

1. **En el aparato:** el DM escribe la pregunta en el panel 📖 Reglas. La App busca en los dos SRD en español que lleva incluidos, sin internet; esto ya existía y sigue funcionando sin conexión.
2. **Qué viaja:** al tocar «Responder con IA», solo se envían al Worker la pregunta (500 caracteres como máximo) y los primeros 6 fragmentos encontrados, con la versión de la campaña primero. Ruta: `POST /v1/rules/answer`, con la sesión del usuario.
3. **En el servidor:** el Worker pide a Cloudflare Workers AI una respuesta en español que use solo esos fragmentos y los cite como [1], [2]…
   - El modelo se puede cambiar con la variable `AI_MODEL`, sin tocar el código. El de partida es `@cf/meta/llama-3.1-8b-instruct-fp8`.
4. **En pantalla:** la App muestra la respuesta, marcada «Respuesta (IA) · revisa el texto citado», sobre los fragmentos numerados con su versión y página.
5. **Sin IA:** si el binding no existe, si se agotó el cupo diario o si el modelo falla, la ruta responde 503. La App dice que la IA no está disponible ahora y los fragmentos del SRD siguen a la vista.

No hace falta cargar el SRD en Neon: la búsqueda ya ocurre en el aparato.

## Costo (revisado el 2026-11-11, AGENTS §10)

- **Cupo gratis:** Workers AI tiene 10.000 «Neurons» gratis por día, en el plan Free y en el Paid.
- **Plan Free:** no tiene cobro por exceso. Al agotarse el cupo, las peticiones fallan y la App vuelve a la búsqueda sin conexión.
- **Costo por pregunta:** con `llama-3.1-8b-instruct-fp8` (unas 13.800 neuronas por millón de tokens de entrada y 26.100 de salida), una pregunta con 6 fragmentos gasta unas 50 neuronas. Alcanza para unas 200 preguntas diarias gratis.
- **Modelo anterior:** `llama-3.1-8b-instruct` aparece retirado desde el 2026-05-30 y no se usa.
- **Condición:** el costo sigue en USD $0 mientras la cuenta quede en el plan Workers Free. No cambiar de plan.

Fuentes: [precios de Workers AI](https://developers.cloudflare.com/workers-ai/platform/pricing/), [precios de Workers](https://developers.cloudflare.com/workers/platform/pricing/), [ficha del modelo](https://developers.cloudflare.com/workers-ai/models/llama-3.1-8b-instruct-fp8/).

## Pruebas

- **Worker:** `backend/test/rules.test.mjs` cubre:
  - una respuesta hecha con los fragmentos;
  - 503 sin binding y cuando el modelo falla;
  - la validación de pregunta, fragmentos, versión y página, y el recorte de cada fragmento;
  - que solo se acepta POST.
- **Cliente compartido:** `HostedApiClientTest` revisa que la pregunta viaje con sus fragmentos y que un 503 llegue como «no disponible».
- **Windows:** `DesktopDmWorkspaceTest` busca «media cobertura» sin conexión, pide la respuesta con un servidor simulado y dibuja `w13_reglas_ia.png`.

## 4. Paso del propietario: Neon y Cloudflare, todo junto antes del QA

Este paquete reemplaza los pasos sueltos anteriores (checkpoint y `DM_MEDIA.md` §5). Costo: USD $0, sin planes nuevos.

1. **Neon** (consola web → proyecto DEV → SQL Editor):
   - Pegar y ejecutar completo `database/migrations/0002_dm_content.sql`.
   - Resultado esperado: termina sin errores y la lista de tablas muestra `dm_content`.
   - No compartir la cadena de conexión.
2. **Cloudflare**, desde `D:\DnD_Aid\repo\backend`:
   1. `npm install`
   2. `npx wrangler login`, que abre el navegador para iniciar sesión.
   3. `npx wrangler kv namespace create dnd-custom-aid-media`. Si pregunta si agrega la configuración, responder que sí, con el nombre de enlace `MEDIA`. Si no lo pregunta, anotar el `id` que muestra (no es secreto).
   4. `npx wrangler deploy`. Publica el Worker existente `dnd-custom-aid-api`, ya con el enlace de IA (`AI`) que trae `wrangler.jsonc`. No crear un Worker nuevo; los secretos se conservan.
3. **Evidencia que devolver:**
   - que `dm_content` aparece en Neon;
   - el `id` del espacio KV;
   - la línea «Current Version ID»;
   - que `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/health` responde `ok`.
4. **Detenerse si:**
   - el paso 1 falla: no publicar el paso 2;
   - Cloudflare pide elegir o pagar un plan: no aceptar.
5. **No compartir:** tokens, contraseñas ni cadenas de conexión.

## 5. Evidencia: paso de proveedores hecho (2026-11-11)

El propietario autorizó en su navegador las sesiones de Cloudflare (`wrangler login`) y de Neon (`neonctl auth`). El Worker hizo el resto desde la consola. Ninguna clave se mostró, se pidió ni se guardó.

**Neon**, proyecto `dnd-custom-aid-dev` (`holy-meadow-19010740`), rama `production`, base `dnd-custom-aid-dev`:
- **Antes:** la migración 0001 estaba y `dm_content` no existía.
- **Qué se hizo:** se aplicó `database/migrations/0002_dm_content.sql`. La conexión pasó directo de `neonctl` al controlador de Neon, sin mostrarse.
- **Después:** existen `dm_content` y sus 5 funciones `dm_content_*`.

**Cloudflare:**
- **Espacio KV:** `dnd-custom-aid-media`, con id `3a1023324c2e4a13b070a97a5995edc3` y enlace `MEDIA`, quedó registrado en `backend/wrangler.jsonc`.
- **Publicación:** `wrangler deploy` del Worker existente `dnd-custom-aid-api`, con enlaces `MEDIA` (KV) y `AI`. Current Version ID: `a63a59c5-8b27-451c-8bf8-b1185b6aa1f3`.
- **Claves:** el Worker conserva sus dos secretos (`DATABASE_URL` y `DESCOPE_PROJECT_ID`; solo se leyeron los nombres).
- **Pruebas desde afuera:**
  - `/health` responde `{"status":"ok"}`;
  - `/v1/me`, `/v1/rules/answer` y `/v1/media/…` sin sesión responden 401 `UNAUTHENTICATED`;
  - `@cf/meta/llama-3.1-8b-instruct-fp8` figura en la lista de modelos de la cuenta, y el modelo retirado ya no.

**Pendiente:** la prueba con una sesión real queda para el QA único del propietario. Cubre la sincronización del contenido del DM, las imágenes y la respuesta con IA.
