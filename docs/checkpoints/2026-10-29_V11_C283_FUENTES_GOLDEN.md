# C283–C285 — Fuentes del golden, campos de P3 y nombre en Garamond (2026-10-29)

PR #173 DRAFT/NO MERGE. Anterior: [C272–C282](2026-10-29_V11_C272_MATRIZ_DIEZ_BLOQUES.md).

**Instrucción del propietario:** «se agregan las fuentes, continua con 10 bloques».

| Bloque | Qué | Base |
|---|---|---|
| C283 | Fuentes exactas del golden en el PDF V11 | Noto Sans Display Condensed Bold (rótulos), Noto Sans Regular/Bold (texto), EB Garamond 12 Bold (nombres). Anchos idénticos a los del golden (error 0,0 pt). Origen: paquetes oficiales de Debian (el golden se generó con esas fuentes del sistema) y el repositorio de EB Garamond. OFL 1.1, SHA-256 en `assets/fonts/pdf/text/README.md`. |
| C284 | Campos de P3 como el golden | Seis campos de cuatro franjas de 17 pt (rótulo + tres renglones), columnas de 263,48 pt; rótulos 7 pt #1d384f, valores 7,3 pt #24394b. Corrige el desborde de P3 que causaba la caja más alta de Noto y el corrimiento de páginas de C2. |
| C285 | Nombre del encabezado en Garamond | EB Garamond 12 Bold 13,4 pt #1d384f en todas las páginas. Las letras que esa fuente no tiene (acentos, eñe, rayas) salen de la EB Garamond Bold actual del mismo proyecto. |

