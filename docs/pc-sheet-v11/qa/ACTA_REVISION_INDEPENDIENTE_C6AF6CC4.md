# Acta de revisión independiente — PDF V11 frente a D-0076 y a los 8 golden

**Resultado en una línea:** la geometría, las fuentes y los colores siguen al golden con mucha fidelidad, pero hay 8 decisiones de D-0076 en NO_CUMPLE y otras 8 sin demostrar; los 8 golden quedan con diferencias que importan.

## 1. Alcance

- **Candidato:** commit c6af6cc4, CI run 42144675231, según lo indicado por quien encargó la revisión. No pude comprobar por mi cuenta que los PDF vengan de ese commit, porque sus metadatos están vacíos (productor, creador y título).
- **Evidencia revisada:** 116 PDF de la App (1.003 páginas) y los 8 golden.
- **Leído completo:** `D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md`, `CONTRATO_VISUAL_APROBADO.md`, `D-0059_RASGOS_DOMAIN.md`, `G4_COMPUERTA_CUMPLIMIENTO_PRE_QA.md`, `LEER_PRIMERO.txt` e `INFORME_V11.md`.
- **Fixtures:** Aldren leído completo; Ilyra y Mara leídos mediante un resumen por script. Además, un script buscó cada texto guardado de los tres fixtures en los PDF Aldren-A, Ilyra-B, Mara-A y Mara-B.
- **Método sobre los PDF:**
  - Scripts PyMuPDF sobre los 116 PDF: familias de página, logo, nombre, número de página, última hoja, fuentes, referencias cruzadas, solapes y textos fuera de margen.
  - Medición de texto y vectores contra el golden, con tolerancia de 0,3 pt.
  - Inspección visual a 70–90 dpi de unas 50 páginas de App y golden, y recortes ampliados de P1.
- **No abrí:** `CONCILIACION_D0076_V11.md`, `COMPARACION_8_GOLDEN.md`, `MATRIZ_CONTRATO_V11_PRE_QA.json`, `qa/evidence`, `REGISTRO_QA_V11.json`, `docs/checkpoints`, `RESUME.md`, git log, tests ni `scripts/audit_*` y `scripts/test_*`.
- **Exposiciones involuntarias que declaro (no las usé como evidencia):**
  - El contexto automático de la sesión mostraba los asuntos de los cinco últimos commits, junto con AGENTS.md y la memoria del proyecto.
  - Una búsqueda devolvió una línea con el nombre de un test en `DesktopPcSheetV11RuntimePdfProofTest.kt`.
  - Un listado de `docs/pc-sheet-v11` mostró nombres de archivos que no abrí.
- **Código fuente consultado** (solo donde el PDF no respondía):
  - `DesktopPcSheetV11PdfProofRenderer.kt`: líneas 574–592, 1605–1625 y 1895–1902, más coincidencias de búsqueda en 754, 1461, 1842, 2811, 3619, 4292 y 5046.
  - `AndroidPcSheetV11PdfProofRenderer.kt`: coincidencias en 1895–2011 y 5046.
  - `CharacterPcSettingsClosureV4.kt`, `DesktopPcManager.kt`, `PcSheetPdfExportFoundation.kt` y `PcSheetV11SemanticContract.kt`: solo coincidencias de búsqueda sobre encuadre del retrato y variante.

## 2. Las 48 secciones de D-0076

Cuando parte de una sección quedó sustituida y el resto sigue vigente, el veredicto es el del resto; la cita está en la fila. Nombres de archivo sin el prefijo `V11-INTERNAL-PROOF-`.

