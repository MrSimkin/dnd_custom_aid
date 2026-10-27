# C211 — RCR-1: candidato de recuperación (evidencia recuperada, SIN implementar)

Fecha 2026-10-26 (Chile). PR #173 DRAFT/NO MERGE. **Estado: PASS LOCAL FÍSICO (no G4) — R1/2 `af7a1058` + R2/2 `3af77855` (solo tripwire), CI exacto SUCCESS.**

```text
INCIDENTE: C211  Gate detectado: G3 (auditor físico C207)  Gate causante: G3 (dibujo de tablas pareadas en C2)
CANDIDATO: rama PR #173, commit 88b1e78a667eb51b00e8aabd679907afd408fc14 (tercer intento normal de C211),
           reconstruido localmente (JDK 17 / Gradle 9.5.0, DesktopPcSheetV11RuntimePdfProofTest)
           V11-INTERNAL-PROOF-C206-both-paired-overflow-A.pdf  sha256 551ee136aa2bcfbbc9567db3448ddc4d1c72a5acb66d1d5f13409c1425b83a6e
           V11-INTERNAL-PROOF-C206-both-paired-overflow-B.pdf  sha256 67a6ed89c712fa46437355673e7845eee2ec9e9dcda737613e4fa1611431055c
EVIDENCIA ORIGINAL: P2 de Mara A/B sin ningún objeto de EQUIPO (todo en C2) porque p2Eligible exige una línea por nombre;
           el golden llena P2 primero. El auditor scripts/audit_v11_p3_c2_manuscript.py vuelve a fallar localmente
           igual que en CI #39361133716: "physical page 6: C207 incomplete/irregular 16pt grid at y=183.00;
           vertical=17, horizontal=41" (A y B). Informe: qa/evidence/c211/2026-10-26_C211_88b1e78_audit_v11_p3_c2_manuscript.json
INTENTOS NORMALES: 3/3 consumidos (0970c67, c66a4fc, 88b1e78) — ver 2026-10-20_V11_C211_ABORTED_P2_WRAPPED_SOURCE_C207_ROLLBACK.md
CAUSA RAÍZ: CONFIRMADA. En la página física 6 (C2 EQUIPO / 2) la segunda pareja termina en la fila 3 y debajo se dibuja
           la cuadrícula NOTAS (x313..569, ritmo 16 pt, 33 líneas). Pero la tabla pareada sigue dibujando sus líneas de
           fila a TODO el ancho (x42..570, grosor 0,45 pt, ritmo 12,4 pt, 41 líneas) por debajo de esa cuadrícula,
           porque `drawPaired` calcula el fondo visible de la tabla con la fila impresa más baja de AMBAS parejas.
           El auditor toma la línea de tabla como borde superior y ve dos ritmos superpuestos: cuadrícula irregular.
           El cambio de p2Eligible solo dejó al descubierto este defecto latente de C2 (antes ningún fixture producía
           parejas desiguales con NOTAS en una sola). Por eso los intentos 2 y 3, que solo ajustaron pruebas, no podían arreglarlo.
CHECKPOINT PASS AL QUE RETROCEDER: no hace falta retroceder; HEAD 8b57bd22 (C232 paso 3) tiene Scaffold #40405131084 y
           QA #40405131302 SUCCESS y no contiene el cambio de p2Eligible.
PROFUNDIDAD: 0 gates (G3 se trabaja sobre el HEAD vigente).
ALTERNATIVA: distinta de los intentos 1–3: además de permitir nombres largos envueltos en P2 (G2: cantidad una sola vez,
           en la primera línea), corregir la causa raíz en `drawPaired`: las líneas de fila de cada pareja terminan en su
           propia última fila cuando bajo ella hay NOTAS; nunca se cruzan con la cuadrícula. Puede fallar si otras
           auditorías o pruebas dependen de líneas de ancho completo (se verá en la prueba roja).
NUEVA PRUEBA ROJA: aserción física en C206 A/B pág. 6: ninguna línea de tabla (0,45 pt) dentro del rectángulo de la
           cuadrícula NOTAS de la segunda pareja; y el auditor C207 en verde sobre el lote completo.
CRITERIO DE ÉXITO: P2 de Mara A/B con objetos reales en EQUIPO; auditor C207/C208/P305 en verde en los 96+ PDF;
           ninguna palabra de origen perdida ni cantidad duplicada frente al lote anterior; Scaffold y QA SUCCESS.
PLAN DE REVERSIÓN: commit nuevo que restaure PcSheetV11PairedTableComposer.kt y los dos renderers al HEAD 8b57bd22.
RECUPERACIÓN: R1/2 pendiente de ejecutar (implementación alternativa); R2/2 solo una corrección.
PRESUPUESTO GLOBAL: 0/2 recuperaciones estructurales gastadas; esta usaría 1/2.
DECISIÓN: RECOVERY_CANDIDATE — se ejecuta R1 solo con la autorización del propietario.
```

