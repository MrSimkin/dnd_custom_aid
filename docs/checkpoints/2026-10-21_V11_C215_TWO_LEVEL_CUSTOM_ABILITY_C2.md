# C215 — Tarjeta de atributo custom en dos niveles realmente medidos (C2)

**Fecha:** 2026-10-21. **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT/SIN MERGE. Rama autorizada `docs/pc-sheet-v11-approved-implementation-handoff`. HEAD inicial `aa07e38626c02d99feaa9f87524097958db5bc85`; `main` remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. [Scaffold #39671000317](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39671000317) y [QA traceability #39671000191](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39671000191) **SUCCESS** previos.

## Defecto de producto C204 delimitado por evidencia genuina

[Checkpoint C214](2026-10-21_V11_C214_MEASURED_C2_SCORE_MODIFIER_SOURCE_INK.md) demostró **199** formas compactas reales, sin pérdida de texto, en 76 PDFs auténticos. Sin embargo, los valores permanecían en **la misma línea** que el nombre, cuando el contrato V11 requiere jerarquía física de tarjeta con nombre y puntaje/óvalo separados. El compositor `PcSheetV11TextFlow` soporta saltos de línea explícitos con offsets UTF-16 intactos, blancos realmente reservados y grupos `keepTogetherIfFits`. No necesita otra heurística o paginador paralelo.

## Intervención C215, bloque coherente Android+Desktop

- Cada fuente custom **aún no impresa en P1** se proyecta en su único `attribute:<index>` con `nombre + "\\n\\n" + score + " / mod " + modifier + " / salvación " + valor_real`. El primer renglón contiene el nombre original íntegro, el siguiente es una reserva **vacía medida** y el tercero conserva los valores originales. No se sintetizan registros digitales ni se reimprimen puntajes; el salto es geometría de papel del renderer.
- El compositor compartido decide wrapping, unidad con habilidades, columna y páginas desde el texto con offsets originales de la proyección. Al crecer los campos la paginación C2 **puede variar válidamente**; comparar integridad fuente→PDF, no exigir artificialmente idéntico número de páginas.
- Solo si la celda vacía **inmediatamente anterior** a los valores está en la misma columna y toca geométricamente la fila de valores, los trazos rectangulares/ovalados se extienden hacia esa celda **ya reservada** hasta 17pt sobre el baseline. Si la línea anterior no existe (continuación excepcional), se usa el contorno compacto demostrado por C214 sin descartar ningún valor.
- Preservar las bandas y rieles de agrupación C213, C212, relleno de cuadrículas C207, demás módulos y familias legacy. Los dos escritores nativos tienen las mismas decisiones semánticas y geometría. Guardia de paridad `scripts/test_v11_c2_source_navigation_contract.py` ampliada.
- No fingir que equivale a todas las cotas de la maqueta ni a las ocho golden: C204 y G4 **siguen abiertos** hasta comparación exhaustiva.

**Estado al publicarse:** `IMPLEMENTADO / CI Y PDFs DEL CANDIDATO PENDIENTES`. Se exige Scaffold/QA de SHA exacto, A/B PDFs **reales de Desktop**, revisión de nombre largo/140 habilidades y ausencia de pérdida, colisiones, fuga fuera C2 y regresiones P1/P2/P3/P4/CN3/Libro/NOTAS. Un fallo es STOP, no PASS; no repetir C211.

**Gates:** G0 histórico PARCIAL; G1 semántico PASS anterior; G2/G3 PARCIALES; G4 BLOCKED con 105/105 NOT_VERIFIED y active_candidate=null; G5/G6 PENDIENTES. C211 BLOCKED 3/3 normales, 0/2 RCR-1 estructurales, sin reiniciar. C212/C213/C214 PASS locales acotados previos, sin nueva aprobación técnica o visual del propietario.

**Siguiente trabajo:** verificar el nuevo binario exacto y PDF físico. Si pasa, registrar métricas y precisar qué resta de C204; luego continuar únicamente otras brechas verificables y reconciliación progresiva G4 sin falsas promociones.
