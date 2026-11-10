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
| V11-QA-007 | «ok, la nueva pantalla de exportación me confunde, ya no sé como sacar las hojas A y B, especialmente porque aun aparecen los nombres antiguos» | «Hoja de personaje PDF» (capturas de Aldren) | V11 queda escondida en «Salida PDF de diagnóstico» y «Familia visual» sigue ofreciendo las cuatro hojas antiguas | Sacar V11 A y B de forma clara, sin las hojas antiguas como opción principal | OPEN |
| V11-QA-008 | «A1 Número y orden de páginas: NO APLICA — Adicional: / Mientras estamos en debug que diga Variante A o B está bien o P1 -- XXXX. Pero despues eso debe salir. / Prefiero un mejor retrato de salida, algo mas "lindo" y acorde.» | Aldren, todas las páginas (cabecera) y P1 RETRATO | Ver cita | Mientras sea debug, «VARIANTE A/B» o «P1 - …» en la cabecera está bien, pero en la versión final debe salir. El marco de retrato de salida (sin foto) es más «lindo» y acorde. | OPEN |
| V11-QA-009 | «A2 Cabecera y clase: BIEN — No recuerdo si la App guarda la sub clase, si la guarda deberia ser Clase "Guerrero (sub clase) 5" por ejemplo. Si no lo guarda, debemos guardarlo.» | Aldren P1, CLASE / NIVEL | Ver cita | Si la App guarda la subclase, CLASE / NIVEL la muestra, por ejemplo «Guerrero (subclase) 5». Si no la guarda, la App debe guardarla. | OPEN |
| V11-QA-010 | «A3 Atributos y salvaciones: BIEN — Observacion adicional, deberiamos considerar una versión de exportación que muestre los numeros variables (en toda la hoja), como atributos, habilidades, CA, etc en un color mas "transparente" para que el jugador pueda escribir encima y "borrarlo" y sobre escribir (a mano)» | Toda la hoja: atributos, habilidades, CA y demás números variables | Ver cita | Considerar una versión de exportación con los números variables en un color más transparente, para que el jugador escriba encima a mano. | OPEN |
| V11-QA-011 | «A8 Equipo, tesoro y Equipo Especial: NO APLICA — Hay solo 6 objetos, Ballesta ligera, Mochila de explorador, Libro de oraciones, Incienso, Vestiduras, Ropa comun. Usa 7 lineas, pero solo 6 objetos, verifica que eso está bien. / Faltan espacios vacios en el equipo especial para "lugares sin llenar", es decir, linea 100% vacía, y creo que son espacios MUY altos.» | Aldren P2: EQUIPO y EQUIPO ESPECIAL | Ver cita | Verificar que EQUIPO use una línea por objeto (6 objetos, no 7 líneas). EQUIPO ESPECIAL tiene filas 100 % vacías para lugares sin llenar, y sus filas no son tan altas. | OPEN (bloqueante) |
| V11-QA-012 | «A9 Rasgos: BIEN — te corrijo en tu notacion, son 8 rasgos, entre la pagina 1 y 2, los rasgos no tienen la sangria que usamos en Equipo, para entender que es parte del mismo texto.» | Aldren P1 y P2: RASGOS | Ver cita | Los 8 rasgos, repartidos entre P1 y P2, usan la sangría de continuación de EQUIPO, para ver que una línea siguiente es parte del mismo rasgo. | OPEN (bloqueante) |
| V11-QA-013 | «A12 Parecido con el golden: BIEN — se ve bien, arreglaria quizas "QUIZAS", esteticamente levemente la pagina 4 por el coloreado del interlineado de recursos y acciones, algo raro hay alli, y el uso del espacio en recursos, parece mucho espacio desperdiciado en cada accion y parece caber mas texto, y parece un poco pequeña la descripcion, aqui lo dejo a tu criterio.» | Aldren p4: ACCIONES y RECURSOS | Ver cita | A criterio del Worker: revisar el coloreado del interlineado de ACCIONES y RECURSOS, el espacio desaprovechado en cada recurso y el tamaño de la descripción. | OPEN |
| V11-QA-014 | «I1 Número y orden de páginas: BIEN — A y B tienen 9 paginas. A no tiene solo 7, pero creo que eso está bien» | Ilyra A y B: número de páginas | Ver cita | Información: A salió con Libro (9 páginas), lo que el propietario considera bien. | OPEN |
| V11-QA-015 | «I3 Lanzamiento de conjuros en la página 1: BIEN — Los espacios de los cuadros dentro de Lanzamiento de conjuro no me parecen bien distribuidos, los haria alineados a la izquierda y el orden de los niveles lo tienes a lo "telefono". Lo haria hacia abajo. / 1 4 7 / 2 5 8 / 3 6 9» | Ilyra P1: LANZAMIENTO DE CONJUROS, casillas de espacios | Ver cita | Las casillas de cada nivel alineadas a la izquierda, y los niveles ordenados hacia abajo por columnas: 1 4 7 / 2 5 8 / 3 6 9. | OPEN (bloqueante) |
| V11-QA-016 | «I7 Equipo y rasgos: BIEN — la descripcion del spellbook, tiene especio adicional innecesario (abajo en la línea)» | Ilyra: descripción en el Libro (spellbook) | Ver cita | Sin espacio adicional innecesario debajo de la descripción. | OPEN (bloqueante) |
| V11-QA-017 | «M7 Equipo que continúa: FALLA — El equipo no tiene el mismo formato del equipo de pagina normal, lineas con sangrias» | Mara: EQUIPO en la hoja de continuación | Ver cita | EQUIPO en continuación con el mismo formato que EQUIPO de P2, incluidas las líneas con sangría. | OPEN (bloqueante) |
| V11-QA-018 | «X4 Niveles épicos: NO APLICA — no se como activar niveles epicos, porque creo que no lo hemos implementado» | Niveles épicos | Ver cita | Información: el propietario no sabe cómo activar los niveles épicos y cree que no están implementados. | OPEN |

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

