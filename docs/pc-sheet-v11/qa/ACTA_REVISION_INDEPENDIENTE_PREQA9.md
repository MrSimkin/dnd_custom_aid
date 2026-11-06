# Acta de revisión independiente — candidato V11 `0.5.0-preqa.9`, build 50900

**Resultado en una línea:** los datos no se pierden y la hoja se parece mucho al golden, pero el candidato **no está listo para el QA del propietario**: hay 4 secciones de D-0076 en NO_CUMPLE, 7 de los 8 golden en NO_COINCIDE y 6 de las 9 reglas pedidas sin demostrar.

**Fecha:** 2026-11-06. **Commit revisado:** `87533aa550a6089cdabaca71960be5ceffb96d99`. Repositorio solo leído; no se hizo ningún cambio.

Abreviaturas: `A:` es un PDF generado por el APK en el emulador. `CI:` es un PDF de CI (Desktop), sin el prefijo `V11-INTERNAL-PROOF-`. «p4» es la página física 4.

---

## 1. Las 48 secciones de D-0076

**Recuento: CUMPLE 35 · NO_CUMPLE 4 · NO_DEMOSTRADO 3 · SUSTITUIDA 6.**

Cuando parte de una sección quedó sustituida y el resto sigue vigente, el veredicto es el del resto y la fila lo aclara.

