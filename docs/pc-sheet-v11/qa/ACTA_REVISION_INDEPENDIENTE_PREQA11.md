# Acta de revisión independiente — candidato V11 `0.5.0-preqa.11`, build 51100

**Resultado en una línea:** mejora clara frente a `preqa.9` (ataques, municiones, títulos de C2, rótulos de Equipo Especial, filas largas y nombre largo ya están bien) y no se pierde ningún dato, pero **todavía no está listo para el QA del propietario**: quedan 2 secciones de D-0076 en NO_CUMPLE, 6 de los 8 golden en NO_COINCIDE y 5 de las 9 reglas pedidas sin demostrar.

**Recuentos:** secciones D-0076: CUMPLE 39 · NO_CUMPLE 2 · NO_DEMOSTRADO 1 · SUSTITUIDA 6. Golden: COINCIDE 0 · DIFERENCIAS_JUSTIFICADAS 2 · NO_COINCIDE 6. QA01–QA08 y GLB12: 4 demostradas con salvedades · 5 no demostradas. Hallazgos nuevos: 13 (0 altos, 3 medios, 10 bajos).

**Fecha:** 2026-11-07. **Commit revisado:** `2eff0e7ced14650dd9d45ed3f541b61c855a3cb4`. Comprobé con `git diff --stat 2eff0e7c HEAD` que los dos commits posteriores (`6f0c62d5`, `a3f44bfe`) solo cambian 5 archivos, todos en `docs/`. No modifiqué nada del repositorio salvo esta acta.

**Abreviaturas:** `A:` es un PDF generado por el APK en el emulador (`android_pdfs\cand11`). `CI:` es un PDF de CI (Desktop) sin el prefijo `V11-INTERNAL-PROOF-`. «p4» es la página física 4.

---

## 1. Las 48 secciones de D-0076

**Recuento: CUMPLE 39 · NO_CUMPLE 2 · NO_DEMOSTRADO 1 · SUSTITUIDA 6.**

Si una parte de la sección quedó sustituida y el resto sigue vigente, juzgo el resto.

