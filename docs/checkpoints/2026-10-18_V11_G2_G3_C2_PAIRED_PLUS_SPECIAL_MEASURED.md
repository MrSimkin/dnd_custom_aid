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

## Verificación del cambio: PASS LOCAL — evidencia exacta

- **Commit productivo auditado:** [`9168ba8dc3408f0436ef3e5aac7be39a1b58713e`](https://github.com/MrSimkin/dnd_custom_aid/commit/9168ba8dc3408f0436ef3e5aac7be39a1b58713e). [Scaffold #39189299120](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39189299120) **SUCCESS** (Kotlin Shared+Desktop tests / Android Debug compile, backend, hosted-db). [QA registry #39189299148](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39189299148) **SUCCESS documental** (no QA propietario).
- [Artefacto PDF Desktop real #12158769351](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39189299120/artifacts/12158769351): ZIP SHA-256 `a6e48904925195e6b4246f33f733096e5060507a7d5718a64c47b578df444dde` (ZIP CRC PASS), **60 PDF / 549 folios**.
- **Ilyra-B fuente auténtica:** `V11-INTERNAL-PROOF-Ilyra-B.pdf`: **12→11 páginas** respecto del artefacto anterior [#12157992511](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39185287349/artifacts/12157992511), nuevo SHA `1c98f5a328deaefed3a30485ca654f862508e8840f45f3054cf12ee6c89207f8`. Página **6** compartida con `EQUIPO / CANTIDAD`, `Suministros de calígrafo = 1`, `EQUIPO ESPECIAL` de cuatro columnas: `UBICACIÓN / NOMBRE / DESCRIPCIÓN / ACTIVO`, `Mochila / Spellbook`, descripción auténtica con `Evocation Savant`, reserva NOTAS y retorno `VIENE DE P2 PÁG. 2`. No hay página especial redundante.
- **Dos pruebas adicionales desde el servicio Desktop real, sin libro opcional:** `V11-INTERNAL-PROOF-C2-ordinary-plus-special-A.pdf` (8 páginas, SHA `08da32050ad9416241954090854dcbf8b0aae3cd51cc1ed7646d2c4561b79f21`) y variante B (8 páginas, SHA `1f3455130402487403074ca33419ceae5592cd0f05acebfc31f8bc4ab93f167a`). Ambas generan idénticos encabezados/campos auténticos y NOTAS final.
- **No regresión de conjunto:** comparados 58 PDF del candidato anterior contra 58 homónimos del nuevo. **41/58** preservan exactamente páginas, secuencia de palabras con coordenadas, número de dibujos e imágenes. Los **17 restantes** pierden **una** página, todos escenarios que reutilizan a Ilyra con el mismo par de módulos y deben compactarse; no hay variación del número de páginas en otras familias. Se añadieron dos casos de prueba A/B, que explican el total final **549** = 550 - 17 + 16.
- Inspección visual raster de folio 6 (Ilyra-B y caso A): cartela superior Equipo, tabla especial a ancho completo por debajo, renglones no solapados y una cuadrícula NOTAS completa hasta el borde útil. **NO** se declara semejanza integral con los ocho golden ni aprobación visual owner.
- [Auditorías físicas #12158889021](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39189299120/artifacts/12158889021): `V11_PHYSICAL_AUDIT_ERRORS=0`, `V11_GOLDEN_FINAL_NOTES=60/60`, `V11_GOLDEN_PORTRAIT_FALLBACK=60/60`, `V11_GOLDEN_DOUBLE_FRAME=549/549` y `V11_LOGO_VISIBLE=549/549; golden=59 pages`. Estos controles son parciales de composición, no G4 PASS.

**Alcance concedido:** PASS LOCAL del empaquetado medido de Equipo+Especial en salida Desktop real y compilación Android equivalente. **No se ejecutó el APK Android instalado**, no hay aceptación técnica ni visual del propietario, no se actualiza ninguna regla de la matriz global sin oráculo G4.

**Gates inalterados:** G0 QA histórica Mara PARCIAL; G1 semántico PASS; G2/G3 parciales; **G4 BLOCKED, 105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes. Evidencias M50800 y observaciones V11-QA-001/002 siguen íntegramente abiertas según autoridad; RCR-1=0/2. PR #173 DRAFT/no merge, `main` intacto.

**Siguiente bloque razonable:** avanzar sobre incumplimientos de mayor impacto en C2/P3/NOTAS digitales observados en PDFs reales, o pasar a conciliación G4 del contrato por módulos (no dar por aprobadas pruebas locales), antes de pedir QA owner. No repetir el trabajo de los dos módulos compactos salvo evidencia nueva.
