# V11 G2/G3 — Equipo y Tesoro juntos en C2 cuando realmente caben

**Fecha:** 2026-10-18 · PR #173 DRAFT / NO MERGE · Base `26371528a38b3ed2ea65d390fd91874366e9020f`.

## Problema comprobado sobre PDF real

Los PDF genuinos del candidato anterior ([Scaffold #39181706739](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706739), caso `V11-INTERNAL-PROOF-P2-currency-source-break-A.pdf`) empleaban **dos páginas físicas C2 distintas**: una para cinco pares ordinarios `Mochila / Libro / Incienso / Vestiduras / Ropa`, otra para tres monedas de origen. Ambas muestran abundante espacio NOTAS y estaban generadas desde una misma P2. Contrato aprobado `C201–C205`: extensión compacta sin desperdiciar hojas ni sacrificar pares, IDs, fuentes o reservas manuales.

## Cambio productivo delimitado

- **Android y Desktop V11:** composición conjunta de **dos tablas de dos parejas internas** en UNA página C2 solo cuando ambas tienen fuente real, cantidades legibles en columnas estándar y el compositor medido demuestra que ambas caben en rectángulos no superpuestos. La altura del primero se deriva de las celdas efectivas, sin escalamiento/ellipsis; segundo usa el resto. Encabezados/cartelas independientes y única referencia bidireccional P2→C2 a esa página física. NO concatenar orígenes/ID de Equipo y Tesoro.
- Si una tabla no cabe, si hay cantidades amplias o módulos muy densos, se mantiene la **ruta completa existente de páginas C2 separadas**. No se tocan los registros P2, los dibujos/saltos de otras familias, el ZIP golden, los demás C2 ni los datos persistidos.
- Evitar escribir NOTAS de la tabla superior sobre la inferior; la tabla inferior conserva cuadrícula NOTAS en espacio útil. No crear hojas sólo de relleno.
- **Prueba funcional real A/B:** se amplía el test de monedas custom CRLF/TAB ya existente, sin duplicarlo. Exige ambos encabezados y sus datos en una sola página C2, 0/37/41/73 sin duplicados, referencia P2 correcta, NOTAS manuscrito y una página física menos (8→7). La regresión ya existente de Equipo/Tesoro densos sigue exigiendo páginas separadas y navegaciones correctas.

## Gate

**Al publicar código: CI y PDF A/B del commit PENDIENTES.** No inventar PASS. G0 QA histórico PARCIAL; G1 semántico PASS; G2/G3 parciales; **G4 BLOCKED 105 reglas NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes, RCR-1=0/2. M50800 y V11-QA-001/002 intactos. PR #173 DRAFT sin merge. Cualquier fallo real se investiga acotadamente, nunca degradando el contrato ni cambiando el test para ocultarlo.

**Siguiente bloque:** comprobar PDF reales, geometría del área compacta y no regresión; después tratar otros grupos de C2 con evidencia primaria, no ciclos de auditorías repetidas.
