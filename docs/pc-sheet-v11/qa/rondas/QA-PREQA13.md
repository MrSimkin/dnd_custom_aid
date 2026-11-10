# Ronda QA-PREQA13 — QA del propietario sobre `0.5.0-preqa.13`

## 0. Identidad del QA

- **ID de ronda:** QA-PREQA13. **Fecha de entrega:** 2026-11-08. **Propietario:** MrSimkin, por el chat de esta sesión.
- **Versión:** `0.5.0-preqa.13`, build 51300. **Commit:** `bdfdaa292e319dc0c5a5a9bce62ad1f619c4b477`. **CI:** Scaffold 42493357978, APK artefacto 13622832102, PDF artefacto 13622727830.
- **APK SHA-256:** `6f586093debc9bd54d5ded260e70b2292b15813f748537ffb07be24f1f226ad7`, entregado en `D:\DnD_Aid\APKs\dnd-custom-aid-0.5.0-preqa.13-build-51300.apk`.
- **Fichas:** `PJ-QA-1-Aldren-Vale.json`, `PJ-QA-2-Ilyra-Quill.json` y `PJ-QA-3-Mara-de-los-Siete-Umbrales.json`, copias exactas de `qa/pc-sheet/fixtures/01`, `02` y `03`.
- **Guía:** `GUIA_QA_PROPIETARIO_PREQA13.md`.
- **Excepción del propietario:** entra a QA sin que el control final haya pasado. Sus palabras: «no, estamos dando mil vueltas en revisiones independientes, es hora de; preparar 3 PJs para probar, darme el APK, preparar el QA» (2026-11-08).
- **PDF del propietario:** `PENDIENTE` (llegarán a `D:\DnD_Aid\PDF Check`).

## 1. Recepción completa e inmutable

Pendiente: se copian aquí, literales y numerados, todos los comentarios del propietario, con su ID `V11-QA-###`.

| ID | Cita literal | Artefacto/Página | Defecto observado | Esperado | Estado |
|---|---|---|---|---|---|
| V11-QA-003 | «No puedo loguearme, no recibo ningun correo, ni en Outlook ni en GMail. Deberé terminar el QA aqui hasta resolver esto.» | P2 Entrar y sincronizar (pantalla QA / autenticación hospedada DEV) | No llega el código de acceso a Outlook ni a Gmail; no puede entrar ni sincronizar | El código llega y el propietario entra | OPEN |
| V11-QA-004 | «ok, haré eso, pero anota que debemos agregar a la App, resolución de conflictos y poder importar desde servidor viendo diferencias y todo lo involucrado junto a lo que corresponda a la QA que estamos viendo y desarrollar de inmediato un buen QA y mejorar la UX de todo lo relacionado y mejorar el UX del DEV screen para que sea mas claro.» | Sincronización de PJ (conflicto de Aldren) | La App no deja ver diferencias ni traer la versión del servidor al resolver un conflicto | Resolver conflictos viendo diferencias, con opción de traer la del servidor; con su QA y buena UX | OPEN |
| V11-QA-005 | (misma cita) | Pantalla «QA / autenticación hospedada DEV» | La pantalla DEV no es clara | Pantalla DEV más clara y mejor UX de entrar y sincronizar | OPEN |
| V11-QA-006 | «y te faltó, una pantalla de login clara, para jugadores y DM, ahi hablaremos los detalles cuando corresponda» | Inicio de sesión de la App | No hay una pantalla de login clara para jugadores y DM | Pantalla de login clara para jugadores y DM; detalles con el propietario | OPEN |

## Carga de los PJ en la base DEV alojada (2026-11-10)

El propietario pidió los PJ por SQL y después eligió que el Worker lo ejecutara con la herramienta `neon` de su PC, con una sesión que él mismo inició (`neon auth`). El Worker no vio ni pidió credenciales.

