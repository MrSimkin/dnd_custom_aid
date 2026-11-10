# V11 G2/G3 — marco físico doble exterior del golden en TODAS las páginas

**Fecha:** 2026-10-18. PR #173 DRAFT, solo rama `docs/pc-sheet-v11-approved-implementation-handoff`, sin merge, `main` intacto. HEAD base verificado `bf55da2431ee88f83b1ac9603170121d012c4de3`. Anterior [Scaffold #39165353061](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39165353061) SUCCESS, 54 PDF/510 páginas; no se reabre el caso monetario ni los anteriores.

## Diferencia primaria, medidas y reproducción negativa

Se cotejaron los **ocho PDFs golden** del ZIP aprobado inmutable SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`. TODOS los folios golden tienen los mismos **primeros dos dibujos** PDF vectoriales (Carta 612×792):
- Marco exterior: x26.3622..585.6378, y24.9449..767.0551 (coordenadas PyMuPDF top-down); grosor 0.95pt, RGB (38,75,104).
- Interior: x30.6142..581.3859, y28.9134..763.0866; 0.38pt, RGB (191,211,222).

Las dos versiones reales de App imprimían en todos los folios, de forma incompatible, (24,24..588,768) a **1.2pt** y (29,29..583,763) a **0.65pt** con un único RGB (80,122,151). La reproducción negativa del oráculo independiente sobre el ZIP **#12150547330** del candidato anterior registra **0/510 páginas conformes** (54 PDF; fallo esperado), frente a todos los golden consistentes. Esto es una divergencia física repetida observable en los ocho diseños, NO una preferencia de diseño nueva.

## Corrección funcional delimitada

Ambos PDFBox V11 reemplazan SOLO los dos dibujos iniciales de cada página por las posiciones, colores y grosores golden reales, preservando el resto de la cabecera, rutas de datos, página Carta, filas P1–P4, C2, notas, fuentes, identificadores y pie. No se retoca la plantilla original, imagen logo, saldos, rasgos, hoja manuscrita NOTAS ni las cuatro familias heredadas. Se añade `scripts/audit_v11_double_outer_frame_golden.py` al Scaffold normal: carga **los ocho PDF golden** del ZIP custodiado, confirma que todos sus marcos coinciden y coteja posición/grosor/RGB de los DOS vectores en cada página App real; fail-closed, tolerancia 0.025pt / 0.004 RGB y JSON de hashes dentro de `v11-independent-physical-envelope-audit`.

**Estado de este commit al publicarlo:** pruebas CI de compilación, 54 PDF App y nuevo auditor PENDIENTES. Nunca declarar PASS antes del nuevo informe. El test anterior negativo no es PASS del cambio. Una vez finalizado, comprobar sucesión de páginas, texto, dibujos de otras regiones, auditorías NOTAS/retrato y paridad A/B en las salidas genuinas; informar solo PASS LOCAL del marco doble.

## Continuidad contractual

**G0 parcial, G4 BLOCKED, 105 obligaciones globales NOT_VERIFIED y active_candidate=null; G5/G6 pendientes.** Este cotejo NO subsana por sí solo cabecera/logo completo, marcos interiores de otras cartelas, todos los rasgos, diferencias globales con ocho golden, cuatro familias legacy ni APK instalado/QA técnico-visual owner. No modificar la matriz para atribuir PASS global, ni fusionar PR. RCR-1 sin activación, solo cambios de causa constatada.

**Siguiente trabajo:** completar la reconciliación por grupos contractuales de G4 mediante PDF real y ocho golden, dando preferencia a incumplimientos sustantivos que ya tengan prueba. No repetir este marco exterior tras pasar su oráculo.

## Resultado verificable del candidato compilado (2026-10-18)

**Código exacto:** [`6cb177a6011cc4d38eec1c72f9a9142736d5497c`](https://github.com/MrSimkin/dnd_custom_aid/commit/6cb177a6011cc4d38eec1c72f9a9142736d5497c). [Scaffold #39168510609](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39168510609) **SUCCESS**, trabajos Kotlin (Desktop/Android DEBUG), backend y hosted-database aprobados. [QA-traceability #39168510698](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39168510698) **SUCCESS documental**, sin QA humano ni dispositivo.

- **[54 PDF reales de App #12151881264](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39168510609/artifacts/12151881264)**, ZIP SHA-256 `ebf1af9f04fd717ac58ed7d7cf83e5c07ee82fca6f58f965267e59b2c84ef0cb`, CRC PASS, 54 archivos, 510 páginas Carta.
- **[Informe de cuatro auditores #12151578370](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39168510609/artifacts/12151578370)**, ZIP SHA-256 `ee8be2adfe5a481dab5eabc83b0765a13205c22888b21ee8b31aea1f4c25c724`, CRC PASS. `v11-double-outer-frame-golden.json`: **8 goldens / 59 páginas de referencia consistentes, 54/54 PDFs App y 510/510 páginas verificadas, 0 fallos** de geometría, grosor y RGB. `v11-physical-envelope-audit.json`: **0 alertas**; `v11-final-notes-golden.json` **54/54 PASS LOCAL**; `v11-portrait-placeholder-golden.json` **54/54 PASS LOCAL**. Los tres controles anteriores siguieron intactos.
- **Reproducción negativa previa realmente ejecutada:** exactamente el mismo inspector, sobre ZIP candidato anterior [#12150547330](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39165353061/artifacts/12150547330), dio **0/510 folios conformes**; golden internamente consistente. Verifica sensibilidad contra el defecto concreto, sin simular un PASS.
- **No regresión fuente→PDF:** comparación independiente **54/54 PDF comunes, 510/510 páginas**, misma paginación, misma lista de palabras con coordenadas, igual cantidad de dibujos e imágenes, y **ninguna diferencia detectada entre las propiedades de todos los trazos posteriores a los primeros dos** (bounds, color, fill, grosor y tipo). Solo cambian los dos trazos exteriores planeados; no se afirma identidad byte a byte ni de todos los operadores.
- Los golden originales aprobados, la matriz de 105 obligaciones, otras familias, M50800 y V11-QA-001/002 se mantienen sin alteración; `main` sin cambios.

**Veredicto:** **G2/G3 PASS LOCAL solo de los dos marcos exteriores de TODOS los folios A/B Desktop**, con código Android equivalente compilado, nunca instalado/validado por owner. El logotipo/cabecera restante y los ornamentos de esquina NO están declarados equivalentes. **G0 PARCIAL, G4 BLOCKED (105 reglas globales NOT_VERIFIED y active_candidate=null), G5/G6 pendientes.** La PR #173 permanece DRAFT sin merge. No se gastó RCR-1.

**Siguiente bloque:** priorizar diferencias aún reales de cabecera/logo/paginación/referencias y composición de módulos con cotejo de ocho golden y D-0076. Abordar G4 por lotes de reglas con evidencia independiente por candidato; no repetir auditoría del marco doble.
