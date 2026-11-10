# Acta de revisión independiente — candidato V11 `0.5.0-preqa.12`, build 51200

**Resultado en una línea:** mejora frente a `preqa.11` (orden del Equipo, subtítulos de habilidades, filetes de ATAQUES, «dos subcolumnas», nombre largo, fuente «Clase») y no se pierde ningún dato, pero **todavía no está listo para el QA del propietario**: aparece una regresión (huecos en blanco sin cuadrícula NOTAS en hojas C2), sigue el Equipo Especial en hoja propia cuando cabría en la anterior, y el Libro pierde el nombre del conjuro al continuar una ficha de nombre largo.

**Recuentos:** secciones D-0076: CUMPLE 38 · NO_CUMPLE 3 · NO_DEMOSTRADO 1 · SUSTITUIDA 6. Golden: COINCIDE 0 · DIFERENCIAS_JUSTIFICADAS 5 · NO_COINCIDE 3. QA01–QA08 y GLB12: 5 demostradas con salvedades · 4 no demostradas. Hallazgos nuevos: 9 (0 altos, 3 medios, 6 bajos). Revisión anterior: de M1–M13, 8 cerrados y 5 abiertos o cerrados en parte (M2, M4, M5, M9, M12); §3.29, §3.32 y §3.12 siguen (detalle en la sección 4).

**Fecha:** 2026-11-07. **Commit revisado:** `07e5aca0e1b9add6457d0be2c36a6c407617c94e`. Comprobé con `git diff --stat 07e5aca0 HEAD` que lo posterior (`12b0b53e`) solo cambia 4 archivos de `docs/`. El APK de CI y la copia para el propietario tienen el mismo SHA-256 `9f16b03b…0cd9`. En el commit, `versionName 0.5.0-preqa.12` y `versionCode 51200`. No modifiqué nada del repositorio salvo esta acta.

**Abreviaturas:** `A:` es un PDF generado por el APK en el emulador (`android_pdfs\cand12`). `CI:` es un PDF de CI (Desktop) sin el prefijo `V11-INTERNAL-PROOF-`. «p4» es la página física 4.

---

## 1. Las 48 secciones de D-0076

**Recuento: CUMPLE 38 · NO_CUMPLE 3 · NO_DEMOSTRADO 1 · SUSTITUIDA 6.**

Si una parte de la sección quedó sustituida por una decisión posterior y el resto sigue vigente, juzgo el resto.

