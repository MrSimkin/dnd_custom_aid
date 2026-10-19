# V11 G2/G3 — C2 compacto con Equipo ordinario y Equipo Especial

Fecha 2026-10-18, PR #173 **DRAFT / NO MERGE**. Parent HEAD: `8cefefc19b101eeac9b0ecd94e8f579568261dc4`.

## Evidencia real motivadora, no hipotética

- [PDF App Desktop Ilyra-B #12157992511](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39185287349/artifacts/12157992511) (12 folios): página física 6 = C2 Equipo para **un** registro `Suministros de calígrafo 1`; página 7 = C2 Equipo Especial con **un** `Spellbook` (Mochila, descripción auténtica, ACTIVO), ambas con gran espacio NOTAS. Verificación de PDF fuente original antes del cambio mediante PyMuPDF.
- Contrato `CONTRATO_VISUAL_APROBADO.md` § C2 / reglas **C201–C205, C207–C208**: compartir módulos heterogéneos en las columnas/formatos requeridos cuando caben, sin más páginas solo por un título; Equipo ordinario conserva dos pares; Equipo Especial conserva ancho completo, ubicación/nombre/descripción/activo; cuadrícula NOTES solo donde quepa. No convertir las reglas G4 en PASS.

## Cambio Kotlin efectivo (Android + Desktop), acotado y reversible

- Se conserva la ruta existente de C2 Equipo/Tesoro y de Equipo Especial. Cuando hay **una** tabla ordinaria remanente (sin Tesoro) y **un** Equipo Especial auténtico y corto, se reserva temporalmente la tabla ordinaria. Solo se comparte papel cuando el compositor medido acredita una página para cada uno, el monto usa columna estándar, los límites no se tocan, hay banda libre de 22pt y la fila especial completa cabe en el resto.
- Se pintan **dos regiones sin solape** en una página Carta: Equipo con sus dos columnas objeto/cantidad en la franja superior; Equipo Especial con sus cuatro columnas y estado ACTIVO en toda la anchura debajo. Una sola línea de retorno auténtico a P2, NOTAS manuscritas al final del espacio libre. Se elimina la página independiente SOLO si se cumplen medidas. Contenido abundante/multipágina conserva exactamente el método previo; ningún string/ID/cantidad/estado original se modifica ni se fabrican valores.
- Se amplía el test Desktop de Ilyra existente sin debilitar verificaciones sustantivas y se añade una única prueba real A/B de módulos, columnas, Spellbook/fuente, página compartida y retorno a P2, más NOTAS final.
- Solo se modifica V11, ninguna plantilla legacy, golden, contrato, matriz ni registro QA histórico. La primera publicación no implica CI PASS.

## Estado de CI y gate

**CI al publicar: PENDIENTE.** No reclamar salida nueva, ahorro de páginas, Android runtime ni paridad visual integral antes de examinar la ejecución exacta, el PDF generado y regresiones de otros casos.

G0 QA histórico parcial, G1 semántico PASS, G2/G3 parciales; **G4 BLOCKED 105 NOT_VERIFIED, active_candidate=null**; G5/G6 pendientes. M50800 y V11-QA-001/002 intactos. RCR-1=0/2. PR #173 sin merge.

Siguiente: verificar CI, PDF real A/B y reversibilidad en datos densos, documentar hashes/cambios exactos; continuar módulos C2/P3 solo sobre defectos reales nuevos.