| § | Qué pide | Veredicto | Evidencia |
|---|---|---|---|
| 3.1 | Logo y nombre en toda página | CUMPLE | Script propio: 1.464 de 1.464 páginas (102 de Android, 1.362 de CI) con la imagen del logo y el nombre arriba, número correcto en todas menos la última, y la última sin número |
| 3.2 | Piezas de la cabecera de P1 | CUMPLE | `A:Mara-A` p1. Jugador y alineamiento con línea a mano, por decisión 2026-11-06 |
| 3.3 | Retrato: recortar, ajustar, silueta | CUMPLE | `A:Portrait-CROP-A` p1: imagen 600×300 dibujada a 123,1×61,6 y recortada al marco. `A:Portrait-FIT-A` p1: 69,4×34,7. Las dos guardan la proporción 2:1. Silueta en `A:Mara-A` p1. No probé imagen inaccesible (sigue pendiente en §3.3) |
| 3.4 | Clase/nivel, raza con subraza, alineamiento | CUMPLE | `Cronomante del Umbral 10` en `A:Mara-A` p1; multiclase en `A:Stress-A` p1; subraza `Elfo — Alto elfo` en `CI:subrace-A` p1. Raza/alineamiento ≈ 61 % / 39 % medido en la imagen. Salvedad: la subraza solo la vi en CI |
| 3.5 | Variantes A y B | CUMPLE | `A:Mara-A` p1 y `A:Mara-B` p1 |
| 3.6 | 2–3 atributos propios en P1, prioridad de los seis, grupos juntos | CUMPLE, con salvedad | `A:Mara-A` p1: FORTUNA, CORDURA y ÉTER; RENOMBRE en p4. Salvedad: tres habilidades sueltas quedan debajo de RENOMBRE en p4 (hallazgo M3) |
| 3.7 | Combate: 3 filas, 8 campos, dados compactos | CUMPLE | `A:Aldren-A` p1 `5d10`; `A:Stress-A` p1 «CONTINÚA EN PÁG. 4» y p4 con los 16 dados |
| 3.8 | Ataques: tabla de 3 columnas | CUMPLE | `CI:P1-attack-fit-order-A`: QALONG1–4 y QASHORT5 en P1, QASHORT6–10 en p4 como filas de tabla, en orden. `A:Stress-A` p1: nombre largo en dos renglones en su fila; p4 sigue en la tabla |
| 3.9 | Rasgos sin descripción | CUMPLE | Busqué el texto centinela de descripción y notas de rasgo en los 183 PDF: 0. Forma `Nombre - Rasgo de Clase: fuente` |
| 3.10 | Reparto dinámico Ataques/Rasgos | SUSTITUIDA | Contrato 2026-11-03: 5 filas y 7 filas fijas |
| 3.11 | Conjuros en P1 siempre; fila épica condicional | CUMPLE | `A:Aldren-A` p1 sin N10; `A:Aldren-epic-A` p1 con N10–N12 en `____ / ____`, sin casillas |
| 3.12 | Filas, bandas, casillas utilizables, gris | NO_DEMOSTRADO | Sin prueba impresa ni en gris. Bandas y casillas presentes en todas las páginas vistas |
| 3.13 | Azul niebla clásico | CUMPLE | Comparado con el golden página a página |
| 3.14 | Municiones, Tesoro, Otros en evaluación | SUSTITUIDA | Por §3.15 |
| 3.15 | Municiones: hasta 20; más, `____ / X` | CUMPLE | 20 casillas en `A:Aldren-A` p1; `____ / 40` en `A:Stress-A` p1 y `CI:ammunition-over-20-A` p1 |
| 3.16 | Marco C2 y fuentes | SUSTITUIDA | Fuentes del golden (2026-10-29). Solo encontré Noto Sans, Noto Sans Display Condensed Bold, EB Garamond y Para Hoja v8, todas incrustadas |
| 3.17 | Barlow y glifos | SUSTITUIDA | Misma decisión |
| 3.18 | Cartelas C2c, casillas simples | CUMPLE | Todas las páginas vistas |
| 3.19 | P2: Equipo, Rasgos, Especial; sin Otros | CUMPLE | `A:Mara-A` p2, `A:Aldren-A` p2 |
| 3.20 | Tesoro bajo Equipo, monedas propias | CUMPLE | `A:Stress-A` p2 con 10 monedas y p4 con 3 más |
| 3.21 | Especial híbrido, sin truncar | CUMPLE | `A:Mara-A` p2 filas altas con «/ NOTAS:» (decisión 2026-11-06) |
| 3.22 | Casilla ACTIVO sin premarcar | CUMPLE | Vacía en todos; Mara tiene `attuned` y no se marca |
| 3.23 | P3 con Rasgos a la derecha | SUSTITUIDA | «P3 exclusivamente narrativa» (2026-10-16) |
| 3.24 | P4 en tres columnas por nivel | CUMPLE | Pares conjuro-fuente: `A:Ilyra-A` p5 27/27, `A:Mara-A` p7 24/24, `A:Stress-A` p6 27/27 |
| 3.25 | Nivel, espacios y casillas en cabecera | CUMPLE | `A:Mara-A` p7: 4,3,3,3,4,2,2,1,1 casillas y `ESP.4 G.2` |
| 3.26 | Referencia superior; cabecera en una línea | CUMPLE | Una línea en todas las vistas; valor por fuente en `A:Stress-A` p6 |
| 3.27 | Notas y última hoja | CUMPLE | Última hoja igual al golden en los 183 PDF (sin número) |
| 3.28 | C-N3 con prioridad digital | CUMPLE | `A:Mara-B` p8–p9: 9 notas y la general |
| 3.29 | C2 compartidas; contratos por módulo | **NO_CUMPLE** | Mejoró: Equipo y Tesoro ya comparten hoja. Pero Equipo Especial sigue en una hoja casi vacía (`A:Mara-A` p6, `A:Stress-A` p5; M2) y el Equipo en pares pierde el orden al partirse (`A:Stress-A` p4, `A:Mara-B` p4–p5; M1) |
| 3.30 | Ubicación e inclusión de ataques, atributos, habilidades | CUMPLE | C2 tras P3, como el golden. Ataques en tabla de 3 columnas en C2 (`A:Stress-A` p4) |
| 3.31 | Prioridades; Notas aparte | CUMPLE | Sin pérdidas ni duplicados (QA01). C-N3 solo con notas |
| 3.32 | Probar familias y elegir la mejor | **NO_CUMPLE** | La salida muestra una regla fija («Especial al pie si cabe; si no, hoja propia»). El golden usa la mixta con Especial arriba (extremo B p5, comparador p2) y así cabría en la hoja anterior. Ver M2 |
| 3.33 | Referencias de ida y vuelta | CUMPLE | Revisé bandas, pies y cartelas de los 13 PDF de Android: todas apuntan a la página correcta. Detalle: M5 |
| 3.34 | Orden: hoja, C2, Notas, Libro, final | CUMPLE | `A:Mara-B`: P1 P2 P3 C2×3 P4 CN3×2 Libro×2 final |
| 3.35 | Sin conjuros no hay P4 | CUMPLE | `A:Aldren-A` sin P4 |
| 3.36 | P3 con reserva de Rasgos | SUSTITUIDA | Igual que §3.23 |
| 3.37 | Sin Equipo: reserva amplia | CUMPLE | `A:new-character-A` p2: 13×2 filas, como el golden |
| 3.38 | Sin Ataques: reserva | CUMPLE | `A:new-character-A` p1: 5 filas. Detalle M6 |
| 3.39 | Sin Municiones: una entrada | CUMPLE | `A:Mara-A` p1 «Tipo:» con 20 casillas |
| 3.40 | P3 vacía se conserva | CUMPLE | `A:new-character-A` p3 |
| 3.41 | Tesoro sin monedas: celdas | CUMPLE | `A:new-character-A` p2: ceros reales conservados |
| 3.42 | Especial vacío: rótulos mixtos | CUMPLE | `A:new-character-A` p2: los 8 rótulos del golden, igual al golden casi vacío p2. Aplica la precedencia golden > D-0076 (ver M11) |
| 3.43 | P2 toda vacía: reservas | CUMPLE | `A:new-character-A` p2 |
| 3.44 | Sin notas no hay C-N3 | CUMPLE | `A:new-character-A`: 4 páginas, como el golden |
| 3.45 | P3 solo texto | CUMPLE | Única imagen en P3: el logo (`A:Portrait-CROP-A`, `A:Aldren-A`) |
| 3.46 | Libro en dos columnas | CUMPLE | `A:Mara-B` p10–p11, `A:Ilyra-B` p7–p8 |
| 3.47 | Ficha esencial | CUMPLE | Sin tiempo, alcance ni duración. Nombre largo en EB Garamond junto a NIVEL: `CI:book-long-name-continuation-A` p7 |
| 3.48 | Ficha entera a la columna siguiente | CUMPLE | `CI:BK04-whole-next-column-A` p7: «Light» pasa entera a la columna 2 |

