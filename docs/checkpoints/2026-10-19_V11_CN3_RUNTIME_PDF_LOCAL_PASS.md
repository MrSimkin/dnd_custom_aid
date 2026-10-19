# V11 — C-N3: PASS LOCAL con PDF auténticos; continuidad reactivada

**Fecha:** 2026-10-19 · **rama única:** `docs/pc-sheet-v11-approved-implementation-handoff` · **PR #173:** DRAFT, **sin merge**. **Fuente exacta probada:** `15a99f830b7ddc5c2f6f0ffe787d559e6a7848e1`, [Scaffold #39321593761](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39321593761) **SUCCESS** (3/3 jobs: Kotlin, backend, hosted-database).

## Desbloqueo necesario, defecto previo demostrado

Las ejecuciones Scaffold de los HEAD anteriores (p. ej. [#39319839631](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39319839631)) terminaron **FAIL** en `scripts/check_resume_route.py` ANTES de iniciar Gradle: «canonical active checkpoint is missing from LATEST.md Read first on resume». La línea `Canonical active checkpoint` había quedado anclada a `2026-10-19_V11_G4_MODULE_TOPOLOGY_REAL_PDFS.md`, mientras `## Read first on resume` nombraba otro. La corrección `15a99f830b7ddc5c2f6f0ffe787d559e6a7848e1` alineó en `docs/checkpoints/LATEST.md` ambas referencias al checkpoint CN3 más reciente, sin cambiar Kotlin, los gates, la matriz o contratos. GitHub CI pasó el guardia y completó Gradle y la auditoría física: **defecto de navegación REPARADO**.

## Evidencia real del C-N3 anteriormente implementado

**Artefacto nuevo exacto:** [68 PDF App/Desktop](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39321593761/artifacts/12221230614) (no ReportLab), vs [64 PDF App/Desktop baseline](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193379904/artifacts/12160714181) / **589 folios**. El nuevo lote añade CUATRO PDF de regresión `CN3-manuscript-{A,B}` y `CN3-long-source-title-{A,B}`.

Comparación independiente PyMuPDF de **los 64 PDF comunes**:
- **64/64 idéntica cantidad de folios**; **64/64 palabras y cajas de texto idénticas** con redondeo de coordenadas a 0,1 pt; cero registros faltantes y cero excepciones.
- **589 folios**: las únicas diferencias de rutas vectoriales se limitan a **64 páginas C-N3**; las rutas originales siguen intactas y solo se agregaron trazos horizontales a color de manuscrito. **CERO diferencias de trazos fuera de C-N3**, comparando caminos de dibujo anteriores completos; las otras cuatro C-N3 densas no adquirieron líneas nuevas.
- **64 páginas C-N3** recibieron entre **7 y 36** líneas nuevas; separación visual medida entre palabra fuente más baja y primera nueva línea **mínimo 25,97 pt** usando coordenadas PDF; todas las líneas nuevas son horizontales, ancho x42..570, dentro del papel, y no alteran el número de folios.
- **Mara A p8**: se inspeccionó raster nuevo, 10 notas reales mantienen títulos/contenidos y 7 líneas manuscritas quedan bajo el último párrafo. **Aldren A p6**: raster nuevo muestra las 36 líneas bajo su única nota corta sin solapar fuente. Ambas evidencias son PDF del run nuevo, no simulación.
- **Caso límite de título largo, A y B:** 11 folios por variante, C-N3 ocupa páginas físicas **8–10**; en las continuaciones **9 y 10** figura explícitamente el UUID fuente `7a300000-0000-4000-8000-000000001000`; inicio y fin de la nota presentes exactamente **una vez**. Se inspeccionó raster del folio 10 y su área manuscrita, sin corte visible.
- **Tests del proyecto Kotlin** y auditoría física independiente completaron con éxito en Scaffold del mismo SHA. El estudio confirma los dos bloques C-N3 recientes: reserva manuscrita secundaria y fallback de identidad fuente.

**Interpretación limitada:** C-N3 recibe **PASS LOCAL** para estas rutas A/B Desktop y la no regresión de los PDF anteriores. El código Android se mantiene sincronizado en ambos cambios, pero **no** se ha instalado ni inspeccionado PDF de Android real, por tanto **NO hay PASS Android/owner** ni aprobación global.

## Control de calidad y autoridad

- **G0** owner/legado parcial (preservar M50800); **G1** PASS semántico previo; **G2/G3** PARCIALES, con el bloque C-N3 scoped LOCAL PASS; **G4 BLOCKED**, las **105/105** reglas permanecen `NOT_VERIFIED`, `active_candidate=null`; **G5/G6 PENDING**.
- Golden V11, D-0076, matriz de reglas y `qa/REGISTRO_QA_V11.json` **sin modificar**; RCR-1 0/2. PR #173 DRAFT, NO merge. Este PASS local no significa aprobación técnica ni visual del propietario.

## Próximo trabajo concreto

1. No repetir las pruebas de C-N3 ya demostradas salvo regresión concreta de nuevo código.
2. Continuar **G2/G3 funcional en P3/C2** solo donde un PDF fuente muestre incumplimiento concreto frente a golden/contrato; corregir agrupado Android+Desktop y conservar evidencia PDF real del mismo commit.
3. Construir a partir de los PDF definitivos un candidato único para **G4** y completar las 105 reglas por ID con reconciliación de golden y Android. Nunca otorgar PASS global por tests aislados.
