# C225 — Referencia mágica de P1 y P4 sin contradicciones por origen

**Fecha Chile:** 2026-10-22. **PR #173 DRAFT, NO MERGE.** Estado: `IN_PROGRESS` hasta CI y auditoría de PDF reales.

## Incumplimiento nuevo y acotado

El contrato visual V11 y D-0076 §§3.11, 3.24–3.26 ordenan repetir aptitud, CD y ataque mágico en las hojas P1 y P4 respetando los orígenes de conjuro. C221/C223/C224 protegían correctamente la referencia superior P4 cuando los perfiles persistidos contradicen el valor global, pero **la banda P1 seguía imprimiendo los valores globales** sin comprobar las mismas contradicciones. Es un fallo independiente de C211 (P2/C207) y no exige nueva disposición ni decisión por fuente.

## Alcance funcional

- Shared `PcSheetV11SpellSourceLabels.contradictoryOverallFields` combina **sin alterar semántica** las verificaciones ya probadas C221/C223/C224 en un único método usado por ambos consumidores P1 y P4.
- Los dos renderizadores V11 (Android/Desktop) aplican `SEGÚN FUENTE` exclusivamente a cada uno de los tres campos P1 respaldado por conflicto real, con guardia de anchura física de 81pt. Con fuentes sin discrepancia o no lanzadores sin conjuros, quedan los campos originales y el espacio en blanco. Las páginas P4 continúan igual salvo la refactorización sin cambios de resultado.
- Shared test demuestra los tres campos a la vez, perfiles sin contradicción, ausencia de conjuros y perfiles mixtos conocido/desconocido. Tests de integración Desktop existentes **C223 y C224** ahora exigen en la P1 real, para A y B, dos avisos numéricos o un aviso de aptitud respectivamente; siguen produciendo PDF genuinos del servicio App. Se rehúsan mocks/Python runtime y cambios legacy.
- Comprobación prevista: CI Kotlin/Android/Desktop y auditor físico CI, obtener ZIP de PDFs del mismo SHA, comparar contra el lote anterior de C224 (86 PDF / 793 páginas) y verificar que los cambios están restringidos a P1 de casos con fuente contradictoria, sin pérdida de contenido, dibujo, logo, notas ni paginación. No declarar PASS global ni G4 por green CI.

## Fuente y presupuestos

`main` al seleccionar bloque: `0c009f10d5e66880d68fa3732491c6fc3e172f22`; rama antes de C225 `5d9b91c2857f7eb6c196d6f20fdaccff4829f723`. Último C224 PASS LOCAL demostrado en [Scaffold #39804358780](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39804358780), [QA #39804358777](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39804358777) y acta hash del checkpoint anterior. Esos resultados son evidencia anterior, **no PASS de C225**. Si CI o PDF fallan, guardar causa exacta y respetar presupuesto RCR-1 por bloque; no tocar C211, que sigue BLOCKED 3/3 normales y 0/2 estructural.

**Gates intactos:** G0 histórico PARCIAL / G1 semántico PASS / G2 y G3 PARCIALES / G4 BLOCKED **105/105 NOT_VERIFIED, active_candidate=null** / G5-G6 PENDIENTES. QA original propietario Mara no recuperado, Android instalado y revisión owner pendientes. 32 observaciones históricas M50800 y 2 V11 se conservan sin reetiquetar. **No fusionar PR #173**.

**Próxima acción:** verificar CI exacto C225, auditar PDFs reales A/B y los casos modificados P1 contra el lote C224 con hashes; cerrar PASS LOCAL o BLOCKED preciso. Luego escoger siguiente brecha funcional o conciliación modular G4 sustentada en output, sin repetir C225 ya verificado.

## C225 cierre físico: PASS LOCAL, **NO G4**

SHA funcional `097bfeb941524c66c586112b90a41b912f116411`. [Scaffold #39815515425](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39815515425) **SUCCESS** y [QA #39815515423](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39815515423) **SUCCESS**. [86 PDF auténticos #12442961337](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39815515425/artifacts/12442961337), ZIP SHA256 `a2246c975ae3e97dd95016133909e627d3fcb40aeff7519431f8f152bab8660f`; [auditor externo #12442662001](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39815515425/artifacts/12442662001), ZIP SHA256 `2acaba32bb91b511b43efac36ba0a192dae4511eeaa545874f45af40f58ec160`, 86/86 PDF, 793/793 páginas y **0** errores de perímetro. C207: 233 páginas C2, 424 regiones, 0 omisiones.

Comparador independiente PyMuPDF de lotes **C224→C225**: 86/86 misma paginación; 793 páginas originales/nuevas, 755/793 mismas palabras y coordenadas; 38 cambios de **solo P1**, todos dentro de banda física de referencia mágica `x312..567, y524..550`; **793/793 dibujos vectoriales idénticos e imágenes referenciadas idénticas**. Las sustituciones obedecen exclusivamente a conflictos fuente→global ya demostrados por C221/C223/C224: Mara reemplaza CD global falso, Ilyra muestra guard de aptitud o de CD/ataque según su perfil real. Dos variantes A/B preservan demás datos y última NOTAS. Captura raster de P1 Ilyra A confirma «APTITUD SEGÚN FUENTE / CD SALVACIÓN 15 / ATAQUE MÁGICO +7» sin recortes visibles.

**PASS LOCAL físico C225**, no cumplimiento íntegro golden D-0076, no revisión de runtime Android instalado/owner. G4 permanece 105/105 NOT_VERIFIED; G5/G6 pendientes. [Acta JSON exacta](../pc-sheet-v11/qa/evidence/2026-10-22_V11_C225_P1_P4_GLOBAL_REFERENCE_REAL_PDFS.json).