---

## 2. Los ocho golden

**Recuento: COINCIDE 0 · DIFERENCIAS_JUSTIFICADAS 2 · NO_COINCIDE 6** (cuatro de ellos por detalles menores).

Comprobé los golden: el ZIP del repositorio tiene el SHA-256 `35a1c6ea…364e6a` que registra `ARTEFACTOS_Y_PROCEDENCIA.md`, y los 8 PDF del scratchpad son byte a byte los del ZIP. Ningún PDF de la App usa los datos del golden: comparo forma y gramática.

**Diferencias que no cuento, porque tienen decisión escrita del propietario:** logo sin escudo; número de página solo y última hoja sin número; «CONTINÚA EN PÁG.» / «VIENE DE PÁG.»; fila de atributo con «TIRADA DE SALVACIÓN»; casillas de P1 y P4 y `gastados/total`; `TEMP 7`, inspiración y `35 pies · 10,5 m`; nombre y alcance en ATAQUES; ataque largo en dos renglones; Historia en C2; sin índice del Libro; rótulo de tipo de fuente en P4; forma `Nombre - Tipo: fuente` de los rasgos (por eso cada rasgo ocupa dos renglones y en P1 caben 6); nombres largos de Equipo partidos con sangría; notas de objeto especial impresas; nombre largo cortado; segunda línea de cabecera con el título de la página.

| Golden | Pares vistos | Veredicto | Lo que queda sin respaldo |
|---|---|---|---|
| `v11_personaje_casi_vacio` (4 p) | `A:new-character-A` p1–p3 y la final | NO_COINCIDE, menor | p1: las filas vacías de ATAQUES llevan filetes verticales; el golden no (M6). p2 y p3 iguales |
| `v11_ordinario_6mas2_variante_A` (7 p) | `A:Ilyra-A` p1, p2, p4, p5; `CI:six-plus-two-A` p1; `A:Mara-B` p8, p10, p12 | NO_COINCIDE, menor | Solo M6 (filas vacías de ATAQUES). P4, Notas, Libro y final con la gramática del golden |
| `v11_ordinario_6mas2_variante_B` (7 p) | `A:Mara-B` p1, `A:new-character-B` p1 y las anteriores | NO_COINCIDE, menor | M6 y M7: falta el rótulo «dos subcolumnas» en HABILIDADES |
| `v11_ordinario_6mas3_variante_A` (7 p) | `A:Mara-A` p1, p2, p3, p7; `A:Mara-B` p8–p12 | DIFERENCIAS_JUSTIFICADAS | Todo con decisión. Mara llena las 5 filas de ATAQUES salvo una, así que M6 casi no se ve |
| `v11_ordinario_6mas3_variante_B` (7 p) | `A:Mara-B` p1 y los mismos | NO_COINCIDE, menor | M7 |
| `v11_extremo_variante_A` (11 p) | `A:Mara-A` p1–p7 lado a lado; `A:Mara-B` p8–p12 | NO_COINCIDE | p4: habilidades sueltas bajo RENOMBRE sin subtítulo (M3); rótulo de columna en la franja de cabecera (M4). p6: una hoja casi vacía con un solo objeto especial, cuando el golden comparte (M2) |
| `v11_extremo_variante_B` (10 p) | `A:Mara-B` p1, p4, p5, p8–p12; golden p4–p5 | NO_COINCIDE | Equipo partido entre p4 y p5 con el orden roto y una fila sola en p4 (M1); Especial solo en p6 (M2); M7 |
| `v11_comparador_C2_extremo` (6 p) | `A:Mara-A` p4–p6, `A:Stress-A` p4–p5, `CI:C2-attack-table-rows-A` p4, `CI:ammo-overflow-once-in-C2-A` p4, `CI:panel-B-skill-overflow-C2-B` p4 | DIFERENCIAS_JUSTIFICADAS | Ataques y municiones ya con la gramática del golden; Tesoro comparte hoja. El comparador muestra disposiciones posibles, no un caso de datos. Lo que lo contradice (Especial aparte) ya lo cuento en los dos extremos |