**Indicación dada al propietario para seguir el QA (V11-QA-007):** en «Prueba de exportación nueva (solo DEBUG)», tocar «Cuatro familias anteriores» y elegir «V11 prueba interna A» o «B»; con eso «Familia visual» deja de contar.

## PDF del propietario (2026-11-10)

El Worker trajo con `adb pull "/sdcard/Download/." "."` todo lo que había en Descargas del emulador del propietario (`emulator-5554`, App `0.5.0-preqa.13`) a `D:\DnD_Aid\PDF Check`. Los archivos anteriores del propietario ya estaban en la subcarpeta `2026-09-28`; no se pisó nada.

| Archivo | Páginas | Variante | Libro | SHA-256 (inicio) | Frente a la prueba del Worker con el mismo APK |
|---|---|---|---|---|---|
| `Aldren Vale - Hoja de PJ - PREQA V11 A.pdf` | 6 | A | no | `02929709b264` | Igual salvo P2: Cobre 12 en vez de 0 |
| `Aldren Vale - Hoja de PJ - PREQA V11 B.pdf` | 6 | B | no | `db7af8ee9733` | Igual salvo P2: Cobre 12 en vez de 0 |
| `Ilyra Quill - Hoja de PJ - PREQA V11 A.pdf` | 9 | A | sí, 2 páginas | `22401060f3f9` | Exportada con Libro; la prueba del Worker de A no lo llevaba |
| `Ilyra Quill - Hoja de PJ - PREQA V11 B.pdf` | 9 | B | sí, 2 páginas | `7d4b20c5b359` | Texto idéntico |
| `Mara de los Siete Umbrales - Hoja de PJ - PREQA V11 A.pdf` | 11 | A | sí, 2 páginas | `98ca5e0f0f72` | Exportada con Libro; la prueba del Worker de A no lo llevaba |
| `Mara de los Siete Umbrales - Hoja de PJ - PREQA V11 B.pdf` | 11 | B | sí, 2 páginas | `536a871466e4` | Texto idéntico |