| § | Qué pide | Veredicto | Evidencia |
|---|---|---|---|
| 3.1 | Logo y nombre en toda página | CUMPLE | 1.329 de 1.329 páginas con la imagen del logo (344×134). Misma línea, según el contrato. Nombre largo: ver N11 |
| 3.2 | Piezas de la cabecera de P1 | CUMPLE | `A:Mara-A` p1. Jugador y alineamiento en línea, «Sí, línea a mano» (2026-11-06) |
| 3.3 | Retrato: recortar, ajustar, silueta | NO_DEMOSTRADO | Silueta en `A:Mara-A` p1. Los dos modos solo en Desktop y sin deformar: `CI:portrait-CROP_TO_FILL-A` p1 (69,4×106 recortado al marco) y `CI:portrait-FIT_ENTIRE_IMAGE-A` p1 (40,3×61,6). **Ningún PDF de Android lleva retrato real**, y Android usa otro archivo de dibujo |
| 3.4 | Clase/nivel, raza con subraza, alineamiento | NO_DEMOSTRADO | `Guerrero 5 / Mago 3` correcto en `CI:two-classes-A` p1. **Ningún PDF imprime una subraza** (`Elfo — Alto elfo`): busqué en P1 de los 156 de CI y no hay ninguna. Multiclase extrema: F08 |
| 3.5 | Variantes A y B | CUMPLE | `A:Mara-A` p1 y `A:Mara-B` p1. Salvaciones en la fila del atributo: decisión 2026-11-01 |
| 3.6 | 2–3 atributos propios en P1; prioridad de los seis | CUMPLE | Escenario de tres en `A:Mara-A` p1; el cuarto en p4. Seis estándar primero en `CI:dense-six-attributes-A` p1 y p4. Valores recalculados por mí: todos bien. No existe PDF 6+2 exacto (ver QA04) |
| 3.7 | Combate: 3 filas, 8 campos, dados compactos | CUMPLE | `A:Mara-A` p1; `5d10 / 3d6` en `CI:two-classes-A` p1; `TEMP 7` y total del nivel, por decisiones 2026-10-28 y 2026-11-05 |
| 3.8 | Ataques: tabla de 3 columnas | **NO_CUMPLE** | P1 bien y alcance autorizado (2026-11-06). Pero lo que pasa a C2 sale como prosa y cambia de orden: `CI:P1-attack-fit-order-A` p1 y p4. Ver N1 |
| 3.9 | Rasgos sin descripción | CUMPLE | Búsqueda de cada `description` y `notes` en los 6 PDF Aldren, Ilyra y Mara, A y B: 0 impresas. Mara 01–26 una vez, en orden P1→P2→C2 |
| 3.10 | Reparto dinámico Ataques/Rasgos | SUSTITUIDA | Contrato 2026-11-03: «ATAQUES tiene 5 filas fijas y RASGOS 7 filas» |
| 3.11 | Conjuros en P1 siempre; fila épica condicional | CUMPLE | `A:Aldren-A` p1 sin N10; `A:Aldren-epic-A` p1 con N10, N11 y N12 en `____/____`, sin casillas. N10 solo aparece en ese PDF y en `CI:P1-epic-levels-A/B` |
| 3.12 | Filas, bandas, casillas utilizables, gris | NO_DEMOSTRADO | 7 mm sustituidos por GLB08 (2026-11-03). Lo vigente exige probar lápiz e impresión en gris, y **no hay prueba impresa**. Con 6 espacios las casillas bajan a unos 6,5 pt (`CI:P1-six-real-spell-slots-A` p1) |
| 3.13 | Azul niebla clásico | CUMPLE | Comparado a ojo con el golden en todas las familias |
| 3.14 | Municiones, Tesoro, Otros en evaluación | SUSTITUIDA | Por §3.15 |
| 3.15 | Municiones hasta 20; más: `____ / X` | CUMPLE | 20 casillas en `A:Aldren-A` p1; `MARCAS: ___ / 40` en `CI:ammunition-over-20-A` p1. Ver N4 y pregunta 4 |
| 3.16 | Marco C2 y fuentes | SUSTITUIDA | Marco presente. Fuentes reemplazadas el 2026-10-29. Halladas solo las del golden y Para Hoja v8 |
| 3.17 | Barlow y glifos | SUSTITUIDA | Misma decisión. Las marcas usan Para Hoja v8 |
| 3.18 | Cartelas C2c, casillas simples | CUMPLE | Todas las familias |
| 3.19 | P2: Equipo, Rasgos, Especial; sin Otros | CUMPLE | `A:Mara-A` p2, `A:Aldren-A` p2 |
| 3.20 | Tesoro bajo Equipo, monedas propias | CUMPLE | `CI:C206-both-paired-overflow-A` p2. Flujo por columna, como el golden, que tiene más precedencia que D-0076 |
| 3.21 | Especial híbrido, sin truncar | CUMPLE | `A:Mara-A` p2 con filas altas; `A:Aldren-A` p2 con filas libres. Rótulos: G-e |
| 3.22 | Casilla ACTIVO sin premarcar | CUMPLE | Vacía aunque Mara tiene `equipped` y `attuned`. No existe dato real para ver una marca |
| 3.23 | P3 con Rasgos a la derecha | SUSTITUIDA | Contrato 2026-10-16: «P3 exclusivamente narrativa» |
| 3.24 | P4 en tres columnas por nivel | CUMPLE | `A:Ilyra-A` p5: 27 de 27 pares conjuro-fuente. `A:Mara-A` p7: 24 de 24. Fila larga: N6 |
| 3.25 | Nivel, espacios y casillas en cabecera | CUMPLE | `A:Mara-A` p7: 4, 3, 3, 3, 4, 2, 2, 1, 1 casillas, decisión 2026-11-06. Trucos sin espacios |
| 3.26 | Referencia superior; cabecera en una línea | CUMPLE | Una línea siempre. Valor por fuente en `CI:P4-six-sources-A` p7 y `CI:P4-same-name-distinct-sources-A` p5 |
| 3.27 | Notas y última hoja | CUMPLE | Última hoja correcta en los 165 PDF. Sin número, con línea de pie |
| 3.28 | C-N3 con prioridad digital | CUMPLE | `A:Mara-A` p8–p9: tituladas primero, 9 notas y la general íntegras |
| 3.29 | C2 compartidas; contratos por módulo | **NO_CUMPLE** | Una hoja por módulo: `CI:P2-C2-large-amounts-A` p6, p7, p8. Municiones como prosa: `CI:ammo-overflow-once-in-C2-A` p4. Ver N3 y N4 |
| 3.30 | Ubicación e inclusión de ataques | **NO_CUMPLE** | Ubicación tras P3: la deja el golden. Pero pide «conservar exactamente» las 3 columnas en C2 y no se cumple (N1) |
| 3.31 | Prioridades; Notas aparte | CUMPLE | Sin pérdidas en QA01. Datos solo en App por decisión 2026-11-06. C-N3 solo notas |
| 3.32 | Probar familias y elegir la mejor | **NO_CUMPLE** | Lo aprobado es comparar disposiciones. El rótulo sale del título de la página (renderer Android, líneas 601–611) y `CI:P2-C2-large-amounts-A` p7 y p8 muestra el resultado de no comparar. Ver N3 |
| 3.33 | Referencias de ida y vuelta | CUMPLE | 803 «CONTINÚA» y 991 «VIENE»: ninguna a una página imposible. 12 «VIENE» vuelven al origen, lo que §3.33 admite |
| 3.34 | Orden: hoja, C2, Notas, Libro, final | CUMPLE | `A:Mara-B`: P1 P2 P3 C2×3 P4 CN3×2 Libro×2 final |
| 3.35 | Sin conjuros no hay P4; decide el dato | CUMPLE | `A:Aldren-A` sin P4. `CI:non-caster-race-spell-A` p5 con Fire Bolt de Raza en un Guerrero |
| 3.36 | P3 con reserva de Rasgos | SUSTITUIDA | Misma decisión que §3.23 |
| 3.37 | Sin Equipo: reserva amplia | CUMPLE | `A:new-character-A` p2: 13 filas × 2, igual al golden |
| 3.38 | Sin Ataques: reserva | CUMPLE | `A:new-character-A` p1 y `CI:no-attacks-A` p1: 5 filas libres. Detalle ajeno al golden: N8 |
| 3.39 | Sin Municiones: una entrada | CUMPLE | `A:Mara-A` p1, «Tipo:» con 20 casillas |
| 3.40 | P3 vacía se conserva | CUMPLE | `A:new-character-A` p3 |
| 3.41 | Tesoro sin monedas: celdas manuales | CUMPLE | `CI:C379-empty-sheet-A` p2. Cero real conservado en `A:new-character-A` p2 |
| 3.42 | Especial vacío: rótulos mixtos | CUMPLE | `A:new-character-A` p2: 4 rótulos y 4 libres, como pide su texto. El conflicto con el golden es G-e |
| 3.43 | P2 toda vacía: reservas separadas | CUMPLE | `CI:C379-empty-sheet-A` p2 |
| 3.44 | Sin notas no hay C-N3 | CUMPLE | `A:new-character-A`: 4 páginas, igual que el golden |
| 3.45 | P3 solo texto; silueta sin retrato | CUMPLE | `CI:background-images-no-portrait-A` p1 con silueta y solo el logo como imagen |
| 3.46 | Libro en dos columnas, fichas variables | CUMPLE | `A:Ilyra-B` p7–p8, `A:Mara-B` p10–p11 |
| 3.47 | Ficha esencial | CUMPLE | Todas las descripciones íntegras en `A:Ilyra-B` y `A:Mara-B`. Sin tiempo, alcance ni duración. Escuela solo si existe: `CI:BK06-school-with-school-A` p7. Nombre largo: N7 |
| 3.48 | Ficha entera a la columna siguiente | CUMPLE | `CI:BK04-whole-next-column-A` p7, `CI:BK05-long-card-A` p7–p8. Solo se probó un párrafo único |