---

## 3. QA01 a QA08 y GLB12

**Recuento: 4 demostradas con salvedades · 5 no demostradas.**

| Regla | ¿Demostrada? | Por qué |
|---|---|---|
| **QA01** datos íntegros | **Sí, con salvedades** | Tabla de abajo: nada perdido ni duplicado en Aldren, Ilyra, Mara y estrés, A y B. Salvedad: el **orden** de Equipo se rompe cuando la tabla en pares se parte (M1). El PDF no trae IDs: comparo textos |
| **QA02** fuentes, glifos, solapes | **Sí, con salvedades** | PyMuPDF sobre los 183 PDF: 0 fuentes sin incrustar; todas las páginas 612×792; 0 textos que se pisan en la misma línea base (golden: también 0). Letra mínima 5,5 pt. Salvedad: es una medida sobre cajas; un roce leve, como el texto de dos renglones que toca el borde de la tarjeta CD en `A:Stress-A` p1, no cuenta |
| **QA03** Guardar/Compartir real | **No** | Solo tengo los PDF. El acta del implementador dice que Compartir no se completó. No vi la pantalla |
| **QA04** comparación con los 8 golden | **No** | 6 de 8 NO_COINCIDE (sección 2). Ningún PDF de la App tiene 6+2 con los datos del golden; uso `CI:six-plus-two-A` |
| **QA05** V11 no depende de las hojas antiguas | **Sí, con salvedades** | `AndroidPcSheetExportService.generateV11Proof` usa solo el mapeador y el renderizador V11; ni ellos ni `PcSheetV11SemanticContract.kt` importan Fantasy, Custom v1 o v2. Salvedad: solo leí importaciones y esa función. La retirada de las hojas antiguas es para después de la aprobación |
| **QA06** identidad del candidato | **Sí, con salvedades** | SHA-256 del APK `5800093f…240f` (copia y CI, iguales); `versionName 0.5.0-preqa.11`, `versionCode 51100` en el commit; los 13 PDF de Android tienen el SHA del acta. Salvedad: no reconstruí el APK ni consulté CI; los PDF no traen metadatos que los liguen al APK |
| **QA07** 48 secciones conciliadas | **No** | Esta acta: 2 NO_CUMPLE y 1 NO_DEMOSTRADO |
| **QA08** todo en PASS | **No** | La matriz tiene QA01–QA08 y GLB12 en `NOT_VERIFIED` y el control final falla según el checkpoint |
| **GLB12** golden intacto; hojas antiguas sin reparar | **No demostrada del todo** | Golden intacto (SHA). No revisé si se tocaron las hojas antiguas; el checkpoint dice que no reciben trabajo |

### Inventario independiente de QA01

Leí cada ficha JSON y busqué cada dato en el PDF (texto sin espacios y, para los nombres partidos en dos columnas, uniendo cada renglón con el que tiene debajo). Lo dudoso lo miré a mano.

