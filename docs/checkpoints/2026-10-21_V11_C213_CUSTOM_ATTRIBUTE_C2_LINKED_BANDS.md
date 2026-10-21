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