---

## 2. Los ocho golden

**Recuento: COINCIDE 0 · DIFERENCIAS_JUSTIFICADAS 1 · NO_COINCIDE 7.** Tres de esos NO_COINCIDE son menores.

Comprobé que los 8 PDF son los del ZIP del repositorio: SHA-256 del ZIP `35a1c6ea…364e6a`, y cada PDF igual. Tiene 59 páginas, 46 distintas por imagen. **Vi las 46 lado a lado con su par de la App.** Ningún PDF de la App usa los datos de un golden, así que comparo forma y gramática, no valores.

**Diferencias que no cuento porque tienen decisión escrita:**
- Logo y sin escudo (2026-10-30).
- Número de página solo (C340) y última hoja sin número.
- «CONTINÚA EN PÁG.» y «VIENE DE PÁG.» (C341).
- Fila de atributo con «TIRADA DE SALVACIÓN» (2026-11-01).
- Casillas de P1 y P4 (2026-11-06) y `gastados/total` (2026-10-28).
- `TEMP 7` e inspiración (2026-10-28); `35 pies · 10,5 m` (2026-10-28).
- Nombre y alcance en ATAQUES (2026-11-06).
- Historia en C2 (2026-11-06).
- Sin índice del Libro (2026-11-06).
- Rótulo de fuente en P4 (2026-10-26 y 2026-10-28).
- Segunda línea de cabecera con el título de la página (2026-10-30).
- Escuela solo si existe (2026-10-29).

Las municiones tachadas del golden son datos de ejemplo: la App no guarda marcas gastadas por munición.

| Golden | Pares vistos | Veredicto | Qué queda sin respaldo |
|---|---|---|---|
| `v11_personaje_casi_vacio` (4 p) | `A:new-character-A` p1–p4 | NO_COINCIDE, menor | **p1:** línea `____` y filetes verticales en la tabla ATAQUES vacía (N8). **p2:** rótulos de Equipo Especial (G-e). p3 y p4 sin diferencias |
| `v11_ordinario_6mas2_variante_A` (7 p) | `A:Ilyra-A` p1, p2, p3, p5, p6, p7; `A:Ilyra-B` p7 | NO_COINCIDE, menor | **p2:** golden con Pecho, Piernas y Libre en las filas sobrantes; App en blanco (G-e). **No hay PDF con 6+2.** El C2 extra de Ilyra está justificado por sus recursos |
| `v11_ordinario_6mas2_variante_B` (7 p) | `A:Ilyra-B` p1 y las mismas | NO_COINCIDE, menor | Lo anterior, más la leyenda `✓ COMPETENCIA ✓✓ PERICIA` en vez de «dos subcolumnas» (N9) |
| `v11_ordinario_6mas3_variante_A` (7 p) | `A:Mara-A` p1, p2, p3, p7, p9, p10; `A:Mara-B` p11 | DIFERENCIAS_JUSTIFICADAS | Todo con decisión. Dos avisos: G-e no se ve aquí solo porque 4 objetos altos llenan las filas; y el rótulo en una sola fuente (N10) |
| `v11_ordinario_6mas3_variante_B` (7 p) | `A:Mara-B` p1 y las mismas | NO_COINCIDE | Leyenda de P1 (N9) |
| `v11_extremo_variante_A` (11 p) | `A:Mara-A` p1–p10; `A:Mara-B` p10; `CI:P4-dense-cantrips-A` p6 | NO_COINCIDE | **p4:** título `PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN` (N2). **p4–p5:** Equipo y Tesoro comparten con otros módulos en el golden; en la App van siempre a una hoja propia, `A:Mara-A` p6 (N3) |
| `v11_extremo_variante_B` (10 p) | `A:Mara-B` p1–p5, p7, p9, p11, p12; `CI:P4-dense-cantrips-B` p6 | NO_COINCIDE | Igual que A. Además el subtítulo «HABILIDADES DE HONOR» del golden p4 no tiene equivalente |
| `v11_comparador_C2_extremo` (6 p) | `CI:C2-grouped-custom-attributes-A` p4, `CI:C2-long-special-item-A` p5, `CI:P3-story-shared-C2-A` p4, `CI:C2-attack-table-rows-A` p4, `CI:C206-both-paired-overflow-A` p4, `CI:P3-three-paragraphs-A` p4 | NO_COINCIDE | Es la mayor distancia. Títulos (N2). Sin ATAQUES a todo el ancho (p4). Sin Tesoro a media página junto a Municiones y Equipo (p5). Municiones en prosa (N4) |

