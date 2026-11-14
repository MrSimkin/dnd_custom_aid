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

### Verificación inicial en la web (2026-11-10, sin acceso de proveedor)

- **SRD:**
  - D&D Beyond publica el SRD 5.2.1 bajo CC BY 4.0, con una sección de descargas traducidas; no se pudo confirmar desde la búsqueda que incluya el español ([dndbeyond.com/srd](https://dndbeyond.com/srd)).
  - Hay indicios de un SRD 5.1 oficial en español, citado por un módulo comunitario ([foundryvtt.com](https://foundryvtt.com/packages/ravanno-dnd5e-es)).
  - Hay que confirmarlo descargando los archivos antes de construir la etapa D.
- **Cloudflare R2:**
  - Su página de precios describe un cupo gratis mensual ([developers.cloudflare.com](https://developers.cloudflare.com/r2/pricing/index.md)).
  - Varias guías de terceros dicen que pide registrar un medio de pago antes de crear el primer bucket ([tech-insider.org](https://tech-insider.org/how-to-set-up-cloudflare-r2-2026/), [freetier.co](https://freetier.co/directory/products/cloudflare-r2)).
  - Con tarjeta registrada, un exceso puede cobrarse: eso choca con D-0075 si no hay un tope duro.

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
| P4 | ¿Las respuestas de reglas muestran siempre el texto del SRD citado, y se agrega la búsqueda sin conexión? | **«Sí, las dos»**. Cada respuesta muestra el texto exacto del SRD con versión (5e / 5.5e) y sección, con enlace a la otra versión. El SRD se guarda también en el aparato y se busca por palabras sin IA ni red. |
| P5 | ¿Cómo llegan a la tablet los archivos del DM (handouts) y el retrato del PJ? | Tras replantearla con el ejemplo de Bram, el propietario respondió **«Viajan solas»**. Las imágenes (handouts, mapas, retratos de PNJ y de PJ) suben y bajan solas por un servicio de archivos en internet. Antes de activarlo, el Worker hace la revisión de costo USD $0 (D-0075, AGENTS §10): cupo gratis, medio de pago, tope duro o cobro por exceso, región y dependencia. Compara R2 con alternativas sin tarjeta y presenta el resultado. Activar el proveedor es un paso del propietario. |
| P6 | ¿QA parciales en el camino o uno solo al final? | **«Uno solo al final»**. No hay QA parciales del propietario. El Worker verifica cada paquete con pruebas, CI y la App en el emulador, y entrega un único QA «Base para DM» al terminar los 8 paquetes. |
| P7 | ¿Una sola App con modo DM según la campaña, o una App aparte para el DM? | **«Una sola App»**. Una pantalla de entrada para jugadores y DM (cumple V11-QA-006). Luego se elige la campaña, y la App abre la Mesa del DM o la ficha del PJ según el papel en esa campaña. Un mismo usuario puede ser DM en una campaña y jugador en otra. |
| P8 | ¿Las preguntas de detalle de cada mesa, ahora o justo antes de construirla? | **«Todas ahora»**. El diseño de las cuatro mesas se cierra antes de programar (P9 en adelante). |
| P9 | Pantalla del DM · ¿Party Lens queda con estos datos? | **«Sí, así»**. Una fila por dato (CA, PV máximos, Percepción, Perspicacia e Investigación pasivas, velocidad, sentidos, idiomas) y una columna por PJ. El mejor valor de cada fila se destaca. Solo datos fijos de la ficha, sin PV actuales. |
| P10 | Pantalla del DM · ¿La referencia rápida queda con estas 10 fichas? | **«Sí, así»**. Condiciones, cobertura, concentración, CD típicas, luz y visión, caídas y asfixia, agarrar y derribar, ocultarse, descansos y viaje. El texto es del SRD en español, en la versión de la campaña, con un toque para ver la otra; sale del mismo corpus que la consulta de reglas. |
| P11 | Mesa de combate · ¿La App lleva los PV de los PJ? | **«Tambien los PJs, solo como anotación, no obligatoria, para referencia, el control principal se llevará en papel, pero como anotación ayuda»**. Los monstruos llevan sus PV en la App. Cada PJ tiene un campo de PV opcional para anotar; puede quedar vacío y nunca se exige. Se presenta como anotación de referencia, distinta del control de papel, y no cambia la ficha del PJ. |
| P12 | Mesa de combate · ¿La iniciativa de los monstruos la tira la App? | Primero respondió «La escribo yo»; antes de que se registrara, corrigió: **«quiero ambas opciones disponibles»**. Cada monstruo tiene su casilla para escribir la iniciativa y un botón «Tirar por mí» (d20 + DES), por monstruo y para todos a la vez. Lo tirado se puede corregir a mano. Los PJ se escriben. |
| P13 | Mesa de combate · ¿Los PV de cada monstruo parten con el promedio, editable y con «Tirar»? | **«Sí, así»**. Cada monstruo parte con el promedio de su bloque de estadísticas; el DM puede escribir otro número o tocar «Tirar» para que la App tire sus dados de PV. |
| P14 | Mesa de combate · ¿Un combate empieza desde un encuentro preparado, improvisado o de ambas formas? | **«Ambas formas»**. Desde un encuentro preparado (entra entero con un toque) o improvisado: buscando en el bestiario con cantidad, o con un nombre libre y PV y CA a mano. Se pueden sumar criaturas a un combate ya empezado. |
| P15 | Mesa de escena · ¿Queda con la versión recomendada? | Tras pedir la recomendación del Worker: **«Sí, esta»**. Buscador; fijados y recientes; recorrido por zona, por función y por aventura (secuencia de escenas); tiendas con qué venden y cuánto cuesta, lo destacado y quién atiende, sin llevar existencias; referencia al papel («Carpeta B, pág. 17»); ficha rápida de PNJ con «para jugadores» / «solo DM»; camino a la vista. Quedan para después: marcar dónde está el grupo y el PNJ improvisado en dos toques. |
| P16 | Mesa de mazmorra · ¿El turno de mazmorra funciona así? | **«Sí, así»**.<br>• Turno de 10 minutos; cada PJ elige su actividad entre las 15 de la regla beta (Buscar con su foco).<br>• La actividad anterior se ve como referencia y nunca se arrastra.<br>• El avance del grupo (normal, reducido, sin avance) se muestra según la actividad más lenta, solo como dato; la App no calcula el movimiento.<br>• Botones «Cerrar turno», «Separar grupo» (cada subgrupo con su avance) e «Interrumpido por combate» (marcar qué se completó).<br>• El registro por turno se arma solo, y los relojes «+1 por turno» avanzan al cerrar. |
| P17 | Mesa de mazmorra · ¿Se agregan «Exploración normal» y «Ocultarse» a la regla beta? | **«se agregan ambas con * y con posibilidad de ajustar en el escritorio»**. Las dos entran marcadas con (*): Exploración normal (hasta 2 zonas) y Ocultarse (sin avance, prueba de Sigilo). La lista de actividades de los turnos de mazmorra (nombre, costo de avance, beneficio, marca *) es contenido de reglas del DM: se ajusta en el escritorio (gestor de reglas caseras) y viaja a la tablet con el resto del contenido del DM. |
| P18 | Mesa de mazmorra · ¿La ficha de zona queda con secciones y marcas de un toque? | **«Sí, así»**.<br>• Barra lateral fija con Presentación, Espacio, Exploración, Hallazgos, Ayuda DM, Encuentro y Referencias; las secciones vacías se ocultan.<br>• La ficha rica del DM se respeta, sin campos obligatorios.<br>• Cada parte marca «para jugadores» o «solo DM».<br>• Marcas de estado de un toque: Explorada, Saqueada, Despejada, Peligro activo, Bloqueada y Secreto hallado. |
| P19 | Mesa de mazmorra · ¿Los paquetes entran ya en la «Base para DM»? | **«Sí, ya»**, contra la recomendación del Worker de dejarlos para después.<br>• Preparar encuentro incluye cantidad, duplicar, cambiar por otra criatura, reserva y ajustes rápidos temporales (PV, CA, bono de ataque, dados y bono de daño), sin reclamar por números que no cuadren.<br>• Incluye además **paquetes**: piezas reutilizables (acciones, rasgos, salvaciones, conjuros) y combinaciones con nombre que suman, quitan y ajustan. Se arman en el escritorio, viajan a la tablet y se aplican de un toque a una criatura o a todo su grupo.<br>• Todo es reversible, y el bestiario base no cambia. «Empezar combate» hereda el estado preparado. |
| P20 | Mesa de mazmorra · ¿Los relojes quedan así? | **«Sí, así»**. Un reloj tiene nombre y segmentos (4, 6, 8…). Avanza «+1 por turno de mazmorra» o a mano (− / +). Puede pertenecer a una mazmorra, a un lugar o a la campaña. Al llenarse avisa, pero no aplica consecuencias: decide el DM. |
| P21 | Mesa de mazmorra · ¿Las notas en vivo funcionan así? | **«Sí, así»**. Un solo tipo de nota, que se pega sola al contexto donde se escribe (zona, criatura, mazmorra…) y se cambia con un toque. Sin etiquetas ni carpetas obligatorias. Una vista reúne las notas con su contexto. Las notas siempre se guardan, sin «borradores» que se pierdan, y viajan al escritorio para ordenarlas después. |
| P22 | Mesa de mazmorra · ¿Una emboscada exitosa abre el combate con ronda sorpresa? | **«Sí, así»**. Si al cerrar el turno una Emboscada salió bien, la App propone empezar con ronda sorpresa para el grupo. Vienen marcados los PJ que emboscaron, y el DM puede cambiarlo. Los enemigos quedan sorprendidos. Siempre se puede empezar sin sorpresa; el DM confirma. |
| P23 | Mesa de mazmorra · ¿El mapa de zonas con grupo y criaturas puestos a mano queda así? | **«Sí, así»**.<br>• Mapa tipo diagrama, armado en el Gestor de mazmorras del escritorio: zonas unidas por pasajes, incluidos los secretos, sin cuadrícula.<br>• Cada zona muestra su estado, y un toque abre su ficha.<br>• El grupo y las criaturas se ubican a mano, arrastrando o con un toque.<br>• Nada se mueve solo; los relojes solo avisan. |
| P24 | ¿Qué nombres en español usa la App? | Respuesta en una tabla editada por el propietario (ver abajo). |
| P25 | Pantalla de entrada (V11-QA-006) · ¿Se puede usar la App sin cuenta? | **«A · Opcional»**, la recomendada. La entrada ofrece «Recibir código» (correo y código) y «Seguir sin cuenta»; sin cuenta, los PJ quedan solo en ese aparato y se puede entrar después. Con cuenta: «Tus campañas» con la marca DM o Jugador, y la App abre el Escritorio DM o los PJ según el papel (P7). |
| P26 | Referencia rápida · ¿Se descargan los SRD oficiales en español (5.1 y 5.2.1, CC BY 4.0) para sacar el texto? | **«Si, descargalos, pero todo debe ir en la App, usaremos todo lo que esté disponible»**. Los dos SRD completos van dentro de la App (por página, con atribución), no solo las diez fichas; de ahí salen la Referencia rápida y la búsqueda sin conexión. |
| P27 | Archivos e imágenes · ¿Qué servicio guarda los handouts, mapas y retratos en internet? | **«Workers KV (Recommended)»**, tras la revisión USD $0 (`docs/technical/DM_MEDIA.md`): misma cuenta gratis de Cloudflare, sin tarjeta, y al llegar al límite falla en vez de cobrar. R2 quedó descartado porque exige tarjeta y cobra el exceso. |
| P28 | Escritorio DM · ¿Cómo se ven las mesas y cómo se cambia de una a otra? (revisión de experiencia de uso, 2026-11-11, tras ver que la primera versión no seguía D-0068/D-0069) | **«Barra lateral con vista general y tambien barra arriba (como opciones en menu de configuracion)»**. Por defecto, una barra lateral fija con todas las mesas (un toque para cambiar) y un primer botón «Escritorio» con la vista general en tarjetas. En Configuración se puede elegir, en su lugar, pestañas arriba. Además respondió «si» a las dos mejoras propuestas: la vista general dentro de la barra, y la posibilidad de abrir una segunda Dungeon. |
| P29 | Escritorio DM · ¿Qué mesas aparecen? | **«Fijas + combates al abrirlos»**. Pantalla del DM, Escena y Dungeon están siempre. Los combates aparecen al crearlos y se cierran al terminar. Con «＋» se puede abrir una segunda Dungeon si el grupo se separa (mejora aceptada). |
| P30 | Escritorio DM · ¿Dos mesas a la vez? | **«Una mesa + paneles deslizantes (recomendada)»**. Se ve una mesa a la vez. Reglas, referencia, la hoja rápida de un PJ, handouts y notas se abren como panel al costado, sin salir de la mesa. |
| P31 | Escritorio DM · ¿Orientación de la tablet? | **«Horizontal (vertical solo de respaldo)»**. El diseño principal es apaisado y a columnas; en vertical las columnas se apilan. En el teléfono, el modo DM queda a criterio del Worker. |
| P32 | Mesa de combate · Edición de criaturas en la mesa (revisión de propuesta 3) | **«no veo ediciones particulares»** y **«tambien hecho de menos, poder agregar ataques, cambiar stats y cosas así tambien»**. Cada criatura se edita por separado de su grupo (nombre en la mesa, nota, ajustes, paquetes) y, además, con un editor completo de la variante en mesa: características, defensas, salvaciones, ataques (agregar, cambiar, quitar), rasgos, acciones, reacciones y conjuros. Amplía D-0068 §13 («no complete creature authoring during live play») por decisión del propietario. Sigue siendo una variante en mesa: el bestiario no cambia, todo es reversible y se puede «Guardar como criatura nueva» para limpiarla en la App de PC (D-0068 §14). |
| P33 | Mesa de combate · Iniciativa por criatura o por grupo (revisión de propuesta 3) | **«me falta en tirar, (en iniciativa), tirar por individual y por grupo, al igual para asignar iniciativas»**. En el paso de iniciativa, cada grupo elige «Una para el grupo» (un solo número tirado o escrito; actúan juntos) o «Cada uno la suya» (cada criatura con su número y su dado, más «tirar a cada uno» y «mismo número» para todo el grupo). «Tirar todas las criaturas» respeta lo elegido en cada grupo. Todo se corrige a mano y los PJ se escriben (P12). |
| P34 | Mesa de combate · Números y dados (aprobación de la propuesta 3) | **«recuerda usar el sistema de numeros y/o dados de PJ, configurable por opciones, pero aprobado y sigue»**. La Mesa de Combate queda **aprobada** (propuesta 3 con P32 y P33). Las tiradas del DM (iniciativa, PV, daño) usan el mismo sistema de dados de la hoja de PJ (mismo motor de tiradas y la misma presentación de resultados: «Dados visibles» o compacto, según Configuración), y la entrada de números usa los mismos campos numéricos de la App, sin un teclado distinto. Lo configurable vive en Configuración, como en la hoja de PJ. |
| P35 | Mesa de escena · Buscar por tipo y mapa de recorrido (revisión de propuesta 4) | **«me falta tambien el buscar por ONJ, tienda, escena, no solo un buscador general y me falta el "mapa de recorrido" para los casos que corresponde»**. El buscador lleva filtros por tipo (Todo, PNJ, Tienda, Lugar, Escena); con un filtro y sin escribir, lista todo lo de ese tipo (propuesta del Worker). Se agrega una cuarta forma de recorrer, «🗺 Mapa», solo en los lugares que tengan mapa: propuesta del Worker, una imagen (handout de tipo Mapa) con zonas y marcas puestas en la App de PC; tocar una marca abre la ficha. **Aprobado** con la Mesa de Escena completa («Aprobado, sigue»). |
| P36 | Mesa de escena · PNJ por idea o contexto (revisión de propuesta 4) | **«me falta el ver PNJ por contexto idea o nombre porque no tengo porque saber quien es quien, la idea era tambien NPCs vinculados a zonas y similares si está configurado asi en la APP de escritorio»**. La búsqueda de PNJ abarca papel, apariencia, lugares, escenas y grupos (no solo el nombre); cada PNJ se muestra con el papel primero, un rasgo para reconocerlo, dónde está y su retrato si lo tiene (propuesta del Worker). PNJ agrupables por zona/lugar, escena, grupo o facción, y papel. Cada ficha de lugar, escena o zona (también en la Mesa de Dungeon) muestra «PNJ aquí». Propuesta del Worker: en la App de PC, vincular PNJ con lugares, zonas, escenas y grupos eligiendo de una lista (hoy son texto libre), sin dejar de buscar en el texto libre. **Aprobado** con la Mesa de Escena completa («Aprobado, sigue»). |
| P37 | Diseño de uso de la tablet · Aprobación final | Las cinco propuestas (Escritorio, Pantalla del DM, Mesa de Combate, Mesa de Escena y Mesa de Dungeon) quedaron **aprobadas** una a una («Aprobado, sigue»). El diseño aprobado está en `docs/technical/DM_UX_APROBADA.md` y manda sobre las pantallas de los paquetes 2, 3 y 5, que se corrigen antes de seguir. |

### Nombres en la App (P24, respuesta literal del propietario)

| Nombre de trabajo | En la App |
|---|---|
| Workspace (el conjunto de mesas) | **Escritorio DM** |
| DM Screen | **Pantalla del DM** |
| Stage Desk | **Mesa de Escena** |
| Dungeon Desk | **Mesa de Dungeon** |
| Combat Desk | **Mesa de Combate** |
| Party Lens | **Vista Grutp**, escrito así; se lee como «Vista Grupo» (error de tipeo evidente) y se usará «Vista Grupo» salvo que el propietario diga otra cosa |
| Zone Brief | **Ficha de Zona** |
| Encounter Readiness | **Preparar encuentro** |
| Quick Rules Question | **Consultar reglas** |
| Dungeon Turn | **Turno de Dungeon** |
| Quick reference | **Referencia rápida** |
| Clocks | **Relojes** |
| Packages | **Paquetes** |

Aviso de claridad para documentos y conversación: «Escritorio DM» es ahora el conjunto de mesas en la tablet. Para no confundirlo, a la aplicación de computador se la llama **«App de PC»** (antes «escritorio», Desktop App).

## 8. Estado del diseño

Diseño de la «Base para DM» cerrado con P1–P24 (2026-11-10). Siguiente: construir el paquete 1, sincronizar el contenido del DM.


## 5. Orden de trabajo tras P1

1. **C0 · Sincronización del contenido del DM:**
   - endpoints y tablas en el servidor;
   - outbox y conflictos en Shared;
   - el escritorio sube su contenido y la tablet lo recibe.
   Usa las mismas reglas que los PJ: revisiones, borrados que no reviven, autoridad del DM de la campaña.
2. **A · Media / Handouts:** según P3.
3. **B · Mesa del DM en la tablet:** según P2.
4. **D · Reglas SRD y consultas:** según P4.

## 6. Consecuencias de P5

- La etapa A suma un **servicio de archivos en internet**. Primero se arma el modelo local de archivos (identidad estable, tipo, tamaño, suma de control, solo DM o apto para jugadores), sin el proveedor; después se conecta el proveedor elegido.
- Si la revisión USD $0 no encuentra un proveedor sin riesgo de cobro, se vuelve a preguntar al propietario antes de activarlo.

## 7. Los 8 paquetes de la «Base para DM» (orden)

1. Sincronizar el contenido del DM (servidor, escritorio, tablet).
2. Modo DM en la App Android para tablet.
3. Pantalla del DM.
4. Archivos e imágenes (biblioteca y servicio de archivos, tras la revisión USD $0).
5. Mesa de combate.
6. Mesa de escena.
7. Mesa de mazmorra.
8. Reglas SRD (IA con cita y búsqueda sin conexión).

QA único del propietario al final (P6).
