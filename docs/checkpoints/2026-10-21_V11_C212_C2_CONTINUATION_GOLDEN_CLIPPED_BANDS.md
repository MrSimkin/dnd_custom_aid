# C212 — banda azul C2c para encabezado real de grupo/atributo continuado

**Fecha:** 2026-10-21 · **Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff`, [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT / NO MERGE. `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`; HEAD previo `74ffd36243100d5bf7716e2315c2c7c867272e59`, [Scaffold #39634133232](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39634133232) + [QA #39634133239](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39634133239) SUCCESS.

## Fuente original inspeccionada; hallazgo independiente de C211

Se recuperó el ZIP V11 aprobado a través de [Scaffold #39634133232](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39634133232), `v11-independent-physical-envelope-audit` (incluye el ZIP golden original, no generar otro prototipo), y **76 PDF Desktop verdaderos** de esa misma corrida. Se abrieron/rasterizaron [golden extremo A p4](https://github.com/MrSimkin/dnd_custom_aid/blob/docs/pc-sheet-v11-approved-implementation-handoff/docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip) y **Mara-A PDF App p4**. La cartela azul recortada C2c es la gramática del golden para identificar módulos; el PDF App ya da `CONTINÚA: RECURSOS Y MUNICIONES` en x314.., y78..87 de la columna derecha, pero como **texto flotante** sin cartela. El C210 anterior ya resolvió **qué grupo** continúa, con prueba real. No repetir ni alterar C210.

Inspección PyMuPDF de los 76 PDF en HEAD previo: **34 páginas C2 con 38 captions de grupos `CONTINÚA:`** arriba, y **4 contextos `CONTINÚA ATRIBUTO:`** adicionales, total **42 encabezados reales** elegibles, sin marco contextual en banda y76..89. Este defecto de presentación es nuevo y acotado, no procede del owner QA histórico Mara 50800 ni reclama cerrar sus 32 observaciones.

## Intervención productiva limitada y pruebas

- Android y Desktop V11 añaden el mismo helper `drawC2ContinuationCartela`: banda C2c cortada en esquinas, tinta/contorno azul sobre la **zona de encabezado ya libre de 703..716pt**, debajo de la línea y717 y encima del primer registro medido y702. **No** se altera texto, fuente, caption, identificador, orden, página, columnas ni paginador. Se aplica a **ambas** rutas existentes: encabezado de grupo y atributo custom cuando realmente continúan.
- Guard de paridad `scripts/test_v11_c2_source_navigation_contract.py` ahora exige las dos llamadas en ambos renderers, además de mantener las referencias físicas originales.
- El auditor real existente `scripts/audit_v11_p3_c2_manuscript.py` compara las palabras fuente `CONTINÚA` con un panel físico azul exacto de 13pt. Exige caso real, sin crear auditor duplicado. Auditores C207, P3 y finales permanecen obligatorios.
- Pruebas esperadas: nuevo Scaffold Kotlin/Android/Desktop y QA traceability del **SHA exacto**, los 76 PDF App de CI (A/B) con **42/42** contextos en cartela; comparación before/after sobre 711 páginas, mismas palabras/posiciones/páginas y sin dibujos alterados fuera de dichos encabezados. NO declarar PASS sin completar toda esa verificación.

## Continuidad y bloqueos

**Al publicar código:** C212 `IMPLEMENTADO / VALIDACIÓN PENDIENTE`. G0 histórico parcial; G1 semántico PASS previo; G2/G3 parciales; G4 BLOCKED **105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes; RCR-1 C211 sigue `BLOCKED 3/3` normal, `0/2` estructural, sin reproducirlo. QA Android instalada, aprobación técnica/visual owner y merge NO realizados. Siguiente acción: verificar exact CI, inspección física real y diff restringido. Solo si PASS LOCAL de C212, continuar a otra brecha sustantiva y a reconciliación modular G4 sin falsos PASS.

## Verificación del candidato real: PASS LOCAL restringido a C212

**Commit productivo exacto:** [`c33ca92ed2`](https://github.com/MrSimkin/dnd_custom_aid/commit/c33ca92ed2f186989b3529316344daa20e175d75), sin tocar familias anteriores ni refactorizar C211. GitHub Actions [Scaffold #39640521647](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39640521647) **SUCCESS** (Kotlin shared/Desktop, compilación Android, auditorías PDF, backend y DB). [QA traceability #39640521109](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39640521109) **SUCCESS** (paridad de ambos renderers e inventario G4).

**Artefactos originales del run nuevo:** [76 PDF reales de la App Desktop #12365134171](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39640521647/artifacts/12365134171), SHA-256 ZIP `0c7d882eea93dcc81a90a64deec5fa7e7ffbf5b5a4932e805ae5d513d677d6e7`. [Auditores físicos #12364864271](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39640521647/artifacts/12364864271) incluyen reporte del C2 y el ZIP golden. Comparación independiente offline PyMuPDF con los [76 PDF anteriores #12359299975](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39634133232/artifacts/12359299975) del run previo (ZIP SHA `a5149fbe073c4a86509e5af5d3746f3d1f4e430f11ce1e9b2543ec05a1234168`):

- **76/76 archivos, 711/711 páginas**, igual cantidad y **todas las palabras y coordenadas idénticas** (cada página); texto fuente e identidad C210 sin cambios.
- **675/711 páginas con vectores idénticos**. Las otras **36** solo añaden **42 bandas físicas C2c** delimitadas exactamente en y=76..89 (coordenadas PDF top-origin); **cero trayectorias antiguas perdidas**, **cero vectores nuevos fuera de las cartelas C2**, **cero cambio de folios**.
- El auditor físico preexistente ampliado `v11-p3-c2-manuscript-audit.json` obtuvo **42/42 rótulos y cartelas**, P3 **96 páginas** y C2 **209 páginas con 230/230 cuadrículas de Notas** completas. Auditor de perímetro 76 PDF/711p **0 errores**; topología modular 0 errores.
- Se rasterizó **Mara A p4** del PDF nuevo. `CONTINÚA: RECURSOS Y MUNICIONES` aparece dentro de la nueva cartela azul recortada en la columna derecha sin tapar texto. La inspección es sobre el artefacto del SHA exacto, no sobre el golden sintético.
- [Acta JSON consolidada](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C212_C2_CLIPPED_BANDS_REAL_APP_PDFS.json), incluyendo los IDs de artefactos y hashes del lote, resultado y límites.

**C212 = PASS LOCAL físico acotado**, no G4. G0 histórico PARCIAL, G1 semántico PASS anterior, G2/G3 parciales, **G4 sigue BLOCKED: 105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 sin comenzar, owner/Android instalado SIN QA, sin aprobación técnica/visual ni merge. **C211 continúa bloqueado 3/3 normal, RCR-1 estructural 0/2; no reabrir sin PDF fallido exacto.**

**Siguiente bloque técnico seguro:** otro incumplimiento real con trazabilidad fuente→PDF/golden (preferir composición sustantiva fuera de C211) y avance modular de G4 a partir de artefactos exactos, sin rehacer cabeceras C2 y sin anticipar QA propietario.
