# V11 G2/G3 — C-N3 reserva manuscrita en espacio realmente libre

**Fecha:** 2026-10-19. **PR exclusiva:** #173 (DRAFT, sin merge); **HEAD base de sesión** `6f235905f635c1397c6c72b220012294dd4a9686`. **Autoridad:** `docs/pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md`, regla `CN304` de `qa/MATRIZ_CONTRATO_V11_PRE_QA.json`; no altera diseño, golden, IDs, historial QA ni cuatro familias legacy.

## Evidencia de salida anterior abierta antes de la corrección

Se descargó **artefacto genuino Desktop #12160714181** del [Scaffold #39193379904](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193379904); ZIP de 64 PDF auténticos / 589 folios, SHA256 `bdc5b3eb366b9f1a88f8874cf67d68b27fc4d853385aec209174ec7a81c7e24a` y CRC testzip correcto. Se inspeccionó físicamente `V11-INTERNAL-PROOF-CN3-separated-Mara-A.pdf` página **8**: 10 límites de notas originales identificables, contenido termina sobre y≈573.57 desde arriba, la región hasta el pie queda vacía y **sin líneas manuscritas secundarias**. También se abrió `V11-INTERNAL-PROOF-CN3-long-note-continuation-A.pdf` páginas 8–10: las primeras dos llegan casi al pie del área impresa; la página 10 deja espacio libre considerable. Esto demuestra el requisito específico que faltaba, no un defecto nuevo del legado M50800.

## Cambio funcional publicado

- Código Kotlin productivo **Desktop** `4dcfdad8f2cd3068880e095376f812decb6f29c1` y **Android** `419c2b04f2104a0c48a5c047329a1c6431d82a79`: solo en páginas C-N3 con líneas digitales reales, dibujar líneas manuscritas secundarias suaves de 16pt **por debajo de la celda fuente más baja**, con 24pt de separación mínima; suelo 92pt por encima del pie. Solo con al menos cuatro líneas completas; no crear nuevos folios, texto, notas falsas, captions, ni desplazar ninguna celda. Los bloques nuevos se cotejaron idénticos en ambos renderizadores.
- **Prueba Desktop PDF genuino escrita** en `DesktopPcSheetV11RuntimePdfProofTest.kt` (`g3ShortDigitalNoteReservesHandwritingOnlyBelowMeasuredSourceAB`): variantes A/B desde respaldo Aldren, compara presencia/ausencia de nota para exigir exactamente una página digital adicional, fuente única, última NOTAS intacta, y operadores **vectoriales PDF** reales del primer renglón. El test preserva PDF A/B para revisión independiente en artefactos del pipeline cuando se ejecute.
- La reserva manuscrita es accesoria: **no sustituye** C-N3 digital ni la última página exclusivamente manuscrita, y nunca invade C2.

## Validación realmente realizada (NO es G3/G4 PASS)

1. Inspección raster del **PDF antiguo original** y superposición **simulada fuera de la App** para la página Mara C-N3 p8: unas siete líneas de papel quedan en el espacio vacío, sin solapamiento visible. Esta ilustración **NO es un PDF generado con los cambios de Kotlin**.
2. Recorrido independiente de los **64 PDF previos**, 68 páginas C-N3, estimando el espacio por cajas de palabras PyMuPDF: **1.749 líneas elegibles simuladas** y **0 advertencias geométricas** con la separación prevista. La medición externa de palabras no es idéntica al `cellPt` Kotlin; sirve solo como control previo.
3. Kotlin aislado con mismas expresiones geométricas + dobles mínimos de stream, compilado y ejecutado localmente: **PASS** para ausencia de notas/no C-N3 y 627 alturas de borde (76–702pt), todas las líneas dentro de la zona segura. No equivale a compilación de Android/Desktop ni ejecución de PDFBox real.
4. Inspección de igualdad del nuevo bloque de producción Desktop/Android: **PASS** (uno en cada archivo).
5. **Pendiente:** CI del HEAD final, test de integración verdadero A/B, PDF genuino producido después de los cambios y revisión independiente de sus vectores/raster/texto. **NO declarar PASS LOCAL del cambio**, ni actualizar ninguna regla G4 a PASS, antes de esa evidencia.

## Gates / continuación

**G0** QA Mara histórica PARCIAL; **G1** semántico PASS; **G2/G3** IN_PROGRESS y este bloque **no cerrado por runtime nuevo**; **G4 BLOCKED** 105/105 NOT_VERIFIED, `active_candidate=null`, conciliación golden/48 decisiones todavía pendiente; **G5/G6** pendientes. M50800 y V11-QA intactos, RCR-1 0/2; PR #173 DRAFT, sin aprobación visual/técnica owner ni merge.

**Siguiente trabajo seguro exacto:** verificar Scaffold + QA del último SHA y abrir los PDF A/B `V11-INTERNAL-PROOF-CN3-manuscript-*.pdf` del artefacto nuevo. Comprobar operadores, tinta/renglones, paginación y preservación del conjunto anterior, reparar **solo error observado**. Luego avanzar la conciliación física G4 por módulo; no repetir más helpers administrativos ni alterar G0/G1 ya acreditados.
