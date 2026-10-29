# V11 — Decisiones pendientes del propietario

Lista viva. El Worker anota aquí cada decisión que corresponde al propietario y sigue con el trabajo ya decidido. Cada entrada indica la vista previa, las opciones y qué desarrollo queda pospuesto. Cuando el propietario responde, la entrada pasa a «Resueltas», con sus palabras, y la regla se lleva al contrato.

## Abiertas

Ninguna. Q14 se resolvió el 2026-10-29 (ver «Resueltas»). Las preguntas Q1–Q13 fueron respondidas una a una el 2026-10-28 (ver «Resueltas»); su detalle original queda abajo como historial.

## Historial — detalle de Q1–Q13 tal como se plantearon

### Q1 — C2: cómo mostrar los Recursos (2026-10-26)

- **Contexto:** la C2 «Habilidades, acciones y recursos» imprime acciones y recursos personalizados como texto con «|». El golden no muestra ninguno de los dos.
- **Vista previa:** `qa/evidence/golden_gap_2026-10-26/C2_propuesta_acciones_recursos_R1_R2.png`. Muestra la tabla ACCIONES con la gramática de Ataques de P1 y los RECURSOS con casillas agrupadas de cinco (gramática de Municiones).
- **Opciones:**
  - **R1:** casillas vacías y el texto «Quedan x de y».
  - **R2:** las casillas gastadas salen marcadas con X según el valor guardado.
- **Pospuesto:** tabla ACCIONES y módulo RECURSOS en C2.
- **Dato para decidir (C250, 2026-10-28):** en P1, los espacios de conjuro ya siguen la gramática del golden, igual que MUNICIONES: una casilla por espacio real y las gastadas sombreadas con X. Esto no decide Q1; solo muestra cómo se ve R2 en otra parte de la hoja (`qa/evidence/c250_c254/B16_P1_lanzamiento_golden_vs_app_Mara_A.png`).

### Q2 — ¿Letra del golden (≈7 pt) también en la Lista rápida de conjuros (P4)? (2026-10-28)

- **Contexto:** en el golden, P4 escribe cada conjuro en ~6,8 pt con el tipo de fuente («Clase», «Raza», «Dote») en letra pequeña a la derecha, una línea por conjuro. La app usa Fira 9 pt; con fuentes de nombre largo (Mara: «Cronomancia umbral»), cada conjuro ocupa dos líneas con «| [X] Cronomancia umbral». En Historia de P3 el propietario eligió el ritmo del golden (Q resuelta), pero dejó el resto del cuerpo de texto para decidirlo aparte.
- **Vista previa (golden a la izquierda, app a la derecha):** `qa/evidence/c235_c239/Q2_P4_golden_vs_app.png`.
- **Opciones:**
  - **A:** mantener 9 pt en P4.
  - **B:** pasar P4 al ritmo del golden (~7 pt), con la fuente alineada a la derecha cuando quepa.
  - **C:** aplicar B a todo el cuerpo de texto de la hoja (P1, P2, C2, Notas).
- **Pospuesto:** tamaño del texto en P4 (y en el resto si se elige C).

### Q3 — P1: ¿los espacios de conjuro llevan también números? (2026-10-28)

- **Contexto:** desde C250, P1 dibuja una casilla por espacio real y marca las gastadas con X sombreada, como el golden. Desaparecieron los números «4 2» (total y gastados) y el rótulo «ESPACIOS / GASTADOS», que el golden no tiene. Lo decidió el Worker; el propietario no lo dijo.
- **Vista previa (golden / antes / ahora):** `qa/evidence/preguntas_2026-10-28/Q3_espacios_conjuro_antes_ahora_golden.png`.
- **Ejemplo (Mara, N1):** 4 espacios, 2 gastados.
  - A: `N1 [X][X][ ][ ]`.
  - B: `N1 [X][X][ ][ ]  2/4`.
  - C: `N1 4 2` en un renglón y las casillas debajo.
- **Opciones:**
  - **A (actual):** solo casillas, como el golden.
  - **B:** casillas y, a la derecha, un número pequeño «gastados/total».
  - **C:** volver al formato anterior.
- **Afecta:** P1. Si se elige B o C, cambian la prueba P115/P116 y la evidencia de la matriz.

### Q4 — P3: rótulo del aviso cuando continúa un campo de perfil (2026-10-28)

