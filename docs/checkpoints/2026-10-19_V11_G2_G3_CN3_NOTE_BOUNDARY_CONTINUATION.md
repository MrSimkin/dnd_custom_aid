# V11 G2/G3 — C-N3 notas digitales con anclajes y continuidad de identidad

**Fecha:** 2026-10-19 UTC / 2026-10-18 Chile. Base `19dc812d9647924a8ac9d4ab32d4ac9462a3a39d`, rama exclusiva PR #173 DRAFT, NO MERGE.

## Evidencia primaria y contrato

- Se abrió el PDF realmente generado por Desktop en Scaffold [#39189299120](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39189299120), `V11-INTERNAL-PROOF-Mara-A.pdf`, folio **8**. El primer párrafo de nota y los títulos 1–9 están separados por solo ~1.6pt de aire (títulos sucesivos y texto contiguo), sin cartela ni marca de límite; en raster la lectura de fichas se mezcla.
- El contrato V11 exige C-N3 exclusivamente digital, registro real completo, NOTAS manuscrita final independiente. La observación histórica original sobre notas continuadas de Mara 50800 existe **pero ese APK legacy no es V11 ni puede considerarse reparado por este bloque**. Este es un hallazgo propio desde PDF V11 actual, no un cierre de observación original del propietario.

## Cambio productivo Android y Desktop

1. Cada `digital-note:<ID|general>` nuevo en C-N3 recibe una banda física azul niebla muy fina del alto **ya medido** de la primera línea y una regla superior, utilizando `cell.sourceStartOffset == 0`. No hay texto inventado, ninguna modificación de coordenadas de datos, nuevo renglón ni página adicional por esta presentación. El papel de la siguiente nota deja de parecer una continuación del texto anterior.
2. Si el **primer** bloque impreso en una página C-N3 es la continuación de una nota real (`sourceStartOffset > 0`), su cabecera reutiliza el título original persistido `CONTINÚA: <título>`. Para títulos físicamente ilegibles o demasiado largos usa su UUID existente como alternativa, nunca una identidad fabricada ni alteración del texto. Solo C-N3, no afecta P3/C2/Libro ni notas manuscritas.
3. Prueba exacta del servicio Desktop real en variantes **A/B**: PDF original Mara con 9 tarjetas más notas generales, banda física por inicio real, y caso fuente modificado con **una nota larga autenticada** que debe continuar en 2+ páginas con su título y preservar tokens de principio/fin una vez, sin contaminar NOTAS final. Exportadores Android/Desktop reciben cambio equivalente.

## Validación y gates

**CI pendiente al registrar código**. No declarar PASS de nueva separación ni cabecera de continuación hasta comprobar el run exacto, auditores previos y PDFs auténticos; si falla, aplicar reparación mínima con evidencia. No se cambia el ZIP aprobado, QA legacy, ni la matriz de reglas.

**G0 histórico PARCIAL; G1 semántico PASS; G2/G3 parciales; G4 BLOCKED** 105 reglas `NOT_VERIFIED`, `active_candidate=null`; G5/G6 pendientes, RCR-1=0/2, sin owner-APK/QA ni merge.

**Siguiente:** si CI+PDF demuestran la mejora, registrar métricas y no-regresión. Priorizar luego pérdida de datos/ligaduras entre módulos o iniciar conciliación física modular de G4; no seguir creando separadores cosméticos innecesarios.

## Primera ejecución, fallo reproducible de la condición de estrés (sin fallo productivo)

- [Scaffold #39192840524](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39192840524) **FAIL** en 1/160 pruebas (`g3RealMaraC_N3SourceAnchorsAndLongNoteContinuationKeepIdentityAB`): el fixture añadía 180 tokens (~texto legible que aún cabía en un único C-N3), de modo que no ejercitaba una continuación. Error concreto: `Long genuine digital note must span multiple C-N3 physical pages.`. Kotlin compiló; los otros 159 tests pasaron. QA traceability [#39192840617](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39192840617) SUCCESS documental.
- **Reparación limitada del oráculo de prueba, no del producto:** extender el único campo `content` de la nota estresada de 180 a 440 términos numerados, todos de origen claro; el test mantiene su obligación de confirmar 2+ páginas, identidad en cabecera, inicio/fin únicos, fuente no mutada. No reducir las aserciones de continuidad ni inventar PASS.
- Nuevo Scaffold exacto **PENDIENTE**; primer ajuste ordinario, sin activar RCR-1.

## Resultado exacto del bloque — PASS LOCAL C-N3, sin promover G4

**Commit productivo:** [`5bf52d8f176168c28a46f5750369a01dc1855ed7`](https://github.com/MrSimkin/dnd_custom_aid/commit/5bf52d8f176168c28a46f5750369a01dc1855ed7); fixture de estrés corregido en [`53532a79623e9616018ce716abfc4fa1a6955aec`](https://github.com/MrSimkin/dnd_custom_aid/commit/53532a79623e9616018ce716abfc4fa1a6955aec). [Scaffold #39193379904](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193379904) **SUCCESS** (Kotlin shared/Desktop + Android DEBUG, backend, hosted-db). [QA documental #39193380450](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193380450) **SUCCESS**, sin QA de propietario. Se conserva sin ocultar la primera ejecución [#39192840524](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39192840524) FAIL: 180 tokens cabían en una página; la nueva prueba mantiene obligación de 2+ páginas con 440 tokens.

**PDF físicos App Desktop:** [artefacto #12160714181](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193379904/artifacts/12160714181). ZIP completo, SHA256 `bdc5b3eb366b9f1a88f8874cf67d68b27fc4d853385aec209174ec7a81c7e24a`, CRC PASS: **64 PDF / 589 páginas**; antes [#12158769351](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39189299120/artifacts/12158769351) 60 PDF / 549 páginas.

- **Mara A/B, C-N3 fuente original:** `V11-INTERNAL-PROOF-CN3-separated-Mara-A.pdf` SHA256 `d64b1f52288b11e29d47497372e29df436706b32a8b8a2db4a266af633176600`, `...B.pdf` SHA256 `de40354a083cf9f5619ff58b1bfa9da4afd513aca6e680a167ebd38787cfb05b`, **9 páginas** cada uno. C-N3 p8: 9 títulos reales + notas generales, exactamente 10 franjas iniciales genuinas, además de regla delimitadora. Se renderizó e inspeccionó el folio A antes/después: bandas azul niebla discretas, apartados legibles, sin perder ninguna palabra ni añadir hojas.
- **Mara estresada A/B:** `V11-INTERNAL-PROOF-CN3-long-note-continuation-A.pdf` SHA256 `ec8ea98850139066718d43cfb052b77a335a2f2a42f0930a1ee177b81e9acbba` y B SHA256 `f1ad613c7c159c5d0300072421ebcba9a87bd9ae9587f7a9d0bdf6e0e3c37477`. **11 páginas** cada uno, con la única nota C-N3 en páginas físicas **8–10**: p9 y p10 cabecera `CONTINÚA: Nota 1 — Hipótesis`, tokens `CN3ACTUALSTART`, `CN3ACTUALFINISH`, `CN3REALCONTENTWORD440` presentes una vez, nota íntegra, NOTAS manuscrita final p11.
- **No regresión independiente** (PyMuPDF): los **60 PDF homónimos**, 549/549 páginas, conservaron exactamente la paginación y las palabras con coordenadas físicas; solo aparecen **408 trazos nuevos en 60 páginas C-N3**, todos limitados a sus rectángulos fuente, **0** alteraciones gráficas fuera de C-N3. El resto de 40 páginas se debe a cuatro PDF A/B nuevos; no se reclama identidad binaria.
- **Auditores existentes** [#12161082482](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193379904/artifacts/12161082482): `V11_PHYSICAL_AUDIT_ERRORS=0`, `V11_GOLDEN_FINAL_NOTES=64/64`, `V11_GOLDEN_PORTRAIT_FALLBACK=64/64`, `V11_GOLDEN_DOUBLE_FRAME=589/589`, `V11_LOGO_VISIBLE=589/589; golden=59 pages`.

**Lectura conservadora:** Solo **PASS LOCAL** de separación de fuentes digitales y continuidad identificable en PDF real **Desktop** y compilación del código Android equivalente. No es revisión visual exhaustiva contra los ocho golden, ni ejecución del APK instalado, ni revalidación de observaciones históricas M50800, ni autorización de owner.

**Gates** sin modificación: G0 QA Mara histórica PARCIAL; G1 semántico PASS, G2/G3 parciales; **G4 BLOCKED 105/105 NOT_VERIFIED, `active_candidate=null`**; G5/G6 pendientes; M50800 y V11-QA-001/002 intactos; RCR-1 0/2, PR #173 DRAFT sin merge, `main` intacto.

**Siguiente trabajo**: pasar de microcorrecciones de composición a conciliación física **por módulos completos** del contrato V11/G4 (incluidos P3, C-N3, C2 y ocho golden), utilizando la evidencia ya generada antes de solicitar promoción a G5. Si aparece discrepancia material real, reparar por lote y verificar, sin marcar global PASS por controles parciales.
