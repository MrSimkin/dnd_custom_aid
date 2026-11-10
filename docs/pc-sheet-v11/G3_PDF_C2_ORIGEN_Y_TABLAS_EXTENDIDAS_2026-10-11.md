# G3 — C2: procedencia P1/P2, tablas completas, NOTAS contractual (2026-10-11)

**Regla owner:** contrato visual A/B ya APROBADO. No pedir más decisiones de diseño ni inspección manual visual antes del QA FORMAL. Implementar los PDFs reales de Android/Desktop, no planificadores aislados. PR #173 DRAFT en `docs/pc-sheet-v11-approved-implementation-handoff`, sin merge. Mantener cuatro familias legacy intactas.

## Estado anterior verificado

- Scaffold `38196267219` SUCCESS: tests de 49/49 referencias a páginas físicas reales del Libro (Ilyra 25, Mara 24).
- Scaffold `38196359251` SUCCESS: checkpoint precedente y renderizadores. Fuente de entrada App real, variantes A/B y salida PDFBox probadas.

## Cambios directos de los renderizadores PDFBox

1. `62891e2a528a3a29b39eb556753fe2251042afc0`: referencias bidireccionales C2 se derivan del módulo. Habilidades/acciones/recursos vienen de P1 y se vinculan a P1; Equipo/Tesoro/Rasgos/Equipo especial vienen de P2 y se vinculan a P2. P1 y P2 apuntan sólo a su primera C2 real, no a cualquier C2. Test PDF real compara número físico exacto en los dos sentidos (Mara). Scaffold `38196858188` SUCCESS.
2. `8ed942cfe83213ec433b04551341474a622f3e5f`: desaparece el arrastre de los pequeños paneles P2 a C2. Equipo ordinario y Tesoro reconstituyen únicamente el sufijo UTF-16 no impreso de cada nombre/denominación conservando fuente, orden, identidad y cantidades. Dos parejas objeto/cantidad ocupan el área completa de C2 (528×626pt); las cantidades que ya se imprimieron en P2 no se repiten en C2. Nuevas pruebas sintéticas de 100 objetos con nombres largos. **CI `38197107454` FALLÓ únicamente por sintaxis rota en el archivo de test**, no por fallo conocido de PDF runtime; se corrigió en el commit posterior.
3. `ba5e211ef01cb1a646424a255f564c8e2b5aa9ed`: añade 45 monedas extraordinarias al caso Aldren para forzar C2 de Tesoro; verifica nombres, valores cero, cantidades una sola vez y NOTAS finales. **CI `38197231114` FALLÓ por el mismo test mal formado.**
4. `0f38168f8378008d33c843cc9b03db12aab92925`: reparación delimitada de cadena literal cortada/llaves en test, confirmada leyendo logs Kotlin; título único `NOTAS` sobre la última página física manuscrita en ambos PDFs Android/Desktop en lugar de texto añadido `NOTAS - escritura manual`. Actualiza nueve aserciones PDF de la prueba de integración para leer el título aislado en su propia línea. **Último Scaffold `38197464921` IN_PROGRESS** a la fecha de este registro; QA owner traceability `38197464915` SUCCESS. Solo ese último run decide la validez del bloque nuevo.

## Próximos pasos (sin intervención manual)

Verificar `38197464921` hasta conclusión. Si falla, inspeccionar compilación/test y corregir desde evidencia, no inventar PASS. Si pasa, descargar artefacto `v11-internal-real-app-pdf-proofs` y ejecutar inspección automatizada no visual de longitud/paginación/secuencias C2; preservar la salida de Mara/Aldren/Ilyra. Continuar módulos físicos contractuales y preparar eventual candidato QA con versión y procedencia, sin fusionar PR #173 ni solicitar aprobación intermedia. G0 histórico PARCIAL/OPEN, G1 PASS, G2 todavía parcial, G3 PDF real en implementación/CI, G4–G6 pendientes, RCR-1 0/2.
