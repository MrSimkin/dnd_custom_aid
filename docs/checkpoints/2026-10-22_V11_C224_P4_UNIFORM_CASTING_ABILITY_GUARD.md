# C224 — Aptitud mágica P4 por fuente, no universal falsa

Fecha 2026-10-22 (Chile). Rama `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT / **NO MERGE**. **CI, PDF físico y control independiente: PASS LOCAL C224, NO G4.**

## Causa y alcance

D-0076 §§3.24–3.26 y contrato visual V11 aprobado: la franja P4 debe reflejar los orígenes mágicos auténticos sin atribuir estadísticas de una fuente a todas las otras. C221 ya corrige perfiles heterogéneos; C223 protege CD y ataque cuando los perfiles son uniformes pero contradicen la ficha global. Brecha C224 independiente: fuentes uniformes configuradas en SAB frente a la aptitud global INT imprimían incorrectamente INT como si representara a esas fuentes. No reabrir C211 (3/3 normales agotados), no rediseñar tarjetas, no tocar familias legacy.

## Cambio funcional y regresiones

- Shared `PcSheetV11SpellSourceLabels.globalAbilityDisagreement`: compara la aptitud explícita de fuentes UUID con uno de seis atributos globales conocidos. Omite divergencias no demostrables ante fuente sin configurar o global `OTHER/NONE`; una fuente con atributo custom frente a global estándar contradice correctamente la atribución universal.
- Android y Desktop V11 usan el mismo método y el rótulo conservador `SEGÚN FUENTE` **solo** en tarjeta `APTITUD` si hay contradicción demostrada. Se mantienen geometría, CD, ataque, identidades UUID, preparación y resto de páginas.
- Shared test: uniformidad entre fuentes y contradicción global, coincidencia, estado no configurado, global OTHER/NONE, aptitud custom. Prueba Desktop desde backup auténtico Ilyra: cambia únicamente aptitud de perfiles persistidos a SAB para ambas variantes A/B, compensando +3 en ajustes de CD/ataque (INT 18→SAB 13) para mantener los valores numéricos originales, además de hoja global INT, conjuros, nombres/estados, y exige la tarjeta no engañosa en **PDF real del servicio de la App**; genera dos PDFs A/B para cotejo independiente.

## Procedencia y pendientes

HEAD previo rama: `d73f79d87141d086037b93c9c3d9457bb48e654f`; `main` previo: `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Última evidencia anterior C223: commit `440b74f`, Scaffold #39713904751 + QA #39713904856 SUCCESS, 84 PDF auténticos/777 páginas según acta C223. **Ese PASS LOCAL no se transfiere automáticamente a C224.**

Antes de declarar PASS LOCAL C224: verificar SHA exacto, Scaffold Kotlin Shared/Desktop/Android, QA y auditor PDF; recuperar lote C224 y comparar páginas, texto/posición, vectores e imágenes con 84 PDF C223, inspeccionar los dos A/B nuevos con hashes y rótulo correcto. Si no están los bytes, permanecer `EVIDENCE_PENDING` sin falso PASS; fallos verificados consumen presupuesto propio de C224 conforme RCR-1.

**Gates no promocionados:** G0 QA histórico PARCIAL; G1 semántico PASS anterior; G2/G3 PARCIALES; G4 BLOCKED **105/105 NOT_VERIFIED** y `active_candidate=null`; G5/G6 PENDIENTES. C211 BLOCKED 3/3 normales, 0/2 recuperación estructural. 32 observaciones M50800 y dos V11 QA siguen vigentes; original PDF runtime propietario Mara no recuperado, Android instalado no revisado, QA propietario no aprobado. PR #173 sigue DRAFT sin fusionar.

**Próximo paso:** CI exacto de C224 y auditoría física real antes de más cambios; luego conciliación modular G4 con golden y fuentes D-0076 sin reabrir C211 sin causa y alternativa nuevas.

**Ajuste preventivo de la prueba, mismo bloque C224:** el primer fixture editaba la aptitud sin compensar el modificador INT +4 frente a SAB +1, lo que creaba también discrepancias en CD/ataque y hacía imposible aislar el control de APTITUD. La versión corregida suma +3 a ambos ajustes de perfil persistido, conservando los dos valores numéricos originales. No se alteró código de producción; CI del candidato corregido sigue siendo condición de PASS.

## Cierre C224: PASS LOCAL FÍSICO; no G4, owner ni merge

Producto `07b529d07a4a54eb23a8883412a5f2e4868625d6`, fixture correctivo `4b0ace1c14faba0b6cace8def28b91d2fe5cf4ac`. Primer Scaffold #39803713129 FAIL en 1/173 tests Desktop: el cambio INT18→SAB13 sin compensación creó además divergencias de CD/ataque. Reparación **solo de prueba** con +3 a los dos ajustes persistidos. Final [Scaffold #39804358780](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39804358780) y [QA #39804358777](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39804358777) SUCCESS; build Android/Desktop y auditoría PDF SUCCESS.

Nuevo artefacto [86 PDF reales/793 páginas #12438041282](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39804358780/artifacts/12438041282), ZIP SHA256 `dcc98d9d60e0965b4c08d5dc0aec9a5edb8b4f98f27f8c720bf3568d1f285377`. Baseline C223 [84 PDF/777p #12398337724](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39713904751/artifacts/12398337724), ZIP `3fd2e9f3c747edcffe2a677ba4b58685f66a4f402edddd530e293489cacbb5b2`. Auditor PyMuPDF 1.26.7: **84/84** igual paginación, **777/777** texto/palabras/posiciones idénticos, **777/777** dibujos vectoriales idénticos; igual cantidad de imágenes (comparación de bytes de imágenes NO demostrada). Cero diferencias imprevistas.

Dos PDF nuevos Ilyra A/B de ocho páginas con P4 en página 6 muestran **APTITUD SEGÚN FUENTE**, CD **15**, ATAQUE **+7**; se preservan UUID/preparación, tamaño Carta y última NOTAS; inspección raster directa sin recortes visibles. Hash PDF A `b10c3dcf6b8bb2a15a9c559c0f51846c1c828298b73742f1b3c6fab2b9ba2875`, B `add718786d929d8cbb678cd25e5997d0c44d410f257fcce7fb6d9d93071296c5`.

Auditores [#12438096072](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39804358780/artifacts/12438096072): ZIP `392cfa732cb7126673ad9fca74f67da39e37704ded4464a84577ea1e65461370`; 86 PDF/793 páginas, perímetro 0 errores, 793/793 marcos y logos, última NOTAS 86/86, 0 omisiones C207, 0 fallos P4/Libro. [Acta JSON exacta](../pc-sheet-v11/qa/evidence/2026-10-22_V11_C224_P4_UNIFORM_CASTING_ABILITY_REAL_APP_PDFS.json).

**G0 PARCIAL / G1 PASS semántico / G2-G3 PARCIALES / G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null / G5-G6 PENDIENTES.** C211 BLOCKED 3/3 normal y 0/2 estructural, original Mara runtime propietario pendiente, 32 M50800 y dos V11 QA abiertos; Android instalado y owner no aprobados. PR #173 DRAFT NO MERGE. Próximo: conciliación D-0076/golden por módulos sobre PDF genuinos, evitando C211 hasta nueva evidencia y alternativa.