| Dato | Aldren A y B | Ilyra A y B | Mara A y B | Estrés A y B |
|---|---|---|---|---|
| Clases | 1 de 1 | 1 de 1 | 1 de 1 | 16 de 16 (6 en P1, 10 en p4) |
| Rasgos | 8 de 8 | 10 de 10 | 26 de 26 (6 P1, 16 P2, 4 C2) | 8 de 8 |
| Entradas de combate | 4 de 4 (2 ATAQUES, 2 ACCIONES) | 2 de 2 | 8 de 8 (4 con bono en ATAQUES, 4 en ACCIONES) | 7 de 7 (5 en P1, 2 en p4) |
| Objetos (especiales) | 11 de 11 (4) | 6 de 6 (2) | 34 de 34 (5: 4 en P2, 1 en p6) | 51 de 51 (14: 8 en P2, 6 en p5) |
| Munición | Virotes 1 vez, P1 | no tiene | no tiene | Virotes 1 vez, P1, `____ / 40` |
| Monedas | 5 de 5 | 5 de 5 | 5 de 5 | 13 de 13 (10 P2, 3 p4) |
| Recursos | 2 de 2 | 4 de 4 | 10 de 10 | 2 de 2 |
| Atributos / habilidades propias | no tiene | no tiene | 4 y 10 | no tiene |
| Pares conjuro-fuente en P4 | sin conjuros | 27 de 27 | 24 de 24 | 27 de 27 |
| Libro (solo B) | no aplica | 25 de 25 | 24 de 24 | 25 de 25 (también en A) |
| Notas tituladas + general | 0 + 1 | 0 + 1 | 9 + 1 | 0 + 1 |
| Descripción o notas de rasgo impresas | 0 | 0 | 0 | 0 |
| Notas de ataque impresas | 0 | 0 | 0 | 0 |

- **Coincidencias que parecían fallos y no lo son:** «Guerrero», «Humano», «Mago», «High Elf» y «Cronomante del Umbral» salen también en la fuente de los rasgos; «Light» está dentro de «Lightning Bolt»; «Evocation Savant» aparece en la descripción del Spellbook; «QAFILA3» sale en su fila y en «ALCANCE QAFILA3».
- **Solo en App:** no aparecen marcadores, defensas, sentidos, competencias ni el progreso de hito de Mara.
- **Valores que recalculé:** Aldren (FUE +4, salvación +7, CA 18, iniciativa +2, 5d10, 30 pies · 9 m) y Mara (`2/3` de Éter: 1 de 3 disponible). Mara imprime CD 16 por su única fuente aunque la ficha guarda 17; es el cálculo por fuente, como en `preqa.9`.

---

## 4. Cierre de la revisión anterior

Comprobado en los PDF reales. Después leí `HALLAZGOS_REVISION_PREQA9.md` solo para contrastar. **Discrepo en N3 y N8**: dice «corregido» y quedan restos.

| Id | Estado | Evidencia |
|---|---|---|
| N1 ataques en C2 | **Cerrado** | `CI:P1-attack-fit-order-A` p1 y p4: orden de la ficha y filas de tabla. `A:Stress-A` p1 y p4 igual |
| N2 título inventado | **Cerrado** | `HABILIDADES Y ACCIONES` no aparece en ningún PDF; C2 usa `ATRIBUTOS Y HABILIDADES`, `ACCIONES`, `RECURSOS` |
| N3 hojas casi vacías | **Sigue en parte** | Equipo y Tesoro ya comparten (`CI:P2-currency-source-break-A` p4). Equipo Especial sigue solo: `A:Mara-A` p6, `A:Stress-A` p5, `CI:P2-C2-large-amounts-A` p6 (M2) |
| N4 municiones en prosa | **Cerrado** | Ningún «MUNICIÓN», «CANTIDAD» ni «MARCAS:». `CI:ammo-overflow-once-in-C2-A` p4: nombre y casillas |
| N5 / G-e rótulos de Especial | **Cerrado en el PDF** | Los 8 del golden en `A:new-character-A` p2. Ver M11: el propietario no lo confirmó |
| N6 fila larga de P4 | **Cerrado** | `CI:C386-long-spell-names-A` p5 y `A:Stress-A` p6: nombre bajo sí mismo, fuente en su columna |
| N7 Libro con nombre largo | **Cerrado** | `CI:book-long-name-continuation-A` p7 |
| N8 ATAQUES vacía | **Sigue en parte** | La línea `____` ya no está; los filetes verticales en filas vacías siguen (M6) |
| N9 leyenda en B | **Cerrado**, con efecto nuevo | Sin leyenda, pero también se perdió «dos subcolumnas» del golden (M7) |
| N10 una sola fuente | **Cerrado** | `A:Mara-A` p1 y p7: `ETE`, `16`, `+8` sin rótulo |
| N11 nombre largo | **Cerrado** por decisión del propietario | `CI:P1-long-character-name-A` p1: cortado sin aviso. El nombre entero sigue como prosa en p4 (lectura del Worker; ver M10) |
| N12 Android ≠ Desktop | **Cerrado** (explicado) | Ver comparación abajo; el acta del candidato ya da su umbral |
| N13 detalles | **Sigue en parte** | `TEMP 0` cerrado. Siguen: rótulos de columna pegados a la cabecera (M4), segunda línea de cabecera (M8), pie `PRUEBA INTERNA V11 - NO APROBADA` |
| F03 fuente de clase sin clase | **Sigue** | `CI:P4-source-physical-break-A` p5: nombres propios `QAP4SOURCEFIRST…` y un `—` en las tarjetas (M9) |
| F08 corte entre clases | **Cerrado** | `CI:P1-multiclass-dice-A` p1 termina en «Clase 9 9», el resto en C2 |
| G-a, G-b, G-d, índice, secuencia | **Siguen cerrados** | Revisados en las mismas páginas |
| G-c | **Cerrado** | Igual que N2 |
| G-e | **Cerrado en el PDF** | Igual que N5 |
| §3.8, §3.30 NO_CUMPLE | **Cerrados** | Por N1 |
| §3.29, §3.32 NO_CUMPLE | **Siguen** | M1 y M2 |
| §3.3 NO_DEMOSTRADO | **Cerrado** | Retrato real en Android |
| §3.4 NO_DEMOSTRADO | **Cerrado** (en CI) | `CI:subrace-A` p1 |
| §3.12 NO_DEMOSTRADO | **Sigue** | Sin prueba impresa |

