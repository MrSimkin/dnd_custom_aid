# V11 — P1 espacios de conjuro con contadores extensos: continuación C2 sin pérdida

**Fecha:** 2026-10-17. **Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff`; **PR #173** abierta/DRAFT, **no fusionar**. `main` permanece en `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Hallazgo concreto

Las nueve celdas P1 de espacios (niveles 1–9) reservan ~22 pt para «total» y ~29 pt para «gastados». Antes, el renderer dibujaba sin medir los contadores `Int` del origen en esas posiciones fijas; cantidades digitalmente válidas de más dígitos podían invadir la columna vecina y resultar ilegibles. Esta brecha es una **prueba técnica extrema nueva**, no una observación histórica atribuida al propietario.

## Implementación acotada

- Desktop renderer: commit `d8ee9bac115b348ca0eb017cc3db11d6a7a1b533`.
- Android renderer: commit `c11051ce0cbaa986cd1d202fbd02b0b4de5876ed`.
- Prueba PDF real Desktop A/B: commit `5bc9efcd02af70f8267eea914d6732bcd0b8ab92`, `g3OversizedPersistedSpellSlotCountersUseP1C2WithoutClippingOrInventingValues`.
- Se miden con la fuente real los valores total/gastados. Si cualquiera excede su ancho fijo, P1 conserva el nivel y muestra `VER C2` en vez de superponer números; C2 conserva **ambas cifras completas** en un único registro `spellcasting:slots:<nivel>` del flujo medido. Los niveles con cifras ordinarias permanecen en P1 sin cambio. Los niveles épicos ya documentados y los cuatro formatos anteriores no se modifican.
- Fixture discriminante: copia del backup auténtico Aldren con **solo** `spellSlots` cambiado artificialmente a nivel 1 total `2147483647`, gastados `2147483646` y nivel 2 total `3`, gastados `1`. No corresponde a cifras históricas reales del personaje. El test exige que los dos números extremos aparezcan una vez en C2, no aparezcan en P1, el segundo nivel permanezca normal y NOTAS siga siendo la última hoja.

## Evidencia real comprobada

- **Scaffold #39072946515** ([GitHub Actions](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946515)): **SUCCESS** en el commit exacto `5bc9efcd02af70f8267eea914d6732bcd0b8ab92`. Incluye suite Kotlin Desktop/Shared, compilación Android, backend y hosted DB. No significa que la app Android se haya instalado.
- **QA traceability #39072946529** ([GitHub Actions](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946529)): **SUCCESS** sobre ese mismo SHA, control de integridad del ledger, no aceptación del propietario.
- **Artefacto real Desktop App #12115184410** ([30 PDF generados en CI](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946515/artifacts/12115184410)): ZIP SHA-256 `3d02f402fdd2b9f28fe4197ec058a5f989b181a5ee03cdf5f112496a96556f2e`, CRC PASS; 30 PDFs, 28 preexistentes + 2 nuevos A/B. ZIP obtenido del workflow que ejecutó el test de exportación real, no del prototipo ReportLab.
- **Regresión independiente:** comparación física de 28/28 PDFs preexistentes contra artefacto #12115157309 (run Scaffold #39071100853). Los 28 conservaron conteo y tamaño de páginas Carta, palabras y sus coordenadas y trazados vectoriales examinados. **No** son necesariamente idénticos byte a byte por metadatos, y no se afirma eso.
- **PDF variante A:** `V11-INTERNAL-PROOF-P1-long-spell-slots-A.pdf`, SHA-256 `bd3f1d4560311652cafee61b343b50b0816b7d5611e84b904c97b51b14835e73`, 7 páginas Carta.
- **PDF variante B:** `V11-INTERNAL-PROOF-P1-long-spell-slots-B.pdf`, SHA-256 `91da054735af5baf709977dec5ffd856833b8c57a79dd3e1d7707313e79ac6f9`, 7 páginas Carta.
- **Inspección independiente de los PDF genuinos A/B:** P1 tiene exactamente un `VER C2` y conserva el nivel normal `N2`; las dos cantidades largas no aparecen en P1; C2 en página física 4 presenta `ESPACIOS REALES NIVEL 1` con `2147483647` y `2147483646` **una sola vez cada una**, sin duplicados; última página 7 dedicada a `NOTAS`. Se rasterizaron y examinaron P1 y C2 para ambas variantes, sin colisiones visibles en el caso probado.

## Resultado de gate y restricciones

**G2/G3 PASS LOCAL exclusivamente para esta preservación de contadores grandes P1→C2**, probado con PDF verdadero Desktop A/B y Android compilado. No es paridad visual de Android instalado ni revisión del propietario, no sustituye el catálogo de condiciones restantes. **Matriz global conserva 105 reglas NOT_VERIFIED y 12 checks de etapa PASS históricos** sin nuevos PASS automáticos. **G4 BLOCKED; G5/G6 pendientes**. QA V11-QA-001/002 y las 32 observaciones históricas M50800 siguen abiertas; recuperación RCR-1 no activada; `active_candidate=null`. PR #173 sigue DRAFT y sin fusionar. Sin modificación de `main`, maqueta golden ni renderers legacy.

## Próxima acción segura

Resolver otra brecha funcional **concreta, distinta y exigida por una regla aún pendiente**, usando fuente real, prueba discriminante y salida PDF Android/Desktop; no reabrir este slice aprobado localmente sin defecto demostrado. Después recopilar la auditoría independiente de **todas** las reglas de G4, confrontar decisiones históricas y ocho golden, y probar las cuatro familias anteriores/Mara y el mismo candidato exacto. Nunca enviar un APK al propietario ni fusionar la PR antes de superar los controles restantes.