**Medida de apoyo:** rectángulos de dibujo iguales con tolerancia de 0,6 pt, sobre los del golden. Vacío p1 83 %, p2 72 %, p3 68 %, final 83 %. Mara-A p1 62 %, p7 80 %. Extremo p4 contra Mara-A p4: 4 %. Sirve de orientación, no de veredicto: los datos cambian las filas.

---

## 3. QA01 a QA08 y GLB12

**Recuento: 3 demostradas con salvedades · 6 no demostradas.**

| Regla | ¿Demostrada? | Por qué |
|---|---|---|
| **QA01** datos íntegros | **Sí, con salvedades** | Tabla de abajo. Ningún registro perdido ni duplicado en los tres personajes, A y B. Secuencias numeradas de estrés completas. Salvedad: el PDF no contiene IDs, así que comparo textos; y el orden de ataques no se conserva en un caso (N1) |
| **QA02** fuentes, glifos, solapes | **Sí, con salvedades** | PyMuPDF sobre 1.329 páginas: 0 fuentes sin incrustar; 0 caracteres U+FFFD; todas 612×792; 0 textos fuera de margen; 0 solapes de texto contra texto (fuera de marcas de casilla en el hueco reservado); 0 textos cruzando filetes verticales. Letra mínima 5,5 pt («RETRATO», como en el golden). Salvedad: es una heurística sobre cajas. Un glifo vacío no se detecta así; lo miré solo en las páginas vistas |
| **QA03** Guardar/Compartir real | **No** | Tengo PDF, no la pantalla. No hay forma de ligar cada PDF al APK: sus metadatos están vacíos. El acta del implementador dice que Compartir no se completó y que el selector de Desktop no se probó. También dice «0 píxeles» frente a CI y yo sí mido diferencias: ver N12 |
| **QA04** comparación con los 8 golden | **No** | 7 de 8 NO_COINCIDE. El escenario 6+2 de la propia regla no existe en ningún PDF |
| **QA05** cuatro familias anteriores | **No** | No tengo comparación de salida contra una base anterior. Custom v1 con Mara falla, según el acta del implementador; es el defecto M50800-02, del 2026-09-28 |
| **QA06** identidad del candidato | **Sí, con salvedades** | APK en scratchpad con SHA `d4406779…50d025`, igual al acta y a la matriz. `versionName 0.5.0-preqa.9` y `versionCode 50900` en el commit. Los 9 PDF de Android con el SHA del acta. Salvedad: no reconstruí el APK ni consulté CI |
| **QA07** 48 secciones conciliadas | **No** | Esta acta hace la conciliación independiente, y el resultado tiene 4 NO_CUMPLE y 3 NO_DEMOSTRADO |
| **QA08** todo en PASS | **No** | La matriz tiene las 129 reglas en `NOT_VERIFIED`. La comprobación es trivial y el resultado no admite otra lectura |
| **GLB12** no alterar legados ni golden | **No demostrada del todo** | Golden intacto: SHA verificado. Código: ningún archivo de renderer legado cambia desde la bifurcación con `main`; `AndroidPcSheetExportService` suma 64 líneas y `PcSheetPdfExportFoundation` añade un campo con valor por omisión y hace `internal` una función. Pero el modelo de datos sí cambia y **no comparé PDF legados** |

### Inventario independiente de QA01

Leí la ficha JSON y busqué cada dato en el PDF real. Las coincidencias dudosas las resolví a mano y están listadas debajo.

| Dato | Aldren A y B | Ilyra A y B | Mara A y B |
|---|---|---|---|
| Rasgos | 8 de 8 | 10 de 10 | 26 de 26 |
| Entradas de combate | 4 de 4 | 2 de 2 | 8 de 8 |
| Objetos (de ellos, especiales) | 11 de 11 (4) | 6 de 6 (2) | 34 de 34 (5) |
| Monedas | 5 de 5 | 5 de 5 | 5 de 5 |
| Pares conjuro-fuente en P4 | sin conjuros | 27 de 27 | 24 de 24 |
| Libro: conjuros y descripción íntegra (solo B) | no aplica | 25 de 25 | 24 de 24 |
| Notas tituladas y generales | 0 + 1 | 0 + 1 | 9 + 1 |
| Recursos | 2 de 2 | 4 de 4 | 10 de 10 |
| Atributos y habilidades propias | no tiene | no tiene | 4 y 10 |
| Campos de Trasfondo | todos | todos | todos |
| Descripción o notas de rasgo impresas | 0 | 0 | 0 |
| Notas de ataque impresas | 0 | 0 | 0 |

- **Coincidencias que parecían fallos y no lo son:**
  - «Champion» y «Evoker»: salen en la fuente real de un rasgo, no como subclase.
  - «Suministros de calígrafo»: Ilyra también lo tiene como objeto.
  - «Eco temporal»: es el nombre de un recurso de Mara, además de un sentido.
  - «Trasfondo Acólito.» y «Equipo inicial de Mago.»: notas iguales en objetos comunes y especiales; las impresas son las del especial.
