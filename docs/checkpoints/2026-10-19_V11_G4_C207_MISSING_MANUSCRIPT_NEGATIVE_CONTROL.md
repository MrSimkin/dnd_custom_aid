# V11 C207 — detector de RESERVAS MANUSCRITAS AUSENTES (control negativo real)

**Fecha:** 2026-10-19 · **PR exclusiva:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), DRAFT, SIN MERGE · commit productivo/control `9700bc1c03a9f36b88fd7a798598d394bb5fe404` · [Scaffold #39342450957](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39342450957) **SUCCESS** (Kotlin + backend + DB + auditoría física independiente) · [ZIP auditoría nueva](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39342450957/artifacts/12229493866) (SHA-256 `b2d1781a662121486c149a02023fcfeb8cad0ba6ed21c6e5666a8360631a38fc`).

## Trabajo nuevo y razón

El control precedente `audit_v11_p3_c2_manuscript.py` verificaba que las cartelas `NOTAS` existentes tuvieran una cuadrícula completa, pero **no podía detectar una cartela ausente** en papel libre. Se añadió `scripts/audit_v11_c207_vacancy.py` más **cuatro pruebas negativas/positivas** `scripts/test_audit_v11_c207_vacancy.py` y paso real en `.github/workflows/scaffold-check.yml`. A partir de palabras fuente originales y trazos PDF auténticos evalúa huecos INFERIORES >=128 pt sin reserva, por lado cuando hay dos parejas de columnas, o como región entera cuando `Equipo Especial` es una tabla de ancho completo. No reubica fuentes ni escribe PDF: solo comprueba y puede hacer FAIL del CI con archivo/página exactos.

## Prueba negativa REAL vs candidato

- Lote auténtico **anterior a la corrección de C207**, 68 PDF Desktop, ZIP SHA `795b0aa70cc2067a6f9e63052b2799e771160e4083dc413dc837aa26008530cc`: resultado `FAIL` solo en `C206-both-paired-overflow-A.pdf` y `-B.pdf`, página física **6**, pareja derecha con **369,41 pt** de altura inferior libre sin `NOTAS`; **2 omisiones**. Es el mismo defecto físicamente corregido en el checkpoint `2026-10-19_V11_G2_G3_C2_ASYMMETRIC_TABLE_NOTES_LOCAL_PASS.md`, no un defecto nuevo que requiera modificar otra vez los renderizadores.
- Lote Desktop actual de **72 PDF**, ZIP previo SHA `5378503652d00bba27adcd161fe3dfd324b9075678daea997150b93190bb8775`: `PASS_SCOPED` con **197 páginas físicas C2**, **358 regiones**, **78 franjas menores al umbral**, **289 regiones cubiertas por cuadrículas existentes**, **cero reservas inferiores omitidas**. Cuatro controles de test verifican: falta de notas a derecha, ambas parejas llenas, Equipo Especial ancho completo lleno por su zona derecha (NO falso positivo) y Equipo Especial completo con espacio inferior verdadero.
- Nuevo CI del mismo commit: [Scaffold SUCCESS](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39342450957) y [artefacto auditores](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39342450957/artifacts/12229493866); `v11-c207-missing-reservations.json` guarda **SHA-256 de los 72 PDF**, métricas, y lista de errores/páginas. El ZIP original con los **ocho golden** se abre/verifica sin corrupción y sus ocho SHA-256 quedan registrados; esto es **inventario y custodia**, no una comparación de todas las reglas visuales contra ellos.
- Comparación de PDFs existente P3/C207/C208 no se repitió como trabajo productivo ni generó cambios en los dos renderizadores: las 72 salidas mantienen la geometría/celdas verificadas por el bloque anterior.

## Interpretación estricta

**C207 recibe nueva evidencia física LOCAL sobre omisiones inferiores**, pero **C207/G3 sigue NOT_VERIFIED** porque el umbral de 128pt es un detector conservador y no prueba por sí solo todos los huecos interiores/medios, ni el contrato visual caso a caso contra los ocho PDF congelados. La mera presencia de ocho hashes no significa `golden_eight_pdfs=COMPLETE`. Tampoco es salida de Android instalado.

**Gates SIN ALTERAR:** G0 histórico parcial; G1 PASS semántico previo; G2/G3 parciales; **G4 BLOCKED con 105/105 reglas globales `NOT_VERIFIED` y `active_candidate=null`**; G5/G6 pendientes; RCR-1 **0/2**. Mantener D-0076, golden, registro QA owner y familias legacy intactos. Usuario **NO** ha aprobado técnica ni visualmente implementación ni producto; #173 no fusionada.

## Trabajo previsto

En lugar de volver a comprobar las 218 cuadrículas ya acreditadas, **contrastar los casos equivalentes P3/C2 de los ocho golden por geometría/tipografía real y precedencia D-0076**, incluido el espacio manuscrito que no esté en la zona inferior. Seleccionar un incumplimiento material solo si su PDF real demuestra una diferencia; si es necesario, reparar Android+Desktop como bloque y ejecutar tests A/B. Preparar progresivamente candidato único G4 y verificaciones Android reales; no habilitar owner QA antes del gate.