- **Destino:** Neon, proyecto `holy-meadow-19010740` (`dnd-custom-aid-dev`), rama `production`, base `dnd-custom-aid-dev`. Organización `MrSimkin_Org`.
- **Script:** `D:\DnD_Aid\APKs\QA preqa.13\qa_pj_preqa13_neon_sql_editor.sql`, SHA-256 `48fc7d518c399631c3e582e8be60702bc13f6688dff811a3d55ba71c279a5abe`. Solo caracteres ASCII: los acentos van como `\uXXXX` dentro del JSON, para que ningún cliente pueda volver a dañarlos. Mismo `ON CONFLICT` que `database/qa/seed_pc_sheet_runtime_characters.sql`; no toca dueño, controlador ni la membresía de Gmail.
- **Hallazgo antes de ejecutar (solo lectura):** los tres PJ de QA tenían los acentos dañados en la base desde la carga del 2026-09-24 («Acólito» guardado como «AcÃ³lito»): 3 de los 4 PJ de la base. Su última modificación era del 2026-09-24, así que no había ediciones del propietario que perder. Las revisiones eran Aldren 1, Ilyra 1 y Mara 2.
- **Ejecución:** `BEGIN`, comprobación de Outlook, campaña, membresía DM, tres `INSERT … ON CONFLICT`, comprobación final y `COMMIT`. Sin errores.
- **Resultado devuelto por Neon:**

| id | PJ | revisión | dueño y controlador | versión |
|---|---|---|---|---|
| `7a000000-0000-4000-8000-000000000101` | Aldren Vale | 2 | Gmail `4ba0f476-…` | 2 |
| `7a000000-0000-4000-8000-000000000102` | Ilyra Quill | 2 | Gmail `4ba0f476-…` | 2 |
| `7a000000-0000-4000-8000-000000000103` | Mara de los Siete Umbrales | 3 | Gmail `4ba0f476-…` | 2 |

- **Membresía de la campaña:** Outlook `f34bc5f0-…` DM ACTIVE; Gmail `4ba0f476-…` PLAYER ACTIVE.
- **Verificación posterior:** 0 PJ con acentos dañados en toda la base. Cada ficha guardada se leyó en base64 y es igual a su fixture del repositorio (blobs `498a47e9`, `9b980847`, `ed919c6e`).

## Set de pruebas del propietario (2026-11-10)

Página de QA: https://claude.ai/artifact/KCcKT6bxKiwXd5t4WPFwGi (privada del propietario). Tiene 41 pruebas: preparación P1–P3, Aldren A1–A12, Ilyra I1–I8, Mara M1–M13 y extras X1–X5. Cada una dice qué hacer y qué debe verse, con los valores esperados tomados de las fichas y del número de páginas medido en Android con `preqa.13` (Aldren 6 y 6; Ilyra 7 y 9 con Libro; Mara 9 y 11 con Libro).

El propietario marca Bien, Falla o No aplica y escribe su comentario. Cada resultado se guarda en la colección `resultados` de la página (un documento por prueba: `estado`, `nota`, `actualizado`). El Worker la lee al recibir el QA y copia cada comentario literal a la sección 1 de esta ronda y al registro, sin resumirlo.

## Resultados recibidos (2026-11-10)

El propietario pegó en el chat el resumen de la página de QA, literal:

```
QA hoja V11 preqa.13
P1 Instalar el APK: BIEN
P2 Entrar y sincronizar: FALLA — No puedo loguearme, no recibo ningun correo, ni en Outlook ni en GMail. Deberé terminar el QA aqui hasta resolver esto.
P3 Sacar los 6 PDF: NO APLICA
```

Todas las demás pruebas, A1 a X5, quedaron «NO APLICA» (la A2 no figura en el resumen). Ninguna prueba de la hoja V11 se ejecutó: el QA se detuvo en el inicio de sesión.

**Diagnóstico inicial del Worker, sin enviar correos:** la App usa el mismo proyecto de Descope y el mismo servidor que en el login verificado del 2026-09-15 (`AndroidHostedAuth.kt` sin cambios desde `bf5f8430`). El proyecto de Descope responde y publica sus claves (HTTP 200), y el servidor responde 401 sin credenciales, como corresponde. La falla está en el envío del correo de Descope o en el mensaje que mostró la pantalla; falta saber cuál.

**Dato del propietario (2026-11-10):** al tocar «Enviar código», la pantalla mostró «Código enviado a…». Descope aceptó la solicitud de código; lo que falla es la entrega del correo. El siguiente paso es del propietario, en su buzón y en la consola de Descope, porque el Worker no tiene acceso autenticado a Descope (AGENTS §5).