**Las 7 preguntas de `preqa.9`:** el contrato recoge respuestas del propietario solo para 3: ataque largo «Sí, en su lugar», nombre largo «Prefiero que el nombre sea cortado…» y notas de objeto especial «No, se imprimen». Las otras 4 (8 rótulos, Equipo y Tesoro en C2 compartida, más de 20 municiones, una sola fuente) están escritas como «Aclaración técnica del Worker … sin decisión nueva del propietario». El checkpoint `2026-10-29_V11_C283_FUENTES_GOLDEN.md` dice además que «las 7» siguen pendientes. Ver M11.

**Comparación Android contra CI** (gris, 100 ppp; píxeles con diferencia > 60 / > 8, página a página):

| PDF | Páginas | > 60 | > 8 (peor página) | Posiciones de texto |
|---|---|---|---|---|
| Aldren-A | 6 | 0 en todas | 53 (p3) | iguales |
| Ilyra-B | 9 | 0 en todas | 41 (p3) | iguales |
| Mara-A | 10 | 0 en todas | 144 (p2) | iguales |
| Mara-B | 12 | 0 en todas | 216 (p10 y p11) | iguales |
| Stress-A / B | 12 | 0 en todas | 1.610 (p9) | iguales |
| new-character-A / B | 4 | 641 en cada página | 981 (p3) | iguales |

Las diferencias de texto son la ligadura «fi» (Desktop) y el código de las marcas (Android `w`). **new-character no es el mismo dato:** Android se llama «PJ Nuevo QA» y CI «Personaje Nuevo»; los 641 píxeles son el nombre. Aldren-B, Ilyra-A, Aldren-epic-A y los dos retratos no tienen par en CI.

---

## 5. Hallazgos nuevos, de mayor a menor gravedad

### M1 — MEDIA — El Equipo en pares cambia de orden cuando se parte
- **Regla:** propietario 2026-10-26, «en el orden original»; §3.31; golden P2 y C2: cada bloque se lee por columnas.
- **Se imprime:**
  - `A:Stress-A` p4 (y B, y `CI:stress-android-A`): QAOBJETO29–33 a la izquierda, 35–39 a la derecha, y QAOBJETO34 **sola, arriba de la columna siguiente**, bajo «EQUIPO / CONTINUACIÓN». Se lee 33, 35 … 39, 34.
  - `A:Mara-B` p4: el bloque abre al pie con **una sola fila**, «Cuaderno … 11 | Cristal … 24»; p5 sigue con 12–23 y 25–34. Se lee 11, 24, 12 …
- **Debe imprimirse:** cada tramo con su propio orden por columnas, sin saltos. `CI:C2-shared-equipment-A` p4, sin corte, sale bien (29–34 | 35–39).

### M2 — MEDIA — Equipo Especial en una hoja casi vacía
- **Regla:** §3.29 «No crear obligatoriamente una hoja por módulo»; §3.32; contrato C2 «Agrupar módulos … de manera compacta»; golden extremo B p5 y comparador p2: Especial a todo el ancho **arriba** de una hoja compartida.
- **Se imprime:** `A:Mara-A` p6 y `A:Mara-B` p6: **un** objeto y el resto cuadrícula. `A:Stress-A` p5: 6 objetos y unos dos tercios de cuadrícula. `CI:P2-C2-large-amounts-A` p6: un objeto.
- **Debe imprimirse:** Especial junto con otros módulos cuando cabe, como en el golden. En `A:Mara-A` p5 la columna derecha es casi toda cuadrícula NOTAS.
- **Origen:** la regla del Worker «al pie si cabe entero; si no, en su propia hoja» (contrato, revisión de `preqa.9`). El golden no la tiene.

