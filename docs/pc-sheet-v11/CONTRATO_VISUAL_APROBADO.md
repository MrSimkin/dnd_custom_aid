# Contrato visual V11 — APROBADO por el propietario (2026-10-09)

**Estado:** APROBACIÓN VISUAL EXPLÍCITA; implementación y QA runtime **NO** aprobadas ni ejecutadas.
**Autoridad:** revisión iterativa V8→V9→V10→V11 por el propietario, última respuesta "la aprobación visual de la maqueta está dada ahora". Este contrato especifica el resultado aprobado, no declara que Kotlin lo genere.
**Referencia:** ZIP `dnd_custom_aid_V11_Carta_revision.zip` SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`. ZIP **incorporado a esta rama** en `docs/pc-sheet-v11/` y verificado desde GitHub Actions (run 38009729554; SHA-256, 20 entradas y 8 PDFs). Consultar `ARTEFACTOS_Y_PROCEDENCIA.md`.
**Jerarquía:** los acuerdos V11 posteriores del propietario prevalecen únicamente en los puntos incompatibles con el antiguo D-0076 y su brief. D-0076 conserva íntegra su historia; no reescribirla. Esta V11 representa una **opción nueva de exportación** sin alterar familias legacy.

## Decisiones globales congeladas

- **Carta** 612 × 792 pt; apariencia de hoja física de fantasía, blanco/azul niebla, no beige; marco doble exterior C2 y cartelas interiores C2c; márgenes imprimibles; ningún elemento cortado.
- Títulos/etiquetas: *reemplazado el 2026-10-29 por las fuentes exactas del golden (ver «fuentes del golden»); texto original:* Barlow Condensed auténtica. Valores: Fira Sans como **elección técnica provisional**; medir y registrar el resultado sin inventar una aprobación estética no dada a la fuente de valores. Estados: Para Hoja de PJ Symbols v8 como prioridad, Font Awesome Free secundaria cuando corresponda. No distribuir archivos de fuentes fuera del programa.
- Logo D&D recuperado de la hoja propia del propietario; proyecto **100 % personal**. Cabecera **una misma línea con logo + nombre PJ** en toda página. No repetir nombre en P1.
- **Dos variantes seleccionables A/B** al exportar. El usuario no debe elegir una nueva disposición cada vez que hay overflow; la composición es automática y determinista.
- Deben existir renglones de escritura manual bajo texto impreso y en filas vacías; el texto se alinea al renglón, no lo pisa. Evitar huecos arbitrarios. Nunca descartar datos para ahorrar páginas.
- Ajustes menores finales pendientes de aplicar al V11 aprobado: etiqueta **SALVACIÓN ligeramente más arriba y a la derecha**; rellenar todos los huecos C2 utilizables con sección manuscrita **NOTAS** y cuadrícula completa, sin forzar páginas extras.

## P1 — Personaje/combatientes

- Cabecera compacta, retrato con alternativas Crop/Fit y silueta si no existe, identidad, clase/nivel, raza/alineamiento.
- Seis atributos estándares + personalizables; nota rectangular y modificador ovalado solapado, tamaño V11 aprobado. Salvaciones visualmente ancladas. En variante A habilidades bajo su atributo **sin** sufijo; habilidad separada de su atributo lleva **(XXX)** abreviatura de tres mayúsculas.
- Ataques en tres columnas; bonificador deliberadamente angosto; **Rasgos y Atributos es SOLO una lista identificativa de rasgos mecánicos: nombre + tipo/categoría + fuente/origen real, nunca descripción ni notas explicativas**. Conservar el orden manual del origen y no truncar nombres largos.
- Lanzamiento de Conjuros P1 siempre presente aun en no lanzadores: 1–9 y 10–12 **solo** si reglas épicas reales. No inventar casillas numéricas. Municiones hasta 20 marcas agrupadas de cinco; para cantidad mayor formato `____ / X`. Sin datos, reserva útil.

## P2 — Inventario y rasgos

- Equipo ordinario con dos subcolumnas internas objeto/cantidad; Tesoro justo debajo en parejas denominación/cantidad, valores cero reales no se borran.
- Rasgos mecánicos en **dos columnas**, aprovechan verticalmente P2; siguen a C2 si no caben. **Nunca continúan a P3**. Es continuación de las entradas identificativas pendientes de P1: **nombre + categoría + fuente real**, sin descripción, notas ni fichas explicativas; no reimprimir las ya mostradas.
- Equipo Especial ancho físico completo: ubicación/nombre/descripción/ACTIVO; estados **reales**, sin inferencias automáticas. Misma gramática con y sin datos, filas utilizables.

## P3 — Solo Trasfondo/Perfil/Historia

- Página narrativa completa, sin módulos mecánicos de Rasgos.
- Trasfondo deja superficie manuscrita suficiente. Historia y otras narrativas mantienen párrafos consecutivos sin intercalar renglones innecesarios; todo impreso alineado con la gramática rayada y con continuación cuando el texto crece.

## Lista rápida de Conjuros

- Página presente **únicamente** si existen conjuros reales. **Tres columnas** a la anchura completa disponible y repartición dinámica proporcional al contenido y la zona libre, no grandes bloques blancos.
- Siempre mostrar Trucos 0 + niveles 1–9 si hay página de conjuros (incluso niveles sin conjuros). Fuentes/preparación/gasto separados conforme a estados reales; no literalidades CL/PREP inventadas.
- Todas las filas manuales vacías incluyen casilla de preparación. No inventar slots o progresiones de ejemplo. Continuaciones por nivel si desborda.

## C2 — Extensiones compartidas

- Dos columnas preferidas, mixto cuando se requiera; **nunca una columna vertical gigante por defecto**.
- Atributos personalizados y sus habilidades conservan relación visual; sufijo (XXX) solo cuando no existe atributo junto a la habilidad.
- Equipo ordinario conserva subcolumnas interiores; Equipo Especial ancho completo incluso en C2.
- Agrupar módulos diferentes de manera compacta según contenido real; referencias bidireccionales a **páginas físicas finales**. Las continuaciones de Rasgos se mantienen **solo identificativas**, sin descripción/efectos/notas explicativas, incluso en C2.
- Todo hueco físicamente apto sin módulo útil se rellena con una **pequeña cuadrícula completa titulada NOTAS** (anotaciones manuscritas incidentales). No generar una página C2 adicional únicamente para rellenar ni insertar renglones sin sentido.
- Estas cuadrículas de relleno **no son datos digitales Notas C-N3**: los datos digitales van en páginas exclusivas.

## Notas, Libro opcional y última página

- Páginas C-N3 exclusivas solo si existen Notas digitales generales o tituladas; nunca mezclarlas con C2.
- Libro de Conjuros es **único anexo opcional**, en dos columnas, fichas de altura variable nombre/nivel/escuela/descripción íntegra. Ficha completa pasa a siguiente columna cuando allí cabe; ficha mayor que una columna se parte legiblemente con continuidad (el Python V11 *NO resuelve* este caso).
- **Última página física obligatoria y enteramente manuscrita**: un solo título NOTAS, renglones en dos columnas arriba y cuadrícula entera abajo, sin última fila recortada; nunca usada para contenido digital (2026-10-30: lleva la línea de pie del golden y ningún número de página; las demás páginas sí llevan su número; ver aclaración de esa fecha).


## Aclaración del propietario — presentación de Rasgos (2026-10-16)

**Fuente:** indicación expresa posterior del propietario: «los rasgos no van enteros, todo el texto sino solo nombre y origen», ejemplo `Pericia - Rasgo de Clase: Picaro`. Este requisito **ya existía expresamente** en `docs/decisions/D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md` §3.9 (sin descripciones ni siquiera en extensiones); la indicación actual precisa cómo identificar nombre, categoría y fuente. La ficha digital puede y debe **conservar** descripción y notas, pero la **hoja PDF V11 no las imprime dentro de Rasgos**. No confundir con Equipo Especial, cuyo campo «Descripción» SÍ se imprime, ni con «Rasgos de personalidad», narrativos de P3.

- Ejemplo del propietario: `Pericia - Rasgo de Clase: Pícaro` = **Nombre** (`Pericia`) + **tipo/categoría** (`Rasgo de Clase`) + **fuente** (`Pícaro`), cuando los tres campos están disponibles en la App. Se extraen de los campos de verdad: no deducir «Pícaro» de nombre/clase actual ni inventar fuentes/categorías; si algún componente no consta, omitir SOLO ese componente, sin fabricar `null`, puntuación huérfana ni perder el nombre. No fijar ese ejemplo como único tipo admitido: hay rasgos de raza, trasfondo, dote, don, homebrew y «Otro».
- Usar esta forma identificativa **en P1, P2 y C2** sin excepciones de extensión. Todo ID de rasgo real debe aparecer **una sola vez** como entrada en el recorrido P1→P2→C2, salvo referencias de navegación deliberadas; respetar orden del usuario, cubrir nombres largos con envoltura/continuación legible.
- **Prohibido** volcar `description` o `notes` de un rasgo en P1/P2/C2; prohibido escamotear IDs para simular ahorro de espacio. El compositor debe usar el resumen correcto desde el origen, no limitarse a recortar un párrafo luego de dibujarlo.
- El módulo «Rasgos y Atributos» de P1/P2/C2 y los «Rasgos de personalidad» narrativos de P3 tienen semántica distinta. V11 **prevalece** frente a los pasajes antiguos de D-0076 que ubicaban Rasgos mecánicos en P3: **P3 exclusivamente narrativa**.
- **Diferencia ya detectada (pendiente, no FIXED):** a HEAD `dab9556f`, el renderer V11 Desktop construía `trait.name + " (" + trait.source + "): " + trait.description + notes` en P2/C2 y P1 solo imprimía `trait.name`; por tanto **incumple** origen/tipo y prohibición de descripción. Verificación requerida sobre Android y Desktop con fixture real y descripción centinela que NO debe aparecer en PDF. No declarar cumplimiento por esta corrección documental.

## Compuerta de cumplimiento **ANTES del QA del propietario**

El inventario verificable de reglas vive en `qa/MATRIZ_CONTRATO_V11_PRE_QA.json`, con política en `qa/G4_COMPUERTA_CUMPLIMIENTO_PRE_QA.md`. La matriz se deriva de **este contrato V11**, decisiones históricas aún compatibles de D-0076, mapeos semánticos y aclaraciones posteriores; toda regla obligatoria tiene ID, fuentes, criterio, prueba, escenarios y evidencia de PDF real. `scripts/check_v11_pre_qa_gate.py` verifica integridad del inventario y bloquea el estado `READY_FOR_OWNER_QA` si falta una sola regla en PASS demostrado por el **mismo candidato**, con revisión independiente y QA de familias legadas. **Regla registrada ≠ regla corregida ≠ regla verificada.**

## Criterios para no repetir la revisión visual

El propietario aprobó P1/P3/P4, P2, C2, final y variante B tras revisión V11. Los dos microajustes mencionados son obligaciones técnicas, no convocatoria a V12. Un error medido frente al golden podrá corregirse dentro del gate y límites fijados, pero nunca reabrir el diseño por preferencias del Worker.

**STOP obligado:** si la nueva fuente o datos verdaderos obligan a cambiar semántica o estructura y no hay equivalencia con el contrato; elevar **una sola** decisión material respaldada por evidencia, no interrogatorios en cadena.

## Aclaración del propietario — fuentes de conjuro en P1 y P4 (2026-10-26)

**Dicho por el propietario (2026-10-26):**

- La fuente de lanzamiento de cada fila de la Lista rápida de Conjuros se rotula por su **TIPO DE ORIGEN, sin nombres específicos**, salvo la **Clase**, que se rotula con el nombre de la clase **sin subclase** (por ejemplo `Mago`, `Clérigo`). Los demás tipos son, por ejemplo, `Rasgo`, `Dote`, `Raza`, `Otro`. El propietario no recordaba la lista completa. Esta regla reemplaza la impresión de nombres completos como `Sage — Magic Initiate (Wizard)` (regla `P411`).
- **«SEGÚN FUENTE» significa mostrar el valor de cada fuente**, por ejemplo CD = `Mago 15 / Hechicero 17`; lo mismo para Aptitud y Ataque mágico. Se aplica en P1 y P4.
- Dos fuentes del mismo tipo pueden verse con el mismo rótulo (respuesta afirmativa del propietario); cada fila conserva su marca de preparación. *Reemplazado el 2026-10-28: ver la aclaración de esa fecha (una fila por fuente, sin marca impresa).*
- Cantidad de casillas de espacios por nivel en P1: **depende de la hoja del PJ, mínimo 0, máximo 4**. No hay personajes con 7 o más espacios por nivel.

**Supuestos del Worker (respondidos por el propietario el 2026-10-28):** lista de tipos CONFIRMADA; fuente borrada = `Otro` CONFIRMADO; el aviso `SEGÚN FUENTE` pasa a ser el último recurso (ver aclaración 2026-10-28).

- La lista completa de tipos se toma del editor de fuentes de la App (D-0058, «Tipo de origen»): Clase, Rasgo, Raza, Trasfondo, Dote, Objeto, Objeto mágico, Don, Otro.
- Una asociación cuya fuente ya no existe (sin tipo guardado) se rotula `Otro`.
- Si el texto por fuente no cabe en la tarjeta (más de tres fuentes en P4 o 81 pt en P1) se conserva el aviso `SEGÚN FUENTE` en lugar de truncar.

## Aclaraciones del propietario — respuestas una a una (2026-10-28)

**Regla general (dicho por el propietario):** «diseño debe llegar a ser igual (fuente, color, distribución, maquetación) al original … está en el contrato». Todo lo añadido usa la tipografía y los colores del golden. Por esta regla quedan resueltos: el tamaño de letra del golden en toda la hoja, dos filas de 10 casillas por munición, los rótulos de ATAQUES del golden, el español con la gramática del golden en C2 (casilla con P/E; Acción, Acción adicional, Reacción) y los detalles técnicos del Worker ajustados al golden donde este los muestra.

- **P1 espacios de conjuro:** «B, pero sin marcas». Casillas vacías, una por espacio real, sin X; al lado, un número pequeño «gastados/total» (Mara N1: `2/4`).
- **P1 tarjetas de magia por fuente:** «B y C en ese orden y mezclando». Primero una línea con letra hasta 6 pt; si no cabe, dos renglones (también con letra menor); solo si aun así no cabe, `SEGÚN FUENTE`. P4 conserva siempre el detalle.
- **P1 INSPIRACIÓN:** «A». Tarjeta INSPIRACIÓN como el golden, con casilla marcada según el valor real de la ficha; los PV temporales van en pequeño dentro de PV ACTUALES (`TEMP 7`).
- **P1 VELOCIDAD:** «D». Pies y metros a partir del valor guardado en pies, 5 pies = 1,5 m (`35 pies · 10,5 m`).
- **P3 aviso de continuación:** recomendación del Worker aceptada «mientras respetes el contrato gráfico»: el nombre de cada campo que continúa (`IDEALES A P. 4 · HISTORIA A P. 5`). (2026-10-30, C341: la forma «A P. n» se sustituye por «IDEALES CONTINÚA EN PÁG. 4 · HISTORIA CONTINÚA EN PÁG. 5»; ver «Avisos de continuación»).
- **P4 tipos de fuente:** lista confirmada (Clase con su nombre sin subclase, Rasgo, Raza, Trasfondo, Dote, Objeto, Objeto mágico, Don, Otro). Fuente borrada: `Otro`.
- **P4 preparación por fuente:** «el hechizo aparece dos veces, una para cada fuente como: Fire bolt Mago / Fire bolt Otro», y «no necesita nunca el [X]». Una fila por cada par conjuro-fuente, juntas dentro de su nivel, con casilla vacía para lápiz y solo el tipo de fuente; la preparación no se imprime. Reemplaza «cada fila conserva su marca de preparación».
- **C2 recursos:** «A». Casillas vacías, una por unidad del total, y el número pequeño «gastados/total» (Éter: `2/3`); debajo, recuperación, fuente y notas en letra pequeña. La tabla ACCIONES usa la gramática de ATAQUES del golden.

## Aclaración del propietario — escuela en las fichas del Libro (2026-10-29)

- **Q14:** «Sí, con escuela». **Q14b:** «aun no hemos importado el SRD … estará en el SRD, debe usar A».
- **Regla:** la ficha del Libro muestra, bajo el nombre, la escuela real que guarda la App (`CharacterSpell.school`), como el golden: mayúsculas, condensada negrita 7 pt, gris #657d90, en la franja del encabezado. Si el conjuro no tiene escuela, no se imprime ninguna línea, ni se deduce de la clase, la fuente, el nombre o la descripción. Esto amplía la ficha esencial BK06 (nombre, nivel, escuela si existe, descripción íntegra).

## Aclaración del propietario — fuentes del golden (2026-10-29)

- «se agregan las fuentes». El PDF V11 usa las fuentes exactas del golden: Noto Sans Display Condensed Bold para rótulos, Noto Sans Regular/Bold para texto y EB Garamond 12 Bold para nombres y títulos (letras que le faltan: EB Garamond Bold del mismo proyecto). Reemplazan a Barlow Condensed y Fira Sans.

## Aclaraciones del propietario — cabecera, estados v8 y última hoja NOTAS (2026-10-30)

- **Logo:** «El golden usa ese texto porque no pudo insertar un logo de D&D, y yo quería uno, puedes usar ese o uno más pequeño si quieres y que quede mejor». El texto «DUNGEONS & DRAGONS» del golden es un sustituto: la cabecera usa el logo del propietario, dentro de los marcos (x57–147 × y719,5–754,5).
- **Escudo:** «el "escudo" que aparece en el golden es parte del "logo" que el renderizador inventó como reemplazo, así que puede ser eliminado para dar espacio al logo». La cabecera no lleva escudo.
- **Cabecera:** «Te autorizo a ajustar el encabezado de las hojas de PJ a tu criterio, con control mío para ajuste conservando el espíritu y concepto del golden». Criterio aplicado y sujeto al control del propietario: línea de cabecera en y717; segunda línea derecha con el título de la página.
- **Estados:** las marcas de estado reales usan Para Hoja de PJ Symbols v8 por sus alias de `GUIDE.md`: competencia ✓ U+E108, pericia ✓✓ U+E109, inspiración ★ U+E10E, ACTIVO ◆ U+E10F (solo con un dato real de la App). Las casillas vacías siguen siendo vectores del golden.
- **Última hoja NOTAS:** «permite la línea, evita/elimina el número de página». La hoja final lleva la línea azul de pie del golden (#527c96, 0,55 pt, x39,69–572,32, y45,07), igual que todas las páginas, y **no** lleva número de página ni ningún otro texto de pie. Esto precisa la regla «nunca usada para contenido digital» de esa hoja.
- ~~**Ninguna página lleva número de página**~~ (C339, **revertido** por el propietario el mismo día; se conserva como historial).
- **Número de página, solo el número (vigente, C340):** «me equivoqué, debí mantener los números de páginas para que las referencias de "viene de pág X" o "continúa en pág Y" tengan sentido. Reversa esa decisión, reincorpora los números de página, pero solo número, de una forma estéticamente atractiva y en línea con el contrato». Cada página física excepto la última hoja NOTAS imprime su número sin ceros a la izquierda y sin «/ total» («1», «2» … «10»), en el lugar del contador del golden: alineado a la derecha en x569,48, línea base y32,6, bajo la línea de pie, tinta del golden #264b68, fuente de cabecera a 8,2 pt. Es el mismo número que usan los avisos «CONTINÚA EN PÁG. n» / «VIENE DE PÁG. n» (C341) y el índice del Libro «PÁGINA n»; nunca se imprime «NN / NN».
- **Avisos de continuación (vigente, C341):** «debería decir más claramente "CONTINÚA EN PÁG. X" y "VIENE DE PÁG. Y"», aplicado «a todas» las continuaciones. Donde un contenido no cabe y sigue en otra hoja, esa hoja dice **«CONTINÚA EN PÁG. X»**; donde llega, **«VIENE DE PÁG. Y»**. Reemplaza las formas abreviadas anteriores («A P. n», «VIENE P. n», «SIG. n», «ANT. n», «CONTINÚA EN C2 PÁG. n», «VIENE DE P2 PÁG. n», «CONTINÚA: …»). En concreto:
  - pie derecho de cada página: «CONTINÚA EN PÁG. X» y/o «VIENE DE PÁG. Y»; si llega de varias páginas, «VIENE DE PÁG. 2 Y 4»;
  - banda de módulos de P1, P2 y P3: «RASGOS CONTINÚA EN PÁG. 5 · EQUIPO CONTINÚA EN PÁG. 6»; los módulos que siguen en la misma página se agrupan («PERSONAJE, HABILIDADES, ATAQUES, RECURSOS Y MUNICIONES CONTINÚAN EN PÁG. 4»);
  - cartelas de C2 que retoman un módulo de P1 o P2: «VIENE DE PÁG. n» antes del rombo derecho;
  - rótulo superior de una columna que retoma un grupo, atributo, equipo especial o nivel de conjuros: «IDENTIDAD / VIENE DE PÁG. n» si viene de la hoja anterior, o «IDENTIDAD / CONTINUACIÓN» si viene de la columna de al lado en la misma hoja (como el «TRUCOS 0 / CONTINUACIÓN» del golden);
  - cabecera de una nota C-N3 que sigue: «… / Nota 1 — Hipótesis / VIENE DE PÁG. 9».
  El índice del Libro («PÁGINA n») no es una continuación y no cambia. Los títulos de sección que dicen «/ CONTINUACIÓN» siguen como en el golden.

## Aclaración del propietario — Equipo y Tesoro con nombres largos (2026-10-26)

**Dicho por el propietario (2026-10-26),** tras ver la página 2 de Mara vacía y una vista previa del arreglo:

- La página 2 se llena **primero**: un objeto o una moneda con nombre largo **no se manda entero a C2**; se escribe en la página 2 partido en varias líneas, en el orden original. Solo el sobrante continúa en C2. Aprobado («apruebo el arreglo de esta forma»).
- Cuando un objeto ocupa varias líneas, **las líneas siguientes a la primera llevan una pequeña sangría**, para que se note que ese bloque es un mismo texto. La cantidad se escribe una sola vez, en la primera línea.
- Se aplica a las tablas pareadas de Equipo y Tesoro en P2 y en C2.

**Detalles técnicos del Worker (no dichos por el propietario):** sangría de 8 pt; las líneas se cortan a la anchura con sangría para que ninguna invada la columna de cantidad; siguen yendo a C2 solo los nombres con saltos de línea guardados o las cantidades más anchas que su casilla. Equipo Especial no cambia en esta aclaración (sigue con su límite de dos líneas por celda; es un defecto aparte pendiente). *Resuelto en C233; ver la sección siguiente.*

### Aclaración del propietario — Historia de P3 con el ritmo del golden (2026-10-26)

**Fuente:** el propietario vio tres vistas previas con la fuente real y la historia real de Mara (A actual 9/12,4 pt; B golden 7/17 pt; C 9/17 pt) y respondió: «Opción B: usa 7 pt sobre renglones de 17 pt como el diseño aprobado en Historia».

**Regla:** «HISTORIA Y CRÓNICA» en P3, y su continuación, se escriben en letra de 7 pt sobre renglones de 17 pt, como el golden aprobado. Los campos de perfil (Trasfondo, Religión/Fe, Rasgos de personalidad, Ideales, Vínculos, Defectos) no cambian en esta aclaración.

**Detalles técnicos del Worker (no dichos por el propietario):**
- Fira Sans Regular; línea base 4 pt sobre la regla al pie de cada renglón (medido en el golden: el texto queda justo sobre la regla).
- La continuación de Historia va en páginas propias con el mismo ritmo, después de cualquier continuación de campos de perfil.
- Una historia muy larga ocupa una página más que antes (capacidad de P3 ~3.100 → ~2.900 caracteres).
- Si el resto del cuerpo de texto debe pasar también a 7 pt, se decidirá aparte.

### Aplicación de D-0076 §3.21 — Equipo Especial con filas de mayor altura (2026-10-26)

**Fuente (propietario, D-0076 §3.21, 2026-10-08):** mostrar primero las entradas reales; «seguir el ritmo general de filas… cuando la entrada quepa, y admitir mayor altura cuando el nombre/descripción requiera varias líneas»; no truncar. No es una decisión nueva: corrige una implementación que mandaba todo a C2 cuando un objeto necesitaba más de dos líneas.

**Regla:** en P2, cada objeto especial real ocupa la altura que necesita, en orden de origen. Desde el primero que ya no cabe en P2, ese objeto y los siguientes continúan completos en C2. Bajo los objetos quedan filas libres de altura normal. Cada fila (objeto o libre) tiene su casilla ACTIVO.

**Detalles técnicos del Worker (no dichos por el propietario):**
- Pauta de 25 pt: un objeto ocupa pautas completas (1 pauta = 2 líneas; las bandas más altas dejan 4 pt al pie) para que las filas libres sigan alineadas.
- Casilla ACTIVO centrada en la banda.
- Sombreado alterno por banda.
- Sin objetos especiales, la página no cambia.