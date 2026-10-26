# C211 — RCR-1: candidato de recuperación (evidencia recuperada, SIN implementar)

Fecha 2026-10-26 (Chile). PR #173 DRAFT/NO MERGE. **Estado: RECOVERY_CANDIDATE. No se ha escrito código de recuperación. R1/2 NO consumido.**

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