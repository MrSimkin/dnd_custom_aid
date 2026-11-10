# V11 G2/G3 — Cabecera legible para nombres largos, nombre fuente completo en C2

**Fecha:** 2026-10-17. **Autoridad:** PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`, DRAFT / SIN FUSIONAR. `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22` sin cambios. El diseño V11 sigue aprobado únicamente como maqueta, NO el resultado de la App.

## Brecha funcional concreta

La cabecera compartida de todas las páginas imprimía `source.name` a partir de x154 a 12pt sin medir el ancho disponible (~406pt). Un nombre largo podía exceder el borde de la cabecera o cortarse en la impresión. La prueba nueva no es una observación atribuida al propietario: parte del contrato existente (logo y nombre en un mismo renglón; integridad de información; geometría imprimible).

## Trabajo productivo publicado

- `f18190f3e97bf963428da3792d670cdd910855ba`: nuevo `PcSheetV11HeaderNameRouter` Shared, mide con la fuente efectiva. Nombres que caben permanecen textualmente idénticos; cuando no caben, conserva un prefijo medido + `/ VER C2` y enruta el **nombre fuente completo** a `p1:character-full-name` dentro del flujo C2 existente. Sin abreviatura destructiva.
- `dd9f1036cf4044347dc8a55f6f50442049c3dc1b` Desktop, `05c4f6ff56c8e9d6ef09f5c4330ca2e732b97b14` Android: misma lógica, sin mover logo, cuerpos, otras páginas ni las cuatro familias legacy. La navegación física P1↔C2 existente resuelve el número real de página.
- Tests Shared `2494be40ac4dce51225c82470416fed2d2a75c6e` y Desktop PDF real A/B `addd794626780781ec91f3a330be31aa3665c2f2` + corrección de aserción `8e623d8b89e357d4ff8968d3aefd50abfd7992ca`. Fixture: backup auténtico Aldren con **solo el nombre** sustituido por 64 tokens artificiales únicos, NO un nombre ni observación históricos del propietario.

## Verificación del código y PDF exactos

- Primer run `39074182113` **FAIL de prueba nueva**: el test contaba un token en la cabecera repetida más una vez en el cuerpo completo. Diagnóstico confirmado; corrección limitada a examinar solo el cuerpo físico de C2. No fue un segundo defecto de producción.
- [Scaffold run 39074710117](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39074710117) **SUCCESS** sobre commit exacto `8e623d8b89e357d4ff8968d3aefd50abfd7992ca`: test real A/B, suite Desktop/Shared, Android compilado, backend y hosted DB. [QA ledger run 39074710185](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39074710185) **SUCCESS** documental.
- [32 PDFs genuinos Desktop App, artefacto #12116582703](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39074710117/artifacts/12116582703): ZIP SHA-256 `acccd4e42c8c89306decb30a1632851c9aeec4737276cf0084d11a4a3ec54f70`, CRC PASS.
- Comparación independiente contra los 30 PDF del [run #39072946515](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946515): **30/30, 286 páginas** coinciden en Carta/paginación, extracción de palabras y coordenadas redondeadas a .01pt y **cantidad** de trazados vectoriales por página; no se afirma mismo hash de bytes ni equivalencia integral de cada instrucción vectorial.
- PDF nuevo `V11-INTERNAL-PROOF-P1-long-character-name-A.pdf` SHA-256 `d5ed9c2e66c84f3100d60f636b966134f8090b839a2df76bf24114550171bd6e`; variante B SHA-256 `554cb3e4799acc34c58633d767e9f63e930e5cc4a577f3fd7319884fe8d3fb25`. Ambos: **7 páginas Carta**, P1 muestra prefijo + `VER C2`, C2 en página física **4** imprime el nombre fuente completo con sus **64 tokens exactamente una vez cada uno** en el cuerpo (sin contar la cabecera repetida), última página solo NOTAS. Frontera del marcador en cabecera x=554.82pt (dentro de 560pt permitido). P1 y C2 de A/B rasterizados y revisados visualmente; sin desborde visible en estos casos.

## Gates, aceptación y continuidad

**PASS local acotado G2/G3** solo para la integridad de nombre largo y cabecera, **NO aprobación técnica/visual global**. La matriz `docs/pc-sheet-v11/qa/MATRIZ_CONTRATO_V11_PRE_QA.json` conserva **105 reglas globales NOT_VERIFIED** y **12 checks etapa PASS históricos**; no marcar otros checks sin auditoría. **G4 BLOCKED**, G5/G6 pendientes. `active_candidate=null`, sin Android instalado ni QA propietario. Mantener abiertas V11-QA-001/002 y las 32 observaciones históricas M50800, sin sustituirlas por este fixture; RCR-1 sin recuperación estructural. Golden V11, cuatro familias legacy y `main` intactos.

**Siguiente paso técnico:** continuar con otra regla funcional aún no demostrada de integridad/rutas de continuación de la matriz, priorizando casos de contenido real con verificación fuente↔PDF A/B y sin reabrir este bloque sin nuevo defecto; después conciliar todo G4 (D-0076 §§3.1–3.48, ocho golden, familias legadas, todas las 105 reglas y stages, candidato idéntico) antes de G5 y cualquier solicitud QA/merge.