| § | Qué pide | Veredicto | Evidencia |
|---|---|---|---|
| 3.1 | Logo y nombre en toda página | CUMPLE | Script propio: 1.459 de 1.459 páginas (13 PDF de Android y 171 de CI) con la imagen del logo en x57–147 y el nombre arriba. Número de página correcto en todas menos la última; la última sin número |
| 3.2 | Piezas de la cabecera de P1 | CUMPLE | `A:Mara-A` p1. Jugador y alineamiento con línea a mano (decisión 2026-11-06) |
| 3.3 | Retrato: recortar, ajustar, silueta | CUMPLE | `A:Portrait-CROP-A` p1: imagen 600×300 dibujada a 123,1×61,6 y recortada al marco. `A:Portrait-FIT-A` p1: 69,4×34,7, entera, con márgenes. Silueta en `A:Mara-A` p1. No probé imagen inaccesible |
| 3.4 | Clase/nivel, raza, alineamiento | CUMPLE | `Cronomante del Umbral 10` (`A:Mara-A` p1); multiclase `Clase larga 1 1 / … / Clase larga 6 1` y el resto en C2 (`A:Stress-A` p1 y p4). Subraza solo en CI (`CI:subrace-A`), como en `preqa.11` |
| 3.5 | Variantes A y B | CUMPLE | `A:Mara-A` p1 y `A:Mara-B` p1 |
| 3.6 | 2–3 atributos propios en P1; prioridad de los seis; grupos juntos | CUMPLE | `A:Mara-A` p1: FORTUNA, CORDURA, ÉTER con sus habilidades. p4: RENOMBRE y «HABILIDADES DE SABIDURÍA» / «HABILIDADES DE INTELIGENCIA» con su subtítulo |
| 3.7 | Combate: 3 filas, 8 campos | CUMPLE | `A:Aldren-A` p1 `5d10`. `TEMP 7` dentro de PV ACTUALES en `A:Mara-A` p1 (decisión 2026-10-28). `A:Stress-A` p1 «CONTINÚA EN PÁG. 4» y p4 con los 16 dados |
| 3.8 | Ataques: tabla de 3 columnas | CUMPLE | `A:Mara-A` p1: las 4 entradas con bono (Técnica 1, 3, 5, 7) con alcance. `A:Stress-A` p1: 5 filas, nombre largo en dos renglones; p4 sigue en tabla |
| 3.9 | Rasgos sin descripción | CUMPLE | En los PDF de Android: 0 frases de 5 palabras de las descripciones de rasgo (84 a 624 comprobadas por ficha). Forma `Nombre - Rasgo de Clase: fuente` |
| 3.10 | Reparto dinámico Ataques/Rasgos | SUSTITUIDA | Contrato 2026-11-03: 5 y 7 filas fijas. Comprobado en `A:Aldren-A` p1 |
| 3.11 | Conjuros en P1 siempre; fila épica condicional | CUMPLE | `A:Aldren-A` p1 sin N10. `A:Aldren-epic-A` p1 con N10–N12 en `____ / ____`, sin casillas. Entre las dos solo cambia la zona x276–568 × y606–657 (diferencia de píxeles) |
| 3.12 | Filas, bandas, casillas utilizables, gris | NO_DEMOSTRADO | Los tonos de las bandas son los del golden: #E7F2F7 y #F0F7FA, medidos en P2 de los dos. Pero no hay prueba impresa ni en gris |
| 3.13 | Azul niebla clásico | CUMPLE | Mismos colores que el golden en las páginas comparadas |
| 3.14 | Municiones, Tesoro, Otros en evaluación | SUSTITUIDA | Por §3.15 |
| 3.15 | Municiones hasta 20; más, `____ / X` | CUMPLE | `A:Aldren-A` p1: Virotes con 20 casillas. `A:Stress-A` p1: `___ / 40` |
| 3.16 | Marco C2 y fuentes | SUSTITUIDA | Fuentes del golden (2026-10-29). Solo hay Noto Sans, Noto Sans Display Condensed Bold, EB Garamond 12 Bold (y EB Garamond Bold para letras que faltan) y Para Hoja v8. Todas incrustadas |
| 3.17 | Barlow y glifos | SUSTITUIDA | Misma decisión. Marcas de estado con Para Hoja v8 |
| 3.18 | Cartelas C2c, casillas simples | CUMPLE | Todas las páginas vistas |
| 3.19 | P2: Equipo, Rasgos, Especial; sin Otros | CUMPLE | `A:Mara-A` p2, `A:Aldren-A` p2 |
| 3.20 | Tesoro bajo Equipo, monedas propias | CUMPLE | `A:Stress-A` p2 con QAMONEDA10–14 y p4 con 15–17. Ceros reales impresos |
| 3.21 | Especial híbrido, sin truncar | CUMPLE | `A:Mara-A` p2: filas altas con «/ NOTAS:». Detalle de rótulos en H7 |
| 3.22 | Casilla ACTIVO sin premarcar | CUMPLE | Vacía en todos los PDF vistos |
| 3.23 | P3 con Rasgos a la derecha | SUSTITUIDA | «P3 exclusivamente narrativa» (2026-10-16) |
| 3.24 | P4 en tres columnas por nivel | CUMPLE | `A:Mara-A` p7, `A:Stress-A` p6. Conjuro con dos fuentes en dos filas (Detect Magic, Misty Step) |
| 3.25 | Nivel, espacios y casillas en cabecera | CUMPLE | `A:Mara-A` p7: 4,3,3,3,4,2,2,1,1 casillas, `ESP.4 G.2`, `ESP. ___` sin dato |
| 3.26 | Referencia superior; cabecera en una línea | CUMPLE | Una línea en todas las vistas. Valor por fuente en `A:Stress-A` p6 |
| 3.27 | Notas y última hoja | CUMPLE | Última hoja igual al golden y sin número en los 184 PDF |
| 3.28 | C-N3 con prioridad digital | CUMPLE | `A:Mara-A` p8–p9: 9 notas tituladas y luego la general |
| 3.29 | C2 compartidas; contratos por módulo | **NO_CUMPLE** | Equipo y Equipo Especial ya comparten hoja en `A:Mara-B` p5. Pero hay huecos en blanco sin cuadrícula (H1) y Especial en hoja propia cuando cabría (H2) |
| 3.30 | Ubicación e inclusión de ataques, atributos, habilidades | CUMPLE | C2 tras P3, como el golden extremo. Ataques en tabla en C2 (`A:Stress-A` p4) |
| 3.31 | Prioridades; Notas aparte | CUMPLE | Sin pérdidas ni duplicados (QA01). C-N3 solo con notas |
| 3.32 | Probar familias y elegir la mejor | **NO_CUMPLE** | `A:Stress-A` p5 es una hoja con 6 objetos especiales y cuadrícula. Con una distribución mixta todo cabe en p4 (cálculo en H2) |
| 3.33 | Referencias de ida y vuelta | CUMPLE | Script propio: 99 referencias en los 13 PDF de Android; cada «CONTINÚA EN PÁG. n» tiene su «VIENE DE PÁG.» en la página n, y al revés. Detalles en H4 y H5 |
| 3.34 | Orden: hoja, C2, Notas, Libro, final | CUMPLE | `A:Mara-B`: P1 P2 P3 C2×2 P4 CN3×2 Libro×2 final |
| 3.35 | Sin conjuros no hay P4 | CUMPLE | `A:Aldren-A` sin P4 |
| 3.36 | P3 con reserva de Rasgos | SUSTITUIDA | Igual que §3.23 |
| 3.37 | Sin Equipo: reserva amplia | CUMPLE | `A:new-character-A` p2, igual que el golden casi vacío p2 |
| 3.38 | Sin Ataques: reserva | CUMPLE | `A:new-character-A` p1: 5 filas, ya sin filetes en filas vacías |
| 3.39 | Sin Municiones: una entrada | CUMPLE | `A:Mara-A` p1 «Tipo: ____» con 20 casillas |
| 3.40 | P3 vacía se conserva | CUMPLE | `A:new-character-A` p3 |
| 3.41 | Tesoro sin monedas: celdas | CUMPLE | `A:new-character-A` p2: las 5 monedas de la App con 0 y celdas a mano |
| 3.42 | Especial vacío: rótulos mixtos | CUMPLE | `A:new-character-A` p2: los 8 rótulos del golden |
| 3.43 | P2 toda vacía: reservas | CUMPLE | `A:new-character-A` p2 |
| 3.44 | Sin notas no hay C-N3 | CUMPLE | `A:new-character-A`: 4 páginas, como el golden |
| 3.45 | P3 solo texto | CUMPLE | Única imagen en P3: el logo |
| 3.46 | Libro en dos columnas | CUMPLE | `A:Mara-B` p9–p10, `A:Stress-A` p8–p11 |
| 3.47 | Ficha esencial | CUMPLE | Sin tiempo, alcance ni duración. Escuela en `CI:BK06-school-with-school-A` p7: «EVOCACIÓN», condensada 7 pt, #657d90, como el golden. Ninguna ficha de Android tiene escuela en sus datos |
| 3.48 | Ficha entera a otra columna; ficha larga partida e identificada | **NO_CUMPLE** | La ficha entera sí pasa (`CI:BK04-whole-next-column-A`). Pero la ficha larga de `A:Stress-A` p9 y p10 sigue con el rótulo «CONTINUACIÓN» **sin el nombre del conjuro** (H3) |

