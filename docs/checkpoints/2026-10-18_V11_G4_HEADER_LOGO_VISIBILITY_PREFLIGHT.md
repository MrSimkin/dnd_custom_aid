# V11 G2/G3 → G4 — preflight independiente de visibilidad del logotipo en cabeceras

**2026-10-18 · PR #173 DRAFT, SIN MERGE.** Autoridad de rama: `docs/pc-sheet-v11-approved-implementation-handoff`. Partida: `53dc38eb3ef8240bad86fe14d0293e5f85a92865` (Scaffold #39168510609 y QA #39168510698 SUCCESS para el marco doble; checkpoint anterior conservado).

## Trabajo ejecutado en este bloque

- Se incorporó `scripts/audit_v11_header_logo_visibility.py` y su prueba roja/verde sintética. El inspector lee el ZIP inmutable de ocho PDF golden (59 folios), calcula la cantidad de tinta oscura dentro del área del logotipo sin incluir los marcos y contrasta el mínimo de visibilidad con **todas las páginas de PDF efectivamente producidas por Desktop**. Detecta logo invisible por recorte erróneo, ausencia de imagen o área en blanco, sin basarse en que exista un recurso embebido.
- Se incorpora la prueba al Scaffold de la rama y el informe `v11-header-logo-visibility.json` a su artefacto `v11-independent-physical-envelope-audit`, con hashes del ZIP golden y de cada PDF del candidato, 8 golden/59 páginas esperadas, nombres de candidatos, cantidad de páginas y ejemplos de fallos.
- **Al publicar el código, CI exacto PENDIENTE**. No reclamar visibilidad PASS ni medidas golden antes de comprobar el resultado de GitHub Actions. El control es deliberadamente acotado: no prueba semejanza píxel a píxel, identidad exacta de logo, nombre/encabezado, fuentes, geometría de otros módulos, ni equivalencia Android instalado. Los nombres de cabecera extraídos son solo diagnóstico hasta cotejar la fuente real.

## Qué se conserva y qué falta

**G0 QA histórica PARCIAL; G1 semántico PASS; G2/G3 funcionales parciales; G4 BLOCKED.** La matriz de 105 reglas permanece **NOT_VERIFIED**, `active_candidate=null`, las 32 observaciones M50800 y V11-QA-001/002 siguen abiertas donde corresponda, RCR-1 no utilizado (0/2). Sin nueva versión de APK owner, sin QA real del dispositivo, sin G5/G6, sin fusionar PR ni actualizar main.

**Siguiente paso obligatorio:** leer el resultado CI exacto de esta auditoría. Si falla por la imagen, examinar PDF real y corregir solo causa medida dentro del contrato V11; si pasa, registrar **PASS LOCAL de presencia visible**, nunca PASS global. Después seguir cotejo exhaustivo de la cabecera/logo y las reglas G4 con ocho golden, sin rediseñar.

## Resultado exacto del control (2026-10-18) — PASS LOCAL

- **Implementación auditada:** commit [`0af6b2c78b7c1fc66b1681f99c54cc21001aaaba`](https://github.com/MrSimkin/dnd_custom_aid/commit/0af6b2c78b7c1fc66b1681f99c54cc21001aaaba); [Scaffold #39169494332](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39169494332) **SUCCESS**, incluidos Kotlin/Android DEBUG/Desktop, backend y hosted-db.
- **Prueba roja/verde sintética:** `V11 logo visibility independent synthetic red/green: PASS`.
- **Salida real:** `V11_LOGO_VISIBLE=510/510; PDFs=54; golden=59 pages`. Los 54 archivos PDF provienen de la aplicación Desktop en ese commit; [artefacto #12152216888](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39169494332/artifacts/12152216888).
- **Informe independiente:** [artefacto #12151992599](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39169494332/artifacts/12151992599) contiene `v11-header-logo-visibility.json` junto con auditores previos. Doble marco `510/510` y auditor de perímetro `0` errores en el mismo run.
- **Interpretación estricta:** solo se verificó visibilidad física del logotipo dentro de su área; **no** igualdad de píxeles/composición exacta de logo, nombre, cabecera entera o tipografía frente a golden. No se auditó la instalación Android ni las cuatro familias legacy en el dispositivo.

**Gate:** PASS LOCAL de presencia visual, G4 global sigue **BLOCKED** con 105 reglas `NOT_VERIFIED` y `active_candidate=null`; G5/G6 pendientes. QA owner no realizada, M50800 histórico y V11-QA-001/002 sin cierre, RCR-1=0/2, sin merge. **Siguiente trabajo:** inspección física independiente de cabecera/logo completa contra ocho golden y reconciliación por grupos de reglas del contrato, sin certificar paridad que este control no mide.
