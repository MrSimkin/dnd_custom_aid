# C230 — P4: referencia mágica con el valor de cada fuente real

Fecha 2026-10-26 (Chile). PR #173 DRAFT/NO MERGE, rama `docs/pc-sheet-v11-approved-implementation-handoff`, HEAD anterior `31d5c574`. **Estado: PASS LOCAL FÍSICO acotado (NO G4, NO aprobación del propietario).**

## Decisión del propietario que cierra la brecha

Resuelve la decisión pendiente de D-0076 §3.24 que C221, C223 y C224 dejaron abierta («presentación per-source»). Respuesta del propietario (2026-10-26): cuando las fuentes de magia difieren, «SEGÚN FUENTE» significa mostrar el valor de cada una, por ejemplo `CD = "Mago 15 / Hechicero 17"`, y lo mismo para aptitud y ataque mágico.

Segunda decisión del propietario (2026-10-26): ningún personaje tiene más de 4 espacios de conjuro por nivel; el caso de 7+ espacios **no aplica** y no se diseña. Elimina esa pendiente de P115/P116; las casillas 1..6 de C226/C227 permanecen sin cambios.

## Producto

- Shared `PcSheetV11SpellSourceLabels.perSourceParts` / `perSourceReference` generan `Nombre valor` por fuente real (UUID distinto), con nombres normalizados a una línea (CR/LF/TAB). Una fuente que desconoce el dato imprime `—`, nunca un número inventado.
- Los renderers V11 Android y Desktop, en la tarjeta P4 de cada campo realmente contradictorio:
  1. una línea `Mago 15 / Hechicero 17` a 12, 10, 9 u 8 pt si cabe en 152 pt;
  2. si no cabe, hasta **tres** líneas de 7 pt, una fuente por línea;
  3. con más de tres fuentes, o si una línea no cabe a 7 pt, se conserva el aviso veraz `SEGÚN FUENTE` (nunca se trunca texto).
- **P1 no cambia:** su celda mide 81 pt, así que conserva `SEGÚN FUENTE`. No es una decisión del propietario; es una limitación de espacio registrada para decisión futura.
- No cambian geometría de tarjetas, otros campos, casillas, familias legacy ni `main`.

## Pruebas

- Shared `PcSheetV11SpellSourceLabelsTest.perSourceReferenceListsEachRealSourceWithItsOwnValue`: `Mago 15 / Hechicero 17`, `Mago INT / Hechicero CAR`, `Mago +7 / Hechicero —`, sin fuentes → null.
- Cinco pruebas de PDF real de escritorio ajustadas: P4 ya no espera `SEGÚN FUENTE` cuando caben los valores; verifican cada fuente por nombre y valor. Con cuatro fuentes (fixture C221) se verifica el aviso de reserva.
- Primera ejecución local reveló dos defectos reales, ya corregidos: (1) nombres de fuente con CR/LF producían un glifo inexistente; (2) tres fuentes de nombre largo (Ilyra) no cabían en una línea, de donde nace el apilado.

## Evidencia local (2026-10-26, entorno Windows con JDK 17 / Gradle 9.5.0 / plataforma 36)

[Acta local](../pc-sheet-v11/qa/evidence/2026-10-26_V11_C230_P4_PER_SOURCE_MAGIC_REFERENCE_LOCAL.json). Se generaron con `DesktopPcSheetV11RuntimePdfProofTest` los 94 PDF del código anterior (`31d5c574`) y los 94 del nuevo, y se compararon con PyMuPDF: **94/94 paginaciones iguales; 859/859 páginas con los mismos vectores e imágenes; 823/859 con las mismas palabras y posiciones**. Las **36 páginas distintas** (18 PDF A/B) son todas la referencia P4: desaparece `SEGÚN FUENTE` y aparecen nombres y valores de las fuentes; ninguna otra diferencia. Inspección raster: Ilyra (tres fuentes, líneas apiladas de 7 pt) cabe en la tarjeta; Mara (una fuente) imprime una línea. Con una fuente sin dato (Sage, High Elf en Ilyra) la tarjeta muestra `—`.

**CI:** [Scaffold #40382631343](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40382631343) y [QA #40382631470](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40382631470) SUCCESS en `5cee7050` (código `6ad1fb88`). La comparación sobre los artefactos de CI ([C230 #12679961537](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40382631343/artifacts/12679961537) contra [C229 #12471702417](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39883400413/artifacts/12471702417)) repite el resultado local: 94/94 paginaciones, 859/859 vectores e imágenes, 823 páginas con las mismas palabras y 36 con solo la referencia P4 cambiada. Auditores físicos de CI: 94 PDF / 859 páginas, 0 errores; medidores P4 sin PDF fallido; P3/C2 0 fallos.

## Qué NO demuestra

No es G4 ni aprobación del propietario; no hay APK instalado. Android solo se compiló, no se ejecutó en dispositivo. Falta la comparación de PDF reales contra el lote C229 (94 PDF / 859 páginas) y el acta con hashes.

**Gates sin promoción:** G0 parcial, G1 PASS semántico anterior, G2/G3 parciales, G4 BLOCKED 105/105 NOT_VERIFIED con `active_candidate=null`, G5/G6 pendientes, C211 BLOCKED 3/3 normal y 0/2 estructural. PR #173 DRAFT NO MERGE.

**Próximo paso:** CI exacto de este commit, comparar los PDF del Scaffold contra C229, registrar acta y cerrar este checkpoint con PASS LOCAL acotado o con el defecto demostrado. Después, reglas G2/G3 sin tocar; no abrir más bordes mágicos sin necesidad demostrable.