---

## 2. Los ocho golden

**Recuento: COINCIDE 0 · DIFERENCIAS_JUSTIFICADAS 5 · NO_COINCIDE 3.**

Comprobé los golden: el ZIP del repositorio tiene el SHA-256 `35a1c6ea…364e6a` que registra `ARTEFACTOS_Y_PROCEDENCIA.md`. Lo descomprimí y los 8 PDF son byte a byte los del scratchpad (y los del manifiesto del ZIP). Ningún PDF de la App usa los datos del golden: comparo forma y gramática.

**Diferencias que no cuento, porque tienen decisión escrita del propietario:** logo sin escudo; número de página solo y última hoja sin número; «CONTINÚA EN PÁG.» / «VIENE DE PÁG.»; fila de atributo con «TIRADA DE SALVACIÓN»; casillas de P1 y P4 con `gastados/total`; `TEMP 7`, inspiración, `35 pies · 10,5 m`; nombre y alcance en ATAQUES; ataque largo en dos renglones; Historia en C2; sin índice del Libro; tipo de fuente en P4; forma `Nombre - Tipo: fuente` de los rasgos; nombres largos de Equipo con sangría; notas de objeto especial impresas; nombre largo cortado; nada a la derecha en hojas de continuación. El pie «PRUEBA INTERNA V11 - NO APROBADA» es la marca de compilación de prueba; no lo cuento.

| Golden | Pares vistos | Veredicto | Lo que queda sin respaldo |
|---|---|---|---|
| `v11_personaje_casi_vacio` (4 p) | `A:new-character-A` p1–p4 | DIFERENCIAS_JUSTIFICADAS | Las filas vacías de ATAQUES ya no llevan filetes. Tesoro con las 5 monedas en 0 porque la App las crea |
| `v11_ordinario_6mas2_variante_A` (7 p) | `A:Ilyra-A` p1–p7, `A:Aldren-A` p1–p6 | DIFERENCIAS_JUSTIFICADAS | Gramática de P1, P2, P3, P4, Notas y final como el golden |
| `v11_ordinario_6mas2_variante_B` (7 p) | `A:Aldren-B` p1, `A:Mara-B` p1 | DIFERENCIAS_JUSTIFICADAS | Ya lleva «dos subcolumnas» |
| `v11_ordinario_6mas3_variante_A` (7 p) | `A:Mara-A` p1, p2, p3, p7 lado a lado | DIFERENCIAS_JUSTIFICADAS | Todo con decisión |
| `v11_ordinario_6mas3_variante_B` (7 p) | `A:Mara-B` p1 lado a lado | DIFERENCIAS_JUSTIFICADAS | Todo con decisión |
| `v11_extremo_variante_A` (11 p) | `A:Mara-A` p1–p7 lado a lado | NO_COINCIDE, menor | p1: la banda dice «HABILIDADES CONTINÚA EN PÁG. 4»; el golden dice «ATRIBUTOS Y HABILIDADES A P. 4» (H4) |
| `v11_extremo_variante_B` (10 p) | `A:Mara-B` p1, p4, p5; golden p4–p5 | NO_COINCIDE | p5: hueco en blanco de 260×420 pt junto al Equipo, sin cuadrícula (H1). p4: el Equipo abre con una sola fila al pie (H6) |
| `v11_comparador_C2_extremo` (6 p) | `A:Mara-A` p4–p6, `A:Mara-B` p4–p5, `A:Stress-A` p4–p5, `CI:P2-C2-large-amounts-A` p5 | NO_COINCIDE | El comparador junta módulos en hojas mixtas y llena huecos con NOTAS. `A:Stress-A` p5 deja Especial solo (H2) y varias hojas dejan huecos en blanco (H1) |