- **Contexto:** el golden solo muestra «HISTORIA A P. 6». Para los campos de perfil que continúan (Trasfondo, Religión/Fe, Personalidad, Ideales, Vínculos, Defectos), el Worker eligió «TRASFONDO Y PERFIL A P. n».
- **Vista previa:** `qa/evidence/preguntas_2026-10-28/Q4_pie_P3_app.png`.
- **Opciones:**
  - **A (actual):** «TRASFONDO Y PERFIL A P. 4 · HISTORIA A P. 5».
  - **B:** «PERFIL A P. 4 · HISTORIA A P. 5».
  - **C:** el nombre de cada campo que continúa: «IDEALES A P. 4 · HISTORIA A P. 5».

### Q5 — Tipos de fuente de conjuro (supuesto del contrato, 2026-10-26)

- **Contexto:** el propietario dijo que cada fuente se rotula por su tipo, salvo la Clase (nombre sin subclase), y que no recordaba la lista completa. El Worker tomó la del editor de fuentes de la App (D-0058).
- **Lista supuesta:** Clase (con su nombre: «Mago»), Rasgo, Raza, Trasfondo, Dote, Objeto, Objeto mágico, Don, Otro.
- **Ejemplo P4:** `[X] Mago   [ ] Dote   [ ] Raza`.
- **Opciones:**
  - **A:** confirmar la lista.
  - **B:** corregirla (agregar, quitar o renombrar tipos).

### Q6 — Fuente de conjuro que ya no existe (supuesto del contrato, 2026-10-26)

- **Contexto:** si un conjuro sigue asociado a una fuente borrada, la App no tiene su tipo. El Worker la rotula «Otro».
- **Opciones:**
  - **A (actual):** «Otro».
  - **B:** «Fuente eliminada».
  - **C:** sin rótulo; solo la casilla de preparación.

### Q7 — Valores por fuente que no caben en la tarjeta de P1 (supuesto del contrato, 2026-10-26)

- **Contexto:** con varias fuentes con valores distintos, P1 escribe cada fuente si cabe en la tarjeta (81 pt, hasta 7 pt de letra). Si no cabe, escribe «SEGÚN FUENTE» y el detalle queda en P4.
- **Vista previa:** `qa/evidence/preguntas_2026-10-28/Q7_segun_fuente_vs_por_fuente.png`.
- **Opciones:**
  - **A (actual):** «SEGÚN FUENTE»; el detalle en P4.
  - **B:** permitir letra más pequeña (6 pt) antes de rendirse.
  - **C:** usar dos renglones dentro de la tarjeta.

### Q8 — P1 MUNICIONES: una fila de 20 o dos filas de 10 (2026-10-28)

- **Contexto:** el golden dibuja cada munición en dos filas de 10 casillas (grupos de cinco). La app usa una fila de 20 casillas más pequeñas, porque cada munición ocupa así un solo renglón de P1.
- **Vista previa:** `qa/evidence/preguntas_2026-10-28/Q8_municiones_golden_vs_app.png` y `Q8_municiones_app_con_datos_Aldren.png`.
- **Opciones:**
  - **A (actual):** una fila de 20.
  - **B:** dos filas de 10, como el golden. Cada munición ocupa el doble de alto y, si hay varias, las últimas pasan antes a C2.
- **Nota:** aparte de esta decisión, el Worker corregirá el color y los espacios de las casillas al azul del golden.

### Q9 — P1 ATAQUES: rótulos de las columnas (2026-10-28)

- **Contexto:** el golden dice «ARMA / ATAQUE / CONJURO · BON. · DAÑO / TIPO» y la app dice «ATAQUE / RANGO · BONO · DAÑO / EFECTO». P1 no imprime el alcance (va a C2 como «ALCANCE …»), así que «RANGO» en P1 puede confundir.
- **Vista previa:** `qa/evidence/preguntas_2026-10-28/Q9_encabezados_ataques_golden_vs_app.png`.
- **Opciones:**
  - **A:** rótulos del golden.
  - **B:** mantener los actuales.

### Q10 — P1: tarjeta INSPIRACIÓN del golden y PV TEMP (2026-10-28)

- **Contexto:** el golden tiene ocho tarjetas, entre ellas INSPIRACIÓN (vacía para escribir a mano), y no tiene PV TEMP. La app puso PV TEMP en ese lugar. El comentario del código dice que la App no guarda la inspiración, pero eso es falso: la ficha tiene `inspiration` (sí/no) y la ocultaba.
- **Vista previa:** `qa/evidence/preguntas_2026-10-28/Q10_inspiracion_vs_pv_temp.png`.
- **Opciones:**
  - **A (actual):** PV TEMP en el lugar de INSPIRACIÓN; la inspiración no se imprime.
  - **B:** INSPIRACIÓN con una casilla marcada según la ficha, y PV TEMP en pequeño dentro de la tarjeta PV ACTUALES («TEMP 7»).
  - **C:** una tarjeta compartida «INSPIRACIÓN [ ] · PV TEMP 7».

