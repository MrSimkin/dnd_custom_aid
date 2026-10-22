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