---

## 3. QA01 a QA08 y GLB12

**Recuento: 5 demostradas con salvedades · 4 no demostradas.**

| Regla | ¿Demostrada? | Por qué |
|---|---|---|
| **QA01** datos íntegros | **Sí, con salvedades** | Inventario de abajo: nada perdido ni duplicado sin motivo en Aldren, Ilyra, Mara y estrés, A y B. El orden de Equipo se conserva. Salvedad: el PDF no trae IDs; comparo textos |
| **QA02** fuentes, glifos, solapes | **Sí, con salvedades** | PyMuPDF sobre 184 PDF (1.459 páginas): 0 fuentes sin incrustar; todas 612×792; 0 textos fuera de x36–576 / y30–762; 0 textos que se pisan. Salvedad: dejé fuera las marcas de Para Hoja v8, que van a propósito dentro del espacio en blanco al inicio del nombre de la habilidad |
| **QA03** Guardar/Compartir real | **No** | Solo tengo los PDF. El acta del implementador dice que Compartir abrió el selector pero no se completó el envío. No vi la pantalla |
| **QA04** comparación con los 8 golden | **No** | 3 de 8 NO_COINCIDE (sección 2) |
| **QA05** V11 no depende de las hojas antiguas | **Sí, con salvedades** | `AndroidPcSheetExportService.generateV11Proof` llama solo a `AndroidPcSheetV11PdfProofRenderer`, y este no importa Fantasy, Classic, Custom v1 ni v2. Salvedad: leí importaciones y esa función, no todo el código |
| **QA06** identidad del candidato | **Sí, con salvedades** | APK `9f16b03b…0cd9` igual en CI y copia; versión y build en el commit; los 13 PDF de Android tienen el SHA que da el acta del candidato (comparé 20 cifras). Salvedad: no reconstruí el APK; los PDF no traen metadatos que los liguen al APK |
| **QA07** 48 secciones conciliadas | **No** | 3 NO_CUMPLE y 1 NO_DEMOSTRADO |
| **QA08** todo en PASS | **No** | La matriz tiene todas las reglas en `NOT_VERIFIED` |
| **GLB12** golden intacto; hojas antiguas sin reparar | **Sí, con salvedad** | Golden intacto (SHA). `git diff --stat 2eff0e7c 07e5aca0` no toca ningún renderizador antiguo. Salvedad: miré solo este tramo de historia |

### Inventario independiente de QA01

Leí cada ficha JSON y busqué cada dato en los PDF de Android. Para no confundir textos partidos en dos líneas, junté los renglones por columna (misma x con 10 pt de margen) y quité las marcas de Para Hoja v8. Lo dudoso lo miré a mano.

| Dato | Aldren A y B | Ilyra A y B | Mara A y B | Estrés A y B |
|---|---|---|---|---|
| Clases | 1 de 1 | 1 de 1 | 1 de 1 | 16 de 16 (6 en P1, 10 en p4) |
| Rasgos | 8 de 8 (6 P1, 2 P2) | 10 de 10 | 26 de 26 (6 P1, 16 P2, 4 C2) | 8 de 8 |
| Entradas de combate | 4 de 4 (2 ATAQUES, 2 ACCIONES) | 2 de 2 | 8 de 8 (4 con bono en ATAQUES, 4 en ACCIONES) | 7 de 7 (5 en P1, 2 en p4) |
| Objetos (especiales) | 11 de 11 (4) | 6 de 6 (2) | 34 de 34 (5: 4 en P2, 1 en C2) | 51 de 51 (14: 8 en P2, 6 en p5) |
| Munición | Virotes 1 vez, P1 | no tiene | no tiene | Virotes 1 vez, P1, `___ / 40` |
| Monedas | 5 de 5 | 5 de 5 | 5 de 5 | 13 de 13 (10 P2, 3 p4) |
| Recursos | 2 de 2 | 4 de 4 | 10 de 10 | 2 de 2 |
| Atributos / habilidades propias | no tiene | no tiene | 4 de 4 / 10 de 10 | no tiene |
| Conjuros en P4 | sin conjuros | 25 de 25 (27 filas conjuro-fuente) | 24 de 24 | 25 de 25 (27 filas) |
| Libro | no aplica | 25 de 25 (solo B) | 24 de 24 (solo B) | 25 de 25 (A y B) |
| Notas tituladas + general | 0 + 1 | 0 + 1 | 9 + 1, texto completo | 0 + 1 |
| Trasfondo, perfil e historia | completos | completos | completos | completos |
| Descripción o notas de rasgo impresas | 0 | 0 | 0 | 0 |
| Notas de ataque impresas | 0 | 0 | 0 | 0 |

