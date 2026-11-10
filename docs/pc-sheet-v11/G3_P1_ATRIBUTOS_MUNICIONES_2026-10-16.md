# V11 G3 — P1 ATRIBUTOS A/B y MUNICIONES sin datos: verificación de dos correcciones

**Fecha:** 2026-10-16. **Autoridad de ruta:** main/RESUME → main/LATEST → checkpoint canónico 2026-10-09 → única rama `docs/pc-sheet-v11-approved-implementation-handoff` PR #173. **G3 todavía parcial**, G4/G5/G6 no aprobados. Golden original SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a` inspeccionado, sin cambiarlo.

## Cierre previo: cartela HABILIDADES B

En `G3_P1_HABILIDADES_B_CARTELA_2026-10-16.md` consta el PASS **solo de esa cartela** y de sus PDFs Desktop: Scaffold #38971731677 SUCCESS, artefacto 12076807568. No repetir; no equiparar a aceptación owner.

## P1 ATRIBUTOS A/B — diferencia física original y corrección real

**Antes:** el golden P1 tiene un encabezado C2c azul niebla sobre la lista de Atributos; el genuino P1 Desktop solo decía `ATRIBUTOS · A/B` sin la cartela. La franja física libre medida queda entre el retrato (termina PDF y605) y la primera fila fuente real (inicia PDF y577). Se decidió implementar la estética ya aprobada en esa franja, sin rediseñar ni mover atributos.

**Código:** commits `6bf13cf9736f86c719a53236f822aeb975e3d01a` Desktop + `c6e68e50ee3b13683a4e2efbb96818ed654e8c14` Android; título x51/y586 y polígono físico x42..300/y579..602 (top-origin y190..213), RGB relleno 231/242/247, borde 38/75/104; fuentes originales y orden de campos intactos.

**Pruebas:** `g3RealP1BothVariantsHaveGoldenAttributesClippedCartelaWithoutLosingScores` sobre Aldren A, Ilyra B, Mara A/B, más guard de paridad `scripts/test_v11_p1_attributes_cartela_contract.py` ejecutado en CI QA. **Scaffold exacto #38972683319 SUCCESS** Kotlin/Desktop PDFBox, Android DEBUG y servicios; artefacto Desktop real **12078065397**, digest ZIP `sha256:80a1af6276d862b05236e8858c395b2702a8ee4e6791d4728d0e8427b8afc36d`, ZIP CRC PASS.

**Oráculo independiente PyMuPDF** vs PDF anterior `12076807568`: Aldren A 7→7, Ilyra B 13→13, Mara A 9→9, Mara B 14→14, **solo P1 cambia**, +1 vector nuevo exacto `Rect(42,190,300,213)` en cada PDF; únicas tres palabras desplazadas son `ATRIBUTOS`, `·`, `A/B` (+8pt x/+2pt top-origin). **Todas las otras palabras/posiciones/páginas/composiciones posteriores iguales**. Región Ilyra B rasterizada sin contacto con retrato o filas de atributos. SHA PDF Ilyra B `e40224b318022fdb82e360892a67e21e2f14a0f1525e836bf6b001788747fdef`. **PASS únicamente de este ajuste gráfico G3 Desktop**, no título/estructura V11 entera.

## P1 MUNICIONES vacío — requisito funcional aprobado §3.39 D-0076 / P118

**Antes:** el genuino PDF Ilyra B sin tipo de munición registrable mostraba panel y líneas vacías pero **cero casillas manuales**. Golden original `v11_personaje_casi_vacio.pdf` sí muestra casillas. Requisito del propietario: **una entrada lógica MANUAL** para tipo/nombre y hasta veinte casillas marcables en grupos de cinco, sin inventar cantidad o munición. Se inspeccionó la salida real anterior antes de cambiar código.

**Código+prueba en un ÚNICO commit atómico** `005cb3b7b8220f443f9a707c0fe8ba2bdbb3a5cc` (diff verificado limitado a dos renderizadores PDFBox V11, un test Desktop genuino, guard Python de paridad y workflow QA; main/legacy/golden intactos): si `p1AmmoItems.isEmpty()`, imprimir solo línea manual `____________` y 20 cuadros 6×6pt dentro del panel existente: 10 en subfila PDF y119, 10 en y103, grupos de cinco con separación adicional. El modo con municiones reales NO cambia. No crear ni persistir registros ficticios ni imprimir `/20` como cantidad.

**Pruebas:** `g3RealP1WithoutAmmunitionHasOnePaperEntryWithTwentyEmptyBoxes` en PDF genuino Ilyra B (20 operadores rectángulo reales, contenedor original fijo, sin `MARCAS: ____ / 20` falso); fuente Android/Desktop cotejada por `scripts/test_v11_ammunition_manual_contract.py` en CI QA. **Scaffold exacto #38973067753 SUCCESS** Kotlin/Desktop y Android DEBUG, backend, DB; QA traceability #38973067689 SUCCESS; ZIP Desktop real `v11-internal-real-app-pdf-proofs` ID **12078165513**, digest `sha256:2ab8a57b088cda8529a8e84f6c5118ada41249393c94ebb5d03c1491344fe91e`, CRC PASS.

**Inspección física independiente** contra salida anterior `12078065397`:
- **Aldren A 7→7 páginas**, con municiones reales: PDF byte/texto/vectores/paginación **idénticos**, SHA PDF `0f343575aa47c899c22c5daf7a6a28c0f2046c2c70a59d2db4163ce27de90b91`.
- **Ilyra B 13→13**, **Mara A 9→9**, **Mara B 14→14**: únicamente cambia stream de P1; +20 operadores geométricos de cuadros por PDF y una palabra de línea en blanco. **Todas las palabras existentes, ID, geometría, páginas posteriores y última hoja NOTAS iguales**. Los cuadros se vieron enteros y separados mediante imagen raster real.
- SHAs PDF nuevos Ilyra B `36a8970560807b4dccd31adf6e03289bd04e0390bcce6aa51b7a57dbf308a20c`, Mara A `c77f06299b7069ac88eb2eeeac9d789c20192d3249a8846b534ab9225012da03`, Mara B `6f921543fe9ee62419a971644fc1cecf5944808f4d062f0639581bf9a637c87f`.

**PASS limitado al incumplimiento funcional "sin Municiones quedan casillas manuscritas" en PDF real Desktop.** Las 105 reglas/48 secciones D-0076/ocho golden/auditoría cuatro familias/Android instalado requieren prueba completa G4 antes de QA propietario. La matriz mantiene `P118.status=NOT_VERIFIED` y su `stage_checks.G3.status=NOT_VERIFIED` hasta verificación de todas las superficies/candidato; no inventar aceptación. El registro V11-QA-001/002 y 32 M50800 permanece intacto, RCR-1 0/2, PR #173 DRAFT sin merge.

## Próxima acción automática autorizada

Seguir G3 conforme al plan original y escoger **el siguiente incumplimiento medido en el golden y en PDF genuino**, priorizando pérdida/campos/espacios útiles frente a decoración. No volver a reparar ni revalidar la misma rebanada verificada por mera reanudación. Cada corrección exige código Kotlin productivo, caso rojo/verde, comprobación de fuente↔PDF, paridad Android/Desktop, y preservación de familias antiguas; G4 global sigue bloqueado. No pedir otra validación estética al propietario.
