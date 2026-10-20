# V11 — P2 cuatro módulos: bandas alternas azul-niebla reales

**20-10-2026 | autoridad GitHub**: rama `docs/pc-sheet-v11-approved-implementation-handoff`, PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT NO MERGE; base remota `7762d8c2f0b5c07eb22647db6e0b0683978c8da6`, `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Último HEAD documental [Scaffold #39494971333](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39494971333) y [QA #39494971323](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39494971323) **SUCCESS**.

## Brecha concreta documentada

Contraste visual de P2 real de `V11-INTERNAL-PROOF-Mara-B.pdf`, artefacto [#12296978059](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39493923916/artifacts/12296978059), con el **golden aprobado** `v11_extremo_variante_B.pdf` p2 del ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`. Golden tiene filas de escritura azul-niebla alternas en **EQUIPO, TESORO, RASGOS Y ATRIBUTOS y EQUIPO ESPECIAL**; PDF de App presentaba grandes bloques de relleno uniforme (RGB 238/247/251), con líneas horizontales y sin tintado alterno en cuatro tablas. Ningún dato real requiere moverse para lograr el papel aprobado.

## Modificación funcional

En **los dos renderers reales Kotlin V11 (Android y Desktop)**, agregar `alternateP2Rows: Boolean = false` al `grid()` interno y pasar `true` exclusivamente desde P2 Equipo/Tesoro/Rasgos. Pintar `RGB(231,242,247)` cada segunda fila calculada con la altura física **ya medida** de la tabla (si vacío, Equipo 16,5 pt; Tesoro 19,5 pt; con fuente, `body.metrics.lineHeightPt`), antes de trazar las reglas y valores originales. **C2 opt-out** preserva intactas sus cuadrículas y auditorías anteriores. Equipo Especial usa su propia fuente real `specialRowHeight=25pt`, tintando filas pares dentro de la reserva P2 original y antes de cuatro columnas, marcas ACTIVO y texto fuente, sin añadir filas/datos.

Se **amplía el auditor existente** `scripts/audit_v11_pdf_physical_envelope.py`, ya ejecutado por Scaffold, para exigir al menos un primer rectángulo real tintado (posición y color) de cada módulo del segundo folio en todos los PDF genuinos. No crear auditoría alternativa. Scaffolds anteriores de 76 PDF, 711 páginas; el nuevo commit debe verificar que páginas, palabras y posiciones se conservan, y **todas las páginas no P2** siguen con vectores idénticos, en especial C2 con 230 notas físicas. La actualización no altera lógica de fuente, folios, tests previos, las 4 familias legacy ni C211.

**Estado al implementar:** código publicado, **CI/PDF del commit exacto PENDIENTES**. Registrar PASS LOCAL exclusivamente si auditor real, tests y cotejo diferencial pasan; no promocionar ninguna regla de la matriz G4.

**Gates:** G0 PARCIAL; G1 semántico PASS previo; G2/G3 PARCIALES; **G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null**; G5/G6 pendientes; Android instalado/QA owner sin aceptación; C211 RCR-1 3/3 normal agotado y 0/2 estructural. PR #173 DRAFT no fusionada.

**Próximo:** validar output exacto, persistir evidencia física, avanzar otra brecha independiente sin reabrir lo que ya pasó.