- **Repeticiones que no son duplicados:** «Second Wind» y «Action Surge» son a la vez rasgo, acción y recurso; «Espada larga», «Ballesta ligera» y «Daga» son ataque y objeto; «Humano» es rasgo, fuente del rasgo y raza; «Fire Bolt», «High Elf» y «Magic Initiate (Wizard)» salen también en descripciones del Libro; «Eco temporal» es el nombre de dos recursos de Mara (no el sentido); «Suministros de calígrafo» es objeto de Ilyra (no la competencia).
- **Lo que las decisiones nuevas sacan del papel, y si tiene respaldo:**
  - Nombre largo solo en la cabecera: `CI:P1-long-character-name-A` corta el nombre en las 6 páginas y ya no hay renglón «NOMBRE COMPLETO DEL PERSONAJE» en ningún PDF. Respaldo: decisión del propietario 2026-11-07 «solo quiero el nombre en la cabecera», escrita en el contrato.
  - Fuente de clase sin clase enlazada: `CI:source-tabs-mechanical-A` p5 rotula «Clase» en las filas; `CI:P4-source-physical-break-A` ya no imprime nombres propios. Respaldo: decisión 2026-11-07 «Solo "Clase"». Queda un «—» (H8).
  - Notas de munición: «Munición para ballesta.» no aparece en Aldren ni en estrés. Respaldo: decisión 2026-11-07 «No, solo en App».
  - Aviso: estas tres citas solo las pude leer en el contrato, que escribe el Worker. No tengo la conversación con el propietario. Son coherentes con las preguntas de mi revisión anterior.
- **Solo en App (decisión 2026-11-06):** no aparecen competencias, idiomas, defensas, sentidos, movimientos, efectos temporales, marcadores, opciones de clase ni subclase.
- **Valores que recalculé:** Aldren (FUE +4, salvación +7, CA 18, 5d10, 30 pies · 9 m). Mara: CD 16 y ataque +8 por su fuente (8 + 4 + ÉTER +4); la ficha guarda 17 como valor antiguo. Estrés: CD 13, ataque +5 (INT 11, competencia +5).
- **Dato raro de la ficha, no del PDF:** en Mara, el daño en texto de Técnica 3 y 5 (`4d6` / `3d6`) no coincide con el daño por componentes de la misma ficha (`3d6` / `4d6`). El PDF imprime el texto. La App, al guardar, rehace el texto desde los componentes, así que con datos reales no debería pasar.

---

## 4. Cierre de la revisión anterior

Comprobado en los PDF reales. Después leí `HALLAZGOS_REVISION_PREQA11.md` solo para contrastar. **Discrepo en M2 y M5**: dice «corregido» y quedan restos.

| Id | Estado | Evidencia |
|---|---|---|
| M1 orden de Equipo al partirse | **Cerrado** (queda un detalle) | `A:Stress-A` p4: 29–33 \| 34–38 y 39 arriba de la otra columna; se lee en orden. `A:Mara-B` p4–p5: 11, 12 y luego 13–24 \| 25–34. Detalle: el tramo de p4 es una sola fila (H6) |
| M2 Especial en hoja casi vacía | **Sigue en parte** | `A:Mara-B` p5 ya junta Equipo, Especial y Rasgos. Pero `A:Stress-A` p5 sigue con 6 objetos y cuadrícula, cuando cabría en p4 (H2). Y la corrección trajo huecos en blanco (H1) |
| M3 habilidades sueltas | **Cerrado** | `A:Mara-A` p4: «HABILIDADES DE SABIDURÍA», «HABILIDADES DE INTELIGENCIA» |
| M4 rótulos en la franja de cabecera | **Sigue**, por regla del Worker | `A:Mara-A` p4 «RECURSOS / CONTINUACIÓN» en y≈94; p5 «RECURSOS / VIENE DE PÁG. 4» junto a «MÓDULOS COMPARTIDOS». Ahora es una aclaración técnica del Worker. Bajo |
| M5 «VIENE DE PÁG. 1» en módulos que no están en P1 | **Sigue en parte** | Las cartelas de ACCIONES y RECURSOS ya no lo dicen. Pero el pie de `A:Aldren-A` p1 y `A:Ilyra-A` p1 dice «CONTINÚA EN PÁG. 4» y p4 «VIENE DE PÁG. 1», cuando en p4 solo hay ACCIONES y RECURSOS (H5) |
| M6 filetes en ATAQUES vacía | **Cerrado** | `A:new-character-A` p1 sin filetes; `A:Aldren-A` p1 solo en las dos filas con ataque |
| M7 «dos subcolumnas» | **Cerrado** | `A:Mara-B` p1 y `A:Aldren-B` p1 |
| M8 segunda línea de cabecera | **Cerrado** por decisión | Las hojas C2 no llevan nada a la derecha (`A:Mara-A` p4–p6, `A:Stress-A` p4–p5) |
| M9 / F03 fuente de clase sin clase | **Cerrado en parte** | Rótulo «Clase» sin nombre propio. Queda «Clase —» en las tarjetas de `CI:P4-source-physical-break-A` p1 y p5 (H8) |
| M10 nombre completo como prosa | **Cerrado** | No hay «NOMBRE COMPLETO» en ningún PDF |
| M11 respuestas atribuidas | **Cerrado** en el documento | El contrato tiene ahora la «Corrección del Worker» con el origen de las cuatro reglas. Coincide con lo que dije en `preqa.11` |
| M12 alcance como renglón en C2 | **Sigue** (abierto a propósito) | `A:Stress-A` p4: «ALCANCE QAFILA3: …» dentro de la tabla ATAQUES. Bajo |
| M13 tres fuentes «Otro» | **Cerrado** por decisión | «Una vez»: `A:Stress-A` p1 y p6 «Otro 13» una sola vez |
| §3.29 y §3.32 NO_CUMPLE | **Siguen** | H1 y H2 |
| §3.12 NO_DEMOSTRADO | **Sigue** | Colores iguales al golden; sin prueba impresa |
| Notas de munición | **Cerrado** por decisión | 0 apariciones |

