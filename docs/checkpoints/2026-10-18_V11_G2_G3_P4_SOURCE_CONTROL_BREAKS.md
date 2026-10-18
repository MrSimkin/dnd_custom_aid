# V11 G2/G3 — Fuentes reales de conjuros con CR/LF/TAB en P4

**Fecha:** 2026-10-18; PR #173 DRAFT, SIN MERGE. **Base exacta:** `80c96a96b73032bd6f2e98d0ace94ee8ec41a819`. Solo V11; Android+Desktop equivalentes. No se modifica UI, modelos persistidos, golden, las cuatro familias legacy ni las aprobaciones pendientes.

## Incumplimiento funcional identificado a partir de código/App real

- P4 muestra origen y marca de preparación **por fuente/UUID**. Cuando la fuente persistida contiene CR, LF o TAB reales, la ruta de rótulo corto a la derecha intentaba medir el texto completo con la fuente PDFBox de una sola línea antes de elegir la composición adecuada. PDFBox no acepta U+000A/U+000D como glifos; puede fallar la exportación aun siendo válidos el personaje y la asociación.
- Se identificó el punto exacto en ambos exportadores V11: `p4RightSources`, que decidía por *ancho* sin verificar antes la *altura física* de la fuente. La ruta compartida de texto medido ya tolera saltos y tabulaciones y conserva offsets/cadenas/IDs; es el destino compatible, no una nueva propuesta estética.
- **Cambio productivo**: excluir del renglón fijo derecho cualquier fuente que contenga CR/LF/TAB; conservar la fuente íntegra en el flujo medido P4 de 3 columnas (junto con el conjuro y su preparación auténtica). No eliminar caracteres de los datos, no truncar, no deducir estados, no convertir a C2 innecesariamente.
- **Prueba nueva:** fixture Ilyra real con fuente adicional UUID estable, asociación de un único conjuro, nombre `QAP4SOURCEFIRST\r\nQAP4SOURCESECOND\tQAP4SOURCETHIRD`, prepared=true. Exporta A y B por el **servicio real Desktop**, preserva dos PDFs; exige cada fragmento una sola vez dentro de P4, conjuro/UUID/preparación conservados y página final NOTAS intacta. Test existente protege origen corto a la derecha y fuentes homónimas.

## Gate y demostración pendiente

Al registrar este commit: **prueba y CI EXACTOS PENDIENTES**. No atribuir PASS hasta inspeccionar logs, PDFs genuinos y paridad/ausencia de regresión. Este bloque atiende un error técnicamente reproducible distinto de la QA histórica Mara (sin cerrar M50800 ni V11-QA-001/002). G0 QA histórica parcial; G1 PASS semántico, G2/G3 parciales; G4 BLOCKED con 105 reglas `NOT_VERIFIED`, `active_candidate=null`, G5/G6 pendientes; RCR-1 0/2, sin APK owner ni merge.

**Siguiente paso:** verificar Scaffold exacto y QA ledger; si falla por causa medida, corregir SOLO el mismo camino; exigir PDFs P4 A/B y no regresiones de las pruebas existentes. Después continuar trabajo funcional en los módulos pendientes con matriz y golden; nunca marcar PASS global por esta prueba local.
