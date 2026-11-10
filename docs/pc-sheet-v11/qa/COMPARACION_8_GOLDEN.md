# Comparación de los ocho PDF golden con la App

**Fecha:** 2026-11-05. **Hecha por:** Claude Code, el mismo agente que implementa el renderizador, así que **no es la revisión independiente** de la compuerta: los ocho casos siguen en `NOT_VERIFIED` y `golden_eight_pdfs` en `PENDING_PHYSICAL_MATCH`. Además no hay candidato, y sin candidato no hay SHA que registrar.

**Método.** `scripts/report_v11_golden_cases.py` compara, página por página y por función de la página, cada forma fija del golden (cartelas, tarjetas, marcos y reglas de cabecera) con el PDF real de la App: mismo sitio y mismas tintas, tolerancia 0,6 pt. Los datos del golden son ficticios, así que filas y textos no se comparan aquí. P1 solo se mide en su columna derecha, porque la izquierda depende de cuántos atributos se impriman. C2, C-N3 y el Libro dependen de los datos y se revisaron a ojo.

## Resultado

| PDF golden | P1 | P2 | P3 | P4 | NOTAS final | C2, C-N3 y Libro |
|---|---|---|---|---|---|---|
| `v11_extremo_variante_A` | coincide (25 formas) | coincide (9) | coincide (8) | ver nota 1 | coincide (23) | revisión visual |
| `v11_extremo_variante_B` | coincide (25) | coincide (9) | coincide (8) | ver nota 1 | coincide (23) | revisión visual |
| `v11_ordinario_6mas2_variante_A` | coincide (25) | coincide (9) | coincide (8) | coincide (9) | coincide (23) | revisión visual |
| `v11_ordinario_6mas2_variante_B` | coincide (25) | coincide (9) | coincide (8) | coincide (9) | coincide (23) | revisión visual |
| `v11_ordinario_6mas3_variante_A` | coincide (25) | coincide (9) | coincide (8) | coincide (9) | coincide (23) | revisión visual |
| `v11_ordinario_6mas3_variante_B` | coincide (25) | coincide (9) | coincide (8) | coincide (9) | coincide (23) | revisión visual |
| `v11_personaje_casi_vacio` | coincide (25) | coincide (9) | coincide (8) | no tiene | coincide (23) | no tiene |
| `v11_comparador_C2_extremo` | no tiene | no tiene | no tiene | no tiene | no tiene | revisión visual |

**Nota 1.** La segunda página de P4 del golden extremo lleva una cartela NOTAS en una columna libre. La App la imprime también (C405), pero su Mara cabe en una página de P4, así que este par no la ejerce. La prueba `c406…` la cubre con 175 trucos.

## Diferencias que siguen, y por qué

| Dónde | Golden | App | Motivo |
|---|---|---|---|
| Cabecera | Texto «DUNGEONS & DRAGONS» y escudo | Logo del propietario, sin escudo | Decisión del propietario, 2026-10-30 |
| Pie | `MAQUETA V11…` y `01 / 11` | `PRUEBA INTERNA V11…` y solo el número | Decisión C340; el rótulo interno es provisional hasta el candidato |
| Pie | `A P. 4` | `CONTINÚA EN PÁG. 4` | Decisión C341 |
| Fila de atributo | Geometría del golden | Fila aprobada P104 | Decisión del propietario, 2026-11-01 y 2026-11-03 |
| P1, niveles sin espacios | 3 casillas | Línea para escribir | Contrato: «mínimo 0», «una por espacio real» |
| P1, munición | Algunas casillas tachadas | Sin marcas | Ficticio en el golden; la App no guarda marcas gastadas por munición |
| P2, Equipo Especial vacío | 8 rótulos | 4 rótulos y filas en blanco | D-0076 §3.42 |
| P4, nivel que sigue en otra columna | Repite el título | `NIVEL n / CONTINUACIÓN` | Decisión C341 |
| C2, composición | EQUIPO y TESORO en media página si los nombres son cortos | A todo el ancho | Sin decisión; los nombres reales suelen ser largos. Es la principal diferencia de composición que queda |
| C2, HISTORIA | Módulo dentro de C2 | Páginas propias de continuación de P3 | Aclaración de la Historia, 2026-10-26 |

## Lo que esta comparación no demuestra

No mide filas ni textos. No sustituye a la revisión independiente, ni a la prueba en Android. Para repetirla:

```bash
python scripts/report_v11_golden_cases.py --golden-zip docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip --pdf-dir desktopApp/build/pc-sheet-proofs --report build/v11-golden-cases.json
```