### Comparación Android contra CI

Gris a 100 ppp, píxeles con diferencia de gris mayor que 60 y mayor que 8, página a página.

| PDF (Android / CI) | Páginas | > 60 | > 8, peor página | Texto |
|---|---|---|---|---|
| Aldren-A / Aldren-A | 6 / 6 | 0 en todas | 53 (p3) | igual salvo «fi» |
| Ilyra-B / Ilyra-B | 9 / 9 | 0 en todas | 41 (p3) | igual salvo «fi» |
| Mara-A / Mara-A | 10 / 10 | 0 en todas | 144 (p2) | igual salvo «fi» |
| Mara-B / Mara-B | 11 / 11 | 0 en todas | 216 (p9 y p10) | igual salvo «fi» |
| Stress-A / stress-android-A | 12 / 12 | 0 en todas | 1.610 (p9; diferencia máxima 42) | igual salvo «fi» |
| Stress-B / stress-android-B | 12 / 12 | 0 en todas | 1.610 (p9) | igual salvo «fi» |
| new-character-A / B | 4 / 4 | 641 en cada página | 981 (p3) | solo cambia el nombre |

`new-character` no es el mismo dato: Android se llama «PJ Nuevo QA» y CI «Personaje Nuevo»; los 641 píxeles son el nombre. Aldren-B, Ilyra-A, Aldren-epic-A y los dos retratos no tienen par en CI.

---

## 5. Hallazgos nuevos, de mayor a menor gravedad

### H1 — MEDIA, regresión — Huecos en blanco sin cuadrícula NOTAS en hojas C2
- **Regla:** contrato, C2: «Todo hueco físicamente apto sin módulo útil se rellena con una pequeña cuadrícula completa titulada NOTAS»; global: «Evitar huecos arbitrarios» (regla C207).
- **Se imprime:** `A:Mara-B` p5: la columna derecha, al lado del Equipo, queda **en blanco**, 260×420 pt (x310–570, y100–520), hasta el Equipo Especial. Igual en 21 PDF de CI, por ejemplo `CI:P2-C2-large-amounts-A` p5 (260×500 pt), `CI:C2-oversized-custom-attribute-A` p7 (260×310), `CI:dense-six-attributes-A` p6 (260×310), `CI:six-plus-two-A` p5 (260×420).
- **Es nuevo:** en `preqa.11`, `A:Mara-B` no tenía ningún hueco así (lo medí con el mismo script: el mayor era 270×60 pt al pie).
- **Golden:** extremo A p5–p6 y comparador: lo que no tiene módulo lleva NOTAS.
- **Debe imprimirse:** cuadrícula NOTAS en ese hueco, o el Equipo repartido en las dos columnas.

### H2 — MEDIA — Equipo Especial en hoja propia cuando cabe en la anterior
- **Regla:** §3.29 «No crear obligatoriamente una hoja por módulo»; §3.32 probar vertical, dos columnas y mixta y elegir la mejor; §3.31, el ahorro de páginas va después de integridad y lectura, pero va.
- **Se imprime:** `A:Stress-A` p5 (y B, y CI): solo 6 objetos especiales (y106–292) y el resto cuadrícula. En p4 la columna izquierda va llena (PERSONAJE, COMBATE, ATAQUES, RECURSOS, EQUIPO, hasta y≈722) y la derecha casi vacía (Equipo 39 y TESORO hasta y≈190; NOTAS de y≈200 a 733).
- **Cálculo:** PERSONAJE + COMBATE + ATAQUES ≈ 370 pt a la izquierda; RECURSOS + EQUIPO + TESORO ≈ 360 pt a la derecha; terminan cerca de y≈480. Especial a todo el ancho mide ≈190 pt: llega a y≈680, antes del límite y≈735. Cabe sin achicar nada.
- **Debe imprimirse:** con la distribución mixta, Especial en p4 y una página menos. Lo mismo, en menor medida, en `A:Mara-A`: p5 tiene la columna derecha entera en NOTAS y p6 existe para un objeto especial y 4 rasgos; `A:Mara-B` junta casi lo mismo en una hoja menos.
- **Origen:** aclaración del Worker de 2026-11-07: «sin módulos que lo sigan, queda en su hoja». El golden no tiene esa regla.

