# V11 P4 — bandas azules al continuar niveles entre columnas

**2026-10-20, PR #173 DRAFT NO MERGE.** Base remota `206c2dba5368ef359b123c6909c5f48327c71ca4`; `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`; [Scaffold #39498803678](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39498803678) y [QA traceability #39498803599](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39498803599) del HEAD anterior **SUCCESS**.

## Evidencia primaria

El golden aprobado `v11_ordinario_6mas2_variante_B.pdf` p4 (ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`) comunica niveles con banda azul. En PDF App auténticos Ilyra-B p6 y Mara-B p7, del artefacto [#12298907449](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39497528573/artifacts/12298907449), los niveles realmente continuados a columnas 2 y 3 imprimían `NIVEL n / CONTINUACIÓN` como texto Fira suelto en la reserva y607..617; sin banda ni tarjeta. PyMuPDF baseline: esos rótulos en x224/404 y PDF top-y175.46–183.85, pero en esa misma región **no hay fills/clipped frame**; las cartelas de niveles originales sí tienen sombreado. No tocar renglones P4 fuente, indicadores ESP ni cajas manuscritas.

## Cambio funcional

En los renderers reales PDF Kotlin Android y Desktop se añade una cartela **de ocho lados**, fondo `RGB(217,236,245)`, borde `RGB(80,122,151)` de 0,45pt, solo si la columna comienza con un elemento de un nivel real ya iniciado. Ocupa estrictamente la **banda original y607.3..616.3 (9pt)** y x=anchura real de columna; conserva exactamente la fuente, tamaño, baseline, ubicación y texto del rótulo real (no inventa niveles), y todas las palabras/celdas. Mantiene 0,7pt hasta tarjetas mágicas y 0,3pt hasta primera fila real. El diseño completo P4 NO se declara igual al golden por esto.

Se **amplía el auditor físico P4 existente** `scripts/audit_v11_p4_slot_meters.py` (Scaffold ya lo ejecuta): exige que cada palabra física `CONTINUACIÓN` en la zona del nivel de P4 posea cartela azul recortada real en su columna, sin alterar las verificaciones anteriores 0–9 niveles, medidores ESP y 27 casillas vectoriales. Nada se añade a la matriz de reglas. Validación proporcional: CI exacto, PDF 76 casos, comparar coordenadas de texto/páginas/vectores fuera P4 contra base, Ilyra y Mara.

**Estado de publicación:** IMPLEMENTADO, CI/PDF exactos **PENDIENTES**. Nunca convertirlo a PASS formal sin pruebas ni a aceptación Android/propietario. No reintentar C211.

**Gates inmutables:** G0 histórico PARTIAL, G1 semantic PASS previo, G2/G3 PARTIAL, **G4 BLOCKED 105/105 NOT_VERIFIED active_candidate=null**, G5/G6 NOT_STARTED; QA Android instalado y aprobación del owner ausentes. C211 normal BLOCKED 3/3; RCR-1 estructural 0/2. PR #173 OPEN DRAFT SIN MERGE.

**Siguiente:** comprobar CI y comparación PDF del commit exacto; registrar PASS LOCAL limitado o fallo. Luego otra diferencia medible independiente o paso de reconciliación formal sin repetir trabajo.

## Cierre físico del commit funcional exacto — PASS LOCAL P4 continuación (NO G4)

El commit `d851c47da22fecd1f9749091d21a31369e2dfb03` cerró [Scaffold #39500838628](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39500838628) y [QA traceability #39500838621](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39500838621) ambos **SUCCESS**. Kotlin Desktop/Android APK, backend y DB pasan. [76 PDF reales App #12301641077](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39500838628/artifacts/12301641077) ZIP SHA256 `51c4c7a102bf7c695fed6ea259b4d9444b9528923c511d37669a826d1c1b9df8`; [auditores existentes #12302400060](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39500838628/artifacts/12302400060) ZIP SHA256 `62fff5fee02bb167ef39c38f5703a519ef31dbd79941fd01454f53932fc5368d`.

**Comparación PyMuPDF externa con el HEAD anterior**: mismos 76 PDF y 711 páginas; **711/711 con las mismas palabras y coordenadas**, **662/662 folios ajenos a P4 con dibujo vectorial idéntico**, **49/49 folios P4 preservan TODOS los trazos anteriores** y añaden exclusivamente **94 cartelas** de ocho lados con tinta/ubicación exactas: una por cada etiqueta auténtica `CONTINUACIÓN`. **0 errores**. Auditor físico P4 ampliado PASS sobre 76 documentos (**45 casters**) con **94 bandas continuadas**, mantuvo niveles normales 0–9, nueve medidores ESP. y tres cuadrados por nivel. Informe de comparación local SHA256 `a7026e6b9ea1e4a9a6b6af33eee6fdc884dfaae62c78169252ec1a9bdca52ed3`. También se verificó visualmente el recorte PDF Ilyra B P4: ambos contextos NIVEL 2/6 tienen ahora banda sin mover el rótulo ni la lista.

[Acta JSON estructurada](../pc-sheet-v11/qa/evidence/2026-10-20_P4_CONTINUED_LEVEL_BLUE_C2C_REAL_PDFS.json).

**Precisiones**: es PASS LOCAL del pequeño subalcance P4, NO paridad P4 completa ni PASS de G4, ni autorización técnica/visual del propietario; APK compilado NO instalado. G0 PARTIAL, G1 semantic PASS previo, G2/G3 PARTIAL, G4 BLOCKED **105/105 NOT_VERIFIED; active_candidate=null**, G5/G6 pendientes, C211 intacto BLOCKED normal 3/3, RCR-1 estructural 0/2. PR #173 DRAFT NO MERGE.

**Siguiente desarrollo**: otro defecto comprobable que afecte la fidelidad fuente/gráfica del P4 restante o módulo distinto; priorizar una operación real y pequeña sobre diagnósticos abstractos, con cotejo proporcional, antes de concluir G4/owner QA.