- **Solo en App (GLB14):** ningún nombre de competencias, defensas, sentidos, movimientos, efectos, marcadores ni opciones de clase, y ningún rótulo de salvaciones contra la muerte, agotamiento o idiomas, en los 165 PDF. Busqué por nombre y por rótulo en español, así que un dato con otro texto se me escaparía.
- **Valores recalculados por mí en P1:** salvaciones, habilidades, habilidades propias, iniciativa, velocidad en metros y dados de golpe: todos correctos en los tres personajes. `gastados/total` de recursos correcto.
- **CD de Mara:** la ficha guarda `spellSaveDc: 17`, pero se imprime 16. Es el cálculo por fuente de la App (8 + 4 de competencia + 4 de Éter), no un invento, según `CharacterSuccessorDomain.kt`. No lo cuento como pérdida; ver N10.
- **Estrés:** secuencias sin huecos ni repetidos en `CI`: 72 atributos y 72 habilidades, 82 objetos y 55 monedas, 118 rasgos, 175 trucos, 8 filas de ataque.

---

## 4. Cierre de la revisión anterior

No usé `HALLAZGOS_REVISION_C6AF6CC4.md` como prueba. Lo leí al final para contrastar. **Discrepo en dos filas**: G-c (dice corregido y no lo está) y G-e (su razonamiento invierte la precedencia).

| Id | Estado en este candidato | Evidencia |
|---|---|---|
| F01 | **Cerrado** por decisión 2026-11-06 | `A:Mara-A` p7: casillas 4, 3, 3, 3, 4, 2, 2, 1, 1 |
| F02 | **Cerrado** | `A:Aldren-A` p1: FUERZA ✓ y `+7`; DESTREZA vacía |
| F03 | **Sigue en parte** | Corregido en `A:Mara-A` p1 y p7, y sin fragmento de ID en `CI:P4-same-name-distinct-sources-A` p5. **Sigue:** `CI:P4-source-physical-break-A` p5 imprime «Fire Bolt \| QAP4SOURCEFIRST» en la celda del nombre y el nombre propio de la fuente en las tarjetas. Es una fuente de tipo clase sin clase enlazada |
| F04 | **Cerrado** | `CI:P4-source-physical-break-A` p5 con valor por fuente. `SEGÚN FUENTE` solo queda en P1 de ese PDF, A y B, como último recurso autorizado |
| F05 | **Cerrado** en habilidades | `A:Mara-A` p4: casilla, nombre y `+8`. La prosa reaparece en Ataques (N1) y Municiones (N4) |
| F06 | **Cerrado** | Alcance en P1, autorizado. `A:Mara-A` no lo repite en C2. Virotes una sola vez en `A:Aldren-A`. Sin notas de ataque |
| F07 | **Sigue en el PDF; respaldado por el golden** | `A:Aldren-A`: C2 tras P3. Con la precedencia dada, el golden pasa delante de §3.30. El propietario no lo ha decidido por escrito; conviene informárselo |
| F08 | **Sigue en parte** | `/` correcto. `CI:P1-multiclass-dice-A` p1 acaba en «Clase 9 9 /» y p4 parte «Clase» de «15 15». Solo en estrés de 16 clases |
| F09 | **Cerrado** | «VER C2» no aparece en ningún PDF. `CI:P1-multiclass-dice-A` p1 y `CI:P1-long-spell-slots-A` p1 dicen «CONTINÚA EN PÁG. 4» |
| F10 | **Cerrado** | `A:Mara-A` p4 con banda «RECURSOS CONTINÚA EN PÁG. 5»; p2 con pie «CONTINÚA EN PÁG. 5 Y 6» |
| F11 G-a | **Cerrado** | `A:Aldren-A` p1: orden del golden |
| F11 G-b | **Cerrado** | `A:Mara-A` p1: FORTUNA, CORDURA, ÉTER |
| F11 G-c | **Sigue** | Quedó bien casi todo, pero `PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN` sale en 104 páginas (N2) |
| F11 G-d | **Cerrado** | Ninguna barra lateral en los 165 PDF |
| F11 G-e | **Sigue** | `A:new-character-A` p2 contra golden casi vacío p2 (N5) |
| F11 índice | **Cerrado** | Decisión 2026-11-06. `A:Ilyra-B` p7 empieza en las fichas |
| F11 secuencia | **Cerrado** | `A:new-character-A`: 4 páginas sin C2 |
| F12 | **Cerrado** | `A:Mara-A` p4: nombre 7,2 pt; «Recupera…» 6,1 pt |
| F13 | **Cerrado** en el contrato | Regla medida en y709,8; el contrato ya lo dice |
| F14 | **Cerrado** | Imagen de 344×134 en las 1.329 páginas, en x57–147, y719,5–754,6 |
| F15 | **Cerrado** | `A:Aldren-epic-A` p1 |

---

## 5. Hallazgos nuevos, de mayor a menor gravedad

### N1 — ALTA — Los ataques que siguen en C2 salen como prosa y cambian de orden
- **Regla:** contrato 2026-11-06: toda entrada con bono «va a la tabla ATAQUES, en su orden, […] lo que no quepa en P1 sigue en la tabla de ATAQUES de C2»; §3.30, «conservar exactamente» las tres columnas; golden comparador p1.
- **Se imprime:**
  - `CI:P1-attack-fit-order-A` p1 lleva QASHORT5–9, y p4 sigue con QASHORT10 y luego QALONG1–4. En la ficha, QALONG1–4 van **primero**.
  - Esos largos salen como `ATAQUE QALONG1 … | BONO 5 | DAÑO 1d6+2`.
  - `CI:P1-attack-multiline-A` p4 imprime `ATAQUE QAATTACKRANGE | ALCANCE … | BONO 7 | DAÑO 1d6+2` con 4 filas libres en P1.