### Q11 — P1: unidad de VELOCIDAD (2026-10-28)

- **Contexto:** el golden muestra «9 m». La App guarda un número (Mara 35), en pies, y P1 lo imprime sin unidad.
- **Opciones:**
  - **A (actual):** «35».
  - **B:** «35 pies».
  - **C:** convertir a metros: «10,5 m».

### Q12 — C2: palabras en inglés (2026-10-28)

- **Contexto:** C2 imprime valores internos sin traducir. En acciones: «ACTION», «BONUS_ACTION», «REACTION». En habilidades: «(EXPERTISE)» y «/ PROFICIENT».
- **Vista previa:** `qa/evidence/preguntas_2026-10-28/Q1_recursos_C2_app_ahora.png` (columna izquierda).
- **Opciones:**
  - **A:** traducir: Acción, Acción adicional, Reacción; Competencia, Pericia.
  - **B:** acciones traducidas y, en habilidades, las marcas de P1 [P] / [E] en la casilla.
  - **C:** dejar como está.

### Q13 — Detalles técnicos que el Worker decidió (2026-10-26 a 2026-10-28)

- **Contexto:** el contrato los anota como «no dichos por el propietario». Se ven en los PDF de prueba.
- **Lista:**
  1. Sangría de 8 pt en las líneas siguientes de un objeto largo en Equipo/Tesoro.
  2. Pauta de 25 pt en Equipo Especial (un objeto ocupa pautas completas; 4 pt de aire al pie).
  3. Casilla ACTIVO centrada en su banda.
  4. Sombreado alterno por banda.
  5. Historia de P3 con la línea base 4 pt sobre la regla.
  6. La continuación de Historia va después de la de los campos de perfil.
- **Opciones:**
  - **A:** aprobar todos.
  - **B:** revisar alguno (indicar cuál).

## Correcciones medidas que el Worker hará sin preguntar (golden ya aprobado)

- Rombos ◇ a los lados de las cartelas, como el golden.
- Rótulos de tarjetas en gris azulado, como el golden, en lugar de negro.
- «DADOS» → «DADOS DE GOLPE» en P1.
- Casillas de MUNICIONES en el azul del golden, con espacio entre grupos de cinco.
- Corregir el comentario erróneo del código sobre la inspiración; qué se imprime lo decide Q10.

## Resueltas

### Q14 — Libro: ¿mostrar la escuela de magia? (2026-10-29)

- **Contexto:** la ficha del Libro del golden muestra, bajo el nombre, la escuela del conjuro en letra pequeña gris («ABJURACIÓN»). La ficha esencial que el propietario aprobó (BK06) es nombre, nivel y descripción íntegra, sin escuela.
- **Vista previa:** `qa/evidence/c255_c266/B26_Libro_golden_vs_app.png`.
- **Opciones:**
  - **A:** agregar la escuela bajo el nombre, como el golden.
  - **B:** dejar la ficha esencial sin escuela.
- **Pospuesto:** la línea de escuela en las fichas del Libro.
- **Respuesta del propietario (2026-10-29):** «Sí, con escuela».
- **Hallazgo del Worker (2026-10-29):** la App **no guardaba la escuela** de ningún conjuro: no estaba en `CharacterSpell` ni en la base local (`character_spell`). Sin ese dato, el PDF no puede imprimir una escuela real (regla: no inventar).
- **Corrección del propietario (2026-10-29):** el SRD **todavía no se ha importado**; hasta ahora solo hay datos de prueba. Por eso no corresponde decir que la escuela «no está en el SRD»: estará cuando se importe.
- **Q14b — cómo obtener la escuela:**
  - **A:** agregar el campo «Escuela» a la App (editor de conjuros Android/Escritorio, base local, copias de seguridad y sincronización con el servidor). El PDF la imprime cuando está cargada.
  - **B:** solo en el PDF, una línea pequeña «ESCUELA ____» en cada ficha para escribir a lápiz, sin dato digital.
  - **C:** B ahora y A más adelante.