**Dato del propietario (2026-11-10, después):** el código llegó a Gmail con mucho retraso, ya vencido; a Outlook no llegó nada.

**Revisión de Descope por el Worker** (consola abierta en el navegador integrado con la sesión que inició el propietario; solo lectura, nada cambiado):

- **Proyecto:** `dnd-custom-aid-dev`, plan gratuito. Uso del mes: 0 de 7.500 usuarios activos y 0 de 100 envíos SMS o de voz; ningún límite alcanzado.
- **Auditoría, últimas 24 h:** dos `LoginStarted` del 2026-11-10, a las 11:55 y 12:00 (hora local), de dos usuarios distintos, y ningún inicio completado. Un tercer evento es la entrada del propietario a la consola.
- **Troubleshooting, últimas 24 h:** ningún registro de error.
- **Conectores:** ninguno configurado. El correo sale por el servicio compartido de Descope («Connector: Descope», plantilla «System»).
- **Código de un solo uso:** vence a los **3 minutos**; 3 intentos cada 60 s; «Allow unverified recipient addresses» desactivado.

**Lectura:** Descope envía sin errores, pero su remitente compartido llega tarde a Gmail (más de 3 minutos, así que el código vence) y Outlook no lo recibe o lo filtra en silencio. No es un defecto de la App ni de V11. Las soluciones tocan la configuración de Descope y las decide el propietario.

**Cambio autorizado en Descope (2026-11-10):** el propietario dijo «si, hazlo. pero, no sigas adelante con Descope». El Worker subió la vigencia del código de un solo uso de **3 a 10 minutos** en el proyecto `dnd-custom-aid-dev` (Authentication Methods → One-time Password) y guardó. Tras recargar la página, `expirationTime = 10 minutes`; intentos (3 cada 60 s), dominio, destinatarios no verificados y conectores quedaron igual. Un primer intento de escribir el valor no llegó a ningún campo (se comprobó que nada cambió) y se repitió sobre el campo correcto. No se hizo nada más en Descope.

**Login del propietario:** con capturas de la App, «Autenticación completa. Worker respondió para la cuenta f34bc5f0-4d35-4d09-b771-505b3851440c» (Outlook, DM de la campaña de QA). El propietario no sabe por qué falló al principio y sigue con el QA. V11-QA-003 queda OPEN como observación del propietario hasta que él diga si el login le quedó resuelto.

**Sincronización del propietario (2026-11-10):** «2 campaña(s) conciliada(s); 0 conflicto(s) de campaña y 1 conflicto(s) de PC fueron preservados sin sobrescribir». El log QA de `0.5.0-preqa.13 (51300)`, sin secretos, muestra:

- 2 campañas conciliadas (`Hosted Batch Test` y `QA - PC Sheet PDF Runtime`), sin conflictos de campaña.
- 4 PJ alojados: 3 sin cambios en esa pasada (Ilyra y Mara ya estaban al día por la sincronización anterior) y 1 en conflicto.
- **Aldren Vale** (`7a000000-…-0101`): `LOCAL_AND_HOSTED_CHANGED`. Revisión local 1, alojada 2, base 1. La copia local difiere de la base y no es igual a la alojada. Ninguna mutación pendiente en la cola de salida.

**Indicación al propietario:** no usar «Resolver conflicto: conservar PC local» (subiría la copia vieja) ni borrar el Aldren local (podría propagar el borrado). Para el QA de Aldren, importar `PJ-QA-1-Aldren-Vale.json`, que es la misma ficha que está alojada. El conflicto queda intacto para decidirlo después del QA.

**Pedido del propietario (2026-11-10):** cita literal en V11-QA-004, V11-QA-005 y V11-QA-006 (pantalla de login para jugadores y DM; sus detalles se hablan con el propietario cuando corresponda). Entra en el próximo lote de desarrollo, junto con las correcciones de este QA. Antes de programar se presenta el diseño de la resolución de conflictos con imágenes, y las decisiones de comportamiento se le preguntan al propietario de una en una.
