# C219 — cifras reales de espacios y gastados en cabeceras P4

Fecha: 2026-10-21. PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT, NO MERGE; rama `docs/pc-sheet-v11-approved-implementation-handoff`, HEAD anterior `ca67e9dae99b1a6c1f4bcb5e645166a828d10650`, main remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Brecha funcional distinta y probada

**D-0076 §§3.25–3.26** exigen en **la misma línea** de cada NIVEL P4 los ESPACIOS reales y ESPACIOS GASTADOS reales, con casillas para escritura manual separadas de estados digitales. En `V11-INTERNAL-PROOF-Mara-A.pdf` P1 imprime de forma real `N1 4 2`, `N2 3 1`, `N3 3 2`, `N4 3 0`, `N5 2 1`; sin embargo en P4 esos mismos niveles muestran sólo `ESP.` y tres casillas vacías, **ocultando números auténticos disponibles**. La maqueta golden extremo A p7 muestra la banda de niveles, pero sus datos ficticios independientes no sustituyen las cifras de la App. Esta brecha no tiene relación con C211/C212–C218.

## Corrección productiva C219

En los DOS renderers PDF Android y Desktop, la cabecera P4 nivel 1–9 consulta por **su nivel real** `data.spellSlots` y compone un label compacto de una única línea `ESP.<totalSlots> G.<spentSlots>` con el **mismo tipo de letra real** de la banda aprobada; la restricción física mide el label completo contra el ancho libre **antes** de dibujarlo. Mantiene tres casillas vectoriales vacías de 7pt para uso manual: nunca las marca automáticamente con gasto digital ni crea casillas ficticias por cada capacidad. No repite información en una segunda línea ni cambia los títulos, conjuros, preparación, fuentes, reparto entre tres columnas, número de páginas o datos.

Si no hay slot real para el nivel, se conserva `ESP.` con línea manuscrita (sin números inventados). Si se recibe un dato verdaderamente patológico cuyo label entero no cabe en los 45pt disponibles, se conserva la banda `ESP.` sin truncarlo ni abreviar silenciosamente; ese caso mantiene su información en P1/C2 según el flujo existente y requerirá una solución de layout separada para cumplir globalmente G4. No fingir compatibilidad total para enteros de 10 dígitos.

**Pruebas específicas:** test Kotlin con PDF auténtico Mara A/B que comprueba cinco parejas `4/2,3/1,3/2,3/0,2/1`, y que ningún nivel sin fuente gane números; auditor PyMuPDF físico P4 vigente ampliado para reconocer `ESP.N G.M` y contabilizar sus 3 casillas genuinas, con test sintético de nuevo formato. Los casos P4 sin slot deben mantener el contador vacío.

**Estado al publicar:** IMPLEMENTADO, **CI y PDF real del SHA pendientes**. Exigir 76 PDF genuinos (o el corpus íntegro habitual), recuento de 9 contadores por P4, números sólo cuando hay fuentes, 27 casillas manuales por P4, conservación de nombres/conjuros/folios y ninguna alteración de otras áreas; no promover a PASS si hay fallo. El candidato no cambia el contrato aprobado ni matriz de 105 reglas.

**Compuertas preservadas:** G0 parcial, G1 semántico PASS histórico, G2/G3 parciales, **G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes. C211 BLOCKED 3/3 normal, RCR-1 estructural 0/2; C212–C218 PASS locales anteriores. Sin Android instalado ni aceptación técnica/visual del propietario. PR sin merge.

**Siguiente trabajo:** si C219 se prueba físicamente, identificar y corregir con evidencia el tratamiento de slots reales extraordinariamente grandes o de niveles épicos solo cuando provengan del modelo auténtico y sea material; después conciliar otros módulos D-0076/golden y la matriz G4 por candidato completo.

## Evidencia física final C219: **PASS LOCAL** del contador P4 auténtico, NO G4

