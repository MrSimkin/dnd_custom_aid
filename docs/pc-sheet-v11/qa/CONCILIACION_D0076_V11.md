# Conciliación D-0076 §§3.1–3.48 con V11

**Fecha:** 2026-11-05. **Revisión hecha por:** Claude Code, el mismo agente que implementa el renderizador. **No es la revisión independiente** que exige `G4_COMPUERTA_CUMPLIMIENTO_PRE_QA.md`, así que `legacy_48_sections` sigue en `PENDING_INDEPENDENT_CROSSCHECK` y las secciones siguen en `NOT_VERIFIED` en la matriz.

**Regla de precedencia** (contrato visual): los acuerdos V11 posteriores prevalecen solo en los puntos incompatibles. D-0076 §7 mantiene vigentes las 48 decisiones.

**Corrección del 2026-11-06:** la revisión independiente de `c6af6cc4` encontró diferencias en filas que aquí decían «sin diferencia» y yo las comprobé en los PDF. Esas filas están corregidas abajo. El estado de cada hallazgo está en [HALLAZGOS_REVISION_C6AF6CC4.md](HALLAZGOS_REVISION_C6AF6CC4.md).

**Disposición:** `RETAIN` significa que la decisión sigue obligando; `SUPERSEDED_BY_V11` que un acuerdo posterior del propietario la reemplaza, en todo o en la parte indicada.

## Resultado

