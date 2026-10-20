# V11 BK07 — fichas físicas del Libro de Conjuros con gramática golden

**2026-10-20 · autoridad:** PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`. Base remota `d330eab057f9735c5d141e4b705b711387ca4199` (Scaffold #39446662101 SUCCESS, QA traceability #39446662177 SUCCESS). `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Fuente primaria y diferencia visible

Se reabrieron **PDF reales Desktop C210** del artefacto [#12234489577](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39355668980/artifacts/12234489577), en particular **Mara B página física 10 (Libro de conjuros)**, y el **golden original** `V11_Carta/pdfs/v11_extremo_variante_B.pdf` **página 9** del ZIP aprobado SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`. Golden: fichas individuales de contorno azul con esquinas recortadas y bandas azul-niebla para nombre/nivel. App C210: texto fuente completo pero fichas **sin contorno ni bandas** (solo cuatro dibujos globales en la página); incumplimiento acotado BK07, separado del C211 bloqueado. No se usa un diseño nuevo ni se toma como aprobación del propietario al candidato.

## Cambio funcional producido

Renderers reales **Android y Desktop V11**: cada fragmento impreso del Libro de Conjuros recibe marco vectorial de ocho lados/esquinas recortadas, azul del golden y panel superior azul-niebla únicamente tras las líneas de **nombre + nivel fuente**, sin inventar escuela, propiedades de conjuro o estado. Fragmentos que continúan otra columna/página conservan su propia protección visual, sin falsificar un nuevo título. No se cambia el algoritmo G2, fuente, posiciones de texto, cantidad de páginas, contenido, índice físico, los otros módulos ni las cuatro familias legacy. Sin tocar `PcSheetV11PairedAmountRouting`: C211 queda BLOCKED 3/3 sin recuperación estructural.

Se incorpora `scripts/audit_v11_spellbook_cards.py` a Scaffold para cotejo **independiente** de formas visibles en PDF reales, detectando libros sin contornos o sin bandas. El test de regresión Kotlin existente BK04/BK05/BK06 sigue comprobando mover tarjetas enteras, trocear las demasiado largas, no inventar campos, y datos fuente completos; Scaffold hace Android build y PDF Desktop.

**Estado al publicar:** IMPLEMENTADO / **CI EXACTO PENDIENTE**. Solo promover a PASS LOCAL BK07 cuando Scaffold y evidencia física de PDF del nuevo commit demuestren 0 errores; reusar golden como comparación informada. Si falla, inspeccionar las páginas exactas e intervenir exclusivamente este bloque dentro RCR-1; no desactivar el inspector.

**Gates sin cambiar:** G0 histórico PARCIAL; G1 semántico PASS previo; G2/G3 PARCIALES; **G4 BLOCKED, 105 reglas NOT_VERIFIED, active_candidate=null**; G5/G6 pendientes; Android instalado/QA owner sin PASS. RCR-1 C211 3/3 normales, 0/2 estructurales. PR DRAFT SIN MERGE.

**Continuidad:** (1) verificar CI exacto y artefactos PDF nuevos, (2) medir conservación de folios/texto/caracteres respecto C210 y contornos/bandas de las tarjetas versus golden, (3) registrar FAIL o PASS LOCAL acotado, (4) seguir módulos visuales aprobados aún carentes de evidencia, sin reabrir C211.

## Resultado exacto — BK03/BK07 PASS LOCAL (NO G4)

[Scaffold #39454778210](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39454778210) **SUCCESS** (Kotlin, Android debug build, Desktop tests, auditores físicos, backend, DB); [QA traceability #39454778169](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39454778169) **SUCCESS** en commit productivo `72dfab01749df4871ae9c018e33209c56afb9503`. [76 PDF auténticos App Desktop, artifact #12281106205](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39454778210/artifacts/12281106205), ZIP SHA256 `a3d00991a2327a18a68962bf8fa478a97be27e0a20bb36322dc01e4526f8bbe7`; [auditores #12281570452](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39454778210/artifacts/12281570452), ZIP SHA `19f42410fa803e740c312d272a60b149381452fe8de8c90095761f0be9d9f63b`.

Cotejo independiente fuente C210 → candidato actual: **76/76 PDF mismos nombres y número de páginas; 709/709 páginas con exactamente las mismas palabras y coordenadas; 678/678 páginas ajenas al Libro con vectores físicos idénticos**. Las **31 páginas de Libro, en 12 PDF**, pasan de **0 marcos/0 bandas** a **267 paneles vectoriales recortados y 504 rectángulos de banda niebla** sin pérdida de ninguna palabra ni página. El auditor del mismo candidato reporta **12/12 libros sin fallo**, PyMuPDF comprueba color/anchura/esquinas; página Mara B p10 nueva visualmente inspeccionada contra golden extremo B p9. [Acta JSON de hashes/alcances](../pc-sheet-v11/qa/evidence/2026-10-20_BK07_LOCAL_PHYSICAL_CARD_COMPARISON.json).

**Alcance exacto:** **BK07 es nombre interno del paquete funcional que avanza la regla existente BK03**, no una regla nueva de G4 ni una declaración de que BK03 global ya esté PASS. El marco/cabecera se acercaron a la gramática del golden sin cambiar datos ni distribución, pero la **separación entre fichas y jerarquía tipográfica final** NO se proclaman idénticas al golden. No cambiar ahora sin nuevo incumplimiento acotado y prueba real; conservar las 105 reglas `NOT_VERIFIED`, `active_candidate=null`, G4 BLOCKED, G5/G6 pendientes y owner Android sin QA. C211 no tocado, RCR-1 3/3 normal / 0/2 estructural. #173 DRAFT SIN MERGE.

**Siguiente acción técnica útil:** seguir conciliando otras partes V11 con los ocho golden y los contratos vigentes, elegir desvío funcional demostrado fuera de C211, resolver sin rediseño ni modificaciones a familias legacy. La inspección Android instalado y G4 100 % se mantienen como gates posteriores, no como aprobaciones inferidas por CI.
