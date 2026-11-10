# G3 — P1 real: Ataques, ranuras y retrato local (2026-10-11)

**Contrato:** `docs/pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md` APROBADO. El propietario ordena desarrollar directamente los PDF de la App. **No pedir revisión visual manual ni aprobación estética antes del QA formal.** La salida sigue marcada PREQA hasta lograr cumplimiento íntegro y pasar los gates.

## Desarrollo real publicado en PR #173 (DRAFT, sin merge)

### Commit `4f1be5c054c0eaa553fabf85f4f44cbf77a67d92`
- Renderizadores reales PDFBox Android/Desktop: bloque **ATAQUE / RANGO | BONO | DAÑO / EFECTO**, bonificador deliberadamente angosto, columnas y reglas dibujadas físicamente. Usa `CharacterCombatEntry` reales, sin cero inventado para bonificador ausente.
- Solo pone en P1 filas cuyos tres textos **caben medidos**; filas que no caben y notas de ataques van íntegramente a C2, manteniendo UUID, daño, rango, bono y notas. Acciones no-ATTACK mantienen su tipo/rango/notas dentro del texto medido, sin duplicarse.
- `LANZAMIENTO DE CONJUROS` siempre físico en P1: niveles 1–9, espacios/totales/gasto desde `CharacterSpellSlot` persistido, marcas de gastados solo para espacios reales. Los huecos sin slots se dejan manuscritos, sin progresión ni números falsos. Niveles épicos 10+ solo si hay datos persistidos, en C2 sin inventar reglamento.
- P1 identity refluye en el área inferior **sin pisar** las áreas de Ataques ni Lanzamiento. Tests real PDF Aldren (Espada larga, Ballesta ligera y conjuros vacío obligatorio), Ilyra (Daga, Fire Bolt, lanzamiento con valores reales).
- Scaffold run `38187118060` IN_PROGRESS al registrar, QA traceability `38187118083` pendiente/consultar.

### Commit `8dbc1a7377f5afb8a2e9176edfd4cf2db6582f19`
- `DesktopPcSheetV11PdfProofRenderer` y `AndroidPcSheetV11PdfProofRenderer`: área de **RETRATO** real dibujada en P1, reservada en una zona independiente del flujo de texto. Embed del `PDImageXObject` desde **bytes locales reales**, con **Recortar/llenar** mediante clip y escala max o **Ajustar imagen entera** mediante min; sin deformación. Con ausencia de bytes, marco y **silueta vectorial**; no inventa personaje.
- `DesktopPcSheetExportService.generateV11Proof` y `AndroidPcSheetExportService.generateV11Proof`: obtienen el `portraitRef` del estado realmente seleccionado, cargan datos exactamente con sus respectivos loaders locales ya usados por familias legacy, pasan disponibilidad a `PcSheetV11SemanticMapper` y bytes al renderer. Sin red ni cambios de persistencia/renderer legacy.
- Test Desktop de extremo a extremo: una imagen PNG QA creada en memoria, retrato disponible en modos CROP_TO_FILL y FIT_ENTIRE_IMAGE, fuente PDF embebida con dimensiones auténticas, caso ausente sin imagen y silueta reservada. Casos Mara/Aldren/Ilyra siguen en la regresión real.
- Scaffold `38187375736` IN_PROGRESS al registrar; QA traceability `38187375751` SUCCESS.

## Estado y siguiente paso obligatorio

- G1 semántico PASS; G2 parcial; G3 prueba de PDF y Save/Share reales ya pasó CI en etapas previas, últimos ataques/slots/retrato **pendientes del CI específico**; G0 QA propietario histórico PARCIAL; G4–G6 no iniciados.
- **Revisar los dos runs exactos y sus logs Kotlin; reparar un fallo de código con evidencia antes de afirmar que estas mejoras pasaron.** La PR no está fusionada.
- Después desarrollar agrupación real de habilidades/atributos en variante A, recursos y C2 mixto/referencias; todo en PDF runtime Kotlin, y pruebas automáticas de contrato. **La única revisión visual humana se hará en QA formal** con APK identificado y artefactos finales, sin pedir nuevas decisiones de diseño.
- Cuatro exportadores anteriores NO modificados; RCR-1 0/2, QA M50800 histórica OPEN.


### Commit `69ac30e8a4649027582955c6b9f9de671e558cd0` — Variante A/B con disposición real

- Variante A: mide en PDFBox los nombres de habilidades a 7pt y sitúa cada habilidad real bajo el recuadro del atributo incorporado al que referencia; muestra la marca `P` (competencia) o `E` (pericia) desde el valor auténtico y el total cuando está resuelto. Sólo ocupa una fila si nombre/marca/total caben en el ancho real, sin clipping.
- Si el texto o la asociación a atributo no permiten ubicarse allí, la habilidad pasa íntegra a una C2, con sufijo `(XXX)` según el atributo real y entrenamiento persistido. Los atributos y habilidades personalizados mantienen asociación por `CharacterAbilityReference`.
- Variante B: conserva listado independiente; etiqueta con `(XXX)` de atributo real al encontrarse fuera del recuadro. No duplica las habilidades A ya colocadas bajo el atributo.
- En P1 la zona manuscrita/identidad se redistribuye para evitar superponerse con los renglones A, el retrato y los Ataques; ambas plataformas ejecutan los mismos cortes y geometría. Tests PDF Aldren A para ATHLETICS/ACROBATICS bajo los cuadros físicos, y Ilyra B para asegurar disposición distinta.
- Scaffold `38187634513` pendiente de conclusión en el momento del checkpoint. No llamar PASS a la variante A hasta validar CI y PDF regenerado.