Ningún PDF tiene acentos dañados. El Aldren exportado es la copia local en conflicto: su única diferencia con la ficha alojada es Cobre 12, que explica el `LOCAL_AND_HOSTED_CHANGED`.

## Resultado completo del QA (2026-11-10), literal

El propietario pegó en el chat el resumen de la página de QA:

```
QA hoja V11 preqa.13
P1 Instalar el APK: BIEN
P2 Entrar y sincronizar: BIEN
P3 Sacar los 6 PDF: BIEN
A1 Número y orden de páginas: NO APLICA — Adicional:
Mientras estamos en debug que diga Variante A o B está bien o P1 -- XXXX. Pero despues eso debe salir.
Prefiero un mejor retrato de salida, algo mas "lindo" y acorde.
A2 Cabecera y clase: BIEN — No recuerdo si la App guarda la sub clase, si la guarda deberia ser Clase "Guerrero (sub clase) 5" por ejemplo. Si no lo guarda, debemos guardarlo.
A3 Atributos y salvaciones: BIEN — Observacion adicional, deberiamos considerar una versión de exportación que muestre los numeros variables (en toda la hoja), como atributos, habilidades, CA, etc en un color mas "transparente" para que el jugador pueda escribir encima y "borrarlo" y sobre escribir (a mano)
A4 Habilidades: BIEN
A5 Ataques: BIEN
A6 Combate y supervivencia: BIEN
A7 Municiones: NO APLICA
A8 Equipo, tesoro y Equipo Especial: NO APLICA — Hay solo 6 objetos, Ballesta ligera, Mochila de explorador, Libro de oraciones, Incienso, Vestiduras, Ropa comun. Usa 7 lineas, pero solo 6 objetos, verifica que eso está bien.
Faltan espacios vacios en el equipo especial para "lugares sin llenar", es decir, linea 100% vacía, y creo que son espacios MUY altos.
A9 Rasgos: BIEN — te corrijo en tu notacion, son 8 rasgos, entre la pagina 1 y 2, los rasgos no tienen la sangria que usamos en Equipo, para entender que es parte del mismo texto.
A10 Trasfondo: BIEN
A11 Hoja de continuación: BIEN
A12 Parecido con el golden: BIEN — se ve bien, arreglaria quizas "QUIZAS", esteticamente levemente la pagina 4 por el coloreado del interlineado de recursos y acciones, algo raro hay alli, y el uso del espacio en recursos, parece mucho espacio desperdiciado en cada accion y parece caber mas texto, y parece un poco pequeña la descripcion, aqui lo dejo a tu criterio.
I1 Número y orden de páginas: BIEN — A y B tienen 9 paginas. A no tiene solo 7, pero creo que eso está bien
I2 Cabecera, clase y combate: BIEN
I3 Lanzamiento de conjuros en la página 1: BIEN — Los espacios de los cuadros dentro de Lanzamiento de conjuro no me parecen bien distribuidos, los haria alineados a la izquierda y el orden de los niveles lo tienes a lo "telefono". Lo haria hacia abajo.
1 4 7
2 5 8
3 6 9
I4 Lista de conjuros: BIEN
I5 Tarjetas de arriba de la página de conjuros: BIEN
I6 Libro de conjuros: BIEN
I7 Equipo y rasgos: BIEN — la descripcion del spellbook, tiene especio adicional innecesario (abajo en la línea)
I8 Parecido con el golden: BIEN
M1 Número y orden de páginas: BIEN
M2 Cabecera y combate: BIEN
M3 Atributos propios: NO APLICA
M4 Banda inferior de la página 1: BIEN
M5 Ataques: BIEN
M6 Hojas de continuación: BIEN
M7 Equipo que continúa: FALLA — El equipo no tiene el mismo formato del equipo de pagina normal, lineas con sangrias
M8 Equipo Especial que continúa: BIEN
M9 Rasgos: BIEN
M10 Recursos: BIEN
M11 Conjuros y Libro: BIEN
M12 Notas digitales: BIEN
M13 Parecido con el golden: BIEN
X1 Retrato: BIEN
X2 Compartir: BIEN
X3 Personaje nuevo vacío: BIEN
X4 Niveles épicos: NO APLICA — no se como activar niveles epicos, porque creo que no lo hemos implementado
X5 Impresión en papel: BIEN
```