### M3 — MEDIA — Habilidades sueltas debajo de otro atributo (variante A, C2)
- **Regla:** §3.6 «continuación claramente vinculada»; contrato C341, rótulo «… / VIENE DE PÁG. n»; golden extremo B p4: subtítulo «HABILIDADES DE HONOR».
- **Se imprime:** `A:Mara-A` p4: RENOMBRE y, pegadas debajo, «Cartografía imposible (SAB)», «Resistencia mental (SAB)» y «Memoria de vidas (INT)». Parecen de RENOMBRE.
- **Debe imprimirse:** las habilidades de SABIDURÍA e INTELIGENCIA bajo un subtítulo propio, como el del golden.

### M4 — BAJA — Rótulos de columna en la franja de cabecera
- **Se imprime:** `A:Mara-A` p4 «RECURSOS / CONTINUACIÓN» en y≈84, tocando la regla de cabecera; p5 «RECURSOS / VIENE DE PÁG. 4» en media columna junto a «MÓDULOS COMPARTIDOS · DOS COLUMNAS»; igual en `A:Mara-B` p5 y `A:Stress-A` p4.
- **Golden:** no tiene este rótulo; su subtítulo de grupo va dentro de la cartela (extremo B p4). Es lo que N13 ya señalaba.

### M5 — BAJA — «VIENE DE PÁG. 1» en módulos que no están en P1
- **Se imprime:** ACCIONES y RECURSOS dicen «VIENE DE PÁG. 1» (`A:Aldren-A` p4, `A:Ilyra-A` p4, `A:Mara-A` p4), y la banda de P1 dice «RECURSOS CONTINÚA EN PÁG. 4», aunque P1 no tiene esos módulos.
- **Golden:** «VIENE» solo para módulos que empezaron en una página base. Es inofensivo, pero puede confundir.

### M6 — BAJA — Filetes verticales en filas vacías de ATAQUES
- `A:new-character-A` p1 y `A:Ilyra-A` p1. El golden (casi vacío p1 y 6+2 A p1) no los dibuja en filas vacías.

### M7 — BAJA — Falta «dos subcolumnas» en HABILIDADES (variante B)
- `A:Mara-B` p1 y `A:new-character-B` p1. El golden B lo lleva. Se quitó junto con la leyenda de N9.

### M8 — BAJA — Segunda línea de cabecera inexacta
- `A:Mara-A` p5 dice «C2 - Habilidades, acciones y recursos / 2» y contiene Equipo y Rasgos; `A:Stress-A` p4 dice lo mismo y contiene Personaje, Equipo y Tesoro. El propietario se reservó el control de la cabecera.

### M9 — BAJA — Fuente de clase sin clase enlazada (F03)
- `CI:P4-source-physical-break-A` p5: «QAP4SOURCEFIRST QAP4SOURCESECOND QAP4SOURCETHIRD —» en las tres tarjetas y esos nombres en la columna de fuente. El propietario pidió tipos, sin nombres propios. Solo en un caso de estrés.

### M10 — BAJA — Nombre completo como prosa en C2
- `CI:P1-long-character-name-A` p4: «NOMBRE COMPLETO DEL PERSONAJE: …» en 10 renglones. El propietario pidió cortar; lo de imprimirlo entero en C2 es «lectura del Worker, sujeta a su revisión». Conviene informárselo.

### M11 — BAJA, documento — Respuestas que el contrato atribuye al Worker
- 8 rótulos de Especial, Equipo/Tesoro compartidos, `____ / X` y «una sola fuente» figuran como decisión técnica del Worker. Son defendibles por la precedencia (golden o texto del contrato), pero no son respuestas del propietario, y el checkpoint dice que las preguntas siguen pendientes.

### M12 — BAJA — Alcance que sigue en C2 como renglón de texto
- `A:Stress-A` p4 y `CI:P126-range-in-C2-A` p4: «ALCANCE QAFILA3: …» a todo el ancho dentro de la tabla ATAQUES. Cumple «el alcance sigue una sola vez en C2»; la forma es del Worker.

### M13 — BAJA — Tres fuentes «Otro» con un solo rótulo
- `A:Stress-A` p1 y p6: «Clase larga 1 13 / Otro 13 / Raza 13 / Trasfondo 13» para seis fuentes, tres de ellas «Otro». Mismo valor, así que no se pierde nada, pero no se ve que son tres.

### Decisiones del Worker revisadas con sospecha

