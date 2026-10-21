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