- **Respuesta del propietario a Q14b (2026-10-29):** «debe usar A».
- **Implementado (C282):** campo `CharacterSpell.school` (texto libre opcional, sirve también para homebrew), columna `character_spell.school` con migración `26.sqm`, editor de conjuros de Android («Escuela (opcional)»), búsqueda de conjuros, copias de seguridad y sincronización (viajan solas porque guardan la ficha completa en JSON; el servidor no interpreta los conjuros, así que no hace falta ningún paso en el proveedor). El escritorio no tiene editor de conjuros. En el PDF, la ficha del Libro imprime la escuela real bajo el nombre como el golden: mayúsculas, condensada negrita 7 pt, gris #657d90, dentro de la franja del encabezado. Sin escuela guardada, no se imprime ninguna línea.
- **Pendiente técnico:** cuando se importe el SRD, el importador debe llenar `school`.


- 2026-10-28 — Q1 Recursos en C2: «A casillas vacias y gastados/total como espacios de conjuro». Una casilla vacía por unidad del total, nada premarcado, y el número pequeño «gastados/total» (Éter: 2/3); debajo, en letra pequeña, recuperación, fuente y notas. La tabla ACCIONES de la vista previa (gramática de Ataques del golden) se aplica salvo objeción. Implementado en C255–C266 ([checkpoint](../checkpoints/2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md)).
- 2026-10-28 — P4 preparación por fuente: «B, pero el hechizo aparece dos veces, una para cada fuente como: Fire bolt Mago / Fire bolt Otro». Una fila por cada par conjuro-fuente, juntas y en orden dentro de su nivel; casilla vacía para lápiz; solo el tipo de fuente, sin [X] ni [ ]; la preparación no se imprime. Reemplaza la regla anterior «cada fila conserva su marca de preparación». Implementado en C255–C266 ([checkpoint](../checkpoints/2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md)).
- 2026-10-28 — Q6 Fuente de conjuro borrada: «A» (se rotula «Otro»). El propietario añade: «no necesita nunca el [X]»; se replantea aparte porque choca con la regla anterior «cada fila conserva su marca de preparación».
- 2026-10-28 — Q5 Tipos de fuente de conjuro: «Confirmo la lista». Clase (con su nombre, sin subclase), Rasgo, Raza, Trasfondo, Dote, Objeto, Objeto mágico, Don, Otro. El supuesto del contrato pasa a regla confirmada.
- 2026-10-28 — Q4 Aviso de P3: el propietario sigue la recomendación del Worker «mientras respetes el contrato gráfico». Opción C: el nombre de cada campo que continúa («IDEALES A P. 4 · HISTORIA A P. 5»), en la franja, letra y color del golden. Implementado en C255–C266 ([checkpoint](../checkpoints/2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md)).
- 2026-10-28 — Q11 Velocidad en P1: «D 35 pies y 10,5 m». La tarjeta muestra pies y metros (5 pies = 1,5 m) a partir del valor guardado en pies; si no cabe, el criterio de Q7 (letra menor, luego dos renglones). Implementado en C255–C266 ([checkpoint](../checkpoints/2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md)).
- 2026-10-28 — Q10 Inspiración y PV TEMP en P1: «A». Tarjeta INSPIRACIÓN como el golden, con casilla marcada según el valor real de la ficha; PV temporales en pequeño dentro de la tarjeta PV ACTUALES («TEMP 7»). Implementado en C255–C266 ([checkpoint](../checkpoints/2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md)).
- 2026-10-28 — Q7 Valores por fuente en P1: «B y C en ese orden y mezclando». Primero una línea bajando la letra hasta 6 pt; si no cabe, dos renglones (también con letra menor si hace falta); si aun así no cabe, aviso «SEGÚN FUENTE» (P4 conserva siempre el detalle). Implementado en C255–C266 ([checkpoint](../checkpoints/2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md)).
- 2026-10-28 — Regla del propietario: «diseño debe llegar a ser igual (fuente, color, distribución, maquetación) al original … está en el contrato». Resuelve por el golden: Q2 tamaño de letra del golden en toda la hoja; Q8 dos filas de 10 por munición; Q9 rótulos de ataques del golden; Q12 español con la gramática del golden (casilla con P/E; Acción, Acción adicional, Reacción); Q13 detalles técnicos ajustados al golden donde el golden los muestra. Pendientes de implementar.
- 2026-10-28 — Q3 Espacios de conjuro en P1: «B, pero sin marcas». Casillas vacías (sin X premarcadas) y, al lado, un número pequeño «gastados/total» (Mara N1: 2/4), con la tipografía y el color del golden. Implementado en C255–C266 ([checkpoint](../checkpoints/2026-10-29_V11_C255_RESPUESTAS_IMPLEMENTADAS.md)).
- 2026-10-26 — P3 Historia: «Opción B: usa 7 pt sobre renglones de 17 pt como el diseño aprobado en Historia.» Contrato y C234.