| Decisión | Mi lectura |
|---|---|
| Municiones una sola vez | Defendible: el golden las pone solo en P1. Sigue «sujeta a la revisión del propietario» |
| Títulos de C2 | Aplicada y correcta. «VIENE DE PÁG. 1» en ACCIONES/RECURSOS no tiene apoyo (M5) |
| Revisión de `preqa.9` (8 rótulos, `____ / X`, una sola fuente) | Defendible por la precedencia; debió decirse al propietario como información (M11) |
| Revisión de `preqa.9` (Especial «en su propia hoja») | **No defendible**: contradice el golden y §3.29; debió seguir el golden o preguntarse (M2) |
| Revisión de `preqa.9` (panel B sin leyenda) | Bien, pero se perdió también un rótulo del golden (M7) |
| Referencias con página real | Defendible, aplica §3.33 y C341 |
| Nombre completo en C2 | Protege el dato; debe saberlo el propietario (M10) |
| Sangría, pautas de 25 pt, fila épica con el alto del golden | Técnicas; se ven bien |

---

## 6. Preguntas que solo puede responder el propietario

Una a una, en este orden. Ninguna es de estilo.

1. **Equipo Especial que no cabe en la página 2.** Si sobran uno o pocos objetos, ¿van arriba de una hoja de continuación junto con otros módulos, como en el golden, o en una hoja propia aunque quede casi vacía?
2. **Nombre muy largo.** Además de cortarlo en la cabecera, ¿quieres que el nombre completo aparezca una vez en la hoja de continuación?
3. **Las cuatro reglas aplicadas sin tu respuesta:** en Equipo Especial vacío se imprimen los 8 rótulos del golden; Equipo y Tesoro comparten hoja con otros módulos; con más de 20 municiones sale solo `____ / X`; con una sola fuente de conjuros sale solo el valor. ¿Las apruebas? (Se pueden preguntar de una en una.)

**Para informar, sin pregunta:** las municiones clasificadas salen una sola vez, en P1, y sus notas quedan en la App; la segunda línea de la cabecera dice cosas como «C2 - Habilidades, acciones y recursos / 2».

---

## 7. Límites de esta revisión

**Páginas miradas como imagen:**

| Conjunto | Total | Vistas a tamaño legible | Solo por script |
|---|---|---|---|
| Android | 102 | unas 35 (Mara-A p1–p7, Mara-B p1, p4, p5, p8–p12, Stress-A p1, p2, p4–p6, Aldren-A p1, p2, p4, p5, Ilyra-A p1, p2, p4, p5, new-character-A p1–p3, recortes de retrato y épica) | el resto |
| CI | 1.362 | unas 15 | el resto |
| Golden | 59 | unas 27 | el resto |

**No pude comprobar:**
- Impresión real, lápiz y escala de grises (§3.12).
- La pantalla de la App: no vi Guardar, Compartir ni la elección A/B. No toqué el emulador ni adb.
- Que cada PDF salga de ese APK: los PDF no traen metadatos; me fío del SHA y del acta.
- Subraza y la mayoría de los casos de estrés en Android: solo están en CI.
- Las 132 reglas de la matriz una por una; solo QA01–QA08 y GLB12.
- Que las hojas antiguas no se hayan tocado (GLB12).

**Métodos y puntos ciegos:**
- Búsqueda de datos sin espacios; un texto corto puede «aparecer» por casualidad. Los dudosos los miré a mano.
- El detector de solapes solo cuenta textos en la misma línea base.
- La medida 60 %/40 % de Raza/Alineamiento la saqué de una imagen a 90 ppp.
- Para QA05 leí solo importaciones y una función, no todo el código.

**Lo que leí además de los PDF:** `ACTA_CANDIDATO_PREQA11.md` al empezar, solo la identidad (y luego para cotejar los SHA de los PDF); el contrato, D-0076 y la matriz (QA01–QA08, GLB12); las fichas JSON como datos; el generador del golden dentro del ZIP (orden de Equipo, rótulos de Especial); `AndroidPcSheetExportService.kt` y las importaciones de los renderizadores V11; el checkpoint `2026-10-29_V11_C283_FUENTES_GOLDEN.md` (estado de las preguntas). Al final, `ACTA_REVISION_INDEPENDIENTE_PREQA9.md` y `HALLAZGOS_REVISION_PREQA9.md`, solo para contrastar. El contexto de la sesión mostraba `AGENTS.md` y la memoria del proyecto.

**Scripts e imágenes:** `C:\Users\gms\AppData\Local\Temp\claude\81e26e1f5f1fff5b\af0b7db1-bfd0-437a-87dd-fbd9b773da41\scratchpad\review_preqa11`.
