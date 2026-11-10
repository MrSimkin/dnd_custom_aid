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

## Evidencia final exacta de C220: **PASS LOCAL FÍSICO**, no G4

**Código productivo:** [`e0513e5`](https://github.com/MrSimkin/dnd_custom_aid/commit/e0513e514c26bf5746d6bb27d0581c31c9c36faf); **corrección exclusivamente de fixture de auditor:** [`348a6b9`](https://github.com/MrSimkin/dnd_custom_aid/commit/348a6b9ebcbff3d37eb4fd036047a7efd0420906). El [Scaffold exacto final #39702383965](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39702383965) **SUCCESS** en backend, BD, Kotlin/Android, auditorías P4 y generación de PDF real Desktop. [QA traceability #39702313846](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39702313846) **SUCCESS** en el commit productivo. El primer Scaffold #39702313827 falló por un test sintético que añadía una segunda etiqueta ESP. a otra fila; se ajustó el fixture para desplazar **la única etiqueta de la misma fila**. El código PDF no fue modificado por ese ajuste.

**A/B auténticos:** anterior [76 PDF C219 #12392678444](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39700421544/artifacts/12392678444), ZIP SHA-256 `47ae9a6f103ac039153bc49c9e8df9c3d74908229e3967ac4ff34996317a093d`; nuevo [78 PDF C220 #12393483195](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39702383965/artifacts/12393483195), ZIP SHA-256 `7056283e34ee15d0023c7fe93f67cd080f20e4503d186796138a0b55a2adaf45`.

PyMuPDF independiente:
- **Los 76 PDF existentes conservan 713/713 páginas idénticas en TODAS las palabras, coordenadas y vectores de PDF.** No hubo regresiones a C219 ni a otros módulos.
- Se añadieron **exactamente 2 PDF genuinos del Desktop App**, casos de estrés Mara A/B, de **9 páginas cada uno** (total candidato 78 PDF / 731 páginas). En cada PDF aparecen `2147483647` y `2147483646` una vez en su registro completo fuente C2 y una vez **intencionalmente repetidos** como `ESP.2147483647 G.2147483646` en la página P4 p7; ningún valor suprimido, recortado ni falsificado.
- En P4, `NIVEL 1` y ambas cifras reales comparten UNA línea horizontal. Bbox física del indicador de total x79.385–120.503, gasto x121.903–157.092; **3 casillas de 7pt** siguen a derecha sin superposición, fuente Barlow 7pt no reducida. Sin páginas ni áreas invadidas. N2 y sus cifras reales permanecen igual.
- Se inspeccionó raster ampliado de la cabecera P4 física, sin colisión visible; el auditor `scripts/audit_v11_p4_slot_meters.py` revisa casillas **por posiciones invariables en columna**, evitando falsos FAIL al moverse el indicador numérico fuente.
- [Acta JSON reproducible](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C220_P4_INTMAX_COUNTERS_REAL_PDFS.json) con SHA de archivos, IDs y geometría.

**Veredicto solo C220:** PASS LOCAL para enteros de 10 dígitos en una cabecera P4 realmente generada, manteniendo texto y flujo previo. **NO afirmar P410 completo, golden global ni G4 PASS**; 105 reglas siguen `NOT_VERIFIED` y `active_candidate=null`. G0 PARCIAL, G1 PASS semántico previo, G2/G3 PARCIALES, G5/G6 pendientes. C211 BLOCKED 3/3 normal y RCR-1 0/2 estructural; C212–C219 PASS locales previos intactos. No existe PDF Android instalado ni aceptación técnica/visual del propietario, sin merge.

**Próximo trabajo:** avanzar por una nueva diferencia funcional demostrable en representación de fuentes y preparación por conjuro de P4 (P406), contrastada con D-0076 §3.24 y golden, sin inferir fuente ni marca cuando hay varias asociaciones. Registrar pruebas y no mover compuertas sin verificación independiente.