- **Debe imprimirse:** filas de la tabla, en orden. Android usa el mismo camino (`AndroidPcSheetV11PdfProofRenderer.kt` líneas 1172 y 1630), aunque no hay PDF de Android de este caso. La prueba de CI exige este comportamiento, así que no lo detecta. Ver pregunta 1.
- **Sin demostrar:** el caso de la regla P126, «la fila imprime el nombre y el alcance sigue una sola vez en C2». Ningún PDF lo muestra.

### N2 — ALTA — Título de C2 que no existe en el golden
- **Regla:** aclaración del Worker 2026-11-06 sobre títulos; regla C215, «Títulos de C2 como los del golden, sin jerga técnica»; regla general 2026-10-28.
- **Se imprime:** `PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN` en 104 páginas, entre ellas `A:Aldren-A` p4, `A:Mara-A` p4 y `A:Mara-B` p4. En Aldren solo hay acciones debajo. En Mara, RENOMBRE queda bajo ese título y sus habilidades bajo otro, «ATRIBUTOS Y HABILIDADES».
- **Debe imprimirse:** atributos y habilidades bajo `ATRIBUTOS Y HABILIDADES`; la tabla bajo un título propio, que el contrato llama ACCIONES.
- **Es visible en 5 de los 9 PDF de Android.** `HALLAZGOS` dice «Corregido en C452»; no lo está.

### N3 — MEDIA-ALTA — Módulos que ocupan una hoja cada uno, con hojas casi vacías
- **Regla:** §3.29, «No crear obligatoriamente una hoja por módulo»; contrato C2, «Agrupar módulos diferentes de manera compacta»; §3.32; golden comparador p5.
- **Se imprime:**
  - `CI:P2-C2-large-amounts-A`: p6 Equipo, p7 **una** moneda, p8 **un** objeto especial; el resto de cada hoja es cuadrícula.
  - `CI:P2-currency-source-break-A`: p5 con 3 monedas y una cuadrícula entera, mientras p4 tiene una columna entera libre.
  - `A:Mara-A`: p6 con Equipo y Especial, y p5 con más de media hoja en cuadrícula.
- **Debe imprimirse:** esos módulos juntos cuando quepan. El propio `COMPARACION_8_GOLDEN.md` lo marca «Sin decisión». Ver pregunta 3.

### N4 — MEDIA — Municiones de C2 como prosa, y una palabra añadida
- **Regla:** regla general 2026-10-28; golden comparador p1, nombre a la izquierda y dos subfilas de casillas.
- **Se imprime:** `CI:ammo-overflow-once-in-C2-A` p4 y `CI:P1-ammo-source-break-A` p4: `MUNICIÓN QATERCERA | CANTIDAD 8`, y debajo `MARCAS:` y las casillas. En P1, `CI:ammunition-over-20-A` p1 dice `MARCAS: ___ / 40`.
- **Debe imprimirse:** la fila del golden, sin «MUNICIÓN», «CANTIDAD» ni «MARCAS:».
- **Conflicto sin resolver:** el golden comparador p1 pone `___ / 21` **y** 20 casillas; el contrato y §3.15 piden sustituirlas. Ver pregunta 4.

### N5 — MEDIA — Rótulos de Equipo Especial distintos del golden (G-e), con precedencia invertida
- **Regla:** precedencia del contrato: decisiones del propietario, luego golden, luego D-0076. §3.42 dice además que «No se aprueba una lista exacta» y que los rótulos salgan de las ubicaciones reconocidas por la App.
- **Se imprime:** `A:new-character-A` p2: Cuello, Cuerpo, Mano izquierda, Mano derecha y 4 en blanco. `A:Ilyra-A` p2: filas sobrantes en blanco.
- **Golden:** Cabeza, Rostro, Cuello, Mano izquierda, Mano derecha, Pecho, Piernas, Libre.
- **Debe resolverse:** la App ya tiene esa lista en `AndroidCustomV2ExtendedRenderer.kt`, línea 2756, salvo «Libre»; «Cuerpo» no está en ella. `HALLAZGOS` dice que §3.42 prevalece sobre el golden, y con esta jerarquía no es así. Ver pregunta 2.

### N6 — MEDIA-BAJA — Fila larga de P4
- **Regla:** contrato 2026-10-28, «casilla vacía para lápiz y solo el tipo de fuente», con la columna de fuente del golden.
- **Se imprime:** `CI:book-long-name-continuation-A` p5: «Conjuro de nombre excepcionalmente largo / para probar… / en el Libro | Mago». La fuente se mete en el nombre tras una barra `|`, y las líneas siguientes empiezan a la izquierda de la casilla. También `CI:P4-spell-name-break-A` p5 y `CI:C386-long-spell-names-A` p5.
- **Efecto añadido:** esa fila empuja un `NIVEL 1 / CONTINUACIÓN` que solo contiene filas vacías, en la columna siguiente (`CI:book-long-name-continuation-A` p5, `CI:P4-source-physical-break-A` p5).
- **Riesgo real:** una clase de nombre largo, como la de Mara, deja poco sitio al nombre del conjuro.
- **Debe imprimirse:** fuente en su columna, sangría bajo el nombre y sin continuación vacía.

