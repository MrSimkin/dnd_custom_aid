# C226 — Casillas marcables reales de cuatro y cinco espacios en P1

Fecha 2026-10-22, PR #173 DRAFT/NO MERGE. Estado al iniciar: **IN_PROGRESS, sin PASS de código ni PDF**.

## Brecha demostrable y frontera

Contrato V11, D-0076 §§3.11–3.12 y matriz P115–P116: el panel fijo P1 debe representar los espacios de conjuro realmente registrados y ofrecer casillas físicas utilizables. El código Android/Desktop anterior dibujaba casillas de 7pt **solo para `totalSlots in 1..3`**, pese a que el backup persistido real de Ilyra tiene **4** espacios de nivel 1 (lo imprime como número, pero sin una sola marca). El dibujo aprobado ya tiene celdas de **85pt** de ancho; cuatro o cinco marcas de 7pt separadas 12pt caben x+19..x+74pt, dejando margen final ≥11pt antes de la siguiente celda. Los controles 1–3 quedan idénticos y valores 6+ mantienen contadores auténticos, sin casillas falsas ni reducción.

## Cambio acotado y prueba

- Android y Desktop V11, exclusivamente `drawP1Spellcasting`: ampliar la condición de `1..3` a `1..5`, con una guarda aritmética de ancho `19+(n-1)*12+7 <= 78` para que no desborde; no mover números, cabecera, datos fuente, P4 ni otras familias.
- Integración de dos PDFs reales Desktop App **Ilyra A/B**: backup con nivel 1 **4** original y un ajuste explícito del nivel 2 a **5 / 3 gastados**. Inspecciona el contenido vectorial de la primera página para confirmar **exactamente cuatro** cuadrados de 7pt nivel 1 y **cinco** de nivel 2; prohíbe quinto/sexto cuadrado ficticio. Conservar textos ESPACIOS/GASTADOS, tamaño Carta y NOTAS final.
- CI requerido: Scaffold (Kotlin Shared/Android/Desktop), QA, auditor físico y comparación por hash de los **86 PDF C225 / 793p** contra el nuevo lote 88 PDF. Cambios admitidos: SOLO incorporación de cuadros de P1 para registros de 4/5 espacios, más dos PDF nuevos A/B; comparar geometría/valores/paginación, inspección raster. **No atribuir PASS por el test agregado sin CI real.**

## Límites

Para 6+ espacios no se diseña una nueva representación, se conservan números auténticos; su seguimiento manual completo continúa pendiente de decisión/composición de ancho, fuera de C226. No remover ni alterar G4 ni aprobación owner. C211 3/3 normales agotados, 0/2 estructural; G0 parcial, G1 semántico PASS, G2/G3 parciales, G4 BLOCKED 105/105 NOT_VERIFIED active_candidate=null, G5/G6 pendientes; Android instalado/original Mara owner no verificados.

**Última base verificada:** C225 commit `097bfeb941524c66c586112b90a41b912f116411`; CI #39815515425/#39815515423 SUCCESS, comparación física del checkpoint C225. **Próximo paso:** CI exacto de C226 y cotejo físico de PDFs; registrar PASS LOCAL o fallo/freno RCR-1 preciso, no fusionar la PR.
