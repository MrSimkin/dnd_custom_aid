# V11 — C-N3: identidad de fuente obligatoria en títulos continuados

**Fecha:** 2026-10-19. **Autoridad:** `main/AGENTS.md → RESUME.md → LATEST.md → PR #173`. Rama exclusiva `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 **DRAFT / SIN MERGE**. Base del bloque: `dda359972422757414c07f793f8c17275168cae3`.

## Problema real identificado en código vigente

Los dos renderizadores V11 compartían este fallback para una C-N3 que continuaba una nota digital fuente:

- título de la nota completo cuando cabía;
- de lo contrario, `C-N3 - Notas digitales / página / CONTINÚA ID: <UUID>` si cabía;
- **en caso contrario, solo `C-N3 - Notas digitales / página / CONTINÚA`**, perdiendo la identidad original. El problema es verificable directamente en ambos archivos Kotlin previos; **no se afirma** que un PDF de usuario haya alcanzado este último caso.

El contrato V11 exige notas completas, continuaciones identificables y ninguna identidad inventada. La reparación se acota a ese último caso, sin intervenir el cuerpo, los offsets, la paginación o los marcos.

## Implementación funcional exacta

- Se añadió **un selector tipado y compartido** `shared/src/commonMain/.../PcSheetV11NoteContinuationHeader.kt`, que conserva la cabecera anterior cuando cabe, pasa a la referencia por UUID completo cuando el título es demasiado extenso y, si la cabecera con página ya no cabe, prueba `C-N3 / CONTINÚA ID: <UUID>` y `CONTINÚA ID: <UUID>`. Si ningún formato puede mostrar la identidad fuente, **falla de forma explícita**, nunca imprime una continuación anónima.
- Ambos generadores `DesktopPcSheetV11PdfProofRenderer.kt` y `AndroidPcSheetV11PdfProofRenderer.kt` llaman exactamente la **misma** función y usan el ancho real de Barlow Condensed incrustada al tamaño 12 pt y 524 pt de banda disponible. La ruta Android/Desktop fue cotejada y resultó textualmente idéntica.
- Mantiene `CONTINÚA: NOTAS GENERALES` para la nota general y la presentación normal previa para títulos que quepan, incluida `Nota 1 — Hipótesis`. Ningún cambio de datos o diseño global.
- Pruebas agregadas: `PcSheetV11NoteContinuationHeaderTest.kt` (cinco condiciones semánticas); `g3VeryLongSourceNoteTitleKeepsStableContinuationIdInRealPdfAB` en `DesktopPcSheetV11RuntimePdfProofTest.kt` solicita PDF genuinos Desktop variantes A/B, nota Mara de 440 términos y título fuente excepcionalmente largo, UUID en continuación, inicio/fin únicos, última NOTAS intacta. Este **test integrado está escrito pero no se ha ejecutado todavía**.

## Evidencias y límites de esta sesión

1. Se abrió el lote auténtico anterior `v11-internal-real-app-pdf-proofs` del Scaffold [#39193379904](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193379904): 64 PDF/589 páginas, incluidas P3/C2/C-N3 de Mara e Ilyra. Se inspeccionaron físicamente sus páginas relevantes. Este lote **precede** a la nueva corrección y es referencia, no prueba del nuevo candidato.
2. Para el nuevo helper compartido, **Git blob SHA-1 `d83fe6863a83999c0898b4e8ed0721fc5cc2170d`** cotejado contra `git hash-object` local del mismo contenido. Se compiló con `kotlinc`/JDK 21 y se ejecutaron cinco condiciones de borde equivalentes: **5/5 PASS local aislado**, incluida fallback corto con UUID, conservar normal, general, salto y fallo cerrado.
3. **NO** se ha demostrado compilación total de Gradle, ni CI exacta, ni PDF nuevo de la App, ni ejecución Android, ni revisión visual independentemente del candidato. Por ello este cambio se declara **implementado, pendiente de verificación integrada**; no se promueve ningún estado de matriz a PASS.
4. La corrección anterior de C-N3 con renglones manuscritos continúa **pendiente de ejecución del test PDF A/B y revisión del artefacto nuevo**, tal como el checkpoint `2026-10-19_V11_G2_G3_CN3_MANUSCRIPT_RESERVE.md` estableció. No se la reinicia ni se la atribuye aprobado.

## Gates y continuación

- **G0** histórico QA Mara **PARCIAL**; **G1** semántico **PASS previo**, sin reabrir; **G2/G3** parciales, corrección C-N3 pendiente de PDF nuevo; **G4 BLOCKED**, 105/105 `NOT_VERIFIED`, `active_candidate=null`; **G5/G6** pendientes.
- Registro original M50800 y V11-QA intactos; matrices, golden y criterios sin alteraciones; RCR-1 **0/2**; owner NO ha aprobado el APK ni esta modificación; PR #173 no se fusiona.

**Próxima intervención funcional obligatoria:** ejecutar suites Gradle y workflows de último HEAD para los **dos** cambios C-N3 aún sin cierre (renglones manuscritos + identidad de continuación), recuperar los PDF A/B auténticos del mismo build, revisar vectors/texto/paginación vs el lote anterior y registrar fallo preciso/reparación solo si se observa. Continuar entonces la conciliación de G4 P3/C2 sin nuevas rondas cosméticas ni certificaciones ficticias.