### H3 — MEDIA — El Libro pierde el nombre del conjuro al continuar una ficha larga
- **Regla:** §3.48: la ficha larga se parte «conservando identificación del conjuro y señalando que es continuación»; contrato C341: rótulo de columna «IDENTIDAD / VIENE DE PÁG. n» o «IDENTIDAD / CONTINUACIÓN».
- **Se imprime:** `A:Stress-A` p9 y p10 (dos columnas cada una) y p11 arriba: solo «CONTINUACIÓN», sin el nombre «QALARGO conjuro de nombre excepcionalmente largo…». El código lo hace a propósito: si «CONTINUACIÓN: nombre» no cabe en una línea, imprime solo «CONTINUACIÓN» (`AndroidPcSheetV11PdfProofRenderer.kt`, comentario C450).
- **Además:** con nombre corto imprime «CONTINUACIÓN: Fire Bolt» (`CI:BK05-long-card-A`), que no es la forma de C341.
- **Debe imprimirse:** el nombre del conjuro en cada tramo, partido en dos renglones si hace falta, y la forma de C341.

### H4 — BAJA — La banda de P1 dice «HABILIDADES» cuando sigue un atributo
- **Se imprime:** `A:Mara-B` p1: «HABILIDADES CONTINÚA EN PÁG. 4», pero las 28 habilidades están en P1; lo que sigue en p4 es el atributo RENOMBRE. `A:Mara-A` p1 dice lo mismo.
- **Golden:** extremo A p1: «ATRIBUTOS Y HABILIDADES A P. 4», el mismo título de la cartela de C2.
- **Debe imprimirse:** «ATRIBUTOS Y HABILIDADES CONTINÚA EN PÁG. 4».

### H5 — BAJA — P1 y p4 se enlazan aunque P1 no tiene esos módulos
- **Se imprime:** `A:Aldren-A` p1 y `A:Ilyra-A` p1: pie «CONTINÚA EN PÁG. 4»; p4: «VIENE DE PÁG. 1». En p4 solo hay ACCIONES, RECURSOS y NOTAS. Las cartelas ya no lo dicen (resto de M5).
- **Debe imprimirse:** sin enlace de pie si ningún módulo de P1 sigue.

### H6 — BAJA — El Equipo abre con una sola fila al pie de la hoja
- **Se imprime:** `A:Mara-B` p4: cartela EQUIPO y una fila («Cuaderno … 11 | Cristal … 12»), y el resto en p5. El orden es correcto.
- **Debe imprimirse:** mejor empezar el módulo en p5 entero, como hace el golden con los módulos que no caben.

### H7 — BAJA — Rótulo de una fila vacía de Equipo Especial que repite un lugar ocupado
- **Se imprime:** `A:Aldren-A` p2: objetos en Cuerpo, Mano izquierda, Mano derecha y Cuello; filas vacías «Mano derecha», «Pecho», «Piernas», «Libre». «Mano derecha» sale dos veces; no aparecen «Cabeza» ni «Rostro».
- **Regla:** §3.21: las filas libres pueden llevar «ubicaciones habituales vacías». Es la lectura del Worker (rótulo por posición del golden).
- **Debe imprimirse:** en filas vacías, rótulos del golden que no estén ocupados.

### H8 — BAJA — «Clase —» en las tarjetas de magia
- **Se imprime:** `CI:P4-source-physical-break-A` p1 y p5: «Clase — / Trasfondo INT / Mago INT / Raza INT». La fuente sin clase enlazada no tiene valor y sale un guion.
- **Regla:** contrato 2026-10-16: no fabricar «null» ni puntuación huérfana (escrito para rasgos, pero es el mismo criterio). Solo en un caso de estrés de CI.

### H9 — BAJA, informativo — El nombre largo se corta igual en hojas sin rótulo a la derecha
- `CI:P1-long-character-name-A`: «Aldren NOMBREQA01 NOMBREQA02 NOMBREQA03 N» termina en x474 en las 6 páginas, también en p4 (C2), donde a la derecha ya no hay nada. No choca con nada (el rótulo de P1 empieza en x470 pero está debajo de la línea base). Cumple «cortado donde deja de caber» de forma prudente.

### Aclaraciones del Worker revisadas con sospecha