Cada comentario quedó como observación propia, de V11-QA-008 a V11-QA-018. **Control de completitud:** 11 pruebas con comentario y 11 observaciones nuevas; las demás marcas (BIEN, NO APLICA sin comentario) quedan en esta cita. M7 es la única FALLA.

## Diagnóstico Q2 (2026-11-10), antes de tocar código

Revisado sobre los PDF reales del propietario (`D:\DnD_Aid\PDF Check`, candidato preqa.13) y sobre el golden (`v11_ordinario_6mas2_variante_A`, `v11_extremo_variante_A`).

| ID | Qué se ve en el PDF real | Golden | Causa / superficie | Decisión |
|---|---|---|---|---|
| V11-QA-008 | Cabecera «VARIANTE A» y «P1 - Personaje / …»; retrato gris genérico | El golden tiene el mismo retrato gris | Etiquetas de prueba; dibujo del marco de retrato | Etiquetas: se quedan mientras sea debug y salen en la versión final. Retrato: pedido del propietario contra el golden; se le muestran opciones |
| V11-QA-009 | «Guerrero 5» sin subclase | — | La App **sí guarda** la subclase (`subclassName`: Champion, Evoker, Cartógrafa de Paradojas); el renderer no la imprime | Arreglo técnico: «Guerrero (Champion) 5» |
| V11-QA-010 | — | — | Función nueva (números variables en tinta clara) | Pregunta al propietario: ahora o después |
| V11-QA-011 | EQUIPO: 7 líneas para 6 objetos porque «Mochila de explorador» no cabe y sigue con sangría en la línea 2 (correcto). EQUIPO ESPECIAL: filas sin objeto llevan rótulo (Cabeza, Rostro…) y altura de banda completa | El golden rotula las filas libres (…Pecho, Piernas, Libre) con la misma altura | Pedido del propietario contra el golden (C456) | Pregunta al propietario con imagen |
| V11-QA-012 | RASGOS en P1, P2 y continuación: la línea siguiente de un rasgo empieza sin sangría | EQUIPO usa sangría de continuación | Dibujo de RASGOS | Arreglo técnico: misma sangría que EQUIPO |
| V11-QA-013 | Pág. 4: ACCIONES con filas de notas en otro tono; RECURSOS con una franja por cada línea de texto pequeño y gris, mucho espacio libre | Sin golden para este caso | Dibujo de ACCIONES/RECURSOS en C2 | A criterio del Worker: una franja por acción/recurso, texto de descripción más grande, menos espacio perdido |
| V11-QA-014 | — | — | Información (A con Libro, 9 páginas) | Sin cambio |
| V11-QA-015 | Casillas de N1–N9 con separación distinta según la cantidad; orden N1 N2 N3 por filas | El golden ordena por filas | Pedido explícito del propietario, que prevalece sobre el golden | Arreglo: casillas alineadas a la izquierda con paso fijo; orden por columnas 1 4 7 / 2 5 8 / 3 6 9 |
| V11-QA-016 | Ilyra P2, EQUIPO ESPECIAL «Spellbook»: 3 líneas de descripción ocupan 2 bandas y queda una línea vacía debajo | — | La fila crece de banda en banda en vez de a la altura que necesita (contrato §157) | Arreglo técnico: la fila mide lo que su texto necesita |
| V11-QA-017 | Mara pág. 5: EQUIPO en continuación pone cada objeto en una celda alta, sin sangría | P2 usa una fila por línea con sangría | Dibujo de EQUIPO en C2 | Arreglo técnico: misma forma que P2 |
| V11-QA-018 | — | — | La casilla «Habilitar niveles épicos» existe en el editor de cada clase, junto a los dados de golpe | Indicarlo al propietario; probar en el próximo QA |
