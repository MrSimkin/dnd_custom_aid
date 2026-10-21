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
