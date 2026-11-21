# QA de la candidata 0.7.0-preqa.6

Esta candidata junta **todo lo hecho desde `0.7.0-preqa.5`**: los seis trabajos del 18-11-2026, los nueve que elegiste esa noche (A, C, D, E, H, J, K, M, N) y los nueve del 20-11-2026 (A, B, C, D, G, H, I, J, K). Nada de `0.7.0-preqa.5` se quitó.

El propietario decidió el 20-11-2026 que **la conversación sobre el diseño visual se hace durante este QA**, no antes.

## Candidata

| Pieza | Identidad | SHA-256 |
| --- | --- | --- |
| APK | `dnd-custom-aid-0.7.0-preqa.6-build-70600.apk` (versionName `0.7.0-preqa.6`, versionCode 70600) | `d8168b43b6736525cce100dd561efad3389a9d2ae7617315fb8ee9395dd21892` |
| Windows | `dnd-custom-aid-windows-0.7.5-preqa.6.zip` (packageVersion 0.7.5) | `bc95d9ed2a85637a50d430a6ba4ab9516297429dc86ad0bd071eb91ce860fcd8` |
| Fuente | rama `feature/dm-ux-redesign`, commit `15c58b6e` | — |
| Worker | `dnd-custom-aid-api` · **sin desplegar todavía** (ver «Lo que falta de tu lado») | — |

- Carpeta local: `D:\DnD_Aid\APKs\QA 0.7.0-preqa.6`.
- La evidencia del recorrido está en `verificacion\tablet` de esa carpeta. Muestra el set privado, así que no entra al repositorio.
- Instalación: como siempre, arrastrando el APK al emulador o al teléfono. No uses ADB.

## Qué cambió desde 0.7.0-preqa.5

### Sincronizar

- **Un solo «Sincronizar»** que hace todo (campañas, personajes y lo preparado) y un **estado en palabras**: «Todo al día. Última sincronización: hace 5 min», o «3 personajes esperando · 1 diferencia por decidir · 2 imágenes sin bajar».
- **Cuando lo mismo cambió aquí y en el servidor**, la App ya no elige por ti: «Diferencias que decidir» muestra las dos versiones lado a lado, resalta los campos distintos y ofrece tres salidas: quedarme con la mía, traer la del servidor, o guardar las dos.
- **Los cambios viajan en grupos de hasta cien** en vez de uno por uno. La primera carga de un paquete pasó de 1083 peticiones a once. (Necesita el Worker desplegado; si no lo está, la App vuelve sola al modo de uno en uno.)

### Preparar (Windows)

- **Lo que preparas se abre como la ficha que lees en la mesa**, con «Editar» por bloque y «Ver todo» para la preparación completa.
- **Se puede borrar**, con **papelera** que lo devuelve con sus enlaces. Ahora también se borra **un dungeon entero con sus zonas**, y la papelera **se puede vaciar**.
- **Copia de seguridad de la campaña** a un archivo, y recuperarla, también en otro computador.
- **«Ver cambios del paquete»** antes de volver a cargarlo, marcando qué traer.
- **Una búsqueda para toda la campaña**, con el origen de cada resultado y un filtro por familia; las reglas de la casa quedan fuera salvo que las pidas.
- **Imprimir**: «Guardar PDF…» deja la ficha de mesa en una hoja, con bloque de combate cuando aplica; **«Imprimir todo…»** guarda la escena o el lugar con sus lugares, sus PNJ y las criaturas de sus encuentros, una ficha por hoja.
- **Las listas vacías enseñan qué hacer** en vez de quedar en blanco.

### En la mesa (tablet y Windows)

- **«🔎 Buscar» es una mesa propia también en la tablet**: busca en toda la campaña, cada resultado dice qué es y dónde vive, y como la tablet no tiene «Preparar», la ficha se lee al lado. Lo que la Mesa de Escena puede abrir tiene «Abrir en la Mesa de Escena».
- **La dificultad del encuentro según tu grupo**: «Mortal para 4 PJ · 3600 PX ajustados», al elegir y mientras lo armas. Dice qué contó, y si las criaturas no tienen VD, lo dice en vez de inventar.
- **«Elegir encuentro»** (de 0.7.0-preqa.5) sigue igual, con la casilla «Cantidad» y las dos columnas ya reparadas.

### Jugadores