| Aclaración | Mi lectura |
|---|---|
| 2026-11-07: Especial que continúa a todo el ancho, Rasgos e Historia debajo; menos de 120 pt, otra hoja | Defendible por el golden extremo B p5. Pero «sin módulos que lo sigan, queda en su hoja» no lo pide nadie y produce H2 |
| 2026-11-07: Equipo en C2 por mitad izquierda y luego derecha | Defendible; arregla el orden (M1). Pero deja la otra columna en blanco (H1) |
| 2026-11-07: habilidades sueltas bajo «HABILIDADES DE …» | Correcta; sigue al golden |
| 2026-11-07: ACCIONES y RECURSOS sin «VIENE DE PÁG. 1» | Correcta en cartelas; falta el pie (H5) |
| 2026-11-07: aviso de columna en la línea de «MÓDULOS COMPARTIDOS» | Defendible como detalle técnico; el golden no tiene el caso. Bajo (M4) |
| 2026-11-07: filetes solo en filas con ataque; «dos subcolumnas» | Correctas; siguen al golden |
| Corrección del Worker sobre el origen de cuatro reglas | Honesta y coincide con mi M11. `____ / X` sí está en D-0076 §3.15 como aclaración del propietario; las otras tres vienen del golden |
| C450 (código): rótulo del Libro sin nombre si no cabe | No defendible: contradice §3.48; debió partirse el rótulo (H3) |
| Anteriores (municiones una vez, títulos de C2, referencias con página real) | Siguen defendibles, como en `preqa.11` |

---

## 6. Preguntas que solo puede responder el propietario

**Ninguna nueva.** Los hallazgos de esta revisión ya tienen regla escrita (contrato, D-0076 o golden). Son trabajo técnico, no decisiones de producto.

**Para informar, sin pregunta:**
- Las tres decisiones del 2026-11-07 que sacan cosas del papel (nombre largo solo en la cabecera, «Clase» sin nombre, notas de munición solo en la App) ya se ven así en los PDF.
- Mara imprime CD 16 por su única fuente, aunque la ficha guarda 17 como valor antiguo. Es el cálculo por fuente.

---

## 7. Límites de esta revisión

**Páginas miradas como imagen:**

| Conjunto | Total | Vistas a tamaño legible | Solo por script |
|---|---|---|---|
| Android | 101 | unas 50 (Mara-A p1–p7 lado a lado con el golden; Mara-B p1–p9; Stress-A p1–p12; Aldren-A p1–p6; Aldren-B p1; Aldren-epic-A p1; Ilyra-A p1–p7; new-character-A p1–p4; recortes de retrato, atributos y ATAQUES) | el resto |
| CI | 1.358 | unas 8 (huecos, nombre largo) | el resto |
| Golden | 59 | unas 25 | el resto |

**No pude comprobar:**
- Impresión real, lápiz y escala de grises (§3.12).
- La pantalla de la App: no vi Guardar, Compartir ni la elección A/B. No toqué el emulador ni adb.
- Que cada PDF salga de ese APK: los PDF no traen metadatos; me fío del SHA y del acta del candidato.
- Las citas del propietario del 2026-11-07: solo las leí en el contrato que escribe el Worker.
- Subraza, escuela en el Libro, fuente «Clase» sin enlace y nombre largo: solo en CI.
- Las 134 reglas de la matriz una por una; solo QA01–QA08 y GLB12.

**Métodos y puntos ciegos:**
- Inventario por texto, juntando renglones por columna. Un texto corto puede aparecer por casualidad; los dudosos los miré a mano.
- Huecos: busqué el mayor rectángulo blanco a 72 ppp, en celdas de 10 pt, dentro de x40–572 / y100–735. Solo miré hojas con «MÓDULOS COMPARTIDOS».
- Solapes: cajas de texto que se cruzan más de un 25 %; las marcas de Para Hoja v8 quedan fuera.
- El cálculo de H2 usa las alturas medidas de los módulos en `A:Stress-A` p4–p5, no una nueva composición.
- Para QA05 leí solo importaciones y una función.

**Lo que leí además de los PDF:** `ACTA_CANDIDATO_PREQA12.md` al empezar, solo la identidad (y luego para cotejar los SHA de los PDF); el contrato, D-0076 completo y la matriz (lista de reglas, QA01–QA08, GLB12); las fichas JSON como datos; partes del código: la función de exportación, las importaciones del renderizador, el rótulo de continuación del Libro, la banda de P1 y el daño por componentes en `CharacterSuccessorRepository.kt`; `ARTEFACTOS_Y_PROCEDENCIA.md` y el manifiesto del ZIP golden. Al final, `ACTA_REVISION_INDEPENDIENTE_PREQA11.md` y `HALLAZGOS_REVISION_PREQA11.md`, solo para contrastar, y los PDF de `android_pdfs\cand11` para medir si H1 era nuevo. El contexto de la sesión mostraba `AGENTS.md` y la memoria del proyecto.

**Scripts e imágenes:** `C:\Users\gms\AppData\Local\Temp\claude\81e26e1f5f1fff5b\af0b7db1-bfd0-437a-87dd-fbd9b773da41\scratchpad\review_preqa12`.
