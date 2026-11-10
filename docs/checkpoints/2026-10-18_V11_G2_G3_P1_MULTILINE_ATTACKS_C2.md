# V11 G2/G3 — Ataques P1 con saltos reales a C2

**Fecha:** 2026-10-18. **Rama/autoridad:** `docs/pc-sheet-v11-approved-implementation-handoff` y PR #173 OPEN/DRAFT; `main` HEAD `0c009f10d5e66880d68fa3732491c6fc3e172f22`. **Base verificada:** `5f3e94c9daf5ec8d62842e0773a67e9b7542648d`, último Scaffold #39158679499 SUCCESS. El checkpoint anterior retrato-ausente/NOTAS tiene prueba independiente y NO fue reabierto.

## Brecha funcional delimitada

El P1 aprobado imprime ataques en **tres columnas y una única fila física por ataque**, sin perder nombres, alcance ni daño. El filtro en AMBOS PDFBox V11 medía `p1AttackLabel(entry)` y `entry.damageEffect` por anchura, pero aceptaba entradas con **CR, LF o TAB** sin comprobar que fuesen una sola línea física. Esto arriesgaba texto fuera de celda o fallo de la impresión nativa. La ruta de ataques que no caben a C2 ya conserva el `UUID`, nombre/alcance/bono/daño/notas; no requiere crear otro módulo, rediseñar ni recortar datos.

**Cambio mínimo:** descartar como candidato de fila P1 cualquier ataque cuyo nombre, alcance o daño contenga CR/LF/TAB; la salida impresa conserva íntegra la entrada en la continuidad **C2 existente**, con texto medido y misma identidad. Ataques cortos sin caracteres de control siguen optando a las seis filas P1 en orden fuente. Cambian exclusivamente los dos renderizadores V11 Android/Desktop y una prueba de PDF Desktop. No se modifican el mapeo real, datos, otros cuatro exportadores, golden, composición de P2/P3/P4 o `main`.

## Prueba y evidencia exigida

Nuevo `g3P1AttacksWithPersistedLineBreaksAndTabsAreRoutedLosslesslyToRealC2AB` parte del backup verdadero Aldren y cambia **solo una copia del snapshot en memoria** con cuatro ataques, IDs persistidos únicos: (1) nombre LF, (2) alcance CRLF, (3) daño TAB, (4) ataque corto normal. Exporta A/B mediante el servicio Desktop App real. Verifica ocho marcadores semánticos exactamente UNA vez en la salida, ausencia de campos multilineales en P1, presencia exacta en C2, ataque corto solo P1, fuente inmutable y última NOTAS sin texto digital; conserva dos PDF genuinos adicionales en artifact CI. No generar PDF por ReportLab.

**CI del nuevo commit: pendiente de corroborar.** No publicar PASS en ausencia de resultados CI y PDF exactos. Validaciones existentes de 48 PDF, NOTAS final y retrato se mantienen sin reejecutar manualmente, pero forman parte del Scaffold automático.

## Continuidad / gates

Se mantiene G0 parcial (QA físico original no custodiado), G1 mapeo semántico previo, avances parciales G2/G3, **105 reglas globales NOT_VERIFIED**, G4 **BLOCKED**, G5/G6 pendientes y `active_candidate=null`. Este trabajo solo permite PASS local de P1/C2 con prueba real, nunca aprobación técnica/visual del propietario. No cambiar matriz G4 ni registros owner M50800/V11-QA, no invocar RCR-1 salvo causa repetida verificable, no merge.

**Siguiente bloque:** contrastar brechas de **datos y semántica** P1/P2/C2 contra los ocho golden, D-0076 y PDF nativos. Priorizar otra obligación material no probada; detenerse y documentar condiciones si exige Android instalado o evidencia propietaria antes de G4.

## Resultado exacto y continuidad comprobada (2026-10-18)

**PASS LOCAL G2/G3 para ataques multilineales P1/C2** en el candidato de código `fdcefcb5ff3c1c467fb848ffce9f1c3bc08ca720`. [Scaffold #39160188797](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188797) **SUCCESS**: jobs Kotlin (Shared/Desktop + Android DEBUG), backend y base de datos completados correctamente. [QA traceability #39160188801](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188801) **SUCCESS documental**, NO aceptación de propietario.

- [Artefacto ZIP #12148523038 — 50 PDF App Desktop reales](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188797/artifacts/12148523038): **SHA-256** `fcc2be64863a5b52e0653610dec5616226ecbe29f0e863f59ab9c25e8b32ea3c`, CRC PASS; 48 casos anteriores + dos nuevos `V11-INTERNAL-PROOF-P1-attack-multiline-A.pdf` y `-B.pdf`. Ambos nuevos **7 páginas Carta**, C2 física **página 4**.
- Validación PyMuPDF externa A/B: `QAATTACKNAMESTART`, `QAATTACKNAMEEND`, `QAATTACKRANGE`, `QAALCANCEINICIO`, `QAALCANCEFINAL`, `QAATTACKDAMAGE`, `QADANOINICIO`, `QADANOFINAL` están **exactamente una vez por variante, en C2 y no en P1**; `QASAFEP1ROW` queda en P1, no aparece en C2; última hoja `NOTAS` sin estos datos. Inspección visual localizada de C2 A confirma texto legible y cuadrículas manuscritas sin mezclar contenido digital.
- Comparación independiente frente a ZIP del HEAD funcional anterior `5436e146`: **48/48 PDF previos con idéntico número de páginas, mismas palabras y cajas PDF y misma cantidad de trazados vectoriales por página**, sin caso eliminado; no implica identidad binaria u operadores gráficos completos.
- [Auditoría ZIP #12148458506](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188797/artifacts/12148458506): SHA-256 `7fdbaf264f88c1072948b7bea0bf0a78413eaf38ca85b641aa5b21def12e6bb3`, CRC PASS; `v11-physical-envelope-audit.json` **50 PDF/480 páginas/0 alertas**; `v11-final-notes-golden.json` **50/50 PASS**; `v11-portrait-placeholder-golden.json` **50/50 PASS**.

**Límites:** confirma PDF genuino Desktop A/B y código equivalente compilado Android, NO exportación desde teléfono instalado, paridad visual completa del golden, QA del propietario, ni cero defectos posibles fuera del caso. **105 reglas G4 globales NOT_VERIFIED, `active_candidate=null`, G4 BLOCKED; G5/G6 pendientes**. Sin modificación de la matriz original, V11-QA-001/002, 32 M50800, golden o `main`; PR #173 DRAFT no fusionada. No RCR-1.

**Siguiente paquete funcional:** otro defecto concreto no cubierto de integridad fuente→PDF en P1/P2/C2; dar prioridad a datos impresos y continuaciones físicas por encima de estética aislada. Contrastar su fuente de autoridad D-0076/V11 y preparar prueba sobre PDF real; no repetir los 50 casos previos fuera de la regresión automática normal.
