# C226 — Casillas marcables reales de cuatro y cinco espacios en P1

Fecha 2026-10-22, PR #173 DRAFT/NO MERGE. Estado al iniciar: **PASS LOCAL FÍSICO VERIFICADO, no G4/owner**.

## Brecha demostrable y frontera

Contrato V11, D-0076 §§3.11–3.12 y matriz P115–P116: el panel fijo P1 debe representar los espacios de conjuro realmente registrados y ofrecer casillas físicas utilizables. El código Android/Desktop anterior dibujaba casillas de 7pt **solo para `totalSlots in 1..3`**, pese a que el backup persistido real de Ilyra tiene **4** espacios de nivel 1 (lo imprime como número, pero sin una sola marca). El dibujo aprobado ya tiene celdas de **85pt** de ancho; cuatro o cinco marcas de 7pt separadas 12pt caben x+19..x+74pt, dejando margen final ≥11pt antes de la siguiente celda. Los controles 1–3 quedan idénticos y valores 6+ mantienen contadores auténticos, sin casillas falsas ni reducción.

## Cambio acotado y prueba

- Android y Desktop V11, exclusivamente `drawP1Spellcasting`: ampliar la condición de `1..3` a `1..5`, con una guarda aritmética de ancho `19+(n-1)*12+7 <= 78` para que no desborde; no mover números, cabecera, datos fuente, P4 ni otras familias.
- Integración de dos PDFs reales Desktop App **Ilyra A/B**: backup con nivel 1 **4** original y un ajuste explícito del nivel 2 a **5 / 3 gastados**. Inspecciona el contenido vectorial de la primera página para confirmar **exactamente cuatro** cuadrados de 7pt nivel 1 y **cinco** de nivel 2; prohíbe quinto/sexto cuadrado ficticio. Conservar textos ESPACIOS/GASTADOS, tamaño Carta y NOTAS final.
- CI requerido: Scaffold (Kotlin Shared/Android/Desktop), QA, auditor físico y comparación por hash de los **86 PDF C225 / 793p** contra el nuevo lote 88 PDF. Cambios admitidos: SOLO incorporación de cuadros de P1 para registros de 4/5 espacios, más dos PDF nuevos A/B; comparar geometría/valores/paginación, inspección raster. **No atribuir PASS por el test agregado sin CI real.**

## Límites

Para 6+ espacios no se diseña una nueva representación, se conservan números auténticos; su seguimiento manual completo continúa pendiente de decisión/composición de ancho, fuera de C226. No remover ni alterar G4 ni aprobación owner. C211 3/3 normales agotados, 0/2 estructural; G0 parcial, G1 semántico PASS, G2/G3 parciales, G4 BLOCKED 105/105 NOT_VERIFIED active_candidate=null, G5/G6 pendientes; Android instalado/original Mara owner no verificados.

**Última base verificada:** C225 commit `097bfeb941524c66c586112b90a41b912f116411`; CI #39815515425/#39815515423 SUCCESS, comparación física del checkpoint C225. **Próximo paso:** CI exacto de C226 y cotejo físico de PDFs; registrar PASS LOCAL o fallo/freno RCR-1 preciso, no fusionar la PR.

## Cierre físico C226: PASS LOCAL, NO G4

Código [e104cec](https://github.com/MrSimkin/dnd_custom_aid/commit/e104cec3976b0fe1ad6a9e868fc758e8ed9f9d6e). [Scaffold #39824298970](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39824298970) y [QA traceability #39824298962](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39824298962) ambos **SUCCESS** del mismo SHA; Kotlin/Android/backend/DB y oráculo PDF independientes. Sin reparaciones ni intentos de RCR-1 consumidos.

[Artefacto de PDF genuinos #12446397671](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39824298970/artifacts/12446397671) ZIP SHA256 `fc4787d05d2a01f348ddf583c33e3ec1fa013be3eff1c3226d6776b9ae182de2`: **88 PDF / 809 páginas**. Baseline previo C225: [86 PDF/793p #12442961337](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39815515425/artifacts/12442961337), ZIP `a2246c975ae3e97dd95016133909e627d3fcb40aeff7519431f8f152bab8660f`. Comparador externo PyMuPDF 1.26.7: **86/86** misma paginación, 742/793 páginas palabras+coordenadas y vectores iguales, **otras 51 páginas SOLO P1** cambian exclusivamente por adición de **204 cuadrados verdaderos de 7pt** y 77 «X» reales de gastos; comparación de dibujos ignorando `seqno` confirma que se preservan todos los anteriores y que los nuevos están dentro del panel mágico. Sin texto previo borrado ni otras palabras agregadas, imágenes referenciadas iguales en 793/793 páginas. Las 51 páginas nuevas de marca no cambian contenido ni otras secciones.

**Dos PDF Ilyra genuinos nuevos (A/B), ocho páginas cada uno:** nivel 1 con total 4/gastados 1 y **cuatro marcas** exactas, nivel 2 modificado a total 5/gastados 3 con **cinco marcas** exactas. Nivel 3 mantiene dos, sin casillas quinta ficticia en nivel 1 ni sexta en nivel 2; coordenadas vectoriales 7×7pt examinadas. A SHA `b9eda81e231563a69345a8ff4e74a8fa0be6b4ea4059ceae27f457ac1c8a66a2`; B SHA `0d9235c4b2223583e24a734e64e24f3f106e0d45d08d9cac3f198d47b5c4ac30`. Inspección raster de filas P1 legible y sin colisiones detectadas; Carta 612×792pt, última NOTAS presente.

[Auditor físico independiente #12447005202](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39824298970/artifacts/12447005202), SHA ZIP `9691c9967c65c99cbc58658dcd5adfa60a63b7aa53f0a5977e91bd5fbf940b81`: **88 PDF, 809 páginas, 0 errores de perímetro**, C207 0 omisiones. [Acta JSON del cotejo](../pc-sheet-v11/qa/evidence/2026-10-22_V11_C226_P1_FOUR_FIVE_SLOT_BOXES_REAL_APP_PDFS.json).

**Límites estrictos:** C226 resuelve casillas para conteos reales 4–5 sin rediseño, NO resuelve escritura completa de totales **6+**, no demuestra equivalencia global al golden, tampoco QA Android instalado ni aceptación owner. No marcar P115/P116 ni cualquier regla de G4 PASS global. G0 PARCIAL / G1 semántico PASS histórico / G2-G3 PARCIALES / G4 BLOCKED **105/105 NOT_VERIFIED active_candidate=null** / G5-G6 pendientes. C211 BLOCKED 3/3 normal y 0/2 estructural, 32 M50800 y dos V11 QA conservadas. PR #173 DRAFT, NO MERGE.

**Siguiente paso:** elegir otra brecha G2/G3 independiente, preferentemente solución verificable de controles manuscritos 6+ sin alterar contrato/estética o conciliación de módulo pendiente contra golden/D-0076; requerir nuevas pruebas y respeto de límites anti-bucle, sin repetir C225/C226.
