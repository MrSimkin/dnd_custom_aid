# V11 — G4: barrido físico independiente de PDFs reales (sin promoción global)

**Fecha:** 2026-10-18. **Autoridad:** PR #173, única rama `docs/pc-sheet-v11-approved-implementation-handoff`, DRAFT/sin fusionar. **Estado previo:** G1 PASS semántico; avances locales G2/G3; G4 BLOCKED, G5/G6 PENDIENTES.

## Subpaso ejecutado

Se descargó el artefacto genuino **#12123599698**, asociado al Scaffold **#39094514187** (implementación `62ee971ff0ace1fedd19bb4969aaf7eaee76afd4`), y se comprobó SHA-256 ZIP **`3c37e5ee1273ffa5f67f28df71032e2478d9ca82d557026c219f1e0e1c97a713`**. Contiene **48 PDF de salida del servicio real Desktop V11**, que suman **466 páginas**. NO son PDFs extraídos de un teléfono Android instalado.

Nuevo control ejecutable `scripts/audit_v11_pdf_physical_envelope.py`, **oráculo externo PyMuPDF** distinto del renderer PDFBox, y prueba negativa `scripts/test_audit_v11_pdf_physical_envelope.py`. Verifica en cada PDF: Carta 612×792, texto dentro de caja imprimible, intersecciones sustantivas entre palabras, fuentes incrustadas Barlow/Fira según el folio, páginas iniciales P1/P2/P3, numeración física de folios intermedios y **última NOTAS manuscrita sin texto digital**. El informe JSON guarda hashes de cada PDF y roles físicos.

**Ejecución local sobre el ZIP real recuperado:** 48 PDF, 466 páginas, **0 alertas**; el test negativo rechaza explícitamente un PDF de tamaño erróneo y sin P1. Estas afirmaciones NO demuestran alineación perfecta con golden, invisibilidad de glifos, integridad de todos los datos/IDs, reglas D-0076, paridad de binario Android ni ausencia de solape texto↔trazos.

## Integración y continuación

El workflow `.github/workflows/scaffold-check.yml` ejecuta la auditoría tras generar los PDF auténticos en CI, solo en esta rama, con mínimo 48 archivos, y conserva `v11-independent-physical-envelope-audit` como artefacto. **CI Scaffold #39147958117 SUCCESS** en commit `a2b8568b9f8a71d8e758bf726a71d629e101e52b`; informe publicado como artefacto **#12144595619**, extraído y verificado: 48 PDF/466 páginas, 0 alertas.

**G4 sigue BLOCKED**: 105 reglas globales `NOT_VERIFIED`, reconciliación completa de las 48 secciones D-0076 y ocho PDF golden pendiente, QA del APK exacto/legado todavía pendiente. No se han modificado la matriz de reglas, los 32 M50800 ni los dos V11-QA; `active_candidate=null`, sin cambio de estado de aceptación. Golden original, cuatro exportadores legacy y `main` intactos. RCR-1 sin activar. PR #173 no fusionada.

**Siguiente:** con el manifiesto independiente de cada PDF disponible en Actions, efectuar auditoría de contenido y geometría **contra los ocho golden y el contrato completo**; registrar por ID verdaderos FAIL/PASS con candidato y prueba, reparar exclusivamente defectos confirmados en ambos renderers V11. No abrir G5 hasta G4 formal PASS.

## Evidencia post-CI y golden recuperado (2026-10-18)

**2026-10-18 — Verificación CI completa del auditor G4:** [Scaffold #39147958117](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39147958117) **SUCCESS**, commit `a2b8568b9f8a71d8e758bf726a71d629e101e52b`; jobs Kotlin (incl. Android DEBUG y Desktop PDF), backend y base de datos **SUCCESS**. [Informe independiente #12144595619](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39147958117/artifacts/12144595619) leído de ZIP verificable: **48 PDF, 466 páginas, 0 alertas, 48/48 PASS solo del perímetro físico**; 48 PDF App [#12143809732](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39147958117/artifacts/12143809732). **Golden renovado** por reejecución exitosa de [#38482518998](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38482518998), [8 PDF + 2 PNG #12143757898](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38482518998/artifacts/12143757898), ZIP nuevo SHA `51ab3c75890b9fa5973e05e339ad0efdabf271d561f82e02a55062b7c80c9512`, CRC PASS y todos los PDF Carta. Sigue **G4 BLOCKED**, 105 reglas globales NOT_VERIFIED; esta auditoría NO sustituye cotejo golden/D-0076, Android instalado ni QA propietario. PR #173 DRAFT/sin merge.

La descarga del ZIP de referencias de la reejecución mantiene disponibles las ocho hojas originales aprobadas para el **siguiente cotejo físico independiente**, que sigue pendiente. No atribuirle PASS a las 105 obligaciones contractuales ni tocar los registros QA de propietario.