### N7 — BAJA — Ficha del Libro con nombre largo
- **Se imprime:** `CI:book-long-name-continuation-A` p7 y `CI:P4-spell-name-break-A` p7: nombre en letra de texto, `NIVEL 0` y `DESCRIPCIÓN:` en el cuerpo de la franja.
- **Debe imprimirse:** la gramática del golden (nombre en EB Garamond y NIVEL a la derecha) partiendo el nombre. Fuente: §3.47 y regla general 2026-10-28.

### N8 — BAJA — Tabla ATAQUES vacía
- **Se imprime:** `A:new-character-A` p1: `____` en la primera fila y dos filetes verticales.
- **Golden casi vacío p1:** filas limpias. No encontré decisión que lo autorice.

### N9 — BAJA — Leyenda añadida en la variante B
- **Se imprime:** `A:Mara-B` p1 y `A:Aldren-B` p1: `✓ COMPETENCIA ✓✓ PERICIA` en la cartela HABILIDADES.
- **Golden:** «dos subcolumnas». Tampoco encontré decisión. Es útil, pero se aparta sin respaldo.

### N10 — BAJA — Rótulo de fuente con una sola fuente
- **Se imprime:** `A:Mara-A` p1 y p7: `Cronomante del Umbral ETE`, `Cronomante del Umbral 16` y `+8` sin rótulo. Ocurre porque la CD global guardada (17) no coincide con la de la fuente (16).
- **El golden y Aldren/Ilyra:** valor solo. La aclaración «SEGÚN FUENTE» habla de varias fuentes. Ver pregunta 6.

### N11 — BAJA — Nombre largo recortado, decisión del Worker
- **Se imprime:** `CI:P1-long-character-name-A` p1–p6 y `CI:P1-character-name-hard-break-A`, incluida la última hoja: «… / NOMBRE EN PÁG. 4». El nombre completo está en p4 como prosa: «NOMBRE COMPLETO DEL PERSONAJE: …».
- **Por qué importa:** es una «Aclaración técnica del Worker», y el propietario se reservó el control de la cabecera (2026-10-30). Ver pregunta 5.

### N12 — BAJA, técnica — Android y Desktop no son idénticos
- **Medido a 100 ppp con diferencia de gris mayor que 8:** `A:Mara-A` p2 frente a CI, 144 píxeles; total de Mara-A 329, Mara-B 761, Aldren-A 65, Ilyra-B 81.
- **Causa:** Desktop une «fi» en una sola letra y Android no. Además, Android guarda las marcas como `w`, `W` y `T` en la capa de texto, y Desktop como U+E108, U+E109 y U+E10E.
- **Efecto:** no se nota en la hoja, pero `ACTA_CANDIDATO_PREQA9.md` dice «0 píxeles», y con este criterio no es así. No conozco el umbral que usó.

### N13 — BAJA — Otros detalles menores
- **Rótulos de continuación:** quedan a 1,2 pt de la tarjeta o de la regla de cabecera (`CI:P4-dense-cantrips-A` p6, `A:Mara-A` p5), con dos cuerpos (7,6 y 6,1 pt) y otra forma en gris («CONTINUACIÓN: Fire Bolt», «EQUIPO: QAC2ANCLA / VIENE DE PÁG. 7»). El contrato cita «TRUCOS 0 / CONTINUACIÓN» como del golden, pero **no aparece en ninguno de los 8 PDF**.
- **Segunda línea de cabecera:** cumple «título de la página» (2026-10-30), pero dice `P1 - Personaje / combate / habilidades / 1` o `C2 - Equipo y tesoro / EQUIPO + EQUIPO ESPECIAL`, con términos internos.
- **`TEMP 0`:** se imprime en todo personaje sin PV temporales (`A:new-character-A` p1). Es un dato real, pero en el golden no aparece.
- **Pie:** `PRUEBA INTERNA V11 - NO APROBADA` está en todas las páginas salvo la última. Es intencionado (ver límites).

### Decisiones del Worker revisadas con sospecha

| Decisión escrita como «del Worker» | Mi lectura |
|---|---|
| Municiones una sola vez (2026-11-06) | La regla es correcta en el PDF. «Sus notas quedan en la App» es defendible por «notas […] de equipo común», pero el contrato la deja «sujeta a la revisión del propietario»: **falta esa revisión** |
| Títulos de C2 (2026-11-06) | **No se aplicó del todo** (N2). `RECURSOS` no está en el golden, pero sale de la aclaración de recursos del 2026-10-28 |
| Referencias con página real (2026-11-06) | Bien en lo demás. Recortar el nombre en todas las hojas **debió preguntarse** (N11) |
| Casillas: más del máximo, hasta 6 (2026-11-06) | El propietario dijo «un máximo». Conservar 5 o 6 protege datos reales y lo acepto como técnico, pero **debe saberlo** |
| Fila épica con el alto de las tres del golden | Técnico; se ve bien en `A:Aldren-epic-A` p1 |
| «/ NOTAS: …» dentro de Descripción en Equipo Especial | No figura ni como decisión. El golden no lo tiene: **debió preguntarse** (pregunta 7) |