**SHA exacto productivo:** [`86d1059105`](https://github.com/MrSimkin/dnd_custom_aid/commit/86d1059105be2266cc1196358b61c10ff3ab12b0). [Scaffold #39700421544](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39700421544) **SUCCESS**, incluyendo Kotlin/Android, auditor físico P4 actual y generación de PDF Desktop; [QA traceability #39700421494](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39700421494) **SUCCESS**.

**PDF reales:** antes [76 PDF C218 #12392115480](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39697902090/artifacts/12392115480), ZIP SHA-256 `af222e39307c835d91a37278f9896bc43952c6e6913d119fd3bf5a607de8e449`; después [76 PDF C219 #12392678444](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39700421544/artifacts/12392678444), ZIP SHA-256 `47ae9a6f103ac039153bc49c9e8df9c3d74908229e3967ac4ff34996317a093d`.

Comparación PyMuPDF independiente y comprobación visual real:
- **76/76 PDF y 713/713 páginas**, roles físicos idénticos; **664/664 páginas que no son P4 con TODOS sus glifos, coordenadas y vectores idénticos**. Son 49 páginas físicas P4 en el corpus.
- Antes había **405** etiquetas `ESP.`; después, **187 cabeceras de nivel usan cantidades reales `ESP.<total> G.<gastado>` y otras 218 conservan ESP. sin inventar slots**. El multiconjunto de palabras fuente original, tras normalizar solo las dos palabras nuevas de estas etiquetas a `ESP.`, coincide exactamente. **0 nombres de conjuros/IDs perdidos, 0 dobles impresiones, 0 páginas adicionales**.
- Los únicos trazos que desaparecen son **187 líneas manuscritas cortas** de `ESP.` sustituido por dos cifras de fuente. **0 otros vectores antiguos borrados y 0 vectores nuevos inesperados**. Se conservan las **tres casillas en la misma banda** por nivel, sin marcas digitales falsas; auditor vigente `scripts/audit_v11_p4_slot_meters.py` y sus unit tests PASS en el CI.
- Inspección de imagen `V11-INTERNAL-PROOF-Mara-A.pdf` p7: NIVEL 1 `ESP.4 G.2`, N2 `ESP.3 G.1`, N3 `ESP.3 G.2`, N4 `ESP.3 G.0`, N5 `ESP.2 G.1`; N6–N9 quedan vacíos como dicta la fuente. Todos los contadores caben en **una línea y sin superponer las casillas**.
- [Evidencia JSON exacta](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C219_TRUE_SLOTS_P4_REAL_APP_PDFS.json) con hashes, runs, límites y trazabilidad.

**Alcance restringido:** **C219 PASS LOCAL físico del contador P4 con datos que caben en el encabezado**. Los datos excepcionales de longitud imposible (por ejemplo enteros de 10 dígitos) no se reproducen íntegros en esos 45pt: conserva el indicador manual sin truncar ni sustituir dígitos, mientras el flujo P1/C2 anterior conserva fuente. Esa excepción **permanece pendiente** de una solución medida distinta; no afirmar PASS total P409/P410 ni P4. No existe validación PDF Android instalado ni aceptación técnica o visual del propietario.

**Gates sin promoción:** G0 parcial, G1 PASS semántico anterior, G2/G3 parciales, **G4 BLOCKED 105/105 NOT_VERIFIED / active_candidate=null**, G5/G6 pendientes, C211 BLOCKED (3/3 normal, RCR-1 0/2), C212–C218 PASS locales históricos. PR #173 DRAFT, NO MERGE.

**Siguiente trabajo previsto:** resolver la visualización sin pérdida de **valores reales de espacios/gastados extraordinariamente largos** en P4 manteniendo su encabezado de una línea, solo con prueba de origen y golden; o avanzar otra diferencia funcional concreta de un módulo diferente. Después consolidar por regla G1/G2/G3/G4 únicamente con evidencia de mismo candidato y revisión independiente, sin reapertura de C211.
