# C255–C266 — Respuestas del propietario implementadas (2026-10-28/29)

PR #173 DRAFT/NO MERGE. Anterior: [C250–C254](2026-10-28_V11_C250_CINCO_BLOQUES_4.md). Las respuestas a Q1–Q13 están en [`DECISIONES_PENDIENTES_PROPIETARIO.md`](../pc-sheet-v11/DECISIONES_PENDIENTES_PROPIETARIO.md) y en el contrato (aclaración del 2026-10-28).

**Instrucción del propietario:** «Sí, implementa» y luego «empieza con unos 10 bloques como anteriormente». Regla general: «diseño debe llegar a ser igual (fuente, color, distribución, maquetación) al original … está en el contrato».

Evidencia gráfica: [`qa/evidence/c255_c266/`](../pc-sheet-v11/qa/evidence/c255_c266/).

| Bloque | Commit | Qué cambió | Decisión de origen |
|---|---|---|---|
| B21 / C255 | `18cd0b14` | P1: espacios de conjuro con casillas vacías y número «gastados/total» (Mara N1 «2/4»). Con 6 espacios, las casillas miden 6,5 pt. | Q3 «B, pero sin marcas» |
| B22 / C256 | `582923bc` | P1: valores por fuente en una línea (hasta 6 pt) y luego en dos renglones; «SEGÚN FUENTE» solo como último recurso. Tarjeta INSPIRACIÓN con casilla real y «TEMP n» dentro de PV ACTUALES. VELOCIDAD «35 pies · 10,5 m». Rótulos del golden «DADOS DE GOLPE» y «ARMA / ATAQUE / CONJURO · BON. · DAÑO / TIPO». Tinta del golden en las tarjetas. | Q7, Q10, Q11, Q9 |
| B23 / C257 | `21a3add7` | P1 MUNICIONES: dos filas de 10 casillas del golden y título «MUNICIONES». En P1 cabe una munición; las demás siguen completas en C2. | Q8 |
| B24 / C258 | `649a1837` | P3: el pie nombra cada campo que continúa («IDEALES A P. 4»). P4: una fila por cada fuente de cada conjuro, solo con el tipo y sin «[X]». | Q4, decisión de preparación |
| B25 / C259 | `4dda419d` | C2 Recursos: casillas vacías y «gastados/total». Acciones en tabla ACCIÓN / TIPO / ALCANCE / EFECTO. Todo en español: Acción, Acción adicional, Reacción, y la letra P/E dentro de la casilla de habilidad. | Q1 «A», Q12 |
| B26 / C260 | `a37ae4ad` | Libro: 6,7 pt sobre 10,5 pt. Notas C-N3: 7 pt sobre 11,6 pt. Cada sección medida dibuja con su propia fuente. | Regla general |
| B27 / C261 | `979e64a3` | P4: 6,8 pt sobre 15,7 pt. | Regla general |
| B28 / C262 | `1cd94e70` | C2 mixta: 7,2 pt sobre 15,7 pt. Un título de módulo ya no queda solo al pie de una columna. | Regla general |
| B29 / C263 | `d4e90be8` + `861a812c` | P2 Equipo, Tesoro y Rasgos: 7,1 pt sobre 16 pt. La cuadrícula de Rasgos sigue el mismo renglón. | Regla general |
| B30 / C264 | `10c9b978` | P1: títulos 9,1 pt; atributos 11 / 8,7 / 7,7 / 5,8 / 7,2 pt; combate 6,7 / 10,7 pt; ataques 7,4 / 7,1 pt. | Regla general |
| B31 / C265 | `cde776f0` | Rombos ◇ del golden en las cartelas de P1 y P2. Títulos a 16 pt dentro de la banda y a 9,1 pt. | Regla general |
| B32 / C266 | `a72effbf` | Títulos de cartela de P1 y P2 en el azul oscuro del golden (#1d384f). | Regla general |

## Verificación (cada bloque)

- `gradle :androidApp:assembleDebug :shared:allTests :desktopApp:test` pasa, y todas las pruebas de Python pasan.
- Los 11 auditores PASS sobre 100 PDF.
- Se ajustaron 3 auditores a la gramática nueva, sin debilitarlos:
  - El control de munición vacía de `audit_v11_p1_clipped_cards.py` vuelve a encontrar las mismas 70 municiones vacías.
  - `audit_v11_p3_c2_manuscript.py` acepta dentro de la casilla de habilidad solo la letra P o E.
  - `audit_v11_pdf_physical_envelope.py` espera las bandas de 16 pt de P2.
- Comparación de palabras contra la CI de B20: solo desaparecen rótulos, palabras técnicas en inglés, marcas «[X]» y títulos repetidos de continuación (hay menos páginas). Ningún dato del personaje se pierde.
- CI exacta SUCCESS hasta `cde776f0` (Scaffold y QA). **PASS LOCAL FÍSICO, no G4.**

## Pendiente y límites

- **Fuentes del golden.** El golden usa Noto Sans y EB Garamond (nombres de conjuro del Libro, cabecera). La app solo incorpora Fira Sans y Barlow Condensed, así que se igualaron tamaños, ritmos y colores, pero no la familia tipográfica. Igualarla exige incorporar esos archivos de fuente; queda pendiente.
- **Sin el ritmo del golden todavía:** Equipo Especial (P2 y C2, 27 pt), las tablas pareadas de C2 (18,4 pt), los títulos de C2, la franja REFERENCIA MÁGICA de P4 y la cabecera del Libro.
- **Páginas de Mara:** pasa de 9 a 10, porque C2 ocupa una página más con la gramática de Recursos y Acciones. No se pierde nada.
- **Matriz:** sin evidencia nueva en esta tanda. Las reglas P115/P116 fueron renovadas con C250 y su geometría cambió de nuevo en C255; hay que renovarlas con la CI exacta. G4: 106 reglas NOT_VERIFIED.

## Ronda siguiente (cinco bloques, C267–C271)

**Instrucción del propietario:** «haz 5 rondas más».

| Bloque | Commit | Qué cambió |
|---|---|---|
| B34 / C267 | `5fe5bd59` | Equipo Especial de P2: texto de 7 pt (paso de 9 pt) dentro de la pauta de 25 pt del propietario (D-0076 §3.21). En la P2 de Mara caben 4 objetos reales en lugar de 2. |
| B35 / C268 | `5035e02c` | C2: Equipo Especial a 7 pt; las tablas de Equipo y Tesoro con el ritmo del golden (7,2 pt sobre 18,4 pt). C207: una columna sin ningún registro en su página recibe su propia cuadrícula NOTAS, también cuando ya está en uso la cuadrícula inferior. |
| B36 / C269 | `9ffad734` | Franja REFERENCIA MÁGICA de P4 como el golden: rombos, título de 9,1 pt, rótulos de 6,65 pt en gris y valores centrados de 10,7 pt. Títulos de cartela de C2 en 9,1 pt con la tinta del golden. |
| B37 / C270 | `b149f23b` | Nombre de cada ficha del Libro como título en negrita con la tinta del golden (Barlow 8,2 pt, la fuente incorporada más cercana a la EB Garamond 11,3 pt del golden) y NIVEL de 6,3 pt en la misma línea base. |
| B38 / C271 | este commit | Matriz: evidencia G3 renovada de P114, P115 y P116 con la CI exacta de `b149f23b`: Scaffold [#40922931592](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40922931592), PDF 12920415449 y auditoría 12918564450. Los 11 auditores, vueltos a ejecutar sobre esos PDF, dan PASS; sin pérdida de palabras frente a los locales. |

- **Verificación:** igual que en la ronda anterior, en cada bloque (Gradle, Python, 11 auditores, palabras, CI exacta SUCCESS).
- **Matriz:** sigue en 39 de 242 comprobaciones por etapa en PASS, ahora con evidencia vigente para P114–P116. G4 sigue bloqueado.
- **Pendiente:**
  - Fuentes Noto Sans y EB Garamond: requieren incorporar sus archivos.
  - Alto de las tarjetas de P4.
  - Escuela de magia en la ficha del Libro (el golden la muestra; la ficha esencial BK06 aprobada no la incluye; se pregunta al propietario si corresponde).
  - Evidencia de la matriz para el resto de las reglas.