- **Invitar con código de seis letras, enlace y QR**, con vencimiento. El enlace y el QR abren una página que explica qué hacer; el QR lo dibuja la App, sin librerías.
- **«Unirme»** en la App del jugador, con el código o con lo que trae el enlace.

### Reglas y contenido

- **Revisión automática del contenido del SRD**, que encontró y reparó al Guerrero de 5e detenido en nivel 11.
- **Tabla pública de qué se hace en cada aparato**, visible en los dos Configuración.
- **El asistente «Nuevo personaje» también en Windows**, con los mismos pasos que la tablet.
- **Las imágenes pueden vivir en R2** en vez de Workers KV, sin un día en que una imagen vieja deje de verse. (Necesita que crees el *bucket*; mientras no exista, todo sigue en KV igual que hoy.)

## Cómo se verificó (AGENTS §6.4)

**Tablet (emulador Lenovo Tab P11, `-read-only -no-snapshot-save`)** — recorrido con el set real, captura por paso en `verificacion\tablet`:

| # | Prueba | Resultado |
| --- | --- | --- |
| T1 | «🔎 Buscar» aparece en la barra y abre su mesa | VISTO FUNCIONANDO |
| T2 | Buscar «mercado» devuelve PNJ y lugares, con «· en …» | VISTO FUNCIONANDO (8 resultados) |
| T3 | Tocar un resultado lo deja leer al lado | VISTO FUNCIONANDO |
| T4 | «Abrir en la Mesa de Escena» abre esa ficha allá | VISTO FUNCIONANDO |
| T5 | «Qué se busca» muestra las familias, con las reglas fuera | VISTO FUNCIONANDO |
| T6 | Un combate nuevo abre «Elegir encuentro» con la dificultad de cada uno | VISTO FUNCIONANDO |
| T7 | Armar un encuentro recalcula la dificultad al agregar criaturas | VISTO FUNCIONANDO (4 × VD 3 = «Fácil para 5 PJ · 5600 PX», correcto con un PJ de nivel 20 en el grupo) |
| T8 | Un encuentro sin criaturas no finge un veredicto | REPARADO EN ESTE RECORRIDO: decía «Trivial»; ahora dice «Sin criaturas todavía: no hay nada que medir» |

**Windows** — **no verificado por el agente**: el propietario pidió no usar su pantalla mientras trabaja. Queda para cuando él lo permita, o para su propio QA. Afecta sobre todo a: Preparar (fichas, papelera, copia de seguridad, paquetes, imprimir), invitaciones y «Diferencias que decidir».

**Lo que el agente no puede verificar nunca** (queda para ti):
- la invitación de verdad a otra persona, con su cuenta;
- la sincronización real contra el servidor (el Worker todavía no tiene lo nuevo);
- las imágenes en R2 (el *bucket* no existe aún).

**Pruebas automáticas:** `:shared:desktopTest`, `:desktopApp:test`, `:androidApp:testDebugUnitTest` y las 79 del backend pasan completas, y CI quedó en verde.

## Lo que falta de tu lado (acciones en el proveedor)

Tres cosas, y ninguna es urgente para probar la App en un solo aparato:

1. **Aplicar la migración `0003`** en Neon (tabla de invitaciones). Pasos en [INVITACIONES.md](INVITACIONES.md).
2. **Desplegar el Worker** desde `D:\DnD_Aid\repo\backend` con `npx wrangler deploy`. Sin esto no existen las invitaciones, el envío en grupos ni la página de unirse; la App lo nota y sigue funcionando como antes.
3. **Opcional: crear el *bucket*** `dnd-custom-aid-media` si quieres mover las imágenes. Pasos en [IMAGENES_FUERA_DE_KV.md](IMAGENES_FUERA_DE_KV.md).

No compartas tokens ni cadenas de conexión: para avisar que quedó listo basta la línea final del despliegue.

## Ventana de QA

Ventana: artefacto **«QA Base DM preqa.6»** — https://claude.ai/artifact/PohRV42o66yqTuQPbwvDjq — 30 pruebas, con el formato acordado:

- marcas **Bien · Bien, pero no lo esperado · En parte · Falla · No aplica**;
- «Comentario» y «Mis comentarios adicionales» en cada prueba;
- comentario general al final, y limpiar marcas por prueba, por sección y de todo.

Secciones: **S** sincronizar · **P** preparar · **T** la tablet en la mesa · **J** jugadores · **R** reglas y contenido · **D** diseño (la conversación sobre el aspecto, que pediste tener aquí).
