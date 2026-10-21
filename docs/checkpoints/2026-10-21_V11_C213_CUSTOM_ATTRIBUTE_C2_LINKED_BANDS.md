# C213 — jerarquía física real atributo-habilidades C2

Fecha 2026-10-21. Rama autorizada `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT, sin fusionar. HEAD anterior `ed0ff9cd87fc58004d831fd14a24e5ac7e0bb58a`; `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Diferencia real frente al aprobado

`docs/pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md` (C2, atributo-habilidad) y el PDF golden `v11_extremo_variante_A.pdf` p4 dentro del ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a` muestran cabecera de atributo azul y filas de habilidad alternas. El PDF REAL anterior [`C2-grouped-custom-attributes-A`](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39640521647/artifacts/12365134171) p4 imprime `QAATR001` lineal sin agrupación; su bbox PyMuPDF es (42.00,165.33)-(82.57,176.13) pt y en ese renglón solo aparecen los dos marcos exteriores existentes. La asociación fuente y el salto de columna YA tenían tests adecuados y no deben rehacerse.

## Bloque funcional implementado (C213)

En **ambos** escritores PDF nativos V11, los `PcSheetV11FlowItem` con `groupKey=custom-attribute-group:<id>` gobiernan ahora dibujo en las celdas FÍSICAS YA MEDIDAS: (1) cabecera azul recortada para cada `attribute:`, de altura real si envuelve; (2) franjas alternas azul claro para `attribute-skill:`, por identidad de skill y sin separar su texto multilineal; (3) riel vertical azul de 1.5pt en gutter para mantener visible el grupo en cada columna/página, incluso si su cabecera original quedó atrás. C2c usa paleta del golden. Ninguna entrada de origen, puntaje, skill, nombre, fila, número de página ni PDF legacy se modifica. Prueba de paridad en `scripts/test_v11_c2_source_navigation_contract.py` ampliada.

**Límite:** esto repara la jerarquía de cabecera/habilidades y su continuidad. La geometría individual de puntaje rectangular, modificador ovalado y salvación **todavía no equivale al golden**. No declarar `C204 PASS` ni `G4 PASS`. No supone nueva aceptación técnica o visual del propietario.

**Pruebas aún por hacer en este commit:** CI exacto Scaffold y QA, después 76 PDF auténticos A/B, comprobar código y PDF físico sobre las franjas y ausencia de regresiones frente a `ed0ff9cd` con PyMuPDF. Si falla, registrar defecto y no declarar PASS.

G0 histórico PARCIAL, G1 semántico PASS anterior, G2/G3 parciales, **G4 bloqueado 105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes. C211 BLOCKED tres intentos normales agotados y RCR-1 estructural 0/2; C212 PASS LOCAL físico anterior, sin reabrir. PR #173 DRAFT sin merge.

**Siguiente bloque previsto:** completar tarjetas de puntaje/óvalo de atributos C2 mediante composición medida propia, solo si pruebas fuente→PDF avalan el diseño; conciliar G4 por módulos después de evidencia independiente.

## Evidencia auténtica del producto: **PASS LOCAL C213**, no C204 ni G4

**Commit renderer:** [`fb31db0`](https://github.com/MrSimkin/dnd_custom_aid/commit/fb31db0bdd713ad6a2a0c350c3e8035ad5b30350). **Commit exacto verificado:** [`6428220`](https://github.com/MrSimkin/dnd_custom_aid/commit/64282206243de4e2c3cd58a2a0cb590fe3c42fa1) (sólo reparó la ruta `Read first` tras un CI inicial fallido por ese control documental). [Scaffold #39663757661](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39663757661) **SUCCESS** (Kotlin/Android, guardia de ruta, PDFs App y auditorías), [QA traceability #39663569355](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39663569355) **SUCCESS** en commit producto original.

**Antes:** [76 PDF Desktop App del SHA C212 #12365134171](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39640521647/artifacts/12365134171), ZIP SHA-256 `0c7d882eea93dcc81a90a64deec5fa7e7ffbf5b5a4932e805ae5d513d677d6e7`. **Después:** [76 PDF verdaderos C213 #12376971384](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39663757661/artifacts/12376971384), ZIP SHA-256 `391494dc23323c55706a9f0012bbfeeb62ea89a43596e63e5741cfa1b586e4c8`.

Comparación independiente local con PyMuPDF del **mismo conjunto 76/76 PDF A/B**:
- **711/711 páginas con el mismo texto, todas las mismas palabras y coordenadas, tamaños de página y folios idénticos.**
- **681/711 páginas con los mismos vectores.** Solo **30 páginas C2 de grupos custom** ganaron 713 rutas: **199** cabeceras azules recortadas, **311** fragmentos de filas de habilidades alternas y **203** rieles reales de grupos (sumando exactamente 713).
- **0 rutas antiguas perdidas, 0 rutas inesperadas añadidas, 0 dibujo alterado en otras secciones**.
- El fixture de 72 atributos adicionales ×2 variantes conserva **144/144 rótulos QAATR bajo cabecera azul medida**; otros cuatro contextos de gran atributo continuado permanecen identificados. Se visualizaron `grouped-custom-attributes-A` p4 y `oversized-custom-attribute-A` p5: banda y riel se ven, sin pisar el texto.
- [Acta JSON de evidencia física](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C213_REAL_APP_CUSTOM_GROUP_PANELS.json) con los IDs de runs, artefactos, SHA-256, procedimiento y límites.

**Veredicto**: C213 **PASS LOCAL FÍSICO SOLO PARA AGRUPACIÓN C2**; no afirmar que puntaje rectangular/modificador ovalado/salvación física del golden estén implementados (resto de C204 pendiente). C211 sigue BLOCKED (3/3 normal, 0/2 estructural); C212 PASS LOCAL anterior sin repetición. G0 parcial, G1 PASS semántico anterior, G2/G3 parciales, **G4 BLOCKED: 105/105 NOT_VERIFIED, active_candidate=null**; G5/G6 pendientes; sin Android instalada/owner QA, sin aprobación técnica/visual ni merge.

**Siguiente trabajo propuesto:** puntaje/óvalo de atributos custom físicamente independientes y fuente real en C2, con geometría medida y prueba de conservación en A/B, si el diseño resiste el contraste con los golden. Después conciliación modular G4, sin reabrir C211.
