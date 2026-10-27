# C233 — P2 Equipo Especial: filas de mayor altura (D-0076 §3.21)

Fecha 2026-10-26 (Chile). PR #173 DRAFT/NO MERGE. **Estado: PASS LOCAL FÍSICO (no G4).** Código `2d806db8`: Scaffold [#40424922755](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40424922755) y QA #40424922744 SUCCESS. Auditoría de envoltura de la CI: 96 PDF / 824 páginas, 0 errores, incluido `p2_special_rows`. Los PDF de la CI coinciden con los locales (824/824 páginas). Contra `af7a1058`, las únicas palabras que desaparecen son el nombre de cabecera de las 31 páginas C2 eliminadas. Matriz: P208 G3 PASS acotado; P208 global y G4 siguen NOT_VERIFIED.

Historial anterior: [C231](2026-10-26_V11_C231_SOURCE_TYPE_LABELS_P1_P4.md) (fuentes de conjuro por tipo, lista P1 C232) y [C211 RCR-1](2026-10-26_V11_C211_RCR1_RECOVERY_CANDIDATE.md).

## C211 — cierre

- R1 `af7a1058`: Scaffold [#40413679203](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40413679203) SUCCESS; QA #40413679233 FAILURE en un solo tripwire literal.
- R2/2 `3af77855` (solo tripwire): Scaffold [#40423111021](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40423111021) y QA [#40423111088](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40423111088) SUCCESS.
- Comparación de PDF de la CI (`8b57bd22` → `af7a1058`, 96 PDF): de 877 a 855 páginas, con 22 PDF que tienen una página C2 menos. Las únicas palabras que desaparecen son el nombre del personaje y el título de la página C2 eliminada. No se pierde ningún dato.
- **C211: PASS LOCAL FÍSICO (no G4).** Presupuesto global RCR-1: 1/2 gastado.

## Defecto

En P2, una fila de Equipo Especial solo se publicaba si cada celda cabía en **dos líneas**. Con el primer objeto largo, el bucle se detenía y **todo** el Equipo Especial iba a C2. Mara A/B mostraba la tabla de P2 vacía, con ocho filas libres, y sus objetos reales en C2.

Contradice D-0076 §3.21 (decisión expresa del propietario, 2026-10-08): «Mostrar primero las entradas reales… Seguir el ritmo general de filas… cuando la entrada quepa, y admitir mayor altura cuando el nombre/descripción requiera varias líneas. No truncar…». No hace falta una decisión nueva del propietario.

## Cambio (Desktop + Android, sincronizados a mano)

1. Cada objeto especial ocupa **una o más pautas completas de 25 pt**. Una pauta conserva sus dos líneas. Las bandas más altas mantienen la primera línea base a 10 pt y un pie de 4 pt (`specialLinesFor`).
2. Los objetos se colocan en orden de origen. Desde el primero que no cabe en las 8 pautas restantes, el sufijo completo va a C2 sin cambios (fragmentos C2 existentes).
3. Bajo los objetos reales, filas libres de una pauta hasta el pie. Cada banda (objeto o libre) tiene **una** casilla ACTIVO centrada. El sombreado alterna por banda.
4. Sin objetos especiales, la salida es idéntica a la anterior (8 bandas de una pauta, ubicaciones preimpresas).

## Pruebas

- Tres pruebas PDF reescritas a la regla:
  - `realMaraApplicationServiceWritesMultiPageLetterPdfFromOwnRenderer`: el Astrolabio está en P2 y no se repite en C2.
  - `realIlyraSpellcasterProducesP4AndHandwrittenGridAsLastPage`: el Spellbook está en P2, sin C2 especial.
  - `g3IlyraGenuineSingleOrdinaryAndSpecialC2ShareOnePhysicalPageAB`: el Spellbook (ubicación, notas, ACTIVO) está en P2, sin C2 de equipo y sin «CONTINÚA EN C2».
  - Queda sin cubrir: la ruta de página C2 compartida «EQUIPO + EQUIPO ESPECIAL» ya no la alcanza ningún personaje real. Ya era así desde C211 R1.
- Nuevo control físico independiente (PyMuPDF) en `audit_v11_pdf_physical_envelope.py` (`p2_special_rows`): ninguna palabra cruza una regla de fila y cada banda tiene exactamente una casilla ACTIVO de 9 pt. Control negativo: una palabra insertada sobre una regla real se detecta. El sombreado de la banda superior acepta alturas de 25·n pt.
- Tripwire `test_v11_special_equipment_manual_contract.py` actualizado a `repeat(specialBands.size)`.

## Verificación local

- 443 pruebas Shared, 178 Desktop y 60 Python pasan. Android `assembleDebug` OK.
- Los 11 auditores pasan sobre 96 PDF / 824 páginas, incluido el nuevo control de filas.
- Comparación con los PDF de la CI de `af7a1058`: de 855 a 824 páginas, con 31 PDF que tienen una página C2 especial menos. Las únicas palabras que desaparecen son el nombre del personaje en la cabecera de la página eliminada. No se pierde ningún dato.
- Mara A/B: dos objetos reales en P2, de 4 pautas cada uno. El resto sigue en C2.
- Ilyra: Bastón (1 pauta) y Spellbook (3 pautas) en P2, más 4 filas libres.

## Pendiente

- **Siguiente (P3, medido contra el golden `v11_extremo_variante_A.pdf` p3):** en Historia y Crónica, la app escribe Fira de 9 pt sobre renglones de 12,4 pt. El golden usa 7 pt sobre renglones de 6 mm (~17 pt), con la línea base cerca del pie de cada renglón (aspecto de cuaderno). Cambiarlo afecta la paginación de P3/C2 y el aspecto, así que se mostrará una vista previa al propietario antes de implementarlo. Sigue la comparación de C2 y las demás páginas.
- G4 106 reglas NOT_VERIFIED; G5/G6 (APK en teléfono y QA del propietario) pendientes.