---

## 6. Preguntas que solo puede responder el propietario

Una a una, en este orden.

1. **Ataque de nombre largo.** Cuando un ataque no cabe en una fila de la página 1, ¿lo escribimos en dos renglones en su lugar, o lo mandamos a la página de continuación aunque se altere el orden?
2. **Equipo Especial sin objetos.** ¿Imprimimos los 8 rótulos del golden (Cabeza, Rostro, Cuello, Mano izquierda, Mano derecha, Pecho, Piernas, Libre), o los 4 actuales y 4 filas en blanco?
3. **Equipo y Tesoro que continúan.** ¿Pueden ir en media hoja junto a otros módulos, como en el golden, aunque los nombres largos ocupen más renglones?
4. **Más de 20 municiones.** ¿Imprimimos solo `____ / X`, o `____ / X` y además 20 casillas, como en el golden?
5. **Nombre muy largo.** ¿Aceptas que la cabecera lo corte y diga «NOMBRE EN PÁG. 4», o prefieres otra forma?
6. **Personaje con una sola fuente de conjuros.** ¿La tarjeta dice solo `16`, o `Cronomante del Umbral 16`?
7. **Notas de un objeto especial.** ¿Se imprimen junto a su descripción, o quedan solo en la App?

**Para informar, sin pregunta:** C2 va tras P3 (F07), y una munición clasificada imprime solo su nombre y marcas, con sus notas en la App.

---

## 7. Límites de esta revisión

**Páginas miradas como imagen:**

| Conjunto | Total | A tamaño legible (66–100 ppp) | En hoja de contacto (38 ppp) | Solo por script |
|---|---|---|---|---|
| Android | 64 | 37 | 13 | 14, idénticas por imagen a una ya vista |
| CI | 1.265 | 50 | unas 100 | **unas 1.115** |
| Golden | 59 | 46 distintas | 0 | 13, idénticas a una ya vista |

A 38 ppp solo se aprecia la composición, no el texto. Las variantes B de CI casi no las miré.

**No pude comprobar:**
- **Impresión:** ninguna prueba en papel, lápiz, tamaño real ni escala de grises. Tampoco legibilidad real de 6,1 pt gris.
- **Pantalla de la App:** no vi Guardar, Compartir, ni elección de A/B y Libro. No toqué el emulador, por las reglas de esta revisión.
- **Origen de los PDF:** sus metadatos están vacíos. No puedo demostrar que cada PDF salga de ese APK ni que el CI sea de ese commit. Confío en el SHA del APK y del acta.
- **Android sin PDF:** retrato real, y casi todo el estrés (ataques largos, más de 20 municiones, Libro largo, seis fuentes). Solo hay 9 PDF de Android. Los dibujos pueden diferir porque Android tiene su propio archivo de código.
- **Casos sin ningún PDF:** subraza; 6+2 exacto; alineamiento y jugador con valor real (la App no los guarda); ACTIVO marcado; alcance que sigue en C2 con el nombre en P1 (P126).
- **El pie final:** todo lleva «PRUEBA INTERNA V11 - NO APROBADA». No existe una salida definitiva que revisar.
- **Las 129 reglas de la matriz:** no las verifiqué una por una. El encargo pedía QA01–QA08 y GLB12, y usé las demás como guía.
- **GLB11 (determinismo):** no generé dos veces. Solo hay coincidencia de composición entre Android y CI.
- **Legados:** no comparé ningún PDF legado con una base anterior.

**Métodos y sus puntos ciegos:**
- La búsqueda de datos ignora espacios y exige la cadena seguida; un dato corto puede «aparecer» por coincidencia, y uno partido entre columnas puede no salir. Los dudosos los miré a mano (por ejemplo, «Técnica 4 — Descarga prismática», partida en `A:Mara-A` p4).
- El detector de solapes reduce las cajas de texto para no contar el interlineado; un roce leve no sale.
- El espacio mínimo para el relleno NOTAS no lo medí. `CI:C2-long-attribute-name-A` p4 deja un hueco abajo a la izquierda sin NOTAS, y no sé si cabía una.

**Lo que leí además de los PDF, y sesgos posibles:**
- `ACTA_CANDIDATO_PREQA9.md`, antes de revisar, por la identidad. `HALLAZGOS`, `CONCILIACION` y `COMPARACION`, al final y solo para contrastar. No los uso como prueba.
- El contexto de la sesión mostraba asuntos de commits, `AGENTS.md` y la memoria del proyecto.
- Código consultado solo por búsqueda y en tramos: `AndroidPcSheetV11PdfProofRenderer.kt` (601–611, 1126–1172, 1612–1650), `AndroidCustomV2ExtendedRenderer.kt` (2750–2762), `CharacterSuccessorDomain.kt` (coincidencias) y `DesktopPcSheetV11RuntimePdfProofTest.kt` (casos `P1-attack-*` y `P4-source-physical-break`). No revisé el código entero.
- Las fichas JSON las traté como datos.

**Scripts e imágenes:** `C:\Users\gms\AppData\Local\Temp\claude\81e26e1f5f1fff5b\af0b7db1-bfd0-437a-87dd-fbd9b773da41\scratchpad\review_preqa9`.
