# C216 — casillas manuscritas de habilidades personalizadas realmente ligadas en C2

2026-10-21 · PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT / NO MERGE. Rama `docs/pc-sheet-v11-approved-implementation-handoff`, HEAD inicial `262d9be6e747851f7616bc6dc9151acc7545da98`, main `0c009f10d5e66880d68fa3732491c6fc3e172f22`. CI previo exacto [Scaffold #39678210392](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39678210392) + [QA #39678210385](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39678210385) **SUCCESS**.

## Hallazgo concreto del golden original, sin reabrir C211–C215

PDF original aprobado (ZIP SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`), `V11_Carta/pdfs/v11_extremo_variante_A.pdf`, página física 4, módulo **ATRIBUTOS Y HABILIDADES**: cada habilidad asociada a Cordura/Honor tiene una **casilla vectorial vacía** de 7pt para uso manuscrito, antes del texto. Contraste real `V11-INTERNAL-PROOF-Mara-A.pdf` página física 4 del artefacto [76 PDF C215 #12382464137](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39676934860/artifacts/12382464137): `Habilidad Sincronía: 9 / EXPERTISE` y `Habilidad Manipulación de éter: 8 / PROFICIENT` sin ninguna casilla en su fila. La información digital está íntegra y correctamente vinculada; la falta es de **interacción física manuscrita**, no semántica ni paginación.

## Alcance implementado C216

En Android y Desktop V11, solo la ruta C2 `attribute-skill:<id real>` genera cada entrada con **seis espacios medidos iniciales** antes de su nombre, sin cambiar ni deducir habilidad, valor, entrenamiento, abreviatura, orden o relación. La paginación común integra automáticamente ese ancho (se admite wrap real, nunca texto sobre casilla). En la primera fila física de cada entrada original (`sourceStartOffset==0`), y solo allí, se dibuja casilla PDF **vectorial vacía de 7×7pt**, azul 0.65pt, x=columna+2 y posición respecto de su baseline real. Las habilidades que continúan con más de una línea no duplican casilla. Si el font efectivo no reserva 11pt se rechaza, en lugar de solapar el texto o reducirlo artificialmente. Las habilidades genéricas ajenas a `attribute-skill:` no cambian.

No se imprime texto `[ ]`, no se altera estado digital de PROFICIENT/EXPERTISE ni se inventa selección de casillas. Los módulos C207/C212/C213/C214/C215 y familias legadas continúan intactos. `scripts/test_v11_c2_source_navigation_contract.py` exige paridad de ambos escritores, y el **auditor físico existente** `scripts/audit_v11_p3_c2_manuscript.py` mide el rectángulo real junto al texto original y detecta colisiones, no solo strings.

**Estado al publicar:** IMPLEMENTADO, **sin PASS hasta CI exacto y PDFs A/B reales**. Verificar lote real completo, habilidades asociadas a atributos en P1 vs C2, estrés de 72 atributos y uno de 140 habilidades; preservación de IDs/folios y no duplicidad de casilla en fragmentos. No inferir equivalencia estética de C204 ni G4 a partir de este avance.

**Gates y fronteras:** G0 parcial; G1 PASS semántico anterior; G2/G3 parciales; **G4 105/105 NOT_VERIFIED, active_candidate=null, BLOCKED**; G5/G6 pendientes. C211 BLOCKED 3/3 intentos normales, RCR-1 estructural 0/2. C212–C215 conservan PASS locales previos. Falta QA PDF Android en dispositivo instalado, revisión técnica y visual independiente del propietario, y aprobación de merge. No cambiar la matriz ni el contrato.

**Siguiente trabajo previsto tras la prueba:** completar el bloque de habilidades ligadas C2: comprobar presentación medida del valor numérico y relación con las marcas P/E frente al golden, sin inventar datos; después continuar conciliación de otros módulos con los ocho PDF golden para G4, sin cierres falsos.
