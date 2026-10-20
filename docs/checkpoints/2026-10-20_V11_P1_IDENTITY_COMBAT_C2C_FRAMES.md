# V11 P1 — contornos recortados de identidad y combate (2026-10-20)

**Ruta autorizada:** rama `docs/pc-sheet-v11-approved-implementation-handoff` [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) **DRAFT SIN MERGE**; base remota verificada `c79ea7754ebf9b30edeb8d28eefc0398d65dd518`; `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Scaffold documental BK08 [#39484300723](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39484300723) y QA [#39484300644](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39484300644) SUCCESS.

## Hallazgo físico concreto

Contraste PDF genuino de la App `V11-INTERNAL-PROOF-Mara-B.pdf` **página 1** (artefacto BK08 [#12292766210](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39482624205/artifacts/12292766210)) contra el golden aprobado `V11_Carta/pdfs/v11_extremo_variante_B.pdf` p1 (ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`): P1 ya ofrece cabecera y atributos C2c, pero **cuatro paneles de identidad** (clase/nivel, jugador, raza, alineamiento) y **ocho celdas de combate** siguen con rectángulos simples, no con las esquinas recortadas del papel aprobado. Comprobación previa PyMuPDF: los cuatro rectángulos de identidad están como trazos de un segmento y toda la tabla de combate como una sola figura rectangular `(312,201)..(570,289)`. **SALVACIÓN y cuadrículas C2 ya tienen pruebas/PASS local y no se tocan.**

## Acción funcional delimitada

En los renderers auténticos `AndroidPcSheetV11PdfProofRenderer.kt` y `DesktopPcSheetV11PdfProofRenderer.kt` se sustituyen exclusivamente los trazos de 4 frames de identidad y 8 celdas de combate por **paneles independientes de ocho lados/esquinas recortadas** (C2c). Conservar exactamente las coordenadas originales de cajas, fuentes, origen, valores, marcas, anotaciones manuscritas, reservas de página y orden de dibujo del texto. Sin cambios a `shared`/modelo; no transformar la tabla vigente de dos columnas en el golden de tres sin una migración de diseño justificada. Esto **no equivale a paridad global de P1**.

Se incorpora el guard físico `scripts/audit_v11_p1_clipped_cards.py` en Scaffold: lee el primer folio de PDF **reales Desktop App**, comprueba 4 contornos de identidad y 8 de combate por geometría/segmentos/color exactos, mínimo 48 PDF; crea informe JSON por documento y SHA. Se conserva en el ZIP del CI junto al resto. La prueba de caja no sustituye los tests Kotlin existentes de origen/estado ni valida Android instalado. No se agregan datos ficticios.

**Estado al publicar código:** IMPLEMENTADO, **CI y PDF DEL COMMIT EXACTO PENDIENTES**. Validar CI, comparar el mismo conjunto de PDF antes/después: cero cambios de páginas/textos/coordenadas; todas las páginas posteriores a P1 con dibujos intactos, P1 solamente 12 contornos afectados. Ante error, inspeccionar fuente y PDF exactos antes de cualquier reparación; no gastar intentos RCR-1 especulativos.

**Gates congelados:** G0 PARCIAL; G1 semántico PASS previo; G2/G3 PARCIALES; **G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null**; G5/G6 pendientes, owner Android sin QA/PASS. C211 BLOCKED 3/3 normal, RCR-1 estructural 0/2, no reabierto. #173 DRAFT sin merge.

**Siguiente:** cerrar la comparación física del commit exacto, registrar PASS LOCAL restringido o FAIL con prueba; después abordar otra desviación material independiente en V11 sin volver a zonas verificadas.
