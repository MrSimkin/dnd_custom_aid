# C227 — Seis espacios de conjuro reales: seis casillas manuscritas en P1

**Fecha:** 2026-10-22 (Chile). **Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT, **NO MERGE**. **Estado inicial: IN_PROGRESS; no PASS sin CI y cotejo de PDF reales**.

## Contrato, evidencia original y solución acotada

D-0076 §§3.11–3.12 y matriz P115–P116: representar espacios digitales reales e incluir casillas utilizables en P1 sin desbordar. El **PASS LOCAL C226** está demostrado para hasta cinco casillas (mismo 7pt y paso 12pt). La última celda P1 tiene 85pt de ancho, pero solo 78pt reservados para las marcas después del origen local x+19pt. Seis casillas con 12pt de paso exceden el margen: `19+5×12+7=86 > 78`. Seis casillas de tamaño **idéntico** con 10pt de paso **sí** caben: `19+5×10+7=76 ≤78` (3pt de separación libre), sin desplazar números, etiquetas o filas, ni tocar dibujo externo. Esto es una adaptación de separación dentro de la gramática física aprobada, no una nueva tarjeta. Para **siete o más** no existe solución igualmente respaldada por este margen; conservar contadores originales sin inventar seguimiento ni páginas.

## Código y verificación requerida

- Android y Desktop V11: `drawP1Spellcasting` usa seis cuadrados de **7×7pt**, paso **10pt** únicamente si `totalSlots == 6`. Las cantidades 1–5 conservan el paso **12pt** probado y los valores 0/7+ permanecen sin nueva marca. La guarda verifica cabida real `19+(n-1)*paso+7≤78pt`. Todas las fuentes, P4, otras páginas, familia legacy, espaciados y reglas de overflow quedan intactos.
- Prueba en el **servicio Desktop real**, desde backup auténtico de Ilyra: solamente nivel 1 se cambia de 4/1 a **6/4**; se mantienen nivel 2 **3/1**, demás conjuros y fuentes, variantes **A/B**. Genera dos PDF reales. Inspección interna del contenido PDFBox exige **seis cuadrados 7pt** en x={334,344,354,364,374,384}, y=210, prohíbe séptimo; nivel 2 conserva sus tres marcas con paso 12pt; tamaño Carta y NOTAS intactas.
- Verificación externa obligatoria y proporcionada: Scaffold Kotlin/Android/Desktop y QA registry exacto; extraer PDFs de la ejecución **C227**; comparar los **88 PDF C226 / 809p** con su nuevo lote y admitir diferencias solo en casillas/X P1 de seis espacios. Revisar tamaño, posiciones, pérdida textual, vectores e imagen, y raster de las dos muestras nuevas. Si no hay bytes de candidato o hay discrepancia, dejar **IN_PROGRESS/BLOCKED** con diagnóstico limitado, no un PASS artificial.
- Línea base C226: commit `e104cec3976b0fe1ad6a9e868fc758e8ed9f9d6e`, cierre `c00403e9a1b53d91e7857c5151cd545ccaedec29`, CI [Scaffold #39824298970](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39824298970) SUCCESS y [QA #39824298962](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39824298962) SUCCESS, [artefacto 88 PDF #12446397671](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39824298970/artifacts/12446397671) SHA-256 ZIP `fc4787d05d2a01f348ddf583c33e3ec1fa013be3eff1c3226d6776b9ae182de2`. El PASS anterior no sustituye las pruebas C227.

## Límites/autoridad

Mantener `G0` PARCIAL, `G1` PASS semántico histórico, `G2/G3` PARCIALES, `G4` BLOCKED **105/105 NOT_VERIFIED** con `active_candidate=null`, `G5/G6` pendientes. C211 sigue BLOCKED con 3/3 intentos ordinarios y 0/2 recuperación estructural. Sin editar matriz G4, observaciones M50800-01..32, V11-QA-001/002, contrato ni historial; QA del propietario e instalación Android no aprobados. **No se autoriza merge**.

**Próximo paso:** ejecutar/leer CI exacto y hacer comparación de PDF real; actualizar este checkpoint con hashes y resultados. Para 7+ casillas, registrar como necesidad de composición/decisión o seleccionar otra brecha independiente: no apretar los controles a dimensiones ilegibles ni añadir líneas o páginas sin aprobación.
