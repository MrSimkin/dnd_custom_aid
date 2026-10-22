# C220 — enteros reales largos en P4 sin truncar, una sola línea

Fecha: 2026-10-21; PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) sigue DRAFT/NO MERGE, rama `docs/pc-sheet-v11-approved-implementation-handoff`. HEAD anterior `fda16f7698b5ab4cb4d1dbbd51b6aa8f66393920`; main remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. [C219](2026-10-21_V11_C219_P4_REAL_SPELL_SLOT_HEADERS.md) PASS LOCAL acotado, sin nuevo aval técnico ni visual.

## Defecto acotado y prueba source-backed

**D-0076 §§3.25–3.26** exige las cifras reales de TOTAL y GASTADOS en la **misma línea del nivel P4**. C219 corrigió 187 cabeceras genuinas, pero dejaba en blanco `ESP.` cuando el texto completo no cabía en 45pt; el código existente P1 ya demuestra el caso técnico verdadero `totalSlots=2147483647, spentSlots=2147483646` con ruta C2 sin truncar. En el PDF real Mara P4 las cabeceras NIVEL ocupan ~30pt y dejan casi 45pt **adicionales** entre NIVEL y el ESP. fijo. Esa zona pertenece a la MISMA banda, sin desplazar las 3 casillas vacías.

## Implementación de producto C220

Android+Desktop, en la misma rama de dibujo de `spell-level:` real: primero medir el texto fuente completo `ESP.N G.M` con el **font Barlow real a 7pt** y tratar de imprimirlo en la banda de 45pt C219 ya certificada. Si no cabe, medir el título auténtico `NIVEL X` con la fuente BODY **a 9pt** y desplazar el origen de `ESP.N G.M` solo lo mínimo necesario para usar espacio libre tras ese título, con ≥7pt de separación real y ≥5pt hasta la primera de las tres casillas. No se reduce la fuente, no se introducen números, no se parte la línea, no se altera el título, no se inventan marcas. Para entradas aún imposibles físicamente se conserva la línea vacía C219 y la ruta fuente P1/C2, sin truncar ni atribuir un PASS global.

El auditor PyMuPDF P4 existente ahora localiza las casillas por sus **coordenadas físicas invariables por columna**, no por distancia al indicador ESP. que ya puede moverse legalmente. Test sintético del auditor ajustado a la geometría P4 real.

**Fixture productivo de estrés:** derivar del backup REAL Mara con conjuros, inyectando SOLO el mismo límite entero en el registro persisted de nivel 1: `2147483647/2147483646` en A/B; mantener nivel 2 auténtico `3/1`. Kotlin genera y preserva estos PDF del Desktop App con nombres `V11-INTERNAL-PROOF-P4-intmax-real-spell-slots-A/B.pdf` y exige el texto íntegro exactamente una vez en la lista rápida P4. A/B, tarjetas intactas, P1/C2 sin perder valores. A diferencia del fixture anterior sobre Aldren no lanzador, aquí P4 existe.

**Estado de esta entrega:** IMPLEMENTADO, **sin PASS hasta el CI exacto y los PDF auténticos** del lote ampliado, comprobando ancho/colisión, 3 casillas, 0 pérdidas, 0 páginas o cambios fuera de alcance. C219 PASS anterior permanece histórico sin repetición.

**Gates:** G0 parcial histórico; G1 semántico PASS previo; G2/G3 parciales, G4 BLOCKED **105/105 NOT_VERIFIED**, `active_candidate=null`; G5/G6 pendientes; C211 BLOCKED 3/3 normal, RCR-1 estructural 0/2. C212–C219 PASS LOCAL previos no reabiertos. Android instalado/owner técnico y visual/merge no autorizados.

**Siguiente trabajo:** si C220 pasa, conciliar las excepciones P4 de representación por fuentes/preparación con reglas P406–P410 y golden aprobados usando datos verdaderos, y continuar G2/G3/G4 sin crear PASS desde checks locales.