Los dos PDF reconstruidos y el informe del auditor se guardan en `docs/pc-sheet-v11/qa/evidence/c211/`. El experimento local se hizo en un worktree temporal y no se integró en la rama.

## R1/2 — ejecución (2026-10-26)

**Autorización del propietario:** «apruebo, pero añadiría que si los objetos quedan en varias líneas dale a las líneas siguientes a la primera una pequeña sangría» y «apruebo el arreglo de esta forma e incorpora al contrato». Vista previa mostrada antes (P2 y C2 de Mara).

**Cambio (Shared + Android + Desktop):**

1. `PcSheetV11PairedAmountRouting.p2Eligible`: un nombre largo ya no es inelegible; se conserva el prefijo contiguo y solo van a C2 los nombres con saltos guardados o cantidades más anchas que su casilla.
2. `PcSheetV11PairedTableComposer.place`: sangría de 8 pt en las líneas de continuación; el corte de línea usa la anchura con sangría.
3. `drawPaired` (causa raíz): las cuadrículas NOTAS laterales de C2 se calculan antes de dibujar la tabla; las líneas de fila y el separador de cantidad de esa pareja se detienen encima de la cuadrícula.
4. Pruebas: una prueba nueva de sangría y cantidad única; cinco pruebas de PDF y una de Shared reescritas a la regla aprobada (orden de origen, cada cantidad una sola vez, ningún dato perdido); comparación de texto normalizada donde ahora hay saltos de línea.

**Prueba roja → verde (local):** el auditor `audit_v11_p3_c2_manuscript.py` que fallaba en C206 A/B pág. 6 queda en verde sobre los 96 PDF; también `audit_v11_c207_vacancy`, envoltura física, topología, tarjetas de P1, medidores P4 y tarjetas del Libro. 442 pruebas Shared y 178 de escritorio pasan; Android compila. Comparación con el lote de CI de `8b57bd22`: ninguna palabra de origen perdida; solo cambian títulos de página, y 22 PDF tienen una página C2 menos porque el equipo cabe en P2. Mara A/B: ocho objetos en P2 con sangría.

**Pendiente:** CI exacto. Si falla, queda **una** corrección (R2/2); si vuelve a fallar, STOP definitivo de C211. Presupuesto global: 1/2 recuperaciones estructurales gastadas.

## R2/2 — corrección única (2026-10-26)

**CI de `af7a1058`:** `Scaffold checks` (run 40413679203) SUCCESS, con compilación Shared/Android/Desktop y los auditores físicos sobre los PDF reales. `PC Sheet V11 owner QA traceability` (run 40413679233) FAILURE en un solo control textual: `test_v11_p2_empty_paper_rhythm_contract.py` buscaba `alternateP2Rows = !compactC2)` literal, y R1 añadió el argumento `stopRulesBelow` detrás.

**Corrección:** solo el tripwire. Ahora exige `alternateP2Rows = !compactC2,` y `stopRulesBelow = sideNotes.map` en ambos renderizadores. El código de la app no cambia. Se cuenta como R2/2 (criterio conservador): si la CI de esta corrección falla, C211 queda en STOP definitivo.

**Resultado:** Scaffold [#40423111021](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40423111021) y QA [#40423111088](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40423111088) SUCCESS en `3af77855`. La comparación de los PDF de la CI `8b57bd22` → `af7a1058` (96 PDF, 877 → 855 páginas) no pierde datos: solo desaparecen el nombre de cabecera y el título de las 22 páginas C2 eliminadas. **C211: PASS LOCAL FÍSICO, no G4.** Seguimiento en [C233](2026-10-26_V11_C233_P2_SPECIAL_EQUIPMENT_TALL_ROWS.md).

**Local antes de subir:** las 60 pruebas Python pasan. `check_resume_route`, `generate_android_pc_sheet_renderer --check`, `check_android_pc_sheet_pdf_delivery` y `check_v11_qa_registry` pasan. `check_v11_qa_registry` requiere `PYTHONUTF8=1` en Windows: `git show` se decodifica en cp1252 y no es un defecto en Linux.