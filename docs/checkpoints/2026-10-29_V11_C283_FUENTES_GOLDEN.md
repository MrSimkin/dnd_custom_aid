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
Evidencia visual: `qa/evidence/c286_c287/` y `qa/evidence/c290/`.

## Hallazgos

- **Defecto del golden:** su EB Garamond 12 Bold no tiene letras acentuadas; el golden imprimió «[hueco]gico» y «Proyectil m». La app no pierde letras (prueba `g3HeaderNameUsesGoldenGaramondAndNeverDropsAccentedLettersAB`).
- **Noto es más alta que Fira** (caja 1,36 em contra 1,2 em): los ritmos medidos del golden se mantienen; un renglón del golden más estrecho que la caja (equipo especial, 7/9 pt) escala la caja al renglón. Las habilidades de B mantienen 9 por columna con paso 10,5 pt.
- **Auditorías:** C207 y la de manuscrito detectaban NOTAS por la altura de la caja de Barlow; ahora usan el rótulo de 12 pt y su línea base (+7 pt hasta la cuadrícula). La de P3 verifica la geometría del golden.

## Pendiente (siguiente)

- C-N3: el aire del golden entre título de nota, su regla y el texto (exige un renglón más por nota).
- C2: renglones con franjas alternas en listas de acciones, recursos y habilidades, y el rótulo «MÓDULOS COMPARTIDOS · DOS COLUMNAS» del golden.
- P2: altura de 22,96 pt de las cartelas: entre EQUIPO y TESORO quedan ~3,5 pt, así que exige rehacer las áreas de P2.

- Libro: chaflán de las esquinas del golden y ~3 pt más de aire antes de la descripción.
- La segunda línea del encabezado del golden muestra un descriptor de la ficha («EXTREMO FICTICIO»); la app pone ahí el título de página. Decidir si el propietario quiere otro dato ahí.
- Matriz: registrar evidencia de G3 de estos bloques con la CI exacta.
