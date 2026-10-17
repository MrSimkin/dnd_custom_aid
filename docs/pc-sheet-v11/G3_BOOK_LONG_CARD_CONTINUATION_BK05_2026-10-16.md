# V11 G3 — BK05: continuación identificada en fichas largas de Libro

**Fecha:** 2026-10-16, Chile. **Plan original:** G0→G6 del 2026-10-09, misma PR #173, más obligación de validar 100 % de las reglas negociadas antes del QA propietario. No se reabre diseño V11 ni se cambia el golden. PR #173 sigue OPEN/DRAFT/UNMERGED; `main` y cuatro exportadores anteriores intactos.

## Defecto funcional verificado

D-0076 §3.48 y BK05 exigen que una ficha que supera una columna pueda continuar en columnas/páginas siguientes, **con identificación del mismo conjuro y sin perder texto**. El compositor de texto compartido `PcSheetV11TextFlow` ya generaba fragmentos exactos (`continuedFromPrevious`, `continuesOnNext`, offsets UTF-16), pero los PDFBox nativos dibujaban la continuación sin su identificación. Esto podía dejar párrafos anónimos en nuevas columnas, incluso con un índice correcto.

## Corrección mínima

Código PDFBox Android/Desktop: [`315b8f8144d34c711d5370f6940c10055f608544`](https://github.com/MrSimkin/dnd_custom_aid/commit/315b8f8144d34c711d5370f6940c10055f608544), corregido con [`c268cefdd0ec89c6953bce8b98f9d84aa7e6391a`](https://github.com/MrSimkin/dnd_custom_aid/commit/c268cefdd0ec89c6953bce8b98f9d84aa7e6391a). Al dibujar únicamente `Libro de conjuros` se examinan los fragmentos verdaderos del flujo medido. Cuando `continuedFromPrevious`, la siguiente columna está vacía en su primera fila y se dibuja una señal **`CONTINUACIÓN: ` + nombre real** a 7pt Fira, x+2, y707, **en la franja físicamente libre y comprobada entre y702 y la regla de cabecera y717**. Longitud comprobada con el mismo `PDFont.getStringWidth`; para nombres que no caben en ese rótulo secundario se emplea `CONTINUACIÓN (VER ÍNDICE)`, sin fabricar una escuela, un conjuro ni recortar el nombre original en su ficha/índice. No se añade un `FlowItem`, no se cambia el texto fuente, ninguna celda, el paginador, el índice físico, la cabecera principal ni el papel. La ruta Android y Desktop posee guard de paridad `scripts/test_v11_spellbook_continuation_label_contract.py`.

El test Kotlin `overlongRealSpellCardContinuesLosslesslyAfterPhysicalIndexPage` confirma texto extremo fuente de Ilyra (240 repeticiones + centinelas de inicio y fin), múltiples continuaciones identificadas e integridad de la última NOTAS. Un ajuste exclusivo al test [`05744b69c8eb114adfe7e88df5294a1d92886e2f`](https://github.com/MrSimkin/dnd_custom_aid/commit/05744b69c8eb114adfe7e88df5294a1d92886e2f) guardó **ambos PDF reales A/B** para inspección. Hubo una primera revisión con fallo de sintaxis de expectativas en el guard Python (`fragment` vs `it` en lambda), reparado con [`29c973e6c8091ff0cf2b72a56d5f122a8a3e1ede`](https://github.com/MrSimkin/dnd_custom_aid/commit/29c973e6c8091ff0cf2b72a56d5f122a8a3e1ede), sin cambiar el renderizador, y CI posteriores SUCCESS.

## CI y revisión física real

**[Scaffold #38992203285](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38992203285) SUCCESS**, jobs Kotlin (incluido Android debug), backend, hosted DB; **[QA traceability #38992203349](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38992203349) SUCCESS**. Corresponden al SHA exacto `05744b69c8eb114adfe7e88df5294a1d92886e2f`.

**[Artefacto de App nativa #12085324827](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38992203285/artifacts/12085324827): 14 PDF Carta reales**, ZIP SHA256 `239210a76ba9962d7a92411ca024efc69df1cbbe8b67cddf7094cf5e26dfa991`, CRC PASS, **no ReportLab**.

Inspección independiente Python PyMuPDF del ZIP:
- Los **12 PDFs** existentes en el artefacto previo #12085189061 son **idénticos** página por página en palabras, cajas/posiciones y vectores, sin nuevas anotaciones innecesarias. Esto confirma ausencia de regresión en perfiles normales, rutas P1–P4/C2, Ilyra/Mara y fichas no desbordadas.
- PDF extremo `V11-INTERNAL-PROOF-BK05-long-card-A.pdf`: **14 páginas** (Libro p10–13), **5** señales exactas `CONTINUACIÓN: Fire Bolt` en columnas siguientes: p10 columna derecha; p11 ambas; p12 ambas; posiciones superiores observadas x314/44, y78.5–86.9 en coordenadas top-origin (equivale a y705–713 desde abajo), dentro de la franja libre real. Contenido comienzo `COMIENZO-EXTENSO` y final `FIN-EXTENSO-VERIFICABLE` exactamente una vez. Índice 25/25 hacia páginas físicas correctas; última NOTAS permanece última. SHA PDF `39e9772a9003ad2a0affda5323ee97ced52e686aafe5ba84cbe2bb180209cabe`.
- PDF extremo `...-B.pdf`: **14 páginas**, Libro p10–13, idéntico patrón de cinco señales, extremos de descripción, 25/25 índice y última NOTAS. SHA PDF `96097d12098be9e8b9f65a1167bbc07959f406ef5ed96705212b523841f85c99`.
- Inspección de raster recortado confirma la etiqueta azul a 7pt sobre el primer párrafo de continuación, sin superposición de contenido ni cabecera.

## Reglas y continuidad del plan

La matriz continúa con **105 obligaciones**. `BK05.stage_checks.G3=PASS` limitado con test, commit y artefacto reales; `BK05.stage_checks.G2=NOT_VERIFIED` y `BK05.status=NOT_VERIFIED` global. `G4=BLOCKED`, `active_candidate=null`, la reconciliación íntegra D-0076 §§3.1–3.48, ocho golden, cuatro familias legacy y paridad Android instalado siguen pendientes; NO autorización para G5/owner QA, APK ni fusión. BK03 escuela sin campo real sigue abierta; BK06 conserva su PASS local.

**Siguiente tarea:** continuar con defectos del contrato demostrables y pruebas por regla, priorizando G2/BK04/BK05 (corte de fichas y mover completas cuando caben), sin reescribir o retroceder en módulos ya verificados. Documentar toda evolución en la única PR sin cambios en main.
