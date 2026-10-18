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

## Resultado exacto — PASS LOCAL de la ruta P4 con fuentes CR/LF/TAB

**Commit bajo prueba:** [`8b57baec0b24406c3e335b343a4990379d9f6196`](https://github.com/MrSimkin/dnd_custom_aid/commit/8b57baec0b24406c3e335b343a4990379d9f6196). [Scaffold #39171821142](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39171821142) **SUCCESS** (Kotlin shared/Desktop, Android DEBUG y Desktop app; backend y hosted-db); [QA ledger #39171821185](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39171821185) **SUCCESS documental**.

**PDF de aplicación real:** [artefacto #12152454938](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39171821142/artifacts/12152454938), 56 PDF, 528 páginas. Se descargó el ZIP y se comprobó por PyMuPDF:

| Prueba | Páginas | SHA-256 PDF | Verificación |
|---|---:|---|---|
| `V11-INTERNAL-PROOF-P4-source-physical-break-A.pdf` | 9 | `35c778a0bb0eb5c0c9a9afba536f45b366c8d72364dd3866696845a32a95c94c` | `QAP4SOURCEFIRST/SECOND/THIRD` exactamente una vez cada uno, P4 en 1 página, último folio NOTAS |
| `V11-INTERNAL-PROOF-P4-source-physical-break-B.pdf` | 9 | `ff1a6985725f425011dbd1aacb94da2823de21ac300e0a415c8a96039ba23079` | Mismas comprobaciones en variante B |

**No regresión acotada del conjunto previo:** se compararon los 54 PDF anteriores (del [artefacto #12152216888](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39169494332/artifacts/12152216888)) con los mismos 54 archivos del nuevo candidato. **52/54** conservan número de páginas, lista de palabras con coordenadas, recuento de trazos e imágenes. **2/54** (`source-tabs-mechanical-A/B`) cambiaron exclusivamente en la presentación de la P4 que ahora despliega los orígenes con tabulaciones en el flujo medido, esperado por esta corrección; mismo total de **14 páginas** y mismo número de apariciones de `SRC7` (24), `ONE` (27), `QA7` (6), `Fórmula umbral` (69), niveles y NOTAS en ambas versiones. No se afirma igualdad byte a byte ni identidad geométrica de esas dos P4 modificadas: siguen pendientes de G4.

**Auditores independientes del mismo commit:** [#12152479992](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39171821142/artifacts/12152479992); en log `V11_PHYSICAL_AUDIT_ERRORS=0`, `V11_GOLDEN_DOUBLE_FRAME=528/528; PDFs=56`, `V11_LOGO_VISIBLE=528/528; PDFs=56; golden=59 pages`. Se reutilizaron los controles existentes, sin inventar un quinto auditor de datos.

**Interpretación:** La prueba del código productivo sí demuestra exportación Desktop real para esta entrada anteriormente no protegida y compilación del código Android equivalente, **pero no contiene ejecución independiente del código anterior con la misma entrada** ni verificación visual de Android instalado. Por eso la causa previa se describe como riesgo de fallo derivado del código, no como incidente runtime previamente observado por el propietario. Es **PASS LOCAL** de preservación de estas fuentes en P4 A/B, no G4, G5, visual-owner ni promoción.

**Estado global sin cambios:** G0 QA Mara parcial, G1 semántico PASS, G2/G3 parcialmente demostrados, G4 **BLOCKED** (105 reglas `NOT_VERIFIED`, `active_candidate=null`), G5/G6 pendientes; original M50800 y V11-QA-001/002 sin cierre; RCR-1 0/2; PR #173 DRAFT sin merge. **Siguiente bloque:** atender diferencias funcionales demostrables en composición C2 y página de conjuros restantes, con una sola prueba fuente→PDF A/B por familia afectada, conservando matriz y golden.