| § | Qué decide | Veredicto | Evidencia |
|---|---|---|---|
| 3.1 | Cabecera completa solo en P1; logo y nombre del PJ en toda página | CUMPLE | Escaneo de los 116 PDF: todas las páginas tienen imagen de logo y nombre en x≈162, también la última. Aldren-A p1–p6. |
| 3.2 | P1 lleva retrato, nombre, jugador, alineamiento, clase/nivel y raza | CUMPLE | Mara-A p1: las seis piezas presentes. El nombre va en la línea del logo, como pide el contrato («No repetir nombre en P1»). Jugador y alineamiento siempre en blanco: ver P5 en §5. |
| 3.3 | Retrato cuadrado, modos Recortar/Ajustar, silueta si no hay imagen | NO_DEMOSTRADO | Silueta vista en Mara-A p1. Ningún PDF lleva retrato (P1 solo tiene la imagen del logo en los 116), así que no hay evidencia de los dos modos. El código ofrece «Recortar para llenar» e «Imagen completa» y el renderer usa `fitMode`; no lo vi producir un PDF. |
| 3.4 | Formato compacto de clase/nivel, raza con subraza, alineamiento completo, Raza/Alineamiento 60/40 | NO_CUMPLE | P1-multiclass-dice-A p1 imprime «Clase 1 1, Clase 2 2, …» con comas, no `Guerrero 5 / Mago 3`. Renderer, línea 1901: `joinToString(", ")`. Medido en Mara-A p1: Raza 175,2 pt y Alineamiento 110,8 pt (61/39). Alineamiento no se puede evaluar: no se guarda. Ver F08. |
| 3.5 | Variantes A y B de la sección izquierda | CUMPLE | Mara-A p1 agrupada, Mara-B p1 con listas independientes y sufijo (XXX). Selector de variante en la UI (código). |
| 3.6 | P1 se adapta a 2–3 atributos personalizados; prioridad de los seis; extensión si no cabe | CUMPLE | Mara-A y Mara-B p1: tres personalizados en P1, el cuarto (Renombre) en C2 p4. dense-six-attributes-A p1: los seis estándar se quedan y los personalizados pasan a p4. No hay PDF con exactamente 6+2. El formato de las habilidades en C2 está en §3.29. |
| 3.7 | Bloque de combate de 3 filas y 8 campos; dados de golpe compactos | NO_DEMOSTRADO | Tres filas y ocho campos correctos en Mara-A p1. 10d8, 5d10 y 5d6 siguen el nivel, como pide la aclaración del 2026-11-05. **Sin PDF con 2–3 clases:** `5d10 / 3d6` no se ve en la tarjeta; con 16 clases sale «VER C2». PV temporales: parte sustituida por «los PV temporales van en pequeño dentro de PV ACTUALES (`TEMP 7`)». |
| 3.8 | Ataques: tabla resumen de 3 columnas, sin alcance ni descripciones por iniciativa propia | NO_CUMPLE | Las tres columnas están. Mara-A p1 añade alcance al nombre («Técnica 1 — Corte de fase / 60 pies») y C2 p4 repite «ALCANCE … 60 pies» y añade «NOTAS DEL ATAQUE». Ver F06. |
| 3.9 | Rasgos sin descripción, en cualquier página | CUMPLE | Búsqueda de todas las `description` y `notes` de rasgos: ninguna aparece en Aldren-A, Ilyra-B, Mara-A ni Mara-B. Formato según la aclaración del 2026-10-16 (`Pericia - Rasgo de Clase: Pícaro`). Rasgos 01–26 de Mara una vez cada uno, en orden, en P1→P2→C2. |
| 3.10 | Reparto dinámico Ataques/Rasgos | SUSTITUIDA | Contrato, 2026-11-03: «ATAQUES tiene 5 filas fijas y RASGOS 7 filas en 2 columnas fijas … Sustituye el reparto dinámico de altura que pedía la regla P112». C2-attack-table-rows-A: 5 filas en P1 y QAROW6–8 en p4. |
| 3.11 | Conjuros en P1 siempre, niveles 1–9, fila épica 10–12 condicional | NO_DEMOSTRADO | Módulo en no lanzador (Aldren-A p1), tres campos y matriz 3×3 correctos. **Sin PDF épico.** En código, P1 dibuja solo `(1..9)` (línea 1461) y los niveles ≥10 pasan a texto C2 (línea 1613), lo que apunta a que la cuarta fila no existe. «ni eliminar las casillas» queda sustituido por «mínimo 0, máximo 4». Ver F15. |
| 3.12 | Filas de 7/5 mm, bandas alternas, casillas utilizables | CUMPLE | Regla de 7 mm sustituida: «sigue el golden … no los 7 mm nominales». Paleta de bandas idéntica al golden. Casillas de 8,8 pt en P1. Uso real a lápiz sin probar (ver §6). |
| 3.13 | Estética base Azul niebla clásico | CUMPLE | Colores de texto y relleno coinciden con el golden en las páginas medidas (#1d384f, #24394b, #264b68, #e7f2f7, #f0f7fa…). |
| 3.14 | Municiones, Tesoro y Otros en evaluación | SUSTITUIDA | §3.15: «La hipótesis previa del §3.14 … queda resuelta en cuanto a ubicación por esta sección». |
| 3.15 | Municiones en P1, hasta 20 casillas por registro, `____ / X` si son más | NO_DEMOSTRADO | 20 casillas en dos subfilas de 10 agrupadas de cinco en Aldren-A p1; 8 casillas en P1-two-ammo-entries-A p1. **Ningún PDF tiene más de 20** (máximo hallado: 20), así que `____ / X` nunca se imprime. |
| 3.16 | Marco C2 y política de fuentes | SUSTITUIDA | Marco C2 presente. Fuentes: contrato, 2026-10-29: «Reemplazan a Barlow Condensed y Fira Sans». Fuentes halladas: NotoSans Regular/Bold, NotoSansDisplay Condensed, EBGaramond12 Bold, EBGaramond Bold, Para Hoja v8. |
| 3.17 | Barlow para rótulos, jerarquía de glifos | SUSTITUIDA | Misma cita que §3.16. La jerarquía de glifos sigue: las marcas usan Para Hoja v8 y no hay Font Awesome. |
| 3.18 | Cartelas C2c; casillas pequeñas sencillas | CUMPLE | Cartelas achaflanadas en P1, P2 y P4; casillas simples sin ornamento. |
| 3.19 | P2: Equipo + Rasgos arriba, Equipo Especial abajo, sin módulo Otros | CUMPLE | Mara-A p2 y Aldren-A p2. La palabra «OTROS» no aparece como módulo. Sin peso ni «Consumible» en Equipo. |
| 3.20 | Tesoro bajo Equipo, monedas custom, filas manuales, flujo por filas | CUMPLE | C206-both-paired-overflow-A p2 y Mara-A p2; ceros reales conservados. El flujo es por columna, como el golden, no por fila; lo justifico con «diseño debe llegar a ser igual (fuente, color, distribución, maquetación) al original». Conflicto elevado: P2 en §5. |
| 3.21 | Equipo Especial híbrido: reales y filas libres | CUMPLE | Aldren-A p2: 4 reales y 4 libres. Mara-A p2: filas de mayor altura, el resto en C2 p6, sin truncar. |
| 3.22 | Casilla ACTIVO, sin premarcar | CUMPLE | Casillas vacías en todas las páginas vistas. Ningún fixture tiene dato de activo. |
| 3.23 | P3 con Rasgos a la derecha | SUSTITUIDA | Contrato, 2026-10-16: «V11 prevalece frente a los pasajes antiguos de D-0076 que ubicaban Rasgos mecánicos en P3: P3 exclusivamente narrativa». Mara-A p3 lo cumple. |
| 3.24 | P4: tres columnas, agrupada por nivel | CUMPLE | Mara-A p7, Ilyra-B p5, P4-dense-cantrips-A p5–p6. Una fila por par conjuro-fuente (Detect Magic dos veces). Marca de preparación sustituida: «no necesita nunca el [X]». |
| 3.25 | Cada encabezado de nivel lleva Nivel, Espacios y casillas de gastados | NO_CUMPLE | Siempre 3 casillas en los 5 PDF medidos, sea cual sea el dato: Mara-A p7 «ESP.4 G.2» y «ESP.2 G.1»; P1-six-real-spell-slots-A p5 «ESP.6 G.4». Ver F01. |
| 3.26 | Franja de referencia superior; encabezado en una línea; sin número fijo de casillas | NO_CUMPLE | Una línea, correcto. Casillas fijas (F01). Además, P4-same-name-distinct-sources-A p5 imprime «SEGÚN FUENTE» en vez de los valores por fuente (F04). |
| 3.27 | Notas C y última hoja manuscrita | CUMPLE | Última hoja de los 116 PDF: NOTAS, renglones en dos columnas y cuadrícula, sin número. 134 de 147 vectores iguales al golden. Sin texto digital. |
| 3.28 | Variante C-N3 con prioridad a lo digital | CUMPLE | Mara-A p8–p9: tituladas, luego generales, «NOTAS MANUSCRITAS» solo si caben; texto de 9 notas sin perder entradas. |
| 3.29 | C2 compartidas; se mantienen los contratos por módulo | NO_CUMPLE | Comparten página (Mara-A p5, Recursos + Rasgos). Pero las habilidades salen como prosa: «Cartografía imposible (SAB) / 8 / Competencia», sin casilla ni signo (96 filas sin signo en los 116 PDF). Ver F05. |
| 3.30 | Ubicación A e inclusión de ataques, atributos y habilidades | NO_CUMPLE | Aldren-A p4: C2 solo de P1 va tras P3, cuando el texto dice «va tras la base 1» (F07). La salvación competente nunca se marca (F02). |
| 3.31 | Prioridades de calidad; Notas aparte | NO_CUMPLE | Notas aparte: correcto. «nada se omite, duplica»: alcance y munición se imprimen dos o tres veces (F06), y hay datos guardados que no llegan al PDF (P4 en §5). |
| 3.32 | Selector automático de disposición | NO_DEMOSTRADO | Salen «DOS COLUMNAS» y «MIXTO» sin intervención del usuario. Pero en código el rótulo depende del título de la página (líneas 576–589), y desde el PDF no se puede demostrar que se comparen candidatas. Familia vertical sustituida: «nunca una columna vertical gigante por defecto». |
| 3.33 | Referencias bidireccionales por módulo con página física | NO_CUMPLE | De 350 «CONTINÚA» comprobadas, todas apuntan a una página posterior que devuelve el aviso; de 362 «VIENE», 12 no tienen aviso recíproco. Además hay «VER C2» sin página y los tramos que siguen dentro de C2 solo avisan en el pie global. Ver F09 y F10. |
| 3.34 | Orden: hoja, C2, Notas, Libro, hoja final | CUMPLE | Mara-B: P1 P2 P3 C2×3 P4 CN3×2 LIB×3 FIN. Ilyra-B igual. La última página siempre es FIN. |
| 3.35 | Sin conjuros no hay lista rápida, decidido por datos y no por clase | NO_DEMOSTRADO | Aldren-A sin P4, y el módulo de P1 sigue. **Ningún PDF con no lanzador y conjuros de raza o dote** (ninguno de los que muestran Guerrero en P1 lleva P4), así que no hay evidencia de «NO decidir la omisión por nombre/clase». |
| 3.36 | P3 sin Rasgos pendientes: reserva compacta a la derecha | SUSTITUIDA | Misma cita que §3.23. |
| 3.37 | Sin Equipo: reserva amplia | CUMPLE | C379-empty-sheet-A p2 y P2-empty-equipment-treasure-A p2: 13 filas × 2, igual que el golden. |
| 3.38 | Sin Ataques: reserva amplia en P1 | NO_DEMOSTRADO | **Ningún PDF tiene cero ataques.** El mínimo es 1, en P1-attack-multiline-A; «C379-empty-sheet» conserva 2. |
| 3.39 | Sin Municiones: una entrada manual | CUMPLE | Mara-A p1 y C379-empty-sheet-A p1: «Tipo:» con 20 casillas vacías, como el golden. |
| 3.40 | P3 vacía se conserva | CUMPLE | C379-empty-sheet-A p3: 135 de 170 vectores iguales al golden; sin texto inventado. La zona de Rasgos a la derecha queda sustituida (cita de §3.23). |
| 3.41 | Tesoro sin monedas: mínimo de celdas manuales | CUMPLE | C379-empty-sheet-A p2: 5 × 2 celdas, igual al golden. La ampliación condicional no se observa; el golden tampoco la hace. |
| 3.42 | Equipo Especial sin objetos: rótulos mixtos, ACTIVO sin marcar | CUMPLE | Aldren-no-special-A p2: Cuello, Cuerpo, Mano izquierda, Mano derecha y 4 filas libres. Lista fija en la línea 5046 del renderer. Difiere de la lista del golden (G-e). |
| 3.43 | P2 con todo vacío: reservas separadas | CUMPLE | C379-empty-sheet-A p2: Equipo, Tesoro, Rasgos y Especial vacíos en el mapa habitual. |
| 3.44 | Sin notas digitales: sin página C-N3 | NO_DEMOSTRADO | **Los 116 PDF llevan C-N3**, también «C379-empty-sheet». El caso nunca se ejerce. |
| 3.45 | P3 solo texto, sin imágenes de Trasfondo | CUMPLE | Las 140 páginas de la familia P3 tienen solo la imagen del logo. Los fixtures no llevan imágenes de Trasfondo, así que «silueta aunque existan» (2026-11-05) no se ejerce. |
| 3.46 | Libro en dos columnas con fichas variables | CUMPLE | BK06-school-with-school-A p8, BK05-long-card-A p8–p9. Revisado solo en muestras. |
| 3.47 | Ficha esencial: nombre, nivel, escuela y descripción íntegra | CUMPLE | Tiempo, alcance y duración no aparecen. Descripciones de Ilyra-B y Mara-B encontradas íntegras. «EVOCACIÓN» solo en Fire Bolt con escuela; ninguna línea sin escuela. |
| 3.48 | Ficha entera a la columna siguiente; partir solo si excede | CUMPLE | BK04-whole-next-column-A p8: Light pasa entera a la columna derecha. BK05-long-card-A p8–p9: ficha partida con «CONTINUACIÓN: Fire Bolt», de COMIENZO a FIN. Solo se probó un párrafo único. |

**Recuento:** CUMPLE 26 · NO_CUMPLE 8 · NO_DEMOSTRADO 8 · SUSTITUIDA 6.

## 3. Los ocho golden

Criterio: CUMPLE solo si no queda ninguna diferencia relevante sin respaldo de una decisión escrita. Ningún PDF de la App usa los datos de un golden. Diferencias comunes (detalle en F11): orden de habilidades (G-a), atributos personalizados sin mayúsculas (G-b), títulos C2 distintos (G-c), barra lateral en C2 (G-d) y rótulos de Equipo Especial (G-e).

Diferencias ya respaldadas por escrito y que no cuento: logo y sin escudo (2026-10-30), fila de atributo (2026-11-01), número de página solo (C340), avisos «CONTINÚA EN PÁG.» (C341), casillas de P1 según datos.

| Golden | PDF de App comparados | Veredicto | Diferencias por familia |
|---|---|---|---|
| `v11_personaje_casi_vacio` | C379-empty-sheet-A; -B solo por texto | NO_CUMPLE (menor); caso vacío NO_DEMOSTRADO | **Secuencia:** 4 páginas contra 6; C379 no está vacío (2 ataques, acciones, recursos, nota), por eso añade C2 y C-N3. **P1:** 239/297 vectores iguales ±0,3 pt; G-a; línea en vez de 3 casillas (respaldado). **P2:** 201/255; desplazamiento de 0,3–0,4 pt en filas bajas de Rasgos; G-e. **P3:** 135/170. **FIN:** 134/147. |
| `v11_ordinario_6mas2_variante_A` | Sin fixture 6+2; más cercanos Mara-A, Aldren-A | NO_CUMPLE | **Secuencia:** golden sin C2; todos los PDF de la App llevan al menos una C2. **P1:** G-a, G-b. **P3:** 135/170 frente a Aldren-A p3. **FIN:** 134/147. **Libro:** índice no presente en el golden. |
| `v11_ordinario_6mas2_variante_B` | Sin fixture 6+2; más cercanos Mara-B, Ilyra-B | NO_CUMPLE | **P1:** G-a dentro de las listas; G-b; leyenda «COMPETENCIA / PERICIA» en lugar de «dos subcolumnas». Resto como la fila anterior. |
| `v11_ordinario_6mas3_variante_A` | Mara-A (6+4, 3 en P1) | NO_CUMPLE | **P1:** G-a, G-b; tres últimos atributos sin mayúsculas. **P2:** Equipo y Tesoro iguales en gramática; Mara-A prefiere 2 subcolumnas por nombres largos. **P4:** 324/365 vectores iguales; rótulo de fuente con nombre propio (F03). **C-N3:** cartelas iguales. **Secuencia:** con C2 y sin Libro en Mara-A. |
| `v11_ordinario_6mas3_variante_B` | Mara-B | NO_CUMPLE | **P1:** G-a, G-b; leyenda añadida. **P2:** 190/272 vectores iguales. **Libro:** Mara-B añade índice. **FIN:** igual en estructura. |
| `v11_extremo_variante_A` | Mara-A, C2-grouped-custom-attributes-A, C206-both-paired-overflow-A, P3-long-story-heading-A | NO_CUMPLE | **C2:** G-c, G-d; golden p4 mezcla Atributos, Ataques, Municiones y Equipo en una hoja, la App separa siempre Equipo/Tesoro en páginas «MIXTO» (Mara-A p6). **Historia:** módulo C2 en el golden p6, páginas P3 propias en la App. **Municiones >20:** sin evidencia. **P4:** 3 casillas como el golden, contra §3.26 (F01). |
| `v11_extremo_variante_B` | Mara-B, mismos proofs -B | NO_CUMPLE | Igual que la fila anterior. Golden con 2 páginas C2; Mara-B con 3 (datos distintos). Rótulo «HABILIDADES DE HONOR» del golden sin equivalente en la App. |
| `v11_comparador_C2_extremo` | Todas las páginas C2 vistas | NO_CUMPLE | **Títulos:** G-c. **Filas de habilidad:** F05. **MIXTO:** el golden usa ancho completo para Ataques (p4); la App reserva MIXTO a Equipo/Tesoro y usa dos columnas para ataques (C2-attack-table-rows-A p4). **NOTAS:** cuadrículas de relleno iguales en gramática. |

**Recuento:** CUMPLE 0 · NO_CUMPLE 8 (uno, además, con el caso vacío sin demostrar). Las familias P2, P3, P4 y FIN están muy cerca del golden; la mayor distancia está en C2.

## 4. Hallazgos, de mayor a menor gravedad

### F01 — ALTA — P4 dibuja siempre 3 casillas de gastados
- **Fuente:** §3.25 «No inventar números de casillas ni marcas iniciales»; §3.26 «no asumir número fijo de casillas»; contrato «No inventar slots o progresiones de ejemplo». `INFORME_V11.md` avisa que las tres del golden «no son una progresión de slots validada».
- **Impreso:** Mara-A p7, NIVEL 1 «ESP.4 G.2» y NIVEL 5 «ESP.2 G.1», ambos con 3. P1-six-real-spell-slots-A p5, «ESP.6 G.4» con 3. También 3 en P1-real-four-five-slot-boxes-A p5 («ESP.5 G.3»), Ilyra-B p5 y P4-intmax-real-spell-slots-A p7.
- **Debe imprimirse:** una casilla por espacio real, como ya hace P1 (Mara-A p1: 4, 3, 3, 3 y 2).

### F02 — ALTA — La competencia en tirada de salvación nunca se marca
- **Fuente:** §3.30 «Habilidades y Tiradas de Salvación: … bonificadores y estados de competencia/pericia»; contrato 2026-11-01 «la casilla de competencia»; 2026-10-30 «competencia ✓ U+E108».
- **Impreso:** Aldren-A p1, FUERZA, fixture `proficient: true`, total +7, casilla vacía. Ningún PDF lleva marca en esas casillas. El renderer solo usa `savingThrowTotal` en sus coincidencias de búsqueda.
- **Debe imprimirse:** U+E108 en la casilla cuando el dato sea verdadero; vacía si no.

### F03 — ALTA — Fuentes de clase rotuladas con el nombre propio de la fuente
- **Fuente:** contrato 2026-10-26 «sin nombres específicos, salvo la **Clase**, que se rotula con el nombre de la clase **sin subclase**»; lista confirmada el 2026-10-28.
- **Impreso:** Mara-A p1 y p7, «Cronomancia umbral» en cada fila y tarjeta. El fixture dice `originKind: CLASS`, enlazada a la clase «Cronomante del Umbral». P4-same-name-distinct-sources-A p5 imprime «QA FUENTE HOMÓNIMA (#7a2000)», con un fragmento de identificador interno. P4-source-physical-break-A p5 imprime «Fire Bolt | QAP4SOURCEFIRST …» en la celda del nombre.
- **Debe imprimirse:** «Cronomante del Umbral»; nunca identificadores; dos fuentes del mismo tipo «pueden verse con el mismo rótulo».

### F04 — ALTA — P4 oculta los valores por fuente con «SEGÚN FUENTE»
- **Fuente:** contrato 2026-10-28 «solo si aun así no cabe, `SEGÚN FUENTE`. P4 conserva siempre el detalle»; §3.26 «conservar su asociación correcta».
- **Impreso:** P4-same-name-distinct-sources-A p5 y P4-source-physical-break-A p5, y sus variantes B, en las tres tarjetas de referencia. P1 de esos PDF también dice «SEGÚN FUENTE». Una búsqueda en todas las páginas no halló los valores por fuente: esos datos no están en el PDF.
- **Debe imprimirse:** Aptitud, CD y Ataque de cada fuente en P4, ajustando la composición.

### F05 — MEDIA — Habilidades en C2 como prosa, sin casilla ni signo
- **Fuente:** contrato 2026-10-28 «el español con la gramática del golden en C2 (casilla con P/E …)»; §3.29 «Mantener los contratos por módulo»; golden extremo A p4 (casilla, nombre, «+1»).
- **Impreso:** Mara-A p4, «Cartografía imposible (SAB) / 8 / Competencia». Bonificador sin signo en las 96 filas detectadas. Cuando la habilidad acompaña a su atributo sale «Habilidad Manipulación de éter», con prefijo que el golden no usa (C2-grouped-custom-attributes-A p4).
- **Debe imprimirse:** fila de golden: casilla con el estado, nombre con (XXX) si va separada del atributo, «+8».

### F06 — MEDIA — Alcance añadido a Ataques y datos repetidos
- **Fuente:** §3.8 «sin … agregar columnas de alcance, propiedades, descripciones extensas o efectos por iniciativa propia»; §3.31 «nada se omite, duplica»; §3.10 «sin duplicaciones».
- **Impreso:** Mara-A p1 «Técnica 5 — Corte de fase / 60 pies» y p4 «ALCANCE Técnica 5 — Corte de fase: 60 pies». Aldren-A p1 «Ballesta ligera / 80/320 pies» y p4 «ALCANCE Ballesta ligera: 80/320 pies»; en cambio «Espada larga / 5 pies» no se repite. Munición: Aldren-A p1, Equipo p2 y «MUNICIÓN Virotes | CANTIDAD 20 | NOTAS …» p4. El golden imprime solo el nombre en la primera columna.
- **Debe imprimirse:** nombre, bonificador y daño. Si alcance y notas deben conservarse (P6 en §5), una sola vez.

### F07 — MEDIA — C2 solo de P1 detrás de P3
- **Fuente:** §3.30 «una que continúa exclusivamente datos de primera página va tras la base 1».
- **Impreso:** Aldren-A: P1 P2 P3 C2 CN3 FIN, con C2 p4 solo de P1. Todo PDF deja C2 tras P3, igual que el golden.
- **Debe imprimirse:** según el texto, P1 C2 P2 P3; pero ver P1 en §5.

### F08 — MEDIA — Multiclase con comas y entrada cortada
- **Fuente:** §3.4 «`Guerrero 5 / Mago 3`».
- **Impreso:** P1-multiclass-dice-A p1 «…, Clase 9 9, Clase 10»; la continuación C2 p4 empieza por «10, Clase 11 11…», separando el nombre de su nivel. Es una prueba de estrés de 16 clases; la coma viene de la línea 1901 y también afectaría a dos clases.
- **Debe imprimirse:** `Guerrero 5 / Mago 3`, cortando entre clases.

### F09 — MEDIA — «VER C2» sin número de página
- **Fuente:** §3.33 «la página física donde continúa»; contrato C341.
- **Impreso:** P1-long-character-name-A p1–p6 («Aldren NOMBREQA01 … NOMB / VER C2», con el texto muy próximo a «VARIANTE A»); P1-multiclass-dice-A p1 (dados de golpe); P1-long-spell-slots-A p1 y P4-intmax-real-spell-slots-A p1 (N1).
- **Debe imprimirse:** «CONTINÚA EN PÁG. n». En el nombre, el texto completo existe en p4; falta la referencia con número.

### F10 — BAJA — Referencias incompletas en C2 y en pies
- **Fuente:** §3.33 «cada tramo que tenga continuación informa dónde sigue»; «No basta una única referencia global».
- **Impreso:** Mara-A p4: Recursos sigue en p5 y solo lo dice el pie global. En 42 páginas el pie nombra una sola página aunque la banda nombre varias (Mara-A p2: banda 5 y 6, pie 5). En 12 páginas hay «VIENE DE PÁG. 2» sin el aviso recíproco en p2 (C206-both-paired-overflow-A p6, C2-long-special-item-A p8); §3.33 admite volver al origen, pero la ida queda sin nombrar esa hoja.
- **Debe imprimirse:** aviso por módulo también en tramos de C2, y pie con todas las páginas.

### F11 — BAJA a MEDIA — Diferencias con el golden sin respaldo escrito
- **G-a, orden de habilidades.** Golden: Medicina, Percepción, Perspicacia, Supervivencia, Trato con Animales; Engaño, Interpretación, Intimidación, Persuasión. App (Aldren-A p1, Mara-A p1): Trato con Animales, Perspicacia, Medicina, Percepción, Supervivencia; Engaño, Intimidación, Interpretación, Persuasión. La App sigue el orden de las claves en inglés.
- **G-b, atributos personalizados.** Golden «SUERTE», «CORDURA», «HONOR»; App «Fortuna», «Cordura», «Éter» junto a «FUERZA» en mayúsculas.
- **G-c, títulos C2.** Golden «ATRIBUTOS Y HABILIDADES», «ATAQUES», «MUNICIONES», «HISTORIA». App «PERSONAJE, HABILIDADES Y ACCIONES», «ATAQUES, NOTAS Y ESPACIOS REALES», «HABILIDADES Y REFERENCIAS», «RECURSOS Y MUNICIONES», y «RASGOS / CONTINUACIÓN P2 / CONTINUACIÓN» (P2-only-traits-C2-A p4). «ESPACIOS REALES» suena a jerga técnica.
- **G-d, barra lateral.** Filas de atributo en C2 con barra #527c96 en x36,7–38,2, fuera del margen de contenido (39,69) y ausente en el golden (Mara-A p4).
- **G-e, Equipo Especial.** Rótulos preimpresos: golden Cabeza, Rostro, Cuello, Mano izquierda, Mano derecha, Pecho, Piernas, Libre; App 4 rótulos y 4 filas en blanco. Con objetos, el golden completa con Pecho, Piernas, Libre; la App deja vacías las filas libres.
- **Índice del Libro.** Una página que no está en el golden (Ilyra-B p7). El contrato C341 lo menciona como existente, pero no hay decisión que lo cree.
- **Secuencia ordinaria.** Golden sin C2; la App siempre lleva C2, por acciones y recursos.

### F12 — BAJA — «Letra pequeña» de recursos no es pequeña
- **Fuente:** contrato 2026-10-28 «debajo, recuperación, fuente y notas en letra pequeña».
- **Impreso:** Mara-A p4, 7,2 pt tanto en «Reserva 1: Éter» como en «Recupera: …».
- **Debe imprimirse:** un cuerpo menor que el del nombre.

### F13 — BAJA — Medida de cabecera distinta a la del contrato
- **Fuente:** contrato 2026-10-30 «línea de cabecera en y717».
- **Impreso:** regla en pdfY 709,8 (Mara-A p1), igual que el golden; ninguna regla horizontal cerca de y717.
- **Debe resolverse:** corregir el contrato o la hoja; el propietario se reservó el control de este ajuste.

### F14 — BAJA, técnica — El logo arrastra una hoja entera
- Cada página incrusta una imagen de 1224×1584 con toda la hoja antigua del propietario, recortada para mostrar solo el logo. No viola ninguna decisión; el contenido oculto sigue dentro del PDF (Mara-A: 298.678 bytes). Conviene incrustar solo el logo.

### F15 — RIESGO, sin PDF — Fila épica aparentemente ausente
- **Fuente:** §3.11 «agregar **solo una cuarta fila** con **niveles 10, 11, 12**».
- **Observado solo en código:** dibujo `(1..9)` en la línea 1461 y texto «ESPACIOS REALES NIVEL n» en C2 en la línea 1613. No es un NO_CUMPLE demostrado, pero es la razón de no confiar en §3.11.

## 5. Preguntas que solo puede responder el propietario

- **P1. Posición de C2.** §3.30 dice que una C2 solo de P1 va tras la base 1. El golden aprobado después siempre la pone tras P3. Por la precedencia dada lo marqué NO_CUMPLE, pero el propietario aprobó ese orden visualmente: ¿manda el texto o el golden?
- **P2. Flujo de Tesoro.** §3.20 dice «continuar en la FILA SIGUIENTE»; golden y App llenan por columna. ¿Basta «igual … al original» del 2026-10-28 para dejarlo así?
- **P3. Niveles sin espacios en P4.** Golden: «ESP. ___» con 3 casillas. §3.26: sin número fijo. ¿Qué se imprime en un nivel sin espacios: línea, nada o 3 casillas para uso manual?
- **P4. Datos guardados que no llegan a ningún PDF.** §3.31 dice «nada se omite»; §7 dice que no se creen módulos sin petición expresa. Hallado en los fixtures:
  - Competencias e idiomas (`proficiencies`), defensas, sentidos, movimientos, efectos temporales.
  - Marcadores personalizados, opciones de clase (`classOptions`), progreso de hito.
  - Salvaciones contra la muerte y nivel de agotamiento: no encontré esos rótulos en ningún PDF revisado. Solo se buscó el texto en Aldren-A, Ilyra-B, Mara-A, source-tabs-mechanical-A y los golden.
  - Subclase, notas de atributos y habilidades personalizadas, notas y ubicación de equipo ordinario, usos y activación de rasgos.

  ¿Se imprimen, o queda aceptado que son solo de la App?
- **P5. Jugador y alineamiento.** No están en los fixtures, y el renderer dice que el alineamiento no se guarda. El golden los muestra rellenos; la App deja un renglón en blanco. ¿Aceptado?
- **P6. Alcance y notas de ataque.** §3.8 no quiere alcance por iniciativa propia, pero §3.31 no quiere omisiones. ¿Se imprimen en C2, o no se imprimen?
- **P7. Ataques con bonificador que no son de tipo ATTACK.** Mara: Técnica 3 (acción adicional, +8, 4d6 + 4) va a la tabla ACCIONES de C2 mientras P1 deja 3 filas de ATAQUES vacías. ¿Debe entrar en ATAQUES?
- **P8. Más de 4 casillas en P1.** El contrato dice «máximo 4». La App dibuja 5 (8,0 pt) y 6 (6,5 pt) si el dato lo pide (P1-real-four-five-slot-boxes-A y P1-six-real-spell-slots-A p1). ¿Correcto?
- **P9. Continuación de Historia.** Golden: módulo en C2. App: páginas P3 propias, según un detalle «del Worker (no dichos por el propietario)». ¿Se confirma?
- **P10. Fila épica.** Si la App no gestiona reglas épicas, ¿sigue siendo exigible la cuarta fila antes del QA?

## 6. Límites de esta revisión

- **Origen de los PDF:** son «INTERNAL-PROOF» de CI. No pude distinguir Android de Desktop ni comprobar la salida real de Guardar/Compartir que exige G4. Todas las páginas de proof llevan al pie «PRUEBA INTERNA V11 - NO APROBADA»; no vi el pie de una exportación definitiva.
- **Casos sin ningún PDF:** retrato real, multiclase de 2–3 clases, municiones >20, cero ataques, sin notas digitales, niveles épicos, no lanzador con conjuros de raza o dote, 6+2 exacto, personaje totalmente vacío, valores reales de jugador y alineamiento. Tampoco los comprobé: rasgo sin fuente, tipo Don, objeto con estado ACTIVO real.
- **No hay Aldren-B ni Ilyra-A,** ni `P2-empty-equipment-treasure-B`.
- **Revisión delgada:** vi con detalle unas 50 páginas de 1.003; el resto solo pasó por scripts. `source-tabs*`, `C209`, `C210`, `C285` y `C386` apenas se miraron. C379-empty-sheet-B no se vio a imagen. El Libro y la paginación de C-N3 largas se revisaron por muestras.
- **Sin impresión física:** no hay prueba a lápiz, tamaño real, escala de grises ni contraste impreso.
- **Fuentes:** PyMuPDF trunca los nombres a 24 caracteres; identifico «NotoSansDisplay-Condense…» por ese prefijo. Las medidas son del PDF y el golden tiene un solo cuerpo de datos ficticios.
- **Comprobación de texto:** la búsqueda de datos ignora espacios y exige la cadena completa, así que puede fallar con texto repartido en columnas. Los casos que cito los confirmé por lectura (por ejemplo, «Técnica 4 — Descarga prismática» sí se imprime, partida). Un dato corto puede «aparecer» por coincidencia con otro texto.
- **Solapes:** el detector solo halló marcas de casilla junto a su texto, en las que no vi choque real; no sustituye a mirar cada página.
