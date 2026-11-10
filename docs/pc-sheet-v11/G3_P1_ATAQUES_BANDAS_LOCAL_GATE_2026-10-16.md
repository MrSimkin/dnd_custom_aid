# V11 — G3 P1 Ataques: bandas alternas, reparación de prueba y control local

**Fecha:** 2026-10-16. **PR:** #173, DRAFT/unmerged. **Estado:** cambio en renderizadores publicado; QA owner NO autorizado; G4 BLOQUEADO; la validación de la última revisión de coordenadas/CI estaba EN CURSO al redactar este documento.

## Autoridad y alcance estrecho

El contrato visual V11 aprobado y el golden real fijan la gramática azul niebla y los encabezados/filas de la tabla de Ataques. En este paso **solo se agregan cuatro bandas alternas muy claras** en las siete filas de datos de P1. No se cambia el texto, no se cambia el número/tamaño de columnas ni las alturas, no se mueve el módulo de Rasgos ni Lanzamiento, no se reabre la aprobación visual.

La nueva regla individual **P120** en `qa/MATRIZ_CONTRATO_V11_PRE_QA.json` exige prueba local G2/G3 y posteriormente evidencia exhaustiva G4. Matriz inicial total: **105 reglas**; todas sujetas al gate fail-closed y a conciliación de las 48 secciones D-0076 y ocho PDFs golden. No declarar PASS por un test aislado.

## Evidencia de implementación y fallo reproducible

- Ambos renderizadores genuinos PDFBox V11: `desktopApp/.../DesktopPcSheetV11PdfProofRenderer.kt` y `androidApp/.../AndroidPcSheetV11PdfProofRenderer.kt`.
- Primer intento funcional: cuatro bandas (RGB 244/250/253), márgenes flotantes PDF x312.8 y ancho256.4, y=467/443/419/395, altura12pt; CI compiló Android/Desktop en **Scaffold #38967244510 PASS**, artefacto auténtico Desktop `v11-internal-real-app-pdf-proofs` ID **12075018632**.
- Test PDF real nuevo `g3RealP1AttacksHaveSubtleAlternatingBandsWithoutLosingPrintedAttackFields` falló en **Scaffold #38967377528**: esperaba `312.8 467 256.4 12 re` literal y contó cero. Extracción independiente del contenido del PDF de `12075018632` con PyMuPDF muestra `312.79999 467 256.39999 12 re` (iguales formas físicamente). **Causa: representación decimal de float PDFBox, no ausencia de bandas.**
- Corrección posterior con números enteros fuente-neutral (Desktop **11c42c4770c245b6b80b4053c3319b172985ecc0**, Android **db2adc38d08352af6d26512a25e55fab5c0b11c4**): banda en PDF x313, ancho256, márgenes internos1pt; mismas y/alto. Prueba actualizada commit **943db0f59118155dc31d56b23bdf9084aad92449**. **No equiparar este cambio a PASS hasta confirmar ejecución y PDF físico nuevo.**
- Control extra de paridad de source bajo `scripts/test_v11_attacks_render_contract.py` y workflow QA ledger, commits **747bf3dd8072ebf5918e5e437ece1d324fe8cc5d** + **46d0e3377936f9e77abba37b4d643498303ccac3**. Nuevo ID G4 **P120** declarado `NOT_VERIFIED` (commits **021baea8a729976e45c6ba1aa9674c421d0e81f4**, **b9d03e7446b1e2d9e9b3ac5a5534521f061ae781**).
- **Comparación independiente** del PDF genuino previo a franjas (Scaffold #38967145354, artefacto **12074928634**) versus primer PDF genuino CON franjas (Scaffold #38967244510, artefacto **12075018632**) con PyMuPDF: Aldren A **7→7**, Ilyra B **13→13**, Mara A **9→9**, Mara B **14→14**, todas páginas Carta. **Todas las palabras fuente y sus coordenadas son idénticas en cada página**, solo cambia el content stream de la página física 1; cada P1 agrega exactamente **cuatro** rectángulos de relleno claro a la tabla de Ataques (Aldren 121→125 figuras, Ilyra 129→133, Mara A 116→120, Mara B 145→149). Posteriores páginas y última hoja manuscrita no cambiaron. **Prueba limitada a la versión flotante que ya pasó Kotlin; la versión entera aún requiere idéntica auditoría.**

## Próximo paso obligatorio

1. Revisar resultado de **Scaffold #38969020545** o posterior para código entero + test y **QA traceability #38969020572** (este último PASS). No tratar RUNNING como PASS.
2. Obtener nuevo ZIP `v11-internal-real-app-pdf-proofs` del SHA final compilado, comparar independencia fuente/posición/páginas contra artifact previo y confirmar **cuatro rectángulos nuevos exactos x313 / ancho256** en P1.
3. Si test falla, usar operadores auténticos de ese PDF para corregir **el test o la causa real**; evitar ajustar geometría a ojo. Publicar CI verde solo tras confirmación.
4. Continuar las faltas medibles de contrato en módulos, actualizar comprobaciones locales correspondientes. **P120 global NOT_VERIFIED** y **G4 BLOCKED** hasta candidato Android instalado, auditoría completa, legacy, golden y revisión externa.
5. Mantener original `qa/REGISTRO_QA_V11.json` con V11-QA-001/002 OPEN, M50800 histórico intacto y RCR-1 sin reinicio; nada en `main` o exportadores legacy.