| C286 | Encabezado de página del golden | Sin la franja celeste; escudo con su aspa y rombo, línea divisoria, nombre en x161,57 / 735,31, rótulo del grupo arriba a la derecha (7,3 pt #264b68: VARIANTE A/B, EQUIPO Y TESORO, TRASFONDO E HISTORIA, CONTINUACIONES C2, LISTA RÁPIDA DE CONJUROS, NOTAS C-N3, LIBRO DE CONJUROS, NOTAS) con su esquinero. El título de página de la app ocupa la segunda línea pequeña del golden (5,9 pt #657d90); la hoja NOTAS final lleva solo su rótulo, como el golden. |
| C287 | Nombres del Libro en Garamond | EB Garamond 12 Bold 11,3 pt #1d384f (acentos desde la de respaldo). |
| C288 | Franja de encabezado de la ficha del Libro | Tres renglones (31,5 pt; golden 31,2): nombre a 12,76 pt del borde, escuela 12,75 pt más abajo, como el golden. |
| C289 | Columnas y márgenes del Libro | Texto 48,19–292,82 y 319,18–563,81 pt; marco 8,5 pt por fuera (39,69–301,32, como el golden) y 2,83 pt sobre la franja; franja 2,83 pt dentro del marco. Las fichas no cambian; el Libro de prueba pasa de 27 a 29 páginas por la columna más angosta del golden. |
| C290 | Pie de página y esquinas del golden | Franja de enlaces en y43,94–56,98 con su regla; marca interna en la línea de nota del golden (6,1 pt #657d90); referencias §3.33 en 6,1 pt antes del número; número de página «01 / 11» (7,4 pt); regla del pie; esquineros con rombo en las cuatro esquinas y rombos del marco exterior. La hoja NOTAS final sigue sin pie digital. |

| C291 | Tinta y separadores del golden | Texto de valores en tinta #24394b (antes negro) en toda la hoja, incluida la Historia; enlaces del pie separados con «   ·   » como el golden. |
| C292 | Cartelas y cantidades de P2 | Leyenda de la cartela en Noto Sans 6,2 pt gris #657d90 («OBJETO / CANTIDAD», «denominación / cantidad»); cantidades en Noto Sans Bold #264b68. |
| C293 | Color de las cartelas del golden | Relleno #e7f2f7 en las 17 cartelas y franjas (antes #d9ecf5, que el golden no usa) y borde #264b68 de 0,65 pt en las cartelas de sección. Los marcos de tarjetas siguen en el azul #527c96 del golden. Pendiente: altura de 22,96 pt de la cartela del golden, que exige mover las tablas de P2. |
| C294 | Tipografía de P4 | Encabezado de nivel en rótulo 7,6 pt #264b68; «ESP.» en 5,9 pt; fuente de cada conjuro en rótulo 6 pt #264b68 a 3,4 pt del borde derecho, como el golden. |
| C295 | Cartelas de C2 | Rombos en ambos extremos, título a 16 pt del borde y «VIENE P. n» en 6,1 pt #264b68 antes del rombo derecho (x 270,5, como el golden). Un título demasiado largo conserva el margen de 5 pt sin rombos. |
| C296 | Notas digitales (C-N3) | Cartela NOTAS con rombos, título 9,1 pt y leyenda «solo notas digitales» en gris 6,2 pt; cada nota en tarjeta con esquinas cortadas (#264b68 0,65 pt, x39,69–572,32) y línea interior clara; título de la nota en rótulo 8 pt #1d384f con una regla fina debajo, sin franja celeste. |
| C297 | Rótulos de identidad de P1 | CLASE / NIVEL 6,2 pt y JUGADOR, RAZA, ALINEAMIENTO 5,9 pt en gris #657d90 (antes 7 pt negro). |
| C298 | Sin texto negro | Cada página empieza en la tinta #24394b y las 16 vueltas a negro pasan a esa tinta; en Mara A quedan 18 caracteres negros de ~16 400. |
| C299 | Encabezado de ATAQUES de P1 | ARMA / ATAQUE / CONJURO 6,2 pt; BON. y DAÑO / TIPO 6 pt; #264b68 (antes 7 pt negro). |
| C300 | Números de atributos de P1 | Noto Sans Bold como el golden: puntaje 11 pt #1d384f, modificador 8,7 pt, salvación 7,2 pt y bono de habilidad (A) 7 pt en #264b68. |
| C301 | Valores de ataques (P1 y C2) | Bono en Noto Sans Bold #264b68 y daño 0,4 pt más chico (P1: 7,1 / 6,7 pt), como el golden. |
| C302 | Números de C2 y habilidades B | Tarjetas de atributo de C2 (puntaje, modificador, salvación), bonos de habilidad de C2 y de la variante B en Noto Sans Bold, como el golden. |
| C303 | Equipo especial | Encabezados de columna en rótulo 6,8 pt #264b68 («ACTIVO» 5,5 pt) en P2 y C2; leyenda del golden «ACTIVO: marcar a lápiz» en la cartela. |
| C304 | Etiquetas de LANZAMIENTO DE CONJUROS | 6,65 pt como el golden (antes 6,5). |
| C305 | Columnas de HISTORIA (P3) | Franjas 39,69–302,88 y 309,12–572,32 pt (6,24 pt de separación) y texto a 4 pt dentro (x 43,7), como el golden. |
| C306 | Cartelas de P3 | Borde #264b68, ancho 39,69–572,32, rombos y título 9,1 pt #1d384f a 16 pt del borde (antes 12 pt tinta). |
| C307 | Títulos de C2 | Cartela de tabla pareada con rombos y título 9,1 pt #1d384f a 16 pt (antes 11 pt); «EQUIPO ESPECIAL» de C2 9,1 pt; rótulo «NOTAS» de C2 7,6 pt #1d384f (antes 12 pt). Auditorías C207 y de manuscrito siguen el rótulo de 7,6 pt. |
| C308 | Tarjetas de REFERENCIA MÁGICA (P4) | Cartela x39,69–572,32 y tres tarjetas en x 39,69 + 178,58·i, 174,33 pt de ancho, borde #264b68 de 0,65 pt, como el golden. La altura (44 pt) se mantiene por el caso apilado de varias fuentes; el golden mide 29,77 pt. |
| C309 | Columnas de la lista rápida (P4) | 39,69–207,5, 222,09–389,91 y 404,5–572,31 pt (14,59 pt de separación), como el golden; casillas de espacios y auditoría siguen el borde real de cada columna. |
| C310 | Cartela HABILIDADES de B | Rombos, «HABILIDADES» 9,1 pt #1d384f a 16 pt y la leyenda P/E como nota gris 6,2 pt a la derecha (antes un rótulo de 12 pt). |
| C311 | Línea del encabezado | Color #527c96 y 0,55 pt, de 39,69 a 572,32 como el golden; queda en y717 (el golden en 709,8) porque la cartela de C-N3 ocupa la franja 703–716. |
| C312 | Matriz | GLB04 actualizada a la decisión de fuentes del propietario y en PASS G3; BK03 en PASS G3. CI exacta 40986651188 (b61c92cd). Matriz: 75 de 242. |
| C313 | Cartela NOTAS de C-N3 en su lugar | La cartela pasa de la franja del encabezado (703–716) a la banda del golden (672,66–695,62, borde #264b68, rombos, título 9,1 pt); las notas empiezan debajo, en 670,68, como el golden. La auditoría física revisa las notas bajo la cartela. |
| C314 | Aire de las notas con título | Un renglón más tras el título y el título 6,47 pt más abajo: del título al texto quedan los 16,73 pt del golden y la regla 6,24 pt bajo el título. Las nueve notas largas de Mara pasan a usar dos páginas de C-N3 (la ficha de prueba pasa de 10 a 11 páginas). |
| C316 | Etiquetas de combate de P1 | 6,65 pt como el golden (antes 6,7). |
| C317 | Casilla de cada conjuro (P4) | 8,22 pt con relleno blanco y borde #527c96 de 0,48 pt, como el golden (antes 7 pt). Las 27 casillas del medidor de espacios siguen en 7 pt. |
| C318 | Encabezado de nivel de P4 | 3,4 pt dentro de la columna, como el golden. |
| C319 | Matriz | P201 (distribución de P2) en PASS G3 con la CI exacta 40996100845 (1b4245dc). Matriz: 76 de 242. |
| C320 | P2 con la maqueta del golden | EQUIPO 39,69–298,49 × 460,63–670,68 (13 renglones de 16,15), TESORO 327,97–427,18 (5 de 19,84), RASGOS 309,26–572,32 × 318,33–670,68, ESPECIAL desde 291,4 hasta 60,94; cartelas de 22,96 pt 1,98 pt sobre su tabla, título y leyenda en sus líneas base del golden. Pruebas y auditoría física comparan la geometría del golden con tolerancia. |
| C321 | P3 con la maqueta del golden | Franjas TRASFONDO (672,66–695,62) e HISTORIA (437,95–460,91) de 22,96 pt; los renglones de HISTORIA van de 435,97 a 61,8 (22 por columna). Corrige la franja HISTORIA de C284, que se montaba sobre el último renglón de VÍNCULOS/DEFECTOS. |
| C322 | P4 con la maqueta del golden | Cartela 672,66–695,62, tarjetas 640,91–670,68 (29,77 pt; etiqueta y valor en las líneas base del golden), columnas desde 630,43 hasta 64,92 con renglones de 15,73 pt. Varias fuentes en una tarjeta: máximo dos líneas y, si no, «SEGÚN FUENTE» (la regla aprobada en P1). La franja «continúa nivel» va en el hueco bajo las tarjetas. Ilyra y Mara ganan 6 renglones en blanco (68 y 71). |
| C323 | Columnas del C2 mixto | 39,69–301,04 y 310,96–572,32 (9,92 pt de separación) como el golden; la tabla ACCIONES se mide con ese ancho; la barra lateral de grupo queda dentro del marco interior. Auditoría de manuscrito sigue las columnas del golden (505 habilidades vinculadas con casilla y total). Pendiente: cartelas de 22,96 pt y el rótulo «MÓDULOS COMPARTIDOS · DOS COLUMNAS». |
| C324 | Arranque del C2 mixto | El contenido empieza en la altura de la primera cartela del golden (685,7) y lleva encima el rótulo «MÓDULOS COMPARTIDOS · DOS COLUMNAS» (7,5 pt #264b68 en x39,7, y691,9). Las páginas C2 de tablas y de equipo especial todavía empiezan en 702. |
| C325 | Estados con Para Hoja de PJ Symbols v8 | El propietario notó que V11 no usaba su fuente v8 (aprobada y congelada). Ahora los estados reales usan sus alias semánticos de `GUIDE.md`: competencia U+E108 (una marca), pericia U+E109 (dos marcas apiladas) en las casillas de habilidades de A y de C2, inspiración U+E10E (estrella llena) y ACTIVO U+E10F (rombo lleno). Cada marca se centra en su casilla con los límites reales de los contornos (empiezan en x=0; el xMin de la cabecera glyf está desfasado). Las casillas vacías siguen siendo vectoriales, como en el golden. ACTIVO no tiene campo en la App (active = null), así que el rombo nunca se imprime todavía. Prueba `c325RealStatesUseOwnerParaHojaDePjSymbolsV8GlyphsAB`; la auditoría de manuscrito acepta U+E108/U+E109 dentro de la casilla. |
| C326 | Logo del propietario en la caja del golden | El propietario explicó que el texto «DUNGEONS & DRAGONS» del golden sustituía al logo y autorizó usar su logo, más pequeño si queda mejor. El logo subía hasta el marco exterior (y732–767); ahora ocupa la caja del logotipo del golden: x42–119, y721–751 (77 × 30 pt), bajo la esquina decorada y sobre la línea de cabecera. CI de C325: 41094207619 y 41094207395 (f437bcc4). |
| C327 | Cabecera sin escudo, logo más grande | El propietario autorizó ajustar la cabecera a criterio técnico (con su control) y aclaró que el escudo del golden era parte del logo sustituto. Se quita el escudo; el logo pasa a x57–147 × y719,5–754,5 (90 × 35 pt), libre de la esquina decorada y su rombo. La línea pálida tras el logo, el nombre y los rótulos no cambian. CI de C326: 41095518975 y 41095518880 (77068466). |
| C328 | Línea de la cabecera (decisión) | Con la autorización del propietario sobre la cabecera, la línea se queda en y717 y no baja a y709,8 como en el golden: en la App las franjas de continuación de C2, las tablas de C2 y el Libro empiezan en y703–716, y la línea las cruzaría. El logo (abajo en y719,5) y el título secundario (y724,5) quedan sobre ella. |
| C329 | Habilidades de B con marcas v8 | Como en A, el entrenamiento de B es la marca v8 dentro de la casilla (U+E108 competencia, U+E109 pericia) y ya no el sufijo « P» / « E». La leyenda de la cartela muestra las mismas marcas: «✓ COMPETENCIA  ✓✓ PERICIA» en gris 6,2 pt. Si una habilidad de B se desborda a C2, el texto conserva el dato en palabras (« / Competencia», « / Pericia»). |
| C330 | Filas y títulos de nivel de P4 | Títulos del golden «TRUCOS 0» (antes «TRUCOS / NIVEL 0») y «NIVEL n»; todas las franjas en #e7f2f7 (antes #eef7fb en las filas) con la regla #94b0c1 de 0,32 pt 0,99 pt sobre el pie, como el golden. La auditoría de P4 reconoce «TRUCOS 0» y el color nuevo. La franja de nivel de 20,7 pt del golden sigue pendiente: el colocador compartido usa un paso único de 15,73 pt. |
Evidencia visual: `qa/evidence/c286_c287/` `qa/evidence/c290/` y `qa/evidence/c325_c326/`.

## Hallazgos

- **Defecto del golden:** su EB Garamond 12 Bold no tiene letras acentuadas; el golden imprimió «[hueco]gico» y «Proyectil m». La app no pierde letras (prueba `g3HeaderNameUsesGoldenGaramondAndNeverDropsAccentedLettersAB`).
- **Noto es más alta que Fira** (caja 1,36 em contra 1,2 em): los ritmos medidos del golden se mantienen; un renglón del golden más estrecho que la caja (equipo especial, 7/9 pt) escala la caja al renglón. Las habilidades de B mantienen 9 por columna con paso 10,5 pt.
- **Auditorías:** C207 y la de manuscrito detectaban NOTAS por la altura de la caja de Barlow; ahora usan el rótulo de 12 pt y su línea base (+7 pt hasta la cuadrícula). La de P3 verifica la geometría del golden.

## Pendiente (siguiente)

- P1 con las columnas del golden: atributos 39,69–260,22 y combate 270,43–572,32 (la app usa dos de 258); cartelas a 588,19–611,15.
- C2 de tablas y de equipo especial: el golden los mezcla en columnas con otros módulos; la app usa páginas a todo el ancho. Columnas de ESPECIAL del golden: 107 / 133,2 / 246 / 46,42.
- P4: franja del encabezado de nivel de 20,7 pt del golden (la app usa un renglón de 15,73); exige filas de alto variable en el colocador compartido.
- C-N3: la tarjeta de notas generales (sin título) no tiene referencia en el golden; su primera línea queda junto al borde.
- C2: renglones con franjas alternas en listas de acciones, recursos y habilidades, y el rótulo «MÓDULOS COMPARTIDOS · DOS COLUMNAS» del golden.

- Libro: chaflán de las esquinas del golden y ~3 pt más de aire antes de la descripción.
- La segunda línea del encabezado del golden muestra un descriptor de la ficha («EXTREMO FICTICIO»); la app pone ahí el título de página. Decidir si el propietario quiere otro dato ahí.
- Matriz: registrar evidencia de G3 de estos bloques con la CI exacta.
