# V11 P3 — recuperación de la gramática azul-niebla del diseño aprobado

**2026-10-20 | Base:** PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT, rama `docs/pc-sheet-v11-approved-implementation-handoff` HEAD `d8b7db19b808b2955956c0fb05ff584683556926` (CI Scaffold #39491483007 y QA #39491483060 **SUCCESS**). `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`. **NO MERGE.**

## Evidencia de desviación

La **P3** de la maqueta aprobada `v11_extremo_variante_B.pdf` p3, ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`, presenta renglones narrativos con **bandas azul-niebla alternadas** y seis cabeceras de perfil también tintadas. La App auténtica, `V11-INTERNAL-PROOF-Mara-B.pdf` p3 del artefacto [#12295018331](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39490469236/artifacts/12295018331), tenía **seis rellenos uniformes 256×45pt, dos columnas de historia uniformes y seis cabeceras blancas**. Se comprobó en vectores/PyMuPDF, no en supuesto estético. La reserva geométrica y textos fuente ya eran correctos; no se modifica su semántica.

## Cambio funcional preciso

Ambos renderers de **App Desktop y Android** aplican franjas alternadas `RGB(231,242,247)` sobre rellenos base `RGB(238,247,251)` a cada segundo **renglón ya medido** de seis campos, dos columnas de historia y páginas P3 de continuación; 6 cabeceras `TRASFONDO`, `RELIGIÓN/FE`, `RASGOS DE PERSONALIDAD`, `IDEALES`, `VÍNCULOS`, `DEFECTOS` reciben banda de 16pt **dentro del espacio existente**, antes de dibujar el texto. El grosor y lugar de renglones, contenido, paginación, cuadros, el diseño C2, las 4 familias legacy y C211 no cambian.

No se crea auditor adicional. Se extiende **`scripts/audit_v11_p3_c2_manuscript.py`**, ya invocado en Scaffold, para exigir seis bandas y ocho filas tintadas medibles en P3 primera página, y presencia de filas tintadas en ambas columnas P3 continuación; preserva controles previos P305/C207/C208, colisión con texto y cuadrículas.

**Estado al implementar:** código publicado, **CI del commit exacto y comparación de PDF aún PENDIENTES**. Condiciones para PASS LOCAL restringido: Scaffold/QA correctos, 76 PDF genuinos sin cambios de páginas/posiciones de palabra, vectores inalterados fuera P3 y franjas físicas correctas. No inferir paridad integral con 8 golden ni aprobación visual/técnica del usuario.

**Gates:** G0 PARCIAL, G1 PASS semántico previo, G2–G3 PARCIALES, **G4 BLOCKED con 105/105 NOT_VERIFIED y active_candidate=null**, G5–G6 pendientes, Android instalado/QA owner pendientes. RCR-1 C211 normal agotado 3/3, recuperación estructural 0/2 sin iniciar. PR #173 DRAFT no fusionar.

**Siguiente:** revisar CI exacto y artefactos, registrar evidencia física o fallo. Después continuar otra diferencia concreta fuera de C211 sin reauditar zonas cerradas.
