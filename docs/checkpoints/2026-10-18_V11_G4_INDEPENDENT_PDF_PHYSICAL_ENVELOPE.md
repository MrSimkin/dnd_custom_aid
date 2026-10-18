# V11 — G4: barrido físico independiente de PDFs reales (sin promoción global)

**Fecha:** 2026-10-18. **Autoridad:** PR #173, única rama `docs/pc-sheet-v11-approved-implementation-handoff`, DRAFT/sin fusionar. **Estado previo:** G1 PASS semántico; avances locales G2/G3; G4 BLOCKED, G5/G6 PENDIENTES.

## Subpaso ejecutado

Se descargó el artefacto genuino **#12123599698**, asociado al Scaffold **#39094514187** (implementación `62ee971ff0ace1fedd19bb4969aaf7eaee76afd4`), y se comprobó SHA-256 ZIP **`3c37e5ee1273ffa5f67f28df71032e2478d9ca82d557026c219f1e0e1c97a713`**. Contiene **48 PDF de salida del servicio real Desktop V11**, que suman **466 páginas**. NO son PDFs extraídos de un teléfono Android instalado.

Nuevo control ejecutable `scripts/audit_v11_pdf_physical_envelope.py`, **oráculo externo PyMuPDF** distinto del renderer PDFBox, y prueba negativa `scripts/test_audit_v11_pdf_physical_envelope.py`. Verifica en cada PDF: Carta 612×792, texto dentro de caja imprimible, intersecciones sustantivas entre palabras, fuentes incrustadas Barlow/Fira según el folio, páginas iniciales P1/P2/P3, numeración física de folios intermedios y **última NOTAS manuscrita sin texto digital**. El informe JSON guarda hashes de cada PDF y roles físicos.

**Ejecución local sobre el ZIP real recuperado:** 48 PDF, 466 páginas, **0 alertas**; el test negativo rechaza explícitamente un PDF de tamaño erróneo y sin P1. Estas afirmaciones NO demuestran alineación perfecta con golden, invisibilidad de glifos, integridad de todos los datos/IDs, reglas D-0076, paridad de binario Android ni ausencia de solape texto↔trazos.

## Integración y continuación

El workflow `.github/workflows/scaffold-check.yml` ejecuta la auditoría tras generar los PDF auténticos en CI, solo en esta rama, con mínimo 48 archivos, y conserva `v11-independent-physical-envelope-audit` como artefacto. **CI del nuevo commit y SHA de reporte: por verificar**; no escribir SUCCESS antes de comprobarlo.

**G4 sigue BLOCKED**: 105 reglas globales `NOT_VERIFIED`, reconciliación completa de las 48 secciones D-0076 y ocho PDF golden pendiente, QA del APK exacto/legado todavía pendiente. No se han modificado la matriz de reglas, los 32 M50800 ni los dos V11-QA; `active_candidate=null`, sin cambio de estado de aceptación. Golden original, cuatro exportadores legacy y `main` intactos. RCR-1 sin activar. PR #173 no fusionada.

**Siguiente:** con el manifiesto independiente de cada PDF disponible en Actions, efectuar auditoría de contenido y geometría **contra los ocho golden y el contrato completo**; registrar por ID verdaderos FAIL/PASS con candidato y prueba, reparar exclusivamente defectos confirmados en ambos renderers V11. No abrir G5 hasta G4 formal PASS.