| § | Tema | Disposición | Regla o decisión que la cubre | Hallazgo |
|---|---|---|---|---|
| 3.1 | Logo y nombre en toda página | RETAIN; posición SUPERSEDED (misma línea) | GLB03 | Sin diferencia |
| 3.2 | Elementos de la cabecera | RETAIN | P102 | La App no guarda jugador ni alineamiento: se imprimen como línea para escribir, sin inventar. **Confirmado por el propietario (2026-11-06)** |
| 3.3 | Retrato, modos, silueta | RETAIN; «cuadrado» SUPERSEDED por el marco del golden | IN04, IN05 | Sin diferencia |
| 3.4 | Jerarquía y campos | RETAIN; reparto 60/40 SUPERSEDED por el golden | P102, **P121 (nueva)** | **Corregido en C432:** la subraza guardada no se imprimía. **Corregido en C440:** multiclase con `/` |
| 3.5 | Variantes A y B | RETAIN; «lista separada de salvaciones» SUPERSEDED (fila de atributo, P104) | P106, P107 | Sin diferencia |
| 3.6 | Atributos y habilidades propios | RETAIN | P108, P109, C204 | **Corregido en C420:** en B las habilidades propias iban a C2 con filas libres en el panel |
| 3.7 | Combate y supervivencia | RETAIN; PV temporales SUPERSEDED (2026-10-28) | P110, **P123 (nueva)** | **Corregido en C437 por decisión del propietario (2026-11-05):** Dados de Golpe imprime el total del nivel (Guerrero 5 → `5d10`), no los que quedan |
| 3.8 | Ataques | RETAIN; alcance PRECISADO (2026-11-06) | P111, P119, P120, **P126, P127 (nuevas)** | **Mal revisado antes.** El alcance va junto al nombre **por decisión del propietario (2026-11-06)**, regla P126. Ya no se repite en C2 |
| 3.9 | Rasgos sin descripción | RETAIN | RSG01–RSG06 | Sin diferencia |
| 3.10 | Reparto Ataques/Rasgos | SUPERSEDED_BY_V11 (P112, 2026-11-03: filas fijas del golden) | P112 | Sin diferencia |
| 3.11 | Lanzamiento de Conjuros | RETAIN; casillas según el propietario, 2026-11-06: el máximo de cada nivel | P113–P116, **P128 (nueva)** | Sustituye a «mínimo 0» y a «una por espacio real». La App no tiene dato de reglas épicas: la fila 10–12 no puede activarse; una fila con espacios ≥10 guardados va a C2 |
| 3.12 | Filas, tonos y casillas | RETAIN; 7 mm SUPERSEDED (GLB08, 2026-11-03) | GLB08, GLB02 | Sin diferencia |
| 3.13 | Azul niebla A | RETAIN | GLB02, auditoría de paleta | Sin diferencia |
| 3.14 | Módulos inferiores (hipótesis) | SUPERSEDED_BY_V11 (la propia §3.15) | P117 | No aplica ya |
| 3.15 | Municiones | RETAIN; Tesoro/Otros SUPERSEDED por §3.19 | P117, **P122 (nueva)** | **Corregido en C433:** nunca se imprimía una segunda munición en P1 |
| 3.16 | Marco C2 y fuentes | RETAIN el marco; fuentes SUPERSEDED (2026-10-29) | GLB02, GLB04, GLB05 | Sin diferencia. Font Awesome no se usa: es secundaria y ningún rótulo la necesita |
| 3.17 | Rótulos Barlow, glifos | SUPERSEDED_BY_V11 en fuente; glifos RETAIN | GLB04, GLB05 | Sin diferencia |
| 3.18 | Cartelas C2c | RETAIN | GLB02 | Sin diferencia |
| 3.19 | P2, sin «Otros» | RETAIN | P201, P212 | Sin diferencia |
| 3.20 | Tesoro | RETAIN | P204, P205, P206 | Orden del golden aplicado en C425 |
| 3.21 | Equipo Especial híbrido | RETAIN | P208 | Sin diferencia |
| 3.22 | Casilla ACTIVO | RETAIN | P209 | Sin diferencia |
| 3.23 | P3 con Rasgos a la derecha | SUPERSEDED_BY_V11 (P3 solo narrativa, 2026-10-16) | P302, P303 | Sin diferencia |
| 3.24 | P4 tres columnas | RETAIN | P402–P406 | Sin diferencia |
| 3.25 | Espacios en la cabecera de nivel | RETAIN | P408, P409, **P412 (nueva)** | **Mal revisado antes; corregido en C440:** una casilla por espacio real. Falta la pregunta P3 para niveles sin espacios |
| 3.26 | Referencia y cabecera en una línea | RETAIN | P409, P410, **P413 (nueva)** | **Mal revisado antes; corregido en C440:** P4 imprime el valor de cada fuente |
| 3.27 | Notas y última hoja | RETAIN | FIN01–FIN04, CN302 | Sin diferencia |
| 3.28 | C-N3 | RETAIN | CN302, CN304, **CN305 (nueva)** | **Corregido en C431:** las notas generales salían antes que las tituladas |
| 3.29 | C2 compartidas | RETAIN | C201–C205, **C211 (nueva)** | **Corregido en parte en C440:** fila de habilidad del golden. Queda abierto el desborde del panel B |
| 3.30 | Ubicación de C2 | RETAIN el principio; orden exacto SUPERSEDED (golden: C2 tras P3) | C209, **P124 (nueva)** | **Mal revisado antes; corregido en C440:** la salvación competente no se marcaba |
| 3.31 | Prioridades y Notas aparte | RETAIN | GLB09, CN303 | **Mal revisado antes.** Hay datos guardados que no se imprimen: pregunta P4 al propietario. Municiones repetidas, abierto |
| 3.32 | Compositor automático | RETAIN | C202, IN08 | La App compone dos columnas y mixto; no compara las tres familias con una puntuación. §3.32 no fija algoritmo |
| 3.33 | Referencias de ida y vuelta | RETAIN; texto SUPERSEDED (C341) | C206, **C212, C213 (nuevas)** | **Mal revisado antes; corregido en parte en C441:** el pie nombra todas las páginas. Abierto: `VER C2` y tramos dentro de C2 |
| 3.34 | Libro al final | RETAIN | BK01, BK02, FIN04 | Sin diferencia |
| 3.35 | Sin conjuros, sin P4 | RETAIN | P401, P113 | Sin diferencia |
| 3.36 | P3 híbrida sin Rasgos | SUPERSEDED_BY_V11 (P3 solo narrativa) | P302 | No aplica ya |
| 3.37 | Equipo vacío | RETAIN | P203 | Sin diferencia |
| 3.38 | Ataques vacíos | RETAIN; número de filas SUPERSEDED (P112) | P112 | Sin diferencia |
| 3.39 | Municiones vacías | RETAIN | P118 | «Tipo:» añadido en C410 |
| 3.40 | P3 vacía se conserva | RETAIN; reserva de Rasgos SUPERSEDED | P301 | Sin diferencia |
| 3.41 | Tesoro vacío | RETAIN | P206 | Sin diferencia |
| 3.42 | Equipo Especial vacío | RETAIN | P210 | El golden trae 8 rótulos; prevalece la decisión escrita |
| 3.43 | P2 toda vacía | RETAIN | P211 | Sin diferencia |
| 3.44 | Sin notas, sin C-N3 | RETAIN | CN301 | Sin diferencia |
| 3.45 | Sin imágenes en P3 | RETAIN; «dos opciones para el retrato» SUPERSEDED (propietario, 2026-11-05) | P306 | **Decidido por el propietario (2026-11-05):** las imágenes del Trasfondo no van al PDF; sin retrato asignado, P1 imprime la silueta |
| 3.46 | Libro en dos columnas | RETAIN | BK03 | Sin diferencia |
| 3.47 | Ficha esencial | RETAIN; posición de NIVEL SUPERSEDED por el golden | BK03, BK06 | Sin diferencia |
| 3.48 | Ficha unida si cabe | RETAIN | BK04, BK05 | Sin diferencia |

## Reglas nuevas que salen de esta conciliación

- **P121:** el campo Raza incluye la subraza guardada (§3.4).
- **P122:** una segunda munición se imprime en P1 cuando sus marcas caben en una subfila (§3.15 y golden).
- **CN305:** las notas tituladas van antes que las generales (§3.28).
- **P123:** Dados de Golpe imprime el total del nivel, por decisión del propietario (§3.7).

## Decisiones del propietario tras la conciliación

**Las dos imágenes del Trasfondo (§3.45).** El propietario había dicho que «las imágenes son las dos opciones posibles para el retrato». Preguntado el 2026-11-05, decide «No, deja la silueta»: el retrato sale solo de `portraitRef` y un PJ con imágenes guardadas y sin `portraitRef` imprime la silueta.

## Lo que no cubre este documento

No demuestra que el PDF cumpla cada regla: eso lo dice cada control de la matriz. Tampoco sustituye a la revisión independiente, ni a la comparación de los ocho PDF golden.
