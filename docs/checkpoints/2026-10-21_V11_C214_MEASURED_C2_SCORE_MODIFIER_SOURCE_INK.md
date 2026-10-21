# C214 — rectángulo/óvalo de puntaje y modificador físicamente ligados al origen C2

**Fecha:** 2026-10-21. **Rama:** `docs/pc-sheet-v11-approved-implementation-handoff` / PR #173 DRAFT SIN MERGE. HEAD inicial `14c3939fcec31d45acc5bbd6f272c544ad1c3911`; main remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Último CI `#39664938107` Scaffold y `#39664938091` QA ambos SUCCESS. Contrato aprobado, 105 reglas y anteriores actas **intactos**.

## Desviación verdadera y alcance limitado

El PDF real `V11-INTERNAL-PROOF-Mara-A.pdf`, p4, contiene `Cordura 13 / mod 1 / salvación 0` en una sola línea, con puntaje y modificador sin contorno; `Éter 18 / mod 4 / salvación 8` también. La maqueta V11 aprobada y `drawP1Attributes()` ya demuestran la gramática de puntaje rectangular, modificador ovalado y casilla física SALVACIÓN. Pero el espacio de renglón C2 de **12,42 pt** no admite, sin reproyectar el compositor, los marcos grandes P1. No superponerlos de forma especulativa ni insertar puntajes duplicados.

## Producto C214

En los dos escritores PDF **Android+Desktop**, para cada registro REAL `attribute:<índice>` ya paginado:

- reconstruir únicamente para **validación** la identidad exacta `nombre score / mod modifier / salvación real`, con sus campos del snapshot; exigir igualdad del texto fuente existente, sin fallback ni datos inventados;
- usando el mismo `body.width.measurePt`, el offset fuente del fragmento y la línea física medida, trazar un **pequeño rectángulo alrededor de los glifos reales del puntaje** y un **óvalo Bézier alrededor del modificador real**; sin reimprimirlos, moverlos, truncarlos ni alterar el paginador;
- crear una casilla de salvación vacía **solo donde el rótulo y valor reales caben completos en una línea con margen**; nunca inferir competencia o selección;
- en filas con token parcialmente envuelto o con delimitadores de control, omitir solo el contorno imposible, **no** el dato ni su impresión;
- trazos limitados a la celda del registro y su separación interpalabra, detrás del texto, no a grupos de C211. Las cabeceras y bandas C213 permanecen tal cual.

`scripts/test_v11_c2_source_navigation_contract.py` exige mismo uso de fragmentos, métrica, forma y pertenencia al origen en ambas plataformas.

**Límite de contrato importante:** esto constituye una **representación geométrica real pero compacta** en las posiciones impresas, NO la tarjeta V11 grande del golden ni la plena equivalencia visual C204. Para esa equivalencia se requerirá un diseño de **dos niveles de layout medido** y reconciliación G4. No declarar PASS global ni aprobación estética.

**Pendiente para dar PASS LOCAL de este cambio:** CI exacto, 76 PDF A/B de App Desktop, 711 páginas comparadas PyMuPDF (texto, fuentes, palabras y coordenadas intactas), conteo de rectángulos/óvalos/casillas solo en atributos reales sin colisión ni overflow, inspección raster A/B. En fallo, bloquear y reportar sin promoción.

**Compuertas conservadas:** G0 PARCIAL histórico; G1 PASS semántico previo; G2/G3 PARCIALES; **G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null**; G5/G6 PENDIENTES. C211 BLOCKED (3/3 intentos normales agotados, 0/2 RCR-1 estructurales), C212/C213 PASS LOCAL previos intactos. Android instalado, propietarios y PR/merge sin aprobación. Siguiente: evaluar reforma medida de tarjeta C2 para equivalencia completa solo desde un PDF fallido exacto y comparar módulos para G4 sin repetir C211.

## Prueba del producto exacto (C214): **PASS LOCAL DE GEOMETRÍA COMPACTA; NO C204/G4**

**SHA de código exacto:** [`b35cb0edc8`](https://github.com/MrSimkin/dnd_custom_aid/commit/b35cb0edc898ae01d5002462ade748a9e7cffb8f). **GitHub Actions completos:** [Scaffold #39669461876](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39669461876) **SUCCESS** (Kotlin, Android, fuente real Desktop PDF, auditorías existentes, backend, DB), [QA traceability #39669462040](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39669462040) **SUCCESS**. El código no altera flujos, datos, texto ni otras familias.

**Artefactos originales:** antes [76 PDF App C213, run #39663757661, artefacto #12376971384](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39663757661/artifacts/12376971384) SHA-256 ZIP `391494dc23323c55706a9f0012bbfeeb62ea89a43596e63e5741cfa1b586e4c8`; después [76 PDF reales C214, run #39669461876, artefacto #12379791454](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39669461876/artifacts/12379791454) SHA-256 ZIP `e8b41860f969fa28a24ba25f4cdbf9e4a6f87c28ccd4e9c91c7626b96d6d7321`.

Comparador físico **independiente PyMuPDF**, sobre todos los PDF reales A/B, confrontando por nombre y página los vectores PDF con firmas geométricas (tipo, puntos Bézier, color, grosor, etc.) y el texto con posición de cada palabra:

- **76/76 PDF, 711/711 páginas**, mismo número de folios, mismas palabras/valores/posiciones.
- **683 páginas de vectores idénticos**; solo **28 páginas C2** reciben **597 rutas físicas** nuevas: **199 rectángulos** alrededor de puntajes verdaderos, **199 óvalos Bézier** alrededor del modificador verdadero y **199 casillas de salvación vacías** junto al valor real; **ningún vector anterior eliminado**.
- **0 vectores fuera del alcance**, **0 números no contenidos dentro de sus figuras**, **0 casillas superpuestas a glifos**, **0 cambios de página ni texto**, **0 errores** de comparación.
- Inspección visual raster del **Mara A PDF físico p4** a tamaño aumentado: `Cordura 13 / mod 1 / salvación 0`, `Éter 18 / mod 4 / salvación 8` y `Renombre 11 / mod 0 / salvación sin valor` conservan texto íntegro y cada valor numérico está dentro de su contorno físico real.

[Acta JSON de este candidato](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C214_REAL_APP_ABILITY_INK_GEOMETRY.json) enlaza fuentes, SHA exacto y métricas reproducibles.

**No promover C204 ni G4:** forma compacta no reproduce la tarjeta separada grande de puntaje/modificador del golden; requiere nueva reserva de celdas/medición para equivalencia. No revisión de ocho golden completa, no QA Android instalado ni aprobación técnica/visual del propietario. G0 PARCIAL, G1 PASS semántico previo, G2/G3 PARCIALES, **G4 BLOCKED 105 NOT_VERIFIED / active_candidate null**, G5/G6 pendientes. C211 BLOCKED 3/3 normales, RCR-1 0/2 estructurales. C212/C213 PASS locales sin retrabajar. PR #173 DRAFT NO MERGE.

**Próximo bloque coherente:** diseñar reserva medida en C2 de dos niveles (cabecera/nombre + celdas independientes puntaje/modificador/salvación) que acepte nombres largos y overflow, y solo cambiarlo tras prueba con PDFs genuinos y sin pérdida; posteriormente reconciliar G4 por módulo con golden/D0076 sin falsas aprobaciones.
